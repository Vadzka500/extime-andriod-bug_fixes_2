package ai.extime.Fragments;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.extime.R;
import com.google.android.gms.common.util.IOUtils;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Base64;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Message;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.snatik.storage.Storage;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;

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

import ai.extime.Adapters.AdapterAccountReply;
import ai.extime.Adapters.ContactsOfMessageAdapterInbox;
import ai.extime.Adapters.EmailTemplatesAdapter;
import ai.extime.Adapters.SearchContactWithEmailAdapter;
import ai.extime.Adapters.TemplateFileAdapter;
import ai.extime.Enums.ClipboardEnum;
import ai.extime.Events.CheckLinkMessageReply;
import ai.extime.Events.PopupInProfile;
import ai.extime.Events.UpdateTemplate;
import ai.extime.Interfaces.IAddRecipient;
import ai.extime.Models.Contact;
import ai.extime.Models.ContactInfo;
import ai.extime.Models.ContactOfMessage;
import ai.extime.Models.EmailMessage;
import ai.extime.Models.FilesTemplate;
import ai.extime.Models.HideContactsProfile;
import ai.extime.Models.Template;
import ai.extime.Services.ContactCacheService;
import ai.extime.Utils.ClipboardType;
import ai.extime.Utils.FileType;
import ai.extime.Utils.ShareTemplatesMessageReply;
import de.hdodenhof.circleimageview.CircleImageView;

public class ReplyFragment extends Fragment implements IAddRecipient {

    public Template template;

    private View mainView;

    private Activity activityApp;

    private EmailMessage message;

    private Toolbar mainToolBar;

    private FrameLayout accountsPopup;

    private String selectEmail;

    private static final String[] SCOPES = {
            GmailScopes.GMAIL_LABELS,
            GmailScopes.GMAIL_READONLY,
            GmailScopes.GMAIL_MODIFY
    };

    public static ReplyFragment newInstance(Template template, EmailMessage message) {
        Bundle args = new Bundle();
        args.putSerializable("template", template);
        args.putSerializable("message", message);
        ReplyFragment replyFragment = new ReplyFragment();
        replyFragment.setArguments(args);
        return replyFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            template = (Template) getArguments().getSerializable("template");
            message = (EmailMessage) getArguments().getSerializable("message");
        }

