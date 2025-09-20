package com.hzc23.nonocontroller;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MainActivity extends BlunoLibrary {
    private static final String TAG = "MainActivity";

    // UI Elements
    private TextView textViewTitle;
    private ImageView batteryIcon;
    private TextView telemetryBattery;
    private Button buttonScan;
    private ImageView settingsIcon;
    private CompassView compassView;
    private Button buttonMoveFwd, buttonMoveBwd, buttonMoveLeft, buttonMoveRight, buttonStop;
    private Button buttonManuel, buttonAuto, buttonObstacle, buttonPIR, buttonCap;
    private SeekBar speedSlider;


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
        initializeUI();

        // Setup Listeners
        setupListeners();
    }

    private void initializeUI() {
        textViewTitle = findViewById(R.id.textViewTitle);
        batteryIcon = findViewById(R.id.batteryIcon);
        telemetryBattery = findViewById(R.id.telemetry_battery);
        buttonScan = findViewById(R.id.buttonScan);
        settingsIcon = findViewById(R.id.settingsIcon);
        compassView = findViewById(R.id.compassView);
        buttonMoveFwd = findViewById(R.id.buttonMoveFwd);
        buttonMoveBwd = findViewById(R.id.buttonMoveBwd);
        buttonMoveLeft = findViewById(R.id.buttonMoveLeft);
        buttonMoveRight = findViewById(R.id.buttonMoveRight);
        buttonStop = findViewById(R.id.buttonStop);
        buttonManuel = findViewById(R.id.buttonManuel);
        buttonAuto = findViewById(R.id.buttonAuto);
        buttonObstacle = findViewById(R.id.buttonObstacle);
        buttonPIR = findViewById(R.id.buttonPIR);
        buttonCap = findViewById(R.id.buttonCap);
        speedSlider = findViewById(R.id.speedSlider);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupListeners() {
        buttonScan.setOnClickListener(v -> buttonScanOnClickProcess());

        // Manual Controls Listeners
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

        // Speed Slider
        speedSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                serialSend("CMD:SPEED:" + seekBar.getProgress() + "\n");
            }
        });
    }

    @Override
    public void onConectionStateChange(connectionStateEnum theConnectionState) {
        switch (theConnectionState) {
            case isConnected:
                buttonScan.setText("Connected");
                break;
            case isConnecting:
                buttonScan.setText("Connecting");
                break;
            case isToScan:
                buttonScan.setText("Scan");
                break;
            case isScanning:
                buttonScan.setText("Scanning");
                break;
            case isDisconnecting:
                buttonScan.setText("Disconnecting");
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
                    parseTelemetry(jsonMessage);
                });
            }
        }
        serialBuffer = new StringBuilder(bufferContent);
    }

    private void parseTelemetry(String jsonString) {
        try {
            JSONObject json = new JSONObject(jsonString);
            int heading = json.optInt("heading", 0);
            int battery = json.optInt("battery", -1);

            compassView.setHeading(heading);
            telemetryBattery.setText(battery != -1 ? String.format("%d%%", battery) : "--");

            if (battery != -1 && battery < 20) {
                telemetryBattery.setTextColor(ContextCompat.getColor(this, R.color.accent_red_alert));
            } else {
                telemetryBattery.setTextColor(ContextCompat.getColor(this, R.color.text_grey_light));
            }

        } catch (JSONException e) {
            Log.e(TAG, "JSON parsing error: " + e.getMessage());
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