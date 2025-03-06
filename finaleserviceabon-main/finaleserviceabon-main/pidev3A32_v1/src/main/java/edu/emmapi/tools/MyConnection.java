/*package edu.emmapi.tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/hive2"; // À adapter si besoin
    private static final String LOGIN = "root"; // À adapter si besoin
    private static final String PWD = ""; // À adapter si besoin
    private static MyConnection instance;
    private Connection cnx;

    // Constructeur privé pour éviter la création d'instances supplémentaires
    private MyConnection() {
        try {
            // Connexion à la base de données
            cnx = DriverManager.getConnection(URL, LOGIN, PWD);
            System.out.println("Connection established!");
        } catch (SQLException e) {
            // Gérer l'exception proprement
            System.err.println("Error connection: " + e.getMessage());
        }
    }

    // Méthode pour obtenir l'instance unique
    public static MyConnection getInstance() {
        if (instance == null) {
            instance = new MyConnection();
        }
        return instance;
    }

    // Méthode pour obtenir la connexion
    public Connection getCnx() {
        return cnx;
    }

    // Méthode pour fermer la connexion proprement, à appeler explicitement
    public void closeConnection() {
        if (cnx != null) {
            try {
                cnx.close();
                System.out.println("Connection closed.");
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
}*/
package edu.emmapi.tools;

import com.mysql.cj.x.protobuf.MysqlxDatatypes;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/hive1"; // À adapter si besoin
    private static final String LOGIN = "root"; // À adapter si besoin
    private static final String PWD = ""; // À adapter si besoin
    private static MyConnection instance;
    private Connection cnx;

    // Constructeur privé pour éviter la création d'instances supplémentaires
    private MyConnection() {
        try {
            // Connexion à la base de données
            cnx = DriverManager.getConnection(URL, LOGIN, PWD);
            System.out.println("Connection established!");
        } catch (SQLException e) {
            // Gérer l'exception proprement
            System.err.println("Error connection: " + e.getMessage());
        }
    }

    // Méthode pour obtenir l'instance unique
    public static MyConnection getInstance() {
        if (instance == null) {
            instance = new MyConnection();
        }
        return instance;
    }

    // Méthode pour obtenir la connexion
    public Connection getCnx() {
        try {
            if (cnx == null || cnx.isClosed()) {
                cnx = DriverManager.getConnection(URL, LOGIN, PWD);

            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ouverture de la connexion : " + e.getMessage());
        }
        return cnx;
    }

    // Méthode pour fermer la connexion proprement, à appeler explicitement
    public void closeConnection() {
        if (cnx != null) {
            try {
                cnx.close();
                System.out.println("Connection closed.");
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
}

