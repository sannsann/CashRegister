package com.stormcloud.cashregister;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Created by schhan when this project was conceived.
 * This class has been deprecated in favor of CategoryListFragment as of 11/19/14
 */

public class ItemCategoryListFragment extends ListFragment {

	private static final String TAG = "ItemCategoryListFragment";

	private ArrayList<Item> mItems;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivity().setTitle(R.string.items_title);

		mItems = ItemInventory.get(getActivity()).getItems();

		ArrayAdapter<Item> adapter = new ArrayAdapter<Item>(getActivity(),
				android.R.layout.simple_list_item_1, mItems);

		setListAdapter(adapter);

	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {

		FragmentManager fm = getActivity().getSupportFragmentManager();
		Fragment fragmentInventory = new CategoryListFragment();

		fm.beginTransaction()
				.replace(R.id.fragment_1_3, fragmentInventory)
				.commit();
	}

}
