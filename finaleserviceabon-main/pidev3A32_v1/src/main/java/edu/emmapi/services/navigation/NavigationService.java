package edu.emmapi.services.navigation;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;

public class NavigationService {
    public NavigationService() {
    }

    public void goToPage(String path, Label anchor){
        FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
        System.out.println(loader);
        try {
            Parent parent = loader.load();
            anchor.getScene().setRoot(parent);
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Echec de navigation");
            alert.setContentText(e.getMessage());
            alert.show();
        }
    }
}
