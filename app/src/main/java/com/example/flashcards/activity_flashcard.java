package com.example.flashcards;

import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.RunnableScheduledFuture;

public class activity_flashcard extends AppCompatActivity {

    private Handler mhandler = new Handler();
    TextView tflash;
    Button bpreguntas;
    ArrayList<String> listapalabras = new ArrayList<>();
    ArrayList<String> listapathsito = new ArrayList<>();
    int carreglo;
    String pathsito;
    private static final String LOG_TAG = "AudioRecordTest";
    private MediaPlayer player = null;

    Boolean repetir=true;
    Boolean startCycle = true;
    //int carreglo=palabras.length;

    public int i=0;

    String mTiempoEnMilis;
    long mTiempoEnMilis2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcard);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        bpreguntas= findViewById(R.id.bpreguntas);
        tflash = findViewById(R.id.tflash);

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "Palabras", null, 2);
        SQLiteDatabase BaseDeDatabase = admin.getWritableDatabase();
         //consulta
        Cursor fila = BaseDeDatabase.rawQuery("select palabra, audio from palabras where seleccion = 1", null);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            mTiempoEnMilis = extras.getString("segs");
        }
        long milisentrada = Long.parseLong(mTiempoEnMilis) * 1000;
        establecerTiempo(milisentrada);

        if (fila.moveToFirst()) {
            do {
                listapalabras.add(fila.getString(0));
                listapathsito.add(fila.getString(1));
            } while (fila.moveToNext());
            BaseDeDatabase.close();
            int npalabras=listapalabras.size();
            carreglo=npalabras-1;
        } else {
            Toast.makeText(this, "No se encuentran palabras", Toast.LENGTH_SHORT).show();
        }
       //fin consulta
        bpreguntas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCycle = false;
                player.stop();
                player.reset();
                player.release();
                player = null;
                Intent intent = new Intent(v.getContext(), PreguntasAudio.class);
                startActivity(intent);
                finish();
            }
        });
       cicloflashcards();
    }

    private Runnable cambiar = new Runnable() {
        @Override
        public void run() {
            if (i<carreglo){
                i++;
                cicloflashcards();
            }
        }

    };

    public void cicloflashcards(){
                if (startCycle != false){
                    System.out.println("entrada");
                    int canpalabra = listapalabras.get(i).toString().length();
                    int tamtexto,t,tamletra=100;
                    if(canpalabra > 7){
                        tamtexto = canpalabra - 7;
                        for (t=1;t<=tamtexto;t++){
                            tamletra= tamletra-5;
                        }
                        tflash.setTextSize(tamletra);
                    }else{
                        tflash.setTextSize(tamletra);
                    }
                    tflash.setText(listapalabras.get(i).toString());
                    pathsito = listapathsito.get(i);
                    player = new MediaPlayer();
                    try {
                        player.setDataSource(pathsito);
                        player.prepare();
                        player.start();
                    } catch (IOException e) {
                        Log.e(LOG_TAG, "prepare() failed");
                    }
                    System.out.println(i);
                    if(i>=carreglo) {
                        i = -1;
                    }
                    mhandler.postDelayed(cambiar,mTiempoEnMilis2);
                }
        }


    public void establecerTiempo(long milisegundos) {
        mTiempoEnMilis2 = milisegundos;
    }


}
