package com.election.backendjava.exception;

public class NoBannedUsersFoundException extends RuntimeException {
    public NoBannedUsersFoundException(String message) {
        super(message);
    }
}
