package ai.extime.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

import org.greenrobot.eventbus.EventBus;

import ai.extime.Activity.TransparentActivity;
import ai.extime.Events.CheckClipboard;
import ai.extime.Services.FabNotificationService;

public class PushClipboard extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        /*try {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
                    | PowerManager.ACQUIRE_CAUSES_WAKEUP
                    | PowerManager.ON_AFTER_RELEASE, "MyWakeLock2");
            wakeLock.acquire();

        } catch (Exception e) {
            e.printStackTrace();
        }*/

        //try {

      /*  System.out.println("BRODCAST");

            Intent dialogIntent = new Intent(context, TransparentActivity.class);
            dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);


            context.startActivity(dialogIntent);*/


            //Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(getPackageName());
            //startActivity(LaunchIntent);

      /*  } catch (Exception e) {
            e.printStackTrace();
        }*/

        //EventBus.getDefault().post(new CheckClipboard());

    }
}
