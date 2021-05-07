package com.example.flashcards;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Preguntas extends AppCompatActivity {
    private Handler mhandler = new Handler();
    Button bpregunta, bopcion1, bopcion2, bopcion3, bopcion4,bsalir;
    ImageView imagen;
    String pathsito;
    byte [] imageBytes;
    Random opciones= new Random();
    int opcion1, opcion2, opcion3, opcion4;
    private SoundPool soundPool;
    private int soundError;

    private static final String LOG_TAG = "AudioRecordTest";
    private MediaPlayer player = null;
    final ArrayList<String> listapalabras = new ArrayList<>();
    final ArrayList<String> listapathsito = new ArrayList<>();
    final ArrayList<Bitmap> imagenes = new ArrayList<>();
    String respuesta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preguntas);

        bpregunta = findViewById(R.id.bpregunta);
        bopcion1 = (Button) findViewById(R.id.bopcion1);
        bopcion2 = (Button) findViewById(R.id.bopcion2);
        bopcion3 = (Button) findViewById(R.id.bopcion3);
        bopcion4 = (Button) findViewById(R.id.bopcion4);
        bsalir = findViewById(R.id.btn_salir);
        imagen = (ImageView) findViewById(R.id.imagen);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(1)
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else {
            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        }

        soundError = soundPool.load(this, R.raw.error_sound, 1);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //proceso preguntas
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "Palabras", null, 2);
        SQLiteDatabase BaseDeDatabase = admin.getWritableDatabase();

        Cursor fila = BaseDeDatabase.rawQuery("select palabra, audio, imagen from palabras where seleccion=1", null);


        if (fila.moveToFirst()) {
            if (fila.moveToFirst()) {
                do {
                    listapalabras.add(fila.getString(0));
                    listapathsito.add(fila.getString(1));
                    imageBytes = fila.getBlob(2);
                    Bitmap objectBitmap = BitmapFactory.decodeByteArray(imageBytes,0, imageBytes.length);
                    imagenes.add(objectBitmap);
                } while (fila.moveToNext());
                BaseDeDatabase.close();
            } else {
                Toast.makeText(this, "No se encuentran palabras", Toast.LENGTH_SHORT).show();
            }
        }
        int npalabras=listapalabras.size();


        final int pregunta=opciones.nextInt(npalabras);
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

                if ((opcion1 == pregunta || opcion2 == pregunta || opcion3 == pregunta || opcion4 == pregunta) && (opcion1!=opcion2  && opcion1!=opcion3 && opcion2!=opcion3 && opcion3!=opcion4 && opcion4 != opcion1 && opcion4!=opcion2)) {
                    bopcion1.setText(listapalabras.get(opcion1).toString());
                    bopcion2.setText(listapalabras.get(opcion2).toString());
                    bopcion3.setText(listapalabras.get(opcion3).toString());
                    bopcion4.setText(listapalabras.get(opcion4).toString());
                    break;
                }
            }

        }

        // Keeps the option buttons unable from being pressed before listening to the word(audio) first.
        bopcion1.setEnabled(false);
        bopcion2.setEnabled(false);
        bopcion3.setEnabled(false);
        bopcion4.setEnabled(false);

        // Button to play the audio of the word and enabling the option buttons.
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
                bopcion1.setEnabled(true);
                bopcion2.setEnabled(true);
                bopcion3.setEnabled(true);
                bopcion4.setEnabled(true);
            }
        });

        respuesta = listapalabras.get(pregunta).toString();

        bsalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), activity_antesComenzar.class);
                startActivity(intent);
            }
        });

        final MediaPlayer player2 = MediaPlayer.create(this, Uri.parse(pathsito));

        bopcion1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bopcion1.getText() == respuesta){
                    bopcion1.setBackgroundResource(R.drawable.green);
                    bopcion1.setTextColor(Color.parseColor("#ffffff"));
                    player2.start();
                    player.stop();
                    player.reset();
                    player.release();
                    player = null;
                    imagen.setImageBitmap(imagenes.get(pregunta));
                    bopcion1.setEnabled(false);
                    bopcion2.setEnabled(false);
                    bopcion3.setEnabled(false);
                    bopcion4.setEnabled(false);
                    mhandler.postDelayed(siguiente,4000);
                }else{
                    bopcion1.setBackgroundResource(R.drawable.red);
                    bopcion1.setTextColor(Color.parseColor("#ffffff"));
                    soundPool.play(soundError, (float) 0.3, (float) 0.3, 0, 0, (float) .85);
                    bopcion1.setEnabled(false);
                    bopcion2.setEnabled(false);
                    bopcion3.setEnabled(false);
                    bopcion4.setEnabled(false);
                    mhandler.postDelayed(siguiente,2000);
                }
                //bopcion1.setBackgroundColor(Color.parseColor("#000000"));

            }
        });

        bopcion2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bopcion2.getText() == respuesta){
                    bopcion2.setBackgroundResource(R.drawable.green);
                    bopcion2.setTextColor(Color.parseColor("#ffffff"));
                    player2.start();
                    player.stop();
                    player.reset();
                    player.release();
                    player = null;
                    imagen.setImageBitmap(imagenes.get(pregunta));
                    bopcion1.setEnabled(false);
                    bopcion2.setEnabled(false);
                    bopcion3.setEnabled(false);
                    bopcion4.setEnabled(false);
                    mhandler.postDelayed(siguiente,4000);
                }else{
                    bopcion2.setBackgroundResource(R.drawable.red);
                    bopcion2.setTextColor(Color.parseColor("#ffffff"));
                    soundPool.play(soundError, (float) 0.3, (float) 0.3, 0, 0, (float) .85);
                    bopcion1.setEnabled(false);
                    bopcion2.setEnabled(false);
                    bopcion3.setEnabled(false);
                    bopcion4.setEnabled(false);
                    mhandler.postDelayed(siguiente,2000);
                }
                //bopcion2.setBackgroundColor(Color.parseColor("#000000"));
            }
        });

        bopcion3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bopcion3.getText() == respuesta){
                    bopcion3.setBackgroundResource(R.drawable.green);
                    bopcion3.setTextColor(Color.parseColor("#ffffff"));
                    player2.start();
                    player.stop();
                    player.reset();
                    player.release();
                    player = null;
                    imagen.setImageBitmap(imagenes.get(pregunta));
                    bopcion1.setEnabled(false);
                    bopcion2.setEnabled(false);
                    bopcion3.setEnabled(false);
                    bopcion4.setEnabled(false);
                    mhandler.postDelayed(siguiente,4000);
                }else{
                    bopcion3.setBackgroundResource(R.drawable.red);
                    bopcion3.setTextColor(Color.parseColor("#ffffff"));
                    soundPool.play(soundError, (float) 0.3, (float) 0.3, 0, 0, (float) .85);
                    bopcion1.setEnabled(false);
                    bopcion2.setEnabled(false);
                    bopcion3.setEnabled(false);
                    bopcion4.setEnabled(false);
                    mhandler.postDelayed(siguiente,2000);
                }
                //bopcion3.setBackgroundColor(Color.parseColor("#000000"));

            }
        });

        bopcion4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bopcion4.getText() == respuesta){
                    bopcion4.setBackgroundResource(R.drawable.green);
                    bopcion4.setTextColor(Color.parseColor("#ffffff"));
                    player2.start();
                    player.stop();
                    player.reset();
                    player.release();
                    player = null;
                    imagen.setImageBitmap(imagenes.get(pregunta));
                    bopcion1.setEnabled(false);
                    bopcion2.setEnabled(false);
                    bopcion3.setEnabled(false);
                    bopcion4.setEnabled(false);
                    mhandler.postDelayed(siguiente,4000);
                }else{
                    bopcion4.setBackgroundResource(R.drawable.red);
                    bopcion4.setTextColor(Color.parseColor("#ffffff"));
                    soundPool.play(soundError, (float) 0.3, (float) 0.3, 0, 0, (float) .85);
                    bopcion1.setEnabled(false);
                    bopcion2.setEnabled(false);
                    bopcion3.setEnabled(false);
                    bopcion4.setEnabled(false);
                    mhandler.postDelayed(siguiente,2000);
                }
                //bopcion4.setBackgroundColor(Color.parseColor("#000000"));


            }
        });
    }


    private Runnable siguiente = new Runnable() {
        @Override
        public void run() {
            Intent intent = new Intent(getBaseContext(), Preguntas.class);
            startActivity(intent);
            finish();
        }
    };

    public void onDestroy() {
        super.onDestroy();
        soundPool.release();
        soundPool = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAndRemoveTask();
        }
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





