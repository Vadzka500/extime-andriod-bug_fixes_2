package ai.extime.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.greenrobot.eventbus.EventBus;

import ai.extime.Events.NewListenEvent;

public class NewListen extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        EventBus.getDefault().post(new NewListenEvent());
        System.out.println("GE BROAD");
    }
}
