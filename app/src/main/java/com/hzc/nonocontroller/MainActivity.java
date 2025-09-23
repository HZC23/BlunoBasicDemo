package com.hzc.nonocontroller;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.hzc.nonocontroller.data.TelemetryData;
import com.hzc.nonocontroller.databinding.ActivityMainBinding;
import com.hzc.nonocontroller.viewmodel.MainViewModel;
import com.hzc.nonocontroller.viewmodel.MainViewModelFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MainActivity extends AppCompatActivity implements BlunoLibraryDelegate {
    private ActivityMainBinding binding;
    private MainViewModel viewModel;
    private BlunoLibrary blunoLibrary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize DataBinding and ViewModel
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        blunoLibrary = new BlunoLibrary(this, this);
        MainViewModelFactory factory = new MainViewModelFactory(blunoLibrary);
        viewModel = new ViewModelProvider(this, factory).get(MainViewModel.class);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        // Initialize BlunoLibrary
        blunoLibrary.request(1000, new BlunoLibrary.OnPermissionsResult() {
            @Override
            public void OnSuccess() {
                Toast.makeText(MainActivity.this, "Permissions granted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void OnFail(List<String> noPermissions) {
                Toast.makeText(MainActivity.this, "Permissions denied", Toast.LENGTH_SHORT).show();
            }
        });

        blunoLibrary.onCreateProcess();
        blunoLibrary.serialBegin(115200);

        // Set up D-Pad listeners
        setupDirectionalButton(binding.buttonUp, "UP");
        setupDirectionalButton(binding.buttonDown, "DOWN");
        setupDirectionalButton(binding.buttonLeft, "LEFT");
        setupDirectionalButton(binding.buttonRight, "RIGHT");
    }

    // BlunoLibrary lifecycle methods
    @Override
    protected void onResume() {
        super.onResume();
        blunoLibrary.onResumeProcess();
    }

    @Override
    protected void onPause() {
        super.onPause();
        blunoLibrary.onPauseProcess();
    }

    @Override
    protected void onStop() {
        super.onStop();
        blunoLibrary.onStopProcess();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        blunoLibrary.onDestroyProcess();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        blunoLibrary.onRequestPermissionsResultProcess(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        blunoLibrary.onActivityResultProcess(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    // BlunoLibraryDelegate methods
    @Override
    public void onConectionStateChange(BlunoLibrary.connectionStateEnum theConnectionState) {
        viewModel.setConnectionState(theConnectionState);
    }

    @Override
    public void onSerialReceived(String theString) {
        viewModel.updateSerialMonitor(viewModel.serialMonitor.getValue() + theString);

        try {
            JSONObject json = new JSONObject(theString.trim());
            TelemetryData currentTelemetry = viewModel.telemetry.getValue();
            if (currentTelemetry == null) {
                currentTelemetry = new TelemetryData();
            }

            if (json.has("state")) currentTelemetry.state = json.getString("state");
            if (json.has("heading")) currentTelemetry.heading = json.getInt("heading");
            if (json.has("distance")) currentTelemetry.setDistance(json.getInt("distance"));
            if (json.has("distanceLaser")) currentTelemetry.setDistanceLaser(json.getInt("distanceLaser"));
            if (json.has("battery")) currentTelemetry.setBattery(json.getInt("battery"));
            if (json.has("speedTarget")) currentTelemetry.setSpeedTarget(json.getInt("speedTarget"));
            if (json.has("speedCurrent")) currentTelemetry.setSpeedCurrent(json.getInt("speedCurrent"));

            viewModel.updateTelemetry(currentTelemetry);
        } catch (JSONException e) {
            // Not a JSON string, log it.
        }
    }

    private void setupDirectionalButton(ImageButton button, final String direction) {
        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        viewModel.onDirectionalButton(direction);
                        return true; // Consume the event
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        viewModel.onDirectionalButtonReleased();
                        return true; // Consume the event
                }
                return false;
            }
        });
    }
}
