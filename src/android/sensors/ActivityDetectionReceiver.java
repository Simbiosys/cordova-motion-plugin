package es.simbiosys.cordova.plugin.motion.sensors;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionResult;
import com.google.android.gms.location.DetectedActivity;

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

    @Override
    public void onReceive(Context context, Intent intent) {
          Log.d(TAG, "onReceive");

          if (ActivityTransitionResult.hasResult(intent)) {
              ActivityTransitionResult result = ActivityTransitionResult.extractResult(intent);
              for (ActivityTransitionEvent event : result.getTransitionEvents()) {
                  Date currentDate = new Date();
                  JSONObject message = new JSONObject();
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
                      message.put("eventName", eventName);
                      message.put("eventData", eventData);

                      triggerJsEvent(message);
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

    public void triggerJsEvent(JSONObject message) {
        if (eventsCallbackContext != null) {
            PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, message);
            pluginResult.setKeepCallback(true);
            eventsCallbackContext.sendPluginResult(pluginResult);
        }
    }
}