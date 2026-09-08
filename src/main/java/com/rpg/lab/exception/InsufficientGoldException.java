package com.rpg.lab.exception;

public class InsufficientGoldException extends RuntimeException {
    public InsufficientGoldException(String message) {
        super(message);
    }
}
