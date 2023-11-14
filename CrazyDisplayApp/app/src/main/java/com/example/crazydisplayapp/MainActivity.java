package com.example.crazydisplayapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
        EditText editTextMessage = (EditText) findViewById(R.id.editTextIP);

        buttonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Log.i("info", "Dentro del boton");
                    appData.connectToWebSocket(editTextMessage.getText().toString());
                    PropertyChangeListener listenerConnection = new PropertyChangeListener() {
                        @Override
                        public void propertyChange(PropertyChangeEvent evt) {
                            Log.i("INFO", "Ha cambiado el Connected");
                        }
                    };
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error connecting to the RPI.", Toast.LENGTH_SHORT).show();
                }
                Log.i("INFO", String.valueOf(appData.connectionStatus));
            }
        });
    }
}
