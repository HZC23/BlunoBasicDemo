package com.hzc.nonocontroller.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.hzc.nonocontroller.BlunoLibrary;

public class CalibrationViewModelFactory implements ViewModelProvider.Factory {

    private final BlunoLibrary blunoLibrary;

    public CalibrationViewModelFactory(BlunoLibrary blunoLibrary) {
        this.blunoLibrary = blunoLibrary;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(CalibrationViewModel.class)) {
            return (T) new CalibrationViewModel(blunoLibrary);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
