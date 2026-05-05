.PHONY: help build up down restart logs clean db-backup db-restore test

# Default target
help:
	@echo "Car Rental System - Docker Commands"
	@echo ""
	@echo "Development:"
	@echo "  make build          - Build all Docker images"
	@echo "  make up             - Start all services"
	@echo "  make down           - Stop all services"
	@echo "  make restart        - Restart all services"
	@echo "  make logs           - View logs (all services)"
	@echo "  make logs-backend   - View backend logs"
	@echo "  make logs-frontend  - View frontend logs"
	@echo "  make logs-db        - View database logs"
	@echo ""
	@echo "Database:"
	@echo "  make db-shell       - Connect to PostgreSQL shell"
	@echo "  make db-backup      - Backup database"
	@echo "  make db-restore     - Restore database from backup"
	@echo ""
	@echo "Cleanup:"
	@echo "  make clean          - Stop and remove all containers, networks"
	@echo "  make clean-all      - Stop and remove everything including volumes"
	@echo ""
	@echo "Testing:"
	@echo "  make test-backend   - Run backend tests"
	@echo "  make test-frontend  - Run frontend tests"
	@echo ""
	@echo "Production:"
	@echo "  make prod-build     - Build for production"
	@echo "  make prod-up        - Start production services"
	@echo "  make prod-down      - Stop production services"

# Development commands
build:
	docker-compose build

up:
	docker-compose up -d
	@echo "Services started!"
	@echo "Frontend: http://localhost:3000"
	@echo "Backend: http://localhost:8080"

down:
	docker-compose down

restart:
	docker-compose restart

logs:
	docker-compose logs -f

logs-backend:
	docker-compose logs -f backend

logs-frontend:
	docker-compose logs -f frontend

logs-db:
	docker-compose logs -f postgres

# Database commands
db-shell:
	docker exec -it car-rental-db psql -U postgres -d car_rental_db

db-backup:
	@mkdir -p backups
	docker exec car-rental-db pg_dump -U postgres car_rental_db > backups/backup_$$(date +%Y%m%d_%H%M%S).sql
	@echo "Database backed up to backups/"

db-restore:
	@read -p "Enter backup file name: " file; \
	docker exec -i car-rental-db psql -U postgres car_rental_db < backups/$$file

# Cleanup commands
clean:
	docker-compose down --remove-orphans
	docker system prune -f

clean-all:
	docker-compose down -v --remove-orphans
	docker system prune -af
	@echo "⚠️  All data has been removed!"

# Testing commands
test-backend:
	./mvnw test

test-frontend:
	cd frontend && npm test

# Production commands
prod-build:
	docker-compose -f docker-compose.prod.yml build

prod-up:
	docker-compose -f docker-compose.prod.yml up -d
	@echo "Production services started!"

prod-down:
	docker-compose -f docker-compose.prod.yml down

prod-logs:
	docker-compose -f docker-compose.prod.yml logs -f

# Development helpers
dev-backend:
	./mvnw spring-boot:run

dev-frontend:
	cd frontend && npm run dev

install-frontend:
	cd frontend && npm install

install-backend:
	./mvnw dependency:resolve
