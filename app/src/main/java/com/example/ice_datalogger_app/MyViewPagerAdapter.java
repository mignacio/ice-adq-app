package com.example.ice_datalogger_app;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.androidplot.Plot;
import com.example.ice_datalogger_app.fragments.Bluetooth;
import com.example.ice_datalogger_app.fragments.Plots;

public class MyViewPagerAdapter extends FragmentStateAdapter {
    public MyViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
            default:
                return new Plots();
            case 1:
                return new Bluetooth();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
