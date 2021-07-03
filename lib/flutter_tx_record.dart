import 'dart:async';

import 'package:flutter/services.dart';

class FlutterTxRecord {
  static const MethodChannel _channel =
      const MethodChannel('flutter_tx_record');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future init(String licenceUrl, String licenseKey) {
    return _channel.invokeMethod(
        'init', {'licenceUrl': licenceUrl, 'licenseKey': licenseKey});
  }

  static Future<dynamic> startRecord() {
    return _channel.invokeMethod('startRecord');
  }

  static Future<dynamic> chooseVideo() {
    return _channel.invokeMethod('chooseVideo');
  }
}
