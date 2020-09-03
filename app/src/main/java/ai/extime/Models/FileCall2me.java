package ai.extime.Models;

import java.util.Date;

/**
 * Created by teaarte on 25.09.2017.
 */

public class FileCall2me {
    private String type;
    private String path;
    private String name;
    String date;

    public FileCall2me(String type, String path, String name, String date){
        this.type = type;
        this.path = path;
        this.name = name;
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }
}
