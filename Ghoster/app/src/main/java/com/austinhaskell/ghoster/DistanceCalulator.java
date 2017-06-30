package com.austinhaskell.ghoster;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Austin on 5/4/2017.
 *
 * This class is in charge of finding the distance between points
 *   as well as figuring out where to store a file in the database
 *
 */

public class DistanceCalulator
{
    // NOTE: Both the ints below represent meters
    public static final int r = 6371;
    public static final double BOX_WIDTH = 0.2692;

    /**
     * Implementation of the Haversine Formula
     *  Calculates the distance between two points on earth
     *
     * @param lat latitude1
     * @param log longitude1
     * @param lat2 latitude2
     * @param log2 longitude2
     * @return distance between two points on earth
     */
    public static double between(double lat, double log, double lat2, double log2)
    {

        double retVal = 0;

        // Convert them all to Radians
        lat  = Math.toRadians(lat);
        log  = Math.toRadians(log);
        lat2 = Math.toRadians(lat2);
        log2 = Math.toRadians(log2);

        double deltaLat = lat - lat2;
        double deltaLong = log - log2;

        double a =      Math.sin(deltaLat/2) * Math.sin(deltaLat/2) +
                        Math.cos(lat) * Math.cos(lat2) *
                        Math.sin(deltaLong/2) * Math.sin(deltaLong/2);

        double c =      2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        retVal = r * c;

        // No negative distances
        return Math.abs(retVal);
    }

    /**
     *Function to deturmine which directories to load from firebase
     *
     * NOTE: Latitude is array index 0, longitude is array index 1
     *
     *
     *
     * <br/>Example: 43.125721, -87.158202
     * <br/> Returns:
     * <br/> 43.1, -87.1
     * <br/> 43.2, -87.1
     * <br/> 43.1, -87.2
     * <br/> 43.2, -87.2
     *
     * @param lat Current Locations latitude
     * @param log Current Locations longitude
     * @return Four Buckets of data to load
     */
    public static ArrayList<String[]> calcBuckets(double lat, double log)
    {
        ArrayList<String[]> retVal = new ArrayList<String[]>();

        // Calc the nearest
        String[] one =   {shift(lat, 0), shift(log, 0)};
        String[] two =   {shift(lat, 0), shift(log, 1)};
        String[] three = {shift(lat, 1), shift(log, 0)};
        String[] four =  {shift(lat, 1), shift(log, 1)};

        // Add to the list
        retVal.add(one);
        retVal.add(two);
        retVal.add(three);
        retVal.add(four);


        return retVal;
    }

    /**
     * Expanded search that will search .6 km away
     * Intended to be used if there are no available images to display nearby
     *
     * <br/>TODO: Make this scale to any size
     *
     * @param lat Current Latitude
     * @param log Current Longitude
     * @return Nearby 8 boxes and current one
     */
    public static ArrayList<String[]> expandedSearch(double lat, double log)
    {
        ArrayList<String[]> retVal = new ArrayList<>();

        // Hella nesting
        // TODO: Flatten this?
        for (double y = -0.002; y <= 0.002; y+=0.002)
        {
            for (double x = -0.002; x <= 0.002; x+= 0.002)
            {
                ArrayList<String[]> tmp = calcBuckets(lat + y, log + x);

                for (String[] n : tmp)
                {
                    retVal.add(n);
                }
            }
        }

        return retVal;
    }

    /**
     * This fuction takes a latitude and longitude and returns the value
     *   Of the storage folder to place the user post, Currently shifts to
     *   the location of the X, Should become more dynamic in the future
     *
     *   TODO: Shift to the nearest corner instead of the upper left
     *            <br/>
     *    ______  <br/>
     *   |X     | <br/>
     *   |      | <br/>
     *   |______| <br/>
     *
     * @param lat Current Latitude
     * @param log Current Longitude
     * @return Sotrage Location, first index is the
     */
    public static ArrayList<String[]> calcBucketToStore(double lat, double log)
    {
        return calcBuckets(lat, log);
    }


    /**
     * Wrapper for numberShift, returns the result as a String rather than a Double
     *
     * @param d number to round
     * @param amount amount to round up or down by (Negatives round down)
     * @return The adjusted number as a string
     */
    public static String shift(double d, int amount)
    {
        return Double.toString(numberShift(d, amount)).replaceAll("\\.", "_");
    }

    /**
     * Function that takes a decimal number and snaps it to the nearest hundreth
     *
     * <br/> Example: 45.7284,  1 - Returns 45.73
     * <br/> Example: 89.3840,  0 - Returns 89.38
     * <br/> Example: 23.1837, -1 - Returns 23.17
     *
     * @param d number to round
     * @param amount amount to round up or down by (Negatives round down)
     * @return The adjusted number as a string
     */
    public static Double numberShift(double d, int amount)
    {
        int PLACE_VALUE = 1000;

        double retVal = d;

        // The part of the decimal to the left of the "."
        int wholeValue = (int) Math.floor(d);

        // Now we should have the value to the right of the "."
        retVal = d - (double)wholeValue;

        // Move that number into the tens column
        retVal = retVal * PLACE_VALUE;

        // Lob off the rest
        retVal = Math.floor(retVal);

        // Shift by the amount
        retVal += amount;

        // Put it back into the right side of the "."
        retVal = retVal / PLACE_VALUE;

        // Add back the whole numbers
        retVal += wholeValue;

        return retVal;
    }


}
