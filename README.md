# SkillUp E-Learning Platform

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.4-green.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17-blue.svg)](https://www.oracle.com/java/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-12%2B-blue.svg)](https://www.postgresql.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE.md)

## 📚 Overview

SkillUp is an enterprise-grade e-learning platform engineered with Spring Boot, delivering a comprehensive solution for online education and training. The platform features a robust multi-tenant architecture supporting diverse user roles and provides advanced course management capabilities comparable to industry leaders like Udemy and Coursera.

## ✨ Core Features

### 🔐 Advanced Authentication & Authorization
- **Enterprise JWT Security**: Stateless authentication with role-based access control
- **Multi-Role Architecture**: Comprehensive support for Students, Instructors, and Administrators
- **Security Hardening**: BCrypt encryption, input validation, and SQL injection prevention

### 📚 Comprehensive Course Management
- **Content Lifecycle Management**: Full CRUD operations for courses, modules, and lessons
- **Interactive Learning**: Progress tracking with detailed analytics and reporting
- **Content Organization**: Hierarchical course structure with categorization and search

### 🎯 User Experience & Analytics
- **Personalized Dashboards**: Role-specific interfaces with tailored functionality
- **Advanced Analytics**: Real-time performance metrics and engagement tracking
- **Administrative Tools**: Platform-wide management and moderation capabilities

### 🛡️ Enterprise-Grade Security
- **Data Protection**: Comprehensive input validation and sanitization
- **Access Control**: Fine-grained permissions and secure API endpoints
- **Compliance Ready**: Security best practices and audit-friendly architecture

## 🏗️ Technical Architecture

### Technology Stack

| Layer | Technology | Version | Purpose |
|-------|------------|---------|---------|
| **Backend Framework** | Spring Boot | 3.5.4 | Application foundation and auto-configuration |
| **Database** | PostgreSQL | 12+ | Primary data persistence layer |
| **Authentication** | JWT | RFC 7519 | Stateless authentication and authorization |
| **Security Framework** | Spring Security | 6.x | Comprehensive security configuration |
| **ORM** | Hibernate/JPA | 3.x | Object-relational mapping and data access |
| **Build Management** | Apache Maven | 3.6+ | Dependency management and build automation |
| **Runtime Environment** | Java SE | 17 LTS | Application runtime platform |

### System Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │   API Gateway   │    │   Database      │
│   (React/Vue)   │◄──►│   Spring Boot   │◄──►│   PostgreSQL    │
│                 │    │   Security      │    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                              │
                              ▼
                       ┌─────────────────┐
                       │   External      │
                       │   Services      │
                       │   (Email, etc.) │
                       └─────────────────┘
```

### Data Model Design

The platform implements a normalized relational database schema optimized for educational content management:

#### Core Entities

| Entity | Purpose | Key Relationships |
|--------|---------|-------------------|
| **User** | Account management for all platform users | → Course (instructor), → Enrollment (student) |
| **Course** | Educational content containers | → Module (composition), → User (instructor) |
| **Module** | Course structural divisions | → Lesson (composition), → Course (aggregation) |
| **Lesson** | Individual learning units | → Module (aggregation), → Quiz (optional) |
| **Enrollment** | Student-course associations | → User (student), → Course, Progress tracking |
| **Quiz** | Assessment and evaluation | → Lesson (aggregation), → Certificate (completion) |
| **Certificate** | Completion credentials | → User (recipient), → Course (source) |

#### Database Schema Highlights

- **Referential Integrity**: Foreign key constraints ensure data consistency
- **Indexing Strategy**: Optimized indexes on frequently queried columns
- **Audit Trails**: Created/updated timestamps on all entities
- **Soft Deletes**: Logical deletion for data preservation and compliance

## 🚀 Quick Start Guide

### System Requirements

| Component | Minimum Version | Recommended |
|-----------|----------------|-------------|
| **Java Development Kit** | OpenJDK 17 LTS | OpenJDK 21 LTS |
| **PostgreSQL** | 12.0+ | 15.0+ |
| **Apache Maven** | 3.6.3 | 3.9.0+ |
| **Memory (RAM)** | 4GB | 8GB+ |
| **Disk Space** | 2GB | 5GB+ |

### Installation & Setup

#### 1. Repository Setup

```bash
git clone https://github.com/hagag1919/skillup.git
cd skillup
```

#### 2. Database Configuration

```sql
-- Create database and user
CREATE DATABASE skillupdb;
CREATE USER skillup_admin WITH PASSWORD 'your_secure_password';
GRANT ALL PRIVILEGES ON DATABASE skillupdb TO skillup_admin;

-- Enable required extensions (if needed)
\c skillupdb
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
```

#### 3. Application Configuration

Create or update `src/main/resources/application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/skillupdb
spring.datasource.username=skillup_admin
spring.datasource.password=your_secure_password

# Connection Pool Settings
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
```

#### 4. Build & Deploy

```bash
# Validate environment
./mvnw clean validate

# Run tests
./mvnw test

# Start application
./mvnw spring-boot:run
```

**Windows Users:**
```cmd
mvnw.cmd clean validate
mvnw.cmd test
mvnw.cmd spring-boot:run
```

#### 5. Verification

- **Health Check**: `GET http://localhost:8888/api/health`
- **API Documentation**: `GET http://localhost:8888/api`
- **Database Connection**: Check application logs for successful connection

### Environment Configuration

#### Development
```properties
spring.profiles.active=development
spring.jpa.show-sql=true
logging.level.com.example.skillup=DEBUG
```

#### Production
```properties
spring.profiles.active=production
spring.jpa.show-sql=false
logging.level.root=WARN
server.port=${PORT:8888}
```

## 📋 API Reference

### API Design Principles

The SkillUp API follows RESTful design patterns with consistent response formats, comprehensive error handling, and standardized HTTP status codes.

#### Base URL
```
Production: https://api.skillup.com/api/v1
Development: http://localhost:8888/api
```

#### Authentication
All protected endpoints require a valid JWT token in the Authorization header:
```
Authorization: Bearer <jwt_token>
```

### Core API Endpoints

#### Authentication & Authorization

| Method | Endpoint | Description | Authentication |
|--------|----------|-------------|----------------|
| `POST` | `/api/auth/register` | Create new user account | None |
| `POST` | `/api/auth/login` | Authenticate user credentials | None |
| `GET` | `/api/auth/validate` | Validate JWT token | Bearer Token |
| `POST` | `/api/auth/refresh` | Refresh JWT token | Bearer Token |
| `POST` | `/api/auth/logout` | Invalidate user session | Bearer Token |

#### User Management

| Method | Endpoint | Description | Access Level |
|--------|----------|-------------|--------------|
| `GET` | `/api/users/{id}` | Retrieve user profile | User/Admin |
| `PUT` | `/api/users/{id}` | Update user profile | User/Admin |
| `DELETE` | `/api/users/{id}` | Delete user account | Admin Only |
| `GET` | `/api/users/instructors` | List all instructors | Public |
| `GET` | `/api/users/students` | List all students | Admin Only |
| `GET` | `/api/users/search` | Search users by criteria | Authenticated |
| `GET` | `/api/users/stats/overview` | Platform analytics | Admin Only |

#### Course Management

| Method | Endpoint | Description | Access Level |
|--------|----------|-------------|--------------|
| `GET` | `/api/courses` | List available courses | Public |
| `POST` | `/api/courses` | Create new course | Instructor |
| `GET` | `/api/courses/{id}` | Get course details | Public |
| `PUT` | `/api/courses/{id}` | Update course information | Instructor |
| `DELETE` | `/api/courses/{id}` | Delete course | Instructor/Admin |
| `GET` | `/api/courses/category/{category}` | Filter by category | Public |
| `GET` | `/api/courses/search` | Search courses | Public |

#### Content Management

| Method | Endpoint | Description | Access Level |
|--------|----------|-------------|--------------|
| `GET` | `/api/courses/{id}/modules` | Get course modules | Enrolled/Instructor |
| `POST` | `/api/courses/{id}/modules` | Create course module | Instructor |
| `GET` | `/api/modules/{id}/lessons` | Get module lessons | Enrolled/Instructor |
| `POST` | `/api/modules/{id}/lessons` | Create lesson | Instructor |
| `PUT` | `/api/lessons/{id}` | Update lesson content | Instructor |
| `DELETE` | `/api/lessons/{id}` | Delete lesson | Instructor |

### API Response Format

#### Success Response
```json
{
  "success": true,
  "message": "Operation completed successfully",
  "data": {
    // Response payload
  },
  "timestamp": "2024-01-01T12:00:00Z",
  "path": "/api/endpoint"
}
```

#### Error Response
```json
{
  "success": false,
  "message": "Error description",
  "errors": [
    {
      "field": "email",
      "message": "Invalid email format"
    }
  ],
  "timestamp": "2024-01-01T12:00:00Z",
  "path": "/api/endpoint"
}
```

## 🔐 Authentication

The platform uses JWT-based authentication with the following flow:

1. **Registration/Login**: Users register or login with credentials
2. **Token Generation**: Server generates JWT token with user details
3. **Token Usage**: Include token in Authorization header: `Bearer <token>`
4. **Token Validation**: Server validates token for protected endpoints

### Sample JWT Payload
```json
{
  "sub": "user@example.com",
  "userId": 1,
  "role": "STUDENT",
  "name": "John Doe",
  "iat": 1640995200,
  "exp": 1641081600
}
```

## 👥 User Roles & Permissions

### Student
- Register for courses
- Track learning progress
- Access course content
- View certificates

### Instructor
- Create and manage courses
- Create modules and lessons
- View student analytics
- Manage course enrollments

### Administrator
- Manage all users
- Moderate courses
- Access platform analytics
- System configuration

## 🧪 Testing

### Run Tests
```bash
./mvnw test
```

### API Testing with Postman

Import the provided Postman collection (`SkillUp_API_Collection.postman_collection.json`) for comprehensive API testing.

**Test Scenarios Include:**
- User registration and authentication
- Course CRUD operations
- Enrollment workflows
- Admin operations
- Error handling

## 📊 Database Schema

### Core Tables

```sql
-- Users table
CREATE TABLE "user" (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    role VARCHAR(50) DEFAULT 'STUDENT',
    bio TEXT,
    banned BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- Courses table
CREATE TABLE course (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(100) NOT NULL,
    thumbnail_url TEXT,
    instructor_id BIGINT REFERENCES "user"(id),
    featured BOOLEAN DEFAULT FALSE,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- Additional tables: module, lesson, enrollment, quiz, certificate
```

## 🛡️ Security Implementation

### Security Architecture

The platform implements a multi-layered security approach following industry best practices and compliance standards.

#### Authentication & Authorization
- **JWT Implementation**: RFC 7519 compliant tokens with configurable expiration
- **Password Security**: BCrypt with adaptive cost factor and salt rounds
- **Role-Based Access Control (RBAC)**: Granular permissions by user role
- **Session Management**: Stateless authentication with token refresh mechanisms

#### Data Protection
- **Input Validation**: Server-side validation with custom validation annotations
- **SQL Injection Prevention**: Parameterized queries and prepared statements
- **XSS Protection**: Input sanitization and output encoding
- **CSRF Protection**: Token-based CSRF prevention for state-changing operations

#### Security Headers & Configuration
```java
// Example security configuration
http.headers(headers -> headers
    .frameOptions().deny()
    .contentTypeOptions().and()
    .httpStrictTransportSecurity(hstsConfig -> hstsConfig
        .maxAgeInSeconds(31536000)
        .includeSubdomains(true))
);
```

#### Compliance Features
- **Data Audit Trails**: Comprehensive logging of user actions
- **Privacy Controls**: User data export and deletion capabilities
- **Access Logging**: Detailed request/response logging for security monitoring

## ⚡ Performance & Scalability

### Performance Optimization

#### Database Layer
- **Connection Pooling**: HikariCP for optimal connection management
- **Query Optimization**: Indexed queries and efficient JOIN strategies
- **Lazy Loading**: JPA lazy initialization to reduce memory footprint
- **Pagination**: Efficient large dataset handling with offset/limit patterns

#### Application Layer
- **Caching Strategy**: Strategic caching of frequently accessed data
- **Async Processing**: Non-blocking operations for resource-intensive tasks
- **Resource Management**: Proper resource cleanup and connection handling

#### Monitoring & Metrics
```properties
# Actuator endpoints for monitoring
management.endpoints.web.exposure.include=health,metrics,info,prometheus
management.endpoint.health.show-details=always
management.metrics.export.prometheus.enabled=true
```

### Scalability Considerations
- **Stateless Design**: Horizontal scaling capabilities
- **Microservice Ready**: Modular architecture for service decomposition
- **Cloud Native**: Container-ready with externalized configuration

## 🐛 Error Handling

The API uses standardized error responses:

```json
{
  "success": false,
  "message": "Error description",
  "data": null,
  "timestamp": "2024-01-01T12:00:00Z"
}
```

### Common Error Codes
- `400 BAD_REQUEST`: Invalid input data
- `401 UNAUTHORIZED`: Authentication required
- `403 FORBIDDEN`: Insufficient permissions
- `404 NOT_FOUND`: Resource not found
- `500 INTERNAL_SERVER_ERROR`: Server error

## 🤝 Contributing

We welcome contributions from the community! Please read our contribution guidelines before submitting pull requests.

### Development Workflow

1. **Fork & Clone**
   ```bash
   git fork https://github.com/hagag1919/skillup.git
   git clone https://github.com/yourusername/skillup.git
   cd skillup
   ```

2. **Create Feature Branch**
   ```bash
   git checkout -b feature/your-feature-name
   git checkout -b bugfix/issue-description
   ```

3. **Development Standards**
   - Follow Java coding conventions (Google Java Style Guide)
   - Write comprehensive unit tests (minimum 80% coverage)
   - Update API documentation for endpoint changes
   - Validate all inputs and implement proper error handling
   - Follow REST API design principles

4. **Testing & Quality**
   ```bash
   # Run full test suite
   ./mvnw clean test
   
   # Check code coverage
   ./mvnw jacoco:report
   
   # Run static analysis
   ./mvnw spotbugs:check
   ```

5. **Submit Pull Request**
   - Ensure all tests pass
   - Update CHANGELOG.md
   - Provide clear commit messages
   - Include documentation updates

### Code Review Process

- All contributions require peer review
- Automated testing must pass
- Documentation must be updated
- Security implications must be considered

## 📝 License

This project is licensed under the **MIT License** - see the [LICENSE.md](LICENSE.md) file for complete terms.

### Third-Party Licenses
- Spring Boot: Apache License 2.0
- PostgreSQL: PostgreSQL License
- JWT: MIT License

## 📞 Support & Community

### Getting Help

| Resource | Description | Link |
|----------|-------------|------|
| **Documentation** | Comprehensive API docs | [API Docs](docs/api.md) |
| **GitHub Issues** | Bug reports & feature requests | [Issues](https://github.com/hagag1919/skillup/issues) |
| **Discussions** | Community support | [Discussions](https://github.com/hagag1919/skillup/discussions) |
| **Security** | Security vulnerability reports | security@skillup.com |

### Community Guidelines

- Be respectful and professional
- Provide detailed bug reports with reproduction steps
- Search existing issues before creating new ones
- Use clear and descriptive titles

## 🔄 Release Notes

### v1.0.0-SNAPSHOT (Current Development)

#### ✨ Features
- **Core Authentication System**: JWT-based multi-role authentication
- **Course Management**: Complete CRUD operations for educational content
- **User Role Management**: Students, Instructors, and Administrator roles
- **Admin Dashboard**: Platform analytics and user management
- **Instructor Tools**: Course creation and student analytics
- **Security Framework**: Comprehensive input validation and protection

#### 🔧 Technical Improvements
- Enterprise-grade Spring Security configuration
- Optimized database queries with proper indexing
- RESTful API design with consistent response formats
- Comprehensive error handling and validation

#### 🛡️ Security Enhancements
- BCrypt password encryption with adaptive cost
- SQL injection prevention through parameterized queries
- XSS protection with input sanitization
- CORS configuration for secure cross-origin requests

---

<div align="center">

**Built with ❤️ using Spring Boot**

[![GitHub Stars](https://img.shields.io/github/stars/hagag1919/skillup.svg)](https://github.com/hagag1919/skillup/stargazers)
[![GitHub Issues](https://img.shields.io/github/issues/hagag1919/skillup.svg)](https://github.com/hagag1919/skillup/issues)
[![GitHub License](https://img.shields.io/github/license/hagag1919/skillup.svg)](LICENSE.md)

*Empowering education through technology*

</div>
