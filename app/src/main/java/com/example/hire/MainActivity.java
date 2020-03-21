package com.example.hire;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

public class MainActivity extends AppCompatActivity {

    private Button buttonCamera, buttonExtract, buttonGallery,buttonPost;
    private RequestQueue mQueue;
    private ImageView imageViewResume;
    private TextView textViewExtractedText;
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
    String extractedTextFromImage="";
    String extractedName="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonCamera = findViewById(R.id.buttonCamera);
        buttonExtract = findViewById(R.id.buttonExtract);
        buttonGallery = findViewById(R.id.buttonGallery);
        buttonPost=findViewById(R.id.buttonPost);
        imageViewResume = findViewById(R.id.imageViewResume);
        textViewExtractedText = findViewById(R.id.textViewExtractedText);

        camaraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        mQueue = Volley.newRequestQueue(this);

        buttonPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //jsonParse();
                //Submit("Tan Hao Yang lives in Seetapak");
                //postData();
                extractText();
            }
        });

        buttonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!checkCameraPermission()) {
                    requestCameraPermission();
                } else {
                    dispatchTakePictureIntent();

                    textViewExtractedText.setText("");
                }

            }
        });

        buttonExtract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                detectTextFromImage();
            }
        });

        buttonGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkStoragePermission()){
                    requestStoragePermission();
                }else{
                    pickGallery();
                }
                //displayImage();
            }
        });
    }

    private void extractText() {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable=true;


        Paint myRectPaint = new Paint();
        myRectPaint.setStrokeWidth(5);
        myRectPaint.setColor(Color.RED);
        myRectPaint.setStyle(Paint.Style.STROKE);

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
                tempCanvas.drawRoundRect(new RectF(x1, y1, x2, y2), 2, 2, myRectPaint);

            }
            //tempCanvas.drawBitmap();

            //imageViewResume.setImageDrawable(new BitmapDrawable(getResources(),tempBitmap));

            //Canvas canvas = new Canvas (tempBitmap);
            //imageViewResume.draw(canvas);
            croppedBitmap = Bitmap.createBitmap(tempBitmap,(int)x1,(int)y1,(int)x2-(int)x1,(int)y2-(int)y1);

            imageViewResume.setImageBitmap(croppedBitmap);

            //Rect src = new Rect((int) x1, (int) y1, (int) x2, (int) y2);
            //Rect dst = new Rect(0, 0, 200, 200);
            //tempCanvas.drawBitmap(imageBitmap, src, dst, null);
        }else{
            Toast.makeText(this, "No face detected", Toast.LENGTH_SHORT).show();
            Log.d("FACE", "No face detected");
        }





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
                sb.append("\n");
            }
            /*Intent intent1 = new Intent(this,ExtractedText.class);
            Bundle bundle = new Bundle();
            bundle.putString("BundleText",sb.toString());
            intent1.putExtra("EXTRACTED_TEXT",bundle);
            startActivity(intent1);*/
            String extractedTextFromImage = sb.toString();
            textViewExtractedText.setText(extractedTextFromImage);
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

    // Post Request For JSONObject
    public void postData(final String extractedText) {
        extractedName="";
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JSONObject object = new JSONObject();
        extractedText.replaceAll("\n","");
        try {
            object.put("parameters",extractedText);
            object.getString("parameters");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = "http://192.168.0.187:9000/?properties%3D%7B%22annotators%22%3A%22tokenize%2Cssplit%2Cner%22%2C%22outputFormat%22%3A%22json%22%7D";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url,object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONArray ary = response.getJSONArray("sentences");
                            JSONObject obj1 = ary.getJSONObject(0);
                            JSONArray ary2 =  obj1.getJSONArray("entitymentions");
                            textViewExtractedText.setText("--- Result ---\n\n");
                            for(int i=0;i<ary2.length();i++){
                                JSONObject obj2 = ary2.getJSONObject(i);
                                String namedEntity = obj2.getString("ner");
                                String namedEntityResult = obj2.getString("text");
                                if(namedEntity.equals("PERSON") && extractedName.isEmpty()){
                                    extractedName = namedEntityResult;
                                }
                                textViewExtractedText.append(namedEntity+" : "+namedEntityResult+"\n");
                            }
                            String str = extractedText;

                            Pattern pattern = Pattern.compile("\\d{3}-?\\d{7,8}");
                            Pattern emailPattern = Pattern.compile("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])");
                            Matcher matcher = pattern.matcher(str);
                            if (matcher.find()) {
                                phoneNumber = matcher.group(0);
                                textViewExtractedText.append("Phone Number: "+phoneNumber+"\n");

                            }

                            Matcher emailMatcher = emailPattern.matcher(str);
                            if (emailMatcher.find()) {
                                email = emailMatcher.group(0);
                                textViewExtractedText.append("Email: "+email+"\n");
                            }

                            Intent intent1 = new Intent(getApplicationContext(),ExtractedText.class);
                            //Bundle bundle = new Bundle();
                            //bundle.putString("BundleText",sb.toString());
                            intent1.putExtra("EXTRACTED_PHONE",phoneNumber);
                            intent1.putExtra("EXTRACTED_EMAIL",email);
                            intent1.putExtra("EXTRACTED_NAME",extractedName);
                            startActivity(intent1);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
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

    private void displayImage() {
        Intent intent = new Intent(this, DisplayImage.class);
        intent.putExtra("image_path", currentImagePath);
        startActivity(intent);
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
            }
        } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
            Toast.makeText(MainActivity.this, "Error :", Toast.LENGTH_SHORT).show();
        }

    }


    private void detectTextFromImage() {

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
                sb.append("\n");
            }
            /*Intent intent1 = new Intent(this,ExtractedText.class);
            Bundle bundle = new Bundle();
            bundle.putString("BundleText",sb.toString());
            intent1.putExtra("EXTRACTED_TEXT",bundle);
            startActivity(intent1);*/
            extractedTextFromImage = sb.toString().replaceAll("\n"," \n ");;

            //textViewExtractedText.setText(extractedTextFromImage);
            postData(extractedTextFromImage);


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

}
