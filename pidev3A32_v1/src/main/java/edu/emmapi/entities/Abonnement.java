package edu.emmapi.entities;

import java.time.LocalDate;

public class Abonnement {
    private Long id;
    private Service service; // Reference to Service
    private TypeAbonnement typeAbonnement; // Reference to TypeAbonnement
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private boolean estActif;
    private double prixTotal;
    private Statut statut; // Changed from String to Statut enum
    private int nombreSeancesRestantes;
    private boolean autoRenouvellement;
    private String modePaiement;
    private String numeroTransaction;
    private LocalDate dernierePresentence;
    private double tauxUtilisation;

    // Constructor
    public Abonnement(Long id, Service service, TypeAbonnement typeAbonnement, LocalDate dateDebut, LocalDate dateFin,
                      boolean estActif, double prixTotal, Statut statut, // Changed to Statut
                      int nombreSeancesRestantes, boolean autoRenouvellement, String modePaiement,
                      String numeroTransaction, LocalDate dernierePresentence, double tauxUtilisation) {
        this.id = id;
        this.service = service;
        this.typeAbonnement = typeAbonnement;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.estActif = estActif;
        this.prixTotal = prixTotal;
        this.statut = statut; // Changed to Statut
        this.nombreSeancesRestantes = nombreSeancesRestantes;
        this.autoRenouvellement = autoRenouvellement;
        this.modePaiement = modePaiement;
        this.numeroTransaction = numeroTransaction;
        this.dernierePresentence = dernierePresentence;
        this.tauxUtilisation = tauxUtilisation;
    }

    // Default constructor
    public Abonnement() {
        // Initialize with default values if needed
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public TypeAbonnement getTypeAbonnement() {
        return typeAbonnement;
    }

    public void setTypeAbonnement(TypeAbonnement typeAbonnement) {
        this.typeAbonnement = typeAbonnement;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public boolean isEstActif() {
        return estActif;
    }

    public void setEstActif(boolean estActif) {
        this.estActif = estActif;
    }

    public double getPrixTotal() {
        return prixTotal;
    }

    public void setPrixTotal(double prixTotal) {
        if (prixTotal < 0) {
            throw new IllegalArgumentException("Prix total ne peut pas être négatif");
        }
        this.prixTotal = prixTotal;
    }

    public Statut getStatut() {
        return statut; // Changed from String to Statut
    }

    public void setStatut(Statut statut) { // Changed from String to Statut
        this.statut = statut;
    }

    public int getNombreSeancesRestantes() {
        return nombreSeancesRestantes;
    }

    public void setNombreSeancesRestantes(int nombreSeancesRestantes) {
        this.nombreSeancesRestantes = nombreSeancesRestantes;
    }

    public boolean isAutoRenouvellement() {
        return autoRenouvellement;
    }

    public void setAutoRenouvellement(boolean autoRenouvellement) {
        this.autoRenouvellement = autoRenouvellement;
    }

    public String getModePaiement() {
        return modePaiement;
    }

    public void setModePaiement(String modePaiement) {
        this.modePaiement = modePaiement;
    }

    public String getNumeroTransaction() {
        return numeroTransaction;
    }

    public void setNumeroTransaction(String numeroTransaction) {
        this.numeroTransaction = numeroTransaction;
    }

    public LocalDate getDernierePresentence() {
        return dernierePresentence;
    }

    public void setDernierePresentence(LocalDate dernierePresentence) {
        this.dernierePresentence = dernierePresentence;
    }

    public double getTauxUtilisation() {
        return tauxUtilisation;
    }

    public void setTauxUtilisation(double tauxUtilisation) {
        this.tauxUtilisation = tauxUtilisation;
    }

    @Override
    public String toString() {
        return "Abonnement{" +
                "id=" + id +
                ", service=" + service +
                ", typeAbonnement=" + typeAbonnement +
                ", dateDebut=" + dateDebut +
                ", dateFin=" + dateFin +
                ", estActif=" + estActif +
                ", prixTotal=" + prixTotal +
                ", statut=" + statut + // Changed to use the Statut enum
                ", nombreSeancesRestantes=" + nombreSeancesRestantes +
                ", autoRenouvellement=" + autoRenouvellement +
                ", modePaiement='" + modePaiement + '\'' +
                ", numeroTransaction='" + numeroTransaction + '\'' +
                ", dernierePresentence=" + dernierePresentence +
                ", tauxUtilisation=" + tauxUtilisation +
                '}';
    }
}