package com.austinhaskell.ghoster;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity
{

    // ----- Firebase Storage -----
    private StorageReference storageRef;
    // ----------------------------


    // ----- Firebase Authentication -----
    private FirebaseAuth authorization;
    private FirebaseAuth.AuthStateListener authStateListener;
    // -----------------------------------


    // ----- UI Stuffs -----
    private RecyclerView mainView;
    private int columnCount = 3;
    // ---------------------

    // ----- Images -----
    ArrayList<Uri> uriList;
    // ------------------

    // ----- Initialization functions -----
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase objects
        startFirebase();

        // Initialize the grid view
        initGrid();

        uriList = new ArrayList<Uri>();

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Floating + button in the lower right corner
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launch the create ghost activity
                Intent intent = new Intent(MainActivity.this, AddGhostActivity.class);
                startActivity(intent);
            }
        });

        if (authorization.getCurrentUser() == null)
        {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            authorization.removeAuthStateListener(authStateListener);
            startActivity(intent);
        }
        else
        {

            // TODO: Actually abstract some of this into the DatabaseManager class
            // rather than just move it down below

            ValueEventListener postListener = generateValueListener();

            DatabaseManager.getInstance().addOneTimeListener(
                    DatabaseManager.getInstance().getImageRefrence(getUserEmail()),
                    postListener);

        }

    }
    // ------------------------------------


    // ----- Options menu functions -----
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            // Change Activities to the settings

            // NOTE: This is a note for my own learning. Data is passed between activities
            // through something called an Intent. The metaphor that google uses is an envelope
            // that you are sending to the next activity.
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);

            startActivity(intent);

            return true;
        }
        else if (id == R.id.action_refresh)
        {
            Toast.makeText(MainActivity.this, "TODO - Add refresh", Toast.LENGTH_LONG).show();
            return true;
        }
        else if (id == R.id.logout)
        {
            authorization.signOut();
        }

        return super.onOptionsItemSelected(item);
    }
    // ----------------------------------


    // ----- Activity start and stop -----
    @Override
    public void onStart()
    {
        super.onStart();
        authorization.addAuthStateListener(authStateListener);
    }

    @Override
    public void onStop()
    {
        super.onStop();
        // If it still is listening
        if (authStateListener != null)
        {
            authorization.removeAuthStateListener(authStateListener);
        }
    }
    // -----------------------------------


    // ----- Error Checking -----
    private String getUserEmail()
    {
        String retVal;

        try
        {
            retVal = authorization.getCurrentUser().getEmail();

            retVal = retVal.split("@")[0];
        }
        catch (NullPointerException error)
        {
            retVal = "";
        }

        return retVal;
    }
    // --------------------------


    // ----- Listener Generation Code -----
    private FirebaseAuth.AuthStateListener generateAuthListener()
    {
        return new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // The user is singed in
                } else {
                    // The user is signed out
                    // TODO: Launch the login activity
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    authorization.removeAuthStateListener(authStateListener);
                    MainActivity.this.finish();
                    startActivity(intent);
                }
            }
        };
    }

    private ValueEventListener generateValueListener()
    {
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI

                // Paths collection
                final ArrayList <Uri> paths = new ArrayList<Uri>();

                for(DataSnapshot n : dataSnapshot.getChildren())
                {
                    // TODO: Get titles (Values)
                    StorageReference img = storageRef.child(n.getKey().toString());

                    img.getDownloadUrl()
                            .addOnSuccessListener(new OnSuccessListener<Uri>()
                            {
                                @Override
                                public void onSuccess(final Uri uri) {
                                    // Successfully downloaded data to local file
                                    // ...
                                    paths.add(uri);
                                    uriList = paths;
                                }
                            }).addOnFailureListener(new OnFailureListener()
                    {
                        @Override
                        public void onFailure(@NonNull Exception exception)
                        {
                            // TODO: Handle failed download
                        }
                    });
                }

                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("MAIN_ACTIVITY", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
    }
    // ------------------------------------

    // ----- Firebase Initilization -----
    public void startFirebase()
    {
        // Fill in our firebase refrences
        storageRef = FirebaseStorage.getInstance().getReference();
        authorization = FirebaseAuth.getInstance();

        // Listener to check if the user logs out
        authStateListener = generateAuthListener();
    }
    // ----------------------------------

    // ----- Seting images -----
    private void getImages(String path)
    {
        StorageReference img = storageRef.child(path);

        URL retVal = null;

        try
        {
           img.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>()
                {
                    @Override
                    public void onSuccess(final Uri uri) {
                        // Successfully downloaded data to local file
                        // ...



                    }
                }).addOnFailureListener(new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception exception)
                    {
                        // TODO: Handle failed download
                    }
                });
        }
        catch (Exception error)
        {
            error.printStackTrace();
        }
    }

    // -------------------------

    // ----- Other -----
    @Override
    public void onBackPressed()
    {
        // Overridden to prevent user from hitting the back button
        // on the main menu
    }
    // -----------------


    // ----- Grid RecyclerView -----
    private void initGrid()
    {
        mainView = (RecyclerView) findViewById(R.id.grid_view);
        mainView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(),columnCount);
        mainView.setLayoutManager(layoutManager);

        // Associate the adapter with the recycler view
        Tile adapter = new Tile(getApplicationContext(),uriList);
        mainView.setAdapter(adapter);

    }
    // -----------------------------
}
