package com.example.zyh.watchit.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.zyh.watchit.HttpUtil;
import com.example.zyh.watchit.R;
import com.example.zyh.watchit.UserInfo;
import com.example.zyh.watchit.service.TimerService;
import com.sina.push.PushManager;

public class ShowFaceActivity extends Activity implements View.OnClickListener{

    public static final String TAG = "ShowFaceActivity";

//    private static final String HINT_VISIT = "Someone at your home.";
//    private static final String HINT_NO_VISIT = "no visitor.";

    private static ImageView faceImageView;

    private static TextView hintTextView, aidTextView;

    private static PushManager pushManager = null;

    private static BitmapFactory.Options options;

    private String APPID = "22267";

    private Button reconnectBtn, getFaceBtn, quitBtn;

    private String fileName;

    public static void startShowFaceActivity(Context context) {
        UserInfo.getUserInfo().setState(UserInfo.USER_IN_CLIENT);
        Intent intent = new Intent(context, ShowFaceActivity.class);
        context.startActivity(intent);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_show_face);

        init();
        initPushManager();


        reconnectBtn.setOnClickListener(this);
        getFaceBtn.setOnClickListener(this);
        quitBtn.setOnClickListener(this);

        Log.i(TAG, "get fileName : " + fileName);
        downloadDataToBitmap(fileName);
    }

    private void init() {
        faceImageView = (ImageView)findViewById(R.id.faceImageView);
        hintTextView = (TextView)findViewById(R.id.hintTextView);
        aidTextView = (TextView)findViewById(R.id.aid);
        reconnectBtn = (Button)findViewById(R.id.reconnect);
        getFaceBtn = (Button)findViewById(R.id.getFace);
        quitBtn = (Button)findViewById(R.id.quit);
        //将 tface 改为 UserInfo.getUserInfo().getName()
        fileName = getString(R.string.faceUrl) + UserInfo.getUserInfo().getId() + ".jpeg";
    }

    private void initPushManager() {
        if (pushManager != null)
            return;
        pushManager = PushManager.getInstance(this);
        pushManager.initPushChannel(APPID, APPID, "100", "100");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reconnect:
                pushManager.refreshConnection();
//                pushManager = null;
//                initPushManager();
                break;
            case R.id.getFace:
                downloadDataToBitmap(fileName);
                break;
            case R.id.quit:
                finish();
                break;
            default:
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    public void setFaceImageView(Bitmap bitmap) {
        if (faceImageView != null)
            faceImageView.setImageBitmap(bitmap);
        Log.i(TAG, "set face view.");
    }

    public static void setHintTextView(String hint) {
        if (hintTextView != null)
            hintTextView.setText(hint);
    }

    public static void setAidTextView(String aid) {
        if (aid != null)
            aidTextView.setText(aid);
    }

    public void downloadDataToBitmap(String urlPath) {
        AsyncTask asyncTask = new AsyncTask<Object, Bitmap, Boolean>() {
            @Override
            protected Boolean doInBackground(Object... params) {
                byte[] bytes = HttpUtil.downloadData((String)params[0]);
                if (bytes == null) {
                    Log.i(TAG, "bytes is null.");
                    return false;
                }
                Log.i(TAG, "bytes length: " + bytes.length);
                Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                publishProgress(bm);


                return true;
            }

            @Override
            protected void onProgressUpdate(Bitmap... values) {
                setFaceImageView(values[0]);
            }

        };
        asyncTask.execute(urlPath);
    }

    public class TimeBroadcastReceiver extends BroadcastReceiver {

        public static final String ACTION = "com.example.zyh.watchit.timereceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            pushManager.refreshConnection();
            TimerService.startTimerService(ShowFaceActivity.this, 1000 * 60 * 60);
        }
    }



    static {
        options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inJustDecodeBounds = false;
        options.inSampleSize = 5;
    }
}
