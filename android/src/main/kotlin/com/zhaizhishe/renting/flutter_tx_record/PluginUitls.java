package com.zhaizhishe.renting.flutter_tx_record;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import io.flutter.plugin.common.MethodChannel;

public class PluginUitls {
    private Context context;
    boolean isRegister = false;
    FlutterTxRecordReceiver flutterTxRecordReceiver;
    public static MethodChannel.Result result;

    public PluginUitls(MethodChannel.Result myResult) {
        result = myResult;

    }

    public void init(Context context, String licenceUrl, String licenseKey) {
        this.context = context;
        initReceiver();
        Intent intent = new Intent("com.zhaizhishe.TxRecord");
        intent.putExtra("action", "init");
        intent.putExtra("licenceUrl", licenceUrl);
        intent.putExtra("licenseKey", licenseKey);
        context.sendBroadcast(intent);
        Log.e("aaa", "flutter发送广播成功");
    }

    public void startRecord() {
        Intent intent = new Intent("com.zhaizhishe.TxRecord");
        intent.putExtra("action", "startRecord");
//        intent.putExtra("licenceUrl",licenceUrl);
//        intent.putExtra("licenseKey",licenseKey);
        context.sendBroadcast(intent);

    }

    public void chooseVideo() {
        Intent intent = new Intent("com.zhaizhishe.TxRecord");
        intent.putExtra("action", "chooseVideo");
//        intent.putExtra("licenceUrl",licenceUrl);
//        intent.putExtra("licenseKey",licenseKey);
        context.sendBroadcast(intent);
    }


    private void initReceiver() {
        if (isRegister) return;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.zhaizhishe.flutterTxRecord");  //action中的值是要监听的系统广播
        flutterTxRecordReceiver = new FlutterTxRecordReceiver();
        context.registerReceiver(flutterTxRecordReceiver, intentFilter);
        isRegister = true;
    }

    public void unRegister() {
        isRegister = false;
        result = null;
        context.unregisterReceiver(flutterTxRecordReceiver);

    }
}
