package com.example.flashcards;

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

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class activity_seleccionarImagenes extends AppCompatActivity {

    Button bcomenzar,mButton1,boton_salir;
    ListView lv1;

    public EditText mEditTextNum;
    public TextView mTextViewNum;
    int cantidad;
    public int i=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccionar_imagenes);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Deseleccionarpalabra();
        mEditTextNum = findViewById(R.id.texto_editar);
        mTextViewNum = findViewById(R.id.tiempo_muestra);
        mButton1 = findViewById(R.id.set_text);
        boton_salir = findViewById(R.id.btn_salir);

        bcomenzar = (Button) findViewById(R.id.bcomenzar);
        lv1 = (ListView) findViewById(R.id.lv1);

        cantidad=0;
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


                if((cantidad >= 1) && (!mTiempo.isEmpty())){
                    Intent intent = new Intent(activity_seleccionarImagenes.this, activity_cicloImagenes.class);
                    intent.putExtra("segs",mTiempo);
                    startActivity(intent);
                }else{
                    Toast.makeText(v.getContext(),"Debe seleccionar palabras e ingresar el tiempo",Toast.LENGTH_SHORT).show();
                }


            }
        });

        mButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        boton_salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), activity_antesComenzar.class);
                startActivity(intent);
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

        if (cantidad>=1){
            Toast.makeText(this, "Se removieron las palabras seleccionadas", Toast.LENGTH_SHORT).show();
        }
    }

    public void onResume() {
        super.onResume();
        Deseleccionarpalabra();
        cantidad=0;
    }
}
