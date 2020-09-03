package ai.extime.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ContactDataClipboard implements Serializable {

    @SerializedName("id")
    private long id;

    @SerializedName("name")
    private String name;

    @SerializedName("photoURL")
    private String photoURL;

    @SerializedName("color")
    public int color;

    @SerializedName("contact")
    public boolean contact;

    public ContactDataClipboard(long id, String name, String photoURL, int color, boolean contact) {
        this.id = id;
        this.name = name;
        this.photoURL = photoURL;
        this.color = color;
        this.contact = contact;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

}
