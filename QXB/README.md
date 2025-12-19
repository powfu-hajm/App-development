# 青心伴 (Qingxinban) - 心理健康管理应用

## 📱 项目简介

青心伴是一款基于 Android 和 Spring Boot 开发的综合性心理健康管理应用，旨在为用户提供 AI 心理咨询、心情日记记录、心理测试评估和心理健康知识推送等服务。

## 🚀 技术栈

### 后端
- **框架**: Spring Boot 3.x
- **ORM**: MyBatis-Plus
- **数据库**: MySQL 8.0+
- **认证**: JWT (JSON Web Token)
- **AI 服务**: 阿里云 DashScope (通义千问)
- **Web 爬虫**: Jsoup
- **构建工具**: Maven

### 前端
- **语言**: Java (Android)
- **网络请求**: Retrofit + OkHttp
- **图片加载**: Glide
- **UI 框架**: Material Design 3
- **构建工具**: Gradle

## ✨ 核心功能

### 1. 用户管理系统 ⭐ **新增功能**

#### 功能特性
- **用户注册**: 支持用户名、密码、昵称注册
- **用户登录**: JWT Token 认证机制
- **个人信息管理**:
  - 修改昵称
  - 修改手机号
  - 修改密码
  - 上传/更换头像（支持相机拍照和相册选择）
- **会话管理**: 自动保存登录状态，支持退出登录

#### 技术实现
- 密码加密: MD5 哈希（生产环境建议使用 BCrypt）
- Token 拦截器: 自动验证 JWT，保护需要登录的接口
- 文件上传: 头像存储在服务器本地 `uploads/avatars/` 目录
- 静态资源映射: `/uploads/**` 映射到本地文件系统

#### 相关文件
**后端**:
- `controller/UserController.java` - 用户相关接口
- `service/IUserService.java` / `impl/UserServiceImpl.java` - 用户业务逻辑
- `entity/User.java` - 用户实体
- `dto/LoginDTO.java`, `RegisterDTO.java`, `UserUpdateDTO.java` - 数据传输对象
- `utils/JwtUtils.java` - JWT 工具类
- `utils/UserContext.java` - 用户上下文（ThreadLocal）
- `config/WebConfig.java` - Token 拦截器配置

**前端**:
- `LoginActivity.java` - 登录界面
- `RegisterActivity.java` - 注册界面
- `EditProfileActivity.java` - 个人信息编辑界面
- `ProfileFragment.java` - 个人中心界面
- `utils/SessionManager.java` - 会话管理工具

**数据库**:
- `user` 表 - 存储用户基本信息

---

### 2. AI 心理咨询 💬

#### 功能特性
- **智能对话**: 基于阿里云通义千问 AI 模型
- **上下文记忆**: AI 能够记住最近 10 条对话历史，提供连贯的对话体验
- **聊天记录持久化**: 所有对话记录保存到数据库，用户下次打开可查看历史
- **历史记录查询**: 支持查看最近 50 条聊天记录

#### 技术实现
- AI 服务集成: 调用 DashScope API
- 消息存储: `chat_message` 表存储用户和 AI 的对话
- 上下文传递: 将历史消息作为上下文传入 AI，提升对话质量

#### 相关文件
**后端**:
- `controller/AIController.java` - AI 聊天接口
- `service/AIService.java` - AI 服务封装
- `service/IChatService.java` / `impl/ChatServiceImpl.java` - 聊天记录管理
- `entity/ChatMessage.java` - 聊天消息实体

**前端**:
- `ChatFragment.java` - AI 对话界面
- `ChatAdapter.java` - 聊天消息适配器

**数据库**:
- `chat_message` 表 - 存储聊天记录

---

### 3. 心情日记 📔

#### 功能特性
- **日记记录**: 记录每日心情、内容、标签
- **日记管理**: 创建、查看、编辑、删除日记
- **情绪分析**:
  - 情绪变化趋势图（最近 30 天）
  - 心情分布统计图
