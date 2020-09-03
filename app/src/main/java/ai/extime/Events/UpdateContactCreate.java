package ai.extime.Events;

import ai.extime.Models.Contact;

public class UpdateContactCreate {

    private Contact contact;

    public UpdateContactCreate(Contact contact){
        this.contact = contact;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }
}
