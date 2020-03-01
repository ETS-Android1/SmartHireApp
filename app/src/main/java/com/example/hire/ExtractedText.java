package com.example.hire;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

public class ExtractedText extends AppCompatActivity {

    TextView textViewExtractedText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extracted_text);
        textViewExtractedText = findViewById(R.id.textViewExtractedText);

        //String extractedText = getIntent().getStringExtra("EXTRACTED_TEXT");
        //textViewExtractedText.setText(extractedText);

        Intent i = getIntent();
        Bundle b =i.getBundleExtra("EXTRACTED_TEXT");
        String extractedText = b.getString("BundleText");
        textViewExtractedText.setText(extractedText);

    }
}
