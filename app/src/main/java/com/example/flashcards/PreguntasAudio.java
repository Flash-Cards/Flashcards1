package com.example.flashcards;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class PreguntasAudio extends AppCompatActivity {

    private Handler mhandler = new Handler();
    private int soundError;
    private int soundCorrect;
    private SoundPool soundPool;
    Button bpregunta, bopcion1, bopcion2, bopcion3, bopcion4,bsalir,verificar;
    ImageView imagen;
    String pathsito;
    String pathOption1;
    String pathOption2;
    String pathOption3;
    String pathOption4;
    Random opciones= new Random();
    byte [] imageBytes;
    int opcion1, opcion2, opcion3, opcion4;

    private static final String LOG_TAG = "AudioRecordTest";
    private MediaPlayer player = null;
    private MediaPlayer playerOption1 = null;
    private MediaPlayer playerOption2 = null;
    private MediaPlayer playerOption3 = null;
    private MediaPlayer playerOption4 = null;

    final ArrayList<String> listapalabras = new ArrayList<>();
    final ArrayList<String> listapathsito = new ArrayList<>();
    final ArrayList<Bitmap> imagenes = new ArrayList<>();
    final ArrayList<String> firstOption = new ArrayList<>();
    final ArrayList<String> secondOption = new ArrayList<>();
    final ArrayList<String> thirdOption = new ArrayList<>();
    final ArrayList<String> fourthOption = new ArrayList<>();
    String respuesta;
    int previousWord;

    boolean audioPressed1 = false;
    boolean audioPressed2 = false;
    boolean audioPressed3 = false;
    boolean audioPressed4 = false;

    boolean firstClicked = false;
    boolean secondClicked = false;
    boolean thirdClicked = false;
    boolean fourthClicked = false;
    boolean buttonVerify = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preguntas_audio);

        bpregunta = findViewById(R.id.bpregunta);
        bopcion1 = findViewById(R.id.bopcion1);
        bopcion2 = findViewById(R.id.bopcion2);
        bopcion3 = findViewById(R.id.bopcion3);
        bopcion4 = findViewById(R.id.bopcion4);
        bsalir = findViewById(R.id.btn_salir);
        imagen = findViewById(R.id.imagen);
        verificar = findViewById(R.id.comprobar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(2)
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else {
            soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        }

        soundError = soundPool.load(this, R.raw.error_sound, 1);
        soundCorrect = soundPool.load(this, R.raw.correct_sound, 1);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //proceso preguntas
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "Palabras", null, 2);
        SQLiteDatabase BaseDeDatabase = admin.getWritableDatabase();

        Cursor fila = BaseDeDatabase.rawQuery("select palabra, audio, imagen from palabras where seleccion=1", null);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            previousWord = extras.getInt("Palabra Anterior");
        } else {
            Log.d("Debug","Intent was null");
        }

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
        int npalabras = listapalabras.size();
        int pregunta = opciones.nextInt(npalabras);

        if (previousWord == pregunta) {
            do{
                pregunta = opciones.nextInt(npalabras);
            } while (previousWord == pregunta);
        }

        int i = 0;

        if (i < npalabras){
            //Tpregunta.setText("Cual es "+listapalabras.get(pregunta).toString()+" ?");
            pathsito = listapathsito.get(pregunta).toString();
            previousWord = pregunta;
            boolean repetir = true;
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
            pathOption1 = listapathsito.get(opcion1);
            pathOption2 = listapathsito.get(opcion2);
            pathOption3 = listapathsito.get(opcion3);
            pathOption4 = listapathsito.get(opcion4);
        }

        // Shows the image of the word (audio).
        imagen.setImageBitmap(imagenes.get(pregunta));

        // Keeps the option buttons unable from being pressed before listening to the word(audio) first.
        bopcion1.setEnabled(false);
        bopcion2.setEnabled(false);
        bopcion3.setEnabled(false);
        bopcion4.setEnabled(false);
        verificar.setEnabled(false);

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

        //final MediaPlayer player2 = MediaPlayer.create(this, Uri.parse(pathsito));

        bopcion1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bopcion1.setBackgroundResource(R.drawable.blue);
                bopcion1.setTextColor(Color.parseColor("#ffffff"));
                bopcion2.setBackgroundResource(R.drawable.white);
                bopcion2.setTextColor(Color.parseColor("#000000"));
                bopcion3.setBackgroundResource(R.drawable.white);
                bopcion3.setTextColor(Color.parseColor("#000000"));
                bopcion4.setBackgroundResource(R.drawable.white);
                bopcion4.setTextColor(Color.parseColor("#000000"));
                playerOption1 = new MediaPlayer();
                try {
                    playerOption1.setDataSource(pathOption1);
                    playerOption1.prepare();
                    playerOption1.start();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "prepare() failed");
                }
                audioPressed1 = true;
                firstClicked = true;
                secondClicked = false;
                thirdClicked = false;
                fourthClicked = false;
                if (buttonVerify == false) {
                    verificar.setEnabled(true);
                }
            }
        });

        bopcion2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bopcion1.setBackgroundResource(R.drawable.white);
                bopcion1.setTextColor(Color.parseColor("#000000"));
                bopcion2.setBackgroundResource(R.drawable.blue);
                bopcion2.setTextColor(Color.parseColor("#ffffff"));
                bopcion3.setBackgroundResource(R.drawable.white);
                bopcion3.setTextColor(Color.parseColor("#000000"));
                bopcion4.setBackgroundResource(R.drawable.white);
                bopcion4.setTextColor(Color.parseColor("#000000"));
                playerOption2 = new MediaPlayer();
                try {
                    playerOption2.setDataSource(pathOption2);
                    playerOption2.prepare();
                    playerOption2.start();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "prepare() failed");
                }
                audioPressed2 = true;
                firstClicked = false;
                secondClicked = true;
                thirdClicked = false;
                fourthClicked = false;
                if (buttonVerify == false) {
                    verificar.setEnabled(true);
                }
            }
        });

        bopcion3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bopcion1.setBackgroundResource(R.drawable.white);
                bopcion1.setTextColor(Color.parseColor("#000000"));
                bopcion2.setBackgroundResource(R.drawable.white);
                bopcion2.setTextColor(Color.parseColor("#000000"));
                bopcion3.setBackgroundResource(R.drawable.blue);
                bopcion3.setTextColor(Color.parseColor("#ffffff"));
                bopcion4.setBackgroundResource(R.drawable.white);
                bopcion4.setTextColor(Color.parseColor("#000000"));
                playerOption3 = new MediaPlayer();
                try {
                    playerOption3.setDataSource(pathOption3);
                    playerOption3.prepare();
                    playerOption3.start();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "prepare() failed");
                }
                audioPressed3 = true;
                firstClicked = false;
                secondClicked = false;
                thirdClicked = true;
                fourthClicked = false;
                if (buttonVerify == false) {
                    verificar.setEnabled(true);
                }
            }
        });

        bopcion4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bopcion1.setBackgroundResource(R.drawable.white);
                bopcion1.setTextColor(Color.parseColor("#000000"));
                bopcion2.setBackgroundResource(R.drawable.white);
                bopcion2.setTextColor(Color.parseColor("#000000"));
                bopcion3.setBackgroundResource(R.drawable.white);
                bopcion3.setTextColor(Color.parseColor("#000000"));
                bopcion4.setBackgroundResource(R.drawable.blue);
                bopcion4.setTextColor(Color.parseColor("#ffffff"));
                playerOption4 = new MediaPlayer();
                try {
                    playerOption4.setDataSource(pathOption4);
                    playerOption4.prepare();
                    playerOption4.start();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "prepare() failed");
                }
                audioPressed4 = true;
                firstClicked = false;
                secondClicked = false;
                thirdClicked = false;
                fourthClicked = true;
                if (buttonVerify == false) {
                    verificar.setEnabled(true);
                }
            }
        });

        verificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bopcion1.getText() == respuesta && firstClicked == true){
                    bopcion1.setBackgroundResource(R.drawable.green);
                    bopcion1.setTextColor(Color.parseColor("#ffffff"));
                    soundPool.play(soundCorrect, (float) 0.3, (float) 0.3, 0, 0, 1);
                }
                else if (bopcion2.getText() == respuesta && secondClicked == true) {
                    bopcion2.setBackgroundResource(R.drawable.green);
                    bopcion2.setTextColor(Color.parseColor("#ffffff"));
                    soundPool.play(soundCorrect, (float) 0.3, (float) 0.3, 0, 0, 1);
                }
                else if (bopcion3.getText() == respuesta && thirdClicked == true) {
                    bopcion3.setBackgroundResource(R.drawable.green);
                    bopcion3.setTextColor(Color.parseColor("#ffffff"));
                    soundPool.play(soundCorrect, (float) 0.3, (float) 0.3, 0, 0, 1);
                }
                else if (bopcion4.getText() == respuesta && fourthClicked == true) {
                    bopcion4.setBackgroundResource(R.drawable.green);
                    bopcion4.setTextColor(Color.parseColor("#ffffff"));
                    soundPool.play(soundCorrect, (float) 0.3, (float) 0.3, 0, 0, 1);
                }
                else if (bopcion1.getText() != respuesta && firstClicked == true) {
                    bopcion1.setBackgroundResource(R.drawable.red);
                    bopcion1.setTextColor(Color.parseColor("#ffffff"));
                    soundPool.play(soundError, (float) 0.3, (float) 0.3, 0, 0, (float) .85);
                }
                else if (bopcion2.getText() != respuesta && secondClicked == true) {
                    bopcion2.setBackgroundResource(R.drawable.red);
                    bopcion2.setTextColor(Color.parseColor("#ffffff"));
                    soundPool.play(soundError, (float) 0.3, (float) 0.3, 0, 0, (float) .85);
                }
                else if (bopcion3.getText() != respuesta && thirdClicked == true) {
                    bopcion3.setBackgroundResource(R.drawable.red);
                    bopcion3.setTextColor(Color.parseColor("#ffffff"));
                    soundPool.play(soundError, (float) 0.3, (float) 0.3, 0, 0, (float) .85);
                }
                else if (bopcion4.getText() != respuesta && fourthClicked == true) {
                    bopcion4.setBackgroundResource(R.drawable.red);
                    bopcion4.setTextColor(Color.parseColor("#ffffff"));
                    soundPool.play(soundError, (float) 0.3, (float) 0.3, 0, 0, (float) .85);
                }
                verificar.setEnabled(false);
                mhandler.postDelayed(siguiente, 1500);
            }
        });
    }

    private Runnable siguiente = new Runnable() {
        @Override
        public void run() {
            Intent intent = new Intent(getBaseContext(), PreguntasAudio.class);
            intent.putExtra("Palabra Anterior", previousWord);
            startActivity(intent);
            finish();
        }
    };

    public void onDestroy() {
        super.onDestroy();
        soundPool.release();
        soundPool = null;

        if (player != null){
            player.stop();
            player.reset();
            player.release();
            player = null;
        }

        if (playerOption1 != null){
            playerOption1.stop();
            playerOption1.reset();
            playerOption1.release();
            playerOption1 = null;
        }

        if (playerOption2 != null) {
            playerOption2.stop();
            playerOption2.reset();
            playerOption2.release();
            playerOption2 = null;
        }

        if (playerOption3 != null) {
            playerOption3.stop();
            playerOption3.reset();
            playerOption3.release();
            playerOption3 = null;
        }

        if (playerOption4 != null){
            playerOption4.stop();
            playerOption4.reset();
            playerOption4.release();
            playerOption4 = null;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAndRemoveTask();
        }
    }
}
