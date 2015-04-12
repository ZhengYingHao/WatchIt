package com.example.zyh.watchit;


public class UserInfo {
    private static UserInfo userInfo = new UserInfo();

    public static final String USER_IN_SERVER = "client";
    public static final String USER_IN_CLIENT = "client";

    private String id;
    private String state;

    private UserInfo() {}

    public static synchronized UserInfo getUserInfo() { return userInfo; }

    public synchronized void setId(String id) { this.id = id; }
    public synchronized String getId() { return id; }

    public synchronized void setState(String state) { this.state = state; }
    public synchronized String getState() { return state; }

    public void clear() { id = null; state = null; }
}
