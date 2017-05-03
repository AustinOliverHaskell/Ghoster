package com.austinhaskell.ghoster;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

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
 */

public class DatabaseManager
{
    // ----- Private Data -----
    private static DatabaseManager instance = null;
    private FirebaseDatabase database;
    // ------------------------

    // ----- Constants/Arbitrary Values -----
    public final String IMAGE_PATH = "images/";
    public final String USER_PATH  = "users/";
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

    public String addImageToUser(String username, String value)
    {
        DatabaseReference addLocation = database.getReference(IMAGE_PATH + username);

        String key = addLocation.push().getKey();

        addLocation = database.getReference(IMAGE_PATH + username +"/" + key);

        addLocation.setValue(value);

        return key;
    }

    public DatabaseReference getImageRefrence(String username)
    {
        DatabaseReference retVal = database.getReference(IMAGE_PATH + username);

        return retVal;
    }

    public void addOneTimeListener(DatabaseReference ref, ValueEventListener listener)
    {
        if (ref != null)
        {
            ref.addListenerForSingleValueEvent(listener);
        }
    }


    // --------------------------------------------




}
