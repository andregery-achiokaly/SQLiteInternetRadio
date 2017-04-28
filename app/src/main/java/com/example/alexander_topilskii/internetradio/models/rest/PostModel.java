package com.example.alexander_topilskii.internetradio.models.rest;


public class PostModel {
    public static final String SUCCESS_RESPONSE = "true";
    public static final String ERROR_RESPONSE = "Login error";
    public static final String WELCOME_RESPONSE = "Welcome!";

    private String success;
    private String message;
    private String token;

    public PostModel(String success, String message, String token) {
        this.success = success;
        this.message = message;
        this.token = token;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
