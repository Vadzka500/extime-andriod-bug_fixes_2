package ai.extime.Interfaces;

import android.support.v7.widget.Toolbar;
import android.view.Menu;

import java.util.ArrayList;

import ai.extime.Adapters.ContactAdapter;
import ai.extime.Adapters.HashTagsAdapter;
import ai.extime.Models.Contact;


public interface Postman {

    ContactAdapter getAdapter();

    Toolbar getToolbar();

    ArrayList<Contact> getListOfContacts();

    ArrayList<Contact> getListSavedContacts();

    void updateListOfContacts();

    void updateSavedList();

    HashTagsAdapter getHashTagsAdapter();

    Menu getMenu();

    ArrayList<Contact> getListOfContactsMain();

    void setListOfContactsMain(ArrayList<Contact> list);

}
