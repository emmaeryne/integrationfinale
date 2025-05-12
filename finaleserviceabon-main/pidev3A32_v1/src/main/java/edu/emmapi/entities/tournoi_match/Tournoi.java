package edu.emmapi.entities.tournoi_match;

import java.sql.Date;

public class Tournoi {
    private int id_tournoi;
    private String nom_tournoi;
    private String type_tournoi;
    private Date date_tournoi;
    private String description_tournoi;

    public Tournoi() {
    }

    public Tournoi(String nom_tournoi, String type_tournoi, Date date_tournoi, String description_tournoi) {
        this.nom_tournoi = nom_tournoi;
        this.type_tournoi = type_tournoi;
        this.date_tournoi = date_tournoi;
        this.description_tournoi = description_tournoi;
    }

    public Tournoi(int id_tournoi, String nom_tournoi, String type_tournoi, Date date_tournoi, String description_tournoi) {
        this.id_tournoi = id_tournoi;
        this.nom_tournoi = nom_tournoi;
        this.type_tournoi = type_tournoi;
        this.date_tournoi = date_tournoi;
        this.description_tournoi = description_tournoi;
    }

    public int getId_tournoi() {
        return id_tournoi;
    }

    public String getNom_tournoi() {
        return nom_tournoi;
    }

    public String getType_tournoi() {
        return type_tournoi;
    }

    public Date getDate_tournoi() {
        return date_tournoi;
    }

    public String getDescription_tournoi() {
        return description_tournoi;
    }

    @Override
    public String toString() {
        return "Tournoi{" +
                "id_tournoi=" + id_tournoi +
                ", nom_tournoi='" + nom_tournoi + '\'' +
                ", type_tournoi='" + type_tournoi + '\'' +
                ", date_tournoi=" + date_tournoi +
                ", description_tournoi='" + description_tournoi + '\'' +
                '}';
    }
}
