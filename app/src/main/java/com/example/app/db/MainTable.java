package com.example.app.db;

import org.litepal.crud.LitePalSupport;

public class MainTable extends LitePalSupport {
    private String userId,anotherId;
    private int chatId;

    public int getId() {
        return chatId;
    }

    public void setId(int  id) {
        this.chatId = id;
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
}
