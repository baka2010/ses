package com.aws.demo.exception;

import java.util.concurrent.CompletionException;

public class EmailException extends CompletionException {
    private final String emailId;

    public EmailException(String emailId, Exception e) {
        super(e);
        this.emailId = emailId;
    }

    public EmailException(String emailId, Throwable t) {
        super(t);
        this.emailId = emailId;
    }

    public EmailException(String message, String emailId) {
        super(message);
        this.emailId = emailId;
    }

    public String getEmailId() {
        return emailId;
    }
}
