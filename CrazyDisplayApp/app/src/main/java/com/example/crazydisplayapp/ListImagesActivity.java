package com.example.crazydisplayapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
public class ListImagesActivity extends AppCompatActivity {
    // Model = Taula d'imatges: utilitzem ArrayList
    ArrayList<Integer> images;
    // ArrayAdapter serà l'intermediari amb la ListView
    ArrayAdapter<Integer> adapter;
    // Obtenim l'instància de l'AppData
    AppData appData = AppData.getInstance();

    @SuppressLint("DiscouragedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_items);
        appData.setCurrentContext(this);

        // Inicialitzem model
        images = new ArrayList<Integer>();

        // Afegim totes l'id de totes les imatges de la ruta res/drawable
        images.add(R.drawable.image1);
        images.add(R.drawable.image2);
        images.add(R.drawable.image3);
        images.add(R.drawable.image4);
        images.add(R.drawable.image5);
        images.add(R.drawable.image6);

        // Inicialitzem l'ArrayAdapter amb el layout pertinent
        adapter = new ArrayAdapter<Integer>(this, R.layout.list_image, images) {
            @SuppressLint("SetTextI18n")
            @NonNull
            public View getView(int pos, View convertView, @NonNull ViewGroup container) {
                // GetView ens construeix el layout i hi "pinta" els valors de l'element en la posició pos
                if (convertView == null) {
                    // Inicialitzem l'element la View amb el seu layout
                    convertView = getLayoutInflater().inflate(R.layout.list_image, container, false);
                }
                // "Pintem" els valors (també quan es refresca)
                ((ImageView) convertView.findViewById(R.id.imageView)).setImageResource(getItem(pos));
                ((TextView) convertView.findViewById(R.id.textView)).setText(getItem(pos));
                return convertView;
            }
        };

        // Busquem la ListView i li endollem l'ArrayAdapter
        ListView lv = findViewById(R.id.messagesView);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Creem un ArrayList a on introduirem les imatges convertides a base 64
                ArrayList<String> base64Images = new ArrayList<>();

                for (Integer image : images) {
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), image);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] imageBytes = baos.toByteArray();
                    String base64String = Base64.encodeToString(imageBytes, Base64.NO_WRAP);
                    base64Images.add(base64String);
                }

                // Afegim la imatge convertida a base 64 com un objecte JSONObject
                JSONObject image = new JSONObject();
                try {
                    image.put("type", "image");
                    image.put("img", base64Images.get(position));
                    Log.i("INFO", "Enviant imatge");

                    // Enviem el la imatge convertida a base 64 al RPI
                    if (appData.socketClient != null && appData.socketClient.getConnection().isOpen()) {
                        appData.socketClient.send(image.toString());
                        Log.i("INFO", "Imatge enviada");
                        Log.i("INFO", image.toString());
                    } else {
                        Log.e("ERROR", "El missatge no s'ha pogut enviar");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

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
