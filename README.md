# 天地一家大爱盟交流论坛
开发中------------------------------------------------------------------------------------------------------

<p align="center">
  <b>✨ 基于《蛊真人》世界观的社区交流平台 ✨</b>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Spring%20Boot-2.6.13-brightgreen" alt="Spring Boot">
  <img src="https://img.shields.io/badge/Vue-3.2-blue" alt="Vue 3">
  <img src="https://img.shields.io/badge/Java-17-orange" alt="Java 17">
  <img src="https://img.shields.io/badge/Element%20Plus-2.12-409EFF" alt="Element Plus">
  <img src="https://img.shields.io/badge/MySQL-8.0-blue" alt="MySQL">
  <img src="https://img.shields.io/badge/license-MIT-green" alt="License MIT">
</p>

---

## 📖 项目简介
**Najisa**（天地一家大爱盟）是一个以国产网络小说 **《蛊真人》**（Reverend Insanity）为世界观背景的**论坛社区**。用户可以在平台上浏览、讨论虚拟的"蛊虫"生物，发表文章与视频，使用 AI 助手，并体验一个完整的社区互动生态。

> 蛊虫（Gu Insect）是中国传统巫蛊文化与小说设定结合的虚构生物，分为**凡蛊**与**仙蛊**两大类别，涵盖 **37 种流派**（金木水火土、风雷冰光暗、时空间……），拥有 1 至 9 转的等级体系。

---

## 🏗️ 技术架构

| 层级 | 技术栈 | 说明 |
|------|--------|------|
| **前端框架** | Vue 3 + Element Plus | 组合式 API，现代化 UI |
| **路由** | Vue Router 4 | SPA 路由，路由守卫鉴权 |
| **HTTP 客户端** | Axios | 封装请求拦截器，JWT 自动携带 |
| **后端框架** | Spring Boot 2.6.13 | RESTful API 服务 |
| **语言** | Java 17 | LTS 版本 |
| **ORM** | MyBatis-Plus 3.5.3 | 增强 CRUD，XML 自定义查询 |
| **数据库** | MySQL 8.0 | 关系型数据存储 |
| **连接池** | Druid 1.2 | 阿里巴巴数据库连接池 |
| **缓存** | Redis (Lettuce) | 热点数据缓存、会话管理 |
| **消息队列** | RabbitMQ | 异步消息处理（文章浏览量、系统通知） |
| **实时通信** | WebSocket | 系统通知实时推送 |
| **认证鉴权** | Spring Security + JWT (jjwt) | BCrypt 密码加密，无状态认证 |
| **API 文档** | Knife4j (Swagger) | 可视化接口文档与调试 |
| **工具库** | Hutool 5.8、Lombok | 通用工具类，简化代码 |
| **AI 集成** | Spring AI 1.1.2 | OpenAI 兼容接口，对接 DeepSeek |
| **文件存储** | 本地文件系统 / MinIO | 双引擎可切换 |

---

## 🎮 核心功能

### 🧭 四大内容板块

| 板块 | 路由 | 功能描述 |
|------|------|----------|
| **蛊界佚闻** | `/system/gujie` | 发布蛊界新闻，如方源成尊，星宿仙尊生平等等；包含热帖，评论，点赞功能 |
| **众生万象** | `/system/zhongsheng` | 视频作品展示，封面预览，点赞/收藏/投币互动，如雷鬼真君胸骨被夺，星宿仙尊被气功果炸的灰头土脸 |
| **宝黄天** | `/system/baohuang` | 售卖蛊虫，材料，宝物等，可以标注价格，可以私聊购买，修改价格等等 |
| **天下名蛊** | `/system/tianxia` | **核心功能**：蛊虫百科图鉴，按类别、流派、转数（1-9 转）多维度筛选，卡片式网格展示 + 图片轮播 |

### 🤖 AI 智能助手

- 独立 AI 对话服务（端口 `8089`）
- 基于 Spring AI + DeepSeek 大语言模型
- 多会话管理，聊天历史记录

