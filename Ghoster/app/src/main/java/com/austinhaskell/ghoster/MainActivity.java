package com.austinhaskell.ghoster;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements  LocationSubject, DatabaseObserver
{

    // ----- Firebase Storage -----
    private StorageReference storageRef;
    // ----------------------------

    // ----- Firebase Authentication -----
    private FirebaseAuth authorization;
    private FirebaseAuth.AuthStateListener authStateListener;
    private String username = "";
    // -----------------------------------

    // ----- UI Stuffs -----
    private RecyclerView mainView;
    private final int columnCount = 3;
    private DrawerLayout navDrawer;
    // ---------------------

    // ----- Images -----
    ArrayList<UserPost> uriList;
    // ------------------

    // ----- GPS -----
    private Location location;
    private final int LOCATION_GRANTED = 2;
    private LocationController controller;
    private boolean loadLocation;
    private long lastLocationTimestamp;
    private final int UPDATE_INTERVAL = 180000;
    // ---------------

    // ----- Camera -----
    private final int CAMERA_GRANTED = 3;
    // ------------------

    // ----- Initialization functions -----
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermissions();

        controller = new LocationController(getApplicationContext(), this);

        // Initialize Firebase objects
        startFirebase();

        uriList = new ArrayList<UserPost>();

        // Initialize the grid view
        initGrid();

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navDrawer = (DrawerLayout) findViewById(R.id.navigation_drawer);

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

        FloatingActionButton menu = (FloatingActionButton) findViewById(R.id.menu_fab);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navDrawer.openDrawer(Gravity.START);
            }
        });

        SwipeRefreshLayout refreshSwipe = (SwipeRefreshLayout) findViewById(R.id.refresh_swipe);
        refreshSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                locationStarted();
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

            loadLocation = true;
            lastLocationTimestamp = 0;
            username = authorization.getCurrentUser().getEmail();

            navDrawer.addDrawerListener(new DrawerLayout.DrawerListener() {
                @Override
                public void onDrawerSlide(View drawerView, float slideOffset) {

                }

                @Override
                public void onDrawerOpened(View drawerView) {
                    ((TextView)findViewById(R.id.username_text_drawer)).setText(username);
                    /*((ImageView)findViewById(R.id.profile_img)).
                            setImageBitmap(
                                    Bitmap.createScaledBitmap(
                                            BitmapFactory.decodeResource(getResources(), R.drawable.placeholder_image_1),
                                            136, 136, false
                                    ));*/
                }

                @Override
                public void onDrawerClosed(View drawerView) {

                }

                @Override
                public void onDrawerStateChanged(int newState) {

                }
            });
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
        else if (id == R.id.logout)
        {
            authorization.signOut();
        }

        return super.onOptionsItemSelected(item);
    }
    // ----------------------------------


    // ----- Activity Lifecycle Methods -----
    @Override
    public void onStart()
    {
        controller.connect();
        super.onStart();
        authorization.addAuthStateListener(authStateListener);
    }

    @Override
    public void onStop()
    {
        super.onStop();
        controller.disconnect();

        // If it still is listening
        if (authStateListener != null)
        {
            authorization.removeAuthStateListener(authStateListener);
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        controller.disconnect();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        controller.connect();
    }

    @Override
    public void onRestart()
    {
        super.onRestart();

        if (location == null) {
            // Reset this value so that the next location update also update the view
            lastLocationTimestamp = 0;
        }
    }


    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    // -----------------------------------


    // ----- Error Checking -----
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
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    authorization.removeAuthStateListener(authStateListener);
                    MainActivity.this.finish();
                    startActivity(intent);
                }
            }
        };
    }

    // TODO: REFACTOR THIS
    private ValueEventListener generateValueListener()
    {
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI

                // Paths collection
                for(DataSnapshot n : dataSnapshot.getChildren())
                {

                    final UserPost userPost = new UserPost("Test", "12", "12.6");

                    // TODO: Get titles (Values)
                    StorageReference img = storageRef.child(n.getKey());

                    img.getDownloadUrl()
                            .addOnSuccessListener(new OnSuccessListener<Uri>()
                            {
                                @Override
                                public void onSuccess(final Uri uri) {
                                    // Successfully downloaded data to local file
                                    // ...

                                    userPost.setUrl(uri);

                                    if (!uriList.contains(userPost))
                                    {
                                        Log.d("OnSuccess!", userPost.toString());
                                        uriList.add(userPost);
                                        mainView.getAdapter().notifyDataSetChanged();
                                    }

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


    // ----- Other -----
    @Override
    public void onBackPressed()
    {
        // Overridden to prevent user from hitting the back button
        // on the main menu unless the nav drawer is open
        if (navDrawer.isDrawerOpen(Gravity.START))
        {
            navDrawer.closeDrawer(Gravity.START);
        }
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



    // ----- Permission Checks -----
    private void checkPermissions()
    {
        // TODO: ADD CAMERA PERMISSIONS
        int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_GRANTED);
        }
    }

    private void checkCameraPermissions()
    {
        int cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

        if (cameraPermission != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_GRANTED);
        }
    }


    @Override
    public void onRequestPermissionsResult (int requestCode, String[] permissions, int[] grantResults)
    {
        // TODO: ADD CAMERA PERMISSIONS
        if (requestCode == LOCATION_GRANTED)
        {
            // Got permission to use the location
            finish();
            startActivity(getIntent());
        }
    }

    // -----------------------------


    // ----- Location Interface Methods -----
    @Override
    public void updateLocation(Location location)
    {
        Location oldLocation = this.location;
        this.location = location;
        // If we havent updated the list in a long time (This is incase a user is sitting in the same area for awhile)
        if (System.currentTimeMillis() - lastLocationTimestamp > UPDATE_INTERVAL)
        {
            // TODO: Make this not clear the entire list
            updateList();
            lastLocationTimestamp = System.currentTimeMillis();
        }
        else if (oldLocation != null)
        {
            if (DistanceCalulator.between(oldLocation.getLatitude(), oldLocation.getLongitude(), this.location.getLatitude(), this.location.getLongitude()) > DistanceCalulator.BOX_WIDTH) {
                // Update
                Toast.makeText(getApplicationContext(), "Moved to a new Box", Toast.LENGTH_LONG).show();
                this.location = location;
                updateList();
            }
        }

    }

    @Override
    public void locationStarted()
    {
        updateList();
    }
    // --------------------------------------


    // ----- Recycler View Methods -----
    public void updateList()
    {
        // TODO: Needs to only update the list when new items are inserted, I should have to call clear here
        //uriList.clear();

        ValueEventListener postListener = generateValueListener();

        for (DatabaseReference n : DatabaseManager.getInstance().getPublicRefrences(location.getLatitude(), location.getLongitude()))
        {
            DatabaseManager.addOneTimeListener(
                    n,
                    postListener);
        }

        SwipeRefreshLayout temp = (SwipeRefreshLayout) findViewById(R.id.refresh_swipe);
        temp.setRefreshing(false);
        //mainView.getAdapter().notifyDataSetChanged();
    }
    // ---------------------------------


    // ----- Database Interface Methods -----
    @Override
    public void onRecieveDatabaseUpdate(DataSnapshot dataSnapshot)
    {
        // Paths collection
        for(DataSnapshot n : dataSnapshot.getChildren())
        {
            String title = (String) n.child("title").getValue();

            final UserPost userPost = new UserPost(title, "12", "12.6");

            // TODO: Get titles (Values)
            StorageReference img = storageRef.child(n.getKey());

            img.getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>()
                    {
                        @Override
                        public void onSuccess(final Uri uri) {
                            // Successfully downloaded data to local file
                            // ...

                            userPost.setUrl(uri);

                            if (!uriList.contains(userPost))
                            {
                                Log.d("OnSuccess!", userPost.toString());
                                uriList.add(userPost);
                                mainView.getAdapter().notifyDataSetChanged();
                            }

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
    }
    // --------------------------------------

    // TODO: Implement a "Smart Clear" that only clears posts that are too far away


}
