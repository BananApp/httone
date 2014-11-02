package io.github.bananapp.httone;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import io.github.bananapp.httone.model.UserMessage;
import io.github.bananapp.httone.networking.ClientApi;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RestAdapter.LogLevel;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class UserMessageService extends IntentService {

    public static final String EXTRA_DEST_MESSAGE = "dest_message";

    public static final String EXTRA_DEST_USER = "dest_user";

    public UserMessageService() {

        super("message_service");
    }

    @Override
    protected void onHandleIntent(final Intent intent) {

        final RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(ClientApi.ENDPOINT)
                                                                 .setLogLevel(LogLevel.BASIC)
                                                                 .build();

        final ClientApi clientApi = restAdapter.create(ClientApi.class);

        final SharedPreferences prefs = getSharedPreferences("httone", Context.MODE_PRIVATE);
        final String accountName = prefs.getString(LocationActivity.KEY_ACCOUNT_NAME, null);

        final String destUser = intent.getStringExtra(EXTRA_DEST_USER);
        final UserMessage message =
                new UserMessage(accountName, intent.getStringExtra(EXTRA_DEST_MESSAGE));

        clientApi.notifyMessage(destUser, message, accountName, new Callback<Void>() {

            @Override
            public void success(final Void aVoid, final Response response) {

            }

            @Override
            public void failure(final RetrofitError error) {

            }
        });
    }
}
