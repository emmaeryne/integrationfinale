package edu.emmapi.entities;

import java.sql.Date; // Utilisation de java.sql.Date pour la compatibilité avec SQL
import java.util.Objects;

public class Planning {
    private int id_planning;
    private Date date_planning; // Utilisation de java.sql.Date pour la compatibilité avec SQL
    private String type_activite;
    private String status;
    private int cours; // ID du cours associé
    private boolean confirmed;
    // Constructeur pour créer un objet Planning avec ID de cours
    public Planning(String type_activite, Date date_planning, String status, int cours) {
        this.type_activite = type_activite;
        this.date_planning = date_planning;
        this.status = status;
        this.cours = cours;
    }

    // Constructeur pour créer un objet Planning avec ID de cours et ID de planning
    public Planning(int id_planning, String type_activite, Date date_planning, String status, int cours) {
        this.id_planning = id_planning;
        this.type_activite = type_activite;
        this.date_planning = date_planning;
        this.status = status;
        this.cours = cours;
    }

    // Getters et Setters
    public int getId_planning() {
        return id_planning;
    }

    public void setId_planning(int id_planning) {
        this.id_planning = id_planning;
    }

    public Date getDate_planning() {
        return date_planning;
    }

    public void setDate_planning(Date date_planning) {
        this.date_planning = date_planning;
    }

    public String getType_activite() {
        return type_activite;
    }

    public void setType_activite(String type_activite) {
        this.type_activite = type_activite;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCours() {
        return cours;
    }

    public void setCours(int cours) {
        this.cours = cours;
    }

    @Override
    public String toString() {
        return "Planning{" +
                "id_planning=" + id_planning +
                ", date_planning=" + date_planning +
                ", type_activite='" + type_activite + '\'' +
                ", status='" + status + '\'' +
                ", cours=" + cours +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Planning)) return false;
        Planning planning = (Planning) o;
        return id_planning == planning.id_planning &&
                cours == planning.cours &&
                Objects.equals(date_planning, planning.date_planning) &&
                Objects.equals(type_activite, planning.type_activite) &&
                Objects.equals(status, planning.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id_planning, date_planning, type_activite, status, cours);
    }
    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }
}