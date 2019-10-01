package com.abdev.offlinephonefinder;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.database.Cursor;
import android.widget.Toast;
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {


    //Button signIn;
    DatabaseHelper databaseHelper;
    SQLiteDatabase sqLiteDatabase;
    Cursor cursor;
    MyListAdapter myListAdapter;
    ListView LISTVIEW;

    ArrayList<String> ID_Array;
    ArrayList<String> FEATURE_Array;
    ArrayList<String> CODE_Array;

    ArrayList<String> ListViewClickItemArray = new ArrayList<String>();
    String TempHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //signIn = findViewById(R.id.next);
//        signIn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent(MainActivity.this, RegisterActivity.class);
//                startActivity(i);
//                finish();
//            }
//        });
        //Checking if app has run before
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        boolean firstStart = prefs.getBoolean("firstStart", true);
        if(firstStart){
            Intent i = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(i);
            finish();
            SharedPreferences prefs2 = getSharedPreferences("prefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs2.edit();
            editor.putBoolean("firstStart", false);
            editor.apply();
        }

        LISTVIEW = (ListView) findViewById(R.id.listView1);
        ID_Array = new ArrayList<String>();
        FEATURE_Array = new ArrayList<String>();
        CODE_Array = new ArrayList<String>();
        databaseHelper = new DatabaseHelper(this);

        LISTVIEW.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), EditCode.class);
                intent.putExtra("ListViewClickedItemValue", ListViewClickItemArray.get(position).toString());
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume(){
        ShowSQLiteDBdata();
        super.onResume();
    }

    private void ShowSQLiteDBdata(){
        sqLiteDatabase = databaseHelper.getWritableDatabase();
        cursor  = sqLiteDatabase.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_CODES + "", null);

        ID_Array.clear();
        FEATURE_Array.clear();
        CODE_Array.clear();

        if(cursor.moveToFirst()){
            do{
                ID_Array.add(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CODES_ID)));
                ListViewClickItemArray.add(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CODES_ID)));
                FEATURE_Array.add(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CODES_FEATURE)));
                CODE_Array.add(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CODES_CODE)));
            } while(cursor.moveToNext());
        }

        myListAdapter = new MyListAdapter(MainActivity.this,
                ID_Array,
                FEATURE_Array,
                CODE_Array
        );

        LISTVIEW.setAdapter(myListAdapter);
        cursor.close();
    }



}
