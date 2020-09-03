package ai.extime.Utils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.extime.R;
import com.github.clans.fab.FloatingActionMenu;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.snatik.storage.Storage;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import ai.extime.Activity.MainActivity;
import ai.extime.Adapters.AccountAdapter;
import ai.extime.Adapters.ShareTemplateAdapter;
import ai.extime.Events.StartProcessDial;
import ai.extime.Fragments.ProfileFragment;
import ai.extime.Interfaces.IMessage;
import ai.extime.Models.EmailMessage;
import ai.extime.Models.Template;

public class ShareTemplatesMessageReply {

    public static boolean popup;

    public static ArrayList<String> name;

    public static ArrayList<String> sender;

    public static String recipient;

    public static EmailMessage emailMessage;

    public static IMessage iMessage;

    public static boolean typeReply;


    public static void showTemplatesPopup(Activity activity, boolean popup, ArrayList<String> senderEmail, ArrayList<String> senderName, String recipient, EmailMessage message, IMessage iMessage, boolean typeReply) {

        ShareTemplatesMessageReply.popup = popup;
        ShareTemplatesMessageReply.name = senderName;
        ShareTemplatesMessageReply.sender = senderEmail;
        ShareTemplatesMessageReply.recipient = recipient;
        ShareTemplatesMessageReply.emailMessage = message;
        ShareTemplatesMessageReply.iMessage = iMessage;
        ShareTemplatesMessageReply.typeReply = typeReply;

        System.out.println("Send to = " + sender);
        System.out.println("SenderName = " + senderName);
        System.out.println("Recip = " + recipient);


        activity.findViewById(R.id.share_template_message).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidePopup(activity);
            }
        });


        showPopup(activity);

    }

    public static void showChooseAccount(Activity activity, boolean popup, ArrayList<String> senderEmail, ArrayList<String> senderName, String recipient, EmailMessage message, IMessage iMessage, boolean typeReply){


        ShareTemplatesMessageReply.popup = popup;
        ShareTemplatesMessageReply.name = senderName;
        ShareTemplatesMessageReply.sender = senderEmail;
        ShareTemplatesMessageReply.recipient = recipient;
        ShareTemplatesMessageReply.emailMessage = message;
        ShareTemplatesMessageReply.iMessage = iMessage;
        ShareTemplatesMessageReply.typeReply = typeReply;

        LinearLayout popupAccounts = activity.findViewById(R.id.accountContainer);;

        popupAccounts.setVisibility(View.VISIBLE);

        //((CheckBox) mainView.findViewById(R.id.all_select)).setChecked(false);

        FloatingActionMenu floatingActionMenu = (FloatingActionMenu) activity.findViewById(R.id.fabMenuContainer);

        //getActivity().findViewById(R.id.fab).setVisibility(View.GONE);

        floatingActionMenu.close(false);
        floatingActionMenu.setVisibility(View.GONE);


                       /* recyclerAcc2 = getActivity().findViewById(R.id.recycler_account);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mainView.getContext());
                        recyclerAcc2.setLayoutManager(mLayoutManager);*/

        Account[] accounts = AccountManager.get(activity).getAccountsByType("com.google");


        SharedPreferences mSettings;
        mSettings = activity.getSharedPreferences("accountUser", Context.MODE_PRIVATE);
        Set<String> set = mSettings.getStringSet("accounts", null);

        ArrayList<Boolean> listCheck = new ArrayList<>();
        ArrayList<Account> list = new ArrayList<>();
        for (Account account : accounts) {
            list.add(account);
            if (set != null && set.contains(account.name)) {
                listCheck.add(true);

            } else {
                listCheck.add(false);

            }
        }



        /*if (set.size() == accounts.length) {
            ((CheckBox) mainView.findViewById(R.id.all_select)).setChecked(true);
        }*/

        if (set != null && set.size() > 0) {
            ((TextView) popupAccounts.findViewById(R.id.apply_account)).setTextColor(activity.getResources().getColor(R.color.primary));
        }

        if (set == null || set.isEmpty()) {
            ((TextView) popupAccounts.findViewById(R.id.count_account)).setText("0");
        } else
            ((TextView) popupAccounts.findViewById(R.id.count_account)).setText(String.valueOf(set.size()));

        //  if (accountAdapter == null) {

        TextView textView =  ((TextView)popupAccounts.findViewById(R.id.count_account));

        CheckBox checkBox = popupAccounts.findViewById(R.id.all_accounts_select);

        AccountAdapter accountAdapter = new AccountAdapter(list, activity, popupAccounts.findViewById(R.id.all_account), popupAccounts.findViewById(R.id.apply_account), activity.findViewById(R.id.count_acc), listCheck, textView, checkBox);

        // }

        RecyclerView recyclerAcc2 = popupAccounts.findViewById(R.id.recycler_account);


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity);

        RecyclerView.LayoutManager mLayoutManager2 = new LinearLayoutManager(activity);
        recyclerAcc2.setLayoutManager(mLayoutManager);


        recyclerAcc2.setAdapter(accountAdapter);




        ((CheckBox) popupAccounts.findViewById(R.id.all_account)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (((CheckBox) popupAccounts.findViewById(R.id.all_account)).isChecked()) {
                    accountAdapter.updateAllSelect(true);
                    ((TextView) popupAccounts.findViewById(R.id.apply_account)).setTextColor(activity.getResources().getColor(R.color.primary));
                } else {
                    accountAdapter.updateAllSelect(false);
                    ((TextView) popupAccounts.findViewById(R.id.apply_account)).setTextColor(activity.getResources().getColor(R.color.gray));
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
                if (accountAdapter != null) {
                    if (!MainActivity.hasConnection(activity)) {
                        Toast.makeText(activity, "Check your internet connection", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else {
                    try {
                        if (!MainActivity.hasConnection(activity)) {
                            Toast.makeText(activity, "Check your internet connection", Toast.LENGTH_SHORT).show();
                            return;
                        }

                    } catch (Exception e) {

                    }
                }
                if (accountAdapter != null && accountAdapter.getListSelectId() != null && accountAdapter.getListSelectId().size() > 0) {

                 /*   mainView.findViewById(R.id.countAccount).setVisibility(View.GONE);
                    mainView.findViewById(R.id.frame_choose_google_account).setVisibility(View.GONE);
                    mainView.findViewById(R.id.listAccounts).setVisibility(View.GONE);
                    mainView.findViewById(R.id.frame_button_google_message).setVisibility(View.GONE);
                    mainView.findViewById(R.id.spaceMessage).setVisibility(View.GONE);*/

                    popupAccounts.setVisibility(View.GONE);

                    //mainView.findViewById(R.id.frame_google_count).setVisibility(View.VISIBLE);
                    //((TextView) mainView.findViewById(R.id.text_count_google_account)).setText(String.valueOf(accountAdapter.getListSelectId().size()));


                    if (accountAdapter.getListSelectId().size() == 1)
                        Toast.makeText(activity, "1 gmail account selected", Toast.LENGTH_SHORT).show();
                    else if (accountAdapter.getListSelectId().size() > 1)
                        Toast.makeText(activity, accountAdapter.getListSelectId().size() + " gmail accounts selected", Toast.LENGTH_SHORT).show();

                    FloatingActionMenu floatingActionMenu = (FloatingActionMenu) activity.findViewById(R.id.fabMenuContainer);
                    floatingActionMenu.setVisibility(View.GONE);

                    //mainView.findViewById(R.id.frameGetMessage).setVisibility(View.VISIBLE);


                    //listCredential = new ArrayList<>();

                    /*for (int i = 0; i < accountAdapter.getListSelectId().size(); i++) {

                        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(
                                getActivity().getApplicationContext(), Arrays.asList(SCOPES))
                                .setBackOff(new ExponentialBackOff());

                        credential.setSelectedAccountName(accountAdapter.getListSelectId().get(i).name);

                        listCredential.add(credential);

                    }*/



                    //getMessages();


                    //int px = (int) (54 * getContext().getResources().getDisplayMetrics().density);

                    //ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mainView.findViewById(R.id.frameChooseAccounts).getLayoutParams();
                    //params.topMargin = 0;

                    Set<String> set = new HashSet<>();
                    for (int i = 0; i < accountAdapter.getListSelectId().size(); i++) {
                        set.add(accountAdapter.getListSelectId().get(i).name);

                        if(i == 0){
                            ShareTemplatesMessageReply.recipient = accountAdapter.getListSelectId().get(i).name;
                        }
                    }

                    SharedPreferences mSettings;
                    mSettings = activity.getSharedPreferences("accountUser", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = mSettings.edit();
                    editor.putStringSet("accounts", set);
                    editor.commit();


                    activity.findViewById(R.id.share_template_message).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            hidePopup(activity);
                        }
                    });


                    showPopup(activity);

                } else {
                    try {
                        Toast.makeText(activity, "Choose account", Toast.LENGTH_SHORT).show();
                    }catch (NullPointerException e){
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public static ArrayList<Template> getData(Activity activity) {
        ArrayList<Template> list = new ArrayList<>();

        Storage storage = new Storage(activity.getApplicationContext());
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        String content = null;
        try {
            content = storage.readTextFile(path + "/Extime/ExtimeContacts/backupTemplates");
        } catch (Exception e) {
            e.printStackTrace();

        }

        if(ShareTemplatesMessageReply.typeReply) {
            Template new_template = new Template("Reply", false, null);
            new_template.setTemplateText("Hello %Name\n\n\n\nbest regards,\n"+((TextView)activity.findViewById(R.id.profileName)).getText());
            list.add(new_template);
        }else{
            Template new_template = new Template("New mail", false, null);
            new_template.setTemplateText("Hello %Name\n\n\n\nbest regards,\n"+((TextView)activity.findViewById(R.id.profileName)).getText());
            list.add(new_template);
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

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showPopup(Activity activity) {

        hideKeyboard(activity);

        activity.findViewById(R.id.share_template_message).setVisibility(View.VISIBLE);

        TranslateAnimation translateAnimation2 = new TranslateAnimation(0, 0, 50, 0);
        translateAnimation2.setFillEnabled(true);
        translateAnimation2.setDuration(300);
        translateAnimation2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                activity.findViewById(R.id.share_template_message).setTranslationY(0f);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
        fadeIn.setDuration(300);
        AnimationSet animation = new AnimationSet(false);
        animation.addAnimation(fadeIn);
        animation.addAnimation(translateAnimation2);
        activity.findViewById(R.id.share_template_message).setAnimation(animation);
        animation.start();


        RecyclerView recycler_tem_share = activity.findViewById(R.id.recycler_tem_share);
        recycler_tem_share.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));

        ShareTemplateAdapter shareTemplateAdapter = new ShareTemplateAdapter(activity, getData(activity));
        recycler_tem_share.setAdapter(shareTemplateAdapter);

        ((TextView) activity.findViewById(R.id.select_templates_share)).setText("select from " + getData(activity).size() + " templates");


        activity.findViewById(R.id.share_template_message).findViewById(R.id.sendDefault).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:" + recipient));
                activity.startActivity(Intent.createChooser(emailIntent, "Send email"));
            }
        });

        activity.findViewById(R.id.share_template_message).findViewById(R.id.sendGmail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("message/rfc822");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{sender.get(0)});
                intent.putExtra(Intent.EXTRA_SUBJECT, "");
                intent.setPackage("com.google.android.gm");
                if (intent.resolveActivity(activity.getPackageManager()) != null)
                    activity.startActivity(intent);
            }
        });

        activity.findViewById(R.id.share_template_message).findViewById(R.id.sendToExtime).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:" + recipient));
                activity.startActivity(Intent.createChooser(emailIntent, "Send email"));*/
                MainActivity.searchData = "mailto:"+sender.get(0);
                EventBus.getDefault().post(new StartProcessDial());
                activity.findViewById(R.id.share_template_message).setVisibility(View.GONE);
            }
        });


    }

    public static void hidePopup(Activity activity) {
        activity.findViewById(R.id.share_template_message).setVisibility(View.GONE);

        TranslateAnimation translateAnimation2 = new TranslateAnimation(0, 0, 0, 50);
        translateAnimation2.setFillEnabled(true);
        translateAnimation2.setDuration(200);
        translateAnimation2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                activity.findViewById(R.id.share_template_message).setTranslationY(0f);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        Animation fadeIn = new AlphaAnimation(1, 0);
        fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
        fadeIn.setDuration(200);
        AnimationSet animation = new AnimationSet(false);
        animation.addAnimation(fadeIn);
        animation.addAnimation(translateAnimation2);
        activity.findViewById(R.id.share_template_message).setAnimation(animation);
        animation.start();
    }


}
