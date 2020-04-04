package com.example.hire;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class ExtractedTextEdit extends AppCompatActivity {

    EditText editTextExtractedName, editTextExtractedPhone,editTextExtractedEmail,editTextExtractedAddress,editTextSkills,editTextEducation,editTextAge;
    ImageView imageViewExtractedImageEdit;
    FloatingActionButton fab,fabEditPhoto;
    Intent intent;
    private static final int EDIT_EXTRACTED_TEXT_CODE = 6;
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 400;
    private static final int IMAGE_PICK_GALLERY_CODE = 1000;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    private BitmapDrawable imageBitmapDrawable;
    private Bitmap imageBitmap, tempBitmap;

    String camaraPermission[];
    String storagePermission[];

    byte[] b;
    Uri resultUri;

    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fab_for_extacted_edit);

        camaraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        editTextExtractedName = findViewById(R.id.editTextExtractedName);
        editTextExtractedPhone = findViewById(R.id.editTextExtractedPhone);
        editTextExtractedEmail = findViewById(R.id.editTextExtractedEmail);
        editTextExtractedAddress = findViewById(R.id.editTextExtractedAddress);
        editTextAge = findViewById(R.id.editTextExtractedAge);
        editTextSkills = findViewById(R.id.editTextSkills);
        editTextEducation = findViewById(R.id.editTextEducation);

        imageViewExtractedImageEdit = findViewById(R.id.imageViewExtractedImage);
        fab = findViewById(R.id.fab);
        fabEditPhoto = findViewById(R.id.fabEditPhoto);
        intent = getIntent();

        String extractedPhoneNumber = intent.getStringExtra("EXTRACTED_PHONE");
        String extractedEmail = intent.getStringExtra("EXTRACTED_EMAIL");
        String extractedName = intent.getStringExtra("EXTRACTED_NAME");
        String extractedAddress = intent.getStringExtra("EXTRACTED_ADDRESS");
        String extractedAge = intent.getStringExtra("EXTRACTED_AGE");
        String extractedSkills = intent.getStringExtra("EXTRACTED_SKILLS");
        String extractedEducation = intent.getStringExtra("EXTARCTED_EDUCATION");
        String extractedFace = intent.getStringExtra("EXTRACTED_FACE");
        resultUri = Uri.parse(extractedFace);

        editTextExtractedPhone.setText(extractedPhoneNumber);
        editTextExtractedEmail.setText(extractedEmail);
        editTextExtractedName.setText(extractedName);
        editTextExtractedAddress.setText(extractedAddress);
        editTextAge.setText(extractedAge);
        editTextSkills.setText(extractedSkills);
        editTextEducation.setText(extractedEducation);
        imageViewExtractedImageEdit.setImageURI(resultUri);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = editTextExtractedName.getText().toString();
                String newPhoneNum = editTextExtractedPhone.getText().toString();
                String newEmail = editTextExtractedEmail.getText().toString();
                String newAddress = editTextExtractedAddress.getText().toString();

                Intent intent = new Intent();
                intent.putExtra("NEW_NAME",newName);
                intent.putExtra("NEW_PHONE_NUM",newPhoneNum);
                intent.putExtra("NEW_EMAIL",newEmail);
                intent.putExtra("NEW_ADDRESS",newAddress);
                intent.putExtra("NEW_PHOTO",resultUri.toString());
                setResult(EDIT_EXTRACTED_TEXT_CODE,intent);
                finish();

            }
        });

        fabEditPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] colors = {"Camera", "Gallery"};

                AlertDialog.Builder builder = new AlertDialog.Builder(ExtractedTextEdit.this);
                builder.setTitle("Pick your options");
                builder.setItems(colors, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int option) {
                        if (option == 0){
                            Toast.makeText(getApplicationContext(), "Camera", Toast.LENGTH_SHORT).show();
                            if (!checkCameraPermission()) {
                                requestCameraPermission();
                            } else {
                                dispatchTakePictureIntent();
                            }
                        }else{
                            Toast.makeText(getApplicationContext(), "Gallery", Toast.LENGTH_SHORT).show();
                            if(!checkStoragePermission()){
                                requestStoragePermission();
                            }else{
                                pickGallery();
                            }
                        }
                        // the user clicked on colors[which]
                    }
                });
                builder.show();
            }
        });


    }

    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, camaraPermission, CAMERA_REQUEST_CODE);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            ContentValues values = new ContentValues();
            Toast.makeText(ExtractedTextEdit.this, "hi:", Toast.LENGTH_SHORT).show();
            values.put(MediaStore.Images.Media.TITLE, "NewPic");
            values.put(MediaStore.Images.Media.DESCRIPTION, "Image to Text");
            imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }

    }

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storagePermission, STORAGE_REQUEST_CODE);
    }

    private void pickGallery() {
        //intent to pick image from gallery
        Intent intent = new Intent(Intent.ACTION_PICK);
        //set intent tyoe to image
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_PICK_GALLERY_CODE);

    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case CAMERA_REQUEST_CODE:
                if(grantResults.length>0){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    Log.d("myTag","Camera:"+cameraAccepted+" Storage:"+writeStorageAccepted);
                    if(cameraAccepted && writeStorageAccepted){
                        dispatchTakePictureIntent();
                    }else{
                        Toast.makeText(ExtractedTextEdit.this,"Permission Denied!" ,Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case STORAGE_REQUEST_CODE:
                if(grantResults.length>0){
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    Log.d("myTag"," Storage:"+writeStorageAccepted);
                    if(writeStorageAccepted){
                        pickGallery();
                    }else{
                        Toast.makeText(ExtractedTextEdit.this,"Permission Denied!" ,Toast.LENGTH_SHORT).show();
                    }
                }
                break;

        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if(requestCode==IMAGE_PICK_GALLERY_CODE){
                CropImage.activity(data.getData())
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(this);
            }
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                //Bundle extras = data.getExtras();
                //imageBitmap = (Bitmap) extras.get("data");
                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(this);
                //Toast.makeText(MainActivity.this,"Error 123:" ,Toast.LENGTH_SHORT).show();

                //imageBitmap = BitmapFactory.decodeFile(currentImagePath);
                //imageViewResume.setImageBitmap(imageBitmap);
            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();//get image uri
                imageViewExtractedImageEdit.setImageURI(resultUri);
                Log.d("Image",""+resultUri);
                imageBitmapDrawable = (BitmapDrawable) imageViewExtractedImageEdit.getDrawable();
                imageBitmap = imageBitmapDrawable.getBitmap();

            }
        } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
            Toast.makeText(ExtractedTextEdit.this, "Error :", Toast.LENGTH_SHORT).show();
        }

    }

}
