package edu.emmapi.interfaces;

import java.sql.SQLException;
import java.util.List;

public interface Planning_Service <P>{
    public void addplanning(P p) throws SQLException;

    public void updateplanning(P p)throws SQLException ;
    public void deleteplanning(P p)throws SQLException;
    public List<P> getAllDataplannig() throws SQLException ;
}
