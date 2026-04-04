package com.election.backendjava.dto.email;

import java.util.List;

public class EmailRequest {
    private EmailSender from;
    private List<EmailRecipient> to;
    private String subject;
    private String html;

    public EmailRequest(EmailSender from, List<EmailRecipient> to, String subject, String html) {
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.html = html;
    }

    public EmailSender getFrom() { return from; }
    public void setFrom(EmailSender from) { this.from = from; }
    public List<EmailRecipient> getTo() { return to; }
    public void setTo(List<EmailRecipient> to) { this.to = to; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getHtml() { return html; }
    public void setHtml(String html) { this.html = html; }
}