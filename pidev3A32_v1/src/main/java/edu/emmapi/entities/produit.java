package edu.emmapi.entities;

public class produit {
    private int id;
    private String Nom_Produit;
    private String Categorie;
    private float Prix;
    private int Stock_disponible;
    private String Date;
    private String Fournisseur;

    public produit() {
    }


    public produit(String nom_Produit, String categorie, float prix, int stock, String dateProduit, String fournisseurChoisi) {
        Nom_Produit = nom_Produit;
        Categorie = categorie;
        Prix = prix;
        Stock_disponible = stock;
        Date = dateProduit;
        Fournisseur = fournisseurChoisi;
    }

    public produit(int id, String nomProduit, String categorie, float prix, int stockDispo, String date, String fournisseur) {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom_Produit() {
        return Nom_Produit;
    }

    public void setNom_Produit(String nom_Produit) {
        Nom_Produit = nom_Produit;
    }

    public String getCategorie() {
        return Categorie;
    }

    public void setCategorie(String categorie) {
        Categorie = categorie;
    }

    public float getPrix() {
        return Prix;
    }

    public void setPrix(float prix) {
        Prix = prix;
    }

    public int getStock_disponible() {
        return Stock_disponible;
    }

    public void setStock_disponible(int stock_disponible) {
        Stock_disponible = stock_disponible;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getFournisseur() {
        return Fournisseur;
    }

    public void setFournisseur(String fournisseur) {
        Fournisseur = fournisseur;
    }
}
