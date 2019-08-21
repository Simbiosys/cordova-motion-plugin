import CoreMotion

class Accelerometer {
    let motionManager: CMMotionManager
    let timeInterval: TimeInterval
    let queue: OperationQueue
    var eventsCallbackId: String?
    var delegate: TriggerJsEventDelegate?
    
    let eventName = "onAccelerometerChanged"
    
    init() {
        motionManager = CMMotionManager()
        timeInterval = 1.0 / 60.0 // 60Hz
        queue = OperationQueue()
        eventsCallbackId = nil
    }
    
    func setEventsCallbackId(_ callbackId: String) {
        self.eventsCallbackId = callbackId
    }
    
    func startCapture() -> (status: CDVCommandStatus, message: String) {
        // Check whether the accelerometer hardware is available
        if !self.motionManager.isAccelerometerAvailable {
            return (CDVCommandStatus_ERROR, "Accelerometer unavailable")
        }
        
        self.motionManager.accelerometerUpdateInterval = self.timeInterval
        self.motionManager.startAccelerometerUpdates(to: self.queue, withHandler: self.accelerometerUpdatesHandler)
        
        return (CDVCommandStatus_OK, "Accelerometer event capture started")
    }
    
    func stopCapture() -> (status: CDVCommandStatus, message: String) {
        self.queue.cancelAllOperations()
        self.motionManager.stopAccelerometerUpdates()
        
        return (CDVCommandStatus_OK, "Accelerometer event capture stopped")
    }
    
    func accelerometerUpdatesHandler (accelerometerData: Optional<CMAccelerometerData>, error: Optional<Error>) {
        if (error != nil) {
            print("Error: \(error!.localizedDescription)")
            
            // TODO: stop sensor capture and cancel queue
            return
        }
        
        // Build message with acceleromenter data
        let message: [AnyHashable : Any] = [
            "eventName": self.eventName,
            "eventData": [
                "x": accelerometerData?.acceleration.x,
                "y": accelerometerData?.acceleration.y,
                "z": accelerometerData?.acceleration.z
            ]
        ]
        print(message)
        // Trigger event
        self.triggerJsEvent(message)
    }
    
    func triggerJsEvent(_ message: [AnyHashable : Any], resultOk: Bool = true) {
        delegate?.triggerJsEvent(message, resultOk: resultOk)
    }
}
