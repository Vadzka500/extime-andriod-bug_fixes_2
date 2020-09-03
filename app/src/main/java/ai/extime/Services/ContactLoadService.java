package ai.extime.Services;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import ai.extime.Activity.MainActivity;
import ai.extime.Events.ContactLoadEvent;
import ai.extime.Events.UpdateFile;
import ai.extime.Events.UpdateList;
import ai.extime.Models.Contact;
import ai.extime.Utils.Call2MeBackUpHelper;
import ai.extime.Utils.Call2meApplication;

/**
 * Created by andrew on 16.12.2017.
 */

public class ContactLoadService extends IntentService {

    ArrayList<Contact> listOfContacts;

    //   private SharedPreferences sharedPreferences;

    public ContactLoadService() {
        super("ContactLoadService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        if(!MainActivity.loadFromFile) {
            ContactsService contactsService = new ContactsService(getContentResolver());
            Activity activity = ((Call2meApplication) getApplicationContext()).getCurrentActivity();
            listOfContacts = contactsService.getContactsPrevWithTypes(true, activity);

            if (listOfContacts != null) {


                //  sharedPreferences.edit().putBoolean(FIRST_RUN, false).apply();
                MainActivity.firstRun = false;

                Call2MeBackUpHelper.requestBackup(activity);
                MainActivity.IS_CONTACT_LOAD = false;
                EventBus.getDefault().post(new ContactLoadEvent(listOfContacts));

                //EventBus.getDefault().post(new UpdateFile());

                stopSelf();
            }
        }else{
            Activity activity = ((Call2meApplication) getApplicationContext()).getCurrentActivity();
            ContactsService contactsService = new ContactsService(getContentResolver(), false, activity);
            contactsService.loadContactFromFile(true, activity);

            //EventBus.getDefault().post(new UpdateList());

            MainActivity.firstRun = false;
            Call2MeBackUpHelper.requestBackup(activity);
            MainActivity.IS_CONTACT_LOAD = false;

            EventBus.getDefault().post(new ContactLoadEvent(null));

            //EventBus.getDefault().post(new UpdateFile());

            stopSelf();
        }
    }
}
