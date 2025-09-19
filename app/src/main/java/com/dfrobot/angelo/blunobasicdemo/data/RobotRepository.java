package com.dfrobot.angelo.blunobasicdemo.data;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.dfrobot.angelo.blunobasicdemo.BlunoLibrary;

import org.json.JSONException;
import org.json.JSONObject;

public class RobotRepository {
    private static final String TAG = "RobotRepository";
    private static RobotRepository instance;
    private final MutableLiveData<Telemetry> telemetryData = new MutableLiveData<>();
    private final StringBuilder serialBuffer = new StringBuilder();
    private BlunoLibrary blunoLibrary;

    private RobotRepository() {
        // private constructor
    }

    public static synchronized RobotRepository getInstance() {
        if (instance == null) {
            instance = new RobotRepository();
        }
        return instance;
    }

    public void setBlunoLibrary(BlunoLibrary blunoLibrary) {
        this.blunoLibrary = blunoLibrary;
    }

    public LiveData<Telemetry> getTelemetry() {
        return telemetryData;
    }

    public void onSerialReceived(String data) {
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
                parseTelemetry(jsonMessage);
            }
        }
        serialBuffer.delete(0, serialBuffer.length()).append(bufferContent);
    }

    private void parseTelemetry(String jsonString) {
        try {
            JSONObject json = new JSONObject(jsonString);
            Telemetry telemetry = new Telemetry();
            telemetry.state = json.optString("state", "N/A");
            telemetry.heading = json.optInt("heading", 0);
            telemetry.distance = json.optInt("distance", -1);
            telemetry.battery = json.optInt("battery", -1);
            telemetry.distanceLaser = json.optInt("distanceLaser", -1);
            telemetry.speedTarget = json.optInt("speedTarget", 0);

            telemetryData.postValue(telemetry);

        } catch (JSONException e) {
            Log.e(TAG, "JSON parsing error: " + e.getMessage());
        }
    }

    public void sendCommand(String command) {
        if (blunoLibrary != null) {
            blunoLibrary.serialSend(command);
        }
    }
}