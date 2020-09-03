package ai.extime.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.extime.R;

import org.greenrobot.eventbus.EventBus;

import ai.extime.Events.SetVoicePush;
import ai.extime.Models.TestService;

public class VoiceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main2);


        EventBus.getDefault().post(new SetVoicePush());


        finish();
    }
}
