package es.simbiosys.cordova.plugin.motion.sensors;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionRequest;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.apache.cordova.CallbackContext;

import java.util.ArrayList;
import java.util.List;

public class ActivityDetection {

    private static final String TAG = "ActivityDetection";

    private final int[] availableActivityTypes = new int[]{
            DetectedActivity.IN_VEHICLE,
            DetectedActivity.ON_BICYCLE,
            DetectedActivity.RUNNING,
            DetectedActivity.STILL,
            DetectedActivity.WALKING
    };

    private Context mContext;
    private List<ActivityTransition> transitions;
    private ActivityTransitionRequest request;
    private PendingIntent pendingIntent;


    public ActivityDetection (Context context) {

        // Save context
        this.mContext = context;

        // Initialize transitions and add all available activity types
        this.transitions = new ArrayList<>();
        for (int activityType : availableActivityTypes) {
            // Adding enter and exit transition for each activity type
            this.transitions.add(
                    new ActivityTransition.Builder()
                        .setActivityType(activityType)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                        .build()
            );
            this.transitions.add(
                    new ActivityTransition.Builder()
                            .setActivityType(activityType)
                            .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                            .build()
            );
        }

        // Initialize activity transition request
        this.request = new ActivityTransitionRequest(this.transitions);
    }

    public void startCapture (CallbackContext callbackContext) {
        // Initialize pending intent
        Intent intent = new Intent(this.mContext, ActivityDetectionReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(
                this.mContext,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Task<Void> task = ActivityRecognition.getClient(this.mContext)
                .requestActivityTransitionUpdates(this.request, this.pendingIntent);

        task.addOnSuccessListener(
                new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callbackContext.success("Activity detection started successfully");
                        // Log.d(TAG, "Activity detection started successfully");
                    }
                }
        );

        task.addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callbackContext.error("Activity detection did not start due an error");
                        // Log.d(TAG, "Activity detection did not start due an error");
                        Log.e(TAG, e.getMessage());
                    }
                }
        );
    }

    public void startPolling (CallbackContext callbackContext) {
        // Initialize pending intent
        Intent intent = new Intent(mContext, ActivityDetectionService.class);
        pendingIntent = PendingIntent.getService(
                mContext,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        Task<Void> task = ActivityRecognition.getClient(this.mContext)
                .requestActivityUpdates(0, pendingIntent);

        task.addOnSuccessListener(
                new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callbackContext.success("Activity polling started successfully");
                    }
                }
        );
        task.addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callbackContext.error("Activity polling did not start due an error");
                        Log.e(TAG, e.getMessage());
                    }
                }
        );
    }

    public void stopCapture (CallbackContext callbackContext) {
        Task<Void> task = ActivityRecognition.getClient(this.mContext)
                .removeActivityTransitionUpdates(this.pendingIntent);

        task.addOnSuccessListener(
                new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        pendingIntent.cancel();
                        if (callbackContext != null) {
                            callbackContext.success("Activity detection stopped successfully");
                        }
                        Log.d(TAG, "Activity detection stopped successfully");
                    }
                }
        );

        task.addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (callbackContext != null) {
                            callbackContext.error("Activity detection did not stop due an error");
                        }
                        Log.e(TAG, e.getMessage());
                    }
                }
        );
    }

    public void stopPolling (CallbackContext callbackContext) {
        Task<Void> task = ActivityRecognition.getClient(this.mContext)
                .removeActivityUpdates(this.pendingIntent);

        task.addOnSuccessListener(
                new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        pendingIntent.cancel();
                        if (callbackContext != null) {
                            callbackContext.success("Activity polling stopped successfully");
                        }
                        Log.d(TAG, "Activity polling stopped successfully");
                    }
                }
        );

        task.addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (callbackContext != null) {
                            callbackContext.error("Activity polling did not stop due an error");
                        }
                        Log.e(TAG, e.getMessage());
                    }
                }
        );
    }

    public void setEventsCallbackContext (CallbackContext callbackContext) {
        ActivityDetectionReceiver.eventsCallbackContext = callbackContext;
        ActivityDetectionService.eventsCallbackContext = callbackContext;
    }

    public void setFusedLocationClient (boolean withLocation) {
        ActivityDetectionReceiver.fusedLocationClient = withLocation ? LocationServices.getFusedLocationProviderClient(mContext) : null;
    }
}