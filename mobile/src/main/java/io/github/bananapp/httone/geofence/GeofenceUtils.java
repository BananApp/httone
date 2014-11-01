/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.bananapp.httone.geofence;

/**
 * This class defines constants used by location sample apps.
 */
public final class GeofenceUtils {

    // Intent actions
    public static final String ACTION_CONNECTION_ERROR =
            "io.github.bananapp.httone.geofence.ACTION_CONNECTION_ERROR";

    public static final String ACTION_CONNECTION_SUCCESS =
            "io.github.bananapp.httone.geofence.ACTION_CONNECTION_SUCCESS";

    public static final String ACTION_GEOFENCES_ADDED =
            "io.github.bananapp.httone.geofence.ACTION_GEOFENCES_ADDED";

    public static final String ACTION_GEOFENCES_REMOVED =
            "io.github.bananapp.httone.geofence.ACTION_GEOFENCES_DELETED";

    public static final String ACTION_GEOFENCE_ERROR =
            "io.github.bananapp.httone.geofence.ACTION_GEOFENCES_ERROR";

    public static final String ACTION_GEOFENCE_TRANSITION =
            "io.github.bananapp.httone.geofence.ACTION_GEOFENCE_TRANSITION";

    public static final String ACTION_GEOFENCE_TRANSITION_ERROR =
            "io.github.bananapp.httone.geofence.ACTION_GEOFENCE_TRANSITION_ERROR";

    /*
     * A log tag for the application
     */
    public static final String APPTAG = "Geofence Detection";

    // The Intent category used by all Location Services sample apps
    public static final String CATEGORY_LOCATION_SERVICES =
            "io.github.bananapp.httone.geofence.CATEGORY_LOCATION_SERVICES";

    /*
     * Define a request code to send to Google Play services
     * This code is returned in Activity.onActivityResult
     */
    public final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    // A string of length 0, used to clear out input fields
    public static final String EMPTY_STRING = new String();

    // Keys for extended data in Intents
    public static final String EXTRA_CONNECTION_CODE =
            "io.github.bananapp.httone.EXTRA_CONNECTION_CODE";

    public static final String EXTRA_CONNECTION_ERROR_CODE =
            "io.github.bananapp.httone.geofence.EXTRA_CONNECTION_ERROR_CODE";

    public static final String EXTRA_CONNECTION_ERROR_MESSAGE =
            "io.github.bananapp.httone.geofence.EXTRA_CONNECTION_ERROR_MESSAGE";

    public static final String EXTRA_GEOFENCE_IDS =
            "io.github.bananapp.httone.geofence.EXTRA_GEOFENCE_IDS";

    public static final String EXTRA_GEOFENCE_STATUS =
            "io.github.bananapp.httone.geofence.EXTRA_GEOFENCE_STATUS";

    public static final String EXTRA_GEOFENCE_TRANSITION =
            "io.github.bananapp.httone.geofence.EXTRA_GEOFENCE_TRANSITION";

    public static final CharSequence GEOFENCE_ID_DELIMITER = ",";

    public static final float INVALID_FLOAT_VALUE = -999.0f;

    public static final int INVALID_INT_VALUE = -999;

    // Invalid values, used to test geofence storage when retrieving geofences
    public static final long INVALID_LONG_VALUE = -999l;

    public static final String KEY_EXPIRATION_DURATION =
            "io.github.bananapp.httone.geofence.KEY_EXPIRATION_DURATION";

    public static final String KEY_LATITUDE = "io.github.bananapp.httone.geofence.KEY_LATITUDE";

    public static final String KEY_LONGITUDE = "io.github.bananapp.httone.geofence.KEY_LONGITUDE";

    /*
     * Keys for flattened geofences stored in SharedPreferences
     */
    public static final String KEY_NAME = "io.github.bananapp.httone.geofence.KEY_NAME";

    // The prefix for flattened geofence keys
    public static final String KEY_PREFIX = "io.github.bananapp.httone.geofence.KEY";

    public static final String KEY_RADIUS = "io.github.bananapp.httone.geofence.KEY_RADIUS";

    public static final String KEY_TRANSITION_TYPE =
            "io.github.bananapp.httone.geofence.KEY_TRANSITION_TYPE";

    /*
     * Constants used in verifying the correctness of input values
     */
    public static final double MAX_LATITUDE = 90.d;

    public static final double MAX_LONGITUDE = 180.d;

    public static final double MIN_LATITUDE = -90.d;

    public static final double MIN_LONGITUDE = -180.d;

    public static final float MIN_RADIUS = 1f;

    // Used to track what type of geofence removal request was made.
    public enum REMOVE_TYPE {
        INTENT, LIST
    }

    // Used to track what type of request is in process
    public enum REQUEST_TYPE {
        ADD, REMOVE
    }

}
