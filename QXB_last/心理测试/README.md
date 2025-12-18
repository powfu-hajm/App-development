# 心理测试模块 - 代码演示

此文件夹包含「青心伴App」心理测试功能的完整代码，包括前端 Android 和后端 Spring Boot 部分。

## 目录结构

```
心理测试/
├── 前端Android/                    # Android 客户端代码
│   ├── activities/                 # Activity 类（测试页面）
│   │   ├── TestActivity.java           # 测试列表页
│   │   ├── TestQuestionActivity.java   # 答题页面
│   │   ├── TestResultActivity.java     # 测试结果页
│   │   └── TestHistoryActivity.java    # 测试历史记录页（从"我的"页面进入）
│   │
│   ├── fragments/                  # Fragment 片段
│   │   ├── TestFragment.java           # 底部导航测试Tab
│   │   └── TestFragment_ui.java        # UI子包中的测试Fragment
│   │
│   ├── viewmodels/                 # MVVM ViewModel
│   │   └── TestViewModel.java          # 测试数据管理
│   │
│   ├── repository/                 # 数据仓库层
│   │   └── TestRepository.java         # 测试数据获取
│   │
│   ├── adapters/                   # RecyclerView 适配器
│   │   └── TestHistoryAdapter.java     # 历史记录列表适配器
│   │
│   ├── models/                     # 数据模型
│   │   └── test/
│   │       ├── TestPaper.java          # 问卷基础信息
│   │       ├── TestPaperDetail.java    # 完整问卷（含题目）
│   │       ├── TestQuestion.java       # 题目信息
│   │       ├── TestOption.java         # 选项信息
│   │       ├── TestSubmitRequest.java  # 提交答案请求
│   │       └── TestResult.java         # 测试结果
│   │
│   ├── network/                    # 网络层
│   │   ├── ApiService.java             # Retrofit API 接口定义
│   │   └── RetrofitClient.java         # HTTP 客户端配置
│   │
│   ├── controllers/                # 本地控制器
│   │   └── TestController.java         # 测试流程控制
│   │
│   └── res/                        # 资源文件
│       ├── layout/
│       │   ├── fragment_test.xml       # 测试Tab布局
│       │   ├── activity_test.xml       # 测试列表页布局
│       │   ├── activity_test_question.xml  # 答题页布局
│       │   ├── activity_test_result.xml    # 结果页布局
│       │   ├── activity_test_history.xml   # 历史记录页布局
│       │   ├── item_test.xml           # 测试列表项布局
│       │   └── item_test_history.xml   # 历史记录项布局
│       └── drawable/
│           └── bg_test_icon.xml        # 测试图标背景
│
└── 后端SpringBoot/                 # Spring Boot 服务端代码
    ├── controller/                 # REST 控制器
    │   └── TestController.java         # 测试相关 API 接口
    │
    ├── service/                    # 业务逻辑层
    │   ├── ITestService.java           # 服务接口
    │   └── impl/
    │       └── TestServiceImpl.java    # 服务实现
    │
    ├── mapper/                     # MyBatis Plus 数据访问层
    │   ├── TestPaperMapper.java        # 问卷 Mapper
    │   ├── TestQuestionMapper.java     # 题目 Mapper
    │   ├── TestOptionMapper.java       # 选项 Mapper
    │   ├── TestResultRangeMapper.java  # 结果范围 Mapper
    │   └── TestRecordMapper.java       # 测试记录 Mapper
    │
    ├── entity/                     # 数据库实体
    │   ├── TestPaper.java              # 问卷表实体
    │   ├── TestQuestion.java           # 题目表实体
    │   ├── TestOption.java             # 选项表实体
    │   ├── TestResultRange.java        # 结果范围表实体
    │   └── TestRecord.java             # 测试记录表实体
    │
    ├── dto/                        # 数据传输对象
    │   ├── TestPaperDetailDTO.java     # 问卷详情 DTO
    │   ├── TestQuestionDTO.java        # 题目 DTO
    │   ├── TestOptionDTO.java          # 选项 DTO
    │   ├── TestResultDTO.java          # 测试结果 DTO
    │   └── TestSubmitDTO.java          # 提交答案 DTO
    │
    ├── common/                     # 通用类
    │   └── Result.java                 # 统一响应包装类
    │
    └── sql/                        # 数据库脚本
        └── test_tables.sql             # 心理测试相关表结构
```

## 功能说明

### 1. 测试列表页
- 展示所有可用的心理测试问卷
- 显示问卷名称、描述、题目数量
- 点击进入答题页面

### 2. 答题页面
- 逐题展示问题和选项
- 支持单选作答
- 显示答题进度
- 完成后自动计算得分

### 3. 测试结果页
- 显示测试总得分
- 根据分数范围显示对应的结果分析
- 提供心理健康建议

### 4. 测试历史记录（从"我的"页面进入）
- 查看用户的历史测试记录
- 显示测试时间、得分、结果
- 支持查看历史测试详情

## API 接口

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/test/papers` | GET | 获取所有问卷列表 |
| `/api/test/paper/{id}` | GET | 获取问卷详情（含题目选项） |
| `/api/test/submit` | POST | 提交答案，返回测试结果 |
| `/api/test/history?userId={id}` | GET | 获取用户测试历史记录 |

## 数据库表

| 表名 | 说明 |
|------|------|
| `test_paper` | 问卷表（问卷基本信息） |
| `test_question` | 题目表（问卷下的题目） |
| `test_option` | 选项表（题目的选项和分值） |
| `test_result_range` | 结果范围表（分数对应的结果） |
| `test_record` | 测试记录表（用户答题记录） |

## 技术栈

**前端 Android:**
- AndroidX + MVVM 架构
- Retrofit 2.9.0 网络请求
- GSON 数据解析
- RecyclerView 列表展示

**后端 Spring Boot:**
- Spring Boot 3.2.5
- MyBatis Plus 3.5.7
- MySQL 8.0
- Java 17

## 文件数量统计

- **前端文件**: 22 个
- **后端文件**: 17 个
- **总计**: 39 个核心代码文件
