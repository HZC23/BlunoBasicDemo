package com.dfrobot.angelo.blunobasicdemo;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.content.Intent;
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
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import com.dfrobot.angelo.blunobasicdemo.data.RobotRepository;
import com.dfrobot.angelo.blunobasicdemo.data.Telemetry;
import com.dfrobot.angelo.blunobasicdemo.viewmodel.MainViewModel;

import java.util.List;

public class MainActivity extends BlunoLibrary {
    private MainViewModel mainViewModel;

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

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        RobotRepository.getInstance().setBlunoLibrary(this);

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

        // Observe LiveData
        observeViewModel();
    }

    private void initializeUI() {
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
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupListeners() {
        buttonScan.setOnClickListener(v -> buttonScanOnClickProcess());

        // Manual Controls Listeners
        buttonStop.setOnClickListener(v -> mainViewModel.sendCommand("CMD:MOVE:STOP\n"));

        View.OnTouchListener moveListener = (v, event) -> {
            String command = "";
            if (v.getId() == R.id.buttonMoveFwd) command = "CMD:MOVE:FWD\n";
            else if (v.getId() == R.id.buttonMoveBwd) command = "CMD:MOVE:BWD\n";
            else if (v.getId() == R.id.buttonMoveLeft) command = "CMD:MOVE:LEFT\n";
            else if (v.getId() == R.id.buttonMoveRight) command = "CMD:MOVE:RIGHT\n";

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                mainViewModel.sendCommand(command);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                mainViewModel.sendCommand("CMD:MOVE:STOP\n");
            }
            return true;
        };

        buttonMoveFwd.setOnTouchListener(moveListener);
        buttonMoveBwd.setOnTouchListener(moveListener);
        buttonMoveLeft.setOnTouchListener(moveListener);
        buttonMoveRight.setOnTouchListener(moveListener);

        // Advanced Controls Listeners
        seekBarSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mainViewModel.sendCommand("CMD:SPEED:" + seekBar.getProgress() + "\n");
            }
        });

        buttonGo.setOnClickListener(v -> {
            String heading = editTextHeading.getText().toString();
            if (!heading.isEmpty()) {
                mainViewModel.sendCommand("CMD:GOTO:" + heading + "\n");
            }
        });

        switchLight.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mainViewModel.sendCommand("CMD:LIGHT:ON\n");
            } else {
                mainViewModel.sendCommand("CMD:LIGHT:OFF\n");
            }
        });

        buttonCalibrate.setOnClickListener(v -> mainViewModel.sendCommand("CMD:CALIBRATE:COMPASS\n"));
    }

    private void observeViewModel() {
        mainViewModel.getTelemetry().observe(this, telemetry -> {
            if (telemetry != null) {
                updateTelemetryUI(telemetry);
            }
        });
    }

    private void updateTelemetryUI(Telemetry telemetry) {
        telemetryState.setText(String.format("State: %s", telemetry.state));
        telemetryHeading.setText(String.format("Heading: %d°", telemetry.heading));
        telemetryBattery.setText(telemetry.battery != -1 ? String.format("Battery: %d%%", telemetry.battery) : "Battery: --");

        if (telemetry.distance != -1) {
            progressDistance.setProgress(100 - telemetry.distance); // Assuming max distance is 100cm
        } else {
            progressDistance.setProgress(0);
        }

        if (telemetry.battery != -1 && telemetry.battery < 20) {
            telemetryBattery.setTextColor(ContextCompat.getColor(this, R.color.accent_red_alert));
        } else {
            telemetryBattery.setTextColor(ContextCompat.getColor(this, R.color.text_grey_light));
        }
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

    @Override
    public void onSerialReceived(String theString) {
        serialReceivedText.append(theString);
        consoleScrollView.fullScroll(View.FOCUS_DOWN);
        RobotRepository.getInstance().onSerialReceived(theString);
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
}
