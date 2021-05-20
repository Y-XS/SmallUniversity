package com.example.app.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public class RegisterActivity extends BaseActivity {

    private EditText userName,passWord,passWord2,school,tel;
    private Button btn_register;
    private String sname,spassword,spassword2,sschool,stel,address,responseData;
    private String netAddr = "http://1.15.115.24/register";
    private Address addr;
    private String status,msg,token;
    private RLInfo.UserInfo userInfo;
    private boolean flag = true,flag2 = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userName = (EditText)findViewById(R.id.register_username);
        passWord = (EditText)findViewById(R.id.register_password);
        passWord2 = (EditText)findViewById(R.id.register_password_2);
        school = (EditText)findViewById(R.id.register_school);
        tel = (EditText)findViewById(R.id.register_tel);
        btn_register = (Button)findViewById(R.id.button_register);

        passWord.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // et.getCompoundDrawables()得到一个长度为4的数组，分别表示左右上下四张图片
                Drawable drawable = passWord.getCompoundDrawables()[2];
                //如果右边没有图片，不再处理
                if (drawable == null)
                    return false;
                //如果不是按下事件，不再处理
                if (event.getAction() != MotionEvent.ACTION_UP)
                    return false;
                if (event.getX() > passWord.getWidth()
                        - passWord.getPaddingRight()
                        - drawable.getIntrinsicWidth()){
//                    password.setText("");
                    if(flag){
                        passWord.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        flag=false;
                    }else {
                        passWord.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        flag=true;
                    }
                }
                return false;
            }
        });
        passWord2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // et.getCompoundDrawables()得到一个长度为4的数组，分别表示左右上下四张图片
                Drawable drawable = passWord.getCompoundDrawables()[2];
                //如果右边没有图片，不再处理
                if (drawable == null)
                    return false;
                //如果不是按下事件，不再处理
                if (event.getAction() != MotionEvent.ACTION_UP)
                    return false;
                if (event.getX() > passWord.getWidth()
                        - passWord.getPaddingRight()
                        - drawable.getIntrinsicWidth()){
//                    password.setText("");
                    if(flag2){
                        passWord.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        flag2=false;
                    }else {
                        passWord.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        flag2=true;
                    }
                }
                return false;
            }
        });


        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sname = userName.getText().toString();
                spassword = passWord.getText().toString();
                spassword2 = passWord2.getText().toString();
                sschool = school.getText().toString();
                stel = tel.getText().toString();

                Log.d("RegisterActivity", "onCreate: "+sname+"\n"+spassword+"\n"+sschool+"\n"+stel);
                if(!spassword.equals(spassword2)){
                    passWord2.clearComposingText();
                    Log.d("RegisterActivity", "onClick: 两次输入密码不一致");
                }
                spassword2 = passWord2.getText().toString();

                addr = new Address(netAddr);
                addr.add(true,"id",stel)
                        .add(false,"name",sname)
                        .add(false,"school",sschool)
                        .add(false,"password",spassword);
                address = addr.getAddress();

                Log.d("RegisterActivity", "onClick: "+address);

                HttpUtil.sendOkHttpRequest(address, new okhttp3.Callback(){
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        responseData = response.body().string();
                        Log.d("RegisterActivity", "onResponse: "+responseData);
                        parseJSONWithGSON(responseData);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if ("0".equals(status)){
                                    Toast.makeText(RegisterActivity.this,"该手机号已被注册",Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(RegisterActivity.this,"注册成功",Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                                    startActivity(intent);
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

        status = info.getStatus();
        msg = info.getMsg();
        token = info.getToken();

        if ("1".equals(status)){
            userInfo = info.getInfo();
            Log.d("MainActivity", "status is "+ status);
            Log.d("MainActivity", "data is " + userInfo);
            Log.d("MainActivity", "msg is " + msg);
            Log.d("MainActivity", "token is " + token + "************************");
        }


    }
}
