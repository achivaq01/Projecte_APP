package com.example.crazydisplayapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
public class ListImagesActivity extends AppCompatActivity {
    // Model = Taula d'items: utilitzem ArrayList
    ArrayList<String> items;
    // ArrayAdapter serà l'intermediari amb la ListView
    ArrayAdapter<String> adapter;
    // Llista de l'id de totes les imatges de la ruta res/drawable
    // int[] images = { R.drawable.image1, R.drawable.image2, R.drawable.image3, R.drawable.image4, R.drawable.image5, R.drawable.image6, R.drawable.image7, R.drawable.image8, R.drawable.image9, R.drawable.image10 };
    // Obtenim l'instància de l'AppData
    AppData appData = AppData.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_items);

        // Inicialitzem model
        items = new ArrayList<String>();
        // Li assignem un nom a cada imatge
        /*
        for (int i=0; i < images.length; i++) {
            items.set(i, "Image " + (i+1));
        }
        */

        // Inicialitzem l'ArrayAdapter amb el layout pertinent
        adapter = new ArrayAdapter<String>(this, R.layout.list_message, items) {
            @NonNull
            public View getView(int pos, View convertView, @NonNull ViewGroup container) {
                // GetView ens construeix el layout i hi "pinta" els valors de l'element en la posició pos
                if (convertView == null) {
                    // Inicialitzem l'element la View amb el seu layout
                    convertView = getLayoutInflater().inflate(R.layout.list_message, container, false);
                }
                // "Pintem" els valors (també quan es refresca)
                // ((ImageView) convertView.findViewById(R.id.imageView)).setImageResource(getItem(pos));
                ((TextView) convertView.findViewById(R.id.imageName)).setText(getItem(pos));
                return convertView;
            }
        };

        // Busquem la ListView i li endollem l'ArrayAdapter
        ListView lv = findViewById(R.id.messagesView);
        lv.setAdapter(adapter);
        /*
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                appData.socketClient.send();
            }
        });
        */
        // Botó per tornar a obrir la classe 'WriteMessagesActivity.java'
        Button buttonBack = (Button) findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Intent(ListImagesActivity.this, WriteMessagesActivity.class);
            }
        });
    }
}
