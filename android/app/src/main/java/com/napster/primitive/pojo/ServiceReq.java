package com.napster.primitive.pojo;

import java.io.Serializable;

/**
 * Created by napster on 05/01/17.
 */

public class ServiceReq implements Serializable {

    int code;

    public ServiceReq(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    String fileUri;
    String fileKey;

    public String getFileUri() {
        return fileUri;
    }

    public void setFileUri(String fileUri) {
        this.fileUri = fileUri;
    }

    public String getFileKey() {
        return fileKey;
    }

    public void setFileKey(String fileKey) {
        this.fileKey = fileKey;
    }
}
