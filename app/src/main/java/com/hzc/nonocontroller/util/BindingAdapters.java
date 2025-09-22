package com.hzc.nonocontroller.util;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.databinding.BindingAdapter;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.hzc.nonocontroller.R;
import com.hzc.nonocontroller.BlunoLibrary;

public class BindingAdapters {

    @BindingAdapter("app:srcCompat")
    public static void setSrcCompat(ImageView imageView, Drawable drawable) {
        if (drawable != null) {
            imageView.setImageDrawable(drawable);
        }
    }

    @BindingAdapter("batteryIcon")
    public static void setBatteryIcon(ImageView imageView, int batteryLevel) {
        int resId;
        if (batteryLevel >= 80) {
            resId = R.drawable.ic_battery_full;
        } else if (batteryLevel >= 50) {
            resId = R.drawable.ic_battery_75;
        } else if (batteryLevel >= 20) {
            resId = R.drawable.ic_battery_25;
        } else {
            resId = R.drawable.ic_battery_alert;
        }
        imageView.setImageDrawable(ContextCompat.getDrawable(imageView.getContext(), resId));
    }

    @BindingAdapter("connectionStatusIcon")
    public static void setConnectionStatusIcon(ImageView imageView, BlunoLibrary.connectionStateEnum state) {
        int resId;
        if (state == BlunoLibrary.connectionStateEnum.isConnected) {
            resId = R.drawable.ic_ble_connected;
        } else if (state == BlunoLibrary.connectionStateEnum.isConnecting) {
            resId = R.drawable.ic_ble_scanning;
        } else {
            resId = R.drawable.ic_ble_disconnected;
        }
        imageView.setImageDrawable(ContextCompat.getDrawable(imageView.getContext(), resId));
    }

    @BindingAdapter("connectionStateText")
    public static void setConnectionStateText(TextView textView, BlunoLibrary.connectionStateEnum state) {
        String text;
        if (state == BlunoLibrary.connectionStateEnum.isConnected) {
            text = textView.getContext().getString(R.string.status_connected);
        } else if (state == BlunoLibrary.connectionStateEnum.isConnecting) {
            text = textView.getContext().getString(R.string.status_connecting);
        } else {
            text = textView.getContext().getString(R.string.status_disconnected);
        }
        textView.setText(text);
    }

    @BindingAdapter("distanceProgressTint")
    public static void setDistanceProgressTint(ProgressBar progressBar, int distance) {
        int colorRes;
        if (distance < 20) {
            colorRes = R.color.red;
        } else if (distance < 50) {
            colorRes = R.color.orange;
        } else {
            colorRes = R.color.green;
        }
        progressBar.setProgressTintList(ContextCompat.getColorStateList(progressBar.getContext(), colorRes));
    }

    @BindingAdapter("android:checkedButton")
    public static void setCheckedButton(MaterialButtonToggleGroup view, int id) {
        if (view.getCheckedButtonId() != id) {
            view.check(id);
        }
    }
}
