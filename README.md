# 🚗 Car Rental System

A full-stack car rental application with a Spring Boot backend and React frontend, containerized with Docker and deployed to cloud platforms.

## 🏗️ Architecture

| Layer | Technology |
|-------|------------|
| Backend | Spring Boot 4.0.6 · Java 17 · Spring Security · JWT |
| Database | PostgreSQL (NeonDB serverless) · Spring Data JPA · HikariCP |
| Frontend | React 19 · Vite 8 · React Router 7 · Axios · Context API |
| Image Storage | Cloudinary (`cloudinary-http5 2.3.2`) |
| Email | [Resend](https://resend.com) transactional email API |
| Infrastructure | Docker · Docker Compose · Nginx (frontend container) |
| CI/CD | GitHub Actions → Docker Hub → self-hosted server (SSH) |
| Frontend Deploy | Vercel (SPA rewrite via `vercel.json`) |

---

## 📋 Features

### User Features
- Registration with email verification (24-hour link via Resend API)
- JWT-based authentication (Bearer token, configurable expiry)
- Browse available cars with filters — category and fuel type
- Date-range availability search
- Book cars with automatic price calculation
- View and cancel own rentals (cancel only while PENDING)
- Profile management with driver license upload

### Admin Features
- Full car management — create (with image upload), update, delete, status change
- Rental management — view all rentals, update status
- User management — view all users, approve/reject driver licenses

---

## 🚀 Quick Start with Docker

### Prerequisites
- Docker Desktop
- A [NeonDB](https://neon.tech) (or any PostgreSQL) database
- A [Resend](https://resend.com) account and API key (free tier available)
- A [Cloudinary](https://cloudinary.com) account (free tier available)

### 1. Clone and configure

```bash
git clone <your-repo-url>
cd Car-Rental
cp .env.example .env
```

Edit `.env` and fill in your values:

```env
# Database — full JDBC URL (NeonDB example)
POSTGRES_DB=jdbc:postgresql://<host>/neondb?sslmode=require
POSTGRES_USERNAME=your_db_user
POSTGRES_PASSWORD=your_db_password

# JWT — Base64-encoded secret, minimum 256 bits
JWT_SECRET=your_base64_jwt_secret
JWT_EXPIRATION=3600000       # 1 hour in ms (optional)

# Email — Resend API (NOT SMTP)
RESEND_API_KEY=re_your_resend_api_key
MAIL_FROM=noreply@yourdomain.com   # must be a verified sender on Resend

# Application URLs
FRONTEND_URL=http://localhost:3000
CORS_ALLOWED_ORIGINS=http://localhost:3000

# Cloudinary
CLOUDINARY_USERNAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_api_secret

# Ports (optional — defaults shown)
BACKEND_PORT=8080
FRONTEND_PORT=3000
VITE_API_URL=http://localhost:8080/api
```

> **Note:** The `.env.example` mentions `MAIL_HOST` / `MAIL_PASSWORD` SMTP variables — those are outdated. The application uses the **Resend API** (`RESEND_API_KEY`), not SMTP.

### 2. Build and run

```bash
docker-compose up --build

# Or detached
docker-compose up -d --build
```

### 3. Access

| Service | URL |
|---------|-----|
| Frontend | http://localhost:3000 |
| Backend API | http://localhost:8080/api |
| Health check | http://localhost:8080/actuator/health |

### 4. Create your first admin

After registering normally, promote the account to admin via the database:

```bash
# Via Docker (if using a local PostgreSQL container)
docker exec -it car-rental-db psql -U postgres -d car_rental_db

# Via NeonDB SQL editor or any psql client
UPDATE users SET role = 'ADMIN' WHERE email = 'your@email.com';
```

---

## 🛠️ Development (without Docker)

### Backend

```bash
# Export required environment variables, then:
./mvnw spring-boot:run

# or
make dev-backend
```

### Frontend

```bash
cd frontend
npm install
npm run dev     # http://localhost:3000
```

---

## 🧰 Makefile Reference

```bash
make build          # Build all Docker images
make up             # Start all services (detached)
make down           # Stop all services
make restart        # Restart all services
make logs           # Tail all logs
make logs-backend   # Tail backend logs
make logs-frontend  # Tail frontend logs
make db-shell       # Open psql shell in DB container
make db-backup      # Dump database to backups/
make db-restore     # Restore from a backup file
make clean          # Remove containers and prune
make clean-all      # Remove everything including volumes ⚠️
make test-backend   # Run backend tests via Maven
make dev-backend    # Run backend locally with Maven
make dev-frontend   # Run frontend locally with Vite
```

---

## 📁 Project Structure

```
Car-Rental/
├── src/main/java/com/example/carrental/
│   ├── config/               # SecurityConfig, CloudinaryConfig, ApplicationConfig
│   ├── controller/           # REST controllers (Admin, Car, Rental, User)
│   ├── dto/
│   │   ├── request/          # CarRequest, RentalRequest, UserRegisterRequest, …
│   │   └── response/         # CarResponse, PublicCarResponse, RentalResponse, UserResponse
│   ├── entity/               # Car, Rental, User (JPA entities)
│   ├── enums/                # CarCategory, CarStatus, FuelType, LicenseStatus,
│   │                         #   RentalStatus, UserRole
│   ├── exceptions/           # GlobalExceptionHandler + custom exceptions
│   ├── mapper/               # CarMapper, RentalMapper, UserMapper
│   ├── repository/           # CarRepository, RentalRepository, UserRepository
│   ├── security/             # JwtService, JwtAuthenticationFilter, CustomUserDetails
│   └── service/              # CarService, ImageService, RentalService,
│                             #   UserService, VerificationMailService
├── src/main/resources/
│   └── application.properties
├── frontend/
│   ├── src/
│   │   ├── api/              # axiosInstance, authApi, carApi, rentalApi, adminApi
│   │   ├── components/       # Navbar, CarCard, Alert, Spinner, StatusBadge, ProtectedRoute
│   │   ├── context/          # AuthContext, CarContext, RentalContext
│   │   └── pages/
│   │       ├── admin/        # AdminCarsPage, AdminRentalsPage, AdminUsersPage, CarFormModal
│   │       ├── CarDetailPage, CarsPage, LoginPage, MyRentalsPage,
│   │       │   ProfilePage, RegisterPage, VerifyEmailPage
│   ├── nginx.conf            # Nginx config for SPA + security headers
│   ├── vercel.json           # Vercel SPA rewrite rule
│   ├── Dockerfile            # Node 20 build → Nginx 1.25 serve
│   └── vite.config.js
├── Dockerfile                # Maven 3.9 build → JRE 21-alpine
├── docker-compose.yml
├── .env.example
├── Makefile
└── pom.xml
```

---

## 🌐 API Endpoints

### Public — no authentication required

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/auth/register` | Register a new user |
| `POST` | `/api/auth/login` | Login — returns JWT |
| `GET` | `/api/auth/verify-email?token=` | Verify email address |
| `POST` | `/api/auth/resend-verification` | Resend verification email |
| `GET` | `/api/cars` | List available cars (`?category=&fuelType=`) |
| `GET` | `/api/cars/available` | Date-range search (`?startDate=&endDate=`) |

### Authenticated — `Authorization: Bearer <token>` required

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/cars/{id}` | Car detail |
| `GET` | `/api/users/me` | Own profile |
| `PUT` | `/api/users/me` | Update profile |
| `POST` | `/api/rentals` | Book a car |
| `GET` | `/api/rentals/my` | Own rental history |
| `DELETE` | `/api/rentals/{id}` | Cancel rental (PENDING only) |

### Admin only — `ROLE_ADMIN` required

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/admin/users` | List all users |
| `PUT` | `/api/admin/users/{id}/license` | Approve / reject license |
| `POST` | `/api/admin/cars` | Create car (`multipart/form-data`: `car` + `image`) |
| `PUT` | `/api/admin/cars/{id}` | Update car details |
| `DELETE` | `/api/admin/cars/{id}` | Delete car |
| `PATCH` | `/api/admin/cars/{id}/status` | Update car status |
| `GET` | `/api/admin/rentals` | List all rentals |
| `PATCH` | `/api/admin/rentals/{id}/status` | Update rental status |

---

## 📊 Database Schema

### Entities

**`users`** — `id`, `email`, `password` (BCrypt), `firstName`, `lastName`, `role`, `emailVerified`, `verificationToken`, `licenseImageUrl`, `licenseStatus`

**`cars`** — `id`, `make`, `model`, `year`, `category`, `fuelType`, `pricePerDay`, `status`, `imageUrl`

**`rentals`** — `id`, `user_id`, `car_id`, `startDate`, `endDate`, `totalPrice`, `status`

### Enums

| Enum | Values |
|------|--------|
| `UserRole` | `USER`, `ADMIN` |
| `LicenseStatus` | `PENDING`, `APPROVED`, `REJECTED` |
| `CarStatus` | `AVAILABLE`, `RENTED`, `MAINTENANCE` |
| `CarCategory` | `SEDAN`, `SUV`, `HATCHBACK`, `CONVERTIBLE`, `TRUCK`, `VAN`, `COUPE` |
| `FuelType` | `PETROL`, `DIESEL`, `ELECTRIC`, `HYBRID` |
| `RentalStatus` | `PENDING`, `CONFIRMED`, `ACTIVE`, `COMPLETED`, `CANCELLED` |

---

## 🔒 Security

- JWT (HMAC-SHA) with configurable expiration, validated on every request via `JwtAuthenticationFilter`
- Passwords hashed with BCrypt
- Role-based access control via `@PreAuthorize` and Spring Security route rules
- CORS configured via `CORS_ALLOWED_ORIGINS` environment variable
- Nginx security headers: `X-Frame-Options`, `X-Content-Type-Options`, `X-XSS-Protection`
- Non-root Docker containers
- SQL injection prevention via JPA parameterized queries

---

## 📧 Email (Resend API)

The application sends transactional email through the [Resend](https://resend.com) REST API — **not SMTP**.

`VerificationMailService` calls `POST https://api.resend.com/emails` with your `RESEND_API_KEY`. The sender address (`MAIL_FROM`) must be a verified sender or domain on your Resend account. Verification tokens expire after `VERIFICATION_TOKEN_EXP_MINUTES` (default: 30 minutes).

---

## 🚢 Deployment

### CI (GitHub Actions)

Every push and pull request to any branch triggers:
- **Backend**: Java 17 + Maven, compiles and runs tests against NeonDB (secrets injected)
- **Frontend**: Node 22, `npm ci && npm run build`, uploads `dist/` as artifact

### CD (GitHub Actions → server)

Pushing to `main` (or manual dispatch):
1. SSHes into the production server
2. Creates a `docker-compose.override.yml` pointing to pre-built Docker Hub images (`navamsharma142/car-rental-backend:latest` / `navamsharma142/car-rental-frontend:latest`)
3. Pulls images and restarts the stack via `docker-compose.prod.yml`
4. Waits for `/actuator/health` to confirm a healthy deployment

### Frontend (Vercel)

`frontend/vercel.json` rewrites all routes to `/index.html` for client-side routing. Set `VITE_API_URL` in Vercel environment settings to point to your backend.

The current production backend URL is: `https://car-rental-backend-latest-gygt.onrender.com`

---

## 🐛 Troubleshooting

**Backend won't start**
- Check logs: `make logs-backend` or `docker-compose logs backend`
- Verify all environment variables in `.env` are set
- Confirm the database URL is reachable (`POSTGRES_DB`)

**Frontend can't reach the backend**
- Check `VITE_API_URL` — it must be set at build time for Vite
- Verify CORS: `CORS_ALLOWED_ORIGINS` must include the frontend origin
- Test backend directly: `curl http://localhost:8080/actuator/health`

**Email verification not arriving**
- Confirm `RESEND_API_KEY` is valid
- Ensure `MAIL_FROM` is a verified sender on your Resend account
- Check backend logs for HTTP errors from the Resend API call
- Verify `FRONTEND_URL` is set correctly so the link in the email points to the right address

**Image upload fails**
- Check Cloudinary credentials (`CLOUDINARY_USERNAME`, `CLOUDINARY_API_KEY`, `CLOUDINARY_API_SECRET`)
- Review backend logs for errors from the Cloudinary SDK

---

## 🧪 Testing

```bash
# Backend unit/integration tests
./mvnw test
# or
make test-backend
```

---

## 📝 License

This project is licensed under the MIT License.
