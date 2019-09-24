const cordova = require('cordova')
const exec = require('cordova/exec')
const channel = require('cordova/channel')

function MotionPlugin () {
  this.sensorTypes = {
    ACTIVITY_DETECTION: 0,
    ACCELEROMETER: 1,
    LINEAR_ACCELEROMETER: 10,
    SIGNIFICANT_MOTION: 17,
    ACTIVITY_RECOGNITION: 22
  }
}

// Subscribe to native code events on plugin initialization
channel.onCordovaReady.subscribe(function () {
  // Send an 'exec' to native code
  exec(function (event) {
    cordova.fireDocumentEvent(event.eventName, event.eventData)
  }, function (e) {
    console.log('Error subscribing to native events')
    console.log(e)
  }, 'MotionPlugin', 'subscribeToNativeEvents', [])
})

// Accelerometer sensor (Android & iOS)
MotionPlugin.prototype.startAccelerometerCapture = function (successCallback, errorCallback) {
  exec(successCallback, errorCallback, 'MotionPlugin', 'startSensorCapture', [this.sensorTypes.ACCELEROMETER])
}

MotionPlugin.prototype.stopAccelerometerCapture = function (successCallback, errorCallback) {
  exec(successCallback, errorCallback, 'MotionPlugin', 'stopSensorCapture', [this.sensorTypes.ACCELEROMETER])
}

// Significant motion sensor (Android only)
MotionPlugin.prototype.enableSignificantMotionTrigger = function (successCallback, errorCallback) {
  if (cordova.platformId !== 'android') {
    errorCallback('Method only available on Android')
    return
  }
  exec(successCallback, errorCallback, 'MotionPlugin', 'startSensorCapture', [this.sensorTypes.SIGNIFICANT_MOTION])
}

MotionPlugin.prototype.disableSignificantMotionTrigger = function (successCallback, errorCallback) {
  if (cordova.platformId !== 'android') {
    errorCallback('Method only available on Android')
    return
  }
  exec(successCallback, errorCallback, 'MotionPlugin', 'stopSensorCapture', [this.sensorTypes.SIGNIFICANT_MOTION])
}

MotionPlugin.prototype.enableTriggerAfterEvent = function (successCallback, errorCallback) {
  if (cordova.platformId !== 'android') {
    errorCallback('Method only available on Android')
    return
  }
  exec(successCallback, errorCallback, 'MotionPlugin', 'enableTriggerAfterEvent', [this.sensorTypes.SIGNIFICANT_MOTION])
}

MotionPlugin.prototype.disableTriggerAfterEvent = function (successCallback, errorCallback) {
  if (cordova.platformId !== 'android') {
    errorCallback('Method only available on Android')
    return
  }
  exec(successCallback, errorCallback, 'MotionPlugin', 'disableTriggerAfterEvent', [this.sensorTypes.SIGNIFICANT_MOTION])
}

// Linear accelerometer (Android & iOS)
MotionPlugin.prototype.startLinearAccelerometerCapture = function (successCallback, errorCallback) {
  if (cordova.platformId !== 'android') {
    errorCallback('Method only available on Android by the moment. iOS availability comming soon.')
    return
  }
  exec(successCallback, errorCallback, 'MotionPlugin', 'startSensorCapture', [this.sensorTypes.LINEAR_ACCELEROMETER])
}

MotionPlugin.prototype.stopLinearAccelerometerCapture = function (successCallback, errorCallback) {
  if (cordova.platformId !== 'android') {
    errorCallback('Method only available on Android by the moment. iOS availability comming soon.')
    return
  }
  exec(successCallback, errorCallback, 'MotionPlugin', 'stopSensorCapture', [this.sensorTypes.LINEAR_ACCELEROMETER])
}

// Activity detection (Android & iOS)
MotionPlugin.prototype.startActivityDetectionCapture = function (successCallback, errorCallback) {
  exec(successCallback, errorCallback, 'MotionPlugin', 'startSensorCapture', [this.sensorTypes.ACTIVITY_DETECTION])
}

MotionPlugin.prototype.stopActivityDetectionCapture = function (successCallback, errorCallback) {
  exec(successCallback, errorCallback, 'MotionPlugin', 'stopSensorCapture', [this.sensorTypes.ACTIVITY_DETECTION])
}

// iOS only
MotionPlugin.prototype.getActivityLog = function (fromDate, toDate, successCallback, errorCallback) {
  if (cordova.platformId !== 'ios') {
    errorCallback('Method only available on iOS')
    return
  }
  exec(successCallback, errorCallback, 'MotionPlugin', 'getActivityLog', [fromDate, toDate])
}

// Android only
MotionPlugin.prototype.setActivityDetectionEventsWithLocation = function (eventsWithLocation, successCallback, errorCallback) {
  if (cordova.platformId !== 'android') {
    errorCallback('Method only available on Android')
    return
  }
  exec(successCallback, errorCallback, 'MotionPlugin', 'setActivityDetectionEventsWithLocation', [eventsWithLocation])
}

// Activity recognition (Android only)
MotionPlugin.prototype.startActivityDetectionPolling = function (successCallback, errorCallback) {
  if (cordova.platformId !== 'android') {
    errorCallback('Method only available on Android')
    return
  }
  exec(successCallback, errorCallback, 'MotionPlugin', 'startSensorCapture', [this.sensorTypes.ACTIVITY_RECOGNITION])
}

MotionPlugin.prototype.stopActivityDetectionPolling = function (successCallback, errorCallback) {
  if (cordova.platformId !== 'android') {
    errorCallback('Method only available on Android')
    return
  }
  exec(successCallback, errorCallback, 'MotionPlugin', 'stopSensorCapture', [this.sensorTypes.ACTIVITY_RECOGNITION])
}

MotionPlugin.prototype.setDetectionIntervalMillis = function (detectionIntervalMillis, successCallback, errorCallback) {
  if (cordova.platformId !== 'android') {
    errorCallback('Method only available on Android')
    return
  }
  exec(successCallback, errorCallback, 'MotionPlugin', 'setDetectionIntervalMillis', [detectionIntervalMillis])
}

module.exports = new MotionPlugin()
