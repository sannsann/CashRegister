package com.stormcloud.cashregister;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by schhan on 12/21/14.
 * <p/>
 * Purpose of this fragment is to display the available icons and assign to an Item.
 */
public class ItemIconPickerFragment extends Fragment {

    ArrayList<Integer> thumbIDs;

    public static final String TAG = "ItemIconPickerFragment";
    public static final String EXTRA_ICON_NUMBER = "com.stormcloud.cashregister.icon_number";

    OnIconSetListener mListener;

    public interface OnIconSetListener{
        public void onIconSet(int iconCode);
    }


    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        thumbIDs = new ArrayList<Integer>();

        for (int i = 0; i < Item.getThumbIDs().length; i++) {
            thumbIDs.add(new Integer(Item.getThumbID(i)));
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnIconSetListener) activity;
        } catch (ClassCastException e) {

            e.printStackTrace();
            throw new ClassCastException(activity.toString()
                    + " must implement OnIconSetListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_items_grid, null);

        GridView gridview = (GridView) view.findViewById(R.id.gridview);
        ImageAdapter adapter = new ImageAdapter(getActivity());
        gridview.setAdapter(adapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(getActivity(), "Selected: " + position, Toast.LENGTH_SHORT)
                        .show();

                Bundle bundle = getArguments();

                if (bundle != null) {
                    bundle.putInt(EXTRA_ICON_NUMBER, position);
//                    getArguments().putAll(bundle);
//                    bundle.put(bundle);

//                    mListener.onIconSet(position);
//                    Log.e(TAG, "bundle is there, we set it now we are using the onIconSet callback");



                }

                // Close iconpickerfragment and go down the stack
                // We added the ItemFragment to the stack before creating iconpickerfragment
                getActivity().getSupportFragmentManager().popBackStack();

            }
        });

        return view;
    }

    public class ImageAdapter extends BaseAdapter {
        private Context mContext;

        public ImageAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            return thumbIDs.size();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater) getActivity()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View gridItem = inflater.inflate(R.layout.inventory_grid_item,
                    parent, false);

            ImageView icon = (ImageView) gridItem
                    .findViewById(R.id.grid_item_icon);
            icon.setImageResource(Item.mThumbIds[position]);

            TextView tvItemName = (TextView) gridItem.findViewById(R.id.grid_item_name);
            tvItemName.setText(String.valueOf(position));

            return gridItem;
        }
    }
}
