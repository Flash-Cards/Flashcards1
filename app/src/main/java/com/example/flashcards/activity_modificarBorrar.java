package com.example.flashcards;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class activity_modificarBorrar extends AppCompatActivity {
    EditText etpalabra;
    ImageView Vimagen;
    Button boton_reproducir,boton_grabar,boton_detenerg,boton_detenerr,bseleccionarimagen,binsertar,beliminar,boton_salir;
    private static final String LOG_TAG = "AudioRecordTest";
    private MediaPlayer player = null;
    String pathaudio;
    String palabraUpdate;
    byte [] imageBytes;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static String fileName = null;
    private MediaRecorder recorder = null;
    private static final int PICK_IMAGE_REQUEST=100;
    private Uri Imagepath;
    private Bitmap imageToStore;
    private ByteArrayOutputStream objectByteArrayOutputStream;
    private  byte[] imageInBytes;
    String mpalabra;
    String palabra;

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
        setContentView(R.layout.activity_modificar_borrar);
        etpalabra = (EditText) findViewById(R.id.etpalabra);
        Vimagen = (ImageView)  findViewById(R.id.Vimagen);
        boton_grabar = findViewById(R.id.boton_grabar);
        boton_reproducir = findViewById(R.id.boton_reproducir);
        boton_detenerg = findViewById(R.id.boton_detenerg);
        boton_detenerr = findViewById(R.id.boton_detenerr);
        bseleccionarimagen = findViewById(R.id.bseleccionarimagen);
        binsertar= findViewById(R.id.binsertar);
        beliminar= findViewById(R.id.beliminar);
        boton_salir = findViewById(R.id.btn_salir);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mpalabra = getIntent().getStringExtra("palabra");

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,"Palabras",null,2);
        SQLiteDatabase BaseDeDatabase = admin.getWritableDatabase();

        Cursor fila = BaseDeDatabase.rawQuery("select palabra, audio, imagen from palabras where palabra='"+mpalabra+"'",null);

        if(fila.moveToFirst()){
            etpalabra.setText(fila.getString(0));
            pathaudio=fila.getString(1);
            imageBytes=fila.getBlob(2);
            Bitmap objetcBitmap = BitmapFactory.decodeByteArray(imageBytes,0, imageBytes.length );
            Vimagen.setImageBitmap(objetcBitmap);
            BaseDeDatabase.close();

        }else{
            Toast.makeText(this,"No se encuentran palabras",Toast.LENGTH_SHORT).show();

        }

        fileName=pathaudio;

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
                Toast.makeText(activity_modificarBorrar.this, "Grabando audio", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(activity_modificarBorrar.this, "Deteniendo grabacion", Toast.LENGTH_SHORT).show();
            }
        });


        boton_reproducir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnreproducir(pathaudio);
            }
        });

        boton_detenerr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.release();
                player = null;
                Toast.makeText(activity_modificarBorrar.this, "Deteniendo audio", Toast.LENGTH_SHORT).show();
            }
        });



        bseleccionarimagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CargarImagen();
            }
        });

        binsertar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                palabra= etpalabra.getText().toString();
                if (!palabra.isEmpty() && fileName != null && Imagepath != null){
                    Modificar();
                }else{

                    Toast.makeText(activity_modificarBorrar.this, "Debe llenar todos los campos", Toast.LENGTH_SHORT).show();
                }

            }
        });

        beliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alerta = new AlertDialog.Builder(activity_modificarBorrar.this);
                alerta.setMessage("¿Desea eliminar la palabra?")
                        .setCancelable(false)
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                eliminar(mpalabra);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });

                AlertDialog titulo = alerta.create();
                titulo.setTitle("Aviso");
                titulo.show();

            }
        });

        boton_salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(),activity_antesComenzar.class);
                startActivity(intent);
            }
        });
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

    public void btnreproducir(String pathsito){
        player = new MediaPlayer();
        try {
            player.setDataSource(pathsito);
            player.prepare();
            player.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    private void Modificar() {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,"Palabras",null,2);
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();

        Bitmap imageToStoreBitmap = imageToStore;

        objectByteArrayOutputStream = new ByteArrayOutputStream();
        imageToStoreBitmap.compress(Bitmap.CompressFormat.JPEG,100,objectByteArrayOutputStream);

        imageInBytes=objectByteArrayOutputStream.toByteArray();

        String palabra=etpalabra.getText().toString();

        if (palabra == mpalabra && !fileName.isEmpty() && imageBytes == imageInBytes){
            Toast.makeText(this,"No ha modificado ningun dato",Toast.LENGTH_SHORT).show();
        }

        if (!palabra.isEmpty() && !fileName.isEmpty() && Imagepath != null){
            ContentValues registro= new ContentValues();
            registro.put("palabra",palabra);
            registro.put("audio",fileName);
            registro.put("imagen",imageInBytes);

            int cant=BaseDeDatos.update("palabras",registro,"palabra='"+mpalabra+"'",null);

            BaseDeDatos.close();
            etpalabra.setText(" ");
            fileName = null;
            Imagepath = null;

            Toast.makeText(this,"Se agrego la palabra",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this,"Debe llenar todos los campos",Toast.LENGTH_SHORT).show();

        }
    }

    public void eliminar(String pa){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,"Palabras",null,2);
        SQLiteDatabase BaseDeDatabase = admin.getWritableDatabase();

        int cant = BaseDeDatabase.delete("palabras", "palabra='" + pa+"'", null);
        BaseDeDatabase.close();



        if(cant==1){
            Toast.makeText(this,"Se borro con exito",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent (this, activity_antesComenzar.class);
            startActivityForResult(intent, 0);
        }


        else{
            Toast.makeText(this,"Fallo al eliminar",Toast.LENGTH_SHORT).show();
        }


    }
}