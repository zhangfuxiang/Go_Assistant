package com.example.sliding_menu1.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;

/**
 * 工程名：Sliding_menu
 * 包名：com.example.sliding_menu1.Activity
 * 作者： win
 * 创建日期：2016/4/11 16:44
 * 实现的主要功能：
 */
/**
 * 演示MapView的基本用法
 *
 * 用户显示下载地图
 */
public class ShowMapActivity extends Activity {

    private Context context;

    @SuppressWarnings("unused")
    private static final String LTAG = ShowMapActivity.class.getSimpleName();
    private MapView mMapView;
    private BaiduMap mBaiduMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        this.context=this;
        Intent intent=getIntent();
        if (intent.hasExtra("x")&&intent.hasExtra("y")){
            // 当用intent参数时，设置中心点为指定点
            Bundle b=intent.getExtras();
            LatLng p=new LatLng(b.getDouble("y"),b.getDouble("x"));
            mMapView=new MapView(context,
                    new BaiduMapOptions().mapStatus(new MapStatus.Builder()
                            .target(p).build()));
        }else {
            mMapView = new MapView(context, new BaiduMapOptions());
        }
        setContentView(mMapView);
        mBaiduMap = mMapView.getMap();

        Log.i("asd", "打开地图");
    }
    @Override
    protected void onPause() {
        super.onPause();
        // activity 暂停时同时暂停地图控件
        mMapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // activity 恢复时同时恢复地图控件
        mMapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // activity 销毁时同时销毁地图控件
        mMapView.onDestroy();
    }
}
