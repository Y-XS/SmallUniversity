package com.example.app.utils;

import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class HttpUtil {

    public static class Response {
        public Integer code;
        public String content;
    }

    private static void proceedRequest(OkHttpClient client, Request request, Response response) {
        try {
            okhttp3.Response temp = client.newCall(request).execute();
            response.code = temp.code();
            ResponseBody body = temp.body();
            if (temp.isSuccessful()) {
                //call string auto close body
                response.content = body.string();
            } else {
                response.content = "网络请求失败";
                temp.body().close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.w("TAG", e.getMessage() == null ? " " : e.getMessage());
            response.code = -1;
            response.content = e.getMessage();
        }
    }

    public static Response httpGet(String url) {

        Response response = new Response();

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        proceedRequest(client, request, response);

        return response;
    }

    public static void sendOkHttpRequest(String address, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(4000, TimeUnit.MILLISECONDS)
                .readTimeout(4000, TimeUnit.MILLISECONDS)
                .writeTimeout(4000, TimeUnit.MILLISECONDS)
                .build();

        Request request = new Request.Builder()
                .url(address)
                .get()
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void upImg(String url, String imagePath, String id, String token, okhttp3.Callback callback) {
        Log.d("TAG", "upImg: " + imagePath);
        File file = new File(imagePath);
        MediaType MEDIA_TYPE_JPG = MediaType.parse("image/jpg");
        RequestBody filebody = MultipartBody.create(MEDIA_TYPE_JPG, file);
        MultipartBody.Builder multiBuilder = new MultipartBody.Builder();
        //这里是 封装上传图片参数
//        multiBuilder.addFormDataPart("file", file.getName(), filebody);
        //参数以添加header方式将参数封装，否则上传参数为空
        // 设置请求体
        multiBuilder.setType(MultipartBody.FORM);
        //这里是 封装上传图片参数
        multiBuilder.addFormDataPart("pic", file.getName(), filebody);
        // 封装请求参数,这里最重要
        HashMap<String, String> params = new HashMap<>();
        params.put("id", id);
        params.put("token", token);
        //参数以添加header方式将参数封装，否则上传参数为空
        if (params != null && !params.isEmpty()) {
            for (String key : params.keySet()) {
                multiBuilder.addPart(
                        Headers.of("Content-Disposition", "form-data; name=\"" + key + "\""),
                        RequestBody.create(null, params.get(key)));
            }
        }
        RequestBody multiBody = multiBuilder.build();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).post(multiBody).build();
        client.newCall(request).enqueue(callback);
    }

    public static void upImgs(String url, List<String> imgPathList, String id, String token,okhttp3.Callback callback){

        List<File> files = new ArrayList<>();
        for(String imgPath:imgPathList){
            files.add(new File(imgPath));
            Log.d("TAG", "upImg: path:"+imgPath);
        }

        OkHttpClient client=new OkHttpClient();
        MediaType MEDIA_TYPE_JPG = MediaType.parse("image/jpg");
        MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");

        //这里是 封装上传图片参数
        if(files!=null){
            for(File file:files){
                MultipartBody.Builder multiBuilder=new MultipartBody.Builder();
                //这里是 封装上传图片参数
//        multiBuilder.addFormDataPart("file", file.getName(), filebody);
                //参数以添加header方式将参数封装，否则上传参数为空
                // 设置请求体
                multiBuilder.setType(MultipartBody.FORM);

                HashMap<String, String> params = new HashMap<>();
                params.put("id",id);
                params.put("token",token);
                //参数以添加header方式将参数封装，否则上传参数为空
                if (params != null && !params.isEmpty()) {
                    for (String key : params.keySet()) {
                        multiBuilder.addPart(
                                Headers.of("Content-Disposition", "form-data; name=\"" + key + "\""),
                                RequestBody.create(null, params.get(key)));
                    }
                }
                multiBuilder.addFormDataPart("pic", file.getName(),RequestBody.create(MEDIA_TYPE_PNG,file));
                Log.d("TAG", "upImgs: file:"+file);
                // 封装请求参数,这里最重要
                RequestBody multiBody=multiBuilder.build();
                Request request=new   Request.Builder().url(url).post(multiBody).build();
                client.newCall(request).enqueue(callback);
            }
        }

    }

    public static void getData(String url, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(4000, TimeUnit.MILLISECONDS)
                .readTimeout(4000, TimeUnit.MILLISECONDS)
                .writeTimeout(4000, TimeUnit.MILLISECONDS)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        client.newCall(request).enqueue(callback);
    }
}