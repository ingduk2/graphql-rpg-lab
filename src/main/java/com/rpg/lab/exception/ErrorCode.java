package com.rpg.lab.exception;

import java.util.Map;

public enum ErrorCode {
    NOT_FOUND,
    BAD_REQUEST,
    INTERNAL_ERROR,
    PREREQUISITE_NOT_COMPLETED;

    public Map<String, Object> toExtensions() {
        return Map.of("errorCode", this.name());
    }
}
