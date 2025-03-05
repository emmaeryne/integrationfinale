package edu.emmapi.controllers.tournoi_match;

import edu.emmapi.controllers.components.MusicPlayer;
import edu.emmapi.entities.equipe.Equipe;
import edu.emmapi.entities.tournoi_match.Match;
import edu.emmapi.entities.tournoi_match.Terrain;
import edu.emmapi.entities.tournoi_match.Tournoi;
import edu.emmapi.services.equipe.EquipeService;
import edu.emmapi.services.tournoi_match.MatchService;
import edu.emmapi.services.tournoi_match.TerrainService;
import edu.emmapi.services.tournoi_match.TournoiService;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AjoutMatchController {

    @FXML
    private VBox error;

    @FXML
    private Label message;

    @FXML
    private DatePicker date_match;

    @FXML
    private TableView<Equipe> liste_equipe1;

    @FXML
    private TableColumn<Equipe, Integer> liste_equipe1_id;

    @FXML
    private TableColumn<Equipe, String> liste_equipe1_nom;

    @FXML
    private TableView<Equipe> liste_equipe2;

    @FXML
    private TableColumn<Equipe, Integer> liste_equipe2_id;

    @FXML
    private TableColumn<Equipe, String> liste_equipe2_nom;

    @FXML
    private ComboBox<String> liste_statut;

    @FXML
    private ComboBox<Integer> liste_terrain;

    @FXML
    private ComboBox<Integer> liste_tournoi;

    @FXML
    private TextField score1;

    @FXML
    private TextField score2;

    @FXML
    private ImageView terrain_bg;

    @FXML
    private Button equipe1;

    @FXML
    private Button equipe2;

    @FXML
    private VBox music_player;

    @FXML
    private ImageView toggler;

    @FXML
    private ImageView play_button;

    private final Image toggle_down = new Image(getClass().getResource("/images/icons/down.png").toExternalForm());
    private final Image toggle_up = new Image(getClass().getResource("/images/icons/up.png").toExternalForm());
    private final Image play = new Image(getClass().getResource("/images/icons/play.png").toExternalForm());
    private final Image pause = new Image(getClass().getResource("/images/icons/pause.png").toExternalForm());

    public boolean playerVisible = true;
    public boolean isPlaying = false;

    MatchService matchService = new MatchService();
    TournoiService tournoiService = new TournoiService();
    TerrainService terrainService = new TerrainService();
    EquipeService equipeService = new EquipeService();

    @FXML
    void togglePlayer(MouseEvent event) {
        if (!MusicPlayer.getInstance().isHidden()) {
            music_player.setLayoutY(-30);
            toggler.setImage(toggle_down);
        }
        else {
            music_player.setLayoutY(0);
            toggler.setImage(toggle_up);
        }
        MusicPlayer.getInstance().setHidden(!MusicPlayer.getInstance().isHidden());
    }

    @FXML
    void toggle_player_state(MouseEvent event) {
        if (isPlaying){
            play_button.setImage(pause);
            MusicPlayer.getInstance().getMediaPlayer().play();
        }
        else {
            play_button.setImage(play);
            MusicPlayer.getInstance().getMediaPlayer().pause();
        }
        isPlaying = !isPlaying;
    }

    @FXML
    void toNextSong(MouseEvent event) {
        MusicPlayer.getInstance().nextSong();
    }

    @FXML
    void toPreviousSong(MouseEvent event) {
        MusicPlayer.getInstance().previousSong();
    }


    public static boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @FXML
    void annulerModification(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/pages/tournoi_match/AfficheMatchs.fxml"));
        try {
            Parent parent = loader.load();
            date_match.getScene().setRoot(parent);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Echec de navigation");
            alert.setContentText(e.getMessage());
            alert.show();
        }
    }

    @FXML
    void confirmerAjout(ActionEvent event) {
        try {
            if (liste_tournoi.getValue()==null){
                showError("Choisir un tournoi", "#F05A5A");
            }
            else if((date_match.getValue()==null)||(tournoiService.getTournoiById(liste_tournoi.getValue()).getDate_tournoi().compareTo(Date.valueOf(date_match.getValue()))>0)) {
                showError("Choisir une date valide", "#F05A5A");
            }
            else if(liste_terrain.getValue()==null){
                showError("Choisir une terrain", "#F05A5A");
            }
            else if(equipe1.getText().equals("Equipe 1") || equipe2.getText().equals("Equipe 2")){
                showError("Choisir une equipe", "#F05A5A");
            }
            else if(equipe1.getText().equals(equipe2.getText())) {
                showError("Vous ne pouvez pas choisir deux fois la même équipe", "#F05A5A");
            }
            else if(!isInteger(score1.getText()) || !isInteger(score2.getText())){
                showError("Le score doit être un entier", "#F05A5A");
            }
            else if(Integer.parseInt(score1.getText()) < 0 || Integer.parseInt(score2.getText()) < 0){
                showError("Le score doit être un entier positive", "#F05A5A");
            }
            else {
                Match match = new Match(liste_tournoi.getValue(), Integer.parseInt(equipe1.getText()), Integer.parseInt(equipe2.getText()), Date.valueOf(date_match.getValue()), liste_terrain.getValue(), Integer.parseInt(score1.getText()), Integer.parseInt(score2.getText()), liste_statut.getValue());
                if (!matchService.checkIfMatchExist(match)){
                    matchService.addEntity(match);
                    showError("Match ajoutée avec succès", "#66ffcc");
                }
                else {
                    showError("Match existe déjà", "#F05A5A");
                }
            }
        } catch (Exception e){
            showError(e.getMessage(), "#F05A5A");
        }
    }


    public void showError(String message, String color){
        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        this.message.setText(message);
        error.setStyle("-fx-background-color: " + color);
        error.setVisible(true);
        pause.setOnFinished(event -> {
            error.setVisible(false);
            if (color.equals("#66ffcc")) {
                annulerModification(new ActionEvent());
            }
        });

        pause.play();
    }


    @FXML
    public void refreshImageTerrain(ActionEvent event){
        try {
            Tournoi tournoi = tournoiService.getTournoiById(liste_tournoi.getValue());
            Image image = new Image(getClass().getResource("/images/backgrounds/" + tournoi.getType_tournoi() + ".png").toExternalForm());
            terrain_bg.setImage(image);
        }catch (Exception e) {
            Image image = new Image(getClass().getResource("/images/backgrounds/Placeholder.png").toExternalForm());
            terrain_bg.setImage(image);
        }
        date_match.setValue(tournoiService.getTournoiById(liste_tournoi.getValue()).getDate_tournoi().toLocalDate());
        liste_equipe1.setVisible(false);
        liste_equipe2.setVisible(false);
        terrain_bg.setVisible(true);
    }

    public void initialize(){
//tournoi
        List<Integer> tournoiList = new ArrayList<>();

        for(Tournoi tournoi : tournoiService.getAllData()){
            tournoiList.add(tournoi.getId_tournoi());
        }

        liste_tournoi.setItems(FXCollections.observableArrayList(tournoiList));

        //terrain
        List<Integer> terrainList = new ArrayList<>();

        for(Terrain terrain : terrainService.getAllData()){
            terrainList.add(terrain.getId_terrain());
        }

        liste_terrain.setItems(FXCollections.observableArrayList(terrainList));

        //equipes
        List<String> equipeList = new ArrayList<>();

        for(Equipe equipe : equipeService.getAllData()){
            equipeList.add(equipe.getNom_equipe());
        }

        //liste_equipe2.getItems().addAll(FXCollections.observableArrayList(equipeService.getAllData()));

        //score
        score1.setText("0");
        score2.setText("0");

        //statut
        liste_statut.setItems(FXCollections.observableArrayList("terminé","annulé","en cours", "pas commencé"));
        liste_statut.setValue("pas commencé");

        //music
        if(MusicPlayer.getInstance().isPlaying()){
            play_button.setImage(pause);
        }
        else{
            play_button.setImage(play);
        }
        if (MusicPlayer.getInstance().isHidden()){
            music_player.setLayoutY(-30);
        }
        else {
            music_player.setLayoutY(0);
        }
    }

    @FXML
    void afficheListeEquipe1(ActionEvent event) {
        List<Equipe> equipeList = new ArrayList<>();

        for(Equipe equipe : equipeService.getAllData()){
            if (Objects.equals(equipe.getType_equipe(), tournoiService.getTournoiById(liste_tournoi.getValue()).getType_tournoi())){
            equipeList.add(equipe);}
        }

        ObservableList<Equipe> equipeObservableList = FXCollections.observableArrayList(
                equipeList
        );

        liste_equipe1_id.setCellValueFactory(new PropertyValueFactory<Equipe, Integer>("id_equipe"));
        liste_equipe1_nom.setCellValueFactory(new PropertyValueFactory<Equipe, String>("nom_equipe"));
        liste_equipe1.setItems(equipeObservableList);
        liste_equipe1.setVisible(true);
        terrain_bg.setVisible(false);
        liste_equipe2.setVisible(false);
        equipe1.setStyle("-fx-background-color: #F2D33A;-fx-text-fill: black;");
        equipe2.setStyle("-fx-background-color: black; -fx-text-fill: white;");
    }

    @FXML
    void afficheListeEquipe2(ActionEvent event) {
        List<Equipe> equipeList = new ArrayList<>();

        for(Equipe equipe : equipeService.getAllData()){
            if (Objects.equals(equipe.getType_equipe(), tournoiService.getTournoiById(liste_tournoi.getValue()).getType_tournoi())){
                equipeList.add(equipe);}
        }

        ObservableList<Equipe> equipeObservableList = FXCollections.observableArrayList(
                equipeList
        );

        liste_equipe2_id.setCellValueFactory(new PropertyValueFactory<Equipe, Integer>("id_equipe"));
        liste_equipe2_nom.setCellValueFactory(new PropertyValueFactory<Equipe, String>("nom_equipe"));
        liste_equipe2.setItems(equipeObservableList);
        liste_equipe2.setVisible(true);
        terrain_bg.setVisible(false);
        liste_equipe1.setVisible(false);
        equipe2.setStyle("-fx-background-color: #F2D33A;-fx-text-fill: black");
        equipe1.setStyle("-fx-background-color: black; -fx-text-fill: white");
    }

    @FXML
    void updateEquipe1(MouseEvent event) {
        equipe1.setText(Integer.toString(liste_equipe1.getSelectionModel().getSelectedItem().getId_equipe()));
    }

    @FXML
    void updateEquipe2(MouseEvent event) {
        equipe2.setText(Integer.toString(liste_equipe2.getSelectionModel().getSelectedItem().getId_equipe()));
    }

}
