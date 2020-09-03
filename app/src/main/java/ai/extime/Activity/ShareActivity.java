package ai.extime.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.extime.R;

import org.greenrobot.eventbus.EventBus;

import ai.extime.Models.TestService;

public class ShareActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main2);

        CharSequence text = getIntent()
                .getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT);

        if(text != null) {
            EventBus.getDefault().post(new TestService(text.toString(), false) );
        }
        {
            CharSequence sharedText = getIntent().getCharSequenceExtra(Intent.EXTRA_TEXT);
            if(sharedText != null){
                EventBus.getDefault().post(new TestService(sharedText.toString(), false));
            }
        }


        finish();
    }
}
