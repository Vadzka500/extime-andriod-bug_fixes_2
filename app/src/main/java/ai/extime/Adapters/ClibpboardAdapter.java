package ai.extime.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import ai.extime.Activity.MainActivity;
import ai.extime.Enums.ExtractEnums;
import ai.extime.Enums.MagicType;
import ai.extime.Enums.SocialEnums;
import ai.extime.Events.HideHistoryPopup;
import ai.extime.Events.SaveHistory;
import ai.extime.Fragments.ContactProfileDataFragment;
import ai.extime.Fragments.CreateFragment;
import ai.extime.Interfaces.CustomRunnable;
import ai.extime.Interfaces.IMessage;
import ai.extime.Models.AdapterScrollModel;
import ai.extime.Models.ContactDataClipboard;
import ai.extime.Models.EmailMessage;
import ai.extime.Models.Extrator;
import ai.extime.Models.Template;
import ai.extime.Services.ContactCacheService;
import ai.extime.Utils.ClipboardType;
import ai.extime.Utils.ShareTemplatesMessageReply;
import de.hdodenhof.circleimageview.CircleImageView;
import ai.extime.Enums.ClipboardEnum;
import ai.extime.Events.PopupCompanyInProfile;
import ai.extime.Events.PopupInProfile;
import ai.extime.Events.SaveClipboard;
import ai.extime.Fragments.ContactsFragment;
import ai.extime.Fragments.ProfileFragment;
import ai.extime.Models.Clipboard;
import ai.extime.Models.Contact;


import com.extime.R;

/**
 * Created by patal on 10.08.2017.
 */

public class ClibpboardAdapter extends RecyclerView.Adapter<ClibpboardAdapter.ClipboardViewHolder> implements IMessage {

    private View mainView;

    private ArrayList<Clipboard> clipboards;

    private List<Integer> checkHash = new ArrayList<>();
    private ArrayList<Clipboard> checkHashClip = new ArrayList<>();

    private Activity activity;

    private LinearLayout popupInfo;

    private FrameLayout popupChangeType;

    LinearLayout popupEditClip;

    ArrayList<Clipboard> listOpen = new ArrayList<>();

    public ContactsFragment contactsFragment;

    public ArrayList<Contact> listContact;

    public ArrayList<Boolean> checkContact;

    public static boolean checkSelectClips = false;

    public static boolean checkUpdateClips = false;

    public ViewGroup vg;

    private ParsingURLClipboard parsingURLClipboard;



    @Override
    public void clickMessage(EmailMessage message) {

    }

    @Override
    public void openFragmentMessage(EmailMessage message) {

    }

    @Override
    public void openContactList(EmailMessage message) {

    }

    @Override
    public void ShowTemplatePopup(Template template) {

    }

    @Override
    public void setStarMessage(EmailMessage message, boolean star) {

    }

    @Override
    public boolean isOpenPreview() {
       return false;
    }


    static class ClipboardViewHolder extends RecyclerView.ViewHolder {
        ImageView imageTypeClipboard, imageTypeClipboardGroup;
        TextView clipboardValue, numberGroup;
        ImageView circleImage;
        TextView timeClipboard;
        CheckBox checkBox;
        RelativeLayout frameLayout;
        LinearLayout linearLayout;
        CircleImageView circleImageView, circleImageViewSearch, arrow;

        RelativeLayout mainFrame;
        FrameLayout frameImageNum;
        TextView initials, numberList;
        ImageView imageCompany;
        FrameLayout frameInageIconClipboard, frameCircle;

        FrameLayout frameContactList1;
        FrameLayout frameContactList2;
        FrameLayout frameContactList_Group;

        TextView countGroup;

        FrameLayout frameCircleSearch;
        FrameLayout frameSearchNote;
        FrameLayout frameSearchSocials;

        TextView groupNumberClipboard_extracted;

        @SuppressLint("WrongViewCast")
        ClipboardViewHolder(View itemView) {
            super(itemView);
            imageTypeClipboard = (ImageView) itemView.findViewById(R.id.imageTypeClipboard);
            //circleImage = (ImageView) itemView.findViewById(R.id.circleImage);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkClip);
            clipboardValue = (TextView) itemView.findViewById(R.id.clipboardValue);
            frameLayout = (RelativeLayout) itemView.findViewById(R.id.checkFrameClip);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.lineInfoClip);
            circleImageView = (CircleImageView) itemView.findViewById(R.id.profileIconAvatar);
            circleImageViewSearch = (CircleImageView) itemView.findViewById(R.id.profileIconSearch);
            initials = (TextView) itemView.findViewById(R.id.profileIconInitials);
            imageCompany = (ImageView) itemView.findViewById(R.id.companyPhotoIcon);
            frameInageIconClipboard = (FrameLayout) itemView.findViewById(R.id.frameInageIconClipboard);
            //timeClipboard = (TextView) itemView.findViewById(R.id.timeClipboard);

            mainFrame = (RelativeLayout) itemView.findViewById(R.id.mainFrame);
            numberList = (TextView) itemView.findViewById(R.id.groupCountClipboard);
            imageTypeClipboardGroup = (ImageView) itemView.findViewById(R.id.imageTypeClipboardGroup);
            arrow = (CircleImageView) itemView.findViewById(R.id.profileIconArrow);
            numberGroup = (TextView) itemView.findViewById(R.id.groupNumberClipboard);
            frameImageNum = (FrameLayout) itemView.findViewById(R.id.frameImageNum);

            frameCircle = (FrameLayout) itemView.findViewById(R.id.frameCircle);


            frameContactList1 = itemView.findViewById(R.id.firstFindContact);
            frameContactList2 = itemView.findViewById(R.id.secondFindContact);
            frameContactList_Group = itemView.findViewById(R.id.frameCircleList);

            countGroup = itemView.findViewById(R.id.contactNumbers);

            frameCircleSearch = itemView.findViewById(R.id.frameCircleSearch);
            frameSearchNote = itemView.findViewById(R.id.search_note);
            frameSearchSocials = itemView.findViewById(R.id.frameSocialsSearch);

