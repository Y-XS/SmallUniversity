//package com.example.app.activities;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.Toast;
//
//import com.example.app.beans.Address;
//import com.example.app.utils.HttpUtil;
//
//import org.jetbrains.annotations.NotNull;
//
//import java.io.IOException;
//
//import okhttp3.Call;
//import okhttp3.Response;
//
//public class WelcomeActivity extends AppCompatActivity {
//
//    private SharedPreferences account_remember;
//    private String userId,userPassword;
//    private boolean isFirstLogin = true;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        userId = account_remember.getString("Id","");
//        userPassword = account_remember.getString("Password","");
//        isFirstLogin = account_remember.getBoolean("isFirstLogin",true);
//
//        if(!isFirstLogin){
//
//            addr = new Address(netAddr);
//            addr.add(true,"id",userId)
//                    .add(false,"password",userPassword);
//            address = addr.getAddress();
//            HttpUtil.sendOkHttpRequest(address, new okhttp3.Callback(){
//                @Override
//                public void onResponse(Call call, Response response) throws IOException {
//                    responseData = response.body().string();
//                    Log.d("LoginActivity", "onResponse: "+responseData);
//                    parseJSONWithGSON(responseData);
//
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            if ("1".equals(status)){
//                                Toast.makeText(LoginActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
//                                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
//                                startActivity(intent);
//                                finish();
//                            }else {
//                                Toast.makeText(LoginActivity.this,"用户名或密码错误",Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
//                }
//
//                @Override
//                public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                }
//            });
//        }
//    }
//}
