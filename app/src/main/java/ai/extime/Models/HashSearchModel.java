package ai.extime.Models;

import java.io.Serializable;

public class HashSearchModel implements Serializable {

    private String hash;

    public HashSearchModel(String s){
        hash  = s;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}
