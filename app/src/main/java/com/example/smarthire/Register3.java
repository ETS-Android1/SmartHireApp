package com.example.smarthire;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.database.DatabaseReference;

import java.text.DateFormat;
import java.util.Calendar;

public class Register3 extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    DatabaseReference reff;
    User user;
    String storeYear,storeMonth,storeDay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_register3);

        Button button = (Button) findViewById(R.id.button_pickDOB);
        Button btnConfirm = (Button) findViewById(R.id.button_next);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(),"date picker");

            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = getIntent().getExtras();
                String userId = bundle.getString("Register_UserId");
                String userFullName = bundle.getString("Register_UserName");
                String passw = bundle.getString("Register_Password");
                String userPosition = bundle.getString("Register_position");
                String userPhoneNumber = bundle.getString("Register_contact");
                String userEmail =  bundle.getString("Register_email");
                String userAddress =  bundle.getString("Register_address");

                Intent intent = new Intent(getApplicationContext(), Register4.class);
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

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR,year);
        cal.set(Calendar.MONTH,month);
        cal.set(Calendar.DAY_OF_MONTH,dayOfMonth);
        String currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(cal.getTime());

        TextView textViewDOB = (TextView) findViewById(R.id.textView_dateOfBirth);
        textViewDOB.setText(currentDate);

        storeYear=year+ "";
        storeMonth=month+ "";
        storeDay=dayOfMonth+ "";
        Toast.makeText(getApplicationContext(),storeYear,Toast.LENGTH_LONG).show();

    }
}
