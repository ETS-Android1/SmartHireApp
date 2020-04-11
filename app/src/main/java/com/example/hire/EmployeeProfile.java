package com.example.hire;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hire.databinding.ActivityEmployeeProfileBinding;
import com.squareup.picasso.Picasso;

public class EmployeeProfile extends AppCompatActivity {

    TextView textViewProfileName,textViewProfilePosition,textViewProfilePhoneNum,textViewProfileEmail,textViewProfileAge,textViewProfileAddress,textViewProfileSkills,textViewProfileEducation;
    ImageView imageViewEmployeeProfile;
    private ActivityEmployeeProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEmployeeProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Intent intent = getIntent();
        
        String employeeName = intent.getStringExtra("EMPLOYEE_NAME");
        String employeePosition = intent.getStringExtra("EMPLOYEE_POSITION");
        String employeePhone = intent.getStringExtra("EMPLOYEE_PHONE");
        String employeeEmail= intent.getStringExtra("EMPLOYEE_EMAIL");
        String employeeAddress = intent.getStringExtra("EMPLOYEE_ADDRESS");
        int employeeAge = intent.getIntExtra("EMPLOYEE_AGE",0
        );
        String employeeSkills = intent.getStringExtra("EMPLOYEE_SKILLS");
        String employeeEducation = intent.getStringExtra("EMPLOYEE_EDUCATION");
        String employeeProfilePhoto = intent.getStringExtra("EMPLOYEE_PHOTO");

        Picasso.get()
                .load(employeeProfilePhoto)
                .fit()
                .centerCrop()
                .into(binding.imageViewEmployeeProfile);

        binding.textViewProfileName.setText(employeeName);
        binding.textViewProfilePosition.setText(employeePosition);
        binding.textViewProfileEmail.setText(employeeEmail);
        binding.textViewProfileAge.setText(Integer.toString(employeeAge));
        binding.textViewProfilePhoneNum.setText(employeePhone);
        binding.textViewProfileAddress.setText(employeeAddress);
        binding.textViewProfileSkills.setText(employeeSkills);
        binding.textViewProfileEducation.setText(employeeEducation);


    }


}
