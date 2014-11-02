package io.github.bananapp.httone.model;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;
import org.parceler.Parcel.Serialization;
import org.parceler.ParcelConstructor;

@Parcel(Serialization.METHOD)
public class Contact {

    @SerializedName("name")
    private String mName;

    @SerializedName("photo")
    private String mPhoto;

    @ParcelConstructor
    public Contact(final String name, final String photo) {

        mName = name;
        mPhoto = photo;
    }

    public String getName() {

        return mName;
    }

    public String getPhoto() {

        return mPhoto;
    }
}
