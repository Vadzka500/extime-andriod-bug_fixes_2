package ai.extime.Fragments;

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
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.extime.R;
import com.google.android.gms.common.util.IOUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import ai.extime.Adapters.TemplateFileAdapter;
import ai.extime.Enums.FragmentTypeEnum;
import ai.extime.Events.AddTemplate;
import ai.extime.Events.CheckLinkMessageTemplateFragment;
import ai.extime.Events.CheckLinkMessageTemplateFragmentEdit;
import ai.extime.Events.SaveChangesProfile;
import ai.extime.Events.UpdateTemplate;
import ai.extime.Models.FilesTemplate;
import ai.extime.Models.Template;
import ai.extime.Utils.FileType;

public class NewTemplateEditFragment extends Fragment {

    private View mainView;

    private Activity activityApp;

    private Template template;

    public static NewTemplateEditFragment newInstance(Template template) {
        Bundle args = new Bundle();
        args.putSerializable("templateEdit", template);
        NewTemplateEditFragment newTemplateEditFragment = new NewTemplateEditFragment();
        newTemplateEditFragment.setArguments(args);
        return newTemplateEditFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            template = (Template) getArguments().getSerializable("templateEdit");
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(CheckLinkMessageTemplateFragmentEdit event) {
        initLinkMessage();
    }

    public void initLinkMessage(){

            SharedPreferences pref_switch = activityApp.getSharedPreferences("SwitchFab", Context.MODE_PRIVATE);
            boolean switchBoolean = pref_switch.getBoolean("switchMessage", true);

            //View drawerLayout = getLayoutInflater().inflate(R.layout.drawer_layout, null);

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



               /* TextPaint ds = new TextPaint();
                ds.setUnderlineText(false);
                click_span.updateDrawState(ds);*/

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
        mainView = inflater.inflate(R.layout.template_edit_fragment, container, false);
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

    public void initRecycler(){
        if(template.getListOfFiles() != null && !template.getListOfFiles().isEmpty()){
            mainView.findViewById(R.id.linearCountDataMEssage).setVisibility(View.VISIBLE);
            mainView.findViewById(R.id.text_no_files_popup).setVisibility(View.GONE);
            mainView.findViewById(R.id.linear_arg_).setVisibility(View.VISIBLE);
            mainView.findViewById(R.id.frameArrMoreMessageData).setVisibility(View.VISIBLE);



            RecyclerView recyclerView = mainView.findViewById(R.id.linear_arg_);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            TemplateFileAdapter adapter = new TemplateFileAdapter(getContext(), template.getListOfFiles(), template, activityApp);
            recyclerView.setAdapter(adapter);

        }else{
            mainView.findViewById(R.id.linearCountDataMEssage).setVisibility(View.GONE);
            mainView.findViewById(R.id.text_no_files_popup).setVisibility(View.VISIBLE);
        }

    }

    public void initViewsSend(){
        if (!template.isTemplateUser()) {




            ((TextView) mainView.findViewById(R.id.recipient_email_edit)).setText("<"+template.getEmailDataTemplate().getSendTo()+">");
            ((TextView) mainView.findViewById(R.id.nameRec)).setText(template.getEmailDataTemplate().getSendToName().get(0));

            EditText textMessage_edit = mainView.findViewById(R.id.textMessage_edit);
            SpannableString text = new SpannableString("Hello "+template.getEmailDataTemplate().getSendToName()+"\n  \n\nbest regards, " + ((TextView) activityApp.findViewById(R.id.profileName)).getText().toString());

            //text.setSpan(new ForegroundColorSpan(Color.parseColor("#87aade")), 6, 11, 0);

            textMessage_edit.setText(text);
            ((EditText) mainView.findViewById(R.id.textMessage_edit_user)).setVisibility(View.GONE);
            ((EditText) mainView.findViewById(R.id.textMessage_edit)).setVisibility(View.VISIBLE);
            ((EditText) mainView.findViewById(R.id.textMessage_edit)).setSelection(14);
            ((EditText) mainView.findViewById(R.id.textMessage_edit)).requestFocus();

            mainView.findViewById(R.id.linear_arg_).setVisibility(View.GONE);
            mainView.findViewById(R.id.frameArrMoreMessageData).setVisibility(View.GONE);
            //editPopupTemplate.findViewById(R.id.text_no_files_popup_).setVisibility(View.VISIBLE);


            ((EditText) mainView.findViewById(R.id.subject_email_edit)).setText("");

            InputMethodManager imm = (InputMethodManager)
                    getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(((EditText) mainView.findViewById(R.id.textMessage_edit)), InputMethodManager.SHOW_IMPLICIT);


        } else {

            ((EditText) mainView.findViewById(R.id.textMessage_edit)).setVisibility(View.GONE);
            ((EditText) mainView.findViewById(R.id.textMessage_edit_user)).setVisibility(View.VISIBLE);
            SpannableString text = new SpannableString(template.getTemplateText());

            ((TextView) mainView.findViewById(R.id.recipient_email_edit)).setText("<"+template.getEmailDataTemplate().getSendTo()+">");
            ((TextView) mainView.findViewById(R.id.nameRec)).setText(template.getEmailDataTemplate().getSendToName().get(0));

            if (template.getTemplateText().toLowerCase().contains("%name")) {
                //int ind = template.getTemplateText().indexOf("%Name");
                //text.setSpan(new ForegroundColorSpan(Color.parseColor("#87aade")), ind, ind + 5, 0);
                text = new SpannableString(text.toString().replace("%Name", template.getEmailDataTemplate().getSendToName().get(0)));
                text = new SpannableString(text.toString().replace("%name", template.getEmailDataTemplate().getSendToName().get(0)));

                ((EditText) mainView.findViewById(R.id.textMessage_edit_user)).setText(text);

            } else ((EditText) mainView.findViewById(R.id.textMessage_edit_user)).setText(text);


            ((EditText) mainView.findViewById(R.id.subject_email_edit)).setText(template.getSubject());

        }
    }

    public void initViews() {
        if (!template.isTemplateUser()) {


            EditText textMessage_edit = mainView.findViewById(R.id.textMessage_edit);

            ((TextView) mainView.findViewById(R.id.recipient_email_edit)).setText("<recipient's email>");

            SpannableString text = new SpannableString("Hello %Name\n  \n\nbest regards, " + ((TextView) activityApp.findViewById(R.id.profileName)).getText().toString());

            text.setSpan(new ForegroundColorSpan(Color.parseColor("#87aade")), 6, 11, 0);

            textMessage_edit.setText(text);
            ((EditText) mainView.findViewById(R.id.textMessage_edit_user)).setVisibility(View.GONE);
            ((EditText) mainView.findViewById(R.id.textMessage_edit)).setVisibility(View.VISIBLE);
            ((EditText) mainView.findViewById(R.id.textMessage_edit)).setSelection(14);
            ((EditText) mainView.findViewById(R.id.textMessage_edit)).requestFocus();

            mainView.findViewById(R.id.linear_arg_).setVisibility(View.GONE);
            mainView.findViewById(R.id.frameArrMoreMessageData).setVisibility(View.GONE);
            //editPopupTemplate.findViewById(R.id.text_no_files_popup_).setVisibility(View.VISIBLE);


            ((EditText) mainView.findViewById(R.id.subject_email_edit)).setText("");

            InputMethodManager imm = (InputMethodManager)
                    getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(((EditText) mainView.findViewById(R.id.textMessage_edit)), InputMethodManager.SHOW_IMPLICIT);


        } else {

            ((EditText) mainView.findViewById(R.id.textMessage_edit)).setVisibility(View.GONE);
            ((EditText) mainView.findViewById(R.id.textMessage_edit_user)).setVisibility(View.VISIBLE);
            SpannableString text = new SpannableString(template.getTemplateText());

            ((TextView) mainView.findViewById(R.id.recipient_email_edit)).setText("<recipient's email>");

            if (template.getTemplateText().contains("%Name")) {
                int ind = template.getTemplateText().indexOf("%Name");
                text.setSpan(new ForegroundColorSpan(Color.parseColor("#87aade")), ind, ind + 5, 0);
                ((EditText) mainView.findViewById(R.id.textMessage_edit_user)).setText(text);

            } else ((EditText) mainView.findViewById(R.id.textMessage_edit_user)).setText(text);


            ((EditText) mainView.findViewById(R.id.subject_email_edit)).setText(template.getSubject());

        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        activityApp.findViewById(R.id.toolbar_template_edit).setVisibility(View.VISIBLE);
        //activityApp.findViewById(R.id.main_toolbar).setVisibility(View.GONE);

        Toolbar mainToolBar = (Toolbar) activityApp.findViewById(R.id.toolbar_template_edit);


        mainToolBar.findViewById(R.id.cancel_toolbar_template).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityApp.onBackPressed();
            }
        });

        mainToolBar.findViewById(R.id.save_toolbar_template).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String text = "Do you want to save changes?";
                if (!template.isTemplateUser() && template.getEmailDataTemplate() == null)
                    text = "Do you want to create new template?";

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        activityApp);
                alertDialogBuilder.setTitle(text);
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Yes", (dialog, id) -> {

