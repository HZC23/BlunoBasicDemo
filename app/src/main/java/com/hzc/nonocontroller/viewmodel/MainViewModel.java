package com.hzc.nonocontroller.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import android.util.Log;
import android.widget.SeekBar;

import com.hzc.nonocontroller.BlunoLibrary;
import com.hzc.nonocontroller.data.TelemetryData;

public class MainViewModel extends ViewModel {

    private final BlunoLibrary blunoLibrary;

    // LiveData for UI state
    private final MutableLiveData<Boolean> _isManualMode = new MutableLiveData<>(true);
    public final LiveData<Boolean> isManualMode = _isManualMode;

    private final MutableLiveData<Boolean> _isAutoPaused = new MutableLiveData<>(true);
    public final LiveData<Boolean> isAutoPaused = _isAutoPaused;

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
        _isAutoPaused.setValue(true); // Start in paused state
        sendCommand("CMD:MOVE:STOP\n"); // Ensure robot is stopped when switching to auto
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
    public void onPauseResumeButtonClicked() {
        if (Boolean.TRUE.equals(_isAutoPaused.getValue())) {
            _isAutoPaused.setValue(false);
            sendCommand("CMD:MODE:AVOID\n"); // Resume autonomous movement
        } else {
            _isAutoPaused.setValue(true);
            sendCommand("CMD:MOVE:STOP\n"); // Pause
        }
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
