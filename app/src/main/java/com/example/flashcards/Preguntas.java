package com.example.flashcards;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Preguntas extends AppCompatActivity {

    Button bpregunta, bopcion1, bopcion2, bopcion3, bopcion4;

    String pathsito;
    Random opciones= new Random();
    int opcion1, opcion2, opcion3, opcion4;

    private static final String LOG_TAG = "AudioRecordTest";
    private MediaPlayer player = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preguntas);

        bpregunta = findViewById(R.id.bpregunta);
        bopcion1 = (Button) findViewById(R.id.bopcion1);
        bopcion2 = (Button) findViewById(R.id.bopcion2);
        bopcion3 = (Button) findViewById(R.id.bopcion3);
        bopcion4 = (Button) findViewById(R.id.bopcion4);

        //proceso preguntas
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "Palabras", null, 1);
        SQLiteDatabase BaseDeDatabase = admin.getWritableDatabase();

        Cursor fila = BaseDeDatabase.rawQuery("select palabra, audio from palabras ", null);
        final ArrayList<String> listapalabras = new ArrayList<>();
        final ArrayList<String> listapathsito = new ArrayList<>();

        if (fila.moveToFirst()) {
            if (fila.moveToFirst()) {
                do {
                    listapalabras.add(fila.getString(0));
                    listapathsito.add(fila.getString(1));
                } while (fila.moveToNext());



                BaseDeDatabase.close();
            } else {
                Toast.makeText(this, "No se encuentran palabras", Toast.LENGTH_SHORT).show();

            }
        }
        int npalabras=listapalabras.size();


        int pregunta=opciones.nextInt(npalabras);
        int i = 0;

        if (i < npalabras){
            //Tpregunta.setText("Cual es "+listapalabras.get(pregunta).toString()+" ?");
            pathsito=listapathsito.get(pregunta).toString();
            boolean repetir=true;
            while(repetir) {
                opcion1 = opciones.nextInt(npalabras);
                opcion2 = opciones.nextInt(npalabras);
                opcion3 = opciones.nextInt(npalabras);
                opcion4 = opciones.nextInt(npalabras);

                if ((opcion1 == pregunta || opcion2 == pregunta || opcion3 == pregunta || opcion4 == pregunta) && (opcion1!=opcion2  && opcion2!=opcion3 && opcion3!=opcion4 && opcion4 != opcion1)) {
                    bopcion1.setText(listapalabras.get(opcion1).toString());
                    bopcion2.setText(listapalabras.get(opcion2).toString());
                    bopcion3.setText(listapalabras.get(opcion3).toString());
                    bopcion4.setText(listapalabras.get(opcion4).toString());
                    break;
                }
            }

        }


        //botones
        bpregunta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player = new MediaPlayer();
                try {
                    player.setDataSource(pathsito);
                    player.prepare();
                    player.start();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "prepare() failed");
                }
            }
        });



        final String respuesta = listapalabras.get(pregunta).toString();
        bopcion1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bopcion1.getText() == respuesta){
                    bopcion1.setBackgroundColor(Color.parseColor("#18FF1F"));

                }else{
                    bopcion1.setBackgroundColor(Color.parseColor("#FF1919"));
                }

                Intent intent = new Intent(v.getContext(), Preguntas.class);
                startActivityForResult(intent, 0);
            }
        });

        bopcion2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bopcion2.getText() == respuesta){
                    bopcion2.setBackgroundColor(Color.parseColor("#18FF1F"));

                }else{
                    bopcion2.setBackgroundColor(Color.parseColor("#FF1919"));
                }
                Intent intent = new Intent(v.getContext(), Preguntas.class);
                startActivityForResult(intent, 0);
            }
        });

        bopcion3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bopcion3.getText() == respuesta){
                    bopcion3.setBackgroundColor(Color.parseColor("#18FF1F"));

                }else{
                    bopcion3.setBackgroundColor(Color.parseColor("#FF1919"));
                }

                Intent intent = new Intent(v.getContext(), Preguntas.class);
                startActivityForResult(intent, 0);
            }
        });

        bopcion4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bopcion4.getText() == respuesta){
                    bopcion4.setBackgroundColor(Color.parseColor("#18FF1F"));

                }else{
                    bopcion4.setBackgroundColor(Color.parseColor("#FF1919"));
                }

                Intent intent = new Intent(v.getContext(), Preguntas.class);
                startActivityForResult(intent, 0);
            }
        });
    }

    }
/*
    private void mPalabra() {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "Palabras", null, 1);
        SQLiteDatabase BaseDeDatabase = admin.getWritableDatabase();

        Cursor fila = BaseDeDatabase.rawQuery("select palabra, audio from palabras ", null);
        final ArrayList<String> listapalabras = new ArrayList<>();
        final ArrayList<String> listapathsito = new ArrayList<>();

        if (fila.moveToFirst()) {
            if (fila.moveToFirst()) {
                do {
                    listapalabras.add(fila.getString(0));
                    listapathsito.add(fila.getString(1));
                } while (fila.moveToNext());



                BaseDeDatabase.close();
            } else {
                Toast.makeText(this, "No se encuentran palabras", Toast.LENGTH_SHORT).show();

            }
        }
        int npalabras=listapalabras.size();
        int i = 0;

        if (i < npalabras){
            Tpregunta.setText("Cual es "+listapalabras.get(pregunta).toString()+" ?");
            boolean repetir=true;
            while(repetir) {
                opcion1 = opciones.nextInt(npalabras);
                opcion2 = opciones.nextInt(npalabras);
                opcion3 = opciones.nextInt(npalabras);
                opcion4 = opciones.nextInt(npalabras);

                if ((opcion1 == pregunta || opcion2 == pregunta || opcion3 == pregunta || opcion4 == pregunta) && (opcion1!=opcion2  && opcion2!=opcion3 && opcion3!=opcion4 && opcion4 != opcion1)) {
                    bopcion1.setText(listapalabras.get(opcion1).toString());
                    bopcion2.setText(listapalabras.get(opcion2).toString());
                    bopcion3.setText(listapalabras.get(opcion3).toString());
                    bopcion4.setText(listapalabras.get(opcion4).toString());
                    break;
                }
            }

        }
    }
  */





