package io.github.bananapp.httone.gcm;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Contacts.Photo;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.gson.Gson;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import io.github.bananapp.httone.LocationActivity;
import io.github.bananapp.httone.R;
import io.github.bananapp.httone.UserMessageService;
import io.github.bananapp.httone.model.Contact;
import io.github.bananapp.httone.model.UserInfo;
import io.github.bananapp.httone.networking.ClientApi;
import io.github.bananapp.httone.networking.ContactsManager;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RestAdapter.LogLevel;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class GcmIntentService extends IntentService {

    public static final String ACTION_GCM_MESSAGE =
            "io.github.bananapp.httone.gcm.ACTION_GCM_MESSAGE";

    public static final String EXTRA_SENDER = "io.github.bananapp.httone.gcm.EXTRA_SENDER";

    public static final String EXTRA_USER_INFO = "io.github.bananapp.httone.gcm.EXTRA_USER_INFO";

    public static final String EXTRA_USER_MESSAGE =
            "io.github.bananapp.httone.gcm.EXTRA_USER_MESSAGE";

    public GcmIntentService() {

        super("gcm_service");
    }

    @Override
    protected void onHandleIntent(final Intent intent) {

        if (intent.hasExtra(EXTRA_USER_MESSAGE)) {

            final String sender = intent.getStringExtra(EXTRA_SENDER);
            final String message = intent.getStringExtra(EXTRA_USER_MESSAGE);

            final Intent viewIntent = new Intent(getApplicationContext(), LocationActivity.class);
            final PendingIntent viewPendingIntent =
                    PendingIntent.getActivity(getApplicationContext(), 0, viewIntent, 0);

            final Builder builder = new Builder(GcmIntentService.this);
            builder.setSmallIcon(R.drawable.smile)
                   .setContentTitle(sender)
                   .setContentText(message)
                   .setContentIntent(viewPendingIntent)
                   .setVibrate(new long[]{200, 200, 200});

//            final ContactsManager contactsManager = new ContactsManager(getApplication());
//            final Cursor cursor = contactsManager.getContacts(sender);
//            cursor.moveToFirst();
//            final Contact contact = contactsManager.getContact(cursor, false);
//
//            String photo = contact.getPhoto();
//
//            //            if (photo.endsWith("/photo")) {
//            //
//            //                photo = photo.replace("/photo", "");
//            //            }
//
//            //            final InputStream input =
//            //                    ContactsContract.Contacts.openContactPhotoInputStream
//            // (getContentResolver(),
//            //                                                                          Uri.parse
//            // (photo));
//
//            Uri uri = Uri.withAppendedPath(Uri.parse(photo), Photo.CONTENT_DIRECTORY);
//
//            InputStream input = null;
//
//            Cursor photoCursor =
//                    getContentResolver().query(uri, new String[]{Contacts.Photo.PHOTO}, null, null,
//                                               null);
//            if (photoCursor != null) {
//
//                try {
//                    if (photoCursor.moveToFirst()) {
//                        Log.d("", DatabaseUtils.dumpCursorToString(photoCursor));
//                        byte[] data = photoCursor.getBlob(photoCursor.getColumnIndex(Contacts.Photo.PHOTO));
//                        if (data != null) {
//                            input = new ByteArrayInputStream(data);
//                        }
//                    }
//                } finally {
//                    photoCursor.close();
//                    cursor.close();
//                }
//            }
//            //                AssetFileDescriptor fd = getContentResolver()
//            // .openAssetFileDescriptor(uri, "r");
//            //
//            //                final InputStream input = fd.createInputStream();
//            // getContentResolver().openInputStream(Uri.parse(photo));
//
//            if (input != null) {
//
//                builder.setLargeIcon(BitmapFactory.decodeStream(input));
//            }

            final Notification notification = builder.build();
            final NotificationManagerCompat manager =
                    NotificationManagerCompat.from(getApplicationContext());

            manager.notify(333, notification);

            GcmReceiver.completeWakefulIntent(intent);

            return;
        }

        final RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(ClientApi.ENDPOINT)
                                                                 .setLogLevel(LogLevel.BASIC)
                                                                 .build();

        final ClientApi clientApi = restAdapter.create(ClientApi.class);

        clientApi.getUserInfos(new Callback<List<UserInfo>>() {

            @Override
            public void success(final List<UserInfo> userInfos, final Response response) {

                final Gson gson = new Gson();
                final String json = gson.toJson(userInfos);

                final Intent viewIntent =
                        new Intent(getApplicationContext(), LocationActivity.class);
                viewIntent.putExtra(EXTRA_USER_INFO, json);

                final PendingIntent viewPendingIntent =
                        PendingIntent.getActivity(getApplicationContext(), 0, viewIntent, 0);

                final Builder builder = new Builder(GcmIntentService.this);
                builder.setSmallIcon(R.drawable.ic_user_status)
                       .setContentTitle("TOUCH")
                       .setContentIntent(viewPendingIntent)
                       .setVibrate(new long[]{400, 200, 100});

                if (!userInfos.isEmpty()) {

                    final UserInfo userInfo = userInfos.get(0);

                    final ContactsManager contactsManager = new ContactsManager(getApplication());
                    final Cursor cursor = contactsManager.getContacts(userInfo.getUserName());
                    final Contact contact = contactsManager.getContact(cursor, false);

                    builder.setLargeIcon(BitmapFactory.decodeFile(contact.getPhoto()));

                    final Intent intentMessage =
                            new Intent(getApplicationContext(), UserMessageService.class);
                    //final Uri uri = Uri.parse("geo:0,0=?q=51.524559,-0.099096");
                    //intentMessage.setData(uri);
                    intentMessage.putExtra(UserMessageService.EXTRA_DEST_USER,
                                           userInfo.getUserName());
                    intentMessage.putExtra(UserMessageService.EXTRA_DEST_MESSAGE, "happy");

                    final PendingIntent messagePendingIntent =
                            PendingIntent.getService(getApplicationContext(), 0, intentMessage, 0);

                    builder.addAction(R.drawable.smile, "FARE!", messagePendingIntent);
                }

                final Notification notification = builder.build();
                final NotificationManagerCompat manager =
                        NotificationManagerCompat.from(getApplicationContext());

                manager.notify(1, notification);

                GcmReceiver.completeWakefulIntent(intent);
            }

            @Override
            public void failure(final RetrofitError error) {

                GcmReceiver.completeWakefulIntent(intent);
            }
        });
    }
}
