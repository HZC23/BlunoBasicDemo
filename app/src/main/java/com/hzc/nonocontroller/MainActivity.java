package com.hzc.nonocontroller;

import android.content.Context;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.hzc.nonocontroller.databinding.ActivityMainBinding;
import com.hzc.nonocontroller.viewmodel.MainViewModel;
import com.hzc.nonocontroller.viewmodel.MainViewModelFactory;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.NonNull;
import com.hzc.nonocontroller.BlunoLibraryDelegate;
import org.json.JSONException;
import org.json.JSONObject;
import com.hzc.nonocontroller.data.TelemetryData;

public class MainActivity  extends AppCompatActivity implements BlunoLibraryDelegate {
    private ActivityMainBinding binding;
    private MainViewModel viewModel;
	private BlunoLibrary blunoLibrary;
	//private Button buttonScan;
	//private Button buttonSerialSend;
	//private EditText serialSendText;
	//private TextView serialReceivedText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_main); // Replaced by DataBinding

        // Initialize DataBinding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        // Initialize ViewModel
        blunoLibrary = new BlunoLibrary(this, this); // Pass MainActivity as Context and BlunoLibraryDelegate
        MainViewModelFactory factory = new MainViewModelFactory(blunoLibrary);
        viewModel = new ViewModelProvider(this, factory).get(MainViewModel.class);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this); // Set the lifecycle owner for LiveData updates

        // Set "Manual" mode as default selected
        ((com.google.android.material.button.MaterialButtonToggleGroup) binding.getRoot().findViewById(R.id.mode_toggle_group)).check(R.id.mode_manual_button);

		blunoLibrary.request(1000, new BlunoLibrary.OnPermissionsResult() {
			@Override
			public void OnSuccess() {
				Toast.makeText(MainActivity.this,"权限请求成功",Toast.LENGTH_SHORT).show();
			}

			@Override
			public void OnFail(List<String> noPermissions) {
				Toast.makeText(MainActivity.this,"权限请求失败",Toast.LENGTH_SHORT).show();
			}
		});

        blunoLibrary.onCreateProcess();														//onCreate Process by BlunoLibrary


        blunoLibrary.serialBegin(115200);													//set the Uart Baudrate on BLE chip to 115200

        //serialReceivedText=(TextView) findViewById(R.id.serialReveicedText);	//initial the EditText of the received data
        //serialSendText=(EditText) findViewById(R.id.serialSendText);			//initial the EditText of the sending data

        //buttonSerialSend = (Button) findViewById(R.id.buttonSerialSend);		//initial the button for sending the data
        //buttonSerialSend.setOnClickListener(new OnClickListener() {

			//@Override
			//public void onClick(View v) {
				// TODO Auto-generated method stub

				//serialSend(serialSendText.getText().toString());				//send the data to the BLUNO
			//}
		//});

        //buttonScan = (Button) findViewById(R.id.buttonScan);					//initial the button for scanning the BLE device
        //buttonScan.setOnClickListener(new OnClickListener() {

			//@Override
			//public void onClick(View v) {
				// TODO Auto-generated method stub

				//buttonScanOnClickProcess();										//Alert Dialog for selecting the BLE device
			//}
		//});
	}

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        blunoLibrary.onRequestPermissionsResultProcess(requestCode, permissions, grantResults);
    }

	@Override
	protected void onResume(){
		super.onResume();
		System.out.println("BlUNOActivity onResume");
		blunoLibrary.onResumeProcess();														//onResume Process by BlunoLibrary
	}
	
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		blunoLibrary.onActivityResultProcess(requestCode, resultCode, data);					//onActivityResult Process by BlunoLibrary
		super.onActivityResult(requestCode, resultCode, data);
	}
	
    @Override
    protected void onPause() {
        super.onPause();
        blunoLibrary.onPauseProcess();														//onPause Process by BlunoLibrary
    }
	
	protected void onStop() {
		super.onStop();
		blunoLibrary.onStopProcess();														//onStop Process by BlunoLibrary
	}
    
	@Override
    protected void onDestroy() {
        super.onDestroy();	
        blunoLibrary.onDestroyProcess();														//onDestroy Process by BlunoLibrary
    }

	@Override
	public void onConectionStateChange(BlunoLibrary.connectionStateEnum theConnectionState) {
		viewModel.setConnectionState(theConnectionState);
	}

	@Override
	public void onSerialReceived(String theString) {
		// Append to serial monitor for debugging
		viewModel.updateSerialMonitor(viewModel.serialMonitor.getValue() + theString);

		try {
			JSONObject json = new JSONObject(theString.trim());
			TelemetryData currentTelemetry = viewModel.telemetry.getValue();
			if (currentTelemetry == null) {
				currentTelemetry = new TelemetryData();
			}

			// Update telemetry data from JSON
			if (json.has("state")) {
				currentTelemetry.state = json.getString("state");
			}
			if (json.has("heading")) {
				currentTelemetry.heading = json.getInt("heading");
			}
			if (json.has("distance")) {
				currentTelemetry.setDistance(json.getInt("distance"));
			}
			if (json.has("distanceLaser")) {
				currentTelemetry.setDistanceLaser(json.getInt("distanceLaser"));
			}
			if (json.has("battery")) {
				currentTelemetry.setBattery(json.getInt("battery"));
			}
			if (json.has("speedTarget")) {
				currentTelemetry.setSpeedTarget(json.getInt("speedTarget"));
			}
			if (json.has("speedCurrent")) {
				currentTelemetry.setSpeedCurrent(json.getInt("speedCurrent"));
			}

			// Post the updated TelemetryData to LiveData
			viewModel.updateTelemetry(currentTelemetry);

		} catch (JSONException e) {
			// Not a JSON string, or malformed JSON. Log it or handle as plain text.
			Log.e("MainActivity", "JSON parsing error: " + e.getMessage() + " for string: " + theString);
		}
	}

}