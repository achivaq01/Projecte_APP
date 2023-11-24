package com.example.crazydisplayapp;

import android.util.Log;

import java.net.URI;
import java.util.Objects;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;
public class AppSocketsClient extends WebSocketClient{
    AppData appData = AppData.getInstance();
    private MainActivity.ConnectionCallback callback_1;
    private WriteMessagesActivity.ConnectionCallback callback_2;
    public AppSocketsClient(URI serverUri) {
        super(serverUri);
    }
    public void setMainCallback(MainActivity.ConnectionCallback callback) {
        this.callback_1 = callback;
    }
    public void setWriteMessagesCallback(WriteMessagesActivity.ConnectionCallback callback) {
        this.callback_2 = callback;
    }
    @Override
    public void onOpen(ServerHandshake handshake) {
        Log.i("infoServer", "on Open");
        appData.connectionStatus = AppData.ConnectionStatus.CONNECTED;
        callback_1.onCalled();
    }
    @Override
    public void onMessage(String message) {
        Log.i("infoServer", "on Message: " + message);
        try {
            if (new JSONObject(message).getString("type").equals("connected")) {
                appData.userId = new JSONObject(message).getString("id");
                callback_1.onCalled();
            }
            else if (new JSONObject(message).getString("type").equals("login")) {
                appData.userStatus = new JSONObject(message).getBoolean("success");
                callback_2.onCalled();
            }
            /*
            else if (new JSONObject(message).getString("type").equals("list")) {
            }
            */
        } catch (JSONException e) {
            Log.e("ERROR", Objects.requireNonNull(e.getMessage()));
        }
    }
    @Override
    public void onClose(int code, String reason, boolean remote) { Log.i("infoServer", "Connection closed: " + code + " " + reason); }
    @Override
    public void onError(Exception ex) {
        Log.i("infoServer", "ERROR: " + ex.getMessage());
    }
}
