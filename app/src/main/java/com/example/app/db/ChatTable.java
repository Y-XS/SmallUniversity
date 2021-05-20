package com.example.app.db;

import org.litepal.crud.LitePalSupport;

public class ChatTable extends LitePalSupport {
    public static int TYPE_SEND=0,TYPE_RECV=1;
    private int type,chatId;
    private String userId,anotherId,userName,anotherName,content,time;
    private boolean isLast;

    public int getId() {
        return chatId;
    }

    public void setId(int id) {
        this.chatId = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAnotherName() {
        return anotherName;
    }

    public void setAnotherName(String anotherName) {
        this.anotherName = anotherName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAnotherId() {
        return anotherId;
    }

    public void setAnotherId(String anotherId) {
        this.anotherId = anotherId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isLast() {
        return isLast;
    }

    public void setLast(boolean last) {
        isLast = last;
    }
}
