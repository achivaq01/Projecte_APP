package com.example.crazydisplayapp;

import android.annotation.SuppressLint;
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

import java.io.IOException;
import java.util.ArrayList;
public class ListImagesActivity extends AppCompatActivity {
    // Model = Taula d'imatges: utilitzem ArrayList
    ArrayList<Integer> images;
    // ArrayList per emmagatzemar el nom de cada imatge
    ArrayList<String> imageNames;
    // ArrayAdapter serà l'intermediari amb la ListView
    ArrayAdapter<Integer> adapter;
    // Enter que determina en número d'arxius PNG que es troben dins del directori 'drawable'
    int pngCount;
    // Obtenim l'instància de l'AppData
    AppData appData = AppData.getInstance();

    @SuppressLint("DiscouragedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_items);

        // Inicialitzem els models
        images = new ArrayList<Integer>();
        imageNames = new ArrayList<String>();
        // Li assignem un nom a cada imatge
        /*
        for (int i=0; i < images.length; i++) {
            items.set(i, "Image " + (i+1));
        }
        */
        // Contem el número total d'arxius PNG que hi han al diretori 'drawable'
        try {
            String[] fileNames = getAssets().list("drawable");
            if (fileNames != null) {
                pngCount = 0;
                for (String fileName : fileNames) {
                    if (fileName.toLowerCase().endsWith(".png")) {
                        pngCount++;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Afegim les imatges i els noms als seus ArrayLists corresponents
        for (int i = 0; i < pngCount; i++) {
            images.add(getResources().getIdentifier("image" + i, "drawable", getPackageName()));
            imageNames.add("Image " + (i+1));
        }

        // Inicialitzem l'ArrayAdapter amb el layout pertinent
        adapter = new ArrayAdapter<Integer>(this, R.layout.list_image, images) {
            @NonNull
            public View getView(int pos, View convertView, @NonNull ViewGroup container) {
                // GetView ens construeix el layout i hi "pinta" els valors de l'element en la posició pos
                if (convertView == null) {
                    // Inicialitzem l'element la View amb el seu layout
                    convertView = getLayoutInflater().inflate(R.layout.list_message, container, false);
                }
                // "Pintem" els valors (també quan es refresca)
                ((ImageView) convertView.findViewById(R.id.imageView)).setImageResource(getItem(pos));
                ((TextView) convertView.findViewById(R.id.imageName)).setText(imageNames.get(pos));
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
                Intent intent = new Intent(ListImagesActivity.this, WriteMessagesActivity.class);
                intent.putExtra("isLogged", true);
                startActivity(intent);
            }
        });
    }
}
