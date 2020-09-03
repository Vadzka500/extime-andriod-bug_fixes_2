package ai.extime.Events;

import java.util.ArrayList;

import ai.extime.Models.Contact;

/**
 * Created by andrew on 28.10.2017.
 */

public class ContactLoadEvent {

    private ArrayList<Contact> contactsList;

    public ArrayList<Contact> getContactsList() {
        return contactsList;
    }

    public ContactLoadEvent(ArrayList<Contact> contactsList) {
        this.contactsList = contactsList;
    }

}