- **数据隔离**: 每个用户只能查看和管理自己的日记

#### 技术实现
- 用户关联: 所有日记记录关联到 `user_id`
- 图表数据: 通过 SQL 聚合查询生成情绪统计数据
- 逻辑删除: 支持软删除（`deleted` 字段）

#### 相关文件
**后端**:
- `controller/DiaryController.java` - 日记相关接口
- `service/IDiaryService.java` / `impl/DiaryServiceImpl.java` - 日记业务逻辑
- `entity/Diary.java` - 日记实体
- `dto/DiaryDTO.java`, `MoodChartDTO.java` - 数据传输对象
- `mapper/DiaryMapper.java` / `DiaryMapper.xml` - 数据库映射

**前端**:
- `DiaryFragment.java` - 日记列表界面
- `EditDiaryActivity.java` - 编辑日记界面
- `MoodChartActivity.java` - 情绪分析图表界面

**数据库**:
- `diary` 表 - 存储日记记录

---

### 4. 基础心理测试 🧪

#### 功能特性
- **测试量表**: 
  - SDS (抑郁自评量表)
  - SAS (焦虑自评量表)
  - PSS (压力感知量表)
  - MBTI (性格类型测试)
- **测试记录**: 保存测试结果，支持查看历史记录
- **结果分析**: 提供测试结果解读和建议

#### 技术实现
- 问卷管理: `test_paper` 表存储问卷信息
- 题目管理: `test_question` 表存储题目和选项
- 结果存储: `test_record` 表存储用户测试结果

#### 相关文件
**后端**:
- `controller/TestController.java` - 测试相关接口
- `service/ITestService.java` / `impl/TestServiceImpl.java` - 测试业务逻辑
- `entity/TestPaper.java`, `TestQuestion.java`, `TestRecord.java` - 实体类
- `dto/TestPaperDetailDTO.java`, `TestResultDTO.java`, `TestSubmitDTO.java` - DTO

**前端**:
- `TestFragment.java` - 测试列表界面
- `TestDetailActivity.java` - 测试详情界面
- `TestResultActivity.java` - 测试结果界面

**数据库**:
- `test_paper` 表 - 问卷表
- `test_question` 表 - 题目表
- `test_record` 表 - 测试记录表

---

### 5. 文章推送 📰 ⭐ **新增功能**

#### 功能特性
- **内容爬取**: 从心理健康知识网站（如：壹心理）自动抓取文章
- **文章展示**: 在首页展示最新文章列表
- **文章详情**: 支持在 WebView 中查看原文链接
- **内容更新**: 支持手动触发爬虫更新内容

#### 技术实现
- Web 爬虫: 使用 Jsoup 解析 HTML，提取文章标题、摘要、封面、原文链接
- 数据去重: 基于 `original_url` 防止重复抓取
- 静态存储: 文章信息存储在 `article` 表中

#### 相关文件
**后端**:
- `controller/ArticleController.java` - 文章相关接口
- `service/CrawlService.java` - 爬虫服务
- `entity/Article.java` - 文章实体
- `mapper/ArticleMapper.java` - 文章数据访问

**前端**:
- `HomeFragment.java` - 首页文章列表
- `ArticleDetailActivity.java` - 文章详情（WebView）

**数据库**:
- `article` 表 - 存储爬取的文章信息

---

## 🗄️ 数据库设计

### 核心表结构

#### `user` 表（用户表）
```sql
- id: BIGINT (主键)
- username: VARCHAR(50) (唯一)
- password: VARCHAR(100) (MD5 加密)
- nickname: VARCHAR(50)
- avatar: VARCHAR(255) (头像 URL)
- phone: VARCHAR(20)
- create_time: DATETIME
- update_time: DATETIME
```

#### `diary` 表（日记表）
```sql
- id: BIGINT (主键)
- user_id: BIGINT (外键关联 user.id)
- content: TEXT
- mood_tag: VARCHAR(20)
- create_time: DATETIME
- update_time: DATETIME
- deleted: TINYINT(1) (逻辑删除标志)
```

