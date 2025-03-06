package edu.emmapi.entities;

import java.util.Objects;

public class Owner extends user {
    private String officialId; // Numéro d'identification officiel (SIRET, etc.)

    // Constructeur par défaut
    public Owner() {
        super();
    }

    // Constructeur avec officialId uniquement
    public Owner(String officialId) {
        super();
        this.officialId = officialId;
    }

    // Constructeur avec tous les champs
    public Owner(int id, String username, String email, String passwordHash, boolean isActive, String role, int securityQuestionId, String securityAnswer, String officialId) {
        super(id, username, email, passwordHash, isActive, role, securityQuestionId, securityAnswer);
        this.officialId = officialId;
    }

    // Constructeur sans ID (pour la création)
    public Owner(String username, String email, String passwordHash, boolean isActive, String role, int securityQuestionId, String securityAnswer, String officialId) {
        super(username, email, passwordHash, isActive, role, securityQuestionId, securityAnswer);
        this.officialId = officialId;
    }

    // Getters et setters
    public String getOfficialId() {
        return officialId;
    }

    public void setOfficialId(String officialId) {
        this.officialId = officialId;
    }

    // Méthode toString()
    @Override
    public String toString() {
        return super.toString() + ", Owner{" +
                "officialId='" + officialId + '\'' +
                '}';
    }

    // Méthode equals()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Owner owner = (Owner) o;
        return Objects.equals(officialId, owner.officialId);
    }

    // Méthode hashCode()
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), officialId);
    }
}