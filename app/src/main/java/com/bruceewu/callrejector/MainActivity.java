package com.bruceewu.callrejector;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.bruceewu.callrejector.utils.CallService;
import com.bruceewu.callrejector.utils.LogUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission();
    }

    @SuppressLint("CheckResult")
    private void checkPermission() {
        new RxPermissions(this)
                .request(Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_CALL_LOG)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(grant -> {
                    if (grant) {
                        register();
                    } else {
                        LogUtils.log("Permission denied");
                    }
                }, error -> {
                    LogUtils.log(error.getMessage());
                });
    }

    private void register() {
        startService(new Intent(this, CallService.class));
    }
}