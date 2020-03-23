package com.example.hire;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class ExtractedText extends AppCompatActivity {

    TextView textViewExtractedPhone;
    TextView textViewExtractedEmail;
    TextView textViewExtractedName;
    TextView textViewExtractedAddress;
    ImageView imageViewExtractedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extracted_text);
        textViewExtractedPhone = findViewById(R.id.textViewExtractedPhone);
        textViewExtractedEmail = findViewById(R.id.textViewExtractedEmail);
        textViewExtractedName = findViewById(R.id.textViewExtractedName);
        textViewExtractedAddress = findViewById(R.id.textViewExtractedAddress);
        imageViewExtractedImage = findViewById(R.id.imageViewExtractedImage);

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
        //bitmap.recycle();


        //Intent i = getIntent();
        //Bundle b =i.getBundleExtra("EXTRACTED_TEXT");
        //String extractedText = b.getString("BundleText");
        //textViewExtractedText.setText(extractedText);

    }
}
