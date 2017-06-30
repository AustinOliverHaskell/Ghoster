package com.austinhaskell.ghoster;

import com.google.firebase.database.DataSnapshot;

/**
 * Created by Austin on 5/28/2017.
 *
 * Interface to recive database updates from the DatabaseManagerClass
 */

public interface DatabaseObserver
{
    /**
     * Function that is called on the DatabaseSubject
     *  whenever an update to the data is returned
     *  gives the result of the query in the form
     *  of a DataSnapshot
     */
    public void onRecieveDatabaseUpdate(DataSnapshot dataSnapshot);

}
