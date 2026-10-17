package com.rpg.lab.exception;

public class HpAlreadyFullException extends RuntimeException {
    public HpAlreadyFullException(String message) {
        super(message);
    }
}
