package com.stormcloud.cashregister;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.nio.charset.IllegalCharsetNameException;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Fragment is responsible for creating an Item and adding to the ItemInventory
 * Check for proper input of Item details prior to inclusion to inventory.
 * @author sanncchhan
 */

public class ItemFragment extends Fragment {

    private Item mItem;
    private EditText mNameField;
    private AutoCompleteTextView mAutocompleteTVCategory;
    private EditText mPriceField;
    private EditText mQtyField;
    private Button mUPCButton;
    private Button mFinishedButton;
    private CheckBox mTaxableCheckbox;


    public ImageView mIconImage;
    private Button mPickIconButton;

    public static final String TAG = "ItemFragment";
    //    public static final String EXTRA_MANAGER_REQUEST = "com.stormcloud.cashregister.manager_request";
    public static final String EXTRA_ITEM_ID = "com.stormcloud.cashregister.item_id";
    public static final String EXTRA_ICON_NUMBER = "com.stormcloud.cashregister.icon_number";

    OnItemFinishedListener mItemFinishedListener;

    private Bundle mBundle;


    private String[] ITEM_CATEGORIES;

    public interface OnItemFinishedListener {
        public void onItemFinished(Item item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the item name from the bundle, if there is no item in the bundle, make a new item.
        mBundle = this.getArguments();

        if (mBundle != null) {
            String itemUUID = mBundle.getString(EXTRA_ITEM_ID);

            if (itemUUID != null) {
                mItem = ItemInventory.get(getActivity()).getItem(UUID.fromString(itemUUID));

            } else {
                mItem = new Item();
            }
        } else {
            mItem = new Item();
        }

        // Populate array to be used with AutoCompleteTextView
        ArrayList<String> temp = ItemInventory.get(getActivity()).getCategories();
        ITEM_CATEGORIES = temp.toArray(new String[temp.size()]);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {

            mItemFinishedListener = (OnItemFinishedListener) activity;

        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnItemSelectedListener/OnItemFinishedListener");
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        // Update icon if we selected a new one from ItemIconPickerFragment
        if (mBundle != null) {

            int temp = mBundle.getInt(EXTRA_ICON_NUMBER, -1);
            if (temp >= 0) {
                mItem.setIconCode(temp);
                mIconImage.setImageResource(Item.getThumbID(mItem.getIconCode()));
            }
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_item, parent, false);

        mNameField = (EditText) v.findViewById(R.id.item_name);
        mNameField.setText(mItem.getName());
        mNameField.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(
                    CharSequence c, int start, int before, int count) {

                c = c.toString().replaceAll("[,:]", "");
                Log.e(TAG, "itemName: " + c);
                mItem.setName(c.toString());
            }

            public void beforeTextChanged(
                    CharSequence c, int start, int count, int after) {
                // This space intentionally left blank
            }

            public void afterTextChanged(Editable c) {
                // This space intentionally left blank
            }
        });


        /**
         * AutoCompleteTextView
         */
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, ITEM_CATEGORIES);
        mAutocompleteTVCategory = (AutoCompleteTextView)
                v.findViewById(R.id.item_category_autocomplete);
        mAutocompleteTVCategory.setAdapter(adapter);
        mAutocompleteTVCategory.setText(mItem.getCategory());
        mAutocompleteTVCategory.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(
                    CharSequence c, int start, int before, int count) {

                c = c.toString().replaceAll("[,:]", "");
                Log.e(TAG, "itemCategory: " + c);
                mItem.setCategory(c.toString());
            }

            public void beforeTextChanged(
                    CharSequence c, int start, int count, int after) {
                // This space intentionally left blank
            }

            public void afterTextChanged(Editable c) {
                // This space intentionally left blank
            }
        });


        mPriceField = (EditText) v.findViewById(R.id.item_price);
        mPriceField.setText(String.valueOf(mItem.getPrice()));
        mPriceField.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(
                    CharSequence c, int start, int before, int count) {

                if (c == null || c.length() == 0) {
                    c = "0";
                }

                mItem.setPrice(Double.parseDouble(c.toString()));
            }

            public void beforeTextChanged(
                    CharSequence c, int start, int count, int after) {
                // This space intentionally left blank
            }

            public void afterTextChanged(Editable c) {
                //This one too
            }
        });

        mTaxableCheckbox = (CheckBox) v.findViewById(R.id.item_taxable_checkbox);

        mQtyField = (EditText) v.findViewById(R.id.item_qty);
        mQtyField.setText(String.valueOf(mItem.getQty()));
        mQtyField.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(
                    CharSequence c, int start, int before, int count) {

                if (c == null || c.length() == 0) {
                    c = "0";
                }

                mItem.setQty(Integer.parseInt(c.toString()));
            }

            public void beforeTextChanged(
                    CharSequence c, int start, int count, int after) {
                // This space intentionally left blank
            }

            public void afterTextChanged(Editable c) {
                //This one too
            }
        });

        mUPCButton = (Button) v.findViewById(R.id.item_upc_button);
        mUPCButton.setVisibility(View.INVISIBLE);
        mUPCButton.setEnabled(false);

        mIconImage = (ImageView) v.findViewById(R.id.item_icon_imageview);
        mIconImage.setImageResource(Item.getThumbID(mItem.getIconCode()));

        mPickIconButton = (Button) v.findViewById(R.id.item_icon_pick_button);
        mPickIconButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentManager fm = getActivity().getSupportFragmentManager();

                ItemIconPickerFragment fragment = new ItemIconPickerFragment();
                fragment.setArguments(mBundle);
                fm.beginTransaction().
                        addToBackStack("item").
                        replace(R.id.fragment_1_2, fragment).
                        commit();
            }
        });

        mFinishedButton = (Button) v.findViewById(R.id.item_finish_button);
        mFinishedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Item item = new Item();

                item.setName(mNameField.getText().toString());
                item.setCategory(mAutocompleteTVCategory.getText().toString());
                item.setPrice(Double.parseDouble(mPriceField.getText().toString()));
                item.setTaxable(mTaxableCheckbox.isChecked());
                item.setQty(Integer.parseInt(mQtyField.getText().toString()));

                Log.e(TAG, "item before getting the icon: " + item.toString());

                if (mBundle == null) {
                    Log.e(TAG, "mBundle is null when creating a new item, aborting.");
                    return;
                } else {
                    int temp = mBundle.getInt(EXTRA_ICON_NUMBER, -1);
                    Log.e(TAG, "There is a bundle, we are about to save the item: " + temp);
                    if (temp >= 0) {
                        item.setIconCode(temp);
                    } else {
                        item.setIconCode(8); // Currently the value for the launcher icon
                    }
                }

                // If appropriate fields are filled in, proceed with saving the item
                onItemFinishButtonClick(item);

            }
        });

        return v;
    }

    public void onItemFinishButtonClick(Item item) {

        // Have the activity do the heavy lifting?
        // Can it just be done by the fragment?
        mItemFinishedListener.onItemFinished(item);

    }
}
