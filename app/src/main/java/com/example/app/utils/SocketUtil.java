package com.example.app.utils;

import android.util.Log;

import com.example.app.activities.ChatActivity;
import com.example.app.beans.ChatMsg;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class SocketUtil{
    private String url = "http://1.15.115.24:3000";
    public final static String FLAG_ONLINE = "online",FLAG_MSG = "message",FLAG_CLOSE = "close";
    private Socket socket;
    public Emitter.Listener onNewMsg;
    private List<ChatMsg> msgs = new ArrayList<>();

    public SocketUtil(){
        try {
            socket = IO.socket(url);
        } catch (URISyntaxException e) {}
    }
    public Socket getSocket(){
        return socket;
    }

    public void init(){
        socket.on("msg", onNewMsg);
        socket.connect();
    }

    public void online(String userId){
        socket.emit(FLAG_ONLINE,userId);
        Log.d("socketutil", "online: -------------->"+userId);
    }
    public void sendMsg(String sname,String rname,String sid,String rid,String msg,String time){
        Log.d("TAG", "sendMsg: ***************************");
        socket.emit(FLAG_MSG,sname,rname,sid,rid,msg,time);
    }
    public void logout(){
        socket.emit(FLAG_CLOSE);
        Log.d("socketutil", "logout: ---------------");
    }
    public void clear(){
        socket.disconnect();
        socket.off("msg",onNewMsg);
        Log.d("TAG", "clear: --------------------");
    }

}
