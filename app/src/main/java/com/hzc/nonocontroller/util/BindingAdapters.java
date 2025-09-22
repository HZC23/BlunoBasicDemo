package com.hzc.nonocontroller.util;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView; // Added import for TextView

import androidx.core.content.ContextCompat;
import androidx.databinding.BindingAdapter;

import com.hzc.nonocontroller.R;
import com.hzc.nonocontroller.data.TelemetryData;
import com.hzc.nonocontroller.BlunoLibrary; // Added import for BlunoLibrary
import com.hzc.nonocontroller.viewmodel.MainViewModel; // Keep if MainViewModel is still used elsewhere

public class BindingAdapters {

    /**
     * Permet de binder un Drawable directement sur app:srcCompat
     */
    @BindingAdapter("app:srcCompat")
    public static void setSrcCompat(ImageView imageView, Drawable drawable) {
        if (drawable != null) {
            imageView.setImageDrawable(drawable);
        }
    }

    /**
     * Change l’icône batterie en fonction du pourcentage
     */
    @BindingAdapter("batteryIcon")
    public static void setBatteryIcon(ImageView imageView, int batteryLevel) { // Changed parameter to int batteryLevel
        int resId;

        if (batteryLevel >= 80) {
            resId = R.drawable.ic_battery_full;
        } else if (batteryLevel >= 50) {
            resId = R.drawable.ic_battery_75; // Changed from ic_battery_half
        } else if (batteryLevel >= 20) {
            resId = R.drawable.ic_battery_25; // Changed from ic_battery_low
        } else {
            resId = R.drawable.ic_battery_alert; // Changed from ic_battery_empty
        }

        imageView.setImageDrawable(ContextCompat.getDrawable(imageView.getContext(), resId));
    }

    /**
     * Affiche une icône selon l’état de connexion
     */
    @BindingAdapter("connectionStatusIcon")
    public static void setConnectionStatusIcon(ImageView imageView, BlunoLibrary.connectionStateEnum state) { // Changed to BlunoLibrary.connectionStateEnum
        int resId;
        if (state == BlunoLibrary.connectionStateEnum.isConnected) { // Changed to BlunoLibrary.connectionStateEnum
            resId = R.drawable.ic_ble_connected; // Using ic_ble_connected as per previous context
        } else if (state == BlunoLibrary.connectionStateEnum.isConnecting) { // Changed to BlunoLibrary.connectionStateEnum
            resId = R.drawable.ic_ble_scanning; // Using ic_ble_scanning as per previous context
        } else {
            resId = R.drawable.ic_ble_disconnected; // Using ic_ble_disconnected as per previous context
        }

        imageView.setImageDrawable(ContextCompat.getDrawable(imageView.getContext(), resId));
    }

    /**
     * Affiche le texte de l'état de connexion
     */
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

    /**
     * Change la couleur de la progressBar en fonction de la distance
     */
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
}