# cordova-motion-plugin #

This plugin provides methods to get data from device accelerometer and motion sensors. The plugin allows you to:
* Detect activity changes.
* Monitor activity (Android only).
* Get a log of activities performed between two dates (iOS only).
* Get raw data from device accelerometer along X,Y,Z axis.

## WARNING: ##
:construction: Documentation under construction :building_construction:

## Installation ##
```
cordova plugin add https://github.com/Simbiosys/cordova-motion-plugin
```

## Initialization ##
The plugin defines a global `motionPlugin` object which has all the available methods that will be described in this README. Although the object is in the global scope, it is not available until after the `deviceready` event.

This plugin susbscribes automatically to events triggered by the different motion sensors of the device. One way to check that the plugin is correctly initialized is to add a listener to the `subscribedOk` event at startup.
```
document.addEventListener('subscribedOk', function () {
  console.log('subscribedOk')
}, false)
```
_subscribedOk_ message printed in the JS console means that the plugin is ready to be used.


## Activity Detection ##
The plugin is able to trigger events when user's activity changes. For example, if the user is walking and get in a car and starts driving, the plugin will trigger an event informing about this change.

#### Supported platforms ####
* Android
* iOS

#### Start capturing events ####
To start capturing activity change events the `startActivityDetectionCapture` must be called.

```
motionPlugin.startActivityDetectionCapture(function (pluginResponse) {
    // Event capture started successfully
  }, function (error) {
    // Something went wrong
  })
```

#### <a id="activitydetectionevent"></a> Activity change event ####
Once capture is started, the plugin will trigger an `onActivityDetection` event each time an activity change is detected.

```
document.addEventListener('onActivityDetection', function (eventData) {
  console.log('onActivityDetection')

  // Do something with eventData
})
```

