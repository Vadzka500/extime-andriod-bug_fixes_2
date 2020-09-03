package ai.extime.Models;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePartHeader;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import ai.extime.Enums.FileEnums;

public class EmailMessage implements Serializable, Parcelable {

    private String id;

    private String user;

    private String title;

    private String message;

    public ArrayList<FileEnums> listTypeOfFiles;

    public ArrayList<String> fileName;

    private String threadID;

    public Date date;

    public Account account;

    public transient Message f_Message;

    public String email_2;

    public ArrayList<Clipboard> listInfo;

    private ArrayList<MessageData> messageData;

    private ArrayList<String> listOfTypeMessage;

    private transient  GoogleAccountCredential credential;

    private boolean checkData = false;

    private List<MessagePartHeader> listHeaders;

    public String getThreadID() {
        return threadID;
    }

    public void setThreadID(String threadID) {
        this.threadID = threadID;
    }

    public List<MessagePartHeader> getListHeaders() {
        return listHeaders;
    }

    public void setListHeaders(List<MessagePartHeader> listHeaders) {
        this.listHeaders = listHeaders;
    }

    protected EmailMessage(Parcel in) {
        id = in.readString();
        user = in.readString();
        title = in.readString();
        message = in.readString();
        fileName = in.createStringArrayList();
        email_2 = in.readString();
        listOfTypeMessage = in.createStringArrayList();
        checkData = in.readByte() != 0;
    }

    public static final Creator<EmailMessage> CREATOR = new Creator<EmailMessage>() {
        @Override
        public EmailMessage createFromParcel(Parcel in) {
            return new EmailMessage(in);
        }

        @Override
        public EmailMessage[] newArray(int size) {
            return new EmailMessage[size];
        }
    };

    public boolean isCheckData() {
        return checkData;
    }

    public void setCheckData(boolean checkData) {
        this.checkData = checkData;
    }

    public EmailMessage(){

    }





    public GoogleAccountCredential getCredential() {
        return credential;
    }

    public void setCredential(GoogleAccountCredential credential) {
        this.credential = credential;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public ArrayList<String> getListOfTypeMessage() {
        return listOfTypeMessage;
    }

    public void setListOfTypeMessage(List<String> listOfTypeMessage) {
        this.listOfTypeMessage = new ArrayList<>(listOfTypeMessage);
    }

    public ArrayList<MessageData> getMessageData() {
        return messageData;
    }

    public void setMessageData(ArrayList<MessageData> messageData) {
        this.messageData = messageData;
    }

    public ArrayList<Clipboard> getListInfo() {
        return listInfo;
    }

    public ArrayList<String> getFileName() {
        return fileName;
    }

    public void setFileName(ArrayList<String> fileName) {
        this.fileName = fileName;
    }

    public void setListInfo(ArrayList<Clipboard> listInfo) {
        this.listInfo = listInfo;
    }

    public String getEmail_2() {
        return email_2;
    }

    public void setEmail_2(String email_2) {
        this.email_2 = email_2;
    }

    public Message getF_Message() {
        return f_Message;
    }

    public void setF_Message(Message f_Message) {
        this.f_Message = f_Message;
    }

    public EmailMessage(Account account, String title, String message, ArrayList<FileEnums> list, Date date){
        this.account = account;
        this.title = title;
        this.message = message;
        this.listTypeOfFiles = list;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<FileEnums> getListTypeOfFiles() {
        return listTypeOfFiles;
    }

    public void setListTypeOfFiles(ArrayList<FileEnums> listTypeOfFiles) {
        this.listTypeOfFiles = listTypeOfFiles;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(id);
        dest.writeString(user);
        dest.writeString(title);
        dest.writeString(message);
        dest.writeStringList(fileName);
        dest.writeString(email_2);
        dest.writeStringList(listOfTypeMessage);
        dest.writeByte((byte) (checkData ? 1 : 0));
    }


}
