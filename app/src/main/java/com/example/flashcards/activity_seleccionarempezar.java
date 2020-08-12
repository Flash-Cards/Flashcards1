package com.example.flashcards;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class activity_seleccionarempezar extends AppCompatActivity {

    Button bcomenzar,mButton1;
    ListView lv1;

    public EditText mEditTextNum;
    public TextView mTextViewNum;
    int cantidad;
    public int i=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccionarempezar);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mEditTextNum = findViewById(R.id.texto_editar);
        mTextViewNum = findViewById(R.id.tiempo_muestra);
        mButton1 = findViewById(R.id.set_text);

        bcomenzar = (Button) findViewById(R.id.bcomenzar);
        lv1 = (ListView) findViewById(R.id.lv1);
        Deseleccionarpalabra();

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "Palabras", null, 1);
        SQLiteDatabase BaseDeDatabase = admin.getWritableDatabase();

        Cursor fila = BaseDeDatabase.rawQuery("select palabra from palabras", null);
        ArrayList<String> listapalabras = new ArrayList<>();
        if (fila.moveToFirst()) {

            do {
                listapalabras.add(fila.getString(0));

            } while (fila.moveToNext());


            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item_palabras, listapalabras);
            lv1.setAdapter(adapter);


            BaseDeDatabase.close();
        } else {
            Toast.makeText(this, "No se encuentran palabras", Toast.LENGTH_SHORT).show();

        }
        lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ModificarSeleccion(parent.getItemAtPosition(position).toString());




            }

        });

        bcomenzar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mTiempo = mEditTextNum.getText().toString();

                mEditTextNum.setText("");

                System.out.println(mTiempo);
                    if(cantidad>=1){
                        Intent intent = new Intent(activity_seleccionarempezar.this, activity_flashcard.class);
                        intent.putExtra("segs",mTiempo);
                        startActivity(intent);
                    }else{
                        Toast.makeText(v.getContext(),"No se seleccionaron palabras",Toast.LENGTH_SHORT).show();
                    }


            }
        });

        mButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });






    }

    public void ModificarSeleccion(String Pselect){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "Palabras", null, 1);
        SQLiteDatabase BaseDeDatabase = admin.getWritableDatabase();

        ContentValues registro = new ContentValues();
        registro.put("seleccion",1);

        cantidad = BaseDeDatabase.update("palabras",registro,"palabra='"+Pselect+"'",null);
        BaseDeDatabase.close();

        if (cantidad==1){
            Toast.makeText(this, "Se ha selecciono la palabra", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Error al seleccionar", Toast.LENGTH_SHORT).show();
        }
    }


    public void Deseleccionarpalabra(){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "Palabras", null, 1);
        SQLiteDatabase BaseDeDatabase = admin.getWritableDatabase();

        ContentValues registro = new ContentValues();
        registro.put("seleccion",0);

        cantidad = BaseDeDatabase.update("palabras",registro,"seleccion='1'",null);
        BaseDeDatabase.close();

        if (cantidad==1){
            Toast.makeText(this, "Se removieron las palabras seleccionadas", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Error al remover las palabras", Toast.LENGTH_SHORT).show();
        }
    }
}

