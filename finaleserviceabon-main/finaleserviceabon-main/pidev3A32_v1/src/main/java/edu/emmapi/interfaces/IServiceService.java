/*package edu.emmapi.interfaces;

import edu.emmapi.entities.Service;
import java.sql.SQLException;
import java.util.List;

public interface IServiceService {
    Service ajouterService(Service service);

    void addService(Service service) throws SQLException;
    void deleteService(int idService) throws SQLException;

    void deleteService(Long idService) throws SQLException;

    void updateService(Service service) throws SQLException;
    List<Service> getAllServices() throws SQLException;
    Service getServiceById(int idService) throws SQLException;
    List<Service> searchServices(String keyword) throws SQLException;
    List<Service> getServicesByCategory(String category) throws SQLException;

    Service getServiceById(Long idService);





}*/
/*package edu.emmapi.interfaces;

import edu.emmapi.entities.Service;

import java.sql.SQLException;
import java.util.List;

public interface IServiceService {
    Service ajouterService(Service service) throws SQLException;

    void deleteService(Long idService) throws SQLException;

    void updateService(Service service) throws SQLException;

    List<Service> getAllServices();

    Service getServiceById(Long idService);

    Service getServiceById(int idService) throws SQLException;

    List<Service> searchServices(String keyword);

    List<Service> getServicesByCategory(String category);
}*/

package edu.emmapi.interfaces;

import edu.emmapi.entities.Service;

import java.sql.SQLException;
import java.util.List;

public interface IServiceService {
    Service ajouterService(Service service) throws SQLException;

    void deleteService(Long idService) throws SQLException;

    void updateService(Service service) throws SQLException;

    List<Service> getAllServices() throws SQLException;

    Service getServiceById(Long idService) throws SQLException;

    Service getServiceById(int idService) throws SQLException;

    List<Service> searchServices(String keyword) throws SQLException;

    List<Service> getServicesByCategory(String category) throws SQLException;

    void updateServiceNote(Long serviceId, double note) throws SQLException;

    void incrementReservationCount(Long serviceId) throws SQLException;
}
