package com.bruceewu.callrejector.ui;

import com.bruceewu.configor.entity.HolderEntity;

import java.util.ArrayList;
import java.util.List;

public enum AppHolders {
    Card("card", CardHolder.class);

    private final String showType;
    private final Class clazz;

    public String showType() {
        return showType;
    }

    AppHolders(String showType, Class clazz) {
        this.showType = showType;
        this.clazz = clazz;
    }

    public static List<HolderEntity> holders() {
        List<HolderEntity> result = new ArrayList<>();
        for (AppHolders holder : values()) {
            result.add(new HolderEntity(holder.showType, holder.clazz));
        }
        return result;
    }
}
