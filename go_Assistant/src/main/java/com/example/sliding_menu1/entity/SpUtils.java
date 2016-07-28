package com.example.sliding_menu1.entity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * 工程名：Sliding_menu
 * 包名：com.example.sliding_menu1.entity
 * 作者： win
 * 创建日期：2016/5/5 16:30
 * 实现的主要功能：SharedPreferences工具类
 */
public class SpUtils {
    private Context context;
    private SharedPreferences sp = null;
    private SharedPreferences.Editor edit = null;

    /**
     * @Description: 创建默认Sp
     * @param: context
     */
    public SpUtils(Context context) {
        this(context, PreferenceManager.getDefaultSharedPreferences(context));
    }

    /**
     * @Description: 通过文件名创建sp
     *
     * @param context
     * @param filename
     */
    @SuppressLint("WorldWriteableFiles")
    @SuppressWarnings("deprecation")
    public SpUtils(Context context, String filename) {
        this(context, context.getSharedPreferences(filename,
                Context.MODE_WORLD_WRITEABLE));
    }

    /**
     * @Description: 通过sp创建sp
     * @param context
     * @param sp
     */
    public SpUtils(Context context, SharedPreferences sp) {
        this.context = context;
        this.sp = sp;
        edit = sp.edit();
    }

    public SharedPreferences getInstance() {
        return sp;
    }

    // Set

    // Boolean
    public void setValue(String key, boolean value) {
        edit.putBoolean(key, value);
        edit.commit();
    }

    public void setValue(int resKey, boolean value) {
        setValue(this.context.getString(resKey), value);
    }

    // Float
    public void setValue(String key, float value) {
        edit.putFloat(key, value);
        edit.commit();
    }

    public void setValue(int resKey, float value) {
        setValue(this.context.getString(resKey), value);
    }

    // Integer
    public void setValue(String key, int value) {
        edit.putInt(key, value);
        edit.commit();
    }

    public void setValue(int resKey, int value) {
        setValue(this.context.getString(resKey), value);
    }

    // Long
    public void setValue(String key, long value) {
        edit.putLong(key, value);
        edit.commit();
    }

    public void setValue(int resKey, long value) {
        setValue(this.context.getString(resKey), value);
    }

    // String
    public void setValue(String key, String value) {
        edit.putString(key, value);
        edit.commit();
    }

    public void setValue(int resKey, String value) {
        setValue(this.context.getString(resKey), value);
    }

    // Get

    // Boolean
    public boolean getValue(String key, boolean defaultValue) {
        return sp.getBoolean(key, defaultValue);
    }

    public boolean getValue(int resKey, boolean defaultValue) {
        return getValue(this.context.getString(resKey), defaultValue);
    }

    // Float
    public float getValue(String key, float defaultValue) {
        return sp.getFloat(key, defaultValue);
    }

    public float getValue(int resKey, float defaultValue) {
        return getValue(this.context.getString(resKey), defaultValue);
    }

    // Integer
    public int getValue(String key, int defaultValue) {
        return sp.getInt(key, defaultValue);
    }

    public int getValue(int resKey, int defaultValue) {
        return getValue(this.context.getString(resKey), defaultValue);
    }

    // Long
    public long getValue(String key, long defaultValue) {
        return sp.getLong(key, defaultValue);
    }

    public long getValue(int resKey, long defaultValue) {
        return getValue(this.context.getString(resKey), defaultValue);
    }

    // String
    public String getValue(String key, String defaultValue) {
        return sp.getString(key, defaultValue);
    }

    public String getValue(int resKey, String defaultValue) {
        return getValue(this.context.getString(resKey), defaultValue);
    }

    // Delete
    public void remove(String key) {
        edit.remove(key);
        edit.commit();
    }

    // Clear
    public void clear() {
        edit.clear();
        edit.commit();
    }
}
