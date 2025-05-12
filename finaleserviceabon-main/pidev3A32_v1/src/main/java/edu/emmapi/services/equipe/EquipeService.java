package edu.emmapi.services.equipe;

import edu.emmapi.entities.equipe.Equipe;
import edu.emmapi.interfaces.tarek.IService;
import edu.emmapi.tools.MyConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EquipeService implements IService<Equipe> {
    @Override
    public void addEntity(Equipe equipe) throws SQLException {
        String query = "INSERT INTO EQUIPE(nom_equipe, type_equipe) VALUES (?, ?)";
        try {
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(query);
            pst.setString(1,equipe.getNom_equipe());
            pst.setString(2,equipe.getType_equipe());
            pst.executeUpdate();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void deleteEntity(int id_equipe) {
        String query = "DELETE FROM EQUIPE WHERE id_equipe = ?";
        try (PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(query)) {
            pst.setInt(1, id_equipe);
            pst.executeUpdate();
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void updateEntity(int id, Equipe equipe) {
        String query = "UPDATE EQUIPE SET nom_equipe=? WHERE id_equipe=?";
        try (PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(query)) {
            pst.setString(1, equipe.getNom_equipe());
            pst.setInt(2, id);
            pst.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<Equipe> getAllData() {
        List<Equipe> equipeList = new ArrayList<>();
        String query = "SELECT * FROM EQUIPE";
        try (PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(query)) {
            ResultSet rs = pst.executeQuery(query);
            while (rs.next()){
                Equipe equipe = new Equipe(
                        rs.getInt("id_equipe"),
                        rs.getString("nom_equipe"),
                        rs.getString("type_equipe")
                );
                equipeList.add(equipe);
            }
        }catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return equipeList;
    }

    public int idEquipeFromNomType(String nom, String type){
        String query = "SELECT id_equipe FROM EQUIPE WHERE nom_equipe = ? AND type_equipe = ?";
        try (PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(query)) {
            pst.setString(1, nom);
            pst.setString(2, type);

            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt("id_equipe");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }

    public Equipe getEquipeById(int id){
        String query = "SELECT * FROM EQUIPE WHERE id_equipe=?";
        try (PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(query)) {
            pst.setInt(1, id);

            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return new Equipe(
                        rs.getInt("id_equipe"),
                        rs.getString("nom_equipe"),
                        rs.getString("type_equipe")
                );

            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}
