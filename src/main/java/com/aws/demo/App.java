package com.aws.demo;

import com.aws.demo.model.EmailAndMetadata;
import software.amazon.awssdk.services.ses.SesAsyncClient;
import java.util.Collections;
import java.util.UUID;


public class App 
{
    private static final String EMAIL_ADDRESS_TO = "";
    private static final String EMAIL_ADDRESS_FROM = "";

    public static void main( String[] args ) throws InterruptedException {
        SESEmailSender emailSender = new SESEmailSender(SesAsyncClient.builder().build(), EMAIL_ADDRESS_FROM);
        for (int i=0; i<100; i++) {
            EmailAndMetadata emailAndMetadata = new EmailAndMetadata("Email Content", Collections.singleton(EMAIL_ADDRESS_TO), "ID", "subj");
            String requestId = UUID.randomUUID().toString();
            emailSender.sendEmail(requestId, emailAndMetadata);
        }
        for(int y = 0; y < 120; y++){
            System.out.println("Seconds in sleep: " + y);
            Thread.sleep(1000);
        }
    }
}
