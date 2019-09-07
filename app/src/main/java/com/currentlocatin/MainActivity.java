package com.currentlocatin;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    TextView tv_location;
    Button button;
    static MainActivity mInstance;
    LocationRequest locationRequest;
    FusedLocationProviderClient fusedLocationProviderClient;
    Double latitude, longitude;
    boolean enabled;

    public static MainActivity getInstance() {
        return mInstance;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mInstance = this;

        tv_location = findViewById(R.id.tv_location);
        button = findViewById(R.id.button);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Dexter.withActivity(this)
                    .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse response) {
                            updateLocation();
                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse response) {
                            Toast.makeText(MainActivity.this, "You Denied Location", Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                        }
                    }).check();
        } else {
            updateLocation();
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
                    boolean enabled = false;
                    if (service != null) {
                        enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);
                    }

                    // Check if enabled and if not send user to the GPS settings
                    if (!enabled) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }else {
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest, getPendingIntent());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void updateLocation() {
        try {
            buildLocationRequest();
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(this, MyLocationService.class);
        intent.setAction(MyLocationService.ACTION_PROCESS_UPDATES);
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void buildLocationRequest() {
        try {
            locationRequest = new LocationRequest();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(5000);
            locationRequest.setFastestInterval(3000);
            locationRequest.setSmallestDisplacement(10f);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void updateTextView(final double lat, final double lng) {

        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    latitude = lat;
                    longitude = lng;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        try {
            String address = null;

            Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
            if (Geocoder.isPresent()) {
                List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
                if (addressList != null && addressList.size() > 0) {
                    address = addressList.get(0).getAddressLine(0);
                    Toast.makeText(this, address, Toast.LENGTH_SHORT).show();
                    tv_location.setText(lat + "\n" + lng + "\n " + address);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
