package com.hzc.nonocontroller.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import com.hzc.nonocontroller.R;
import com.hzc.nonocontroller.data.RobotRepository;
import com.hzc.nonocontroller.databinding.ActivityMainBinding;
import com.hzc.nonocontroller.viewmodel.MainViewModel;

public class MainActivity extends AppCompatActivity {

    private MainViewModel viewModel;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the RobotRepository
        RobotRepository.getInstance().init(this);

        // Inflate the layout and get an instance of the binding class.
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        // Get the ViewModel.
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        // Set the ViewModel for data binding.
        binding.setViewModel(viewModel);

        // Specify the current activity as the lifecycle owner of the binding.
        // This is used so that the binding can observe LiveData updates.
        binding.setLifecycleOwner(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RobotRepository.getInstance().destroy();
    }
}