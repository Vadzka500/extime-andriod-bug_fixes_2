package ai.extime.Events;

public class AddHistoryEntry {

    private String text;

    public AddHistoryEntry(String text){
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
