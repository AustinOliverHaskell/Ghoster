package com.austinhaskell.ghoster;

import android.provider.ContactsContract;
import android.util.Log;

import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * Created by Austin on 5/2/2017.
 *
 * Wrapper class for the firebase database
 *
 * Created to help wrap and abstract how the database works
 *  to make it feel as though its a simpler construct such
 *  as an ArrayList
 *
 *  Additionally this allows me to move all the error checking
 *   logic outside of the Activity Classes
 *
 *   TODO: Implement Callbacks
 */

public class DatabaseManager
{
    // ----- Private Data -----
    private static DatabaseManager instance = null;
    private FirebaseDatabase database;
    // ------------------------

    // ----- Constants/Arbitrary Values -----
    public final String IMAGE_PATH  = "images/";
    public final String USER_PATH   = "users/";
    public final String PUBLIC_PATH = "public/";
    // --------------------------------------


    // ----- Constructors -----
    private DatabaseManager()
    {
        database = FirebaseDatabase.getInstance();
    }
    // ------------------------


    // ----- Getters and Setters -----
    public static DatabaseManager getInstance()
    {
        if (instance == null)
        {
            instance = new DatabaseManager();
        }

        return instance;
    }
    // -------------------------------


    // ----- Push and Pull data from database -----
    public String[] getUserData()
    {
        // TODO: Implement query to get user database and make
        // TODO: user data section on Firebase

        return null;
    }

    public String addImageToUser(String username, String title, double lat, double log)
    {
        String[] location = DistanceCalulator.calcBucketToStore(lat, log).get(0);

        DatabaseReference addLocation = database.getReference(IMAGE_PATH + username + "/" + location[0] + "/" + location[1]);

        String key = addLocation.push().getKey();

        addLocation = database.getReference(IMAGE_PATH + username + "/" + location[0] + "/" + location[1]);

        addLocation.child("title").setValue(title);
        addLocation.child("Lat").setValue(Double.toString(lat));
        addLocation.child("Long").setValue(Double.toString(log));

        return key;
    }

    public String addImageToPublic(String username, String title, double lat, double log)
    {
        ArrayList<String[]> storageLocations = DistanceCalulator.calcBucketToStore(lat, log);

        String retVal = "";
        boolean firstKey = true;

        for(String[] location : storageLocations)
        {
            DatabaseReference addLocation = database.getReference(PUBLIC_PATH + "/" + location[0] + "/" + location[1]);
            String key = addLocation.push().getKey();

            if (firstKey)
            {
                retVal = key;
                firstKey = false;
            }

            addLocation = database.getReference(PUBLIC_PATH + "/" + location[0] + "/" + location[1] + "/" + key);

            addLocation.child("title").setValue(title);
            addLocation.child("Lat").setValue(Double.toString(lat));
            addLocation.child("Long").setValue(Double.toString(log));
            addLocation.child("Img").setValue(retVal);
            addLocation.child("Owner").setValue(username);

        }
        return retVal;
    }

    public String addImageToOther(String username, String value, String base)
    {
        DatabaseReference addLocation = database.getReference(base + username);

        String key = addLocation.push().getKey();

        addLocation = database.getReference(base + username +"/" + key);

        addLocation.setValue(value);

        return key;
    }


    public DatabaseReference getImageRefrence(String username)
    {
        DatabaseReference retVal = database.getReference(PUBLIC_PATH);

        return retVal;
    }

    /**
     * Returns the refrences to the corners of the geographical box
     *
     * @param lat - Current Latitude
     * @param log - Current Longitude
     * @return A list of the four nearby storagerefrences
     */
    public ArrayList<DatabaseReference> getPublicRefrences(double lat, double log)
    {
        ArrayList<DatabaseReference> retVal = new ArrayList<>();

        ArrayList<String[]> nearby = DistanceCalulator.calcBuckets(lat, log);


        for (String[] n : nearby)
        {
            try
            {
                DatabaseReference ref = database.getReference(PUBLIC_PATH + n[0] + "/" + n[1]);
                retVal.add(ref);
            }
            catch (DatabaseException error)
            {
                error.printStackTrace();
            }
        }
        Log.d("RETVAL OF ARRAY",retVal.toString());

        return retVal;
    }

    public static void addOneTimeListener(DatabaseReference ref, ValueEventListener listener)
    {
        if (ref != null)
        {
            ref.addValueEventListener(listener);
        }
    }

    public void requestUpdate(DatabaseObserver observer)
    {

    }


    // --------------------------------------------




}
