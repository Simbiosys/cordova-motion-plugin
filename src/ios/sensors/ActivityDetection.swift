import CoreMotion

class ActivityDetection {
    let motionActivityManager: CMMotionActivityManager
    var eventsCallbackId: String?
    let queue: OperationQueue
    var delegate: TriggerJsEventDelegate?
    
    let eventName = "onActivityDetection"
    
    init() {
        motionActivityManager = CMMotionActivityManager()
        eventsCallbackId = nil
        queue = OperationQueue()
    }
    
    func setEventsCallbackId(_ callbackId: String) {
        self.eventsCallbackId = callbackId
    }
    
    func startCapture() -> (status: CDVCommandStatus, message: String) {
        // Check whether motion data is available on the current device
        if !CMMotionActivityManager.isActivityAvailable() {
            return (CDVCommandStatus_ERROR, "Motion data unavailable")
        }
        
        self.motionActivityManager.startActivityUpdates(to: self.queue, withHandler: self.motionActivityHandler)
        
        return (CDVCommandStatus_OK, "Activity detection started successfully")
    }
    
    func stopCapture() -> (status: CDVCommandStatus, message: String) {
        motionActivityManager.stopActivityUpdates()
        
        return (CDVCommandStatus_OK, "Activity detection stopped successfully")
    }
    
    func motionActivityHandler(activity: Optional<CMMotionActivity>) {
        /* print("IN_VEHICLE: \(activity?.automotive ?? false)")
        print("ON_BICYCLE: \(activity?.cycling ?? false)")
        print("RUNNING: \(activity?.running ?? false)")
        print("WALKING: \(activity?.walking ?? false)")
        print("STILL: \(activity?.stationary ?? false)") */
        
        var detectedActivities: [String] = []
        
        if activity!.automotive {
            detectedActivities.append("IN_VEHICLE")
        }
        if activity!.cycling {
            detectedActivities.append("ON_BICYCLE")
        }
        if activity!.running {
            detectedActivities.append("RUNNING")
        }
        if activity!.walking {
            detectedActivities.append("WALKING")
        }
        if activity!.stationary {
            detectedActivities.append("STILL")
        }
        
        let message: [AnyHashable : Any] = [
            "eventName": self.eventName,
            "eventData": [
                "detectedActivities": detectedActivities,
                "timestamp": self.getTimestamp()
            ]
        ]
        // Trigger event
        self.triggerJsEvent(message)
    }
    
    func getTimestamp() -> String {
        let now = Date()
        let formatter = DateFormatter()
        formatter.timeZone = TimeZone.current
        formatter.dateFormat = "yyyy-MM-dd HH:mm"
        
        let dateString = formatter.string(from: now)
        return dateString
    }
    
    func triggerJsEvent(_ message: [AnyHashable : Any], resultOk: Bool = true) {
        delegate?.triggerJsEvent(message, resultOk: resultOk)
    }
}
