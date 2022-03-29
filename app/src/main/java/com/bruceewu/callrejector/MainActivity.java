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
import com.bruceewu.callrejector.utils.SharePreferenceUtils;
import com.bruceewu.callrejector.utils.ToastUtils;
import com.bruceewu.configor.RecyclerViewConfigor;
import com.bruceewu.configor.entity.DisplayItem;
import com.bruceewu.configor.holder.DefaultHolders;
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
        initPreFix();
        initList();
    }

    private void initView() {
        Runnable errorAction = () -> {
            SharePreferenceUtils.setInterrupt(false);
            refreshSwitch();
        };
        refreshSwitch();
        ((Switch) findViewById(R.id.open)).setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharePreferenceUtils.setInterrupt(isChecked);
            if (isChecked) {
                checkNormalPermission(() -> checkSinglePermission(() -> {
                }, errorAction), errorAction);
            }
        });
        findViewById(R.id.add).setOnClickListener(v -> add());
        findViewById(R.id.addPreFix).setOnClickListener(v -> addPreFix());
    }

    private void refreshSwitch() {
        ((Switch) findViewById(R.id.open)).setChecked(SharePreferenceUtils.needInterrupt());
    }


    //------------------------------通用前缀------------------------------

    private RecyclerViewConfigor configorPreFix;
    private final List<DisplayItem> uisPreFix = new ArrayList<>();

    private void initPreFix() {
        DisplayItem flowItem = DisplayItem.newItem(DefaultHolders.Flow.showType());
        flowItem.setChildren(uisPreFix);
        configorPreFix = new RecyclerViewConfigor
                .Builder()
                .buildRecyclerView(findViewById(R.id.listPreFix))
                .buildRefresh(false)
                .buildLoadMore(false)
                .buildScrollType(RecyclerViewConfigor.ScrollType.Vertical)
                .buildClickListener(this::itemClickPreFix)
                .build();
        List<DisplayItem> result = new ArrayList<>();
        result.add(flowItem);
        configorPreFix.set(result);
        refreshPreFix();
    }

    private void refreshPreFix() {
        uisPreFix.clear();
        List<String> items = CallFilter.getInstance().getPreFixs();
        for (String item : items) {
            uisPreFix.add(CardHolder.newInstance(item));
        }
        configorPreFix.refresh();
    }

    private void itemClickPreFix(DisplayItem item) {
        DialogHelper.show(this,
                String.format("将%s从通用前缀中移除？", item.showData()),
                () -> {
                    CallFilter.getInstance().delPreFix(item.showData());
                    refreshPreFix();
                });
    }

    private void addPreFix() {
        DialogHelper.add(this, input -> {
            CallFilter.getInstance().addPreFix(input);
            refreshPreFix();
        });
    }

    //------------------------------黑名单相关------------------------------

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
        List<String> items = CallFilter.getInstance().get();
        for (String item : items) {
            uis.add(CardHolder.newInstance(item));
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

    //--------------------------------------权限相关------------------------------------------

    @SuppressLint("CheckResult")
    private void checkNormalPermission(Runnable next, Runnable error) {
        new RxPermissions(this)
                .request(
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.READ_CALL_LOG,
                        Manifest.permission.CALL_PHONE
                )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(grant -> {
                    if (grant) {
                        next.run();
                    } else {
                        error.run();
                        ToastUtils.show("获取权限失败！");
                    }
                }, err -> {
                    error.run();
                    ToastUtils.show(err.getMessage());
                });
    }

    @SuppressLint("CheckResult")
    private void checkSinglePermission(Runnable next, Runnable error) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            new RxPermissions(this)
                    .request(Manifest.permission.ANSWER_PHONE_CALLS
                    )
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(grant -> {
                        if (grant) {
                            next.run();
                        } else {
                            error.run();
                            ToastUtils.show("获取权限失败！");
                        }
                    }, err -> {
                        error.run();
                        ToastUtils.show(err.getMessage());
                    });
        } else {
            next.run();
        }
    }
}