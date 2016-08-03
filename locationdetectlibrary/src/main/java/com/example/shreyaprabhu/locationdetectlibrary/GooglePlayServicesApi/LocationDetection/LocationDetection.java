package com.example.shreyaprabhu.locationdetectlibrary.GooglePlayServicesApi.LocationDetection;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import android.util.Log;
import android.widget.Toast;

import com.example.shreyaprabhu.locationdetectlibrary.ToastMaker.ToastMaker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by Shreya Prabhu on 8/2/2016.
 */
public class LocationDetection implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleApiClient mgoogleApiClient;
    private Location mLocation;
    private LocationRequest mLocationRequest;
    private String mLastUpdateTime;
    private static final String TAG = "MainActivity";
    Context context;
    Activity activity;
    ToastMaker toastMaker = new ToastMaker();

    /*
     *  Initialise the required variables through MainActivity
     */
    public void Intialise(Context context, Activity activity) {

        mgoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        this.context = context;
        this.activity = activity;
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    /*
     * Get Location using Google Play Services API
     */

    public void getLocation() {
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mgoogleApiClient);
        if (mLocation != null) {
            toastMaker.ToastMakerShort(context,"Latitude:" + mLocation.getLatitude() + "\n" + "Longitude:" + mLocation.getLongitude());
        } else {
            toastMaker.ToastMakerShort(context,"Location not Detected");
        }
    }

    /*
     * tasks to be performed once permission is granted(for Marshmallow and above)
     */
    public void onRequestResult() {
        // permission was granted, yay! Do the

        // contacts-related task you need to do.
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mgoogleApiClient);
        if (mLocation != null) {
            toastMaker.ToastMakerShort(context,"Latitude:" + mLocation.getLatitude() + "\n" + "Longitude:" + mLocation.getLongitude());
        } else {
            toastMaker.ToastMakerShort(context,"Location not Detected");
        }


    }

    public void startLocationDetection() {
        mgoogleApiClient.connect();

    }

    public void stopLocationDetection() {
        if (mgoogleApiClient.isConnected()) {
            mgoogleApiClient.disconnect();
        }

    }

    /*
     * Function to get Location updates after every given interval of time
     */

    public void getLocationUpdates(int interval, int fastestInterval) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setInterval(interval);
        mLocationRequest.setFastestInterval(fastestInterval);
        LocationServices.FusedLocationApi.requestLocationUpdates(mgoogleApiClient, mLocationRequest, this);
    }

    /*
     * Function called when location is changed.
     */
    @Override
    public void onLocationChanged(Location location) {
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        toastMaker.ToastMakerShort(context,"Latitude:" + mLocation.getLatitude() + "\n" + "Longitude:" + mLocation.getLongitude());
        toastMaker.ToastMakerShort(context, "Updated: " + mLastUpdateTime);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection Suspended");
        mgoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed. Error: " + connectionResult.getErrorCode());

    }
}
