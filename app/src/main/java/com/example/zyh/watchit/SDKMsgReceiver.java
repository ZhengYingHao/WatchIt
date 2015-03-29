package com.example.zyh.watchit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.zyh.watchit.service.MyIntentService;
import com.example.zyh.watchit.ui.ShowFaceActivity;
import com.sina.push.MPSConsts;
import com.sina.push.model.ActionResult;
import com.sina.push.response.PushDataPacket;
import com.sina.push.service.message.GdidServiceMsg;


public class SDKMsgReceiver extends BroadcastReceiver {

    public static final String TAG = "SDK msg receiver";

    public SDKMsgReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int msg_type = intent.getIntExtra(MPSConsts.CMD_ACTION, -1);

        switch (msg_type) {

            case MPSConsts.MSG_TYPE_GET_GDID:

                Bundle msgBundle = intent.getBundleExtra(MPSConsts.KEY_MSG_GDID);

                GdidServiceMsg msg = new GdidServiceMsg();

                msg = (GdidServiceMsg) msg.parserFromBundle(msgBundle);


                Toast.makeText(context, msg.getGdid(), Toast.LENGTH_LONG).show();

                Log.i(TAG, "get gdid.");
                break;
            case MPSConsts.MSG_TYPE_MPS_PUSH_DATA:

                PushDataPacket packet = intent
                        .getParcelableExtra(MPSConsts.KEY_MSG_MPS_PUSH_DATA);

                Toast.makeText(context, "onPush data: " + packet.getSrcJson(),
                        Toast.LENGTH_LONG).show();

                Log.i(TAG, "push data.");
                break;
            case MPSConsts.MSG_CHANNEL_HAS_BEEN_BUILDED:

                ActionResult actResult = intent
                        .getParcelableExtra(MPSConsts.KEY_MSG_ACTION_SWITCH_CHANNEL);

                if (actResult.getResultCode() == 1) {
                    // 打开通道成功，可以正常接收Push和调用接口功能
                    Log.i(TAG, "open channel success.");
                }

                Log.i(TAG, "build channel.");
                break;

            case MPSConsts.MSG_TYPE_SAE_DATA:
                String aid = intent.getStringExtra(MPSConsts.KEY_MSG_SAE_DATA);
                ShowFaceActivity.setAidTextView(aid);
                MyIntentService.startActionSendAid(context, aid);
                break;
        }

    }
}
