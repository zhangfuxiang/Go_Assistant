package com.example.sliding_menu1.Fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sliding_menu1.R;

/**
 * 工程名：Sliding_menu
 * 包名：com.example.sliding_menu1.Fragment
 * 作者： win
 * 创建日期：2016/4/13 0:26
 * 实现的主要功能：
 */
public class FragmentAbout extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_about, container, false);
        return view;
    }
}
