package com.example.crazydisplayapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
public class ListMessagesActivity extends AppCompatActivity {
    // Model: Message (date, text)
    public static class Message {
        public String date, text;
        public Message (String _date, String _text) {
            date = _date;
            text = _text;
        }
        @NonNull
        @Override
        public String toString() {
            return "Date: " + date + " | Text: " + text;
        }
    }
    // Model = Taula de missatges: utilitzem ArrayList
    Set<Message> messages;
    // ArrayAdapter serà l'intermediari amb la ListView
    ArrayAdapter<Message> adapter;
    // Obtenim l'instància de l'AppData
    AppData appData = AppData.getInstance();
    private void resendMessageDialog(int index) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Reenviament de missatges");
        builder.setMessage("Estàs segur de que vols reenviar aquest missatge?");
        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Afegim el missatge com un objecte JSONObject
                JSONObject message = new JSONObject();
                try {
                    message.put("platform", "Android");
                    message.put("text", WriteMessagesActivity.messages.get(index).getString("text"));
                    Log.i("INFO", "Enviando mensaje");

                    // Enviem el missatge al RPI
                    if (appData.socketClient != null && appData.socketClient.getConnection().isOpen()) {
                        appData.socketClient.send(message.toString());
                        Log.i("INFO", "Messatge enviat");
                        Log.i("INFO", message.toString());
                    } else {
                        Log.e("ERROR", "El missatge no s'ha pogut enviar");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_items);

        // Inicialitzem model
        messages = new HashSet<>();

        // Obtenim les dades de l'ArrayList de missatges
        if (!WriteMessagesActivity.messages.isEmpty()) {
            for (JSONObject message : WriteMessagesActivity.messages) {
                try {
                    // Afegim cada element de l'ArrayList de missatges al Set de missatges
                    messages.add(new Message(message.getString("date"), message.getString("text")));
                    Log.i("INFO", "Missatge afegit");
                    Log.i("INFO", messages.toString());

                } catch (JSONException e) {
                    Log.e("ERROR", "Error retrieving message fields: " + e.getMessage());
                }
            }
        } else {
            Log.i("INFO", "No messages available");
        }
        adapter = new ArrayAdapter<Message>(this, R.layout.list_message, new ArrayList<>(messages)) {
            @NonNull
            public View getView(int pos, View convertView, @NonNull ViewGroup container) {

                // GetView ens construeix el layout i hi "pinta" els valors de l'element en la posició pos
                if (convertView == null) {
                    // Inicialitzem l'element la View amb el seu layout
                    convertView = getLayoutInflater().inflate(R.layout.list_message, container, false);
                }
                // "Pintem" els valors (també quan es refresca)
                ((TextView) convertView.findViewById(R.id.message)).setText(Objects.requireNonNull(getItem(pos)).toString());
                return convertView;
            }
        };

        // Busquem la ListView i li endollem l'ArrayAdapter
        ListView lv = findViewById(R.id.messagesView);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                resendMessageDialog(position);
            }
        });

        // Botó per tornar a obrir la classe 'WriteMessagesActivity.java'
        Button buttonBack = (Button) findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ListMessagesActivity.this, WriteMessagesActivity.class));
            }
        });
    }
}
