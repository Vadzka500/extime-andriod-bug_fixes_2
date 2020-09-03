package ai.extime.Fragments;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.arch.lifecycle.Lifecycle;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.UserManager;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.util.Linkify;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
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
import ai.extime.Adapters.ContactTimelineAdapter;
import ai.extime.Adapters.ContactsOfMessageAdapter;
import ai.extime.Adapters.MessageAdapter;
import ai.extime.Adapters.MessageDataAdapter;
import ai.extime.Adapters.MessageDataExtratorAdapter;
import ai.extime.Adapters.TemplateFileAdapter;
import ai.extime.Enums.ClipboardEnum;
import ai.extime.Enums.FileEnums;
import ai.extime.Events.UpdateMessageAdapter;
import ai.extime.Interfaces.IMessage;
import ai.extime.Models.Clipboard;
import ai.extime.Models.Contact;
import ai.extime.Models.ContactInfo;
import ai.extime.Models.ContactOfMessage;
import ai.extime.Models.EmailMessage;
import ai.extime.Models.FilesTemplate;
import ai.extime.Models.HashTag;
import ai.extime.Models.MessageData;
import ai.extime.Models.SocialModel;
import ai.extime.Models.Template;
import ai.extime.Services.ContactCacheService;
import ai.extime.Utils.ClipboardType;
import ai.extime.Utils.FileType;
import ai.extime.Utils.ShareTemplatesMessageReply;
import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmList;

import com.extime.R;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.util.IOUtils;
import com.google.android.gms.tasks.Task;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.util.Utils;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Base64;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.client.util.StringUtils;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.ListLabelsResponse;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartBody;
import com.google.api.services.gmail.model.ModifyMessageRequest;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.w3c.dom.Text;

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

import static android.app.Activity.RESULT_OK;

public class ContactTimelineDataFragment extends Fragment implements IMessage {

    private View mainView;

    View rootView;

    public ArrayList<Contact> selectedContact;

    RecyclerView listOfProfileDataRV;

    ContactTimelineAdapter contactTimelineAdapter;

    ArrayList<ContactInfo> listOfContactInfo;

    GoogleSignInClient mGoogleSignInClient;

    GoogleAccountCredential mCredential;

    ArrayList<GoogleAccountCredential> listCredential;

    ArrayList<EmailMessage> listMessages;

    static ArrayList<String> email_contact;

    FileType fileType;

    MessageAdapter messageAdapter;

    AccountAdapter accountAdapter;

    RecyclerView recyclerAccount;

    RecyclerView recyclerAcc2;

    LinearLayout popupAccounts;

    FrameLayout popupMessage;

    FrameLayout popupContacts;

    String value = "Extime";


    private static final String APPLICATION_NAME = "Extime";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    public static final int REQUEST_AUTHORIZATION = 8;

    //private static final List<String> SCOPES = Collections.singletonList(GmailScopes.GMAIL_LABELS);


    private static final String[] SCOPES = {
            GmailScopes.GMAIL_LABELS,
            GmailScopes.GMAIL_READONLY,
            GmailScopes.GMAIL_MODIFY
    };

    //private static final List<String> SCOPES = Collections.singletonList(GmailScopes.GMAIL_LABELS);

    private static final String CREDENTIALS_FILE_PATH = "credentials.json";

    /*private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = getContext().getResources().openRawResource(R.raw.credentials);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.

        File tokenFolder = new File(Environment.getExternalStorageDirectory() +
                File.separator + TOKENS_DIRECTORY_PATH);
        if (!tokenFolder.exists()) {
            tokenFolder.mkdirs();
        }

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(tokenFolder))
                .setAccessType("offline")
                .build();


        final LocalServerReceiver[] receiver = {null};
        final Credential[] aa = new Credential[1];
        new Thread(new Runnable() {
            @Override
            public void run() {
                receiver[0] = new LocalServerReceiver.Builder().setPort(8888).build();

                try {
                     aa[0] = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

            while(aa[0] == null) {
                //return new AuthorizationCodeInstalledApp(flow, receiver[0]).authorize("user");
            }

            return aa[0];

    }*/

    private void getDataFromBundle() {
        Bundle args = getArguments();

        try {
            selectedContact = (ArrayList<Contact>) args.getSerializable("selectedContact");
        } catch (Exception e) {
            selectedContact.add((Contact) args.getSerializable("selectedContact"));
        }

        if (selectedContact == null || selectedContact.isEmpty()) {
            Contact createdContact = new Contact(new Date());
            createdContact.setCompany(null);
            createdContact.setCompanyPossition(null);

            selectedContact = new ArrayList<Contact>();
            selectedContact.add(createdContact);

        }

    }

    public void checkEmail() {
        email_contact = new ArrayList<>();
        int count_Emails = 0;
        if (selectedContact != null && selectedContact.get(0) != null && selectedContact.get(0).getListOfContactInfo() != null && !selectedContact.get(0).getListOfContactInfo().isEmpty()) {

            mainView.findViewById(R.id.frameGoGmail).setVisibility(View.GONE);

            for (ContactInfo i : selectedContact.get(0).getListOfContactInfo()) {
                if (ClipboardType.isEmail(i.value)) {
                    email_contact.add(i.value);
                    count_Emails++;
                }
            }

        }
        if (count_Emails == 1) {
            SharedPreferences mSettings;
            mSettings = getActivity().getSharedPreferences("accountUser", Context.MODE_PRIVATE);
            Set<String> set = mSettings.getStringSet("accounts", null);

            if (set == null || set.isEmpty()) {
                mainView.findViewById(R.id.frameGoGmail).setVisibility(View.VISIBLE);
            }
        }
    }

