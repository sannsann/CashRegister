package com.stormcloud.cashregister;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fortysevendeg.swipelistview.SwipeListView;
import com.fortysevendeg.swipelistview.SwipeListViewListener;

public class ReceiptFragment extends ListFragment {

    private static final String TAG = "ReceiptFragment";

    private ArrayList<Item> mReceiptItems;

    private CustomerReceipt mCustomerReceipt;

//    private TextView tvLine1;
    private TextView tvLine2;
//    private TextView tvLine3;

    private SwipeListView slv;
    private TextView mGrandTotalTextView;
    private TextView mTaxTotalTextView;
    private TextView mTenderedTextView;
    private TextView mChangeTotalTextView;

    private Button mReceiptBackButton;
    private Button mReceiptForwardButton;

    private Button mCompleteTransactionButton;

    private File todaysSalesFile;

    private int counter;
    private int maxCounter;

    OnSaleLoggedListener mSaleLoggedListener;

    public interface OnSaleLoggedListener {
        public void onSaleLogged();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mReceiptItems = CustomerReceipt.get(getActivity()).getReceiptItems();
        SwipeViewAdapter adapter = new SwipeViewAdapter(mReceiptItems);
        setListAdapter(adapter);

        todaysSalesFile = getSalesFileHandle();
        counter = getCounter();
        maxCounter = getCounter();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mSaleLoggedListener = (OnSaleLoggedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnSaleLoggedListener");
        }
    }


