package ai.extime.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import ai.extime.Services.FabNotificationService;

/**
 * Created by patal on 22.09.2017.
 */

public class ReceiverBootCompleted extends BroadcastReceiver {

    public ReceiverBootCompleted() {}

    public void onReceive(Context context, Intent intent) {

        Intent intentServiceNotification = new Intent(context, FabNotificationService.class);

        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        //  context.startForegroundService(intentServiceNotification);
        //}else {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intentServiceNotification);
        }else{
            context.startService(intentServiceNotification);
        }
        //}

        //Toast toast = Toast.makeText(context, "RECEIVER", Toast.LENGTH_LONG);
        //toast.show();
        //Log.d("myapp", "Dsadsadas");
    }
}
