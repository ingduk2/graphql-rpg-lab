package com.rpg.lab.exception;

public class ItemAlreadyOwnedException extends RuntimeException {
    public ItemAlreadyOwnedException(String message) {
        super(message);
    }
}
