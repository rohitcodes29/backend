# Deployment Guide - Complete Step-by-Step

Learn how to deploy your Spring Boot backend and PostgreSQL database to the cloud.

## Part 1: Local Development Setup

### Step 1: Install PostgreSQL

#### Windows
1. Download from https://www.postgresql.org/download/windows/
2. Run installer and remember the PostgreSQL password (default user: postgres)
3. Accept default settings
4. Start PostgreSQL service

#### macOS
```bash
brew install postgresql@15
brew services start postgresql@15
```

#### Linux (Ubuntu/Debian)
```bash
sudo apt-get update
sudo apt-get install postgresql postgresql-contrib
sudo service postgresql start
```

### Step 2: Create Local Database

```bash
# Connect to PostgreSQL (Windows/Mac/Linux)
psql -U postgres

# In psql shell, run:
CREATE DATABASE dbtesting;

# Exit psql
\q
```

### Step 3: Clone and Build Project

```bash
# Clone repository (if hosted on GitHub)
git clone <your-repo-url>
cd backend

# Build project
mvn clean package

# Run locally
mvn spring-boot:run
```

**Backend is now running on:** `http://localhost:8080`

### Step 4: Test Locally

```bash
# Test the API
curl http://localhost:8080/api/users

# Create a user
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name": "Test User", "email": "test@example.com"}'
```

---

## Part 2: Database Deployment - ElephantSQL (Free)

### Step 1: Create ElephantSQL Account

1. Go to https://www.elephantsql.com/
2. Click "Sign Up"
3. Create account with email and password
4. Verify email

### Step 2: Create PostgreSQL Instance

1. Click "Create New Instance"
2. Select plan: **Tiny Turtle (Free)**
3. Name: `dbtesting-prod`
4. Region: Select closest to you (e.g., US-East-1)
5. Click "Select Region"
6. Click "Create Instance"

### Step 3: Get Connection String

1. Go to your instance in ElephantSQL dashboard
2. You'll see connection information:
   ```
   Server: xxxxx.db.elephantsql.com
   User & Default database: xxxxx
   Password: xxxxx
   Port: 5432
   ```

3. The connection string will look like:
   ```
   postgres://username:password@host:5432/database
   ```

4. Copy this URL - you'll need it for Render

### Step 4: Initialize Database Schema

1. Click "Browser" in ElephantSQL dashboard
2. Click "New Table"
3. Run this SQL:

```sql
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_email ON users(email);
```

---

## Part 3: Backend Deployment - Render.com (Free)

### Step 1: Setup GitHub Repository

