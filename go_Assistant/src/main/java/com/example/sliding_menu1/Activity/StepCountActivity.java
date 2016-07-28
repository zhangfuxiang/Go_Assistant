package com.example.sliding_menu1.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.sliding_menu1.R;
import com.example.sliding_menu1.sensor.StepDector;
import com.example.sliding_menu1.service.StepCounterService;

import java.text.DecimalFormat;

/**
 * 工程名：Sliding_menu
 * 包名：com.example.sliding_menu1.Activity
 * 作者： win
 * 创建日期：2016/4/21 16:12
 * 实现的主要功能：
 */
/** 应用程序的用户界面，
 * 主要功能就是按照XML布局文件的内容显示界面，
 * 并与用户进行交互
 * 负责前台界面展示
 * 在android中Activity负责前台界面展示，service负责后台的需要长期运行的任务。
 * Activity和Service之间的通信主要由Intent负责
 */
public class StepCountActivity extends Activity {
    private TextView tv_show_step;// 步数
    private TextView tv_distance;// 行程
    private TextView tv_calories;// 卡路里
    private TextView tv_velocity;// 速度
    private TextView tv_timer;// 运行时间
    private Button btn_start;// 开始按钮
    private Button btn_stop;// 停止按钮

    private ImageView iv_back,iv_setting;
    private boolean isRun = false;

    private long timer = 0;// 运动时间
    private  long startTimer = 0;// 开始时间

    private  long tempTime = 0;

    private Double distance = 0.0;// 路程：米
    private Double calories = 0.0;// 热量：卡路里
    private Double velocity = 0.0;// 速度：米每秒

    private int step_length = 0;  //步长
    private int weight = 0;       //体重
    private int total_step = 0;   //走的总步数

    private Thread thread;  //定义线程对象

    private TableRow hide1, hide2;
    private TextView step_counter;
    // 当创建一个新的Handler实例时, 它会绑定到当前线程和消息的队列中,开始分发数据
    // Handler有两个作用, (1) : 定时执行Message和Runnalbe 对象
    // (2): 让一个动作,在不同的线程中执行.
    Handler handler=new Handler(){
        //主要接受子线程发送的数据, 并用此数据配合主线程更新UI
        //Handler运行在主线程中(UI线程中), 它与子线程可以通过Message对象来传递数据,
        //Handler就承担着接受子线程传过来的(子线程用sendMessage()方法传递Message对象，(里面包含数据)
        //把这些消息放入主线程队列中，配合主线程进行更新UI。

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            countDistance();//调用距离方法，看一下走了多远
            if (timer != 0 && distance != 0.0) {

                // 体重、距离
                // 跑步热量（kcal）＝体重（kg）×距离（公里）×1.036
                calories = weight * distance * 0.001;
                //速度velocity
                velocity = distance * 1000 / timer;
            } else {
                calories = 0.0;
                velocity = 0.0;
            }
            countStep();          //调用步数方法
            tv_show_step.setText(total_step + "");// 显示当前步数

            tv_distance.setText(formatDouble(distance));// 显示路程
            tv_calories.setText(formatDouble(calories));// 显示卡路里
            tv_velocity.setText(formatDouble(velocity));// 显示速度
            tv_timer.setText(getFormatTime(timer));// 显示当前运行时间
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_stepcounter);
        if (StepSettingActivity.sharedPreferences==null){
            StepSettingActivity.sharedPreferences=this.getSharedPreferences(
                    StepSettingActivity.SETP_SHARED_PREFERENCES,
                    Context.MODE_PRIVATE);

        }
        Bundle extras = getIntent().getExtras();

        if (thread==null){
            thread=new Thread(){// 子线程用于监听当前步数的变化


                @Override
                public void run() {
                    super.run();
                    int temp=0;
                    while (true){
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (StepCounterService.FLAG) {
                            Message msg = new Message();
                            if (temp != StepDector.CURRENT_SETP) {
                                temp = StepDector.CURRENT_SETP;
                            }
                            if (startTimer != System.currentTimeMillis()) {
                                timer = tempTime + System.currentTimeMillis()
                                        - startTimer;
                            }
                            handler.sendMessage(msg);// 通知主线程
                        }
                    }
                }
            };
            thread.start();
        }
        // 获取界面控件
        addView();

