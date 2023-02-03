import Foundation
import CallKit
import PushKit
import SendBirdCalls
import UIKit
import React

@objc(RNSendBirdCalls)
class RNSendBirdCalls: RCTEventEmitter, SendBirdCallDelegate, DirectCallDelegate, PKPushRegistryDelegate {

    var queue: DispatchQueue = DispatchQueue(label: "RNSendBirdCalls")
    var voipRegistry: PKPushRegistry?

    static let DirectCallRinging = "SendBirdCallRinging"
    static let DirectCallDidAccept = "DirectCallDidAccept"
    static let DirectCallDidConnect = "DirectCallDidConnect"
    static let DirectCallDidEnd = "DirectCallDidEnd"
    static let DirectCallRemoteAudioSettingsChanged = "DirectCallRemoteAudioSettingsChanged"
    static let DirectCallVideoSettingsChanged = "DirectCallVideoSettingsChanged"

    override init() {
        super.init()
        SendBirdCall.addDelegate(self, identifier: "RNSendBirdCalls")
    }

    override func supportedEvents() -> [String]! {
        return [
            RNSendBirdCalls.DirectCallDidConnect,
            RNSendBirdCalls.DirectCallDidEnd,
            RNSendBirdCalls.DirectCallRemoteAudioSettingsChanged,
            RNSendBirdCalls.DirectCallVideoSettingsChanged,
            RNSendBirdCalls.DirectCallRinging,
            RNSendBirdCalls.DirectCallDidAccept
        ]
      }

    @objc func configure(_ appId: String, resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) -> Void {
        SendBirdCall.configure(appId: appId)
        resolve(true)
    }

    @objc func authenticate(_ userId: String, accessToken: String, resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) -> Void {
        let params = AuthenticateParams(userId: userId, accessToken: accessToken)

        SendBirdCall.authenticate(with: params) { (user, error) in
            guard let user = user, error == nil else {
                // Handle error.
                let code = "\(error?.errorCode.rawValue ?? 0)"
                let message = error?.localizedDescription
                reject(code,message, nil)
           
                return
            }

            // The user has been authenticated successfully and is connected to Sendbird server.
            // Register device token by using the `SendBirdCall.registerVoIPPush` or `SendBirdCall.registerRemotePush` methods.
            resolve(["userId": user.userId, "nickname": user.nickname])
        }
    }
    
    @objc func deauthenticate(_ resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) -> Void {
        SendBirdCall.deauthenticate() { error in
            guard error == nil else {
                let code = "\(error?.errorCode.rawValue ?? 0)"
                let message = error?.localizedDescription
                reject(code,message, nil)
                return
            }
            resolve(true)
        }
    }

    @objc func dial(_ calleeId: String, isVideoCall: Bool, resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock){

        let callOptions = CallOptions(isAudioEnabled: true, isVideoEnabled: true, useFrontCamera: true)
        let params = DialParams(calleeId: calleeId, isVideoCall: isVideoCall, callOptions: callOptions)

        let directCall = SendBirdCall.dial(with: params) { call, error in

            guard let call = call, error == nil else {
                
                let code = "\(error?.errorCode.rawValue ?? 0)"
                let message = error?.localizedDescription
                reject(code,message, nil)
                return
            }

            // The call has been created successfully
            let params = self.buildParams(call)
            resolve(params)
        }

        directCall?.delegate = self

    }

    @objc func endCall(_ callId: String, resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) {
        guard let call = SendBirdCall.getCall(forCallId: callId) else {
            reject("0","Call not found",nil)
            return
        }
        call.end()
        CXCallManager.shared.endCXCall(call)
        let params = buildParams(call)
        resolve(params)
    }
    
    @objc func acceptCall(_ callId: String, resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) {
        guard let call = SendBirdCall.getCall(forCallId: callId) else {
            reject("0","Call not found",nil)
            return
        }
        call.accept(with: AcceptParams())
        call.delegate = self
        
        var callId: UUID = UUID()
        if let callUUID = call.callUUID {
            callId = callUUID
        }
//        CXCallManager.shared.endCXCall(call)
        let params = buildParams(call)
        resolve(params)
    }

    @objc func voipRegistration() {
        self.voipRegistry = PKPushRegistry(queue: DispatchQueue.main)
        self.voipRegistry?.delegate = self
        self.voipRegistry?.desiredPushTypes = [.voIP]
    }
    
