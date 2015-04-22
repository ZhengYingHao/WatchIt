package com.example.zyh.watchit.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.zyh.watchit.HttpUtil;
import com.example.zyh.watchit.R;
import com.example.zyh.watchit.UserInfo;
import com.ta.TAActivity;
import com.ta.util.http.AsyncHttpClient;
import com.ta.util.http.AsyncHttpResponseHandler;
import com.ta.util.http.RequestParams;


public class LoginActivity extends TAActivity implements View.OnClickListener{

    public static final String TAG = "LoginActivity";
    private static final String SPNAME = "user";

    private EditText userNameText, passwordText;
    private Button loginBtn, registerBtn;
    private TextView hintTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        init();

        loginBtn.setOnClickListener(this);
        registerBtn.setOnClickListener(this);

        loadUserInfo();
    }

    private void init() {
        userNameText = (EditText)findViewById(R.id.userName);
        passwordText = (EditText)findViewById(R.id.password);
        loginBtn = (Button)findViewById(R.id.loginBtn);
        registerBtn = (Button)findViewById(R.id.registerBtn);
        hintTv = (TextView)findViewById(R.id.hintTv);
    }

    @Override
    public void onClick(View v) {
        String name = userNameText.getText().toString();
        String pd = passwordText.getText().toString();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(pd)) {
            hintTv.setVisibility(View.VISIBLE);
            return ;
        }

        RequestParams params = new RequestParams();
        params.put(UserInfo.NAME, name);
        params.put(UserInfo.PASSWORD, pd);

        switch (v.getId()) {
            case R.id.loginBtn:
                if (hasLogin()) {
                    ShowFaceActivity.start(LoginActivity.this);
                } else {
                    AsyncHttpClient client = new AsyncHttpClient();
                    client.post(getString(R.string.checkUserUrl), params,
                            new UserDataAsyncHttpResponseHandler(name));
                }
                break;
            case R.id.registerBtn:
                AsyncHttpClient client = new AsyncHttpClient();
                client.post(getString(R.string.registerUrl), params,
                        new UserDataAsyncHttpResponseHandler(name));
                break;
        }
    }

    /**
     * 用户信息不为空则已登录。
     */
    private boolean hasLogin() {
        return !UserInfo.getUserInfo().getName().equals("");
    }

    /**
     * 加载存储在user(SharedPreference)的用户信息。
     */
    private synchronized void loadUserInfo() {
        SharedPreferences sp = getSharedPreferences(SPNAME, MODE_PRIVATE);
        String name = sp.getString(UserInfo.NAME, "");
        String id = sp.getString(UserInfo.USER_ID, "");
        UserInfo.getUserInfo().setName(name);
        UserInfo.getUserInfo().setUserId(id);
    }


    /**
     * 在名子为 user 的 sharedPreference 中记录用户信息，用于检测是否已登录，免得每次都要用户登录。
     */
    private synchronized void storageInSharedPreference(String name, String userId) {
        SharedPreferences.Editor editor = getSharedPreferences(SPNAME, MODE_PRIVATE).edit();
        editor.putString(UserInfo.NAME, name);
        editor.putString(UserInfo.USER_ID, userId);
        editor.apply();
    }

