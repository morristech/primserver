package com.napster.primitive.api;

import com.napster.primitive.pojo.FileQueueResponse;
import com.napster.primitive.pojo.FileUploadResponse;

import java.util.HashMap;

import retrofit.http.Body;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.mime.TypedFile;

/**
 * Created by napster on 05/01/17.
 */

public interface ApiInterface {

    @PUT("/dev/{fileKey}")
    @Headers("Content-Type: image/jpeg")
    FileUploadResponse uploadFile(@Path("fileKey") String fileKey, @Body TypedFile file);

    @POST("/dev/queue")
    @Headers("Content-Type: application/json")
    FileQueueResponse queueFile(@Body HashMap<String, String> payload);

}
