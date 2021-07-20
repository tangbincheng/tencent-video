# flutter_tx_record

A new flutter plugin project.

## Getting Started
要比较修改一下文件
example / android / settings.gradle


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
//            dirs project(':app').file('libs')
//            if (!liteavSourcePath.isEmpty()) {
//                dirs project(':liteav_leb_player').file('src/main/libs')
//            }
        }
        jcenter()
        google()
    }
}
```


example / android / build.gradle
example / android / app / build.gradle
example/android/app/src/main/kotlin/com/zhaizhishe/renting/flutter_tx_record_example/MainActivity.kt

新增
example/android/app/src/main/kotlin/com/zhaizhishe/renting/flutter_tx_record_example/MethodUtil.java
example/android/app/src/main/kotlin/com/zhaizhishe/renting/flutter_tx_record_example/TxRecordReceiver.java


