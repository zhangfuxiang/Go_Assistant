package com.example.sliding_menu1.Activity;


import android.app.Activity;
import android.graphics.Camera;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import com.example.sliding_menu1.R;

import java.security.Policy;

/**
 * 工程名：Sliding_menu
 * 包名：com.example.sliding_menu1.Activity
 * 作者： win
 * 创建日期：2016/4/13 13:37
 * 实现的主要功能：手电筒
 */
public class FlashlightActivity extends Activity {
    private ImageView ivflashlight;
    private ImageView ivflashlightcontroller;
    private android.hardware.Camera mCamera;
    private android.hardware.Camera.Parameters mParameters;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashlight);
        init();
        ivflashlight.setTag(false);
        getScreenSize();

    }

    private void init() {
        ivflashlight= (ImageView) findViewById(R.id.iv_flashlight);
        ivflashlightcontroller= (ImageView) findViewById(R.id.iv_flashlight_controller);
        findViewById(R.id.ibtn_flashlight_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void onClick_flashlight(View view){
        if (((boolean)ivflashlight.getTag())==false)
        {
            openFlashlight();
        }else{
            closeFlashlight();
        }
    }
    //获得这个屏幕的像素,按屏幕改变尺寸
    public void getScreenSize() {
        Point point=new Point();

        getWindowManager().getDefaultDisplay().getSize(point);
        LayoutParams layoutParams= (LayoutParams) ivflashlightcontroller.getLayoutParams();
        //高度占整个屏幕3/4，宽度占1/3
        layoutParams.height=point.y*3/4;
        layoutParams.width=point.x/3;
        ivflashlightcontroller.setLayoutParams(layoutParams);
    }
    //打开闪关灯
    private void openFlashlight(){
        TransitionDrawable drawable= (TransitionDrawable) ivflashlight.getDrawable();
        //播放渐变动画，时间200ms
        drawable.startTransition(200);
        ivflashlight.setTag(true);
        try{
            mCamera= android.hardware.Camera.open();
            int textureId=0;
            //创建surface纹理
            mCamera.setPreviewTexture(new SurfaceTexture(textureId));
            mCamera.startPreview();

            mParameters=mCamera.getParameters();
            mParameters.setFlashMode(android.hardware.Camera.Parameters.FLASH_MODE_TORCH);
            mCamera.setParameters(mParameters);
        }catch (Exception e){
        }

    }
    //关闭闪关灯
    private void closeFlashlight(){
        TransitionDrawable drawable= (TransitionDrawable) ivflashlight.getDrawable();
        if(((boolean)ivflashlight.getTag()))
        {
            drawable.reverseTransition(200);
            ivflashlight.setTag(false);
            if (mCamera!=null){
                mParameters=mCamera.getParameters();
                mParameters.setFlashMode(android.hardware.Camera.Parameters.FLASH_MODE_OFF);
                //设置mCamera变量参数
                mCamera.setParameters(mParameters);
                //对mCamera停止预览
                mCamera.stopPreview();
                //对mCamera释放，便于下次用
                mCamera.release();
                mCamera=null;
            }
        }
    }

    //当窗口失去焦点是关闭闪光灯
    @Override
    protected void onPause(){
        super.onPause();
        closeFlashlight();
    }
}
