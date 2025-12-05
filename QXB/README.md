# QXB - 青心伴 App

## 快速开始

### 1. 启动后端

```bash
cd QXB/BackEnd
chmod +x mvnw
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

### 2. 配置 Android 网络

> 详细的后端配置、数据库初始化、IP 查看方法请参考 [BackEnd/README.md](./BackEnd/README.md)

修改 `FrontEnd/app/src/main/java/com/example/qxb/RetrofitClient.java`:

```java
// 模拟器调试：使用 10.0.2.2
private static final String BASE_URL = "http://10.0.2.2:8080/api/";

// 真机调试：使用电脑局域网 IP
// private static final String BASE_URL = "http://192.168.x.x:8080/api/";
```

### 3. 运行 Android

在 Android Studio 中打开 `FrontEnd` 目录并运行。

## 功能说明

### 心理测试

- 入口：底部导航 -> 测试
- 流程：选择量表 -> 答题 -> 查看结果
- 历史记录：点击右上角历史图标查看

### 心情日记

- 入口：底部导航 -> 日记
- 支持增删改查

## API 接口

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/test/papers` | GET | 量表列表 |
| `/api/test/paper/{id}` | GET | 量表详情 |
| `/api/test/submit` | POST | 提交答案 |
| `/api/test/history` | GET | 测试历史 |
| `/api/diary` | GET/POST/PUT/DELETE | 日记操作 |

## 常见问题

**模拟器连不上后端？**
- 确认后端已启动
- 使用 `10.0.2.2` 地址

**真机连不上后端？**
- 手机和电脑需在同一 WiFi
- 使用电脑 IP（执行 `ifconfig` 或 `ipconfig` 查看）