        @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_receipt, parent, false);

        /**
         * Begin receipt header
         */

        TextView tvHeadlineName = (TextView) v
                .findViewById(R.id.receipt_header_line1);
        TextView tvHeadlineAddress = (TextView) v
                .findViewById(R.id.receipt_header_line2);
        TextView tvHeadlinePhoneNum = (TextView) v
                .findViewById(R.id.receipt_header_line3);

        // Use shared preferences to retrieve info for header
        SharedPreferences preferences = this.getActivity()
                .getSharedPreferences("pref", Context.MODE_PRIVATE);

        String headlineName = preferences.getString("BusinessName",
                "MyBusinessName");
        tvHeadlineName.setText(headlineName);

        String headlineAddress = preferences.getString("BusinessAddress",
                "123 MyAddress St.");
        tvHeadlineAddress.setText(headlineAddress);

        String headlinePhoneNum = preferences.getString("BusinessPhoneNum",
                "(012) 345-6789");
        tvHeadlinePhoneNum.setText(headlinePhoneNum);

        /**
         * Begin itemized portion of receipt
         */
        slv = (SwipeListView) v.findViewById(android.R.id.list);

        // Swipe mode
        slv.setSwipeMode(SwipeListView.SWIPE_MODE_RIGHT);

        // Swiping towards the left (4 available options)
        slv.setSwipeActionLeft(SwipeListView.SWIPE_ACTION_DISMISS);

        // Swiping towards the right
        slv.setSwipeActionRight(SwipeListView.SWIPE_ACTION_DISMISS);
        // slv.setOffsetLeft(convertDpToPixel(260f)); // left side offset
        // slv.setOffsetRight(convertDpToPixel(0f)); // right side offset
        slv.setAnimationTime(250); // animation time
        // slv.setSwipeOpenOnLongPress(true)

        SwipeListViewListener slvListener = new SwipeListViewListener() {

            @Override
            public void onStartOpen(int position, int action, boolean right) {
                // Intentionally left blank

            }

            @Override
            public void onStartClose(int position, boolean right) {
                // Intentionally left blank

            }

            @Override
            public void onOpened(int position, boolean toRight) {
                // Intentionally left blank

            }

            @Override
            public void onMove(int position, float x) {
                // Intentionally left blank

            }

            @Override
            public void onListChanged() {
                // Intentionally left blank
            }

            @Override
            public void onLastListItem() {
                // Intentionally left blank

            }

            @Override
            public void onFirstListItem() {
                // Intentionally left blank

//                Toast.makeText(getActivity(),"First Item is showing", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onDismiss(int[] reverseSortedPositions) {

                // This callback takes an int array, reverseSortedPositions,
                // of length 1. The value of reverseSortedPositions[0], when
                // called by the SwipeListView class, is the position of the
                // item that was swiped

                CustomerReceipt.get(getActivity()).removeItem(
                        reverseSortedPositions[0]);

                ((SwipeViewAdapter) getListAdapter()).notifyDataSetChanged();

                updateReceipt();

            }

            @Override
            public void onClosed(int position, boolean fromRight) {
                // Intentionally left blank

            }

            @Override
            public void onClickFrontView(int position) {

                Log.d("swipe", String.format("onClickFrontView %d", position));

                // When item touched, front view will open
                // slv.openAnimate(position);

            }

            @Override
            public void onClickBackView(int position) {
                Log.d("swipe", String.format("onClickBackView %d", position));

                // slv.closeAnimate(position);// when you touch back view
                // it will close

            }

            @Override
            public void onChoiceStarted() {
                // Intentionally left blank

            }

            @Override
            public void onChoiceEnded() {
                // Intentionally left blank

            }

            @Override
            public void onChoiceChanged(int position, boolean selected) {
                // Intentionally left blank

            }

            @Override
            public int onChangeSwipeMode(int position) {

                return SwipeListView.SWIPE_MODE_BOTH;
            }
        };

        slv.setSwipeListViewListener(slvListener);

        /**
         * Begin totals/footer of receipt
         */

        mTaxTotalTextView = (TextView) v
                .findViewById(R.id.receipt_tax_total);

        mGrandTotalTextView = (TextView) v
                .findViewById(R.id.receipt_grand_total);

        mTenderedTextView = (TextView) v
                .findViewById(R.id.receipt_tendered_total);

        mChangeTotalTextView = (TextView) v
                .findViewById(R.id.receipt_change_total);

        mCompleteTransactionButton = (Button) v
                .findViewById(R.id.receipt_complete_transaction);

        mCompleteTransactionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (todaysSalesFile.exists()) {

                    logSale();

                    try {
                        Thread.sleep(100);                 //1000 milliseconds is one second.
                    } catch(InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }

                    // Clear the ReceiptFragment's display value
                    mSaleLoggedListener.onSaleLogged();

                    // Use a thread to kick the drawer as sometimes the BlueTooth connection drops
                    // Also, this prevents lockup of main thread and prevents choke of filewrite.
                    Runnable runnable = new Runnable() {
                        public void run() {
                            kickCashDrawer();
                        }
                    };

                    Thread drawerThread = new Thread(runnable);
                    drawerThread.start();

                }
            }
        });

        mReceiptBackButton = (Button) v.findViewById(R.id.receipt_back);
        mReceiptBackButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Fills the fragment with the previous sale's info
                // Header would update to when the sales was made
                // Time
                // Receipt #

                // Need to start a new receipt
                CustomerReceipt.get(getActivity()).clearItems();

                loadPreviousSale();

                mReceiptItems = CustomerReceipt.get(getActivity()).getReceiptItems();
                SwipeViewAdapter adapter = new SwipeViewAdapter(mReceiptItems);
                setListAdapter(adapter);
                updateReceipt();

                counter -= 1;
                Log.d(TAG, String.valueOf(counter));

            }
        });

        tvLine2 = (TextView) v.findViewById(R.id.receipt_header_line2);

        mReceiptForwardButton = (Button) v.findViewById(R.id.receipt_forward);
        mReceiptForwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "counter: " + String.valueOf(counter));
                Log.d(TAG, "maxCounter: " + String.valueOf(maxCounter));

                counter += 1;

                if (counter >= maxCounter) {

                    // Clear the items?
                    CustomerReceipt.get(getActivity()).clearItems();
                    mReceiptItems = CustomerReceipt.get(getActivity()).getReceiptItems();
                    SwipeViewAdapter adapter = new SwipeViewAdapter(mReceiptItems);
                    setListAdapter(adapter);
                    updateReceipt();

                    counter = maxCounter;
                    return;

                }

                CustomerReceipt.get(getActivity()).clearItems();

                loadPreviousSale();

                mReceiptItems = CustomerReceipt.get(getActivity()).getReceiptItems();
                SwipeViewAdapter adapter = new SwipeViewAdapter(mReceiptItems);
                setListAdapter(adapter);
                updateReceipt();

            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        // ((ReceiptItemAdapter) getListAdapter()).notifyDataSetChanged();
        ((SwipeViewAdapter) getListAdapter()).notifyDataSetChanged();

        updateReceipt();
    }

    /*
     * Helper method to update receipt fragment view every time a change is made
     * to the receipt
     */
    public void updateReceipt() {

        mReceiptItems = CustomerReceipt.get(getActivity()).getReceiptItems();

        View v = getView();

        // Get the new grand total and update the view
        double grandTotal = CustomerReceipt.get(getActivity()).receiptTotal();
        double taxTotal = CustomerReceipt.get(getActivity()).taxTotal();

        // Use the formatter to round to nearest 100ths place.
        DecimalFormat myFormatter = new DecimalFormat("0.00");

        // Reset the SwipeListView with the updated ReceiptItems
        if (slv != null) {
            SwipeViewAdapter adapter = new SwipeViewAdapter(mReceiptItems);
            setListAdapter(adapter);
        }

        // Update all TextViews accordingly as well.

        if (mTaxTotalTextView != null)
            mTaxTotalTextView.setText(myFormatter.format(taxTotal));

        if (mGrandTotalTextView != null)
            mGrandTotalTextView.setText(myFormatter.format(grandTotal));

    }


    public class SwipeViewAdapter extends ArrayAdapter<Item> {

        Context mFragmentContext;

        public SwipeViewAdapter(ArrayList<Item> items) {
            super(getActivity(), R.layout.fragment_receipt, items);
            this.mFragmentContext = getActivity();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            // Inflate the SwipeListView item layout
            LayoutInflater inflater = (LayoutInflater) mFragmentContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = inflater.inflate(R.layout.receipt_swipelistview_item,
                    parent, false);
            row.setBackgroundColor(getActivity().getResources().getColor(
                    android.R.color.holo_red_dark));

            // Get the item
            Item c = (Item) (getListAdapter()).getItem(position);

            // find the TextView and the button defined in the layout
            TextView textViewItemName = (TextView) row
                    .findViewById(R.id.text_item_name);
            TextView textViewItemPrice = (TextView) row
                    .findViewById(R.id.text_item_price);
            TextView textViewItemQty = (TextView) row
                    .findViewById(R.id.text_item_qty);

            // set each TextView with respective value
            // If name is longer than 25, reduce the font size.
            if (c.getName().length() > 16) {
                textViewItemName.setTextSize(20);
            }
            textViewItemName.setText(c.getName());

            DecimalFormat myFormatter = new DecimalFormat("0.00");
            textViewItemPrice.setText(myFormatter.format(c.getPrice()));

            if (String.valueOf(c.getQty()) == null) {
                Log.e(TAG, "Value of qty in adapter getView is null. Problem.");
            } else {
                String subTotal = myFormatter.format(c.getQty() * c.getPrice());
                textViewItemQty.setText(String.valueOf(c.getQty()));
                textViewItemPrice.setText(subTotal);
            }

            return row;
        }

    }

    public void setTendered(String tenderedAmount) {
        mTenderedTextView.setText(tenderedAmount);
    }

    private File getSalesFileHandle() {

        // *** Begin file creation of a todaysSalesFile file ***

        Date today = new Date();
        SimpleDateFormat simpleDateFormatter = new SimpleDateFormat();

        // Parse date to get the year value.
        simpleDateFormatter.applyPattern("yyyy"); // 2014
        String year = simpleDateFormatter.format(today); // Saved the year. Move
        // on to the month

        // Parse date to get month value.
        simpleDateFormatter.applyPattern("MMM"); // Jul
        String month = simpleDateFormatter.format(today);

        String format = "MM-dd-yy";
        simpleDateFormatter.applyLocalizedPattern(format);

        String fileName = simpleDateFormatter.format(today);

        // Get the directory where app files are stored
        File appDir = getActivity().getFilesDir();

        File yearDir = new File(appDir, year);
        if (!yearDir.exists())
            yearDir.mkdir();

        File monthDir = new File(yearDir, month);
        if (!monthDir.exists())
            monthDir.mkdir();

        File salesFile = new File(monthDir, fileName + ".csv");
        if (!salesFile.exists()) {
            try {
                salesFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return salesFile;
    }

    public void parseItems(String itemsOfSale) {

        // Parses the string item in the format of: ItemName:QtyPurchased:Subtotal
        // There is N entries, each separated by a comma. Go through all of them and add to a
        // the CustomerReceipt singleton.

        // Find index of first comma in itemsOfSale
        int itemsComma, previousComma;
        ArrayList<String> stringItems = new ArrayList<String>();

        // While there are items still in the original string
        // This is a primitive way to check, it should not fail according to the
        // data structure.
        while (itemsOfSale.length() > 1) {
            // First comma is at:
            itemsComma = itemsOfSale.indexOf(",");

            stringItems.add(itemsOfSale.substring(0, itemsComma));

            // Done with the first item, trim to string to do the other items
            itemsOfSale = itemsOfSale.substring(itemsComma + 1);
        }

        String itemName, itemQty, itemSubtotal;
        Item temp;
        int colonPosition, previousColon;

        for (String s : stringItems) {
            colonPosition = s.indexOf(":");
            previousColon = colonPosition;

            itemName = s.substring(0, colonPosition);

            colonPosition = s.indexOf(":", previousColon + 1);
            itemQty = s.substring(previousColon + 1, colonPosition);

            previousColon = colonPosition;
            itemSubtotal = s.substring(previousColon + 1);

            // Finished parsing, let's make the item and put it into the list.
            temp = new Item();
            temp.setName(itemName);

            int qty = Integer.parseInt(itemQty);
            temp.setQty(qty);

            double subtotal = Double.parseDouble(itemSubtotal);
            temp.setPrice(subtotal / qty);

            CustomerReceipt.get(getActivity()).addReceiptItem(temp);
        }
    }

    public void logSale() {

        // Open the file and append the receipt to that file

        try {
            FileWriter fw = new FileWriter(todaysSalesFile, true);
            fw.append(CustomerReceipt.get(getActivity())
                    .logSale());
            fw.close();

            CustomerReceipt.get(getActivity()).clearItems();
            Toast.makeText(getActivity(),
                    "Transaction Complete! Thanks!",
                    Toast.LENGTH_LONG).show();

            updateReceipt();

            // Kick out the drawer

            mCompleteTransactionButton
                    .setVisibility(View.INVISIBLE);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void kickCashDrawer() {

        ArrayList<byte[]> list = new ArrayList<byte[]>();

        list.add(new byte[]{0x07}); // Hexcode to kick cash drawer

        CustomerReceipt.get(getActivity())
                .sendCommand(getActivity(), "BT:Star Micronics", "", list);

    }

    public int getCounter() {

        int tempCounter = 0;

        try {

            InputStream inputStream = new FileInputStream(todaysSalesFile);

            // If file the available for reading
            if (inputStream != null) {

                // Prepare the file for reading
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                String line;

                // Figure out how many sales entries are there today
                while ((line = bufferedReader.readLine()) != null) {
                    tempCounter++;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return tempCounter;
    }

    public void loadPreviousSale() {
        try {

            ArrayList<String> recentEntries = new ArrayList<String>();

            InputStream inputStream = new FileInputStream(todaysSalesFile);

            // If file the available for reading
            if (inputStream != null) {

                // Prepare the file for reading
                inputStream = new FileInputStream(todaysSalesFile);
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                String line;
                String logEntry = "";

                if (counter >= 0) {
                    // Read until we are one before the desired entry
                    for (int i = 0; i < counter - 1; i++) {
                        line = bufferedReader.readLine();
                    }

                    line = bufferedReader.readLine();
                    recentEntries.add(line);
                    logEntry = line;

                } else {

                    Toast.makeText(getActivity(), "No previous receipts!",
                            Toast.LENGTH_SHORT).show();

                    return;
                }

                // Now we have the entry
                // Parse the last entry into items
                // Supply that to the SwipeViewAdapter

                if (logEntry == null || !(logEntry.length() > 0) || logEntry.isEmpty() ) {
                    return;
                }

                String timeOfSale = logEntry.substring(0, 5); // Exclusive upperlimit

                tvLine2.setText(timeOfSale);

                int commaPosition = logEntry.indexOf(",");
                int previousIndex;

                previousIndex = commaPosition;
                commaPosition = logEntry.indexOf(",", previousIndex + 1);

                String totalOfSale = logEntry.substring(previousIndex + 1, commaPosition);

                previousIndex = commaPosition;
                commaPosition = logEntry.indexOf(",", previousIndex + 1);

                String itemsOfSale = logEntry.substring(previousIndex + 1);


                // We now have a String of items in the following format:
                // ItemName:QtyPurchased:Subtotal
                // Parse Items and display;

                parseItems(itemsOfSale);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
