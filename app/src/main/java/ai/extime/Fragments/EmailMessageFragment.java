package ai.extime.Fragments;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.util.Linkify;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.extime.R;
import com.github.clans.fab.FloatingActionMenu;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.MessagePartBody;
import com.google.api.services.gmail.model.ModifyMessageRequest;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

import ai.extime.Activity.MainActivity;
import ai.extime.Adapters.CompanyAdapter;
import ai.extime.Adapters.ContactAdapter;
import ai.extime.Adapters.ContactsOfMessageAdapter;
import ai.extime.Adapters.MessageDataAdapter;
import ai.extime.Adapters.MessageDataExtratorAdapter;
import ai.extime.Enums.ClipboardEnum;
import ai.extime.Events.UpdateFile;
import ai.extime.Events.UpdateMessageAdapter;
import ai.extime.Interfaces.Postman;
import ai.extime.Models.Clipboard;
import ai.extime.Models.Contact;
import ai.extime.Models.ContactInfo;
import ai.extime.Models.ContactOfMessage;
import ai.extime.Models.EmailMessage;
import ai.extime.Models.HashTag;
import ai.extime.Models.MessageData;
import ai.extime.Models.SocialModel;
import ai.extime.Services.ContactCacheService;
import ai.extime.Utils.ClipboardType;
import ai.extime.Utils.ShareTemplatesMessageReply;
import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmList;

public class EmailMessageFragment extends Fragment {

    private View mainView;

    private EmailMessage message;

    private ArrayList<String> emailConatct;

    private String type;

    private String nameContact;

    private boolean startSearchContact = true;

    private FrameLayout popupContacts;

    private ViewTreeObserver.OnGlobalLayoutListener checkKeyboardListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            Rect r = new Rect();
            mainView.getWindowVisibleDisplayFrame(r);
            int screenHeight = mainView.getRootView().getHeight();
            int keypadHeight = screenHeight - r.bottom;

