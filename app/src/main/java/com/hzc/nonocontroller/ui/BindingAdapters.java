package com.hzc.nonocontroller.ui;

import android.graphics.PorterDuff;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.databinding.BindingAdapter;

import com.hzc.nonocontroller.R;

public class BindingAdapters {

    @BindingAdapter("distanceProgressTint")
    public static void setDistanceProgressTint(ProgressBar progressBar, int distance) {
        int color;
        if (distance > 70) {
            color = progressBar.getContext().getResources().getColor(R.color.green);
        } else if (distance > 30) {
            color = progressBar.getContext().getResources().getColor(R.color.orange);
        } else {
            color = progressBar.getContext().getResources().getColor(R.color.red);
        }
        progressBar.setProgressTintList(progressBar.getContext().getResources().getColorStateList(color));
    }

    @BindingAdapter("batteryIcon")
    public static void setBatteryIcon(ImageView imageView, int batteryLevel) {
        int drawableResId;
        if (batteryLevel > 75) {
            drawableResId = R.drawable.ic_battery_full;
        } else if (batteryLevel > 50) {
            drawableResId = R.drawable.ic_battery_75;
        } else if (batteryLevel > 25) {
            drawableResId = R.drawable.ic_battery_50;
        } else if (batteryLevel > 10) {
            drawableResId = R.drawable.ic_battery_25;
        } else {
            drawableResId = R.drawable.ic_battery_alert;
        }
        imageView.setImageResource(drawableResId);
    }

    @BindingAdapter("connectionStatusIcon")
    public static void setConnectionStatusIcon(ImageView imageView, String connectionState) {
        int drawableResId;
        int tintColor;
        if (connectionState == null) {
            drawableResId = R.drawable.ic_ble_disconnected;
            tintColor = R.color.red;
        } else {
            switch (connectionState) {
                case "IS_CONNECTED":
                    drawableResId = R.drawable.ic_ble_connected;
                    tintColor = R.color.green;
                    break;
                case "IS_SCANNING":
                    drawableResId = R.drawable.ic_ble_scanning;
                    tintColor = R.color.accent_electric_blue; // Using an existing blue color
                    break;
                case "IS_CONNECTING":
                    drawableResId = R.drawable.ic_ble_scanning; // Can use scanning icon for connecting
                    tintColor = R.color.accent_electric_blue;
                    break;
                default: // IS_TO_SCAN, IS_DISCONNECTING
                    drawableResId = R.drawable.ic_ble_disconnected;
                    tintColor = R.color.red;
                    break;
            }
        }
        imageView.setImageResource(drawableResId);
        imageView.setColorFilter(imageView.getContext().getResources().getColor(tintColor), PorterDuff.Mode.SRC_IN);
    }
}