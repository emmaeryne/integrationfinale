package edu.emmapi.entities.tournoi_match;


import java.sql.Date;

public class Match {
    private int id_match;
    private int id_tournoi;
    private int id_equipe1;
    private int id_equipe2;
    private Date date_match;
    private int id_terrain;
    private int score_equipe1;
    private int score_equipe2;
    private String statut_match;

    public Match() {
    }

    public Match(int id_tournoi, int id_equipe1, int id_equipe2, Date date_match, int id_terrain, int score_equipe1, int score_equipe2, String statut_match) {
        this.id_tournoi = id_tournoi;
        this.id_equipe1 = id_equipe1;
        this.id_equipe2 = id_equipe2;
        this.date_match = date_match;
        this.id_terrain = id_terrain;
        this.score_equipe1 = score_equipe1;
        this.score_equipe2 = score_equipe2;
        this.statut_match = statut_match;
    }

    public Match(int id_match, int id_tournoi, int id_equipe1, int id_equipe2, Date date_match, int id_terrain, int score_equipe1, int score_equipe2, String statut_match) {
        this.id_match = id_match;
        this.id_tournoi = id_tournoi;
        this.id_equipe1 = id_equipe1;
        this.id_equipe2 = id_equipe2;
        this.date_match = date_match;
        this.id_terrain = id_terrain;
        this.score_equipe1 = score_equipe1;
        this.score_equipe2 = score_equipe2;
        this.statut_match = statut_match;
    }

    public int getId_match() {
        return id_match;
    }

    public int getId_tournoi() {
        return id_tournoi;
    }

    public int getId_equipe1() {
        return id_equipe1;
    }

    public int getId_equipe2() {
        return id_equipe2;
    }

    public Date getDate_match() {
        return date_match;
    }

    public int getId_terrain() {
        return id_terrain;
    }

    public int getScore_equipe1() {
        return score_equipe1;
    }

    public int getScore_equipe2() {
        return score_equipe2;
    }

    public String getStatut_match() {
        return statut_match;
    }

    @Override
    public String toString() {
        return "Match{" +
                "id_match=" + id_match +
                ", id_tournoi=" + id_tournoi +
                ", id_equipe1=" + id_equipe1 +
                ", id_equipe2=" + id_equipe2 +
                ", date_match=" + date_match +
                ", id_terrain=" + id_terrain +
                ", score_equipe1=" + score_equipe1 +
                ", score_equipe2=" + score_equipe2 +
                ", statut_match='" + statut_match + '\'' +
                '}';
    }
}
