package com.example.ice_datalogger_app;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.androidplot.xy.CatmullRomInterpolator;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PanZoom;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.UUID;

import java.util.Arrays;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    //Bluetooth globals
    TextView textView;
    Button buttonOn, buttonOff, buttonShow, buttonScan;
    BluetoothAdapter myBluetoothAdapter;
    ListView listView, scanListView;
    ArrayList<String> stringArrayList = new ArrayList<String>();
    ArrayAdapter<String> arrayAdapter;

    Intent btEnablingIntent;
    int requestCodeForeEnable;

    //Plot globals
    private XYPlot myPlot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 2);
            }
        }

//        // Bluetooth stuff
//        textView = (TextView) findViewById(R.id.textView);
//        Thread2 thread2 = new Thread2();
//        thread2.start();

        buttonOn = (Button) findViewById(R.id.btOn);
        buttonOff = (Button) findViewById(R.id.btOff);
        buttonShow = (Button) findViewById(R.id.btShow);
        buttonScan = (Button) findViewById(R.id.btScan);
        scanListView = (ListView) findViewById(R.id.scanList);
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        btEnablingIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        requestCodeForeEnable = 1;
        bluetoothOnMethod();
        bluetoothOffMethod();
        exeButton();
        bluetoothScanMethod();

        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(myReceiver, intentFilter);

        arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, stringArrayList);
        scanListView.setAdapter(arrayAdapter);
        //listView = (ListView) findViewById(R.id.listView);

        //Plot stuff
        myPlot = (XYPlot) findViewById(R.id.xyplot);
        myPlot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.LEFT).getPaint().setColor(Color.YELLOW);
        myPlot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).getPaint().setColor(Color.YELLOW);
        myPlot.getTitle().getLabelPaint().setColor(Color.rgb(94, 255, 0));
        myPlot.getRangeTitle().getLabelPaint().setColor(Color.rgb(255, 0, 245));
        myPlot.getDomainTitle().getLabelPaint().setColor(Color.rgb(221, 255, 0));

        myPlot.getDomainTitle().getLabelPaint().setTextSize(50f);
        myPlot.getDomainTitle().getLabelPaint().setTextSize(50f);
        myPlot.getTitle().getLabelPaint().setTextSize(50f);

        Number[] series1Numbers = {1, 5, 4, 2, 6, 10, 8, 5, 14, 7};
        //XYSeries series1 = new SimpleXYSeries(Arrays.asList(series1Numbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series 1");
        //XYSeries series1 = new SimpleXYSeries(Arrays.asList(series1Numbers), SimpleXYSeries.ArrayFormat.XY_VALS_INTERLEAVED, "Series 1");

        Number[] xVals = {1, 4, 6, 8, 14, 16, 18, 32, 46, 64};
        Number[] yVals = {5, 2, 10, 5, 20, 10, 40, 20, 80, 40};
        XYSeries series1 = new SimpleXYSeries(Arrays.asList(xVals), Arrays.asList(yVals), "Series 1");

        LineAndPointFormatter series1Format = new LineAndPointFormatter(Color.RED, Color.BLUE, null, null);
        series1Format.setInterpolationParams(new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Uniform));
        series1Format.getLinePaint().setStrokeWidth(10f);
        series1Format.getVertexPaint().setStrokeWidth(30f);

        myPlot.addSeries(series1, series1Format);
        PanZoom.attach(myPlot);
    }

//    private class Thread2 extends Thread {
//        public void run() {
//            for (int i = 0; i < 50; i++) {
//                textView.setText(String.valueOf(i));
//                try {
//                    sleep(500);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }

    BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "BLUETOOTH_CONNECT permission missing.", Toast.LENGTH_SHORT).show();
                    return;
                }
                stringArrayList.add(device.getName());
                arrayAdapter.notifyDataSetChanged();
            }
        }
    };

    private void bluetoothScanMethod() {
        buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "BLUETOOTH_CONNECT permission missing.", Toast.LENGTH_SHORT).show();
                    return;
                }
                myBluetoothAdapter.startDiscovery();

            }
        });
    }
    private void exeButton() {
        buttonShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "BLUETOOTH_CONNECT permission missing.", Toast.LENGTH_SHORT).show();
                    return;
                }
//                Set<BluetoothDevice> btBondedDevices = myBluetoothAdapter.getBondedDevices();
//                String[] strings = new String[btBondedDevices.size()];
//                int index = 0;
//                if(btBondedDevices.size() > 0){
//                    for(BluetoothDevice btDevice:btBondedDevices){
//                        strings[index] = btDevice.getName();
//                        index++;
//                    }
//                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, strings);
//                    listView.setAdapter(arrayAdapter);
//                }
            }
        });
    }

    private void bluetoothOnMethod() {
        buttonOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(myBluetoothAdapter == null){
                    Toast.makeText(getApplicationContext(), "Bluetooth not supported on this device.", Toast.LENGTH_SHORT).show();
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
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(getApplicationContext(), "User rejected disabling Bluetooth.", Toast.LENGTH_LONG).show();
                        return;
                    }
                    myBluetoothAdapter.disable();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == requestCodeForeEnable) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(getApplicationContext(), "Bluetooth enabled.", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "Bluetooth not enabled.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
