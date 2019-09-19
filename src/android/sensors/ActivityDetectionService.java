package es.simbiosys.cordova.plugin.motion.sensors;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

public class ActivityDetectionService extends IntentService {

    private static final String TAG = "ActivityDetection";
    private static final String eventName = "onActivityDetection";

    public ActivityDetectionService () {
        super("ActivityDetectionService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent");

        if(ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            DetectedActivity activity = result.getMostProbableActivity();
            Log.d(TAG, "activity type: " + activity.getType());
            Log.d(TAG, "confidence: " + activity.getConfidence());

            /* for (DetectedActivity activity : result.getProbableActivities()) {
                Log.d(TAG, "activity type: " + activity.getType());
                Log.d(TAG, "confidence: " + activity.getConfidence());
            } */
        }
    }
}
