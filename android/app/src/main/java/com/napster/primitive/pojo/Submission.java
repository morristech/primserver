package com.napster.primitive.pojo;

/**
 * Created by napster on 05/01/17.
 */

public class Submission {

    String fileKey;
    boolean processed;
    String originalUri;
    String processedUri;

    public String getFileKey() {
        return fileKey;
    }

    public void setFileKey(String fileKey) {
        this.fileKey = fileKey;
    }

    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    public String getOriginalUri() {
        return originalUri;
    }

    public void setOriginalUri(String originalUri) {
        this.originalUri = originalUri;
    }

    public String getProcessedUri() {
        return processedUri;
    }

    public void setProcessedUri(String processedUri) {
        this.processedUri = processedUri;
    }
}
