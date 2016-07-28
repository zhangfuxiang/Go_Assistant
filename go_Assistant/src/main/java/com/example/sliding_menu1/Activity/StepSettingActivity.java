package com.example.sliding_menu1.Activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sliding_menu1.R;
import com.example.sliding_menu1.sensor.StepDector;

/**
 * 工程名：Sliding_menu
 * 包名：com.example.sliding_menu1.Activity
 * 作者： win
 * 创建日期：2016/4/21 14:18
 * 实现的主要功能：
 */
//计步设置界面
public class StepSettingActivity extends Activity implements View.OnClickListener {
    public static final String WEIGHT_VALUE = "weight_value";

    public static final String STEP_LENGTH_VALUE = "step_length_value";// 步长

    public static final String SENSITIVITY_VALUE = "sensitivity_value";// 灵敏值

    public static final String SETP_SHARED_PREFERENCES = "setp_shared_preferences";// 设置

    public static SharedPreferences sharedPreferences;

    private SharedPreferences.Editor editor;

    private Button save,cancel;

    private TextView tv_sensitivity_vlaue;
    private TextView tv_step_length_vlaue;
    private TextView tv_weight_value;

    private SeekBar sb_sensitivity;
    private SeekBar sb_step_length;
    private SeekBar sb_weight;

    private int sensitivity = 0;
    private int step_length = 0;
    private int weight = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_stepsetting);
        addView();
        init();
        listener();
        
    }
    /**
     * SeekBar的拖动监听
     */
    private void listener() {
        sb_sensitivity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sensitivity = progress;
                tv_sensitivity_vlaue.setText(sensitivity + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        sb_step_length.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                step_length = progress * 5 + 40;
                tv_step_length_vlaue.setText(step_length
                        + getString(R.string.cm));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        sb_weight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                weight = progress * 2 + 30;
                tv_weight_value.setText(weight + getString(R.string.kg));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void init() {
        if (sharedPreferences == null) {    //SharedPreferences是Android平台上一个轻量级的存储类，
            //主要是保存一些常用的配置比如窗口状态
            sharedPreferences = getSharedPreferences(SETP_SHARED_PREFERENCES,
                    MODE_PRIVATE);
        }
        editor = sharedPreferences.edit();
        sensitivity=10-sharedPreferences.getInt(SENSITIVITY_VALUE, 7);
        step_length = sharedPreferences.getInt(STEP_LENGTH_VALUE, 70);
        weight = sharedPreferences.getInt(WEIGHT_VALUE, 50);

        sb_sensitivity.setProgress(sensitivity);
        sb_step_length.setProgress((step_length - 40) / 5);               //步长按钮在进度条上占得比例
        sb_weight.setProgress((weight - 30) / 2);

        tv_sensitivity_vlaue.setText(sensitivity + "");
        tv_step_length_vlaue.setText(step_length + getString(R.string.cm));
        tv_weight_value.setText(weight + getString(R.string.kg));
    }

    private void addView() {
        tv_sensitivity_vlaue = (TextView)
                findViewById(R.id.sensitivity_value);
        tv_step_length_vlaue = (TextView)findViewById(R.id.step_lenth_value);
        tv_weight_value = (TextView)findViewById(R.id.weight_value);

        sb_sensitivity = (SeekBar)findViewById(R.id.sensitivity);
        sb_step_length = (SeekBar)findViewById(R.id.step_lenth);
        sb_weight = (SeekBar)findViewById(R.id.weight);

        tv_sensitivity_vlaue.setText(sensitivity + "");
        tv_step_length_vlaue.setText(step_length + getString(R.string.cm));
        tv_weight_value.setText(weight + getString(R.string.kg));

        save= (Button) findViewById(R.id.save);
        cancel= (Button) findViewById(R.id.cancle);
        save.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save:
                editor.putInt(SENSITIVITY_VALUE, 10 - sensitivity);
                editor.putInt(STEP_LENGTH_VALUE, step_length);
                editor.putInt(WEIGHT_VALUE, weight);
                editor.commit();

                Toast.makeText(this, "保存成功！", Toast.LENGTH_SHORT).show();

                this.finish();
                StepDector.SENSITIVITY = 10 - sensitivity;
                break;

            case R.id.cancle:
                this.finish();
                break;

            default:
                break;
        }
    }
}
