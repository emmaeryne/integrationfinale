package edu.emmapi.entities;

public enum Statut {
    ACTIF("Actif"),
    SUSPENDU("Suspendu"),
    TERMINE("Terminé"),
    EN_ATTENTE("En Attente");

    private final String displayName;

    Statut(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}