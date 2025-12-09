# QXB - 青心伴 App -- 心理测试模块

## 快速开始

### 1. 启动后端
打开Vscode进入到项目下运行
```bash
cd QXB/BackEnd
setx SPRING_PROFILES_ACTIVE local
mvnw.cmd spring-boot:run
```


### 2. 运行 Android

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