#### `chat_message` 表（聊天记录表）
```sql
- id: BIGINT (主键)
- user_id: BIGINT (外键关联 user.id)
- role: VARCHAR(20) (user/assistant)
- content: TEXT
- create_time: DATETIME
```

#### `article` 表（文章表）
```sql
- id: BIGINT (主键)
- title: VARCHAR(255)
- summary: TEXT
- cover_url: VARCHAR(255)
- original_url: VARCHAR(255) (唯一)
- source: VARCHAR(50)
- read_count: INT
- publish_time: DATETIME
- create_time: DATETIME
- deleted: TINYINT(1)
```

#### `test_paper` 表（问卷表）
```sql
- id: BIGINT (主键)
- title: VARCHAR(255)
- description: TEXT
- question_count: INT
```

#### `test_question` 表（题目表）
```sql
- id: BIGINT (主键)
- paper_id: BIGINT (外键)
- question_text: TEXT
- question_type: VARCHAR(20)
- options: JSON
```

#### `test_record` 表（测试记录表）
```sql
- id: BIGINT (主键)
- user_id: BIGINT (外键)
- paper_id: BIGINT (外键)
- score: INT
- result: TEXT
- create_time: DATETIME
```

### 初始化 SQL 脚本

项目根目录提供了以下 SQL 脚本：
- `user_table.sql` - 创建用户表
- `chat_table.sql` - 创建聊天记录表
- `article_table.sql` - 创建文章表
- `diary_table.sql` - 创建日记表（如果不存在）
- `test_tables.sql` - 创建测试相关表
- `add_deleted_column.sql` - 为 diary 表添加 deleted 字段
- `add_phone_column.sql` - 为 user 表添加 phone 字段

---

## ⚙️ 环境配置

### 后端配置

1. **数据库配置** (`BackEnd/src/main/resources/application.yml`):
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mood_diary?...
    username: root
    password: 你的密码

server:
  port: 8080
  servlet:
    context-path: /api

ai:
  api:
    key: 你的阿里云 DashScope API Key
```

2. **文件上传配置**:
```yaml
spring:
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 10MB
```

3. **静态资源路径**: 头像文件存储在 `BackEnd/uploads/avatars/` 目录

### 前端配置

1. **API 基础地址** (`FrontEnd/app/src/main/java/com/example/qxb/RetrofitClient.java`):
```java
public static String BASE_URL = "http://你的IP:8080/api/";
```

2. **网络权限** (`AndroidManifest.xml`):
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```

3. **网络安全配置**: 允许 HTTP 明文传输（开发环境）

---

## 🏃 运行指南

### 后端启动

1. **初始化数据库**:
```bash
mysql -u root -p
CREATE DATABASE mood_diary CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE mood_diary;
SOURCE /path/to/user_table.sql;
SOURCE /path/to/chat_table.sql;
SOURCE /path/to/article_table.sql;
SOURCE /path/to/diary_table.sql;
SOURCE /path/to/test_tables.sql;
SOURCE /path/to/add_deleted_column.sql;
SOURCE /path/to/add_phone_column.sql;
```

2. **配置数据库密码** (创建 `application-local.yml`):
```yaml
spring:
  datasource:
    password: 你的数据库密码
```

3. **启动后端**:
```bash
cd QXB/BackEnd
mvnw.cmd spring-boot:run  # Windows
# 或
./mvnw spring-boot:run   # Mac/Linux
```

4. **验证启动**: 访问 `http://localhost:8080/api/test/papers` 应返回 JSON

### 前端启动

1. **配置后端 IP 地址**:
   - 修改 `RetrofitClient.java` 中的 `BASE_URL`
   - 确保手机和电脑在同一 WiFi 网络

2. **构建并运行**:
```bash
cd QXB/FrontEnd
./gradlew assembleDebug  # 或使用 Android Studio
```

