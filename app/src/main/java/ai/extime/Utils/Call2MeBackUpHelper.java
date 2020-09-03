package ai.extime.Utils;

import android.app.backup.BackupAgentHelper;
import android.app.backup.BackupDataInput;
import android.app.backup.BackupManager;
import android.app.backup.SharedPreferencesBackupHelper;
import android.content.Context;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.IOException;

/**
 * Created by andrew on 01.11.2017.
 */

public class Call2MeBackUpHelper extends BackupAgentHelper {

    private final static String CONTACTS_PREFERENCES = "Call2MePref";

    @Override
    public void onCreate() {
//        SharedPreferencesBackupHelper helper = new SharedPreferencesBackupHelper(
//                this, CONTACTS_PREFERENCES);
//        addHelper(CONTACTS_PREFERENCES, helper);
    }

    public static void requestBackup(Context context) {
//        BackupManager backupManager = new BackupManager(context);
//        backupManager.dataChanged();
    }


}