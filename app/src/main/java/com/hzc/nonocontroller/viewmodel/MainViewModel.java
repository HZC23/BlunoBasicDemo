package com.hzc.nonocontroller.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import android.view.View;
import android.widget.CompoundButton;
import android.util.Log;

import com.hzc.nonocontroller.BlunoLibrary;
import com.hzc.nonocontroller.data.TelemetryData;

public class MainViewModel extends ViewModel {

    private final BlunoLibrary blunoLibrary;

    public MainViewModel(BlunoLibrary blunoLibrary) {
        this.blunoLibrary = blunoLibrary;
    }

    // ... existing code ...

    private final MutableLiveData<TelemetryData> _telemetry = new MutableLiveData<>(new TelemetryData());
    public LiveData<TelemetryData> telemetry = _telemetry;

    private final MutableLiveData<String> _serialMonitor = new MutableLiveData<>("");
    public LiveData<String> serialMonitor = _serialMonitor;

    public void updateSerialMonitor(String newText) {
        _serialMonitor.postValue(newText);
    }

    public void updateTelemetry(TelemetryData newTelemetry) {
        _telemetry.postValue(newTelemetry);
    }

    private final MutableLiveData<Integer> _gotoAngle = new MutableLiveData<>(0);
    public LiveData<Integer> gotoAngle = _gotoAngle;

    private final MutableLiveData<BlunoLibrary.connectionStateEnum> _connectionState = new MutableLiveData<>(BlunoLibrary.connectionStateEnum.isNull);
    public LiveData<BlunoLibrary.connectionStateEnum> connectionState = _connectionState;

    public void setConnectionState(BlunoLibrary.connectionStateEnum s) {
        _connectionState.setValue(s);
    }

    public BlunoLibrary.connectionStateEnum getConnectionState() {
        return _connectionState.getValue();
    }

    public void setGotoAngle(int value) {
        _gotoAngle.setValue(value);
    }

    public int getGotoAngle() {
        return _gotoAngle.getValue() != null ? _gotoAngle.getValue() : 0;
    }

    public void onDirectionalButtonClicked(String direction, String currentState) {
        String command = "";
        if ("FOLLOW_HEADING".equals(currentState) || "MAINTAIN_HEADING".equals(currentState)) {
            switch (direction) {
                case "up":
                    command = "CMD:MOVE:FWD\n"; // Pause/Reprendre
                    break;
                case "down":
                    command = "CMD:MOVE:BWD\n"; // Pause
                    break;
                case "left":
                    command = "CMD:MOVE:LEFT\n"; // Ajuster Cap -
                    break;
                case "right":
                    command = "CMD:MOVE:RIGHT\n"; // Ajuster Cap +
                    break;
            }
        } else {
            // Normal movement commands
            switch (direction) {
                case "up":
                    command = "CMD:MOVE:FORWARD\n";
                    break;
                case "down":
                    command = "CMD:MOVE:BACKWARD\n";
                    break;
                case "left":
                    command = "CMD:MOVE:LEFT_TURN\n";
                    break;
                case "right":
                    command = "CMD:MOVE:RIGHT_TURN\n";
                    break;
            }
        }
        Log.d("MainViewModel", "Sending command: " + command);
        blunoLibrary.serialSend(command);
    }

    public void onClearLogClicked() {
        // Placeholder implementation
    }

    public void onScanStart() {
        blunoLibrary.serialSend("CMD:SCAN:START\n");
        Log.d("MainViewModel", "Sending command: CMD:SCAN:START");
    }

    public void onTurretCenter() {
        blunoLibrary.serialSend("CMD:TURRET:CENTER\n");
        Log.d("MainViewModel", "Sending command: CMD:TURRET:CENTER");
    }

    public void onScanClicked() {
        blunoLibrary.buttonScanOnClickProcess();
    }

    public void onSpeedChanged(int speed) {
        // Placeholder implementation
    }

    public void onStopButtonClicked() {
        blunoLibrary.serialSend("CMD:MOVE:STOP\n");
        Log.d("MainViewModel", "Sending command: CMD:MOVE:STOP");
    }

    public void onManualModeSelected() {
        blunoLibrary.serialSend("CMD:MODE:MANUAL\n");
        Log.d("MainViewModel", "Mode set to MANUAL");
    }
    public void onManualModeSelected(View v) { onManualModeSelected(); }

    public void onAvoidModeSelected() {
        blunoLibrary.serialSend("CMD:MODE:AVOID\n");
        Log.d("MainViewModel", "Mode set to AVOID");
    }
    public void onAvoidModeSelected(View v) { onAvoidModeSelected(); }

    public void onSentryModeSelected() {
        blunoLibrary.serialSend("CMD:MODE:SENTRY\n");
        Log.d("MainViewModel", "Mode set to SENTRY");
    }
    public void onSentryModeSelected(View v) { onSentryModeSelected(); }

    public void onFollowHeadingModeSelected() {
        blunoLibrary.serialSend("CMD:GOTO:0\n");
        Log.d("MainViewModel", "Mode set to FOLLOW_HEADING (via GOTO:0)");
    }
    public void onFollowHeadingModeSelected(View v) { onFollowHeadingModeSelected(); }

    public void onGoToAngle() { /* TODO */ }
    public void onGoToAngle(View v) { onGoToAngle(); }

    // appelé par dataBinding avec un boolean
    public void onLightChanged(boolean checked) {
        // TODO: logique, ex: viewModel.setLightEnabled(checked);
    }

    // signature alternative si le layout passe le CompoundButton + boolean:
    public void onLightChanged(CompoundButton button, boolean checked) {
        onLightChanged(checked);
    }

    public void onCalibrateCompass() {
        // TODO: implémenter calibration
    }

    public void onCalibrateCompass(View v) {
        onCalibrateCompass();
    }
}
