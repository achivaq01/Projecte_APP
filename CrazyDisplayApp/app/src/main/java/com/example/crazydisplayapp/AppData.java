package com.example.crazydisplayapp;

import android.util.Log;
import java.net.URI;
import java.net.URISyntaxException;

public class AppData {

    private static final AppData instance = new AppData();
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

    public void connectToWebSocket(String ip, String port) {
        // declaramos  valores para conectarnos al WebSocket
        // Esto mas tarde ha de ser dinamico, de momento ponemos lo valores estaticos
        String uriString = "ws://"+ip+":"+port;
        Log.i("info", "dentro de la funcion para connectar "+uriString);
        try {
            AppSocketsClient myAppSocketClient = new AppSocketsClient(new URI(uriString));
            myAppSocketClient.connect();
            socketClient = myAppSocketClient;
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

    }

}
