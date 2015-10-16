/*
 * Copyright 2012 GitHub Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.oschina.gitapp.util;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.widget.TextView;

import net.oschina.gitapp.AppContext;

import java.util.Arrays;

/**
 * Helpers for dealing with custom typefaces and measuring text to display
 */
public class TypefaceUtils {

    private static Typeface OCTICONS;

    private static Typeface SEMANTIC;

    /**
     * Find the maximum number of digits in the given numbers
     *
     * @param numbers
     * @return max digits
     */
    public static int getMaxDigits(int... numbers) {
        int max = 1;
        for (int number : numbers)
            max = Math.max(max, (int) Math.log10(number) + 1);
        return max;
    }

    /**
     * Get width of number of digits
     *
     * @param view
     * @param numberOfDigits
     * @return number width
     */
    public static int getWidth(TextView view, int numberOfDigits) {
        Paint paint = new Paint();
        paint.setTypeface(view.getTypeface());
        paint.setTextSize(view.getTextSize());
        char[] text = new char[numberOfDigits];
        Arrays.fill(text, '0');
        return Math.round(paint.measureText(text, 0, text.length));
    }

    /**
     * Get octicons typeface
     *
     * @param context
     * @return octicons typeface
     */
    public static Typeface getOcticons(final Context context) {
        if (OCTICONS == null)
            OCTICONS = getTypeface(context, "octicons-regular-webfont.ttf");
        return OCTICONS;
    }

    public static Typeface getSemantic(final Context context) {
        if (SEMANTIC == null) {
            SEMANTIC = getTypeface(context, "icons.ttf");
        }

        return SEMANTIC;
    }

    public static Typeface getFontAwsome(final Context context) {
        return getTypeface(context, "fontawesome-webfont.ttf");
    }

    /**
     * Set octicons typeface on given text view(s)
     *
     * @param textViews
     */
    public static void setOcticons(final TextView... textViews) {
        if (textViews == null || textViews.length == 0)
            return;

        Typeface typeface = getOcticons(textViews[0].getContext());
        for (TextView textView : textViews)
            textView.setTypeface(typeface);
    }

    /**
     *
     * @param tv
     * @param text
     * @param iconRes
     */
    public static void setOcticons(final TextView tv, String text, int iconRes) {
        if (tv == null)
            return;
        Typeface typeface = getOcticons(tv.getContext());
        text = AppContext.getInstance().getResources().getString(iconRes) + " " + text;
        tv.setText(text);
        tv.setTypeface(typeface);
    }

    /**
     * 设置semantic的字体图标
     * @param textViews
     */
    public static void setSemantic(final TextView... textViews) {
        if (textViews == null || textViews.length == 0)
            return;

        Typeface typeface = getSemantic(textViews[0].getContext());
        for (TextView textView : textViews)
            textView.setTypeface(typeface);
    }

    /**
     *
     * @param tv
     * @param text
     * @param iconRes
     */
    public static void setSemantic(final TextView tv, String text, int iconRes) {
        if (tv == null)
            return;
        Typeface typeface = getSemantic(tv.getContext());
        text = AppContext.getInstance().getResources().getString(iconRes) + " " + text;
        tv.setText(text);
        tv.setTypeface(typeface);
    }

    /**
     * 设置fontAwsome的字体图标
     * @param textViews
     */
    public static void setFontAwsome(final TextView... textViews) {
        if (textViews == null || textViews.length == 0)
            return;
        Typeface typeface = getFontAwsome(textViews[0].getContext());
        for (TextView textView : textViews)
            textView.setTypeface(typeface);
    }

    /**
     *
     * @param tv
     * @param text
     * @param iconRes
     */
    public static void setFontAwsome(final TextView tv, String text, int iconRes) {
        if (tv == null)
            return;
        Typeface typeface = getFontAwsome(tv.getContext());
        text = AppContext.getInstance().getResources().getString(iconRes) + " " + text;
        tv.setText(text);
        tv.setTypeface(typeface);
    }

    /**
     * Get typeface with name
     *
     * @param context
     * @param name
     * @return typeface
     */
    public static Typeface getTypeface(final Context context, final String name) {
        return Typeface.createFromAsset(context.getAssets(), name);
    }
}
