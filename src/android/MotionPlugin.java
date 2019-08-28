package es.simbiosys.cordova.plugin.motion;

import android.hardware.Sensor;
import android.util.Log;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import es.simbiosys.cordova.plugin.motion.sensors.Accelerometer;
import es.simbiosys.cordova.plugin.motion.sensors.ActivityDetection;
import es.simbiosys.cordova.plugin.motion.sensors.LinearAccelerometer;
import es.simbiosys.cordova.plugin.motion.sensors.SignificantMotion;

public class MotionPlugin extends CordovaPlugin {

  private static final String TAG = "MotionPlugin";
  private static final int ACTIVITY_DETECTION_ID = 0;

  private Accelerometer accelerometerSensor;
  private SignificantMotion significantMotionSensor;
  private LinearAccelerometer linearAccelerometerSensor;
  private ActivityDetection activityDetectionSensor;

  @Override
  public void initialize(CordovaInterface cordova, CordovaWebView webView) {
    super.initialize(cordova, webView);

    // Initialize motion sensors
    accelerometerSensor = new Accelerometer(cordova.getContext());
    significantMotionSensor = new SignificantMotion(cordova.getContext());
    linearAccelerometerSensor = new LinearAccelerometer(cordova.getContext());

    // Initialize activity detection and register receiver
    activityDetectionSensor = new ActivityDetection(cordova.getContext());
  }

  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
    if (action.equals("subscribeToNativeEvents")) {
      this.setSensorEventsCallbackContext(callbackContext);

      // Build event data
      JSONObject message = new JSONObject();
      message.putOpt("eventName", "subscribedOk");

      // Return OK result
      PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, message);
      pluginResult.setKeepCallback(true);
      callbackContext.sendPluginResult(pluginResult);

      return true;
    } else if (action.equals("startSensorCapture")) {
      int sensorType = args.optInt(0);
      this.startSensorCapture(callbackContext, sensorType);

      return true;
    } else if (action.equals("stopSensorCapture")) {
      int sensorType = args.optInt(0);
      this.stopSensorCapture(callbackContext, sensorType);

      return true;
    } else if (action.equals("enableTriggerAfterEvent")) {
      int sensorType = args.optInt(0);
      if (sensorType != Sensor.TYPE_SIGNIFICANT_MOTION) {
        callbackContext.error("Method unavailable for this sensor");
        return true;
      }

      this.significantMotionSensor.setEnableAfterEvent(true);
      callbackContext.success("Trigger will be enabled automatically after event is triggered");

      return true;
    } else if (action.equals("disableTriggerAfterEvent")) {
      int sensorType = args.optInt(0);
      if (sensorType != Sensor.TYPE_SIGNIFICANT_MOTION) {
        callbackContext.error("Method unavailable for this sensor");
        return true;
      }

      this.significantMotionSensor.setEnableAfterEvent(false);
      callbackContext.success("Trigger will be disabled after event is triggered");

      return true;
    }

    return false;
  }

  private void setSensorEventsCallbackContext(CallbackContext callbackContext) {
    // Set events callback context on each sensor to communicate Java with Javascript
    this.accelerometerSensor.setEventsCallbackContext(callbackContext);
    this.significantMotionSensor.setEventsCallbackContext(callbackContext);
    this.linearAccelerometerSensor.setEventsCallbackContext(callbackContext);
    this.activityDetectionSensor.setEventsCallbackContext(callbackContext);
  }

  private void startSensorCapture(CallbackContext callbackContext, int sensorType) {
    switch (sensorType) {
      case Sensor.TYPE_ACCELEROMETER:
        if (accelerometerSensor == null) {
          callbackContext.error("No accelerometer sensor");
          return;
        }

        accelerometerSensor.startCapture();
        callbackContext.success("Accelerometer event capture started");
        break;
      case Sensor.TYPE_SIGNIFICANT_MOTION:
        if (significantMotionSensor == null) {
          callbackContext.error("No accelerometer sensor");
          return;
        }

        significantMotionSensor.startCapture();
        callbackContext.success("SignificantMotion trigger enabled");
        break;
      case Sensor.TYPE_LINEAR_ACCELERATION:
        if (linearAccelerometerSensor == null) {
          callbackContext.error("No linear accelerometer sensor");
          return;
        }

        linearAccelerometerSensor.startCapture();
        callbackContext.success("Linear accelerometer event capture started");
        break;
      case ACTIVITY_DETECTION_ID:
        if (activityDetectionSensor == null) {
          callbackContext.error("No activity detection sensor");
          return;
        }

        cordova.getThreadPool().execute(new Runnable() {
          @Override
          public void run() {
            activityDetectionSensor.startCapture(callbackContext);
          }
        });
        break;
      default:
        callbackContext.error("Unknown sensor");
    }
  }

  private void stopSensorCapture(CallbackContext callbackContext, int sensorType) {
    switch (sensorType) {
      case Sensor.TYPE_ACCELEROMETER:
        if (accelerometerSensor == null) {
          callbackContext.error("No accelerometer sensor");
          return;
        }

        accelerometerSensor.stopCapture();
        callbackContext.success("Accelerometer event capture stopped");
        break;
      case Sensor.TYPE_SIGNIFICANT_MOTION:
        if (significantMotionSensor == null) {
          callbackContext.error("No accelerometer sensor");
          return;
        }

        significantMotionSensor.stopCapture();
        callbackContext.success("SignificantMotion trigger disabled");
        break;
      case Sensor.TYPE_LINEAR_ACCELERATION:
        if (linearAccelerometerSensor == null) {
          callbackContext.error("No linear accelerometer sensor");
          return;
        }

        linearAccelerometerSensor.stopCapture();
        callbackContext.success("Linear accelerometer event capture stopped");
        break;
      case ACTIVITY_DETECTION_ID:
        if (activityDetectionSensor == null) {
          callbackContext.error("No activity detection sensor");
          return;
        }

        cordova.getThreadPool().execute(new Runnable() {
          @Override
          public void run() {
            activityDetectionSensor.stopCapture(callbackContext);
          }
        });
        break;
      default:
        callbackContext.error("Unknown sensor");
    }
  }

  @Override
  public void onDestroy() {
    Log.d(TAG, "onDestroy()");

    // Stop activity transition recognition API
    if (activityDetectionSensor != null) {
      activityDetectionSensor.stopCapture(null);
    }

    super.onDestroy();
  }
}