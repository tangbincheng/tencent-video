import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:flutter_tx_record/flutter_tx_record.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    try {
      platformVersion = await FlutterTxRecord.platformVersion;
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Column(
          children: [
            TextButton(
              onPressed: () {
                FlutterTxRecord.init(
                    'http://license.vod2.myqcloud.com/license/v1/78e40e49a6e6469f04f32f2cfc8ef4d2/TXUgcSDK.licence',
                    'ffa77ecf56b2145297961e624f39808b');
              },
              child: Text('初始化'),
            ),
            TextButton(
              onPressed: () {
                FlutterTxRecord.startRecord().then((value){
                  print('我收到录制视频回调:$value');
                });
              },
              child: Text('开始录制'),
            ),
            TextButton(
              onPressed: () {
                FlutterTxRecord.chooseVideo();
              },
              child: Text('选择视频'),
            ),
          ],
        ),
      ),
    );
  }
}
