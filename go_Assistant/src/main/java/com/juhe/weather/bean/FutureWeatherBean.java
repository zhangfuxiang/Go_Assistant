package com.juhe.weather.bean;

/**
 * 工程名：Sliding_menu
 * 包名：com.juhe.weather.bean
 * 作者： win
 * 创建日期：2016/4/16 14:19
 * 实现的主要功能：
 */
public class FutureWeatherBean {
    private String week;
    private String Weather_id;
    private String temp;
    private String date;//判断时间

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getWeather_id() {
        return Weather_id;
    }

    public void setWeather_id(String weather_id) {
        Weather_id = weather_id;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }



}
