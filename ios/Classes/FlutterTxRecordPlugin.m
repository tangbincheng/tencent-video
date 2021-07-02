#import "FlutterTxRecordPlugin.h"
#if __has_include(<flutter_tx_record/flutter_tx_record-Swift.h>)
#import <flutter_tx_record/flutter_tx_record-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "flutter_tx_record-Swift.h"
#endif

@implementation FlutterTxRecordPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftFlutterTxRecordPlugin registerWithRegistrar:registrar];
}
@end
