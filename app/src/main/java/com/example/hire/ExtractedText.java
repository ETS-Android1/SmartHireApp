package com.example.hire;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ExtractedText extends AppCompatActivity {

    TextView textViewExtractedPhone;
    TextView textViewExtractedEmail;
    TextView textViewExtractedName;
    TextView textViewExtractedAddress;
    ImageView imageViewExtractedImage;
    FloatingActionButton fab,fab1,fab2;
    Animation fabOpen, fabClose, rotateForward, rotateBackward;
    boolean isOpen = false;
    Intent intent;
    String extractedPhoneNumber,extractedEmail,extractedName,extractedAddress;
    private static final int EDIT_EXTRACTED_TEXT_CODE = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fab_for_extacted);

        textViewExtractedPhone = findViewById(R.id.editTextExtractedPhone);
        textViewExtractedEmail = findViewById(R.id.editTextExtractedEmail);
        textViewExtractedName = findViewById(R.id.editTextExtractedName);
        textViewExtractedAddress = findViewById(R.id.editTextExtractedAddress);
        imageViewExtractedImage = findViewById(R.id.imageViewExtractedImage);

        fab = findViewById(R.id.fab);
        fab1 = findViewById(R.id.fabCamera);
        fab2 = findViewById(R.id.fabGallery);

        fabOpen = AnimationUtils.loadAnimation(this,R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(this,R.anim.fab_close);
        rotateForward = AnimationUtils.loadAnimation(this,R.anim.rotate_forward);
        rotateBackward = AnimationUtils.loadAnimation(this,R.anim.rotate_backward);

        intent = getIntent();

        extractedPhoneNumber = intent.getStringExtra("EXTRACTED_PHONE");
        extractedEmail = intent.getStringExtra("EXTRACTED_EMAIL");
        extractedName = intent.getStringExtra("EXTRACTED_NAME");
        extractedAddress = intent.getStringExtra("EXTRACTED_ADDRESS");
        final Bitmap bitmap = intent.getParcelableExtra("EXTRACTED_FACE");

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
                Intent intent2 = new Intent(ExtractedText.this,ExtractedTextEdit.class);
                intent2.putExtra("EXTRACTED_NAME",extractedName);
                intent2.putExtra("EXTRACTED_PHONE",extractedPhoneNumber);
                intent2.putExtra("EXTRACTED_EMAIL",extractedEmail);
                intent2.putExtra("EXTRACTED_ADDRESS",extractedAddress);
                intent2.putExtra("EXTRACTED_FACE",bitmap);

                startActivityForResult(intent2,EDIT_EXTRACTED_TEXT_CODE);
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
            //fabExtract.startAnimation(fabClose);
            fab1.setClickable(false);
            fab2.setClickable(false);
            //fabExtract.setClickable(false);
            isOpen=false;
        }else{
            fab.startAnimation(rotateForward);
            fab1.startAnimation(fabOpen);
            fab2.startAnimation(fabOpen);
            //fabExtract.startAnimation(fabOpen);
            fab1.setClickable(true);
            fab2.setClickable(true);
            //fabExtract.setClickable(true);
            isOpen=true;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if(requestCode==EDIT_EXTRACTED_TEXT_CODE)
        {
            String newName =data.getStringExtra("NEW_NAME");
            String newPhoneNum =data.getStringExtra("NEW_PHONE_NUM");
            String newEmail =data.getStringExtra("NEW_EMAIL");
            String newAddress =data.getStringExtra("NEW_ADDRESS");
            //final Bitmap bitmap = data.getParcelableExtra("NEW_PHOTO");
            Bundle extras = data.getExtras();
            //byte[] b = extras.getByteArray("NEW_PHOTO");
            String newPhoto = data.getStringExtra("NEW_PHOTO");
            Uri photoUri = Uri.parse(newPhoto);
            //Bitmap bmp = BitmapFactory.decodeByteArray(b, 0, b.length);


            textViewExtractedPhone.setText(newPhoneNum);
            textViewExtractedEmail.setText(newEmail);
            textViewExtractedName.setText(newName);
            textViewExtractedAddress.setText(newAddress);
            imageViewExtractedImage.setImageURI(photoUri);
        }
    }
}
