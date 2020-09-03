package ai.extime.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.greenrobot.eventbus.EventBus;

import ai.extime.Events.VoiceAgainEvent;

public class VoiceAgain extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        EventBus.getDefault().post(new VoiceAgainEvent());
    }
}
