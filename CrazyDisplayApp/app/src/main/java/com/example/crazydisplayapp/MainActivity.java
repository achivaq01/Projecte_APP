package com.example.crazydisplayapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;


public class MainActivity extends AppCompatActivity {
    AppData appData = AppData.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonConnect = (Button) findViewById(R.id.buttonConn);
        Button buttonSend = (Button) findViewById(R.id.buttonSend);

        EditText editTextMessage = (EditText) findViewById(R.id.editTextMessage);

        buttonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("info", "Dentro del boton");
                appData.connectToWebSocket("192.168.0.21","8888");
                PropertyChangeListener listenerConnection = new PropertyChangeListener() {
                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                        Log.i("INFO", "Ha cambiado el Connected");
                    }
                };

                Log.i("INFO", String.valueOf(appData.connectionStatus));
            }
        });

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject message = new JSONObject();
                try {
                    message.put("platform", "Android");
                    message.put("text", editTextMessage.getText());
                    Log.i("info", "enviando mensaje");
                    appData.socketClient.send(message.toString());
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }
        });
    }
}