package com.napster.primitive.services;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import static com.google.android.gms.internal.zzs.TAG;

/**
 * Created by napster on 25/11/16.
 */

public class MessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        Log.e(TAG, "From: " + remoteMessage.getFrom());

        try {
            Log.e(TAG, "Notification Message Body: " + new Gson().toJson(remoteMessage.getData()));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
