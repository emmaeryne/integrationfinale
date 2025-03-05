package edu.emmapi.interfaces;

import java.sql.SQLException;
import java.util.List;

public interface IUserService<T> {
    public void addEntity(T t) throws SQLException;
    public void deleteEntity(T t);
    public void updateEntity( T t, boolean newEtat) throws SQLException;
    public List<T> getAllData();
}
