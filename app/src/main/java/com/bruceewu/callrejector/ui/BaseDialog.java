package com.bruceewu.callrejector.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatDialog;
import android.view.Gravity;
import android.view.WindowManager;

import com.bruceewu.callrejector.R;
import com.bruceewu.callrejector.utils.ScreenUtils;
import com.bruceewu.configor.helper.ViewHelper;

public abstract class BaseDialog extends AppCompatDialog {
    protected ViewHelper mHelper;

    public BaseDialog(@NonNull Context context) {
        super(context, R.style.BaseDialog);
    }

    protected abstract int getLayoutID();

    protected void initView() {

    }

    protected boolean bottom() {
        return false;
    }

    protected abstract float widthScale();

    protected int animation() {
        return 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutID());

        if (animation() != 0) {
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.windowAnimations = animation();
            getWindow().setAttributes(params);
        }

        if (bottom()) {
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.gravity = Gravity.BOTTOM;
            getWindow().setAttributes(params);
        }
        float widthScale = widthScale();
        if (widthScale != 0) {
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.width = (int) (ScreenUtils.getScreenWidth() * widthScale());
            getWindow().setAttributes(params);
        }

        mHelper = new ViewHelper(getWindow().getDecorView());
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        initView();
    }
}
