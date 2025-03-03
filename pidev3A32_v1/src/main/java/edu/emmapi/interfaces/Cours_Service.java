package edu.emmapi.interfaces;

import java.sql.SQLException;
import java.util.List;

public interface Cours_Service <P>{
    public void addcours(P p) throws SQLException;

    public void updatecour(P p);
    public void deletecour(P p);
    public List<P> getAllDatacour();

}
