# cordova-motion-plugin #

This plugin provides methods to get data from device accelerometer and motion sensors. The plugin allows you to:
* Detect activity changes.
* Monitor activity (Android only).
* Get a log of activities performed between two dates (iOS only).
* Get raw data from device accelerometer along X,Y,Z axis.

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

#### Activity change event ####
Once capture is started, the plugin will trigger an `onActivityDetection` event every time an activity change is detected.

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

#### Stop capturing events ####
To stop capturing activity change events the `stopActivityDetectionCapture` must be called.

```
motionPlugin.stopActivityDetectionCapture(function (pluginResponse) {
    // Event capture stopped successfully
  }, function (error) {
    // Something went wrong
  })
```

## <a id="activitytypes"></a> Detected activity identifiers ##
The `detectedActivities` array obtained in activity detection and recognition events contains string identifiers of the detected activities. These identifiers are:
* *STILL*. The device is still (not moving).
* *ON_FOOT*. The device is on a user who is walking or running.
* *TITLING*. 	The device angle relative to gravity changed significantly.
* *WALKING*. The device is on a user who is walking.
* *RUNNING*. The device is on a user who is running.
* *ON_BICYCLE*. 	The device is on a bicycle.
* *IN_VEHICLE*. The device is in a vehicle, such as a car.