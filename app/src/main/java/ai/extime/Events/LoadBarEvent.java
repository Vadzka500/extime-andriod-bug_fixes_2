package ai.extime.Events;

import android.app.Activity;

/**
 * Created by andrew on 28.10.2017.
 */

public class LoadBarEvent {

    private Activity activity;
    public LoadBarEvent(Activity activity) {
        this.activity = activity;
    }

    public Activity getActivity(){
        return activity;
    }

}
