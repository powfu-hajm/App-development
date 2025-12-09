# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

青心伴 (QingXinBan) - A Chinese mental health app with psychological assessments and mood diary features.

## Build Commands

### Backend (Spring Boot)

```bash
cd BackEnd

# Build
./mvnw clean package

# Run (requires MySQL and local profile)
./mvnw spring-boot:run -Dspring-boot.run.profiles=local

# Windows
mvnw.cmd spring-boot:run
```

### Android Frontend

```bash
cd FrontEnd

# Build
./gradlew clean build

# Install to device/emulator
./gradlew installDebug
```

## Database Setup

MySQL 8.0+ required. Initialize database:

```sql
CREATE DATABASE IF NOT EXISTS mood_diary CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE mood_diary;
SOURCE BackEnd/src/main/resources/diary_table.sql;
SOURCE BackEnd/src/main/resources/test_tables.sql;
```

Create `BackEnd/src/main/resources/application-local.yml` with your password:

```yaml
spring:
  datasource:
    password: your_password
```

## Tech Stack

| Layer | Technology |
|-------|------------|
| Backend | Spring Boot 3.2.5, MyBatis Plus 3.5.7, MySQL 8.0, Java 17 |
| Android | AndroidX, Retrofit 2.9.0, GSON, MPAndroidChart 3.1.0, Java |

## Architecture

### Backend (BackEnd/src/main/java/org/example/emotionbackend/)

- `controller/` - REST endpoints returning `Result<T>` wrapper
- `service/` - Business logic (interface + `impl/`)
- `mapper/` - MyBatis Plus data access
- `entity/` - DB entities with `@TableName`, `@TableField`
- `dto/` - Request/response objects
- `common/Result.java` - Standard API response: `{code, message, data}`

### Android (FrontEnd/app/src/main/java/com/example/qxb/)

- `MainActivity.java` - Bottom navigation host
- `ApiService.java` - Retrofit API interface
- `RetrofitClient.java` - HTTP client (update `BASE_URL` for your environment)
- `ui/` - Fragments for each tab (home, chat, diary, test, profile)
- `models/` - Data models including `test/` and `network/`
- `viewmodels/` - MVVM ViewModels with LiveData
- `adapter/` - RecyclerView adapters

## Key Patterns

- **API Response**: All endpoints return `Result<T>` with `code`, `message`, `data`
- **Soft Delete**: MyBatis Plus `logic-delete-value: 1` (entities with `deleted` field)
- **Auto Timestamps**: `@TableField(fill = FieldFill.INSERT)` auto-fills `createTime`/`updateTime`
- **Validation**: Use `@Valid` on request DTOs in controllers

## Configuration

**Android API URL** (in `RetrofitClient.java`):
- Emulator: `http://10.0.2.2:8080/api/`
- Real device: `http://<your-lan-ip>:8080/api/`

**Network Security**: Cleartext HTTP allowed via `res/xml/network_security_config.xml`

## API Endpoints

All endpoints prefixed with `/api`:

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/test/papers` | GET | List all questionnaires |
| `/test/paper/{id}` | GET | Get questionnaire with questions |
| `/test/submit` | POST | Submit answers, get result |
| `/test/history?userId={id}` | GET | User's test history |
| `/diary` | POST | Create diary entry |
| `/diaries?userId={id}` | GET | List user's diaries |
| `/diary` | PUT | Update diary |
| `/diary?id={id}` | DELETE | Delete diary |

## Database Tables

| Table | Purpose |
|-------|---------|
| `test_paper` | Questionnaire definitions |
| `test_question` | Questions per questionnaire |
| `test_option` | Answer options with scores |
| `test_result_range` | Score interpretation ranges |
| `test_record` | User test submissions |
| `diary` | User diary entries |
