package com.example.hire;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
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
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

public class MainActivity extends AppCompatActivity {

    private Button buttonCamera, buttonExtract, buttonGallery,buttonPost;
    private RequestQueue mQueue;
    private ImageView imageViewResume;
    private TextView textViewExtractedText, textViewProgress;
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

    String phoneNumber="";
    String email="";
    String address="";
    String extractedTextFromImage="";
    String extractedName="";
    String extractedLocation = "";
    String extractedOrganization="";

    Intent intent1;

    FloatingActionButton fab, fabCamera, fabGallery, fabExtract;
    TextView textViewCamera, textViewGallery, textViewExtract;
    Animation fabOpen, fabClose, rotateForward, rotateBackward;
    boolean isOpen = false;

    ProgressBar progressBar;

    private int progressStatus = 0;
    private Handler handler = new Handler();
    private boolean isCanceled;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fab);
        //buttonCamera = findViewById(R.id.buttonCamera);
        //buttonExtract = findViewById(R.id.buttonExtract);
        //buttonGallery = findViewById(R.id.buttonGallery);
        //buttonPost=findViewById(R.id.buttonPost);
        imageViewResume = findViewById(R.id.imageViewResume);
        //textViewExtractedText = findViewById(R.id.textViewExtractedText);

        camaraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        intent1 = new Intent(getApplicationContext(),ExtractedText.class);

        mQueue = Volley.newRequestQueue(this);

        fab = findViewById(R.id.fab);
        fabCamera = findViewById(R.id.fabCamera);
        fabGallery = findViewById(R.id.fabGallery);
        fabExtract = findViewById(R.id.fabExtract);

        textViewCamera = findViewById(R.id.textViewCamera);
        textViewGallery= findViewById(R.id.textViewGallery);
        textViewExtract = findViewById(R.id.textViewExtract);

        progressBar = findViewById(R.id.progressBar);

        fabOpen = AnimationUtils.loadAnimation(this,R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(this,R.anim.fab_close);

        rotateForward = AnimationUtils.loadAnimation(this,R.anim.rotate_forward);
        rotateBackward = AnimationUtils.loadAnimation(this,R.anim.rotate_backward);

        Log.d("FAB", ""+isOpen);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                animateFab();
                Log.d("FAB", ""+isOpen);
            }
        });

        fabCamera.setOnClickListener(new View.OnClickListener() {
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

        fabGallery.setOnClickListener(new View.OnClickListener() {
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

        fabExtract.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {
                //fab.setVisibility(FloatingActionButton.INVISIBLE);
                fab.setClickable(false);
                Toast.makeText(MainActivity.this,"Extracting...",Toast.LENGTH_LONG).show();
                animateFab();
                progressBar.setVisibility(ProgressBar.VISIBLE);
                ExtractThread extractThread = new ExtractThread();
                new Thread(extractThread).start();



            }
        });

        /*buttonPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //jsonParse();
                //Submit("Tan Hao Yang lives in Seetapak");
                //postData();
                extractText();
            }
        });*/

        /*buttonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!checkCameraPermission()) {
                    requestCameraPermission();
                } else {
                    dispatchTakePictureIntent();

                    textViewExtractedText.setText("");
                }

            }
        });*/

        /*buttonExtract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(ProgressBar.VISIBLE);
                ExtractThread extractThread = new ExtractThread();
                new Thread(extractThread).start();
                //detectTextFromImage();
            }
        });*/

        /*buttonGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkStoragePermission()){
                    requestStoragePermission();
                }else{
                    pickGallery();
                }
                //displayImage();
            }
        });*/
    }

    private void animateFab(){
        if (isOpen){
            fab.startAnimation(rotateBackward);
            fabCamera.startAnimation(fabClose);
            //textViewCamera.setVisibility(View.GONE);
            textViewCamera.setAnimation(fabClose);
            textViewGallery.setAnimation(fabClose);
            textViewExtract.setAnimation(fabClose);
            fabGallery.startAnimation(fabClose);
            fabExtract.startAnimation(fabClose);
            fabCamera.setClickable(false);
            fabGallery.setClickable(false);
            fabExtract.setClickable(false);
            isOpen=false;
        }else{
            fab.startAnimation(rotateForward);
            fabCamera.startAnimation(fabOpen);
            //textViewCamera.setVisibility(View.VISIBLE);
            fabGallery.startAnimation(fabOpen);
            fabExtract.startAnimation(fabOpen);
            textViewCamera.setAnimation(fabOpen);
            textViewGallery.setAnimation(fabOpen);
            textViewExtract.setAnimation(fabOpen);
            fabCamera.setClickable(true);
            fabGallery.setClickable(true);
            fabExtract.setClickable(true);
            isOpen=true;
        }
    }

    private void extractText() {
        /*String addressRegex = "(Address|address|ADDRESS):?";
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

    private void jsonParse() {

        String url = "https://api.myjson.com/bins/n66m2";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("users");
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject users = jsonArray.getJSONObject(i);

                        String first = users.getString("name");
                        textViewExtractedText.append(first);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

            }
        });

        mQueue.add(request);

        /*Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
        StanfordCoreNLPClient pipeline = new StanfordCoreNLPClient(props, "http://localhost", 9000, 2);
// read some text in the text variable
        String text = "Tan Hao Yang"; // Add your text here!
// create an empty Annotation just with the given text
        Annotation document = new Annotation(text);
// run all Annotators on this text
        pipeline.annotate((Iterable<edu.stanford.nlp.pipeline.Annotation>) document);*/

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
                Log.d("FACE", "x2 : "+x2);
                y2 = y1 + thisFace.getHeight();
                Log.d("FACE", "y2 : "+y2);
                //tempCanvas.drawRoundRect(new RectF(x1, y1, x2, y2), 2, 2, myRectPaint);

            }
            //tempCanvas.drawBitmap();

            //imageViewResume.setImageDrawable(new BitmapDrawable(getResources(),tempBitmap));

            //Canvas canvas = new Canvas (tempBitmap);
            //imageViewResume.draw(canvas);
            croppedBitmap = Bitmap.createBitmap(tempBitmap,(int)x1,(int)y1,(int)x2-(int)x1,(int)y2-(int)y1);

            //imageViewResume.setImageBitmap(croppedBitmap);

            intent1.putExtra("EXTRACTED_FACE",croppedBitmap);

            //Rect src = new Rect((int) x1, (int) y1, (int) x2, (int) y2);
            //Rect dst = new Rect(0, 0, 200, 200);
            //tempCanvas.drawBitmap(imageBitmap, src, dst, null);
        }else{
            Toast.makeText(this, "No face detected", Toast.LENGTH_SHORT).show();
            Log.d("FACE", "No face detected");
            Bitmap noFaceBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_person);
            intent1.putExtra("EXTRACTED_FACE",noFaceBitmap);

        }

        /*final FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(imageBitmap);
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
        });*/

        TextRecognizer recognizer = new TextRecognizer.Builder(getApplicationContext()).build();

        if (!recognizer.isOperational()) {
            Toast.makeText(MainActivity.this, "Error :", Toast.LENGTH_SHORT).show();
        } else {
            Frame frame = new Frame.Builder().setBitmap(imageBitmap).build();
            SparseArray<TextBlock> items = recognizer.detect(frame);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < items.size(); i++) {
                TextBlock myItems = items.valueAt(i);
                sb.append(myItems.getValue());
                sb.append("  ");
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

    // Post Request For JSONObject
    public void postData(final String extractedText) {
        extractedName="";
        extractedLocation="";
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JSONObject object = new JSONObject();

        try {
            object.put("parameters",extractedText);
            //object.getString("parameters");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = "http://192.168.0.187:9000/?properties=%7B%22annotators%22%3A%22tokenize%2Cssplit%2Cner%22%7D";
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
                                    if(namedEntity.equals("PERSON") && extractedName.isEmpty()){
                                        extractedName = namedEntityResult;
                                        Log.d("NAMEFOUND : ", namedEntityResult);
                                    }else if((namedEntity.equals("LOCATION") || namedEntity.equals("CITY") ||namedEntity.equals("COUNTRY"))&&extractedLocation.isEmpty()){
                                        extractedLocation = namedEntityResult;
                                        Log.d("LOCATIONFOUND", namedEntityResult);
                                    }else if (namedEntity.equals("ORGANIZATION")){
                                        extractedOrganization = namedEntityResult;
                                    }
                                    //textViewExtractedText.append(namedEntity+" : "+namedEntityResult+"\n");
                                    Log.d("NER", namedEntity+" : "+namedEntityResult+"\n");
                                }
                            }

                            String str = extractedText;

                            Pattern phonePattern = Pattern.compile("((60+))?\\d{2,3}-? ?\\d{3,4} ?\\d{4,5}");
                            Pattern emailPattern = Pattern.compile("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])");
                            Matcher phoneMatcher = phonePattern.matcher(str);
                            if (phoneMatcher.find()) {
                                phoneNumber = phoneMatcher.group(0);
                                //textViewExtractedText.append("Phone Number: "+phoneNumber+"\n");

                            }

                            Matcher emailMatcher = emailPattern.matcher(str);
                            if (emailMatcher.find()) {
                                email = emailMatcher.group(0);
                                //textViewExtractedText.append("Email: "+email+"\n");
                            }

                            String addressRegex = "(Address|address|ADDRESS):?";
                            String addressFormat = "(NO|no|No|LOT|Lot|lot)?.?:?";
                            String location = extractedLocation;
                            Pattern addressPattern = Pattern.compile(addressFormat+"((\\d{1,2})?-?(\\d{1,2}[A-Z]?)?\\d{1,4},.+?)"+(location),Pattern.DOTALL);
                            Matcher addressMatcher = addressPattern.matcher(str);
                            if (addressMatcher.find()) {
                                address = addressMatcher.group(0);
                                Log.d("Address: ",address);
                                /*if(address.length()>100){
                                    address="";
                                }*/
                                //textViewExtractedText.append("Address :"+address+"\n");
                            }
                            //textViewExtractedText.setText(matcher.group(2));
                            //Bundle bundle = new Bundle();
                            //bundle.putString("BundleText",sb.toString());
                            intent1.putExtra("EXTRACTED_PHONE",phoneNumber);
                            intent1.putExtra("EXTRACTED_EMAIL",email);
                            intent1.putExtra("EXTRACTED_NAME",extractedName);
                            intent1.putExtra("EXTRACTED_ADDRESS",address);
                            progressBar.setVisibility(ProgressBar.INVISIBLE);
                            startActivity(intent1);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(ProgressBar.INVISIBLE);
                Toast.makeText(getApplicationContext(),"Error getting response",Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.add(jsonObjectRequest);

    }

    private void Submit(String data)
    {
        final String savedata= data;
        String URL="http://192.168.0.187:9000/?properties%3D%7B%22annotators%22%3A%22tokenize%2Cssplit%2Cner%22%2C%22outputFormat%22%3A%22json%22%7D";

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject objres=new JSONObject(response);
                    //Toast.makeText(getApplicationContext(),"Sucess"+objres.toString(),Toast.LENGTH_LONG).show();
                    Toast.makeText(getApplicationContext(),"Posted",Toast.LENGTH_LONG).show();
                    textViewExtractedText.setText(objres.toString());



                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(),"Server Error",Toast.LENGTH_LONG).show();

                }
                //Log.i("VOLLEY", response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getApplicationContext(), "Errorrr: "+error.getMessage(), Toast.LENGTH_SHORT).show();

                //Log.v("VOLLEY", error.toString());
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("sentences","Tan Hao Yang is studying at Tunku Abdul Rahman University College.");
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/json");
                return params;
            }
        };
        requestQueue.add(stringRequest);
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
                Uri resultUri = result.getUri();//get image uri
                imageViewResume.setImageURI(resultUri);
                imageBitmapDrawable = (BitmapDrawable) imageViewResume.getDrawable();
                imageBitmap = imageBitmapDrawable.getBitmap();
                //code here
            }
        } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
            Toast.makeText(MainActivity.this, "Error :", Toast.LENGTH_SHORT).show();
        }

    }




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
            fab.setClickable(true);

        }
    }


}

