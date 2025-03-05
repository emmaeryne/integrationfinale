package edu.emmapi.entities;

import java.time.LocalDate;
import javafx.beans.property.*;


public class Commande {
    private final IntegerProperty idCommande = new SimpleIntegerProperty();
    private final ObjectProperty<LocalDate> dateDeCommande = new SimpleObjectProperty<>();
    private final IntegerProperty idUtilisateur = new SimpleIntegerProperty();
    private final StringProperty status = new SimpleStringProperty();



    public Commande(int idCommande, LocalDate dateDeCommande, int idUtilisateur, String status) {
        this.idCommande.set(idCommande);
        this.dateDeCommande.set(dateDeCommande);
        this.idUtilisateur.set(idUtilisateur);
        this.status.set(status);
    }

    public Commande(LocalDate dateDeCommande, int idUtilisateur, String status) {
        this.dateDeCommande.set(dateDeCommande);
        this.idUtilisateur.set(idUtilisateur);
        this.status.set(status);
    }

    // Getters pour les propriétés
    public int getIdCommande() { return idCommande.get(); }
    public IntegerProperty idCommandeProperty() { return idCommande; }
    public void setIdCommande(int idCommande) { this.idCommande.set(idCommande); }

    public LocalDate getDateDeCommande() { return dateDeCommande.get(); }
    public ObjectProperty<LocalDate> dateDeCommandeProperty() { return dateDeCommande; }
    public void setDateDeCommande(LocalDate dateDeCommande) { this.dateDeCommande.set(dateDeCommande); }

    public int getIdUtilisateur() { return idUtilisateur.get(); }
    public IntegerProperty idUtilisateurProperty() { return idUtilisateur; }
    public void setIdUtilisateur(int idUtilisateur) { this.idUtilisateur.set(idUtilisateur); }

    public String getStatus() { return status.get(); }
    public StringProperty statusProperty() { return status; }
    public void setStatus(String status) { this.status.set(status); }
}