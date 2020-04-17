package com.example.hire;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
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
    private String employeePhone,employeeName,employeePosition,employeeEmail,employeeAddress,employeeSkills,employeeEducation,employeeProfilePhoto;
    private int employeeAge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEmployeeProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setSupportActionBar(binding.toolbarProfile);
        getSupportActionBar().setTitle("Hire");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();

        employeeName = intent.getStringExtra("EMPLOYEE_NAME");
        employeePosition = intent.getStringExtra("EMPLOYEE_POSITION");
        employeePhone = intent.getStringExtra("EMPLOYEE_PHONE");
        employeeEmail= intent.getStringExtra("EMPLOYEE_EMAIL");
        employeeAddress = intent.getStringExtra("EMPLOYEE_ADDRESS");
        employeeAge = intent.getIntExtra("EMPLOYEE_AGE",0
        );
        employeeSkills = intent.getStringExtra("EMPLOYEE_SKILLS");
        employeeEducation = intent.getStringExtra("EMPLOYEE_EDUCATION");
        employeeProfilePhoto = intent.getStringExtra("EMPLOYEE_PHOTO");

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

        binding.fabProfileCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callIntent();
            }
        });

        binding.fabProfileMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageIntent();
            }
        });


    }

    private void callIntent(){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:"+employeePhone));
        startActivity(intent);

    }

    private void messageIntent(){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL,employeeEmail);
        intent.putExtra(Intent.EXTRA_TEXT,"Hi, "+employeeName+". ");
        intent.setType("text/plain");
        startActivity(intent);

    }



}
