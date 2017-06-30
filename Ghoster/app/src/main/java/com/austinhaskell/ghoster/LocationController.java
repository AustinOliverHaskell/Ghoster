package com.austinhaskell.ghoster;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by Austin on 5/8/2017.
 *
 * Wrapper class for the GPS API
 *
 * This allows for easy reuse of error checking and initialization code
 *
 */

public class LocationController implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener
{
    // ----- GPS -----
    private GoogleApiClient mGoogleApiClient = null;
    private Location location;
    private final int LOCATION_GRANTED = 2;
    // ---------------

    // ----- Private Data -----
    private LocationSubject subject;
    private Context context;
    private boolean started;
    // ------------------------

    // ----- Constructors -----
    LocationController(Context context, LocationSubject subject)
    {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        this.subject = subject;
        this.context = context;
        this.started = false;
    }
    // ------------------------

    @Override
    public void onConnected(@Nullable Bundle bundle)
    {
        LocationRequest local = new LocationRequest();

        local.setInterval(10000);
        local.setFastestInterval(5000);
        local.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        Log.d("-----", "In onConnected");
        try
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, local, this);
            started = true;
        }
        catch (SecurityException error)
        {
            error.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i)
    {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {
        Toast.makeText(context, "CONNECTION FAILED", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLocationChanged(Location location)
    {
        // Set this objects current location to the updated location
        this.location = location;

        // Update the subjects location
        subject.updateLocation(this.location);

        // Let the subject know that the location services have been started
        if (started)
        {
            subject.locationStarted();
            started = false;
        }
    }

    public void connect()
    {
        mGoogleApiClient.connect();
    }

    public void disconnect()
    {
        mGoogleApiClient.disconnect();
    }
}
