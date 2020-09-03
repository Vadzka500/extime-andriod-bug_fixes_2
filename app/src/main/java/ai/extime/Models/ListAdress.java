package ai.extime.Models;

import java.io.Serializable;

import io.realm.RealmObject;

/**
 * Created by Admin on 19.04.2018.
 */

public class ListAdress extends RealmObject implements Serializable {

    public String address;

    public ListAdress(){

    }

    public ListAdress(String address){
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