### 👤 用户系统

- **注册 / 登录 / 退出**：JWT 无状态认证，密码 BCrypt + 前端 SHA256 双层加密
- **个人中心**：头像上传、昵称、性别、年龄、联系方式管理
- **财富体系**：元石 / 仙元石 双币制 + 经验值等级
- **角色系统**：普通用户 / 管理员

### 🔔 实时通知

- WebSocket 实时推送系统消息（蛊虫诞生 / 蛊虫消逝事件）
- 顶部通知铃铛 + 抽屉式消息列表
- 已读/未读标记，支持一键全部已读
- 在线用户数统计

### 🛠️ 后台管理

- **用户管理**：CRUD、批量删除、搜索
- **蛊虫管理**：CRUD、批量删除、图片预览、多条件筛选
- 路由守卫 + 角色鉴权

### 📊 异步数据处理

- RabbitMQ 驱动的文章浏览量异步计数
- Redis 缓存热点数据，定时批量写入 MySQL
- 日浏览量 ≥ 100 自动标记"热门"文章

---

## 📁 项目结构

```
najisa/
├── backend/                          # Spring Boot 后端
│   ├── pom.xml                       # Maven 依赖配置
│   └── src/main/
│       ├── java/com/zzl/umr/
│       │   ├── NajisaApplication.java    # 应用入口
│       │   ├── config/                   # 配置类（安全、跨域、MQ、Redis、MyBatis-Plus）
│       │   ├── constants/                # 常量定义
│       │   ├── controller/               # REST 控制器（文章、评论、文件、蛊虫、用户、视频、消息）
│       │   ├── enums/                    # 枚举（蛊虫流派 37 种、等级、角色、消息类型）
│       │   ├── mapper/                   # MyBatis-Plus Mapper 接口
│       │   ├── model/                    # 实体类 / DTO
│       │   ├── service/                  # 业务逻辑 + WebSocket / MQ 生产消费
│       │   └── utils/                    # 工具类（JWT、Snowflake ID、IP 解析、密码校验）
│       └── resources/
│           ├── application.yml           # 主配置
│           ├── application-dev.yml       # 开发环境配置
│           ├── doc/sql/umr.sql           # 数据库建表脚本
│           └── mapper/mysql/             # MyBatis XML 映射文件
│
├── frontend/                         # Vue 3 前端
│   ├── package.json
│   ├── vue.config.js
│   └── src/
│       ├── main.js                      # Vue 应用入口
│       ├── App.vue                      # 根组件
│       ├── router/index.js              # 路由配置 + 登录守卫
│       ├── utils/
│       │   ├── request.js               # Axios 实例（后端 8088）
│       │   └── aiRequest.js             # AI 服务 Axios（8089）
│       ├── components/
│       │   └── MessageNotification.vue  # 通知铃铛组件
│       └── views/
│           ├── LoginPage.vue            # 登录页
│           ├── RegisterPage.vue         # 注册页
│           ├── system/                  # 前台系统页面
│           │   ├── SystemLayout.vue     # 主布局（顶栏导航 + 背景）
│           │   ├── GuJieYiWen.vue       # 蛊界佚闻（论坛列表）
│           │   ├── ZhongShengWanXiang.vue # 众生万象（视频）
│           │   ├── BaoHuangTian.vue     # 宝黄天（画廊）
│           │   ├── TianXiaMingGu.vue    # 天下名蛊（蛊虫图鉴）
│           │   ├── AiChat.vue           # AI 对话
│           │   ├── UserProfile.vue      # 个人中心
│           │   ├── ArticlePublish.vue   # 文章发布
│           │   ├── ArticleDetail.vue    # 文章详情
│           │   ├── VideoPublish.vue     # 视频发布
│           │   ├── VideoDetail.vue      # 视频详情
│           │   └── InsectDetail.vue     # 蛊虫详情
│           └── admin/                   # 后台管理
│               ├── AdminLayout.vue
│               ├── UserManage.vue       # 用户管理
│               └── InsectManage.vue     # 蛊虫管理
│
└── README.md                          # 你在看这里 👀
```

