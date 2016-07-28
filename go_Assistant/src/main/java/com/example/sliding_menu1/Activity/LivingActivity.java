package com.example.sliding_menu1.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

import com.example.sliding_menu1.R;


/**
 * @Description: 校内资讯功能
 * @author: yzx
 * @time: 2016-3-7 下午1:27:01
 */
public class LivingActivity extends Activity implements OnClickListener {

	private ImageButton backBtn;// 返回按钮
	private WebView webView;// 加载网页
	private Dialog dialog;// 加载对话框

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_living);
		initView();
		setListener();
		initData();
	}

	/**
	 * 
	 * @Description: 初始化控件
	 */
	private void initView() {
		backBtn = (ImageButton) findViewById(R.id.living_back_btn);
		webView = (WebView) findViewById(R.id.living_wv);
	}

	/**
	 * 
	 * @Description: 设置监听
	 */
	private void setListener() {
		backBtn.setOnClickListener(this);
	}

	/**
	 * 
	 * @Description: 初始化数据
	 */
	@SuppressLint("SetJavaScriptEnabled")
	private void initData() {
		AlertDialog.Builder builder=new AlertDialog.Builder(this);
		builder.setMessage("正在加载中...");
		dialog = builder.create();
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setAllowContentAccess(true);
		webSettings.setAppCacheEnabled(false);
		webSettings.setBuiltInZoomControls(true);
		webSettings.setUseWideViewPort(true);
		webSettings.setLoadWithOverviewMode(true);
		webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		webView.setWebChromeClient(new WebChromeClient() {

			@Override
			public void onProgressChanged(WebView view, int newProgress) {

				if (newProgress == 100) {
					dialog.dismiss();
				}
				super.onProgressChanged(view, newProgress);
			}

		});
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				dialog.show();// 在WebView里实现页面跳转
				view.loadUrl(url);
				return true;
			}
		});
		dialog.show();
		webView.loadUrl("http://yiban.fzu.edu.cn/campuslife/xysh_xybst.aspx");
	}

	/**
	 * 
	 * @Description: 按键监听
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.living_back_btn: {// 返回
			if (webView.canGoBack()) {
				webView.goBack();
				return;
			}
			finish();
			break;
		}

		default:
			break;
		}
	}

	/**
	 * 
	 * @Description: 重写返回键
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if (webView.canGoBack()) {
				webView.goBack();
				return false;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
}
