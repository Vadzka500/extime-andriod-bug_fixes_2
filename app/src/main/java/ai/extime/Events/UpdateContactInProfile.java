package ai.extime.Events;

import ai.extime.Models.Contact;

public class UpdateContactInProfile {

    private Contact contact;

    public UpdateContactInProfile(Contact contact){
        this.contact = contact;
    }

    public Contact getContact() {
        return contact;
    }
}
