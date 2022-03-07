#import "React/RCTViewManager.h"
@interface RCT_EXTERN_MODULE(RNVSendBirdCallsVideoManager, RCTViewManager)
    RCT_EXPORT_VIEW_PROPERTY(callId, NSString)
    RCT_EXPORT_VIEW_PROPERTY(local, BOOL)
@end
