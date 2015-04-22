package com.example.zyh.watchit;


public class UserInfo {
    private static UserInfo userInfo = new UserInfo();

    public static final String USER_IN_SERVER = "server";
    public static final String USER_IN_CLIENT = "client";

    public static final String NAME = "name";
    public static final String PASSWORD = "password";
    public static final String USER_ID = "userId";

    private String name;
    private String userId;
    private String state;

    private UserInfo() {}

    public static UserInfo getUserInfo() { return userInfo; }

    public synchronized void setUserId(String id) { this.userId = id; }
    public String getUserId() { return userId; }

    public synchronized void setState(String state) { this.state = state; }
    public String getState() { return state; }

    public synchronized void setName(String name) { this.name = name; }
    public String getName() { return name; }

    public void clear() { userId = null; name = null; state = null; }
}
