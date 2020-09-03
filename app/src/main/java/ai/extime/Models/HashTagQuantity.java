package ai.extime.Models;

/**
 * Created by patal on 05.10.2017.
 */

public class HashTagQuantity {

    private int quantity;

    private HashTag hashTag;

    public HashTag getHashTag() {
        return hashTag;
    }

    public void setHashTag(HashTag hashTag) {
        this.hashTag = hashTag;
    }

    public int getQuantity() {

        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public HashTagQuantity(HashTag hashTag,Integer quantity){
        this.quantity = quantity;
        this.hashTag = hashTag;
    }

}
