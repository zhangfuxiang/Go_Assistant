package com.example.sliding_menu1.Fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.example.sliding_menu1.Activity.MainActivity;
import com.example.sliding_menu1.Json_analyze.CordLocation;
import com.example.sliding_menu1.sensor.MyOrientationListener;
import com.example.sliding_menu1.R;
import com.google.gson.Gson;


/**
 * 工程名：Sliding_menu
 * 包名：com.example.sliding_menu1.Fragment
 * 作者： win
 * 创建日期：2016/3/26 16:44
 * 实现的主要功能：
 */
public class FragmentHome extends Fragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener, CompoundButton.OnCheckedChangeListener {
    private MapView mapView=null;
    private BaiduMap mBaiduMap=null;

    // 判断是否全屏，一定要用static修饰；否则失败。因为要保存上一次的状态；
    static boolean isDisplayScreen = false;

    private Context context;

    //定位相关
    private LocationClient mLocationClient=null;
    private MyLoationListener mylocationListener;
    private boolean isTip = false;
    private boolean isFirstLoc=false;
    private double mLatitude;
    private double mLongitude;

    // 自定义定位图标
    private BitmapDescriptor mIconLocation;
    private MyOrientationListener myOrientationListener;
    private float mCurrentX;
    private MyLocationConfiguration.LocationMode mLocationMode;

    private  PopupWindow popupWindow;
    private View popupView;
    private RadioGroup map_radiogroup,mode_radiogroup;
    private RadioButton normal_map,statle_map,normal_mode,compass_mode,follow_mode;
    private CheckBox traffic_map,hot_map, fullscreen_mode;


    private Dialog dialog;// 加载对话框
    //二维码识别的坐标
    private CordLocation location;
    private double cordLatitude;
    private double cordLongitude;
    private LatLng latlng;
    //覆盖物
    private Marker mMarker;
    BitmapDescriptor bd = BitmapDescriptorFactory
            .fromResource(R.mipmap.icon_gcoding);
    private InfoWindow mInfoWindow;
    //标记上的气泡
    private Button popupbutton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("asd","this is onCreate");

//        setHasOptionsMenu(true);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fg_home,container,false);
        Log.i("asd","this is onCreateView");
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context=getView().getContext();
        Log.i("asd","this is onActivityCreated");

        isFirstLoc=true;
        //标记上的气泡
        popupbutton=new Button(context);
        popupbutton.setBackgroundResource(R.mipmap.popup);


        //悬浮的功能按钮和定位按钮
        getView().findViewById(R.id.fab).setOnClickListener(this);
        getView().findViewById(R.id.btn_add).setOnClickListener(this);


        //设置二维码返回的数据进行定位
        if(MainActivity.result!=null){
            Gson gson=new Gson();
            location=gson.fromJson(MainActivity.result,CordLocation.class);
            //将字符串数字转换为浮点数
            cordLatitude=Double.parseDouble(location.getLatitude());
            cordLongitude=Double.parseDouble(location.getLongitude());
            latlng=new LatLng(cordLatitude,cordLongitude);
            //识别二维码窗口提示
            showdialog(latlng);
            MainActivity.result=null;
        }

        popupView= initPopupWindow();
        initPopupWindowClick();

        //初始化地图
        initView();

        //初始化定位
        initLocation();

