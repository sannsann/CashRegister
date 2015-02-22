package com.stormcloud.cashregister;

import java.text.DecimalFormat;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * KeypadFragment was designed to handle input of digits regarding cash tendered in addition to
 * being able to log items that are not in ItemInventory by inputing a price and selecting a category
 * to log a generic Item with selected category.
 * @author schhan
 */
public class KeypadFragment extends Fragment {

    private static final String TAG = "KeypadFragment";
    private TextView mDisplayTextView;

    private String displayValue = "";

    OnTenderedAmountSetListener mTenderedListener;
    OnItemsClearedListener mClearedListener;

    // Container Activity must implement this interface
    public interface OnTenderedAmountSetListener {
        public void onTenderedSet();
    }

    // Container Activity must implement this interface
    public interface OnItemsClearedListener {
        public void onItemsCleared();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.fragment_keypad, parent, false);

        v.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        final int widthMeasured = v.getMeasuredWidth();
        final int heightMeasured = v.getMeasuredHeight();

        int widthButton = (widthMeasured * 10) / 14;
        int heightButton = heightMeasured / 5;

        mDisplayTextView = (TextView) v.findViewById(R.id.keypad_display);
        mDisplayTextView.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

                // If the display is empty, set it to 0.00
                if (s.length() == 0) {
                    mDisplayTextView.setText("0.00");
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        final Button btnKeypad1 = (Button) v.findViewById(R.id.keypad1);
        btnKeypad1.setWidth(widthButton);
        btnKeypad1.setHeight(heightButton);
        btnKeypad1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                displayValue += "1";
                mDisplayTextView.setText(displayValue);
            }
        });

        Button btnKeypad2 = (Button) v.findViewById(R.id.keypad2);
        btnKeypad2.setWidth(widthButton);
        btnKeypad2.setHeight(heightButton);
        btnKeypad2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                displayValue += "2";
                mDisplayTextView.setText(displayValue);
            }
        });

        Button btnKeypad3 = (Button) v.findViewById(R.id.keypad3);
        btnKeypad3.setWidth(widthButton);
        btnKeypad3.setHeight(heightButton);
        btnKeypad3.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                displayValue += "3";
                mDisplayTextView.setText(displayValue);
            }
        });

        Button btnKeypad4 = (Button) v.findViewById(R.id.keypad4);
        btnKeypad4.setWidth(widthButton);
        btnKeypad4.setHeight(heightButton);
        btnKeypad4.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                displayValue += "4";
                mDisplayTextView.setText(displayValue);
            }
        });

        Button btnKeypad5 = (Button) v.findViewById(R.id.keypad5);
        btnKeypad5.setWidth(widthButton);
        btnKeypad5.setHeight(heightButton);
        btnKeypad5.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                displayValue += "5";
                mDisplayTextView.setText(displayValue);
            }
        });

        Button btnKeypad6 = (Button) v.findViewById(R.id.keypad6);
        btnKeypad6.setWidth(widthButton);
        btnKeypad6.setHeight(heightButton);
        btnKeypad6.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                displayValue += "6";
                mDisplayTextView.setText(displayValue);
            }
        });

        Button btnKeypad7 = (Button) v.findViewById(R.id.keypad7);
        btnKeypad7.setWidth(widthButton);
        btnKeypad7.setHeight(heightButton);
        btnKeypad7.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                displayValue += "7";
                mDisplayTextView.setText(displayValue);
            }
        });

        Button btnKeypad8 = (Button) v.findViewById(R.id.keypad8);
        btnKeypad8.setWidth(widthButton);
        btnKeypad8.setHeight(heightButton);
        btnKeypad8.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                displayValue += "8";
                mDisplayTextView.setText(displayValue);
            }
        });

        Button btnKeypad9 = (Button) v.findViewById(R.id.keypad9);
        btnKeypad9.setWidth(widthButton);
        btnKeypad9.setHeight(heightButton);
        btnKeypad9.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                displayValue += "9";
                mDisplayTextView.setText(displayValue);
            }
        });

        Button btnKeypad0 = (Button) v.findViewById(R.id.keypad0);
        btnKeypad0.setWidth(widthButton);
        btnKeypad0.setHeight(heightButton);
        btnKeypad0.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                displayValue += "0";
                mDisplayTextView.setText(displayValue);
            }
        });

        Button btnKeypad00 = (Button) v.findViewById(R.id.keypad00);
        btnKeypad00.setWidth(widthButton);
        btnKeypad00.setHeight(heightButton);
        btnKeypad00.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                displayValue += "00";
                mDisplayTextView.setText(displayValue);
            }
        });

        Button btnKeypadDot = (Button) v.findViewById(R.id.keypaddot);
        btnKeypadDot.setWidth(widthButton);
        btnKeypadDot.setHeight(heightButton);
        btnKeypadDot.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (!displayValue.contains(".")) {
                    displayValue += ".";
                    mDisplayTextView.setText(displayValue);
                }

            }
        });

        Button btnKeypadEnter = (Button) v.findViewById(R.id.keypad_enter);
        btnKeypadEnter.setWidth(widthButton);
        btnKeypadEnter.setHeight(heightButton);
        btnKeypadEnter.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                // Make sure the contents of displayValue isn't empty or a
                // single decimal point
                if (displayValue.length() == 0 || displayValue.equals(".")) {
                    return;
                }

                DecimalFormat myFormatter = new DecimalFormat("0.00");
                String output = myFormatter.format(Double
                        .parseDouble(displayValue));
                // tvTendered.setText(output);

                CustomerReceipt.get(getActivity()).setTendered(
                        Double.parseDouble(output));

                mTenderedListener.onTenderedSet();

            }
        });

        Button btnKeypadClear = (Button) v.findViewById(R.id.keypad_clear);
        btnKeypadClear.setWidth(widthButton * 2);
        btnKeypadClear.setHeight(heightButton);
        btnKeypadClear.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                displayValue = "";
                mDisplayTextView.setText(displayValue);
            }

        });

        btnKeypadClear.setLongClickable(true);
        btnKeypadClear.setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {

                CustomerReceipt.get(getActivity()).clearItems();

                mClearedListener.onItemsCleared();

                return true;
            }
        });

        Button keypad1Dollar = (Button)v.findViewById(R.id.keypad_1_dollar);
        keypad1Dollar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                displayValue = "1";
                mDisplayTextView.setText(displayValue);

                DecimalFormat myFormatter = new DecimalFormat("0.00");
                String output = myFormatter.format(Double
                        .parseDouble(displayValue));
                // tvTendered.setText(output);

                CustomerReceipt.get(getActivity()).setTendered(
                        Double.parseDouble(output));

                mTenderedListener.onTenderedSet();

            }
        });

        Button keypad5Dollar = (Button) v.findViewById(R.id.keypad_5_dollar);
        keypad5Dollar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                displayValue = "5";
                mDisplayTextView.setText(displayValue);

                DecimalFormat myFormatter = new DecimalFormat("0.00");
                String output = myFormatter.format(Double
                        .parseDouble(displayValue));
                // tvTendered.setText(output);

                CustomerReceipt.get(getActivity()).setTendered(
                        Double.parseDouble(output));

                mTenderedListener.onTenderedSet();
            }
        });

        Button keypad10Dollar = (Button) v.findViewById(R.id.keypad_10_dollar);
        keypad10Dollar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                displayValue = "10";
                mDisplayTextView.setText(displayValue);

                DecimalFormat myFormatter = new DecimalFormat("0.00");
                String output = myFormatter.format(Double
                        .parseDouble(displayValue));
                // tvTendered.setText(output);

                CustomerReceipt.get(getActivity()).setTendered(
                        Double.parseDouble(output));

                mTenderedListener.onTenderedSet();
            }
        });

        Button keypad20Dollar = (Button) v.findViewById(R.id.keypad_20_dollar);
        keypad20Dollar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                displayValue = "20";
                mDisplayTextView.setText(displayValue);

                DecimalFormat myFormatter = new DecimalFormat("0.00");
                String output = myFormatter.format(Double
                        .parseDouble(displayValue));
                // tvTendered.setText(output);

                CustomerReceipt.get(getActivity()).setTendered(
                        Double.parseDouble(output));

                mTenderedListener.onTenderedSet();
            }
        });

        Button keypad50Dollar = (Button) v.findViewById(R.id.keypad_50_dollar);
        keypad50Dollar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                displayValue = "50";
                mDisplayTextView.setText(displayValue);

                DecimalFormat myFormatter = new DecimalFormat("0.00");
                String output = myFormatter.format(Double
                        .parseDouble(displayValue));
                // tvTendered.setText(output);

                CustomerReceipt.get(getActivity()).setTendered(
                        Double.parseDouble(output));

                mTenderedListener.onTenderedSet();
            }
        });

        Button keypad100Dollar = (Button) v.findViewById(R.id.keypad_100_dollar);
        keypad100Dollar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                displayValue = "100";
                mDisplayTextView.setText(displayValue);

                DecimalFormat myFormatter = new DecimalFormat("0.00");
                String output = myFormatter.format(Double
                        .parseDouble(displayValue));
                // tvTendered.setText(output);

                CustomerReceipt.get(getActivity()).setTendered(
                        Double.parseDouble(output));

                mTenderedListener.onTenderedSet();
            }
        });

        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mTenderedListener = (OnTenderedAmountSetListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnTenderedAmountSetListener");
        }

        try {
            mClearedListener = (OnItemsClearedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnItemsClearedListener");
        }
    }

    public String getDisplayValue() {

        return displayValue;

    }

    public void clearDisplayValue() {

        displayValue = "";
        mDisplayTextView.setText("0.00");

    }
}
