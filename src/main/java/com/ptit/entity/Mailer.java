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
}
