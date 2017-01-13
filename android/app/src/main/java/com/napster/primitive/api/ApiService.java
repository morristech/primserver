package com.napster.primitive.api;

import android.app.IntentService;
import android.content.Intent;

import com.napster.primitive.pojo.FileQueueResponse;
import com.napster.primitive.pojo.FileUploadResponse;
import com.napster.primitive.pojo.ServiceReq;
import com.napster.primitive.pojo.ServiceResp;
import com.napster.primitive.utils.Constants;

import org.greenrobot.eventbus.EventBus;

public class ApiService extends IntentService {

    ApiConnector apiConnector;

    public ApiService() {
        super("ApiService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        apiConnector = new ApiConnector();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            ServiceReq req = (ServiceReq) intent.getSerializableExtra(Constants.SERVICE_REQUEST);
            if (req != null) {
                switch (req.getCode()) {
                    case Constants.API_UPLOAD_FILE:
                        uploadImage(req);
                        break;
                    case Constants.API_QUEUE_FILE:
                        queueFile(req);
                        break;
                }
            }
        }
    }

    private void uploadImage(ServiceReq req) {
        ServiceResp resp = new ServiceResp(req.getCode());
        FileUploadResponse response = apiConnector.uploadFile(getApplicationContext(), req.getFileUri());
        if (response != null) {
            resp.setSuccess(true);
            resp.setFileKey(response.getFileKey());
            resp.setFileUri(req.getFileUri());
        }
        EventBus.getDefault().post(resp);
    }

    private void queueFile(ServiceReq req) {
        ServiceResp resp = new ServiceResp(req.getCode());
        FileQueueResponse response = apiConnector.queueFile(getApplicationContext(), req.getFileKey());
        if(response != null) {
            resp.setSuccess(true);
            resp.setFileKey(req.getFileKey());
            resp.setFileUri(req.getFileUri());
        }
        EventBus.getDefault().post(resp);
    }
}
