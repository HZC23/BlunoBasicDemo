package com.hzc.nonocontroller.data;

import android.app.Activity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.google.gson.Gson;
import android.util.Log;

public class RobotRepository implements BleManager.BleManagerListener {
    // Singleton instance
    private static volatile RobotRepository instance;

    private BleManager bleManager;
    private final MutableLiveData<Telemetry> telemetry = new MutableLiveData<>();
    private final MutableLiveData<String> connectionState = new MutableLiveData<>();
    private final MutableLiveData<String> serialMonitor = new MutableLiveData<>();
    private final Gson gson = new Gson();
    private StringBuilder serialBuffer = new StringBuilder();

    private RobotRepository() {
        // Private constructor for singleton
    }

    public static RobotRepository getInstance() {
        if (instance == null) {
            synchronized (RobotRepository.class) {
                if (instance == null) {
                    instance = new RobotRepository();
                }
            }
        }
        return instance;
    }

    public void init(Activity activity) {
        bleManager = new BleManager(activity);
        bleManager.setListener(this);
        if (!bleManager.initiate()) {
            // Handle BLE not supported
            connectionState.postValue("BLE not supported");
        }
        bleManager.registerReceiver();
    }

    public void destroy() {
        bleManager.unregisterReceiver();
        bleManager.close();
    }

    public LiveData<Telemetry> getTelemetry() {
        return telemetry;
    }

    public LiveData<String> getConnectionState() {
        return connectionState;
    }

    public LiveData<String> getSerialMonitor() {
        return serialMonitor;
    }

    public void scan() {
        bleManager.scanLeDevice(true);
    }

    public void sendCommand(String command) {
        bleManager.sendCommand(command);
    }

    @Override
    public void onConnectionStateChange(BleManager.ConnectionState connectionState) {
        this.connectionState.postValue(connectionState.toString());
    }

    @Override
    public void onSerialReceived(String data) {
        serialMonitor.postValue(data);
        serialBuffer.append(data);
        String bufferContent = serialBuffer.toString();

        int newlineIndex;
        while ((newlineIndex = bufferContent.indexOf('\n')) != -1) {
            String jsonMessage = bufferContent.substring(0, newlineIndex).trim();
            if (bufferContent.length() > newlineIndex + 1) {
                bufferContent = bufferContent.substring(newlineIndex + 1);
            } else {
                bufferContent = "";
            }

            if (!jsonMessage.isEmpty()) {
                try {
                    Log.d("RobotRepository", "Received JSON: " + jsonMessage);
                    Telemetry telemetryData = gson.fromJson(jsonMessage, Telemetry.class);
                    telemetry.postValue(telemetryData);
                } catch (Exception e) {
                    Log.e("RobotRepository", "Failed to parse JSON: " + jsonMessage, e);
                }
            }
        }
        serialBuffer = new StringBuilder(bufferContent);
    }

    @Override
    public void onServicesDiscovered() {
        bleManager.onServicesDiscovered();
    }
}
