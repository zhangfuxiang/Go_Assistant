package com.example.sliding_menu1.entity;

/**
 * 工程名：Sliding_menu
 * 包名：com.example.sliding_menu1.entity
 * 作者： win
 * 创建日期：2016/5/4 22:40
 * 实现的主要功能：
 */
public class CommonConstant {
    // ---------------------------------更改头像------------------------------------//
    /**
     * @Description: 设置页面访问相机的请求码
     */
    public static final int REQUESTCODE_PHOTO_CAMERA = 103;
    /**
     * @Description: 设置页面访问相册的请求码
     */
    public static final int REQUESTCODE_PHOTO_ALBUM = 104;
    /**
     * @Description: 设置页面切图的请求码
     */
    public static final int REQUESTCODE_PHOTO_CUT = 105;

    // ---------------------------------保存属性------------------------------------//
    /**
     * @Description: SharedPreferences存储的文件名
     */
    public static final String SP_FAIL_NAME = "ZHAOWO_SP";
    /**
     * @Description: SharedPreferences存储是否记住密码的key
     */
    public static final String REMBER_PWD_KEY = "REMBER_PWD";
    /**
     * @Description: SharedPreferences存储是否自动登录的key
     */
    public static final String AUTO_LOGIN_KEY = "AUTO_LOGIN";
    /**
     * @Description: SharedPreferences存储username的key
     */
    public static final String USERNAME_KEY = "USERNAME";
    /**
     * @Description: SharedPreferences存储nick的key
     */
    public static final String NICK_KEY = "NICK";
    /**
     * @Description: SharedPreferences存储password的key
     */
    public static final String PASSWORD_KEY = "PASSWORD";
    // ---------------------------------更改昵称------------------------------------//
    /**
     * @Description: 设置界面传到更改昵称界面的请求码
     */
    public static final int REQUESTCODE_EDIT_NICK = 100;
    /**
     * @Description: 更改昵称界面传到设置界面的结果码
     */
    public static final int RESULTCODE_EDIT_NICK_OK = 101;
    /**
     * @Description: 更改昵称界面传到设置界面的结果码
     */
    public static final int RESULTCODE_EDIT_NICK_CANCEL = 102;
}
