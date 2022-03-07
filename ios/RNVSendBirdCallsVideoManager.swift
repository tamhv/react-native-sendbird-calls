import SendBirdCalls

@objc(RNVSendBirdCallsVideoManager)
class RNVSendBirdCallsVideoManager: RCTViewManager {

    override static func requiresMainQueueSetup() -> Bool{
        return true
    }

  override func view() -> UIView! {
      return RNVSendBirdCallsVideo()

  }
}

