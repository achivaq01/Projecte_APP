package com.example.crazydisplayapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;
public class WriteMessagesActivity extends AppCompatActivity {
    AppData appData = AppData.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_messages);

        Button buttonSendMessage = (Button) findViewById(R.id.buttonSendMessage);
        Button buttonSendImage = (Button) findViewById(R.id.buttonSendImage);
        Button buttonView = (Button) findViewById(R.id.buttonView);
        EditText editTextMessage = (EditText) findViewById(R.id.editTextMessage);

        buttonSendMessage.setOnClickListener(new View.OnClickListener() {
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
        buttonSendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Intent(WriteMessagesActivity.this, ListImagesActivity.class);
            }
        });
        buttonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Intent(WriteMessagesActivity.this, ListMessagesActivity.class);
            }
        });
    }
}
