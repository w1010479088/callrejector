package com.bruceewu.callrejector.business;

import android.text.TextUtils;

import com.bruceewu.callrejector.utils.LogUtils;
import com.bruceewu.callrejector.utils.SharePreferenceUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CallFilter {

    private final List<Integer> tags = new ArrayList<>();
    private final List<String> preFixs = new ArrayList<>();//号码前缀，无效的号段，需要过滤一下

    private static class HOLDER {
        private static final CallFilter INSTANCE = new CallFilter();
    }

    public static CallFilter getInstance() {
        return HOLDER.INSTANCE;
    }

    private CallFilter() {
        try {
            {
                preFixs.add("+");
                preFixs.add("86");
                preFixs.add("0");
            }

            {
                String callList = SharePreferenceUtils.getCallList();
                if (!TextUtils.isEmpty(callList)) {
                    List<Integer> temps = new Gson().fromJson(callList, new TypeToken<List<Integer>>() {
                    }.getType());
                    if (temps.size() != 0) {
                        tags.clear();
                        tags.addAll(temps);
                    }
                }
            }
        } catch (Exception ex) {
            LogUtils.log(ex.getMessage());
        }
    }

    public static void filter(String mobile, Runnable next) {
        if (CallFilter.getInstance().has(mobile)) {
            next.run();
        }
    }

    //升序一下
    public List<Integer> get() {
        Collections.sort(tags, (o1, o2) -> o1 - o2);
        return tags;
    }

    //是否指定前缀开头
    public boolean has(String mobile) {
        if (!TextUtils.isEmpty(mobile)) {
            String fixedMobile = fix(mobile);
            for (Integer tag : tags) {
                if (fixedMobile.startsWith(String.valueOf(tag))) {
                    return true;
                }
            }
        }
        return false;
    }

    public void add(String value) {
        try {
            Integer tag = Integer.parseInt(fix(value));
            if (tag != 0 && !tags.contains(tag)) {
                tags.add(tag);
                SharePreferenceUtils.setCallList(new Gson().toJson(tags));
            }
        } catch (Exception ex) {
            LogUtils.log(ex.getMessage());
        }
    }

    public void del(String value) {
        try {
            Integer tag = Integer.parseInt(fix(value));
            if (tag != 0 && tags.remove(tag)) {
                SharePreferenceUtils.setCallList(new Gson().toJson(tags));
            }
        } catch (Exception ex) {
            LogUtils.log(ex.getMessage());
        }
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
        return "0";
    }
}