1. Create GitHub account (if you don't have one)
2. Create new repository (e.g., `backend-api`)
3. Clone locally:
   ```bash
   git clone https://github.com/your-username/backend-api.git
   cd backend-api
   ```

4. Copy your backend code into the repository
5. Commit and push:
   ```bash
   git add .
   git commit -m "Initial commit: Spring Boot backend"
   git push origin main
   ```

### Step 2: Create Render Account

1. Go to https://render.com/
2. Click "Sign Up"
3. Sign up with GitHub (recommended)
4. Authorize Render to access your GitHub account

### Step 3: Deploy Backend on Render

1. Click "New +" → "Web Service"
2. Select your `backend-api` repository
3. Fill in the details:

   | Field | Value |
   |-------|-------|
   | Name | `backend-api` |
   | Environment | `Docker` |
   | Build Command | `mvn clean install -DskipTests` |
   | Start Command | `java -Dserver.port=$PORT -jar target/backend-0.0.1-SNAPSHOT.jar` |
   | Instance Type | `Free` |

4. Click "Create Web Service"

### Step 4: Add Environment Variables

While deployment is in progress, add environment variables:

1. Go to "Environment" tab
2. Click "Add Environment Variable"
3. Add these variables:

   | Key | Value |
   |-----|-------|
   | `DATABASE_URL` | PostgreSQL URL from ElephantSQL |
   | `DB_USERNAME` | ElephantSQL username |
   | `DB_PASSWORD` | ElephantSQL password |
   | `SPRING_PROFILES_ACTIVE` | `prod` |

   Example DATABASE_URL:
   ```
   postgres://user:password@host:5432/database
   ```

### Step 5: Monitor Deployment

1. Render will automatically build and deploy
2. You can see logs in the dashboard
3. Wait for "Deploy successful" message
4. Your app URL will be: `https://backend-api.onrender.com`

### Step 6: First Deployment Might Be Slow

- First build can take 5-10 minutes
- After deployment, accessing from the web cold-starts the service again (free tier)
- Paid plans have always-on service

---

## Part 4: Testing Deployed Application

### Test Backend API

```bash
# Health check
curl https://backend-api.onrender.com/api/users/health/check

# Get all users
curl https://backend-api.onrender.com/api/users

# Create a user
curl -X POST https://backend-api.onrender.com/api/users \
  -H "Content-Type: application/json" \
  -d '{"name": "Cloud User", "email": "cloud@example.com"}'
```

### Expected Response
```json
{
  "id": 1,
  "name": "Cloud User",
  "email": "cloud@example.com",
  "createdAt": "2024-01-15T15:30:00",
  "updatedAt": "2024-01-15T15:30:00"
}
```

---

## Part 5: Updating Your Deployed Application

### Make Code Changes

1. Edit code locally
2. Test locally: `mvn spring-boot:run`
3. Commit and push:
   ```bash
   git add .
   git commit -m "Update: Added new feature"
   git push origin main
   ```

### Render Auto-Deploys

- Render automatically detects changes on GitHub
- Re-builds and deploys automatically
- No manual steps needed!

---

## Part 6: Troubleshooting Deployment

### Build Failures

**Check logs in Render dashboard:**
1. Go to "Logs" tab
2. Look for Java/Maven errors
3. Common issues:
   - Missing environment variables
   - Wrong Java version
   - Dependency not found

**Solution:**
```bash
# Delete old build artifacts
rm -rf target/
mvn clean package
```

### Database Connection Error

**Error:** `Unable to connect to database`

**Solution:**
1. Verify ElephantSQL is running (check dashboard)
2. Verify `DATABASE_URL` environment variable is correct
3. Test connection locally with psql:
   ```bash
   psql postgres://user:password@host:5432/database
   ```

### API Returning 502 Bad Gateway

**Cause:** Application crashed or not running

**Solution:**
1. Check Render logs for errors
2. Verify environment variables are set
3. Check database connectivity
4. Restart service: Click "Manual Deploy" in Render

### Cold Start Issues (Free Tier)

**Cause:** Free tier services spin down after inactivity

**Solution:**
1. First request takes 30+ seconds
2. Subsequent requests are fast
3. Keep-alive script:
   ```bash
   # Ping every 14 minutes to keep service alive
   */14 * * * * curl https://backend-api.onrender.com/api/users/health/check
   ```

---

## Part 7: Database Backup

### ElephantSQL Backup

1. Go to your instance
2. Click "Backups" tab
3. Backups are auto-created
4. Download backup if needed

### Manual Backup

```bash
# Export database
pg_dump postgres://user:password@host:5432/database > backup.sql

# Restore from backup
psql postgres://user:password@host:5432/database < backup.sql
```

---

## Part 8: Cost Estimate

| Service | Free Tier | Cost |
|---------|-----------|------|
| ElephantSQL PostrgreSQL | 20 MB storage | Free |
| Render Web Service | 750 hours/month | Free |
| Render PostgreSQL | NOT available | $15/month |
| Overall | ✅ FREE | ~$0 |

---

## Part 9: Performance Optimization

### For Production Use:

1. **Enable Response Compression:**
   Already configured in `application-prod.yaml`

2. **Database Connection Pooling:**
   Already configured with HikariCP

3. **Monitoring:**
   - Use Render's built-in monitoring
   - Add APM (e.g., New Relic)

4. **Caching:**
   - Consider Redis (for future learning)

### Upgrade from Free to Paid

When ready for production:

1. **ElephantSQL:**
   - Upgrade to higher tier (more storage)
   - Cost: $15+/month

2. **Render:**
   - Upgrade to paid plan (always-on)
   - Cost: $7+/month

---

## Part 10: Next Steps - Frontend

Once backend is deployed:

1. Create React/Vue/Angular frontend
2. API endpoint: `https://backend-api.onrender.com/api/users`
3. On load, fetch users: `GET /api/users`
4. On form submit, create user: `POST /api/users`

Example frontend code:
```javascript
// Fetch all users
fetch('https://backend-api.onrender.com/api/users')
  .then(res => res.json())
  .then(users => console.log(users));

// Create user
fetch('https://backend-api.onrender.com/api/users', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ name: 'John', email: 'john@example.com' })
})
.then(res => res.json())
.then(user => console.log(user));
```

---

## Useful Links

- [Spring Boot Docs](https://spring.io/projects/spring-boot)
- [Render Docs](https://render.com/docs)
- [ElephantSQL Support](https://www.elephantsql.com/docs/)
- [PostgreSQL Docs](https://www.postgresql.org/docs/)
- [GitHub Docs](https://docs.github.com/)
- [Postman API Testing](https://www.postman.com/)
