package edu.emmapi.services.joueur;

import edu.emmapi.entities.joueur.Joueur;
import edu.emmapi.entities.tournoi_match.Match;
import edu.emmapi.interfaces.tarek.IService;
import edu.emmapi.tools.MyConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JoueurService implements IService<Joueur> {
    @Override
    public void addEntity(Joueur joueur) throws SQLException {
        String query = "INSERT INTO JOUEUR(nom_joueur, cin, url_photo) VALUES (?, ?, ?)";
        try {
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(query);
            pst.setString(1, joueur.getNom_joueur());
            pst.setInt(2, joueur.getCin());
            pst.setString(3, joueur.getUrl_image());
            pst.executeUpdate();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void deleteEntity(int id) {
        String query = "DELETE FROM JOUEUR WHERE id_joueur = ?";
        try (PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(query)) {
            pst.setInt(1, id);
            pst.executeUpdate();
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void updateEntity(int id, Joueur joueur) {
        String query = "UPDATE JOUEUR SET nom_joueur=?, url_photo=?, cin=? WHERE id_joueur=?";
        try (PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(query)) {
            pst.setString(1, joueur.getNom_joueur());
            pst.setString(2, joueur.getUrl_image());
            pst.setInt(3, joueur.getCin());
            pst.setInt(4, id);
            pst.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void updateJoueurEquipe(int id, int equipe){
        String query = "UPDATE JOUEUR SET id_equipe=? WHERE id_joueur=?";
        try (PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(query)) {
            pst.setInt(1, equipe);
            pst.setInt(2, id);
            pst.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<Joueur> getAllData() {
        List<Joueur> joueurList = new ArrayList<>();
        String query = "SELECT * FROM JOUEUR";
        try (PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(query)) {
            ResultSet rs = pst.executeQuery(query);
            while (rs.next()){
                Joueur joueur = new Joueur(
                        rs.getInt("id_joueur"),
                        rs.getString("nom_joueur"),
                        rs.getInt("id_equipe"),
                        rs.getInt("cin")
                );
                joueurList.add(joueur);
            }
        }catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return joueurList;
    }

    public void ajoutJoueurClient(Joueur joueur){
        String query = "INSERT INTO JOUEUR(cin, url_photo) VALUES (?, ?)";
        try {
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(query);
            pst.setInt(1, joueur.getCin());
            pst.setString(2, joueur.getUrl_image());
            pst.executeUpdate();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public boolean joueurExiste(Joueur joueur){
        String query = "SELECT * FROM JOUEUR WHERE nom_joueur=? OR cin=? OR (url_photo=? AND url_photo!=''";
        try (PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(query)) {
            pst.setString(1, joueur.getNom_joueur());
            pst.setInt(2, joueur.getCin());
            pst.setString(3, joueur.getUrl_image());
            ResultSet rs = pst.executeQuery();
            if(rs.next()){
                return true;
            }
        }catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public Joueur getJoueurById(int id){
        String query = "SELECT * FROM JOUEUR WHERE id_joueur=?";
        try (PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(query)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if(rs.next()){
                return new Joueur(
                        rs.getInt("id_joueur"),
                        rs.getString("nom_joueur"),
                        rs.getInt("id_equipe"),
                        rs.getInt("cin"),
                        rs.getString("url_photo")
                );
            }
        }catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public Joueur getJoueurByIdEquipe(int id){
        String query = "SELECT * FROM JOUEUR WHERE id_equipe=?";
        try (PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(query)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if(rs.next()){
                return new Joueur(
                        rs.getInt("id_joueur"),
                        rs.getString("nom_joueur"),
                        rs.getInt("id_equipe"),
                        rs.getInt("cin"),
                        rs.getString("url_photo")
                );
            }
        }catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}
