import SendBirdCalls
@objc(RNSendBirdCallsHelper)
public class RNSendBirdCallsHelper: NSObject {
    @objc public static func applicationWillTerminate(application: UIApplication?) -> Void {
        print("sayHello2")

     // This method will be called when the app is forcefully terminated.
     // End all ongoing calls in this method.
     let callManager = CXCallManager.shared
     let ongoingCalls = callManager.currentCalls.compactMap { SendBirdCall.getCall(forUUID: $0.uuid) }

     ongoingCalls.forEach { directCall in
         // Sendbird Calls: End call
         directCall.end()

         // CallKit: Request End transaction
         callManager.endCXCall(directCall)

         // CallKit: Report End if uuid is valid
         if let uuid = directCall.callUUID {
             callManager.endCall(for: uuid, endedAt: Date(), reason: .none)
         }
     }
     // However, because iOS gives a limited time to perform remaining tasks,
     // There might be some calls failed to be ended
     // In this case, I recommend that you register local notification to notify the unterminated calls.
    }
}
