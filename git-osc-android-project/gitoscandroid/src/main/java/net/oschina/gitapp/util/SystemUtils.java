package net.oschina.gitapp.util;

import android.app.Activity;
import android.util.DisplayMetrics;

/**
 * Created by 火蚁 on 15/5/4.
 */
public class SystemUtils {

    /***
     * 获取activity的宽度
     * @param activity
     * @return
     */
    public static int getWidthPixels(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    /***
     * 获取activity的高度
     * @param activity
     * @return
     */
    public static int getHeightPixels(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }
}
