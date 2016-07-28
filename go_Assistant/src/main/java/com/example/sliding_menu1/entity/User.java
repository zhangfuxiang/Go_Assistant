package com.example.sliding_menu1.entity;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;

/**
 * 工程名：Sliding_menu
 * 包名：com.example.sliding_menu1.entity
 * 作者： win
 * 创建日期：2016/5/3 14:15
 * 实现的主要功能：
 */
public class User extends BmobUser {
    private String name;//昵称
    private Boolean sex;//性别
    /**
     * @Description: 头像文件
     */
    private BmobFile avatar;
    //需要用到密码
    private String password_need;
    public String getPassword_need() {
        return password_need;
    }

    public void setPassword_need(String password_need) {
        this.password_need = password_need;
    }






    public BmobFile getAvatar() {
        return avatar;
    }

    public void setAvatar(BmobFile avatar) {
        this.avatar = avatar;
    }

    public Boolean getSex() {
        return sex;
    }

    public void setSex(Boolean sex) {
        this.sex = sex;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
