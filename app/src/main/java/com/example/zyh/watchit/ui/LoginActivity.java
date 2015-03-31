package com.example.zyh.watchit.ui;

import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zyh.watchit.HttpUtil;
import com.example.zyh.watchit.R;

import java.util.Objects;


public class LoginActivity extends Activity implements View.OnClickListener{

    public static final String TAG = "LoginActivity";
    private static final String USER= "user";
    private static final String PASSWORD = "password";
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

        //暂时直接登录
//        ShowFaceActivity.startShowFaceActivity(LoginActivity.this);
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
        String name, pd;
        switch (v.getId()) {
            case R.id.loginBtn:
                name = userNameText.getText().toString();
                pd = passwordText.getText().toString();

                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(pd)) {
                    hintTv.setVisibility(View.VISIBLE);
                } else if (isHaveLogin(name, pd)) {
                    ShowFaceActivity.startShowFaceActivity(LoginActivity.this);
                } else {
                    AsyncTask asyncTask = new AsyncTask<Object, Void, Boolean>() {
                        @Override
                        protected Boolean doInBackground(Object... params) {
                            try {
                                String path = (String)params[0];
                                String name = (String)params[1];
                                String pd = (String)params[2];
                                String result = HttpUtil.isUser(path, name, pd);
                                Log.i(TAG, "get nameCheck result: " + result);
                                if ("true".equals(result)) {
                                    storageInSharedPreference(name, pd);
                                    ShowFaceActivity.startShowFaceActivity(LoginActivity.this);
                                    return true;
                                } else {
                                    return false;
                                }
                            } catch (NetworkErrorException e) {
                                Log.i(TAG, "register error.");
                                return false;
                            }
                        }

                        @Override
                        protected void onPostExecute(Boolean aBoolean) {
                            if (aBoolean) hintTv.setVisibility(View.INVISIBLE);
                            else hintTv.setVisibility(View.VISIBLE);
                        }
                    };
                    asyncTask.execute(getString(R.string.checkUserUrl), name, pd);
                }
                break;
            case R.id.registerBtn:
                name = userNameText.getText().toString();
                pd = passwordText.getText().toString();

                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(pd)) {
                    hintTv.setVisibility(View.VISIBLE);
                } else {
                    AsyncTask asyncTask = new AsyncTask<Object, Void, Boolean>() {
                        @Override
                        protected Boolean doInBackground(Object... params) {
                            try {
                                String path = (String)params[0];
                                String name = (String)params[1];
                                String pd = (String)params[2];
                                String result = HttpUtil.register(path, name, pd);
                                if ("success".equals(result)) {
                                    storageInSharedPreference(name, pd);
                                    ShowFaceActivity.startShowFaceActivity(LoginActivity.this);
                                    return true;
                                } else {
                                    return false;
                                }
                            } catch (NetworkErrorException e) {
                                Log.i(TAG, "register error.");
                            }
                            return true;
                        }
                        @Override
                        protected void onPostExecute(Boolean aBoolean) {
                            if (aBoolean)
                                Toast.makeText(LoginActivity.this, "register success.",
                                        Toast.LENGTH_LONG).show();
                        }
                    };
                    asyncTask.execute(getString(R.string.registerUrl), name, pd);
                }
                break;
        }
    }

    private boolean isHaveLogin(String name, String pd) {
        SharedPreferences sp = getSharedPreferences(SPNAME, MODE_PRIVATE);
        String spName = sp.getString(USER, "");
        String spPassword = sp.getString(PASSWORD, "");
        return spName.equals(name) && spPassword.equals(pd);
    }


    private void storageInSharedPreference(String name, String pd) {
        SharedPreferences.Editor editor = getSharedPreferences(SPNAME, MODE_PRIVATE).edit();
        editor.putString(USER, name);
        editor.putString(PASSWORD, pd);
        editor.apply();
    }


    @Override
    protected void onResume() {
        super.onResume();

    }


}
