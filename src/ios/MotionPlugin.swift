import Foundation

@objc(MotionPlugin) class MotionPlugin: CDVPlugin, TriggerJsEventDelegate {
    enum SensorTypes: Int {
        case ACTIVITY_DETECTION = 0
        case ACCELEROMETER = 1
    }
    
    var eventsCallbackId: String?
    var accelerometer: Accelerometer?
    var activityDetection: ActivityDetection?
    
    override func pluginInitialize() {
        accelerometer = Accelerometer()
        accelerometer?.delegate = self
        
        activityDetection = ActivityDetection()
        activityDetection?.delegate = self
        
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
            
            let result = self.accelerometer?.startCapture()
            pluginResult = CDVPluginResult(
                status: result?.status ?? CDVCommandStatus_ERROR,
                messageAs: result?.message
            )
            break
        case SensorTypes.ACTIVITY_DETECTION.rawValue:
            if self.activityDetection == nil {
                pluginResult = CDVPluginResult(
                    status: CDVCommandStatus_ERROR,
                    messageAs: "No activity detection sensor"
                )
                break
            }
            
            let result = activityDetection?.startCapture()
            pluginResult = CDVPluginResult(
                status: result?.status ?? CDVCommandStatus_ERROR,
                messageAs: result?.message
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
            
            let result = self.accelerometer?.stopCapture()
            pluginResult = CDVPluginResult(
                status: result?.status ?? CDVCommandStatus_ERROR,
                messageAs: result?.message
            )
            break
        case SensorTypes.ACTIVITY_DETECTION.rawValue:
            if self.activityDetection == nil {
                pluginResult = CDVPluginResult(
                    status: CDVCommandStatus_ERROR,
                    messageAs: "No activity detection sensor"
                )
                break
            }
            
            let result = activityDetection?.stopCapture()
            pluginResult = CDVPluginResult(
                status: result?.status ?? CDVCommandStatus_ERROR,
                messageAs: result?.message
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
    
    @objc(getActivityLog:)
    func getActivityLog(_ command: CDVInvokedUrlCommand) {
        let fromDateStr = command.arguments[0] as? String ?? nil
        let toDateStr = command .arguments[1] as? String ?? nil
        
        var pluginResult: CDVPluginResult?
        
        if fromDateStr == nil || toDateStr == nil {
            pluginResult = CDVPluginResult(
                status: CDVCommandStatus_ERROR,
                messageAs: "Wrong parameters"
            )
            self.commandDelegate!.send(
                pluginResult,
                callbackId: command.callbackId
            )
            return
        }
        
        let fromDate = self.getDate(from: fromDateStr!)
        let toDate = self.getDate(from: toDateStr!)
        
        if fromDate == nil || toDate == nil {
            pluginResult = CDVPluginResult(
                status: CDVCommandStatus_ERROR,
                messageAs: "Wrong date format"
            )
            self.commandDelegate!.send(
                pluginResult,
                callbackId: command.callbackId
            )
            return
        }
        
        let result = self.activityDetection?.motionActivityQuery(fromDate: fromDate!, toDate: toDate!)
        pluginResult = CDVPluginResult(
            status: result?.status ?? CDVCommandStatus_OK,
            messageAs: result?.message
        )
        self.commandDelegate!.send(
            pluginResult,
            callbackId: command.callbackId
        )
        return
    }
    
    func getDate(from: String) -> Date? {
        let formatter = DateFormatter()
        formatter.timeZone = TimeZone.current
        formatter.dateFormat = "yyyy-MM-dd HH:mm:ss"
        
        return formatter.date(from: from)
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
