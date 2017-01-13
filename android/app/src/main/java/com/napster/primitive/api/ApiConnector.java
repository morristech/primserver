package com.napster.primitive.api;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.napster.primitive.pojo.FileQueueResponse;
import com.napster.primitive.pojo.FileUploadResponse;
import com.napster.primitive.utils.Constants;
import com.napster.primitive.utils.Utils;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

import retrofit.RestAdapter;
import retrofit.mime.TypedFile;

/**
 * Created by napster on 05/01/17.
 */

public class ApiConnector {

    private static final RestAdapter adapter = new RestAdapter.Builder()
            .setLogLevel(RestAdapter.LogLevel.FULL)
            .setEndpoint("https://3d9x38s9bk.execute-api.us-east-1.amazonaws.com")
            .build();

    private static final ApiInterface api = adapter.create(ApiInterface.class);


    public FileUploadResponse uploadFile(Context context, String fileUri) {
        FileUploadResponse response = null;
        try {
            String fileKey = UUID.randomUUID().toString();
            Uri uri = Uri.parse(fileUri);
            Bitmap bitmap = Utils.getScaledBitmapFromUri(context, uri, 500);
            if(bitmap != null) {
                File file = Utils.getCachedFile(context, bitmap);
                if (file != null) {
                    TypedFile image = new TypedFile("image/jpeg", file);
                    response = api.uploadFile(fileKey, image);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    public FileQueueResponse queueFile(Context context, String fileKey) {
        FileQueueResponse response = null;
        try {
            String fcmToken = PreferenceManager
                    .getDefaultSharedPreferences(context).getString(Constants.FCM_TOKEN, null);
            if(fcmToken == null)
                throw new Exception("No FCM token present. Unable to queue file");
            HashMap<String, String> payload = new HashMap<>();
            payload.put("filekey", fileKey);
            payload.put("fcmtoken", fcmToken);
            response = api.queueFile(payload);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
}
