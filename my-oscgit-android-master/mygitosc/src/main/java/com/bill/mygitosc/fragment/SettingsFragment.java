package com.bill.mygitosc.fragment;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.text.TextUtils;
import android.widget.Toast;

import com.bill.mygitosc.R;
import com.bill.mygitosc.cache.DataCleanManager;
import com.bill.mygitosc.common.AppContext;
import com.bill.mygitosc.ui.SettingActivity;
import com.bill.mygitosc.utils.FileUtils;
import com.jenzz.materialpreference.CheckBoxPreference;
import com.jenzz.materialpreference.Preference;
import com.jenzz.materialpreference.SwitchPreference;

import java.io.File;
import java.util.List;

/**
 * Created by liaobb on 2015/7/8.
 */
public class SettingsFragment extends PreferenceFragment {
    private Context context;

    private SwitchPreference rightHandModeSwitch;
    private CheckBoxPreference noPictureModeCheckBox;
    private Preference clearCachePreference;

    private Preference feedbackPreference;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private boolean rightHandMode;
    private boolean noPictureMode;

    public SettingsFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs);

        context = getActivity();
        sharedPreferences = context.getSharedPreferences(SettingActivity.PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        rightHandMode = sharedPreferences.getBoolean(getString(R.string.right_hand_mode_key), false);
        rightHandModeSwitch = (SwitchPreference) findPreference(getString(R.string.right_hand_mode_key));
        rightHandModeSwitch.setChecked(rightHandMode);

        noPictureMode = sharedPreferences.getBoolean(getString(R.string.no_picture_mode_key), false);
        noPictureModeCheckBox = (CheckBoxPreference) findPreference(getString(R.string.no_picture_mode_key));
        noPictureModeCheckBox.setChecked(noPictureMode);

        feedbackPreference = (Preference) findPreference(getString(R.string.advice_feedback_key));
        initFeedbackPreference();

        clearCachePreference = (Preference) findPreference(getString(R.string.clear_cache_data_key));
        initCachePreference();
    }


    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, android.preference.Preference preference) {
        if (preference == null)
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        String key = preference.getKey();
        if (TextUtils.equals(key, getString(R.string.right_hand_mode_key))) {
            rightHandMode = !rightHandMode;
            editor.putBoolean(getString(R.string.right_hand_mode_key), rightHandMode).commit();
        } else if (TextUtils.equals(key, getString(R.string.no_picture_mode_key))) {
            noPictureMode = !noPictureMode;
            editor.putBoolean(getString(R.string.no_picture_mode_key), noPictureMode).commit();
        } else if (TextUtils.equals(key, getString(R.string.clear_cache_data_key))) {
            clearAppCache();
        } else if (TextUtils.equals(key, getString(R.string.give_favor_key))) {
            giveFavor();
        }

        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    private void initCachePreference() {
        long fileSize = 0;
        String cacheSize = "0KB";
        File filesDir = getActivity().getFilesDir();
        File cacheDir = getActivity().getCacheDir();

        fileSize += FileUtils.getDirSize(filesDir);
        fileSize += FileUtils.getDirSize(cacheDir);
        // 2.2版本才有将应用缓存转移到sd卡的功能
        if (AppContext.isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)) {
            File externalCacheDir = getActivity().getExternalCacheDir();
            fileSize += FileUtils.getDirSize(externalCacheDir);
        }
        if (fileSize > 0)
            cacheSize = FileUtils.formatFileSize(fileSize);
        clearCachePreference.setSummary(cacheSize);
    }

    private void initFeedbackPreference() {
        Uri uri = Uri.parse("mailto:1250440341@qq.com");
        final Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        PackageManager pm = getActivity().getPackageManager();
        List<ResolveInfo> infos = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (infos == null || infos.size() <= 0) {
            feedbackPreference.setSummary(getString(R.string.no_email_app_tip));
            return;
        }
        feedbackPreference.setOnPreferenceClickListener(new android.preference.Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(android.preference.Preference preference) {
                startActivity(intent);
                return true;
            }
        });
    }

    private void giveFavor() {
        try {
            Uri uri = Uri.parse("market://details?id=" + getActivity().getPackageName());
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void clearAppCache() {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    clearCachePreference.setSummary("0KB");
                    Toast.makeText(getActivity(), getString(R.string.clear_cache_success), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.clear_cache_fail), Toast.LENGTH_SHORT).show();
                }
            }
        };
        new Thread() {
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    DataCleanManager.cleanDatabases(getActivity());
                    // 清除数据缓存
                    DataCleanManager.cleanInternalCache(getActivity());
                    // 2.2版本才有将应用缓存转移到sd卡的功能
                    if (AppContext.isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)) {
                        DataCleanManager.cleanCustomCache(getActivity().getExternalCacheDir());
                    }
                    msg.what = 1;
                } catch (Exception e) {
                    e.printStackTrace();
                    msg.what = -1;
                }
                handler.sendMessage(msg);
            }
        }.start();
    }
}
