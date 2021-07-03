package com.zhaizhishe.renting.flutter_tx_record

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.StrictMode
import androidx.annotation.NonNull

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar

/** FlutterTxRecordPlugin */
class FlutterTxRecordPlugin: FlutterPlugin, MethodCallHandler,ActivityAware {
  override fun onDetachedFromActivity() {
    pluginUitls?.unRegister()
  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {

  }

  override fun onDetachedFromActivityForConfigChanges() {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private lateinit var channel : MethodChannel
  lateinit var context: Context
  lateinit var pluginUitls: PluginUitls
  lateinit var myResult: Result

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "flutter_tx_record")
    channel.setMethodCallHandler(this)
    context=flutterPluginBinding.applicationContext
  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    myResult=result
    if (call.method == "getPlatformVersion") {
      result.success("Android ${android.os.Build.VERSION.RELEASE}")
    }else if(call.method=="init"){
      pluginUitls=PluginUitls(result)
      pluginUitls.init(context,call.argument("licenceUrl"),call.argument("licenseKey"))
    }else if(call.method=="startRecord"){
      pluginUitls?.startRecord()
    }else if(call.method=="chooseVideo"){
      pluginUitls?.chooseVideo()
    }

    else {
      result.notImplemented()
    }
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }
}
