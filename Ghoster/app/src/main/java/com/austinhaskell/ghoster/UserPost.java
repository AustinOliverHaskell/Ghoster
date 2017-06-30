package com.austinhaskell.ghoster;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Austin on 5/4/2017.
 *
 * Class to encapsulate the data from firebase
 *  is a Parceble object so that it can be moved between
 *  activities
 *
 */

public class UserPost implements Parcelable
{

    // ----- Private Data -----
    private String title;
    private Double lat;
    private Double log;
    private Uri url;
    // ------------------------


    // ----- Constructors -----
    UserPost(String title, String lat, String log, Uri url)
    {
        this.title = title;
        this.lat = Double.parseDouble(lat);
        this.log = Double.parseDouble(log);
        this.url = url;
    }

    UserPost(String title, String lat, String log)
    {
        this.title = title;
        this.lat = Double.parseDouble(lat);
        this.log = Double.parseDouble(log);
        this.url = null;
    }

    /**
     * Private constructor that is used to reconstruct this object <br />
     *  after being sent via intent
     *
     * @param parcel incoming parcel from the CREATOR
     */
    private UserPost(Parcel parcel)
    {
        this.title = parcel.readString();
        this.lat   = parcel.readDouble();
        this.log   = parcel.readDouble();

        // Uri is already parceble so we need to construct it from that
        this.url   = parcel.readParcelable(Uri.class.getClassLoader());
    }
    // ------------------------


    // ----- Getters and Setters -----
    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public Double getLat()
    {
        return lat;
    }

    public void setLat(Double lat)
    {
        this.lat = lat;
    }

    public Double getLog()
    {
        return log;
    }

    public void setLog(Double log)
    {
        this.log = log;
    }

    public void setUrl(Uri url)
    {
        this.url = url;
    }

    public Uri getUrl()
    {
        return this.url;
    }
    // -------------------------------

    // ----- Overrides -----
    @Override
    public boolean equals(Object obj)
    {
        UserPost post = (UserPost) obj;

        return post.getUrl().equals(this.getUrl());
    }

    // - Parceble Interface -

    /**
     * Describes the contents as an integer
     * @return An integer describing the parceble in some way
     */
    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(this.title);
        dest.writeDouble(this.lat);
        dest.writeDouble(this.log);
        dest.writeParcelable(this.url, flags);
    }
    // ---------------------


    // ----- Parcelable Field -----
    public static final Parcelable.Creator<UserPost> CREATOR = new Parcelable.Creator<UserPost>() {

        // Calls our private constructor
        @Override
        public UserPost createFromParcel(Parcel parcel) {
            return new UserPost(parcel);
        }

        @Override
        public UserPost[] newArray(int size) {
            return new UserPost[size];
        }
    };
    // ----------------------------


}
