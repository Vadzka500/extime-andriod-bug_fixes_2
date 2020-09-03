package ai.extime.Events;

import ai.extime.Models.Contact;

public class UpdateMessengersInCompany {

    Contact contact;

    public UpdateMessengersInCompany(Contact contact) {
        this.contact = contact;
    }

    public Contact getContact() {
        return contact;
    }
}
