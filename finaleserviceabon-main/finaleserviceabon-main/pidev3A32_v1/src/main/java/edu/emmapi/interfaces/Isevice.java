package edu.pidev3a32.interfaces;
import java.sql.SQLException;
import java.util.List;
public interface Isevice <T>{
    public void addplanning(T t) throws SQLException;
    public void addplanning2(T t);
    public void updateplanning(T t);
    public void deletePlanning(T t);
    public List<T> getAllDataplanning();

}
