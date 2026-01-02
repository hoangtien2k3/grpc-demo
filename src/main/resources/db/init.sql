-- Tạo database
CREATE DATABASE grpc_demo;

-- Kết nối tới database grpc_demo
\c grpc_demo

-- Tạo table users
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    age INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Tạo index cho email
CREATE INDEX IF NOT EXISTS idx_email ON users(email);

-- Insert data mẫu
INSERT INTO users (name, email, age) VALUES
('Hoàng Tiến', 'hoangtien2k3@gmail.com', 25),
('Nguyễn Văn A', 'nguyenvana@gmail.com', 30),
('Trần Thị B', 'tranthib@gmail.com', 28),
('Lê Văn C', 'levanc@gmail.com', 35);
