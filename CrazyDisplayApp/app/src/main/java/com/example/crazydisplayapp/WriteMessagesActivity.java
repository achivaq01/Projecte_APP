package com.example.crazydisplayapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
public class WriteMessagesActivity extends AppCompatActivity {
    Button buttonSendMessage, buttonSendImage, buttonView;
    AppData appData = AppData.getInstance();
    static ArrayList<JSONObject> messages = new ArrayList<JSONObject>();
    private void logUserDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Log In");
        builder.setMessage("Write a user name");

        // Inserim un nom d'usuari
        EditText user = new EditText(this);
        builder.setView(user);
        builder.setPositiveButton("Next", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                logPasswordDialog(user.getText().toString());
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
    private void logPasswordDialog(String user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Log In");
        builder.setMessage("Write your password");
        // Inserim la contrasenya del nostre compte
        EditText password = new EditText(this);
        builder.setView(password);
        builder.setPositiveButton("Log in", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                buttonSendMessage.setEnabled(true);
                buttonSendImage.setEnabled(true);
                buttonView.setEnabled(true);
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_messages);

        EditText editTextMessage = (EditText) findViewById(R.id.editTextMessage);

        // Botó per enviar missatges
        buttonSendMessage = (Button) findViewById(R.id.buttonSendMessage);
        buttonSendMessage.setEnabled(false);
        buttonSendMessage.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SimpleDateFormat")
            @Override
            public void onClick(View v) {

                File file = new File(getFilesDir(), "messages.json");
                if (!file.exists()) {
                    // Creem l'arxiu 'messages.json' en cas de que no existeixi
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                try {
                    // Afegim el nou missatge com un objecte JSONObject
                    JSONObject message = new JSONObject();
                    message.put("platform", "Android");
                    message.put("text", editTextMessage.getText());
                    Log.i("INFO", "Enviando mensaje");

                    // Enviem el nou missatge al RPI
                    if (appData.socketClient != null && appData.socketClient.getConnection().isOpen()) {
                        appData.socketClient.send(message.toString());
                        Log.i("INFO", "Messatge enviat");
                        Log.i("INFO", message.toString());
                    } else {
                        Log.e("ERROR", "El missatge no s'ha pogut enviar");
                    }

                    message.put("date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                    Log.i("INFO", "Data afegida");
                    Log.i("INFO", message.toString());

                    // Afegim el nou missatge a l'ArrayList de missatges
                    messages.add(new JSONObject(message.toString()));
                    Log.i("INFO", "Missatge afegit a l'ArrayList de missatges");
                    Log.i("INFO", messages.toString());

                    FileOutputStream fileOutputStream = getApplicationContext().openFileOutput("messages.json", Context.MODE_PRIVATE);
                    fileOutputStream.write(message.toString().getBytes());
                    fileOutputStream.close();

                    Log.i("INFO", "Missatge inserit a l'arxiu");
                    Log.i("INFO", message.toString());

                } catch (IOException | JSONException e) {
                    Log.e("ERROR", "Error inserint el missatge a l'arxiu: " + e.getMessage());
                }
            }
        });

        // Botó per canviar a la classe 'ListImagesActivity.java'
        buttonSendImage = (Button) findViewById(R.id.buttonSendImage);
        buttonSendImage.setEnabled(false);
        buttonSendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WriteMessagesActivity.this, ListImagesActivity.class));
            }
        });

        // Botó per canviar a la classe 'ListMessagesActivity.java'
        buttonView = (Button) findViewById(R.id.buttonView);
        buttonView.setEnabled(false);
        buttonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WriteMessagesActivity.this, ListMessagesActivity.class));
            }
        });
        // Botó per iniciar sessió al servidor
        Button buttonLogIn = (Button) findViewById(R.id.buttonLogIn);
        buttonLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logUserDialog();
            }
        });
    }
}
