# 🚗 Car Rental System

A full-stack car rental application with Spring Boot backend and React frontend, containerized with Docker.

## 🏗️ Architecture

- **Backend**: Spring Boot 4.0.6 + PostgreSQL + JWT Authentication
- **Frontend**: React 18 + Vite + React Router + Context API
- **Infrastructure**: Docker + Docker Compose
- **Image Storage**: Cloudinary
- **Email**: SMTP (Gmail/custom)

## 📋 Features

### User Features
- User registration with email verification
- JWT-based authentication
- Browse available cars with filters (category, fuel type, date range)
- Book cars with automatic price calculation
- View and cancel bookings
- Profile management with license upload

### Admin Features
- Manage cars (CRUD operations with image upload)
- Manage rentals (approve, confirm, complete)
- Manage users (approve/reject driver licenses)
- Update car and rental statuses

## 🚀 Quick Start with Docker

### Prerequisites
- Docker Desktop installed
- Docker Compose installed
- Cloudinary account (free tier available)
- Gmail account with App Password (for email verification)

### 1. Clone and Setup

```bash
git clone <your-repo-url>
cd car-rental
```

### 2. Configure Environment Variables

Copy the example environment file:

```bash
cp .env.example .env
```

Edit `.env` and fill in your credentials:

```env
# Database
POSTGRES_PASSWORD=your_secure_password

# JWT Secret (generate a secure random string)
JWT_SECRET=your_jwt_secret_minimum_256_bits

# Email (Gmail example)
MAIL_USERNAME=your_email@gmail.com
MAIL_PASSWORD=your_gmail_app_password

# Cloudinary
CLOUDINARY_USERNAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_api_secret
```

#### 📧 Gmail Setup
1. Enable 2-Factor Authentication on your Google account
2. Go to [App Passwords](https://myaccount.google.com/apppasswords)
3. Generate a new app password for "Mail"
4. Use this password in `MAIL_PASSWORD`

#### ☁️ Cloudinary Setup
1. Sign up at [cloudinary.com](https://cloudinary.com)
2. Get your credentials from the dashboard
3. Add them to your `.env` file

### 3. Build and Run

```bash
# Build and start all services
docker-compose up --build

# Or run in detached mode
docker-compose up -d --build
```

### 4. Access the Application

- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080/api
- **Database**: localhost:5432

### 5. Create Admin User

The first registered user will be a regular user. To create an admin:

**Option 1: Via Database**
```bash
# Connect to the database
docker exec -it car-rental-db psql -U postgres -d car_rental_db

# Update user role
UPDATE users SET role = 'ADMIN' WHERE email = 'your_email@example.com';
```

**Option 2: Via Application Code**
Modify `UserService.java` temporarily to set role as ADMIN for specific email.

## 🛠️ Development

### Run Without Docker

#### Backend
```bash
# Set environment variables or use application.properties
export JWT_SECRET=your_secret
export MAIL_USERNAME=your_email
# ... other vars

# Run with Maven
./mvnw spring-boot:run
```

#### Frontend
```bash
cd frontend
npm install
npm run dev
```

### Docker Commands

```bash
# Start services
docker-compose up

# Stop services
docker-compose down

# Stop and remove volumes (⚠️ deletes database data)
docker-compose down -v

# View logs
docker-compose logs -f

# View specific service logs
docker-compose logs -f backend
docker-compose logs -f frontend

# Rebuild specific service
docker-compose up --build backend

# Execute commands in containers
docker exec -it car-rental-backend bash
docker exec -it car-rental-db psql -U postgres -d car_rental_db
```

## 📁 Project Structure

```
car-rental/
├── src/                          # Spring Boot backend
│   └── main/
│       ├── java/com/example/carrental/
│       │   ├── config/          # Security, Cloudinary config
│       │   ├── controller/      # REST endpoints
│       │   ├── dto/             # Request/Response objects
│       │   ├── entity/          # JPA entities
│       │   ├── repository/      # Data access
│       │   ├── service/         # Business logic
│       │   └── security/        # JWT, filters
│       └── resources/
│           └── application.properties
├── frontend/                     # React frontend
│   ├── src/
│   │   ├── api/                 # Axios API clients
│   │   ├── components/          # Reusable components
│   │   ├── context/             # Context API state
│   │   ├── pages/               # Route pages
│   │   └── App.jsx
│   ├── Dockerfile
│   └── nginx.conf
├── docker-compose.yml
├── Dockerfile
├── .env.example
└── README.md
```

## 🔒 Security

- JWT tokens with configurable expiration
- Password hashing with BCrypt
- CORS configuration
- SQL injection prevention via JPA
- XSS protection headers in Nginx
- Non-root Docker containers
- Environment variable secrets

## 🧪 Testing

```bash
# Backend tests
./mvnw test

# Frontend tests
cd frontend
npm test
```

## 📊 Database Schema

### Main Tables
- **users**: User accounts with roles and license info
- **cars**: Car inventory with details and images
- **rentals**: Booking records with status tracking

### Enums
- **UserRole**: USER, ADMIN
- **LicenseStatus**: PENDING, APPROVED, REJECTED
- **CarStatus**: AVAILABLE, RENTED, MAINTENANCE
- **CarCategory**: SEDAN, SUV, HATCHBACK, CONVERTIBLE, TRUCK, VAN, COUPE
- **FuelType**: PETROL, DIESEL, ELECTRIC, HYBRID
- **RentalStatus**: PENDING, CONFIRMED, ACTIVE, COMPLETED, CANCELLED

## 🌐 API Endpoints

### Public
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login
- `GET /api/auth/verify-email?token=` - Verify email
- `GET /api/cars` - Browse available cars
- `GET /api/cars/available?startDate=&endDate=` - Search by dates

### Authenticated
- `GET /api/users/me` - Get profile
- `PUT /api/users/me` - Update profile
- `GET /api/cars/{id}` - Car details
- `POST /api/rentals` - Book a car
- `GET /api/rentals/my` - My rentals
- `DELETE /api/rentals/{id}` - Cancel rental

### Admin Only
- `GET /api/admin/users` - List all users
- `PUT /api/admin/users/{id}/license` - Update license status
- `POST /api/admin/cars` - Create car (multipart/form-data)
- `PUT /api/admin/cars/{id}` - Update car
- `DELETE /api/admin/cars/{id}` - Delete car
- `PATCH /api/admin/cars/{id}/status` - Update car status
- `GET /api/admin/rentals` - List all rentals
- `PATCH /api/admin/rentals/{id}/status` - Update rental status

## 🐛 Troubleshooting

### Backend won't start
- Check database connection in logs: `docker-compose logs backend`
- Verify environment variables are set correctly
- Ensure PostgreSQL is healthy: `docker-compose ps`

### Frontend can't connect to backend
- Check `VITE_API_URL` in `.env`
- Verify backend is running: `curl http://localhost:8080/actuator/health`
- Check CORS configuration in `SecurityConfig.java`

### Email verification not working
- Verify Gmail App Password is correct
- Check mail logs: `docker-compose logs backend | grep mail`
- Ensure `APP_VERIFICATION_URL` points to frontend

### Image upload fails
- Verify Cloudinary credentials
- Check file size limits
- Review backend logs for Cloudinary errors

## 📝 License

This project is licensed under the MIT License.

## 👥 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Open a Pull Request

## 📧 Support

For issues and questions, please open a GitHub issue.
