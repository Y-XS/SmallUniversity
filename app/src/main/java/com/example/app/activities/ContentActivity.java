package com.example.app.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.app.adapters.GVAdapter;
import com.example.app.adapters.GridViewAdapter;
import com.example.app.adapters.ImageAdapter;
import com.example.app.beans.PostingItem;

import java.util.ArrayList;
import java.util.List;

public class ContentActivity extends BaseActivity {
    private String name,text,time;
    private String headImgUrl,imgUrl;
    private TextView contentText,userName,pubTime;
    private ImageView headImg;
    private ImageButton btn_back;

    private GridView mGridView;
    private ImageAdapter adapter;
    private RecyclerView recyclerView;
    private List<String> imgUrls = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        contentText = (TextView)findViewById(R.id.content_text);
        userName = (TextView)findViewById(R.id.content_username);
        headImg = (ImageView)findViewById(R.id.content_head_img);
        mGridView = (GridView)findViewById(R.id.content_gv);
        btn_back = (ImageButton)findViewById(R.id.btn_back_cnt);
        pubTime = (TextView)findViewById(R.id.content_time);

        Intent intent = getIntent();
        PostingItem item = (PostingItem) intent.getSerializableExtra("item");
        text=item.getText();
        name=item.getName();
        time=item.getTime();
        headImgUrl=item.getHeadImgUrl();
        imgUrls=item.getImageUrl();
        contentText.setText(text);
        userName.setText(name);
        pubTime.setText(time);
        Glide.with(this).load(headImgUrl).placeholder(R.mipmap.error).centerCrop().into(headImg);

//        initDatas();
        adapter=new ImageAdapter(ContentActivity.this,imgUrls);
        mGridView.setAdapter(adapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(ContentActivity.this,ImageActivity.class);
                intent.putExtra("URL",imgUrls.get(position));
                startActivity(intent);
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContentActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initDatas() {
        imgUrls.clear();
        imgUrls.add(imgUrl);
    }
}
