package edu.emmapi.entities;

public class Participant {
    private int id;
    private String nom;
    private String prenom;
    private int age;
    private String adresse;
    private String tel;

    public Participant() {
    }

    // Constructor
    public Participant(String nom, String prenom, int age, String adresse, String tel) {
        this.nom = nom;
        this.prenom = prenom;
        this.age = age;
        this.adresse = adresse;
        this.tel = tel;
    }

    // Getters and Setters
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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }
}
/*package edu.pidev3a32.entities;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Participant {
    // Déclaration des propriétés JavaFX
    private IntegerProperty id;
    private StringProperty nom;
    private StringProperty prenom;
    private StringProperty adresse;
    private StringProperty tel;
    private IntegerProperty age;

    // Constructeur
    public Participant(String nom, String prenom, int age, String adresse, String tel) {
        this.nom = new SimpleStringProperty(nom);
        this.prenom = new SimpleStringProperty(prenom);
        this.age = new SimpleIntegerProperty(age);
        this.adresse = new SimpleStringProperty(adresse);
        this.tel = new SimpleStringProperty(tel);
        this.id = new SimpleIntegerProperty();
    }

    // Getters pour les propriétés JavaFX
    public IntegerProperty idProperty() {
        return id;
    }

    public StringProperty nomProperty() {
        return nom;
    }

    public StringProperty prenomProperty() {
        return prenom;
    }

    public StringProperty adresseProperty() {
        return adresse;
    }

    public StringProperty telProperty() {
        return tel;
    }

    public IntegerProperty ageProperty() {
        return age;
    }

    // Getters simples pour les valeurs des attributs
    public int getId() {
        return id.get();
    }

    public String getNom() {
        return nom.get();
    }

    public String getPrenom() {
        return prenom.get();
    }

    public String getAdresse() {
        return adresse.get();
    }

    public String getTel() {
        return tel.get();
    }

    public int getAge() {
        return age.get();
    }

    // Setters pour modifier les valeurs des attributs
    public void setId(int id) {
        this.id.set(id);
    }

    public void setNom(String nom) {
        this.nom.set(nom);
    }

    public void setPrenom(String prenom) {
        this.prenom.set(prenom);
    }

    public void setAdresse(String adresse) {
        this.adresse.set(adresse);
    }

    public void setTel(String tel) {
        this.tel.set(tel);
    }

    public void setAge(int age) {
        this.age.set(age);
    }
}*/

