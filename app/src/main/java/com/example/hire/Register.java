package com.example.hire;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {
    DatabaseReference reff;
    Button btnConfirm;
    User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_register);


        final EditText txtUsername,txtPassword;
        txtUsername = (EditText) findViewById(R.id.editText_username);
        txtPassword = (EditText) findViewById(R.id.editText_password);
        btnConfirm = (Button) findViewById(R.id.button_confirmRegister);

        user = new User();

        reff = FirebaseDatabase.getInstance().getReference().child("User");
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = txtUsername.getText().toString();
                String passw =  txtPassword.getText().toString();
                user.setUserId(userId);
                user.setPassword(passw);
                user.setName("Tester");
                user.setContact("0161234567");
                user.setEmail("test@testmail.com");
                user.setFaceImage("@testURL");
                reff.push().setValue(user);
                Toast.makeText(Register.this,"Successfully registered!",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);

            }
        });
    }
}
