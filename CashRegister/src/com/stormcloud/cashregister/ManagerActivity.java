package com.stormcloud.cashregister;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.Toast;

import com.stormcloud.cashregister.ItemInventoryListFragment.OnItemSelectedListener;
import com.stormcloud.cashregister.ItemFragment.OnItemFinishedListener;
import com.stormcloud.cashregister.ItemIconPickerFragment.OnIconSetListener;

import com.stormcloud.cashregister.ReceiptFragment.OnSaleLoggedListener;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Hosts Fragments that were designed for non-sale functions and settings.
 *
 * @author schhan
 */
public class ManagerActivity extends FragmentActivity implements
        OnItemSelectedListener, OnItemFinishedListener, OnIconSetListener,
        ReceiptFragment.OnSaleLoggedListener {

    public static final String TAG = "ManagerActivity";

    public static final String EXTRA_MANAGER_REQUEST = "com.stormcloud.cashregister.manager_request";
    public static final String EXTRA_ITEM_ID = "com.stormcloud.cashregister.item_id";
    public static final String EXTRA_ICON_NUMBER = "com.stormcloud.cashregister.icon_number";
    public static final String EXTRA_REPORT_DATE = "com.stormcloud.cashregister.report_date";

    private static final String DIALOG_DATE = "date";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_double_fragment);

        getActionBar().setTitle("RingUP" + ": Manager Mode");
        getActionBar().setDisplayHomeAsUpEnabled(true);

        FragmentManager fm = getSupportFragmentManager();

        String managerRequest = getIntent().getStringExtra(
                EXTRA_MANAGER_REQUEST);

        if (managerRequest.equals("inventory")) {

            Fragment fragmentEmptyItem = fm.findFragmentById(R.id.fragment_2_2);
            Fragment fragmentItemList = fm.findFragmentById(R.id.fragment_1_2);

            if (fragmentItemList == null) {
                fragmentItemList = new ItemInventoryListFragment();
                fm.beginTransaction()
                        .add(R.id.fragment_1_2, fragmentItemList).commit();
            }

            // Have an empty right fragment showing instruction:
            // "Click on item on left to edit"
            if (fragmentEmptyItem == null) {
                fragmentEmptyItem = new ItemEmptyFragment();
                fm.beginTransaction()
                        .add(R.id.fragment_2_2, fragmentEmptyItem).commit();
            }

        } else if (managerRequest.equals("settings")) {

            Fragment fragmentSettings = fm.findFragmentById(R.id.fragment_1_2);
            if (fragmentSettings == null) {
                fragmentSettings = new SettingsFragment();
                fm.beginTransaction()
                        .add(R.id.fragment_1_2, fragmentSettings).commit();
            }
        } else if (managerRequest.equals("reports")) {

            Fragment fragmentReports = fm.findFragmentById(R.id.fragment_1_2);
            if (fragmentReports == null) {
                fragmentReports = new ReceiptDayReportFragment();
                fm.beginTransaction().
                        add(R.id.fragment_1_2, fragmentReports).commit();
            }

        } //else if (managerRequest.equals("icon")) {
//
////            ItemIconPickerFragment fragmentIconPicker = new ItemIconPickerFragment();
//            Fragment fragmentIconPicker = new ItemIconPickerFragment();
//            fm.beginTransaction().
//                    replace(R.id.fragmentContainerRight, fragmentIconPicker).commit();
//
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Instead of having the entire menu available, each fragment is able to inflate its own.

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        // Handle presses on the action bar items
        FragmentManager fm = getSupportFragmentManager();

        switch (menuItem.getItemId()) {
            case R.id.action_new_item:
                newItem();
                return true;
//            case R.id.action_settings:
//                openSettings();
//                return true;
            case R.id.action_pick_date:

                // Create calender view date picker dialog that sets the date and sends
                // to ReceiptDayReportFragment

                Calendar cal = Calendar.getInstance();
                Date date = new Date();
                cal.setTime(date);

                DatePickerDialog datePicker =
                        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                                // monthOfYear has January as a value of 0, not 1
                                // Therefore we must add 1 to the value
                                String formattedStringDate =
                                        String.format("%s-%s-%s", monthOfYear + 1, dayOfMonth, year % 100);

                                Log.e(TAG, formattedStringDate);
                                Bundle bundle = new Bundle();

                                bundle.putString(EXTRA_REPORT_DATE, formattedStringDate);
                                // set Fragment class Arguments
                                ReceiptDayReportFragment dayReportFragment = new ReceiptDayReportFragment();
                                dayReportFragment.setArguments(bundle);

                                FragmentManager fm = getSupportFragmentManager();

                                fm.beginTransaction()
                                        .replace(R.id.fragment_1_2, dayReportFragment)
                                        .commit();

                            }
                        },
                                cal.get(Calendar.YEAR),
                                cal.get(Calendar.MONTH),
                                cal.get(Calendar.DAY_OF_MONTH));

                datePicker.show();
                return true;

            case R.id.action_print_report:
//                ReceiptDayReportFragment temp =
//                        (ReceiptDayReportFragment) fm.findFragmentById(R.id.fragment_1_2);
//
//                temp.printDayReport();

                // Fragment should not be responsible for printing, it is CustomerReceipt's duty
                return true;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    @Override
    public void onIconSet(int iconCode) {

        Bundle data = new Bundle();
        data.putInt(EXTRA_ICON_NUMBER, iconCode);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragmentItem = new ItemFragment();

        fragmentItem.setArguments(data);

        fm.beginTransaction()
                .replace(R.id.fragment_2_2, fragmentItem)
                .commit();
    }

    @Override
    public void onItemSelected(Item item) {

        // Fills in an ItemFragment with the details of the selected item
        Bundle data = new Bundle();
        data.putString(EXTRA_ITEM_ID, item.getID().toString());

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragmentItem = new ItemFragment();

        fragmentItem.setArguments(data);

        fm.beginTransaction()
                .replace(R.id.fragment_2_2, fragmentItem)
                .commit();

    }

    @Override
    public void onItemFinished(Item item) {

        // This method should save the file to disk using ShopItemJSONSerializer
        // If there isn't one, make one (initial check only)

        ShopItemJSONSerializer jsonSerializer = new ShopItemJSONSerializer(this, "items");

        ArrayList<Item> currentItemsList = ItemInventory.get(this).getItems();
        currentItemsList.add(item);

        try {
            jsonSerializer.saveItems(currentItemsList);
        } catch (JSONException jEx) {
            jEx.printStackTrace();
        } catch (IOException iEx) {
            iEx.printStackTrace();
        }

        Toast.makeText(this, item.toString() + " added to inventory", Toast.LENGTH_SHORT).show();
        newItem();
    }

    public void newItem() {

        Bundle data = new Bundle();
        data.putInt(EXTRA_ICON_NUMBER, -1);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragmentItem = new ItemFragment();
        fragmentItem.setArguments(data);

        fm.beginTransaction()
                .replace(R.id.fragment_2_2, fragmentItem)
                .commit();
    }

    @Override
    public void onSaleLogged() {
    }
}
