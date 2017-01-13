package com.napster.primitive.activities;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by napster on 05/01/17.
 */

public class BaseActivity extends AppCompatActivity {

    MaterialDialog dialog;

    public void showSnackBar(String message) {
        View view = findViewById(android.R.id.content);
        if (view != null)
            Snackbar
                    .make(view, message, Snackbar.LENGTH_LONG)
                    .show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    public void showProgressDialog(String message) {
        dialog = new MaterialDialog.Builder(this)
                .content(message)
                .progress(true, 0)
                .build();
        dialog.show();
    }

    public void dismissProgressDialog() {
        if(dialog != null)
            dialog.dismiss();
    }
}
