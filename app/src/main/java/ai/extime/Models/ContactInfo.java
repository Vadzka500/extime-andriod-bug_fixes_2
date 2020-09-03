package ai.extime.Models;

import java.io.Serializable;
import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ContactInfo extends RealmObject implements Serializable {

    @PrimaryKey
    public String id;

    public String type;
    public String value;
    private String carrier;
    private String region;
    private String newValue = null;
    public String typeData;
    public boolean isEmail;
    public boolean isPhone;
    public boolean isGeo;
    public boolean isNote;
    public boolean isConfirmed;

    public String titleValue;

    public boolean isPrimary;

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public String getNewValue() {
        return newValue;
    }




    public ContactInfo(){

    }

    public void generateNewId(){
        this.id = UUID.randomUUID().toString();
    }

    public ContactInfo(String type, String value, boolean isEmail, boolean isPhone, boolean isGeo, boolean isNote, boolean isConfirmed) {
        this.type = type;
        this.value = value;
        this.isEmail = isEmail;
        this.isPhone = isPhone;
        this.isGeo = isGeo;
        this.isNote = isNote;
        this.isConfirmed = isConfirmed;

        this.id = UUID.randomUUID().toString();
    }

    public ContactInfo(ContactInfo contactInfo) {
        this.type = contactInfo.type;
        this.value = contactInfo.value;
        this.isEmail = contactInfo.isEmail;
        this.isPhone = contactInfo.isPhone;
        this.isGeo = contactInfo.isGeo;
        this.isNote = contactInfo.isNote;
        this.isConfirmed = contactInfo.isConfirmed;
        this.id = contactInfo.id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ContactInfo that = (ContactInfo) o;

        if (isEmail != that.isEmail) return false;
        if (isPhone != that.isPhone) return false;
        if (isGeo != that.isGeo) return false;
        if (isNote != that.isNote) return false;
        if (isConfirmed != that.isConfirmed) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        return value != null ? value.equals(that.value) : that.value == null;
    }

    @Override
    public String toString(){
        return value;
    }

    @Override
    public int hashCode() {
        int result = (type != null ? type.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (isEmail ? 1 : 0);
        result = 31 * result + (isPhone ? 1 : 0);
        result = 31 * result + (isGeo ? 1 : 0);
        result = 31 * result + (isNote ? 1 : 0);
        result = 31 * result + (isConfirmed ? 1 : 0);
        return result;
    }
}
