package com.abdev.offlinephonefinder;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.database.Cursor;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity implements LocationListener {




    DatabaseHelper databaseHelper;
    SQLiteDatabase sqLiteDatabase;
    Cursor cursor;
    Cursor cursor2;
    MyListAdapter myListAdapter;
    ListView LISTVIEW;

    ArrayList<String> ID_Array;
    ArrayList<String> FEATURE_Array;
    ArrayList<String> CODE_Array;
    ArrayList<String> codesArray = new ArrayList<String>();

    ArrayList<String> ListViewClickItemArray = new ArrayList<String>();
    String TempHolder;
    SmsManager smsManager = SmsManager.getDefault();
    Intent j;
    String Sender;
    String messageBody;
    String locCode;
    String contactCode;
    String ringCode;
    String code;
    protected LocationManager locationManager;
    protected boolean gps_enabled, network_enabled;

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkAndRequestPermissions();


        //Checking if app has run before
        //If first run, register new user.
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

        checkIntent();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // getting GPS status
        gps_enabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        // getting network status
        network_enabled = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (messageBody != null){
            //Getting codes from database
            databaseHelper = new DatabaseHelper(this);
            sqLiteDatabase = databaseHelper.getWritableDatabase();
            cursor2  = sqLiteDatabase.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_CODES + "", null);
            codesArray.clear();
            if(cursor2.moveToFirst()){
                do{
                    codesArray.add(cursor2.getString(cursor2.getColumnIndex(DatabaseHelper.COLUMN_CODES_CODE)));
                } while(cursor2.moveToNext());
            }

            locCode = codesArray.get(0);
            contactCode = codesArray.get(1);
            ringCode = codesArray.get(2);
            //Checking code in message against codes from database
            code = messageBody.substring(5);
            if(code.equalsIgnoreCase(locCode)){
                if (network_enabled) {

                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                }
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);



            }
        }

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
        //Toast.makeText(this, "notworked", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onLocationChanged(Location location) {
        //Toast.makeText(this, "notworked", Toast.LENGTH_SHORT).show();
        Log.e("location change","location");
        Intent i = getIntent();
        double lat = location.getLatitude();
        String sender = getIntent().getStringExtra("sender");
        String message =getIntent().getStringExtra("message");

//        //Getting codes from database
//        sqLiteDatabase = databaseHelper.getWritableDatabase();
//        cursor2  = sqLiteDatabase.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_CODES + "", null);
//
//        if(cursor2.moveToFirst()){
//            do{
//                codesArray.add(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CODES_CODE)));
//            } while(cursor.moveToNext());
//        }
//
//        String locCode = codesArray.get(0);
//        String contactCode = codesArray.get(1);
//        String ringCode = codesArray.get(2);
//        //Checking code in message against codes from database
//        String code = message.substring(5);
        //Toast.makeText(this, code, Toast.LENGTH_SHORT).show();
        if(code.equalsIgnoreCase(locCode))
            smsManager.sendTextMessage(sender, null, "http://maps.google.com/maps?q="+location.getLatitude()+","+location.getLongitude(), null, null);
        else if(code.equalsIgnoreCase(ringCode)){
            AudioManager audio_mngr = (AudioManager) getBaseContext().getSystemService(Context.AUDIO_SERVICE);
            audio_mngr .setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        }else{
            //Toast.makeText(this, "notworked", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    private void checkIntent() {
        j = getIntent();
        String sender = getIntent().getStringExtra("sender");
        messageBody = getIntent().getStringExtra("message");

        Sender = sender;


    }

    private void checkAndRequestPermissions() {
        int permissionSendMessage = ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS);

        int receiveSMS = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECEIVE_SMS);


        int coarseLocation = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        int fineLocation = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();

        if (receiveSMS != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECEIVE_SMS);
        }

        if (permissionSendMessage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.SEND_SMS);
        }
        if (coarseLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (fineLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                    101);

        }

    }
}
