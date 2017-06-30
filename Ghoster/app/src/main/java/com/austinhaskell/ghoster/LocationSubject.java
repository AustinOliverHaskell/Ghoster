package com.austinhaskell.ghoster;

import android.location.Location;

/**
 * Created by Austin on 5/8/2017.
 *
 * Interface for updating an object with a location
 */

public interface LocationSubject
{
    void updateLocation(Location location);
    void locationStarted();
}
