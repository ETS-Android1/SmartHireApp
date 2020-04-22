package com.example.hire.frgaments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hire.databinding.ActivityEmployeeProfileBinding;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class EmployeeProfileFragment extends Fragment {

    private ActivityEmployeeProfileBinding binding;

    private String employeePhone,employeeName,employeePosition,employeeEmail,employeeAddress,employeeSkills,employeeEducation,employeeProfilePhoto;
    private int employeeAge;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = ActivityEmployeeProfileBinding.inflate(getLayoutInflater(),container,false);
        View view = binding.getRoot();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();

        employeeName = bundle.getString("EMPLOYEE_NAME");
        employeePosition = bundle.getString("EMPLOYEE_POSITION");
        employeePhone = bundle.getString("EMPLOYEE_PHONE");
        employeeEmail= bundle.getString("EMPLOYEE_EMAIL");
        employeeAddress = bundle.getString("EMPLOYEE_ADDRESS");
        employeeAge = bundle.getInt("EMPLOYEE_AGE",0);
        employeeSkills = bundle.getString("EMPLOYEE_SKILLS");
        employeeEducation = bundle.getString("EMPLOYEE_EDUCATION");
        employeeProfilePhoto = bundle.getString("EMPLOYEE_PHOTO");

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
