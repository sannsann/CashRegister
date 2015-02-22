package com.stormcloud.cashregister;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fortysevendeg.swipelistview.SwipeListView;
import com.fortysevendeg.swipelistview.SwipeListViewListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.text.DateFormat;
import java.util.concurrent.TimeUnit;

/**
 * Created by schhan on 11/30/14.
 * <p/>
 * Hosted by the ManagerActivity in the leftContainer.
 * <p/>
 * ReceiptDayReportFragment will sum up the days sales and report in similar fashion to Receipt
 * fragment.
 * <p/>
 * DatePickerDialog/ dialog(?) should be used to quickly select date.
 * <p/>
 * Header displays the date of Receipt with functioning back and forward buttons used to navigate
 * through the receipts from current Receipt.
 */
public class ReceiptDayReportFragment extends ReceiptFragment {

    public static final String EXTRA_REPORT_DATE = "com.stormcloud.cashregister.report_date";

    private static final String TAG = "ReceiptDayReportFragment";
    private SwipeListView slv;

    private Calendar currentCalenderDay;

    private Date currentDate;
    private Date currentReportDate;

    private File currentSalesFile;
    private int daysDifference;

    private TextView mGrandTotalTextView;
    private TextView mTaxTotalTextView;
    private TextView mTenderedTextView;
    private TextView mChangeTotalTextView;

    private TextView mDateHeadline;

    private Button mReceiptBackButton;
    private Button mReceiptForwardButton;

    private ArrayList<Item> mSoldItems;

    private DateFormat dateFormat;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        mSoldItems = new ArrayList<Item>();

        currentDate = new Date();

        currentCalenderDay = Calendar.getInstance();

        dateFormat = new SimpleDateFormat("MM-dd-yy");

        daysDifference = 0;

        Bundle bundle = this.getArguments();

        if (bundle != null) {
            String reportDate = bundle.getString(EXTRA_REPORT_DATE, "NONE");

            currentSalesFile = getSalesFileHandleFromFormattedDate(reportDate);

            Log.e(TAG, currentSalesFile.toString());

            try {
                Date date1 = dateFormat.parse(reportDate);
                long diff = currentDate.getTime() - date1.getTime();

                daysDifference = (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
                daysDifference *= -1;

                Log.e(TAG, "Days: " + TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));


            } catch (ParseException e) {
                e.printStackTrace();
            }


        } else {

            currentSalesFile = getSalesFileHandle();

        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSoldItems.clear();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "In onResume");

        currentSalesFile = getSalesFileHandle();

        if (!currentSalesFile.exists()) {
            Toast.makeText(getActivity(), "No sales log for " + getDateString(),
                    Toast.LENGTH_LONG).show();
            return;
        } else {

        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_report_fragment, menu);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_receipt, parent, false);

        /**
         * Begin filling in header portion of receipt
         */

        TextView tvHeadlineName = (TextView) v
                .findViewById(R.id.receipt_header_line1);

        mDateHeadline = (TextView) v
                .findViewById(R.id.receipt_header_line2);

        TextView tvHeadlinePhoneNum = (TextView) v
                .findViewById(R.id.receipt_header_line3);

        // Use shared preferences to retrieve info for header
        SharedPreferences preferences = this.getActivity()
                .getSharedPreferences("pref", Context.MODE_PRIVATE);

        String headlineName = preferences.getString("BusinessName",
                "MyBusinessName");
        tvHeadlineName.setText(headlineName);

        String headlineAddress = getDateString();
        mDateHeadline.setText(headlineAddress);

        /**
         *
         * Begin itemized portion of receipt
         *
         */

        slv = (SwipeListView) v.findViewById(android.R.id.list);

        // Swipe mode, turned off as it is unnecessary at the moment
        slv.setSwipeMode(SwipeListView.SWIPE_MODE_NONE);

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

