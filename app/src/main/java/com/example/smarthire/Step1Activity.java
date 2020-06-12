package com.example.smarthire;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import edmt.dev.edmtdevcognitiveface.Contract.Face;
import edmt.dev.edmtdevcognitiveface.FaceServiceClient;
import edmt.dev.edmtdevcognitiveface.FaceServiceRestClient;
import edmt.dev.edmtdevcognitiveface.Rest.ClientException;
import edmt.dev.edmtdevcognitiveface.Rest.Utils;


public class Step1Activity extends AppCompatActivity {

    ImageView imageView;
    TextView textView_error;

    Button button;
    Uri imageUri;
    Uri imageUriPass;
    Button btOpen;
    Button btConfirm;
    String []cameraPermission;
    String []storagePermission;
    boolean bool_image;
    boolean firebaseImage;
    String errorMessage;


    private static final int PICK_IMAGE = 100;
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 400;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    private final String API_KEY="298ac06ff3884863928b35b43e7d07a6";
    private final String API_LINK="https://southeastasia.api.cognitive.microsoft.com/face/v1.0/";

    private FaceServiceClient faceServiceClient = new FaceServiceRestClient(API_LINK, API_KEY);

    class CheckPhotoTaskFirebase extends AsyncTask<Bitmap,String, Integer> {
        @Override
        protected void onPostExecute(Integer integer) {
            if (integer == 1){
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference imageRef = storage.getReferenceFromUrl(imageUri.toString());

                imageRef.getBytes(4096*4096)
                        .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                                imageView.setImageBitmap(bitmap);
                                firebaseImage = true;

                            }
                        });


            } else if (integer==2||integer==3||integer==0) {
                textView_error.setText(errorMessage);
                bool_image = false;
            } else {
                textView_error.setText("unknown error");
                bool_image = false;
            }

        }

        @Override
        protected Integer doInBackground(Bitmap... bitmap) {
            try {
                Face[] result;
                errorMessage = "";
                InputStream imageInputStream = null;
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap[0].compress(Bitmap.CompressFormat.JPEG, 100, stream);
                imageInputStream = new ByteArrayInputStream(stream.toByteArray());
                bool_image = true;
                imageUriPass = imageUri;



                result = faceServiceClient.detect(
                        imageInputStream,
                        true,         // returnFaceId
                        false,        // returnFaceLandmarks
                        null          // returnFaceAttributes:
                );

                if (result.length > 1 ) {
                    errorMessage = "Cannot exceed more than 1 face";
                    return 2;
                } else if (result == null || result.length == 0) {
                    errorMessage = "No face detected";
                    return 3;
                } else {
                    return 1;
                }
            } catch (Exception e) {
                errorMessage = "[Error]" + e;
                return 0;
            }
        }
    }

    class CheckPhotoTask extends AsyncTask<String,String, Integer> {
        @Override
        protected void onPostExecute(Integer integer) {
            if (integer == 1){
                textView_error.setText("");
                imageView.setImageURI(imageUri);
                bool_image = true;
                firebaseImage = false;

            } else if (integer==2||integer==3||integer==0) {
                textView_error.setText(errorMessage);
                bool_image = false;
            } else {
                textView_error.setText("unknown error");
                bool_image = false;
            }

        }

        @Override
        protected Integer doInBackground(String... params) {
            try {
                Face[] result;
                errorMessage = "";
                InputStream imageInputStream = null;
                Bitmap mBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(params[0]));
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                imageInputStream = new ByteArrayInputStream(stream.toByteArray());



                result = faceServiceClient.detect(
                        imageInputStream,
                        true,         // returnFaceId
                        false,        // returnFaceLandmarks
                        null          // returnFaceAttributes:
                );

                if (result.length > 1 ) {
                    errorMessage = "Cannot exceed more than 1 face";
                    return 2;
                } else if (result == null || result.length == 0) {
                    errorMessage = "No face detected";
                    return 3;
                } else {
                    return 1;
                }
            } catch (Exception e) {
                errorMessage = "[Error]" + e;
                return 0;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step1);

        bool_image = false;

        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        imageView = findViewById(R.id.imageView_Photo);



        Bundle bundle = getIntent().getExtras();
        String data = bundle.getString("employeeFace");
        if (!data.equals("N/A")){
            imageUri  =  Uri.parse(data);

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference imageRef = storage.getReferenceFromUrl(imageUri.toString());

            imageRef.getBytes(4096*4096)
                    .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                            new CheckPhotoTaskFirebase().execute(bitmap);

                        }
                    });
        }


        button = findViewById(R.id.button_Gallery);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();

            }
        });

        btOpen = findViewById(R.id.button_TakePhoto);

        textView_error = findViewById(R.id.textView_error);

        btOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                //Open Camera

                if (checkCameraPermission()) {
                    dispatchTakePictureIntent();
                }

            }

        });

        btConfirm = findViewById(R.id.button_Confirm);
        btConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Open Confirm
                if (bool_image == true) {
                    Bundle extras = getIntent().getExtras();
                    String data = extras.getString("employeeResume");


                    Intent intent = new Intent(getApplicationContext(), Step2Activity.class);

                    try {
                        intent.putExtra("employeeFace", imageUriPass.toString());
                        if (firebaseImage){
                            intent.putExtra("fbBoolean", "true");
                        } else {
                            intent.putExtra("fbBoolean", "false");
                        }

                    } catch (Exception ex){
                        Toast.makeText(getApplicationContext(),ex.toString(),Toast.LENGTH_LONG).show();
                    }
                    intent.putExtra("employeeResume",data);
                    startActivity(intent);


                } else {
                    Toast.makeText(getApplicationContext(),"Please upload your image",Toast.LENGTH_LONG).show();
                }


            }
        });

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
            imageUriPass = imageUri;
            firebaseImage = false;
            new CheckPhotoTask().execute(imageUri.toString());
        }

        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            imageUriPass = imageUri;
            firebaseImage = false;
            new CheckPhotoTask().execute(imageUri.toString());
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


}
