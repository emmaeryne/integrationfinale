package edu.emmapi.entities;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;

public class Cours {
    private final IntegerProperty idCours;       // Use IntegerProperty for ID
    private final StringProperty nomCours;        // Use StringProperty for course name
    private final IntegerProperty duree;          // Use IntegerProperty for duration
    private final StringProperty etatCours;       // Use StringProperty for course status

    public Cours() {
        this.idCours = new SimpleIntegerProperty();
        this.nomCours = new SimpleStringProperty();
        this.duree = new SimpleIntegerProperty();
        this.etatCours = new SimpleStringProperty();
    }

    public Cours(String nom_Cours, int duree, String etat_Cours) {
        this();
        this.nomCours.set(nom_Cours);
        this.duree.set(duree);
        this.etatCours.set(etat_Cours);
    }

    public Cours(int id_Cours, String nom_Cours, int duree, String etat_Cours) {
        this(nom_Cours, duree, etat_Cours);
        this.idCours.set(id_Cours);
    }

    public int getId_Cours() {
        return idCours.get(); // Return the value
    }

    public void setId_Cours(int id_Cours) {
        this.idCours.set(id_Cours);
    }

    public String getEtat_Cours() {
        return etatCours.get(); // Return the value
    }

    public void setEtat_Cours(String etat_Cours) {
        this.etatCours.set(etat_Cours);
    }

    public int getDuree() {
        return duree.get(); // Return the value
    }

    public void setDuree(int duree) {
        this.duree.set(duree);
    }

    public String getNom_Cours() {
        return nomCours.get(); // Return the value
    }

    public void setNom_Cours(String nom_Cours) {
        this.nomCours.set(nom_Cours);
    }

    // Property methods for binding
    public IntegerProperty idCoursProperty() {
        return idCours;
    }

    public StringProperty nomCoursProperty() {
        return nomCours;
    }

    public IntegerProperty dureeProperty() {
        return duree;
    }

    public StringProperty etatCoursProperty() {
        return etatCours;
    }

    @Override
    public String toString() {
        return nomCours.get(); // Correctly return the course name as a String
    }
}