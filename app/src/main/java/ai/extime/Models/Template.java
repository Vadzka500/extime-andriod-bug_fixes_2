package ai.extime.Models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class Template implements Serializable, Parcelable {

    private UUID id = UUID.randomUUID();

    private String title;

    private String subject;

    private boolean templateUser = false;

    private String templateText;

    private Date dateCreate;

    public String time;

    private EmailDataTemplate emailDataTemplate;

    private ArrayList<FilesTemplate> listOfFiles;

    public Template(String title, boolean templateUser, String templateText) {
        this.title = title;
        this.templateUser = templateUser;
        this.templateText = templateText;
    }



    public void addFile(FilesTemplate filesTemplate){
        if(listOfFiles == null)
            listOfFiles = new ArrayList<>();

        listOfFiles.add(filesTemplate);
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void addUriFile(Uri uri){
        if(listOfFiles == null)
            listOfFiles = new ArrayList<>();

        listOfFiles.add(new FilesTemplate(uri));
    }

    public ArrayList<FilesTemplate> getListOfFiles() {
        return listOfFiles;
    }

    public UUID getId() {
        return id;
    }

    public void setListOfFiles(ArrayList<FilesTemplate> listOfFiles) {
        this.listOfFiles = listOfFiles;
    }

    public EmailDataTemplate getEmailDataTemplate() {
        return emailDataTemplate;
    }

    public void setEmailDataTemplate(EmailDataTemplate emailDataTemplate) {
        this.emailDataTemplate = emailDataTemplate;
    }

    public Template(){

    }

    public Date getDateCreate() {
        return dateCreate;
    }

    public void setDateCreate(Date dateCreate) {
        this.dateCreate = dateCreate;
    }

    protected Template(Parcel in) {
        title = in.readString();
        subject = in.readString();
        templateUser = in.readByte() != 0;
        templateText = in.readString();


    }

    public static final Creator<Template> CREATOR = new Creator<Template>() {
        @Override
        public Template createFromParcel(Parcel in) {
            return new Template(in);
        }

        @Override
        public Template[] newArray(int size) {
            return new Template[size];
        }
    };

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isTemplateUser() {
        return templateUser;
    }

    public void setTemplateUser(boolean templateUser) {
        this.templateUser = templateUser;
    }

    public String getTemplateText() {
        return templateText;
    }

    public void setTemplateText(String templateText) {
        this.templateText = templateText;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(subject);
        dest.writeByte((byte) (templateUser ? 1 : 0));
        dest.writeString(templateText);
    }
}
