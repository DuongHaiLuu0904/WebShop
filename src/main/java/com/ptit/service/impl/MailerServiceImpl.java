package com.ptit.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.ptit.entity.Mailer;
import com.ptit.service.MailerService;

import java.io.File;
import java.io.UnsupportedEncodingException;

@Service
public class MailerServiceImpl implements MailerService {

    @Autowired
    private JavaMailSender sender;

    @Override
    public void send(Mailer mail) throws MessagingException {
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
        helper.setFrom(mail.getFrom());
        helper.setTo(mail.getTo());
        helper.setSubject(mail.getSubject());
        helper.setText(mail.getBody(), true);
        helper.setReplyTo(mail.getFrom());
        String[] cc = mail.getCc();
        if (cc != null && cc.length > 0) {
            helper.setCc(cc);
        }
        String[] bcc = mail.getBcc();
        if (bcc != null && bcc.length > 0) {
            helper.setBcc(bcc);
        }
        String[] attachments = mail.getAttachments();
        if (attachments != null && attachments.length > 0) {
            for (String path : attachments) {
                File file = new File(path);
                helper.addAttachment(file.getName(), file);
            }
        }
        sender.send(message);
    }

    @Override
    public void send(String to, String subject, String body) throws MessagingException {
        this.send(new Mailer(to, subject, body));
    }

    @Override
    public void sendEmail(String email, String link) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
        helper.setFrom("dluu57328@gmail.com", "Julie Store");
        helper.setTo(email);
        String button = "background-color:#783ecf;color:#fff;padding:12px 10px;text-decoration:none;border-radius:3px";
        String subject = "Request to reset your password";
        String content = "" + "<div style='font-family:Roboto,sans-serif;width:50%;margin:0 auto;text-align:center'>"
                + "<div style='font-size:3em;padding:0.5em 1em'><b>Password reset</b></div>"
                + "<div style='background-color:#f0f8ff;font-size:16px;padding:1em 3em'>"
                + "<p><b style='font-size:18px;'>Someone requested that the password be reset for the following account.</b></p>"
                + "<p>To reset your password, visit the following address:</p>" + "<p style='margin-top:2em'><a href=\""
                + link + "\" style='" + button + "'>Reset your password</a></p>"
                + "<p style='margin-bottom:2em'>Your mail: <a href=\"" + "mailto:" + email
                + "\" style='color:#b745dd;text-decoration:none'>" + email + "</a></p>"
                + "<p>If this was a mistake, just ignore this email and nothing will happen.</p>" + "</div>"
                + "<div style='font-size:14px;padding:2em'>Copyright © 2025 <b>Julie</b>. All Rights Reserved.</div>"
                + "</div>";
        helper.setSubject(subject);
        helper.setText(content, true);
        sender.send(message);
    }

}
