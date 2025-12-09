# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run Commands

```bash
# Build
./mvnw clean package

# Run (requires MySQL running with mood_diary database)
./mvnw spring-boot:run

# Run with local profile (uses application-local.yml for db password)
./mvnw spring-boot:run -Dspring-boot.run.profiles=local

# Run tests
./mvnw test

# Run single test class
./mvnw test -Dtest=EmotionBackendApplicationTests
```

## Database Setup

MySQL 8.0 database `mood_diary` required. Initialize with:
```sql
CREATE DATABASE IF NOT EXISTS mood_diary CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE mood_diary;
SOURCE src/main/resources/diary_table.sql;
SOURCE src/main/resources/test_tables.sql;
```

For local development, create `src/main/resources/application-local.yml`:
```yaml
spring:
  datasource:
    password: your_password
```

## Architecture

Spring Boot 3.2.5 + MyBatis Plus 3.5.7 + Java 17

```
src/main/java/org/example/emotionbackend/
├── controller/     # REST endpoints (TestController, DiaryController)
├── service/        # Interfaces (ITestService, IDiaryService) + impl/
├── mapper/         # MyBatis Plus mappers extending BaseMapper<T>
├── entity/         # JPA entities with @TableName annotations
├── dto/            # Request/response DTOs
├── config/         # CORS (WebConfig), MyBatis (MybatisPlusConfig, MyMetaObjectHandler)
└── common/         # Result<T> response wrapper
```

## Key Patterns

**API Response Wrapper**: All endpoints return `Result<T>` with code/message/data fields
```java
Result.success(data)           // code: 200
Result.success(message, data)  // code: 200 with custom message
Result.error(message)          // code: 500
```

**MyBatis Plus Configuration** (in application.yml):
- Soft delete: `deleted` field with logic-delete-value: 1
- Auto ID: `id-type: auto`
- Underscore to camelCase mapping enabled

**Auto Timestamps**: `MyMetaObjectHandler` auto-fills `createTime` and `updateTime` fields

## API Endpoints

All endpoints prefixed with `/api` (context-path in application.yml)

| Module | Endpoint | Method | Description |
|--------|----------|--------|-------------|
| Test | `/test/papers` | GET | List questionnaires |
| Test | `/test/paper/{id}` | GET | Get test with questions |
| Test | `/test/submit` | POST | Submit answers |
| Test | `/test/history?userId={id}` | GET | User's test history |
| Diary | `/diary` | POST | Create diary |
| Diary | `/diary` | GET | Get single diary by id param |
| Diary | `/diary` | PUT | Update diary |
| Diary | `/diary?id={id}` | DELETE | Delete diary |
| Diary | `/diaries?userId={id}` | GET | List user's diaries |
| Diary | `/mood-chart?userId={id}` | GET | Mood chart data |
