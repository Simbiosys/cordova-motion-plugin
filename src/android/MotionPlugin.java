package es.simbiosys.cordova.plugin.motion;

import android.hardware.Sensor;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import es.simbiosys.cordova.plugin.motion.sensors.Accelerometer;

public class MotionPlugin extends CordovaPlugin {

  private static final String TAG = "MotionPlugin";

  private Accelerometer accelerometerSensor;

  @Override
  public void initialize(CordovaInterface cordova, CordovaWebView webView) {
    super.initialize(cordova, webView);

    // Initialize motion sensors
    accelerometerSensor = new Accelerometer(cordova.getContext());
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
    }

    return false;
  }

  private void setSensorEventsCallbackContext(CallbackContext callbackContext) {
    // Set events callback context on each sensor to communicate Java with Javascript
    this.accelerometerSensor.setEventsCallbackContext(callbackContext);
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
        callbackContext.success("Accelerometer event capture started");
        break;
      default:
        callbackContext.error("Unknown sensor");
    }
  }
}