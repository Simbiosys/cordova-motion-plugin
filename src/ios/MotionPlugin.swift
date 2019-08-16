import Foundation

@objc(MotionPlugin) class MotionPlugin: CDVPlugin {
    var eventListenerCallbackId: String?
    
    enum SensorTypes: Int {
        case ACCELEROMETER = 1
    }
    
    @objc(subscribeToNativeEvents:)
    func subscribeToNativeEvents(_ command: CDVInvokedUrlCommand) {
        // Set callback ID in class property to reuse in any event
        self.eventListenerCallbackId = command.callbackId
        
        // Set plugin result
        let message: [AnyHashable : Any] = [
            "eventName": "subscribedOk"
        ]
        let pluginResult = CDVPluginResult(
            status: CDVCommandStatus_OK,
            messageAs: message
        )
        
        // Keep callback
        pluginResult!.setKeepCallbackAs(true)
        
        // Return OK result
        self.commandDelegate!.send(
            pluginResult,
            callbackId: command.callbackId
        )
    }
    
    @objc(startSensorCapture:)
    func startSensorCapture(_ command: CDVInvokedUrlCommand) {
        let sensorType = command.arguments[0] as? Int ?? -1
        
        var pluginResult: CDVPluginResult?
        
        switch sensorType {
        case SensorTypes.ACCELEROMETER.rawValue:
            pluginResult = CDVPluginResult(
                status: CDVCommandStatus_OK,
                messageAs: "Accelerometer event capture started"
            )
            break
        default:
            pluginResult = CDVPluginResult(
                status: CDVCommandStatus_ERROR,
                messageAs: "Unknown sensor"
            )
        }
        
        self.commandDelegate!.send(
            pluginResult,
            callbackId: command.callbackId
        )
    }
    
    @objc(stopSensorCapture:)
    func stopSensorCapture(_ command: CDVInvokedUrlCommand) {
        let sensorType = command.arguments[0] as? Int ?? -1
        
        var pluginResult: CDVPluginResult?
        
        switch sensorType {
        case SensorTypes.ACCELEROMETER.rawValue:
            pluginResult = CDVPluginResult(
                status: CDVCommandStatus_OK,
                messageAs: "Accelerometer event capture stopped"
            )
            break
        default:
            pluginResult = CDVPluginResult(
                status: CDVCommandStatus_ERROR,
                messageAs: "Unknown sensor"
            )
        }
        
        self.commandDelegate!.send(
            pluginResult,
            callbackId: command.callbackId
        )
    }
}