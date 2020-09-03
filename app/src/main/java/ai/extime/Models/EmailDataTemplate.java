package ai.extime.Models;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import java.util.ArrayList;

public class EmailDataTemplate {

    private ArrayList<String> sendTo;

    private ArrayList<String> sendToName;

    private String recipient;

    private EmailMessage message;

    private ArrayList <Boolean> send;

    public EmailDataTemplate(ArrayList<String> sendTo, ArrayList<String> sendToName, String recipient, EmailMessage message) {
        this.sendTo = new ArrayList<>();
        this.sendToName = new ArrayList<>();
        this.send = new ArrayList<>();
        this.sendTo.addAll(sendTo);
        this.sendToName.addAll(sendToName);
        this.recipient = recipient;
        this.message = message;
        for(int i = 0; i < sendTo.size();i++)
            this.send.add(true);
    }

    public ArrayList<Boolean> getSend() {
        return send;
    }

    public EmailMessage getAccountCredential() {
        return message;
    }

    public void setAccountCredential(EmailMessage message) {
        this.message = message;
    }

    public ArrayList<String> getSendTo() {
        return sendTo;
    }

    /*public void setSendTo(String sendTo) {
        this.sendTo = sendTo;
    }*/

    public ArrayList<String> getSendToName() {
        return sendToName;
    }

    /*public void setSendToName(String sendToName) {
        this.sendToName = sendToName;
    }*/

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }
}
