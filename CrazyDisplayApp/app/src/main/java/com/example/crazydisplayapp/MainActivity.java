package com.example.crazydisplayapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Objects;
public class MainActivity extends AppCompatActivity {
    AppData appData = AppData.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonConnect = (Button) findViewById(R.id.buttonConn);
        EditText editTextIP = (EditText) findViewById(R.id.editTextIP);

        buttonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Log.i("info", "Dentro del boton");
                    appData.connectToWebSocket(editTextIP.getText().toString());
                    PropertyChangeListener listenerConnection = new PropertyChangeListener() {
                        @Override
                        public void propertyChange(PropertyChangeEvent evt) {
                            Log.i("INFO", "Ha cambiado el Connected");
                        }
                    };
                } catch (Exception e) {
                    Log.e("ERROR", Objects.requireNonNull(e.getMessage()));
                    Toast.makeText(getApplicationContext(), "Error connecting to the RPI.", Toast.LENGTH_SHORT).show();
                }

                Log.i("INFO", String.valueOf(appData.connectionStatus));

                if (appData.connectionStatus == AppData.ConnectionStatus.CONNECTED) {
                    startActivity(new Intent(MainActivity.this, WriteMessagesActivity.class));
                } else {
                    Toast.makeText(getApplicationContext(), "You have written an incorrect Wifi IP. RPI is not connected.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
