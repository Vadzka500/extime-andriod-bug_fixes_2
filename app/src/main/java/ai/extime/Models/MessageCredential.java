package ai.extime.Models;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;

public class MessageCredential {

    private Message message;

    private GoogleAccountCredential accountCredential;

    private Gmail gmail;

    public MessageCredential(Message message, GoogleAccountCredential accountCredential, Gmail gmail) {
        this.message = message;
        this.accountCredential = accountCredential;
        this.gmail = gmail;
    }

    public Message getMessage() {
        return message;
    }

    public GoogleAccountCredential getAccountCredential() {
        return accountCredential;
    }

    public Gmail getGmail() {
        return gmail;
    }
}
