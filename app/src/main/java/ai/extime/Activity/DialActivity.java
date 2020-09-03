package ai.extime.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.extime.R;

import org.greenrobot.eventbus.EventBus;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import ai.extime.Events.StartProcessDial;
import ai.extime.Models.TestService;

public class DialActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);


        Uri text = getIntent().getData();


        if(text != null)
        MainActivity.searchData = text.toString();



        if(!MainActivity.destroyStop){
            EventBus.getDefault().post(new StartProcessDial());
        }else{
            Intent dialogIntent = new Intent(this, MainActivity.class);
            dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            dialogIntent.setData(text);
            startActivity(dialogIntent);
        }

        /*
        String searchData = null;

        if (text != null && text.toString().contains("tel:")) {


            if(text.toString().contains("%2B")){

                text = Uri.parse(text.toString().replace("%2B", ""));
            }
            EventBus.getDefault().post(new TestService(text.toString().substring(4), true));

            searchData = text.toString().substring(4);

        }else if(text != null && text.toString().contains("mailto:")){


            EventBus.getDefault().post(new TestService(text.toString().substring(7), true));

             searchData = text.toString().substring(7);
        }*/


      /*  Intent dialogIntent = new Intent(this, MainActivity.class);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        dialogIntent.setData(text);
        startActivity(dialogIntent);*/

        finish();

    }
}