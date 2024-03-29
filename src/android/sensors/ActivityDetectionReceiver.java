package es.simbiosys.cordova.plugin.motion.sensors;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.util.Log;

import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ActivityDetectionReceiver extends BroadcastReceiver {

    private static final String TAG = "ActivityDetection";
    private static final String eventName = "onActivityDetection";

    public static CallbackContext eventsCallbackContext;
    public static FusedLocationProviderClient fusedLocationClient;

    @Override
    public void onReceive(Context context, Intent intent) {
          Log.d(TAG, "onReceive");

          if (ActivityTransitionResult.hasResult(intent)) {
              ActivityTransitionResult result = ActivityTransitionResult.extractResult(intent);
              for (ActivityTransitionEvent event : result.getTransitionEvents()) {
                  Date currentDate = new Date();
                  // JSONObject message = new JSONObject();
                  JSONObject eventData = new JSONObject();

                  /* Log.d(TAG, "activity type: " + event.getActivityType());
                  Log.d(TAG, "transition type: " + event.getTransitionType());
                  Log.d(TAG, "elapsed real time: " + convertNsToMs(event.getElapsedRealTimeNanos()));
                  Log.d(TAG, "current time: " + currentDate.getTime()); */

                  long timestamp = currentDate.getTime() - (SystemClock.elapsedRealtime() - convertNsToMs(event.getElapsedRealTimeNanos()));

                  try {
                      // Build event data
                      eventData.put("detectedActivities", getDetectedActivities(event.getActivityType()));
                      eventData.put("transitionType", getTransitionTypeLiteral(event.getTransitionType()));
                      eventData.put("timestamp", getDateString(new Date(timestamp)));

                      // Build message
                      // message.put("eventName", eventName);
                      // message.put("eventData", eventData);

                      if (fusedLocationClient != null
                              && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PermissionChecker.PERMISSION_GRANTED) {
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
              }
          }
    }

    private String getTransitionTypeLiteral (int transitionType) {
        switch (transitionType) {
            case ActivityTransition.ACTIVITY_TRANSITION_ENTER:
                return "ACTIVITY_TRANSITION_ENTER";
            case ActivityTransition.ACTIVITY_TRANSITION_EXIT:
                return "ACTIVITY_TRANSITION_EXIT";
            default:
                return "UNKNOWN";

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
            default:
                detectedActivities.put("UNKNOWN");
        }

        return detectedActivities;
    }

    private String getDateString(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        return formatter.format(date);
    }

    private long convertNsToMs(long ns) {
        return ns/1000000;
    }

    public void triggerJsEvent(JSONObject eventData) {
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