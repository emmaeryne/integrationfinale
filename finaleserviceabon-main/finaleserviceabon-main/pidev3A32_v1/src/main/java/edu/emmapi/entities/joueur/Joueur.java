package edu.emmapi.entities.joueur;

public class Joueur {
    int id_joueur;
    String nom_joueur;
    int id_equipe;
    int cin;
    String url_image="";

    public String getUrl_image() {
        return url_image;
    }

    public Joueur(String nom_joueur, int cin) {
        this.nom_joueur = nom_joueur;
        this.cin = cin;
    }

    public Joueur(int cin, String url_image) {
        this.cin = cin;
        this.url_image = url_image;
    }

    public Joueur(String nom, int cin, String url_image) {
        this.cin = cin;
        this.url_image = url_image;
        this.nom_joueur = nom;
    }

    public Joueur(int id_joueur, String nom_joueur, int id_equipe, int cin) {
        this.id_joueur = id_joueur;
        this.nom_joueur = nom_joueur;
        this.id_equipe = id_equipe;
        this.cin = cin;
    }

    public Joueur(int id_joueur, String nom_joueur, int id_equipe, int cin, String url_image) {
        this.id_joueur = id_joueur;
        this.nom_joueur = nom_joueur;
        this.id_equipe = id_equipe;
        this.cin = cin;
        this.url_image = url_image;
    }

    public int getId_joueur() {
        return id_joueur;
    }

    public void setId_joueur(int id_joueur) {
        this.id_joueur = id_joueur;
    }

    public String getNom_joueur() {
        return nom_joueur;
    }

    public void setNom_joueur(String nom_joueur) {
        this.nom_joueur = nom_joueur;
    }

    public int getId_equipe() {
        return id_equipe;
    }

    public void setId_equipe(int id_equipe) {
        this.id_equipe = id_equipe;
    }

    public int getCin() {
        return cin;
    }

    public void setCin(int cin) {
        this.cin = cin;
    }

    @Override
    public String toString() {
        return "Joueur{" +
                "id_joueur=" + id_joueur +
                ", nom_joueur='" + nom_joueur + '\'' +
                ", id_equipe=" + id_equipe +
                ", cin=" + cin +
                ", url_image='" + url_image + '\'' +
                '}';
    }
}
