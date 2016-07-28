package com.example.sliding_menu1.Activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

/*import com.juhe.weather.bean.FutureWeatherBean;
import com.juhe.weather.bean.HoursWeatherBean;
import com.juhe.weather.bean.PMBean;
import com.juhe.weather.bean.WeatherBean;
import com.juhe.weather.service.WeatherService;
import com.juhe.weather.service.WeatherService.OnParserCallBack;
import com.juhe.weather.service.WeatherService.WeatherServiceBinder;*/
import com.baidu.platform.comapi.map.A;
import com.juhe.weather.bean.FutureWeatherBean;
import com.juhe.weather.bean.HourWeatherBean;
import com.juhe.weather.bean.PMBean;
import com.juhe.weather.bean.WeatherBean;
import com.juhe.weather.service.WeatherService;
import com.juhe.weather.swiperefresh.PullToRefreshBase;
import com.juhe.weather.swiperefresh.PullToRefreshScrollView;
import com.thinkland.juheapi.common.JsonCallBack;
import com.thinkland.juheapi.data.air.AirData;
import com.thinkland.juheapi.data.weather.WeatherData;
import com.example.sliding_menu1.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 工程名：Sliding_menu
 * 包名：com.example.sliding_menu1.Activity
 * 作者： win
 * 创建日期：2016/4/15 16:11
 * 实现的主要功能：
 */
public class WeatherActivity extends Activity {

    private Context mContext;
    private PullToRefreshScrollView pullToRefreshScrollView;
    private ScrollView scrollView;

    private WeatherService mService;

    private TextView tv_city,// 城市
            tv_release,// 发布时间
            tv_now_weather,// 天气
            tv_today_temp,// 温度
            tv_now_temp,// 当前温度
            tv_aqi,// 空气质量指数
            tv_quality,// 空气质量
            tv_next_three,// 3小时
            tv_next_six,// 6小时
            tv_next_nine,// 9小时
            tv_next_twelve,// 12小时
            tv_next_fifteen,// 15小时
            tv_next_three_temp,// 3小时温度
            tv_next_six_temp,// 6小时温度
            tv_next_nine_temp,// 9小时温度
            tv_next_twelve_temp,// 12小时温度
            tv_next_fifteen_temp,// 15小时温度
            tv_today_temp_a,// 今天温度a
            tv_today_temp_b,// 今天温度b
            tv_tommorrow,// 明天
            tv_tommorrow_temp_a,// 明天温度a
            tv_tommorrow_temp_b,// 明天温度b
            tv_thirdday,// 第三天
            tv_thirdday_temp_a,// 第三天温度a
            tv_thirdday_temp_b,// 第三天温度b
            tv_fourthday,// 第四天
            tv_fourthday_temp_a,// 第四天温度a
            tv_fourthday_temp_b,// 第四天温度b
            tv_humidity,// 湿度
            tv_wind, tv_uv_index,// 紫外线指数
            tv_dressing_advice,//穿衣推荐
            tv_dressing_index;// 穿衣指数


    private ImageView iv_now_weather,// 现在
            iv_next_three,// 3小时
            iv_next_six,// 6小时
            iv_next_nine,// 9小时
            iv_next_twelve,// 12小时
            iv_next_fifteen,// 15小时
            iv_today_weather,// 今天
            iv_tommorrow_weather,// 明天
            iv_thirdday_weather,// 第三天
            iv_fourthday_weather;// 第四天

