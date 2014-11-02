package io.github.bananapp.httone.gcm;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.app.NotificationManagerCompat;

import com.google.gson.Gson;

import java.util.List;

import io.github.bananapp.httone.LocationActivity;
import io.github.bananapp.httone.R;
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

    public GcmIntentService() {

        super("gcm_service");
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
                final Intent intentMap = new Intent(Intent.ACTION_VIEW);
                final Uri uri = Uri.parse("geo:0,0=?q=51.524559,-0.099096");
                intentMap.setData(uri);

                final PendingIntent farePendingIntent =
                        PendingIntent.getActivity(getApplicationContext(), 0, intentMap, 0);

                final Builder builder = new Builder(GcmIntentService.this);
                final Notification notification =
                        builder.setSmallIcon(R.drawable.abc_ic_commit_search_api_mtrl_alpha)
                               .setContentTitle("SOCCAZZI")
                               .setContentText("HOGIAMMANGIATO!!!")
                               .setContentIntent(viewPendingIntent)
                               .addAction(R.drawable.abc_ab_share_pack_holo_light, "FARE!",
                                          farePendingIntent)
                               .build();

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
