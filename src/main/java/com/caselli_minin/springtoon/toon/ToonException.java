package com.caselli_minin.springtoon.toon;

public class ToonException extends Exception {

    public ToonException(String message) {
        super(message);
    }

    public ToonException(String message, Throwable cause) {
        super(message, cause);
    }
}