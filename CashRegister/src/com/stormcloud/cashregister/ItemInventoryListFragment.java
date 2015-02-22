package com.stormcloud.cashregister;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ItemInventoryListFragment extends ListFragment {

    public static final String EXTRA_ITEM_CATEGORY = "com.stormcloud.cashregister.item_category";

    private ArrayList<Item> mAllItems;

    private ListAdapter mListAdapter;
    OnItemSelectedListener mListener;


    // Container Activity must implement this interface
    public interface OnItemSelectedListener {
        public void onItemSelected(Item item);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAllItems = ItemInventory.get(getActivity()).getItems();

        ItemAdapter adapter = new ItemAdapter(mAllItems);

        setListAdapter(adapter);
        adapter.notifyDataSetChanged();
        setHasOptionsMenu(true);

        // Experimental, 2/4/15
        getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_inventory_list, null);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ListView lv = getListView();

        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        lv.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                //Required but not used in this implementation
            }

            // ActionMode.Callback methods
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.inventory_list_item_context, menu);
                return true; // What is true? What is false?
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_item_delete_item:
                        ArrayAdapter<Item> adapter = (ArrayAdapter<Item>) getListAdapter();
                        ItemInventory itemInventory = ItemInventory.get(getActivity());

                        for (int i = adapter.getCount() - 1; i >= 0; i--) {
                            if (getListView().isItemChecked(i)) {
                                itemInventory.deleteItem(i);
                            }
                        }

                        mode.finish();
                        adapter.notifyDataSetChanged();

                        ItemInventory.get(getActivity()).saveItems();

                        return true;
                    default:
                        return false;

                }
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                // Required, but not used in this implementation.

            }
        });

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_inventory_fragment, menu);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnItemSelectedListener) activity;

        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnItemSelectedListener/OnItemFinishedListener");
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        Item c = ((ItemAdapter)getListAdapter()).getItem(position);

        if (c != null) {
            mListener.onItemSelected(c);

            // Must notify the ListView of update to adapter items.
            ListView lv = getListView();

            ItemAdapter adapter = new ItemAdapter(mAllItems);
            setListAdapter(adapter);
            lv.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }

    private class ItemAdapter extends ArrayAdapter<Item> {

        public ItemAdapter(ArrayList<Item> items) {
            super(getActivity(), 0, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // If we weren't given a view, inflate one
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater()
                        .inflate(R.layout.list_item_inventory_item, null);
            }

            // Configure the view for this Item
            Item item = getItem(position);
            TextView tvName = (TextView) convertView.findViewById(R.id.item_list_item_nameTextView);
            tvName.setText(item.getName());

            return convertView;
        }

    }
}
