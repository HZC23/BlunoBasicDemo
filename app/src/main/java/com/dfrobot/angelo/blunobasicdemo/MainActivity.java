package com.dfrobot.angelo.blunobasicdemo;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MainActivity extends BlunoLibrary {
    private static final String TAG = "MainActivity";

    // UI Elements
    private Button buttonScan;
    private TextView connectionStatus;
    private TextView serialReceivedText;
    private ScrollView consoleScrollView;

    // Manual Controls
    private Button buttonMoveFwd, buttonMoveBwd, buttonMoveLeft, buttonMoveRight, buttonStop;

    // Advanced Controls
    private SeekBar seekBarSpeed;
    private EditText editTextHeading;
    private Button buttonGo;
    private Switch switchLight;
    private Button buttonCalibrate;

    // Telemetry UI
    private TextView telemetryState;
    private TextView telemetryHeading;
    private ProgressBar progressDistance;
    private TextView telemetryBattery;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Request permissions
        request(1000, new OnPermissionsResult() {
            @Override
            public void OnSuccess() {
                // Permissions granted
            }

            @Override
            public void OnFail(List<String> noPermissions) {
                Toast.makeText(MainActivity.this, "Permissions required for Bluetooth.", Toast.LENGTH_LONG).show();
            }
        });

        onCreateProcess(); // BlunoLibrary setup
        serialBegin(115200); // Set Baudrate

        // Initialize UI components
        buttonScan = findViewById(R.id.buttonScan);
        connectionStatus = findViewById(R.id.connectionStatus);
        serialReceivedText = findViewById(R.id.serialReceivedText);
        consoleScrollView = findViewById(R.id.consoleScrollView);

        // Manual Controls
        buttonMoveFwd = findViewById(R.id.buttonMoveFwd);
        buttonMoveBwd = findViewById(R.id.buttonMoveBwd);
        buttonMoveLeft = findViewById(R.id.buttonMoveLeft);
        buttonMoveRight = findViewById(R.id.buttonMoveRight);
        buttonStop = findViewById(R.id.buttonStop);

        // Advanced Controls
        seekBarSpeed = findViewById(R.id.seekBarSpeed);
        editTextHeading = findViewById(R.id.editTextHeading);
        buttonGo = findViewById(R.id.buttonGo);
        switchLight = findViewById(R.id.switchLight);
        buttonCalibrate = findViewById(R.id.buttonCalibrate);

        // Telemetry
        telemetryState = findViewById(R.id.telemetry_state);
        telemetryHeading = findViewById(R.id.telemetry_heading);
        progressDistance = findViewById(R.id.progress_distance);
        telemetryBattery = findViewById(R.id.telemetry_battery);

        // Setup Listeners
        buttonScan.setOnClickListener(v -> buttonScanOnClickProcess());

        // Manual Controls Listeners
        setupButtonListeners();

        // Advanced Controls Listeners
        setupAdvancedControlsListeners();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupButtonListeners() {
        buttonStop.setOnClickListener(v -> serialSend("CMD:MOVE:STOP\n"));

        View.OnTouchListener moveListener = (v, event) -> {
            String command = "";
            if (v.getId() == R.id.buttonMoveFwd) command = "CMD:MOVE:FWD\n";
            else if (v.getId() == R.id.buttonMoveBwd) command = "CMD:MOVE:BWD\n";
            else if (v.getId() == R.id.buttonMoveLeft) command = "CMD:MOVE:LEFT\n";
            else if (v.getId() == R.id.buttonMoveRight) command = "CMD:MOVE:RIGHT\n";

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                serialSend(command);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                serialSend("CMD:MOVE:STOP\n");
            }
            return true;
        };

        buttonMoveFwd.setOnTouchListener(moveListener);
        buttonMoveBwd.setOnTouchListener(moveListener);
        buttonMoveLeft.setOnTouchListener(moveListener);
        buttonMoveRight.setOnTouchListener(moveListener);
    }

    private void setupAdvancedControlsListeners() {
        seekBarSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                serialSend("CMD:SPEED:" + seekBar.getProgress() + "\n");
            }
        });

        buttonGo.setOnClickListener(v -> {
            String heading = editTextHeading.getText().toString();
            if (!heading.isEmpty()) {
                serialSend("CMD:GOTO:" + heading + "\n");
            }
        });

        switchLight.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                serialSend("CMD:LIGHT:ON\n");
            } else {
                serialSend("CMD:LIGHT:OFF\n");
            }
        });

        buttonCalibrate.setOnClickListener(v -> serialSend("CMD:CALIBRATE:COMPASS\n"));
    }


    @Override
    public void onConectionStateChange(connectionStateEnum theConnectionState) {
        switch (theConnectionState) {
            case isConnected:
                buttonScan.setText("Connected");
                connectionStatus.setText("Connected");
                connectionStatus.setTextColor(ContextCompat.getColor(this, R.color.accent_green_status));
                break;
            case isConnecting:
                buttonScan.setText("Connecting");
                connectionStatus.setText("Connecting");
                connectionStatus.setTextColor(Color.YELLOW);
                break;
            case isToScan:
                buttonScan.setText("Scan");
                connectionStatus.setText("Disconnected");
                connectionStatus.setTextColor(ContextCompat.getColor(this, R.color.accent_red_alert));
                break;
            case isScanning:
                buttonScan.setText("Scanning");
                connectionStatus.setText("Scanning...");
                connectionStatus.setTextColor(Color.YELLOW);
                break;
            case isDisconnecting:
                buttonScan.setText("Disconnecting");
                connectionStatus.setText("Disconnecting");
                connectionStatus.setTextColor(ContextCompat.getColor(this, R.color.accent_red_alert));
                break;
            default:
                break;
        }
    }

    private StringBuilder serialBuffer = new StringBuilder();

    @Override
    public void onSerialReceived(String theString) {
        serialBuffer.append(theString);
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
                runOnUiThread(() -> {
                    serialReceivedText.append(jsonMessage + "\n");
                    consoleScrollView.fullScroll(View.FOCUS_DOWN);
                    parseTelemetry(jsonMessage);
                });
            }
        }
        serialBuffer = new StringBuilder(bufferContent);
    }

    private void parseTelemetry(String jsonString) {
        try {
            JSONObject json = new JSONObject(jsonString);
            String state = json.optString("state", "N/A");
            int heading = json.optInt("heading", 0);
            int distance = json.optInt("distance", -1);
            int battery = json.optInt("battery", -1);

            telemetryState.setText(String.format("State: %s", state));
            telemetryHeading.setText(String.format("Heading: %d°", heading));
            telemetryBattery.setText(battery != -1 ? String.format("Battery: %d%%", battery) : "Battery: --");

            if (distance != -1) {
                progressDistance.setProgress(100 - distance); // Assuming max distance is 100cm
            } else {
                progressDistance.setProgress(0);
            }

            if (battery != -1 && battery < 20) {
                telemetryBattery.setTextColor(ContextCompat.getColor(this, R.color.accent_red_alert));
            } else {
                telemetryBattery.setTextColor(ContextCompat.getColor(this, R.color.text_grey_light));
            }

        } catch (JSONException e) {
            Log.e(TAG, "JSON parsing error: " + e.getMessage());
            serialReceivedText.append("[ERROR] Invalid Telemetry: " + jsonString + "\n");
            consoleScrollView.fullScroll(View.FOCUS_DOWN);
        }
    }


    // region BlunoLibrary Lifecycle
    @Override
    protected void onResume() {
        super.onResume();
        onResumeProcess();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        onActivityResultProcess(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        onPauseProcess();
    }

    @Override
    protected void onStop() {
        super.onStop();
        onStopProcess();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onDestroyProcess();
    }
    // endregion
}