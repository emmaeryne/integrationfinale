package edu.emmapi.entities;

import java.util.Objects;

public class profile {
    private int id;
    private int userId;
    private String firstName;
    private String lastName;
    private String dateOfBirth;
    private String profilePicture;
    private String bio;
    private String location;
    private String phoneNumber;
    private String website;
    private String socialMediaLink;

    public profile() {
    }

    public profile(int id, String firstName, int userId, String dateOfBirth, String lastName, String profilePicture, String bio, String location, String phoneNumber, String socialMediaLink, String website) {
        this.id = id;
        this.firstName = firstName;
        this.userId = userId;
        this.dateOfBirth = dateOfBirth;
        this.lastName = lastName;
        this.profilePicture = profilePicture;
        this.bio = bio;
        this.location = location;
        this.phoneNumber = phoneNumber;
        this.socialMediaLink = socialMediaLink;
        this.website = website;
    }

    public profile(int userId, String firstName, String lastName, String dateOfBirth, String profilePicture, String bio, String location, String phoneNumber, String website, String socialMediaLink) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.profilePicture = profilePicture;
        this.bio = bio;
        this.location = location;
        this.phoneNumber = phoneNumber;
        this.website = website;
        this.socialMediaLink = socialMediaLink;
    }

    public profile(String firstName, String lastName, String dateOfBirth, String profilePic, String bio, String location, String phone, String website, String socialMediaLinks) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.profilePicture = profilePic;
        this.bio = bio;
        this.location = location;
        this.phoneNumber = phone;
        this.website = website;
        this.socialMediaLink = socialMediaLinks;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSocialMediaLink() {
        return socialMediaLink;
    }

    public void setSocialMediaLink(String socialMediaLink) {
        this.socialMediaLink = socialMediaLink;
    }

    @Override
    public String toString() {
        return "profile{" +
                "id=" + id +
                ", userId=" + userId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", profilePicture='" + profilePicture + '\'' +
                ", bio='" + bio + '\'' +
                ", location='" + location + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", website='" + website + '\'' +
                ", socialMediaLink='" + socialMediaLink + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        profile profile = (profile) o;
        return id == profile.id && userId == profile.userId && Objects.equals(firstName, profile.firstName) && Objects.equals(lastName, profile.lastName) && Objects.equals(dateOfBirth, profile.dateOfBirth) && Objects.equals(profilePicture, profile.profilePicture) && Objects.equals(bio, profile.bio) && Objects.equals(location, profile.location) && Objects.equals(phoneNumber, profile.phoneNumber) && Objects.equals(website, profile.website) && Objects.equals(socialMediaLink, profile.socialMediaLink);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, firstName, lastName, dateOfBirth, profilePicture, bio, location, phoneNumber, website, socialMediaLink);
    }
}