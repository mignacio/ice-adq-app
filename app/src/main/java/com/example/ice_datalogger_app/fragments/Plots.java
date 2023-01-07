package com.example.ice_datalogger_app.fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.androidplot.xy.CatmullRomInterpolator;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PanZoom;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.example.ice_datalogger_app.R;

import java.util.Arrays;
import java.util.Objects;

public class Plots extends Fragment {
    private XYPlot myPlot;

    Button buttonOn, buttonOff, buttonShow;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_plots, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        myPlot = (XYPlot) requireView().findViewById(R.id.xyplot);
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
}