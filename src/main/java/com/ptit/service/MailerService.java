package com.ptit.service;

import jakarta.mail.MessagingException;

import java.io.UnsupportedEncodingException;

import com.ptit.entity.Mailer;

public interface MailerService {

    void send(Mailer mail) throws MessagingException;

    void send(String to, String subject, String body) throws MessagingException;

    void sendEmail(String recipientEmail, String link) throws MessagingException, UnsupportedEncodingException;

}
