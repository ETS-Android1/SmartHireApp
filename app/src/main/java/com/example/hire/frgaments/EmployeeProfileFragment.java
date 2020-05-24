package com.example.hire.frgaments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.telecom.Call;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.ceylonlabs.imageviewpopup.ImagePopup;
import com.example.hire.R;
import com.example.hire.databinding.ActivityEmployeeProfileBinding;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class EmployeeProfileFragment extends Fragment {

    private static final String ARG_KITTEN_NUMBER = "argKittenNumber";

    public static EmployeeProfileFragment newInstance(int kittenNumber) {
        Bundle args = new Bundle();
        args.putInt(ARG_KITTEN_NUMBER, kittenNumber);

        EmployeeProfileFragment fragment = new EmployeeProfileFragment();
        fragment.setArguments(args);

        return fragment;
    }

    private ActivityEmployeeProfileBinding binding;
    private ConnectivityManager connectivityManager;

    private String employeePhone, employeeName, employeePosition, employeeEmail, employeeAddress, employeeSkills, employeeEducation, employeeProfilePhoto,
            employeeResume,verify;
    private int employeeAge;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = ActivityEmployeeProfileBinding.inflate(getLayoutInflater(), container, false);

        View view = binding.getRoot();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Bundle args = getArguments();
        //int kittenNumber = args.containsKey(ARG_KITTEN_NUMBER) ? args.getInt(ARG_KITTEN_NUMBER) : 1;



        connectivityManager = (ConnectivityManager) getActivity().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);


        Bundle bundle = getArguments();

        employeeName = bundle.getString("EMPLOYEE_NAME");
        employeePosition = bundle.getString("EMPLOYEE_POSITION");
        employeePhone = bundle.getString("EMPLOYEE_PHONE");
        employeeEmail = bundle.getString("EMPLOYEE_EMAIL");
        employeeAddress = bundle.getString("EMPLOYEE_ADDRESS");
        employeeAge = bundle.getInt("EMPLOYEE_AGE", 0);
        employeeSkills = bundle.getString("EMPLOYEE_SKILLS");
        employeeEducation = bundle.getString("EMPLOYEE_EDUCATION");
        employeeProfilePhoto = bundle.getString("EMPLOYEE_PHOTO");
        employeeResume = bundle.getString("EMPLOYEE_RESUME");
        verify = bundle.getString("EMPLOYEE_VERIFY");

        if(verify.equals("verified")){
            
            binding.buttonProfileVerify.setText(getString(R.string.verified));
            binding.buttonProfileVerify.setClickable(false);
            binding.imageViewVerified.setVisibility(ImageView.VISIBLE);

        }


        if(employeeProfilePhoto.equals("noProfile")){
            binding.imageViewEmployeeProfile.setImageResource(R.drawable.ic_person);

        }else{
            Picasso.get()
                    .load(employeeProfilePhoto)
                    .fit()
                    .centerCrop()
                    .into(binding.imageViewEmployeeProfile, new Callback() {
                        @Override
                        public void onSuccess() {
                            //Toast.makeText(getActivity(), "Image Loaded Successfully", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(Exception e) {
                            Toast.makeText(getActivity(), "Error loading image", Toast.LENGTH_SHORT).show();
                        }
                    });
        }


        //binding.imageViewEmployeeProfile.setImageURI(Uri.parse(employeeProfilePhoto));

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

        binding.fabResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inflateDialogResume();
            }
        });

        binding.buttonProfileVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyEmployee();
            }
        });


    }

    private void verifyEmployee(){

        binding.buttonProfileVerify.setText(getString(R.string.verified));
        binding.buttonProfileVerify.setClickable(false);
        binding.imageViewVerified.setVisibility(ImageView.VISIBLE);
    }

    private void inflateDialogResume() {
        Toast.makeText(getActivity(), "Loading resume", Toast.LENGTH_SHORT).show();
        final ImagePopup imagePopup = new ImagePopup(getActivity());
        //imagePopup.setWindowHeight(800);
        imagePopup.setFullScreen(true);
        imagePopup.setBackgroundColor(Color.WHITE);
        //imagePopup.setWindowWidth(650);
        imagePopup.setImageOnClickClose(true);
        if(employeeResume!=" "){
            imagePopup.initiatePopupWithPicasso(employeeResume);
        }else{
            imagePopup.initiatePopup(getResources().getDrawable(R.drawable.error));
        }
        imagePopup.viewPopup();
        /*AlertDialog.Builder imageDialog = new AlertDialog.Builder(getActivity());
        //ImageView showImage = new ImageView(getActivity());
        //imageDialog.setTitle("Resume");
        View dialogLayout = getLayoutInflater().inflate(R.layout.dialog_resume, null);
        ImageView imageView = dialogLayout.findViewById(R.id.imageViewDialogResume);
        imageView.setImageURI(Uri.parse(employeeProfilePhoto));
        imageDialog.setView(dialogLayout);
        imageDialog.show();*/


    }

    private void callIntent() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + employeePhone));
        startActivity(intent);

    }

    private void messageIntent() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, employeeEmail);
        intent.putExtra(Intent.EXTRA_TEXT, "Hi, " + employeeName + ". ");
        intent.setType("text/plain");
        startActivity(intent);

    }


}
