package com.example.hire.frgaments;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.hire.BottomNavigationActivity;
import com.example.hire.Employee;
import com.example.hire.R;
import com.example.hire.databinding.ActivityFabForExtactedEditBinding;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ExtractedEditFragment extends Fragment {

    Bundle bundle;
    private static final int EDIT_EXTRACTED_TEXT_CODE = 6;
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 400;
    private static final int IMAGE_PICK_GALLERY_CODE = 1000;
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private BitmapDrawable imageBitmapDrawable;
    private Bitmap imageBitmap, tempBitmap;

    String camaraPermission[];
    String storagePermission[];

    byte[] b;
    private Uri resultUri, resumeUri, imageUri;

    //google firebase database
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private StorageTask mUploadTask;

    private ActivityFabForExtactedEditBinding binding;

    private Uri photoDownloadUri, resumeDownloadUri;

    private String extractedPhoneNumber, extractedEmail, extractedName, extractedAddress, extractedSkills, extractedEducation, extractedFace, resume;
    private int extractedAge;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //setHasOptionsMenu(true);
        binding = ActivityFabForExtactedEditBinding.inflate(getLayoutInflater(), container, false);
        View view = binding.getRoot();

        //((AppCompatActivity)getActivity()).setSupportActionBar(binding.include.toolbarHomePage);
        //((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Hire");

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");

        camaraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        bundle = getArguments();

        extractedPhoneNumber = bundle.getString("EXTRACTED_PHONE");
        extractedEmail = bundle.getString("EXTRACTED_EMAIL");
        extractedName = bundle.getString("EXTRACTED_NAME");
        extractedAddress = bundle.getString("EXTRACTED_ADDRESS");
        extractedAge = bundle.getInt("EXTRACTED_AGE", 0);
        extractedSkills = bundle.getString("EXTRACTED_SKILLS");
        extractedEducation = bundle.getString("EXTRACTED_EDUCATION");
        extractedFace = bundle.getString("EXTRACTED_FACE");
        resume = bundle.getString("RESUME");
        resultUri = Uri.parse(extractedFace);
        resumeUri = Uri.parse(resume);

        binding.include.editTextExtractedPhone.setText(extractedPhoneNumber);
        binding.include.editTextExtractedEmail.setText(extractedEmail);
        binding.include.editTextExtractedName.setText(extractedName);
        binding.include.editTextExtractedAddress.setText(extractedAddress);
        binding.include.editTextExtractedAge.setText(Integer.toString(extractedAge));
        binding.include.editTextSkills.setText(extractedSkills);
        binding.include.editTextEducation.setText(extractedEducation);

        if(extractedFace.equals("noProfile")){

            binding.include.imageViewExtractedImageEdit.setImageResource(R.drawable.ic_person);

        }else{

            binding.include.imageViewExtractedImageEdit.setImageURI(resultUri);

        }


        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    //mProgressBar.setVisibility(ProgressBar.VISIBLE);
                    Toast.makeText(getActivity(), "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {

                    if (isNumeric(binding.include.editTextExtractedAge.getText().toString())) {
                        uploadToDatabase();
                    } else {
                        binding.include.editTextExtractedAge.setError(getString(R.string.numeric_age_error));
                    }
                }
            }
        });

        binding.include.fabEditPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpAlertDialog();

            }
        });

    }

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            int number = Integer.parseInt(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    private void setUpAlertDialog() {
        //String[] options = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Pick your options");
        builder.setItems(getResources().getStringArray(R.array.take_photo_options), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int option) {
                if (option == 0) {
                    Toast.makeText(getActivity(), "Say Cheese", Toast.LENGTH_SHORT).show();
                    if (!checkCameraPermission()) {
                        requestCameraPermission();
                    } else {
                        dispatchTakePictureIntent();
                    }
                } else {
                    Toast.makeText(getActivity(), "Pick a photo", Toast.LENGTH_SHORT).show();
                    if (!checkStoragePermission()) {
                        requestStoragePermission();
                    } else {
                        pickGallery();
                    }
                }
                // the user clicked on colors[which]
            }
        });
        builder.show();
    }

    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(getActivity(), camaraPermission, CAMERA_REQUEST_CODE);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {

            ContentValues values = new ContentValues();
            Toast.makeText(getActivity(), "Say cheese!", Toast.LENGTH_SHORT).show();
            values.put(MediaStore.Images.Media.TITLE, "NewPic");
            values.put(MediaStore.Images.Media.DESCRIPTION, "Image to Text");
            imageUri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }

    }

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(getActivity(), storagePermission, STORAGE_REQUEST_CODE);
    }

    private void pickGallery() {
        //intent to pick image from gallery
        Intent intent = new Intent(Intent.ACTION_PICK);
        //set intent tyoe to image
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);

    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    Log.d("myTag", "Camera:" + cameraAccepted + " Storage:" + writeStorageAccepted);
                    if (cameraAccepted && writeStorageAccepted) {
                        dispatchTakePictureIntent();
                    } else {
                        Toast.makeText(getActivity(), "Permission Denied!", Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case STORAGE_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    Log.d("myTag", " Storage:" + writeStorageAccepted);
                    if (writeStorageAccepted) {
                        pickGallery();
                    } else {
                        Toast.makeText(getActivity(), "Permission Denied!", Toast.LENGTH_SHORT).show();
                    }
                }
                break;

        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case IMAGE_PICK_GALLERY_CODE:
                    CropImage.activity(data.getData())
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(getActivity(), ExtractedEditFragment.this);
                    break;
                case REQUEST_IMAGE_CAPTURE:
                    //Bundle extras = data.getExtras();
                    //imageBitmap = (Bitmap) extras.get("data");
                    CropImage.activity(imageUri)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(getActivity(), ExtractedEditFragment.this);
                    //Toast.makeText(MainActivity.this,"Error 123:" ,Toast.LENGTH_SHORT).show();

                    //imageBitmap = BitmapFactory.decodeFile(currentImagePath);
                    //imageViewResume.setImageBitmap(imageBitmap);
                    break;
                case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    resultUri = result.getUri();//get image uri
                    binding.include.imageViewExtractedImageEdit.setImageURI(resultUri);
                    Log.d("Image", "" + resultUri);
                    imageBitmapDrawable = (BitmapDrawable) binding.include.imageViewExtractedImageEdit.getDrawable();
                    imageBitmap = imageBitmapDrawable.getBitmap();
                    break;

                case CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE:
                    Toast.makeText(getActivity(), "Error getting crop image", Toast.LENGTH_SHORT).show();
                    break;

                default:
                    Toast.makeText(getActivity(), "Error getting result", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadToDatabase() {
        Toast.makeText(getActivity(), "Uploading to Database", Toast.LENGTH_SHORT).show();
        binding.progressBarExtractedEdit.setVisibility(ProgressBar.VISIBLE);

        /*String timeStamp = new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date());
        Employee upload = new Employee(extractedName.trim(), resultUri.toString(),resumeUri.toString(),extractedAddress.trim(),extractedPhoneNumber.trim(),extractedEmail.trim(),timeStamp,extractedSkills.trim(),extractedEducation.trim(),extractedAge);
        String uploadId = mDatabaseRef.push().getKey();
        mDatabaseRef.child(uploadId).setValue(upload);
        //mDatabaseRef.push().setValue(upload);
        Toast.makeText(getActivity(), "Upload successful", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getActivity(), BottomNavigationActivity.class);
        binding.progressBarExtractedEdit.setVisibility(ProgressBar.INVISIBLE);
        startActivity(intent);*/
        if (resultUri != null && !(extractedFace.equals("noProfile"))) {
            final StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(resultUri));

            fileReference.putFile(resultUri).continueWithTask(
                    new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return fileReference.getDownloadUrl();
                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {

                                photoDownloadUri = task.getResult();

                            } else {
                                Toast.makeText(getActivity(), "Profile photo upload failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        } else {
            photoDownloadUri = Uri.parse("noProfile");
            //Toast.makeText(getActivity(), "No file selected", Toast.LENGTH_SHORT).show();
        }

        if (resumeUri != null) {
            final StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(resumeUri));

            fileReference.putFile(resumeUri).continueWithTask(
                    new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return fileReference.getDownloadUrl();
                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {

                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        binding.progressBarExtractedEdit.setVisibility(ProgressBar.INVISIBLE);
                                    }
                                }, 200);

                                resumeDownloadUri = task.getResult();
                                String timeStamp = new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date());
                                Employee upload = new Employee(extractedName.trim(), photoDownloadUri.toString(), resumeDownloadUri.toString(), extractedAddress.trim(), extractedPhoneNumber.trim(), extractedEmail.trim(), timeStamp, extractedSkills.trim(), extractedEducation.trim(), extractedAge);
                                String uploadId = mDatabaseRef.push().getKey();
                                mDatabaseRef.child(uploadId).setValue(upload);
                                //mDatabaseRef.push().setValue(upload);
                                Toast.makeText(getActivity(), "Upload successful", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getActivity(), BottomNavigationActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(getActivity(), "upload failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        } else {
            resumeDownloadUri = Uri.parse("noResume");
            //Toast.makeText(getActivity(), "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
}
