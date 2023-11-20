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
    ArrayList<JSONObject> messages = new ArrayList<JSONObject>();
    private void logUserDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Log In");
        builder.setMessage("Write a user name");

        // Inserim un nom d'usuari
        EditText user = new EditText(this);
        builder.setView(user);
        builder.setPositiveButton("Next", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                try {
                    // Obrim l'arxiu 'server.json' des del directori Assets
                    InputStream inputStream = openFileInput("server.json");
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;

                    // Llegim el contingut actual de l'arxiu 'server.json'
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                    }

                    inputStream.close();
                    inputStreamReader.close();
                    bufferedReader.close();

                    // Convertim el contingut a un JSONArray
                    JSONArray jsonArray = new JSONArray(stringBuilder.toString());

                    // Comprovem si el nom d'usuari que hem inserit existeix dins l'arxiu 'server.json'
                    boolean userExists = false;

                    for (int i = 0; i < jsonArray.length(); i++) {
                        if (user.getText().toString().equals(jsonArray.getJSONObject(i).getString("user"))) {
                            userExists = true;
                            break;
                        }
                    }

                    if (userExists) {
                        logPasswordDialog(user.getText().toString());
                        dialog.dismiss();
                    } else {
                        Toast.makeText(getApplicationContext(), "The user does not exist.", Toast.LENGTH_SHORT).show();
                    }

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        builder.create().show();
    }
    private void logPasswordDialog(String user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Log In");
        builder.setMessage("Hello " + user + "! Write your password");
        // Inserim la contrasenya del nostre compte
        EditText password = new EditText(this);
        builder.setView(password);
        builder.setPositiveButton("Log in", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                try {
                    // Obrim l'arxiu 'server.json' des del directori Assets
                    InputStream inputStream = openFileInput("server.json");
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;

                    // Llegim el contingut actual de l'arxiu 'server.json'
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                    }

                    inputStream.close();
                    inputStreamReader.close();
                    bufferedReader.close();

                    // Convertim el contingut a un JSONArray
                    JSONArray jsonArray = new JSONArray(stringBuilder.toString());

                    // Comprovem si la contrassenya que hem inserit pertany a l'usuari amb el que estem intentat iniciar sessió
                    boolean validPassword = false;

                    for (int i = 0; i < jsonArray.length(); i++) {
                        if (user.equals(jsonArray.getJSONObject(i).getString("user"))) {
                            if (password.getText().toString().equals(jsonArray.getJSONObject(i).getString("password"))) {
                                validPassword = true;
                            }
                            break;
                        }
                    }

                    if (validPassword) {
                        buttonSendMessage.setEnabled(true);
                        buttonSendImage.setEnabled(true);
                        buttonView.setEnabled(true);
                        dialog.dismiss();
                    } else {
                        Toast.makeText(getApplicationContext(), "The password that you have written is not " + user + "'s one.", Toast.LENGTH_LONG).show();
                    }

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
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
                    // Obrim l'arxiu 'messages.json' des del directori intern de l'aplicació
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
                    JSONArray jsonArray;
                    if (stringBuilder.length() > 0) {
                        jsonArray = new JSONArray(stringBuilder.toString());
                    } else {
                        jsonArray = new JSONArray();
                    }

                    // Afegim el nou missatge com un objecte JSONObject
                    JSONObject message = new JSONObject();
                    message.put("platform", "Android");
                    message.put("text", editTextMessage.getText());
                    message.put("date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                    Log.i("INFO", "Enviando mensaje");

                    // Afegim el nou missatge al JSONArray
                    jsonArray.put(message);

                    // Desem el nou missatge a l'arxiu 'messages.json'
                    FileOutputStream fileOutputStream = getApplicationContext().openFileOutput("messages.json", Context.MODE_PRIVATE);
                    fileOutputStream.write(jsonArray.toString().getBytes());
                    fileOutputStream.close();

                    Log.i("INFO", "Message written to file");
                    Log.i("INFO", jsonArray.toString());

                } catch (IOException | JSONException e) {
                    Log.e("ERROR", "Error writing message to file: " + e.getMessage());
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
                File file = new File(getFilesDir(), "server.json");
                if (!file.exists()) {
                    // Creem l'arxiu 'server.json' en cas de que no existeixi
                    try {
                        file.createNewFile();
                        // Obrim l'arxiu 'server.json' des del directori intern de l'aplicació
                        InputStream inputStream = openFileInput("server.json");
                        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                        StringBuilder stringBuilder = new StringBuilder();
                        String line;

                        // Llegim el contingut actual del fitxer JSON
                        while ((line = bufferedReader.readLine()) != null) {
                            stringBuilder.append(line);
                        }

                        inputStream.close();
                        inputStreamReader.close();
                        bufferedReader.close();

                        // Convertim el contingut a un JSONArray
                        JSONArray jsonArray;
                        if (stringBuilder.length() > 0) {
                            jsonArray = new JSONArray(stringBuilder.toString());
                        } else {
                            jsonArray = new JSONArray();
                        }

                        // Afegim el nou contacte com un objecte JSONObject
                        JSONObject server = new JSONObject();
                        server.put("user", "ieti@192.168.0.21");
                        server.put("password", "ieti");
                        jsonArray.put(server);

                        // Guardem el nou contingut al fitxer JSON
                        FileOutputStream fileOutputStream = getApplicationContext().openFileOutput("server.json", Context.MODE_PRIVATE);
                        fileOutputStream.write(jsonArray.toString().getBytes());
                        fileOutputStream.close();

                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }
                logUserDialog();
            }
        });
    }
}
