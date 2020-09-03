package ai.extime.Utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;

/**
 * Created by andrew on 01.11.2017.
 */

public class Call2meApplication extends Application {

    private Activity mCurrentActivity = null;
    public Activity getCurrentActivity(){
        return mCurrentActivity;
    }
    public void setCurrentActivity(Activity mCurrentActivity){
        this.mCurrentActivity = mCurrentActivity;
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onCreate() {
        Call2MeBackUpHelper.requestBackup(this);
    }
}