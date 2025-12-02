# 心理测试功能实现计划

## 快速开始指南

### 1. 初始化数据库

```bash
# 登录 MySQL
mysql -u root -p你的密码

# 在 MySQL 中执行以下命令：
CREATE DATABASE IF NOT EXISTS mood_diary CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE mood_diary;
SOURCE /你的项目路径/QXB/BackEnd/src/main/resources/test_tables.sql;

# 验证
SHOW TABLES;
SELECT COUNT(*) FROM test_question;  -- 应该显示 20
```

### 2. 启动后端

```bash
cd "QXB/BackEnd"
./mvnw spring-boot:run
# Windows: mvnw.cmd spring-boot:run
```

### 3. 运行 Android App

在 Android Studio 中 Build → Run

### 4. 测试流程

App → 底部"测试" → "抑郁自评量表" → 开始测试 → 答题 → 提交 → 查看结果

---

## 一、数据库设计

### 1.1 表结构设计

```sql
-- 1. 问卷表 (test_paper)
CREATE TABLE test_paper (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL COMMENT '问卷标题',
    description TEXT COMMENT '问卷描述',
    question_count INT NOT NULL DEFAULT 0 COMMENT '题目数量',
    estimated_time INT NOT NULL DEFAULT 5 COMMENT '预计用时(分钟)',
    icon VARCHAR(50) DEFAULT 'default' COMMENT '图标标识',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='心理测试问卷表';

-- 2. 题目表 (test_question)
CREATE TABLE test_question (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    paper_id BIGINT NOT NULL COMMENT '所属问卷ID',
    question_order INT NOT NULL COMMENT '题目顺序(从1开始)',
    content TEXT NOT NULL COMMENT '题目内容',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_paper_id (paper_id),
    INDEX idx_order (paper_id, question_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='测试题目表';

-- 3. 选项表 (test_option)
CREATE TABLE test_option (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    question_id BIGINT NOT NULL COMMENT '所属题目ID',
    option_order INT NOT NULL COMMENT '选项顺序(A=1, B=2...)',
    content VARCHAR(500) NOT NULL COMMENT '选项内容',
    score INT NOT NULL DEFAULT 0 COMMENT '该选项得分',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_question_id (question_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题目选项表';

-- 4. 结果区间表 (test_result_range)
CREATE TABLE test_result_range (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    paper_id BIGINT NOT NULL COMMENT '所属问卷ID',
    min_score INT NOT NULL COMMENT '最低分(含)',
    max_score INT NOT NULL COMMENT '最高分(含)',
    result_level VARCHAR(50) NOT NULL COMMENT '结果等级(如:正常/轻度/中度/重度)',
    result_title VARCHAR(100) NOT NULL COMMENT '结果标题',
    result_description TEXT NOT NULL COMMENT '结果描述',
    suggestion TEXT COMMENT '建议',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_paper_id (paper_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='测试结果区间表';

-- 5. 用户测试记录表 (test_record)
CREATE TABLE test_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    paper_id BIGINT NOT NULL COMMENT '问卷ID',
    total_score INT NOT NULL COMMENT '总得分',
    result_level VARCHAR(50) NOT NULL COMMENT '结果等级',
    result_title VARCHAR(100) NOT NULL COMMENT '结果标题',
    result_description TEXT NOT NULL COMMENT '结果描述',
    suggestion TEXT COMMENT '建议',
    answers TEXT COMMENT '用户答案JSON(可选)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_user_id (user_id),
    INDEX idx_paper_id (paper_id),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户测试记录表';
```

### 1.2 表关系图

```
test_paper (1) ----< (N) test_question (1) ----< (N) test_option
     |
     +----< (N) test_result_range
     |
     +----< (N) test_record
```

---

## 二、后端设计

### 2.1 实体类 (Entity)

```
src/main/java/org/example/emotionbackend/entity/
├── TestPaper.java        # 问卷实体
├── TestQuestion.java     # 题目实体
├── TestOption.java       # 选项实体
├── TestResultRange.java  # 结果区间实体
└── TestRecord.java       # 测试记录实体
```

