package com.austinhaskell.ghoster;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MainActivity extends AppCompatActivity {

    // ----- Firebase Storage -----
    private StorageReference storageRef;
    // ----------------------------


    // ----- Firebase Authentication -----
    private FirebaseAuth authorization;
    private FirebaseAuth.AuthStateListener authStateListener;
    // -----------------------------------


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
