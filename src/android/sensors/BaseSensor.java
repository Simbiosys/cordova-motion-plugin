package es.simbiosys.cordova.plugin.motion.sensors;

import android.content.Context;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.json.JSONObject;

public abstract class BaseSensor implements SensorEventListener {

    protected SensorManager mSensorManager;
    protected CallbackContext eventsCallbackContext;

    public BaseSensor (Context context) {
        this.eventsCallbackContext = null;
        this.mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }

    public void setEventsCallbackContext (CallbackContext callbackContext) {
        this.eventsCallbackContext = callbackContext;
    }

    public abstract void startCapture();

    public void stopCapture() {
        this.mSensorManager.unregisterListener(this);
    }

    protected void triggerJsEvent(JSONObject message) {
        if (this.eventsCallbackContext != null) {
            PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, message);
            pluginResult.setKeepCallback(true);
            eventsCallbackContext.sendPluginResult(pluginResult);
        }
    }
}
