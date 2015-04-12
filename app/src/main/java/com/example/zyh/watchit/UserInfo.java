package com.example.zyh.watchit;


public class UserInfo {
    private static UserInfo userInfo = new UserInfo();

    private String name;

    private UserInfo() {}

    public static UserInfo getUserInfo() { return userInfo; }

    public void setName(String name) { this.name = name; }
    public String getName() { return name; }

}
