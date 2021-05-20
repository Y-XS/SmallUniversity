package com.example.app.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.app.adapters.PostingItemAdapter;
import com.example.app.beans.Address;
import com.example.app.beans.PostingItem;
import com.example.app.beans.PostingItemData;
import com.example.app.beans.RLInfo;
import com.example.app.utils.HttpUtil;
import com.example.app.utils.MyApplication;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class SearchActivity extends BaseActivity implements AdapterView.OnItemSelectedListener {

    private MyApplication myApplication;
    private RLInfo userInfo;

    private EditText searchInfo_description;
    private Spinner spinner;
    private ArrayAdapter<String> sp_adapter;
    private Button search_btn;
    private ImageButton btn_back;
    private RecyclerView recyclerView;
    private PostingItemAdapter adapter;
    private List<PostingItem> PostingItems = new ArrayList<>();
    private List<PostingItemData.ItemData> data;

    private String netAddr = "http://1.15.115.24/searchMsg";
    private Address addr;
    private String url;

    private String description,group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        myApplication = (MyApplication)getApplication();
        userInfo = myApplication.getLoginUser();

        searchInfo_description = (EditText) findViewById(R.id.search_description);
        search_btn = (Button)findViewById(R.id.search_btn_search);
        btn_back = (ImageButton)findViewById(R.id.search_btn_back);
        spinner = (Spinner)findViewById(R.id.search_sp);
        sp_adapter = new ArrayAdapter<String>(SearchActivity.this,
                android.R.layout.simple_list_item_1, getDataSource());
        spinner.setAdapter(sp_adapter);
        spinner.setOnItemSelectedListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.search_rv);
        recyclerView.setLayoutManager (new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
        adapter = new PostingItemAdapter(PostingItems);
        recyclerView.setAdapter(adapter);
        DividerItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(this,R.drawable.divider_rclist));
        recyclerView.addItemDecoration(divider);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PostingItems.clear();
                description = searchInfo_description.getText().toString();
                addr = new Address(netAddr);
                addr.add(true,"group",group)
                        .add(false,"desc",description)
                        .add(false,"token",userInfo.getToken());
                url = addr.getAddress();
                Log.d("TAG", "===url= "+url+"desc= "+description);
                HttpUtil.getData(url, new okhttp3.Callback(){

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        String responseData = response.body().string();
//                        parseJSONWithJSONObject(responseData);
                        Log.d("Search::", "onResponse: "+responseData);

                        parseJSONWithGSON(responseData);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
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
    public List<String> getDataSource() {
        List<String> list = new ArrayList<String>();
        list.add("墙");
        list.add("活动");
        list.add("比赛");
        list.add("志愿");
        list.add("兼职");
        return list;
    }

    private void parseJSONWithGSON(String jsonData) {
        int visibility;
        String headImgUrl;
        List<PostingItemData.Pics> pics;
        ArrayList<String> imgUrls=new ArrayList<>();
        Gson gson = new Gson();
        PostingItemData datas = gson.fromJson(jsonData, PostingItemData.class);

        if ("1".equals(datas.getStatus())) {
            data = datas.getData();
            if (data.size() == 0) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SearchActivity.this, "没有相关数据", Toast.LENGTH_SHORT).show();
                    }
                });
                return;
            }
            for (PostingItemData.ItemData itemData : data) {
                if (itemData.getPic().size() != 0) {
                    pics = itemData.getPic();
                    for(PostingItemData.Pics pic:pics){
                        imgUrls.add(pic.getUrl());
                    }
                    visibility = View.VISIBLE;
                } else {
//                    ImgUrl = "noImg";
                    visibility = View.GONE;
                }
                if(itemData.getFace()!=null){
                    headImgUrl = itemData.getFace().getUrl();
                }else {
                    headImgUrl = "noHeadImg";
                }
                Log.d("TAG", "------------------------>>>>data: " + itemData.getName()+" "+itemData.getTime()+ " "+itemData.getId());
                PostingItems.add(new PostingItem(itemData.getName(), itemData.getTitle(),itemData.getTime(), headImgUrl, imgUrls, itemData.getMsg(), visibility));
            }
            Log.d("TAG", "parseJSONWithGSON: " + data.get(0));
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String itemString = spinner.getItemAtPosition(position).toString();
        switch (itemString){
            case "墙":group="wall";break;
            case "活动":group="activity";break;
            case "比赛":group="competition";break;
            case "志愿":group="volunteer";break;
            case "兼职":group="ptjob";break;
        }
        Toast.makeText(this,"选择的是"+itemString,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
