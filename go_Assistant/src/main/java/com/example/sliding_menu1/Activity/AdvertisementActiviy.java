package com.example.sliding_menu1.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Window;

import com.example.sliding_menu1.R;

/**
 * 工程名：Sliding_menu
 * 包名：com.example.sliding_menu1.Activity
 * 作者： win
 * 创建日期：2016/4/20 17:29
 * 实现的主要功能：开启程序时的广告页
 */
public class AdvertisementActiviy extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_advertisement);
        //倒计时方法
        //第一个参数表示总时间，第二个参数表示间隔时间
        new CountDownTimer(1000, 1000)
        {
            //倒计时时间结束后回调方法onFinish
            public void onFinish()
            {

                //启动界面淡入淡出效果
                Intent intent = new Intent();
                intent.setClass(AdvertisementActiviy.this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();

            }
            //每隔一个单位时间回调一次方法onTick
            public void onTick(long paramLong)
            {
            }
        }.start();
    }
}
