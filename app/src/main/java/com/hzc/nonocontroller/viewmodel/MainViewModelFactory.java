package com.hzc.nonocontroller.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.hzc.nonocontroller.BlunoLibrary;

public class MainViewModelFactory implements ViewModelProvider.Factory {

    private final BlunoLibrary blunoLibrary;

    public MainViewModelFactory(BlunoLibrary blunoLibrary) {
        this.blunoLibrary = blunoLibrary;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MainViewModel.class)) {
            return (T) new MainViewModel(blunoLibrary);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}