            groupNumberClipboard_extracted = itemView.findViewById(R.id.groupNumberClipboard_extracted);

        }
    }

    public RecyclerView r;

    private FrameLayout globalSpopup;

    private MagicType typeOfData;

    public ClibpboardAdapter(ArrayList<Clipboard> clipboards, Activity activity, LinearLayout linearLayout, LinearLayout linearLayout2, ContactsFragment contactsFragment, FrameLayout change, RecyclerView r, FrameLayout global, MagicType typeOfData) {
        this.listContact = new ArrayList<>();
        this.checkContact = new ArrayList<>();
        for (int i = 0; i < clipboards.size(); i++) {
            listContact.add(null);
            checkContact.add(false);
        }
        this.clipboards = new ArrayList<>();
        this.clipboards.addAll(clipboards);

        this.globalSpopup = global;
        //this.clipboards = clipboards;

        this.r = r;

        checkHash = new ArrayList<>();
        checkHashClip = new ArrayList<>();
        this.activity = activity;

        this.popupInfo = linearLayout;

        this.popupChangeType = change;

        this.popupEditClip = linearLayout2;
        checkHash = new ArrayList<>();
        checkHashClip = new ArrayList<>();
        this.contactsFragment = contactsFragment;

        this.typeOfData = typeOfData;

        //if(!EventBus.getDefault().isRegistered(this)) EventBus.getDefault().register(this);

    }

    public void openPreview(String value){

        /*for(int i = 0;i<clipboards.size();i++){
            if(clipboards.get(i).getValueCopy().equalsIgnoreCase(value)){


                openPrev(clipboards.get(i), i, null);
                break;
            }
        }*/

    }


    public void updateIconPreview(Clipboard clipboard){
        if (clipboard.getImageTypeClipboard() != null) {
            ((ImageView) popupInfo.findViewById(R.id.iconData)).setImageURI(Uri.parse(clipboard.getImageTypeClipboard()));
            ((TextView) popupInfo.findViewById(R.id.textParseURL)).setText(clipboard.getType().toString().substring(0, 1).toUpperCase() + clipboard.getType().toString().substring(1).toLowerCase());
            ((TextView) popupInfo.findViewById(R.id.textParseURL)).requestLayout();
            ((ImageView) popupEditClip.findViewById(R.id.iconDataEdit)).setImageURI(Uri.parse(clipboard.getImageTypeClipboard()));
            try {
                ((TextView) popupEditClip.findViewById(R.id.textParseURLEdit)).setText(clipboard.getType().toString().substring(0, 1).toUpperCase() + clipboard.getType().toString().substring(1).toLowerCase());
                ((TextView) popupEditClip.findViewById(R.id.textParseURLEdit)).requestLayout();
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        } else {
            ((ImageView) popupInfo.findViewById(R.id.iconData)).setImageDrawable(activity.getResources().getDrawable(R.drawable.icn_notes));
            ((TextView) popupInfo.findViewById(R.id.textParseURL)).setText("Notes");
            ((TextView) popupInfo.findViewById(R.id.textParseURL)).requestLayout();

            ((ImageView) popupEditClip.findViewById(R.id.iconDataEdit)).setImageDrawable(activity.getResources().getDrawable(R.drawable.icn_notes));
            ((TextView) popupEditClip.findViewById(R.id.textParseURLEdit)).setText("Notes");
            ((TextView) popupEditClip.findViewById(R.id.textParseURLEdit)).requestLayout();
        }
    }

    public void openPrev(Clipboard clipboard, int position, ClipboardViewHolder holder){
        if (popupEditClip.getVisibility() == View.VISIBLE) {
            if (parsingURLClipboard != null) parsingURLClipboard.cancel(false);
            return;
        }

        if(popupInfo.getMeasuredHeight() > activity.findViewById(R.id.popupContainer).getMeasuredHeight()){
            ViewGroup.LayoutParams lp = popupInfo.getLayoutParams();
            lp.height = (int) (155 * activity.getResources().getDisplayMetrics().density);
            popupInfo.setLayoutParams(lp);
        }

        if (popupChangeType != null && popupChangeType.getVisibility() == View.VISIBLE) {
            popupChangeType.setVisibility(View.GONE);

            int co = 0;
            for (Clipboard cl : checkHashClip) {
                if (cl.getListClipboards() == null || cl.getListClipboards().isEmpty()) {
                    co++;
                }
            }
            if (typeOfData.equals(MagicType.CLIPBOARD)) {
                if (co > 0) {
                    ((TextView) activity.findViewById(R.id.textTopClipboard)).setText(co + " fields selected");
                    ((TextView) activity.findViewById(R.id.textTopClipboard)).setTextSize(9);
                    ((TextView) activity.findViewById(R.id.textTopClipboard)).requestLayout();
                } else
                    activity.findViewById(R.id.textTopClipboard).setVisibility(View.INVISIBLE);
            } else {
                if (co > 0) {
                    ((TextView) activity.findViewById(R.id.textTopHistory)).setText(co + " fields selected");
                    ((TextView) activity.findViewById(R.id.textTopHistory)).setTextSize(9);
                    ((TextView) activity.findViewById(R.id.textTopHistory)).requestLayout();
                } else
                    activity.findViewById(R.id.textTopHistory).setVisibility(View.INVISIBLE);
            }

            return;
        }

        if (globalSpopup.getVisibility() == View.VISIBLE) {
            globalSpopup.setVisibility(View.GONE);
            return;
        }

        if (activity.findViewById(R.id.frameExtractClipboard).getVisibility() == View.VISIBLE) {
            activity.findViewById(R.id.frameExtractClipboard).setVisibility(View.GONE);

            return;
        }
        if (popupInfo != null && popupInfo.getVisibility() == View.VISIBLE) {
            popupInfo.setVisibility(View.GONE);

            int co = 0;
            for (Clipboard cl : checkHashClip) {
                if (cl.getListClipboards() == null || cl.getListClipboards().isEmpty()) {
                    co++;
                }
            }
            if (typeOfData.equals(MagicType.CLIPBOARD)) {
                if (co > 0) {
                    ((TextView) activity.findViewById(R.id.textTopClipboard)).setText(co + " fields selected");
                    ((TextView) activity.findViewById(R.id.textTopClipboard)).setTextSize(9);
                    ((TextView) activity.findViewById(R.id.textTopClipboard)).requestLayout();
                } else
                    activity.findViewById(R.id.textTopClipboard).setVisibility(View.INVISIBLE);
            } else {
                if (co > 0) {
                    ((TextView) activity.findViewById(R.id.textTopHistory)).setText(co + " fields selected");
                    ((TextView) activity.findViewById(R.id.textTopHistory)).setTextSize(9);
                    ((TextView) activity.findViewById(R.id.textTopHistory)).requestLayout();
                } else
                    activity.findViewById(R.id.textTopHistory).setVisibility(View.INVISIBLE);
            }

            if (parsingURLClipboard != null) parsingURLClipboard.cancel(false);
            return;
        }


        popupInfo.setVisibility(View.VISIBLE);

        popupInfo.findViewById(R.id.frame_send_messageInfo_clipboard_parse).setVisibility(View.GONE);


        //int px = (int) (9 * activity.getResources().getDisplayMetrics().density);
        if (typeOfData.equals(MagicType.CLIPBOARD)) {
            ((TextView) activity.findViewById(R.id.textTopClipboard)).setText(clipboard.getTime());
            ((TextView) activity.findViewById(R.id.textTopClipboard)).setTextSize(9);
            ((TextView) activity.findViewById(R.id.textTopClipboard)).requestLayout();
            activity.findViewById(R.id.textTopClipboard).setVisibility(View.VISIBLE);
        } else {
            ((TextView) activity.findViewById(R.id.textTopHistory)).setText(clipboard.getTime());
            ((TextView) activity.findViewById(R.id.textTopHistory)).setTextSize(9);
            ((TextView) activity.findViewById(R.id.textTopHistory)).requestLayout();
            activity.findViewById(R.id.textTopHistory).setVisibility(View.VISIBLE);
        }


        popupInfo.findViewById(R.id.iconData).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openChangeType(clipboard);
            }
        });



        if (clipboard.getImageTypeClipboard() != null) {
            ((ImageView) popupInfo.findViewById(R.id.iconData)).setImageURI(Uri.parse(clipboard.getImageTypeClipboard()));
            ((TextView) popupInfo.findViewById(R.id.textParseURL)).setText(clipboard.getType().toString().substring(0, 1).toUpperCase() + clipboard.getType().toString().substring(1).toLowerCase());

            ((ImageView) popupEditClip.findViewById(R.id.iconDataEdit)).setImageURI(Uri.parse(clipboard.getImageTypeClipboard()));
            try {
                ((TextView) popupEditClip.findViewById(R.id.textParseURLEdit)).setText(clipboard.getType().toString().substring(0, 1).toUpperCase() + clipboard.getType().toString().substring(1).toLowerCase());
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        } else {
            ((ImageView) popupInfo.findViewById(R.id.iconData)).setImageDrawable(activity.getResources().getDrawable(R.drawable.icn_notes));
            ((TextView) popupInfo.findViewById(R.id.textParseURL)).setText("Notes");

            ((ImageView) popupEditClip.findViewById(R.id.iconDataEdit)).setImageDrawable(activity.getResources().getDrawable(R.drawable.icn_notes));
            ((TextView) popupEditClip.findViewById(R.id.textParseURLEdit)).setText("Notes");
        }


        ((TextView) popupEditClip.findViewById(R.id.textParseURLEdit)).requestLayout();

        //ScrollingMovementMethod sc = new ScrollingMovementMethod();
        //sc.
        ((TextView) popupInfo.findViewById(R.id.textParseURL)).setTextColor(activity.getResources().getColor(R.color.black));
        //((TextView) popupInfo.findViewById(R.id.valueUpdClip)).setMovementMethod(new ScrollingMovementMethod());

        //((TextView) popupInfo.findViewById(R.id.textParseURL)).setText(clipboard.getType().toString().substring(0, 1).toUpperCase() + clipboard.getType().toString().substring(1).toLowerCase());
        ((TextView) popupInfo.findViewById(R.id.textParseURL)).requestLayout();
        ((TextView) popupEditClip.findViewById(R.id.textParseURLEdit)).requestLayout();

        popupInfo.findViewById(R.id.sernInformClipboardParseData).setVisibility(View.GONE);


        if (clipboard.getType().equals(ClipboardEnum.FACEBOOK) || clipboard.getType().equals(ClipboardEnum.LINKEDIN) || clipboard.getType().equals(ClipboardEnum.INSTAGRAM) || clipboard.getType().equals(ClipboardEnum.VK) ||
                clipboard.getType().equals(ClipboardEnum.YOUTUBE) || clipboard.getType().equals(ClipboardEnum.TWITTER) || clipboard.getType().equals(ClipboardEnum.MEDIUM) || clipboard.getType().equals(ClipboardEnum.WEB) ||
                clipboard.getType().equals(ClipboardEnum.GITHUB) || clipboard.getType().equals(ClipboardEnum.TUMBLR) || clipboard.getType().equals(ClipboardEnum.ANGEL) || clipboard.getType().equals(ClipboardEnum.G_DOC) ||
                clipboard.getType().equals(ClipboardEnum.G_SHEET)) {

            popupInfo.findViewById(R.id.imageView3).setVisibility(View.GONE);
            popupInfo.findViewById(R.id.search_note_popup).setVisibility(View.VISIBLE);
            ((ImageView) popupInfo.findViewById(R.id.imageView3)).setImageDrawable(activity.getResources().getDrawable(R.drawable.icn_extract_social));


            if(clipboard.getNameFromSocial() != null && !clipboard.getNameFromSocial().isEmpty()) {

                ((TextView) popupInfo.findViewById(R.id.textParseURL)).setText(clipboard.getNameFromSocial().trim());
                ((TextView) popupInfo.findViewById(R.id.textParseURL)).requestLayout();
                ((TextView) popupInfo.findViewById(R.id.textParseURL)).setTextColor(mainView.getContext().getResources().getColor(R.color.black));

                ((TextView) popupEditClip.findViewById(R.id.textParseURLEdit)).setText(clipboard.getNameFromSocial().trim());
                ((TextView) popupEditClip.findViewById(R.id.textParseURLEdit)).requestLayout();
                ((TextView) popupEditClip.findViewById(R.id.textParseURLEdit)).setTextColor(mainView.getContext().getResources().getColor(R.color.black));


            }else{
                parsingURLClipboard = new ClibpboardAdapter.ParsingURLClipboard(clipboard, popupInfo, popupEditClip);
                parsingURLClipboard.execute();
            }

            ((ImageView) popupInfo.findViewById(R.id.icn_mail_clip)).setImageDrawable(activity.getResources().getDrawable(R.drawable.icn_extract_social));


        } else if (clipboard.getType().equals(ClipboardEnum.NAME) || clipboard.getType().equals(ClipboardEnum.NOTE) || clipboard.getType().equals(ClipboardEnum.COMPANY) || clipboard.getType().equals(ClipboardEnum.POSITION) ||
                clipboard.getType().equals(ClipboardEnum.PHONE) || clipboard.getType().equals(ClipboardEnum.EMAIL) || clipboard.getType().equals(ClipboardEnum.ADDRESS)) {
            popupInfo.findViewById(R.id.imageView3).setVisibility(View.GONE);
            popupInfo.findViewById(R.id.search_note_popup).setVisibility(View.VISIBLE);
            ((ImageView) popupInfo.findViewById(R.id.imageView3)).setImageDrawable(activity.getResources().getDrawable(R.drawable.new_icon));

            if (!clipboard.getType().equals(ClipboardEnum.EMAIL) || !clipboard.getType().equals(ClipboardEnum.PHONE)) {
                ((ImageView) popupInfo.findViewById(R.id.icn_mail_clip)).setImageDrawable(activity.getResources().getDrawable(R.drawable.icn_extract_social));
            }
            if (clipboard.getType().equals(ClipboardEnum.EMAIL)) {
                ((ImageView) popupInfo.findViewById(R.id.icn_mail_clip)).setImageDrawable(activity.getResources().getDrawable(R.drawable.icn_remind_message));
            }

        } else {
            ((ImageView) popupInfo.findViewById(R.id.imageView3)).setImageDrawable(activity.getResources().getDrawable(R.drawable.new_icon));
            popupInfo.findViewById(R.id.imageView3).setVisibility(View.VISIBLE);
            popupInfo.findViewById(R.id.search_note_popup).setVisibility(View.GONE);
        }

                /*if (clipboard.getValueCopy().length() > 25) {
                    StringBuffer stringBuffer = new StringBuffer(clipboard.getValueCopy().trim());

                    Rect bounds = new Rect();
                    Paint textPaint = new Paint();
                    textPaint.setTypeface(Typeface.DEFAULT);// your preference here
                    textPaint.setTextSize(16);

                    int pos = 0;
                    for (int i = 0; i < stringBuffer.length(); i++) {

                        textPaint.getTextBounds(stringBuffer.substring(pos, i), 0, i - pos, bounds);
                        if (bounds.width() > 230) {
                            stringBuffer.insert(i, "\n");
                            pos = i;
                        }
                    }

                    ((TextView) popupInfo.findViewById(R.id.valueUpdClip)).setText(stringBuffer);
                } else*/

        ((TextView) popupInfo.findViewById(R.id.valueUpdClip)).setText(clipboard.getValueCopy().trim());

        if (!clipboard.getType().equals(ClipboardEnum.NOTE)) {
            ((TextView) popupInfo.findViewById(R.id.valueUpdClip)).setTextColor(activity.getResources().getColor(R.color.colorPrimaryDark));
        } else {
            ((TextView) popupInfo.findViewById(R.id.valueUpdClip)).setTextColor(activity.getResources().getColor(R.color.black));
        }

        //((TextView) popupInfo.findViewById(R.id.valueUpdClip)).setTextIsSelectable(true);

        ((TextView) popupInfo.findViewById(R.id.valueUpdClip)).scrollTo(0, 0);

        if (clipboards.get(position).getType().equals(ClipboardEnum.WEB) || clipboards.get(position).getType().equals(ClipboardEnum.GITHUB)) {

            ((ImageView) popupInfo.findViewById(R.id.imageCLipsType)).setImageDrawable(activity.getResources().getDrawable(R.drawable.icn_popup_web_blue));
        } else if (clipboards.get(position).getType().equals(ClipboardEnum.FACEBOOK)) {
            ((ImageView) popupInfo.findViewById(R.id.imageCLipsType)).setImageTintMode(null);
            ((ImageView) popupInfo.findViewById(R.id.imageCLipsType)).setImageDrawable(activity.getResources().getDrawable(R.drawable.icn_social_facebook));
        } else if (clipboards.get(position).getType().equals(ClipboardEnum.VK)) {
            ((ImageView) popupInfo.findViewById(R.id.imageCLipsType)).setImageTintMode(null);
            ((ImageView) popupInfo.findViewById(R.id.imageCLipsType)).setImageDrawable(activity.getResources().getDrawable(R.drawable.icn_social_vk));
        } else if (clipboards.get(position).getType().equals(ClipboardEnum.INSTAGRAM)) {
            ((ImageView) popupInfo.findViewById(R.id.imageCLipsType)).setImageTintMode(null);
            ((ImageView) popupInfo.findViewById(R.id.imageCLipsType)).setImageDrawable(activity.getResources().getDrawable(R.drawable.icn_social_instagram));
        } else if (clipboards.get(position).getType().equals(ClipboardEnum.LINKEDIN)) {
            ((ImageView) popupInfo.findViewById(R.id.imageCLipsType)).setImageTintMode(null);
            ((ImageView) popupInfo.findViewById(R.id.imageCLipsType)).setImageDrawable(activity.getResources().getDrawable(R.drawable.icn_social_linkedin));
        } else if (clipboards.get(position).getType().equals(ClipboardEnum.TWITTER)) {
            ((ImageView) popupInfo.findViewById(R.id.imageCLipsType)).setImageTintMode(null);
            ((ImageView) popupInfo.findViewById(R.id.imageCLipsType)).setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_twitter_64));
        } else if (clipboards.get(position).getType().equals(ClipboardEnum.YOUTUBE)) {
            ((ImageView) popupInfo.findViewById(R.id.imageCLipsType)).setImageTintMode(null);
            ((ImageView) popupInfo.findViewById(R.id.imageCLipsType)).setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_youtube_48));
        } else if (clipboards.get(position).getType().equals(ClipboardEnum.G_DOC)) {
            ((ImageView) popupInfo.findViewById(R.id.imageCLipsType)).setImageTintMode(null);
            ((ImageView) popupInfo.findViewById(R.id.imageCLipsType)).setImageDrawable(activity.getResources().getDrawable(R.drawable.google_docs));
        } else if (clipboards.get(position).getType().equals(ClipboardEnum.G_SHEET)) {
            ((ImageView) popupInfo.findViewById(R.id.imageCLipsType)).setImageTintMode(null);
            ((ImageView) popupInfo.findViewById(R.id.imageCLipsType)).setImageDrawable(activity.getResources().getDrawable(R.drawable.google_sheets));
        } else if (clipboards.get(position).getType().equals(ClipboardEnum.ANGEL)) {
            ((ImageView) popupInfo.findViewById(R.id.imageCLipsType)).setImageTintMode(null);
            ((ImageView) popupInfo.findViewById(R.id.imageCLipsType)).setImageDrawable(activity.getResources().getDrawable(R.drawable.angel));
        } else if (clipboards.get(position).getType().equals(ClipboardEnum.MEDIUM)) {
            ((ImageView) popupInfo.findViewById(R.id.imageCLipsType)).setImageTintMode(null);
            ((ImageView) popupInfo.findViewById(R.id.imageCLipsType)).setImageDrawable(activity.getResources().getDrawable(R.drawable.medium_size_64));
        } else if (clipboards.get(position).getType().equals(ClipboardEnum.TUMBLR)) {
            ((ImageView) popupInfo.findViewById(R.id.imageCLipsType)).setImageTintMode(null);
            ((ImageView) popupInfo.findViewById(R.id.imageCLipsType)).setImageDrawable(activity.getResources().getDrawable(R.drawable.tumblr));
        } else if (clipboards.get(position).getType().equals(ClipboardEnum.EMAIL)) {
            ((ImageView) popupInfo.findViewById(R.id.imageCLipsType)).setImageTintMode(null);
            ((ImageView) popupInfo.findViewById(R.id.imageCLipsType)).setImageDrawable(activity.getResources().getDrawable(R.drawable.gmail));
        } else if (clipboards.get(position).getType().equals(ClipboardEnum.PHONE)) {
            ((ImageView) popupInfo.findViewById(R.id.imageCLipsType)).setImageDrawable(activity.getResources().getDrawable(R.drawable.icn_popup_call));
        } else if (clipboards.get(position).getType().equals(ClipboardEnum.ADDRESS)) {
            ((ImageView) popupInfo.findViewById(R.id.imageCLipsType)).setImageDrawable(activity.getResources().getDrawable(R.drawable.icn_fab_maps));
        } else {
            ((ImageView) popupInfo.findViewById(R.id.imageCLipsType)).setImageTintMode(null);
            ((ImageView) popupInfo.findViewById(R.id.imageCLipsType)).setImageDrawable(activity.getResources().getDrawable(R.drawable.icn_fab_google));
        }



        popupInfo.findViewById(R.id.head_priview_clips).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupInfo.setVisibility(View.GONE);
            }
        });


        popupInfo.findViewById(R.id.editPopupCopyClip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = clipboard.getValueCopy().trim();

                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
                    android.text.ClipboardManager clipboard = (android.text.ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboard.setText(text);
                } else {
                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
                    clipboard.setPrimaryClip(clip);
                }
                Toast.makeText(mainView.getContext(), "Copied", Toast.LENGTH_SHORT).show();
                notifyDataSetChanged();
            }
        });


        popupInfo.findViewById(R.id.updateContactClip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupInfo.setVisibility(View.GONE);

                if(popupEditClip.getMeasuredHeight() > activity.findViewById(R.id.popupContainer).getMeasuredHeight()){
                    ViewGroup.LayoutParams lp = popupEditClip.getLayoutParams();
                    lp.height = (int) (155 * activity.getResources().getDisplayMetrics().density);
                    popupEditClip.setLayoutParams(lp);
                }

                popupEditClip.findViewById(R.id.t_social_clip).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openChangeType(clipboard);
                    }
                });

                popupEditClip.setVisibility(View.VISIBLE);
                ((EditText) popupEditClip.findViewById(R.id.dataToEditClip)).setText(((TextView) popupInfo.findViewById(R.id.valueUpdClip)).getText());
                EditText value = ((EditText) popupEditClip.findViewById(R.id.dataToEditClip));

                        /*if (clipboard.getImageTypeClipboard() != null) {
                            ((ImageView) popupEditClip.findViewById(R.id.iconDataEdit)).setImageURI(Uri.parse(clipboard.getImageTypeClipboard()));
                            ((TextView) popupEditClip.findViewById(R.id.textParseURLEdit)).setText(clipboard.getType().toString().substring(0, 1).toUpperCase() + clipboard.getType().toString().substring(1).toLowerCase());
                        } else {
                            ((ImageView) popupEditClip.findViewById(R.id.iconDataEdit)).setImageDrawable(activity.getResources().getDrawable(R.drawable.icn_notes));
                            ((TextView) popupEditClip.findViewById(R.id.textParseURLEdit)).setText("Notes");
                        }

                        ((TextView) popupEditClip.findViewById(R.id.textParseURLEdit)).requestLayout();*/


                //value.setMovementMethod(new ScrollingMovementMethod());

                popupEditClip.findViewById(R.id.updateContactField).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String text = ((EditText) popupEditClip.findViewById(R.id.dataToEditClip)).getText().toString().trim();
                        if (!text.isEmpty()) {


                                    /*if (text.length() > 25) {
                                        StringBuffer stringBuffer = new StringBuffer(text.trim());
                                        Rect bounds = new Rect();
                                        Paint textPaint = new Paint();
                                        textPaint.setTypeface(Typeface.DEFAULT);// your preference here
                                        textPaint.setTextSize(16);
                                        int pos = 0;
                                        for (int i = 0; i < stringBuffer.length(); i++) {
                                            textPaint.getTextBounds(stringBuffer.substring(pos, i), 0, i - pos, bounds);
                                            if (bounds.width() > 230) {
                                                stringBuffer.insert(i, "\n");
                                                pos = i;
                                            }
                                        }

                                        ((TextView) popupInfo.findViewById(R.id.valueUpdClip)).setText(stringBuffer);
                                    } else*/
                            ((TextView) popupInfo.findViewById(R.id.valueUpdClip)).setText(text);


                            clipboards.get(position).setValueCopy(text);

                            popupInfo.findViewById(R.id.frame_send_messageInfo_clipboard_parse).setVisibility(View.GONE);

                            ClipboardEnum enumOld = clipboards.get(position).getType();
                            ClipboardType.updateClipboardType(clipboards.get(position), mainView.getContext());
                            if (!enumOld.equals(clipboards.get(position).getType())) {
                                notifyItemChanged(position);


                                if (clipboard.getImageTypeClipboard() != null) {
                                    ((ImageView) popupInfo.findViewById(R.id.iconData)).setImageURI(Uri.parse(clipboard.getImageTypeClipboard()));
                                    ((TextView) popupInfo.findViewById(R.id.textParseURL)).setText(clipboard.getType().toString().substring(0, 1).toUpperCase() + clipboard.getType().toString().substring(1).toLowerCase());

                                    ((ImageView) popupEditClip.findViewById(R.id.iconDataEdit)).setImageURI(Uri.parse(clipboard.getImageTypeClipboard()));
                                    ((TextView) popupEditClip.findViewById(R.id.textParseURLEdit)).setText(clipboard.getType().toString().substring(0, 1).toUpperCase() + clipboard.getType().toString().substring(1).toLowerCase());
                                } else {
                                    ((ImageView) popupInfo.findViewById(R.id.iconData)).setImageDrawable(activity.getResources().getDrawable(R.drawable.icn_notes));
                                    ((TextView) popupInfo.findViewById(R.id.textParseURL)).setText("Notes");

                                    ((ImageView) popupEditClip.findViewById(R.id.iconDataEdit)).setImageDrawable(activity.getResources().getDrawable(R.drawable.icn_notes));
                                    ((TextView) popupEditClip.findViewById(R.id.textParseURLEdit)).setText("Notes");
                                }

                                ((TextView) popupInfo.findViewById(R.id.textParseURL)).requestLayout();


                                if (clipboard.getType().equals(ClipboardEnum.FACEBOOK) || clipboard.getType().equals(ClipboardEnum.LINKEDIN) || clipboard.getType().equals(ClipboardEnum.INSTAGRAM) || clipboard.getType().equals(ClipboardEnum.VK) ||
                                        clipboard.getType().equals(ClipboardEnum.YOUTUBE) || clipboard.getType().equals(ClipboardEnum.TWITTER) || clipboard.getType().equals(ClipboardEnum.MEDIUM) || clipboard.getType().equals(ClipboardEnum.WEB) ||
                                        clipboard.getType().equals(ClipboardEnum.GITHUB) || clipboard.getType().equals(ClipboardEnum.TUMBLR) || clipboard.getType().equals(ClipboardEnum.ANGEL) || clipboard.getType().equals(ClipboardEnum.G_DOC) ||
                                        clipboard.getType().equals(ClipboardEnum.G_SHEET)) {

                                    popupInfo.findViewById(R.id.imageView3).setVisibility(View.GONE);
                                    popupInfo.findViewById(R.id.search_note_popup).setVisibility(View.VISIBLE);
                                    ((ImageView) popupInfo.findViewById(R.id.imageView3)).setImageDrawable(activity.getResources().getDrawable(R.drawable.icn_extract_social));


                                    parsingURLClipboard = new ClibpboardAdapter.ParsingURLClipboard(clipboard, popupInfo, popupEditClip);
                                    parsingURLClipboard.execute();

                                    ((ImageView) popupInfo.findViewById(R.id.icn_mail_clip)).setImageDrawable(activity.getResources().getDrawable(R.drawable.icn_extract_social));


                                } else if (clipboards.get(position).getType().equals(ClipboardEnum.NAME) || clipboards.get(position).getType().equals(ClipboardEnum.NOTE) || clipboards.get(position).getType().equals(ClipboardEnum.COMPANY) || clipboards.get(position).getType().equals(ClipboardEnum.POSITION) ||
                                        clipboards.get(position).getType().equals(ClipboardEnum.PHONE) || clipboards.get(position).getType().equals(ClipboardEnum.EMAIL) || clipboards.get(position).getType().equals(ClipboardEnum.ADDRESS)) {
                                    popupInfo.findViewById(R.id.imageView3).setVisibility(View.GONE);
                                    popupInfo.findViewById(R.id.search_note_popup).setVisibility(View.VISIBLE);
                                    ((ImageView) popupInfo.findViewById(R.id.imageView3)).setImageDrawable(activity.getResources().getDrawable(R.drawable.new_icon));

                                    if (!clipboards.get(position).getType().equals(ClipboardEnum.EMAIL) || !clipboards.get(position).getType().equals(ClipboardEnum.PHONE)) {
                                        ((ImageView) popupInfo.findViewById(R.id.icn_mail_clip)).setImageDrawable(activity.getResources().getDrawable(R.drawable.icn_extract_social));
                                    }
                                    if (clipboards.get(position).getType().equals(ClipboardEnum.EMAIL)) {
                                        ((ImageView) popupInfo.findViewById(R.id.icn_mail_clip)).setImageDrawable(activity.getResources().getDrawable(R.drawable.icn_remind_message));
                                    }

                                } else {
                                    ((ImageView) popupInfo.findViewById(R.id.imageView3)).setImageDrawable(activity.getResources().getDrawable(R.drawable.new_icon));
                                    popupInfo.findViewById(R.id.imageView3).setVisibility(View.VISIBLE);
                                    popupInfo.findViewById(R.id.search_note_popup).setVisibility(View.GONE);
                                }


                                if (clipboards.get(position).getType().equals(ClipboardEnum.WEB) || clipboards.get(position).getType().equals(ClipboardEnum.GITHUB)) {

                                    ((ImageView) popupInfo.findViewById(R.id.imageCLipsType)).setImageDrawable(activity.getResources().getDrawable(R.drawable.icn_popup_web_blue));
                                } else if (clipboards.get(position).getType().equals(ClipboardEnum.FACEBOOK)) {
                                    ((ImageView) popupInfo.findViewById(R.id.imageCLipsType)).setImageTintMode(null);
                                    ((ImageView) popupInfo.findViewById(R.id.imageCLipsType)).setImageDrawable(activity.getResources().getDrawable(R.drawable.icn_social_facebook));
                                } else if (clipboards.get(position).getType().equals(ClipboardEnum.VK)) {
                                    ((ImageView) popupInfo.findViewById(R.id.imageCLipsType)).setImageTintMode(null);
                                    ((ImageView) popupInfo.findViewById(R.id.imageCLipsType)).setImageDrawable(activity.getResources().getDrawable(R.drawable.icn_social_vk));
                                } else if (clipboards.get(position).getType().equals(ClipboardEnum.INSTAGRAM)) {
                                    ((ImageView) popupInfo.findViewById(R.id.imageCLipsType)).setImageTintMode(null);
                                    ((ImageView) popupInfo.findViewById(R.id.imageCLipsType)).setImageDrawable(activity.getResources().getDrawable(R.drawable.icn_social_instagram));
                                } else if (clipboards.get(position).getType().equals(ClipboardEnum.LINKEDIN)) {
                                    ((ImageView) popupInfo.findViewById(R.id.imageCLipsType)).setImageTintMode(null);
                                    ((ImageView) popupInfo.findViewById(R.id.imageCLipsType)).setImageDrawable(activity.getResources().getDrawable(R.drawable.icn_social_linkedin));
                                } else if (clipboards.get(position).getType().equals(ClipboardEnum.TWITTER)) {
                                    ((ImageView) popupInfo.findViewById(R.id.imageCLipsType)).setImageTintMode(null);
                                    ((ImageView) popupInfo.findViewById(R.id.imageCLipsType)).setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_twitter_64));
                                } else if (clipboards.get(position).getType().equals(ClipboardEnum.YOUTUBE)) {
                                    ((ImageView) popupInfo.findViewById(R.id.imageCLipsType)).setImageTintMode(null);
                                    ((ImageView) popupInfo.findViewById(R.id.imageCLipsType)).setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_youtube_48));
                                } else if (clipboards.get(position).getType().equals(ClipboardEnum.G_DOC)) {
                                    ((ImageView) popupInfo.findViewById(R.id.imageCLipsType)).setImageTintMode(null);
                                    ((ImageView) popupInfo.findViewById(R.id.imageCLipsType)).setImageDrawable(activity.getResources().getDrawable(R.drawable.google_docs));
                                } else if (clipboards.get(position).getType().equals(ClipboardEnum.G_SHEET)) {
                                    ((ImageView) popupInfo.findViewById(R.id.imageCLipsType)).setImageTintMode(null);
                                    ((ImageView) popupInfo.findViewById(R.id.imageCLipsType)).setImageDrawable(activity.getResources().getDrawable(R.drawable.google_sheets));
                                } else if (clipboards.get(position).getType().equals(ClipboardEnum.ANGEL)) {
                                    ((ImageView) popupInfo.findViewById(R.id.imageCLipsType)).setImageTintMode(null);
                                    ((ImageView) popupInfo.findViewById(R.id.imageCLipsType)).setImageDrawable(activity.getResources().getDrawable(R.drawable.angel));
                                } else if (clipboards.get(position).getType().equals(ClipboardEnum.MEDIUM)) {
                                    ((ImageView) popupInfo.findViewById(R.id.imageCLipsType)).setImageTintMode(null);
                                    ((ImageView) popupInfo.findViewById(R.id.imageCLipsType)).setImageDrawable(activity.getResources().getDrawable(R.drawable.medium_size_64));
                                } else if (clipboards.get(position).getType().equals(ClipboardEnum.TUMBLR)) {
                                    ((ImageView) popupInfo.findViewById(R.id.imageCLipsType)).setImageTintMode(null);
                                    ((ImageView) popupInfo.findViewById(R.id.imageCLipsType)).setImageDrawable(activity.getResources().getDrawable(R.drawable.tumblr));
                                } else if (clipboards.get(position).getType().equals(ClipboardEnum.EMAIL)) {
                                    ((ImageView) popupInfo.findViewById(R.id.imageCLipsType)).setImageTintMode(null);
                                    ((ImageView) popupInfo.findViewById(R.id.imageCLipsType)).setImageDrawable(activity.getResources().getDrawable(R.drawable.gmail));
                                } else if (clipboards.get(position).getType().equals(ClipboardEnum.PHONE)) {
                                    ((ImageView) popupInfo.findViewById(R.id.imageCLipsType)).setImageDrawable(activity.getResources().getDrawable(R.drawable.icn_popup_call));
                                } else {
                                    ((ImageView) popupInfo.findViewById(R.id.imageCLipsType)).setImageTintMode(null);
                                    ((ImageView) popupInfo.findViewById(R.id.imageCLipsType)).setImageDrawable(activity.getResources().getDrawable(R.drawable.icn_fab_google));
                                }


                            }

                            popupInfo.setVisibility(View.VISIBLE);
                        } else {
                            clipboards.remove(position);
                            popupInfo.setVisibility(View.GONE);
                        }
                        popupEditClip.setVisibility(View.GONE);

                       /* if (typeOfData.equals(MagicType.CLIPBOARD))
                            EventBus.getDefault().post(new SaveClipboard());
                        else
                            EventBus.getDefault().post(new SaveHistory());*/
                        notifyDataSetChanged();
                    }
                });


            }
        });


        popupInfo.findViewById(R.id.delete_contact_numberClip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        activity);
                alertDialogBuilder.setTitle("Do you want to delete clipboard?");
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Yes", (dialog, id) -> {

                            if (checkHashClip != null && clipboards.get(position) != null && checkHashClip.contains(clipboards.get(position))) {

                                if (clipboards.get(position).getListClipboards() != null && !clipboards.get(position).getListClipboards().isEmpty()) {
                                    for (Clipboard clipboard1 : clipboards.get(position).getListClipboards()) {
                                        if (checkHashClip.contains(clipboard1))
                                            checkHashClip.remove(clipboard1);
                                    }
                                }

                                checkHashClip.remove(clipboards.get(position));


                                int co = 0;
                                for (Clipboard cl : checkHashClip) {
                                    if (cl.getListClipboards() == null || cl.getListClipboards().isEmpty()) {
                                        co++;
                                    }
                                }

                                if (co > 0) {

                                    ((TextView) activity.findViewById(R.id.textTopClipboard)).setText(co + " fields selected");
                                } else {
                                    if (typeOfData.equals(MagicType.CLIPBOARD)) {
                                        activity.findViewById(R.id.statClipboard).setVisibility(View.VISIBLE);
                                        activity.findViewById(R.id.textTopClipboard).setVisibility(View.INVISIBLE);
                                        activity.findViewById(R.id.removeClipData).setVisibility(View.GONE);
                                        activity.findViewById(R.id.shareClipData).setVisibility(View.GONE);
                                        activity.findViewById(R.id.copyClipData).setVisibility(View.GONE);
                                    } else {
                                        activity.findViewById(R.id.statHistory).setVisibility(View.VISIBLE);
                                        activity.findViewById(R.id.textTopHistory).setVisibility(View.INVISIBLE);
                                        activity.findViewById(R.id.removeHistoryData).setVisibility(View.GONE);
                                        activity.findViewById(R.id.shareHistoryData).setVisibility(View.GONE);
                                        activity.findViewById(R.id.copyHistoryData).setVisibility(View.GONE);
                                    }
                                }

                            }

                            //clipboards.remove(position);

                            if (clipboards.contains(clipboard)) {

                                clipboards.remove(clipboard);
                            }

                            for (int i = 0; i < listOpen.size(); i++) {
                                if (listOpen.get(i).getListClipboards().contains(clipboard)) {

                                    listOpen.get(i).getListClipboards().remove(clipboard);
                                    if (listOpen.get(i).getListClipboards().isEmpty()) {
                                        clipboards.remove(listOpen.get(i));
                                        listOpen.remove(i);
                                        break;
                                    }
                                }
                            }

                            if (listOpen.contains(clipboard)) {
                                for (Clipboard clipboard1 : clipboard.getListClipboards()) {
                                    if (clipboards.contains(clipboard1))
                                        clipboards.remove(clipboard1);
                                }
                            }
                            if (typeOfData.equals(MagicType.CLIPBOARD))
                                ((TextView) activity.findViewById(R.id.statClipboard)).setText(getCountCLipboards() + " entries, " + getCountDetectedClipboards() + " detected");
                            else
                                ((TextView) activity.findViewById(R.id.statHistory)).setText(getCountCLipboards() + " entries, " + getCountDetectedClipboards() + " detected");

                           /* if (typeOfData.equals(MagicType.CLIPBOARD))
                                EventBus.getDefault().post(new SaveClipboard());
                            else
                                EventBus.getDefault().post(new SaveHistory());*/
                            notifyDataSetChanged();
                            popupInfo.setVisibility(View.GONE);
                        })
                        .setNegativeButton("No", (dialog, id) -> dialog.cancel());

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();


            }
        });

        popupInfo.findViewById(R.id.editPopupMailClip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (clipboard.getType().equals(ClipboardEnum.FACEBOOK) || clipboard.getType().equals(ClipboardEnum.LINKEDIN) || clipboard.getType().equals(ClipboardEnum.INSTAGRAM) || clipboard.getType().equals(ClipboardEnum.VK) ||
                        clipboard.getType().equals(ClipboardEnum.YOUTUBE) || clipboard.getType().equals(ClipboardEnum.TWITTER) || clipboard.getType().equals(ClipboardEnum.MEDIUM) || clipboard.getType().equals(ClipboardEnum.WEB) ||
                        clipboard.getType().equals(ClipboardEnum.GITHUB) || clipboard.getType().equals(ClipboardEnum.TUMBLR) || clipboard.getType().equals(ClipboardEnum.G_SHEET) || clipboard.getType().equals(ClipboardEnum.G_DOC) ||
                        clipboard.getType().equals(ClipboardEnum.ANGEL) || clipboard.getType().equals(ClipboardEnum.NAME) || clipboard.getType().equals(ClipboardEnum.NOTE) || clipboard.getType().equals(ClipboardEnum.COMPANY) || clipboard.getType().equals(ClipboardEnum.POSITION) ||
                        /*clipboard.getType().equals(ClipboardEnum.PHONE) || clipboard.getType().equals(ClipboardEnum.EMAIL) ||*/ clipboard.getType().equals(ClipboardEnum.ADDRESS)) {
                    //activity.findViewById(R.id.popupGlobalSearch).setVisibility(View.VISIBLE);
                    //openGlobalPopup(clipboard);


                    holder.frameCircleSearch.callOnClick();
                }

                if (clipboards.get(position).getType().equals(ClipboardEnum.PHONE)) {
                    //activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + clipboards.get(position).getValueCopy().toString().trim())));
                } else if (clipboards.get(position).getType().equals(ClipboardEnum.EMAIL)) {


                            /*Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                            emailIntent.setData(Uri.parse("mailto:" + clipboards.get(position).getValueCopy().toString().trim()));
                            activity.startActivity(Intent.createChooser(emailIntent, "Send email"));*/


                    //============================================ new 22.05.2020

                    SharedPreferences mSettings;
                    mSettings = activity.getSharedPreferences("accountUser", Context.MODE_PRIVATE);
                    Set<String> set = mSettings.getStringSet("accounts", null);

                    String finalReplyFrom = null;

                    if (set != null && !set.isEmpty()) {

                        for(String s : set){
                            finalReplyFrom = s;
                            break;
                        }

                    }

                    if(finalReplyFrom == null){

                        String senderName = "";
                        if (clipboards.get(position).getListContactsSearch() != null && !clipboards.get(position).getListContactsSearch().isEmpty() && clipboards.get(position).getListContactsSearch().get(0) != null) {
                            senderName = clipboards.get(position).getListContactsSearch().get(0).getName();
                        }
                        //====================================

                        ArrayList<String> sTo = new ArrayList<>();
                        ArrayList<String> sName = new ArrayList<>();

                        sTo.add( clipboards.get(position).getValueCopy().toString().trim());
                        sName.add(senderName);

                        ShareTemplatesMessageReply.showChooseAccount(activity, false,sTo, sName, finalReplyFrom, null, ClibpboardAdapter.this, false);



                        //====================================
                               /* try {
                                    Toast.makeText(activity, "Choose account", Toast.LENGTH_SHORT).show();
                                }catch (NullPointerException e){
                                    e.printStackTrace();
                                }*/

                        //return;
                    }else {


                        String senderName = "";
                        if (clipboards.get(position).getListContactsSearch() != null && !clipboards.get(position).getListContactsSearch().isEmpty() && clipboards.get(position).getListContactsSearch().get(0) != null) {
                            senderName = clipboards.get(position).getListContactsSearch().get(0).getName();
                        }

                        ArrayList<String> sTo = new ArrayList<>();
                        ArrayList<String> sName = new ArrayList<>();

                        sTo.add( clipboards.get(position).getValueCopy().toString().trim());
                        sName.add(senderName);

                        ShareTemplatesMessageReply.showTemplatesPopup(activity, false, sTo, sName, finalReplyFrom, null, ClibpboardAdapter.this, false);


                    }
                    //============================================


                }

            }
        });

        popupInfo.findViewById(R.id.editPopupCallClip).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (clipboards.get(position).getType().equals(ClipboardEnum.EMAIL)) {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                    emailIntent.setData(Uri.parse("mailto:" + clipboards.get(position).getValueCopy().toString().trim()));
                    activity.startActivity(Intent.createChooser(emailIntent, "Send email"));
                    return true;
                } else if (clipboards.get(position).getType().equals(ClipboardEnum.NOTE)) {

                    Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                    String term = clipboards.get(position).getValueCopy();
                    intent.putExtra(SearchManager.QUERY, term);

                            /*Intent i = new Intent(Intent.ACTION_WEB_SEARCH);
                            i.setData(clipboards.get(position).getValueCopy());*/
                    activity.startActivity(Intent.createChooser(intent, "Choice browser"));



                }
                return false;
            }
        });

        popupInfo.findViewById(R.id.editPopupCallClip).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if (clipboards.get(position).getType().equals(ClipboardEnum.PHONE)) {
                    //activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("tel", clipboards.get(position).getValueCopy().toString().trim(), null)));

                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.fromParts("tel", clipboards.get(position).getValueCopy().toString().trim(), null));

                    activity.startActivity(Intent.createChooser(i, "Open with..."));

                } else if (clipboards.get(position).getType().equals(ClipboardEnum.INSTAGRAM) || clipboards.get(position).getType().equals(ClipboardEnum.FACEBOOK)
                        || clipboards.get(position).getType().equals(ClipboardEnum.VK) || clipboards.get(position).getType().equals(ClipboardEnum.LINKEDIN)
                        || clipboards.get(position).getType().equals(ClipboardEnum.WEB) || clipboards.get(position).getType().equals(ClipboardEnum.GITHUB)
                        || clipboards.get(position).getType().equals(ClipboardEnum.MEDIUM) || clipboards.get(position).getType().equals(ClipboardEnum.TUMBLR)
                        || clipboards.get(position).getType().equals(ClipboardEnum.ANGEL) || clipboards.get(position).getType().equals(ClipboardEnum.YOUTUBE)
                        || clipboards.get(position).getType().equals(ClipboardEnum.TWITTER)) {
                    try {
                        String uris = clipboards.get(position).getValueCopy();
                        if (!clipboards.get(position).getValueCopy().contains("https://") && !clipboards.get(position).getValueCopy().contains("http://"))
                            uris = "https://" + uris;



                        Intent i = new Intent(Intent.ACTION_VIEW);

                        //i.addCategory(Intent.CATEGORY_APP_BROWSER);
                        //i.setPackage("com.android.chrome");
                        i.setData(Uri.parse(uris));


                        activity.startActivity(Intent.createChooser(i, "Open with..."));

                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                } else if (clipboards.get(position).getType().equals(ClipboardEnum.EMAIL)) {

                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                    emailIntent.setData(Uri.parse("mailto:" + clipboards.get(position).getValueCopy().toString().trim()));
                    activity.startActivity(Intent.createChooser(emailIntent, "Send email"));

                } else if (clipboards.get(position).getType().equals(ClipboardEnum.G_DOC) || clipboards.get(position).getType().equals(ClipboardEnum.G_SHEET)) {
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(clipboards.get(position).getValueCopy()));
                    activity.startActivity(Intent.createChooser(i, "Open with..."));

                } else {
                    Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                    String term = clipboards.get(position).getValueCopy();
                    intent.putExtra(SearchManager.QUERY, term);
                    activity.startActivity(Intent.createChooser(intent, "Open with..."));
                    //activity.startActivity(intent);
                }
                return true;
            }
        });

        popupInfo.findViewById(R.id.editPopupCallClip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clipboards.get(position).getType().equals(ClipboardEnum.PHONE))
                    activity.startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", clipboards.get(position).getValueCopy().toString().trim(), null)));
                else if (clipboards.get(position).getType().equals(ClipboardEnum.FACEBOOK)) {

                    Intent intent;
                    if (clipboards.get(position).getValueCopy().contains("?id=")) {
                        String idProfile = clipboards.get(position).getValueCopy().substring(clipboards.get(position).getValueCopy().indexOf('=') + 1, clipboards.get(position).getValueCopy().length());
                        if (idProfile.contains("&")) {
                            idProfile = idProfile.substring(0, idProfile.indexOf("&"));
                        }
                        intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/" + idProfile));

                    } else if (clipboards.get(position).getValueCopy().contains("/people/")) {
                        String idProfile = clipboards.get(position).getValueCopy().substring(clipboards.get(position).getValueCopy().lastIndexOf('/') + 1, clipboards.get(position).getValueCopy().length());
                        if (idProfile.contains("&")) {
                            idProfile = idProfile.substring(0, idProfile.indexOf("&"));
                        }

                        intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/" + idProfile));

                    } else
                        intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://facewebmodal/f?href=" + clipboards.get(position).getValueCopy().replace("fb","facebook")));

                    PackageManager packageManager = activity.getPackageManager();
                    List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
                    boolean isIntentSafe = activities.size() > 0;
                    if (!isIntentSafe) {
                                /*Intent intent2 = new Intent(Intent.ACTION_VIEW);
                                intent2.setData(Uri.parse("market://details?id=com.facebook.katana"));
                                activity.startActivity(intent2);*/
                        try {
                            String uris = clipboards.get(position).getValueCopy();
                            if (!clipboards.get(position).getValueCopy().contains("https://") && !clipboards.get(position).getValueCopy().contains("http://"))
                                uris = "https://" + uris;

                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(uris));
                            activity.startActivity(i);
                        } catch (Exception e) {

                        }
                    } else
                        activity.startActivity(intent);
                } else if (clipboards.get(position).getType().equals(ClipboardEnum.VK)) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format(clipboards.get(position).getValueCopy().toString().trim())));
                    Intent intent2 = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("vkontakte://profile/")));
                    try {
                        activity.startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                        PackageManager packageManager = activity.getPackageManager();
                        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent2, 0);
                        boolean isIntentSafe = activities.size() > 0;
                        if (!isIntentSafe) {
                                    /*Intent intent3 = new Intent(Intent.ACTION_VIEW);
                                    intent3.setData(Uri.parse("market://details?id=com.vkontakte.android"));
                                    activity.startActivity(intent3);*/
                            try {
                                String uris = clipboards.get(position).getValueCopy();
                                if (!clipboards.get(position).getValueCopy().contains("https://") && !clipboards.get(position).getValueCopy().contains("http://"))
                                    uris = "https://" + uris;

                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(uris));
                                activity.startActivity(i);
                            } catch (Exception e2) {

                            }
                        } else
                            activity.startActivity(intent2);
                    }
                } else if (clipboards.get(position).getType().equals(ClipboardEnum.INSTAGRAM)) {
                    Uri uri = Uri.parse(clipboards.get(position).getValueCopy().toString().trim());
                    Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);
                    likeIng.setPackage("com.instagram.android");
                    PackageManager packageManager = activity.getPackageManager();
                    List<ResolveInfo> activities = packageManager.queryIntentActivities(likeIng, 0);
                    boolean isIntentSafe = activities.size() > 0;
                    if (!isIntentSafe) {
                                /*Intent intent2 = new Intent(Intent.ACTION_VIEW);
                                intent2.setData(Uri.parse("market://details?id=com.instagram.android"));
                                activity.startActivity(intent2);*/
                        try {
                            String uris = clipboards.get(position).getValueCopy();
                            if (!clipboards.get(position).getValueCopy().contains("https://") && !clipboards.get(position).getValueCopy().contains("http://"))
                                uris = "https://" + uris;

                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(uris));
                            activity.startActivity(i);
                        } catch (Exception e2) {

                        }
                    } else
                        activity.startActivity(likeIng);
                } else if (clipboards.get(position).getType().equals(ClipboardEnum.LINKEDIN)) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(clipboards.get(position).getValueCopy().toString().trim()));
                    PackageManager packageManager = activity.getPackageManager();
                    List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
                    boolean isIntentSafe = activities.size() > 0;
                    if (!isIntentSafe) {
                        Intent intent2 = new Intent(Intent.ACTION_VIEW);
                        intent2.setData(Uri.parse("https://www.linkedin.com/in/"));
                        activity.startActivity(intent2);
                    } else {
                        try {
                            activity.startActivity(intent);
                        } catch (Exception e) {
                            Intent intent2 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/"));
                            activity.startActivity(intent2);
                        }


                    }
                } else if (clipboards.get(position).getType().equals(ClipboardEnum.WEB) || clipboards.get(position).getType().equals(ClipboardEnum.GITHUB) || clipboards.get(position).getType().equals(ClipboardEnum.MEDIUM) || clipboards.get(position).getType().equals(ClipboardEnum.TUMBLR)
                        || clipboards.get(position).getType().equals(ClipboardEnum.ANGEL)) {
                    try {

                        String uri = clipboards.get(position).getValueCopy();
                        if (!uri.contains("https://") && !uri.contains("http://"))
                            uri = "https://" + uri;

                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(uri));
                        activity.startActivity(i);
                    } catch (Exception e) {

                    }
                } else if (clipboards.get(position).getType().equals(ClipboardEnum.TWITTER)) {
                    String text = clipboards.get(position).getValueCopy();
                    if (text.contains("twitter.com/")) {
                        text = text.substring(text.indexOf(".com/") + 5);
                    }
                    if (text.length() > 0 && text.charAt(0) == '@')
                        text = text.substring(1);
                    Intent intent = null;
                    try {
                        // get the Twitter app if possible

                        activity.getPackageManager().getPackageInfo("com.twitter.android", 0);
                        intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + text));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    } catch (Exception e) {
                        // no Twitter app, revert to browser
                        intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/" + text));
                    }
                    activity.startActivity(intent);

                } else if (clipboards.get(position).getType().equals(ClipboardEnum.YOUTUBE)) {
                    String text = clipboards.get(position).getValueCopy();
                    if (text.contains("youtu.be/") || text.contains("watch?v=")) {
                        if (text.contains("youtu.be/"))
                            text = text.substring(text.indexOf("youtu.be/") + 9);
                        else if (text.contains("watch?v="))
                            text = text.substring(text.indexOf("watch?v=") + 8);

                        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + text));

                        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://www.youtube.com/watch?v=" + text));

                        try {
                            activity.startActivity(appIntent);
                        } catch (ActivityNotFoundException ex) {
                            activity.startActivity(webIntent);
                        }
                    } else if (text.contains("user/") || text.contains("channel/") || text.contains("/c/")) {
                        try {
                            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(text)));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else if (clipboards.get(position).getType().equals(ClipboardEnum.YOUTUBE)) {
                    String text = clipboards.get(position).getValueCopy();
                    if (text.contains("youtu.be/") || text.contains("watch?v=")) {
                        if (text.contains("youtu.be/"))
                            text = text.substring(text.indexOf("youtu.be/") + 9);
                        else if (text.contains("watch?v="))
                            text = text.substring(text.indexOf("watch?v=") + 8);

                        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + text));

                        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://www.youtube.com/watch?v=" + text));

                        try {
                            activity.startActivity(appIntent);
                        } catch (ActivityNotFoundException ex) {
                            activity.startActivity(webIntent);
                        }
                    } else if (text.contains("user/") || text.contains("channel/") || text.contains("/c/")) {
                        try {
                            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(text)));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else if (clipboards.get(position).getType().equals(ClipboardEnum.YOUTUBE)) {
                    String text = clipboards.get(position).getValueCopy();
                    if (text.contains("youtu.be/") || text.contains("watch?v=")) {
                        if (text.contains("youtu.be/"))
                            text = text.substring(text.indexOf("youtu.be/") + 9);
                        else if (text.contains("watch?v="))
                            text = text.substring(text.indexOf("watch?v=") + 8);

                        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + text));

                        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://www.youtube.com/watch?v=" + text));

                        try {
                            activity.startActivity(appIntent);
                        } catch (ActivityNotFoundException ex) {
                            activity.startActivity(webIntent);
                        }
                    } else if (text.contains("user/") || text.contains("channel/") || text.contains("/c/")) {
                        try {
                            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(text)));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else if (clipboards.get(position).getType().equals(ClipboardEnum.EMAIL)) {

                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("message/rfc822");
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{clipboards.get(position).getValueCopy()});
                    intent.putExtra(Intent.EXTRA_SUBJECT, "");
                    intent.setPackage("com.google.android.gm");
                    if (intent.resolveActivity(activity.getPackageManager()) != null)
                        activity.startActivity(intent);

                } else if (clipboards.get(position).getType().equals(ClipboardEnum.G_DOC) || clipboards.get(position).getType().equals(ClipboardEnum.G_SHEET)) {
                    activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(clipboards.get(position).getValueCopy())));
                } else if (clipboards.get(position).getType().equals(ClipboardEnum.ADDRESS)) {

                    try {
                        Intent map = new Intent(Intent.ACTION_VIEW,
                                Uri.parse(String.format("geo:0,0?q=%s",
                                        URLEncoder.encode(clipboards.get(position).getValueCopy(), "UTF-8"))));

                        activity.startActivity(map);

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                } else {
                    Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                    String term = clipboards.get(position).getValueCopy();
                    intent.putExtra(SearchManager.QUERY, term);
                    activity.startActivity(intent);
                }


            }
        });

        popupInfo.findViewById(R.id.remind_contact_numberClip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        popupInfo.findViewById(R.id.call2me_contact_numberClip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                      /*  if (clipboard.getType().equals(ClipboardEnum.FACEBOOK) || clipboard.getType().equals(ClipboardEnum.LINKEDIN) || clipboard.getType().equals(ClipboardEnum.INSTAGRAM) || clipboard.getType().equals(ClipboardEnum.VK) ||
                                clipboard.getType().equals(ClipboardEnum.YOUTUBE) || clipboard.getType().equals(ClipboardEnum.TWITTER) || clipboard.getType().equals(ClipboardEnum.MEDIUM) || clipboard.getType().equals(ClipboardEnum.WEB) ||
                                clipboard.getType().equals(ClipboardEnum.GITHUB)) {

                            holder.frameCircleSearch.callOnClick();
                        }*/

                if (clipboard.getType().equals(ClipboardEnum.FACEBOOK) || clipboard.getType().equals(ClipboardEnum.LINKEDIN) || clipboard.getType().equals(ClipboardEnum.INSTAGRAM) || clipboard.getType().equals(ClipboardEnum.VK) ||
                        clipboard.getType().equals(ClipboardEnum.YOUTUBE) || clipboard.getType().equals(ClipboardEnum.TWITTER) || clipboard.getType().equals(ClipboardEnum.MEDIUM) || clipboard.getType().equals(ClipboardEnum.WEB) ||
                        clipboard.getType().equals(ClipboardEnum.GITHUB) || clipboard.getType().equals(ClipboardEnum.TUMBLR) || clipboard.getType().equals(ClipboardEnum.G_SHEET) || clipboard.getType().equals(ClipboardEnum.G_DOC) ||
                        clipboard.getType().equals(ClipboardEnum.ANGEL) || clipboard.getType().equals(ClipboardEnum.NAME) || clipboard.getType().equals(ClipboardEnum.NOTE) || clipboard.getType().equals(ClipboardEnum.COMPANY) || clipboard.getType().equals(ClipboardEnum.POSITION) ||
                        clipboard.getType().equals(ClipboardEnum.PHONE) || clipboard.getType().equals(ClipboardEnum.EMAIL) || clipboard.getType().equals(ClipboardEnum.ADDRESS)) {
                    //activity.findViewById(R.id.popupGlobalSearch).setVisibility(View.VISIBLE);
                    openGlobalPopup(clipboard);
                }
            }
        });

        popupInfo.findViewById(R.id.editPopupShareClip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = clipboards.get(position).getValueCopy().trim();

                text += "\n\n";
                text += "Data shared via http://Extime.pro\n";

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, text);
                activity.startActivity(Intent.createChooser(shareIntent, "Share"));
            }
        });
    }

    public void updateCount() {
        int co = 0;
        for (Clipboard cl : checkHashClip) {
            if (cl.getListClipboards() == null || cl.getListClipboards().isEmpty()) {
                co++;
            }
        }
        if (typeOfData.equals(MagicType.CLIPBOARD)) {
            if (co > 0) {
                ((TextView) activity.findViewById(R.id.textTopClipboard)).setText(co + " fields selected");
                ((TextView) activity.findViewById(R.id.textTopClipboard)).setTextSize(9);
                ((TextView) activity.findViewById(R.id.textTopClipboard)).requestLayout();
            } else
                activity.findViewById(R.id.textTopClipboard).setVisibility(View.INVISIBLE);
        } else {
            if (co > 0) {
                ((TextView) activity.findViewById(R.id.textTopHistory)).setText(co + " fields selected");
                ((TextView) activity.findViewById(R.id.textTopHistory)).setTextSize(9);
                ((TextView) activity.findViewById(R.id.textTopHistory)).requestLayout();
            } else
                activity.findViewById(R.id.textTopHistory).setVisibility(View.INVISIBLE);
        }
    }

    public void updateClipboards(ArrayList<Clipboard> list) {
       /* listContact = new ArrayList<>();
        checkContact = new ArrayList<>();
        for(int i = 0;i<list.size();i++) {
            listContact.add(null);
            checkContact.add(false);
        }*/

        //this.clipboards = list;
        this.clipboards = new ArrayList<>();
        this.clipboards.addAll(list);

        //checkHash = new ArrayList<>();

        //checkHashClip = new ArrayList<>();
        listOpen.clear();

        if (typeOfData.equals(MagicType.CLIPBOARD))
            ((TextView) activity.findViewById(R.id.statClipboard)).setText(getCountCLipboards() + " entries, " + getCountDetectedClipboards() + " detected");
        else
            ((TextView) activity.findViewById(R.id.statHistory)).setText(getCountCLipboards() + " entries, " + getCountDetectedClipboards() + " detected");

        notifyDataSetChanged();
    }

    public int getCountCLipboards() {
        int count = 0;
        for (int i = 0; i < clipboards.size(); i++) {
            if (clipboards.get(i).getListClipboards() == null || clipboards.get(i).getListClipboards().isEmpty())
                count++;
            else {
                if (!listOpen.contains(clipboards.get(i)))
                    count += clipboards.get(i).getListClipboards().size();
            }
        }
        return count;
    }

    public int getCountDetectedClipboards() {

        int count = 0;
        for (int i = 0; i < clipboards.size(); i++) {

            // if(clipboards.get(i).getListContactsSearch() != null)

            if ((clipboards.get(i).getListClipboards() == null || clipboards.get(i).getListClipboards().isEmpty()) && (clipboards.get(i).getListContactsSearch() != null && !clipboards.get(i).getListContactsSearch().isEmpty())) {

                count++;
            } else if (clipboards.get(i).getListClipboards() != null && !clipboards.get(i).getListClipboards().isEmpty() && !listOpen.contains(clipboards.get(i))) {

                for (Clipboard clipboard : clipboards.get(i).getListClipboards()) {

                    if (clipboard.getListContactsSearch() != null && !clipboard.getListContactsSearch().isEmpty()) {

                        count++;
                    }
                }
            }
        }
        return count;
    }

    @Override
    public ClipboardViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        mainView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_clipboard, viewGroup, false);
        vg = viewGroup;
        return new ClipboardViewHolder(mainView);
    }

    private String getInitials(Contact contact) {
        String initials = "";
        if (contact.getName() != null && !contact.getName().isEmpty()) {
            String names[] = contact.getName().split("\\s+");
            for (String namePart : names) {
                if (namePart != null && namePart.length() > 0)
                    initials += namePart.charAt(0);
            }
        }
        return initials.toUpperCase();
    }

    private String getInitialsByName(String name) {
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

    private void restartPopupChange() {
        ((ImageView) popupChangeType.findViewById(R.id.icnAddressType)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
        ((ImageView) popupChangeType.findViewById(R.id.icnBirthType)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
        ((ImageView) popupChangeType.findViewById(R.id.icnEmailType)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
        ((ImageView) popupChangeType.findViewById(R.id.icnProfileType)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
        ((ImageView) popupChangeType.findViewById(R.id.icnNoteType)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
        ((ImageView) popupChangeType.findViewById(R.id.icnPositionType)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
        ((ImageView) popupChangeType.findViewById(R.id.icnPhoneType)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
        ((ImageView) popupChangeType.findViewById(R.id.icnWebType)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
        ((ImageView) popupChangeType.findViewById(R.id.icnCompanyType)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
        ((ImageView) popupChangeType.findViewById(R.id.icnHashType)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
        ((ImageView) popupChangeType.findViewById(R.id.icnBioType)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));



        View Instagram = activity.getLayoutInflater().inflate(R.layout.social_insta_change, null);

        Drawable color = new ColorDrawable(Color.parseColor("#e2e5e8"));
        Drawable image = activity.getResources().getDrawable(R.drawable.icn_social_ints2);
        LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
        ((ImageView) Instagram.findViewById(R.id.inst_icon)).setImageDrawable(ld);

        ((FrameLayout) popupChangeType.findViewById(R.id.icnInstagramType)).addView(Instagram);



        Drawable color44 = new ColorDrawable(Color.parseColor("#e2e5e8"));
        Drawable image44 = activity.getResources().getDrawable(R.drawable.ic_youtube_white);
        LayerDrawable ld44 = new LayerDrawable(new Drawable[]{color44, image44});
        ((ImageView) popupChangeType.findViewById(R.id.icnYoutubeType)).setImageDrawable(ld44);


        Drawable color42 = new ColorDrawable(Color.parseColor("#e2e5e8"));
        Drawable image42 = activity.getResources().getDrawable(R.drawable.medium_white);
        LayerDrawable ld42 = new LayerDrawable(new Drawable[]{color42, image42});
        ((ImageView) popupChangeType.findViewById(R.id.icnMediumType)).setImageDrawable(ld42);





        ((TextView) popupChangeType.findViewById(R.id.typeAddressText)).setText("address");
        ((TextView) popupChangeType.findViewById(R.id.typeDateText)).setText("birthday");
        ((TextView) popupChangeType.findViewById(R.id.typeEmailText)).setText("email");
        ((TextView) popupChangeType.findViewById(R.id.typeNameText)).setText("name");
        ((TextView) popupChangeType.findViewById(R.id.typeNoteText)).setText("note");
        ((TextView) popupChangeType.findViewById(R.id.typePositionText)).setText("position");
        ((TextView) popupChangeType.findViewById(R.id.typePhoneText)).setText("phone");
        ((TextView) popupChangeType.findViewById(R.id.typeWebText)).setText("web");
        ((TextView) popupChangeType.findViewById(R.id.typeCompanyText)).setText("company");
        ((TextView) popupChangeType.findViewById(R.id.typeHashText)).setText("hashtag");
        ((TextView) popupChangeType.findViewById(R.id.typeBioText)).setText("bio/intro");

        ((TextView) popupChangeType.findViewById(R.id.typeAddressText)).setTypeface(Typeface.DEFAULT);
        ((TextView) popupChangeType.findViewById(R.id.typeDateText)).setTypeface(Typeface.DEFAULT);
        ((TextView) popupChangeType.findViewById(R.id.typeEmailText)).setTypeface(Typeface.DEFAULT);
        ((TextView) popupChangeType.findViewById(R.id.typeNameText)).setTypeface(Typeface.DEFAULT);
        ((TextView) popupChangeType.findViewById(R.id.typeNoteText)).setTypeface(Typeface.DEFAULT);
        ((TextView) popupChangeType.findViewById(R.id.typePositionText)).setTypeface(Typeface.DEFAULT);
        ((TextView) popupChangeType.findViewById(R.id.typePhoneText)).setTypeface(Typeface.DEFAULT);
        ((TextView) popupChangeType.findViewById(R.id.typeWebText)).setTypeface(Typeface.DEFAULT);
        ((TextView) popupChangeType.findViewById(R.id.typeCompanyText)).setTypeface(Typeface.DEFAULT);
        ((TextView) popupChangeType.findViewById(R.id.typeHashText)).setTypeface(Typeface.DEFAULT);
        ((TextView) popupChangeType.findViewById(R.id.typeBioText)).setTypeface(Typeface.DEFAULT);


        ((TextView) popupChangeType.findViewById(R.id.typeAddressText)).setTextColor(ContextCompat.getColor(mainView.getContext(), R.color.gray_textView));
        ((TextView) popupChangeType.findViewById(R.id.typeDateText)).setTextColor(ContextCompat.getColor(mainView.getContext(), R.color.gray_textView));
        ((TextView) popupChangeType.findViewById(R.id.typeEmailText)).setTextColor(ContextCompat.getColor(mainView.getContext(), R.color.gray_textView));
        ((TextView) popupChangeType.findViewById(R.id.typeNameText)).setTextColor(ContextCompat.getColor(mainView.getContext(), R.color.gray_textView));
        ((TextView) popupChangeType.findViewById(R.id.typeNoteText)).setTextColor(ContextCompat.getColor(mainView.getContext(), R.color.gray_textView));
        ((TextView) popupChangeType.findViewById(R.id.typePositionText)).setTextColor(ContextCompat.getColor(mainView.getContext(), R.color.gray_textView));
        ((TextView) popupChangeType.findViewById(R.id.typePhoneText)).setTextColor(ContextCompat.getColor(mainView.getContext(), R.color.gray_textView));
        ((TextView) popupChangeType.findViewById(R.id.typeWebText)).setTextColor(ContextCompat.getColor(mainView.getContext(), R.color.gray_textView));
        ((TextView) popupChangeType.findViewById(R.id.typeCompanyText)).setTextColor(ContextCompat.getColor(mainView.getContext(), R.color.gray_textView));
        ((TextView) popupChangeType.findViewById(R.id.typeHashText)).setTextColor(ContextCompat.getColor(mainView.getContext(), R.color.gray_textView));
        ((TextView) popupChangeType.findViewById(R.id.typeBioText)).setTextColor(ContextCompat.getColor(mainView.getContext(), R.color.gray_textView));

        ((TextView) popupChangeType.findViewById(R.id.currentAddress)).setVisibility(View.GONE);
        ((TextView) popupChangeType.findViewById(R.id.currentCompany)).setVisibility(View.GONE);
        ((TextView) popupChangeType.findViewById(R.id.currentDate)).setVisibility(View.GONE);
        ((TextView) popupChangeType.findViewById(R.id.currentEmail)).setVisibility(View.GONE);
        ((TextView) popupChangeType.findViewById(R.id.currentName)).setVisibility(View.GONE);
        ((TextView) popupChangeType.findViewById(R.id.currentNote)).setVisibility(View.GONE);
        ((TextView) popupChangeType.findViewById(R.id.currentPosition)).setVisibility(View.GONE);
        ((TextView) popupChangeType.findViewById(R.id.currentPhone)).setVisibility(View.GONE);
        ((TextView) popupChangeType.findViewById(R.id.currentWeb)).setVisibility(View.GONE);
        ((TextView) popupChangeType.findViewById(R.id.currentCompany)).setVisibility(View.GONE);
        ((TextView) popupChangeType.findViewById(R.id.currentHash)).setVisibility(View.GONE);
        ((TextView) popupChangeType.findViewById(R.id.currentBio)).setVisibility(View.GONE);

        ((TextView) popupChangeType.findViewById(R.id.currentAddress)).setTextColor(ContextCompat.getColor(mainView.getContext(), R.color.gray_textView));
        ((TextView) popupChangeType.findViewById(R.id.currentCompany)).setTextColor(ContextCompat.getColor(mainView.getContext(), R.color.gray_textView));
        ((TextView) popupChangeType.findViewById(R.id.currentDate)).setTextColor(ContextCompat.getColor(mainView.getContext(), R.color.gray_textView));
        ((TextView) popupChangeType.findViewById(R.id.currentEmail)).setTextColor(ContextCompat.getColor(mainView.getContext(), R.color.gray_textView));
        ((TextView) popupChangeType.findViewById(R.id.currentNote)).setTextColor(ContextCompat.getColor(mainView.getContext(), R.color.gray_textView));
        ((TextView) popupChangeType.findViewById(R.id.currentPosition)).setTextColor(ContextCompat.getColor(mainView.getContext(), R.color.gray_textView));
        ((TextView) popupChangeType.findViewById(R.id.currentPhone)).setTextColor(ContextCompat.getColor(mainView.getContext(), R.color.gray_textView));
        ((TextView) popupChangeType.findViewById(R.id.currentWeb)).setTextColor(ContextCompat.getColor(mainView.getContext(), R.color.gray_textView));
        ((TextView) popupChangeType.findViewById(R.id.currentCompany)).setTextColor(ContextCompat.getColor(mainView.getContext(), R.color.gray_textView));
        ((TextView) popupChangeType.findViewById(R.id.currentHash)).setTextColor(ContextCompat.getColor(mainView.getContext(), R.color.gray_textView));
        ((TextView) popupChangeType.findViewById(R.id.currentBio)).setTextColor(ContextCompat.getColor(mainView.getContext(), R.color.gray_textView));

        ((ImageView) popupChangeType.findViewById(R.id.addressTick)).setVisibility(View.GONE);
        ((ImageView) popupChangeType.findViewById(R.id.noteTick)).setVisibility(View.GONE);
        ((ImageView) popupChangeType.findViewById(R.id.companyTick)).setVisibility(View.GONE);
        ((ImageView) popupChangeType.findViewById(R.id.positionTick)).setVisibility(View.GONE);
        ((ImageView) popupChangeType.findViewById(R.id.birthTick)).setVisibility(View.GONE);
        ((ImageView) popupChangeType.findViewById(R.id.phoneTick)).setVisibility(View.GONE);
        ((ImageView) popupChangeType.findViewById(R.id.emailTick)).setVisibility(View.GONE);
        ((ImageView) popupChangeType.findViewById(R.id.webTick)).setVisibility(View.GONE);
        ((ImageView) popupChangeType.findViewById(R.id.profileTick)).setVisibility(View.GONE);
        ((ImageView) popupChangeType.findViewById(R.id.hashTick)).setVisibility(View.GONE);

        ((ImageView) popupChangeType.findViewById(R.id.bioTick)).setVisibility(View.GONE);
        ((ImageView) popupChangeType.findViewById(R.id.followTick)).setVisibility(View.GONE);

        ((ImageView) popupChangeType.findViewById(R.id.facebookTick)).setVisibility(View.GONE);
        ((ImageView) popupChangeType.findViewById(R.id.twitterTick)).setVisibility(View.GONE);
        ((ImageView) popupChangeType.findViewById(R.id.linkedInTick)).setVisibility(View.GONE);
        ((ImageView) popupChangeType.findViewById(R.id.whatsAppTick)).setVisibility(View.GONE);
        ((ImageView) popupChangeType.findViewById(R.id.skypeTick)).setVisibility(View.GONE);
        ((ImageView) popupChangeType.findViewById(R.id.instagramTick)).setVisibility(View.GONE);
        ((ImageView) popupChangeType.findViewById(R.id.youTubeTick)).setVisibility(View.GONE);
        ((ImageView) popupChangeType.findViewById(R.id.mediumTick)).setVisibility(View.GONE);
        ((ImageView) popupChangeType.findViewById(R.id.viberTick)).setVisibility(View.GONE);
        ((ImageView) popupChangeType.findViewById(R.id.telegramTick)).setVisibility(View.GONE);




        /*((TextView) popupChangeType.findViewById(R.id.typeValueAddress)).setVisibility(View.GONE);
        ((TextView) popupChangeType.findViewById(R.id.typeValueBirth)).setVisibility(View.GONE);
        ((TextView) popupChangeType.findViewById(R.id.typeValueEmail)).setVisibility(View.GONE);
        ((TextView) popupChangeType.findViewById(R.id.typeValueName)).setVisibility(View.GONE);
        ((TextView) popupChangeType.findViewById(R.id.typeValueNote)).setVisibility(View.GONE);
        ((TextView) popupChangeType.findViewById(R.id.typeValuePosition)).setVisibility(View.GONE);
        ((TextView) popupChangeType.findViewById(R.id.typeValuePhone)).setVisibility(View.GONE);
        ((TextView) popupChangeType.findViewById(R.id.typeValueWeb)).setVisibility(View.GONE);*/
    }


    public void changeTypeUI(ClipboardEnum en, ClipboardEnum[] before) {
        if (en.equals(ClipboardEnum.ADDRESS)) {
            ((ImageView) popupChangeType.findViewById(R.id.icnAddressType)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
            //((TextView) popupChangeType.findViewById(R.id.currentAddress)).setVisibility(View.GONE);
        } else if (en.equals(ClipboardEnum.DATEOFBIRTH)) {
            ((ImageView) popupChangeType.findViewById(R.id.icnBirthType)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
            //((TextView) popupChangeType.findViewById(R.id.currentDate)).setVisibility(View.GONE);
        } else if (en.equals(ClipboardEnum.EMAIL)) {
            ((ImageView) popupChangeType.findViewById(R.id.icnEmailType)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
            //((TextView) popupChangeType.findViewById(R.id.currentEmail)).setVisibility(View.GONE);
        } else if (en.equals(ClipboardEnum.NAME)) {
            ((ImageView) popupChangeType.findViewById(R.id.icnProfileType)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
            //((TextView) popupChangeType.findViewById(R.id.currentName)).setVisibility(View.GONE);
        } else if (en.equals(ClipboardEnum.NOTE)) {
            ((ImageView) popupChangeType.findViewById(R.id.icnNoteType)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
            //((TextView) popupChangeType.findViewById(R.id.currentNote)).setVisibility(View.GONE);
        } else if (en.equals(ClipboardEnum.POSITION)) {
            ((ImageView) popupChangeType.findViewById(R.id.icnPositionType)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
            //((TextView) popupChangeType.findViewById(R.id.currentPosition)).setVisibility(View.GONE);
        } else if (en.equals(ClipboardEnum.PHONE)) {
            ((ImageView) popupChangeType.findViewById(R.id.icnPhoneType)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
            //((TextView) popupChangeType.findViewById(R.id.currentPhone)).setVisibility(View.GONE);
        } else if (en.equals(ClipboardEnum.WEB)) {
            ((ImageView) popupChangeType.findViewById(R.id.icnWebType)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
            //((TextView) popupChangeType.findViewById(R.id.currentWeb)).setVisibility(View.GONE);
        } else if (en.equals(ClipboardEnum.COMPANY)) {
            ((ImageView) popupChangeType.findViewById(R.id.icnCompanyType)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
        } else if (en.equals(ClipboardEnum.HASHTAG)) {
            ((ImageView) popupChangeType.findViewById(R.id.icnHashType)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
        }else if (en.equals(ClipboardEnum.BIO_INTRO)) {
            ((ImageView) popupChangeType.findViewById(R.id.icnBioType)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
        }
        if (before[0] == null) return;

        if (before[0].equals(ClipboardEnum.ADDRESS)) {
            ((ImageView) popupChangeType.findViewById(R.id.icnAddressType)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
            ((TextView) popupChangeType.findViewById(R.id.typeAddressText)).setText("address");
            ((TextView) popupChangeType.findViewById(R.id.typeAddressText)).setTextColor(ContextCompat.getColor(mainView.getContext(), R.color.gray_textView));
            ((TextView) popupChangeType.findViewById(R.id.currentAddress)).setVisibility(View.GONE);
        }else if (before[0].equals(ClipboardEnum.BIO_INTRO)) {
            ((ImageView) popupChangeType.findViewById(R.id.icnBioType)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
            ((TextView) popupChangeType.findViewById(R.id.typeBioText)).setText("bio/intro");
            ((TextView) popupChangeType.findViewById(R.id.typeBioText)).setTextColor(ContextCompat.getColor(mainView.getContext(), R.color.gray_textView));
            ((TextView) popupChangeType.findViewById(R.id.currentBio)).setVisibility(View.GONE);
        } else if (before[0].equals(ClipboardEnum.DATEOFBIRTH)) {
            ((ImageView) popupChangeType.findViewById(R.id.icnBirthType)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
            ((TextView) popupChangeType.findViewById(R.id.typeDateText)).setText("birthday");
            ((TextView) popupChangeType.findViewById(R.id.typeDateText)).setTextColor(ContextCompat.getColor(mainView.getContext(), R.color.gray_textView));
            ((TextView) popupChangeType.findViewById(R.id.currentDate)).setVisibility(View.GONE);
        } else if (before[0].equals(ClipboardEnum.EMAIL)) {
            ((ImageView) popupChangeType.findViewById(R.id.icnEmailType)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
            ((TextView) popupChangeType.findViewById(R.id.typeEmailText)).setText("email");
            ((TextView) popupChangeType.findViewById(R.id.typeEmailText)).setTextColor(ContextCompat.getColor(mainView.getContext(), R.color.gray_textView));
            ((TextView) popupChangeType.findViewById(R.id.currentEmail)).setVisibility(View.GONE);
        } else if (before[0].equals(ClipboardEnum.NAME)) {
            ((ImageView) popupChangeType.findViewById(R.id.icnProfileType)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
            ((TextView) popupChangeType.findViewById(R.id.typeNameText)).setText("name");
            ((TextView) popupChangeType.findViewById(R.id.typeNameText)).setTextColor(ContextCompat.getColor(mainView.getContext(), R.color.gray_textView));
            ((TextView) popupChangeType.findViewById(R.id.currentName)).setVisibility(View.GONE);
        } else if (before[0].equals(ClipboardEnum.NOTE)) {
            ((ImageView) popupChangeType.findViewById(R.id.icnNoteType)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
            ((TextView) popupChangeType.findViewById(R.id.typeNoteText)).setText("note");
            ((TextView) popupChangeType.findViewById(R.id.typeNoteText)).setTextColor(ContextCompat.getColor(mainView.getContext(), R.color.gray_textView));
            ((TextView) popupChangeType.findViewById(R.id.currentNote)).setVisibility(View.GONE);
        } else if (before[0].equals(ClipboardEnum.POSITION)) {
            ((ImageView) popupChangeType.findViewById(R.id.icnPositionType)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
            ((TextView) popupChangeType.findViewById(R.id.typePositionText)).setText("position");
            ((TextView) popupChangeType.findViewById(R.id.typePositionText)).setTextColor(ContextCompat.getColor(mainView.getContext(), R.color.gray_textView));
            ((TextView) popupChangeType.findViewById(R.id.currentPosition)).setVisibility(View.GONE);
        } else if (before[0].equals(ClipboardEnum.PHONE)) {
            ((ImageView) popupChangeType.findViewById(R.id.icnPhoneType)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
            ((TextView) popupChangeType.findViewById(R.id.typePhoneText)).setText("phone");
            ((TextView) popupChangeType.findViewById(R.id.typePhoneText)).setTextColor(ContextCompat.getColor(mainView.getContext(), R.color.gray_textView));
            ((TextView) popupChangeType.findViewById(R.id.currentPhone)).setVisibility(View.GONE);
        } else if (before[0].equals(ClipboardEnum.WEB)) {
            ((ImageView) popupChangeType.findViewById(R.id.icnWebType)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
            ((TextView) popupChangeType.findViewById(R.id.typeWebText)).setText("web");
            ((TextView) popupChangeType.findViewById(R.id.typeWebText)).setTextColor(ContextCompat.getColor(mainView.getContext(), R.color.gray_textView));
            ((TextView) popupChangeType.findViewById(R.id.currentWeb)).setVisibility(View.GONE);
        } else if (before[0].equals(ClipboardEnum.COMPANY)) {
            ((ImageView) popupChangeType.findViewById(R.id.icnCompanyType)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
            ((TextView) popupChangeType.findViewById(R.id.typeCompanyText)).setText("company");
            ((TextView) popupChangeType.findViewById(R.id.typeCompanyText)).setTextColor(ContextCompat.getColor(mainView.getContext(), R.color.gray_textView));
            ((TextView) popupChangeType.findViewById(R.id.currentCompany)).setVisibility(View.GONE);
        } else if (before[0].equals(ClipboardEnum.HASHTAG)) {
            ((ImageView) popupChangeType.findViewById(R.id.icnHashType)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
            ((TextView) popupChangeType.findViewById(R.id.typeHashText)).setText("hashtag");
            ((TextView) popupChangeType.findViewById(R.id.typeHashText)).setTextColor(ContextCompat.getColor(mainView.getContext(), R.color.gray_textView));
            ((TextView) popupChangeType.findViewById(R.id.currentHash)).setVisibility(View.GONE);
        }
    }

    public boolean allBind = true;


    @Override
    public boolean onFailedToRecycleView(ClipboardViewHolder holder) {

        return super.onFailedToRecycleView(holder);
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(final ClipboardViewHolder holder, int position) {
        Clipboard clipboard = clipboards.get(position);
        holder.clipboardValue.setTypeface(null, Typeface.NORMAL);

        if (typeOfData.equals(MagicType.CLIPBOARD)) {
            holder.itemView.findViewById(R.id.frame_plane_history).setVisibility(View.GONE);
        } else {
            holder.itemView.findViewById(R.id.frame_plane_history).setVisibility(View.VISIBLE);

            holder.itemView.findViewById(R.id.frame_plane_history).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((EditText) contactsFragment.getActivity().findViewById(R.id.magic_edit_text)).setText(clipboard.getValueCopy());

                    contactsFragment.allSearch = true;
                    ((ContactAdapter) contactsFragment.containerContacts.getAdapter()).searchByStrAllData(((EditText) contactsFragment.getActivity().findViewById(R.id.magic_edit_text)).getText().toString().trim());

                    EventBus.getDefault().post(new HideHistoryPopup());
                }
            });

        }

        if (clipboard.getListClipboards() == null || clipboard.getListClipboards().isEmpty()) {

            holder.imageTypeClipboard.setVisibility(View.VISIBLE);
            holder.frameImageNum.setVisibility(View.VISIBLE);


            holder.imageTypeClipboardGroup.setVisibility(View.GONE);
            holder.frameCircle.setVisibility(View.GONE);
            holder.numberList.setVisibility(View.GONE);


            //Glide.with(mainView.getContext()).load(clipboard.getImageTypeClipboard()).apply(new RequestOptions().override(30,30).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false)).into(holder.imageTypeClipboard);
            holder.imageTypeClipboard.setImageURI(null);
            holder.imageTypeClipboard.setImageURI(Uri.parse(clipboard.getImageTypeClipboard()));
            holder.arrow.setVisibility(View.GONE);
            holder.numberGroup.setVisibility(View.GONE);

            String textOut = clipboard.getValueCopy().replaceAll("\n", " ");
            holder.clipboardValue.setText(textOut);
            if (listOpen != null) {
                for (Clipboard clipboard1 : listOpen) {
                    if (clipboard1.getListClipboards().contains(clipboard)) {
                        holder.numberGroup.setVisibility(View.VISIBLE);

                        holder.clipboardValue.setText(" " + textOut);

                        holder.numberGroup.setText(String.valueOf(clipboard1.getListClipboards().indexOf(clipboard) + 1));
                        if (clipboard1.getListClipboards().size() == clipboard1.getListClipboards().indexOf(clipboard) + 1 && /*clipboard1.getType() != ClipboardEnum.GROUP*/ (!clipboard1.getType().equals(ClipboardEnum.FACEBOOK) && !clipboard1.getType().equals(ClipboardEnum.LINKEDIN) &&
                                !clipboard1.getType().equals(ClipboardEnum.INSTAGRAM) && !clipboard1.getType().equals(ClipboardEnum.VK) && !clipboard1.getType().equals(ClipboardEnum.YOUTUBE) && !clipboard1.getType().equals(ClipboardEnum.TWITTER) &&
                                !clipboard1.getType().equals(ClipboardEnum.MEDIUM) && !clipboard1.getType().equals(ClipboardEnum.WEB) && !clipboard1.getType().equals(ClipboardEnum.GITHUB))) {

                            holder.clipboardValue.setTypeface(null, Typeface.ITALIC);
                        }
                        break;
                    }
                }
            }
        } else {

            String textOut = clipboard.getValueCopy().replaceAll("\n", " ");

            holder.clipboardValue.setText(textOut);

            if (clipboard.getType().equals(ClipboardEnum.FACEBOOK) || clipboard.getType().equals(ClipboardEnum.LINKEDIN) || clipboard.getType().equals(ClipboardEnum.INSTAGRAM) ||
                    clipboard.getType().equals(ClipboardEnum.VK) || clipboard.getType().equals(ClipboardEnum.TWITTER) || clipboard.getType().equals(ClipboardEnum.YOUTUBE) || clipboard.getType().equals(ClipboardEnum.MEDIUM) ||
                    clipboard.getType().equals(ClipboardEnum.WEB) || clipboard.getType().equals(ClipboardEnum.GITHUB)) {

                holder.frameImageNum.setVisibility(View.VISIBLE);
                holder.imageTypeClipboard.setVisibility(View.VISIBLE);

                holder.imageTypeClipboardGroup.setVisibility(View.GONE);
                holder.numberList.setVisibility(View.GONE);
                holder.frameCircle.setVisibility(View.GONE);
                holder.numberGroup.setVisibility(View.GONE);
                //holder.circleImageView.setVisibility(View.GONE);

                holder.imageTypeClipboard.setImageURI(null);
                holder.imageTypeClipboard.setImageURI(Uri.parse(clipboard.getImageTypeClipboard()));
            } else {

                holder.imageTypeClipboard.setVisibility(View.GONE);
                holder.frameImageNum.setVisibility(View.GONE);

                holder.imageTypeClipboardGroup.setVisibility(View.VISIBLE);
                holder.numberList.setVisibility(View.VISIBLE);
                holder.frameCircle.setVisibility(View.VISIBLE);
                holder.numberList.setText(String.valueOf(clipboard.getListClipboards().size()));
            }

            holder.arrow.setVisibility(View.VISIBLE);
        }

        holder.frameCircleSearch.setVisibility(View.GONE);
        holder.frameContactList1.setVisibility(View.GONE);
        holder.frameContactList2.setVisibility(View.GONE);
        holder.frameContactList_Group.setVisibility(View.GONE);
        holder.groupNumberClipboard_extracted.setVisibility(View.GONE);


        //String value = clipboard.getValueCopy().replaceAll("[\\t\\n\\r]+", " ");
       /* String textOut = clipboard.getValueCopy().replaceAll("\n", " ");

        holder.clipboardValue.setText(textOut);*/

        //final boolean[] clickProfile = {false};
        //Integer integer = new Integer(position);
        if (checkHashClip.contains(clipboard)) {
            holder.checkBox.setChecked(true);
        } else {
            holder.checkBox.setChecked(false);
        }

        if (!allBind) return;

        holder.frameInageIconClipboard.setVisibility(View.VISIBLE);

        final boolean[] cliclArr = {false};

        //=====================
        final ContactDataClipboard[] c = {null};
        //LoadContact loadContact = new LoadContact(holder, c[0], clipboard);
        //==


        if ((clipboard.getListClipboards() == null || clipboard.getListClipboards().isEmpty())) {


            if (clipboard.getListContactsSearch() == null) {
                holder.circleImageViewSearch.setVisibility(View.VISIBLE);
            } else {
                holder.circleImageViewSearch.setVisibility(View.GONE);

                if (listOpen != null) {
                    for (Clipboard clipboard1 : listOpen) {
                        if (clipboard1.getListClipboards().contains(clipboard)) {
                                        /*holder.numberGroup.setVisibility(View.VISIBLE);
                                        holder.numberGroup.setText(String.valueOf(clipboard1.getListClipboards().indexOf(clipboard) + 1));
                                        if (clipboard1.getListClipboards().size() == clipboard1.getListClipboards().indexOf(clipboard) + 1) {
                                            holder.clipboardValue.setTypeface(null, Typeface.ITALIC);
                                        }*/

                            r.post(new Runnable() {
                                @Override
                                public void run() {
                                    notifyItemChanged(clipboards.indexOf(clipboard1));
                                }
                            });


                            break;
                        }
                    }
                }


            }

            if (clipboard.getType().equals(ClipboardEnum.NOTE)) {
                holder.clipboardValue.setTextColor(activity.getResources().getColor(R.color.black));
            } else {
                holder.clipboardValue.setTextColor(activity.getResources().getColor(R.color.colorPrimaryDark));
            }


            holder.circleImageView.setVisibility(View.GONE);
            holder.imageCompany.setVisibility(View.GONE);

        } else {


            if (clipboard.getType().equals(ClipboardEnum.FACEBOOK) || clipboard.getType().equals(ClipboardEnum.LINKEDIN) || clipboard.getType().equals(ClipboardEnum.INSTAGRAM) ||
                    clipboard.getType().equals(ClipboardEnum.VK) || clipboard.getType().equals(ClipboardEnum.TWITTER) || clipboard.getType().equals(ClipboardEnum.YOUTUBE) || clipboard.getType().equals(ClipboardEnum.MEDIUM) ||
                    clipboard.getType().equals(ClipboardEnum.WEB) || clipboard.getType().equals(ClipboardEnum.GITHUB)) {

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                        //holder.frameContactList_Group.setVisibility(View.VISIBLE);
                        //holder.countGroup.setText(String.valueOf(clipboard.getListClipboards().size()));
                        holder.groupNumberClipboard_extracted.setVisibility(View.VISIBLE);
                        holder.groupNumberClipboard_extracted.setText(String.valueOf(clipboard.getListClipboards().size()));


                    }
                });

            } else {

                //=======================================================  NEW

                ContactDataClipboard firstContact = null;
                ContactDataClipboard secondContact = null;
                final int[] countContactGONE = {0};
                ArrayList<Long> list_Id = new ArrayList<>();


                for (Clipboard clip : clipboard.getListClipboards()) {

                    //synk in close list


                    /*try {
                        if (clip.getListContactsSearch() == null || clip.getListContactsSearch().isEmpty() || clip.getListContactsSearch().size() == 0 && (clip.getListClipboards() == null || clip.getListClipboards().isEmpty())) {

                            Contact ct = ContactCacheService.find2(clip.getValueCopy(), clip.getType(), activity);

                            if (ct != null) {
                                boolean com = true;
                                if (ct.listOfContacts != null && !ct.listOfContacts.isEmpty())
                                    com = false;

                                c[0] = new ContactDataClipboard(ct.getId(), ct.getName(), ct.getPhotoURL(), ct.color, com);

                            } else c[0] = null;

                            if (c[0] != null) {
                                clipboard.addContactToListSearch(c[0]);
                            }
                        } else if (clip.getListClipboards() == null || clip.getListClipboards().isEmpty()) {

                            if (!ContactCacheService.checkExistConatctFromClipboard(clip.getListContactsSearch().get(0).getId(), clip.getValueCopy(), clip.getType())) {

                                Contact ct = ContactCacheService.find2(clip.getValueCopy(), clip.getType(), activity);

                                if (ct != null) {
                                    boolean com = true;
                                    if (ct.listOfContacts != null && !ct.listOfContacts.isEmpty())
                                        com = false;

                                    c[0] = new ContactDataClipboard(ct.getId(), ct.getName(), ct.getPhotoURL(), ct.color, com);
                                } else c[0] = null;

                                if (c[0] != null) {
                                    clip.setFirstClipsToSearch(c[0]);
                                } else {
                                    try {
                                        clip.removeConatctFromListSearchBuId(clip.getListContactsSearch().get(0).getId());
                                    } catch (Exception e) {
                                        clip.getListContactsSearch().remove(0);
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/


                    //================================================================


                    if (clip.getListContactsSearch() != null && clip.getListContactsSearch().size() > 0) {
                        if (firstContact == null) {
                            firstContact = clip.getListContactsSearch().get(0);
                            list_Id.add(firstContact.getId());
                            //add photo
                            ContactDataClipboard finalFirstContact = firstContact;
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    try {


                                        holder.frameContactList1.setVisibility(View.VISIBLE);
                                        ((CircleImageView) holder.frameContactList1.findViewById(R.id.profileIconFirst)).setImageURI(null);

                                        ((TextView) holder.frameContactList1.findViewById(R.id.profileIconInitialsFirst)).setVisibility(View.GONE);

                                        if (finalFirstContact.getPhotoURL() != null)
                                            ((CircleImageView) holder.frameContactList1.findViewById(R.id.profileIconFirst)).setImageURI(Uri.parse(finalFirstContact.getPhotoURL()));

                                        if (finalFirstContact.getPhotoURL() == null) {
                                            GradientDrawable circle = (GradientDrawable) ContextCompat.getDrawable(activity, R.drawable.blue_circle).mutate();
                                            circle.setColor(finalFirstContact.color);
                                            ((CircleImageView) holder.frameContactList1.findViewById(R.id.profileIconFirst)).setBackground(circle);
                                            ((CircleImageView) holder.frameContactList1.findViewById(R.id.profileIconFirst)).setImageDrawable(null);
                                            String initials = getInitialsByName(finalFirstContact.getName());

                                            holder.frameContactList1.findViewById(R.id.profileIconInitialsFirst).setVisibility(View.VISIBLE);
                                            ((TextView) holder.frameContactList1.findViewById(R.id.profileIconInitialsFirst)).setText(initials);

                                            if (!finalFirstContact.contact) {

                                                holder.frameContactList1.findViewById(R.id.profileIconFirst).setVisibility(View.GONE);
                                                holder.frameContactList1.findViewById(R.id.companyPhotoIconFirst).setVisibility(View.VISIBLE);
                                                holder.frameContactList1.findViewById(R.id.companyPhotoIconFirst).setBackgroundColor(finalFirstContact.color);
                                            } else {
                                                holder.frameContactList1.findViewById(R.id.companyPhotoIconFirst).setVisibility(View.GONE);
                                                holder.frameContactList1.findViewById(R.id.profileIconFirst).setVisibility(View.VISIBLE);
                                            }
                                        }
                                    } catch (IllegalStateException e) {

                                        clip.getListContactsSearch().remove(0);
                                        holder.frameContactList1.setVisibility(View.GONE);
                                        if (typeOfData.equals(MagicType.CLIPBOARD))
                                            ((TextView) activity.findViewById(R.id.statClipboard)).setText(getCountCLipboards() + " entries, " + getCountDetectedClipboards() + " detected");
                                        else
                                            ((TextView) activity.findViewById(R.id.statHistory)).setText(getCountCLipboards() + " entries, " + getCountDetectedClipboards() + " detected");

                                    }


                                }
                            });
                            //=====

                            ContactDataClipboard finalFirstContact1 = firstContact;
                            holder.frameContactList1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    contactsFragment.closeOtherPopup();
                                    if (!ProfileFragment.profile) {
                                        if (finalFirstContact1.contact) {
                                            Contact contact = ContactCacheService.getContactById(finalFirstContact1.getId());
                                            if (contact == null || !contact.isValid()) return;
                                            contactsFragment.contactAdapter.selectedContactId = String.valueOf(contact.getId());
                                            contactsFragment.showProfilePopUp(contact);

                                            if (ClibpboardAdapter.checkSelectClips) {

                                                if (contactsFragment.getActivity() == null)
                                                    ((TextView) activity.findViewById(R.id.updateContactClipboard)).setTextColor(contactsFragment.getResources().getColor(R.color.colorPrimary));
                                                else
                                                    ((TextView) contactsFragment.getActivity().findViewById(R.id.updateContactClipboard)).setTextColor(contactsFragment.getResources().getColor(R.color.colorPrimary));
                                            }

                                        } else {

                                            Contact contact = ContactCacheService.getContactById(finalFirstContact1.getId());
                                            if (contact != null && contact.isValid()) {
                                                contactsFragment.contactAdapter.selectedContactId = contact.getName();
                                                contactsFragment.showCompanyPopup(contact);

                                                if (ClibpboardAdapter.checkSelectClips) {

                                                    if (contactsFragment.getActivity() == null)
                                                        ((TextView) activity.findViewById(R.id.updateContactClipboard)).setTextColor(contactsFragment.getResources().getColor(R.color.colorPrimary));
                                                    else
                                                        ((TextView) contactsFragment.getActivity().findViewById(R.id.updateContactClipboard)).setTextColor(contactsFragment.getResources().getColor(R.color.colorPrimary));
                                                }
                                            }

                                        }
                                    } else {
                                        if (finalFirstContact1.contact) {
                                            EventBus.getDefault().post(new PopupInProfile(finalFirstContact1.getId()));
                                        } else
                                            EventBus.getDefault().post(new PopupCompanyInProfile(finalFirstContact1.getId()));
                                    }

                                }
                            });


                        } else if (secondContact == null) {
                            if (!list_Id.contains(clip.getListContactsSearch().get(0).getId())) {
                                secondContact = clip.getListContactsSearch().get(0);
                                list_Id.add(secondContact.getId());
                                //add photo
                                ContactDataClipboard finalSecondContact = secondContact;

                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        try {


                                            holder.frameContactList2.setVisibility(View.VISIBLE);
                                            ((CircleImageView) holder.frameContactList2.findViewById(R.id.profileIconSecond)).setImageURI(null);

                                            ((TextView) holder.frameContactList2.findViewById(R.id.profileIconInitialsSecond)).setVisibility(View.GONE);

                                            if (finalSecondContact.getPhotoURL() != null)
                                                ((CircleImageView) holder.frameContactList2.findViewById(R.id.profileIconSecond)).setImageURI(Uri.parse(finalSecondContact.getPhotoURL()));

                                            if (finalSecondContact.getPhotoURL() == null) {
                                                GradientDrawable circle = (GradientDrawable) ContextCompat.getDrawable(activity, R.drawable.blue_circle).mutate();
                                                circle.setColor(finalSecondContact.color);

                                                ((CircleImageView) holder.frameContactList2.findViewById(R.id.profileIconSecond)).setBackground(circle);
                                                ((CircleImageView) holder.frameContactList2.findViewById(R.id.profileIconSecond)).setImageDrawable(null);
                                                String initials = getInitialsByName(finalSecondContact.getName());

                                                holder.frameContactList2.findViewById(R.id.profileIconInitialsSecond).setVisibility(View.VISIBLE);
                                                ((TextView) holder.frameContactList2.findViewById(R.id.profileIconInitialsSecond)).setText(initials);

                                                if (!finalSecondContact.contact) {

                                                    holder.frameContactList2.findViewById(R.id.profileIconSecond).setVisibility(View.GONE);
                                                    holder.frameContactList2.findViewById(R.id.companyPhotoIconSecond).setVisibility(View.VISIBLE);
                                                    holder.frameContactList2.findViewById(R.id.companyPhotoIconSecond).setBackgroundColor(finalSecondContact.color);
                                                } else {
                                                    holder.frameContactList2.findViewById(R.id.companyPhotoIconSecond).setVisibility(View.GONE);
                                                    holder.frameContactList2.findViewById(R.id.profileIconSecond).setVisibility(View.VISIBLE);
                                                }
                                            }
                                        } catch (IllegalStateException e) {

                                            clip.getListContactsSearch().remove(0);
                                            holder.frameContactList2.setVisibility(View.GONE);
                                            if (typeOfData.equals(MagicType.CLIPBOARD))
                                                ((TextView) activity.findViewById(R.id.statClipboard)).setText(getCountCLipboards() + " entries, " + getCountDetectedClipboards() + " detected");
                                            else
                                                ((TextView) activity.findViewById(R.id.statHistory)).setText(getCountCLipboards() + " entries, " + getCountDetectedClipboards() + " detected");

                                        }


                                    }
                                });
                                //=====

                                ContactDataClipboard finalSecondContact1 = secondContact;
                                holder.frameContactList2.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {


                                        contactsFragment.closeOtherPopup();
                                        if (!ProfileFragment.profile) {
                                            if (finalSecondContact1.contact) {
                                                Contact contact = ContactCacheService.getContactById(finalSecondContact1.getId());
                                                if (contact == null || !contact.isValid()) return;

                                                contactsFragment.contactAdapter.selectedContactId = String.valueOf(contact.getId());
                                                contactsFragment.showProfilePopUp(contact);

                                                if (ClibpboardAdapter.checkSelectClips) {

                                                    if (contactsFragment.getActivity() == null)
                                                        ((TextView) activity.findViewById(R.id.updateContactClipboard)).setTextColor(contactsFragment.getResources().getColor(R.color.colorPrimary));
                                                    else
                                                        ((TextView) contactsFragment.getActivity().findViewById(R.id.updateContactClipboard)).setTextColor(contactsFragment.getResources().getColor(R.color.colorPrimary));
                                                }

                                            } else {

                                                Contact contact = ContactCacheService.getContactById(finalSecondContact1.getId());

                                                if (contact != null && contact.isValid()) {
                                                    contactsFragment.contactAdapter.selectedContactId = contact.getName();
                                                    contactsFragment.showCompanyPopup(contact);

                                                    if (ClibpboardAdapter.checkSelectClips) {
                                                        ((TextView) activity.findViewById(R.id.updateContactClipboard)).setTextColor(contactsFragment.getResources().getColor(R.color.colorPrimary));
                                                    }
                                                }

                                            }
                                        } else {
                                            if (finalSecondContact1.contact) {
                                                EventBus.getDefault().post(new PopupInProfile(finalSecondContact1.getId()));
                                            } else
                                                EventBus.getDefault().post(new PopupCompanyInProfile(finalSecondContact1.getId()));
                                        }

                                    }
                                });

                            }
                        } else {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    if (!list_Id.contains(clip.getListContactsSearch().get(0).getId())) {
                                        if (countContactGONE[0] == 0) {
                                            countContactGONE[0]++;
                                            holder.frameContactList_Group.setVisibility(View.VISIBLE);
                                            holder.countGroup.setText(String.valueOf(countContactGONE[0]));
                                        } else {
                                            countContactGONE[0]++;
                                            holder.countGroup.setText(String.valueOf(countContactGONE[0]));
                                        }
                                    }

                                }
                            });
                        }


                    }
                }

            }
            //======================================================= END


            //holder.frameInageIconClipboard.setVisibility(View.VISIBLE);
            holder.clipboardValue.setTextColor(activity.getResources().getColor(R.color.colorPrimaryDark));
            holder.circleImageView.setVisibility(View.GONE);
            holder.imageCompany.setVisibility(View.GONE);
            holder.circleImageViewSearch.setVisibility(View.GONE);
            holder.arrow.setVisibility(View.VISIBLE);
            if (listOpen.contains(clipboard)) {
                holder.arrow.setImageDrawable(activity.getResources().getDrawable(R.drawable.arr_up));
                holder.clipboardValue.setTypeface(null, Typeface.BOLD);
                cliclArr[0] = true;
            } else {
                holder.arrow.setImageDrawable(activity.getResources().getDrawable(R.drawable.arr_down));
                cliclArr[0] = false;
            }
        }


        if (clipboard.getListContactsSearch() != null && !clipboard.getListContactsSearch().isEmpty() && clipboard.getListContactsSearch().size() > 0 && (clipboard.getListClipboards() == null || clipboard.getListClipboards().isEmpty())) {

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    try {

                        holder.circleImageViewSearch.setVisibility(View.GONE);
                        holder.initials.setVisibility(View.GONE);
                        holder.imageCompany.setVisibility(View.GONE);
                        holder.circleImageView.setVisibility(View.VISIBLE);
                        holder.circleImageView.setImageURI(null);

                        if (clipboard.getListContactsSearch().get(0).getPhotoURL() != null)
                            holder.circleImageView.setImageURI(Uri.parse(clipboard.getListContactsSearch().get(0).getPhotoURL()));

                        if (clipboard.getListContactsSearch().get(0).getPhotoURL() == null) {


                            GradientDrawable circle = (GradientDrawable) ContextCompat.getDrawable(activity, R.drawable.blue_circle).mutate();
                            circle.setColor(clipboard.getListContactsSearch().get(0).color);
                            holder.circleImageView.setBackground(circle);
                            holder.circleImageView.setImageDrawable(null);
                            String initials = getInitialsByName(clipboard.getListContactsSearch().get(0).getName());
                            holder.initials.setVisibility(View.VISIBLE);
                            holder.initials.setText(initials);

                            if (!clipboard.getListContactsSearch().get(0).contact) {

                                holder.circleImageView.setVisibility(View.GONE);
                                holder.imageCompany.setVisibility(View.VISIBLE);
                                holder.imageCompany.setBackgroundColor(clipboard.getListContactsSearch().get(0).color);
                            }
                        }
                    } catch (IllegalStateException e) {

                        clipboard.getListContactsSearch().remove(0);
                        holder.circleImageView.setVisibility(View.GONE);
                        if (typeOfData.equals(MagicType.CLIPBOARD))
                            ((TextView) activity.findViewById(R.id.statClipboard)).setText(getCountCLipboards() + " entries, " + getCountDetectedClipboards() + " detected");
                        else
                            ((TextView) activity.findViewById(R.id.statHistory)).setText(getCountCLipboards() + " entries, " + getCountDetectedClipboards() + " detected");

                    }


                }
            });
        } else if (clipboard.getListContactsSearch() == null || clipboard.getListContactsSearch().isEmpty() && (clipboard.getListClipboards() == null || clipboard.getListClipboards().isEmpty())) {
            if (clipboard.getType().equals(ClipboardEnum.FACEBOOK) || clipboard.getType().equals(ClipboardEnum.LINKEDIN) ||
                    clipboard.getType().equals(ClipboardEnum.INSTAGRAM) || clipboard.getType().equals(ClipboardEnum.VK) || clipboard.getType().equals(ClipboardEnum.YOUTUBE) || clipboard.getType().equals(ClipboardEnum.TWITTER) ||
                    clipboard.getType().equals(ClipboardEnum.MEDIUM) || clipboard.getType().equals(ClipboardEnum.WEB) || clipboard.getType().equals(ClipboardEnum.GITHUB)) {

                holder.circleImageView.setVisibility(View.GONE);
                holder.frameCircleSearch.setVisibility(View.VISIBLE);
                holder.frameSearchNote.setVisibility(View.GONE);
                holder.frameSearchSocials.setVisibility(View.VISIBLE);
            } else if (clipboard.getType().equals(ClipboardEnum.NAME) || clipboard.getType().equals(ClipboardEnum.COMPANY) || clipboard.getType().equals(ClipboardEnum.POSITION) ||
                    clipboard.getType().equals(ClipboardEnum.NOTE) || clipboard.getType().equals(ClipboardEnum.PHONE) || clipboard.getType().equals(ClipboardEnum.EMAIL) || clipboard.getType().equals(ClipboardEnum.ADDRESS)) {
                holder.circleImageView.setVisibility(View.GONE);
                holder.frameCircleSearch.setVisibility(View.VISIBLE);
                holder.frameSearchNote.setVisibility(View.VISIBLE);
                holder.frameSearchSocials.setVisibility(View.GONE);
            }
        }

        /*LinearLayoutManager myLayoutManager = (LinearLayoutManager) r.getLayoutManager();
        if(myLayoutManager.findFirstVisibleItemPosition() == -1 && myLayoutManager.findLastVisibleItemPosition() == -1) return;*/


        CustomRunnable customRunnable = new CustomRunnable() {

            //Realm realm;
            boolean run = true;

            public void stopThread() {
                run = false;
            }

            @Override
            public void run() {




                try {


                    if (clipboard.getListContactsSearch() == null || clipboard.getListContactsSearch().isEmpty() || clipboard.getListContactsSearch().size() == 0 && (clipboard.getListClipboards() == null || clipboard.getListClipboards().isEmpty())) {

                        if (!run) {

                            return;
                        }

                        Contact ct = ContactCacheService.find2(clipboard.getValueCopy(), clipboard.getType(), activity);

                        if (ct != null) {
                            boolean com = true;
                            if (ct.listOfContacts != null && !ct.listOfContacts.isEmpty())
                                com = false;

                            c[0] = new ContactDataClipboard(ct.getId(), ct.getName(), ct.getPhotoURL(), ct.color, com);
                        } else c[0] = null;

                        if (!run) {

                            /*activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if ((clipboard.getListContactsSearch() == null || clipboard.getListContactsSearch().isEmpty()) && (clipboard.getType().equals(ClipboardEnum.FACEBOOK) *//*|| clipboard.getType().equals(ClipboardEnum.LINKEDIN)*//* ||
                                            clipboard.getType().equals(ClipboardEnum.INSTAGRAM) || clipboard.getType().equals(ClipboardEnum.VK))) {

                                        boolean checkVisibleSearch = true;
                                        if (listOpen != null) {
                                            for (Clipboard clipboard1 : listOpen) {
                                                if (clipboard1.getListClipboards().contains(clipboard)) {
                                                    checkVisibleSearch = false;
                                                    break;
                                                }
                                            }
                                        }
                                        if (checkVisibleSearch)
                                            holder.frameCircleSearch.setVisibility(View.VISIBLE);

                                    }
                                }
                            });*/


                            c[0] = null;

                            return;
                        }

                      /*  if((position + 1 < contactsFragment.firstItem || position - 1 > contactsFragment.lastItem) && (contactsFragment.firstItem != 0 && contactsFragment.lastItem != 0)){

                            return;
                        }*/

                        //ArrayList<Contact> lll = new ArrayList<Contact>();
                        //clipboards.get(position).setListContactsSearch(lll);
                        //clipboards.get(position).setListContactsSearch(lll);
                        if (c[0] != null) {

                            clipboard.addContactToListSearch(c[0]);


                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (typeOfData.equals(MagicType.CLIPBOARD))
                                        ((TextView) activity.findViewById(R.id.statClipboard)).setText(getCountCLipboards() + " entries, " + getCountDetectedClipboards() + " detected");
                                    else
                                        ((TextView) activity.findViewById(R.id.statHistory)).setText(getCountCLipboards() + " entries, " + getCountDetectedClipboards() + " detected");

                                    holder.frameCircleSearch.setVisibility(View.GONE);
                                    holder.circleImageViewSearch.setVisibility(View.GONE);
                                    holder.initials.setVisibility(View.GONE);
                                    holder.imageCompany.setVisibility(View.GONE);
                                    holder.circleImageView.setVisibility(View.VISIBLE);
                                    holder.circleImageView.setImageURI(null);

                                    if (c[0].getPhotoURL() != null)
                                        holder.circleImageView.setImageURI(Uri.parse(c[0].getPhotoURL()));

                                    if (c[0].getPhotoURL() == null) {

                                        holder.circleImageView.setVisibility(View.VISIBLE);
                                        GradientDrawable circle = (GradientDrawable) ContextCompat.getDrawable(activity, R.drawable.blue_circle).mutate();
                                        circle.setColor(c[0].color);
                                        holder.circleImageView.setBackground(circle);
                                        holder.circleImageView.setImageDrawable(null);
                                        String initials = getInitialsByName(c[0].getName());
                                        holder.initials.setVisibility(View.VISIBLE);
                                        holder.initials.setText(initials);
                                        if (!c[0].contact) {

                                            holder.circleImageView.setVisibility(View.GONE);
                                            holder.imageCompany.setVisibility(View.VISIBLE);
                                            holder.imageCompany.setBackgroundColor(c[0].color);
                                        }
                                    }
                                }
                            });


                            //notifyDataSetChanged();


                        } else {


                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    holder.circleImageViewSearch.setVisibility(View.GONE);

                                   /* if (((clipboard.getListClipboards() == null || clipboard.getListClipboards().isEmpty()) && (clipboard.getListContactsSearch() == null || clipboard.getListContactsSearch().isEmpty())) && (clipboard.getType().equals(ClipboardEnum.FACEBOOK) || clipboard.getType().equals(ClipboardEnum.LINKEDIN) ||
                                            clipboard.getType().equals(ClipboardEnum.INSTAGRAM) || clipboard.getType().equals(ClipboardEnum.VK) || clipboard.getType().equals(ClipboardEnum.YOUTUBE) || clipboard.getType().equals(ClipboardEnum.TWITTER) ||
                                            clipboard.getType().equals(ClipboardEnum.MEDIUM) || clipboard.getType().equals(ClipboardEnum.WEB))) {

                                        boolean checkVisibleSearch = true;
                                        if (listOpen != null) {
                                            for (Clipboard clipboard1 : listOpen) {
                                                if (clipboard1.getListClipboards().contains(clipboard)) {
                                                    checkVisibleSearch = false;
                                                    break;
                                                }
                                            }
                                        }
                                        if (checkVisibleSearch) {
                                            holder.frameCircleSearch.setVisibility(View.VISIBLE);
                                            holder.frameSearchNote.setVisibility(View.GONE);
                                            holder.frameSearchSocials.setVisibility(View.VISIBLE);
                                        }

                                    }*/
                                    //new add


                                    //holder.imageCompany.setVisibility(View.GONE);
                                }
                            });

                        }



                        if (listOpen != null) {
                            for (Clipboard clipboard1 : listOpen) {
                                if (clipboard1.getListClipboards().contains(clipboard)) {
                                        /*holder.numberGroup.setVisibility(View.VISIBLE);
                                        holder.numberGroup.setText(String.valueOf(clipboard1.getListClipboards().indexOf(clipboard) + 1));
                                        if (clipboard1.getListClipboards().size() == clipboard1.getListClipboards().indexOf(clipboard) + 1) {
                                            holder.clipboardValue.setTypeface(null, Typeface.ITALIC);
                                        }*/



                                    r.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            notifyItemChanged(clipboards.indexOf(clipboard1));
                                        }
                                    });


                                    break;
                                }
                            }
                        }

                    } else if (clipboard.getListClipboards() == null || clipboard.getListClipboards().isEmpty()) {

                        if (!ContactCacheService.checkExistConatctFromClipboard(clipboards.get(position).getListContactsSearch().get(0).getId(), clipboards.get(position).getValueCopy(), clipboards.get(position).getType(), activity)) {

                            if (!run) {
                                return;
                            }

                            Contact ct = ContactCacheService.find2(clipboard.getValueCopy(), clipboard.getType(), activity);

                            if (ct != null) {
                                boolean com = true;
                                if (ct.listOfContacts != null && !ct.listOfContacts.isEmpty())
                                    com = false;

                                c[0] = new ContactDataClipboard(ct.getId(), ct.getName(), ct.getPhotoURL(), ct.color, com);
                            } else c[0] = null;

                            if (!run) {


                                c[0] = null;
                                return;
                            }


                            if (c[0] != null) {


                                clipboard.setFirstClipsToSearch(c[0]);


                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        holder.frameCircleSearch.setVisibility(View.GONE);
                                        holder.circleImageViewSearch.setVisibility(View.GONE);
                                        holder.initials.setVisibility(View.GONE);
                                        holder.imageCompany.setVisibility(View.GONE);
                                        holder.circleImageView.setVisibility(View.VISIBLE);
                                        holder.circleImageView.setImageURI(null);
                                        if (c[0].getPhotoURL() != null)
                                            holder.circleImageView.setImageURI(Uri.parse(c[0].getPhotoURL()));

                                        if (c[0].getPhotoURL() == null) {

                                            holder.circleImageView.setVisibility(View.VISIBLE);
                                            GradientDrawable circle = (GradientDrawable) ContextCompat.getDrawable(activity, R.drawable.blue_circle).mutate();
                                            circle.setColor(c[0].color);
                                            holder.circleImageView.setBackground(circle);
                                            holder.circleImageView.setImageDrawable(null);
                                            String initials = getInitialsByName(c[0].getName());
                                            holder.initials.setVisibility(View.VISIBLE);
                                            holder.initials.setText(initials);
                                            if (!c[0].contact) {

                                                holder.circleImageView.setVisibility(View.GONE);
                                                holder.imageCompany.setVisibility(View.VISIBLE);
                                                holder.imageCompany.setBackgroundColor(c[0].color);
                                            }
                                        }
                                    }
                                });

                                if (listOpen != null) {
                                    for (Clipboard clipboard1 : listOpen) {
                                        if (clipboard1.getListClipboards().contains(clipboard)) {
                                        /*holder.numberGroup.setVisibility(View.VISIBLE);
                                        holder.numberGroup.setText(String.valueOf(clipboard1.getListClipboards().indexOf(clipboard) + 1));
                                        if (clipboard1.getListClipboards().size() == clipboard1.getListClipboards().indexOf(clipboard) + 1) {
                                            holder.clipboardValue.setTypeface(null, Typeface.ITALIC);
                                        }*/

                                            r.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    notifyItemChanged(clipboards.indexOf(clipboard1));
                                                }
                                            });


                                            break;
                                        }
                                    }
                                }


                            } else {

                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        holder.circleImageViewSearch.setVisibility(View.GONE);

                                        /*if (((clipboard.getListClipboards() == null || clipboard.getListClipboards().isEmpty()) && (clipboard.getListContactsSearch() == null || clipboard.getListContactsSearch().isEmpty())) && (clipboard.getType().equals(ClipboardEnum.FACEBOOK) || clipboard.getType().equals(ClipboardEnum.LINKEDIN) ||
                                                clipboard.getType().equals(ClipboardEnum.INSTAGRAM) || clipboard.getType().equals(ClipboardEnum.YOUTUBE) || clipboard.getType().equals(ClipboardEnum.TWITTER) || clipboard.getType().equals(ClipboardEnum.MEDIUM) ||
                                                clipboard.getType().equals(ClipboardEnum.VK) || clipboard.getType().equals(ClipboardEnum.WEB))) {

                                            boolean checkVisibleSearch = true;
                                            if (listOpen != null) {
                                                for (Clipboard clipboard1 : listOpen) {
                                                    if (clipboard1.getListClipboards().contains(clipboard)) {
                                                        checkVisibleSearch = false;
                                                        break;
                                                    }
                                                }
                                            }

                                            if (checkVisibleSearch) {
                                                holder.frameCircleSearch.setVisibility(View.VISIBLE);
                                                holder.frameSearchNote.setVisibility(View.GONE);
                                                holder.frameSearchSocials.setVisibility(View.VISIBLE);
                                            }

                                        }*/

                                        //holder.imageCompany.setVisibility(View.GONE);
                                    }
                                });


                                try {
                                    clipboard.removeConatctFromListSearchBuId(clipboard.getListContactsSearch().get(0).getId());
                                } catch (Exception e) {
                                    clipboard.getListContactsSearch().remove(0);
                                }

                                c[0] = null;

                                //==========================
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (typeOfData.equals(MagicType.CLIPBOARD))
                                            ((TextView) activity.findViewById(R.id.statClipboard)).setText(getCountCLipboards() + " entries, " + getCountDetectedClipboards() + " detected");
                                        else
                                            ((TextView) activity.findViewById(R.id.statHistory)).setText(getCountCLipboards() + " entries, " + getCountDetectedClipboards() + " detected");


                                        //holder.frameInageIconClipboard.setVisibility(View.VISIBLE);
                                        holder.imageCompany.setVisibility(View.GONE);
                                        holder.circleImageView.setVisibility(View.GONE);

                                        if (clipboard.getListClipboards() == null || clipboard.getListClipboards().isEmpty())
                                            holder.circleImageViewSearch.setVisibility(View.GONE);
                                        else {
                                            holder.circleImageViewSearch.setVisibility(View.GONE);
                                            holder.arrow.setVisibility(View.VISIBLE);
                                            holder.arrow.setImageDrawable(null);
                                            if (listOpen.contains(clipboard)) {
                                                holder.arrow.setImageDrawable(activity.getResources().getDrawable(R.drawable.arr_up));
                                                cliclArr[0] = true;
                                            } else {
                                                holder.arrow.setImageDrawable(activity.getResources().getDrawable(R.drawable.arr_down));
                                                cliclArr[0] = false;
                                            }
                                        }
                                    }
                                });

                                if (listOpen != null) {
                                    for (Clipboard clipboard1 : listOpen) {
                                        if (clipboard1.getListClipboards().contains(clipboard)) {
                                        /*holder.numberGroup.setVisibility(View.VISIBLE);
                                        holder.numberGroup.setText(String.valueOf(clipboard1.getListClipboards().indexOf(clipboard) + 1));
                                        if (clipboard1.getListClipboards().size() == clipboard1.getListClipboards().indexOf(clipboard) + 1) {
                                            holder.clipboardValue.setTypeface(null, Typeface.ITALIC);
                                        }*/

                                            r.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    notifyItemChanged(clipboards.indexOf(clipboard1));
                                                }
                                            });


                                            break;
                                        }
                                    }
                                }

                                //==========================
                            }



                            //notifyDataSetChanged();

                        } else {
                            c[0] = clipboard.getListContactsSearch().get(0);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        Thread main = new Thread(customRunnable);
        main.start();


        if (checkHashClip.size() == 0) {


            if (popupInfo.getVisibility() == View.GONE) {
                if (typeOfData.equals(MagicType.CLIPBOARD))
                    activity.findViewById(R.id.textTopClipboard).setVisibility(View.INVISIBLE);
                else
                    activity.findViewById(R.id.textTopHistory).setVisibility(View.INVISIBLE);

            }

            if (typeOfData.equals(MagicType.CLIPBOARD)) {
                activity.findViewById(R.id.removeClipData).setVisibility(View.GONE);
                activity.findViewById(R.id.shareClipData).setVisibility(View.GONE);
                activity.findViewById(R.id.copyClipData).setVisibility(View.GONE);
                activity.findViewById(R.id.statClipboard).setVisibility(View.VISIBLE);
            } else {
                activity.findViewById(R.id.removeHistoryData).setVisibility(View.GONE);
                activity.findViewById(R.id.shareHistoryData).setVisibility(View.GONE);
                activity.findViewById(R.id.copyHistoryData).setVisibility(View.GONE);
                activity.findViewById(R.id.statHistory).setVisibility(View.VISIBLE);
            }
            //((TextView)activity.findViewById(R.id.statClipboard)).setText(getCountCLipboards()+" entries, "+getCountDetectedClipboards()+" detected");
        }

        //=========================


        holder.frameSearchNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //activity.findViewById(R.id.popupGlobalSearch).setVisibility(View.VISIBLE);
                openGlobalPopup(clipboard);
            }
        });

        holder.frameCircleSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                FrameLayout layout;
                if (typeOfData.equals(MagicType.CLIPBOARD)) {
                    layout = activity.findViewById(R.id.frameExtractClipboard);
                } else {
                    layout = activity.findViewById(R.id.frameExtractHistory);
                }

                layout.setVisibility(View.VISIBLE);
                //((TextView)layout.findViewById(R.id.textSearchClipboard)).setText("Reading link, please wait...");

                if (MainActivity.hasConnection(mainView.getContext())) {


                    RecyclerView recyclerView = layout.findViewById(R.id.clipboardExtract);
                    RecyclerView.LayoutManager mostLayoutManager = new LinearLayoutManager(mainView.getContext());
                    recyclerView.setLayoutManager(mostLayoutManager);
                    ExtratorAdapter extratorAdapter = new ExtratorAdapter(new ArrayList<Extrator>(), mainView.getContext());
                    recyclerView.setAdapter(extratorAdapter);
                    //checkConnectionFroParsing(true);
                    ClibpboardAdapter.ParsingSocialClipboard parsingSocial;
                    //if (parsingSocial != null)
                    //parsingSocial.cancel(true);

                    //floatingActionMenu.close(false);
                    // floatingActionMenu.setVisibility(View.GONE);
                    parsingSocial = new ClibpboardAdapter.ParsingSocialClipboard(clipboard, layout, extratorAdapter);
                    parsingSocial.execute();

                    layout.findViewById(R.id.buttonExtract).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (extratorAdapter.getSizeList() > 0) {


                                //==========
                                if (listOpen != null) {
                                    for (Clipboard clipboard1 : listOpen) {
                                        if (clipboard1.getListClipboards().contains(clipboard)) {
                                            /*holder.numberGroup.setVisibility(View.VISIBLE);
                                            holder.numberGroup.setText(String.valueOf(clipboard1.getListClipboards().indexOf(clipboard) + 1));
                                            if (clipboard1.getListClipboards().size() == clipboard1.getListClipboards().indexOf(clipboard) + 1 && *//*clipboard1.getType() != ClipboardEnum.GROUP*//* (!clipboard1.getType().equals(ClipboardEnum.FACEBOOK) && !clipboard1.getType().equals(ClipboardEnum.LINKEDIN) &&
                                                    !clipboard1.getType().equals(ClipboardEnum.INSTAGRAM) && !clipboard1.getType().equals(ClipboardEnum.VK) && !clipboard1.getType().equals(ClipboardEnum.YOUTUBE) && !clipboard1.getType().equals(ClipboardEnum.TWITTER) &&
                                                    !clipboard1.getType().equals(ClipboardEnum.MEDIUM))) {

                                                holder.clipboardValue.setTypeface(null, Typeface.ITALIC);
                                            }
                                            break;*/

                                            int id_ = clipboard1.getListClipboards().indexOf(clipboard);

                                            ArrayList<Clipboard> listClipsExtract = new ArrayList<>();
                                            for (int i = 0; i < extratorAdapter.getSizeList(); i++) {
                                                if (!extratorAdapter.getListExtract().get(i).isCheck())
                                                    continue;

                                                if (extratorAdapter.getListExtract().get(i).getType().equals(ExtractEnums.NAME)) {
                                                    listClipsExtract.add(ClipboardType.getNameType(extratorAdapter.getListExtract().get(i).getText(), mainView.getContext()));
                                                    continue;
                                                }
                                                listClipsExtract.addAll(ClipboardType.getListDataClipboard(extratorAdapter.getListExtract().get(i).getAllLink(), mainView.getContext()));
                                                //listClipsExtract.add(ClipboardType.getNoteType(extratorAdapter.getListExtract().get(i).getAllLink(), mainView.getContext()));
                                            }

                                            if (listClipsExtract.size() > 0) {

                                                for (int i = 0; i < listClipsExtract.size(); i++) {
                                                    for (Clipboard clip_ : clipboard1.getListClipboards()) {
                                                        if (listClipsExtract.get(i).getValueCopy().equalsIgnoreCase(clip_.getValueCopy())) {

                                                            listClipsExtract.remove(i);
                                                            i--;
                                                            break;
                                                        }
                                                    }
                                                    //if(listClipsExtract.size() == 0) break;
                                                }

                                                if (listClipsExtract.size() == 0) {
                                                    r.post(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Toast.makeText(activity, "Data exists already", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                    layout.setVisibility(View.GONE);
                                                    return;
                                                }
                                            }

                                            clipboard1.getListClipboards().addAll(id_ + 1, listClipsExtract);

                                            int ind = clipboards.indexOf(clipboard);
                                            clipboards.addAll(ind + 1, listClipsExtract);
                                            notifyItemRangeInserted(ind + 1, listClipsExtract.size());


                                            notifyDataSetChanged();

                                            layout.setVisibility(View.GONE);

                                            /*if (typeOfData.equals(MagicType.CLIPBOARD))
                                                EventBus.getDefault().post(new SaveClipboard());
                                            else
                                                EventBus.getDefault().post(new SaveHistory());*/

                                            r.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(activity, "Successfully extracted", Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                            return;
                                        }
                                    }
                                }
                                //=====================
                                if (clipboard.getListClipboards() != null && !clipboard.getListClipboards().isEmpty() && listOpen.contains(clipboard)) {
                                    holder.frameInageIconClipboard.callOnClick();

                                    for (Clipboard cl : clipboard.getListClipboards()) {
                                        checkHashClip.remove(cl);
                                    }
                                    checkHashClip.remove(clipboard);


                                    int co = 0;
                                    for (Clipboard cl : checkHashClip) {
                                        if (cl.getListClipboards() == null || cl.getListClipboards().isEmpty()) {
                                            co++;
                                        }
                                    }

                                    if (co > 0) {
                                        if (typeOfData.equals(MagicType.CLIPBOARD))
                                            ((TextView) activity.findViewById(R.id.textTopClipboard)).setText(co + " fields selected");
                                        else
                                            ((TextView) activity.findViewById(R.id.textTopHistory)).setText(co + " fields selected");

                                    } else {

                                        if (typeOfData.equals(MagicType.CLIPBOARD)) {
                                            activity.findViewById(R.id.statClipboard).setVisibility(View.VISIBLE);
                                            activity.findViewById(R.id.textTopClipboard).setVisibility(View.INVISIBLE);
                                            activity.findViewById(R.id.removeClipData).setVisibility(View.GONE);
                                            activity.findViewById(R.id.shareClipData).setVisibility(View.GONE);
                                            activity.findViewById(R.id.copyClipData).setVisibility(View.GONE);
                                        } else {
                                            activity.findViewById(R.id.statHistory).setVisibility(View.VISIBLE);
                                            activity.findViewById(R.id.textTopHistory).setVisibility(View.INVISIBLE);
                                            activity.findViewById(R.id.removeHistoryData).setVisibility(View.GONE);
                                            activity.findViewById(R.id.shareHistoryData).setVisibility(View.GONE);
                                            activity.findViewById(R.id.copyHistoryData).setVisibility(View.GONE);
                                        }
                                    }

                                } else if (clipboard.getListClipboards() != null && !clipboard.getListClipboards().isEmpty()) {

                                    for (Clipboard cl : clipboard.getListClipboards()) {
                                        checkHashClip.remove(cl);
                                    }
                                    checkHashClip.remove(clipboard);


                                    int co = 0;
                                    for (Clipboard cl : checkHashClip) {
                                        if (cl.getListClipboards() == null || cl.getListClipboards().isEmpty()) {
                                            co++;
                                        }
                                    }

                                    if (co > 0) {
                                        if (typeOfData.equals(MagicType.CLIPBOARD))
                                            ((TextView) activity.findViewById(R.id.textTopClipboard)).setText(co + " fields selected");
                                        else
                                            ((TextView) activity.findViewById(R.id.textTopHistory)).setText(co + " fields selected");
                                    } else {
                                        if (typeOfData.equals(MagicType.CLIPBOARD)) {
                                            activity.findViewById(R.id.statClipboard).setVisibility(View.VISIBLE);
                                            activity.findViewById(R.id.textTopClipboard).setVisibility(View.INVISIBLE);
                                            activity.findViewById(R.id.removeClipData).setVisibility(View.GONE);
                                            activity.findViewById(R.id.shareClipData).setVisibility(View.GONE);
                                            activity.findViewById(R.id.copyClipData).setVisibility(View.GONE);
                                        } else {
                                            activity.findViewById(R.id.statHistory).setVisibility(View.VISIBLE);
                                            activity.findViewById(R.id.textTopHistory).setVisibility(View.INVISIBLE);
                                            activity.findViewById(R.id.removeHistoryData).setVisibility(View.GONE);
                                            activity.findViewById(R.id.shareHistoryData).setVisibility(View.GONE);
                                            activity.findViewById(R.id.copyHistoryData).setVisibility(View.GONE);
                                        }
                                    }

                                }

                                ArrayList<Clipboard> listClipsExtract = new ArrayList<>();
                                for (int i = 0; i < extratorAdapter.getSizeList(); i++) {
                                    if (!extratorAdapter.getListExtract().get(i).isCheck())
                                        continue;

                                    if (extratorAdapter.getListExtract().get(i).getType().equals(ExtractEnums.NAME)) {
                                        listClipsExtract.add(ClipboardType.getNameType(extratorAdapter.getListExtract().get(i).getText(), mainView.getContext()));
                                        continue;
                                    }

                                    listClipsExtract.addAll(ClipboardType.getListDataClipboard(extratorAdapter.getListExtract().get(i).getAllLink(), mainView.getContext()));
                                    //listClipsExtract.add(ClipboardType.getNoteType(extratorAdapter.getListExtract().get(i).getAllLink(), mainView.getContext()));
                                }

                                //---------------------------------------


                                //---------------------------------------

                                clipboard.setListClipboards(listClipsExtract);
                                //clipboard.setType(ClipboardEnum.GROUP);
                                layout.setVisibility(View.GONE);
                                notifyItemChanged(position);

                                /*if (typeOfData.equals(MagicType.CLIPBOARD))
                                    EventBus.getDefault().post(new SaveClipboard());
                                else
                                    EventBus.getDefault().post(new SaveHistory());*/

                                r.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(activity, "Successfully extracted", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });

                    layout.findViewById(R.id.imageExtClip).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (extratorAdapter.getSizeList() > 0) {
                                ArrayList<Clipboard> listClipsExtract = new ArrayList<>();
                                String text = "";
                                for (int i = 0; i < extratorAdapter.getSizeList(); i++) {
                                    if (!extratorAdapter.getListExtract().get(i).isCheck())
                                        continue;

                                    if (extratorAdapter.getListExtract().get(i).getType().equals(ExtractEnums.NAME)) {
                                        text += "Name: " + extratorAdapter.getListExtract().get(i).getText() + "\n";
                                        continue;
                                    }
                                    //listClipsExtract.addAll(ClipboardType.getListDataClipboard(extratorAdapter.getListExtract().get(i).getAllLink(), mainView.getContext()));

                                    text += extratorAdapter.getListExtract().get(i).getType().toString().toLowerCase().substring(0, 1).toUpperCase() +
                                            extratorAdapter.getListExtract().get(i).getType().toString().toLowerCase().substring(1) + " : " +
                                            extratorAdapter.getListExtract().get(i).getAllLink() + "\n";
                                    //listClipsExtract.add(ClipboardType.getNoteType(extratorAdapter.getListExtract().get(i).getAllLink(), mainView.getContext()));
                                }

                               /* clipboard.setListClipboards(listClipsExtract);
                                clipboard.setType(ClipboardEnum.GROUP);
                                layout.setVisibility(View.GONE);
                                notifyItemChanged(position);*/


                                text += "\n";
                                text += "Data shared via http://Extime.pro\n";

                                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                                shareIntent.setType("text/plain");
                                shareIntent.putExtra(Intent.EXTRA_TEXT, text);
                                activity.startActivity(Intent.createChooser(shareIntent, "Share"));
                            }
                        }
                    });

                    layout.findViewById(R.id.buttonExtractCancel).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (parsingSocial != null)
                                parsingSocial.cancel(true);

                            layout.setVisibility(View.GONE);
                        }
                    });

                } else {
                    ((TextView) layout.findViewById(R.id.textSearchClipboard)).setText("No internet connection");
                    //checkConnectionFroParsing(false);
                    //getActivity().findViewById(R.id.framePopupSocial).setVisibility(View.VISIBLE);
                    //((ImageView) getActivity().findViewById(R.id.imageSocialExtract)).setImageDrawable(getResources().getDrawable(R.drawable.ic_youtube_48));

                    layout.findViewById(R.id.buttonExtractCancel).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            layout.setVisibility(View.GONE);
                        }
                    });

                }
                //      }
                //======================== socials
            }
        });


        holder.frameImageNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                openChangeType(clipboard);

            }
        });


        holder.frameInageIconClipboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if(!clickProfile[0]) {
                if (c[0] != null && (clipboard.getListClipboards() == null || clipboard.getListClipboards().isEmpty())) {
                    contactsFragment.closeOtherPopup();
                    if (!ProfileFragment.profile) {
                        if (c[0].contact) {
                            Contact contact = ContactCacheService.getContactById(c[0].getId());
                            contactsFragment.contactAdapter.selectedContactId = String.valueOf(contact.getId());
                            contactsFragment.showProfilePopUp(contact);

                            if (ClibpboardAdapter.checkSelectClips) {

                                /*if (contactsFragment.getActivity() == null)
                                    ((TextView) ac.findViewById(R.id.updateContactClipboard)).setTextColor(contactsFragment.getResources().getColor(R.color.colorPrimary));
                                else*/
                                ((TextView) activity.findViewById(R.id.updateContactClipboard)).setTextColor(contactsFragment.getResources().getColor(R.color.colorPrimary));
                            }

                        } else {

                            Contact contact = ContactCacheService.getContactById(c[0].getId());

                            if (contact != null) {
                                contactsFragment.contactAdapter.selectedContactId = contact.getName();
                                contactsFragment.showCompanyPopup(contact);

                                if (ClibpboardAdapter.checkSelectClips) {

                                    /*if (contactsFragment.getActivity() == null)
                                        ((TextView) MainActivity.activityProfile.findViewById(R.id.updateContactClipboard)).setTextColor(contactsFragment.getResources().getColor(R.color.colorPrimary));
                                    else*/
                                    ((TextView) activity.findViewById(R.id.updateContactClipboard)).setTextColor(contactsFragment.getResources().getColor(R.color.colorPrimary));
                                }
                            }

                        }
                    } else {
                        if (c[0].contact) {
                            EventBus.getDefault().post(new PopupInProfile(c[0].getId()));
                        } else
                            EventBus.getDefault().post(new PopupCompanyInProfile(c[0].getId()));
                    }
                } else if (clipboard.getListClipboards() != null && !clipboard.getListClipboards().isEmpty()) {
                    if (!cliclArr[0]) {
                        cliclArr[0] = true;
                        holder.arrow.setImageDrawable(activity.getResources().getDrawable(R.drawable.arr_up));
                        listOpen.add(clipboard);
                        ArrayList<Clipboard> listChildClip = clipboard.getListClipboards();
                        int ind = clipboards.indexOf(clipboard);
                        clipboards.addAll(ind + 1, listChildClip);
                        notifyItemRangeInserted(ind + 1, listChildClip.size());
                        holder.clipboardValue.setTypeface(null, Typeface.BOLD);
                        //notifyDataSetChanged();


                    } else {
                        cliclArr[0] = false;
                        holder.arrow.setImageDrawable(activity.getResources().getDrawable(R.drawable.arr_down));
                        listOpen.remove(clipboard);
                        int ind = clipboards.indexOf(clipboard);
                        ArrayList<Clipboard> listChildClip = clipboard.getListClipboards();

                        for (int i = 0; i < listChildClip.size(); i++)
                            clipboards.remove(ind + 1);

                        notifyItemRangeRemoved(ind + 1, listChildClip.size());
                        holder.clipboardValue.setTypeface(null, Typeface.NORMAL);

                        //notifyItemChanged(ind+1);
                        //notifyDataSetChanged();


                    }
                }
            }
        });

        holder.frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.checkBox.isChecked()) holder.checkBox.setChecked(false);
                else holder.checkBox.setChecked(true);



                if (holder.checkBox.isChecked()) {

                    checkHashClip.add(clipboard);//click


                    if (clipboard.getListClipboards() != null && !clipboard.getListClipboards().isEmpty()) {
                        if (listOpen.contains(clipboard)) {

                            for (Clipboard cl : clipboard.getListClipboards()) {
                                if (!checkHashClip.contains(cl)) {
                                    checkHashClip.add(cl);
                                    allBind = false;
                                    notifyItemChanged(clipboards.indexOf(cl));
                                    allBind = true;
                                }
                            }


                        } else {
                            for (Clipboard cl : clipboard.getListClipboards()) {
                                checkHashClip.add(cl);

                            }

                        }
                    } else {
                        for (Clipboard cll : clipboards) {
                            if (cll.getListClipboards() != null && !cll.getListClipboards().isEmpty()) {
                                if (cll.getListClipboards().contains(clipboard)) {
                                    boolean checkB = true;
                                    for (Clipboard cll2 : cll.getListClipboards()) {
                                        if (!checkHashClip.contains(cll2)) checkB = false;
                                    }
                                    if (checkB) {
                                        checkHashClip.add(cll);
                                        allBind = false;
                                        notifyItemChanged(clipboards.indexOf(cll));
                                        allBind = true;
                                    }
                                }
                            }
                        }
                    }

                    boolean checkList = false;

                    int co = 0;
                    for (Clipboard cl : checkHashClip) {
                        if (cl.getListClipboards() == null || cl.getListClipboards().isEmpty()) {
                            co++;
                        } else {
                            checkList = true;
                        }
                    }

                    if (typeOfData.equals(MagicType.CLIPBOARD)) {
                        ((TextView) activity.findViewById(R.id.textTopClipboard)).setText(co + " fields selected");
                        activity.findViewById(R.id.textTopClipboard).setVisibility(View.VISIBLE);

                        int px = (int) (12 * activity.getResources().getDisplayMetrics().density);

                        ((TextView) activity.findViewById(R.id.textTopClipboard)).setTextSize(9);
                        ((TextView) activity.findViewById(R.id.textTopClipboard)).requestLayout();


                        activity.findViewById(R.id.statClipboard).setVisibility(View.GONE);
                        activity.findViewById(R.id.removeClipData).setVisibility(View.VISIBLE);
                        activity.findViewById(R.id.shareClipData).setVisibility(View.VISIBLE);
                        activity.findViewById(R.id.copyClipData).setVisibility(View.VISIBLE);
                        ((TextView) activity.findViewById(R.id.createContactClipboard)).setTextColor(mainView.getContext().getResources().getColor(R.color.colorPrimary));
                    } else {
                        ((TextView) activity.findViewById(R.id.textTopHistory)).setText(co + " fields selected");
                        activity.findViewById(R.id.textTopHistory).setVisibility(View.VISIBLE);

                        int px = (int) (12 * activity.getResources().getDisplayMetrics().density);

                        ((TextView) activity.findViewById(R.id.textTopHistory)).setTextSize(9);
                        ((TextView) activity.findViewById(R.id.textTopHistory)).requestLayout();


                        activity.findViewById(R.id.statHistory).setVisibility(View.GONE);
                        activity.findViewById(R.id.removeHistoryData).setVisibility(View.VISIBLE);
                        activity.findViewById(R.id.shareHistoryData).setVisibility(View.VISIBLE);
                        activity.findViewById(R.id.copyHistoryData).setVisibility(View.VISIBLE);
                        ((TextView) activity.findViewById(R.id.createContactHistory)).setTextColor(mainView.getContext().getResources().getColor(R.color.colorPrimary));
                    }


                    checkSelectClips = true;
                    boolean checkCompanyInList = false;
                    int countListClips = 0;
                    if (!checkList) {
                        for (Clipboard clipboard1 : checkHashClip) {
                            if (clipboard1.getListContactsSearch() != null && !clipboard1.getListContactsSearch().isEmpty()) {
                                countListClips++;
                                if (!clipboard1.getListContactsSearch().get(0).contact)
                                    checkCompanyInList = true;
                            }
                        }
                        if (checkHashClip.size() > 1)
                            ((TextView) activity.findViewById(R.id.mergeClipboards)).setTextColor(mainView.getContext().getResources().getColor(R.color.colorPrimary));
                        else
                            ((TextView) activity.findViewById(R.id.mergeClipboards)).setTextColor(mainView.getContext().getResources().getColor(R.color.gray));
                    } else {
                        ((TextView) activity.findViewById(R.id.updateContactClipboard)).setTextColor(mainView.getContext().getResources().getColor(R.color.gray));
                        ((TextView) activity.findViewById(R.id.mergeClipboards)).setTextColor(mainView.getContext().getResources().getColor(R.color.gray));
                        checkUpdateClips = false;
                    }

                    if (countListClips == 1 && checkHashClip.size() > 1 && !checkCompanyInList) {
                        ((TextView) activity.findViewById(R.id.updateContactClipboard)).setTextColor(mainView.getContext().getResources().getColor(R.color.colorPrimary));
                        checkUpdateClips = true;
                    } else {
                        ((TextView) activity.findViewById(R.id.updateContactClipboard)).setTextColor(mainView.getContext().getResources().getColor(R.color.gray));
                        checkUpdateClips = false;
                    }



                    if ((ProfileFragment.profile /*&& !ProfileFragment.company*/) || (contactsFragment.contactAdapter.getSelectContactID() != null) || ContactsFragment.createContact) {
                        ((TextView) activity.findViewById(R.id.updateContactClipboard)).setTextColor(mainView.getContext().getResources().getColor(R.color.colorPrimary));
                        if (contactsFragment.contactAdapter.getSelectContactID() != null) {
                            /*try{
                                long l = Long.parseLong(contactsFragment.contactAdapter.getSelectContactID());
                            }catch (Exception e){
                                ((TextView) activity.findViewById(R.id.updateContactClipboard)).setTextColor(mainView.getContext().getResources().getColor(R.color.gray));
                            }*/
                        }
                    }

                } else {

                    if (clipboard.getListClipboards() != null && !clipboard.getListClipboards().isEmpty()) {
                        if (listOpen.contains(clipboard)) {

                            for (Clipboard cl : clipboard.getListClipboards()) {
                                checkHashClip.remove(cl);
                                allBind = false;
                                notifyItemChanged(clipboards.indexOf(cl));
                                allBind = true;
                            }


                        } else {
                            for (Clipboard cl : clipboard.getListClipboards()) {
                                checkHashClip.remove(cl);

                            }

                        }
                        checkHashClip.remove(clipboard);
                    } else {

                        for (Clipboard cll : checkHashClip) {
                            if (cll.getListClipboards() != null && !cll.getListClipboards().isEmpty()) {
                                if (cll.getListClipboards().contains(clipboard)) {
                                    checkHashClip.remove(cll);
                                    allBind = false;
                                    notifyItemChanged(clipboards.indexOf(cll));
                                    allBind = true;
                                    break;
                                }
                            }
                        }

                        try {
                            checkHashClip.remove(clipboard);//--
                        } catch (Exception e) {
                        }

                    }

                    boolean checkList = false;
                    if (checkHashClip.size() > 0) {
                        int co = 0;
                        for (Clipboard cl : checkHashClip) {
                            if (cl.getListClipboards() == null || cl.getListClipboards().isEmpty()) {
                                co++;
                            } else {
                                checkList = true;
                            }
                        }


                        if (typeOfData.equals(MagicType.CLIPBOARD)) {
                            ((TextView) activity.findViewById(R.id.textTopClipboard)).setText(co + " fields selected");
                            activity.findViewById(R.id.removeClipData).setVisibility(View.VISIBLE);
                            activity.findViewById(R.id.shareClipData).setVisibility(View.VISIBLE);
                            activity.findViewById(R.id.copyClipData).setVisibility(View.VISIBLE);
                            activity.findViewById(R.id.statClipboard).setVisibility(View.GONE);
                            ((TextView) activity.findViewById(R.id.createContactClipboard)).setTextColor(mainView.getContext().getResources().getColor(R.color.colorPrimary));

                            if (ProfileFragment.profile) {
                                ((TextView) activity.findViewById(R.id.updateContactClipboard)).setTextColor(mainView.getContext().getResources().getColor(R.color.colorPrimary));
                            }
                            checkSelectClips = true;
                            boolean checkCompanyInList = false;

                            int countListClips = 0;
                            if (!checkList) {
                                for (Clipboard clipboard1 : checkHashClip) {
                                    if (clipboard1.getListContactsSearch() != null && !clipboard1.getListContactsSearch().isEmpty()) {
                                        countListClips++;
                                        if (!clipboard1.getListContactsSearch().get(0).contact)
                                            checkCompanyInList = true;

                                    }
                                }
                                if (checkHashClip.size() > 1)
                                    ((TextView) activity.findViewById(R.id.mergeClipboards)).setTextColor(mainView.getContext().getResources().getColor(R.color.colorPrimary));
                                else
                                    ((TextView) activity.findViewById(R.id.mergeClipboards)).setTextColor(mainView.getContext().getResources().getColor(R.color.gray));
                            } else {
                                ((TextView) activity.findViewById(R.id.updateContactClipboard)).setTextColor(mainView.getContext().getResources().getColor(R.color.gray));
                                ((TextView) activity.findViewById(R.id.mergeClipboards)).setTextColor(mainView.getContext().getResources().getColor(R.color.gray));
                                checkUpdateClips = false;
                            }

                            if (countListClips == 1 && checkHashClip.size() > 1 && !checkCompanyInList) {
                                ((TextView) activity.findViewById(R.id.updateContactClipboard)).setTextColor(mainView.getContext().getResources().getColor(R.color.colorPrimary));
                                checkUpdateClips = true;
                            } else {
                                ((TextView) activity.findViewById(R.id.updateContactClipboard)).setTextColor(mainView.getContext().getResources().getColor(R.color.gray));
                                checkUpdateClips = false;
                            }

                            if ((ProfileFragment.profile /*&& !ProfileFragment.company*/) || (contactsFragment.contactAdapter.getSelectContactID() != null) || ContactsFragment.createContact) {
                                ((TextView) activity.findViewById(R.id.updateContactClipboard)).setTextColor(mainView.getContext().getResources().getColor(R.color.colorPrimary));
                            /*if(contactsFragment.contactAdapter.getSelectContactID() != null){
                                try{
                                    long l = Long.parseLong(contactsFragment.contactAdapter.getSelectContactID());
                                }catch (Exception e){
                                    ((TextView) activity.findViewById(R.id.updateContactClipboard)).setTextColor(mainView.getContext().getResources().getColor(R.color.gray));
                                }
                            }*/
                            }
                        } else {
                            ((TextView) activity.findViewById(R.id.textTopHistory)).setText(co + " fields selected");
                            activity.findViewById(R.id.removeHistoryData).setVisibility(View.VISIBLE);
                            activity.findViewById(R.id.shareHistoryData).setVisibility(View.VISIBLE);
                            activity.findViewById(R.id.copyHistoryData).setVisibility(View.VISIBLE);
                            activity.findViewById(R.id.statHistory).setVisibility(View.GONE);
                            ((TextView) activity.findViewById(R.id.createContactHistory)).setTextColor(mainView.getContext().getResources().getColor(R.color.colorPrimary));

                            if (ProfileFragment.profile) {
                                ((TextView) activity.findViewById(R.id.updateContactHistory)).setTextColor(mainView.getContext().getResources().getColor(R.color.colorPrimary));
                            }
                            checkSelectClips = true;
                            boolean checkCompanyInList = false;

                            int countListClips = 0;
                            if (!checkList) {
                                for (Clipboard clipboard1 : checkHashClip) {
                                    if (clipboard1.getListContactsSearch() != null && !clipboard1.getListContactsSearch().isEmpty()) {
                                        countListClips++;
                                        if (!clipboard1.getListContactsSearch().get(0).contact)
                                            checkCompanyInList = true;

                                    }
                                }
                                if (checkHashClip.size() > 1)
                                    ((TextView) activity.findViewById(R.id.mergeHistory)).setTextColor(mainView.getContext().getResources().getColor(R.color.colorPrimary));
                                else
                                    ((TextView) activity.findViewById(R.id.mergeHistory)).setTextColor(mainView.getContext().getResources().getColor(R.color.gray));
                            } else {
                                ((TextView) activity.findViewById(R.id.updateContactHistory)).setTextColor(mainView.getContext().getResources().getColor(R.color.gray));
                                ((TextView) activity.findViewById(R.id.mergeHistory)).setTextColor(mainView.getContext().getResources().getColor(R.color.gray));
                                checkUpdateClips = false;
                            }

                            if (countListClips == 1 && checkHashClip.size() > 1 && !checkCompanyInList) {
                                ((TextView) activity.findViewById(R.id.updateContactHistory)).setTextColor(mainView.getContext().getResources().getColor(R.color.colorPrimary));
                                checkUpdateClips = true;
                            } else {
                                ((TextView) activity.findViewById(R.id.updateContactHistory)).setTextColor(mainView.getContext().getResources().getColor(R.color.gray));
                                checkUpdateClips = false;
                            }

                            if ((ProfileFragment.profile /*&& !ProfileFragment.company*/) || (contactsFragment.contactAdapter.getSelectContactID() != null) || ContactsFragment.createContact) {
                                ((TextView) activity.findViewById(R.id.updateContactHistory)).setTextColor(mainView.getContext().getResources().getColor(R.color.colorPrimary));
                            /*if(contactsFragment.contactAdapter.getSelectContactID() != null){
                                try{
                                    long l = Long.parseLong(contactsFragment.contactAdapter.getSelectContactID());
                                }catch (Exception e){
                                    ((TextView) activity.findViewById(R.id.updateContactClipboard)).setTextColor(mainView.getContext().getResources().getColor(R.color.gray));
                                }
                            }*/
                            }
                        }
                    } else {
                        if (typeOfData.equals(MagicType.CLIPBOARD)) {
                            ((TextView) activity.findViewById(R.id.statClipboard)).setText(getCountCLipboards() + " entries, " + getCountDetectedClipboards() + " detected");
                            activity.findViewById(R.id.statClipboard).setVisibility(View.VISIBLE);
                            activity.findViewById(R.id.textTopClipboard).setVisibility(View.INVISIBLE);
                            activity.findViewById(R.id.removeClipData).setVisibility(View.GONE);
                            activity.findViewById(R.id.shareClipData).setVisibility(View.GONE);
                            activity.findViewById(R.id.copyClipData).setVisibility(View.GONE);
                            ((TextView) activity.findViewById(R.id.createContactClipboard)).setTextColor(mainView.getContext().getResources().getColor(R.color.gray));
                            ((TextView) activity.findViewById(R.id.updateContactClipboard)).setTextColor(mainView.getContext().getResources().getColor(R.color.gray));
                            ((TextView) activity.findViewById(R.id.mergeClipboards)).setTextColor(mainView.getContext().getResources().getColor(R.color.gray));
                        } else {
                            ((TextView) activity.findViewById(R.id.statHistory)).setText(getCountCLipboards() + " entries, " + getCountDetectedClipboards() + " detected");
                            activity.findViewById(R.id.statHistory).setVisibility(View.VISIBLE);
                            activity.findViewById(R.id.textTopHistory).setVisibility(View.INVISIBLE);
                            activity.findViewById(R.id.removeHistoryData).setVisibility(View.GONE);
                            activity.findViewById(R.id.shareHistoryData).setVisibility(View.GONE);
                            activity.findViewById(R.id.copyHistoryData).setVisibility(View.GONE);
                            ((TextView) activity.findViewById(R.id.createContactHistory)).setTextColor(mainView.getContext().getResources().getColor(R.color.gray));
                            ((TextView) activity.findViewById(R.id.updateContactHistory)).setTextColor(mainView.getContext().getResources().getColor(R.color.gray));
                            ((TextView) activity.findViewById(R.id.mergeHistory)).setTextColor(mainView.getContext().getResources().getColor(R.color.gray));
                        }
                        checkSelectClips = false;
                        checkUpdateClips = false;
                    }
                }

            }
        });


        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View view) {

                //===



                openPrev(clipboard, position, holder);


                //=============
            }
        });


        //============================

        new Thread(new Runnable() {
            @Override
            public void run() {

                if ((position + 1 < contactsFragment.firstItem || position - 2 > contactsFragment.lastItem) && (contactsFragment.firstItem != 0 && contactsFragment.lastItem != 0)) {
                    if (clipboard.getType().equals(ClipboardEnum.PHONE)) {
                        customRunnable.stopThread();

                    }
                }
            }
        }).start();


    }

    public void uncheck() {
        checkHashClip.clear();
        checkSelectClips = false;
        checkUpdateClips = false;

        if (mainView != null) {
            ((TextView) activity.findViewById(R.id.createContactClipboard)).setTextColor(mainView.getContext().getResources().getColor(R.color.gray));
            ((TextView) activity.findViewById(R.id.updateContactClipboard)).setTextColor(mainView.getContext().getResources().getColor(R.color.gray));
            ((TextView) activity.findViewById(R.id.mergeClipboards)).setTextColor(mainView.getContext().getResources().getColor(R.color.gray));
        }

        notifyDataSetChanged();
    }


    public void openChangeType(Clipboard clipboard) {
        restartPopupChange();

        ((TextView) popupChangeType.findViewById(R.id.valueChange)).setText(clipboard.getValueCopy());


        popupChangeType.findViewById(R.id.ok_change).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupChangeType.setVisibility(View.GONE);
            }
        });

        popupChangeType.findViewById(R.id.head_change).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupChangeType.setVisibility(View.GONE);
            }
        });



        if (clipboard.getType().equals(ClipboardEnum.ADDRESS)) {
            ((ImageView) popupChangeType.findViewById(R.id.icnAddressType)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));

            ((TextView) popupChangeType.findViewById(R.id.typeAddressText)).setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            ((TextView) popupChangeType.findViewById(R.id.typeAddressText)).setTextColor(Color.parseColor("#000000"));

            popupChangeType.findViewById(R.id.addressTick).setVisibility(View.VISIBLE);

            //((TextView) popupChangeType.findViewById(R.id.currentAddress)).setText("(current)");
            //((TextView) popupChangeType.findViewById(R.id.currentAddress)).setVisibility(View.VISIBLE);

        } else if (clipboard.getType().equals(ClipboardEnum.DATEOFBIRTH)) {

            ((ImageView) popupChangeType.findViewById(R.id.icnBirthType)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));

            ((TextView) popupChangeType.findViewById(R.id.typeDateText)).setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            ((TextView) popupChangeType.findViewById(R.id.typeDateText)).setTextColor(Color.parseColor("#000000"));

            popupChangeType.findViewById(R.id.birthTick).setVisibility(View.VISIBLE);

            //((TextView) popupChangeType.findViewById(R.id.currentDate)).setText("(current)");
            //((TextView) popupChangeType.findViewById(R.id.currentDate)).setVisibility(View.VISIBLE);

        } else if (clipboard.getType().equals(ClipboardEnum.EMAIL)) {
            ((ImageView) popupChangeType.findViewById(R.id.icnEmailType)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));

            ((TextView) popupChangeType.findViewById(R.id.typeEmailText)).setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            ((TextView) popupChangeType.findViewById(R.id.typeEmailText)).setTextColor(Color.parseColor("#000000"));

            popupChangeType.findViewById(R.id.emailTick).setVisibility(View.VISIBLE);

            //((TextView) popupChangeType.findViewById(R.id.currentEmail)).setText("(current)");
            //((TextView) popupChangeType.findViewById(R.id.currentEmail)).setVisibility(View.VISIBLE);

        } else if (clipboard.getType().equals(ClipboardEnum.NAME)) {
            ((ImageView) popupChangeType.findViewById(R.id.icnProfileType)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));

            ((TextView) popupChangeType.findViewById(R.id.typeNameText)).setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            ((TextView) popupChangeType.findViewById(R.id.typeNameText)).setTextColor(Color.parseColor("#000000"));

            popupChangeType.findViewById(R.id.profileTick).setVisibility(View.VISIBLE);

            //((TextView) popupChangeType.findViewById(R.id.currentName)).setText("(current)");
            //((TextView) popupChangeType.findViewById(R.id.currentName)).setVisibility(View.VISIBLE);


        } else if (clipboard.getType().equals(ClipboardEnum.NOTE)) {
            ((ImageView) popupChangeType.findViewById(R.id.icnNoteType)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));

            ((TextView) popupChangeType.findViewById(R.id.typeNoteText)).setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            ((TextView) popupChangeType.findViewById(R.id.typeNoteText)).setTextColor(Color.parseColor("#000000"));

            popupChangeType.findViewById(R.id.noteTick).setVisibility(View.VISIBLE);

            //((TextView) popupChangeType.findViewById(R.id.currentNote)).setText("(current)");
            //((TextView) popupChangeType.findViewById(R.id.currentNote)).setVisibility(View.VISIBLE);


        } else if (clipboard.getType().equals(ClipboardEnum.POSITION)) {
            ((ImageView) popupChangeType.findViewById(R.id.icnPositionType)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));

            ((TextView) popupChangeType.findViewById(R.id.typePositionText)).setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            ((TextView) popupChangeType.findViewById(R.id.typePositionText)).setTextColor(Color.parseColor("#000000"));

            popupChangeType.findViewById(R.id.positionTick).setVisibility(View.VISIBLE);

            //((TextView) popupChangeType.findViewById(R.id.currentPosition)).setText("(current)");
            //((TextView) popupChangeType.findViewById(R.id.currentPosition)).setVisibility(View.VISIBLE);

        } else if (clipboard.getType().equals(ClipboardEnum.PHONE)) {
            ((ImageView) popupChangeType.findViewById(R.id.icnPhoneType)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));

            ((TextView) popupChangeType.findViewById(R.id.typePhoneText)).setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            ((TextView) popupChangeType.findViewById(R.id.typePhoneText)).setTextColor(Color.parseColor("#000000"));

            popupChangeType.findViewById(R.id.phoneTick).setVisibility(View.VISIBLE);

            //((TextView) popupChangeType.findViewById(R.id.currentPhone)).setText("(current)");
            //((TextView) popupChangeType.findViewById(R.id.currentPhone)).setVisibility(View.VISIBLE);


        } else if (clipboard.getType().equals(ClipboardEnum.WEB)) {
            ((ImageView) popupChangeType.findViewById(R.id.icnWebType)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));

            ((TextView) popupChangeType.findViewById(R.id.typeWebText)).setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            ((TextView) popupChangeType.findViewById(R.id.typeWebText)).setTextColor(Color.parseColor("#000000"));

            popupChangeType.findViewById(R.id.webTick).setVisibility(View.VISIBLE);

            //((TextView) popupChangeType.findViewById(R.id.currentWeb)).setText("(current)");
            //((TextView) popupChangeType.findViewById(R.id.currentWeb)).setVisibility(View.VISIBLE);

        } else if (clipboard.getType().equals(ClipboardEnum.COMPANY)) {
            ((ImageView) popupChangeType.findViewById(R.id.icnCompanyType)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));

            ((TextView) popupChangeType.findViewById(R.id.typeCompanyText)).setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            ((TextView) popupChangeType.findViewById(R.id.typeCompanyText)).setTextColor(Color.parseColor("#000000"));

            popupChangeType.findViewById(R.id.companyTick).setVisibility(View.VISIBLE);

            //((TextView) popupChangeType.findViewById(R.id.currentCompany)).setText("(current)");
            //((TextView) popupChangeType.findViewById(R.id.currentCompany)).setVisibility(View.VISIBLE);

        } else if (clipboard.getType().equals(ClipboardEnum.HASHTAG)) {
            ((ImageView) popupChangeType.findViewById(R.id.icnHashType)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));

            ((TextView) popupChangeType.findViewById(R.id.typeHashText)).setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            ((TextView) popupChangeType.findViewById(R.id.typeHashText)).setTextColor(Color.parseColor("#000000"));

            popupChangeType.findViewById(R.id.hashTick).setVisibility(View.VISIBLE);

            //((TextView) popupChangeType.findViewById(R.id.currentHash)).setText("(current)");
            //((TextView) popupChangeType.findViewById(R.id.currentHash)).setVisibility(View.VISIBLE);

        }else if (clipboard.getType().equals(ClipboardEnum.BIO_INTRO)) {
            ((ImageView) popupChangeType.findViewById(R.id.icnBioType)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));

            ((TextView) popupChangeType.findViewById(R.id.typeBioText)).setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            ((TextView) popupChangeType.findViewById(R.id.typeBioText)).setTextColor(Color.parseColor("#000000"));

            popupChangeType.findViewById(R.id.bioTick).setVisibility(View.VISIBLE);

            //((TextView) popupChangeType.findViewById(R.id.currentHash)).setText("(current)");
            //((TextView) popupChangeType.findViewById(R.id.currentHash)).setVisibility(View.VISIBLE);

        } else {
            return;
        }

        if (typeOfData.equals(MagicType.CLIPBOARD)) {
            ((TextView) activity.findViewById(R.id.textTopClipboard)).setText("select type for this data");
            ((TextView) activity.findViewById(R.id.textTopClipboard)).setTextSize(9);
            ((TextView) activity.findViewById(R.id.textTopClipboard)).requestLayout();
            activity.findViewById(R.id.textTopClipboard).setVisibility(View.VISIBLE);
        } else {
            ((TextView) activity.findViewById(R.id.textTopHistory)).setText("select type for this data");
            ((TextView) activity.findViewById(R.id.textTopHistory)).setTextSize(9);
            ((TextView) activity.findViewById(R.id.textTopHistory)).requestLayout();
            activity.findViewById(R.id.textTopHistory).setVisibility(View.VISIBLE);
        }

        ClipboardEnum currentEnum = clipboard.getType();
        final ClipboardEnum[] clickBefore = {null};
        popupChangeType.setVisibility(View.VISIBLE);


        popupChangeType.findViewById(R.id.addressLinear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!currentEnum.equals(ClipboardEnum.ADDRESS) && (clickBefore[0] == null || !clickBefore[0].equals(ClipboardEnum.ADDRESS))) {
                    changeTypeUI(currentEnum, clickBefore);
                    ((ImageView) popupChangeType.findViewById(R.id.icnAddressType)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));

                    ((TextView) popupChangeType.findViewById(R.id.typeAddressText)).setTypeface(Typeface.DEFAULT);
                    ((TextView) popupChangeType.findViewById(R.id.typeAddressText)).setTextColor(ContextCompat.getColor(mainView.getContext(), R.color.gray_textView));
                    ((TextView) popupChangeType.findViewById(R.id.currentAddress)).setText("(New)");
                    ((TextView) popupChangeType.findViewById(R.id.currentAddress)).setTextColor(Color.parseColor("#f6a623"));
                    ((TextView) popupChangeType.findViewById(R.id.currentAddress)).setVisibility(View.VISIBLE);

                    clickBefore[0] = ClipboardEnum.ADDRESS;
                    clipboard.setType(ClipboardEnum.ADDRESS);
                    clipboard.getListContactsSearch().clear();
                    clipboard.setImageTypeClipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + activity.getResources().getResourcePackageName(R.drawable.icn_geo) + '/' + activity.getResources().getResourceTypeName(R.drawable.icn_geo) + '/' + activity.getResources().getResourceEntryName(R.drawable.icn_geo));
                    notifyItemChanged(clipboards.indexOf(clipboard));

                    /*if (typeOfData.equals(MagicType.CLIPBOARD))
                        EventBus.getDefault().post(new SaveClipboard());
                    else
                        EventBus.getDefault().post(new SaveHistory());*/

                    popupChangeType.setVisibility(View.GONE);
                    Toast.makeText(mainView.getContext(), "Data's type updated", Toast.LENGTH_SHORT).show();
                    if(popupEditClip.getVisibility() == View.VISIBLE || popupInfo.getVisibility() == View.VISIBLE){
                        updateIconPreview(clipboard);
                    }
                } else if (currentEnum.equals(ClipboardEnum.ADDRESS) && clickBefore[0] != null) {



                    restartPopupChange();
                    ((ImageView) popupChangeType.findViewById(R.id.icnAddressType)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));

                    ((TextView) popupChangeType.findViewById(R.id.typeAddressText)).setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                    ((TextView) popupChangeType.findViewById(R.id.typeAddressText)).setTextColor(Color.parseColor("#000000"));
                    ((TextView) popupChangeType.findViewById(R.id.currentAddress)).setText("(current)");
                    ((TextView) popupChangeType.findViewById(R.id.currentAddress)).setVisibility(View.VISIBLE);


                    clickBefore[0] = null;
                    clipboard.setType(ClipboardEnum.ADDRESS);
                    clipboard.setImageTypeClipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + activity.getResources().getResourcePackageName(R.drawable.icn_geo) + '/' + activity.getResources().getResourceTypeName(R.drawable.icn_geo) + '/' + activity.getResources().getResourceEntryName(R.drawable.icn_geo));
                    notifyItemChanged(clipboards.indexOf(clipboard));
                    /*if (typeOfData.equals(MagicType.CLIPBOARD))
                        EventBus.getDefault().post(new SaveClipboard());
                    else
                        EventBus.getDefault().post(new SaveHistory());*/
                }else{
                    popupChangeType.setVisibility(View.GONE);
                }



            }
        });

        popupChangeType.findViewById(R.id.bioLinear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!currentEnum.equals(ClipboardEnum.BIO_INTRO) && (clickBefore[0] == null || !clickBefore[0].equals(ClipboardEnum.BIO_INTRO))) {
                    changeTypeUI(currentEnum, clickBefore);
                    ((ImageView) popupChangeType.findViewById(R.id.icnBioType)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));

                    ((TextView) popupChangeType.findViewById(R.id.typeBioText)).setTypeface(Typeface.DEFAULT);
                    ((TextView) popupChangeType.findViewById(R.id.typeBioText)).setTextColor(ContextCompat.getColor(mainView.getContext(), R.color.gray_textView));
                    ((TextView) popupChangeType.findViewById(R.id.currentBio)).setText("(New)");
                    ((TextView) popupChangeType.findViewById(R.id.currentBio)).setTextColor(Color.parseColor("#f6a623"));
                    ((TextView) popupChangeType.findViewById(R.id.currentBio)).setVisibility(View.VISIBLE);

                    clickBefore[0] = ClipboardEnum.BIO_INTRO;
                    clipboard.setType(ClipboardEnum.BIO_INTRO);
                    clipboard.getListContactsSearch().clear();
                    clipboard.setImageTypeClipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + activity.getResources().getResourcePackageName(R.drawable.intro_n) + '/' + activity.getResources().getResourceTypeName(R.drawable.intro_n) + '/' + activity.getResources().getResourceEntryName(R.drawable.intro_n));
                    notifyItemChanged(clipboards.indexOf(clipboard));

                    /*if (typeOfData.equals(MagicType.CLIPBOARD))
                        EventBus.getDefault().post(new SaveClipboard());
                    else
                        EventBus.getDefault().post(new SaveHistory());*/

                    popupChangeType.setVisibility(View.GONE);
                    Toast.makeText(mainView.getContext(), "Data's type updated", Toast.LENGTH_SHORT).show();
                    if(popupEditClip.getVisibility() == View.VISIBLE || popupInfo.getVisibility() == View.VISIBLE){
                        updateIconPreview(clipboard);
                    }
                } else if (currentEnum.equals(ClipboardEnum.ADDRESS) && clickBefore[0] != null) {



                    restartPopupChange();
                    ((ImageView) popupChangeType.findViewById(R.id.icnBioType)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));

                    ((TextView) popupChangeType.findViewById(R.id.typeBioText)).setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                    ((TextView) popupChangeType.findViewById(R.id.typeBioText)).setTextColor(Color.parseColor("#000000"));
                    ((TextView) popupChangeType.findViewById(R.id.currentBio)).setText("(current)");
                    ((TextView) popupChangeType.findViewById(R.id.currentBio)).setVisibility(View.VISIBLE);


                    clickBefore[0] = null;
                    clipboard.setType(ClipboardEnum.BIO_INTRO);
                    clipboard.setImageTypeClipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + activity.getResources().getResourcePackageName(R.drawable.intro_n) + '/' + activity.getResources().getResourceTypeName(R.drawable.intro_n) + '/' + activity.getResources().getResourceEntryName(R.drawable.intro_n));
                    notifyItemChanged(clipboards.indexOf(clipboard));
                    /*if (typeOfData.equals(MagicType.CLIPBOARD))
                        EventBus.getDefault().post(new SaveClipboard());
                    else
                        EventBus.getDefault().post(new SaveHistory());*/
                }else{
                    popupChangeType.setVisibility(View.GONE);
                }



            }
        });

        popupChangeType.findViewById(R.id.birthLinear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!currentEnum.equals(ClipboardEnum.DATEOFBIRTH) && (clickBefore[0] == null || !clickBefore[0].equals(ClipboardEnum.DATEOFBIRTH))) {
                    changeTypeUI(currentEnum, clickBefore);
                    ((ImageView) popupChangeType.findViewById(R.id.icnBirthType)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));

                    ((TextView) popupChangeType.findViewById(R.id.typeDateText)).setTypeface(Typeface.DEFAULT);
                    ((TextView) popupChangeType.findViewById(R.id.typeDateText)).setTextColor(ContextCompat.getColor(mainView.getContext(), R.color.gray_textView));
                    ((TextView) popupChangeType.findViewById(R.id.currentDate)).setText("(New)");
                    ((TextView) popupChangeType.findViewById(R.id.currentDate)).setTextColor(Color.parseColor("#f6a623"));
                    ((TextView) popupChangeType.findViewById(R.id.currentDate)).setVisibility(View.VISIBLE);


                    clickBefore[0] = ClipboardEnum.DATEOFBIRTH;

                    clipboard.setType(ClipboardEnum.DATEOFBIRTH);
                    clipboard.getListContactsSearch().clear();
                    clipboard.setImageTypeClipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + activity.getResources().getResourcePackageName(R.drawable.gift_blue) + '/' + activity.getResources().getResourceTypeName(R.drawable.gift_blue) + '/' + activity.getResources().getResourceEntryName(R.drawable.gift_blue));
                    notifyItemChanged(clipboards.indexOf(clipboard));
                    /*if (typeOfData.equals(MagicType.CLIPBOARD))
                        EventBus.getDefault().post(new SaveClipboard());
                    else
                        EventBus.getDefault().post(new SaveHistory());*/
                    popupChangeType.setVisibility(View.GONE);
                    Toast.makeText(mainView.getContext(), "Data's type updated", Toast.LENGTH_SHORT).show();
                    if(popupEditClip.getVisibility() == View.VISIBLE || popupInfo.getVisibility() == View.VISIBLE){
                        updateIconPreview(clipboard);
                    }
                } else if (currentEnum.equals(ClipboardEnum.DATEOFBIRTH) && clickBefore[0] != null) {
                    restartPopupChange();
                    ((ImageView) popupChangeType.findViewById(R.id.icnBirthType)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));

                    clickBefore[0] = null;


                    ((TextView) popupChangeType.findViewById(R.id.typeDateText)).setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                    ((TextView) popupChangeType.findViewById(R.id.typeDateText)).setTextColor(ContextCompat.getColor(mainView.getContext(), R.color.gray_textView));
                    ((TextView) popupChangeType.findViewById(R.id.currentDate)).setText("(current)");
                    ((TextView) popupChangeType.findViewById(R.id.currentDate)).setVisibility(View.VISIBLE);

                    clipboard.setType(ClipboardEnum.DATEOFBIRTH);
                    clipboard.setImageTypeClipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + activity.getResources().getResourcePackageName(R.drawable.gift_blue) + '/' + activity.getResources().getResourceTypeName(R.drawable.gift_blue) + '/' + activity.getResources().getResourceEntryName(R.drawable.gift_blue));
                    notifyItemChanged(clipboards.indexOf(clipboard));
                    /*if (typeOfData.equals(MagicType.CLIPBOARD))
                        EventBus.getDefault().post(new SaveClipboard());
                    else
                        EventBus.getDefault().post(new SaveHistory());*/
                }else{
                    popupChangeType.setVisibility(View.GONE);
                }
            }
        });


        popupChangeType.findViewById(R.id.emailLinear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!currentEnum.equals(ClipboardEnum.EMAIL) && (clickBefore[0] == null || !clickBefore[0].equals(ClipboardEnum.EMAIL))) {
                    changeTypeUI(currentEnum, clickBefore);
                    ((ImageView) popupChangeType.findViewById(R.id.icnEmailType)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));

                    ((TextView) popupChangeType.findViewById(R.id.typeEmailText)).setTypeface(Typeface.DEFAULT);
                    ((TextView) popupChangeType.findViewById(R.id.typeEmailText)).setTextColor(ContextCompat.getColor(mainView.getContext(), R.color.gray_textView));
                    ((TextView) popupChangeType.findViewById(R.id.currentEmail)).setText("(New)");
                    ((TextView) popupChangeType.findViewById(R.id.currentEmail)).setTextColor(Color.parseColor("#f6a623"));
                    ((TextView) popupChangeType.findViewById(R.id.currentEmail)).setVisibility(View.VISIBLE);

                    clickBefore[0] = ClipboardEnum.EMAIL;

                    clipboard.setType(ClipboardEnum.EMAIL);
                    clipboard.getListContactsSearch().clear();
                    clipboard.setImageTypeClipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + activity.getResources().getResourcePackageName(R.drawable.icn_emails) + '/' + activity.getResources().getResourceTypeName(R.drawable.icn_emails) + '/' + activity.getResources().getResourceEntryName(R.drawable.icn_emails));
                    notifyItemChanged(clipboards.indexOf(clipboard));
                    /*if (typeOfData.equals(MagicType.CLIPBOARD))
                        EventBus.getDefault().post(new SaveClipboard());
                    else
                        EventBus.getDefault().post(new SaveHistory());*/
                    popupChangeType.setVisibility(View.GONE);
                    Toast.makeText(mainView.getContext(), "Data's type updated", Toast.LENGTH_SHORT).show();
                    if(popupEditClip.getVisibility() == View.VISIBLE || popupInfo.getVisibility() == View.VISIBLE){
                        updateIconPreview(clipboard);
                    }
                } else if (currentEnum.equals(ClipboardEnum.EMAIL) && clickBefore[0] != null) {
                    restartPopupChange();
                    ((ImageView) popupChangeType.findViewById(R.id.icnEmailType)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));

                    ((TextView) popupChangeType.findViewById(R.id.typeEmailText)).setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                    ((TextView) popupChangeType.findViewById(R.id.typeEmailText)).setTextColor(Color.parseColor("#000000"));
                    ((TextView) popupChangeType.findViewById(R.id.currentEmail)).setText("(current)");
                    ((TextView) popupChangeType.findViewById(R.id.currentEmail)).setVisibility(View.VISIBLE);

                    clickBefore[0] = null;
                    clipboard.setType(ClipboardEnum.EMAIL);
                    clipboard.setImageTypeClipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + activity.getResources().getResourcePackageName(R.drawable.icn_emails) + '/' + activity.getResources().getResourceTypeName(R.drawable.icn_emails) + '/' + activity.getResources().getResourceEntryName(R.drawable.icn_emails));
                    notifyItemChanged(clipboards.indexOf(clipboard));
                    /*if (typeOfData.equals(MagicType.CLIPBOARD))
                        EventBus.getDefault().post(new SaveClipboard());
                    else
                        EventBus.getDefault().post(new SaveHistory());*/
                }else{
                    popupChangeType.setVisibility(View.GONE);
                }
            }
        });


        popupChangeType.findViewById(R.id.nameLinear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!currentEnum.equals(ClipboardEnum.NAME) && (clickBefore[0] == null || !clickBefore[0].equals(ClipboardEnum.NAME))) {
                    changeTypeUI(currentEnum, clickBefore);
                    ((ImageView) popupChangeType.findViewById(R.id.icnProfileType)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));

                    ((TextView) popupChangeType.findViewById(R.id.typeNameText)).setTypeface(Typeface.DEFAULT);
                    ((TextView) popupChangeType.findViewById(R.id.typeNameText)).setTextColor(ContextCompat.getColor(mainView.getContext(), R.color.gray_textView));
                    ((TextView) popupChangeType.findViewById(R.id.currentName)).setText("(New)");
                    ((TextView) popupChangeType.findViewById(R.id.currentName)).setTextColor(Color.parseColor("#f6a623"));
                    ((TextView) popupChangeType.findViewById(R.id.currentName)).setVisibility(View.VISIBLE);

                    clickBefore[0] = ClipboardEnum.NAME;

                    clipboard.setType(ClipboardEnum.NAME);
                    clipboard.getListContactsSearch().clear();
                    clipboard.setImageTypeClipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + activity.getResources().getResourcePackageName(R.drawable.profile_blue) + '/' + activity.getResources().getResourceTypeName(R.drawable.profile_blue) + '/' + activity.getResources().getResourceEntryName(R.drawable.profile_blue));
                    notifyItemChanged(clipboards.indexOf(clipboard));
                    /*if (typeOfData.equals(MagicType.CLIPBOARD))
                        EventBus.getDefault().post(new SaveClipboard());
                    else
                        EventBus.getDefault().post(new SaveHistory());*/
                    popupChangeType.setVisibility(View.GONE);
                    Toast.makeText(mainView.getContext(), "Data's type updated", Toast.LENGTH_SHORT).show();
                    if(popupEditClip.getVisibility() == View.VISIBLE || popupInfo.getVisibility() == View.VISIBLE){
                        updateIconPreview(clipboard);
                    }
                } else if (currentEnum.equals(ClipboardEnum.NAME) && clickBefore[0] != null) {
                    restartPopupChange();
                    ((ImageView) popupChangeType.findViewById(R.id.icnProfileType)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));

                    clickBefore[0] = null;

                    ((TextView) popupChangeType.findViewById(R.id.typeNameText)).setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                    ((TextView) popupChangeType.findViewById(R.id.typeNameText)).setTextColor(Color.parseColor("#000000"));
                    ((TextView) popupChangeType.findViewById(R.id.currentName)).setText("(current)");
                    ((TextView) popupChangeType.findViewById(R.id.currentName)).setVisibility(View.VISIBLE);


                    clipboard.setType(ClipboardEnum.NAME);
                    clipboard.setImageTypeClipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + activity.getResources().getResourcePackageName(R.drawable.profile_blue) + '/' + activity.getResources().getResourceTypeName(R.drawable.profile_blue) + '/' + activity.getResources().getResourceEntryName(R.drawable.profile_blue));
                    notifyItemChanged(clipboards.indexOf(clipboard));
                    /*if (typeOfData.equals(MagicType.CLIPBOARD))
                        EventBus.getDefault().post(new SaveClipboard());
                    else
                        EventBus.getDefault().post(new SaveHistory());*/
                }else{
                    popupChangeType.setVisibility(View.GONE);
                }
            }
        });

        popupChangeType.findViewById(R.id.noteLinear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!currentEnum.equals(ClipboardEnum.NOTE) && (clickBefore[0] == null || !clickBefore[0].equals(ClipboardEnum.NOTE))) {
                    changeTypeUI(currentEnum, clickBefore);
                    ((ImageView) popupChangeType.findViewById(R.id.icnNoteType)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));
                            /*String name = "note  (New)";

                            final SpannableStringBuilder text = new SpannableStringBuilder(name);
                            final ForegroundColorSpan style = new ForegroundColorSpan(Color.parseColor("#f6a623"));
                            //StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
                            text.setSpan(style, 6, name.length(), Spannable.SPAN_COMPOSING);
                            //text.setSpan(bss, 6, name.length(), Spannable.SPAN_COMPOSING);
                            ((TextView) popupChangeType.findViewById(R.id.typeNoteText)).setText(text);

                            ((TextView) popupChangeType.findViewById(R.id.typeValueNote)).setVisibility(View.VISIBLE);
                            ((TextView) popupChangeType.findViewById(R.id.typeValueNote)).setText(clipboards.get(position).getValueCopy());
                            ((TextView) popupChangeType.findViewById(R.id.typeValueNote)).setTextColor(activity.getResources().getColor(R.color.primary));*/

                    ((TextView) popupChangeType.findViewById(R.id.typeNoteText)).setTypeface(Typeface.DEFAULT);
                    ((TextView) popupChangeType.findViewById(R.id.typeNoteText)).setTextColor(ContextCompat.getColor(mainView.getContext(), R.color.gray_textView));
                    ((TextView) popupChangeType.findViewById(R.id.currentNote)).setText("(New)");
                    ((TextView) popupChangeType.findViewById(R.id.currentNote)).setTextColor(Color.parseColor("#f6a623"));
                    ((TextView) popupChangeType.findViewById(R.id.currentNote)).setVisibility(View.VISIBLE);

                    clickBefore[0] = ClipboardEnum.NOTE;
                    clipboard.getListContactsSearch().clear();
                    clipboard.setType(ClipboardEnum.NOTE);
                    clipboard.setImageTypeClipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + activity.getResources().getResourcePackageName(R.drawable.icn_notes)
                            + '/' + activity.getResources().getResourceTypeName(R.drawable.icn_notes) + '/' + activity.getResources().getResourceEntryName(R.drawable.icn_notes));
                    notifyItemChanged(clipboards.indexOf(clipboard));
                    /*if (typeOfData.equals(MagicType.CLIPBOARD))
                        EventBus.getDefault().post(new SaveClipboard());
                    else
                        EventBus.getDefault().post(new SaveHistory());*/
                    popupChangeType.setVisibility(View.GONE);
                    Toast.makeText(mainView.getContext(), "Data's type updated", Toast.LENGTH_SHORT).show();
                    if(popupEditClip.getVisibility() == View.VISIBLE || popupInfo.getVisibility() == View.VISIBLE){
                        updateIconPreview(clipboard);
                    }
                } else if (currentEnum.equals(ClipboardEnum.NOTE) && clickBefore[0] != null) {
                    restartPopupChange();
                    ((ImageView) popupChangeType.findViewById(R.id.icnNoteType)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));
                            /*String name = "note  (current)";

                            final SpannableStringBuilder text = new SpannableStringBuilder(name);
                            final ForegroundColorSpan style = new ForegroundColorSpan(Color.parseColor("#000000"));
                            StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
                            text.setSpan(style, 0, 4, Spannable.SPAN_COMPOSING);
                            text.setSpan(bss, 0, 4, Spannable.SPAN_COMPOSING);
                            ((TextView) popupChangeType.findViewById(R.id.typeNoteText)).setText(text);*/
                    clickBefore[0] = null;

                    ((TextView) popupChangeType.findViewById(R.id.typeNoteText)).setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                    ((TextView) popupChangeType.findViewById(R.id.typeNoteText)).setTextColor(Color.parseColor("#000000"));
                    ((TextView) popupChangeType.findViewById(R.id.currentNote)).setText("(current)");
                    ((TextView) popupChangeType.findViewById(R.id.currentNote)).setVisibility(View.VISIBLE);

                           /* ((TextView) popupChangeType.findViewById(R.id.typeValueNote)).setVisibility(View.VISIBLE);
                            ((TextView) popupChangeType.findViewById(R.id.typeValueNote)).setText(clipboards.get(position).getValueCopy());
                            ((TextView) popupChangeType.findViewById(R.id.typeValueNote)).setTextColor(activity.getResources().getColor(R.color.primary));*/
                    clipboard.setType(ClipboardEnum.NOTE);
                    clipboard.setImageTypeClipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + activity.getResources().getResourcePackageName(R.drawable.icn_notes)
                            + '/' + activity.getResources().getResourceTypeName(R.drawable.icn_notes) + '/' + activity.getResources().getResourceEntryName(R.drawable.icn_notes));
                    notifyItemChanged(clipboards.indexOf(clipboard));
                    /*if (typeOfData.equals(MagicType.CLIPBOARD))
                        EventBus.getDefault().post(new SaveClipboard());
                    else
                        EventBus.getDefault().post(new SaveHistory());*/
                }else{
                    popupChangeType.setVisibility(View.GONE);
                }
            }
        });

        popupChangeType.findViewById(R.id.positionLinear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!currentEnum.equals(ClipboardEnum.POSITION) && (clickBefore[0] == null || !clickBefore[0].equals(ClipboardEnum.POSITION))) {
                    changeTypeUI(currentEnum, clickBefore);
                    ((ImageView) popupChangeType.findViewById(R.id.icnPositionType)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));
                            /*String name = "position  (New)";

                            final SpannableStringBuilder text = new SpannableStringBuilder(name);
                            final ForegroundColorSpan style = new ForegroundColorSpan(Color.parseColor("#f6a623"));
                            //StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
                            text.setSpan(style, 10, name.length(), Spannable.SPAN_COMPOSING);
                            //text.setSpan(bss, 10, name.length(), Spannable.SPAN_COMPOSING);
                            ((TextView) popupChangeType.findViewById(R.id.typePositionText)).setText(text);

                            ((TextView) popupChangeType.findViewById(R.id.typeValuePosition)).setVisibility(View.VISIBLE);
                            ((TextView) popupChangeType.findViewById(R.id.typeValuePosition)).setText(clipboards.get(position).getValueCopy());
                            ((TextView) popupChangeType.findViewById(R.id.typeValuePosition)).setTextColor(activity.getResources().getColor(R.color.primary));*/

                    ((TextView) popupChangeType.findViewById(R.id.typePositionText)).setTypeface(Typeface.DEFAULT);
                    ((TextView) popupChangeType.findViewById(R.id.typePositionText)).setTextColor(ContextCompat.getColor(mainView.getContext(), R.color.gray_textView));
                    ((TextView) popupChangeType.findViewById(R.id.currentPosition)).setText("(New)");
                    ((TextView) popupChangeType.findViewById(R.id.currentPosition)).setTextColor(Color.parseColor("#f6a623"));
                    ((TextView) popupChangeType.findViewById(R.id.currentPosition)).setVisibility(View.VISIBLE);

                    clickBefore[0] = ClipboardEnum.POSITION;
                    clipboard.setType(ClipboardEnum.POSITION);
                    clipboard.getListContactsSearch().clear();
                    clipboard.setImageTypeClipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + activity.getResources().getResourcePackageName(R.drawable.position_blue) + '/' + activity.getResources().getResourceTypeName(R.drawable.position_blue) + '/' + activity.getResources().getResourceEntryName(R.drawable.position_blue));
                    notifyItemChanged(clipboards.indexOf(clipboard));
                    /*if (typeOfData.equals(MagicType.CLIPBOARD))
                        EventBus.getDefault().post(new SaveClipboard());
                    else
                        EventBus.getDefault().post(new SaveHistory());*/
                    popupChangeType.setVisibility(View.GONE);
                    Toast.makeText(mainView.getContext(), "Data's type updated", Toast.LENGTH_SHORT).show();
                    if(popupEditClip.getVisibility() == View.VISIBLE || popupInfo.getVisibility() == View.VISIBLE){
                        updateIconPreview(clipboard);
                    }
                } else if (currentEnum.equals(ClipboardEnum.POSITION) && clickBefore[0] != null) {
                    restartPopupChange();
                    ((ImageView) popupChangeType.findViewById(R.id.icnPositionType)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));
                           /* String name = "position  (current)";

                            final SpannableStringBuilder text = new SpannableStringBuilder(name);
                            final ForegroundColorSpan style = new ForegroundColorSpan(Color.parseColor("#000000"));
                            StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
                            text.setSpan(style, 0, 8, Spannable.SPAN_COMPOSING);
                            text.setSpan(bss, 0, 8, Spannable.SPAN_COMPOSING);
                            ((TextView) popupChangeType.findViewById(R.id.typePositionText)).setText(text);*/

                    ((TextView) popupChangeType.findViewById(R.id.typePositionText)).setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                    ((TextView) popupChangeType.findViewById(R.id.typePositionText)).setTextColor(Color.parseColor("#000000"));
                    ((TextView) popupChangeType.findViewById(R.id.currentPosition)).setText("(current)");
                    ((TextView) popupChangeType.findViewById(R.id.currentPosition)).setVisibility(View.VISIBLE);

                    clickBefore[0] = null;
                            /*((TextView) popupChangeType.findViewById(R.id.typeValuePosition)).setVisibility(View.VISIBLE);
                            ((TextView) popupChangeType.findViewById(R.id.typeValuePosition)).setText(clipboards.get(position).getValueCopy());
                            ((TextView) popupChangeType.findViewById(R.id.typeValuePosition)).setTextColor(activity.getResources().getColor(R.color.primary));*/

                    clipboard.setType(ClipboardEnum.POSITION);
                    clipboard.setImageTypeClipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + activity.getResources().getResourcePackageName(R.drawable.position_blue) + '/' + activity.getResources().getResourceTypeName(R.drawable.position_blue) + '/' + activity.getResources().getResourceEntryName(R.drawable.position_blue));
                    notifyItemChanged(clipboards.indexOf(clipboard));
                    /*if (typeOfData.equals(MagicType.CLIPBOARD))
                        EventBus.getDefault().post(new SaveClipboard());
                    else
                        EventBus.getDefault().post(new SaveHistory());*/
                }else{
                    popupChangeType.setVisibility(View.GONE);
                }
            }
        });

        popupChangeType.findViewById(R.id.phoneLinear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!currentEnum.equals(ClipboardEnum.PHONE) && (clickBefore[0] == null || !clickBefore[0].equals(ClipboardEnum.PHONE))) {
                    changeTypeUI(currentEnum, clickBefore);
                    ((ImageView) popupChangeType.findViewById(R.id.icnPhoneType)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));
                            /*String name = "phone  (New)";

                            final SpannableStringBuilder text = new SpannableStringBuilder(name);
                            final ForegroundColorSpan style = new ForegroundColorSpan(Color.parseColor("#f6a623"));
                            //StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
                            text.setSpan(style, 7, name.length(), Spannable.SPAN_COMPOSING);
                            //text.setSpan(bss, 7, name.length(), Spannable.SPAN_COMPOSING);
                            ((TextView) popupChangeType.findViewById(R.id.typePhoneText)).setText(text);

                            ((TextView) popupChangeType.findViewById(R.id.typeValuePhone)).setVisibility(View.VISIBLE);
                            ((TextView) popupChangeType.findViewById(R.id.typeValuePhone)).setText(clipboards.get(position).getValueCopy());
                            ((TextView) popupChangeType.findViewById(R.id.typeValuePhone)).setTextColor(activity.getResources().getColor(R.color.primary));*/

                    ((TextView) popupChangeType.findViewById(R.id.typePhoneText)).setTypeface(Typeface.DEFAULT);
                    ((TextView) popupChangeType.findViewById(R.id.typePhoneText)).setTextColor(ContextCompat.getColor(mainView.getContext(), R.color.gray_textView));
                    ((TextView) popupChangeType.findViewById(R.id.currentPhone)).setText("(New)");
                    ((TextView) popupChangeType.findViewById(R.id.currentPhone)).setTextColor(Color.parseColor("#f6a623"));
                    ((TextView) popupChangeType.findViewById(R.id.currentPhone)).setVisibility(View.VISIBLE);

                    clickBefore[0] = ClipboardEnum.PHONE;

                    clipboard.setType(ClipboardEnum.PHONE);
                    clipboard.getListContactsSearch().clear();
                    clipboard.setImageTypeClipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + activity.getResources().getResourcePackageName(R.drawable.icn_phone) + '/' + activity.getResources().getResourceTypeName(R.drawable.icn_phone) + '/' + activity.getResources().getResourceEntryName(R.drawable.icn_phone));
                    notifyItemChanged(clipboards.indexOf(clipboard));
                    /*if (typeOfData.equals(MagicType.CLIPBOARD))
                        EventBus.getDefault().post(new SaveClipboard());
                    else
                        EventBus.getDefault().post(new SaveHistory());*/
                    popupChangeType.setVisibility(View.GONE);
                    Toast.makeText(mainView.getContext(), "Data's type updated", Toast.LENGTH_SHORT).show();
                    if(popupEditClip.getVisibility() == View.VISIBLE || popupInfo.getVisibility() == View.VISIBLE){
                        updateIconPreview(clipboard);
                    }
                } else if (currentEnum.equals(ClipboardEnum.PHONE) && clickBefore[0] != null) {
                    restartPopupChange();
                    ((ImageView) popupChangeType.findViewById(R.id.icnPhoneType)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));
                            /*String name = "phone  (current)";

                            final SpannableStringBuilder text = new SpannableStringBuilder(name);
                            final ForegroundColorSpan style = new ForegroundColorSpan(Color.parseColor("#000000"));
                            StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
                            text.setSpan(style, 0, 5, Spannable.SPAN_COMPOSING);
                            text.setSpan(bss, 0, 5, Spannable.SPAN_COMPOSING);
                            ((TextView) popupChangeType.findViewById(R.id.typePhoneText)).setText(text);*/
                    clickBefore[0] = null;

                    ((TextView) popupChangeType.findViewById(R.id.typePhoneText)).setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                    ((TextView) popupChangeType.findViewById(R.id.typePhoneText)).setTextColor(Color.parseColor("#000000"));
                    ((TextView) popupChangeType.findViewById(R.id.currentPhone)).setText("(current)");
                    ((TextView) popupChangeType.findViewById(R.id.currentPhone)).setVisibility(View.VISIBLE);

                           /* ((TextView) popupChangeType.findViewById(R.id.typeValuePhone)).setVisibility(View.VISIBLE);
                            ((TextView) popupChangeType.findViewById(R.id.typeValuePhone)).setText(clipboards.get(position).getValueCopy());
                            ((TextView) popupChangeType.findViewById(R.id.typeValuePhone)).setTextColor(activity.getResources().getColor(R.color.primary));*/
                    clipboard.setType(ClipboardEnum.PHONE);

                    clipboard.setImageTypeClipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + activity.getResources().getResourcePackageName(R.drawable.icn_phone) + '/' + activity.getResources().getResourceTypeName(R.drawable.icn_phone) + '/' + activity.getResources().getResourceEntryName(R.drawable.icn_phone));
                    notifyItemChanged(clipboards.indexOf(clipboard));
                    /*if (typeOfData.equals(MagicType.CLIPBOARD))
                        EventBus.getDefault().post(new SaveClipboard());
                    else
                        EventBus.getDefault().post(new SaveHistory());*/
                }else{
                    popupChangeType.setVisibility(View.GONE);
                }
            }
        });

        popupChangeType.findViewById(R.id.webLinear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!currentEnum.equals(ClipboardEnum.WEB) && (clickBefore[0] == null || !clickBefore[0].equals(ClipboardEnum.WEB))) {
                    changeTypeUI(currentEnum, clickBefore);
                    ((ImageView) popupChangeType.findViewById(R.id.icnWebType)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));
                            /*String name = "web  (New)";

                            final SpannableStringBuilder text = new SpannableStringBuilder(name);
                            final ForegroundColorSpan style = new ForegroundColorSpan(Color.parseColor("#f6a623"));
                            //StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
                            text.setSpan(style, 5, name.length(), Spannable.SPAN_COMPOSING);
                            //text.setSpan(bss, 5, name.length(), Spannable.SPAN_COMPOSING);
                            ((TextView) popupChangeType.findViewById(R.id.typeWebText)).setText(text);

                            ((TextView) popupChangeType.findViewById(R.id.typeValueWeb)).setVisibility(View.VISIBLE);
                            ((TextView) popupChangeType.findViewById(R.id.typeValueWeb)).setText(clipboards.get(position).getValueCopy());
                            ((TextView) popupChangeType.findViewById(R.id.typeValueWeb)).setTextColor(activity.getResources().getColor(R.color.primary));*/

                    ((TextView) popupChangeType.findViewById(R.id.typeWebText)).setTypeface(Typeface.DEFAULT);
                    ((TextView) popupChangeType.findViewById(R.id.typeWebText)).setTextColor(ContextCompat.getColor(mainView.getContext(), R.color.gray_textView));
                    ((TextView) popupChangeType.findViewById(R.id.currentWeb)).setText("(New)");
                    ((TextView) popupChangeType.findViewById(R.id.currentWeb)).setTextColor(Color.parseColor("#f6a623"));
                    ((TextView) popupChangeType.findViewById(R.id.currentWeb)).setVisibility(View.VISIBLE);


                    clickBefore[0] = ClipboardEnum.WEB;
                    clipboard.setType(ClipboardEnum.WEB);
                    clipboard.getListContactsSearch().clear();
                    clipboard.setImageTypeClipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + activity.getResources().getResourcePackageName(R.drawable.icn_popup_web) + '/' + activity.getResources().getResourceTypeName(R.drawable.icn_popup_web) + '/' + activity.getResources().getResourceEntryName(R.drawable.icn_popup_web));
                    notifyItemChanged(clipboards.indexOf(clipboard));
                    /*if (typeOfData.equals(MagicType.CLIPBOARD))
                        EventBus.getDefault().post(new SaveClipboard());
                    else
                        EventBus.getDefault().post(new SaveHistory());*/
                    popupChangeType.setVisibility(View.GONE);
                    Toast.makeText(mainView.getContext(), "Data's type updated", Toast.LENGTH_SHORT).show();
                    if(popupEditClip.getVisibility() == View.VISIBLE || popupInfo.getVisibility() == View.VISIBLE){
                        updateIconPreview(clipboard);
                    }
                } else if (currentEnum.equals(ClipboardEnum.WEB) && clickBefore[0] != null) {
                    restartPopupChange();
                    ((ImageView) popupChangeType.findViewById(R.id.icnWebType)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));
                            /*String name = "web  (current)";

                            final SpannableStringBuilder text = new SpannableStringBuilder(name);
                            final ForegroundColorSpan style = new ForegroundColorSpan(Color.parseColor("#000000"));
                            StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
                            text.setSpan(style, 0, 3, Spannable.SPAN_COMPOSING);
                            text.setSpan(bss, 0, 3, Spannable.SPAN_COMPOSING);
                            ((TextView) popupChangeType.findViewById(R.id.typeWebText)).setText(text);*/
                    clickBefore[0] = null;

                    ((TextView) popupChangeType.findViewById(R.id.typeWebText)).setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                    ((TextView) popupChangeType.findViewById(R.id.typeWebText)).setTextColor(Color.parseColor("#000000"));
                    ((TextView) popupChangeType.findViewById(R.id.currentWeb)).setText("(current)");
                    ((TextView) popupChangeType.findViewById(R.id.currentWeb)).setVisibility(View.VISIBLE);

                            /*((TextView) popupChangeType.findViewById(R.id.typeValueWeb)).setVisibility(View.VISIBLE);
                            ((TextView) popupChangeType.findViewById(R.id.typeValueWeb)).setText(clipboards.get(position).getValueCopy());
                            ((TextView) popupChangeType.findViewById(R.id.typeValueWeb)).setTextColor(activity.getResources().getColor(R.color.primary));*/

                    clipboard.setType(ClipboardEnum.WEB);
                    clipboard.setImageTypeClipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + activity.getResources().getResourcePackageName(R.drawable.icn_popup_web) + '/' + activity.getResources().getResourceTypeName(R.drawable.icn_popup_web) + '/' + activity.getResources().getResourceEntryName(R.drawable.icn_popup_web));
                    notifyItemChanged(clipboards.indexOf(clipboard));
                    /*if (typeOfData.equals(MagicType.CLIPBOARD))
                        EventBus.getDefault().post(new SaveClipboard());
                    else
                        EventBus.getDefault().post(new SaveHistory());*/
                }else{
                    popupChangeType.setVisibility(View.GONE);
                }
            }
        });


        popupChangeType.findViewById(R.id.companyLinear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!currentEnum.equals(ClipboardEnum.COMPANY) && (clickBefore[0] == null || !clickBefore[0].equals(ClipboardEnum.COMPANY))) {
                    changeTypeUI(currentEnum, clickBefore);
                    ((ImageView) popupChangeType.findViewById(R.id.icnCompanyType)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));
                            /*String name = "web  (New)";

                            final SpannableStringBuilder text = new SpannableStringBuilder(name);
                            final ForegroundColorSpan style = new ForegroundColorSpan(Color.parseColor("#f6a623"));
                            //StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
                            text.setSpan(style, 5, name.length(), Spannable.SPAN_COMPOSING);
                            //text.setSpan(bss, 5, name.length(), Spannable.SPAN_COMPOSING);
                            ((TextView) popupChangeType.findViewById(R.id.typeWebText)).setText(text);

                            ((TextView) popupChangeType.findViewById(R.id.typeValueWeb)).setVisibility(View.VISIBLE);
                            ((TextView) popupChangeType.findViewById(R.id.typeValueWeb)).setText(clipboards.get(position).getValueCopy());
                            ((TextView) popupChangeType.findViewById(R.id.typeValueWeb)).setTextColor(activity.getResources().getColor(R.color.primary));*/

                    ((TextView) popupChangeType.findViewById(R.id.typeCompanyText)).setTypeface(Typeface.DEFAULT);
                    ((TextView) popupChangeType.findViewById(R.id.typeCompanyText)).setTextColor(ContextCompat.getColor(mainView.getContext(), R.color.gray_textView));
                    ((TextView) popupChangeType.findViewById(R.id.currentCompany)).setText("(New)");
                    ((TextView) popupChangeType.findViewById(R.id.currentCompany)).setTextColor(Color.parseColor("#f6a623"));
                    ((TextView) popupChangeType.findViewById(R.id.currentCompany)).setVisibility(View.VISIBLE);


                    clickBefore[0] = ClipboardEnum.COMPANY;
                    clipboard.setType(ClipboardEnum.COMPANY);
                    clipboard.getListContactsSearch().clear();
                    clipboard.setImageTypeClipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + activity.getResources().getResourcePackageName(R.drawable.icn_companies) + '/' + activity.getResources().getResourceTypeName(R.drawable.icn_companies) + '/' + activity.getResources().getResourceEntryName(R.drawable.icn_companies));
                    notifyItemChanged(clipboards.indexOf(clipboard));
                    /*if (typeOfData.equals(MagicType.CLIPBOARD))
                        EventBus.getDefault().post(new SaveClipboard());
                    else
                        EventBus.getDefault().post(new SaveHistory());*/
                    popupChangeType.setVisibility(View.GONE);
                    Toast.makeText(mainView.getContext(), "Data's type updated", Toast.LENGTH_SHORT).show();
                    if(popupEditClip.getVisibility() == View.VISIBLE || popupInfo.getVisibility() == View.VISIBLE){
                        updateIconPreview(clipboard);
                    }
                } else if (currentEnum.equals(ClipboardEnum.COMPANY) && clickBefore[0] != null) {
                    restartPopupChange();
                    ((ImageView) popupChangeType.findViewById(R.id.icnCompanyType)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));
                            /*String name = "web  (current)";

                            final SpannableStringBuilder text = new SpannableStringBuilder(name);
                            final ForegroundColorSpan style = new ForegroundColorSpan(Color.parseColor("#000000"));
                            StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
                            text.setSpan(style, 0, 3, Spannable.SPAN_COMPOSING);
                            text.setSpan(bss, 0, 3, Spannable.SPAN_COMPOSING);
                            ((TextView) popupChangeType.findViewById(R.id.typeWebText)).setText(text);*/
                    clickBefore[0] = null;

                    ((TextView) popupChangeType.findViewById(R.id.typeCompanyText)).setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                    ((TextView) popupChangeType.findViewById(R.id.typeCompanyText)).setTextColor(Color.parseColor("#000000"));
                    ((TextView) popupChangeType.findViewById(R.id.currentCompany)).setText("(current)");
                    ((TextView) popupChangeType.findViewById(R.id.currentCompany)).setVisibility(View.VISIBLE);

                            /*((TextView) popupChangeType.findViewById(R.id.typeValueWeb)).setVisibility(View.VISIBLE);
                            ((TextView) popupChangeType.findViewById(R.id.typeValueWeb)).setText(clipboards.get(position).getValueCopy());
                            ((TextView) popupChangeType.findViewById(R.id.typeValueWeb)).setTextColor(activity.getResources().getColor(R.color.primary));*/

                    clipboard.setType(ClipboardEnum.COMPANY);
                    clipboard.setImageTypeClipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + activity.getResources().getResourcePackageName(R.drawable.icn_companies) + '/' + activity.getResources().getResourceTypeName(R.drawable.icn_companies) + '/' + activity.getResources().getResourceEntryName(R.drawable.icn_companies));
                    notifyItemChanged(clipboards.indexOf(clipboard));
                    /*if (typeOfData.equals(MagicType.CLIPBOARD))
                        EventBus.getDefault().post(new SaveClipboard());
                    else
                        EventBus.getDefault().post(new SaveHistory());*/
                }else{
                    popupChangeType.setVisibility(View.GONE);
                }
            }
        });


        popupChangeType.findViewById(R.id.hashLinear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!currentEnum.equals(ClipboardEnum.HASHTAG) && (clickBefore[0] == null || !clickBefore[0].equals(ClipboardEnum.HASHTAG))) {
                    changeTypeUI(currentEnum, clickBefore);
                    ((ImageView) popupChangeType.findViewById(R.id.icnHashType)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));
                            /*String name = "web  (New)";

                            final SpannableStringBuilder text = new SpannableStringBuilder(name);
                            final ForegroundColorSpan style = new ForegroundColorSpan(Color.parseColor("#f6a623"));
                            //StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
                            text.setSpan(style, 5, name.length(), Spannable.SPAN_COMPOSING);
                            //text.setSpan(bss, 5, name.length(), Spannable.SPAN_COMPOSING);
                            ((TextView) popupChangeType.findViewById(R.id.typeWebText)).setText(text);

                            ((TextView) popupChangeType.findViewById(R.id.typeValueWeb)).setVisibility(View.VISIBLE);
                            ((TextView) popupChangeType.findViewById(R.id.typeValueWeb)).setText(clipboards.get(position).getValueCopy());
                            ((TextView) popupChangeType.findViewById(R.id.typeValueWeb)).setTextColor(activity.getResources().getColor(R.color.primary));*/

                    ((TextView) popupChangeType.findViewById(R.id.typeHashText)).setTypeface(Typeface.DEFAULT);
                    ((TextView) popupChangeType.findViewById(R.id.typeHashText)).setTextColor(ContextCompat.getColor(mainView.getContext(), R.color.gray_textView));
                    ((TextView) popupChangeType.findViewById(R.id.currentHash)).setText("(New)");
                    ((TextView) popupChangeType.findViewById(R.id.currentHash)).setTextColor(Color.parseColor("#f6a623"));
                    ((TextView) popupChangeType.findViewById(R.id.currentHash)).setVisibility(View.VISIBLE);


                    clickBefore[0] = ClipboardEnum.HASHTAG;
                    clipboard.setType(ClipboardEnum.HASHTAG);
                    clipboard.getListContactsSearch().clear();
                    clipboard.setImageTypeClipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + activity.getResources().getResourcePackageName(R.drawable.icn_sort_hashtah) + '/' + activity.getResources().getResourceTypeName(R.drawable.icn_sort_hashtah) + '/' + activity.getResources().getResourceEntryName(R.drawable.icn_sort_hashtah));
                    notifyItemChanged(clipboards.indexOf(clipboard));
                    /*if (typeOfData.equals(MagicType.CLIPBOARD))
                        EventBus.getDefault().post(new SaveClipboard());
                    else
                        EventBus.getDefault().post(new SaveHistory());*/
                    popupChangeType.setVisibility(View.GONE);
                    Toast.makeText(mainView.getContext(), "Data's type updated", Toast.LENGTH_SHORT).show();
                    if(popupEditClip.getVisibility() == View.VISIBLE || popupInfo.getVisibility() == View.VISIBLE){
                        updateIconPreview(clipboard);
                    }

                } else if (currentEnum.equals(ClipboardEnum.HASHTAG) && clickBefore[0] != null) {
                    restartPopupChange();
                    ((ImageView) popupChangeType.findViewById(R.id.icnHashType)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));
                            /*String name = "web  (current)";

                            final SpannableStringBuilder text = new SpannableStringBuilder(name);
                            final ForegroundColorSpan style = new ForegroundColorSpan(Color.parseColor("#000000"));
                            StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
                            text.setSpan(style, 0, 3, Spannable.SPAN_COMPOSING);
                            text.setSpan(bss, 0, 3, Spannable.SPAN_COMPOSING);
                            ((TextView) popupChangeType.findViewById(R.id.typeWebText)).setText(text);*/
                    clickBefore[0] = null;

                    ((TextView) popupChangeType.findViewById(R.id.typeHashText)).setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                    ((TextView) popupChangeType.findViewById(R.id.typeHashText)).setTextColor(Color.parseColor("#000000"));
                    ((TextView) popupChangeType.findViewById(R.id.currentHash)).setText("(current)");
                    ((TextView) popupChangeType.findViewById(R.id.currentHash)).setVisibility(View.VISIBLE);

                            /*((TextView) popupChangeType.findViewById(R.id.typeValueWeb)).setVisibility(View.VISIBLE);
                            ((TextView) popupChangeType.findViewById(R.id.typeValueWeb)).setText(clipboards.get(position).getValueCopy());
                            ((TextView) popupChangeType.findViewById(R.id.typeValueWeb)).setTextColor(activity.getResources().getColor(R.color.primary));*/

                    clipboard.setType(ClipboardEnum.HASHTAG);
                    clipboard.setImageTypeClipboard(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + activity.getResources().getResourcePackageName(R.drawable.icn_sort_hashtah) + '/' + activity.getResources().getResourceTypeName(R.drawable.icn_sort_hashtah) + '/' + activity.getResources().getResourceEntryName(R.drawable.icn_sort_hashtah));
                    notifyItemChanged(clipboards.indexOf(clipboard));
                    /*if (typeOfData.equals(MagicType.CLIPBOARD))
                        EventBus.getDefault().post(new SaveClipboard());
                    else
                        EventBus.getDefault().post(new SaveHistory());*/
                }else{
                    popupChangeType.setVisibility(View.GONE);
                }
            }
        });
    }


    public ArrayList<Clipboard> getClipbaordList() {

        ArrayList<Clipboard> listReturn = new ArrayList<>();
        for (int i = 0; i < checkHashClip.size(); i++) {
            listReturn.add(checkHashClip.get(i));
        }
        return listReturn;
    }

    public void clearCheck() {
        checkHashClip.clear();
    }

    public ArrayList<Clipboard> getClipOpen() {
        return listOpen;
    }

    public ArrayList<Clipboard> getClipboards() {
        return clipboards;
    }

    @Override
    public int getItemCount() {
        return clipboards.size();
    }

    public void openGlobalPopup(Clipboard clipboard) {

        FrameLayout globalFrame = globalSpopup;

        ((TextView) globalFrame.findViewById(R.id.globalLinkedValue)).setText(clipboard.getValueCopy());
        ((TextView) globalFrame.findViewById(R.id.globalGoogleValue)).setText(clipboard.getValueCopy());
        ((TextView) globalFrame.findViewById(R.id.globalExtimeValue)).setText(clipboard.getValueCopy());

        if (clipboard.getListContactsSearch() != null && clipboard.getListContactsSearch().size() > 0) {
            globalFrame.findViewById(R.id.globalExtimeDataFound).setVisibility(View.VISIBLE);
            globalFrame.findViewById(R.id.globalExtimeSearch).setVisibility(View.GONE);
            globalFrame.findViewById(R.id.globalExtimeProfileFrame).setVisibility(View.VISIBLE);

            try {
                globalFrame.findViewById(R.id.globalCompanyPhotoIcon).setVisibility(View.GONE);
                globalFrame.findViewById(R.id.globalIconAvatar).setVisibility(View.VISIBLE);
                ((CircleImageView) globalFrame.findViewById(R.id.globalIconAvatar)).setImageURI(null);

                if (clipboard.getListContactsSearch().get(0).getPhotoURL() != null)
                    ((CircleImageView) globalFrame.findViewById(R.id.globalIconAvatar)).setImageURI(Uri.parse(clipboard.getListContactsSearch().get(0).getPhotoURL()));

                if (clipboard.getListContactsSearch().get(0).getPhotoURL() == null /*|| (holder.circleImageView.getDrawable()) == null*/) {

                    GradientDrawable circle = (GradientDrawable) ContextCompat.getDrawable(activity, R.drawable.blue_circle).mutate();
                    circle.setColor(clipboard.getListContactsSearch().get(0).color);
                    globalFrame.findViewById(R.id.globalIconAvatar).setBackground(circle);
                    ((CircleImageView) globalFrame.findViewById(R.id.globalIconAvatar)).setImageDrawable(null);
                    String initials = getInitialsByName(clipboard.getListContactsSearch().get(0).getName());
                    ((TextView) globalFrame.findViewById(R.id.globalIconInitials)).setText(initials);

                    if (!clipboard.getListContactsSearch().get(0).contact) {
                        globalFrame.findViewById(R.id.globalCompanyPhotoIcon).setVisibility(View.VISIBLE);
                        globalFrame.findViewById(R.id.globalIconAvatar).setVisibility(View.GONE);
                        globalFrame.findViewById(R.id.globalCompanyPhotoIcon).setBackgroundColor(clipboard.getListContactsSearch().get(0).color);
                    }
                }
            } catch (IllegalStateException e) {

                /*clipboard.getListContactsSearch().remove(0);
                holder.circleImageView.setVisibility(View.GONE);
                ((TextView) activity.findViewById(R.id.statClipboard)).setText(getCountCLipboards() + " entries, " + getCountDetectedClipboards() + " detected");*/
            }

        } else {
            globalFrame.findViewById(R.id.globalExtimeDataFound).setVisibility(View.GONE);
            globalFrame.findViewById(R.id.globalExtimeSearch).setVisibility(View.VISIBLE);
            globalFrame.findViewById(R.id.globalExtimeProfileFrame).setVisibility(View.GONE);
        }


        globalFrame.findViewById(R.id.globalGoogleSearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                String term = clipboard.getValueCopy();
                intent.putExtra(SearchManager.QUERY, term);
                activity.startActivity(intent);
            }
        });

        globalFrame.findViewById(R.id.globalLinkedSearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = clipboard.getValueCopy();
                if (name == null || name.trim().isEmpty()) return;

                String[] mach = name.split(" ");

                Intent i = new Intent(Intent.ACTION_VIEW);
                if (mach.length >= 2) {
                    String str = "";
                    for (int i2 = 0; i2 < mach.length; i2++) {
                        str += mach[i2];
                        if (i2 != mach.length - 1) str += "%20";
                    }
                    //   if (clipboard.getType() != ClipboardEnum.COMPANY)
                    i.setData(Uri.parse("https://www.linkedin.com/mwlite/search/results/all/?keywords=" + str));
                    //   else
                    //       i.setData(Uri.parse("https://www.linkedin.com/search/results/companies/v2/?keywords=" + str));
                } else {
                    //   if (clipboard.getType() != ClipboardEnum.COMPANY)
                    i.setData(Uri.parse("https://www.linkedin.com/mwlite/search/results/all/?keywords=" + mach[0]));
                    //   else
                    //       i.setData(Uri.parse("https://www.linkedin.com/search/results/companies/v2/?keywords=" + mach[0]));
                }

                activity.startActivity(i);
            }
        });

        globalFrame.findViewById(R.id.globalIconAvatar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //EventBus.getDefault().post(new PopupInProfile(clipboard.getListContactsSearch().get(0)));
            }
        });

        globalFrame.findViewById(R.id.globalCompanyPhotoIcon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //EventBus.getDefault().post(new PopupCompanyInProfile(clipboard.getListContactsSearch().get(0)));
            }
        });


        //globalFrame.setVisibility(View.VISIBLE);

        ((MainActivity) activity).showGlobal();
    }


    class LoadContact extends AsyncTask<Void, Void, String> {

        ClipboardViewHolder holder;
        Contact c;
        Clipboard clipboard;

        public LoadContact(final ClipboardViewHolder holder, Contact contact, Clipboard clipboard) {
            this.holder = holder;
            this.c = contact;
            this.clipboard = clipboard;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(Void... params) {

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

        }
    }


    public class ParsingSocialClipboard extends AsyncTask<Void, Void, Void> {

        private SocialEnums socialEnums;
        private String way;
        private String name = null;
        private FrameLayout frameLayout;
        private Clipboard clipboard;
        private String allWayLink;
        private ExtratorAdapter extratorAdapter;

        public ParsingSocialClipboard(Clipboard clipboard, FrameLayout frameLayout, ExtratorAdapter extratorAdapter) {
            this.way = clipboard.getValueCopy();
            this.clipboard = clipboard;
            //this.socialEnums = socialEnums;
            this.frameLayout = frameLayout;
            this.extratorAdapter = extratorAdapter;
        }

        @Override
        protected void onPreExecute() {

            frameLayout.findViewById(R.id.textCountExt).setVisibility(View.INVISIBLE);
            ((TextView) frameLayout.findViewById(R.id.textSearchClipboard)).setText("Reading link, please wait...");
            ((TextView) frameLayout.findViewById(R.id.textSearchClipboard)).setTextColor(mainView.getContext().getResources().getColor(R.color.gray_textView));
            ((TextView) frameLayout.findViewById(R.id.buttonExtract)).setTextColor(mainView.getContext().getResources().getColor(R.color.gray));
           /* getActivity().findViewById(R.id.framePopupSocial).setVisibility(View.VISIBLE);
            getActivity().findViewById(R.id.textDataExistsSocial).setVisibility(View.GONE);
            getActivity().findViewById(R.id.nameContactExtractFind).setVisibility(View.GONE);
            getActivity().findViewById(R.id.CompanyPositionExtract).setVisibility(View.GONE);*/


            //showWaitText(socialEnums);

            /*if (getActivity().findViewById(R.id.clipboardContainer).getVisibility() == View.VISIBLE) {
                getActivity().findViewById(R.id.clipboardContainer).setVisibility(View.GONE);
                getActivity().findViewById(R.id.fabMenuContainer).setVisibility(View.GONE);
            }*/
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... voids) {

            int ind = way.indexOf("https://");

            if (ind == -1)
                ind = way.indexOf("http://");

            if (ind == -1)
                way = "https://" + way;

            allWayLink = way;
            try {
                name = Jsoup.connect(way).timeout(7000).get().title();

            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e2) {
                e2.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //hideWaitText();

            if (name != null && !name.isEmpty()) {
                int index = name.length();
                if (clipboard.getType().equals(ClipboardEnum.INSTAGRAM)) {
                    index = name.indexOf('|');
                } else if (clipboard.getType().equals(ClipboardEnum.VK)) {
                    index = name.indexOf('|');
                    //((ImageView) getActivity().findViewById(R.id.imageSocialExtract)).setImageDrawable(getResources().getDrawable(R.drawable.icn_social_vk));
                    //((TextView) getActivity().findViewById(R.id.nameSocialExtract)).setText("Vk");
                    //extractEnumsToExtractContainer = ExtractEnums.VK;

                } else if (clipboard.getType().equals(ClipboardEnum.FACEBOOK)) {
                    index = name.indexOf('|');
                    //((ImageView) getActivity().findViewById(R.id.imageSocialExtract)).setImageDrawable(getResources().getDrawable(R.drawable.icn_social_facebook));
                    //((TextView) getActivity().findViewById(R.id.nameSocialExtract)).setText("Facebook");
                    //extractEnumsToExtractContainer = ExtractEnums.FACEBOOK;
                } else if (clipboard.getType().equals(ClipboardEnum.LINKEDIN)) {
                    //((ImageView) getActivity().findViewById(R.id.imageSocialExtract)).setImageDrawable(getResources().getDrawable(R.drawable.icn_social_linkedin));
                    //((TextView) getActivity().findViewById(R.id.nameSocialExtract)).setText("LinkedIn");
                    //extractEnumsToExtractContainer = ExtractEnums.LINKEDIN;
                } else if (clipboard.getType().equals(ClipboardEnum.TWITTER)) {
                    index = name.indexOf('|');
                    //((ImageView) getActivity().findViewById(R.id.imageSocialExtract)).setImageDrawable(getResources().getDrawable(R.drawable.ic_twitter_48));
                    //((TextView) getActivity().findViewById(R.id.nameSocialExtract)).setText("Twitter");
                    //extractEnumsToExtractContainer = ExtractEnums.TWITTER;
                } else if (clipboard.getType().equals(ClipboardEnum.YOUTUBE)) {
                    index = name.lastIndexOf('-');
                    //((ImageView) getActivity().findViewById(R.id.imageSocialExtract)).setImageDrawable(getResources().getDrawable(R.drawable.ic_youtube_48));
                    //((TextView) getActivity().findViewById(R.id.nameSocialExtract)).setText("Youtube");
                    //extractEnumsToExtractContainer = ExtractEnums.YOUTUBE;
                } else if (clipboard.getType().equals(ClipboardEnum.MEDIUM)) {
                    index = name.indexOf('–');
                    //((ImageView) getActivity().findViewById(R.id.imageSocialExtract)).setImageDrawable(getResources().getDrawable(R.drawable.medium_size_64));
                    //((TextView) getActivity().findViewById(R.id.nameSocialExtract)).setText("Medium");
                    //extractEnumsToExtractContainer = ExtractEnums.MEDIUM;
                } else if (clipboard.getType().equals(ClipboardEnum.TUMBLR)) {
                    index = name.indexOf('|');

                } else if (clipboard.getType().equals(ClipboardEnum.G_SHEET)) {
                    index = name.indexOf('-');

                }
                if (index == -1)
                    index = name.length();




                ArrayList<Extrator> listExtract = new ArrayList<>();
                String usernameLink = "Error!";
                //String name = "Error!";
                name = name.substring(0, index - 1);
                ExtractEnums extractEnums = null;
                try {
                    //===============================================================================CUT LINK!
                    int index2 = name.length();
                    if (clipboard.getType().equals(ClipboardEnum.INSTAGRAM)) {
                        extractEnums = ExtractEnums.INSTAGRAM;
                        index2 = name.indexOf('@');
                        if (index2 != -1 && index2 != 0) {
                            usernameLink = name.substring(index2, name.length() - 1);
                            if (usernameLink.contains("•")) {
                                int ind2 = usernameLink.indexOf('•');
                                usernameLink = usernameLink.substring(0, ind2 - 2);
                            }

                            index2 -= 2;
                            name = name.substring(0, index2).trim();
                        } else {
                            index2 = name.indexOf('•');
                            if (index2 != -1) {
                                usernameLink = name.substring(0, index2 - 1);

                                name = name.substring(0, index2 - 1).trim();
                                index2 -= 2;
                            }
                        }
                    } else if (clipboard.getType().equals(ClipboardEnum.VK)) {
                        extractEnums = ExtractEnums.VK;
                        index2 = allWayLink.indexOf(".com/");
                        usernameLink = allWayLink.toString().substring(index2 + 5, allWayLink.length());
                        //name = name;
                    } else if (clipboard.getType().equals(ClipboardEnum.FACEBOOK)) {
                        extractEnums = ExtractEnums.FACEBOOK;
                        //name = CreateFragment.nameContactExtractFromSocial;
                        if (name.contains("(") && name.contains(")")) {
                            int ind = name.indexOf("(");
                            name = name.substring(0, ind - 1);
                        }
                        index2 = allWayLink.lastIndexOf(".com/");
                        if (allWayLink.contains("people")) {
                            index2 = allWayLink.lastIndexOf("/");
                            usernameLink = "id" + allWayLink.toString().substring(index2 + 5, allWayLink.length());
                        } else {
                            usernameLink = allWayLink.toString().substring(index2 + 5, allWayLink.length());
                            if (usernameLink.contains("/")) {
                                usernameLink = usernameLink.substring(0, usernameLink.indexOf('/'));
                            }
                        }

                    } else if (clipboard.getType().equals(ClipboardEnum.LINKEDIN)) {
                        extractEnums = ExtractEnums.LINKEDIN;
                        //name = CreateFragment.nameContactExtractFromSocial;
                        usernameLink = allWayLink;
                    } else if (clipboard.getType().equals(ClipboardEnum.TWITTER)) {
                        extractEnums = ExtractEnums.TWITTER;
                        index2 = name.indexOf('@');
                        if (index2 != -1) {
                            usernameLink = name.substring(index2, name.length() - 1);

                            index2 -= 2;
                            name = name.substring(0, index2).trim();
                        } /*else if (CreateFragment.nameContactExtractFromSocial != null && !CreateFragment.nameContactExtractFromSocial.isEmpty()) {
                            name = CreateFragment.nameContactExtractFromSocial;
                        }*/
                    } else if (clipboard.getType().equals(ClipboardEnum.YOUTUBE)) {
                        extractEnums = ExtractEnums.YOUTUBE;
                        //name = CreateFragment.nameContactExtractFromSocial;
                        usernameLink = allWayLink;
                    } else if (clipboard.getType().equals(ClipboardEnum.MEDIUM)) {
                        extractEnums = ExtractEnums.MEDIUM;
                        if (allWayLink.contains(".com/@")) {
                            int in = allWayLink.indexOf(".com/@") + 5;
                            usernameLink = allWayLink.substring(in);
                        } else usernameLink = allWayLink;
                    } else if (clipboard.getType().equals(ClipboardEnum.WEB)) {
                        extractEnums = ExtractEnums.WEB;
                        usernameLink = allWayLink;
                    } else if (clipboard.getType().equals(ClipboardEnum.GITHUB)) {
                        extractEnums = ExtractEnums.GITHUB;
                        usernameLink = allWayLink;
                    } else if (clipboard.getType().equals(ClipboardEnum.ANGEL)) {
                        extractEnums = ExtractEnums.ANGEL;
                        usernameLink = allWayLink;
                    } else if (clipboard.getType().equals(ClipboardEnum.TUMBLR)) {
                        extractEnums = ExtractEnums.TUMBL;
                        usernameLink = allWayLink;
                    } else if (clipboard.getType().equals(ClipboardEnum.G_DOC)) {
                        extractEnums = ExtractEnums.G_DOC;
                        usernameLink = allWayLink;
                    } else if (clipboard.getType().equals(ClipboardEnum.G_SHEET)) {
                        extractEnums = ExtractEnums.G_SHEET;
                        usernameLink = allWayLink;
                    }
                    //if (index2 < 0)
                    //index2 = CreateFragment.nameContactExtractFromSocial.length();
                    //=====================================================================================
                } catch (Exception e) {
                    e.printStackTrace();
                }
                listExtract.add(new Extrator(ExtractEnums.NAME, name.trim(), null));
                //listExtract.add(new Extrator(extractEnumsToExtractContainer, ((EditText) getActivity().findViewById(R.id.magic_edit_text)).getText().toString()));
                listExtract.add(new Extrator(extractEnums, usernameLink, allWayLink));


                extratorAdapter.updateList(listExtract);

                ((TextView) frameLayout.findViewById(R.id.textSearchClipboard)).setText(allWayLink);
                ((TextView) frameLayout.findViewById(R.id.textSearchClipboard)).setTextColor(mainView.getContext().getResources().getColor(R.color.primary));
                ((TextView) frameLayout.findViewById(R.id.buttonExtract)).setTextColor(mainView.getContext().getResources().getColor(R.color.primary));

            } else {

                ((TextView) frameLayout.findViewById(R.id.textSearchClipboard)).setText("Failed to get data.");
                //frameLayout.setVisibility(View.GONE);
                this.cancel(true);
            }
            super.onPostExecute(result);

        }





    }


    public class ParsingURLClipboard extends AsyncTask<Void, Void, Void> {

        private SocialEnums socialEnums;
        private String way;
        private String name = null;
        private LinearLayout frameLayout, frameEdit;
        private Clipboard clipboard;
        private String allWayLink;


        public ParsingURLClipboard(Clipboard clipboard, LinearLayout frameLayout, LinearLayout frameEdit) {
            this.way = clipboard.getValueCopy();
            this.clipboard = clipboard;
            this.frameLayout = frameLayout;
            this.frameEdit = frameEdit;

        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPreExecute() {

            //frameLayout.findViewById(R.id.textCountExt).setVisibility(View.INVISIBLE);
            ((TextView) frameLayout.findViewById(R.id.textParseURL)).setText("Reading link, please wait...");
            ((TextView) frameLayout.findViewById(R.id.textParseURL)).requestLayout();
            ((TextView) frameLayout.findViewById(R.id.textParseURL)).setTextColor(mainView.getContext().getResources().getColor(R.color.gray_textView));

            ((TextView) frameEdit.findViewById(R.id.textParseURLEdit)).setText("Reading link, please wait...");
            ((TextView) frameEdit.findViewById(R.id.textParseURLEdit)).requestLayout();
            ((TextView) frameEdit.findViewById(R.id.textParseURLEdit)).setTextColor(mainView.getContext().getResources().getColor(R.color.gray_textView));
            //((TextView) frameLayout.findViewById(R.id.buttonExtract)).setTextColor(mainView.getContext().getResources().getColor(R.color.gray));

            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... voids) {
            Document doc = null;


            int ind = way.indexOf("https://");

            if (ind == -1)
                ind = way.indexOf("http://");

            if (ind == -1)
                way = "https://" + way;

            allWayLink = way;

            try {
                name = Jsoup.connect(way).timeout(7000).get().title();

            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e2) {
                e2.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            frameLayout.findViewById(R.id.frame_send_messageInfo_clipboard_parse).setVisibility(View.GONE);

            if (name != null && !name.isEmpty()) {
                int index = name.length();
                if (clipboard.getType().equals(ClipboardEnum.INSTAGRAM)) {
                    index = name.indexOf('|');
                } else if (clipboard.getType().equals(ClipboardEnum.VK)) {
                    index = name.indexOf('|');


                } else if (clipboard.getType().equals(ClipboardEnum.FACEBOOK)) {
                    index = name.indexOf('|');

                } else if (clipboard.getType().equals(ClipboardEnum.LINKEDIN)) {

                } else if (clipboard.getType().equals(ClipboardEnum.TWITTER)) {
                    index = name.indexOf('|');

                } else if (clipboard.getType().equals(ClipboardEnum.YOUTUBE)) {
                    index = name.lastIndexOf('-');

                } else if (clipboard.getType().equals(ClipboardEnum.MEDIUM)) {
                    index = name.indexOf('–');

                } else if (clipboard.getType().equals(ClipboardEnum.TUMBLR)) {
                    index = name.indexOf('|');

                } else if (clipboard.getType().equals(ClipboardEnum.G_SHEET)) {
                    index = name.indexOf('-');

                }


                if (index == -1)
                    index = name.length();




                ArrayList<Extrator> listExtract = new ArrayList<>();
                String usernameLink = "Error!";

                name = name.substring(0, index - 1);
                ExtractEnums extractEnums = null;
                try {
                    //===============================================================================CUT LINK!
                    int index2 = name.length();
                    if (clipboard.getType().equals(ClipboardEnum.INSTAGRAM)) {
                        extractEnums = ExtractEnums.INSTAGRAM;
                        index2 = name.indexOf('@');
                        if (index2 != -1 && index2 != 0) {
                            usernameLink = name.substring(index2, name.length() - 1);
                            if (usernameLink.contains("•")) {
                                int ind2 = usernameLink.indexOf('•');
                                usernameLink = usernameLink.substring(0, ind2 - 2);
                            }

                            index2 -= 2;
                            name = name.substring(0, index2).trim();
                        } else {
                            index2 = name.indexOf('•');
                            if (index2 != -1) {
                                usernameLink = name.substring(0, index2 - 1);

                                name = name.substring(0, index2 - 1).trim();
                                index2 -= 2;
                            }
                        }
                    } else if (clipboard.getType().equals(ClipboardEnum.VK)) {
                        extractEnums = ExtractEnums.VK;
                        index2 = allWayLink.indexOf(".com/");
                        usernameLink = allWayLink.toString().substring(index2 + 5, allWayLink.length());

                    } else if (clipboard.getType().equals(ClipboardEnum.FACEBOOK)) {
                        extractEnums = ExtractEnums.FACEBOOK;
                        //name = CreateFragment.nameContactExtractFromSocial;
                        if (name.contains("(") && name.contains(")")) {
                            int ind = name.indexOf("(");
                            name = name.substring(0, ind - 1);
                        }
                        index2 = allWayLink.lastIndexOf(".com/");
                        if (allWayLink.contains("people")) {
                            index2 = allWayLink.lastIndexOf("/");
                            usernameLink = "id" + allWayLink.toString().substring(index2 + 5, allWayLink.length());
                        } else {
                            usernameLink = allWayLink.toString().substring(index2 + 5, allWayLink.length());
                            if (usernameLink.contains("/")) {
                                usernameLink = usernameLink.substring(0, usernameLink.indexOf('/'));
                            }
                        }

                    } else if (clipboard.getType().equals(ClipboardEnum.LINKEDIN)) {
                        extractEnums = ExtractEnums.LINKEDIN;

                        usernameLink = allWayLink;
                    } else if (clipboard.getType().equals(ClipboardEnum.TWITTER)) {
                        extractEnums = ExtractEnums.TWITTER;
                        index2 = name.indexOf('@');
                        if (index2 != -1) {
                            usernameLink = name.substring(index2, name.length() - 1);

                            index2 -= 2;
                            name = name.substring(0, index2).trim();
                        }
                    } else if (clipboard.getType().equals(ClipboardEnum.YOUTUBE)) {
                        extractEnums = ExtractEnums.YOUTUBE;

                        usernameLink = allWayLink;
                    } else if (clipboard.getType().equals(ClipboardEnum.MEDIUM)) {
                        extractEnums = ExtractEnums.MEDIUM;
                        if (allWayLink.contains(".com/@")) {
                            int in = allWayLink.indexOf(".com/@") + 5;
                            usernameLink = allWayLink.substring(in);
                        } else usernameLink = allWayLink;
                    } else if (clipboard.getType().equals(ClipboardEnum.WEB)) {
                        extractEnums = ExtractEnums.WEB;
                        usernameLink = allWayLink;
                    } else if (clipboard.getType().equals(ClipboardEnum.GITHUB)) {
                        extractEnums = ExtractEnums.GITHUB;
                        usernameLink = allWayLink;
                    } else if (clipboard.getType().equals(ClipboardEnum.ANGEL)) {
                        extractEnums = ExtractEnums.ANGEL;
                        usernameLink = allWayLink;
                    } else if (clipboard.getType().equals(ClipboardEnum.TUMBLR)) {
                        extractEnums = ExtractEnums.TUMBL;
                        usernameLink = allWayLink;
                    } else if (clipboard.getType().equals(ClipboardEnum.G_DOC)) {
                        extractEnums = ExtractEnums.G_DOC;
                        usernameLink = allWayLink;
                    } else if (clipboard.getType().equals(ClipboardEnum.G_SHEET)) {
                        extractEnums = ExtractEnums.G_SHEET;
                        usernameLink = allWayLink;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                listExtract.add(new Extrator(ExtractEnums.NAME, name.trim(), null));

                listExtract.add(new Extrator(extractEnums, usernameLink, allWayLink));


                //extratorAdapter.updateList(listExtract);

                clipboard.setNameFromSocial(name);

                //EventBus.getDefault().post(new SaveClipboard());

                ((TextView) frameLayout.findViewById(R.id.textParseURL)).setText(name.trim());
                ((TextView) frameLayout.findViewById(R.id.textParseURL)).requestLayout();
                ((TextView) frameLayout.findViewById(R.id.textParseURL)).setTextColor(mainView.getContext().getResources().getColor(R.color.black));

                ((TextView) frameEdit.findViewById(R.id.textParseURLEdit)).setText(name.trim());
                ((TextView) frameEdit.findViewById(R.id.textParseURLEdit)).requestLayout();
                ((TextView) frameEdit.findViewById(R.id.textParseURLEdit)).setTextColor(mainView.getContext().getResources().getColor(R.color.black));
                //((TextView) frameLayout.findViewById(R.id.buttonExtract)).setTextColor(mainView.getContext().getResources().getColor(R.color.primary));

            } else {

                frameLayout.findViewById(R.id.frame_send_messageInfo_clipboard_parse).setVisibility(View.VISIBLE);

                ((TextView) frameLayout.findViewById(R.id.sernInformClipboardParseData)).setVisibility(View.VISIBLE);

                frameLayout.findViewById(R.id.frame_send_messageInfo_clipboard_parse).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        String uriText =
                                "mailto:services@extime.pro" +
                                        "?subject=" + Uri.encode("Unable to read link.") +
                                        "&body=" + Uri.encode(way);
                        Uri uri = Uri.parse(uriText);
                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                        emailIntent.setData(uri);
                        activity.startActivity(Intent.createChooser(emailIntent, "Send email..."));

                    }
                });

                ((TextView) frameLayout.findViewById(R.id.textParseURL)).setText("Unable to read link.");
                ((TextView) frameLayout.findViewById(R.id.textParseURL)).requestLayout();
                ((ImageView) frameLayout.findViewById(R.id.iconData)).setImageDrawable(mainView.getContext().getResources().getDrawable(R.drawable.link));

                ((TextView) frameEdit.findViewById(R.id.textParseURLEdit)).setText("Unable to read link.");
                ((TextView) frameEdit.findViewById(R.id.textParseURLEdit)).requestLayout();
                ((ImageView) frameEdit.findViewById(R.id.iconDataEdit)).setImageDrawable(mainView.getContext().getResources().getDrawable(R.drawable.link));

                this.cancel(true);
            }
            super.onPostExecute(result);

        }

    }


}
