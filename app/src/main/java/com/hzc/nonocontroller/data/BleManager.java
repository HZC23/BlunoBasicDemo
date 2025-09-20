package com.hzc.nonocontroller.data;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.hzc.nonocontroller.BluetoothLeService;
import com.hzc.nonocontroller.R;

import java.util.ArrayList;
import java.util.List;

public class BleManager {

    private Context context;
    private Activity activity;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeService bluetoothLeService;
    private boolean scanning = false;
    public enum ConnectionState {NULL, IS_SCANNING, IS_TO_SCAN, IS_CONNECTING, IS_CONNECTED, IS_DISCONNECTING}
    public ConnectionState connectionState = ConnectionState.NULL;

    private LeDeviceListAdapter leDeviceListAdapter;
    private AlertDialog scanDeviceDialog;
    private Handler handler = new Handler();

    private String deviceName;
    private String deviceAddress;

    private BleManagerListener listener;

    private BluetoothGattCharacteristic sCharacteristic, modelNumberCharacteristic, serialPortCharacteristic, commandCharacteristic;

    public static final String SERIAL_PORT_UUID = "0000dfb1-0000-1000-8000-00805f9b34fb";
    public static final String COMMAND_UUID = "0000dfb2-0000-1000-8000-00805f9b34fb";
    public static final String MODEL_NUMBER_STRING_UUID = "00002a24-0000-1000-8000-00805f9b34fb";


    public interface BleManagerListener {
        void onConnectionStateChange(ConnectionState connectionState);
        void onSerialReceived(String data);
        void onServicesDiscovered();
    }

    public void setListener(BleManagerListener listener) {
        this.listener = listener;
    }

    private static final String TAG = BleManager.class.getSimpleName();

    public BleManager(Activity activity) {
        this.activity = activity;
        this.context = activity.getApplicationContext();
    }

