package com.stormcloud.cashregister;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ItemGridFragment extends Fragment {

	/**
	 * This class is unfinished and has yet to be tested as of 10/14. Ideally,
	 * the view should displaying pictures using the icons Jenny created with
	 * the name and price displayed below the icon.
	 */

	public static final String TAG = "ItemGridFragment";
	public static final String EXTRA_ITEM_CATEGORY = "com.stormcloud.cashregister.item_category";

	private ArrayList<Item> mFilteredItems;

	private Button mBackButton;

	OnGridItemSelectedListener mListener;

	// Container Activity must implement this interface
	public interface OnGridItemSelectedListener {
		public void onGridItemSelected(Item item);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle bundle = this.getArguments();
		String category = bundle.getString(EXTRA_ITEM_CATEGORY, "NONE");
		mFilteredItems = ItemInventory.get(getActivity()).getItemsWithCategory(
				category);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_items_grid, null);

		mBackButton = (Button) view.findViewById(R.id.button_back);
		mBackButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				FragmentManager fm = getActivity().getSupportFragmentManager();

				Fragment fragmentKeypad = new KeypadFragment();

				fm.beginTransaction()
						.replace(R.id.fragment_3_3, fragmentKeypad)
						.commit();
			}
		});

		GridView gridview = (GridView) view.findViewById(R.id.gridview);
		ImageAdapter adapter = new ImageAdapter(getActivity());
		gridview.setAdapter(adapter);

		gridview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				Item item = mFilteredItems.get(position);

				if (item != null) {
					mListener.onGridItemSelected(item);
				}
			}
		});

		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnGridItemSelectedListener) activity;
		} catch (ClassCastException e) {

			e.printStackTrace();
			throw new ClassCastException(activity.toString()
					+ " must implement OnItemSelectedListener");
		}
	}

	public class ImageAdapter extends BaseAdapter {
		private Context mContext;

		public ImageAdapter(Context c) {
			mContext = c;
		}

		public int getCount() {
			return mFilteredItems.size();
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return 0;
		}

		// create a new ImageView for each item referenced by the Adapter
		public View getView(int position, View convertView, ViewGroup parent) {

			Item item = mFilteredItems.get(position);

			LayoutInflater inflater = (LayoutInflater) getActivity()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			View gridItem = inflater.inflate(R.layout.inventory_grid_item,
					parent, false);

			ImageView icon = (ImageView) gridItem
					.findViewById(R.id.grid_item_icon);

			icon.setImageResource(mThumbIds[item.getIconCode()]);

			TextView tvItemName = (TextView) gridItem.findViewById(R.id.grid_item_name);

            tvItemName.setGravity(Gravity.CENTER);
			tvItemName.setText(item.getName() + "\n" + item.getPrice());

			return gridItem;
		}
		// references to our images
        public Integer[] mThumbIds = {
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
	}
}
