package com.example.app.beans;

public class MsgListItem {

    private String name,time;
    private String headImgUrl;
    private String text,userId;

    public MsgListItem(String userId, String name, String time, String headImgUrl, String text) {
        this.userId=userId;
        this.name = name;
        this.headImgUrl=headImgUrl;
        this.text=text;
        this.time=time;
    }

    public String getUserId() {
        return userId;
    }

    public String getHeadImgUrl() {
        return headImgUrl;
    }

    public String getName() {
        return name;
    }
    public String getTime(){return time;}
    public String getText(){return text;}
    public String getHeadImgId(){return headImgUrl;}

    public void setHeadImgUrl(String headImgUrl) {
        this.headImgUrl = headImgUrl;
    }
}
