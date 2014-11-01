package io.github.bananapp.httone;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.ErrorDialogFragment;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.location.Geofence;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import io.github.bananapp.httone.geofence.GeofenceRemover;
import io.github.bananapp.httone.geofence.GeofenceRequester;
import io.github.bananapp.httone.geofence.GeofenceUtils;
import io.github.bananapp.httone.geofence.GeofenceUtils.REMOVE_TYPE;
import io.github.bananapp.httone.geofence.GeofenceUtils.REQUEST_TYPE;
import io.github.bananapp.httone.geofence.SimpleGeofence;
import io.github.bananapp.httone.geofence.SimpleGeofenceStore;
import io.github.bananapp.httone.geolocation.GeoLocationService;
import io.github.bananapp.httone.geolocation.GeoLocationService.GeoLocationCallback;
import io.github.bananapp.httone.model.Place;
import io.github.bananapp.httone.model.UserAccount;
import io.github.bananapp.httone.networking.ClientApi;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RestAdapter.LogLevel;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class LocationActivity extends Activity implements GeoLocationCallback {

    private static final String KEY_ACCOUNT_NAME = "KEY_ACCOUNT_NAME";

    private static final int RC_PICK_GOOGLE_ACCOUNT = 33;

    private String mAccountName;

    /*
         * An instance of an inner class that receives broadcasts from listeners and from the
         * IntentService that receives geofence transition events
         */
    private GeofenceSampleReceiver mBroadcastReceiver;

    private ClientApi mClientApi;

    // Store a list of geofences to add
    private List<Geofence> mCurrentGeofences;

    private Location mCurrentLocation;

    // Store the list of geofences to remove
    private List<String> mGeofenceIdsToRemove;

    // Remove geofences handler
    private GeofenceRemover mGeofenceRemover;

    // Add geofences handler
    private GeofenceRequester mGeofenceRequester;

    private List<SimpleGeofence> mGeofences;

    // An intent filter for the broadcast receiver
    private IntentFilter mIntentFilter;

    private GeoLocationService mLocationService;

    // Persistent storage for geofences
    private SimpleGeofenceStore mPrefs;

    // Store the current type of removal
    private REMOVE_TYPE mRemoveType;

    // Store the current request
    private REQUEST_TYPE mRequestType;

    @Override
    public void onLocationFailure() {

    }

    @Override
    public void onLocationChanged(final Location location) {

        mCurrentLocation = location;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Instantiate a new geofence storage area
        mPrefs = new SimpleGeofenceStore(this);

        // Instantiate the current List of geofences
        mCurrentGeofences = new ArrayList<Geofence>();

        // Instantiate a Geofence requester
        mGeofenceRequester = new GeofenceRequester(this);

        // Instantiate a Geofence remover
        mGeofenceRemover = new GeofenceRemover(this);

        // Create a new broadcast receiver to receive updates from the listeners and service
        mBroadcastReceiver = new GeofenceSampleReceiver();

        // Create an intent filter for the broadcast receiver
        mIntentFilter = new IntentFilter();

        // Action for broadcast Intents that report successful addition of geofences
        mIntentFilter.addAction(GeofenceUtils.ACTION_GEOFENCES_ADDED);

        // Action for broadcast Intents that report successful removal of geofences
        mIntentFilter.addAction(GeofenceUtils.ACTION_GEOFENCES_REMOVED);

        // Action for broadcast Intents containing various types of geofencing errors
        mIntentFilter.addAction(GeofenceUtils.ACTION_GEOFENCE_ERROR);

        // All Location Services sample apps use this category
        mIntentFilter.addCategory(GeofenceUtils.CATEGORY_LOCATION_SERVICES);

        mGeofences = new ArrayList<SimpleGeofence>();

        mLocationService = new GeoLocationService(this);

        final RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(ClientApi.ENDPOINT)
                                                                 .setLogLevel(LogLevel.BASIC)
                                                                 .build();

        mClientApi = restAdapter.create(ClientApi.class);

        final Animation animation_save_button = AnimationUtils.loadAnimation(this, R.anim.scale);

        final EditText name = (EditText) findViewById(R.id.editText);

        final View button = findViewById(R.id.button);
        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(final View v) {

                final Location location = mCurrentLocation;

                if (location != null) {

                    v.startAnimation(animation_save_button);

                    final String placeName = name.getText().toString();

                    final Place place =
                            new Place(location.getLatitude(), location.getLongitude(), 10,
                                      placeName, "");

                    mClientApi.createPlace(place, new Callback<String>() {

                        @Override
                        public void success(final String s, final Response response) {

                            final SimpleGeofence fence =
                                    new SimpleGeofence(s, placeName, location.getLatitude(),
                                                       location.getLongitude(), 10, Long.MAX_VALUE,
                                                       Geofence.GEOFENCE_TRANSITION_DWELL
                                                               | Geofence.GEOFENCE_TRANSITION_EXIT);
                            final Geofence geofence = fence.toGeofence();

                            mCurrentGeofences.add(geofence);

                            try {
                                // Try to add geofences
                                mGeofenceRequester.addGeofences(mCurrentGeofences);

                            } catch (UnsupportedOperationException e) {
                                // Notify user that previous request hasn't finished.
                                Toast.makeText(LocationActivity.this,
                                               R.string.add_geofences_already_requested_error,
                                               Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void failure(final RetrofitError error) {

                            Toast.makeText(LocationActivity.this,
                                           R.string.add_geofences_result_failure, Toast.LENGTH_LONG)
                                 .show();
                        }
                    });
                }
            }
        });

        getAccount();
    }

    @Override
    protected void onResume() {

        super.onResume();

        if (!servicesConnected()) {

            return;
        }

        mGeofences = mPrefs.getGeofences();
    }

    @Override
    protected void onPause() {

        super.onPause();
        mPrefs.setGeofences(mGeofences);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
     * Handle results returned to this Activity by other Activities started with
     * startActivityForResult(). In particular, the method onConnectionFailed() in
     * GeofenceRemover and GeofenceRequester may call startResolutionForResult() to
     * start an Activity that handles Google Play services problems. The result of this
     * call returns here, to onActivityResult.
     * calls
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        // Choose what to do based on the request code
        switch (requestCode) {

            // If the request code matches the code sent in onConnectionFailed
            case GeofenceUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST:

                switch (resultCode) {
                    // If Google Play services resolved the problem
                    case Activity.RESULT_OK:

                        // If the request was to add geofences
                        if (GeofenceUtils.REQUEST_TYPE.ADD == mRequestType) {

                            // Toggle the request flag and send a new request
                            mGeofenceRequester.setInProgressFlag(false);

                            // Restart the process of adding the current geofences
                            mGeofenceRequester.addGeofences(mCurrentGeofences);

                            // If the request was to remove geofences
                        } else if (GeofenceUtils.REQUEST_TYPE.REMOVE == mRequestType) {

                            // Toggle the removal flag and send a new removal request
                            mGeofenceRemover.setInProgressFlag(false);

                            // If the removal was by Intent
                            if (GeofenceUtils.REMOVE_TYPE.INTENT == mRemoveType) {

                                // Restart the removal of all geofences for the PendingIntent
                                mGeofenceRemover.removeGeofencesByIntent(
                                        mGeofenceRequester.getRequestPendingIntent());

                                // If the removal was by a List of geofence IDs
                            } else {

                                // Restart the removal of the geofence list
                                mGeofenceRemover.removeGeofencesById(mGeofenceIdsToRemove);
                            }
                        }
                        break;

                    // If any other result was returned by Google Play services
                    default:

                        // Report that Google Play services was unable to resolve the problem.
                        Log.d(GeofenceUtils.APPTAG, getString(R.string.no_resolution));
                }

                break;

            case RC_PICK_GOOGLE_ACCOUNT:

                if (resultCode == Activity.RESULT_OK) {

                    onUserAccount(intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME));
                }

                break;

            // If any other request code was received
            default:
                // Report that this Activity received an unknown requestCode
                Log.d(GeofenceUtils.APPTAG,
                      getString(R.string.unknown_activity_request_code, requestCode));

                break;
        }
    }

    private void getAccount() {

        final SharedPreferences prefs = getSharedPreferences("httone", Context.MODE_PRIVATE);
        final String accountName = prefs.getString(KEY_ACCOUNT_NAME, null);

        if (!TextUtils.isEmpty(accountName)) {

            setAccount(accountName);

            return;
        }

        String[] accountTypes = new String[]{GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE};

        Intent intent =
                AccountPicker.newChooseAccountIntent(null, null, accountTypes, BuildConfig.DEBUG,
                                                     null, null, null, null);

        startActivityForResult(intent, RC_PICK_GOOGLE_ACCOUNT);
    }

    private void onUserAccount(final String accountName) {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(final Void... params) {

                try {

                    final String registerId =
                            GoogleCloudMessaging.getInstance(LocationActivity.this)
                                                .register("832095141376");

                    mClientApi.registerAccount(new UserAccount(accountName, registerId),
                                               new Callback<Void>() {

                                                   @Override
                                                   public void success(final Void aVoid,
                                                           final Response response) {

                                                       setAccount(accountName);
                                                   }

                                                   @Override
                                                   public void failure(final RetrofitError error) {

                                                   }
                                               });

                } catch (final IOException ignored) {

                }

                return null;
            }
        }.execute();
    }

    /**
     * Verify that Google Play services is available before making a request.
     *
     * @return true if Google Play services is available, otherwise false
     */
    private boolean servicesConnected() {

        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {

            // In debug mode, log the status
            Log.d(GeofenceUtils.APPTAG, getString(R.string.play_services_available));

            // Continue
            return true;

            // Google Play services was not available for some reason
        } else {

            // Display an error dialog
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, 0);
            if (dialog != null) {
                ErrorDialogFragment errorFragment = ErrorDialogFragment.newInstance(dialog);
                errorFragment.show(getFragmentManager(), GeofenceUtils.APPTAG);
            }
            return false;
        }
    }

    private void setAccount(final String accountName) {

        final SharedPreferences prefs = getSharedPreferences("httone", Context.MODE_PRIVATE);

        prefs.edit().putString(KEY_ACCOUNT_NAME, accountName).apply();

        mAccountName = accountName;

        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, mIntentFilter);
        mLocationService.requestLocation(this);

        updatePlaces();
    }

    private void updatePlaces() {

        mClientApi.getPlaces(new Callback<List<Place>>() {

            @Override
            public void success(final List<Place> places, final Response response) {

                final List<SimpleGeofence> geofences;

                if (places != null) {

                    geofences = new ArrayList<SimpleGeofence>(places.size());

                    for (final Place place : places) {

                        geofences.add(SimpleGeofence.newGeofence(place));
                    }

                } else {

                    geofences = Collections.emptyList();
                }

                final ArrayList<String> idsToRemove = new ArrayList<String>();
                final Iterator<SimpleGeofence> iterator = mGeofences.iterator();

                while (iterator.hasNext()) {

                    final SimpleGeofence geofence = iterator.next();

                    if (!geofences.contains(geofence)) {

                        idsToRemove.add(geofence.getId());
                        iterator.remove();
                    }
                }

                if (!idsToRemove.isEmpty()) {

                    mGeofenceRemover.removeGeofencesById(idsToRemove);
                }

                for (SimpleGeofence geofence : geofences) {

                    if (!mGeofences.contains(geofence)) {

                        mGeofences.add(geofence);
                    }
                }

                for (SimpleGeofence geofence : mGeofences) {

                    mCurrentGeofences.add(geofence.toGeofence());
                }

                try {
                    // Try to add geofences
                    mGeofenceRequester.addGeofences(mCurrentGeofences);

                } catch (UnsupportedOperationException e) {
                    // Notify user that previous request hasn't finished.
                    Toast.makeText(LocationActivity.this,
                                   R.string.add_geofences_already_requested_error,
                                   Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void failure(final RetrofitError error) {

            }
        });
    }

    /**
     * Define a Broadcast receiver that receives updates from connection listeners and
     * the geofence transition service.
     */
    public class GeofenceSampleReceiver extends BroadcastReceiver {

        /*
         * Define the required method for broadcast receivers
         * This method is invoked when a broadcast Intent triggers the receiver
         */
        @Override
        public void onReceive(Context context, Intent intent) {

            // Check the action code and determine what to do
            String action = intent.getAction();

            // Intent contains information about errors in adding or removing geofences
            if (TextUtils.equals(action, GeofenceUtils.ACTION_GEOFENCE_ERROR)) {

                handleGeofenceError(context, intent);

                // Intent contains information about successful addition or removal of geofences
            } else if (TextUtils.equals(action, GeofenceUtils.ACTION_GEOFENCES_ADDED)
                    || TextUtils.equals(action, GeofenceUtils.ACTION_GEOFENCES_REMOVED)) {

                handleGeofenceStatus(context, intent);

                // Intent contains information about a geofence transition
            } else if (TextUtils.equals(action, GeofenceUtils.ACTION_GEOFENCE_TRANSITION)) {

                handleGeofenceTransition(context, intent);

                // The Intent contained an invalid action
            } else {
                Log.e(GeofenceUtils.APPTAG, getString(R.string.invalid_action_detail, action));
                Toast.makeText(context, R.string.invalid_action, Toast.LENGTH_LONG).show();
            }
        }

        /**
         * Report addition or removal errors to the UI, using a Toast
         *
         * @param intent A broadcast Intent sent by ReceiveTransitionsIntentService
         */
        private void handleGeofenceError(Context context, Intent intent) {

            String msg = intent.getStringExtra(GeofenceUtils.EXTRA_GEOFENCE_STATUS);
            Log.e(GeofenceUtils.APPTAG, msg);
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
        }

        /**
         * If you want to display a UI message about adding or removing geofences, put it here.
         *
         * @param context A Context for this component
         * @param intent  The received broadcast Intent
         */
        private void handleGeofenceStatus(Context context, Intent intent) {

        }

        /**
         * Report geofence transitions to the UI
         *
         * @param context A Context for this component
         * @param intent  The Intent containing the transition
         */
        private void handleGeofenceTransition(Context context, Intent intent) {
            /*
             * If you want to change the UI when a transition occurs, put the code
             * here. The current design of the app uses a notification to inform the
             * user that a transition has occurred.
             */
            final int transition = intent.getIntExtra(GeofenceUtils.EXTRA_GEOFENCE_TRANSITION, -1);

            if (transition == Geofence.GEOFENCE_TRANSITION_EXIT) {

                mClientApi.notifyPlace("NOWHERE", mAccountName, new Callback<Void>() {

                    @Override
                    public void success(final Void aVoid, final Response response) {

                    }

                    @Override
                    public void failure(final RetrofitError error) {

                    }
                });

            } else {

                final ArrayList<String> ids =
                        intent.getStringArrayListExtra(GeofenceUtils.EXTRA_GEOFENCE_IDS);

                if (!ids.isEmpty()) {

                    final String id = ids.get(0);

                    mClientApi.notifyPlace(id, mAccountName, new Callback<Void>() {

                        @Override
                        public void success(final Void aVoid, final Response response) {

                        }

                        @Override
                        public void failure(final RetrofitError error) {

                        }
                    });
                }
            }
        }
    }
}
