package com.example.hire;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class EmployeeProfile extends AppCompatActivity {

    TextView textViewProfileName,textViewProfilePosition,textViewProfilePhoneNum,textViewProfileEmail,textViewProfileAge,textViewProfileAddress,textViewProfileSkills,textViewProfileEducation;
    ImageView imageViewEmployeeProfile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_profile);
        Intent intent = getIntent();

        textViewProfileName = findViewById(R.id.textViewProfileName);
        textViewProfilePosition = findViewById(R.id.textViewProfilePosition);
        textViewProfilePhoneNum = findViewById(R.id.textViewProfilePhoneNum);
        textViewProfileEmail = findViewById(R.id.textViewProfileEmail);
        textViewProfileAddress= findViewById(R.id.textViewProfileAddress);
        textViewProfileSkills = findViewById(R.id.textViewProfileSkills);
        textViewProfileAge = findViewById(R.id.textViewProfileAge);
        textViewProfileEducation = findViewById(R.id.textViewProfileEducation);
        imageViewEmployeeProfile = findViewById(R.id.imageViewEmployeeProfile);

        String employeeName = intent.getStringExtra("EMPLOYEE_NAME");
        String employeePosition = intent.getStringExtra("EMPLOYEE_POSITION");
        String employeePhone = intent.getStringExtra("EMPLOYEE_PHONE");
        String employeeEmail= intent.getStringExtra("EMPLOYEE_EMAIL");
        String employeeAddress = intent.getStringExtra("EMPLOYEE_ADDRESS");
        int employeeAge = Integer.parseInt(intent.getStringExtra("EMPLOYEE_AGE"));
        String employeeSkills = intent.getStringExtra("EMPLOYEE_SKILLS");
        String employeeEducation = intent.getStringExtra("EMPLOYEE_EDUCATION");
        String employeeProfilePhoto = intent.getStringExtra("EMPLOYEE_PHOTO");

        Picasso.get()
                .load(employeeProfilePhoto)
                .fit()
                .centerCrop()
                .into(imageViewEmployeeProfile);

        textViewProfileName.setText(employeeName);
        textViewProfilePosition.setText(employeePosition);
        textViewProfileEmail.setText(employeeEmail);
        textViewProfileAge.setText(employeeAge);
        textViewProfilePhoneNum.setText(employeePhone);
        textViewProfileAddress.setText(employeeAddress);
        textViewProfileSkills.setText(employeeSkills);
        textViewProfileEducation.setText(employeeEducation);


    }


}
