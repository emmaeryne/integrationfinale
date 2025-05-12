/*package edu.pidev3a32.entities;

public class Cours_Participant {
    int id_cours;
    int id_participant;;

    public Cours_Participant() {
    }

    public Cours_Participant(int id_cours, int id_participant) {
        this.id_cours = id_cours;
        this.id_participant = id_participant;
    }

    public int getId_cours() {
        return id_cours;
    }

    public void setId_cours(int id_cours) {
        this.id_cours = id_cours;
    }

    public int getId_participant() {
        return id_participant;
    }

    public void setId_participant(int id_participant) {
        this.id_participant = id_participant;
    }

    @Override
    public String toString() {
        return "Cours_Participant{" +
                "id_cours=" + id_cours +
                ", id_participant=" + id_participant +
                '}';
    }
}*/
package edu.emmapi.entities;

public class Cours_Participant {
    private int id_cours;
    private int id_participant;

    // Constructeurs
    public Cours_Participant() {}

    public Cours_Participant(int id_cours, int id_participant) {
        this.id_cours = id_cours;
        this.id_participant = id_participant;
    }

    // Getters et Setters
    public int getId_cours() {
        return id_cours;
    }

    public void setId_cours(int id_cours) {
        this.id_cours = id_cours;
    }

    public int getId_participant() {
        return id_participant;
    }

    public void setId_participant(int id_participant) {
        this.id_participant = id_participant;
    }

    // Méthode toString
    @Override
    public String toString() {
        return "Cours_Participant{" +
                "id_cours=" + id_cours +
                ", id_participant=" + id_participant +
                '}';
    }
}

