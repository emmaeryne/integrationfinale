package edu.emmapi.entities.equipe;

public class Equipe {
    int id_equipe;
    String nom_equipe;
    String type_equipe;

    public Equipe() {
    }

    public Equipe(String nom_equipe, String type_equipe) {
        this.nom_equipe = nom_equipe;
        this.type_equipe = type_equipe;
    }

    public Equipe(int id_equipe, String nom_equipe, String type_equipe) {
        this.id_equipe = id_equipe;
        this.nom_equipe = nom_equipe;
        this.type_equipe = type_equipe;
    }

    public int getId_equipe() {
        return id_equipe;
    }

    public String getNom_equipe() {
        return nom_equipe;
    }

    public String getType_equipe() {
        return type_equipe;
    }

    @Override
    public String toString() {
        return "Equipe{" +
                "id_equipe=" + id_equipe +
                ", nom_equipe='" + nom_equipe + '\'' +
                ", type_equipe'" + type_equipe + '\'' +
                '}';
    }

    public int numJoueursFromType(String type){
        if (type.equals("BasketBall")){
            return 5;
        }
        else return 11;
    }
}
