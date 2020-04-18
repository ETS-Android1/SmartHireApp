package com.example.hire;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.MimeTypeMap;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;

import com.example.hire.databinding.ActivityFabBinding;
import com.example.hire.databinding.ActivityFabForExtactedBinding;
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

import java.text.SimpleDateFormat;
import java.util.Date;

public class ExtractedFragment extends Fragment {

    Animation fabOpen, fabClose, rotateForward, rotateBackward;
    boolean isOpen = false;
    Bundle bundle;
    private String extractedPhoneNumber,extractedEmail,extractedName,extractedAddress,extractedSkills,extractedEducation,extractedOther,extractedFace,resume;
    private int extractedAge;
    private static final int EDIT_EXTRACTED_TEXT_CODE = 6;

    //google firebase database
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private StorageTask mUploadTask;

    private Uri photoUri,resumeUri,photoDownloadUri,resumeDownloadUri;

    private ActivityFabForExtactedBinding binding;

    private NavController navController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //setHasOptionsMenu(true);
        binding = ActivityFabForExtactedBinding.inflate(getLayoutInflater(),container,false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //firebase
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");

        fabOpen = AnimationUtils.loadAnimation(getActivity(),R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(getActivity(),R.anim.fab_close);
        rotateForward = AnimationUtils.loadAnimation(getActivity(),R.anim.rotate_forward);
        rotateBackward = AnimationUtils.loadAnimation(getActivity(),R.anim.rotate_backward);

        bundle = getArguments();

        extractedPhoneNumber = bundle.getString("EXTRACTED_PHONE");
        extractedEmail = bundle.getString("EXTRACTED_EMAIL");
        extractedName = bundle.getString("EXTRACTED_NAME");
        extractedAddress = bundle.getString("EXTRACTED_ADDRESS");
        extractedAge = bundle.getInt("EXTRACTED_AGE",0);
        extractedSkills = bundle.getString("EXTRACTED_SKILLS");
        extractedEducation = bundle.getString("EXTRACTED_EDUCATION");
        //extractedOther = intent.getStringExtra("EXTRACTED_OTHER");

        extractedFace = bundle.getString("EXTRACTED_FACE");
        resume = bundle.getString("RESUME");
        resumeUri = Uri.parse(resume);
        photoUri = Uri.parse(extractedFace);

        binding.include.textViewExtractedPhoneNum.setText(extractedPhoneNumber);
        binding.include.textViewExtractedEmail.setText(extractedEmail);
        binding.include.textViewExtractedName.setText(extractedName);
        binding.include.textViewExtractedAddress.setText(extractedAddress);
        binding.include.textViewExtractedAge.setText(Integer.toString(extractedAge));
        binding.include.textViewExtractedSkills.setText(extractedSkills);
        binding.include.textViewExtractedEducation.setText(extractedEducation);
        //textViewExtractedOther.setText(extractedOther);
        binding.include.imageViewExtractedImage.setImageURI(photoUri);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFab();
            }
        });

        binding.fabEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFab();
                Bundle bundle2 = new Bundle();
                bundle2.putString("EXTRACTED_NAME",extractedName);
                bundle2.putString("EXTRACTED_PHONE",extractedPhoneNumber);
                bundle2.putString("EXTRACTED_EMAIL",extractedEmail);
                bundle2.putString("EXTRACTED_ADDRESS",extractedAddress);
                bundle2.putInt("EXTRACTED_AGE",extractedAge);
                bundle2.putString("EXTRACTED_SKILLS",extractedSkills);
                bundle2.putString("EXTRACTED_EDUCATION",extractedEducation);
                bundle2.putString("EXTRACTED_FACE",photoUri.toString());

                //navController.navigate(R.id.action_extractedFragment_to_extractedEditFragment);



                Intent intent2 = new Intent(getActivity(),ExtractedTextEdit.class);
                intent2.putExtra("EXTRACTED_NAME",extractedName);
                intent2.putExtra("EXTRACTED_PHONE",extractedPhoneNumber);
                intent2.putExtra("EXTRACTED_EMAIL",extractedEmail);
                intent2.putExtra("EXTRACTED_ADDRESS",extractedAddress);
                intent2.putExtra("EXTRACTED_AGE",extractedAge);
                intent2.putExtra("EXTRACTED_SKILLS",extractedSkills);
                intent2.putExtra("EXTRACTED_EDUCATION",extractedEducation);
                intent2.putExtra("EXTRACTED_FACE",photoUri.toString());

                startActivityForResult(intent2,EDIT_EXTRACTED_TEXT_CODE);
            }
        });

        binding.fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFab();
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    //mProgressBar.setVisibility(ProgressBar.VISIBLE);
                    Toast.makeText(getActivity(), "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    uploadToDatabase();

                }
            }
        });

    }

    private void animateFab(){
        if (isOpen){
            binding.fab.startAnimation(rotateBackward);
            binding.fabEdit.startAnimation(fabClose);
            binding.fabSave.startAnimation(fabClose);
            //fabExtract.startAnimation(fabClose);
            binding.fabEdit.setClickable(false);
            binding.fabSave.setClickable(false);
            //fabExtract.setClickable(false);
            isOpen=false;
        }else{
            binding.fab.startAnimation(rotateForward);
            binding.fabEdit.startAnimation(fabOpen);
            binding.fabSave.startAnimation(fabOpen);
            //fabExtract.startAnimation(fabOpen);
            binding.fabEdit.setClickable(true);
            binding.fabSave.setClickable(true);
            //fabExtract.setClickable(true);
            isOpen=true;
        }
    }

    private void uploadToDatabase() {
        binding.progressBarExtracted.setVisibility(ProgressBar.VISIBLE);
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

                                photoDownloadUri = task.getResult();

                            }
                            else { Toast.makeText(getActivity(), "upload failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
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
            Toast.makeText(getActivity(), "No file selected", Toast.LENGTH_SHORT).show();
        }

        if (resumeUri != null) {
            final StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(resumeUri));

            fileReference.putFile(resumeUri).continueWithTask(
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
                                        binding.progressBarExtracted.setVisibility(ProgressBar.INVISIBLE);
                                    }
                                }, 200);

                                resumeDownloadUri = task.getResult();
                                String timeStamp = new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date());
                                Employee upload = new Employee(extractedName.trim(), photoDownloadUri.toString(),resumeDownloadUri.toString(),extractedAddress.trim(),extractedPhoneNumber.trim(),extractedEmail.trim(),timeStamp,extractedSkills.trim(),extractedEducation.trim(),extractedAge);
                                String uploadId = mDatabaseRef.push().getKey();
                                mDatabaseRef.child(uploadId).setValue(upload);
                                //mDatabaseRef.push().setValue(upload);
                                Toast.makeText(getActivity(), "Upload successful", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getActivity(),BottomNavigationActivity.class);
                                startActivity(intent);
                            }
                            else { Toast.makeText(getActivity(), "upload failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
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
            Toast.makeText(getActivity(), "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
}
