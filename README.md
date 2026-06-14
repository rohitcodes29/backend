# Backend User API - Spring Boot

A simple REST API built with Spring Boot and PostgreSQL to save and retrieve user information (name and email). This project is designed for learning deployment on Render.

## Prerequisites

- Java 21+
- Maven 3.6+
- PostgreSQL installed and running
- Git

## Project Structure

```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/testing/backend/
│   │   │   ├── controller/      # REST API endpoints
│   │   │   ├── entity/          # JPA entities
│   │   │   ├── repository/      # Data access layer
│   │   │   ├── service/         # Business logic
│   │   │   └── BackendApplication.java
│   │   └── resources/
│   │       └── application.yaml  # Configuration
│   └── test/
├── pom.xml
└── database_schema.sql
```

## Database Setup

### Local Development

1. Create the database:
```sql
CREATE DATABASE dbtesting;
```

2. Run the DDL script or let Hibernate create tables automatically (configured in application.yaml with `ddl-auto: update`)

### PostgreSQL on ElephantSQL (Free Tier)

1. Go to https://www.elephantsql.com/
2. Sign up for a free account
3. Create a new instance (PostgreSQL 15)
4. Copy the connection string
5. Update `application.yaml` with the connection details

## API Endpoints

### 1. **Save a New User (POST)**
```
POST /api/users
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john@example.com"
}

Response: 201 Created
{
  "id": 1,
  "name": "John Doe",
  "email": "john@example.com",
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00"
}
```

### 2. **Get All Users (GET)**
```
GET /api/users

Response: 200 OK
[
  {
    "id": 1,
    "name": "John Doe",
    "email": "john@example.com",
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": "2024-01-15T10:30:00"
  },
  {
    "id": 2,
    "name": "Jane Smith",
    "email": "jane@example.com",
    "createdAt": "2024-01-15T10:35:00",
    "updatedAt": "2024-01-15T10:35:00"
  }
]
```

### 3. **Get User by ID (GET)**
```
GET /api/users/{id}

Response: 200 OK
{
  "id": 1,
  "name": "John Doe",
  "email": "john@example.com",
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00"
}
```

### 4. **Update User (PUT)**
```
PUT /api/users/{id}
Content-Type: application/json

{
  "name": "John Updated",
  "email": "john.updated@example.com"
}

Response: 200 OK
```

### 5. **Delete User (DELETE)**
```
DELETE /api/users/{id}

Response: 204 No Content
```

### 6. **Health Check**
```
GET /api/users/health/check

Response: 200 OK
"Backend API is running!"
```

## Building the Project

### Local Build

```bash
# Build the project
mvn clean package

# Run the application
mvn spring-boot:run

# Or run the JAR file
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

The application will start on `http://localhost:8080`

## Running Locally

### Prerequisites for Local Run

1. **Start PostgreSQL:**
```bash
# Windows (if installed as service)
# It should already be running

# Or start manually via pgAdmin
```

2. **Update Database Connection (if different):**
Edit `src/main/resources/application.yaml`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/dbtesting
    username: postgres
    password: [your_password]
```

3. **Build and Run:**
```bash
mvn clean package
mvn spring-boot:run
```

## Deployment on Render

### Step 1: Prepare for Deployment

1. Create a `.gitignore` file (if not exists)
2. Commit your code to GitHub

### Step 2: Deploy Database on ElephantSQL

1. Go to https://www.elephantsql.com/
2. Create a new free PostgreSQL instance
3. Copy the URL (format: `postgres://user:password@host:port/dbname`)

### Step 3: Deploy Backend on Render

1. Go to https://render.com/
2. Click "New +" → "Web Service"
3. Connect your GitHub repository
4. Configure the service:

   **Build Command:**
   ```
   mvn clean install -DskipTests
   ```

   **Start Command:**
   ```
   java -jar target/backend-0.0.1-SNAPSHOT.jar
   ```

5. **Add Environment Variables:**
   - `DATABASE_URL`: PostgreSQL connection string from ElephantSQL
   - `JDBC_DATABASE_URL`: PostgreSQL connection string
   - `JDBC_DATABASE_USERNAME`: Database username
   - `JDBC_DATABASE_PASSWORD`: Database password

6. Create a `application-prod.yaml` for production (see below)

### Step 4: Production Configuration

Create `src/main/resources/application-prod.yaml`:
```yaml
spring:
  application:
    name: backend-api
  
  datasource:
    url: ${JDBC_DATABASE_URL}
    username: ${JDBC_DATABASE_USERNAME}
    password: ${JDBC_DATABASE_PASSWORD}
    driver-class-name: org.postgresql.Driver
    hikari:
      maximum-pool-size: 5
  
  jpa:
    hibernate:
      ddl-auto: validate
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
  
  jackson:
    serialization:
      write-dates-as-timestamps: false

server:
  port: ${PORT:8080}
  servlet:
    context-path: /
```

### Step 5: Run on Render

1. Click "Create Web Service"
2. Render will automatically build and deploy your application
3. Once deployed, you'll get a URL like: `https://your-app-name.onrender.com`

## Testing the Deployed API

```bash
# Get all users
curl https://your-app-name.onrender.com/api/users

# Create a new user
curl -X POST https://your-app-name.onrender.com/api/users \
  -H "Content-Type: application/json" \
  -d '{"name": "John Doe", "email": "john@example.com"}'
```

## Frontend Integration

The frontend will call these endpoints:

1. **On page load:** `GET /api/users` to fetch all users
2. **On user submission:** `POST /api/users` with name and email form data

## Technologies Used

- **Spring Boot 4.1.0** - Backend framework
- **Spring Data JPA** - Object-relational mapping
- **PostgreSQL** - Database
- **Lombok** - Boilerplate code reduction
- **Maven** - Build tool

## Common Issues & Solutions

### Local Database Connection Issues
```
Error: FATAL: role "postgres" does not exist

Solution: Create the role or use correct credentials
```

### Render Deployment Issues
```
Error: Failed to connect to database

Solution: 
1. Verify DATABASE_URL is correct
2. Check that ElephantSQL instance is active
3. Verify environment variables are set
```

## Next Steps

1. Build the frontend (React/Vue/Angular)
2. Configure CORS properly for frontend domain
3. Add authentication (JWT)
4. Add validation and error handling
5. Set up logging and monitoring

## Learning Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [Render Documentation](https://render.com/docs/)
- [Postman for API Testing](https://www.postman.com/)
