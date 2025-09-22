package com.hzc.nonocontroller;

public interface BlunoListener {
    void onConnectionStateChange(BlunoLibrary.connectionStateEnum state);
    void onSerialReceived(String data);
}
