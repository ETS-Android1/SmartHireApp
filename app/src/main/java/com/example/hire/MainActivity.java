package com.example.hire;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.hire.databinding.ActivityFabBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import edu.stanford.nlp.pipeline.*;

import android.app.AlertDialog;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

public class MainActivity extends AppCompatActivity {

    private RequestQueue mQueue;
    private Bitmap imageBitmap,croppedBitmap;
    private BitmapDrawable imageBitmapDrawable;
    private float x1,x2,y1,y2;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    String currentImagePath = null;
    Uri imageUri;
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 400;
    private static final int IMAGE_PICK_GALLERY_CODE = 1000;
    String camaraPermission[];
    String storagePermission[];

    private String employeePhoneNum,employeeEmail,employeeAddress,employeeName,employeeLocation,employeeEducation,employeeAge,employeeSkills,employeeOther,employeeOrganization;
    private String extractedTextFromImage="";
    Uri croppedFace,resultUri;

    Intent intent1;

    Animation fabOpen, fabClose, rotateForward, rotateBackward;
    boolean isOpen = false;

    private int progressStatus = 0;
    private Handler handler = new Handler();
    private boolean isCanceled;

    private int expandWidth, expandHeight, faceWidth, faceHeight;

    StringBuilder stringBuilderSkills;
    StringBuilder stringBuilderEducation ;
    StringBuilder stringBuilderOther ;

    private ActivityFabBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFabBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        camaraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        intent1 = new Intent(getApplicationContext(),ExtractedText.class);

        mQueue = Volley.newRequestQueue(this);

