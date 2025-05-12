package edu.emmapi.entities;

public class security_questions {
    private int id; // Identifiant unique de la question
    private String questionText; // Texte de la question de sécurité

    // Constructeur
    public security_questions(int id, String questionText) {
        this.id = id;
        this.questionText = questionText;
    }

    // Constructeur sans id (utile pour l'ajout de nouvelles questions)
    public security_questions(String questionText) {
        this.questionText = questionText;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    @Override
    public String toString() {
        return "SecurityQuestion{" +
                "id=" + id +
                ", questionText='" + questionText + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        security_questions that = (security_questions) o;
        return id == that.id && questionText.equals(that.questionText);
    }

    @Override
    public int hashCode() {
        return 31 * id + (questionText != null ? questionText.hashCode() : 0);
    }
}
