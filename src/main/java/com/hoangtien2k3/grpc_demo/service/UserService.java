package com.hoangtien2k3.grpc_demo.service;

import com.hoangtien2k3.grpc_demo.entity.User;
import com.hoangtien2k3.grpc_demo.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(String name, String email, Integer age) {
        User user = new User(name, email, age);
        return userRepository.save(user);
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User updateUser(Long id, String name, String email, Integer age) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (name != null && !name.isEmpty()) {
                user.setName(name);
            }
            if (email != null && !email.isEmpty()) {
                user.setEmail(email);
            }
            if (age != null && age > 0) {
                user.setAge(age);
            }
            return userRepository.save(user);
        }
        return null;
    }

    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Page<User> listUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page > 0 ? page - 1 : 0, size > 0 ? size : 10);
        return userRepository.findAll(pageable);
    }
}
