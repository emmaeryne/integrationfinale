package edu.emmapi.controllers.equipe;

import edu.emmapi.controllers.components.MusicPlayer;
import edu.emmapi.entities.equipe.Equipe;
import edu.emmapi.entities.joueur.Joueur;
import edu.emmapi.services.equipe.EquipeService;
import edu.emmapi.services.joueur.JoueurService;
import edu.emmapi.services.navigation.NavigationService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;

import java.sql.SQLException;

public class ModifierEquipeController {

    @FXML
    private TextField nom_equipe;

    @FXML
    private TableView<Joueur> tableview_joueur;

    @FXML
    private TableColumn<Joueur, Integer> tableview_joueur_equipe;

    @FXML
    private TableColumn<Joueur, Integer> tableview_joueur_id;

    @FXML
    private TableColumn<Joueur, String> tableview_joueur_nom;

    @FXML
    private TableView<Joueur> tableview_joueur_selectionne;

    @FXML
    private TableColumn<Joueur, Integer> tableview_joueur_selectionne_equipe;

    @FXML
    private TableColumn<Joueur, Integer> tableview_joueur_selectionne_id;

    @FXML
    private TableColumn<Joueur, String> tableview_joueur_selectionne_nom;

    @FXML
    private ComboBox<String> type_equipe;

    @FXML
    private VBox error;

    @FXML
    private Label message;

    JoueurService joueurService = new JoueurService();
    EquipeService equipeService = new EquipeService();
    NavigationService navigationService = new NavigationService();

    private ObservableList<Joueur> joueursList = FXCollections.observableArrayList(joueurService.getAllData());
    private ObservableList<Joueur> joueursSelectionnesList = FXCollections.observableArrayList();

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
    void annulerAjout(ActionEvent event) {
        navigationService.goToPage("/pages/joueur/AfficheJoueurs.fxml", message);
    }

    @FXML
    void confirmerAjout(ActionEvent event) {
        Equipe equipe = new Equipe(nom_equipe.getText(), type_equipe.getValue());
        try {
            equipeService.addEntity(equipe);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        int id_equipe = equipeService.idEquipeFromNomType(equipe.getNom_equipe(), equipe.getType_equipe());
        for (Joueur joueur : joueursSelectionnesList){
            joueur.setId_equipe(id_equipe);
            joueurService.updateJoueurEquipe(joueur.getId_joueur(), joueur.getId_equipe());
        }
        navigationService.goToPage("/pages/joueur/AfficheJoueurs.fxml",message);
    }

    public void refreshTableviewJoueur(){
        tableview_joueur_id.setCellValueFactory(new PropertyValueFactory<Joueur, Integer>("id_joueur"));
        tableview_joueur_nom.setCellValueFactory(new PropertyValueFactory<Joueur, String>("nom_joueur"));
        tableview_joueur_equipe.setCellValueFactory(new PropertyValueFactory<Joueur, Integer>("id_equipe"));
        tableview_joueur.setItems(joueursList);
        tableview_joueur_selectionne_id.setCellValueFactory(new PropertyValueFactory<Joueur, Integer>("id_joueur"));
        tableview_joueur_selectionne_nom.setCellValueFactory(new PropertyValueFactory<Joueur, String>("nom_joueur"));
        tableview_joueur_selectionne_equipe.setCellValueFactory(new PropertyValueFactory<Joueur, Integer>("id_equipe"));
        tableview_joueur_selectionne.setItems(joueursSelectionnesList);
    }

    public void initialize(){
        refreshTableviewJoueur();
        enableDragAndDrop();
        type_equipe.setItems(FXCollections.observableArrayList("Football", "Basketball", "Volley-ball", "Baseball", "Rugby", "Handball","Tennis", "Badminton", "Tennis de table", "Squash","Golf", "Bowling", "Bocce", "Croquet","Water-polo", "Dodgeball", "Sepak Takraw", "Lacrosse"));
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

    private void enableDragAndDrop() {
        tableview_joueur.setRowFactory(tv -> {
            TableRow<Joueur> row = new TableRow<>();

            row.setOnDragDetected(event -> {
                if (!row.isEmpty()) {
                    Dragboard db = row.startDragAndDrop(TransferMode.MOVE);
                    ClipboardContent content = new ClipboardContent();
                    content.putString(row.getItem().getNom_joueur());
                    db.setContent(content);
                    event.consume();
                }
            });

            return row;
        });

        tableview_joueur_selectionne.setOnDragOver(event -> {
            if (event.getGestureSource() != tableview_joueur_selectionne && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        tableview_joueur_selectionne.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;

            if (db.hasString()) {
                String draggedName = db.getString();

                Joueur draggedJoueur = joueursList.stream()
                        .filter(j -> j.getNom_joueur().equals(draggedName))
                        .findFirst()
                        .orElse(null);

                if (draggedJoueur != null) {
                    joueursList.remove(draggedJoueur);
                    joueursSelectionnesList.add(draggedJoueur);
                    success = true;
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });

        tableview_joueur_selectionne.setRowFactory(tv -> {
            TableRow<Joueur> row = new TableRow<>();

            row.setOnDragDetected(event -> {
                if (!row.isEmpty()) {
                    Dragboard db = row.startDragAndDrop(TransferMode.MOVE);
                    ClipboardContent content = new ClipboardContent();
                    content.putString(row.getItem().getNom_joueur());
                    db.setContent(content);
                    event.consume();
                }
            });

            return row;
        });

        tableview_joueur.setOnDragOver(event -> {
            if (event.getGestureSource() != tableview_joueur && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        tableview_joueur.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;

            if (db.hasString()) {
                String draggedName = db.getString();

                Joueur draggedJoueur = joueursSelectionnesList.stream()
                        .filter(j -> j.getNom_joueur().equals(draggedName))
                        .findFirst()
                        .orElse(null);

                if (draggedJoueur != null) {
                    joueursSelectionnesList.remove(draggedJoueur);
                    joueursList.add(draggedJoueur);
                    success = true;
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }


}
