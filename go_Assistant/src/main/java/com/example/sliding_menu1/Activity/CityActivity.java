package com.example.sliding_menu1.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.sliding_menu1.R;
import com.juhe.weather.adapter.CityListAdatper;
import com.thinkland.juheapi.common.JsonCallBack;
import com.thinkland.juheapi.data.weather.WeatherData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 工程名：Sliding_menu
 * 包名：com.example.sliding_menu1.Activity
 * 作者： win
 * 创建日期：2016/4/17 23:50
 * 实现的主要功能：天气预报里城市选择
 */
public class CityActivity extends Activity {

    private ListView lv_city;
    private List<String> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);
        initView();
        getCities();
    }
    private void initView(){
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        lv_city= (ListView) findViewById(R.id.lv_city);
    }
    private void getCities(){
        WeatherData data=WeatherData.getInstance();
        data.getCities(new JsonCallBack() {
            @Override
            public void jsonLoaded(JSONObject jsonObject) {
                Log.i("asd", String.valueOf(jsonObject));
                int resulcode= 0;
                try {
                    resulcode = jsonObject.getInt("resultcode");
                    int error_code=jsonObject.getInt("error_code");
                    if(error_code==0&&resulcode==200){
                        list=new ArrayList<String>();
                        JSONArray resultArray=jsonObject.getJSONArray("result");
                        Set<String> citySet=new HashSet<String>();
                        for (int i=0;i<resultArray.length();i++){
                            String city=resultArray.getJSONObject(i).getString("city");
                            citySet.add(city);
                        }
                        list.addAll(citySet);
                        CityListAdatper adatper=new CityListAdatper(CityActivity.this,list);
                        lv_city.setAdapter(adatper);
                        lv_city.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent intent=new Intent();
                                intent.putExtra("city",list.get(position));
                                setResult(1,intent);
                                finish();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }
}
