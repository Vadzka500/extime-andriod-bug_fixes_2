package ai.extime.Events;

import java.util.ArrayList;

public class UpdateFileSync {

    private String type;
    private ArrayList<String> list;
    private ArrayList<Boolean> listType;

    public UpdateFileSync(String type, ArrayList<String>list, ArrayList<Boolean>listType){
        this.list = list;
        this.type = type;
        this.listType = listType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<String> getList() {
        return list;
    }

    public void setList(ArrayList<String> list) {
        this.list = list;
    }


    public ArrayList<Boolean> getListType() {
        return listType;
    }

    public void setListType(ArrayList<Boolean> listType) {
        this.listType = listType;
    }
}
