package com.example.ice_datalogger_app;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.androidplot.xy.CatmullRomInterpolator;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PanZoom;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.google.android.material.tabs.TabLayout;

import java.util.Arrays;
import java.util.Set;

public class MainActivity extends AppCompatActivity{

    //Bluetooth globals
    Button buttonOn, buttonOff, buttonShow;
    BluetoothAdapter myBluetoothAdapter;
    ListView listView;

    Intent btEnablingIntent;
    int requestCodeForeEnable;

    //Plot globals
    private XYPlot myPlot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 2);
            }
        }

        // Bluetooth stuff
        buttonOn = (Button) findViewById(R.id.btOn);
        buttonOff = (Button) findViewById(R.id.btOff);
        buttonShow = (Button) findViewById(R.id.btShow);
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        btEnablingIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        requestCodeForeEnable = 1;
        bluetoothOnMethod();
        bluetoothOffMethod();

        listView = (ListView) findViewById(R.id.listView);
        exeButton();

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

    private void exeButton() {
        buttonShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "BLUETOOTH_CONNECT permission missing.", Toast.LENGTH_SHORT).show();
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
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, strings);
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
