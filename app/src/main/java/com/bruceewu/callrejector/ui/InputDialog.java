package com.bruceewu.callrejector.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.webkit.ValueCallback;
import android.widget.EditText;

import com.bruceewu.callrejector.R;
import com.bruceewu.callrejector.utils.ToastUtils;

public class InputDialog extends BaseDialog {

    public static void show(Context context, ValueCallback<String> listener) {
        InputDialog dialog = new InputDialog(context);
        dialog.show();
        dialog.listener = listener;
    }

    private ValueCallback<String> listener;

    public InputDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getLayoutID() {
        return R.layout.dialog_add;
    }

    @Override
    protected float widthScale() {
        return 0.8F;
    }

    @Override
    protected void initView() {
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        mHelper.setClick(R.id.confirm, () -> {
            EditText inputView = mHelper.getView(R.id.input);
            String input = inputView.getText().toString();
            if (!TextUtils.isEmpty(input)) {
                dismiss();
                listener.onReceiveValue(input);
            } else {
                ToastUtils.show("请输入...");
            }
        });
    }
}