    @objc func registerPushToken(_ token: String, resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) {
        SendBirdCall.registerRemotePush(token: token.data(using: .utf8), unique: false) { (error) in
            guard error == nil else {
                let code = "\(error?.errorCode.rawValue ?? 0)"
                let message = error?.localizedDescription
                reject(code,message, nil)
                return
            }
            resolve(true)
        }
    }
    
    @objc func addDirectCallSound(_ soundType: String, filename: String) {
        if(soundType == "DIALING"){
            SendBirdCall.addDirectCallSound(filename, forType: .dialing)
        }else if(soundType == "RINGING"){
            SendBirdCall.addDirectCallSound(filename, forType: .ringing)
        }else if(soundType == "RECONNECTING"){
            SendBirdCall.addDirectCallSound(filename, forType: .reconnecting)
        }else if(soundType == "RECONNECTED"){
            SendBirdCall.addDirectCallSound(filename, forType: .reconnected)
        }
        
    }
    
    @objc func setDirectCallDialingSoundOnWhenSilentMode(_ isEnabled: Bool) {
        SendBirdCall.setDirectCallDialingSoundOnWhenSilentMode(isEnabled: isEnabled)
    }
    
    @objc func setCallConnectionTimeout(_ second: Int) {
        SendBirdCall.setCallConnectingTimeout(second)
    }
    
    @objc func setRingingTimeout(_ second: Int) {
        SendBirdCall.setRingingTimeout(second)
    }
    
    @objc func switchCamera(_ callId: String, resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) {
        guard let call = SendBirdCall.getCall(forCallId: callId) else {
            reject("0","Call not found",nil)
            return
        }
        call.switchCamera() {error in
            guard error == nil else {
                let code = "\(error?.errorCode.rawValue ?? 0)"
                let message = error?.localizedDescription
                reject(code,message, nil)
                return
            }
            resolve(true);
        }
    }
    
    @objc func stopVideo(_ callId: String, resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) {
        guard let call = SendBirdCall.getCall(forCallId: callId) else {
            reject("0","Call not found",nil)
            return
        }
        call.stopVideo()
        resolve(true)
    }
    
    @objc func startVideo(_ callId: String, resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) {
        guard let call = SendBirdCall.getCall(forCallId: callId) else {
            reject("0","Call not found",nil)
            return
        }
        call.startVideo()
        resolve(true)
    }
    
    @objc func muteMicrophone(_ callId: String, resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) {
        guard let call = SendBirdCall.getCall(forCallId: callId) else {
            reject("0","Call not found",nil)
            return
        }
        call.muteMicrophone()
        resolve(true)
    }
    
    @objc func unmuteMicrophone(_ callId: String, resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) {
        guard let call = SendBirdCall.getCall(forCallId: callId) else {
            reject("0","Call not found",nil)
            return
        }
        call.unmuteMicrophone()
        resolve(true)
    }

    // MARK: - SendBirdCalls - Registering push token.
    func pushRegistry(_ registry: PKPushRegistry, didUpdate pushCredentials: PKPushCredentials, for type: PKPushType) {
        print("RNSendBirdCalls:Push token is \(pushCredentials.token)")

        SendBirdCall.registerVoIPPush(token: pushCredentials.token, unique: true) { error in
            guard error == nil else { return }
        }
    }

    // MARK: - SendBirdCalls - Receive incoming push event
    func pushRegistry(_ registry: PKPushRegistry, didReceiveIncomingPushWith payload: PKPushPayload, for type: PKPushType) {
        print("RNSendBirdCalls::pushRegistry incoming push event")
        SendBirdCall.pushRegistry(registry, didReceiveIncomingPushWith: payload, for: type, completionHandler: nil)
    }


    // MARK: - SendBirdCalls - Handling incoming call
    @available(iOS 10.0, *)
    func pushRegistry(_ registry: PKPushRegistry, didReceiveIncomingPushWith payload: PKPushPayload, for type: PKPushType, completion: @escaping () -> Void) {
        print("RNSendBirdCalls::pushRegistry incoming call")
        SendBirdCall.pushRegistry(registry, didReceiveIncomingPushWith: payload, for: type) { uuid in
            guard uuid != nil else {
                let update = CXCallUpdate()
                update.remoteHandle = CXHandle(type: .generic, value: "invalid")
                let randomUUID = UUID()

                CXCallManager.shared.reportIncomingCall(with: randomUUID, update: update) { _ in
                    CXCallManager.shared.endCall(for: randomUUID, endedAt: Date(), reason: .acceptFailed)
                }
                completion()
                return
            }

            completion()
        }
    }

