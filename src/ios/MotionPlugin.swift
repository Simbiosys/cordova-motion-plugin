import Foundation

@objc(MotionPlugin) class MotionPlugin: CDVPlugin {
    var eventListenerCallbackId : String?
    
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
}
