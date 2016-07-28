package com.example.sliding_menu1.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sliding_menu1.R;
import com.example.sliding_menu1.contants.Config;
import com.example.sliding_menu1.entity.GlobalParams;
import com.example.sliding_menu1.entity.User;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.SaveListener;

/**
 * 工程名：Sliding_menu
 * 包名：com.example.sliding_menu1.Activity
 * 作者： win
 * 创建日期：2016/5/1 12:40
 * 实现的主要功能：登录页面
 */
public class LoginActivity extends Activity {

    private EditText et_username,et_password;
    private ProgressBar progressBar;
    private ImageButton ibtn_login;
    private CheckBox cb_remember_user;

    public static SharedPreferences preferences;
    public static SharedPreferences.Editor editor;
    public static final String USER_NAME="username";
    public static final String PASSWORD="password";
    public static final String REMEMBER_USER="cb_remember";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        //bmob初始化
        Bmob.initialize(this, Config.APP_ID);
        preferences=getSharedPreferences("mydata",MODE_PRIVATE);
        editor=preferences.edit();

        init();
        initdata();
        //登录按钮监听事件
        ibtn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if (cb_remember_user.isChecked()){
                    editor.putString(USER_NAME,et_username.getText().toString().trim());
                    editor.putString(PASSWORD,et_password.getText().toString().trim());
                    editor.putBoolean(REMEMBER_USER,true);

                }else {
                    editor.putString(USER_NAME,null);
                    editor.putString(PASSWORD,null);
                    editor.putBoolean(REMEMBER_USER,false);

                }
                editor.commit();
                final User user=new User();
                user.setUsername(et_username.getText().toString().trim());
                user.setPassword(et_password.getText().toString().trim());
                user.login(getApplicationContext(), new SaveListener() {
                    @Override
                    public void onSuccess() {
                        User userInfo= BmobUser.getCurrentUser(LoginActivity.this,User.class);
                        loginSuccess(userInfo);

                    }

                    @Override
                    public void onFailure(int i, String s) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(LoginActivity.this,"登陆失败:"+s,Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

    }

    //点击返回键事件处理
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode==KeyEvent.KEYCODE_BACK){
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    //关闭程序方法
    private void exit() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("提示")
                .setMessage("确定要退出程序吗？")
                .setPositiveButton("是", dialogListener)
                .setNegativeButton("否", dialogListener)
                .create()
                .show();
    }
    //退出对话框按钮监听
    DialogInterface.OnClickListener dialogListener= new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {

            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    finish();
                    break;
                default:
                    dialog.cancel();
                    break;
            }
        }
    };
    private void initdata() {
        if(preferences.getBoolean(REMEMBER_USER,false)==true){
            et_username.setText(preferences.getString(USER_NAME,null));
            et_password.setText(preferences.getString(PASSWORD,null));
            cb_remember_user.setChecked(true);
        }else
        {
            cb_remember_user.setChecked(false);
        }
    }

    private void loginSuccess(User userInfo){
        GlobalParams.userInfo=userInfo;//添加全局
        if(GlobalParams.userInfo.getName()==null||
                GlobalParams.userInfo.getName().equals("")){
            GlobalParams.userInfo.setName(userInfo.getName());
        }
        //记录用户登录密码
        GlobalParams.userInfo.setPassword_need(et_password.getText().toString().trim());
        Log.i("asd","loginSuccess:"+GlobalParams.userInfo.getPassword_need());
        Intent itent=new Intent(LoginActivity.this,MainActivity.class);
        startActivity(itent);
        finish();

    }

    private void init() {
        et_username= (EditText) findViewById(R.id.et_username);
        et_password= (EditText) findViewById(R.id.et_password);
        ibtn_login= (ImageButton) findViewById(R.id.ibtn_login);
        progressBar= (ProgressBar) findViewById(R.id.progressBar);
        cb_remember_user= (CheckBox) findViewById(R.id.cb_remember_user);
        findViewById(R.id.tv_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent itent=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(itent);
            }
        });

    }
}
