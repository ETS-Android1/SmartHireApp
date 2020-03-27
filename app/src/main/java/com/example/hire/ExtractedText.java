package com.example.hire;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.w3c.dom.Text;

public class ExtractedText extends AppCompatActivity {

    TextView textViewExtractedPhone;
    TextView textViewExtractedEmail;
    TextView textViewExtractedName;
    TextView textViewExtractedAddress;
    ImageView imageViewExtractedImage;
    FloatingActionButton fab,fab1,fab2;
    Animation fabOpen, fabClose, rotateForward, rotateBackward;
    boolean isOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extracted_text);
        textViewExtractedPhone = findViewById(R.id.textViewExtractedPhone);
        textViewExtractedEmail = findViewById(R.id.textViewExtractedEmail);
        textViewExtractedName = findViewById(R.id.textViewExtractedName);
        textViewExtractedAddress = findViewById(R.id.textViewExtractedAddress);
        imageViewExtractedImage = findViewById(R.id.imageViewExtractedImage);
        fab = findViewById(R.id.fab);
        fab1 = findViewById(R.id.fab1);
        fab2 = findViewById(R.id.fab2);

        fabOpen = AnimationUtils.loadAnimation(this,R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(this,R.anim.fab_close);
        rotateForward = AnimationUtils.loadAnimation(this,R.anim.rotate_forward);
        rotateBackward = AnimationUtils.loadAnimation(this,R.anim.rotate_backward);



        //imageViewExtractedImage.setImageResource(R.mipmap.ic_launcher_round);

        String extractedPhoneNumber = getIntent().getStringExtra("EXTRACTED_PHONE");
        String extractedEmail = getIntent().getStringExtra("EXTRACTED_EMAIL");
        String extractedName = getIntent().getStringExtra("EXTRACTED_NAME");
        String extractedAddress = getIntent().getStringExtra("EXTRACTED_ADDRESS");

        Bitmap bitmap = getIntent().getParcelableExtra("EXTRACTED_FACE");
        textViewExtractedPhone.setText(extractedPhoneNumber);
        textViewExtractedEmail.setText(extractedEmail);
        textViewExtractedName.setText(extractedName);
        textViewExtractedAddress.setText(extractedAddress);
        imageViewExtractedImage.setImageBitmap(bitmap);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFab();
            }
        });

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFab();
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFab();
            }
        });

    }

    private void animateFab(){
        if (isOpen){
            fab.startAnimation(rotateBackward);
            fab1.startAnimation(fabClose);
            fab2.startAnimation(fabClose);
            //fab3.startAnimation(fabClose);
            fab1.setClickable(false);
            fab2.setClickable(false);
            //fab3.setClickable(false);
            isOpen=false;
        }else{
            fab.startAnimation(rotateForward);
            fab1.startAnimation(fabOpen);
            fab2.startAnimation(fabOpen);
            //fab3.startAnimation(fabOpen);
            fab1.setClickable(true);
            fab2.setClickable(true);
            //fab3.setClickable(true);
            isOpen=true;
        }
    }
}
