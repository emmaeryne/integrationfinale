/*package edu.emmapi.entities;

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
}*/
/*package edu.emmapi.entities;

import java.time.LocalDate;

public class Abonnement {
    private Integer id;
    private Service service;
    private TypeAbonnement typeAbonnement;
    private double prixTotal;
    private Statut statut;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private boolean autoRenouvellement;

    // Constructors
    public Abonnement() {}

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Service getService() { return service; }
    public void setService(Service service) { this.service = service; }

    public TypeAbonnement getTypeAbonnement() { return typeAbonnement; }
    public void setTypeAbonnement(TypeAbonnement typeAbonnement) { this.typeAbonnement = typeAbonnement; }

    public double getPrixTotal() { return prixTotal; }
    public void setPrixTotal(double prixTotal) { this.prixTotal = prixTotal; }

    public Statut getStatut() { return statut; }
    public void setStatut(Statut statut) { this.statut = statut; }

    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }

    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }

    public boolean isAutoRenouvellement() { return autoRenouvellement; }
    public void setAutoRenouvellement(boolean autoRenouvellement) { this.autoRenouvellement = autoRenouvellement; }

    // Prolonger Abonnement (extends dateFin by 1 month and sets statut to ACTIF)
    public void prolongerAbonnement() {
        this.dateFin = this.dateFin.plusMonths(1);
        this.statut = Statut.ACTIF;
    }
}*/
        package edu.emmapi.entities;

import java.time.LocalDate;

public class Abonnement {
    private Integer id;
    private Service service;
    private TypeAbonnement typeAbonnement;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private boolean estActif;
    private double prixTotal;
    private boolean estGratuit;
    private Statut statut;
    private int nombreSeancesRestantes;
    private boolean autoRenouvellement;
    private Integer dureeMois;
    private String modePaiement;

    // Constructors
    public Abonnement() {
        this.estActif = true;
        this.estGratuit = false;
        this.nombreSeancesRestantes = 0;
        this.autoRenouvellement = false;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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
        updateDateFin();
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
            throw new IllegalArgumentException("Le prix total ne peut pas être négatif.");
        }
        if (this.estGratuit && prixTotal != 0.0) {
            throw new IllegalArgumentException("Le prix total doit être 0.00 si l'abonnement est gratuit.");
        }
        this.prixTotal = prixTotal;
    }

    public boolean isEstGratuit() {
        return estGratuit;
    }

    public void setEstGratuit(boolean estGratuit) {
        this.estGratuit = estGratuit;
        if (estGratuit) {
            this.prixTotal = 0.0;
        }
    }

    public Statut getStatut() {
        return statut;
    }

    public void setStatut(Statut statut) {
        this.statut = statut;
    }

    public int getNombreSeancesRestantes() {
        return nombreSeancesRestantes;
    }

    public void setNombreSeancesRestantes(int nombreSeancesRestantes) {
        if (nombreSeancesRestantes < 0) {
            throw new IllegalArgumentException("Le nombre de séances restantes ne peut pas être négatif.");
        }
        this.nombreSeancesRestantes = nombreSeancesRestantes;
    }

    public boolean isAutoRenouvellement() {
        return autoRenouvellement;
    }

    public void setAutoRenouvellement(boolean autoRenouvellement) {
        this.autoRenouvellement = autoRenouvellement;
        updateDateFin();
    }

    public Integer getDureeMois() {
        return dureeMois;
    }

    public void setDureeMois(Integer dureeMois) {
        if (dureeMois != null && dureeMois <= 0) {
            throw new IllegalArgumentException("La durée doit être un nombre positif de mois.");
        }
        this.dureeMois = dureeMois;
        updateDateFin();
    }

    public String getModePaiement() {
        return modePaiement;
    }

    public void setModePaiement(String modePaiement) {
        if (modePaiement != null && !modePaiement.matches("Carte bancaire|Espèces")) {
            throw new IllegalArgumentException("Le mode de paiement doit être 'Carte bancaire' ou 'Espèces'.");
        }
        this.modePaiement = modePaiement;
    }

    // Validation and Logic
    private void updateDateFin() {
        if (this.dateDebut == null) {
            return;
        }
        if (this.autoRenouvellement) {
            this.dateFin = this.dateDebut.plusYears(1);
        } else if (this.dureeMois != null && this.dureeMois > 0) {
            this.dateFin = this.dateDebut.plusMonths(this.dureeMois);
        }
    }

    public void prolongerAbonnement() {
        if (this.autoRenouvellement) {
            this.dateFin = LocalDate.now().plusMonths(1);
            this.statut = Statut.ACTIF;
            this.estActif = true;
        }
    }
}