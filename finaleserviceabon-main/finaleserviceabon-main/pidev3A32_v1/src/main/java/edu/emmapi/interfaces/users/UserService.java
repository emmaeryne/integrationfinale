package edu.emmapi.interfaces.users;

import edu.emmapi.entities.users.Users;

import java.util.Optional;

public interface UserService {
    Users saveUser(Users user);
    Optional<Users> findByEmail(String email);
    boolean verifyPassword(String rawPassword, String hashedPassword);
    String hashPassword(String rawPassword);
}