        EventBus.getDefault().post(new HideContactsProfile());

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.layout_reply_fragment, container, false);
        setHasOptionsMenu(true);


        initViews();


        initLinkPremium();


        initSender();

        return mainView;
    }

    public void initLinkPremium() {
        SharedPreferences pref_switch = activityApp.getSharedPreferences("SwitchFab", Context.MODE_PRIVATE);
        boolean switchBoolean = pref_switch.getBoolean("switchMessage", true);

        //View drawerLayout = getLayoutInflater().inflate(R.layout.drawer_layout, null);

        if (!switchBoolean) {
            mainView.findViewById(R.id.linkMessageReply).setVisibility(View.GONE);
        } else {

            ClickableSpan click_span = new ClickableSpan() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onClick(View widget) {

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("http://Extime.pro"));
                    startActivity(Intent.createChooser(intent, "Open with..."));

                }

                @Override
                public void updateDrawState(@NonNull TextPaint ds) {
                    ds.setUnderlineText(false);
                    ds.setColor(Color.parseColor("#87aade"));

                }
            };



               /* TextPaint ds = new TextPaint();
                ds.setUnderlineText(false);
                click_span.updateDrawState(ds);*/

            String text = "I create and send this email in just 3 secs \n" +
                    "Save time with personal CRM ecosystem Extime.pro - try it for free";

            SpannableStringBuilder text_ = new SpannableStringBuilder(text);

            text_.setSpan(click_span, 0, text_.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            text_.setSpan(new UnderlineSpan(), 0, text.length(), 0);

            ((TextView) mainView.findViewById(R.id.linkMessageReply)).setMovementMethod(LinkMovementMethod.getInstance());

            ((TextView) mainView.findViewById(R.id.linkMessageReply)).setText(text_);

            mainView.findViewById(R.id.linkMessageReply).setVisibility(View.VISIBLE);

           /* Pattern pattern = Pattern.compile("http://Extime.pro");
            Linkify.addLinks(((TextView)mainView.findViewById(R.id.linkMessageReply)), pattern, "http://");
            ((TextView)mainView.findViewById(R.id.linkMessageReply)).setText(Html.fromHtml("<a href='http://Extime.pro'>http://123123123</a>"));*/


            //text.setSpan(click_span, startI, startI + template.getEmailDataTemplate().getSendToName().get(0).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            //((TextView) mainView.findViewById(R.id.linkMessageReply)).htm
        }
    }

    public String sortPopupContact = "sortByDescTime";

    public boolean sortTimePopup = false;

    public boolean sortTimeContactPopup = false;

    public boolean sortPopulPopup = false;

    public SearchContactWithEmailAdapter searchContactWithEmailAdapter;

    public AdapterAccountReply contactsOfMessageAdapter;

    public void initRecyclerContacts() {
        RecyclerView recyclerView = accountsPopup.findViewById(R.id.recycler_contacts_message_reply);

        recyclerView.setLayoutManager(new LinearLayoutManager(mainView.getContext()));


        SharedPreferences mSettings;
        mSettings = getActivity().getSharedPreferences("accountUser", Context.MODE_PRIVATE);
        Set<String> setAcc = mSettings.getStringSet("accounts", null);


        ArrayList<ContactOfMessage> senders = new ArrayList<>();

        for (int i = 0; i < template.getEmailDataTemplate().getSendTo().size(); i++) {

            if(template.getEmailDataTemplate().getSendTo().get(i) == null) continue;

            ContactOfMessage recipient = new ContactOfMessage();
            recipient.setRecipient();
            recipient.setName(template.getEmailDataTemplate().getSendToName().get(i));
            recipient.setEmail(template.getEmailDataTemplate().getSendTo().get(i));
            recipient.setSendCheck(template.getEmailDataTemplate().getSend().get(i));


            if (setAcc != null)
                if (setAcc.contains(recipient.getEmail())) recipient.setGoogle(true);


            senders.add(recipient);
        }

        Account[] accounts = AccountManager.get(activityApp).getAccountsByType("com.google");

        ContactOfMessage main_sender = new ContactOfMessage();
        main_sender.setGoogle(true);
        main_sender.setSender();
        main_sender.setEmail(template.getEmailDataTemplate().getRecipient());

        for (Account account : accounts) {
            ContactOfMessage send = new ContactOfMessage();
            if (setAcc.contains(account.name)) send.setGoogle(true);

            send.setSender();
            send.setEmail(account.name);

            main_sender.addAcc(send);
        }

        senders.add(0, main_sender);

        int countFind = 0;

        for (ContactOfMessage cf : senders) {
            Contact c = ContactCacheService.find2(cf.getEmail(), ClipboardEnum.EMAIL, activityApp);
            if (c != null) {
                cf.setSearchContact(c);
                countFind++;
            }
        }

        ((TextView) mainView.findViewById(R.id.textSelectMEssReply)).setText(String.valueOf(senders.size()) + " contacts: " + countFind + " existing, " + String.valueOf(senders.size() - countFind) + " new");


        contactsOfMessageAdapter = new AdapterAccountReply(mainView.getContext(), senders, null);

        recyclerView.setAdapter(contactsOfMessageAdapter);
    }

    ArrayList<Contact> listContactWithEmail;

    public static boolean openPreview;

    public void initSender() {
        mainView.findViewById(R.id.firstC).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (listContactWithEmail == null)
                    listContactWithEmail = new ArrayList<>();


                initRecyclerContacts();


                mainView.findViewById(R.id.popupSelectContactReply).setVisibility(View.VISIBLE);

                mainView.findViewById(R.id.popupSelectContactReply).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (openPreview) {
                            openPreview = false;
                            EventBus.getDefault().post(new HideContactsProfile());
                            return;
                        }


                        if (mainView.findViewById(R.id.popup_mess_search).getVisibility() == View.VISIBLE)
                            mainView.findViewById(R.id.popup_mess_search).setVisibility(View.GONE);
                        else
                            mainView.findViewById(R.id.popupSelectContactReply).setVisibility(View.GONE);
                    }
                });


                mainView.findViewById(R.id.popupSelectContactReply).findViewById(R.id.calcelMessageReply).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mainView.findViewById(R.id.popupSelectContactReply).setVisibility(View.GONE);
                    }
                });

                if (listContactWithEmail.isEmpty())
                    listContactWithEmail = ContactCacheService.getListWithEmail();

                ArrayList<Contact> finalListContactWithEmail = listContactWithEmail;
                ArrayList<Contact> finalListContactWithEmail1 = listContactWithEmail;
                mainView.findViewById(R.id.addContactReply).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ((EditText) mainView.findViewById(R.id.search_contact_reply)).setText("");

                        RecyclerView recyclerView1 = mainView.findViewById(R.id.recycler_contacts_message_reply_search);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mainView.getContext());
                        recyclerView1.setLayoutManager(mLayoutManager);
                        searchContactWithEmailAdapter = new SearchContactWithEmailAdapter(finalListContactWithEmail, activityApp, "", ReplyFragment.this);
                        recyclerView1.setAdapter(searchContactWithEmailAdapter);

                        mainView.findViewById(R.id.popup_mess_search).setVisibility(View.VISIBLE);

                        if (sortPopupContact.equals("sortByAsc")) {
                            searchContactWithEmailAdapter.sortCompanybyAsc();
                            //selectPositionAdapter.sortPosByAsc();
                            ((TextView) mainView.findViewById(R.id.popupSelectContactReply).findViewById(R.id.sortTextPopup)).setTextColor(getActivity().getResources().getColor(R.color.primary));
                            ((ImageView) mainView.findViewById(R.id.popupSelectContactReply).findViewById(R.id.timeSortPopup)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
                            ((ImageView) mainView.findViewById(R.id.popupSelectContactReply).findViewById(R.id.populSort)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
                            ((TextView) mainView.findViewById(R.id.popupSelectContactReply).findViewById(R.id.sortTextPopup)).setText("A-Z");
                            sortTimeContactPopup = false;
                        } else if (sortPopupContact.equals("sortByDesc")) {
                            ((TextView) mainView.findViewById(R.id.popupSelectContactReply).findViewById(R.id.sortTextPopup)).setTextColor(getActivity().getResources().getColor(R.color.primary));
                            ((ImageView) mainView.findViewById(R.id.popupSelectContactReply).findViewById(R.id.timeSortPopup)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
                            ((ImageView) mainView.findViewById(R.id.popupSelectContactReply).findViewById(R.id.populSort)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
                            ((TextView) mainView.findViewById(R.id.popupSelectContactReply).findViewById(R.id.sortTextPopup)).setText("Z-A");
                            searchContactWithEmailAdapter.sortCompanybyDesc();
                            //selectPositionAdapter.sortPosByDesc();
                        } else if (sortPopupContact.equals("sortByAscTime")) {
                            ((TextView) mainView.findViewById(R.id.popupSelectContactReply).findViewById(R.id.sortTextPopup)).setTextColor(getActivity().getResources().getColor(R.color.gray));
                            ((ImageView) mainView.findViewById(R.id.popupSelectContactReply).findViewById(R.id.timeSortPopup)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));
                            ((ImageView) mainView.findViewById(R.id.popupSelectContactReply).findViewById(R.id.populSort)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
                            searchContactWithEmailAdapter.sortByAscTime();
                            sortPopupContact = "sortByAscTime";
                            sortTimeContactPopup = true;
                        } else if (sortPopupContact.equals("sortByDescTime")) {
                            ((TextView) mainView.findViewById(R.id.popupSelectContactReply).findViewById(R.id.sortTextPopup)).setTextColor(getActivity().getResources().getColor(R.color.gray));
                            ((ImageView) mainView.findViewById(R.id.popupSelectContactReply).findViewById(R.id.timeSortPopup)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));
                            ((ImageView) mainView.findViewById(R.id.popupSelectContactReply).findViewById(R.id.populSort)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
                            searchContactWithEmailAdapter.sortByDescTime();
                            sortPopupContact = "sortByDescTime";
                            sortTimeContactPopup = false;
                        }


                        mainView.findViewById(R.id.popupSelectContactReply).findViewById(R.id.sortElementsPopup).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (((TextView) mainView.findViewById(R.id.popupSelectContactReply).findViewById(R.id.sortTextPopup)).getText().toString().equals("A-Z")) {

                                    ((TextView) mainView.findViewById(R.id.popupSelectContactReply).findViewById(R.id.sortTextPopup)).setTextColor(getActivity().getResources().getColor(R.color.primary));
                                    ((ImageView) mainView.findViewById(R.id.popupSelectContactReply).findViewById(R.id.timeSortPopup)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
                                    ((ImageView) mainView.findViewById(R.id.popupSelectContactReply).findViewById(R.id.populSort)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
                                    ((TextView) mainView.findViewById(R.id.popupSelectContactReply).findViewById(R.id.sortTextPopup)).setText("Z-A");
                                    searchContactWithEmailAdapter.sortCompanybyDesc();
                                    //selectPositionAdapter.sortPosByDesc();
                                    sortPopupContact = "sortByDesc";

                                      /*  SharedPreferences.Editor editor = mSettings.edit();
                                        editor.putString("Sort", "sortByDesc");
                                        editor.commit();*/


                                } else {
                                    ((TextView) mainView.findViewById(R.id.popupSelectContactReply).findViewById(R.id.sortTextPopup)).setTextColor(getActivity().getResources().getColor(R.color.primary));
                                    ((ImageView) mainView.findViewById(R.id.popupSelectContactReply).findViewById(R.id.timeSortPopup)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
                                    ((ImageView) mainView.findViewById(R.id.popupSelectContactReply).findViewById(R.id.populSort)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
                                    ((TextView) mainView.findViewById(R.id.popupSelectContactReply).findViewById(R.id.sortTextPopup)).setText("A-Z");
                                    searchContactWithEmailAdapter.sortCompanybyAsc();
                                    //selectPositionAdapter.sortPosByAsc();
                                    sortPopupContact = "sortByAsc";

                                      /*  SharedPreferences.Editor editor = mSettings.edit();
                                        editor.putString("Sort", "sortByAsc");
                                        editor.commit();*/


                                }
                            }
                        });

                        mainView.findViewById(R.id.popupSelectContactReply).findViewById(R.id.sortByTimePopup).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (!sortTimeContactPopup) {
                                    ((TextView) mainView.findViewById(R.id.popupSelectContactReply).findViewById(R.id.sortTextPopup)).setTextColor(getActivity().getResources().getColor(R.color.gray));
                                    ((ImageView) mainView.findViewById(R.id.popupSelectContactReply).findViewById(R.id.timeSortPopup)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));
                                    ((ImageView) mainView.findViewById(R.id.popupSelectContactReply).findViewById(R.id.populSort)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
                                    searchContactWithEmailAdapter.sortByAscTime();
                                    sortPopupContact = "sortByAscTime";
                                    sortTimeContactPopup = true;

                                        /*SharedPreferences.Editor editor = mSettings.edit();
                                        editor.putString("Sort", "sortByAscTime");
                                        editor.commit();*/

                                } else {
                                    ((TextView) mainView.findViewById(R.id.popupSelectContactReply).findViewById(R.id.sortTextPopup)).setTextColor(getActivity().getResources().getColor(R.color.gray));
                                    ((ImageView) mainView.findViewById(R.id.popupSelectContactReply).findViewById(R.id.timeSortPopup)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));
                                    ((ImageView) mainView.findViewById(R.id.popupSelectContactReply).findViewById(R.id.populSort)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
                                    searchContactWithEmailAdapter.sortByDescTime();
                                    sortPopupContact = "sortByDescTime";
                                    sortTimeContactPopup = false;

                                       /* SharedPreferences.Editor editor = mSettings.edit();
                                        editor.putString("Sort", "sortByDescTime");
                                        editor.commit();*/

                                }
                            }
                        });

                        mainView.findViewById(R.id.popupSelectContactReply).findViewById(R.id.sortByPopupPopup).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                            }
                        });


                        ((EditText) mainView.findViewById(R.id.search_contact_reply)).addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {

                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                                ArrayList<Contact> listC = new ArrayList<>();
                                for (Contact ct : finalListContactWithEmail1) {
                                    if (ct.getName().toLowerCase().contains(s.toString().toLowerCase())) {
                                        listC.add(ct);
                                        //continue;
                                    }

                                    /*if(ct.getCompany() != null && ct.getCompany().contains(s.toString())){
                                        listC.add(ct);
                                        continue;
                                    }
                                    if(ct.getCompanyPossition() != null && ct.getCompanyPossition().contains(s.toString())){
                                        listC.add(ct);
                                        continue;
                                    }
                                    if(ct.getListOfContactInfo() != null){
                                        for(ContactInfo i : ct.getListOfContactInfo()){
                                            if(i.isEmail){
                                                if(i.value.contains(s.toString())){
                                                    listC.add(ct);
                                                    break;
                                                }
                                            }
                                        }
                                    }*/

                                }


                                searchContactWithEmailAdapter.updateSearch(s.toString(), listC);

                                if (sortPopupContact.equals("sortByAsc")) {
                                    searchContactWithEmailAdapter.sortCompanybyAsc();
                                    //selectPositionAdapter.sortPosByAsc();
                                    ((TextView) mainView.findViewById(R.id.popupSelectContactReply).findViewById(R.id.sortTextPopup)).setTextColor(getActivity().getResources().getColor(R.color.primary));
                                    ((ImageView) mainView.findViewById(R.id.popupSelectContactReply).findViewById(R.id.timeSortPopup)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
                                    ((ImageView) mainView.findViewById(R.id.popupSelectContactReply).findViewById(R.id.populSort)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
                                    ((TextView) mainView.findViewById(R.id.popupSelectContactReply).findViewById(R.id.sortTextPopup)).setText("A-Z");
                                    sortTimeContactPopup = false;
                                } else if (sortPopupContact.equals("sortByDesc")) {
                                    ((TextView) mainView.findViewById(R.id.popupSelectContactReply).findViewById(R.id.sortTextPopup)).setTextColor(getActivity().getResources().getColor(R.color.primary));
                                    ((ImageView) mainView.findViewById(R.id.popupSelectContactReply).findViewById(R.id.timeSortPopup)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
                                    ((ImageView) mainView.findViewById(R.id.popupSelectContactReply).findViewById(R.id.populSort)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
                                    ((TextView) mainView.findViewById(R.id.popupSelectContactReply).findViewById(R.id.sortTextPopup)).setText("Z-A");
                                    searchContactWithEmailAdapter.sortCompanybyDesc();
                                    //selectPositionAdapter.sortPosByDesc();
                                } else if (sortPopupContact.equals("sortByAscTime")) {
                                    ((TextView) mainView.findViewById(R.id.popupSelectContactReply).findViewById(R.id.sortTextPopup)).setTextColor(getActivity().getResources().getColor(R.color.gray));
                                    ((ImageView) mainView.findViewById(R.id.popupSelectContactReply).findViewById(R.id.timeSortPopup)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));
                                    ((ImageView) mainView.findViewById(R.id.popupSelectContactReply).findViewById(R.id.populSort)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
                                    searchContactWithEmailAdapter.sortByAscTime();
                                    sortPopupContact = "sortByAscTime";
                                    sortTimeContactPopup = true;
                                } else if (sortPopupContact.equals("sortByDescTime")) {
                                    ((TextView) mainView.findViewById(R.id.popupSelectContactReply).findViewById(R.id.sortTextPopup)).setTextColor(getActivity().getResources().getColor(R.color.gray));
                                    ((ImageView) mainView.findViewById(R.id.popupSelectContactReply).findViewById(R.id.timeSortPopup)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));
                                    ((ImageView) mainView.findViewById(R.id.popupSelectContactReply).findViewById(R.id.populSort)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
                                    searchContactWithEmailAdapter.sortByDescTime();
                                    sortPopupContact = "sortByDescTime";
                                    sortTimeContactPopup = false;
                                }


                            }
                        });
                    }
                });

                mainView.findViewById(R.id.confirmContactMessage).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (contactsOfMessageAdapter.getListRe().isEmpty()) {
                            Toast.makeText(activityApp, "Choose sender", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        template.getEmailDataTemplate().setRecipient(contactsOfMessageAdapter.getEmail());

                        selectEmail = contactsOfMessageAdapter.getEmail();

                        ArrayList<ContactOfMessage> lll = contactsOfMessageAdapter.getListRe();

                        for (int i = 0; i < template.getEmailDataTemplate().getSendTo().size(); i++) {
                            boolean check = false;
                            for (ContactOfMessage co : lll) {
                                if (co.getEmail().equals(template.getEmailDataTemplate().getSendTo().get(i))) {
                                    check = true;
                                    break;
                                }
                            }
                            template.getEmailDataTemplate().getSend().set(i, check);
                        }

                       /* GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(
                                activityApp.getApplicationContext(), Arrays.asList(SCOPES))
                                .setBackOff(new ExponentialBackOff());

                        credential.setSelectedAccountName(template.getEmailDataTemplate().getRecipient());*/

                        System.out.println("CRED = " + template.getEmailDataTemplate().getRecipient());

                        template.getEmailDataTemplate().setAccountCredential(null);

                        mainView.findViewById(R.id.popupSelectContactReply).setVisibility(View.GONE);
                        initSender_recipient();
                    }
                });


            }
        });


    }

    public void initRecycler() {
        if (template.getListOfFiles() != null && !template.getListOfFiles().isEmpty()) {
            mainView.findViewById(R.id.linearCountDataMEssage).setVisibility(View.VISIBLE);
            mainView.findViewById(R.id.text_no_files_popup).setVisibility(View.GONE);

            RecyclerView recyclerView = mainView.findViewById(R.id.linear_arg_);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            TemplateFileAdapter adapter = new TemplateFileAdapter(getContext(), template.getListOfFiles(), template, activityApp);
            recyclerView.setAdapter(adapter);

        } else {
            mainView.findViewById(R.id.linearCountDataMEssage).setVisibility(View.GONE);
            mainView.findViewById(R.id.text_no_files_popup).setVisibility(View.VISIBLE);
        }

    }

    public void initViews() {

        accountsPopup = mainView.findViewById(R.id.popupSelectContactReply);

        if (message != null) {
            mainView.findViewById(R.id.prevEmailTemplate).setVisibility(View.VISIBLE);
        } else {
            mainView.findViewById(R.id.prevEmailTemplate).setVisibility(View.GONE);
        }

        if (mainToolBar != null)
            ((ImageView) mainToolBar.findViewById(R.id.more_templates_toolbar)).setImageDrawable(activityApp.getResources().getDrawable(R.drawable.arr_down));

        activityApp.findViewById(R.id.main_content_ll).setVisibility(View.GONE);

        initSender_recipient();
        initTimeMessage();
        initMessage_title();
        initRecycler();

        activityApp.findViewById(R.id.fabMenuContainer).setVisibility(View.GONE);

        try {
            if (!template.isTemplateUser() && template.getTitle().equalsIgnoreCase("New mail")) {
                ((TextView) mainToolBar.findViewById(R.id.toolbar_title)).setText("New mail");
            } else if (!template.isTemplateUser() && template.getTitle().equalsIgnoreCase("Reply")) {
                ((TextView) mainToolBar.findViewById(R.id.toolbar_title)).setText("Reply");
            } else {
                //((TextView) mainToolBar.findViewById(R.id.toolbar_title)).setText("Re: " + template.getTitle());

                if (message == null)
                    ((TextView) mainToolBar.findViewById(R.id.toolbar_title)).setText("" + template.getTitle());
                else
                    ((TextView) mainToolBar.findViewById(R.id.toolbar_title)).setText("Re: " + template.getTitle());

            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        ((EditText) mainView.findViewById(R.id.textMessage_edit)).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ((EditText) mainView.findViewById(R.id.textMessage_edit)).getParent().requestDisallowInterceptTouchEvent(false);
                return false;
            }
        });

        mainView.findViewById(R.id.frame_select_template_reply).setVisibility(View.GONE);

        mainView.findViewById(R.id.selectFileProfileTemplate).setOnClickListener(new View.OnClickListener() {
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


        if (message != null) {
            if (message.getMessage().contains("<br>"))
                ((TextView) mainView.findViewById(R.id.textReplyOld)).setText(Html.fromHtml(message.getMessage()));
            else
                ((TextView) mainView.findViewById(R.id.textReplyOld)).setText(message.getMessage());
        }


        ((TextView) mainView.findViewById(R.id.textReplyOld)).setMovementMethod(LinkMovementMethod.getInstance());

        ((TextView) mainView.findViewById(R.id.textReplyOld)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("CLICK TEXT");
                //((TextView) mainView.findViewById(R.id.textMessage)).clearFocus();
            }
        });

        ((TextView) mainView.findViewById(R.id.textReplyOld)).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                return false;
            }
        });


        if (message != null) {
            try {
                Calendar current = Calendar.getInstance();
                Calendar contactDate = Calendar.getInstance();
                current.setTime(new Date());
                contactDate.setTime(message.getDate());
                String timeStr = "";
                Calendar cal1 = Calendar.getInstance();
                cal1.setTime(message.getDate());
                Time time = getRandomDate();
                time.setHours(cal1.get(Calendar.HOUR));
                time.setMinutes(cal1.get(Calendar.MINUTE));
                time.setSeconds(cal1.get(Calendar.SECOND));


                String s = "On ";
                s += contactDate.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.ENGLISH);
                s += ", ";
                s += contactDate.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.ENGLISH) + " " + contactDate.get(Calendar.DAY_OF_MONTH);
                s += ", " + contactDate.get(Calendar.YEAR);
                s += " at ";
                s += time.getHours() + ":" + time.getMinutes();
                if (cal1.get(Calendar.HOUR_OF_DAY) > 12) {
                    s += " PM";
                } else s += " AM";


                ((TextView) mainView.findViewById(R.id.timeReplySender)).setText(s);
            } catch (Exception e) {
                System.out.println("ERROR TO GET TIME Contact Adapter");
            }

            String textS = message.getAccount().getName() + " < " + message.getAccount().getEmail() + " > wrote:";

            String q = "< " + message.getAccount().getEmail() + " >";

            int startI = textS.indexOf("<");
            //System.out.println("POS = "+startI+", qq = "+q.length());
            final SpannableStringBuilder text = new SpannableStringBuilder(textS);
            final ForegroundColorSpan style = new ForegroundColorSpan(Color.parseColor("#567bac"));

            text.setSpan(style, startI, startI + q.length(), Spannable.SPAN_COMPOSING);

            ((TextView) mainView.findViewById(R.id.titleBodySenderReplySender)).setText(text);


        }

        String textBody = template.getTemplateText();
        if (textBody == null) {
            template.setTemplateText("Hello %Name\n\n\n\nbest regards,\n" + ((TextView) activityApp.findViewById(R.id.profileName)).getText());
            textBody = template.getTemplateText();
        }

        SpannableStringBuilder text = new SpannableStringBuilder(textBody);

        EditText textUser = ((EditText) mainView.findViewById(R.id.textMessage_edit));

        final boolean[] checkC = {false};

        if (message != null) {
            if (!ClipboardType.isEmail(message.getAccount().getName())) {
                textBody = textBody.replaceAll("%Name", message.getAccount().getName());
                textBody = textBody.replaceAll("%name", message.getAccount().getName());

                int startI = textBody.indexOf(message.getAccount().getName());
                text = new SpannableStringBuilder(textBody);
                final ForegroundColorSpan style = new ForegroundColorSpan(Color.parseColor("#87aade"));
                text.setSpan(style, startI, startI + message.getAccount().getName().length(), Spannable.SPAN_COMPOSING);

                /*ClickableSpan click_span = new ClickableSpan() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(View widget) {

                        //checkC[0] = true;
                        System.out.println("CLICK SPAN 2");

                        //textUser.setShowSoftInputOnFocus(false);


                        //textUser.callOnClick();

                       *//* if(textUser.hasFocus()){
                            System.out.println("FOC");
                            InputMethodManager imm = (InputMethodManager)activityApp.getSystemService(Service.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(textUser, 0);
                        }


                        activityApp.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                        InputMethodManager inputMethodManager = (InputMethodManager) activityApp.getSystemService(Activity.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(activityApp.getCurrentFocus().getWindowToken(), 0);
                        textUser.clearFocus();*//*
                    }

                    @Override
                    public void updateDrawState(@NonNull TextPaint ds) {
                        ds.setUnderlineText(false);
                        ds.setColor(Color.parseColor("#87aade"));

                    }
                };
*/


              /*  TextPaint ds = new TextPaint();
                ds.setUnderlineText(false);
                click_span.updateDrawState(ds);*/


                //text.setSpan(click_span, startI, startI + template.getEmailDataTemplate().getSendToName().get(0).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            } else {
                textBody = textBody.replaceAll("%Name", "");
                textBody = textBody.replaceAll("%name", "");
                text = new SpannableStringBuilder(textBody);
            }
        } else {
            if (!ClipboardType.isEmail(template.getEmailDataTemplate().getSendToName().get(0))) {
                textBody = textBody.replaceAll("%Name", template.getEmailDataTemplate().getSendToName().get(0));
                textBody = textBody.replaceAll("%name", template.getEmailDataTemplate().getSendToName().get(0));

                int startI = textBody.indexOf(template.getEmailDataTemplate().getSendToName().get(0));
                text = new SpannableStringBuilder(textBody);
                final ForegroundColorSpan style = new ForegroundColorSpan(Color.parseColor("#87aade"));
                text.setSpan(style, startI, startI + template.getEmailDataTemplate().getSendToName().get(0).length(), Spannable.SPAN_COMPOSING);




              /*  ClickableSpan click_span = new ClickableSpan() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(View widget) {

                        //checkC[0] = true;
                        System.out.println("CLICK SPAN 2");

                        //textUser.setShowSoftInputOnFocus(false);


                        //textUser.callOnClick();

                       *//* if(textUser.hasFocus()){
                            System.out.println("FOC");
                            InputMethodManager imm = (InputMethodManager)activityApp.getSystemService(Service.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(textUser, 0);
                        }


                        activityApp.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                        InputMethodManager inputMethodManager = (InputMethodManager) activityApp.getSystemService(Activity.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(activityApp.getCurrentFocus().getWindowToken(), 0);
                        textUser.clearFocus();*//*
                    }

                    @Override
                    public void updateDrawState(@NonNull TextPaint ds) {
                        ds.setUnderlineText(false);
                        ds.setColor(Color.parseColor("#87aade"));

                    }
                };*/



              /*  TextPaint ds = new TextPaint();
                ds.setUnderlineText(false);
                click_span.updateDrawState(ds);*/


                //text.setSpan(click_span, startI, startI + template.getEmailDataTemplate().getSendToName().get(0).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            } else {
                textBody = textBody.replaceAll("%Name", "");
                textBody = textBody.replaceAll("%name", "");
                text = new SpannableStringBuilder(textBody);
            }
        }


        /*UnderlineSpan[] uspans = text.getSpans(0, text.length(), UnderlineSpan.class);
        for (UnderlineSpan us : uspans) {
            text.removeSpan(us);
        }*/


        //textUser.setMovementMethod(LinkMovementMethod.getInstance());

        /*textUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("EDIT CLICK");
                //textUser.setInputType(InputType.TYPE_NULL);




               *//* if(checkC[0]) {
                    if(textUser.getShowSoftInputOnFocus()) {
                        textUser.setShowSoftInputOnFocus(false);
                    }
                }
                else {
                    if(!textUser.getShowSoftInputOnFocus()) {
                        textUser.setShowSoftInputOnFocus(true);
                    }
                }

                checkC[0] = false;*//*
            }
        });*/


        textUser.setText(text);


    }

    public ArrayList<Template> getData() {

        ArrayList list = new ArrayList<>();

        Storage storage = new Storage(activityApp.getApplicationContext());
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        String content = null;
        try {
            content = storage.readTextFile(path + "/Extime/ExtimeContacts/backupTemplates");
        } catch (Exception e) {
            e.printStackTrace();

        }

        if (content == null) {

            System.out.println("template null");

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

            System.out.println("template get");

            Gson gson = new Gson();


            list.addAll(gson.fromJson(content, new TypeToken<ArrayList<Template>>() {
            }.getType()));

        }
        return list;
    }

    public void initTemplatesAdapter() {

        ArrayList<Template> list = new ArrayList<>();

        if (message != null) {
            Template new_template = new Template("Reply", false, null);
            new_template.setTemplateText("Hello %Name\n\n\n\nbest regards,\n" + ((TextView) activityApp.findViewById(R.id.profileName)).getText());
            list.add(new_template);
        } else {
            Template new_template = new Template("New mail", false, null);
            new_template.setTemplateText("Hello %Name\n\n\n\nbest regards,\n" + ((TextView) activityApp.findViewById(R.id.profileName)).getText());
            list.add(new_template);
        }

        list.addAll(getData());


        EmailTemplatesAdapter emailTemplatesAdapter = new EmailTemplatesAdapter(list, null, mainView.getContext(), ReplyFragment.this);
        RecyclerView recyclerView = mainView.findViewById(R.id.recycler_t);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(emailTemplatesAdapter);

        //((TextView) mainView.findViewById(R.id.count_template_reply)).setText("select from " + list.size() + " templates");


       /* mainView.findViewById(R.id.closer_emails).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideTemplatePopup();
            }
        });*/
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        mainToolBar = (Toolbar) activityApp.findViewById(R.id.main_toolbar);

        if (!template.isTemplateUser() && template.getTitle().equalsIgnoreCase("New mail")) {
            ((TextView) mainToolBar.findViewById(R.id.toolbar_title)).setText("New mail");

          /*  txtSearch.setFocusableInTouchMode(true);
            txtSearch.setFocusable(true);
            txtSearch.requestFocus();*/

        } else if (!template.isTemplateUser() && template.getTitle().equalsIgnoreCase("Reply")) {
            ((TextView) mainToolBar.findViewById(R.id.toolbar_title)).setText("Reply");
        } else {
            if (message == null)
                ((TextView) mainToolBar.findViewById(R.id.toolbar_title)).setText("" + template.getTitle());
            else
                ((TextView) mainToolBar.findViewById(R.id.toolbar_title)).setText("Re: " + template.getTitle());
        }

        ((ImageView) mainToolBar.findViewById(R.id.more_templates_toolbar)).setImageDrawable(activityApp.getResources().getDrawable(R.drawable.arr_down));

        mainToolBar.findViewById(R.id.linear_toolbar_title).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mainView.findViewById(R.id.frame_select_template_reply).getVisibility() == View.GONE) {
                    initTemplatesAdapter();

                    ((ImageView) mainToolBar.findViewById(R.id.more_templates_toolbar)).setImageDrawable(activityApp.getResources().getDrawable(R.drawable.arr_up));

                    mainView.findViewById(R.id.frame_select_template_reply).setVisibility(View.VISIBLE);
                    mainView.findViewById(R.id.frame_select_template_reply).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mainView.findViewById(R.id.frame_select_template_reply).setVisibility(View.GONE);
                            ((ImageView) mainToolBar.findViewById(R.id.more_templates_toolbar)).setImageDrawable(activityApp.getResources().getDrawable(R.drawable.arr_down));
                        }
                    });
                } else {
                    mainView.findViewById(R.id.frame_select_template_reply).setVisibility(View.GONE);
                    ((ImageView) mainToolBar.findViewById(R.id.more_templates_toolbar)).setImageDrawable(activityApp.getResources().getDrawable(R.drawable.arr_down));
                }
            }
        });


        mainToolBar.findViewById(R.id.more_templates_toolbar).setVisibility(View.VISIBLE);


        mainToolBar.findViewById(R.id.toolbar_share).setVisibility(View.GONE);
        mainToolBar.findViewById(R.id.toolbar_menu).setVisibility(View.GONE);
        mainToolBar.findViewById(R.id.toolbar_kanban_clicker).setVisibility(View.GONE);

        mainToolBar.findViewById(R.id.toolbar_plane).setVisibility(View.VISIBLE);
        mainToolBar.findViewById(R.id.toolbar_edit).setVisibility(View.GONE);
        mainToolBar.findViewById(R.id.toolbar_menu).setVisibility(View.VISIBLE);


        mainToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.support.v4.app.FragmentManager fragmentManager = ((FragmentActivity) activityApp).getSupportFragmentManager();
                fragmentManager.popBackStack();
            }
        });


        //menu.findItem(R.id.share_profile).setVisible(false);

        menu.findItem(R.id.menu_profile).setVisible(false);
        menu.findItem(R.id.action_favorite).setVisible(false);
        mainToolBar.setNavigationIcon(R.drawable.icn_arrow_back);

        mainToolBar.findViewById(R.id.toolbar_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mainToolBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mainToolBar.findViewById(R.id.toolbar_plane).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        mainToolBar.findViewById(R.id.toolbar_plane).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //if (template.getEmailDataTemplate() != null) {


                new Thread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void run() {


                        try {
                            if(template.getEmailDataTemplate().getSendTo() == null || template.getEmailDataTemplate().getSendTo().size() == 0){

                                activityApp.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(activityApp, "Select recipient", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                return;
                            }

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


                            for (int i = 0; i < template.getEmailDataTemplate().getSendTo().size(); i++) {
                                if (template.getEmailDataTemplate().getSend().get(i)) {
                                    email.addRecipient(javax.mail.Message.RecipientType.TO,
                                            new InternetAddress(template.getEmailDataTemplate().getSendTo().get(i)));
                                }
                            }

                            /*for(String s : template.getEmailDataTemplate().getSendTo()) {

                            }*/


                            //=============

                            if (message != null && template.getEmailDataTemplate().getAccountCredential() != null) {

                                String d = "";
                                String s = "";


                                for (int i2 = 0; i2 < message.getListHeaders().size(); i2++) {
                                    System.out.println(i2 + ". " + message.getListHeaders().get(i2).getValue() + ", type = " + message.getListHeaders().get(i2).getName());

                                    if (message.getListHeaders().get(i2).getName().equalsIgnoreCase("Message-ID")) {

                                        d = message.getListHeaders().get(i2).getValue();

                                    }

                                    if (message.getListHeaders().get(i2).getName().equalsIgnoreCase("Subject")) {

                                        s = message.getListHeaders().get(i2).getValue();

                                    }

                                }

                                //email.setSubject(s);

                                email.addHeader("In-Reply-To", d);
                                email.addHeader("References", d);

                            }


                            //=================


                            //email.addRecipient(javax.mail.Message.RecipientType.TO,
                            //        new InternetAddress("andreww.pataletaa@gmail.com"));


                            if (((EditText) mainView.findViewById(R.id.titleMessage)).getText().toString().trim().length() == 0) {
                                email.setSubject("No subject");
                            } else {
                                email.setSubject(((EditText) mainView.findViewById(R.id.titleMessage)).getText().toString());
                            }

                            MimeBodyPart mimeBodyPart = new MimeBodyPart();
                            //MimeBodyPart mimeBodyPart_link = new MimeBodyPart();

                            String text = ((EditText) mainView.findViewById(R.id.textMessage_edit)).getText().toString();


                            SharedPreferences pref_switch = activityApp.getSharedPreferences("SwitchFab", Context.MODE_PRIVATE);
                            boolean switchBoolean = pref_switch.getBoolean("switchMessage", true);

                            if (switchBoolean) {
                                text += "<br><br><a href='https://extime.pro/' target='_blank'>I create and send this email in just 3 secs<br>Save time with personal CRM ecosystem Extime.pro - try it for free </a>";
                            }


                            text = text.replaceAll("\n", "<br>");

                            mimeBodyPart.setContent(text, "text/html; charset=utf-8");

                            //mimeBodyPart_link.setContent(text_link, "text/html; charset=utf-8");


                                /*if(template.getTemplateText() == null || template.getTemplateText().isEmpty()) {
                                    System.out.println("TExt Send 1");
                                    String text = ((TextView) mainView.findViewById(R.id.textMessage_1)).getText().toString() + " ";
                                    text += ((TextView) mainView.findViewById(R.id.textMessage_2)).getText().toString() + "\n";
                                    text += ((TextView) mainView.findViewById(R.id.textMessage_p)).getText().toString() + "\n";
                                    text += ((TextView) mainView.findViewById(R.id.footer_message)).getText().toString();

                                    mimeBodyPart.setContent(text, "text/plain; charset=utf-8");
                                }else{
                                    System.out.println("TExt Send 2");

                                    mimeBodyPart.setContent(template.getTemplateText(), "text/plain; charset=utf-8");
                                }*/


                            Multipart multipart = new MimeMultipart();
                            multipart.addBodyPart(mimeBodyPart);
                            //multipart.addBodyPart(mimeBodyPart_link);


                            if (template.getListOfFiles() != null) {
                                for (int i = 0; i < template.getListOfFiles().size(); i++) {

                                    try {
                                        if (template.getListOfFiles().get(i).getFinalUrl() == null) {


                                           /* InputStream selectedFileInputStream =
                                                    activityApp.getContentResolver().openInputStream(template.getListOfFiles().get(i).getUrl());*/


                                            Storage storage = new Storage(activityApp);

                                            File f2 = storage.getFile(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Extime/TemplatesFiles/" + template.getListOfFiles().get(i).getFilename());

                                            InputStream selectedFileInputStream =
                                                    activityApp.getContentResolver().openInputStream(Uri.fromFile(f2));

                                            byte[] bytes = IOUtils.toByteArray(selectedFileInputStream);
                                            String path = activityApp.getFilesDir() + "/templateFiles";

                                            File folder = new File(path);
                                            if (!folder.exists()) {
                                                folder.mkdir();
                                            }

                                            File f = new File(path, template.getListOfFiles().get(i).getFilename());

                                            System.out.println("FULL PATH = "+f.getAbsolutePath());

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

                            Message message_ = new Message();

                            if (message != null && template.getEmailDataTemplate().getAccountCredential() != null)
                                message_.setThreadId(message.getThreadID());


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

                            message_.setRaw(encodedEmail);


                            HttpTransport transport = AndroidHttp.newCompatibleTransport();
                            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();


                            GoogleAccountCredential cred;
                            /*if (template.getEmailDataTemplate().getAccountCredential() != null) {
                                cred = template.getEmailDataTemplate().getAccountCredential().getCredential();
                            } else {*/
                                GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(
                                        activityApp.getApplicationContext(), Arrays.asList(SCOPES))
                                        .setBackOff(new ExponentialBackOff());

                                credential.setSelectedAccountName(template.getEmailDataTemplate().getRecipient());

                                System.out.println("Cred 2 = " + template.getEmailDataTemplate().getRecipient());
                                cred = credential;
                           // }


                            Gmail service = new Gmail.Builder(transport, jsonFactory, cred)
                                    .setApplicationName("Extime")
                                    .build();

                            try {
                                service.users().messages().send(template.getEmailDataTemplate().getRecipient(), message_).execute();

                                activityApp.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(activityApp, "Send success", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            } catch (UnknownHostException e1) {
                                activityApp.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(activityApp, "Send failed", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                        } catch (MessagingException | IOException e) {
                            e.printStackTrace();
                        }

                    }
                }).start();


                if(template.getEmailDataTemplate().getSendTo() != null && template.getEmailDataTemplate().getSendTo().size() > 0)
                    activityApp.onBackPressed();

                // }


            }
        });

    }

    public void initTimeMessage() {
        try {
            Calendar current = Calendar.getInstance();
            Calendar contactDate = Calendar.getInstance();
            current.setTime(new Date());
            contactDate.setTime(new Date());
            String timeStr = "";
            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(new Date());
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

            ((TextView) mainView.findViewById(R.id.timeMessage)).setText(timeStr);
        } catch (Exception e) {
            System.out.println("ERROR TO GET TIME Contact Adapter");
        }
    }

    public Time getRandomDate() {
        final Random random = new Random();
        final int millisInDay = 24 * 60 * 60 * 1000;
        return new Time((long) random.nextInt(millisInDay));
    }

    public void initMessage_title() {

        if (/*!template.isTemplateUser() && template.getTitle().equalsIgnoreCase("reply")*/ message != null) {
            ((TextView) mainView.findViewById(R.id.titleMessage)).setText("Re: " + message.getTitle());
        } else if (template.isTemplateUser()) {
            ((TextView) mainView.findViewById(R.id.titleMessage)).setText(template.getSubject());
        }

    }


    public void initSender_recipient() {
        //((ImageView) mainView.findViewById(R.id.google_recipient)).setVisibility(View.GONE);

        ((ImageView) mainView.findViewById(R.id.google_sender)).setVisibility(View.GONE);


        boolean checkSelect = false;

        //if (emailConatct.contains(message.getAccount().getEmail())/*message.getAccount().getEmail().equals(email_contact)*/) {
        System.out.println("WHITE 1");

        checkSelect = true;


        //=====================================================


            mainView.findViewById(R.id.moreCont_1).setVisibility(View.GONE);
            mainView.findViewById(R.id.secondC_1).setVisibility(View.GONE);
            mainView.findViewById(R.id.firstC_1).setVisibility(View.GONE);

            if(template.getEmailDataTemplate().getSendTo().get(0) == null){
                template.getEmailDataTemplate().getSendTo().remove(0);
            }



        if (template.getEmailDataTemplate().getSendTo().size() > 2) {
            ((TextView) mainView.findViewById(R.id.contactC_1)).setText(String.valueOf(template.getEmailDataTemplate().getSendTo().size() - 2));
            mainView.findViewById(R.id.moreCont_1).setVisibility(View.VISIBLE);
        }

        if (template.getEmailDataTemplate().getSendTo().size() == 1 && template.getEmailDataTemplate().getSendTo().get(0) != null) {

            mainView.findViewById(R.id.moreCont_1).setVisibility(View.GONE);
            mainView.findViewById(R.id.secondC_1).setVisibility(View.GONE);
            mainView.findViewById(R.id.firstC_1).setVisibility(View.VISIBLE);

            int hash = 0;

            if (template.getEmailDataTemplate().getSendToName().get(0) != null && !template.getEmailDataTemplate().getSendToName().get(0).isEmpty())
                hash = template.getEmailDataTemplate().getSendToName().get(0).hashCode();
            else if (template.getEmailDataTemplate().getSendTo().get(0) != null)
                hash = template.getEmailDataTemplate().getSendTo().get(0).hashCode();


            //int hash = selectedContact.get(0).getName().hashCode();
            int color22 = Color.rgb(Math.abs(hash * 28439) % 255, Math.abs(hash * 211239) % 255, Math.abs(hash * 42368) % 255);
            GradientDrawable circle22 = (GradientDrawable) ContextCompat.getDrawable(getContext(), R.drawable.blue_circle).mutate();
            circle22.setColor(color22);
            ((CircleImageView) mainView.findViewById(R.id.avatar_sender)).setBackground(circle22);

            if (template.getEmailDataTemplate().getSendToName().get(0) != null) {
                String iinitials = getInitials(template.getEmailDataTemplate().getSendToName().get(0));
                ((TextView) mainView.findViewById(R.id.initial_sender)).setText(iinitials);
            } else if (template.getEmailDataTemplate().getSendTo().get(0) != null) {
                String iinitials = getInitials(template.getEmailDataTemplate().getSendTo().get(0));
                ((TextView) mainView.findViewById(R.id.initial_sender)).setText(iinitials);
            }

           /* if (listOfContactsMessage.get(0).isGoogle()) {
                ((ImageView) mainView.findViewById(R.id.google_recipient_1)).setVisibility(View.VISIBLE);
            } else {
                ((ImageView) mainView.findViewById(R.id.google_recipient_1)).setVisibility(View.GONE);
            }*/

        }


        if (template.getEmailDataTemplate().getSendTo().size() >= 2) {
            if (template.getEmailDataTemplate().getSendTo().size() > 2)
                mainView.findViewById(R.id.moreCont_1).setVisibility(View.VISIBLE);
            else
                mainView.findViewById(R.id.moreCont_1).setVisibility(View.GONE);


            mainView.findViewById(R.id.secondC_1).setVisibility(View.VISIBLE);
            mainView.findViewById(R.id.firstC_1).setVisibility(View.VISIBLE);


            //mainView.findViewById(R.id.secondC_1).setVisibility(View.VISIBLE);

            int hash = 0;

            if (template.getEmailDataTemplate().getSendToName().get(0) != null)
                hash = template.getEmailDataTemplate().getSendToName().get(0).hashCode();
            else if (template.getEmailDataTemplate().getSendTo().get(0) != null)
                hash = template.getEmailDataTemplate().getSendTo().get(0).hashCode();


            //int hash = selectedContact.get(0).getName().hashCode();
            int color22 = Color.rgb(Math.abs(hash * 28439) % 255, Math.abs(hash * 211239) % 255, Math.abs(hash * 42368) % 255);
            GradientDrawable circle22 = (GradientDrawable) ContextCompat.getDrawable(getContext(), R.drawable.blue_circle).mutate();
            circle22.setColor(color22);
            ((CircleImageView) mainView.findViewById(R.id.avatar_sender)).setBackground(circle22);

            if (template.getEmailDataTemplate().getSendToName().get(0) != null) {
                String iinitials = getInitials(template.getEmailDataTemplate().getSendToName().get(0));
                ((TextView) mainView.findViewById(R.id.initial_sender)).setText(iinitials);
            } else if (template.getEmailDataTemplate().getSendTo().get(0) != null) {
                String iinitials = getInitials(template.getEmailDataTemplate().getSendTo().get(0));
                ((TextView) mainView.findViewById(R.id.initial_sender)).setText(iinitials);
            }


            int hash_2 = 0;

            if (template.getEmailDataTemplate().getSendToName().get(1) != null)
                hash_2 = template.getEmailDataTemplate().getSendToName().get(1).hashCode();
            else if (template.getEmailDataTemplate().getSendTo().get(1) != null)
                hash_2 = template.getEmailDataTemplate().getSendTo().get(1).hashCode();


            //int hash = selectedContact.get(0).getName().hashCode();
            int color2_2 = Color.rgb(Math.abs(hash_2 * 28439) % 255, Math.abs(hash_2 * 211239) % 255, Math.abs(hash_2 * 42368) % 255);
            GradientDrawable circle2_2 = (GradientDrawable) ContextCompat.getDrawable(getContext(), R.drawable.blue_circle).mutate();
            circle2_2.setColor(color2_2);
            ((CircleImageView) mainView.findViewById(R.id.avatar_recipient_2_1)).setBackground(circle2_2);

            if (template.getEmailDataTemplate().getSendToName().get(1) != null) {
                String iinitials = getInitials(template.getEmailDataTemplate().getSendToName().get(1));
                ((TextView) mainView.findViewById(R.id.initials_recipient_2_1)).setText(iinitials);
            } else if (template.getEmailDataTemplate().getSendTo().get(1) != null) {
                String iinitials = getInitials(template.getEmailDataTemplate().getSendTo().get(1));
                ((TextView) mainView.findViewById(R.id.initials_recipient_2_1)).setText(iinitials);
            }

            mainView.findViewById(R.id.secondC_1).setVisibility(View.VISIBLE);
            mainView.findViewById(R.id.google_recipient_2_1).setVisibility(View.GONE);

           /* if (listOfContactsMessage.get(1).isGoogle()) {
                ((ImageView) mainView.findViewById(R.id.google_recipient_2)).setVisibility(View.VISIBLE);
            } else {
                ((ImageView) mainView.findViewById(R.id.google_recipient_2)).setVisibility(View.GONE);
            }*/

        }



        if(template.getEmailDataTemplate().getSendTo().size() > 0) {
            ((TextView) mainView.findViewById(R.id.header_message)).setText(template.getEmailDataTemplate().getSendToName().get(0) + " < " + template.getEmailDataTemplate().getSendTo().get(0) + " >");
        }else{
            ((TextView) mainView.findViewById(R.id.header_message)).setText("");
        }


        //===============================================================================================


       /* if (message != null) {
            int nameHash = message.getAccount().getName().hashCode();
            int color = Color.rgb(Math.abs(nameHash * 28439) % 255, Math.abs(nameHash * 211239) % 255, Math.abs(nameHash * 42368) % 255);
            GradientDrawable circle = (GradientDrawable) ContextCompat.getDrawable(getContext(), R.drawable.blue_circle).mutate();
            circle.setColor(color);
            ((CircleImageView) mainView.findViewById(R.id.avatar_sender)).setBackground(circle);
            String initials = getInitials(message.getAccount().getName());
            ((TextView) mainView.findViewById(R.id.initial_sender)).setText(initials);
            ((TextView) mainView.findViewById(R.id.header_message)).setText(message.getAccount().getName() + " < " + message.getAccount().getEmail() + " >");
        } else {
            int nameHash;
            if (template.getEmailDataTemplate().getSendToName().get(0) == null || template.getEmailDataTemplate().getSendToName().get(0).isEmpty()) {
                nameHash = template.getEmailDataTemplate().getSendTo().get(0).hashCode();
            } else {
                nameHash = template.getEmailDataTemplate().getSendToName().get(0).hashCode();
            }
            int color = Color.rgb(Math.abs(nameHash * 28439) % 255, Math.abs(nameHash * 211239) % 255, Math.abs(nameHash * 42368) % 255);
            GradientDrawable circle = (GradientDrawable) ContextCompat.getDrawable(getContext(), R.drawable.blue_circle).mutate();
            circle.setColor(color);
            ((CircleImageView) mainView.findViewById(R.id.avatar_sender)).setBackground(circle);

            String initials;

            if (template.getEmailDataTemplate().getSendToName().get(0) == null || template.getEmailDataTemplate().getSendToName().get(0).isEmpty()) {
                initials = getInitials(template.getEmailDataTemplate().getSendTo().get(0));
            } else {
                initials = getInitials(template.getEmailDataTemplate().getSendToName().get(0));
            }

            ((TextView) mainView.findViewById(R.id.initial_sender)).setText(initials);
            if (template.getEmailDataTemplate().getSendToName().get(0) == null || template.getEmailDataTemplate().getSendToName().get(0).isEmpty()) {
                ((TextView) mainView.findViewById(R.id.header_message)).setText(template.getEmailDataTemplate().getSendTo().get(0));
            } else {
                ((TextView) mainView.findViewById(R.id.header_message)).setText(template.getEmailDataTemplate().getSendToName().get(0) + " < " + template.getEmailDataTemplate().getSendTo().get(0) + " >");
            }

        }
*/

            /*SharedPreferences mSettings;
            mSettings = getActivity().getSharedPreferences("accountUser", Context.MODE_PRIVATE);
            Set<String> set = mSettings.getStringSet("accounts", null);*/


      /*  } else {
            System.out.println("WHITE 2");
            int nameHash = message.getAccount().getName().hashCode();
            int color = Color.rgb(Math.abs(nameHash * 28439) % 255, Math.abs(nameHash * 211239) % 255, Math.abs(nameHash * 42368) % 255);
            GradientDrawable circle = (GradientDrawable) ContextCompat.getDrawable(getContext(), R.drawable.blue_circle).mutate();
            circle.setColor(color);
            ((CircleImageView) mainView.findViewById(R.id.avatar_sender)).setBackground(circle);
            String initials = getInitials(message.getEmail_2());
            ((TextView) mainView.findViewById(R.id.initial_sender)).setText(initials);
            ((TextView) mainView.findViewById(R.id.header_message)).setText("< " + message.getAccount().getName() + " >");

            *//*int hash = nameContact.hashCode();
            int color2 = Color.rgb(Math.abs(hash * 28439) % 255, Math.abs(hash * 211239) % 255, Math.abs(hash * 42368) % 255);
            GradientDrawable circle2 = (GradientDrawable) ContextCompat.getDrawable(getContext(), R.drawable.blue_circle).mutate();
            circle2.setColor(color2);
            ((CircleImageView) mainView.findViewById(R.id.avatar_recipient)).setBackground(circle2);
            String initials2 = getInitials(nameContact);
            ((TextView) mainView.findViewById(R.id.initials_recipient)).setText(initials2);*//*


            ((ImageView) mainView.findViewById(R.id.google_sender)).setVisibility(View.VISIBLE);
        }*/


        //================================


        String data = null;

        ArrayList<ContactOfMessage> listOfContactsMessage = new ArrayList<>();

        ContactOfMessage sender = new ContactOfMessage();

        /*if (message != null) {
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
        }

        boolean checkU = false;

        if (data != null && selectEmail == null) {
            String[] mas = data.split(",");
            if (mas.length > 0) {
                for (int i = 0; i < mas.length; i++) {
                    System.out.println("Search = " + mas[0]);
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

                        if (set.contains(email)) {
                            contact.setGoogle(true);

                        }

                    }

                    listOfContactsMessage.add(contact);
                }
            }
        } else if (message != null) {
            ContactOfMessage contact = new ContactOfMessage();

            SharedPreferences mSettings;
            mSettings = getActivity().getSharedPreferences("accountUser", Context.MODE_PRIVATE);
            Set<String> set = mSettings.getStringSet("accounts", null);


            if (set != null && !set.isEmpty()) {

                if (set.contains(selectEmail)) {
                    contact.setGoogle(true);

                }

            }

            contact.setEmail(selectEmail);

            listOfContactsMessage.add(contact);
        }*/



        /*if (message == null && selectEmail == null) {
            SharedPreferences mSettings;
            mSettings = getActivity().getSharedPreferences("accountUser", Context.MODE_PRIVATE);
            Set<String> set = mSettings.getStringSet("accounts", null);

            for (String s : set) {
                if (s.equalsIgnoreCase(template.getEmailDataTemplate().getRecipient())) {
                    ContactOfMessage contact = new ContactOfMessage();
                    contact.setName(s);
                    contact.setEmail(s);
                    contact.setGoogle(true);
                    listOfContactsMessage.add(contact);
                    break;
                }
            }
        } else if (message == null && selectEmail != null) {*/


        selectEmail = template.getEmailDataTemplate().getRecipient();

        ContactOfMessage contact = new ContactOfMessage();

        SharedPreferences mSettings;
        mSettings = getActivity().getSharedPreferences("accountUser", Context.MODE_PRIVATE);
        Set<String> set = mSettings.getStringSet("accounts", null);


        if (set != null && !set.isEmpty()) {

            if (set.contains(selectEmail)) {
                contact.setGoogle(true);

            }

        }

        contact.setEmail(selectEmail);

        listOfContactsMessage.add(contact);
        //  }



        if (listOfContactsMessage.size() > 2) {
            ((TextView) mainView.findViewById(R.id.contactC)).setText(String.valueOf(listOfContactsMessage.size() - 2));
            mainView.findViewById(R.id.moreCont).setVisibility(View.VISIBLE);
        }

        if (listOfContactsMessage.size() == 1) {
            mainView.findViewById(R.id.moreCont).setVisibility(View.GONE);
            mainView.findViewById(R.id.secondC).setVisibility(View.GONE);

            int hash = 0;

            if (listOfContactsMessage.get(0).getName() != null)
                hash = listOfContactsMessage.get(0).getName().hashCode();
            else if (listOfContactsMessage.get(0).getEmail() != null)
                hash = listOfContactsMessage.get(0).getEmail().hashCode();


            //int hash = selectedContact.get(0).getName().hashCode();
            int color2 = Color.rgb(Math.abs(hash * 28439) % 255, Math.abs(hash * 211239) % 255, Math.abs(hash * 42368) % 255);
            GradientDrawable circle2 = (GradientDrawable) ContextCompat.getDrawable(getContext(), R.drawable.blue_circle).mutate();
            circle2.setColor(color2);
            ((CircleImageView) mainView.findViewById(R.id.avatar_recipient_1)).setBackground(circle2);

            if (listOfContactsMessage.get(0).getName() != null) {
                String iinitials = getInitials(listOfContactsMessage.get(0).getName());
                ((TextView) mainView.findViewById(R.id.initials_recipient_1)).setText(iinitials);
            } else if (listOfContactsMessage.get(0).getEmail() != null) {
                String iinitials = getInitials(listOfContactsMessage.get(0).getEmail());
                ((TextView) mainView.findViewById(R.id.initials_recipient_1)).setText(iinitials);
            }

            if (listOfContactsMessage.get(0).isGoogle()) {
                ((ImageView) mainView.findViewById(R.id.google_recipient_1)).setVisibility(View.VISIBLE);
            } else {
                ((ImageView) mainView.findViewById(R.id.google_recipient_1)).setVisibility(View.GONE);
            }

        }


        if (listOfContactsMessage.size() >= 2) {
            if (listOfContactsMessage.size() > 2)
                mainView.findViewById(R.id.moreCont).setVisibility(View.VISIBLE);
            else
                mainView.findViewById(R.id.moreCont).setVisibility(View.GONE);

            mainView.findViewById(R.id.secondC).setVisibility(View.VISIBLE);

            int hash = 0;

            if (listOfContactsMessage.get(0).getName() != null)
                hash = listOfContactsMessage.get(0).getName().hashCode();
            else if (listOfContactsMessage.get(0).getEmail() != null)
                hash = listOfContactsMessage.get(0).getEmail().hashCode();


            //int hash = selectedContact.get(0).getName().hashCode();
            int color2 = Color.rgb(Math.abs(hash * 28439) % 255, Math.abs(hash * 211239) % 255, Math.abs(hash * 42368) % 255);
            GradientDrawable circle2 = (GradientDrawable) ContextCompat.getDrawable(getContext(), R.drawable.blue_circle).mutate();
            circle2.setColor(color2);
            ((CircleImageView) mainView.findViewById(R.id.avatar_recipient_1)).setBackground(circle2);

            if (listOfContactsMessage.get(0).getName() != null) {
                String iinitials = getInitials(listOfContactsMessage.get(0).getName());
                ((TextView) mainView.findViewById(R.id.initials_recipient_1)).setText(iinitials);
            } else if (listOfContactsMessage.get(0).getEmail() != null) {
                String iinitials = getInitials(listOfContactsMessage.get(0).getEmail());
                ((TextView) mainView.findViewById(R.id.initials_recipient_1)).setText(iinitials);
            }


            if (listOfContactsMessage.get(0).isGoogle()) {
                ((ImageView) mainView.findViewById(R.id.google_recipient_1)).setVisibility(View.VISIBLE);
            } else {
                ((ImageView) mainView.findViewById(R.id.google_recipient_1)).setVisibility(View.GONE);
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
            ((CircleImageView) mainView.findViewById(R.id.avatar_recipient_2)).setBackground(circle2_2);

            if (listOfContactsMessage.get(1).getName() != null) {
                String iinitials = getInitials(listOfContactsMessage.get(1).getName());
                ((TextView) mainView.findViewById(R.id.initials_recipient_2)).setText(iinitials);
            } else if (listOfContactsMessage.get(1).getEmail() != null) {
                String iinitials = getInitials(listOfContactsMessage.get(1).getEmail());
                ((TextView) mainView.findViewById(R.id.initials_recipient_2)).setText(iinitials);
            }

            if (listOfContactsMessage.get(1).isGoogle()) {
                ((ImageView) mainView.findViewById(R.id.google_recipient_2)).setVisibility(View.VISIBLE);
            } else {
                ((ImageView) mainView.findViewById(R.id.google_recipient_2)).setVisibility(View.GONE);
            }

        }


        //================================
    }

    private String getInitials(String name) {
        String initials = "";
        if (name != null && !name.isEmpty()) {
            String names[] = name.split("\\s+");
            for (String namePart : names) {
                if (namePart != null && namePart.length() > 0) {

                    initials += namePart.charAt(0);
                }
            }
        }
        return initials.toUpperCase();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == 42 && resultCode == Activity.RESULT_OK) {
            Uri[] uriPaths;

            if (resultData == null || (resultData.getData() == null && resultData.getClipData() == null)) {
                //Toast.makeText(getContext(), R.string.invalid_source, Toast.LENGTH_SHORT).show();
                return;
            }

            if (resultData.getData() != null) {
                uriPaths = new Uri[1];
                uriPaths[0] = resultData.getData();
                System.out.println("URI = " + uriPaths[0]);


                activityApp.getContentResolver().takePersistableUriPermission(uriPaths[0], Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);


                File f = new File(uriPaths[0].toString());

                System.out.println("FILE = " + f.getAbsolutePath());

                //dumpImageMetaData(uriPaths[0]);

                FilesTemplate ft = new FilesTemplate(uriPaths[0]);

                FileType.getNameSize(ft, activityApp);

                template.addFile(ft);

                initRecycler();


            } else if (resultData.getClipData() != null) {
                int selectedCount = resultData.getClipData().getItemCount();
                uriPaths = new Uri[selectedCount];
                for (int i = 0; i < selectedCount; i++) {
                    uriPaths[i] = resultData.getClipData().getItemAt(i).getUri();

                    activityApp.getContentResolver().takePersistableUriPermission(uriPaths[i], Intent.FLAG_GRANT_READ_URI_PERMISSION
                            | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                    System.out.println("Uri = " + uriPaths[i]);
                    FilesTemplate ft = new FilesTemplate(uriPaths[i]);

                    FileType.getNameSize(ft, activityApp);

                    template.addFile(ft);

                }
                initRecycler();


            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mainToolBar.findViewById(R.id.more_templates_toolbar).setVisibility(View.GONE);
        activityApp.findViewById(R.id.main_toolbar).findViewById(R.id.toolbar_plane).setVisibility(View.GONE);
        activityApp.findViewById(R.id.main_content_ll).setVisibility(View.VISIBLE);
        activityApp.findViewById(R.id.fabMenuContainer).setVisibility(View.GONE);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            activityApp = (Activity) context;
        }
    }

    /*ArrayList<String> sendToNew;
    ArrayList<String> sendToNameNew;*/

    @Override
    public void addRecipient(String name, String email) {

       /* if(sendToNameNew == null){
            sendToNew = new ArrayList<>();
            sendToNameNew = new ArrayList<>();
        }*/

        if (template != null && template.getEmailDataTemplate() != null) {
            if (!template.getEmailDataTemplate().getSendTo().contains(email)) {
                template.getEmailDataTemplate().getSendTo().add(email);
                template.getEmailDataTemplate().getSendToName().add(name);
                template.getEmailDataTemplate().getSend().add(true);
                mainView.findViewById(R.id.popup_mess_search).setVisibility(View.GONE);
                initRecyclerContacts();
                initSender_recipient();
            } else {
                Toast.makeText(activityApp, "Data already exist", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!EventBus.getDefault().isRegistered(this)) EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (EventBus.getDefault().isRegistered(this)) EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(CheckLinkMessageReply event) {
        initLinkPremium();
    }
}
