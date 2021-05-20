package com.example.app.db;

import org.litepal.crud.LitePalSupport;

public class ChatListTable extends LitePalSupport {
    private String userId,anotherId;
    private boolean isOnline,isDelete;
    private int unReadNum,id,chatId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getChatId() {
        return chatId;
    }

    public void setChatId(int chatId) {
        this.chatId = chatId;
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

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public boolean isDelete() {
        return isDelete;
    }

    public void setDelete(boolean delete) {
        isDelete = delete;
    }

    public int getUnReadNum() {
        return unReadNum;
    }

    public void setUnReadNum(int unReadNum) {
        this.unReadNum = unReadNum;
    }
}
