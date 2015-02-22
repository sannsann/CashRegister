package com.stormcloud.cashregister;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by schhan on 11/21/14.
 *
 * Fragment displays instructions in ManagerActivity when Open Inventory action item is selected.
 *
 */

public class ItemEmptyFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup parent,
                 Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_item_empty, parent, false);

        return v;
    }

}
