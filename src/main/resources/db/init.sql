-- create database
CREATE DATABASE grpc_demo;

-- connect to database
\c grpc_demo

-- create table users
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    age INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- create index on email
CREATE INDEX IF NOT EXISTS idx_email ON users(email);

-- Insert data sample into users table
INSERT INTO users (name, email, age) VALUES
('Hoàng Tiến', 'hoangtien2k3@gmail.com', 25),
('Nguyễn Văn A', 'nguyenvana@gmail.com', 30),
('Trần Thị B', 'tranthib@gmail.com', 28),
('Lê Văn C', 'levanc@gmail.com', 35);
