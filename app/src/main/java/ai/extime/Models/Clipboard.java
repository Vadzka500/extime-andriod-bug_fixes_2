package ai.extime.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import ai.extime.Enums.ClipboardEnum;

public class Clipboard implements Serializable {

    @SerializedName("valueCopy")
    private String valueCopy;
    @SerializedName("time")
    private String time;
    @SerializedName("imageTypeClipboard")
    private String imageTypeClipboard;
    @SerializedName("imageSearchCircle")
    private String imageSearchCircle;
    @SerializedName("type")
    private ClipboardEnum type;

    @SerializedName("label")
    private String label;

    @SerializedName("listClipboards")
    private ArrayList<Clipboard> listClipboards = null;
    @SerializedName("nameFromSocial")
    private String nameFromSocial = null;

    @SerializedName("listContactsSearch")
    private ArrayList<ContactDataClipboard> listContactsSearch = null;

    public ClipboardEnum getType() {
        return type;
    }

    public ArrayList<ContactDataClipboard> getListContactsSearch() {
        if(listContactsSearch == null)
            listContactsSearch = new ArrayList<>();

        return listContactsSearch;
    }

    public void setListContactsSearch(ArrayList<Contact> listContactsSearch) {
        if(listContactsSearch != null){
            this.listContactsSearch = new ArrayList<>();
            for(Contact contact : listContactsSearch){
                boolean com = true;
                if(contact.listOfContacts != null && !contact.listOfContacts.isEmpty()) com = false;
                this.listContactsSearch.add(new ContactDataClipboard(contact.getId(), contact.getName(), contact.getPhotoURL(), contact.color, com));
            }
        }else this.listContactsSearch = null;
    }

    public void addContactToListSearch(Contact contact){
        if(this.listContactsSearch == null)
            this.listContactsSearch = new ArrayList<>();

        boolean com = true;
        if(contact.listOfContacts != null && !contact.listOfContacts.isEmpty()) com = false;

        this.listContactsSearch.add(new ContactDataClipboard(contact.getId(), contact.getName(), contact.getPhotoURL(), contact.color, com));
    }

    public void addContactToListSearch(ContactDataClipboard contact){
        if(this.listContactsSearch == null)
            this.listContactsSearch = new ArrayList<>();

        this.listContactsSearch.add(contact);
    }

    public void setFirstClipsToSearch(Contact contact){
        this.listContactsSearch = new ArrayList<>();
        boolean com = true;
        if(contact.listOfContacts != null && !contact.listOfContacts.isEmpty()) com = false;

        this.listContactsSearch.add(new ContactDataClipboard(contact.getId(), contact.getName(), contact.getPhotoURL(), contact.color, com));
    }

    public void setFirstClipsToSearch(ContactDataClipboard contact){
        this.listContactsSearch = new ArrayList<>();

        this.listContactsSearch.add(contact);
    }

    public void removeConatctFromListSearchBuId(long id){
        if(this.listContactsSearch == null) return;

        for(ContactDataClipboard contact1 : listContactsSearch){
            if(contact1.getId() == id)
                listContactsSearch.remove(contact1);
        }
        //======    return?
    }

    public void setType(ClipboardEnum type) {
        this.type = type;
    }

    public String getValueCopy() {
        return valueCopy;
    }

    public void setValueCopy(String valueCopy) {
        this.valueCopy = valueCopy;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getImageTypeClipboard() {
        return imageTypeClipboard;
    }

    public void setImageTypeClipboard(String imageTypeClipboard) {
        this.imageTypeClipboard = imageTypeClipboard;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    /* public Uri getImageSearchCircle() {
        return imageSearchCircle;
    }

    public void setImageSearchCircle(Uri imageSearchCircle) {
        this.imageSearchCircle = imageSearchCircle;
    }*/

    public Clipboard(String imageTypeClipboard, String valueCopy, String imageSearchCircle, String time, ClipboardEnum type) {
        this.imageTypeClipboard = imageTypeClipboard;
        this.valueCopy = valueCopy;
        this.imageSearchCircle = imageSearchCircle;
        this.time = time;
        this.type = type;

    }

    public Clipboard(String imageTypeClipboard, String valueCopy, String imageSearchCircle, String time, ClipboardEnum type, String name) {
        this.imageTypeClipboard = imageTypeClipboard;
        this.valueCopy = valueCopy;
        this.imageSearchCircle = imageSearchCircle;
        this.time = time;
        this.type = type;
        this.nameFromSocial = name;

    }

    public ArrayList<Clipboard> getListClipboards() {
        return listClipboards;
    }

    public void setListClipboards(ArrayList<Clipboard> listClipboards) {
        this.listClipboards = new ArrayList<>();
        this.listClipboards.addAll(listClipboards);
    }

    public void addClipboardToList(Clipboard clipboard){
        if(this.listClipboards == null)
            this.listClipboards = new ArrayList<>();

        this.listClipboards.add(clipboard);
    }

    public String getNameFromSocial() {
        return nameFromSocial;
    }

    public void setNameFromSocial(String nameFromSocial) {

        this.nameFromSocial = nameFromSocial;
    }



}
