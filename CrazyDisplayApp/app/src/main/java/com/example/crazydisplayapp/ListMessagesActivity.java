package com.example.crazydisplayapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
public class ListMessagesActivity extends AppCompatActivity {
    // Model: Message (data, text)
    public static class Message {
        public String date, text;
        public Message(String _data, String _text) {
            date = _data;
            text = _text;
        }
        @NonNull
        @Override
        public String toString() {
            return "Date: " + date + "; Text: " + text;
        }
    }
    // Model = Taula de missatges: utilitzem ArrayList
    ArrayList<Message> messages;
    // Set<Message> messages;
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
                // Ens assegurem de que l'índex es troba dins dels límits abans d'intentar recuperar el missatge
                if (index >= 0 && index < adapter.getCount()) {
                    // appData.socketClient.send(adapter.getItem(index));
                    appData.socketClient.send(messages.get(index).text);
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
        messages = new ArrayList<Message>();
        // messages = new HashSet<>();

        try {
            // Obrim l'arxiu 'messages.json' des del directori Assets
            InputStream inputStream = openFileInput("messages.json");
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();
            String line;

            // Llegim el contingut actual de l'arxiu 'messages.json'
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }

            inputStream.close();
            inputStreamReader.close();
            bufferedReader.close();

            // Convertim el contingut a un JSONArray
            JSONArray jsonArray = new JSONArray(stringBuilder.toString());

            // Afegim cada element del JSONArray al Set de missatges
            for (int i = 0; i < jsonArray.length(); i++) {
                String date = jsonArray.getJSONObject(i).getString("date");
                String text = jsonArray.getJSONObject(i).getString("text");
                messages.add(new Message(date, text));
            }

            // Ordenem l'ArrayList de missatges en funció de la seva data d'enviament
            messages.sort(new Comparator<Message>() {
                @Override
                public int compare(Message message1, Message message2) {
                    // Assuming date is in a format suitable for lexicographical comparison
                    return message1.date.compareTo(message2.date);
                }
            });

        } catch (IOException | JSONException | RuntimeException e) {
            e.printStackTrace();
        }

        // Inicialitzem l'ArrayAdapter amb el layout pertinent
        adapter = new ArrayAdapter<Message>(this, R.layout.list_message, messages) {
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
        /*
        adapter = new ArrayAdapter<String>(this, R.layout.list_message, new ArrayList<>(messages)) {
            @NonNull
            public View getView(int pos, View convertView, @NonNull ViewGroup container) {

                // GetView ens construeix el layout i hi "pinta" els valors de l'element en la posició pos
                if (convertView == null) {
                    // Inicialitzem l'element la View amb el seu layout
                    convertView = getLayoutInflater().inflate(R.layout.list_message, container, false);
                }
                // "Pintem" els valors (també quan es refresca)
                ((TextView) convertView.findViewById(R.id.message)).setText(getItem(pos));
                return convertView;
            }
        };
        */
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
