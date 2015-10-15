package com.bill.mygitosc.cache;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Created by liaobb on 2015/8/3.
 */
public class DataCleanManager {

    /**
     * �����Ӧ���ڲ�����
     * (/data/data/com.xxx.xxx/cache)
     * @param context
     */
    public static void cleanInternalCache(Context context) {
        deleteFilesByDirectory(context.getCacheDir());
        deleteFilesByDirectory(context.getFilesDir());
    }

    /**
     * �����Ӧ���������ݿ�
     * (/data/data/com.xxx.xxx/databases)
     * @param context
     */
    public static void cleanDatabases(Context context) {
        deleteFilesByDirectory(new File("/data/data/"
                + context.getPackageName() + "/databases"));
    }

    /**
     * �����Ӧ��SharedPreference
     * (/data/data/com.xxx.xxx/shared_prefs)
     * @param context
     */
    public static void cleanSharedPreference(Context context) {
        deleteFilesByDirectory(new File("/data/data/"
                + context.getPackageName() + "/shared_prefs"));
    }

    /**
     * ����ⲿcache�µ�����(/mnt/sdcard/android/data/com.xxx.xxx/cache)
     * @param context
     */
    public static void cleanExternalCache(Context context) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            deleteFilesByDirectory(context.getExternalCacheDir());
        }
    }

    /**
     * ����Զ���·���µ��ļ���ʹ����С�ģ��벻Ҫ��ɾ������ֻ֧��Ŀ¼�µ��ļ�ɾ��
     * @param file
     */
    public static void cleanCustomCache(File file) {
        deleteFilesByDirectory(file);
    }

    /**
     * ɾ������ ����ֻ��ɾ��ĳ���ļ����µ��ļ�����������directory�Ǹ��ļ�������������
     * @param directory
     */
    private static void deleteFilesByDirectory(File directory) {
        if (directory != null && directory.exists() && directory.isDirectory()) {
            for (File child : directory.listFiles()) {
                if (child.isDirectory()) {
                    deleteFilesByDirectory(child);
                }
                child.delete();
            }
        }
    }
}
