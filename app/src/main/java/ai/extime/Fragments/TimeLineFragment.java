package ai.extime.Fragments;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;

import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;

import ai.extime.Activity.MainActivity;
import ai.extime.Adapters.AccountAdapter;
import ai.extime.Adapters.ContactAdapter;
import ai.extime.Adapters.ContactsOfMessageAdapter;
import ai.extime.Adapters.ContactsOfMessageAdapterInbox;
import ai.extime.Adapters.ContactsSectionAdapter;
import ai.extime.Adapters.KanbanAdapter;
import ai.extime.Adapters.KanbanTimelineAdapter;
import ai.extime.Adapters.MessageAdapter;
import ai.extime.Adapters.MessageDataAdapter;
import ai.extime.Adapters.MessageDataExtratorAdapter;
import ai.extime.Adapters.TemplateFileAdapter;
import ai.extime.Adapters.TimelineSectionAdapter;
import ai.extime.Enums.ClipboardEnum;
import ai.extime.Enums.ExtractEnums;
import ai.extime.Enums.FragmentTypeEnum;
import ai.extime.Enums.MagicType;
import ai.extime.Enums.TypeCardTimeline;
import ai.extime.Events.ClickMessageKanban;
import ai.extime.Events.EventSetStarMessage;
import ai.extime.Events.HidePopupMessage;
import ai.extime.Events.NotifyKanbanTimeline;
import ai.extime.Events.OpenMessageKanban;
import ai.extime.Events.SetKanbanTimeline;
import ai.extime.Events.TypeCard;
import ai.extime.Events.UpdateMessageAdapter;
import ai.extime.Events.UpdateMessengersPreview;
import ai.extime.Events.UpdateTemplate;
import ai.extime.Interfaces.IMessage;

import ai.extime.Interfaces.PopupsInter;
import ai.extime.Adapters.TimeLineAdapter;
import ai.extime.Interfaces.Postman;
import ai.extime.Models.Clipboard;
import ai.extime.Models.Contact;
import ai.extime.Models.ContactInfo;
import ai.extime.Models.ContactOfMessage;
import ai.extime.Models.EmailMessage;
import ai.extime.Models.FilesTemplate;
import ai.extime.Models.HashTag;
import ai.extime.Models.MessageCredential;
import ai.extime.Models.MessageData;
import ai.extime.Models.SocialModel;
import ai.extime.Models.Template;
import ai.extime.Models.TimeLine;
import ai.extime.Services.ContactCacheService;
import ai.extime.Utils.ClipboardType;
import ai.extime.Utils.FileType;
import ai.extime.Utils.ShareTemplatesMessageReply;
import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.RealmList;


import com.extime.R;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.util.IOUtils;
import com.google.android.gms.tasks.Task;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import com.google.api.client.util.ExponentialBackOff;
import com.google.api.client.util.StringUtils;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartBody;
import com.google.api.services.gmail.model.ModifyMessageRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import static ai.extime.Fragments.ContactTimelineDataFragment.REQUEST_AUTHORIZATION;
import static android.app.Activity.RESULT_OK;

/**
 * Created by patal on 21.09.2017.
 */

public class TimeLineFragment extends Fragment implements PopupsInter, IMessage {

    private View mainView;

    private RecyclerView containerTimelines;

    private LinearLayoutManager mLinearLayoutManager;

    private ArrayList<TimeLine> listOfTimelines;

    FrameLayout popupContacts;

    public Activity activityApp;

    private ArrayList<View> openedViews;

    private FrameLayout timeLinesPopup;

    LinearLayout popupAccounts;

    FileType fileType;

    public static String selectedId = null;

    public static int type_k = 0;

    FrameLayout popupMessage;

    public MessageAdapter messageAdapter;

    AccountAdapter accountAdapter;

    RecyclerView recyclerAccount;

    RecyclerView recyclerAcc2;

    FrameLayout inboxPopup;

    FrameLayout contactInboxPopup;

    String value = "Extime";

    private Toolbar toolbarC;

    public static int mode = 1;

    public static TypeCardTimeline typeCard = TypeCardTimeline.EXTRA;
    //public static TypeCardTimeline typeCard_list = TypeCardTimeline.EXTRA;

    private static final String APPLICATION_NAME = "Extime";

    private static final String[] SCOPES = {
            GmailScopes.GMAIL_LABELS,
            GmailScopes.GMAIL_READONLY,
            GmailScopes.GMAIL_MODIFY
    };

    GoogleAccountCredential mCredential;

    ArrayList<GoogleAccountCredential> listCredential;

    ArrayList<EmailMessage> listMessages;

