package io.github.bananapp.httone.networking;

import android.app.Application;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashSet;

import io.github.bananapp.httone.model.Contact;

public class ContactsManager {

    private final Application mApplication;

    private final ContentResolver mContentResolver;

    public ContactsManager(Application application) {

        mApplication = application;

        mContentResolver = mApplication.getContentResolver();
    }

    public Contact getContact(Cursor cursor, boolean loadDetails) {

        String name = getName(cursor);
        String photo = getPhoto(cursor);

        final Contact contact = new Contact(name, photo);

        if (loadDetails) {

            filldeatils(contact, getLookup(cursor), String.valueOf(getId(cursor)));
        }

        return contact;
    }

    public Cursor getContacts(String match) {

        ArrayList<String> selectionArgs = new ArrayList<String>();

        String[] projection = new String[]{Data._ID, Data.LOOKUP_KEY, Contacts.DISPLAY_NAME_PRIMARY,
                                           Contacts.PHOTO_URI, Email.DATA};

        String selection = Contacts.IN_VISIBLE_GROUP + " = ?";
        selectionArgs.add("1");

        if (!TextUtils.isEmpty(match)) {

            selection = selection + " AND " + Email.DATA + " LIKE ?";
            selectionArgs.add("%" + match + "%");
        }

        String[] selections = selectionArgs.toArray(new String[selectionArgs.size()]);

        String sortOrder = Contacts.DISPLAY_NAME_PRIMARY + " COLLATE LOCALIZED ASC";

        return mContentResolver.query(Email.CONTENT_URI, projection, selection, selections,
                                      sortOrder);
    }

    public String getFirstName(Cursor cursor) {

        return cursor.getString(cursor.getColumnIndex(StructuredName.GIVEN_NAME));
    }

    public long getId(Cursor contacts) {

        return Long.valueOf(contacts.getString(contacts.getColumnIndex(Contacts._ID)));
    }

    public String getLastName(Cursor cursor) {

        return cursor.getString(cursor.getColumnIndex(StructuredName.FAMILY_NAME));
    }

    public String getLookup(Cursor contacts) {

        return contacts.getString(contacts.getColumnIndex(Contacts.LOOKUP_KEY));
    }

    public String getName(Cursor contacts) {

        return contacts.getString(contacts.getColumnIndex(Contacts.DISPLAY_NAME_PRIMARY));
    }

    public String getPhoto(Cursor contacts) {

        final String photo = contacts.getString(contacts.getColumnIndex(Contacts.PHOTO_URI));

        if (photo == null) {

            return null;
        }

        return ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, getId(contacts))
                          .toString();
    }

    private void filldeatils(Contact contact, String lookup, String id) {

        String[] projection = new String[]{StructuredName.GIVEN_NAME, StructuredName.FAMILY_NAME};

        //String selection = Data.LOOKUP_KEY + " = ?";
        String selection = Data.RAW_CONTACT_ID + " = ?";

        String[] selectionArgs = new String[]{lookup};

        final Cursor cursor =
                mContentResolver.query(Data.CONTENT_URI, null, selection, selectionArgs,
                                       Data.MIMETYPE);

        if (cursor.moveToFirst()) {

            //            contact.setFirstName(getFirstName(cursor));
            //            contact.setLastName(getLastName(cursor));
        }

        cursor.close();
    }

    private HashSet<String> getEmails(String id) {

        HashSet<String> emails = new HashSet<String>();

        Cursor emailCursor =
                mContentResolver.query(Email.CONTENT_URI, null, Email.CONTACT_ID + " = ?",
                                       new String[]{id}, null);

        if (emailCursor.getCount() > 0) {

            while (emailCursor.moveToNext()) {

                emails.add(emailCursor.getString(emailCursor.getColumnIndex(Email.DATA)));
            }
        }

        emailCursor.close();

        return emails;
    }
}
