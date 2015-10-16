package net.oschina.gitapp.util;

import android.view.View;
import android.widget.Toast;

import net.oschina.gitapp.AppContext;

/**
 * Created by 火蚁 on 15/4/16.
 */
public class GitViewUtils {

    // 通过一个viewId来获取一个view
    public static <T extends View> T findViewById(View container, int viewId) {
        return (T)container.findViewById(viewId);
    }

    public static void showToast(String msg) {
        Toast.makeText(AppContext.getInstance(), msg, Toast.LENGTH_LONG).show();
    }

    public static void showToast(int msgRes) {
        AppContext appContext = AppContext.getInstance();
        Toast.makeText(appContext, appContext.getResources().getString(msgRes), Toast.LENGTH_LONG).show();
    }
}
