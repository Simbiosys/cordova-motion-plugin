package es.simbiosys.cordova.plugin.motion.sensors;

import android.Manifest;
import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ActivityDetectionService extends IntentService {

    private static final String TAG = "ActivityRecognition";
    private static final String eventName = "onActivityRecognition";

    public static CallbackContext eventsCallbackContext;
    public static FusedLocationProviderClient fusedLocationClient;

    public ActivityDetectionService () {
        super("ActivityDetectionService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent");

        if(ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            DetectedActivity activity = result.getMostProbableActivity();

            /* Log.d(TAG, "activity type: " + activity.getType());
            Log.d(TAG, "confidence: " + activity.getConfidence()); */

            JSONObject eventData = new JSONObject();

            try {
                // Build event data
                eventData.put("detectedActivities", getDetectedActivities(activity.getType()));
                eventData.put("confidence", activity.getConfidence());
                eventData.put("timestamp", getDateString(new Date()));

                /* if (fusedLocationClient != null) {
                    Log.d(TAG, "fusedLocationClient != null");
                } else {
                    Log.d(TAG, "fusedLocationClient == null");
                }

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PermissionChecker.PERMISSION_GRANTED) {
                    Log.d(TAG, "permission granted");
                } else {
                    Log.d(TAG, "permission NOT granted");
                } */

                if (fusedLocationClient != null
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PermissionChecker.PERMISSION_GRANTED) {
                    fusedLocationClient.getLastLocation()
                            .addOnSuccessListener(new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    if (location != null) {
                                        try {
                                            eventData.put("latitude", location.getLatitude());
                                            eventData.put("longitude", location.getLongitude());

                                        } catch (JSONException e) {
                                            Log.e(TAG, e.getMessage());
                                        }
                                    }
                                    triggerJsEvent(eventData);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e(TAG, e.getMessage());

                                    triggerJsEvent(eventData);
                                }
                            });
                } else {
                    triggerJsEvent(eventData);
                }
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage());
            }
        } else {
            Log.d(TAG, "No result");
        }
    }

    private JSONArray getDetectedActivities (int activityType) {
        JSONArray detectedActivities = new JSONArray();

        switch (activityType) {
            case DetectedActivity.IN_VEHICLE:
                detectedActivities.put("IN_VEHICLE");
                break;
            case DetectedActivity.ON_BICYCLE:
                detectedActivities.put("ON_BICYCLE");
                break;
            case DetectedActivity.RUNNING:
                detectedActivities.put("RUNNING");
                break;
            case DetectedActivity.STILL:
                detectedActivities.put("STILL");
                break;
            case DetectedActivity.WALKING:
                detectedActivities.put("WALKING");
                break;
            case DetectedActivity.ON_FOOT:
                detectedActivities.put("ON_FOOT");
                break;
            case DetectedActivity.TILTING:
                detectedActivities.put("TITLING");
                break;
            default:
                detectedActivities.put("UNKNOWN");
        }

        return detectedActivities;
    }

    private String getDateString(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        return formatter.format(date);
    }

    private void triggerJsEvent(JSONObject eventData) {
        JSONObject message = new JSONObject();

        try {
            // Build message
            message.put("eventName", eventName);
            message.put("eventData", eventData);

            if (eventsCallbackContext != null) {
                PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, message);
                pluginResult.setKeepCallback(true);
                eventsCallbackContext.sendPluginResult(pluginResult);
            }
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
    }
}
