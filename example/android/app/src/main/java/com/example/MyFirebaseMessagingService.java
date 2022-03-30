package com.example;

import androidx.annotation.NonNull;
import android.content.Context;
import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
// import com.sendbird.calls.SendBirdCall;
import com.rnsendbirdcalls.RNSendBirdCallsModule;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Log.i("FCMService", "[MyFirebaseMessagingService] onMessageReceived");
        RNSendBirdCallsModule.onMessageReceived(remoteMessage.getData());
//         if (SendBirdCall.handleFirebaseMessageData(remoteMessage.getData())) {
//             Log.i("RNSendBirdCalls", "[MyFirebaseMessagingService] onMessageReceived() => " + remoteMessage.getData().toString());
//         }
    }

    @Override
    public void onNewToken(@NonNull String token) {
        RNSendBirdCallsModule.onNewToken(token);
        Log.i("FCMService", "[MyFirebaseMessagingService] onNewToken(token: " + token + ")");


    }
}
