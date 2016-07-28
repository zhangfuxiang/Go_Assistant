package com.example.sliding_menu1.Fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import com.example.sliding_menu1.Activity.BrowserActivity;
import com.example.sliding_menu1.Activity.CalendarActivity;
import com.example.sliding_menu1.Activity.CalulaterActivity;
import com.example.sliding_menu1.Activity.DownloadActivity;
import com.example.sliding_menu1.Activity.FlashlightActivity;
import com.example.sliding_menu1.Activity.LivingActivity;
import com.example.sliding_menu1.Activity.StepCountActivity;
import com.example.sliding_menu1.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 工程名：Sliding_menu
 * 包名：com.example.sliding_menu1.Fragment
 * 作者： win
 * 创建日期：2016/3/26 16:44
 * 实现的主要功能：
 */
public class FragmentOften extends Fragment implements AdapterView.OnItemClickListener {

    private GridView mGridView;
    private SimpleAdapter adapter;
    private Context context;
    private List<Map<String, Object>> dataList;

    private int[] image={R.mipmap.ic_browser, R.mipmap.ic_caculater,
            R.mipmap.ic_flashlight,R.mipmap.ic_calendar,R.mipmap.ic_school,R.mipmap.ic_stepcounter};
    private String[] text = { "浏览器", "计算器", "手电筒","日历","校内助手","计步器"};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fg_often,container,false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context=getView().getContext();
        initGridView();
    }

    private void initGridView() {
        mGridView= (GridView) getView().findViewById(R.id.use_gridView);
        mGridView.setOnItemClickListener(this);
        dataList=new ArrayList<Map<String, Object>>();
        adapter=new SimpleAdapter(context,getData(),R.layout.gridview_item,new String[]{"image","text"},
                new int[]{R.id.id_pic,R.id.id_text});
        mGridView.setAdapter(adapter);
    }

    private List<Map<String,Object>> getData() {
        for (int i=0;i<image.length;i++){
            HashMap<String, Object> map=new HashMap<String, Object>();
            map.put("image", image[i]);
            map.put("text", text[i]);
            dataList.add(map);
        }
        return dataList;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch(position){
            case 0:
                startActivity(new Intent(context, BrowserActivity.class));
                break;
            case 1:
                startActivity(new Intent(context, CalulaterActivity.class));
                break;
            case 2:
                startActivity(new Intent(context, FlashlightActivity.class));
                break;
            case 3:
                startActivity(new Intent(context, CalendarActivity.class));
                break;
            case 4:
                startActivity(new Intent(context, LivingActivity.class));
                break;
            case 5:
                startActivity(new Intent(context, StepCountActivity.class));
                break;
        }
    }
}
