package com.example.smarthire;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.example.smarthire.databinding.StartExtractBinding;

public class StartExtractActivity extends AppCompatActivity {

    private StartExtractBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = StartExtractBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setSupportActionBar(binding.toolbarHomePage2);
        getSupportActionBar().setTitle("Hire");

    }
}
