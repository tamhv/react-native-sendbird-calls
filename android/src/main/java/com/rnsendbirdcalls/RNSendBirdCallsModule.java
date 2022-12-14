// RNSendBirdCallsModule.java

package com.rnsendbirdcalls;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReadableMap;
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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RNSendBirdCallsModule extends ReactContextBaseJavaModule {

    private final Context context;
    private final ReactApplicationContext reactContext;
    private final Vibrator vibrator;

    public RNSendBirdCallsModule(ReactApplicationContext reactApplicationContext) {
        super(reactApplicationContext);
        this.context = reactApplicationContext.getApplicationContext();
        this.reactContext = reactApplicationContext;
        this.vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    @NonNull
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
        long[] vibratePattern = {100, 300, 500, 300, 500, 300};
        int repeat = 0;
        if (SendBirdCall.init(context, appId)) {
            SendBirdCall.removeAllListeners();
            SendBirdCall.addListener(UUID.randomUUID().toString(), new SendBirdCallListener() {
                @Override
                public void onRinging(@NotNull DirectCall directCall) {
                    Log.i(getName(), "onRinging");
                    int ongoingCallCount = SendBirdCall.getOngoingCallCount();
                    if (ongoingCallCount >= 2) {
                        vibrator.cancel();
                        directCall.end();
                        return;
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(
                                VibrationEffect.createWaveform(
                                        vibratePattern,
                                        repeat
                                )
                        );
                    } else {
                        vibrator.vibrate(vibratePattern, repeat);
                    }

                    directCall.unmuteMicrophone();
                    setListener(directCall);
                    WritableMap params = createCallParams(directCall);
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
        switch (soundType) {
            case "DIALING": {
                int id = context.getResources().getIdentifier(filename, "raw", context.getPackageName());
                if (id > 0) {
                    SendBirdCall.Options.addDirectCallSound(SendBirdCall.SoundType.DIALING, id);
                    promise.resolve(true);
                } else {
                    promise.reject("0", "File not found");
                }
                break;
            }
            case "RINGING": {
                int id = context.getResources().getIdentifier(filename, "raw", context.getPackageName());
                if (id > 0) {
                    SendBirdCall.Options.addDirectCallSound(SendBirdCall.SoundType.RINGING, id);
                    promise.resolve(true);
                } else {
                    promise.reject("0", "File not found");
                }
                break;
            }
            case "RECONNECTED": {
                int id = context.getResources().getIdentifier(filename, "raw", context.getPackageName());
                if (id > 0) {
                    SendBirdCall.Options.addDirectCallSound(SendBirdCall.SoundType.RECONNECTED, id);
                    promise.resolve(true);
                } else {
                    promise.reject("0", "File not found");
                }
                break;
            }
            case "RECONNECTING": {
                int id = context.getResources().getIdentifier(filename, "raw", context.getPackageName());
                if (id > 0) {
                    SendBirdCall.Options.addDirectCallSound(SendBirdCall.SoundType.RECONNECTING, id);
                    promise.resolve(true);
                } else {
                    promise.reject("0", "File not found");
                }
                break;
            }
            default:
                promise.reject("0", "Sound type not found");
                break;
        }
    }

    @ReactMethod
    public void removeDirectCallSound(String soundType, Promise promise) {
        switch (soundType) {
            case "DIALING":
                SendBirdCall.Options.removeDirectCallSound(SendBirdCall.SoundType.DIALING);
                promise.resolve(true);
                break;
            case "RINGING":
                SendBirdCall.Options.removeDirectCallSound(SendBirdCall.SoundType.RINGING);
                promise.resolve(true);
                break;
            case "RECONNECTED":
                SendBirdCall.Options.removeDirectCallSound(SendBirdCall.SoundType.RECONNECTED);
                promise.resolve(true);
                break;
            case "RECONNECTING":
                SendBirdCall.Options.removeDirectCallSound(SendBirdCall.SoundType.RECONNECTING);
                promise.resolve(true);
                break;
            default:
                promise.reject("0", "Sound type not found");
                break;
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
    public void deauthenticate(Promise promise) {
        try {
            SendBirdCall.deauthenticate( (e) -> {
                if (e == null) {
                    promise.resolve(true);
                } else {
                    promise.reject(String.format("%s ", e.getCode()), e.toString());
                }
            });
        } catch (Exception e) {
            promise.reject("0", e.toString());
        }
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

            if (call != null) {
                call.unmuteMicrophone();
                WritableMap params = createCallParams(call);
                promise.resolve(params);
            } else {
                promise.reject("0", "Failed to dial the call");
            }
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
            WritableMap params = createCallParams(directCall);
            promise.resolve(params);
        } else {
            promise.reject("0", "Call not found");
        }
    }

    @ReactMethod
    public void acceptCall(String callId, Promise promise) {
        DirectCall directCall = SendBirdCall.getCall(callId);
        if (directCall != null) {
            directCall.unmuteMicrophone();
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

    @ReactMethod
    public void handleFirebaseMessageData(ReadableMap readableMap, Promise promise) {
        Map<String, String> data = ReactNativeJson.readableMapToMap(readableMap);
        if (data != null) {
            try {
                if (SendBirdCall.handleFirebaseMessageData(data)) {
//            Log.i("RNSendBirdCalls", "[MyFirebaseMessagingService] onMessageReceived() => " + data.toString());
                    promise.resolve(true);
                } else {
                    promise.reject("0", "Failed to handle FCM");
                }
            } catch (Exception e) {
                promise.reject("0", "Failed to handle FCM");
            }
        } else {
            promise.reject("0", "Failed to handle FCM");
        }
    }

    @ReactMethod
    public void switchCamera(String callId, Promise promise) {
        DirectCall directCall = SendBirdCall.getCall(callId);
        if (directCall != null) {
            directCall.switchCamera(e -> {
                if (e != null) {
                    promise.reject(String.format("%s", e.getCode()), e.getMessage());
                    return;
                }
                promise.resolve(true);
            });
        } else {
            promise.reject("0", "Failed to switch camera");
        }
    }

    @ReactMethod
    public void stopVideo(String callId, Promise promise) {
        DirectCall directCall = SendBirdCall.getCall(callId);
        if (directCall != null) {
            directCall.stopVideo();
            promise.resolve(true);
        } else {
            promise.reject("0", "Failed to stop video");
        }
    }

    @ReactMethod
    public void startVideo(String callId, Promise promise) {
        DirectCall directCall = SendBirdCall.getCall(callId);
        if (directCall != null) {
            directCall.startVideo();
            promise.resolve(true);
        } else {
            promise.reject("0", "Failed to start video");
        }
    }

    @ReactMethod
    public void muteMicrophone(String callId, Promise promise) {
        DirectCall directCall = SendBirdCall.getCall(callId);
        if (directCall != null) {
            promise.resolve(directCall.muteMicrophone());
        } else {
            promise.reject("0", "Failed to mute microphone");
        }
    }

    @ReactMethod
    public void unmuteMicrophone(String callId, Promise promise) {
        DirectCall directCall = SendBirdCall.getCall(callId);
        if (directCall != null) {
            promise.resolve(directCall.unmuteMicrophone());
        } else {
            promise.reject("0", "Failed to unmute microphone");
        }
    }

    private void setListener(DirectCall call) {
        call.setListener(new DirectCallListener() {
            @Override
            public void onEstablished(@NotNull DirectCall directCall) {
                //Remote user accepted the call.
                Log.i(getName(), "onEstablished");
                WritableMap params = createCallParams(directCall);
                reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("DirectCallDidAccept", params);
            }

            @Override
            public void onConnected(@NotNull DirectCall directCall) {
                Log.i(getName(), "onConnected");
                vibrator.cancel();
                WritableMap params = createCallParams(directCall);
                reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("DirectCallDidConnect", params);
            }

            @Override
            public void onEnded(@NotNull DirectCall directCall) {
                Log.i(getName(), "onEnded");
                vibrator.cancel();
                WritableMap params = createCallParams(directCall);
                reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("DirectCallDidEnd", params);
            }

            @Override
            public void onRemoteAudioSettingsChanged(@NonNull DirectCall directCall) {
                Log.i(getName(), "onRemoteAudioSettingsChanged");
                if (call.getRemoteUser() != null) {
                    WritableMap params = createCallParams(directCall);
                    reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("DirectCallRemoteAudioSettingsChanged", params);
                }
            }

            @Override
            public void onRemoteVideoSettingsChanged(@NonNull DirectCall directCall) {
                Log.i(getName(), "onRemoteAudioSettingsChanged");
                if (call.getRemoteUser() != null) {
                    WritableMap params = createCallParams(directCall);
                    reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("DirectCallVideoSettingsChanged", params);
                }
            }
        });
    }

    private WritableMap createCallParams(@NotNull DirectCall call) {
        WritableMap params = Arguments.createMap();
        params.putString("callId", call.getCallId());
        params.putString("caller", call.getCaller().getUserId());
        params.putString("callerNickname", call.getCaller().getNickname());
        params.putString("callee", call.getCallee().getUserId());
        params.putString("calleeNickname", call.getCallee().getNickname());
        params.putDouble("duration", call.getDuration());
        params.putBoolean("isVideoCall", call.isVideoCall());
        params.putBoolean("isLocalAudioEnabled", call.isLocalAudioEnabled());
        params.putBoolean("isRemoteAudioEnabled", call.isRemoteAudioEnabled());
        params.putString("endResult", call.getEndResult().toString());
        params.putString("myRole", call.getMyRole().toString());
        if (call.isVideoCall()) {
            params.putBoolean("isLocalVideoEnabled", call.isLocalVideoEnabled());
            params.putBoolean("isRemoteVideoEnabled", call.isRemoteVideoEnabled());
        }
        return params;
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
