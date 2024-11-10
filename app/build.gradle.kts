import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
plugins {
    id("com.android.application")
}

android {
    namespace = "com.qb.hotelTV"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.qb.hotelTV"
        minSdk = 21
        targetSdk =33
        versionCode = 5
        versionName = "1.4.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        ndk {
            // 限制APK中包含的ABI
            abiFilters += listOf("armeabi-v7a","x86_64")
        }
    }

    android.applicationVariants.all {
        outputs.all {
            if (this is com.android.build.gradle.internal.api.ApkVariantOutputImpl) {
                val config = project.android.defaultConfig
                val versionName = config.versionName
                val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
                val createTime =LocalDateTime.now().format(formatter)
                this.outputFileName = "hotelTv-V${versionName}-$createTime.apk"
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures{
        dataBinding = true
    }
}

dependencies {
    implementation ("com.kingja.loadsir:loadsir:1.3.8")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
//    implementation("androidx.media3:media3-ui:1.4.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation ("com.dreamgyf.android.ui.widget:MarqueeTextView:1.1")
    implementation ("com.squareup.okhttp3:okhttp:4.9.1")
    implementation ("com.google.code.gson:gson:2.10.1")
    implementation ("com.squareup.retrofit2:retrofit:2.3.0")
//    更新用的
    implementation("io.github.azhon:appupdate:4.3.2")
//导入retrofit
    implementation ("com.google.code.gson:gson:2.6.2")
    implementation ("com.facebook.stetho:stetho-okhttp3:1.3.1")
    implementation ("com.facebook.stetho:stetho:1.3.1")
//Gson 库
//下面两个是RxJava 和 RxAndroid
    implementation ("io.reactivex.rxjava2:rxandroid:2.0.2")
    implementation ("io.reactivex.rxjava2:rxjava:2.x.y")
    implementation ("com.squareup.retrofit2:converter-gson:2.3.0")
//转换器，请求结果转换成Model
    implementation ("com.squareup.retrofit2:adapter-rxjava2:2.3.0")
//配合Rxjava 使用
    implementation ("com.squareup.okhttp3:logging-interceptor:3.4.1")

    implementation ("com.github.bumptech.glide:glide:4.16.0")
    implementation ("com.github.JessYanCoding:AndroidAutoSize:v1.2.1")
    implementation ("com.github.maning0303:MNUpdateAPK:V2.0.5")
    implementation ("pl.droidsonroids.gif:android-gif-drawable:1.2.1")
    implementation ("androidx.leanback:leanback:1.0.0")

    implementation ("androidx.leanback:leanback:1.0.0")
    implementation ("com.google.android.exoplayer:exoplayer:2.18.0")
//    implementation(project(":ffmpeg"))

    implementation ("org.checkerframework:checker-qual:3.20.0")
    //noinspection GradleDependency
//    implementation ("org.jellyfin.exoplayer:exoplayer-ffmpeg-extension:2.18.0+1")

//    implementation ("com.google.android.exoplayer:extension-rtsp:2.17.1")
    implementation ("androidx.appcompat:appcompat:1.3.0")
    implementation ("com.google.android.material:material:1.4.0")
    implementation ("androidx.constraintlayout:constraintlayout:2.0.4")
//    okhttp
    implementation ("com.alibaba:fastjson:1.2.58")
    implementation ("com.orhanobut:logger:2.2.0")
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementation(fileTree("libs"))
//    implementation(name:'webrtc', ext:'aar')
    //或androidx支持库的multidex库
    implementation ("androidx.multidex:multidex:2.0.1")


//    implementation ("org.videolan.android:libvlc-all:3.1.12")

//    implementation ("xyz.doikki.android.dkplayer:dkplayer-java:3.3.7")
////    # 可选，使用exoplayer进行解码
//    implementation ("xyz.doikki.android.dkplayer:player-exo:3.3.7")
//
//    implementation ("xyz.doikki.android.dkplayer:dkplayer-ui:3.3.7")
////    # 可选，使用ijkplayer进行解码
//    implementation ("xyz.doikki.android.dkplayer:player-ijk:3.3.7")
    api("tv.danmaku.ijk.media:ijkplayer-java:0.8.8")

//    # 必选，可兼容市面上绝大部分设备
//    implementation ("com.github.dueeeke.dkplayer:dkplayer-java:2.5.3")
//    implementation ("com.github.dueeeke.dkplayer:dkplayer-armv7a:2.5.3")
////    # 可选，里面包含StandardVideoController的实现
//    implementation ("com.github.dueeeke.dkplayer:dkplayer-ui:2.5.3")
    implementation ("com.github.mafanwei:libvlc:0.0.5")
}
