package com.hzc23.nonocontroller.data;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;

public class RobotRepository {
    // Singleton instance
    private static volatile RobotRepository instance;

    private BleManager bleManager;
    private final MutableLiveData<Telemetry> telemetry = new MutableLiveData<>();
    private final MutableLiveData<String> connectionState = new MutableLiveData<>();
    private final MutableLiveData<String> serialMonitor = new MutableLiveData<>();
    private final Gson gson = new Gson();

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

    public void init(Context context) {
        bleManager = new BleManager(context);
        if (!bleManager.initiate()) {
            // Handle BLE not supported
            connectionState.postValue("BLE not supported");
        }
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
        // to be implemented
    }

    public void sendCommand(String command) {
        // to be implemented
    }
}