package com.jw.android.realtime_bus;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;

import java.util.List;

public class Registered extends AppCompatActivity {

    private int resultCode =1;//来自登录界面

    String reUserName=null;//用户名
    String rePassword=null;//密码
    String reRePassword=null;//重复密码
    EditText reUserText=null;//用户名输入
    EditText rePassText=null;//密码输入
    EditText reRePassText=null;//密码第二次输入
    Button reBack=null;//返回键
    Button reRegister=null;//注册键

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registered);

        reBack = (Button) findViewById(R.id.register_back);
        reRegister = (Button) findViewById(R.id.register_yes);
        reRegister.setOnClickListener(new Register1());
        reBack.setOnClickListener(new Cancel());

        reUserText=(EditText)findViewById(R.id.regiserUserName);
        rePassText=(EditText)findViewById(R.id.regiserPassword);
        reRePassText=(EditText)findViewById(R.id.rewritePassword);
        reUserText.addTextChangedListener(textWatcher1);
        rePassText.addTextChangedListener(textWatcher1);
        reRePassText.addTextChangedListener(textWatcher1);

    }

    private TextWatcher textWatcher1 = new TextWatcher()
    {//文本框监听事件
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        @Override
        public void afterTextChanged  (Editable s) {
            reUserName = reUserText.getText().toString();//输入框输入结束后，将用户名文本框的内容赋值给reUserName
            rePassword = rePassText.getText().toString();//输入框输入结束后，将密码文本框的内容赋值给rePassword
            reRePassword=reRePassText.getText().toString();//输入框输入结束后，将密码文本框的内容赋值给reRePassword
        }
    };

    class Register1 implements View.OnClickListener{

        public void onClick(View v){
            if(TextUtils.isEmpty(reUserName) ||TextUtils.isEmpty(rePassword)|| TextUtils.isEmpty(reRePassword)){
                AlertDialog login_info= new  AlertDialog.Builder(Registered.this)
                        .setTitle("注册提示" )
                        .setMessage("用户名或密码为空！" )
                        .setPositiveButton("确定" ,  null )
                        .show();

            }else if(!rePassword.equals(reRePassword)){
                AlertDialog login_info=new AlertDialog.Builder(Registered.this)
                        .setTitle("注册失败")
                        .setMessage("两次输入密码不一致，请重新输入")
                        .setPositiveButton("确定",null)
                        .show();
            } else {
                AVQuery<AVObject> avQuery = new AVQuery<>("UserInfo");
                avQuery.whereEqualTo("username", reUserName);
                avQuery.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {
                        if (!list.isEmpty()){
                            AlertDialog login_info= new  AlertDialog.Builder(Registered.this)
                                    .setTitle("注册提示" )
                                    .setMessage("此用户名已经注册，请更换用户名！" )
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            reUserText.setText("");
                                        }
                                    })
                                    .show();
                        }else {
                            AVObject User = new AVObject("UserInfo");
                            User.put("username", reUserName);
                            User.put("password", rePassword);
                            User.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(AVException e) {
                                    if(e == null){
                                        AlertDialog login_info= new  AlertDialog.Builder(Registered.this)
                                                .setTitle("注册提示" )
                                                .setMessage("注册成功，是否现在登录？" )
                                                .setPositiveButton("确定" ,new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which){
                                                        //登录跳转
                                                        resultCode = 1;
                                                        Intent mIntent = new Intent();
                                                        mIntent.putExtra("name", reUserName);
                                                        // 设置结果，并进行传送
                                                        Registered.this.setResult(resultCode, mIntent);
                                                        finish();

                                                    }
                                                } )
                                                .setNegativeButton("取消",null)
                                                .show();
                                    }
                                }
                            });
                        }
                    }
                });


            }

        }
    }

    class Cancel  implements View.OnClickListener{
        @Override
        public void onClick(View v){
//                finish();
            resultCode = 0;//未登录
            Intent mIntent = new Intent();
            mIntent.putExtra("name", reUserName);
            // 设置结果，并进行传送
            Registered.this.setResult(resultCode, mIntent);
            finish();
        }

    }
}
