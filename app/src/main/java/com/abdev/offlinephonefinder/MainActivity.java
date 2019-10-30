package com.abdev.offlinephonefinder;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.database.Cursor;
import android.widget.RelativeLayout;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
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
    RelativeLayout relativeLayout;

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
    String codeSpecial;
    String typedPassword;
    Boolean correctPassword;
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    protected boolean gps_enabled, network_enabled;
    public boolean firstClick = false;
    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute


    public int pos = 0;
    public AlertDialog.Builder builder;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        builder  = new AlertDialog.Builder(MainActivity.this);

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
        relativeLayout = new RelativeLayout(getApplicationContext());
        LISTVIEW = (ListView) findViewById(R.id.listView1);
        ID_Array = new ArrayList<String>();
        FEATURE_Array = new ArrayList<String>();
        CODE_Array = new ArrayList<String>();
        databaseHelper = new DatabaseHelper(this);


        LISTVIEW.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long id) {
                pos = position;
                build(position).show();
                    }
                //}

                //
//                if (correctPassword){
//                    Intent intent = new Intent(getApplicationContext(), EditCode.class);
//                    intent.putExtra("ListViewClickedItemValue", ListViewClickItemArray.get(position).toString());
//                    startActivity(intent);
//                }else{
//                    Toast.makeText(MainActivity.this, "Wrong password, try again", Toast.LENGTH_SHORT).show();
//                    Toast.makeText(MainActivity.this, typedPassword, Toast.LENGTH_SHORT).show();
//                }

            //}
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
            if(messageBody.contains(" ")){
                codeSpecial = messageBody.substring(5, messageBody.indexOf(" "));
            }

            if(code.equalsIgnoreCase(locCode)){
                if (network_enabled) {

                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                }else{
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                }


            }else if(codeSpecial.equalsIgnoreCase(contactCode)){
                //Toast.makeText(this, "hereee", Toast.LENGTH_SHORT).show();
                String name= messageBody.substring(messageBody.indexOf(" ")+1);
                smsManager.sendTextMessage(Sender, null, ""+fetchContacts(name), null, null);
                getIntent().removeExtra("sender");
                getIntent().removeExtra("message");
                this.finishAffinity();
            }else if(code.equalsIgnoreCase(ringCode)){
                AudioManager audio_mngr = (AudioManager) getBaseContext().getSystemService(Context.AUDIO_SERVICE);
                audio_mngr .setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                this.finishAffinity();
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

    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onLocationChanged(Location location) {
        //Toast.makeText(this, "notworked", Toast.LENGTH_SHORT).show();
        Log.e("location change","location");
        Intent i = getIntent();
        double lat = location.getLatitude();
        String sender = getIntent().getStringExtra("sender");
        String message =getIntent().getStringExtra("message");



            smsManager.sendTextMessage(sender, null, "http://maps.google.com/maps?q="+location.getLatitude()+","+location.getLongitude(), null, null);
        //this.finishAffinity();
//        finish();
//        System.exit(0);
        stopLocationUpdates();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void stopLocationUpdates(){
        locationManager.removeUpdates(this);
        //this.finishAffinity();
        finish();
        System.exit(0);
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

    public AlertDialog build(final int position){
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            builder.setTitle("Enter Password");
            builder.setMessage("Please enter your password");
            builder.setView(input);


        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Editable typedPasswordE = input.getText();
                typedPassword = typedPasswordE.toString();
                SharedPreferences preferences = getSharedPreferences("prefs", MODE_PRIVATE);
                String password = preferences.getString("password", null);
                //int pos = position;

                if (typedPassword.equals(password)){
                        Intent intent = new Intent(getApplicationContext(), EditCode.class);
                        intent.putExtra("ListViewClickedItemValue", ListViewClickItemArray.get(position).toString());
                        startActivity(intent);


                }else{

                   Toast.makeText(MainActivity.this, "Wrong password, try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        final AlertDialog passwordAlert = builder.create();
        return passwordAlert;
    }

    private void checkAndRequestPermissions() {
        int permissionSendMessage = ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS);

        int receiveSMS = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECEIVE_SMS);

        int readContact = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS);

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

        if (readContact != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_CONTACTS);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                    101);

        }

    }

    public String fetchContacts(String Name) {

        String phoneNumber = null;


        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String _ID = ContactsContract.Contacts._ID;
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

        Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;



        StringBuffer output = new StringBuffer();

        ContentResolver contentResolver = getContentResolver();

        Cursor cursor = contentResolver.query(CONTENT_URI, null,null, null, null);

        // Loop for every contact in the phone
        if (cursor.getCount() > 0) {

            while (cursor.moveToNext()) {

                String contact_id = cursor.getString(cursor.getColumnIndex( _ID ));
                String name = cursor.getString(cursor.getColumnIndex( DISPLAY_NAME ));

                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex( HAS_PHONE_NUMBER )));

                if (hasPhoneNumber > 0) {
                    if(name.equalsIgnoreCase(Name)){
                        Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[] { contact_id }, null);

                        while (phoneCursor.moveToNext()) {
                            phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                            return phoneNumber;

                        }

                        phoneCursor.close();
                    }


                    // Query and loop for every phone number of the contact


                }

            }

        }
        return "Contact not found.";
    }
}
