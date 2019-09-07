package com.currentlocatin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.LocationResult;


public class MyLocationService extends BroadcastReceiver {
    private static final String TAG = "MyLocationService";
    public static final String ACTION_PROCESS_UPDATES = "com.currentlocatin.UPDATE_LOCATION";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent != null) {
            try {
                final String action = intent.getAction();

                if (ACTION_PROCESS_UPDATES.equals(action)) {
                    LocationResult locationRequest = LocationResult.extractResult(intent);
                    if (locationRequest != null) {
                        Location location = locationRequest.getLastLocation();

                        String cuLocations = new StringBuilder(String.valueOf(location.getLatitude())).append("\n").append(location.getLongitude()).toString();

                        try {
                            MainActivity.getInstance().updateTextView(location.getLatitude(),location.getLongitude());
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e(TAG, "onReceive: " + cuLocations);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
