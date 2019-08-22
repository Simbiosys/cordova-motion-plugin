import CoreMotion

class ActivityDetection {
    let motionActivityManager: CMMotionActivityManager
    var eventsCallbackId: String?
    let queue: OperationQueue
    var delegate: TriggerJsEventDelegate?
    
    let eventName = "onActivityDetection"
    let queryEventName = "onActivityQueryCompleted"
    
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
    
    func motionActivityQuery(fromDate: Date, toDate: Date) -> (status: CDVCommandStatus, message: String) {
        motionActivityManager.queryActivityStarting(from: fromDate, to: toDate, to: self.queue, withHandler: self.motionActivityQueryHandler)
        
        return (CDVCommandStatus_OK, "Activity detection query launched successfully")
    }
    
    func motionActivityHandler(activity: Optional<CMMotionActivity>) {
        /* print("IN_VEHICLE: \(activity?.automotive ?? false)")
        print("ON_BICYCLE: \(activity?.cycling ?? false)")
        print("RUNNING: \(activity?.running ?? false)")
        print("WALKING: \(activity?.walking ?? false)")
        print("STILL: \(activity?.stationary ?? false)") */
        
        let message: [AnyHashable : Any] = [
            "eventName": self.eventName,
            "eventData": [
                "detectedActivities": self.getDetectedActivities(fromActivity: activity!),
                "timestamp": self.getDateString(from: activity!.startDate),
                "confidence": self.getConfidenceString(from: activity!.confidence)
            ]
        ]
        // Trigger event
        self.triggerJsEvent(message)
    }
    
    func motionActivityQueryHandler(activities: Optional<Array<CMMotionActivity>>, error: Optional<Error>) {
        if error != nil {
            print("Error: \(error!.localizedDescription)")
            
            // TODO: generate an event with the error
            return
        }
        
        // Build activity log array
        var activityLog: [String: Array<Any>] = [
            "activities": Array()
        ]
        for activity in activities! {
            let oneActivityLog: [String: Any] = [
                "detectedActivities": self.getDetectedActivities(fromActivity: activity),
                "timestamp": self.getDateString(from: activity.startDate),
                "confidence": self.getConfidenceString(from: activity.confidence)
            ]
            activityLog["activities"]?.append(oneActivityLog)
        }
        
        // Build plugin message and trigger event
        let message: [AnyHashable : Any] = [
            "eventName": self.queryEventName,
            "eventData": activityLog
        ]
        self.triggerJsEvent(message)
    }
    
    func getDetectedActivities(fromActivity: CMMotionActivity) -> ([String]) {
        var detectedActivities: [String] = []
        
        if fromActivity.automotive {
            detectedActivities.append("IN_VEHICLE")
        }
        if fromActivity.cycling {
            detectedActivities.append("ON_BICYCLE")
        }
        if fromActivity.running {
            detectedActivities.append("RUNNING")
        }
        if fromActivity.walking {
            detectedActivities.append("WALKING")
        }
        if fromActivity.stationary {
            detectedActivities.append("STILL")
        }
        if fromActivity.unknown {
            detectedActivities.append("UNKNOWN")
        }
        
        return detectedActivities
    }
    
    func getDateString(from: Date) -> String {
        let formatter = DateFormatter()
        formatter.timeZone = TimeZone.current
        formatter.dateFormat = "yyyy-MM-dd HH:mm:ss"
        
        let dateString = formatter.string(from: from)
        return dateString
    }
    
    func getConfidenceString(from: CMMotionActivityConfidence) -> String {
        switch from {
        case CMMotionActivityConfidence.high:
            return "HIGH"
        case CMMotionActivityConfidence.medium:
            return "MEDIUM"
        case CMMotionActivityConfidence.low:
            return "LOW"
        default:
            return "UNKNOWN"
        }
    }
    
    func triggerJsEvent(_ message: [AnyHashable : Any], resultOk: Bool = true) {
        delegate?.triggerJsEvent(message, resultOk: resultOk)
    }
}
