package com.zhaizhishe.renting.flutter_tx_record_example;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;

import com.tencent.liteav.demo.videoediter.TCVideoPickerActivity;
import com.tencent.liteav.demo.videorecord.TCVideoRecordActivity;
import com.tencent.liteav.demo.videouploader.ui.TCCompressActivity;
import com.tencent.liteav.demo.videouploader.ui.TCVideoPublishActivity;
import com.tencent.liteav.demo.videouploader.ui.utils.Constants;
import com.tencent.qcloud.ugckit.UGCKit;
import com.tencent.rtmp.TXLiveBase;
import com.tencent.ugc.TXUGCBase;

public class TxRecordReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("aaa","我是主项目的广播");
        String action= intent.getStringExtra("action");
        Log.e("aaa","action="+action);
        if(action.equals("init")){
            String licenceUrl=intent.getStringExtra("licenceUrl");
            String licenseKey=intent.getStringExtra("licenseKey");
            TXLiveBase.setConsoleEnabled(true);
//                   initBugly();
            UGCKit.init(context);
            TXLiveBase.getInstance().setLicence(context, licenceUrl, licenseKey);

            // 短视频licence设置
            TXUGCBase.getInstance().setLicence(context, licenceUrl, licenseKey);
            UGCKit.init(context);
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                builder.detectFileUriExposure();
            }
//            closeAndroidPDialog();
        }else if(action.equals("startRecord")){
            Intent intentActivity=new Intent(context, TCVideoRecordActivity.class);
            context.startActivity(intentActivity);
        }else if(action.equals("chooseVideo")){
            Intent intentActivity=new Intent(context, TCVideoPickerActivity.class);
            context.startActivity(intentActivity);
        }else if(action.equals("upload1")){
            String videoPath=intent.getStringExtra("path");
            String mVideoSourcePath=intent.getStringExtra("path");
            Intent intentActivity=new Intent(context, TCVideoPublishActivity.class);
            intentActivity.putExtra(Constants.VIDEO_EDITER_PATH, videoPath);
            intentActivity.putExtra(Constants.VIDEO_SOURCE_PATH, mVideoSourcePath);
            context.startActivity(intentActivity);
        }

    }
}
