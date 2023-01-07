package com.example.ice_datalogger_app.fragments;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.ice_datalogger_app.R;

import java.util.Objects;
import java.util.Set;

public class Bluetooth extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bluetooth, container, false);
    }

    Button buttonOn, buttonOff, buttonShow;
    BluetoothAdapter myBluetoothAdapter;
    ListView listView;

    Intent btEnablingIntent;
    int requestCodeForeEnable;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        buttonOn = (Button) requireView().findViewById(R.id.btOn);
        buttonOff = (Button) requireView().findViewById(R.id.btOff);
        buttonShow = (Button) requireView().findViewById(R.id.btShow);
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        btEnablingIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        requestCodeForeEnable = 1;
        bluetoothOnMethod();
        bluetoothOffMethod();

        listView = (ListView) requireView().findViewById(R.id.listView);
        exeButton();
    }

    private void exeButton() {
        buttonShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(requireActivity().getApplicationContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(requireActivity().getApplicationContext(), "BLUETOOTH_CONNECT permission missing.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Set<BluetoothDevice> btBondedDevices = myBluetoothAdapter.getBondedDevices();
                String[] strings = new String[btBondedDevices.size()];
                int index = 0;
                if(btBondedDevices.size() > 0){
                    for(BluetoothDevice btDevice:btBondedDevices){
                        strings[index] = btDevice.getName();
                        index++;
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(requireActivity().getApplicationContext(), android.R.layout.simple_list_item_1, strings);
                    listView.setAdapter(arrayAdapter);
                }
            }
        });
    }

    private void bluetoothOnMethod() {
        buttonOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(myBluetoothAdapter == null){
                    Toast.makeText(requireActivity().getApplicationContext(), "Bluetooth not supported on this device.", Toast.LENGTH_SHORT).show();
                }
                else {
                    if(!myBluetoothAdapter.isEnabled()){
                        startActivityForResult(btEnablingIntent, requestCodeForeEnable);
                    }
                }
            }
        });
    }

    private void bluetoothOffMethod() {
        buttonOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myBluetoothAdapter.isEnabled()) {
                    if (ActivityCompat.checkSelfPermission(requireActivity().getApplicationContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(requireActivity().getApplicationContext(), "User rejected disabling Bluetooth.", Toast.LENGTH_LONG).show();
                        return;
                    }
                    myBluetoothAdapter.disable();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == requestCodeForeEnable) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(requireActivity().getApplicationContext(), "Bluetooth enabled.", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(requireActivity().getApplicationContext(), "Bluetooth not enabled.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}