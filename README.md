# gRPC Demo Project

Dự án demo về **gRPC Service** với Spring Boot 4.0, JPA/Hibernate và PostgreSQL 16.

## 📋 Mô Tả Dự Án

Đây là một ứng dụng Spring Boot sử dụng **gRPC** (thay vì HTTP REST) để cung cấp các API CRUD cho quản lý người dùng (User Management).

### Công Nghệ Sử Dụng
- **Framework**: Spring Boot 4.0.1
- **gRPC**: Protocol Buffers 3 + gRPC Java
- **Database**: PostgreSQL 16+
- **ORM**: JPA/Hibernate 7.2
- **Build Tool**: Gradle
- **Java Version**: Java 17+

## 🚀 Chuẩn Bị

### Yêu Cầu Hệ Thống
- Java 17 hoặc cao hơn
- PostgreSQL 16 (chạy trên Docker hoặc cài đặt trực tiếp)
- Gradle 8.0+ (hoặc dùng `./gradlew`)
- Go 1.16+ (để cài grpcui - tùy chọn)

### 1. Khởi Động PostgreSQL với Docker

```bash
# Chạy PostgreSQL 16 trên Docker
docker run --name postgres-grpc \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=grpc_demo \
  -p 5432:5432 \
  -d postgres:16-alpine
```

### 2. Tạo Database và Bảng

```bash
# Kết nối vào PostgreSQL
docker exec -it postgres-grpc psql -U postgres

# Chạy các lệnh SQL sau:
```

```sql
CREATE DATABASE grpc_demo;

\c grpc_demo

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    age INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_email ON users(email);

-- Thêm dữ liệu mẫu
INSERT INTO users (name, email, age) VALUES
('Hoàng Tiến', 'hoangtien2k3@gmail.com', 25),
('Nguyễn Văn A', 'nguyenvana@gmail.com', 30),
('Trần Thị B', 'tranthib@gmail.com', 28),
('Lê Văn C', 'levanc@gmail.com', 35);
```

## 📁 Cấu Trúc Dự Án

```
src/main/
├── java/com/hoangtien2k3/grpc_demo/
│   ├── entity/
│   │   └── User.java                    # JPA Entity
│   ├── repository/
│   │   └── UserRepository.java          # Data access layer
│   ├── service/
│   │   └── UserService.java             # Business logic
│   ├── grpc/
│   │   └── UserServiceGrpc.java         # gRPC service implementation
│   └── GrpcDemoApplication.java         # Spring Boot main class
├── proto/
│   └── com/hoangtien2k3/grpc_demo/
│       └── user.proto                   # Protocol Buffer definition
└── resources/
    ├── application.yaml                 # Spring Boot configuration
    └── db/
        └── init.sql                     # Database initialization script
```

## 🏃 Chạy Ứng Dụng

### 1. Build Project

```bash
# Sử dụng gradlew (khuyến nghị)
./gradlew clean build -x test

# Hoặc nếu đã cài Gradle
gradle clean build -x test
```

### 2. Chạy Server

```bash
# Sử dụng gradlew
./gradlew bootRun

# Hoặc chạy JAR sau khi build
java -jar build/libs/grpc-demo-0.0.1-SNAPSHOT.jar
```

**Output:**
```
...
2026-01-02T14:07:00.000+07:00  INFO ... : Started GrpcDemoApplication in 5.123 seconds
2026-01-02T14:07:00.000+07:00  INFO ... : grpc server started, listening on port: 9090
```

✅ Server gRPC đang lắng nghe trên **port 9090**

## 🧪 Test gRPC Services

### Cách 1: Dùng grpcui (Web UI)

```bash
# 1. Cài đặt grpcui
go install github.com/fullstorydev/grpcui/cmd/grpcui@latest

# 2. Chạy grpcui
grpcui -plaintext localhost:9090

# 3. Mở trình duyệt: http://localhost:8080
```

Tại grpcui UI, bạn có thể:
- Chọn service: `com.hoangtien2k3.grpc_demo.UserService`
- Chọn method và nhập parameters
- Xem response

### Cách 2: Dùng grpcurl (Command Line)

```bash
# 1. Cài đặt grpcurl
go install github.com/fullstorydev/grpcurl/cmd/grpcurl@latest

# 2. List tất cả services
grpcurl -plaintext localhost:9090 list

# 3. Test các API:

# GetUser (lấy user có ID = 1)
grpcurl -plaintext -d '{"id": 1}' localhost:9090 com.hoangtien2k3.grpc_demo.UserService/GetUser

# CreateUser (tạo user mới)
grpcurl -plaintext -d '{
  "name": "John Doe",
  "email": "john@example.com",
  "age": 28
}' localhost:9090 com.hoangtien2k3.grpc_demo.UserService/CreateUser

# UpdateUser (cập nhật user)
grpcurl -plaintext -d '{
  "id": 1,
  "name": "Updated Name",
  "email": "updated@example.com",
  "age": 26
}' localhost:9090 com.hoangtien2k3.grpc_demo.UserService/UpdateUser

# DeleteUser (xóa user)
grpcurl -plaintext -d '{"id": 1}' localhost:9090 com.hoangtien2k3.grpc_demo.UserService/DeleteUser

# ListUsers (lấy danh sách user, phân trang)
grpcurl -plaintext -d '{
  "page": 1,
  "size": 10
}' localhost:9090 com.hoangtien2k3.grpc_demo.UserService/ListUsers
```

