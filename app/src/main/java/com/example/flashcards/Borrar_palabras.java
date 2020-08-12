package com.example.flashcards;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class Borrar_palabras extends AppCompatActivity {


   ListView lv1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrar_palabras);
        lv1=(ListView) findViewById(R.id.lv1);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

         consulta();
        lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                eliminar(parent.getItemAtPosition(position).toString());

            }

        });

    }


    public void consulta(){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,"Palabras",null,1);
        SQLiteDatabase BaseDeDatabase = admin.getWritableDatabase();

        Cursor fila = BaseDeDatabase.rawQuery("select * from palabras",null);
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



    public void eliminar(String pa){
      AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,"Palabras",null,1);
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

