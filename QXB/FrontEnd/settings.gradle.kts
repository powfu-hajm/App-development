pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        // 阿里云镜像放在最后（作为备选）
        maven { url = uri("https://maven.aliyun.com/repository/google") }
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") }
    }
}

dependencyResolutionManagement {
    // 改为 FAIL_ON_PROJECT_REPOS 或 PREFER_PROJECT
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        // 阿里云镜像放在最后
        maven { url = uri("https://maven.aliyun.com/repository/google") }
        maven { url = uri("https://maven.aliyun.com/repository/public") }
    }
}

rootProject.name = "QXB"
include(":app")