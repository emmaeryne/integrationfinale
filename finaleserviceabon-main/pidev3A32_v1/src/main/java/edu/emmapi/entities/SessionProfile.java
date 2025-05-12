package edu.emmapi.entities;

import com.google.api.services.gmail.model.Profile;

public class SessionProfile {
    private static SessionProfile instance;
    private profile profile;

    private SessionProfile() {
        // Constructeur privé pour empêcher l'instanciation directe
    }

    public static SessionProfile getInstance() {
        if (instance == null) {
            instance = new SessionProfile();
        }
        return instance;
    }

    public void setprofile(profile profiles) {
        this.profile=profiles;
    }

    public profile getProfile() {
        return profile;
    }

    public void clearSession() {
        profile = null;
    }
}
