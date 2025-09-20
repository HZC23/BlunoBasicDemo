package com.hzc.nonocontroller;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.tabs.TabLayout;
import com.hzc.nonocontroller.data.RobotRepository;
import com.hzc.nonocontroller.databinding.ActivityMainBinding;
import com.hzc.nonocontroller.databinding.ControlsPanelBinding;
import com.hzc.nonocontroller.databinding.LogPanelBinding;
import com.hzc.nonocontroller.databinding.TelemetryPanelBinding;
import com.hzc.nonocontroller.viewmodel.MainViewModel;
import com.hzc.nonocontroller.widget.JoystickView;

public class MainActivity extends AppCompatActivity {

    private MainViewModel viewModel;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        

        // Set up ViewModel and DataBinding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        // Set fullscreen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            final WindowInsetsController insetsController = getWindow().getInsetsController();
            if (insetsController != null) {
                insetsController.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
                insetsController.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
        } else {
            getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }

        // Initialize RobotRepository
        RobotRepository.getInstance().init(this);

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        // Request Bluetooth permissions
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1000);

        // Setup UI
        setupJoystick();
        setupTabs();
    }

    private void setupJoystick() {
        binding.joystick.setJoystickListener(new JoystickView.JoystickListener() {
            @Override
            public void onJoystickMoved(float xPercent, float yPercent, int angle, int strength) {
                viewModel.onJoystickMoved(angle, strength);
            }
        });
    }

    private void setupTabs() {
        // Select first tab by default
        updateInfoContent(binding.infoTabs.getTabAt(0));

        binding.infoTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                updateInfoContent(tab);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });
    }

    private void updateInfoContent(TabLayout.Tab tab) {
        binding.infoContent.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        if (tab.getPosition() == 0) { // Telemetry
            TelemetryPanelBinding telemetryBinding = DataBindingUtil.inflate(inflater, R.layout.telemetry_panel, binding.infoContent, true);
            telemetryBinding.setViewModel(viewModel);
            telemetryBinding.setLifecycleOwner(this);
        } else if (tab.getPosition() == 1) { // Controls
            ControlsPanelBinding controlsBinding = DataBindingUtil.inflate(inflater, R.layout.controls_panel, binding.infoContent, true);
            controlsBinding.setViewModel(viewModel);
            controlsBinding.setLifecycleOwner(this);
        } else { // Log
            LogPanelBinding logBinding = DataBindingUtil.inflate(inflater, R.layout.log_panel, binding.infoContent, true);
            logBinding.setViewModel(viewModel);
            logBinding.setLifecycleOwner(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000) {
            if (grantResults.length > 0 && grantResults[0] == getPackageManager().PERMISSION_GRANTED) {
                // Permission granted
            } else {
                Toast.makeText(this, "Bluetooth permissions are required to connect to the robot.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
