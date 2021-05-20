package com.example.app.beans;

public class RLInfo {
    private String status,msg,token;
    private UserInfo data;

    public void setStatus(String status){
        this.status = status;
    }
    public String getStatus(){
        return this.status;
    }

    public void setInfo(UserInfo info){
        this.data = info;
    }
    public UserInfo getInfo(){
        return this.data;
    }

    public void setMsg(String msg){
        this.msg = msg;
    }
    public String getMsg(){
        return this.msg;
    }
    public void setToken(String token){
        this.token = token;
    }
    public String getToken(){
        return this.token;
    }

    public class UserInfo{
        String Id,Name,School,Password;
        UserHeadImg Pic;
        public void setId(String id){
            this.Id = id;
        }
        public String getId(){
            return this.Id;
        }
        public void setName(String name){
            this.Name = name;
        }
        public String getName(){
            return this.Name;
        }
        public void setSchool(String school){
            this.School = school;
        }
        public String getSchool(){
            return this.School;
        }
        public void setPassword(String password){
            this.Password = password;
        }
        public String getPassword(){
            return this.Password;
        }
        public void setPic(UserHeadImg pic){this.Pic = pic;}
        public UserHeadImg getPic(){return this.Pic;}
    }

    public class UserHeadImg{
        String Id,Url;
        public void setId(String id){this.Id = id;}
        public String getId(){return this.Id;}
        public void setUrl(String url){this.Url = url;}
        public String getUrl(){return this.Url;}
    }
}
