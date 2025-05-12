package edu.emmapi.interfaces.tarek;

import java.sql.SQLException;
import java.util.List;

public interface IService<T> {
    public void addEntity(T t) throws SQLException;
    public void deleteEntity(int id) throws SQLException;
    public void updateEntity(int id, T t);
    public List<T> getAllData();
}
