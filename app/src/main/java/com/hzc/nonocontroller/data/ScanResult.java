package com.hzc.nonocontroller.data;

import android.bluetooth.BluetoothDevice;

public class ScanResult {
    public final BluetoothDevice device;
    public final int rssi;

    public ScanResult(BluetoothDevice device, int rssi) {
        this.device = device;
        this.rssi = rssi;
    }

    public String getDeviceName() {
        return device.getName() == null ? "Unknown Device" : device.getName();
    }

    public String getDeviceAddress() {
        return device.getAddress();
    }

    public String getRssiString() {
        return rssi + " dBm";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScanResult that = (ScanResult) o;
        return device.getAddress().equals(that.device.getAddress());
    }

    @Override
    public int hashCode() {
        return device.getAddress().hashCode();
    }
}
