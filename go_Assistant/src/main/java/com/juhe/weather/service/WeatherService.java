package com.juhe.weather.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.juhe.weather.bean.FutureWeatherBean;
import com.juhe.weather.bean.HourWeatherBean;
import com.juhe.weather.bean.PMBean;
import com.juhe.weather.bean.WeatherBean;
import com.thinkland.juheapi.common.JsonCallBack;
import com.thinkland.juheapi.data.air.AirData;
import com.thinkland.juheapi.data.weather.WeatherData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * 工程名：Sliding_menu
 * 包名：com.juhe.weather.service
 * 作者： win
 * 创建日期：2016/4/17 17:09
 * 实现的主要功能：
 */
public class WeatherService extends Service{

    private final String tag="WeatherService";
    private WeatherServiceBinder binder=new WeatherServiceBinder();
    //判断回调函数的执行
    private String city;
    private boolean isRunning=false;
    private int count=0;
    private List<HourWeatherBean> list;
    private PMBean pmBean;
    private WeatherBean weatherBean;

    private OnParserCallBack callBack;

    private final int REPEAT_MSG = 0x01;
    private final int CALLBACK_OK = 0x02;
    private final int CALLBACK_ERROR = 0x04;
    //设置回调
    public interface OnParserCallBack{
        public void OnParserComplete(List<HourWeatherBean> list,PMBean pmBean,WeatherBean weatherBean);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(tag,"onCreate");
        city="福州";
        handler.sendEmptyMessage(REPEAT_MSG);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(tag,"onDestroy");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(tag,"onStartCommand");
        return super.onStartCommand(intent, flags, startId);

    }
    public void test(){
        Log.i(tag,"test!!!!!");
    }

    android.os.Handler handler=new android.os.Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REPEAT_MSG:

                    getCityWeather();
                    sendEmptyMessageDelayed(REPEAT_MSG, 30 * 60 * 1000);
                    break;
                case CALLBACK_OK:
                    if (callBack != null) {
                        callBack.OnParserComplete(list, pmBean, weatherBean);
                    }
                    isRunning = false;
                    break;
                case CALLBACK_ERROR:
                    Toast.makeText(getApplicationContext(), "loading error", Toast.LENGTH_SHORT).show();
                    break;

