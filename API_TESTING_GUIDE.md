# API Testing Guide

## Testing the Backend API

This guide shows how to test all API endpoints using cURL or Postman.

## Prerequisites

- Backend running on `http://localhost:8080` (local) or your Render URL
- PostgreSQL database connected and running

## Test Cases

### 1. Health Check

**Test if API is running:**

```bash
curl http://localhost:8080/api/users/health/check
```

**Expected Response:**
```
HTTP 200 OK
"Backend API is running!"
```

---

### 2. Create User (POST) - Test Case 1

**Create a new user - John Doe:**

```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com"
  }'
```

**Expected Response:**
```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john@example.com",
  "createdAt": "2024-01-15T10:30:45.123456",
  "updatedAt": "2024-01-15T10:30:45.123456"
}
```

---

### 3. Create User (POST) - Test Case 2

**Create another user - Jane Smith:**

```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Jane Smith",
    "email": "jane@example.com"
  }'
```

**Expected Response:**
```json
{
  "id": 2,
  "name": "Jane Smith",
  "email": "jane@example.com",
  "createdAt": "2024-01-15T10:35:20.654321",
  "updatedAt": "2024-01-15T10:35:20.654321"
}
```

---

### 4. Create User (POST) - Test Case 3 - Error Case

**Try to create user with duplicate email (should fail):**

```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Another",
    "email": "john@example.com"
  }'
```

**Expected Response:**
```
HTTP 400 Bad Request
```

---

### 5. Get All Users (GET)

**Fetch all users from database:**

```bash
curl http://localhost:8080/api/users
```

**Expected Response:**
```json
[
  {
    "id": 1,
    "name": "John Doe",
    "email": "john@example.com",
    "createdAt": "2024-01-15T10:30:45.123456",
    "updatedAt": "2024-01-15T10:30:45.123456"
  },
  {
    "id": 2,
    "name": "Jane Smith",
    "email": "jane@example.com",
    "createdAt": "2024-01-15T10:35:20.654321",
    "updatedAt": "2024-01-15T10:35:20.654321"
  }
]
```

---

### 6. Get User by ID (GET)

**Fetch specific user by ID:**

```bash
curl http://localhost:8080/api/users/1
```

**Expected Response:**
```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john@example.com",
  "createdAt": "2024-01-15T10:30:45.123456",
  "updatedAt": "2024-01-15T10:30:45.123456"
}
```

---

### 7. Get Non-existent User (GET) - Error Case

**Try to fetch user that doesn't exist:**

```bash
curl http://localhost:8080/api/users/999
```

**Expected Response:**
```
HTTP 404 Not Found
```

---

### 8. Update User (PUT)

**Update an existing user:**

```bash
curl -X PUT http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Updated",
    "email": "john.updated@example.com"
  }'
```

**Expected Response:**
```json
{
  "id": 1,
  "name": "John Updated",
  "email": "john.updated@example.com",
  "createdAt": "2024-01-15T10:30:45.123456",
  "updatedAt": "2024-01-15T10:40:00.999999"
}
```

---

### 9. Delete User (DELETE)

**Delete a user:**

```bash
curl -X DELETE http://localhost:8080/api/users/2
```

**Expected Response:**
```
HTTP 204 No Content
```

---

### 10. Verify Deletion (GET)

**Verify user was deleted:**

```bash
curl http://localhost:8080/api/users
```

**Expected Response:**
```json
[
  {
    "id": 1,
    "name": "John Updated",
    "email": "john.updated@example.com",
    "createdAt": "2024-01-15T10:30:45.123456",
    "updatedAt": "2024-01-15T10:40:00.999999"
  }
]
```

---

## Testing with Postman

### Import Collection

1. Open Postman
2. Create a new collection "Backend API"
3. Create the following requests:

#### Request 1: Health Check
- **Method:** GET
- **URL:** `{{BASE_URL}}/api/users/health/check`
- **Headers:** None

#### Request 2: Create User
- **Method:** POST
- **URL:** `{{BASE_URL}}/api/users`
- **Headers:** 
  - Content-Type: application/json
- **Body (raw):**
```json
{
  "name": "John Doe",
  "email": "john@example.com"
}
```

#### Request 3: Get All Users
- **Method:** GET
- **URL:** `{{BASE_URL}}/api/users`
- **Headers:** None

#### Request 4: Get User by ID
- **Method:** GET
- **URL:** `{{BASE_URL}}/api/users/1`
- **Headers:** None

#### Request 5: Update User
- **Method:** PUT
- **URL:** `{{BASE_URL}}/api/users/1`
- **Headers:** 
  - Content-Type: application/json
- **Body (raw):**
```json
{
  "name": "John Updated",
  "email": "john.updated@example.com"
}
```

#### Request 6: Delete User
- **Method:** DELETE
- **URL:** `{{BASE_URL}}/api/users/1`
- **Headers:** None

### Postman Environment Variables

Create an environment with the following variables:

```
BASE_URL = http://localhost:8080
```

For production testing, change to:
```
BASE_URL = https://your-app-name.onrender.com
```

---

## Expected HTTP Status Codes

| Operation | Status | Meaning |
|-----------|--------|---------|
| GET all users | 200 | OK |
| GET user by ID (exists) | 200 | OK |
| GET user by ID (not found) | 404 | Not Found |
| POST user (success) | 201 | Created |
| POST user (duplicate email) | 400 | Bad Request |
| PUT user (success) | 200 | OK |
| PUT user (not found) | 404 | Not Found |
| DELETE user (success) | 204 | No Content |
| DELETE non-existent user | 204 | No Content |

---

## Troubleshooting

### Connection Refused
```
Error: Failed to connect to localhost:8080
```
**Solution:** Make sure backend is running with `mvn spring-boot:run`

### Database Connection Error
```
Error: Unable to connect to database
```
**Solution:** 
1. Check PostgreSQL is running
2. Verify connection details in application.yaml
3. Verify dbtesting database exists

### JSON Parse Error
```
Error: Unexpected character in JSON
```
**Solution:** 
1. Check JSON format is valid
2. Use `-H "Content-Type: application/json"` header
3. Escape quotes properly in JSON

### Duplicate Email Error
```
Error: unique constraint violation
```
**Solution:** 
1. Use unique email addresses for each test
2. Delete previous users if needed
3. Check database for existing email

---

## Performance Testing

### Load Test with ApacheBench

```bash
# Install ApacheBench (Windows: use WSL or Git Bash)
ab -n 100 -c 10 http://localhost:8080/api/users
```

### Stress Test

```bash
# Test with 1000 requests, 50 concurrent
ab -n 1000 -c 50 http://localhost:8080/api/users
```

---

## Next Steps

1. Test all endpoints locally
2. Deploy to Render
3. Test deployed endpoints
4. Build frontend
5. Test frontend + backend integration
