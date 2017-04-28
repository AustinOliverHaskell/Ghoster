package com.austinhaskell.ghoster;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    // ----- Firebase Storage -----
    private StorageReference storageRef;
    private FirebaseDatabase database;
    // ----------------------------


    // ----- Firebase Authentication -----
    private FirebaseAuth authorization;
    private FirebaseAuth.AuthStateListener authStateListener;
    // -----------------------------------


    // ----- User Data -----
    String userEmail;
    // ---------------------

    // ----- Initialization functions -----
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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


        // Fill in our firebase refrences
        storageRef = FirebaseStorage.getInstance().getReference();
        authorization = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        // Listener to check if the user logs out
        authStateListener = new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null)
                {
                    // The user is singed in
                }
                else
                {
                    // The user is signed out
                    // TODO: Launch the login activity
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    authorization.removeAuthStateListener(authStateListener);
                    startActivity(intent);
                }
            }
        };

        String[] tokens = authorization.getCurrentUser().getEmail().split("@");

        DatabaseReference ref = database.getReference("images/" + tokens[0]);

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI

                String result = "";

               for (DataSnapshot n : dataSnapshot.getChildren())
               {
                    result = n.getValue().toString();
               }

                //StorageReference imgRef = storageRef.child(result);

                StorageReference img = storageRef.child("image/"+ result + ".jpg");

                try {
                    final File localFile = File.createTempFile("image", "jpg");
                    img.getFile(localFile)
                            .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    // Successfully downloaded data to local file
                                    // ...

                                    Toast.makeText(getApplicationContext(), "SUCCESS!", Toast.LENGTH_SHORT).show();

                                    ImageView tile = (ImageView) findViewById(R.id.first_tile);

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle failed download
                            // ...

                            Toast.makeText(getApplicationContext(), "FAILURE!", Toast.LENGTH_SHORT).show();

                        }
                    });
                }
                catch (Exception error)
                {
                    Log.d("MAIN_ACITIVITY", "");
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

        if (ref != null)
        {
            ref.addListenerForSingleValueEvent(postListener);
        }

        userEmail = authorization.getCurrentUser().getEmail();

        userEmail = userEmail.split("@")[0];

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
}
