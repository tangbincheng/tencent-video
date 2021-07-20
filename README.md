# flutter_tx_record

A new flutter plugin project.

## Getting Started
要比较修改一下文件
example / android / settings.gradle
```
include ':app'
include ':ugckit'
include ':beautysettingkit'
include ':ugcvideoeditdemo'
include ':ugcvideorecorddemo'
include ':ugcvideojoindemo'
include ':ugcvideouploaddemo'
include ':superplayerkit'
include ':superplayerdemo'


if (!liteavSourcePath.isEmpty()) {
    apply from: new File(settingsDir, '../source_link_gradle/liteav_source_settings.gradle').getAbsolutePath()
}

if (!videoEngineSourcePath.isEmpty()) {
    apply from: new File(settingsDir, '../source_link_gradle/videoengine_source_settings.gradle').getAbsolutePath()
}

def localPropertiesFile = new File(rootProject.projectDir, "local.properties")
def properties = new Properties()

assert localPropertiesFile.exists()
localPropertiesFile.withReader("UTF-8") { reader -> properties.load(reader) }

def flutterSdkPath = properties.getProperty("flutter.sdk")
assert flutterSdkPath != null, "flutter.sdk not set in local.properties"
apply from: "$flutterSdkPath/packages/flutter_tools/gradle/app_plugin_loader.gradle"

plugins.each { name, path ->
    def pluginDirectory = flutterProjectRoot.resolve(path).resolve('android').toFile()
    include ":$name"
    project(":$name").projectDir = pluginDirectory
}
```
example/android/gradle.properties
```
org.gradle.jvmargs=-Xmx1536M
android.useAndroidX=true
android.enableJetifier=true
#liteavSourcePath=/Users/parkhuang/src/liteav
liteavSourcePath=

#videoEngineSourcePath=/Users/aazgulhuang/Code/liteav-base/src/liteav
videoEngineSourcePath=
```

example / android / build.gradle
```
def flutterProjectRoot = rootProject.projectDir.parentFile.toPath()
def plugins = new Properties()
def pluginsFile = new File(flutterProjectRoot.toFile(), '.flutter-plugins')
if (pluginsFile.exists()) {
    pluginsFile.withReader('UTF-8') { reader -> plugins.load(reader) }
}

```

```
allprojects {
    repositories {
        flatDir {
            dirs 'libs'
            dirs "${plugins.get("flutter_tencentplayer")}android/libs"
            dirs 'src/main/jniLibs'
            dirs project(':ugckit').file('libs')
        }
        jcenter()
        google()
    }
}
```

example / android / app / build.gradle
```
dependencies {
    compile 'com.mcxiaoke.volley:library:1.0.19'
    compile 'com.github.bumptech.glide:glide:3.7.0'
    compile fileTree(include: ['*.jar'], dir: 'libs')
    compile fileTree(include: ['*.jar'], dir: 'src/main/jniLibs')
    implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlin_version"
    compile project(':ugcvideoeditdemo')
    compile project(':ugcvideojoindemo')
    compile project(':ugcvideorecorddemo')
    compile project(':ugcvideouploaddemo')
    compile project(':superplayerdemo')
    compile 'com.android.support:appcompat-v7:25.+'
    compile 'com.android.support:recyclerview-v7:25.+'
    compile 'com.squareup.okhttp3:logging-interceptor:3.8.1'
    compile 'com.android.support:multidex:1.0.0'
    compile 'com.android.support:appcompat-v7:25.+'
    compile 'com.android.support.constraint:constraint-layout:1.1.3'
    compile "com.android.support:design:26.0.1"
    compile 'com.squareup.picasso:picasso:2.71828'
    compile 'com.blankj:utilcode:1.25.9'
    compile 'com.tencent.bugly:crashreport_upgrade:1.5.1'
    compile 'com.tencent.bugly:nativecrashreport:3.8.0'
}
```

example/android/app/src/main/kotlin/com/zhaizhishe/renting/flutter_tx_record_example/MainActivity.kt
```java
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
```

新增
example/android/app/src/main/kotlin/com/zhaizhishe/renting/flutter_tx_record_example/MethodUtil.java
```java
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
```
example/android/app/src/main/kotlin/com/zhaizhishe/renting/flutter_tx_record_example/TxRecordReceiver.java
```java
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
//            UGCKit.init(context);
//            TXLiveBase.getInstance().setLicence(context, licenceUrl, licenseKey);

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
```

关于跟flutter_tencentplayer插件冲突解决办法
修改flutter_tencentplayer的Android app 底下的build.gradle
```
dependencies {
    implementation fileTree(include: ['*.jar'], dir: 'libs')
//    implementation(name: 'LiteAVSDK_Player_7.6.9376', ext: 'aar')
//    compile rootProject.ext.liteavSdk
    compile rootProject.ext.liteavSdk
    implementation(name: 'libsuperplayer', ext: 'aar')
//    compile project(':superplayerkit')
// 超级播放器弹幕集成的第三方库
    implementation 'com.github.ctiao:DanmakuFlameMaster:0.5.3'
    androidTestImplementation 'androidx.test:runner:1.1.1'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.1.1'

}
```


