package com.bruceewu.callrejector;

import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Switch;

import com.bruceewu.callrejector.business.CallFilter;
import com.bruceewu.callrejector.ui.CardHolder;
import com.bruceewu.callrejector.ui.DialogHelper;
import com.bruceewu.callrejector.utils.LogUtils;
import com.bruceewu.callrejector.utils.SharePreferenceUtils;
import com.bruceewu.callrejector.utils.ToastUtils;
import com.bruceewu.configor.RecyclerViewConfigor;
import com.bruceewu.configor.entity.DisplayItem;
import com.bruceewu.configor.holder.DefaultHolders;
import com.google.gson.Gson;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initList();
    }

    private void initView() {
        ((Switch) findViewById(R.id.open)).setChecked(SharePreferenceUtils.needInterrupt());
        ((Switch) findViewById(R.id.open)).setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharePreferenceUtils.setInterrupt(isChecked);
        });
        checkNormalPermission(() -> checkSinglePermission(() -> {
            LogUtils.log("权限授予成功！");
            LogUtils.log(new Gson().toJson(CallFilter.getInstance().get()));
        }));
        findViewById(R.id.add).setOnClickListener(v -> add());
    }

    private RecyclerViewConfigor configor;
    private final List<DisplayItem> uis = new ArrayList<>();

    private void initList() {
        DisplayItem flowItem = DisplayItem.newItem(DefaultHolders.Flow.showType());
        flowItem.setChildren(uis);
        configor = new RecyclerViewConfigor
                .Builder()
                .buildRecyclerView(findViewById(R.id.list))
                .buildRefresh(false)
                .buildLoadMore(false)
                .buildScrollType(RecyclerViewConfigor.ScrollType.Vertical)
                .buildClickListener(this::itemClick)
                .build();
        List<DisplayItem> result = new ArrayList<>();
        result.add(flowItem);
        configor.set(result);
        refresh();
    }

    private void refresh() {
        uis.clear();
        List<Integer> items = CallFilter.getInstance().get();
        for (Integer item : items) {
            uis.add(CardHolder.newInstance(String.valueOf(item)));
        }
        configor.refresh();
    }

    private void itemClick(DisplayItem item) {
        DialogHelper.show(this,
                String.format("将%s从黑明单中移除？", item.showData()),
                () -> {
                    CallFilter.getInstance().del(item.showData());
                    refresh();
                });
    }

    private void add() {
        DialogHelper.add(this, input -> {
            CallFilter.getInstance().add(input);
            refresh();
        });
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