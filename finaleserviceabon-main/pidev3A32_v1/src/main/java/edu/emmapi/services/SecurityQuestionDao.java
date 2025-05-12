package edu.emmapi.services;

import edu.emmapi.entities.security_questions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SecurityQuestionDao {

    private Connection connection;

    public SecurityQuestionDao(Connection connection) {
        this.connection = connection;
    }

    // Méthode pour récupérer toutes les questions de sécurité
    public List<security_questions> getAllSecurityQuestions() {
        List<security_questions> questions = new ArrayList<>();
        String sql = "SELECT id, question_Text FROM security_questions";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String questionText = resultSet.getString("question_Text");
                questions.add(new security_questions(id, questionText));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return questions;
    }
}