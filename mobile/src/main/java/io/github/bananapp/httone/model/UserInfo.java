package io.github.bananapp.httone.model;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;
import org.parceler.Parcel.Serialization;
import org.parceler.ParcelConstructor;

@Parcel(Serialization.METHOD)
public class UserInfo {

    @SerializedName("current_place")
    private Place mCurrPlace;

    @SerializedName("previous_place")
    private Place mPrevPlace;

    @SerializedName("user_id")
    private String mUserName;

    @ParcelConstructor
    public UserInfo(final Place currPlace, final Place prevPlace, final String userName) {

        mCurrPlace = currPlace;
        mPrevPlace = prevPlace;
        mUserName = userName;
    }

    public Place getCurrPlace() {

        return mCurrPlace;
    }

    public Place getPrevPlace() {

        return mPrevPlace;
    }

    public String getUserName() {

        return mUserName;
    }
}
