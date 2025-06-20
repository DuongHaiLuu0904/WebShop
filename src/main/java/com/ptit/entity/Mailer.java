package com.ptit.entity;

import lombok.Getter;

@Getter
public class Mailer {
    private String from;
    private String to;
    private String[] cc;
    private String[] bcc;
    private String subject;
    private String body;
    private String[] attachments;

    public Mailer(String to, String subject, String body) {
        this.from = "photit@stu.ptit.edu.vn";
        this.to = to;
        this.subject = subject;
        this.body = body;
    }

    // Explicit getter methods (in case Lombok doesn't work properly)
    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String[] getCc() {
        return cc;
    }

    public String[] getBcc() {
        return bcc;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public String[] getAttachments() {
        return attachments;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setCc(String[] cc) {
        this.cc = cc;
    }

    public void setBcc(String[] bcc) {
        this.bcc = bcc;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setAttachments(String[] attachments) {
        this.attachments = attachments;
    }
}
