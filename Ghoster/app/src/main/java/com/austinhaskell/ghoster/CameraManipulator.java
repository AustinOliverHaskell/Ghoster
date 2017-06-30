package com.austinhaskell.ghoster;

import android.app.Activity;
import android.content.Context;
import android.hardware.camera2.*;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.SurfaceView;


/**
 * Created by Austin Oliver Haskell on 5/1/2017.
 *
 * This class is in charge of handling the camera permissions as
 * well as using the surface view
 */

public class CameraManipulator
{

    // Camera2 API Manager Class
    private CameraManager manager;

    // Current Context
    private Context context;
    private Activity activity;


    /**
     * Constructor - Takes the current context for use with the
     *  Camera2 API
     *
     * @param context Current Application context
     */
    CameraManipulator(Context context)
    {
        this.context = context;
    }

    /**
     * Takes a photo with the camera2 API
     *
     *
     */
    public void takePhoto()
    {

    }


    /**
     * Attaches the camera to a surface view, checks to make sure that we have
     * done the necessary setup
     *
     * @param view to attach the camera to
     */
    public void attachToView(SurfaceView view)
    {

    }


    /**
     * Preforms the necessary startup routines for the camera2 API, additionally this
     * checks for permissions
     *
     */
    public void startCamera()
    {
        this.manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);

        try
        {
            // Iterate through all the connected cameras
            for (String cameraListItem : this.manager.getCameraIdList())
            {
                // Get the info on the camera that we are iterating on currently
                CameraCharacteristics characteristics = this.manager.getCameraCharacteristics(cameraListItem);

                // Figure out if the camera we are on is the front facing camera
                Integer front = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (front != null && front != CameraCharacteristics.LENS_FACING_FRONT)
                {
                    // Not a front facing camera
                    continue;
                }


                // Data object to store all of the output formats supported by camera
                StreamConfigurationMap map = characteristics.get(
                        CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                if (map == null) {
                    continue;
                }






            }
        }
        catch(CameraAccessException error)
        {

        }
        catch(Exception error)
        {
            error.printStackTrace();
        }
    }


    /**
     *  Performs the nessasary teardown routines for the camera2 API
     */
    public void stopCamera()
    {

    }


}
