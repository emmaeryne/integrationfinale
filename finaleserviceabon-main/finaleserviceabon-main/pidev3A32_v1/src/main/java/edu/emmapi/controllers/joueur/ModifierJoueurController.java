package edu.emmapi.controllers.joueur;

import edu.emmapi.controllers.components.MusicPlayer;
import edu.emmapi.entities.joueur.Joueur;
import edu.emmapi.services.joueur.JoueurService;
import edu.emmapi.services.navigation.NavigationService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class ModifierJoueurController {

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
    private Label id;

    public boolean isPlaying = false;

    private final Image toggle_down = new Image(getClass().getResource("/images/icons/down.png").toExternalForm());
    private final Image toggle_up = new Image(getClass().getResource("/images/icons/up.png").toExternalForm());
    private final Image play = new Image(getClass().getResource("/images/icons/play.png").toExternalForm());
    private final Image pause = new Image(getClass().getResource("/images/icons/pause.png").toExternalForm());


    NavigationService navigationService = new NavigationService();
    JoueurService joueurService = new JoueurService();


    @FXML
    void ModifierJouer(ActionEvent event) {
        Joueur joueur = new Joueur(nom_joueur.getText(), Integer.parseInt(cin_joueur.getText()));
        joueurService.updateEntity(Integer.parseInt(id.getText()), joueur);
    }

    @FXML
    void annuler(ActionEvent event) {
        navigationService.goToPage("/pages/joueur/AfficheJoueurs.fxml",message);
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

    public void setIdJoueur(int id){
        this.id.setText(String.valueOf(id));
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
