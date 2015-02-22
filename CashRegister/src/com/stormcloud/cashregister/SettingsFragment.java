package com.stormcloud.cashregister;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.starmicronics.stario.PortInfo;
import com.starmicronics.stario.StarIOPort;
import com.starmicronics.stario.StarIOPortException;
import com.starmicronics.stario.StarPrinterStatus;

import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends Fragment {

    EditText etHeadlineName, etHeadlineAddress1, etHeadlineAddress2, etHeadlinePhoneNum;

    Button btnDiscoverPrinter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_settings, parent, false);

        SharedPreferences preferences = this.getActivity()
                .getSharedPreferences("pref", Context.MODE_PRIVATE);

        String headlineName = preferences.getString("BusinessName", "");
        String headlineAddress1 = preferences.getString("BusinessAddress1", "");
        String headlineAddress2 = preferences.getString("BusinessAddress2", "");
        String headlinePhoneNum = preferences.getString("BusinessPhoneNum", "");

        etHeadlineName = (EditText) v.findViewById(R.id.settings_name);
        etHeadlineAddress1 = (EditText) v.findViewById(R.id.settings_address1);
        etHeadlineAddress2 = (EditText) v.findViewById(R.id.settings_address2);
        etHeadlinePhoneNum = (EditText) v.findViewById(R.id.settings_phoneNum);

        etHeadlineName.setText(headlineName);
        etHeadlineAddress1.setText(headlineAddress1);
        etHeadlineAddress2.setText(headlineAddress2);
        etHeadlinePhoneNum.setText(headlinePhoneNum);

        btnDiscoverPrinter = (Button) v.findViewById(R.id.settings_btn_discoverPrinter);
        btnDiscoverPrinter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPortDiscovery("Bluetooth");

//                PrintSampleReceipt(getActivity(), "BT:Star Micronics", "");
            }


        });

        return v;
    }

    @Override
    public void onStop() {

        super.onStop();

        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context

        SharedPreferences preferences = this.getActivity()
                .getSharedPreferences("pref", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();

        editor.putString("BusinessName", etHeadlineName.getText().toString());
        editor.putString("BusinessAddress1", etHeadlineAddress1.getText().toString());
        editor.putString("BusinessAddress2", etHeadlineAddress2.getText().toString());
        editor.putString("BusinessPhoneNum", etHeadlinePhoneNum.getText().toString());

        // Commit the edits!
        editor.commit();
    }

    private void getPortDiscovery(String interfaceName) {
        List<PortInfo> BTPortList;
        List<PortInfo> TCPPortList;
        final EditText editPortName;

        final ArrayList<PortInfo> arrayDiscovery;
        ArrayList<String> arrayPortName;

        arrayDiscovery = new ArrayList<PortInfo>();
        arrayPortName = new ArrayList<String>();

        try {
            if (true == interfaceName.equals("Bluetooth") || true == interfaceName.equals("All")) {
                BTPortList = StarIOPort.searchPrinter("BT:");

                for (PortInfo portInfo : BTPortList) {
                    arrayDiscovery.add(portInfo);
                }
            }
            if (true == interfaceName.equals("LAN") || true == interfaceName.equals("All")) {
                TCPPortList = StarIOPort.searchPrinter("TCP:");

                for (PortInfo portInfo : TCPPortList) {
                    arrayDiscovery.add(portInfo);
                }
            }

            arrayPortName = new ArrayList<String>();

            for (PortInfo discovery : arrayDiscovery) {
                String portName;

                portName = discovery.getPortName();

                if (discovery.getMacAddress().equals("") == false) {
                    portName += "\n - " + discovery.getMacAddress();
                    if (discovery.getModelName().equals("") == false) {
                        portName += "\n - " + discovery.getModelName();
                    }
                }

                arrayPortName.add(portName);
            }
        } catch (StarIOPortException e) {
            e.printStackTrace();
        }

        editPortName = new EditText(getActivity());

        new AlertDialog.Builder(getActivity())
                .setIcon(android.R.drawable.checkbox_on_background)
                .setTitle("Please Select IP Address or Input Port Name")
                .setCancelable(false).setView(editPortName)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int button) {
//                ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
//                ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(false);
//                EditText portNameField = (EditText) findViewById(R.id.editText_PortName);
//                portNameField.setText(editPortName.getText());
//                SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
//                SharedPreferences.Editor editor = pref.edit();
//                editor.putString("portName", portNameField.getText().toString());
//                editor.commit();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int button) {
            }
        }).setItems(arrayPortName.toArray(new String[0]), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int select) {
//                EditText portNameField = (EditText) findViewById(R.id.editText_PortName);
//                portNameField.setText(arrayDiscovery.get(select).getPortName());

                String portName = arrayDiscovery.get(select).getPortName();

                SharedPreferences pref = getActivity().getSharedPreferences("pref",
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("portName", portName);


                editor.commit();
            }
        }).show();
    }

}
