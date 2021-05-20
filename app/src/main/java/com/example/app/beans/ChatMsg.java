package com.example.app.beans;

public class ChatMsg {
    public static final int TYPE_RECEIVED = 1;

    public static final int TYPE_SENT = 0;

    private String content;
    private String headImgUrl;
    private String myHeadImgUrl;
    private String userName;
    private String myName;
    private int type;

    public ChatMsg(String headImgUrl, String myHeadImgUrl, String userName, String myName, String content, int type) {
        this.content = content;
        this.type = type;
        this.headImgUrl = headImgUrl;
        this.myHeadImgUrl = myHeadImgUrl;
        this.myName = myName;
        this.userName = userName;
    }

    public String getContent() {
        return content;
    }
    public String  getheadImgUrl(){
        return headImgUrl;
    }
    public String getMyHeadImgUrl(){return myHeadImgUrl;}
    public String getUserName(){return userName;}
    public String getMyName(){return myName;}
    public int getType() {
        return type;
    }
}
