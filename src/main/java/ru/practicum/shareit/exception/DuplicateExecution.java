package ru.practicum.shareit.exception;

public class DuplicateExecution extends RuntimeException {
    public DuplicateExecution(String message) {
        super(message);
    }
}