package ai.extime.Events;

import java.util.ArrayList;

public class AddContactsToList {

    private ArrayList<Long> list;

    public AddContactsToList(ArrayList<Long> list){
        this.list = list;
    }

    public ArrayList<Long> getList() {
        return list;
    }

    public void setList(ArrayList<Long> list) {
        this.list = list;
    }
}
