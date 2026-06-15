# Najisa User Management System

## Project Overview

Najisa is a backend service for a user management system built on the Spring Boot framework, providing comprehensive user information management and login authentication functionality. The system employs MyBatis-Plus as the persistence layer framework, integrates Redis for caching, and incorporates Knife4j to provide API documentation support.

## Technology Stack

- **Backend Framework**: Spring Boot 2.x
- **Persistence Layer**: MyBatis-Plus
- **Database**: MySQL
- **Caching**: Redis (Lettuce connection)
- **API Documentation**: Knife4j (Swagger enhancement)
- **ID Generation**: Snowflake distributed ID algorithm
- **Build Tool**: Maven

## Project Structure

```
com.zzl.umr
├── NajisaApplication.java          # Main application class
├── config/                          # Configuration classes
│   ├── CorsConfig.java             # CORS configuration
│   ├── Knife4jConfig.java          # API documentation configuration
│   ├── MyMetaObjectHandler.java    # MyBatis meta-object handler
│   ├── redis/RedisConfig.java      # Redis configuration
│   └── exception/                   # Exception handling
│       ├── CommonServiceException.java
│       └── GlobalExceptionHandler.java
├── controller/                      # Controller layer
│   ├── BasicUserController.java    # User management API
│   └── BasicUserLoginController.java # Login record management API
├── service/                         # Service layer
│   ├── BasicUserService.java       # User service interface
│   ├── BasicUserLoginService.java  # Login service interface
│   ├── RedisService.java           # Redis service
│   └── impl/                        # Service implementations
├── mapper/                          # Data access layer
│   ├── BasicUserMapper.java
│   └── BasicUserLoginMapper.java
├── model/                           # Data models
│   ├── BasicUser.java              # User entity
│   ├── BasicUserLogin.java         # Login record entity
│   ├── cdn/LoginCdn.java           # Login request DTO
│   └── dto/HttpResult.java         # Unified response format
├── enums/                           # Enumerations
│   ├── ResultCodeEnum.java
│   └── TagTypeEnum.java
├── constants/                       # Constant classes
│   └── MessageConstant.java
└── utils/                           # Utility classes
    ├── CommonDateUtil.java
    ├── NickNameGeneratorUtil.java
    ├── PasswordValidatorUtil.java
    └── SnowflakeIdWorker.java
```

## Key Features

### User Management
- CRUD operations for user information
- Bulk user deletion
- User registration
- Password recovery

### Authentication
- User login/logout
- Login record management
- Login failure count tracking
- Redis caching support

### API Documentation
- Integrated Knife4j for interactive API documentation
- Interface grouping and tag management

## Quick Start

### Environment Requirements
- JDK 1.8+
- MySQL 5.7+
- Redis 3.0+
- Maven 3.x

### Configuration

The project supports multi-environment configurations via `application-{profile}.yml` files:

- `application-dev.yml`: Development environment
- `application-prod.yml`: Production environment

Key configuration items include:
- Database connection settings
- Redis connection settings
- CORS cross-origin configuration

### Initialize Database

Execute the SQL script located at `src/main/resources/doc/sql/umr.sql` to create the database schema.

### Run the Project

```bash
# Build the project
mvn clean package -DskipTests

# Run the application
java -jar target/najisa.jar
```

### Access API Documentation

After starting the project, access the Knife4j API documentation at:

```
http://localhost:8080/doc.html
```

## Dependencies

Key dependencies include:
- Spring Boot Starter Web
- MyBatis-Plus Boot Starter
- Redis Lettuce
- Knife4j OpenAPI2
- Lombok
- Hutool utility library

## License

This project is licensed under the [MIT License](LICENSE).