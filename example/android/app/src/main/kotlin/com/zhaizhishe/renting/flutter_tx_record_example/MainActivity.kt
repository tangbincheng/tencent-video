package com.zhaizhishe.renting.flutter_tx_record_example

import android.os.Bundle
import io.flutter.embedding.android.FlutterActivity
import io.flutter.plugin.common.MethodChannel

class MainActivity: FlutterActivity() {
    lateinit var  methodUtil: MethodUtil
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        methodUtil=MethodUtil()
        methodUtil.init(this)
    }

    override fun onDestroy() {
        methodUtil.unregister(this)
        super.onDestroy()
    }
}
