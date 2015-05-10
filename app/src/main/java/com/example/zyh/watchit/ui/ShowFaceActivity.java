package com.example.zyh.watchit.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.example.zyh.watchit.R;
import com.example.zyh.watchit.UserInfo;
import com.example.zyh.watchit.service.TimerService;
import com.sina.push.PushManager;
import com.ta.TAActivity;
import com.ta.util.http.AsyncHttpClient;
import com.ta.util.http.BinaryHttpResponseHandler;

public class ShowFaceActivity extends TAActivity implements View.OnClickListener{

    public static final String TAG = "ShowFaceActivity";


    private static ImageView faceImageView;

    private static PushManager pushManager = null;

    private static final BitmapFactory.Options options = new BitmapFactory.Options();

    private static final String APPID = "22267";

    private Button reconnectBtn, getFaceBtn, quitBtn;

    private String fileName;

    public static void start(Context context) {
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
        Log.i(TAG, "have bitmap.");
    }

    private void init() {
        faceImageView = (ImageView)findViewById(R.id.faceImageView);
        reconnectBtn = (Button)findViewById(R.id.reconnect);
        getFaceBtn = (Button)findViewById(R.id.getFace);
        quitBtn = (Button)findViewById(R.id.quit);
        //将 tface 改为 UserInfo.getUserInfo().getId()
        fileName = getString(R.string.faceUrl) +
                UserInfo.getUserInfo().getUserId() + ".jpeg";
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

    /**
     * 如果服务端没有对应的jpeg文件，会导致 “channel is unrecoverably broken and will be disposed”
     * 有可能是 memory leak 导致的（stackoverflow 说的，有可能而已...），真正原因没能找到。
     *
     * 而且下载文件有问题，下载完没有显示。
     */
    public void downloadDataToBitmap(String urlPath) {
        Log.i(TAG, "download bitmap.");
        AsyncHttpClient client = new AsyncHttpClient();
        client.download(this, urlPath, new BinaryHttpResponseHandler() {
            @Override
            public void onSuccess(byte[] binaryData) {
                Bitmap bm = BitmapFactory.decodeByteArray(binaryData, 0, binaryData.length);
                Log.i(TAG, "bitmap data length : " + binaryData.length);
                setFaceImageView(bm);
            }
        });
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
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inJustDecodeBounds = false;
        options.inSampleSize = 5;
    }
}
