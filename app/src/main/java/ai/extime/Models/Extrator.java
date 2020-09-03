package ai.extime.Models;

import ai.extime.Enums.ExtractEnums;

public class Extrator {

    private ExtractEnums type;

    private String text;

    private String allLink;

    private boolean check = true;

    public Extrator(ExtractEnums type,  String text, String allLink){
        this.type = type;
        this.text = text;
        this.allLink = allLink;
    }
    public ExtractEnums getType() {
        return type;
    }

    public void setType(ExtractEnums type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public String getAllLink() {
        return allLink;
    }

    public void setAllLink(String allLink) {
        this.allLink = allLink;
    }
}
