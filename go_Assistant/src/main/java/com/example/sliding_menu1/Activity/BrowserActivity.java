package com.example.sliding_menu1.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.sliding_menu1.R;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 工程名：Sliding_menu
 * 包名：com.example.sliding_menu1.Activity
 * 作者： win
 * 创建日期：2016/4/12 14:15
 * 实现的主要功能：浏览器功能
 */
public class BrowserActivity extends Activity implements View.OnClickListener {
    private WebView wv;
    private Button btn_go;
    private ImageButton btn_forward, btn_back, btn_home, btn_refresh, btn_close;
    private EditText et_input;
    private boolean isExit;
    private ArrayAdapter<String> arr_adapter;

    private String result=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);
        closeStrictMode();
        init();
        cilent();
        isExit=false;
        initinput();
        Intent intent=this.getIntent();
        result=intent.getStringExtra("result");
        Log.i("asd","内容为:"+result);
        if (result == null) {
            wv.loadUrl("http://www.baidu.com");
        }else {
            wv.loadUrl(result);
            result=null;
        }

    }
    //二维码扫码得到的网址进行访问
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK){
            result=data.getExtras().getString("result");
            Log.i("asd",result);

        }
    }
    //保存输入的网址
    private void initinput() {
        SharedPreferences sp=getSharedPreferences("his", 0);
        String history=sp.getString("history", "no data");
        String[] history_array=history.split(",");
        arr_adapter=new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line
                ,history_array);
//        et_input.setAdapter(arr_adapter);
    }

    private void cilent() {
        wv.setWebChromeClient(new WebChromeClient(){
            public boolean shouldOverrideUrlLoading(WebView view, String url) { //
                view.loadUrl(url);
                return true;
            }
        });
        wv.setWebViewClient(new WebViewClient(){
            public boolean shouldOverrideUrlLoading(WebView view, String url) { //
                view.loadUrl(url);
                return true;
            }

            public void onPageFinished(WebView view, String url) {
                et_input.setText(wv.getUrl().toString());
                et_input.clearFocus();
                //closeInputMethod();
            }
        });
    }
    //控件初始化
    private void init() {
        //控件初始化
        wv = (WebView) findViewById(R.id.wv_browser);
        et_input = (EditText) findViewById(R.id.et_input_url);
        btn_go= (Button) findViewById(R.id.btn_go);
        btn_forward= (ImageButton) findViewById(R.id.btn_forward);
        btn_back= (ImageButton) findViewById(R.id.btn_back);
        btn_home= (ImageButton) findViewById(R.id.btn_home);
        btn_refresh= (ImageButton) findViewById(R.id.btn_refresh);
        btn_close= (ImageButton) findViewById(R.id.btn_close);

        //设置按钮监听
        btn_go.setOnClickListener(this);
        btn_forward.setOnClickListener(this);
        btn_back.setOnClickListener(this);
        btn_home.setOnClickListener(this);
        btn_refresh.setOnClickListener(this);
        btn_close.setOnClickListener(this);

        WebSettings ws = wv.getSettings();
        //支持js脚本
        ws.setJavaScriptEnabled(true);
        ws.setJavaScriptCanOpenWindowsAutomatically(true);
        //支持缓存
        ws.setAppCacheEnabled(true);
        ws.setAppCacheMaxSize(10240);

        ws.setCacheMode(WebSettings.LOAD_NO_CACHE);

    }

    private void closeStrictMode() {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectAll().penaltyLog().build());
    }



    //点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btn_go:
                conn();
                break;
            case R.id.btn_forward:
                forward();
                break;
            case R.id.btn_back:
                back();
                break;
            case R.id.btn_home:
                wv.loadUrl("http://www.baidu.com");
                break;
            case R.id.btn_refresh:
                wv.reload();
                Toast.makeText(this, "正在刷新", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_close:
                exit();
                break;
        }
    }
    //退出操作
    private void exit() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("提示")
                .setMessage("确定要退出吗？")
                .setPositiveButton("是", dialogListener)
                .setNegativeButton("否", dialogListener)
                .create()
                .show();
    }
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
    //向后方法
    private void back() {
        if(wv.canGoBack()){
            wv.goBack();
        }else{
            Toast.makeText(this, "已经是第一页", Toast.LENGTH_SHORT).show();
        }
    }
    //后前方法
    private void forward() {
        if(wv.canGoForward())
        {
            wv.goForward();
        } else{
            Toast.makeText(this, "已经是最新页", Toast.LENGTH_SHORT).show();
        }
    }
    //访问方法
    private void conn() {
        String url;
        url=et_input.getText().toString();
        if(url.contains("http://")||url.contains("https://"))
        {
            if (URLUtil.isNetworkUrl(url))
                wv.loadUrl(url);
        }else if(isConnection("http://"+url))wv.loadUrl("http://"+url);
        else wv.loadUrl("http://www.baidu.com/s?wd="+url);
        et_input.clearFocus();
        closeInputMethod();
        save();
    }

    private void save() {
        // 获取搜索框信息
        String text = et_input.getText().toString();
        SharedPreferences mysp = getSharedPreferences("his", 0);
        String old_text = mysp.getString("history", "nodata");

        // 利用StringBuilder.append新增内容，逗号便于读取内容时用逗号拆分开
        StringBuilder builder = new StringBuilder(old_text);
        builder.append(text + ",");

        // 判断搜索内容是否已经存在于历史文件，已存在则不重复添加
        if (!old_text.contains(text + ",")) {
            SharedPreferences.Editor editor = mysp.edit();
            editor.putString("history", builder.toString());
            editor.commit();
            initinput();
        }
    }

    private void closeInputMethod() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        boolean isOpen = imm.isActive();
        if (isOpen) {
            // imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);//没有显示则显示
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private boolean isConnection(String url)
    {
        boolean flag = false;
        int counts = 0;
        int state=-1;
        int s=-1;
        URL ur;
        HttpURLConnection con;
        while(counts<3) {
            try {
                ur = new URL(url);
                con = (HttpURLConnection) ur.openConnection();
                state = con.getResponseCode();
                System.out.println(state);
                if (state == 200) {
                    flag = true;
                }
                break;
            } catch (Exception e) {
                counts++;
                continue;
            }
        }
        return flag;
    }
}
