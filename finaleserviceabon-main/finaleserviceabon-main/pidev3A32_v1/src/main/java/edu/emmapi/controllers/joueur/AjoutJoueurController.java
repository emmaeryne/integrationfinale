package edu.emmapi.controllers.joueur;

import edu.emmapi.controllers.components.MusicPlayer;
import edu.emmapi.entities.joueur.Joueur;
import edu.emmapi.services.joueur.JoueurService;
import edu.emmapi.services.navigation.NavigationService;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.sql.SQLException;

public class AjoutJoueurController {

    @FXML
    private TextField cin_joueur;

    @FXML
    private VBox error;

    @FXML
    private Label message;

    @FXML
    private VBox music_player;

    @FXML
    private TextField nom_joueur;

    @FXML
    private ImageView play_button;

    @FXML
    private ImageView terrain_bg;

    @FXML
    private ImageView toggler;

    @FXML
    private TextField photo_joueur;

    @FXML
    private ImageView view_photo;

    public boolean isPlaying = false;

    private final Image toggle_down = new Image(getClass().getResource("/images/icons/down.png").toExternalForm());
    private final Image toggle_up = new Image(getClass().getResource("/images/icons/up.png").toExternalForm());
    private final Image play = new Image(getClass().getResource("/images/icons/play.png").toExternalForm());
    private final Image pause = new Image(getClass().getResource("/images/icons/pause.png").toExternalForm());


    NavigationService navigationService = new NavigationService();
    JoueurService joueurService = new JoueurService();

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
        navigationService.goToPage("/pages/joueur/AfficheJoueurs.fxml",message);
    }

    @FXML
    void confirmerAjout(ActionEvent event) {
        if (nom_joueur.getText().isEmpty()){
            showError("Donner un nom pour votre joueur svp", "#F05A5A");
        }
        else if (!nom_joueur.getText().matches("^[A-Za-z]+\\s[A-Za-z]+$")){
            showError("Donner un nom valide svp", "#F05A5A");
        }
        else if (cin_joueur.getText().isEmpty()){
            showError("Donner un cin pour votre joueur svp", "#F05A5A");
        }
        else if (!cin_joueur.getText().matches("^[0-9]{8}$")) {
            showError("Donner un cin valide svp", "#F05A5A");
        }
        else {
        Joueur joueur = new Joueur(nom_joueur.getText(), Integer.parseInt(cin_joueur.getText()), photo_joueur.getText());
        if (joueurService.joueurExiste(joueur)){
            showError("Ce joueur existe déja", "#F05A5A");
        }
        else {
            try {
                joueurService.addEntity(joueur);
                showError("Joueur modifiée avec succès", "#66ffcc");
                navigationService.goToPage("/pages/joueur/AfficheJoueurs.fxml",message);
            } catch (SQLException e) {
                showError(e.getMessage(), "#F05A5A");
            }
        }
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

    @FXML
    void updatePhoto(KeyEvent event) {
        view_photo.setImage(new Image(photo_joueur.getText()));
    }

    public void initialize(){
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
