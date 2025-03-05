package edu.emmapi.entities;

import java.time.LocalDate;

public class Paiement {
    private int idPaiement;
    private int idCommande;
    private int idUtilisateur;
    private double montant;
    private String modeDePaiement;
    private LocalDate dateDePaiement;
    private String status;


    public Paiement(int idPaiement, int idCommande, int idUtilisateur, double montant, String modeDePaiement, LocalDate dateDePaiement, String status) {
        this.idPaiement = idPaiement;
        this.idCommande = idCommande;
        this.idUtilisateur = idUtilisateur;
        this.montant = montant;
        this.modeDePaiement = modeDePaiement;
        this.dateDePaiement = dateDePaiement;
        this.status = status;
    }


    public Paiement(int idCommande, int idUtilisateur, double montant, String modeDePaiement, LocalDate dateDePaiement, String status) {
        this.idCommande = idCommande;
        this.idUtilisateur = idUtilisateur;
        this.montant = montant;
        this.modeDePaiement = modeDePaiement;
        this.dateDePaiement = dateDePaiement;
        this.status = status;
    }


    public int getIdPaiement() { return idPaiement; }
    public int getIdCommande() { return idCommande; }
    public int getIdUtilisateur() { return idUtilisateur; }
    public double getMontant() { return montant; }
    public String getModeDePaiement() { return modeDePaiement; }
    public LocalDate getDateDePaiement() { return dateDePaiement; }
    public String getStatus() { return status; }


    public void setIdPaiement(int idPaiement) { this.idPaiement = idPaiement; }
    public void setStatus(String status) { this.status = status; }
    public void setDateDePaiement(LocalDate dateDePaiement) { this.dateDePaiement = dateDePaiement; }
}
