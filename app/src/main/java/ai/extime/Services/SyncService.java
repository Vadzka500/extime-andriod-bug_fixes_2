package ai.extime.Services;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Intent;
import android.support.annotation.Nullable;

import ai.extime.SynkFromAddress;

public class SyncService extends IntentService {

    private ContentResolver contentResolver;

    public SyncService() {
        super("sync");
    }

    @Override
    public void onCreate() {
        this.contentResolver = getApplication().getContentResolver();
        super.onCreate();
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        SynkFromAddress synkFromAddress = new SynkFromAddress(contentResolver, getApplication());

        synkFromAddress.startSync();



        Intent responseIntent = new Intent();
        responseIntent.setAction("finishSync");
        responseIntent.addCategory(Intent.CATEGORY_DEFAULT);
        sendBroadcast(responseIntent);

        //Runtime.getRuntime().gc();
    }
}
