package com.hzc.nonocontroller.data;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.hzc.nonocontroller.BR;

public class TelemetryData extends BaseObservable {

    private String state = "--";
    private int heading = 0;
    private int distance = 0;
    private int distanceLaser = 0;
    private int battery = 0;
    private int speedTarget = 0;
    private int speedCurrent = 0;

    @Bindable
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
        notifyPropertyChanged(BR.state);
    }

    @Bindable
    public int getHeading() {
        return heading;
    }

    public void setHeading(int heading) {
        this.heading = heading;
        notifyPropertyChanged(BR.heading);
    }

    @Bindable
    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
        notifyPropertyChanged(BR.distance);
    }

    @Bindable
    public int getDistanceLaser() {
        return distanceLaser;
    }

    public void setDistanceLaser(int distanceLaser) {
        this.distanceLaser = distanceLaser;
        notifyPropertyChanged(BR.distanceLaser);
    }

    @Bindable
    public int getBattery() {
        return battery;
    }

    public void setBattery(int battery) {
        this.battery = battery;
        notifyPropertyChanged(BR.battery);
    }

    @Bindable
    public int getSpeedTarget() {
        return speedTarget;
    }

    public void setSpeedTarget(int speedTarget) {
        this.speedTarget = speedTarget;
        notifyPropertyChanged(BR.speedTarget);
        notifyPropertyChanged(BR.speed);
    }

    @Bindable
    public int getSpeedCurrent() {
        return speedCurrent;
    }

    public void setSpeedCurrent(int speedCurrent) {
        this.speedCurrent = speedCurrent;
        notifyPropertyChanged(BR.speedCurrent);
    }

    @Bindable
    public int getSpeed() {
        return speedTarget;
    }
}