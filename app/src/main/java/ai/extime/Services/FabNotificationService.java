package ai.extime.Services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.Voice;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.snatik.storage.Storage;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import ai.extime.Activity.ShareActivity;
import ai.extime.Activity.TransparentActivity;
import ai.extime.Activity.MainActivity;

import ai.extime.Activity.VoiceActivity;
import ai.extime.Events.CheckClipboard;
import ai.extime.Events.CopyValueEvent;
import ai.extime.Events.NewListenEvent;
import ai.extime.Events.OpenClipboard;
import ai.extime.Events.SetDefaultPush;
import ai.extime.Events.SetVoicePush;
import ai.extime.Events.StopVoiceEvent;
import ai.extime.Events.VoiceAgainEvent;
import ai.extime.Models.Clipboard;
import ai.extime.Models.Contact;
import ai.extime.Models.TestService;
import ai.extime.Receivers.NewListen;
import ai.extime.Receivers.OpenVoice;
import ai.extime.Receivers.PushClipboard;
import ai.extime.Receivers.StopVoice;
import ai.extime.Receivers.VoiceAgain;
import ai.extime.Utils.ClipboardType;
import de.hdodenhof.circleimageview.CircleImageView;

import com.extime.R;

/**
 * Created by patal on 13.09.2017.
 */

public class FabNotificationService extends Service {

    private final String ACTION_OPEN_WIDGET = "open_widget";
    RemoteViews contentView;
    PendingIntent openAppPI;
    PendingIntent openAppClip;
    PendingIntent openWidgetPI;
    ClipboardManager clipboardManager;
    ClipboardManager.OnPrimaryClipChangedListener clipChangedListener;
    Notification notification;
    public static ArrayList<Clipboard> clipboard;
    public static Clipboard newClipboard = null;
    SharedPreferences mSettings;

    int LAYOUT_FLAG;

    private static final int LayoutParamFlags = WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
            | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
            | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
            | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;

    private LayoutInflater inflater;
    private Display mDisplay;
    private View layoutView;
    private WindowManager windowManager;
    private WindowManager.LayoutParams params;

    private final static String CONTACTS_PREFERENCES = "Call2MePref";
    private final static String CLIPBOARD = "clipboardsData";


    private int px_fab;

    private static WindowManager manager;
    private static View rootView;
    private static WindowManager.LayoutParams topParams;


    private static final int MAX_CLICK_DURATION = 200;
    private long startClickTime;


