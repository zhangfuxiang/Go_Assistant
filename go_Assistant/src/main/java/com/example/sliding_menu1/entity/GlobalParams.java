package com.example.sliding_menu1.entity;

import android.app.Activity;

import cn.bmob.v3.BmobUser;

/**
 * 工程名：Sliding_menu
 * 包名：com.example.sliding_menu1.entity
 * 作者： win
 * 创建日期：2016/5/4 20:09
 * 实现的主要功能：
 */
public class GlobalParams {
    public static User userInfo;
    public static Activity context;
    public void init(Activity activity) {
        if (userInfo != null) {
            return;
        }
        userInfo = BmobUser.getCurrentUser(activity, User.class);
        context = activity;
    }
}
