package edu.emmapi.entities;

import java.util.Objects;

public class Coach extends user {
    private String specialty; // Spécialité du coach (ex: Fitness, Yoga, etc.)
    private int experienceYears; // Nombre d'années d'expérience
    private String certifications; // Certifications du coach

    // Constructeurs
    public Coach() {

    }

    public Coach(String specialty, int experienceYears, String certifications) {
        this.specialty = specialty;
        this.experienceYears = experienceYears;
        this.certifications = certifications;
    }

    public Coach(int id, String username, String email, String passwordHash, boolean isActive, String role, int securityQuestionId, String securityAnswer , String specialty, int experienceYears, String certifications) {
        super(id, username, email, passwordHash, isActive, role, securityQuestionId, securityAnswer);
        this.specialty = specialty;
        this.experienceYears = experienceYears;
        this.certifications = certifications;
    }

    public Coach(String username, String email, String passwordHash, boolean isActive, String role, int securityQuestionId, String securityAnswer, String specialty, int experienceYears, String certifications) {
        super(username, email, passwordHash, isActive, role, securityQuestionId, securityAnswer);
        this.specialty = specialty;
        this.experienceYears = experienceYears;
        this.certifications = certifications;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public int getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(int experienceYears) {
        this.experienceYears = experienceYears;
    }

    public String getCertifications() {
        return certifications;
    }

    public void setCertifications(String certifications) {
        this.certifications = certifications;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Coach coach = (Coach) o;
        return experienceYears == coach.experienceYears && Objects.equals(specialty, coach.specialty) && Objects.equals(certifications, coach.certifications);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), specialty, experienceYears, certifications);
    }

}