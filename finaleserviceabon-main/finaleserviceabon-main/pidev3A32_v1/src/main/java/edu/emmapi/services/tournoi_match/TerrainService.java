package edu.emmapi.services.tournoi_match;

import edu.emmapi.entities.tournoi_match.Terrain;
import edu.emmapi.interfaces.tarek.IService;
import edu.emmapi.tools.MyConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TerrainService implements IService<Terrain> {
    @Override
    public void addEntity(Terrain terrain) throws SQLException {
        String query = "INSERT INTO TERRAIN() VALUES ()";
        try {
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(query);
            pst.executeUpdate();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void deleteEntity(int id_terrain) {
        String query = "DELETE FROM TERRAIN WHERE id_terrain = ?";
        try (PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(query)) {
            pst.setInt(1, id_terrain);
            pst.executeUpdate();
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void updateEntity(int id, Terrain terrain) {

    }

    @Override
    public List<Terrain> getAllData() {
        List<Terrain> terrainList = new ArrayList<>();
        String query = "SELECT * FROM TERRAIN";
        try (PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(query)) {
            ResultSet rs = pst.executeQuery(query);
            while (rs.next()){
                Terrain terrain = new Terrain(
                        rs.getInt("id_terrain")
                );
                terrainList.add(terrain);
            }
        }catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return terrainList;
    }
}