        // 初始化控件
        init();
    }
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

    }

    private void addView() {
        tv_show_step = (TextView) this.findViewById(R.id.show_step);

        tv_timer = (TextView) this.findViewById(R.id.timer);

        tv_distance = (TextView) this.findViewById(R.id.distance);
        tv_calories = (TextView) this.findViewById(R.id.calories);
        tv_velocity = (TextView) this.findViewById(R.id.velocity);

        iv_back= (ImageView) findViewById(R.id.iv_back);
        iv_setting= (ImageView) findViewById(R.id.iv_setting);

        btn_start = (Button) this.findViewById(R.id.start);
        btn_stop = (Button) this.findViewById(R.id.stop);

        hide1 = (TableRow)findViewById(R.id.hide1);
        hide2 = (TableRow)findViewById(R.id.hide2);
        step_counter = (TextView)findViewById(R.id.step_counter);

        if(isRun){
            hide1.setVisibility(View.GONE);
            hide2.setVisibility(View.GONE);
            step_counter.setText("次数");
        }

        Intent service = new Intent(this, StepCounterService.class);
        stopService(service);
        StepDector.CURRENT_SETP = 0;
        tempTime = timer = 0;
        tv_timer.setText(getFormatTime(timer));      //如果关闭之后，格式化时间
        tv_show_step.setText("0");
        tv_distance.setText(formatDouble(0.0));
        tv_calories.setText(formatDouble(0.0));
        tv_velocity.setText(formatDouble(0.0));

        handler.removeCallbacks(thread);
    }

    /**
     * 初始化界面
     */
    private void init() {

        step_length = StepSettingActivity.sharedPreferences.getInt(
                StepSettingActivity.STEP_LENGTH_VALUE, 70);
        weight = StepSettingActivity.sharedPreferences.getInt(
                StepSettingActivity.WEIGHT_VALUE, 50);

        countDistance();
        countStep();
        if ((timer += tempTime) != 0 && distance != 0.0) {  //tempTime记录运动的总时间，timer记录每次运动时间

            // 体重、距离
            // 跑步热量（kcal）＝体重（kg）×距离（公里）×1.036，换算一下
            calories = weight * distance * 0.001;

            velocity = distance * 1000 / timer;
        } else {
            calories = 0.0;
            velocity = 0.0;
        }

        tv_timer.setText(getFormatTime(timer + tempTime));

        tv_distance.setText(formatDouble(distance));
        tv_calories.setText(formatDouble(calories));
        tv_velocity.setText(formatDouble(velocity));

        tv_show_step.setText(total_step + "");

        btn_start.setEnabled(!StepCounterService.FLAG);
        btn_stop.setEnabled(StepCounterService.FLAG);

        if (StepCounterService.FLAG) {
            btn_stop.setText(getString(R.string.pause));
        } else if (StepDector.CURRENT_SETP > 0) {
            btn_stop.setEnabled(true);
            btn_stop.setText(getString(R.string.cancel));
        }
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        iv_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),StepSettingActivity.class);
                startActivity(intent);
            }
        });

    }
    /**
     * 计算并格式化doubles数值，保留两位有效数字
     *
     * @param doubles
     * @return 返回当前路程
     */
    private String formatDouble(Double doubles) {
        DecimalFormat format = new DecimalFormat("####.##");
        String distanceStr = format.format(doubles);
        return distanceStr.equals(getString(R.string.zero)) ? getString(R.string.double_zero)
                : distanceStr;
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }
    public void onClick(View view) {
        Intent service = new Intent(this, StepCounterService.class);
        switch (view.getId()) {
            case R.id.start:
                startService(service);
                btn_start.setEnabled(false);
                btn_stop.setEnabled(true);
                btn_stop.setText(getString(R.string.pause));
                startTimer = System.currentTimeMillis();
                tempTime = timer;
                break;

            case R.id.stop:
                stopService(service);
                if (StepCounterService.FLAG && StepDector.CURRENT_SETP > 0) {
                    btn_stop.setText(getString(R.string.cancel));
                } else {
                    StepDector.CURRENT_SETP = 0;
                    tempTime = timer = 0;

                    btn_stop.setText(getString(R.string.pause));
                    btn_stop.setEnabled(false);

                    tv_timer.setText(getFormatTime(timer));      //如果关闭之后，格式化时间

                    tv_show_step.setText("0");
                    tv_distance.setText(formatDouble(0.0));
                    tv_calories.setText(formatDouble(0.0));
                    tv_velocity.setText(formatDouble(0.0));

                    handler.removeCallbacks(thread);
                }
                btn_start.setEnabled(true);
                break;
        }
    }
    /**
     * 得到一个格式化的时间
     *
     * @param time
     *            时间 毫秒
     * @return 时：分：秒：毫秒
     */
    private String getFormatTime(long time) {
        time = time / 1000;
        long second = time % 60;
        long minute = (time % 3600) / 60;
        long hour = time / 3600;

        // 毫秒秒显示两位
        // String strMillisecond = "" + (millisecond / 10);
        // 秒显示两位
        String strSecond = ("00" + second)
                .substring(("00" + second).length() - 2);
        // 分显示两位
        String strMinute = ("00" + minute)
                .substring(("00" + minute).length() - 2);
        // 时显示两位
        String strHour = ("00" + hour).substring(("00" + hour).length() - 2);

        return strHour + ":" + strMinute + ":" + strSecond;
        // + strMillisecond;
    }

    private void changeStep() {
    }


    /**
     * 实际的步数
     */
    private void countStep() {
        if (StepDector.CURRENT_SETP % 2 == 0) {
            total_step = StepDector.CURRENT_SETP;
        } else {
            total_step = StepDector.CURRENT_SETP +1;
        }

        total_step = StepDector.CURRENT_SETP;
    }

    /**
     * 计算行走的距离
     */
    private void countDistance() {
        if (StepDector.CURRENT_SETP % 2 == 0) {
            distance = (StepDector.CURRENT_SETP / 2) * 3 * step_length * 0.01;
        } else {
            distance = ((StepDector.CURRENT_SETP / 2) * 3 + 1) * step_length * 0.01;
        }
    }
}
