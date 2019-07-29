const cordova = require('cordova')
const exec = require('cordova/exec')
const channel = require('cordova/channel')

function MotionPlugin () {
  this.sensorTypes = {
    ACCELEROMETER: 1
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

MotionPlugin.prototype.startAccelerometerCapture = function (successCallback, errorCallback) {
  exec(successCallback, errorCallback, 'MotionPlugin', 'startSensorCapture', [this.sensorTypes.ACCELEROMETER])
}

MotionPlugin.prototype.stopAccelerometerCapture = function (successCallback, errorCallback) {
  exec(successCallback, errorCallback, 'MotionPlugin', 'stopSensorCapture', [this.sensorTypes.ACCELEROMETER])
}

module.exports = new MotionPlugin()
