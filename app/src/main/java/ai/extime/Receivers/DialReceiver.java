package ai.extime.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import org.greenrobot.eventbus.EventBus;

import java.nio.charset.Charset;

import ai.extime.Activity.MainActivity;
import ai.extime.Events.StartProcessDial;

public class DialReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Uri text = intent.getData();

        System.out.println("MAIN DATA = "+text);
        if(text != null)
            MainActivity.searchData = text.toString();

        System.out.println("DIAL = "+MainActivity.destroyApp);

        if(!MainActivity.destroyStop){
            EventBus.getDefault().post(new StartProcessDial());
        }else{
            Intent dialogIntent = new Intent(context, MainActivity.class);
            dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            dialogIntent.setData(text);
            context.startActivity(dialogIntent);
        }
    }
}
