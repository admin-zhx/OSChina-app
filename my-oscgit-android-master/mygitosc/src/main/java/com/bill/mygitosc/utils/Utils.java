package com.bill.mygitosc.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.bill.mygitosc.R;
import com.bill.mygitosc.common.BitmapCache;
import com.bill.mygitosc.ui.BaseActivity;

/**
 * Created by liaobb on 2015/7/24.
 */
public class Utils {


    public static void getPorTraitFormURL(Context context, ImageView imageView, String portraitURL) {
        if (!Utils.checkNoPicMode(context)) {
            RequestQueue mQueue = Volley.newRequestQueue(context);
            ImageLoader mImageLoader = new ImageLoader(mQueue, new BitmapCache());
            ImageLoader.ImageListener listener = ImageLoader.getImageListener(imageView,
                    R.drawable.mini_avatar, R.drawable.mini_avatar);
            mImageLoader.get(portraitURL, listener);
        }
    }

    public static boolean checkNoPicMode(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(BaseActivity.PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(context.getString(R.string.no_picture_mode_key), false);
    }
}
