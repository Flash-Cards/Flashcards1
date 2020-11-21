package com.example.flashcards;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Lista_palabras extends AppCompatActivity {
    ListView lv1;
    Button boton_salir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_palabras);
        lv1=(ListView)findViewById(R.id.lv1);
        boton_salir = findViewById(R.id.btn_salir);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        boton_salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), activity_antesComenzar.class);
                startActivity(intent);
            }
        });


            AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,"Palabras",null,1);
            SQLiteDatabase BaseDeDatabase = admin.getWritableDatabase();

            Cursor fila = BaseDeDatabase.rawQuery("select palabra from palabras",null);
            ArrayList<String> listapalabras = new ArrayList<>();
            if(fila.moveToFirst()){
                do {
                    listapalabras.add(fila.getString(0));

                } while(fila.moveToNext());
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.list_item_palabras, listapalabras);
                lv1.setAdapter(adapter);

                BaseDeDatabase.close();
            }else{
                Toast.makeText(this,"No se encuentran palabras",Toast.LENGTH_SHORT).show();

            }

        }

    }


