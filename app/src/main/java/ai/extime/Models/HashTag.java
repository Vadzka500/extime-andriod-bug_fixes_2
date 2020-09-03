package ai.extime.Models;

import java.io.Serializable;
import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by patal on 05.10.2017.
 */

public class HashTag extends RealmObject implements Serializable{

    private String hashTagValue = "";

    private String typeHashtag = "";

    private Date date;

    public HashTag(){}

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public HashTag(String hashTagValue){
        this.hashTagValue = hashTagValue;
        this.date = new Date();
    }

    public String getHashTagValue() {
        return hashTagValue;
    }

    public void setHashTagValue(String hashTagValue) {
        this.hashTagValue = hashTagValue;
    }

    public String getTypeHashtag() {
        return typeHashtag;
    }

    public void setTypeHashtag(String typeHashtag) {
        this.typeHashtag = typeHashtag;
    }

    @Override
    public String toString(){
        return hashTagValue;
    }

    @Override
    public boolean equals(Object other){
        if ((other instanceof HashTag)){

            if(((HashTag) other).getHashTagValue().compareTo(this.getHashTagValue()) ==0 ) {
                return true;}
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + hashTagValue.hashCode();
        result = 31 * result + typeHashtag.hashCode();
        return result;
    }

}
