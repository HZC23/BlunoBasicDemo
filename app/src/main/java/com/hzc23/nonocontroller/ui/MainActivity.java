package com.hzc23.nonocontroller.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import com.hzc23.nonocontroller.R;
import com.hzc23.nonocontroller.databinding.ActivityMainBinding;
import com.hzc23.nonocontroller.viewmodel.MainViewModel;

public class MainActivity extends AppCompatActivity {

    private MainViewModel viewModel;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
}