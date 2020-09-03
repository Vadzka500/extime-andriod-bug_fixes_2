package ai.extime.Fragments;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import ai.extime.Activity.MainActivity;
import ai.extime.Adapters.ContactAdapter;
import ai.extime.Adapters.EmailTemplatesAdapter;
import ai.extime.Adapters.TemplateFileAdapter;
import ai.extime.Events.AddTemplate;
import ai.extime.Events.CheckLinkMessageTEmails;
import ai.extime.Events.UpdateTemplate;
import ai.extime.Interfaces.IEmails;
import ai.extime.Interfaces.PopupsInter;
import ai.extime.Interfaces.Postman;
import ai.extime.Models.FilesTemplate;
import ai.extime.Models.Template;
import ai.extime.Utils.FileType;

import com.extime.R;
import com.google.android.gms.common.util.IOUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.snatik.storage.Storage;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.Set;

/**
 * Created by patal on 21.09.2017.
 */

public class EmailsFragment extends Fragment implements PopupsInter, IEmails {

    private View mainView;

    private Toolbar toolbarC;

    private RecyclerView recyclerView;

    private Activity activityApp;

    private ArrayList<Template> list;

    private SharedPreferences sharedPreferences;

    private EmailTemplatesAdapter emailTemplatesAdapter;

    private FrameLayout popupTemplate;

    private FrameLayout editPopupTemplate;

