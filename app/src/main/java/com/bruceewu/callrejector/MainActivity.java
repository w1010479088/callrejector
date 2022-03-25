package com.bruceewu.callrejector;

import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Switch;

import com.bruceewu.callrejector.business.CallFilter;
import com.bruceewu.callrejector.utils.LogUtils;
import com.bruceewu.callrejector.utils.SharePreferenceUtils;
import com.bruceewu.callrejector.utils.ToastUtils;
import com.google.gson.Gson;
import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        ((Switch) findViewById(R.id.open)).setChecked(SharePreferenceUtils.needInterrupt());
        ((Switch) findViewById(R.id.open)).setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharePreferenceUtils.setInterrupt(isChecked);
        });
        checkNormalPermission(() -> checkSinglePermission(() -> {
            LogUtils.log("权限授予成功！");
            CallFilter.getInstance().add("175");
            CallFilter.getInstance().del("151");
            CallFilter.getInstance().add("898");
            LogUtils.log(new Gson().toJson(CallFilter.getInstance().get()));
        }));
    }

    @SuppressLint("CheckResult")
    private void checkNormalPermission(Runnable next) {
        new RxPermissions(this)
                .request(
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.READ_CALL_LOG,
                        Manifest.permission.CALL_PHONE,
                        Manifest.permission.PROCESS_OUTGOING_CALLS
                )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(grant -> {
                    if (grant) {
                        next.run();
                    } else {
                        ToastUtils.show("获取权限失败！");
                    }
                }, error -> {
                    ToastUtils.show(error.getMessage());
                });
    }

    @SuppressLint("CheckResult")
    private void checkSinglePermission(Runnable next) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            new RxPermissions(this)
                    .request(Manifest.permission.ANSWER_PHONE_CALLS
                    )
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(grant -> {
                        if (grant) {
                            next.run();
                        } else {
                            ToastUtils.show("获取权限失败！");
                        }
                    }, error -> {
                        ToastUtils.show(error.getMessage());
                    });
        } else {
            next.run();
        }
    }
}