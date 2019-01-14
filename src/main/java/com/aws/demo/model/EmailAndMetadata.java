package com.aws.demo.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;

public class EmailAndMetadata implements Serializable {
    private final String emailContent;
    private final String subject;
    private final Set<String> emailAddresses;
    private final String id;

    public EmailAndMetadata(
            String emailContent, Set<String> emailAddresses, String id, String subject) {
        this.emailContent = emailContent;
        this.emailAddresses = emailAddresses;
        this.id = id;
        this.subject = subject;
    }

    public EmailAndMetadata(
            String emailContent, String emailAddress, String id, String subject) {
        this.emailContent = emailContent;
        this.emailAddresses = Collections.singleton(emailAddress);
        this.id = id;
        this.subject = subject;
    }

    public Set<String> getEmailAddresses() {
        return Collections.unmodifiableSet(emailAddresses);
    }

    public String getId() {
        return id;
    }

    public String getEmailContent() {
        return emailContent;
    }

    public String getSubject() {
        return subject;
    }

    public String getEmailAddressesAsString() {
        return String.join(",", getEmailAddresses());
    }

    @Override
    public String toString() {
        return "EmailAndMetadata{" +
                "emailContent='" + emailContent + '\'' +
                ", subject='" + subject + '\'' +
                ", emailAddresses=" + emailAddresses +
                ", id='" + id + '\'' +
                '}';
    }
}
