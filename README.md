# SkillUp E-Learning Platform

## 📚 Overview

SkillUp is a comprehensive e-learning platform built with Spring Boot that provides a complete solution for online education. The platform supports multiple user roles (Students, Instructors, and Administrators) and offers a full-featured course management system similar to Udemy or Coursera.

### 🎯 Key Features

- **Multi-Role Authentication**: JWT-based authentication supporting Student, Instructor, and Admin roles
- **Course Management**: Complete CRUD operations for courses, modules, and lessons
- **User Management**: Registration, profile management, and role-based access control
- **Progress Tracking**: Student enrollment tracking with progress monitoring
- **Admin Dashboard**: Administrative tools for user management and platform analytics
- **Instructor Dashboard**: Course creation, student analytics, and content management
- **Security**: Input validation, SQL injection prevention, and secure password handling

## 🏗️ Architecture

### Technology Stack

- **Backend Framework**: Spring Boot 3.5.4
- **Database**: PostgreSQL (with H2 for testing)
- **Authentication**: JWT (JSON Web Tokens)
- **Security**: Spring Security with BCrypt password encoding
- **ORM**: Hibernate/JPA
- **Build Tool**: Maven
- **Java Version**: 17

### Database Schema

The platform uses a relational database with the following main entities:

- **User**: Manages user accounts (students, instructors, admins)
- **Course**: Course information and metadata
- **Module**: Course sections/chapters
- **Lesson**: Individual learning units
- **Enrollment**: Student-course relationships with progress tracking
- **Quiz**: Assessment components
- **Certificate**: Course completion certificates

## 🚀 Getting Started

### Prerequisites

- Java 17 or higher
- PostgreSQL 12 or higher
- Maven 3.6 or higher

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/hagag1919/skillup.git
   cd skillup
   ```

2. **Database Setup**
   ```sql
   CREATE DATABASE skillupdb;
   CREATE USER yourPostgressName WITH PASSWORD "password";
   GRANT ALL PRIVILEGES ON DATABASE skillupdb TO postgres;
   ```

3. **Configure Application Properties**
   
   Update `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/skillupdb
   spring.datasource.username=yourPostgressName
   spring.datasource.password=password
   ```

4. **Run the Application**
   ```bash
   ./mvnw spring-boot:run
   ```
   
   Or on Windows:
   ```cmd
   mvnw.cmd spring-boot:run
   ```

5. **Access the Application**
   - API Base URL: `http://localhost:8888/api`
   - Health Check: `http://localhost:8888/api/health`

## 📋 API Documentation

### Authentication Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | User login |
| GET | `/api/auth/validate` | Validate JWT token |

### User Management

| Method | Endpoint | Description | Roles |
|--------|----------|-------------|-------|
| GET | `/api/users/{id}` | Get user by ID | All |
| PUT | `/api/users/{id}` | Update user profile | All |
| DELETE | `/api/users/{id}` | Delete user | Admin |
| GET | `/api/users/instructors` | Get all instructors | All |
| GET | `/api/users/students` | Get all students | All |
| GET | `/api/users/search` | Search users by name | All |
| GET | `/api/users/stats` | Get user statistics | All |

### Course Management

| Method | Endpoint | Description | Roles |
|--------|----------|-------------|-------|
| GET | `/api/courses` | Get all courses | All |
| POST | `/api/courses` | Create new course | Instructor |
| GET | `/api/courses/{id}` | Get course details | All |
| PUT | `/api/courses/{id}` | Update course | Instructor |
| DELETE | `/api/courses/{id}` | Delete course | Instructor |
| GET | `/api/courses/category/{category}` | Get courses by category | All |
| GET | `/api/courses/search` | Search courses | All |

### Instructor Dashboard

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/instructor/courses` | Get instructor's courses |
| POST | `/api/instructor/courses` | Create new course |
| GET | `/api/instructor/courses/{id}/modules` | Get course modules |
| POST | `/api/instructor/courses/{id}/modules` | Create module |
| GET | `/api/instructor/modules/{id}/lessons` | Get module lessons |
| POST | `/api/instructor/modules/{id}/lessons` | Create lesson |
| GET | `/api/instructor/dashboard/stats` | Get instructor statistics |
| GET | `/api/instructor/courses/{id}/analytics` | Get course analytics |

### Admin Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/admin/users` | Get all users with pagination |
| PUT | `/api/admin/users/{id}/ban` | Ban/unban user |
| DELETE | `/api/admin/users/{id}` | Delete user |
| GET | `/api/admin/courses` | Get all courses |
| PUT | `/api/admin/courses/{id}/feature` | Feature/unfeature course |
| PUT | `/api/admin/courses/{id}/activate` | Activate/deactivate course |
| GET | `/api/admin/analytics/overview` | Get platform analytics |

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

## 🛡️ Security Features

- **Password Encryption**: BCrypt hashing
- **Input Validation**: Comprehensive validation for all inputs
- **SQL Injection Prevention**: Parameterized queries
- **XSS Protection**: Input sanitization
- **CORS Configuration**: Configurable cross-origin requests
- **JWT Security**: Token-based stateless authentication

## 🔧 Configuration

### Application Properties

```properties
# Server Configuration
server.port=8888

# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/skillupdb
spring.datasource.username=yourPostgressName
spring.datasource.password=password

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# JWT Configuration
security.jwt.secret-key=your-secret-key
security.jwt.expiration-time=3600000
```

## 📈 Performance Considerations

- **Lazy Loading**: Entity relationships use lazy loading
- **Pagination**: List endpoints support pagination
- **Database Indexing**: Indexed on frequently queried fields
- **Connection Pooling**: Configured for optimal database connections

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

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Development Guidelines

- Follow Java naming conventions
- Write comprehensive unit tests
- Update API documentation
- Validate all inputs
- Use proper error handling

## 📝 License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details.

## 📞 Support

For support and questions:

- Create an issue on GitHub
- Contact the development team
- Check the API documentation

## 🔄 Version History

### v0.0.1-SNAPSHOT (Current)
- Initial release
- Core authentication system
- Course management functionality
- User role management
- Admin dashboard
- Instructor dashboard
- Basic analytics

---

**Built with ❤️ using Spring Boot**
