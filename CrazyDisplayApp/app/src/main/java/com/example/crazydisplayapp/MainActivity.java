package com.example.crazydisplayapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.net.URI;
import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonConnect = (Button) findViewById(R.id.buttonConn);

        buttonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("info", "Dentro del boton");
                connectToWebSocket("192.168.0.100","8888");
            }
        });
    }

    public void connectToWebSocket(String ip, String port) {
        // declaramos  valores para conectarnos al WebSocket
        // Esto mas tarde ha de ser dinamico, de momento ponemos lo valores estaticos
        String uriString = "ws://"+ip+":"+port;
        Log.i("info", "dentro de la funcion para connectar "+uriString);
        try {
            AppSocketsClient myAppSocketClient = new AppSocketsClient(new URI("ws://192.168.17.134:8888"));
            myAppSocketClient.connect();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

    }
}