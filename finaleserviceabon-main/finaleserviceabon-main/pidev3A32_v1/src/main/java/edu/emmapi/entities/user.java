package edu.emmapi.entities;

import java.util.Objects;

public class user {
    private int id; // Identifiant unique de l'utilisateur
    private String username; // Nom d'utilisateur
    private String email; // Adresse e-mail
    private String passwordHash; // Mot de passe hashé
    private static boolean isActive; // Statut actif ou désactivé
    private String role; // Rôle de l'utilisateur
    private int securityQuestionId;
    private String securityAnswer;

    public user() {
    }

    public user(int id, String username, String email, String passwordHash,boolean isActive, String role, int securityQuestionId, String securityAnswer) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.isActive = isActive;
        this.role = role;
        this.securityQuestionId = securityQuestionId;
        this.securityAnswer = securityAnswer;
    }

    public user(String username, String passwordHash, String email,boolean isActive, String role, int securityQuestionId, String securityAnswer) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.role = role;
        this.isActive = isActive;
        this.securityQuestionId = securityQuestionId;
        this.securityAnswer = securityAnswer;
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public static boolean isIsActive() {
        return isActive;
    }

    public static void setIsActive(boolean isActive) {
        user.isActive = isActive;
    }

    public int getSecurityQuestionId() {
        return securityQuestionId;
    }

    public void setSecurityQuestionId(int securityQuestionId) {
        this.securityQuestionId = securityQuestionId;
    }

    public String getSecurityAnswer() {
        return securityAnswer;
    }

    public void setSecurityAnswer(String securityAnswer) {
        this.securityAnswer = securityAnswer;
    }

    public static boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "user{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", passwordHash='" + passwordHash + '\'' +
                ", role='" + role + '\'' +
                ", securityQuestionId=" + securityQuestionId +
                ", securityAnswer='" + securityAnswer + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        user user = (user) o;
        return id == user.id && securityQuestionId == user.securityQuestionId && Objects.equals(username, user.username) && Objects.equals(email, user.email) && Objects.equals(passwordHash, user.passwordHash) && Objects.equals(role, user.role) && Objects.equals(securityAnswer, user.securityAnswer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, email, passwordHash, role, securityQuestionId, securityAnswer);
    }
}