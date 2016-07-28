package com.example.sliding_menu1.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sliding_menu1.R;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 工程名：Sliding_menu
 * 包名：com.example.sliding_menu1.Activity
 * 作者： win
 * 创建日期：2016/5/5 13:06
 * 实现的主要功能：修改密码
 */
public class ModifyPwdActivity extends Activity{

    private EditText et_old_password,et_new_password,et_again_password;
    private Button btn_submit;
    private String oldPwd="",newPwd="",againPwd="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_modifypwd);
        init();
        setListener();
    }
    //设置监听
    private void setListener() {
        findViewById(R.id.ibtn_modifypwd_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modifypwd();
            }
        });
    }
    //修改密码
    private void modifypwd() {
        oldPwd=et_old_password.getText().toString().trim();
        newPwd=et_new_password.getText().toString().trim();
        againPwd=et_again_password.getText().toString().trim();
        if (!oldPwd.equals("")&&!newPwd.equals("")&&!againPwd.equals("")){
            if (newPwd.equals(againPwd)){
                modifypwd(oldPwd,newPwd);
            }else {
                Toast.makeText(getApplicationContext(),"两次输入的密码不一致",Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(getApplicationContext(),"任何一项不能为空",Toast.LENGTH_SHORT).show();
        }
    }
    //根据旧密码更改密码
    private void modifypwd(String oldPwd, String newPwd) {
        BmobUser.updateCurrentUserPassword(ModifyPwdActivity.this, oldPwd,
                newPwd, new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(getApplicationContext(),"修改密码成功请重新登陆",Toast.LENGTH_SHORT).show();
                        LoginActivity.editor.putString(LoginActivity.PASSWORD,null);
                        LoginActivity.editor.commit();
                        Intent intent=new Intent(ModifyPwdActivity.this,LoginActivity.class);
                        startActivity(intent);
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
                            case 210: {// 旧密码错误
                                Toast.makeText(getApplicationContext(),"请输入正确的旧密码",Toast.LENGTH_SHORT).show();

                                break;
                            }
                            default: {
                                Toast.makeText(getApplicationContext(),"修改密码失败，请重试",Toast.LENGTH_SHORT).show();
                                break;
                            }
                        }
                    }
                });
    }
    //控件初始化
    private void init() {
        et_old_password= (EditText) findViewById(R.id.et_old_password);
        et_new_password= (EditText) findViewById(R.id.et_new_password);
        et_again_password= (EditText) findViewById(R.id.et_agian_password);
        btn_submit= (Button) findViewById(R.id.btn_modifypwd_submit);

    }
}
