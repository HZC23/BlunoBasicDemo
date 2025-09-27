package com.hzc.nonocontroller.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import android.util.Log;

import com.hzc.nonocontroller.BlunoLibrary;

public class CalibrationViewModel extends ViewModel {

    private final BlunoLibrary blunoLibrary;

    private final MutableLiveData<Integer> _heading = new MutableLiveData<>(0);
    public final LiveData<Integer> heading = _heading;

    public CalibrationViewModel(BlunoLibrary blunoLibrary) {
        this.blunoLibrary = blunoLibrary;
    }

    public void onStartCalibrationClicked() {
        sendCommand("CMD:CALIBRATE:COMPASS\n");
    }

    public void updateHeading(int newHeading) {
        _heading.postValue(newHeading);
    }

    private void sendCommand(String command) {
        if (blunoLibrary != null && blunoLibrary.mConnectionState == BlunoLibrary.connectionStateEnum.isConnected) {
            blunoLibrary.serialSend(command);
            Log.d("CalibrationViewModel", "Sent: " + command.trim());
        } else {
            Log.w("CalibrationViewModel", "Not connected, command ignored: " + command.trim());
        }
    }
}
