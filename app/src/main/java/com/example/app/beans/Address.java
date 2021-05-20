package com.example.app.beans;

public class Address {
    private String address;

    public Address(String netAddr){
        this.address = netAddr;
    }

    public Address add(Boolean isFirst, String name, String value){

        if(isFirst.equals(true)){
            address += "?"+name+"="+value;
        }else {
            address += "&"+name+"="+value;
        }
        return this;
    }

    public String getAddress() {
        return address;
    }
}
