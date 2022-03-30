// RNSendBirdCallsModule.java

package com.rnsendbirdcalls;

import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.sendbird.calls.AuthenticateParams;
import com.sendbird.calls.CallOptions;
import com.sendbird.calls.DialParams;
import com.sendbird.calls.AcceptParams;
import com.sendbird.calls.DirectCall;
import com.sendbird.calls.SendBirdCall;
import com.sendbird.calls.handler.DirectCallListener;
import com.sendbird.calls.handler.SendBirdCallListener;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

public class RNSendBirdCallsModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;

    public RNSendBirdCallsModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "RNSendBirdCalls";
    }

    @ReactMethod
    public void addListener(String eventName) {

    }

    @ReactMethod
    public void removeListeners(Integer count) {

    }

    @ReactMethod
    public void configure(String appId, Promise promise) {
        if (SendBirdCall.init(getReactApplicationContext(), appId)) {
            SendBirdCall.addListener(UUID.randomUUID().toString(), new SendBirdCallListener() {
                @Override
                public void onRinging(@NotNull DirectCall directCall) {
                    Log.i(getName(), "onRinging");
                    setListener(directCall);
                }
            });
            promise.resolve(true);

        } else {
            promise.reject("'init'", "Init failed");
        }
    }

    @ReactMethod
    public void authenticate(String userId, String accessToken, Promise promise) {
        AuthenticateParams params = new AuthenticateParams(userId).setAccessToken(accessToken);
        SendBirdCall.authenticate(params, (user, e) -> {
            if (e == null) {
                // The user has been authenticated successfully and is connected to Sendbird server
                WritableMap result = Arguments.createMap();
                result.putString("userId", user.getUserId());
                result.putString("nickname", user.getNickname());
                promise.resolve(result);
            } else {
                promise.reject(String.format("%s ", e.getCode()), e.toString());
            }
        });
    }

    @ReactMethod
    public void registerPushToken(String token, Promise promise) {
        if (SendBirdCall.getCurrentUser() != null) {
            SendBirdCall.registerPushToken(token, false, e -> {
                if (e != null) {
                    Log.i("RNSendBirdCalls", "registerPushToken => e: " + e.getMessage());
                    promise.reject("registerPushToken", e.getMessage(), e);
                } else {
                    promise.resolve(true);
                }
            });
        }
    }


    @ReactMethod
    public void dial(String callee, boolean isVideoCall, Promise promise) {
        CallOptions callOptions = new CallOptions().setVideoEnabled(true).setAudioEnabled(true);

        DialParams dialParams = new DialParams(callee);
        dialParams.setVideoCall(isVideoCall);
        dialParams.setCallOptions(callOptions);
        DirectCall mDirectCall = SendBirdCall.dial(dialParams, (call, e) -> {
            if (e != null) {
                promise.reject(String.format("%s", e.getCode()), e.getMessage());
                return;
            }

            WritableMap params = Arguments.createMap();
            params.putString("callId", call.getCallId());
            params.putString("caller", call.getCaller().getUserId());
            params.putString("callee", call.getCallee().getUserId());
            promise.resolve(params);
        });

        if (mDirectCall != null) {
            setListener(mDirectCall);
        }
    }

    @ReactMethod
    public void endCall(String callId, Promise promise) {
        DirectCall directCall = SendBirdCall.getCall(callId);
        if (directCall != null) {
            directCall.end();
            WritableMap params = Arguments.createMap();
            params.putString("callId", directCall.getCallId());
            params.putString("caller", directCall.getCaller().getUserId());
            params.putString("callee", directCall.getCallee().getUserId());
            params.putString("duration", String.valueOf(directCall.getDuration()));
            promise.resolve(params);
        } else {
            promise.reject("0", "Call not found");
        }
    }

    @ReactMethod
    public void acceptCall(String callId, Promise promise) {
        DirectCall directCall = SendBirdCall.getCall(callId);
        if (directCall != null) {
            AcceptParams acceptParams = new AcceptParams();
            directCall.accept(acceptParams);
            promise.resolve(true);
        } else {
            promise.reject("0", "Call not found");
        }
    }

    private void setListener(DirectCall directCall) {
        directCall.setListener(new DirectCallListener() {
            @Override
            public void onEstablished(@NotNull DirectCall call) {
                //Remote user accepted the call.
                Log.i(getName(), "onEstablished");
            }

            @Override
            public void onConnected(@NotNull DirectCall directCall) {
                Log.i(getName(), "onConnected");
                WritableMap params = Arguments.createMap();
                params.putString("callId", directCall.getCallId());
                params.putString("caller", directCall.getCaller().getUserId());
                params.putString("callee", directCall.getCallee().getUserId());
                params.putBoolean("isVideoCall", directCall.isVideoCall());
                reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("DirectCallDidConnect", params);
            }

            @Override
            public void onEnded(@NotNull DirectCall directCall) {
                Log.i(getName(), "onEnded");
                WritableMap params = Arguments.createMap();
                params.putString("callId", directCall.getCallId());
                params.putString("caller", directCall.getCaller().getUserId());
                params.putString("callee", directCall.getCallee().getUserId());
                params.putDouble("duration", directCall.getDuration());
                reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("DirectCallDidEnd", params);
            }
        });
    }

    public static void onMessageReceived(Map<String, String> data) {
        if (SendBirdCall.handleFirebaseMessageData(data)) {
//            Log.i("RNSendBirdCalls", "[MyFirebaseMessagingService] onMessageReceived() => " + data.toString());
        }
    }

    public static void onNewToken(String token) {
        if (SendBirdCall.getCurrentUser() != null) {
            SendBirdCall.registerPushToken(token, false, e -> {
                if (e != null) {
                    Log.i("RNSendBirdCalls", "[FirebaseMessagingService] onNewToken() => e: " + e.getMessage());
                }
            });
        }
    }
}
