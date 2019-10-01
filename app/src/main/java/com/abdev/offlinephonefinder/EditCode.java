package com.abdev.offlinephonefinder;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class EditCode extends AppCompatActivity {

    TextView feature;
    EditText code;
    Button update;
    SQLiteDatabase sqLiteDatabase;
    DatabaseHelper databaseHelper;
    Cursor cursor;
    String IDHolder;
    String SQLiteDataBaseQueryHolder;
    SQLiteDatabase sqLiteDatabaseObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_code);

        feature = (TextView) findViewById(R.id.textViewFeature);
        code = (EditText) findViewById(R.id.editTextCode);
        update = (Button) findViewById(R.id.buttonUpdate);

        databaseHelper = new DatabaseHelper(this);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String getFeature = feature.getText().toString();
                String getCode = code.getText().toString();

                OpenSQLiteDatabase();
                SQLiteDataBaseQueryHolder = "UPDATE " + DatabaseHelper.TABLE_CODES + " SET " + DatabaseHelper.COLUMN_CODES_CODE + " = '" +getCode + "' WHERE code_id = " + IDHolder + "";
                sqLiteDatabaseObj.execSQL(SQLiteDataBaseQueryHolder);
                sqLiteDatabase.close();
                Toast.makeText(EditCode.this, "Code updated successfully", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }
    @Override
    protected void onResume(){
        ShowRecordInEditText();
        super.onResume();
    }

    public void ShowRecordInEditText(){
        sqLiteDatabase = databaseHelper.getWritableDatabase();
        IDHolder = getIntent().getStringExtra("ListViewClickedItemValue");
        cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_CODES + " WHERE code_id = " + IDHolder + "", null);

        if (cursor.moveToFirst()){
            do{
                feature.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CODES_FEATURE)));
                code.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CODES_CODE)));
            } while(cursor.moveToNext());

            cursor.close();
        }
    }

    public void OpenSQLiteDatabase(){
        sqLiteDatabaseObj = openOrCreateDatabase(DatabaseHelper.DATABASE_NAME, Context.MODE_PRIVATE, null);
    }
}
