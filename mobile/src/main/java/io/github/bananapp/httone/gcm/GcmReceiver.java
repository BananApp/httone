package io.github.bananapp.httone.gcm;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        final Bundle extras = intent.getExtras();

        final GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
        final String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {

            if (messageType != null && messageType
                    .equals(GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE)) {

                final ComponentName component =
                        new ComponentName(context.getPackageName(), GcmIntentService.class.getName());

                intent.setComponent(component);
                intent.setAction(GcmIntentService.ACTION_GCM_MESSAGE);

                startWakefulService(context, intent);
            }
        }

        setResultCode(Activity.RESULT_OK);
    }
}