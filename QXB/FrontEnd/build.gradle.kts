// 项目根目录 build.gradle.kts
buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        // 阿里云镜像放在最后
        maven { url = uri("https://maven.aliyun.com/repository/google") }
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") }
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.1.4")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.0")
    }
}

// 移除 plugins 块，因为它与 buildscript 冲突
// plugins {
//     id("com.android.application") version "8.1.4" apply false
//     id("org.jetbrains.kotlin.android") version "1.9.0" apply false
// }

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}