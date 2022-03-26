package com.bruceewu.callrejector.business;

import android.text.TextUtils;

import com.bruceewu.callrejector.utils.LogUtils;
import com.bruceewu.callrejector.utils.SharePreferenceUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class CallFilter {

    private final List<String> tags = new ArrayList<>();
    private final List<String> preFixs = new ArrayList<>();//号码前缀，无效的号段，需要过滤一下

    private static class HOLDER {
        private static final CallFilter INSTANCE = new CallFilter();
    }

    public static CallFilter getInstance() {
        return HOLDER.INSTANCE;
    }

    private CallFilter() {
        tryCatch(() -> {
            {//preFixs
                String preFix = SharePreferenceUtils.getCallPreFix();
                if (!TextUtils.isEmpty(preFix)) {
                    List<String> temps = new Gson().fromJson(preFix, new TypeToken<List<String>>() {
                    }.getType());
                    if (!temps.isEmpty()) {
                        preFixs.clear();
                        preFixs.addAll(temps);
                    }
                }
            }

            {//黑名单
                String callList = SharePreferenceUtils.getCallList();
                if (!TextUtils.isEmpty(callList)) {
                    List<String> temps = new Gson().fromJson(callList, new TypeToken<List<String>>() {
                    }.getType());
                    if (!temps.isEmpty()) {
                        tags.clear();
                        tags.addAll(temps);
                    }
                }
            }
        });
    }

    public static void filter(String mobile, Runnable next) {
        if (CallFilter.getInstance().has(mobile)) {
            next.run();
        }
    }

    public List<String> get() {
        return tags;
    }

    public List<String> getPreFixs() {
        return preFixs;
    }

    //是否指定前缀开头
    public boolean has(String mobile) {
        if (!TextUtils.isEmpty(mobile)) {
            String fixedMobile = fix(mobile);
            if (!TextUtils.isEmpty(fixedMobile)) {
                for (String tag : tags) {
                    if (fixedMobile.startsWith(tag)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void add(String value) {
        tryCatch(() -> {
            String tag = fix(value);
            if (!TextUtils.isEmpty(tag) && !tags.contains(tag)) {
                tags.add(tag);
                SharePreferenceUtils.setCallList(new Gson().toJson(tags));
            }
        });
    }

    public void addPreFix(String value) {
        tryCatch(() -> {
            if (!TextUtils.isEmpty(value) && !preFixs.contains(value)) {
                preFixs.add(value);
                SharePreferenceUtils.setCallPreFix(new Gson().toJson(preFixs));
            }
        });
    }

    public void del(String value) {
        tryCatch(() -> {
            String tag = fix(value);
            if (!TextUtils.isEmpty(tag) && tags.remove(tag)) {
                SharePreferenceUtils.setCallList(new Gson().toJson(tags));
            }
        });
    }

    public void delPreFix(String value) {
        tryCatch(() -> {
            if (!TextUtils.isEmpty(value) && preFixs.remove(value)) {
                SharePreferenceUtils.setCallPreFix(new Gson().toJson(preFixs));
            }
        });
    }

    //过滤无效的前缀，+,86,0，得到一个正常的号码
    private String fix(String value) {
        try {
            if (!TextUtils.isEmpty(value)) {
                String tag = value;
                for (String preFix : preFixs) {
                    if (tag.startsWith(preFix)) {
                        tag = tag.replaceFirst(preFix, "");
                    }
                }
                return tag;
            }
        } catch (Exception ex) {
            LogUtils.log(ex.getMessage());
        }
        return null;
    }

    private void tryCatch(Runnable next) {
        try {
            next.run();
        } catch (Exception ex) {
            LogUtils.log(ex.getMessage());
        }
    }
}
