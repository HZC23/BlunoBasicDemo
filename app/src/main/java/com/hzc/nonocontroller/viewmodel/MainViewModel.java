package com.hzc.nonocontroller.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import android.util.Log;
import android.widget.SeekBar;

import android.view.View;

import com.hzc.nonocontroller.BlunoLibrary;
import com.hzc.nonocontroller.data.TelemetryData;

public class MainViewModel extends ViewModel {

    private final BlunoLibrary blunoLibrary;

    // LiveData for UI state
    private final MutableLiveData<TelemetryData> _telemetry = new MutableLiveData<>(new TelemetryData());
    public final LiveData<TelemetryData> telemetry = _telemetry;

    private final MutableLiveData<String> _serialMonitor = new MutableLiveData<>("");
    public final LiveData<String> serialMonitor = _serialMonitor;

    private final MutableLiveData<BlunoLibrary.connectionStateEnum> _connectionState = new MutableLiveData<>(BlunoLibrary.connectionStateEnum.isNull);
    public final LiveData<BlunoLibrary.connectionStateEnum> connectionState = _connectionState;

    private final MutableLiveData<Integer> _speed = new MutableLiveData<>(150);
    public final LiveData<Integer> speed = _speed;

    public MainViewModel(BlunoLibrary blunoLibrary) {
        this.blunoLibrary = blunoLibrary;
        // Observe the telemetry data to update UI visibility
        _telemetry.observeForever(this::updateUIVisibilityFromState);
    }

    // --- UI Visibility LiveData ---
    private final MutableLiveData<Integer> _autonomousModesVisibility = new MutableLiveData<>(View.VISIBLE);
    public final LiveData<Integer> autonomousModesVisibility = _autonomousModesVisibility;

    private final MutableLiveData<Integer> _activeAutonomousControlsVisibility = new MutableLiveData<>(View.GONE);
    public final LiveData<Integer> activeAutonomousControlsVisibility = _activeAutonomousControlsVisibility;

    private final MutableLiveData<Integer> _systemControlsVisibility = new MutableLiveData<>(View.GONE);
    public final LiveData<Integer> systemControlsVisibility = _systemControlsVisibility;


    // --- LiveData Updaters ---
    public void updateTelemetry(TelemetryData newTelemetry) {
        _telemetry.postValue(newTelemetry);
    }

    private void updateUIVisibilityFromState(TelemetryData telemetryData) {
        if (telemetryData == null || telemetryData.getState() == null) {
            setIdleModeVisibility();
            return;
        }

        String state = telemetryData.getState();
        if (state.contains("FOLLOW_HEADING") || state.contains("SMART_AVOIDANCE")) {
            setActiveAutonomousModeVisibility();
        } else { // Covers IDLE, MANUAL_CONTROL, etc.
            setIdleModeVisibility();
        }
    }

    private void setIdleModeVisibility() {
        _autonomousModesVisibility.postValue(View.VISIBLE);
        _activeAutonomousControlsVisibility.postValue(View.GONE);
    }

    private void setActiveAutonomousModeVisibility() {
        _autonomousModesVisibility.postValue(View.GONE);
        _activeAutonomousControlsVisibility.postValue(View.VISIBLE);
    }

    public void updateSerialMonitor(String newText) {
        String currentText = _serialMonitor.getValue();
        if (currentText == null) {
            currentText = "";
        }
        // Append new text and limit the length to avoid excessive memory usage
        String updatedText = currentText + newText;
        if (updatedText.length() > 5000) { // Keep last 5000 characters
            updatedText = updatedText.substring(updatedText.length() - 5000);
        }
        _serialMonitor.postValue(updatedText);
    }

    public void setConnectionState(BlunoLibrary.connectionStateEnum s) {
        _connectionState.postValue(s);
    }

    // --- UI Event Handlers ---

    public void onTurretScanClicked(View view) {
        sendCommand("CMD:SCAN:START\n");
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
    public void onSmartAvoidanceClicked() {
        sendCommand("CMD:MODE:AVOID\n");
    }

    public void onGoToHeadingClicked(int heading) {
        sendCommand("CMD:GOTO:" + heading + "\n");
    }

    public void onSentryModeClicked() {
        sendCommand("CMD:MODE:SENTRY\n");
    }

    public void onPauseClicked() {
        sendCommand("CMD:PAUSE\n");
    }

    public void onResumeClicked() {
        sendCommand("CMD:RESUME\n");
    }

    // --- Settings ---
    public void onSettingsClicked() {
        // Logic to show settings dialog will be in MainActivity
    }

    public void onToggleConsole(boolean show) {
        // This will be used to update a LiveData for console visibility
    }

    public void onGoToCalibrationClicked() {
        // This could navigate to a new Activity or show a specific dialog
    }

    // --- Accessories & System ---
    public void onLightSwitched(boolean isChecked) {
        if (isChecked) {
            sendCommand("CMD:LIGHT:ON\n");
        } else {
            sendCommand("CMD:LIGHT:OFF\n");
        }
    }

    public final SeekBar.OnSeekBarChangeListener onSpeedChanged = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                _speed.setValue(progress);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) { }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            sendCommand("CMD:SPEED:" + seekBar.getProgress() + "\n");
        }
    };

    public void onCalibrateCompassClicked() {
        sendCommand("CMD:CALIBRATE:COMPASS\n");
    }

    

    public void onSetCompassOffsetClicked(float offset) {
        sendCommand("CMD:COMPASS_OFFSET:" + offset + "\n");
    }

    public void onSendLcdMessageClicked(String message) {
        if (message != null && !message.isEmpty()) {
            sendCommand("CMD:LCD:" + message + "\n");
        }
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