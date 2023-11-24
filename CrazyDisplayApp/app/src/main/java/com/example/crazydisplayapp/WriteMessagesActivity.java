package com.example.crazydisplayapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
public class WriteMessagesActivity extends AppCompatActivity {
    boolean isLogged;
    Button buttonSendMessage, buttonSendImage, buttonView;
    AppData appData = AppData.getInstance();
    static ArrayList<JSONObject> messages = new ArrayList<JSONObject>();
    @SuppressLint("InflateParams")
    private void logInDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Log In");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_login, null);
        builder.setView(view);

        EditText editTextUser = (EditText) view.findViewById(R.id.editTextUser);
        EditText editTextPassword = (EditText) view.findViewById(R.id.editTextPassword);

        builder.setPositiveButton("Log in", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                try {
                    // Afegim l'usuari i la seva contrasenya com un objecte JSONObject
                    JSONObject user = new JSONObject();
                    user.put("platform", "Android");
                    user.put("type", "login");
                    user.put("id", appData.userId);
                    user.put("user", editTextUser.getText().toString());
                    user.put("password", editTextPassword.getText().toString());
                    Log.i("INFO", "Enviant usuari");

                    // Enviem l'usuari i la seva contrasenya al servidor
                    if (appData.socketClient != null && appData.socketClient.getConnection().isOpen()) {
                        appData.socketClient.send(user.toString());
                        Log.i("INFO", "Usuari enviat");
                        Log.i("INFO", user.toString());
                    } else {
                        Log.e("ERROR", "L'usuari no s'ha pogut enviar");
                    }

                } catch (JSONException e) {
                    Log.e("ERROR", "Error inserint l'usuari: " + e.getMessage());
                }
                appData.connectWriteMessagesToWebSocket(new WriteMessagesActivity.ConnectionCallback() {
                    @Override
                    public void onCalled() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Log.i("INFO", String.valueOf(appData.userStatus));
                                buttonSendMessage.setEnabled(appData.userStatus);
                                Log.i("INFO", String.valueOf(appData.userStatus));
                                buttonSendImage.setEnabled(appData.userStatus);
                                Log.i("INFO", String.valueOf(appData.userStatus));
                                buttonView.setEnabled(appData.userStatus);
                                Log.i("INFO", String.valueOf(appData.userStatus));
                                dialog.dismiss();
                            }
                        });
                    }
                });
            }
        });
        builder.create().show();
    }
    private void consultCustomersDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Connected customers");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
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

        // Inicialitzem en nostre booleà amb un altre que rebem de les classes 'ListMessagesActivity.java' i 'ListImages.java'
        isLogged = getIntent().getBooleanExtra("isLogged", false);

        // Botó per enviar missatges
        buttonSendMessage = (Button) findViewById(R.id.buttonSendMessage);
        // Comprovem si l'usuari s'ha pogut enregistrar
        buttonSendMessage.setEnabled(isLogged);
        buttonSendMessage.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SimpleDateFormat")
            @Override
            public void onClick(View v) {

                File file = new File(getFilesDir(), "messages.txt");
                if (!file.exists()) {
                    // Creem l'arxiu 'messages.txt' en cas de que no existeixi
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
                    message.put("type", "string");
                    message.put("text", editTextMessage.getText());
                    Log.i("INFO", "Enviant missatge");

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

                    FileOutputStream fileOutputStream = getApplicationContext().openFileOutput("messages.txt", Context.MODE_APPEND);
                    fileOutputStream.write(message.toString().getBytes()); // Escrivim les dades d'un nou contacte
                    fileOutputStream.write(System.lineSeparator().getBytes()); // Afegeim un salt de línia
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
        // Comprovem si l'usuari s'ha pogut enregistrar
        buttonSendImage.setEnabled(isLogged);
        buttonSendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WriteMessagesActivity.this, ListImagesActivity.class));
            }
        });

        // Botó per canviar a la classe 'ListMessagesActivity.java'
        buttonView = (Button) findViewById(R.id.buttonView);
        // Comprovem si l'usuari s'ha pogut enregistrar
        buttonView.setEnabled(isLogged);
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
                logInDialog();
            }
        });

        // Botó per mostrar la llista dels clients connectats
        Button buttonCustomers = (Button) findViewById(R.id.buttonCustomers);
        buttonCustomers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                consultCustomersDialog();
            }
        });
    }
    public interface ConnectionCallback {
        void onCalled();
    }
}