    private RelativeLayout rl_city;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
//        getCityWeather();
        mContext=this;
        init();
        initService();
    }
    //绑定服务
    private void initService(){
        Intent intent=new Intent(mContext, WeatherService.class);
        startService(intent);
        bindService(intent,conn,Context.BIND_AUTO_CREATE);
    }
    //服务连接
    ServiceConnection conn=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService=((WeatherService.WeatherServiceBinder)service).getService();
            mService.setCallBack(new WeatherService.OnParserCallBack() {
                @Override
                public void OnParserComplete(List<HourWeatherBean> list, PMBean pmBean, WeatherBean weatherBean) {
                    pullToRefreshScrollView.onRefreshComplete();
                    if (list != null && list.size() >= 5) {
                        setHourViews(list);
                    }

                    if (pmBean != null) {
                        setPMView(pmBean);
                    }

                    if (weatherBean != null) {
                        setWeatherviews(weatherBean);
                    }
                }
            });
            //绑定的同时,调用
            mService.getCityWeather();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService.removeCallBack();
        }
    };


    private void setPMView(PMBean bean){
        tv_aqi.setText(bean.getAqi());
        tv_quality.setText(bean.getQuality());
    }

    //给控件传入数据的方法
    private void setWeatherviews(WeatherBean bean){
        tv_city.setText(bean.getCity());
        tv_now_weather.setText(bean.getRelease());
        tv_now_weather.setText(bean.getWeather_str());

        //对字符串进行拆分
        String[] tempArr =bean.getTemp().split("~");
        String temp_str_a = tempArr[1].substring(0, tempArr[1].indexOf("℃"));
        String temp_str_b = tempArr[0].substring(0, tempArr[0].indexOf("℃"));
        // 温度 8℃~16℃" ↑ ↓ °
        tv_today_temp.setText("高:" + temp_str_a + "℃   低:" + temp_str_b + "℃");
        tv_now_temp.setText(bean.getNow_temp() +"°");
        iv_today_weather.setImageResource(getResources().getIdentifier("d" + bean.getWeather_id(), "drawable", "com.example.sliding_menu1"));


        tv_today_temp_a.setText(temp_str_a + "℃");
        tv_today_temp_b.setText(temp_str_b + "℃");
        List<FutureWeatherBean> futureList=bean.getFuturelist();
        if (futureList != null && futureList.size() == 3) {
            setFutureData(tv_tommorrow, iv_tommorrow_weather, tv_tommorrow_temp_a, tv_tommorrow_temp_b, futureList.get(0));
            setFutureData(tv_thirdday, iv_thirdday_weather, tv_thirdday_temp_a, tv_thirdday_temp_b, futureList.get(1));
            setFutureData(tv_fourthday, iv_fourthday_weather, tv_fourthday_temp_a, tv_fourthday_temp_b, futureList.get(2));
        }
        //现在天气的图片
        Calendar c=Calendar.getInstance();
        int time=c.get(Calendar.HOUR_OF_DAY);
        String prefixStr=null;
        if (time>=6&&time<18){
            //白天
            prefixStr="d";
        }else {
            //晚上
            prefixStr="n";
        }
        iv_now_weather.setImageResource(getResources().getIdentifier
                (prefixStr + bean.getWeather_id(), "drawable", "com.example.sliding_menu1"));

        tv_humidity.setText(bean.getHumidity());
        tv_dressing_index.setText(bean.getDressing_index());
        tv_uv_index.setText(bean.getUv_index());
        tv_wind.setText(bean.getWind());
        tv_dressing_advice.setText(bean.getDressing_advice());
    }
    private void setHourViews(List<HourWeatherBean> list){
        //设置每个三小时的显示
        setHourData(tv_next_three,iv_next_three,tv_next_three_temp,list.get(0));
        setHourData(tv_next_six,iv_next_six,tv_next_six_temp,list.get(1));
        setHourData(tv_next_nine,iv_next_nine,tv_next_nine_temp,list.get(2));
        setHourData(tv_next_twelve,iv_next_twelve,tv_next_twelve_temp,list.get(3));
        setHourData(tv_next_fifteen,iv_next_fifteen,tv_next_fifteen_temp,list.get(4));
    }
    private void setHourData(TextView tv_hour,ImageView iv_weather,TextView tv_temp,HourWeatherBean bean){
        String prefixStr=null;
        int time=Integer.valueOf(bean.getTime());
        if (time>=6&&time<18){
            //白天
            prefixStr="d";
        }else {
            //晚上
            prefixStr="n";
        }

        tv_hour.setText(bean.getTime()+"点");
        iv_weather.setImageResource(getResources().getIdentifier
                (prefixStr + bean.getWeather_id(), "drawable", "com.example.sliding_menu1"));
        tv_temp.setText(bean.getTemp()+"℃");

    }
    private void setFutureData(TextView tv_week, ImageView iv_weather, TextView tv_temp_a, TextView tv_temp_b, FutureWeatherBean bean){
        tv_week.setText(bean.getWeek());
        iv_weather.setImageResource(getResources().getIdentifier("d" + bean.getWeather_id(), "drawable", "com.example.sliding_menu1"));
        String[] tempArr = bean.getTemp().split("~");
        String temp_str_a = tempArr[1].substring(0, tempArr[1].indexOf("℃"));
        String temp_str_b = tempArr[0].substring(0, tempArr[0].indexOf("℃"));
        tv_temp_a.setText(temp_str_a+"℃" );
        tv_temp_b.setText(temp_str_b+"℃");

    }

    //requestCode是来确定是哪个activity启动的，resultCode是来确实是哪个activity返回的
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==1&&resultCode==1){
            String city=data.getStringExtra("city");
            mService.getCityWeather(city);
        }
    }


    private void init() {
        pullToRefreshScrollView= (PullToRefreshScrollView) findViewById(R.id.pull_refresh_scrollview);
        //给下拉刷新的控件添加刷新方法
        pullToRefreshScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
//                getCityWeather();
                mService.getCityWeather();
            }
        });
        scrollView=pullToRefreshScrollView.getRefreshableView();

        rl_city = (RelativeLayout) findViewById(R.id.rl_city);
        rl_city.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                startActivityForResult(new Intent(mContext, CityActivity.class), 1);

            }
        });

        tv_city = (TextView) findViewById(R.id.tv_city);
        tv_release = (TextView) findViewById(R.id.tv_release);
        tv_now_weather = (TextView) findViewById(R.id.tv_now_weather);
        tv_today_temp = (TextView) findViewById(R.id.tv_today_temp);
        tv_now_temp = (TextView) findViewById(R.id.tv_now_temp);
        tv_aqi = (TextView) findViewById(R.id.tv_aqi);
        tv_quality = (TextView) findViewById(R.id.tv_quality);
        tv_next_three = (TextView) findViewById(R.id.tv_next_three);
        tv_next_six = (TextView) findViewById(R.id.tv_next_six);
        tv_next_nine = (TextView) findViewById(R.id.tv_next_nine);
        tv_next_twelve = (TextView) findViewById(R.id.tv_next_twelve);
        tv_next_fifteen = (TextView) findViewById(R.id.tv_next_fifteen);
        tv_next_three_temp = (TextView) findViewById(R.id.tv_next_three_temp);
        tv_next_six_temp = (TextView) findViewById(R.id.tv_next_six_temp);
        tv_next_nine_temp = (TextView) findViewById(R.id.tv_next_nine_temp);
        tv_next_twelve_temp = (TextView) findViewById(R.id.tv_next_twelve_temp);
        tv_next_fifteen_temp = (TextView) findViewById(R.id.tv_next_fifteen_temp);
        tv_today_temp_a = (TextView) findViewById(R.id.tv_today_temp_a);
        tv_today_temp_b = (TextView) findViewById(R.id.tv_today_temp_b);
        tv_tommorrow = (TextView) findViewById(R.id.tv_tommorrow);
        tv_tommorrow_temp_a = (TextView) findViewById(R.id.tv_tommorrow_temp_a);
        tv_tommorrow_temp_b = (TextView) findViewById(R.id.tv_tommorrow_temp_b);
        tv_thirdday = (TextView) findViewById(R.id.tv_thirdday);
        tv_thirdday_temp_a = (TextView) findViewById(R.id.tv_thirdday_temp_a);
        tv_thirdday_temp_b = (TextView) findViewById(R.id.tv_thirdday_temp_b);
        tv_fourthday = (TextView) findViewById(R.id.tv_fourthday);
        tv_fourthday_temp_a = (TextView) findViewById(R.id.tv_fourthday_temp_a);
        tv_fourthday_temp_b = (TextView) findViewById(R.id.tv_fourthday_temp_b);
        tv_humidity = (TextView) findViewById(R.id.tv_humidity);
        tv_wind = (TextView) findViewById(R.id.tv_wind);
        tv_uv_index = (TextView) findViewById(R.id.tv_uv_index);
        tv_dressing_index = (TextView) findViewById(R.id.tv_dressing_index);
        tv_dressing_advice= (TextView) findViewById(R.id.tv_dressing_advice);

        iv_now_weather = (ImageView) findViewById(R.id.iv_now_weather);
        iv_next_three = (ImageView) findViewById(R.id.iv_next_three);
        iv_next_six = (ImageView) findViewById(R.id.iv_next_six);
        iv_next_nine = (ImageView) findViewById(R.id.iv_next_nine);
        iv_next_twelve = (ImageView) findViewById(R.id.iv_next_twelve);
        iv_next_fifteen = (ImageView) findViewById(R.id.iv_next_fifteen);
        iv_today_weather = (ImageView) findViewById(R.id.iv_today_weather);
        iv_tommorrow_weather = (ImageView) findViewById(R.id.iv_tommorrow_weather);
        iv_thirdday_weather = (ImageView) findViewById(R.id.iv_thirdday_weather);
        iv_fourthday_weather = (ImageView) findViewById(R.id.iv_fourthday_weather);
    }

    @Override
    protected void onDestroy() {

        unbindService(conn);
        super.onDestroy();
    }
}
