package com.bruceewu.callrejector.ui;

import android.view.View;

import com.bruceewu.callrejector.R;
import com.bruceewu.configor.entity.CusOnClickListener;
import com.bruceewu.configor.entity.DisplayItem;
import com.bruceewu.configor.holder.base.CusBaseHolder;

public class CardHolder extends CusBaseHolder {

    public static DisplayItem newInstance(String title) {
        DisplayItem item = DisplayItem.newItem(AppHolders.Card.showType());
        item.setShowData(title);
        return item;
    }

    public CardHolder(View rootView) {
        super(rootView);
    }

    @Override
    public int layoutID() {
        return R.layout.holder_card;
    }

    @Override
    public void renderUI(DisplayItem item, CusOnClickListener listener) {
        mHelper.setText(R.id.title, item.showData());
        getRootView().setOnLongClickListener(v -> {
            listener.onClick(item);
            return true;
        });
    }
}
