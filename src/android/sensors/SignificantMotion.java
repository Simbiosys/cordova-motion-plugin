package es.simbiosys.cordova.plugin.motion.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.TriggerEvent;
import android.hardware.TriggerEventListener;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class SignificantMotion extends BaseSensor {

    private Sensor mSignificantMotion;
    private TriggerEventListener mTriggerEventListener;
    private Boolean enableAfterEvent;

    private static final String eventName = "onSignificantMotion";
    private static final String TAG = "SignificantMotion";

    public SignificantMotion (Context context) {
        super(context);

        this.mSignificantMotion = this.mSensorManager.getDefaultSensor(Sensor.TYPE_SIGNIFICANT_MOTION);
        this.mTriggerEventListener = new TriggerEventListener() {
            @Override
            public void onTrigger(TriggerEvent event) {
                // Return if event is not trigger by significant motion
                if (event.sensor.getType() != Sensor.TYPE_SIGNIFICANT_MOTION)
                    return;

                JSONObject message = new JSONObject();
                JSONObject eventData = new JSONObject();

                try {
                    // Build event data
                    eventData.put("values", event.values[0]);

                    // Build message
                    message.put("eventName", eventName);
                    message.put("eventData", eventData);

                    triggerJsEvent(message);

                    if (enableAfterEvent) {
                        // Enable trigger again
                        startCapture();
                    }
                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        };

        this.enableAfterEvent = false; // By default, not enable trigger again after event
    }

    @Override
    public void startCapture() {
        this.mSensorManager.requestTriggerSensor(this.mTriggerEventListener, this.mSignificantMotion);
    }

    @Override
    public void stopCapture() {
        super.stopCapture();

        this.mSensorManager.cancelTriggerSensor(mTriggerEventListener, mSignificantMotion);
    }

    public void setEnableAfterEvent (Boolean enableAfterEvent) {
        this.enableAfterEvent = enableAfterEvent;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
