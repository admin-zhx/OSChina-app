package net.oschina.gitapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import net.oschina.gitapp.AppContext;
import net.oschina.gitapp.R;
import net.oschina.gitapp.common.FileUtils;
import net.oschina.gitapp.common.MethodsCompat;
import net.oschina.gitapp.common.UIHelper;
import net.oschina.gitapp.common.UpdateManager;
import net.oschina.gitapp.ui.baseactivity.BaseActivity;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by 火蚁 on 15/4/29.
 */
public class SettingActivity extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    @InjectView(R.id.cb_receive_notice)
    CheckBox cbReceiveNotice;
    @InjectView(R.id.cb_notice_vioce)
    CheckBox cbNoticeVioce;
    @InjectView(R.id.cb_check_update_start)
    CheckBox cbCheckUpdateStart;
    @InjectView(R.id.tv_cache_size)
    TextView tvCacheSize;

    private AppContext appContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.inject(this);
        initDate();
    }

    private void initDate() {
        appContext = AppContext.getInstance();
        cbReceiveNotice.setChecked(appContext.isReceiveNotice());
        cbNoticeVioce.setChecked(appContext.isVoice());
        cbCheckUpdateStart.setChecked(appContext.isCheckUp());

        tvCacheSize.setText(calCache());

        cbReceiveNotice.setOnCheckedChangeListener(this);
    }

    @Override
    @OnClick({R.id.ll_receive_notice, R.id.ll_notice_voice, R.id.ll_check_update_start,
            R.id.ll_feedback, R.id.ll_clear_cache, R.id.ll_check_update, R.id.ll_about})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_receive_notice:
                updateReceiveNotice();
                break;
            case R.id.ll_notice_voice:

                updateNoticeVoice();
                break;
            case R.id.ll_check_update_start:
                updateCheckUpdateStart();
                break;
            case R.id.ll_feedback:
                onFeedBack();
                break;
            case R.id.ll_clear_cache:
                onCache();
                break;
            case R.id.ll_check_update:
                UpdateManager.getUpdateManager().checkAppUpdate(this, true);
                break;
            case R.id.ll_about:
                showAbout();
                break;
            default:
                break;
        }
    }

    private void updateReceiveNotice() {
        if (cbReceiveNotice.isChecked()) {
            cbReceiveNotice.setChecked(false);
        } else {
            cbReceiveNotice.setChecked(true);
        }
        appContext.setConfigReceiveNotice(cbReceiveNotice.isChecked());
    }

    private void updateNoticeVoice() {
        if (cbNoticeVioce.isChecked()) {
            cbNoticeVioce.setChecked(false);
        } else {
            cbNoticeVioce.setChecked(true);
        }
        appContext.setConfigReceiveNotice(cbNoticeVioce.isChecked());
    }

    private void updateCheckUpdateStart() {
        if (cbCheckUpdateStart.isChecked()) {
            cbCheckUpdateStart.setChecked(false);
        } else {
            cbCheckUpdateStart.setChecked(true);
        }
        appContext.setConfigCheckUp(cbCheckUpdateStart.isChecked());
    }

    private void onCache() {
        UIHelper.clearAppCache(SettingActivity.this);
        tvCacheSize.setText("OKB");
    }

    private String calCache() {
        long fileSize = 0;
        String cacheSize = "0KB";
        File filesDir = getFilesDir();
        File cacheDir = getCacheDir();

        fileSize += FileUtils.getDirSize(filesDir);
        fileSize += FileUtils.getDirSize(cacheDir);
        // 2.2版本才有将应用缓存转移到sd卡的功能
        if (AppContext.isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)) {
            File externalCacheDir = MethodsCompat.getExternalCacheDir(this);
            fileSize += FileUtils.getDirSize(externalCacheDir);
        }
        if (fileSize > 0)
            cacheSize = FileUtils.formatFileSize(fileSize);
        return cacheSize;
    }

    /**
     * 发送反馈意见到指定的邮箱
     */
    private void onFeedBack() {
        Intent i = new Intent(Intent.ACTION_SEND);
        //i.setType("text/plain"); //模拟器
        i.setType("message/rfc822"); //真机
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{"zhangdeyi@oschina.net"});
        i.putExtra(Intent.EXTRA_SUBJECT, "用户反馈-git@osc Android客户端");
        i.putExtra(Intent.EXTRA_TEXT, "");
        startActivity(Intent.createChooser(i, "send email to me..."));
    }

    private void showAbout() {
        Intent intent = new Intent(SettingActivity.this, AboutActivity.class);
        startActivity(intent);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.cb_receive_notice:
                appContext.setConfigReceiveNotice(isChecked);
                break;
            case R.id.cb_notice_vioce:
                appContext.setConfigVoice(isChecked);
                break;
            case R.id.cb_check_update_start:
                appContext.setConfigCheckUp(isChecked);
                break;
            default:
                break;
        }
    }
}
