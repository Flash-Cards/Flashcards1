package com.example.flashcards;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class activity_cicloImagenes extends AppCompatActivity {

    private Handler mhandler = new Handler();
    TextView tflash;
    ImageView imagen;
    byte [] imageBytes;
    Button bpreguntas;
    ArrayList<String> listapalabras = new ArrayList<>();
    final ArrayList<Bitmap> imagenes = new ArrayList<>();
    int carreglo;

    Boolean repetir=true;
    //int carreglo=palabras.length;

    public int i=0;

    String mTiempoEnMilis;
    long mTiempoEnMilis2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ciclo_imagenes);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        bpreguntas= findViewById(R.id.bpreguntas);
        imagen= findViewById(R.id.imagen);

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "Palabras", null, 2);
        SQLiteDatabase BaseDeDatabase = admin.getWritableDatabase();
        //consulta
        Cursor fila = BaseDeDatabase.rawQuery("select imagen from palabras where seleccion = 1", null);
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            mTiempoEnMilis = extras.getString("segs");
        }
        long milisentrada = Long.parseLong(mTiempoEnMilis) * 1000;
        establecerTiempo(milisentrada);

        if (fila.moveToFirst()) {
            do {
                imageBytes = fila.getBlob(0);
                Bitmap objectBitmap = BitmapFactory.decodeByteArray(imageBytes,0, imageBytes.length);
                imagenes.add(objectBitmap);
            } while (fila.moveToNext());
            BaseDeDatabase.close();
            int nimagenes=imagenes.size();
            carreglo=nimagenes-1;
        } else {
            Toast.makeText(this, "No se encuentran palabras", Toast.LENGTH_SHORT).show();
        }
        //fin consulta
        bpreguntas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), PreguntasAudio.class);
                startActivity(intent);
            }
        });

        cicloImagenes();
    }

    private Runnable cambiar = new Runnable() {
        @Override
        public void run() {
            if (i<carreglo){
                i++;
                cicloImagenes();
            }
        }
    };

    public void cicloImagenes(){
        imagen.setImageBitmap(imagenes.get(i));
        if(i>=carreglo) {
            i = -1;
        }
        mhandler.postDelayed(cambiar,mTiempoEnMilis2);
    }

    public void establecerTiempo(long milisegundos) {
        mTiempoEnMilis2 = milisegundos;
    }
}
