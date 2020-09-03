package ai.extime.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.greenrobot.eventbus.EventBus;

import ai.extime.Activity.MainActivity;
import ai.extime.Events.StopVoiceEvent;
import ai.extime.Services.FabNotificationService;

public class StopVoice extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        EventBus.getDefault().post(new StopVoiceEvent());

      /*  Intent openAppIntent = new Intent(context, MainActivity.class).putExtra("voice_command", FabNotificationService.textVoice);
        openAppIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(openAppIntent);*/


    }
}
