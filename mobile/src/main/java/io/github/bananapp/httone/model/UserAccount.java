package io.github.bananapp.httone.model;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;
import org.parceler.Parcel.Serialization;
import org.parceler.ParcelConstructor;

@Parcel(Serialization.METHOD)
public class UserAccount {

    @SerializedName("email")
    private String mEmail;

    @SerializedName("registration_id")
    private String mRegistrationId;

    @ParcelConstructor
    public UserAccount(final String email, final String registrationId) {

        mEmail = email;
        mRegistrationId = registrationId;
    }

    public String getEmail() {

        return mEmail;
    }

    public String getRegistrationId() {

        return mRegistrationId;
    }
}
