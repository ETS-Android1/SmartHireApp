package com.example.hire.frgaments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.pdf.PdfRenderer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.MimeTypeMap;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.hire.R;
import com.example.hire.databinding.ActivityFabBinding;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UploadFragment extends Fragment {

    private RequestQueue mQueue;
    private Bitmap imageBitmap, croppedBitmap;
    private BitmapDrawable imageBitmapDrawable;
    private float x1, x2, y1, y2;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    String currentImagePath = null;
    Uri imageUri;
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 400;
    private static final int IMAGE_PICK_GALLERY_CODE = 1000;
    private static final int PICK_PDF_CODE = 100;
    String camaraPermission[];
    String storagePermission[];

    private String employeePhoneNum, employeeEmail, employeeAddress, employeeName, employeeLocation, employeeEducation, employeeAge, employeeSkills, employeeOther, employeeOrganization;
    private String extractedTextFromImage = "";
    Uri croppedFace, resultUri;

    Intent intent1;

    Animation fabOpen, fabClose, rotateForward, rotateBackward;
    boolean isOpen = false;

    private int progressStatus = 0;
    private Handler handler = new Handler();
    private boolean isCanceled;
    private boolean connected;
    private ConnectivityManager connectivityManager;
    private NetworkInfo networkInfo;
    private ExtractThread extractThread;

    private int expandWidth, expandHeight, faceWidth, faceHeight;

    StringBuilder stringBuilderSkills;
    StringBuilder stringBuilderEducation;
    StringBuilder stringBuilderOther;

    private ActivityFabBinding binding;
    private NavController navController;

    private boolean isFaceCropped;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //setHasOptionsMenu(true);
        binding = ActivityFabBinding.inflate(getLayoutInflater(), container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        extractThread = new ExtractThread();

        connectivityManager = (ConnectivityManager) getActivity().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();
        camaraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        mQueue = Volley.newRequestQueue(getActivity());

        fabOpen = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_close);

        rotateForward = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_forward);
        rotateBackward = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_backward);

        //setSupportActionBar(binding.include.toolbarMain);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);
        //getSupportActionBar().setTitle("Hirkle");
        //getSupportActionBar().setIcon(getDrawable(R.drawable.hire_logo));


        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                animateFab();
                //Log.d("NETWORK", ""+networkInfo.isConnected());
                if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                    //we are connected to a network
                    connected = true;
                    Log.d("CONNECT", "" + connected);
                } else
                    connected = false;
                Log.d("CONNECT", "" + connected);

            }
        });

        binding.fabCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomToast(getString(R.string.capture_resume),Toast.LENGTH_SHORT);
                //Toast.makeText(getActivity(), "Capture the resume", Toast.LENGTH_SHORT).show();
                animateFab();
                if (!checkCameraPermission()) {
                    requestCameraPermission();
                } else {
                    dispatchTakePictureIntent();
                }
            }
        });

        binding.fabGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomToast(getString(R.string.choose_resume),Toast.LENGTH_SHORT);
                //Toast.makeText(getActivity(), "Choose a resume", Toast.LENGTH_SHORT).show();
                animateFab();

                if (!checkStoragePermission()) {
                    requestStoragePermission();
                } else {
                    pickGallery();
                }
            }
        });

        binding.fabPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkStoragePermission()) {
                    requestStoragePermission();
                } else {
                    selectPDF();
                }
            }
        });

        binding.fabExtract.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {
                //fab.setVisibility(FloatingActionButton.INVISIBLE);
                if (resultUri == null) {
                    showCustomToast(getString(R.string.upload_resume_first),Toast.LENGTH_LONG);
                    //Toast.makeText(getActivity(), "Please Upload your resume first", Toast.LENGTH_LONG).show();
                } else {
                    connected = false;
                    if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                            connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                        //we are connected to a network
                        connected = true;
                        binding.fab.setClickable(false);
                        showCustomToast(getString(R.string.extracting),Toast.LENGTH_SHORT);
                        //Toast.makeText(getActivity(), "Extracting...", Toast.LENGTH_LONG).show();
                        animateFab();
                        binding.progressBar.setVisibility(ProgressBar.VISIBLE);
                        new Thread(extractThread).start();
                        navController = Navigation.findNavController(v);
                    } else {
                        connected = false;
                        showCustomToast(getString(R.string.no_internet),Toast.LENGTH_SHORT);
                        //Toast.makeText(getActivity(), "No Internet", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private void animateFab() {
        if (isOpen) {
            binding.fab.startAnimation(rotateBackward);
            binding.fabCamera.startAnimation(fabClose);
            //textViewCamera.setVisibility(View.GONE);
            binding.textViewCamera.setAnimation(fabClose);
            binding.textViewGallery.setAnimation(fabClose);
            binding.textViewExtract.setAnimation(fabClose);
            binding.fabGallery.startAnimation(fabClose);
            binding.fabExtract.startAnimation(fabClose);
            binding.fabPDF.startAnimation(fabClose);
            binding.textViewPDF.startAnimation(fabClose);
            binding.fabCamera.setClickable(false);
            binding.fabGallery.setClickable(false);
            binding.fabExtract.setClickable(false);
            binding.fabPDF.setClickable(false);
            isOpen = false;
        } else {
            binding.fab.startAnimation(rotateForward);
            binding.fabCamera.startAnimation(fabOpen);
            //textViewCamera.setVisibility(View.VISIBLE);
            binding.fabGallery.startAnimation(fabOpen);
            binding.fabExtract.startAnimation(fabOpen);
            binding.textViewCamera.setAnimation(fabOpen);
            binding.textViewGallery.setAnimation(fabOpen);
            binding.textViewExtract.setAnimation(fabOpen);
            binding.fabPDF.startAnimation(fabOpen);
            binding.textViewPDF.startAnimation(fabOpen);
            //textViewExtract.setBackgroundResource(R.color.colorPrimary);
            binding.textViewCamera.setBackgroundResource(R.drawable.rounded_corner);
            binding.textViewGallery.setBackgroundResource(R.drawable.rounded_corner);
            binding.textViewExtract.setBackgroundResource(R.drawable.rounded_corner);
            binding.textViewPDF.setBackgroundResource(R.drawable.rounded_corner);
            binding.fabCamera.setClickable(true);
            binding.fabGallery.setClickable(true);
            binding.fabExtract.setClickable(true);
            binding.fabPDF.setClickable(true);
            isOpen = true;
        }
    }

    private void selectPDF(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT); //If you want the user to choose something based on MIME type, use ACTION_GET_CONTENT.
        intent.setType("application/pdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            startActivityForResult(Intent.createChooser(intent,"Select PDF"),PICK_PDF_CODE);
        }else{
            showCustomToast("Your version is not support pdf chooser",Toast.LENGTH_LONG);
        }
    }
    public Bitmap getBitmap(ParcelFileDescriptor file){
        int pageNum = 0;
        PdfiumCore pdfiumCore = new PdfiumCore(getContext());
        try {
            PdfDocument pdfDocument = pdfiumCore.newDocument(file);
            pdfiumCore.openPage(pdfDocument, pageNum);

            int width = pdfiumCore.getPageWidthPoint(pdfDocument, pageNum);
            int height = pdfiumCore.getPageHeightPoint(pdfDocument, pageNum);


            // ARGB_8888 - best quality, high memory usage, higher possibility of OutOfMemoryError
            // RGB_565 - little worse quality, twice less memory usage
            Bitmap bitmap = Bitmap.createBitmap(width , height ,
                    Bitmap.Config.ARGB_8888);
            pdfiumCore.renderPageBitmap(pdfDocument, bitmap, pageNum, 0, 0,
                    width, height);
            //if you need to render annotations and form fields, you can use
            //the same method above adding 'true' as last param

            pdfiumCore.closeDocument(pdfDocument); // important!
            return bitmap;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private ArrayList<Bitmap> pdfToBitmap(ParcelFileDescriptor pdfFile) {

        ArrayList<Bitmap> bitmaps = new ArrayList<>();

        try {
            PdfRenderer renderer = new PdfRenderer(pdfFile);

            Bitmap bitmap;
            final int pageCount = renderer.getPageCount();
            for (int i = 0; i < pageCount; i++) {
                PdfRenderer.Page page = renderer.openPage(i);

                int width = getResources().getDisplayMetrics().densityDpi / 72 * page.getWidth();
                int height = getResources().getDisplayMetrics().densityDpi / 72 * page.getHeight();
                bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

                bitmaps.add(bitmap);

                // close the page
                page.close();

            }

            // close the renderer
            renderer.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return bitmaps;

    }

    private void pickGallery() {
        //intent to pick image from gallery
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        //set intent tyoe to image
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);

    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(getActivity(), storagePermission, STORAGE_REQUEST_CODE);
    }

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(getActivity(), camaraPermission, CAMERA_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {

            ContentValues values = new ContentValues();
            //Toast.makeText(getActivity(), "hi:", Toast.LENGTH_SHORT).show();
            values.put(MediaStore.Images.Media.TITLE, "NewPic");
            values.put(MediaStore.Images.Media.DESCRIPTION, "Image to Text");
            imageUri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                CropImage.activity(data.getData())
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(getActivity(), UploadFragment.this);
            }
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                //Bundle extras = data.getExtras();
                //imageBitmap = (Bitmap) extras.get("data");
                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(getActivity(), UploadFragment.this);
                //Toast.makeText(MainActivity.this,"Error 123:" ,Toast.LENGTH_SHORT).show();

                //imageBitmap = BitmapFactory.decodeFile(currentImagePath);
                //imageViewResume.setImageBitmap(imageBitmap);
            }
            if(requestCode == PICK_PDF_CODE){

                Uri selectedPDF = data.getData();
                ParcelFileDescriptor inputPFD = null;

                try {
                    inputPFD = getContext().getContentResolver().openFileDescriptor(selectedPDF, "r");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                //ArrayList<Bitmap> pdfbitmap = pdfToBitmap(inputPFD);
                //Log.d("URI PDF", "onActivityResult: "+pdfbitmap.toString());

                Bitmap pdfbitmap = getBitmap(inputPFD);

                resultUri = getImageUri(getActivity(),pdfbitmap);

                //resultUri = selectedPDF;
                //imageBitmap = pdfbitmap;
                CropImage.activity(resultUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(getActivity(), UploadFragment.this);

                //binding.include.imageViewResume.setImageBitmap(pdfbitmap);

                //FileDescriptor fd = inputPFD.getFileDescriptor();

                //String pdfPath = selectedPDF.toString();

                //File pdfFile = new File(Uri.parse(pdfPath).toString());

                //Log.d("URI PDF", "onActivityResult: "+fd.toString());

               // pdfToBitmap(pdfFile);

            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK) {
                resultUri = result.getUri();//get image uri
                /*Picasso.get()
                        .load(resultUri)
                        .into(binding.include.imageViewResume);*/
                //Log.d("IMAGEURI", "onActivityResult: " + resultUri +" AND" +binding.include.imageViewResume.getDrawable().toString());
                binding.include.imageViewResume.setImageURI(resultUri);
                binding.include.includeStepBar.textViewDot1.setBackgroundResource(R.drawable.circle_text_view_done);
                binding.include.includeStepBar.textViewDot2.setBackgroundResource(R.drawable.circle_text_view_done);
                binding.include.includeStepBar.textViewDot3.setBackgroundResource(R.drawable.circle_text_view_done);
                binding.include.includeStepBar.textViewStep2.setBackgroundResource(R.drawable.circle_text_view_done);
                imageBitmapDrawable = (BitmapDrawable) binding.include.imageViewResume.getDrawable();
                imageBitmap = imageBitmapDrawable.getBitmap();
                //code here
            }
        } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
            showCustomToast(getString(R.string.error_crop),Toast.LENGTH_SHORT);
            //Toast.makeText(getActivity(), "Error :", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
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
                        showCustomToast(getString(R.string.permission_denied),Toast.LENGTH_SHORT);
                        //Toast.makeText(getActivity(), "Permission Denied!", Toast.LENGTH_SHORT).show();
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
                        showCustomToast(getString(R.string.permission_denied),Toast.LENGTH_SHORT);
                        //Toast.makeText(getActivity(), "Permission Denied", Toast.LENGTH_SHORT).show();
                    }
                }
                break;

        }

    }

    private void detectTextFromImage() {

        TextRecognizer recognizer = new TextRecognizer.Builder(getActivity().getApplicationContext()).build();

        if (!recognizer.isOperational()) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showCustomToast(getString(R.string.error_setting_up_text_recognizer),Toast.LENGTH_SHORT);
                    //Toast.makeText(getActivity(), "Error setting up text recognizer", Toast.LENGTH_SHORT).show();
                }
            });
            return;

        } else {
            Frame frame = new Frame.Builder().setBitmap(imageBitmap).build();
            SparseArray<TextBlock> items = recognizer.detect(frame);
            StringBuilder sb = new StringBuilder();
            Log.d("TESTING", "detectTextFromImage: " + items.size());
            for (int i = 0; i < items.size(); i++) {
                TextBlock myItems = items.valueAt(i);
                sb.append(myItems.getValue());
                sb.append("  ");
                Log.d("BOUNDING", "Block " + i + " : " + myItems.getValue() + "Top: " + myItems.getBoundingBox().top + "Bottom :" + myItems.getBoundingBox().bottom);
            }
            /*Intent intent1 = new Intent(this,ExtractedText.class);
            Bundle bundle = new Bundle();
            bundle.putString("BundleText",sb.toString());
            intent1.putExtra("EXTRACTED_TEXT",bundle);
            startActivity(intent1);*/
            extractedTextFromImage = sb.toString().replaceAll("\n", " ");
            String newExtractedText = extractedTextFromImage.replaceAll(" \\ ", ".\n ");
            //extractedTextFromImage.substring(0,1000);
            //textViewExtractedText.setText(extractedTextFromImage);
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    showCustomToast(getString(R.string.extracting_credentials),Toast.LENGTH_SHORT);
                    //Toast.makeText(getActivity(), "No Face Detected", Toast.LENGTH_SHORT).show();
                }
            });
            postData(newExtractedText);
        }
    }

    private boolean detectFace() {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;

        /*Paint myRectPaint = new Paint();
        myRectPaint.setStrokeWidth(5);
        myRectPaint.setColor(Color.RED);
        myRectPaint.setStyle(Paint.Style.STROKE);*/

        Bitmap tempBitmap = Bitmap.createBitmap(imageBitmap.getWidth(), imageBitmap.getHeight(), Bitmap.Config.RGB_565);
        Canvas tempCanvas = new Canvas(tempBitmap);
        tempCanvas.drawBitmap(imageBitmap, 0, 0, null);

        FaceDetector faceDetector = new
                FaceDetector.Builder(getActivity().getApplicationContext()).setTrackingEnabled(false)
                .build();
        if (!faceDetector.isOperational()) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showCustomToast(getString(R.string.could_not_set_up_face_detector),Toast.LENGTH_SHORT);
                    //Toast.makeText(getActivity(), "Could not set up face detector", Toast.LENGTH_LONG).show();
                }
            });
            return true;
        }

        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                showCustomToast(getString(R.string.detecting_face),Toast.LENGTH_SHORT);
                //Toast.makeText(getActivity(), "No Face Detected", Toast.LENGTH_SHORT).show();
            }
        });

        Frame frame1 = new Frame.Builder().setBitmap(imageBitmap).build();
        SparseArray<Face> faces = faceDetector.detect(frame1);
        Log.d("FACE", "Face Size : " + faces.size());
        System.out.println("Face Size : " + faces.size());
        Log.d("FACE", "tempBitmap.getWidth() : " + tempBitmap.getWidth());
        Log.d("FACE", "tempBitmap.getHeight() : " + tempBitmap.getHeight());

        if (faces.size() != 0) {
            for (int i = 0; i < faces.size(); i++) {
                Face thisFace = faces.valueAt(i);
                x1 = thisFace.getPosition().x;
                Log.d("FACE", "x1 : " + x1);
                y1 = thisFace.getPosition().y;
                Log.d("FACE", "y1 : " + y1);
                x2 = x1 + thisFace.getWidth();
                expandWidth = (int) (thisFace.getWidth() * 0.25);
                Log.d("FACE", "x2 : " + x2);
                y2 = y1 + thisFace.getHeight();
                expandHeight = (int) (thisFace.getHeight() * 0.30);
                Log.d("FACE", "y2 : " + y2);
                //tempCanvas.drawRoundRect(new RectF(x1, y1, x2, y2), 2, 2, myRectPaint);
            }
            //tempCanvas.drawBitmap();

            //imageViewResume.setImageDrawable(new BitmapDrawable(getResources(),tempBitmap));

            //Canvas canvas = new Canvas (tempBitmap);
            //imageViewResume.draw(canvas);
            faceWidth = (int) x2 - (int) x1;
            faceHeight = (int) y2 - (int) y1;

            isFaceCropped = true;

            croppedBitmap = Bitmap.createBitmap(tempBitmap, (int) x1 - expandWidth, (int) y1 - expandHeight, faceWidth + (2 * expandWidth), faceHeight + (2 * expandHeight));

            Log.d("FACE URL", "" + croppedFace);

            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    showCustomToast(getString(R.string.face_detected),Toast.LENGTH_SHORT);
                    //Toast.makeText(getActivity(), "No Face Detected", Toast.LENGTH_SHORT).show();
                }
            });
            //imageViewResume.setImageBitmap(croppedBitmap);
            //Rect src = new Rect((int) x1, (int) y1, (int) x2, (int) y2);
            //Rect dst = new Rect(0, 0, 200, 200);
            //tempCanvas.drawBitmap(imageBitmap, src, dst, null);

        } else {

            isFaceCropped = false;

            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    showCustomToast(getString(R.string.no_face_detected),Toast.LENGTH_SHORT);
                    //Toast.makeText(getActivity(), "No Face Detected", Toast.LENGTH_SHORT).show();
                }
            });
            //Resources resources = getActivity().getResources();
            //croppedFace = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + resources.getResourcePackageName(R.drawable.ic_person) + '/' + resources.getResourceTypeName(R.drawable.ic_person) + '/' + resources.getResourceEntryName(R.drawable.ic_person));
            croppedFace = Uri.parse("noProfile");
            //Bitmap noFaceBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_person);
            //intent1.putExtra("EXTRACTED_FACE",noFaceBitmap);

        }
        return false;
    }

    public void postData(final String extractedText) {
        employeeName = "";
        employeeLocation = "";
        employeeOrganization = "";
        employeeEmail = "";
        employeePhoneNum = "";
        employeeAddress = "";
        employeeSkills = "";
        employeeEducation = "";
        employeeOther = "";
        employeeAge = "0";
        stringBuilderSkills = new StringBuilder();
        stringBuilderEducation = new StringBuilder();
        stringBuilderOther = new StringBuilder();

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        JSONObject object = new JSONObject();

        try {
            object.put("parameters", extractedText);
            //object.getString("parameters");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        /*http://192.168.0.187:9000/?properties={"ner.model":"/Users/ASUS/Desktop/stanford-corenlp-full-2018-10-05/ner-model.ser.gz,
        edu/stanford/nlp/models/ner/english.muc.7class.distsim.crf.ser.gz","annotators":"tokenize,ssplit,ner"}*/
        String url = "http://192.168.0.187:9000/?properties=%7B" +
                "%22ner.model%22%3A%22/Users/ASUS/Desktop/stanford-corenlp-full-2018-10-05/ner-model.ser.gz," +
                "edu/stanford/nlp/models/ner/english.all.3class.distsim.crf.ser.gz,edu/stanford/nlp/models/ner/english.conll.4class.distsim.crf.ser.gz," +
                "edu/stanford/nlp/models/ner/english.muc.7class.distsim.crf.ser.gz%22%2C" +
                "%22annotators%22%3A%22tokenize%2Cssplit%2Cner%22%2C" +
                "%22ner.combinationMode%22%3A%22HIGH_RECALL%22%2C"+
                "%22ner.useSUTime%22%3Afalse%2C" +
                "%22ner.applyNumericClassifiers%22%3Afalse%7D";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONArray ary = response.getJSONArray("sentences");
                            for (int i = 0; i < ary.length(); i++) {

                                JSONObject obj1 = ary.getJSONObject(i);
                                JSONArray testarray = obj1.getJSONArray("entitymentions");
                                System.out.println(testarray);

                                JSONArray ary2 = obj1.getJSONArray("entitymentions");

                                //textViewExtractedText.setText("--- Result ---\n\n");

                                for (int y = 0; y < ary2.length(); y++) {
                                    JSONObject obj2 = ary2.getJSONObject(y);
                                    String namedEntity = obj2.getString("ner");
                                    String namedEntityResult = obj2.getString("text");
                                    if (namedEntity.equals("PERSON") && employeeName.isEmpty()) {
                                        employeeName = namedEntityResult;
                                        Log.d("NAMEFOUND : ", namedEntityResult);
                                    } else if ((namedEntity.equals("LOCATION") || namedEntity.equals("CITY") || namedEntity.equals("COUNTRY")) && employeeLocation.isEmpty()) {
                                        employeeLocation = namedEntityResult;
                                        Log.d("LOCATIONFOUND", namedEntityResult);
                                    } else if (namedEntity.equals("ORGANIZATION")) {
                                        employeeOrganization = namedEntityResult;
                                    } else if (namedEntity.equals("SKILL")) {
                                        //employeeSkills = namedEntityResult;
                                        if (employeeSkills.isEmpty()) {
                                            employeeSkills = namedEntityResult;
                                            stringBuilderSkills.append(employeeSkills);
                                        } else {
                                            employeeSkills = namedEntityResult;
                                            stringBuilderSkills.append(" , " + employeeSkills);
                                        }

                                    } else if (namedEntity.equals("EDUCATION")) {
                                        if(employeeEducation.isEmpty()){
                                            employeeEducation = namedEntityResult;
                                            stringBuilderEducation.append(employeeEducation);
                                        }else {
                                            employeeEducation = namedEntityResult;
                                            if (stringBuilderEducation.indexOf(employeeEducation) == -1) {
                                                stringBuilderEducation.append( "\n"+ employeeEducation);
                                            }
                                        }
                                    }
                                    /*else if(namedEntity.equals("COURSE")||namedEntity.equals("SUBJECT")||namedEntity.equals("CERTIFICATE")){
                                        employeeOther = namedEntityResult;
                                        stringBuilderOther.append(employeeOther+"\n");

                                    }*/
                                    //textViewExtractedText.append(namedEntity+" : "+namedEntityResult+"\n");
                                    Log.d("NER", namedEntity + " : " + namedEntityResult + "\n");
                                }
                            }

                            employeeSkills = stringBuilderSkills.toString();
                            employeeEducation = stringBuilderEducation.toString();
                            employeeOther = stringBuilderOther.toString();

                            String str = extractedText;

                            //old phone regex :String phoneRegex = "((\\(60\\+\\))?^(01\\d{1}-? ?\\d{3,4} ?\\d{4}))(?!\\d)";
                            //Phone regex use (?!\\d) is a negative lookahead, means the following regex cannot be a number.
                            // (?<![\\d]) is a negative lookbehind which the preceding elements cannot be digit.
                            // It will match from (+60)1.. or 011 ...
                            String phoneRegex = "(?<![\\d])((((\\()?\\+60(\\))?) ?(1\\d{1}-? ?\\d{3,4} ?\\d{3,4}))|(01\\d{1}-? ?\\d{3,4} ?\\d{3,4}))(?!\\d)";

                            // age has the regex from 10 years old to 59 years old and (?<![\\d]) is a negative lookbehind which the preceding
                            //elements cannot be digit.
                            String ageHeader = "(Age|age|AGE):? ?";
                            String ageNo = "(?<![\\d])[1-5][0-9](?!\\d)";
                            String ageFormat = " ?(years old|Years Old|YEARS OLD|-year(s)?-old)";
                            String ageRegex = ageHeader + ageNo +
                                    "|" +
                                    ageNo + ageFormat +
                                    "|" +
                                    "[.\n] ?"+ageNo + " ?[.\n]";

                            //Old address: String addressRegex = addressHeader + addressFormat+"((\\d{1,2})?-?(\\d{1,2}[A-Z]?)?\\d{1,4},.+?)"+location;
                            //Address use ".+?" which is the lazy matching which matching as few characters as possible between \n and postCode and
                            //location. If location not found, then, it will match the nearest addressNo and postCode.
                            // "." will match any character except newline.
                            //".+?" Makes the preceding quantifier lazy, causing it to match as few characters as possible. By default, quantifiers are greedy, and will match as many characters as possible.
                            String addressHeader = "(Address|address|ADDRESS):? ?";
                            String addressFormat = "((NO|no|No|LOT|Lot|lot).?:?)? ?";
                            String addressNo = "(\\d{1,2})?-?(\\d{1,2}[A-Z]?)?\\d{1,4},?";
                            String postCode = "(?<![\\d])[0-9]{5}(?!\\d)";
                            String location = employeeLocation;
                            /*String addressRegex = addressHeader + addressFormat +
                                    "(" + addressFormat + ".+?" + postCode + ".+?" + location + ")" +
                                    "|" +
                                    "(" + addressNo + ".+?" + postCode + ".+?" + location + ")" +
                                    "|" +
                                    addressNo + ".+?" + postCode;*/

                            String addressRegex =
                                    "(" + addressHeader + ".+?" + postCode + ".+?" + location + ")" +
                                            "|" +
                                    "(" + ".+?" + postCode + ".+?" + location + ")" +
                                            "|" +
                                            addressNo + ".+?" + postCode;

                            String emailRegex = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"" +
                                    "(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])" +
                                    "*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|" +
                                    "[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:" +
                                    "(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";

                            Pattern allPattern = Pattern.compile(phoneRegex + "|" + emailRegex + "|" + addressRegex + "|" + ageRegex);
                            Matcher allMatcher = allPattern.matcher(str);
                            while (allMatcher.find()) {
                                Log.d("REXRESULT", "onResponse: "+ allMatcher.group(0));
                                String result = allMatcher.group(0);
                                if (result.matches(phoneRegex) && employeePhoneNum.isEmpty()) {
                                    employeePhoneNum = result;
                                } else if (result.matches(addressRegex) && employeeAddress.isEmpty()) {
                                    int indexOfAddress = 0;
                                    String newAddress="";
                                    if(result.contains("Address")){
                                        indexOfAddress = result.indexOf("Address")+8;
                                        newAddress = result.substring(indexOfAddress);
                                        result = newAddress;
                                    }else if (result.contains("ADDRESS")){
                                        indexOfAddress = result.indexOf("ADDRESS")+8;
                                        newAddress = result.substring(indexOfAddress);
                                        result = newAddress;
                                    }

                                    employeeAddress = result;

                                } else if (result.matches(emailRegex) && employeeEmail.isEmpty()) {
                                    employeeEmail = result;
                                } else if (result.matches(ageRegex) && employeeAge.equals("0")) {
                                    employeeAge = result;
                                    Pattern p = Pattern.compile("\\d+");
                                    Matcher m = p.matcher(employeeAge);
                                    while (m.find()) {
                                        employeeAge = m.group();
                                    }
                                    //employeeAge.substring(0, 1);
                                    //Log.d("AGE", employeeAge + "subString" + employeeAge.substring(0, 1));
                                }
                            }

                            checkExtractedIsEmpty();

                            if(isFaceCropped){
                                croppedFace = getImageUri(getActivity(), croppedBitmap);
                            }

                            showSuccessCustomToast(getString(R.string.extracted_successfully),Toast.LENGTH_SHORT);

                            Bundle bundle = new Bundle();
                            bundle.putString("EXTRACTED_PHONE", employeePhoneNum);
                            bundle.putString("EXTRACTED_EMAIL", employeeEmail);
                            bundle.putString("EXTRACTED_NAME", employeeName);
                            bundle.putString("EXTRACTED_ADDRESS", employeeAddress);
                            bundle.putString("EXTRACTED_SKILLS", employeeSkills);
                            bundle.putString("EXTRACTED_EDUCATION", employeeEducation);
                            //intent1.putExtra("EXTRACTED_OTHER",employeeOther);
                            bundle.putInt("EXTRACTED_AGE", Integer.parseInt(employeeAge.trim()));
                            bundle.putString("EXTRACTED_FACE", croppedFace.toString());
                            bundle.putString("RESUME", resultUri.toString());

                            binding.progressBar.setVisibility(ProgressBar.INVISIBLE);
                            navController.navigate(R.id.action_uploadFragment_to_extractedTextEditFragment, bundle);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                binding.progressBar.setVisibility(ProgressBar.INVISIBLE);

                showCustomToast(getString(R.string.error_getting_response),Toast.LENGTH_LONG);
                //Toast.makeText(getActivity(), "Error getting response, try again", Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.add(jsonObjectRequest);

    }

    private void checkExtractedIsEmpty() {

        if (employeeName.isEmpty()) {
            employeeName = getString(R.string.no_name_found);
        }

        if (employeePhoneNum.isEmpty()) {
            employeePhoneNum = getString(R.string.no_phone_found);
        }

        if (employeeEmail.isEmpty()) {
            employeeEmail = getString(R.string.no_email_found);
        }

        if (employeeAddress.isEmpty()) {
            employeeAddress = getString(R.string.no_address_found);
        }

        if (employeeSkills.isEmpty()) {
            employeeSkills = getString(R.string.no_skills_found);
        }

        if (employeeEducation.isEmpty()) {
            employeeEducation = getString(R.string.no_education_found);
        }
    }

    private Uri getImageUri(Context context, Bitmap inImage) {
        String timeStamp = new SimpleDateFormat("yyyMMdd_HHmmss").format(new Date());
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, timeStamp, null);
        return Uri.parse(path);
    }

    private void showCustomToast(String msg, int length){

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast, getActivity().findViewById(R.id.custom_toast_container));

        TextView text = layout.findViewById(R.id.text);
        text.setText(msg);

        Toast toast = new Toast(getActivity().getApplicationContext());
        toast.setGravity(Gravity.BOTTOM, 0, 120);
        toast.setDuration(length);
        toast.setView(layout);
        toast.show();

    }

    private void showSuccessCustomToast(String msg, int length){

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast_success, getActivity().findViewById(R.id.custom_toast_container_success));

        TextView text = layout.findViewById(R.id.text);
        text.setText(msg);

        Toast toast = new Toast(getActivity().getApplicationContext());
        toast.setGravity(Gravity.BOTTOM, 0, 120);
        toast.setDuration(length);
        toast.setView(layout);
        toast.show();

    }

    class ExtractThread implements Runnable {

        @Override
        public void run() {
            if (detectFace()) return;
            detectTextFromImage();
            binding.fab.setClickable(true);
        }
    }
}
