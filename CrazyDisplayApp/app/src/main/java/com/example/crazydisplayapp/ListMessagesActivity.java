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

import java.util.ArrayList;
public class ListMessagesActivity extends AppCompatActivity {
    // Model = Taula de missatges: utilitzem ArrayList
    ArrayList<String> messages;
    // ArrayAdapter serà l'intermediari amb la ListView
    ArrayAdapter<String> adapter;
    // Obtenim l'instància de l'AppData
    AppData appData = AppData.getInstance();
    private void resendMessageDialog(int index) {
        // intent = new Intent(MainActivity.this, HallOfFame.class);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Reenviament de missatges");
        builder.setMessage("Estàs segur de que vols reenviar aquest missatge?");
        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                appData.socketClient.send(messages.get(index));
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
        messages = new ArrayList<String>();

        // Inicialitzem l'ArrayAdapter amb el layout pertinent
        adapter = new ArrayAdapter<String>(this, R.layout.list_message, messages) {
            @NonNull
            public View getView(int pos, View convertView, @NonNull ViewGroup container) {
                // GetView ens construeix el layout i hi "pinta" els valors de l'element en la posició pos
                if (convertView == null) {
                    // Inicialitzem l'element la View amb el seu layout
                    convertView = getLayoutInflater().inflate(R.layout.list_message, container, false);
                }
                // "Pintem" els valors (també quan es refresca)
                ((TextView) convertView.findViewById(R.id.message)).setText(messages.get(pos));
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
                new Intent(ListMessagesActivity.this, WriteMessagesActivity.class);
            }
        });
    }
}
