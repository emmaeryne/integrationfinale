package edu.emmapi.entities;

import javafx.beans.property.*;

public class CommandeProduits {
    private final IntegerProperty idCommande = new SimpleIntegerProperty();
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final IntegerProperty quantite = new SimpleIntegerProperty();
    private final FloatProperty prix = new SimpleFloatProperty ();

    public CommandeProduits(int idCommande, int id, int quantite, float prix) {
        this.idCommande.set(idCommande);
        this.id.set(id);
        this.quantite.set(quantite);
        this.prix.set(prix);
    }

    public int getIdCommande() { return idCommande.get(); }
    public IntegerProperty idCommandeProperty() { return idCommande; }

    public int getId() { return id.get(); }
    public IntegerProperty idProperty() { return id; }

    public int getQuantite() { return quantite.get(); }
    public IntegerProperty quantiteProperty() { return quantite; }
    public void setQuantite(int quantite) { this.quantite.set(quantite); }

    public float getPrix() { return prix.get(); }
    public FloatProperty  prixProperty() { return prix; }
    public void setPrix(float prix) { this.prix.set(prix); }
}
