package com.example.demo.config;

public class InvalidPageRequestException extends RuntimeException{
    public InvalidPageRequestException(String message) {
        super(message);
    }
}
