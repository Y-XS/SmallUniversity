package com.example.app.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.app.beans.Address;
import com.example.app.beans.RLInfo;
import com.example.app.utils.HttpUtil;
import com.example.app.utils.MyApplication;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public class LoginActivity extends BaseActivity {

    private NetworkChangeReceiver networkChangeReceiver;
    private IntentFilter intentFilter;
    private MyApplication myApplication;
    private SharedPreferences account_remember;
    private String userId,userPassword;
    private boolean isFirstLogin = true;

    private Button btn_register,btn_login;
    private EditText userName,password;
    private String sUserName,sPassword,address,responseData;
    private String netAddr = "http://1.15.115.24/login";
    private Address addr;
    private String status,msg,token;
    private RLInfo.UserInfo userInfo;

    private boolean flag=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver, intentFilter);

        myApplication = (MyApplication)getApplication();
        account_remember = getSharedPreferences("data", MODE_PRIVATE);

        userId = account_remember.getString("Id","");
        userPassword = account_remember.getString("Password","");
        isFirstLogin = account_remember.getBoolean("isFirstLogin",true);
        if(!isFirstLogin){

            addr = new Address(netAddr);
            addr.add(true,"id",userId)
                    .add(false,"password",userPassword);
            address = addr.getAddress();
            HttpUtil.sendOkHttpRequest(address, new okhttp3.Callback(){
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    responseData = response.body().string();
                    Log.d("LoginActivity", "onResponse: "+responseData);
                    parseJSONWithGSON(responseData);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if ("1".equals(status)){
                                Toast.makeText(LoginActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                startActivity(intent);
                                finish();
                            }else {
                                Toast.makeText(LoginActivity.this,"用户名或密码错误",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                }
            });
        }

        setContentView(R.layout.activity_login);

        btn_register = (Button)findViewById(R.id.btn_register);
        btn_login = (Button)findViewById(R.id.btn_login);
        userName = (EditText)findViewById(R.id.edit_username);
        password = (EditText)findViewById(R.id.edit_password);


        password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // et.getCompoundDrawables()得到一个长度为4的数组，分别表示左右上下四张图片
                Drawable drawable = password.getCompoundDrawables()[2];
                //如果右边没有图片，不再处理
                if (drawable == null)
                    return false;
                //如果不是按下事件，不再处理
                if (event.getAction() != MotionEvent.ACTION_UP)
                    return false;
                if (event.getX() > password.getWidth()
                        - password.getPaddingRight()
                        - drawable.getIntrinsicWidth()){
//                    password.setText("");
                    if(flag){
                        password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        flag=false;
                    }else {
                        password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        flag=true;
                    }
                }
                return false;
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sUserName = userName.getText().toString();
                sPassword = password.getText().toString();

                addr = new Address(netAddr);
                addr.add(true,"id",sUserName)
                        .add(false,"password",sPassword);
                address = addr.getAddress();

                HttpUtil.sendOkHttpRequest(address, new okhttp3.Callback(){
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        responseData = response.body().string();
                        Log.d("LoginActivity", "onResponse: "+responseData);
                        parseJSONWithGSON(responseData);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if ("1".equals(status)){
                                    Toast.makeText(LoginActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }else {
                                    Toast.makeText(LoginActivity.this,"用户名或密码错误",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    }
                });
            }
        });
    }

    private void parseJSONWithGSON(String jsonData){
        Gson gson = new Gson();
        RLInfo info = gson.fromJson(jsonData, RLInfo.class);

        myApplication.userLogin(info);
        status = info.getStatus();
        msg = info.getMsg();
        token = info.getToken();

        if("1".equals(status)){
            userInfo = info.getInfo();

            if(isFirstLogin){
                isFirstLogin = false;
                SharedPreferences.Editor editor = account_remember.edit();
                editor.putString("Id",userInfo.getId());
                editor.putString("Password",userInfo.getPassword());
                editor.putBoolean("isFirstLogin",isFirstLogin);
                editor.apply();
            }

            Log.d("TAG", "info :"+info);
            Log.d("MainActivity", "status is "+ status);
            Log.d("MainActivity", "data is " + userInfo);
            Log.d("MainActivity", "msg is " + msg);
            Log.d("MainActivity", "token is " + token + "************************");
            Log.d("TAG", "user id: "+userInfo.getId());
            Log.d("TAG", "user name: "+userInfo.getName());
            Log.d("TAG", "user school: "+userInfo.getSchool());
            Log.d("TAG", "user password: "+userInfo.getPassword());

            Log.d("TAG", "User Login Test: "+myApplication.getLoginUser().getInfo().getName());
        }
    }

    class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectionManager = (ConnectivityManager)
                    getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectionManager.getActiveNetworkInfo();
            if (networkInfo == null) {
                Toast.makeText(context, "网络异常", Toast.LENGTH_SHORT).show();
            }
        }

    }
    @Override
    public void onBackPressed() {
        ActivityCollector.finishAll();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkChangeReceiver);
    }
}
