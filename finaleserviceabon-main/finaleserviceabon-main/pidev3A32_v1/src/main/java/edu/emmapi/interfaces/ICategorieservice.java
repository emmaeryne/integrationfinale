package edu.emmapi.interfaces;




import edu.emmapi.entities.categorieproduit;
import java.util.List;
import java.util.Map;

public interface ICategorieservice{
    void ajouterCategorieProduit(categorieproduit c);
    void supprimerCategorieProduit(categorieproduit c);
    void modifierCategorieProduit(categorieproduit c );
    public List<categorieproduit> afficherCategorieProduit();
}

