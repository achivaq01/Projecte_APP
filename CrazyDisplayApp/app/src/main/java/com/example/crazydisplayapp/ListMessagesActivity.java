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
    ArrayList<Message> messages;
    // ArrayAdapter serà l'intermediari amb la ListView
    ArrayAdapter<Message> adapter;
    // Obtenim l'instància de l'AppData
    AppData appData = AppData.getInstance();
    private void resendMessageDialog(int index) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Resending messages");
        builder.setMessage("Are you sure you want to resend this message?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Afegim el missatge com un objecte JSONObject
                JSONObject message = new JSONObject();
                try {
                    message.put("platform", "Android");
                    message.put("type", "string");
                    message.put("text", messages.get(index).text);
                    Log.i("INFO", "Enviant missatge");

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
        appData.setCurrentContext(this);

        // Inicialitzem model
        messages = new ArrayList<Message>();

        try {
            // Obrim l'arxiu 'messages.txt' des del directori Assets
            InputStream inputStream = openFileInput("messages.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line;

            // Afegim cada línia de l'arxiu a l'ArrayList de missatges
            while ((line = bufferedReader.readLine()) != null) {
                // Convertim cada línia de l'arxiu a un JSONObject
                JSONObject message = new JSONObject(line);
                // Comprovem si el missatge actual està repetit
                boolean isRepeated = false;
                if (messages.size() > 0) {
                    for (int i = 0; i < messages.size(); i++) {
                        if (message.getString("text").equals(messages.get(i).text)) {
                            isRepeated = true;
                        }
                    }
                }
                if (!isRepeated) {
                    messages.add(new Message(message.getString("date"), message.getString("text")));
                }
            }

            inputStream.close();
            inputStreamReader.close();
            bufferedReader.close();

        } catch (IOException | JSONException e) {
            e.printStackTrace();
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
                Intent intent = new Intent(ListMessagesActivity.this, WriteMessagesActivity.class);
                intent.putExtra("isLogged", true);
                startActivity(intent);
            }
        });
    }
}
