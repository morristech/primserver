package com.napster.primitive.pojo;

import java.io.Serializable;

/**
 * Created by napster on 05/01/17.
 */

public class ServiceResp implements Serializable {

    int code;
    boolean success;
    String fileKey;
    String fileUri;

    public ServiceResp(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getFileKey() {
        return fileKey;
    }

    public void setFileKey(String fileKey) {
        this.fileKey = fileKey;
    }

    public String getFileUri() {
        return fileUri;
    }

    public void setFileUri(String fileUri) {
        this.fileUri = fileUri;
    }
}
