package com.stormcloud.cashregister;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.starmicronics.stario.StarIOPort;
import com.starmicronics.stario.StarIOPortException;
import com.starmicronics.stario.StarPrinterStatus;

/**
 * Class intended to log a sale for the CashRegister app.
 * <p/>
 * CustomerReceipt contains a list of items and the price total, a date for the
 * sale, the amount received/tendered.
 * <p/>
 * The CustomerReceipt class can also keep a log of itself. Daily logs are
 * created to keep track of sales. Files are named with the date. Each receipt
 * will be numbered sequentially. Receipt numbers are reset each day.
 * <p/>
 * This class is also in charge of printing itself or sending commands to the printer and
 * cash drawer.
 *
 * @author sanncchhan
 */
public class CustomerReceipt {

    private ArrayList<Item> mReceiptItems;

    private static CustomerReceipt sCustomerReceipt;
    private Context mAppContext;

    private Date mDate;
    private double mTendered;

    DecimalFormat decimalFormatter;
    private final String DECIMAL_FORMAT = "0.00";

    private SharedPreferences mPreferences;

    // Maybe change this to be settable in ManagerActivity in the future.
    public static final double TAX_RATE = 0.09;

    private CustomerReceipt(Context appContext) {

        mAppContext = appContext;
        mDate = new Date();

        mReceiptItems = new ArrayList<Item>();

        decimalFormatter = new DecimalFormat(DECIMAL_FORMAT);

        mPreferences = appContext.getSharedPreferences("pref", appContext.MODE_PRIVATE);
    }

    public static CustomerReceipt get(Context c) {
        if (sCustomerReceipt == null) {
            sCustomerReceipt = new CustomerReceipt(c.getApplicationContext());

        }
        return sCustomerReceipt;
    }

    public ArrayList<Item> getReceiptItems() {
        return mReceiptItems;
    }

    public void addReceiptItem(Item item) {
        mReceiptItems.add(item);
    }

    public double receiptTotal() {

        double total = 0;

        for (Item i : mReceiptItems) {
            total += i.getQty() * i.getPrice();
        }
        total += taxTotal();

        return total;
    }

    public double receiptTotal(ArrayList<Item> list) {

        double total = 0;

        for (Item i : list) {
            total += i.getQty() * i.getPrice();
        }
        total += taxTotal();

        return total;
    }

    public double taxTotal() {

        double tax = 0;

        for (Item i : mReceiptItems) {
            if (i.isTaxable()) {
                tax += i.getPrice() * TAX_RATE;
            }
        }

        String temp = decimalFormatter.format(tax);

        // Does this return give a good rounding? I don't want anything more than 2 decimals
        return Double.parseDouble(temp);
    }

    public Item getItem(int position) {

        return mReceiptItems.get(position);

    }

    public void removeItem(int position) {

        mReceiptItems.remove(position);

    }

    public void clearItems() {

        mReceiptItems = new ArrayList<Item>();

    }

    public double changeTotal() {
        return getTendered() - receiptTotal();
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        this.mDate = date;
    }

    public double getTendered() {
        return mTendered;
    }

    public void setTendered(double tendered) {
        this.mTendered = tendered;
    }

    public double getChange() {
        return getTendered() - receiptTotal();
    }

    @Override
    public String toString() {

        return logSale();

    }

    public String logSale() {

        /**
         * The receipt will be printed in the following format:
         *
         * [Time],[PurchaseTotal],
         * [ItemName1]:[ItemQty1]:[ItemSubtotal1],
         * [ItemName2]:[ItemQty2]:[ItemSubtotal2],
         * [ItemNameX]:[ItemQtyX]:[ItemSubtotalX]
         *
         * Each entry will end with a newline character to note the end of the entry.
         */

        String temp = "";

        String timeFormat = "kk:mm"; // 24-hour format
        SimpleDateFormat dateFormatter = new SimpleDateFormat(timeFormat);

        // DateTimeFormatter formatter =
        // DateTimeFormatter.ofPattern("yyyy MM dd");
        // String text = date.toString(formatter);
        // LocalDate date = LocalDate.parse(text, formatter);

        // Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("");
        // String newString = new SimpleDateFormat("H:mm").format(date); // 9:00

        temp = dateFormatter.format(new Date());

        if (!mReceiptItems.isEmpty()) {

            // Begin logging the items
            temp += ",";

            String output = decimalFormatter.format(this.receiptTotal());

            temp += output;
            temp += ",";

            for (Item item : mReceiptItems) {
                temp += item.getName();
                temp += ":";
                temp += item.getQty();
                temp += ":";
                temp += item.getPrice() * item.getQty();
                temp += ",";
            }
        }

        temp += "\n";

        return temp;
    }

