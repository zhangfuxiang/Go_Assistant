package com.example.sliding_menu1.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog.Builder;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.SupportMapFragment;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.overlayutil.BusLineOverlay;
import com.baidu.mapapi.search.busline.BusLineResult;
import com.baidu.mapapi.search.busline.BusLineSearch;
import com.baidu.mapapi.search.busline.BusLineSearchOption;
import com.baidu.mapapi.search.busline.OnGetBusLineSearchResultListener;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.example.sliding_menu1.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 工程名：Sliding_menu
 * 包名：com.example.sliding_menu1.Activity
 * 作者： win
 * 创建日期：2016/4/9 17:24
 * 实现的主要功能：查询公交路线
 */
public class BusActivity extends FragmentActivity implements BaiduMap.OnMapClickListener,
        OnGetPoiSearchResultListener, OnGetBusLineSearchResultListener, AdapterView.OnItemSelectedListener {
    private Context context;

    private Button mBtnPre = null;// 上一个节点
    private Button mBtnNext = null;// 下一个节点
    private int nodeIndex = -2;// 节点索引,供浏览节点时使用
    private BusLineResult route = null;// 保存驾车/步行路线数据的变量，供浏览节点时使用
    private List<String> busLineIDList = null;// 这是返回的公交车站列表，以字符串形式存储在List中；
    private int busLineIndex = 0;// 返回的公交路线索引；一般为 0,1
    // 搜索相关
    private PoiSearch mSearch = null; // 搜索模块，也可去掉地图模块独立使用
    private BusLineSearch mBusLineSearch = null;
    private BaiduMap mBaiduMap = null;

    private String city_return;

    private Spinner province;
    private Spinner city;
    private EditText searchkey;

    // 为两个Spinner分别创建适配器
    private ArrayAdapter<String> arrayAdapterProvince;
    private ArrayAdapter<String> arrayAdapterCity;

    private String selectedProvince;
    private String selectedCity;

    @Nullable
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_bus);
        this.context = this;

        initView();
        initAdapter();// 省市联动；
        province.setAdapter(arrayAdapterProvince);

        mBaiduMap = ((SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapView)).getBaiduMap();
        mSearch = PoiSearch.newInstance();
        mBusLineSearch = BusLineSearch.newInstance();
        initListener();
        busLineIDList = new ArrayList<String>();
    }

    private void initListener() {
        mBaiduMap.setOnMapClickListener(this);
        mSearch.setOnGetPoiSearchResultListener(this);
        mBusLineSearch.setOnGetBusLineSearchResultListener(this);
    }

    private void initAdapter() {
        arrayAdapterProvince = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_dropdown_item, getResources()
                .getStringArray(R.array.province));
    }

    private void initView() {
        mBtnPre = (Button) findViewById(R.id.btn_pre);
        mBtnNext = (Button) findViewById(R.id.btn_next);

        province = (Spinner) findViewById(R.id.sp_province);
        city = (Spinner) findViewById(R.id.sp_city);
        searchkey = (EditText) findViewById(R.id.ed_bus_search);

        province.setOnItemSelectedListener(this);
        city.setOnItemSelectedListener(this);
        findViewById(R.id.bus_btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    //发起检索,点击“搜索”按钮
    public void searchButtonProcess(View v) {
        busLineIDList.clear();
        busLineIndex = 0;
        mBtnPre.setVisibility(View.GONE);
        mBtnNext.setVisibility(View.GONE);
        // 发起poi检索，从得到所有poi中找到公交线路类型的poi，再使用该poi的uid进行公交详情搜索
        // 这里使用了链式编程，对于有相同返回值的方法通过“.”串联起来。简化代码；
        mSearch.searchInCity((new PoiCitySearchOption()).city(selectedCity)
                .keyword(searchkey.getText().toString().trim()));
    }

    //点击“逆向”按钮 注意：在多次点击“逆向”按钮后，查询到的公交线路索引busLineIndex会清零。 然后继续从busLineIDList读取；
    public void SearchNextBusline(View v) {
        if (busLineIndex >= busLineIDList.size()) {// 这是不可能的情况，busLineIndex=0,1；busLineIDList.size()=2；
            busLineIndex = 0;// 清零；
        }
        if (busLineIndex >= 0 && busLineIndex < busLineIDList.size()
                && busLineIDList.size() > 0) {
            mBusLineSearch.searchBusLine((new BusLineSearchOption()
                    .city(selectedCity).uid(busLineIDList.get(busLineIndex))));

            busLineIndex++;
        }

    }

    //节点浏览示例
    public void nodeClick(View v) {

        if (nodeIndex < -1 || route == null
                || nodeIndex >= route.getStations().size()) {
            return;
        }

        // 注意这里对控件的使用，不是通过xml定义控件，而是直接在代码中实现；
        TextView popupText = new TextView(this);
        popupText.setBackgroundResource(R.mipmap.popup);
        popupText.setTextColor(0xff000000);

        switch (v.getId()) {
            case R.id.btn_pre: // 上一个节点
                if (nodeIndex > 0) {
                    // 索引减
                    nodeIndex--;
                }

                break;

            case R.id.btn_next: // 下一个节点
                if (nodeIndex < (route.getStations().size() - 1)) {
                    // 索引加
                    nodeIndex++;
                }

                break;

        }

        if (nodeIndex >= 0) {
            // 移动到指定索引的坐标
            /**
             * 把地图中心点移动到当前车站，即把当前车站作为地图中点；通过获取车站的经纬度来移动；
             *
             */
            mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(route
                    .getStations().get(nodeIndex).getLocation()));
            // 弹出气泡，气泡上显示车站名称；
            popupText.setText(route.getStations().get(nodeIndex).getTitle());

            /**
             * 以下方法的作用是把气泡移动到车站的位置，也是需要传递车站的经纬度；
             *
             */
            mBaiduMap.showInfoWindow(new InfoWindow(popupText, route
                    .getStations().get(nodeIndex).getLocation(), 0));
        }
    }

    @Override
    protected void onDestroy() {
        mSearch.destroy();
        mBusLineSearch.destroy();
        super.onDestroy();
    }


    //点击地图后，气泡消失
    @Override
    public void onMapClick(LatLng latLng) {
        mBaiduMap.hideInfoWindow();
    }

    @Override
    public boolean onMapPoiClick(MapPoi mapPoi) {
        return false;
    }

    //执行searchButtonProcess开始按钮后，返回的POI数据。
    @Override
    public void onGetPoiResult(PoiResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(context, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
            return;
        }
        // 遍历所有poi，找到类型为公交线路的poi
        busLineIDList.clear();
        // 一般以下for循环执行2次，因为同一条公交线路有正反2个方向；
        //for (循环变量类型 循环变量名称 : 要被遍历的对象)  循环体
        for (PoiInfo poi:result.getAllPoi()){
            if (poi.type==PoiInfo.POITYPE.BUS_LINE
                    ||poi.type==PoiInfo.POITYPE.SUBWAY_LINE){
                busLineIDList.add(poi.uid);// 就把该poi信息存储到公交路线列表中；busLineIDList中存储的是公交线路的ID值；
                Log.i("asd", "城市：" + poi.city + "；线路：" + poi.name);
                city_return = poi.city;
            }
        }
        // 即要把原来所有的数据清空；
        SearchNextBusline(null);
        route = null;
    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
    }
    //这是点击搜索按钮后，从服务器返回公交信息；在这里得到route值；
    @Override
    public void onGetBusLineResult(BusLineResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(BusActivity.this, "抱歉，未找到结果", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        mBaiduMap.clear();
        route=result;
        nodeIndex=-1;
        BusLineOverlay overlay=new BusLineOverlay(mBaiduMap);
        mBaiduMap.setOnMarkerClickListener(overlay);
        overlay.setData(result);
        overlay.addToMap();
        overlay.zoomToSpan();
        mBtnPre.setVisibility(View.VISIBLE);
        mBtnNext.setVisibility(View.VISIBLE);
        Toast.makeText(context, result.getBusLineName(), Toast.LENGTH_SHORT)
                .show();
        Log.i("asd", "总站数：" + result.getStations().size() + "");

        // 最好使用对话框的形式把公交信息显示出来
        String message="";
        Builder builder=new Builder(context);
        builder.setTitle("为您查询到的公交信息：");
        for (int i=0;i<result.getStations().size();i++){
            message=message+"公交站"+(i+1)+":"+
                    result.getStations().get(i).getTitle() + "\n";
        }
        builder.setMessage(city_return+result.getBusLineName() + "共有"
                + result.getStations().size() + "个公交站:" + "\n" + message);
        builder.setPositiveButton("确定", null);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()){
            case R.id.sp_province:
                selectedProvince=arrayAdapterProvince.getItem(position);
                Toast.makeText(context,selectedProvince,Toast.LENGTH_SHORT).show();
                initAdapterCity(selectedProvince);
                break;
            case R.id.sp_city:
                selectedCity = arrayAdapterCity.getItem(position);
                Toast.makeText(context, selectedCity, Toast.LENGTH_SHORT).show();
                break;
        }
    }
    //获取到省份之后，选择城市
    private void initAdapterCity(String selectedProvince) {
        switch (selectedProvince) {
            case "北京":
                arrayAdapterCity = new ArrayAdapter<>(context,
                        android.R.layout.simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.北京));

                break;

            case "天津":
                arrayAdapterCity = new ArrayAdapter<>(context,
                        android.R.layout.simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.天津));
                break;

            case "上海":
                arrayAdapterCity = new ArrayAdapter<>(context,
                        android.R.layout.simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.上海));
                break;

            case "重庆":
                arrayAdapterCity = new ArrayAdapter<>(context,
                        android.R.layout.simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.重庆));
                break;

            case "香港":
                arrayAdapterCity = new ArrayAdapter<>(context,
                        android.R.layout.simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.香港));
                break;

            case "澳门":
                arrayAdapterCity = new ArrayAdapter<>(context,
                        android.R.layout.simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.澳门));
                break;

            case "河北":
                arrayAdapterCity = new ArrayAdapter<>(context,
                        android.R.layout.simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.河北));
                break;

            case "山东":
                arrayAdapterCity = new ArrayAdapter<>(context,
                        android.R.layout.simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.山东));
                break;

            case "辽宁":
                arrayAdapterCity = new ArrayAdapter<>(context,
                        android.R.layout.simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.辽宁));
                break;

            case "黑龙江":
                arrayAdapterCity = new ArrayAdapter<>(context,
                        android.R.layout.simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.黑龙江));
                break;

            case "江苏":
                arrayAdapterCity = new ArrayAdapter<>(context,
                        android.R.layout.simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.江苏));
                break;

            case "浙江":
                arrayAdapterCity = new ArrayAdapter<>(context,
                        android.R.layout.simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.浙江));
                break;

            case "陕西":
                arrayAdapterCity = new ArrayAdapter<>(context,
                        android.R.layout.simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.陕西));
                break;

            case "山西":
                arrayAdapterCity = new ArrayAdapter<>(context,
                        android.R.layout.simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.山西));
                break;

            case "福建":
                arrayAdapterCity = new ArrayAdapter<>(context,
                        android.R.layout.simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.福建));
                break;

            case "广东":
                arrayAdapterCity = new ArrayAdapter<>(context,
                        android.R.layout.simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.广东));
                break;

            case "河南":
                arrayAdapterCity = new ArrayAdapter<>(context,
                        android.R.layout.simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.河南));
                break;

            case "四川":
                arrayAdapterCity = new ArrayAdapter<>(context,
                        android.R.layout.simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.四川));
                break;

            case "湖南":
                arrayAdapterCity = new ArrayAdapter<>(context,
                        android.R.layout.simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.湖南));
                break;

            case "湖北":
                arrayAdapterCity = new ArrayAdapter<>(context,
                        android.R.layout.simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.湖北));
                break;

            case "安徽":
                arrayAdapterCity = new ArrayAdapter<>(context,
                        android.R.layout.simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.安徽));
                break;

            case "江西":
                arrayAdapterCity = new ArrayAdapter<>(context,
                        android.R.layout.simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.江西));
                break;

            case "海南":
                arrayAdapterCity = new ArrayAdapter<>(context,
                        android.R.layout.simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.海南));
                break;

            case "云南":
                arrayAdapterCity = new ArrayAdapter<>(context,
                        android.R.layout.simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.云南));
                break;

            case "贵州":
                arrayAdapterCity = new ArrayAdapter<>(context,
                        android.R.layout.simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.贵州));
                break;

            case "甘肃":
                arrayAdapterCity = new ArrayAdapter<>(context,
                        android.R.layout.simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.甘肃));
                break;

            case "青海":
                arrayAdapterCity = new ArrayAdapter<>(context,
                        android.R.layout.simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.青海));
                break;

            case "宁夏":
                arrayAdapterCity = new ArrayAdapter<>(context,
                        android.R.layout.simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.宁夏));
                break;

            case "内蒙古":
                arrayAdapterCity = new ArrayAdapter<>(context,
                        android.R.layout.simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.内蒙古));
                break;

            case "广西":
                arrayAdapterCity = new ArrayAdapter<>(context,
                        android.R.layout.simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.广西));
                break;

            case "新疆":
                arrayAdapterCity = new ArrayAdapter<>(context,
                        android.R.layout.simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.新疆));
                break;

            case "西藏":
                arrayAdapterCity = new ArrayAdapter<>(context,
                        android.R.layout.simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.西藏));
                break;

            case "台湾":
                arrayAdapterCity = new ArrayAdapter<>(context,
                        android.R.layout.simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.台湾));
                break;

        }
        city.setAdapter(arrayAdapterCity);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
