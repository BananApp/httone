package io.github.bananapp.httone.model;

import org.parceler.Parcel;
import org.parceler.Parcel.Serialization;
import org.parceler.ParcelConstructor;

@Parcel(Serialization.METHOD)
public class UserInfo {

    private Place mCurrPlace;

    private Place mPrevPlace;

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