        fabOpen = AnimationUtils.loadAnimation(this,R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(this,R.anim.fab_close);

        rotateForward = AnimationUtils.loadAnimation(this,R.anim.rotate_forward);
        rotateBackward = AnimationUtils.loadAnimation(this,R.anim.rotate_backward);

        setSupportActionBar(binding.include.toolbarMain);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //getSupportActionBar().setTitle("Hirkle");
        //getSupportActionBar().setIcon(getDrawable(R.drawable.hire_logo));

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                animateFab();
                Log.d("FAB", ""+isOpen);
            }
        });

        binding.fabCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"Capture the resume",Toast.LENGTH_SHORT).show();
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
                Toast.makeText(MainActivity.this,"Choose a resume",Toast.LENGTH_SHORT).show();
                animateFab();

                if(!checkStoragePermission()){
                    requestStoragePermission();
                }else{
                    pickGallery();
                }
            }
        });

        binding.fabExtract.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {
                //fab.setVisibility(FloatingActionButton.INVISIBLE);
                binding.fab.setClickable(false);
                Toast.makeText(MainActivity.this,"Extracting...",Toast.LENGTH_LONG).show();
                animateFab();
                binding.progressBar.setVisibility(ProgressBar.VISIBLE);
                ExtractThread extractThread = new ExtractThread();
                new Thread(extractThread).start();

            }
        });

    }

    private void animateFab(){
        if (isOpen){
            binding.fab.startAnimation(rotateBackward);
            binding.fabCamera.startAnimation(fabClose);
            //textViewCamera.setVisibility(View.GONE);
            binding.textViewCamera.setAnimation(fabClose);
            binding.textViewGallery.setAnimation(fabClose);
            binding.textViewExtract.setAnimation(fabClose);
            binding.fabGallery.startAnimation(fabClose);
            binding.fabExtract.startAnimation(fabClose);
            binding.fabCamera.setClickable(false);
            binding.fabGallery.setClickable(false);
            binding.fabExtract.setClickable(false);
            isOpen=false;
        }else{
            binding.fab.startAnimation(rotateForward);
            binding.fabCamera.startAnimation(fabOpen);
            //textViewCamera.setVisibility(View.VISIBLE);
            binding.fabGallery.startAnimation(fabOpen);
            binding.fabExtract.startAnimation(fabOpen);
            binding.textViewCamera.setAnimation(fabOpen);
            binding.textViewGallery.setAnimation(fabOpen);
            binding.textViewExtract.setAnimation(fabOpen);
            //textViewExtract.setBackgroundResource(R.color.colorPrimary);
            binding.textViewCamera.setBackgroundResource(R.drawable.rounded_corner);
            binding.textViewGallery.setBackgroundResource(R.drawable.rounded_corner);
            binding.textViewExtract.setBackgroundResource(R.drawable.rounded_corner);
            binding.fabCamera.setClickable(true);
            binding.fabGallery.setClickable(true);
            binding.fabExtract.setClickable(true);
            isOpen=true;
        }
    }

    private void extractText() {
        /*String addressRegex = "(Address|employeeAddress|ADDRESS):?";
        String abc = " \n ";
        final Pattern pattern = Pattern.compile(addressRegex+"(.+?)"+abc, Pattern.DOTALL);
        final Matcher matcher = pattern.matcher("Age: 12. \n Email: yujune99@gmail.com. \n Address: No.49 Jalan\nCempaka Wangi 12, Taman Cempaka, 42700, Banting, Selangor. \n Hi");
        matcher.find();
        textViewExtractedText.setText(matcher.group(2));*/
        String val="abc KEYWORD1 def KEYWORD1 ghi KEYWORD2 jkl KEYWORD2";
        String REGEX="KEYWORD1((.(?!KEYWORD1))+?)KEYWORD2";
        Pattern pattern = Pattern.compile(REGEX);
        Matcher matcher = pattern.matcher(val);
        if(matcher.find()){
            System.out.println(matcher.group());
        }
    }

    private void detectTextFromImage() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable=true;

        /*Paint myRectPaint = new Paint();
        myRectPaint.setStrokeWidth(5);
        myRectPaint.setColor(Color.RED);
        myRectPaint.setStyle(Paint.Style.STROKE);*/

        Bitmap tempBitmap = Bitmap.createBitmap(imageBitmap.getWidth(), imageBitmap.getHeight(), Bitmap.Config.RGB_565);
        Canvas tempCanvas = new Canvas(tempBitmap);
        tempCanvas.drawBitmap(imageBitmap, 0, 0, null);

        FaceDetector faceDetector = new
                FaceDetector.Builder(getApplicationContext()).setTrackingEnabled(false)
                .build();
        if(!faceDetector.isOperational()){
            new AlertDialog.Builder(getApplicationContext()).setMessage("Could not set up the face detector!").show();
            return;
        }

        Frame frame1 = new Frame.Builder().setBitmap(imageBitmap).build();
        SparseArray<Face> faces = faceDetector.detect(frame1);
        Log.d("FACE", "Face Size : "+faces.size());
        System.out.println("Face Size : "+faces.size());
        Log.d("FACE", "tempBitmap.getWidth() : "+tempBitmap.getWidth());
        Log.d("FACE", "tempBitmap.getHeight() : "+tempBitmap.getHeight());

        if(faces.size()!=0){
            for(int i=0; i<faces.size(); i++) {
                Face thisFace = faces.valueAt(i);
                x1 = thisFace.getPosition().x;
                Log.d("FACE", "x1 : "+x1);
                y1 = thisFace.getPosition().y;
                Log.d("FACE", "y1 : "+y1);
                x2 = x1 + thisFace.getWidth();
                expandWidth = (int)(thisFace.getWidth()*0.25);
                Log.d("FACE", "x2 : "+x2);
                y2 = y1 + thisFace.getHeight();
                expandHeight = (int)(thisFace.getHeight()*0.30);
                Log.d("FACE", "y2 : "+y2);
                //tempCanvas.drawRoundRect(new RectF(x1, y1, x2, y2), 2, 2, myRectPaint);

            }
            //tempCanvas.drawBitmap();

            //imageViewResume.setImageDrawable(new BitmapDrawable(getResources(),tempBitmap));

            //Canvas canvas = new Canvas (tempBitmap);
            //imageViewResume.draw(canvas);
            faceWidth = (int)x2-(int)x1;
            faceHeight = (int)y2-(int)y1;
            croppedBitmap = Bitmap.createBitmap(tempBitmap,(int)x1-expandWidth,(int)y1-expandHeight,faceWidth+(2*expandWidth),faceHeight+(2*expandHeight));
            croppedFace = getImageUri(MainActivity.this,croppedBitmap);
            //imageViewResume.setImageBitmap(croppedBitmap);
            //Rect src = new Rect((int) x1, (int) y1, (int) x2, (int) y2);
            //Rect dst = new Rect(0, 0, 200, 200);
            //tempCanvas.drawBitmap(imageBitmap, src, dst, null);
        }else{
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(MainActivity.this, "No Face Detected", Toast.LENGTH_SHORT).show();
                }
            });
            Resources resources = MainActivity.this.getResources();
            croppedFace = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + resources.getResourcePackageName(R.drawable.ic_person) + '/' + resources.getResourceTypeName(R.drawable.ic_person) + '/' + resources.getResourceEntryName(R.drawable.ic_person));
            //Bitmap noFaceBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_person);
            //intent1.putExtra("EXTRACTED_FACE",noFaceBitmap);

        }

        TextRecognizer recognizer = new TextRecognizer.Builder(getApplicationContext()).build();

        if (!recognizer.isOperational()) {
            Toast.makeText(MainActivity.this, "Error :", Toast.LENGTH_SHORT).show();
        } else {
            Frame frame = new Frame.Builder().setBitmap(imageBitmap).build();
            SparseArray<TextBlock> items = recognizer.detect(frame);
            StringBuilder sb = new StringBuilder();
            Log.d("TESTING", "detectTextFromImage: "+ items.size());
            for (int i = 0; i < items.size(); i++) {
                TextBlock myItems = items.valueAt(i);
                sb.append(myItems.getValue());
                sb.append("  ");
                Log.d("BOUNDING","Block "+i +" : " +myItems.getValue()+ "Top: "+myItems.getBoundingBox().top+"Bottom :"+myItems.getBoundingBox().bottom);
            }
            /*Intent intent1 = new Intent(this,ExtractedText.class);
            Bundle bundle = new Bundle();
            bundle.putString("BundleText",sb.toString());
            intent1.putExtra("EXTRACTED_TEXT",bundle);
            startActivity(intent1);*/
            extractedTextFromImage = sb.toString().replaceAll("\n"," ");
            String newExtractedText = extractedTextFromImage.replaceAll(" \\ ",".\n ");
            //extractedTextFromImage.substring(0,1000);
            //textViewExtractedText.setText(extractedTextFromImage);
            postData(newExtractedText);


        }

    }

    private Uri getImageUri(Context context, Bitmap inImage) {
        String timeStamp = new SimpleDateFormat("yyyMMdd_HHmmss").format(new Date());
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, timeStamp, null);
        return Uri.parse(path);
    }

    // Post Request For JSONObject
    public void postData(final String extractedText) {
        employeeName ="";
        employeeLocation ="";
        employeeOrganization="";
        employeeEmail="";
        employeePhoneNum="";
        employeeAddress ="";
        employeeSkills="";
        employeeEducation="";
        employeeOther="";
        employeeAge="";
        stringBuilderSkills = new StringBuilder();
        stringBuilderEducation = new StringBuilder();
        stringBuilderOther = new StringBuilder();

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JSONObject object = new JSONObject();

        try {
            object.put("parameters",extractedText);
            //object.getString("parameters");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = "http://192.168.0.187:9000/?properties=%7B%22ner.model%22%3A%22/Users/ASUS/Desktop/stanford-corenlp-full-2018-10-05/ner-model.ser.gz,edu/stanford/nlp/models/ner/english.muc.7class.distsim.crf.ser.gz%22%2C%22annotators%22%3A%22tokenize%2Cssplit%2Cner%22%7D";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url,object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONArray ary = response.getJSONArray("sentences");
                            for(int i=0;i<ary.length();i++){

                                JSONObject obj1 = ary.getJSONObject(i);
                                JSONArray testarray = obj1.getJSONArray("entitymentions");
                                System.out.println(testarray);

                                JSONArray ary2 =  obj1.getJSONArray("entitymentions");

                                //textViewExtractedText.setText("--- Result ---\n\n");

                                for(int y=0;y<ary2.length();y++){
                                    JSONObject obj2 = ary2.getJSONObject(y);
                                    String namedEntity = obj2.getString("ner");
                                    String namedEntityResult = obj2.getString("text");
                                    if(namedEntity.equals("PERSON") && employeeName.isEmpty()){
                                        employeeName = namedEntityResult;
                                        Log.d("NAMEFOUND : ", namedEntityResult);
                                    }else if((namedEntity.equals("LOCATION") || namedEntity.equals("CITY") ||namedEntity.equals("COUNTRY"))&& employeeLocation.isEmpty()){
                                        employeeLocation = namedEntityResult;
                                        Log.d("LOCATIONFOUND", namedEntityResult);
                                    }else if (namedEntity.equals("ORGANIZATION")){
                                        employeeOrganization = namedEntityResult;
                                    }else if (namedEntity.equals("SKILL")){
                                        //employeeSkills = namedEntityResult;
                                        if (employeeSkills.isEmpty()){
                                            employeeSkills = namedEntityResult;
                                            stringBuilderSkills.append(employeeSkills);
                                        }else{
                                            employeeSkills = namedEntityResult;
                                            stringBuilderSkills.append(" , " + employeeSkills);
                                        }

                                    }else if(namedEntity.equals("EDUCATION")){
                                        employeeEducation = namedEntityResult;
                                        if(stringBuilderEducation.indexOf(employeeEducation)==-1){
                                            stringBuilderEducation.append(employeeEducation+"\n");
                                        }

                                    }
                                    /*else if(namedEntity.equals("COURSE")||namedEntity.equals("SUBJECT")||namedEntity.equals("CERTIFICATE")){
                                        employeeOther = namedEntityResult;
                                        stringBuilderOther.append(employeeOther+"\n");

                                    }*/
                                    //textViewExtractedText.append(namedEntity+" : "+namedEntityResult+"\n");
                                    Log.d("NER", namedEntity+" : "+namedEntityResult+"\n");
                                }
                            }

                            employeeSkills = stringBuilderSkills.toString();
                            employeeEducation = stringBuilderEducation.toString();
                            employeeOther = stringBuilderOther.toString();

                            String str = extractedText;
                            String phoneRegex = "((60+))?\\d{2,3}-? ?\\d{3,4} ?\\d{4,5}";
                            String ageHeader = "((Age|age):?)?";
                            String ageRegex = ageHeader +"\\d{2} ?(years)? (old)?";
                            String addressHeader = "((Address|address|ADDRESS):?)?";
                            String addressFormat = "((NO|no|No|LOT|Lot|lot).?:?)?";
                            String location = employeeLocation;
                            String addressRegex = addressHeader + addressFormat+"((\\d{1,2})?-?(\\d{1,2}[A-Z]?)?\\d{1,4},.+?)"+location;
                            String emailRegex = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";

                            Pattern allPattern = Pattern.compile(phoneRegex+"|"+emailRegex+"|"+addressRegex+"|"+ageRegex);
                            Matcher allMatcher = allPattern.matcher(str);
                            while(allMatcher.find()){
                                String result = allMatcher.group(0);
                                if (result.matches(phoneRegex) && employeePhoneNum.isEmpty()){
                                    employeePhoneNum = result;
                                }else if(result.matches(addressRegex)&& employeeAddress.isEmpty()){
                                    employeeAddress = result;
                                }else if(result.matches(emailRegex) && employeeEmail.isEmpty()){
                                    employeeEmail = result;
                                }else if(result.matches(ageRegex) && employeeAge.isEmpty()){
                                    employeeAge = result;
                                    employeeAge.substring(0,1);
                                    Log.d("AGE:",employeeAge+employeeAge.substring(0,1));
                                }
                            }

                            /*Pattern phonePattern = Pattern.compile("((60+))?\\d{2,3}-? ?\\d{3,4} ?\\d{4,5}");
                            Pattern emailPattern = Pattern.compile("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])");
                            Matcher phoneMatcher = phonePattern.matcher(str);
                            if (phoneMatcher.find()) {
                                employeePhoneNum = phoneMatcher.group(0);
                                //textViewExtractedText.append("Phone Number: "+employeePhoneNum+"\n");

                            }

                            Matcher emailMatcher = emailPattern.matcher(str);
                            if (emailMatcher.find()) {
                                employeeEmail = emailMatcher.group(0);
                                //textViewExtractedText.append("Email: "+employeeEmail+"\n");
                            }

                            String addressRegex = "(Address|employeeAddress|ADDRESS):?";
                            String addressFormat = "(NO|no|No|LOT|Lot|lot)?.?:?";
                            String location = employeeLocation;
                            Pattern addressPattern = Pattern.compile(addressFormat+"((\\d{1,2})?-?(\\d{1,2}[A-Z]?)?\\d{1,4},.+?)"+(location),Pattern.DOTALL);
                            Matcher addressMatcher = addressPattern.matcher(str);
                            if (addressMatcher.find()) {
                                employeeAddress = addressMatcher.group(0);
                                Log.d("Address: ",employeeAddress);
                                //textViewExtractedText.append("Address :"+employeeAddress+"\n");
                            }*/
                            //textViewExtractedText.setText(matcher.group(2));
                            //Bundle bundle = new Bundle();
                            //bundle.putString("BundleText",sb.toString());

                            intent1.putExtra("EXTRACTED_PHONE", employeePhoneNum);
                            intent1.putExtra("EXTRACTED_EMAIL", employeeEmail);
                            intent1.putExtra("EXTRACTED_NAME", employeeName);
                            intent1.putExtra("EXTRACTED_ADDRESS", employeeAddress);
                            intent1.putExtra("EXTRACTED_SKILLS",employeeSkills);
                            intent1.putExtra("EXTRACTED_EDUCATION",employeeEducation);
                            //intent1.putExtra("EXTRACTED_OTHER",employeeOther);
                            intent1.putExtra("EXTRACTED_AGE",Integer.parseInt(employeeAge.trim()));
                            intent1.putExtra("EXTRACTED_FACE",croppedFace.toString());
                            intent1.putExtra("RESUME",resultUri.toString());

                            binding.progressBar.setVisibility(ProgressBar.INVISIBLE);
                            startActivity(intent1);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                binding.progressBar.setVisibility(ProgressBar.INVISIBLE);
                Toast.makeText(getApplicationContext(),"Error getting response",Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.add(jsonObjectRequest);

    }

    private void pickGallery() {
        //intent to pick image from gallery
        Intent intent = new Intent(Intent.ACTION_PICK);
        //set intent tyoe to image
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_PICK_GALLERY_CODE);

    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storagePermission, STORAGE_REQUEST_CODE);
    }

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, camaraPermission, CAMERA_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }


    /*private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            File imageFile = null;

            try {
                imageFile = getImageFile();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            if (imageFile != null) {
                //ContentValues values = new ContentValues();
                //values.put(MediaStore.Images.Media.TITLE,"NewPic");
                //values.put(MediaStore.Images.Media.DESCRIPTION,"Image to Text");
                //imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
                imageUri = FileProvider.getUriForFile(this, "com.example.android.fileprovider", imageFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }

        }
    }*/


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            ContentValues values = new ContentValues();
            Toast.makeText(MainActivity.this, "hi:", Toast.LENGTH_SHORT).show();
            values.put(MediaStore.Images.Media.TITLE, "NewPic");
            values.put(MediaStore.Images.Media.DESCRIPTION, "Image to Text");
            imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }

    }


    @SuppressLint("RestrictedApi")
    @Override
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
                binding.include.imageViewResume.setImageURI(resultUri);
                imageBitmapDrawable = (BitmapDrawable) binding.include.imageViewResume.getDrawable();
                imageBitmap = imageBitmapDrawable.getBitmap();
                //code here
            }
        } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
            Toast.makeText(MainActivity.this, "Error :", Toast.LENGTH_SHORT).show();
        }

    }

    /*private void firebaseTextDetecter(){
        final FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(imageBitmap);
        FirebaseVisionTextDetector firebaseVisionTextDetector = FirebaseVision.getInstance().getVisionTextDetector();
        firebaseVisionTextDetector.detectInImage(firebaseVisionImage).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {

                displayTextFromImage(firebaseVisionText);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Error :" + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("Error :", e.getMessage());
            }
        });
    }*/




    /*private void displayTextFromImage(FirebaseVisionText firebaseVisionText) {

        List<FirebaseVisionText.Block> blockList = firebaseVisionText.getBlocks();
        if (blockList.size() == 0) {
            Toast.makeText(MainActivity.this, "No Text Founded in this image", Toast.LENGTH_SHORT).show();
        } else {
            for (FirebaseVisionText.Block block : firebaseVisionText.getBlocks()) {
                String text = block.getText();
                textViewExtractedText.setText(text);
            }
        }
    }*/

    private File getImageFile() throws Exception {

        String timeStamp = new SimpleDateFormat("yyyMMdd_HHmmss").format(new Date());
        String imageName = "jpg_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File imageFile = File.createTempFile(imageName, ".jpg", storageDir);
        currentImagePath = imageFile.getAbsolutePath();
        return imageFile;


    }

    @Override
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
                        Toast.makeText(MainActivity.this,"Permission Denied!" ,Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(MainActivity.this,"Permission Denied!" ,Toast.LENGTH_SHORT).show();
                    }
                }
                break;

        }

    }

    private void intermediateProgressbar(){
        isCanceled = false;
        // Initialize a new instance of progress dialog
        final ProgressDialog pd = new ProgressDialog(MainActivity.this);

        // Set progress dialog style horizontal
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        // Set the progress dialog title and message
        pd.setTitle("Title of progress dialog.");
        pd.setMessage("Loading.........");
        // Set the progress dialog background color
        pd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFD4D9D0")));

        pd.setIndeterminate(false);
                /*
                    Set the progress dialog non cancelable
                    It will disallow user's to cancel progress dialog by clicking outside of dialog
                    But, user's can cancel the progress dialog by cancel button
                 */
        pd.setCancelable(false);

        pd.setMax(100);

        pd.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener(){
            // Set a click listener for progress dialog cancel button
            @Override
            public void onClick(DialogInterface dialog, int which){
                // dismiss the progress dialog
                pd.dismiss();
                // Tell the system about cancellation
                isCanceled = true;
            }
        });

        pd.show();

        // Set the progress status zero on each button click
        progressStatus = 0;

        // Start the lengthy operation in a background thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(progressStatus < pd.getMax()){
                    // If user's click the cancel button from progress dialog
                    if(isCanceled)
                    {
                        // Stop the operation/loop
                        break;
                    }
                    // Update the progress status
                    progressStatus +=1;

                    // Try to sleep the thread for 200 milliseconds
                    try{
                        Thread.sleep(200);
                    }catch(InterruptedException e){
                        e.printStackTrace();
                    }

                    // Update the progress bar
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            // Update the progress status
                            pd.setProgress(progressStatus);
                            //textViewProgress.setText(progressStatus+"");
                            // If task execution completed
                            if(progressStatus == pd.getMax()){
                                // Dismiss/hide the progress dialog
                                pd.dismiss();
                                //textViewProgress.setText("Operation completed.");
                            }
                        }
                    });
                }
            }
        }).start(); // Start the operation
    }

    class ExtractThread implements Runnable{

        @Override
        public void run() {
            detectTextFromImage();
            binding.fab.setClickable(true);

        }
    }

}

