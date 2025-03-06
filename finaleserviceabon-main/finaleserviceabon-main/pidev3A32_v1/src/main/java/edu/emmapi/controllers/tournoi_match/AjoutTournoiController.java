package edu.emmapi.controllers.tournoi_match;

import edu.emmapi.controllers.components.MusicPlayer;
import edu.emmapi.entities.tournoi_match.Tournoi;
import edu.emmapi.services.tournoi_match.TournoiService;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;

public class AjoutTournoiController {

    @FXML
    private DatePicker date_tournoi;

    @FXML
    private ComboBox<String> liste_type;

    @FXML
    private TextField nom_tournoi;

    @FXML
    private TextField trounoi_description;

    @FXML
    private ImageView terrain_bg;

    @FXML
    private VBox error;

    @FXML
    private Label message;

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

    TournoiService tournoiService = new TournoiService();

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

    public void showError(String message, String color){
        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        this.message.setText(message);
        error.setStyle("-fx-background-color: " + color);
        error.setVisible(true);
        pause.setOnFinished(event -> {
            error.setVisible(false);
            if (color.equals("#66ffcc")) {
                annulerAjout(new ActionEvent());
            }
        });
        pause.play();
    }

    @FXML
    void annulerAjout(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/pages/tournoi_match/AfficheTournois.fxml"));
        try {
            Parent parent = loader.load();
            date_tournoi.getScene().setRoot(parent);
        } catch (IOException e) {
            showError("Echec de navigation", "#F05A5A");
        }
    }

    @FXML
    void confirmerAjout(ActionEvent event) {
        try {
            if(nom_tournoi.getText().isEmpty()){
                showError("Le nom ne peut pas être vide", "#F05A5A");
            } else if (tournoiService.tournoiExiste(nom_tournoi.getText())){
                showError("Le nom est déja utilisé", "#F05A5A");
            }else if (date_tournoi.getValue()==null) {
                showError("Veuillez choisir une date", "#F05A5A");
            } else if (liste_type.getValue()==null) {
                showError("Veuillez choisir une type de tournoi", "#F05A5A");
            } else {
                Tournoi tournoi = new Tournoi(nom_tournoi.getText(), liste_type.getValue(), Date.valueOf(date_tournoi.getValue()), trounoi_description.getText());
                tournoiService.addEntity(tournoi);
                showError("Tournoi ajoutée avec succès", "#66ffcc");
            }
        } catch (Exception e) {
            showError("Veuillez remplir correctement le formulaire", "#F05A5A");
        }
    }

    @FXML
    public void refreshImageTerrain(ActionEvent event){
        try {
            Image image = new Image(getClass().getResource("/images/backgrounds/" + liste_type.getValue() + ".png").toExternalForm());
            terrain_bg.setImage(image);
        }catch (Exception e) {
            Image image = new Image(getClass().getResource("/images/backgrounds/Placeholder.png").toExternalForm());
            terrain_bg.setImage(image);
        }
    }

    public void initialize(){
        liste_type.setItems(FXCollections.observableArrayList("Football", "Basketball", "Volley-ball", "Baseball", "Rugby", "Handball","Tennis", "Badminton", "Tennis de table", "Squash","Golf", "Bowling", "Bocce", "Croquet","Water-polo", "Dodgeball", "Sepak Takraw", "Lacrosse"));
        date_tournoi.setValue(LocalDate.now());
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