    public void PrintPaperReceipt(Context context,
                                  String portName, String portSettings) {

        ArrayList<byte[]> list = new ArrayList<byte[]>();

        list.add(new byte[]{0x1b, 0x1d, 0x61, 0x01}); // Alignment (center)

        // list.add("[If loaded.. Logo1 goes here]\r\n".getBytes());

        String headlineName = mPreferences.getString("BusinessName", "");
        String headlineAddress1 = mPreferences.getString("BusinessAddress1", "");
        String headlineAddress2 = mPreferences.getString("BusinessAddress2", "");
        String headlinePhoneNum = mPreferences.getString("BusinessPhoneNum", "");

        list.add((headlineName + "\n").getBytes());
        list.add((headlineAddress1 + "\n").getBytes());
        list.add((headlineAddress2 + "\n").getBytes());
        list.add((headlinePhoneNum + "\n\r\n").getBytes());

        list.add(new byte[]{0x1b, 0x1d, 0x61, 0x00}); // Alignment Left

        list.add(new byte[]{0x1b, 0x44, 0x02, 0x10, 0x22, 0x00}); // Set horizontal tab

        SimpleDateFormat sdf = new SimpleDateFormat();

        String datePattern = "EEE, d MMM yyyy";
        String timePattern = "hh:mm aaa";
        String formattedDate = "";
        String formattedTime = "";

        sdf.applyPattern(datePattern);
        formattedDate = sdf.format(mDate);

        sdf.applyPattern(timePattern);
        formattedTime = sdf.format(mDate);


        list.add(String.format("Date: %s", formattedDate).getBytes());

        list.add(new byte[]{' ', 0x09}); // Moving Horizontal Tab

        list.add(String.format("Time: %s\r\n------------------------------------------------\r\n\r\n", formattedTime).getBytes());

        list.add(new byte[]{0x1b, 0x2d, 0x31}); // underline on
        list.add("Quantity".getBytes());

        list.add(new byte[]{0x1b, 0x2d, 0x30}); // underline off

        list.add("   ".getBytes());

        list.add(new byte[]{0x1b, 0x2d, 0x31}); // underline on

        list.add("Item".getBytes());

        list.add(new byte[]{0x1b, 0x2d, 0x30}); // underline off

        list.add(new byte[]{0x09, 0x09, 0x20, 0x20});

        list.add(new byte[]{0x1b, 0x2d, 0x31}); // underline on

        list.add("Subtotal\r\n".getBytes());

        list.add(new byte[]{0x1b, 0x2d, 0x30}); // underline off
        for (Item i : mReceiptItems) {

            String tempPrice = decimalFormatter.format(i.getPrice());
            list.add(String.format("%d @ %s", i.getQty(), tempPrice).getBytes());
            list.add("   ".getBytes());
            list.add(i.getName().getBytes());
            list.add(new byte[]{0x09, 0x20, 0x20});

            String total = decimalFormatter.format(i.getPrice() * i.getQty());
            list.add(total.getBytes());
            list.add("\r\n".getBytes());
        }

        list.add("\r\n".getBytes());

        list.add(new byte[]{0x1b, 0x45}); // bold

        list.add(new byte[]{0x09, 0x09});

        list.add("Total:".getBytes());

        list.add(new byte[]{0x1b, 0x46}); // bold off

        list.add(new byte[]{0x09, 0x20, 0x20});

        list.add(decimalFormatter.format(receiptTotal()).getBytes());


//        // Notice that we use a unicode representation because that is
//        // how Java expresses these bytes as double byte unicode
//        // This will TAB to the next horizontal position
//        list.add("  Description   \u0009         Total\r\n".getBytes());

        list.add(String.format("\r\n------------------------------------------------\r\n\r\n").getBytes());//".getBytes());

        list.add(new byte[]{0x1b, 0x1d, 0x61, 0x01}); // Alignment (center)

        list.add("Thanks for visiting us!\r\n".getBytes());
        list.add("Come back soon :D\r\n\r\n\r\n".getBytes());

        list.add(new byte[]{0x1b, 0x64, 0x02}); // Cut
        list.add(new byte[]{0x07}); // Kick cash drawer

        sendCommand(context, portName, portSettings, list);
    }

