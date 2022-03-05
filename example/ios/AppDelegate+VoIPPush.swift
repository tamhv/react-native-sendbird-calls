////
////  AppDelegate+VoIPPush.swift
////  example
////
////  Created by Tam Huynh on 2/26/22.
////
//
//
//import UIKit
//import CallKit
//import PushKit
//import SendBirdCalls
//
// 
//
//// MARK: VoIP Push
//extension AppDelegate: PKPushRegistryDelegate {
//    func voipRegistration() {
//        self.voipRegistry = PKPushRegistry(queue: DispatchQueue.main)
//        self.voipRegistry?.delegate = self
//        self.voipRegistry?.desiredPushTypes = [.voIP]
//    }
//    
//    // MARK: - SendBirdCalls - Registering push token.
//    func pushRegistry(_ registry: PKPushRegistry, didUpdate pushCredentials: PKPushCredentials, for type: PKPushType) {
//        UserDefaults.standard.voipPushToken = pushCredentials.token
//        print("Push token is \(pushCredentials.token)")
//        
//        SendBirdCall.registerVoIPPush(token: pushCredentials.token, unique: true) { error in
//            guard error == nil else { return }
//        }
//    }
//    
//    // MARK: - SendBirdCalls - Receive incoming push event
//    func pushRegistry(_ registry: PKPushRegistry, didReceiveIncomingPushWith payload: PKPushPayload, for type: PKPushType) {
//        SendBirdCall.pushRegistry(registry, didReceiveIncomingPushWith: payload, for: type, completionHandler: nil)
//    }
//    
//    // MARK: - SendBirdCalls - Handling incoming call
//    // Please refer to `AppDelegate+SendBirdCallsDelegates.swift` file.
//    func pushRegistry(_ registry: PKPushRegistry, didReceiveIncomingPushWith payload: PKPushPayload, for type: PKPushType, completion: @escaping () -> Void) {
//        SendBirdCall.pushRegistry(registry, didReceiveIncomingPushWith: payload, for: type) { uuid in
//            guard uuid != nil else {
//                let update = CXCallUpdate()
//                update.remoteHandle = CXHandle(type: .generic, value: "invalid")
//                let randomUUID = UUID()
//                
//                CXCallManager.shared.reportIncomingCall(with: randomUUID, update: update) { _ in
//                    CXCallManager.shared.endCall(for: randomUUID, endedAt: Date(), reason: .acceptFailed)
//                }
//                completion()
//                return
//            }
//            
//            completion()
//        }
//    }
//
//}