## 📡 gRPC API Reference

### 1. GetUser
Lấy thông tin của một người dùng theo ID

**Request:**
```protobuf
message GetUserRequest {
  int64 id = 1;
}
```

**Response:**
```protobuf
message UserResponse {
  int64 id = 1;
  string name = 2;
  string email = 3;
  int32 age = 4;
  string created_at = 5;
  string updated_at = 6;
}
```

### 2. CreateUser
Tạo một người dùng mới

**Request:**
```protobuf
message CreateUserRequest {
  string name = 1;
  string email = 2;
  int32 age = 3;
}
```

**Response:** `UserResponse`

### 3. UpdateUser
Cập nhật thông tin người dùng

**Request:**
```protobuf
message UpdateUserRequest {
  int64 id = 1;
  string name = 2;
  string email = 3;
  int32 age = 4;
}
```

**Response:** `UserResponse`

### 4. DeleteUser
Xóa một người dùng

**Request:**
```protobuf
message DeleteUserRequest {
  int64 id = 1;
}
```

**Response:**
```protobuf
message DeleteUserResponse {
  bool success = 1;
  string message = 2;
}
```

### 5. ListUsers
Lấy danh sách người dùng (có phân trang)

**Request:**
```protobuf
message ListUsersRequest {
  int32 page = 1;
  int32 size = 2;
}
```

**Response:**
```protobuf
message ListUsersResponse {
  repeated UserResponse users = 1;
  int32 total = 2;
}
```

## ⚙️ Cấu Hình

### application.yaml

```yaml
grpc:
  server:
    port: 9090                          # gRPC server port
    enable-keep-alive: true             # Bật keep-alive
    keep-alive-time: 30s                # Gửi keep-alive mỗi 30s
    keep-alive-timeout: 10s             # Timeout 10s
    permit-keep-alive-without-calls: true # Cho phép keep-alive khi không có request

spring:
  application:
    name: grpc-demo
  datasource:
    url: jdbc:postgresql://localhost:5432/grpc_demo
    username: postgres
    password: postgres                  # ⚠️ Đổi password nếu cần
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: update                  # Tự động tạo/update bảng
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true
    show-sql: true                      # In SQL query ra console
```

### Thay Đổi Cấu Hình

Nếu PostgreSQL chạy trên host/port khác, sửa trong `src/main/resources/application.yaml`:

```yaml
datasource:
  url: jdbc:postgresql://YOUR_HOST:YOUR_PORT/grpc_demo
  username: YOUR_USERNAME
  password: YOUR_PASSWORD
```

## 🔧 Build & Compile

### Compile Proto Files

Proto files sẽ được tự động compile khi build:

```bash
./gradlew clean build
```

Generated files sẽ được tạo trong `build/generated/source/proto/main/`:
- `UserServiceGrpc.java` (gRPC stub)
- `UserProto.java` (Proto message classes)

### View Generated Files

```bash
ls -la build/generated/source/proto/main/java/com/hoangtien2k3/grpc_demo/proto/
```

## 📊 Database Schema

```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    age INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_email ON users(email);
```

## 🐛 Troubleshooting

### Lỗi: Connection refused

```
org.postgresql.util.PSQLException: Connection to localhost:5432 refused
```

**Giải pháp:**
```bash
# Kiểm tra PostgreSQL đang chạy
docker ps | grep postgres

# Nếu chưa chạy, khởi động lại
docker start postgres-grpc
```

### Lỗi: Password authentication failed

**Giải pháp:**
Kiểm tra password trong `application.yaml` khớp với password PostgreSQL

### Lỗi: Build thất bại

```bash
# Clean và rebuild
./gradlew clean build -x test --no-build-cache
```

## 📚 Tài Liệu Tham Khảo

- [gRPC Java Documentation](https://grpc.io/docs/languages/java/)
- [Protocol Buffers v3 Guide](https://developers.google.com/protocol-buffers/docs/proto3)
- [Spring Boot gRPC Starter](https://github.com/grpc-ecosystem/grpc-spring)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)

## 📝 Lưu Ý

- gRPC sử dụng **HTTP/2** làm transport protocol
- Dữ liệu được serialize bằng **Protocol Buffers** (nhỏ hơn JSON, nhanh hơn)
- Hỗ trợ **Bidirectional Streaming** (nâng cao)
- Không cần REST API - tối ưu hóa cho microservices

## 👤 Tác Giả

Hoàng Tiến - [hoangtien2k3@gmail.com](mailto:hoangtien2k3@gmail.com)

---

**Enjoy gRPC! 🚀**
