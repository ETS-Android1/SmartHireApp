package com.example.smarthire;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import edmt.dev.edmtdevcognitiveface.Contract.Face;
import edmt.dev.edmtdevcognitiveface.Contract.VerifyResult;
import edmt.dev.edmtdevcognitiveface.FaceServiceClient;
import edmt.dev.edmtdevcognitiveface.FaceServiceRestClient;

import android.os.Handler;

public class Step2Activity extends AppCompatActivity {

    ImageView imageView;
    TextView textView_error;

    Button btConfirm,button,btOpen;
    boolean bool_image;
    boolean firebaseImage;
    boolean firebaseImagePre;
    String errorMessage;

    private final String API_KEY="d88466ed30124b38bcc124f250a09c46";
    private final String API_LINK="https://southeastasia.api.cognitive.microsoft.com/face/v1.0/";

    private FaceServiceClient faceServiceClient = new FaceServiceRestClient(API_LINK, API_KEY);

    Uri imageUri;
    Uri imageUriPass;

    private static final int PICK_IMAGE = 100;
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 400;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    String []cameraPermission;
    String []storagePermission;

    Bitmap bitmap1,bitmap2;

    LoadingDialog dialog = new LoadingDialog(Step2Activity.this);





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
            dialog.startLoadingDialog();
            new CheckPhotoTask().execute(imageUri.toString());
            //imageView.setImageURI(imageUri);
            //bool_image = true;
        }

        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            imageUriPass = imageUri;
            dialog.startLoadingDialog();
            new CheckPhotoTask().execute(imageUri.toString());
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

    private static class ParamsVerifyFb {
        Bitmap imgPath1,imgPath2;

        ParamsVerifyFb(Bitmap imgPath1,Bitmap imgPath2) {
            this.imgPath1 = imgPath1;
            this.imgPath2 = imgPath2;
        }
    }

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
            dialog.dissmissDialog();

        }

        @Override
        protected Integer doInBackground(String... params) {
            try {
                Face[] result;
                errorMessage = "";

                Bitmap mBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(params[0]));
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                InputStream imageInputStream = new ByteArrayInputStream(stream.toByteArray());

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

    class VerifyTaskFb extends AsyncTask<ParamsVerifyFb,String, VerifyResult> {
        @Override
        protected void onPostExecute(VerifyResult result) {


            if (result != null) {
                if (result.isIdentical){

                    Bundle extras = getIntent().getExtras();
                    String key = extras.getString("employeeKey");
                    DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");
                    mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            dataSnapshot.getRef().child(key).child("verify").setValue("verified");
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.d("User", databaseError.getMessage());
                        }
                    });
                    dialog.dissmissDialog();

                    Intent intent = new Intent(getApplicationContext(), Step3Activity.class);
                    startActivity(intent);
                } else {
                    dialog.dissmissDialog();
                    showCustomToast("Failed",Toast.LENGTH_SHORT);
                    //Toast.makeText(getBaseContext(), "Failed!", Toast.LENGTH_SHORT).show();
                    textView_error.setText("The faces are different");
                }
            }
        }

        @Override
        protected VerifyResult doInBackground(ParamsVerifyFb... params) {
            try {

                Bitmap mBitmap = params[0].imgPath1;
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                InputStream imageInputStream = new ByteArrayInputStream(stream.toByteArray());
                Face[] result,result2;
                result = faceServiceClient.detect(
                        imageInputStream,
                        true,         // returnFaceId
                        false,        // returnFaceLandmarks
                        null          // returnFaceAttributes:
                );

                if (result.length > 1 ) {
                    return null;
                } else if (result == null || result.length == 0) {
                    return null;
                }

                mBitmap = params[0].imgPath2;
                stream = new ByteArrayOutputStream();
                mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                imageInputStream = new ByteArrayInputStream(stream.toByteArray());

                result2 = faceServiceClient.detect(
                        imageInputStream,
                        true,         // returnFaceId
                        false,        // returnFaceLandmarks
                        null          // returnFaceAttributes:
                );

                if (result2.length > 1 ) {
                    return null;
                } else if (result2 == null || result2.length == 0) {
                    return null;
                }

                //Verify
                return faceServiceClient.verify(result[0].faceId,result2[0].faceId);
            } catch (Exception e) {
                return null;
            }
        }
    }

    private interface FirebaseCallback {
        void onCallback (Bitmap bitmap);
    }

    private void readData1 (FirebaseCallback firebaseCallback){
        Bundle extras = getIntent().getExtras();
        String faceUri = extras.getString("employeeFace");
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference imageRef = storage.getReferenceFromUrl(faceUri.toString());
        imageRef.getBytes(4096*4096)
                .addOnSuccessListener(new OnSuccessListener<byte[]>()
                {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                        firebaseCallback.onCallback(bitmap);

                    }
                });

    }

    private void readData2 (FirebaseCallback firebaseCallback){
        Bundle extras = getIntent().getExtras();
        String resumeUri = extras.getString("employeeResume");
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference imageRef = storage.getReferenceFromUrl(resumeUri.toString());
        imageRef.getBytes(4096*4096)
                .addOnSuccessListener(new OnSuccessListener<byte[]>()
                {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                        firebaseCallback.onCallback(bitmap);

                    }
                });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step2);

        bool_image = false;


        imageView = findViewById(R.id.imageView_PhotoB);
        Bundle extras = getIntent().getExtras();
        String data = extras.getString("employeeResume");
        String dataFb = extras.getString("fbBoolean");

        if (dataFb.equals("true")){
            firebaseImagePre = true;
        } else {
            firebaseImagePre = false;
        }

        if (!data.equals("N/A")){

            imageUri  =  Uri.parse(data);

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference imageRef = storage.getReferenceFromUrl(imageUri.toString());


            imageRef.getBytes(4096*4096)
                    .addOnSuccessListener(new OnSuccessListener<byte[]>()
                    {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                            new CheckPhotoTaskFirebase().execute(bitmap);

                        }
                    });
        }



        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};


        button = findViewById(R.id.button_GalleryB);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();

            }
        });

        btOpen = findViewById(R.id.button_TakePhotoB);

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

        btConfirm = findViewById(R.id.button_Confirm2B);
        btConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //Open Confirm


                    if (bool_image== true){
                        dialog.startLoadingDialog();
                        readAllDataEX();
                    } else {
                        showCustomToast("Please upload your image",Toast.LENGTH_LONG);
                        //Toast.makeText(getApplicationContext(),"Please upload your image",Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    showCustomToast(e.toString(),Toast.LENGTH_LONG);
                    //Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void readAllDataEX() {
        String stringUri;

        try {
            if (!firebaseImage){
                bitmap2 = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUriPass);
            }
            if (!firebaseImagePre){
                Bundle extras = getIntent().getExtras();
                stringUri = extras.getString("employeeFace");
                bitmap1 = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(stringUri));
            }

            if (firebaseImage && !firebaseImagePre){
                readData2(new FirebaseCallback() {
                    @Override
                    public void onCallback(Bitmap bitmap) {

                        bitmap2 = bitmap;
                        ParamsVerifyFb params = new ParamsVerifyFb(bitmap1,bitmap2);
                        new VerifyTaskFb().execute(params);
                    }
                });
            } else if (firebaseImagePre && !firebaseImage){
                readData1(new FirebaseCallback() {
                    @Override
                    public void onCallback(Bitmap bitmap) {

                        bitmap1 = bitmap;
                        ParamsVerifyFb params = new ParamsVerifyFb(bitmap1,bitmap2);
                        new VerifyTaskFb().execute(params);
                    }
                });
            } else if (firebaseImagePre && firebaseImage) {
                readData1(new FirebaseCallback() {
                    @Override
                    public void onCallback(Bitmap bitmap) {

                        bitmap1 = bitmap;
                        readData2(new FirebaseCallback() {
                            @Override
                            public void onCallback(Bitmap bitmap) {

                                bitmap2 = bitmap;
                                ParamsVerifyFb params = new ParamsVerifyFb(bitmap1, bitmap2);
                                new VerifyTaskFb().execute(params);
                            }
                        });
                    }
                });
            } else {
                ParamsVerifyFb params = new ParamsVerifyFb(bitmap1, bitmap2);
                new VerifyTaskFb().execute(params);
            }
        } catch (Exception e) {
            showCustomToast(""+e,Toast.LENGTH_SHORT);
            //Toast.makeText(getApplicationContext(),""+e,Toast.LENGTH_SHORT).show();
        }
    }


    private void readAllData() {
        String stringUri;

        try {
            if (!firebaseImage){
                bitmap1 = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUriPass);
            }
            if (!firebaseImagePre){
                Bundle extras = getIntent().getExtras();
                stringUri = extras.getString("employeeFace");
                bitmap2 = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(stringUri));
            }

            if (firebaseImage){
                runReadData2();
            }
            if (firebaseImagePre){
                runReadData1();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void runReadData1() {
        readData1(new FirebaseCallback() {
            @Override
            public void onCallback(Bitmap bitmap) {

                bitmap1 = bitmap;
            }
        });
    }

    private void runReadData2() {
        readData2(new FirebaseCallback() {
            @Override
            public void onCallback(Bitmap bitmap) {

                bitmap2 = bitmap;
            }
        });
    }

    private void showCustomToast(String msg, int length){

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast, findViewById(R.id.custom_toast_container));

        TextView text = layout.findViewById(R.id.text);
        text.setText(msg);

        Toast toast = new Toast(Step2Activity.this);
        toast.setGravity(Gravity.BOTTOM, 0, 120);
        toast.setDuration(length);
        toast.setView(layout);
        toast.show();

    }


}
