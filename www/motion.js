const cordova = require('cordova')
const exec = require('cordova/exec')
const channel = require('cordova/channel')

function MotionPlugin () {}

// Subscribe to native code events on plugin initialization
channel.onCordovaReady.subscribe(function () {
  // Send an 'exec' to native code
  exec(function (event) {
    cordova.fireDocumentEvent(event)
  }, function (e) {
    console.log('Error subscribing to native events')
    console.log(e)
  }, 'MotionPlugin', 'subscribeToNativeEvents', [])
})

module.exports = new MotionPlugin()
