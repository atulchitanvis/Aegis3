package com.example.android.spitit;

import android.content.Context;
import android.content.res.Resources;

import com.google.android.gms.location.GeofenceStatusCodes;

/**
 * Created by aashayjain611 on 08/01/18.
 */

class GeofenceErrorMessages {

    public GeofenceErrorMessages()
    {

    }

    public static String getErrorString(Context context, int errorCode)
    {
        Resources resources=context.getResources();
        switch (errorCode)
        {
            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                return "Geofence service is not available now";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                return "Your app has registered too many geofences";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                return "You have added too many PendingIntents to addGeo...";
            default:
                return "Unknown error : The Geofence service is not available now";
        }
    }

}
