package com.example.crazydisplayapp;

import android.util.Log;

import java.net.URI;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
public class AppSocketsClient extends WebSocketClient{
    AppData appData = AppData.getInstance();
    public AppSocketsClient(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        Log.i("infoServer", "on Open");
        appData.connectionStatus = AppData.ConnectionStatus.CONNECTED;
    }

    @Override
    public void onMessage(String message) {
        Log.i("infoServer", "on Message: "+message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.i("infoServer", "Connection closed: " + code + " " + reason);
    }

    @Override
    public void onError(Exception ex) {
        Log.i("infoServer", "ERROR: "+ex.getMessage());
    }
}
