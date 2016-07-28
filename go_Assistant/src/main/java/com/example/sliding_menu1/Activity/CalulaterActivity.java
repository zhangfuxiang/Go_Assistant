package com.example.sliding_menu1.Activity;

/**
 * 工程名：Sliding_menu
 * 包名：com.example.sliding_menu1.Activity
 * 作者： win
 * 创建日期：2016/4/12 23:15
 * 实现的主要功能：计算器功能
 */
import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.sliding_menu1.R;

public class CalulaterActivity extends Activity implements View.OnClickListener{

    Button btn_0;Button btn_1;Button btn_2;Button btn_3;Button btn_4;Button btn_5;Button btn_6;Button btn_7;
    Button btn_8;Button btn_9;Button btn_clear;Button btn_del;Button btn_mul;Button btn_sum;Button btn_sub;
    Button btn_equ;Button btn_point;Button btn_div; EditText input;
    boolean clear_flag;//清空标识

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculater);
        btn_0= (Button) findViewById(R.id.btn_0);btn_1= (Button) findViewById(R.id.btn_1);btn_2= (Button) findViewById(R.id.btn_2);
        btn_3= (Button) findViewById(R.id.btn_3);btn_4= (Button) findViewById(R.id.btn_4);btn_5= (Button) findViewById(R.id.btn_5);
        btn_6= (Button) findViewById(R.id.btn_6);btn_7= (Button) findViewById(R.id.btn_7);btn_8= (Button) findViewById(R.id.btn_8);
        btn_9= (Button) findViewById(R.id.btn_9);btn_clear= (Button) findViewById(R.id.clear);btn_del= (Button) findViewById(R.id.del);
        btn_mul= (Button) findViewById(R.id.mult);btn_sub= (Button) findViewById(R.id.sub);btn_sum= (Button) findViewById(R.id.sum);
        btn_point= (Button) findViewById(R.id.btn_point);btn_equ= (Button) findViewById(R.id.equal);input=(EditText)findViewById(R.id.input);
        btn_div= (Button) findViewById(R.id.divide);
        btn_0.setOnClickListener((View.OnClickListener) this);
        btn_1.setOnClickListener((View.OnClickListener) this);
        btn_2.setOnClickListener((View.OnClickListener) this);
        btn_3.setOnClickListener((View.OnClickListener) this);
        btn_4.setOnClickListener((View.OnClickListener) this);
        btn_5.setOnClickListener((View.OnClickListener) this);
        btn_6.setOnClickListener((View.OnClickListener) this);
        btn_7.setOnClickListener((View.OnClickListener) this);
        btn_8.setOnClickListener((View.OnClickListener) this);
        btn_9.setOnClickListener((View.OnClickListener) this);
        btn_point.setOnClickListener((View.OnClickListener) this);
        btn_mul.setOnClickListener((View.OnClickListener) this);
        btn_sub.setOnClickListener((View.OnClickListener) this);
        btn_sum.setOnClickListener((View.OnClickListener) this);
        btn_equ.setOnClickListener((View.OnClickListener) this);
        btn_del.setOnClickListener((View.OnClickListener) this);
        btn_clear.setOnClickListener((View.OnClickListener) this);
        btn_div.setOnClickListener((View.OnClickListener) this);
        findViewById(R.id.ibtn_calculater_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    @Override
    public void onClick(View v) {
        //先把原来空的传到字符串里面
        String str=input.getText().toString();
        switch (v.getId()){
            case R.id.btn_0:
            case R.id.btn_1:
            case R.id.btn_2:
            case R.id.btn_3:
            case R.id.btn_4:
            case R.id.btn_5:
            case R.id.btn_6:
            case R.id.btn_7:
            case R.id.btn_8:
            case R.id.btn_9:
            case R.id.btn_point:
                if(clear_flag){
                    clear_flag=false;
                    str="";
                    input.setText("");
                }//当点击数字按钮的时候需要清空

                input.setText(str+((Button)v).getText());//在原来的字符串里面累计输入的内容
                break;
            case R.id.sum:
            case R.id.sub:
            case R.id.mult:
            case R.id.divide:
                //将运算符也加入里面
                if(clear_flag){
                    clear_flag=false;
                    str="";
                    input.setText("");
                }
                input.setText(str+" "+((Button)v).getText()+" ");
                break;
            //清除的设置
            case R.id.clear:
                clear_flag=false;
                str="";
                input.setText("");
                break;
            case R.id.del:
                //不为空的时候进行减一操作
                if(clear_flag){
                    clear_flag=false;
                    input.setText("");
                }else if (str!=null&&!str.equals("")){
                    input.setText(str.substring(0,str.length()-1));
                }
                break;
            case R.id.equal:
                getresult();
                break;
        }
    }
    private void getresult(){
        String exp=input.getText().toString();
        //为空的话原来状态
        if (exp==null||exp.equals("")){
            return;
        }
        //因为没有空格，就说明没有运算符，判断是否含有空格，如果没有空格就不运算返回原来的值
        if (!exp.contains(" ")){
            return;
        }
        //有空格则开始截取数据
        if (clear_flag){
            clear_flag=false;
            return;
        }//再点等号设置成空
        clear_flag=true;
        double result=0;
        String s1=exp.substring(0,exp.indexOf(" "));//截取运算符前面字符串
        String op=exp.substring(exp.indexOf(" ")+1,exp.indexOf(" ")+2);//截取运算符
        String s2=exp.substring(exp.indexOf(" ")+3);//截取运算符后面的字符串
        if (!s1.equals("")&&!s2.equals("")){//s1和s2都不是空情况下
            double d1=Double.parseDouble(s1);//将字符串强制转换为双浮点型数据
            double d2=Double.parseDouble(s2);
            if (op.equals("＋")){
                result=d1+d2;
            }else if (op.equals("-")){
                result=d1-d2;
            }else if (op.equals("×")){
                result=d1*d2;
            }else if (op.equals("÷")){
                if (d2==0){
                    result=0;
                }else {
                    result = d1 / d2;
                }
            }
            if(!s1.contains(".")&&!s2.contains(".")&&!op.equals("÷")){
                int r=(int)result;
                input.setText(r+"");
            }else{
                input.setText(result+"");
            }
        }else if(!s1.equals("")&&s2.equals("")){//s1不是空，s2是空的情况下
            input.setText(exp);
        }else if(s1.equals("")&&!s2.equals("")){//s1是空，s2不是空的情况下
            double d2=Double.parseDouble(s2);
            if (op.equals("+")){
                result=0+d2;
            }else if (op.equals("-")){
                result=0-d2;
            }else if (op.equals("×")){
                result=0;
            }else if (op.equals("÷")){
                if (d2==0){
                    result=0;
                }
            }
            if(!s2.contains(".")){
                int r=(int)result;
                input.setText(r+"");
            }else{
                input.setText(result+"");
            }
        }else{
            input.setText("");
        }
    }
}
