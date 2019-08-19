import Foundation

@objc(MotionPlugin) class MotionPlugin: CDVPlugin, TriggerJsEventDelegate {
    enum SensorTypes: Int {
        case ACCELEROMETER = 1
    }
    
    var eventsCallbackId: String?
    var accelerometer: Accelerometer?
    
    override func pluginInitialize() {
        accelerometer = Accelerometer()
        accelerometer?.delegate = self
        
        super.pluginInitialize()
    }
    
    @objc(subscribeToNativeEvents:)
    func subscribeToNativeEvents(_ command: CDVInvokedUrlCommand) {
        // Set callback ID to reuse in any event
        self.eventsCallbackId = command.callbackId
        
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
            if self.accelerometer == nil {
                pluginResult = CDVPluginResult(
                    status: CDVCommandStatus_ERROR,
                    messageAs: "No accelerometer sensor"
                )
                break
            }
            
            self.accelerometer?.startCapture()
            
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
            if self.accelerometer == nil {
                pluginResult = CDVPluginResult(
                    status: CDVCommandStatus_ERROR,
                    messageAs: "No accelerometer sensor"
                )
                break
            }
            
            self.accelerometer?.stopCapture()
            
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
    
    func triggerJsEvent(_ message: [AnyHashable : Any], resultOk: Bool = true) {
        if self.eventsCallbackId == nil {
            print("No callback ID")
            return
        }
        
        let pluginResult = CDVPluginResult(
            status: resultOk ? CDVCommandStatus_OK : CDVCommandStatus_ERROR,
            messageAs: message
        )
        
        // Keep callback
        pluginResult!.setKeepCallbackAs(true)
        
        // Return OK result
        self.commandDelegate.send(
            pluginResult,
            callbackId: self.eventsCallbackId
        )
    }
}
