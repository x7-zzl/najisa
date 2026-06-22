

# Najisa 

## 项目简介


## 技术栈

- **后端框架**: Spring Boot 2.x
- **持久层框架**: MyBatis-Plus
- **数据库**: MySQL
- **缓存**: Redis (Lettuce 连接)
- **API 文档**: Knife4j (Swagger 增强)
- **ID 生成**: Snowflake 分布式 ID 算法
- **构建工具**: Maven

## 项目结构

```
com.zzl.umr
├── NajisaApplication.java          # 启动类
├── config/                          # 配置类
│   ├── CorsConfig.java             # 跨域配置
│   ├── Knife4jConfig.java          # API 文档配置
│   ├── MyMetaObjectHandler.java    # MyBatis 元对象处理器
│   ├── redis/RedisConfig.java      # Redis 配置
│   └── exception/                   # 异常处理
│       ├── CommonServiceException.java
│       └── GlobalExceptionHandler.java
├── controller/                      # 控制器层
│   ├── BasicUserController.java    # 用户管理接口
│   └── BasicUserLoginController.java # 登录记录管理接口
├── service/                         # 服务层
│   ├── BasicUserService.java       # 用户服务接口
│   ├── BasicUserLoginService.java  # 登录服务接口
│   ├── RedisService.java           # Redis 服务
│   └── impl/                        # 服务实现
├── mapper/                          # 数据访问层
│   ├── BasicUserMapper.java
│   └── BasicUserLoginMapper.java
├── model/                           # 数据模型
│   ├── BasicUser.java              # 用户实体
│   ├── BasicUserLogin.java         # 登录记录实体
│   ├── cdn/LoginCdn.java           # 登录请求 DTO
│   └── dto/HttpResult.java         # 统一响应结果
├── enums/                           # 枚举类
│   ├── ResultCodeEnum.java
│   └── TagTypeEnum.java
├── constants/                       # 常量类
│   └── MessageConstant.java
└── utils/                           # 工具类
    ├── CommonDateUtil.java
    ├── NickNameGeneratorUtil.java
    ├── PasswordValidatorUtil.java
    └── SnowflakeIdWorker.java
```

## 主要功能

### 用户管理
- 用户信息的增删改查 (CRUD)
- 批量删除用户
- 用户注册
- 忘记密码

### 登录认证
- 用户登录/登出
- 登录记录管理
- 登录失败次数统计
- Redis 缓存支持

### API 文档
- 集成 Knife4j 提供可视化 API 文档
- 接口分组和标签管理

## 快速开始

### 环境要求
- JDK 1.8+
- MySQL 5.7+
- Redis 3.0+
- Maven 3.x

### 配置说明

项目支持多环境配置，通过 `application-{profile}.yml` 文件配置：

- `application-dev.yml`: 开发环境
- `application-prod.yml`: 生产环境

主要配置项包括：
- 数据库连接配置
- Redis 连接配置
- CORS 跨域配置

### 初始化数据库

执行 `src/main/resources/doc/sql/umr.sql` 文件创建数据库表结构。

### 运行项目

```bash
# 编译项目
mvn clean package -DskipTests

# 运行
java -jar target/najisa.jar
```

### 访问 API 文档

项目启动后，可通过以下地址访问 Knife4j API 文档：

```
http://localhost:8080/doc.html
```

## 依赖配置

主要依赖包括：
- Spring Boot Starter Web
- MyBatis-Plus Boot Starter
- Redis Lettuce
- Knife4j OpenAPI2
- Lombok
- Hutool 工具库

## License

本项目采用 [MIT License](LICENSE) 开源协议。