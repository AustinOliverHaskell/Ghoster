package com.austinhaskell.ghoster;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class AddGhostActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener
{



    private String selectedImagePath;

    private GoogleApiClient mGoogleApiClient = null;

    private Uri selectedImage;

    // The user should be logged in right now. So this
    // shouldnt need to be modified too much
    private FirebaseAuth authorization;
    private StorageReference storage;
    private FirebaseDatabase database;

    private Location location = null;

    private final int LOCATION_GRANTED = 2;
    private static final int SELECT_PICTURE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ghost);

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck == PackageManager.PERMISSION_GRANTED)
        {
            Log.d("Good", "GOOD PERMISSION");
        }
        else
        {
            Log.d("Bad", "BAD PERMISSION");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_GRANTED);
        }

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        // Add an event listener to out add image button
        findViewById(R.id.add_img_bttn).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // Create a generic intent
                Intent intent = new Intent();

                // set the type
                intent.setType("image/*");

                // set the action that we want preformed by the intent we are calling
                intent.setAction(Intent.ACTION_GET_CONTENT);

                // Start the activity
                startActivityForResult(Intent.createChooser(intent,
                        "Select Picture"), SELECT_PICTURE);
            }
        });

        findViewById(R.id.update_location).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (location != null)
                {
                    hideFields();

                    StringBuilder display = new StringBuilder();

                    display.append("Lat:");
                    display.append(location.getLatitude());
                    display.append(" Long:");
                    display.append(location.getLongitude());

                    Context context = getApplicationContext();
                    CharSequence text = display.toString();
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    //toast.show();

                    EditText fileName = (EditText) findViewById(R.id.tag_name_id);

                    // Get the references to the applicable fields
                    StorageReference ref = storage.child(fileName.getText().toString());

                    String[] tokens = authorization.getCurrentUser().getEmail().split("@");

                    String userPath = tokens[0];

                    DatabaseReference dref = database.getReference("images/"+userPath+"/url");
                    DatabaseReference latRef = database.getReference("images/"+userPath+"/lat");
                    DatabaseReference longRef = database.getReference("images/"+userPath+"/long");

                    // Add the image to the database
                    dref.setValue(fileName.getText().toString());
                    latRef.setValue(location.getLatitude());
                    longRef.setValue(location.getLongitude());

                    ref.putFile(selectedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
                    {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                        {
                            Intent intent = new Intent(AddGhostActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    });
                }
                else
                {
                    Context context = getApplicationContext();
                    CharSequence text = "It was null";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            }
        });

        // -- Initialize storage and Database --
        storage = FirebaseStorage.getInstance().getReference();
        database = FirebaseDatabase.getInstance();
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

            try
            {
                // Get the location of the image and then set the image view to the
                // image as a bmp
                Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                ImageView img = (ImageView) findViewById(R.id.preview_img_view);

                img.setImageBitmap(bmp);

                Button bttn = (Button) findViewById(R.id.add_img_bttn);
                bttn.setText("Change");

            }
            catch (IOException error)
            {
                error.printStackTrace();
            }
        }
    }


    // Documentation
    /* https://developers.google.com/android/reference/com/google/android/gms/common/api/GoogleApiClient.ConnectionCallbacks
    * After calling connect(), this method will be invoked asynchronously when
    * the connect request has successfully completed. After this callback, the
    * application can make requests on other methods provided by the client and
    * expect that no user intervention is required to call methods that use account
     * and scopes provided to the client constructor.
    * */
    @Override
    public void onConnected(@Nullable Bundle bundle)
    {
        LocationRequest local = new LocationRequest();

        local.setInterval(10000);
        local.setFastestInterval(5000);
        local.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        try
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, local, this);
        }
        catch (SecurityException error)
        {
            error.printStackTrace();
        }
    }


    /* Documentation
    * Called when the client is temporarily in a disconnected state. This can happen
    * if there is a problem with the remote service (e.g. a crash or resource problem
    * causes it to be killed by the system). When called, all requests have been canceled
    * and no outstanding listeners will be executed. GoogleApiClient will automatically
    * attempt to restore the connection. Applications should disable UI components that
    * require the service, and wait for a call to onConnected(Bundle) to re-enable them.
    * */
    @Override
    public void onConnectionSuspended(int i) {

    }



    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {
        Context context = getApplicationContext();
        CharSequence text = "Connection Failed";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
    }

    // Method to be called at the start of this activity
    // this is useful because we need to connect to the location
    // services api
    @Override
    public void onStart()
    {
        mGoogleApiClient.connect();
        super.onStart();
    }

    // Method that is called on the destruction of this activity
    // need to disconnect from the api that we connected to at the
    // start of this activity
    @Override
    public void onStop()
    {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onLocationChanged(Location location)
    {
        this.location = location;
    }

    @Override
    public void onRequestPermissionsResult (int requestCode, String[] permissions, int[] grantResults)
    {
        if (requestCode == LOCATION_GRANTED)
        {
            // Got permission to use the location
            finish();
            startActivity(getIntent());
        }
    }


    private void hideFields()
    {
        findViewById(R.id.add_img_bttn).setVisibility(View.INVISIBLE);
        findViewById(R.id.preview_img_view).setVisibility(View.INVISIBLE);
        findViewById(R.id.update_location).setVisibility(View.INVISIBLE);
        findViewById(R.id.duration_bttn).setVisibility(View.INVISIBLE);
        findViewById(R.id.upload_progress).setVisibility(View.VISIBLE);
    }

    private void showFields()
    {
        findViewById(R.id.add_img_bttn).setVisibility(View.VISIBLE);
        findViewById(R.id.preview_img_view).setVisibility(View.VISIBLE);
        findViewById(R.id.update_location).setVisibility(View.VISIBLE);
        findViewById(R.id.duration_bttn).setVisibility(View.VISIBLE);
        findViewById(R.id.upload_progress).setVisibility(View.INVISIBLE);
    }

}