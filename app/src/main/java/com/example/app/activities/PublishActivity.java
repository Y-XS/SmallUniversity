package com.example.app.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.app.adapters.GridViewAdapter;
import com.example.app.beans.Address;
import com.example.app.beans.RLInfo;
import com.example.app.utils.HttpUtil;
import com.example.app.utils.MyApplication;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class PublishActivity extends BaseActivity {

    private MyApplication myApplication;
    private RLInfo userInfo;

    private String mGroup;
    public static final int CHOOSE_PHOTO = 2;
    private String url = "http://1.15.115.24/uploadPic";

    private String netAddr = "http://1.15.115.24/uploadMsg";
    private Address addr;
    private String address;
    private String title,name,time,group,content,token;
    private String content_status,content_id;

    ImageButton btn_back;
    Button btn_pub;
    ImageView headImg;
    EditText editText;
    TextView userName;

    private List<String> mDatas;
    private GridView mGridView;
    private GridViewAdapter adapter;
    private String path,responseData;
    private String status_pic,msg_pic;
    private int mPosition,count=0,i=0;
    boolean isFirst = true,isAdded, pathIsNull = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);

        myApplication = (MyApplication)getApplication();
        userInfo = myApplication.getLoginUser();
        token = myApplication.getLoginUser().getToken();
        mGroup = getIntent().getStringExtra("group");

        mGridView = (GridView)findViewById(R.id.pub_gv);
        btn_back = (ImageButton)findViewById(R.id.btn_back_publish);
        btn_pub = (Button)findViewById(R.id.btn_pub_publish);
        editText = (EditText)findViewById(R.id.part_edit);
        userName = (TextView)findViewById(R.id.publish_username);
        headImg = (ImageView)findViewById(R.id.head_img_pub);

        userName.setText(userInfo.getInfo().getName());
        Glide.with(this).load(userInfo.getInfo().getPic().getUrl()).error(R.mipmap.error).into(headImg);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PublishActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

        btn_pub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_pub.setBackgroundResource(R.drawable.shape_btn_unpublish);
                btn_pub.setClickable(false);
                Log.d("TAG", "onClick: " + mDatas);
                final ProgressDialog progressDialog = new ProgressDialog(PublishActivity.this);
                progressDialog.setTitle("内容上传中");
                progressDialog.setMessage("uploading...");
                progressDialog.setCancelable(true);

                title = myApplication.getLoginUser().getInfo().getName();
                name = myApplication.getLoginUser().getInfo().getId();
                content = editText.getText().toString();

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM月dd日 HH:mm");
                Date date = new Date(System.currentTimeMillis());
                time = simpleDateFormat.format(date);

                addr = new Address(netAddr);
                addr.add(true,"title",title)
                        .add(false,"name",name)
                        .add(false,"time",time)
                        .add(false,"group",mGroup)
                        .add(false,"content",content)
                        .add(false,"token",token);
                address = addr.getAddress();
                HttpUtil.sendOkHttpRequest(address, new okhttp3.Callback() {
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        responseData = response.body().string();
                        parseJSONWithJSONObject_Content(responseData);
                        Log.d("TAG", "onResponse1: "+content_status+" time="+time+" "+responseData);

                        if(mDatas.size()>0){
//                            progressDialog.show();
                            HttpUtil.upImgs(url,mDatas, content_id,token,new okhttp3.Callback(){
                                @Override
                                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                    responseData = response.body().string();
                                    parseJSONWithJSONObject(responseData);
                                    Log.d("TAG", "onResponse: "+token);
                                    Log.d("TAG", "onResponse: "+content_id);
                                    Log.d("TAG", "onResponse: "+responseData);
                                    if("1".equals(status_pic)){
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
//                                                progressDialog.dismiss();
                                                editText.setText("");
                                                mDatas.clear();
                                                btn_pub.setClickable(true);
                                                btn_pub.setBackgroundResource(R.drawable.shape_btn_publish);
                                                Toast.makeText(PublishActivity.this,"发送成功!",Toast.LENGTH_SHORT).show();
                                                adapter=new GridViewAdapter(PublishActivity.this, mDatas,GridViewAdapter.TYPE_PUB);
                                                mGridView.setAdapter(adapter);
                                                adapter.notifyDataSetChanged();
                                            }
                                        });
                                    }
                                }
                                @Override
                                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                }
                            });
                        }else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    editText.setText("");
                                    Toast.makeText(PublishActivity.this,"发送成功!",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        //改动
//                        count = mDatas.size();
//                        while (i++<count){
////                            progressDialog.show();
//                            HttpUtil.upImg(url,mDatas.get(i), content_id,token,new okhttp3.Callback(){
//                                @Override
//                                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
//                                    responseData = response.body().string();
//                                    parseJSONWithJSONObject(responseData);
//                                    Log.d("TAG", "onResponse: "+token);
//                                    Log.d("TAG", "onResponse: "+content_id);
//                                    Log.d("TAG", "onResponse: "+responseData);
////                                    if("1".equals(status_pic)){
////                                        runOnUiThread(new Runnable() {
////                                            @Override
////                                            public void run() {
//////                                                progressDialog.dismiss();
////                                                editText.setText("");
////                                                mDatas.clear();
////                                                Toast.makeText(PublishActivity.this,"发送成功!",Toast.LENGTH_SHORT).show();
////                                                adapter=new GridViewAdapter(PublishActivity.this, mDatas,GridViewAdapter.TYPE_PUB);
////                                                mGridView.setAdapter(adapter);
////                                                adapter.notifyDataSetChanged();
////                                            }
////                                        });
////                                    }
//                                    if(i+1==count){
//                                        runOnUiThread(new Runnable() {
//                                            @Override
//                                            public void run() {
////                                                progressDialog.dismiss();
//                                                editText.setText("");
//                                                mDatas.clear();
//                                                Toast.makeText(PublishActivity.this,"发送成功!",Toast.LENGTH_SHORT).show();
//                                                adapter=new GridViewAdapter(PublishActivity.this, mDatas,GridViewAdapter.TYPE_PUB);
//                                                mGridView.setAdapter(adapter);
//                                                adapter.notifyDataSetChanged();
//                                            }
//                                        });
//                                    }
//                                }
//                                @Override
//                                public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                                }
//                            });
//                        }
                    }
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    }
                });
            }
        });

        initDatas();
        adapter=new GridViewAdapter(PublishActivity.this,mDatas,GridViewAdapter.TYPE_PUB);
        mGridView.setAdapter(adapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                mPosition = position;
                if(position ==  parent.getCount()-1){
                    if (ContextCompat.checkSelfPermission(PublishActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.
                            PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(PublishActivity.this, new
                                String[]{ Manifest.permission. WRITE_EXTERNAL_STORAGE }, 1);
                    } else {
                        openAlbum();
                        isAdded = false;
                        Log.d("TAG", "onItemClick: size=" + mDatas.size()+ " positon="+position+" path="+path);
                        Toast.makeText(PublishActivity.this, "您点击了添加", Toast.LENGTH_SHORT).show();
                    }
                }
                Log.d("TAG", "onItemClick: position="+position+" listcount="+parent.getCount());
            }
        });
    }

        private void parseJSONWithJSONObject_Content(String jsonData) {
            try {
                JSONObject jsonObject = new JSONObject(jsonData);
                content_status = jsonObject.getString("status");
                String data = jsonObject.getString("data");
                JSONObject jsonObject1 = new JSONObject(data);
                content_id = jsonObject1.getString("Id");
                Log.d("TAG", "status =" + content_status+" Id="+content_id);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    private void parseJSONWithJSONObject(String jsonData) {
        try {
                JSONObject jsonObject = new JSONObject(jsonData);
                status_pic = jsonObject.getString("status");
                msg_pic = jsonObject.getString("msg");
                Log.d("TAG", "status is " + status_pic);
                Log.d("TAG", "msg is " + msg_pic);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume(){
        if(isFirst){
            isFirst = false;
        }else {
            if(!isAdded && path != null){
                mDatas.add(path);
                isAdded = true;
            }else {
                pathIsNull = true;
            }
        }
        if(mDatas.size()>0){
            Log.d("TAG", "onResume size=" + mDatas.size()+ " positon="+mPosition+" path="+path+" get=");
        }
        Log.d("TAG", "onResume datasize=" + mDatas.size()+ " positon="+mPosition+" path="+path);
        super.onResume();
        adapter=new GridViewAdapter(PublishActivity.this, mDatas,GridViewAdapter.TYPE_PUB);
        mGridView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        path = null;
    }


    private void initDatas() {
        mDatas=new ArrayList<>();
    }

    private void openAlbum() {
        final Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO); // 打开相册
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.
                        PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(this, "You denied the permission",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        switch (requestCode) {
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    // 判断手机系统版本号
                    if (Build.VERSION.SDK_INT >= 19) {
                        // 4.4及以上系统使用这个方法处理图片
                        handleImageOnKitKat(data);
                    } else {
                        // 4.4以下系统使用这个方法处理图片
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
            default:
                break;
        }
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        Log.i("MainActivity", "Thread ID 2 : "+Thread.currentThread());
        if (DocumentsContract.isDocumentUri(this, uri)) {
            // 如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1]; // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                path = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.
                    getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                path = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是content类型的Uri，则使用普通方式处理
            path = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的Uri，直接获取图片路径即可
            path = uri.getPath();
        }
//        displayImage(imagePath); // 根据图片路径显示图片
    }

    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
//        String imagePath = getImagePath(uri, null);
        path = getImagePath(uri, null);
//        displayImage(imagePath);
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.
                        Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }
}


