package com.example.hire;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

public class ExtractedText extends AppCompatActivity {

    TextView textViewExtractedPhone;
    TextView textViewExtractedEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extracted_text);
        textViewExtractedPhone = findViewById(R.id.textViewExtractedPhone);
        textViewExtractedEmail = findViewById(R.id.textViewExtractedEmail);

        String extractedPhoneNumber = getIntent().getStringExtra("EXTRACTED_PHONE");
        String extractedEmail = getIntent().getStringExtra("EXTRACTED_EMAIL");
        textViewExtractedPhone.setText(extractedPhoneNumber);
        textViewExtractedEmail.setText(extractedEmail);

        //Intent i = getIntent();
        //Bundle b =i.getBundleExtra("EXTRACTED_TEXT");
        //String extractedText = b.getString("BundleText");
        //textViewExtractedText.setText(extractedText);

    }
}
