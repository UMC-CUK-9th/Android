import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
    alias(libs.plugins.google.gms.google.services)
}

val properties = Properties()
properties.load(rootProject.file("local.properties").inputStream())

android {
    namespace = "com.example.umc_android_mission2"
    compileSdk = 36

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.example.umc_android_mission2"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // BuildConfig에 local.properties 적용
        buildConfigField("String", "BASE_URL", "\"${properties.getProperty("BASE_URL")}\"")
        buildConfigField("String", "BASE_URL_SSE", "\"${properties.getProperty("BASE_URL_SSE")}\"")
        buildConfigField("String", "KAKAO_API_KEY", "\"${properties.getProperty("KAKAO_API_KEY")}\"")

        // manifestPlaceholders
        manifestPlaceholders["KAKAO_API_KEY"] = properties.getProperty("KAKAO_API_KEY") ?: ""
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.firebase.database)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation("androidx.viewpager2:viewpager2:1.0.0")
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("me.relex:circleindicator:2.1.6")
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    kapt(libs.androidx.room.compiler)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")
    // Kakao SDK (★ 가장 중요: v2 최신 안정 구성)
    implementation("com.kakao.sdk:v2-user:2.22.0")   // 사용자 관리
    implementation("com.kakao.sdk:v2-auth:2.22.0")   // OAuth 인증
    implementation("com.kakao.sdk:v2-common:2.22.0") // 공통 유틸
}
