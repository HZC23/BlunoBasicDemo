package com.dfrobot.angelo.blunobasicdemo.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.dfrobot.angelo.blunobasicdemo.data.RobotRepository;
import com.dfrobot.angelo.blunobasicdemo.data.Telemetry;

public class MainViewModel extends ViewModel {
    private final RobotRepository robotRepository;

    public MainViewModel() {
        this.robotRepository = RobotRepository.getInstance();
    }

    public LiveData<Telemetry> getTelemetry() {
        return robotRepository.getTelemetry();
    }

    public void sendCommand(String command) {
        robotRepository.sendCommand(command);
    }
}
