package com.abdev.offlinephonefinder;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    DatabaseHelper db;
    EditText emailEditText, passwordEditText, confirmPasswordEditText;
    Button registerButton;
    TextView logInTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new DatabaseHelper(this);
        emailEditText = (EditText) findViewById(R.id.email);
        passwordEditText = (EditText) findViewById(R.id.password);
        confirmPasswordEditText = (EditText) findViewById(R.id.confirmPassword);
        registerButton = (Button) findViewById(R.id.register);
        logInTextView = (TextView) findViewById(R.id.logIn);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String confirmPassword = confirmPasswordEditText.getText().toString();
            }
        });
    }
}
