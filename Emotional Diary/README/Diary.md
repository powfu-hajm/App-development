# 心情日记子项目（前端+后端）概述
### 1.项目（Emotional Diary）构建信息（前后端分离）
#### （1）前端：
- 编译工具：Android Studio
- 绘图库：MPAndroidChart https://github.com/PhilJay/MPAndroidChart
#### （2）后端：
- 编译工具：IntelliJ IDEA  
- 数据库及管理工具：MySQL + Navicat Premium 16
- 后端框架：SpringBoot 3.2.5 + MyBatis Plus 3.5.7
- 后端项目构建工具: Maven
- 后端API请求与接收测试工具：Apifox(Postman也可)
### 3.前端介绍
#### (1)项目架构介绍：
- Java代码部分：
![alt text](<屏幕截图 2025-11-25 005825(1)-1.png>)
- XML代码部分：
![alt text](<屏幕截图 2025-11-25 013119-1.png>)
#### (2)界面介绍：
- 心情日记主界面/首次编辑日记界面（MainActivity+DiaryFragment）
- 历史日记界面（DiaryListActivity）
- 编辑日记界面（DiaryEidtActivity）
- 情绪图表界面（MoodChartActivity）
### 3.后端介绍
#### (1)项目架构介绍：
- Java代码部分（项目主体）：
![alt text](<屏幕截图 2025-11-25 011857-1.png>)
- 其他配置文件：resources/application.yml(配置数据库信息/端口信息等)、resources/wrapper/DairyMapper.xml(数据库映射文件)和pom.xml（项目依赖文件，利用Maven工具，类似Android Studio中的gradle工具）
### 4.基本功能介绍
- 对日记增删改查（可显示首次编辑与修改时间）
- 通过日记数据绘制折线图（以天为单位）和饼图（统计输入的心情个数与分布）
### 5.遇到的一些困难
- 前后端连接失败（数据库角度和网络连接角度）
- 数据更新问题（修改信息后时间得不到更新）
- 绘图问题（不更新和显示错误）
### 6.注意事项
#### (1)网络权限引入（前端Manifest.xml）：
![alt text](<屏幕截图 2025-11-25 014846-1.png>)
#### (2)数据库信息：
- SQL文件名：diary.sql
- 用户名：root
- 密码：1234
- 默认存储的用户信息中:user_id一项始终为1（原因是没有实现登录注册逻辑）
#### (3)后端信息：
- 端口：8080
- 地址：0.0.0.0
- 前端访问后端完整URL(模拟器)：http://10.0.2.2:8080/api/
#### (4)后端API问题：
为了实现日记的修改功能，在项目中多引入了两个API，即如下所示：
- PUT/api/diary (修改日记API)
- DELETE/api/diary （删除日记API）
总共的API如下：
![alt text](<屏幕截图 2025-11-25 015518-1.png>)
#### (5)运行问题答疑（配置方面）
- 针对前端运行问题，针对于配置方面，优先检查：项目级和模块级build.gradle.kts文件，其次检查项目级下的settings.gradle.kts文件和gradle.properties文件，大多都是版本不兼容问题，最后再排除其他问题
- 针对后端运行问题，对于配置方面，参考项目下的pom.xml文件即可，其中SpringBoot和MyBatis Plus版本不兼容问题最为典型
#### (6)前端测试方式（后端开启前提下）
- Andriod Studio 内置模拟器支持
- 真机调试支持（有线连接/无线连接 WIFI）
<span style="color:red">特别注意：模拟器调试时，请将模拟器的IP地址设置为10.0.2.2，而不是localhost；而对于真机调试，请将真机的IP地址设置为你的电脑IP地址，同时注意调整后端中applicaltion.yml配置文件，以及前端中的network_security_config.xml文件。</span>
- 真机调试手机型号：vivo s12 Pro

#### (7)额外声明
- 以上所有介绍都是基于Emotional Diary子项目
- 最终的效果已经同步到github上原项目（QXB）
- 两者效果方面可能有所出入

