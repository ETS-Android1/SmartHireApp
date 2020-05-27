package com.example.hire;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.hire.databinding.ActivityAboutUsBinding;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class AboutUs extends AppCompatActivity implements View.OnClickListener {

    private ActivityAboutUsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAboutUsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setSupportActionBar(binding.toolbarAboutUs);
        getSupportActionBar().setTitle("About Us");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //set up up button

        Picasso.get()
                .load("https://raw.githubusercontent.com/yujune/Hire/master/screenshots/hire_easy.jpeg")
                .fit()
                .centerCrop()
                .into(binding.imageViewAboutUs, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(AboutUs.this, "Error loading image", Toast.LENGTH_SHORT).show();

                    }
                });

        Picasso.get()
                .load("https://raw.githubusercontent.com/yujune/Hire/master/screenshots/teeyujune.JPG")
                .fit()
                .centerCrop()
                .into(binding.imageViewTeam1, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {

                        Toast.makeText(AboutUs.this, "Error loading image", Toast.LENGTH_SHORT).show();

                    }
                });

        Picasso.get()
                .load("https://raw.githubusercontent.com/yujune/Hire/master/screenshots/pohchongsien.jpeg")
                .fit()
                .centerCrop()
                .into(binding.imageViewTeam2, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {

                        Toast.makeText(AboutUs.this, "Error loading image", Toast.LENGTH_SHORT).show();

                    }
                });

        binding.buttonAboutUs.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent(AboutUs.this, BottomNavigationActivity.class);
        startActivity(intent);
    }
}
