package com.austinhaskell.ghoster;

import android.Manifest;
import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.camera2.CameraManager;
import android.location.Location;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.google.android.cameraview.CameraView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import id.zelory.compressor.Compressor;
import id.zelory.compressor.FileUtil;

public class AddGhostActivity extends Activity implements LocationSubject
{

    // ----- Services -----
    private FirebaseAuth authorization;
    private StorageReference storage;
    // --------------------

    // ----- Image/Camera -----
    private Uri selectedImage;
    // ------------------------

    // ----- Flags/Constants -----
    private final int LOCATION_GRANTED = 2;
    private final int CAMERA_GRANTED = 3;
    private static final int SELECT_PICTURE = 1;
    // ----------------------------

    // ----- GPS -----
    private Location location = null;
    private LocationController controller;
    // ---------------

    // ----- Camera -----
    private CameraView camera;
    private byte[] imageData;
    private boolean photoTaken;
    private final int COMPRESSION_RATE = 70;
    // ------------------

    // ----- General UI -----
    private LinearLayout postPhotoTakenUI;
    private LinearLayout photoEditUI;
    private ImageView    takePhotoButton;
    private ImageView    flipCameraButton;
    private ImageView    deletePhotoButton;
    // ----------------------


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Check for GPS and Camera Permissions
        checkPermissions();

        setContentView(R.layout.activity_add_ghost);

        // Add listeners for the camera and the buttons to go along with it
        camera = (CameraView) findViewById(R.id.camera_view);
        postPhotoTakenUI = (LinearLayout) findViewById(R.id.bottom_buttons_add_ghost);
        photoEditUI      = (LinearLayout) findViewById(R.id.photo_taken_bttns);
        takePhotoButton  = (ImageView) findViewById(R.id.camera_take_photo);
        flipCameraButton = (ImageView) findViewById(R.id.change_camera_bttn);
        deletePhotoButton= (ImageView) findViewById(R.id.delete_taken_photo_bttn);
        photoTaken = false;
        imageData  = null;


        // GPS Code
        controller = new LocationController(getApplicationContext(), this);


        findViewById(R.id.update_location).setOnClickListener(makeUploadListener());

        flipCameraButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Animate the button to rotate before changing cameras
                flipCameraButton
                        .animate()
                        .rotation(360)
                        .setDuration(250)
                        .setInterpolator(new LinearInterpolator())
                        .setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                flipCameraButton.setRotation(0);
                                switchCamera();
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });




            }
        });


        takePhotoButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                pictureTakenUI();
                photoTaken = true;
                takePhoto();
            }
        });

        deletePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                cameraUI();
                photoTaken = false;
                camera.start();
            }
        });

        camera.addCallback(mCallback);

        // -- Initialize storage and Database --
        storage = FirebaseStorage.getInstance().getReference();
        authorization = FirebaseAuth.getInstance();
        // ------------------------

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        // This method is what is called after we call the gallery activity

        super.onActivityResult(requestCode, resultCode, data);

        // Make sure that the activity that just returned to us was
        // select picture and that the user actually selected something
        if (requestCode == SELECT_PICTURE
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null)
        {
            // Data that was returned from the intent
            // We know that its going to be a universal resource index
            Uri uri = data.getData();
            selectedImage = uri;

            // TODO: Finnish this file compression
           // File imgFile = FileUtil.from(this, data.getData();

            //imgFile = Compressor.getDefault(this).compressToBitmap(imgFile);

            // Get the location of the image and then set the image view to the
            // image as a bmp

            Matrix matrix = new Matrix();
            matrix.postRotate(90);


        }
    }




    // ----- Activity Lifecycle Methods -----
    @Override
    public void onStart()
    {
        controller.connect();
        super.onStart();
    }


    @Override
    public void onStop()
    {
        controller.disconnect();
        super.onStop();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        camera.start();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        camera.stop();
    }

    @Override
    public void onBackPressed()
    {
        if (photoTaken)
        {
            cameraUI();
            photoTaken = false;
            camera.start();
        }
        else
        {
            finish();
        }

    }

    // --------------------------------------


    // ----- Permission Functions -----
    @Override
    public void onRequestPermissionsResult (int requestCode, String[] permissions, int[] grantResults)
    {
        if (requestCode == LOCATION_GRANTED)
        {
            // Got permission to use the location
            finish();
            startActivity(getIntent());
        }
        else if (requestCode == CAMERA_GRANTED)
        {
            finish();
            startActivity(getIntent());
        }
    }

    private void checkPermissions()
    {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_GRANTED);
        }

        if (cameraPermission != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_GRANTED);
        }
    }
    // --------------------------------



    // ----- Upload Logic -----
    private View.OnClickListener makeUploadListener()
    {
        return new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (location != null)
                {
                    hideFields();

                    EditText fileName = (EditText) findViewById(R.id.tag_name_id);

                    String[] tokens = authorization.getCurrentUser().getEmail().split("@");
                    String userPath = tokens[0];

                    String filename = DatabaseManager.getInstance().addImageToPublic(
                            userPath,
                            fileName.getText().toString(),
                            location.getLatitude(),
                            location.getLongitude());

                    StorageReference ref = storage.child(filename);

                    ref.putBytes(createTemporaryImageFile()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
                    {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                        {
                            showFields();
                            Intent intent = new Intent(AddGhostActivity.this, MainActivity.class);
                            AddGhostActivity.this.finish();
                            startActivity(intent);
                        }
                    });
                }
                else
                {
                    Context context = getApplicationContext();
                    CharSequence text = "Internal Error - Unable to pin";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            }
        };
    }
    // ------------------------


    // ----- UI Manipulation -----
    private void hideFields()
    {
        findViewById(R.id.update_location).setVisibility(View.INVISIBLE);
        findViewById(R.id.duration_bttn).setVisibility(View.INVISIBLE);
        findViewById(R.id.tag_name_id).setVisibility(View.INVISIBLE);
        findViewById(R.id.upload_progress).setVisibility(View.VISIBLE);
    }

    private void showFields()
    {
        findViewById(R.id.update_location).setVisibility(View.VISIBLE);
        findViewById(R.id.duration_bttn).setVisibility(View.VISIBLE);
        findViewById(R.id.tag_name_id).setVisibility(View.VISIBLE);
        findViewById(R.id.upload_progress).setVisibility(View.INVISIBLE);
    }

    private void pictureTakenUI()
    {
        photoEditUI.setVisibility(View.VISIBLE);
        photoEditUI.setClickable(true);
        flipCameraButton.setVisibility(View.INVISIBLE);
        flipCameraButton.setClickable(false);
        takePhotoButton.setVisibility(View.INVISIBLE);
        takePhotoButton.setClickable(false);
        postPhotoTakenUI.setVisibility(View.VISIBLE);
        postPhotoTakenUI.setClickable(true);
    }

    private void cameraUI()
    {
        photoEditUI.setVisibility(View.INVISIBLE);
        photoEditUI.setClickable(false);
        flipCameraButton.setVisibility(View.VISIBLE);
        flipCameraButton.setClickable(true);
        takePhotoButton.setVisibility(View.VISIBLE);
        takePhotoButton.setClickable(true);
        postPhotoTakenUI.setVisibility(View.INVISIBLE);
        postPhotoTakenUI.setClickable(false);
    }

    // ---------------------------


    // ----- Location Functions -----
    @Override
    public void updateLocation(Location location)
    {
        this.location = location;
    }

    @Override
    public void locationStarted()
    {
        // Callback function for the location object
        // no content needed here
    }
    // -----------------------------


    // ----- Camera Functions -----
    public void switchCamera()
    {
        if (camera.getFacing() == CameraView.FACING_BACK)
        {
            camera.setFacing(CameraView.FACING_FRONT);
        }
        else if (camera.getFacing() == CameraView.FACING_FRONT)
        {
            camera.setFacing(CameraView.FACING_BACK);
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Cannot Switch Cameras", Toast.LENGTH_LONG).show();
        }
    }

    public void takePhoto()
    {
        camera.takePicture();
    }

    private CameraView.Callback mCallback
            = new CameraView.Callback() {

        @Override
        public void onCameraOpened(CameraView cameraView) {
        }

        @Override
        public void onCameraClosed(CameraView cameraView) {
        }

        @Override
        public void onPictureTaken(CameraView cameraView, final byte[] data)
        {
            Log.d("onPictureTaken", "onPictureTaken " + data.length);

            camera.stop();

            imageData = data;
        }
    };
    // ----------------------------


    // ----- Image Methods -----
    public byte[] createTemporaryImageFile()
    {
        byte[] retVal = null;

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();

        Bitmap bmp = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);

        if (camera.getFacing() == CameraView.FACING_FRONT)
        {
            bmp = rotateBitmap(bmp, 270);
        }
        else
        {
            bmp = rotateBitmap(bmp, 90);
        }

        bmp.compress(Bitmap.CompressFormat.JPEG, COMPRESSION_RATE, outStream);

        retVal = outStream.toByteArray();

        return retVal;
    }

    public static Bitmap rotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
    // -------------------------

}