package edu.emmapi.controllers.tournoi_match;

import edu.emmapi.controllers.components.MusicPlayer;
import edu.emmapi.entities.tournoi_match.Tournoi;
import edu.emmapi.services.tournoi_match.TournoiService;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.Date;

public class ModifierTournoiController {

    @FXML
    private Label tournoi_id_label;

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

    private int id_tournoi;

    private TournoiService tournoiService = new TournoiService();

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
    void annulerAjout(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/pages/tournoi_match/AfficheTournois.fxml"));
        try {
            Parent parent = loader.load();
            date_tournoi.getScene().setRoot(parent);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Echec de navigation");
            alert.setContentText(e.getMessage());
            alert.show();
        }
    }

    @FXML
    void confirmerModification(ActionEvent event) {
        try {
            Tournoi tournoi = new Tournoi(nom_tournoi.getText(), liste_type.getValue(), Date.valueOf(date_tournoi.getValue()), trounoi_description.getText());
            tournoiService.updateEntity(id_tournoi, tournoi);
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Tournoi modifiée avec succès");
            alert.setContentText(tournoi.toString());
            alert.show();
            annulerAjout(event);
        }catch ( Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Veuillez remplir correctement le formulaire");
            alert.setContentText(e.getMessage());
            alert.show();
        }
    }

    public void setId_tournoi(int id_tournoi) {
        this.id_tournoi = id_tournoi;
        refreshModifierTournoi();
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

    public void refreshModifierTournoi(){
        Tournoi tournoi = tournoiService.getTournoiById(id_tournoi);
        //titre
        tournoi_id_label.setText(String.valueOf(id_tournoi));

        //nom
        nom_tournoi.setText(tournoi.getNom_tournoi());


        //date
        date_tournoi.setValue(tournoi.getDate_tournoi().toLocalDate());

        //type
        liste_type.setItems(FXCollections.observableArrayList("Football", "Basketball", "Volley-ball", "Baseball", "Rugby", "Handball","Tennis", "Badminton", "Tennis de table", "Squash","Golf", "Bowling", "Bocce", "Croquet","Water-polo", "Dodgeball", "Sepak Takraw", "Lacrosse"));
        liste_type.setValue(tournoi.getType_tournoi());

        //description
        trounoi_description.setText(tournoi.getDescription_tournoi());

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