### 2.2 DTO 设计

```java
// 1. 获取问卷详情的响应DTO
TestPaperDetailDTO {
    Long id;
    String title;
    String description;
    Integer questionCount;
    Integer estimatedTime;
    List<TestQuestionDTO> questions;
}

TestQuestionDTO {
    Long id;
    Integer questionOrder;
    String content;
    List<TestOptionDTO> options;
}

TestOptionDTO {
    Long id;
    Integer optionOrder;
    String content;
    // 注意：不返回score，防止前端作弊
}

// 2. 提交答案的请求DTO
TestSubmitDTO {
    Long paperId;           // 问卷ID
    Long userId;            // 用户ID
    List<Long> optionIds;   // 用户选择的选项ID列表
}

// 3. 测试结果响应DTO
TestResultDTO {
    Long recordId;          // 记录ID
    Integer totalScore;     // 总分
    String resultLevel;     // 结果等级
    String resultTitle;     // 结果标题
    String resultDescription; // 结果描述
    String suggestion;      // 建议
    LocalDateTime testTime; // 测试时间
}
```

### 2.3 API 接口设计

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 获取问卷列表 | GET | /api/test/papers | 返回所有可用问卷 |
| 获取问卷详情 | GET | /api/test/paper/{id} | 返回问卷含所有题目和选项 |
| 提交测试答案 | POST | /api/test/submit | 计算分数，返回结果 |
| 获取测试历史 | GET | /api/test/history | 返回用户历史测试记录 |

### 2.4 计分算法

```java
public TestResultDTO submitTest(TestSubmitDTO submitDTO) {
    // 1. 根据optionIds查询所有选项，累加score
    int totalScore = optionMapper.sumScoreByIds(submitDTO.getOptionIds());

    // 2. 根据paperId和totalScore查询对应的结果区间
    TestResultRange range = resultRangeMapper.selectOne(
        new LambdaQueryWrapper<TestResultRange>()
            .eq(TestResultRange::getPaperId, submitDTO.getPaperId())
            .le(TestResultRange::getMinScore, totalScore)
            .ge(TestResultRange::getMaxScore, totalScore)
    );

    // 3. 保存测试记录
    TestRecord record = new TestRecord();
    record.setUserId(submitDTO.getUserId());
    record.setPaperId(submitDTO.getPaperId());
    record.setTotalScore(totalScore);
    record.setResultLevel(range.getResultLevel());
    record.setResultTitle(range.getResultTitle());
    record.setResultDescription(range.getResultDescription());
    record.setSuggestion(range.getSuggestion());
    recordMapper.insert(record);

    // 4. 返回结果
    return convertToDTO(record);
}
```

---

## 三、前端设计

### 3.1 页面流程

```
TestFragment (测试列表)
    ↓ 点击"开始测试"
TestQuestionActivity (答题页面)
    ↓ 答题完成，提交
TestResultActivity (结果展示)
    ↓ 返回
TestFragment
```

### 3.2 新增文件

```
app/src/main/java/com/example/qxb/
├── TestQuestionActivity.java    # 答题页面
├── TestResultActivity.java      # 结果页面
├── models/
│   ├── TestPaper.java           # 问卷模型
│   ├── TestQuestion.java        # 题目模型
│   ├── TestOption.java          # 选项模型
│   └── TestResult.java          # 结果模型
└── adapter/
    └── TestOptionAdapter.java   # 选项列表适配器

app/src/main/res/layout/
├── activity_test_question.xml   # 答题页面布局
├── activity_test_result.xml     # 结果页面布局
└── item_test_option.xml         # 选项项布局
```

### 3.3 答题页面功能

- **进度条**：显示当前题目/总题数
- **题目展示**：显示当前题目内容
- **选项列表**：RadioButton 单选
- **导航按钮**：上一题/下一题/提交
- **数据暂存**：Map<Long, Long> 存储 questionId → optionId

