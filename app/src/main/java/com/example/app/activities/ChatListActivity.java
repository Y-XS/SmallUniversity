package com.example.app.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.app.adapters.MsgListItemAdapter;
import com.example.app.beans.Address;
import com.example.app.beans.MsgListItem;
import com.example.app.db.ChatMsgData;
import com.example.app.db.ChatTable;
import com.example.app.db.MainTable;
import com.example.app.utils.HttpUtil;
import com.example.app.utils.MyApplication;
import com.example.app.utils.SocketUtil;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

import io.socket.emitter.Emitter;
import okhttp3.Call;
import okhttp3.Response;

public class ChatListActivity extends BaseActivity {

    private MyApplication myApplication;
    private String token,myId;
    private SocketUtil mSocket;
    private Address addr;
    private String netUrl="http://1.15.115.24/getUserPic";
    private HashMap<String ,String > headImgs = new HashMap<>();
    private int count=0;

    private ImageButton btn_back,btn_menu;
    private SwipeRefreshLayout swipeRefresh;
    private List<MsgListItem> msgListItems = new ArrayList<>();
    private List<ChatMsgData> mData = new ArrayList<>();
    private MsgListItemAdapter adapter;
    private MsgListItem[] items = {
            new MsgListItem("12345678911","111","17:02","http://1.15.115.24/images/pic_2.jpg","this is text1"),
//            new MsgListItem("222","17:03",R.mipmap.nav_me,"this is text2"),
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        myApplication = (MyApplication)getApplication();
        token = myApplication.getLoginUser().getToken();
        myId = myApplication.getLoginUser().getInfo().getId();

        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_msg);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.
                OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });
        initMsgListItems();
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recycler_view_msg);
        recyclerView.setLayoutManager (new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        adapter = new MsgListItemAdapter(msgListItems);
        recyclerView.setAdapter(adapter);
        DividerItemDecoration divider = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(this,R.drawable.msglist_divider));
        recyclerView.addItemDecoration(divider);

        btn_back = (ImageButton)findViewById(R.id.btn_back_msg);
        btn_menu = (ImageButton)findViewById(R.id.btn_menu_msg);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatListActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        final AlertDialog.Builder confirm_clear = new AlertDialog.Builder(this);
        confirm_clear.setTitle("提示")
                .setMessage("确认清除消息列表吗？");
        confirm_clear.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                LitePal.deleteAll(MainTable.class);
                LitePal.deleteAll(ChatTable.class);
                msgListItems.clear();
                adapter.notifyDataSetChanged();
                Toast.makeText(ChatListActivity.this,"消息列表已清除",Toast.LENGTH_SHORT).show();
            }
        });
        confirm_clear.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        btn_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm_clear.show();
            }
        });
        loadHeadImg();
    }

    private void initMsgListItems() {
        msgListItems.clear();
        mData.clear();
        List<MainTable> items= LitePal.where("userId=?",myId).find(MainTable.class);

        for(MainTable item:items){

            List<ChatTable> citem = LitePal.where("chatId=?",String.valueOf(item.getId()))
                    .order("time desc")
                    .limit(1)
                    .find(ChatTable.class);

            msgListItems.add(new MsgListItem(citem.get(0).getAnotherId(),citem.get(0).getAnotherName(),citem.get(0).getTime(),"",citem.get(0).getContent()));

            headImgs.put(citem.get(0).getAnotherId(),"");
        }
        for (Map.Entry<String ,String > mm:headImgs.entrySet()){
            Log.d("TAG", "initMsgListItems: =====map: "+mm.getKey()+" "+mm.getValue());
        }
    }
    private void refreshData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initMsgListItems();
                        loadHeadImg();
                        adapter.notifyDataSetChanged();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        }).start();
    }
    private void loadHeadImg(){
        for (final Map.Entry<String ,String > mm:headImgs.entrySet()){
            addr = new Address(netUrl);
            addr.add(true,"id",mm.getKey())
                    .add(false,"token",token);
            String url=addr.getAddress();
            HttpUtil.sendOkHttpRequest(url,new okhttp3.Callback(){
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {

                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    JSONObject jsonObject = null;
                    JSONObject jsonObject1 = null;
                    try {
                        jsonObject = new JSONObject(response.body().string());
                        String data = jsonObject.getString("data");
                        jsonObject1 = new JSONObject(data);
                        String headImgUrl = jsonObject1.getString("Url");
                        mm.setValue(headImgUrl);
                        Log.d("TAG", "onResponse: ============mm:"+mm.getKey()+" "+mm.getValue());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                for (MsgListItem item:msgListItems){
                                    if (mm.getKey().equals(item.getUserId())){
                                        item.setHeadImgUrl(mm.getValue());
                                        adapter.notifyDataSetChanged();
                                    }
                                }
//                                msgListItems.get(count).setHeadImgUrl(headImgs.get(msgListItems.get(count).getUserId()));
//                                Log.d("TAG", "onCreate: +++++"+"id:"+headImgs.get(msgListItems.get(count).getUserId())+" "+count+" "+msgListItems.get(count).getHeadImgUrl());
//                                adapter.notifyDataSetChanged();
//                                count++;
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        }
    }

    public static List distinct(List list) {
        HashSet h = new HashSet(list);
        list.clear();
        list.addAll(h);
        return list;
    }
}
