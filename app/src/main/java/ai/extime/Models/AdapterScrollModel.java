package ai.extime.Models;

import io.realm.Realm;

public class AdapterScrollModel {

    public int position;

    public Realm realm;

    public AdapterScrollModel(int position){
        this.position = position;
    }

    public void setRealm(Realm realm) {
        this.realm = realm;
    }
}
