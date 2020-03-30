package com.example.hire;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

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
    String extractedPhoneNumber,extractedEmail,extractedName,extractedAddress,extractedFace;
    private static final int EDIT_EXTRACTED_TEXT_CODE = 6;

    //google firebase database
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private StorageTask mUploadTask;

    private ProgressBar mProgressBar;
    private Uri photoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fab_for_extacted);

        //firebase
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");

        mProgressBar = findViewById(R.id.progressBar);

        textViewExtractedPhone = findViewById(R.id.editTextExtractedPhone);
        textViewExtractedEmail = findViewById(R.id.editTextExtractedEmail);
        textViewExtractedName = findViewById(R.id.editTextExtractedName);
        textViewExtractedAddress = findViewById(R.id.editTextExtractedAddress);
        imageViewExtractedImage = findViewById(R.id.imageViewExtractedImage);

        fab = findViewById(R.id.fab);
        fab1 = findViewById(R.id.fabEdit);
        fab2 = findViewById(R.id.fabSave);

        fabOpen = AnimationUtils.loadAnimation(this,R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(this,R.anim.fab_close);
        rotateForward = AnimationUtils.loadAnimation(this,R.anim.rotate_forward);
        rotateBackward = AnimationUtils.loadAnimation(this,R.anim.rotate_backward);

        intent = getIntent();

        extractedPhoneNumber = intent.getStringExtra("EXTRACTED_PHONE");
        extractedEmail = intent.getStringExtra("EXTRACTED_EMAIL");
        extractedName = intent.getStringExtra("EXTRACTED_NAME");
        extractedAddress = intent.getStringExtra("EXTRACTED_ADDRESS");
        extractedFace = intent.getStringExtra("EXTRACTED_FACE");
        photoUri = Uri.parse(extractedFace);

        textViewExtractedPhone.setText(extractedPhoneNumber);
        textViewExtractedEmail.setText(extractedEmail);
        textViewExtractedName.setText(extractedName);
        textViewExtractedAddress.setText(extractedAddress);
        imageViewExtractedImage.setImageURI(photoUri);

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
                intent2.putExtra("EXTRACTED_FACE",photoUri.toString());

                startActivityForResult(intent2,EDIT_EXTRACTED_TEXT_CODE);
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFab();
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    //mProgressBar.setVisibility(ProgressBar.VISIBLE);
                    Toast.makeText(ExtractedText.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    uploadToDatabase();

                }
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if(requestCode==EDIT_EXTRACTED_TEXT_CODE)
        {
            extractedName =data.getStringExtra("NEW_NAME");
            extractedPhoneNumber =data.getStringExtra("NEW_PHONE_NUM");
            extractedEmail =data.getStringExtra("NEW_EMAIL");
            extractedAddress =data.getStringExtra("NEW_ADDRESS");
            //final Bitmap bitmap = data.getParcelableExtra("NEW_PHOTO");
            //Bundle extras = data.getExtras();
            //byte[] b = extras.getByteArray("NEW_PHOTO");
            extractedFace = data.getStringExtra("NEW_PHOTO");
            photoUri = Uri.parse(extractedFace);
            //Bitmap bmp = BitmapFactory.decodeByteArray(b, 0, b.length);


            textViewExtractedPhone.setText(extractedPhoneNumber);
            textViewExtractedEmail.setText(extractedEmail);
            textViewExtractedName.setText(extractedName);
            textViewExtractedAddress.setText(extractedAddress);
            imageViewExtractedImage.setImageURI(photoUri);
        }
    }

    //Firebase
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadToDatabase() {
        mProgressBar.setVisibility(ProgressBar.VISIBLE);
        if (photoUri != null) {
            final StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(photoUri));

            fileReference.putFile(photoUri).continueWithTask(
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

                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                                    }
                                }, 200);

                                Uri downloadUri = task.getResult();
                                Employee upload = new Employee(extractedName.trim(), downloadUri.toString(),extractedAddress.trim(),extractedEmail.trim(),extractedPhoneNumber.trim());
                                String uploadId = mDatabaseRef.push().getKey();
                                mDatabaseRef.child(uploadId).setValue(upload);
                                //mDatabaseRef.push().setValue(upload);
                                Toast.makeText(ExtractedText.this, "Upload successful", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(ExtractedText.this,RecylerViewActivity.class);
                                startActivity(intent);
                            }
                            else { Toast.makeText(ExtractedText.this, "upload failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ExtractedText.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }


}
