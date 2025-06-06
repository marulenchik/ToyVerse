package com.toyverse.toyverse_backend.exception;

public class NotPurchasedException extends RuntimeException {
    public NotPurchasedException(String message) {
        super(message);
    }
}
