package com.demo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sendinblue.ApiClient;
import sendinblue.Configuration;
import sendinblue.auth.ApiKeyAuth;
import sibApi.TransactionalEmailsApi;
import sibModel.SendSmtpEmail;
import sibModel.SendSmtpEmailSender;
import sibModel.SendSmtpEmailTo;

import java.util.Collections;

@Service
@Slf4j
public class BreveEmailService {

    private final TransactionalEmailsApi transactionalEmailsApi;

    public BreveEmailService(@Value("${brevo.api-key}") String apiKeyValue) {
        ApiClient client = Configuration.getDefaultApiClient();

        ApiKeyAuth apiKey = (ApiKeyAuth) client.getAuthentication("api-key");
        apiKey.setApiKey(apiKeyValue);

        this.transactionalEmailsApi = new TransactionalEmailsApi(client);
    }

    public void sendEmail(String toEmail, String subject, String content) {
        try {
            SendSmtpEmailSender sender = new SendSmtpEmailSender();
            sender.setEmail("testing991162@gmail.com");
            sender.setName("Ajay Kumar");

            SendSmtpEmailTo to = new SendSmtpEmailTo();
            to.setEmail(toEmail);

            SendSmtpEmail email = new SendSmtpEmail();
            email.setSender(sender);
            email.setTo(Collections.singletonList(to));
            email.setSubject(subject);
            email.setTextContent(content);

            transactionalEmailsApi.sendTransacEmail(email);
            log.info("Email sent successfully to: {}", toEmail);

        } catch (Exception e) {
            log.error("Failed to send email", e);
        }
    }
}
