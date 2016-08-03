package com.example.shreyaprabhu.locationdetectlibrary.GooglePlayServicesApi.ActionDetection;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.shreyaprabhu.locationdetectlibrary.R;
import com.example.shreyaprabhu.locationdetectlibrary.ToastMaker.ToastMaker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;

/**
 * Created by Shreya Prabhu on 8/2/2016.
 */
public class ActivityDetection implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {

    private static final String TAG = "ActivityDetection";
    private GoogleApiClient mGoogleApiClient;
    private ActivityDetectionBroadcastReceiver mBroadcastReceiver;
    ToastMaker toastMaker = new ToastMaker();

/*
 *  Initialise the required variables through MainActivity
 */
    public void Intialise(Context context){
        mBroadcastReceiver = new ActivityDetectionBroadcastReceiver();

        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(ActivityRecognition.API)
                .build();
    }

/*
 *  Request Action updates of the device
 */
    public void requestActivityUpdates(Context context) {
        if (!mGoogleApiClient.isConnected()) {
            toastMaker.ToastMakerShort(context,"GoogleApiClient not yet connected");
        } else {
            ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mGoogleApiClient, 0, getActivityDetectionPendingIntent(context)).setResultCallback(this);
        }
    }

/*
 *  Remove Action updates of the device
 */

    public void removeActivityUpdates(Context context) {
        ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(mGoogleApiClient, getActivityDetectionPendingIntent(context)).setResultCallback(this);
    }

    private PendingIntent getActivityDetectionPendingIntent(Context context) {
        Intent intent = new Intent(context, ActivitiesIntentService.class);

        return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

/*
 *  Broadcast Reciever for detection of Activity of the device
 */


    public class ActivityDetectionBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<DetectedActivity> detectedActivities = intent.getParcelableArrayListExtra(Constants.STRING_EXTRA);
            String activityString = "";
            for(DetectedActivity activity: detectedActivities) {
                activityString = "Activity: " + getDetectedActivity(activity.getType(),context) + ", Confidence: " + activity.getConfidence() + "%\n";
                toastMaker.ToastMakerShort(context,activityString);
            }

        }
    }

/*
 *  Get type of detected activity
 */

    public String getDetectedActivity(int detectedActivityType, Context context) {
        Resources resources = context.getResources();
        switch(detectedActivityType) {
            case DetectedActivity.IN_VEHICLE:
                return resources.getString(R.string.in_vehicle);
            case DetectedActivity.ON_BICYCLE:
                return resources.getString(R.string.on_bicycle);
            case DetectedActivity.ON_FOOT:
                return resources.getString(R.string.on_foot);
            case DetectedActivity.RUNNING:
                return resources.getString(R.string.running);
            case DetectedActivity.WALKING:
                return resources.getString(R.string.walking);
            case DetectedActivity.STILL:
                return resources.getString(R.string.still);
            case DetectedActivity.TILTING:
                return resources.getString(R.string.tilting);
            case DetectedActivity.UNKNOWN:
                return resources.getString(R.string.unknown);
            default:
                return resources.getString(R.string.unidentifiable_activity, detectedActivityType);
        }
    }

    public void startActivityDetection(){
        mGoogleApiClient.connect();
    }

    public void stopActivityDetection(){
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    public void resumeActivityDetection(Context context){
        LocalBroadcastManager.getInstance(context).registerReceiver(mBroadcastReceiver, new IntentFilter(Constants.STRING_ACTION));
    }

    public void pauseActivityDetection(Context context){
        LocalBroadcastManager.getInstance(context).unregisterReceiver(mBroadcastReceiver);
    }



    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Connected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed. Error: " + connectionResult.getErrorCode());
    }

    @Override
    public void onResult(Status status) {
        if (status.isSuccess()) {
            Log.e(TAG, "Successfully added activity detection.");

        } else {
            Log.e(TAG, "Error: " + status.getStatusMessage());
        }
    }
}
