package edu.emmapi.interfaces;

import java.sql.SQLException;
import java.util.List;

public interface Participant_Service <P>{
    public void addparticipant(P p)throws SQLException;

    public void updateparticipant(P p)throws SQLException;
    public void deleteparticipant(P p)throws SQLException;
    public List<P> getAllDataparticipant()throws SQLException;;
}
