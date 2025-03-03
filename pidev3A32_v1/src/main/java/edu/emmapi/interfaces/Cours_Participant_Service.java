package edu.emmapi.interfaces;

import java.sql.SQLException;
import java.util.List;

public interface Cours_Participant_Service <P> {
    void addCoursParticipant(P entity) throws SQLException;
    void updateCoursParticipant(P entity) throws SQLException;
    void deleteCoursParticipant(P entity) throws SQLException;
    List<P> getAllDataCoursParticipant() throws SQLException;
    List<P> getCoursByParticipant(int participantId) throws SQLException;
}
