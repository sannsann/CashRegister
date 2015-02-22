package com.stormcloud.cashregister;

import java.util.ArrayList;
import java.util.UUID;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * ItemInventory is a singleton class that controls contents of documented items.
 *
 * @author sanncchhan
 */

public class ItemInventory {

    private static final String TAG = "ItemInventory";
    private static final String FILENAME = "items.json";

    private ArrayList<Item> mItems;
    private ShopItemJSONSerializer mSerializer;

    private static ItemInventory sItemInventory;
    private Context mAppContext;

    private ItemInventory(Context appContext) {
        mAppContext = appContext;
        mSerializer = new ShopItemJSONSerializer(mAppContext, FILENAME);


        try {
            mItems = mSerializer.loadItems();
        } catch (Exception e) {
            mItems = new ArrayList<Item>();
            Log.e(TAG, "Error loading crimes: ", e);
        }
    }

    public static ItemInventory get(Context c) {
        if (sItemInventory == null) {
            sItemInventory = new ItemInventory(c.getApplicationContext());
        }
        return sItemInventory;
    }

    public ArrayList<Item> getItems() {
        return mItems;
    }

    public Item getItem(UUID id) {
        for (Item i : mItems) {
            if (i.getID().equals(id))
                return i;
        }

        return null;
    }

    public ArrayList<String> getCategories() {

        ArrayList<String> categories = new ArrayList<String>();

        for (Item i : mItems) {
            if (categories.contains(i.getCategory())) {
                continue;
            } else {
                categories.add(i.getCategory());
            }
        }

        return categories;
    }

    public ArrayList<Item> getItemsWithCategory(String category) {

        ArrayList<Item> filteredItems = new ArrayList<Item>();

        for (Item i : mItems) {
            if (i.getCategory().equals(category)) {
                filteredItems.add(i);
            }
        }

        return filteredItems;
    }

    public boolean saveItems() {
        try {
            mSerializer.saveItems(mItems);
            Log.d(TAG, "items saved to file");
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error saving items: ", e);
            Toast.makeText(mAppContext, "Error saving crimes: " + e.toString(), Toast.LENGTH_LONG)
                    .show();
            return false;
        }
    }

    public void addItem(Item item) {
        mItems.add(item);
    }

    public void deleteItem(int position) { mItems.remove(position); }

    public void reset() {
        mItems.clear();
    }
}