//                CustomerReceipt.get(getActivity()).removeItem(
//                        reverseSortedPositions[0]);
//
//                ((SwipeViewAdapter) getListAdapter()).notifyDataSetChanged();
//
//                updateReceipt();

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
         *
         * Begin totals/footer of receipt
         *
         */

        mTaxTotalTextView = (TextView) v
                .findViewById(R.id.receipt_tax_total);

        mGrandTotalTextView = (TextView) v
                .findViewById(R.id.receipt_grand_total);

        mTenderedTextView = (TextView) v
                .findViewById(R.id.receipt_tendered_total);

        mChangeTotalTextView = (TextView) v
                .findViewById(R.id.receipt_change_total);

        mReceiptBackButton = (Button) v.findViewById(R.id.receipt_back);
        mReceiptBackButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Fills the fragment with the previous sale's info

                CustomerReceipt.get(getActivity()).clearItems();

                daysDifference--;

                currentSalesFile = getSalesFileHandle();

                // Update the headline with the date.
                mDateHeadline.setText(getDateString());

                ArrayList<Item> listToDisplay = getSalesList();

                if (listToDisplay != null) {

                    SwipeViewAdapter adapter = new SwipeViewAdapter(listToDisplay);
                    setListAdapter(adapter);
                    updateReceipt();

                    double grandTotal = CustomerReceipt.get(getActivity()).receiptTotal(listToDisplay);

                    // Use the formatter to round to nearest 100ths place.
                    DecimalFormat myFormatter = new DecimalFormat("0.00");
                    mGrandTotalTextView.setText(myFormatter.format(grandTotal));
                } else {
                    listToDisplay = new ArrayList<Item>();
                    SwipeViewAdapter adapter = new SwipeViewAdapter(listToDisplay);
                    setListAdapter(adapter);
                    updateReceipt();

                    double grandTotal = CustomerReceipt.get(getActivity()).receiptTotal(listToDisplay);

                    // Use the formatter to round to nearest 100ths place.
                    DecimalFormat myFormatter = new DecimalFormat("0.00");
                    mGrandTotalTextView.setText(myFormatter.format(grandTotal));

                }
            }
        });

        mReceiptForwardButton = (Button) v.findViewById(R.id.receipt_forward);
        mReceiptForwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                CustomerReceipt.get(getActivity()).clearItems();
