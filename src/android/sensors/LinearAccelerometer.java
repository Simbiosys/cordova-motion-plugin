package es.simbiosys.cordova.plugin.motion.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class LinearAccelerometer extends BaseSensor {

    private Sensor mAccelerometer;

    private static final String eventName = "onLinearAccelerometerChanged";
    private static final String TAG = "LinearAccelerometer";

    public LinearAccelerometer (Context context) {
        super(context);

        this.mAccelerometer = this.mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
    }

    @Override
    public void startCapture() {
        this.mSensorManager.registerListener(this, this.mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // Return if event is not trigger by linear accelerometer
        if (event.sensor.getType() != Sensor.TYPE_LINEAR_ACCELERATION)
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
