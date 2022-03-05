// RNSendBirdCalls.m

#import <React/RCTBridgeModule.h>

@interface RCT_EXTERN_MODULE(RNSendBirdCalls, NSObject)

RCT_EXTERN_METHOD(configure:(NSString *)appId resolve:(RCTPromiseResolveBlock *)resolve reject:(RCTPromiseRejectBlock *)reject)
RCT_EXTERN_METHOD(authenticate:(NSString *)userId accessToken:(NSString *)accessToken resolve:(RCTPromiseResolveBlock *)resolve reject:(RCTPromiseRejectBlock *)reject)
RCT_EXTERN_METHOD(addDelegate:(NSString *)identifier)
RCT_EXTERN_METHOD(dial)
RCT_EXTERN_METHOD(voipRegistration)


+ (BOOL) requiresMainQueueSetup {
  return YES;
}

@end
