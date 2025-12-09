# 后端运行指南

## 环境要求

- JDK 17+
- MySQL 8.0+

## 运行步骤

### 1. 初始化数据库

```bash
cd QXB/BackEnd/src/main/resources
mysql -u root -p
```

数据库mood_diary配置：每一行输入后都要换行
```sql
CREATE DATABASE IF NOT EXISTS mood_diary CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE mood_diary;
SOURCE diary_table.sql
SOURCE test_tables.sql
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


## API 接口文档

### 心理测试模块

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/test/papers` | GET | 获取所有量表列表 |
| `/api/test/paper/{id}` | GET | 获取量表详情（含题目和选项） |
| `/api/test/submit` | POST | 提交测试答案，返回结果 |
| `/api/test/history?userId={id}` | GET | 获取用户测试历史记录 |

### 日记模块

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/diary` | POST | 创建日记 |
| `/api/diaries?userId={id}` | GET | 获取用户日记列表 |
| `/api/diary` | PUT | 更新日记 |
| `/api/diary?id={id}` | DELETE | 删除日记 |

---

## 数据库表结构

| 表名 | 说明 |
|------|------|
| `test_paper` | 心理测试量表 |
| `test_question` | 测试题目 |
| `test_option` | 题目选项 |
| `test_result_range` | 结果区间配置 |
| `test_record` | 用户测试记录 |
| `diary` | 用户日记 |
