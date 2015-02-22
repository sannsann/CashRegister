package com.stormcloud.cashregister;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class CategoryListFragment extends ListFragment {
	
	OnCategorySelectedListener mListener;
	
	private static final String TAG = "CategoryListFragment";

	private ArrayList<String> mCategories;
	
	public interface OnCategorySelectedListener {
		public void onCategorySelected(String category);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mCategories = ItemInventory.get(getActivity()).getCategories();

        CategoryAdapter adapter = new CategoryAdapter(getActivity());

		setListAdapter(adapter);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnCategorySelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnCategorySelectedListener");
		}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {

		String category = getListAdapter().getItem(position).toString();
		
		mListener.onCategorySelected(category);
		
	}

    public class CategoryAdapter extends BaseAdapter {
        private Context mContext;
        public CategoryAdapter(Context c) {
            mContext = c;
        }
        public int getCount() {
            return mCategories.size();
        }
        public Object getItem(int position) {
            return mCategories.get(position);
        }
        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater) getActivity()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = inflater.inflate(R.layout.category_list_item_2,
                    parent, false);

            ImageView image = (ImageView) view.findViewById(R.id.item_category_imageView);
            image.setImageResource(mThumbIds[position]);

            TextView category = (TextView) view.findViewById(R.id.item_category_textView);
            category.setText(mCategories.get(position).toString());

            return view;
        }
    }

    public Integer[] mThumbIds = {
            R.drawable.donut_glaze,
            R.drawable.croissant_plain,
            R.drawable.coffee_large,
            R.drawable.milk_large,
            R.drawable.soda_can,
            R.drawable.lottery,
            R.drawable.lottery_cash,
            R.drawable.scratcher,
            R.drawable.scratcher_cash
    };

}
