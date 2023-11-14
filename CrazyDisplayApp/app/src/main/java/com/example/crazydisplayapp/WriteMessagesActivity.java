package com.example.crazydisplayapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
public class WriteMessagesActivity extends AppCompatActivity {
    AppData appData = AppData.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_messages);

        Button buttonSend = (Button) findViewById(R.id.buttonSend);

        EditText editTextMessage = (EditText) findViewById(R.id.editTextMessage);

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