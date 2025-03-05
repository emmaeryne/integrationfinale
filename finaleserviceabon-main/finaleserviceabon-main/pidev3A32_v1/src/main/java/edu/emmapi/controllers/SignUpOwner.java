package edu.emmapi.controllers;

import edu.emmapi.entities.Owner;
import edu.emmapi.entities.security_questions;
import edu.emmapi.services.OwnerService;
import edu.emmapi.services.SecurityQuestionDao;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import edu.emmapi.tools.MyConnection;

public class SignUpOwner {

    @FXML
    private TextField email;

    @FXML
    private Label email_error;

    @FXML
    private Label errorLabel;

    @FXML
    private Label firstNameErrorLabel;

    @FXML
    private TextField firstNameField;

    @FXML
    private Label officialIdErrorLabel;

    @FXML
    private TextField officialIdField;

    @FXML
    private PasswordField password;

    @FXML
    private PasswordField password1;

    @FXML
    private Label passwordErrorLabel;

    @FXML
    private Button sinscrireButton;

    @FXML
    private ComboBox<String> SelectQ;

    @FXML
    private TextField Answer;

    private final OwnerService ownerService = new OwnerService();
    private final Map<String, Integer> questionMap = new HashMap<>();

    private static final String EMAIL_ERROR = "Invalid email format.";
    private static final String PASSWORD_ERROR = "Password must start with uppercase and contain letters and numbers.";
    private static final String FIRST_NAME_ERROR = "First name must start with uppercase letter.";
    private static final String OFFICIAL_ID_ERROR = "Official ID must be 8 digits!";
    private static final String SECURITY_QUESTION_ERROR = "Please select a security question.";
    private static final String SECURITY_ANSWER_ERROR = "Please provide an answer to the security question.";

    @FXML
    public void initialize() {
        loadSecurityQuestions();

        // Set default error messages
        email_error.setText(EMAIL_ERROR);
        firstNameErrorLabel.setText(FIRST_NAME_ERROR);
        officialIdErrorLabel.setText(OFFICIAL_ID_ERROR);
        passwordErrorLabel.setText(PASSWORD_ERROR);

        // Email listener (validate email format)
        email.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!isValidEmail(newValue)) {
                email_error.setVisible(true);
                email_error.setText(EMAIL_ERROR);
            } else {
                email_error.setVisible(false);
            }
        });

        // Listener for official ID field (Only digits, exactly 8 digits)
        officialIdField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d{0,8}")) {
                officialIdField.setText(oldValue); // Revert back if not valid
                officialIdErrorLabel.setText(OFFICIAL_ID_ERROR);
            } else {
                officialIdErrorLabel.setText("");
            }
        });

        // Listener for first name field (Starts with uppercase letter)
        firstNameField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[A-Z][a-zA-Z]*")) {
                firstNameErrorLabel.setText(FIRST_NAME_ERROR);
            } else {
                firstNameErrorLabel.setText("");
            }
        });

        // Listener for password field (Starts with uppercase letter and contains both letters and digits)
        password.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[A-Z][A-Za-z0-9]*")) {
                passwordErrorLabel.setText(PASSWORD_ERROR);
            } else {
                passwordErrorLabel.setText("");
            }
        });
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean isValidFirstName(String firstName) {
        return firstName.matches("[A-Z][a-zA-Z]*");
    }

    private boolean isValidOfficialId(String officialId) {
        return officialId.matches("\\d{8}");
    }

    private boolean isValidPassword(String password) {
        return password.matches("[A-Z][A-Za-z0-9]*");
    }

//    private boolean verifyEmailWithHunter(String email) {
//        OkHttpClient client = new OkHttpClient();
//        String apiKey = "51ebed6f136f3456dc8bb794c07d8270788706fa"; // Replace with environment variable
//
//        String url = "https://api.hunter.io/v2/email-verifier";
//        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
//        urlBuilder.addQueryParameter("email", email);
//        urlBuilder.addQueryParameter("api_key", apiKey);
//        String finalUrl = urlBuilder.build().toString();
//
//        Request request = new Request.Builder().url(finalUrl).build();
//
//        try (Response response = client.newCall(request).execute()) {
//            if (response.isSuccessful()) {
//                String responseBody = response.body().string();
//                System.out.println("Réponse de l'API : " + responseBody);
//
//                JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
//                String status = jsonResponse.getAsJsonObject("data").get("status").getAsString();
//
//                return "valid".equals(status) && "deliverable".equals(jsonResponse.getAsJsonObject("data").get("result").getAsString());
//            } else {
//                System.out.println("Erreur de l'API : " + response.code() + " - " + response.message());
//                return false;
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }

    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }




    private void loadSecurityQuestions() {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/hive2", "root", "")) {
            SecurityQuestionDao securityQuestionDao = new SecurityQuestionDao(connection);
            List<security_questions> questions = securityQuestionDao.getAllSecurityQuestions();

            questionMap.clear();
            SelectQ.getItems().clear();

            for (security_questions question : questions) {
                questionMap.put(question.getQuestionText(), question.getId());
            }

            SelectQ.getItems().addAll(questionMap.keySet());
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les questions de sécurité depuis la base de données. Vérifiez votre connexion.");
        }
    }

    @FXML
    void handleSignUp(ActionEvent event) throws SQLException, IOException {
        String firstName = firstNameField.getText();
        String officialId = officialIdField.getText();
        String emailValue = email.getText();
        String passwordValue = password.getText();
        String securityAnswer = Answer.getText();
        String securityQuestion = SelectQ.getValue();

        if (!isValidEmail(emailValue)) {
            showAlert(Alert.AlertType.ERROR, "Error", EMAIL_ERROR);
            return;
        }
        if (!isValidFirstName(firstName)) {
            showAlert(Alert.AlertType.ERROR, "Error", FIRST_NAME_ERROR);
            return;
        }
        if (!isValidOfficialId(officialId)) {
            showAlert(Alert.AlertType.ERROR, "Error", OFFICIAL_ID_ERROR);
            return;
        }
        if (!isValidPassword(passwordValue)) {
            showAlert(Alert.AlertType.ERROR, "Error", PASSWORD_ERROR);
            return;
        }
        if (!password.getText().equals(password1.getText())) {
            showAlert(Alert.AlertType.ERROR, "Error", "Passwords do not match!");
            return;
        }
        if (securityQuestion == null || securityQuestion.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", SECURITY_QUESTION_ERROR);
            return;
        }
        if (securityAnswer == null || securityAnswer.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", SECURITY_ANSWER_ERROR);
            return;
        }
boolean check=true;
        if (check) {
            showAlert(Alert.AlertType.ERROR, "Error", "The email is not valid according to Hunter.");
            return;
        }

        if (ownerService.chercherEmail(emailValue) != null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Email already exists!");
            return;
        }

        boolean active = false;
        String role = "OWNER";
        int securityQuestionId = questionMap.get(securityQuestion);
        String mdpHache= BCrypt.hashpw(passwordValue, BCrypt.gensalt());


        Owner owner = new Owner(firstName, emailValue, mdpHache, active, role, securityQuestionId, securityAnswer, officialId);
        ownerService.addEntity(owner);

        showAlert(Alert.AlertType.INFORMATION, "Success", "Registration successful!");
        loginBack();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    public void loginBack() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/logIn.fxml"));
        Scene currentScene = email.getScene();
        currentScene.setRoot(root);
        Stage stage = (Stage) currentScene.getWindow();
        stage.setWidth(457);
        stage.setHeight(1000);
    }
}