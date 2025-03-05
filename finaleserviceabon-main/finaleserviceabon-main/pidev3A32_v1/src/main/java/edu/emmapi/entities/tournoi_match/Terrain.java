package edu.emmapi.entities.tournoi_match;

public class Terrain {
    private int id_terrain;

    public Terrain() {
    }

    public Terrain(int id_terrain) {
        this.id_terrain = id_terrain;
    }

    public int getId_terrain() {
        return id_terrain;
    }

    @Override
    public String toString() {
        return "Terrain{" +
                "id_terraisn=" + id_terrain +
                '}';
    }
}
