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
            SendBirdCall.removeAllListeners();
            SendBirdCall.addListener(UUID.randomUUID().toString(), new SendBirdCallListener() {
                @Override
                public void onRinging(@NotNull DirectCall directCall) {
                    Log.i(getName(), "onRinging");
                    setListener(directCall);
                    WritableMap params = Arguments.createMap();
                    params.putString("callId", directCall.getCallId());
                    params.putString("caller", directCall.getCaller().getUserId());
                    params.putString("callee", directCall.getCallee().getUserId());
                    params.putBoolean("isVideoCall", directCall.isVideoCall());
                    reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("SendBirdCallRinging", params);
                }
            });
            promise.resolve(true);

        } else {
            promise.reject("0", "Init failed");
        }
    }

    @ReactMethod
    public void addDirectCallSound(String soundType, String filename, Promise promise) {
        if(soundType.equals("DIALING")){
            int id = reactContext.getResources().getIdentifier(filename, "raw", reactContext.getPackageName());
            if (id > 0) {
                SendBirdCall.Options.addDirectCallSound(SendBirdCall.SoundType.DIALING, id);
                promise.resolve(true);
            } else {
                promise.reject("0", "File not found");
            }
        }else if(soundType.equals("RINGING")){
            int id = reactContext.getResources().getIdentifier(filename, "raw", reactContext.getPackageName());
            if (id > 0) {
                SendBirdCall.Options.addDirectCallSound(SendBirdCall.SoundType.RINGING, id);
                promise.resolve(true);
            } else {
                promise.reject("0", "File not found");
            }
        }else if(soundType.equals("RECONNECTED")){
            int id = reactContext.getResources().getIdentifier(filename, "raw", reactContext.getPackageName());
            if (id > 0) {
                SendBirdCall.Options.addDirectCallSound(SendBirdCall.SoundType.RECONNECTED, id);
                promise.resolve(true);
            } else {
                promise.reject("0", "File not found");
            }
        }else if(soundType.equals("RECONNECTING")){
            int id = reactContext.getResources().getIdentifier(filename, "raw", reactContext.getPackageName());
            if (id > 0) {
                SendBirdCall.Options.addDirectCallSound(SendBirdCall.SoundType.RECONNECTING, id);
                promise.resolve(true);
            } else {
                promise.reject("0", "File not found");
            }
        }else{
            promise.reject("0", "Sound type not found");
        }
    }

    @ReactMethod
    public void removeDirectCallSound(String soundType, Promise promise) {
        if(soundType.equals("DIALING")){
            SendBirdCall.Options.removeDirectCallSound(SendBirdCall.SoundType.DIALING);
            promise.resolve(true);
        }else if(soundType.equals("RINGING")){
            SendBirdCall.Options.removeDirectCallSound(SendBirdCall.SoundType.RINGING);
            promise.resolve(true);
        }else if(soundType.equals("RECONNECTED")){
            SendBirdCall.Options.removeDirectCallSound(SendBirdCall.SoundType.RECONNECTED);
            promise.resolve(true);
        }else if(soundType.equals("RECONNECTING")){
            SendBirdCall.Options.removeDirectCallSound(SendBirdCall.SoundType.RECONNECTING);
            promise.resolve(true);
        }else{
            promise.reject("0", "Sound type not found");
        }
    }

    @ReactMethod
    public void setDirectCallDialingSoundOnWhenSilentOrVibrateMode(boolean s, Promise promise) {
        SendBirdCall.Options.setDirectCallDialingSoundOnWhenSilentOrVibrateMode(s);
        promise.resolve(true);
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
    public void unregisterPushToken(String token, Promise promise) {
        if (SendBirdCall.getCurrentUser() != null) {
            SendBirdCall.unregisterPushToken(token, e -> {
                if (e != null) {
                    Log.i("RNSendBirdCalls", "unregisterPushToken => e: " + e.getMessage());
                    promise.reject("unregisterPushToken", e.getMessage(), e);
                } else {
                    promise.resolve(true);
                }
            });
        }
    }

    @ReactMethod
    public void unregisterAllPushTokens(Promise promise) {
        if (SendBirdCall.getCurrentUser() != null) {
            SendBirdCall.unregisterAllPushTokens(e -> {
                if (e != null) {
                    Log.i("RNSendBirdCalls", "unregisterAllPushTokens => e: " + e.getMessage());
                    promise.reject("unregisterAllPushTokens", e.getMessage(), e);
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

    @ReactMethod
    public void setCallConnectionTimeout(int s) {
        SendBirdCall.Options.setCallConnectionTimeout(s);
    }

    @ReactMethod
    public void setRingingTimeout(int s) {
        SendBirdCall.Options.setRingingTimeout(s);
    }

    private void setListener(DirectCall call) {
        call.setListener(new DirectCallListener() {
            @Override
            public void onEstablished(@NotNull DirectCall directCall) {
                //Remote user accepted the call.
                Log.i(getName(), "onEstablished");
                WritableMap params = Arguments.createMap();
                params.putString("callId", directCall.getCallId());
                params.putString("caller", directCall.getCaller().getUserId());
                params.putString("callee", directCall.getCallee().getUserId());
                params.putBoolean("isVideoCall", directCall.isVideoCall());
                reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("DirectCallDidAccept", params);
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
