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
        robotRepository.sendCommand("U\n");
    }

    public void onMoveBackward() {
        robotRepository.sendCommand("D\n");
    }

    public void onMoveLeft() {
        robotRepository.sendCommand("L\n");
    }

    public void onMoveRight() {
        robotRepository.sendCommand("R\n");
    }

    public void onStop() {
        robotRepository.sendCommand("stop\n");
    }

    public void onSpeedChanged(int speed) {
        robotRepository.sendCommand("vitesse " + speed + "\n");
    }

    public void onGoToAngle() {
        String angle = gotoAngle.getValue();
        if (angle != null && !angle.isEmpty()) {
            robotRepository.sendCommand("virageprecis " + angle + "\n");
        }
    }

    public void onLightChanged(boolean isChecked) {
        String command = isChecked ? "on\n" : "off\n";
        robotRepository.sendCommand(command);
    }

    public void onCalibrateCompass() {
        robotRepository.sendCommand("calibrer\n");
    }

    public void onJoystickMoved(int angle, int strength) {
        if (strength < 20) {
            onStop();
            return;
        }

        if (angle > 45 && angle <= 135) {
            onMoveRight();
        } else if (angle > 135 && angle <= 225) {
            onMoveBackward();
        } else if (angle > 225 && angle <= 315) {
            onMoveLeft();
        } else {
            onMoveForward();
        }
    }

    public void onManualModeSelected() {
        robotRepository.sendCommand("manual\n");
    }

    public void onAutoModeSelected() {
        robotRepository.sendCommand("auto\n");
    }

    public void onObstacleModeSelected() {
        robotRepository.sendCommand("obstacle\n");
    }
}