package com.example.hire.frgaments;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.hire.BottomNavigationActivity;
import com.example.hire.databinding.ActivityEmployeeProfileBinding;
import com.example.hire.databinding.FragmentProfileBinding;
import android.content.SharedPreferences;
import android.content.Context;

import com.example.hire.R;

public class EmployerProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(getLayoutInflater(), container, false);

        View view = binding.getRoot();

        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences sharedPref = requireActivity().getSharedPreferences("PREF",Context.MODE_PRIVATE);


        binding.textViewProfileName.setText(sharedPref.getString("USER_ID",""));


    }

}
