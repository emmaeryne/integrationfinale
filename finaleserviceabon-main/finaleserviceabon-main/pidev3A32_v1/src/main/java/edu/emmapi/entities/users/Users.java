package edu.emmapi.entities.users;

public class Users {
    private Integer id;
    private String username;
    private String email;
    private String password_hash;
    private Boolean isActive = true;
    private String role = "CLIENT";

    public Users() {
        this.id = (int) (Math.random() * 1000000); // Simple ID generation for in-memory storage
    }

    // Getter and Setter for id
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    // Getter and Setter for username
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // Getter and Setter for email
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Getter and Setter for password
    public String getPassword() {
        return password_hash;
    }

    public void setPassword(String password) {
        this.password_hash= password;
    }

    // Getter and Setter for isActive
    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    // Getter and Setter for role
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}