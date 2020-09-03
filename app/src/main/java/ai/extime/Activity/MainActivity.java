package ai.extime.Activity;

import android.Manifest;
import android.app.Activity;

import android.app.ActivityManager;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.PictureDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.bumptech.glide.GenericRequestBuilder;
import com.bumptech.glide.Glide;
import com.caverock.androidsvg.SVG;
import com.github.clans.fab.FloatingActionMenu;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.rustamg.filedialogs.FileDialog;
import com.rustamg.filedialogs.OpenFileDialog;
import com.snatik.storage.Storage;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

import ai.extime.Adapters.DialogAdapter;
import ai.extime.Adapters.HashTagsAdapter;

import ai.extime.Enums.MagicType;
import ai.extime.Enums.TypeCardTimeline;
import ai.extime.Events.AddHistoryEntry;
import ai.extime.Events.CheckLinkMessageReply;
import ai.extime.Events.CheckLinkMessageTEmails;
import ai.extime.Events.CheckLinkMessageTemplateFragment;
import ai.extime.Events.CheckLinkMessageTemplateFragmentEdit;
import ai.extime.Events.ClickNameCreate;
import ai.extime.Events.HideHistoryPopup;
import ai.extime.Events.NotifyClipboardAdapter;
import ai.extime.Events.OpenClipboard;
import ai.extime.Events.OpenDriver;
import ai.extime.Events.SaveChangesProfile;
import ai.extime.Events.SaveCreate;
import ai.extime.Events.SaveHistory;
import ai.extime.Events.SetDefaultPush;
import ai.extime.Events.SetMainProfile;
import ai.extime.Events.StartProcessDial;
import ai.extime.Events.StatusPopupGone;
import ai.extime.Events.TypeCard;
import ai.extime.Fragments.ReplyFragment;
import ai.extime.Interfaces.ContactBarInter;
import ai.extime.Interfaces.FileBarInter;
import ai.extime.Interfaces.PopupsInter;
import ai.extime.Interfaces.Postman;
import ai.extime.Models.ContactDataClipboard;
import ai.extime.Models.DataUpdate;
import ai.extime.Models.HashTag;
import ai.extime.Models.SaveAndUpdateHistoryList;
import ai.extime.Models.Template;
import ai.extime.Models.TestService;
import ai.extime.Models.UpdateCountClipboard;
import ai.extime.ParceToJsonClipboard;
import ai.extime.Services.ContactCacheService;
import ai.extime.Utils.ClipboardType;
import ai.extime.Utils.SocialEq;
import ai.extime.Utils.SwipeController;
import ai.extime.Utils.SwipeControllerActions;
import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import ai.extime.Adapters.ClibpboardAdapter;
import ai.extime.Adapters.CompanyAdapter;
import ai.extime.Adapters.ContactAdapter;
import ai.extime.Enums.ClipboardEnum;
import ai.extime.Enums.FragmentTypeEnum;
import ai.extime.Events.ClickBackOnProfile;
import ai.extime.Events.ContactLoadEvent;
import ai.extime.Events.CopyFile;
import ai.extime.Events.CopyValueEvent;
import ai.extime.Events.LoadBarEvent;
import ai.extime.Events.SaveClipboard;
import ai.extime.Events.StartSync;
import ai.extime.Events.UpdateContactCreate;
import ai.extime.Events.UpdateContactInProfile;
import ai.extime.Events.UpdateFile;
import ai.extime.Events.UpdateFileFromRealmResult;
import ai.extime.Events.UpdateFileSync;
import ai.extime.Fragments.ContactsFragment;
import ai.extime.Fragments.CreateFragment;
import ai.extime.Fragments.EmailsFragment;
import ai.extime.Fragments.FilesFragment;
import ai.extime.Fragments.LogFragment;
import ai.extime.Fragments.OrdersFragment;
import ai.extime.Fragments.PlacesFragment;
import ai.extime.Fragments.ProfileFragment;
import ai.extime.Fragments.ScheduleFragment;
import ai.extime.Fragments.TimeLineFragment;
import ai.extime.Models.Clipboard;
import ai.extime.Models.Contact;
import ai.extime.Models.ContactInfo;
import ai.extime.Models.SocialModel;

import com.extime.R;

import ai.extime.Services.ContactLoadService;
import ai.extime.Services.ContactsService;
import ai.extime.Services.FabNotificationService;
import ai.extime.Services.UpdateFileService;
import ai.extime.Utils.Call2meApplication;


public class MainActivity extends AppCompatActivity implements FileDialog.OnFileSelectedListener, Postman {

    private Toolbar mainToolBar;

    public Fragment selectedFragment;

    FrameLayout sortImage;

    LinearLayout hashTagBlock;

    LinearLayout magicBlock;

    private ViewFlipper viewFlipper;

    public static boolean firstRun = true;

    public static boolean loadFromFile = false;

    public Contact PROFILE_CONTACT;

    public int SELECTED_STATUS_IMAGE = R.drawable.icn_status_online_active;

    private PlacesFragment placesFragment;

    private ContactsFragment contactsFragment;

    private ScheduleFragment scheduleFragment;

    private EmailsFragment emailsFragment;

    private TimeLineFragment timeLineFragment;

    private FilesFragment filesFragment;

    private OrdersFragment ordersFragment;

    private LogFragment logFragment;

    private int REQUEST_READ_PHONE_STATE = 41;

    public static boolean IS_PAUSE = false;

    public static int mainCountCompanuFromLoad = 0;

    public static boolean checkNewClipboard = false;

    public static int scrollPos;

    public Drawer drawer;

    public Menu MAIN_MENU;

    private final int PERMISSION_REQUEST_CONTACT = 88;

    private final int PERMISSION_REQUEST_ALERT = 24;

    private final int PERMISSION_REQUEST_BOOT_COMPLETED = 74;

    LinearLayout favoritePopup;

    public static boolean IS_CONTACT_LOAD = false;

    public static FragmentTypeEnum selectedType;

    private FrameLayout STATUS_POPUP;

    public ArrayList<Contact> LIST_OF_CONTACTS = new ArrayList<>();

    private LinearLayout clipboardPopup;
    private LinearLayout historyPopup;

    private FrameLayout globalSearchpopup;

    private ClibpboardAdapter clibpboardAdapter;

    private ClibpboardAdapter historyAdapter;

    LinearLayout allContactsBlock;


    FrameLayout loadBar;


    static Boolean OPENED_MENU_POPUP = true;

    private FloatingActionMenu fabMenuContainer;

    public Intent intentServiceNotification;

    private SharedPreferences sharedPreferences;

    private ContactsService contactsService;

    private RecyclerView clipboardRecycler, historyRecycler;

    private ArrayList<Clipboard> clipboard;

    private ArrayList<Clipboard> history;

    private final static String CONTACTS_PREFERENCES = "Call2MePref";

    private final static String CLIPBOARD = "clipboardsData";

    private final static String HISTORY = "historyData";

    private Intent loadServiceIntent;

    private Storage storage;

    private String path;

    public LinearLayout clipboardInfo, historyInfo;

    public FrameLayout popupChangeType, popupChangeTypeHistory;

    public LinearLayout popupEditClip, popupEditHistory;

    LinearLayout filesTypesBlock, barFavoriteFiles, barLinkFiles;

    FrameLayout barHashFiles;

    public int number = 0;
    public int countContactSFromFile = 0;
    public ArrayList<Contact> listcontacts;

    public ProgressBar bar;

    public static String typeUpdate = "none";
    public static ArrayList<String> nameToUpd = new ArrayList<>();
    public static ArrayList<Boolean> nameToUpdTypeContact = new ArrayList<>();
    public static int upd = 0;

    public static ArrayList<Object> listToManyUpdateFile = new ArrayList<>();
    public static String oldNameToUpdate;

    public static ArrayList<Contact> LIST_TO_SAVE_OF_FILE = new ArrayList<>();

    public static JSONArray mainJS;

    public FrameLayout mainProfile;

    public static boolean checkDel = false;


    private Intent serviceUpdateFile = null;


    public static int versionDebug = 80;

    public static boolean processFileClipboard = false;

    public static int secretMode = 2;

    public GoogleSignInAccount accountUser;

    public GoogleSignInAccount accountUserDrive;

    public GoogleSignInClient googleSignInClient;

    public GoogleSignInClient googleDriveSignInClient;

    private final int GOOGLE_SIGNIN_BACKUP_REQUEST_CODE = 103;

    public static String voice_command;

    private void initViewComponents() {
        mainToolBar = findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolBar);
        getSupportActionBar().setTitle(null);
        viewFlipper = findViewById(R.id.barFlipper);
        sortImage = findViewById(R.id.sortElements);
        STATUS_POPUP = findViewById(R.id.statusPopup);
        hashTagBlock = findViewById(R.id.block_hashtag);
        allContactsBlock = findViewById(R.id.allContactsBlock);
        filesTypesBlock = findViewById(R.id.allContactsBlock12);
        barFavoriteFiles = findViewById(R.id.barFavoriteFiles);
        barLinkFiles = findViewById(R.id.block_link);
        barHashFiles = findViewById(R.id.barHash);
        clipboardInfo = findViewById(R.id.popupInfoClipboard);
        popupEditClip = findViewById(R.id.popupEditClip);
        popupChangeType = findViewById(R.id.popupChangeType);

        historyInfo = findViewById(R.id.popupInfoHistory);
        popupEditHistory = findViewById(R.id.popupEditHistory);
        popupChangeTypeHistory = findViewById(R.id.popupChangeTypeHistory);

        globalSearchpopup = findViewById(R.id.popupGlobalSearch);

        findViewById(R.id.countSearchContacts).setVisibility(View.GONE);

        favoritePopup = findViewById(R.id.barFavorite);
        loadBar = findViewById(R.id.loadBar);
        magicBlock = findViewById(R.id.magic_block);
        magicBlock.setOnClickListener((View v) -> {
        });
        selectedType = FragmentTypeEnum.CONTACTS;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        clipboardPopup = findViewById(R.id.clipboardContainer);
        historyPopup = findViewById(R.id.historyContainer);
        fabMenuContainer = findViewById(R.id.fabMenuContainer);

        contactsService = new ContactsService(this.getContentResolver());

    }

    private void getUserData() {
        LIST_OF_CONTACTS = new ArrayList<>();
        if (!firstRun)
            try {

                LIST_OF_CONTACTS = ContactCacheService.getAllContacts(this);
            } catch (Exception e) {
                //   checkFirstRun();
            }
        else {


            if (!loadFromFile)
                LIST_OF_CONTACTS.addAll(contactsService.getContactsPrevWithTypes(false, this));
            else {
                contactsService.loadContactFromFile(false, this);

                EventBus.getDefault().post(new ContactLoadEvent(null));
                //EventBus.getDefault().post(new UpdateFile());
            }
        }
    }

    public void chooseDialog() {
        FileDialog dialog = new OpenFileDialog();
        dialog.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AppTheme);
        dialog.show(getSupportFragmentManager(), OpenFileDialog.class.getName());
    }

    public static boolean clickFrameClipboard = false;

    public static boolean clickFrameClipboard_h = false;

    public int maxH = 0;

    public int maxH_h = 0;

    final float[] bottomY = {0};

    final float[] bottomY_h = {0};

    float px_min = 0;

    float px_min_h = 0;

    public void showGlobal() {

        if (historyPopup.getVisibility() == View.GONE) {

            int px_n = (int) (185 * getApplicationContext().getResources().getDisplayMetrics().density);

            int px_n_2 = (int) (67 * getApplicationContext().getResources().getDisplayMetrics().density);


            int h2 = findViewById(R.id.main_content).getMeasuredHeight() - px_n_2;

            maxH = findViewById(R.id.main_content).getMeasuredHeight() - px_n;


            if (findViewById(R.id.popupContainer).getMeasuredHeight() > maxH) {
                ViewGroup.LayoutParams lp = clipboardPopup.findViewById(R.id.popupContainer).getLayoutParams();
                lp.height = maxH;
                clipboardPopup.findViewById(R.id.popupContainer).setLayoutParams(lp);

                //===

                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) globalSearchpopup.getLayoutParams();
                params.setMargins(0, 0, 0, h2);
                params.height = px_n;
                globalSearchpopup.setLayoutParams(params);
            } else {
                int clips = clipboardPopup.getMeasuredHeight();

                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) globalSearchpopup.getLayoutParams();
                params.setMargins(0, 0, 0, clips);
                params.height = px_n;

                globalSearchpopup.setLayoutParams(params);
            }

        } else {
            int px_n = (int) (185 * getApplicationContext().getResources().getDisplayMetrics().density);

            int px_n_2 = (int) (67 * getApplicationContext().getResources().getDisplayMetrics().density);


            int h2 = findViewById(R.id.main_content).getMeasuredHeight() - px_n_2;

            maxH_h = findViewById(R.id.main_content).getMeasuredHeight() - px_n;


            if (findViewById(R.id.popupContainerHistory).getMeasuredHeight() > maxH_h) {
                ViewGroup.LayoutParams lp = historyPopup.findViewById(R.id.popupContainerHistory).getLayoutParams();
                lp.height = maxH_h;
                historyPopup.findViewById(R.id.popupContainerHistory).setLayoutParams(lp);

                //===

                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) globalSearchpopup.getLayoutParams();
                params.setMargins(0, 0, 0, h2);
                params.height = px_n;
                globalSearchpopup.setLayoutParams(params);
            } else {
                int clips = historyPopup.getMeasuredHeight();

                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) globalSearchpopup.getLayoutParams();
                params.setMargins(0, 0, 0, clips);
                params.height = px_n;

                globalSearchpopup.setLayoutParams(params);
            }
        }


        globalSearchpopup.setVisibility(View.VISIBLE);

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(HideHistoryPopup event) {
        showHistoryPopup();
    }

    private void showHistoryPopup() {
        if (historyPopup.getVisibility() == View.VISIBLE) {
            globalSearchpopup.setVisibility(View.GONE);
            historyAdapter.uncheck();
            historyPopup.setVisibility(View.GONE);

            popupEditHistory.setVisibility(View.GONE);
            historyInfo.setVisibility(View.GONE);
            findViewById(R.id.frameExtractClipboard).setVisibility(View.GONE);

            return;
        }

        if (clipboardPopup.getVisibility() == View.VISIBLE) {
            showClipboardPopup();
        }


        if ((findViewById(R.id.popupProfileCompanyPossitions) != null && findViewById(R.id.popupProfileCompanyPossitions).getVisibility() == View.VISIBLE) && isOpenSoftKey) {
            findViewById(R.id.popupProfileCompanyPossitions).setVisibility(View.GONE);
        }

        ((TextView) findViewById(R.id.createContactHistory)).setTextColor(getResources().getColor(R.color.gray));
        ((TextView) findViewById(R.id.updateContactHistory)).setTextColor(getResources().getColor(R.color.gray));
        ((TextView) findViewById(R.id.mergeHistory)).setTextColor(getResources().getColor(R.color.gray));

        popupEditHistory.setVisibility(View.GONE);
        historyInfo.setVisibility(View.GONE);


        ((TextView) historyPopup.findViewById(R.id.statHistory)).setText(historyAdapter.getCountCLipboards() + " entries, " + historyAdapter.getCountDetectedClipboards() + " detected");
        findViewById(R.id.statHistory).setVisibility(View.VISIBLE);
        findViewById(R.id.removeHistoryData).setVisibility(View.GONE);
        findViewById(R.id.shareHistoryData).setVisibility(View.GONE);
        findViewById(R.id.copyHistoryData).setVisibility(View.GONE);

        findViewById(R.id.extratorContainer).setVisibility(View.GONE);
        findViewById(R.id.framePopupSocial).setVisibility(View.GONE);


        maxH_h = findViewById(R.id.main_content).getMeasuredHeight();

        if (px_min_h == 0)
            px_min_h = (247 - 50) * getApplicationContext().getResources().getDisplayMetrics().density;


        ViewGroup.LayoutParams lp = findViewById(R.id.popupContainerHistory).getLayoutParams();

        float px2 = 0;

        px2 = 247 * getApplicationContext().getResources().getDisplayMetrics().density;

        if (!isOpenSoftKey && lp.height < px2) {
            lp.height = (int) px2;
            historyPopup.findViewById(R.id.popupContainerHistory).setLayoutParams(lp);

        }


        findViewById(R.id.popupContainerHistory).getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int[] location = new int[2];
                findViewById(R.id.popupContainerHistory).getLocationOnScreen(location);

                bottomY_h[0] = location[1];
                bottomY_h[0] += findViewById(R.id.popupContainerHistory).getMeasuredHeight();

                historyPopup.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });


        int[] location = new int[2];

        final float[] top = {0};


        historyPopup.findViewById(R.id.frameCloseHistory).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN) {
                    findViewById(R.id.popupContainerHistory).getLocationOnScreen(location);
                    top[0] = location[1] - event.getRawY();
                    clickFrameClipboard_h = true;
                    if (globalSearchpopup.getVisibility() == View.VISIBLE) showGlobal();
                }

                if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
                    if (globalSearchpopup.getVisibility() == View.VISIBLE) showGlobal();

                    if (clickFrameClipboard_h) {

                        return false;

                    } else {
                        clickFrameClipboard_h = false;
                        return true;
                    }
                }


                if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_MOVE && (findViewById(R.id.popupContainerHistory).getMeasuredHeight() < maxH_h || bottomY_h[0] - event.getRawY() < maxH_h) && ((findViewById(R.id.popupContainerHistory).getMeasuredHeight() > px_min_h
                        || bottomY_h[0] - event.getRawY() - top[0] > px_min_h))) {
                    //if (lastY != Float.MIN_VALUE) {


                    final float dist = event.getRawY();
                    final int next = (int) ((int) (bottomY_h[0] - (int) dist) - top[0]);
                    ViewGroup.LayoutParams lp = historyPopup.findViewById(R.id.popupContainerHistory).getLayoutParams();

                    if ((dist + 1) - lp.height < 2 || (dist - 1) - lp.height < 2) {
                        clickFrameClipboard_h = true;

                    }


                    lp.height = next;
                    if (lp.height > maxH_h) {

                        lp.height = maxH_h;
                        historyPopup.findViewById(R.id.popupContainerHistory).setLayoutParams(lp);

                        if (globalSearchpopup.getVisibility() == View.VISIBLE) showGlobal();

                        return true;
                    }

                    if (lp.height < px_min_h) {

                        lp.height = (int) px_min;
                        historyPopup.findViewById(R.id.popupContainerHistory).setLayoutParams(lp);

                        if (globalSearchpopup.getVisibility() == View.VISIBLE) showGlobal();


                        return true;
                    }

                    historyPopup.findViewById(R.id.popupContainerHistory).setLayoutParams(lp);

                    if (globalSearchpopup.getVisibility() == View.VISIBLE) showGlobal();


                    return true;
                } else {
                    if (globalSearchpopup.getVisibility() == View.VISIBLE) showGlobal();
                    return false;
                }

            }
        });


        historyPopup.findViewById(R.id.frameCloseHistory).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                historyInfo.setVisibility(View.GONE);
                popupEditHistory.setVisibility(View.GONE);
                popupChangeTypeHistory.setVisibility(View.GONE);
                historyAdapter.updateCount();
            }
        });

        historyPopup.findViewById(R.id.copyHistoryData).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Clipboard> listClip = historyAdapter.getClipbaordList();
                String exportData = "";
                for (Clipboard clip : listClip) {
                    if (clip.getListClipboards() == null || clip.getListClipboards().isEmpty())
                        exportData += clip.getValueCopy() + " " + "\n";
                }
                contactsFragment.copyToClipboard(MainActivity.this, exportData);
                historyAdapter.clearCheck();
                Toast.makeText(MainActivity.this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });

        historyPopup.findViewById(R.id.removeHistoryData).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (historyAdapter != null) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            contactsFragment.getContext());
                    String t = "Do you want to delete entries?";
                    if (historyAdapter.getClipbaordList().size() == 1)
                        t = "Do you want to delete entry?";
                    alertDialogBuilder.setTitle(t);
                    alertDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("Yes", (dialog, id) -> {


                                ArrayList<Clipboard> listOpen = historyAdapter.getClipOpen();
                                ArrayList<Clipboard> listClip = historyAdapter.getClipbaordList();
                                historyAdapter.clearCheck();

                                for (int j = 0; j < listOpen.size(); j++) {
                                    for (int i = 0; i < listClip.size(); i++) {
                                        if (listOpen.get(j).getListClipboards().contains(listClip.get(i))) {

                                            listOpen.get(j).getListClipboards().remove(listClip.get(i));

                                            if (listOpen.get(j).getListClipboards().isEmpty()) {

                                                for (int q = 0; q < history.size(); q++) {
                                                    if (history.get(q).getValueCopy().equals(listOpen.get(j).getValueCopy()))
                                                        history.remove(q);
                                                }

                                                historyAdapter.getClipOpen().remove(j);
                                                //listOpen.remove(j);
                                                if (listOpen.size() == 0) {
                                                    break;

                                                }
                                            } else {
                                                for (Clipboard c : history) {
                                                    if (c.getValueCopy().equals(listOpen.get(j).getValueCopy()))
                                                        c.setListClipboards(listOpen.get(j).getListClipboards());
                                                }
                                            }
                                        }
                                    }
                                }

                                for (int i = 0; i < listClip.size(); i++) {
                                    if (listOpen.contains(listClip.get(i))) {
                                        for (int j = 0; j < listClip.get(i).getListClipboards().size(); j++) {
                                            history.remove(listClip.get(i).getListClipboards().get(j));
                                        }
                                    }
                                }

                                for (int i = 0; i < history.size(); i++) {

                                    if (listClip.contains(history.get(i))) {
                                        history.remove(i);
                                        i--;
                                    }
                                }

                                if (history.size() == 0) {
                                    historyPopup.findViewById(R.id.removeHistoryData).setVisibility(View.GONE);
                                    historyPopup.findViewById(R.id.shareHistoryData).setVisibility(View.GONE);
                                    historyPopup.findViewById(R.id.copyHistoryData).setVisibility(View.GONE);
                                    historyPopup.findViewById(R.id.statHistory).setVisibility(View.VISIBLE);
                                    ((TextView) historyPopup.findViewById(R.id.statHistory)).setText(historyAdapter.getCountCLipboards() + " entries, " + historyAdapter.getCountDetectedClipboards() + " detected");
                                }


                                Toast.makeText(contactsFragment.getContext(), "Delete success", Toast.LENGTH_SHORT).show();
                                findViewById(R.id.textTopHistory).setVisibility(View.INVISIBLE);
                                historyAdapter.updateClipboards(history);
                                EventBus.getDefault().post(new SaveHistory());


                            })
                            .setNegativeButton("No", (dialog, id) -> dialog.cancel());

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            }
        });


        historyPopup.findViewById(R.id.shareHistoryData).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<Clipboard> listClip = historyAdapter.getClipbaordList();
                String exportData = "";
                for (Clipboard clip : listClip) {
                    exportData += clip.getValueCopy() + "\n\n";
                }

                exportData += "\n\n";
                exportData += "Data shared via http://Extime.pro\n";


                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, exportData);
                startActivity(Intent.createChooser(shareIntent, "Поделиться контактом"));

            }
        });

        popupEditHistory.findViewById(R.id.cancelEditField).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupEditHistory.setVisibility(View.GONE);
                historyInfo.setVisibility(View.VISIBLE);

            }
        });

        historyPopup.findViewById(R.id.createContactHistory).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createContact(MagicType.HISTORY);
            }
        });

        historyPopup.findViewById(R.id.updateContactHistory).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateContact(MagicType.HISTORY);
            }
        });


        historyPopup.setVisibility(View.VISIBLE);
    }

    private void showClipboardPopup() {


        contactsFragment.listAdapter = new ArrayList<>();

        if (clipboardPopup.getVisibility() == View.VISIBLE) {
            globalSearchpopup.setVisibility(View.GONE);
            clipboardPopup.setVisibility(View.GONE);
            clibpboardAdapter.uncheck();
            fabMenuContainer.setVisibility(View.VISIBLE);
            popupEditClip.setVisibility(View.GONE);
            clipboardInfo.setVisibility(View.GONE);
            findViewById(R.id.frameExtractClipboard).setVisibility(View.GONE);
            //EventBus.getDefault().post(new SaveClipboard());
            return;
        }

        if (historyPopup.getVisibility() == View.VISIBLE) {
            showHistoryPopup();
        }


        if ((findViewById(R.id.popupProfileCompanyPossitions) != null && findViewById(R.id.popupProfileCompanyPossitions).getVisibility() == View.VISIBLE) && isOpenSoftKey) {
            findViewById(R.id.popupProfileCompanyPossitions).setVisibility(View.GONE);
        }

        /*if ((clipboardPopup != null && clipboardPopup.getVisibility() == View.GONE) &&  (((contactsFragment.popupHelpCompanyposition != null && contactsFragment.popupHelpCompanyposition.getVisibility() == View.VISIBLE) || (findViewById(R.id.popupEditMain).getVisibility() == View.VISIBLE)) || (ProfileFragment.profile || ContactsFragment.createContact))) {
            checkKeyboardOpen();
            //contactsFragment.popupHelpCompanyposition.setVisibility(View.GONE);
        }*/

        ((TextView) findViewById(R.id.createContactClipboard)).setTextColor(getResources().getColor(R.color.gray));
        ((TextView) findViewById(R.id.updateContactClipboard)).setTextColor(getResources().getColor(R.color.gray));
        ((TextView) findViewById(R.id.mergeClipboards)).setTextColor(getResources().getColor(R.color.gray));

        popupEditClip.setVisibility(View.GONE);
        clipboardInfo.setVisibility(View.GONE);


        ((TextView) clipboardPopup.findViewById(R.id.statClipboard)).setText(clibpboardAdapter.getCountCLipboards() + " entries, " + clibpboardAdapter.getCountDetectedClipboards() + " detected");
        findViewById(R.id.statClipboard).setVisibility(View.VISIBLE);
        findViewById(R.id.removeClipData).setVisibility(View.GONE);
        findViewById(R.id.shareClipData).setVisibility(View.GONE);
        findViewById(R.id.copyClipData).setVisibility(View.GONE);
        findViewById(R.id.extratorContainer).setVisibility(View.GONE);
        findViewById(R.id.framePopupSocial).setVisibility(View.GONE);

       /* if (((PopupsInter) selectedFragment) != null)
            ((PopupsInter) selectedFragment).closeOtherPopup();*/


        //int cx = (clipboardPopup.getLeft() + clipboardPopup.getRight()) / 2;
        //int cy = (clipboardPopup.getTop() + clipboardPopup.getBottom()) / 2;

        // get the final radius for the clipping circle
        //int dx = Math.max(cx, clipboardPopup.getWidth() - cx);
        //int dy = Math.max(cy, clipboardPopup.getHeight() - cy);
        //float finalRadius = (float) Math.hypot(dx, dy);

       /* SupportAnimator animator =
                ViewAnimationUtils.createCircularReveal(clipboardPopup, cx, cy, 0, finalRadius);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(500);
        animator.start();*/
        LinearLayoutManager myLayoutManager = (LinearLayoutManager) clipboardRecycler.getLayoutManager();
        scrollPos = myLayoutManager.findFirstVisibleItemPosition();


        if (scrollPos > 0 && checkNewClipboard) {
            clipboardPopup.findViewById(R.id.scrollToCopieed).setVisibility(View.VISIBLE);
            findViewById(R.id.circle_new_clip).setVisibility(View.VISIBLE);
        } else {
            clipboardPopup.findViewById(R.id.scrollToCopieed).setVisibility(View.GONE);
            findViewById(R.id.circle_new_clip).setVisibility(View.GONE);
            checkNewClipboard = false;
            if (FabNotificationService.countNewCLips > 0) {
                FabNotificationService.countNewCLips = 0;
                EventBus.getDefault().post(new SetDefaultPush());
            }
        }


        if (scrollPos > 0) {

            clipboardRecycler.scrollToPosition(scrollPos);
            scrollPos = 0;
        }

        clipboardPopup.setVisibility(View.VISIBLE);
        //clibpboardAdapter.notifyDataSetChanged();

        fabMenuContainer.setVisibility(View.GONE);


        maxH = findViewById(R.id.main_content).getMeasuredHeight();

        if (px_min == 0)
            px_min = (247 - 50) * getApplicationContext().getResources().getDisplayMetrics().density;


        ViewGroup.LayoutParams lp = findViewById(R.id.popupContainer).getLayoutParams();

        float px2 = 0;

       /* if(clipboardPopup.findViewById(R.id.textTopClipboard).getVisibility() == View.GONE)
            px2 = 240 * getApplicationContext().getResources().getDisplayMetrics().density;
        else*/
        px2 = 247 * getApplicationContext().getResources().getDisplayMetrics().density;

        if (!isOpenSoftKey && lp.height < px2) {
            lp.height = (int) px2;
            clipboardPopup.findViewById(R.id.popupContainer).setLayoutParams(lp);

        }


        findViewById(R.id.popupContainer).getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int[] location = new int[2];
                findViewById(R.id.popupContainer).getLocationOnScreen(location);


                bottomY[0] = location[1];
                bottomY[0] += findViewById(R.id.popupContainer).getMeasuredHeight();

                clipboardPopup.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });


        int[] location = new int[2];

        final float[] top = {0};

        final float[] top2 = {0};


        clipboardPopup.findViewById(R.id.frameCloseClip).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                //float lastY = Float.MIN_VALUE;


                //if(bottomY[0] == 0) bottomY[0] = location[1];


                //if(findViewById(R.id.popupContainer).getMeasuredHeight() >= maxH) return true;


                if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN) {
                    findViewById(R.id.popupContainer).getLocationOnScreen(location);
                    top[0] = location[1] - event.getRawY();
                    clickFrameClipboard = true;
                    if (globalSearchpopup.getVisibility() == View.VISIBLE) showGlobal();
                }

                if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
                    if (globalSearchpopup.getVisibility() == View.VISIBLE) showGlobal();

                    if (clickFrameClipboard) {

                        return false;

                    } else {
                        clickFrameClipboard = false;
                        return true;
                    }
                }


                if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_MOVE && (findViewById(R.id.popupContainer).getMeasuredHeight() < maxH || bottomY[0] - event.getRawY() < maxH) && ((findViewById(R.id.popupContainer).getMeasuredHeight() > px_min || bottomY[0] - event.getRawY() - top[0] > px_min))) {
                    //if (lastY != Float.MIN_VALUE) {


                    final float dist = event.getRawY();
                    final int next = (int) ((int) (bottomY[0] - (int) dist) - top[0]);
                    ViewGroup.LayoutParams lp = clipboardPopup.findViewById(R.id.popupContainer).getLayoutParams();

                    if ((dist + 1) - lp.height < 2 || (dist - 1) - lp.height < 2) {
                        clickFrameClipboard = true;

                    }


                    lp.height = next;
                    if (lp.height > maxH) {

                        lp.height = maxH;
                        clipboardPopup.findViewById(R.id.popupContainer).setLayoutParams(lp);

                        if (globalSearchpopup.getVisibility() == View.VISIBLE) showGlobal();

                        return true;
                    }

                    if (lp.height < px_min) {

                        lp.height = (int) px_min;
                        clipboardPopup.findViewById(R.id.popupContainer).setLayoutParams(lp);

                        if (globalSearchpopup.getVisibility() == View.VISIBLE) showGlobal();


                        return true;
                    }

                    clipboardPopup.findViewById(R.id.popupContainer).setLayoutParams(lp);

                    if (globalSearchpopup.getVisibility() == View.VISIBLE) showGlobal();

                    //clipboardPopup.invalidate();
                    //clipboardPopup.requestLayout();


                    //clipboardPopup.findViewById(R.id.bottomFrameClipboard).invalidate();
                    //clipboardPopup.findViewById(R.id.bottomFrameClipboard).requestLayout();
                    //}
                    //lastY = event.getRawY();
                    return true;
                } else {
                    if (globalSearchpopup.getVisibility() == View.VISIBLE) showGlobal();
                    return false;
                }

                /*if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
                    lastY = Float.MIN_VALUE;
                }*/

                //return true;


            }
        });

        clipboardInfo.findViewById(R.id.frame_info_touch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        clipboardInfo.findViewById(R.id.frame_info_touch).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {


                if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN) {
                    clipboardInfo.findViewById(R.id.popupInfoClipboard).getLocationOnScreen(location);
                    top[0] = location[1] - event.getRawY();

                    top2[0] = (location[1] + clipboardInfo.findViewById(R.id.popupInfoClipboard).getMeasuredHeight()) - event.getRawY();

                    clickFrameClipboard = true;
                    if (globalSearchpopup.getVisibility() == View.VISIBLE) showGlobal();
                }

                if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
                    if (globalSearchpopup.getVisibility() == View.VISIBLE) showGlobal();

                    if (clickFrameClipboard) {

                        return false;

                    } else {
                        clickFrameClipboard = false;
                        return true;
                    }
                }


                if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_MOVE) {

                    clipboardInfo.findViewById(R.id.popupInfoClipboard).getLocationOnScreen(location);


                    float bottomY = location[1];
                    bottomY += clipboardInfo.findViewById(R.id.popupInfoClipboard).getMeasuredHeight();

                    float top = location[1] - event.getRawY();

                    float t = bottomY - event.getRawY();

                    final float dist = event.getRawY();
                    final int next = (int) ((int) (bottomY - (int) dist) - top);
                    ViewGroup.LayoutParams lp = clipboardInfo.findViewById(R.id.popupInfoClipboard).getLayoutParams();

                    if ((dist + 1) - lp.height < 2 || (dist - 1) - lp.height < 2) {
                        clickFrameClipboard = true;

                    }


                    lp.height = (int) ((event.getRawY() + top2[0]) - location[1]);


                    if (lp.height < 155 * getApplicationContext().getResources().getDisplayMetrics().density) {
                        lp.height = (int) (155 * getApplicationContext().getResources().getDisplayMetrics().density);
                        clipboardPopup.findViewById(R.id.popupInfoClipboard).setLayoutParams(lp);
                        return true;
                    }


                    if (lp.height > clipboardPopup.findViewById(R.id.popupContainer).getMeasuredHeight() - 26 * getApplicationContext().getResources().getDisplayMetrics().density) {
                        lp.height = (int) (clipboardPopup.findViewById(R.id.popupContainer).getMeasuredHeight() - 26 * getApplicationContext().getResources().getDisplayMetrics().density);
                        clipboardPopup.findViewById(R.id.popupInfoClipboard).setLayoutParams(lp);
                        return true;
                    }


                    clipboardPopup.findViewById(R.id.popupInfoClipboard).setLayoutParams(lp);
                    //clipboardPopup.findViewById(R.id.valueUpdClip).requestLayout();

                    if (globalSearchpopup.getVisibility() == View.VISIBLE) showGlobal();


                    return true;
                } else {
                    if (globalSearchpopup.getVisibility() == View.VISIBLE) showGlobal();
                    return false;
                }


            }
        });

        popupEditClip.findViewById(R.id.frame_edit_touch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        popupEditClip.findViewById(R.id.frame_edit_touch).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {


                if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN) {
                    popupEditClip.findViewById(R.id.popupEditClip).getLocationOnScreen(location);
                    //top[0] = location[1] - event.getRawY();

                    top2[0] = (location[1] + popupEditClip.findViewById(R.id.popupEditClip).getMeasuredHeight()) - event.getRawY();

                    clickFrameClipboard = true;
                    if (globalSearchpopup.getVisibility() == View.VISIBLE) showGlobal();
                }

                if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
                    if (globalSearchpopup.getVisibility() == View.VISIBLE) showGlobal();

                    if (clickFrameClipboard) {

                        return false;

                    } else {
                        clickFrameClipboard = false;
                        return true;
                    }
                }


                if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_MOVE) {

                    popupEditClip.findViewById(R.id.popupEditClip).getLocationOnScreen(location);

                    final float dist = event.getRawY();
                    ViewGroup.LayoutParams lp = popupEditClip.findViewById(R.id.popupEditClip).getLayoutParams();

                    if ((dist + 1) - lp.height < 2 || (dist - 1) - lp.height < 2) {
                        clickFrameClipboard = true;

                    }


                    lp.height = (int) ((event.getRawY() + top2[0]) - location[1]);


                    if (lp.height < 155 * getApplicationContext().getResources().getDisplayMetrics().density) {
                        lp.height = (int) (155 * getApplicationContext().getResources().getDisplayMetrics().density);
                        clipboardPopup.findViewById(R.id.popupEditClip).setLayoutParams(lp);
                        return true;
                    }


                    if (lp.height > clipboardPopup.findViewById(R.id.popupContainer).getMeasuredHeight() - 26 * getApplicationContext().getResources().getDisplayMetrics().density) {
                        lp.height = (int) (clipboardPopup.findViewById(R.id.popupContainer).getMeasuredHeight() - 26 * getApplicationContext().getResources().getDisplayMetrics().density);
                        clipboardPopup.findViewById(R.id.popupEditClip).setLayoutParams(lp);
                        return true;
                    }


                    clipboardPopup.findViewById(R.id.popupEditClip).setLayoutParams(lp);
                    //clipboardPopup.findViewById(R.id.dataToEditClip).requestLayout();

                    if (globalSearchpopup.getVisibility() == View.VISIBLE) showGlobal();


                    return true;
                } else {
                    if (globalSearchpopup.getVisibility() == View.VISIBLE) showGlobal();
                    return false;
                }


            }
        });

        clipboardRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) clipboardRecycler.getLayoutManager();

                contactsFragment.firstItem = layoutManager.findFirstVisibleItemPosition();
                contactsFragment.lastItem = layoutManager.findLastVisibleItemPosition();

            }
        });


    }

    private void setListeners() {

        TimeLineFragment.mode = 1;
        TimeLineFragment.typeCard = TypeCardTimeline.EXTRA;

        findViewById(R.id.clipboardPopup).setOnClickListener(v -> {
            try {
                showClipboardPopup();
            } catch (Exception e) {

            }
        });

        findViewById(R.id.search_magic_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHistoryPopup();
            }
        });

        clipboardPopup.findViewById(R.id.scrollToCopieed).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clipboardRecycler != null) {
                    try {
                        clipboardRecycler.scrollToPosition(0);
                        clipboardPopup.findViewById(R.id.scrollToCopieed).setVisibility(View.GONE);
                        findViewById(R.id.circle_new_clip).setVisibility(View.GONE);
                        checkNewClipboard = false;
                        if (FabNotificationService.countNewCLips > 0) {
                            FabNotificationService.countNewCLips = 0;
                            EventBus.getDefault().post(new SetDefaultPush());
                        }
                    } catch (Exception e) {

                    }
                }

            }
        });


        sortImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ((ContactBarInter) selectedFragment).sortList();
                } catch (ClassCastException e) {
                    e.printStackTrace();
                }
            }
        });

        hashTagBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ((ContactBarInter) selectedFragment).showHashTagPopUp();
                } catch (ClassCastException e) {
                    e.printStackTrace();
                }
            }
        });

        allContactsBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ((ContactBarInter) selectedFragment).showAllContactsPopup();
                } catch (ClassCastException e) {
                    e.printStackTrace();
                }
            }
        });

        favoritePopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ((ContactBarInter) selectedFragment).showFavoriteContactsPopup();
                } catch (ClassCastException e) {
                    e.printStackTrace();
                }
            }
        });

        filesTypesBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ((FileBarInter) selectedFragment).showAllFilesPopup();
                } catch (ClassCastException e) {
                    e.printStackTrace();
                }
            }
        });

        barFavoriteFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ((FileBarInter) selectedFragment).showAllCloud();
                } catch (ClassCastException e) {
                    e.printStackTrace();
                }
            }
        });

        barLinkFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ((FileBarInter) selectedFragment).showAllLink();
                } catch (ClassCastException e) {
                    e.printStackTrace();
                }
            }
        });

        barHashFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ((FileBarInter) selectedFragment).showAllHash();
                } catch (ClassCastException e) {
                    e.printStackTrace();
                }
            }
        });


        findViewById(R.id.places_block).setOnClickListener(v -> {

            if (ProfileFragment.profile && ProfileFragment.nowEdit) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        this);
                alertDialogBuilder.setTitle("Do you want to save changes?");
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Yes", (dialog, id) -> {
                            EventBus.getDefault().post(new SaveChangesProfile());

                            ProfileFragment.nowEdit = false;
                            findViewById(R.id.places_block).callOnClick();
                            dialog.cancel();

                        })
                        .setNegativeButton("No", (dialog, id) -> {
                            ProfileFragment.nowEdit = false;
                            findViewById(R.id.places_block).callOnClick();
                            dialog.cancel();
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            } else if (ContactsFragment.createContact) {
                if (CreateFragment.createdContact.getName() != null && !CreateFragment.createdContact.getName().equals("")) {

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            this);
                    alertDialogBuilder.setTitle("Do you want to create new contact?");
                    alertDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("Yes", (dialog, id) -> {
                                EventBus.getDefault().post(new SaveCreate());

                                ContactsFragment.createContact = false;
                                findViewById(R.id.places_block).callOnClick();
                                dialog.cancel();

                            })
                            .setNegativeButton("No", (dialog, id) -> {
                                ContactsFragment.createContact = false;
                                findViewById(R.id.places_block).callOnClick();
                                dialog.cancel();
                            });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                } else {
                    ContactsFragment.createContact = false;
                    findViewById(R.id.places_block).callOnClick();
                }
            } else {
                selectedType = FragmentTypeEnum.PLACES;
                applySelection();
            }
            ProfileFragment.nowEdit = false;
        });

        findViewById(R.id.contacts_block).setOnClickListener(v -> {

            /*android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
            List<Fragment> listF = fm.getFragments();
            if(listF.size() > 1){
                getSupportFragmentManager().beginTransaction().remove(listF.get(listF.size() - 1)).commit()
            }*/

            try {
                contactsFragment.contactAdapter.setSelectContactID("");
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            if (ProfileFragment.profile && ProfileFragment.nowEdit) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        this);
                alertDialogBuilder.setTitle("Do you want to save changes?");
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Yes", (dialog, id) -> {
                            EventBus.getDefault().post(new SaveChangesProfile());

                            ProfileFragment.nowEdit = false;
                            findViewById(R.id.contacts_block).callOnClick();
                            dialog.cancel();

                        })
                        .setNegativeButton("No", (dialog, id) -> {
                            ProfileFragment.nowEdit = false;
                            findViewById(R.id.contacts_block).callOnClick();
                            dialog.cancel();
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            } else if (ContactsFragment.createContact) {
                if (CreateFragment.createdContact.getName() != null && !CreateFragment.createdContact.getName().equals("")) {

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            this);
                    alertDialogBuilder.setTitle("Do you want to create new contact?");
                    alertDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("Yes", (dialog, id) -> {
                                EventBus.getDefault().post(new SaveCreate());

                                ContactsFragment.createContact = false;
                                findViewById(R.id.contacts_block).callOnClick();
                                dialog.cancel();

                            })
                            .setNegativeButton("No", (dialog, id) -> {
                                ContactsFragment.createContact = false;
                                findViewById(R.id.contacts_block).callOnClick();
                                dialog.cancel();
                            });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                } else {
                    ContactsFragment.createContact = false;
                    findViewById(R.id.contacts_block).callOnClick();
                }
            } else {

                if (getSupportFragmentManager().getFragments() != null && getSupportFragmentManager().getFragments().size() > 1) {
                    for (int i = 0; i <= getSupportFragmentManager().getFragments().size() - 1; i++) {
                        Fragment mFragment = getSupportFragmentManager().getFragments().get(i);

                        if (mFragment != null && !mFragment.getClass().equals(ContactsFragment.class)) {

                            getSupportFragmentManager().beginTransaction().remove(mFragment).commit();
                        }
                    }
                }

                // }


                selectedType = FragmentTypeEnum.CONTACTS;
                applySelection();

            }
            ProfileFragment.nowEdit = false;
        });

        findViewById(R.id.schedule_block).setOnClickListener(v -> {

            if (ProfileFragment.profile && ProfileFragment.nowEdit) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        this);
                alertDialogBuilder.setTitle("Do you want to save changes?");
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Yes", (dialog, id) -> {
                            EventBus.getDefault().post(new SaveChangesProfile());

                            selectedType = FragmentTypeEnum.SCHEDULE;
                            findViewById(R.id.countSearchContacts).setVisibility(View.GONE);
                            applySelection();

                        })
                        .setNegativeButton("No", (dialog, id) -> {
                            ProfileFragment.nowEdit = false;
                            findViewById(R.id.schedule_block).callOnClick();
                            dialog.cancel();
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            } else if (ContactsFragment.createContact) {
                if (CreateFragment.createdContact.getName() != null && !CreateFragment.createdContact.getName().equals("")) {

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            this);
                    alertDialogBuilder.setTitle("Do you want to create new contact?");
                    alertDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("Yes", (dialog, id) -> {
                                EventBus.getDefault().post(new SaveCreate());

                                ContactsFragment.createContact = false;
                                findViewById(R.id.schedule_block).callOnClick();
                                dialog.cancel();

                            })
                            .setNegativeButton("No", (dialog, id) -> {
                                ContactsFragment.createContact = false;
                                findViewById(R.id.schedule_block).callOnClick();
                                dialog.cancel();
                            });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                } else {
                    ContactsFragment.createContact = false;
                    findViewById(R.id.schedule_block).callOnClick();
                }
            } else {

                selectedType = FragmentTypeEnum.SCHEDULE;
                findViewById(R.id.countSearchContacts).setVisibility(View.GONE);
                applySelection();

            }
            ProfileFragment.nowEdit = false;
        });

        findViewById(R.id.log_block).setOnClickListener(v -> {

            if (ProfileFragment.profile && ProfileFragment.nowEdit) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        this);
                alertDialogBuilder.setTitle("Do you want to save changes?");
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Yes", (dialog, id) -> {

                            EventBus.getDefault().post(new SaveChangesProfile());

                            selectedType = FragmentTypeEnum.LOG;
                            findViewById(R.id.countSearchContacts).setVisibility(View.GONE);
                            applySelection();

                        })
                        .setNegativeButton("No", (dialog, id) -> {
                            ProfileFragment.nowEdit = false;
                            findViewById(R.id.log_block).callOnClick();
                            dialog.cancel();
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            } else if (ContactsFragment.createContact) {
                if (CreateFragment.createdContact.getName() != null && !CreateFragment.createdContact.getName().equals("")) {

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            this);
                    alertDialogBuilder.setTitle("Do you want to create new contact?");
                    alertDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("Yes", (dialog, id) -> {
                                EventBus.getDefault().post(new SaveCreate());

                                ContactsFragment.createContact = false;
                                findViewById(R.id.log_block).callOnClick();
                                dialog.cancel();

                            })
                            .setNegativeButton("No", (dialog, id) -> {
                                ContactsFragment.createContact = false;
                                findViewById(R.id.log_block).callOnClick();
                                dialog.cancel();
                            });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                } else {
                    ContactsFragment.createContact = false;
                    findViewById(R.id.log_block).callOnClick();
                }
            } else {

                selectedType = FragmentTypeEnum.LOG;
                findViewById(R.id.countSearchContacts).setVisibility(View.GONE);
                applySelection();
            }

            ProfileFragment.nowEdit = false;
        });

        findViewById(R.id.emails_block).setOnClickListener(v -> {

            if (ProfileFragment.profile && ProfileFragment.nowEdit) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        this);
                alertDialogBuilder.setTitle("Do you want to save changes?");
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Yes", (dialog, id) -> {
                            EventBus.getDefault().post(new SaveChangesProfile());

                            selectedType = FragmentTypeEnum.EMAILS;
                            findViewById(R.id.countSearchContacts).setVisibility(View.GONE);
                            applySelection();

                        })
                        .setNegativeButton("No", (dialog, id) -> {
                            ProfileFragment.nowEdit = false;
                            findViewById(R.id.emails_block).callOnClick();
                            dialog.cancel();
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            } else if (ContactsFragment.createContact) {
                if (CreateFragment.createdContact.getName() != null && !CreateFragment.createdContact.getName().equals("")) {

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            this);
                    alertDialogBuilder.setTitle("Do you want to create new contact?");
                    alertDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("Yes", (dialog, id) -> {
                                EventBus.getDefault().post(new SaveCreate());

                                ContactsFragment.createContact = false;
                                findViewById(R.id.emails_block).callOnClick();
                                dialog.cancel();

                            })
                            .setNegativeButton("No", (dialog, id) -> {
                                ContactsFragment.createContact = false;
                                findViewById(R.id.emails_block).callOnClick();
                                dialog.cancel();
                            });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                } else {
                    ContactsFragment.createContact = false;
                    findViewById(R.id.emails_block).callOnClick();
                }
            } else {
                selectedType = FragmentTypeEnum.EMAILS;
                findViewById(R.id.countSearchContacts).setVisibility(View.GONE);
                applySelection();

            }

            ProfileFragment.nowEdit = false;
        });

        findViewById(R.id.timeline_block).setOnClickListener(v -> {

            if (ProfileFragment.profile && ProfileFragment.nowEdit) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        this);
                alertDialogBuilder.setTitle("Do you want to save changes?");
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Yes", (dialog, id) -> {
                            EventBus.getDefault().post(new SaveChangesProfile());

                            selectedType = FragmentTypeEnum.TIMELINE;
                            findViewById(R.id.countSearchContacts).setVisibility(View.GONE);
                            applySelection();

                        })
                        .setNegativeButton("No", (dialog, id) -> {
                            ProfileFragment.nowEdit = false;
                            findViewById(R.id.timeline_block).callOnClick();
                            dialog.cancel();
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            } else if (ContactsFragment.createContact) {
                if (CreateFragment.createdContact.getName() != null && !CreateFragment.createdContact.getName().equals("")) {

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            this);
                    alertDialogBuilder.setTitle("Do you want to create new contact?");
                    alertDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("Yes", (dialog, id) -> {
                                EventBus.getDefault().post(new SaveCreate());

                                ContactsFragment.createContact = false;
                                findViewById(R.id.timeline_block).callOnClick();
                                dialog.cancel();

                            })
                            .setNegativeButton("No", (dialog, id) -> {
                                ContactsFragment.createContact = false;
                                findViewById(R.id.timeline_block).callOnClick();
                                dialog.cancel();
                            });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                } else {
                    ContactsFragment.createContact = false;
                    findViewById(R.id.timeline_block).callOnClick();
                }
            } else {

                selectedType = FragmentTypeEnum.TIMELINE;
                findViewById(R.id.countSearchContacts).setVisibility(View.GONE);
                applySelection();
            }
            ProfileFragment.nowEdit = false;
        });

        findViewById(R.id.files_block).setOnClickListener(v -> {

            if (ProfileFragment.profile && ProfileFragment.nowEdit) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        this);
                alertDialogBuilder.setTitle("Do you want to save changes?");
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Yes", (dialog, id) -> {
                            EventBus.getDefault().post(new SaveChangesProfile());

                            selectedType = FragmentTypeEnum.FILES;
                            findViewById(R.id.countSearchContacts).setVisibility(View.GONE);
                            applySelection();

                        })
                        .setNegativeButton("No", (dialog, id) -> {
                            ProfileFragment.nowEdit = false;
                            findViewById(R.id.files_block).callOnClick();
                            dialog.cancel();
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            } else if (ContactsFragment.createContact) {
                if (CreateFragment.createdContact.getName() != null && !CreateFragment.createdContact.getName().equals("")) {

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            this);
                    alertDialogBuilder.setTitle("Do you want to create new contact?");
                    alertDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("Yes", (dialog, id) -> {
                                EventBus.getDefault().post(new SaveCreate());

                                ContactsFragment.createContact = false;
                                findViewById(R.id.files_block).callOnClick();
                                dialog.cancel();

                            })
                            .setNegativeButton("No", (dialog, id) -> {
                                ContactsFragment.createContact = false;
                                findViewById(R.id.files_block).callOnClick();
                                dialog.cancel();
                            });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                } else {
                    ContactsFragment.createContact = false;
                    findViewById(R.id.files_block).callOnClick();
                }
            } else {
                selectedType = FragmentTypeEnum.FILES;
                findViewById(R.id.countSearchContacts).setVisibility(View.GONE);
                applySelection();
            }
            ProfileFragment.nowEdit = false;
        });

        findViewById(R.id.orders_block).setOnClickListener(v -> {

            if (ProfileFragment.profile && ProfileFragment.nowEdit) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        this);
                alertDialogBuilder.setTitle("Do you want to save changes?");
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Yes", (dialog, id) -> {
                            EventBus.getDefault().post(new SaveChangesProfile());

                            selectedType = FragmentTypeEnum.ORDERS;
                            findViewById(R.id.countSearchContacts).setVisibility(View.GONE);
                            applySelection();

                        })
                        .setNegativeButton("No", (dialog, id) -> {
                            ProfileFragment.nowEdit = false;
                            findViewById(R.id.orders_block).callOnClick();
                            dialog.cancel();
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            } else if (ContactsFragment.createContact) {
                if (CreateFragment.createdContact.getName() != null && !CreateFragment.createdContact.getName().equals("")) {

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            this);
                    alertDialogBuilder.setTitle("Do you want to create new contact?");
                    alertDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("Yes", (dialog, id) -> {
                                EventBus.getDefault().post(new SaveCreate());

                                ContactsFragment.createContact = false;
                                findViewById(R.id.orders_block).callOnClick();
                                dialog.cancel();

                            })
                            .setNegativeButton("No", (dialog, id) -> {
                                ContactsFragment.createContact = false;
                                findViewById(R.id.orders_block).callOnClick();
                                dialog.cancel();
                            });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                } else {
                    ContactsFragment.createContact = false;
                    findViewById(R.id.orders_block).callOnClick();
                }
            } else {
                selectedType = FragmentTypeEnum.ORDERS;
                findViewById(R.id.countSearchContacts).setVisibility(View.GONE);
                applySelection();
            }
            ProfileFragment.nowEdit = false;
        });

        findViewById(R.id.offline_image).setOnClickListener(v -> {
            setAllInactiveIcons();
            ((ImageView) findViewById(R.id.offline_image)).setImageResource(R.drawable.icn_status_offline_active);
            MAIN_MENU.getItem(0).setIcon(R.drawable.icn_status_offline_active);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("selectedStatus", "offline");
            editor.commit();
            SELECTED_STATUS_IMAGE = R.drawable.icn_status_offline_active;
            //startServiceNotification();
        });

        findViewById(R.id.online_image).setOnClickListener(v -> {
            setAllInactiveIcons();
            ((ImageView) findViewById(R.id.online_image)).setImageResource(R.drawable.icn_status_online_active);
            MAIN_MENU.getItem(0).setIcon(R.drawable.icn_status_online_active);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("selectedStatus", "online");
            editor.commit();
            SELECTED_STATUS_IMAGE = R.drawable.icn_status_online_active;
            //startServiceNotification();

        });

        findViewById(R.id.sport_image).setOnClickListener(v -> {
            setAllInactiveIcons();
            ((ImageView) findViewById(R.id.sport_image)).setImageResource(R.drawable.icn_status_sport_active);
            MAIN_MENU.getItem(0).setIcon(R.drawable.icn_status_sport_active);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("selectedStatus", "sport");
            editor.commit();
            SELECTED_STATUS_IMAGE = R.drawable.icn_status_sport_active;
            //startServiceNotification();
        });

        findViewById(R.id.busy_image).setOnClickListener(v -> {
            setAllInactiveIcons();
            ((ImageView) findViewById(R.id.busy_image)).setImageResource(R.drawable.icn_status_busy_active);
            MAIN_MENU.getItem(0).setIcon(R.drawable.icn_status_busy_active);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("selectedStatus", "busy");
            editor.commit();
            SELECTED_STATUS_IMAGE = R.drawable.icn_status_busy_active;
            //startServiceNotification();
        });

        findViewById(R.id.dinner_image).setOnClickListener(v -> {
            setAllInactiveIcons();
            ((ImageView) findViewById(R.id.dinner_image)).setImageResource(R.drawable.icn_status_dinner_active);
            MAIN_MENU.getItem(0).setIcon(R.drawable.icn_status_dinner_active);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("selectedStatus", "dinner");
            editor.commit();
            SELECTED_STATUS_IMAGE = R.drawable.icn_status_dinner_active;
            //startServiceNotification();
        });


        findViewById(R.id.party_image).setOnClickListener(v -> {
            setAllInactiveIcons();
            ((ImageView) findViewById(R.id.party_image)).setImageResource(R.drawable.icn_status_party_active);
            MAIN_MENU.getItem(0).setIcon(R.drawable.icn_status_party_active);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("selectedStatus", "party");
            editor.commit();
            SELECTED_STATUS_IMAGE = R.drawable.icn_status_party_active;
            //startServiceNotification();
        });
    }

    private void firstRunLoad() {

        IS_CONTACT_LOAD = true;
        ((Call2meApplication) getApplicationContext()).setCurrentActivity(this);
        loadServiceIntent = new Intent(this, ContactLoadService.class);
        startService(loadServiceIntent);
    }

    private void readFromFile() {


        String nameBackup = null;
        List<File> listBack = storage.getFiles(path + "/Extime/ExtimeContacts/");
        if (listBack != null) {
            for (int i = 0; i < listBack.size(); i++) {
                if (listBack.get(i).getName().contains("backup_") || listBack.get(i).getName().equalsIgnoreCase("backup")) {
                    nameBackup = listBack.get(i).getName();
                }
            }
        }

        System.out.println("READ FROM FILE");
        ArrayList<Contact> listOfContacts = new ArrayList<>();

        String content = storage.readTextFile(path + "/Extime/ExtimeContacts/" + nameBackup);

        Gson gson = new Gson();

        try {
            JSONArray js = new JSONArray(content);
            mainJS = js;
            countContactSFromFile = js.length();

            ArrayList<Contact> l = new ArrayList<>();
            int co = js.length();
            if (js.length() > 50) {
                co = 50;
                ContactsFragment.MessageToLoad = 1;
            }
            for (int i = 0; i < co; i++) {
                JSONObject o = js.getJSONObject(i);

                JSONArray ll = (JSONArray) o.get("listOfContacts");

                if (ll.length() == 0 && o.optString("company") == "") {
                    number = i;
                    Contact c = gson.fromJson(o.toString(), Contact.class);
                    LIST_OF_CONTACTS.add(c);
                    listOfContacts.add(c);


                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            ContactCacheService.updateContact(c, null);
                        }
                    }).start();

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        firstRun = false;


    }

    public static List TimeList;
    public boolean checkRun = false;

    private void updateFile() {

        if (serviceUpdateFile == null) {
            serviceUpdateFile = new Intent(this, UpdateFileService.class);
        }

        startService(serviceUpdateFile);


    }


    public void updateFileOfEvent() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Contact> list = TimeList;
                Gson gson = new Gson();
                String jsonContacts = gson.toJson(list);
                storage.deleteFile(path + "/Extime/ExtimeContacts/backup");
                storage.createFile(path + "/Extime/ExtimeContacts/backup", jsonContacts);
            }
        }).start();

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(UpdateFile event) {

        updateFile();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SetMainProfile event) {
        mainProfile = event.getFrameLayout();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(AddHistoryEntry event) {
        addHistoryEntry(event.getText());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SaveHistory event) {

        saveHistory();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SaveAndUpdateHistoryList event) {

        saveHistory();

        historyAdapter.updateClipboards(history);
        //historyAdapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(UpdateCountClipboard event) {
        clibpboardAdapter.updateCount();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(StatusPopupGone event) {
        STATUS_POPUP.setVisibility(View.GONE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(OpenDriver event) {
        drawer.openDrawer();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NotifyClipboardAdapter event) {
        if (clibpboardAdapter != null) {
            clibpboardAdapter.notifyDataSetChanged();
        }
    }


    public boolean check_k = false;

    public boolean check_k_h;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ClickNameCreate event) {


        ViewGroup.LayoutParams lp = clipboardPopup.findViewById(R.id.popupContainer).getLayoutParams();
        float px = (247 - 50) * getApplicationContext().getResources().getDisplayMetrics().density;


        lp.height = (int) px;
        clipboardPopup.findViewById(R.id.popupContainer).setLayoutParams(lp);


        float px2 = 0;

        px2 = 247 * getApplicationContext().getResources().getDisplayMetrics().density;


        maxH = (int) px2;
        px_min = px;
        check_k = true;


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(StartSync event) {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(UpdateFileSync event) {

        MainActivity.typeUpdate = event.getType();
        MainActivity.nameToUpd.addAll(event.getList());
        MainActivity.nameToUpdTypeContact.addAll(event.getListType());
        updateFile();
    }


    private void updateCountLoad(int number, int countContactSFromFile) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {


                findViewById(R.id.loadBar).setVisibility(View.GONE);
                findViewById(R.id.loadBar).setVisibility(View.VISIBLE);
                findViewById(R.id.progressBar).setVisibility(View.GONE);
                findViewById(R.id.progressBar).setVisibility(View.VISIBLE);

                ((TextView) findViewById(R.id.quantity_contacts_load)).setText("" + number);
                Integer firstValue = new Integer(number);
                Integer secondValue = new Integer(countContactSFromFile);
                ((android.widget.ProgressBar) findViewById(R.id.progressBar)).setProgress((firstValue * 100) / secondValue);
                ((android.widget.ProgressBar) findViewById(R.id.progressBar)).getProgressDrawable().setColorFilter(Color.parseColor("#43A047"), android.graphics.PorterDuff.Mode.SRC_IN);


                ((TextView) findViewById(R.id.main_text_load)).setText(" contacts sync from");

                ((TextView) findViewById(R.id.all_contacts_for_load)).setText("" + countContactSFromFile);
                findViewById(R.id.loadProc).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.loadProc)).setText(" Progress - " + (((firstValue * 100) / secondValue) + 1) + "%   ");


                if (number == countContactSFromFile || number == -1) {

                    findViewById(R.id.loadProc).setVisibility(View.GONE);

                    findViewById(R.id.loadBar).setVisibility(View.GONE);
                    findViewById(R.id.progressBar).setVisibility(View.GONE);


                }
            }
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(UpdateFileFromRealmResult event) {

        updateFileOfEvent();
    }


    private void checkFirstRun() {
        // mainSharedPref = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        // dir
        storage = new Storage(getApplicationContext());

        path = Environment.getExternalStorageDirectory().getAbsolutePath();


        //storage.createFile(path + "/Extime/ExtimeContacts/backup_1", "asdasd");

        boolean dirExists3 = storage.isDirectoryExists(path + "/Extime/Old backup");
        if (!dirExists3)
            storage.createDirectory(path + "/Extime/Old backup");

        boolean dirExists8 = storage.isDirectoryExists(path + "/Extime/TemplatesFiles");
        if (!dirExists8)
            storage.createDirectory(path + "/Extime/TemplatesFiles");

        boolean fileExists = false;
        List<File> listBack = storage.getFiles(path + "/Extime/ExtimeContacts/");
        if (listBack != null) {
            for (int i = 0; i < listBack.size(); i++) {
                if (listBack.get(i).getName().contains("backup_") || listBack.get(i).getName().equalsIgnoreCase("backup")) {
                    fileExists = true;
                }
            }
        }
        boolean dirExists = storage.isDirectoryExists(path + "/Extime/ExtimeFiles");
        if (!dirExists)
            storage.createDirectory(path + "/Extime/ExtimeFiles");

        boolean dirExists2 = storage.isDirectoryExists(path + "/Extime/ExtimeContacts");
        if (!dirExists2)
            storage.createDirectory(path + "/Extime/ExtimeContacts");

        if (fileExists && ContactCacheService.getCountContacts() == 0) {

            int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            if (permission != PackageManager.PERMISSION_GRANTED) {
                //    sharedPreferences.edit().putBoolean(FIRST_RUN, true).apply();
                firstRun = true;
                firstRunLoad();
                LIST_OF_CONTACTS = new ArrayList<>();

                upd = 1;
                LIST_OF_CONTACTS.addAll(contactsService.getContactsPrevWithTypes(false, this));

            } else {
                loadFromFile = true;
                if (ContactCacheService.getCountContacts() == 0) {

                    //upd = 1;
                    firstRunLoad();
                } else if (ContactCacheService.getCountContacts() != 0) {
                    firstRun = false;
                    try {
                        EventBus.getDefault().post(new ContactLoadEvent(null));
                    } catch (NullPointerException e) {

                    }

                }
            }

        } else {

            loadFromFile = false;

            if (!fileExists && ContactCacheService.getCountContacts() == 0) {

                upd = 1;
                firstRunLoad();
            } else if (ContactCacheService.getCountContacts() != 0) {
                firstRun = false;
                try {
                    EventBus.getDefault().post(new ContactLoadEvent(null));
                } catch (NullPointerException e) {

                }

            }
        }

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        boolean checkFirst = sharedPreferences.getBoolean("FIRST", false);


        if (checkFirst) {
            EventBus.getDefault().post(new LoadBarEvent(this));
        }


    }

    public void startServiceNotification() {
        intentServiceNotification = new Intent(this, FabNotificationService.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intentServiceNotification);
        } else {
            startService(intentServiceNotification);
        }

    }

    private void setAllInactiveIcons() {
        ((ImageView) findViewById(R.id.online_image)).setImageResource(R.drawable.icn_status_online_inactive);
        ((ImageView) findViewById(R.id.offline_image)).setImageResource(R.drawable.icn_status_offline_inactive);
        ((ImageView) findViewById(R.id.sport_image)).setImageResource(R.drawable.icn_status_sport_inactive);
        ((ImageView) findViewById(R.id.busy_image)).setImageResource(R.drawable.icn_status_busy_inactive);
        ((ImageView) findViewById(R.id.dinner_image)).setImageResource(R.drawable.icn_status_dinner_inactive);
        ((ImageView) findViewById(R.id.party_image)).setImageResource(R.drawable.icn_status_party_inactive);
    }


    private void asdForBootCompleted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_BOOT_COMPLETED) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_BOOT_COMPLETED}, PERMISSION_REQUEST_BOOT_COMPLETED);
    }

    private void singIngoogleDrive() {

        accountUserDrive = GoogleSignIn.getLastSignedInAccount(this);

        if (accountUserDrive == null && hasConnection(this)) {
            System.out.println("GO DRIVE");
            GoogleSignInOptions signInOptions =
                    new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestEmail()
                            .requestProfile()
                            .requestScopes(Drive.SCOPE_FILE)
                            .build();

            googleDriveSignInClient = GoogleSignIn.getClient(this, signInOptions);

            Intent signInIntent = googleDriveSignInClient.getSignInIntent();

            startActivityForResult(signInIntent, GOOGLE_SIGNIN_BACKUP_REQUEST_CODE);
        }

    }

    public static int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 5469;


    public void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
            }
        }
    }

    public void removeMesFiles() {
        String path = getFilesDir() + "/mesFiles";
        File folder = new File(path);
        if (folder.exists()) {

            File[] files = folder.listFiles();
            if (files != null) {

                for (int i = 0; i < files.length; i++) {
                    files[i].delete();
                }
            }

            folder.delete();
        }
    }

    private GenericRequestBuilder<Uri, InputStream, SVG, PictureDrawable> requestBuilder;

    public static String searchData = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Realm.init(getApplicationContext());


        RealmConfiguration contextRealm = new RealmConfiguration.
                Builder().
                deleteRealmIfMigrationNeeded().
                build();


        Realm.setDefaultConfiguration(contextRealm);

        ContactCacheService.initRealm();

        regitstTrackingExceptiion();


        accountUser = GoogleSignIn.getLastSignedInAccount(this);
       /* if (accountUser == null && hasConnection(this)) {
            signInGoogle();
        }*/


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)) {
            askForContactPermission();

        } else {


            initViewComponents();
            setListeners();
            checkFirstRun();
            getUserData();

            ((TextView) findViewById(R.id.barCountContacts)).setText(" " + (ContactCacheService.getCountContacts()) + "");

            applySelection();
            keyboardCheck();
            initDrawer(mainToolBar);

            removeMesFiles();

            //initStatusBar();
            startServiceNotification();
            initClipboardRecycler();
            initHistoryrecycler();

            if (!EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().register(this);
            }

            Uri text = getIntent().getData();


            if (text != null && text.toString().contains("tel:")) {

                if (text.toString().contains("%2B")) {
                    text = Uri.parse(text.toString().replace("%2B", "+"));
                }

                if (text.toString().contains("%20")) {
                    text = Uri.parse(text.toString().replaceAll("%20", " "));
                }
                EventBus.getDefault().post(new TestService(text.toString().substring(4), true));

                searchData = text.toString().substring(4);

            } else if (text != null && text.toString().contains("mailto:")) {

                EventBus.getDefault().post(new TestService(text.toString().substring(7), true));

                searchData = text.toString().substring(7);
            } else if (text != null && ClipboardType.isWeb(text.toString())) {
                EventBus.getDefault().post(new TestService(text.toString(), true));
                searchData = text.toString();

            } else if (text != null) {
                EventBus.getDefault().post(new TestService(text.toString(), true));
                searchData = text.toString();
            }

            getIntent().setData(null);

            if (((TextView) drawer.getDrawerLayout().findViewById(R.id.switchFab)).getText().toString().equalsIgnoreCase("on"))
                checkPermission();

            //if (((Switch) drawer.getDrawerLayout().findViewById(R.id.switchFab)).isChecked())


            getProfileUser();


        }
    }

    public void getProfileUser() {
        new Thread(new Runnable() {
            @Override
            public void run() {


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            Cursor cursor = getContentResolver().query(ContactsContract.Profile.CONTENT_URI, null, null, null, null);

                            while (cursor.moveToNext()) {


                                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                                String wayPhoto = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI));

                                if (name != null)
                                    ((TextView) findViewById(R.id.profileName)).setText(name);
                                if (wayPhoto != null)
                                    ((CircleImageView) findViewById(R.id.profile_image)).setImageURI(Uri.parse(wayPhoto));

                            }
                            cursor.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                });
            }
        }).start();
    }

    public ArrayList<Template> getListDataNew() {

        ArrayList list = new ArrayList<>();

        Storage storage = new Storage(getApplicationContext());
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        String content = null;
        try {
            content = storage.readTextFile(path + "/Extime/ExtimeContacts/backupTemplates");
        } catch (Exception e) {
            e.printStackTrace();

        }

        if (content == null) {


            Template new_template = new Template("New template", false, null);
            Template intro = new Template("Intro", false, null);
            Template presentation = new Template("Presentation", false, null);
            Template one_pager = new Template("One pager", false, null);
            Template files = new Template("Files", false, null);
            list.add(new_template);
            list.add(intro);
            list.add(presentation);
            list.add(one_pager);
            list.add(files);

        } else {

            Gson gson = new Gson();

            list.addAll(gson.fromJson(content, new TypeToken<ArrayList<Template>>() {
            }.getType()));


        }
        return list;
    }

    public int getContactsCt() {
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        Cursor cursor = this.getContentResolver().query(uri, null, null, null, null);
        int contactsCt = 0;

        if (cursor != null) {
            while (cursor.moveToNext()) {

                if (cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)) != null) {
                    contactsCt++;

                }

            }
        }

        return contactsCt;
    }

    private void regitstTrackingExceptiion() {
        Thread.UncaughtExceptionHandler handleAppCrash = (t, e) -> {

            Storage storage = new Storage(getApplicationContext());
            path = Environment.getExternalStorageDirectory().getAbsolutePath();


            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String errors = sw.toString();

            try {
                storage.deleteFile(path + "/Extime/crash");
                storage.createFile(path + "/Extime/crash", errors);
            } catch (Exception e1) {

            }

            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);

        };
        Thread.setDefaultUncaughtExceptionHandler(handleAppCrash);

    }


    private void changeToolbarToCompany() {
        ((TextView) findViewById(R.id.toolbar_title)).setText("Company");
        mainToolBar.setNavigationIcon(R.drawable.icn_arrow_back);
        mainToolBar.setNavigationOnClickListener(v -> {
            MainActivity.IS_PAUSE = false;
            onBackPressed();
        });
    }

    private void changeToolbarToProfile() {
        ((TextView) findViewById(R.id.toolbar_title)).setText("Profile");
        mainToolBar.setNavigationIcon(R.drawable.icn_arrow_back);
        mainToolBar.setNavigationOnClickListener(v -> {
            MainActivity.IS_PAUSE = false;
            onBackPressed();
        });
    }

    private void changeToolbarToFile() {
        ((TextView) findViewById(R.id.toolbar_title)).setText("Files");
    }

    private void changeToolbarToMerge() {
        mainToolBar.findViewById(R.id.toolbar_title).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.toolbar_title)).setText("Merge");
        //   mainToolBar.setNavigationIcon(R.drawable.icn_arrow_back);
        mainToolBar.setNavigationOnClickListener(v -> {
            MainActivity.IS_PAUSE = false;
            onBackPressed();
        });
    }

    private void changeToolbarToCreate() {
        ((TextView) findViewById(R.id.toolbar_title)).setText("Create");
        // mainToolBar.setNavigationIcon(R.drawable.icn_arrow_back);
        mainToolBar.setNavigationOnClickListener(v -> {
            MainActivity.IS_PAUSE = false;
            onBackPressed();
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!ContactAdapter.checkMerge) {


                if (!CompanyAdapter.selectionModeCompany) {

                    System.out.println("BACK CLICK 1");
                    int count = getSupportFragmentManager().getBackStackEntryCount();


                    int ti = 0;

                    for (int i = 0; i < count; i++) {

                        //System.out.println("i = "+i+", count = "+count);


                        if (getSupportFragmentManager().getBackStackEntryAt(i).getName() != null && getSupportFragmentManager().getBackStackEntryAt(i).getName().equals("timeline")) {
                           /* count--;
                            i--;*/
                            ti++;
                        }


                    }

                    count -= ti;


                    if (findViewById(R.id.share_template_message).getVisibility() == View.VISIBLE) {
                        findViewById(R.id.share_template_message).setVisibility(View.GONE);
                        return false;
                    }

                    if (contactsFragment != null && contactsFragment.companyProfilePopup != null && contactsFragment.companyProfilePopup.getVisibility() == View.VISIBLE) {

                        //if(contactsFragment.)
                        contactsFragment.companyProfilePopup.setVisibility(View.GONE);
                        if (contactsFragment.profilePopUp != null && contactsFragment.profilePopUp.getVisibility() == View.VISIBLE) {
                            contactsFragment.selectedContactPopup = contactsFragment.lastOpenedProfileId;
                        } else {
                            contactsFragment.contactAdapter.setSelectContactID(null);
                            contactsFragment.closeOtherPopup();
                            contactsFragment.contactAdapter.notifyDataSetChanged();
                        }
                        return false;
                    }

                    if (findViewById(R.id.popupMessage) != null && findViewById(R.id.popupMessage).getVisibility() == View.VISIBLE && mainToolBar.findViewById(R.id.toolbar_kanban).getVisibility() == View.GONE) {
                        findViewById(R.id.popupMessage).setVisibility(View.GONE);
                        return false;
                    }

                    if (contactsFragment != null && contactsFragment.profilePopUp != null && contactsFragment.profilePopUp.getVisibility() == View.VISIBLE) {
                        contactsFragment.profilePopUp.setVisibility(View.GONE);
                        contactsFragment.contactAdapter.setSelectContactID(null);
                        contactsFragment.closeOtherPopup();
                        contactsFragment.contactAdapter.notifyDataSetChanged();
                        return false;
                    }


                    if (count == 0) {
                        Intent startMain = new Intent(Intent.ACTION_MAIN);
                        startMain.addCategory(Intent.CATEGORY_HOME);
                        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(startMain);
                    } else {
                        if (checkDel) {

                            android.support.v4.app.FragmentManager fragmentManager = this.getSupportFragmentManager();
                            fragmentManager.popBackStack("contacts", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        } else if (ContactsFragment.createContact) {


                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                    this);
                            alertDialogBuilder.setTitle("Do you want to quit creating a contact?");
                            alertDialogBuilder
                                    .setCancelable(false)
                                    .setPositiveButton("Yes", (dialog, id) -> {
                                        if (findViewById(R.id.framePopupSocial).getVisibility() == View.VISIBLE) {
                                            ((EditText) findViewById(R.id.magic_edit_text)).setText("");
                                        }
                                        findViewById(R.id.framePopupSocial).setVisibility(View.GONE);
                                        mainToolBar.findViewById(R.id.cancel_toolbar).setVisibility(View.GONE);
                                        ContactsFragment.createContact = false;
                                        fabMenuContainer.setVisibility(View.VISIBLE);
                                        findViewById(R.id.popupProfileCompanyPossitions).setVisibility(View.GONE);
                                        ContactAdapter.checkMergeContacts = false;
                                        onBackPressed();

                                    })
                                    .setNegativeButton("No", (dialog, id) -> dialog.cancel());

                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();

                        } else {

                            if (ProfileFragment.profile && ProfileFragment.nowEdit) {
                                ProfileFragment.nowEdit = false;
                            }


                            android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
                            List<Fragment> listF = fm.getFragments();


                            int c = 1;
                            boolean checkCOnt = false;
                            for (int i = listF.size() - 1; i >= 0; i--) {
                                try {


                                    if (listF.get(i).getClass().equals(ContactsFragment.class))
                                        checkCOnt = true;

                                    if (!((ProfileFragment) listF.get(i)).getMainContact().isValid()) {
                                        c++;
                                        continue;

                                    }
                                    //System.out.println("Name = " + ((ProfileFragment) listF.get(listF.size() - 2)).getMainContact().getName());
                                } catch (Exception e) {

                                }
                                break;
                            }


                            if (checkCOnt && c == 1) {
                                Intent startMain = new Intent(Intent.ACTION_MAIN);
                                startMain.addCategory(Intent.CATEGORY_HOME);
                                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(startMain);
                            } else {


                                for (int q = 0; q < c; q++) {
                                    try {
                                        getSupportFragmentManager().popBackStack();
                                    } catch (IllegalStateException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }
                        }
                    }

                    return true;
                } else {
                    if (CompanyAdapter.mergeCompanyAdapter) {


                        CompanyAdapter.mergeCompanyAdapter = false;
                        onBackPressed();
                    } else {
                        CompanyAdapter.selectionModeCompany = false;

                        EventBus.getDefault().post(new ClickBackOnProfile());
                    }
                    return false;
                }
            } else {

                // if(!CompanyAdapter.selectionModeCompany)
                if (ContactsFragment.createContact) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            this);
                    alertDialogBuilder.setTitle("Do you want to quit creating a contact?");
                    alertDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("Yes", (dialog, id) -> {
                                if (findViewById(R.id.framePopupSocial).getVisibility() == View.VISIBLE) {
                                    ((EditText) findViewById(R.id.magic_edit_text)).setText("");
                                }
                                findViewById(R.id.framePopupSocial).setVisibility(View.GONE);
                                mainToolBar.findViewById(R.id.cancel_toolbar).setVisibility(View.GONE);
                                ContactsFragment.createContact = false;
                                fabMenuContainer.setVisibility(View.VISIBLE);
                                ContactAdapter.checkMergeContacts = false;
                                onBackPressed();

                            })
                            .setNegativeButton("No", (dialog, id) -> dialog.cancel());

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                } else
                    contactsFragment.contactAdapter.stopNEWSelection();

                return false;
            }


        }
        return super.onKeyDown(keyCode, event);
    }

    public void askForContactPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            if ((ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.SYSTEM_ALERT_WINDOW) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, PERMISSION_REQUEST_CONTACT);


            }
    }

    public void changeToolbarToContacts() {
        mainToolBar.setNavigationIcon(R.drawable.icn_menu);
        ((TextView) findViewById(R.id.toolbar_title)).setText("Contacts");
        mainToolBar.setNavigationOnClickListener(v -> {

            drawer.openDrawer();
            drawer.openDrawer();
        });

    }

    public void startDrawer() {
        mainToolBar.setNavigationIcon(R.drawable.icn_menu);
        mainToolBar.setNavigationOnClickListener(v -> {

            drawer.openDrawer();
            drawer.openDrawer();
        });

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(StartProcessDial event) {
        System.out.println("Update clipboard");
        startP();
    }

    public void startP() {
        Uri text = getIntent().getData();


        if (text == null && MainActivity.searchData != null) {

            try {
                findViewById(R.id.contacts_block).callOnClick();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }

            text = Uri.parse(searchData);
        }

        if (text != null)
            searchData = text.toString();


        if (text != null && text.toString().contains("tel:")) {
            System.out.println("CALL = " + text);

            if (text.toString().contains("%2B")) {
                //System.out.println("REPLACE");
                text = Uri.parse(text.toString().replaceAll("%2B", ""));
            }

            if (text.toString().contains("%20")) {
                //System.out.println("REPLACE");
                text = Uri.parse(text.toString().replaceAll("%20", ""));
            }

            EventBus.getDefault().post(new TestService(text.toString().substring(4), true));

            searchData = text.toString().substring(4);

        } else if (text != null && text.toString().contains("mailto:")) {
            System.out.println("MAIL = " + text);

            EventBus.getDefault().post(new TestService(text.toString().substring(7), true));

            searchData = text.toString().substring(7);

        } else if (text != null && ClipboardType.isWeb(text.toString())) {
            EventBus.getDefault().post(new TestService(text.toString(), true));
            searchData = text.toString();

        } else if (text != null) {
            EventBus.getDefault().post(new TestService(text.toString(), true));
            searchData = text.toString();
        }

        /*if(searchData != null) {
            findViewById(R.id.contacts_block).callOnClick();
        }*/


        if (searchData != null) {
            ((EditText) findViewById(R.id.magic_edit_text)).setText(MainActivity.searchData);
            //findViewById(R.id.plane_icon).callOnClick();
            contactsFragment.searcByAll();
            MainActivity.searchData = null;
            getIntent().setData(null);
        }
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        startApp = false;
        if (destroyStop) {
            System.out.println("RESTART ACTIVITY");


            Uri text = getIntent().getData();


            if (text == null && MainActivity.searchData != null) {
                text = Uri.parse(searchData);
            }

            if (text != null)
                searchData = text.toString();


            if (text != null && text.toString().contains("tel:")) {
                System.out.println("CALL = " + text);

                if (text.toString().contains("%2B")) {
                    //System.out.println("REPLACE");
                    text = Uri.parse(text.toString().replaceAll("%2B", ""));
                }

                if (text.toString().contains("%20")) {
                    //System.out.println("REPLACE");
                    text = Uri.parse(text.toString().replaceAll("%20", ""));
                }

                EventBus.getDefault().post(new TestService(text.toString().substring(4), true));

                searchData = text.toString().substring(4);

            } else if (text != null && text.toString().contains("mailto:")) {
                System.out.println("MAIL = " + text);

                EventBus.getDefault().post(new TestService(text.toString().substring(7), true));

                searchData = text.toString().substring(7);

            } else if (text != null && ClipboardType.isWeb(text.toString())) {
                EventBus.getDefault().post(new TestService(text.toString(), true));
                searchData = text.toString();
            } else if (text != null) {
                EventBus.getDefault().post(new TestService(text.toString(), true));
                searchData = text.toString();
            }

            if (searchData != null) {

                try {

                    findViewById(R.id.contacts_block).callOnClick();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }


            if (searchData != null) {
                ((EditText) findViewById(R.id.magic_edit_text)).setText(MainActivity.searchData);
                //findViewById(R.id.plane_icon).callOnClick();
                contactsFragment.searcByAll();
                MainActivity.searchData = null;
                getIntent().setData(null);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        destroyStop = false;
        //destroyApp = false;
        //================================


        if (voice_command == null) {


            try {
                String command = FabNotificationService.textVoice;
                voice_command = command;


                if (voice_command != null || FabNotificationService.textVoice != null)
                    EventBus.getDefault().post(new SetDefaultPush());

                if (voice_command != null && (voice_command.toLowerCase().contains("email template") || voice_command.toLowerCase().contains("email"))) {


                    ArrayList<Template> l = getListDataNew();

                    String[] mas = voice_command.split(" ");

                    if ((voice_command.toLowerCase().contains("email template") && mas.length == 2) || (voice_command.toLowerCase().contains("email") && mas.length == 1))
                        return;

                    StringBuilder textTemplate = new StringBuilder();

                    if (voice_command.toLowerCase().contains("email template")) {
                        for (int i = 2; i < mas.length; i++) {
                            textTemplate.append(mas[i]).append(" ");

                        }
                    } else {
                        for (int i = 1; i < mas.length; i++) {
                            textTemplate.append(mas[i]).append(" ");

                        }
                    }


                    //==========================

                    EmailsFragment.searchCall = textTemplate.toString();
                    findViewById(R.id.emails_block).callOnClick();
                }

                if (voice_command != null && voice_command.trim().substring(0, 4).equals("call") && voice_command.trim().length() > 4) {


                    ((EditText) findViewById(R.id.magic_edit_text)).setText(voice_command.replace("call", "").trim());
                    findViewById(R.id.contacts_block).callOnClick();
                }


            } catch (Exception e) {
                e.printStackTrace();

            }

        }


        //================================


        try {
            if (!EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().register(this);

            }
        } catch (Exception e) {
        }

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();
        destroyStop = true;
        System.out.println("STOP MAIN");
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(CopyValueEvent event) {
        System.out.println("Update clipboard");
        updateClipboardRecycler();
        //EventBus.getDefault().post(new SaveClipboard());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(OpenClipboard event) {
        System.out.println("open clipboard 1");
        //clipboardPopup.setVisibility(View.VISIBLE);

        if (clipboardPopup != null) {

            if (clipboardPopup.getVisibility() == View.GONE)
                showClipboardPopup();
            else {
                showClipboardPopup();
                showClipboardPopup();
            }
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case 101:
                    try {
                        // The Task returned from this call is always completed, no need to attach
                        // a listener.
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                        accountUser = task.getResult(ApiException.class);


                        //onLoggedIn(account);
                    } catch (ApiException e) {
                        // The ApiException status code indicates the detailed failure reason.
                        //Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
                    }
                    break;

                case GOOGLE_SIGNIN_BACKUP_REQUEST_CODE: {
                    try {
                        // The Task returned from this call is always completed, no need to attach
                        // a listener.
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                        accountUserDrive = task.getResult(ApiException.class);

                        if (accountUserDrive != null && hasConnection(this)) {
                            drawer.getDrawerLayout().findViewById(R.id.drawerdrive).setVisibility(View.GONE);

                            Glide.with(this).load(accountUserDrive.getPhotoUrl()).into(((CircleImageView) drawer.getDrawerLayout().findViewById(R.id.accountLogoDrive)));

                            //((CircleImageView) drawer.getDrawerLayout().findViewById(R.id.accountLogoDrive)).setImageURI(accountUserDrive.getPhotoUrl());
                            ((TextView) drawer.getDrawerLayout().findViewById(R.id.emailAccountDrawer)).setText(accountUserDrive.getEmail());

                            drawer.getDrawerLayout().findViewById(R.id.drawerdriveAccount).setVisibility(View.VISIBLE);
                        } else {
                            drawer.getDrawerLayout().findViewById(R.id.drawerdriveAccount).setVisibility(View.GONE);
                            drawer.getDrawerLayout().findViewById(R.id.drawerdrive).setVisibility(View.VISIBLE);
                        }


                        //onLoggedIn(account);
                    } catch (ApiException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }

        if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    // You don't have permission
                    //checkPermission();

                    SharedPreferences pref_switch = getSharedPreferences("SwitchFab", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref_switch.edit();
                    editor.putBoolean("switch", false);
                    editor.apply();

                    //((Switch) drawer.getDrawerLayout().findViewById(R.id.switchFab)).setChecked(false);
                    ((TextView) drawer.getDrawerLayout().findViewById(R.id.switchFab)).setText("OFF");
                    ((TextView) drawer.getDrawerLayout().findViewById(R.id.switchFab)).setTextColor(getResources().getColor(R.color.gray));
                } else {
                    // Do as per your logic


                    boolean tStartService = false;

                    ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
                    List<ActivityManager.RunningServiceInfo> rs = am.getRunningServices(50);
                    for (int i = 0; i < rs.size(); i++) {
                        ActivityManager.RunningServiceInfo rsi = rs.get(i);
                        if (FabNotificationService.class.getName().equalsIgnoreCase(rsi.service.getClassName())) {
                            tStartService = true;
                        }
                    }

                    if (tStartService) {

                        if (((TextView) drawer.getDrawerLayout().findViewById(R.id.switchFab)).getText().toString().equalsIgnoreCase("ON")) {
                            ((TextView) drawer.getDrawerLayout().findViewById(R.id.switchFab)).setTextColor(getResources().getColor(R.color.primary));
                            SharedPreferences pref_switch = getSharedPreferences("SwitchFab", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref_switch.edit();
                            editor.putBoolean("switch", true);
                            editor.apply();

                            FabNotificationService.showFab();
                        } else {
                            ((TextView) drawer.getDrawerLayout().findViewById(R.id.switchFab)).setTextColor(getResources().getColor(R.color.primary));
                            //((Switch) drawer.getDrawerLayout().findViewById(R.id.switchFab)).setChecked(true);
                            ((TextView) drawer.getDrawerLayout().findViewById(R.id.switchFab)).setText("ON");
                            SharedPreferences pref_switch = getSharedPreferences("SwitchFab", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref_switch.edit();
                            editor.putBoolean("switch", true);
                            editor.apply();

                            FabNotificationService.showFab();
                        }

                        /*if (((Switch) drawer.getDrawerLayout().findViewById(R.id.switchFab)).isChecked()) {

                            SharedPreferences pref_switch = getSharedPreferences("SwitchFab", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref_switch.edit();
                            editor.putBoolean("switch", true);
                            editor.apply();

                            FabNotificationService.showFab();
                        } else {
                            ((Switch) drawer.getDrawerLayout().findViewById(R.id.switchFab)).setChecked(true);

                            SharedPreferences pref_switch = getSharedPreferences("SwitchFab", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref_switch.edit();
                            editor.putBoolean("switch", true);
                            editor.apply();
                        }*/

                    }

                }
            }
            startServiceNotification();
        } else {
            startServiceNotification();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {


        if (requestCode == PERMISSION_REQUEST_ALERT) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(this, "РўСЂРµР±СѓРµС‚СЃСЏ РїРѕРґС‚РІРµСЂРґРёС‚СЊ СЂР°Р·СЂРµС€РµРЅРёРµ", Toast.LENGTH_SHORT).show();
            }
            if (requestCode == REQUEST_READ_PHONE_STATE) ;
            if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                PROFILE_CONTACT = contactsService.getUserProfile();
                System.out.println(PROFILE_CONTACT.getName());
            }

        }
        if (requestCode == 91) {
            if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Storage storage = new Storage(getApplicationContext());
                String path = Environment.getExternalStorageDirectory().getPath();
                Gson gson = new Gson();
                String jsonContacts = gson.toJson(LIST_OF_CONTACTS);

                storage.createDirectory(path + "/Extime/ExtimeContacts", false);

                storage.createFile(path + "/Extime/ExtimeContacts/backup_" + MainActivity.versionDebug, jsonContacts);
            }

        }
        if (requestCode == 66) {
            if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                readFromFile();
            }
        }


        if (requestCode == PERMISSION_REQUEST_CONTACT) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {


                initViewComponents();


                setListeners();
                //initStatusBar();
                keyboardCheck();
                initDrawer(mainToolBar);

                new Thread(new Runnable() {
                    @Override
                    public void run() {


                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                try {
                                    Cursor cursor = getContentResolver().query(ContactsContract.Profile.CONTENT_URI, /*SELF_PROJECTION*/ null, null, null, null);
                                    //      cursor.moveToFirst();
                                    while (cursor.moveToNext()) {
                                        String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                                        /* String wayPhoto = cursor.getString(2);*/

                                        String wayPhoto = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI));


                                        if (name != null)
                                            ((TextView) findViewById(R.id.profileName)).setText(name);
                                        if (wayPhoto != null)
                                            ((CircleImageView) findViewById(R.id.profile_image)).setImageURI(Uri.parse(wayPhoto));

                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                //   }


                            }
                        });
                    }
                }).start();

                checkFirstRun();

                selectedFragment = new ContactsFragment();

                getUserData();

                applySelection();

                initClipboardRecycler();

                initHistoryrecycler();

                //if (((Switch) drawer.getDrawerLayout().findViewById(R.id.switchFab)).isChecked())
                if (((TextView) drawer.getDrawerLayout().findViewById(R.id.switchFab)).getText().toString().equalsIgnoreCase("ON")) {

                    checkPermission();
                }


            } else {
                Toast.makeText(this, "РўСЂРµР±СѓРµС‚СЃСЏ РїРѕРґС‚РІРµСЂРґРёС‚СЊ СЂР°Р·СЂРµС€РµРЅРёРµ РґР»СЏ РґРѕСЃС‚СѓРїР° Рє РєРѕРЅС‚Р°РєС‚Р°Рј", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void removeSoftKeyboard() {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean startApp = false;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus && !startApp) {


            ClipboardManager clipboardManager = (ClipboardManager) this.getSystemService(CLIPBOARD_SERVICE);
            ClipData clipData = clipboardManager.getPrimaryClip();
            if (clipData != null) {
                ClipData.Item item = clipData.getItemAt(0);


                if (item.getText() != null) {
                    EventBus.getDefault().post(new TestService(item.getText().toString(), false));
                }
            }
            startApp = true;
        }

    }


    public static boolean destroyApp = false;
    public static boolean destroyStop = false;

    @Override
    protected void onDestroy() {

        super.onDestroy();

        EventBus.getDefault().post(new UpdateFile());
        EventBus.getDefault().post(new SaveClipboard());
        EventBus.getDefault().post(new SaveHistory());

        try {
            if (EventBus.getDefault().isRegistered(this))
                EventBus.getDefault().unregister(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        destroyApp = true;

        ContactCacheService.realmContactService.close();

        System.out.println("Compact = " + Realm.compactRealm(Realm.getDefaultConfiguration()));

    }


    public static boolean hasConnection(final Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        wifiInfo = cm.getActiveNetworkInfo();
        return wifiInfo != null && wifiInfo.isConnected();
    }


    public void initDrawer(Toolbar toolbar) {

        View drawerLayout = getLayoutInflater().inflate(R.layout.drawer_layout, null);


      /*  RequestBuilder <PictureDrawable> requestBuilder =
                Glide.with(this)
                        .as(PictureDrawable.class)
                        .placeholder(R.drawable.link)
                        .error(R.drawable.link)
                        //.transition(withCrossFade())
                        .listener(new SvgSoftwareLayerSetter());

        Uri uri = Uri.parse("http://www.clker.com/cliparts/u/Z/2/b/a/6/android-toy-h.svg");



        //ImageView img = findViewById(R.id.textSvg)

        requestBuilder.load(uri).into((ImageView) drawerLayout.findViewById(R.id.textSvg));*/

        /*ImageView img = drawerLayout.findViewById(R.id.textSvg);

        Uri uri = Uri.parse("https://svgshare.com/i/J4i.svg");

        //GlideToVectorYou.justLoadImage(this, uri, img);

        GlideToVectorYou.init().with(this).withListener(new GlideToVectorYouListener() {
            @Override
            public void onLoadFailed() {
                Toast.makeText(MainActivity.this, "Load failed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResourceReady() {
                Toast.makeText(MainActivity.this, "Image ready", Toast.LENGTH_SHORT).show();
            }
        }).load(uri, img);*/


/*
        RequestBuilder requestBuilder = Glide.with(this)
                .using(Glide.buildStreamModelLoader(Uri.class, this), InputStream.class)
                .from(Uri.class)
                .as(SVG.class)
                .transcode(new SvgDrawableTranscoder(), PictureDrawable.class)
                .sourceEncoder(new StreamEncoder())
                .cacheDecoder(new FileToStreamDecoder<SVG>(new SvgDecoder()))
                .decoder(new SvgDecoder())
                .placeholder(R.drawable.icn_link)
                .error(R.drawable.icn_link)
                .animate(android.R.anim.fade_in)
                .listener(new SvgSoftwareLayerSetter<Uri>());*/


        int f = versionDebug / 10;
        int s = versionDebug % 10;
        ((TextView) drawerLayout.findViewById(R.id.versionApp)).setText("Version " + f + "." + s);


        accountUserDrive = GoogleSignIn.getLastSignedInAccount(this);
        if (accountUserDrive != null && hasConnection(this)) {
            drawerLayout.findViewById(R.id.drawerdrive).setVisibility(View.GONE);


            //Glide.

            Glide.with(this).load(accountUserDrive.getPhotoUrl()).into(((CircleImageView) drawerLayout.findViewById(R.id.accountLogoDrive)));

            //((CircleImageView) drawerLayout.findViewById(R.id.accountLogoDrive)).setImageURI(accountUserDrive.getPhotoUrl());
            ((TextView) drawerLayout.findViewById(R.id.emailAccountDrawer)).setText(accountUserDrive.getEmail());

            drawerLayout.findViewById(R.id.drawerdriveAccount).setVisibility(View.VISIBLE);
        } else {
            drawerLayout.findViewById(R.id.drawerdriveAccount).setVisibility(View.GONE);
            drawerLayout.findViewById(R.id.drawerdrive).setVisibility(View.VISIBLE);
        }

        SharedPreferences pref_switch = getSharedPreferences("SwitchFab", Context.MODE_PRIVATE);
        boolean switchBoolean = pref_switch.getBoolean("switch", true);

        if (!switchBoolean) {
            ((TextView) drawerLayout.findViewById(R.id.switchFab)).setText("OFF");
            ((TextView) drawerLayout.findViewById(R.id.switchFab)).setTextColor(getResources().getColor(R.color.gray));
            //((Switch) drawerLayout.findViewById(R.id.switchFab)).setChecked(false);
        } else {
            ((TextView) drawerLayout.findViewById(R.id.switchFab)).setText("ON");
            ((TextView) drawerLayout.findViewById(R.id.switchFab)).setTextColor(getResources().getColor(R.color.primary));
        }


        boolean switchBoolean_2 = pref_switch.getBoolean("switchMessage", true);

        if (!switchBoolean_2) {
            ((TextView) drawerLayout.findViewById(R.id.switchMessageLink)).setText("OFF");
            ((TextView) drawerLayout.findViewById(R.id.switchMessageLink)).setTextColor(getResources().getColor(R.color.gray));
            //((Switch) drawerLayout.findViewById(R.id.switchMessageLink)).setChecked(false);
        } else {
            ((TextView) drawerLayout.findViewById(R.id.switchMessageLink)).setText("ON");
            ((TextView) drawerLayout.findViewById(R.id.switchMessageLink)).setTextColor(getResources().getColor(R.color.primary));
        }

        SharedPreferences.Editor editor = pref_switch.edit();
        editor.putBoolean("switchMessage", true);
        editor.apply();

        drawer = new DrawerBuilder()
                .withCustomView(drawerLayout)
                .withActivity(this)
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View drawerView) {


                        drawerLayout.findViewById(R.id.drawerdrive).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                singIngoogleDrive();
                            }
                        });

                        drawerLayout.findViewById(R.id.frameSwitch).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (((TextView) drawerLayout.findViewById(R.id.switchFab)).getText().toString().equalsIgnoreCase("OFF")) {
                                    try {

                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                            if (Settings.canDrawOverlays(MainActivity.this)) {
                                                try {

                                                    SharedPreferences.Editor editor = pref_switch.edit();
                                                    editor.putBoolean("switch", true);
                                                    editor.apply();

                                                    FabNotificationService.showFab();

                                                    ((TextView) drawerLayout.findViewById(R.id.switchFab)).setText("ON");
                                                    ((TextView) drawerLayout.findViewById(R.id.switchFab)).setTextColor(getResources().getColor(R.color.primary));

                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            } else {
                                                checkPermission();
                                            }
                                        }


                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                } else {
                                    try {

                                        ((TextView) drawerLayout.findViewById(R.id.switchFab)).setText("OFF");
                                        ((TextView) drawerLayout.findViewById(R.id.switchFab)).setTextColor(getResources().getColor(R.color.gray));
                                        SharedPreferences.Editor editor = pref_switch.edit();
                                        editor.putBoolean("switch", false);
                                        editor.apply();

                                        FabNotificationService.hideFab();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });


                        drawerLayout.findViewById(R.id.frameSwitchMessageLink).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (((TextView) drawerLayout.findViewById(R.id.switchMessageLink)).getText().toString().equalsIgnoreCase("OFF")) {
                                    try {


                                        try {

                                            SharedPreferences.Editor editor = pref_switch.edit();
                                            editor.putBoolean("switchMessage", true);
                                            editor.apply();


                                            ((TextView) drawerLayout.findViewById(R.id.switchMessageLink)).setText("ON");
                                            ((TextView) drawerLayout.findViewById(R.id.switchMessageLink)).setTextColor(getResources().getColor(R.color.primary));

                                            EventBus.getDefault().post(new CheckLinkMessageReply(true));


                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }


                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                } else {
                                    try {

                                        ((TextView) drawerLayout.findViewById(R.id.switchMessageLink)).setText("OFF");
                                        ((TextView) drawerLayout.findViewById(R.id.switchMessageLink)).setTextColor(getResources().getColor(R.color.gray));
                                        SharedPreferences.Editor editor = pref_switch.edit();
                                        editor.putBoolean("switchMessage", false);
                                        editor.apply();

                                        EventBus.getDefault().post(new CheckLinkMessageReply(false));


                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                EventBus.getDefault().post(new CheckLinkMessageTEmails());
                                EventBus.getDefault().post(new CheckLinkMessageTemplateFragment());
                                EventBus.getDefault().post(new CheckLinkMessageTemplateFragmentEdit());
                            }
                        });


                        /*((Switch) drawerLayout.findViewById(R.id.switchFab)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if (isChecked) {
                                    try {

                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                            if (Settings.canDrawOverlays(MainActivity.this)) {
                                                try {

                                                    SharedPreferences.Editor editor = pref_switch.edit();
                                                    editor.putBoolean("switch", true);
                                                    editor.apply();

                                                    FabNotificationService.showFab();

                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            } else {
                                                checkPermission();
                                            }
                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                } else {
                                    try {


                                        SharedPreferences.Editor editor = pref_switch.edit();
                                        editor.putBoolean("switch", false);
                                        editor.apply();

                                        FabNotificationService.hideFab();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });*/


                        drawerLayout.findViewById(R.id.drawerDirvices).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                            }
                        });

                        drawerLayout.findViewById(R.id.drawerProfile).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                            }
                        });

                        drawerLayout.findViewById(R.id.drawerMessengers).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                            }
                        });

                        drawerLayout.findViewById(R.id.drawerWidget).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                            }
                        });

                        drawerLayout.findViewById(R.id.drawerHelp).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                            }
                        });

                        drawerLayout.findViewById(R.id.drawerRating).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                            }
                        });

                        drawerLayout.findViewById(R.id.drawerSettings).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                            }
                        });

                        drawerLayout.findViewById(R.id.drawerinvite).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                            }
                        });


                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                    }

                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {
                        removeSoftKeyboard();
                    }
                })
                .withToolbar(toolbar)
                .withOnDrawerItemClickListener((view, positimon, drawerItem) -> false)
                .build();
    }

    public void requestAlertMessage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SYSTEM_ALERT_WINDOW) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW}, PERMISSION_REQUEST_ALERT);
    }

    private boolean isOpenSoftKey = false;

    private boolean checkK2 = false;

    private boolean checkK2_h = false;

    public void keyboardCheck() {
        View contentView = findViewById(R.id.activity_main);
        contentView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect r = new Rect();
            contentView.getWindowVisibleDisplayFrame(r);
            int screenHeight = contentView.getRootView().getHeight();
            int keypadHeight = screenHeight - r.bottom;

            if (keypadHeight > screenHeight * 0.15) {


                if (historyPopup != null && historyPopup.getVisibility() == View.VISIBLE) {
                    globalSearchpopup.setVisibility(View.GONE);


                    if ((findViewById(R.id.popupProfileCompanyPossitions) != null && findViewById(R.id.popupProfileCompanyPossitions).getVisibility() == View.VISIBLE)) {
                        //contactsFragment.popupHelpCompanyposition.setVisibility(View.GONE);
                        historyPopup.setVisibility(View.GONE);
                        if (historyAdapter != null) historyAdapter.uncheck();
                        fabMenuContainer.setVisibility(View.VISIBLE);
                    }


                    check_k_h = false;
                    ViewGroup.LayoutParams lp = historyPopup.findViewById(R.id.popupContainerHistory).getLayoutParams();

                    float px = 247 * getApplicationContext().getResources().getDisplayMetrics().density;

                    if (lp.height > px) {
                        lp.height = (int) px;
                        historyPopup.findViewById(R.id.popupContainerHistory).setLayoutParams(lp);

                    }

                    //System.out.println("F2");
                    maxH_h = (int) px;
                    px_min_h = (247 - 50) * getApplicationContext().getResources().getDisplayMetrics().density;


                    if (!checkK2_h) {
                        lp.height = (int) px_min;
                        historyPopup.findViewById(R.id.popupContainerHistory).setLayoutParams(lp);
                        checkK2_h = true;
                    }

                    int[] location = new int[2];

                    findViewById(R.id.popupContainerHistory).getLocationOnScreen(location);

                    bottomY_h[0] = location[1];
                    bottomY_h[0] += findViewById(R.id.popupContainerHistory).getMeasuredHeight();

                   /* else if(ProfileFragment.profile){

                    }else if(ContactsFragment.createContact){

                    }*/
                    if (findViewById(R.id.popupEditMain).getVisibility() == View.VISIBLE) {
                        findViewById(R.id.popupEditMain).setVisibility(View.GONE);
                    }

                }

                if (clipboardPopup != null && clipboardPopup.getVisibility() == View.VISIBLE) {

                    globalSearchpopup.setVisibility(View.GONE);

                    if ((findViewById(R.id.popupProfileCompanyPossitions) != null && findViewById(R.id.popupProfileCompanyPossitions).getVisibility() == View.VISIBLE)) {
                        //contactsFragment.popupHelpCompanyposition.setVisibility(View.GONE);
                        clipboardPopup.setVisibility(View.GONE);
                        if (clibpboardAdapter != null) clibpboardAdapter.uncheck();
                        fabMenuContainer.setVisibility(View.VISIBLE);
                    }


                    if (clipboardPopup.findViewById(R.id.popupInfoClipboard).getMeasuredHeight() > findViewById(R.id.popupContainer).getMeasuredHeight()) {
                        ViewGroup.LayoutParams lp_1 = clipboardInfo.findViewById(R.id.popupInfoClipboard).getLayoutParams();
                        lp_1.height = (int) (155 * getApplicationContext().getResources().getDisplayMetrics().density);
                        clipboardPopup.findViewById(R.id.popupInfoClipboard).setLayoutParams(lp_1);
                    }

                    if (clipboardPopup.findViewById(R.id.popupEditClip).getMeasuredHeight() > findViewById(R.id.popupContainer).getMeasuredHeight()) {
                        ViewGroup.LayoutParams lp_2 = popupEditClip.findViewById(R.id.popupEditClip).getLayoutParams();
                        lp_2.height = (int) (155 * getApplicationContext().getResources().getDisplayMetrics().density);
                        clipboardPopup.findViewById(R.id.popupEditClip).setLayoutParams(lp_2);
                    }


                    check_k = false;
                    ViewGroup.LayoutParams lp = clipboardPopup.findViewById(R.id.popupContainer).getLayoutParams();

                    float px = 247 * getApplicationContext().getResources().getDisplayMetrics().density;

                    if (lp.height > px) {
                        lp.height = (int) px;
                        clipboardPopup.findViewById(R.id.popupContainer).setLayoutParams(lp);

                    }

                    //System.out.println("F2");
                    maxH = (int) px;
                    px_min = (247 - 50) * getApplicationContext().getResources().getDisplayMetrics().density;


                    if (!checkK2) {
                        lp.height = (int) px_min;
                        clipboardPopup.findViewById(R.id.popupContainer).setLayoutParams(lp);
                        checkK2 = true;
                    }

                    int[] location = new int[2];

                    findViewById(R.id.popupContainer).getLocationOnScreen(location);

                    bottomY[0] = location[1];
                    bottomY[0] += findViewById(R.id.popupContainer).getMeasuredHeight();

                   /* else if(ProfileFragment.profile){

                    }else if(ContactsFragment.createContact){

                    }*/
                    if (findViewById(R.id.popupEditMain).getVisibility() == View.VISIBLE) {
                        findViewById(R.id.popupEditMain).setVisibility(View.GONE);
                    }
                }

                isOpenSoftKey = true;

                FrameLayout mylayout = findViewById(R.id.hashtag_popup);
                FrameLayout mylayout2 = findViewById(R.id.favoriteContacts);
                FrameLayout mylayout3 = findViewById(R.id.popup_contacts);
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mylayout.getLayoutParams();
                ViewGroup.MarginLayoutParams params2 = (ViewGroup.MarginLayoutParams) mylayout2.getLayoutParams();
                ViewGroup.MarginLayoutParams params3 = (ViewGroup.MarginLayoutParams) mylayout3.getLayoutParams();

                float diL = 115f;
                Resources r1 = getResources();
                int px = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        diL,
                        r1.getDisplayMetrics()
                );


                float dipB = 51f;
                float dipB2 = 53f;
                Resources r2 = getResources();
                int px2 = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        dipB,
                        r2.getDisplayMetrics()
                );

                int px3 = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        dipB2,
                        r2.getDisplayMetrics()
                );

                params.setMargins(px, 0, 0, px2);
                params2.bottomMargin = px3;
                params3.bottomMargin = px3;

                params2.height = ViewGroup.LayoutParams.MATCH_PARENT;

                mylayout.setLayoutParams(params);
                mylayout2.setLayoutParams(params2);
                mylayout3.setLayoutParams(params3);
                findViewById(R.id.bottom_bar_to_close).setVisibility(View.GONE);

                findViewById(R.id.kb_close).setVisibility(View.GONE);

                findViewById(R.id.kb_account_close).setVisibility(View.GONE); ///new

                findViewById(R.id.kb_close2).setVisibility(View.GONE);


                // findViewById(R.id.kb_close_hs).setVisibility(View.GONE);
                findViewById(R.id.kb_open).setVisibility(View.VISIBLE);

                findViewById(R.id.kb_account_open).setVisibility(View.VISIBLE); //new

                findViewById(R.id.kb_close_h).setVisibility(View.GONE);

                findViewById(R.id.kb_open_h).setVisibility(View.VISIBLE);


                findViewById(R.id.kb_open2).setVisibility(View.VISIBLE);
//                changeMarginIfkeybardWasOpen();
                findViewById(R.id.fab).setVisibility(View.INVISIBLE);

                float pxx = 53 * getApplicationContext().getResources().getDisplayMetrics().density;

                ViewGroup.MarginLayoutParams paramss = (ViewGroup.MarginLayoutParams) findViewById(R.id.completeTagsPopup).getLayoutParams();
                paramss.setMargins(0, 0, 0, (int) pxx);
                findViewById(R.id.completeTagsPopup).setLayoutParams(paramss);

            } else {
                //System.out.println("F4");

                if (checkK2_h) {
                    ViewGroup.LayoutParams lp = historyPopup.findViewById(R.id.popupContainerHistory).getLayoutParams();

                    float pxx = 0;
                    pxx = 247 * getApplicationContext().getResources().getDisplayMetrics().density;


                    if (lp.height < pxx) {
                        lp.height = (int) pxx;
                        historyPopup.findViewById(R.id.popupContainerHistory).setLayoutParams(lp);

                    }
                }

                if (checkK2) {
                    ViewGroup.LayoutParams lp = clipboardPopup.findViewById(R.id.popupContainer).getLayoutParams();

                    float pxx = 0;

                   /* if(clipboardPopup.findViewById(R.id.textTopClipboard).getVisibility() == View.GONE)
                        pxx = 240 * getApplicationContext().getResources().getDisplayMetrics().density;
                    else*/
                    pxx = 247 * getApplicationContext().getResources().getDisplayMetrics().density;


                    if (lp.height < pxx) {
                        lp.height = (int) pxx;
                        clipboardPopup.findViewById(R.id.popupContainer).setLayoutParams(lp);

                    }
                }

                checkK2 = false;

                checkK2_h = false;

                if (!check_k_h) {
                    int[] location = new int[2];
                    findViewById(R.id.popupContainerHistory).getLocationOnScreen(location);

                    bottomY_h[0] = location[1];
                    bottomY_h[0] += findViewById(R.id.popupContainerHistory).getMeasuredHeight();

                    ViewGroup.LayoutParams lp = historyPopup.findViewById(R.id.popupContainerHistory).getLayoutParams();
                    float pxx = 240 * getApplicationContext().getResources().getDisplayMetrics().density;

                    if (globalSearchpopup.getVisibility() == View.GONE)
                        maxH_h = findViewById(R.id.main_content).getMeasuredHeight();
                    else if (globalSearchpopup.getVisibility() == View.VISIBLE)
                        maxH_h = findViewById(R.id.main_content).getMeasuredHeight() - globalSearchpopup.getMeasuredHeight();
                    px_min_h = (247 - 50) * getApplicationContext().getResources().getDisplayMetrics().density;

                    if (lp.height < px_min_h) {
                        lp.height = (int) px_min_h;
                        historyPopup.findViewById(R.id.popupContainerHistory).setLayoutParams(lp);

                    }


                }

                if (!check_k) {
                    int[] location = new int[2];
                    findViewById(R.id.popupContainer).getLocationOnScreen(location);

                    bottomY[0] = location[1];
                    bottomY[0] += findViewById(R.id.popupContainer).getMeasuredHeight();

                    ViewGroup.LayoutParams lp = clipboardPopup.findViewById(R.id.popupContainer).getLayoutParams();
                    float pxx = 240 * getApplicationContext().getResources().getDisplayMetrics().density;

                    if (globalSearchpopup.getVisibility() == View.GONE)
                        maxH = findViewById(R.id.main_content).getMeasuredHeight();
                    else if (globalSearchpopup.getVisibility() == View.VISIBLE)
                        maxH = findViewById(R.id.main_content).getMeasuredHeight() - globalSearchpopup.getMeasuredHeight();
                    px_min = (247 - 50) * getApplicationContext().getResources().getDisplayMetrics().density;

                    if (lp.height < px_min) {
                        lp.height = (int) px_min;
                        clipboardPopup.findViewById(R.id.popupContainer).setLayoutParams(lp);

                    }


                }

                isOpenSoftKey = false;
                FrameLayout mylayout = findViewById(R.id.hashtag_popup);
                FrameLayout mylayout2 = findViewById(R.id.favoriteContacts);
                FrameLayout mylayout3 = findViewById(R.id.popup_contacts);
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mylayout.getLayoutParams();
                ViewGroup.MarginLayoutParams params2 = (ViewGroup.MarginLayoutParams) mylayout2.getLayoutParams();
                ViewGroup.MarginLayoutParams params3 = (ViewGroup.MarginLayoutParams) mylayout3.getLayoutParams();

                float diL = 115f;
                Resources r1 = getResources();
                int px = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        diL,
                        r1.getDisplayMetrics()
                );


                float dipB = 116f;
                Resources r2 = getResources();
                int px2 = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        dipB,
                        r2.getDisplayMetrics()
                );

                params.setMargins(px, 0, 0, px2);
                params2.bottomMargin = px2;
                params3.bottomMargin = px2;

                params2.height = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        338,
                        r2.getDisplayMetrics()
                );

                mylayout.setLayoutParams(params);
                mylayout2.setLayoutParams(params2);
                mylayout3.setLayoutParams(params3);

                // when kb closed
                findViewById(R.id.bottom_bar_to_close).setVisibility(View.VISIBLE);
                findViewById(R.id.main_toolbar).setVisibility(View.VISIBLE);

                findViewById(R.id.kb_close).setVisibility(View.VISIBLE);

                findViewById(R.id.kb_account_close).setVisibility(View.VISIBLE);

                findViewById(R.id.kb_close2).setVisibility(View.VISIBLE);

                findViewById(R.id.kb_close_h).setVisibility(View.VISIBLE);

                findViewById(R.id.kb_open_h).setVisibility(View.GONE);


                //    findViewById(R.id.kb_close_hs).setVisibility(View.VISIBLE);
                findViewById(R.id.kb_open).setVisibility(View.GONE);

                findViewById(R.id.kb_account_open).setVisibility(View.GONE); ///new

                findViewById(R.id.kb_open2).setVisibility(View.GONE);
//                changeMarginIfkeybardWasClosed();
                findViewById(R.id.fab).setVisibility(View.VISIBLE);


                float pxx = 118 * getApplicationContext().getResources().getDisplayMetrics().density;
                ViewGroup.MarginLayoutParams paramss = (ViewGroup.MarginLayoutParams) findViewById(R.id.completeTagsPopup).getLayoutParams();
                paramss.setMargins(0, 0, 0, (int) pxx);
                findViewById(R.id.completeTagsPopup).setLayoutParams(paramss);

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.action_favorite: {


                break;
            }
        }
        return true;
    }


    public static void CloseStatusPopup() {
        EventBus.getDefault().post(new StatusPopupGone());
        OPENED_MENU_POPUP = true;
    }


    private void setImageToMenu(int selectedStatus) {
        try {
            MAIN_MENU.getItem(0).setIcon(selectedStatus);
        } catch (Exception e) {
        }
    }

    private void hideSortBar() {
        findViewById(R.id.sortBar).setVisibility(View.GONE);
    }

    private void showSortBar() {
        findViewById(R.id.sortBar).setVisibility(View.VISIBLE);
    }

    public void setProfileToContent() {
        changeToolbarToProfile();
        hideSortBar();
    }

    public void setBarToFiles() {
        changeToolbarToFile();
    }

    public void setCompanyToContent() {
        changeToolbarToCompany();
        hideSortBar();
    }

    public void setCreateToContent() {
        changeToolbarToCreate();
        hideSortBar();
    }

    public void setMergeToContent() {
        changeToolbarToMerge();
        hideSortBar();
    }

    private void defaultBottomBar() {

        findViewById(R.id.emails_block).setBackgroundColor(Color.TRANSPARENT);
        findViewById(R.id.log_block).setBackgroundColor(Color.TRANSPARENT);
        findViewById(R.id.contacts_block).setBackgroundColor(Color.TRANSPARENT);
        findViewById(R.id.timeline_block).setBackgroundColor(Color.TRANSPARENT);
        findViewById(R.id.schedule_block).setBackgroundColor(Color.TRANSPARENT);
        findViewById(R.id.places_block).setBackgroundColor(Color.TRANSPARENT);
        findViewById(R.id.files_block).setBackgroundColor(Color.TRANSPARENT);
        findViewById(R.id.orders_block).setBackgroundColor(Color.TRANSPARENT);
    }

    public void applyBottomAndTopBar() {

        final int SELECTED_BLOCK_COLOR = Color.parseColor("#94B3E1");

        defaultBottomBar();

        switch (selectedType) {
            case EMAILS:
                selectedFragment = emailsFragment;
                viewFlipper.setDisplayedChild(4);
                findViewById(R.id.emails_block).setBackgroundColor(SELECTED_BLOCK_COLOR);
                break;
            case LOG:
                selectedFragment = logFragment;
                findViewById(R.id.log_block).setBackgroundColor(SELECTED_BLOCK_COLOR);
                break;
            case CONTACTS:
                selectedFragment = contactsFragment;
                viewFlipper.setDisplayedChild(0);
                findViewById(R.id.contacts_block).setBackgroundColor(SELECTED_BLOCK_COLOR);
                break;
            case TIMELINE:
                selectedFragment = timeLineFragment;
                viewFlipper.setDisplayedChild(1);
                findViewById(R.id.timeline_block).setBackgroundColor(SELECTED_BLOCK_COLOR);
                break;
            case SCHEDULE:
                selectedFragment = scheduleFragment;
                findViewById(R.id.schedule_block).setBackgroundColor(SELECTED_BLOCK_COLOR);
                break;
            case PLACES:
                selectedFragment = placesFragment;
                viewFlipper.setDisplayedChild(3);
                findViewById(R.id.places_block).setBackgroundColor(SELECTED_BLOCK_COLOR);
                break;
            case FILES:
                selectedFragment = filesFragment;
                viewFlipper.setDisplayedChild(2);
                findViewById(R.id.files_block).setBackgroundColor(SELECTED_BLOCK_COLOR);
                break;
            case ORDERS:
                selectedFragment = ordersFragment;
                findViewById(R.id.orders_block).setBackgroundColor(SELECTED_BLOCK_COLOR);
                break;
        }
    }

    public void applySelection() {
        final int SELECTED_BLOCK_COLOR = Color.parseColor("#94B3E1");

        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        defaultBottomBar();

        if (emailsFragment == null) emailsFragment = new EmailsFragment();

        switch (selectedType) {
            case EMAILS:
                if (emailsFragment == null) emailsFragment = new EmailsFragment();
                ((PopupsInter) selectedFragment).closeOtherPopup();
                selectedFragment = emailsFragment;
                if (!selectedFragment.isAdded()) {
                    fragmentTransaction.replace(R.id.main_content, selectedFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commitAllowingStateLoss();
                }
                //viewFlipper.setDisplayedChild(4);
                findViewById(R.id.emails_block).setBackgroundColor(SELECTED_BLOCK_COLOR);
                break;
            case LOG:
                if (logFragment == null) logFragment = new LogFragment();
                ((PopupsInter) selectedFragment).closeOtherPopup();
                selectedFragment = logFragment;
                if (!selectedFragment.isAdded()) {
                    fragmentTransaction.replace(R.id.main_content, selectedFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commitAllowingStateLoss();
                }
                findViewById(R.id.log_block).setBackgroundColor(SELECTED_BLOCK_COLOR);
                break;
            case CONTACTS:
                if (contactsFragment == null)
                    contactsFragment = new ContactsFragment();
                try {
                    ((PopupsInter) selectedFragment).closeOtherPopup();
                } catch (Exception e) {

                }
                //  ((FileBarInter) selectedFragment).closeOtherPopups();
                selectedFragment = contactsFragment;
                viewFlipper.setDisplayedChild(0);

                findViewById(R.id.cancel_toolbar).setVisibility(View.GONE);


                if (!selectedFragment.isAdded()) {
                    fragmentTransaction.replace(R.id.main_content, selectedFragment);
                    fragmentTransaction.commitAllowingStateLoss();
                }
                findViewById(R.id.contacts_block).setBackgroundColor(SELECTED_BLOCK_COLOR);
                break;
            case TIMELINE:
                if (timeLineFragment == null) timeLineFragment = new TimeLineFragment();
                ((PopupsInter) selectedFragment).closeOtherPopup();
                //   ((FileBarInter) selectedFragment).closeOtherPopups();
                selectedFragment = timeLineFragment;

                findViewById(R.id.fabMenuContainer).setVisibility(View.GONE);


                //viewFlipper.setDisplayedChild(1);
                if (!selectedFragment.isAdded()) {
                    fragmentTransaction.replace(R.id.main_content, selectedFragment);
                    fragmentTransaction.addToBackStack("timeline");
                    fragmentTransaction.commitAllowingStateLoss();
                }
                findViewById(R.id.timeline_block).setBackgroundColor(SELECTED_BLOCK_COLOR);
                break;
            case SCHEDULE:
                if (scheduleFragment == null) scheduleFragment = new ScheduleFragment();
                ((PopupsInter) selectedFragment).closeOtherPopup();
                selectedFragment = scheduleFragment;
                if (!selectedFragment.isAdded()) {
                    fragmentTransaction.replace(R.id.main_content, selectedFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commitAllowingStateLoss();
                }
                findViewById(R.id.schedule_block).setBackgroundColor(SELECTED_BLOCK_COLOR);
                break;
            case PLACES:
                if (placesFragment == null) placesFragment = new PlacesFragment();
                ((PopupsInter) selectedFragment).closeOtherPopup();
                //   ((FileBarInter) selectedFragment).closeOtherPopups();
                selectedFragment = placesFragment;
                if (!selectedFragment.isAdded()) {
                    fragmentTransaction.replace(R.id.main_content, selectedFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commitAllowingStateLoss();
                }
                viewFlipper.setDisplayedChild(3);
                findViewById(R.id.places_block).setBackgroundColor(SELECTED_BLOCK_COLOR);
                break;
            case FILES:
                if (filesFragment == null) filesFragment = new FilesFragment();
                ((PopupsInter) selectedFragment).closeOtherPopup();
                //      ((FileBarInter) selectedFragment).closeOtherPopups();
                selectedFragment = filesFragment;
                if (!selectedFragment.isAdded()) {
                    fragmentTransaction.replace(R.id.main_content, selectedFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commitAllowingStateLoss();
                }
                viewFlipper.setDisplayedChild(2);
                findViewById(R.id.files_block).setBackgroundColor(SELECTED_BLOCK_COLOR);
                break;
            case ORDERS:
                if (ordersFragment == null) ordersFragment = new OrdersFragment();
                ((PopupsInter) selectedFragment).closeOtherPopup();
                selectedFragment = ordersFragment;
                if (!selectedFragment.isAdded()) {
                    fragmentTransaction.replace(R.id.main_content, selectedFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commitAllowingStateLoss();
                }
                findViewById(R.id.orders_block).setBackgroundColor(SELECTED_BLOCK_COLOR);
                break;
        }
    }

    public void setContactsToContent() {
        if (!ContactAdapter.checkMerge)
            changeToolbarToContacts();
        showSortBar();
    }

    public void hideViewFlipper() {

        viewFlipper.setVisibility(View.GONE);

        //findViewById(R.id.sortBar).setVisibility(View.GONE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SaveClipboard event) {
        System.out.println("SAVE BUFER");
        ArrayList<Clipboard> listSave = clibpboardAdapter.getClipboards();
        ArrayList<Clipboard> listOpen = clibpboardAdapter.getClipOpen();
        ArrayList<Clipboard> listMain = new ArrayList<>();
        if (listOpen != null) {
            for (int q = 0; q < listSave.size(); q++) {
                boolean check = true;
                for (int i = 0; i < listOpen.size(); i++) {
                    //listSave.removeAll(listOpen.get(i).getListClipboards());
                    if (listOpen.get(i).getListClipboards().contains(listSave.get(q)))
                        check = false;
                }
                if (check) listMain.add(listSave.get(q));

            }
        } else listMain = listSave;


        if (FabNotificationService.clipboard == null)
            FabNotificationService.clipboard = new ArrayList<>();

        FabNotificationService.clipboard.clear();
        FabNotificationService.clipboard.addAll(listMain);

        ArrayList<Clipboard> finalListMain = listMain;

        new Thread(new Runnable() {
            @Override
            public void run() {
                //Gson gson = new Gson();


                System.out.println("BEFORE GSON");
                //String jsonClipboard = gson.toJson(finalListMain);

                ParceToJsonClipboard parceToJsonClipboard = new ParceToJsonClipboard(finalListMain);
                String jsonClipboard = parceToJsonClipboard.getJson();
                System.out.println("AFTER GSON");


                SharedPreferences mSettings = getSharedPreferences(CONTACTS_PREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = mSettings.edit();
                editor.putString(CLIPBOARD, jsonClipboard);
                editor.apply();

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
            }
        }).start();

    }

    private void updateClipboardRecycler() {
        clipboard = new ArrayList<>();

        if (FabNotificationService.clipboard == null || clipboardRecycler == null || clibpboardAdapter == null) {
            initClipboardRecycler();
            return;
        }

        clipboard.addAll(FabNotificationService.clipboard);
        clipboard.removeAll(Collections.singleton(null));
        clibpboardAdapter.updateClipboards(clipboard);

        try {
            LinearLayoutManager myLayoutManager = (LinearLayoutManager) clipboardRecycler.getLayoutManager();
            scrollPos = myLayoutManager.findFirstVisibleItemPosition();

            if (scrollPos > 0 && checkNewClipboard) {
                findViewById(R.id.circle_new_clip).setVisibility(View.VISIBLE);
                clipboardPopup.findViewById(R.id.scrollToCopieed).setVisibility(View.VISIBLE);
            } else {
                clipboardPopup.findViewById(R.id.scrollToCopieed).setVisibility(View.GONE);
                findViewById(R.id.circle_new_clip).setVisibility(View.GONE);
                if (clipboardPopup.getVisibility() == View.VISIBLE) {
                    //findViewById(R.id.circle_new_clip).setVisibility(View.GONE);
                    checkNewClipboard = false;
                    if (FabNotificationService.countNewCLips > 0) {
                        FabNotificationService.countNewCLips = 0;
                        EventBus.getDefault().post(new SetDefaultPush());
                    }

                }
                if (checkNewClipboard && clipboardPopup.getVisibility() == View.GONE) {
                    findViewById(R.id.circle_new_clip).setVisibility(View.VISIBLE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void addHistoryEntry(String str) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                history.add(0, ClipboardType.getClipboardHistory(str, MainActivity.this));
                if (history.get(0) != null) {

                    if (history.get(0).getListClipboards() != null && !history.get(0).getListClipboards().isEmpty()) {
                        for (Clipboard clipboard : history.get(0).getListClipboards()) {
                            if (clipboard != null) {
                                Contact c = ContactCacheService.find2(clipboard.getValueCopy(), clipboard.getType(), null);
                                if (c != null)
                                    clipboard.addContactToListSearch(c);
                            }
                        }
                    } else {
                        Contact c = ContactCacheService.find2(history.get(0).getValueCopy(), history.get(0).getType(), null);
                        if (c != null)
                            history.get(0).addContactToListSearch(c);
                    }

                }

                if (history != null && history.size() > 3) {
                    for (int i = 1; i < 3; i++) {
                        try {
                            if (history.get(0).getValueCopy().equals(history.get(i).getValueCopy())) {
                                history.set(0, history.get(i));
                                history.remove(i);
                                i--;
                            }
                        } catch (Exception e) {
                            System.out.println("ERROR REMOVE HISTORY");
                            e.printStackTrace();
                        }
                    }
                }


                EventBus.getDefault().post(new SaveAndUpdateHistoryList());

            }
        }).start();

    }

    private void saveHistory() {


        new Thread(new Runnable() {
            @Override
            public void run() {

                ArrayList<Clipboard> listSave = historyAdapter.getClipboards();
                ArrayList<Clipboard> listOpen = historyAdapter.getClipOpen();
                ArrayList<Clipboard> listMain = new ArrayList<>();
                if (listOpen != null) {
                    for (int q = 0; q < listSave.size(); q++) {
                        boolean check = true;
                        for (int i = 0; i < listOpen.size(); i++) {
                            //listSave.removeAll(listOpen.get(i).getListClipboards());
                            if (listOpen.get(i).getListClipboards().contains(listSave.get(q)))
                                check = false;
                        }
                        if (check) listMain.add(listSave.get(q));

                    }
                } else listMain = listSave;


                ArrayList<Clipboard> finalListMain = listMain;


                System.out.println("BEFORE GSON HISTORY");

                ParceToJsonClipboard parceToJsonClipboard = new ParceToJsonClipboard(finalListMain);
                String jsonClipboard = parceToJsonClipboard.getJson();

                System.out.println("AFTER GSON HISTORY");

                SharedPreferences mSettings = getSharedPreferences(CONTACTS_PREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = mSettings.edit();
                editor.putString(HISTORY, jsonClipboard);
                editor.apply();


            }
        }).start();

    }

    private void initHistoryrecycler() {
        history = new ArrayList<>();

        SharedPreferences mSettings;
        mSettings = this.getSharedPreferences(CONTACTS_PREFERENCES, Context.MODE_PRIVATE);


        if (!mSettings.getString(HISTORY, "").isEmpty()) {
            String spData = mSettings.getString(HISTORY, "");

            if (spData.isEmpty())
                history = new ArrayList<>();
            else {
                history.addAll(new Gson().fromJson(spData, new TypeToken<ArrayList<Clipboard>>() {

                }.getType()));
            }
        } else System.out.println("EMPTY HISTORY");

        if (historyRecycler == null) {
            historyRecycler = findViewById(R.id.historyRV);
            historyRecycler.setLayoutManager(new LinearLayoutManager(this));
        }

        history.removeAll(Collections.singleton(null));


        if (historyAdapter == null) {
            historyAdapter = new ClibpboardAdapter(history, this, historyInfo, popupEditHistory, contactsFragment, popupChangeTypeHistory, historyRecycler, globalSearchpopup, MagicType.HISTORY);
            historyRecycler.setAdapter(historyAdapter);
        } else {
            historyAdapter.updateClipboards(history);

        }


        historyPopup.findViewById(R.id.frameCloseHistory).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                historyInfo.setVisibility(View.GONE);
                popupEditHistory.setVisibility(View.GONE);
                popupChangeTypeHistory.setVisibility(View.GONE);
                historyAdapter.updateCount();
            }
        });

    }

    public void updateContactSwipe(Clipboard clip_) {


        Contact contact = null;
        Contact contactFromRealm = null;
        int count_upd = 0;

        contact = ProfileFragment.selectedContact;
        contactFromRealm = ContactCacheService.getContactByIdAndWithoutRealm(ProfileFragment.selectedContact.getId());


        Realm realm = Realm.getDefaultInstance(); //-


        realm.beginTransaction();
        if (clip_.getType().equals(ClipboardEnum.NAME)) {
            contactsService.updateName(contact.getIdContact(), null, clip_.getValueCopy().trim());
            contact.setName(clip_.getValueCopy().trim());
            if (clip_.getListContactsSearch() == null || clip_.getListContactsSearch().isEmpty()) {
                contactFromRealm = ContactCacheService.getContactByIdAndWithoutRealm(ProfileFragment.selectedContact.getId());


                contactFromRealm = ContactCacheService.getContactByIdAndWithoutRealm(ProfileFragment.selectedContact.getId());


                clip_.addContactToListSearch(contactFromRealm);
            }
            count_upd++;
        }

        if (clip_.getType().equals(ClipboardEnum.FACEBOOK)) {
            if (contact.getSocialModel() == null)
                contact.setSocialModel(realm.createObject(SocialModel.class));

            if (contact.getSocialModel().getFacebookLink() != null && !contact.getSocialModel().getFacebookLink().isEmpty() /*&& contact.hasFacebook*/ && !SocialEq.checkFacebookSocial(SocialEq.getSub(clip_.getValueCopy().trim()), contact)) {
                //contactsService.updateNote(contact.getIdContact(), contact.getSocialModel().getFacebookLink(), extrator.getValueCopy().trim());
                boolean check = true;
                for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                    if (SocialEq.checkStrSocials(contactInfo.value.trim(), clip_.getValueCopy().trim()))
                        check = false;
                }
                if (check) {

                    contact.addNote(clip_.getValueCopy());
                    contactsService.addNoteToContact(contact.getIdContact(), clip_.getValueCopy().trim(), contact.getName());
                    count_upd++;
                }
            } else if (contact.getSocialModel().getFacebookLink() == null || contact.getSocialModel().getFacebookLink().isEmpty()) {
                contact.getSocialModel().setFacebookLink(clip_.getValueCopy());
                contactsService.addNoteToContact(contact.getIdContact(), clip_.getValueCopy(), contact.getName());
                count_upd++;
            }


            contact.hasFacebook = true;
            if (clip_.getListContactsSearch() == null || clip_.getListContactsSearch().isEmpty())
                clip_.addContactToListSearch(contactFromRealm);

        }

        if (clip_.getType().equals(ClipboardEnum.INSTAGRAM)) {
            if (contact.getSocialModel() == null)
                contact.setSocialModel(realm.createObject(SocialModel.class));

            if (contact.getSocialModel().getInstagramLink() != null && !contact.getSocialModel().getInstagramLink().isEmpty() /*&& contact.hasInst */ && !SocialEq.checkInstaSocial(SocialEq.getSub(clip_.getValueCopy().trim()), contact)) {
                //contactsService.updateNote(contact.getIdContact(), contact.getSocialModel().getInstagramLink(), extrator.getValueCopy().trim());

                boolean check = true;
                for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                    if (SocialEq.checkStrSocials(contactInfo.value.trim(), clip_.getValueCopy().trim()))
                        check = false;
                }
                if (check) {
                    contact.addNote(clip_.getValueCopy());
                    contactsService.addNoteToContact(contact.getIdContact(), clip_.getValueCopy().trim(), contact.getName());
                    count_upd++;
                }


            } else if (contact.getSocialModel().getInstagramLink() == null || contact.getSocialModel().getInstagramLink().isEmpty()) {
                contactsService.addNoteToContact(contact.getIdContact(), clip_.getValueCopy().trim(), contact.getName());
                contact.getSocialModel().setInstagramLink(clip_.getValueCopy());
                count_upd++;
            }

            if (clip_.getListContactsSearch() == null || clip_.getListContactsSearch().isEmpty())
                clip_.addContactToListSearch(contactFromRealm);
            contact.hasInst = true;
        }

        if (clip_.getType().equals(ClipboardEnum.YOUTUBE)) {
            if (clip_.getValueCopy().contains("user") || clip_.getValueCopy().contains("channel")) {
                if (contact.getSocialModel() == null)
                    contact.setSocialModel(realm.createObject(SocialModel.class));

                if (contact.getSocialModel().getYoutubeLink() != null && !contact.getSocialModel().getYoutubeLink().isEmpty() /*&& contact.hasYoutube*/ && !SocialEq.checkYoutubeSocial(SocialEq.getSub(clip_.getValueCopy().trim()), contact)) {
                    boolean check = true;
                    for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                        if (SocialEq.checkStrSocials(contactInfo.value.trim(), clip_.getValueCopy().trim()))
                            check = false;
                    }
                    if (check) {
                        contact.addNote(clip_.getValueCopy());
                        contactsService.addNoteToContact(contact.getIdContact(), clip_.getValueCopy().trim(), contact.getName());
                        count_upd++;
                    }


                } else if (contact.getSocialModel().getYoutubeLink() == null || contact.getSocialModel().getYoutubeLink().isEmpty()) {
                    contactsService.addNoteToContact(contact.getIdContact(), clip_.getValueCopy().trim(), contact.getName());
                    contact.getSocialModel().setYoutubeLink(clip_.getValueCopy());
                    count_upd++;
                }


                contact.hasYoutube = true;
            } else {
                boolean check = true;
                for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                    if (SocialEq.checkStrSocials(contactInfo.value.trim(), clip_.getValueCopy().trim()))
                        check = false;
                }
                if (check) {
                    contact.addNote(clip_.getValueCopy());
                    contactsService.addNoteToContact(contact.getIdContact(), clip_.getValueCopy().trim(), contact.getName());
                    count_upd++;
                }
            }
            if (clip_.getListContactsSearch() == null || clip_.getListContactsSearch().isEmpty())
                clip_.addContactToListSearch(contactFromRealm);
        }

        if (clip_.getType().equals(ClipboardEnum.MEDIUM)) {
            if (clip_.getValueCopy().contains("com/@")) {
                if (contact.getSocialModel() == null)
                    contact.setSocialModel(realm.createObject(SocialModel.class));

                if (contact.getSocialModel().getMediumLink() != null && !contact.getSocialModel().getMediumLink().isEmpty() /*&& contact.hasMedium*/ && !SocialEq.checkMediumSocail(SocialEq.getSub(clip_.getValueCopy().trim()), contact)) {
                    boolean check = true;
                    for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                        if (SocialEq.checkStrSocials(contactInfo.value.trim(), clip_.getValueCopy().trim()))
                            check = false;
                    }
                    if (check) {
                        contact.addNote(clip_.getValueCopy());
                        contactsService.addNoteToContact(contact.getIdContact(), clip_.getValueCopy().trim(), contact.getName());
                        count_upd++;
                    }


                } else if (contact.getSocialModel().getMediumLink() == null || contact.getSocialModel().getMediumLink().isEmpty()) {
                    contactsService.addNoteToContact(contact.getIdContact(), clip_.getValueCopy().trim(), contact.getName());
                    contact.getSocialModel().setMediumLink(clip_.getValueCopy());
                    count_upd++;
                }


                contact.hasMedium = true;
            } else {
                boolean check = true;
                for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                    if (SocialEq.checkStrSocials(contactInfo.value.trim(), clip_.getValueCopy().trim()))
                        check = false;
                }
                if (check) {
                    contact.addNote(clip_.getValueCopy());
                    contactsService.addNoteToContact(contact.getIdContact(), clip_.getValueCopy().trim(), contact.getName());
                    count_upd++;
                }
            }
            if (clip_.getListContactsSearch() == null || clip_.getListContactsSearch().isEmpty())
                clip_.addContactToListSearch(contactFromRealm);
        }

        if (clip_.getType().equals(ClipboardEnum.TWITTER)) {
            if (contact.getSocialModel() == null)
                contact.setSocialModel(realm.createObject(SocialModel.class));

            if (contact.getSocialModel().getTwitterLink() != null && !contact.getSocialModel().getTwitterLink().isEmpty() /*&& contact.hasTwitter*/ && !SocialEq.checkTwitterSocial(SocialEq.getSub(clip_.getValueCopy().trim()), contact)) {
                boolean check = true;
                for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                    if (SocialEq.checkStrSocials(contactInfo.value.trim(), clip_.getValueCopy().trim()))
                        check = false;
                }
                if (check) {
                    contact.addNote(clip_.getValueCopy());
                    contactsService.addNoteToContact(contact.getIdContact(), clip_.getValueCopy().trim(), contact.getName());
                    count_upd++;
                }


            } else if (contact.getSocialModel().getTwitterLink() == null || contact.getSocialModel().getTwitterLink().isEmpty()) {
                contactsService.addNoteToContact(contact.getIdContact(), clip_.getValueCopy().trim(), contact.getName());
                contact.getSocialModel().setTwitterLink(clip_.getValueCopy());
                count_upd++;
            }

            if (clip_.getListContactsSearch() == null || clip_.getListContactsSearch().isEmpty())
                clip_.addContactToListSearch(contactFromRealm);
            contact.hasTwitter = true;
        }


        if (clip_.getType().equals(ClipboardEnum.VK)) {
            if (contact.getSocialModel() == null)
                contact.setSocialModel(realm.createObject(SocialModel.class));

            if (contact.getSocialModel().getVkLink() != null && !contact.getSocialModel().getVkLink().isEmpty() /*&& contact.hasVk*/ && !SocialEq.checkVkSocial(SocialEq.getSub(clip_.getValueCopy().trim()), contact)) {
                //contactsService.updateNote(contact.getIdContact(), contact.getSocialModel().getVkLink(), extrator.getValueCopy().trim());

                boolean check = true;
                for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                    if (SocialEq.checkStrSocials(contactInfo.value.trim(), clip_.getValueCopy().trim()))
                        check = false;
                }
                if (check) {
                    contact.addNote(clip_.getValueCopy());
                    contactsService.addNoteToContact(contact.getIdContact(), clip_.getValueCopy().trim(), contact.getName());
                    count_upd++;
                }

            } else if (contact.getSocialModel().getVkLink() == null || contact.getSocialModel().getVkLink().isEmpty()) {
                contactsService.addNoteToContact(contact.getIdContact(), clip_.getValueCopy(), contact.getName());
                contact.getSocialModel().setVkLink(clip_.getValueCopy());
                count_upd++;

            }


            contact.hasVk = true;
            if (clip_.getListContactsSearch() == null || clip_.getListContactsSearch().isEmpty())
                clip_.addContactToListSearch(contactFromRealm);
        }

        if (clip_.getType().equals(ClipboardEnum.LINKEDIN)) {

            if (contact.getSocialModel() == null)
                contact.setSocialModel(realm.createObject(SocialModel.class));

            if (contact.getSocialModel().getLinkedInLink() != null && !contact.getSocialModel().getLinkedInLink().isEmpty() /*&& contact.hasLinked*/ && !SocialEq.checkLinkedInSocial(SocialEq.getSub(clip_.getValueCopy().trim()), contact)) {
                //contactsService.updateNote(contact.getIdContact(), contact.getSocialModel().getLinkedInLink(), extrator.getValueCopy().trim());

                boolean check = true;
                for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                    if (SocialEq.checkStrSocials(contactInfo.value.trim(), clip_.getValueCopy().trim()))
                        check = false;
                }
                if (check) {
                    contact.addNote(clip_.getValueCopy());
                    contactsService.addNoteToContact(contact.getIdContact(), clip_.getValueCopy().trim(), contact.getName());
                    count_upd++;
                }

            } else if (contact.getSocialModel().getLinkedInLink() == null || contact.getSocialModel().getLinkedInLink().isEmpty()) {
                contactsService.addNoteToContact(contact.getIdContact(), clip_.getValueCopy(), contact.getName());
                contact.getSocialModel().setLinkedInLink(clip_.getValueCopy());
                count_upd++;
            }


            contact.hasLinked = true;
            if (clip_.getListContactsSearch() == null || clip_.getListContactsSearch().isEmpty())
                clip_.addContactToListSearch(contactFromRealm);
        }

        if (clip_.getType().equals(ClipboardEnum.SKYPE)) {
            if (contact.getSocialModel() == null)
                contact.setSocialModel(realm.createObject(SocialModel.class));

            if (contact.getSocialModel().getSkypeLink() != null && !contact.getSocialModel().getSkypeLink().isEmpty() && contact.hasSkype && !contact.getSocialModel().getSkypeLink().equalsIgnoreCase(clip_.getValueCopy().trim())) {
                //contactsService.updateNote(contact.getIdContact(), contact.getSocialModel().getLinkedInLink(), extrator.getValueCopy().trim());

                contactsService.deleteNoteContact(contact.getIdContact(), "Skype: " + contact.getSocialModel().getSkypeLink());


            } /*else if (contact.getSocialModel().getSkypeLink() == null || contact.getSocialModel().getSkypeLink().isEmpty()) {*/
            contactsService.addNoteToContact(contact.getIdContact(), "Skype: " + clip_.getValueCopy(), contact.getName());
            contact.getSocialModel().setSkypeLink(clip_.getValueCopy());
            count_upd++;
            //}


            contact.hasSkype = true;
            if (clip_.getListContactsSearch() == null || clip_.getListContactsSearch().isEmpty())
                clip_.addContactToListSearch(contactFromRealm);
        }

        if (clip_.getType().equals(ClipboardEnum.TELEGRAM)) {

            boolean check = true;
            for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                if (contactInfo.value.trim().equals(clip_.getValueCopy().trim()))
                    check = false;
            }
            if (check) {
                contact.addNote(clip_.getValueCopy());
                contactsService.addNoteToContact(contact.getIdContact(), clip_.getValueCopy().trim(), contact.getName());
                count_upd++;
            }

        }

        if (clip_.getType().equals(ClipboardEnum.VIBER)) {

            boolean check = true;
            for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                if (contactInfo.value.trim().equals(clip_.getValueCopy().trim()))
                    check = false;
            }
            if (check) {
                contact.addNote(clip_.getValueCopy());
                contactsService.addNoteToContact(contact.getIdContact(), clip_.getValueCopy().trim(), contact.getName());
                count_upd++;
            }

        }

        if (clip_.getType().equals(ClipboardEnum.WHATSAPP)) {

            boolean check = true;
            for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                if (contactInfo.value.trim().equals(clip_.getValueCopy().trim()))
                    check = false;
            }
            if (check) {
                contact.addNote(clip_.getValueCopy());
                contactsService.addNoteToContact(contact.getIdContact(), clip_.getValueCopy().trim(), contact.getName());
                count_upd++;
            }

        }


        if (clip_.getType().equals(ClipboardEnum.PHONE)) {
            boolean check = true;
            for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                if (contactInfo.value.trim().equals(clip_.getValueCopy().trim())) {
                    check = false;
                    continue;
                }


                String str = contactInfo.value.trim().replaceAll("[\\s\\-\\+\\(\\)]", "");
                String str2 = clip_.getValueCopy().trim().replaceAll("[\\s\\-\\+\\(\\)]", "");

                if ((str.charAt(0) == '7' || str.charAt(0) == '8') && str.length() == 11) {
                    str = str.substring(1);
                }
                if ((str2.charAt(0) == '7' || str2.charAt(0) == '8') && str2.length() == 11) {
                    str2 = str2.substring(1);
                }

                if (str.equalsIgnoreCase(str2)) {
                    check = false;
                    continue;
                }


                if (contactInfo.type.equals("phone") && contactInfo.value.equals("+000000000000")) {
                    contactInfo.value = clip_.getValueCopy();
                    contactsService.updatePhone(contact.getIdContact(), "+000000000000", clip_.getValueCopy().trim());
                    count_upd++;
                    check = false;
                    continue;
                }
            }
            if (check) {
                contact.addPhone(clip_.getValueCopy());
                String id_S = contact.getIdContact();
                String val = clip_.getValueCopy().trim();
                String name_S = contact.getName();

                contactsService.addPhoneToContact(id_S, val, name_S);
                count_upd++;


            }
            if (clip_.getListContactsSearch() == null || clip_.getListContactsSearch().isEmpty())
                clip_.addContactToListSearch(contactFromRealm);
        }

        if (clip_.getType().equals(ClipboardEnum.EMAIL)) {
            boolean check = true;
            for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                if (contactInfo.value.trim().equals(clip_.getValueCopy().trim()))
                    check = false;
            }
            if (check) {
                contact.addEmail(clip_.getValueCopy());
                contactsService.addMailToContact(contact.getIdContact(), clip_.getValueCopy().trim(), contact.getName());
                count_upd++;
            }
            if (clip_.getListContactsSearch() == null || clip_.getListContactsSearch().isEmpty())
                clip_.addContactToListSearch(contactFromRealm);
        }

        if (clip_.getType().equals(ClipboardEnum.GITHUB)) {
            boolean check = true;
            for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                if (contactInfo.value.trim().equals(clip_.getValueCopy().trim()))
                    check = false;
            }
            if (check) {
                contact.addNote(clip_.getValueCopy());
                contactsService.addNoteToContact(contact.getIdContact(), clip_.getValueCopy().trim(), contact.getName());
                count_upd++;
            }
            if (clip_.getListContactsSearch() == null || clip_.getListContactsSearch().isEmpty())
                clip_.addContactToListSearch(contactFromRealm);
        }

        if (clip_.getType().equals(ClipboardEnum.ANGEL)) {
            boolean check = true;
            for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                if (contactInfo.value.trim().equals(clip_.getValueCopy().trim()))
                    check = false;
            }
            if (check) {
                contact.addNote(clip_.getValueCopy());
                contactsService.addNoteToContact(contact.getIdContact(), clip_.getValueCopy().trim(), contact.getName());
                count_upd++;
            }
            if (clip_.getListContactsSearch() == null || clip_.getListContactsSearch().isEmpty())
                clip_.addContactToListSearch(contactFromRealm);
        }

        if (clip_.getType().equals(ClipboardEnum.TUMBLR)) {
            boolean check = true;
            for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                if (contactInfo.value.trim().equals(clip_.getValueCopy().trim()))
                    check = false;
            }
            if (check) {
                contact.addNote(clip_.getValueCopy());
                contactsService.addNoteToContact(contact.getIdContact(), clip_.getValueCopy().trim(), contact.getName());
                count_upd++;
            }
            if (clip_.getListContactsSearch() == null || clip_.getListContactsSearch().isEmpty())
                clip_.addContactToListSearch(contactFromRealm);
        }

        if (clip_.getType().equals(ClipboardEnum.G_DOC)) {
            boolean check = true;
            for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                if (contactInfo.value.trim().equals(clip_.getValueCopy().trim()))
                    check = false;
            }
            if (check) {
                contact.addNote(clip_.getValueCopy());
                contactsService.addNoteToContact(contact.getIdContact(), clip_.getValueCopy().trim(), contact.getName());
                count_upd++;
            }
            if (clip_.getListContactsSearch() == null || clip_.getListContactsSearch().isEmpty())
                clip_.addContactToListSearch(contactFromRealm);
        }

        if (clip_.getType().equals(ClipboardEnum.G_SHEET)) {
            boolean check = true;
            for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                if (contactInfo.value.trim().equals(clip_.getValueCopy().trim()))
                    check = false;
            }
            if (check) {
                contact.addNote(clip_.getValueCopy());
                contactsService.addNoteToContact(contact.getIdContact(), clip_.getValueCopy().trim(), contact.getName());
                count_upd++;
            }
            if (clip_.getListContactsSearch() == null || clip_.getListContactsSearch().isEmpty())
                clip_.addContactToListSearch(contactFromRealm);
        }


        if (clip_.getType().equals(ClipboardEnum.WEB)) {
            boolean check = true;
            for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                if (contactInfo.value.trim().equals(clip_.getValueCopy().trim()))
                    check = false;
            }
            if (check) {
                contact.addNote(clip_.getValueCopy());
                contactsService.addNoteToContact(contact.getIdContact(), clip_.getValueCopy().trim(), contact.getName());
                count_upd++;
            }
            if (clip_.getListContactsSearch() == null || clip_.getListContactsSearch().isEmpty())
                clip_.addContactToListSearch(contactFromRealm);
        }

        if (clip_.getType().equals(ClipboardEnum.NOTE)) {
            boolean check = true;
            for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                if (contactInfo.value.trim().equals(clip_.getValueCopy().trim()))
                    check = false;
            }
            if (check) {
                contact.addNote(clip_.getValueCopy());
                contactsService.addNoteToContact(contact.getIdContact(), clip_.getValueCopy().trim(), contact.getName());
                count_upd++;
            }
        }

        if (clip_.getType().equals(ClipboardEnum.BIO_INTRO)) {
            boolean check = true;
            for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                if (contactInfo.value.trim().equals(clip_.getValueCopy().trim()))
                    check = false;
            }
            if (check) {
                contact.addBio(clip_.getValueCopy());
                //contactsService.addNoteToContact(contact.getIdContact(), clip_.getValueCopy().trim(), contact.getName());
                count_upd++;
            }
        }


        if (clip_.getType().equals(ClipboardEnum.HASHTAG)) {
            String[] hashtags = clip_.getValueCopy().split(" ");
            if (contact.getListOfHashtags() == null || contact.getListOfHashtags().isEmpty()) {
                RealmList<HashTag> listH = new RealmList<>();
                for (String str : hashtags) {

                    HashTag hashtag = realm.createObject(HashTag.class);
                    hashtag.setDate(new Date());
                    hashtag.setHashTagValue(str);

                    listH.add(hashtag);

                    contactsService.addNoteToContact(contact.getIdContact(), str, contact.getName());

                    count_upd++;
                }
                contact.setListOfHashtags(listH);
            } else {
                for (String str : hashtags) {
                    boolean check = false;
                    for (HashTag h : contact.getListOfHashtags()) {
                        if (h.getHashTagValue().equals(str)) {
                            check = true;
                            break;
                        }
                    }
                    if (!check) {

                        HashTag hashtag = realm.createObject(HashTag.class);
                        hashtag.setDate(new Date());
                        hashtag.setHashTagValue(str);

                        contact.getListOfHashtags().add(hashtag);
                        contactsService.addNoteToContact(contact.getIdContact(), str, contact.getName());

                        count_upd++;
                    }
                }
            }
        }

        if (clip_.getType().equals(ClipboardEnum.ADDRESS)) {
            boolean check = true;
            for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                if (contactInfo.value.trim().equals(clip_.getValueCopy().trim()))
                    check = false;
            }
            if (check) {
                contact.addAddress(clip_.getValueCopy());
                contactsService.addAddressToContact(contact.getIdContact(), clip_.getValueCopy().trim(), contact.getName());
                count_upd++;
            }
        }


        if (clip_.getType().equals(ClipboardEnum.DATEOFBIRTH)) {
            boolean check = true;
            for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                if (contactInfo.value.trim().equals(clip_.getValueCopy().trim()))
                    check = false;
            }
            if (check) {
                contact.addDateBirth(clip_.getValueCopy());
                contactsService.addNoteToContact(contact.getIdContact(), clip_.getValueCopy().trim(), contact.getName());
                count_upd++;
            }
        }

        if (clip_.getType().equals(ClipboardEnum.BIO_INTRO)) {
            boolean check = true;
            for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                if (contactInfo.value.trim().equals(clip_.getValueCopy().trim()))
                    check = false;
            }
            if (check) {
                contact.addBio(clip_.getValueCopy());
                //contactsService.addNoteToContact(contact.getIdContact(), clip_.getValueCopy().trim(), contact.getName());
                count_upd++;
            }
        }
        try {
            realm.commitTransaction();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        if (clip_.getType().equals(ClipboardEnum.COMPANY)) {
            String oldComp = contact.getCompany();
            realm.beginTransaction();
            contact.setCompany(clip_.getValueCopy().trim());
            realm.commitTransaction();

            boolean findCompany = false;
            //===========================================
            for (Contact contactC : ContactCacheService.getCompanies()) {
                if (contactC.getName().toLowerCase().compareTo(contact.getCompany().toLowerCase()) == 0) {
                    boolean ch = false;
                    for (int i = 0; i < contactC.listOfContacts.size(); i++) {
                        if (contactC.listOfContacts.get(i).getId() == contact.getId()) {
                            realm.beginTransaction();
                            contactC.listOfContacts.set(i, contact);
                            realm.commitTransaction();


                            ch = true;
                        }
                    }
                    if (!ch) {
                        realm.beginTransaction();
                        contactC.listOfContacts.add(contact);
                        realm.commitTransaction();

                    }

                    count_upd++;
                    findCompany = true;
                } else if (oldComp != null && contactC.getName().toLowerCase().compareTo(oldComp.toLowerCase()) == 0) {
                    //  realm.commitTransaction();

                    Contact comp = ContactCacheService.getCompany(oldComp);
                    Contact ct = ContactCacheService.getContactById(contact.getId());

                    if (comp != null) {
                        if (comp.listOfContacts.size() == 1) {

                            contactsFragment.contactAdapter.removeContactById(comp);

                        }
                        MainActivity.checkDel = ContactCacheService.removeContactFromCompany(comp, ct);
                    }

                    count_upd++;
                }
            }
            if (!findCompany) {

                Date date = new Date();
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                Time time = getRandomDate();
                time.setHours(cal.get(Calendar.HOUR_OF_DAY));
                time.setMinutes(cal.get(Calendar.MINUTE));
                time.setSeconds(cal.get(Calendar.SECOND));

                Contact company = new Contact(date);
                company.setName(contact.getCompany().trim());
                //    company.time = getRandomDate();
                company.time = time.toString();
                company.color = Color.rgb(Math.abs(contact.getCompany().hashCode() * 28439) % 255, Math.abs(contact.getCompany().hashCode() * 211239) % 255, Math.abs(contact.getCompany().hashCode() * 42368) % 255);
                RealmList<Contact> listForSaveContact = new RealmList<>();
                listForSaveContact.add(contact);
                company.listOfContacts = listForSaveContact;
                //ContactsFragment.listOfContacts.add(company);
                //   realm.commitTransaction();
                ContactCacheService.updateCompany(company);

                if (oldComp != null) {
                    Contact oldCompany = ContactCacheService.getCompany(oldComp);
                    if (oldCompany != null) {
                        if (oldCompany.listOfContacts.size() == 1 && oldCompany.listOfContacts.contains(contact)) {
                            contactsFragment.contactAdapter.removeContactById(oldCompany);
                            //=================
                        }
                        ContactCacheService.removeContactFromCompany(oldCompany, contact);
                    }
                }

                Contact comm = ContactCacheService.getCompany(company.getName());

                       /* contactAdapter.getListOfContacts().add(comm);
                        contactAdapter.getSavedList().add(comm);*/

                contactsFragment.contactAdapter.addContact(comm);

                count_upd++;
            }

            //===============================================================


            ContactsFragment.UPD_ALL = true;

            contactsService.deleteCompany_Possition(contact.getIdContact());
            if (contact.getCompany() != null && !contact.getCompany().isEmpty())
                contactsService.insertCompany(contact.getIdContact(), contact.getCompany(), contact.getName());

            if (contact.getCompanyPossition() != null && !contact.getCompanyPossition().isEmpty() && (contact.getCompany() != null && !contact.getCompany().isEmpty()))
                contactsService.addComp(contact.getIdContact(), contact.getCompanyPossition(), contact.getCompany());

            if (contact.getCompanyPossition() != null && !contact.getCompanyPossition().isEmpty() && (contact.getCompany() == null || contact.getCompany().isEmpty()))
                contactsService.insertPosition(contact.getIdContact(), contact.getCompanyPossition(), contact.getName());


        }
        realm.beginTransaction();

        if (clip_.getType().equals(ClipboardEnum.POSITION)) {

            contact.setCompanyPossition(clip_.getValueCopy().trim());

            count_upd++;

            contactsService.deleteCompany_Possition(contact.getIdContact());
            if (contact.getCompany() != null && !contact.getCompany().isEmpty())
                contactsService.insertCompany(contact.getIdContact(), contact.getCompany(), contact.getName());

            if (contact.getCompanyPossition() != null && !contact.getCompanyPossition().isEmpty() && (contact.getCompany() != null && !contact.getCompany().isEmpty()))
                contactsService.addComp(contact.getIdContact(), contact.getCompanyPossition(), contact.getCompany());

            if (contact.getCompanyPossition() != null && !contact.getCompanyPossition().isEmpty() && (contact.getCompany() == null || contact.getCompany().isEmpty()))
                contactsService.insertPosition(contact.getIdContact(), contact.getCompanyPossition(), contact.getName());


        }


        realm.commitTransaction();


        realm.close();


        clibpboardAdapter.notifyDataSetChanged();


        if (contactsFragment.profilePopUp != null && contactsFragment.profilePopUp.getVisibility() == View.INVISIBLE) {

            contactsFragment.profilePopUp.setVisibility(View.GONE);
        }

        boolean editMode = false;
        if (ProfileFragment.nowEdit) editMode = true;

        if (!ProfileFragment.nowEdit) {

            EventBus.getDefault().post(new UpdateContactInProfile(contact));
        } else {
            EventBus.getDefault().post(new UpdateContactInProfile(contact));
        }

        int finalCount_upd = count_upd;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (finalCount_upd > 0)
                    Toast.makeText(contactsFragment.getContext(), clip_.getType().toString().toLowerCase() + " successfully updated", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(contactsFragment.getContext(), "Data already exists", Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void openDialodUpdateProfile(ArrayList<DataUpdate> list, Clipboard clipboard) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View view = getLayoutInflater().inflate(R.layout.clipboard_swipe_update_dialog, null);

        final float scale = getResources().getDisplayMetrics().density;

        builder.setView(view);
        AlertDialog alertDialog = builder.create();

        RecyclerView recyclerView = view.findViewById(R.id.recycleUpdate);
        ViewGroup.LayoutParams params = recyclerView.getLayoutParams();

        params.height = (int) (55 * scale);


        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        DialogAdapter dialogAdapter = new DialogAdapter(list, this);
        recyclerView.setAdapter(dialogAdapter);

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {

                updateContactSwipe(clipboard);

            }
        });


        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "No", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {

            }
        });

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button negButton = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_NEGATIVE);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(0, 0, 50, 0);
                negButton.setLayoutParams(params);
            }
        });

        alertDialog.show();

    }

    private void initClipboardRecycler() {
        clipboard = new ArrayList<>();
        //clipboard.clear();
        storage = new Storage(getApplicationContext());
        //boolean fileExists = storage.isFileExist(path + "/Extime/ExtimeContacts/backupClipboard");


        boolean fileExists = false;
        List<File> listBack = storage.getFiles(path + "/Extime/ExtimeContacts/");
        if (listBack != null) {
            for (int i = 0; i < listBack.size(); i++) {
                if (listBack.get(i).getName().contains("backupClipboard_") || listBack.get(i).getName().equalsIgnoreCase("backupClipboard")) {
                    fileExists = true;
                }
            }
        }


        if (clipboardRecycler == null) {
            clipboardRecycler = findViewById(R.id.clipboardRV);
            clipboardRecycler.setLayoutManager(new LinearLayoutManager(this));
        }


        try {
            if (FabNotificationService.clipboard == null) {

                Gson gson = new Gson();
                SharedPreferences mSettings;
                mSettings = this.getSharedPreferences(CONTACTS_PREFERENCES, Context.MODE_PRIVATE);

                if (!mSettings.getString(CLIPBOARD, "").isEmpty()) {
                    String spData = "";
                    if (!mSettings.getString(CLIPBOARD, "").isEmpty()) {
                        spData = mSettings.getString(CLIPBOARD, "");

                    }
                    if (spData.isEmpty())
                        clipboard = new ArrayList<>();
                    else {
                        clipboard.addAll(gson.fromJson(spData, new TypeToken<ArrayList<Clipboard>>() {
                        }.getType()));
                    }


                    if (fileExists) {
                        String nameBackup = null;
                        List<File> listBacks = storage.getFiles(path + "/Extime/ExtimeContacts/");
                        if (listBacks != null) {
                            for (int i = 0; i < listBacks.size(); i++) {
                                if (listBacks.get(i).getName().contains("backupClipboard_") || listBacks.get(i).getName().equalsIgnoreCase("backupClipboard")) {
                                    nameBackup = listBacks.get(i).getName();
                                }
                            }
                        }


                        String spData2 = storage.readTextFile(path + "/Extime/ExtimeContacts/" + nameBackup);

                        if (spData2 != null) {
                            JSONArray js = new JSONArray(spData2);
                            if (js.length() > clipboard.size() + 10) {
                                clipboard = new ArrayList<>();
                                clipboard.addAll(gson.fromJson(spData2, new TypeToken<ArrayList<Clipboard>>() {
                                }.getType()));

                                String jsonClipboard = gson.toJson(clipboard);   //error java.lang.IllegalStateException: Realm access from incorrect thread. Realm objects can only be accessed on the thread they were created.
                                mSettings = this.getSharedPreferences(CONTACTS_PREFERENCES, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = mSettings.edit();
                                editor.putString(CLIPBOARD, jsonClipboard);
                                editor.apply();
                            }
                        }
                    }

                    FabNotificationService.clipboard = new ArrayList<>();
                    FabNotificationService.clipboard.addAll(clipboard);

                    ArrayList<Clipboard> listClips = (ArrayList<Clipboard>) clipboard.clone();
                    String jsonClipboard = gson.toJson(listClips);


                    if (!MainActivity.processFileClipboard) {
                        MainActivity.processFileClipboard = true;
                        storage.deleteFile(path + "/Extime/ExtimeContacts/backupClipboard_" + MainActivity.versionDebug);
                        storage.createFile(path + "/Extime/ExtimeContacts/backupClipboard_" + MainActivity.versionDebug, jsonClipboard);
                        MainActivity.processFileClipboard = false;
                    }


                    //storage.deleteFile(path + "/Extime/ExtimeContacts/backupClipboard");
                    //storage.createFile(path + "/Extime/ExtimeContacts/backupClipboard", jsonClipboard);
                } else if (fileExists) {


                    String nameBackup = null;
                    List<File> listBacks = storage.getFiles(path + "/Extime/ExtimeContacts/");
                    if (listBacks != null) {
                        for (int i = 0; i < listBacks.size(); i++) {
                            if (listBacks.get(i).getName().contains("backupClipboard_") || listBacks.get(i).getName().equalsIgnoreCase("backupClipboard")) {
                                nameBackup = listBacks.get(i).getName();
                            }
                        }
                    }


                    String spData = storage.readTextFile(path + "/Extime/ExtimeContacts/" + nameBackup);


                    if (spData == null || spData.isEmpty())
                        clipboard = new ArrayList<>();
                    else {

                        clipboard.addAll(gson.fromJson(spData, new TypeToken<ArrayList<Clipboard>>() {
                        }.getType()));

                    }

                    FabNotificationService.clipboard = new ArrayList<>();
                    FabNotificationService.clipboard.addAll(clipboard);


                    String jsonClipboard = gson.toJson(clipboard);   //error java.lang.IllegalStateException: Realm access from incorrect thread. Realm objects can only be accessed on the thread they were created.

                    mSettings = this.getSharedPreferences(CONTACTS_PREFERENCES, Context.MODE_PRIVATE);

                    SharedPreferences.Editor editor = mSettings.edit();
                    editor.putString(CLIPBOARD, jsonClipboard);
                    editor.apply();

                }

            } else {
                if (fileExists) {

                    String nameBackup = null;
                    List<File> listBacks = storage.getFiles(path + "/Extime/ExtimeContacts/");
                    if (listBacks != null) {
                        for (int i = 0; i < listBacks.size(); i++) {
                            if (listBacks.get(i).getName().contains("backupClipboard_") || listBacks.get(i).getName().equalsIgnoreCase("backupClipboard")) {
                                nameBackup = listBacks.get(i).getName();
                            }
                        }
                    }


                    String spData = storage.readTextFile(path + "/Extime/ExtimeContacts/" + nameBackup);

                    if (spData != null) {
                        JSONArray js = new JSONArray(spData);
                        if (js.length() > FabNotificationService.clipboard.size() + 10) {
                            Gson gson = new Gson();
                            clipboard.addAll(gson.fromJson(spData, new TypeToken<ArrayList<Clipboard>>() {
                            }.getType()));

                            FabNotificationService.clipboard = new ArrayList<>();
                            FabNotificationService.clipboard.addAll(clipboard);

                            SharedPreferences mSettings;
                            String jsonClipboard = gson.toJson(clipboard);   //error java.lang.IllegalStateException: Realm access from incorrect thread. Realm objects can only be accessed on the thread they were created.
                            mSettings = this.getSharedPreferences(CONTACTS_PREFERENCES, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = mSettings.edit();
                            editor.putString(CLIPBOARD, jsonClipboard);
                            editor.apply();
                        } else {
                            clipboard.addAll(FabNotificationService.clipboard);
                        }
                    } else {
                        clipboard.addAll(FabNotificationService.clipboard);
                    }
                } else {

                    clipboard.addAll(FabNotificationService.clipboard);
                }
            }

            clipboard.removeAll(Collections.singleton(null));


            if (clibpboardAdapter == null) {
                clibpboardAdapter = new ClibpboardAdapter(clipboard, this, clipboardInfo, popupEditClip, contactsFragment, popupChangeType, clipboardRecycler, globalSearchpopup, MagicType.CLIPBOARD);
                clipboardRecycler.setAdapter(clibpboardAdapter);
            } else {
                clibpboardAdapter.updateClipboards(clipboard);

            }

            //swipe


            SwipeController swipeController = new SwipeController(new SwipeControllerActions() {
                @Override
                public void swipe(int position) {


                    if (position == -1) return;


                    if (ProfileFragment.profile && !clibpboardAdapter.getClipboards().get(position).getType().equals(ClipboardEnum.GROUP) && !ProfileFragment.company) {
                        ArrayList<DataUpdate> updateArrayList = new ArrayList<>();
                        DataUpdate dataUpdate = new DataUpdate(clibpboardAdapter.getClipboards().get(position).getValueCopy(), clibpboardAdapter.getClipboards().get(position).getType());
                        updateArrayList.add(dataUpdate);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        openDialodUpdateProfile(updateArrayList, clibpboardAdapter.getClipboards().get(position));
                                    }
                                });

                            }
                        }).start();

                    }
                }

                @Override
                public void swipeLeft(int position) {
                    if (position == -1) return;
                    ((EditText) findViewById(R.id.magic_edit_text)).setText(clibpboardAdapter.getClipboards().get(position).getValueCopy());


                }
            });


            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeController);

            itemTouchHelper.attachToRecyclerView(clipboardRecycler);


            //==========


            LinearLayoutManager myLayoutManager = (LinearLayoutManager) clipboardRecycler.getLayoutManager();
            scrollPos = myLayoutManager.findFirstVisibleItemPosition();

            if (scrollPos > 0 && checkNewClipboard) {
                clipboardPopup.findViewById(R.id.scrollToCopieed).setVisibility(View.VISIBLE);
                findViewById(R.id.circle_new_clip).setVisibility(View.VISIBLE);
            } else {
                clipboardPopup.findViewById(R.id.scrollToCopieed).setVisibility(View.GONE);
                findViewById(R.id.circle_new_clip).setVisibility(View.GONE);
                if (clipboardPopup.getVisibility() == View.VISIBLE) {
                    checkNewClipboard = false;
                    if (FabNotificationService.countNewCLips > 0) {
                        FabNotificationService.countNewCLips = 0;
                        EventBus.getDefault().post(new SetDefaultPush());
                    }
                    // findViewById(R.id.circle_new_clip).setVisibility(View.GONE);
                }
            }


            clipboardRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    LinearLayoutManager myLayoutManager = (LinearLayoutManager) clipboardRecycler.getLayoutManager();
                    int p = myLayoutManager.findFirstVisibleItemPosition();

                    if (p == 0 && clipboardPopup.findViewById(R.id.scrollToCopieed).getVisibility() == View.VISIBLE) {
                        clipboardPopup.findViewById(R.id.scrollToCopieed).setVisibility(View.GONE);
                        findViewById(R.id.circle_new_clip).setVisibility(View.GONE);
                        checkNewClipboard = false;
                        if (FabNotificationService.countNewCLips > 0) {
                            FabNotificationService.countNewCLips = 0;
                            EventBus.getDefault().post(new SetDefaultPush());
                        }
                    }

                }
            });


            //clipboardRecycler.setAdapter(clibpboardAdapter);

        } catch (Exception e) {
            System.out.println("ERROR GET CLIPS");
            e.printStackTrace();
        }

        clipboardPopup.findViewById(R.id.frameCloseClip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                clipboardInfo.setVisibility(View.GONE);
                popupEditClip.setVisibility(View.GONE);
                popupChangeType.setVisibility(View.GONE);
                clibpboardAdapter.updateCount();
            }
        });

        clipboardPopup.findViewById(R.id.copyClipData).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Clipboard> listClip = clibpboardAdapter.getClipbaordList();
                String exportData = "";
                for (Clipboard clip : listClip) {
                    if (clip.getListClipboards() == null || clip.getListClipboards().isEmpty())
                        exportData += clip.getValueCopy() + " " + "\n";
                }
                contactsFragment.copyToClipboard(MainActivity.this, exportData);
                clibpboardAdapter.clearCheck();
                Toast.makeText(MainActivity.this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });

        clipboardPopup.findViewById(R.id.removeClipData).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (clibpboardAdapter != null) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            contactsFragment.getContext());
                    String t = "Do you want to delete clipboards?";
                    if (clibpboardAdapter.getClipbaordList().size() == 1)
                        t = "Do you want to delete clipboard?";
                    alertDialogBuilder.setTitle(t);
                    alertDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("Yes", (dialog, id) -> {


                                ArrayList<Clipboard> listOpen = clibpboardAdapter.getClipOpen();
                                ArrayList<Clipboard> listClip = clibpboardAdapter.getClipbaordList();
                                clibpboardAdapter.clearCheck();

                                for (int j = 0; j < listOpen.size(); j++) {
                                    for (int i = 0; i < listClip.size(); i++) {
                                        if (listOpen.get(j).getListClipboards().contains(listClip.get(i))) {

                                            listOpen.get(j).getListClipboards().remove(listClip.get(i));

                                            if (listOpen.get(j).getListClipboards().isEmpty()) {

                                                for (int q = 0; q < clipboard.size(); q++) {
                                                    if (clipboard.get(q).getValueCopy().equals(listOpen.get(j).getValueCopy()))
                                                        clipboard.remove(q);
                                                }
                                                clibpboardAdapter.getClipOpen().remove(j);
                                                //listOpen.remove(j);
                                                if (listOpen.size() == 0) {
                                                    break;

                                                }
                                            } else {
                                                for (Clipboard c : clipboard) {
                                                    if (c.getValueCopy().equals(listOpen.get(j).getValueCopy()))
                                                        c.setListClipboards(listOpen.get(j).getListClipboards());
                                                }
                                            }
                                        }
                                    }
                                }

                                for (int i = 0; i < listClip.size(); i++) {
                                    if (listOpen.contains(listClip.get(i))) {
                                        for (int j = 0; j < listClip.get(i).getListClipboards().size(); j++) {
                                            clipboard.remove(listClip.get(i).getListClipboards().get(j));
                                        }
                                    }
                                }

                                for (int i = 0; i < clipboard.size(); i++) {

                                    if (listClip.contains(clipboard.get(i))) {
                                        clipboard.remove(i);
                                        i--;
                                    }
                                }

                                if (clipboard.size() == 0) {
                                    clipboardPopup.findViewById(R.id.removeClipData).setVisibility(View.GONE);
                                    clipboardPopup.findViewById(R.id.shareClipData).setVisibility(View.GONE);
                                    clipboardPopup.findViewById(R.id.copyClipData).setVisibility(View.GONE);
                                    clipboardPopup.findViewById(R.id.statClipboard).setVisibility(View.VISIBLE);
                                    ((TextView) clipboardPopup.findViewById(R.id.statClipboard)).setText(clibpboardAdapter.getCountCLipboards() + " entries, " + clibpboardAdapter.getCountDetectedClipboards() + " detected");
                                }


                                Toast.makeText(contactsFragment.getContext(), "Delete success", Toast.LENGTH_SHORT).show();
                                findViewById(R.id.textTopClipboard).setVisibility(View.INVISIBLE);
                                clibpboardAdapter.updateClipboards(clipboard);

                                //EventBus.getDefault().post(new SaveClipboard());


                            })
                            .setNegativeButton("No", (dialog, id) -> dialog.cancel());

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            }
        });


        clipboardPopup.findViewById(R.id.shareClipData).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<Clipboard> listClip = clibpboardAdapter.getClipbaordList();
                String exportData = "";
                for (Clipboard clip : listClip) {
                    exportData += clip.getValueCopy() + "\n\n";
                }

                exportData += "\n\n";
                exportData += "Data shared via http://Extime.pro\n";


                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, exportData);
                startActivity(Intent.createChooser(shareIntent, "Поделиться контактом"));

            }
        });

        popupEditClip.findViewById(R.id.cancelEditField).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupEditClip.setVisibility(View.GONE);
                clipboardInfo.setVisibility(View.VISIBLE);

            }
        });

        //==========================
        clipboardPopup.findViewById(R.id.createContactClipboard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                createContact(MagicType.CLIPBOARD);

            }
        });


        clipboardPopup.findViewById(R.id.updateContactClipboard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateContact(MagicType.CLIPBOARD);
            }
        });

    }

    private void updateContact(MagicType magicType) {

        if ((ProfileFragment.profile && ProfileFragment.selectedContact != null && ((ProfileFragment.selectedContact.listOfContacts == null || ProfileFragment.selectedContact.listOfContacts.isEmpty()))) || (contactsFragment.contactAdapter.getSelectContactID() != null && (contactsFragment.profilePopUp != null && contactsFragment.profilePopUp.getVisibility() == View.VISIBLE)) /*|| ContactsFragment.createContact*/) {

            ArrayList<Clipboard> listClipForUpd;
            if (magicType.equals(MagicType.CLIPBOARD))
                listClipForUpd = clibpboardAdapter.getClipbaordList();
            else
                listClipForUpd = historyAdapter.getClipbaordList();

            int co = 0;
            for (Clipboard cl : listClipForUpd) {
                if (cl.getListClipboards() == null || cl.getListClipboards().isEmpty()) {
                    co++;
                }
            }

            if (co > 0) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        contactsFragment.getContext());
                alertDialogBuilder.setTitle("Update contact with " + co + " new data fields?");
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Yes", (dialog, id) -> {

                            Contact contact = null;
                            Contact contactFromRealm = null;
                            int count_upd = 0;
                            int fragmentVisibility;
                            try {
                                fragmentVisibility = contactsFragment.profilePopUp.getVisibility();
                            } catch (NullPointerException exception) {
                                exception.printStackTrace();
                                fragmentVisibility = View.INVISIBLE;
                            }
                            if (contactsFragment.contactAdapter.getSelectContactID() != null && (contactsFragment.profilePopUp != null && fragmentVisibility == View.VISIBLE)) {
                                try {
                                    contact = ContactCacheService.getContactById(Long.parseLong(contactsFragment.contactAdapter.getSelectContactID()));
                                    contactFromRealm = ContactCacheService.getContactByIdAndWithoutRealm(Long.parseLong(contactsFragment.contactAdapter.getSelectContactID()));
                                } catch (Exception e) {
                                    return;
                                }
                            } else if (ProfileFragment.profile && ProfileFragment.selectedContact != null && ((ProfileFragment.selectedContact.listOfContacts == null || ProfileFragment.selectedContact.listOfContacts.isEmpty()))) {
                                contact = ProfileFragment.selectedContact;
                                contactFromRealm = ContactCacheService.getContactByIdAndWithoutRealm(ProfileFragment.selectedContact.getId());

                            }/*else if(ContactsFragment.createContact && CreateFragment.createdContact != null){
                                        contact = CreateFragment.createdContact;
                                    }*/

                            //ContactCacheService.getContactByIdAndWithoutRealm();


                            Realm realm = Realm.getDefaultInstance(); //-

                            for (Clipboard extrator : listClipForUpd) {
                                if (extrator.getListClipboards() != null && !extrator.getListClipboards().isEmpty())
                                    continue;

                                realm.beginTransaction();
                                if (extrator.getType().equals(ClipboardEnum.NAME)) {
                                    contactsService.updateName(contact.getIdContact(), null, extrator.getValueCopy().trim());
                                    contact.setName(extrator.getValueCopy().trim());
                                    if (extrator.getListContactsSearch() == null || extrator.getListContactsSearch().isEmpty()) {
                                        contactFromRealm = ContactCacheService.getContactByIdAndWithoutRealm(ProfileFragment.selectedContact.getId());


                                        if (contactsFragment.contactAdapter.getSelectContactID() != null && (contactsFragment.profilePopUp.getVisibility() == View.VISIBLE)) {
                                            try {
                                                contactFromRealm = ContactCacheService.getContactByIdAndWithoutRealm(Long.parseLong(contactsFragment.contactAdapter.getSelectContactID()));
                                            } catch (Exception e) {
                                                return;
                                            }
                                        } else if (ProfileFragment.profile && ProfileFragment.selectedContact != null && ((ProfileFragment.selectedContact.listOfContacts == null || ProfileFragment.selectedContact.listOfContacts.isEmpty()))) {
                                            contactFromRealm = ContactCacheService.getContactByIdAndWithoutRealm(ProfileFragment.selectedContact.getId());
                                        }

                                        extrator.addContactToListSearch(contactFromRealm);
                                    }
                                    count_upd++;
                                }

                                if (extrator.getType().equals(ClipboardEnum.FACEBOOK)) {
                                    if (contact.getSocialModel() == null)
                                        contact.setSocialModel(realm.createObject(SocialModel.class));

                                    if (contact.getSocialModel().getFacebookLink() != null && !contact.getSocialModel().getFacebookLink().isEmpty() /*&& contact.hasFacebook*/ && !SocialEq.checkFacebookSocial(SocialEq.getSub(extrator.getValueCopy().trim()), contact)) {
                                        //contactsService.updateNote(contact.getIdContact(), contact.getSocialModel().getFacebookLink(), extrator.getValueCopy().trim());
                                        boolean check = true;
                                        for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                            if (SocialEq.checkStrSocials(contactInfo.value.trim(), extrator.getValueCopy().trim()))
                                                check = false;
                                        }
                                        if (check) {

                                            contact.addNote(extrator.getValueCopy());
                                            contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim(), contact.getName());
                                            count_upd++;
                                        }
                                    } else if (contact.getSocialModel().getFacebookLink() == null || contact.getSocialModel().getFacebookLink().isEmpty()) {
                                        contact.getSocialModel().setFacebookLink(extrator.getValueCopy());
                                        contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy(), contact.getName());
                                        count_upd++;
                                    }


                                    contact.hasFacebook = true;
                                    if (extrator.getListContactsSearch() == null || extrator.getListContactsSearch().isEmpty())
                                        extrator.addContactToListSearch(contactFromRealm);

                                    //if(extrator.getNameFromSocial() != null) contact.setName(extrator.getNameFromSocial());
                                    //ParsingClipboard parsingSocial= new ParsingClipboard(extrator.getValueCopy().toString(), SocialEnums.FACEBOOK);
                                    //parsingSocial.execute();
                                }

                                if (extrator.getType().equals(ClipboardEnum.INSTAGRAM)) {
                                    if (contact.getSocialModel() == null)
                                        contact.setSocialModel(realm.createObject(SocialModel.class));

                                    if (contact.getSocialModel().getInstagramLink() != null && !contact.getSocialModel().getInstagramLink().isEmpty() /*&& contact.hasInst */ && !SocialEq.checkInstaSocial(SocialEq.getSub(extrator.getValueCopy().trim()), contact)) {
                                        //contactsService.updateNote(contact.getIdContact(), contact.getSocialModel().getInstagramLink(), extrator.getValueCopy().trim());

                                        boolean check = true;
                                        for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                            if (SocialEq.checkStrSocials(contactInfo.value.trim(), extrator.getValueCopy().trim()))
                                                check = false;
                                        }
                                        if (check) {
                                            contact.addNote(extrator.getValueCopy());
                                            contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim(), contact.getName());
                                            count_upd++;
                                        }


                                    } else if (contact.getSocialModel().getInstagramLink() == null || contact.getSocialModel().getInstagramLink().isEmpty()) {
                                        contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim(), contact.getName());
                                        contact.getSocialModel().setInstagramLink(extrator.getValueCopy());
                                        count_upd++;
                                    }

                                    if (extrator.getListContactsSearch() == null || extrator.getListContactsSearch().isEmpty())
                                        extrator.addContactToListSearch(contactFromRealm);
                                    contact.hasInst = true;
                                }

                                if (extrator.getType().equals(ClipboardEnum.YOUTUBE)) {
                                    if (extrator.getValueCopy().contains("user") || extrator.getValueCopy().contains("channel")) {
                                        if (contact.getSocialModel() == null)
                                            contact.setSocialModel(realm.createObject(SocialModel.class));

                                        if (contact.getSocialModel().getYoutubeLink() != null && !contact.getSocialModel().getYoutubeLink().isEmpty() /*&& contact.hasYoutube*/ && !SocialEq.checkYoutubeSocial(SocialEq.getSub(extrator.getValueCopy().trim()), contact)) {
                                            boolean check = true;
                                            for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                                if (SocialEq.checkStrSocials(contactInfo.value.trim(), extrator.getValueCopy().trim()))
                                                    check = false;
                                            }
                                            if (check) {
                                                contact.addNote(extrator.getValueCopy());
                                                contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim(), contact.getName());
                                                count_upd++;
                                            }


                                        } else if (contact.getSocialModel().getYoutubeLink() == null || contact.getSocialModel().getYoutubeLink().isEmpty()) {
                                            contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim(), contact.getName());
                                            contact.getSocialModel().setYoutubeLink(extrator.getValueCopy());
                                            count_upd++;
                                        }


                                        contact.hasYoutube = true;
                                    } else {
                                        boolean check = true;
                                        for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                            if (SocialEq.checkStrSocials(contactInfo.value.trim(), extrator.getValueCopy().trim()))
                                                check = false;
                                        }
                                        if (check) {
                                            contact.addNote(extrator.getValueCopy());
                                            contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim(), contact.getName());
                                            count_upd++;
                                        }
                                    }
                                    if (extrator.getListContactsSearch() == null || extrator.getListContactsSearch().isEmpty())
                                        extrator.addContactToListSearch(contactFromRealm);
                                }

                                if (extrator.getType().equals(ClipboardEnum.MEDIUM)) {
                                    if (extrator.getValueCopy().contains("com/@")) {
                                        if (contact.getSocialModel() == null)
                                            contact.setSocialModel(realm.createObject(SocialModel.class));

                                        if (contact.getSocialModel().getMediumLink() != null && !contact.getSocialModel().getMediumLink().isEmpty() /*&& contact.hasMedium*/ && !SocialEq.checkMediumSocail(SocialEq.getSub(extrator.getValueCopy().trim()), contact)) {
                                            boolean check = true;
                                            for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                                if (SocialEq.checkStrSocials(contactInfo.value.trim(), extrator.getValueCopy().trim()))
                                                    check = false;
                                            }
                                            if (check) {
                                                contact.addNote(extrator.getValueCopy());
                                                contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim(), contact.getName());
                                                count_upd++;
                                            }


                                        } else if (contact.getSocialModel().getMediumLink() == null || contact.getSocialModel().getMediumLink().isEmpty()) {
                                            contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim(), contact.getName());
                                            contact.getSocialModel().setMediumLink(extrator.getValueCopy());
                                            count_upd++;
                                        }


                                        contact.hasMedium = true;
                                    } else {
                                        boolean check = true;
                                        for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                            if (SocialEq.checkStrSocials(contactInfo.value.trim(), extrator.getValueCopy().trim()))
                                                check = false;
                                        }
                                        if (check) {
                                            contact.addNote(extrator.getValueCopy());
                                            contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim(), contact.getName());
                                            count_upd++;
                                        }
                                    }
                                    if (extrator.getListContactsSearch() == null || extrator.getListContactsSearch().isEmpty())
                                        extrator.addContactToListSearch(contactFromRealm);
                                }

                                if (extrator.getType().equals(ClipboardEnum.TWITTER)) {
                                    if (contact.getSocialModel() == null)
                                        contact.setSocialModel(realm.createObject(SocialModel.class));

                                    if (contact.getSocialModel().getTwitterLink() != null && !contact.getSocialModel().getTwitterLink().isEmpty() /*&& contact.hasTwitter*/ && !SocialEq.checkTwitterSocial(SocialEq.getSub(extrator.getValueCopy().trim()), contact)) {
                                        boolean check = true;
                                        for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                            if (SocialEq.checkStrSocials(contactInfo.value.trim(), extrator.getValueCopy().trim()))
                                                check = false;
                                        }
                                        if (check) {
                                            contact.addNote(extrator.getValueCopy());
                                            contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim(), contact.getName());
                                            count_upd++;
                                        }


                                    } else if (contact.getSocialModel().getTwitterLink() == null || contact.getSocialModel().getTwitterLink().isEmpty()) {
                                        contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim(), contact.getName());
                                        contact.getSocialModel().setTwitterLink(extrator.getValueCopy());
                                        count_upd++;
                                    }

                                    if (extrator.getListContactsSearch() == null || extrator.getListContactsSearch().isEmpty())
                                        extrator.addContactToListSearch(contactFromRealm);
                                    contact.hasTwitter = true;
                                }


                                if (extrator.getType().equals(ClipboardEnum.VK)) {
                                    if (contact.getSocialModel() == null)
                                        contact.setSocialModel(realm.createObject(SocialModel.class));

                                    if (contact.getSocialModel().getVkLink() != null && !contact.getSocialModel().getVkLink().isEmpty() /*&& contact.hasVk*/ && !SocialEq.checkVkSocial(SocialEq.getSub(extrator.getValueCopy().trim()), contact)) {
                                        //contactsService.updateNote(contact.getIdContact(), contact.getSocialModel().getVkLink(), extrator.getValueCopy().trim());

                                        boolean check = true;
                                        for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                            if (SocialEq.checkStrSocials(contactInfo.value.trim(), extrator.getValueCopy().trim()))
                                                check = false;
                                        }
                                        if (check) {
                                            contact.addNote(extrator.getValueCopy());
                                            contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim(), contact.getName());
                                            count_upd++;
                                        }

                                    } else if (contact.getSocialModel().getVkLink() == null || contact.getSocialModel().getVkLink().isEmpty()) {
                                        contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy(), contact.getName());
                                        contact.getSocialModel().setVkLink(extrator.getValueCopy());
                                        count_upd++;

                                    }


                                    contact.hasVk = true;
                                    if (extrator.getListContactsSearch() == null || extrator.getListContactsSearch().isEmpty())
                                        extrator.addContactToListSearch(contactFromRealm);
                                }

                                if (extrator.getType().equals(ClipboardEnum.LINKEDIN)) {

                                    if (contact.getSocialModel() == null)
                                        contact.setSocialModel(realm.createObject(SocialModel.class));

                                    if (contact.getSocialModel().getLinkedInLink() != null && !contact.getSocialModel().getLinkedInLink().isEmpty() /*&& contact.hasLinked*/ && !SocialEq.checkLinkedInSocial(SocialEq.getSub(extrator.getValueCopy().trim()), contact)) {
                                        //contactsService.updateNote(contact.getIdContact(), contact.getSocialModel().getLinkedInLink(), extrator.getValueCopy().trim());

                                        boolean check = true;
                                        for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                            if (SocialEq.checkStrSocials(contactInfo.value.trim(), extrator.getValueCopy().trim()))
                                                check = false;
                                        }
                                        if (check) {
                                            contact.addNote(extrator.getValueCopy());
                                            contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim(), contact.getName());
                                            count_upd++;
                                        }

                                    } else if (contact.getSocialModel().getLinkedInLink() == null || contact.getSocialModel().getLinkedInLink().isEmpty()) {
                                        contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy(), contact.getName());
                                        contact.getSocialModel().setLinkedInLink(extrator.getValueCopy());
                                        count_upd++;
                                    }


                                    contact.hasLinked = true;
                                    if (extrator.getListContactsSearch() == null || extrator.getListContactsSearch().isEmpty())
                                        extrator.addContactToListSearch(contactFromRealm);
                                }

                                if (extrator.getType().equals(ClipboardEnum.SKYPE)) {
                                    if (contact.getSocialModel() == null)
                                        contact.setSocialModel(realm.createObject(SocialModel.class));

                                    if (contact.getSocialModel().getSkypeLink() != null && !contact.getSocialModel().getSkypeLink().isEmpty() && contact.hasSkype && !contact.getSocialModel().getSkypeLink().equalsIgnoreCase(extrator.getValueCopy().trim())) {
                                        //contactsService.updateNote(contact.getIdContact(), contact.getSocialModel().getLinkedInLink(), extrator.getValueCopy().trim());

                                        contactsService.deleteNoteContact(contact.getIdContact(), "Skype: " + contact.getSocialModel().getSkypeLink());


                                    } /*else if (contact.getSocialModel().getSkypeLink() == null || contact.getSocialModel().getSkypeLink().isEmpty()) {*/
                                    contactsService.addNoteToContact(contact.getIdContact(), "Skype: " + extrator.getValueCopy(), contact.getName());
                                    contact.getSocialModel().setSkypeLink(extrator.getValueCopy());
                                    count_upd++;
                                    //}


                                    contact.hasSkype = true;
                                    if (extrator.getListContactsSearch() == null || extrator.getListContactsSearch().isEmpty())
                                        extrator.addContactToListSearch(contactFromRealm);
                                }

                                if (extrator.getType().equals(ClipboardEnum.TELEGRAM)) {
                                            /*if (contact.getSocialModel() == null)
                                                contact.setSocialModel(realm.createObject(SocialModel.class));
                                            contact.getSocialModel().setTelegramLink(extrator.getValueCopy());
                                            contact.hasTelegram = true;*/

                                    boolean check = true;
                                    for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                        if (contactInfo.value.trim().equals(extrator.getValueCopy().trim()))
                                            check = false;
                                    }
                                    if (check) {
                                        contact.addNote(extrator.getValueCopy());
                                        contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim(), contact.getName());
                                        count_upd++;
                                    }

                                }

                                if (extrator.getType().equals(ClipboardEnum.VIBER)) {
                                            /*if (contact.getSocialModel() == null)
                                                contact.setSocialModel(realm.createObject(SocialModel.class));
                                            contact.getSocialModel().setTelegramLink(extrator.getValueCopy());
                                            contact.hasTelegram = true;*/

                                    boolean check = true;
                                    for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                        if (contactInfo.value.trim().equals(extrator.getValueCopy().trim()))
                                            check = false;
                                    }
                                    if (check) {
                                        contact.addNote(extrator.getValueCopy());
                                        contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim(), contact.getName());
                                        count_upd++;
                                    }

                                }

                                if (extrator.getType().equals(ClipboardEnum.WHATSAPP)) {
                                            /*if (contact.getSocialModel() == null)
                                                contact.setSocialModel(realm.createObject(SocialModel.class));
                                            contact.getSocialModel().setTelegramLink(extrator.getValueCopy());
                                            contact.hasTelegram = true;*/

                                    boolean check = true;
                                    for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                        if (contactInfo.value.trim().equals(extrator.getValueCopy().trim()))
                                            check = false;
                                    }
                                    if (check) {
                                        contact.addNote(extrator.getValueCopy());
                                        contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim(), contact.getName());
                                        count_upd++;
                                    }

                                }


                                if (extrator.getType().equals(ClipboardEnum.PHONE)) {
                                    boolean check = true;
                                    for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                        if (contactInfo.value.trim().equals(extrator.getValueCopy().trim())) {
                                            check = false;
                                            continue;
                                        }


                                        String str = contactInfo.value.trim().replaceAll("[\\s\\-\\+\\(\\)]", "");
                                        String str2 = extrator.getValueCopy().trim().replaceAll("[\\s\\-\\+\\(\\)]", "");

                                        if ((str.charAt(0) == '7' || str.charAt(0) == '8') && str.length() == 11) {
                                            str = str.substring(1);
                                        }
                                        if ((str2.charAt(0) == '7' || str2.charAt(0) == '8') && str2.length() == 11) {
                                            str2 = str2.substring(1);
                                        }

                                        if (str.equalsIgnoreCase(str2)) {
                                            check = false;
                                            continue;
                                        }


                                        if (contactInfo.type.equals("phone") && contactInfo.value.equals("+000000000000")) {
                                            contactInfo.value = extrator.getValueCopy();
                                            contactsService.updatePhone(contact.getIdContact(), "+000000000000", extrator.getValueCopy().trim());
                                            count_upd++;
                                            check = false;
                                            continue;
                                        }
                                    }
                                    if (check) {
                                        contact.addPhone(extrator.getValueCopy());
                                        String id_S = contact.getIdContact();
                                        String val = extrator.getValueCopy().trim();
                                        String name_S = contact.getName();

                                        contactsService.addPhoneToContact(id_S, val, name_S);
                                        count_upd++;


                                    }
                                    if (extrator.getListContactsSearch() == null || extrator.getListContactsSearch().isEmpty())
                                        extrator.addContactToListSearch(contactFromRealm);
                                }

                                if (extrator.getType().equals(ClipboardEnum.EMAIL)) {
                                    boolean check = true;
                                    for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                        if (contactInfo.value.trim().equals(extrator.getValueCopy().trim()))
                                            check = false;
                                    }
                                    if (check) {
                                        contact.addEmail(extrator.getValueCopy());
                                        contactsService.addMailToContact(contact.getIdContact(), extrator.getValueCopy().trim(), contact.getName());
                                        count_upd++;
                                    }
                                    if (extrator.getListContactsSearch() == null || extrator.getListContactsSearch().isEmpty())
                                        extrator.addContactToListSearch(contactFromRealm);
                                }

                                if (extrator.getType().equals(ClipboardEnum.GITHUB)) {
                                    boolean check = true;
                                    for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                        if (contactInfo.value.trim().equals(extrator.getValueCopy().trim()))
                                            check = false;
                                    }
                                    if (check) {
                                        contact.addNote(extrator.getValueCopy());
                                        contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim(), contact.getName());
                                        count_upd++;
                                    }
                                    if (extrator.getListContactsSearch() == null || extrator.getListContactsSearch().isEmpty())
                                        extrator.addContactToListSearch(contactFromRealm);
                                }

                                if (extrator.getType().equals(ClipboardEnum.ANGEL)) {
                                    boolean check = true;
                                    for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                        if (contactInfo.value.trim().equals(extrator.getValueCopy().trim()))
                                            check = false;
                                    }
                                    if (check) {
                                        contact.addNote(extrator.getValueCopy());
                                        contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim(), contact.getName());
                                        count_upd++;
                                    }
                                    if (extrator.getListContactsSearch() == null || extrator.getListContactsSearch().isEmpty())
                                        extrator.addContactToListSearch(contactFromRealm);
                                }

                                if (extrator.getType().equals(ClipboardEnum.TUMBLR)) {
                                    boolean check = true;
                                    for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                        if (contactInfo.value.trim().equals(extrator.getValueCopy().trim()))
                                            check = false;
                                    }
                                    if (check) {
                                        contact.addNote(extrator.getValueCopy());
                                        contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim(), contact.getName());
                                        count_upd++;
                                    }
                                    if (extrator.getListContactsSearch() == null || extrator.getListContactsSearch().isEmpty())
                                        extrator.addContactToListSearch(contactFromRealm);
                                }

                                if (extrator.getType().equals(ClipboardEnum.G_DOC)) {
                                    boolean check = true;
                                    for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                        if (contactInfo.value.trim().equals(extrator.getValueCopy().trim()))
                                            check = false;
                                    }
                                    if (check) {
                                        contact.addNote(extrator.getValueCopy());
                                        contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim(), contact.getName());
                                        count_upd++;
                                    }
                                    if (extrator.getListContactsSearch() == null || extrator.getListContactsSearch().isEmpty())
                                        extrator.addContactToListSearch(contactFromRealm);
                                }

                                if (extrator.getType().equals(ClipboardEnum.G_SHEET)) {
                                    boolean check = true;
                                    for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                        if (contactInfo.value.trim().equals(extrator.getValueCopy().trim()))
                                            check = false;
                                    }
                                    if (check) {
                                        contact.addNote(extrator.getValueCopy());
                                        contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim(), contact.getName());
                                        count_upd++;
                                    }
                                    if (extrator.getListContactsSearch() == null || extrator.getListContactsSearch().isEmpty())
                                        extrator.addContactToListSearch(contactFromRealm);
                                }



                                if (extrator.getType().equals(ClipboardEnum.WEB)) {
                                    boolean check = true;
                                    for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                        if (contactInfo.value.trim().equals(extrator.getValueCopy().trim()))
                                            check = false;
                                    }
                                    if (check) {
                                        contact.addNote(extrator.getValueCopy());
                                        contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim(), contact.getName());
                                        count_upd++;
                                    }
                                    if (extrator.getListContactsSearch() == null || extrator.getListContactsSearch().isEmpty())
                                        extrator.addContactToListSearch(contactFromRealm);
                                }

                                if (extrator.getType().equals(ClipboardEnum.NOTE)) {
                                    boolean check = true;
                                    for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                        if (contactInfo.value.trim().equals(extrator.getValueCopy().trim()))
                                            check = false;
                                    }
                                    if (check) {
                                        contact.addNote(extrator.getValueCopy());
                                        contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim(), contact.getName());
                                        count_upd++;
                                    }
                                }

                                if (extrator.getType().equals(ClipboardEnum.HASHTAG)) {
                                    String[] hashtags = extrator.getValueCopy().split(" ");
                                    if (contact.getListOfHashtags() == null || contact.getListOfHashtags().isEmpty()) {
                                        RealmList<HashTag> listH = new RealmList<>();
                                        for (String str : hashtags) {

                                            HashTag hashtag = realm.createObject(HashTag.class);
                                            hashtag.setDate(new Date());
                                            hashtag.setHashTagValue(str);

                                            listH.add(hashtag);

                                            contactsService.addNoteToContact(contact.getIdContact(), str, contact.getName());

                                            count_upd++;
                                        }
                                        contact.setListOfHashtags(listH);
                                    } else {
                                        for (String str : hashtags) {
                                            boolean check = false;
                                            for (HashTag h : contact.getListOfHashtags()) {
                                                if (h.getHashTagValue().equals(str)) {
                                                    check = true;
                                                    break;
                                                }
                                            }
                                            if (!check) {

                                                HashTag hashtag = realm.createObject(HashTag.class);
                                                hashtag.setDate(new Date());
                                                hashtag.setHashTagValue(str);

                                                contact.getListOfHashtags().add(hashtag);
                                                contactsService.addNoteToContact(contact.getIdContact(), str, contact.getName());

                                                count_upd++;
                                            }
                                        }
                                    }
                                }

                                if (extrator.getType().equals(ClipboardEnum.ADDRESS)) {
                                    boolean check = true;
                                    for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                        if (contactInfo.value.trim().equals(extrator.getValueCopy().trim()))
                                            check = false;
                                    }
                                    if (check) {
                                        contact.addAddress(extrator.getValueCopy());
                                        contactsService.addAddressToContact(contact.getIdContact(), extrator.getValueCopy().trim(), contact.getName());
                                        count_upd++;
                                    }
                                }


                                if (extrator.getType().equals(ClipboardEnum.DATEOFBIRTH)) {
                                    boolean check = true;
                                    for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                        if (contactInfo.value.trim().equals(extrator.getValueCopy().trim()))
                                            check = false;
                                    }
                                    if (check) {
                                        contact.addDateBirth(extrator.getValueCopy());
                                        contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim(), contact.getName());
                                        count_upd++;
                                    }
                                }
                                if (extrator.getType().equals(ClipboardEnum.BIO_INTRO)) {
                                    boolean check = true;
                                    for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                        if (contactInfo.value.trim().equals(extrator.getValueCopy().trim()))
                                            check = false;
                                    }
                                    if (check) {
                                        contact.addBio(extrator.getValueCopy());
                                        //contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim(), contact.getName());
                                        count_upd++;
                                    }
                                }
                                try {
                                    realm.commitTransaction();
                                } catch (IllegalStateException e) {
                                    e.printStackTrace();
                                }
                                if (extrator.getType().equals(ClipboardEnum.COMPANY)) {
                                    String oldComp = contact.getCompany();
                                    realm.beginTransaction();
                                    contact.setCompany(extrator.getValueCopy().trim());
                                    realm.commitTransaction();

                                    boolean findCompany = false;
                                    //===========================================
                                    for (Contact contactC : ContactCacheService.getCompanies()) {
                                        if (contactC.getName().toLowerCase().compareTo(contact.getCompany().toLowerCase()) == 0) {
                                            boolean ch = false;
                                            for (int i = 0; i < contactC.listOfContacts.size(); i++) {
                                                if (contactC.listOfContacts.get(i).getId() == contact.getId()) {
                                                    realm.beginTransaction();
                                                    contactC.listOfContacts.set(i, contact);
                                                    realm.commitTransaction();


                                                    ch = true;
                                                }
                                            }
                                            if (!ch) {
                                                realm.beginTransaction();
                                                contactC.listOfContacts.add(contact);
                                                realm.commitTransaction();

                                            }

                                            count_upd++;
                                            findCompany = true;
                                        } else if (oldComp != null && contactC.getName().toLowerCase().compareTo(oldComp.toLowerCase()) == 0) {
                                            //  realm.commitTransaction();

                                            Contact comp = ContactCacheService.getCompany(oldComp);
                                            Contact ct = ContactCacheService.getContactById(contact.getId());

                                            if (comp != null) {
                                                if (comp.listOfContacts.size() == 1) {

                                                    contactsFragment.contactAdapter.removeContactById(comp);

                                                }
                                                MainActivity.checkDel = ContactCacheService.removeContactFromCompany(comp, ct);
                                            }

                                            count_upd++;
                                        }
                                    }
                                    if (!findCompany) {

                                        Date date = new Date();
                                        Calendar cal = Calendar.getInstance();
                                        cal.setTime(date);
                                        Time time = getRandomDate();
                                        time.setHours(cal.get(Calendar.HOUR_OF_DAY));
                                        time.setMinutes(cal.get(Calendar.MINUTE));
                                        time.setSeconds(cal.get(Calendar.SECOND));

                                        Contact company = new Contact(date);
                                        company.setName(contact.getCompany().trim());
                                        //    company.time = getRandomDate();
                                        company.time = time.toString();
                                        company.color = Color.rgb(Math.abs(contact.getCompany().hashCode() * 28439) % 255, Math.abs(contact.getCompany().hashCode() * 211239) % 255, Math.abs(contact.getCompany().hashCode() * 42368) % 255);
                                        RealmList<Contact> listForSaveContact = new RealmList<>();
                                        listForSaveContact.add(contact);
                                        company.listOfContacts = listForSaveContact;
                                        //ContactsFragment.listOfContacts.add(company);
                                        //   realm.commitTransaction();
                                        ContactCacheService.updateCompany(company);

                                        if (oldComp != null) {
                                            Contact oldCompany = ContactCacheService.getCompany(oldComp);
                                            if (oldCompany != null) {
                                                if (oldCompany.listOfContacts.size() == 1 && oldCompany.listOfContacts.contains(contact)) {
                                                    contactsFragment.contactAdapter.removeContactById(oldCompany);
                                                    //=================
                                                }
                                                ContactCacheService.removeContactFromCompany(oldCompany, contact);
                                            }
                                        }

                                        Contact comm = ContactCacheService.getCompany(company.getName());

                       /* contactAdapter.getListOfContacts().add(comm);
                        contactAdapter.getSavedList().add(comm);*/

                                        contactsFragment.contactAdapter.addContact(comm);

                                        count_upd++;
                                    }

                                    //===============================================================


                                    ContactsFragment.UPD_ALL = true;

                                    contactsService.deleteCompany_Possition(contact.getIdContact());
                                    if (contact.getCompany() != null && !contact.getCompany().isEmpty())
                                        contactsService.insertCompany(contact.getIdContact(), contact.getCompany(), contact.getName());

                                    if (contact.getCompanyPossition() != null && !contact.getCompanyPossition().isEmpty() && (contact.getCompany() != null && !contact.getCompany().isEmpty()))
                                        contactsService.addComp(contact.getIdContact(), contact.getCompanyPossition(), contact.getCompany());

                                    if (contact.getCompanyPossition() != null && !contact.getCompanyPossition().isEmpty() && (contact.getCompany() == null || contact.getCompany().isEmpty()))
                                        contactsService.insertPosition(contact.getIdContact(), contact.getCompanyPossition(), contact.getName());


                                }
                                realm.beginTransaction();

                                if (extrator.getType().equals(ClipboardEnum.POSITION)) {
                                    //boolean check = true;
                                            /*if (contact.getCompanyPossition() == null || contact.getCompanyPossition().isEmpty()) {
                                                contactsService.insertPosition(contact.getIdContact(), extrator.getValueCopy().trim());
                                            } else {
                                                contactsService.updateLocation(contact.getIdContact(), contact.getCompanyPossition(), extrator.getValueCopy().trim());
                                            }*/
                                    contact.setCompanyPossition(extrator.getValueCopy().trim());

                                    count_upd++;

                                    contactsService.deleteCompany_Possition(contact.getIdContact());
                                    if (contact.getCompany() != null && !contact.getCompany().isEmpty())
                                        contactsService.insertCompany(contact.getIdContact(), contact.getCompany(), contact.getName());

                                    if (contact.getCompanyPossition() != null && !contact.getCompanyPossition().isEmpty() && (contact.getCompany() != null && !contact.getCompany().isEmpty()))
                                        contactsService.addComp(contact.getIdContact(), contact.getCompanyPossition(), contact.getCompany());

                                    if (contact.getCompanyPossition() != null && !contact.getCompanyPossition().isEmpty() && (contact.getCompany() == null || contact.getCompany().isEmpty()))
                                        contactsService.insertPosition(contact.getIdContact(), contact.getCompanyPossition(), contact.getName());


                                }


                                realm.commitTransaction();

                            }

                            realm.close();

                            //clipboardPopup.setVisibility(View.GONE);
                            if (magicType.equals(MagicType.CLIPBOARD)) {
                                clibpboardAdapter.uncheck();
                                ((TextView) clipboardPopup.findViewById(R.id.statClipboard)).setText(clibpboardAdapter.getCountCLipboards() + " entries, " + clibpboardAdapter.getCountDetectedClipboards() + " detected");
                            } else {
                                historyAdapter.uncheck();
                                ((TextView) historyPopup.findViewById(R.id.statClipboard)).setText(historyAdapter.getCountCLipboards() + " entries, " + historyAdapter.getCountDetectedClipboards() + " detected");
                            }


                            if (contactsFragment.profilePopUp != null && contactsFragment.profilePopUp.getVisibility() == View.INVISIBLE) {

                                contactsFragment.profilePopUp.setVisibility(View.GONE);
                            }


                            //EventBus.getDefault().post(new UpdateFile());

                            if (contactsFragment.contactAdapter.getSelectContactID() != null && (contactsFragment.profilePopUp != null && contactsFragment.profilePopUp.getVisibility() == View.VISIBLE)) {
                                //contactsFragment.showProfilePopUp(contact);
                                contactsFragment.initIconColor(contact, contactsFragment.profilePopUp);

                            } else if (ProfileFragment.profile && ProfileFragment.selectedContact != null && ((ProfileFragment.selectedContact.listOfContacts == null || ProfileFragment.selectedContact.listOfContacts.isEmpty()))) {


                                boolean editMode = false;
                                if (ProfileFragment.nowEdit) editMode = true;

                                if (!ProfileFragment.nowEdit) {
                                           /* android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                                            fragmentManager.popBackStack();
                                            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                            fragmentTransaction.replace(R.id.main_content, ProfileFragment.newInstance(contact, editMode)).addToBackStack("contacts").commit();*/
                                    EventBus.getDefault().post(new UpdateContactInProfile(contact));
                                } else {
                                    EventBus.getDefault().post(new UpdateContactInProfile(contact));
                                }
                            }/*else if(ContactsFragment.createContact){
                                        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                                        fragmentManager.popBackStack();
                                        ArrayList<Contact> listCreate = new ArrayList<>();
                                        listCreate.add(contact);
                                        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                        fragmentTransaction.replace(R.id.main_content, CreateFragment.newInstance(listCreate)).addToBackStack("contacts").commit();
                                    }*/


                            int finalCount_upd = count_upd;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (finalCount_upd > 0)
                                        Toast.makeText(contactsFragment.getContext(), "Update success", Toast.LENGTH_SHORT).show();
                                    else
                                        Toast.makeText(contactsFragment.getContext(), "Data already exists", Toast.LENGTH_SHORT).show();
                                }
                            });


                        })
                        .setNegativeButton("No", (dialog, id) -> dialog.cancel());

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        } else if ((ProfileFragment.profile && ProfileFragment.selectedContact != null && ((ProfileFragment.selectedContact.listOfContacts != null && !ProfileFragment.selectedContact.listOfContacts.isEmpty()))) || (contactsFragment.contactAdapter.getSelectContactID() != null && (contactsFragment.companyProfilePopup != null && contactsFragment.companyProfilePopup.getVisibility() == View.VISIBLE)) /*|| ContactsFragment.createContact*/) {


            ArrayList<Clipboard> listClipForUpd;
            if (magicType.equals(MagicType.CLIPBOARD))
                listClipForUpd = clibpboardAdapter.getClipbaordList();
            else
                listClipForUpd = historyAdapter.getClipbaordList();

            int co = 0;
            for (Clipboard cl : listClipForUpd) {
                if (cl.getListClipboards() == null || cl.getListClipboards().isEmpty()) {
                    co++;
                }
            }

            if (co > 0) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        contactsFragment.getContext());
                alertDialogBuilder.setTitle("Update company with " + co + " new data fields?");
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Yes", (dialog, id) -> {

                            Contact contact = null;
                            Contact contactFromRealm = null;
                            if (contactsFragment.contactAdapter.getSelectContactID() != null && (contactsFragment.companyProfilePopup != null && contactsFragment.companyProfilePopup.getVisibility() == View.VISIBLE)) {
                                try {
                                    contact = ContactCacheService.getCompany(contactsFragment.contactAdapter.getSelectContactID());
                                    contactFromRealm = ContactCacheService.getContactByIdAndWithoutRealm(contact.getId());
                                } catch (Exception e) {
                                    return;
                                }
                            } else if (ProfileFragment.profile && ProfileFragment.selectedContact != null && ((ProfileFragment.selectedContact.listOfContacts != null && !ProfileFragment.selectedContact.listOfContacts.isEmpty()))) {
                                contact = ProfileFragment.selectedContact;
                                contactFromRealm = ContactCacheService.getContactByIdAndWithoutRealm(contact.getId());
                            }/*else if(ContactsFragment.createContact && CreateFragment.createdContact != null){
                                        contact = CreateFragment.createdContact;
                                    }*/


                            Realm realm = Realm.getDefaultInstance(); //-

                            for (Clipboard extrator : listClipForUpd) {
                                if (extrator.getListClipboards() != null && !extrator.getListClipboards().isEmpty())
                                    continue;

                                realm.beginTransaction();
                                        /*if (extrator.getType().equals(ClipboardEnum.NAME)) {
                                            contactsService.updateName(contact.getIdContact(), null, extrator.getValueCopy().trim());
                                            contact.setName(extrator.getValueCopy().trim());
                                        }*/

                                if (extrator.getType().equals(ClipboardEnum.FACEBOOK)) {
                                    if (contact.getSocialModel() == null)
                                        contact.setSocialModel(realm.createObject(SocialModel.class));

                                           /* if (contact.getSocialModel().getFacebookLink() != null && !contact.getSocialModel().getFacebookLink().isEmpty() && contact.hasFacebook && !contact.getSocialModel().getFacebookLink().equalsIgnoreCase(extrator.getValueCopy().trim())) {
                                                //contactsService.updateNote(contact.getIdContact(), contact.getSocialModel().getFacebookLink(), extrator.getValueCopy().trim());
                                                boolean check = true;
                                                for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                                    if (contactInfo.value.trim().equals(extrator.getValueCopy().trim()))
                                                        check = false;
                                                }
                                                if (check) {
                                                    contact.addNote(extrator.getValueCopy());
                                                    contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim());
                                                }
                                            } else if (contact.getSocialModel().getFacebookLink() == null || contact.getSocialModel().getFacebookLink().isEmpty()) {*/
                                    contact.getSocialModel().setFacebookLink(extrator.getValueCopy());
                                           /*     contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy());
                                            }*/


                                    contact.hasFacebook = true;
                                    if (extrator.getListContactsSearch() == null || extrator.getListContactsSearch().isEmpty())
                                        extrator.addContactToListSearch(contactFromRealm);
                                    //if(extrator.getNameFromSocial() != null) contact.setName(extrator.getNameFromSocial());
                                    //ParsingClipboard parsingSocial= new ParsingClipboard(extrator.getValueCopy().toString(), SocialEnums.FACEBOOK);
                                    //parsingSocial.execute();
                                }

                                if (extrator.getType().equals(ClipboardEnum.INSTAGRAM)) {
                                    if (contact.getSocialModel() == null)
                                        contact.setSocialModel(realm.createObject(SocialModel.class));

                                         /*   if (contact.getSocialModel().getInstagramLink() != null && !contact.getSocialModel().getInstagramLink().isEmpty() && contact.hasInst && !contact.getSocialModel().getInstagramLink().equalsIgnoreCase(extrator.getValueCopy().trim())) {
                                                //contactsService.updateNote(contact.getIdContact(), contact.getSocialModel().getInstagramLink(), extrator.getValueCopy().trim());

                                                boolean check = true;
                                                for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                                    if (contactInfo.value.trim().equals(extrator.getValueCopy().trim()))
                                                        check = false;
                                                }
                                                if (check) {
                                                    contact.addNote(extrator.getValueCopy());
                                                    contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim());
                                                }


                                            } else if (contact.getSocialModel().getInstagramLink() == null || contact.getSocialModel().getInstagramLink().isEmpty()) {
                                                contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim());*/
                                    contact.getSocialModel().setInstagramLink(extrator.getValueCopy());
                                    //}

                                    if (extrator.getListContactsSearch() == null || extrator.getListContactsSearch().isEmpty())
                                        extrator.addContactToListSearch(contactFromRealm);
                                    contact.hasInst = true;
                                }

                                if (extrator.getType().equals(ClipboardEnum.YOUTUBE)) {
                                    if (extrator.getValueCopy().contains("user") || extrator.getValueCopy().contains("channel")) {
                                        if (contact.getSocialModel() == null)
                                            contact.setSocialModel(realm.createObject(SocialModel.class));

                                                /*if (contact.getSocialModel().getYoutubeLink() != null && !contact.getSocialModel().getYoutubeLink().isEmpty() && contact.hasYoutube && !contact.getSocialModel().getYoutubeLink().equalsIgnoreCase(extrator.getValueCopy().trim())) {
                                                    boolean check = true;
                                                    for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                                        if (contactInfo.value.trim().equals(extrator.getValueCopy().trim()))
                                                            check = false;
                                                    }
                                                    if (check) {
                                                        contact.addNote(extrator.getValueCopy());
                                                        contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim());
                                                    }


                                                } else if (contact.getSocialModel().getYoutubeLink() == null || contact.getSocialModel().getYoutubeLink().isEmpty()) {
                                                    contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim());*/
                                        contact.getSocialModel().setYoutubeLink(extrator.getValueCopy());
                                        //}


                                        contact.hasYoutube = true;
                                        if (extrator.getListContactsSearch() == null || extrator.getListContactsSearch().isEmpty())
                                            extrator.addContactToListSearch(contactFromRealm);
                                    } /*else {
                                                boolean check = true;
                                                for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                                    if (contactInfo.value.trim().equals(extrator.getValueCopy().trim()))
                                                        check = false;
                                                }
                                                if (check) {
                                                    contact.addNote(extrator.getValueCopy());
                                                    contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim());
                                                }
                                            }*/
                                }


                                if (extrator.getType().equals(ClipboardEnum.MEDIUM)) {
                                    if (extrator.getValueCopy().contains("com/@")) {
                                        if (contact.getSocialModel() == null)
                                            contact.setSocialModel(realm.createObject(SocialModel.class));

                                                /*if (contact.getSocialModel().getYoutubeLink() != null && !contact.getSocialModel().getYoutubeLink().isEmpty() && contact.hasYoutube && !contact.getSocialModel().getYoutubeLink().equalsIgnoreCase(extrator.getValueCopy().trim())) {
                                                    boolean check = true;
                                                    for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                                        if (contactInfo.value.trim().equals(extrator.getValueCopy().trim()))
                                                            check = false;
                                                    }
                                                    if (check) {
                                                        contact.addNote(extrator.getValueCopy());
                                                        contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim());
                                                    }


                                                } else if (contact.getSocialModel().getYoutubeLink() == null || contact.getSocialModel().getYoutubeLink().isEmpty()) {
                                                    contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim());*/
                                        contact.getSocialModel().setMediumLink(extrator.getValueCopy());
                                        //}


                                        contact.hasMedium = true;
                                        if (extrator.getListContactsSearch() == null || extrator.getListContactsSearch().isEmpty())
                                            extrator.addContactToListSearch(contactFromRealm);
                                    } /*else {
                                                boolean check = true;
                                                for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                                    if (contactInfo.value.trim().equals(extrator.getValueCopy().trim()))
                                                        check = false;
                                                }
                                                if (check) {
                                                    contact.addNote(extrator.getValueCopy());
                                                    contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim());
                                                }
                                            }*/
                                }

                                if (extrator.getType().equals(ClipboardEnum.TWITTER)) {
                                    if (contact.getSocialModel() == null)
                                        contact.setSocialModel(realm.createObject(SocialModel.class));

                                            /*if (contact.getSocialModel().getTwitterLink() != null && !contact.getSocialModel().getTwitterLink().isEmpty() && contact.hasTwitter && !contact.getSocialModel().getTwitterLink().equalsIgnoreCase(extrator.getValueCopy().trim())) {
                                                boolean check = true;
                                                for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                                    if (contactInfo.value.trim().equals(extrator.getValueCopy().trim()))
                                                        check = false;
                                                }
                                                if (check) {
                                                    contact.addNote(extrator.getValueCopy());
                                                    contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim());
                                                }


                                            } else if (contact.getSocialModel().getTwitterLink() == null || contact.getSocialModel().getTwitterLink().isEmpty()) {
                                                contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim());*/
                                    contact.getSocialModel().setTwitterLink(extrator.getValueCopy());
                                    //}

                                    if (extrator.getListContactsSearch() == null || extrator.getListContactsSearch().isEmpty())
                                        extrator.addContactToListSearch(contactFromRealm);
                                    contact.hasTwitter = true;
                                }


                                if (extrator.getType().equals(ClipboardEnum.VK)) {
                                    if (contact.getSocialModel() == null)
                                        contact.setSocialModel(realm.createObject(SocialModel.class));

                                           /* if (contact.getSocialModel().getVkLink() != null && !contact.getSocialModel().getVkLink().isEmpty() && contact.hasVk && !contact.getSocialModel().getVkLink().equalsIgnoreCase(extrator.getValueCopy().trim())) {
                                                //contactsService.updateNote(contact.getIdContact(), contact.getSocialModel().getVkLink(), extrator.getValueCopy().trim());

                                                boolean check = true;
                                                for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                                    if (contactInfo.value.trim().equals(extrator.getValueCopy().trim()))
                                                        check = false;
                                                }
                                                if (check) {
                                                    contact.addNote(extrator.getValueCopy());
                                                    contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim());
                                                }

                                            } else if (contact.getSocialModel().getVkLink() == null || contact.getSocialModel().getVkLink().isEmpty()) {
                                                contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy());*/
                                    contact.getSocialModel().setVkLink(extrator.getValueCopy());

                                    //}
                                    if (extrator.getListContactsSearch() == null || extrator.getListContactsSearch().isEmpty())
                                        extrator.addContactToListSearch(contactFromRealm);

                                    contact.hasVk = true;
                                    //if(extrator.getNameFromSocial() != null) contact.setName(extrator.getNameFromSocial());
                                    //ParsingClipboard parsingSocial= new ParsingClipboard(extrator.getValueCopy().toString(), SocialEnums.VK);
                                    //parsingSocial.execute();
                                }

                                if (extrator.getType().equals(ClipboardEnum.LINKEDIN)) {
                                    if (contact.getSocialModel() == null)
                                        contact.setSocialModel(realm.createObject(SocialModel.class));

                                    contact.getSocialModel().setLinkedInLink(extrator.getValueCopy());
                                    //}

                                    if (extrator.getListContactsSearch() == null || extrator.getListContactsSearch().isEmpty())
                                        extrator.addContactToListSearch(contactFromRealm);
                                    contact.hasLinked = true;
                                }

                                if (extrator.getType().equals(ClipboardEnum.SKYPE)) {
                                    if (contact.getSocialModel() == null)
                                        contact.setSocialModel(realm.createObject(SocialModel.class));

                                    contact.getSocialModel().setSkypeLink(extrator.getValueCopy());
                                    //}

                                    if (extrator.getListContactsSearch() == null || extrator.getListContactsSearch().isEmpty())
                                        extrator.addContactToListSearch(contactFromRealm);
                                    contact.hasSkype = true;
                                }

                                if (extrator.getType().equals(ClipboardEnum.TELEGRAM)) {

                                    boolean check = true;
                                    for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                        if (contactInfo.value.trim().equals(extrator.getValueCopy().trim()))
                                            check = false;
                                    }
                                    if (check) {
                                        contact.addNote(extrator.getValueCopy());
                                        contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim(), contact.getName());
                                    }
                                }

                                if (extrator.getType().equals(ClipboardEnum.VIBER)) {

                                    boolean check = true;
                                    for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                        if (contactInfo.value.trim().equals(extrator.getValueCopy().trim()))
                                            check = false;
                                    }
                                    if (check) {
                                        contact.addNote(extrator.getValueCopy());
                                        contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim(), contact.getName());
                                    }
                                }

                                if (extrator.getType().equals(ClipboardEnum.WHATSAPP)) {

                                    boolean check = true;
                                    for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                        if (contactInfo.value.trim().equals(extrator.getValueCopy().trim()))
                                            check = false;
                                    }
                                    if (check) {
                                        contact.addNote(extrator.getValueCopy());
                                        contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim(), contact.getName());
                                    }
                                }


                                if (extrator.getType().equals(ClipboardEnum.HASHTAG)) {
                                    String[] hashtags = extrator.getValueCopy().split(" ");
                                    if (contact.getListOfHashtags() == null || contact.getListOfHashtags().isEmpty()) {
                                        RealmList<HashTag> listH = new RealmList<>();
                                        for (String str : hashtags) {

                                            HashTag hashtag = realm.createObject(HashTag.class);
                                            hashtag.setDate(new Date());
                                            hashtag.setHashTagValue(str);

                                            listH.add(hashtag);

                                            //contactsService.addNoteToContact(contact.getIdContact(), str, contact.getName());
                                        }
                                        contact.setListOfHashtags(listH);
                                    } else {
                                        for (String str : hashtags) {
                                            boolean check = false;
                                            for (HashTag h : contact.getListOfHashtags()) {
                                                if (h.getHashTagValue().equals(str)) {
                                                    check = true;
                                                    break;
                                                }
                                            }
                                            if (!check) {

                                                HashTag hashtag = realm.createObject(HashTag.class);
                                                hashtag.setDate(new Date());
                                                hashtag.setHashTagValue(str);

                                                contact.getListOfHashtags().add(hashtag);
                                                //contactsService.addNoteToContact(contact.getIdContact(), str, contact.getName());
                                            }
                                        }
                                    }
                                }

                                if (extrator.getType().equals(ClipboardEnum.EMAIL)) {
                                    boolean check = true;
                                    for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                        if (contactInfo.value.trim().equals(extrator.getValueCopy().trim()))
                                            check = false;
                                    }
                                    if (check) {
                                        contact.addEmail(extrator.getValueCopy().trim());
                                        //contactsService.addMailToContact(contact.getIdContact(), extrator.getValueCopy().trim());
                                    }
                                }

                                if (extrator.getType().equals(ClipboardEnum.BIO_INTRO)) {
                                    boolean check = true;
                                    for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                        if (contactInfo.value.trim().equals(extrator.getValueCopy().trim()))
                                            check = false;
                                    }
                                    if (check) {
                                        contact.shortInto = extrator.getValueCopy().trim();
                                        //contact.addEmail();
                                        //contactsService.addMailToContact(contact.getIdContact(), extrator.getValueCopy().trim());
                                    }
                                }

                                if (extrator.getType().equals(ClipboardEnum.WEB)) {
                                    if (contact.webSite == null || contact.webSite.isEmpty())
                                        contact.webSite = extrator.getValueCopy().trim();
                                    else {
                                        if (contact.listOfContactInfo == null || contact.listOfContactInfo.isEmpty())
                                            contact.addNote(extrator.getValueCopy().trim());
                                        else {
                                            boolean checK_C = true;
                                            for (ContactInfo ci : contact.getListOfContactInfo()) {
                                                if (ci.value.equalsIgnoreCase(extrator.getValueCopy().trim())) {
                                                    //Toast.makeText(MainActivity.this, "Data already exists", Toast.LENGTH_SHORT).show();
                                                    checK_C = false;
                                                    break;
                                                }
                                            }
                                            if (checK_C) {
                                                contact.addNote(extrator.getValueCopy().trim());
                                            }

                                        }
                                    }


                                }


                                realm.commitTransaction();

                            }

                            realm.close();

                            //clipboardPopup.setVisibility(View.GONE);
                            if (magicType.equals(MagicType.CLIPBOARD)) {
                                clibpboardAdapter.uncheck();
                                ((TextView) clipboardPopup.findViewById(R.id.statClipboard)).setText(clibpboardAdapter.getCountCLipboards() + " entries, " + clibpboardAdapter.getCountDetectedClipboards() + " detected");
                            } else {
                                historyAdapter.uncheck();
                                ((TextView) historyPopup.findViewById(R.id.statClipboard)).setText(historyAdapter.getCountCLipboards() + " entries, " + historyAdapter.getCountDetectedClipboards() + " detected");
                            }


                            if (contactsFragment.companyProfilePopup != null && contactsFragment.companyProfilePopup.getVisibility() == View.INVISIBLE) {

                                contactsFragment.companyProfilePopup.setVisibility(View.GONE);
                            }


                            //EventBus.getDefault().post(new UpdateFile());

                            if (ProfileFragment.profile && ProfileFragment.selectedContact != null && ((ProfileFragment.selectedContact.listOfContacts != null && !ProfileFragment.selectedContact.listOfContacts.isEmpty()))) {
                                android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                                fragmentManager.popBackStack();

                                boolean editMode = false;
                                if (ProfileFragment.nowEdit) editMode = true;


                                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                ProfileFragment profileFragment = ProfileFragment.newInstance(contact, editMode);
                                if (!profileFragment.isAdded()) {
                                    fragmentTransaction.replace(R.id.main_content, profileFragment).addToBackStack("contact").commit();
                                }
                            } else if (contactsFragment.contactAdapter.getSelectContactID() != null && (contactsFragment.companyProfilePopup != null && contactsFragment.companyProfilePopup.getVisibility() == View.VISIBLE)) {
                                contactsFragment.showCompanyPopup(contact);
                            }

                            Toast.makeText(contactsFragment.getContext(), "Update success", Toast.LENGTH_SHORT).show();

                        })
                        .setNegativeButton("No", (dialog, id) -> dialog.cancel());

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }

        } else if (ContactsFragment.createContact) {

            ArrayList<Clipboard> listClipForUpd;
            if (magicType.equals(MagicType.CLIPBOARD))
                listClipForUpd = clibpboardAdapter.getClipbaordList();
            else
                listClipForUpd = historyAdapter.getClipbaordList();

            int co = 0;
            for (Clipboard cl : listClipForUpd) {
                if (cl.getListClipboards() == null || cl.getListClipboards().isEmpty()) {
                    co++;
                }
            }

            if (co > 0) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        contactsFragment.getContext());
                alertDialogBuilder.setTitle("Update contact with " + co + " new data fields?");
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Yes", (dialog, id) -> {

                            Contact contact = new Contact();


                            Realm realm = Realm.getDefaultInstance(); //-

                            for (Clipboard extrator : listClipForUpd) {
                                if (extrator.getListClipboards() != null && !extrator.getListClipboards().isEmpty())
                                    continue;

                                realm.beginTransaction();
                                if (extrator.getType().equals(ClipboardEnum.NAME)) {
                                    //contactsService.updateName(contact.getIdContact(), null, extrator.getValueCopy().trim());
                                    contact.setName(extrator.getValueCopy().trim());
                                }

                                if (extrator.getType().equals(ClipboardEnum.FACEBOOK)) {
                                    if (contact.getSocialModel() == null)
                                        contact.setSocialModel(realm.createObject(SocialModel.class));

                                    if (contact.getSocialModel().getFacebookLink() != null && !contact.getSocialModel().getFacebookLink().isEmpty() && contact.hasFacebook && !SocialEq.checkFacebookSocial(SocialEq.getSub(extrator.getValueCopy().trim()), contact)) {
                                        //contactsService.updateNote(contact.getIdContact(), contact.getSocialModel().getFacebookLink(), extrator.getValueCopy().trim());
                                        boolean check = true;
                                        for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                            if (SocialEq.checkStrSocials(contactInfo.value.trim(), extrator.getValueCopy().trim()))
                                                check = false;
                                        }
                                        if (check) {
                                            contact.addNote(extrator.getValueCopy());
                                            //contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim());
                                        }
                                    } else if (contact.getSocialModel().getFacebookLink() == null || contact.getSocialModel().getFacebookLink().isEmpty()) {
                                        contact.getSocialModel().setFacebookLink(extrator.getValueCopy());
                                        //contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy());
                                    }


                                    contact.hasFacebook = true;
                                }

                                if (extrator.getType().equals(ClipboardEnum.INSTAGRAM)) {
                                    if (contact.getSocialModel() == null)
                                        contact.setSocialModel(realm.createObject(SocialModel.class));

                                    if (contact.getSocialModel().getInstagramLink() != null && !contact.getSocialModel().getInstagramLink().isEmpty() /*&& contact.hasInst*/ && !SocialEq.checkInstaSocial(SocialEq.getSub(extrator.getValueCopy().trim()), contact)) {
                                        //contactsService.updateNote(contact.getIdContact(), contact.getSocialModel().getInstagramLink(), extrator.getValueCopy().trim());

                                        boolean check = true;
                                        for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                            if (SocialEq.checkStrSocials(contactInfo.value.trim(), extrator.getValueCopy().trim()))
                                                check = false;
                                        }
                                        if (check) {
                                            contact.addNote(extrator.getValueCopy());
                                            //contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim());
                                        }


                                    } else if (contact.getSocialModel().getInstagramLink() == null || contact.getSocialModel().getInstagramLink().isEmpty()) {
                                        //contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim());
                                        contact.getSocialModel().setInstagramLink(extrator.getValueCopy());
                                    }


                                    contact.hasInst = true;
                                }

                                if (extrator.getType().equals(ClipboardEnum.YOUTUBE)) {
                                    if (extrator.getValueCopy().contains("user") || extrator.getValueCopy().contains("channel")) {
                                        if (contact.getSocialModel() == null)
                                            contact.setSocialModel(realm.createObject(SocialModel.class));

                                        if (contact.getSocialModel().getYoutubeLink() != null && !contact.getSocialModel().getYoutubeLink().isEmpty() /*&& contact.hasYoutube*/ && !SocialEq.checkYoutubeSocial(SocialEq.getSub(extrator.getValueCopy().trim()), contact)) {
                                            boolean check = true;
                                            for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                                if (SocialEq.checkStrSocials(contactInfo.value.trim(), extrator.getValueCopy().trim()))
                                                    check = false;
                                            }
                                            if (check) {
                                                contact.addNote(extrator.getValueCopy());
                                                //contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim());
                                            }


                                        } else if (contact.getSocialModel().getYoutubeLink() == null || contact.getSocialModel().getYoutubeLink().isEmpty()) {
                                            //contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim());
                                            contact.getSocialModel().setYoutubeLink(extrator.getValueCopy());
                                        }


                                        contact.hasYoutube = true;
                                    } else {
                                        boolean check = true;
                                        for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                            if (SocialEq.checkStrSocials(contactInfo.value.trim(), extrator.getValueCopy().trim()))
                                                check = false;
                                        }
                                        if (check) {
                                            contact.addNote(extrator.getValueCopy());
                                            //contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim());
                                        }
                                    }
                                }

                                if (extrator.getType().equals(ClipboardEnum.MEDIUM)) {
                                    if (extrator.getValueCopy().contains("com/@")) {
                                        if (contact.getSocialModel() == null)
                                            contact.setSocialModel(realm.createObject(SocialModel.class));

                                        if (contact.getSocialModel().getMediumLink() != null && !contact.getSocialModel().getMediumLink().isEmpty() /*&& contact.hasMedium*/ && !SocialEq.checkMediumSocail(SocialEq.getSub(extrator.getValueCopy().trim()), contact)) {
                                            boolean check = true;
                                            for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                                if (SocialEq.checkStrSocials(contactInfo.value.trim(), extrator.getValueCopy().trim()))
                                                    check = false;
                                            }
                                            if (check) {
                                                contact.addNote(extrator.getValueCopy());
                                                //contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim());
                                            }


                                        } else if (contact.getSocialModel().getMediumLink() == null || contact.getSocialModel().getMediumLink().isEmpty()) {
                                            //contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim());
                                            contact.getSocialModel().setMediumLink(extrator.getValueCopy());
                                        }


                                        contact.hasMedium = true;
                                    } else {
                                        boolean check = true;
                                        for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                            if (SocialEq.checkStrSocials(contactInfo.value.trim(), extrator.getValueCopy().trim()))
                                                check = false;
                                        }
                                        if (check) {
                                            contact.addNote(extrator.getValueCopy());
                                            //contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim());
                                        }
                                    }
                                }

                                if (extrator.getType().equals(ClipboardEnum.TWITTER)) {
                                    if (contact.getSocialModel() == null)
                                        contact.setSocialModel(realm.createObject(SocialModel.class));

                                    if (contact.getSocialModel().getTwitterLink() != null && !contact.getSocialModel().getTwitterLink().isEmpty() /*&& contact.hasTwitter*/ && !SocialEq.checkTwitterSocial(SocialEq.getSub(extrator.getValueCopy().trim()), contact)) {
                                        boolean check = true;
                                        for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                            if (SocialEq.checkStrSocials(contactInfo.value.trim(), extrator.getValueCopy().trim()))
                                                check = false;
                                        }
                                        if (check) {
                                            contact.addNote(extrator.getValueCopy());
                                            //contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim());
                                        }


                                    } else if (contact.getSocialModel().getTwitterLink() == null || contact.getSocialModel().getTwitterLink().isEmpty()) {
                                        //contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim());
                                        contact.getSocialModel().setTwitterLink(extrator.getValueCopy());
                                    }


                                    contact.hasTwitter = true;
                                }


                                if (extrator.getType().equals(ClipboardEnum.VK)) {
                                    if (contact.getSocialModel() == null)
                                        contact.setSocialModel(realm.createObject(SocialModel.class));

                                    if (contact.getSocialModel().getVkLink() != null && !contact.getSocialModel().getVkLink().isEmpty() /*&& contact.hasVk*/ && !SocialEq.checkVkSocial(SocialEq.getSub(extrator.getValueCopy().trim()), contact)) {
                                        //contactsService.updateNote(contact.getIdContact(), contact.getSocialModel().getVkLink(), extrator.getValueCopy().trim());

                                        boolean check = true;
                                        for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                            if (SocialEq.checkStrSocials(contactInfo.value.trim(), extrator.getValueCopy().trim()))
                                                check = false;
                                        }
                                        if (check) {
                                            contact.addNote(extrator.getValueCopy());
                                            //contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim());
                                        }

                                    } else if (contact.getSocialModel().getVkLink() == null || contact.getSocialModel().getVkLink().isEmpty()) {
                                        //contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy());
                                        contact.getSocialModel().setVkLink(extrator.getValueCopy());

                                    }


                                    contact.hasVk = true;
                                    //if(extrator.getNameFromSocial() != null) contact.setName(extrator.getNameFromSocial());
                                    //ParsingClipboard parsingSocial= new ParsingClipboard(extrator.getValueCopy().toString(), SocialEnums.VK);
                                    //parsingSocial.execute();
                                }

                                if (extrator.getType().equals(ClipboardEnum.LINKEDIN)) {
                                    if (contact.getSocialModel() == null)
                                        contact.setSocialModel(realm.createObject(SocialModel.class));

                                    if (contact.getSocialModel().getLinkedInLink() != null && !contact.getSocialModel().getLinkedInLink().isEmpty() /*&& contact.hasLinked*/ && !SocialEq.checkLinkedInSocial(SocialEq.getSub(extrator.getValueCopy().trim()), contact)) {
                                        //contactsService.updateNote(contact.getIdContact(), contact.getSocialModel().getLinkedInLink(), extrator.getValueCopy().trim());

                                        boolean check = true;
                                        for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                            if (contactInfo.value.trim().equals(extrator.getValueCopy().trim()))
                                                check = false;
                                        }
                                        if (check) {
                                            contact.addNote(extrator.getValueCopy());
                                            //contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim());
                                        }

                                    } else if (contact.getSocialModel().getLinkedInLink() == null || contact.getSocialModel().getLinkedInLink().isEmpty()) {
                                        //contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy());
                                        contact.getSocialModel().setLinkedInLink(extrator.getValueCopy());
                                    }


                                    contact.hasLinked = true;
                                }

                                if (extrator.getType().equals(ClipboardEnum.SKYPE)) {
                                    if (contact.getSocialModel() == null)
                                        contact.setSocialModel(realm.createObject(SocialModel.class));

                                    if (contact.getSocialModel().getSkypeLink() != null && !contact.getSocialModel().getSkypeLink().isEmpty() && contact.hasSkype && !contact.getSocialModel().getSkypeLink().equalsIgnoreCase(extrator.getValueCopy().trim())) {
                                        //contactsService.updateNote(contact.getIdContact(), contact.getSocialModel().getLinkedInLink(), extrator.getValueCopy().trim());

                                        boolean check = true;
                                        for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                            if (contactInfo.value.trim().equals(extrator.getValueCopy().trim()))
                                                check = false;
                                        }
                                        if (check) {
                                            contact.addNote(extrator.getValueCopy());
                                            //contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim());
                                        }

                                    } else if (contact.getSocialModel().getSkypeLink() == null || contact.getSocialModel().getSkypeLink().isEmpty()) {
                                        //contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy());
                                        contact.getSocialModel().setSkypeLink(extrator.getValueCopy());
                                    }


                                    contact.hasSkype = true;
                                }

                                if (extrator.getType().equals(ClipboardEnum.TELEGRAM)) {
                                    boolean check = true;
                                    for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                        if (contactInfo.value.trim().equals(extrator.getValueCopy().trim()))
                                            check = false;
                                    }
                                    if (check) {
                                        contact.addNote(extrator.getValueCopy());
                                        //contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim(), contact.getName());
                                    }
                                }

                                if (extrator.getType().equals(ClipboardEnum.VIBER)) {
                                    boolean check = true;
                                    for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                        if (contactInfo.value.trim().equals(extrator.getValueCopy().trim()))
                                            check = false;
                                    }
                                    if (check) {
                                        contact.addNote(extrator.getValueCopy());
                                        //contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim(), contact.getName());
                                    }
                                }

                                if (extrator.getType().equals(ClipboardEnum.WHATSAPP)) {
                                    boolean check = true;
                                    for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                        if (contactInfo.value.trim().equals(extrator.getValueCopy().trim()))
                                            check = false;
                                    }
                                    if (check) {
                                        contact.addNote(extrator.getValueCopy());
                                        //contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim(), contact.getName());
                                    }
                                }


                                if (extrator.getType().equals(ClipboardEnum.PHONE)) {
                                    boolean check = true;
                                    for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                        if (contactInfo.value.trim().equals(extrator.getValueCopy().trim())) {
                                            check = false;
                                            continue;
                                        }
                                        if (contactInfo.type.equals("phone") && contactInfo.value.equals("+000000000000")) {
                                            contactInfo.value = extrator.getValueCopy();
                                            //contactsService.updatePhone(contact.getIdContact(), "+000000000000", extrator.getValueCopy().trim());
                                            check = false;
                                            continue;
                                        }
                                    }
                                    if (check) {
                                        contact.addPhone(extrator.getValueCopy());
                                        //contactsService.addPhoneToContact(contact.getIdContact(), extrator.getValueCopy().trim());
                                    }
                                }

                                if (extrator.getType().equals(ClipboardEnum.EMAIL)) {
                                    boolean check = true;
                                    for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                        if (contactInfo.value.trim().equals(extrator.getValueCopy().trim()))
                                            check = false;
                                    }
                                    if (check) {
                                        contact.addEmail(extrator.getValueCopy());
                                        //contactsService.addMailToContact(contact.getIdContact(), extrator.getValueCopy().trim());
                                    }
                                }

                                if (extrator.getType().equals(ClipboardEnum.WEB)) {
                                    boolean check = true;
                                    for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                        if (contactInfo.value.trim().equals(extrator.getValueCopy().trim()))
                                            check = false;
                                    }
                                    if (check) {
                                        contact.addNote(extrator.getValueCopy());
                                        //contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim());
                                    }
                                }

                                if (extrator.getType().equals(ClipboardEnum.GITHUB)) {
                                    boolean check = true;
                                    for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                        if (contactInfo.value.trim().equals(extrator.getValueCopy().trim()))
                                            check = false;
                                    }
                                    if (check) {
                                        contact.addNote(extrator.getValueCopy());
                                        //contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim());
                                    }
                                }

                                if (extrator.getType().equals(ClipboardEnum.ANGEL)) {
                                    boolean check = true;
                                    for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                        if (contactInfo.value.trim().equals(extrator.getValueCopy().trim()))
                                            check = false;
                                    }
                                    if (check) {
                                        contact.addNote(extrator.getValueCopy());
                                        //contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim());
                                    }
                                }

                                if (extrator.getType().equals(ClipboardEnum.TUMBLR)) {
                                    boolean check = true;
                                    for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                        if (contactInfo.value.trim().equals(extrator.getValueCopy().trim()))
                                            check = false;
                                    }
                                    if (check) {
                                        contact.addNote(extrator.getValueCopy());
                                        //contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim());
                                    }
                                }

                                if (extrator.getType().equals(ClipboardEnum.G_DOC)) {
                                    boolean check = true;
                                    for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                        if (contactInfo.value.trim().equals(extrator.getValueCopy().trim()))
                                            check = false;
                                    }
                                    if (check) {
                                        contact.addNote(extrator.getValueCopy());
                                        //contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim());
                                    }
                                }

                                if (extrator.getType().equals(ClipboardEnum.G_SHEET)) {
                                    boolean check = true;
                                    for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                        if (contactInfo.value.trim().equals(extrator.getValueCopy().trim()))
                                            check = false;
                                    }
                                    if (check) {
                                        contact.addNote(extrator.getValueCopy());
                                        //contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim());
                                    }
                                }


                                if (extrator.getType().equals(ClipboardEnum.NOTE)) {
                                    boolean check = true;
                                    for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                        if (contactInfo.value.trim().equals(extrator.getValueCopy().trim()))
                                            check = false;
                                    }
                                    if (check) {
                                        contact.addNote(extrator.getValueCopy());
                                        //contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim());
                                    }
                                }

                                if (extrator.getType().equals(ClipboardEnum.HASHTAG)) {
                                    String[] hashtags = extrator.getValueCopy().split(" ");
                                    if (contact.getListOfHashtags() == null || contact.getListOfHashtags().isEmpty()) {
                                        RealmList<HashTag> listH = new RealmList<>();
                                        for (String str : hashtags) {

                                            HashTag hashtag = realm.createObject(HashTag.class);
                                            hashtag.setDate(new Date());
                                            hashtag.setHashTagValue(str);

                                            listH.add(hashtag);

                                            //contactsService.addNoteToContact(contact.getIdContact(), str, contact.getName());
                                        }
                                        contact.setListOfHashtags(listH);
                                    } else {
                                        for (String str : hashtags) {
                                            boolean check = false;
                                            for (HashTag h : contact.getListOfHashtags()) {
                                                if (h.getHashTagValue().equals(str)) {
                                                    check = true;
                                                    break;
                                                }
                                            }
                                            if (!check) {

                                                HashTag hashtag = realm.createObject(HashTag.class);
                                                hashtag.setDate(new Date());
                                                hashtag.setHashTagValue(str);

                                                contact.getListOfHashtags().add(hashtag);
                                                //contactsService.addNoteToContact(contact.getIdContact(), str, contact.getName());
                                            }
                                        }
                                    }
                                }

                                if (extrator.getType().equals(ClipboardEnum.ADDRESS)) {
                                    boolean check = true;
                                    for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                        if (contactInfo.value.trim().equals(extrator.getValueCopy().trim()))
                                            check = false;
                                    }
                                    if (check) {
                                        contact.addAddress(extrator.getValueCopy());
                                        //contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim());
                                    }
                                }


                                if (extrator.getType().equals(ClipboardEnum.DATEOFBIRTH)) {
                                    boolean check = true;
                                    for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                        if (contactInfo.value.trim().equals(extrator.getValueCopy().trim()))
                                            check = false;
                                    }
                                    if (check) {
                                        contact.addDateBirth(extrator.getValueCopy());
                                        //contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim());
                                    }
                                }

                                if (extrator.getType().equals(ClipboardEnum.BIO_INTRO)) {
                                    boolean check = true;
                                    for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                        if (contactInfo.value.trim().equals(extrator.getValueCopy().trim()))
                                            check = false;
                                    }
                                    if (check) {
                                        contact.addBio(extrator.getValueCopy());
                                        //contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim());
                                    }
                                }

                                realm.commitTransaction();
                                if (extrator.getType().equals(ClipboardEnum.COMPANY)) {
                                    realm.beginTransaction();
                                    contact.setCompany(extrator.getValueCopy().trim());
                                    realm.commitTransaction();

                                }
                                realm.beginTransaction();

                                if (extrator.getType().equals(ClipboardEnum.POSITION)) {
                                    contact.setCompanyPossition(extrator.getValueCopy().trim());
                                }

                                realm.commitTransaction();
                            }

                            realm.close();

                            //clipboardPopup.setVisibility(View.GONE);

                            if (magicType.equals(MagicType.CLIPBOARD))
                                clibpboardAdapter.uncheck();
                            else
                                historyAdapter.uncheck();

                            EventBus.getDefault().post(new UpdateContactCreate(contact));


                        })
                        .setNegativeButton("No", (dialog, id) -> dialog.cancel());

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        } else if (ClibpboardAdapter.checkUpdateClips) {
            //==========

            ArrayList<Clipboard> listClipForUp;
            if (magicType.equals(MagicType.CLIPBOARD))
                listClipForUp = clibpboardAdapter.getClipbaordList();
            else
                listClipForUp = historyAdapter.getClipbaordList();

            int co = 0;
            for (Clipboard cl : listClipForUp) {
                if (cl.getListClipboards() == null || cl.getListClipboards().isEmpty()) {
                    co++;
                }
            }

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    contactsFragment.getContext());
            alertDialogBuilder.setTitle("Update contact with " + --co + " new data fields?");
            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialog, id) -> {


                        ArrayList<Clipboard> listClipForUpd;
                        if (magicType.equals(MagicType.CLIPBOARD))
                            listClipForUpd = clibpboardAdapter.getClipbaordList();
                        else
                            listClipForUpd = historyAdapter.getClipbaordList();

                        ContactDataClipboard contactUp = null;
                        for (Clipboard clipboard : listClipForUpd) {
                            if (clipboard.getListContactsSearch() != null && !clipboard.getListContactsSearch().isEmpty()) {
                                contactUp = clipboard.getListContactsSearch().get(0);
                            }
                        }
                        if (contactUp != null) {

                            Realm realm = Realm.getDefaultInstance(); //-

                            Contact contact = ContactCacheService.getContactById(contactUp.getId());
                            Contact contactFromRealm = ContactCacheService.getContactByIdAndWithoutRealm(contact.getId());

                            for (Clipboard extrator : listClipForUpd) {
                                if (extrator.getListContactsSearch() == null || extrator.getListContactsSearch().isEmpty()) {
                                    extrator.getListContactsSearch().add(contactUp);
                                    realm.beginTransaction();
                                    if (extrator.getType().equals(ClipboardEnum.NAME)) {
                                        contactsService.updateName(contact.getIdContact(), null, extrator.getValueCopy().trim());
                                        contact.setName(extrator.getValueCopy().trim());

                                        if (extrator.getListContactsSearch() == null || extrator.getListContactsSearch().isEmpty()) {
                                            contactFromRealm = ContactCacheService.getContactByIdAndWithoutRealm(contact.getId());
                                            extrator.addContactToListSearch(contactFromRealm);

                                        }

                                    }

                                    if (extrator.getType().equals(ClipboardEnum.FACEBOOK)) {
                                        if (contact.getSocialModel() == null)
                                            contact.setSocialModel(realm.createObject(SocialModel.class));


                                        if (contact.getSocialModel().getFacebookLink() != null && !contact.getSocialModel().getFacebookLink().isEmpty() && contact.hasFacebook && !SocialEq.checkFacebookSocial(SocialEq.getSub(extrator.getValueCopy().trim()), contact)) {
                                            //contactsService.updateNote(contact.getIdContact(), contact.getSocialModel().getFacebookLink(), extrator.getValueCopy().trim());

                                            boolean check = true;
                                            for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                                if (SocialEq.checkStrSocials(contactInfo.value, extrator.getValueCopy().trim()) /* contactInfo.value.trim().equals(extrator.getValueCopy().trim())*/)
                                                    check = false;
                                            }
                                            if (check) {
                                                contact.addNote(extrator.getValueCopy());
                                                contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim(), contact.getName());
                                            }
                                        } else if (contact.getSocialModel().getFacebookLink() == null || contact.getSocialModel().getFacebookLink().isEmpty()) {
                                            contact.getSocialModel().setFacebookLink(extrator.getValueCopy());
                                            contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy(), contact.getName());
                                        }

                                        if (extrator.getListContactsSearch() == null || extrator.getListContactsSearch().isEmpty()) {
                                            extrator.addContactToListSearch(contactFromRealm);
                                        }

                                        contact.hasFacebook = true;

                                    }

                                    if (extrator.getType().equals(ClipboardEnum.INSTAGRAM)) {
                                        if (contact.getSocialModel() == null)
                                            contact.setSocialModel(realm.createObject(SocialModel.class));

                                        if (contact.getSocialModel().getInstagramLink() != null && !contact.getSocialModel().getInstagramLink().isEmpty() && contact.hasInst && !contact.getSocialModel().getInstagramLink().equalsIgnoreCase(extrator.getValueCopy().trim())) {
                                            //contactsService.updateNote(contact.getIdContact(), contact.getSocialModel().getInstagramLink(), extrator.getValueCopy().trim());

                                            boolean check = true;
                                            for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                                if (contactInfo.value.trim().equals(extrator.getValueCopy().trim()))
                                                    check = false;
                                            }
                                            if (check) {
                                                contact.addNote(extrator.getValueCopy());
                                                contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim(), contact.getName());
                                            }


                                        } else if (contact.getSocialModel().getInstagramLink() == null || contact.getSocialModel().getInstagramLink().isEmpty()) {
                                            contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim(), contact.getName());
                                            contact.getSocialModel().setInstagramLink(extrator.getValueCopy());
                                        }

                                        if (extrator.getListContactsSearch() == null || extrator.getListContactsSearch().isEmpty()) {
                                            extrator.addContactToListSearch(contactFromRealm);
                                        }

                                        contact.hasInst = true;
                                        //if(extrator.getNameFromSocial() != null) contact.setName(extrator.getNameFromSocial());
                                        //ParsingClipboard parsingSocial= new ParsingClipboard(extrator.getValueCopy().toString(), SocialEnums.INSTAGRAM);
                                        //parsingSocial.execute();
                                    }

                                    if (extrator.getType().equals(ClipboardEnum.YOUTUBE)) {
                                        if (extrator.getValueCopy().contains("user") || extrator.getValueCopy().contains("chennel")) {
                                            if (contact.getSocialModel() == null)
                                                contact.setSocialModel(realm.createObject(SocialModel.class));

                                            if (contact.getSocialModel().getYoutubeLink() != null && !contact.getSocialModel().getYoutubeLink().isEmpty() && contact.hasYoutube && !contact.getSocialModel().getYoutubeLink().equalsIgnoreCase(extrator.getValueCopy().trim())) {
                                                //contactsService.updateNote(contact.getIdContact(), contact.getSocialModel().getInstagramLink(), extrator.getValueCopy().trim());

                                                boolean check = true;
                                                for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                                    if (contactInfo.value.trim().equals(extrator.getValueCopy().trim()))
                                                        check = false;
                                                }
                                                if (check) {
                                                    contact.addNote(extrator.getValueCopy());
                                                    contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim(), contact.getName());
                                                }


                                            } else if (contact.getSocialModel().getYoutubeLink() == null || contact.getSocialModel().getYoutubeLink().isEmpty()) {
                                                contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim(), contact.getName());
                                                contact.getSocialModel().setYoutubeLink(extrator.getValueCopy());
                                            }


                                            contact.hasYoutube = true;
                                        } else {
                                            boolean check = true;
                                            for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                                if (contactInfo.value.trim().equals(extrator.getValueCopy().trim()))
                                                    check = false;
                                            }
                                            if (check) {
                                                contact.addNote(extrator.getValueCopy());
                                                contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim(), contact.getName());
                                            }
                                        }

                                        if (extrator.getListContactsSearch() == null || extrator.getListContactsSearch().isEmpty()) {
                                            extrator.addContactToListSearch(contactFromRealm);
                                        }

                                    }


                                    if (extrator.getType().equals(ClipboardEnum.MEDIUM)) {
                                        if (extrator.getValueCopy().contains("com/@")) {
                                            if (contact.getSocialModel() == null)
                                                contact.setSocialModel(realm.createObject(SocialModel.class));

                                            if (contact.getSocialModel().getMediumLink() != null && !contact.getSocialModel().getMediumLink().isEmpty() && contact.hasMedium && !contact.getSocialModel().getMediumLink().equalsIgnoreCase(extrator.getValueCopy().trim())) {
                                                //contactsService.updateNote(contact.getIdContact(), contact.getSocialModel().getInstagramLink(), extrator.getValueCopy().trim());

                                                boolean check = true;
                                                for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                                    if (contactInfo.value.trim().equals(extrator.getValueCopy().trim()))
                                                        check = false;
                                                }
                                                if (check) {
                                                    contact.addNote(extrator.getValueCopy());
                                                    contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim(), contact.getName());
                                                }


                                            } else if (contact.getSocialModel().getMediumLink() == null || contact.getSocialModel().getMediumLink().isEmpty()) {
                                                contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim(), contact.getName());
                                                contact.getSocialModel().setMediumLink(extrator.getValueCopy());
                                            }


                                            contact.hasMedium = true;
                                        } else {
                                            boolean check = true;
                                            for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                                if (contactInfo.value.trim().equals(extrator.getValueCopy().trim()))
                                                    check = false;
                                            }
                                            if (check) {
                                                contact.addNote(extrator.getValueCopy());
                                                contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim(), contact.getName());
                                            }
                                        }

                                        if (extrator.getListContactsSearch() == null || extrator.getListContactsSearch().isEmpty()) {
                                            extrator.addContactToListSearch(contactFromRealm);
                                        }

                                    }

                                    if (extrator.getType().equals(ClipboardEnum.TWITTER)) {

                                        if (contact.getSocialModel() == null)
                                            contact.setSocialModel(realm.createObject(SocialModel.class));

                                        if (contact.getSocialModel().getTwitterLink() != null && !contact.getSocialModel().getTwitterLink().isEmpty() && contact.hasTwitter && !contact.getSocialModel().getTwitterLink().equalsIgnoreCase(extrator.getValueCopy().trim())) {
                                            //contactsService.updateNote(contact.getIdContact(), contact.getSocialModel().getInstagramLink(), extrator.getValueCopy().trim());

                                            boolean check = true;
                                            for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                                if (contactInfo.value.trim().equals(extrator.getValueCopy().trim()))
                                                    check = false;
                                            }
                                            if (check) {
                                                contact.addNote(extrator.getValueCopy());
                                                contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim(), contact.getName());
                                            }


                                        } else if (contact.getSocialModel().getTwitterLink() == null || contact.getSocialModel().getTwitterLink().isEmpty()) {
                                            contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim(), contact.getName());
                                            contact.getSocialModel().setTwitterLink(extrator.getValueCopy());
                                        }

                                        if (extrator.getListContactsSearch() == null || extrator.getListContactsSearch().isEmpty()) {
                                            extrator.addContactToListSearch(contactFromRealm);
                                        }

                                        contact.hasTwitter = true;
                                        //if(extrator.getNameFromSocial() != null) contact.setName(extrator.getNameFromSocial());
                                        //ParsingClipboard parsingSocial= new ParsingClipboard(extrator.getValueCopy().toString(), SocialEnums.INSTAGRAM);
                                        //parsingSocial.execute();
                                    }

                                    if (extrator.getType().equals(ClipboardEnum.VK)) {
                                        if (contact.getSocialModel() == null)
                                            contact.setSocialModel(realm.createObject(SocialModel.class));

                                        if (contact.getSocialModel().getVkLink() != null && !contact.getSocialModel().getVkLink().isEmpty() && contact.hasVk && !contact.getSocialModel().getVkLink().equalsIgnoreCase(extrator.getValueCopy().trim())) {
                                            //contactsService.updateNote(contact.getIdContact(), contact.getSocialModel().getVkLink(), extrator.getValueCopy().trim());

                                            boolean check = true;
                                            for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                                if (contactInfo.value.trim().equals(extrator.getValueCopy().trim()))
                                                    check = false;
                                            }
                                            if (check) {
                                                contact.addNote(extrator.getValueCopy());
                                                contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim(), contact.getName());
                                            }

                                        } else if (contact.getSocialModel().getVkLink() == null || contact.getSocialModel().getVkLink().isEmpty()) {
                                            contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy(), contact.getName());
                                            contact.getSocialModel().setVkLink(extrator.getValueCopy());

                                        }

                                        if (extrator.getListContactsSearch() == null || extrator.getListContactsSearch().isEmpty()) {
                                            extrator.addContactToListSearch(contactFromRealm);
                                        }

                                        contact.hasVk = true;

                                    }

                                    if (extrator.getType().equals(ClipboardEnum.LINKEDIN)) {
                                        if (contact.getSocialModel() == null)
                                            contact.setSocialModel(realm.createObject(SocialModel.class));

                                        if (contact.getSocialModel().getLinkedInLink() != null && !contact.getSocialModel().getLinkedInLink().isEmpty() && contact.hasLinked && !contact.getSocialModel().getLinkedInLink().equalsIgnoreCase(extrator.getValueCopy().trim())) {
                                            //contactsService.updateNote(contact.getIdContact(), contact.getSocialModel().getLinkedInLink(), extrator.getValueCopy().trim());

                                            boolean check = true;
                                            for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                                if (contactInfo.value.trim().equals(extrator.getValueCopy().trim()))
                                                    check = false;
                                            }
                                            if (check) {
                                                contact.addNote(extrator.getValueCopy());
                                                contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim(), contact.getName());
                                            }

                                        } else if (contact.getSocialModel().getLinkedInLink() == null || contact.getSocialModel().getLinkedInLink().isEmpty()) {
                                            contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy(), contact.getName());
                                            contact.getSocialModel().setLinkedInLink(extrator.getValueCopy());
                                        }

                                        if (extrator.getListContactsSearch() == null || extrator.getListContactsSearch().isEmpty()) {
                                            extrator.addContactToListSearch(contactFromRealm);
                                        }

                                        contact.hasLinked = true;
                                    }

                                    if (extrator.getType().equals(ClipboardEnum.TELEGRAM)) {
                                               /* if (contact.getSocialModel() == null)
                                                    contact.setSocialModel(realm.createObject(SocialModel.class));
                                                contact.getSocialModel().setTelegramLink(extrator.getValueCopy());
                                                contact.hasTelegram = true;*/

                                        boolean check = true;
                                        for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                            if (contactInfo.value.trim().equals(extrator.getValueCopy().trim()))
                                                check = false;
                                        }
                                        if (check) {
                                            contact.addNote(extrator.getValueCopy());
                                            contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim(), contact.getName());
                                        }
                                    }

                                    if (extrator.getType().equals(ClipboardEnum.VIBER)) {
                                               /* if (contact.getSocialModel() == null)
                                                    contact.setSocialModel(realm.createObject(SocialModel.class));
                                                contact.getSocialModel().setTelegramLink(extrator.getValueCopy());
                                                contact.hasTelegram = true;*/

                                        boolean check = true;
                                        for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                            if (contactInfo.value.trim().equals(extrator.getValueCopy().trim()))
                                                check = false;
                                        }
                                        if (check) {
                                            contact.addNote(extrator.getValueCopy());
                                            contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim(), contact.getName());
                                        }
                                    }

                                    if (extrator.getType().equals(ClipboardEnum.WHATSAPP)) {
                                               /* if (contact.getSocialModel() == null)
                                                    contact.setSocialModel(realm.createObject(SocialModel.class));
                                                contact.getSocialModel().setTelegramLink(extrator.getValueCopy());
                                                contact.hasTelegram = true;*/

                                        boolean check = true;
                                        for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                            if (contactInfo.value.trim().equals(extrator.getValueCopy().trim()))
                                                check = false;
                                        }
                                        if (check) {
                                            contact.addNote(extrator.getValueCopy());
                                            contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim(), contact.getName());
                                        }
                                    }


                                    if (extrator.getType().equals(ClipboardEnum.PHONE)) {
                                        boolean check = true;
                                        for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                            if (contactInfo.value.trim().equals(extrator.getValueCopy().trim())) {
                                                check = false;
                                                continue;
                                            }
                                            if (contactInfo.type.equals("phone") && contactInfo.value.equals("+000000000000")) {
                                                contactInfo.value = extrator.getValueCopy();
                                                contactsService.updatePhone(contact.getIdContact(), "+000000000000", extrator.getValueCopy().trim());
                                                check = false;
                                                continue;
                                            }
                                        }
                                        if (check) {
                                            contact.addPhone(extrator.getValueCopy());
                                            contactsService.addPhoneToContact(contact.getIdContact(), extrator.getValueCopy().trim(), contact.getName());
                                        }
                                    }

                                    if (extrator.getType().equals(ClipboardEnum.EMAIL)) {
                                        boolean check = true;
                                        for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                            if (contactInfo.value.trim().equals(extrator.getValueCopy().trim()))
                                                check = false;
                                        }
                                        if (check) {
                                            contact.addEmail(extrator.getValueCopy());
                                            contactsService.addMailToContact(contact.getIdContact(), extrator.getValueCopy().trim(), contact.getName());
                                        }
                                    }

                                    if (extrator.getType().equals(ClipboardEnum.WEB)) {
                                        boolean check = true;
                                        for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                            if (contactInfo.value.trim().equals(extrator.getValueCopy().trim()))
                                                check = false;
                                        }
                                        if (check) {
                                            contact.addNote(extrator.getValueCopy());
                                            contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim(), contact.getName());
                                        }

                                        if (extrator.getListContactsSearch() == null || extrator.getListContactsSearch().isEmpty()) {
                                            extrator.addContactToListSearch(contactFromRealm);
                                        }

                                    }

                                    if (extrator.getType().equals(ClipboardEnum.ANGEL)) {
                                        boolean check = true;
                                        for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                            if (contactInfo.value.trim().equals(extrator.getValueCopy().trim()))
                                                check = false;
                                        }
                                        if (check) {
                                            contact.addNote(extrator.getValueCopy());
                                            contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim(), contact.getName());
                                        }

                                        if (extrator.getListContactsSearch() == null || extrator.getListContactsSearch().isEmpty()) {
                                            extrator.addContactToListSearch(contactFromRealm);
                                        }
                                    }

                                    if (extrator.getType().equals(ClipboardEnum.TUMBLR)) {
                                        boolean check = true;
                                        for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                            if (contactInfo.value.trim().equals(extrator.getValueCopy().trim()))
                                                check = false;
                                        }
                                        if (check) {
                                            contact.addNote(extrator.getValueCopy());
                                            contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim(), contact.getName());
                                        }

                                        if (extrator.getListContactsSearch() == null || extrator.getListContactsSearch().isEmpty()) {
                                            extrator.addContactToListSearch(contactFromRealm);
                                        }

                                    }

                                    if (extrator.getType().equals(ClipboardEnum.G_DOC)) {
                                        boolean check = true;
                                        for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                            if (contactInfo.value.trim().equals(extrator.getValueCopy().trim()))
                                                check = false;
                                        }
                                        if (check) {
                                            contact.addNote(extrator.getValueCopy());
                                            contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim(), contact.getName());
                                        }

                                        if (extrator.getListContactsSearch() == null || extrator.getListContactsSearch().isEmpty()) {
                                            extrator.addContactToListSearch(contactFromRealm);
                                        }

                                    }

                                    if (extrator.getType().equals(ClipboardEnum.G_SHEET)) {
                                        boolean check = true;
                                        for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                            if (contactInfo.value.trim().equals(extrator.getValueCopy().trim()))
                                                check = false;
                                        }
                                        if (check) {
                                            contact.addNote(extrator.getValueCopy());
                                            contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim(), contact.getName());
                                        }

                                        if (extrator.getListContactsSearch() == null || extrator.getListContactsSearch().isEmpty()) {
                                            extrator.addContactToListSearch(contactFromRealm);
                                        }

                                    }

                                    if (extrator.getType().equals(ClipboardEnum.NOTE)) {
                                        boolean check = true;
                                        for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                            if (contactInfo.value.trim().equals(extrator.getValueCopy().trim()))
                                                check = false;
                                        }
                                        if (check) {
                                            contact.addNote(extrator.getValueCopy());
                                            contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim(), contact.getName());
                                        }
                                    }

                                    if (extrator.getType().equals(ClipboardEnum.HASHTAG)) {
                                        String[] hashtags = extrator.getValueCopy().split(" ");
                                        if (contact.getListOfHashtags() == null || contact.getListOfHashtags().isEmpty()) {
                                            RealmList<HashTag> listH = new RealmList<>();
                                            for (String str : hashtags) {

                                                HashTag hashtag = realm.createObject(HashTag.class);
                                                hashtag.setDate(new Date());
                                                hashtag.setHashTagValue(str);

                                                listH.add(hashtag);

                                                contactsService.addNoteToContact(contact.getIdContact(), str, contact.getName());
                                            }
                                            contact.setListOfHashtags(listH);
                                        } else {
                                            for (String str : hashtags) {
                                                boolean check = false;
                                                for (HashTag h : contact.getListOfHashtags()) {
                                                    if (h.getHashTagValue().equals(str)) {
                                                        check = true;
                                                        break;
                                                    }
                                                }
                                                if (!check) {

                                                    HashTag hashtag = realm.createObject(HashTag.class);
                                                    hashtag.setDate(new Date());
                                                    hashtag.setHashTagValue(str);

                                                    contact.getListOfHashtags().add(hashtag);
                                                    contactsService.addNoteToContact(contact.getIdContact(), str, contact.getName());
                                                }
                                            }
                                        }
                                    }

                                    if (extrator.getType().equals(ClipboardEnum.ADDRESS)) {
                                        boolean check = true;
                                        for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                            if (contactInfo.value.trim().equals(extrator.getValueCopy().trim()))
                                                check = false;
                                        }
                                        if (check) {
                                            contact.addAddress(extrator.getValueCopy());
                                            contactsService.addAddressToContact(contact.getIdContact(), extrator.getValueCopy().trim(), contact.getName());
                                        }
                                    }


                                    if (extrator.getType().equals(ClipboardEnum.DATEOFBIRTH)) {
                                        boolean check = true;
                                        for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                            if (contactInfo.value.trim().equals(extrator.getValueCopy().trim()))
                                                check = false;
                                        }
                                        if (check) {
                                            contact.addDateBirth(extrator.getValueCopy());
                                            contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim(), contact.getName());
                                        }
                                    }
                                    if (extrator.getType().equals(ClipboardEnum.BIO_INTRO)) {
                                        boolean check = true;
                                        for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                            if (contactInfo.value.trim().equals(extrator.getValueCopy().trim()))
                                                check = false;
                                        }
                                        if (check) {
                                            contact.addBio(extrator.getValueCopy());
                                            //contactsService.addNoteToContact(contact.getIdContact(), extrator.getValueCopy().trim(), contact.getName());
                                        }
                                    }

                                    realm.commitTransaction();
                                    if (extrator.getType().equals(ClipboardEnum.COMPANY)) {
                                        String oldComp = contact.getCompany();
                                        realm.beginTransaction();
                                        contact.setCompany(extrator.getValueCopy().trim());
                                        realm.commitTransaction();
                                            /*if(contact.getCompany() == null){
                                                contact.setCompany(extrator.getValueCopy().trim());
                                                contactsService.insertCompany(contact.getIdContact(), contact.getCompany());
                                            }else if(!contact.getCompany().equalsIgnoreCase(extrator.getValueCopy().trim())){*/
                                        boolean findCompany = false;
                                        //===========================================
                                        for (Contact contactC : ContactCacheService.getCompanies()) {
                                            if (contactC.getName().toLowerCase().compareTo(contact.getCompany().toLowerCase()) == 0) {
                                                boolean ch = false;
                                                for (int i = 0; i < contactC.listOfContacts.size(); i++) {
                                                    if (contactC.listOfContacts.get(i).getId() == contact.getId()) {
                                                        realm.beginTransaction();
                                                        contactC.listOfContacts.set(i, contact);
                                                        realm.commitTransaction();


                                                        ch = true;
                                                    }
                                                }
                                                if (!ch) {
                                                    realm.beginTransaction();
                                                    contactC.listOfContacts.add(contact);
                                                    realm.commitTransaction();

                                                }
                                                findCompany = true;
                                            } else if (oldComp != null && contactC.getName().toLowerCase().compareTo(oldComp.toLowerCase()) == 0) {
                                                //  realm.commitTransaction();

                                                Contact comp = ContactCacheService.getCompany(oldComp);
                                                Contact ct = ContactCacheService.getContactById(contact.getId());

                                                if (comp != null) {
                                                    if (comp.listOfContacts.size() == 1) {

                                                        contactsFragment.contactAdapter.removeContactById(comp);

                                                    }
                                                    MainActivity.checkDel = ContactCacheService.removeContactFromCompany(comp, ct);
                                                }
                                            }
                                        }
                                        if (!findCompany) {

                                            Date date = new Date();
                                            Calendar cal = Calendar.getInstance();
                                            cal.setTime(date);
                                            Time time = getRandomDate();
                                            time.setHours(cal.get(Calendar.HOUR_OF_DAY));
                                            time.setMinutes(cal.get(Calendar.MINUTE));
                                            time.setSeconds(cal.get(Calendar.SECOND));

                                            Contact company = new Contact(date);
                                            company.setName(contact.getCompany().trim());
                                            //    company.time = getRandomDate();
                                            company.time = time.toString();
                                            company.color = Color.rgb(Math.abs(contact.getCompany().hashCode() * 28439) % 255, Math.abs(contact.getCompany().hashCode() * 211239) % 255, Math.abs(contact.getCompany().hashCode() * 42368) % 255);
                                            RealmList<Contact> listForSaveContact = new RealmList<>();
                                            listForSaveContact.add(contact);
                                            company.listOfContacts = listForSaveContact;
                                            //ContactsFragment.listOfContacts.add(company);
                                            //   realm.commitTransaction();
                                            ContactCacheService.updateCompany(company);

                                            if (oldComp != null) {
                                                Contact oldCompany = ContactCacheService.getCompany(oldComp);
                                                if (oldCompany != null) {
                                                    if (oldCompany.listOfContacts.size() == 1 && oldCompany.listOfContacts.contains(contact)) {
                                                        contactsFragment.contactAdapter.removeContactById(oldCompany);
                                                        //=================
                                                    }
                                                    ContactCacheService.removeContactFromCompany(oldCompany, contact);
                                                }
                                            }
                                            //System.out.println("NEW SIZE = "+contactAdapter.getListOfContacts().size());

                                            Contact comm = ContactCacheService.getCompany(company.getName());

                                            contactsFragment.contactAdapter.addContact(comm);

                                        }

                                        //===============================================================


                                        ContactsFragment.UPD_ALL = true;

                                        contactsService.deleteCompany_Possition(contact.getIdContact());
                                        if (contact.getCompany() != null && !contact.getCompany().isEmpty())
                                            contactsService.insertCompany(contact.getIdContact(), contact.getCompany(), contact.getName());

                                        if (contact.getCompanyPossition() != null && !contact.getCompanyPossition().isEmpty() && (contact.getCompany() != null && !contact.getCompany().isEmpty()))
                                            contactsService.addComp(contact.getIdContact(), contact.getCompanyPossition(), contact.getCompany());

                                        if (contact.getCompanyPossition() != null && !contact.getCompanyPossition().isEmpty() && (contact.getCompany() == null || contact.getCompany().isEmpty()))
                                            contactsService.insertPosition(contact.getIdContact(), contact.getCompanyPossition(), contact.getName());


                                    }
                                    realm.beginTransaction();

                                    if (extrator.getType().equals(ClipboardEnum.POSITION)) {
                                        contact.setCompanyPossition(extrator.getValueCopy().trim());

                                        contactsService.deleteCompany_Possition(contact.getIdContact());
                                        if (contact.getCompany() != null && !contact.getCompany().isEmpty())
                                            contactsService.insertCompany(contact.getIdContact(), contact.getCompany(), contact.getName());

                                        if (contact.getCompanyPossition() != null && !contact.getCompanyPossition().isEmpty() && (contact.getCompany() != null && !contact.getCompany().isEmpty()))
                                            contactsService.addComp(contact.getIdContact(), contact.getCompanyPossition(), contact.getCompany());

                                        if (contact.getCompanyPossition() != null && !contact.getCompanyPossition().isEmpty() && (contact.getCompany() == null || contact.getCompany().isEmpty()))
                                            contactsService.insertPosition(contact.getIdContact(), contact.getCompanyPossition(), contact.getName());


                                    }


                                    realm.commitTransaction();
                                }
                            }

                            realm.close();
                            if (magicType.equals(MagicType.CLIPBOARD)) {
                                ((TextView) clipboardPopup.findViewById(R.id.statClipboard)).setText(clibpboardAdapter.getCountCLipboards() + " entries, " + clibpboardAdapter.getCountDetectedClipboards() + " detected");
                                clibpboardAdapter.uncheck();
                            } else {
                                ((TextView) historyPopup.findViewById(R.id.statClipboard)).setText(historyAdapter.getCountCLipboards() + " entries, " + historyAdapter.getCountDetectedClipboards() + " detected");
                                historyAdapter.uncheck();
                            }


                            //EventBus.getDefault().post(new UpdateFile());

                        }

                        Toast.makeText(contactsFragment.getContext(), "Update success", Toast.LENGTH_SHORT).show();

                    })
                    .setNegativeButton("No", (dialog, id) -> dialog.cancel());

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
//==========
        }
    }


    private void createContact(MagicType magicType) {

        Contact contact = new Contact();
        ArrayList<Clipboard> listEx;

        if (magicType.equals(MagicType.CLIPBOARD))
            listEx = clibpboardAdapter.getClipbaordList();
        else
            listEx = historyAdapter.getClipbaordList();


        if (listEx.size() == 0) {
            Toast.makeText(contactsFragment.getContext(), "Choose data", Toast.LENGTH_SHORT).show();
            return;
        }
        for (Clipboard extrator : listEx) {


            if (extrator.getListClipboards() != null && !extrator.getListClipboards().isEmpty())
                continue;

            if (extrator.getType().equals(ClipboardEnum.NAME))
                contact.setName(extrator.getValueCopy().trim());

            if (extrator.getType().equals(ClipboardEnum.FACEBOOK)) {
                if (contact.getSocialModel() == null)
                    contact.setSocialModel(new SocialModel());

                if (contact.hasFacebook && contact.getSocialModel().getFacebookLink() != null && !contact.getSocialModel().getFacebookLink().isEmpty()) {
                    contact.listOfContactInfo.add(new ContactInfo("note", extrator.getValueCopy(), false, false, false, true, false));

                } else {
                    contact.getSocialModel().setFacebookLink(extrator.getValueCopy());
                    contact.hasFacebook = true;
                    if (extrator.getNameFromSocial() != null && (contact.getName() == null || contact.getName().isEmpty()))
                        contact.setName(extrator.getNameFromSocial());
                }
            }

            if (extrator.getType().equals(ClipboardEnum.INSTAGRAM)) {
                if (contact.getSocialModel() == null)
                    contact.setSocialModel(new SocialModel());
                if (contact.hasInst && contact.getSocialModel().getInstagramLink() != null && !contact.getSocialModel().getInstagramLink().isEmpty()) {
                    contact.listOfContactInfo.add(new ContactInfo("note", extrator.getValueCopy(), false, false, false, true, false));
                } else {
                    contact.getSocialModel().setInstagramLink(extrator.getValueCopy());
                    contact.hasInst = true;
                    if (extrator.getNameFromSocial() != null && (contact.getName() == null || contact.getName().isEmpty())) {
                        contact.setName(extrator.getNameFromSocial());
                    }
                }
            }

            if (extrator.getType().equals(ClipboardEnum.VK)) {
                if (contact.getSocialModel() == null)
                    contact.setSocialModel(new SocialModel());
                if (contact.hasVk && contact.getSocialModel().getVkLink() != null && !contact.getSocialModel().getVkLink().isEmpty()) {
                    contact.listOfContactInfo.add(new ContactInfo("note", extrator.getValueCopy(), false, false, false, true, false));
                } else {
                    contact.getSocialModel().setVkLink(extrator.getValueCopy());
                    contact.hasVk = true;
                    if (extrator.getNameFromSocial() != null && (contact.getName() == null || contact.getName().isEmpty()))
                        contact.setName(extrator.getNameFromSocial());
                }
            }

            if (extrator.getType().equals(ClipboardEnum.TWITTER)) {
                if (contact.getSocialModel() == null)
                    contact.setSocialModel(new SocialModel());
                if (contact.hasTwitter && contact.getSocialModel().getTwitterLink() != null && !contact.getSocialModel().getTwitterLink().isEmpty()) {
                    contact.listOfContactInfo.add(new ContactInfo("note", extrator.getValueCopy(), false, false, false, true, false));
                } else {
                    contact.getSocialModel().setTwitterLink(extrator.getValueCopy());
                    contact.hasTwitter = true;
                    if (extrator.getNameFromSocial() != null && (contact.getName() == null || contact.getName().isEmpty()))
                        contact.setName(extrator.getNameFromSocial());
                }
            }

            if (extrator.getType().equals(ClipboardEnum.YOUTUBE)) {
                if (extrator.getValueCopy().contains("user") || extrator.getValueCopy().contains("channel")) {
                    if (contact.getSocialModel() == null)
                        contact.setSocialModel(new SocialModel());
                    if (contact.hasYoutube && contact.getSocialModel().getYoutubeLink() != null && !contact.getSocialModel().getYoutubeLink().isEmpty()) {
                        contact.listOfContactInfo.add(new ContactInfo("note", extrator.getValueCopy(), false, false, false, true, false));
                    } else {
                        contact.getSocialModel().setYoutubeLink(extrator.getValueCopy());
                        contact.hasYoutube = true;
                        if (extrator.getNameFromSocial() != null && (contact.getName() == null || contact.getName().isEmpty()))
                            contact.setName(extrator.getNameFromSocial());
                    }
                } else {
                    contact.addNote(extrator.getValueCopy());
                }
            }

            if (extrator.getType().equals(ClipboardEnum.LINKEDIN)) {
                if (contact.getSocialModel() == null)
                    contact.setSocialModel(new SocialModel());
                contact.getSocialModel().setLinkedInLink(extrator.getValueCopy());
                contact.hasLinked = true;
            }

            if (extrator.getType().equals(ClipboardEnum.SKYPE)) {
                if (contact.getSocialModel() == null)
                    contact.setSocialModel(new SocialModel());
                contact.getSocialModel().setSkypeLink(extrator.getValueCopy());
                contact.hasSkype = true;
            }

            if (extrator.getType().equals(ClipboardEnum.TELEGRAM)) {
                contact.listOfContactInfo.add(new ContactInfo("note", extrator.getValueCopy(), false, false, false, true, false));
            }

            if (extrator.getType().equals(ClipboardEnum.VIBER)) {
                contact.listOfContactInfo.add(new ContactInfo("note", extrator.getValueCopy(), false, false, false, true, false));
            }


            if (extrator.getType().equals(ClipboardEnum.WHATSAPP)) {
                contact.listOfContactInfo.add(new ContactInfo("note", extrator.getValueCopy(), false, false, false, true, false));
            }

            if (extrator.getType().equals(ClipboardEnum.GITHUB)) {
                contact.addNote(extrator.getValueCopy());
            }

            if (extrator.getType().equals(ClipboardEnum.ANGEL)) {
                contact.addNote(extrator.getValueCopy());
            }

            if (extrator.getType().equals(ClipboardEnum.TUMBLR)) {
                contact.addNote(extrator.getValueCopy());
            }

            if (extrator.getType().equals(ClipboardEnum.G_DOC)) {
                contact.addNote(extrator.getValueCopy());
            }

            if (extrator.getType().equals(ClipboardEnum.G_SHEET)) {
                contact.addNote(extrator.getValueCopy());
            }

            if (extrator.getType().equals(ClipboardEnum.MEDIUM)) {
                if (extrator.getValueCopy().contains("com/@")) {
                    if (contact.getSocialModel() == null)
                        contact.setSocialModel(new SocialModel());
                    if (contact.hasMedium && contact.getSocialModel().getMediumLink() != null && !contact.getSocialModel().getMediumLink().isEmpty()) {
                        contact.listOfContactInfo.add(new ContactInfo("note", extrator.getValueCopy(), false, false, false, true, false));
                    } else {
                        contact.getSocialModel().setMediumLink(extrator.getValueCopy());
                        contact.hasMedium = true;
                        if (extrator.getNameFromSocial() != null)
                            contact.setName(extrator.getNameFromSocial());
                    }
                } else {
                    contact.addNote(extrator.getValueCopy());
                }
            }

            if (extrator.getType().equals(ClipboardEnum.PHONE)) {
                contact.addPhone(extrator.getValueCopy());
            }

            if (extrator.getType().equals(ClipboardEnum.EMAIL)) {
                contact.addEmail(extrator.getValueCopy());
            }

            if (extrator.getType().equals(ClipboardEnum.WEB)) {
                contact.addNote(extrator.getValueCopy());
            }

            if (extrator.getType().equals(ClipboardEnum.NOTE)) {
                contact.addNote(extrator.getValueCopy());
            }

            if (extrator.getType().equals(ClipboardEnum.ADDRESS)) {
                contact.addAddress(extrator.getValueCopy());
            }


            if (extrator.getType().equals(ClipboardEnum.POSITION)) {
                contact.setCompanyPossition(extrator.getValueCopy().trim());
            }

            if (extrator.getType().equals(ClipboardEnum.DATEOFBIRTH)) {
                contact.addDateBirth(extrator.getValueCopy());
            }

            if (extrator.getType().equals(ClipboardEnum.BIO_INTRO)) {
                contact.addBio(extrator.getValueCopy());
            }

            if (extrator.getType().equals(ClipboardEnum.COMPANY)) {
                contact.setCompany(extrator.getValueCopy());
            }

            if (extrator.getType().equals(ClipboardEnum.HASHTAG)) {
                String[] hashtags = extrator.getValueCopy().split(" ");
                if (contact.getListOfHashtags() == null || contact.getListOfHashtags().isEmpty()) {
                    RealmList<HashTag> listH = new RealmList<>();
                    for (String str : hashtags) {


                        listH.add(new HashTag(str));

                        //contactsService.addNoteToContact(contact.getIdContact(), str, contact.getName());
                    }
                    contact.setListOfHashtags(listH);
                } else {
                    for (String str : hashtags) {
                        boolean check = false;
                        for (HashTag h : contact.getListOfHashtags()) {
                            if (h.getHashTagValue().equals(str)) {
                                check = true;
                                break;
                            }
                        }
                        if (!check) {


                            contact.getListOfHashtags().add(new HashTag(str));
                            //contactsService.addNoteToContact(contact.getIdContact(), str, contact.getName());
                        }
                    }
                }
            }


        }

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                contactsFragment.getContext());
        alertDialogBuilder.setTitle("Create new contact?");
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> {

                    contactsFragment.closeOtherPopup();


                    ContactsFragment.createContact = true;

                    ArrayList<Contact> contactExtract = new ArrayList<>();
                    contactExtract.add(contact);

                    android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                    android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    CreateFragment createFragment = CreateFragment.newInstance(contactExtract);
                    if (!createFragment.isAdded()) {
                        fragmentTransaction.replace(R.id.main_content, createFragment).addToBackStack("create").commit();
                    }


                    if (magicType.equals(MagicType.CLIPBOARD))
                        clibpboardAdapter.uncheck();
                    else
                        historyAdapter.uncheck();


                })
                .setNegativeButton("No", (dialog, id) -> dialog.cancel());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    private Time getRandomDate() {
        final Random random = new Random();
        final int millisInDay = 24 * 60 * 60 * 1000;
        return new Time(random.nextInt(millisInDay));
    }

    public boolean checkBurg = true;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MAIN_MENU = menu;


        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        MenuItem edit = menu.findItem(R.id.edit_contact);
        edit.setVisible(false);

        mainToolBar.findViewById(R.id.toolbar_kanban_clicker).setVisibility(View.GONE);
        mainToolBar.findViewById(R.id.toolbar_share).setVisibility(View.GONE);
        mainToolBar.findViewById(R.id.toolbar_edit).setVisibility(View.GONE);
        mainToolBar.findViewById(R.id.toolbar_menu).setVisibility(View.GONE);


        //initStatusBar();


        if (!ContactAdapter.checkMerge) {
            mainToolBar.findViewById(R.id.toolbar_kanban_clicker).setVisibility(View.VISIBLE);
            //MAIN_MENU.getItem(0).setVisible(true);
        }

        mainToolBar.findViewById(R.id.toolbar_share).setVisibility(View.GONE);
        mainToolBar.findViewById(R.id.toolbar_edit).setVisibility(View.GONE);
        mainToolBar.findViewById(R.id.toolbar_menu).setVisibility(View.GONE);

        MAIN_MENU.getItem(1).setVisible(false);
        MAIN_MENU.getItem(2).setVisible(false);
        MAIN_MENU.getItem(3).setVisible(false);
        MAIN_MENU.getItem(4).setVisible(false);

        MAIN_MENU.findItem(R.id.download_avatar_profile).setVisible(false);

        MAIN_MENU.findItem(R.id.menu_profile).setVisible(false);

        MAIN_MENU.findItem(R.id.share_profile).setVisible(false);


        try {
            if (ContactAdapter.selectionModeEnabled && !ProfileFragment.profile) {

                MAIN_MENU.getItem(0).setVisible(false);
                MAIN_MENU.getItem(2).setVisible(true);
                MAIN_MENU.getItem(3).setVisible(true);
                MAIN_MENU.getItem(4).setVisible(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (ContactAdapter.checkFoActionIcon) {

                MAIN_MENU.getItem(0).setVisible(false);
                MAIN_MENU.getItem(2).setVisible(false);
                MAIN_MENU.getItem(3).setVisible(false);
                MAIN_MENU.getItem(4).setVisible(false);
                ContactAdapter.checkFoActionIcon = false;
            }
            if (ContactAdapter.checkFoActionIconProfile) {
                MAIN_MENU.getItem(0).setVisible(false);
                MAIN_MENU.getItem(2).setVisible(false);
                MAIN_MENU.getItem(3).setVisible(false);
                MAIN_MENU.getItem(4).setVisible(false);
                ContactAdapter.checkFoActionIconProfile = false;
                mainToolBar.findViewById(R.id.toolbar_title).setVisibility(View.VISIBLE);

                if (!ContactAdapter.checkFoActionIconProfileCompany)
                    ((TextView) mainToolBar.findViewById(R.id.toolbar_title)).setText("Profile");
                else
                    ((TextView) mainToolBar.findViewById(R.id.toolbar_title)).setText("Company");

                ContactAdapter.checkFoActionIconProfileCompany = false;
                mainToolBar.setNavigationIcon(R.drawable.icn_arrow_back);

                ((TextView) mainToolBar.findViewById(R.id.cancel_toolbar)).setTextSize(14);
                mainToolBar.findViewById(R.id.cancel_toolbar).setVisibility(View.GONE);
                ((TextView) mainToolBar.findViewById(R.id.cancel_toolbar)).setText("CANCEL");

                findViewById(R.id.select_menu).setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (contactsFragment.mode == 1) {
            ((ImageView) mainToolBar.findViewById(R.id.toolbar_kanban)).setImageDrawable(getResources().getDrawable(R.drawable.new_list_k_2));

            //((ImageView)mainToolBar.findViewById(R.id.toolbar_kanban)).setBackgroundColor(getResources().getColor(R.color.primary));

        } else {
            ((ImageView) mainToolBar.findViewById(R.id.toolbar_kanban)).setImageDrawable(getResources().getDrawable(R.drawable.ic_k_2));

            //((ImageView)mainToolBar.findViewById(R.id.toolbar_kanban)).setBackgroundColor(getResources().getColor(R.color.white));
        }

        MAIN_MENU.getItem(0).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                findViewById(R.id.popup_menu_contacts).setVisibility(View.GONE);
                findViewById(R.id.popup_menu_settings).setVisibility(View.VISIBLE);

                contactsFragment.addOpewViews(findViewById(R.id.popup_menu_settings));
                return false;
            }
        });


        mainToolBar.findViewById(R.id.toolbar_kanban_clicker).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (selectedType.equals(FragmentTypeEnum.CONTACTS)) {
                    findViewById(R.id.popup_menu_contacts).setVisibility(View.VISIBLE);


                    CardView popup_main = findViewById(R.id.popup_menu_contacts);

                    popup_main.findViewById(R.id.menu_profile_remind).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (contactsFragment.mode != 2 && selectedFragment.getClass().equals(ContactsFragment.class)) {

                                ((TextView) popup_main.findViewById(R.id.text_list_mode)).setTextColor(Color.parseColor("#414141"));
                                ((TextView) popup_main.findViewById(R.id.text_kanban_mode)).setTextColor(getResources().getColor(R.color.primary));

                                mainToolBar.findViewById(R.id.toolbar_kanban).getLayoutParams().height = (int) (15.5 * getResources().getDisplayMetrics().density);
                                mainToolBar.findViewById(R.id.toolbar_kanban).getLayoutParams().width = (int) (15.5 * getResources().getDisplayMetrics().density);
                                mainToolBar.findViewById(R.id.toolbar_kanban).requestLayout();

                                ((ImageView) mainToolBar.findViewById(R.id.toolbar_kanban)).setImageDrawable(getResources().getDrawable(R.drawable.ic_k_2));

                                //((ImageView)mainToolBar.findViewById(R.id.toolbar_kanban)).setBackgroundColor(getResources().getColor(R.color.white));


                                popup_main.findViewById(R.id.check_list_mode).setVisibility(View.GONE);
                                popup_main.findViewById(R.id.check_kanban_mode).setVisibility(View.VISIBLE);

                                //popup_main.setVisibility(View.GONE);

                                contactsFragment.closeOtherPopup();

                                contactsFragment.setKanbanMode();


                            }

                        }
                    });

                    popup_main.findViewById(R.id.menu_profile_global_search).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (contactsFragment.mode != 1 && selectedFragment.getClass().equals(ContactsFragment.class)) {
                                ((TextView) popup_main.findViewById(R.id.text_kanban_mode)).setTextColor(Color.parseColor("#414141"));
                                ((TextView) popup_main.findViewById(R.id.text_list_mode)).setTextColor(getResources().getColor(R.color.primary));

                                popup_main.findViewById(R.id.check_kanban_mode).setVisibility(View.GONE);
                                popup_main.findViewById(R.id.check_list_mode).setVisibility(View.VISIBLE);

                                //((ImageView)mainToolBar.findViewById(R.id.toolbar_kanban)).setLayoutParams(new Toolbar.LayoutParams(17,17));

                                mainToolBar.findViewById(R.id.toolbar_kanban).getLayoutParams().height = (int) (17 * getResources().getDisplayMetrics().density);
                                mainToolBar.findViewById(R.id.toolbar_kanban).getLayoutParams().width = (int) (17 * getResources().getDisplayMetrics().density);
                                mainToolBar.findViewById(R.id.toolbar_kanban).requestLayout();

                                ((ImageView) mainToolBar.findViewById(R.id.toolbar_kanban)).setImageDrawable(getResources().getDrawable(R.drawable.new_list_k_2));

                                //((ImageView)mainToolBar.findViewById(R.id.toolbar_kanban)).setBackgroundColor(getResources().getColor(R.color.primary));

                                //popup_main.setVisibility(View.GONE);

                                contactsFragment.closeOtherPopup();

                                contactsFragment.setListMode();
                            }
                        }
                    });

                    popup_main.findViewById(R.id.menu_profile_share).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });

               /* ((RadioButton) popup_main.findViewById(R.id.radioSHORT)).setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        contactsFragment.typeCard = TypeCard.SHORT;
                        contactsFragment.changeType();


                    }
                });*/

                    popup_main.findViewById(R.id.kanban_mode_1).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            contactsFragment.typeCard = TypeCard.SHORT;
                            contactsFragment.changeType();

                            int width_medium = (int) (160 * getResources().getDisplayMetrics().density);
                            int height_medium = (int) (40 * getResources().getDisplayMetrics().density);

                            contactsFragment.getView().findViewById(R.id.head_card_1).setLayoutParams(new LinearLayout.LayoutParams(width_medium, height_medium));
                            contactsFragment.getView().findViewById(R.id.head_card_2).setLayoutParams(new LinearLayout.LayoutParams(width_medium, height_medium));
                            contactsFragment.getView().findViewById(R.id.head_card_3).setLayoutParams(new LinearLayout.LayoutParams(width_medium, height_medium));
                            contactsFragment.getView().findViewById(R.id.head_card_4).setLayoutParams(new LinearLayout.LayoutParams(width_medium, height_medium));
                            contactsFragment.getView().findViewById(R.id.head_card_5).setLayoutParams(new LinearLayout.LayoutParams(width_medium, height_medium));
                            contactsFragment.getView().findViewById(R.id.head_card_6).setLayoutParams(new LinearLayout.LayoutParams(width_medium, height_medium));
                            contactsFragment.getView().findViewById(R.id.head_card_7).setLayoutParams(new LinearLayout.LayoutParams(width_medium, height_medium));
                            contactsFragment.getView().findViewById(R.id.head_card_8).setLayoutParams(new LinearLayout.LayoutParams(width_medium, height_medium));

                            ((TextView) popup_main.findViewById(R.id.kanban_mode_1_text)).setTextColor(getResources().getColor(R.color.primary));
                            ((TextView) popup_main.findViewById(R.id.kanban_mode_2_text)).setTextColor(getResources().getColor(R.color.popup_kanban_color));
                            ((TextView) popup_main.findViewById(R.id.kanban_mode_3_text)).setTextColor(getResources().getColor(R.color.popup_kanban_color));
                            ((TextView) popup_main.findViewById(R.id.kanban_mode_0_text)).setTextColor(getResources().getColor(R.color.popup_kanban_color));


                        }
                    });

                    popup_main.findViewById(R.id.kanban_mode_0).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            contactsFragment.typeCard = TypeCard.ROW;
                            contactsFragment.changeType();
                        /*
                        int width_medium = Resources.getSystem().getDisplayMetrics().widthPixels;
                        int height_medium = (int) (40 * getResources().getDisplayMetrics().density);

                        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(width_medium, height_medium);
                        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(width_medium, height_medium);
                        LinearLayout.LayoutParams layoutParams3 = new LinearLayout.LayoutParams(width_medium, height_medium);
                        LinearLayout.LayoutParams layoutParams4 = new LinearLayout.LayoutParams(width_medium, height_medium);
                        LinearLayout.LayoutParams layoutParams5 = new LinearLayout.LayoutParams(width_medium, height_medium);
                        LinearLayout.LayoutParams layoutParams6 = new LinearLayout.LayoutParams(width_medium, height_medium);
                        LinearLayout.LayoutParams layoutParams7 = new LinearLayout.LayoutParams(width_medium, height_medium);
                        LinearLayout.LayoutParams layoutParams8 = new LinearLayout.LayoutParams(width_medium, height_medium);

                        contactsFragment.getView().findViewById(R.id.head_card_1).setLayoutParams(layoutParams1);
                        contactsFragment.getView().findViewById(R.id.head_card_2).setLayoutParams(layoutParams2);
                        contactsFragment.getView().findViewById(R.id.head_card_3).setLayoutParams(layoutParams3);
                        contactsFragment.getView().findViewById(R.id.head_card_4).setLayoutParams(layoutParams4);
                        contactsFragment.getView().findViewById(R.id.head_card_5).setLayoutParams(layoutParams5);
                        contactsFragment.getView().findViewById(R.id.head_card_6).setLayoutParams(layoutParams6);
                        contactsFragment.getView().findViewById(R.id.head_card_7).setLayoutParams(layoutParams7);
                        contactsFragment.getView().findViewById(R.id.head_card_8).setLayoutParams(layoutParams8);*/

                            ((TextView) popup_main.findViewById(R.id.kanban_mode_1_text)).setTextColor(getResources().getColor(R.color.popup_kanban_color));
                            ((TextView) popup_main.findViewById(R.id.kanban_mode_2_text)).setTextColor(getResources().getColor(R.color.popup_kanban_color));
                            ((TextView) popup_main.findViewById(R.id.kanban_mode_3_text)).setTextColor(getResources().getColor(R.color.popup_kanban_color));
                            ((TextView) popup_main.findViewById(R.id.kanban_mode_0_text)).setTextColor(getResources().getColor(R.color.primary));

                        }
                    });

                    popup_main.findViewById(R.id.kanban_mode_2).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            contactsFragment.typeCard = TypeCard.MEDIUM;
                            contactsFragment.changeType();

                            int width_medium = Resources.getSystem().getDisplayMetrics().widthPixels;
                            int height_medium = (int) (40 * getResources().getDisplayMetrics().density);

                            LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(width_medium, height_medium);
                            //layoutParams1.leftMargin = 4;

                            LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(width_medium, height_medium);
                            //layoutParams2.leftMargin = 4;

                            LinearLayout.LayoutParams layoutParams3 = new LinearLayout.LayoutParams(width_medium, height_medium);
                            //layoutParams3.leftMargin = 4;

                            LinearLayout.LayoutParams layoutParams4 = new LinearLayout.LayoutParams(width_medium, height_medium);
                            //layoutParams4.leftMargin = 4;

                            LinearLayout.LayoutParams layoutParams5 = new LinearLayout.LayoutParams(width_medium, height_medium);
                            //layoutParams5.leftMargin = 4;

                            LinearLayout.LayoutParams layoutParams6 = new LinearLayout.LayoutParams(width_medium, height_medium);
                            //layoutParams6.leftMargin = 4;

                            LinearLayout.LayoutParams layoutParams7 = new LinearLayout.LayoutParams(width_medium, height_medium);
                            //layoutParams7.leftMargin = 4;

                            LinearLayout.LayoutParams layoutParams8 = new LinearLayout.LayoutParams(width_medium, height_medium);
                            //layoutParams8.leftMargin = 4;

                            contactsFragment.getView().findViewById(R.id.head_card_1).setLayoutParams(layoutParams1);
                            contactsFragment.getView().findViewById(R.id.head_card_2).setLayoutParams(layoutParams2);
                            contactsFragment.getView().findViewById(R.id.head_card_3).setLayoutParams(layoutParams3);
                            contactsFragment.getView().findViewById(R.id.head_card_4).setLayoutParams(layoutParams4);
                            contactsFragment.getView().findViewById(R.id.head_card_5).setLayoutParams(layoutParams5);
                            contactsFragment.getView().findViewById(R.id.head_card_6).setLayoutParams(layoutParams6);
                            contactsFragment.getView().findViewById(R.id.head_card_7).setLayoutParams(layoutParams7);
                            contactsFragment.getView().findViewById(R.id.head_card_8).setLayoutParams(layoutParams8);

                            ((TextView) popup_main.findViewById(R.id.kanban_mode_1_text)).setTextColor(getResources().getColor(R.color.popup_kanban_color));
                            ((TextView) popup_main.findViewById(R.id.kanban_mode_2_text)).setTextColor(getResources().getColor(R.color.primary));
                            ((TextView) popup_main.findViewById(R.id.kanban_mode_3_text)).setTextColor(getResources().getColor(R.color.popup_kanban_color));
                            ((TextView) popup_main.findViewById(R.id.kanban_mode_0_text)).setTextColor(getResources().getColor(R.color.popup_kanban_color));
                        }
                    });

                    popup_main.findViewById(R.id.kanban_mode_3).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            contactsFragment.typeCard = TypeCard.FULL;
                            contactsFragment.changeType();

                            int width_medium = (int) (160 * getResources().getDisplayMetrics().density);
                            int height_medium = (int) (40 * getResources().getDisplayMetrics().density);

                            contactsFragment.getView().findViewById(R.id.head_card_1).setLayoutParams(new LinearLayout.LayoutParams(width_medium, height_medium));
                            contactsFragment.getView().findViewById(R.id.head_card_2).setLayoutParams(new LinearLayout.LayoutParams(width_medium, height_medium));
                            contactsFragment.getView().findViewById(R.id.head_card_3).setLayoutParams(new LinearLayout.LayoutParams(width_medium, height_medium));
                            contactsFragment.getView().findViewById(R.id.head_card_4).setLayoutParams(new LinearLayout.LayoutParams(width_medium, height_medium));
                            contactsFragment.getView().findViewById(R.id.head_card_5).setLayoutParams(new LinearLayout.LayoutParams(width_medium, height_medium));
                            contactsFragment.getView().findViewById(R.id.head_card_6).setLayoutParams(new LinearLayout.LayoutParams(width_medium, height_medium));
                            contactsFragment.getView().findViewById(R.id.head_card_7).setLayoutParams(new LinearLayout.LayoutParams(width_medium, height_medium));
                            contactsFragment.getView().findViewById(R.id.head_card_8).setLayoutParams(new LinearLayout.LayoutParams(width_medium, height_medium));

                            ((TextView) popup_main.findViewById(R.id.kanban_mode_1_text)).setTextColor(getResources().getColor(R.color.popup_kanban_color));
                            ((TextView) popup_main.findViewById(R.id.kanban_mode_2_text)).setTextColor(getResources().getColor(R.color.popup_kanban_color));
                            ((TextView) popup_main.findViewById(R.id.kanban_mode_3_text)).setTextColor(getResources().getColor(R.color.primary));
                            ((TextView) popup_main.findViewById(R.id.kanban_mode_0_text)).setTextColor(getResources().getColor(R.color.popup_kanban_color));
                        }
                    });


                    ((RadioButton) popup_main.findViewById(R.id.radioFULL)).setOnCheckedChangeListener((buttonView, isChecked) -> {
                        if (isChecked) {
                            contactsFragment.typeCard = TypeCard.FULL;
                            contactsFragment.changeType();
                        }
                    });

                    contactsFragment.addOpewViews(findViewById(R.id.popup_menu_contacts));

                } else if (selectedType.equals(FragmentTypeEnum.TIMELINE)) {
                    findViewById(R.id.popup_menu_timeline).setVisibility(View.VISIBLE);


                    //timeLineFragment.addOpewViews(findViewById(R.id.popup_menu_contacts));

                    CardView popup_main = findViewById(R.id.popup_menu_timeline);

                    popup_main.findViewById(R.id.menu_profile_remind).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            if (TimeLineFragment.mode != 2 && selectedFragment.getClass().equals(TimeLineFragment.class)) {

                                ((TextView) popup_main.findViewById(R.id.text_list_mode)).setTextColor(Color.parseColor("#414141"));
                                ((TextView) popup_main.findViewById(R.id.text_kanban_mode)).setTextColor(getResources().getColor(R.color.primary));

                                mainToolBar.findViewById(R.id.toolbar_kanban).getLayoutParams().height = (int) (15.5 * getResources().getDisplayMetrics().density);
                                mainToolBar.findViewById(R.id.toolbar_kanban).getLayoutParams().width = (int) (15.5 * getResources().getDisplayMetrics().density);
                                mainToolBar.findViewById(R.id.toolbar_kanban).requestLayout();

                                ((ImageView) mainToolBar.findViewById(R.id.toolbar_kanban)).setImageDrawable(getResources().getDrawable(R.drawable.ic_k_2));


                                popup_main.findViewById(R.id.check_list_mode).setVisibility(View.GONE);
                                popup_main.findViewById(R.id.check_kanban_mode).setVisibility(View.VISIBLE);

                                //popup_main.setVisibility(View.GONE);

                                timeLineFragment.closeOtherPopup();

                                timeLineFragment.setKanbanMode(false);


                            }

                        }
                    });

                    popup_main.findViewById(R.id.menu_profile_global_search).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (TimeLineFragment.mode != 1 && selectedFragment.getClass().equals(TimeLineFragment.class)) {
                                ((TextView) popup_main.findViewById(R.id.text_kanban_mode)).setTextColor(Color.parseColor("#414141"));
                                ((TextView) popup_main.findViewById(R.id.text_list_mode)).setTextColor(getResources().getColorary));(R.color.prim

                                popup_main.findViewById(R.id.check_kanban_mode).setVisibility(View.GONE);
                                popup_main.findViewById(R.id.check_list_mode).setVisibility(View.VISIBLE);


                                mainToolBar.findViewById(R.id.toolbar_kanban).getLayoutParams().height = (int) (17 * getResources().getDisplayMetrics().density);
                                mainToolBar.findViewById(R.id.toolbar_kanban).getLayoutParams().width = (int) (17 * getResources().getDisplayMetrics().density);
                                mainToolBar.findViewById(R.id.toolbar_kanban).requestLayout();

                                ((ImageView) mainToolBar.findViewById(R.id.toolbar_kanban)).setImageDrawable(getResources().getDrawable(R.drawable.new_list_k_2));


                                timeLineFragment.closeOtherPopup();

                                timeLineFragment.setListMode(false);
                            }
                        }
                    });

                    popup_main.findViewById(R.id.menu_profile_share).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });


                    popup_main.findViewById(R.id.kanban_mode_t_1).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            TimeLineFragment.typeCard = TypeCardTimeline.BOXES;

                            timeLineFragment.changeType();

                            int width_medium = (int) (160 * getResources().getDisplayMetrics().density);
                            int height_medium = (int) (40 * getResources().getDisplayMetrics().density);

                            timeLineFragment.getView().findViewById(R.id.head_card_1).setLayoutParams(new LinearLayout.LayoutParams(width_medium, height_medium));
                            timeLineFragment.getView().findViewById(R.id.head_card_2).setLayoutParams(new LinearLayout.LayoutParams(width_medium, height_medium));
                            timeLineFragment.getView().findViewById(R.id.head_card_3).setLayoutParams(new LinearLayout.LayoutParams(width_medium, height_medium));
                            timeLineFragment.getView().findViewById(R.id.head_card_4).setLayoutParams(new LinearLayout.LayoutParams(width_medium, height_medium));
                            timeLineFragment.getView().findViewById(R.id.head_card_5).setLayoutParams(new LinearLayout.LayoutParams(width_medium, height_medium));
                            timeLineFragment.getView().findViewById(R.id.head_card_6).setLayoutParams(new LinearLayout.LayoutParams(width_medium, height_medium));
                            timeLineFragment.getView().findViewById(R.id.head_card_7).setLayoutParams(new LinearLayout.LayoutParams(width_medium, height_medium));
                            timeLineFragment.getView().findViewById(R.id.head_card_8).setLayoutParams(new LinearLayout.LayoutParams(width_medium, height_medium));

                            ((TextView) popup_main.findViewById(R.id.kanban_mode_1_text)).setTextColor(getResources().getColor(R.color.primary));
                            ((TextView) popup_main.findViewById(R.id.kanban_mode_2_text)).setTextColor(getResources().getColor(R.color.popup_kanban_color));
                            ((TextView) popup_main.findViewById(R.id.kanban_mode_3_text)).setTextColor(getResources().getColor(R.color.popup_kanban_color));
                            ((TextView) popup_main.findViewById(R.id.kanban_mode_0_text)).setTextColor(getResources().getColor(R.color.popup_kanban_color));


                        }
                    });

                    /*popup_main.findViewById(R.id.timeline_list_mode_1).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            TimeLineFragment.typeCard = TypeCardTimeline.LIST;
                            timeLineFragment.changeType();

                            ((TextView) popup_main.findViewById(R.id.kanban_mode_1_text)).setTextColor(getResources().getColor(R.color.popup_kanban_color));
                            ((TextView) popup_main.findViewById(R.id.kanban_mode_3_text)).setTextColor(getResources().getColor(R.color.popup_kanban_color));
                            ((TextView) popup_main.findViewById(R.id.kanban_mode_0_text)).setTextColor(getResources().getColor(R.color.popup_kanban_color));
                            ((TextView) popup_main.findViewById(R.id.kanban_mode_2_text)).setTextColor(getResources().getColor(R.color.primary));

                        }
                    });*/

                    popup_main.findViewById(R.id.kanban_mode_t_0).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            TimeLineFragment.typeCard = TypeCardTimeline.EXTRA;
                            timeLineFragment.changeType();

                            ((TextView) popup_main.findViewById(R.id.kanban_mode_1_text)).setTextColor(getResources().getColor(R.color.popup_kanban_color));
                            ((TextView) popup_main.findViewById(R.id.kanban_mode_3_text)).setTextColor(getResources().getColor(R.color.popup_kanban_color));
                            ((TextView) popup_main.findViewById(R.id.kanban_mode_2_text)).setTextColor(getResources().getColor(R.color.popup_kanban_color));
                            ((TextView) popup_main.findViewById(R.id.kanban_mode_0_text)).setTextColor(getResources().getColor(R.color.primary));

                        }
                    });

                    popup_main.findViewById(R.id.kanban_mode_t_3).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            TimeLineFragment.typeCard = TypeCardTimeline.TOP;
                            timeLineFragment.changeType();

                            ((TextView) popup_main.findViewById(R.id.kanban_mode_1_text)).setTextColor(getResources().getColor(R.color.popup_kanban_color));

                            ((TextView) popup_main.findViewById(R.id.kanban_mode_2_text)).setTextColor(getResources().getColor(R.color.popup_kanban_color));
                            ((TextView) popup_main.findViewById(R.id.kanban_mode_0_text)).setTextColor(getResources().getColor(R.color.popup_kanban_color));
                            ((TextView) popup_main.findViewById(R.id.kanban_mode_3_text)).setTextColor(getResources().getColor(R.color.primary));

                        }
                    });

                    popup_main.findViewById(R.id.kanban_mode_t_2).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            TimeLineFragment.typeCard = TypeCardTimeline.ROWS;
                            timeLineFragment.changeType();

                            int width_medium = Resources.getSystem().getDisplayMetrics().widthPixels;
                            int height_medium = (int) (40 * getResources().getDisplayMetrics().density);

                            LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(width_medium, height_medium);


                            LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(width_medium, height_medium);


                            LinearLayout.LayoutParams layoutParams3 = new LinearLayout.LayoutParams(width_medium, height_medium);


                            LinearLayout.LayoutParams layoutParams4 = new LinearLayout.LayoutParams(width_medium, height_medium);


                            LinearLayout.LayoutParams layoutParams5 = new LinearLayout.LayoutParams(width_medium, height_medium);


                            LinearLayout.LayoutParams layoutParams6 = new LinearLayout.LayoutParams(width_medium, height_medium);


                            LinearLayout.LayoutParams layoutParams7 = new LinearLayout.LayoutParams(width_medium, height_medium);


                            LinearLayout.LayoutParams layoutParams8 = new LinearLayout.LayoutParams(width_medium, height_medium);


                            timeLineFragment.getView().findViewById(R.id.head_card_1).setLayoutParams(layoutParams1);
                            timeLineFragment.getView().findViewById(R.id.head_card_2).setLayoutParams(layoutParams2);
                            timeLineFragment.getView().findViewById(R.id.head_card_3).setLayoutParams(layoutParams3);
                            timeLineFragment.getView().findViewById(R.id.head_card_4).setLayoutParams(layoutParams4);
                            timeLineFragment.getView().findViewById(R.id.head_card_5).setLayoutParams(layoutParams5);
                            timeLineFragment.getView().findViewById(R.id.head_card_6).setLayoutParams(layoutParams6);
                            timeLineFragment.getView().findViewById(R.id.head_card_7).setLayoutParams(layoutParams7);
                            timeLineFragment.getView().findViewById(R.id.head_card_8).setLayoutParams(layoutParams8);

                            ((TextView) popup_main.findViewById(R.id.kanban_mode_1_text)).setTextColor(getResources().getColor(R.color.popup_kanban_color));
                            ((TextView) popup_main.findViewById(R.id.kanban_mode_3_text)).setTextColor(getResources().getColor(R.color.popup_kanban_color));
                            ((TextView) popup_main.findViewById(R.id.kanban_mode_2_text)).setTextColor(getResources().getColor(R.color.primary));
                            ((TextView) popup_main.findViewById(R.id.kanban_mode_0_text)).setTextColor(getResources().getColor(R.color.popup_kanban_color));
                        }
                    });


                }

            }
        });


        MAIN_MENU.getItem(2).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                return false;
            }
        });

        MAIN_MENU.getItem(3).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                ContactsFragment.mergedContacts = true;
                contactsFragment.contactAdapter.mergeSelectedContactsToList();
                return false;
            }
        });

        MAIN_MENU.getItem(4).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                //ContactsFragment.mergedContacts = true;
                contactsFragment.showExportPopup();
                return false;
            }
        });


        System.out.println("CREATE MENU END");
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onFileSelected(FileDialog fileDialog, File file) {
        Storage storage = new Storage(this);
        storage.copy(file.getAbsolutePath(), Environment.getExternalStorageDirectory().getPath() + "/Extime/ExtimeFiles/" + file.getName());
        EventBus.getDefault().post(new CopyFile(file));
        ((FileBarInter) selectedFragment).sortAfterAddFileByTime();
    }

    @Override
    public ContactAdapter getAdapter() {
        return contactsFragment.contactAdapter;
    }

    @Override
    public Toolbar getToolbar() {
        return mainToolBar;
    }

    @Override
    public ArrayList<Contact> getListOfContacts() {
        return contactsFragment.listOfContacts;
    }

    @Override
    public ArrayList<Contact> getListSavedContacts() {
        return contactsFragment.contactAdapter.savedContacts;
    }

    @Override
    public void updateListOfContacts() {
        contactsFragment.listOfContacts = ContactCacheService.getAllContacts(null);
    }

    @Override
    public void updateSavedList() {
        contactsFragment.contactAdapter.savedContacts = ContactCacheService.getAllContacts(null);
    }

    @Override
    public HashTagsAdapter getHashTagsAdapter() {
        return contactsFragment.HASHTAG_ADAPTER;
    }

    @Override
    public Menu getMenu() {
        return MAIN_MENU;
    }

    @Override
    public ArrayList<Contact> getListOfContactsMain() {
        return LIST_OF_CONTACTS;
    }

    @Override
    public void setListOfContactsMain(ArrayList<Contact> list) {
        this.LIST_OF_CONTACTS = list;
    }


    public class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            contactsFragment.contactAdapter.savedContacts = ContactCacheService.getAllContacts(null);
            contactsFragment.listOfContacts = contactsFragment.contactAdapter.savedContacts;

            contactsFragment.contactAdapter.defaultListContacts();

            contactsFragment.contactAdapter.getListSaveSort().clear();

            contactsFragment.contactAdapter.setListSaveSort();

            contactsFragment.initContactsBar();
            contactsFragment.initRecyclerHashTags();

            contactsFragment.contactAdapter.notifyDataSetChanged();

        }
    }

    public class MyBroadcastReceiverUpdate extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            for (int i = 0; i < contactsFragment.contactAdapter.savedContacts.size(); i++) {
                if (!contactsFragment.contactAdapter.savedContacts.get(i).isValid()) {
                    contactsFragment.contactAdapter.savedContacts.remove(i);
                    i--;
                }
            }

            contactsFragment.listOfContacts = contactsFragment.contactAdapter.savedContacts;


            for (int i = 0; i < contactsFragment.contactAdapter.getListOfContacts().size(); i++) {
                if (!contactsFragment.contactAdapter.getListOfContacts().get(i).isValid()) {
                    contactsFragment.contactAdapter.getListOfContacts().remove(i);
                    i--;
                }
            }

            if (contactsFragment.contactAdapter.getListSaveSort() != null)
                for (int i = 0; i < contactsFragment.contactAdapter.getListSaveSort().size(); i++) {
                    if (!contactsFragment.contactAdapter.getListSaveSort().get(i).isValid()) {
                        contactsFragment.contactAdapter.getListSaveSort().remove(i);
                        i--;
                    }
                }

            contactsFragment.contactAdapter.notifyDataSetChanged();

        }
    }

    public class MyBroadcastReceiverUpdateCount extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            int c = intent.getIntExtra("update", 0);
            updateCountLoad(c, ContactsService.CONTACT_COUNT);
        }
    }
}





