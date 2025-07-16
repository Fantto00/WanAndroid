plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.wanandroid"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.wanandroid"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation ("com.google.code.gson:gson:2.13.1")
    implementation("com.squareup.okhttp3:okhttp:5.1.0")
    implementation ("io.github.youth5201314:banner:2.2.3")
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    implementation ("com.google.android.material:material:1.10.0")
    implementation ("com.github.franmontiel:PersistentCookieJar:v1.0.1")
    implementation ("androidx.security:security-crypto:1.1.0-alpha06")
}