                            if (!template.isTemplateUser()) {


                                if (((EditText) mainToolBar.findViewById(R.id.toolbar_title_template)).getText().length() == 0) {
                                    template.setTitle("New template");
                                } else {
                                    template.setTitle(((EditText) mainToolBar.findViewById(R.id.toolbar_title_template)).getText().toString());
                                }


                                if (((EditText) mainView.findViewById(R.id.subject_email_edit)).getText().length() == 0) {
                                    template.setSubject("No subject");
                                } else {
                                    template.setSubject(((EditText) mainView.findViewById(R.id.subject_email_edit)).getText().toString());
                                }

                                template.setTemplateUser(true);
                                template.setTemplateText(((EditText) mainView.findViewById(R.id.textMessage_edit)).getText().toString());

                                template.setDateCreate(new Date());

                                Date date1 = template.getDateCreate();
                                Calendar cal1 = Calendar.getInstance();
                                cal1.setTime(date1);
                                Time time1 = getRandomDate();
                                time1.setHours(cal1.get(Calendar.HOUR_OF_DAY));
                                time1.setMinutes(cal1.get(Calendar.MINUTE));
                                time1.setSeconds(cal1.get(Calendar.SECOND));

                                template.time = time1.toString();

                                if(template.getEmailDataTemplate() == null)

                                EventBus.getDefault().post(new AddTemplate(template));


                            } else {
                                if (((EditText) mainToolBar.findViewById(R.id.toolbar_title_template)).getText().length() == 0) {
                                    template.setTitle("New template");
                                } else {
                                    template.setTitle(((EditText) mainToolBar.findViewById(R.id.toolbar_title_template)).getText().toString());
                                }

                                if (((EditText) mainView.findViewById(R.id.subject_email_edit)).getText().length() == 0) {
                                    template.setSubject("No subject");
                                } else {
                                    template.setSubject(((EditText) mainView.findViewById(R.id.subject_email_edit)).getText().toString());
                                }

                                template.setTemplateUser(true);
                                template.setTemplateText(((EditText) mainView.findViewById(R.id.textMessage_edit_user)).getText().toString());


                                if(template.getEmailDataTemplate() == null)
                                EventBus.getDefault().post(new UpdateTemplate(template));


                            }

