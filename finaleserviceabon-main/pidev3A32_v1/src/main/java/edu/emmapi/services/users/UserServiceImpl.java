package edu.emmapi.services.users;

import edu.emmapi.entities.users.Users;
import edu.emmapi.interfaces.users.UserService;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.Optional;

public class UserServiceImpl implements UserService {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/hive3";
    private static final String DB_USER = "root"; // Replace with your MySQL username
    private static final String DB_PASSWORD = ""; // Replace with your MySQL password

    @Override
    public Users saveUser(Users user) {
        String sql = "INSERT INTO users (id, username, email, password_hash, is_active, role) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            user.setPassword(hashPassword(user.getPassword()));
            user.setId(generateUniqueId(conn)); // Generate unique ID
            stmt.setInt(1, user.getId());
            stmt.setString(2, user.getUsername());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPassword());
            stmt.setBoolean(5, user.getIsActive());
            stmt.setString(6, user.getRole());
            stmt.executeUpdate();
            return user;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving user: " + e.getMessage());
        }
    }

   @Override
    public Optional<Users> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Users user = new Users();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password_hash"));
                user.setIsActive(rs.getBoolean("is_active"));
                user.setRole(rs.getString("role"));
                return Optional.of(user);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding user by email: " + e.getMessage());
        }
    }

    @Override
    public boolean verifyPassword(String rawPassword, String hashedPassword) {
        return BCrypt.checkpw(rawPassword, hashedPassword);
    }

    @Override
    public String hashPassword(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
    }

    private int generateUniqueId(Connection conn) throws SQLException {
        String sql = "SELECT MAX(id) FROM users";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            int maxId = rs.next() ? rs.getInt(1) : 0;
            return maxId + 1;
        }
    }
}