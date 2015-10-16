package net.oschina.gitapp.ui.baseactivity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.View;
import android.view.Window;

import com.umeng.analytics.MobclickAgent;

import net.oschina.gitapp.AppManager;
import net.oschina.gitapp.common.StringUtils;

import java.lang.reflect.Method;

public abstract class BaseActivity extends ActionBarActivity {

	// 是否可以返回
	protected static boolean isCanBack;
	
	protected ActionBar mActionBar;

	protected String mTitle;
	
	protected String mSubTitle;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initActionBar();
		//将activity加入到AppManager堆栈中
		AppManager.getAppManager().addActivity(this);
	}

	// 关闭该Activity
	@Override
	public boolean onSupportNavigateUp() {
		finish();
		return super.onSupportNavigateUp();
	}
	
	// 初始化ActionBar
	private void initActionBar() {
		mActionBar = getSupportActionBar();
		int flags = ActionBar.DISPLAY_HOME_AS_UP;
		int change = mActionBar.getDisplayOptions() ^ flags;
		// 设置返回的图标
		mActionBar.setDisplayOptions(change, flags);
        if (mTitle != null && !StringUtils.isEmpty(mTitle)) {
            mActionBar.setTitle(mTitle);
        }
        if (mSubTitle != null && !StringUtils.isEmpty(mSubTitle)) {
            mActionBar.setSubtitle(mSubTitle);
        }
	}

    public void setActionBarTitle(String title) {
        mActionBar.setTitle(title);
    }

    public void setActionBarSubTitle(String subTitle) {
        mActionBar.setSubtitle(subTitle);
    }

    protected <T extends View> T findView(int id) {
        return (T)findViewById(id);
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        //setOverflowIconVisible(featureId, menu);
        return super.onMenuOpened(featureId, menu);
    }

    // 显示出菜单的icon
    protected void setOverflowIconVisible(int featureId, Menu menu) {
        if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {

                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
