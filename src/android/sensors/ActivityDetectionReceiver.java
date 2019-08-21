package es.simbiosys.cordova.plugin.motion.sensors;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionResult;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.json.JSONException;
import org.json.JSONObject;

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
                  Log.d(TAG, "activity type: " + event.getActivityType());
                  Log.d(TAG, "transition type: " + event.getTransitionType());

                  JSONObject message = new JSONObject();
                  JSONObject eventData = new JSONObject();

                  try {
                      // Build event data
                      eventData.put("activityType", event.getActivityType());
                      eventData.put("transitionType", event.getTransitionType());

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

    public void triggerJsEvent(JSONObject message) {
        if (eventsCallbackContext != null) {
            PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, message);
            pluginResult.setKeepCallback(true);
            eventsCallbackContext.sendPluginResult(pluginResult);
        }
    }
}