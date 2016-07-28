package com.example.sliding_menu1.Activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.SupportMapFragment;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.PoiOverlay;
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.example.sliding_menu1.R;
import com.example.sliding_menu1.sensor.MyOrientationListener;

/**
 * 工程名：Sliding_menu
 * 包名：com.example.sliding_menu1.Activity
 * 作者： win
 * 创建日期：2016/4/25 16:01
 * 实现的主要功能：兴趣点搜索
 */
public class PoiSearchActivity extends FragmentActivity implements OnGetPoiSearchResultListener, OnGetSuggestionResultListener {

    private PoiSearch mPoiSearch=null;
    private SuggestionSearch mSuggestionSearch=null;
    private BaiduMap mBaiduMap=null;
    private PoiNearbySearchOption nearbySearchOption;
    private LatLng mLatLng;
    private int totalPage;

    // 自定义定位图标
    private BitmapDescriptor mIconLocation;
//    private MyOrientationListener myOrientationListener;
//    private float mCurrentX;

    //定位相关
    boolean isFirstLoc = true; // 是否首次定位
    private MyLocationConfiguration.LocationMode mCurrentMode;
    private LocationClient mLocationClient=null;
    private MyLoationListener mylocationListener;
    private double mLatitude;
    private double mLongitude;
    /**
     * 搜索关键字输入窗口
     */
    private AutoCompleteTextView keyWorldsView = null;
    private ArrayAdapter<String> sugAdapter = null;
    private int load_Index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_poisearch);
        init();
        initLocation();

        if (!mLocationClient.isStarted()) {
            mLocationClient.start();
        }
/*        //开启方向传感器
        myOrientationListener.start();*/


    }
    /**
     * 影响搜索按钮点击事件
     *
     * @param v
     */
    public void searchButton(View v) {
        EditText editSearchKey = (EditText) findViewById(R.id.searchkey);
        mLatLng=new LatLng(mLatitude,mLongitude);
        Log.i("asd", String.valueOf(mLatitude));
        Log.i("asd", String.valueOf(mLongitude));
        nearbySearchOption=new PoiNearbySearchOption();
        nearbySearchOption.location(mLatLng);
        nearbySearchOption.keyword(editSearchKey.getText().toString());
        nearbySearchOption.radius(1000);// 检索半径，单位是米
        nearbySearchOption.pageNum(load_Index);
        mPoiSearch.searchNearby(nearbySearchOption);// 发起附近检索请求

    }

    public void goToNextPage(View v) {
        Log.i("asd", "结果总数:"+String.valueOf(totalPage));
        Log.i("asd", "当前第几页:"+String.valueOf(load_Index));
       if (load_Index==totalPage){
           Toast.makeText(this,"为最后一组，下一次回到第一组",Toast.LENGTH_SHORT).show();
            load_Index=0;
        }else {load_Index++;}
        searchButton(null);
    }
    @Override
    public void onStart() {
        super.onStart();
        Log.i("asd","this is onStart");
    }
    @Override
    public void onDestroy() {
        // 退出时销毁定位
        mLocationClient.stop();
        mPoiSearch.destroy();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        super.onDestroy();
        Log.i("asd","this is onDestroy");
    }

    private void initLocation() {
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        mCurrentMode= MyLocationConfiguration.LocationMode.NORMAL;
        mLocationClient=new LocationClient(getApplicationContext());
        mylocationListener=new MyLoationListener();
        mLocationClient.registerLocationListener(mylocationListener);

        LocationClientOption option=new LocationClientOption();
        option.setCoorType("bd09ll");
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setScanSpan(1000);// 设置定位一秒钟请求一次
        mLocationClient.setLocOption(option);

        //初始化图标
        mIconLocation= BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked);
    }

    private void init() {
        mPoiSearch=PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(this);
        mSuggestionSearch=SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(this);

        mBaiduMap = ((SupportMapFragment) (getSupportFragmentManager()
                .findFragmentById(R.id.map))).getBaiduMap();
        findViewById(R.id.ibtn_back_poi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    public void onGetPoiResult(PoiResult result) {
        Log.i("asd", "onGetPoiResult:"+String.valueOf(result));
        if (result == null
                || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
            Toast.makeText(this, "未找到结果", Toast.LENGTH_LONG).show();
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            mBaiduMap.clear();
            PoiOverlay overlay = new MyPoiOverlay(mBaiduMap);
            mBaiduMap.setOnMarkerClickListener(overlay);
            overlay.setData(result);
            overlay.addToMap();
            overlay.zoomToSpan();
            totalPage = result.getTotalPageNum();// 获取总分页数
            return;
        }
    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult result) {
        Log.i("asd", "onGetPoiDetailResult:"+String.valueOf(result));
        if (result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(this, "抱歉，未找到结果", Toast.LENGTH_SHORT)
                    .show();
        } else {
            Toast.makeText(this, result.getName() + ": " + result.getAddress(), Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    public void onGetSuggestionResult(SuggestionResult suggestionResult) {

    }

    private class MyLoationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            MyLocationData data=new MyLocationData.Builder()
                    .accuracy(bdLocation.getRadius())
                    .latitude(bdLocation.getLatitude())
                    .longitude(bdLocation.getLongitude())
//            // 此处设置开发者获取到的方向信息，顺时针0-360
//            .direction(100).latitude(bdLocation.getLatitude())
                    .build();
            mBaiduMap.setMyLocationData(data);
            //更新经纬度
            mLatitude=bdLocation.getLatitude();
            mLongitude=bdLocation.getLongitude();
            if (isFirstLoc) {
                isFirstLoc = false;
                Log.i("asd", String.valueOf(mLatitude));
                Log.i("asd", String.valueOf(mLongitude));
                LatLng latLng = new LatLng(bdLocation.getLatitude(),
                        bdLocation.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(latLng).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }

            //设置自定义图标
            MyLocationConfiguration config=new MyLocationConfiguration(mCurrentMode,true,mIconLocation);
            mBaiduMap.setMyLocationConfigeration(config);

        }

    }
    private class MyPoiOverlay extends PoiOverlay{

        /**
         * 构造函数
         *
         * @param baiduMap 该 PoiOverlay 引用的 BaiduMap 对象
         */
        public MyPoiOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public boolean onPoiClick(int index) {
            super.onPoiClick(index);
            PoiInfo poi = getPoiResult().getAllPoi().get(index);
            // if (poi.hasCaterDetails) {
            mPoiSearch.searchPoiDetail((new PoiDetailSearchOption())
                    .poiUid(poi.uid));
            // }
            return true;
        }
    }
}