The `eventData` object is a bit different depending on the platform. Consits of a JSON object with the following properties:
* `detectedActivities` (Android & iOS). An array containing the [detected activities](#activitytypes).
  * On Android the array will contain only one element, the activity that the user stops or starts doing, depending on the value of the `transitionType` property.
  * On iOS the array can contain several activities but commonly will contain only one. This activity will be the activity the user starts.

* `transitionType` (Android only). A string with value *ACTIVITY_TRANSITION_ENTER* if the user starts the activity or *ACTIVITY_TRANSITION_EXIT* if the stops doing it.
* `confidence` (iOS only). The confidence in the assessment of the detected activity. Its value can be *LOW*, *MEDIUM* or *HIGH*.
* `timestamp` (Android & iOS). Timestamp indicating when the event is detected. Format _YYYY-MM-DD HH:mm:ss_.
* `latitude` (Android, optional). Latitude where the device is when the activity change is detected. See [Activity events with location](#activityeventswithlocation) section to know how to get this property within event data.
* `longitude` (Android, optional). Longitude where the device is when the activity change is detected. See [Activity events with location](#activityeventswithlocation) section to know how to get this property within event data.

#### Stop capturing events ####
To stop capturing activity change events the `stopActivityDetectionCapture` must be called.

```
motionPlugin.stopActivityDetectionCapture(function (pluginResponse) {
    // Event capture stopped successfully
  }, function (error) {
    // Something went wrong
  })
```


## <a id="activitytypes"></a> Activity identifiers ##
The `detectedActivities` array obtained in activity detection and recognition events contains string identifiers of the detected activities. These identifiers are:
* *STILL*. The device is still (not moving).
* *ON_FOOT*. The device is on a user who is walking or running.
* *TITLING*. 	The device angle relative to gravity changed significantly.
* *WALKING*. The device is on a user who is walking.
* *RUNNING*. The device is on a user who is running.
* *ON_BICYCLE*. 	The device is on a bicycle.
* *IN_VEHICLE*. The device is in a vehicle, such as a car.


## Significant motion detection ##
Some Android devices are capable of trigger an event each time significant motion is detected. A significant motion is a motion that might lead to a change in the user's location; for example walking, biking, or sitting in a moving car. Once the sensor triggers the event, it disables itself, so it must be enabled again to detect the next significatn motion.

#### Supported platforms ####
* Android

#### Enable significant motion sensor ####
To enable significant motion sensor, method `enableSignificantMotionTrigger` must be called.
```
motionPlugin.enableSignificantMotionTrigger(function (pluginResponse) {
  // Motion sensor enabled successfully
}, function (error) {
  // Something went wrong
})
```

#### Significant motion event ####
When device detects a significant motion, this plugin will trigger an `onSignificantMotion` event.
```
document.addEventListener('onSignificantMotion', function () {
  console.log('onSignificantMotion')

  // Do work
}, false)
```
There is no information about the nature of the significant motion, the device only triggers the event when detect one.

#### Disable significant motion sensor ####
To disable significant motion sensor, method `disableSignificantMotionTrigger` must be called.
```
motionPlugin.disableSignificantMotionTrigger(function (pluginResponse) {
  // Motion sensor disabled successfully
}, function (error) {
  // Something went wrong
})
```

#### Re-enable significant motion sensor after event ####
The sensor disables itself after detect a significant motion and trigger the event. But this plugin allows to enable it again automatically. To do this, method `enableTriggerAfterEvent` must be called before enabling the sensor. You can call `enableTriggerAfterEvent` and in the success callback enable sensor through `enableSignificantMotionTrigger`.

```
motionPlugin.enableSignificantMotionTrigger(function (pluginResponse) {
  // Significant motion sensor will be re-enabled automatically after event

  // Enable sensor
  motionPlugin.enableTriggerAfterEvent(function (pluginResponse) {
    // Motion sensor enabled successfully
  }, function (error) {
    // Something went wrong
  })
}, function (error) {
  // Something went wrong
})
```

This _automatic re-enabling_ can be undone at any time by calling the `disableTriggerAfterEvent`.
```
motionPlugin.disableTriggerAfterEvent(function (pluginResponse) {
  // Significant motion sensor will disable it self after trigger event
}, function (error) {
  // Something went wrong
})
```


## Monitoring activity ##
On Android is possible to retrieve activity information periodically. The plugin _asks_ to the device which kind of activity is performing at intervals that can be specified, and triggers a JS event with the response.

#### Supported platforms ####
* Android

#### Start activity monitoring ####
To start activity monitoring method `startActivityDetectionPolling` must be called.
```
motionPlugin.startActivityDetectionPolling(function (pluginResponse) {
  // Activity monitoring started successfully
}, function (error) {
  // Something went wrong
})
```

#### Set polling interval ####
By default, activity recognition results will be delivered at the fastest possible rate. It is recommended to set the desired time between activity detections. Larger values will result in fewer activity detections while improving battery life.

To set this interval, method `setDetectionIntervalMillis` must be called, passing the interval value in milliseconds. This interval **will only take effect** if method `setDetectionIntervalMillis` is called before start activity monitoring. You can call `setDetectionIntervalMillis` and in the success callback enable activity monitoring through `startActivityDetectionPolling`.
```
motionPlugin.setDetectionIntervalMillis(
  30000,
  function (pluginResponse) {
    // Interval set to 30 seconds

    // Start capture
    motionPlugin.startActivityDetectionPolling(function (pluginResponse) {
      // Activity monitoring started successfully
    }, function (error) {
      // Something went wrong while starting activity monitoring
    })
},
function (error) {
  // Something went wrong while setting interval
}
```

#### <a id="activityrecognitionevent"></a> Activity recognition event ####
Once capture is started, the plugin will trigger an `onActivityRecognition` event each time the device responds with the recognized activity.

```
document.addEventListener('onActivityRecognition', function (eventData) {
  console.log('onActivityDetection')

  // Do something with eventData
})
```

The `eventData` object consits of a JSON object with the following properties:
* `detectedActivities`. An array containing the [recognized activity](#activitytypes). It will always be an array of only one element.
* `confidence`. The confidence in the assessment of the detected activity. Number between 0 and 100.
* `timestamp`. Timestamp indicating when the activity is recognized. Format _YYYY-MM-DD HH:mm:ss_.
* `latitude` (Optional). Latitude where the device is when the activity recognition event is triggered. See [Activity events with location](#activityeventswithlocation) section to know how to get this property within event data.
* `longitude` (Optional). Longitude where the device is when the activity recognition event is triggered. See [Activity events with location](#activityeventswithlocation) section to know how to get this property within event data.

#### Stop activity monitoring ####
To stop activity monitoring, method `stopActivityDetectionPolling` must be called.
```
motionPlugin.stopActivityDetectionPolling(function (pluginResponse) {
  // Activity monitoring stopped successfully
}, function (error) {
  // Something went wrong
})
```


## <a id="activityeventswithlocation"></a> Activity events with location ##
Is possible to get location coordinates (latitude & longitude) within event data from both activity detection and recognition.

#### Supported platforms ####
* Android

#### Set location coordinates within event data ####
To make the plugin return latitude and longitude within the detection and recognition activity data, method `setActivityDetectionEventsWithLocation` must be called, **always before starting** the activity detection/recognition sensor. I.e. to get location within the event data, first call the `setActivityDetectionEventsWithLocation` method and in the success callback start the activity detection/monitoring.
```
// Example of activity change detection with location initialization
motionPlugin.setActivityDetectionEventsWithLocation(
  true,
  function (pluginResponse) {
    // Start capture
    motionPlugin.startActivityDetectionCapture(function (pluginResponse) {
      // Activity detection started successfully
    }, function (error) {
      // Something went wrong
    })
  }, function (error) {
    // Something went wrong
  })

// Example of activity monitoring with location initialization
motionPlugin.setActivityDetectionEventsWithLocation(
  true,
  function (pluginResponse) {
    // Start capture
    motionPlugin.startActivityDetectionPolling(function (pluginResponse) {
      // Activity monitoring started successfully
    }, function (error) {
      // Something went wrong
    })
  },
  function (error) {
    // Something went wrong
  }
)
```

By doing this, the event data will contain _latitude_ and _longitude_ (as explained in [activity detection event](#activitydetectionevent) and [activity monitoring event](#activityrecognitionevent) sections) properties, with the location of the device at the moment the event is triggered.


## Log of activities in a time period ##
On iOS it is possible to get historical motion data for a specified time period.

#### Supported platforms ####
* iOS

To get a list of activities detected by the device in a time period, method `getActivityLog` must be called, passing start and end dates as first and second parameter respectively. Dates format must be _YYYY-MM-DD HH:mm:ss_.
```
motionPlugin.getActivityLog(
  fromDate,  // Start date in YYYY-MM-DD HH:mm:ss format
  toDate, // End date in YYYY-MM-DD HH:mm:ss format
  function (pluginResponse) {
    // Log query completed successfully
  },
  function (error) {
    // Something went wrong
  })
```

A `onActivityQueryCompleted` event will be triggered with the log data.
```
document.addEventListener('onActivityQueryCompleted', function (eventData) {
  // Do something with eventData
})
```

The event data consists of a JSON with the following properties:
* `activities`. Array with all the activities detected in the specified time period. Each object of the array will contain:
  * `detectedActivities`. An array containing the [detected activities](#activitytypes). The array can contain several activities but commonly will contain only one.
  * `confidence`. The confidence in the assessment of the detected activity. Its value can be *LOW*, *MEDIUM* or *HIGH*.
  * `timestamp`. Timestamp indicating when the event is detected. Format _YYYY-MM-DD HH:mm:ss_.
