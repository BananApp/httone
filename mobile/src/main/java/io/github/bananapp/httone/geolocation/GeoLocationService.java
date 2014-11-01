package io.github.bananapp.httone.geolocation;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

public class GeoLocationService implements LocationListener {

    private final LocationManager mLocationManager;

    private GeoLocationCallback mCallback;

    public GeoLocationService(Activity activity) {

        mLocationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
    }

    public void cancelRequest() {

        mLocationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {

        Log.d("", "onLocationChanged: " + location);

        mCallback.onLocationChanged(location);

        cancelRequest();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        //No-op
    }

    @Override
    public void onProviderEnabled(String provider) {
        //No-op
    }

    @Override
    public void onProviderDisabled(String provider) {
        //No-op
    }

    public void requestLocation(GeoLocationCallback callback) {

        mCallback = callback;

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setAltitudeRequired(false);
        criteria.setSpeedRequired(false);

        final String provider = mLocationManager.getBestProvider(criteria, true);

        if (!TextUtils.isEmpty(provider)) {

            final Location location = mLocationManager.getLastKnownLocation(provider);

            if (location != null) {

                Log.d("", "got last known location available: " + location);

                onLocationChanged(location);
                return;
            }

            Log.d("", "No last known location available, requesting one");

            mLocationManager.requestLocationUpdates(provider, 0, 0, this);

        } else {

            Log.d("", "No provider enabled, notify client error");

            mCallback.onLocationFailure();
        }
    }

    public interface GeoLocationCallback {

        public void onLocationFailure();

        public void onLocationChanged(Location location);
    }
}