                default:
                    break;
            }
        }
    };
    public void setCallBack(OnParserCallBack callBack){
        this.callBack=callBack;
    }

    public void removeCallBack(){
        callBack=null;
    }
    //获取城市天气数据
    public void getCityWeather(){
        count=0;

        WeatherData data=WeatherData.getInstance();
        //获取该城市的天气数据
        data.getByCitys(city, 2, new JsonCallBack() {
            @Override
            public void jsonLoaded(JSONObject jsonObject) {
                Log.i("asd",jsonObject.toString());
                weatherBean=parserWeather(jsonObject);
                count++;
                if (weatherBean!=null){
                    //给控件传入数据
//                    setWeatherviews(bean);
                }
                //三个回调都执行完就解释刷新
                if (count==3){
                    if (callBack!=null){
                        callBack.OnParserComplete(list,pmBean,weatherBean);
                    }
//                    pullToRefreshScrollView.onRefreshComplete();
                }
            }
        });

        //获取该城市未来每三个小时的数据
        data.getForecast3h(city, new JsonCallBack() {
            @Override
            public void jsonLoaded(JSONObject jsonObject) {
                Log.i("asd",jsonObject.toString());
                try {
                    list=parserForecast3h(jsonObject);
                    count++;
                    if (list!=null&&list.size()>=5){
//                        setHourViews(list);
                    }
                    //三个回调都执行完就解释刷新
                    if (count==3){
                        if (callBack!=null){
                            callBack.OnParserComplete(list,pmBean,weatherBean);
                        }
//                        pullToRefreshScrollView.onRefreshComplete();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        //获取该城市空气质量的数据
        AirData airData= AirData.getInstance();
        airData.cityAir(city, new JsonCallBack() {
            @Override
            public void jsonLoaded(JSONObject jsonObject) {
                Log.i("asd",jsonObject.toString());
                pmBean=parserPM(jsonObject);
                count++;
                if (pmBean!=null){
//                    setPMView(bean);
                }
                //三个回调都执行完就解释刷新
                if (count==3){
                    if (callBack!=null){
                        callBack.OnParserComplete(list,pmBean,weatherBean);
                    }
//                    pullToRefreshScrollView.onRefreshComplete();
                }
            }
        });

    }

    //更新城市名称
    public void getCityWeather(String city){
        this.city=city;
        getCityWeather();
    }

    //PM数据解析
    private PMBean parserPM(JSONObject json){
        PMBean bean=null;
        try {
            int resulcode=json.getInt("resultcode");
            int error_code=json.getInt("error_code");
            if(error_code==0&&resulcode==200){
                bean=new PMBean();
                JSONObject pmJson=json.getJSONArray("result").getJSONObject(0).getJSONObject("citynow");
                bean.setAqi(pmJson.getString("AQI"));
                bean.setQuality(pmJson.getString("quality"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return bean;
    }
    //解析城市天气数据
    private WeatherBean parserWeather(JSONObject json){
        WeatherBean bean=null;
        //设置时间格式
        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");

        try {
            int resulcode=json.getInt("resultcode");
            int error_code=json.getInt("error_code");
            if(error_code==0&&resulcode==200){
                JSONObject resultJson=json.getJSONObject("result");
                bean=new WeatherBean();

                //today
                JSONObject todayJson=resultJson.getJSONObject("today");
                bean.setCity(todayJson.getString("city"));
                bean.setUv_index(todayJson.getString("uv_index"));
                bean.setTemp(todayJson.getString("temperature"));
                bean.setWeather_str(todayJson.getString("weather"));
                bean.setWeather_id(todayJson.getJSONObject("weather_id").getString("fa"));
                bean.setDressing_index(todayJson.getString("dressing_index"));
                bean.setDressing_advice(todayJson.getString("dressing_advice"));
                //sk
                JSONObject skJson=resultJson.getJSONObject("sk");
                bean.setWind(skJson.getString("wind_direction")+skJson.getString("wind_strength"));
                bean.setNow_temp(skJson.getString("temp"));
                bean.setRelease(skJson.getString("time"));
                bean.setHumidity(skJson.getString("humidity"));

                //计算时间
                Date date=new Date(System.currentTimeMillis());
                //future
                JSONArray futureArray=resultJson.getJSONArray("future");
                List<FutureWeatherBean> futurelist=new ArrayList<FutureWeatherBean>();
                for (int i=0;i<futureArray.length();i++){
                    JSONObject futureJson=futureArray.getJSONObject(i);
                    FutureWeatherBean futureWeatherBean=new FutureWeatherBean();
                    Date datef=sdf.parse(futureJson.getString("date"));
                    if (!datef.after(date)){
                        continue;
                    }

                    futureWeatherBean.setTemp(futureJson.getString("temperature"));
                    futureWeatherBean.setWeek(futureJson.getString("week"));
                    futureWeatherBean.setWeather_id(futureJson.getJSONObject("weather_id").getString("fa"));
                    futurelist.add(futureWeatherBean);
                    if (futurelist.size()==3){
                        break;
                    }
                }
                bean.setFuturelist(futurelist);
            }else{
                Toast.makeText(getApplicationContext(),"Weather_error",Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return bean;
    }
    //解析未来三小时天气数据
    private List<HourWeatherBean> parserForecast3h(JSONObject json) throws ParseException {
        List<HourWeatherBean> list=null;
        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddhhmmss");
        Date date=new Date(System.currentTimeMillis());
        try {
            int code=json.getInt("resultcode");
            int error_code=json.getInt("error_code");
            if (error_code==0&&code==200){
                list=new ArrayList<HourWeatherBean>();
                JSONArray resultArray=json.getJSONArray("result");
                for (int i=0;i<resultArray.length();i++){
                    JSONObject hourJson=resultArray.getJSONObject(i);
                    Date hDate=sdf.parse(hourJson.getString("sfdate"));
                    if (!hDate.after(date)){
                        continue;
                    }
                    HourWeatherBean bean=new HourWeatherBean();
                    bean.setWeather_id(hourJson.getString("weatherid"));
                    bean.setTemp(hourJson.getString("temp1"));
                    Calendar c=Calendar.getInstance();
                    c.setTime(hDate);
                    bean.setTime(c.get(Calendar.HOUR_OF_DAY)+"");
                    list.add(bean);
                    if (list.size()==5){
                        break;
                    }
                }
            }else {
                Toast.makeText(getApplicationContext(),"Hours_error",Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
    //进行通信的类
    public class WeatherServiceBinder extends Binder{
        public WeatherService getService(){
            return WeatherService.this;
        }
    }
}