//                SwipeViewAdapter adapter = new SwipeViewAdapter(mSoldItems);
//                setListAdapter(adapter);
//                updateReceipt();

                if (daysDifference < 0) {
                    daysDifference += 1;
                } else if (daysDifference == 0) {
                    Toast.makeText(getActivity(), "Proceeding day not available yet.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                currentSalesFile = getSalesFileHandle();

                mDateHeadline.setText(getDateString());

                ArrayList<Item> listToDisplay = getSalesList();

                if (listToDisplay == null) {
                    listToDisplay = new ArrayList<Item>();
                }

                SwipeViewAdapter adapter = new SwipeViewAdapter(listToDisplay);
                setListAdapter(adapter);
                updateReceipt();

                double grandTotal = CustomerReceipt.get(getActivity()).receiptTotal(listToDisplay);

                // Use the formatter to round to nearest 100ths place.
                DecimalFormat myFormatter = new DecimalFormat("0.00");
                mGrandTotalTextView.setText(myFormatter.format(grandTotal));
            }
        });

        ArrayList<Item> listToDisplay = getSalesList();

        if (listToDisplay != null) {
            SwipeViewAdapter adapter = new SwipeViewAdapter(listToDisplay);
            setListAdapter(adapter);
            updateReceipt();

            double grandTotal = CustomerReceipt.get(getActivity()).receiptTotal(listToDisplay);

            // Use the formatter to round to nearest 100ths place.
            DecimalFormat myFormatter = new DecimalFormat("0.00");
            mGrandTotalTextView.setText(myFormatter.format(grandTotal));
        }

        return v;
    }

    private String getDateString() {
        Calendar cal = Calendar.getInstance();

        cal.setTime(currentDate);

        cal.add(Calendar.DATE, daysDifference);

        return dateFormat.format(cal.getTime());
    }

    private File getSalesFileHandle() {

        try {
            Date date = dateFormat.parse(getDateString());

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            currentReportDate = date;

        } catch (ParseException e) {
            e.printStackTrace();
        }


        SimpleDateFormat simpleDateFormatter = new SimpleDateFormat();

        // Parse date to get the year value.
        simpleDateFormatter.applyPattern("yyyy"); // 2014
        String year = simpleDateFormatter.format(currentReportDate); // Saved the year. Next: month

        // Parse date to get month value.
        simpleDateFormatter.applyPattern("MMM"); // Jul
        String month = simpleDateFormatter.format(currentReportDate);

        String format = "MM-dd-yy";
        simpleDateFormatter.applyLocalizedPattern(format);

        String fileName = simpleDateFormatter.format(currentReportDate);
        Log.d(TAG, fileName);

        File appDir = getActivity().getFilesDir();

        File yearDir = new File(appDir, year);
        if (!yearDir.exists())
            yearDir.mkdir();

        File monthDir = new File(yearDir, month);
        if (!monthDir.exists())
            monthDir.mkdir();

        return new File(monthDir, fileName + ".csv");
    }

    private ArrayList<Item> getSalesList() {

        ArrayList<Item> newList = new ArrayList<Item>();

        mSoldItems.clear();

        //Reconstruct the items from the String of items.
        //Go through each item, see if that item exists in mSoldItems.
        //--If does not exist, add that item and it's quantity and subtotal as done already
        //--If item exists, update quantity and subtotal.
        //Do this with every line and then populate the slv with the complete list.

        try {

            InputStream inputStream = new FileInputStream(currentSalesFile);

            // If file the available for reading
            if (inputStream != null) {

                // Prepare the file for reading
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                String logEntry = "";
                ArrayList<String> itemsInStringFormat = new ArrayList<String>();

                // Read each line and parse through for time and items.
                while ((logEntry = bufferedReader.readLine()) != null) {

                    // Reset the ArrayList each time we read a new line.
                    itemsInStringFormat.clear();

                    Log.d(TAG, "logEntry: " + logEntry);

                    if (logEntry.length() < 1) {
                        Log.d(TAG, "logEntry was length<1, invalid entry detected");
                        return null;
                    }

                    String timeOfSale = logEntry.substring(0, 5); //The second parameter is the
                                                                 // exclusive upperlimit

                    int commaPosition = logEntry.indexOf(",");
                    int previousIndex;

                    previousIndex = commaPosition;
                    commaPosition = logEntry.indexOf(",", previousIndex + 1);

                    String totalOfSale = logEntry.substring(previousIndex + 1,
                            commaPosition);

                    previousIndex = commaPosition;
                    commaPosition = logEntry.indexOf(",", previousIndex + 1);
                    String itemsOfSale = logEntry.substring(previousIndex + 1);

                    // We now have a String of items in the following format:
                    // ItemName:QtyPurchased:Subtotal
                    // First, separate the items
                    // Second, parse the String for its contents,
                    // while there are more items, keep looping.

                    // Find index of first comma in itemsOfSale
                    int itemsCommaIndex, previousCommaIndex;


                    // While there are items still in the original string, parse them all
                    // This is a primitive way to check, it should not fail according to the
                    // data structure.
                    // Loop ends when we have a single ',' left over.
                    while (itemsOfSale.length() > 1) {
                        // First comma is at:
                        itemsCommaIndex = itemsOfSale.indexOf(",");

                        itemsInStringFormat.add(itemsOfSale.substring(0, itemsCommaIndex));

                        // Done with the first item, trim to string to do the other items
                        itemsOfSale = itemsOfSale.substring(itemsCommaIndex + 1);
                    }
                    Log.d(TAG, "itemsInStringFormat: " + itemsInStringFormat.toString());

                    // Now have the items in a String format stored in ArrayList.
                    // Create each item from the String and store in mSoldItems.

                    for (String s : itemsInStringFormat) {

                        Item temp = new Item(s);

                        Log.d(TAG, "Item being added to mSoldItems: " + temp.toString());

                        mSoldItems.add(temp);
                    }
                }
            }

            if (mSoldItems == null || mSoldItems.size() == 0) {
                Log.d(TAG, "mSoldItems was null or empty, we in trouble.");
                return null;
            } else {
                Log.d(TAG, "mSoldItems: " + mSoldItems.toString());
            }

            // Consolidate the items in mSoldItems.
            // If we find a similar item, combine the quantities, then delete the duplicate.

            newList.clear();

            int upperLimit = mSoldItems.size();
            int i = 0;
            while (i < upperLimit) {
                Item temp = mSoldItems.get(i);

                for (int k = 1; k < upperLimit + 1; ) {

                    if (i + k == upperLimit) {
                        break;
                    }

                    Item tempNext = mSoldItems.get(i + k);

                    if (temp.getName().equals(tempNext.getName())) {
                        temp.setQty(temp.getQty() + tempNext.getQty());
                        mSoldItems.remove(i + k);
                        upperLimit--;
                    } else {
                        k++;
                    }

                    if (i + k == upperLimit) {
                        break;
                    }
                }

                newList.add(temp);
                i++;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            SwipeViewAdapter adapter = new SwipeViewAdapter(mSoldItems);
            setListAdapter(adapter);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return newList;
    }

    public void printDayReport() {

        final String DECIMAL_FORMAT = "0.00";
        DecimalFormat decimalFormatter = new DecimalFormat(DECIMAL_FORMAT);

        ArrayList<byte[]> list = new ArrayList<byte[]>();

        list.add(new byte[]{0x1b, 0x1d, 0x61, 0x01}); // Alignment (center)

        // list.add("[If loaded.. Logo1 goes here]\r\n".getBytes());

        list.add("\nMonterey Donut\r\n".getBytes());
        list.add("5930 Monterey Rd.\r\n".getBytes());
        list.add("Los Angeles, CA 90031\r\n".getBytes());
        list.add("(323) 254-2728\r\n\r\n".getBytes());

        list.add("Daily Sales Report\r\n\r\n".getBytes());

        list.add(new byte[]{0x1b, 0x1d, 0x61, 0x00}); // Alignment Left

        list.add(new byte[]{0x1b, 0x44, 0x02, 0x0D, 0x22, 0x26, 0x00}); // Set horizontal tab
        SimpleDateFormat sdf = new SimpleDateFormat();

        String datePattern = "EEE, d MMM yyyy";
        String timePattern = "hh:mm aaa";
        String formattedDate = "";
        String formattedTime = "";

        sdf.applyPattern(datePattern);
        formattedDate = sdf.format(currentReportDate);

        sdf.applyPattern(timePattern);
        formattedTime = sdf.format(new Date());

        list.add(String.format("Date: %s", formattedDate).getBytes());

        list.add(new byte[]{' ', 0x09}); // Moving Horizontal Tab

        list.add(String.format("Time: %s\r\n------------------------------------------------\r\n\r\n", formattedTime).getBytes());

//        list.add(new byte[]{0x1b, 0x45}); // bold
//        list.add(new byte[]{0x1b, 0x46}); // bold off

        list.add(new byte[]{0x1b, 0x2d, 0x31}); // underline on

        list.add("Quantity".getBytes());

//        list.add(new byte[]{0x1b, 0x2d, 0x30}); // underline off

        list.add(new byte[]{0x09});

        list.add("Item".getBytes());

        list.add(new byte[]{0x09, 0x09});

        list.add("Subtotal\r\n".getBytes());

        list.add(new byte[]{0x1b, 0x2d, 0x30}); // underline off

        for (Item i : mSoldItems) {

            String tempPrice = decimalFormatter.format(i.getPrice());
            list.add(String.format("%d @ %s", i.getQty(), tempPrice).getBytes());
            list.add(new byte[]{0x09});
            list.add(i.getName().getBytes());
            list.add(new byte[]{0x09, 0x09});

            String total = decimalFormatter.format(i.getPrice() * i.getQty());
            list.add(total.getBytes());
            list.add("\r\n".getBytes());
        }

        list.add("\r\n".getBytes());

        list.add(new byte[]{0x1b, 0x45}); // bold

        list.add(new byte[]{0x09, 0x09});

        list.add("Total:".getBytes());

        list.add(new byte[]{0x1b, 0x46}); // bold off

        list.add(new byte[]{0x09, 0x09});

        double total = CustomerReceipt.get(getActivity()).receiptTotal(mSoldItems);
        list.add(decimalFormatter.format(total).getBytes());

        list.add("\r\n\r\n\r\n".getBytes());

        list.add(new byte[]{0x1b, 0x64, 0x02}); // Cut

        CustomerReceipt.get(getActivity())
                .sendCommand(getActivity(), "BT:Star Micronics", "", list);

    }

    private File getSalesFileHandleFromFormattedDate(String formattedDate) {
        // This method seems to be a clone of getSalesFileHandle
        // formattedDate should be in the form "MM-dd-yy"

        // Parse the String into a Date object
        String dtStart = formattedDate;
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yy");

        try {
            Date date = formatter.parse(dtStart);

            Calendar cal = Calendar.getInstance();
            cal.setTime(date);

            currentReportDate = date;

        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat simpleDateFormatter = new SimpleDateFormat();

        // Parse date to get the year value.
        simpleDateFormatter.applyPattern("yyyy"); // 2014
        String year = simpleDateFormatter.format(currentReportDate); // Saved the year. Next: month

        // Parse date to get month value.
        simpleDateFormatter.applyPattern("MMM"); // Jul
        String month = simpleDateFormatter.format(currentReportDate);

        String format = "MM-dd-yy";
        simpleDateFormatter.applyLocalizedPattern(format);

        String fileName = simpleDateFormatter.format(currentReportDate);
        Log.d(TAG, fileName);

        File appDir = getActivity().getFilesDir();

        File yearDir = new File(appDir, year);
        if (!yearDir.exists())
            yearDir.mkdir();

        File monthDir = new File(yearDir, month);
        if (!monthDir.exists()) {
            monthDir.mkdir();
        }

        return new File(monthDir, fileName + ".csv");
    }
}