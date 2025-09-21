package com.hzc.nonocontroller.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.hzc.nonocontroller.data.RobotRepository;
import com.hzc.nonocontroller.data.Telemetry;

public class MainViewModel extends ViewModel {

    private RobotRepository robotRepository;

    public final MutableLiveData<String> gotoAngle = new MutableLiveData<>();

    public MainViewModel() {
        robotRepository = RobotRepository.getInstance();
    }

    public LiveData<Telemetry> getTelemetry() {
        return robotRepository.getTelemetry();
    }

    public LiveData<String> getConnectionState() {
        return robotRepository.getConnectionState();
    }

    public LiveData<String> getSerialMonitor() {
        return robotRepository.getSerialMonitor();
    }

    public void onScanClicked() {
        robotRepository.scan();
    }

    public void onMoveForward() {
        robotRepository.sendCommand("CMD:MOVE:FWD\n");
    }

    public void onMoveBackward() {
        robotRepository.sendCommand("CMD:MOVE:BWD\n");
    }

    public void onMoveLeft() {
        robotRepository.sendCommand("CMD:MOVE:LEFT\n");
    }

    public void onMoveRight() {
        robotRepository.sendCommand("CMD:MOVE:RIGHT\n");
    }

    public void onStop() {
        robotRepository.sendCommand("CMD:MOVE:STOP\n");
    }

    public void onSpeedChanged(int speed) {
        robotRepository.sendCommand("CMD:SPEED:" + speed + "\n");
    }

    public void onGoToAngle() {
        String angle = gotoAngle.getValue();
        if (angle != null && !angle.isEmpty()) {
            robotRepository.sendCommand("CMD:GOTO:" + angle + "\n");
        }
    }

    public void onLightChanged(boolean isChecked) {
        String command = isChecked ? "CMD:LIGHT:ON\n" : "CMD:LIGHT:OFF\n";
        robotRepository.sendCommand(command);
    }

    public void onCalibrateCompass() {
        robotRepository.sendCommand("CMD:CALIBRATE:COMPASS\n");
    }

    public void onDirectionalButtonClicked(String direction) {
        Telemetry telemetry = getTelemetry().getValue();
        if (telemetry != null) {
            String state = telemetry.state;
            if (state != null && (state.equals("FOLLOW_HEADING") || state.equals("MAINTAIN_HEADING"))) {
                switch (direction) {
                    case "up":
                        robotRepository.sendCommand("CMD:MOVE:FWD\n");
                        break;
                    case "down":
                        robotRepository.sendCommand("CMD:MOVE:BWD\n");
                        break;
                    case "left":
                        robotRepository.sendCommand("CMD:MOVE:LEFT\n");
                        break;
                    case "right":
                        robotRepository.sendCommand("CMD:MOVE:RIGHT\n");
                        break;
                }
                return;
            }
        }

        // Default behavior
        switch (direction) {
            case "up":
                robotRepository.sendCommand("CMD:MOVE:FWD\n");
                break;
            case "down":
                robotRepository.sendCommand("CMD:MOVE:BWD\n");
                break;
            case "left":
                robotRepository.sendCommand("CMD:MOVE:LEFT\n");
                break;
            case "right":
                robotRepository.sendCommand("CMD:MOVE:RIGHT\n");
                break;
        }
    }

    public void onStopButtonClicked() {
        robotRepository.sendCommand("CMD:MOVE:STOP\n");
    }

    public void onManualModeSelected() {
        robotRepository.sendCommand("CMD:MODE:MANUAL\n");
    }

    public void onAvoidModeSelected() {
        robotRepository.sendCommand("CMD:MODE:AVOID\n");
    }

    public void onSentryModeSelected() {
        robotRepository.sendCommand("CMD:MODE:SENTRY\n");
    }

    public void onScanStart() {
        robotRepository.sendCommand("CMD:SCAN:START\n");
    }

    public void onTurretCenter() {
        robotRepository.sendCommand("CMD:TURRET:CENTER\n");
    }

    public void onClearLogClicked() {
        robotRepository.clearSerialMonitor();
    }
}