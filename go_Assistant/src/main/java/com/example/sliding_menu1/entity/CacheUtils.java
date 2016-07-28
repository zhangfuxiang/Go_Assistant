package com.example.sliding_menu1.entity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import static android.os.Environment.MEDIA_MOUNTED;

/**
 * 工程名：Sliding_menu
 * 包名：com.example.sliding_menu1.entity
 * 作者： win
 * 创建日期：2016/5/4 22:45
 * 实现的主要功能：缓存工具类
 */
public class CacheUtils {
    private static final String TAG = "CacheUtils";
    private static final String EXTERNAL_STORAGE_PERMISSION = "android.permission.WRITE_EXTERNAL_STORAGE";

    /**
     * 获取/data/data/files目录
     *
     * @param context
     * @return
     */
    @SuppressLint("SdCardPath")
    public static File getFileDirectory(Context context) {
        File appCacheDir = null;
        if (appCacheDir == null) {
            appCacheDir = context.getFilesDir();
        }
        if (appCacheDir == null) {
            String cacheDirPath = "/data/data/" + context.getPackageName()
                    + "/files/";
            appCacheDir = new File(cacheDirPath);
        }
        return appCacheDir;
    }

    @SuppressLint("SdCardPath")
    public static File getCacheDirectory(Context context,
                                         boolean preferExternal, String dirName) {
        File appCacheDir = null;
        if (preferExternal
                && MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                && hasExternalStoragePermission(context)) {
            appCacheDir = getExternalCacheDir(context, dirName);
        }
        if (appCacheDir == null) {
            appCacheDir = context.getCacheDir();
        }
        if (appCacheDir == null) {
            String cacheDirPath = "/data/data/" + context.getPackageName()
                    + "/cache/";
            Log.w("Can't define system cache directory! '%s' will be used.",
                    cacheDirPath);
            appCacheDir = new File(cacheDirPath);
        }
        return appCacheDir;
    }

    private static File getExternalCacheDir(Context context, String dirName) {
        File dataDir = new File(new File(
                Environment.getExternalStorageDirectory(), "Android"), "data");
        File appCacheDir2 = new File(
                new File(dataDir, context.getPackageName()), "cache");
        File appCacheDir = new File(appCacheDir2, dirName);
        if (!appCacheDir.exists()) {
            if (!appCacheDir.mkdirs()) {
                return null;
            }
            try {
                new File(appCacheDir, ".nomedia").createNewFile();
            } catch (IOException e) {

            }
        }
        return appCacheDir;
    }

    private static boolean hasExternalStoragePermission(Context context) {
        int perm = context
                .checkCallingOrSelfPermission(EXTERNAL_STORAGE_PERMISSION);
        return perm == PackageManager.PERMISSION_GRANTED;
    }
}