    private DisplayMetrics calculateDisplayMetrics() {
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        mDisplay.getMetrics(mDisplayMetrics);
        return mDisplayMetrics;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

 /*   @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //super.onStartCommand(intent, flags, startId);
        //startTimer();
        return START_STICKY;
    }
*/


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

        try {
            manager.removeView(rootView);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate() {
        super.onCreate();


        EventBus.getDefault().register(this);

        //Intent openWidgetIntent = new Intent(getBaseContext(), FabNotificationService.class);


        //openWidgetIntent.putExtra(ACTION_OPEN_WIDGET, true);
        Intent openWidgetIntent = new Intent(this, OpenVoice.class);
        //openWidgetIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        openWidgetPI = PendingIntent.getBroadcast(getBaseContext(), 10, openWidgetIntent, PendingIntent.FLAG_CANCEL_CURRENT);


        Intent openAppIntent = new Intent(this, MainActivity.class);
        openAppIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        openAppIntent.setAction(Intent.ACTION_MAIN);
        openAppIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        //Intent openAppIntentClip = new Intent(this, TransparentActivity.class);
        //openAppIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //openAppIntent.setAction(Intent.ACTION_MAIN);
        //openAppIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        Intent openWidgetIntent_2 = new Intent(this, OpenVoice.class);
        //openWidgetIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        openWidgetPI = PendingIntent.getBroadcast(getBaseContext(), 10, openWidgetIntent_2, PendingIntent.FLAG_CANCEL_CURRENT);


        openAppPI = PendingIntent.getActivity(getBaseContext(), 0, openAppIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        //openAppClip = PendingIntent.getActivity(getBaseContext(), 0, openAppIntentClip, PendingIntent.FLAG_CANCEL_CURRENT);
        
        contentView = new RemoteViews(getPackageName(), R.layout.push_layout);

        Intent openWidgetIntent_clip = new Intent(this, TransparentActivity.class);
        openWidgetIntent_clip.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);



        PendingIntent openWidgetPI_2 = PendingIntent.getActivity(getBaseContext(), 10, openWidgetIntent_clip, PendingIntent.FLAG_CANCEL_CURRENT);

        //contentView_2.setOnClickPendingIntent(R.id.ok_voice, openWidgetPI);



        int paddings = (getResources().getDisplayMetrics().widthPixels - dpToPx((int) (5 * 50 + 54.5 + 2 * 8))) / 5;
        contentView.setImageViewResource(R.id.statusNotification, R.drawable.icn_status_online_active);
        contentView.setViewPadding(R.id.block_1, 0, 0, paddings, 0);
        contentView.setViewPadding(R.id.block_2, 0, 0, paddings, 0);
        contentView.setViewPadding(R.id.block_3, 0, 0, paddings, 0);
        contentView.setViewPadding(R.id.block_4, 0, 0, paddings, 0);
        contentView.setViewPadding(R.id.block_5, 0, 0, paddings, 0);
        contentView.setOnClickPendingIntent(R.id.push_girl, openWidgetPI);
        contentView.setOnClickPendingIntent(R.id.block_2, openAppPI);
        contentView.setOnClickPendingIntent(R.id.block_3, openAppPI);
        contentView.setOnClickPendingIntent(R.id.block_1, openWidgetPI_2);

        if(countNewCLips == 0){
            contentView.setTextViewText(R.id.countClipsUnread, "");

        }else{
            contentView.setTextViewText(R.id.countClipsUnread, String.valueOf(countNewCLips));
        }


        //contentView.setOnClickPendingIntent(R.id.block_1, openAppClip);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startMyOwnForeground();
        } else {

            notification = new NotificationCompat.Builder(getApplicationContext())
                    .setOngoing(true)
                    .setSmallIcon(R.drawable.new_icon)
                    .setContent(contentView)
                    .build();

            startForeground(1, notification);


        }

        clipboardManager = (ClipboardManager) this.getSystemService(CLIPBOARD_SERVICE);


       /* clipChangedListener = () -> {

        };*/


        //ClipboardManager.OnPrimaryClipChangedListener onPrimaryClipChangedListener = () -> new Thread(this::saveClipboardData).start();

        clipboardManager.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {



                        /*try {
                            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                            PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
                                    | PowerManager.ACQUIRE_CAUSES_WAKEUP
                                    | PowerManager.ON_AFTER_RELEASE, "MyWakeLock2");
                            wakeLock.acquire();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {


                            Intent dialogIntent = new Intent(FabNotificationService.this, Main3Activity.class);
                            dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(dialogIntent);


                            //Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(getPackageName());
                            //startActivity(LaunchIntent);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }*/


                        saveClipboardData();

                    }
                }).start();
            }
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }


        manager = (WindowManager) getSystemService(WINDOW_SERVICE);


        DisplayMetrics displayMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(displayMetrics);

        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        px_fab = (int) (28 * getApplicationContext().getResources().getDisplayMetrics().density);

        //int offset = (int) TypedValue.applyDimension(1, 50.0f, FabNotificationService.this.getResources().getDisplayMetrics());

        topParams = new WindowManager.LayoutParams(
                px_fab, // Ширина экрана
                /*(height/2) +*/ px_fab, // Высота экрана
                LAYOUT_FLAG, // Говорим, что приложение будет поверх других. В поздних API > 26, данный флаг перенесен на TYPE_APPLICATION_OVERLAY
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN, // Необходимо для того чтобы TouchEvent'ы в пустой области передавались на другие приложения
                PixelFormat.TRANSLUCENT); // Само окно прозрачное

        // Задаем позиции для нашего Layout

        topParams.gravity = Gravity.START | Gravity.TOP;


        topParams.x = width;
        topParams.y = height / 2;

        // Отображаем наш Layout

        //GradientDrawable circle = (GradientDrawable) ContextCompat.getDrawable(getApplicationContext(), R.drawable.blue_circle).mutate();
        //circle.setColor(Color.parseColor("#ffffff"));

        rootView = (CircleImageView) LayoutInflater.from(this).inflate(R.layout.alert_window_application, null);

       // ((CircleImageView) rootView.findViewById(R.id.alert_back)).setBackground(circle);

        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE) {

                    int offset = (int) TypedValue.applyDimension(1, 50.0f, FabNotificationService.this.getResources().getDisplayMetrics());

                    topParams.x = ((int) event.getRawX()) - (px_fab / 2) /*- (offset / 2)*/;
                    topParams.y = ((int) event.getRawY()) - (px_fab / 2) /*- (offset / 2)*/;

                    manager.updateViewLayout(rootView, topParams);
                    //rootView.dispatchTouchEvent(MotionEvent.obtain(event));
                }

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    startClickTime = Calendar.getInstance().getTimeInMillis();
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {

                    long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                    if (clickDuration < MAX_CLICK_DURATION) {

                        try {
                            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                            @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
                                    | PowerManager.ACQUIRE_CAUSES_WAKEUP
                                    | PowerManager.ON_AFTER_RELEASE, "MyWakeLock2");
                            wakeLock.acquire();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {

                            Intent dialogIntent = new Intent(FabNotificationService.this, TransparentActivity.class);
                            dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(dialogIntent);


                            //Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(getPackageName());
                            //startActivity(LaunchIntent);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        //startActivity(new Intent(FabNotificationService.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

                        //getBaseContext().startActivity(MainActivity.class);

                        System.out.println("CLICK SCREEN = " + getPackageName());

                    }

                }
                return true;
            }
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(this))

                try {

                    SharedPreferences pref_switch = getSharedPreferences("SwitchFab", Context.MODE_PRIVATE);
                    boolean switchBoolean = pref_switch.getBoolean("switch", true);

                    if (switchBoolean)
                        manager.addView(rootView, topParams);

                } catch (Exception e) {
                    e.printStackTrace();
                }
        }


        System.out.println("START SERVICE");


        //clipboardManager.addPrimaryClipChangedListener(() -> new Thread(this::saveClipboardData).start());


        /*Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        // Use a language model based on free-form speech recognition.
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                getBaseContext().getPackageName());


        CustomRecognitionListener listener = new CustomRecognitionListener();
        SpeechRecognizer sr = SpeechRecognizer.createSpeechRecognizer(getBaseContext());
        sr.setRecognitionListener(listener);

        sr.startListening(intent);*/

        // Add custom listeners.

        new Thread(new Runnable() {
            @Override
            public void run() {
               /* AudioManager amanager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                amanager.setStreamMute(AudioManager.STREAM_NOTIFICATION, false);
                amanager.setStreamMute(AudioManager.STREAM_ALARM, false);
                amanager.setStreamMute(AudioManager.STREAM_MUSIC, true);
                amanager.setStreamMute(AudioManager.STREAM_RING, false);
                amanager.setStreamMute(AudioManager.STREAM_SYSTEM, false);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                amanager.setStreamMute(AudioManager.STREAM_MUSIC, false);*/

            }
        }).start();


    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //return super.onStartCommand(intent, flags, startId);
        //return START_REDELIVER_INTENT;
        return START_STICKY;
    }

    NotificationManager manager_2;

    NotificationChannel chan = null;

    private void startMyOwnForeground() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String NOTIFICATION_CHANNEL_ID = "com.example.extime";
            String channelName = "Service";


            chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_DEFAULT);



            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            manager_2 = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager_2 != null;
            manager_2.createNotificationChannel(chan);





            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);

            notification = notificationBuilder
                    .setOngoing(true)
                    .setSmallIcon(R.drawable.new_icon)
                    .setContent(contentView)

                    .build();


            startForeground(1, notification);
            //manager.notify(1,notificationBuilder.build());

        }
    }

    public static void showFab() {
        try {
            manager.addView(rootView, topParams);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public static void hideFab() {
        try {
            manager.removeView(rootView);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    private void saveClipboardData() {




        mSettings = this.getSharedPreferences(CONTACTS_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSettings.edit();
        Gson gson = new Gson();

        if (clipboard == null)
            clipboard = new ArrayList<>();


        try {

            ClipData clipData = clipboardManager.getPrimaryClip();
            ClipData.Item item = clipData.getItemAt(0);
            String str;
            if (item != null) {
                CharSequence text = item.getText();
                if (text == null) {
                    str = item.coerceToText(this).toString().trim();
                } else {
                    str = item.getText().toString().trim();
                }
                if (str == null && item.getUri() != null) {
                    str = item.getUri().toString().trim();
                }

                clipboard.addAll(0, ClipboardType.getListDataClipboard(str, getBaseContext()));
                if (clipboard.get(0) != null) {

                    //if(/*MainActivity.hideApp && */ MainActivity.scrollPos > 0){
                    MainActivity.checkNewClipboard = true;
                    countNewCLips++;
                    //}

                    if (clipboard.get(0).getListClipboards() != null && !clipboard.get(0).getListClipboards().isEmpty()) {
                        for (Clipboard clipboard : clipboard.get(0).getListClipboards()) {
                            if (clipboard != null) {
                                Contact c = ContactCacheService.find2(clipboard.getValueCopy(), clipboard.getType(), null);
                                if (c != null)
                                    clipboard.addContactToListSearch(c);
                            }
                        }
                    } else {
                        Contact c = ContactCacheService.find2(clipboard.get(0).getValueCopy(), clipboard.get(0).getType(), null);
                        if (c != null)
                            clipboard.get(0).addContactToListSearch(c);
                    }
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }


        if (clipboard.size() < 2) {

            Storage storage = new Storage(getApplicationContext());
            String path = Environment.getExternalStorageDirectory().getAbsolutePath();

            if (!mSettings.getString(CLIPBOARD, "").isEmpty()) {
                String spData = mSettings.getString(CLIPBOARD, "");

                if (spData.isEmpty())
                    clipboard = new ArrayList<>();
                else {
                    clipboard.addAll(gson.fromJson(spData, new TypeToken<ArrayList<Clipboard>>() {
                    }.getType()));
                }
                ArrayList<Clipboard> listClips = (ArrayList<Clipboard>) clipboard.clone();
                String jsonClipboard = gson.toJson(listClips);
                /*storage.deleteFile(path + "/Extime/ExtimeContacts/backupClipboard");
                storage.createFile(path + "/Extime/ExtimeContacts/backupClipboard", jsonClipboard);*/


                List<File> listBack = storage.getFiles(path + "/Extime/ExtimeContacts/");
                if (listBack != null) {
                    for (int i = 0; i < listBack.size(); i++) {
                        if ((listBack.get(i).getName().contains("backupClipboard_") && !listBack.get(i).getName().equalsIgnoreCase("backupClipboard_" + MainActivity.versionDebug)) || listBack.get(i).getName().equalsIgnoreCase("backupClipboard")) {
                            storage.copy(path + "/Extime/ExtimeContacts/" + listBack.get(i).getName(), path + "/Extime/Old backup/" + listBack.get(i).getName());
                            storage.rename(path + "/Extime/ExtimeContacts/" + listBack.get(i).getName(), path + "/Extime/ExtimeContacts/backupClipboard_" + MainActivity.versionDebug);
                        }
                    }
                }

                if (!MainActivity.processFileClipboard) {
                    MainActivity.processFileClipboard = true;
                    storage.deleteFile(path + "/Extime/ExtimeContacts/backupClipboard_" + MainActivity.versionDebug);
                    storage.createFile(path + "/Extime/ExtimeContacts/backupClipboard_" + MainActivity.versionDebug, jsonClipboard);
                    MainActivity.processFileClipboard = false;
                }

                //storage.deleteFile(path + "/Extime/ExtimeContacts/backup_"+MainActivity.versionDebug);
                // storage.createFile(path + "/Extime/ExtimeContacts/backup_"+MainActivity.versionDebug, json);


            } else {

                try {

                    String nameBackup = null;
                    List<File> listBack = storage.getFiles(path + "/Extime/ExtimeContacts/");
                    if (listBack != null) {
                        for (int i = 0; i < listBack.size(); i++) {
                            if (listBack.get(i).getName().contains("backupClipboard_") || listBack.get(i).getName().equalsIgnoreCase("backupClipboard")) {
                                nameBackup = listBack.get(i).getName();
                            }
                        }
                    }

                    String spData = storage.readTextFile(path + "/Extime/ExtimeContacts/" + nameBackup);
                    if (spData.length() > 1) {
                        clipboard.addAll(gson.fromJson(spData, new TypeToken<ArrayList<Clipboard>>() {
                        }.getType()));
                    }

                    String jsonClipboard = gson.toJson(clipboard);

                    editor.putString(CLIPBOARD, jsonClipboard);

                    editor.apply();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }



        if (clipboard != null && clipboard.size() > 0 && clipboard.get(0) != null && clipboard.get(0).getValueCopy() != null && clipboard.get(0).getValueCopy().contains("vk.com") && clipboard.get(0).getValueCopy().contains("?")) {
            int ind = clipboard.get(0).getValueCopy().indexOf('?');
            if (ind != -1)
                clipboard.get(0).setValueCopy(clipboard.get(0).getValueCopy().substring(0, ind));

        }

        if (clipboard != null && clipboard.size() > 0 && clipboard.get(0) != null && clipboard.get(0).getValueCopy() != null && clipboard.get(0).getValueCopy().toLowerCase().contains("instagram.com") && clipboard.get(0).getValueCopy().contains("?")) {
            int ind = clipboard.get(0).getValueCopy().indexOf('?');
            if (ind != -1)
                clipboard.get(0).setValueCopy(clipboard.get(0).getValueCopy().substring(0, ind));

        }

        if (clipboard.get(0).getValueCopy().charAt(clipboard.get(0).getValueCopy().length() - 1) == '/') {
            clipboard.get(0).setValueCopy(clipboard.get(0).getValueCopy().substring(0, clipboard.get(0).getValueCopy().length() - 1));
        }

        if (clipboard != null && clipboard.size() > 0 && clipboard.get(0) != null && clipboard.get(0).getValueCopy() != null && clipboard.get(0).getValueCopy().contains("twitter.com") && clipboard.get(0).getValueCopy().contains("?")) {
            int ind = clipboard.get(0).getValueCopy().indexOf('?');
            if (ind != -1)
                clipboard.get(0).setValueCopy(clipboard.get(0).getValueCopy().substring(0, ind));

        }

        if (clipboard != null && clipboard.size() > 0 && clipboard.get(0) != null && clipboard.get(0).getValueCopy() != null && (clipboard.get(0).getValueCopy().contains("youtu.be") || clipboard.get(0).getValueCopy().contains("youtube")) && clipboard.get(0).getValueCopy().contains("?") && !clipboard.get(0).getValueCopy().contains("watch?")) {
            int ind = clipboard.get(0).getValueCopy().indexOf('?');

            if (ind != -1)
                clipboard.get(0).setValueCopy(clipboard.get(0).getValueCopy().substring(0, ind));

        }

        if (clipboard != null && clipboard.size() > 0 && clipboard.get(0) != null && clipboard.get(0).getValueCopy() != null && (clipboard.get(0).getValueCopy().contains("facebook.com") || clipboard.get(0).getValueCopy().contains("fb.com")) && clipboard.get(0).getValueCopy().contains("?") && !clipboard.get(0).getValueCopy().contains("?id=") && !clipboard.get(0).getValueCopy().contains("?v=") && !clipboard.get(0).getValueCopy().contains("?story")) {
            int ind = clipboard.get(0).getValueCopy().indexOf("?");
            if (ind != -1)
                clipboard.get(0).setValueCopy(clipboard.get(0).getValueCopy().substring(0, ind));

        }

        if (clipboard != null && clipboard.size() > 0 && clipboard.get(0) != null && clipboard.get(0).getValueCopy() != null && (clipboard.get(0).getValueCopy().contains("facebook.com") || clipboard.get(0).getValueCopy().contains("fb.com")) && clipboard.get(0).getValueCopy().contains("&__")) {
            int ind = clipboard.get(0).getValueCopy().indexOf("&__");
            if (ind != -1)
                clipboard.get(0).setValueCopy(clipboard.get(0).getValueCopy().substring(0, ind));
        }

        /*if (clipboard != null && clipboard.size() > 0 && clipboard.get(0) != null && clipboard.get(0).getValueCopy() != null && clipboard.get(0).getType().equals(ClipboardEnum.WEB) && clipboard.get(0).getValueCopy().contains("?")) {
            int ind = clipboard.get(0).getValueCopy().indexOf("?");
            if (ind != -1)
                clipboard.get(0).setValueCopy(clipboard.get(0).getValueCopy().substring(0, ind));
        }*/




        if (clipboard != null && clipboard.size() > 4) {
            // for (int i = 0; i < clipboard.size(); i++) {
            for (int i = 1; i < 4; i++) {

                try {
                    if (clipboard.get(0).getValueCopy().equals(clipboard.get(i).getValueCopy())) {
                        clipboard.set(0, clipboard.get(i));

                        if (i == 1){
                            MainActivity.checkNewClipboard = false;
                            countNewCLips--;
                        }

                        clipboard.remove(i);
                        i--;
                    }
                } catch (Exception e) {

                    e.printStackTrace();
                }

            }
        }


        ArrayList<Clipboard> listSave = new ArrayList<>();
        try {
            listSave.addAll(clipboard);
        } catch (Exception e) {
            listSave.clear();
            listSave.addAll(clipboard);
        }



        /*Context c = this;
        new Thread(new Runnable() {
            @Override
            public void run() {


                try {
                    String jsonClipboard = gson.toJson(listSave);   //error java.lang.IllegalStateException: Realm access from incorrect thread. Realm objects can only be accessed on the thread they were created.



                    editor.putString(CLIPBOARD, jsonClipboard);
                    editor.apply();

                    Storage storage = new Storage(c);
                    String path = Environment.getExternalStorageDirectory().getAbsolutePath();

                    //storage.deleteFile(path + "/Extime/ExtimeContacts/backupClipboard");
                    //storage.createFile(path + "/Extime/ExtimeContacts/backupClipboard", jsonClipboard);

                    List<File> listBack = storage.getFiles(path + "/Extime/ExtimeContacts/");
                    if (listBack != null) {
                        for (int i = 0; i < listBack.size(); i++) {
                            if ((listBack.get(i).getName().contains("backupClipboard_") && !listBack.get(i).getName().equalsIgnoreCase("backupClipboard_" + MainActivity.versionDebug)) || listBack.get(i).getName().equalsIgnoreCase("backupClipboard")) {
                                storage.copy(path + "/Extime/ExtimeContacts/" + listBack.get(i).getName(), path + "/Extime/Old backup/" + listBack.get(i).getName());
                                storage.rename(path + "/Extime/ExtimeContacts/" + listBack.get(i).getName(), path + "/Extime/ExtimeContacts/backupClipboard_" + MainActivity.versionDebug);
                            }
                        }
                    }

                    if (!MainActivity.processFileClipboard) {
                        MainActivity.processFileClipboard = true;
                        storage.deleteFile(path + "/Extime/ExtimeContacts/backupClipboard_" + MainActivity.versionDebug);
                        storage.createFile(path + "/Extime/ExtimeContacts/backupClipboard_" + MainActivity.versionDebug, jsonClipboard);
                        MainActivity.processFileClipboard = false;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();*/



        EventBus.getDefault().post(new CopyValueEvent());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(TestService event) {

        saveTextToClipboardData(event.getText());



        if(event.open){

            EventBus.getDefault().post(new OpenClipboard(event.getText()));
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(CheckClipboard event) {

        //getClip();
    }
    
   /* public void getClip(){
        try {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
                    | PowerManager.ACQUIRE_CAUSES_WAKEUP
                    | PowerManager.ON_AFTER_RELEASE, "MyWakeLock2");
            wakeLock.acquire();

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {

            Intent dialogIntent = new Intent(FabNotificationService.this, TransparentActivity.class);
            dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(dialogIntent);


            //Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(getPackageName());
            //startActivity(LaunchIntent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/



    public boolean checkAgain = false;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(VoiceAgainEvent event) {
        checkAgain = true;
        startVoiceMode();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NewListenEvent event) {

       /* listener.onEndOfSpeech();
        sr.stopListening();*/
        startVoiceMode();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SetVoicePush event) {
        startVoiceMode();

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(StopVoiceEvent event) {

        sr.stopListening();
        setDefault();

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SetDefaultPush event) {
        setDefault();

    }

    SpeechRecognizer sr;
    CustomRecognitionListener listener;

    public void startVoiceMode() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        // Use a language model based on free-form speech recognition.

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en_US");
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);

        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);

        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                getBaseContext().getPackageName());


        listener = new CustomRecognitionListener();
        sr = SpeechRecognizer.createSpeechRecognizer(getBaseContext());
        sr.setRecognitionListener(listener);


        sr.startListening(intent);
    }

    public static int countNewCLips = 0;

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void saveTextToClipboardData(String str) {




        mSettings = this.getSharedPreferences(CONTACTS_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSettings.edit();
        Gson gson = new Gson();

        if (clipboard == null)
            clipboard = new ArrayList<>();



        try {

            clipboard.addAll(0, ClipboardType.getListDataClipboard(str, getBaseContext()));
            if (clipboard.get(0) != null) {

                //if(/*MainActivity.hideApp && */ MainActivity.scrollPos > 0){
                MainActivity.checkNewClipboard = true;
                countNewCLips++;

                //}

                if (clipboard.get(0).getListClipboards() != null && !clipboard.get(0).getListClipboards().isEmpty()) {
                    for (Clipboard clipboard : clipboard.get(0).getListClipboards()) {
                        if (clipboard != null) {
                            Contact c = ContactCacheService.find2(clipboard.getValueCopy(), clipboard.getType(), null);
                            if (c != null)
                                clipboard.addContactToListSearch(c);
                        }
                    }
                } else {
                    Contact c = ContactCacheService.find2(clipboard.get(0).getValueCopy(), clipboard.get(0).getType(), null);
                    if (c != null)
                        clipboard.get(0).addContactToListSearch(c);
                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        }





        if (clipboard.size() < 2) {

            //String spData = mSettings.getString(CLIPBOARD, "");

            //Gson gson = new Gson();
            //String jsonClipboard = gson.toJson(spData);
            Storage storage = new Storage(getApplicationContext());
            String path = Environment.getExternalStorageDirectory().getAbsolutePath();

            if (!mSettings.getString(CLIPBOARD, "").isEmpty()) {
                String spData = mSettings.getString(CLIPBOARD, "");

                if (spData.isEmpty())
                    clipboard = new ArrayList<>();
                else {
                    clipboard.addAll(gson.fromJson(spData, new TypeToken<ArrayList<Clipboard>>() {
                    }.getType()));
                }
                ArrayList<Clipboard> listClips = (ArrayList<Clipboard>) clipboard.clone();
                String jsonClipboard = gson.toJson(listClips);
                /*storage.deleteFile(path + "/Extime/ExtimeContacts/backupClipboard");
                storage.createFile(path + "/Extime/ExtimeContacts/backupClipboard", jsonClipboard);*/


                List<File> listBack = storage.getFiles(path + "/Extime/ExtimeContacts/");
                if (listBack != null) {
                    for (int i = 0; i < listBack.size(); i++) {
                        if ((listBack.get(i).getName().contains("backupClipboard_") && !listBack.get(i).getName().equalsIgnoreCase("backupClipboard_" + MainActivity.versionDebug)) || listBack.get(i).getName().equalsIgnoreCase("backupClipboard")) {
                            storage.copy(path + "/Extime/ExtimeContacts/" + listBack.get(i).getName(), path + "/Extime/Old backup/" + listBack.get(i).getName());
                            storage.rename(path + "/Extime/ExtimeContacts/" + listBack.get(i).getName(), path + "/Extime/ExtimeContacts/backupClipboard_" + MainActivity.versionDebug);
                        }
                    }
                }

                if (!MainActivity.processFileClipboard) {
                    MainActivity.processFileClipboard = true;
                    storage.deleteFile(path + "/Extime/ExtimeContacts/backupClipboard_" + MainActivity.versionDebug);
                    storage.createFile(path + "/Extime/ExtimeContacts/backupClipboard_" + MainActivity.versionDebug, jsonClipboard);
                    MainActivity.processFileClipboard = false;
                }

                //storage.deleteFile(path + "/Extime/ExtimeContacts/backup_"+MainActivity.versionDebug);
                // storage.createFile(path + "/Extime/ExtimeContacts/backup_"+MainActivity.versionDebug, json);


            } else {

                try {

                    String nameBackup = null;
                    List<File> listBack = storage.getFiles(path + "/Extime/ExtimeContacts/");
                    if (listBack != null) {
                        for (int i = 0; i < listBack.size(); i++) {
                            if (listBack.get(i).getName().contains("backupClipboard_") || listBack.get(i).getName().equalsIgnoreCase("backupClipboard")) {
                                nameBackup = listBack.get(i).getName();
                            }
                        }
                    }

                    String spData = storage.readTextFile(path + "/Extime/ExtimeContacts/" + nameBackup);
                    if (spData.length() > 1) {
                        clipboard.addAll(gson.fromJson(spData, new TypeToken<ArrayList<Clipboard>>() {
                        }.getType()));
                    }

                    String jsonClipboard = gson.toJson(clipboard);

                    editor.putString(CLIPBOARD, jsonClipboard);

                    editor.apply();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }


        if (clipboard != null && clipboard.size() > 0 && clipboard.get(0) != null && clipboard.get(0).getValueCopy() != null && clipboard.get(0).getValueCopy().contains("vk.com") && clipboard.get(0).getValueCopy().contains("?")) {
            int ind = clipboard.get(0).getValueCopy().indexOf('?');
            if (ind != -1)
                clipboard.get(0).setValueCopy(clipboard.get(0).getValueCopy().substring(0, ind));

        }

        if (clipboard != null && clipboard.size() > 0 && clipboard.get(0) != null && clipboard.get(0).getValueCopy() != null && clipboard.get(0).getValueCopy().toLowerCase().contains("instagram.com") && clipboard.get(0).getValueCopy().contains("?")) {
            int ind = clipboard.get(0).getValueCopy().indexOf('?');
            if (ind != -1)
                clipboard.get(0).setValueCopy(clipboard.get(0).getValueCopy().substring(0, ind));

        }

        if (clipboard.get(0).getValueCopy().charAt(clipboard.get(0).getValueCopy().length() - 1) == '/') {
            clipboard.get(0).setValueCopy(clipboard.get(0).getValueCopy().substring(0, clipboard.get(0).getValueCopy().length() - 1));
        }

        if (clipboard != null && clipboard.size() > 0 && clipboard.get(0) != null && clipboard.get(0).getValueCopy() != null && clipboard.get(0).getValueCopy().contains("twitter.com") && clipboard.get(0).getValueCopy().contains("?")) {
            int ind = clipboard.get(0).getValueCopy().indexOf('?');
            if (ind != -1)
                clipboard.get(0).setValueCopy(clipboard.get(0).getValueCopy().substring(0, ind));

        }

        if (clipboard != null && clipboard.size() > 0 && clipboard.get(0) != null && clipboard.get(0).getValueCopy() != null && (clipboard.get(0).getValueCopy().contains("youtu.be") || clipboard.get(0).getValueCopy().contains("youtube")) && clipboard.get(0).getValueCopy().contains("?") && !clipboard.get(0).getValueCopy().contains("watch?")) {
            int ind = clipboard.get(0).getValueCopy().indexOf('?');
            if (ind != -1)
                clipboard.get(0).setValueCopy(clipboard.get(0).getValueCopy().substring(0, ind));

        }

        if (clipboard != null && clipboard.size() > 0 && clipboard.get(0) != null && clipboard.get(0).getValueCopy() != null && clipboard.get(0).getValueCopy().contains("facebook.com") && clipboard.get(0).getValueCopy().contains("?") && !clipboard.get(0).getValueCopy().contains("?id=") && !clipboard.get(0).getValueCopy().contains("?v=") && !clipboard.get(0).getValueCopy().contains("?story")) {
            int ind = clipboard.get(0).getValueCopy().indexOf("?");
            if (ind != -1)
                clipboard.get(0).setValueCopy(clipboard.get(0).getValueCopy().substring(0, ind));

        }

        if (clipboard != null && clipboard.size() > 0 && clipboard.get(0) != null && clipboard.get(0).getValueCopy() != null && clipboard.get(0).getValueCopy().contains("facebook.com") && clipboard.get(0).getValueCopy().contains("&__")) {
            int ind = clipboard.get(0).getValueCopy().indexOf("&__");
            if (ind != -1)
                clipboard.get(0).setValueCopy(clipboard.get(0).getValueCopy().substring(0, ind));

        }

        /*if (clipboard != null && clipboard.size() > 0 && clipboard.get(0) != null && clipboard.get(0).getValueCopy() != null && clipboard.get(0).getType().equals(ClipboardEnum.WEB) && clipboard.get(0).getValueCopy().contains("?")) {
            int ind = clipboard.get(0).getValueCopy().indexOf("?");
            if (ind != -1)
                clipboard.get(0).setValueCopy(clipboard.get(0).getValueCopy().substring(0, ind));
        }*/



        //try {

        boolean rem = false;


        if (clipboard != null && clipboard.size() > 4) {
            // for (int i = 0; i < clipboard.size(); i++) {
            for (int i = 1; i < 4; i++) {

                try {
                    if (clipboard.get(0).getValueCopy().equals(clipboard.get(i).getValueCopy())) {

                        if (i == 1){
                            MainActivity.checkNewClipboard = false;
                            countNewCLips--;
                            rem = true;
                        }

                        clipboard.set(0, clipboard.get(i));
                        clipboard.remove(i);
                        i--;
                    }
                } catch (Exception e) {
                    System.out.println("ERROR REMOVE CLIPBOARD");
                    e.printStackTrace();
                }

            }
        }

        if(!rem)
            setDefault();

        System.out.println("BUFER 5");
        ArrayList<Clipboard> listSave = new ArrayList<>();
        try {
            listSave.addAll(clipboard);
        } catch (Exception e) {
            listSave.clear();
            listSave.addAll(clipboard);
        }
        //System.out.println("TO STRING = " + listSave.toString());
        //String jsonClipboard = gson.toJson(listSave);

        //String jsonClipboard = gson.toJson(clipboard);


        /*Context c = this;
        new Thread(new Runnable() {
            @Override
            public void run() {


                try {
                    String jsonClipboard = gson.toJson(listSave);   //error java.lang.IllegalStateException: Realm access from incorrect thread. Realm objects can only be accessed on the thread they were created.

                    System.out.println("JSON === " + jsonClipboard);

                    editor.putString(CLIPBOARD, jsonClipboard);
                    editor.apply();

                    Storage storage = new Storage(c);
                    String path = Environment.getExternalStorageDirectory().getAbsolutePath();

                    //storage.deleteFile(path + "/Extime/ExtimeContacts/backupClipboard");
                    //storage.createFile(path + "/Extime/ExtimeContacts/backupClipboard", jsonClipboard);

                    List<File> listBack = storage.getFiles(path + "/Extime/ExtimeContacts/");
                    if (listBack != null) {
                        for (int i = 0; i < listBack.size(); i++) {
                            if ((listBack.get(i).getName().contains("backupClipboard_") && !listBack.get(i).getName().equalsIgnoreCase("backupClipboard_" + MainActivity.versionDebug)) || listBack.get(i).getName().equalsIgnoreCase("backupClipboard")) {
                                storage.copy(path + "/Extime/ExtimeContacts/" + listBack.get(i).getName(), path + "/Extime/Old backup/" + listBack.get(i).getName());
                                storage.rename(path + "/Extime/ExtimeContacts/" + listBack.get(i).getName(), path + "/Extime/ExtimeContacts/backupClipboard_" + MainActivity.versionDebug);
                            }
                        }
                    }

                    if (!MainActivity.processFileClipboard) {
                        MainActivity.processFileClipboard = true;
                        storage.deleteFile(path + "/Extime/ExtimeContacts/backupClipboard_" + MainActivity.versionDebug);
                        storage.createFile(path + "/Extime/ExtimeContacts/backupClipboard_" + MainActivity.versionDebug, jsonClipboard);
                        MainActivity.processFileClipboard = false;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();*/



        EventBus.getDefault().post(new CopyValueEvent());
    }

    /*private void showWidget() {
        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PRIORITY_PHONE,
                LayoutParamFlags,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.LEFT;
        windowManager = (WindowManager) this.getSystemService(WINDOW_SERVICE);
        mDisplay = windowManager.getDefaultDisplay();
        inflater = LayoutInflater.from(this);
        layoutView = inflater.inflate(R.layout.widget_layout, null);
        windowManager.addView(layoutView, params);

        final FloatingActionMenu button = (FloatingActionMenu) layoutView.findViewById(R.id.fabMenuContainerWidget);

        button.setOnMenuButtonLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });

        button.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;
            private long downTime;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        downTime = SystemClock.elapsedRealtime();
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        long currentTime = SystemClock.elapsedRealtime();
                        if (currentTime - downTime < 200) {
                            v.performClick();
                        } else {
                            updateViewLocation();
                        }
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        windowManager.updateViewLayout(layoutView, params);
                        return true;
                }
                return false;
            }

            private void updateViewLocation() {
                DisplayMetrics metrics = calculateDisplayMetrics();
                windowManager.updateViewLayout(layoutView, params);
            }
        });
    }*/
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setDef() {


        RemoteViews contentView_2 = new RemoteViews(getPackageName(), R.layout.push_layout_before_voice);

        Intent openWidgetIntent = new Intent(this, StopVoice.class);


        PendingIntent openWidgetPI = PendingIntent.getBroadcast(getBaseContext(), 10, openWidgetIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        contentView_2.setOnClickPendingIntent(R.id.ok_before, openWidgetPI);

        //CharSequence title = getText(R.string.title_activity);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, new Intent(this, MainActivity.class), 0);
        String NOTIFICATION_CHANNEL_ID = "com.example.extime";
        Notification notification_2 = new Notification.Builder(this, NOTIFICATION_CHANNEL_ID)
                //.setContentTitle("11")
                .setSmallIcon(R.drawable.new_icon)
                .setContent(contentView_2).getNotification();

        //NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager_2.notify(1, notification_2);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setDefNew() {

        checkAgain = false;

        RemoteViews contentView_2 = new RemoteViews(getPackageName(), R.layout.push_layout_before_new);

        Intent openWidgetIntent = new Intent(this, StopVoice.class);


        PendingIntent openWidgetPI = PendingIntent.getBroadcast(getBaseContext(), 10, openWidgetIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        contentView_2.setOnClickPendingIntent(R.id.ok_before, openWidgetPI);



        //CharSequence title = getText(R.string.title_activity);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, new Intent(this, MainActivity.class), 0);
        String NOTIFICATION_CHANNEL_ID = "com.example.extime";
        Notification notification_2 = new Notification.Builder(this, NOTIFICATION_CHANNEL_ID)
                //.setContentTitle("11")
                .setSmallIcon(R.drawable.new_icon)
                .setContent(contentView_2).getNotification();

        //NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager_2.notify(1, notification_2);
    }


    public static String textVoice = null;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void updateNotifi(String t) {

        MainActivity.voice_command = null;

        textVoice = t;

        /*textVoice = textVoice.replaceAll("инвест","invest");
        textVoice = textVoice.replaceAll("Инвест","Invest");*/

        RemoteViews contentView_2 = new RemoteViews(getPackageName(), R.layout.push_layout_voice);

        //contentView_2.setTextViewText(R.id.textPush, "");
        contentView_2.setTextViewText(R.id.textPush, textVoice);

        String[] mas = null;

        if (textVoice == null || textVoice.isEmpty()) {

            Intent openWidgetIntent = new Intent(this, StopVoice.class);

            PendingIntent openWidgetPI = PendingIntent.getBroadcast(getBaseContext(), 10, openWidgetIntent, PendingIntent.FLAG_CANCEL_CURRENT);

            contentView_2.setOnClickPendingIntent(R.id.ok_voice, openWidgetPI);

        } else {

            mas = textVoice.split(" ");

            Intent openAppIntent = new Intent(this, MainActivity.class).putExtra("voice_command", textVoice);

            PendingIntent openTemplate = PendingIntent.getActivity(getBaseContext(), 0, openAppIntent, PendingIntent.FLAG_CANCEL_CURRENT);

            contentView_2.setOnClickPendingIntent(R.id.ok_voice, openTemplate);
        }

        if (textVoice == null || textVoice.isEmpty() || (!mas[0].toLowerCase().contains("email") &&  !mas[0].toLowerCase().contains("call"))) {


            Intent openWidgetIntent = new Intent(this, VoiceAgain.class);

            PendingIntent openWidgetPI = PendingIntent.getBroadcast(getBaseContext(), 10, openWidgetIntent, PendingIntent.FLAG_CANCEL_CURRENT);

            contentView_2.setOnClickPendingIntent(R.id.ok_voice, openWidgetPI);
        }


        Intent openWidgetIntent = new Intent(this, NewListen.class);

        PendingIntent openWidgetPIVoice = PendingIntent.getBroadcast(getBaseContext(), 10, openWidgetIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        contentView_2.setOnClickPendingIntent(R.id.new_voice, openWidgetPIVoice);


        //CharSequence title = getText(R.string.title_activity);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, new Intent(this, MainActivity.class), 0);
        String NOTIFICATION_CHANNEL_ID = "com.example.extime";
        Notification notification_2 = new Notification.Builder(this, NOTIFICATION_CHANNEL_ID)
                //.setContentTitle("11")

                .setSmallIcon(R.drawable.new_icon)
                .setContent(contentView_2).getNotification();

        //NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager_2.notify(1, notification_2);
        System.out.println("LAST TEXT = " + textVoice);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setDefault() {

        if(countNewCLips == 0){
            contentView.setTextViewText(R.id.countClipsUnread, "");

        }else{
            contentView.setTextViewText(R.id.countClipsUnread, String.valueOf(countNewCLips));
        }

        //chan.setImportance(NotificationManager.IMPORTANCE_DEFAULT);

        manager_2.notify(1, notification);

        //chan.setImportance(NotificationManager.IMPORTANCE_HIGH);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void endVoice() {
        RemoteViews contentView_2 = new RemoteViews(getPackageName(), R.layout.push_layout_voice);

        contentView_2.setTextViewText(R.id.textPush, textVoice);

        Intent openAppIntent = new Intent(this, MainActivity.class).putExtra("voice_command", textVoice);

        PendingIntent openTemplate = PendingIntent.getActivity(getBaseContext(), 0, openAppIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        contentView_2.setOnClickPendingIntent(R.id.ok_voice, openTemplate);

        //CharSequence title = getText(R.string.title_activity);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, new Intent(this, MainActivity.class), 0);
        String NOTIFICATION_CHANNEL_ID = "com.example.extime";
        Notification notification_2 = new Notification.Builder(this, NOTIFICATION_CHANNEL_ID)
                //.setContentTitle("11")
                .setContentText(textVoice)
                .setSmallIcon(R.drawable.new_icon)
                .setContent(contentView_2).getNotification();

        //NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager_2.notify(1, notification_2);
    }

    class CustomRecognitionListener implements RecognitionListener {

        private static final String TAG = "TAGS";


        @RequiresApi(api = Build.VERSION_CODES.O)
        public void onReadyForSpeech(Bundle params) {
            Log.d(TAG, "onReadyForSpeech");
            if (checkAgain)
                setDefNew();
            else
                setDef();
            textVoice = null;

        }

        public void onBeginningOfSpeech() {
            Log.d(TAG, "onBeginningOfSpeech");
            check_text = false;

        }

        public void onRmsChanged(float rmsdB) {
            //Log.d(TAG, "onRmsChanged");
        }

        public void onBufferReceived(byte[] buffer) {
            Log.d(TAG, "onBufferReceived");
        }

        public void onEndOfSpeech() {
            Log.d(TAG, "onEndofSpeech");

            //updateNotifi();
        }

        public void onError(int error) {


            String message;
            switch (error) {
                case SpeechRecognizer.ERROR_AUDIO:
                    message = "Audio recording error";
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    message = "Client side error";
                    break;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    message = "Insufficient permissions";
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    message = "Network error";
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    message = "Network timeout";
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    message = "No match";
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    message = "RecognitionService busy";
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    message = "error from server";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    message = "No speech input";
                    break;
                default:
                    message = "Didn't understand, please try again.";
                    break;
            }

            Log.e(TAG, "error: " + message);

            //conversionCallaback.onErrorOccured(TranslatorUtil.getErrorText(error));
        }

        boolean check_text = false;

        @RequiresApi(api = Build.VERSION_CODES.O)
        public void onResults(Bundle results) {
            ArrayList<String> matches = results
                    .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            String text = matches.get(0).toString();
           /* for (String result : matches)
                text += result + "\n";*/

          /*  ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            for (int i = 0; i < data.size(); i++)
            {
                Log.d(TAG, "result " + data.get(i));
                text += data.get(i);
            }*/

            //Toast.makeText(getApplicationContext(), data.get(0).toString(), Toast.LENGTH_SHORT).show();

            //System.out.println("TEXT VOICE 1= " + data.get(0).toString());

            System.out.println("TEXT VOICE= " + text);
            //textVoice = text;

            /*if (!check_text)*/
            updateNotifi(text);

            check_text = true;
            //endVoice();

        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onPartialResults(Bundle partialResults) {
            ArrayList data = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            String word = (String) data.get(data.size() - 1);
            System.out.println("TEXT VOICE = " + word);

            Log.i("TEST", "partial_results: " + word);

            updateNotifi(word);

        }


        public void onEvent(int eventType, Bundle params) {
            Log.d(TAG, "onEvent " + eventType);
        }
    }


}
