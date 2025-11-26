plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.mood_frontend"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.mood_frontend"
        minSdk = 26
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // 使用 Java 兼容版本
    implementation("androidx.core:core:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.activity:activity:1.7.2")

    // ViewModel 和 LiveData
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.6.2")
    implementation("androidx.lifecycle:lifecycle-livedata:2.6.2")
    implementation("androidx.lifecycle:lifecycle-common-java8:2.6.2")

    // RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.3.1")

    // 添加必要的 Kotlin 依赖以避免版本冲突
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.10")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")

    // Retrofit 网络请求
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // MPAndroidChart 图表库
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}

// 统一 Kotlin 版本，避免冲突
configurations.all {
    resolutionStrategy {
        eachDependency {
            // 统一所有 Kotlin 相关依赖的版本
            when (requested.group) {
                "org.jetbrains.kotlin" -> useVersion("1.8.10")
                "org.jetbrains.kotlinx" -> {
                    when (requested.name) {
                        "kotlinx-coroutines-core" -> useVersion("1.6.4")
                        "kotlinx-coroutines-android" -> useVersion("1.6.4")
                    }
                }
            }
        }
    }
}