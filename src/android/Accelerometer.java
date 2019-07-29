package es.simbiosys.cordova.plugin.motion;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.json.JSONException;
import org.json.JSONObject;

public class Accelerometer implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

    private CallbackContext eventsCallbackContext;

    private static final String eventName = "onAccelerometerChanged";
    private static final String TAG = "Accelerometer";

    public Accelerometer (Context context) {
        this.eventsCallbackContext = null;
        this.mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        this.mAccelerometer = this.mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    public void setEventsCallbackContext (CallbackContext callbackContext) {
        this.eventsCallbackContext = callbackContext;
    }

    public void startCapture() {
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void stopCapture() {
        mSensorManager.unregisterListener(this);
    }

    private void triggerJsEvent(JSONObject message) {
        if (eventsCallbackContext != null) {
            PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, message);
            pluginResult.setKeepCallback(true);
            eventsCallbackContext.sendPluginResult(pluginResult);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // Return if event is not trigger by accelerometer
        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER)
            return;

        JSONObject message = new JSONObject();
        JSONObject eventData = new JSONObject();

        try {
            // Build event data
            eventData.put("x", event.values[0]);
            eventData.put("y", event.values[1]);
            eventData.put("z", event.values[2]);

            // Build message
            message.put("eventName", eventName);
            message.put("eventData", eventData);

            triggerJsEvent(message);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}