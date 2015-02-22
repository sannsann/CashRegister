package com.stormcloud.cashregister;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.UUID;

public class Item {
    private static final String TAG = "Item.class";

    private static final String JSON_ID = "id";
    private static final String JSON_NAME = "name";
    private static final String JSON_CATEGORY = "category";
    private static final String JSON_PRICE = "price";
    private static final String JSON_TAXABLE = "taxable";
    private static final String JSON_QTY = "qty";
    private static final String JSON_ICON = "icon";
    private static final String JSON_UPC = "upc";

    private UUID mID;
    private String mName;
    private String mCategory;
    private double mPrice;

    private boolean mTaxable;
    private int mQty;

    private int mIconCode;

    private String mUPC;

    public static Integer[] mThumbIds = {
            R.drawable.coffee_large,
            R.drawable.coffee_small,
            R.drawable.cookie,
            R.drawable.croissant_plain,
            R.drawable.croissant_rect,
            R.drawable.donut_apple_frit,
            R.drawable.donut_bar,
            R.drawable.donut_cinn_roll,
            R.drawable.donut_dozen,
            R.drawable.donut_fancy_cake,
            R.drawable.donut_glaze,
            R.drawable.donut_half_dozen,
            R.drawable.donut_hole_ten,
            R.drawable.donut_jelly,
            R.drawable.donut_twist,
            R.drawable.hot_choc_large,
            R.drawable.hot_choc_small,
            R.drawable.milk_large,
            R.drawable.milk_small,
            R.drawable.muffin,
            R.drawable.orange_juice_large,
            R.drawable.orange_juice_small,
            R.drawable.smoothie,
            R.drawable.soda_can,
            R.drawable.water_large,
            R.drawable.water_small,
            R.drawable.apple_juice,
            R.drawable.monster,
            R.drawable.red_bull,
            R.drawable.snapple,
            R.drawable.snapple_can,
            R.drawable.gatorade,
            R.drawable.lottery,
            R.drawable.scratcher,
            R.drawable.nesquick,
            R.drawable.frappucino,
            R.drawable.scratcher_cash,
            R.drawable.lottery_cash


    };


    public Item() {
        // Generate unique identifier
        mID = UUID.randomUUID();
        mUPC = "";

        //TODO We will need a placeholder icon, an "MD" perhaps?
        mIconCode = R.drawable.donut_glaze;
    }

    public Item(String logEntry) {

        // What did this function do?? -Sann 12/21/14
        if (logEntry.length() < 1) {
            return;
        }

        String itemName, itemQty, itemSubtotal;
        int colonPosition, previousColon;

        Log.d(TAG, logEntry);
        colonPosition = logEntry.indexOf(":");
        previousColon = colonPosition;

        itemName = logEntry.substring(0, colonPosition);

        colonPosition = logEntry.indexOf(":", previousColon + 1);
        itemQty = logEntry.substring(previousColon + 1, colonPosition);

        previousColon = colonPosition;
        colonPosition = logEntry.indexOf(":", previousColon + 1);

        itemSubtotal = logEntry.substring(previousColon + 1);

        int qty = Integer.parseInt(itemQty);

        double subtotal = Double.parseDouble(itemSubtotal);

        mName = itemName;
        mQty = qty;
        mPrice = subtotal / qty;
    }

    public Item(JSONObject json) throws JSONException {
        mID = UUID.fromString(json.getString(JSON_ID));

        if (json.has(JSON_NAME)) {
            mName = json.getString(JSON_NAME);
        }

        if (json.has(JSON_CATEGORY)) {
            mCategory = json.getString(JSON_CATEGORY);
        }

        if (json.has(JSON_PRICE)) {
            mPrice = json.getDouble(JSON_PRICE);
        }

        if (json.has(JSON_TAXABLE)) {
            mTaxable = json.getBoolean(JSON_TAXABLE);
        }

        if (json.has(JSON_ICON)) {
            mIconCode = json.getInt(JSON_ICON);
        }

        if (json.has(JSON_UPC)) {
            mUPC = json.getString(JSON_UPC);
        }
    }

    @Override
    public String toString() {
        return mName;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public double getPrice() {
        return mPrice;
    }

    public void setPrice(double mPrice) {
        this.mPrice = mPrice;
    }

    public UUID getID() {
        return mID;
    }

    public String getCategory() {
        return mCategory;
    }

    public void setCategory(String mCategory) {
        this.mCategory = mCategory;
    }

    public boolean isTaxable() {
        return mTaxable;
    }

    public void setTaxable(boolean mTaxable) {
        this.mTaxable = mTaxable;
    }

    public int getQty() {
        return mQty;
    }

    public void setQty(int mQty) {
        this.mQty = mQty;
    }

    public int getIconCode() {
        return mIconCode;
    }

    public void setIconCode(int mIconCode) {
        this.mIconCode = mIconCode;
    }

    public void setUPC(String UPC) {
        this.mUPC = UPC;
    }

    public String getUPC() {
        return mUPC;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();

        json.put(JSON_ID, mID.toString());
        json.put(JSON_NAME, mName);
        json.put(JSON_CATEGORY, mCategory);
        json.put(JSON_PRICE, String.valueOf(mPrice));
        json.put(JSON_TAXABLE, mTaxable);
        json.put(JSON_QTY, String.valueOf(mQty));
        json.put(JSON_ICON, String.valueOf(mIconCode));
        json.put(JSON_UPC, String.valueOf(mUPC));

        return json;
    }


    public static int getThumbID(int refNumber) {

        int id;

        if (refNumber >= 0 && refNumber < mThumbIds.length) {
            id = mThumbIds[refNumber];
        } else {
            id = mThumbIds[0];
        }

        return id;
    }

    static public Integer[] getThumbIDs() {
        return mThumbIds;
    }
}