//    private class MyAsyncTask extends AsyncTask<Object, Void, Boolean> {
//        @Override
//        protected Boolean doInBackground(Object... params) {
//            try {
//                String path = (String) params[0];
//                String name = (String) params[1];
//                String pd = (String) params[2];
//                String result = HttpUtil.isUser(path, name, pd);
//                Log.i(TAG, "get nameCheck result: " + result);
//                if ("true".equals(result)) {
//                    storageInSharedPreference(name, pd);
//                    UserInfo.getUserInfo().setName(name);
//                    return true;
//                } else {
//                    return false;
//                }
//            } catch (NetworkErrorException e) {
//                Log.i(TAG, "net error.");
//                return false;
//            }
//        }
//
//        protected void onPostExecute(Boolean aBoolean) {
//            if (aBoolean) {
//                hintTv.setVisibility(View.INVISIBLE);
//                MainActivity.start(LoginActivity.this);
//            }
//            else hintTv.setVisibility(View.VISIBLE);
//        }
//    }


    /**
     * 异步处理从网站返回的信息。
     */
    private class UserDataAsyncHttpResponseHandler extends AsyncHttpResponseHandler {
        private String name;
        public UserDataAsyncHttpResponseHandler(String name) {
            super();
            this.name = name;
        }
        @Override
        public void onSuccess(String content) {
            super.onSuccess(content);
            Log.i(TAG, content);
            if (!HttpUtil.WRONG_USER_NAME_OR_PASSWORD_INFO.equals(content)) {
                storageInSharedPreference(name, content);

                setUserInfo(name, content);

                hintTv.setVisibility(View.INVISIBLE);
                Log.i(TAG, "ready to MainActivity.");
                ShowFaceActivity.start(LoginActivity.this);
                Log.i(TAG, "跳转到 MainActivity.");
            } else {
                hintTv.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 设置用户信息
     * @param name 用户名
     * @param userId 用户id
     */
    private void setUserInfo(String name, String userId) {
        UserInfo.getUserInfo().setName(name);
        UserInfo.getUserInfo().setUserId(userId);
    }

}







//
//public class LoginActivity extends TAActivity implements View.OnClickListener{
//
//    public static final String TAG = "LoginActivity";
//    private static final String USER = "user";
//    private static final String PASSWORD = "password";
//    // sharePreference's name
//    private static final String SPNAME = "user";
//
//    private EditText userNameText, passwordText;
//    private Button loginBtn, registerBtn;
//    private TextView hintTv;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setContentView(R.layout.activity_login);
//
//        localLogin();
//
//        init();
//
//        loginBtn.setOnClickListener(this);
//        registerBtn.setOnClickListener(this);
//
//    }
//
//    private void init() {
//        userNameText = (EditText)findViewById(R.id.userName);
//        passwordText = (EditText)findViewById(R.id.password);
//        loginBtn = (Button)findViewById(R.id.loginBtn);
//        registerBtn = (Button)findViewById(R.id.registerBtn);
//        hintTv = (TextView)findViewById(R.id.hintTv);
//    }
//
//    @Override
//    public void onClick(View v) {
//        String name, pd;
//        name = userNameText.getText().toString();
//        pd = passwordText.getText().toString();
//        switch (v.getId()) {
//            case R.id.loginBtn:
//                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(pd)) {
//                    hintTv.setVisibility(View.VISIBLE);
//                } else {
//                    AsyncTask asyncTask = new AsyncTask<Object, Void, Boolean>() {
//                        @Override
//                        protected Boolean doInBackground(Object... params) {
//                            try {
//                                String path = (String)params[0];
//                                String name = (String)params[1];
//                                String pd = (String)params[2];
//                                String result = HttpUtil.isUser(path, name, pd);
//                                Log.i(TAG, "get nameCheck result: " + result);
//                                if (!"false".equals(result)) {
//                                    storageInSharedPreference(result, pd);
//                                    //返回id
//                                    UserInfo.getUserInfo().setId(result);
//                                    ShowFaceActivity.startShowFaceActivity(LoginActivity.this);
//                                    return true;
//                                } else {
//                                    return false;
//                                }
//                            } catch (NetworkErrorException e) {
//                                Log.i(TAG, "register error.");
//                                return false;
//                            }
//                        }
//
//                        @Override
//                        protected void onPostExecute(Boolean aBoolean) {
//                            if (aBoolean) hintTv.setVisibility(View.INVISIBLE);
//                            else hintTv.setVisibility(View.VISIBLE);
//                        }
//                    };
//                    asyncTask.execute(getString(R.string.checkUserUrl), name, pd);
//                }
//                break;
//            case R.id.registerBtn:
//                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(pd)) {
//                    hintTv.setVisibility(View.VISIBLE);
//                } else {
//                    AsyncTask asyncTask = new AsyncTask<Object, Void, Boolean>() {
//                        @Override
//                        protected Boolean doInBackground(Object... params) {
//                            try {
//                                String path = (String)params[0];
//                                String name = (String)params[1];
//                                String pd = (String)params[2];
//                                String result = HttpUtil.register(path, name, pd);
//                                if (!"false".equals(result)) {
//                                    storageInSharedPreference(result, pd);
//                                    UserInfo.getUserInfo().setId(result);
//                                    ShowFaceActivity.startShowFaceActivity(LoginActivity.this);
//                                    return true;
//                                } else {
//                                    return false;
//                                }
//                            } catch (NetworkErrorException e) {
//                                Log.i(TAG, "register error.");
//                            }
//                            return true;
//                        }
//                        @Override
//                        protected void onPostExecute(Boolean aBoolean) {
//                            if (aBoolean) hintTv.setVisibility(View.INVISIBLE);
//                            else hintTv.setVisibility(View.VISIBLE);
//                        }
//                    };
//                    asyncTask.execute(getString(R.string.registerUrl), name, pd);
//                }
//                break;
//        }
//    }
//
//    private void localLogin() {
//        SharedPreferences sp = getSharedPreferences(SPNAME, MODE_PRIVATE);
//        String id = sp.getString(USER, "");
//        String password = sp.getString(PASSWORD, "");
//        if (!TextUtils.isEmpty(id) && !TextUtils.isEmpty(password)) {
//            UserInfo.getUserInfo().setId(id);
//            ShowFaceActivity.startShowFaceActivity(LoginActivity.this);
//        }
//    }
//
//
//    private void storageInSharedPreference(String id, String pd) {
//        SharedPreferences.Editor editor = getSharedPreferences(SPNAME, MODE_PRIVATE).edit();
//        editor.putString(USER, id);
//        editor.putString(PASSWORD, pd);
//        editor.apply();
//    }
//
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//
//    }
//
//
//}
