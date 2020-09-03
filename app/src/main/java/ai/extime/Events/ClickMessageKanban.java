package ai.extime.Events;

import ai.extime.Models.EmailMessage;

public class ClickMessageKanban {

    private EmailMessage emailMessage;

    public ClickMessageKanban(EmailMessage emailMessage) {
        this.emailMessage = emailMessage;
    }

    public EmailMessage getEmailMessage() {
        return emailMessage;
    }
}
