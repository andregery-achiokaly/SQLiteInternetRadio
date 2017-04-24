package com.example.alexander_topilskii.internetradio.models.database;


public class NoStationsException extends Throwable {
    String message;

    public NoStationsException(String message) {
        super(message);
        this.message = message;
    }
}