### 3.4 API 接口定义

```java
// ApiService.java 新增接口
@GET("test/papers")
Call<ApiResponse<List<TestPaper>>> getTestPapers();

@GET("test/paper/{id}")
Call<ApiResponse<TestPaperDetail>> getTestPaperDetail(@Path("id") Long id);

@POST("test/submit")
Call<ApiResponse<TestResult>> submitTest(@Body TestSubmitRequest request);
```

---

## 四、实现步骤

### 第一阶段：数据库与后端 (60%)

1. **创建SQL文件并初始化数据库**
   - 创建5张表
   - 插入测试数据（SDS抑郁自评量表20题）

2. **后端实体类开发**
   - TestPaper, TestQuestion, TestOption, TestResultRange, TestRecord

3. **后端Mapper层**
   - 继承BaseMapper
   - 自定义查询方法

4. **后端Service层**
   - ITestService 接口
   - TestServiceImpl 实现

5. **后端Controller层**
   - TestController
   - 4个API接口

### 第二阶段：前端界面 (40%)

6. **前端数据模型**
   - 与后端对应的 POJO 类

7. **前端API接口**
   - ApiService 新增方法

8. **TestFragment 改造**
   - 从后端获取问卷列表
   - 点击跳转到答题页面

9. **TestQuestionActivity**
   - 题目逐一展示
   - 选项选择
   - 进度条更新
   - 上一题/下一题

10. **TestResultActivity**
    - 显示测试结果
    - 返回首页

---

## 五、预置测试数据

### SDS抑郁自评量表（20题示例）

| 题号 | 题目 | 选项A(1分) | 选项B(2分) | 选项C(3分) | 选项D(4分) |
|------|------|-----------|-----------|-----------|-----------|
| 1 | 我感到情绪沮丧，郁闷 | 没有或偶尔 | 有时 | 经常 | 总是如此 |
| 2 | 我感到早晨心情最好 | 没有或偶尔 | 有时 | 经常 | 总是如此 |
| ... | ... | ... | ... | ... | ... |

### 结果区间（SDS标准分）

| 分数区间 | 等级 | 描述 |
|----------|------|------|
| 25-49 | 正常 | 您的心理状态良好 |
| 50-59 | 轻度 | 存在轻度抑郁倾向 |
| 60-69 | 中度 | 存在中度抑郁症状 |
| 70-100 | 重度 | 存在重度抑郁症状 |

---

## 六、文件清单

### 后端新增文件 (12个)
```
src/main/java/org/example/emotionbackend/
├── entity/
│   ├── TestPaper.java
│   ├── TestQuestion.java
│   ├── TestOption.java
│   ├── TestResultRange.java
│   └── TestRecord.java
├── dto/
│   ├── TestPaperDetailDTO.java
│   ├── TestSubmitDTO.java
│   └── TestResultDTO.java
├── mapper/
│   ├── TestPaperMapper.java
│   ├── TestQuestionMapper.java
│   ├── TestOptionMapper.java
│   ├── TestResultRangeMapper.java
│   └── TestRecordMapper.java
├── service/
│   ├── ITestService.java
│   └── impl/TestServiceImpl.java
└── controller/
    └── TestController.java

src/main/resources/
└── mapper/TestMapper.xml
```

### 前端新增文件 (10个)
```
app/src/main/java/com/example/qxb/
├── TestQuestionActivity.java
├── TestResultActivity.java
├── models/
│   ├── TestPaper.java
│   ├── TestQuestion.java
│   ├── TestOption.java
│   ├── TestPaperDetail.java
│   ├── TestSubmitRequest.java
│   └── TestResult.java
└── adapter/
    └── TestOptionAdapter.java

app/src/main/res/layout/
├── activity_test_question.xml
├── activity_test_result.xml
└── item_test_option.xml
```

### SQL文件 (1个)
```
test_tables.sql - 建表语句 + 测试数据
```
