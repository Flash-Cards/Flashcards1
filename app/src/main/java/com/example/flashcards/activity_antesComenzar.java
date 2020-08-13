package com.example.flashcards;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class activity_antesComenzar extends AppCompatActivity {
    Button insertpablabra,bconsultar,bborrar,bempezar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_antes_comenzar);
        //Quitamos barra de notificaciones
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        insertpablabra = (Button) findViewById(R.id.insertpalabra);
        bconsultar = (Button) findViewById(R.id.bconsultar);
        bborrar = (Button) findViewById(R.id.bborrar);
        bempezar = (Button) findViewById(R.id.bempezar);

        insertpablabra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (v.getContext(), activity_insertarPalabra.class);
                startActivityForResult(intent, 0);
            }
        });

       bconsultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (v.getContext(), Lista_palabras.class);
                startActivityForResult(intent, 0);
            }
        });

        bborrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (v.getContext(), Borrar_palabras.class);
                startActivityForResult(intent, 0);
            }
        });

        bempezar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (v.getContext(), activity_seleccionarempezar.class);
                startActivityForResult(intent, 0);
            }
        });
    }
}
