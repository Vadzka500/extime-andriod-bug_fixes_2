package ai.extime.Events;

import ai.extime.Models.EmailMessage;

public class EventSetStarMessage {

    private boolean star;

    private EmailMessage emailMessage;

    public EventSetStarMessage(boolean star, EmailMessage emailMessage) {
        this.star = star;
        this.emailMessage = emailMessage;
    }

    public boolean isStar() {
        return star;
    }

    public EmailMessage getEmailMessage() {
        return emailMessage;
    }
}
