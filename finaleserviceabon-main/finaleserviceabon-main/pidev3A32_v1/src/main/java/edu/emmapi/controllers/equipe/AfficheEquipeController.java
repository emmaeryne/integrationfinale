package edu.emmapi.controllers.equipe;

import edu.emmapi.controllers.components.MusicPlayer;
import edu.emmapi.controllers.joueur.ModifierJoueurController;
import edu.emmapi.entities.equipe.Equipe;
import edu.emmapi.services.equipe.EquipeService;
import edu.emmapi.services.navigation.NavigationService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.io.IOException;


public class AfficheEquipeController {

    @FXML
    private TableColumn<Equipe, Integer> tableview_equipe_id;

    @FXML
    private TableColumn<Equipe, String> tableview_equipe_nom;

    @FXML
    private TableView<Equipe> tableview_equipes;

    @FXML
    private Label titre_liste_equipes;

    EquipeService equipeService = new EquipeService();
    NavigationService navigationService = new NavigationService();

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
    void goToAfficheEquipes(MouseEvent event) {
        refreshTableviewEquipe();
    }

    @FXML
    void goToAfficheMatchs(MouseEvent event) {
        navigationService.goToPage("/pages/tournoi_match/AfficheMatchs.fxml", titre_liste_equipes);
    }

    @FXML
    void goToAfficheJoueurs(MouseEvent event) {
        navigationService.goToPage("/pages/joueur/AfficheJoueurs.fxml", titre_liste_equipes);
    }

    @FXML
    void goToAfficheTournois(MouseEvent event) {
        navigationService.goToPage("/pages/tournoi_match/AfficheTournois.fxml", titre_liste_equipes);
    }

    @FXML
    void goToAjout(ActionEvent event) {
        navigationService.goToPage("/pages/equipe/AjoutEquipe.fxml", titre_liste_equipes);
    }

    @FXML
    void modifierEquipe(ActionEvent event) {
        int selected_equipe_id = tableview_equipes.getSelectionModel().getSelectedItem().getId_equipe();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/pages/equipe/ModifierEquipe.fxml"));
        Parent parent = null;
        try {
            parent = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ModifierEquipeController modifierEquipeController = loader.getController();
        modifierEquipeController.setIdEquipe(selected_equipe_id);
        Equipe equipe = equipeService.getEquipeById(selected_equipe_id);
        System.out.println(equipe.toString());
        modifierEquipeController.setEquipe(equipe);

        tableview_equipes.getScene().setRoot(parent);
    }

    @FXML
    void supprimerEquipe(ActionEvent event) {

    }

    public void refreshTableviewEquipe(){
        ObservableList<Equipe> equipeObservableList = FXCollections.observableArrayList(
                equipeService.getAllData()
        );

        tableview_equipe_id.setCellValueFactory(new PropertyValueFactory<Equipe, Integer>("id_equipe"));
        tableview_equipe_nom.setCellValueFactory(new PropertyValueFactory<Equipe, String>("nom_equipe"));
        tableview_equipes.setItems(equipeObservableList);
    }

    public void initialize(){
        refreshTableviewEquipe();
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
