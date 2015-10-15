package com.bill.mygitosc.common;

import android.graphics.Typeface;
import android.widget.TextView;

/**
 * Created by liaobb on 2015/7/23.
 */
public class TypefaceUtils {
    private static Typeface typeface;

    private static Typeface getTypeface() {
        if (typeface == null) {
            typeface = Typeface.createFromAsset(AppContext.getInstance().getAssets(), "icons.ttf");
        }
        return typeface;
    }

    public static void setIconText(TextView tv_watch, String text) {
        tv_watch.setText(text);
        tv_watch.setTypeface(TypefaceUtils.getTypeface());
    }
}
