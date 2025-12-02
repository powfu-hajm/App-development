# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

青心伴App (QingXinBan) - A Chinese mental health and wellness application with mood tracking, psychological assessments, and diary features. The repo contains two projects:

- **QXB/** - Main project (primary focus)
- **Emotional Diary/** - Secondary diary-focused project

## Build Commands

### QXB Backend (Spring Boot + Maven)

```bash
cd QXB/BackEnd

# Build
./mvnw clean package

# Run (default port 8080)
./mvnw spring-boot:run

# Run with local profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

### QXB Frontend (Android + Gradle)

```bash
cd QXB/FrontEnd

# Build
./gradlew clean build

# Install to connected device/emulator
./gradlew installDebug
```

### Emotional Diary Backend

```bash
cd "Emotional Diary/Emotion-Backend"
./mvnw clean package
./mvnw spring-boot:run
```

## Tech Stack

| Layer | Technology |
|-------|------------|
| Backend | Spring Boot 3.2.5, MyBatis Plus 3.5.7, MySQL 8.0, Java 17 |
| Android | AndroidX, Retrofit 2.9.0, GSON, MPAndroidChart 3.1.0, Java |
| Build | Maven (backend), Gradle Kotlin DSL (Android) |

## Architecture

### Backend Structure (QXB/BackEnd)

```
src/main/java/org/example/emotionbackend/
├── controller/     # REST endpoints (@RestController)
├── service/        # Business logic (interface + impl/)
├── mapper/         # MyBatis Plus mappers
├── entity/         # Database entities with @TableName
├── dto/            # Data transfer objects
├── config/         # Spring configs (CORS, MyBatis)
└── common/         # Result<T> response wrapper
```

### Android Structure (QXB/FrontEnd)

```
app/src/main/java/com/example/qxb/
├── MainActivity.java      # Bottom navigation host
├── ApiService.java        # Retrofit interface
├── RetrofitClient.java    # HTTP client singleton
├── ui/                    # Fragments (home/chat/diary/test/profile)
├── models/                # Data models (test/, network/)
├── viewmodels/            # MVVM ViewModels with LiveData
├── adapter/               # RecyclerView adapters
└── repository/            # Data repositories
```

## Key Patterns

- **API Response**: All backend responses use `Result<T>` wrapper with code/message/data
- **Soft Delete**: MyBatis Plus configured with `logic-delete-value: 1`
- **Auto Timestamps**: `@TableField(fill = FieldFill.INSERT)` for createTime/updateTime
- **Network Config**: Cleartext HTTP allowed in dev via `network_security_config.xml`

## Database

MySQL database `mood_diary` with tables:
- test_paper, test_question, test_option, test_result_range, test_record (testing module)
- diary (diary entries)

SQL scripts: `QXB/BackEnd/src/main/resources/*.sql`

## Configuration

| File | Purpose |
|------|---------|
| `QXB/BackEnd/src/main/resources/application.yml` | MySQL connection, MyBatis config |
| `QXB/FrontEnd/app/src/main/java/.../RetrofitClient.java` | API base URL |
| `QXB/FrontEnd/app/src/main/res/xml/network_security_config.xml` | Allowed hosts |

**Android API URL**: Update `BASE_URL` in RetrofitClient.java
- Emulator: `http://10.0.2.2:8080/api/`
- Real device: `http://<your-ip>:8080/api/`

## API Endpoints

### Test Module
- `GET /api/test/papers` - List all questionnaires
- `GET /api/test/paper/{id}` - Get test with questions
- `POST /api/test/submit` - Submit answers, returns result
- `GET /api/test/history` - User's test history

### Diary Module
- `GET /api/diary` - List diaries
- `POST /api/diary` - Create diary
- `PUT /api/diary` - Update diary
- `DELETE /api/diary` - Delete diary
