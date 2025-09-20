package com.hzc23.nonocontroller.data;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;

import java.util.ArrayList;

public class BleManager {

    private Context context;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeService bluetoothLeService;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> gattCharacteristics = new ArrayList<>();
    private boolean scanning = false;
    private String deviceName;
    private String deviceAddress;
    public enum ConnectionState {NULL, IS_SCANNING, IS_TO_SCAN, IS_CONNECTING, IS_CONNECTED, IS_DISCONNECTING}
    public ConnectionState connectionState = ConnectionState.NULL;

    private static final String TAG = BleManager.class.getSimpleName();

    public static final String SERIAL_PORT_UUID = "0000dfb1-0000-1000-8000-00805f9b34fb";
    public static final String COMMAND_UUID = "0000dfb2-0000-1000-8000-00805f9b34fb";
    public static final String MODEL_NUMBER_STRING_UUID = "00002a24-0000-1000-8000-00805f9b34fb";


    public BleManager(Context context) {
        this.context = context;
    }

    public boolean initiate() {
        // Use this check to determine whether BLE is supported on the device.
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            return false;
        }

        // Initializes a Bluetooth adapter.
        final BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (bluetoothAdapter == null) {
            return false;
        }

        return true;
    }
}