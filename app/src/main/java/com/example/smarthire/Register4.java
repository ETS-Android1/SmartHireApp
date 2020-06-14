package com.example.smarthire;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.Calendar;

import edmt.dev.edmtdevcognitiveface.Contract.Face;

public class Register4 extends AppCompatActivity {
    DatabaseReference reff;
    Button btnConfirm;
    User user;
    ImageView imageView;

    boolean bool_image;

    private static final int PICK_IMAGE = 100;
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 400;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    String []cameraPermission;
    String []storagePermission;

    TextView textView_error;

    Uri imageUri,profileUri,photoDownloadUri;

    private StorageReference mStorageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_register4);

        Button buttonGallery,buttonCamera;

        Button button = (Button) findViewById(R.id.button_next);
        buttonGallery = findViewById(R.id.button_gallery);
        buttonCamera = findViewById(R.id.button_camera);

        imageView = findViewById(R.id.imageView_registerPicture);

        textView_error = findViewById(R.id.textView_registerPicError);

        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    registerUser(new FirebaseCallback() {
                        @Override
                        public void onCallback(Uri profileUriCallBack) {
                            photoDownloadUri = profileUriCallBack;
                            uploadUserData();
                        }
                    });

                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
                }


            }
        });

        buttonGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();

            }
        });

        buttonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                //Open Camera

                if (checkCameraPermission()) {
                    dispatchTakePictureIntent();
                }

            }

        });



    }

    private void uploadUserData() {
        Bundle bundle = getIntent().getExtras();
        String userId = bundle.getString("Register_UserId");
        String userFullName = bundle.getString("Register_UserName");
        String passw = bundle.getString("Register_Password");
        String userPosition = bundle.getString("Register_position");
        String userPhoneNumber = bundle.getString("Register_contact");
        String userEmail =  bundle.getString("Register_email");
        String userAddress =  bundle.getString("Register_address");
        String dobDay = bundle.getString("Register_birthDay");
        String dobMonth = bundle.getString("Register_birthMonth");
        String dobYear = bundle.getString("Register_birthYear");


        user = new User();

        user.setUserId(userId);
        user.setPassword(passw);
        user.setName(userFullName);
        user.setPosition(userPosition);
        user.setContact(userPhoneNumber);
        user.setEmail(userEmail);
        user.setAddress(userAddress);
        user.setBirthDay(dobDay);
        user.setBirthMonth(dobMonth);
        user.setBirthYear(dobYear);
        user.setFaceImage(photoDownloadUri.toString());
        reff = FirebaseDatabase.getInstance().getReference().child("User");
        reff.push().setValue(user);
        Toast.makeText(Register4.this,"Successfully registered!",Toast.LENGTH_LONG).show();

        Intent intent = new Intent(getApplicationContext(), Login.class);
        startActivity(intent);
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery,PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data ){
        super.onActivityResult(requestCode,resultCode,data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            imageUri = data.getData();
            profileUri = imageUri;
            imageView.setImageURI(imageUri);
            bool_image = true;
            //imageView.setImageURI(imageUri);
            //bool_image = true;
        }

        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            profileUri = imageUri;
            imageView.setImageURI(imageUri);
            bool_image = true;
            //imageView.setImageURI(imageUri);
            //bool_image = true;
        }





    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storagePermission, STORAGE_REQUEST_CODE);
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermission, CAMERA_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {

        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        if (!result1) {

            requestStoragePermission();

        }
        if (!result) {
            requestCameraPermission();
        }

        return result && result1;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            ContentValues values = new ContentValues();

            values.put(MediaStore.Images.Media.TITLE, "NewPic");
            values.put(MediaStore.Images.Media.DESCRIPTION, "Image to Text");
            imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }


    }

    private void showCustomToast(String msg, int length){

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast, findViewById(R.id.custom_toast_container));

        TextView text = layout.findViewById(R.id.text);
        text.setText(msg);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.BOTTOM, 0, 120);
        toast.setDuration(length);
        toast.setView(layout);
        toast.show();

    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private interface FirebaseCallback {
        void onCallback (Uri profileUriCallBack);
    }

    private void registerUser (FirebaseCallback firebaseCallback){
        if (profileUri != null) {

            mStorageRef = FirebaseStorage.getInstance().getReference("uploads");

            final StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(profileUri));

            fileReference.putFile(profileUri).continueWithTask(
                    new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException(); }
                            return fileReference.getDownloadUrl();
                        } })
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {

                                firebaseCallback.onCallback(task.getResult());

                            }
                            else {
                                //Toast.makeText(getActivity(), "upload failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();

                                showCustomToast(getString(R.string.error)+" : "+ task.getException().getMessage(), Toast.LENGTH_LONG);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            showCustomToast(e.getMessage(), Toast.LENGTH_LONG);
                            //Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        } else {
            firebaseCallback.onCallback(Uri.parse("noProfile"));
            //Toast.makeText(getActivity(), "No file selected", Toast.LENGTH_SHORT).show();
        }


    }

}