    private Storage storage;
    private String path;




    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        System.out.println("Create EMAIL");
    }

    public static String searchCall = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle bundle) {
        if (mainView == null) {
            mainView = inflater.inflate(R.layout.emails_layout_content, viewGroup, false);

            System.out.println("Create EMAIL VIEW");

            toolbarC = ((Postman) getActivity()).getToolbar();

            setHasOptionsMenu(true);
        }

        sharedPreferences = getContext().getSharedPreferences("templateList", Context.MODE_PRIVATE);

        initViews();



        initListeners();

        if(searchCall != null && !searchCall.isEmpty()){

            ((EditText) getActivity().findViewById(R.id.magic_edit_text)).setText(searchCall.trim());
            searchCall = null;
        }

        /*if(MainActivity.voice_command != null){


            try {
                ArrayList l = getListDataNew();
                String[] mas = MainActivity.voice_command.split(" ");

                for(Template template)

                openTemplateFragment();

            }catch (Exception e){
                e.printStackTrace();
            }

            MainActivity.voice_command = null;
        }*/

        return mainView;
    }

    public void initLinkPremiumPopupPreview(){
        SharedPreferences pref_switch = activityApp.getSharedPreferences("SwitchFab", Context.MODE_PRIVATE);
        boolean switchBoolean = pref_switch.getBoolean("switchMessage", true);

        if (!switchBoolean) {
            popupTemplate.findViewById(R.id.linkMessageReply).setVisibility(View.GONE);
        }else{

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


            String text = "I create and send this email in just 3 secs \n" +
                    "Save time with personal CRM ecosystem Extime.pro - try it for free";

            SpannableStringBuilder text_ = new SpannableStringBuilder(text);

            text_.setSpan(click_span, 0, text_.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            text_.setSpan(new UnderlineSpan(), 0, text.length(), 0);

            ((TextView)popupTemplate.findViewById(R.id.linkMessageReply)).setMovementMethod(LinkMovementMethod.getInstance());

            ((TextView)popupTemplate.findViewById(R.id.linkMessageReply)).setText(text_);

            popupTemplate.findViewById(R.id.linkMessageReply).setVisibility(View.VISIBLE);

        }
    }

    public void initLinkPremiumPopupEdit(){
        SharedPreferences pref_switch = activityApp.getSharedPreferences("SwitchFab", Context.MODE_PRIVATE);
        boolean switchBoolean = pref_switch.getBoolean("switchMessage", true);

        if (!switchBoolean) {
            editPopupTemplate.findViewById(R.id.linkMessageReply).setVisibility(View.GONE);
        }else{

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


            String text = "I create and send this email in just 3 secs \n" +
                    "Save time with personal CRM ecosystem Extime.pro - try it for free";

            SpannableStringBuilder text_ = new SpannableStringBuilder(text);

            text_.setSpan(click_span, 0, text_.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            text_.setSpan(new UnderlineSpan(), 0, text.length(), 0);

            ((TextView)editPopupTemplate.findViewById(R.id.linkMessageReply)).setMovementMethod(LinkMovementMethod.getInstance());

            ((TextView)editPopupTemplate.findViewById(R.id.linkMessageReply)).setText(text_);

            editPopupTemplate.findViewById(R.id.linkMessageReply).setVisibility(View.VISIBLE);

        }
    }

    public void initViews() {
        popupTemplate = activityApp.findViewById(R.id.popupTemplate);
        editPopupTemplate = activityApp.findViewById(R.id.popupTemplateEdit);
        recyclerView = mainView.findViewById(R.id.templatesContainer);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        ((TextView) mainView.findViewById(R.id.barTemplates_count)).setText("5");

        Account[] accounts = AccountManager.get(getActivity()).getAccountsByType("com.google");

        SharedPreferences mSettings;
        mSettings = getActivity().getSharedPreferences("accountUser", Context.MODE_PRIVATE);
        Set<String> set = mSettings.getStringSet("accounts", null);

        if (set != null) {
            ((TextView) mainView.findViewById(R.id.barCountGmails)).setText(String.valueOf(set.size()));
        }

        ((EditText) getActivity().findViewById(R.id.magic_edit_text)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                emailTemplatesAdapter.updateList(getSearchList(s.toString()));
                emailTemplatesAdapter.updateSearch(s.toString());
                ((TextView) activityApp.findViewById(R.id.countSearchContacts)).setVisibility(View.GONE);
            }
        });
    }

    public ArrayList<Template> getSearchList(String search){
        ArrayList<Template> listSearch = new ArrayList<>();
        for(Template t : list){
            if(t.getTitle().toLowerCase().contains(search.toLowerCase())){
                listSearch.add(t);
            }
        }
        return listSearch;
    }

    public void initListeners() {


        emailTemplatesAdapter = new EmailTemplatesAdapter(getData(), this, mainView.getContext(), null);
        recyclerView.setAdapter(emailTemplatesAdapter);

        mainView.findViewById(R.id.closer_emails).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideTemplatePopup();
            }
        });
    }

    public ArrayList<Template> getData() {

        list = new ArrayList<>();

        storage = new Storage(activityApp.getApplicationContext());
        path = Environment.getExternalStorageDirectory().getAbsolutePath();
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
            updateList();
        } else {

            System.out.println("template get");

            Gson gson = new Gson();


            list.addAll(gson.fromJson(content, new TypeToken<ArrayList<Template>>() {
            }.getType()));

            updateBar();
        }
        return list;
    }

    public ArrayList<Template> getListDataNew() {

        ArrayList list = new ArrayList<>();

        storage = new Storage(activityApp.getApplicationContext());
        path = Environment.getExternalStorageDirectory().getAbsolutePath();
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

    public void updateTemplateInList(Template template) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId().equals(template.getId())) {
                list.set(i, template);
                break;
            }
        }
        updateList();
    }


    public void updateList() {
        System.out.println("template update");


        Gson gson = new Gson();
        String json = gson.toJson(list);
       /* SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("list", json);
        editor.apply();*/


        storage.deleteFile(path + "/Extime/ExtimeContacts/backupTemplates");
        storage.createFile(path + "/Extime/ExtimeContacts/backupTemplates", json);


    }

    public void addTemplate(Template template) {
        list.add(0, template);

        Gson gson = new Gson();
        String json = gson.toJson(list);

        storage.deleteFile(path + "/Extime/ExtimeContacts/backupTemplates");
        storage.createFile(path + "/Extime/ExtimeContacts/backupTemplates", json);


        emailTemplatesAdapter.notifyDataSetChanged();
        System.out.println("Add template");

        updateBar();
    }


    @Override
    public void closeOtherPopup() {

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (!ContactAdapter.checkMerge)
            menu.getItem(0).setVisible(true);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(AddTemplate event) {
        addTemplate(event.getTemplate());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(CheckLinkMessageTEmails event) {

        initLinkPremiumPopupPreview();
        initLinkPremiumPopupEdit();

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(UpdateTemplate event) {
        updateTemplateInList(event.getTemplate());
        emailTemplatesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        ((MainActivity) mainView.getContext()).hideViewFlipper();
        ((MainActivity) mainView.getContext()).startDrawer();

        ((TextView) toolbarC.findViewById(R.id.toolbar_title)).setText("Email templates");

        if (!ContactAdapter.checkMerge)
            ((TextView) toolbarC.findViewById(R.id.cancel_toolbar)).setVisibility(View.GONE);

        toolbarC.findViewById(R.id.toolbar_kanban_clicker).setVisibility(View.GONE);

        super.onPrepareOptionsMenu(menu);
    }


    @Override
    public void showTemplatePopup(Template template) {

        if(popupTemplate == null)
            popupTemplate = activityApp.findViewById(R.id.popupTemplate);

        if(editPopupTemplate == null)
            editPopupTemplate = activityApp.findViewById(R.id.popupTemplateEdit);

        if (editPopupTemplate.getVisibility() == View.VISIBLE) {
            editPopupTemplate.setVisibility(View.GONE);
            return;
        }

        if (popupTemplate.getVisibility() == View.VISIBLE) {
            popupTemplate.setVisibility(View.GONE);
            return;
        }

        initLinkPremiumPopupPreview();

        popupTemplate.findViewById(R.id.gmailMessageTemp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTemplateFragment(template);
            }
        });

        selectTemplate = null;


        if (!template.isTemplateUser()) {

            selectTemplate = new Template();
            selectTemplate.setTemplateUser(false);
            selectTemplate.setTitle(template.getTitle());



            popupTemplate.findViewById(R.id.l_message_title).setVisibility(View.VISIBLE);
            popupTemplate.findViewById(R.id.footer_message).setVisibility(View.VISIBLE);
            ((TextView) popupTemplate.findViewById(R.id.namepopupTemplate)).setText("Name Surname");
            ((TextView) popupTemplate.findViewById(R.id.recipient_email)).setText("<recipient's email>");
            ((TextView) popupTemplate.findViewById(R.id.textMessageHint)).setText("Start typing here _");

            ((TextView) popupTemplate.findViewById(R.id.textMessageHint)).setVisibility(View.VISIBLE);
            ((TextView) popupTemplate.findViewById(R.id.textMessage_p)).setVisibility(View.GONE);

            ((TextView) popupTemplate.findViewById(R.id.textMessage_2)).setText("%Name");
            ((TextView) popupTemplate.findViewById(R.id.textMessage_2)).setTextColor(activityApp.getResources().getColor(R.color.primary));


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
            ((TextView) popupTemplate.findViewById(R.id.recipient_email)).setText("<recipient's email>");

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
                                    list.remove(template);
                                    updateBar();
                                    updateList();
                                    emailTemplatesAdapter.updateList(list);
                                    popupTemplate.setVisibility(View.GONE);

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

            //((TextView)mainView.findViewById(R.id.textMessage_p)).setTextColor(mainView.getContext().getResources().getColor(R.color.black));
            //((TextView)mainView.findViewById(R.id.textMessage))

            if (template.getTemplateText().contains("%Name")) {
                int startI = template.getTemplateText().toLowerCase().indexOf("%name");
                final SpannableStringBuilder text = new SpannableStringBuilder(template.getTemplateText());
                final ForegroundColorSpan style = new ForegroundColorSpan(Color.parseColor("#87aade"));
                //StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
                text.setSpan(style, startI, startI + 5, Spannable.SPAN_COMPOSING);
                //text.setSpan(bss, startI, startI + search.length(), Spannable.SPAN_COMPOSING);
                ((TextView) popupTemplate.findViewById(R.id.textMessage_p)).setText(text);
            } else
                ((TextView) popupTemplate.findViewById(R.id.textMessage_p)).setText(template.getTemplateText());

        }

        popupTemplate.findViewById(R.id.sendMEssagePreview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

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
    }

    public Template selectTemplate;

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

                ft.setUrl(Uri.parse(dumpImageMetaData(uriPaths[0])));

                selectTemplate.addFile(ft);

                if (selectTemplate.getEmailDataTemplate() == null && selectTemplate.isTemplateUser()) {

                    EventBus.getDefault().post(new UpdateTemplate(selectTemplate));
                } else if (!selectTemplate.isTemplateUser()) {

                    /*Template template = new Template();

                    template.setTitle("New Template");
                    template.setTemplateUser(true);
                    template.setSubject("No subject");


                    String text = ((TextView) popupTemplate.findViewById(R.id.textMessage_1)).getText().toString() + " ";
                    text += ((TextView) popupTemplate.findViewById(R.id.textMessage_2)).getText().toString()+"\n";
                    text += ((TextView) popupTemplate.findViewById(R.id.textMessage_p)).getText().toString()+"\n";
                    text += ((TextView) popupTemplate.findViewById(R.id.footer_message)).getText().toString();

                    template.setTemplateText(text);
                    template.setDateCreate(new Date());

                    template.addFile(ft);

                    selectTemplate = template;

                    EventBus.getDefault().post(new AddTemplate(template));*/
                }

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

                    System.out.println("Uri = " + uriPaths[i]);
                    FilesTemplate ft = new FilesTemplate(uriPaths[i]);

                    FileType.getNameSize(ft, activityApp);

                    ft.setUrl(Uri.parse(dumpImageMetaData(uriPaths[i])));

                    selectTemplate.addFile(ft);

                   /* if (selectTemplate.getEmailDataTemplate() == null && selectTemplate.isTemplateUser())

                    else {
                        template.addFile(ft);
                    }*/


                    //template.addFile(uriPaths[i]);
                }


                if (selectTemplate.getEmailDataTemplate() == null && selectTemplate.isTemplateUser())
                    EventBus.getDefault().post(new UpdateTemplate(selectTemplate));
                else if (!selectTemplate.isTemplateUser()) {



                   /* template.setTitle("New Template");
                    template.setTemplateUser(true);
                    template.setSubject("No subject");

                    String text = ((TextView) popupTemplate.findViewById(R.id.textMessage_1)).getText().toString() + " ";
                    text += ((TextView) popupTemplate.findViewById(R.id.textMessage_2)).getText().toString()+"\n";
                    text += ((TextView) popupTemplate.findViewById(R.id.textMessage_p)).getText().toString()+"\n";
                    text += ((TextView) popupTemplate.findViewById(R.id.footer_message)).getText().toString();

                    template.setTemplateText(text);

                    template.setDateCreate(new Date());

                    selectTemplate = template;

                    EventBus.getDefault().post(new AddTemplate(template));*/
                }

                if(popupTemplate.getVisibility() == View.VISIBLE)
                    initRecyclerPreview(selectTemplate);
                else
                    initRecyclerPreviewEdit(selectTemplate);

            }
        }
    }

    public String dumpImageMetaData(Uri uri) {


        Cursor cursor = activityApp.getContentResolver()
                .query(uri, null, null, null, null, null);

        String nameF = null;

        try {



            if (cursor != null && cursor.moveToFirst()) {


                String displayName = cursor.getString(
                        cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));

                nameF = displayName;
                //System.out.println("Display name = " + displayName);

                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);

                String size = null;
                if (!cursor.isNull(sizeIndex)) {

                    size = cursor.getString(sizeIndex);
                } else {
                    size = "Unknown";
                }

            }
        } finally {
            cursor.close();
        }

        String newUri = Environment.getExternalStorageDirectory() + "/Extime/TemplatesFiles/" + nameF;


        try {
            InputStream is = activityApp.getContentResolver().openInputStream(uri);




            File f = new File(Environment.getExternalStorageDirectory() + "/Extime/TemplatesFiles/", nameF);
            FileOutputStream fileOutFile;
            try {
                fileOutFile = new FileOutputStream(f);
                fileOutFile.write(IOUtils.toByteArray(is));
                fileOutFile.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }



        return newUri;
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

    public void showEditTemplate(Template template) {

        if (!template.isTemplateUser()) {


            ((ImageView) popupTemplate.findViewById(R.id.delete_template_icon)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));


            EditText textMessage_edit = editPopupTemplate.findViewById(R.id.textMessage_edit);

            ((TextView) editPopupTemplate.findViewById(R.id.recipient_email_edit)).setText("<recipient's email>");

            SpannableString text = new SpannableString("Hello %Name\n  \n\nbest regards, " + ((TextView) activityApp.findViewById(R.id.profileName)).getText().toString());

            text.setSpan(new ForegroundColorSpan(Color.parseColor("#87aade")), 6, 11, 0);

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
            ((ImageView) popupTemplate.findViewById(R.id.delete_template_icon)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));
            ((EditText) editPopupTemplate.findViewById(R.id.textMessage_edit)).setVisibility(View.GONE);
            ((EditText) editPopupTemplate.findViewById(R.id.textMessage_edit_user)).setVisibility(View.VISIBLE);
            SpannableString text = new SpannableString(template.getTemplateText());

            ((TextView) editPopupTemplate.findViewById(R.id.recipient_email_edit)).setText("<recipient's email>");

            if (template.getTemplateText().contains("%Name")) {
                int ind = template.getTemplateText().indexOf("%Name");
                text.setSpan(new ForegroundColorSpan(Color.parseColor("#87aade")), ind, ind + 5, 0);
                ((EditText) editPopupTemplate.findViewById(R.id.textMessage_edit_user)).setText(text);

            } else
                ((EditText) editPopupTemplate.findViewById(R.id.textMessage_edit_user)).setText(text);

            if (!template.getTitle().equalsIgnoreCase("New template"))
                ((EditText) editPopupTemplate.findViewById(R.id.edit_name)).setText(template.getTitle());
            else
                ((EditText) editPopupTemplate.findViewById(R.id.edit_name)).setText("");

            ((EditText) editPopupTemplate.findViewById(R.id.subject_email_edit)).setText(template.getSubject());

        }

        initLinkPremiumPopupEdit();

        ((TextView) editPopupTemplate.findViewById(R.id.recEdit)).setText("Name Surname");

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

        editPopupTemplate.findViewById(R.id.updateContactField).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!template.isTemplateUser()) {
                    Template template1 = new Template();

                    if (((EditText) editPopupTemplate.findViewById(R.id.edit_name)).getText().length() == 0) {
                        template1.setTitle(template.getTitle());
                    } else {
                        template1.setTitle(((EditText) editPopupTemplate.findViewById(R.id.edit_name)).getText().toString());
                    }

                    if (((EditText) editPopupTemplate.findViewById(R.id.subject_email_edit)).getText().length() == 0) {
                        template1.setSubject("No subject");
                    } else {
                        template1.setSubject(((EditText) editPopupTemplate.findViewById(R.id.subject_email_edit)).getText().toString());
                    }

                    template1.setTemplateUser(true);
                    template1.setTemplateText(((EditText) editPopupTemplate.findViewById(R.id.textMessage_edit)).getText().toString());

                    template1.setDateCreate(new Date());

                    template1.setListOfFiles(template.getListOfFiles());

                    Date date1 = template1.getDateCreate();
                    Calendar cal1 = Calendar.getInstance();
                    cal1.setTime(date1);
                    Time time1 = getRandomDate();
                    time1.setHours(cal1.get(Calendar.HOUR_OF_DAY));
                    time1.setMinutes(cal1.get(Calendar.MINUTE));
                    time1.setSeconds(cal1.get(Calendar.SECOND));

                    template1.time = time1.toString();

                    editPopupTemplate.setVisibility(View.GONE);

                    showTemplatePopup(template1);

                    Toast.makeText(mainView.getContext(), "Email template created!", Toast.LENGTH_SHORT).show();

                    addTemplate(template1);


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

                    showTemplatePopup(template);

                    updateList();

                    Toast.makeText(mainView.getContext(), "Email template updated!", Toast.LENGTH_SHORT).show();

                    emailTemplatesAdapter.notifyDataSetChanged();

                    //addTemplate(template1);

                }
            }
        });


        popupTemplate.setVisibility(View.GONE);
        editPopupTemplate.setVisibility(View.VISIBLE);
    }

    public Time getRandomDate() {
        final Random random = new Random();
        final int millisInDay = 24 * 60 * 60 * 1000;
        return new Time((long) random.nextInt(millisInDay));
    }

    public void updateBar() {
        ((TextView) mainView.findViewById(R.id.barCountUserTemplates)).setText(String.valueOf(list.size() - 5));
    }

    public void hideTemplatePopup() {
        popupTemplate.setVisibility(View.GONE);
        editPopupTemplate.setVisibility(View.GONE);
    }

    @Override
    public void openTemplateFragment(Template template) {

        hideTemplatePopup();

        Template templateSend;
        if (template.isTemplateUser()) templateSend = template;
        else {
            templateSend = new Template();
            templateSend.setDateCreate(template.getDateCreate());
            templateSend.setTemplateText(template.getTemplateText());
            templateSend.setSubject(template.getSubject());
            templateSend.setTitle(template.getTitle());
        }

        android.support.v4.app.FragmentManager fragmentManager = ((MainActivity) activityApp).getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.main_content, NewTemplateFragment.newInstance(templateSend)).addToBackStack("template").commit();
    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences mSettings;
        mSettings = getActivity().getSharedPreferences("accountUser", Context.MODE_PRIVATE);
        Set<String> set = mSettings.getStringSet("accounts", null);

        if (set != null && mainView != null) {
            ((TextView) mainView.findViewById(R.id.barCountGmails)).setText(String.valueOf(set.size()));
        }

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
            System.out.println("REGist");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        hideTemplatePopup();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(popupTemplate != null && popupTemplate.getVisibility() == View.VISIBLE)
            popupTemplate.setVisibility(View.GONE);

        if(editPopupTemplate != null && editPopupTemplate.getVisibility() == View.VISIBLE)
            editPopupTemplate.setVisibility(View.GONE);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            activityApp = (Activity) context;
        }
    }
}
