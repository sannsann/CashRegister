package com.stormcloud.cashregister;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
//import android.app.DialogFragment;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

import java.util.Date;
import java.util.Calendar;

/**
 * Created by schhan on 11/30/14.
 *
 * Used in ReceiptReportFragment
 *
 * Deprecated?
 */
public class DatePickerFragment extends DialogFragment{

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Calendar cal = Calendar.getInstance();
        Date date = new Date();

        cal.setTime(date);

        DatePickerDialog datePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            }
        },
                cal.YEAR, cal.MONTH, cal.DAY_OF_MONTH);

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.date_picker_title)
                .setPositiveButton(android.R.string.ok, null)
                .create();
    }

}
