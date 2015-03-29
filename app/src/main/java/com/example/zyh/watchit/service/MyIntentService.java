package com.example.zyh.watchit.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import com.example.zyh.watchit.HttpUtil;
import com.example.zyh.watchit.R;


public class MyIntentService extends IntentService {

    private static final String ACTION_SENDAID = "com.example.zyh.watchit.service.action.sendaid";


    private static final String EXTRA_AID = "com.example.zyh.watchit.service.extra.aid";


    public static void startActionSendAid(Context context, String aid) {
        Intent intent = new Intent(context, MyIntentService.class);
        intent.setAction(ACTION_SENDAID);
        intent.putExtra(EXTRA_AID, aid);
        context.startService(intent);
    }


    public MyIntentService() {
        super("MyIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_SENDAID.equals(action)) {
                final String aid = intent.getStringExtra(EXTRA_AID);
                handleActionSendAid(aid);
            }
        }
    }

    private void handleActionSendAid(String aid) {
        HttpUtil.uploadString(getString(R.string.updateAid), aid);
    }

}