    // MARK: SendBirdCallDelegate
    func didStartRinging(_ call: DirectCall) {
        print("RNSendBirdCalls:didStartRinging")
       call.delegate = self // To receive call event through `DirectCallDelegate`

       guard let uuid = call.callUUID else { return }
       guard CXCallManager.shared.shouldProcessCall(for: uuid) else { return }  // Should be cross-checked with state to prevent weird event processings

       // Use CXProvider to report the incoming call to the system
       // Construct a CXCallUpdate describing the incoming call, including the caller.
       let name = call.caller?.userId ?? "Unknown"
       let update = CXCallUpdate()
       update.remoteHandle = CXHandle(type: .generic, value: name)
       update.hasVideo = call.isVideoCall
       update.localizedCallerName = call.caller?.nickname ?? call.caller?.userId ?? "Unknown"

       if SendBirdCall.getOngoingCallCount() > 1 {
           // Allow only one ongoing call.
           CXCallManager.shared.reportIncomingCall(with: uuid, update: update) { _ in
               CXCallManager.shared.endCall(for: uuid, endedAt: Date(), reason: .declined)
           }
           call.end()
       } else {
           // Report the incoming call to the system
           CXCallManager.shared.reportIncomingCall(with: uuid, update: update)
       }
        let params = buildParams(call)
        self.sendEvent(withName: RNSendBirdCalls.DirectCallRinging, body:params)
    }
    
    // MARK: DirectCallDelegate
    func didEstablish(_ call: DirectCall) {
        print("RNSendBirdCalls:didConnect")
        let params = buildParams(call)
        self.sendEvent(withName: RNSendBirdCalls.DirectCallDidConnect, body:params)
    }

    // MARK: DirectCallDelegate
    func didConnect(_ call: DirectCall) {
     print("RNSendBirdCalls:didConnect")
        //active timer call.duration
        let params = buildParams(call)
        self.sendEvent(withName: RNSendBirdCalls.DirectCallDidConnect, body:params)
    }

    // MARK: DirectCallDelegate
    func didEnd(_ call: DirectCall) {
       var callId: UUID = UUID()
       if let callUUID = call.callUUID {
           callId = callUUID
       }
       print("RNSendBirdCalls:didEnd")
        let params = buildParams(call)
        self.sendEvent(withName: RNSendBirdCalls.DirectCallDidEnd, body: params)
       CXCallManager.shared.endCall(for: callId, endedAt: Date(), reason: call.endResult)

       guard let callLog = call.callLog else { return }
       //UserDefaults.standard.callHistories.insert(CallHistory(callLog: callLog), at: 0)
       //CallHistoryViewController.main?.updateCallHistories()
    }
    
    // MARK: DirectCallDelegate
    func didRemoteAudioSettingsChange(_ call: DirectCall) {
     print("RNSendBirdCalls:didConnect")
        let params = buildParams(call)
        self.sendEvent(withName: RNSendBirdCalls.DirectCallRemoteAudioSettingsChanged, body:params)
    }
    
    // MARK: DirectCallDelegate
    func didRemoteVideoSettingsChange(_ call: DirectCall) {
        let params = buildParams(call)
        self.sendEvent(withName: RNSendBirdCalls.DirectCallVideoSettingsChanged, body:params)
    }
    
    private func buildParams(_ call: DirectCall) -> [String : Any] {
        var params = [
            "callId": call.callId as Any,
            "callee": call.callee?.userId as Any,
            "calleeNickname": call.callee?.nickname as Any,
            "caller": call.caller?.userId as Any,
            "callerNickname": call.caller?.nickname as Any,
            "duration": call.duration as Any,
            "isVideoCall": call.isVideoCall as Any,
            "isLocalAudioEnabled": call.isLocalAudioEnabled as Any,
            "isRemoteAudioEnabled": call.isRemoteAudioEnabled as Any,
            "endResult": call.endResult.rawValue as Any,
            "myRole": call.myRole.rawValue as Any,
        ] as [String : Any]
        if (call.isVideoCall) {
            params.updateValue(call.isRemoteVideoEnabled as Any, forKey: "isRemoteVideoEnabled")
            params.updateValue(call.isLocalVideoEnabled as Any, forKey: "isLocalVideoEnabled")
        }
        return params;
    }
    
}
