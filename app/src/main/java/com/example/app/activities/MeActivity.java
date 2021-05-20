package com.example.app.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.app.beans.Address;
import com.example.app.beans.RLInfo;
import com.example.app.utils.HttpUtil;
import com.example.app.utils.MyApplication;
import com.example.app.utils.SocketUtil;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

import static com.example.app.activities.PublishActivity.CHOOSE_PHOTO;

public class MeActivity extends BaseActivity {

    private MyApplication myApplication;
    private RLInfo userInfo;

    private ImageButton btn_back;
    private Button btn_logout,btn_contact;
    private TextView userName;
    private ImageView userHeadImg;

    private String netAddr = "http://1.15.115.24/uploadPic";
    private String url_login = "http://1.15.115.24/login";
    private String url_change = "http://1.15.115.24/change";
    private Address addr;

    private String imgPath;
    private SocketUtil mSocket;
    AlertDialog.Builder inputDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me);

        myApplication = (MyApplication)(this.getApplication());
        userInfo = myApplication.getLoginUser();
        mSocket = myApplication.getSocket();

        btn_back = (ImageButton)findViewById(R.id.btn_back_me);
        btn_logout = (Button)findViewById(R.id.logout);
        btn_contact = (Button)findViewById(R.id.me_btn_contact_us);
        userName = (TextView)findViewById(R.id.me_username);
        userHeadImg = (ImageView)findViewById(R.id.me_userheadimg);

        //设置用户名和头像
        userName.setText(myApplication.getLoginUser().getInfo().getName());
        if(userInfo.getInfo()!=null){
            Glide.with(this).load(userInfo.getInfo().getPic().getUrl()).centerCrop().into(userHeadImg);
        }

        //对话框
        final AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setTitle("提示")
                .setMessage("确认更换头像吗");
        ab.setPositiveButton("确认",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (ContextCompat.checkSelfPermission(MeActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.
                        PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MeActivity.this, new
                            String[]{ Manifest.permission. WRITE_EXTERNAL_STORAGE }, 1);
                } else {
                    openAlbum();
                }
            }

        });
        ab.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final AlertDialog.Builder confirm_logout = new AlertDialog.Builder(this);
        confirm_logout.setTitle("提示")
                .setMessage("确认注销吗？");
        confirm_logout.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences account_remember = getSharedPreferences("data", MODE_PRIVATE);
                SharedPreferences.Editor editor = account_remember.edit();
                editor.clear();
                editor.commit();
                ActivityCollector.finishOneActivity(MainActivity.class.getName());
                Log.d("MeActivity", "onClick: ===================注销");
                Intent intent = new Intent(MeActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });
        confirm_logout.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final EditText editText = new EditText(MeActivity.this);
        inputDialog = new AlertDialog.Builder(MeActivity.this);
        inputDialog.setTitle("输入用户名").setView(editText);
        inputDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        changeUserName(editText.getText().toString());
                    }
                });
        inputDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        /**
        * 点击事件
        * */
        //返回
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MeActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        //更改用户名
        userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputDialog.show();
            }
        });
        userHeadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ab.show();
            }
        });

        btn_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactUs();
            }
        });

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm_logout.show();
            }
        });

    }

    @Override
    protected void onResume(){
        super.onResume();
        Glide.with(this).load(userInfo.getInfo().getPic().getUrl()).centerCrop().into(userHeadImg);
        if(imgPath!=null){
            changeUserHeadImg();
            Glide.with(this).load(imgPath).centerCrop().into(userHeadImg);
        }else {
            Log.d("TAG", "onResume: no selected");
        }
    }

    private void changeUserName(String name){
        //更换用户名
        addr = new Address(url_change);
        addr.add(true,"id",userInfo.getInfo().getId())
                .add(false,"name",name)
                .add(false,"school",userInfo.getInfo().getSchool())
                .add(false,"password",userInfo.getInfo().getPassword());
        String url = addr.getAddress();
        HttpUtil.sendOkHttpRequest(url,new okhttp3.Callback(){
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                //重新登录
                try {
                    addr = new Address(url_login);
                    addr.add(true,"id",userInfo.getInfo().getId())
                            .add(false,"password",userInfo.getInfo().getPassword());
                    String url = addr.getAddress();
                    HttpUtil.sendOkHttpRequest(url, new okhttp3.Callback(){
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String responseData = response.body().string();
                            Log.d("Login-> ", "onResponse: "+responseData);

                            Gson gson = new Gson();
                            RLInfo info = gson.fromJson(responseData, RLInfo.class);
                            myApplication.userLogin(info);
                            userInfo = myApplication.getLoginUser();
                            userName.setText(userInfo.getInfo().getName());
                        }
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
            }
        });
    }
    private void changeUserHeadImg(){
        //更换用户头像
        Log.d("TAG", "changeUserHeadImg: "+userInfo.getInfo().getId());
        HttpUtil.upImg(netAddr,imgPath, userInfo.getInfo().getId(),userInfo.getToken(),new okhttp3.Callback(){

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseData = response.body().string();

                try {
                    addr = new Address(url_login);
                    addr.add(true,"id",userInfo.getInfo().getId())
                            .add(false,"password",userInfo.getInfo().getPassword());
                    String url = addr.getAddress();
                    HttpUtil.sendOkHttpRequest(url, new okhttp3.Callback(){

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String responseData = response.body().string();
                            Log.d("Login-> ", "onResponse: "+responseData);

                            Gson gson = new Gson();
                            RLInfo info = gson.fromJson(responseData, RLInfo.class);
                            myApplication.userLogin(info);
                        }
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.d("changeHeadImg", "onResponse: "+responseData);
            }
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
            }
        });
    }
    private void contactUs(){
        //联系我们
        final AlertDialog.Builder contact_dlg = new AlertDialog.Builder(this);
        contact_dlg.setTitle("联系我们")
                .setMessage("e-mail:xys612r@163.com");
        contact_dlg.setNegativeButton("关闭", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        contact_dlg.show();
    }

    /**
     * 选择照片======================================================↓
     */
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
                imgPath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.
                    getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imgPath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是content类型的Uri，则使用普通方式处理
            imgPath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的Uri，直接获取图片路径即可
            imgPath = uri.getPath();
        }
//        displayImage(imagePath); // 根据图片路径显示图片
    }

    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
//        String imagePath = getImagePath(uri, null);
        imgPath = getImagePath(uri, null);
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
