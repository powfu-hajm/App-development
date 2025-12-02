# 后端运行指南

## 环境要求

- JDK 17+
- MySQL 8.0+

## 运行步骤

### 1. 初始化数据库

```
mysql -u root -p
CREATE DATABASE IF NOT EXISTS mood_diary CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE mood_diary;
SOURCE /绝对路径/QXB/BackEnd/src/main/resources/diary_table.sql
SOURCE /绝对路径/QXB/BackEnd/src/main/resources/test_tables.sql
```

验证：
```sql
SELECT id, title, question_count FROM test_paper;
```
应显示4个量表（SDS、SAS、PSS、MBTI）。

### 2. 配置数据库密码

创建 `src/main/resources/application-local.yml`：

```yaml
spring:
  datasource:
    password: 你的数据库密码
```

### 3. 设置环境变量

Mac/Linux：
```
export SPRING_PROFILES_ACTIVE=local
```

Windows：
```
setx SPRING_PROFILES_ACTIVE local
```

### 4. 启动后端

Mac/Linux：
```
chmod +x mvnw
./mvnw spring-boot:run
```

Windows：
```
mvnw.cmd spring-boot:run
```

### 5. 查看本机局域网 IP

Mac/Linux：
```
ifconfig | grep "inet " | grep -v 127.0.0.1
```

Windows：
```
ipconfig
```
找 `IPv4 地址`，通常为 `192.168.x.x`

### 6. 配置 Android 端

修改 `FrontEnd/app/src/main/java/com/example/qxb/RetrofitClient.java`：
```java
private static final String BASE_URL = "http://你的IP:8080/api/";
```

修改 `FrontEnd/app/src/main/res/xml/network_security_config.xml`：
```xml
<domain includeSubdomains="true">你的IP</domain>
```

### 7. 验证

后端：访问 http://localhost:8080/api/test/papers 返回 JSON 即成功。

前后端互通：手机和电脑连同一 WiFi，手机浏览器访问 http://你的IP:8080/api/test/papers 返回 JSON 即成功。
