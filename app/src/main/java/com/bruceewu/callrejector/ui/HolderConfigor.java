package com.bruceewu.callrejector.ui;

import android.widget.ImageView;
import android.widget.TextView;

import com.bruceewu.callrejector.utils.LogUtils;
import com.bruceewu.callrejector.utils.ScreenUtils;
import com.bruceewu.configor.IConfigor;
import com.bruceewu.configor.entity.DisplayItem;
import com.bruceewu.configor.helper.ErrorLogger;

public class HolderConfigor {

    public static void init() {
        IConfigor.init(new IConfigor() {
            @Override
            public void showSingleImageText(TextView view, String imgUrl, String text, int start, int end) {

            }

            @Override
            public void loadImage(ImageView view, String url) {

            }

            @Override
            public void loadRoundImageByDp(ImageView view, String url, int radius) {

            }

            @Override
            public void setImageRTop(ImageView view, String url, int radiusDP) {

            }

            @Override
            public void clearImage(ImageView view) {

            }

            @Override
            public ErrorLogger getLogger() {
                return new ErrorLogger() {
                    @Override
                    public void log(Exception ex) {
                        LogUtils.log(ex.getMessage());
                    }

                    @Override
                    public void log(String content) {
                        LogUtils.log(content);
                    }
                };
            }

            @Override
            public int loadingSize() {
                return 0;
            }

            @Override
            public int dip2px(int dp) {
                return ScreenUtils.dip2px(dp);
            }

            @Override
            public int getScreenWidth() {
                return ScreenUtils.getScreenWidth();
            }

            @Override
            public int defaultBgColor() {
                return 0;
            }

            @Override
            public int defaultEmptyIcon() {
                return 0;
            }

            @Override
            public int colorIndicator() {
                return 0;
            }

            @Override
            public int colorUnselTabText() {
                return 0;
            }

            @Override
            public int colorSelTabText() {
                return 0;
            }

            @Override
            public void setSchemaor(DisplayItem item, Object schemaor) {

            }
        });
        IConfigor.config(AppHolders.holders());
    }
}
