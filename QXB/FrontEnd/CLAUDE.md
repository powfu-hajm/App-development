# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
# Build the project
./gradlew clean build

# Install debug APK to connected device/emulator
./gradlew installDebug

# Build release APK
./gradlew assembleRelease

# Run lint checks
./gradlew lint

# Run unit tests
./gradlew test

# Run instrumented tests (requires device/emulator)
./gradlew connectedAndroidTest
```

## Project Configuration

| Setting | Value |
|---------|-------|
| Min SDK | 26 (Android 8.0) |
| Target SDK | 34 (Android 14) |
| Java Version | 1.8 |
| Language | Java (pure, no Kotlin source) |
| View Binding | Enabled |

## Architecture

### MVVM Pattern
- **ViewModels**: `ChatViewModel`, `DiaryViewModel`, `ProfileViewModel` extend AndroidX ViewModel
- **LiveData**: Used for reactive UI updates in fragments
- **Repositories**: `TestRepository`, `UserRepository` (shells for data abstraction)

### Navigation Structure
```
MainActivity (BottomNavigationView host)
├── HomeFragment     - Dashboard with content cards
├── ChatFragment     - AI assistant (stub)
├── DiaryFragment    - Mood journal with server sync
├── TestFragment     - Psychology test launcher
└── ProfileFragment  - User menu and settings
```

### Network Layer
- **RetrofitClient.java**: Singleton HTTP client with logging interceptor
- **ApiService.java**: Retrofit interface defining all endpoints
- **ApiResponse<T>**: Generic wrapper with `code`, `message`, `data` fields

## Key Files

| File | Purpose |
|------|---------|
| `RetrofitClient.java` | API base URL configuration (change `BASE_URL` for different environments) |
| `ApiService.java` | All REST endpoint definitions |
| `res/xml/network_security_config.xml` | Cleartext HTTP allowed hosts |
| `AndroidManifest.xml` | Activity declarations, permissions, launcher config |

## API Base URL Configuration

In `RetrofitClient.java`, update `BASE_URL`:
- **Emulator**: `http://10.0.2.2:8080/api/`
- **Real device (LAN)**: `http://<your-ip>:8080/api/`

Allowed cleartext hosts are configured in `res/xml/network_security_config.xml`.

## Key Dependencies

| Library | Version | Purpose |
|---------|---------|---------|
| Retrofit | 2.9.0 | REST client |
| OkHttp | 4.11.0 | HTTP with logging |
| MPAndroidChart | 3.1.0 | Mood data visualization |
| Material | 1.9.0 | Material Design 3 components |

## Adapter Pattern

All RecyclerView adapters in `adapter/`:
- `ArticleAdapter` - Home feed articles
- `ChatAdapter` - Chat message bubbles
- `DiaryAdapter` - Diary entry list
- `HomePagerAdapter` - ViewPager content
- `TestHistoryAdapter` - Test history records

## Models Structure

```
models/
├── network/
│   ├── ApiResponse.java    - Generic API wrapper
│   ├── Diary.java          - Diary entry DTO
│   └── MoodChartData.java  - Chart statistics
└── test/
    ├── TestPaper.java      - Test metadata
    ├── TestPaperDetail.java - Full test with questions
    ├── TestQuestion.java   - Question with options
    ├── TestOption.java     - Answer option
    └── TestResult.java     - Submission result
```

## Useful Patterns

### Fragment Loading (MainActivity)
```java
private void loadFragment(Fragment fragment) {
    getSupportFragmentManager()
        .beginTransaction()
        .replace(R.id.fragment_container, fragment)
        .commit();
}
```

### API Call Pattern
```java
ApiService api = RetrofitClient.getInstance().create(ApiService.class);
api.getTestPapers().enqueue(new Callback<ApiResponse<List<TestPaper>>>() {
    @Override
    public void onResponse(Call<ApiResponse<List<TestPaper>>> call,
                          Response<ApiResponse<List<TestPaper>>> response) {
        if (response.isSuccessful() && response.body().isSuccess()) {
            // Handle data
        }
    }
    @Override
    public void onFailure(Call<ApiResponse<List<TestPaper>>> call, Throwable t) {
        // Handle error
    }
});
```
