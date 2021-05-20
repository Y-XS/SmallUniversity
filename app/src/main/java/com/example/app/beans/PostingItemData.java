package com.example.app.beans;

import java.util.ArrayList;
import java.util.List;

//从服务器获取的帖子信息类
public class PostingItemData {
    private String status,msg;
    private List<ItemData> data;

    public void setStatus(String status){this.status=status;}
    public String getStatus(){return status;}

    public void setMsg(String msg){this.msg=msg;}
    public String getMsg(){return msg;}

    public void setData(List<ItemData> data){this.data=data;}
    public List<ItemData> getData(){return data;}

    public class ItemData{
        private String Id,Title,Name,Time,Type,Msg;
        private List<Pics> Pic;
        private userHeadImg Face;

        public void setId(String id){this.Id=id;}
        public String getId(){return Id;}
        public void setTitle(String title){this.Title=title;}
        public String getTitle(){return Title;}
        public void setName(String name){this.Name=name;}
        public String getName(){return Name;}
        public void setTime(String time){this.Time=time;}
        public String getTime(){return Time;}
        public void setType(String type){this.Type=type;}
        public String getType(){return Type;}
        public void setMsg(String msg){this.Msg=msg;}
        public String getMsg(){return Msg;}
        public void setPic(List<Pics> pics){this.Pic=pics;}
        public List<Pics> getPic(){return Pic;}
        public void setFace(userHeadImg face){this.Face=face;}
        public userHeadImg getFace(){return this.Face;}
    }
    public class userHeadImg{
        private String Id,Url;
        public void setId(String id){this.Id=id;}
        public String getId(){return Id;}
        public void setUrl(String Url){this.Url=Url;}
        public String getUrl(){return Url;}
    }

    public class Pics{
        String Id,Url;

        public void setId(String id){this.Id=id;}
        public String getId(){return Id;}

        public void setUrl(String Url){this.Url=Url;}
        public String getUrl(){return Url;}
    }
}
