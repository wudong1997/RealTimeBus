package com.jw.android.realtime_bus;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

public class LoginActivity extends AppCompatActivity {

    private int resultCode =1;//来自登录界面
    String username_put = "test";//传回主界面的用户名
    String username = null; //填写的用户名
    String password =null; //填写的密码
    EditText user_text = null;//用户名输入框
    EditText pass_text = null;//密码输入框
    Button login = null;//登录按钮
    Button register =null;//注册按钮
    Button help=null;//帮助按钮
    //FloatingActionButton cancle = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        login = (Button)findViewById(R.id.Login);
        register = (Button)findViewById(R.id.Register);
        help=(Button)findViewById(R.id.Help);
        //cancle = (FloatingActionButton)findViewById(R.id.cancle);
        register.setOnClickListener(new Register());
        login.setOnClickListener(new Login());
        help.setOnClickListener(new Help());
        //cancle.setOnClickListener(new Cancel());
        user_text = (EditText)findViewById(R.id.userName);
        pass_text = (EditText)findViewById(R.id.userPassword);
        user_text.addTextChangedListener( textWatcher);
        pass_text.addTextChangedListener( textWatcher);

        /**FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.cancle);
         fab.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
        .setAction("Action", null).show();
        }
        });*/
    }

    private TextWatcher textWatcher = new TextWatcher() {//文本框监听事件
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
        }
        @Override
        public void afterTextChanged  (Editable s) {
            username = user_text.getText().toString();//输入框输入结束后，将用户名文本框的内容赋值给username
            password = pass_text.getText().toString();//输入框输入结束后，将密码文本框的内容赋值给passw
        }
    };

    /**class Cancel  implements View.OnClickListener{
    @Override
    public void onClick(View v){
    //                finish();
    resultCode = 0;//未登录
    Intent mIntent = new Intent();
    mIntent.putExtra("name", username);
    // 设置结果，并进行传送
    LoginActivity.this.setResult(resultCode, mIntent);
    finish();
    }

    }*/

    class Login implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                AlertDialog login_info = new AlertDialog.Builder(LoginActivity.this)
                        .setTitle("登录提示")
                        .setMessage("用户名或密码为空！请重新填写！")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                pass_text = (EditText) findViewById(R.id.userPassword);
                                pass_text.setText("");

                            }
                        })
                        .show();
            } else {
                AVQuery<AVObject> avQuery = new AVQuery<>("UserInfo");
                avQuery.whereEqualTo("username", username);
                avQuery.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {
                        if (!list.isEmpty()) {
                            String get_pass = list.get(0).getString("password");
                            if (get_pass.equals(password)) {
                                resultCode =1;
                                Intent mIntent = new Intent();
                                mIntent.putExtra("name", username);
                                // 设置结果，并进行传送
                                LoginActivity.this.setResult(resultCode, mIntent);
                                finish();

                            } else {
                                AlertDialog login_info = new AlertDialog.Builder(LoginActivity.this)
                                        .setTitle("登录提示")
                                        .setMessage("用户名或密码不正确！请重新输入！")
                                        .setPositiveButton("确定",  new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                pass_text = (EditText) findViewById(R.id.userPassword);
                                                pass_text.setText("");

                                            }
                                        })
                                        .show();
                            }

                            //这里写具体操作
                        } else {
                            AlertDialog login_info = new AlertDialog.Builder(LoginActivity.this)
                                    .setTitle("登录提示")
                                    .setMessage("此用户未注册！")
                                    .setPositiveButton("确定", null)
                                    .show();
                            //这里写返回为空时的操作
                        }
                    }
                });

            }
        }
    }

    class Register implements View.OnClickListener{

        public  void onClick(View v){
            Intent intent=new Intent(LoginActivity.this,Registered.class);
            startActivity(intent);
        }

        /**public void onClick(View v){
            if(TextUtils.isEmpty(username) || TextUtils.isEmpty(password)){
                AlertDialog login_info= new  AlertDialog.Builder(LoginActivity.this)
                        .setTitle("注册提示" )
                        .setMessage("用户名或密码为空！" )
                        .setPositiveButton("确定" ,  null )
                        .show();

            }else {
                AVQuery<AVObject> avQuery = new AVQuery<>("UserInfo");
                avQuery.whereEqualTo("username", username);
                avQuery.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {
                        if (!list.isEmpty()){
                            AlertDialog login_info= new  AlertDialog.Builder(LoginActivity.this)
                                    .setTitle("注册提示" )
                                    .setMessage("此用户名已经注册，请更换用户名！" )
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            user_text.setText("");
                                        }
                                    })
                                    .show();
                        }else {
                            AVObject User = new AVObject("UserInfo");
                            User.put("username", username);
                            User.put("password", password);
                            User.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(AVException e) {
                                    if(e == null){
                                        AlertDialog login_info= new  AlertDialog.Builder(LoginActivity.this)
                                                .setTitle("注册提示" )
                                                .setMessage("注册成功，是否现在登录？" )
                                                .setPositiveButton("确定" ,new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which){
                                                        //登录跳转
                                                        resultCode = 1;
                                                        Intent mIntent = new Intent();
                                                        mIntent.putExtra("name", username);
                                                        // 设置结果，并进行传送
                                                        LoginActivity.this.setResult(resultCode, mIntent);
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

        }*/
    }

    class Help  implements View.OnClickListener{
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.Help:
                    AlertDialog.Builder dialog = new AlertDialog.Builder(LoginActivity.this);
                    dialog.setTitle("提示");
                    dialog.setMessage("若您已注册登录账号，直接登录即可；若还未注册账号，填写账号密码后点击右下方注册按钮进行注册与登录");
                    dialog.setCancelable(false);
                    dialog.setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    dialog.show();
                    break;
                default:
                    break;
            }
        }
    }

}
