// RNSendBirdCalls.m

#import <React/RCTBridgeModule.h>

@interface RCT_EXTERN_MODULE(RNSendBirdCalls, NSObject)

RCT_EXTERN_METHOD(configure:(NSString *)appId resolve:(RCTPromiseResolveBlock *)resolve reject:(RCTPromiseRejectBlock *)reject)
RCT_EXTERN_METHOD(authenticate:(NSString *)userId accessToken:(NSString *)accessToken resolve:(RCTPromiseResolveBlock *)resolve reject:(RCTPromiseRejectBlock *)reject)
RCT_EXTERN_METHOD(dial:(NSString *)calleeId isVideoCall:(BOOL *)isVideoCall resolve:(RCTPromiseResolveBlock *)resolve reject:(RCTPromiseRejectBlock *)reject)
RCT_EXTERN_METHOD(endCall:(NSString *)callId resolve:(RCTPromiseResolveBlock *)resolve reject:(RCTPromiseRejectBlock *)reject)
RCT_EXTERN_METHOD(acceptCall:(NSString *)callId resolve:(RCTPromiseResolveBlock *)resolve reject:(RCTPromiseRejectBlock *)reject)
RCT_EXTERN_METHOD(voipRegistration)
RCT_EXTERN_METHOD(registerPushToken:(NSString *)callId resolve:(RCTPromiseResolveBlock *)resolve reject:(RCTPromiseRejectBlock *)reject)
RCT_EXTERN_METHOD(addDirectCallSound:(NSString *)soundType filename:(NSString *)filename)
RCT_EXTERN_METHOD(setDirectCallDialingSoundOnWhenSilentMode:(BOOL *)isEnabled)
RCT_EXTERN_METHOD(setCallConnectionTimeout:(NSInteger *)second)
RCT_EXTERN_METHOD(setRingingTimeout:(NSInteger *)second)
RCT_EXTERN_METHOD(switchCamera:(NSString *)callId resolve:(RCTPromiseResolveBlock *)resolve reject:(RCTPromiseRejectBlock *)reject)
RCT_EXTERN_METHOD(stopVideo:(NSString *)callId resolve:(RCTPromiseResolveBlock *)resolve reject:(RCTPromiseRejectBlock *)reject)
RCT_EXTERN_METHOD(startVideo:(NSString *)callId resolve:(RCTPromiseResolveBlock *)resolve reject:(RCTPromiseRejectBlock *)reject)
RCT_EXTERN_METHOD(muteMicrophone:(NSString *)callId resolve:(RCTPromiseResolveBlock *)resolve reject:(RCTPromiseRejectBlock *)reject)
RCT_EXTERN_METHOD(unmuteMicrophone:(NSString *)callId resolve:(RCTPromiseResolveBlock *)resolve reject:(RCTPromiseRejectBlock *)reject)

+ (BOOL) requiresMainQueueSetup {
  return YES;
}

@end
