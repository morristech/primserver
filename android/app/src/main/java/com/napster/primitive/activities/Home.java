package com.napster.primitive.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.napster.primitive.R;
import com.napster.primitive.adapters.SubmissionsAdapter;
import com.napster.primitive.api.ApiService;
import com.napster.primitive.dao.PrimitiveDao;
import com.napster.primitive.pojo.ServiceReq;
import com.napster.primitive.pojo.ServiceResp;
import com.napster.primitive.pojo.Submission;
import com.napster.primitive.utils.Constants;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;

public class Home extends BaseActivity {

    private Uri outputFileUri = null;
    private final int PICK_IMG_REQ_CODE = 0;
    private final int PERMISSION_REQ_CODE = 1;
    private final int CAPTURE_IMG_REQ_CODE = 2;

    SubmissionsAdapter adapter;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        adapter = new SubmissionsAdapter();
        RecyclerView rvImages = (RecyclerView) findViewById(R.id.rvImages);
        //StaggeredGridLayoutManager lm = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        GridLayoutManager lm = new GridLayoutManager(this, 2);
        rvImages.setLayoutManager(lm);
        rvImages.setAdapter(adapter);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swpRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new DbSyncTask().execute();
            }
        });

        new DbSyncTask().execute();
    }

    public void onAddClicked(View view) {
        new MaterialDialog.Builder(this)
                .items("Select from Gallery", "New Picture")
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        switch (which) {
                            case 0:
                                fireGalleryIntent();
                                break;
                            case 1:
                                fireCameraIntent();
                                break;
                        }
                    }
                })
                .show();
    }

    private void fireCameraIntent() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Snackbar.make(findViewById(android.R.id.content), "This device does not have a camera", Snackbar.LENGTH_LONG).show();
            return;
        }

        outputFileUri = createImageFileUri();
        if (outputFileUri != null) {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(cameraIntent, CAPTURE_IMG_REQ_CODE);
            } else {
                showSnackBar("No camera apps installed");
            }
        } else {
            showSnackBar("Unable to write to external storage");
        }
    }

    private void fireGalleryIntent() {
        if (checkPermission()) {
            sendPickIntent();
        } else {
            requestPermission();
        }
    }

    private Uri createImageFileUri() {
        String collisionFreeFileName = "IMG_" + System.currentTimeMillis();
        File directory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        Uri uri = null;
        try {
            File imageFile = File.createTempFile(collisionFreeFileName, ".jpg", directory);
            uri = Uri.fromFile(imageFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return uri;
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void sendPickIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMG_REQ_CODE);
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            showSnackBar("Permission required to select image");
        }
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQ_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMG_REQ_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                dispatchIntentWithFileUri(data.getData());
            }
        } else if (requestCode == CAPTURE_IMG_REQ_CODE && resultCode == RESULT_OK) {
            dispatchIntentWithFileUri(outputFileUri);
        }
    }

    private void dispatchIntentWithFileUri(Uri localFileUri) {
        showProgressDialog("Uploading file...");
        ServiceReq req = new ServiceReq(Constants.API_UPLOAD_FILE);
        Intent intent = new Intent(this, ApiService.class);
        req.setFileUri(localFileUri.toString());
        intent.putExtra(Constants.SERVICE_REQUEST, req);
        startService(intent);
    }

    private void queueFile(String fileKey, String fileUri) {
        showProgressDialog("Queueing your request...");
        ServiceReq req = new ServiceReq(Constants.API_QUEUE_FILE);
        Intent intent = new Intent(this, ApiService.class);
        req.setFileKey(fileKey);
        req.setFileUri(fileUri);
        intent.putExtra(Constants.SERVICE_REQUEST, req);
        startService(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onApiResponse(ServiceResp response) {
        dismissProgressDialog();
        switch (response.getCode()) {
            case Constants.API_UPLOAD_FILE:
                if (response.isSuccess()) {
                    showSnackBar("Upload complete");
                    queueFile(response.getFileKey(), response.getFileUri());
                } else {
                    showSnackBar("Unable to upload file");
                }
                break;
            case Constants.API_QUEUE_FILE:
                if(response.isSuccess()) {
                    showSnackBar("Successfully queued your file");
                    new DbSyncTask().execute(response);
                } else {
                    showSnackBar("Unable to queue your request. Please try later.");
                }
                break;
        }
    }

    class DbSyncTask extends AsyncTask<ServiceResp, Void, ArrayList<Submission>> {

        @Override
        protected ArrayList<Submission> doInBackground(ServiceResp... responses) {
            PrimitiveDao dao = new PrimitiveDao(getApplicationContext());
            if(responses != null && responses.length > 0) {
                ServiceResp response = responses[0];
                Submission submission = new Submission();
                submission.setFileKey(response.getFileKey());
                submission.setOriginalUri(response.getFileUri());
                submission.setProcessed(false);
                dao.insertSubmission(submission);
            }

            ArrayList<Submission> submissions = dao.selectSubmissions();
            return submissions;
        }

        @Override
        protected void onPostExecute(ArrayList<Submission> submissions) {
            super.onPostExecute(submissions);
            Log.e("------", new Gson().toJson(submissions));
            adapter.setSubmissions(submissions);
            swipeRefreshLayout.setRefreshing(false);
        }
    }
}