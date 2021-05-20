package com.example.app.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.app.adapters.ChatMsgAdapter;
import com.example.app.beans.ChatMsg;
import com.example.app.db.ChatListTable;
import com.example.app.db.ChatMsgData;
import com.example.app.db.ChatTable;
import com.example.app.db.MainTable;
import com.example.app.utils.MyApplication;
import com.example.app.utils.SocketUtil;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.socket.emitter.Emitter;

public class ChatActivity extends BaseActivity {

    private MyApplication myApplication;
    private ImageButton btn_back,btn_menu;

    private List<ChatMsg> msgList = new ArrayList<>();
    private List<ChatTable> mData;
    private EditText inputText;
    private TextView tv_userName;
    private Button bsend;
    private RecyclerView msgRecyclerView;
    private ChatMsgAdapter adapter;
    private SwipeRefreshLayout swipeRefresh;

    private String userId,userName,headImgUrl;
    private String myId,myName,myHeadImgUrl;
    private SocketUtil mSocket;
    private String data;

    private LocalReceiver localReceiver;
    private IntentFilter intentFilter;
    private LocalBroadcastManager localBroadcastManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        myApplication = (MyApplication)getApplication();
        userId = getIntent().getStringExtra("id");
        userName = getIntent().getStringExtra("userName");
        headImgUrl = getIntent().getStringExtra("headImg");
        myId = myApplication.getLoginUser().getInfo().getId();
        myName = myApplication.getLoginUser().getInfo().getName();
        myHeadImgUrl = myApplication.getLoginUser().getInfo().getPic().getUrl();

        mSocket = myApplication.getSocket();
        Log.d("ChatActivity", "onCreate: "+userId+" "+headImgUrl+" "+userName+" "+mSocket);

        tv_userName = (TextView)findViewById(R.id.tv_chat_userName);
        tv_userName.setText(userName);
        btn_back = (ImageButton)findViewById(R.id.btn_back_chat);
        btn_menu = (ImageButton)findViewById(R.id.btn_menu_chat);
        inputText = (EditText) findViewById(R.id.input);
        bsend = (Button) findViewById(R.id.send);
        msgRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_chatmsg);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//        layoutManager.setStackFromEnd(true);

        initMsgs();
        msgRecyclerView.setLayoutManager(layoutManager);
        adapter = new ChatMsgAdapter(msgList);
        msgRecyclerView.setAdapter(adapter);
//        msgRecyclerView.scrollToPosition(adapter.getItemCount()-1);
        msgRecyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if(bottom<oldBottom){
                    msgRecyclerView.postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            msgRecyclerView.scrollToPosition(adapter.getItemCount()-1);
                        }
                    },0);

                }
            }
        });
        final AlertDialog.Builder confirm_delete = new AlertDialog.Builder(this);
        confirm_delete.setTitle("提示")
                .setMessage("确认清除聊天记录吗？");
        confirm_delete.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                LitePal.deleteAll(MainTable.class,"userId=? and anotherId=?",myId,userId);
                LitePal.deleteAll(ChatTable.class,"userId=? and anotherId=?",myId,userId);
                msgList.clear();
                adapter.notifyDataSetChanged();
                Toast.makeText(ChatActivity.this,"聊天记录已清除",Toast.LENGTH_SHORT).show();
            }
        });
        confirm_delete.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        bsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = inputText.getText().toString();
                if (!"".equals(content)) {
                    ChatMsg msg = new ChatMsg(headImgUrl,myHeadImgUrl,userName,myName,content, ChatMsg.TYPE_SENT);
                    msgList.add(msg);
                    adapter.notifyItemInserted(msgList.size() - 1); // 当有新消息时，刷新RecyclerView中的显示
                    msgRecyclerView.scrollToPosition(msgList.size() - 1); // 将RecyclerView定位到最后一行
                    inputText.setText(""); // 清空输入框中的内容

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM月dd日 HH:mm:ss");
                    Date date = new Date(System.currentTimeMillis());
                    String time = simpleDateFormat.format(date);
                    mSocket.sendMsg(myName,userName,myId,userId,content,time);
                    Log.d("TAG", "bsend-----------------> "+myName+" "+" "+userName+" "+myId+" "+userId+" "+content+" "+time);

                    if(LitePal.count(MainTable.class)==0){
                        Log.d("TAG", "onClick: -----count"+LitePal.count(MainTable.class));
                        MainTable mainTable = new MainTable();
                        mainTable.setId(1);
                        mainTable.setUserId(myId);
                        mainTable.setAnotherId(userId);
                        mainTable.save();

                        ChatTable table = new ChatTable();
                        table.setId(1);
                        table.setUserId(myId);
                        table.setAnotherId(userId);
                        table.setUserName(myName);
                        table.setAnotherName(userName);
                        table.setContent(content);
                        table.setTime(time);
                        table.setType(ChatTable.TYPE_SEND);
                        table.save();
                    }else {
                        boolean a = LitePal.isExist(MainTable.class,"userId=? and anotherId=?",myId,userId);
                        Log.d("TAG", "onClick: -----exist? "+a);
                        int id=0;
                        if(!a){
                            id = LitePal.findLast(MainTable.class).getId()+1;
                            MainTable table = new MainTable();
                            table.setId(id);
                            table.setUserId(myId);
                            table.setAnotherId(userId);
                            table.save();
                            Log.d("TAG", "onClick: new id="+id);
                        }
                        else {
                            List<MainTable> tables = LitePal.where("userId=? and anotherId=?",myId,userId).find(MainTable.class);
                            id = tables.get(0).getId();
                            Log.d("TAG", "onClick: exist id="+id);
                        }
                        ChatTable table2 = new ChatTable();
                        Log.d("TAG", "onClick: =====id====="+id);
                        table2.setId(id);
                        table2.setUserId(myId);
                        table2.setAnotherId(userId);
                        table2.setUserName(myName);
                        table2.setAnotherName(userName);
                        table2.setType(ChatTable.TYPE_SEND);
                        table2.setTime(time);
                        table2.setContent(content);
                        table2.save();
                    }
                }
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
        btn_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm_delete.show();
            }
        });

        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.app.LOCAL_BROADCAST");
        localReceiver = new LocalReceiver();
        localBroadcastManager.registerReceiver(localReceiver, intentFilter);
    }
    private void initMsgs() {
        mData = LitePal.where("userId=? and anotherId=?",myId,userId).order("time").find(ChatTable.class);
        for (ChatTable data:mData){
            int type = data.getType();
            msgList.add(new ChatMsg(headImgUrl,myHeadImgUrl,userName,myName,data.getContent(),type));
        }

    }

    class LocalReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
//            Toast.makeText(context, "received local broadcast", Toast.LENGTH_SHORT).
//                    show();
            Log.d("BDRCV", "onReceive: --------------->");
            String sid = intent.getStringExtra("sid");
            String rid = intent.getStringExtra("rid");
            String sname = intent.getStringExtra("sname");
            String rname = intent.getStringExtra("rname");
            String msg = intent.getStringExtra("msg");
            String rtime = intent.getStringExtra("rtime");
            Log.d("BDRCV", "onReceive: "+sname+" "+rname+" "+sid+" "+rid+" "+msg+" "+rtime);
            msgList.add(new ChatMsg(headImgUrl,myHeadImgUrl,userName,myName,msg,ChatTable.TYPE_RECV));
            adapter.notifyDataSetChanged();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(localReceiver);
    }
}
