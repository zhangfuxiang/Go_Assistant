package com.juhe.weather.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.sliding_menu1.R;

import java.util.List;

/**
 * 工程名：Sliding_menu
 * 包名：com.juhe.weather.adapter
 * 作者： win
 * 创建日期：2016/4/17 19:45
 * 实现的主要功能：
 */
public class CityListAdatper extends BaseAdapter {

    private List<String> list;
    private LayoutInflater layoutInflater;

    public CityListAdatper(Context context,List<String> list){
        this.list=list;
        layoutInflater=LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public String getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView=null;
        if (convertView==null){
            rowView=layoutInflater.inflate(R.layout.item_city_list,null);
        }else {
            rowView=convertView;
        }
        TextView tv_city= (TextView) rowView.findViewById(R.id.tv_city1);
        tv_city.setText(getItem(position));
        return rowView;


    }
}
