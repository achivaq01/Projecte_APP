package com.example.crazydisplayapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Objects;
public class MainActivity extends AppCompatActivity {
    AppData appData = AppData.getInstance();

    private static final MainActivity instance = new MainActivity();
    public static MainActivity getInstance() {
        return instance;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        appData.setCurrentContext(this);

        Button buttonConnect = (Button) findViewById(R.id.buttonConn);
        EditText editTextIP = (EditText) findViewById(R.id.editTextIP);

        buttonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Log.i("INFO", "Dins del botó");

                    appData.connectMainToWebSocket(editTextIP.getText().toString(), new ConnectionCallback() {
                        @Override
                        public void onCalled() {
                            Log.i("INFO", "Ha cambiat el Connected");
                            Log.i("INFO", String.valueOf(appData.connectionStatus));
                            if (appData.connectionStatus == AppData.ConnectionStatus.CONNECTED) {
                                startActivity(new Intent(MainActivity.this, WriteMessagesActivity.class));
                            } else {
                                Toast.makeText(getApplicationContext(), "You have written an incorrect Wifi IP. RPI is not connected.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } catch (Exception e) {
                    Log.e("ERROR", Objects.requireNonNull(e.getMessage()));
                    Toast.makeText(getApplicationContext(), "Error connecting to the RPI.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public interface ConnectionCallback {
        void onCalled();
    }
}
