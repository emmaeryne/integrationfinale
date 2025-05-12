package edu.emmapi.entities;

public class CategorieItem {
    private int id;
    private String nom;

    public CategorieItem(int id, String nom) {
        this.id = id;
        this.nom = nom;
    }

    public int getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    @Override
    public String toString() {
        return nom; // Ce qui s'affiche dans le ComboBox
    }
}