    public boolean initiate() {
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            return false;
        }
        final BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null) {
            return false;
        }
        return true;
    }

    public void registerReceiver() {
        context.registerReceiver(gattUpdateReceiver, makeGattUpdateIntentFilter());
    }

    public void unregisterReceiver() {
        context.unregisterReceiver(gattUpdateReceiver);
    }

    public void scanLeDevice(final boolean enable) {
        if (enable) {
            if (leDeviceListAdapter == null) {
                leDeviceListAdapter = new LeDeviceListAdapter();
                scanDeviceDialog = new AlertDialog.Builder(activity)
                        .setTitle("BLE Device Scan...")
                        .setAdapter(leDeviceListAdapter, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final BluetoothDevice device = leDeviceListAdapter.getDevice(which);
                                if (device == null) return;
                                scanLeDevice(false);
                                connect(device.getAddress());
                            }
                        })
                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                scanLeDevice(false);
                            }
                        }).create();
            }
            leDeviceListAdapter.clear();
            leDeviceListAdapter.notifyDataSetChanged();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    scanning = false;
                    bluetoothAdapter.stopLeScan(leScanCallback);
                    scanDeviceDialog.dismiss();
                }
            }, 10000); // Stops scanning after a pre-defined scan period.
            scanning = true;
            bluetoothAdapter.startLeScan(leScanCallback);
            scanDeviceDialog.show();
        } else {
            scanning = false;
            bluetoothAdapter.stopLeScan(leScanCallback);
            if(scanDeviceDialog != null && scanDeviceDialog.isShowing()) {
                scanDeviceDialog.dismiss();
            }
        }
    }

    public boolean connect(final String address) {
        if (bluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        // Previously connected device.  Try to reconnect.
        if (deviceAddress != null && address.equals(deviceAddress)
                && bluetoothLeService != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (bluetoothLeService.connect(address)) {
                connectionState = ConnectionState.IS_CONNECTING;
                return true;
            } else {
                return false;
            }
        }

        final BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        Intent gattServiceIntent = new Intent(context, BluetoothLeService.class);
        context.bindService(gattServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE);

        deviceAddress = address;
        connectionState = ConnectionState.IS_CONNECTING;
        return true;
    }

    public void disconnect() {
        if (bluetoothAdapter == null || bluetoothLeService == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        bluetoothLeService.disconnect();
    }

    public void close() {
        if (bluetoothLeService == null) {
            return;
        }
        context.unbindService(serviceConnection);
        bluetoothLeService.close();
        bluetoothLeService = null;
    }


    private final ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            bluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!bluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
            }
            // Automatically connects to the device upon successful start-up initialization.
            bluetoothLeService.connect(deviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bluetoothLeService = null;
        }
    };

    private final BroadcastReceiver gattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                connectionState = ConnectionState.IS_CONNECTED;
                if (listener != null) {
                    listener.onConnectionStateChange(connectionState);
                }
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                connectionState = ConnectionState.IS_TO_SCAN;
                if (listener != null) {
                    listener.onConnectionStateChange(connectionState);
                }
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                if (listener != null) {
                    listener.onServicesDiscovered();
                }
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                if (sCharacteristic == modelNumberCharacteristic) {
                    if (intent.getStringExtra(BluetoothLeService.EXTRA_DATA).toUpperCase().startsWith("DF BLUNO")) {
                        setCharacteristicNotification(sCharacteristic, false);
                        sCharacteristic = commandCharacteristic;
                        sCharacteristic.setValue("AT+PASSWOR=DFRobot\r\n");
                        writeCharacteristic(sCharacteristic);
                        sCharacteristic.setValue("AT+CURRUART=115200\r\n");
                        writeCharacteristic(sCharacteristic);
                        sCharacteristic = serialPortCharacteristic;
                        setCharacteristicNotification(sCharacteristic, true);
                        connectionState = ConnectionState.IS_CONNECTED;
                        if (listener != null) {
                            listener.onConnectionStateChange(connectionState);
                        }
                    } else {
                        Toast.makeText(context, "Please select DFRobot devices", Toast.LENGTH_SHORT).show();
                        connectionState = ConnectionState.IS_TO_SCAN;
                        if (listener != null) {
                            listener.onConnectionStateChange(connectionState);
                        }
                    }
                } else if (sCharacteristic == serialPortCharacteristic) {
                    if (listener != null) {
                        listener.onSerialReceived(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
                    }
                }
            }
        }
    };

    public void onServicesDiscovered() {
        // Show all the supported services and characteristics on the user interface.
        for (BluetoothGattService gattService : bluetoothLeService.getSupportedGattServices()) {
            Log.i(TAG, "Service Uuid: " + gattService.getUuid().toString());
        }
        getGattServices(bluetoothLeService.getSupportedGattServices());
    }

    private void getGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;
        modelNumberCharacteristic=null;
        serialPortCharacteristic=null;
        commandCharacteristic=null;

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            uuid = gattService.getUuid().toString();
            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();

            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                uuid = gattCharacteristic.getUuid().toString();
                if(uuid.equals(MODEL_NUMBER_STRING_UUID)){
                    modelNumberCharacteristic=gattCharacteristic;
                }
                else if(uuid.equals(SERIAL_PORT_UUID)){
                    serialPortCharacteristic = gattCharacteristic;
                }
                else if(uuid.equals(COMMAND_UUID)){
                    commandCharacteristic = gattCharacteristic;
                }
            }
        }

        if (modelNumberCharacteristic==null || serialPortCharacteristic==null || commandCharacteristic==null) {
            Toast.makeText(context, "Please select DFRobot devices",Toast.LENGTH_SHORT).show();
            connectionState = ConnectionState.IS_TO_SCAN;
            if (listener != null) {
                listener.onConnectionStateChange(connectionState);
            }
        }
        else {
            sCharacteristic=modelNumberCharacteristic;
            setCharacteristicNotification(sCharacteristic, true);
            readCharacteristic(sCharacteristic);
        }
    }

    public void sendCommand(String command) {
        if (commandCharacteristic != null) {
            commandCharacteristic.setValue(command);
            writeCharacteristic(commandCharacteristic);
        }
    }

    public void writeCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (bluetoothAdapter == null || bluetoothLeService == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        bluetoothLeService.writeCharacteristic(characteristic);
    }

    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enabled) {
        if (bluetoothAdapter == null || bluetoothLeService == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        bluetoothLeService.setCharacteristicNotification(characteristic, enabled);
    }

    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (bluetoothAdapter == null || bluetoothLeService == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        bluetoothLeService.readCharacteristic(characteristic);
    }


    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }


    private BluetoothAdapter.LeScanCallback leScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi,
                                     byte[] scanRecord) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            leDeviceListAdapter.addDevice(device);
                            leDeviceListAdapter.notifyDataSetChanged();
                        }
                    });
                }
            };


    private class LeDeviceListAdapter extends BaseAdapter {
        private ArrayList<BluetoothDevice> leDevices;
        private LayoutInflater inflator;

        public LeDeviceListAdapter() {
            super();
            leDevices = new ArrayList<BluetoothDevice>();
            inflator = activity.getLayoutInflater();
        }

        public void addDevice(BluetoothDevice device) {
            if (!leDevices.contains(device)) {
                leDevices.add(device);
            }
        }

        public BluetoothDevice getDevice(int position) {
            return leDevices.get(position);
        }

        public void clear() {
            leDevices.clear();
        }

        @Override
        public int getCount() {
            return leDevices.size();
        }

        @Override
        public Object getItem(int i) {
            return leDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if (view == null) {
                view = inflator.inflate(R.layout.listitem_device, null);
                viewHolder = new ViewHolder();
                viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
                viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            BluetoothDevice device = leDevices.get(i);
            final String deviceName = device.getName();
            if (deviceName != null && deviceName.length() > 0)
                viewHolder.deviceName.setText(deviceName);
            else
                viewHolder.deviceName.setText(R.string.unknown_device);
            viewHolder.deviceAddress.setText(device.getAddress());

            return view;
        }
    }

    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
    }
}