package com.example.earthquake.exception;

public class EarthquakeException extends RuntimeException{
    public EarthquakeException(String message){
        super(message);
    }

    public EarthquakeException(String message, Throwable cause){
        super(message,cause);
    }
}
