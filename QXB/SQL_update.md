# QXB 数据库迁移指南

## 数据库配置

```yaml
数据库名: mood_diary
用户名: root
密码: 1234
端口: 3306
```

---

## 迁移场景

### 场景1：全新设备（无数据库）

```bash
cd /path/to/QXB

# 1. 创建表结构
mysql -u root -p1234 < full_database_schema.sql

# 2. 导入测试问卷数据
mysql -u root -p1234 mood_diary < test_tables.sql
```

### 场景2：已有数据库，补全缺失部分

```bash
cd /path/to/QXB

# 一键补全（安全，不删除现有数据）
mysql -u root -p1234 mood_diary < database_update.sql
```

---

## SQL 文件说明

| 文件 | 作用 | 是否删除现有数据 |
|------|------|-----------------|
| `full_database_schema.sql` | 创建所有表结构 | 是 (DROP TABLE) |
| `test_tables.sql` | 心理测试问卷数据 | 是 (DROP TABLE) |
| `database_update.sql` | 增量补全缺失部分 | 否 (安全) |

---

## 数据库表结构

| 表名 | 说明 | 关键字段 |
|------|------|----------|
| `user` | 用户表 | id, username, password, nickname, avatar, phone |
| `diary` | 日记表 | id, user_id, content, mood_tag, deleted |
| `chat_message` | AI聊天记录 | id, user_id, role, content |
| `article` | 心理文章 | id, title, summary, cover_url, original_url, type |
| `test_paper` | 测试问卷 | id, title, description, question_count |
| `test_question` | 测试题目 | id, paper_id, question_order, content |
| `test_option` | 题目选项 | id, question_id, content, score, dimension |
| `test_result_range` | 结果区间 | id, paper_id, min_score, max_score, result_level |
| `test_record` | 用户测试记录 | id, user_id, paper_id, total_score |
| `mbti_type` | MBTI人格类型 | id, type_code, type_name, strengths, weaknesses |

---

## database_update.sql 更新内容

该脚本会自动检测并补全以下内容：

1. **user 表** - 添加 `phone` 字段
2. **diary 表** - 添加 `deleted` 字段
3. **article 表** - 添加 `type` 字段
4. **test_option 表** - 添加 `dimension` 字段 (MBTI维度)
5. **mbti_type 表** - 创建表并插入16种人格类型数据
6. **MBTI维度标识** - 更新选项的 E/I/S/N/T/F/J/P 维度

---

## 测试问卷列表

| ID | 问卷名称 | 题目数 | 预计时间 |
|----|----------|--------|----------|
| 1 | SDS抑郁自评量表 | 20 | 5分钟 |
| 2 | SAS焦虑自评量表 | 20 | 5分钟 |
| 3 | PSS压力知觉量表 | 14 | 4分钟 |
| 4 | MBTI简易人格测试 | 28 | 8分钟 |

---

## 默认账户

```
用户名: root
密码: root (MD5: 63a9f0ea7bb98050796b649e85481845)
```

---

## 常用命令

```bash
# 连接数据库
mysql -u root -p1234 mood_diary

# 查看所有表
SHOW TABLES;

# 查看表结构
DESCRIBE user;

# 查看数据量
SELECT
  (SELECT COUNT(*) FROM user) as users,
  (SELECT COUNT(*) FROM diary) as diaries,
  (SELECT COUNT(*) FROM test_paper) as papers,
  (SELECT COUNT(*) FROM mbti_type) as mbti_types;
```
