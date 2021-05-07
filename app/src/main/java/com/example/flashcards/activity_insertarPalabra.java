package com.example.flashcards;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class activity_insertarPalabra extends AppCompatActivity {
    EditText etpalabra;
    Button binsertar, bseleccionarimagen, boton_grabar, boton_reproducir, boton_detenerg, boton_detenerr, boton_salir;
    ImageView Vimagen;

    private static final int PICK_IMAGE_REQUEST=100;
    private Uri Imagepath;
    private Bitmap imageToStore;
    private  byte[] imageInBytes;
    String palabra;
    private ByteArrayOutputStream objectByteArrayOutputStream;

    private static final String LOG_TAG = "AudioRecordTest";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static String fileName = null;
    private MediaRecorder recorder = null;
    private MediaPlayer player = null;

    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted) finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insertar_palabra);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        etpalabra = findViewById(R.id.etpalabra);
        binsertar= findViewById(R.id.binsertar);
        boton_grabar = findViewById(R.id.boton_grabar);
        boton_reproducir = findViewById(R.id.boton_reproducir);
        boton_detenerg = findViewById(R.id.boton_detenerg);
        boton_detenerr = findViewById(R.id.boton_detenerr);
        bseleccionarimagen = findViewById(R.id.bseleccionarimagen);
        Vimagen = findViewById(R.id.Vimagen);
        boton_salir = findViewById(R.id.btn_salir);

        fileName = null;


        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        boton_salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), activity_antesComenzar.class);
                startActivity(intent);
            }
        });

        binsertar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                palabra = etpalabra.getText().toString();
                if (!palabra.isEmpty() && fileName != null && Imagepath != null){
                    Insertar();
                }else{
                    Toast.makeText(activity_insertarPalabra.this, "Debe llenar todos los campos", Toast.LENGTH_SHORT).show();
                }

            }
        });

        boton_grabar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileName = getExternalCacheDir().getAbsolutePath();
                fileName += "/Audio " + System.currentTimeMillis() + ".3gp";

                recorder = new MediaRecorder();
                recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                recorder.setOutputFile(fileName);
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

                try {
                    recorder.prepare();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "prepare() failed");
                }
                Toast.makeText(activity_insertarPalabra.this, "Grabando audio", Toast.LENGTH_SHORT).show();
                recorder.start();

            }
        });

        boton_detenerg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recorder.stop();
                recorder.reset();
                recorder.release();
                recorder = null;
                System.out.println(fileName);
                System.out.println(etpalabra.getText().toString());
                Toast.makeText(activity_insertarPalabra.this, "Deteniendo grabacion", Toast.LENGTH_SHORT).show();
            }
        });

        boton_reproducir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player = new MediaPlayer();
                try {
                    player.setDataSource(fileName);
                    player.prepare();
                    player.start();
                    Toast.makeText(activity_insertarPalabra.this, "Reproduciendo audio", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "prepare() failed");
                }
            }
        });

        boton_detenerr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.release();
                player = null;
                Toast.makeText(activity_insertarPalabra.this, "Deteniendo audio", Toast.LENGTH_SHORT).show();
            }
        });

        bseleccionarimagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CargarImagen();
            }
        });



    }

    @Override
    protected void onStop() {
        super.onStop();
        if (recorder != null){
            recorder.release();
            recorder = null;
        }
        if (player != null){
            player.release();
            player = null;
        }
    }

    public void CargarImagen() {
        try{
            Intent intent = new Intent();
            intent.setType("image/*");

            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent,PICK_IMAGE_REQUEST);
        }
        catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if(requestCode==PICK_IMAGE_REQUEST && resultCode==RESULT_OK && data!=null && data.getData()!=null){
                Imagepath=data.getData();
                imageToStore = MediaStore.Images.Media.getBitmap(getContentResolver(), Imagepath);
                Vimagen.setImageBitmap(imageToStore);

                //Toast.makeText(this, ""+Imagepath.getLastPathSegment(), Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    private void Insertar() {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,"Palabras",null,2);
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();

        Bitmap imageToStoreBitmap = imageToStore;
        objectByteArrayOutputStream = new ByteArrayOutputStream();
        imageToStoreBitmap.compress(Bitmap.CompressFormat.JPEG,100,objectByteArrayOutputStream);
        imageInBytes=objectByteArrayOutputStream.toByteArray();

        if (!palabra.isEmpty() && !fileName.isEmpty() && Imagepath != null){
            ContentValues registro= new ContentValues();
            registro.put("palabra",palabra);
            registro.put("audio",fileName);
            registro.put("imagen",imageInBytes);

            BaseDeDatos.insert("palabras",null,registro);

            BaseDeDatos.close();
            etpalabra.setText(" ");
            fileName = null;
            Imagepath = null;

            Toast.makeText(this,"Se agrego la palabra",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this,"Debe llenar todos los campos",Toast.LENGTH_SHORT).show();

        }
    }
}
