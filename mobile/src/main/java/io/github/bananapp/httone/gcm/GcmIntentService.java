package io.github.bananapp.httone.gcm;

import android.app.IntentService;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;

import java.util.List;

import io.github.bananapp.httone.LocationActivity;
import io.github.bananapp.httone.model.UserInfo;
import io.github.bananapp.httone.networking.ClientApi;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RestAdapter.LogLevel;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class GcmIntentService extends IntentService {

    public static final String ACTION_GCM_MESSAGE =
            "io.github.bananapp.httone.gcm.ACTION_GCM_MESSAGE";

    public static final String EXTRA_USER_INFO = "io.github.bananapp.httone.gcm.EXTRA_USER_INFO";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public GcmIntentService(final String name) {

        super(name);
    }

    @Override
    protected void onHandleIntent(final Intent intent) {

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

                final Notification notification =
                        new Builder(GcmIntentService.this).setContentIntent(viewPendingIntent)
                                                          .build();

                final NotificationManager manager =
                        (NotificationManager) getApplicationContext().getSystemService(
                                Context.NOTIFICATION_SERVICE);

                manager.notify(0, notification);

                GcmReceiver.completeWakefulIntent(intent);
            }

            @Override
            public void failure(final RetrofitError error) {

                GcmReceiver.completeWakefulIntent(intent);
            }
        });
    }
}
