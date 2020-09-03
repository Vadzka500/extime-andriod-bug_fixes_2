package ai.extime.Fragments;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
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


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
import javax.mail.MessagingException;
import javax.mail.Multipart;


import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import ai.extime.Activity.MainActivity;
import ai.extime.Adapters.MessageDataAdapter;
import ai.extime.Adapters.TemplateFileAdapter;
import ai.extime.Events.AddTemplate;
import ai.extime.Events.CheckLinkMessageTemplateFragment;
import ai.extime.Events.UpdateTemplate;
import ai.extime.Models.FilesTemplate;
import ai.extime.Models.Template;
import ai.extime.Utils.FileType;

public class NewTemplateFragment extends Fragment {

    private Template template;

    private View mainView;

    private static final String[] SCOPES = {
            GmailScopes.GMAIL_LABELS,
            GmailScopes.GMAIL_READONLY,
            GmailScopes.GMAIL_MODIFY
    };

    private Activity activityApp;

    public static NewTemplateFragment newInstance(Template template) {
        Bundle args = new Bundle();
        args.putSerializable("template", template);
        NewTemplateFragment newTemplateFragment = new NewTemplateFragment();
        newTemplateFragment.setArguments(args);
        return newTemplateFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            template = (Template) getArguments().getSerializable("template");
        }

    }

    public void initLinkMessage(){
        SharedPreferences pref_switch = activityApp.getSharedPreferences("SwitchFab", Context.MODE_PRIVATE);
        boolean switchBoolean = pref_switch.getBoolean("switchMessage", true);

        if (!switchBoolean) {
            mainView.findViewById(R.id.linkMessageReply).setVisibility(View.GONE);
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

            ((TextView)mainView.findViewById(R.id.linkMessageReply)).setMovementMethod(LinkMovementMethod.getInstance());

            ((TextView)mainView.findViewById(R.id.linkMessageReply)).setText(text_);

            mainView.findViewById(R.id.linkMessageReply).setVisibility(View.VISIBLE);

        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.template_fragment, container, false);

        setHasOptionsMenu(true);


        if (template.getEmailDataTemplate() == null)
            initViews();
        else
            initViewsSend();

        initLinkMessage();

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


        initRecycler();


        return mainView;
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







                activityApp.getContentResolver().takePersistableUriPermission(uriPaths[0], Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);


                FilesTemplate ft = new FilesTemplate(uriPaths[0]);

                FileType.getNameSize(ft, activityApp);

                ft.setUrl(Uri.parse(dumpImageMetaData(uriPaths[0])));

                //File f = new File( );


                //dumpImageMetaData(uriPaths[0]);



                template.addFile(ft);

                initRecycler();

                if (template.getEmailDataTemplate() == null && template.isTemplateUser())
                    EventBus.getDefault().post(new UpdateTemplate(template));
                else if (!template.isTemplateUser()) {

                   /* template.setTemplateUser(true);
                    template.setSubject("No subject");

                    String text = ((TextView) mainView.findViewById(R.id.textMessage_1)).getText().toString() + " ";
                    text += ((TextView) mainView.findViewById(R.id.textMessage_2)).getText().toString()+"\n";
                    text += ((TextView) mainView.findViewById(R.id.textMessage_p)).getText().toString()+"\n";
                    text += ((TextView) mainView.findViewById(R.id.footer_message)).getText().toString();

                    template.setTemplateText(text);
                    template.setDateCreate(new Date());
                    EventBus.getDefault().post(new AddTemplate(template));*/

                }


            } else if (resultData.getClipData() != null) {
                int selectedCount = resultData.getClipData().getItemCount();
                uriPaths = new Uri[selectedCount];
                for (int i = 0; i < selectedCount; i++) {
                    uriPaths[i] = resultData.getClipData().getItemAt(i).getUri();

                    activityApp.getContentResolver().takePersistableUriPermission(uriPaths[i], Intent.FLAG_GRANT_READ_URI_PERMISSION
                            | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);


                    FilesTemplate ft = new FilesTemplate(uriPaths[i]);

                    FileType.getNameSize(ft, activityApp);

                    ft.setUrl(Uri.parse(dumpImageMetaData(uriPaths[i])));

                    template.addFile(ft);


                    //template.addFile(uriPaths[i]);
                }
                initRecycler();

                if (template.getEmailDataTemplate() == null && template.isTemplateUser())
                    EventBus.getDefault().post(new UpdateTemplate(template));
                else if (!template.isTemplateUser()) {
                   /* template.setTemplateUser(true);
                    template.setSubject("No subject");

                    String text = ((TextView) mainView.findViewById(R.id.textMessage_1)).getText().toString() + " ";
                    text += ((TextView) mainView.findViewById(R.id.textMessage_2)).getText().toString()+"\n";
                    text += ((TextView) mainView.findViewById(R.id.textMessage_p)).getText().toString()+"\n";
                    text += ((TextView) mainView.findViewById(R.id.footer_message)).getText().toString();

                    template.setTemplateText(text);

                    template.setDateCreate(new Date());
                    EventBus.getDefault().post(new AddTemplate(template));*/
                }

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

    public void initViewsSend() {
        activityApp.findViewById(R.id.fabMenuContainer).setVisibility(View.GONE);


        if (!template.isTemplateUser()) {
            mainView.findViewById(R.id.l_message_title).setVisibility(View.VISIBLE);
            mainView.findViewById(R.id.footer_message).setVisibility(View.VISIBLE);

            ((TextView) mainView.findViewById(R.id.recipient_email)).setText("<" + template.getEmailDataTemplate().getSendTo() + ">");

            ((TextView) mainView.findViewById(R.id.nameRecipient)).setText(template.getEmailDataTemplate().getSendToName().get(0));

            ((TextView) mainView.findViewById(R.id.textMessage_2)).setText(template.getEmailDataTemplate().getSendToName().get(0));

            ((TextView) mainView.findViewById(R.id.textMessage_2)).setTextColor(mainView.getContext().getResources().getColor(R.color.black));


            //((TextView)mainView.findViewById(R.id.textMessageHint)).setText("Start typing here _");

            ((TextView) mainView.findViewById(R.id.textMessageHint)).setVisibility(View.VISIBLE);
            ((TextView) mainView.findViewById(R.id.textMessage_p)).setText("");
            ((TextView) mainView.findViewById(R.id.textMessage_p)).setVisibility(View.GONE);


            ((TextView) mainView.findViewById(R.id.footer_message)).setText("\nbest regards, " + ((TextView) activityApp.findViewById(R.id.profileName)).getText().toString());

            //((TextView)mainView.findViewById(R.id.texttemplate)).setText("New template");


            ((TextView) mainView.findViewById(R.id.textMessageHint)).setTextColor(mainView.getContext().getResources().getColor(R.color.gray_textView));


            ((TextView) mainView.findViewById(R.id.subject_email)).setTextColor(mainView.getContext().getResources().getColor(R.color.gray));

            ((TextView) mainView.findViewById(R.id.subject_email)).setText("Email subject");

            //((TextView)mainView.findViewById(R.id.textMessage)).setText("New template");


            //mainView.findViewById(R.id.frameArrMoreMessageData).setVisibility(View.GONE);

            mainView.findViewById(R.id.text_no_files_popup).setVisibility(View.VISIBLE);

        /*    ((TextView)mainView.findViewById(R.id.textMessageHint)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showEditTemplate(template);
                }
            });*/


        } else {

            ((TextView) mainView.findViewById(R.id.subject_email)).setTextColor(mainView.getContext().getResources().getColor(R.color.black));

            ((TextView) mainView.findViewById(R.id.textMessageHint)).setVisibility(View.GONE);
            ((TextView) mainView.findViewById(R.id.textMessage_p)).setVisibility(View.VISIBLE);

            mainView.findViewById(R.id.l_message_title).setVisibility(View.GONE);
            mainView.findViewById(R.id.footer_message).setVisibility(View.GONE);

            ((TextView) mainView.findViewById(R.id.recipient_email)).setText("<" + template.getEmailDataTemplate().getSendTo() + ">");

            ((TextView) mainView.findViewById(R.id.nameRecipient)).setText(template.getEmailDataTemplate().getSendToName().get(0));

            ((TextView) mainView.findViewById(R.id.textMessage_2)).setText(template.getEmailDataTemplate().getSendToName().get(0));
            ((TextView) mainView.findViewById(R.id.textMessage_2)).setTextColor(mainView.getContext().getResources().getColor(R.color.black));

            //((TextView)mainView.findViewById(R.id.texttemplate)).setText(template.getTitle());

            ((TextView) mainView.findViewById(R.id.subject_email)).setText(template.getSubject());


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

                template.setTemplateText(text.toString());
                ((TextView) mainView.findViewById(R.id.textMessage_p)).setText(text);
            } else
                ((TextView) mainView.findViewById(R.id.textMessage_p)).setText(template.getTemplateText());

        }
    }

    public void initViews() {
        activityApp.findViewById(R.id.fabMenuContainer).setVisibility(View.GONE);


        if (!template.isTemplateUser()) {
            mainView.findViewById(R.id.l_message_title).setVisibility(View.VISIBLE);
            mainView.findViewById(R.id.footer_message).setVisibility(View.VISIBLE);
            ((TextView) mainView.findViewById(R.id.recipient_email)).setText("<recipient's email>");
            //((TextView)mainView.findViewById(R.id.textMessageHint)).setText("Start typing here _");

            ((TextView) mainView.findViewById(R.id.textMessageHint)).setVisibility(View.VISIBLE);
            ((TextView) mainView.findViewById(R.id.textMessage_p)).setVisibility(View.GONE);


            ((TextView) mainView.findViewById(R.id.footer_message)).setText("\nbest regards, " + ((TextView) activityApp.findViewById(R.id.profileName)).getText().toString());

            //((TextView)mainView.findViewById(R.id.texttemplate)).setText("New template");


            ((TextView) mainView.findViewById(R.id.textMessageHint)).setTextColor(mainView.getContext().getResources().getColor(R.color.gray_textView));


            ((TextView) mainView.findViewById(R.id.subject_email)).setTextColor(mainView.getContext().getResources().getColor(R.color.gray));

            ((TextView) mainView.findViewById(R.id.subject_email)).setText("Email subject");

            //((TextView)mainView.findViewById(R.id.textMessage)).setText("New template");


            //mainView.findViewById(R.id.frameArrMoreMessageData).setVisibility(View.GONE);

            mainView.findViewById(R.id.text_no_files_popup).setVisibility(View.VISIBLE);

        /*    ((TextView)mainView.findViewById(R.id.textMessageHint)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showEditTemplate(template);
                }
            });*/


        } else {

            ((TextView) mainView.findViewById(R.id.subject_email)).setTextColor(mainView.getContext().getResources().getColor(R.color.black));

            ((TextView) mainView.findViewById(R.id.textMessageHint)).setVisibility(View.GONE);
            ((TextView) mainView.findViewById(R.id.textMessage_p)).setVisibility(View.VISIBLE);

            mainView.findViewById(R.id.l_message_title).setVisibility(View.GONE);
            mainView.findViewById(R.id.footer_message).setVisibility(View.GONE);
            ((TextView) mainView.findViewById(R.id.recipient_email)).setText("<recipient's email>");

            //((TextView)mainView.findViewById(R.id.texttemplate)).setText(template.getTitle());

            ((TextView) mainView.findViewById(R.id.subject_email)).setText(template.getSubject());


            //((TextView)mainView.findViewById(R.id.textMessage_p)).setTextColor(mainView.getContext().getResources().getColor(R.color.black));
            //((TextView)mainView.findViewById(R.id.textMessage))

            if (template.getTemplateText().contains("%Name")) {
                int startI = template.getTemplateText().toLowerCase().indexOf("%name");
                final SpannableStringBuilder text = new SpannableStringBuilder(template.getTemplateText());
                final ForegroundColorSpan style = new ForegroundColorSpan(Color.parseColor("#87aade"));
                //StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
                text.setSpan(style, startI, startI + 5, Spannable.SPAN_COMPOSING);
                //text.setSpan(bss, startI, startI + search.length(), Spannable.SPAN_COMPOSING);
                ((TextView) mainView.findViewById(R.id.textMessage_p)).setText(text);
            } else
                ((TextView) mainView.findViewById(R.id.textMessage_p)).setText(template.getTemplateText());

        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {


        Toolbar mainToolBar = (Toolbar) activityApp.findViewById(R.id.main_toolbar);

        if (template == null)
            ((TextView) mainToolBar.findViewById(R.id.toolbar_title)).setText("New template");
        else
            ((TextView) mainToolBar.findViewById(R.id.toolbar_title)).setText(template.getTitle());

        mainToolBar.findViewById(R.id.toolbar_share).setVisibility(View.GONE);
        mainToolBar.findViewById(R.id.toolbar_menu).setVisibility(View.GONE);
        mainToolBar.findViewById(R.id.toolbar_kanban_clicker).setVisibility(View.GONE);

        mainToolBar.findViewById(R.id.toolbar_plane).setVisibility(View.VISIBLE);
        mainToolBar.findViewById(R.id.toolbar_edit).setVisibility(View.VISIBLE);
        mainToolBar.findViewById(R.id.toolbar_menu).setVisibility(View.VISIBLE);




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


                if (template.getEmailDataTemplate() != null) {


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

                                if(template.getTemplateText() == null || template.getTemplateText().isEmpty()) {

                                    String text = ((TextView) mainView.findViewById(R.id.textMessage_1)).getText().toString() + " ";
                                    text += ((TextView) mainView.findViewById(R.id.textMessage_2)).getText().toString() + "\n";
                                    text += ((TextView) mainView.findViewById(R.id.textMessage_p)).getText().toString() + "\n";
                                    text += ((TextView) mainView.findViewById(R.id.footer_message)).getText().toString();

                                    mimeBodyPart.setContent(text, "text/plain; charset=utf-8");
                                }else{


                                    mimeBodyPart.setContent(template.getTemplateText(), "text/plain; charset=utf-8");
                                }


                                Multipart multipart = new MimeMultipart();
                                multipart.addBodyPart(mimeBodyPart);


                                if (template.getListOfFiles() != null) {
                                    for (int i = 0; i < template.getListOfFiles().size(); i++) {

                                        try {
                                            if (template.getListOfFiles().get(i).getFinalUrl() == null) {


                                                InputStream selectedFileInputStream =
                                                        activityApp.getContentResolver().openInputStream(template.getListOfFiles().get(i).getUrl());

                                                byte[] bytes = IOUtils.toByteArray(selectedFileInputStream);
                                                String path = activityApp.getFilesDir() + "/templateFiles";

                                                File folder = new File(path);
                                                if (!folder.exists()) {
                                                    folder.mkdir();
                                                }

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


                                GoogleAccountCredential cred;
                                if(template.getEmailDataTemplate().getAccountCredential() != null){
                                    cred = template.getEmailDataTemplate().getAccountCredential().getCredential();
                                }else{
                                    GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(
                                            activityApp.getApplicationContext(), Arrays.asList(SCOPES))
                                            .setBackOff(new ExponentialBackOff());

                                    credential.setSelectedAccountName(template.getEmailDataTemplate().getRecipient());

                                    cred = credential;
                                }



                                Gmail service = new Gmail.Builder(transport, jsonFactory, cred)
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


                    //activityApp.onBackPressed();

                }


            }
        });

        mainToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.support.v4.app.FragmentManager fragmentManager = ((FragmentActivity) activityApp).getSupportFragmentManager();
                fragmentManager.popBackStack();
                //activityApp.onBackPressed();
            }
        });

        mainToolBar.findViewById(R.id.toolbar_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.support.v4.app.FragmentManager fragmentManager = ((MainActivity) activityApp).getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_content, NewTemplateEditFragment.newInstance(template)).addToBackStack("templateEdit").commit();
            }
        });

    }

    public static String getRealPathFromURI_API19(Context context, Uri uri) {
        String filePath = "";
        String wholeID = DocumentsContract.getDocumentId(uri);

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

        String[] column = {MediaStore.Images.Media.DATA};

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{id}, null);

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        activityApp.findViewById(R.id.main_toolbar).findViewById(R.id.toolbar_plane).setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        activityApp.findViewById(R.id.main_toolbar).findViewById(R.id.toolbar_share).setVisibility(View.GONE);
        activityApp.findViewById(R.id.main_toolbar).setVisibility(View.VISIBLE);
        activityApp.findViewById(R.id.toolbar_template_edit).setVisibility(View.GONE);

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(CheckLinkMessageTemplateFragment event) {
        initLinkMessage();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            activityApp = (Activity) context;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }
}