    public void sendCommand(Context context, String portName, String portSettings, ArrayList<byte[]> byteList) {
        StarIOPort port = null;
        try {
            /*
             * using StarIOPort3.1.jar (support USB Port) Android OS Version: upper 2.2
			 */
            port = StarIOPort.getPort(portName, portSettings, 10000, context);
            /*
             * using StarIOPort.jar Android OS Version: under 2.1 port = StarIOPort.getPort(portName, portSettings, 10000);
			 */
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }

			/*
			 * Using Begin / End Checked Block method When sending large amounts of raster data,
			 * adjust the value in the timeout in the "StarIOPort.getPort" in order to prevent
			 * "timeout" of the "endCheckedBlock method" while a printing.
			 *
			 * If receipt print is success but timeout error occurs(Show message which is "There
			 * was no response of the printer within the timeout period." ), need to change value
			 * of timeout more longer in "StarIOPort.getPort" method.
			 * (e.g.) 10000 -> 30000
			 */
            StarPrinterStatus status = port.beginCheckedBlock();

            if (true == status.offline) {
                throw new StarIOPortException("A printer is offline");
            }

            byte[] commandToSendToPrinter = convertFromListByteArrayTobyteArray(byteList);
            port.writePort(commandToSendToPrinter, 0, commandToSendToPrinter.length);

            port.setEndCheckedBlockTimeoutMillis(30000);// Change the timeout time of endCheckedBlock method.
            status = port.endCheckedBlock();

            if (status.coverOpen == true) {
                throw new StarIOPortException("Printer cover is open");
            } else if (status.receiptPaperEmpty == true) {
                throw new StarIOPortException("Receipt paper is empty");
            } else if (status.offline == true) {
                throw new StarIOPortException("Printer is offline");
            }
        } catch (StarIOPortException e) {
//            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
//            dialog.setNegativeButton("OK", null);
//            AlertDialog alert = dialog.create();
//            alert.setTitle("Failure");
//            alert.setMessage(e.getMessage());
//            alert.setCancelable(false);
//            alert.show();
        } finally {
            if (port != null) {
                try {
                    StarIOPort.releasePort(port);
                } catch (StarIOPortException e) {
                }
            }
        }
    }

    // Helper method to help print to the Receipt Printer
    private static byte[] convertFromListByteArrayTobyteArray(List<byte[]> ByteArray) {
        int dataLength = 0;
        for (int i = 0; i < ByteArray.size(); i++) {
            dataLength += ByteArray.get(i).length;
        }

        int distPosition = 0;
        byte[] byteArray = new byte[dataLength];
        for (int i = 0; i < ByteArray.size(); i++) {
            System.arraycopy(ByteArray.get(i), 0, byteArray, distPosition, ByteArray.get(i).length);
            distPosition += ByteArray.get(i).length;
        }

        return byteArray;
    }

    public void PrintSampleReceipt(Context context,
                                   String portName, String portSettings) {

        ArrayList<byte[]> list = new ArrayList<byte[]>();

        list.add(new byte[]{0x1b, 0x1d, 0x61, 0x01}); // Alignment (center)

        // list.add("[If loaded.. Logo1 goes here]\r\n".getBytes());

        list.add("\nHey Lee...\r\n".getBytes());
        list.add("Fight me\r\n!!!!!\r\n\r\n".getBytes());

        if (mAppContext != null) {
            CustomerReceipt.get(mAppContext).sendCommand(context, portName, portSettings, list);
        }
    }
}
