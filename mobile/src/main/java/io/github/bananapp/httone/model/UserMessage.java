package io.github.bananapp.httone.model;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;
import org.parceler.Parcel.Serialization;
import org.parceler.ParcelConstructor;

@Parcel(Serialization.METHOD)
public class UserMessage {

    @SerializedName("message")
    private String mMessage;

    @SerializedName("sender")
    private String mSender;

    @ParcelConstructor
    public UserMessage(final String message, final String sender) {

        mMessage = message;
        mSender = sender;
    }

    public String getMessage() {

        return mMessage;
    }

    public String getSender() {

        return mSender;
    }
}
