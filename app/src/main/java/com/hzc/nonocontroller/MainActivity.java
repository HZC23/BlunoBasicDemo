package com.hzc.nonocontroller;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
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
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        applySettings();

        // Initialize DataBinding and ViewModel
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        blunoLibrary = BlunoLibrary.getInstance(this, this);
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

        // Set up listeners
        setupListeners(binding);
    }

    private void applySettings() {
        // Apply Dark Mode
        boolean isDarkMode = sharedPreferences.getBoolean("dark_mode", false);
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        // TODO: Apply inverted layout
    }

    private void setupListeners(ActivityMainBinding mainBinding) {
        // The bindings for included layouts are now fields in ActivityMainBinding
        com.hzc.nonocontroller.databinding.ControlsPanelBinding controlsBinding = mainBinding.controlsPanelInclude;
        com.hzc.nonocontroller.databinding.AutonomousModesCardBinding autonomousBinding = mainBinding.autonomousModesCardInclude;
        com.hzc.nonocontroller.databinding.ActiveAutonomousControlsBinding activeAutonomousBinding = mainBinding.activeAutonomousControlsInclude;

        // D-Pad from controls_panel.xml
        setupDirectionalButton(controlsBinding.buttonUp, "UP");
        setupDirectionalButton(controlsBinding.buttonDown, "DOWN");
        setupDirectionalButton(controlsBinding.buttonLeft, "LEFT");
        setupDirectionalButton(controlsBinding.buttonRight, "RIGHT");

        // Click handlers from the main layout
        mainBinding.mainSendLcdMessageButton.setOnClickListener(v -> {
            String message = mainBinding.mainLcdMessageInput.getText().toString();
            if (!message.isEmpty()) {
                viewModel.onSendLcdMessageClicked(message);
                mainBinding.mainLcdMessageInput.setText(""); // Clear input
            }
        });

        // Autonomous mode buttons
        autonomousBinding.gotoHeadingButton.setOnClickListener(v -> showGoToHeadingDialog());

        // Settings Icon from activity_main.xml
        mainBinding.settingsButton.setOnClickListener(v -> showSettingsDialog());

        mainBinding.connectIconButton.setOnClickListener(v -> blunoLibrary.buttonScanOnClickProcess());
    }

    // --- Dialogs ---
    private void showSettingsDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_settings, null);

        TextView consoleButton = dialogView.findViewById(R.id.settings_console_button);
        TextView calibrationButton = dialogView.findViewById(R.id.settings_calibration_button);
        com.google.android.material.switchmaterial.SwitchMaterial darkModeSwitch = dialogView.findViewById(R.id.settings_dark_mode_switch);
        com.google.android.material.switchmaterial.SwitchMaterial invertLayoutSwitch = dialogView.findViewById(R.id.settings_invert_layout_switch);

        boolean isDarkMode = sharedPreferences.getBoolean("dark_mode", false);
        boolean isInverted = sharedPreferences.getBoolean("invert_layout", false);

        darkModeSwitch.setChecked(isDarkMode);
        invertLayoutSwitch.setChecked(isInverted);

        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setTitle("Paramètres")
                .setView(dialogView)
                .setPositiveButton("OK", (d, which) -> {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    boolean themeChanged = false;
                    boolean layoutChanged = false;

                    boolean newDarkModeState = darkModeSwitch.isChecked();
                    if (newDarkModeState != isDarkMode) {
                        editor.putBoolean("dark_mode", newDarkModeState);
                        themeChanged = true;
                    }

                    boolean newInvertState = invertLayoutSwitch.isChecked();
                    if (newInvertState != isInverted) {
                        editor.putBoolean("invert_layout", newInvertState);
                        layoutChanged = true;
                    }

                    editor.apply();

                    if (themeChanged || layoutChanged) {
                        Toast.makeText(this, "Veuillez redémarrer l'application pour appliquer les changements", Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Annuler", null)
                .create();

        consoleButton.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ConsoleActivity.class));
            dialog.dismiss();
        });

        calibrationButton.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, CalibrationActivity.class));
            dialog.dismiss();
        });

        dialog.show();
    }

    private void showGoToHeadingDialog() {
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setHint("0-359");

        new MaterialAlertDialogBuilder(this)
                .setTitle("Aller au Cap")
                .setMessage("Entrez le cap en degrés (0-359):")
                .setView(input)
                .setPositiveButton("Go", (dialog, which) -> {
                    String value = input.getText().toString();
                    if (!value.isEmpty()) {
                        try {
                            int heading = Integer.parseInt(value);
                            viewModel.onGoToHeadingClicked(heading);
                        } catch (NumberFormatException e) {
                            Toast.makeText(this, "Entrée invalide", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Annuler", null)
                .show();
    }

    private void showSetCompassOffsetDialog() {
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
        input.setHint("-180.0 à 180.0");

        new MaterialAlertDialogBuilder(this)
                .setTitle("Définir l'Offset du Compas")
                .setMessage("Entrez la valeur de compensation:")
                .setView(input)
                .setPositiveButton("Définir", (dialog, which) -> {
                    String value = input.getText().toString();
                    if (!value.isEmpty()) {
                        try {
                            float offset = Float.parseFloat(value);
                            viewModel.onSetCompassOffsetClicked(offset);
                        } catch (NumberFormatException e) {
                            Toast.makeText(this, "Entrée invalide", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Annuler", null)
                .show();
    }


    // BlunoLibrary lifecycle methods
    @Override
    protected void onResume() {
        super.onResume();
        blunoLibrary.setDelegate(this);
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
        switch (theConnectionState) {
            case isConnected:
                Toast.makeText(this, "Connecté au robot", Toast.LENGTH_SHORT).show();
                break;
            case isConnecting:
                Toast.makeText(this, "Connexion en cours...", Toast.LENGTH_SHORT).show();
                break;
            case isToScan: // Fall-through
            case isScanning: // Fall-through
            case isDisconnecting:
                break; // No toast for these intermediate states
            default:
                Toast.makeText(this, "Déconnecté", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onSerialReceived(String theString) {
        Log.d("MainActivity", "Serial received: " + theString);
        // Update the serial monitor first
        String currentLog = viewModel.serialMonitor.getValue() != null ? viewModel.serialMonitor.getValue() : "";
        viewModel.updateSerialMonitor(currentLog + theString.trim() + "\n");

        // Parse the JSON telemetry data
        try {
            JSONObject json = new JSONObject(theString.trim());

            // Create a new object for the update to ensure LiveData triggers reliably.
            TelemetryData newTelemetry = new TelemetryData();

            // Populate the new object directly from the JSON data.
            if (json.has("state")) newTelemetry.setState(json.getString("state"));
            if (json.has("heading")) newTelemetry.setHeading(json.getInt("heading"));
            if (json.has("distance")) newTelemetry.setDistance(json.getInt("distance"));
            if (json.has("distanceLaser")) newTelemetry.setDistanceLaser(json.getInt("distanceLaser"));
            if (json.has("battery")) newTelemetry.setBattery(json.getInt("battery"));
            if (json.has("speedTarget")) newTelemetry.setSpeedTarget(json.getInt("speedTarget"));
            // speedCurrent is not sent by the robot, so we don't parse it.

            // Update the ViewModel with the new data object.
            viewModel.updateTelemetry(newTelemetry);
            Log.d("MainActivity", "Telemetry updated: " + newTelemetry.getState());

        } catch (JSONException e) {
            Log.e("MainActivity", "Failed to parse JSON: " + theString, e);
            // This is expected if the serial string is not a JSON object (e.g., a debug message)
            // Do nothing with it for telemetry.
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
