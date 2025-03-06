package edu.emmapi.controllers.tournoi_match;

import edu.emmapi.controllers.components.MusicPlayer;
import edu.emmapi.entities.tournoi_match.Tournoi;
import edu.emmapi.services.navigation.NavigationService;
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
import java.util.Optional;

public class AfficheTournoisController {

    @FXML
    private TableView<Tournoi> tableview_tournoi;

    @FXML
    private TableColumn<Tournoi, Date> tableview_tournoi_date;

    @FXML
    private TableColumn<Tournoi, String> tableview_tournoi_description;

    @FXML
    private TableColumn<Tournoi, String> tableview_tournoi_type;

    @FXML
    private TableColumn<Tournoi, Integer> tableview_tournoi_id;

    @FXML
    private TableColumn<Tournoi, String> tableview_tournoi_nom;

    @FXML
    private Label titre_liste_tournois;

    @FXML
    private VBox music_player;

    @FXML
    private ImageView toggler;

    @FXML
    private ImageView play_button;

    @FXML
    private Label message;

    @FXML
    private VBox error;

    private final Image toggle_down = new Image(getClass().getResource("/images/icons/down.png").toExternalForm());
    private final Image toggle_up = new Image(getClass().getResource("/images/icons/up.png").toExternalForm());
    private final Image play = new Image(getClass().getResource("/images/icons/play.png").toExternalForm());
    private final Image pause = new Image(getClass().getResource("/images/icons/pause.png").toExternalForm());

    public boolean playerVisible = true;
    public boolean isPlaying = false;

    TournoiService tournoiService = new TournoiService();
    NavigationService navigationService = new NavigationService();

    public void showError(String message, String color){
        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        this.message.setText(message);
        error.setStyle("-fx-background-color: " + color);
        error.setVisible(true);
        pause.setOnFinished(event -> {
            error.setVisible(false);
        });
        pause.play();
    }

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

    @FXML
    void goToAjout(ActionEvent event) {
        navigationService.goToPage("/pages/tournoi_match/AjoutTournoi.fxml", titre_liste_tournois);
    }

    @FXML
    void modifierTournoi(ActionEvent event) {
        try{
            int selected_tournoi_id = tableview_tournoi.getSelectionModel().getSelectedItem().getId_tournoi();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pages/tournoi_match/ModifierTournoi.fxml"));
            try {
                Parent parent = loader.load();
                ModifierTournoiController modifierTournoiController = loader.getController();
                modifierTournoiController.setId_tournoi(selected_tournoi_id);
                tableview_tournoi.getScene().setRoot(parent);
            } catch (IOException e){
                showError("Echec de navigation", "#F05A5A");
            }
        }catch (Exception e) {
            showError("Vous devez sélectionner une tournoi pour la modifier", "#F05A5A");

        }
    }

    @FXML
    void supprimerTournoi(ActionEvent event) {
        if (tableview_tournoi.getSelectionModel().getSelectedItem() == null) {
            showError("Sélectionnez un tournoi", "#F05A5A");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText(null);
        alert.setContentText("Êtes-vous sûr de vouloir supprimer ce tournoi ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                tournoiService.deleteEntity(tableview_tournoi.getSelectionModel().getSelectedItem().getId_tournoi());
                tableview_tournoi.getItems().clear();
                refreshTableviewTournoi();
            } catch (Exception e) {
                showError("Erreur lors de la suppression du tournoi", "#F05A5A");
            }
        }
    }


    public void refreshTableviewTournoi(){
        ObservableList<Tournoi> tournoiObservableList = FXCollections.observableArrayList(
                tournoiService.getAllData()
        );

        tableview_tournoi_id.setCellValueFactory(new PropertyValueFactory<Tournoi, Integer>("id_tournoi"));
        tableview_tournoi_nom.setCellValueFactory(new PropertyValueFactory<Tournoi, String>("nom_tournoi"));
        tableview_tournoi_type.setCellValueFactory(new PropertyValueFactory<Tournoi, String>("type_tournoi"));
        tableview_tournoi_date.setCellValueFactory(new PropertyValueFactory<Tournoi, Date>("date_tournoi"));
        tableview_tournoi_description.setCellValueFactory(new PropertyValueFactory<Tournoi, String>("description_tournoi"));
        tableview_tournoi.setItems(tournoiObservableList);
    }

    public void initialize() {
        refreshTableviewTournoi();
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
    void goToAfficheMatchs(MouseEvent event) {
        navigationService.goToPage("/pages/tournoi_match/AfficheMatchs.fxml", titre_liste_tournois);
    }

    @FXML
    void goToAfficheJoueurs(MouseEvent event) {
        navigationService.goToPage("/pages/joueur/AfficheJoueurs.fxml", titre_liste_tournois);
    }

    @FXML
    void goToAfficheTournois(MouseEvent event) {
        refreshTableviewTournoi();
    }

    @FXML
    void goToAfficheEquipes(MouseEvent event) {
        navigationService.goToPage("/pages/equipe/AfficheEquipes.fxml", titre_liste_tournois);
    }
}
