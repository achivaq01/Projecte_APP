package com.example.crazydisplayapp;

import android.util.Log;
import java.net.URI;
import java.net.URISyntaxException;
public class AppData {
    private static final AppData instance = new AppData();
    String userId;
    boolean userStatus;
    ConnectionStatus connectionStatus = ConnectionStatus.DISCONNECTED;
    AppSocketsClient socketClient;
    public enum ConnectionStatus {
        DISCONNECTED, CONNECTED
    }
    private AppData() {
    }
    public static AppData getInstance() {
        return instance;
    }
    public void connectMainToWebSocket(String ip, MainActivity.ConnectionCallback callback) {
        // Declarem valors per connectar-nos al WebSocket
        String uriString = "ws://" + ip + ":8888";
        Log.i("INFO", "Dins de la funció per connectar " + uriString);
        try {
            socketClient = new AppSocketsClient(new URI(uriString));
            socketClient.setMainCallback(callback);
            socketClient.connect();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
    public void connectWriteMessagesToWebSocket(WriteMessagesActivity.ConnectionCallback callback) {
        socketClient.setWriteMessagesCallback(callback);
    }
}
