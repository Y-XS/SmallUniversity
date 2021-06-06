package com.example.app.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.app.db.ChatMsgData;
import com.example.app.db.ChatTable;
import com.example.app.db.MainTable;
import com.example.app.fragments.ActivityFragment;
//import com.example.app.fragments.PtjobFragment;
import com.example.app.utils.MyApplication;
import com.example.app.utils.SocketUtil;
import com.google.android.material.tabs.TabLayout;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import io.socket.emitter.Emitter;

public class MainActivity extends BaseActivity {

    private MyApplication myApplication;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private List<String> tabTitles = new ArrayList<>();
    private List<Fragment> tabFragments = new ArrayList<>();
    private String mTitles[] = {
            "墙", "活动", "比赛", "志愿", "兼职",
            "跳蚤", "财经", "时尚"};

    private RelativeLayout bottom;
    private ImageView nav_msg,nav_publish;
    private ImageView nav_me;
    private FrameLayout frameLayout;
    private ImageButton btn_search;

    private boolean isDoubleBack = false;

    private SocketUtil mSocket;
    private String sid,msg,rtime;
    private LocalBroadcastManager localBroadcastManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myApplication = (MyApplication)getApplication();

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.vp_content);
        bottom = (RelativeLayout)findViewById(R.id.container);
        nav_msg = (ImageView)findViewById(R.id.nav_msg);
        nav_me = (ImageView)findViewById(R.id.nav_me);
        nav_publish = (ImageView)findViewById(R.id.nav_pub);
        btn_search = (ImageButton)findViewById(R.id.search_button);

        tabFragments.add(ActivityFragment.newInstance("wall"));
        tabFragments.add(ActivityFragment.newInstance("activity"));
        tabFragments.add(ActivityFragment.newInstance("competition"));
        tabFragments.add(ActivityFragment.newInstance("volunteer"));
        tabFragments.add(ActivityFragment.newInstance("ptjob"));

        Log.d("Main", "onCreate: ============================user: "+myApplication.getLoginUser().getInfo().getName());
        for(int i =0;i<5;i++){
            tabTitles.add(mTitles[i]);
        }

        ViewCompat.setElevation(tabLayout, 10);


        viewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return tabFragments.get(position);
            }

            @Override
            public int getCount() {
                return tabFragments.size();
            }
            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                super.destroyItem(container, position, object);
            }
            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {

                return tabTitles.get(position);
            }

        });
        viewPager.setOffscreenPageLimit(4);
        viewPager.setCurrentItem(0,false);
        tabLayout.setupWithViewPager(viewPager);

        nav_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ChatListActivity.class);
                startActivity(intent);
            }
        });
        nav_me.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MeActivity.class);
                startActivity(intent);
            }
        });
        nav_publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                String id = myApplication.getLoginUser().getInfo().getId();
//                Log.d("TAG", "Socket Test: ----------------->"+id+" "+mSocket);
//                mSocket.online(id);
//                mSocket.sendMsg(id,"111","test","14:49");

                Intent intent = new Intent(MainActivity.this,PublishActivity.class);

                switch (viewPager.getCurrentItem()){
                    case 0:
                        intent.putExtra("group","wall");
                        Toast.makeText(MainActivity.this,"当前板块：墙",Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        intent.putExtra("group","activity");
                        Toast.makeText(MainActivity.this,"当前板块：活动",Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        intent.putExtra("group","competition");
                        Toast.makeText(MainActivity.this,"当前板块：比赛",Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        intent.putExtra("group","volunteer");
                        Toast.makeText(MainActivity.this,"当前板块：志愿",Toast.LENGTH_SHORT).show();
                        break;
                    case 4:
                        intent.putExtra("group","ptjob");
                        Toast.makeText(MainActivity.this,"当前板块：兼职",Toast.LENGTH_SHORT).show();
                        break;
                }
                startActivity(intent);
            }
        });
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,SearchActivity.class);
                startActivity(intent);
            }
        });

        //====================改动
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        mSocket = myApplication.getSocket();
        Log.d("TAG", "onCreate: ---------->Socket:"+mSocket);
        mSocket.onNewMsg = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String userName = args[0].toString();
                        String myName = args[1].toString();
                        String userId = args[2].toString();
                        String myId = args[3].toString();
                        String msg = args[4].toString();
                        String rtime = args[5].toString();
                        Log.d("MSG", "run: --------->" + userName + " " + myName + " " + userId + " " + myId + " " + msg + " " + rtime);
                        Intent intent = new Intent("com.example.app.LOCAL_BROADCAST");
                        intent.putExtra("sname", userName);
                        intent.putExtra("rname", myName);
                        intent.putExtra("sid", userId);
                        intent.putExtra("rid", myId);
                        intent.putExtra("msg", msg);
                        intent.putExtra("rtime", rtime);
                        localBroadcastManager.sendBroadcast(intent);

                        if (LitePal.count(MainTable.class) == 0) {
                            Log.d("TAG", "onClick: -----count" + LitePal.count(MainTable.class));
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
                            table.setContent(msg);
                            table.setTime(rtime);
                            table.setType(ChatTable.TYPE_SEND);
                            table.save();
                        } else {
                            boolean isExist = LitePal.isExist(MainTable.class, "userId=? and anotherId=?", myId, userId);
                            int id = 0;
                            if (!isExist) {
                                id = LitePal.findLast(MainTable.class).getId() + 1;
                                MainTable table = new MainTable();
                                table.setId(id);
                                table.setUserId(myId);
                                table.setAnotherId(userId);
                                table.save();
                                Log.d("TAG", "onClick: new id=" + id);
                            } else {
                                List<MainTable> tables = LitePal.where("userId=? and anotherId=?", myId, userId).find(MainTable.class);
                                id = tables.get(0).getId();
                                Log.d("TAG", "onClick: exist id=" + id);
                            }
                            ChatTable table2 = new ChatTable();
                            Log.d("TAG", "onClick: =====id=====" + id);
                            table2.setId(id);
                            table2.setUserId(myId);
                            table2.setAnotherId(userId);
                            table2.setUserName(myName);
                            table2.setAnotherName(userName);
                            table2.setType(ChatTable.TYPE_RECV);
                            table2.setTime(rtime);
                            table2.setContent(msg);
                            table2.save();
                        }
                    }
                });
            }
        };
        mSocket.init();
        mSocket.online(myApplication.getLoginUser().getInfo().getId());
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("MainActivity", "onStart: ==============");
    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }


    @Override
    public void onBackPressed() {

        if (isDoubleBack) {
            finish();
        }
        isDoubleBack = true;
        Toast.makeText(this,"再按一次退出", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isDoubleBack = false;
            }
        }, 2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        mSocket.logout();
//        mSocket.clear();
        mSocket.logout();
        mSocket.clear();
        Log.d("MainActivity", "onDestroy: -------------> quit");
    }
}
