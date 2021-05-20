package com.example.app.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//Adapter 的Bean类
public class PostingItem implements Serializable {

    private String name,time;
    private List<String> imageUrl=new ArrayList<>();
    private String  headImgUrl;
    private String text;
    private String userId;
    private int visibility;

    public PostingItem(String userId, String name, String time, String  headImgUrl, List<String> imageUrl, String text, int visibility) {
        this.userId = userId;
        this.name = name;
//        this.imageUrl = imageUrl;
        this.headImgUrl=headImgUrl;
        this.text=text;
        this.time=time;
        this.visibility = visibility;
        this.imageUrl.addAll(imageUrl);
    }
    public String getUserId(){return userId;}
    public String getName() {
        return name;
    }
    public String getTime(){return time;}
    public String getText(){return text;}
    public List<String> getImageUrl() {
        return imageUrl;
    }
    public String getHeadImgUrl(){return headImgUrl;}
    public int getVisibility(){return visibility;}

    public void setVisibility(int visibility){this.visibility = visibility;}

    public void setName(String name) {
        this.name = name;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setImageUrl(List<String> imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setHeadImgUrl(String headImgUrl) {
        this.headImgUrl = headImgUrl;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
