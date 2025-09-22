package com.hzc.nonocontroller;

import android.widget.ImageView;
import androidx.databinding.BindingAdapter;

public class BindingAdapters {

    @BindingAdapter("app:connectionStatusIcon")
    public static void setConnectionStatusIcon(ImageView imageView, BlunoLibrary.connectionStateEnum connectionState) {
        if (connectionState == null) {
            imageView.setImageResource(R.drawable.ic_ble_disconnected);
            return;
        }
        switch (connectionState) {
            case isConnected:
                imageView.setImageResource(R.drawable.ic_ble_connected);
                break;
            case isConnecting:
                imageView.setImageResource(R.drawable.ic_ble_scanning);
                break;
            case isToScan:
            case isScanning:
            case isDisconnected:
            default:
                imageView.setImageResource(R.drawable.ic_ble_disconnected);
                break;
        }
    }

    @BindingAdapter("app:batteryIcon")
    public static void setBatteryIcon(ImageView imageView, int batteryLevel) {
        if (batteryLevel > 75) {
            imageView.setImageResource(R.drawable.ic_battery_full);
        } else if (batteryLevel > 50) {
            imageView.setImageResource(R.drawable.ic_battery_75);
        } else if (batteryLevel > 25) {
            imageView.setImageResource(R.drawable.ic_battery_50);
        } else if (batteryLevel > 10) {
            imageView.setImageResource(R.drawable.ic_battery_25);
        } else {
            imageView.setImageResource(R.drawable.ic_battery_alert);
        }
    }
}
