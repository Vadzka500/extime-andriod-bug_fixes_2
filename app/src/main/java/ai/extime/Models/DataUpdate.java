package ai.extime.Models;

import ai.extime.Enums.ClipboardEnum;

public class DataUpdate {

    private ClipboardEnum type;

    private String value;

    public DataUpdate(String value, ClipboardEnum type){
        this.value = value;
        this.type = type;
    }

    public ClipboardEnum getType() {
        return type;
    }

    public void setType(ClipboardEnum type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
