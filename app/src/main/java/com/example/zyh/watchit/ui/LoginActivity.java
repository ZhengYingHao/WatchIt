package com.example.zyh.watchit.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.zyh.watchit.R;


public class LoginActivity extends Activity implements View.OnClickListener{

    public static final String TAG = "LoginActivity";

    private EditText userNameText, passwordText;
    private Button loginBtn, registerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        init();

        loginBtn.setOnClickListener(this);
        registerBtn.setOnClickListener(this);

        //暂时直接登录
        ShowFaceActivity.startShowFaceActivity(LoginActivity.this);
    }

    private void init() {
        userNameText = (EditText)findViewById(R.id.userName);
        passwordText = (EditText)findViewById(R.id.password);
        loginBtn = (Button)findViewById(R.id.loginBtn);
        registerBtn = (Button)findViewById(R.id.registerBtn);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginBtn:
                if (isUserInfoCorrect(userNameText.getText().toString(),
                        passwordText.getText().toString()))
                    ShowFaceActivity.startShowFaceActivity(LoginActivity.this);
                else {
                    Toast.makeText(LoginActivity.this, "wrong userName or password.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.registerBtn:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }



    private boolean isUserInfoCorrect(String name, String password) {
        return true;
    }



}
