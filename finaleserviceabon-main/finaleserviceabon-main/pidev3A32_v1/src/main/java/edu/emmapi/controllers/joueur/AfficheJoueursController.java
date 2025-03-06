package edu.emmapi.controllers.joueur;

import edu.emmapi.controllers.components.MusicPlayer;
import edu.emmapi.entities.joueur.Joueur;
import edu.emmapi.services.joueur.JoueurService;
import edu.emmapi.services.navigation.NavigationService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Optional;

public class AfficheJoueursController {

    @FXML
    private VBox beacon;

    @FXML
    private VBox error;

    @FXML
    private Label message;

    @FXML
    private VBox music_player;

    @FXML
    private ImageView play_button;

    @FXML
    private TableView<Joueur> tableview_joueur;

    @FXML
    private TableColumn<Joueur, Integer> tableview_joueur_cin;

    @FXML
    private TableColumn<Joueur, Integer> tableview_joueur_equipe;

    @FXML
    private TableColumn<Joueur, Integer> tableview_joueur_id;

    @FXML
    private TableColumn<Joueur, String> tableview_joueur_nom;

    @FXML
    private Label titre_liste_joueurs;

    @FXML
    private ImageView toggler;

    @FXML
    private HBox wrapper;

    @FXML
    private TextField filterField;

    private FilteredList<Joueur> filteredData;


    public boolean isPlaying = false;

    private final Image toggle_down = new Image(getClass().getResource("/images/icons/down.png").toExternalForm());
    private final Image toggle_up = new Image(getClass().getResource("/images/icons/up.png").toExternalForm());
    private final Image play = new Image(getClass().getResource("/images/icons/play.png").toExternalForm());
    private final Image pause = new Image(getClass().getResource("/images/icons/pause.png").toExternalForm());


    NavigationService navigationService = new NavigationService();
    JoueurService joueurService = new JoueurService();


    @FXML
    void filter(KeyEvent event) {
        String filterText = filterField.getText().trim().toLowerCase();
        filteredData.setPredicate(joueur -> {
            if (filterText.isEmpty()) {
                return true;
            }
            return String.valueOf(joueur.getId_joueur()).contains(filterText) ||
                    joueur.getNom_joueur().toLowerCase().contains(filterText) ||
                    String.valueOf(joueur.getId_equipe()).contains(filterText) ||
                    String.valueOf(joueur.getCin()).contains(filterText);
        });
    }

    @FXML
    void goToAfficheEquipes(MouseEvent event) {
        navigationService.goToPage("/pages/equipe/AfficheEquipes.fxml", titre_liste_joueurs);
    }

    @FXML
    void goToAfficheJoueurs(MouseEvent event) {
        refreshTableviewJoueur();
    }

    @FXML
    void goToAfficheMatchs(MouseEvent event) {
        navigationService.goToPage("/pages/tournoi_match/AfficheMatchs.fxml", titre_liste_joueurs);
    }

    @FXML
    void goToAfficheTournois(MouseEvent event) {
        navigationService.goToPage("/pages/tournoi_match/AfficheTournois.fxml", titre_liste_joueurs);
    }

    @FXML
    void goToAjout(ActionEvent event) {
        navigationService.goToPage("/pages/joueur/AjoutJoueur.fxml", titre_liste_joueurs);
    }

    @FXML
    void modifierJoueur(ActionEvent event) {
        int selected_joueur_id = tableview_joueur.getSelectionModel().getSelectedItem().getId_joueur();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/pages/joueur/ModifierJoueur.fxml"));
        Parent parent = null;
        try {
            parent = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ModifierJoueurController modifierJoueurController = loader.getController();
        modifierJoueurController.setIdJoueur(selected_joueur_id);
        modifierJoueurController.setJoueur();
        tableview_joueur.getScene().setRoot(parent);
    }

    @FXML
    void supprimerJoueur(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText(null);
        alert.setContentText("Êtes-vous sûr de vouloir supprimer ce joueur ?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            joueurService.deleteEntity(tableview_joueur.getSelectionModel().getSelectedItem().getId_joueur());
            tableview_joueur.getItems().clear();
            refreshTableviewJoueur();
        }
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

    public void refreshTableviewJoueur(){
        ObservableList<Joueur> joueurObservableList = FXCollections.observableArrayList(
                joueurService.getAllData()
        );

        filteredData = new FilteredList<>(joueurObservableList, p -> true);

        tableview_joueur_id.setCellValueFactory(new PropertyValueFactory<Joueur, Integer>("id_joueur"));
        tableview_joueur_nom.setCellValueFactory(new PropertyValueFactory<Joueur, String>("nom_joueur"));
        tableview_joueur_equipe.setCellValueFactory(new PropertyValueFactory<Joueur, Integer>("id_equipe"));
        tableview_joueur_cin.setCellValueFactory(new PropertyValueFactory<Joueur, Integer>("cin"));
        tableview_joueur.setItems(filteredData);

        if (filterField != null) {
            filterField.setText("");
        }
    }

    public void initialize(){
        refreshTableviewJoueur();
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
}