            if (keypadHeight > screenHeight * 0.15) {

               /* isKeyboard = true;


                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mainView.findViewById(R.id.popupMessage).getLayoutParams();

                layoutParams.gravity = Gravity.TOP;

                layoutParams.topMargin = 0;

                if(mainView.findViewById(R.id.test1).getVisibility() == View.VISIBLE) {

                           *//* float px_b = 280 * getContext().getResources().getDisplayMetrics().density;

                            layoutParams.height = (int) px_b;*//*

                }*/

                if (mainView.findViewById(R.id.test1).getVisibility() == View.VISIBLE) {

                    LinearLayout.LayoutParams lp1 = (LinearLayout.LayoutParams) mainView.findViewById(R.id.recyclerDataMessage).getLayoutParams();

                    float px = 60 * getContext().getResources().getDisplayMetrics().density;

                    lp1.height = (int) px;

                    mainView.findViewById(R.id.recyclerDataMessage).setLayoutParams(lp1);

                }


            } else {

               /* isKeyboard = false;

                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mainView.findViewById(R.id.popupMessage).getLayoutParams();

                //layoutParams.gravity = Gravity.NO_GRAVITY;

                int px_margin_top = (int) (145 * mainView.getContext().getResources().getDisplayMetrics().density);

                layoutParams.topMargin = px_margin_top;*/


                if (mainView.findViewById(R.id.test1).getVisibility() == View.VISIBLE) {

                    LinearLayout.LayoutParams lp1 = (LinearLayout.LayoutParams) mainView.findViewById(R.id.recyclerDataMessage).getLayoutParams();

                    float px = 125 * getContext().getResources().getDisplayMetrics().density;

                    lp1.height = (int) px;

                    mainView.findViewById(R.id.recyclerDataMessage).setLayoutParams(lp1);

                    /*FrameLayout layoutParams1 = mainView.findViewById(R.id.popupMessage).findViewById(R.id.frameTextMess);

                    LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) layoutParams1.getLayoutParams();

                    float px = (float) (80 * mainView.getContext().getResources().getDisplayMetrics().density);

                    //122.5

                    lp.weight = 0;

                    lp.height = (int) px;

                    layoutParams1.setLayoutParams(lp);

                    FrameLayout.LayoutParams lp1 = (FrameLayout.LayoutParams) mainView.findViewById(R.id.popupMessage).findViewById(R.id.popup_mess).getLayoutParams();

                    lp1.height = ViewGroup.LayoutParams.MATCH_PARENT;

                    mainView.findViewById(R.id.popupMessage).findViewById(R.id.popup_mess).setLayoutParams(lp1);

                    FrameLayout.LayoutParams lp2 = (FrameLayout.LayoutParams) mainView.findViewById(R.id.popupMessage).getLayoutParams();

                    lp2.height = ViewGroup.LayoutParams.MATCH_PARENT;

                    mainView.findViewById(R.id.popupMessage).setLayoutParams(lp2);*/
                }
            }

        }
    };

    public static EmailMessageFragment newInstance(EmailMessage message, ArrayList<String> emailContact, String nameContact, String type) {
        Bundle args = new Bundle();
        args.putSerializable("message", message);
        args.putSerializable("emails", emailContact);
        args.putSerializable("nameContact", nameContact);
        args.putSerializable("type", type);
        EmailMessageFragment fragment = new EmailMessageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && getArguments().getSerializable("message") != null && getArguments().getSerializable("emails") != null && getArguments().getSerializable("nameContact") != null && getArguments().getSerializable("type") != null) {
            message = (EmailMessage) getArguments().getSerializable("message");
            emailConatct = getArguments().getStringArrayList("emails");
            if (emailConatct == null) emailConatct = new ArrayList<>();
            nameContact = (String) getArguments().getSerializable("nameContact");
            type = (String) getArguments().getSerializable("type");
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_message, container, false);
        setHasOptionsMenu(true);

        initViews();
        initKeyboard();

        return mainView;
    }

    public void initKeyboard() {
        mainView.getViewTreeObserver().addOnGlobalLayoutListener(checkKeyboardListener);
    }


    String sendTo;
    String sendToName;
    String replyFrom;

    public void initViews() {

        startSearchContact = true;

        popupContacts = mainView.findViewById(R.id.popupMessageContact);


        initSender_recipient();

        initTimeMessage();

        initMessage_title();

        ((TextView) mainView.findViewById(R.id.textMessage)).setMovementMethod(LinkMovementMethod.getInstance());

        ((TextView) mainView.findViewById(R.id.textMessage)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        ((TextView) mainView.findViewById(R.id.textMessage)).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                return false;
            }
        });

        mainView.findViewById(R.id.frame_open_contact_).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openContactList(message);
            }
        });

        mainView.findViewById(R.id.secondC).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openContactList(message);
            }
        });

        mainView.findViewById(R.id.firstC).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openContactList(message);
            }
        });

        mainView.findViewById(R.id.timeMessage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openContactList(message);
            }
        });

        if(message.getListOfTypeMessage().contains("STARRED")){
            mainView.findViewById(R.id.favoriteMessageProfile).setVisibility(View.VISIBLE);
            mainView.findViewById(R.id.favoriteMessageProfileEmpty).setVisibility(View.GONE);
        }else{
            mainView.findViewById(R.id.favoriteMessageProfile).setVisibility(View.GONE);
            mainView.findViewById(R.id.favoriteMessageProfileEmpty).setVisibility(View.VISIBLE);
        }

        if(message.getListOfTypeMessage().contains("DRAFT")){
            mainView.findViewById(R.id.draft_message_text).setVisibility(View.VISIBLE);
        }else{
            mainView.findViewById(R.id.draft_message_text).setVisibility(View.GONE);
        }

        mainView.findViewById(R.id.favoriteMessageProfile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setStarMessage(message, false);
                mainView.findViewById(R.id.favoriteMessageProfile).setVisibility(View.GONE);
                mainView.findViewById(R.id.favoriteMessageProfileEmpty).setVisibility(View.VISIBLE);
                Toast.makeText(mainView.getContext(), "Deleted from Starred", Toast.LENGTH_SHORT).show();
            }
        });

        mainView.findViewById(R.id.favoriteMessageProfileEmpty).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setStarMessage(message, true);
                mainView.findViewById(R.id.favoriteMessageProfile).setVisibility(View.VISIBLE);
                mainView.findViewById(R.id.favoriteMessageProfileEmpty).setVisibility(View.GONE);
                Toast.makeText(mainView.getContext(), "Successfully added to Starred", Toast.LENGTH_SHORT).show();
            }
        });



        mainView.findViewById(R.id.fragment_menu_message).setVisibility(View.GONE);
        mainView.findViewById(R.id.closerFragmentMessage).setVisibility(View.GONE);


        mainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().findViewById(R.id.popup_menu_profile).setVisibility(View.GONE);
            }
        });

        if (message.getMessageData().size() > 0) {
            mainView.findViewById(R.id.linearCountDataMEssage).setVisibility(View.VISIBLE);
            mainView.findViewById(R.id.linear_arg).setVisibility(View.VISIBLE);
            //mainView.findViewById(R.id.line_button_3).setVisibility(View.VISIBLE);

            RecyclerView recyclerView = mainView.findViewById(R.id.linear_arg);

            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            MessageDataAdapter adapter = new MessageDataAdapter(getContext(), message.getMessageData(), message);

            recyclerView.setAdapter(adapter);
        } else {
            mainView.findViewById(R.id.linearCountDataMEssage).setVisibility(View.GONE);
            mainView.findViewById(R.id.linear_arg).setVisibility(View.GONE);
            //mainView.findViewById(R.id.line_button_3).setVisibility(View.GONE);
        }

        if(!message.isCheckData()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {


                        ArrayList<Clipboard> listC = new ArrayList<>(ClipboardType.getListDataClipboard(message.getMessage(), getActivity()));


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

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                message.setCheckData(true);

                                if (message.getMessageData().size() > 0) {
                                    mainView.findViewById(R.id.linearCountDataMEssage).setVisibility(View.VISIBLE);
                                    mainView.findViewById(R.id.linear_arg).setVisibility(View.VISIBLE);
                                    //mainView.findViewById(R.id.line_button_3).setVisibility(View.VISIBLE);

                                    RecyclerView recyclerView = mainView.findViewById(R.id.linear_arg);

                                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                                    MessageDataAdapter adapter = new MessageDataAdapter(getContext(), message.getMessageData(), message);

                                    recyclerView.setAdapter(adapter);
                                } else {
                                    mainView.findViewById(R.id.linearCountDataMEssage).setVisibility(View.GONE);
                                    mainView.findViewById(R.id.linear_arg).setVisibility(View.GONE);
                                    //mainView.findViewById(R.id.line_button_3).setVisibility(View.GONE);
                                }


                                /*if (message.getMessageData() == null || message.getMessageData().isEmpty()) {
                                    ((ImageView) mainView.findViewById(R.id.imageExMessage)).setColorFilter(ContextCompat.getColor(getContext(), R.color.gray));
                                } else
                                    ((ImageView) mainView.findViewById(R.id.imageExMessage)).setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimary));*/


                            }
                        });


                    } catch (Exception w) {
                        w.printStackTrace();
                    }

                }
            }).start();

        }


        if (message.getMessageData() == null || message.getMessageData().isEmpty()) {
            ((ImageView) mainView.findViewById(R.id.imageExMessage)).setColorFilter(ContextCompat.getColor(getContext(), R.color.gray));
        } else
            ((ImageView) mainView.findViewById(R.id.imageExMessage)).setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimary));






        mainView.findViewById(R.id.linear_arg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mainView.findViewById(R.id.deleteMessage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mainView.findViewById(R.id.replyMessage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList<String> sTo = new ArrayList<>();
                ArrayList<String> sName = new ArrayList<>();

                sTo.add( sendTo);
                sName.add(sendToName);

                ShareTemplatesMessageReply.showTemplatesPopup(getActivity(), false, sTo, sName, replyFrom, message, null, true);
            }
        });

        mainView.findViewById(R.id.gmailMessage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mainView.findViewById(R.id.remindMessage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mainView.findViewById(R.id.frameArrMoreMessageData).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showExtratorData(message);
            }
        });

        mainView.findViewById(R.id.extratorMessage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (message.getMessageData() != null && !message.getMessageData().isEmpty()) {
                    showExtratorData(message);
                }

            }
        });

        mainView.findViewById(R.id.shareMessage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mainView.findViewById(R.id.fragmentMessage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainView.findViewById(R.id.fragment_menu_message).setVisibility(View.GONE);
                mainView.findViewById(R.id.closerFragmentMessage).setVisibility(View.GONE);
            }
        });

        mainView.findViewById(R.id.replyMeny).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainView.findViewById(R.id.replyMessage).callOnClick();
            }
        });

        mainView.findViewById(R.id.menu_fragment_message).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainView.findViewById(R.id.fragment_menu_message).findViewById(R.id.menu_message_reply).setVisibility(View.VISIBLE);
                mainView.findViewById(R.id.fragment_menu_message).findViewById(R.id.menu_message_replyAll).setVisibility(View.VISIBLE);
                mainView.findViewById(R.id.fragment_menu_message).findViewById(R.id.menu_message_forward).setVisibility(View.VISIBLE);
                mainView.findViewById(R.id.fragment_menu_message).findViewById(R.id.menu_message_mark).setVisibility(View.VISIBLE);
                mainView.findViewById(R.id.fragment_menu_message).findViewById(R.id.menu_message_mardUnread).setVisibility(View.VISIBLE);
                mainView.findViewById(R.id.fragment_menu_message).findViewById(R.id.menu_message_saveAsTemplate).setVisibility(View.VISIBLE);
                mainView.findViewById(R.id.fragment_menu_message).findViewById(R.id.menu_message_more).setVisibility(View.VISIBLE);



                mainView.findViewById(R.id.fragment_menu_message).findViewById(R.id.menu_message_less).setVisibility(View.GONE);
                mainView.findViewById(R.id.fragment_menu_message).findViewById(R.id.menu_message_share).setVisibility(View.GONE);
                mainView.findViewById(R.id.fragment_menu_message).findViewById(R.id.menu_message_detector).setVisibility(View.GONE);
                mainView.findViewById(R.id.fragment_menu_message).findViewById(R.id.menu_message_massSend).setVisibility(View.GONE);
                mainView.findViewById(R.id.fragment_menu_message).findViewById(R.id.menu_message_saveAs).setVisibility(View.GONE);
                mainView.findViewById(R.id.fragment_menu_message).findViewById(R.id.menu_message_delete).setVisibility(View.GONE);
                mainView.findViewById(R.id.fragment_menu_message).findViewById(R.id.menu_message_remind).setVisibility(View.GONE);

                mainView.findViewById(R.id.fragment_menu_message).findViewById(R.id.menu_message_less).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mainView.findViewById(R.id.fragment_menu_message).findViewById(R.id.menu_message_reply).setVisibility(View.VISIBLE);
                        mainView.findViewById(R.id.fragment_menu_message).findViewById(R.id.menu_message_replyAll).setVisibility(View.VISIBLE);
                        mainView.findViewById(R.id.fragment_menu_message).findViewById(R.id.menu_message_forward).setVisibility(View.VISIBLE);
                        mainView.findViewById(R.id.fragment_menu_message).findViewById(R.id.menu_message_mark).setVisibility(View.VISIBLE);
                        mainView.findViewById(R.id.fragment_menu_message).findViewById(R.id.menu_message_mardUnread).setVisibility(View.VISIBLE);
                        mainView.findViewById(R.id.fragment_menu_message).findViewById(R.id.menu_message_saveAsTemplate).setVisibility(View.VISIBLE);
                        mainView.findViewById(R.id.fragment_menu_message).findViewById(R.id.menu_message_more).setVisibility(View.VISIBLE);

                        mainView.findViewById(R.id.fragment_menu_message).findViewById(R.id.menu_message_less).setVisibility(View.GONE);
                        mainView.findViewById(R.id.fragment_menu_message).findViewById(R.id.menu_message_share).setVisibility(View.GONE);
                        mainView.findViewById(R.id.fragment_menu_message).findViewById(R.id.menu_message_detector).setVisibility(View.GONE);
                        mainView.findViewById(R.id.fragment_menu_message).findViewById(R.id.menu_message_massSend).setVisibility(View.GONE);
                        mainView.findViewById(R.id.fragment_menu_message).findViewById(R.id.menu_message_saveAs).setVisibility(View.GONE);
                        mainView.findViewById(R.id.fragment_menu_message).findViewById(R.id.menu_message_delete).setVisibility(View.GONE);
                        mainView.findViewById(R.id.fragment_menu_message).findViewById(R.id.menu_message_remind).setVisibility(View.GONE);
                    }
                });

                mainView.findViewById(R.id.fragment_menu_message).findViewById(R.id.menu_message_more).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mainView.findViewById(R.id.fragment_menu_message).findViewById(R.id.menu_message_reply).setVisibility(View.GONE);
                        mainView.findViewById(R.id.fragment_menu_message).findViewById(R.id.menu_message_replyAll).setVisibility(View.GONE);
                        mainView.findViewById(R.id.fragment_menu_message).findViewById(R.id.menu_message_forward).setVisibility(View.GONE);
                        mainView.findViewById(R.id.fragment_menu_message).findViewById(R.id.menu_message_mark).setVisibility(View.GONE);
                        mainView.findViewById(R.id.fragment_menu_message).findViewById(R.id.menu_message_mardUnread).setVisibility(View.GONE);
                        mainView.findViewById(R.id.fragment_menu_message).findViewById(R.id.menu_message_saveAsTemplate).setVisibility(View.GONE);
                        mainView.findViewById(R.id.fragment_menu_message).findViewById(R.id.menu_message_more).setVisibility(View.GONE);

                        mainView.findViewById(R.id.fragment_menu_message).findViewById(R.id.menu_message_less).setVisibility(View.VISIBLE);
                        mainView.findViewById(R.id.fragment_menu_message).findViewById(R.id.menu_message_share).setVisibility(View.VISIBLE);
                        mainView.findViewById(R.id.fragment_menu_message).findViewById(R.id.menu_message_detector).setVisibility(View.VISIBLE);
                        mainView.findViewById(R.id.fragment_menu_message).findViewById(R.id.menu_message_massSend).setVisibility(View.VISIBLE);
                        mainView.findViewById(R.id.fragment_menu_message).findViewById(R.id.menu_message_saveAs).setVisibility(View.VISIBLE);
                        mainView.findViewById(R.id.fragment_menu_message).findViewById(R.id.menu_message_delete).setVisibility(View.VISIBLE);
                        mainView.findViewById(R.id.fragment_menu_message).findViewById(R.id.menu_message_remind).setVisibility(View.VISIBLE);
                    }
                });


                mainView.findViewById(R.id.menu_message_reply).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mainView.findViewById(R.id.replyMessage).callOnClick();
                    }
                });

                mainView.findViewById(R.id.menu_message_detector).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mainView.findViewById(R.id.extratorMessage).callOnClick();
                    }
                });




                mainView.findViewById(R.id.fragment_menu_message).setVisibility(View.VISIBLE);
                mainView.findViewById(R.id.closerFragmentMessage).setVisibility(View.VISIBLE);
            }
        });

        mainView.findViewById(R.id.closerFragmentMessage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainView.findViewById(R.id.fragment_menu_message).setVisibility(View.GONE);
                mainView.findViewById(R.id.closerFragmentMessage).setVisibility(View.GONE);
            }
        });

        try {
            ((TextView) mainView.findViewById(R.id.textMessage)).scrollTo(0, 0);


        new Thread(new Runnable() {
            @Override
            public void run() {

                try{
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

                }catch (ConcurrentModificationException e){
                    e.printStackTrace();
                }
            }
        }).start();

        }catch (ConcurrentModificationException e){
            e.printStackTrace();
        }

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
                if (namePart != null && namePart.length() > 0) {

                    initials += namePart.charAt(0);
                }
            }
        }
        return initials.toUpperCase();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    Toolbar mainToolBar;

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        //profileMenu = menu;
        //menuItem = menu.findItem(R.id.edit_contact);
       /* menu.findItem(R.id.action_favorite).setVisible(false);

        menu.findItem(R.id.edit_contact).setVisible(true);
        menu.findItem(R.id.save_contact).setVisible(false);

        menu.findItem(R.id.share_profile).setVisible(false);
        menu.findItem(R.id.menu_profile).setVisible(false);*/


        mainToolBar = (Toolbar) getActivity().findViewById(R.id.main_toolbar);
        TextView cancel = (TextView) getActivity().findViewById(R.id.cancel_toolbar);

        menu.findItem(R.id.action_favorite).setVisible(false);

        mainToolBar.findViewById(R.id.toolbar_menu).setVisibility(View.VISIBLE);
        mainToolBar.findViewById(R.id.toolbar_share).setVisibility(View.VISIBLE);
        mainToolBar.findViewById(R.id.toolbar_edit).setVisibility(View.GONE);
        mainToolBar.findViewById(R.id.toolbar_kanban).setVisibility(View.GONE);

        mainToolBar.setNavigationIcon(R.drawable.icn_arrow_back);
        mainToolBar.setNavigationOnClickListener(v -> {
            MainActivity.IS_PAUSE = false;
            getActivity().onBackPressed();
        });

        //getActivity().findViewById(R.id.barFlipper).setVisibility(View.GONE);

        try {
            getActivity().findViewById(R.id.fabMenuContainer).setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ((TextView) mainToolBar.findViewById(R.id.toolbar_title)).setText("Email");

        //getActivity().findViewById(R.id.fabMenuContainer).setVisibility(View.GONE);



        /*mainToolBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!company) {
                    ++number_of_clicks[0];
                    if (!thread_started[0]) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                thread_started[0] = true;
                                try {
                                    Thread.sleep(DELAY_BETWEEN_CLICKS_IN_MILLISECONDS);
                                    if (number_of_clicks[0] == 1) {
                                        //client.send(AppHelper.FORMAT_LEFT_CLICK);
                                    } else if (number_of_clicks[0] == 2) {

                                        if (!blur) {
                                            blur = true;
                                            activityApp.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(getContext(), "Secret blur mode enabled", Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                        } else {
                                            blur = false;
                                            activityApp.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(getContext(), "Secret blur mode disabled", Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                        }


                                        activityApp.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                contactProfileDataFragment.setBlur(blur);
                                                contactTimelineDataFragment.setBlur(blur);
                                            }
                                        });


                                    }
                                    number_of_clicks[0] = 0;
                                    thread_started[0] = false;
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                }
            }
        });*/


        mainToolBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    getActivity().findViewById(R.id.popup_menu_profile).setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        mainToolBar.findViewById(R.id.toolbar_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //shareContact();
            }
        });

        mainToolBar.findViewById(R.id.toolbar_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CardView card = getActivity().findViewById(R.id.popup_menu_profile);

                //card.findViewById(R.id.copy_menu).
                ((ImageView) card.findViewById(R.id.copy_menu)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
                ((ImageView) card.findViewById(R.id.pen_menu)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
                ((ImageView) card.findViewById(R.id.share_menu)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
                ((ImageView) card.findViewById(R.id.openInMobileMenu)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));

                card.findViewById(R.id.menu_profile_share).setVisibility(View.VISIBLE);
                card.findViewById(R.id.menu_profile_global_search).setVisibility(View.VISIBLE);
                card.findViewById(R.id.menu_profile_remind).setVisibility(View.VISIBLE);
                card.findViewById(R.id.menu_profile_copy).setVisibility(View.VISIBLE);
                card.findViewById(R.id.menu_profile_edit).setVisibility(View.VISIBLE);
                card.findViewById(R.id.menu_profile_delete).setVisibility(View.VISIBLE);
                card.findViewById(R.id.menu_profile_more).setVisibility(View.VISIBLE);

                card.findViewById(R.id.menu_profile_less).setVisibility(View.GONE);
                card.findViewById(R.id.menu_profile_exchange).setVisibility(View.GONE);
                card.findViewById(R.id.menu_profile_open).setVisibility(View.GONE);
                card.findViewById(R.id.menu_profile_salesforce).setVisibility(View.GONE);
                card.findViewById(R.id.menu_profile_add_more).setVisibility(View.GONE);


                card.setVisibility(View.VISIBLE);

                card.findViewById(R.id.profile_menu_share_telegram).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //shareContactToPackage("org.telegram.messenger");
                    }
                });

                card.findViewById(R.id.profile_menu_share_slack).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //shareContactToPackage("com.Slack");
                    }
                });

                card.findViewById(R.id.profile_menu_share_whatsapp).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //shareContactToPackage("com.whatsapp");
                    }
                });

                card.findViewById(R.id.menu_profile_share).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //shareContact();
                    }
                });

                card.findViewById(R.id.menu_profile_copy).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //copyContactData();
                    }
                });

                card.findViewById(R.id.menu_profile_open).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /*if (selectedContact.listOfContacts == null || selectedContact.listOfContacts.isEmpty()) {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(selectedContact.getIdContact()));
                            intent.setData(uri);
                            mainView.getContext().startActivity(intent);
                        }*/

                    }
                });

                card.findViewById(R.id.menu_profile_edit).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /*nowEdit = true;

                        card.setVisibility(View.GONE);

                        menu.findItem(R.id.menu_profile).setVisible(false);
                        menu.findItem(R.id.share_profile).setVisible(false);

                        mainToolBar.findViewById(R.id.toolbar_kanban_clicker).setVisibility(View.GONE);
                        mainToolBar.findViewById(R.id.toolbar_share).setVisibility(View.GONE);
                        mainToolBar.findViewById(R.id.toolbar_edit).setVisibility(View.GONE);
                        mainToolBar.findViewById(R.id.toolbar_menu).setVisibility(View.GONE);

                        if (selectedContact.listOfContacts == null || selectedContact.listOfContacts.isEmpty()) {
                   *//* if(companyProfileDataFragment.mainView == null) {
                        //setupSectionPager2();
                        //setEditAdapter();

                    }else*//*
                            if (contactProfileDataFragment.mainView == null) {

                                ArrayList<Contact> c = new ArrayList<>();
                                c.add(selectedContact);
                                contactProfileDataFragment.setEditAdapter2(c, activityApp);
                            } else
                                setEditAdapter();

                        }
                        mainToolBar.setNavigationIcon(null);
                        cancel.setVisibility(View.VISIBLE);
                        startEditMode();
                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (editModeChecker) {
                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                            getActivity());
                                    alertDialogBuilder.setTitle("Do you want to discard changes?");
                                    alertDialogBuilder
                                            .setCancelable(false)
                                            .setPositiveButton("Yes", (dialog, id) -> {
                                                FragmentManager fm = getActivity().getFragmentManager();
                                                cancel.setVisibility(View.GONE);
                                                mainToolBar.setNavigationIcon(R.drawable.icn_arrow_back);
                                                cancleEditMode();
                                                menu.findItem(R.id.edit_contact).setVisible(false); //
                                                menu.findItem(R.id.save_contact).setVisible(false);
                                                if (selectedContact.listOfContacts == null || selectedContact.listOfContacts.isEmpty()) {
                                                    setAdapter();
                                                    *//*contactProfileDataFragment.contactNumberAdapter.updateContactsList(selectedContact.getListOfContactInfo());*//*
                                                }
                                                editModeChecker = false;
                                            })
                                            .setNegativeButton("No", (dialog, id) -> {
                                                dialog.cancel();
                                            });

                                    AlertDialog alertDialog = alertDialogBuilder.create();
                                    alertDialog.show();
                                } else {
                                    FragmentManager fm = getActivity().getFragmentManager();
                                    cancel.setVisibility(View.GONE);
                                    mainToolBar.setNavigationIcon(R.drawable.icn_arrow_back);
                                    cancleEditMode();
                                    menu.findItem(R.id.edit_contact).setVisible(false); //
                                    menu.findItem(R.id.save_contact).setVisible(false);
                                    if (selectedContact.listOfContacts == null || selectedContact.listOfContacts.isEmpty()) {
                                        try {
                                            setAdapter();
                                        } catch (NullPointerException e) {
                                            setupSectionPager();
                                        }
                                    }
                                }

                                mainToolBar.findViewById(R.id.toolbar_kanban_clicker).setVisibility(View.GONE);
                                mainToolBar.findViewById(R.id.toolbar_share).setVisibility(View.VISIBLE);
                                mainToolBar.findViewById(R.id.toolbar_edit).setVisibility(View.VISIBLE);
                                mainToolBar.findViewById(R.id.toolbar_menu).setVisibility(View.VISIBLE);

                                menu.findItem(R.id.menu_profile).setVisible(false); //
                                menu.findItem(R.id.share_profile).setVisible(false); //
                            }

                        });
                        editModeChecker = false;
                        menu.findItem(R.id.edit_contact).setVisible(false);
                        menu.findItem(R.id.save_contact).setVisible(true);
                        checkEdit = true;
                        //return false;*/
                    }
                });


                card.findViewById(R.id.menu_profile_more).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        card.findViewById(R.id.menu_profile_share).setVisibility(View.GONE);
                        card.findViewById(R.id.menu_profile_global_search).setVisibility(View.GONE);
                        card.findViewById(R.id.menu_profile_remind).setVisibility(View.GONE);
                        card.findViewById(R.id.menu_profile_copy).setVisibility(View.GONE);
                        card.findViewById(R.id.menu_profile_edit).setVisibility(View.GONE);
                        card.findViewById(R.id.menu_profile_delete).setVisibility(View.GONE);
                        card.findViewById(R.id.menu_profile_more).setVisibility(View.GONE);

                        card.findViewById(R.id.menu_profile_less).setVisibility(View.VISIBLE);
                        card.findViewById(R.id.menu_profile_exchange).setVisibility(View.VISIBLE);
                        card.findViewById(R.id.menu_profile_open).setVisibility(View.VISIBLE);
                        card.findViewById(R.id.menu_profile_salesforce).setVisibility(View.VISIBLE);
                        card.findViewById(R.id.menu_profile_add_more).setVisibility(View.VISIBLE);

                        card.findViewById(R.id.menu_profile_less).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                card.findViewById(R.id.menu_profile_share).setVisibility(View.VISIBLE);
                                card.findViewById(R.id.menu_profile_global_search).setVisibility(View.VISIBLE);
                                card.findViewById(R.id.menu_profile_remind).setVisibility(View.VISIBLE);
                                card.findViewById(R.id.menu_profile_copy).setVisibility(View.VISIBLE);
                                card.findViewById(R.id.menu_profile_edit).setVisibility(View.VISIBLE);
                                card.findViewById(R.id.menu_profile_delete).setVisibility(View.VISIBLE);
                                card.findViewById(R.id.menu_profile_more).setVisibility(View.VISIBLE);

                                card.findViewById(R.id.menu_profile_less).setVisibility(View.GONE);
                                card.findViewById(R.id.menu_profile_exchange).setVisibility(View.GONE);
                                card.findViewById(R.id.menu_profile_open).setVisibility(View.GONE);
                                card.findViewById(R.id.menu_profile_salesforce).setVisibility(View.GONE);
                                card.findViewById(R.id.menu_profile_add_more).setVisibility(View.GONE);
                            }
                        });
                    }
                });

            }
        });


        mainToolBar.findViewById(R.id.toolbar_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        menu.findItem(R.id.save_contact).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        getActivity());
                alertDialogBuilder.setTitle("Do you want to save changes?");
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Yes", (dialog, id) -> {


                        })
                        .setNegativeButton("No", (dialog, id) -> dialog.cancel());
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                return false;
            }
        });
    }

    public void initSender_recipient() {
        //((ImageView) mainView.findViewById(R.id.google_recipient)).setVisibility(View.GONE);

        ((ImageView) mainView.findViewById(R.id.google_sender)).setVisibility(View.GONE);

        mainView.findViewById(R.id.replyMessPopup).setVisibility(View.GONE);

        if (message.getTitle().length() > 4) {

            if (message.getTitle().substring(0, 3).contains("Re:") || message.getTitle().substring(0, 3).contains("Re[")) {
                ((ImageView) mainView.findViewById(R.id.replyMessPopup)).setImageDrawable(mainView.getContext().getResources().getDrawable(R.drawable.reply_message));
                mainView.findViewById(R.id.replyMessPopup).setVisibility(View.VISIBLE);
            } else if (message.getTitle().substring(0, 4).contains("Fwd:") || message.getTitle().substring(0, 4).contains("Fwd[")) {
                ((ImageView) mainView.findViewById(R.id.replyMessPopup)).setImageDrawable(mainView.getContext().getResources().getDrawable(R.drawable.foward_message));
                mainView.findViewById(R.id.replyMessPopup).setVisibility(View.VISIBLE);
            }
        }

        boolean checkSelect = false;

        if (emailConatct.contains(message.getAccount().getEmail())/*message.getAccount().getEmail().equals(email_contact)*/) {


            checkSelect = true;


            int nameHash = message.getAccount().getName().hashCode();
            int color = Color.rgb(Math.abs(nameHash * 28439) % 255, Math.abs(nameHash * 211239) % 255, Math.abs(nameHash * 42368) % 255);
            GradientDrawable circle = (GradientDrawable) ContextCompat.getDrawable(getContext(), R.drawable.blue_circle).mutate();
            circle.setColor(color);
            ((CircleImageView) mainView.findViewById(R.id.avatar_sender)).setBackground(circle);
            String initials = getInitials(nameContact);
            ((TextView) mainView.findViewById(R.id.initial_sender)).setText(initials);
            ((TextView) mainView.findViewById(R.id.header_message)).setText(nameContact + " < " + message.getAccount().getEmail() + " >");


            SharedPreferences mSettings;
            mSettings = getActivity().getSharedPreferences("accountUser", Context.MODE_PRIVATE);
            Set<String> set = mSettings.getStringSet("accounts", null);


            //list.add(account);
            if (set != null && set.contains(message.getAccount().getEmail()) || set != null && set.contains(nameContact)) {
                if (set.contains(message.getAccount().getEmail()))
                    replyFrom = message.getAccount().getEmail();
                else replyFrom = nameContact;
            } else {
                sendTo = message.getAccount().getEmail();
                sendToName = nameContact;
            }





           /* int nameHash2 = message.getEmail_2().hashCode();
            int color2 = Color.rgb(Math.abs(nameHash2 * 28439) % 255, Math.abs(nameHash2 * 211239) % 255, Math.abs(nameHash2 * 42368) % 255);
            GradientDrawable circle2 = (GradientDrawable) ContextCompat.getDrawable(getContext(), R.drawable.blue_circle).mutate();
            circle2.setColor(color2);
            //holder.avatar.setImageResource(android.R.color.transparent);
            ((CircleImageView) mainView.findViewById(R.id.avatar_recipient)).setBackground(circle2);
            String initials2 = getInitials(message.getEmail_2());
            ((TextView) mainView.findViewById(R.id.initials_recipient)).setText(initials2);
            ((ImageView) mainView.findViewById(R.id.google_recipient)).setVisibility(View.VISIBLE);*/
        } else {

            int nameHash = message.getAccount().getName().hashCode();
            int color = Color.rgb(Math.abs(nameHash * 28439) % 255, Math.abs(nameHash * 211239) % 255, Math.abs(nameHash * 42368) % 255);
            GradientDrawable circle = (GradientDrawable) ContextCompat.getDrawable(getContext(), R.drawable.blue_circle).mutate();
            circle.setColor(color);
            ((CircleImageView) mainView.findViewById(R.id.avatar_sender)).setBackground(circle);
            String initials = getInitials(message.getEmail_2());
            ((TextView) mainView.findViewById(R.id.initial_sender)).setText(initials);
            ((TextView) mainView.findViewById(R.id.header_message)).setText("< " + message.getAccount().getName() + " >");

            /*int hash = nameContact.hashCode();
            int color2 = Color.rgb(Math.abs(hash * 28439) % 255, Math.abs(hash * 211239) % 255, Math.abs(hash * 42368) % 255);
            GradientDrawable circle2 = (GradientDrawable) ContextCompat.getDrawable(getContext(), R.drawable.blue_circle).mutate();
            circle2.setColor(color2);
            ((CircleImageView) mainView.findViewById(R.id.avatar_recipient)).setBackground(circle2);
            String initials2 = getInitials(nameContact);
            ((TextView) mainView.findViewById(R.id.initials_recipient)).setText(initials2);*/


            ((ImageView) mainView.findViewById(R.id.google_sender)).setVisibility(View.VISIBLE);
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

        boolean checkU = false;

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

        if(listOfContactsMessage.size() == 0){
            mainView.findViewById(R.id.moreCont).setVisibility(View.GONE);
            mainView.findViewById(R.id.secondC).setVisibility(View.GONE);
            mainView.findViewById(R.id.firstC).setVisibility(View.GONE);
        }

        if (listOfContactsMessage.size() > 2) {
            ((TextView) mainView.findViewById(R.id.contactC)).setText(String.valueOf(listOfContactsMessage.size() - 2));
            mainView.findViewById(R.id.moreCont).setVisibility(View.VISIBLE);
        }

        if (listOfContactsMessage.size() == 1) {
            mainView.findViewById(R.id.moreCont).setVisibility(View.GONE);
            mainView.findViewById(R.id.secondC).setVisibility(View.GONE);
            mainView.findViewById(R.id.firstC).setVisibility(View.VISIBLE);
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
                String initials = getInitials(listOfContactsMessage.get(0).getName());
                ((TextView) mainView.findViewById(R.id.initials_recipient_1)).setText(initials);
            } else if (listOfContactsMessage.get(0).getEmail() != null) {
                String initials = getInitials(listOfContactsMessage.get(0).getEmail());
                ((TextView) mainView.findViewById(R.id.initials_recipient_1)).setText(initials);
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
            mainView.findViewById(R.id.firstC).setVisibility(View.VISIBLE);

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
                String initials = getInitials(listOfContactsMessage.get(0).getName());
                ((TextView) mainView.findViewById(R.id.initials_recipient_1)).setText(initials);
            } else if (listOfContactsMessage.get(0).getEmail() != null) {
                String initials = getInitials(listOfContactsMessage.get(0).getEmail());
                ((TextView) mainView.findViewById(R.id.initials_recipient_1)).setText(initials);
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
                String initials = getInitials(listOfContactsMessage.get(1).getName());
                ((TextView) mainView.findViewById(R.id.initials_recipient_2)).setText(initials);
            } else if (listOfContactsMessage.get(1).getEmail() != null) {
                String initials = getInitials(listOfContactsMessage.get(1).getEmail());
                ((TextView) mainView.findViewById(R.id.initials_recipient_2)).setText(initials);
            }

            if (listOfContactsMessage.get(1).isGoogle()) {
                ((ImageView) mainView.findViewById(R.id.google_recipient_2)).setVisibility(View.VISIBLE);
            } else {
                ((ImageView) mainView.findViewById(R.id.google_recipient_2)).setVisibility(View.GONE);
            }

        }


        //================================

    }
    private static final String APPLICATION_NAME = "Extime";
    public void setStarMessage(EmailMessage message, boolean star) {
        if (message.getListOfTypeMessage() != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {

                    try {
                        HttpTransport transport = AndroidHttp.newCompatibleTransport();
                        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
                        Gmail service = null;

                                service = new Gmail.Builder(transport, jsonFactory, message.getCredential())
                                        .setApplicationName(APPLICATION_NAME)
                                        .build();





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

                        }

                        EventBus.getDefault().post(new UpdateMessageAdapter());


                    } catch (
                            Exception e) {
                        e.printStackTrace();
                    }

                }
            }).start();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mainToolBar != null)
            mainToolBar.findViewById(R.id.toolbar_kanban).setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mainToolBar != null)
            mainToolBar.findViewById(R.id.toolbar_kanban).setVisibility(View.VISIBLE);
    }

    public void initTimeMessage() {
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

            ((TextView) mainView.findViewById(R.id.timeMessage)).setText(timeStr);
        } catch (Exception e) {
            System.out.println("ERROR TO GET TIME Contact Adapter");
        }
    }

    public void initMessage_title() {

        ((TextView) mainView.findViewById(R.id.titleMessage)).setText(message.getTitle());

        if (message.getMessage() != null) {
            if(message.getMessage().contains("<br>"))
                ((TextView) mainView.findViewById(R.id.textMessage)).setText(Html.fromHtml(message.getMessage().trim()));
                else
            ((TextView) mainView.findViewById(R.id.textMessage)).setText(message.getMessage().trim());
        } else
            ((TextView) mainView.findViewById(R.id.textMessage)).setText("");

        if (message.getMessageData() != null) {
            final SpannableStringBuilder text = new SpannableStringBuilder(message.getMessage());
            for (int i = 0; i < message.getMessageData().size(); i++) {
                if (message.getMessageData().get(i).getClipboard() != null) {
                    int startI = message.getMessage().toLowerCase().indexOf(message.getMessageData().get(i).getClipboard().getValueCopy().toLowerCase());

                    final ForegroundColorSpan style = new ForegroundColorSpan(Color.parseColor("#567bac"));

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

                }
            }

            if(message.getMessage().contains("<br>"))
                ((TextView) mainView.findViewById(R.id.textMessage)).setText(Html.fromHtml(message.getMessage().trim()));
            else
                ((TextView) mainView.findViewById(R.id.textMessage)).setText(message.getMessage().trim());
            //((TextView) mainView.findViewById(R.id.textMessage)).setText(text);



            //Linkify.addLinks(((TextView) mainView.findViewById(R.id.textMessage)), Linkify.WEB_URLS);
        }
    }

    public void showExtratorData(EmailMessage message) {

        /*FrameLayout layoutParams = mainView.findViewById(R.id.frameTextMess);

        startSearchContact = false;

        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) layoutParams.getLayoutParams();

        float px = (float) (80 * mainView.getContext().getResources().getDisplayMetrics().density);

        //122.5

        lp.weight = 0;

        lp.height = (int) px;

        layoutParams.setLayoutParams(lp);*/

        //===========

        mainView.findViewById(R.id.linearCountDataMEssage).setVisibility(View.GONE);
        mainView.findViewById(R.id.linear_arg).setVisibility(View.GONE);
        mainView.findViewById(R.id.linear_button_message).setVisibility(View.GONE);

        //mainView.findViewById(R.id.line_button_3).setVisibility(View.GONE);
        //popupMessage.findViewById(R.id.line_button).setVisibility(View.GONE);


        //===========


        if (ProfileFragment.isKeyboard) {


            /*FrameLayout.LayoutParams lp1 = (FrameLayout.LayoutParams) popupMessage.findViewById(R.id.popup_mess).getLayoutParams();
            FrameLayout.LayoutParams layoutParamss = (FrameLayout.LayoutParams) popupMessage.getLayoutParams();

            float px_b = 295 * getContext().getResources().getDisplayMetrics().density;




            layoutParamss.height = (int) px_b;

            layoutParamss.gravity = Gravity.BOTTOM;

            lp1.height = ViewGroup.LayoutParams.MATCH_PARENT;*/

            //============================

            LinearLayout.LayoutParams lp1 = (LinearLayout.LayoutParams) mainView.findViewById(R.id.recyclerDataMessage).getLayoutParams();

            float px = 60 * getContext().getResources().getDisplayMetrics().density;

            lp1.height = (int) px;

            mainView.findViewById(R.id.recyclerDataMessage).setLayoutParams(lp1);

           /* FrameLayout.LayoutParams lp2 = (FrameLayout.LayoutParams) mainView.getLayoutParams();

            lp2.height = ViewGroup.LayoutParams.MATCH_PARENT;

            mainView.setLayoutParams(lp2);*/


            //===========================

            //popupMessage.setLayoutParams(layoutParamss);

            //LinearLayout.LayoutParams lp2 = (LinearLayout.LayoutParams) popupMessage.findViewById(R.id.bottomFrameClipboard).getLayoutParams();

            //float px_b2 = 45 * getContext().getResources().getDisplayMetrics().density;

            //lp2.height = (int) px_b2;

            //popupMessage.requestLayout();

            //popupMessage.findViewById(R.id.recyclerDataMessage).requestLayout();

            //popupMessage.findViewById(R.id.bottomFrameClipboard).requestLayout();


        } else {
            LinearLayout.LayoutParams lp1 = (LinearLayout.LayoutParams) mainView.findViewById(R.id.recyclerDataMessage).getLayoutParams();

            float px = 125 * getContext().getResources().getDisplayMetrics().density;

            lp1.height = (int) px;

            mainView.findViewById(R.id.recyclerDataMessage).setLayoutParams(lp1);

            /*FrameLayout.LayoutParams lp2 = (FrameLayout.LayoutParams) mainView.getLayoutParams();

            lp2.height = ViewGroup.LayoutParams.MATCH_PARENT;

            mainView.setLayoutParams(lp2);*/
        }


        mainView.findViewById(R.id.test1).setVisibility(View.VISIBLE);


        RecyclerView recyclerView = mainView.findViewById(R.id.recyclerDataMessage);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //ArrayList<MessageData> list = new ArrayList<>();

                /*for(MessageData messageData : message.getMessageData()){
                    if(messageData.getClipboard() != null)
                        list.add(messageData);
                }*/

        mainView.findViewById(R.id.downloadMessageData).setVisibility(View.GONE);
        mainView.findViewById(R.id.copyMessageData).setVisibility(View.GONE);
        mainView.findViewById(R.id.shareMessageData).setVisibility(View.GONE);
        mainView.findViewById(R.id.textCountMessageData).setVisibility(View.VISIBLE);
        mainView.findViewById(R.id.hideMoreMessageData).setVisibility(View.VISIBLE);


        MessageDataExtratorAdapter messageDataExtratorAdapter = new MessageDataExtratorAdapter(message.getMessageData(), getContext(), getActivity(), mainView.findViewById(R.id.createContactMessageData), mainView.findViewById(R.id.textCountMessageData), mainView.findViewById(R.id.shareMessageData), mainView.findViewById(R.id.hideMoreMessageData), mainView.findViewById(R.id.copyMessageData), mainView.findViewById(R.id.downloadMessageData));
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

        ((TextView) mainView.findViewById(R.id.textCountMessageData)).setText(text);


        mainView.findViewById(R.id.hideMoreMessageData).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideMessageData();
            }
        });


        mainView.findViewById(R.id.bottomFrameMessage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideMessageData();

            }
        });

        mainView.findViewById(R.id.createContactMessageDataLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (messageDataExtratorAdapter.getSelectMessageData().size() == 0) return;


                Contact contact = new Contact();
                ArrayList<MessageData> listEx = messageDataExtratorAdapter.getSelectMessageData();


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


        mainView.findViewById(R.id.shareMessageData).setOnClickListener(new View.OnClickListener() {
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

        mainView.findViewById(R.id.copyMessageData).setOnClickListener(new View.OnClickListener() {
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

        mainView.findViewById(R.id.downloadMessageData).setOnClickListener(new View.OnClickListener() {
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

        mainView.findViewById(R.id.test1).setVisibility(View.GONE);

        mainView.findViewById(R.id.linearCountDataMEssage).setVisibility(View.VISIBLE);

        mainView.findViewById(R.id.linear_arg).setVisibility(View.VISIBLE);

        mainView.findViewById(R.id.linear_button_message).setVisibility(View.GONE); //new


/*
        FrameLayout layoutParams = mainView.findViewById(R.id.frameTextMess);

        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) layoutParams.getLayoutParams();

        //

        //122.5

        lp.weight = 1;

        lp.height = 0;

        layoutParams.setLayoutParams(lp);

        //===========



        //===========

        FrameLayout.LayoutParams lp1 = (FrameLayout.LayoutParams) mainView.findViewById(R.id.popup_mess).getLayoutParams();

        float px = (float) (235 * mainView.getContext().getResources().getDisplayMetrics().density);

        lp1.height = (int) px;

        mainView.findViewById(R.id.popup_mess).setLayoutParams(lp1);

        FrameLayout.LayoutParams lp2 = (FrameLayout.LayoutParams) mainView.getLayoutParams();

        lp2.height = (int) px;

        mainView.setLayoutParams(lp2);

        mainView.findViewById(R.id.test1).setVisibility(View.GONE);*/

    }


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

        if(type.equals("timeline")) {

            if (TimeLineFragment.email_contact.contains(message.getAccount().getEmail())) {

                sender.setName(message.getAccount().getName());
                sender.setEmail(message.getAccount().getEmail());

                checkSelect = true;
            } else {

                sender.setName(message.getEmail_2());
                sender.setEmail(message.getAccount().getName().replaceAll("\\<\\>", ""));
                sender.setGoogle(true);

            }
        }else if(type.equals("contact")){
            if (ContactTimelineDataFragment.email_contact.contains(message.getAccount().getEmail())) {

                sender.setName(message.getAccount().getName());
                sender.setEmail(message.getAccount().getEmail());

                checkSelect = true;
            } else {

                sender.setName(message.getEmail_2());
                sender.setEmail(message.getAccount().getName().replaceAll("\\<\\>", ""));
                sender.setGoogle(true);

            }
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();


        mainView.getViewTreeObserver().removeOnGlobalLayoutListener(checkKeyboardListener);
        try {
            getActivity().findViewById(R.id.popup_menu_profile).setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