3. **测试用户**:
   - 用户名: `root`
   - 密码: `root`

---

## 📡 API 接口文档

### 用户相关 (`/api/user`)

| 方法 | 路径 | 说明 | 需要登录 |
|------|------|------|---------|
| POST | `/user/register` | 用户注册 | ❌ |
| POST | `/user/login` | 用户登录 | ❌ |
| GET | `/user/info` | 获取用户信息 | ✅ |
| POST | `/user/update` | 更新用户信息 | ✅ |
| POST | `/user/avatar` | 上传头像 | ✅ |

### 日记相关 (`/api/`)

| 方法 | 路径 | 说明 | 需要登录 |
|------|------|------|---------|
| POST | `/diary` | 创建日记 | ✅ |
| GET | `/diaries` | 获取日记列表 | ✅ |
| GET | `/diary` | 获取单条日记 | ✅ |
| PUT | `/diary` | 更新日记 | ✅ |
| DELETE | `/diary` | 删除日记 | ✅ |
| GET | `/mood-chart` | 获取情绪图表数据 | ✅ |

### AI 咨询相关 (`/api/ai`)

| 方法 | 路径 | 说明 | 需要登录 |
|------|------|------|---------|
| POST | `/ai/chat` | 发送消息 | ✅ |
| GET | `/ai/history` | 获取聊天历史 | ✅ |
| GET | `/ai/test` | 测试接口 | ❌ |

### 测试相关 (`/api/test`)

| 方法 | 路径 | 说明 | 需要登录 |
|------|------|------|---------|
| GET | `/test/papers` | 获取问卷列表 | ❌ |
| GET | `/test/paper/{id}` | 获取问卷详情 | ❌ |
| POST | `/test/submit` | 提交测试答案 | ✅ |
| GET | `/test/history` | 获取测试历史 | ✅ |

### 文章相关 (`/api/article`)

| 方法 | 路径 | 说明 | 需要登录 |
|------|------|------|---------|
| GET | `/article/list` | 获取文章列表 | ❌ |
| GET | `/article/crawl` | 触发爬虫 | ❌ |

---

## 🔐 安全机制

1. **JWT 认证**: 所有需要登录的接口都通过 JWT Token 验证
2. **用户隔离**: 用户只能访问自己的数据（日记、测试记录、聊天记录）
3. **密码加密**: 使用 MD5 哈希（生产环境建议升级为 BCrypt）
4. **Token 拦截器**: 自动验证请求头中的 `Authorization: Bearer <token>`

---

## 📝 更新日志

### v2.0 (最新版本)

#### 新增功能
- ✅ **用户管理系统**: 注册、登录、个人信息管理、头像上传
- ✅ **AI 聊天记录持久化**: 保存对话历史，支持上下文记忆
- ✅ **文章爬虫功能**: 从外部网站自动抓取心理健康文章
- ✅ **用户数据隔离**: 所有功能都关联到具体用户，实现数据隔离

#### 优化改进
- 🔧 统一 API 路径前缀 (`/api`)
- 🔧 优化 Token 拦截器配置
- 🔧 改进错误处理和日志记录
- 🔧 修复情绪图表数据查询问题

### v1.0 (初始版本)

- ✅ AI 心理咨询基础功能
- ✅ 心情日记记录
- ✅ 基础心理测试
- ✅ 文章推送（静态数据）

---

## 🐛 已知问题

1. **头像缓存问题**: 上传新头像后，Glide 可能缓存旧图片，已通过禁用缓存解决
2. **路径配置**: 静态资源路径依赖 `user.dir`，在不同环境下可能需要调整
3. **密码加密**: 当前使用 MD5，建议生产环境升级为 BCrypt

---

## 📄 许可证

本项目仅供学习和研究使用。

---

## 👥 贡献者

感谢所有为项目做出贡献的开发者！

---

## 📞 联系方式

如有问题或建议，请通过 Issue 反馈。

