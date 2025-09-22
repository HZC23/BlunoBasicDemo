package com.hzc.nonocontroller.data;

public class TelemetryData {
    // champs (exemples)
    public int heading = 0; // Placeholder for heading
    public String state = ""; // Placeholder for state
    private int distance;
    private int distanceLaser;
    private int battery; // ou Integer ou LiveData<Integer>

    // getters/setters for DataBinding
    public int getDistance() { return distance; }
    public void setDistance(int distance) { this.distance = distance; }

    public int getDistanceLaser() { return distanceLaser; }
    public void setDistanceLaser(int distanceLaser) { this.distanceLaser = distanceLaser; }

    public int getBattery() { return battery; }
    public void setBattery(int battery) { this.battery = battery; }

    private int speedTarget;

    public int getSpeedTarget() {
        return speedTarget;
    }

    public void setSpeedTarget(int speedTarget) {
        this.speedTarget = speedTarget;
    }

    private int speedCurrent;

    public int getSpeedCurrent() {
        return speedCurrent;
    }

    public void setSpeedCurrent(int speedCurrent) {
        this.speedCurrent = speedCurrent;
    }

    // si tu utilises LiveData :
    // private MutableLiveData<Integer> distance = new MutableLiveData<>();
    // public LiveData<Integer> getDistance() { return distance; }
}
