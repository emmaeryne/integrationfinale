package edu.emmapi.entities;



public class categorieproduit {
    private int id;
    private String nomcategorie;
    private byte[] image;
    private String description;

    public categorieproduit() {
    }

    public categorieproduit(String nomcategorie, String description,byte[] image) {
        this.nomcategorie = nomcategorie;
        this.description = description;
        this.image = image;

    }

    public categorieproduit(String nomcategorie, byte[] image, String description) {
        this.nomcategorie = nomcategorie;
        this.image = image;
        this.description = description;
    }



    public categorieproduit(int id, String nomcategorie, byte[] image, String description) {
        this.id = id;
        this.nomcategorie = nomcategorie;
        this.image = image;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomcategorie() {
        return nomcategorie;
    }

    public void setNomcategorie(String nomcategorie) {
        this.nomcategorie = nomcategorie;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}

