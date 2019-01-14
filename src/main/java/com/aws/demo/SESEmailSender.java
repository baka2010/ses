package com.aws.demo;


import com.aws.demo.exception.EmailException;
import com.aws.demo.model.EmailAndMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.ses.SesAsyncClient;
import software.amazon.awssdk.services.ses.model.RawMessage;
import software.amazon.awssdk.services.ses.model.SendRawEmailRequest;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;

public class SESEmailSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(SESEmailSender.class);
    private final SesAsyncClient amazonSESClient;

    private static final String TYPE_TEXT_HTML = "text/html; charset=UTF-8";
    private static final String MIME_MULTIPART_SUBTYPE = "mixed";
    private static final String CONTENT_TRANSFER_ENC_NAME = "Content-Transfer-Encoding";
    private static final String CONTENT_TRANSFER_ENC_VALUE = "base64";

    private  String emailSender ;

    public SESEmailSender(SesAsyncClient amazonSESClient, String emailSender) {
        this.emailSender = emailSender;
        this.amazonSESClient = amazonSESClient;
    }

    public CompletableFuture<Void> sendEmail(String emailId, EmailAndMetadata emailAndMetadata) {
        SdkBytes sdkBytes = SdkBytes.fromByteBuffer(ByteBuffer.wrap(buildHTMLRawBytes(emailAndMetadata)));
        RawMessage rawMessage = RawMessage.builder().data(sdkBytes).build();
        SendRawEmailRequest sendRawEmailRequest = SendRawEmailRequest.builder().rawMessage(rawMessage).build();
        LOGGER.info("start sending email for {} and email {}", emailAndMetadata.getId(), emailAndMetadata.getEmailAddressesAsString());
        return amazonSESClient.sendRawEmail(sendRawEmailRequest).thenAccept(
                response -> LOGGER.info("Email with id {} was sent to {}, response id {} .", emailId, emailAndMetadata.getEmailAddressesAsString(), response.messageId())
        ).exceptionally(e -> {
            LOGGER.error("Error on sending email", e);
            throw new EmailException(emailId, e);
        });
    }

    private byte[] buildHTMLRawBytes(EmailAndMetadata emailAndMetadata) {
        Session session = null;
        MimeMessage message = new MimeMessage(session);
        try {
            message.setSubject(emailAndMetadata.getSubject());
            message.setFrom(new InternetAddress(emailSender));

            message.setRecipients(javax.mail.Message.RecipientType.TO, InternetAddress.parse(emailAndMetadata.getEmailAddressesAsString()));

            MimeMultipart msgBody = new MimeMultipart(MIME_MULTIPART_SUBTYPE);

            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(emailAndMetadata.getEmailContent(), TYPE_TEXT_HTML);
            htmlPart.setHeader(CONTENT_TRANSFER_ENC_NAME, CONTENT_TRANSFER_ENC_VALUE);

            msgBody.addBodyPart(htmlPart);
            message.setContent(msgBody);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            message.writeTo(outputStream);
            return outputStream.toByteArray();
        } catch (MessagingException | IOException e) {
            throw new EmailException(emailAndMetadata.getId(), e);
        }
    }
}
