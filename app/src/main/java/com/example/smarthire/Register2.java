package com.example.smarthire;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register2 extends AppCompatActivity {
    DatabaseReference reff;
    User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_register2);

        EditText text_position,text_phoneNumber,text_email,text_address;
        Button button = (Button) findViewById(R.id.button_next);

        text_position = (EditText) findViewById(R.id.editText_position);
        text_phoneNumber = (EditText) findViewById(R.id.editText_phoneNumber);
        text_email = (EditText) findViewById(R.id.editText_email);
        text_address = (EditText) findViewById(R.id.editText_address);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = getIntent().getExtras();

                String userId = bundle.getString("Register_UserId");
                String userFullName = bundle.getString("Register_UserName");
                String passw = bundle.getString("Register_Password");
                String userPosition = text_position.getText().toString();
                String userPhoneNumber = text_phoneNumber.getText().toString();
                String userEmail =  text_email.getText().toString();
                String userAddress =  text_address.getText().toString();

                Intent intent = new Intent(getApplicationContext(), Register3.class);
                intent.putExtra("Register_UserId",userId);
                intent.putExtra("Register_Name",userFullName);
                intent.putExtra("Register_Password",passw);
                intent.putExtra("Register_position",userPosition);
                intent.putExtra("Register_contact",userPhoneNumber);
                intent.putExtra("Register_email",userEmail);
                intent.putExtra("Register_address",userAddress);
                startActivity(intent);
            }
        });








    }
}
