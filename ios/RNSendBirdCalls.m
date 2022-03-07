// RNSendBirdCalls.m

#import <React/RCTBridgeModule.h>

@interface RCT_EXTERN_MODULE(RNSendBirdCalls, NSObject)

RCT_EXTERN_METHOD(configure:(NSString *)appId resolve:(RCTPromiseResolveBlock *)resolve reject:(RCTPromiseRejectBlock *)reject)
RCT_EXTERN_METHOD(authenticate:(NSString *)userId accessToken:(NSString *)accessToken resolve:(RCTPromiseResolveBlock *)resolve reject:(RCTPromiseRejectBlock *)reject)
RCT_EXTERN_METHOD(dial:(NSString *)calleeId resolve:(RCTPromiseResolveBlock *)resolve reject:(RCTPromiseRejectBlock *)reject)
RCT_EXTERN_METHOD(endCall:(NSString *)callId resolve:(RCTPromiseResolveBlock *)resolve reject:(RCTPromiseRejectBlock *)reject)
RCT_EXTERN_METHOD(voipRegistration)


+ (BOOL) requiresMainQueueSetup {
  return YES;
}

@end
