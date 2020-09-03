package ai.extime.Events;

import ai.extime.Models.Contact;

public class UpdateMessengersPreview {

    Contact contact;

    public UpdateMessengersPreview(Contact contact) {
        this.contact = contact;
    }

    public Contact getContact() {
        return contact;
    }
}
