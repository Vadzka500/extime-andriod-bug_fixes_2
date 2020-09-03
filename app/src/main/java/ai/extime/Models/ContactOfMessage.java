package ai.extime.Models;

import java.util.ArrayList;

public class ContactOfMessage {

    private String name;

    private String email;

    private boolean google;

    private Contact searchContact;

    private String type;

    private boolean sendCheck;

    private ArrayList<ContactOfMessage> listOfAccount;

    private boolean checkSearch = false;

    public boolean isCheckSearch() {
        return checkSearch;
    }

    public void setCheckSearch(boolean checkSearch) {
        this.checkSearch = checkSearch;
    }

    public boolean isSendCheck() {
        return sendCheck;
    }

    public void setSendCheck(boolean sendCheck) {
        this.sendCheck = sendCheck;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void addAcc(ContactOfMessage contactOfMessage){
        if(listOfAccount == null)
            listOfAccount = new ArrayList<>();

        listOfAccount.add(contactOfMessage);
    }

    public ArrayList<ContactOfMessage> getListOfAccount() {
        return listOfAccount;
    }

    public void setListOfAccount(ArrayList<ContactOfMessage> listOfAccount) {
        this.listOfAccount = listOfAccount;
    }

    public ContactOfMessage(){

    }

    public void setSender(){
        this.type = "Sender";
    }

    public void setRecipient(){
        this.type = "Recipient";
    }

    public String getType() {
        return type;
    }

    public Contact getSearchContact() {
        return searchContact;
    }

    public void setSearchContact(Contact searchContact) {
        this.searchContact = searchContact;
    }

    public ContactOfMessage(String name, String email, boolean google) {
        this.name = name;
        this.email = email;
        this.google = google;
    }

    public boolean isGoogle() {
        return google;
    }

    public void setGoogle(boolean google) {
        this.google = google;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
