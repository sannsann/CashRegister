package com.stormcloud.cashregister;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;

import android.app.ActionBar;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.starmicronics.stario.StarIOPort;
import com.starmicronics.stario.StarIOPortException;
import com.stormcloud.cashregister.CategoryListFragment.OnCategorySelectedListener;
import com.stormcloud.cashregister.ItemGridFragment.OnGridItemSelectedListener;
import com.stormcloud.cashregister.KeypadFragment.OnItemsClearedListener;
import com.stormcloud.cashregister.KeypadFragment.OnTenderedAmountSetListener;

public class POSActivity extends FragmentActivity implements
        OnCategorySelectedListener, OnGridItemSelectedListener,
        OnTenderedAmountSetListener, OnItemsClearedListener, ReceiptFragment.OnSaleLoggedListener {

    private final static String TAG = "com.stormcloud.cashregister.pos_activity";

    OnCategorySelectedListener mCatListener;
    OnGridItemSelectedListener mItemListener;
    OnTenderedAmountSetListener mTenderedListener;
    OnItemsClearedListener mClearedListener;
    ReceiptFragment.OnSaleLoggedListener mSaleLoggedListener;

    public static final String EXTRA_ITEM_CATEGORY = "com.stormcloud.cashregister.item_category";
    public static final String EXTRA_RECEIPT_ITEM = "com.stormcloud.cashregister.receipt_item";
    public static final String EXTRA_MANAGER_REQUEST = "com.stormcloud.cashregister.manager_request";

    private String portName;
    private String portSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_triple_fragment);

        getActionBar().setTitle("RingUp" + ": Sales Mode");
        getActionBar().setDisplayHomeAsUpEnabled(true);

        loadItemsToInventory();

        FragmentManager fm = getSupportFragmentManager();

        Fragment fragmentReceipt = new ReceiptFragment();
        fm.beginTransaction()
                .add(R.id.fragment_1_3, fragmentReceipt).commit();

        Fragment fragmentItemCat = new CategoryListFragment();
        fm.beginTransaction()
                .add(R.id.fragment_2_3, fragmentItemCat).commit();

        Fragment fragmentKeyPad = new KeypadFragment();
        fm.beginTransaction()
                .add(R.id.fragment_3_3, fragmentKeyPad).commit();


        // Begin printer related configurations
        StarIOPort port = null;
        try {
            port = StarIOPort.getPort(portName, portSettings, 5000);
        } catch (StarIOPortException e) {
            //There was an error opening the port
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Reset to prevent repopulating the ItemInventory ArrayList with duplicate items
        ItemInventory.get(this).reset();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pos_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        // Handle presses on the action bar items
        switch (menuItem.getItemId()) {
            case R.id.action_item_inventory:
                openInventory();
                return true;
            case R.id.action_sales_report:
                openReports();
                return true;
            case R.id.action_settings:
                openSettings();
                return true;
            case R.id.action_print_receipt:
                printCurrentReceipt();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    @Override
    public void onCategorySelected(String category) {

        Bundle data = new Bundle();
        data.putString(EXTRA_ITEM_CATEGORY, category);

        FragmentManager fm = getSupportFragmentManager();
        KeypadFragment keypadFragment;
        String strDisplayValue = "";

        try {
            keypadFragment =
                    (KeypadFragment) fm.findFragmentById(R.id.fragment_3_3);

            strDisplayValue = keypadFragment.getDisplayValue();

            if (strDisplayValue.length() == 0 || Double.parseDouble(strDisplayValue) < 0) {

                Fragment fragmentInventory = new ItemGridFragment();
                fragmentInventory.setArguments(data);
                fm.beginTransaction()
                        .replace(R.id.fragment_3_3, fragmentInventory)
                        .commit();

            } else {

                Item temp = new Item();

                temp.setName(category);
                temp.setCategory(category);
                temp.setQty(1);

                if (category.contains("Payout")) {
                    strDisplayValue = "-" + strDisplayValue;
                }
                temp.setPrice(Double.parseDouble(strDisplayValue));

                CustomerReceipt.get(this).addReceiptItem(temp);

                Fragment fragmentReceipt = new ReceiptFragment();

                fm.beginTransaction()
                        .replace(R.id.fragment_1_3, fragmentReceipt).commit();

                keypadFragment.clearDisplayValue();
            }
        } catch (ClassCastException ex) {

            Fragment fragmentInventory = new ItemGridFragment();

            data.putString(EXTRA_ITEM_CATEGORY, category);

            fragmentInventory.setArguments(data);

            fm.beginTransaction()
                    .replace(R.id.fragment_3_3, fragmentInventory)
                    .commit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void onGridItemSelected(Item item) {
        // Add selected item to the customer's receipt
        if (CustomerReceipt.get(this).getReceiptItems().contains(item)) {
            int itemPosition = CustomerReceipt.get(this).getReceiptItems()
                    .indexOf(item);

            int updatedQty = CustomerReceipt.get(this).getItem(itemPosition)
                    .getQty() + 1;

            CustomerReceipt.get(this).getItem(itemPosition).setQty(updatedQty);

        } else {

            item.setQty(1); // Set the first item's qty to 1 then add to list.
            CustomerReceipt.get(this).addReceiptItem(item);

        }

        // Can maybe substitute below with just a setListAdapter rather than a new transaction each
        // time an item is added.
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragmentReceipt = new ReceiptFragment();
        fm.beginTransaction()
                .replace(R.id.fragment_1_3, fragmentReceipt).commit();

    }

    @Override
    public void onTenderedSet() {

        /**
         * These controls should be moved to the receipt fragment as that is the
         * parent.
         */

        FragmentManager fm = getSupportFragmentManager();
        ReceiptFragment receiptFragment = (ReceiptFragment) fm
                .findFragmentById(R.id.fragment_1_3);

        DecimalFormat myFormatter = new DecimalFormat("#,##0.00");
        String output = myFormatter.format(CustomerReceipt.get(this)
                .getTendered());
        receiptFragment.setTendered(output);

        TextView tvChange = (TextView) this
                .findViewById(R.id.receipt_change_total);

        output = myFormatter.format(CustomerReceipt.get(this).getChange());
        tvChange.setText(output);

        Button btnCompleteTransaction = (Button) this
                .findViewById(R.id.receipt_complete_transaction);
        btnCompleteTransaction.setVisibility(View.VISIBLE);
    }

    @Override
    public void onItemsCleared() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragmentReceipt = new ReceiptFragment();
        fm.beginTransaction()
                .replace(R.id.fragment_1_3, fragmentReceipt).commit();
    }

    public void openInventory() {
        Intent intent = new Intent(this, ManagerActivity.class);
        intent.putExtra(EXTRA_MANAGER_REQUEST, "inventory");
        startActivity(intent);
    }

    public void openSettings() {
        Intent intent = new Intent(this, ManagerActivity.class);
        intent.putExtra(EXTRA_MANAGER_REQUEST, "settings");
        startActivity(intent);
    }

    public void openReports() {
        Intent intent = new Intent(this, ManagerActivity.class);
        intent.putExtra(EXTRA_MANAGER_REQUEST, "reports");
        startActivity(intent);
    }


    public void loadItemsToInventory() {

        try {
            // Check to see if items.json exists. First time loading CashRegister, the file should
            // not be there:

            // If this is the first time CashRegister is run, load Inventory from stock asset
            // Else, load from items.json

            File file = new File(getFilesDir(), "items");

            if (file.exists()) {

                ShopItemJSONSerializer jsonSerializer = new ShopItemJSONSerializer(this, "items");

                ArrayList<Item> list = jsonSerializer.loadItems();

                for (Item i : list) {
                    ItemInventory.get(this).addItem(i);

                }

            } else {
                // File is not present, loading from the hardcoded CSV.
                // Retrieve assets from the AssetManager
                AssetManager assetManager = this.getAssets();

                // prepare the file for reading
                InputStreamReader inputStreamReader = new InputStreamReader(
                        assetManager.open("CashRegisterInventory.csv"));

                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line; // Variable to hold a line

                // Read the lines until end of file
                while ((line = reader.readLine()) != null) {

                    // Used *** to denote comment lines in the input csv file
                    if (line.contains("***")) {
                        continue;
                    } else {
                        // Create the item according to details in the csv file
                        String[] itemDetails = line.split(",");
                        Item item = new Item();
                        for (int i = 0; i < itemDetails.length; i++) {
                            item.setName(itemDetails[0]);
                            item.setCategory(itemDetails[1]);
                            item.setPrice(Double.parseDouble(itemDetails[2]));
                            item.setTaxable(Boolean.parseBoolean(itemDetails[3]));

                            // This one will have to be dealt with to work properly
                            // alongside inventory assistance feature.

                            // Same with icon. This will have to do for now.
                            item.setQty(Integer.parseInt(itemDetails[4]));
                            item.setIconCode(Integer.parseInt(itemDetails[5]));

                        }
                        ItemInventory.get(this).addItem(item);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void onSaleLogged(){

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragmentKeyPad = new KeypadFragment();
        fm.beginTransaction()
                .replace(R.id.fragment_3_3, fragmentKeyPad).commit();

    }

    public void printCurrentReceipt() {
        CustomerReceipt.get(this).PrintPaperReceipt(this, "BT:Star Micronics", "");
    }

}
