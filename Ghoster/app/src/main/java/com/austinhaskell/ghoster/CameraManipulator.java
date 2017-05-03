package com.austinhaskell.ghoster;

import android.hardware.camera2.*;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;


/**
 * Created by Austin Oliver Haskell on 5/1/2017.
 *
 * This class is in charge of handling the camera permissions as
 * well as using the surface view
 *
 *
 */

public class CameraManipulator
{
    // ----- Private Data -----
    private CameraManager manager;

    private String[] cameraList;

    private boolean valid;
    // ------------------------


    // ----- Constructors -----
    public CameraManipulator(CameraManager manager)
    {
        this.manager = manager;
        this.valid = true;

        try
        {
            cameraList = manager.getCameraIdList();

           /* manager.openCamera(cameraList[0], new CameraDevice.StateCallback() {
                @Override
                public void onOpened(@NonNull CameraDevice camera) {

                }

                @Override
                public void onDisconnected(@NonNull CameraDevice camera) {

                }

                @Override
                public void onError(@NonNull CameraDevice camera, int error) {

                }
            }, );*/
        }
        catch (CameraAccessException error)
        {
            // End this constuctor, set the valid bit to false
            valid = false;
            return;
        }
        catch (SecurityException security)
        {
            // Didnt have the permission
            valid = false;
            return;
        }
        catch (Exception generic)
        {
            generic.printStackTrace();
        }


    }
    // ------------------------


    // ----- Getters and Setters -----
    public boolean isValid()
    {
        return this.valid;
    }
    // -------------------------------

}
