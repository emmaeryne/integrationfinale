package edu.emmapi.entities;

public class Personne {
    private int id;
    private String nom;
    private String prenom;
    private String gouvernorat;
    private int age;
    private int numTel;
    private String role;



    public Personne() {
    }

    public Personne(String nom, String prenom) {
        this.nom = nom;
        this.prenom = prenom;
    }

    public Personne(int id, String nom, int age, int numTel, String role, String gouvernorat, String prenom) {
        this.id = id;
        this.nom = nom;
        this.age = age;
        this.numTel = numTel;
        this.role = role;
        this.gouvernorat = gouvernorat;
        this.prenom = prenom;
    }

    public Personne(String nom, String prenom, int age, String gouvernorat, int numTel, String role) {
        this.nom = nom;
        this.prenom = prenom;
        this.age = age;
        this.gouvernorat = gouvernorat;
        this.numTel = numTel;
        this.role = role;
    }

    @Override
    public String toString() {
        return "Personne{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", gouvernorat='" + gouvernorat + '\'' +
                ", age=" + age +
                ", numTel=" + numTel +
                ", role='" + role + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getGouvernorat() {
        return gouvernorat;
    }

    public void setGouvernorat(String gouvernorat) {
        this.gouvernorat = gouvernorat;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getNumTel() {
        return numTel;
    }

    public void setNumTel(int numTel) {
        this.numTel = numTel;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }


}
