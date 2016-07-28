package com.example.sliding_menu1.Activity;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.SDKInitializer;
import com.example.sliding_menu1.Fragment.FragmentAbout;
import com.example.sliding_menu1.Fragment.FragmentHome;
import com.example.sliding_menu1.Fragment.FragmentLook;
import com.example.sliding_menu1.Fragment.FragmentUser;
import com.example.sliding_menu1.Fragment.FragmentOften;
import com.example.sliding_menu1.R;
import com.example.sliding_menu1.entity.BaseApplication;
import com.example.sliding_menu1.entity.GlobalParams;
import com.example.sliding_menu1.menu.ContextMenuDialogFragment;
import com.example.sliding_menu1.menu.MenuObject;
import com.example.sliding_menu1.menu.MenuParams;
import com.example.sliding_menu1.menu.interfaces.OnMenuItemClickListener;
import com.example.sliding_menu1.menu.interfaces.OnMenuItemLongClickListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.datatype.BmobFile;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,OnMenuItemClickListener, OnMenuItemLongClickListener, View.OnClickListener {
    private FragmentHome fragmentHome;
    private FragmentLook fragmentLook;
    private FragmentUser fragmentUser;
    private FragmentOften fragmentOften;
    private FragmentAbout fragmentAbout;

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private android.support.v4.app.FragmentManager fragmentManager;
    private ContextMenuDialogFragment mMenuDialogFragment;
    private Toolbar toolbar;
    //头像内容
    public static ImageView iv_header;
    //头像下的昵称
    public static TextView tv_iv_header_name;

    public static String result=null;

    //判断是否是二维码界面跳转回来
    public static boolean captureactivity_return_result=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        Log.i("asd","this is Activity onCreate");
        fragmentManager = getSupportFragmentManager();
        //初始化控件
        init();
        setSupportActionBar(toolbar);

        initMenuFragment();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        iv_header.setOnClickListener(this);

        //设置默认Fragment
        setDefaultFragment();


    }

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        //获取整个drawerLayout
        drawerLayout= (DrawerLayout) findViewById(R.id.drawer_layout);
        //获取整个导航抽屉
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        iv_header= (ImageView) navigationView.inflateHeaderView(R.layout.nav_header_main).findViewById(R.id.iv_user_head);
        tv_iv_header_name= (TextView)navigationView.getHeaderView(0).findViewById(R.id.tv_iv_header_name);
    }

    @Override
    public void onStart() {
        super.onStart();
        //导航页的昵称
        tv_iv_header_name.setText(GlobalParams.userInfo.getName());
        //导航页的头像
        BmobFile avatarFile = GlobalParams.userInfo.getAvatar();
        if (null != avatarFile) {
            ImageLoader.getInstance().displayImage(
                    avatarFile.getFileUrl(this),
                    iv_header,
                    BaseApplication.getInstance().getOptions(
                            R.drawable.user_icon_default_main),
                    new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingComplete(String imageUri,
                                                      View view, Bitmap loadedImage) {
                            super.onLoadingComplete(imageUri, view, loadedImage);
                        }
                    });
        }
        Log.i("asd","this is  Activity onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //判断是否是二维码界面跳转回来
        if (captureactivity_return_result==true){
            setDefaultFragment();
            captureactivity_return_result=false;
        }

        Log.i("asd","this is  Activity onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("asd","this is  Activity onResume");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("asd","this is  Activity onStop");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("asd","this is  Activity onPause");
    }

    private void initMenuFragment() {
        MenuParams menuParams = new MenuParams();
        menuParams.setActionBarSize((int) getResources().getDimension(R.dimen.tool_bar_height));
        menuParams.setMenuObjects(getMenuObjects());
        menuParams.setClosableOutside(false);
        mMenuDialogFragment = ContextMenuDialogFragment.newInstance(menuParams);
        mMenuDialogFragment.setItemClickListener(this);
        mMenuDialogFragment.setItemLongClickListener(this);
    }

    private List<MenuObject> getMenuObjects() {

        List<MenuObject> menuObjects = new ArrayList<>();

        MenuObject close = new MenuObject();
        close.setResource(R.mipmap.ic_menu_close);

        MenuObject send = new MenuObject("扫一扫");
        send.setResource(R.mipmap.ic_menu_saoma);

        MenuObject like = new MenuObject("浏览器");
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_menu_browser);
        like.setBitmap(b);

        MenuObject addFr = new MenuObject("计算器");
        BitmapDrawable bd = new BitmapDrawable(getResources(),
                BitmapFactory.decodeResource(getResources(), R.mipmap.ic_menu_caculater));
        addFr.setDrawable(bd);

        MenuObject addFav = new MenuObject("手电筒");
        addFav.setResource(R.mipmap.ic_menu_flashlight);

        MenuObject block = new MenuObject("计步器");
        block.setResource(R.mipmap.ic_menu_stepcounter);

        menuObjects.add(close);
        menuObjects.add(send);
        menuObjects.add(like);
        menuObjects.add(addFr);
        menuObjects.add(addFav);
        menuObjects.add(block);
        return menuObjects;
    }
    //默认的fragment
    private  void setDefaultFragment(){
        FragmentManager fm=getFragmentManager();
        FragmentTransaction transaction=fm.beginTransaction();
        fragmentHome=new FragmentHome();
        transaction.replace(R.id.content_main,fragmentHome).commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            if (fragmentManager.findFragmentByTag(ContextMenuDialogFragment.TAG) == null) {
                mMenuDialogFragment.show(fragmentManager, ContextMenuDialogFragment.TAG);
            }
            return super.onOptionsItemSelected(item);
//            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        FragmentManager fm=getFragmentManager();
        FragmentTransaction transaction=fm.beginTransaction();

        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id){

            case R.id.nav_home:
                if (fragmentHome==null)
                {
                    fragmentHome=new FragmentHome();
                }
                transaction.replace(R.id.content_main,fragmentHome).commit();
                break;
            case R.id.nav_look:
                if (fragmentLook==null)
                {
                    fragmentLook=new FragmentLook();
                }
                transaction.replace(R.id.content_main,fragmentLook).commit();
                break;
            case R.id.nav_often:
                if (fragmentOften==null)
                {
                    fragmentOften =new FragmentOften();
                }
                transaction.replace(R.id.content_main, fragmentOften).commit();
                break;
            case R.id.nav_user:
                if (fragmentUser==null)
                {
                    fragmentUser=new FragmentUser();
                }
                transaction.replace(R.id.content_main,fragmentUser).commit();
                break;

            case R.id.nav_about:
                if (fragmentAbout==null)
                {
                    fragmentAbout=new FragmentAbout();
                }
                transaction.replace(R.id.content_main,fragmentAbout).commit();
                break;
            case R.id.nav_close:
                exit();
                break;

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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

    //点击返回键事件处理
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode==KeyEvent.KEYCODE_BACK){
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK){
            result=data.getExtras().getString("result");
//            Log.i("asd",result);

        }
    }
    //菜单短按点击事件
    @Override
    public void onMenuItemClick(View clickedView, int position) {
//        Toast.makeText(this, "Clicked on position: " + position, Toast.LENGTH_SHORT).show();
        if(position==1){
            Intent startScan=new Intent(MainActivity.this, CaptureActivity.class);
            startActivityForResult(startScan,0);

//            setDefaultFragment();
        }
        if(position==2){
            Intent startBrowser=new Intent(MainActivity.this, BrowserActivity.class);
            startActivity(startBrowser);
        }
        if(position==3){
            Intent startCalulater=new Intent(MainActivity.this, CalulaterActivity.class);
            startActivity(startCalulater);
        }
        if(position==4){
            Intent startFlashlight=new Intent(MainActivity.this, FlashlightActivity.class);
            startActivity(startFlashlight);
        }
        if(position==5){
            Intent startStep=new Intent(MainActivity.this, StepCountActivity.class);
            startActivity(startStep);
        }
    }

    //菜单长按点击事件
    @Override
    public void onMenuItemLongClick(View clickedView, int position) {
//        Toast.makeText(this, "Long clicked on position: " + position, Toast.LENGTH_SHORT).show();
        if(position==1){
            Intent startScan=new Intent(MainActivity.this, CaptureActivity.class);
            startActivity(startScan);
            setDefaultFragment();
        }
        if(position==2){
            Intent startBrowser=new Intent(MainActivity.this, BrowserActivity.class);
            startActivity(startBrowser);
        }
        if(position==3){
            Intent startCalulater=new Intent(MainActivity.this, CalulaterActivity.class);
            startActivity(startCalulater);
        }
        if(position==4){
            Intent startFlashlight=new Intent(MainActivity.this, FlashlightActivity.class);
            startActivity(startFlashlight);
        }
        if(position==5){
            Intent startStep=new Intent(MainActivity.this, StepCountActivity.class);
            startActivity(startStep);
        }
    }


    @Override
    public void onClick(View v) {
        FragmentManager fm=getFragmentManager();
        FragmentTransaction transaction=fm.beginTransaction();
        switch (v.getId()){
            case R.id.iv_user_head:
                if (fragmentUser==null)
                {
                    fragmentUser=new FragmentUser();
                }
                transaction.replace(R.id.content_main,fragmentUser).commit();
                //关闭导航抽屉
                drawerLayout.closeDrawer(Gravity.LEFT);
                break;
        }
    }
}
