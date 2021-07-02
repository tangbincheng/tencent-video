package com.zhaizhishe.renting.flutter_tx_record_example;

import android.content.Context;
import android.content.IntentFilter;

import io.flutter.Log;
import io.flutter.plugin.common.MethodChannel;

public class MethodUtil {
    TxRecordReceiver recordReceiver;
    public  void init(Context context){
        recordReceiver=new TxRecordReceiver();
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction("com.zhaizhishe.TxRecord");
        context.registerReceiver(recordReceiver,intentFilter);
        Log.e("aaa","主项目初始化广播成功");

    }

    public  void unregister(Context context){
        context.unregisterReceiver(recordReceiver);

    }
}
