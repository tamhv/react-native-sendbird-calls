package com.rnsendbirdcalls;

import android.view.View;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.sendbird.calls.DirectCall;
import com.sendbird.calls.SendBirdCall;
import com.sendbird.calls.SendBirdVideoView;

import org.jetbrains.annotations.NotNull;

public class RNVSendBirdCallsVideoManager extends ViewGroupManager<RNVSendBirdCallsVideo> {

    private int propWidth;
    private int propHeight;

    @NotNull
    @Override
    public String getName() {
        return "RNVSendBirdCallsVideo";
    }

    @NonNull
    @NotNull
    @Override
    protected RNVSendBirdCallsVideo createViewInstance(@NonNull @NotNull ThemedReactContext reactContext) {
        return new RNVSendBirdCallsVideo(reactContext);
    }

    @ReactProp(name = "call")
    public void setCall(RNVSendBirdCallsVideo view, ReadableMap call) {
        String callId = call.getString("callId");
        boolean local = call.getBoolean("local");
        if (callId != null) {
            DirectCall directCall = SendBirdCall.getCall(callId);
            if (directCall != null) {
                SendBirdVideoView videoView = view.getSurfaceViewRenderer();
                if (local) {
                    directCall.setLocalVideoView(videoView);
                } else {
                    directCall.setRemoteVideoView(videoView);
                }
            }
        }
    }

    public void manuallyLayoutChildren(View view) {
        // propWidth and propHeight coming from react-native props
        int width = propWidth;
        int height = propHeight;

        view.measure(
                View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY));

        view.layout(0, 0, width, height);
    }
}
