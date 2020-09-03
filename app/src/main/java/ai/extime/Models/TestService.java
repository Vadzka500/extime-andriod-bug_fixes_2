package ai.extime.Models;

public class TestService {

    private String text;
    public boolean open;

    public TestService(String text, boolean open){
        this.text = text;
        this.open = open;
    }

    public String getText(){
        return text;
    }

    public boolean isOpen() {
        return open;
    }
}
