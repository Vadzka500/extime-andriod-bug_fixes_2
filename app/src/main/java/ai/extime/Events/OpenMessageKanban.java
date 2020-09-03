package ai.extime.Events;

import ai.extime.Models.EmailMessage;

public class OpenMessageKanban {

    private EmailMessage emailMessage;

    public OpenMessageKanban(EmailMessage emailMessage) {
        this.emailMessage = emailMessage;
    }

    public EmailMessage getEmailMessage() {
        return emailMessage;
    }

}
