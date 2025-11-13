# Online Payment System (OPS)

## Deskripsi Proyek

Online Payment System (OPS) adalah sistem pembayaran online mini project yang dibangun menggunakan Spring Boot. Sistem
ini menyediakan layanan pembayaran untuk berbagai jenis produk seperti PLN, Pulsa, Internet, BPJS, dan PDAM dengan
berbagai metode pembayaran seperti QRIS, Virtual Account, OVO, GOPAY, dll.

Proyek ini mengimplementasikan arsitektur event-driven dengan menggunakan RabbitMQ untuk pengiriman email notifikasi
secara asynchronous, serta fitur retry dan dead letter queue untuk menangani kegagalan pengiriman email.

## Stack Teknologi

### Backend Framework

- **Java**: 21
- **Spring Boot**: 3.5.7
- **Spring Framework**: 6.2.x

### Database & Persistence

- **PostgreSQL**: Database utama
- **H2 Database**: Untuk testing
- **Spring Data JPA**: ORM framework
- **Hibernate**: JPA implementation

### Messaging & Caching

- **RabbitMQ**: Message broker untuk email notifications
- **Redis**: Caching layer
- **Spring AMQP**: RabbitMQ integration

### Security & Authentication

- **Spring Security**: Framework keamanan
- **JWT (JJWT)**: JSON Web Token untuk authentication
- **BCrypt**: Password hashing

### Email & Template

- **Jakarta Mail**: Email sending
- **Thymeleaf**: Template engine untuk HTML email
- **Gmail SMTP**: Email provider

### API Documentation

- **SpringDoc OpenAPI**: Swagger UI untuk API documentation

### Build & Testing

- **Maven**: Build tool
- **JUnit 5**: Unit testing
- **Mockito**: Mocking framework
- **Spring Boot Test**: Integration testing

### Development Tools

- **Spring Boot DevTools**: Development utilities
- **Spring Boot Validation**: Input validation

## Arsitektur Sistem

### Komponen Utama

1. **User Management**
    - Customer registration & authentication
    - Gateway authentication (external system)
    - Admin roles (Super Admin, System Admin)

2. **Payment Processing**
    - Payment creation dengan berbagai metode
    - Payment status tracking (Processing, Success, Failed, Cancelled)
    - Payment history & filtering

3. **Email Notification System**
    - Forgot password emails
    - Payment success/failure notifications
    - Asynchronous processing dengan RabbitMQ

4. **Master Data Management**
    - Product Types (PLN, Pulsa, Internet, BPJS, PDAM)
    - Payment Types (QRIS, Virtual Account, OVO, GOPAY, dll.)
    - Payment Statuses
    - Role Types

### Arsitektur Event-Driven

```
User Request → Controller → Service → Repository
                              ↓
                       RabbitMQ Queue → Email Service
                              ↓
                       Dead Letter Queue (on failure)
```

### Database Schema

- **Users**: User management dengan role-based access
- **Payments**: Payment transactions
- **Payment_Types**: Master data metode pembayaran
- **Product_Types**: Master data jenis produk
- **Payment_Statuses**: Master data status pembayaran
- **Role_Types**: Master data roles

## Fitur Utama

### 1. User Management

- ✅ Customer registration dengan email verification
- ✅ Login dengan JWT authentication
- ✅ Gateway authentication dengan secret key
- ✅ Change password
- ✅ Forgot password dengan email reset
- ✅ Bulk user activation

### 2. Payment System

- ✅ Create payment dengan berbagai validasi
- ✅ Payment status updates (Gateway only)
- ✅ Payment cancellation
- ✅ Payment history dengan pagination & filtering
- ✅ Support multiple payment types & product types

### 3. Email Notifications

- ✅ Forgot password email dengan temporary password
- ✅ Payment success/failure notifications
- ✅ Asynchronous processing dengan retry mechanism
- ✅ Dead Letter Queue untuk failed messages

### 4. Security Features

- ✅ JWT-based authentication
- ✅ Role-based authorization (Customer, Gateway, Admin)
- ✅ Password encryption dengan BCrypt
- ✅ Input validation

### 5. API Features

- ✅ RESTful API design
- ✅ Swagger UI documentation
- ✅ Pagination support
- ✅ Error handling dengan proper HTTP status codes

