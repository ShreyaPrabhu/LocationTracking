package com.example.shreyaprabhu.locationtracking;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.shreyaprabhu.locationdetectlibrary.GooglePlayServicesApi.ActionDetection.ActivityDetection;
import com.example.shreyaprabhu.locationdetectlibrary.GooglePlayServicesApi.LocationDetection.LocationDetection;
import com.example.shreyaprabhu.locationdetectlibrary.LocationManagerProviders.LocationManagerProviders;
import com.example.shreyaprabhu.locationdetectlibrary.ToastMaker.ToastMaker;

public class MainActivity extends AppCompatActivity {

    Context context;
    LocationManagerProviders locationManagerProviders;
    ActivityDetection activityDetection;
    LocationDetection locationDetection;
    ToastMaker toastMaker;
    Button ProviderGetLocation;
    Button ProviderStop;
    Button DetectAction;
    Button StopDetectAction;
    Button GPSALocation;
    Button GPSAUpdates;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = MainActivity.this;

        //create an object of ToastMaker
        toastMaker = new ToastMaker();

        //create an object of LocationManagerProvider Class
        locationManagerProviders = new LocationManagerProviders(context, MainActivity.this);

        ProviderGetLocation = (Button) findViewById(R.id.ProviderGetLocation);
        ProviderStop = (Button) findViewById(R.id.ProviderStop);

        //to check GPS/Wi-Fi enabled
        ProviderGetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(context,
                                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                } else {
                    Boolean canGetLocation = locationManagerProviders.canGetLocation();
                    if (canGetLocation) {

                        //to get the current location of the device
                        Location location = locationManagerProviders.getLocation(context);

                        //to access latitude and longitude values
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        String string = "Lat :" + latitude + "\n" + "Long :" + longitude;
                        toastMaker.ToastMakerShort(context, string);

                        //to find which satellite system is being used to detect location
                        locationManagerProviders.checkSystem();
                    }

                    //if NETWORK_PROVIDER/GPS_PROVIDER not available, show dialog box to change location settings
                    if (!canGetLocation) {
                        locationManagerProviders.showSettingsAlert();
                    }
                }
            }
        });


        //to request location updates through location manager, set minimum distance and time for updates
        //locationManagerProviders.MinDistanceForUpdates(100000);
        //locationManagerProviders.MinTimeForUpdates(1000*60);


        //to stop using GPS LISTENER
        ProviderStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationManagerProviders.stopUsingGPS();
            }
        });

        //create an object of Location Detection Class
        locationDetection = new LocationDetection();
        locationDetection.Intialise(context, MainActivity.this);

        GPSALocation = (Button) findViewById(R.id.GPSALoc);
        GPSAUpdates = (Button) findViewById(R.id.GPSAUpdates);
        GPSALocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(context,
                                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                } else {
                    locationDetection.getLocation();
                }
            }
        });
        GPSAUpdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationDetection.getLocationUpdates(10000, 5000);
            }
        });


        //create an object of ActivityDetection Class
        activityDetection = new ActivityDetection();
        activityDetection.Intialise(context);

        DetectAction = (Button) findViewById(R.id.DetectAction);
        StopDetectAction = (Button) findViewById(R.id.StopDetectAction);


        //to get Acitivity Updates about the device
        DetectAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    activityDetection.requestActivityUpdates(context);
                }
        });

        //to stop recieving Activity Updates about the device
        StopDetectAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityDetection.removeActivityUpdates(context);
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the

                    // contacts-related task you need to do.
                    toastMaker.ToastMakerShort(context,"Permission Granted! Click Again");
                } else {
                    // Can't get location.
                    // GPS or network is not enabled.
                    // Ask user to enable GPS/network in settings.
                    toastMaker.ToastMakerShort(context,"Permission not granted");
                }


            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //start();
        activityDetection.startActivityDetection();
        locationDetection.startLocationDetection();

    }

    @Override
    protected void onStop() {
        super.onStop();
        //stop();
        activityDetection.stopActivityDetection();
        locationDetection.stopLocationDetection();
    }

    @Override
    protected void onResume() {
        super.onResume();
        activityDetection.resumeActivityDetection(context);
    }

    @Override
    protected void onPause() {
        activityDetection.pauseActivityDetection(context);
        super.onPause();
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
}
