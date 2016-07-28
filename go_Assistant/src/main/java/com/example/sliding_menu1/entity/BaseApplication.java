package com.example.sliding_menu1.entity;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;

import com.example.sliding_menu1.contants.Config;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.thinkland.juheapi.common.CommonFun;

import java.io.File;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;

/**
 * 工程名：Sliding_menu
 * 包名：com.example.sliding_menu1.entity
 * 作者： win
 * 创建日期：2016/5/4 23:22
 * 实现的主要功能：
 */
public class BaseApplication extends Application {
    private static BaseApplication baseApplication = null;
    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        baseApplication = this;
        //天气查询方面的
        CommonFun.initialize(getApplicationContext());
        context = getApplicationContext();
        initBmob();
        initImageLoader();
    }

    /**
     *
     * @Description: 初始化Bmob
     */
    private void initBmob() {
        // SDK初始化建议放在启动页
        Bmob.initialize(this, Config.APP_ID);
        // 初始化更新组件 仅第一次
        // BmobUpdateAgent.initAppVersion(this);
        // 使用推送服务时的初始化操作
        BmobInstallation.getCurrentInstallation(this).save();
    }

    public static Context getContext() {
        return context;
    }

    public static BaseApplication getInstance() {
        return baseApplication;
    }

    /**
     * 初始化imageLoader
     */
    public void initImageLoader() {
        File cacheDir = StorageUtils.getCacheDirectory(getApplicationContext());
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getApplicationContext())
                .memoryCache(new LruMemoryCache(5 * 1024 * 1024))
                .memoryCacheSize(10 * 1024 * 1024)
                .discCache(new UnlimitedDiscCache(cacheDir))
                .discCacheFileNameGenerator(new HashCodeFileNameGenerator())
                .build();
        ImageLoader.getInstance().init(config);
    }

    public DisplayImageOptions getOptions(int drawableId) {
        return new DisplayImageOptions.Builder().showImageOnLoading(drawableId)
                .showImageForEmptyUri(drawableId).showImageOnFail(drawableId)
                .resetViewBeforeLoading(true).cacheInMemory(true)
                .cacheOnDisc(true).imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
    }
}
