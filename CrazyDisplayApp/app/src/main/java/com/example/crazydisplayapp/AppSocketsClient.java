package com.example.crazydisplayapp;

import android.util.Log;
import android.widget.Toast;

import java.net.URI;
import java.util.ArrayList;
import java.util.Objects;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
public class AppSocketsClient extends WebSocketClient{
    AppData appData = AppData.getInstance();
    private MainActivity.ConnectionCallback callback_1;
    private WriteMessagesActivity.ConnectionCallback callback_2;
    /*
    public AppSocketsClient(URI serverUri) {
        super(serverUri);
    }
    */
    private final WriteMessagesActivity writeMessagesActivity;
    public AppSocketsClient(URI serverUri, WriteMessagesActivity writeMessagesActivity) {
        super(serverUri);
        this.writeMessagesActivity = writeMessagesActivity;
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
    }
    @Override
    public void onMessage(String message) {
        Log.i("infoServer", "on Message: " + message);
        try {
            String type = new JSONObject(message).getString("type");
            if (type.equals("connected")) {
                appData.userId = new JSONObject(message).getString("id");
                callback_1.onCalled();
                JSONArray list = new JSONArray(new JSONObject(message).getString("list"));
                appData.userList = new ArrayList<JSONObject>();
                for (int i = 0; i < list.length(); i++) {
                    appData.userList.add(list.getJSONObject(i));
                }

            }
            else if (type.equals("new connection")) {
                writeMessagesActivity.runOnUiThread(() -> {
                    try {
                        Toast.makeText(appData.getCurrentContext(), "New customer " + new JSONObject(message).getString("id") + " is connected!", Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        Log.e("ERROR", Objects.requireNonNull(e.getMessage()));
                    }
                });
            }
            else if (type.equals("new disconnection")) {
                writeMessagesActivity.runOnUiThread(() -> {
                    try {
                        Toast.makeText(appData.getCurrentContext(), "The customer " + new JSONObject(message).getString("id") + " has disconnected.", Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        Log.e("ERROR", Objects.requireNonNull(e.getMessage()));
                    }
                });
            }
            else if (type.equals("new message")) {
                writeMessagesActivity.runOnUiThread(() -> {
                    try {
                        Toast.makeText(appData.getCurrentContext(), new JSONObject(message).getString("id") + " has sent a new message!", Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        Log.e("ERROR", Objects.requireNonNull(e.getMessage()));
                    }
                });
            }
            else {
                if (type.equals("login") || type.equals("list")) {
                    if (type.equals("login")) {
                        appData.userStatus = new JSONObject(message).getBoolean("success");
                    }
                    else if (type.equals("list")) {
                        JSONArray list = new JSONArray(new JSONObject(message).getString("list"));
                        appData.userList = new ArrayList<JSONObject>();
                        for (int i = 0; i < list.length(); i++) {
                            appData.userList.add(list.getJSONObject(i));
                        }
                    }
                    callback_2.onCalled();
                }
            }
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