        //定位的标记上的气泡点击监听
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker) {
                InfoWindow.OnInfoWindowClickListener listener = null;
                listener=new InfoWindow.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick() {
                        //点击气泡消失气泡
                        mBaiduMap.hideInfoWindow();
                    }
                };
                if (marker==mMarker){
                    //点击标记生成气泡
                    mBaiduMap.showInfoWindow(new InfoWindow(BitmapDescriptorFactory.fromView(popupbutton)
                            ,latlng,-80,listener));
                }
                return true;
            }
        });


        //全屏模式
        fullscreen_mode = (CheckBox) popupView.findViewById(R.id.fullscreen_mode_checkbox);
        fullscreen_mode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    getActivity().getWindow().setFlags(
                            WindowManager.LayoutParams.FLAG_FULLSCREEN,
                            WindowManager.LayoutParams.FLAG_FULLSCREEN);
                }else{
                    getActivity().getWindow().clearFlags(
                            WindowManager.LayoutParams.FLAG_FULLSCREEN);
                }

            }
        });



    }
    //二维码识别成功后的提示窗口
    private void showdialog(final LatLng latlng) {
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setTitle("坐标信息");
        builder.setMessage("识别得到"+"经度："+location.getLatitude()+"  纬度："+location.getLongitude()+"  是否要进行要定位?");

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MapStatusUpdate msu=MapStatusUpdateFactory.newLatLng(latlng);
                mBaiduMap.animateMapStatus(msu);
                MarkerOptions ooA = new MarkerOptions().position(latlng).icon(bd)
                        .zIndex(9).draggable(true);
                ooA.animateType(MarkerOptions.MarkerAnimateType.drop);
                mMarker= (Marker) mBaiduMap.addOverlay(ooA);
                popupbutton.setText(location.getName());
                Toast.makeText(context,"位置:"+location.getName(),Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog=builder.create();
        dialog.show();
        Log.i("asd","经度："+location.getLatitude()+"   纬度："+location.getLongitude());


    }

    //初始化popupwindow
    private void initPopupWindowClick() {
        map_radiogroup= (RadioGroup) popupView.findViewById(R.id.map_radiongroup);
        normal_map= (RadioButton) popupView.findViewById(R.id.normal_map_radionbutton);
        statle_map= (RadioButton) popupView.findViewById(R.id.sate_map_radiobutton);
        mode_radiogroup= (RadioGroup) popupView.findViewById(R.id.mode_radiogroup);
        normal_mode= (RadioButton) popupView.findViewById(R.id.mode_radiobutton);
        compass_mode= (RadioButton) popupView.findViewById(R.id.compass_radiobutton);
        follow_mode= (RadioButton) popupView.findViewById(R.id.follow_radiobutton);
        traffic_map= (CheckBox) popupView.findViewById(R.id.traffic_map_checkbox);
        hot_map= (CheckBox) popupView.findViewById(R.id.hot_map_checkbox);


        map_radiogroup.setOnCheckedChangeListener(this);
        mode_radiogroup.setOnCheckedChangeListener(this);
        traffic_map.setOnCheckedChangeListener(this);
        compass_mode.setOnCheckedChangeListener(this);
        hot_map.setOnCheckedChangeListener(this);
        popupView.findViewById(R.id.close_button).setOnClickListener(this);
        popupView.findViewById(R.id.rbtn_mylocation).setOnClickListener(this);

    }

    private View initPopupWindow() {
        View view=LayoutInflater.from(context).inflate(R.layout.popup_window,null);
        popupWindow=new PopupWindow(view,400,1260);
        popupWindow.setContentView(view);
        //点击域外让popupwindow消失的方法
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);
        return view;
    }


    private void initView() {
        mapView= (MapView) getView().findViewById(R.id.map_view);
        // 改变显示的比例尺
        mBaiduMap = mapView.getMap();
        MapStatusUpdate after = MapStatusUpdateFactory.zoomTo(15.0f);
        mBaiduMap.setMapStatus(after);
    }

    private  void initLocation(){
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        mLocationMode= MyLocationConfiguration.LocationMode.NORMAL;
        mLocationClient=new LocationClient(getActivity().getApplicationContext());
        mylocationListener=new MyLoationListener();
        mLocationClient.registerLocationListener(mylocationListener);

        LocationClientOption option=new LocationClientOption();
        option.setCoorType("bd09ll");
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setScanSpan(1000);// 设置定位一秒钟请求一次；
        mLocationClient.setLocOption(option);
        //初始化图标
        mIconLocation= BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked);
        myOrientationListener=new MyOrientationListener(context);
        myOrientationListener
                .setOnOrientationListener(new MyOrientationListener.OnOrientationListener() {

                    @Override
                    public void onOrientationChanged(float x) {
                        mCurrentX = x;
                    }
                });

        Log.i("asd","this is initlocation");

    }


    @Override
    public void onStart() {
        super.onStart();
        //开始定位
        mBaiduMap.setMyLocationEnabled(true);
        if (!mLocationClient.isStarted()) {
            mLocationClient.start();
        }
        //开启方向传感器
        myOrientationListener.start();

        Log.i("asd","this is onStart");
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        Log.i("asd","this is onDestroy");
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        Log.i("asd","this is onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        Log.i("asd","this is onPause");
    }
    @Override
    public void onStop() {
        super.onStop();

        // 停止定位
        mBaiduMap.setMyLocationEnabled(false);
        mLocationClient.stop();

        // 停止方向传感器
        myOrientationListener.stop();
        Log.i("asd","this is onStop");
    }
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.fab:
                centerToMyLocation();
                break;
            case R.id.btn_add:
                popupWindow.showAsDropDown(v);
                break;
            case R.id.close_button:
                popupWindow.dismiss();
                break;
            case R.id.rbtn_mylocation:
                isTip=true;

                break;
        }
    }
    //定位自己位置的方法
    private void centerToMyLocation() {
        LatLng latlng=new LatLng(mLatitude,mLongitude);
        MapStatusUpdate msu=MapStatusUpdateFactory.newLatLng(latlng);
        mBaiduMap.animateMapStatus(msu);
    }
    //popupwindow里面功能勾选监听
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (group==map_radiogroup) {
            if (checkedId == normal_map.getId()) {
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
            }
            if (checkedId == statle_map.getId()) {
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
            }
        }
        if (group==mode_radiogroup){
            if (checkedId==normal_mode.getId()){
                mLocationMode = MyLocationConfiguration.LocationMode.NORMAL;
            }
            if (checkedId==follow_mode.getId()){
                mLocationMode = MyLocationConfiguration.LocationMode.FOLLOWING;
            }
            if (checkedId==compass_mode.getId()){
                mLocationMode = MyLocationConfiguration.LocationMode.COMPASS;
            }

        }
    }
    //popupwindow里面功能勾选监听
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView==traffic_map){
            if (isChecked){
                mBaiduMap.setTrafficEnabled(true);
            }else{
                mBaiduMap.setTrafficEnabled(false);
            }
        }
        if (buttonView==hot_map){
            if (isChecked){
                mBaiduMap.setBaiduHeatMapEnabled(true);
            }else{
                mBaiduMap.setBaiduHeatMapEnabled(false);
            }
        }

    }

    //所在位置定位监听类
    private class MyLoationListener implements BDLocationListener{

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            MyLocationData data=new MyLocationData.Builder()
                    .direction(mCurrentX)
                    .accuracy(bdLocation.getRadius())
                    .latitude(bdLocation.getLatitude())
                    .longitude(bdLocation.getLongitude())
                    .build();
            mBaiduMap.setMyLocationData(data);

            //设置自定义图标
            MyLocationConfiguration config=new MyLocationConfiguration(mLocationMode,true,mIconLocation);
            mBaiduMap.setMyLocationConfigeration(config);

            //更新经纬度
            mLatitude=bdLocation.getLatitude();
            mLongitude=bdLocation.getLongitude();
            //判断是否是第一次加载并地位
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

            //判断是否要位置定位并提示位置
            if (isTip){
                LatLng latlong=new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude());
                MapStatusUpdate msu=MapStatusUpdateFactory.newLatLng(latlong);
                mBaiduMap.animateMapStatus(msu);
                isTip=false;
                Toast.makeText(context, bdLocation.getAddrStr(),
                        Toast.LENGTH_SHORT).show();
                Log.i("asd",
                        bdLocation.getAddrStr() + "\n" + bdLocation.getAltitude()
                                + "" + "\n" + bdLocation.getCity() + "\n"
                                + bdLocation.getCityCode() + "\n"
                                + bdLocation.getCoorType() + "\n"
                                + bdLocation.getDirection() + "\n"
                                + bdLocation.getDistrict() + "\n"
                                + bdLocation.getFloor() + "\n"
                                + bdLocation.getLatitude() + "\n"
                                + bdLocation.getLongitude() + "\n"
                                + bdLocation.getNetworkLocationType() + "\n"
                                + bdLocation.getProvince() + "\n"
                                + bdLocation.getSatelliteNumber() + "\n"
                                + bdLocation.getStreet() + "\n"
                                + bdLocation.getStreetNumber() + "\n"
                                + bdLocation.getTime() + "\n");

                // 弹出对话框显示定位信息；
                AlertDialog.Builder builder=new AlertDialog.Builder(context);
                builder.setTitle("为您获得的定位信息：");
                builder.setMessage("当前位置：" + bdLocation.getAddrStr() + "\n"
                        + "城市编号：" + bdLocation.getCityCode() + "\n" + "定位时间："
                        + bdLocation.getTime() + "\n" + "当前纬度："
                        + bdLocation.getLatitude() + "\n" + "当前经度："
                        + bdLocation.getLongitude());
                builder.setPositiveButton("确定", null);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        }

    }
}
