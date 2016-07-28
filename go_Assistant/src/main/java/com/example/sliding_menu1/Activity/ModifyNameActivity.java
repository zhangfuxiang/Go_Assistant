package com.example.sliding_menu1.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sliding_menu1.R;
import com.example.sliding_menu1.entity.CommonConstant;
import com.example.sliding_menu1.entity.GlobalParams;
import com.example.sliding_menu1.entity.SpUtils;

import cn.bmob.v3.listener.UpdateListener;

/**
 * 工程名：Sliding_menu
 * 包名：com.example.sliding_menu1.Activity
 * 作者： win
 * 创建日期：2016/5/5 13:46
 * 实现的主要功能：修改昵称
 */
public class ModifyNameActivity extends Activity {

    protected SpUtils sputil;
    private EditText et_modifyname;
    private Button btn_modifyname_submit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_modifyname);
        init();
        setListener();
        initData();
    }

    private void initData() {
    }

    private void setListener() {
        findViewById(R.id.ibtn_modifyname_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_modifyname_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=et_modifyname.getText().toString().trim();
                if (!name.equals("")){
                    modifyName(name);
                }else {
                    Toast.makeText(getApplicationContext(),"用户昵称不允许为空",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void modifyName(String name) {
        GlobalParams.userInfo.setName(name);
        GlobalParams.userInfo.update(getApplicationContext(), GlobalParams.userInfo.getObjectId(),
                new UpdateListener() {
                    @Override
                    public void onSuccess() {

                        setResult(CommonConstant.RESULTCODE_EDIT_NICK_OK);
/*                        sputil.setValue(CommonConstant.NICK_KEY,
                                GlobalParams.userInfo.getName());*/
                        finish();
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        switch (i) {
                            case 9010: {// 网络超时
                                Toast.makeText(getApplicationContext(),"网络超时，请检查您的手机网络",Toast.LENGTH_SHORT).show();
                                break;
                            }
                            case 9016: {// 无网络连接，请检查您的手机网络
                                Toast.makeText(getApplicationContext(),"无网络连接，请检查您的手机网络",Toast.LENGTH_SHORT).show();
                                break;
                            }
                            default: {
                                Toast.makeText(getApplicationContext(),"修改昵称失败，请重试",Toast.LENGTH_SHORT).show();
                                break;
                            }
                        }
                    }
                });
    }

    private void init() {
        et_modifyname= (EditText) findViewById(R.id.et_new_name);
        btn_modifyname_submit= (Button) findViewById(R.id.btn_modifyname_submit);


    }
}
