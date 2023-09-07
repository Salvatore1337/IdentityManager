package com.identitymanager.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.identitymanager.R;

public class TrustFragment extends Fragment {

    private static final int REQUEST_ENABLE_BT = 0;
    private static final int REQUEST_DISCOVER_BT = 1;
    private static final int RESULT_OK = -1;
    BluetoothAdapter bluetoothAdapter = null;

    Button trustButton;

    public TrustFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trust, container, false);

        trustButton = view.findViewById(R.id.trust_button);
        trustButton.setOnClickListener(btnView -> {
            this.trust();
        });
        return view;
    }

    public void trust() {

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter != null) {

            if (!bluetoothAdapter.isEnabled()) {//cheking bluetooth status

                if ((ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED)
                        && (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED)
                        && (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED)) { //cheking permission
                    Toast.makeText(this.getContext(), "Bluetooth permissions denied", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    Toast.makeText(this.getContext(), "Enabling bluetooth", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(bluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intent, REQUEST_ENABLE_BT);
                }
            }

            //bluetooth scan permissions for sdk 29+
            if(ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                
            }
            if (!bluetoothAdapter.isDiscovering()) {
                Toast.makeText(this.getContext(), "Making your device discoverable", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(bluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                startActivityForResult(intent, REQUEST_DISCOVER_BT);
            }

            //start searching for devices
            bluetoothAdapter.startDiscovery();
            Toast.makeText(this.getContext(), "Searching for devices", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(this.getContext(), "Bluetooth not available", Toast.LENGTH_SHORT).show();
        }
    }

    public void getTrusted(View view) {

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter != null) {

            if (!bluetoothAdapter.isEnabled()) {//cheking bluetooth status

                if ((ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED)
                        && (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED)
                        && (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED)) { //cheking permission
                    Toast.makeText(this.getContext(), "Bluetooth permissions denied", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    Toast.makeText(this.getContext(), "Enabling bluetooth", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(bluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intent, REQUEST_ENABLE_BT);
                }
            }

            //bluetooth discoverability
            if (!bluetoothAdapter.isDiscovering()) {
                Toast.makeText(this.getContext(), "Making your device discoverable", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(bluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                startActivityForResult(intent, REQUEST_DISCOVER_BT);
            }
        } else {
            Toast.makeText(this.getContext(), "Bluetooth not available", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == RESULT_OK) {
                    //bluetooth is on
                    Toast.makeText(this.getContext(), "Bluetooth enabled", Toast.LENGTH_SHORT).show();
                } else {
                    //user denied bluetooth permission
                    Toast.makeText(this.getContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
            case REQUEST_DISCOVER_BT:
                if (resultCode == RESULT_OK) {
                    Toast.makeText(this.getContext(), "Device is discoverable", Toast.LENGTH_SHORT).show();
                } else {
                    //user denied bluetooth permission
                    Toast.makeText(this.getContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void showBluetoothDevicesDialog() {
    }

    public void createNewTrustedDialog(){

        Fragment trustFragment = new TrustFragment();
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, trustFragment).commit();
//
//        dialogBuilder = new AlertDialog.Builder(getActivity());
//        final View newTrusted_popupView = getLayoutInflater().inflate(R.layout.new_trusted_popup, null);
//        editTextNickname_newTrusted = newTrusted_popupView.findViewById(R.id.editTextNickname_newTrusted);
//        editTextEmail_newTrusted = newTrusted_popupView.findViewById(R.id.editTextEmail_newTrusted);
//
//        buttonAdd_newTrusted = newTrusted_popupView.findViewById(R.id.buttonAdd_newTrusted);
//        buttonCancel_newTrusted =  newTrusted_popupView.findViewById(R.id.buttonCancel_newTrusted);
//
//        dialogBuilder.setView(newTrusted_popupView);
//        dialog = dialogBuilder.create();
//        dialog.show();
//
//        // activate bluetooth if not active
//        bluetoothManager = getActivity().getSystemService(BluetoothManager.class);
//
//        System.out.println(bluetoothManager.toString());
//
//        bluetoothAdapter = bluetoothManager.getAdapter();
//
//        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
//            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//        }
//
//        // searching for nearby devices
//
//        // click on the device to connect
//
//        // send a trust request to connected device
//
//        // wait for a response
//
//        // if response is OK save the trust into db
    }
}