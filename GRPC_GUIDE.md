# gRPC Demo Project

Dự án demo về gRPC Service với Spring Boot, JPA và MySQL.

## Cấu Trúc Dự Án

```
src/main/
├── java/com/hoangtien2k3/grpc_demo/
│   ├── entity/          # Entity class (User)
│   ├── repository/      # Data access layer
│   ├── service/         # Business logic
│   ├── grpc/           # gRPC service implementation
│   └── GrpcDemoApplication.java
├── proto/
│   └── com/hoangtien2k3/grpc_demo/
│       └── user.proto   # Protocol Buffer definition
└── resources/
    ├── application.yaml # Spring Boot configuration
    └── db/init.sql     # Database initialization script
```

## Yêu Cầu

- Java 17+
- MySQL 8.0+
- Gradle 8.0+

## Cài Đặt

### 1. Tạo Database

```bash
mysql -u root -p < src/main/resources/db/init.sql
```

Hoặc chạy lệnh SQL trực tiếp trong MySQL:

```sql
CREATE DATABASE IF NOT EXISTS grpc_demo;
USE grpc_demo;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    age INT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email (email)
);

INSERT INTO users (name, email, age) VALUES
('Hoàng Tiến', 'hoangtien2k3@gmail.com', 25),
('Nguyễn Văn A', 'nguyenvana@gmail.com', 30),
('Trần Thị B', 'tranthib@gmail.com', 28),
('Lê Văn C', 'levanc@gmail.com', 35);
```

### 2. Cập Nhật Database Configuration

Mở `src/main/resources/application.yaml` và cập nhật thông tin kết nối MySQL:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/grpc_demo?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
    username: root        # Thay đổi username MySQL của bạn
    password: root        # Thay đổi password MySQL của bạn
```

### 3. Build Project

```bash
./gradlew clean build
```

### 4. Chạy Application

```bash
./gradlew bootRun
```

Server sẽ chạy trên cổng **9090** (gRPC port).

## gRPC Services

### 1. GetUser - Lấy thông tin người dùng

```proto
rpc GetUser (GetUserRequest) returns (UserResponse) {}

message GetUserRequest {
  int64 id = 1;
}
```

**Ví dụ:**
```
Request: GetUserRequest { id: 1 }
Response: UserResponse { id: 1, name: "Hoàng Tiến", email: "hoangtien2k3@gmail.com", age: 25, ... }
```

### 2. CreateUser - Tạo người dùng mới

```proto
rpc CreateUser (CreateUserRequest) returns (UserResponse) {}

message CreateUserRequest {
  string name = 1;
  string email = 2;
  int32 age = 3;
}
```

**Ví dụ:**
```
Request: CreateUserRequest { name: "John Doe", email: "john@example.com", age: 30 }
Response: UserResponse { id: 5, name: "John Doe", email: "john@example.com", age: 30, ... }
```

### 3. UpdateUser - Cập nhật thông tin người dùng

```proto
rpc UpdateUser (UpdateUserRequest) returns (UserResponse) {}

message UpdateUserRequest {
  int64 id = 1;
  string name = 2;
  string email = 3;
  int32 age = 4;
}
```

### 4. DeleteUser - Xóa người dùng

```proto
rpc DeleteUser (DeleteUserRequest) returns (DeleteUserResponse) {}

message DeleteUserRequest {
  int64 id = 1;
}

message DeleteUserResponse {
  bool success = 1;
  string message = 2;
}
```

### 5. ListUsers - Danh sách người dùng (phân trang)

```proto
rpc ListUsers (ListUsersRequest) returns (ListUsersResponse) {}

message ListUsersRequest {
  int32 page = 1;
  int32 size = 2;
}

message ListUsersResponse {
  repeated UserResponse users = 1;
  int32 total = 2;
}
```

**Ví dụ:**
```
Request: ListUsersRequest { page: 1, size: 10 }
Response: ListUsersResponse { users: [...], total: 4 }
```

## Testing với grpcui

### 1. Cài đặt grpcui

```bash
go install github.com/fullstorydev/grpcui/cmd/grpcui@latest
```

### 2. Chạy grpcui

```bash
grpcui -plaintext localhost:9090
```

Sau đó mở trình duyệt và truy cập `http://localhost:8080`

## Database Schema

```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    age INT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email (email)
);
```

## Project Structure

- **Entity**: `User.java` - Định nghĩa entity với JPA
- **Repository**: `UserRepository.java` - Data access layer
- **Service**: `UserService.java` - Business logic
- **gRPC Service**: `UserServiceGrpc.java` - gRPC server implementation
- **Proto**: `user.proto` - Protocol Buffer definition

## Dependencies

```gradle
implementation 'io.grpc:grpc-services'
implementation 'org.springframework.grpc:spring-grpc-spring-boot-starter'
implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
implementation 'com.mysql:mysql-connector-j'
implementation 'org.springframework.boot:spring-boot-starter-validation'
```

## Notes

- gRPC Server chạy trên port **9090**
- HTTP/2 được sử dụng để truyền tải
- Tất cả dữ liệu được định serialization bằng Protocol Buffers
- Database sử dụng MySQL 8.0+
- Hỗ trợ Keep-Alive để duy trì kết nối lâu dài
