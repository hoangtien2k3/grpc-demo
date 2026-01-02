package com.hoangtien2k3.grpc_demo.grpc;

import com.hoangtien2k3.grpc_demo.entity.User;
import com.hoangtien2k3.grpc_demo.proto.*;
import com.hoangtien2k3.grpc_demo.service.UserService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.data.domain.Page;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

@GrpcService
public class UserGrpcService extends UserServiceGrpc.UserServiceImplBase {

    private final UserService userService;
    
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public UserGrpcService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void getUser(GetUserRequest request, StreamObserver<UserResponse> responseObserver) {
        try {
            long userId = request.getId();
            if (userId <= 0) {
                responseObserver.onError(Status.INVALID_ARGUMENT
                        .withDescription("User ID must be positive")
                        .asException());
                return;
            }
            Optional<User> user = userService.getUserById(userId);
            if (user.isPresent()) {
                UserResponse response = convertToProto(user.get());
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            } else {
                responseObserver.onError(Status.NOT_FOUND
                        .withDescription("User not found with id: " + userId)
                        .asException());
            }
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Error: " + e.getMessage())
                    .asException());
        }
    }
    
    @Override
    public void createUser(CreateUserRequest request, StreamObserver<UserResponse> responseObserver) {
        try {
            String name = request.getName();
            String email = request.getEmail();
            int age = request.getAge();
            if (name.isEmpty()) {
                responseObserver.onError(Status.INVALID_ARGUMENT
                        .withDescription("Name is required")
                        .asException());
                return;
            }
            if (email.isEmpty()) {
                responseObserver.onError(Status.INVALID_ARGUMENT
                        .withDescription("Email is required")
                        .asException());
                return;
            }
            if (age <= 0 || age > 150) {
                responseObserver.onError(Status.INVALID_ARGUMENT
                        .withDescription("Age must be between 1 and 150")
                        .asException());
                return;
            }
            User user = userService.createUser(name, email, age);
            UserResponse response = convertToProto(user);
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Error: " + e.getMessage())
                    .asException());
        }
    }
    
    @Override
    public void updateUser(UpdateUserRequest request, StreamObserver<UserResponse> responseObserver) {
        try {
            long userId = request.getId();
            String name = request.getName();
            String email = request.getEmail();
            int age = request.getAge();
            if (userId <= 0) {
                responseObserver.onError(Status.INVALID_ARGUMENT
                        .withDescription("User ID must be positive")
                        .asException());
                return;
            }
            User user = userService.updateUser(userId, name, email, age > 0 ? age : null);
            if (user != null) {
                UserResponse response = convertToProto(user);
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            } else {
                responseObserver.onError(Status.NOT_FOUND
                        .withDescription("User not found with id: " + userId)
                        .asException());
            }
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Error: " + e.getMessage())
                    .asException());
        }
    }
    
    @Override
    public void deleteUser(DeleteUserRequest request, StreamObserver<DeleteUserResponse> responseObserver) {
        try {
            long userId = request.getId();
            if (userId <= 0) {
                responseObserver.onError(Status.INVALID_ARGUMENT
                        .withDescription("User ID must be positive")
                        .asException());
                return;
            }
            boolean deleted = userService.deleteUser(userId);
            DeleteUserResponse response = DeleteUserResponse.newBuilder()
                    .setSuccess(deleted)
                    .setMessage(deleted ? "User deleted successfully" : "User not found")
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Error: " + e.getMessage())
                    .asException());
        }
    }
    
    @Override
    public void listUsers(ListUsersRequest request, StreamObserver<ListUsersResponse> responseObserver) {
        try {
            int page = request.getPage();
            int size = request.getSize();
            Page<User> users = userService.listUsers(page, size);
            ListUsersResponse.Builder builder = ListUsersResponse.newBuilder();
            builder.setTotal((int) users.getTotalElements());
            for (User user : users.getContent()) {
                builder.addUsers(convertToProto(user));
            }
            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Error: " + e.getMessage())
                    .asException());
        }
    }
    
    private UserResponse convertToProto(User user) {
        String createdAt = user.getCreatedAt() != null ? 
                user.getCreatedAt().format(formatter) : "";
        String updatedAt = user.getUpdatedAt() != null ? 
                user.getUpdatedAt().format(formatter) : "";
        return UserResponse.newBuilder()
                .setId(user.getId())
                .setName(user.getName())
                .setEmail(user.getEmail())
                .setAge(user.getAge() != null ? user.getAge() : 0)
                .setCreatedAt(createdAt)
                .setUpdatedAt(updatedAt)
                .build();
    }
}
