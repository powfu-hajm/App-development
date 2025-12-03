# 前端合并指南 (AI对话功能模块)

你好！这是负责“AI对话”功能的代码交付包。
文件夹结构已经按照 Android 项目结构整理好，请将对应的 Java 文件和 XML 文件放入你们的项目中。

## 1. 需要添加的文件 (已包含在文件夹中)

请将 `files_to_submit` 下的所有文件复制到你的项目中对应的位置：

**Java 文件:**
- `ui/chat/ChatFragment.java` (核心聊天界面)
- `ChatAdapter.java` & `ChatMessage.java` (聊天列表适配器)
- `data/network/` 下的 `ApiService.java` 和 `RetrofitClient.java` (网络请求)
- `data/models/` 下的 `ChatRequest.java` 和 `ChatResponse.java` (数据模型)

**资源文件:**
- `res/layout/item_chat_message.xml` (聊天气泡布局)
- `res/xml/network_security_config.xml` (网络安全配置，用于允许 HTTP 请求)

---

## 2. 需要修改的配置 (关键!)

除了复制文件，还需要手动修改以下两个配置文件，否则会报错或无法联网。

### A. 修改 `app/build.gradle.kts` (或 build.gradle)
请在 `dependencies` 块中添加以下 Retrofit 和 Gson 的依赖：

```kotlin
dependencies {
    // ... 原有的依赖 ...

    // === 新增：网络请求库 Retrofit & Gson ===
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
}
```

### B. 修改 `app/src/main/AndroidManifest.xml`
需要做两件事：
1. 添加 INTERNET 权限。
2. 引用网络安全配置（为了能访问 HTTP 后端）。

```xml
<manifest ...>
    <!-- 1. 添加这一行权限 -->
    <uses-permission android:name="android.permission.INTERNET" />

    <application
        <!-- 2. 在 application 标签里添加这一行属性 -->
        android:networkSecurityConfig="@xml/network_security_config"
        ... >
        
        ...
    </application>
</manifest>
```

### C. 修改 `MainActivity.java` (如果需要)
如果主界面通过导航栏跳转到聊天界面，请确保引用的是新的 Fragment：
`import com.example.qxb.ui.chat.ChatFragment;`

---

## 3. 后端对接说明
前端代码中的 `RetrofitClient.java` 默认连接的是本地后端：
`private static final String BASE_URL = "http://10.0.2.2:8080/";`

- 如果是模拟器运行，保持这个地址即可。
- 如果是真机运行，需要将其改为电脑的局域网 IP (如 `http://192.168.1.5:8080/`)。
- 如果部署到了服务器，请改为服务器地址。

辛苦整理！

