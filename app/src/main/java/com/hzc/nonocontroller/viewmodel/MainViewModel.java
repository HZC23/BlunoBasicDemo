package com.hzc.nonocontroller.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import android.util.Log;

import com.hzc.nonocontroller.BlunoLibrary;
import com.hzc.nonocontroller.data.TelemetryData;

public class MainViewModel extends ViewModel {

    private final BlunoLibrary blunoLibrary;

    // LiveData for UI state
    private final MutableLiveData<Boolean> _isManualMode = new MutableLiveData<>(true);
    public final LiveData<Boolean> isManualMode = _isManualMode;

    private final MutableLiveData<TelemetryData> _telemetry = new MutableLiveData<>(new TelemetryData());
    public final LiveData<TelemetryData> telemetry = _telemetry;

    private final MutableLiveData<String> _serialMonitor = new MutableLiveData<>("");
    public final LiveData<String> serialMonitor = _serialMonitor;

    private final MutableLiveData<BlunoLibrary.connectionStateEnum> _connectionState = new MutableLiveData<>(BlunoLibrary.connectionStateEnum.isNull);
    public final LiveData<BlunoLibrary.connectionStateEnum> connectionState = _connectionState;

    public MainViewModel(BlunoLibrary blunoLibrary) {
        this.blunoLibrary = blunoLibrary;
    }

    // --- LiveData Updaters ---
    public void updateTelemetry(TelemetryData newTelemetry) {
        _telemetry.postValue(newTelemetry);
    }

    public void updateSerialMonitor(String newText) {
        _serialMonitor.postValue(newText);
    }

    public void setConnectionState(BlunoLibrary.connectionStateEnum s) {
        _connectionState.setValue(s);
    }

    // --- UI Event Handlers ---

    public void onScanClicked() {
        blunoLibrary.buttonScanOnClickProcess();
    }

    public void onManualModeSelected() {
        _isManualMode.setValue(true);
        sendCommand("CMD:MODE:MANUAL\n");
    }

    public void onAutoModeSelected() {
        _isManualMode.setValue(false);
        // Note: Does not send a command immediately. User chooses an auto sub-mode.
    }

    public void onStopButtonClicked() {
        sendCommand("CMD:MOVE:STOP\n");
    }

    // --- Manual Control ---
    public void onDirectionalButton(String direction) {
        String command = "";
        switch (direction) {
            case "UP":
                command = "CMD:MOVE:FWD\n";
                break;
            case "DOWN":
                command = "CMD:MOVE:BWD\n";
                break;
            case "LEFT":
                command = "CMD:MOVE:LEFT\n";
                break;
            case "RIGHT":
                command = "CMD:MOVE:RIGHT\n";
                break;
        }
        sendCommand(command);
    }

    public void onDirectionalButtonReleased() {
        sendCommand("CMD:MOVE:STOP\n");
    }

    // --- Autonomous Control ---
    public void onAvoidModeButtonClicked() {
        sendCommand("CMD:MODE:AVOID\n");
    }

    public void onGoToHeadingClicked() {
        // Here you would typically open a dialog to get the heading angle.
        // For now, sending a default command.
        sendCommand("CMD:GOTO:90\n");
    }

    // --- Accessories ---
    public void onTurretLeftClicked() {
        sendCommand("CMD:TURRET:LEFT\n");
    }

    public void onTurretCenterClicked() {
        sendCommand("CMD:TURRET:CENTER\n");
    }

    public void onTurretRightClicked() {
        sendCommand("CMD:TURRET:RIGHT\n");
    }

    // --- Private Helper ---
    private void sendCommand(String command) {
        if (blunoLibrary != null && _connectionState.getValue() == BlunoLibrary.connectionStateEnum.isConnected) {
            blunoLibrary.serialSend(command);
            Log.d("MainViewModel", "Sent: " + command.trim());
        } else {
            Log.w("MainViewModel", "Not connected, command ignored: " + command.trim());
        }
    }
}