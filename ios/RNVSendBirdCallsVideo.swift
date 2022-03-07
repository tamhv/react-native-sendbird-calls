import SendBirdCalls
import CoreGraphics

@objc(RNVSendBirdCallsVideo)
class RNVSendBirdCallsVideo : UIView {
    @objc var callId: String = ""
    @objc var local: Bool = true
    var videoView: SendBirdVideoView!

    override init(frame: CGRect) {
        super.init(frame: frame);
        videoView = SendBirdVideoView(frame: self.frame)
        videoView.backgroundColor = UIColor.gray
        videoView.frame = self.bounds
        self.addSubview(videoView)
    }


       required init?(coder aDecoder: NSCoder) {
          fatalError("init(coder:) has not been implemented")
        }

    override func layoutSubviews() {
        videoView.frame = self.bounds
       }

    override func didSetProps(_ changedProps: [String]!) {
        guard let call = SendBirdCall.getCall(forCallId: self.callId) else {
            return
        }

        if self.local {
            call.updateLocalVideoView(self.videoView)
        }else{
            call.updateRemoteVideoView(self.videoView)
        }

    }

        func setCallId(callId: String) {
            self.callId = callId
        }

    func setLocal(local: Bool) {
        self.local = local
    }
}