---

## 🚀 快速开始

### 环境要求

| 组件 | 版本要求 |
|------|----------|
| JDK | 17+ |
| Node.js | 18+ (推荐 v24) |
| MySQL | 8.0+ |
| Redis | 3.0+ |
| RabbitMQ | 3.x (可选，消息队列功能需要) |
| Maven | 3.x |

### 1. 克隆项目

```bash
git clone https://github.com/your-username/najisa.git
cd najisa
```

### 2. 初始化数据库

```bash
# 创建数据库
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS umr DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_general_ci;"

# 导入表结构
mysql -u root -p umr < backend/src/main/resources/doc/sql/umr.sql
```

### 3. 配置后端

编辑 `backend/src/main/resources/application-dev.yml`，修改为你的环境配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/umr?...
    username: your_db_user
    password: your_db_password
  redis:
    host: 127.0.0.1
    port: 6379
  rabbitmq:
    host: your_rabbitmq_host
    username: guest
    password: guest
```

### 4. 启动后端

```bash
cd backend

# 方式一：Maven 启动
mvn clean package -DskipTests
java -jar target/umr-0.0.1-SNAPSHOT.jar

# 方式二：IDE 直接运行
# 运行 com.zzl.umr.NajisaApplication.main()
```

后端启动后访问：
- API 服务：`http://localhost:8088/umr`
- 接口文档：`http://localhost:8088/umr/doc.html`

### 5. 启动前端

```bash
cd frontend
npm install
npm run serve
```

前端开发服务器默认运行在 `http://localhost:8080`。

### 6. 启动 AI 服务（可选）

AI 对话功能依赖独立的 API 服务（端口 `8089`）。你需要：
- 配置 DeepSeek API Key
- 或部署兼容 OpenAI 接口的代理服务在端口 `8089`

---

## 🔐 安全设计

- **密码安全**：前端 SHA256 哈希 + 后端 BCrypt 加盐双重加密
- **无状态认证**：JWT (HS512) 令牌，登录时签发，请求头 `Authorization` 携带
- **跨域保护**：CORS 白名单配置
- **路由守卫**：前端 `beforeEach` 校验登录状态，未登录自动跳转
- **SQL 注入防护**：MyBatis-Plus 参数化查询 + Druid 防火墙

---

## 📊 数据库核心表

| 表名 | 说明 |
|------|------|
| `basic_user` | 用户账号信息 |
| `basic_user_login` | 登录日志（IP 追踪） |
| `basic_user_wealth` | 用户财富（元石/仙元石/经验值） |
| `basic_article` | 文章/帖子 |
| `basic_comments` | 文章评论 |
| `basic_video` | 视频作品 |
| `basic_video_info` | 视频互动数据（点赞/收藏/投币） |
| `basic_gu_insect` | 蛊虫信息（核心） |
| `rel_user_gu_insect` | 用户-蛊虫关联 |
| `basic_file` | 文件/图片存储记录 |
| `sys_message` | 系统通知消息 |
| `sys_message_read` | 消息已读记录 |

---

## 📄 License

本项目采用 [MIT License](backend/LICENSE) 开源。

---

## 🙏 致谢

- 世界观灵感来源：网络小说《蛊真人》（Reverend Insanity）
- 前端 UI：[Element Plus](https://element-plus.org/)
- 后端框架：[Spring Boot](https://spring.io/projects/spring-boot)
- API 文档：[Knife4j](https://doc.xiaominfo.com/)
- AI 能力：[Spring AI](https://spring.io/projects/spring-ai) + [DeepSeek](https://www.deepseek.com/)

---

<p align="center">
  <sub>天地一家大爱盟 — 普天之下，莫非蛊土 🌏</sub>
</p>