                            activityApp.onBackPressed();

                        })
                        .setNegativeButton("No", (dialog, id) -> {


                            dialog.cancel();
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();


            }
        });

        ((EditText) mainToolBar.findViewById(R.id.toolbar_title_template)).addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onTextChanged(CharSequence arg0, int start, int before,
                                      int count) {
                if (arg0.length() == 0) {
                    // No entered text so will show hint
                    ((EditText) mainToolBar.findViewById(R.id.toolbar_title_template)).setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);
                } else {
                    ((EditText) mainToolBar.findViewById(R.id.toolbar_title_template)).setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                }
            }
        });


        if (template != null && !template.getTitle().equalsIgnoreCase("New template"))
            ((EditText) mainToolBar.findViewById(R.id.toolbar_title_template)).setText(template.getTitle());
        else
            ((EditText) mainToolBar.findViewById(R.id.toolbar_title_template)).setText("");


    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == 42 && resultCode ==Activity.RESULT_OK) {
            Uri[] uriPaths;

            if (resultData == null || (resultData.getData() == null && resultData.getClipData() == null)) {
                //Toast.makeText(getContext(), R.string.invalid_source, Toast.LENGTH_SHORT).show();
                return;
            }

            if (resultData.getData() != null) {
                uriPaths = new Uri[1];
                uriPaths[0] = resultData.getData();
                System.out.println("URI = "+uriPaths[0]);



                activityApp.getContentResolver().takePersistableUriPermission(uriPaths[0], Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);




                File f = new File(uriPaths[0].toString());

                System.out.println("FILE = "+f.getAbsolutePath());

                //dumpImageMetaData(uriPaths[0]);

                FilesTemplate ft = new FilesTemplate(uriPaths[0]);

                FileType.getNameSize(ft, activityApp);

                ft.setUrl(Uri.parse(dumpImageMetaData(uriPaths[0])));

                template.addFile(ft);

                initRecycler();

                /*if(template.getEmailDataTemplate() == null && template.isTemplateUser())
                    EventBus.getDefault().post(new UpdateTemplate(template));
                else if(!template.isTemplateUser()) {
                    template.setTemplateUser(true);
                    template.setSubject("No subject");
                    template.setTemplateText("");
                    template.setDateCreate(new Date());
                    EventBus.getDefault().post(new AddTemplate(template));
                }*/


            }else if (resultData.getClipData() != null) {
                int selectedCount = resultData.getClipData().getItemCount();
                uriPaths = new Uri[selectedCount];
                for (int i = 0; i < selectedCount; i++) {
                    uriPaths[i] = resultData.getClipData().getItemAt(i).getUri();

                    activityApp.getContentResolver().takePersistableUriPermission(uriPaths[i], Intent.FLAG_GRANT_READ_URI_PERMISSION
                            | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                    System.out.println("Uri = "+uriPaths[i]);
                    FilesTemplate ft = new FilesTemplate(uriPaths[i]);

                    FileType.getNameSize(ft, activityApp);

                    ft.setUrl(Uri.parse(dumpImageMetaData(uriPaths[i])));

                    template.addFile(ft);


                    //template.addFile(uriPaths[i]);
                }

                initRecycler();

                /*if(template.getEmailDataTemplate() == null && template.isTemplateUser())
                    EventBus.getDefault().post(new UpdateTemplate(template));
                else if(!template.isTemplateUser()) {
                    template.setTemplateUser(true);
                    template.setSubject("No subject");
                    template.setTemplateText("");
                    template.setDateCreate(new Date());
                    EventBus.getDefault().post(new AddTemplate(template));
                }*/

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

    public Time getRandomDate() {
        final Random random = new Random();
        final int millisInDay = 24 * 60 * 60 * 1000;
        return new Time((long) random.nextInt(millisInDay));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        activityApp.findViewById(R.id.main_toolbar).setVisibility(View.VISIBLE);
        activityApp.findViewById(R.id.toolbar_template_edit).setVisibility(View.GONE);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            activityApp = (Activity) context;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }
}
