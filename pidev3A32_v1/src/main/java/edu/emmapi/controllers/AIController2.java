package edu.emmapi.controllers;

import edu.emmapi.services.OpenAIService;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class AIController2 {
    @FXML
    private TextArea userInput;

    @FXML
    private TextArea aiResponseArea;

    @FXML
    public void handleAIButtonClick() {
        String userMessage = userInput.getText();
        if (userMessage.isEmpty()) {
            aiResponseArea.setText("Veuillez entrer un message !");
            return;
        }

        String response = OpenAIService.getAIResponse(userMessage);
        aiResponseArea.setText(response);
    }
}
