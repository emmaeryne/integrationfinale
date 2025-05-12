package edu.emmapi.entities;

public class SessionManager {

        private static SessionManager instance;
        private user users;

        private SessionManager() {
            // Constructeur privé pour empêcher l'instanciation directe
        }

        public static SessionManager getInstance() {
            if (instance == null) {
                instance = new SessionManager();
            }
            return instance;
        }

        public void setUser(user users) {
            this.users = users;
        }

        public user getUser() {
            return users;
        }

        public void clearSession() {
            users = null;
        }
    }

