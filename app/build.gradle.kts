plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.qxb"
    compileSdk = 34  // 改为 34，这是稳定版本

    defaultConfig {
        applicationId = "com.example.qxb"
        minSdk = 24
        targetSdk = 34  // 与 compileSdk 保持一致
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
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

    buildFeatures {
        viewBinding = true
        // 移除 Compose，因为这是纯 Java 项目
        // compose = true
    }
}

configurations.all {
    resolutionStrategy {
        // 强制使用统一的 Kotlin 版本，避免冲突
        force("org.jetbrains.kotlin:kotlin-stdlib:1.8.22")
        force("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.8.22")
        force("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.22")
    }
}

dependencies {
    // 排除所有传递性的 Kotlin 依赖
    implementation("androidx.appcompat:appcompat:1.6.1") {
        exclude(group = "org.jetbrains.kotlin")
    }

    implementation("com.google.android.material:material:1.9.0") {
        exclude(group = "org.jetbrains.kotlin")
    }

    implementation("androidx.constraintlayout:constraintlayout:2.1.4") {
        exclude(group = "org.jetbrains.kotlin")
    }

    implementation("androidx.core:core-splashscreen:1.0.1") {
        exclude(group = "org.jetbrains.kotlin")
    }

    // 添加 Java 兼容的依赖
    implementation("androidx.core:core:1.12.0") {
        exclude(group = "org.jetbrains.kotlin")
    }

    implementation("androidx.lifecycle:lifecycle-viewmodel:2.6.2") {
        exclude(group = "org.jetbrains.kotlin")
    }

    implementation("androidx.lifecycle:lifecycle-livedata:2.6.2") {
        exclude(group = "org.jetbrains.kotlin")
    }

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}