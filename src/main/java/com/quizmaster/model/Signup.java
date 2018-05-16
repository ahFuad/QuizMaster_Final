package com.quizmaster.model;

public class Signup {
    private String userId;
    private String userEmail;
    private String userPassword;
    private String userPasswordRepeat;

    public Signup() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserPasswordRepeat() {
        return userPasswordRepeat;
    }

    public void setUserPasswordRepeat(String userPasswordRepeat) {
        this.userPasswordRepeat = userPasswordRepeat;
    }
}