## Struktur Proyek

```
ops/
├── src/
│   ├── main/
│   │   ├── java/com/dansmultipro/ops/
│   │   │   ├── OnlinePaymentSystemApplication.java
│   │   │   ├── config/
│   │   │   │   ├── RabbitConfig.java
│   │   │   │   ├── RedisConfig.java
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   └── SwaggerConfig.java
│   │   │   ├── constant/
│   │   │   ├── controller/
│   │   │   ├── dto/
│   │   │   ├── filter/
│   │   │   ├── model/
│   │   │   ├── pojo/
│   │   │   ├── repo/
│   │   │   ├── service/
│   │   │   │   ├── impl/
│   │   │   │   └── interface/
│   │   │   ├── specification/
│   │   │   ├── util/
│   │   │   └── validation/
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── database-script.sql
│   │       └── templates/
│   │           ├── email-forgot-password.html
│   │           └── email-payment.html
│   └── test/
│       ├── java/com/dansmultipro/ops/
│       │   └── integration/
│       └── resources/
│           └── application-test.properties
├── pom.xml
├── mvnw
├── mvnw.cmd
└── README.md
```

## Konfigurasi Environment

### Database (PostgreSQL)

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/db_ops
spring.datasource.username=postgres
spring.datasource.password=dans2025
```

### RabbitMQ

```properties
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest
```

### Redis

```properties
spring.data.redis.host=localhost
spring.data.redis.port=6379
```

### Email (Gmail SMTP)

```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
```

## Cara Menjalankan

### Prerequisites

- Java 21
- PostgreSQL
- RabbitMQ
- Redis
- Maven

### Setup Database

1. Buat database PostgreSQL: `db_ops`
2. Jalankan script: `src/main/resources/database-script.sql`

### Build & Run

```bash
# Build project
mvn clean install

# Run application
mvn spring-boot:run

# atau
./mvnw spring-boot:run
```

### Access Points

- **Application**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **H2 Console** (test): http://localhost:8080/h2-console

## Testing

### Unit Tests

```bash
mvn test
```

### Integration Tests

```bash
mvn test -Dtest="*IntegrationTest"
```

### Coverage Report

```bash
mvn test jacoco:report
```

## API Endpoints Utama

### Authentication

- `POST /api/auth/login` - Customer login
- `POST /api/auth/gateway/login` - Gateway login

### User Management

- `POST /api/users/register` - Register customer
- `GET /api/users` - Get all users (admin)
- `POST /api/users/forgot-password` - Forgot password
- `POST /api/users/change-password` - Change password

### Payment

- `POST /api/payments` - Create payment
- `PUT /api/payments/{id}/status` - Update payment status (gateway)
- `PUT /api/payments/{id}/cancel` - Cancel payment
- `GET /api/payments/history` - Get payment history

### Master Data

- `GET /api/payment-types` - Get payment types
- `GET /api/product-types` - Get product types
- `GET /api/payment-statuses` - Get payment statuses

## Error Handling & Resilience

### Retry Mechanism

- Email sending menggunakan Spring Retry dengan exponential backoff
- Failed messages dikirim ke Dead Letter Queue
- Configurable retry attempts

### Validation

- Input validation menggunakan Bean Validation
- Custom exceptions untuk business logic errors
- Proper HTTP status codes

### Security

- JWT token expiration
- Password strength requirements
- CORS configuration
- CSRF protection

## Monitoring & Logging

- **Spring Boot Actuator**: Health checks, metrics
- **SLF4J + Logback**: Logging framework
- **Structured Logging**: JSON format logs
- **Database Query Logging**: Hibernate SQL logging

## Deployment

### Docker Support

```dockerfile
FROM openjdk:21-jdk-slim
COPY target/ops-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
```

### Production Configuration

- External configuration dengan Spring Profiles
- Connection pooling untuk database
- SSL/TLS untuk email
- Environment-specific properties

## Contributing

1. Fork repository
2. Create feature branch
3. Add tests untuk new features
4. Ensure all tests pass
5. Submit pull request

## License

This project is part of Mini Project 2 - DansMultiPro assessment.</content>
<parameter name="filePath">C:\Users\SOLUSINDO\Downloads\ops\README.md
