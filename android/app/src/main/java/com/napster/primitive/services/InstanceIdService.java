package com.napster.primitive.services;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.napster.primitive.utils.Constants;

import static com.google.android.gms.internal.zzs.TAG;

/**
 * Created by napster on 25/11/16.
 */

public class InstanceIdService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.e(TAG, "FCM Token refreshed");
        SharedPreferences.Editor editor = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext()).edit();
        editor.putString(Constants.FCM_TOKEN, token);
        editor.commit();
    }
}
