package com.example.flashcards;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class activity_antesComenzar extends AppCompatActivity implements View.OnClickListener{
    private CardView insertpablabra,bconsultar,bborrar,bempezar,bimagenes;
    Button binfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_antes_comenzar);
        //Quitamos barra de notificaciones
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        insertpablabra = (CardView) findViewById(R.id.btn_agregar);
        bconsultar = (CardView) findViewById(R.id.btn_flashcards);
        bborrar = (CardView) findViewById(R.id.btn_borrar);
        bempezar = (CardView) findViewById(R.id.btn_palabras);
        bimagenes = findViewById(R.id.btn_imagenes);
        binfo = findViewById(R.id.btn_info);

        insertpablabra.setOnClickListener(this);
        bconsultar.setOnClickListener(this);
        bborrar.setOnClickListener(this);
        bempezar.setOnClickListener(this);
        bimagenes.setOnClickListener(this);

        binfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), activity_info.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent i;

        switch (v.getId()) {
            case R.id.btn_agregar : i = new Intent(this,activity_insertarPalabra.class);startActivity(i); break;
            case R.id.btn_flashcards : i = new Intent(this,activity_seleccionarempezar.class);startActivity(i); break;
            case R.id.btn_borrar : i = new Intent(this,Borrar_palabras.class);startActivity(i); break;
            case R.id.btn_palabras : i = new Intent(this,Lista_palabras.class);startActivity(i); break;
            case R.id.btn_imagenes : i = new Intent(this,activity_seleccionarImagenes.class);startActivity(i); break;
    }
}}
