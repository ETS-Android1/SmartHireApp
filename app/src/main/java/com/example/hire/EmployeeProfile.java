package com.example.hire;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class EmployeeProfile extends AppCompatActivity {

    TextView textViewName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_profile);
        Intent intent = getIntent();

        textViewName = findViewById(R.id.textViewName);
        String employee_name = intent.getStringExtra("EMPLOYEE_NAME");
        textViewName.setText(employee_name);

    }


}
