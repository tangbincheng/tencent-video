package com.zhaizhishe.renting.flutter_tx_record;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FlutterTxRecordReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("aaa", "我收到信息了");
        String action = intent.getStringExtra("action");
        if (action.equals("upload")) {
            String videoURL = intent.getStringExtra("videoURL");
            String coverURL = intent.getStringExtra("coverURL");
            String videoId = intent.getStringExtra("videoId");
            int retCode = intent.getIntExtra("retCode", 0);
            String descMsg = intent.getStringExtra("descMsg");
            if (PluginUitls.result != null) {
                Map<String, Object> map = new HashMap<>();
                map.put("videoURL", videoURL);
                map.put("coverURL", coverURL);
                map.put("videoId", videoId);
                map.put("retCode", retCode);
                map.put("descMsg", descMsg);
                map.put("action", action);
                PluginUitls.result.success(map);
            }

        }
//        intent.putExtra("action","upload");
//        intent.putExtra("videoURL",result.videoURL);
//        intent.putExtra("coverURL",result.coverURL);
//        intent.putExtra("videoId",result.videoId);
//        intent.putExtra("retCode",result.retCode);
//        intent.putExtra("descMsg",result.descMsg);


    }


}
