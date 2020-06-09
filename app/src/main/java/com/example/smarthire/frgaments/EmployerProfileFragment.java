package com.example.smarthire.frgaments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smarthire.databinding.ActivityEmployeeProfileBinding;
import com.example.smarthire.databinding.FragmentProfileBinding;
import android.content.SharedPreferences;
import android.content.Context;

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
