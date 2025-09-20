package com.hzc23.nonocontroller;

import android.app.Application;

import com.hzc23.nonocontroller.data.RobotRepository;

public class NonoControllerApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        RobotRepository.getInstance().init(this);
    }
}