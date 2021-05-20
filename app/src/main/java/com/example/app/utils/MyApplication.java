package com.example.app.utils;


import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.app.beans.ChatMsg;
import com.example.app.beans.RLInfo;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import io.socket.emitter.Emitter;

public class MyApplication extends Application {
    public static int WITH_IMG = View.VISIBLE;
    public static int NO_IMG = View.GONE;

    private static Context context;
    private RLInfo loginUser;

    private SocketUtil mSocket;
    private List<ChatMsg> msgs = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        mSocket = new SocketUtil();
        LitePal.initialize(this);
        Log.d("TAG", "onCreate: ---------->mSocket:"+mSocket);
    }
    public static Context getContext(){
        return context;
    }

    public RLInfo getLoginUser(){
        return loginUser;
    }
    public void userLogin(RLInfo info){
        loginUser = info;
    }

    public SocketUtil getSocket(){
        return mSocket;
    }

}