    private void initViews() {

        try {
            popupMessage = ((ProfileFragment) getParentFragment()).mainView.findViewById(R.id.popupMessage);
            mainView.setVisibility(View.VISIBLE);
        } catch (Exception e) {

            mainView.setVisibility(View.INVISIBLE);

            return;
        }

        try {
            popupContacts = ((ProfileFragment) getParentFragment()).mainView.findViewById(R.id.popupMessageContact);
            mainView.setVisibility(View.VISIBLE);
        } catch (Exception e) {

            mainView.setVisibility(View.INVISIBLE);

            return;
        }


        ((TextView) popupMessage.findViewById(R.id.textMessage)).setMovementMethod(new ScrollingMovementMethod());

        mainView.findViewById(R.id.frameChooseAccounts).setVisibility(View.GONE);
        mainView.findViewById(R.id.frameGetMessage).setVisibility(View.GONE);
        mainView.findViewById(R.id.frame_google_count).setVisibility(View.GONE);

        popupAccounts = getActivity().findViewById(R.id.accountContainer);

        String[] selectionArgs = new String[]{String.valueOf("com.google"), String.valueOf(selectedContact.get(0).getIdContact())};
        ContentResolver cr = getActivity().getContentResolver();

        Cursor c = cr.query(ContactsContract.RawContacts.CONTENT_URI, new String[]{ContactsContract.RawContacts.ACCOUNT_NAME}, ContactsContract.RawContacts.ACCOUNT_TYPE + " = ? AND " + ContactsContract.RawContacts.CONTACT_ID + " = ?", selectionArgs, null);

        if (c != null && c.moveToFirst()) {
            //while (c.moveToFirst()) {
            c.moveToFirst();
            ((TextView) mainView.findViewById(R.id.value1)).setText(c.getString(c.getColumnIndex(ContactsContract.RawContacts.ACCOUNT_NAME)));
            value = c.getString(c.getColumnIndex(ContactsContract.RawContacts.ACCOUNT_NAME));
            ((ImageView) mainView.findViewById(R.id.type_image)).setImageDrawable(getResources().getDrawable(R.drawable.icn_fab_google));
            //break;
            //}
        } else {
            ((TextView) mainView.findViewById(R.id.value1)).setText("Extime");
            value = "Extime";
            ((ImageView) mainView.findViewById(R.id.type_image)).setImageDrawable(getResources().getDrawable(R.drawable.new_icon));
        }


        mainView.findViewById(R.id.frameGoGmail).setVisibility(View.GONE);

        SharedPreferences mSettings;
        mSettings = getActivity().getSharedPreferences("accountUser", Context.MODE_PRIVATE);
        Set<String> set2 = mSettings.getStringSet("accounts", null);

        email_contact = new ArrayList<>();
        if (set2 == null || set2.isEmpty()) {
            if (selectedContact != null && selectedContact.get(0) != null && selectedContact.get(0).getListOfContactInfo() != null && !selectedContact.get(0).getListOfContactInfo().isEmpty() && (selectedContact.get(0).listOfContacts == null || selectedContact.get(0).listOfContacts.isEmpty())) {

                for (ContactInfo i : selectedContact.get(0).getListOfContactInfo()) {
                    if (ClipboardType.isEmail(i.value)) {
                        email_contact.add(i.value);
                        mainView.findViewById(R.id.frameGoGmail).setVisibility(View.VISIBLE);
                    }
                }

            }else if(selectedContact != null && selectedContact.get(0) != null  && selectedContact.get(0).listOfContacts != null || !selectedContact.get(0).listOfContacts.isEmpty()){
                if(selectedContact.get(0).getListOfContactInfo() != null && !selectedContact.get(0).getListOfContactInfo().isEmpty()){
                    for (ContactInfo i : selectedContact.get(0).getListOfContactInfo()) {
                        if (ClipboardType.isEmail(i.value) /*&& !email_contact.contains(i.value)*/) {

                            boolean check_E = false;
                            for(String s : email_contact){
                                if(s.toLowerCase().equals(i.value.toLowerCase())){
                                    check_E = true;
                                    break;
                                }
                            }
                            if(check_E) continue;

                            email_contact.add(i.value);
                            mainView.findViewById(R.id.frameGoGmail).setVisibility(View.VISIBLE);
                        }
                    }
                }

                for(Contact cont : selectedContact.get(0).listOfContacts){
                    if(cont.getListOfContactInfo() != null && !cont.getListOfContactInfo().isEmpty()){
                        for (ContactInfo i : cont.getListOfContactInfo()) {
                            if (ClipboardType.isEmail(i.value) /*&& !email_contact.contains(i.value)*/) {

                                boolean check_E = false;
                                for(String s : email_contact){
                                    if(s.toLowerCase().equals(i.value.toLowerCase())){
                                        check_E = true;
                                        break;
                                    }
                                }
                                if(check_E) continue;

                                email_contact.add(i.value);
                                mainView.findViewById(R.id.frameGoGmail).setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }

            }
        } else {

            if (selectedContact != null && selectedContact.get(0) != null && selectedContact.get(0).getListOfContactInfo() != null && !selectedContact.get(0).getListOfContactInfo().isEmpty() && (selectedContact.get(0).listOfContacts == null || selectedContact.get(0).listOfContacts.isEmpty())) {

                for (ContactInfo i : selectedContact.get(0).getListOfContactInfo()) {
                    if (ClipboardType.isEmail(i.value)) {
                        email_contact.add(i.value);
                    }
                }

            }else if(selectedContact != null && selectedContact.get(0) != null  && selectedContact.get(0).listOfContacts != null && !selectedContact.get(0).listOfContacts.isEmpty()){
                if(selectedContact.get(0).getListOfContactInfo() != null && !selectedContact.get(0).getListOfContactInfo().isEmpty()){
                    for (ContactInfo i : selectedContact.get(0).getListOfContactInfo()) {
                        if (ClipboardType.isEmail(i.value) /*&& !email_contact.contains(i.value)*/) {

                            boolean check_E = false;
                            for(String s : email_contact){
                                if(s.toLowerCase().equals(i.value.toLowerCase())){
                                    check_E = true;
                                    break;
                                }
                            }
                            if(check_E) continue;

                            email_contact.add(i.value);
                        }
                    }
                }

                for(Contact cont : selectedContact.get(0).listOfContacts){
                    if(cont.getListOfContactInfo() != null && !cont.getListOfContactInfo().isEmpty()){
                        for (ContactInfo i : cont.getListOfContactInfo()) {
                            if (ClipboardType.isEmail(i.value) /*&& !email_contact.contains(i.value)*/) {

                                boolean check_E = false;
                                for(String s : email_contact){
                                    if(s.toLowerCase().equals(i.value.toLowerCase())){
                                        check_E = true;
                                        break;
                                    }
                                }
                                if(check_E) continue;

                                email_contact.add(i.value);
                            }
                        }
                    }
                }

            }



            mainView.findViewById(R.id.frame_google_count).setVisibility(View.VISIBLE);
            ((TextView) mainView.findViewById(R.id.text_count_google_account)).setText(String.valueOf(set2.size()));

            recyclerAccount = mainView.findViewById(R.id.listAccounts);
            recyclerAcc2 = getActivity().findViewById(R.id.recycler_account);


            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mainView.getContext());

            RecyclerView.LayoutManager mLayoutManager2 = new LinearLayoutManager(mainView.getContext());
            recyclerAcc2.setLayoutManager(mLayoutManager);
            recyclerAccount.setLayoutManager(mLayoutManager2);

            mainView.findViewById(R.id.frame_google_count).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mainView.findViewById(R.id.frameGetMessage).getVisibility() == View.GONE) {

                        //==

                        popupAccounts.setVisibility(View.VISIBLE);

                        ((CheckBox) mainView.findViewById(R.id.all_select)).setChecked(false);

                        FloatingActionMenu floatingActionMenu = (FloatingActionMenu) getActivity().findViewById(R.id.fabMenuContainer);

                        //getActivity().findViewById(R.id.fab).setVisibility(View.GONE);

                        floatingActionMenu.close(false);
                        floatingActionMenu.setVisibility(View.GONE);


                       /* recyclerAcc2 = getActivity().findViewById(R.id.recycler_account);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mainView.getContext());
                        recyclerAcc2.setLayoutManager(mLayoutManager);*/

                        Account[] accounts = AccountManager.get(getActivity()).getAccountsByType("com.google");


                        SharedPreferences mSettings;
                        mSettings = getActivity().getSharedPreferences("accountUser", Context.MODE_PRIVATE);
                        Set<String> set = mSettings.getStringSet("accounts", null);

                        ArrayList<Boolean> listCheck = new ArrayList<>();
                        ArrayList<Account> list = new ArrayList<>();
                        for (Account account : accounts) {
                            list.add(account);
                            if (set.contains(account.name)) {
                                listCheck.add(true);

                            } else {
                                listCheck.add(false);

                            }
                        }



                        if (set.size() == accounts.length) {
                            ((CheckBox) mainView.findViewById(R.id.all_select)).setChecked(true);
                        }

                        if (set.size() > 0) {
                            ((TextView) popupAccounts.findViewById(R.id.apply_account)).setTextColor(getContext().getResources().getColor(R.color.primary));
                        }

                        if (set == null || set.isEmpty()) {
                            ((TextView) popupAccounts.findViewById(R.id.count_account)).setText("0");
                        } else
                            ((TextView) popupAccounts.findViewById(R.id.count_account)).setText(String.valueOf(set.size()));

                      //  if (accountAdapter == null) {

                            TextView textView =  ((TextView)popupAccounts.findViewById(R.id.count_account));

                            CheckBox checkBox = popupAccounts.findViewById(R.id.all_accounts_select);

                            accountAdapter = new AccountAdapter(list, getContext(), popupAccounts.findViewById(R.id.all_account), popupAccounts.findViewById(R.id.apply_account), getActivity().findViewById(R.id.count_acc), listCheck, textView, checkBox);

                       // }

                        recyclerAcc2.setAdapter(accountAdapter);




                        ((CheckBox) popupAccounts.findViewById(R.id.all_account)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (((CheckBox) popupAccounts.findViewById(R.id.all_account)).isChecked()) {
                                    accountAdapter.updateAllSelect(true);
                                    ((TextView) popupAccounts.findViewById(R.id.apply_account)).setTextColor(getContext().getResources().getColor(R.color.primary));
                                } else {
                                    accountAdapter.updateAllSelect(false);
                                    ((TextView) popupAccounts.findViewById(R.id.apply_account)).setTextColor(getContext().getResources().getColor(R.color.gray));
                                }
                            }
                        });

                        popupAccounts.findViewById(R.id.cancel_account).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                popupAccounts.setVisibility(View.GONE);
                                if (mainView.findViewById(R.id.frameChooseAccounts).getVisibility() == View.GONE)
                                    mainView.findViewById(R.id.frameGoGmail).setVisibility(View.VISIBLE);
                            }
                        });


                        popupAccounts.findViewById(R.id.apply_account).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (getContext() != null) {
                                    if (!MainActivity.hasConnection(getContext())) {
                                        Toast.makeText(getContext(), "Check your internet connection", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                } else {
                                    try {
                                        if (!MainActivity.hasConnection(((ProfileFragment) getParentFragment()).activityApp)) {
                                            Toast.makeText(getContext(), "Check your internet connection", Toast.LENGTH_SHORT).show();
                                            return;
                                        }

                                    } catch (Exception e) {

                                    }
                                }
                                if (accountAdapter != null && accountAdapter.getListSelectId() != null && accountAdapter.getListSelectId().size() > 0) {

                                    mainView.findViewById(R.id.countAccount).setVisibility(View.GONE);
                                    mainView.findViewById(R.id.frame_choose_google_account).setVisibility(View.GONE);
                                    mainView.findViewById(R.id.listAccounts).setVisibility(View.GONE);
                                    mainView.findViewById(R.id.frame_button_google_message).setVisibility(View.GONE);
                                    mainView.findViewById(R.id.spaceMessage).setVisibility(View.GONE);

                                    popupAccounts.setVisibility(View.GONE);

                                    mainView.findViewById(R.id.frame_google_count).setVisibility(View.VISIBLE);
                                    ((TextView) mainView.findViewById(R.id.text_count_google_account)).setText(String.valueOf(accountAdapter.getListSelectId().size()));


                                    if (accountAdapter.getListSelectId().size() == 1)
                                        Toast.makeText(getContext(), "1 gmail account selected", Toast.LENGTH_SHORT).show();
                                    else if (accountAdapter.getListSelectId().size() > 1)
                                        Toast.makeText(getContext(), accountAdapter.getListSelectId().size() + " gmail accounts selected", Toast.LENGTH_SHORT).show();

                                    FloatingActionMenu floatingActionMenu = (FloatingActionMenu) getActivity().findViewById(R.id.fabMenuContainer);
                                    floatingActionMenu.setVisibility(View.GONE);

                                    mainView.findViewById(R.id.frameGetMessage).setVisibility(View.VISIBLE);


                                    listCredential = new ArrayList<>();

                                    for (int i = 0; i < accountAdapter.getListSelectId().size(); i++) {

                                        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(
                                                getActivity().getApplicationContext(), Arrays.asList(SCOPES))
                                                .setBackOff(new ExponentialBackOff());

                                        credential.setSelectedAccountName(accountAdapter.getListSelectId().get(i).name);

                                        listCredential.add(credential);

                                    }



                                    getMessages();


                                    //int px = (int) (54 * getContext().getResources().getDisplayMetrics().density);

                                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mainView.findViewById(R.id.frameChooseAccounts).getLayoutParams();
                                    params.topMargin = 0;

                                    Set<String> set = new HashSet<>();
                                    for (int i = 0; i < accountAdapter.getListSelectId().size(); i++) {
                                        set.add(accountAdapter.getListSelectId().get(i).name);
                                    }

                                    SharedPreferences mSettings;
                                    mSettings = getActivity().getSharedPreferences("accountUser", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = mSettings.edit();
                                    editor.putStringSet("accounts", set);
                                    editor.commit();


                                } else {
                                    try {
                                        Toast.makeText(getContext(), "Choose account", Toast.LENGTH_SHORT).show();
                                    }catch (NullPointerException e){
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });



                        /*if (accounts.length > 1)
                            ((TextView) getActivity().findViewById(R.id.count_acc)).setText(accounts.length + " accounts "+listCheck.size()+" seelcted");
                        else
                            ((TextView) getActivity().findViewById(R.id.count_acc)).setText("1 account "+listCheck.size()+" selected");*/
                    }
                }
            });





        }


        Calendar contactDate = Calendar.getInstance();
        //=====================new date

        if (selectedContact.get(0).getDateCreate() != null)
            contactDate.setTime(selectedContact.get(0).getDateCreate());
        else
            contactDate.setTime(new Date());
        //=====================================

        String timeStr = "";
        timeStr = contactDate.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.ENGLISH) + " " + contactDate.get(Calendar.DAY_OF_MONTH);

        ((TextView) mainView.findViewById(R.id.timeCreateTimeline)).setText(timeStr);


        mainView.findViewById(R.id.frameGoGmail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((CheckBox) popupAccounts.findViewById(R.id.all_account)).setChecked(false);
                popupAccounts.setVisibility(View.VISIBLE);

                mainView.findViewById(R.id.frameGoGmail).setVisibility(View.GONE);

                //mainView.findViewById(R.id.frameChooseAccounts).setVisibility(View.VISIBLE);
                //mainView.findViewById(R.id.spaceMessage).setVisibility(View.VISIBLE);

                ((CheckBox) mainView.findViewById(R.id.all_select)).setChecked(false);

                FloatingActionMenu floatingActionMenu = (FloatingActionMenu) getActivity().findViewById(R.id.fabMenuContainer);

                //getActivity().findViewById(R.id.fab).setVisibility(View.GONE);

                floatingActionMenu.close(false);
                floatingActionMenu.setVisibility(View.GONE);


                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.GET_ACCOUNTS)) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.GET_ACCOUNTS}, 1);
                    } else {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.GET_ACCOUNTS}, 1);
                    }
                }
                //do some stuff
                ArrayList<String> emails = new ArrayList<>();

                Pattern gmailPattern = Patterns.EMAIL_ADDRESS;
                Account[] accounts = AccountManager.get(getActivity()).getAccountsByType("com.google");


                recyclerAccount = mainView.findViewById(R.id.listAccounts);
                recyclerAcc2 = getActivity().findViewById(R.id.recycler_account);

                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mainView.getContext());

                RecyclerView.LayoutManager mLayoutManager2 = new LinearLayoutManager(mainView.getContext());
                recyclerAcc2.setLayoutManager(mLayoutManager);
                recyclerAccount.setLayoutManager(mLayoutManager2);

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

                TextView textView =  ((TextView)popupAccounts.findViewById(R.id.count_account));

                CheckBox checkBox = popupAccounts.findViewById(R.id.all_accounts_select);

               // if (set == null || set.isEmpty()) {
                    ((TextView) popupAccounts.findViewById(R.id.count_account)).setText("0");
                //} else
                    //((TextView) popupAccounts.findViewById(R.id.count_account)).setText(String.valueOf(set.size()));

                accountAdapter = new AccountAdapter(list, getContext(), popupAccounts.findViewById(R.id.all_account), popupAccounts.findViewById(R.id.apply_account), getActivity().findViewById(R.id.count_acc), null, textView, checkBox);

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
                            ((TextView) popupAccounts.findViewById(R.id.apply_account)).setTextColor(getContext().getResources().getColor(R.color.primary));
                        } else {
                            accountAdapter.updateAllSelect(false);
                            ((TextView) popupAccounts.findViewById(R.id.apply_account)).setTextColor(getContext().getResources().getColor(R.color.gray));
                        }
                    }
                });

                popupAccounts.findViewById(R.id.cancel_account).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupAccounts.setVisibility(View.GONE);
                        if (mainView.findViewById(R.id.frameChooseAccounts).getVisibility() == View.GONE)
                            mainView.findViewById(R.id.frameGoGmail).setVisibility(View.VISIBLE);
                    }
                });


                popupAccounts.findViewById(R.id.apply_account).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (getContext() != null) {
                            if (!MainActivity.hasConnection(getContext())) {
                                Toast.makeText(getContext(), "Check your internet connection", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } else {
                            try {
                                if (!MainActivity.hasConnection(((ProfileFragment) getParentFragment()).activityApp)) {
                                    Toast.makeText(getContext(), "Check your internet connection", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                            } catch (Exception e) {
                                ((CreateFragment) getParentFragment()).closeOtherPopup();
                            }
                        }

                        if (accountAdapter.getListSelectId().size() > 0) {

                            mainView.findViewById(R.id.countAccount).setVisibility(View.GONE);
                            mainView.findViewById(R.id.frame_choose_google_account).setVisibility(View.GONE);
                            mainView.findViewById(R.id.listAccounts).setVisibility(View.GONE);
                            mainView.findViewById(R.id.frame_button_google_message).setVisibility(View.GONE);
                            mainView.findViewById(R.id.spaceMessage).setVisibility(View.GONE);


                            popupAccounts.setVisibility(View.GONE);

                            mainView.findViewById(R.id.frame_google_count).setVisibility(View.VISIBLE);
                            ((TextView) mainView.findViewById(R.id.text_count_google_account)).setText(String.valueOf(accountAdapter.getListSelectId().size()));


                            if (accountAdapter.getListSelectId().size() == 1)
                                Toast.makeText(getContext(), "1 gmail account selected", Toast.LENGTH_SHORT).show();
                            else if (accountAdapter.getListSelectId().size() > 1)
                                Toast.makeText(getContext(), accountAdapter.getListSelectId().size() + " gmail accounts chosen", Toast.LENGTH_SHORT).show();

                            floatingActionMenu.setVisibility(View.GONE);

                            mainView.findViewById(R.id.frameGetMessage).setVisibility(View.VISIBLE);


                            listCredential = new ArrayList<>();

                            for (int i = 0; i < accountAdapter.getListSelectId().size(); i++) {

                                GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(
                                        getActivity().getApplicationContext(), Arrays.asList(SCOPES))
                                        .setBackOff(new ExponentialBackOff());

                                credential.setSelectedAccountName(accountAdapter.getListSelectId().get(i).name);

                                listCredential.add(credential);

                            }


                            getMessages();


                            //mainView.findViewById(R.id.joinContact).setVisibility(View.GONE);

                            //int px = (int) (54 * getContext().getResources().getDisplayMetrics().density);

                            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mainView.findViewById(R.id.frameChooseAccounts).getLayoutParams();
                            params.topMargin = 0;

                            Set<String> set = new HashSet<>();
                            for (int i = 0; i < accountAdapter.getListSelectId().size(); i++) {
                                set.add(accountAdapter.getListSelectId().get(i).name);
                            }

                            SharedPreferences mSettings;
                            mSettings = getActivity().getSharedPreferences("accountUser", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = mSettings.edit();
                            editor.putStringSet("accounts", set);
                            editor.commit();


                           /* FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mainView.findViewById(R.id.joinContact).getLayoutParams();

                            layoutParams.gravity = Gravity.BOTTOM;

                            mainView.findViewById(R.id.joinContact).setLayoutParams(layoutParams);

                            mainView.findViewById(R.id.joinContact).requestLayout();
                            mainView.findViewById(R.id.frameChooseAccounts).requestLayout();*/


                            mainView.findViewById(R.id.frame_google_count).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (mainView.findViewById(R.id.frameGetMessage).getVisibility() == View.GONE) {
                                        popupAccounts.setVisibility(View.VISIBLE);

                                        ((CheckBox) mainView.findViewById(R.id.all_select)).setChecked(false);

                                        FloatingActionMenu floatingActionMenu = (FloatingActionMenu) getActivity().findViewById(R.id.fabMenuContainer);

                                        //getActivity().findViewById(R.id.fab).setVisibility(View.GONE);

                                        floatingActionMenu.close(false);
                                        floatingActionMenu.setVisibility(View.GONE);


                                        recyclerAcc2 = getActivity().findViewById(R.id.recycler_account);
                                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mainView.getContext());
                                        recyclerAcc2.setLayoutManager(mLayoutManager);


                                        ArrayList<Account> list = new ArrayList<>();
                                        for (Account account : accounts) {
                                            list.add(account);
                                        }

                                        //if (set == null || set.isEmpty()) {
                                            ((TextView) popupAccounts.findViewById(R.id.count_account)).setText("0");
                                        //} else
                                       //     ((TextView) popupAccounts.findViewById(R.id.count_account)).setText(String.valueOf(set.size()));

                                       // if (accountAdapter == null) {
                                            TextView textView =  ((TextView)popupAccounts.findViewById(R.id.count_account));

                                            CheckBox checkBox = popupAccounts.findViewById(R.id.all_accounts_select);
                                            accountAdapter = new AccountAdapter(list, getContext(), popupAccounts.findViewById(R.id.all_account), popupAccounts.findViewById(R.id.apply_account), getActivity().findViewById(R.id.count_acc), null, textView, checkBox);
                                       // }

                                        recyclerAcc2.setAdapter(accountAdapter);




                                        ((CheckBox) popupAccounts.findViewById(R.id.all_account)).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                if (((CheckBox) popupAccounts.findViewById(R.id.all_account)).isChecked()) {
                                                    accountAdapter.updateAllSelect(true);
                                                    ((TextView) popupAccounts.findViewById(R.id.apply_account)).setTextColor(getContext().getResources().getColor(R.color.primary));
                                                } else {
                                                    accountAdapter.updateAllSelect(false);
                                                    ((TextView) popupAccounts.findViewById(R.id.apply_account)).setTextColor(getContext().getResources().getColor(R.color.gray));
                                                }
                                            }
                                        });

                                        popupAccounts.findViewById(R.id.cancel_account).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                popupAccounts.setVisibility(View.GONE);
                                                if (mainView.findViewById(R.id.frameChooseAccounts).getVisibility() == View.GONE)
                                                    mainView.findViewById(R.id.frameGoGmail).setVisibility(View.VISIBLE);
                                            }
                                        });


                                        popupAccounts.findViewById(R.id.apply_account).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if (getContext() != null) {
                                                    if (!MainActivity.hasConnection(getContext())) {
                                                        Toast.makeText(getContext(), "Check your internet connection", Toast.LENGTH_SHORT).show();
                                                        return;
                                                    }
                                                } else {
                                                    try {
                                                        if (!MainActivity.hasConnection(((ProfileFragment) getParentFragment()).activityApp)) {
                                                            Toast.makeText(getContext(), "Check your internet connection", Toast.LENGTH_SHORT).show();
                                                            return;
                                                        }

                                                    } catch (Exception e) {

                                                    }
                                                }
                                                if (accountAdapter != null && accountAdapter.getListSelectId() != null && accountAdapter.getListSelectId().size() > 0) {

                                                    mainView.findViewById(R.id.countAccount).setVisibility(View.GONE);
                                                    mainView.findViewById(R.id.frame_choose_google_account).setVisibility(View.GONE);
                                                    mainView.findViewById(R.id.listAccounts).setVisibility(View.GONE);
                                                    mainView.findViewById(R.id.frame_button_google_message).setVisibility(View.GONE);
                                                    mainView.findViewById(R.id.spaceMessage).setVisibility(View.GONE);

                                                    popupAccounts.setVisibility(View.GONE);

                                                    mainView.findViewById(R.id.frame_google_count).setVisibility(View.VISIBLE);
                                                    ((TextView) mainView.findViewById(R.id.text_count_google_account)).setText(String.valueOf(accountAdapter.getListSelectId().size()));


                                                    if (accountAdapter.getListSelectId().size() == 1)
                                                        Toast.makeText(getContext(), "1 gmail account selected", Toast.LENGTH_SHORT).show();
                                                    else if (accountAdapter.getListSelectId().size() > 1)
                                                        Toast.makeText(getContext(), accountAdapter.getListSelectId().size() + " gmail accounts selected", Toast.LENGTH_SHORT).show();

                                                    FloatingActionMenu floatingActionMenu = (FloatingActionMenu) getActivity().findViewById(R.id.fabMenuContainer);
                                                    floatingActionMenu.setVisibility(View.GONE);

                                                    mainView.findViewById(R.id.frameGetMessage).setVisibility(View.VISIBLE);


                                                    listCredential = new ArrayList<>();

                                                    for (int i = 0; i < accountAdapter.getListSelectId().size(); i++) {

                                                        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(
                                                                getActivity().getApplicationContext(), Arrays.asList(SCOPES))
                                                                .setBackOff(new ExponentialBackOff());

                                                        credential.setSelectedAccountName(accountAdapter.getListSelectId().get(i).name);

                                                        listCredential.add(credential);

                                                    }



                                                    getMessages();


                                                    //int px = (int) (54 * getContext().getResources().getDisplayMetrics().density);

                                                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mainView.findViewById(R.id.frameChooseAccounts).getLayoutParams();
                                                    params.topMargin = 0;

                                                    Set<String> set = new HashSet<>();
                                                    for (int i = 0; i < accountAdapter.getListSelectId().size(); i++) {
                                                        set.add(accountAdapter.getListSelectId().get(i).name);
                                                    }

                                                    SharedPreferences mSettings;
                                                    mSettings = getActivity().getSharedPreferences("accountUser", Context.MODE_PRIVATE);
                                                    SharedPreferences.Editor editor = mSettings.edit();
                                                    editor.putStringSet("accounts", set);
                                                    editor.commit();


                                                } else {
                                                    try {
                                                        Toast.makeText(getContext(), "Choose account", Toast.LENGTH_SHORT).show();
                                                    }catch (NullPointerException e){
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }
                                        });
                                        /*if (accounts.length > 1)
                                            ((TextView) getActivity().findViewById(R.id.count_acc)).setText(accounts.length + " accounts 0 selected");
                                        else
                                            ((TextView) getActivity().findViewById(R.id.count_acc)).setText("1 account 0 selected");*/
                                    }
                                }
                            });

                        } else
                            Toast.makeText(getContext(), "Choose account", Toast.LENGTH_SHORT).show();
                    }
                });


                //((TextView) mainView.findViewById(R.id.countAccount)).setText(accounts.length + " accounts");


                //GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());

               /* if (account != null) {
                    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestEmail()
                            .build();


                    mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

                    mGoogleSignInClient.signOut();
                }*/

            }
        });


        mainView.findViewById(R.id.closerTimeline).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ((ProfileFragment) getParentFragment()).closeOtherPopup();
                } catch (Exception e) {
                    ((CreateFragment) getParentFragment()).closeOtherPopup();
                }

                if (popupMessage != null && popupMessage.getVisibility() == View.VISIBLE)
                    popupMessage.setVisibility(View.GONE);

                if (popupContacts != null && popupContacts.getVisibility() == View.VISIBLE)
                    popupContacts.setVisibility(View.GONE);

            }
        });
    }

    public double countMessages = 0;

    public double countLoad = 0;

    public void getMessages() {


        String nameContact = new String(selectedContact.get(0).getName());

        long id_Contact = selectedContact.get(0).getId();

        new Thread(new Runnable() {
            @Override
            public void run() {

                Realm realm = Realm.getDefaultInstance();
                Contact select_contact = ContactCacheService.getContactByIdSynk(id_Contact, realm);

                //int count_mess = 100 / listCredential.size();

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((TextView) mainView.findViewById(R.id.progressTimelineProfile)).setText("Expecting time 100%");
                        ((TextView) mainView.findViewById(R.id.progressTimelineProfile)).setVisibility(View.VISIBLE);
                    }
                });

                listMessages = new ArrayList<>();




                for (int c = 0; c < listCredential.size(); c++) {
                    for (int z = 0; z < email_contact.size(); z++) {

                        HttpTransport transport = AndroidHttp.newCompatibleTransport();
                        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

                        Gmail service = new Gmail.Builder(transport, jsonFactory, listCredential.get(c))
                                .setApplicationName(APPLICATION_NAME)
                                .build();

                        String user = listCredential.get(c).getSelectedAccountName();

                        try {
                            ListMessagesResponse countM1 = service.users().messages().list(user).setQ("to:" + email_contact.get(z)).setMaxResults(30L).execute();
                            if (countM1 != null && countM1.getMessages() != null)
                                countMessages += countM1.getMessages().size();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        transport = AndroidHttp.newCompatibleTransport();
                        jsonFactory = JacksonFactory.getDefaultInstance();

                        service = new Gmail.Builder(transport, jsonFactory, listCredential.get(c))
                                .setApplicationName(APPLICATION_NAME)
                                .build();

                        user = listCredential.get(c).getSelectedAccountName();

                        try {
                            ListMessagesResponse countM2 = service.users().messages().list(user).setQ("from:" + email_contact.get(z)).setMaxResults(30L).execute();
                            if (countM2 != null && countM2.getMessages() != null)
                                countMessages += countM2.getMessages().size();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                if (countMessages == 0){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((TextView) mainView.findViewById(R.id.progressTimelineProfile)).setVisibility(View.GONE);
                        }
                    });
                }



                for (int c = 0; c < listCredential.size(); c++) {
                    for (int z = 0; z < email_contact.size(); z++) {

                        HttpTransport transport = AndroidHttp.newCompatibleTransport();
                        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

                        Gmail service = new Gmail.Builder(transport, jsonFactory, listCredential.get(c))
                                .setApplicationName(APPLICATION_NAME)
                                .build();

                        String user = listCredential.get(c).getSelectedAccountName();

                        //ListLabelsResponse listResponse = null;

                        ListMessagesResponse lll = null;
                        try {



                            lll = service.users().messages().list(user).setQ("to:" + email_contact.get(z)).setMaxResults(30L).execute();

                            //countMessages +=lll.getMessages().size();


                        } catch (UserRecoverableAuthIOException e1) {

                            e1.printStackTrace();
                            startActivityForResult(e1.getIntent(), REQUEST_AUTHORIZATION);
                            return;
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

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                //for (int a = 0; a < message.getLabelIds().size(); a++)


                                try {




                                    StringBuilder stringBuilder = new StringBuilder();
                                    getPlainTextFromMessageParts(message.getPayload().getParts(), stringBuilder);
                                    byte[] bodyBytes = Base64.decodeBase64(stringBuilder.toString());
                                    String text = new String(bodyBytes, StandardCharsets.UTF_8);


                                    emailMessage.setMessage(text.trim());

                                   /* if (StringUtils.newStringUtf8(Base64.decodeBase64(message.getPayload().getParts().get(0).getBody().getData())) == null) {
                                        emailMessage.setMessage(message.getSnippet());
                                    } else {
                                        emailMessage.setMessage(StringUtils.newStringUtf8(Base64.decodeBase64(message.getPayload().getParts().get(0).getBody().getData())));  //=====================set Message
                                    }*/








                                    String type = "No Subject";

                                    boolean cont = true;
                                    for (int i2 = 0; i2 < message.getPayload().getHeaders().size(); i2++) {
                                        System.out.println(i2 + " from . " + message.getPayload().getHeaders().get(i2).getValue() + ", type = " + message.getPayload().getHeaders().get(i2).getName());

                                        if (message.getPayload().getHeaders().get(i2).getName().equalsIgnoreCase("Subject")) {
                                            if (!message.getPayload().getHeaders().get(i2).getValue().equalsIgnoreCase("") /*&& !message.getPayload().getHeaders().get(i2).getValue().equalsIgnoreCase("1.0")*/) {
                                                type = message.getPayload().getHeaders().get(i2).getValue();
                                            }
                                        }

                                        if (message.getPayload().getHeaders().get(i2).getName().equalsIgnoreCase("From")) {
                                            if (!message.getPayload().getHeaders().get(i2).getValue().toLowerCase().contains(user.toLowerCase())) {
                                                cont = false;
                                                break;
                                            }
                                        }
                                    }
                                    if (!cont) continue;

                                    emailMessage.setTitle(type);  //=======================set title

                                    if (emailMessage.getTitle().isEmpty()) {
                                        emailMessage.setTitle("No subject");
                                    }

                                    emailMessage.setAccount(new ai.extime.Models.Account(listCredential.get(c).getSelectedAccountName(), listCredential.get(c).getSelectedAccountName())); //=============================set account


                                    /*if(select_contact.listOfContacts == null || select_contact.listOfContacts.isEmpty()) {
                                        emailMessage.setEmail_2(nameContact); //=============================set account
                                    }else{

                                        if(select_contact.getListOfContactInfo() != null){
                                            for(ContactInfo i_ : select_contact.getListOfContactInfo()){
                                                if(i_.isEmail && i_.value.equals(email_contact.get(z))){
                                                    emailMessage.setEmail_2(select_contact.getName());
                                                    break;
                                                }
                                            }
                                        }


                                        for(Contact cc : select_contact.listOfContacts){
                                            if(cc.getListOfContactInfo() != null){
                                                for(ContactInfo i_ : cc.getListOfContactInfo()){
                                                    if(i_.isEmail && i_.value.equals(email_contact.get(z))){
                                                        emailMessage.setEmail_2(cc.getName());
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    }*/

                                    emailMessage.setEmail_2(listCredential.get(c).getSelectedAccountName());

                                    Timestamp stamp = new Timestamp(message.getInternalDate());
                                    Date date = new Date(stamp.getTime());


                                    emailMessage.setDate(date); //==========================set date

                                    emailMessage.setMessageData(fileType.getTypeOFFile(message, service, user));

                                    emailMessage.setF_Message(message);

                                    emailMessage.setListOfTypeMessage(message.getLabelIds());

                                    emailMessage.setCredential(listCredential.get(c));

                                    emailMessage.setListHeaders(message.getPayload().getHeaders());

                                    emailMessage.setThreadID(message.getThreadId());

                                    //
                                    emailMessage.setId(message.getId());
                                    emailMessage.setUser(user);

                                    ArrayList<Clipboard> listC = new ArrayList<>(ClipboardType.getListDataClipboard(emailMessage.getMessage(), getContext()));

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
                                                emailMessage.getMessageData().add(messageData);


                                        }
                                    }

                                    listMessages.add(emailMessage);


                                    try {

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }


                                    //Log.d("Body", String.valueOf(message));

                                } catch (Exception t) {
                                    t.printStackTrace();
                                    // Log.e("My app", "not done");
                                }


                            }

                        }

                        //====================================================================================== TO


                        transport = AndroidHttp.newCompatibleTransport();
                        jsonFactory = JacksonFactory.getDefaultInstance();

                        service = new Gmail.Builder(transport, jsonFactory, listCredential.get(c))
                                .setApplicationName(APPLICATION_NAME)
                                .build();

                        user = listCredential.get(c).getSelectedAccountName();
                        //listResponse = null;

                        lll = null;
                        try {
                            if(email_contact != null && z < email_contact.size()) {
                                lll = service.users().messages().list(user).setQ("from:" + email_contact.get(z)).setMaxResults(30L).execute();
                            }

                        } catch (UserRecoverableAuthIOException e1) {

                            e1.printStackTrace();
                            startActivityForResult(e1.getIntent(), REQUEST_AUTHORIZATION);
                            return;
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

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                //for (int a = 0; a < message.getLabelIds().size(); a++)


                                try {


                                    System.out.println("Message snippet: " + message.getSnippet());


                                    StringBuilder stringBuilder = new StringBuilder();
                                    getPlainTextFromMessageParts(message.getPayload().getParts(), stringBuilder);
                                    byte[] bodyBytes = Base64.decodeBase64(stringBuilder.toString());
                                    String text = new String(bodyBytes, StandardCharsets.UTF_8);




                                    emailMessage.setMessage(text.trim());

                                    //emailMessage.setMessage(StringUtils.newStringUtf8(Base64.decodeBase64(message.getPayload().getParts().get(0).getBody().getData())));  //=====================set Message




                                    /*if (StringUtils.newStringUtf8(Base64.decodeBase64(message.getPayload().getParts().get(0).getBody().getData())) == null) {
                                        emailMessage.setMessage(message.getSnippet());
                                    } else {
                                        emailMessage.setMessage(StringUtils.newStringUtf8(Base64.decodeBase64(message.getPayload().getParts().get(0).getBody().getData())));  //=====================set Message
                                    }*/




                                    String type = "No subject";

                                    for (int i2 = 0; i2 < message.getPayload().getHeaders().size(); i2++) {
                                        System.out.println(i2 + "to . " + message.getPayload().getHeaders().get(i2).getValue() + ", type = " + message.getPayload().getHeaders().get(i2).getName());

                                        if (message.getPayload().getHeaders().get(i2).getName().equalsIgnoreCase("Subject")) {
                                            if (!message.getPayload().getHeaders().get(i2).getValue().equalsIgnoreCase("") /*&& !message.getPayload().getHeaders().get(i2).getValue().equalsIgnoreCase("1.0")*/) {
                                                type = message.getPayload().getHeaders().get(i2).getValue();
                                            }
                                        }

                                    }



                                    emailMessage.setTitle(type);  //=======================set title


                                    if(select_contact.listOfContacts == null || select_contact.listOfContacts.isEmpty()) {
                                        emailMessage.setAccount(new ai.extime.Models.Account(nameContact, email_contact.get(z))); //=============================set account
                                    }else{

                                        if(select_contact.getListOfContactInfo() != null){
                                            for(ContactInfo i_ : select_contact.getListOfContactInfo()){
                                                if(i_.isEmail && i_.value.equals(email_contact.get(z))){
                                                    emailMessage.setAccount(new ai.extime.Models.Account(select_contact.getName(), email_contact.get(z)));
                                                    break;
                                                }
                                            }
                                        }


                                        for(Contact cc : select_contact.listOfContacts){
                                            if(cc.getListOfContactInfo() != null){
                                                for(ContactInfo i_ : cc.getListOfContactInfo()){
                                                    if(i_.isEmail && i_.value.equals(email_contact.get(z))){
                                                        emailMessage.setAccount(new ai.extime.Models.Account(cc.getName(), email_contact.get(z)));
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    }


                                    Timestamp stamp = new Timestamp(message.getInternalDate());
                                    Date date = new Date(stamp.getTime());


                                    emailMessage.setDate(date); //==========================set date

                                    emailMessage.setMessageData(fileType.getTypeOFFile(message, service, user));

                                    emailMessage.setEmail_2(listCredential.get(c).getSelectedAccountName());

                                    listMessages.add(emailMessage);

                                    emailMessage.setF_Message(message);

                                    emailMessage.setListOfTypeMessage(message.getLabelIds());

                                    emailMessage.setCredential(listCredential.get(c));

                                    emailMessage.setListHeaders(message.getPayload().getHeaders());

                                    emailMessage.setThreadID(message.getThreadId());

                                    //

                                    emailMessage.setId(message.getId());
                                    emailMessage.setUser(user);

                                    //emailMessage.setListInfo(ClipboardType.getListDataClipboard(message.getSnippet(), getContext()));

                                    ArrayList<Clipboard> listC = new ArrayList<>(ClipboardType.getListDataClipboard(emailMessage.getMessage(), getContext()));

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

                                            /*Contact cc = ContactCacheService.find2(messageData.getClipboard().getValueCopy(), messageData.getClipboard().getType(), null);
                                            if (cc != null)
                                                messageData.getClipboard().addContactToListSearch(cc);*/

                                            if (!listC.get(0).getListClipboards().get(iq).getType().equals(ClipboardEnum.HASHTAG) && !listC.get(0).getListClipboards().get(iq).getValueCopy().isEmpty())
                                                emailMessage.getMessageData().add(messageData);
                                        }
                                    }



                                    //Log.d("Body", String.valueOf(message));

                                } catch (Exception t) {
                                    t.printStackTrace();
                                }


                            }


                        }

                        //==
                    }
                }

                realm.close();

                try {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Collections.sort(listMessages, (messFirst, messSecond) -> messSecond.getDate().compareTo(messFirst.getDate()));

                            listMessages.add(new EmailMessage(null, null, value, null, selectedContact.get(0).getDateCreate()));

                            messageAdapter = new MessageAdapter(getContext(), listMessages, (IMessage) ContactTimelineDataFragment.this, 1);
                            recyclerAccount.setAdapter(messageAdapter);




                            if (listMessages.size() == 1) {
                                mainView.findViewById(R.id.joinContact).setVisibility(View.VISIBLE);
                                mainView.findViewById(R.id.frameChooseAccounts).setVisibility(View.GONE);
                            } else {
                                recyclerAccount.setVisibility(View.VISIBLE);
                                mainView.findViewById(R.id.frameChooseAccounts).setVisibility(View.VISIBLE);
                                mainView.findViewById(R.id.joinContact).setVisibility(View.GONE);
                            }
                            mainView.findViewById(R.id.frameGetMessage).setVisibility(View.GONE);

                        }
                    });


                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void updateProgress() {


        double c1 = 100.0 / countMessages;

        double c2 = c1 * countLoad;

        double proc = 100 - c2;

        try {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mainView.findViewById(R.id.progressTimelineProfile).setVisibility(View.VISIBLE);
                    ((TextView) mainView.findViewById(R.id.progressTimelineProfile)).setText("Expecting time " + (int) proc + "%");
                    if (proc < 1) {
                        mainView.findViewById(R.id.progressTimelineProfile).setVisibility(View.GONE);
                        countMessages = 0;
                        countLoad = 0;
                    }
                }
            });
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    private void getPlainTextFromMessageParts(List<MessagePart> messageParts, StringBuilder stringBuilder) {
        for (MessagePart messagePart : messageParts) {
            if (messagePart.getMimeType().equals("text/plain") || messagePart.getMimeType().equals("text/html")) {
                stringBuilder.append(messagePart.getBody().getData());
            }

            if (messagePart.getParts() != null) {
                getPlainTextFromMessageParts(messagePart.getParts(), stringBuilder);
            }
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
                mainView.findViewById(R.id.frameChooseAccounts).setVisibility(View.GONE);
                mainView.findViewById(R.id.frameGoGmail).setVisibility(View.VISIBLE);
                mainView.findViewById(R.id.frameGetMessage).setVisibility(View.GONE);
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



                getActivity().getContentResolver().takePersistableUriPermission(uriPaths[0], Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);


                File f = new File(uriPaths[0].toString());



                //dumpImageMetaData(uriPaths[0]);

                FilesTemplate ft = new FilesTemplate(uriPaths[0]);

                FileType.getNameSize(ft, getActivity());

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

                    getActivity().getContentResolver().takePersistableUriPermission(uriPaths[i], Intent.FLAG_GRANT_READ_URI_PERMISSION
                            | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);


                    FilesTemplate ft = new FilesTemplate(uriPaths[i]);

                    FileType.getNameSize(ft, getActivity());

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

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);



            mCredential.setSelectedAccountName(account.getAccount().name);


            //GoogleAccountCredential cc = GoogleAuthProvider.get

            // Signed in successfully, show authenticated UI.
            //updateUI(account);


        } catch (ApiException e) {


        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle bundle) {

        mainView = inflater.inflate(R.layout.fragment_contact_profile_timeline_list, viewGroup, false);
        rootView = mainView.getRootView();
        selectedContact = new ArrayList<>();
        getDataFromBundle();


        fileType = new FileType();

        initViews();

        if(selectedContact.get(0).listOfContacts == null || selectedContact.get(0).listOfContacts.isEmpty())
            ((TextView)mainView.findViewById(R.id.hashtag_text)).setText("joined - id"+selectedContact.get(0).getId());
        else
            ((TextView)mainView.findViewById(R.id.hashtag_text)).setText("joined");

        return mainView;
    }


    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();

        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onStop() {
        super.onStop();

        if (EventBus.getDefault().isRegistered(this)) EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    //private boolean blur;
    public void setBlur(boolean blur) {
        //this.blur = blur;
        if (messageAdapter != null) {
            messageAdapter.setBlur(blur);
        }

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser && isResumed() && email_contact != null && !email_contact.isEmpty()) {



            if (mainView.findViewById(R.id.frameChooseAccounts).getVisibility() == View.GONE && mainView.findViewById(R.id.frameChooseAccounts).getVisibility() == View.GONE && mainView.findViewById(R.id.frameGetMessage).getVisibility() == View.GONE) {
                SharedPreferences mSettings;
                mSettings = getActivity().getSharedPreferences("accountUser", Context.MODE_PRIVATE);
                Set<String> set = mSettings.getStringSet("accounts", null);

                if (set != null && !set.isEmpty()) {


                    if (getContext() != null) {
                        if (!MainActivity.hasConnection(getContext())) {
                            Toast.makeText(getContext(), "Check your internet connection", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } else {
                        try {
                            if (!MainActivity.hasConnection(((ProfileFragment) getParentFragment()).activityApp)) {
                                Toast.makeText(getContext(), "Check your internet connection", Toast.LENGTH_SHORT).show();
                                return;
                            }

                        } catch (Exception e) {
                            ((CreateFragment) getParentFragment()).closeOtherPopup();
                        }
                    }


                    mainView.findViewById(R.id.countAccount).setVisibility(View.GONE);
                    mainView.findViewById(R.id.frame_choose_google_account).setVisibility(View.GONE);
                    mainView.findViewById(R.id.listAccounts).setVisibility(View.GONE);
                    mainView.findViewById(R.id.frame_button_google_message).setVisibility(View.GONE);
                    mainView.findViewById(R.id.spaceMessage).setVisibility(View.GONE);


                    popupAccounts.setVisibility(View.GONE);

                    mainView.findViewById(R.id.frame_google_count).setVisibility(View.VISIBLE);
                    ((TextView) mainView.findViewById(R.id.text_count_google_account)).setText(String.valueOf(set.size()));


                /*if (set.size() == 1)
                    Toast.makeText(getContext(), "1 gmail account chosen", Toast.LENGTH_SHORT).show();
                else if (set.size() > 1)
                    Toast.makeText(getContext(), accountAdapter.getListSelectId().size() + " gmail accounts chosen", Toast.LENGTH_SHORT).show();*/

                    FloatingActionMenu floatingActionMenu = (FloatingActionMenu) getActivity().findViewById(R.id.fabMenuContainer);
                    floatingActionMenu.setVisibility(View.GONE);

                    mainView.findViewById(R.id.frameGetMessage).setVisibility(View.VISIBLE);


                    listCredential = new ArrayList<>();

                    Account[] accounts = AccountManager.get(getActivity()).getAccountsByType("com.google");

                    for (Account account : accounts) {

                        if (set.contains(account.name)) {
                            GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(
                                    getActivity().getApplicationContext(), Arrays.asList(SCOPES))
                                    .setBackOff(new ExponentialBackOff());


                            credential.setSelectedAccountName(account.name);

                            listCredential.add(credential);
                        }

                    }



                    getMessages();


                    //mainView.findViewById(R.id.joinContact).setVisibility(View.GONE);

                    //int px = (int) (54 * getContext().getResources().getDisplayMetrics().density);

                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mainView.findViewById(R.id.frameChooseAccounts).getLayoutParams();
                    params.topMargin = 0;


                }
            }
        } else {

            if (popupMessage != null && popupMessage.getVisibility() == View.VISIBLE)
                popupMessage.setVisibility(View.GONE);

            if (popupContacts != null && popupContacts.getVisibility() == View.VISIBLE)
                popupContacts.setVisibility(View.GONE);
        }
    }

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

                            getActivity().runOnUiThread(new Runnable() {
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

                            if(!star)
                                mods = new ModifyMessageRequest().setRemoveLabelIds(listLabel);
                            else
                                mods = new ModifyMessageRequest().setAddLabelIds(listLabel);

                            service.users().messages().modify(message.getUser(), message.getId(), mods).execute();


                            if(!star)
                                message.getListOfTypeMessage().remove(message.getListOfTypeMessage().indexOf("STARRED"));
                            else
                                message.getListOfTypeMessage().add("STARRED");

                            getActivity().runOnUiThread(new Runnable() {
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
        if(popupMessage != null)
            return popupMessage.getVisibility() == View.VISIBLE;
        else return false;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(UpdateMessageAdapter event) {
        messageAdapter.notifyDataSetChanged();
    }

    private boolean startSearchContact = true;

    @Override
    public void clickMessage(EmailMessage message) {

        if (editPopupTemplate != null && editPopupTemplate.getVisibility() == View.VISIBLE) {
            editPopupTemplate.setVisibility(View.GONE);
            return;
        }

        if (popupTemplate != null && popupTemplate.getVisibility() == View.VISIBLE) {
            popupTemplate.setVisibility(View.GONE);
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

        /*if((getParentFragment() != null ? ((ProfileFragment) getParentFragment()).mainView.findViewById(R.id.profile_popup).getVisibility() : 0) == View.VISIBLE){
            if (getParentFragment() != null) {
                ((ProfileFragment) getParentFragment()).mainView.findViewById(R.id.profile_popup).setVisibility(View.GONE);
                return;
            }

        }*/


        popupMessage.findViewById(R.id.popup_menu_message).setVisibility(View.GONE);

        popupMessage.findViewById(R.id.replyMessPopup).setVisibility(View.GONE);

        if(message.getListOfTypeMessage().contains("STARRED")){
            popupMessage.findViewById(R.id.favoriteMessagePreview).setVisibility(View.VISIBLE);
            popupMessage.findViewById(R.id.favoriteMessagePreviewEmpty).setVisibility(View.GONE);
        }else{
            popupMessage.findViewById(R.id.favoriteMessagePreview).setVisibility(View.GONE);
            popupMessage.findViewById(R.id.favoriteMessagePreviewEmpty).setVisibility(View.VISIBLE);
        }

        if(message.getListOfTypeMessage().contains("DRAFT")){
            popupMessage.findViewById(R.id.draft_message_text).setVisibility(View.VISIBLE);
        }else{
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


        popupMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupMessage.findViewById(R.id.popup_menu_message).getVisibility() == View.VISIBLE)
                    popupMessage.findViewById(R.id.popup_menu_message).setVisibility(View.GONE);
            }
        });

        String sendTo = null;
        String sendToName = null;
        String replyFrom = null;

        boolean checkSelect = false;

        if (email_contact.contains(message.getAccount().getEmail())/*message.getAccount().getEmail().equals(email_contact)*/) {

            checkSelect = true;

            int nameHash = message.getAccount().getName().hashCode();
            int color = Color.rgb(Math.abs(nameHash * 28439) % 255, Math.abs(nameHash * 211239) % 255, Math.abs(nameHash * 42368) % 255);
            GradientDrawable circle = (GradientDrawable) ContextCompat.getDrawable(getContext(), R.drawable.blue_circle).mutate();
            circle.setColor(color);
            ((CircleImageView) popupMessage.findViewById(R.id.avatar_sender)).setBackground(circle);
            //String initials = getInitials(selectedContact.get(0).getName());
            String initials = getInitials((message.getAccount().getName()));
            ((TextView) popupMessage.findViewById(R.id.initial_sender)).setText(initials);
            ((TextView) popupMessage.findViewById(R.id.header_message)).setText(message.getAccount().getName() + " < " + message.getAccount().getEmail() + " >");

            SharedPreferences mSettings;
            mSettings = getActivity().getSharedPreferences("accountUser", Context.MODE_PRIVATE);
            Set<String> set = mSettings.getStringSet("accounts", null);


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
            GradientDrawable circle2 = (GradientDrawable) ContextCompat.getDrawable(getContext(), R.drawable.blue_circle).mutate();
            circle2.setColor(color2);*/

            //((CircleImageView) popupMessage.findViewById(R.id.avatar_recipient)).setBackground(circle2);
            String initials2 = getInitials(message.getEmail_2());
            //((TextView) popupMessage.findViewById(R.id.initials_recipient)).setText(initials2);

            //((ImageView) popupMessage.findViewById(R.id.google_recipient)).setVisibility(View.VISIBLE);

        } else {

            int nameHash = message.getAccount().getName().hashCode();
            int color = Color.rgb(Math.abs(nameHash * 28439) % 255, Math.abs(nameHash * 211239) % 255, Math.abs(nameHash * 42368) % 255);
            GradientDrawable circle = (GradientDrawable) ContextCompat.getDrawable(getContext(), R.drawable.blue_circle).mutate();
            circle.setColor(color);
            ((CircleImageView) popupMessage.findViewById(R.id.avatar_sender)).setBackground(circle);
            String initials = getInitials(message.getEmail_2());
            ((TextView) popupMessage.findViewById(R.id.initial_sender)).setText(initials);

            ((TextView) popupMessage.findViewById(R.id.header_message)).setText("< " + message.getAccount().getName() + " >");

            replyFrom = message.getAccount().getName();

         /*   if (set != null && set.contains(message.getAccount().getEmail()) || set != null && set.contains(message.getAccount().getName())) {
                if (set.contains(message.getAccount().getEmail()))

                else replyFrom = message.getAccount().getName();
            } else {
                sendTo = message.getAccount().getEmail();
                sendToName = message.getAccount().getName();
            }*/


            int hash = selectedContact.get(0).getName().hashCode();

           /* int color2 = Color.rgb(Math.abs(hash * 28439) % 255, Math.abs(hash * 211239) % 255, Math.abs(hash * 42368) % 255);
            GradientDrawable circle2 = (GradientDrawable) ContextCompat.getDrawable(getContext(), R.drawable.blue_circle).mutate();
            circle2.setColor(color2);
            ((CircleImageView) popupMessage.findViewById(R.id.avatar_recipient)).setBackground(circle2);
            String initials2 = getInitials(selectedContact.get(0).getName());
            ((TextView) popupMessage.findViewById(R.id.initials_recipient)).setText(initials2);*/

            ((ImageView) popupMessage.findViewById(R.id.google_sender)).setVisibility(View.VISIBLE);

        }


        //================================


        String data = null;

        ArrayList<ContactOfMessage> listOfContactsMessage = new ArrayList<>();

        ContactOfMessage sender = new ContactOfMessage();

        for (int i2 = 0; i2 < message.getF_Message().getPayload().getHeaders().size(); i2++) {
            System.out.println(i2 + ". " + message.getF_Message().getPayload().getHeaders().get(i2).getValue() + ", type = " + message.getF_Message().getPayload().getHeaders().get(i2).getName());

            if (message.getF_Message().getPayload().getHeaders().get(i2).getName().equalsIgnoreCase("From") && !checkSelect) {
                String send = message.getF_Message().getPayload().getHeaders().get(i2).getValue();
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

            if (message.getF_Message().getPayload().getHeaders().get(i2).getName().equalsIgnoreCase("To")) {
                data = message.getF_Message().getPayload().getHeaders().get(i2).getValue();
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
                    } else {

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

        if(listOfContactsMessage.size() == 0){
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

        }

        if (message.getMessage() != null) {
            //message.setMessage(message.getMessage().replaceAll("\n", ""));
            ((TextView) popupMessage.findViewById(R.id.textMessage)).setText(message.getMessage().trim());
        } else
            ((TextView) popupMessage.findViewById(R.id.textMessage)).setText("");

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
                                    getActivity().startActivity(i);

                                } else if (ClipboardType.isEmail(text.toString().substring(startI, startI + message.getMessageData().get(finalI).getClipboard().getValueCopy().length()))) {

                                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                                    emailIntent.setData(Uri.parse("mailto:" + text.toString().substring(startI, startI + message.getMessageData().get(finalI).getClipboard().getValueCopy().length())));
                                    getActivity().startActivity(Intent.createChooser(emailIntent, "Send email"));

                                } else if (ClipboardType.isPhone(text.toString().substring(startI, startI + message.getMessageData().get(finalI).getClipboard().getValueCopy().length()))) {
                                    getActivity().startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", text.toString().substring(startI, startI + message.getMessageData().get(finalI).getClipboard().getValueCopy().length()), null)));
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
            if(text.toString().contains("<br>"))
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

        //===========================

        if (message.getMessageData().size() > 0) {

            popupMessage.findViewById(R.id.linearCountDataMEssage).setVisibility(View.VISIBLE);
            popupMessage.findViewById(R.id.linear_arg).setVisibility(View.VISIBLE);


            RecyclerView recyclerView = popupMessage.findViewById(R.id.linear_arg);

            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));


            MessageDataAdapter adapter = new MessageDataAdapter(getContext(), message.getMessageData(), message);


            recyclerView.setAdapter(adapter);
        } else {
            popupMessage.findViewById(R.id.linearCountDataMEssage).setVisibility(View.GONE);
            popupMessage.findViewById(R.id.linear_arg).setVisibility(View.GONE);
        }

        if (message.getMessageData() == null || message.getMessageData().isEmpty()) {
            ((ImageView) popupMessage.findViewById(R.id.imageExMessage)).setColorFilter(ContextCompat.getColor(getContext(), R.color.gray));
        } else
            ((ImageView) popupMessage.findViewById(R.id.imageExMessage)).setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimary));






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
                ShareTemplatesMessageReply.showTemplatesPopup(getActivity(), false, sTo, sName, finalReplyFrom, message, ContactTimelineDataFragment.this, true);
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


        android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        String nameS = selectedContact.get(0).getName();

        if(selectedContact.get(0).listOfContacts == null || selectedContact.get(0).listOfContacts.isEmpty()) {

        }else{

            if(selectedContact.get(0).getListOfContactInfo() != null){
                for(ContactInfo i_ : selectedContact.get(0).getListOfContactInfo()){
                    if(i_.isEmail && email_contact.contains(i_.value)){
                        nameS = selectedContact.get(0).getName();
                        break;
                    }
                }
            }


            for(Contact cc : selectedContact.get(0).listOfContacts){
                if(cc.getListOfContactInfo() != null){
                    for(ContactInfo i_ : cc.getListOfContactInfo()){
                        if(i_.isEmail && email_contact.contains(i_.value)){
                            nameS = cc.getName();
                            break;
                        }
                    }
                }
            }
        }

        fragmentTransaction.add(R.id.main_content, EmailMessageFragment.newInstance(message, email_contact, nameS, "contact")).addToBackStack("message").commit();
    }

    @Override
    public void openContactList(EmailMessage message) {

        RecyclerView recyclerView = popupContacts.findViewById(R.id.recycler_contacts_message);

        recyclerView.setLayoutManager(new LinearLayoutManager(mainView.getContext()));

        ArrayList<ContactOfMessage> listOfContactsMessage = new ArrayList<>();

        ContactOfMessage sender = new ContactOfMessage();

        ((TextView)popupContacts.findViewById(R.id.createContactMessage)).setTextColor(mainView.getContext().getResources().getColor(R.color.gray));

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


        for (int i2 = 0; i2 < message.getF_Message().getPayload().getHeaders().size(); i2++) {
            System.out.println(i2 + ". " + message.getF_Message().getPayload().getHeaders().get(i2).getValue() + ", type = " + message.getF_Message().getPayload().getHeaders().get(i2).getName());

            if (message.getF_Message().getPayload().getHeaders().get(i2).getName().equalsIgnoreCase("From") && !checkSelect) {
                String send = message.getF_Message().getPayload().getHeaders().get(i2).getValue();
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

            if (message.getF_Message().getPayload().getHeaders().get(i2).getName().equalsIgnoreCase("To")) {
                data = message.getF_Message().getPayload().getHeaders().get(i2).getValue();
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
                    if(email_contact.contains(email.trim())){

                        if(selectedContact.get(0).listOfContacts == null || selectedContact.get(0).listOfContacts.isEmpty()) {
                            contact.setName(selectedContact.get(0).getName()); //=============================set account
                        }else{

                            if(selectedContact.get(0).getListOfContactInfo() != null){
                                for(ContactInfo i_ : selectedContact.get(0).getListOfContactInfo()){
                                    if(i_.isEmail && i_.value.equals(email.trim())){
                                        contact.setName(selectedContact.get(0).getName());
                                        break;
                                    }
                                }
                            }


                            for(Contact cc : selectedContact.get(0).listOfContacts){
                                if(cc.getListOfContactInfo() != null){
                                    for(ContactInfo i_ : cc.getListOfContactInfo()){
                                        if(i_.isEmail && i_.value.equals(email.trim())){
                                            contact.setName(cc.getName());
                                            break;
                                        }
                                    }
                                }
                            }
                        }

                    }else {
                        if (mas[i].length() > 0) contact.setName(mas[i]);
                    }


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
                if(contactsOfMessageAdapter.getSelectList().size() == 1)
                    createContact(contactsOfMessageAdapter.getSelectList().get(0));
            }
        });


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

                    android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.main_content, CreateFragment.newInstance(contactExtract)).addToBackStack("create").commit();


                })
                .setNegativeButton("No", (dialog, id) -> dialog.cancel());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    //===============================================================================================================================================================

    FrameLayout popupTemplate;
    FrameLayout editPopupTemplate;
    Template selectTemplate;


    @Override
    public void ShowTemplatePopup(Template template) {

        if (popupTemplate == null)
            popupTemplate = getActivity().findViewById(R.id.popupTemplate);


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


            ((TextView) popupTemplate.findViewById(R.id.footer_message)).setText("\nbest regards, " + ((TextView) getActivity().findViewById(R.id.profileName)).getText().toString());

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

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), "Sending message...", Toast.LENGTH_SHORT).show();
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


                                            InputStream selectedFileInputStream =
                                                    getActivity().getContentResolver().openInputStream(template.getListOfFiles().get(i).getUrl());

                                            byte[] bytes = IOUtils.toByteArray(selectedFileInputStream);
                                            String path = getActivity().getFilesDir() + "/templateFiles";
                                            File f = new File(path, template.getListOfFiles().get(i).getFilename());

                                            FileOutputStream fileOutFile = new FileOutputStream(f);
                                            fileOutFile.write(bytes);
                                            fileOutFile.close();

                                            Uri contentUri = FileProvider.getUriForFile(getActivity(), "com.extime.fileprovider", f.getAbsoluteFile());
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

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), "Send success", Toast.LENGTH_SHORT).show();
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
            TemplateFileAdapter adapter = new TemplateFileAdapter(getContext(), template.getListOfFiles(), template, getActivity());
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
            TemplateFileAdapter adapter = new TemplateFileAdapter(getContext(), template.getListOfFiles(), template, getActivity());
            recyclerView.setAdapter(adapter);

        } else {
            editPopupTemplate.findViewById(R.id.linearCountDataMEssage).setVisibility(View.GONE);
            editPopupTemplate.findViewById(R.id.text_no_files_popup).setVisibility(View.VISIBLE);
        }
    }


    public void showEditTemplate(Template template) {

        if (editPopupTemplate == null) {
            editPopupTemplate = getActivity().findViewById(R.id.popupTemplateEdit);
        }

        if (!template.isTemplateUser()) {


            EditText textMessage_edit = editPopupTemplate.findViewById(R.id.textMessage_edit);

            ((TextView) editPopupTemplate.findViewById(R.id.recipient_email_edit)).setText("<" + template.getEmailDataTemplate().getSendTo() + ">");


            SpannableString text = new SpannableString("Hello " + template.getEmailDataTemplate().getSendToName() + "\n  \n\nbest regards, " + ((TextView) getActivity().findViewById(R.id.profileName)).getText().toString());

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
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
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

        MessageDataExtratorAdapter messageDataExtratorAdapter = new MessageDataExtratorAdapter(message.getMessageData(), getContext(), getActivity(), popupMessage.findViewById(R.id.createContactMessageData), popupMessage.findViewById(R.id.textCountMessageData), popupMessage.findViewById(R.id.shareMessageData), popupMessage.findViewById(R.id.hideMoreMessageData), popupMessage.findViewById(R.id.copyMessageData), popupMessage.findViewById(R.id.downloadMessageData));
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
                        getContext());
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

                            android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
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
                                ((MainActivity) getContext()).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(((MainActivity) getContext()), "Saving files...", Toast.LENGTH_SHORT).show();
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
                                ((MainActivity) getContext()).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(((MainActivity) getContext()), "Files saved", Toast.LENGTH_SHORT).show();
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

    public Time getRandomDate() {
        final Random random = new Random();
        final int millisInDay = 24 * 60 * 60 * 1000;
        return new Time((long) random.nextInt(millisInDay));
    }

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


}

