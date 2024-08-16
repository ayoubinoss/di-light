package com.ayoubinoss.dilight.exceptions;

public class FileClassLoaderException extends RuntimeException {

    public FileClassLoaderException(String message) {
        super(message);
    }

    public FileClassLoaderException(String message, Throwable cause) {
        super(message, cause);
    }
}
