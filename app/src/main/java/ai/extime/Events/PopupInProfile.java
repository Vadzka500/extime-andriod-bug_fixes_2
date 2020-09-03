package ai.extime.Events;

import ai.extime.Models.Contact;

public class PopupInProfile {

    private long contact;

    public PopupInProfile(long contact){
        this.contact = contact;
    }

    public long getContact() {
        return contact;
    }

    public void setContact(long contact) {
        this.contact = contact;
    }
}
