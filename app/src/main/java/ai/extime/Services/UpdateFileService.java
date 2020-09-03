package ai.extime.Services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;
import android.support.annotation.Nullable;

import com.snatik.storage.Storage;

import java.io.File;
import java.util.List;

import ai.extime.Activity.MainActivity;
import ai.extime.ParserToJson;

public class UpdateFileService extends IntentService {

    private Storage storage;
    private String path;

    public UpdateFileService() {
        super("UpdateFileService");
    }

    @Override
    public void onCreate() {
        storage = new Storage(getApplicationContext());
        path = Environment.getExternalStorageDirectory().getAbsolutePath();
        super.onCreate();
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        ParserToJson parserToJson = new ParserToJson();
        System.out.println("Get Json Start");
        String json = parserToJson.getJson();
        System.out.println("Get Json End");

        List<File> listBack = storage.getFiles(path + "/Extime/ExtimeContacts/");
        if(listBack != null){
            for(int i = 0;i<listBack.size();i++){
                if((listBack.get(i).getName().contains("backup_") && !listBack.get(i).getName().equalsIgnoreCase("backup_"+ MainActivity.versionDebug)) || listBack.get(i).getName().equalsIgnoreCase("backup")){
                    storage.copy(path + "/Extime/ExtimeContacts/" + listBack.get(i).getName(), path + "/Extime/Old backup/" + listBack.get(i).getName());
                    storage.rename(path + "/Extime/ExtimeContacts/" + listBack.get(i).getName(), path + "/Extime/ExtimeContacts/backup_"+MainActivity.versionDebug);
                }
            }
        }

        if(json != null) {

            if(listBack != null){
                for(int i = 0;i<listBack.size();i++){
                    if((listBack.get(i).getName().contains("backup_") && listBack.get(i).getName().equalsIgnoreCase("backup_"+ MainActivity.versionDebug))){
                        storage.copy(path + "/Extime/ExtimeContacts/" + listBack.get(i).getName(), path + "/Extime/Old backup/" + listBack.get(i).getName());
                    }
                }
            }

            storage.deleteFile(path + "/Extime/ExtimeContacts/backup_" + MainActivity.versionDebug);
            storage.createFile(path + "/Extime/ExtimeContacts/backup_" + MainActivity.versionDebug, json);
        }
        //Runtime.getRuntime().gc();
    }
}
