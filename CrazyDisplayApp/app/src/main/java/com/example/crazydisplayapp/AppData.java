package com.example.crazydisplayapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
public class AppData extends AppCompatActivity {
    @SuppressLint("StaticFieldLeak")
    private static final AppData instance = new AppData();
    String userId;
    boolean userStatus;
    ArrayList<JSONObject> userList;
    ConnectionStatus connectionStatus = ConnectionStatus.DISCONNECTED;
    AppSocketsClient socketClient;
    Context currentContext;
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
            socketClient = new AppSocketsClient(new URI(uriString), WriteMessagesActivity.getInstance());
            socketClient.setMainCallback(callback);
            socketClient.connect();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
    public void connectWriteMessagesToWebSocket(WriteMessagesActivity.ConnectionCallback callback) {
        socketClient.setWriteMessagesCallback(callback);
    }
    public Context getCurrentContext() {
        return currentContext;
    }
    public void setCurrentContext(Context context) {
        currentContext = context;
    }
}
