package com.example.sliding_menu1.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sliding_menu1.R;
import com.example.sliding_menu1.entity.User;

import cn.bmob.v3.listener.SaveListener;

/**
 * 工程名：Sliding_menu
 * 包名：com.example.sliding_menu1.Activity
 * 作者： win
 * 创建日期：2016/5/1 12:42
 * 实现的主要功能：注册页面
 */
public class RegisterActivity extends Activity {

    private EditText name,username,password,agianpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_register);
        init();
    }

    private void init() {
        name= (EditText) findViewById(R.id.et_name);
        username= (EditText) findViewById(R.id.et_username);
        password= (EditText) findViewById(R.id.et_password);
        agianpassword= (EditText) findViewById(R.id.et_agian_password);
        findViewById(R.id.register_btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.btn_reigster).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!name.getText().toString().equals("")&&!username.getText().toString().equals("")
                        &&!password.getText().toString().equals("")&&!agianpassword.getText().toString().equals("")){
                    if (!password.getText().toString().equals(agianpassword.getText().toString())){
                        Toast.makeText(getApplicationContext(),"两次输入密码不一致",Toast.LENGTH_SHORT).show();
                    }else{
                        User user=new User();
                        user.setUsername(username.getText().toString());
                        user.setPassword(password.getText().toString());
                        user.setName(name.getText().toString());
                        user.signUp(getApplicationContext(), new SaveListener() {
                            @Override
                            public void onSuccess() {
                                Toast.makeText(getApplicationContext(),"注册成功",Toast.LENGTH_SHORT).show();
                                finish();
                            }

                            @Override
                            public void onFailure(int i, String s) {
                                Toast.makeText(getApplicationContext(),"注册失败:"+s,Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                }else {
                    Toast.makeText(getApplicationContext(),"任何一项不能为空",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
