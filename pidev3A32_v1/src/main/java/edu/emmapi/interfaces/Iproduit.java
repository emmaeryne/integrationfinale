package edu.emmapi.interfaces;
import edu.emmapi.entities.produit;
import edu.emmapi.entities.produit;

import java.sql.SQLException;
import java.util.List;
public interface Iproduit {
    void ajouterProduit(produit p)throws SQLException;
    void supprimerProduit(produit p);
    void modifierProduit(produit p);

    public List<produit> afficherProduit();
}



