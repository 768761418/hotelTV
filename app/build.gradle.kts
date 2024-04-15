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
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation ("com.dreamgyf.android.ui.widget:MarqueeTextView:1.1")
    implementation ("com.squareup.okhttp3:okhttp:4.9.1")
    implementation ("com.google.code.gson:gson:2.10.1")
    implementation ("com.squareup.retrofit2:retrofit:2.3.0")
//导入retrofit
    implementation ("com.google.code.gson:gson:2.6.2")
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
}