    public static ArrayList<String> email_contact;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle bundle) {
        if (mainView == null) {
            mainView = inflater.inflate(R.layout.timeline_layout_content, viewGroup, false);

            toolbarC = ((Postman) activityApp).getToolbar();

            setHasOptionsMenu(true);

            fileType = new FileType();

            initAccounts();

            initViews();

            initGetGmailButton();

            checkGmail();

            initBar();

            mainView.findViewById(R.id.recyclerKankab).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if (activityApp.findViewById(R.id.popup_menu_timeline).getVisibility() == View.VISIBLE) {
                        activityApp.findViewById(R.id.popup_menu_timeline).setVisibility(View.GONE);
                        return;
                    }

                    if (activityApp.findViewById(R.id.popup_menu_settings).getVisibility() == View.VISIBLE) {
                        activityApp.findViewById(R.id.popup_menu_settings).setVisibility(View.GONE);
                        return;
                    }

                    if (contactInboxPopup != null && contactInboxPopup.getVisibility() == View.VISIBLE) {
                        contactInboxPopup.setVisibility(View.GONE);
                        return;
                    }

                    if (editPopupTemplate != null && editPopupTemplate.getVisibility() == View.VISIBLE) {
                        editPopupTemplate.setVisibility(View.GONE);
                        return;
                    }

                    if (popupTemplate != null && popupTemplate.getVisibility() == View.VISIBLE) {
                        popupTemplate.setVisibility(View.GONE);
                        return;
                    }

                    if (inboxPopup != null && inboxPopup.getVisibility() == View.VISIBLE) {
                        mainView.findViewById(R.id.folders_Block).setBackgroundColor(0);
                        inboxPopup.setVisibility(View.GONE);
                        return;
                    }

                    if (popupContacts.getVisibility() == View.VISIBLE) {
                        popupContacts.setVisibility(View.GONE);
                        return;
                    }


                    if (popupMessage.getVisibility() == View.VISIBLE) {
                        popupMessage.setVisibility(View.GONE);
                        startSearchContact = false;
                        return;
                    }
                }
            });

        }


        return mainView;
    }

    public void getMessagesFromFile() {

      /*  String path = activityApp.getFilesDir() + "/messages";
        File folder = new File(path);*/

        new Thread(new Runnable() {
            @Override
            public void run() {


                SharedPreferences mPref = activityApp.getSharedPreferences("backupMessages", Context.MODE_PRIVATE);
                String sort = mPref.getString("messages", null);
                String emails = mPref.getString("emails", null);
                String sender_ = mPref.getString("senders_load", null);
                String recipient_ = mPref.getString("recipients_load", null);


                Gson gson = new Gson();
                listMessages = new ArrayList<>();
                listMessages.addAll(gson.fromJson(sort, new TypeToken<ArrayList<EmailMessage>>() {
                }.getType()));

                email_contact = new ArrayList<>();
                try {
                    email_contact.addAll(gson.fromJson(emails, new TypeToken<ArrayList<String>>() {
                    }.getType()));
                } catch (NullPointerException e) {
                    getMessages();
                    return;
                }

                senders_load = new ArrayList<>();
                senders_load.addAll(gson.fromJson(sender_, new TypeToken<ArrayList<ContactOfMessage>>() {
                }.getType()));

                recipients_load = new ArrayList<>();
                recipients_load.addAll(gson.fromJson(recipient_, new TypeToken<ArrayList<ContactOfMessage>>() {
                }.getType()));


                activityApp.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                        messageAdapter = new MessageAdapter(activityApp, listMessages, TimeLineFragment.this, 1);
                        recyclerAccount.setAdapter(messageAdapter);
                        recyclerAccount.setVisibility(View.VISIBLE);

                        mainView.findViewById(R.id.frameGetMessage).setVisibility(View.GONE);

                    }
                });

                listCredential = new ArrayList<>();

                SharedPreferences mSettings;
                mSettings = activityApp.getSharedPreferences("accountUser", Context.MODE_PRIVATE);
                Set<String> set = mSettings.getStringSet("accounts", null);

                Account[] accounts = AccountManager.get(activityApp).getAccountsByType("com.google");

                for (Account account : accounts) {

                    if (set.contains(account.name)) {
                        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(
                                activityApp.getApplicationContext(), Arrays.asList(SCOPES))
                                .setBackOff(new ExponentialBackOff());


                        credential.setSelectedAccountName(account.name);

                        listCredential.add(credential);
                    }
                }

                messageArrayList = new ArrayList<>();

                checkNewMessages();

                /*for (int c = 0; c < listCredential.size(); c++) {
                    HttpTransport transport = AndroidHttp.newCompatibleTransport();
                    JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

                    Gmail service = new Gmail.Builder(transport, jsonFactory, listCredential.get(c))
                            .setApplicationName(APPLICATION_NAME)
                            .build();

                    String user = listCredential.get(c).getSelectedAccountName();

                    try {
                        ListMessagesResponse countM1 = service.users().messages().list(user).setMaxResults(100L).execute();
                        if (countM1 != null && countM1.getMessages() != null) {
                            //countMessages += countM1.getMessages().size();

                            for (Message message : countM1.getMessages()) {
                                messageArrayList.add(new MessageCredential(message, listCredential.get(c), service));
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    //==============================



                    sortMessagesByTime(messageArrayList);

                    final List<Message> messages = new ArrayList<Message>();


                    BatchRequest batchRequest = null;
                    String gmail = null;
                    int c_ = 0;

                    for (MessageCredential messageCredential : messageArrayList) {




                        if (gmail != null && !gmail.equals(messageCredential.getAccountCredential().getSelectedAccountName())) {
                            try {
                                batchRequest.execute();


                            } catch (IllegalStateException | IOException e) {
                                e.printStackTrace();
                            }
                            c_ = 0;

                            gmail = messageCredential.getAccountCredential().getSelectedAccountName();
                            batchRequest = messageCredential.getGmail().batch();

                        } else {


                        }

                        if (gmail == null) {
                            gmail = messageCredential.getAccountCredential().getSelectedAccountName();
                            batchRequest = messageCredential.getGmail().batch();
                        }

                    }




                        c_++;


//=========================================
                        ListMessagesResponse lll = null;


                    Date d = listMessages.get(0).getDate();

                    Calendar current = Calendar.getInstance();
                    Calendar contactDate = Calendar.getInstance();
                    current.setTime(new Date());
                    contactDate.setTime(d);
                    String timeStr = "";

                    Calendar cal1 = Calendar.getInstance();
                    cal1.setTime(d);
                    Time time = getRandomDate();
                    time.setHours(cal1.get(Calendar.HOUR_OF_DAY));
                    time.setMinutes(cal1.get(Calendar.MINUTE));
                    time.setSeconds(cal1.get(Calendar.SECOND));

                    String times = "after:";
                    times += contactDate.get(Calendar.MONTH) + 1 + "/" + contactDate.get(Calendar.DAY_OF_MONTH) + "/" + contactDate.get(Calendar.YEAR);



                    int oldCount = listMessages.size();


                        try {
                            lll = service.users().messages().list(user).setQ(*//*"after:06/17/2020"*//* times).execute();

                            sortMessagesByTimeNew(lll);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if (lll != null && lll.getMessages() != null) {

                            for (int i = 0; i < lll.getMessages().size(); i++) {

                                countLoad++;
                                updateProgress();

                                Message message = null;
                                EmailMessage emailMessage = new EmailMessage();
                                try {

                                    message = service.users().messages().get(user, String.valueOf(lll.getMessages().get(i).getId())).execute();

                                    initNewMessage(message, service, listCredential.get(c));


                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            updateListAdapterNew(listMessages.size() - oldCount);
                            //saveMessages();
                        }

                        //==========================================================

                        if (c_ == 20) {
                            c_ = 0;
                            try {
                                batchRequest.execute();
                            } catch (IllegalStateException | IOException e) {
                                e.printStackTrace();
                            }
                        }



                    }*/


            }
        }).start();

    }

    public void checkNewMessages() {


        if (mainView.findViewById(R.id.progressBarTimeline).getVisibility() == View.GONE) {

            new Thread(new Runnable() {
                @Override
                public void run() {


                    for (int c = 0; c < listCredential.size(); c++) {
                        HttpTransport transport = AndroidHttp.newCompatibleTransport();
                        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

                        Gmail service = new Gmail.Builder(transport, jsonFactory, listCredential.get(c))
                                .setApplicationName(APPLICATION_NAME)
                                .build();

                        String user = listCredential.get(c).getSelectedAccountName();


//=========================================
                        ListMessagesResponse lll = null;


                        Date d = listMessages.get(0).getDate();

                        Calendar current = Calendar.getInstance();
                        Calendar contactDate = Calendar.getInstance();
                        current.setTime(new Date());
                        contactDate.setTime(d);


                        Calendar cal1 = Calendar.getInstance();
                        cal1.setTime(d);
                        Time time = getRandomDate();
                        time.setHours(cal1.get(Calendar.HOUR_OF_DAY));
                        time.setMinutes(cal1.get(Calendar.MINUTE));
                        time.setSeconds(cal1.get(Calendar.SECOND));

                        String times = "after:";
                        times += contactDate.get(Calendar.MONTH) + 1 + "/" + contactDate.get(Calendar.DAY_OF_MONTH) + "/" + contactDate.get(Calendar.YEAR);


                        int oldCount = listMessages.size();


                        try {
                            lll = service.users().messages().list(user).setQ(/*"after:06/17/2020"*/ times).execute();

                            sortMessagesByTimeNew(lll);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if (lll != null && lll.getMessages() != null) {


                            for (int i = 0; i < lll.getMessages().size(); i++) {

                                countLoad++;
                                updateProgress();

                                Message message;

                                try {

                                    message = service.users().messages().get(user, String.valueOf(lll.getMessages().get(i).getId())).execute();


                                    initNewMessage(message, service, listCredential.get(c));


                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            updateListAdapterNew(listMessages.size() - oldCount);

                        }

                        //==========================================================

                    }

                }
            }).start();

        }
    }

    public void saveMessages() {

        new Thread(new Runnable() {
            @Override
            public void run() {


                if (listMessages != null) {
                    synchronized (listMessages) {

                        try {

                            SharedPreferences mPref = activityApp.getSharedPreferences("backupMessages", Context.MODE_PRIVATE);

                            SharedPreferences.Editor editor = mPref.edit();

                            Gson gson = new Gson();
                            String json = gson.toJson(listMessages);
                            editor.putString("messages", json);
                            editor.apply();

                            String emails = gson.toJson(email_contact);
                            editor.putString("emails", emails);
                            editor.apply();

                            String sender_ = gson.toJson(senders_load);
                            editor.putString("senders_load", sender_);
                            editor.apply();

                            String recipient = gson.toJson(recipients_load);
                            editor.putString("recipients_load", recipient);
                            editor.apply();

                        } catch (ConcurrentModificationException e) {
                            e.printStackTrace();
                        }
                    }
                }


            }
        }).start();

    }

    @Override
    public void onStop() {
        super.onStop();
        if (EventBus.getDefault().isRegistered(this)) EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (listMessages != null && !listMessages.isEmpty()) {
            checkNewMessages();
        }

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        if (toolbarC != null) {
            toolbarC.findViewById(R.id.toolbar_kanban_clicker).setVisibility(View.VISIBLE);
            toolbarC.findViewById(R.id.toolbar_kanban).setVisibility(View.VISIBLE);
        }

    }


    ArrayList<ContactOfMessage> senders_load;
    ArrayList<ContactOfMessage> recipients_load;

    ContactsOfMessageAdapterInbox contactsOfMessageAdapter;

    public void initBar() {

        contactInboxPopup = mainView.findViewById(R.id.popupInboxContact);

        mainView.findViewById(R.id.barFavoriteTimeline).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RecyclerView recyclerView = contactInboxPopup.findViewById(R.id.recycler_contacts_message_inbox);

                recyclerView.setLayoutManager(new LinearLayoutManager(mainView.getContext()));

                ArrayList<ContactOfMessage> senders = new ArrayList<>();

                if (senders_load != null && recipients_load != null) {
                    senders.addAll(senders_load);
                    senders.addAll(recipients_load);
                }

                int countFind = 0;

                for (ContactOfMessage contactOfMessage : senders) {
                    if (contactOfMessage.getSearchContact() != null) {
                        countFind++;
                    }
                }

                ((TextView) contactInboxPopup.findViewById(R.id.textSelectMEssInbox)).setText(String.valueOf(senders.size()) + " contacts: " + countFind + " existing, " + String.valueOf(senders.size() - countFind) + " new");

                contactsOfMessageAdapter = new ContactsOfMessageAdapterInbox(mainView.getContext(), senders, contactInboxPopup.findViewById(R.id.shareClipDataInboxPopup));

                recyclerView.setAdapter(contactsOfMessageAdapter);


                contactInboxPopup.findViewById(R.id.calcelMessageInbox).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        contactInboxPopup.setVisibility(View.GONE);
                    }
                });

                contactInboxPopup.findViewById(R.id.shareClipDataInboxPopup).setVisibility(View.GONE);

                contactInboxPopup.findViewById(R.id.shareClipDataInboxPopup).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String text = "";
                        for (ContactOfMessage cm : contactsOfMessageAdapter.getSelectList()) {

                            String name = cm.getName();
                            if (name == null) name = "";

                            text += "Name: " + name + " " + cm.getEmail() + "\n";


                        }


                        text += "\n";
                        text += "Data shared via http://Extime.pro\n";

                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setType("text/plain");
                        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
                        activityApp.startActivity(Intent.createChooser(shareIntent, "Share"));
                    }
                });

                contactInboxPopup.setVisibility(View.VISIBLE);
            }
        });
    }

    public void updateInboxPopup() {
        RecyclerView recyclerView = contactInboxPopup.findViewById(R.id.recycler_contacts_message_inbox);

        recyclerView.setLayoutManager(new LinearLayoutManager(mainView.getContext()));

        ArrayList<ContactOfMessage> senders = new ArrayList<>();

        if (senders_load != null && recipients_load != null) {
            senders.addAll(senders_load);
            senders.addAll(recipients_load);
        }

        int countFind = 0;

        for (ContactOfMessage contactOfMessage : senders) {
            if (contactOfMessage.getSearchContact() != null) {
                countFind++;
            }
        }

        ((TextView) contactInboxPopup.findViewById(R.id.textSelectMEssInbox)).setText(String.valueOf(senders.size()) + " contacts: " + countFind + " existing, " + String.valueOf(senders.size() - countFind) + " new");

        if (contactsOfMessageAdapter == null)
            contactsOfMessageAdapter = new ContactsOfMessageAdapterInbox(mainView.getContext(), senders, contactInboxPopup.findViewById(R.id.shareClipDataInboxPopup));
        else {
            contactsOfMessageAdapter.updateList(senders);
        }

        contactInboxPopup.findViewById(R.id.shareClipDataInboxPopup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String text = "";
                for (ContactOfMessage cm : contactsOfMessageAdapter.getSelectList()) {

                    String name = cm.getName();
                    if (name == null) name = "";

                    text += "Name: " + name + " " + cm.getEmail() + "\n";


                }


                text += "\n";
                text += "Data shared via http://Extime.pro\n";

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, text);
                activityApp.startActivity(Intent.createChooser(shareIntent, "Share"));
            }
        });

        recyclerView.setAdapter(contactsOfMessageAdapter);
    }

    public void getMoreMessages() {

        countLoad = 0;

        for (int c = 0; c < listCredential.size(); c++) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

            Gmail service = new Gmail.Builder(transport, jsonFactory, listCredential.get(c))
                    .setApplicationName(APPLICATION_NAME)
                    .build();

            String user = listCredential.get(c).getSelectedAccountName();


            final List<Message> messages = new ArrayList<Message>();


            BatchRequest batchRequest = null;
            String gmail = null;
            int c_ = 0;

            c_++;


            ListMessagesResponse lll = null;


            Date d = listMessages.get(listMessages.size() - 1).getDate();

            Calendar current = Calendar.getInstance();
            Calendar contactDate = Calendar.getInstance();
            current.setTime(new Date());
            contactDate.setTime(d);
            String timeStr = "";

            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(d);
            Time time = getRandomDate();
            time.setHours(cal1.get(Calendar.HOUR_OF_DAY));
            time.setMinutes(cal1.get(Calendar.MINUTE));
            time.setSeconds(cal1.get(Calendar.SECOND));

            String times = "before:";
            times += contactDate.get(Calendar.MONTH) + 1 + "/" + contactDate.get(Calendar.DAY_OF_MONTH) + "/" + contactDate.get(Calendar.YEAR);


            int oldCount = listMessages.size();


            try {
                lll = service.users().messages().list(user).setQ(/*"after:06/17/2020"*/ times).setMaxResults(30L).execute();

                sortMessagesByTimeNew(lll);

            } catch (IOException e) {
                e.printStackTrace();
            }

            if (lll != null && lll.getMessages() != null) {


                for (int i = 0; i < lll.getMessages().size(); i++) {

                    countLoad++;
                    //updateProgress();

                    Message message = null;
                    EmailMessage emailMessage = new EmailMessage();
                    try {

                        message = service.users().messages().get(user, String.valueOf(lll.getMessages().get(i).getId())).execute();

                        initMoreMessage(message, service, listCredential.get(c));

                        updateListAdapterMore();


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


                //saveMessages();
            }

            if (c_ == 15) {
                c_ = 0;
                try {
                    batchRequest.execute();
                } catch (IllegalStateException | IOException e) {
                    e.printStackTrace();
                }
            }


        }

    }

    boolean getMore = false;

    public void initViews() {

        mainView.findViewById(R.id.folders_Block).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inboxPopup = (FrameLayout) mainView.getRootView().findViewById(R.id.inboxContacts);

                if (inboxPopup.getVisibility() == View.VISIBLE) {
                    mainView.findViewById(R.id.folders_Block).setBackgroundColor(0);
                    inboxPopup.setVisibility(View.GONE);
                    return;
                }

                if (getActivity().findViewById(R.id.framePopupSocial).getVisibility() == View.VISIBLE) {
                    closeOtherPopup();
                    getActivity().findViewById(R.id.framePopupSocial).setVisibility(View.VISIBLE);
                } else
                    closeOtherPopup();


                inboxPopup.setVisibility(View.VISIBLE);
                //mainView.findViewById(R.id.folders_Block).setBackgroundColor(Color.parseColor("#e9e9e9"));
                inboxPopup.setFocusable(true);
                inboxPopup.setClickable(true);
                inboxPopup.requestFocus();
                if (openedViews != null) openedViews.add(inboxPopup);
                // openedFavoritePopup = true;
            }
        });

        popupAccounts = activityApp.findViewById(R.id.accountContainer);
        recyclerAccount = mainView.findViewById(R.id.timelineContainer);
        recyclerAccount.setLayoutManager(new LinearLayoutManager(mainView.getContext()));
        mainView.findViewById(R.id.frameGetMessage).setVisibility(View.GONE);


        recyclerAccount.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)) {


                    if (mainView.findViewById(R.id.progressBarTimeline).getVisibility() == View.GONE) {
                        //Toast.makeText(activityApp, "Load new", Toast.LENGTH_SHORT).show();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                if (!getMore) {
                                    getMore = true;
                                    getMoreMessages();
                                    getMore = false;
                                }

                            }
                        }).start();
                    }


                }
            }
        });


        mainView.findViewById(R.id.frame_google_count).setVisibility(View.GONE);


        popupContacts = mainView.findViewById(R.id.popupMessageContact);
        mainView.setVisibility(View.VISIBLE);


        popupMessage = mainView.findViewById(R.id.popupMessage);

        mainView.findViewById(R.id.frame_google_count).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mainView.findViewById(R.id.frameGetMessage).getVisibility() == View.GONE && mainView.findViewById(R.id.loadBarTimeline).getVisibility() == View.GONE) {
                    popupAccounts.setVisibility(View.VISIBLE);

                    //((CheckBox) mainView.findViewById(R.id.all_select)).setChecked(false);

                    FloatingActionMenu floatingActionMenu = (FloatingActionMenu) activityApp.findViewById(R.id.fabMenuContainer);

                    //getActivity().findViewById(R.id.fab).setVisibility(View.GONE);

                    floatingActionMenu.close(false);
                    floatingActionMenu.setVisibility(View.GONE);


                    recyclerAcc2 = activityApp.findViewById(R.id.recycler_account);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mainView.getContext());
                    recyclerAcc2.setLayoutManager(mLayoutManager);

                    Account[] accounts = AccountManager.get(activityApp).getAccountsByType("com.google");

                    ArrayList<Account> list = new ArrayList<>();
                    for (Account account : accounts) {
                        list.add(account);
                    }

                    SharedPreferences mSettings;
                    mSettings = getActivity().getSharedPreferences("accountUser", Context.MODE_PRIVATE);
                    Set<String> set = mSettings.getStringSet("accounts", null);

                    ((TextView) popupAccounts.findViewById(R.id.count_account)).setText(String.valueOf(set.size()));

                    CheckBox checkBox = popupAccounts.findViewById(R.id.all_accounts_select);

                    ArrayList<Boolean> listCheck = new ArrayList<>();
                    for (Account account : accounts) {
                        //list.add(account);
                        if (set != null && set.contains(account.name)) {
                            listCheck.add(true);

                        } else {
                            listCheck.add(false);

                        }
                    }

                    if (set == null || set.isEmpty()) {
                        ((TextView) popupAccounts.findViewById(R.id.count_account)).setText("0");
                    } else
                        ((TextView) popupAccounts.findViewById(R.id.count_account)).setText(String.valueOf(set.size()));


                    //if (accountAdapter == null) {
                    TextView textView = ((TextView) popupAccounts.findViewById(R.id.count_account));
                    CheckBox checkBox_ = popupAccounts.findViewById(R.id.all_accounts_select);
                    accountAdapter = new AccountAdapter(list, activityApp, popupAccounts.findViewById(R.id.all_account), popupAccounts.findViewById(R.id.apply_account), activityApp.findViewById(R.id.count_acc), listCheck, textView, checkBox_);
                    //  }


                    recyclerAcc2.setAdapter(accountAdapter);


                    ((CheckBox) popupAccounts.findViewById(R.id.all_account)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (((CheckBox) popupAccounts.findViewById(R.id.all_account)).isChecked()) {
                                accountAdapter.updateAllSelect(true);
                                ((TextView) popupAccounts.findViewById(R.id.apply_account)).setTextColor(activityApp.getResources().getColor(R.color.primary));
                            } else {
                                accountAdapter.updateAllSelect(false);
                                ((TextView) popupAccounts.findViewById(R.id.apply_account)).setTextColor(activityApp.getResources().getColor(R.color.gray));
                            }
                        }
                    });


                    popupAccounts.findViewById(R.id.cancel_account).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popupAccounts.setVisibility(View.GONE);
               /* if (mainView.findViewById(R.id.frameChooseAccounts).getVisibility() == View.GONE)
                    mainView.findViewById(R.id.frameGoGmail).setVisibility(View.VISIBLE);*/
                        }
                    });


                    popupAccounts.findViewById(R.id.apply_account).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (activityApp != null) {
                                if (!MainActivity.hasConnection(activityApp)) {
                                    Toast.makeText(activityApp, "Check your internet connection", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            } else {
                                try {
                                    if (!MainActivity.hasConnection(((ProfileFragment) getParentFragment()).activityApp)) {
                                        Toast.makeText(activityApp, "Check your internet connection", Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                } catch (Exception e) {

                                }
                            }
                            if (accountAdapter.getListSelectId().size() > 0) {

                   /* mainView.findViewById(R.id.countAccount).setVisibility(View.GONE);
                    mainView.findViewById(R.id.frame_choose_google_account).setVisibility(View.GONE);
                    mainView.findViewById(R.id.listAccounts).setVisibility(View.GONE);
                    mainView.findViewById(R.id.frame_button_google_message).setVisibility(View.GONE);
                    mainView.findViewById(R.id.spaceMessage).setVisibility(View.GONE);*/

                                popupAccounts.setVisibility(View.GONE);

                                mainView.findViewById(R.id.frame_google_count).setVisibility(View.VISIBLE);
                                ((TextView) mainView.findViewById(R.id.text_count_google_account)).setText(String.valueOf(accountAdapter.getListSelectId().size()));


                                if (accountAdapter.getListSelectId().size() == 1)
                                    Toast.makeText(activityApp, "1 gmail account selected", Toast.LENGTH_SHORT).show();
                                else if (accountAdapter.getListSelectId().size() > 1)
                                    Toast.makeText(activityApp, accountAdapter.getListSelectId().size() + " gmail accounts selected", Toast.LENGTH_SHORT).show();

                                FloatingActionMenu floatingActionMenu = (FloatingActionMenu) activityApp.findViewById(R.id.fabMenuContainer);
                                floatingActionMenu.setVisibility(View.GONE);

                                mainView.findViewById(R.id.frameGetMessage).setVisibility(View.VISIBLE);


                                listCredential = new ArrayList<>();

                                for (int i = 0; i < accountAdapter.getListSelectId().size(); i++) {

                                    GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(
                                            activityApp.getApplicationContext(), Arrays.asList(SCOPES))
                                            .setBackOff(new ExponentialBackOff());

                                    credential.setSelectedAccountName(accountAdapter.getListSelectId().get(i).name);

                                    listCredential.add(credential);

                                }


                                if (messageAdapter != null) messageAdapter.clearList();

                                getMessages();


                                //int px = (int) (54 * getContext().getResources().getDisplayMetrics().density);

                    /*ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mainView.findViewById(R.id.frameChooseAccounts).getLayoutParams();
                    params.topMargin = 0;*/

                                Set<String> set = new HashSet<>();
                                for (int i = 0; i < accountAdapter.getListSelectId().size(); i++) {
                                    set.add(accountAdapter.getListSelectId().get(i).name);
                                }

                                SharedPreferences mSettings;
                                mSettings = activityApp.getSharedPreferences("accountUser", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = mSettings.edit();
                                editor.putStringSet("accounts", set);
                                editor.apply();

                                Gson gson = new Gson();

                                String sender_ = gson.toJson(senders_load);
                                editor.putString("senders_load", sender_);
                                editor.apply();

                                String recipient_ = gson.toJson(recipients_load);
                                editor.putString("recipients_load", recipient_);
                                editor.apply();


                            } else
                                Toast.makeText(activityApp, "Choose account", Toast.LENGTH_SHORT).show();
                        }
                    });


                }
            }
        });


        popupAccounts.findViewById(R.id.cancel_account).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupAccounts.setVisibility(View.GONE);
               /* if (mainView.findViewById(R.id.frameChooseAccounts).getVisibility() == View.GONE)
                    mainView.findViewById(R.id.frameGoGmail).setVisibility(View.VISIBLE);*/
            }
        });


        popupAccounts.findViewById(R.id.apply_account).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activityApp != null) {
                    if (!MainActivity.hasConnection(activityApp)) {
                        Toast.makeText(activityApp, "Check your internet connection", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else {
                    try {
                        if (!MainActivity.hasConnection(((ProfileFragment) getParentFragment()).activityApp)) {
                            Toast.makeText(activityApp, "Check your internet connection", Toast.LENGTH_SHORT).show();
                            return;
                        }

                    } catch (Exception e) {

                    }
                }
                if (accountAdapter.getListSelectId().size() > 0) {

                   /* mainView.findViewById(R.id.countAccount).setVisibility(View.GONE);
                    mainView.findViewById(R.id.frame_choose_google_account).setVisibility(View.GONE);
                    mainView.findViewById(R.id.listAccounts).setVisibility(View.GONE);
                    mainView.findViewById(R.id.frame_button_google_message).setVisibility(View.GONE);
                    mainView.findViewById(R.id.spaceMessage).setVisibility(View.GONE);*/

                    popupAccounts.setVisibility(View.GONE);

                    mainView.findViewById(R.id.frame_google_count).setVisibility(View.VISIBLE);
                    ((TextView) mainView.findViewById(R.id.text_count_google_account)).setText(String.valueOf(accountAdapter.getListSelectId().size()));


                    if (accountAdapter.getListSelectId().size() == 1)
                        Toast.makeText(activityApp, "1 gmail account selected", Toast.LENGTH_SHORT).show();
                    else if (accountAdapter.getListSelectId().size() > 1)
                        Toast.makeText(activityApp, accountAdapter.getListSelectId().size() + " gmail accounts selected", Toast.LENGTH_SHORT).show();

                    FloatingActionMenu floatingActionMenu = (FloatingActionMenu) activityApp.findViewById(R.id.fabMenuContainer);
                    floatingActionMenu.setVisibility(View.GONE);

                    mainView.findViewById(R.id.frameGetMessage).setVisibility(View.VISIBLE);


                    listCredential = new ArrayList<>();

                    for (int i = 0; i < accountAdapter.getListSelectId().size(); i++) {

                        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(
                                activityApp.getApplicationContext(), Arrays.asList(SCOPES))
                                .setBackOff(new ExponentialBackOff());

                        credential.setSelectedAccountName(accountAdapter.getListSelectId().get(i).name);

                        listCredential.add(credential);

                    }


                    if (messageAdapter != null) messageAdapter.clearList();

                    getMessages();


                    //int px = (int) (54 * getContext().getResources().getDisplayMetrics().density);

                    /*ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mainView.findViewById(R.id.frameChooseAccounts).getLayoutParams();
                    params.topMargin = 0;*/

                    Set<String> set = new HashSet<>();
                    for (int i = 0; i < accountAdapter.getListSelectId().size(); i++) {
                        set.add(accountAdapter.getListSelectId().get(i).name);
                    }

                    SharedPreferences mSettings;
                    mSettings = activityApp.getSharedPreferences("accountUser", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = mSettings.edit();
                    editor.putStringSet("accounts", set);
                    editor.commit();


                } else
                    Toast.makeText(activityApp, "Choose account", Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void initGetGmailButton() {

        mainView.findViewById(R.id.frameGoGmail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((CheckBox) popupAccounts.findViewById(R.id.all_account)).setChecked(false);
                popupAccounts.setVisibility(View.VISIBLE);

                mainView.findViewById(R.id.frameGoGmail).setVisibility(View.GONE);

                //mainView.findViewById(R.id.frameChooseAccounts).setVisibility(View.VISIBLE);
                //mainView.findViewById(R.id.spaceMessage).setVisibility(View.VISIBLE);

                //((CheckBox) mainView.findViewById(R.id.all_select)).setChecked(false);

                FloatingActionMenu floatingActionMenu = (FloatingActionMenu) activityApp.findViewById(R.id.fabMenuContainer);

                //getActivity().findViewById(R.id.fab).setVisibility(View.GONE);

                floatingActionMenu.close(false);
                floatingActionMenu.setVisibility(View.GONE);


                if (ContextCompat.checkSelfPermission(activityApp, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(activityApp, Manifest.permission.GET_ACCOUNTS)) {
                        ActivityCompat.requestPermissions(activityApp, new String[]{Manifest.permission.GET_ACCOUNTS}, 1);
                    } else {
                        ActivityCompat.requestPermissions(activityApp, new String[]{Manifest.permission.GET_ACCOUNTS}, 1);
                    }
                }

                //do some stuff
                ArrayList<String> emails = new ArrayList<>();

                Pattern gmailPattern = Patterns.EMAIL_ADDRESS;
                Account[] accounts = AccountManager.get(activityApp).getAccountsByType("com.google");


                //recyclerAccount = mainView.findViewById(R.id.listAccounts);
                recyclerAcc2 = activityApp.findViewById(R.id.recycler_account);

                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mainView.getContext());

                RecyclerView.LayoutManager mLayoutManager2 = new LinearLayoutManager(mainView.getContext());
                recyclerAcc2.setLayoutManager(mLayoutManager);
                //recyclerAccount.setLayoutManager(mLayoutManager2);

                ArrayList<Account> list = new ArrayList<>();
                for (Account account : accounts) {
                    list.add(account);
                }
                //list.remove(1);


                  /*  for (Account account : accounts) {
                        list.add(account);
                    }

                    for (Account account : accounts) {
                        list.add(account);
                    }*/

                SharedPreferences mSettings;
                mSettings = getActivity().getSharedPreferences("accountUser", Context.MODE_PRIVATE);
                Set<String> set = mSettings.getStringSet("accounts", null);


                if (set == null || set.isEmpty()) {
                    ((TextView) popupAccounts.findViewById(R.id.count_account)).setText("0");
                } else
                    ((TextView) popupAccounts.findViewById(R.id.count_account)).setText(String.valueOf(set.size()));

                TextView textView = ((TextView) popupAccounts.findViewById(R.id.count_account));

                CheckBox checkBox = popupAccounts.findViewById(R.id.all_accounts_select);

                ArrayList<Boolean> listCheck = new ArrayList<>();
                for (Account account : accounts) {
                    //list.add(account);
                    if (set != null && set.contains(account.name)) {
                        listCheck.add(true);

                    } else {
                        listCheck.add(false);

                    }
                }


                accountAdapter = new AccountAdapter(list, activityApp, popupAccounts.findViewById(R.id.all_account), popupAccounts.findViewById(R.id.apply_account), activityApp.findViewById(R.id.count_acc), listCheck, textView, checkBox);

                recyclerAcc2.setAdapter(accountAdapter);


               /* if (accounts.length > 1)
                    ((TextView) getActivity().findViewById(R.id.count_acc)).setText(accounts.length + " accounts 0 selected");
                else
                    ((TextView) getActivity().findViewById(R.id.count_acc)).setText("1 account 0 selected");*/


                ((CheckBox) popupAccounts.findViewById(R.id.all_account)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (((CheckBox) popupAccounts.findViewById(R.id.all_account)).isChecked()) {
                            accountAdapter.updateAllSelect(true);
                            ((TextView) popupAccounts.findViewById(R.id.apply_account)).setTextColor(activityApp.getResources().getColor(R.color.primary));
                        } else {
                            accountAdapter.updateAllSelect(false);
                            ((TextView) popupAccounts.findViewById(R.id.apply_account)).setTextColor(activityApp.getResources().getColor(R.color.gray));
                        }
                    }
                });

                popupAccounts.findViewById(R.id.cancel_account).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupAccounts.setVisibility(View.GONE);
                        //if (mainView.findViewById(R.id.frameChooseAccounts).getVisibility() == View.GONE)
                        if (mainView.findViewById(R.id.frame_google_count).getVisibility() == View.GONE)
                            mainView.findViewById(R.id.frameGoGmail).setVisibility(View.VISIBLE);
                    }
                });


                popupAccounts.findViewById(R.id.apply_account).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (accounts != null) {
                            if (!MainActivity.hasConnection(activityApp)) {
                                Toast.makeText(activityApp, "Check your internet connection", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } else {
                            try {
                                if (!MainActivity.hasConnection(((ProfileFragment) getParentFragment()).activityApp)) {
                                    Toast.makeText(activityApp, "Check your internet connection", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                            } catch (Exception e) {
                                ((CreateFragment) getParentFragment()).closeOtherPopup();
                            }
                        }

                        if (accountAdapter.getListSelectId().size() > 0) {

                            //mainView.findViewById(R.id.countAccount).setVisibility(View.GONE);
                            //mainView.findViewById(R.id.frame_choose_google_account).setVisibility(View.GONE);
                            //mainView.findViewById(R.id.listAccounts).setVisibility(View.GONE);
                            //mainView.findViewById(R.id.frame_button_google_message).setVisibility(View.GONE);
                            //mainView.findViewById(R.id.spaceMessage).setVisibility(View.GONE);


                            popupAccounts.setVisibility(View.GONE);

                            mainView.findViewById(R.id.frame_google_count).setVisibility(View.VISIBLE);

                            ((TextView) mainView.findViewById(R.id.text_count_google_account)).setText(String.valueOf(accountAdapter.getListSelectId().size()));


                            if (accountAdapter.getListSelectId().size() == 1)
                                Toast.makeText(activityApp, "1 gmail account selected", Toast.LENGTH_SHORT).show();
                            else if (accountAdapter.getListSelectId().size() > 1)
                                Toast.makeText(activityApp, accountAdapter.getListSelectId().size() + " gmail accounts chosen", Toast.LENGTH_SHORT).show();

                            floatingActionMenu.setVisibility(View.GONE);

                            mainView.findViewById(R.id.frameGetMessage).setVisibility(View.VISIBLE);


                            listCredential = new ArrayList<>();

                            for (int i = 0; i < accountAdapter.getListSelectId().size(); i++) {

                                GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(
                                        activityApp.getApplicationContext(), Arrays.asList(SCOPES))
                                        .setBackOff(new ExponentialBackOff());

                                credential.setSelectedAccountName(accountAdapter.getListSelectId().get(i).name);

                                listCredential.add(credential);

                            }

                            if (messageAdapter != null) messageAdapter.clearList();


                            getMessages();


                            //ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mainView.findViewById(R.id.frameChooseAccounts).getLayoutParams();
                            //params.topMargin = 0;

                            Set<String> set = new HashSet<>();
                            for (int i = 0; i < accountAdapter.getListSelectId().size(); i++) {
                                set.add(accountAdapter.getListSelectId().get(i).name);
                            }

                            SharedPreferences mSettings;
                            mSettings = activityApp.getSharedPreferences("accountUser", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = mSettings.edit();
                            editor.putStringSet("accounts", set);
                            editor.commit();


                            mainView.findViewById(R.id.frame_google_count).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (mainView.findViewById(R.id.frameGetMessage).getVisibility() == View.GONE && mainView.findViewById(R.id.loadBarTimeline).getVisibility() == View.GONE) {
                                        popupAccounts.setVisibility(View.VISIBLE);

                                        //((CheckBox) mainView.findViewById(R.id.all_select)).setChecked(false);

                                        FloatingActionMenu floatingActionMenu = (FloatingActionMenu) activityApp.findViewById(R.id.fabMenuContainer);

                                        //getActivity().findViewById(R.id.fab).setVisibility(View.GONE);

                                        floatingActionMenu.close(false);
                                        floatingActionMenu.setVisibility(View.GONE);


                                        recyclerAcc2 = activityApp.findViewById(R.id.recycler_account);
                                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mainView.getContext());
                                        recyclerAcc2.setLayoutManager(mLayoutManager);


                                        ArrayList<Account> list = new ArrayList<>();
                                        for (Account account : accounts) {
                                            list.add(account);
                                        }

                                        SharedPreferences mSettings;
                                        mSettings = getActivity().getSharedPreferences("accountUser", Context.MODE_PRIVATE);
                                        Set<String> set = mSettings.getStringSet("accounts", null);

                                        ArrayList<Boolean> listCheck = new ArrayList<>();
                                        for (Account account : accounts) {
                                            //list.add(account);
                                            if (set != null && set.contains(account.name)) {
                                                listCheck.add(true);

                                            } else {
                                                listCheck.add(false);

                                            }
                                        }

                                        if (set == null || set.isEmpty()) {
                                            ((TextView) popupAccounts.findViewById(R.id.count_account)).setText("0");
                                        } else
                                            ((TextView) popupAccounts.findViewById(R.id.count_account)).setText(String.valueOf(set.size()));

                                        //if (accountAdapter == null) {

                                        TextView textView = ((TextView) popupAccounts.findViewById(R.id.count_account));

                                        CheckBox checkBox = popupAccounts.findViewById(R.id.all_accounts_select);

                                        accountAdapter = new AccountAdapter(list, activityApp, popupAccounts.findViewById(R.id.all_account), popupAccounts.findViewById(R.id.apply_account), activityApp.findViewById(R.id.count_acc), listCheck, textView, checkBox);
                                        //}

                                        recyclerAcc2.setAdapter(accountAdapter);


                                        ((CheckBox) popupAccounts.findViewById(R.id.all_account)).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if (((CheckBox) popupAccounts.findViewById(R.id.all_account)).isChecked()) {
                                                    accountAdapter.updateAllSelect(true);
                                                    ((TextView) popupAccounts.findViewById(R.id.apply_account)).setTextColor(activityApp.getResources().getColor(R.color.primary));
                                                } else {
                                                    accountAdapter.updateAllSelect(false);
                                                    ((TextView) popupAccounts.findViewById(R.id.apply_account)).setTextColor(activityApp.getResources().getColor(R.color.gray));
                                                }
                                            }
                                        });


                                        popupAccounts.findViewById(R.id.cancel_account).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                popupAccounts.setVisibility(View.GONE);
               /* if (mainView.findViewById(R.id.frameChooseAccounts).getVisibility() == View.GONE)
                    mainView.findViewById(R.id.frameGoGmail).setVisibility(View.VISIBLE);*/
                                            }
                                        });


                                        popupAccounts.findViewById(R.id.apply_account).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if (activityApp != null) {
                                                    if (!MainActivity.hasConnection(activityApp)) {
                                                        Toast.makeText(activityApp, "Check your internet connection", Toast.LENGTH_SHORT).show();
                                                        return;
                                                    }
                                                } else {
                                                    try {
                                                        if (!MainActivity.hasConnection(((ProfileFragment) getParentFragment()).activityApp)) {
                                                            Toast.makeText(activityApp, "Check your internet connection", Toast.LENGTH_SHORT).show();
                                                            return;
                                                        }

                                                    } catch (Exception e) {

                                                    }
                                                }
                                                if (accountAdapter.getListSelectId().size() > 0) {

                   /* mainView.findViewById(R.id.countAccount).setVisibility(View.GONE);
                    mainView.findViewById(R.id.frame_choose_google_account).setVisibility(View.GONE);
                    mainView.findViewById(R.id.listAccounts).setVisibility(View.GONE);
                    mainView.findViewById(R.id.frame_button_google_message).setVisibility(View.GONE);
                    mainView.findViewById(R.id.spaceMessage).setVisibility(View.GONE);*/

                                                    popupAccounts.setVisibility(View.GONE);

                                                    mainView.findViewById(R.id.frame_google_count).setVisibility(View.VISIBLE);
                                                    ((TextView) mainView.findViewById(R.id.text_count_google_account)).setText(String.valueOf(accountAdapter.getListSelectId().size()));


                                                    if (accountAdapter.getListSelectId().size() == 1)
                                                        Toast.makeText(activityApp, "1 gmail account selected", Toast.LENGTH_SHORT).show();
                                                    else if (accountAdapter.getListSelectId().size() > 1)
                                                        Toast.makeText(activityApp, accountAdapter.getListSelectId().size() + " gmail accounts selected", Toast.LENGTH_SHORT).show();

                                                    FloatingActionMenu floatingActionMenu = (FloatingActionMenu) activityApp.findViewById(R.id.fabMenuContainer);
                                                    floatingActionMenu.setVisibility(View.GONE);

                                                    mainView.findViewById(R.id.frameGetMessage).setVisibility(View.VISIBLE);


                                                    listCredential = new ArrayList<>();

                                                    for (int i = 0; i < accountAdapter.getListSelectId().size(); i++) {

                                                        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(
                                                                activityApp.getApplicationContext(), Arrays.asList(SCOPES))
                                                                .setBackOff(new ExponentialBackOff());

                                                        credential.setSelectedAccountName(accountAdapter.getListSelectId().get(i).name);

                                                        listCredential.add(credential);

                                                    }


                                                    if (messageAdapter != null)
                                                        messageAdapter.clearList();

                                                    getMessages();


                                                    //int px = (int) (54 * getContext().getResources().getDisplayMetrics().density);

                    /*ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mainView.findViewById(R.id.frameChooseAccounts).getLayoutParams();
                    params.topMargin = 0;*/

                                                    Set<String> set = new HashSet<>();
                                                    for (int i = 0; i < accountAdapter.getListSelectId().size(); i++) {
                                                        set.add(accountAdapter.getListSelectId().get(i).name);
                                                    }

                                                    SharedPreferences mSettings;
                                                    mSettings = activityApp.getSharedPreferences("accountUser", Context.MODE_PRIVATE);
                                                    SharedPreferences.Editor editor = mSettings.edit();
                                                    editor.putStringSet("accounts", set);
                                                    editor.commit();


                                                } else
                                                    Toast.makeText(activityApp, "Choose account", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    }
                                }
                            });

                        } else
                            Toast.makeText(activityApp, "Choose account", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });

    }

    public void initAccounts() {
        SharedPreferences mSettings;
        mSettings = activityApp.getSharedPreferences("accountUser", Context.MODE_PRIVATE);
        Set<String> set = mSettings.getStringSet("accounts", null);

        if (set == null || set.isEmpty()) {
            mainView.findViewById(R.id.frameGoGmail).setVisibility(View.VISIBLE);
        } else {
            mainView.findViewById(R.id.frameGoGmail).setVisibility(View.GONE);
        }
    }

    public double countMessages = 0;

    public double countLoad = 0;

    public ArrayList<ListMessagesResponse> listMessagesResponses;

    public ArrayList<MessageCredential> messageArrayList;

    public void sortMessagesByTime(ArrayList<MessageCredential> list) {
        Collections.sort(list, new Comparator<MessageCredential>() {
            @Override
            public int compare(MessageCredential message, MessageCredential t1) {
                return t1.getMessage().getId().compareTo(message.getMessage().getId());
            }
        });
    }

    public void sortMessagesByTimeNew(ListMessagesResponse list) {
        if (list != null && list.getMessages() != null) {
            Collections.sort(list.getMessages(), new Comparator<Message>() {
                @Override
                public int compare(Message message, Message t1) {
                    return message.getId().compareTo(t1.getId());
                }
            });
        }
    }

    public void getMessages() {

        senders_load = new ArrayList<>();
        recipients_load = new ArrayList<>();


        messageArrayList = new ArrayList<>();

        mainView.findViewById(R.id.progressBarTimeline).setVisibility(View.VISIBLE);
        mainView.findViewById(R.id.loadBarTimeline).setVisibility(View.VISIBLE);
        ((TextView) mainView.findViewById(R.id.quantity_timeline_load)).setText("0%");


        new Thread(new Runnable() {
            @Override
            public void run() {

                listMessages = new ArrayList<>();

                activityApp.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateKanbanLoad();
                    }
                });


                long countL = 100 / listCredential.size();

                for (int c = 0; c < listCredential.size(); c++) {


                    HttpTransport transport = AndroidHttp.newCompatibleTransport();
                    JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

                    Gmail service = new Gmail.Builder(transport, jsonFactory, listCredential.get(c))
                            .setApplicationName(APPLICATION_NAME)
                            .build();

                    String user = listCredential.get(c).getSelectedAccountName();

                    try {
                        ListMessagesResponse countM1 = service.users().messages().list(user).setMaxResults(countL).execute();
                        if (countM1 != null && countM1.getMessages() != null) {
                            countMessages += countM1.getMessages().size();

                            for (Message message : countM1.getMessages()) {
                                messageArrayList.add(new MessageCredential(message, listCredential.get(c), service));
                            }
                        }
                    } catch (UserRecoverableAuthIOException e1) {

                        e1.printStackTrace();
                        startActivityForResult(e1.getIntent(), REQUEST_AUTHORIZATION);
                        return;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


                sortMessagesByTime(messageArrayList);

                final List<Message> messages = new ArrayList<Message>();


                JsonBatchCallback<Message> callbackMessage = new JsonBatchCallback<Message>() {
                    public void onSuccess(Message message, HttpHeaders responseHeaders) {


                        try {
                            new Thread(() -> {
                                synchronized (messages) {


                                    for (MessageCredential messageCredential : messageArrayList) {
                                        if (messageCredential.getMessage().getId().equals(message.getId())) {
                                            initMessage(message, messageCredential.getGmail(), messageCredential.getAccountCredential());
                                            break;
                                        }
                                    }

                                    messages.add(message);

                                }
                            }).start();
                        } catch (InternalError e) {
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onFailure(GoogleJsonError e, HttpHeaders responseHeaders)
                            throws IOException {

                    }
                };

                BatchRequest batchRequest = null;
                String gmail = null;
                int c_ = 0;

                for (MessageCredential messageCredential : messageArrayList) {


                    if (gmail != null && !gmail.equals(messageCredential.getAccountCredential().getSelectedAccountName())) {
                        try {
                            batchRequest.execute();


                        } catch (IllegalStateException | IOException e) {
                            e.printStackTrace();
                        }
                        c_ = 0;

                        gmail = messageCredential.getAccountCredential().getSelectedAccountName();
                        batchRequest = messageCredential.getGmail().batch();

                    } else {


                    }

                    if (gmail == null) {
                        gmail = messageCredential.getAccountCredential().getSelectedAccountName();
                        batchRequest = messageCredential.getGmail().batch();
                    }


                    String user = messageCredential.getAccountCredential().getSelectedAccountName();


                    c_++;

                    try {
                        messageCredential.getGmail()
                                .users().messages().get(user, String.valueOf(messageCredential.getMessage().getId()))/*.set("format", "raw")*/.queue(batchRequest, callbackMessage);


                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (c_ == 20) {
                        c_ = 0;
                        try {
                            batchRequest.execute();
                        } catch (IllegalStateException | IOException e) {
                            e.printStackTrace();
                        }
                    }

                }

                try {
                    if (batchRequest != null) {
                        batchRequest.execute();
                    }
                } catch (IllegalStateException | IOException | NullPointerException e) {
                    e.printStackTrace();
                }


            }
        }).start();
    }

    public void initNewMessage(final Message message, Gmail service, GoogleAccountCredential
            googleAccountCredential) {

        //countLoad++;
        //updateProgress();

        final EmailMessage emailMessage = new EmailMessage();


        try {


            System.out.println("Message snippet 1 : " + message.getSnippet());

            StringBuilder stringBuilder = new StringBuilder();
            getPlainTextFromMessageParts(message.getPayload().getParts(), stringBuilder);
            byte[] bodyBytes = Base64.decodeBase64(stringBuilder.toString());
            String text = new String(bodyBytes, StandardCharsets.UTF_8);

            System.out.println("all message = " + text);

            if (text != null && !text.isEmpty())
                emailMessage.setMessage(text.trim());
            else {
                if (message.getPayload().getBody().getData() != null && !message.getPayload().getBody().getData().isEmpty())
                    emailMessage.setMessage(StringUtils.newStringUtf8(Base64.decodeBase64(message.getPayload().getBody().getData())));
                else
                    emailMessage.setMessage(message.getSnippet().trim());
            }


            String type = "No Subject";
            String email_ = "";
            String name_ = "";

            boolean cont = true;
            for (int i2 = 0; i2 < message.getPayload().getHeaders().size(); i2++) {
                System.out.println(i2 + ". " + message.getPayload().getHeaders().get(i2).getValue() + ", type = " + message.getPayload().getHeaders().get(i2).getName());

                if (message.getPayload().getHeaders().get(i2).getName().equalsIgnoreCase("Subject")) {
                    if (!message.getPayload().getHeaders().get(i2).getValue().equalsIgnoreCase("") /*&& !message.getPayload().getHeaders().get(i2).getValue().equalsIgnoreCase("1.0")*/) {
                        type = message.getPayload().getHeaders().get(i2).getValue();
                    }
                }

                if (message.getPayload().getHeaders().get(i2).getName().equalsIgnoreCase("Return-Path")) {
                    email_ = message.getPayload().getHeaders().get(i2).getValue();
                    if (email_ != null) {
                        email_ = email_.replace("<", "");
                        email_ = email_.replace(">", "");
                    } else {
                        email_ = "";
                    }
                }

                if (message.getPayload().getHeaders().get(i2).getName().equalsIgnoreCase("From")) {
                    name_ = message.getPayload().getHeaders().get(i2).getValue();
                }
            }
            //if (!cont) continue;

            emailMessage.setTitle(type);  //=======================set title

            if (email_contact == null) {
                email_contact = new ArrayList<>();
            }

            if (email_.equals("")) {
                try {

                    int in_s = name_.indexOf("<");
                    int in_e = name_.indexOf(">");

                    email_ = name_.substring(in_s + 1, in_e);

                    System.out.println("new email = " + email_);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            email_contact.add(email_);

            if (emailMessage.getTitle().isEmpty()) {
                emailMessage.setTitle("No subject");
            }

            //mailMessage.setAccount(new ai.extime.Models.Account(listCredential.get(c).getSelectedAccountName(), listCredential.get(c).getSelectedAccountName())); //=============================set account

            if (name_ != null) {
                name_ = name_.replace(email_, "");
                int ind = name_.indexOf('<');
                if (ind != -1) {
                    try {
                        name_ = name_.substring(0, ind - 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                name_ = name_.replace("<", "");


                name_ = name_.trim();
            } else {
                name_ = "";
            }

            emailMessage.setAccount(new ai.extime.Models.Account(name_, email_));


            for (String la : message.getLabelIds()) {
                System.out.println("LABLE = " + la);
            }

            emailMessage.setEmail_2(googleAccountCredential.getSelectedAccountName());

            Timestamp stamp = new Timestamp(message.getInternalDate());
            Date date = new Date(stamp.getTime());


            emailMessage.setDate(date); //==========================set date

            emailMessage.setMessageData(fileType.getTypeOFFile(message, service, googleAccountCredential.getSelectedAccountName()));

            emailMessage.setF_Message(message);

            emailMessage.setListOfTypeMessage(message.getLabelIds());

            emailMessage.setCredential(googleAccountCredential);

            emailMessage.setListHeaders(message.getPayload().getHeaders());

            emailMessage.setThreadID(message.getThreadId());

            //
            emailMessage.setId(message.getId());
            emailMessage.setUser(googleAccountCredential.getSelectedAccountName());


            boolean check_id = false;
            for (EmailMessage m : listMessages) {
                if (m.getId().equals(emailMessage.getId()))
                    check_id = true;
            }

            if (!check_id)
                listMessages.add(0, emailMessage);


            //==================================================================================================


            if ( /*mainView.findViewById(R.id.progressBarTimeline).getVisibility() == View.GONE &&*/ emailMessage != null) {

                SharedPreferences mSettings;
                mSettings = getActivity().getSharedPreferences("accountUser", Context.MODE_PRIVATE);
                Set<String> setAcc = mSettings.getStringSet("accounts", null);

                if (setAcc == null || setAcc.isEmpty()) {
                    return;
                }


                EmailMessage messageE = emailMessage;
                if (messageE.getF_Message().getLabelIds().contains("INBOX")) {

                    for (int i2 = 0; i2 < messageE.getF_Message().getPayload().getHeaders().size(); i2++) {
                        System.out.println(i2 + ". " + messageE.getF_Message().getPayload().getHeaders().get(i2).getValue() + ", type = " + messageE.getF_Message().getPayload().getHeaders().get(i2).getName());

                        if (messageE.getF_Message().getPayload().getHeaders().get(i2).getName().equalsIgnoreCase("From")) {
                            String send = messageE.getF_Message().getPayload().getHeaders().get(i2).getValue();
                            String email = ClipboardType.getEmail(send);

                            ContactOfMessage senderL = new ContactOfMessage();


                            if (email != null) {
                                senderL.setEmail(email);
                                send = send.replace(email, "");
                                send = send.replace("<", "");
                                send = send.replace(">", "");
                                send = send.replaceAll("\"", "");

                                send = send.trim();

                                if (send.length() > 0) senderL.setName(send);


                                if (setAcc.contains(email)) senderL.setGoogle(true);

                                senderL.setSender();


                                if (senders_load == null) {
                                    senders_load = new ArrayList<>();
                                }

                                if (recipients_load == null) {
                                    recipients_load = new ArrayList<>();
                                }


                                boolean ck = false;
                                for (ContactOfMessage s : senders_load) {
                                    if (s.getEmail().equals(email)) {

                                        if (s.getName() == null || s.getName().trim().isEmpty()) {
                                            if (senderL.getName() != null && !senderL.getName().trim().isEmpty()) {
                                                s.setName(senderL.getName());
                                            }
                                        }

                                        ck = true;
                                        break;
                                    }
                                }
                                if (!ck) senders_load.add(senderL);

                            }

                        }


                        if (messageE.getF_Message().getPayload().getHeaders().get(i2).getName().equalsIgnoreCase("To")) {
                            String send = messageE.getF_Message().getPayload().getHeaders().get(i2).getValue();
                            String email = ClipboardType.getEmail(send);

                            ContactOfMessage senderL = new ContactOfMessage();


                            if (email != null) {
                                senderL.setEmail(email);
                                send = send.replace(email, "");
                                send = send.replace("<", "");
                                send = send.replace(">", "");
                                send = send.replaceAll("\"", "");

                                send = send.trim();

                                if (send.length() > 0) senderL.setName(send);


                                if (setAcc.contains(email)) senderL.setGoogle(true);

                                senderL.setRecipient();


                                boolean ck = false;
                                for (ContactOfMessage s : recipients_load) {
                                    if (s.getEmail().equals(email)) {
                                        if (s.getName() == null || s.getName().trim().isEmpty()) {
                                            if (senderL.getName() != null && !senderL.getName().trim().isEmpty()) {
                                                s.setName(senderL.getName());
                                            }
                                        }

                                        ck = true;
                                        break;
                                    }
                                }

                                if (!ck) recipients_load.add(senderL);

                            }

                        }


                    }
                }


                //senders_load.addAll(recipients_load);

                for (ContactOfMessage contactOfMessage : senders_load) {
                    if (!contactOfMessage.isCheckSearch()) {
                        Contact contact = ContactCacheService.find2(contactOfMessage.getEmail(), ClipboardEnum.EMAIL, getActivity());
                        if (contact != null) {
                            contactOfMessage.setSearchContact(contact);
                        }
                        contactOfMessage.setCheckSearch(true);
                    }
                }

                for (ContactOfMessage contactOfMessage : recipients_load) {
                    if (!contactOfMessage.isCheckSearch()) {
                        Contact contact = ContactCacheService.find2(contactOfMessage.getEmail(), ClipboardEnum.EMAIL, getActivity());
                        if (contact != null) {
                            contactOfMessage.setSearchContact(contact);
                        }
                        contactOfMessage.setCheckSearch(true);
                    }
                }


                try {
                    if (contactInboxPopup != null && contactInboxPopup.getVisibility() == View.VISIBLE && Integer.parseInt(((TextView) mainView.findViewById(R.id.barTemplates_count_timeline)).getText().toString()) != (senders_load.size() + recipients_load.size())) {

                        activityApp.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateInboxPopup();
                            }
                        });

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }


                activityApp.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((TextView) mainView.findViewById(R.id.barTemplates_count_timeline)).setText(String.valueOf(senders_load.size() + recipients_load.size()));
                    }
                });


            }

            //=========================================================================================


            //Log.d("Body", String.valueOf(message));

        } catch (Exception t) {
            t.printStackTrace();
            System.out.println("mail crash");
            // Log.e("My app", "not done");
        }

        //updateListAdapter();


    }


    public void initMoreMessage(final Message message, Gmail service, GoogleAccountCredential
            googleAccountCredential) {

        //countLoad++;
        //updateProgress();

        final EmailMessage emailMessage = new EmailMessage();


        try {


            System.out.println("Message snippet 1 : " + message.getSnippet());

            StringBuilder stringBuilder = new StringBuilder();
            getPlainTextFromMessageParts(message.getPayload().getParts(), stringBuilder);
            byte[] bodyBytes = Base64.decodeBase64(stringBuilder.toString());
            String text = new String(bodyBytes, StandardCharsets.UTF_8);



           /* String mimeType = message.getPayload().getMimeType();
            List<MessagePart> parts = message.getPayload().getParts();
            if (mimeType.contains("alternative")) {

                String mailBody = "";
                for (MessagePart part : parts) {
                    mailBody = new String(Base64.decodeBase64(part.getBody()
                            .getData().getBytes()));

                }

            }*/

            if (text != null && !text.isEmpty())
                emailMessage.setMessage(text.trim());
            else {
                if (message.getPayload().getBody().getData() != null && !message.getPayload().getBody().getData().isEmpty())
                    emailMessage.setMessage(StringUtils.newStringUtf8(Base64.decodeBase64(message.getPayload().getBody().getData())));
                else
                    emailMessage.setMessage(message.getSnippet().trim());
            }


            String type = "No Subject";
            String email_ = "";
            String name_ = "";

            boolean cont = true;
            for (int i2 = 0; i2 < message.getPayload().getHeaders().size(); i2++) {
                System.out.println(i2 + ". " + message.getPayload().getHeaders().get(i2).getValue() + ", type = " + message.getPayload().getHeaders().get(i2).getName());

                if (message.getPayload().getHeaders().get(i2).getName().equalsIgnoreCase("Subject")) {
                    if (!message.getPayload().getHeaders().get(i2).getValue().equalsIgnoreCase("") /*&& !message.getPayload().getHeaders().get(i2).getValue().equalsIgnoreCase("1.0")*/) {
                        type = message.getPayload().getHeaders().get(i2).getValue();
                    }
                }

                if (message.getPayload().getHeaders().get(i2).getName().equalsIgnoreCase("Return-Path")) {
                    email_ = message.getPayload().getHeaders().get(i2).getValue();
                    if (email_ != null) {
                        email_ = email_.replace("<", "");
                        email_ = email_.replace(">", "");
                    } else {
                        email_ = "";
                    }
                }

                if (message.getPayload().getHeaders().get(i2).getName().equalsIgnoreCase("From")) {
                    name_ = message.getPayload().getHeaders().get(i2).getValue();
                }
            }
            //if (!cont) continue;

            emailMessage.setTitle(type);  //=======================set title

            if (email_contact == null) {
                email_contact = new ArrayList<>();
            }

            if (email_.equals("")) {
                try {

                    int in_s = name_.indexOf("<");
                    int in_e = name_.indexOf(">");

                    if (in_s != -1 && in_e != -1)
                        email_ = name_.substring(in_s + 1, in_e);

                    System.out.println("new email = " + email_);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            email_contact.add(email_);

            if (emailMessage.getTitle().isEmpty()) {
                emailMessage.setTitle("No subject");
            }

            //mailMessage.setAccount(new ai.extime.Models.Account(listCredential.get(c).getSelectedAccountName(), listCredential.get(c).getSelectedAccountName())); //=============================set account

            if (name_ != null) {
                name_ = name_.replace(email_, "");
                int ind = name_.indexOf('<');
                if (ind != -1) {
                    try {
                        name_ = name_.substring(0, ind - 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                name_ = name_.replace("<", "");


                name_ = name_.trim();
            } else {
                name_ = "";
            }

            emailMessage.setAccount(new ai.extime.Models.Account(name_, email_));


            for (String la : message.getLabelIds()) {
                System.out.println("LABLE = " + la);
            }

            emailMessage.setEmail_2(googleAccountCredential.getSelectedAccountName());

            Timestamp stamp = new Timestamp(message.getInternalDate());
            Date date = new Date(stamp.getTime());


            emailMessage.setDate(date); //==========================set date

            emailMessage.setMessageData(fileType.getTypeOFFile(message, service, googleAccountCredential.getSelectedAccountName()));

            emailMessage.setF_Message(message);

            emailMessage.setListOfTypeMessage(message.getLabelIds());

            emailMessage.setCredential(googleAccountCredential);

            emailMessage.setListHeaders(message.getPayload().getHeaders());

            emailMessage.setThreadID(message.getThreadId());

            //
            emailMessage.setId(message.getId());
            emailMessage.setUser(googleAccountCredential.getSelectedAccountName());


            boolean check_id = false;
            for (EmailMessage m : listMessages) {
                if (m.getId().equals(emailMessage.getId()))
                    check_id = true;
            }

            if (!check_id)
                listMessages.add(emailMessage);


            //==================================================================================================


            if ( /*mainView.findViewById(R.id.progressBarTimeline).getVisibility() == View.GONE &&*/ emailMessage != null) {

                SharedPreferences mSettings;
                mSettings = getActivity().getSharedPreferences("accountUser", Context.MODE_PRIVATE);
                Set<String> setAcc = mSettings.getStringSet("accounts", null);

                if (setAcc == null || setAcc.isEmpty()) {
                    return;
                }


                EmailMessage messageE = emailMessage;
                if (messageE.getF_Message().getLabelIds().contains("INBOX")) {

                    for (int i2 = 0; i2 < messageE.getF_Message().getPayload().getHeaders().size(); i2++) {
                        System.out.println(i2 + ". " + messageE.getF_Message().getPayload().getHeaders().get(i2).getValue() + ", type = " + messageE.getF_Message().getPayload().getHeaders().get(i2).getName());

                        if (messageE.getF_Message().getPayload().getHeaders().get(i2).getName().equalsIgnoreCase("From")) {
                            String send = messageE.getF_Message().getPayload().getHeaders().get(i2).getValue();
                            String email = ClipboardType.getEmail(send);

                            ContactOfMessage senderL = new ContactOfMessage();


                            if (email != null) {
                                senderL.setEmail(email);
                                send = send.replace(email, "");
                                send = send.replace("<", "");
                                send = send.replace(">", "");
                                send = send.replaceAll("\"", "");

                                send = send.trim();

                                if (send.length() > 0) senderL.setName(send);


                                if (setAcc.contains(email)) senderL.setGoogle(true);

                                senderL.setSender();


                                if (senders_load == null) {
                                    senders_load = new ArrayList<>();
                                }

                                if (recipients_load == null) {
                                    recipients_load = new ArrayList<>();
                                }


                                boolean ck = false;
                                for (ContactOfMessage s : senders_load) {
                                    if (s.getEmail().equals(email)) {

                                        if (s.getName() == null || s.getName().trim().isEmpty()) {
                                            if (senderL.getName() != null && !senderL.getName().trim().isEmpty()) {
                                                s.setName(senderL.getName());
                                            }
                                        }

                                        ck = true;
                                        break;
                                    }
                                }
                                if (!ck) senders_load.add(senderL);

                            }

                        }


                        if (messageE.getF_Message().getPayload().getHeaders().get(i2).getName().equalsIgnoreCase("To")) {
                            String send = messageE.getF_Message().getPayload().getHeaders().get(i2).getValue();
                            String email = ClipboardType.getEmail(send);

                            ContactOfMessage senderL = new ContactOfMessage();


                            if (email != null) {
                                senderL.setEmail(email);
                                send = send.replace(email, "");
                                send = send.replace("<", "");
                                send = send.replace(">", "");
                                send = send.replaceAll("\"", "");

                                send = send.trim();

                                if (send.length() > 0) senderL.setName(send);


                                if (setAcc.contains(email)) senderL.setGoogle(true);

                                senderL.setRecipient();


                                boolean ck = false;
                                for (ContactOfMessage s : recipients_load) {
                                    if (s.getEmail().equals(email)) {
                                        if (s.getName() == null || s.getName().trim().isEmpty()) {
                                            if (senderL.getName() != null && !senderL.getName().trim().isEmpty()) {
                                                s.setName(senderL.getName());
                                            }
                                        }

                                        ck = true;
                                        break;
                                    }
                                }

                                if (!ck) recipients_load.add(senderL);

                            }

                        }


                    }
                }


                //senders_load.addAll(recipients_load);

                for (ContactOfMessage contactOfMessage : senders_load) {
                    if (!contactOfMessage.isCheckSearch()) {
                        Contact contact = ContactCacheService.find2(contactOfMessage.getEmail(), ClipboardEnum.EMAIL, getActivity());
                        if (contact != null) {
                            contactOfMessage.setSearchContact(contact);
                        }
                        contactOfMessage.setCheckSearch(true);
                    }
                }

                for (ContactOfMessage contactOfMessage : recipients_load) {
                    if (!contactOfMessage.isCheckSearch()) {
                        Contact contact = ContactCacheService.find2(contactOfMessage.getEmail(), ClipboardEnum.EMAIL, getActivity());
                        if (contact != null) {
                            contactOfMessage.setSearchContact(contact);
                        }
                        contactOfMessage.setCheckSearch(true);
                    }
                }


                try {
                    if (contactInboxPopup != null && contactInboxPopup.getVisibility() == View.VISIBLE && Integer.parseInt(((TextView) mainView.findViewById(R.id.barTemplates_count_timeline)).getText().toString()) != (senders_load.size() + recipients_load.size())) {

                        activityApp.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateInboxPopup();
                            }
                        });

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }


                activityApp.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((TextView) mainView.findViewById(R.id.barTemplates_count_timeline)).setText(String.valueOf(senders_load.size() + recipients_load.size()));
                    }
                });


            }

            //=========================================================================================


            //Log.d("Body", String.valueOf(message));

        } catch (Exception t) {
            t.printStackTrace();
        }

    }

    public void initMessage(final Message message, Gmail service, GoogleAccountCredential
            googleAccountCredential) {


        countLoad++;
        updateProgress();

        final EmailMessage emailMessage = new EmailMessage();


        try {


            System.out.println("Message snippet 1 : " + message.getSnippet());

            StringBuilder stringBuilder = new StringBuilder();
            getPlainTextFromMessageParts(message.getPayload().getParts(), stringBuilder);
            byte[] bodyBytes = Base64.decodeBase64(stringBuilder.toString());
            String text = new String(bodyBytes, StandardCharsets.UTF_8);


            if (text != null && !text.isEmpty())
                emailMessage.setMessage(text.trim());
            else {
                if (message.getPayload().getBody().getData() != null && !message.getPayload().getBody().getData().isEmpty())
                    emailMessage.setMessage(StringUtils.newStringUtf8(Base64.decodeBase64(message.getPayload().getBody().getData())));
                else
                    emailMessage.setMessage(message.getSnippet().trim());
            }

            //emailMessage.setMessage(text.trim());


            String type = "No Subject";
            String email_ = "";
            String name_ = "";

            boolean cont = true;
            for (int i2 = 0; i2 < message.getPayload().getHeaders().size(); i2++) {
                System.out.println(i2 + ". " + message.getPayload().getHeaders().get(i2).getValue() + ", type = " + message.getPayload().getHeaders().get(i2).getName());

                if (message.getPayload().getHeaders().get(i2).getName().equalsIgnoreCase("Subject")) {
                    if (!message.getPayload().getHeaders().get(i2).getValue().equalsIgnoreCase("") /*&& !message.getPayload().getHeaders().get(i2).getValue().equalsIgnoreCase("1.0")*/) {
                        type = message.getPayload().getHeaders().get(i2).getValue();
                    }
                }

                if (message.getPayload().getHeaders().get(i2).getName().equalsIgnoreCase("Return-Path")) {
                    email_ = message.getPayload().getHeaders().get(i2).getValue();
                    if (email_ != null) {
                        email_ = email_.replace("<", "");
                        email_ = email_.replace(">", "");
                    } else {
                        email_ = "";
                    }
                }

                if (message.getPayload().getHeaders().get(i2).getName().equalsIgnoreCase("From")) {
                    name_ = message.getPayload().getHeaders().get(i2).getValue();
                }
            }
            //if (!cont) continue;

            emailMessage.setTitle(type);  //=======================set title

            if (email_contact == null) {
                email_contact = new ArrayList<>();
            }

            if (email_.equals("")) {
                try {

                    int in_s = name_.indexOf("<");
                    int in_e = name_.indexOf(">");

                    email_ = name_.substring(in_s + 1, in_e);

                    System.out.println("new email = " + email_);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            email_contact.add(email_);

            if (emailMessage.getTitle().isEmpty()) {
                emailMessage.setTitle("No subject");
            }

            //mailMessage.setAccount(new ai.extime.Models.Account(listCredential.get(c).getSelectedAccountName(), listCredential.get(c).getSelectedAccountName())); //=============================set account

            if (name_ != null) {
                name_ = name_.replace(email_, "");
                int ind = name_.indexOf('<');
                if (ind != -1) {
                    try {
                        name_ = name_.substring(0, ind - 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                name_ = name_.replace("<", "");


                name_ = name_.trim();
            } else {
                name_ = "";
            }

            emailMessage.setAccount(new ai.extime.Models.Account(name_, email_));


            for (String la : message.getLabelIds()) {
                System.out.println("LABLE = " + la);
            }

            emailMessage.setEmail_2(googleAccountCredential.getSelectedAccountName());

            Timestamp stamp = new Timestamp(message.getInternalDate());
            Date date = new Date(stamp.getTime());


            emailMessage.setDate(date); //==========================set date

            emailMessage.setMessageData(fileType.getTypeOFFile(message, service, googleAccountCredential.getSelectedAccountName()));

            emailMessage.setF_Message(message);

            emailMessage.setListOfTypeMessage(message.getLabelIds());

            emailMessage.setCredential(googleAccountCredential);

            emailMessage.setListHeaders(message.getPayload().getHeaders());

            //
            emailMessage.setId(message.getId());
            emailMessage.setUser(googleAccountCredential.getSelectedAccountName());

           /* new Thread(new Runnable() {
                @Override
                public void run() {
                    try {


                        ArrayList<Clipboard> listC = new ArrayList<>(ClipboardType.getListDataClipboard(emailMessage.getMessage(), activityApp));


                        if (listC != null
                                && !listC.isEmpty()
                                && listC.get(0) != null
                                && listC.get(0).getListClipboards() != null
                                && !listC.get(0).getListClipboards().isEmpty()) {

                            for (int q = 0; q < listC.get(0).getListClipboards().size(); q++) {
                                for (int j = 1; j < listC.get(0).getListClipboards().size(); j++) {
                                    if (q != j) {
                                        if (listC.get(0).getListClipboards().get(q).getValueCopy().equalsIgnoreCase(listC.get(0).getListClipboards().get(j).getValueCopy())) {
                                            listC.get(0).getListClipboards().remove(j);
                                            j--;
                                        }
                                    }
                                }
                            }


                            for (int iq = 0; iq < listC.get(0).getListClipboards().size() - 2; iq++) {

                                MessageData messageData = new MessageData();


                                messageData.setClipboard(listC.get(0).getListClipboards().get(iq));

                                           *//* Contact cc = ContactCacheService.find2(messageData.getClipboard().getValueCopy(), messageData.getClipboard().getType(), null);
                                            if (cc != null)
                                                messageData.getClipboard().addContactToListSearch(cc);*//*


                                if (!listC.get(0).getListClipboards().get(iq).getType().equals(ClipboardEnum.HASHTAG) && !listC.get(0).getListClipboards().get(iq).getValueCopy().isEmpty())
                                    emailMessage.getMessageData().add(messageData);
                            }

                        }
                    } catch (Exception w) {
                        w.printStackTrace();
                    }

                }
            }).start();*/


            listMessages.add(emailMessage);


            //==================================================================================================


            if ( /*mainView.findViewById(R.id.progressBarTimeline).getVisibility() == View.GONE &&*/ emailMessage != null) {

                SharedPreferences mSettings;
                mSettings = getActivity().getSharedPreferences("accountUser", Context.MODE_PRIVATE);
                Set<String> setAcc = mSettings.getStringSet("accounts", null);

                if (setAcc == null || setAcc.isEmpty()) {
                    return;
                }


                EmailMessage messageE = emailMessage;
                if (messageE.getF_Message().getLabelIds().contains("INBOX")) {

                    for (int i2 = 0; i2 < messageE.getF_Message().getPayload().getHeaders().size(); i2++) {
                        System.out.println(i2 + ". " + messageE.getF_Message().getPayload().getHeaders().get(i2).getValue() + ", type = " + messageE.getF_Message().getPayload().getHeaders().get(i2).getName());

                        if (messageE.getF_Message().getPayload().getHeaders().get(i2).getName().equalsIgnoreCase("From")) {
                            String send = messageE.getF_Message().getPayload().getHeaders().get(i2).getValue();
                            String email = ClipboardType.getEmail(send);

                            ContactOfMessage senderL = new ContactOfMessage();


                            if (email != null) {
                                senderL.setEmail(email);
                                send = send.replace(email, "");
                                send = send.replace("<", "");
                                send = send.replace(">", "");
                                send = send.replaceAll("\"", "");

                                send = send.trim();

                                if (send.length() > 0) senderL.setName(send);


                                if (setAcc.contains(email)) senderL.setGoogle(true);

                                senderL.setSender();


                                boolean ck = false;
                                for (ContactOfMessage s : senders_load) {
                                    if (s.getEmail().equals(email)) {

                                        if (s.getName() == null || s.getName().trim().isEmpty()) {
                                            if (senderL.getName() != null && !senderL.getName().trim().isEmpty()) {
                                                s.setName(senderL.getName());
                                            }
                                        }

                                        ck = true;
                                        break;
                                    }
                                }
                                if (!ck) senders_load.add(senderL);

                            }

                        }


                        if (messageE.getF_Message().getPayload().getHeaders().get(i2).getName().equalsIgnoreCase("To")) {
                            String send = messageE.getF_Message().getPayload().getHeaders().get(i2).getValue();
                            String email = ClipboardType.getEmail(send);

                            ContactOfMessage senderL = new ContactOfMessage();


                            if (email != null) {
                                senderL.setEmail(email);
                                send = send.replace(email, "");
                                send = send.replace("<", "");
                                send = send.replace(">", "");
                                send = send.replaceAll("\"", "");

                                send = send.trim();

                                if (send.length() > 0) senderL.setName(send);


                                if (setAcc.contains(email)) senderL.setGoogle(true);

                                senderL.setRecipient();


                                boolean ck = false;
                                for (ContactOfMessage s : recipients_load) {
                                    if (s.getEmail().equals(email)) {
                                        if (s.getName() == null || s.getName().trim().isEmpty()) {
                                            if (senderL.getName() != null && !senderL.getName().trim().isEmpty()) {
                                                s.setName(senderL.getName());
                                            }
                                        }

                                        ck = true;
                                        break;
                                    }
                                }

                                if (!ck) recipients_load.add(senderL);

                            }

                        }


                    }
                }


                //senders_load.addAll(recipients_load);

                for (ContactOfMessage contactOfMessage : senders_load) {
                    if (!contactOfMessage.isCheckSearch()) {
                        Contact contact = ContactCacheService.find2(contactOfMessage.getEmail(), ClipboardEnum.EMAIL, getActivity());
                        if (contact != null) {
                            contactOfMessage.setSearchContact(contact);
                        }
                        contactOfMessage.setCheckSearch(true);
                    }
                }

                for (ContactOfMessage contactOfMessage : recipients_load) {
                    if (!contactOfMessage.isCheckSearch()) {
                        Contact contact = ContactCacheService.find2(contactOfMessage.getEmail(), ClipboardEnum.EMAIL, getActivity());
                        if (contact != null) {
                            contactOfMessage.setSearchContact(contact);
                        }
                        contactOfMessage.setCheckSearch(true);
                    }
                }


                try {
                    if (contactInboxPopup != null && contactInboxPopup.getVisibility() == View.VISIBLE && Integer.parseInt(((TextView) mainView.findViewById(R.id.barTemplates_count_timeline)).getText().toString()) != (senders_load.size() + recipients_load.size())) {

                        activityApp.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateInboxPopup();
                            }
                        });

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }


                activityApp.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((TextView) mainView.findViewById(R.id.barTemplates_count_timeline)).setText(String.valueOf(senders_load.size() + recipients_load.size()));
                    }
                });


            }

            //=========================================================================================


            try {
                System.out.println("FILE = " + message.getPayload().getFilename());
            } catch (Exception e) {
                e.printStackTrace();
            }


            //Log.d("Body", String.valueOf(message));

        } catch (Exception t) {
            t.printStackTrace();
            System.out.println("mail crash");
            // Log.e("My app", "not done");
        }

        updateListAdapter();

        if (mode == 2) {
            activityApp.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateKanbanLoad();
                }
            });

        }


    }

    public void updateKanbanLoad() {


        if (/*typeCard.equals(TypeCardTimeline.ROWS) || */typeCard.equals(TypeCardTimeline.BOXES)) {
            int width_medium = (int) (160 * getResources().getDisplayMetrics().density);
            int height_medium = (int) (40 * getResources().getDisplayMetrics().density);

            mainView.findViewById(R.id.head_card_1).setLayoutParams(new LinearLayout.LayoutParams(width_medium, height_medium));
            mainView.findViewById(R.id.head_card_2).setLayoutParams(new LinearLayout.LayoutParams(width_medium, height_medium));
            mainView.findViewById(R.id.head_card_3).setLayoutParams(new LinearLayout.LayoutParams(width_medium, height_medium));
            mainView.findViewById(R.id.head_card_4).setLayoutParams(new LinearLayout.LayoutParams(width_medium, height_medium));
            mainView.findViewById(R.id.head_card_5).setLayoutParams(new LinearLayout.LayoutParams(width_medium, height_medium));
            mainView.findViewById(R.id.head_card_6).setLayoutParams(new LinearLayout.LayoutParams(width_medium, height_medium));
            mainView.findViewById(R.id.head_card_7).setLayoutParams(new LinearLayout.LayoutParams(width_medium, height_medium));
            mainView.findViewById(R.id.head_card_8).setLayoutParams(new LinearLayout.LayoutParams(width_medium, height_medium));
        }


        listInbox = new ArrayList<>();
        listUnread = new ArrayList<>();
        listfavourite = new ArrayList<>();
        listSent = new ArrayList<>();
        listImportant = new ArrayList<>();
        listDrafts = new ArrayList<>();
        listSpam = new ArrayList<>();
        listTrash = new ArrayList<>();


        for (EmailMessage i : listMessages) {
            if (i.getListOfTypeMessage().contains("INBOX"))
                listInbox.add(i);
            if (i.getListOfTypeMessage().contains("UNREAD"))
                listUnread.add(i);
            if (i.getListOfTypeMessage().contains("STARRED") && !listfavourite.contains(i))
                listfavourite.add(i);
            if (i.getListOfTypeMessage().contains("SENT"))
                listSent.add(i);
            if (i.getListOfTypeMessage().contains("IMPORTANT"))
                listImportant.add(i);
            if (i.getListOfTypeMessage().contains("DRAFT"))
                listDrafts.add(i);
            if (i.getListOfTypeMessage().contains("SPAM"))
                listSpam.add(i);
            if (i.getListOfTypeMessage().contains("TRASH"))
                listTrash.add(i);
        }

        if (typeCard.equals(TypeCardTimeline.BOXES) || typeCard.equals(TypeCardTimeline.ROWS)) {

            ((TextView) mainView.findViewById(R.id.countType_1)).setText(String.valueOf(listInbox.size()));
            ((TextView) mainView.findViewById(R.id.countType_2)).setText(String.valueOf(listUnread.size()));
            ((TextView) mainView.findViewById(R.id.countType_3)).setText(String.valueOf(listfavourite.size()));
            ((TextView) mainView.findViewById(R.id.countType_4)).setText(String.valueOf(listSent.size()));
            ((TextView) mainView.findViewById(R.id.countType_5)).setText(String.valueOf(listImportant.size()));
            ((TextView) mainView.findViewById(R.id.countType_6)).setText(String.valueOf(listDrafts.size()));
            ((TextView) mainView.findViewById(R.id.countType_7)).setText(String.valueOf(listSpam.size()));
            ((TextView) mainView.findViewById(R.id.countType_8)).setText(String.valueOf(listTrash.size()));

            if (kanbanAdapter_1 == null || kanbanAdapter_2 == null || kanbanAdapter_3 == null || kanbanAdapter_4 == null || kanbanAdapter_5 == null || kanbanAdapter_6 == null || kanbanAdapter_7 == null || kanbanAdapter_8 == null
                    || kanbanMessageAdapter_1 == null || kanbanMessageAdapter_2 == null || kanbanMessageAdapter_3 == null || kanbanMessageAdapter_4 == null || kanbanMessageAdapter_5 == null || kanbanMessageAdapter_6 == null
                    || kanbanMessageAdapter_7 == null || kanbanMessageAdapter_8 == null) {


                if (recyclerView_k == null || recyclerView_k_1 == null || recyclerView_k_2 == null || recyclerView_k_3 == null || recyclerView_k_4 == null || recyclerView_k_5 == null || recyclerView_k_6 == null || recyclerView_k_7 == null) {
                    recyclerView_k = mainView.findViewById(R.id.kanban_list);
                    recyclerView_k_1 = mainView.findViewById(R.id.kanban_list_2);
                    recyclerView_k_2 = mainView.findViewById(R.id.kanban_list_3);
                    recyclerView_k_3 = mainView.findViewById(R.id.kanban_list_4);
                    recyclerView_k_4 = mainView.findViewById(R.id.kanban_list_5);
                    recyclerView_k_5 = mainView.findViewById(R.id.kanban_list_6);
                    recyclerView_k_6 = mainView.findViewById(R.id.kanban_list_7);
                    recyclerView_k_7 = mainView.findViewById(R.id.kanban_list_8);
                }

                if (typeCard.equals(TypeCardTimeline.BOXES)/* && (kanbanAdapter_1 == null || kanbanAdapter_2 == null || kanbanAdapter_3 == null)*/) {

                    kanbanAdapter_1 = new KanbanTimelineAdapter(listInbox, mainView.getContext(), typeCard, activityApp, this, 1);
                    kanbanAdapter_2 = new KanbanTimelineAdapter(listUnread, mainView.getContext(), typeCard, activityApp, this, 2);
                    kanbanAdapter_3 = new KanbanTimelineAdapter(listfavourite, mainView.getContext(), typeCard, activityApp, this, 3);
                    kanbanAdapter_4 = new KanbanTimelineAdapter(listSent, mainView.getContext(), typeCard, activityApp, this, 4);
                    kanbanAdapter_5 = new KanbanTimelineAdapter(listImportant, mainView.getContext(), typeCard, activityApp, this, 5);
                    kanbanAdapter_6 = new KanbanTimelineAdapter(listDrafts, mainView.getContext(), typeCard, activityApp, this, 6);
                    kanbanAdapter_7 = new KanbanTimelineAdapter(listSpam, mainView.getContext(), typeCard, activityApp, this, 7);
                    kanbanAdapter_8 = new KanbanTimelineAdapter(listTrash, mainView.getContext(), typeCard, activityApp, this, 8);

                    recyclerView_k.setLayoutManager(new LinearLayoutManager(mainView.getContext()));
                    recyclerView_k.setAdapter(kanbanAdapter_1);

                    recyclerView_k_1.setLayoutManager(new LinearLayoutManager(mainView.getContext()));
                    recyclerView_k_1.setAdapter(kanbanAdapter_2);

                    recyclerView_k_2.setLayoutManager(new LinearLayoutManager(mainView.getContext()));
                    recyclerView_k_2.setAdapter(kanbanAdapter_3);

                    recyclerView_k_3.setLayoutManager(new LinearLayoutManager(mainView.getContext()));
                    recyclerView_k_3.setAdapter(kanbanAdapter_4);

                    recyclerView_k_4.setLayoutManager(new LinearLayoutManager(mainView.getContext()));
                    recyclerView_k_4.setAdapter(kanbanAdapter_5);

                    recyclerView_k_5.setLayoutManager(new LinearLayoutManager(mainView.getContext()));
                    recyclerView_k_5.setAdapter(kanbanAdapter_6);

                    recyclerView_k_6.setLayoutManager(new LinearLayoutManager(mainView.getContext()));
                    recyclerView_k_6.setAdapter(kanbanAdapter_7);

                    recyclerView_k_7.setLayoutManager(new LinearLayoutManager(mainView.getContext()));
                    recyclerView_k_7.setAdapter(kanbanAdapter_8);

                    recyclerView_k.setPadding(0, 0, 0, 0);
                    recyclerView_k_1.setPadding(0, 0, 0, 0);
                    recyclerView_k_2.setPadding(0, 0, 0, 0);
                    recyclerView_k_3.setPadding(0, 0, 0, 0);
                    recyclerView_k_4.setPadding(0, 0, 0, 0);
                    recyclerView_k_5.setPadding(0, 0, 0, 0);
                    recyclerView_k_6.setPadding(0, 0, 0, 0);
                    recyclerView_k_7.setPadding(0, 0, 0, 0);

                } else /*if(typeCard.equals(TypeCardTimeline.ROWS) && (kanbanMessageAdapter_1 == null || kanbanMessageAdapter_2 == null || kanbanMessageAdapter_3 == null))*/ {

                    kanbanMessageAdapter_1 = new MessageAdapter(activityApp, listInbox, this, 2);
                    kanbanMessageAdapter_2 = new MessageAdapter(activityApp, listUnread, this, 2);
                    kanbanMessageAdapter_3 = new MessageAdapter(activityApp, listfavourite, this, 2);
                    kanbanMessageAdapter_4 = new MessageAdapter(activityApp, listSent, this, 2);
                    kanbanMessageAdapter_5 = new MessageAdapter(activityApp, listImportant, this, 2);
                    kanbanMessageAdapter_6 = new MessageAdapter(activityApp, listDrafts, this, 2);
                    kanbanMessageAdapter_7 = new MessageAdapter(activityApp, listSpam, this, 2);
                    kanbanMessageAdapter_8 = new MessageAdapter(activityApp, listTrash, this, 2);


                    recyclerView_k.setLayoutManager(new LinearLayoutManager(mainView.getContext()));
                    recyclerView_k.setAdapter(kanbanMessageAdapter_1);

                    recyclerView_k_1.setLayoutManager(new LinearLayoutManager(mainView.getContext()));
                    recyclerView_k_1.setAdapter(kanbanMessageAdapter_2);

                    recyclerView_k_2.setLayoutManager(new LinearLayoutManager(mainView.getContext()));
                    recyclerView_k_2.setAdapter(kanbanMessageAdapter_3);

                    recyclerView_k_3.setLayoutManager(new LinearLayoutManager(mainView.getContext()));
                    recyclerView_k_3.setAdapter(kanbanMessageAdapter_4);

                    recyclerView_k_4.setLayoutManager(new LinearLayoutManager(mainView.getContext()));
                    recyclerView_k_4.setAdapter(kanbanMessageAdapter_5);

                    recyclerView_k_5.setLayoutManager(new LinearLayoutManager(mainView.getContext()));
                    recyclerView_k_5.setAdapter(kanbanMessageAdapter_6);

                    recyclerView_k_6.setLayoutManager(new LinearLayoutManager(mainView.getContext()));
                    recyclerView_k_6.setAdapter(kanbanMessageAdapter_7);

                    recyclerView_k_7.setLayoutManager(new LinearLayoutManager(mainView.getContext()));
                    recyclerView_k_7.setAdapter(kanbanMessageAdapter_8);

                    int px_L = (int) (14 * activityApp.getResources().getDisplayMetrics().density);
                    int px_R = (int) (5 * activityApp.getResources().getDisplayMetrics().density);
                    recyclerView_k.setPadding(px_L, 0, px_R, 0);
                    recyclerView_k_1.setPadding(px_L, 0, px_R, 0);
                    recyclerView_k_2.setPadding(px_L, 0, px_R, 0);
                    recyclerView_k_3.setPadding(px_L, 0, px_R, 0);
                    recyclerView_k_4.setPadding(px_L, 0, px_R, 0);
                    recyclerView_k_5.setPadding(px_L, 0, px_R, 0);
                    recyclerView_k_6.setPadding(px_L, 0, px_R, 0);
                    recyclerView_k_7.setPadding(px_L, 0, px_R, 0);
                }

            } else {

                if (typeCard.equals(TypeCardTimeline.BOXES)) {
                    kanbanAdapter_1.updateList(listInbox);
                    kanbanAdapter_2.updateList(listUnread);
                    kanbanAdapter_3.updateList(listfavourite);
                    kanbanAdapter_4.updateList(listSent);
                    kanbanAdapter_5.updateList(listImportant);
                    kanbanAdapter_6.updateList(listDrafts);
                    kanbanAdapter_7.updateList(listSpam);
                    kanbanAdapter_8.updateList(listTrash);


                } else {
                    kanbanMessageAdapter_1.updateKanban(listInbox);
                    kanbanMessageAdapter_2.updateKanban(listUnread);
                    kanbanMessageAdapter_3.updateKanban(listfavourite);
                    kanbanMessageAdapter_4.updateKanban(listSent);
                    kanbanMessageAdapter_5.updateKanban(listImportant);
                    kanbanMessageAdapter_6.updateKanban(listDrafts);
                    kanbanMessageAdapter_7.updateKanban(listSpam);
                    kanbanMessageAdapter_8.updateKanban(listTrash);

                }

            }


            getActivity().findViewById(R.id.recyclerKankab).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeOtherPopup();

                    if (activityApp.findViewById(R.id.popup_menu_timeline).getVisibility() == View.VISIBLE) {
                        activityApp.findViewById(R.id.popup_menu_timeline).setVisibility(View.GONE);
                        return;
                    }

                    if (activityApp.findViewById(R.id.popup_menu_settings).getVisibility() == View.VISIBLE) {
                        activityApp.findViewById(R.id.popup_menu_settings).setVisibility(View.GONE);
                        return;
                    }

                    if (contactInboxPopup != null && contactInboxPopup.getVisibility() == View.VISIBLE) {
                        contactInboxPopup.setVisibility(View.GONE);
                        return;
                    }

                    if (editPopupTemplate != null && editPopupTemplate.getVisibility() == View.VISIBLE) {
                        editPopupTemplate.setVisibility(View.GONE);
                        return;
                    }

                    if (popupTemplate != null && popupTemplate.getVisibility() == View.VISIBLE) {
                        popupTemplate.setVisibility(View.GONE);
                        return;
                    }

                    if (inboxPopup != null && inboxPopup.getVisibility() == View.VISIBLE) {
                        mainView.findViewById(R.id.folders_Block).setBackgroundColor(0);
                        inboxPopup.setVisibility(View.GONE);
                        return;
                    }

                    if (popupContacts.getVisibility() == View.VISIBLE) {
                        popupContacts.setVisibility(View.GONE);
                        return;
                    }


                    if (popupMessage.getVisibility() == View.VISIBLE) {
                        popupMessage.setVisibility(View.GONE);
                        startSearchContact = false;
                        return;
                    }
                }
            });


        }


        if (typeCard.equals(TypeCardTimeline.EXTRA) || typeCard.equals(TypeCardTimeline.TOP)) {

            if (kanbanRowFragment_Inbox == null || kanbanRowFragment_Unread == null || kanbanRowFragment_Favourite == null || kanbanRowFragment_Sent == null || kanbanRowFragment_Important == null
                    || kanbanRowFragment_Drafts == null || kanbanRowFragment_Spam == null || kanbanRowFragment_Trash == null/* || kanbanRowFragment_Fin == null || kanbanRowFragment_Imp == null
                || kanbanRowFragment_Inv == null || kanbanRowFragment_P == null || kanbanRowFragment_St == null || kanbanRowFragment_Vip == null || kanbanRowFragment_All.kanbanContactsAdapter == null
                || kanbanRowFragment_Crown.kanbanContactsAdapter == null || kanbanRowFragment_Fav.kanbanContactsAdapter == null || kanbanRowFragment_Fin.kanbanContactsAdapter == null ||
                kanbanRowFragment_Imp.kanbanContactsAdapter == null || kanbanRowFragment_Inv.kanbanContactsAdapter == null || kanbanRowFragment_P.kanbanContactsAdapter == null ||
                kanbanRowFragment_St.kanbanContactsAdapter == null || kanbanRowFragment_Vip.kanbanContactsAdapter == null*/) {


                kanbanRowFragment_Inbox = new KanbanRowTimelineFragment();
                Bundle args = new Bundle();
                args.putString("list", "Inbox");
                kanbanRowFragment_Inbox.setArguments(args);

                kanbanRowFragment_Unread = new KanbanRowTimelineFragment();
                Bundle args_F = new Bundle();
                args_F.putString("list", "Unread");
                kanbanRowFragment_Unread.setArguments(args_F);

                kanbanRowFragment_Favourite = new KanbanRowTimelineFragment();
                Bundle args_Imp = new Bundle();
                args_Imp.putString("list", "Favorite");
                kanbanRowFragment_Favourite.setArguments(args_Imp);

                kanbanRowFragment_Sent = new KanbanRowTimelineFragment();
                Bundle args_Sent = new Bundle();
                args_Sent.putString("list", "Sent");
                kanbanRowFragment_Sent.setArguments(args_Sent);

                kanbanRowFragment_Important = new KanbanRowTimelineFragment();
                Bundle args_Impo = new Bundle();
                args_Impo.putString("list", "Important");
                kanbanRowFragment_Important.setArguments(args_Impo);

                kanbanRowFragment_Drafts = new KanbanRowTimelineFragment();
                Bundle args_Dr = new Bundle();
                args_Dr.putString("list", "Drafts");
                kanbanRowFragment_Drafts.setArguments(args_Dr);

                kanbanRowFragment_Spam = new KanbanRowTimelineFragment();
                Bundle args_Sp = new Bundle();
                args_Sp.putString("list", "Spam");
                kanbanRowFragment_Spam.setArguments(args_Sp);

                kanbanRowFragment_Trash = new KanbanRowTimelineFragment();
                Bundle args_Tr = new Bundle();
                args_Tr.putString("list", "Trash");
                kanbanRowFragment_Trash.setArguments(args_Tr);


                ViewPager viewPagerRow = (ViewPager) mainView.findViewById(R.id.viewpager_kanban_t);
                ViewPager viewPagerRow_top = (ViewPager) mainView.findViewById(R.id.viewpager_kanban_t);

                TimelineSectionAdapter adapter, adapter_top;

                adapter = new TimelineSectionAdapter(getChildFragmentManager(), getContext(), TypeCardTimeline.EXTRA);
                adapter_top = new TimelineSectionAdapter(getChildFragmentManager(), getContext(), TypeCardTimeline.TOP);


                adapter.addFragment(kanbanRowFragment_Inbox, "Inbox", listInbox.size());
                adapter.addFragment(kanbanRowFragment_Unread, "Unread", listUnread.size());
                adapter.addFragment(kanbanRowFragment_Favourite, "Favorites", listfavourite.size());
                adapter.addFragment(kanbanRowFragment_Sent, "Sent", listSent.size());
                adapter.addFragment(kanbanRowFragment_Important, "Important", listImportant.size());
                adapter.addFragment(kanbanRowFragment_Drafts, "Drafts", listDrafts.size());
                adapter.addFragment(kanbanRowFragment_Spam, "Spam", listSpam.size());
                adapter.addFragment(kanbanRowFragment_Trash, "Trash", listTrash.size());

                adapter_top.addFragment(kanbanRowFragment_Inbox, "Inbox", listInbox.size());
                adapter_top.addFragment(kanbanRowFragment_Unread, "Unread", listUnread.size());
                adapter_top.addFragment(kanbanRowFragment_Favourite, "Favorites", listfavourite.size());
                adapter_top.addFragment(kanbanRowFragment_Sent, "Sent", listSent.size());
                adapter_top.addFragment(kanbanRowFragment_Important, "Important", listImportant.size());
                adapter_top.addFragment(kanbanRowFragment_Drafts, "Drafts", listDrafts.size());
                adapter_top.addFragment(kanbanRowFragment_Spam, "Spam", listSpam.size());
                adapter_top.addFragment(kanbanRowFragment_Trash, "Trash", listTrash.size());


                viewPagerRow.setAdapter(adapter);
                viewPagerRow_top.setAdapter(adapter_top);

                tabs = (SmartTabLayout) mainView.findViewById(R.id.result_tabs_kanban_t);

                tabs_top = (SmartTabLayout) mainView.findViewById(R.id.result_tabs_kanban_t_top);

                tabs.setCustomTabView(adapter);
                tabs.setViewPager(viewPagerRow);

                tabs_top.setCustomTabView(adapter_top);
                tabs_top.setViewPager(viewPagerRow_top);


                tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int i, float v, int i1) {

                    }

                    @Override
                    public void onPageSelected(int i) {
                        ((TextView) tabs.getTabAt(i).findViewById(R.id.text_title_tab)).setTextColor(getContext().getResources().getColor(R.color.black));
                        ((TextView) tabs.getTabAt(lastPosition).findViewById(R.id.text_title_tab)).setTextColor(getContext().getResources().getColor(R.color.gray_dark));


                        lastPosition = i;
                    }

                    @Override
                    public void onPageScrollStateChanged(int i) {

                    }
                });

                tabs_top.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int i, float v, int i1) {

                    }

                    @Override
                    public void onPageSelected(int i) {
                        ((TextView) tabs_top.getTabAt(i).findViewById(R.id.text_title_tab)).setTextColor(getContext().getResources().getColor(R.color.black));
                        ((TextView) tabs_top.getTabAt(lastPosition_top).findViewById(R.id.text_title_tab)).setTextColor(getContext().getResources().getColor(R.color.gray_dark));


                        lastPosition_top = i;
                    }

                    @Override
                    public void onPageScrollStateChanged(int i) {

                    }
                });


            } else {


                ((TextView) tabs.getTabAt(0).findViewById(R.id.text_count_tab)).setText(String.valueOf(listInbox.size()));
                ((TextView) tabs.getTabAt(1).findViewById(R.id.text_count_tab)).setText(String.valueOf(listUnread.size()));
                ((TextView) tabs.getTabAt(2).findViewById(R.id.text_count_tab)).setText(String.valueOf(listfavourite.size()));
                ((TextView) tabs.getTabAt(3).findViewById(R.id.text_count_tab)).setText(String.valueOf(listSent.size()));
                ((TextView) tabs.getTabAt(4).findViewById(R.id.text_count_tab)).setText(String.valueOf(listImportant.size()));
                ((TextView) tabs.getTabAt(5).findViewById(R.id.text_count_tab)).setText(String.valueOf(listDrafts.size()));
                ((TextView) tabs.getTabAt(6).findViewById(R.id.text_count_tab)).setText(String.valueOf(listSpam.size()));
                ((TextView) tabs.getTabAt(7).findViewById(R.id.text_count_tab)).setText(String.valueOf(listTrash.size()));

                ((TextView) tabs_top.getTabAt(0).findViewById(R.id.text_count_tab)).setText(String.valueOf(listInbox.size()));
                ((TextView) tabs_top.getTabAt(1).findViewById(R.id.text_count_tab)).setText(String.valueOf(listUnread.size()));
                ((TextView) tabs_top.getTabAt(2).findViewById(R.id.text_count_tab)).setText(String.valueOf(listfavourite.size()));
                ((TextView) tabs_top.getTabAt(3).findViewById(R.id.text_count_tab)).setText(String.valueOf(listSent.size()));
                ((TextView) tabs_top.getTabAt(4).findViewById(R.id.text_count_tab)).setText(String.valueOf(listImportant.size()));
                ((TextView) tabs_top.getTabAt(5).findViewById(R.id.text_count_tab)).setText(String.valueOf(listDrafts.size()));
                ((TextView) tabs_top.getTabAt(6).findViewById(R.id.text_count_tab)).setText(String.valueOf(listSpam.size()));
                ((TextView) tabs_top.getTabAt(7).findViewById(R.id.text_count_tab)).setText(String.valueOf(listTrash.size()));

                if (kanbanRowFragment_Inbox.messageAdapter == null) {
                    Bundle args = new Bundle();
                    args.putString("list", "Inbox");
                    kanbanRowFragment_Inbox.setArguments(args);
                } else {
                    kanbanRowFragment_Inbox.updateList(listInbox);
                }

                if (kanbanRowFragment_Unread.messageAdapter == null) {
                    Bundle args_F = new Bundle();
                    args_F.putString("list", "Unread");
                    kanbanRowFragment_Unread.setArguments(args_F);
                } else {
                    kanbanRowFragment_Unread.updateList(listUnread);
                }

                if (kanbanRowFragment_Favourite.messageAdapter == null) {
                    Bundle args_Imp = new Bundle();
                    args_Imp.putString("list", "Favorite");
                    kanbanRowFragment_Favourite.setArguments(args_Imp);
                } else {
                    kanbanRowFragment_Favourite.updateList(listfavourite);
                }

                if (kanbanRowFragment_Sent.messageAdapter == null) {
                    Bundle args_Imp = new Bundle();
                    args_Imp.putString("list", "Sent");
                    kanbanRowFragment_Sent.setArguments(args_Imp);
                } else {
                    kanbanRowFragment_Sent.updateList(listSent);
                }

                if (kanbanRowFragment_Important.messageAdapter == null) {
                    Bundle args_Imp = new Bundle();
                    args_Imp.putString("list", "Important");
                    kanbanRowFragment_Important.setArguments(args_Imp);
                } else {
                    kanbanRowFragment_Important.updateList(listImportant);
                }

                if (kanbanRowFragment_Drafts.messageAdapter == null) {
                    Bundle args_Imp = new Bundle();
                    args_Imp.putString("list", "Drafts");
                    kanbanRowFragment_Drafts.setArguments(args_Imp);
                } else {
                    kanbanRowFragment_Drafts.updateList(listDrafts);
                }

                if (kanbanRowFragment_Spam.messageAdapter == null) {
                    Bundle args_Imp = new Bundle();
                    args_Imp.putString("list", "Spam");
                    kanbanRowFragment_Spam.setArguments(args_Imp);
                } else {
                    kanbanRowFragment_Spam.updateList(listSpam);
                }

                if (kanbanRowFragment_Trash.messageAdapter == null) {
                    Bundle args_Imp = new Bundle();
                    args_Imp.putString("list", "Trash");
                    kanbanRowFragment_Trash.setArguments(args_Imp);
                } else {
                    kanbanRowFragment_Trash.updateList(listTrash);
                }
            }
        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        saveMessages();
    }

    public void updateListAdapterMore() {
        try {
            activityApp.runOnUiThread(new Runnable() {
                @Override
                public void run() {


                    if (messageAdapter == null) {
                        messageAdapter = new MessageAdapter(activityApp, listMessages, TimeLineFragment.this, 1);
                        recyclerAccount.setAdapter(messageAdapter);
                    } else {
                        recyclerAccount.post(new Runnable() {
                            @Override
                            public void run() {
                                synchronized (listMessages) {

                                    /*LinearLayoutManager myLayoutManager = (LinearLayoutManager) recyclerAccount.getLayoutManager();
                                    int scrollPosition = myLayoutManager.findFirstVisibleItemPosition();*/
                                    messageAdapter.updateListMore(listMessages);





                                    /*if (scrollPosition == 0)
                                        recyclerAccount.scrollToPosition(0);
                                    else
                                        messageAdapter.notifyDataSetChanged();*/

                                }
                            }
                        });

                    }


                    updateProgressMore();


                }
            });


        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void updateListAdapterNew(int count) {
        try {
            activityApp.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    //Collections.sort(listMessages, (messFirst, messSecond) -> messSecond.getDate().compareTo(messFirst.getDate()));

                    //listMessages.add(new EmailMessage(null, null, value, null, selectedContact.get(0).getDateCreate()));

                    if (messageAdapter == null) {
                        messageAdapter = new MessageAdapter(activityApp, listMessages, TimeLineFragment.this, 1);
                        recyclerAccount.setAdapter(messageAdapter);
                    } else {
                        recyclerAccount.post(new Runnable() {
                            @Override
                            public void run() {
                                synchronized (listMessages) {

                                    LinearLayoutManager myLayoutManager = (LinearLayoutManager) recyclerAccount.getLayoutManager();
                                    int scrollPosition = myLayoutManager.findFirstVisibleItemPosition();
                                    messageAdapter.updateListNew(listMessages, count);


                                    if (scrollPosition == 0)
                                        recyclerAccount.scrollToPosition(0);
                                    else
                                        messageAdapter.notifyDataSetChanged();

                                }
                            }
                        });

                    }


                }
            });


        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }


    public void updateListAdapter() {
        try {
            activityApp.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    //Collections.sort(listMessages, (messFirst, messSecond) -> messSecond.getDate().compareTo(messFirst.getDate()));

                    //listMessages.add(new EmailMessage(null, null, value, null, selectedContact.get(0).getDateCreate()));

                    if (messageAdapter == null) {
                        messageAdapter = new MessageAdapter(activityApp, listMessages, TimeLineFragment.this, 1);
                        recyclerAccount.setAdapter(messageAdapter);
                    } else {
                        recyclerAccount.post(new Runnable() {
                            @Override
                            public void run() {
                                synchronized (listMessages) {
                                    messageAdapter.updateList(listMessages);
                                }
                            }
                        });

                    }


                    if (listMessages.size() == 0) {
                        //mainView.findViewById(R.id.joinContact).setVisibility(View.VISIBLE);
                        //mainView.findViewById(R.id.frameChooseAccounts).setVisibility(View.GONE);
                    } else {
                        recyclerAccount.setVisibility(View.VISIBLE);
                        //mainView.findViewById(R.id.frameChooseAccounts).setVisibility(View.VISIBLE);
                        //mainView.findViewById(R.id.joinContact).setVisibility(View.GONE);
                    }
                    mainView.findViewById(R.id.frameGetMessage).setVisibility(View.GONE);

                }
            });


        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void updateProgress() {
        double c1 = 100.0 / countMessages;
        double c2 = c1 * countLoad;

        try {
            activityApp.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (c2 < 100) {
                        mainView.findViewById(R.id.progressBarTimeline).setVisibility(View.VISIBLE);
                        mainView.findViewById(R.id.loadBarTimeline).setVisibility(View.VISIBLE);

                        ((TextView) mainView.findViewById(R.id.quantity_timeline_load)).setText(String.valueOf((int) c2) + "%");
                        ((ProgressBar) mainView.findViewById(R.id.progressBarTimeline)).setProgress((int) c2);

                    } else {
                        countMessages = 0;
                        countLoad = 0;
                        mainView.findViewById(R.id.progressBarTimeline).setVisibility(View.GONE);
                        mainView.findViewById(R.id.loadBarTimeline).setVisibility(View.GONE);
                        saveMessages();
                    }


                }
            });
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    public void updateProgressMore() {
        double c1 = 100.0 / 30;
        double c2 = c1 * countLoad;

        try {
            activityApp.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (c2 < 100 && c2 != 0.0) {
                        mainView.findViewById(R.id.progressBarTimeline).setVisibility(View.VISIBLE);
                        mainView.findViewById(R.id.loadBarTimeline).setVisibility(View.VISIBLE);

                        ((TextView) mainView.findViewById(R.id.quantity_timeline_load)).setText(String.valueOf((int) c2) + "%");
                        ((ProgressBar) mainView.findViewById(R.id.progressBarTimeline)).setProgress((int) c2);

                    } else {
                        countMessages = 0;
                        countLoad = 0;
                        mainView.findViewById(R.id.progressBarTimeline).setVisibility(View.GONE);
                        mainView.findViewById(R.id.loadBarTimeline).setVisibility(View.GONE);
                       /* if(c2 != 0.0)
                            saveMessages();*/
                    }


                }
            });
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }


    public void checkGmail() {


        if (mainView.findViewById(R.id.frameGetMessage).getVisibility() == View.GONE) {
            SharedPreferences mSettings;
            mSettings = activityApp.getSharedPreferences("accountUser", Context.MODE_PRIVATE);
            Set<String> set = mSettings.getStringSet("accounts", null);

            if (set != null && !set.isEmpty()) {


                if (activityApp != null) {
                    if (!MainActivity.hasConnection(activityApp)) {
                        Toast.makeText(activityApp, "Check your internet connection", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else {
                    try {
                        if (!MainActivity.hasConnection(((ProfileFragment) getParentFragment()).activityApp)) {
                            Toast.makeText(activityApp, "Check your internet connection", Toast.LENGTH_SHORT).show();
                            return;
                        }

                    } catch (Exception e) {
                        ((CreateFragment) getParentFragment()).closeOtherPopup();
                    }
                }


                popupAccounts.setVisibility(View.GONE);

                mainView.findViewById(R.id.frame_google_count).setVisibility(View.VISIBLE);
                ((TextView) mainView.findViewById(R.id.text_count_google_account)).setText(String.valueOf(set.size()));


                /*if (set.size() == 1)
                    Toast.makeText(getContext(), "1 gmail account chosen", Toast.LENGTH_SHORT).show();
                else if (set.size() > 1)
                    Toast.makeText(getContext(), accountAdapter.getListSelectId().size() + " gmail accounts chosen", Toast.LENGTH_SHORT).show();*/

                FloatingActionMenu floatingActionMenu = (FloatingActionMenu) activityApp.findViewById(R.id.fabMenuContainer);
                floatingActionMenu.setVisibility(View.GONE);

                mainView.findViewById(R.id.frameGetMessage).setVisibility(View.VISIBLE);


                listCredential = new ArrayList<>();

                Account[] accounts = AccountManager.get(activityApp).getAccountsByType("com.google");

                for (Account account : accounts) {

                    if (set.contains(account.name)) {
                        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(
                                activityApp.getApplicationContext(), Arrays.asList(SCOPES))
                                .setBackOff(new ExponentialBackOff());


                        credential.setSelectedAccountName(account.name);

                        listCredential.add(credential);
                    }

                }


                SharedPreferences mPref = activityApp.getSharedPreferences("backupMessages", Context.MODE_PRIVATE);
                String sort = mPref.getString("messages", null);

              /*  String path = activityApp.getFilesDir() + "/messages";
                File folder = new File(path);*/
                if (sort == null) {
                    //folder.mkdir();
                    getMessages();
                } else {
                    getMessagesFromFile();
                }


                //mainView.findViewById(R.id.joinContact).setVisibility(View.GONE);

                //int px = (int) (54 * getContext().getResources().getDisplayMetrics().density);


            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (!ContactAdapter.checkMerge)
            menu.getItem(0).setVisible(true);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        ((MainActivity) mainView.getContext()).hideViewFlipper();
        ((MainActivity) mainView.getContext()).startDrawer();

        ((TextView) toolbarC.findViewById(R.id.toolbar_title)).setText("Timeline");

        if (!ContactAdapter.checkMerge)
            ((TextView) toolbarC.findViewById(R.id.cancel_toolbar)).setVisibility(View.GONE);

        toolbarC.findViewById(R.id.toolbar_kanban_clicker).setVisibility(View.VISIBLE);

        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void closeOtherPopup() {
        if (openedViews != null) {
            for (View view : openedViews) {
                view.setVisibility(View.GONE);
            }
            openedViews.clear();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);


        if (requestCode == 1) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(resultData);
            handleSignInResult(task);


        }
        if (requestCode == REQUEST_AUTHORIZATION) {

            //String accountName = data.getExtras().getString(AccountManager.KEY_ACCOUNT_NAME);


            //mCredential.setSelectedAccount(new Account(accountName, "com.extime"));

            //mCredential.setSelectedAccountName(accountName);


            if (resultCode == RESULT_OK) {
                getMessages();
            } else {
                /*mainView.findViewById(R.id.frameChooseAccounts).setVisibility(View.GONE);
                mainView.findViewById(R.id.frameGoGmail).setVisibility(View.VISIBLE);
                mainView.findViewById(R.id.frameGetMessage).setVisibility(View.GONE);*/
                return;
            }
        }


        if (requestCode == 42 && resultCode == Activity.RESULT_OK) {
            Uri[] uriPaths;

            if (resultData == null || (resultData.getData() == null && resultData.getClipData() == null)) {
                //Toast.makeText(getContext(), R.string.invalid_source, Toast.LENGTH_SHORT).show();
                return;
            }

            if (resultData.getData() != null) {
                uriPaths = new Uri[1];
                uriPaths[0] = resultData.getData();


                activityApp.getContentResolver().takePersistableUriPermission(uriPaths[0], Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);


                File f = new File(uriPaths[0].toString());

                System.out.println("FILE = " + f.getAbsolutePath());

                //dumpImageMetaData(uriPaths[0]);

                FilesTemplate ft = new FilesTemplate(uriPaths[0]);

                FileType.getNameSize(ft, activityApp);

                selectTemplate.addFile(ft);


                if (popupTemplate.getVisibility() == View.VISIBLE)
                    initRecyclerPreview(selectTemplate);
                else
                    initRecyclerPreviewEdit(selectTemplate);


            } else if (resultData.getClipData() != null) {
                int selectedCount = resultData.getClipData().getItemCount();
                uriPaths = new Uri[selectedCount];

                Template template = new Template();

                for (int i = 0; i < selectedCount; i++) {
                    uriPaths[i] = resultData.getClipData().getItemAt(i).getUri();

                    activityApp.getContentResolver().takePersistableUriPermission(uriPaths[i], Intent.FLAG_GRANT_READ_URI_PERMISSION
                            | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);


                    FilesTemplate ft = new FilesTemplate(uriPaths[i]);

                    FileType.getNameSize(ft, activityApp);

                    selectTemplate.addFile(ft);

                   /* if (selectTemplate.getEmailDataTemplate() == null && selectTemplate.isTemplateUser())

                    else {
                        template.addFile(ft);
                    }*/


                    //template.addFile(uriPaths[i]);
                }


                if (popupTemplate.getVisibility() == View.VISIBLE)
                    initRecyclerPreview(selectTemplate);
                else
                    initRecyclerPreviewEdit(selectTemplate);

            }
        }
    }

    private void getPlainTextFromMessageParts(List<MessagePart> messageParts, StringBuilder
            stringBuilder) {
        if (messageParts != null && messageParts.get(0) != null) {
            if (messageParts.get(0).getMimeType().equals("text/plain")) {

                stringBuilder.append(messageParts.get(0).getBody().getData());
                if (messageParts.get(0).getParts() != null) {
                    getPlainTextFromMessageParts(messageParts.get(0).getParts(), stringBuilder);
                }

            } else if (messageParts.get(0).getMimeType().equals("text/html")) {
                stringBuilder.append(messageParts.get(0).getBody().getData());
                if (messageParts.get(0).getParts() != null) {
                    getPlainTextFromMessageParts(messageParts.get(0).getParts(), stringBuilder);
                }
            }

            /*for (MessagePart messagePart : messageParts) {
                System.out.println("MIMETYPE = "+messagePart.getMimeType());
                if (messagePart.getMimeType().equals("text/plain") || messagePart.getMimeType().equals("text/html")) {

                }

                if (messagePart.getParts() != null) {
                    getPlainTextFromMessageParts(messagePart.getParts(), stringBuilder);
                }
            }*/
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);


            mCredential.setSelectedAccountName(account.getAccount().name);


            //GoogleAccountCredential cc = GoogleAuthProvider.get

            // Signed in successfully, show authenticated UI.
            //updateUI(account);


        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.

            //updateUI(null);

        }
    }

    private boolean startSearchContact = true;

    private String getInitials(String name) {
        String initials = "";
        if (name != null && !name.isEmpty()) {
            String names[] = name.split("\\s+");
            for (String namePart : names) {
                if (namePart != null && namePart.length() > 0)
                    initials += namePart.charAt(0);
            }
        }
        return initials.toUpperCase();
    }

    public Time getRandomDate() {
        final Random random = new Random();
        final int millisInDay = 24 * 60 * 60 * 1000;
        return new Time((long) random.nextInt(millisInDay));
    }

    @Override
    public void clickMessage(EmailMessage message) {

        if (activityApp.findViewById(R.id.popup_menu_timeline).getVisibility() == View.VISIBLE) {
            activityApp.findViewById(R.id.popup_menu_timeline).setVisibility(View.GONE);
            return;
        }

        if (activityApp.findViewById(R.id.popup_menu_settings).getVisibility() == View.VISIBLE) {
            activityApp.findViewById(R.id.popup_menu_settings).setVisibility(View.GONE);
            return;
        }

        if (contactInboxPopup != null && contactInboxPopup.getVisibility() == View.VISIBLE) {
            contactInboxPopup.setVisibility(View.GONE);
            return;
        }

        if (editPopupTemplate != null && editPopupTemplate.getVisibility() == View.VISIBLE) {
            editPopupTemplate.setVisibility(View.GONE);
            return;
        }

        if (popupTemplate != null && popupTemplate.getVisibility() == View.VISIBLE) {
            popupTemplate.setVisibility(View.GONE);
            return;
        }

        if (inboxPopup != null && inboxPopup.getVisibility() == View.VISIBLE) {
            mainView.findViewById(R.id.folders_Block).setBackgroundColor(0);
            inboxPopup.setVisibility(View.GONE);
            return;
        }

        if (popupContacts.getVisibility() == View.VISIBLE) {
            popupContacts.setVisibility(View.GONE);
            return;
        }


        if (popupMessage.getVisibility() == View.VISIBLE) {
            popupMessage.setVisibility(View.GONE);
            startSearchContact = false;
            return;
        }

        popupMessage.findViewById(R.id.popup_menu_message).setVisibility(View.GONE);


        popupMessage.findViewById(R.id.replyMessPopup).setVisibility(View.GONE);

        if (message.getListOfTypeMessage().contains("STARRED")) {
            popupMessage.findViewById(R.id.favoriteMessagePreview).setVisibility(View.VISIBLE);
            popupMessage.findViewById(R.id.favoriteMessagePreviewEmpty).setVisibility(View.GONE);
        } else {
            popupMessage.findViewById(R.id.favoriteMessagePreview).setVisibility(View.GONE);
            popupMessage.findViewById(R.id.favoriteMessagePreviewEmpty).setVisibility(View.VISIBLE);
        }

        if (message.getListOfTypeMessage().contains("DRAFT")) {
            popupMessage.findViewById(R.id.draft_message_text).setVisibility(View.VISIBLE);
        } else {
            popupMessage.findViewById(R.id.draft_message_text).setVisibility(View.GONE);
        }

        popupMessage.findViewById(R.id.favoriteMessagePreview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setStarMessage(message, false);
                popupMessage.findViewById(R.id.favoriteMessagePreview).setVisibility(View.GONE);
                popupMessage.findViewById(R.id.favoriteMessagePreviewEmpty).setVisibility(View.VISIBLE);
                Toast.makeText(mainView.getContext(), "Deleted from Starred", Toast.LENGTH_SHORT).show();
            }
        });

        popupMessage.findViewById(R.id.favoriteMessagePreviewEmpty).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setStarMessage(message, true);
                popupMessage.findViewById(R.id.favoriteMessagePreview).setVisibility(View.VISIBLE);
                popupMessage.findViewById(R.id.favoriteMessagePreviewEmpty).setVisibility(View.GONE);
                Toast.makeText(mainView.getContext(), "Successfully added to Starred", Toast.LENGTH_SHORT).show();
            }
        });


        if (message.getTitle().length() > 4) {

            if (message.getTitle().substring(0, 3).contains("Re:") || message.getTitle().substring(0, 3).contains("Re[")) {
                ((ImageView) popupMessage.findViewById(R.id.replyMessPopup)).setImageDrawable(mainView.getContext().getResources().getDrawable(R.drawable.reply_message));
                popupMessage.findViewById(R.id.replyMessPopup).setVisibility(View.VISIBLE);
            } else if (message.getTitle().substring(0, 4).contains("Fwd:") || message.getTitle().substring(0, 4).contains("Fwd[")) {
                ((ImageView) popupMessage.findViewById(R.id.replyMessPopup)).setImageDrawable(mainView.getContext().getResources().getDrawable(R.drawable.foward_message));
                popupMessage.findViewById(R.id.replyMessPopup).setVisibility(View.VISIBLE);
            }
        }

        popupMessage.findViewById(R.id.menuMessage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                popupMessage.findViewById(R.id.popup_menu_message).findViewById(R.id.menu_message_reply).setVisibility(View.VISIBLE);
                popupMessage.findViewById(R.id.popup_menu_message).findViewById(R.id.menu_message_replyAll).setVisibility(View.VISIBLE);
                popupMessage.findViewById(R.id.popup_menu_message).findViewById(R.id.menu_message_forward).setVisibility(View.VISIBLE);
                popupMessage.findViewById(R.id.popup_menu_message).findViewById(R.id.menu_message_mark).setVisibility(View.VISIBLE);
                popupMessage.findViewById(R.id.popup_menu_message).findViewById(R.id.menu_message_mardUnread).setVisibility(View.VISIBLE);
                popupMessage.findViewById(R.id.popup_menu_message).findViewById(R.id.menu_message_saveAsTemplate).setVisibility(View.VISIBLE);
                popupMessage.findViewById(R.id.popup_menu_message).findViewById(R.id.menu_message_more).setVisibility(View.VISIBLE);

                popupMessage.findViewById(R.id.popup_menu_message).findViewById(R.id.menu_message_less).setVisibility(View.GONE);
                popupMessage.findViewById(R.id.popup_menu_message).findViewById(R.id.menu_message_share).setVisibility(View.GONE);
                popupMessage.findViewById(R.id.popup_menu_message).findViewById(R.id.menu_message_detector).setVisibility(View.GONE);
                popupMessage.findViewById(R.id.popup_menu_message).findViewById(R.id.menu_message_massSend).setVisibility(View.GONE);
                popupMessage.findViewById(R.id.popup_menu_message).findViewById(R.id.menu_message_saveAs).setVisibility(View.GONE);
                popupMessage.findViewById(R.id.popup_menu_message).findViewById(R.id.menu_message_delete).setVisibility(View.GONE);
                popupMessage.findViewById(R.id.popup_menu_message).findViewById(R.id.menu_message_remind).setVisibility(View.GONE);

                popupMessage.findViewById(R.id.popup_menu_message).findViewById(R.id.menu_message_less).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupMessage.findViewById(R.id.popup_menu_message).findViewById(R.id.menu_message_reply).setVisibility(View.VISIBLE);
                        popupMessage.findViewById(R.id.popup_menu_message).findViewById(R.id.menu_message_replyAll).setVisibility(View.VISIBLE);
                        popupMessage.findViewById(R.id.popup_menu_message).findViewById(R.id.menu_message_forward).setVisibility(View.VISIBLE);
                        popupMessage.findViewById(R.id.popup_menu_message).findViewById(R.id.menu_message_mark).setVisibility(View.VISIBLE);
                        popupMessage.findViewById(R.id.popup_menu_message).findViewById(R.id.menu_message_mardUnread).setVisibility(View.VISIBLE);
                        popupMessage.findViewById(R.id.popup_menu_message).findViewById(R.id.menu_message_saveAsTemplate).setVisibility(View.VISIBLE);
                        popupMessage.findViewById(R.id.popup_menu_message).findViewById(R.id.menu_message_more).setVisibility(View.VISIBLE);

                        popupMessage.findViewById(R.id.popup_menu_message).findViewById(R.id.menu_message_less).setVisibility(View.GONE);
                        popupMessage.findViewById(R.id.popup_menu_message).findViewById(R.id.menu_message_share).setVisibility(View.GONE);
                        popupMessage.findViewById(R.id.popup_menu_message).findViewById(R.id.menu_message_detector).setVisibility(View.GONE);
                        popupMessage.findViewById(R.id.popup_menu_message).findViewById(R.id.menu_message_massSend).setVisibility(View.GONE);
                        popupMessage.findViewById(R.id.popup_menu_message).findViewById(R.id.menu_message_saveAs).setVisibility(View.GONE);
                        popupMessage.findViewById(R.id.popup_menu_message).findViewById(R.id.menu_message_delete).setVisibility(View.GONE);
                        popupMessage.findViewById(R.id.popup_menu_message).findViewById(R.id.menu_message_remind).setVisibility(View.GONE);
                    }
                });

                popupMessage.findViewById(R.id.popup_menu_message).findViewById(R.id.menu_message_more).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupMessage.findViewById(R.id.popup_menu_message).findViewById(R.id.menu_message_reply).setVisibility(View.GONE);
                        popupMessage.findViewById(R.id.popup_menu_message).findViewById(R.id.menu_message_replyAll).setVisibility(View.GONE);
                        popupMessage.findViewById(R.id.popup_menu_message).findViewById(R.id.menu_message_forward).setVisibility(View.GONE);
                        popupMessage.findViewById(R.id.popup_menu_message).findViewById(R.id.menu_message_mark).setVisibility(View.GONE);
                        popupMessage.findViewById(R.id.popup_menu_message).findViewById(R.id.menu_message_mardUnread).setVisibility(View.GONE);
                        popupMessage.findViewById(R.id.popup_menu_message).findViewById(R.id.menu_message_saveAsTemplate).setVisibility(View.GONE);
                        popupMessage.findViewById(R.id.popup_menu_message).findViewById(R.id.menu_message_more).setVisibility(View.GONE);

                        popupMessage.findViewById(R.id.popup_menu_message).findViewById(R.id.menu_message_less).setVisibility(View.VISIBLE);
                        popupMessage.findViewById(R.id.popup_menu_message).findViewById(R.id.menu_message_share).setVisibility(View.VISIBLE);
                        popupMessage.findViewById(R.id.popup_menu_message).findViewById(R.id.menu_message_detector).setVisibility(View.VISIBLE);
                        popupMessage.findViewById(R.id.popup_menu_message).findViewById(R.id.menu_message_massSend).setVisibility(View.VISIBLE);
                        popupMessage.findViewById(R.id.popup_menu_message).findViewById(R.id.menu_message_saveAs).setVisibility(View.VISIBLE);
                        popupMessage.findViewById(R.id.popup_menu_message).findViewById(R.id.menu_message_delete).setVisibility(View.VISIBLE);
                        popupMessage.findViewById(R.id.popup_menu_message).findViewById(R.id.menu_message_remind).setVisibility(View.VISIBLE);
                    }
                });


                popupMessage.findViewById(R.id.menu_message_reply).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupMessage.findViewById(R.id.replyMessage).callOnClick();
                    }
                });

                popupMessage.findViewById(R.id.menu_message_detector).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupMessage.findViewById(R.id.extratorMessage).callOnClick();
                    }
                });

                popupMessage.findViewById(R.id.popup_menu_message).setVisibility(View.VISIBLE);
            }
        });

        popupMessage.findViewById(R.id.popup_mess).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupMessage.findViewById(R.id.popup_menu_message).setVisibility(View.GONE);
            }
        });

            /*popupMessage.findViewById(R.id.replyMessage).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });*/

        startSearchContact = true;

        setReadMessage(message);

        //((ImageView) popupMessage.findViewById(R.id.google_recipient)).setVisibility(View.GONE);

        ((ImageView) popupMessage.findViewById(R.id.google_sender)).setVisibility(View.GONE);

        /*popupMessage.findViewById(R.id.replyMessage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupMessage.findViewById(R.id.popup_menu_message).setVisibility(View.VISIBLE);
            }
        });*/

        popupMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupMessage.findViewById(R.id.popup_menu_message).getVisibility() == View.VISIBLE)
                    popupMessage.findViewById(R.id.popup_menu_message).setVisibility(View.GONE);
            }
        });


        popupMessage.findViewById(R.id.timeMessage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openContactList(message);
            }
        });

        popupMessage.findViewById(R.id.moreCont).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openContactList(message);
            }
        });

        popupMessage.findViewById(R.id.secondC).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openContactList(message);
            }
        });

        popupMessage.findViewById(R.id.firstC).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openContactList(message);
            }
        });

        popupMessage.findViewById(R.id.clickF).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openContactList(message);
            }
        });

        boolean checkSelect = false;

        String sendTo = null;
        String sendToName = null;
        String replyFrom = null;


        if (email_contact.contains(message.getAccount().getEmail())/*message.getAccount().getEmail().equals(email_contact)*/) {

            checkSelect = true;

            int nameHash = message.getAccount().getName().hashCode();
            int color = Color.rgb(Math.abs(nameHash * 28439) % 255, Math.abs(nameHash * 211239) % 255, Math.abs(nameHash * 42368) % 255);
            GradientDrawable circle = (GradientDrawable) ContextCompat.getDrawable(activityApp, R.drawable.blue_circle).mutate();
            circle.setColor(color);
            ((CircleImageView) popupMessage.findViewById(R.id.avatar_sender)).setBackground(circle);
            String initials = getInitials(message.getAccount().getName());
            ((TextView) popupMessage.findViewById(R.id.initial_sender)).setText(initials);
            ((TextView) popupMessage.findViewById(R.id.header_message)).setText(message.getAccount().getName() + " < " + message.getAccount().getEmail() + " >");


            SharedPreferences mSettings;
            mSettings = getActivity().getSharedPreferences("accountUser", Context.MODE_PRIVATE);
            Set<String> set = mSettings.getStringSet("accounts", null);


            //list.add(account);
            if (set != null && set.contains(message.getAccount().getEmail()) || set != null && set.contains(message.getAccount().getName())) {
                if (set.contains(message.getAccount().getEmail()))
                    replyFrom = message.getAccount().getEmail();
                else replyFrom = message.getAccount().getName();
            } else {
                sendTo = message.getAccount().getEmail();
                sendToName = message.getAccount().getName();
            }




                /*int nameHash2 = message.getEmail_2().hashCode();
                int color2 = Color.rgb(Math.abs(nameHash2 * 28439) % 255, Math.abs(nameHash2 * 211239) % 255, Math.abs(nameHash2 * 42368) % 255);
                GradientDrawable circle2 = (GradientDrawable) ContextCompat.getDrawable(activityApp, R.drawable.blue_circle).mutate();
                circle2.setColor(color2);
                //holder.avatar.setImageResource(android.R.color.transparent);
                ((CircleImageView) popupMessage.findViewById(R.id.avatar_recipient)).setBackground(circle2);
                String initials2 = getInitials(message.getEmail_2());
                ((TextView) popupMessage.findViewById(R.id.initials_recipient)).setText(initials2);

                ((ImageView) popupMessage.findViewById(R.id.google_recipient)).setVisibility(View.VISIBLE);*/

        } else {

            int nameHash = message.getAccount().getName().hashCode();
            int color = Color.rgb(Math.abs(nameHash * 28439) % 255, Math.abs(nameHash * 211239) % 255, Math.abs(nameHash * 42368) % 255);
            GradientDrawable circle = (GradientDrawable) ContextCompat.getDrawable(activityApp, R.drawable.blue_circle).mutate();
            circle.setColor(color);
            ((CircleImageView) popupMessage.findViewById(R.id.avatar_sender)).setBackground(circle);
            String initials = getInitials(message.getEmail_2());
            ((TextView) popupMessage.findViewById(R.id.initial_sender)).setText(initials);

            ((TextView) popupMessage.findViewById(R.id.header_message)).setText("< " + message.getAccount().getName() + " >");

                /*int hash = message.getAccount().getName().hashCode();
                int color2 = Color.rgb(Math.abs(hash * 28439) % 255, Math.abs(hash * 211239) % 255, Math.abs(hash * 42368) % 255);
                GradientDrawable circle2 = (GradientDrawable) ContextCompat.getDrawable(activityApp, R.drawable.blue_circle).mutate();
                circle2.setColor(color2);
                ((CircleImageView) popupMessage.findViewById(R.id.avatar_recipient)).setBackground(circle2);
                String initials2 = getInitials(message.getAccount().getName());
                ((TextView) popupMessage.findViewById(R.id.initials_recipient)).setText(initials2);*/

            ((ImageView) popupMessage.findViewById(R.id.google_sender)).setVisibility(View.VISIBLE);

        }


        //================================


        String data = null;

        ArrayList<ContactOfMessage> listOfContactsMessage = new ArrayList<>();

        ContactOfMessage sender = new ContactOfMessage();

        for (int i2 = 0; i2 < message.getListHeaders().size(); i2++) {
            System.out.println(i2 + ". " + message.getListHeaders().get(i2).getValue() + ", type = " + message.getListHeaders().get(i2).getName());

            if (message.getListHeaders().get(i2).getName().equalsIgnoreCase("From") && !checkSelect) {
                String send = message.getListHeaders().get(i2).getValue();
                String email = ClipboardType.getEmail(send);

                if (email != null) {


                    send = send.replace(email, "");
                    send = send.replace("<", "");
                    send = send.replace(">", "");
                    send = send.replaceAll("\"", "");

                    send = send.trim();

                    if (send.length() > 0) sender.setName(send);
                }
            }

            if (message.getListHeaders().get(i2).getName().equalsIgnoreCase("To")) {
                data = message.getListHeaders().get(i2).getValue();
                break;
            }
        }

        if (data != null) {
            String[] mas = data.split(",");
            if (mas.length > 0) {
                for (int i = 0; i < mas.length; i++) {

                    String email = ClipboardType.getEmail(mas[i]);
                    ContactOfMessage contact = new ContactOfMessage();
                    if (email != null)
                        contact.setEmail(email.trim());

                    mas[i] = mas[i].replace(email, "");
                    mas[i] = mas[i].replace("<", "");
                    mas[i] = mas[i].replace(">", "");
                    mas[i] = mas[i].replaceAll("\"", "");

                    mas[i] = mas[i].trim();
                    if (mas[i].length() > 0) contact.setName(mas[i]);


                    SharedPreferences mSettings;
                    mSettings = getActivity().getSharedPreferences("accountUser", Context.MODE_PRIVATE);
                    Set<String> set = mSettings.getStringSet("accounts", null);

                    if (replyFrom != null && sendTo == null) {
                        sendTo = email.trim();
                        sendToName = mas[i];
                    }

                    if (set != null && !set.isEmpty()) {

                        if (set.contains(email)) {
                            contact.setGoogle(true);
                            replyFrom = contact.getEmail();
                        }

                    }
                    listOfContactsMessage.add(contact);
                }
            }
        }

        if (listOfContactsMessage.size() == 0) {
            popupMessage.findViewById(R.id.moreCont).setVisibility(View.GONE);
            popupMessage.findViewById(R.id.secondC).setVisibility(View.GONE);
            popupMessage.findViewById(R.id.firstC).setVisibility(View.GONE);
        }

        if (listOfContactsMessage.size() > 2) {
            ((TextView) popupMessage.findViewById(R.id.contactC)).setText(String.valueOf(listOfContactsMessage.size() - 2));
            popupMessage.findViewById(R.id.moreCont).setVisibility(View.VISIBLE);
        }

        if (listOfContactsMessage.size() == 1) {
            popupMessage.findViewById(R.id.moreCont).setVisibility(View.GONE);
            popupMessage.findViewById(R.id.secondC).setVisibility(View.GONE);
            popupMessage.findViewById(R.id.firstC).setVisibility(View.VISIBLE);

            int hash = 0;

            if (listOfContactsMessage.get(0).getName() != null)
                hash = listOfContactsMessage.get(0).getName().hashCode();
            else if (listOfContactsMessage.get(0).getEmail() != null)
                hash = listOfContactsMessage.get(0).getEmail().hashCode();


            //int hash = selectedContact.get(0).getName().hashCode();
            int color2 = Color.rgb(Math.abs(hash * 28439) % 255, Math.abs(hash * 211239) % 255, Math.abs(hash * 42368) % 255);
            GradientDrawable circle2 = (GradientDrawable) ContextCompat.getDrawable(getContext(), R.drawable.blue_circle).mutate();
            circle2.setColor(color2);
            ((CircleImageView) popupMessage.findViewById(R.id.avatar_recipient_1)).setBackground(circle2);

            if (listOfContactsMessage.get(0).getName() != null) {
                String initials = getInitials(listOfContactsMessage.get(0).getName());
                ((TextView) popupMessage.findViewById(R.id.initials_recipient_1)).setText(initials);
            } else if (listOfContactsMessage.get(0).getEmail() != null) {
                String initials = getInitials(listOfContactsMessage.get(0).getEmail());
                ((TextView) popupMessage.findViewById(R.id.initials_recipient_1)).setText(initials);
            }

            if (listOfContactsMessage.get(0).isGoogle()) {
                ((ImageView) popupMessage.findViewById(R.id.google_recipient_1)).setVisibility(View.VISIBLE);

            } else {
                ((ImageView) popupMessage.findViewById(R.id.google_recipient_1)).setVisibility(View.GONE);
            }

        }


        if (listOfContactsMessage.size() >= 2) {
            if (listOfContactsMessage.size() > 2)
                popupMessage.findViewById(R.id.moreCont).setVisibility(View.VISIBLE);
            else
                popupMessage.findViewById(R.id.moreCont).setVisibility(View.GONE);

            popupMessage.findViewById(R.id.secondC).setVisibility(View.VISIBLE);
            popupMessage.findViewById(R.id.firstC).setVisibility(View.VISIBLE);

            int hash = 0;

            if (listOfContactsMessage.get(0).getName() != null)
                hash = listOfContactsMessage.get(0).getName().hashCode();
            else if (listOfContactsMessage.get(0).getEmail() != null)
                hash = listOfContactsMessage.get(0).getEmail().hashCode();


            //int hash = selectedContact.get(0).getName().hashCode();
            int color2 = Color.rgb(Math.abs(hash * 28439) % 255, Math.abs(hash * 211239) % 255, Math.abs(hash * 42368) % 255);
            GradientDrawable circle2 = (GradientDrawable) ContextCompat.getDrawable(getContext(), R.drawable.blue_circle).mutate();
            circle2.setColor(color2);
            ((CircleImageView) popupMessage.findViewById(R.id.avatar_recipient_1)).setBackground(circle2);

            if (listOfContactsMessage.get(0).getName() != null) {
                String initials = getInitials(listOfContactsMessage.get(0).getName());
                ((TextView) popupMessage.findViewById(R.id.initials_recipient_1)).setText(initials);
            } else if (listOfContactsMessage.get(0).getEmail() != null) {
                String initials = getInitials(listOfContactsMessage.get(0).getEmail());
                ((TextView) popupMessage.findViewById(R.id.initials_recipient_1)).setText(initials);
            }


            if (listOfContactsMessage.get(0).isGoogle()) {
                ((ImageView) popupMessage.findViewById(R.id.google_recipient_1)).setVisibility(View.VISIBLE);
            } else {
                ((ImageView) popupMessage.findViewById(R.id.google_recipient_1)).setVisibility(View.GONE);
            }


            int hash_2 = 0;

            if (listOfContactsMessage.get(1).getName() != null)
                hash_2 = listOfContactsMessage.get(1).getName().hashCode();
            else if (listOfContactsMessage.get(1).getEmail() != null)
                hash_2 = listOfContactsMessage.get(1).getEmail().hashCode();


            //int hash = selectedContact.get(0).getName().hashCode();
            int color2_2 = Color.rgb(Math.abs(hash * 28439) % 255, Math.abs(hash * 211239) % 255, Math.abs(hash * 42368) % 255);
            GradientDrawable circle2_2 = (GradientDrawable) ContextCompat.getDrawable(getContext(), R.drawable.blue_circle).mutate();
            circle2.setColor(color2_2);
            ((CircleImageView) popupMessage.findViewById(R.id.avatar_recipient_2)).setBackground(circle2_2);

            if (listOfContactsMessage.get(1).getName() != null) {
                String initials = getInitials(listOfContactsMessage.get(1).getName());
                ((TextView) popupMessage.findViewById(R.id.initials_recipient_2)).setText(initials);
            } else if (listOfContactsMessage.get(1).getEmail() != null) {
                String initials = getInitials(listOfContactsMessage.get(1).getEmail());
                ((TextView) popupMessage.findViewById(R.id.initials_recipient_2)).setText(initials);
            }

            if (listOfContactsMessage.get(1).isGoogle()) {
                ((ImageView) popupMessage.findViewById(R.id.google_recipient_2)).setVisibility(View.VISIBLE);
            } else {
                ((ImageView) popupMessage.findViewById(R.id.google_recipient_2)).setVisibility(View.GONE);
            }

        }


        //================================


        ((TextView) popupMessage.findViewById(R.id.titleMessage)).setText(message.getTitle());
        ((TextView) popupMessage.findViewById(R.id.titleMessage)).setSelected(true);

        try {
            Calendar current = Calendar.getInstance();
            Calendar contactDate = Calendar.getInstance();
            current.setTime(new Date());
            contactDate.setTime(message.getDate());
            String timeStr = "";

            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(message.getDate());
            Time time = getRandomDate();
            time.setHours(cal1.get(Calendar.HOUR_OF_DAY));
            time.setMinutes(cal1.get(Calendar.MINUTE));
            time.setSeconds(cal1.get(Calendar.SECOND));

            if (current.get(Calendar.YEAR) == contactDate.get(Calendar.YEAR) && current.get(Calendar.MONTH) == contactDate.get(Calendar.MONTH) && current.get(Calendar.DAY_OF_MONTH) == contactDate.get(Calendar.DAY_OF_MONTH)) {

                timeStr = time.getHours() + ":";
                if (time.getMinutes() < 10) timeStr += "0";
                timeStr += time.getMinutes();
            } else if (current.get(Calendar.YEAR) == contactDate.get(Calendar.YEAR) && (current.get(Calendar.MONTH) != contactDate.get(Calendar.MONTH) || current.get(Calendar.DAY_OF_MONTH) != contactDate.get(Calendar.DAY_OF_MONTH))) {
                timeStr = contactDate.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.ENGLISH) + " " + contactDate.get(Calendar.DAY_OF_MONTH);
            } else {
                timeStr = contactDate.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.ENGLISH) + " " + String.valueOf(contactDate.get(Calendar.YEAR))/*.substring(2)*/;
            }


            ((TextView) popupMessage.findViewById(R.id.timeMessage)).setText(timeStr);
        } catch (Exception e) {
            System.out.println("ERROR TO GET TIME Contact Adapter");
        }

        if (message.getMessage() != null) {
            //message.setMessage(message.getMessage().replaceAll("\n", ""));
            //if(message.getMessage().contains("<br>")){
            ((TextView) popupMessage.findViewById(R.id.textMessage)).setText(Html.fromHtml(message.getMessage().trim()));
            //}else{
            //((TextView) popupMessage.findViewById(R.id.textMessage)).setText(message.getMessage().trim());
            // }

        } else
            ((TextView) popupMessage.findViewById(R.id.textMessage)).setText("");


        //((TextView) popupMessage.findViewById(R.id.textMessage)).setMovementMethod(new ScrollingMovementMethod());

        popupMessage.findViewById(R.id.frameTextMess).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupMessage.findViewById(R.id.popup_menu_message).setVisibility(View.GONE);
            }
        });


        if (message.getMessageData() != null) {
            final SpannableStringBuilder text = new SpannableStringBuilder(message.getMessage());
            for (int i = 0; i < message.getMessageData().size(); i++) {
                if (message.getMessageData().get(i).getClipboard() != null) {
                    int startI = message.getMessage().toLowerCase().indexOf(message.getMessageData().get(i).getClipboard().getValueCopy().toLowerCase());

                    final ForegroundColorSpan style = new ForegroundColorSpan(Color.parseColor("#567bac"));
                    //StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);


                    int finalI = i;
                    ClickableSpan clickableSpan = new ClickableSpan() {
                        @Override
                        public void onClick(@NonNull View widget) {

                            if (startI != -1) {
                                if (ClipboardType.isWeb(text.toString().substring(startI, startI + message.getMessageData().get(finalI).getClipboard().getValueCopy().length()))) {

                                    String uri = text.toString().substring(startI, startI + message.getMessageData().get(finalI).getClipboard().getValueCopy().length());
                                    if (!uri.contains("https://") && !uri.contains("http://"))
                                        uri = "https://" + uri;

                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                    i.setData(Uri.parse(uri));
                                    activityApp.startActivity(i);

                                } else if (ClipboardType.isEmail(text.toString().substring(startI, startI + message.getMessageData().get(finalI).getClipboard().getValueCopy().length()))) {

                                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                                    emailIntent.setData(Uri.parse("mailto:" + text.toString().substring(startI, startI + message.getMessageData().get(finalI).getClipboard().getValueCopy().length())));
                                    activityApp.startActivity(Intent.createChooser(emailIntent, "Send email"));

                                } else if (ClipboardType.isPhone(text.toString().substring(startI, startI + message.getMessageData().get(finalI).getClipboard().getValueCopy().length()))) {
                                    activityApp.startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", text.toString().substring(startI, startI + message.getMessageData().get(finalI).getClipboard().getValueCopy().length()), null)));
                                }
                            }

                        }

                        @Override
                        public void updateDrawState(@NonNull TextPaint ds) {
                            super.updateDrawState(ds);
                            if (ClipboardType.isWeb(text.toString().substring(startI, startI + message.getMessageData().get(finalI).getClipboard().getValueCopy().length()))) {
                                ds.setUnderlineText(true);
                            }
                        }


                    };


                    if (startI != -1) {
                        text.setSpan(style, startI, startI + message.getMessageData().get(i).getClipboard().getValueCopy().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        text.setSpan(clickableSpan, startI, startI + message.getMessageData().get(i).getClipboard().getValueCopy().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }


                    //text.setSpan(bss, startI, startI + searchString.length(), Spannable.SPAN_COMPOSING);

                    //   holder.userName.setText(text);
                }
            }
            if (text.toString().contains("<html>") || text.toString().contains("<br>"))
                ((TextView) popupMessage.findViewById(R.id.textMessage)).setText(Html.fromHtml(text.toString()));
            else
                ((TextView) popupMessage.findViewById(R.id.textMessage)).setText(text);

            ((TextView) popupMessage.findViewById(R.id.textMessage)).setMovementMethod(LinkMovementMethod.getInstance());


            ((TextView) popupMessage.findViewById(R.id.textMessage)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //((TextView) popupMessage.findViewById(R.id.textMessage)).clearFocus();
                }
            });

            ((TextView) popupMessage.findViewById(R.id.textMessage)).setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    return false;
                }
            });
            //((TextView) popupMessage.findViewById(R.id.textMessage)).setMovementMethod(LinkMovementMethod.getInstance());
            //Linkify.addLinks(((TextView) popupMessage.findViewById(R.id.textMessage)), Linkify.ALL);
        }


        //====================================

        if (message.getMessageData().size() > 0) {

            popupMessage.findViewById(R.id.linearCountDataMEssage).setVisibility(View.VISIBLE);
            popupMessage.findViewById(R.id.linear_arg).setVisibility(View.VISIBLE);


            RecyclerView recyclerView = popupMessage.findViewById(R.id.linear_arg);

            recyclerView.setLayoutManager(new LinearLayoutManager(activityApp, LinearLayoutManager.HORIZONTAL, false));


            MessageDataAdapter adapter = new MessageDataAdapter(activityApp, message.getMessageData(), message);


            recyclerView.setAdapter(adapter);
        } else {
            popupMessage.findViewById(R.id.linearCountDataMEssage).setVisibility(View.GONE);
            popupMessage.findViewById(R.id.linear_arg).setVisibility(View.GONE);
        }

        //===================================================

        if (!message.isCheckData()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {


                        ArrayList<Clipboard> listC = new ArrayList<>(ClipboardType.getListDataClipboard(message.getMessage(), activityApp));


                        if (listC != null
                                && !listC.isEmpty()
                                && listC.get(0) != null
                                && listC.get(0).getListClipboards() != null
                                && !listC.get(0).getListClipboards().isEmpty()) {

                            for (int q = 0; q < listC.get(0).getListClipboards().size(); q++) {
                                for (int j = 1; j < listC.get(0).getListClipboards().size(); j++) {
                                    if (q != j) {
                                        if (listC.get(0).getListClipboards().get(q).getValueCopy().equalsIgnoreCase(listC.get(0).getListClipboards().get(j).getValueCopy())) {
                                            listC.get(0).getListClipboards().remove(j);
                                            j--;
                                        }
                                    }
                                }
                            }


                            for (int iq = 0; iq < listC.get(0).getListClipboards().size() - 2; iq++) {

                                MessageData messageData = new MessageData();


                                messageData.setClipboard(listC.get(0).getListClipboards().get(iq));

                                           /* Contact cc = ContactCacheService.find2(messageData.getClipboard().getValueCopy(), messageData.getClipboard().getType(), null);
                                            if (cc != null)
                                                messageData.getClipboard().addContactToListSearch(cc);*/


                                if (!listC.get(0).getListClipboards().get(iq).getType().equals(ClipboardEnum.HASHTAG) && !listC.get(0).getListClipboards().get(iq).getValueCopy().isEmpty())
                                    message.getMessageData().add(messageData);
                            }

                        }

                        activityApp.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                message.setCheckData(true);

                                if (message.getMessageData().size() > 0) {


                                    popupMessage.findViewById(R.id.linearCountDataMEssage).setVisibility(View.VISIBLE);
                                    popupMessage.findViewById(R.id.linear_arg).setVisibility(View.VISIBLE);


                                    RecyclerView recyclerView = popupMessage.findViewById(R.id.linear_arg);

                                    recyclerView.setLayoutManager(new LinearLayoutManager(activityApp, LinearLayoutManager.HORIZONTAL, false));


                                    MessageDataAdapter adapter = new MessageDataAdapter(activityApp, message.getMessageData(), message);


                                    recyclerView.setAdapter(adapter);
                                } else {
                                    popupMessage.findViewById(R.id.linearCountDataMEssage).setVisibility(View.GONE);
                                    popupMessage.findViewById(R.id.linear_arg).setVisibility(View.GONE);
                                }

                                if (message.getMessageData() == null || message.getMessageData().isEmpty()) {
                                    ((ImageView) popupMessage.findViewById(R.id.imageExMessage)).setColorFilter(ContextCompat.getColor(activityApp, R.color.gray));
                                } else
                                    ((ImageView) popupMessage.findViewById(R.id.imageExMessage)).setColorFilter(ContextCompat.getColor(activityApp, R.color.colorPrimary));


                            }
                        });


                    } catch (Exception w) {
                        w.printStackTrace();
                    }

                }
            }).start();

        }


        //===========================

        if (message.getMessageData() == null || message.getMessageData().isEmpty()) {
            ((ImageView) popupMessage.findViewById(R.id.imageExMessage)).setColorFilter(ContextCompat.getColor(activityApp, R.color.gray));
        } else
            ((ImageView) popupMessage.findViewById(R.id.imageExMessage)).setColorFilter(ContextCompat.getColor(activityApp, R.color.colorPrimary));


        popupMessage.findViewById(R.id.linear_arg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        popupMessage.findViewById(R.id.deleteMessage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        String finalSendTo = sendTo;
        String finalSendToName = sendToName;
        String finalReplyFrom = replyFrom;

        popupMessage.findViewById(R.id.replyMessage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList<String> sTo = new ArrayList<>();
                ArrayList<String> sName = new ArrayList<>();

                sTo.add(finalSendTo);
                sName.add(finalSendToName);

                ShareTemplatesMessageReply.showTemplatesPopup(activityApp, false, sTo, sName, finalReplyFrom, message, TimeLineFragment.this, true);
            }
        });

        popupMessage.findViewById(R.id.gmailMessage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragmentMessage(message);
            }
        });

        popupMessage.findViewById(R.id.remindMessage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        popupMessage.findViewById(R.id.frameArrMoreMessageData).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showExtratorData(message);
            }
        });

        popupMessage.findViewById(R.id.extratorMessage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (message.getMessageData() != null && !message.getMessageData().isEmpty()) {
                    showExtratorData(message);
                    popupMessage.findViewById(R.id.label_popup_message).setVisibility(View.GONE);
                }

            }
        });

        popupMessage.findViewById(R.id.shareMessage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        ((TextView) popupMessage.findViewById(R.id.textMessage)).scrollTo(0, 0);

        //popupMessage.requestLayout();
        //((TextView)popupMessage.findViewById(R.id.textMessage)).requestLayout();
        new Thread(new Runnable() {
            @Override
            public void run() {

                if (message.getMessageData() != null) {
                    for (MessageData messageData : message.getMessageData()) {
                        if (messageData.getClipboard() != null && startSearchContact) {
                            Contact cc = ContactCacheService.find2(messageData.getClipboard().getValueCopy(), messageData.getClipboard().getType(), null);

                            if (cc != null) {
                                ArrayList<Contact> list = new ArrayList<>();
                                list.add(cc);
                                messageData.getClipboard().setListContactsSearch(list);
                            }
                        } else return;
                    }

                }
            }
        }).start();

        popupMessage.setVisibility(View.VISIBLE);


    }

    @Override
    public void openFragmentMessage(EmailMessage message) {
        setReadMessage(message);

        toolbarC.setNavigationIcon(R.drawable.icn_arrow_back);
        toolbarC.setNavigationOnClickListener(v -> {
            MainActivity.IS_PAUSE = false;
            activityApp.onBackPressed();
        });

        android.support.v4.app.FragmentManager fragmentManager = ((MainActivity) activityApp).getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.main_content, EmailMessageFragment.newInstance(message, email_contact, message.getAccount().getName(), "timeline")).addToBackStack("message").commit();
    }

    private void createContact(ContactOfMessage l) {

        Contact contact = new Contact();


        contact.setName(l.getName());


        contact.addEmail(l.getEmail());


        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                mainView.getContext());
        alertDialogBuilder.setTitle("Create new contact?");
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> {


                    ContactsFragment.createContact = true;

                    ArrayList<Contact> contactExtract = new ArrayList<>();
                    contactExtract.add(contact);

                    android.support.v4.app.FragmentManager fragmentManager = ((MainActivity) activityApp).getSupportFragmentManager();
                    android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.main_content, CreateFragment.newInstance(contactExtract)).addToBackStack("create").commit();


                })
                .setNegativeButton("No", (dialog, id) -> dialog.cancel());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    @Override
    public void openContactList(EmailMessage message) {
        RecyclerView recyclerView = popupContacts.findViewById(R.id.recycler_contacts_message);

        recyclerView.setLayoutManager(new LinearLayoutManager(mainView.getContext()));

        ArrayList<ContactOfMessage> listOfContactsMessage = new ArrayList<>();

        ContactOfMessage sender = new ContactOfMessage();

        ((TextView) popupContacts.findViewById(R.id.createContactMessage)).setTextColor(mainView.getContext().getResources().getColor(R.color.gray));

        popupContacts.findViewById(R.id.calcelFrame).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupContacts.setVisibility(View.GONE);
            }
        });


        boolean checkSelect = false;

        if (email_contact.contains(message.getAccount().getEmail())) {

            sender.setName(message.getAccount().getName());
            sender.setEmail(message.getAccount().getEmail());

            checkSelect = true;
        } else {

            sender.setName(message.getEmail_2());
            sender.setEmail(message.getAccount().getName().replaceAll("\\<\\>", ""));
            sender.setGoogle(true);

        }

        String data = null;


        for (int i2 = 0; i2 < message.getListHeaders().size(); i2++) {
            System.out.println(i2 + ". " + message.getListHeaders().get(i2).getValue() + ", type = " + message.getListHeaders().get(i2).getName());

            if (message.getListHeaders().get(i2).getName().equalsIgnoreCase("From") && !checkSelect) {
                String send = message.getListHeaders().get(i2).getValue();
                String email = ClipboardType.getEmail(send);

                if (email != null) {


                    send = send.replace(email, "");
                    send = send.replace("<", "");
                    send = send.replace(">", "");
                    send = send.replaceAll("\"", "");

                    send = send.trim();

                    if (send.length() > 0) sender.setName(send);
                }
            }

            if (message.getListHeaders().get(i2).getName().equalsIgnoreCase("To")) {
                data = message.getListHeaders().get(i2).getValue();
                break;
            }
        }

        if (data != null) {
            String[] mas = data.split(",");
            if (mas.length > 0) {
                for (int i = 0; i < mas.length; i++) {

                    String email = ClipboardType.getEmail(mas[i]);
                    ContactOfMessage contact = new ContactOfMessage();
                    if (email != null)
                        contact.setEmail(email.trim());

                    mas[i] = mas[i].replace(email, "");
                    mas[i] = mas[i].replace("<", "");
                    mas[i] = mas[i].replace(">", "");
                    mas[i] = mas[i].replaceAll("\"", "");

                    mas[i] = mas[i].trim();
                    if (mas[i].length() > 0) contact.setName(mas[i]);


                    SharedPreferences mSettings;
                    mSettings = getActivity().getSharedPreferences("accountUser", Context.MODE_PRIVATE);
                    Set<String> set = mSettings.getStringSet("accounts", null);

                    if (set != null && !set.isEmpty()) {

                        if (set.contains(email)) contact.setGoogle(true);

                    }

                    listOfContactsMessage.add(contact);
                }
            }
        }


        listOfContactsMessage.add(0, sender);

        int countFind = 0;

        for (ContactOfMessage contactOfMessage : listOfContactsMessage) {
            Contact contact = ContactCacheService.find2(contactOfMessage.getEmail(), ClipboardEnum.EMAIL, getActivity());
            if (contact != null) {
                contactOfMessage.setSearchContact(contact);
                countFind++;
            }
        }

        ((TextView) popupContacts.findViewById(R.id.textSelectMEss)).setText(String.valueOf(listOfContactsMessage.size()) + " contacts: " + countFind + " existing, " + String.valueOf(listOfContactsMessage.size() - countFind) + " new");


        ContactsOfMessageAdapter contactsOfMessageAdapter = new ContactsOfMessageAdapter(mainView.getContext(), listOfContactsMessage, popupContacts.findViewById(R.id.createContactMessage));

        recyclerView.setAdapter(contactsOfMessageAdapter);

        popupContacts.setVisibility(View.VISIBLE);

        popupContacts.findViewById(R.id.createContactMessage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (contactsOfMessageAdapter.getSelectList().size() == 1)
                    createContact(contactsOfMessageAdapter.getSelectList().get(0));
            }
        });

    }


    //===============================================================================================================================================================

    FrameLayout popupTemplate;
    FrameLayout editPopupTemplate;
    Template selectTemplate;

    @Override
    public void ShowTemplatePopup(Template template) {

        if (popupTemplate == null)
            popupTemplate = activityApp.findViewById(R.id.popupTemplate);


        if (popupMessage.getVisibility() == View.VISIBLE) {
            popupMessage.setVisibility(View.GONE);
        }

        /*if(editPopupTemplate == null)
            editPopupTemplate = activityApp.findViewById(R.id.popupTemplateEdit);

        if (editPopupTemplate.getVisibility() == View.VISIBLE) {
            editPopupTemplate.setVisibility(View.GONE);
            return;
        }*/

        if (popupTemplate.getVisibility() == View.VISIBLE) {
            popupTemplate.setVisibility(View.GONE);
            return;
        }

        selectTemplate = template;


        if (!template.isTemplateUser()) {

           /* selectTemplate = new Template();
            selectTemplate.setTemplateUser(false);
            selectTemplate.setTitle(template.getTitle());*/


            popupTemplate.findViewById(R.id.l_message_title).setVisibility(View.VISIBLE);
            popupTemplate.findViewById(R.id.footer_message).setVisibility(View.VISIBLE);

            ((TextView) popupTemplate.findViewById(R.id.recipient_email)).setText("<" + template.getEmailDataTemplate().getSendTo() + ">");


            ((TextView) popupTemplate.findViewById(R.id.namepopupTemplate)).setText(template.getEmailDataTemplate().getSendToName().get(0));


            ((TextView) popupTemplate.findViewById(R.id.textMessage_2)).setText(template.getEmailDataTemplate().getSendToName().get(0));

            ((TextView) popupTemplate.findViewById(R.id.textMessage_2)).setTextColor(mainView.getContext().getResources().getColor(R.color.black));

            ((TextView) popupTemplate.findViewById(R.id.textMessageHint)).setText("Start typing here _");

            ((TextView) popupTemplate.findViewById(R.id.textMessageHint)).setVisibility(View.VISIBLE);
            ((TextView) popupTemplate.findViewById(R.id.textMessage_p)).setVisibility(View.GONE);


            ((TextView) popupTemplate.findViewById(R.id.footer_message)).setText("\nbest regards, " + ((TextView) activityApp.findViewById(R.id.profileName)).getText().toString());

            ((TextView) popupTemplate.findViewById(R.id.texttemplate)).setText(selectTemplate.getTitle());


            ((TextView) popupTemplate.findViewById(R.id.textMessageHint)).setTextColor(mainView.getContext().getResources().getColor(R.color.gray_textView));


            ((TextView) popupTemplate.findViewById(R.id.subject_email)).setTextColor(mainView.getContext().getResources().getColor(R.color.gray));

            ((TextView) popupTemplate.findViewById(R.id.subject_email)).setText("Email subject");

            //((TextView)mainView.findViewById(R.id.textMessage)).setText("New template");


            popupTemplate.findViewById(R.id.linear_arg_).setVisibility(View.GONE);
            popupTemplate.findViewById(R.id.frameArrMoreMessageData).setVisibility(View.GONE);

            popupTemplate.findViewById(R.id.text_no_files_popup).setVisibility(View.VISIBLE);

            ((TextView) popupTemplate.findViewById(R.id.textMessageHint)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectTemplate == null)
                        showEditTemplate(template);
                    else
                        showEditTemplate(selectTemplate);
                }
            });

            ((ImageView) popupTemplate.findViewById(R.id.delete_template_icon)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));

            popupTemplate.findViewById(R.id.deleteMessage).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

        } else {

            selectTemplate = template;

            ((TextView) popupTemplate.findViewById(R.id.subject_email)).setTextColor(mainView.getContext().getResources().getColor(R.color.black));

            ((TextView) popupTemplate.findViewById(R.id.textMessageHint)).setVisibility(View.GONE);
            ((TextView) popupTemplate.findViewById(R.id.textMessage_p)).setVisibility(View.VISIBLE);

            popupTemplate.findViewById(R.id.l_message_title).setVisibility(View.GONE);
            popupTemplate.findViewById(R.id.footer_message).setVisibility(View.GONE);

            ((TextView) popupTemplate.findViewById(R.id.recipient_email)).setText("<" + template.getEmailDataTemplate().getSendTo() + ">");

            ((TextView) popupTemplate.findViewById(R.id.namepopupTemplate)).setText(template.getEmailDataTemplate().getSendToName().get(0));

            ((TextView) popupTemplate.findViewById(R.id.textMessage_2)).setText(template.getEmailDataTemplate().getSendToName().get(0));

            ((TextView) popupTemplate.findViewById(R.id.textMessage_2)).setTextColor(mainView.getContext().getResources().getColor(R.color.black));

            ((TextView) popupTemplate.findViewById(R.id.texttemplate)).setText(template.getTitle());

            ((TextView) popupTemplate.findViewById(R.id.subject_email)).setText(template.getSubject());

            ((ImageView) popupTemplate.findViewById(R.id.delete_template_icon)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));

            popupTemplate.findViewById(R.id.deleteMessage).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (template.isTemplateUser()) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                mainView.getContext());
                        alertDialogBuilder.setTitle("Do you want to delete template?");
                        alertDialogBuilder
                                .setCancelable(false)
                                .setPositiveButton("Yes", (dialog, id) -> {
                                   /* list.remove(template);
                                    updateBar();
                                    updateList();
                                    emailTemplatesAdapter.updateList(list);
                                    popupTemplate.setVisibility(View.GONE);

                                    Toast.makeText(mainView.getContext(), "Delete success", Toast.LENGTH_SHORT).show();*/

                                })
                                .setNegativeButton("No", (dialog, id) -> {

                                    dialog.cancel();
                                });

                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                }
            });

            //((TextView)mainView.findViewById(R.id.textMessage_p)).setTextColor(mainView.getContext().getResources().getColor(R.color.black));
            //((TextView)mainView.findViewById(R.id.textMessage))

            if (template.getTemplateText().toLowerCase().contains("%name")) {
                int startI = template.getTemplateText().toLowerCase().indexOf("%name");
                SpannableStringBuilder text = new SpannableStringBuilder(template.getTemplateText());
                //final ForegroundColorSpan style = new ForegroundColorSpan(Color.parseColor("#87aade"));
                //StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
                //text.setSpan(style, startI, startI + 5, Spannable.SPAN_COMPOSING);
                text = new SpannableStringBuilder(text.toString().replace("%Name", template.getEmailDataTemplate().getSendToName().get(0)));
                text = new SpannableStringBuilder(text.toString().replace("%name", template.getEmailDataTemplate().getSendToName().get(0)));
                //text.setSpan(bss, startI, startI + search.length(), Spannable.SPAN_COMPOSING);
                ((TextView) popupTemplate.findViewById(R.id.textMessage_p)).setText(text);
            } else
                ((TextView) popupTemplate.findViewById(R.id.textMessage_p)).setText(template.getTemplateText());


        }

        if (selectTemplate == null)
            initRecyclerPreview(template);
        else
            initRecyclerPreview(selectTemplate);


        popupTemplate.findViewById(R.id.selectFilePopupTemplate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setType("*/*");
                startActivityForResult(intent, 42);
            }
        });

        popupTemplate.findViewById(R.id.type_bold).setVisibility(View.VISIBLE);


        popupTemplate.setVisibility(View.VISIBLE);


        popupTemplate.findViewById(R.id.replyMessage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectTemplate == null)
                    showEditTemplate(template);
                else
                    showEditTemplate(selectTemplate);
            }
        });


        popupTemplate.findViewById(R.id.sendMEssagePreview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                new Thread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void run() {


                        try {

                            activityApp.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(activityApp, "Sending message...", Toast.LENGTH_SHORT).show();
                                }
                            });

                            Properties props = new Properties();

                            Session session = Session.getDefaultInstance(props, null);

                            MimeMessage email = new MimeMessage(session);

                            email.setFrom(new InternetAddress(template.getEmailDataTemplate().getRecipient()));

                            email.addRecipient(javax.mail.Message.RecipientType.TO,
                                    new InternetAddress(template.getEmailDataTemplate().getSendTo().get(0)));

                            //email.addRecipient(javax.mail.Message.RecipientType.TO,
                            //        new InternetAddress("andreww.pataletaa@gmail.com"));


                            if (template.getSubject() == null || template.getSubject().isEmpty()) {
                                email.setSubject("No subject");
                            } else {
                                email.setSubject(template.getSubject());
                            }

                            MimeBodyPart mimeBodyPart = new MimeBodyPart();

                            if (template.getTemplateText() == null || template.getTemplateText().isEmpty()) {
                                String text = ((TextView) popupTemplate.findViewById(R.id.textMessage_1)).getText().toString() + " ";
                                text += ((TextView) popupTemplate.findViewById(R.id.textMessage_2)).getText().toString() + "\n";
                                if (popupTemplate.findViewById(R.id.textMessageHint).getVisibility() == View.GONE)
                                    text += ((TextView) popupTemplate.findViewById(R.id.textMessage_p)).getText().toString() + "\n";
                                text += ((TextView) popupTemplate.findViewById(R.id.footer_message)).getText().toString();

                                mimeBodyPart.setContent(text, "text/plain; charset=utf-8");
                            } else {
                                mimeBodyPart.setContent(template.getTemplateText(), "text/plain; charset=utf-8");
                            }


                            Multipart multipart = new MimeMultipart();
                            multipart.addBodyPart(mimeBodyPart);


                            if (template.getListOfFiles() != null) {
                                for (int i = 0; i < template.getListOfFiles().size(); i++) {

                                    try {
                                        if (template.getListOfFiles().get(i).getFinalUrl() == null) {

                                            //storage.getFiles(path + "/Extime/ExtimeContacts/");

                                            InputStream selectedFileInputStream =
                                                    activityApp.getContentResolver().openInputStream(template.getListOfFiles().get(i).getUrl());

                                            byte[] bytes = IOUtils.toByteArray(selectedFileInputStream);
                                            String path = activityApp.getFilesDir() + "/templateFiles";
                                            File f = new File(path, template.getListOfFiles().get(i).getFilename());

                                            FileOutputStream fileOutFile = new FileOutputStream(f);
                                            fileOutFile.write(bytes);
                                            fileOutFile.close();

                                            Uri contentUri = FileProvider.getUriForFile(activityApp, "com.extime.fileprovider", f.getAbsoluteFile());
                                            template.getListOfFiles().get(i).setFinalUrl(f.getAbsolutePath());


                                        }

                                        File f = new File(template.getListOfFiles().get(i).getFinalUrl());

                                        mimeBodyPart = new MimeBodyPart();
                                        DataSource source = new FileDataSource(f.getPath());

                                        mimeBodyPart.setDataHandler(new DataHandler(source));
                                        mimeBodyPart.setFileName(f.getName());

                                        multipart.addBodyPart(mimeBodyPart);

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }


                            email.setContent(multipart);

                            Message message = new Message();

                            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

                             /*   MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
                                mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
                                mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
                                mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
                                mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
                                mc.addMailcap("message/rfc822;; x-java-content- handler=com.sun.mail.handlers.message_rfc822");*/

                            Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
                            try {

                                email.writeTo(buffer);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                            byte[] bytes = buffer.toByteArray();
                            String encodedEmail = Base64.encodeBase64URLSafeString(bytes);

                            message.setRaw(encodedEmail);


                            HttpTransport transport = AndroidHttp.newCompatibleTransport();
                            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();


                            Gmail service = new Gmail.Builder(transport, jsonFactory, template.getEmailDataTemplate().getAccountCredential().getCredential())
                                    .setApplicationName("Extime")
                                    .build();

                            service.users().messages().send(template.getEmailDataTemplate().getRecipient(), message).execute();

                            activityApp.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(activityApp, "Send success", Toast.LENGTH_SHORT).show();
                                }
                            });


                        } catch (MessagingException | IOException e) {
                            e.printStackTrace();
                        }

                    }
                }).start();


            }
        });

    }


    public void initRecyclerPreview(Template template) {
        if (template.getListOfFiles() != null && !template.getListOfFiles().isEmpty()) {
            popupTemplate.findViewById(R.id.linearCountDataMEssage).setVisibility(View.VISIBLE);
            popupTemplate.findViewById(R.id.text_no_files_popup).setVisibility(View.GONE);
            popupTemplate.findViewById(R.id.linear_arg_).setVisibility(View.VISIBLE);
            popupTemplate.findViewById(R.id.frameArrMoreMessageData).setVisibility(View.VISIBLE);


            RecyclerView recyclerView = popupTemplate.findViewById(R.id.linear_arg_);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            TemplateFileAdapter adapter = new TemplateFileAdapter(getContext(), template.getListOfFiles(), template, activityApp);
            recyclerView.setAdapter(adapter);

        } else {
            popupTemplate.findViewById(R.id.linearCountDataMEssage).setVisibility(View.GONE);
            popupTemplate.findViewById(R.id.text_no_files_popup).setVisibility(View.VISIBLE);
        }
    }

    public void initRecyclerPreviewEdit(Template template) {
        if (template.getListOfFiles() != null && !template.getListOfFiles().isEmpty()) {
            editPopupTemplate.findViewById(R.id.linearCountDataMEssage).setVisibility(View.VISIBLE);
            editPopupTemplate.findViewById(R.id.text_no_files_popup).setVisibility(View.GONE);
            editPopupTemplate.findViewById(R.id.linear_arg_).setVisibility(View.VISIBLE);
            editPopupTemplate.findViewById(R.id.frameArrMoreMessageData).setVisibility(View.VISIBLE);


            RecyclerView recyclerView = editPopupTemplate.findViewById(R.id.linear_arg_);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            TemplateFileAdapter adapter = new TemplateFileAdapter(getContext(), template.getListOfFiles(), template, activityApp);
            recyclerView.setAdapter(adapter);

        } else {
            editPopupTemplate.findViewById(R.id.linearCountDataMEssage).setVisibility(View.GONE);
            editPopupTemplate.findViewById(R.id.text_no_files_popup).setVisibility(View.VISIBLE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(UpdateMessageAdapter event) {
        messageAdapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(HidePopupMessage event) {
        popupMessage.setVisibility(View.GONE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NotifyKanbanTimeline event) {
        kanbanAdapter_1.notifyDataSetChanged();
        kanbanAdapter_2.notifyDataSetChanged();
        kanbanAdapter_3.notifyDataSetChanged();
        kanbanAdapter_4.notifyDataSetChanged();
        kanbanAdapter_5.notifyDataSetChanged();
        kanbanAdapter_6.notifyDataSetChanged();
        kanbanAdapter_7.notifyDataSetChanged();
        kanbanAdapter_8.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ClickMessageKanban event) {
        clickMessage(event.getEmailMessage());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(OpenMessageKanban event) {
        openFragmentMessage(event.getEmailMessage());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventSetStarMessage event) {
        setStarMessage(event.getEmailMessage(), event.isStar());

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SetKanbanTimeline event) {
        setKanbanMode(false);

    }

    public void showEditTemplate(Template template) {

        if (editPopupTemplate == null) {
            editPopupTemplate = activityApp.findViewById(R.id.popupTemplateEdit);
        }

        if (!template.isTemplateUser()) {


            EditText textMessage_edit = editPopupTemplate.findViewById(R.id.textMessage_edit);

            ((TextView) editPopupTemplate.findViewById(R.id.recipient_email_edit)).setText("<" + template.getEmailDataTemplate().getSendTo() + ">");


            SpannableString text = new SpannableString("Hello " + template.getEmailDataTemplate().getSendToName() + "\n  \n\nbest regards, " + ((TextView) activityApp.findViewById(R.id.profileName)).getText().toString());

            textMessage_edit.setText(text);
            ((EditText) editPopupTemplate.findViewById(R.id.textMessage_edit_user)).setVisibility(View.GONE);
            ((EditText) editPopupTemplate.findViewById(R.id.textMessage_edit)).setVisibility(View.VISIBLE);
            ((EditText) editPopupTemplate.findViewById(R.id.textMessage_edit)).setSelection(14);
            ((EditText) editPopupTemplate.findViewById(R.id.textMessage_edit)).requestFocus();

            editPopupTemplate.findViewById(R.id.linear_arg_).setVisibility(View.GONE);
            editPopupTemplate.findViewById(R.id.frameArrMoreMessageData).setVisibility(View.GONE);
            //editPopupTemplate.findViewById(R.id.text_no_files_popup_).setVisibility(View.VISIBLE);

            ((EditText) editPopupTemplate.findViewById(R.id.edit_name)).setText("");
            ((EditText) editPopupTemplate.findViewById(R.id.subject_email_edit)).setText("");

            InputMethodManager imm = (InputMethodManager)
                    getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(((EditText) editPopupTemplate.findViewById(R.id.textMessage_edit)), InputMethodManager.SHOW_IMPLICIT);

            /*textMessage_edit.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before,
                                          int count) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (!s.toString().startsWith("Hello %Name")) {

                        String q = "Hello %Name";

                        textMessage_edit.setText(q.toCharArray(), 0,11);
                        Selection.setSelection(textMessage_edit.getText(), textMessage_edit
                                .getText().length());

                    }
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count,
                                              int after) {
                    // TODO Auto-generated method stub

                }



            });*/

        } else {
            //((ImageView) popupTemplate.findViewById(R.id.delete_template_icon)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));
            ((EditText) editPopupTemplate.findViewById(R.id.textMessage_edit)).setVisibility(View.GONE);
            ((EditText) editPopupTemplate.findViewById(R.id.textMessage_edit_user)).setVisibility(View.VISIBLE);

            ((TextView) editPopupTemplate.findViewById(R.id.recipient_email_edit)).setText("<" + template.getEmailDataTemplate().getSendTo() + ">");

            SpannableString text = new SpannableString(template.getTemplateText());

            if (template.getTemplateText().toLowerCase().contains("%name")) {
                //int ind = template.getTemplateText().indexOf("%Name");
                //text.setSpan(new ForegroundColorSpan(Color.parseColor("#87aade")), ind, ind + 5, 0);
                text = new SpannableString(text.toString().replace("%Name", template.getEmailDataTemplate().getSendToName().get(0)));
                text = new SpannableString(text.toString().replace("%name", template.getEmailDataTemplate().getSendToName().get(0)));

                ((EditText) editPopupTemplate.findViewById(R.id.textMessage_edit_user)).setText(text);

            } else
                ((EditText) editPopupTemplate.findViewById(R.id.textMessage_edit_user)).setText(text);


            if (!template.getTitle().equalsIgnoreCase("New template"))
                ((EditText) editPopupTemplate.findViewById(R.id.edit_name)).setText(template.getTitle());
            else
                ((EditText) editPopupTemplate.findViewById(R.id.edit_name)).setText("");

            ((EditText) editPopupTemplate.findViewById(R.id.subject_email_edit)).setText(template.getSubject());

        }

        ((TextView) editPopupTemplate.findViewById(R.id.recEdit)).setText(template.getEmailDataTemplate().getSendToName().get(0));

        editPopupTemplate.findViewById(R.id.selectFilePopupTemplateEdit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setType("*/*");
                startActivityForResult(intent, 42);
            }
        });

        initRecyclerPreviewEdit(template);


        editPopupTemplate.findViewById(R.id.cancelEditField).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupTemplate.setVisibility(View.VISIBLE);
                editPopupTemplate.setVisibility(View.GONE);
            }
        });

        /*popupTemplate.findViewById(R.id.deleteMessage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (template.isTemplateUser()) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            mainView.getContext());
                    alertDialogBuilder.setTitle("Do you want to delete template?");
                    alertDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("Yes", (dialog, id) -> {
                                list.remove(template);
                                updateBar();
                                updateList();
                                emailTemplatesAdapter.updateList(list);
                                activityApp.findViewById(R.id.popupTemplate).setVisibility(View.GONE);

                                Toast.makeText(mainView.getContext(), "Delete success", Toast.LENGTH_SHORT).show();

                            })
                            .setNegativeButton("No", (dialog, id) -> {

                                dialog.cancel();
                            });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            }
        });
*/
        editPopupTemplate.findViewById(R.id.updateContactField).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!template.isTemplateUser()) {


                    if (((EditText) editPopupTemplate.findViewById(R.id.edit_name)).getText().length() == 0) {
                        template.setTitle(template.getTitle());
                    } else {
                        template.setTitle(((EditText) editPopupTemplate.findViewById(R.id.edit_name)).getText().toString());
                    }

                    if (((EditText) editPopupTemplate.findViewById(R.id.subject_email_edit)).getText().length() == 0) {
                        template.setSubject("No subject");
                    } else {
                        template.setSubject(((EditText) editPopupTemplate.findViewById(R.id.subject_email_edit)).getText().toString());
                    }

                    template.setTemplateUser(true);
                    template.setTemplateText(((EditText) editPopupTemplate.findViewById(R.id.textMessage_edit)).getText().toString());

                    template.setDateCreate(new Date());

                    template.setListOfFiles(template.getListOfFiles());

                    Date date1 = template.getDateCreate();
                    Calendar cal1 = Calendar.getInstance();
                    cal1.setTime(date1);
                    Time time1 = getRandomDate();
                    time1.setHours(cal1.get(Calendar.HOUR_OF_DAY));
                    time1.setMinutes(cal1.get(Calendar.MINUTE));
                    time1.setSeconds(cal1.get(Calendar.SECOND));

                    template.time = time1.toString();

                    editPopupTemplate.setVisibility(View.GONE);

                    ShowTemplatePopup(template);

                    Toast.makeText(mainView.getContext(), "Email template updated!", Toast.LENGTH_SHORT).show();


                    //Toast.makeText(mainView.getContext(), "Email template created!", Toast.LENGTH_SHORT).show();


                } else {
                    if (((EditText) editPopupTemplate.findViewById(R.id.edit_name)).getText().length() == 0) {
                        //template.setTitle("New template");
                    } else {
                        template.setTitle(((EditText) editPopupTemplate.findViewById(R.id.edit_name)).getText().toString());
                    }

                    if (((EditText) editPopupTemplate.findViewById(R.id.subject_email_edit)).getText().length() == 0) {
                        template.setSubject("No subject");
                    } else {
                        template.setSubject(((EditText) editPopupTemplate.findViewById(R.id.subject_email_edit)).getText().toString());
                    }

                    template.setTemplateUser(true);
                    template.setTemplateText(((EditText) editPopupTemplate.findViewById(R.id.textMessage_edit_user)).getText().toString());


                    editPopupTemplate.setVisibility(View.GONE);

                    ShowTemplatePopup(template);

                    Toast.makeText(mainView.getContext(), "Email template updated!", Toast.LENGTH_SHORT).show();


                }
            }
        });


        popupTemplate.setVisibility(View.GONE);
        editPopupTemplate.setVisibility(View.VISIBLE);
    }


    //=============================================

    public void setReadMessage(EmailMessage message) {
        if (message.getListOfTypeMessage() != null && message.getListOfTypeMessage().contains("INBOX") && message.getListOfTypeMessage().contains("UNREAD")) {
            new Thread(new Runnable() {
                @Override
                public void run() {

                    ArrayList<String> listLabel = new ArrayList<>();
                    listLabel.add("UNREAD");

                    try {
                        HttpTransport transport = AndroidHttp.newCompatibleTransport();
                        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
                        Gmail service = null;
                        for (int i = 0; i < listCredential.size(); i++) {
                            if (listCredential.get(i).getSelectedAccountName().equalsIgnoreCase(message.getUser())) {
                                service = new Gmail.Builder(transport, jsonFactory, listCredential.get(i))
                                        .setApplicationName(APPLICATION_NAME)
                                        .build();
                            }
                        }


                        if (service != null) {


                            ModifyMessageRequest mods = new ModifyMessageRequest().setRemoveLabelIds(listLabel);

                            service.users().messages().modify(message.getUser(), message.getId(), mods).execute();


                            message.getListOfTypeMessage().remove(message.getListOfTypeMessage().indexOf("UNREAD"));

                            activityApp.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    messageAdapter.notifyDataSetChanged();

                                    updateKanbanLoad();
                                }
                            });


                        }


                    } catch (
                            Exception e) {
                        e.printStackTrace();
                    }

                }
            }).start();
        }
    }

    @Override
    public void setStarMessage(EmailMessage message, boolean star) {
        if (message.getListOfTypeMessage() != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {

                    try {
                        HttpTransport transport = AndroidHttp.newCompatibleTransport();
                        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
                        Gmail service = null;
                        for (int i = 0; i < listCredential.size(); i++) {
                            if (listCredential.get(i).getSelectedAccountName().equalsIgnoreCase(message.getUser())) {
                                service = new Gmail.Builder(transport, jsonFactory, listCredential.get(i))
                                        .setApplicationName(APPLICATION_NAME)
                                        .build();
                            }
                        }


                        if (service != null) {

                            ArrayList<String> listLabel = new ArrayList<>();

                            listLabel.add("STARRED");

                            ModifyMessageRequest mods;

                            if (!star)
                                message.getListOfTypeMessage().remove(message.getListOfTypeMessage().indexOf("STARRED"));
                            else
                                message.getListOfTypeMessage().add("STARRED");

                            if (mode == 2) {
                                if (star)
                                    EventBus.getDefault().post(new SetKanbanTimeline());
                                else {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                Thread.sleep(2000);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                            EventBus.getDefault().post(new SetKanbanTimeline());
                                        }
                                    }).start();
                                }
                            }

                            if (!star)
                                mods = new ModifyMessageRequest().setRemoveLabelIds(listLabel);
                            else
                                mods = new ModifyMessageRequest().setAddLabelIds(listLabel);

                            service.users().messages().modify(message.getUser(), message.getId(), mods).execute();


                            activityApp.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    messageAdapter.notifyDataSetChanged();
                                }
                            });


                        }


                    } catch (
                            Exception e) {
                        e.printStackTrace();
                    }


                }
            }).start();
        }
    }

    @Override
    public boolean isOpenPreview() {
        if (popupMessage != null)
            return popupMessage.getVisibility() == View.VISIBLE ? true : false;
        else return false;
    }

    public void showExtratorData(EmailMessage message) {
        FrameLayout layoutParams = popupMessage.findViewById(R.id.frameTextMess);

        startSearchContact = false;

        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) layoutParams.getLayoutParams();

        float px = (float) (80 * mainView.getContext().getResources().getDisplayMetrics().density);

        //122.5

        lp.weight = 0;

        lp.height = (int) px;

        layoutParams.setLayoutParams(lp);

        //===========

        popupMessage.findViewById(R.id.linearCountDataMEssage).setVisibility(View.GONE);
        popupMessage.findViewById(R.id.linear_arg).setVisibility(View.GONE);
        popupMessage.findViewById(R.id.linear_button_message).setVisibility(View.GONE);
        //popupMessage.findViewById(R.id.line_button).setVisibility(View.GONE);


        //===========


        if (ProfileFragment.isKeyboard) {


            /*FrameLayout.LayoutParams lp1 = (FrameLayout.LayoutParams) popupMessage.findViewById(R.id.popup_mess).getLayoutParams();
            FrameLayout.LayoutParams layoutParamss = (FrameLayout.LayoutParams) popupMessage.getLayoutParams();

            float px_b = 295 * getContext().getResources().getDisplayMetrics().density;




            layoutParamss.height = (int) px_b;

            layoutParamss.gravity = Gravity.BOTTOM;

            lp1.height = ViewGroup.LayoutParams.MATCH_PARENT;*/

            FrameLayout.LayoutParams lp1 = (FrameLayout.LayoutParams) popupMessage.findViewById(R.id.popup_mess).getLayoutParams();

            lp1.height = ViewGroup.LayoutParams.MATCH_PARENT;

            popupMessage.findViewById(R.id.popup_mess).setLayoutParams(lp1);

            FrameLayout.LayoutParams lp2 = (FrameLayout.LayoutParams) popupMessage.getLayoutParams();

            lp2.height = ViewGroup.LayoutParams.MATCH_PARENT;

            popupMessage.setLayoutParams(lp2);


            //popupMessage.setLayoutParams(layoutParamss);

            //LinearLayout.LayoutParams lp2 = (LinearLayout.LayoutParams) popupMessage.findViewById(R.id.bottomFrameClipboard).getLayoutParams();

            //float px_b2 = 45 * getContext().getResources().getDisplayMetrics().density;

            //lp2.height = (int) px_b2;

            //popupMessage.requestLayout();

            //popupMessage.findViewById(R.id.recyclerDataMessage).requestLayout();

            //popupMessage.findViewById(R.id.bottomFrameClipboard).requestLayout();


        } else {

            FrameLayout.LayoutParams lp1 = (FrameLayout.LayoutParams) popupMessage.findViewById(R.id.popup_mess).getLayoutParams();

            lp1.height = ViewGroup.LayoutParams.MATCH_PARENT;

            popupMessage.findViewById(R.id.popup_mess).setLayoutParams(lp1);

            FrameLayout.LayoutParams lp2 = (FrameLayout.LayoutParams) popupMessage.getLayoutParams();

            lp2.height = ViewGroup.LayoutParams.MATCH_PARENT;

            popupMessage.setLayoutParams(lp2);

        }


        popupMessage.findViewById(R.id.test1).setVisibility(View.VISIBLE);


        RecyclerView recyclerView = popupMessage.findViewById(R.id.recyclerDataMessage);
        recyclerView.setLayoutManager(new LinearLayoutManager(activityApp));
        //ArrayList<MessageData> list = new ArrayList<>();

                /*for(MessageData messageData : message.getMessageData()){
                    if(messageData.getClipboard() != null)
                        list.add(messageData);
                }*/


        popupMessage.findViewById(R.id.downloadMessageData).setVisibility(View.GONE);
        popupMessage.findViewById(R.id.copyMessageData).setVisibility(View.GONE);
        popupMessage.findViewById(R.id.shareMessageData).setVisibility(View.GONE);

        popupMessage.findViewById(R.id.textCountMessageData).setVisibility(View.VISIBLE);
        popupMessage.findViewById(R.id.hideMoreMessageData).setVisibility(View.VISIBLE);

        MessageDataExtratorAdapter messageDataExtratorAdapter = new MessageDataExtratorAdapter(message.getMessageData(), activityApp, activityApp, popupMessage.findViewById(R.id.createContactMessageData), popupMessage.findViewById(R.id.textCountMessageData), popupMessage.findViewById(R.id.shareMessageData), popupMessage.findViewById(R.id.hideMoreMessageData), popupMessage.findViewById(R.id.copyMessageData), popupMessage.findViewById(R.id.downloadMessageData));
        recyclerView.setAdapter(messageDataExtratorAdapter);

        int count_files = 0;
        int count_contact = 0;

        for (MessageData messageData : message.getMessageData()) {
            if (messageData.getClipboard() == null)
                count_files++;
            else {
                if (messageData.getClipboard().getListContactsSearch() == null || messageData.getClipboard().getListContactsSearch().isEmpty()) {

                } else
                    count_contact++;
            }
        }


        String t = message.getMessageData().size() + " items:";
        if (count_contact > 0) {
            if (count_contact == 1)
                t += " 1 contact,";
            else
                t += " " + count_contact + " contacts,";
        }
        if (count_files > 0) {
            if (count_files == 1)
                t += " 1 file";
            else
                t += " " + count_files + " files";
        }

        if (t.charAt(t.length() - 1) == ',')
            t = t.substring(0, t.length() - 1);

        int startI = t.indexOf(":") + 1;
        final SpannableStringBuilder text = new SpannableStringBuilder(t);
        final ForegroundColorSpan style = new ForegroundColorSpan(Color.parseColor("#808080"));

        //StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
        if (startI != -1) {
            text.setSpan(style, startI, text.length(), Spannable.SPAN_COMPOSING);
            //text.setSpan(bss, startI, startI + search.length(), Spannable.SPAN_COMPOSING);
        }
        //holder.hashTagValue.setText(text);

        ((TextView) popupMessage.findViewById(R.id.textCountMessageData)).setText(text);


        popupMessage.findViewById(R.id.hideMoreMessageData).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideMessageData();
            }
        });


        popupMessage.findViewById(R.id.bottomFrameMessage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideMessageData();

            }
        });

        popupMessage.findViewById(R.id.createContactMessageDataLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (messageDataExtratorAdapter.getSelectMessageData().size() == 0) return;


                Contact contact = new Contact();
                ArrayList<MessageData> listEx = messageDataExtratorAdapter.getSelectMessageData();

               /* if (listEx.size() == 0) {
                    Toast.makeText(contactsFragment.getContext(), "Choose data", Toast.LENGTH_SHORT).show();
                    return;
                }*/

                for (MessageData extrator : listEx) {
                    if (extrator.getClipboard() == null) continue;

                    /*if (extrator.getListClipboards() != null && !extrator.getListClipboards().isEmpty())
                        continue;*/

                    if (extrator.getClipboard().getType().equals(ClipboardEnum.NAME))
                        contact.setName(extrator.getClipboard().getValueCopy().trim());

                    if (extrator.getClipboard().getType().equals(ClipboardEnum.FACEBOOK)) {
                        if (contact.getSocialModel() == null)
                            contact.setSocialModel(new SocialModel());
                        if (contact.hasFacebook && contact.getSocialModel().getFacebookLink() != null && !contact.getSocialModel().getFacebookLink().isEmpty()) {
                            contact.listOfContactInfo.add(new ContactInfo("note", extrator.getClipboard().getValueCopy(), false, false, false, true, false));
                        } else {
                            contact.getSocialModel().setFacebookLink(extrator.getClipboard().getValueCopy());
                            contact.hasFacebook = true;
                            if (extrator.getClipboard().getNameFromSocial() != null && (contact.getName() == null || contact.getName().isEmpty()))
                                contact.setName(extrator.getClipboard().getNameFromSocial());
                        }


                        //ParsingClipboard parsingSocial= new ParsingClipboard(extrator.getValueCopy().toString(), SocialEnums.FACEBOOK);
                        //parsingSocial.execute();
                    }

                    if (extrator.getClipboard().getType().equals(ClipboardEnum.INSTAGRAM)) {
                        if (contact.getSocialModel() == null)
                            contact.setSocialModel(new SocialModel());
                        if (contact.hasInst && contact.getSocialModel().getInstagramLink() != null && !contact.getSocialModel().getInstagramLink().isEmpty()) {
                            contact.listOfContactInfo.add(new ContactInfo("note", extrator.getClipboard().getValueCopy(), false, false, false, true, false));
                        } else {
                            contact.getSocialModel().setInstagramLink(extrator.getClipboard().getValueCopy());
                            contact.hasInst = true;
                            if (extrator.getClipboard().getNameFromSocial() != null && (contact.getName() == null || contact.getName().isEmpty())) {
                                contact.setName(extrator.getClipboard().getNameFromSocial());
                            }
                        }

                        //ParsingClipboard parsingSocial= new ParsingClipboard(extrator.getValueCopy().toString(), SocialEnums.INSTAGRAM);
                        //parsingSocial.execute();
                    }

                    if (extrator.getClipboard().getType().equals(ClipboardEnum.VK)) {
                        if (contact.getSocialModel() == null)
                            contact.setSocialModel(new SocialModel());
                        if (contact.hasVk && contact.getSocialModel().getVkLink() != null && !contact.getSocialModel().getVkLink().isEmpty()) {
                            contact.listOfContactInfo.add(new ContactInfo("note", extrator.getClipboard().getValueCopy(), false, false, false, true, false));
                        } else {
                            contact.getSocialModel().setVkLink(extrator.getClipboard().getValueCopy());
                            contact.hasVk = true;
                            if (extrator.getClipboard().getNameFromSocial() != null && (contact.getName() == null || contact.getName().isEmpty()))
                                contact.setName(extrator.getClipboard().getNameFromSocial());
                        }
                        //ParsingClipboard parsingSocial= new ParsingClipboard(extrator.getValueCopy().toString(), SocialEnums.VK);
                        //parsingSocial.execute();
                    }

                    if (extrator.getClipboard().getType().equals(ClipboardEnum.TWITTER)) {
                        if (contact.getSocialModel() == null)
                            contact.setSocialModel(new SocialModel());
                        if (contact.hasTwitter && contact.getSocialModel().getTwitterLink() != null && !contact.getSocialModel().getTwitterLink().isEmpty()) {
                            contact.listOfContactInfo.add(new ContactInfo("note", extrator.getClipboard().getValueCopy(), false, false, false, true, false));
                        } else {
                            contact.getSocialModel().setTwitterLink(extrator.getClipboard().getValueCopy());
                            contact.hasTwitter = true;
                            if (extrator.getClipboard().getNameFromSocial() != null && (contact.getName() == null || contact.getName().isEmpty()))
                                contact.setName(extrator.getClipboard().getNameFromSocial());
                        }
                        //ParsingClipboard parsingSocial= new ParsingClipboard(extrator.getValueCopy().toString(), SocialEnums.VK);
                        //parsingSocial.execute();
                    }

                    if (extrator.getClipboard().getType().equals(ClipboardEnum.YOUTUBE)) {
                        if (extrator.getClipboard().getValueCopy().contains("user") || extrator.getClipboard().getValueCopy().contains("channel")) {
                            if (contact.getSocialModel() == null)
                                contact.setSocialModel(new SocialModel());
                            if (contact.hasYoutube && contact.getSocialModel().getYoutubeLink() != null && !contact.getSocialModel().getYoutubeLink().isEmpty()) {
                                contact.listOfContactInfo.add(new ContactInfo("note", extrator.getClipboard().getValueCopy(), false, false, false, true, false));
                            } else {
                                contact.getSocialModel().setYoutubeLink(extrator.getClipboard().getValueCopy());
                                contact.hasYoutube = true;
                                if (extrator.getClipboard().getNameFromSocial() != null && (contact.getName() == null || contact.getName().isEmpty()))
                                    contact.setName(extrator.getClipboard().getNameFromSocial());
                            }
                        } else {
                            contact.addNote(extrator.getClipboard().getValueCopy());
                        }
                    }

                    if (extrator.getClipboard().getType().equals(ClipboardEnum.LINKEDIN)) {
                        if (contact.getSocialModel() == null)
                            contact.setSocialModel(new SocialModel());
                        contact.getSocialModel().setLinkedInLink(extrator.getClipboard().getValueCopy());
                        contact.hasLinked = true;
                    }

                    if (extrator.getClipboard().getType().equals(ClipboardEnum.SKYPE)) {
                        if (contact.getSocialModel() == null)
                            contact.setSocialModel(new SocialModel());
                        contact.getSocialModel().setSkypeLink(extrator.getClipboard().getValueCopy());
                        contact.hasSkype = true;
                    }

                    if (extrator.getClipboard().getType().equals(ClipboardEnum.TELEGRAM)) {
                        /*if (contact.getSocialModel() == null)
                            contact.setSocialModel(new SocialModel());
                        if (contact.hasLinked && contact.getSocialModel().getLinkedInLink() != null && !contact.getSocialModel().getLinkedInLink().isEmpty()) {*/
                        contact.listOfContactInfo.add(new ContactInfo("note", extrator.getClipboard().getValueCopy(), false, false, false, true, false));
                        /*} else {
                            contact.getSocialModel().setTelegramLink(extrator.getValueCopy());
                            contact.hasTelegram = true;
                        }*/
                    }

                    if (extrator.getClipboard().getType().equals(ClipboardEnum.GITHUB)) {
                        contact.addNote(extrator.getClipboard().getValueCopy());
                    }

                    if (extrator.getClipboard().getType().equals(ClipboardEnum.MEDIUM)) {
                        if (extrator.getClipboard().getValueCopy().contains("com/@")) {
                            if (contact.getSocialModel() == null)
                                contact.setSocialModel(new SocialModel());
                            if (contact.hasMedium && contact.getSocialModel().getMediumLink() != null && !contact.getSocialModel().getMediumLink().isEmpty()) {
                                contact.listOfContactInfo.add(new ContactInfo("note", extrator.getClipboard().getValueCopy(), false, false, false, true, false));
                            } else {
                                contact.getSocialModel().setMediumLink(extrator.getClipboard().getValueCopy());
                                contact.hasMedium = true;
                                if (extrator.getClipboard().getNameFromSocial() != null)
                                    contact.setName(extrator.getClipboard().getNameFromSocial());
                            }
                        } else {
                            contact.addNote(extrator.getClipboard().getValueCopy());
                        }
                    }

                    if (extrator.getClipboard().getType().equals(ClipboardEnum.PHONE)) {
                        contact.addPhone(extrator.getClipboard().getValueCopy());
                    }

                    if (extrator.getClipboard().getType().equals(ClipboardEnum.EMAIL)) {
                        contact.addEmail(extrator.getClipboard().getValueCopy());
                    }

                    if (extrator.getClipboard().getType().equals(ClipboardEnum.WEB)) {
                        contact.addNote(extrator.getClipboard().getValueCopy());
                    }

                    if (extrator.getClipboard().getType().equals(ClipboardEnum.NOTE)) {
                        contact.addNote(extrator.getClipboard().getValueCopy());
                    }

                    if (extrator.getClipboard().getType().equals(ClipboardEnum.ADDRESS)) {
                        contact.addAddress(extrator.getClipboard().getValueCopy());
                    }


                    if (extrator.getClipboard().getType().equals(ClipboardEnum.POSITION)) {
                        contact.setCompanyPossition(extrator.getClipboard().getValueCopy().trim());
                    }

                    if (extrator.getClipboard().getType().equals(ClipboardEnum.DATEOFBIRTH)) {
                        contact.addDateBirth(extrator.getClipboard().getValueCopy());
                    }

                    if (extrator.getClipboard().getType().equals(ClipboardEnum.COMPANY)) {
                        contact.setCompany(extrator.getClipboard().getValueCopy());
                    }

                    if (extrator.getClipboard().getType().equals(ClipboardEnum.HASHTAG)) {
                        String[] hashtags = extrator.getClipboard().getValueCopy().split(" ");
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
                        activityApp);
                alertDialogBuilder.setTitle("Create new contact?");
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Yes", (dialog, id) -> {

                            //contactsFragment.closeOtherPopup();


                            ContactsFragment.createContact = true;

                            ArrayList<Contact> contactExtract = new ArrayList<>();
                            contactExtract.add(contact);
                            //Contact contact1 = new Contact();
                            //contact1.setName("");
                            //contactExtract.add(contact1);
                            //contactExtract.add(contact1);

                            android.support.v4.app.FragmentManager fragmentManager = ((MainActivity) activityApp).getSupportFragmentManager();
                            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.add(R.id.main_content, CreateFragment.newInstance(contactExtract)).addToBackStack("create").commit();


                            //clipboardPopup.setVisibility(View.GONE);

                            /*ViewGroup.LayoutParams lp = clipboardPopup.findViewById(R.id.popupContainer).getLayoutParams();
                            float px = 240 * getApplicationContext().getResources().getDisplayMetrics().density;
                            if(lp.height > px){
                                lp.height = (int) px;
                                clipboardPopup.findViewById(R.id.popupContainer).setLayoutParams(lp);

                            }*/

                            //clibpboardAdapter.uncheck();
                            messageDataExtratorAdapter.uncheck();


                        })
                        .setNegativeButton("No", (dialog, id) -> dialog.cancel());

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
              /*  findViewById(R.id.extratorContainer).setVisibility(View.GONE);
                ((TextView) getActivity().findViewById(R.id.textExtractSocial)).setText("Extract data");
                getActivity().findViewById(R.id.framePopupSocial).setVisibility(View.GONE);
                getActivity().findViewById(R.id.text_reset_btn).callOnClick();*/
                //contactsFragment.hideSelectMenu();



















               /* android.support.v4.app.FragmentManager fragmentManager = ((FragmentActivity) getContext()).getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                ArrayList<Contact> l = new ArrayList<>();
                l.add(ProfileFragment.selectedContact);
                fragmentTransaction.add(R.id.main_content, CreateFragment.newInstance(l)).addToBackStack("contacts").commit();*/
            }
        });


        popupMessage.findViewById(R.id.shareMessageData).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<MessageData> listClip = messageDataExtratorAdapter.getSelectMessageData();
                String exportData = "";
                for (MessageData clip : listClip) {
                    if (clip.getClipboard() != null)
                        exportData += clip.getClipboard().getValueCopy() + "\n";
                }

                exportData += "\n\n";
                exportData += "Data shared via http://Extime.pro\n";


                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, exportData);
                startActivity(Intent.createChooser(shareIntent, "Поделиться контактом"));

            }
        });

        popupMessage.findViewById(R.id.copyMessageData).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<MessageData> listClip = messageDataExtratorAdapter.getSelectMessageData();
                String exportData = "";
                for (MessageData clip : listClip) {
                    if (clip.getClipboard() != null)
                        exportData += clip.getClipboard().getValueCopy() + " " + "\n";
                }

                /*if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
                    android.text.ClipboardManager clipboard = (android.text.ClipboardManager) mainView.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboard.setText(exportData);
                } else {*/
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) mainView.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", exportData);
                clipboard.setPrimaryClip(clip);
                //  }

                if (!exportData.isEmpty()) {
                    messageDataExtratorAdapter.uncheck();
                    Toast.makeText(mainView.getContext(), "Copied to clipboard", Toast.LENGTH_SHORT).show();
                }
            }
        });

        popupMessage.findViewById(R.id.downloadMessageData).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        ArrayList<MessageData> listClip = messageDataExtratorAdapter.getSelectMessageData();


                        for (MessageData clip : listClip) {

                            if (clip.getClipboard() == null) {
                                (activityApp).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText((activityApp), "Saving files...", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                break;
                            }
                        }

                        for (MessageData clip : listClip) {
                            if (clip.getClipboard() == null) {


                                try {

                                    HttpTransport transport = AndroidHttp.newCompatibleTransport();
                                    JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

                                    Gmail service = new Gmail.Builder(transport, jsonFactory, message.getCredential())
                                            .setApplicationName("Extime")
                                            .build();

                                    if (clip.getFilename() != null && !clip.getFilename().isEmpty()) {
                                        MessagePartBody attachPart = service.users().messages().attachments().
                                                get(message.getUser(), message.getId(), clip.getIdFile()).execute();


                                        clip.setFileByteArray(com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64.decodeBase64(attachPart.getData()));

                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                                File f = new File(Environment.getExternalStorageDirectory() + "/Extime/ExtimeFiles", clip.getFilename());
                                FileOutputStream fileOutFile = null;
                                try {
                                    fileOutFile = new FileOutputStream(f);
                                    fileOutFile.write(clip.getFileByteArray());
                                    fileOutFile.close();

                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }


                            }

                        }


                        for (MessageData clip : listClip) {
                            if (clip.getClipboard() == null) {
                                (activityApp).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText((activityApp), "Files saved", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                break;
                            }
                        }


                    }
                }).start();
            }
        });
    }


    public void hideMessageData() {
        popupMessage.findViewById(R.id.linearCountDataMEssage).setVisibility(View.VISIBLE);
        popupMessage.findViewById(R.id.linear_arg).setVisibility(View.VISIBLE);
        popupMessage.findViewById(R.id.linear_button_message).setVisibility(View.VISIBLE);

        popupMessage.findViewById(R.id.label_popup_message).setVisibility(View.VISIBLE);

        FrameLayout layoutParams = popupMessage.findViewById(R.id.frameTextMess);

        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) layoutParams.getLayoutParams();

        //

        //122.5

        lp.weight = 1;

        lp.height = 0;

        layoutParams.setLayoutParams(lp);

        //===========


        //popupMessage.findViewById(R.id.line_button).setVisibility(View.GONE);


        //===========

        FrameLayout.LayoutParams lp1 = (FrameLayout.LayoutParams) popupMessage.findViewById(R.id.popup_mess).getLayoutParams();

        float px = (float) (255 * mainView.getContext().getResources().getDisplayMetrics().density);

        float px3 = (float) (268 * mainView.getContext().getResources().getDisplayMetrics().density);

        lp1.height = (int) px;

        popupMessage.findViewById(R.id.popup_mess).setLayoutParams(lp1);

        FrameLayout.LayoutParams lp2 = (FrameLayout.LayoutParams) popupMessage.getLayoutParams();

        lp2.height = (int) px3;

        popupMessage.setLayoutParams(lp2);

        popupMessage.findViewById(R.id.test1).setVisibility(View.GONE);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            activityApp = (Activity) context;
        }
    }


    //===================================================================================


    public RecyclerView recyclerView_k, recyclerView_k_1, recyclerView_k_2, recyclerView_k_3, recyclerView_k_4, recyclerView_k_5, recyclerView_k_6, recyclerView_k_7;

    public KanbanRowTimelineFragment kanbanRowFragment_Inbox;
    public KanbanRowTimelineFragment kanbanRowFragment_Unread;
    public KanbanRowTimelineFragment kanbanRowFragment_Favourite;
    public KanbanRowTimelineFragment kanbanRowFragment_Sent;
    public KanbanRowTimelineFragment kanbanRowFragment_Important;
    public KanbanRowTimelineFragment kanbanRowFragment_Drafts;
    public KanbanRowTimelineFragment kanbanRowFragment_Spam;
    public KanbanRowTimelineFragment kanbanRowFragment_Trash;
    public KanbanRowFragment kanbanRowFragment_P;
    public KanbanRowFragment kanbanRowFragment_Fin;
    public KanbanRowFragment kanbanRowFragment_Crown;
    public KanbanRowFragment kanbanRowFragment_Vip;
    public KanbanRowFragment kanbanRowFragment_Inv;
    public KanbanRowFragment kanbanRowFragment_St;

    KanbanTimelineAdapter kanbanAdapter_1, kanbanAdapter_2, kanbanAdapter_3, kanbanAdapter_4, kanbanAdapter_5, kanbanAdapter_6, kanbanAdapter_7, kanbanAdapter_8;
    MessageAdapter kanbanMessageAdapter_1, kanbanMessageAdapter_2, kanbanMessageAdapter_3, kanbanMessageAdapter_4, kanbanMessageAdapter_5, kanbanMessageAdapter_6, kanbanMessageAdapter_7, kanbanMessageAdapter_8;
    public static ArrayList<EmailMessage> listInbox, listUnread, listfavourite, listSent, listImportant, listDrafts, listSpam, listTrash;

    //public SharedPreferences prefKanbanTouch;

    public int lastPosition = 0;
    public int lastPosition_top = 0;

    SmartTabLayout tabs, tabs_top;

    public void setKanbanMode(boolean updateF) {


        if (/*typeCard.equals(TypeCardTimeline.ROWS) || */typeCard.equals(TypeCardTimeline.BOXES)) {
            int width_medium = (int) (160 * getResources().getDisplayMetrics().density);
            int height_medium = (int) (40 * getResources().getDisplayMetrics().density);

            mainView.findViewById(R.id.head_card_1).setLayoutParams(new LinearLayout.LayoutParams(width_medium, height_medium));
            mainView.findViewById(R.id.head_card_2).setLayoutParams(new LinearLayout.LayoutParams(width_medium, height_medium));
            mainView.findViewById(R.id.head_card_3).setLayoutParams(new LinearLayout.LayoutParams(width_medium, height_medium));
            mainView.findViewById(R.id.head_card_4).setLayoutParams(new LinearLayout.LayoutParams(width_medium, height_medium));
            mainView.findViewById(R.id.head_card_5).setLayoutParams(new LinearLayout.LayoutParams(width_medium, height_medium));
            mainView.findViewById(R.id.head_card_6).setLayoutParams(new LinearLayout.LayoutParams(width_medium, height_medium));
            mainView.findViewById(R.id.head_card_7).setLayoutParams(new LinearLayout.LayoutParams(width_medium, height_medium));
            mainView.findViewById(R.id.head_card_8).setLayoutParams(new LinearLayout.LayoutParams(width_medium, height_medium));
        }


        listInbox = new ArrayList<>();
        listUnread = new ArrayList<>();

        /*  if(mode == 2)*/
        if (!updateF)
            listfavourite = new ArrayList<>();

        listSent = new ArrayList<>();
        listImportant = new ArrayList<>();
        listDrafts = new ArrayList<>();
        listSpam = new ArrayList<>();
        listTrash = new ArrayList<>();


        for (EmailMessage i : listMessages) {
            if (i.getListOfTypeMessage().contains("INBOX"))
                listInbox.add(i);
            if (i.getListOfTypeMessage().contains("UNREAD"))
                listUnread.add(i);
            if (i.getListOfTypeMessage().contains("STARRED") && !listfavourite.contains(i))
                listfavourite.add(i);
            if (i.getListOfTypeMessage().contains("SENT"))
                listSent.add(i);
            if (i.getListOfTypeMessage().contains("IMPORTANT"))
                listImportant.add(i);
            if (i.getListOfTypeMessage().contains("DRAFT"))
                listDrafts.add(i);
            if (i.getListOfTypeMessage().contains("SPAM"))
                listSpam.add(i);
            if (i.getListOfTypeMessage().contains("TRASH"))
                listTrash.add(i);
        }




       /* if (getActivity().findViewById(R.id.countSearchContacts).getVisibility() == View.VISIBLE) {
            ((TextView) getActivity().findViewById(R.id.countSearchContacts)).setText((listF.size() + listI.size() + listP.size() + listS.size() + listC.size() + listV.size() + listSt.size() + listInv.size()) + " matched entries");
        }*/

        ((TextView) mainView.findViewById(R.id.countType_1)).setText(String.valueOf(listInbox.size()));
        ((TextView) mainView.findViewById(R.id.countType_2)).setText(String.valueOf(listUnread.size()));
        ((TextView) mainView.findViewById(R.id.countType_3)).setText(String.valueOf(listfavourite.size()));
        ((TextView) mainView.findViewById(R.id.countType_4)).setText(String.valueOf(listSent.size()));
        ((TextView) mainView.findViewById(R.id.countType_5)).setText(String.valueOf(listImportant.size()));
        ((TextView) mainView.findViewById(R.id.countType_6)).setText(String.valueOf(listDrafts.size()));
        ((TextView) mainView.findViewById(R.id.countType_7)).setText(String.valueOf(listSpam.size()));
        ((TextView) mainView.findViewById(R.id.countType_8)).setText(String.valueOf(listTrash.size()));

        if (kanbanAdapter_1 == null || kanbanAdapter_2 == null || kanbanAdapter_3 == null || kanbanAdapter_4 == null || kanbanAdapter_5 == null || kanbanAdapter_6 == null || kanbanAdapter_7 == null || kanbanAdapter_8 == null
                || kanbanMessageAdapter_1 == null || kanbanMessageAdapter_2 == null || kanbanMessageAdapter_3 == null || kanbanMessageAdapter_4 == null || kanbanMessageAdapter_5 == null || kanbanMessageAdapter_6 == null
                || kanbanMessageAdapter_7 == null || kanbanMessageAdapter_8 == null) {


            if (recyclerView_k == null || recyclerView_k_1 == null || recyclerView_k_2 == null || recyclerView_k_3 == null || recyclerView_k_4 == null || recyclerView_k_5 == null || recyclerView_k_6 == null || recyclerView_k_7 == null) {
                recyclerView_k = mainView.findViewById(R.id.kanban_list);
                recyclerView_k_1 = mainView.findViewById(R.id.kanban_list_2);
                recyclerView_k_2 = mainView.findViewById(R.id.kanban_list_3);
                recyclerView_k_3 = mainView.findViewById(R.id.kanban_list_4);
                recyclerView_k_4 = mainView.findViewById(R.id.kanban_list_5);
                recyclerView_k_5 = mainView.findViewById(R.id.kanban_list_6);
                recyclerView_k_6 = mainView.findViewById(R.id.kanban_list_7);
                recyclerView_k_7 = mainView.findViewById(R.id.kanban_list_8);
            }

            if (typeCard.equals(TypeCardTimeline.BOXES)/* && (kanbanAdapter_1 == null || kanbanAdapter_2 == null || kanbanAdapter_3 == null)*/) {

                kanbanAdapter_1 = new KanbanTimelineAdapter(listInbox, mainView.getContext(), typeCard, activityApp, this, 1);
                kanbanAdapter_2 = new KanbanTimelineAdapter(listUnread, mainView.getContext(), typeCard, activityApp, this, 2);
                kanbanAdapter_3 = new KanbanTimelineAdapter(listfavourite, mainView.getContext(), typeCard, activityApp, this, 3);
                kanbanAdapter_4 = new KanbanTimelineAdapter(listSent, mainView.getContext(), typeCard, activityApp, this, 4);
                kanbanAdapter_5 = new KanbanTimelineAdapter(listImportant, mainView.getContext(), typeCard, activityApp, this, 5);
                kanbanAdapter_6 = new KanbanTimelineAdapter(listDrafts, mainView.getContext(), typeCard, activityApp, this, 6);
                kanbanAdapter_7 = new KanbanTimelineAdapter(listSpam, mainView.getContext(), typeCard, activityApp, this, 7);
                kanbanAdapter_8 = new KanbanTimelineAdapter(listTrash, mainView.getContext(), typeCard, activityApp, this, 8);

                recyclerView_k.setLayoutManager(new LinearLayoutManager(mainView.getContext()));
                recyclerView_k.setAdapter(kanbanAdapter_1);

                recyclerView_k_1.setLayoutManager(new LinearLayoutManager(mainView.getContext()));
                recyclerView_k_1.setAdapter(kanbanAdapter_2);

                recyclerView_k_2.setLayoutManager(new LinearLayoutManager(mainView.getContext()));
                recyclerView_k_2.setAdapter(kanbanAdapter_3);

                recyclerView_k_3.setLayoutManager(new LinearLayoutManager(mainView.getContext()));
                recyclerView_k_3.setAdapter(kanbanAdapter_4);

                recyclerView_k_4.setLayoutManager(new LinearLayoutManager(mainView.getContext()));
                recyclerView_k_4.setAdapter(kanbanAdapter_5);

                recyclerView_k_5.setLayoutManager(new LinearLayoutManager(mainView.getContext()));
                recyclerView_k_5.setAdapter(kanbanAdapter_6);

                recyclerView_k_6.setLayoutManager(new LinearLayoutManager(mainView.getContext()));
                recyclerView_k_6.setAdapter(kanbanAdapter_7);

                recyclerView_k_7.setLayoutManager(new LinearLayoutManager(mainView.getContext()));
                recyclerView_k_7.setAdapter(kanbanAdapter_8);

                recyclerView_k.setPadding(0, 0, 0, 0);
                recyclerView_k_1.setPadding(0, 0, 0, 0);
                recyclerView_k_2.setPadding(0, 0, 0, 0);
                recyclerView_k_3.setPadding(0, 0, 0, 0);
                recyclerView_k_4.setPadding(0, 0, 0, 0);
                recyclerView_k_5.setPadding(0, 0, 0, 0);
                recyclerView_k_6.setPadding(0, 0, 0, 0);
                recyclerView_k_7.setPadding(0, 0, 0, 0);

            } else /*if(typeCard.equals(TypeCardTimeline.ROWS) && (kanbanMessageAdapter_1 == null || kanbanMessageAdapter_2 == null || kanbanMessageAdapter_3 == null))*/ {

                kanbanMessageAdapter_1 = new MessageAdapter(activityApp, listInbox, this, 2);
                kanbanMessageAdapter_2 = new MessageAdapter(activityApp, listUnread, this, 2);
                kanbanMessageAdapter_3 = new MessageAdapter(activityApp, listfavourite, this, 2);
                kanbanMessageAdapter_4 = new MessageAdapter(activityApp, listSent, this, 2);
                kanbanMessageAdapter_5 = new MessageAdapter(activityApp, listImportant, this, 2);
                kanbanMessageAdapter_6 = new MessageAdapter(activityApp, listDrafts, this, 2);
                kanbanMessageAdapter_7 = new MessageAdapter(activityApp, listSpam, this, 2);
                kanbanMessageAdapter_8 = new MessageAdapter(activityApp, listTrash, this, 2);


                recyclerView_k.setLayoutManager(new LinearLayoutManager(mainView.getContext()));
                recyclerView_k.setAdapter(kanbanMessageAdapter_1);

                recyclerView_k_1.setLayoutManager(new LinearLayoutManager(mainView.getContext()));
                recyclerView_k_1.setAdapter(kanbanMessageAdapter_2);

                recyclerView_k_2.setLayoutManager(new LinearLayoutManager(mainView.getContext()));
                recyclerView_k_2.setAdapter(kanbanMessageAdapter_3);

                recyclerView_k_3.setLayoutManager(new LinearLayoutManager(mainView.getContext()));
                recyclerView_k_3.setAdapter(kanbanMessageAdapter_4);

                recyclerView_k_4.setLayoutManager(new LinearLayoutManager(mainView.getContext()));
                recyclerView_k_4.setAdapter(kanbanMessageAdapter_5);

                recyclerView_k_5.setLayoutManager(new LinearLayoutManager(mainView.getContext()));
                recyclerView_k_5.setAdapter(kanbanMessageAdapter_6);

                recyclerView_k_6.setLayoutManager(new LinearLayoutManager(mainView.getContext()));
                recyclerView_k_6.setAdapter(kanbanMessageAdapter_7);

                recyclerView_k_7.setLayoutManager(new LinearLayoutManager(mainView.getContext()));
                recyclerView_k_7.setAdapter(kanbanMessageAdapter_8);

                int px_L = (int) (14 * activityApp.getResources().getDisplayMetrics().density);
                int px_R = (int) (5 * activityApp.getResources().getDisplayMetrics().density);
                recyclerView_k.setPadding(px_L, 0, px_R, 0);
                recyclerView_k_1.setPadding(px_L, 0, px_R, 0);
                recyclerView_k_2.setPadding(px_L, 0, px_R, 0);
                recyclerView_k_3.setPadding(px_L, 0, px_R, 0);
                recyclerView_k_4.setPadding(px_L, 0, px_R, 0);
                recyclerView_k_5.setPadding(px_L, 0, px_R, 0);
                recyclerView_k_6.setPadding(px_L, 0, px_R, 0);
                recyclerView_k_7.setPadding(px_L, 0, px_R, 0);
            }




           /* recyclerView_k_3 = mainView.findViewById(R.id.kanban_list_4);
            recyclerView_k_4 = mainView.findViewById(R.id.kanban_list_5);
            recyclerView_k_5 = mainView.findViewById(R.id.kanban_list_6);
            recyclerView_k_6 = mainView.findViewById(R.id.kanban_list_7);
            recyclerView_k_7 = mainView.findViewById(R.id.kanban_list_8);*/



            /*recyclerView_k_3.setLayoutManager(new LinearLayoutManager(mainView.getContext()));
            recyclerView_k_3.setAdapter(kanbanAdapter_4);

            recyclerView_k_4.setLayoutManager(new LinearLayoutManager(mainView.getContext()));
            recyclerView_k_4.setAdapter(kanbanAdapter_5);

            recyclerView_k_5.setLayoutManager(new LinearLayoutManager(mainView.getContext()));
            recyclerView_k_5.setAdapter(kanbanAdapter_6);

            recyclerView_k_6.setLayoutManager(new LinearLayoutManager(mainView.getContext()));
            recyclerView_k_6.setAdapter(kanbanAdapter_7);

            recyclerView_k_7.setLayoutManager(new LinearLayoutManager(mainView.getContext()));
            recyclerView_k_7.setAdapter(kanbanAdapter_8);*/


          /*  int sortK1 = prefKanbanTouch.getInt("scrollKanban1", 0);
            int sortK2 = prefKanbanTouch.getInt("scrollKanban2", 0);
            int sortK3 = prefKanbanTouch.getInt("scrollKanban3", 0);
            int sortK4 = prefKanbanTouch.getInt("scrollKanban4", 0);
            int sortK5 = prefKanbanTouch.getInt("scrollKanban5", 0);
            int sortK6 = prefKanbanTouch.getInt("scrollKanban6", 0);
            int sortK7 = prefKanbanTouch.getInt("scrollKanban7", 0);
            int sortK8 = prefKanbanTouch.getInt("scrollKanban8", 0);*/

         /*   ((LinearLayoutManager) recyclerView_k.getLayoutManager()).scrollToPosition(sortK1);
            ((LinearLayoutManager) recyclerView_k_1.getLayoutManager()).scrollToPosition(sortK2);
            ((LinearLayoutManager) recyclerView_k_2.getLayoutManager()).scrollToPosition(sortK3);
            ((LinearLayoutManager) recyclerView_k_3.getLayoutManager()).scrollToPosition(sortK4);
            ((LinearLayoutManager) recyclerView_k_4.getLayoutManager()).scrollToPosition(sortK5);

            ((LinearLayoutManager) recyclerView_k_5.getLayoutManager()).scrollToPosition(sortK6);
            ((LinearLayoutManager) recyclerView_k_6.getLayoutManager()).scrollToPosition(sortK7);
            ((LinearLayoutManager) recyclerView_k_7.getLayoutManager()).scrollToPosition(sortK8);*/

        } else {

            if (typeCard.equals(TypeCardTimeline.BOXES)) {
                kanbanAdapter_1.updateList(listInbox);
                kanbanAdapter_2.updateList(listUnread);
                kanbanAdapter_3.updateList(listfavourite);
                kanbanAdapter_4.updateList(listSent);
                kanbanAdapter_5.updateList(listImportant);
                kanbanAdapter_6.updateList(listDrafts);
                kanbanAdapter_7.updateList(listSpam);
                kanbanAdapter_8.updateList(listTrash);


            } else {
                kanbanMessageAdapter_1.updateKanban(listInbox);
                kanbanMessageAdapter_2.updateKanban(listUnread);
                kanbanMessageAdapter_3.updateKanban(listfavourite);
                kanbanMessageAdapter_4.updateKanban(listSent);
                kanbanMessageAdapter_5.updateKanban(listImportant);
                kanbanMessageAdapter_6.updateKanban(listDrafts);
                kanbanMessageAdapter_7.updateKanban(listSpam);
                kanbanMessageAdapter_8.updateKanban(listTrash);

            }

        }


        //recyclerView_3.setHasFixedSize(true);


        //mainView.findViewById(R.id.kanban_list).setVisibility(View.VISIBLE);


        getActivity().findViewById(R.id.recyclerKankab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeOtherPopup();

                if (activityApp.findViewById(R.id.popup_menu_timeline).getVisibility() == View.VISIBLE) {
                    activityApp.findViewById(R.id.popup_menu_timeline).setVisibility(View.GONE);
                    return;
                }

                if (activityApp.findViewById(R.id.popup_menu_settings).getVisibility() == View.VISIBLE) {
                    activityApp.findViewById(R.id.popup_menu_settings).setVisibility(View.GONE);
                    return;
                }

                if (contactInboxPopup != null && contactInboxPopup.getVisibility() == View.VISIBLE) {
                    contactInboxPopup.setVisibility(View.GONE);
                    return;
                }

                if (editPopupTemplate != null && editPopupTemplate.getVisibility() == View.VISIBLE) {
                    editPopupTemplate.setVisibility(View.GONE);
                    return;
                }

                if (popupTemplate != null && popupTemplate.getVisibility() == View.VISIBLE) {
                    popupTemplate.setVisibility(View.GONE);
                    return;
                }

                if (inboxPopup != null && inboxPopup.getVisibility() == View.VISIBLE) {
                    mainView.findViewById(R.id.folders_Block).setBackgroundColor(0);
                    inboxPopup.setVisibility(View.GONE);
                    return;
                }

                if (popupContacts.getVisibility() == View.VISIBLE) {
                    popupContacts.setVisibility(View.GONE);
                    return;
                }


                if (popupMessage.getVisibility() == View.VISIBLE) {
                    popupMessage.setVisibility(View.GONE);
                    startSearchContact = false;
                    return;
                }
            }
        });





       /* if (getActivity().findViewById(R.id.countSearchContacts).getVisibility() == View.VISIBLE) {
            ((TextView) getActivity().findViewById(R.id.countSearchContacts)).setText((listF.size() + listI.size() + listP.size() + listS.size() + listC.size() + listV.size() + listSt.size() + listInv.size()) + " matched entries");
        }*/

        if (kanbanRowFragment_Inbox == null || kanbanRowFragment_Unread == null || kanbanRowFragment_Favourite == null || kanbanRowFragment_Sent == null || kanbanRowFragment_Important == null
                || kanbanRowFragment_Drafts == null || kanbanRowFragment_Spam == null || kanbanRowFragment_Trash == null/* || kanbanRowFragment_Fin == null || kanbanRowFragment_Imp == null
                || kanbanRowFragment_Inv == null || kanbanRowFragment_P == null || kanbanRowFragment_St == null || kanbanRowFragment_Vip == null || kanbanRowFragment_All.kanbanContactsAdapter == null
                || kanbanRowFragment_Crown.kanbanContactsAdapter == null || kanbanRowFragment_Fav.kanbanContactsAdapter == null || kanbanRowFragment_Fin.kanbanContactsAdapter == null ||
                kanbanRowFragment_Imp.kanbanContactsAdapter == null || kanbanRowFragment_Inv.kanbanContactsAdapter == null || kanbanRowFragment_P.kanbanContactsAdapter == null ||
                kanbanRowFragment_St.kanbanContactsAdapter == null || kanbanRowFragment_Vip.kanbanContactsAdapter == null*/) {


            kanbanRowFragment_Inbox = new KanbanRowTimelineFragment();
            Bundle args = new Bundle();
            args.putString("list", "Inbox");
            kanbanRowFragment_Inbox.setArguments(args);

            kanbanRowFragment_Unread = new KanbanRowTimelineFragment();
            Bundle args_F = new Bundle();
            args_F.putString("list", "Unread");
            kanbanRowFragment_Unread.setArguments(args_F);

            kanbanRowFragment_Favourite = new KanbanRowTimelineFragment();
            Bundle args_Imp = new Bundle();
            args_Imp.putString("list", "Favorite");
            kanbanRowFragment_Favourite.setArguments(args_Imp);

            kanbanRowFragment_Sent = new KanbanRowTimelineFragment();
            Bundle args_Sent = new Bundle();
            args_Sent.putString("list", "Sent");
            kanbanRowFragment_Sent.setArguments(args_Sent);

            kanbanRowFragment_Important = new KanbanRowTimelineFragment();
            Bundle args_Impo = new Bundle();
            args_Impo.putString("list", "Important");
            kanbanRowFragment_Important.setArguments(args_Impo);

            kanbanRowFragment_Drafts = new KanbanRowTimelineFragment();
            Bundle args_Dr = new Bundle();
            args_Dr.putString("list", "Drafts");
            kanbanRowFragment_Drafts.setArguments(args_Dr);

            kanbanRowFragment_Spam = new KanbanRowTimelineFragment();
            Bundle args_Sp = new Bundle();
            args_Sp.putString("list", "Spam");
            kanbanRowFragment_Spam.setArguments(args_Sp);

            kanbanRowFragment_Trash = new KanbanRowTimelineFragment();
            Bundle args_Tr = new Bundle();
            args_Tr.putString("list", "Trash");
            kanbanRowFragment_Trash.setArguments(args_Tr);


            ViewPager viewPagerRow = (ViewPager) mainView.findViewById(R.id.viewpager_kanban_t);
            ViewPager viewPagerRow_top = (ViewPager) mainView.findViewById(R.id.viewpager_kanban_t);

            TimelineSectionAdapter adapter, adapter_top;

            adapter = new TimelineSectionAdapter(getChildFragmentManager(), getContext(), TypeCardTimeline.EXTRA);
            adapter_top = new TimelineSectionAdapter(getChildFragmentManager(), getContext(), TypeCardTimeline.TOP);


            adapter.addFragment(kanbanRowFragment_Inbox, "Inbox", listInbox.size());
            adapter.addFragment(kanbanRowFragment_Unread, "Unread", listUnread.size());
            adapter.addFragment(kanbanRowFragment_Favourite, "Favorites", listfavourite.size());
            adapter.addFragment(kanbanRowFragment_Sent, "Sent", listSent.size());
            adapter.addFragment(kanbanRowFragment_Important, "Important", listImportant.size());
            adapter.addFragment(kanbanRowFragment_Drafts, "Drafts", listDrafts.size());
            adapter.addFragment(kanbanRowFragment_Spam, "Spam", listSpam.size());
            adapter.addFragment(kanbanRowFragment_Trash, "Trash", listTrash.size());

            adapter_top.addFragment(kanbanRowFragment_Inbox, "Inbox", listInbox.size());
            adapter_top.addFragment(kanbanRowFragment_Unread, "Unread", listUnread.size());
            adapter_top.addFragment(kanbanRowFragment_Favourite, "Favorites", listfavourite.size());
            adapter_top.addFragment(kanbanRowFragment_Sent, "Sent", listSent.size());
            adapter_top.addFragment(kanbanRowFragment_Important, "Important", listImportant.size());
            adapter_top.addFragment(kanbanRowFragment_Drafts, "Drafts", listDrafts.size());
            adapter_top.addFragment(kanbanRowFragment_Spam, "Spam", listSpam.size());
            adapter_top.addFragment(kanbanRowFragment_Trash, "Trash", listTrash.size());


            viewPagerRow.setAdapter(adapter);
            viewPagerRow_top.setAdapter(adapter_top);

            tabs = (SmartTabLayout) mainView.findViewById(R.id.result_tabs_kanban_t);

            tabs_top = (SmartTabLayout) mainView.findViewById(R.id.result_tabs_kanban_t_top);

            tabs.setCustomTabView(adapter);
            tabs.setViewPager(viewPagerRow);

            tabs_top.setCustomTabView(adapter_top);
            tabs_top.setViewPager(viewPagerRow_top);


            tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int i, float v, int i1) {

                }

                @Override
                public void onPageSelected(int i) {
                    ((TextView) tabs.getTabAt(i).findViewById(R.id.text_title_tab)).setTextColor(getContext().getResources().getColor(R.color.black));
                    ((TextView) tabs.getTabAt(lastPosition).findViewById(R.id.text_title_tab)).setTextColor(getContext().getResources().getColor(R.color.gray_dark));


                    lastPosition = i;
                }

                @Override
                public void onPageScrollStateChanged(int i) {

                }
            });

            tabs_top.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int i, float v, int i1) {

                }

                @Override
                public void onPageSelected(int i) {
                    ((TextView) tabs_top.getTabAt(i).findViewById(R.id.text_title_tab)).setTextColor(getContext().getResources().getColor(R.color.black));
                    ((TextView) tabs_top.getTabAt(lastPosition_top).findViewById(R.id.text_title_tab)).setTextColor(getContext().getResources().getColor(R.color.gray_dark));


                    lastPosition_top = i;
                }

                @Override
                public void onPageScrollStateChanged(int i) {

                }
            });


        } else {


            ((TextView) tabs.getTabAt(0).findViewById(R.id.text_count_tab)).setText(String.valueOf(listInbox.size()));
            ((TextView) tabs.getTabAt(1).findViewById(R.id.text_count_tab)).setText(String.valueOf(listUnread.size()));
            ((TextView) tabs.getTabAt(2).findViewById(R.id.text_count_tab)).setText(String.valueOf(listfavourite.size()));
            ((TextView) tabs.getTabAt(3).findViewById(R.id.text_count_tab)).setText(String.valueOf(listSent.size()));
            ((TextView) tabs.getTabAt(4).findViewById(R.id.text_count_tab)).setText(String.valueOf(listImportant.size()));
            ((TextView) tabs.getTabAt(5).findViewById(R.id.text_count_tab)).setText(String.valueOf(listDrafts.size()));
            ((TextView) tabs.getTabAt(6).findViewById(R.id.text_count_tab)).setText(String.valueOf(listSpam.size()));
            ((TextView) tabs.getTabAt(7).findViewById(R.id.text_count_tab)).setText(String.valueOf(listTrash.size()));

            ((TextView) tabs_top.getTabAt(0).findViewById(R.id.text_count_tab)).setText(String.valueOf(listInbox.size()));
            ((TextView) tabs_top.getTabAt(1).findViewById(R.id.text_count_tab)).setText(String.valueOf(listUnread.size()));
            ((TextView) tabs_top.getTabAt(2).findViewById(R.id.text_count_tab)).setText(String.valueOf(listfavourite.size()));
            ((TextView) tabs_top.getTabAt(3).findViewById(R.id.text_count_tab)).setText(String.valueOf(listSent.size()));
            ((TextView) tabs_top.getTabAt(4).findViewById(R.id.text_count_tab)).setText(String.valueOf(listImportant.size()));
            ((TextView) tabs_top.getTabAt(5).findViewById(R.id.text_count_tab)).setText(String.valueOf(listDrafts.size()));
            ((TextView) tabs_top.getTabAt(6).findViewById(R.id.text_count_tab)).setText(String.valueOf(listSpam.size()));
            ((TextView) tabs_top.getTabAt(7).findViewById(R.id.text_count_tab)).setText(String.valueOf(listTrash.size()));

            if (kanbanRowFragment_Inbox.messageAdapter == null) {
                Bundle args = new Bundle();
                args.putString("list", "Inbox");
                kanbanRowFragment_Inbox.setArguments(args);
            } else {
                kanbanRowFragment_Inbox.updateList(listInbox);
            }

            if (kanbanRowFragment_Unread.messageAdapter == null) {
                Bundle args_F = new Bundle();
                args_F.putString("list", "Unread");
                kanbanRowFragment_Unread.setArguments(args_F);
            } else {
                kanbanRowFragment_Unread.updateList(listUnread);
            }

            if (kanbanRowFragment_Favourite.messageAdapter == null) {
                Bundle args_Imp = new Bundle();
                args_Imp.putString("list", "Favorite");
                kanbanRowFragment_Favourite.setArguments(args_Imp);
            } else {
                kanbanRowFragment_Favourite.updateList(listfavourite);
            }

            if (kanbanRowFragment_Sent.messageAdapter == null) {
                Bundle args_Imp = new Bundle();
                args_Imp.putString("list", "Sent");
                kanbanRowFragment_Sent.setArguments(args_Imp);
            } else {
                kanbanRowFragment_Sent.updateList(listSent);
            }

            if (kanbanRowFragment_Important.messageAdapter == null) {
                Bundle args_Imp = new Bundle();
                args_Imp.putString("list", "Important");
                kanbanRowFragment_Important.setArguments(args_Imp);
            } else {
                kanbanRowFragment_Important.updateList(listImportant);
            }

            if (kanbanRowFragment_Drafts.messageAdapter == null) {
                Bundle args_Imp = new Bundle();
                args_Imp.putString("list", "Drafts");
                kanbanRowFragment_Drafts.setArguments(args_Imp);
            } else {
                kanbanRowFragment_Drafts.updateList(listDrafts);
            }

            if (kanbanRowFragment_Spam.messageAdapter == null) {
                Bundle args_Imp = new Bundle();
                args_Imp.putString("list", "Spam");
                kanbanRowFragment_Spam.setArguments(args_Imp);
            } else {
                kanbanRowFragment_Spam.updateList(listSpam);
            }

            if (kanbanRowFragment_Trash.messageAdapter == null) {
                Bundle args_Imp = new Bundle();
                args_Imp.putString("list", "Trash");
                kanbanRowFragment_Trash.setArguments(args_Imp);
            } else {
                kanbanRowFragment_Trash.updateList(listTrash);
            }

            //kanbanRowFragment_Inbox.messageAdapter.updateList(listAll);

            /*if(kanbanRowFragment_Fav.kanbanContactsAdapter != null) {
                kanbanRowFragment_Fav.kanbanContactsAdapter.updateList(listF);
            }else{
                Bundle args_F = new Bundle();
                args_F.putSerializable("list", listF);
                kanbanRowFragment_Fav.setArguments(args_F);
            }
            if(kanbanRowFragment_Imp.kanbanContactsAdapter != null) {
                kanbanRowFragment_Imp.kanbanContactsAdapter.updateList(listI);
            }else{
                Bundle args_F = new Bundle();
                args_F.putSerializable("list", listI);
                kanbanRowFragment_Imp.setArguments(args_F);
            }
            if(kanbanRowFragment_P.kanbanContactsAdapter != null) {
                kanbanRowFragment_P.kanbanContactsAdapter.updateList(listP);
            }else{

            }
            kanbanRowFragment_Fin.kanbanContactsAdapter.updateList(listS);
            kanbanRowFragment_Crown.kanbanContactsAdapter.updateList(listC);
            kanbanRowFragment_Vip.kanbanContactsAdapter.updateList(listV);
            kanbanRowFragment_Inv.kanbanContactsAdapter.updateList(listInv);
            kanbanRowFragment_St.kanbanContactsAdapter.updateList(listSt);*/

        }


        if (!typeCard.equals(TypeCardTimeline.EXTRA) && !typeCard.equals(TypeCardTimeline.TOP)) {
            mainView.findViewById(R.id.kanbanTabs).setVisibility(View.GONE);
            mainView.findViewById(R.id.timelineContainer).setVisibility(View.GONE);
            mainView.findViewById(R.id.scrollRec).setVisibility(View.VISIBLE);
        } else {
            mainView.findViewById(R.id.kanbanTabs).setVisibility(View.VISIBLE);
            mainView.findViewById(R.id.timelineContainer).setVisibility(View.GONE);
            mainView.findViewById(R.id.scrollRec).setVisibility(View.GONE);

            if (typeCard.equals(TypeCardTimeline.EXTRA)) {
                mainView.findViewById(R.id.result_tabs_kanban_t_top).setVisibility(View.GONE);
                mainView.findViewById(R.id.result_tabs_kanban_t).setVisibility(View.VISIBLE);
            } else {
                mainView.findViewById(R.id.result_tabs_kanban_t_top).setVisibility(View.VISIBLE);
                mainView.findViewById(R.id.result_tabs_kanban_t).setVisibility(View.GONE);
            }
        }

        mode = 2;
    }


    public ArrayList<EmailMessage> getListInbox() {

        return listInbox;
    }


    public ArrayList<EmailMessage> getListUnread() {

        return listUnread;
    }


    public ArrayList<EmailMessage> getListFavorites() {
        return listfavourite;
    }

    public void setListMode(boolean updateF) {


        mainView.findViewById(R.id.timelineContainer).setVisibility(View.VISIBLE);
        mainView.findViewById(R.id.kanbanTabs).setVisibility(View.GONE);
        mainView.findViewById(R.id.scrollRec).setVisibility(View.GONE);

        mode = 1;
    }

    public void changeType() {

        listInbox = new ArrayList<>();
        listUnread = new ArrayList<>();
        listfavourite = new ArrayList<>();
        listSent = new ArrayList<>();
        listImportant = new ArrayList<>();
        listDrafts = new ArrayList<>();
        listSpam = new ArrayList<>();
        listTrash = new ArrayList<>();


        for (EmailMessage i : listMessages) {
            if (i.getListOfTypeMessage().contains("INBOX"))
                listInbox.add(i);
            if (i.getListOfTypeMessage().contains("UNREAD"))
                listUnread.add(i);
            if (i.getListOfTypeMessage().contains("STARRED"))
                listfavourite.add(i);
            if (i.getListOfTypeMessage().contains("SENT"))
                listSent.add(i);
            if (i.getListOfTypeMessage().contains("IMPORTANT"))
                listImportant.add(i);
            if (i.getListOfTypeMessage().contains("DRAFT"))
                listDrafts.add(i);
            if (i.getListOfTypeMessage().contains("SPAM"))
                listSpam.add(i);
            if (i.getListOfTypeMessage().contains("TRASH"))
                listTrash.add(i);
        }

        if (recyclerView_k == null || recyclerView_k_1 == null || recyclerView_k_2 == null) {
            recyclerView_k = mainView.findViewById(R.id.kanban_list);
            recyclerView_k_1 = mainView.findViewById(R.id.kanban_list_2);
            recyclerView_k_2 = mainView.findViewById(R.id.kanban_list_3);
            recyclerView_k_3 = mainView.findViewById(R.id.kanban_list_4);
            recyclerView_k_4 = mainView.findViewById(R.id.kanban_list_5);
            recyclerView_k_5 = mainView.findViewById(R.id.kanban_list_6);
            recyclerView_k_6 = mainView.findViewById(R.id.kanban_list_7);
            recyclerView_k_7 = mainView.findViewById(R.id.kanban_list_8);


            recyclerView_k.setLayoutManager(new LinearLayoutManager(mainView.getContext()));
            recyclerView_k_1.setLayoutManager(new LinearLayoutManager(mainView.getContext()));
            recyclerView_k_2.setLayoutManager(new LinearLayoutManager(mainView.getContext()));
            recyclerView_k_3.setLayoutManager(new LinearLayoutManager(mainView.getContext()));
            recyclerView_k_4.setLayoutManager(new LinearLayoutManager(mainView.getContext()));
            recyclerView_k_5.setLayoutManager(new LinearLayoutManager(mainView.getContext()));
            recyclerView_k_6.setLayoutManager(new LinearLayoutManager(mainView.getContext()));
            recyclerView_k_7.setLayoutManager(new LinearLayoutManager(mainView.getContext()));
        }


        if (typeCard.equals(TypeCardTimeline.BOXES)) {
            if (kanbanAdapter_1 != null && kanbanAdapter_2 != null && kanbanAdapter_3 != null && kanbanAdapter_4 != null && kanbanAdapter_5 != null && kanbanAdapter_6 != null && kanbanAdapter_7 != null && kanbanAdapter_8 != null) {
                kanbanAdapter_1.updateList(listInbox);
                kanbanAdapter_2.updateList(listUnread);
                kanbanAdapter_3.updateList(listfavourite);
                kanbanAdapter_4.updateList(listSent);
                kanbanAdapter_5.updateList(listImportant);
                kanbanAdapter_6.updateList(listDrafts);
                kanbanAdapter_7.updateList(listSpam);
                kanbanAdapter_8.updateList(listTrash);

            } else {
                kanbanAdapter_1 = new KanbanTimelineAdapter(listInbox, mainView.getContext(), typeCard, activityApp, this, 1);
                kanbanAdapter_2 = new KanbanTimelineAdapter(listUnread, mainView.getContext(), typeCard, activityApp, this, 2);
                kanbanAdapter_3 = new KanbanTimelineAdapter(listfavourite, mainView.getContext(), typeCard, activityApp, this, 3);
                kanbanAdapter_4 = new KanbanTimelineAdapter(listSent, mainView.getContext(), typeCard, activityApp, this, 4);
                kanbanAdapter_5 = new KanbanTimelineAdapter(listImportant, mainView.getContext(), typeCard, activityApp, this, 5);
                kanbanAdapter_6 = new KanbanTimelineAdapter(listDrafts, mainView.getContext(), typeCard, activityApp, this, 6);
                kanbanAdapter_7 = new KanbanTimelineAdapter(listSpam, mainView.getContext(), typeCard, activityApp, this, 7);
                kanbanAdapter_8 = new KanbanTimelineAdapter(listTrash, mainView.getContext(), typeCard, activityApp, this, 8);
            }


            //recyclerView_k.setLayoutManager(new LinearLayoutManager(mainView.getContext()));


            recyclerView_k.setAdapter(kanbanAdapter_1);

            //recyclerView_k_1.setLayoutManager(new LinearLayoutManager(mainView.getContext()));
            recyclerView_k_1.setAdapter(kanbanAdapter_2);

            //recyclerView_k_2.setLayoutManager(new LinearLayoutManager(mainView.getContext()));
            recyclerView_k_2.setAdapter(kanbanAdapter_3);

            recyclerView_k_3.setAdapter(kanbanAdapter_4);
            recyclerView_k_4.setAdapter(kanbanAdapter_5);
            recyclerView_k_5.setAdapter(kanbanAdapter_6);
            recyclerView_k_6.setAdapter(kanbanAdapter_7);
            recyclerView_k_7.setAdapter(kanbanAdapter_8);

            recyclerView_k.setPadding(0, 0, 0, 0);
            recyclerView_k_1.setPadding(0, 0, 0, 0);
            recyclerView_k_2.setPadding(0, 0, 0, 0);
            recyclerView_k_3.setPadding(0, 0, 0, 0);
            recyclerView_k_4.setPadding(0, 0, 0, 0);
            recyclerView_k_5.setPadding(0, 0, 0, 0);
            recyclerView_k_6.setPadding(0, 0, 0, 0);
            recyclerView_k_7.setPadding(0, 0, 0, 0);

            if (mode == 2) {
                mainView.findViewById(R.id.scrollRec).setVisibility(View.VISIBLE);
                mainView.findViewById(R.id.kanbanTabs).setVisibility(View.GONE);
            }
        } else if (typeCard.equals(TypeCardTimeline.ROWS)) {
            if (kanbanMessageAdapter_1 != null && kanbanMessageAdapter_2 != null && kanbanMessageAdapter_3 != null && kanbanMessageAdapter_4 != null && kanbanMessageAdapter_5 != null && kanbanMessageAdapter_6 != null
                    && kanbanMessageAdapter_7 != null && kanbanMessageAdapter_8 != null) {
                kanbanMessageAdapter_1.updateKanban(listInbox);
                kanbanMessageAdapter_2.updateKanban(listUnread);
                kanbanMessageAdapter_3.updateKanban(listfavourite);
                kanbanMessageAdapter_4.updateKanban(listSent);
                kanbanMessageAdapter_5.updateKanban(listImportant);
                kanbanMessageAdapter_6.updateKanban(listDrafts);
                kanbanMessageAdapter_7.updateKanban(listSpam);
                kanbanMessageAdapter_8.updateKanban(listTrash);


            } else {
                kanbanMessageAdapter_1 = new MessageAdapter(activityApp, listInbox, this, 2);
                kanbanMessageAdapter_2 = new MessageAdapter(activityApp, listUnread, this, 2);
                kanbanMessageAdapter_3 = new MessageAdapter(activityApp, listfavourite, this, 2);
                kanbanMessageAdapter_4 = new MessageAdapter(activityApp, listSent, this, 2);
                kanbanMessageAdapter_5 = new MessageAdapter(activityApp, listImportant, this, 2);
                kanbanMessageAdapter_6 = new MessageAdapter(activityApp, listDrafts, this, 2);
                kanbanMessageAdapter_7 = new MessageAdapter(activityApp, listSpam, this, 2);
                kanbanMessageAdapter_8 = new MessageAdapter(activityApp, listTrash, this, 2);

            }

           /* recyclerView_k = mainView.findViewById(R.id.kanban_list);
            recyclerView_k_1 = mainView.findViewById(R.id.kanban_list_2);
            recyclerView_k_2 = mainView.findViewById(R.id.kanban_list_3);*/

            //recyclerView_k.setLayoutManager(new LinearLayoutManager(mainView.getContext()));
            recyclerView_k.setAdapter(kanbanMessageAdapter_1);

            //recyclerView_k_1.setLayoutManager(new LinearLayoutManager(mainView.getContext()));
            recyclerView_k_1.setAdapter(kanbanMessageAdapter_2);

            //recyclerView_k_2.setLayoutManager(new LinearLayoutManager(mainView.getContext()));
            recyclerView_k_2.setAdapter(kanbanMessageAdapter_3);

            recyclerView_k_3.setAdapter(kanbanMessageAdapter_4);
            recyclerView_k_4.setAdapter(kanbanMessageAdapter_5);
            recyclerView_k_5.setAdapter(kanbanMessageAdapter_6);
            recyclerView_k_6.setAdapter(kanbanMessageAdapter_7);
            recyclerView_k_7.setAdapter(kanbanMessageAdapter_8);

            int px_L = (int) (14 * activityApp.getResources().getDisplayMetrics().density);
            int px_R = (int) (5 * activityApp.getResources().getDisplayMetrics().density);
            recyclerView_k.setPadding(px_L, 0, px_R, 0);
            recyclerView_k_1.setPadding(px_L, 0, px_R, 0);
            recyclerView_k_2.setPadding(px_L, 0, px_R, 0);

            recyclerView_k_3.setPadding(px_L, 0, px_R, 0);
            recyclerView_k_4.setPadding(px_L, 0, px_R, 0);
            recyclerView_k_5.setPadding(px_L, 0, px_R, 0);
            recyclerView_k_6.setPadding(px_L, 0, px_R, 0);
            recyclerView_k_7.setPadding(px_L, 0, px_R, 0);

            if (mode == 2) {
                mainView.findViewById(R.id.scrollRec).setVisibility(View.VISIBLE);
                mainView.findViewById(R.id.kanbanTabs).setVisibility(View.GONE);
            }
        } else {

            if (mode == 2) {

                if (TimeLineFragment.typeCard.equals(TypeCardTimeline.EXTRA)) {
                    mainView.findViewById(R.id.result_tabs_kanban_t_top).setVisibility(View.GONE);
                    mainView.findViewById(R.id.result_tabs_kanban_t).setVisibility(View.VISIBLE);
                } else {
                    mainView.findViewById(R.id.result_tabs_kanban_t_top).setVisibility(View.VISIBLE);
                    mainView.findViewById(R.id.result_tabs_kanban_t).setVisibility(View.GONE);
                }

                mainView.findViewById(R.id.scrollRec).setVisibility(View.GONE);
                mainView.findViewById(R.id.kanbanTabs).setVisibility(View.VISIBLE);

                mainView.findViewById(R.id.kanbanTabs).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupMessage.setVisibility(View.GONE);
                    }
                });
            }

        }
    }
}
