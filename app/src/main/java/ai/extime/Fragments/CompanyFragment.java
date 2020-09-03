package ai.extime.Fragments;

import android.app.Activity;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.extime.R;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import org.greenrobot.eventbus.EventBus;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Random;

import ai.extime.Activity.MainActivity;
import ai.extime.Adapters.CompanyAdapter;
import ai.extime.Adapters.CompanyEditAdapter;
import ai.extime.Adapters.ContactAdapter;
import ai.extime.Adapters.CustomTagsAdapter;
import ai.extime.Adapters.HashtagMasstaggingAdapter;
import ai.extime.Adapters.MostUsedTagsAdapter;
import ai.extime.Adapters.PositionEditAdapter;
import ai.extime.Adapters.ProfileSectionAdapter;
import ai.extime.Enums.ClipboardEnum;
import ai.extime.Enums.SocEnum;
import ai.extime.Enums.SocialEnums;
import ai.extime.Events.AddHistoryEntry;
import ai.extime.Events.AnimColorMessenger;
import ai.extime.Events.UpdateFile;
import ai.extime.Interfaces.CompanySelectInterface;
import ai.extime.Interfaces.HashtagAddInterface;
import ai.extime.Interfaces.Postman;
import ai.extime.Models.Clipboard;
import ai.extime.Models.Contact;
import ai.extime.Models.HashTag;
import ai.extime.Models.HashTagQuantity;
import ai.extime.Models.SocialModel;
import ai.extime.Services.ContactCacheService;
import ai.extime.Services.ContactsService;
import ai.extime.Services.FabNotificationService;
import ai.extime.Utils.ClipboardType;
import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmList;

public class CompanyFragment extends Fragment implements HashtagAddInterface, CompanySelectInterface {

    private Contact company;

    private Activity activityApp;

    private View mainView;

    private ArrayList<View> openedViews = new ArrayList<>();

    private FrameLayout socialPopup;

    public FrameLayout popupPositionEdit = null;
    public FrameLayout popupCompaniesEdit = null;

    private FrameLayout popupProfileEditPreview;
    private FrameLayout popupProfileEditPreviewSocial;

    private ContactsService contactsService;

    private FrameLayout profilePopUp;
    private FrameLayout companyProfilePopup;
    private FrameLayout popupEditSocial;
    private FrameLayout popupUserHashtags;
    private RecyclerView containerAssistant;
    private RecyclerView containerMost;

    private MostUsedTagsAdapter mostTagAdapter;

    CompanyProfileDataFragment companyProfileDataFragment;
    ContactTimelineDataFragment contactTimelineDataFragment;
    CompanyContactsFragment companyContactsFragment;

    private SocialModel socialModel;


    public static boolean nowEdit = false;

    public FrameLayout popupHelpCompanyposition;

    private CustomTagsAdapter customTagsAdapter;

    public ContactAdapter adapterC;

    private FrameLayout frameSelectBar;

    public boolean hideSelect = true;

    private LinearLayout closePopups;

    public FrameLayout popupProfileEdit;

    private CompanyAdapter companyAdapter;

    public static boolean editModeChecker = false;

    public CompanyEditAdapter selectCompanyAdapter;

    public static CompanyFragment newInstance(Contact company, boolean edit){
        Bundle args = new Bundle();
        args.putSerializable("contact", company);
        //args.putBoolean("edit", edit);
        CompanyFragment fragment = new CompanyFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && getArguments().getSerializable("contact") != null) {
            company = (Contact) getArguments().getSerializable("contact");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_company_profile, container, false);

        setHasOptionsMenu(true);

        try {
            fragmentManagerSetup = getChildFragmentManager();
        } catch (Exception e) {
            e.printStackTrace();
        }

        contactsService = new ContactsService(activityApp.getContentResolver(), false);

        adapterC = ((Postman) activityApp).getAdapter();

        getActivity().findViewById(R.id.barFlipper).setVisibility(View.GONE);

        getActivity().findViewById(R.id.extratorContainer).setVisibility(View.GONE);
        getActivity().findViewById(R.id.framePopupSocial).setVisibility(View.GONE);
        getActivity().findViewById(R.id.fabMenuContainer).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.countSearchContacts).setVisibility(View.GONE);

        mainView.setFocusable(true);
        mainView.setClickable(true);
        mainView.requestFocus();

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        initViews();
        initListeners();

        setupSectionPager();


        return mainView;
    }

    public void initListeners(){

        mainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("CLicl close");
                if (openedViews != null) closeOtherPopup();
            }
        });

        mainView.findViewById(R.id.socialsArrowDown).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("SOCIAL CLICK");
                showSocialPopup(company);
            }
        });

        mainView.findViewById(R.id.arrowShowHashtags2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("ARROW CLICK");

                if (socialPopup != null) {
                    if (socialPopup.getVisibility() == View.VISIBLE)
                        return;
                    showPopupUserHashtags(company);
                } else
                    showPopupUserHashtags(company);
            }
        });

        closePopups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupUserHashtags != null) {
                    ViewGroup.LayoutParams mostParams = containerMost.getLayoutParams();
                    mostParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
                    containerMost.setLayoutParams(mostParams);
                    ViewGroup.LayoutParams assistantParams = containerMost.getLayoutParams();
                    assistantParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
                    containerAssistant.setLayoutParams(assistantParams);
                    popupUserHashtags.findViewById(R.id.arrowSuggest).setScaleY(1f);
                    popupUserHashtags.setVisibility(View.GONE);
                }
                //contactProfileDataFragment.closeOtherPopup();
                closeOtherPopup();

                System.out.println("Close popup");
                //mainView.findViewById(R.id.popupStar).setVisibility(View.GONE);

                if (contactTimelineDataFragment != null && contactTimelineDataFragment.popupMessage != null && contactTimelineDataFragment.popupMessage.getVisibility() == View.VISIBLE)
                    contactTimelineDataFragment.popupMessage.setVisibility(View.GONE);




            }
        });

        mainView.findViewById(R.id.closer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (popupUserHashtags != null) {
                    ViewGroup.LayoutParams mostParams = containerMost.getLayoutParams();
                    mostParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
                    containerMost.setLayoutParams(mostParams);
                    ViewGroup.LayoutParams assistantParams = containerMost.getLayoutParams();
                    assistantParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
                    containerAssistant.setLayoutParams(assistantParams);
                    popupUserHashtags.findViewById(R.id.arrowSuggest).setScaleY(1f);
                    popupUserHashtags.setVisibility(View.GONE);
                }

                if (popupProfileEditPreview.getVisibility() != View.INVISIBLE)
                    popupProfileEditPreview.setVisibility(View.GONE);
                if (openedViews.size() > 0) {
                    closeOtherPopup();
                    if (adapterC != null) {
                        companyAdapter.notifyDataSetChanged();
                    }
                }
            }
        });



        getActivity().findViewById(R.id.plane_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*if(((EditText)getActivity().findViewById(R.id.magic_edit_text)).getText().length() > 0){
                    System.out.println("ADD HISTORY 4");
                    EventBus.getDefault().post(new AddHistoryEntry(((EditText)getActivity().findViewById(R.id.magic_edit_text)).getText().toString()));
                }*/

                //System.out.println("SIZE LAST1 = " + selectedContact);
                //System.out.println("SIZE LAST2 = "+contactProfileDataFragment.selectedContact.size());
                //String mag = ((EditText) getActivity().findViewById(R.id.magic_edit_text)).getText().toString().trim();
                String[] magicSplit = ((EditText) getActivity().findViewById(R.id.magic_edit_text)).getText().toString().trim().split(" ");

                String[] magicSplitEdit = ((EditText) getActivity().findViewById(R.id.hashTagsEdit)).getText().toString().split(" ");


                ArrayList<String> listOfHashtags = new ArrayList<>();

                ArrayList<String> listOfHashtagsEdit = new ArrayList<>();

                for (int indexI = 0; indexI < magicSplit.length; indexI++) {
                    if (magicSplit[indexI].trim().length() > 1 && String.valueOf(magicSplit[indexI].trim().charAt(0)).compareTo("#") == 0) {
                        if (!listOfHashtags.contains(magicSplit[indexI].trim().toLowerCase()))
                            listOfHashtags.add(magicSplit[indexI].trim().toLowerCase());
                    } else {
                        if (magicSplit[indexI].trim().length() == 1 && String.valueOf(magicSplit[indexI].trim().charAt(0)).compareTo("#") == 0)
                            continue;
                        listOfHashtags.clear();
                        break;
                    }
                }

                for (int indexI = 0; indexI < magicSplitEdit.length; indexI++) {
                    if (magicSplitEdit[indexI].trim().length() > 1 && String.valueOf(magicSplitEdit[indexI].trim().charAt(0)).compareTo("#") == 0)
                        listOfHashtagsEdit.add(magicSplitEdit[indexI].trim().toLowerCase());
                }

                if (listOfHashtags.size() > 0) {
                    if (listOfHashtags.size() == 1) {
                        if (!nowEdit)
                            addHashtagToProfile(magicSplit[0]);
                        else {
                            if (!listOfHashtagsEdit.contains(listOfHashtags.get(0).toLowerCase().trim().toString()))
                                ((EditText) getActivity().findViewById(R.id.hashTagsEdit)).append(" " + listOfHashtags.get(0).trim().toString());
                            else
                                Toast.makeText(mainView.getContext(), "Hashtag already exist", Toast.LENGTH_SHORT).show();

                        }

                    } else {
                        if (!nowEdit)
                            addHashtagsToProfile(listOfHashtags);
                        else {
                            //((EditText) getActivity().findViewById(R.id.hashTagsEdit)).append(((EditText) getActivity().findViewById(R.id.magic_edit_text)).getText().toString());

                            for (int i = 0; i < listOfHashtags.size(); i++) {
                                if (!listOfHashtagsEdit.contains(listOfHashtags.get(i)))
                                    ((EditText) getActivity().findViewById(R.id.hashTagsEdit)).append(" " + listOfHashtags.get(i).toString());
                            }
                        }

                    }
                    ContactsFragment.UPD_ALL = true;
                    ((EditText) getActivity().findViewById(R.id.magic_edit_text)).getText().clear();
                } else {
                    if (ClipboardType.isEmail(((EditText) getActivity().findViewById(R.id.magic_edit_text)).getText().toString().trim()) && (company.listOfContacts == null || company.listOfContacts.isEmpty())) {
                        //addMailToContact(((EditText) getActivity().findViewById(R.id.magic_edit_text)).getText().toString().trim());
                    } else if (ClipboardType.isPhone(((EditText) getActivity().findViewById(R.id.magic_edit_text)).getText().toString().trim()) && (company.listOfContacts == null || company.listOfContacts.isEmpty())) {
                        //addPhoneToContact(((EditText) getActivity().findViewById(R.id.magic_edit_text)).getText().toString().trim());
                    } else {
                        //addNoteToContact(((EditText) getActivity().findViewById(R.id.magic_edit_text)).getText().toString().trim());
                    }
                    //    }
                }


            }
        });

    }


    public void closeOtherPopup() {
        if (openedViews != null) {
            for (View view : openedViews) {
                view.setVisibility(View.GONE);
            }
            openedViews.clear();
        }

        //mainView.findViewById(R.id.popupStar).setVisibility(View.GONE);
        activityApp.findViewById(R.id.popup_menu_profile).setVisibility(View.GONE);
    }

    public void initViews(){
        ((View) mainView.findViewById(R.id.lineNameProfile)).setVisibility(View.GONE);
        ((View) mainView.findViewById(R.id.lineCompanyProfile)).setVisibility(View.GONE);
        ((View) mainView.findViewById(R.id.lineCompanyProfileWeb)).setVisibility(View.GONE);

        ((View) mainView.findViewById(R.id.positionVileEditPreview)).setVisibility(View.GONE);

        ((View) mainView.findViewById(R.id.lineHashProfile)).setVisibility(View.GONE);

        //popupHelpCompanyposition = (FrameLayout) getActivity().findViewById(R.id.popupProfileCompanyPossitions);

        popupHelpCompanyposition = (FrameLayout) activityApp.findViewById(R.id.popupProfileCompanyPossitions);

        //mainView.findViewById(R.id.popupStar).setVisibility(View.GONE);



        if (company.webSite != null && !company.webSite.isEmpty()) {

            ((ImageView) mainView.findViewById(R.id.webImgCompany)).setImageResource(R.drawable.icn_popup_web_blue);


            ((TextView) mainView.findViewById(R.id.web_title)).setVisibility(View.VISIBLE);
            ((TextView) mainView.findViewById(R.id.web_title)).setText(company.webSite);
            ((TextView) mainView.findViewById(R.id.web_title)).setTextColor(getResources().getColor(R.color.primary_dark));

            mainView.findViewById(R.id.web_title).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        //System.out.println("WEB ICIT GO name = " + contact.getName() + ", web = " + contact.webSite);
                        String uri = company.webSite;
                        if (!uri.contains("https://") && !uri.contains("http://"))
                            uri = "https://" + uri;

                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(uri));

                        if (getActivity() != null)
                            getActivity().startActivity(i);
                        else
                            activityApp.startActivity(i);
                    } catch (Exception e) {

                    }
                }
            });

            mainView.findViewById(R.id.webImgCompany).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                            /*Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                    "mailto", selectedContact.webSite, null));
                            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
                            emailIntent.putExtra(Intent.EXTRA_TEXT, "");
                            startActivity(Intent.createChooser(emailIntent, "Send email"));*/

                    try {

                        String uri = company.webSite;
                        if (!uri.contains("https://") && !uri.contains("http://"))
                            uri = "https://" + uri;

                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(uri));

                        if (getActivity() != null)
                            getActivity().startActivity(i);
                        else
                            activityApp.startActivity(i);

                    } catch (Exception e) {

                    }

                }
            });
        } else {
            ((ImageView) mainView.findViewById(R.id.webImgCompany)).setImageResource(R.drawable.icn_popup_web);
            ((TextView) mainView.findViewById(R.id.web_title)).setText("add web");
            ((TextView) mainView.findViewById(R.id.web_title)).setTextColor(getResources().getColor(R.color.gray));


            mainView.findViewById(R.id.web_title).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {

                        FrameLayout editFrameWeb = (FrameLayout) getActivity().findViewById(R.id.popupEditMain);
                        ((TextView) editFrameWeb.findViewById(R.id.typeField)).setText("Web");

                        ((ImageView) editFrameWeb.findViewById(R.id.imageEditSocial)).setVisibility(View.VISIBLE);
                        ((ImageView) editFrameWeb.findViewById(R.id.imageEditSocialInsta)).setVisibility(View.GONE);
                        ((ImageView) editFrameWeb.findViewById(R.id.imageEditSocial)).setImageDrawable(getResources().getDrawable(R.drawable.icn_popup_web));
                        ((TextView) editFrameWeb.findViewById(R.id.dataToEdit)).setText("");
                        editFrameWeb.findViewById(R.id.ok_social).setVisibility(View.GONE);

                        editFrameWeb.findViewById(R.id.searchSocial).setVisibility(View.VISIBLE);
                        ((EditText) editFrameWeb.findViewById(R.id.dataToEdit)).setHint("Add name or address");
                        ((EditText) editFrameWeb.findViewById(R.id.dataToEdit)).setPadding(0, 0, 150, 0);


                        editFrameWeb.findViewById(R.id.cancelEditField).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ((TextView) editFrameWeb.findViewById(R.id.dataToEdit)).setText("");
                                editFrameWeb.setVisibility(View.GONE);
                            }
                        });

                        editFrameWeb.findViewById(R.id.updateContactField).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (((TextView) editFrameWeb.findViewById(R.id.dataToEdit)).getText().length() > 0) {


                                    Realm realm = Realm.getDefaultInstance(); //+
                                    realm.beginTransaction();
                                    company.webSite = ((TextView) editFrameWeb.findViewById(R.id.dataToEdit)).getText().toString();
                                    realm.commitTransaction();
                                    realm.close();


                                    ((ImageView) mainView.findViewById(R.id.webImgCompany)).setImageResource(R.drawable.icn_popup_web_blue);
                                    ((TextView) mainView.findViewById(R.id.web_title)).setVisibility(View.VISIBLE);
                                    ((TextView) mainView.findViewById(R.id.web_title)).setText(company.webSite);
                                    ((TextView) mainView.findViewById(R.id.web_title)).setTextColor(getResources().getColor(R.color.primary_dark));

                                    mainView.findViewById(R.id.web_title).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            try {


                                                String uri = company.webSite;
                                                if (!uri.contains("https://") && !uri.contains("http://"))
                                                    uri = "https://" + uri;

                                                Intent i = new Intent(Intent.ACTION_VIEW);
                                                i.setData(Uri.parse(uri));

                                                if (getActivity() != null)
                                                    getActivity().startActivity(i);
                                                else
                                                    activityApp.startActivity(i);

                                            } catch (Exception e) {

                                            }
                                        }
                                    });

                                    mainView.findViewById(R.id.webImgCompany).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            try {

                                                //System.out.println("WEB ICIT GO name = " + contact.getName() + ", web = " + contact.webSite);
                                                String uri = company.webSite;
                                                if (!uri.contains("https://") && !uri.contains("http://"))
                                                    uri = "https://" + uri;

                                                Intent i = new Intent(Intent.ACTION_VIEW);
                                                i.setData(Uri.parse(uri));

                                                if (getActivity() != null)
                                                    getActivity().startActivity(i);
                                                else
                                                    activityApp.startActivity(i);

                                            } catch (Exception e) {

                                            }
                                        }
                                    });
                                }
                                editFrameWeb.setVisibility(View.GONE);
                            }
                        });


                        editFrameWeb.findViewById(R.id.get_last_clips).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                String socialLinkClip = "";
                                for (Clipboard cl : FabNotificationService.clipboard) { //NullPointerException
                                    if (cl.getListClipboards() != null && !cl.getListClipboards().isEmpty()) {
                                        boolean checkFind = false;
                                        for (Clipboard cl2 : cl.getListClipboards()) {
                                            if (cl2.getType().equals(ClipboardEnum.WEB)) {
                                                socialLinkClip = cl2.getValueCopy();
                                                checkFind = true;
                                                break;
                                            }
                                        }
                                        if (checkFind) break;
                                    } else {
                                        if (cl.getType().equals(ClipboardEnum.WEB)) {
                                            socialLinkClip = cl.getValueCopy();
                                            break;
                                        }
                                    }
                                }
                                ((EditText) editFrameWeb.findViewById(R.id.dataToEdit)).setText(socialLinkClip);
                            }
                        });


                        editFrameWeb.findViewById(R.id.imageEditSocial).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    if (((EditText) editFrameWeb.findViewById(R.id.dataToEdit)).length() > 0) {
                                        //System.out.println("WEB ICIT GO name = " + contact.getName() + ", web = " + contact.webSite);
                                        String uri = company.webSite;
                                        if (!uri.contains("https://") && !uri.contains("http://"))
                                            uri = "https://" + uri;

                                        Intent i = new Intent(Intent.ACTION_VIEW);
                                        i.setData(Uri.parse(uri));

                                        if (getActivity() != null)
                                            getActivity().startActivity(i);
                                        else
                                            activityApp.startActivity(i);
                                    }

                                } catch (Exception e) {

                                }
                            }
                        });

                        editFrameWeb.findViewById(R.id.imageEditSocial).setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                return false;
                            }
                        });

                        editFrameWeb.findViewById(R.id.searchSocial).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    if (((EditText) editFrameWeb.findViewById(R.id.dataToEdit)).length() > 0) {
                                        String uri = ((EditText) editFrameWeb.findViewById(R.id.dataToEdit)).getText().toString();

                                       /* if(ClipboardType.isWeb(uri)) {
                                            if (!uri.contains("https://") && !uri.contains("http://"))
                                                uri = "https://" + uri;

                                            Intent i = new Intent(Intent.ACTION_VIEW);
                                            i.setData(Uri.parse(uri));

                                            if (getActivity() != null)
                                                getActivity().startActivity(i);
                                        }else {*/
                                            Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                                            intent.putExtra(SearchManager.QUERY, ((EditText) editFrameWeb.findViewById(R.id.dataToEdit)).getText().toString());
                                            if (getActivity() != null)
                                                getActivity().startActivity(intent);
                                      //  }

                                           /* else
                                                MainActivity.activityProfile.startActivity(i);*/
                                    }

                                } catch (Exception e) {

                                }
                            }
                        });


                        editFrameWeb.setVisibility(View.VISIBLE);


                        openedViews.add(editFrameWeb);

                    } catch (Exception e) {

                    }
                }
            });


        }



        LinearLayout containerHashTags = (LinearLayout) mainView.findViewById(R.id.containerHashTags);
        if (containerHashTags.getChildCount() > 0)
            containerHashTags.removeAllViews();


        if (company != null && company.getListOfHashtags() != null) {
            for (HashTag hashTag : company.getListOfHashtags()) {
                TextView text = new TextView(getActivity());
                text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                //DisplayMetrics metrics = mainView.getContext().getResources().getDisplayMetrics();
                text.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                text.setTextColor(getResources().getColor(R.color.colorPrimary));
                text.setText(hashTag.getHashTagValue() + " ");
                System.out.println("HASHTAG PROFILE");
                text.setOnClickListener(v -> adapterC.searchByHashTagValue(hashTag.getHashTagValue()));
                text.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        deleteHashTagsFromUser(text.getText().toString(), company);
                        return true;
                    }
                });
                containerHashTags.addView(text);
            }
        }

        if (company != null && (company.getListOfHashtags() == null || company.getListOfHashtags().size() == 0)) {
            System.out.println("Add Hashtah TExt added");
            TextView text = new TextView(getActivity());
            text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            //DisplayMetrics metrics = mainView.getContext().getResources().getDisplayMetrics();
            text.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            text.setTextColor(getResources().getColor(R.color.colorPrimary));
            text.setText("hashtags");
            text.setOnClickListener(v -> {
                //containerHashTags.removeAllViews();
                showPopupUserHashtags(company);
            });
            containerHashTags.addView(text);
        }


        HorizontalScrollView scrollView = (HorizontalScrollView) mainView.findViewById(R.id.scrollHorizontal);

        if (scrollView.getChildCount() > 0) scrollView.removeAllViews();

        scrollView.addView(containerHashTags);
        scrollView.setSmoothScrollingEnabled(false);
        scrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
        scrollView.setSmoothScrollingEnabled(true);

        frameSelectBar = (FrameLayout) getActivity().findViewById(R.id.frame_select_bar);
        closePopups = (LinearLayout) mainView.findViewById(R.id.close_popups);
        popupProfileEdit = (FrameLayout) mainView.findViewById(R.id.popupEditProfile);
        popupProfileEditPreview = (FrameLayout) mainView.findViewById(R.id.popupPreviewEdit);
        contactsService = new ContactsService(getActivity().getContentResolver(), false);


        try {
            ((TextView) mainView.findViewById(R.id.name)).setText(company.getName());
        } catch (IllegalStateException e) {
            getFragmentManager().popBackStack();
        }

        mainView.findViewById(R.id.company).setVisibility(View.GONE);
        mainView.findViewById(R.id.company_title).setVisibility(View.GONE);

        if (frameSelectBar.getVisibility() == View.VISIBLE) {
            System.out.println("HIDE SELECT MENU");
            frameSelectBar.setVisibility(View.GONE);
            hideSelect = false;
        }
        if (company.listOfContacts != null && !company.listOfContacts.isEmpty()) {
            mainView.findViewById(R.id.profilePerson).setVisibility(View.GONE);
            mainView.findViewById(R.id.profile_company).setVisibility(View.VISIBLE);


            //mainView.findViewById(R.id.profileCompany).setVisibility(View.VISIBLE);



            ((TextView) mainView.findViewById(R.id.companyNumb)).setText(String.valueOf(company.listOfContacts.size()));


            // mainView.findViewById(R.id.companyNumb).setVisibility(View.VISIBLE);

            ((MainActivity) mainView.getContext()).setCompanyToContent();

        } else {
            mainView.findViewById(R.id.profilePerson).setVisibility(View.VISIBLE);
            mainView.findViewById(R.id.profile_company).setVisibility(View.GONE);
            ((MainActivity) mainView.getContext()).setProfileToContent();
        }

        if (company.getCompany() != null) {
            mainView.findViewById(R.id.company_title).setVisibility(View.VISIBLE);
            ((TextView) mainView.findViewById(R.id.company_title)).setText(company.getCompany());
            if (company.getCompanyPossition() != null) {
                Double ems = company.getCompanyPossition().length() / 2.5;
                int ems_count = ems.intValue();
                if (ems_count < 6) {
                    ((TextView) mainView.findViewById(R.id.company_title)).setMaxEms(6 + (6 - ems_count));
                }
            } else {
                ((TextView) mainView.findViewById(R.id.company_title)).setMaxEms(12);
            }

            /*((TextView) mainView.findViewById(R.id.company_title)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    findByCompany(company.getCompany());
                }
            });*/
        }
        if (company.getCompanyPossition() != null) {
            mainView.findViewById(R.id.company).setVisibility(View.VISIBLE);
            ((TextView) mainView.findViewById(R.id.company)).setText(company.getCompanyPossition());
            if (company.getCompany() != null) {
                Double ems = company.getCompany().length() / 2.5;
                int ems_count = ems.intValue();
                if (ems_count < 6) {
                    ((TextView) mainView.findViewById(R.id.company)).setMaxEms(6 + (6 - ems_count));
                }
            } else {
                ((TextView) mainView.findViewById(R.id.company)).setMaxEms(12);
            }

        }

        //((TextView) mainView.findViewById(R.id.time)).setText(/*getUpdTime*/(selectedContact.time));


        try {
            mainView.findViewById(R.id.profilePopupInitials).setVisibility(View.GONE);
            ((ImageView) mainView.findViewById(R.id.profilePopupAvatar)).setImageURI(Uri.parse(company.getPhotoURL()));
            if (((BitmapDrawable) ((ImageView) mainView.findViewById(R.id.profilePopupAvatar)).getDrawable()).getBitmap() == null) {
                GradientDrawable circle = (GradientDrawable) ContextCompat.getDrawable(mainView.getContext(), R.drawable.blue_circle).mutate();
                circle.setColor(company.color);
                mainView.findViewById(R.id.profilePopupAvatar).setBackground(circle);
                if (company.listOfContacts != null && !company.listOfContacts.isEmpty()) {
                    mainView.findViewById(R.id.profilePopupAvatar).setVisibility(View.GONE);
                    mainView.findViewById(R.id.companyPopupAvatar).setBackgroundColor(company.color);
                }
                ((ImageView) mainView.findViewById(R.id.profilePopupAvatar)).setImageDrawable(null);

                String initials = "";
                String names[] = company.getName().split("\\s+");

                for (String namePart : names)
                    initials += namePart.charAt(0);

                mainView.findViewById(R.id.profilePopupInitials).setVisibility(View.VISIBLE);
                ((TextView) mainView.findViewById(R.id.profilePopupInitials)).setText(initials);
            }
        } catch (Exception e) {
            GradientDrawable circle = (GradientDrawable) ContextCompat.getDrawable(mainView.getContext(), R.drawable.blue_circle).mutate();
            circle.setColor(company.color);
            mainView.findViewById(R.id.profilePopupAvatar).setBackground(circle);
            if (company.listOfContacts != null && !company.listOfContacts.isEmpty()) {
                mainView.findViewById(R.id.profilePopupAvatar).setVisibility(View.GONE);
                mainView.findViewById(R.id.companyPopupAvatar).setBackgroundColor(company.color);
            }
            ((ImageView) mainView.findViewById(R.id.profilePopupAvatar)).setImageDrawable(null);

            String initials = "";
            String names[] = company.getName().split("\\s+");

            for (String namePart : names)
                initials += namePart.charAt(0);

            mainView.findViewById(R.id.profilePopupInitials).setVisibility(View.VISIBLE);
            ((TextView) mainView.findViewById(R.id.profilePopupInitials)).setText(initials);
        }



        initIconColor(company, mainView);



    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }


    private Menu profileMenu;

    public static boolean checkEdit = false;

    MenuItem menuItem;

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        System.out.println("onPrepareOptionsMenu");
        profileMenu = menu;
        menuItem = menu.findItem(R.id.edit_contact);
        menu.findItem(R.id.action_favorite).setVisible(false);
        menu.findItem(R.id.edit_contact).setVisible(true);
        menu.findItem(R.id.save_contact).setVisible(false);

        Toolbar mainToolBar = (Toolbar) getActivity().findViewById(R.id.main_toolbar);
        TextView cancel = (TextView) getActivity().findViewById(R.id.cancel_toolbar);

        menu.findItem(R.id.menu_profile).setVisible(true);

        menu.findItem(R.id.share_profile).setVisible(true); //



       /* menu.findItem(R.id.share_profile).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                shareContact();
                return false;
            }
        });*/





        getActivity().findViewById(R.id.barFlipper).setVisibility(View.GONE);

        if (checkEdit) {
            menu.findItem(R.id.edit_contact).setVisible(false);
            menu.findItem(R.id.save_contact).setVisible(true);
        }

        if (nowEdit) {
            menu.findItem(R.id.edit_contact).setVisible(false);
            menu.findItem(R.id.save_contact).setVisible(true);
        } else {
            menu.findItem(R.id.edit_contact).setVisible(true);
            menu.findItem(R.id.save_contact).setVisible(false);

            mainToolBar.setNavigationIcon(R.drawable.icn_arrow_back);

            mainToolBar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (MainActivity.checkDel) {
                        android.support.v4.app.FragmentManager fragmentManager = ((FragmentActivity) getActivity()).getSupportFragmentManager();

                        fragmentManager.popBackStack("contacts", android.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        MainActivity.checkDel = false;

                    } else
                        getActivity().onBackPressed();


                }
            });
        }

        ((TextView) mainToolBar.findViewById(R.id.toolbar_title)).setText("Company");

        //getActivity().findViewById(R.id.fabMenuContainer).setVisibility(View.GONE);

        if(mainView != null){

            if(mainView.findViewById(R.id.popupMessage).getVisibility() == View.VISIBLE){
                getActivity().findViewById(R.id.fabMenuContainer).setVisibility(View.GONE);
            }
        }




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
                                                    Toast.makeText(activityApp, "Secret blur mode enabled", Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                        } else {
                                            blur = false;
                                            activityApp.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(activityApp, "Secret blur mode disabled", Toast.LENGTH_SHORT).show();
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




        if (CompanyAdapter.selectionModeCompany) {
            menu.findItem(R.id.edit_contact).setVisible(false);
        }
      /*  if (ContactAdapter.checkFoActionIcon) {

            MainActivity.MAIN_MENU.getItem(0).setVisible(false);
            MainActivity.MAIN_MENU.getItem(2).setVisible(false);
            MainActivity.MAIN_MENU.getItem(3).setVisible(false);
            MainActivity.mainToolBar.findViewById(R.id.toolbar_title).setVisibility(View.VISIBLE);
            ((TextView)MainActivity.mainToolBar.findViewById(R.id.toolbar_title)).setText("Profile");

            getActivity().findViewById(R.id.select_menu).setVisibility(View.GONE);
            ContactAdapter.checkFoActionIcon = false;
          //  MainActivity.checkDel = true;
        }*/




        menu.findItem(R.id.edit_contact).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                nowEdit = true;

                menu.findItem(R.id.menu_profile).setVisible(false);
                menu.findItem(R.id.share_profile).setVisible(false);


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
                                        android.app.FragmentManager fm = getActivity().getFragmentManager();
                                        cancel.setVisibility(View.GONE);
                                        mainToolBar.setNavigationIcon(R.drawable.icn_arrow_back);
                                        cancleEditMode();
                                        menu.findItem(R.id.edit_contact).setVisible(true);
                                        menu.findItem(R.id.save_contact).setVisible(false);

                                        editModeChecker = false;
                                    })
                                    .setNegativeButton("No", (dialog, id) -> {
                                        dialog.cancel();
                                    });

                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                        } else {
                            android.app.FragmentManager fm = getActivity().getFragmentManager();
                            cancel.setVisibility(View.GONE);
                            mainToolBar.setNavigationIcon(R.drawable.icn_arrow_back);
                            cancleEditMode();
                            menu.findItem(R.id.edit_contact).setVisible(true);
                            menu.findItem(R.id.save_contact).setVisible(false);

                        }

                        menu.findItem(R.id.menu_profile).setVisible(true);
                        menu.findItem(R.id.share_profile).setVisible(true); //
                    }

                });
                editModeChecker = false;
                menu.findItem(R.id.edit_contact).setVisible(false);
                menu.findItem(R.id.save_contact).setVisible(true);
                checkEdit = true;
                return false;
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


                            nowEdit = false;
                            editModeChecker = false;

                            popupHelpCompanyposition.setVisibility(View.GONE);

                            endEditMode();

                            Contact selectedContact = company;

                               /* new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ContactsFragment.listOfContacts = ContactCacheService.getAllContacts(null);
                                    }
                                }).start();*/


                            //contactAdapter = ContactAdapter.contactAd;
                            ///ContactsFragment.listOfContacts = ContactAdapter.savedContacts;
                            //if(contactAdapter.get == null)System.out.println("NULLADAPTER");


                            /*ContactAdapter.savedContacts.clear();
                            ContactAdapter.savedContacts = ContactCacheService.getAllContacts(null);*/


                            ((Postman) activityApp).updateSavedList();

                            //contactAdapter = ContactAdapter.contactAd;
                            //if (contactAdapter != null)
                            for (int i = 0; i < adapterC.getListOfContacts().size(); i++) {
                                if (adapterC.getListOfContacts().get(i) == null || !adapterC.getListOfContacts().get(i).isValid())
                                    adapterC.getListOfContacts().remove(i);
                            }


                           /* MainActivity.LIST_OF_CONTACTS.clear();
                            MainActivity.LIST_OF_CONTACTS = ContactAdapter.savedContacts;*/


                            //  contactAdapter.notifyDataSetChanged();



                            //Realm realm = Realm.getDefaultInstance();


                                try {
                                    companyProfileDataFragment.companyAdapter.notifyDataSetChanged();
                                } catch (Exception e) {

                                }


                            initIconColor(selectedContact, mainView);


                            cancel.setVisibility(View.GONE);
                            mainToolBar.setNavigationIcon(R.drawable.icn_arrow_back);
                            //     ContactCacheService.updateContact(selectedContact, mainView.getContext());



                            mainToolBar.setNavigationOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    if (MainActivity.checkDel) {
                                        android.support.v4.app.FragmentManager fragmentManager = ((FragmentActivity) getActivity()).getSupportFragmentManager();

                                        fragmentManager.popBackStack("contacts", android.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                        MainActivity.checkDel = false;

                                    } else
                                        getActivity().onBackPressed();


                                }
                            });
                            menu.findItem(R.id.save_contact).setVisible(false);
                            menu.findItem(R.id.edit_contact).setVisible(true);
                            ContactsFragment.UPD_ALL = true;

                            contactTimelineDataFragment.checkEmail();

                            //EventBus.getDefault().post(new UpdateFile());

                            menu.findItem(R.id.menu_profile).setVisible(true);
                            menu.findItem(R.id.share_profile).setVisible(true); //


                        })
                        .setNegativeButton("No", (dialog, id) -> dialog.cancel());
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                return false;
            }
        });
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            activityApp = (Activity) context;
        }
    }

    public boolean checkClick_facebookEdit = true;

    public void showSocialPopup(Contact contact){


        socialPopup = (FrameLayout) getActivity().findViewById(R.id.popupSocial);

        ((ScrollView) socialPopup.findViewById(R.id.scroll)).scrollTo(0, 0);

        socialPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupProfileEditPreviewSocial != null && popupProfileEditPreviewSocial.getVisibility() == View.VISIBLE)
                    popupProfileEditPreviewSocial.setVisibility(View.GONE);
            }
        });
        FrameLayout editFrame = (FrameLayout) getActivity().findViewById(R.id.popupEditMain);
        editFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        editFrame.findViewById(R.id.imageEditSocial).setVisibility(View.GONE);
        editFrame.findViewById(R.id.frameImageEditSocialInsta).setVisibility(View.GONE);


        // ((ImageView) socialPopup.findViewById(R.id.facebook_icon)).setImageResource(R.drawable.icn_social_facebook_gray);
        Drawable color = new ColorDrawable(Color.parseColor("#e2e5e8"));
        Drawable image = getResources().getDrawable(R.drawable.icn_social_facebook2);
        LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
        ((ImageView) socialPopup.findViewById(R.id.facebook_icon)).setImageDrawable(ld);

        ((TextView) socialPopup.findViewById(R.id.facebook_nick)).setText("add username or link");

        Drawable color2 = new ColorDrawable(Color.parseColor("#e2e5e8"));
        Drawable image2 = getResources().getDrawable(R.drawable.icn_social_vk2);
        LayerDrawable ld2 = new LayerDrawable(new Drawable[]{color2, image2});
        ((ImageView) socialPopup.findViewById(R.id.vk_icon)).setImageDrawable(ld2);

        // ((ImageView) socialPopup.findViewById(R.id.vk_icon)).setImageResource(R.drawable.icn_social_vk_gray);
        ((TextView) socialPopup.findViewById(R.id.vkNick)).setText("add username or link");

        Drawable color3 = new ColorDrawable(Color.parseColor("#e2e5e8"));
        Drawable image3 = getResources().getDrawable(R.drawable.icn_social_linked2);
        LayerDrawable ld3 = new LayerDrawable(new Drawable[]{color3, image3});
        ((ImageView) socialPopup.findViewById(R.id.link_icon)).setImageDrawable(ld3);

        //  ((ImageView) socialPopup.findViewById(R.id.link_icon)).setImageResource(R.drawable.icn_social_linkedin_gray);
        ((TextView) socialPopup.findViewById(R.id.link_text)).setText("add username or link");

        //  ((ImageView) socialPopup.findViewById(R.id.instagram_icon)).setImageResource(R.drawable.icn_social_instagram_gray);
        Drawable color4 = new ColorDrawable(Color.parseColor("#e2e5e8"));
        Drawable image4 = getResources().getDrawable(R.drawable.icn_social_ints2);
        LayerDrawable ld4 = new LayerDrawable(new Drawable[]{color4, image4});
        ((ImageView) socialPopup.findViewById(R.id.instagram_icon)).setImageDrawable(ld4);

        ((TextView) socialPopup.findViewById(R.id.instagramNick)).setText("add username or link");

        Drawable color44 = new ColorDrawable(Color.parseColor("#e2e5e8"));
        Drawable image44 = getResources().getDrawable(R.drawable.ic_youtube_white);
        LayerDrawable ld44 = new LayerDrawable(new Drawable[]{color44, image44});
        ((ImageView) socialPopup.findViewById(R.id.youtube_icon)).setImageDrawable(ld44);

        ((TextView) socialPopup.findViewById(R.id.youtubeNick)).setText("add username or link");

        Drawable color41 = new ColorDrawable(Color.parseColor("#e2e5e8"));
        Drawable image41 = getResources().getDrawable(R.drawable.ic_twitter_white);
        LayerDrawable ld41 = new LayerDrawable(new Drawable[]{color41, image41});
        ((ImageView) socialPopup.findViewById(R.id.twitter_icon)).setImageDrawable(ld41);
        //((ImageView) socialPopup.findViewById(R.id.twitter_icon)).setImageDrawable(getResources().getDrawable(R.drawable.ic_twitter_white));
        ((TextView) socialPopup.findViewById(R.id.twitterNick)).setText("add username or link");

        Drawable color42 = new ColorDrawable(Color.parseColor("#e2e5e8"));
        Drawable image42 = getResources().getDrawable(R.drawable.medium_white);
        LayerDrawable ld42 = new LayerDrawable(new Drawable[]{color42, image42});
        ((ImageView) socialPopup.findViewById(R.id.medium_icon)).setImageDrawable(ld42);
        //((ImageView) socialPopup.findViewById(R.id.twitter_icon)).setImageDrawable(getResources().getDrawable(R.drawable.ic_twitter_white));
        ((TextView) socialPopup.findViewById(R.id.mediumNick)).setText("add username or link");


        Drawable color5 = new ColorDrawable(Color.parseColor("#e2e5e8"));
        Drawable image5 = getResources().getDrawable(R.drawable.icn_social_viber2_gray);
        LayerDrawable ld5 = new LayerDrawable(new Drawable[]{color5, image5});
        ((CircleImageView) socialPopup.findViewById(R.id.viber_icon)).setImageDrawable(ld5);
        //   ((ImageView) socialPopup.findViewById(R.id.viber_icon)).setImageResource(R.drawable.icn_social_viber_gray);
        ((TextView) socialPopup.findViewById(R.id.viber_text)).setText("add link");

        Drawable color6 = new ColorDrawable(Color.parseColor("#e2e5e8"));
        Drawable image6 = getResources().getDrawable(R.drawable.icn_social_whatsapp3_gray);
        LayerDrawable ld6 = new LayerDrawable(new Drawable[]{color6, image6});
        ((CircleImageView) socialPopup.findViewById(R.id.whatsapp_icon)).setImageDrawable(ld6);

        //  ((ImageView) socialPopup.findViewById(R.id.whatsapp_icon)).setImageResource(R.drawable.icn_social_whatsapp_gray);
        ((TextView) socialPopup.findViewById(R.id.whatsapp_text)).setText("add link");

        //   ((ImageView) socialPopup.findViewById(R.id.telegram_icon)).setImageResource(R.drawable.icn_social_telegram_gray);
        Drawable color7 = new ColorDrawable(Color.parseColor("#e2e5e8"));
        Drawable image7 = getResources().getDrawable(R.drawable.icn_social_telegram2);
        LayerDrawable ld7 = new LayerDrawable(new Drawable[]{color7, image7});
        ((CircleImageView) socialPopup.findViewById(R.id.telegram_icon)).setImageDrawable(ld7);

        ((TextView) socialPopup.findViewById(R.id.telegram_text)).setText("add link");

        //    ((ImageView) socialPopup.findViewById(R.id.skype_icon)).setImageResource(R.drawable.icn_social_skype_gray);
        Drawable color8 = new ColorDrawable(Color.parseColor("#e2e5e8"));
        Drawable image8 = getResources().getDrawable(R.drawable.icn_social_skype2_gray);
        LayerDrawable ld8 = new LayerDrawable(new Drawable[]{color8, image8});
        ((CircleImageView) socialPopup.findViewById(R.id.skype_icon)).setImageDrawable(ld8);

        ((TextView) socialPopup.findViewById(R.id.skype_text)).setText("add link");

        if ((contact.hasTelegram && contact.getSocialModel() != null && contact.getSocialModel().getTelegramLink() != null)) {
            //  ((ImageView) socialPopup.findViewById(R.id.telegram_icon)).setImageResource(R.drawable.icn_social_telegram);
            Drawable colort = new ColorDrawable(Color.parseColor("#7AA5DA"));
            Drawable imaget = getResources().getDrawable(R.drawable.icn_social_telegram2);
            LayerDrawable ldt = new LayerDrawable(new Drawable[]{colort, imaget});
            ((CircleImageView) socialPopup.findViewById(R.id.telegram_icon)).setImageDrawable(ldt);


        }

        if (contact.getSocialModel() != null) {
            socialModel = contact.getSocialModel();

            if (socialModel.getFacebookLink() != null) {
                //   ((ImageView) socialPopup.findViewById(R.id.facebook_icon)).setImageResource(R.drawable.icn_social_facebook);
                Drawable colorf = new ColorDrawable(Color.parseColor("#475993"));
                Drawable imagef = getResources().getDrawable(R.drawable.icn_social_facebook2);
                LayerDrawable ldf = new LayerDrawable(new Drawable[]{colorf, imagef});
                ((ImageView) socialPopup.findViewById(R.id.facebook_icon)).setImageDrawable(ldf);


                String link = socialModel.getFacebookLink();
                boolean checkich = false;
                try {
                    if(link.contains("http") && !link.contains("https"))
                        link = link.replace("http","https");
                    String checkFacebook = link.substring(0, 21);
                    String checkFacebook2 = link.substring(0, 23);
                    String checkFacebook3 = link.substring(0, 25);
                    if (checkFacebook.equals("https://facebook.com/")) {
                        link = link.substring(21, link.length());
                        if (link.contains("profile.php")) {
                            link = link.substring(link.indexOf(".php") + 5, link.length());
                        }

                        ((TextView) socialPopup.findViewById(R.id.facebook_nick)).setText(link);
                        checkich = true;
                    } else if (checkFacebook2.equals("https://m.facebook.com/")) {
                        link = link.substring(23, link.length());
                        if (link.contains("profile.php")) {
                            link = link.substring(link.indexOf(".php") + 5, link.length());
                        }
                        ((TextView) socialPopup.findViewById(R.id.facebook_nick)).setText(link);
                        checkich = true;
                    } else if (checkFacebook3.equals("https://www.facebook.com/") || checkFacebook3.equals("https://web.facebook.com/")) {
                        link = link.substring(25, link.length());
                        ((TextView) socialPopup.findViewById(R.id.facebook_nick)).setText(link);
                        checkich = true;
                    }
                } catch (Exception e) {

                    e.printStackTrace();
                }
                if (!checkich) {
                    try {
                        String checkFacebook = link.substring(0, 25);
                        if (checkFacebook.equals("https://www.facebook.com/")) {
                            link = link.substring(25, link.length());
                            if (link.contains("profile.php")) {
                                link = link.substring(link.indexOf(".php") + 5, link.length());
                            }
                            ((TextView) socialPopup.findViewById(R.id.facebook_nick)).setText(link);
                            checkich = true;
                        } else if (link.contains("php?")) {
                            int index = link.indexOf("php?");
                            link = link.substring(index + 4, link.length());
                            ((TextView) socialPopup.findViewById(R.id.facebook_nick)).setText(link);
                            checkich = true;
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (!checkich)
                    ((TextView) socialPopup.findViewById(R.id.facebook_nick)).setText(contact.getSocialModel().getFacebookLink());
            }
            if (socialModel.getVkLink() != null) {
                //  ((ImageView) socialPopup.findViewById(R.id.vk_icon)).setImageResource(R.drawable.icn_social_vk);
                Drawable colorv = new ColorDrawable(Color.parseColor("#507299"));
                Drawable imagev = getResources().getDrawable(R.drawable.icn_social_vk2);
                LayerDrawable ldv = new LayerDrawable(new Drawable[]{colorv, imagev});
                ((ImageView) socialPopup.findViewById(R.id.vk_icon)).setImageDrawable(ldv);

                try {
                    String link = socialModel.getVkLink();
                    String checkVK = link.substring(0, 8);
                    if (link.contains("https://vk.com/")) {

                        link = link.substring(15, link.length());

                        ((TextView) socialPopup.findViewById(R.id.vkNick)).setText(link);
                    } else if (link.contains("https://m.vk.com/")) {
                        link = link.substring(17, link.length());
                        ((TextView) socialPopup.findViewById(R.id.vkNick)).setText(link);

                    } else if (link.contains("m.vk.com/")) {
                        link = link.substring(9, link.length());
                        ((TextView) socialPopup.findViewById(R.id.vkNick)).setText(link);
                    } else if (link.contains("vk.com/")) {
                        link = link.substring(7, link.length());
                        ((TextView) socialPopup.findViewById(R.id.vkNick)).setText(link);
                    } else if (link.contains("https://www.vk.com/")) {
                        link = link.substring(19, link.length());
                        ((TextView) socialPopup.findViewById(R.id.vkNick)).setText(link);

                    } else
                        ((TextView) socialPopup.findViewById(R.id.vkNick)).setText(socialModel.getVkLink());
                } catch (Exception e) {
                    e.printStackTrace();
                    ((TextView) socialPopup.findViewById(R.id.vkNick)).setText(contact.getSocialModel().getVkLink());
                }
            }
            if (socialModel.getLinkedInLink() != null) {
                //   ((ImageView) socialPopup.findViewById(R.id.link_icon)).setImageResource(R.drawable.icn_social_linkedin);
                Drawable colorl = new ColorDrawable(Color.parseColor("#0077B7"));
                Drawable imagel = getResources().getDrawable(R.drawable.icn_social_linked2);
                LayerDrawable ldl = new LayerDrawable(new Drawable[]{colorl, imagel});
                ((ImageView) socialPopup.findViewById(R.id.link_icon)).setImageDrawable(ldl);
                try {
                    String link = socialModel.getLinkedInLink();
                    if (link.contains("/in/")) {
                        String sub = link.substring(link.indexOf("/in/") + 4, link.length());
                        ((TextView) socialPopup.findViewById(R.id.link_text)).setText(sub);
                    } else {
                        String sub = link.substring(link.length() - 20, link.length());
                        ((TextView) socialPopup.findViewById(R.id.link_text)).setText(sub);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ((TextView) socialPopup.findViewById(R.id.link_text)).setText(contact.getSocialModel().getLinkedInLink());
                }
            }
            if (socialModel.getTwitterLink() != null) {
                //   ((ImageView) socialPopup.findViewById(R.id.link_icon)).setImageResource(R.drawable.icn_social_linkedin);
                Drawable colorl = new ColorDrawable(Color.parseColor("#2ca7e0"));
                Drawable imagel = getResources().getDrawable(R.drawable.ic_twitter_white);
                LayerDrawable ldl = new LayerDrawable(new Drawable[]{colorl, imagel});

                ((ImageView) socialPopup.findViewById(R.id.twitter_icon)).setImageDrawable(ldl);

                try {
                    /*String link = socialModel.getLinkedInLink();
                    String sub = link.substring(link.length() - 20, link.length());*/
                    if (socialModel.getTwitterLink().contains(".com/")) {
                        ((TextView) socialPopup.findViewById(R.id.twitterNick)).setText(socialModel.getTwitterLink().substring(socialModel.getTwitterLink().indexOf(".com/") + 5));
                    } else
                        ((TextView) socialPopup.findViewById(R.id.twitterNick)).setText(socialModel.getTwitterLink());
                } catch (Exception e) {
                    e.printStackTrace();

                }
            }

            if (socialModel.getYoutubeLink() != null) {
                //   ((ImageView) socialPopup.findViewById(R.id.link_icon)).setImageResource(R.drawable.icn_social_linkedin);
                Drawable colorl = new ColorDrawable(Color.parseColor("#ed2524"));
                Drawable imagel = getResources().getDrawable(R.drawable.ic_youtube_white);
                LayerDrawable ldl = new LayerDrawable(new Drawable[]{colorl, imagel});
                ((ImageView) socialPopup.findViewById(R.id.youtube_icon)).setImageDrawable(ldl);

                try {
                    /*String link = socialModel.getLinkedInLink();
                    String sub = link.substring(link.length() - 20, link.length());*/
                    if (socialModel.getYoutubeLink().contains("user/") || socialModel.getYoutubeLink().contains("channel/")) {
                        if (socialModel.getYoutubeLink().contains("user/")) {
                            String text = socialModel.getYoutubeLink().substring(socialModel.getYoutubeLink().indexOf("user/") + 5);
                            ((TextView) socialPopup.findViewById(R.id.youtubeNick)).setText(text);
                        } else if (socialModel.getYoutubeLink().contains("channel/")) {
                            String text = socialModel.getYoutubeLink().substring(socialModel.getYoutubeLink().indexOf("channel/") + 8);
                            ((TextView) socialPopup.findViewById(R.id.youtubeNick)).setText(text);
                        }
                    } else
                        ((TextView) socialPopup.findViewById(R.id.youtubeNick)).setText(socialModel.getYoutubeLink());
                } catch (Exception e) {
                    e.printStackTrace();
                    //((TextView) socialPopup.findViewById(R.id.link_text)).setText(contact.getSocialModel().getLinkedInLink());
                }
            }

            if (socialModel.getInstagramLink() != null) {
                //  ((ImageView) socialPopup.findViewById(R.id.instagram_icon)).setImageResource(R.drawable.icn_social_instagram);
                Drawable colori = new ColorDrawable(Color.parseColor("#8a3ab9"));
                Drawable imagei = getResources().getDrawable(R.drawable.icn_social_ints2);
                LayerDrawable ldi = new LayerDrawable(new Drawable[]{colori, imagei});
                ((ImageView) socialPopup.findViewById(R.id.instagram_icon)).setImageDrawable(ldi);

                String inst = contact.getSocialModel().getInstagramLink();

                if (inst.contains(".com/")) {
                    int ind = inst.indexOf(".com/");
                    String outLink = inst.substring(ind + 5, inst.length());

                    if (outLink.contains("?")) {
                        int in = outLink.indexOf("?");
                        outLink = outLink.substring(0, in);
                    }

                    ((TextView) socialPopup.findViewById(R.id.instagramNick)).setText(outLink);
                } else
                    ((TextView) socialPopup.findViewById(R.id.instagramNick)).setText(contact.getSocialModel().getInstagramLink());

            }

            if (socialModel.getMediumLink() != null) {
                //  ((ImageView) socialPopup.findViewById(R.id.instagram_icon)).setImageResource(R.drawable.icn_social_instagram);
                Drawable colori = new ColorDrawable(Color.parseColor("#000000"));
                Drawable imagei = getResources().getDrawable(R.drawable.medium_white);
                LayerDrawable ldi = new LayerDrawable(new Drawable[]{colori, imagei});
                ((ImageView) socialPopup.findViewById(R.id.medium_icon)).setImageDrawable(ldi);

                String inst = contact.getSocialModel().getMediumLink();

                if (inst.contains(".com/")) {
                    int ind = inst.indexOf(".com/");
                    String outLink = inst.substring(ind + 5, inst.length());

                    if (outLink.contains("?")) {
                        int in = outLink.indexOf("?");
                        outLink = outLink.substring(0, in);
                    }

                    ((TextView) socialPopup.findViewById(R.id.mediumNick)).setText(outLink);
                } else
                    ((TextView) socialPopup.findViewById(R.id.mediumNick)).setText(contact.getSocialModel().getMediumLink());

            }

            if (socialModel.getViberLink() != null) {
                //   ((ImageView) socialPopup.findViewById(R.id.viber_icon)).setImageResource(R.drawable.icn_social_viber);
                Drawable colorvi = new ColorDrawable(Color.parseColor("#6F3FAA"));
                Drawable imagevi = getResources().getDrawable(R.drawable.icn_social_viber2);
                LayerDrawable ldvi = new LayerDrawable(new Drawable[]{colorvi, imagevi});
                ((CircleImageView) socialPopup.findViewById(R.id.viber_icon)).setImageDrawable(ldvi);
                ((TextView) socialPopup.findViewById(R.id.viber_text)).setText(contact.getSocialModel().getViberLink());
            }
            if (socialModel.getWhatsappLink() != null) {

                //   ((ImageView) socialPopup.findViewById(R.id.whatsapp_icon)).setImageResource(R.drawable.icn_social_whatsapp);
                Drawable colorw = new ColorDrawable(Color.parseColor("#75B73B"));
                Drawable imagew = getResources().getDrawable(R.drawable.icn_social_whatsapp3);
                LayerDrawable ldw = new LayerDrawable(new Drawable[]{colorw, imagew});
                ((CircleImageView) socialPopup.findViewById(R.id.whatsapp_icon)).setImageDrawable(ldw);

                ((TextView) socialPopup.findViewById(R.id.whatsapp_text)).setText(contact.getSocialModel().getWhatsappLink());
            }
            if (socialModel.getTelegramLink() != null) {
                String tel = contact.getSocialModel().getTelegramLink();
                if (tel.contains(".me/")) {
                    int indexx = tel.indexOf(".me/");
                    String outLink = tel.substring(indexx + 4, tel.length());
                    ((TextView) socialPopup.findViewById(R.id.telegram_text)).setText(outLink);
                } else if (tel.contains("?p=")) {
                    int indexx = tel.indexOf("?p=");
                    String outLink = tel.substring(indexx + 3, tel.length());
                    ((TextView) socialPopup.findViewById(R.id.telegram_text)).setText(outLink);

                } else
                    ((TextView) socialPopup.findViewById(R.id.telegram_text)).setText(contact.getSocialModel().getTelegramLink());
            }

            if (socialModel.getSkypeLink() != null) {
                //((ImageView) socialPopup.findViewById(R.id.skype_icon)).setImageResource(R.drawable.icn_social_skype);
                Drawable colors = new ColorDrawable(Color.parseColor("#1eb8ff"));
                Drawable images = getResources().getDrawable(R.drawable.icn_social_skype2);
                LayerDrawable lds = new LayerDrawable(new Drawable[]{colors, images});
                ((CircleImageView) socialPopup.findViewById(R.id.skype_icon)).setImageDrawable(lds);

                ((TextView) socialPopup.findViewById(R.id.skype_text)).setText(contact.getSocialModel().getSkypeLink());
            }
        }


        socialPopup.findViewById(R.id.facebook_social).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (popupProfileEditPreviewSocial != null && popupProfileEditPreviewSocial.getVisibility() == View.VISIBLE) {
                    popupProfileEditPreviewSocial.setVisibility(View.GONE);
                    return;
                }

                editFrame.setVisibility(View.GONE);

                ((EditText) editFrame.findViewById(R.id.dataToEdit)).setText("");
                if (contact.getSocialModel() != null)
                    if (contact.getSocialModel().getFacebookLink() != null)
                        ((EditText) editFrame.findViewById(R.id.dataToEdit)).setText(contact.getSocialModel().getFacebookLink());


                // if (((ContactProfileDataFragment) parentFragment).popupProfileEdit != null && ((ContactProfileDataFragment) parentFragment).popupProfileEdit.getVisibility() != View.VISIBLE)
                //     ((ContactProfileDataFragment) parentFragment).showEditPopupPreview(contactInfo1, ((TextView)v.findViewById(R.id.hashtag_text)).getText().toString());

                if (contact.getSocialModel() != null && contact.getSocialModel().getFacebookLink() != null) {
                    showEditPopupPreviewSocial(contact, SocialEnums.FACEBOOK);

                } else {
                    editFrame.setVisibility(View.VISIBLE);


                    //if(contact.listOfContacts == null || contact.listOfContacts.isEmpty()) {
                    editFrame.findViewById(R.id.ok_social).setVisibility(View.GONE);
                    editFrame.findViewById(R.id.get_last_clips).setVisibility(View.VISIBLE);
                    editFrame.findViewById(R.id.imageEditSocial).setVisibility(View.VISIBLE);
                    editFrame.findViewById(R.id.searchSocial).setVisibility(View.VISIBLE);
                    editFrame.findViewById(R.id.frameImageEditSocialInsta).setVisibility(View.GONE);

                    Drawable color = new ColorDrawable(Color.parseColor("#e2e5e8"));
                    Drawable image = getActivity().getResources().getDrawable(R.drawable.icn_social_facebook2);
                    LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
                    ((ImageView) editFrame.findViewById(R.id.imageEditSocial)).setImageDrawable(ld);


                    ((EditText) editFrame.findViewById(R.id.dataToEdit)).setHint("Add link, username or search");
                    ((EditText) editFrame.findViewById(R.id.dataToEdit)).setPadding(0, 0, 150, 0);
                    /*}else{
                        ((EditText)editFrame.findViewById(R.id.dataToEdit)).setPadding(0,0,0,0);
                        editFrame.findViewById(R.id.imageEditSocial).setVisibility(View.GONE);
                        editFrame.findViewById(R.id.searchSocial).setVisibility(View.GONE);
                        editFrame.findViewById(R.id.frameImageEditSocialInsta).setVisibility(View.GONE);
                        ((EditText) editFrame.findViewById(R.id.dataToEdit)).setHint("Add link or username");
                    }*/

                }

                editFrame.findViewById(R.id.get_last_clips).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String socialLinkClip = "";
                        for (Clipboard cl : FabNotificationService.clipboard) { //NullPointerException
                            if (cl.getListClipboards() != null && !cl.getListClipboards().isEmpty()) {
                                boolean checkFind = false;
                                for (Clipboard cl2 : cl.getListClipboards()) {
                                    if (cl2.getType().equals(ClipboardEnum.FACEBOOK)) {
                                        socialLinkClip = cl2.getValueCopy();
                                        checkFind = true;
                                        break;
                                    }
                                }
                                if (checkFind) break;
                            } else {
                                if (cl.getType().equals(ClipboardEnum.FACEBOOK)) {
                                    socialLinkClip = cl.getValueCopy();
                                    break;
                                }
                            }
                        }
                        ((EditText) editFrame.findViewById(R.id.dataToEdit)).setText(socialLinkClip);
                    }
                });

                editFrame.findViewById(R.id.imageEditSocial).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String name = contact.getName();
                        String[] mach = name.split(" ");

                        Intent i = new Intent(Intent.ACTION_VIEW);
                        if (mach.length >= 2) {
                            String str = "";
                            for (int i2 = 0; i2 < mach.length; i2++) {
                                str += mach[i2];
                                if (i2 != mach.length - 1) str += "%20";
                            }

                            i.setData(Uri.parse("https://www.facebook.com/search/people/?q=" + str));
                        } else
                            i.setData(Uri.parse("https://www.facebook.com/search/people/?q=" + mach[0]));
                        startActivity(i);
                    }
                });

                editFrame.findViewById(R.id.searchSocial).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String name = contact.getName();
                        String[] mach = name.split(" ");

                        Intent i = new Intent(Intent.ACTION_VIEW);
                        if (mach.length >= 2) {
                            String str = "";
                            for (int i2 = 0; i2 < mach.length; i2++) {
                                str += mach[i2];
                                if (i2 != mach.length - 1) str += "%20";
                            }
                            i.setData(Uri.parse("https://www.facebook.com/search/people/?q=" + str));
                        } else
                            i.setData(Uri.parse("https://www.facebook.com/search/people/?q=" + mach[0]));
                        startActivity(i);

                    }
                });


                editFrame.findViewById(R.id.imageEditSocial).setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {


                        ImageView cImg = ((ImageView) editFrame.findViewById(R.id.imageEditSocial));
                        //    ImageView imageView = ((ImageView) profilePopUp.findViewById(R.id.imagePreviewIcon));
                        //  FrameLayout frameLayout = ((FrameLayout) mainView.findViewById(R.id.company_profile_block));
                        switch (motionEvent.getAction()) {
                            case MotionEvent.ACTION_DOWN: {

                                if (checkClick_facebookEdit) {
                                    checkClick_facebookEdit = false;
                                    int colorFrom = Color.parseColor("#e2e5e8");
                                    int colorTo = Color.parseColor("#F9A825");
                                    Drawable image = getResources().getDrawable(R.drawable.icn_social_facebook2);
                                    AnimColorMessenger.ActionDown_(colorFrom, colorTo, image, cImg);
                                }

                                //     OnTouchMethod(textView);
                                break;
                            }
                            case MotionEvent.ACTION_UP: {
                                if (!checkClick_facebookEdit) {
                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#e2e5e8");
                                    Drawable image = getResources().getDrawable(R.drawable.icn_social_facebook2);
                                    AnimColorMessenger.ActionUp_(colorFrom, colorTo, image, cImg);
                                }

                                break;
                            }
                            case MotionEvent.ACTION_CANCEL: {

                                int colorFrom = Color.parseColor("#F9A825");
                                int colorTo = Color.parseColor("#e2e5e8");
                                Drawable image = getResources().getDrawable(R.drawable.icn_social_facebook2);
                                AnimColorMessenger.ActionCancel_(colorFrom, colorTo, image, cImg);

                                break;
                            }
                            case MotionEvent.ACTION_MOVE: {
                                if (!checkClick_facebookEdit) {

                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#e2e5e8");
                                    Drawable image = getResources().getDrawable(R.drawable.icn_social_facebook2);
                                    checkClick_facebookEdit = AnimColorMessenger.ActionMove_(colorFrom, colorTo, image, cImg, motionEvent, mainView);

                                }

                                break;
                            }
                        }

                        return false;
                    }
                });


                openedViews.add(editFrame);
                ((TextView) editFrame.findViewById(R.id.typeField)).setText("facebook");

                editFrame.findViewById(R.id.updateContactField).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        Realm realm = Realm.getDefaultInstance(); //-
                        realm.beginTransaction();
                        SocialModel socialModel;
                        if (contact.getSocialModel() == null) {
                            socialModel = realm.createObject(SocialModel.class);
                        } else
                            socialModel = contact.getSocialModel();

                        if (((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().length() > 0) {
                            //   if (contact.getSocialModel() != null)


                            if (socialModel.getFacebookLink() != null && !socialModel.getFacebookLink().isEmpty()) {
                                contactsService.deleteNoteContact(contact.getIdContact(), socialModel.getFacebookLink());
                            }


                            String f = ((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().toString();

                            if (!f.contains(".com/")) {
                                f = "https://www.facebook.com/" + f;
                            }

                            if (ClipboardType.isFacebook(f)) {
                                socialModel.setFacebookLink(f);
                            } else {
                                socialModel.setFacebookLink(((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().toString());
                            }

                            //socialModel.setFacebookLink(((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().toString());

                            //       ((ImageView) socialPopup.findViewById(R.id.facebook_icon)).setImageResource(R.drawable.icn_social_facebook);
                            //       ((ImageView) getActivity().findViewById(R.id.facebook_icon)).setImageResource(R.drawable.icn_social_facebook);
                            Drawable colorf = new ColorDrawable(Color.parseColor("#475993"));
                            Drawable imagef = getResources().getDrawable(R.drawable.icn_social_facebook2);
                            LayerDrawable ldf = new LayerDrawable(new Drawable[]{colorf, imagef});
                            ((ImageView) socialPopup.findViewById(R.id.facebook_icon)).setImageDrawable(ldf);


                           /* ((ImageView) getActivity().findViewById(R.id.facebook_icon)).setImageDrawable(ldf);

                            try{
                                ((ImageView) profilePopUp.findViewById(R.id.facebook_icon)).setImageDrawable(ldf);
                            }catch (Exception e){

                            }*/

                            if (profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE) {
                                ((ImageView) profilePopUp.findViewById(R.id.facebook_icon)).setImageDrawable(ldf);
                            } else if (companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE) {
                                ((ImageView) companyProfilePopup.findViewById(R.id.facebook_icon)).setImageDrawable(ldf);
                            } else {
                                ((ImageView) getActivity().findViewById(R.id.facebook_icon)).setImageDrawable(ldf);
                            }

                          /*  try{
                                ((ImageView) companyProfilePopup.findViewById(R.id.facebook_icon)).setImageDrawable(ldf);
                            }catch (Exception e){

                            }*/


                            String link = socialModel.getFacebookLink();
                            boolean checkich = false;
                            try {
                                String checkFacebook = link.substring(0, 37);
                                if (checkFacebook.equals("https://www.facebook.com/profile.php?")) {
                                    link = link.substring(37, link.length());
                                    ((TextView) socialPopup.findViewById(R.id.facebook_nick)).setText(link);
                                    checkich = true;
                                }
                            } catch (Exception e) {

                                e.printStackTrace();
                            }
                            if (!checkich) {
                                try {
                                    if (link.contains("https://facebook.com/")) {
                                        link = link.substring(21, link.length());
                                        if (link.contains("profile.php")) {
                                            link = link.substring(link.indexOf(".php") + 5, link.length());
                                        }
                                        ((TextView) socialPopup.findViewById(R.id.facebook_nick)).setText(link);
                                        checkich = true;
                                    } else if (link.contains("https://m.facebook.com/")) {
                                        link = link.substring(23, link.length());
                                        if (link.contains("profile.php")) {
                                            link = link.substring(link.indexOf(".php") + 5, link.length());
                                        }
                                        ((TextView) socialPopup.findViewById(R.id.facebook_nick)).setText(link);
                                        checkich = true;
                                    } else if (link.contains("php?")) {
                                        int index = link.indexOf("php?");
                                        link = link.substring(index + 4, link.length());
                                        ((TextView) socialPopup.findViewById(R.id.facebook_nick)).setText(link);
                                        checkich = true;
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            if (!checkich)
                                ((TextView) socialPopup.findViewById(R.id.facebook_nick)).setText(socialModel.getFacebookLink());

                            //   ((TextView) socialPopup.findViewById(R.id.facebook_nick)).setText(socialModel.getFacebookLink());
                            contact.hasFacebook = true;
                            contact.setSocialModel(socialModel);
                            realm.commitTransaction();
                            contactsService.addNoteToContact(contact.getIdContact(), contact.getSocialModel().getFacebookLink(), contact.getName());
                            ContactCacheService.updateContact(contact, mainView.getContext());
                            //  EventBus.getDefault().post(new UpdateFile());

                            editFrame.setVisibility(View.GONE);
                        } else {
                            if (socialModel.getFacebookLink() != null && !socialModel.getFacebookLink().isEmpty()) {
                                contactsService.deleteNoteContact(contact.getIdContact(), socialModel.getFacebookLink());
                            }
                            contact.getSocialModel().setFacebookLink(null);
                            //  ((ImageView) socialPopup.findViewById(R.id.facebook_icon)).setImageResource(R.drawable.icn_social_facebook_gray);
                            //  ((ImageView) getActivity().findViewById(R.id.facebook_icon)).setImageResource(R.drawable.icn_social_facebook_gray);
                            Drawable color = new ColorDrawable(Color.parseColor("#e2e5e8"));
                            Drawable image = getResources().getDrawable(R.drawable.icn_social_facebook2);
                            LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
                            ((ImageView) socialPopup.findViewById(R.id.facebook_icon)).setImageDrawable(ld);

                           /* ((ImageView) getActivity().findViewById(R.id.facebook_icon)).setImageDrawable(ld);


                            try{
                                ((ImageView) profilePopUp.findViewById(R.id.facebook_icon)).setImageDrawable(ld);
                            }catch (Exception e){

                            }

                            try{
                                ((ImageView) companyProfilePopup.findViewById(R.id.facebook_icon)).setImageDrawable(ld);
                            }catch (Exception e){

                            }*/


                            if (profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE) {
                                ((ImageView) profilePopUp.findViewById(R.id.facebook_icon)).setImageDrawable(ld);
                            } else if (companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE) {
                                ((ImageView) companyProfilePopup.findViewById(R.id.facebook_icon)).setImageDrawable(ld);
                            } else {
                                ((ImageView) getActivity().findViewById(R.id.facebook_icon)).setImageDrawable(ld);
                            }


                            ((TextView) socialPopup.findViewById(R.id.facebook_nick)).setText("add username or link");
                            contact.hasFacebook = false;
                            contact.setSocialModel(socialModel);
                            realm.commitTransaction();
                            ContactCacheService.updateContact(contact, mainView.getContext());
                            // EventBus.getDefault().post(new UpdateFile());
                            editFrame.setVisibility(View.GONE);
                        }

                        //initIconColor(contact, mainView);

                        realm.close();

                        initIconColor(contact, mainView);


                        //UPDATE
                        /*ArrayList<String> listEdit = new ArrayList<>();
                        ArrayList<Boolean> listEditBool = new ArrayList<>();

                        listEdit.add(contact.getName());
                        if (contact.listOfContacts == null || contact.listOfContacts.isEmpty())
                            listEditBool.add(true);
                        else
                            listEditBool.add(false);

                        MainActivity.listToManyUpdateFile.add("EDIT");
                        MainActivity.listToManyUpdateFile.add(listEdit);
                        MainActivity.listToManyUpdateFile.add(listEditBool);*/

                        //EventBus.getDefault().post(new UpdateFile());

                    }
                });
                editFrame.findViewById(R.id.cancelEditField).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editFrame.setVisibility(View.GONE);
                    }
                });
            }
        });


        socialPopup.findViewById(R.id.vk_link).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupProfileEditPreviewSocial != null && popupProfileEditPreviewSocial.getVisibility() == View.VISIBLE) {
                    popupProfileEditPreviewSocial.setVisibility(View.GONE);
                    return;
                }
                editFrame.setVisibility(View.GONE);

                ((EditText) editFrame.findViewById(R.id.dataToEdit)).setText("");
                if (contact.getSocialModel() != null)
                    if (contact.getSocialModel().getVkLink() != null)
                        ((EditText) editFrame.findViewById(R.id.dataToEdit)).setText(contact.getSocialModel().getVkLink());


                if (contact.getSocialModel() != null && contact.getSocialModel().getVkLink() != null)
                    showEditPopupPreviewSocial(contact, SocialEnums.VK);
                else {
                    editFrame.setVisibility(View.VISIBLE);


                    //if(contact.listOfContacts == null || contact.listOfContacts.isEmpty()) {
                    editFrame.findViewById(R.id.ok_social).setVisibility(View.GONE);
                    editFrame.findViewById(R.id.get_last_clips).setVisibility(View.VISIBLE);
                    editFrame.findViewById(R.id.imageEditSocial).setVisibility(View.VISIBLE);
                    editFrame.findViewById(R.id.searchSocial).setVisibility(View.VISIBLE);
                    editFrame.findViewById(R.id.frameImageEditSocialInsta).setVisibility(View.GONE);

                    Drawable color = new ColorDrawable(Color.parseColor("#e2e5e8"));
                    Drawable image = getResources().getDrawable(R.drawable.icn_social_vk2);
                    LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
                    ((ImageView) editFrame.findViewById(R.id.imageEditSocial)).setImageDrawable(ld);
                    ((EditText) editFrame.findViewById(R.id.dataToEdit)).setHint("Add link, username or search");
                    ((EditText) editFrame.findViewById(R.id.dataToEdit)).setPadding(0, 0, 150, 0);
                    /*}else{
                        ((EditText)editFrame.findViewById(R.id.dataToEdit)).setPadding(0,0,0,0);
                        editFrame.findViewById(R.id.imageEditSocial).setVisibility(View.GONE);
                        editFrame.findViewById(R.id.searchSocial).setVisibility(View.GONE);
                        editFrame.findViewById(R.id.frameImageEditSocialInsta).setVisibility(View.GONE);
                        ((EditText) editFrame.findViewById(R.id.dataToEdit)).setHint("Add link or username");
                    }*/
                }

                editFrame.findViewById(R.id.get_last_clips).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String socialLinkClip = "";
                        for (Clipboard cl : FabNotificationService.clipboard) { //NullPointerException
                            if (cl.getListClipboards() != null && !cl.getListClipboards().isEmpty()) {
                                boolean checkFind = false;
                                for (Clipboard cl2 : cl.getListClipboards()) {
                                    if (cl2.getType().equals(ClipboardEnum.VK)) {
                                        socialLinkClip = cl2.getValueCopy();
                                        checkFind = true;
                                        break;
                                    }
                                }
                                if (checkFind) break;
                            } else {
                                if (cl.getType().equals(ClipboardEnum.VK)) {
                                    socialLinkClip = cl.getValueCopy();
                                    break;
                                }
                            }
                        }
                        ((EditText) editFrame.findViewById(R.id.dataToEdit)).setText(socialLinkClip);
                    }
                });


                editFrame.findViewById(R.id.imageEditSocial).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                editFrame.findViewById(R.id.searchSocial).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });


                editFrame.findViewById(R.id.imageEditSocial).setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {


                        ImageView cImg = ((ImageView) editFrame.findViewById(R.id.imageEditSocial));
                        //    ImageView imageView = ((ImageView) profilePopUp.findViewById(R.id.imagePreviewIcon));
                        //  FrameLayout frameLayout = ((FrameLayout) mainView.findViewById(R.id.company_profile_block));
                        switch (motionEvent.getAction()) {
                            case MotionEvent.ACTION_DOWN: {

                                if (checkClick_facebookEdit) {
                                    checkClick_facebookEdit = false;
                                    int colorFrom = Color.parseColor("#e2e5e8");
                                    int colorTo = Color.parseColor("#F9A825");
                                    Drawable image = getResources().getDrawable(R.drawable.icn_social_vk2);
                                    AnimColorMessenger.ActionDown_(colorFrom, colorTo, image, cImg);
                                }

                                //     OnTouchMethod(textView);
                                break;
                            }
                            case MotionEvent.ACTION_UP: {
                                if (!checkClick_facebookEdit) {
                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#e2e5e8");
                                    Drawable image = getResources().getDrawable(R.drawable.icn_social_vk2);
                                    AnimColorMessenger.ActionUp_(colorFrom, colorTo, image, cImg);
                                }

                                break;
                            }
                            case MotionEvent.ACTION_CANCEL: {

                                int colorFrom = Color.parseColor("#F9A825");
                                int colorTo = Color.parseColor("#e2e5e8");
                                Drawable image = getResources().getDrawable(R.drawable.icn_social_vk2);
                                AnimColorMessenger.ActionCancel_(colorFrom, colorTo, image, cImg);

                                break;
                            }
                            case MotionEvent.ACTION_MOVE: {
                                if (!checkClick_facebookEdit) {

                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#e2e5e8");
                                    Drawable image = getResources().getDrawable(R.drawable.icn_social_vk2);
                                    checkClick_facebookEdit = AnimColorMessenger.ActionMove_(colorFrom, colorTo, image, cImg, motionEvent, mainView);

                                }

                                break;
                            }
                        }

                        return false;
                    }
                });


                //editFrame.setVisibility(View.VISIBLE);
                openedViews.add(editFrame);
                ((TextView) editFrame.findViewById(R.id.typeField)).setText("vkontakte");


                editFrame.findViewById(R.id.updateContactField).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        Realm realm = Realm.getDefaultInstance(); //-
                        realm.beginTransaction();
                        SocialModel socialModel;
                        if (contact.getSocialModel() == null) {
                            socialModel = realm.createObject(SocialModel.class);
                        } else
                            socialModel = contact.getSocialModel();

                        if (((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().length() > 0) {
                            //     if (contact.getSocialModel() != null)

                            if (socialModel.getVkLink() != null && !socialModel.getVkLink().isEmpty()) {
                                contactsService.deleteNoteContact(contact.getIdContact(), socialModel.getVkLink());
                            }

                            String f = ((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().toString();

                            if (!f.contains(".com/")) {
                                f = "https://vk.com/" + f;
                            }

                            if (ClipboardType.isVk(f)) {
                                socialModel.setVkLink(f);
                            } else {
                                socialModel.setVkLink(((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().toString());
                            }

                            //socialModel.setVkLink(((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().toString());
                            //     ((ImageView) socialPopup.findViewById(R.id.vk_icon)).setImageResource(R.drawable.icn_social_vk);
                            //     ((ImageView) getActivity().findViewById(R.id.vk_icon)).setImageResource(R.drawable.icn_social_vk);
                            Drawable colorv = new ColorDrawable(Color.parseColor("#507299"));
                            Drawable imagev = getResources().getDrawable(R.drawable.icn_social_vk2);
                            LayerDrawable ldv = new LayerDrawable(new Drawable[]{colorv, imagev});
                            ((ImageView) socialPopup.findViewById(R.id.vk_icon)).setImageDrawable(ldv);

                          /*  ((ImageView) getActivity().findViewById(R.id.vk_icon)).setImageDrawable(ldv);

                            try{
                                ((ImageView) profilePopUp.findViewById(R.id.vk_icon)).setImageDrawable(ldv);
                            }catch (Exception e){

                            }

                            try{
                                ((ImageView) companyProfilePopup.findViewById(R.id.vk_icon)).setImageDrawable(ldv);
                            }catch (Exception e){

                            }*/


                            if (profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE) {
                                ((ImageView) profilePopUp.findViewById(R.id.vk_icon)).setImageDrawable(ldv);
                            } else if (companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE) {
                                ((ImageView) companyProfilePopup.findViewById(R.id.vk_icon)).setImageDrawable(ldv);
                            } else {
                                ((ImageView) getActivity().findViewById(R.id.vk_icon)).setImageDrawable(ldv);
                            }


                            try {
                                String link = socialModel.getVkLink();
                                String checkVK = link.substring(0, 8);
                                if (link.contains("https://vk.com/")) {

                                    link = link.substring(15, link.length());
                                    ((TextView) socialPopup.findViewById(R.id.vkNick)).setText(link);
                                } else if (link.contains("https://m.vk.com/")) {
                                    link = link.substring(17, link.length());
                                    ((TextView) socialPopup.findViewById(R.id.vkNick)).setText(link);

                                } else if (link.contains("m.vk.com/")) {
                                    link = link.substring(9, link.length());
                                    ((TextView) socialPopup.findViewById(R.id.vkNick)).setText(link);
                                } else if (link.contains("vk.com/")) {
                                    link = link.substring(7, link.length());
                                    ((TextView) socialPopup.findViewById(R.id.vkNick)).setText(link);
                                } else if (link.contains("https://www.vk.com/")) {
                                    link = link.substring(19, link.length());
                                    ((TextView) socialPopup.findViewById(R.id.vkNick)).setText(link);

                                } else
                                    ((TextView) socialPopup.findViewById(R.id.vkNick)).setText(socialModel.getVkLink());
                            } catch (Exception e) {
                                ((TextView) socialPopup.findViewById(R.id.vkNick)).setText(socialModel.getVkLink());
                            }

                            //        if (contact.getSocialModel() != null)
                            //    ((TextView) socialPopup.findViewById(R.id.vkNick)).setText(socialModel.getVkLink());
                            contact.hasVk = true;
                            contact.setSocialModel(socialModel);
                            realm.commitTransaction();
                            contactsService.addNoteToContact(contact.getIdContact(), contact.getSocialModel().getVkLink(), contact.getName());
                            ContactCacheService.updateContact(contact, mainView.getContext());
                            // EventBus.getDefault().post(new UpdateFile());
                            editFrame.setVisibility(View.GONE);
                        } else {
                            if (socialModel.getVkLink() != null && !socialModel.getVkLink().isEmpty()) {
                                contactsService.deleteNoteContact(contact.getIdContact(), socialModel.getVkLink());
                            }
                            contact.getSocialModel().setVkLink(null);
                            //     ((ImageView) socialPopup.findViewById(R.id.vk_icon)).setImageResource(R.drawable.icn_social_vk_gray);
                            //     ((ImageView) getActivity().findViewById(R.id.vk_icon)).setImageResource(R.drawable.icn_social_vk_gray);
                            Drawable color2 = new ColorDrawable(Color.parseColor("#e2e5e8"));
                            Drawable image2 = getResources().getDrawable(R.drawable.icn_social_vk2);
                            LayerDrawable ld2 = new LayerDrawable(new Drawable[]{color2, image2});
                            ((ImageView) socialPopup.findViewById(R.id.vk_icon)).setImageDrawable(ld2);
                         /*   ((ImageView) getActivity().findViewById(R.id.vk_icon)).setImageDrawable(ld2);

                            try{
                                ((ImageView) profilePopUp.findViewById(R.id.facebook_icon)).setImageDrawable(ld2);
                            }catch (Exception e){

                            }

                            try{
                                ((ImageView) companyProfilePopup.findViewById(R.id.facebook_icon)).setImageDrawable(ld2);
                            }catch (Exception e){

                            }*/

                            if (profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE) {
                                ((ImageView) profilePopUp.findViewById(R.id.vk_icon)).setImageDrawable(ld2);
                            } else if (companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE) {
                                ((ImageView) companyProfilePopup.findViewById(R.id.vk_icon)).setImageDrawable(ld2);
                            } else {
                                ((ImageView) getActivity().findViewById(R.id.vk_icon)).setImageDrawable(ld2);
                            }


                            ((TextView) socialPopup.findViewById(R.id.vkNick)).setText("add username or");
                            contact.hasVk = false;
                            contact.setSocialModel(socialModel);
                            realm.commitTransaction();
                            ContactCacheService.updateContact(contact, mainView.getContext());
                            //  EventBus.getDefault().post(new UpdateFile());
                            editFrame.setVisibility(View.GONE);
                        }


                        realm.close();

                        //initIconColor(contact, mainView);

                        initIconColor(contact, mainView);


                        //EventBus.getDefault().post(new UpdateFile());

                    }
                });
                editFrame.findViewById(R.id.cancelEditField).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editFrame.setVisibility(View.GONE);
                    }
                });
            }
        });


        //==============================================================================================================================add new Social


        socialPopup.findViewById(R.id.medium_link).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupProfileEditPreviewSocial != null && popupProfileEditPreviewSocial.getVisibility() == View.VISIBLE) {
                    popupProfileEditPreviewSocial.setVisibility(View.GONE);
                    return;
                }
                editFrame.setVisibility(View.GONE);

                ((EditText) editFrame.findViewById(R.id.dataToEdit)).setText("");
                if (contact.getSocialModel() != null)
                    if (contact.getSocialModel().getMediumLink() != null)
                        ((EditText) editFrame.findViewById(R.id.dataToEdit)).setText(contact.getSocialModel().getMediumLink());


                if (contact.getSocialModel() != null && contact.getSocialModel().getMediumLink() != null)
                    showEditPopupPreviewSocial(contact, SocialEnums.MEDIUM);
                else {
                    editFrame.setVisibility(View.VISIBLE);


                    //if(contact.listOfContacts == null || contact.listOfContacts.isEmpty()) {
                    editFrame.findViewById(R.id.ok_social).setVisibility(View.GONE);
                    editFrame.findViewById(R.id.get_last_clips).setVisibility(View.VISIBLE);
                    editFrame.findViewById(R.id.imageEditSocial).setVisibility(View.VISIBLE);
                    editFrame.findViewById(R.id.searchSocial).setVisibility(View.VISIBLE);
                    editFrame.findViewById(R.id.frameImageEditSocialInsta).setVisibility(View.GONE);

                    Drawable color = new ColorDrawable(Color.parseColor("#e2e5e8"));
                    Drawable image = getResources().getDrawable(R.drawable.medium_white);
                    LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
                    ((ImageView) editFrame.findViewById(R.id.imageEditSocial)).setImageDrawable(ld);
                    ((EditText) editFrame.findViewById(R.id.dataToEdit)).setHint("Add link, username or search");
                    ((EditText) editFrame.findViewById(R.id.dataToEdit)).setPadding(0, 0, 150, 0);
                    /*}else{
                        ((EditText)editFrame.findViewById(R.id.dataToEdit)).setPadding(0,0,0,0);
                        editFrame.findViewById(R.id.imageEditSocial).setVisibility(View.GONE);
                        editFrame.findViewById(R.id.searchSocial).setVisibility(View.GONE);
                        editFrame.findViewById(R.id.frameImageEditSocialInsta).setVisibility(View.GONE);
                        ((EditText) editFrame.findViewById(R.id.dataToEdit)).setHint("Add link or username");
                    }*/
                }

                editFrame.findViewById(R.id.get_last_clips).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String socialLinkClip = "";
                        for (Clipboard cl : FabNotificationService.clipboard) { //NullPointerException
                            if (cl.getListClipboards() != null && !cl.getListClipboards().isEmpty()) {
                                boolean checkFind = false;
                                for (Clipboard cl2 : cl.getListClipboards()) {
                                    if (cl2.getType().equals(ClipboardEnum.MEDIUM)) {
                                        socialLinkClip = cl2.getValueCopy();
                                        checkFind = true;
                                        break;
                                    }
                                }
                                if (checkFind) break;
                            } else {
                                if (cl.getType().equals(ClipboardEnum.MEDIUM)) {
                                    socialLinkClip = cl.getValueCopy();
                                    break;
                                }
                            }
                        }
                        ((EditText) editFrame.findViewById(R.id.dataToEdit)).setText(socialLinkClip);
                    }
                });


                editFrame.findViewById(R.id.imageEditSocial).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                editFrame.findViewById(R.id.searchSocial).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });


                editFrame.findViewById(R.id.imageEditSocial).setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {


                        ImageView cImg = ((ImageView) editFrame.findViewById(R.id.imageEditSocial));
                        //    ImageView imageView = ((ImageView) profilePopUp.findViewById(R.id.imagePreviewIcon));
                        //  FrameLayout frameLayout = ((FrameLayout) mainView.findViewById(R.id.company_profile_block));
                        switch (motionEvent.getAction()) {
                            case MotionEvent.ACTION_DOWN: {

                                if (checkClick_facebookEdit) {
                                    checkClick_facebookEdit = false;
                                    int colorFrom = Color.parseColor("#e2e5e8");
                                    int colorTo = Color.parseColor("#F9A825");
                                    Drawable image = getResources().getDrawable(R.drawable.medium_white);
                                    AnimColorMessenger.ActionDown_(colorFrom, colorTo, image, cImg);
                                }

                                //     OnTouchMethod(textView);
                                break;
                            }
                            case MotionEvent.ACTION_UP: {
                                if (!checkClick_facebookEdit) {
                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#e2e5e8");
                                    Drawable image = getResources().getDrawable(R.drawable.medium_white);
                                    AnimColorMessenger.ActionUp_(colorFrom, colorTo, image, cImg);
                                }

                                break;
                            }
                            case MotionEvent.ACTION_CANCEL: {

                                int colorFrom = Color.parseColor("#F9A825");
                                int colorTo = Color.parseColor("#e2e5e8");
                                Drawable image = getResources().getDrawable(R.drawable.medium_white);
                                AnimColorMessenger.ActionCancel_(colorFrom, colorTo, image, cImg);

                                break;
                            }
                            case MotionEvent.ACTION_MOVE: {
                                if (!checkClick_facebookEdit) {

                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#e2e5e8");
                                    Drawable image = getResources().getDrawable(R.drawable.medium_white);
                                    checkClick_facebookEdit = AnimColorMessenger.ActionMove_(colorFrom, colorTo, image, cImg, motionEvent, mainView);

                                }

                                break;
                            }
                        }

                        return false;
                    }
                });


                //editFrame.setVisibility(View.VISIBLE);
                openedViews.add(editFrame);
                ((TextView) editFrame.findViewById(R.id.typeField)).setText("medium");


                editFrame.findViewById(R.id.updateContactField).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        Realm realm = Realm.getDefaultInstance(); //+
                        realm.beginTransaction();
                        SocialModel socialModel;
                        if (contact.getSocialModel() == null) {
                            socialModel = realm.createObject(SocialModel.class);
                        } else
                            socialModel = contact.getSocialModel();

                        if (((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().length() > 0) {
                            //     if (contact.getSocialModel() != null)

                            if (socialModel.getMediumLink() != null && !socialModel.getMediumLink().isEmpty()) {
                                contactsService.deleteNoteContact(contact.getIdContact(), socialModel.getMediumLink());
                            }


                            String f = ((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().toString();

                            if (!f.contains(".com/")) {
                                if (!f.contains("@"))
                                    f = "https://medium.com/@" + f;
                                else
                                    f = "https://medium.com/" + f;
                            }

                            if (ClipboardType.isMedium(f)) {
                                socialModel.setMediumLink(f);
                            } else {
                                socialModel.setMediumLink(((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().toString());
                            }

                            //socialModel.setMediumLink(((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().toString());
                            //     ((ImageView) socialPopup.findViewById(R.id.vk_icon)).setImageResource(R.drawable.icn_social_vk);
                            //     ((ImageView) getActivity().findViewById(R.id.vk_icon)).setImageResource(R.drawable.icn_social_vk);
                            Drawable colorv = new ColorDrawable(Color.parseColor("#000000"));
                            Drawable imagev = getResources().getDrawable(R.drawable.medium_white);
                            LayerDrawable ldv = new LayerDrawable(new Drawable[]{colorv, imagev});
                            ((ImageView) socialPopup.findViewById(R.id.medium_icon)).setImageDrawable(ldv);

                          /*  ((ImageView) getActivity().findViewById(R.id.vk_icon)).setImageDrawable(ldv);

                            try{
                                ((ImageView) profilePopUp.findViewById(R.id.vk_icon)).setImageDrawable(ldv);
                            }catch (Exception e){

                            }

                            try{
                                ((ImageView) companyProfilePopup.findViewById(R.id.vk_icon)).setImageDrawable(ldv);
                            }catch (Exception e){

                            }*/


                            if (profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE) {
                                ((ImageView) profilePopUp.findViewById(R.id.medium_icon)).setImageDrawable(ldv);
                            } else if (companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE) {
                                ((ImageView) companyProfilePopup.findViewById(R.id.medium_icon)).setImageDrawable(ldv);
                            } else {
                                ((ImageView) getActivity().findViewById(R.id.medium_icon)).setImageDrawable(ldv);
                            }


                            try {
                                String link = socialModel.getMediumLink();
                                String checkVK = link.substring(0, 8);
                                if (link.contains("com/@")) {
                                    //System.out.println("TRUE VK LINK");
                                    link = link.substring(link.indexOf("com/") + 4, link.length());
                                    //System.out.println("TRUE VK LINK2 = " + link);
                                    ((TextView) socialPopup.findViewById(R.id.mediumNick)).setText(link);
                                } else
                                    ((TextView) socialPopup.findViewById(R.id.mediumNick)).setText(socialModel.getMediumLink());
                            } catch (Exception e) {
                                ((TextView) socialPopup.findViewById(R.id.mediumNick)).setText(socialModel.getMediumLink());
                            }

                            //        if (contact.getSocialModel() != null)
                            //    ((TextView) socialPopup.findViewById(R.id.vkNick)).setText(socialModel.getVkLink());
                            contact.hasMedium = true;
                            contact.setSocialModel(socialModel);
                            realm.commitTransaction();
                            contactsService.addNoteToContact(contact.getIdContact(), contact.getSocialModel().getMediumLink(), contact.getName());
                            ContactCacheService.updateContact(contact, mainView.getContext());
                            // EventBus.getDefault().post(new UpdateFile());
                            editFrame.setVisibility(View.GONE);
                        } else {
                            if (socialModel.getMediumLink() != null && !socialModel.getMediumLink().isEmpty()) {
                                contactsService.deleteNoteContact(contact.getIdContact(), socialModel.getMediumLink());
                            }
                            contact.getSocialModel().setMediumLink(null);
                            //     ((ImageView) socialPopup.findViewById(R.id.vk_icon)).setImageResource(R.drawable.icn_social_vk_gray);
                            //     ((ImageView) getActivity().findViewById(R.id.vk_icon)).setImageResource(R.drawable.icn_social_vk_gray);
                            Drawable color2 = new ColorDrawable(Color.parseColor("#e2e5e8"));
                            Drawable image2 = getResources().getDrawable(R.drawable.medium_white);
                            LayerDrawable ld2 = new LayerDrawable(new Drawable[]{color2, image2});
                            ((ImageView) socialPopup.findViewById(R.id.medium_icon)).setImageDrawable(ld2);
                         /*   ((ImageView) getActivity().findViewById(R.id.vk_icon)).setImageDrawable(ld2);

                            try{
                                ((ImageView) profilePopUp.findViewById(R.id.facebook_icon)).setImageDrawable(ld2);
                            }catch (Exception e){

                            }

                            try{
                                ((ImageView) companyProfilePopup.findViewById(R.id.facebook_icon)).setImageDrawable(ld2);
                            }catch (Exception e){

                            }*/

                            if (profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE) {
                                ((ImageView) profilePopUp.findViewById(R.id.medium_icon)).setImageDrawable(ld2);
                            } else if (companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE) {
                                ((ImageView) companyProfilePopup.findViewById(R.id.medium_icon)).setImageDrawable(ld2);
                            } else {
                                ((ImageView) getActivity().findViewById(R.id.medium_icon)).setImageDrawable(ld2);
                            }


                            ((TextView) socialPopup.findViewById(R.id.mediumNick)).setText("add username or link");
                            contact.hasMedium = false;
                            contact.setSocialModel(socialModel);
                            realm.commitTransaction();
                            ContactCacheService.updateContact(contact, mainView.getContext());
                            //  EventBus.getDefault().post(new UpdateFile());
                            editFrame.setVisibility(View.GONE);
                        }

                        //initIconColor(contact, mainView);

                        realm.close();

                        initIconColor(contact, mainView);


                        //EventBus.getDefault().post(new UpdateFile());

                    }
                });
                editFrame.findViewById(R.id.cancelEditField).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editFrame.setVisibility(View.GONE);
                    }
                });
            }
        });


        socialPopup.findViewById(R.id.twitter_link).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupProfileEditPreviewSocial != null && popupProfileEditPreviewSocial.getVisibility() == View.VISIBLE) {
                    popupProfileEditPreviewSocial.setVisibility(View.GONE);
                    return;
                }
                editFrame.setVisibility(View.GONE);

                ((EditText) editFrame.findViewById(R.id.dataToEdit)).setText("");
                if (contact.getSocialModel() != null)
                    if (contact.getSocialModel().getTwitterLink() != null)
                        ((EditText) editFrame.findViewById(R.id.dataToEdit)).setText(contact.getSocialModel().getTwitterLink());


                if (contact.getSocialModel() != null && contact.getSocialModel().getTwitterLink() != null)
                    showEditPopupPreviewSocial(contact, SocialEnums.TWITTER);
                else {
                    editFrame.setVisibility(View.VISIBLE);


                    //if(contact.listOfContacts == null || contact.listOfContacts.isEmpty()) {
                    editFrame.findViewById(R.id.ok_social).setVisibility(View.GONE);
                    editFrame.findViewById(R.id.get_last_clips).setVisibility(View.VISIBLE);
                    editFrame.findViewById(R.id.imageEditSocial).setVisibility(View.VISIBLE);
                    editFrame.findViewById(R.id.searchSocial).setVisibility(View.VISIBLE);
                    editFrame.findViewById(R.id.frameImageEditSocialInsta).setVisibility(View.GONE);

                    Drawable color = new ColorDrawable(Color.parseColor("#e2e5e8"));
                    Drawable image = getResources().getDrawable(R.drawable.ic_twitter_white);
                    LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
                    ((ImageView) editFrame.findViewById(R.id.imageEditSocial)).setImageDrawable(ld);
                    ((EditText) editFrame.findViewById(R.id.dataToEdit)).setHint("Add link, username or search");
                    ((EditText) editFrame.findViewById(R.id.dataToEdit)).setPadding(0, 0, 150, 0);
                    /*}else{
                        ((EditText)editFrame.findViewById(R.id.dataToEdit)).setPadding(0,0,0,0);
                        editFrame.findViewById(R.id.imageEditSocial).setVisibility(View.GONE);
                        editFrame.findViewById(R.id.searchSocial).setVisibility(View.GONE);
                        editFrame.findViewById(R.id.frameImageEditSocialInsta).setVisibility(View.GONE);
                        ((EditText) editFrame.findViewById(R.id.dataToEdit)).setHint("Add link or username");
                    }*/
                }

                editFrame.findViewById(R.id.get_last_clips).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String socialLinkClip = "";
                        for (Clipboard cl : FabNotificationService.clipboard) { //NullPointerException
                            if (cl.getListClipboards() != null && !cl.getListClipboards().isEmpty()) {
                                boolean checkFind = false;
                                for (Clipboard cl2 : cl.getListClipboards()) {
                                    if (cl2.getType().equals(ClipboardEnum.TWITTER)) {
                                        socialLinkClip = cl2.getValueCopy();
                                        checkFind = true;
                                        break;
                                    }
                                }
                                if (checkFind) break;
                            } else {
                                if (cl.getType().equals(ClipboardEnum.TWITTER)) {
                                    socialLinkClip = cl.getValueCopy();
                                    break;
                                }
                            }
                        }
                        ((EditText) editFrame.findViewById(R.id.dataToEdit)).setText(socialLinkClip);
                    }
                });


                editFrame.findViewById(R.id.imageEditSocial).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String name = contact.getName();
                        if (name == null || name.trim().isEmpty()) return;

                        String[] mach = name.split(" ");

                        Intent i = new Intent(Intent.ACTION_VIEW);
                        if (mach.length >= 2) {
                            String str = "";
                            for (int i2 = 0; i2 < mach.length; i2++) {
                                str += mach[i2];
                                if (i2 != mach.length - 1) str += "%20";
                            }
                            //     if (contact.listOfContacts == null || contact.listOfContacts.isEmpty())
                            i.setData(Uri.parse("https://twitter.com/search?q=" + str + "&src=typed_query&f=user"));
                            //     else
                            //         i.setData(Uri.parse("https://www.linkedin.com/search/results/companies/v2/?keywords=" + str));
                        } else {
                            //     if (contact.listOfContacts == null || contact.listOfContacts.isEmpty())
                            i.setData(Uri.parse("https://twitter.com/search?q=" + mach[0] + "&src=typed_query&f=user"));
                            //i.setData(Uri.parse("https://www.linkedin.com/mwlite/search/results/all/?keywords=" + mach[0]));
                            //     else
                            //         i.setData(Uri.parse("https://www.linkedin.com/search/results/companies/v2/?keywords=" + mach[0]));
                        }

                        startActivity(i);
                    }
                });

                editFrame.findViewById(R.id.searchSocial).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String name = contact.getName();
                        if (name == null || name.trim().isEmpty()) return;

                        String[] mach = name.split(" ");

                        Intent i = new Intent(Intent.ACTION_VIEW);
                        if (mach.length >= 2) {
                            String str = "";
                            for (int i2 = 0; i2 < mach.length; i2++) {
                                str += mach[i2];
                                if (i2 != mach.length - 1) str += "%20";
                            }
                            //     if (contact.listOfContacts == null || contact.listOfContacts.isEmpty())
                            i.setData(Uri.parse("https://twitter.com/search?q=" + str + "&src=typed_query&f=user"));
                            //     else
                            //         i.setData(Uri.parse("https://www.linkedin.com/search/results/companies/v2/?keywords=" + str));
                        } else {
                            //     if (contact.listOfContacts == null || contact.listOfContacts.isEmpty())
                            i.setData(Uri.parse("https://twitter.com/search?q=" + mach[0] + "&src=typed_query&f=user"));
                            //i.setData(Uri.parse("https://www.linkedin.com/mwlite/search/results/all/?keywords=" + mach[0]));
                            //     else
                            //         i.setData(Uri.parse("https://www.linkedin.com/search/results/companies/v2/?keywords=" + mach[0]));
                        }

                        startActivity(i);
                    }
                });


                editFrame.findViewById(R.id.imageEditSocial).setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {


                        ImageView cImg = ((ImageView) editFrame.findViewById(R.id.imageEditSocial));
                        //    ImageView imageView = ((ImageView) profilePopUp.findViewById(R.id.imagePreviewIcon));
                        //  FrameLayout frameLayout = ((FrameLayout) mainView.findViewById(R.id.company_profile_block));
                        switch (motionEvent.getAction()) {
                            case MotionEvent.ACTION_DOWN: {

                                if (checkClick_facebookEdit) {
                                    checkClick_facebookEdit = false;
                                    int colorFrom = Color.parseColor("#e2e5e8");
                                    int colorTo = Color.parseColor("#F9A825");
                                    Drawable image = getResources().getDrawable(R.drawable.ic_twitter_white);
                                    AnimColorMessenger.ActionDown_(colorFrom, colorTo, image, cImg);
                                }

                                //     OnTouchMethod(textView);
                                break;
                            }
                            case MotionEvent.ACTION_UP: {
                                if (!checkClick_facebookEdit) {
                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#e2e5e8");
                                    Drawable image = getResources().getDrawable(R.drawable.ic_twitter_white);
                                    AnimColorMessenger.ActionUp_(colorFrom, colorTo, image, cImg);
                                }

                                break;
                            }
                            case MotionEvent.ACTION_CANCEL: {

                                int colorFrom = Color.parseColor("#F9A825");
                                int colorTo = Color.parseColor("#e2e5e8");
                                Drawable image = getResources().getDrawable(R.drawable.ic_twitter_white);
                                AnimColorMessenger.ActionCancel_(colorFrom, colorTo, image, cImg);

                                break;
                            }
                            case MotionEvent.ACTION_MOVE: {
                                if (!checkClick_facebookEdit) {

                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#e2e5e8");
                                    Drawable image = getResources().getDrawable(R.drawable.ic_twitter_white);
                                    checkClick_facebookEdit = AnimColorMessenger.ActionMove_(colorFrom, colorTo, image, cImg, motionEvent, mainView);

                                }

                                break;
                            }
                        }

                        return false;
                    }
                });


                //editFrame.setVisibility(View.VISIBLE);
                openedViews.add(editFrame);
                ((TextView) editFrame.findViewById(R.id.typeField)).setText("twitter");


                editFrame.findViewById(R.id.updateContactField).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Realm realm = Realm.getDefaultInstance(); //-
                        realm.beginTransaction();
                        SocialModel socialModel;
                        if (contact.getSocialModel() == null) {
                            socialModel = realm.createObject(SocialModel.class);
                        } else
                            socialModel = contact.getSocialModel();

                        if (((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().length() > 0) {
                            //     if (contact.getSocialModel() != null)

                            if (socialModel.getTwitterLink() != null && !socialModel.getTwitterLink().isEmpty()) {
                                contactsService.deleteNoteContact(contact.getIdContact(), socialModel.getTwitterLink());
                            }

                            String f = ((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().toString();

                            if (!f.contains(".com/")) {
                                f = "https://twitter.com/" + f;
                            }

                            if (ClipboardType.isTwitter(f)) {
                                socialModel.setTwitterLink(f);
                            } else {
                                socialModel.setTwitterLink(((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().toString());
                            }

                            //socialModel.setTwitterLink(((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().toString());
                            //     ((ImageView) socialPopup.findViewById(R.id.vk_icon)).setImageResource(R.drawable.icn_social_vk);
                            //     ((ImageView) getActivity().findViewById(R.id.vk_icon)).setImageResource(R.drawable.icn_social_vk);
                            Drawable colorv = new ColorDrawable(Color.parseColor("#2ca7e0"));
                            Drawable imagev = getResources().getDrawable(R.drawable.ic_twitter_white);
                            LayerDrawable ldv = new LayerDrawable(new Drawable[]{colorv, imagev});
                            ((ImageView) socialPopup.findViewById(R.id.twitter_icon)).setImageDrawable(ldv);

                            ((ImageView) getActivity().findViewById(R.id.vk_icon)).setImageDrawable(ldv);




                            if (socialModel.getTwitterLink().contains(".com/")) {
                                ((TextView) socialPopup.findViewById(R.id.twitterNick)).setText(socialModel.getTwitterLink().substring(socialModel.getTwitterLink().indexOf(".com/") + 5));
                            } else
                                ((TextView) socialPopup.findViewById(R.id.twitterNick)).setText(socialModel.getTwitterLink());
                            //}

                            //        if (contact.getSocialModel() != null)
                            //    ((TextView) socialPopup.findViewById(R.id.vkNick)).setText(socialModel.getVkLink());
                            contact.hasTwitter = true;
                            contact.setSocialModel(socialModel);
                            realm.commitTransaction();
                            contactsService.addNoteToContact(contact.getIdContact(), contact.getSocialModel().getTwitterLink(), contact.getName());
                            ContactCacheService.updateContact(contact, mainView.getContext());
                            // EventBus.getDefault().post(new UpdateFile());
                            editFrame.setVisibility(View.GONE);
                        } else {
                            if (socialModel.getTwitterLink() != null && !socialModel.getTwitterLink().isEmpty()) {
                                contactsService.deleteNoteContact(contact.getIdContact(), socialModel.getTwitterLink());
                            }
                            contact.getSocialModel().setTwitterLink(null);
                            //     ((ImageView) socialPopup.findViewById(R.id.vk_icon)).setImageResource(R.drawable.icn_social_vk_gray);
                            //     ((ImageView) getActivity().findViewById(R.id.vk_icon)).setImageResource(R.drawable.icn_social_vk_gray);
                            Drawable color2 = new ColorDrawable(Color.parseColor("#e2e5e8"));
                            Drawable image2 = getResources().getDrawable(R.drawable.ic_twitter_white);
                            LayerDrawable ld2 = new LayerDrawable(new Drawable[]{color2, image2});
                            ((ImageView) socialPopup.findViewById(R.id.twitter_icon)).setImageDrawable(ld2);



                            ((TextView) socialPopup.findViewById(R.id.twitterNick)).setText("add username or");
                            contact.hasTwitter = false;
                            contact.setSocialModel(socialModel);
                            realm.commitTransaction();
                            ContactCacheService.updateContact(contact, mainView.getContext());
                            //  EventBus.getDefault().post(new UpdateFile());
                            editFrame.setVisibility(View.GONE);
                        }

                        //initIconColor(contact, mainView);

                        realm.close();

                        initIconColor(contact, mainView);



                        //EventBus.getDefault().post(new UpdateFile());

                    }
                });
                editFrame.findViewById(R.id.cancelEditField).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editFrame.setVisibility(View.GONE);
                    }
                });
            }
        });

        socialPopup.findViewById(R.id.youtube_link).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupProfileEditPreviewSocial != null && popupProfileEditPreviewSocial.getVisibility() == View.VISIBLE) {
                    popupProfileEditPreviewSocial.setVisibility(View.GONE);
                    return;
                }
                editFrame.setVisibility(View.GONE);

                ((EditText) editFrame.findViewById(R.id.dataToEdit)).setText("");
                if (contact.getSocialModel() != null)
                    if (contact.getSocialModel().getYoutubeLink() != null)
                        ((EditText) editFrame.findViewById(R.id.dataToEdit)).setText(contact.getSocialModel().getYoutubeLink());


                if (contact.getSocialModel() != null && contact.getSocialModel().getYoutubeLink() != null)
                    showEditPopupPreviewSocial(contact, SocialEnums.YOUTUBE);
                else {
                    editFrame.setVisibility(View.VISIBLE);


                    //if(contact.listOfContacts == null || contact.listOfContacts.isEmpty()) {
                    editFrame.findViewById(R.id.ok_social).setVisibility(View.VISIBLE);
                    editFrame.findViewById(R.id.get_last_clips).setVisibility(View.VISIBLE);
                    editFrame.findViewById(R.id.imageEditSocial).setVisibility(View.VISIBLE);
                    editFrame.findViewById(R.id.searchSocial).setVisibility(View.VISIBLE);
                    editFrame.findViewById(R.id.frameImageEditSocialInsta).setVisibility(View.GONE);

                    Drawable color = new ColorDrawable(Color.parseColor("#e2e5e8"));
                    Drawable image = getResources().getDrawable(R.drawable.ic_youtube_white);
                    LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
                    ((ImageView) editFrame.findViewById(R.id.imageEditSocial)).setImageDrawable(ld);
                    ((EditText) editFrame.findViewById(R.id.dataToEdit)).setHint("Add link, username or search");
                    ((EditText) editFrame.findViewById(R.id.dataToEdit)).setPadding(0, 0, 150, 0);

                }

                editFrame.findViewById(R.id.get_last_clips).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String socialLinkClip = "";
                        for (Clipboard cl : FabNotificationService.clipboard) { //NullPointerException
                            if (cl.getListClipboards() != null && !cl.getListClipboards().isEmpty()) {
                                boolean checkFind = false;
                                for (Clipboard cl2 : cl.getListClipboards()) {
                                    if (cl2.getType().equals(ClipboardEnum.YOUTUBE)) {
                                        socialLinkClip = cl2.getValueCopy();
                                        checkFind = true;
                                        break;
                                    }
                                }
                                if (checkFind) break;
                            } else {
                                if (cl.getType().equals(ClipboardEnum.YOUTUBE)) {
                                    socialLinkClip = cl.getValueCopy();
                                    break;
                                }
                            }
                        }
                        ((EditText) editFrame.findViewById(R.id.dataToEdit)).setText(socialLinkClip);
                    }
                });


                editFrame.findViewById(R.id.imageEditSocial).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                editFrame.findViewById(R.id.searchSocial).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });


                editFrame.findViewById(R.id.imageEditSocial).setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {


                        ImageView cImg = ((ImageView) editFrame.findViewById(R.id.imageEditSocial));
                        //    ImageView imageView = ((ImageView) profilePopUp.findViewById(R.id.imagePreviewIcon));
                        //  FrameLayout frameLayout = ((FrameLayout) mainView.findViewById(R.id.company_profile_block));
                        switch (motionEvent.getAction()) {
                            case MotionEvent.ACTION_DOWN: {

                                if (checkClick_facebookEdit) {
                                    checkClick_facebookEdit = false;
                                    int colorFrom = Color.parseColor("#e2e5e8");
                                    int colorTo = Color.parseColor("#F9A825");
                                    Drawable image = getResources().getDrawable(R.drawable.ic_youtube_white);
                                    AnimColorMessenger.ActionDown_(colorFrom, colorTo, image, cImg);
                                }

                                //     OnTouchMethod(textView);
                                break;
                            }
                            case MotionEvent.ACTION_UP: {
                                if (!checkClick_facebookEdit) {
                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#e2e5e8");
                                    Drawable image = getResources().getDrawable(R.drawable.ic_youtube_white);
                                    AnimColorMessenger.ActionUp_(colorFrom, colorTo, image, cImg);
                                }

                                break;
                            }
                            case MotionEvent.ACTION_CANCEL: {

                                int colorFrom = Color.parseColor("#F9A825");
                                int colorTo = Color.parseColor("#e2e5e8");
                                Drawable image = getResources().getDrawable(R.drawable.ic_youtube_white);
                                AnimColorMessenger.ActionCancel_(colorFrom, colorTo, image, cImg);

                                break;
                            }
                            case MotionEvent.ACTION_MOVE: {
                                if (!checkClick_facebookEdit) {

                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#e2e5e8");
                                    Drawable image = getResources().getDrawable(R.drawable.ic_youtube_white);
                                    checkClick_facebookEdit = AnimColorMessenger.ActionMove_(colorFrom, colorTo, image, cImg, motionEvent, mainView);

                                }

                                break;
                            }
                        }

                        return false;
                    }
                });


                //editFrame.setVisibility(View.VISIBLE);
                openedViews.add(editFrame);
                ((TextView) editFrame.findViewById(R.id.typeField)).setText("Youtube");


                editFrame.findViewById(R.id.updateContactField).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Realm realm = Realm.getDefaultInstance(); //-
                        realm.beginTransaction();
                        SocialModel socialModel;
                        if (contact.getSocialModel() == null) {
                            socialModel = realm.createObject(SocialModel.class);
                        } else
                            socialModel = contact.getSocialModel();

                        if (((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().length() > 0) {
                            //     if (contact.getSocialModel() != null)

                            if (socialModel.getYoutubeLink() != null && !socialModel.getYoutubeLink().isEmpty()) {
                                contactsService.deleteNoteContact(contact.getIdContact(), socialModel.getYoutubeLink());
                            }

                            String f = ((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().toString();

                            if (!f.contains(".com/")) {
                                f = "https://www.youtube.com/channel/" + f;
                            }

                            if (ClipboardType.isYoutube(f)) {
                                socialModel.setYoutubeLink(f);
                            } else {
                                socialModel.setYoutubeLink(((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().toString());
                            }

                            //socialModel.setYoutubeLink(((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().toString());
                            //     ((ImageView) socialPopup.findViewById(R.id.vk_icon)).setImageResource(R.drawable.icn_social_vk);
                            //     ((ImageView) getActivity().findViewById(R.id.vk_icon)).setImageResource(R.drawable.icn_social_vk);
                            Drawable colorv = new ColorDrawable(Color.parseColor("#ed2524"));
                            Drawable imagev = getResources().getDrawable(R.drawable.ic_youtube_white);
                            LayerDrawable ldv = new LayerDrawable(new Drawable[]{colorv, imagev});
                            ((ImageView) socialPopup.findViewById(R.id.youtube_icon)).setImageDrawable(ldv);


                            if (socialModel.getYoutubeLink().contains("user/") || socialModel.getYoutubeLink().contains("channel/")) {
                                if (socialModel.getYoutubeLink().contains("user/")) {
                                    String text = socialModel.getYoutubeLink().substring(socialModel.getYoutubeLink().indexOf("user/") + 5);
                                    ((TextView) socialPopup.findViewById(R.id.youtubeNick)).setText(text);
                                } else if (socialModel.getYoutubeLink().contains("channel/")) {
                                    String text = socialModel.getYoutubeLink().substring(socialModel.getYoutubeLink().indexOf("channel/") + 8);
                                    ((TextView) socialPopup.findViewById(R.id.youtubeNick)).setText(text);
                                }
                            } else
                                ((TextView) socialPopup.findViewById(R.id.youtubeNick)).setText(socialModel.getYoutubeLink());
                            //}

                            //        if (contact.getSocialModel() != null)
                            //    ((TextView) socialPopup.findViewById(R.id.vkNick)).setText(socialModel.getVkLink());
                            contact.hasYoutube = true;
                            contact.setSocialModel(socialModel);
                            realm.commitTransaction();
                            contactsService.addNoteToContact(contact.getIdContact(), contact.getSocialModel().getYoutubeLink(), contact.getName());
                            ContactCacheService.updateContact(contact, mainView.getContext());
                            // EventBus.getDefault().post(new UpdateFile());
                            editFrame.setVisibility(View.GONE);
                        } else {
                            if (socialModel.getYoutubeLink() != null && !socialModel.getYoutubeLink().isEmpty()) {
                                contactsService.deleteNoteContact(contact.getIdContact(), socialModel.getYoutubeLink());
                            }
                            contact.getSocialModel().setYoutubeLink(null);
                            //     ((ImageView) socialPopup.findViewById(R.id.vk_icon)).setImageResource(R.drawable.icn_social_vk_gray);
                            //     ((ImageView) getActivity().findViewById(R.id.vk_icon)).setImageResource(R.drawable.icn_social_vk_gray);
                            Drawable color2 = new ColorDrawable(Color.parseColor("#e2e5e8"));
                            Drawable image2 = getResources().getDrawable(R.drawable.ic_youtube_white);
                            LayerDrawable ld2 = new LayerDrawable(new Drawable[]{color2, image2});
                            ((ImageView) socialPopup.findViewById(R.id.vk_icon)).setImageDrawable(ld2);



                            ((TextView) socialPopup.findViewById(R.id.youtubeNick)).setText("add username or");
                            contact.hasYoutube = false;
                            contact.setSocialModel(socialModel);
                            realm.commitTransaction();
                            ContactCacheService.updateContact(contact, mainView.getContext());
                            //  EventBus.getDefault().post(new UpdateFile());
                            editFrame.setVisibility(View.GONE);
                        }

                        //initIconColor(contact, mainView);

                        realm.close();

                        initIconColor(contact, mainView);




                        //EventBus.getDefault().post(new UpdateFile());

                    }
                });
                editFrame.findViewById(R.id.cancelEditField).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editFrame.setVisibility(View.GONE);
                    }
                });
            }
        });

        //=====================================================================================================================================================end Add New social


        socialPopup.findViewById(R.id.linkedLink).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupProfileEditPreviewSocial != null && popupProfileEditPreviewSocial.getVisibility() == View.VISIBLE) {
                    popupProfileEditPreviewSocial.setVisibility(View.GONE);
                    return;
                }
                editFrame.setVisibility(View.GONE);
                ((EditText) editFrame.findViewById(R.id.dataToEdit)).setText("");
                if (contact.getSocialModel() != null)
                    if (contact.getSocialModel().getLinkedInLink() != null)
                        ((EditText) editFrame.findViewById(R.id.dataToEdit)).setText(contact.getSocialModel().getLinkedInLink());


                if (contact.getSocialModel() != null && contact.getSocialModel().getLinkedInLink() != null)
                    showEditPopupPreviewSocial(contact, SocialEnums.LINKEDIN);
                else {
                    editFrame.setVisibility(View.VISIBLE);


                    //if(contact.listOfContacts == null || contact.listOfContacts.isEmpty()) {
                    ((EditText) editFrame.findViewById(R.id.dataToEdit)).setPadding(0, 0, 150, 0);
                    editFrame.findViewById(R.id.ok_social).setVisibility(View.GONE);
                    editFrame.findViewById(R.id.get_last_clips).setVisibility(View.VISIBLE);
                    editFrame.findViewById(R.id.imageEditSocial).setVisibility(View.VISIBLE);
                    editFrame.findViewById(R.id.searchSocial).setVisibility(View.VISIBLE);
                    editFrame.findViewById(R.id.frameImageEditSocialInsta).setVisibility(View.GONE);

                    Drawable color = new ColorDrawable(Color.parseColor("#e2e5e8"));
                    Drawable image = getResources().getDrawable(R.drawable.icn_social_linked2);
                    LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
                    ((ImageView) editFrame.findViewById(R.id.imageEditSocial)).setImageDrawable(ld);
                    ((EditText) editFrame.findViewById(R.id.dataToEdit)).setHint("Add link, username or search");
                    /*}else{
                        ((EditText)editFrame.findViewById(R.id.dataToEdit)).setPadding(0,0,0,0);
                        editFrame.findViewById(R.id.imageEditSocial).setVisibility(View.GONE);
                        editFrame.findViewById(R.id.searchSocial).setVisibility(View.GONE);
                        editFrame.findViewById(R.id.frameImageEditSocialInsta).setVisibility(View.GONE);
                        ((EditText) editFrame.findViewById(R.id.dataToEdit)).setHint("Add link or username");
                    }*/
                }

                editFrame.findViewById(R.id.get_last_clips).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String socialLinkClip = "";
                        for (Clipboard cl : FabNotificationService.clipboard) { //NullPointerException
                            if (cl.getListClipboards() != null && !cl.getListClipboards().isEmpty()) {
                                boolean checkFind = false;
                                for (Clipboard cl2 : cl.getListClipboards()) {
                                    if (cl2.getType().equals(ClipboardEnum.LINKEDIN)) {
                                        socialLinkClip = cl2.getValueCopy();
                                        checkFind = true;
                                        break;
                                    }
                                }
                                if (checkFind) break;
                            } else {
                                if (cl.getType().equals(ClipboardEnum.LINKEDIN)) {
                                    socialLinkClip = cl.getValueCopy();
                                    break;
                                }
                            }
                        }
                        ((EditText) editFrame.findViewById(R.id.dataToEdit)).setText(socialLinkClip);
                    }
                });


                editFrame.findViewById(R.id.imageEditSocial).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String name = contact.getName();
                        String[] mach = name.split(" ");

                        Intent i = new Intent(Intent.ACTION_VIEW);
                        if (mach.length >= 2) {
                            String str = "";
                            for (int i2 = 0; i2 < mach.length; i2++) {
                                str += mach[i2];
                                if (i2 != mach.length - 1) str += "%20";
                            }
                            //    if (selectedContact.listOfContacts == null || selectedContact.listOfContacts.isEmpty())
                            i.setData(Uri.parse("https://www.linkedin.com/mwlite/search/results/all/?keywords=" + str));
                            //    else
                            //        i.setData(Uri.parse("https://www.linkedin.com/search/results/companies/v2/?keywords=" + str));

                        } else {
                            //    if (selectedContact.listOfContacts == null || selectedContact.listOfContacts.isEmpty())
                            i.setData(Uri.parse("https://www.linkedin.com/mwlite/search/results/all/?keywords=" + mach[0]));
                            //    else
                            //        i.setData(Uri.parse("https://www.linkedin.com/search/results/companies/v2/?keywords=" + mach[0]));

                        }

                        startActivity(i);
                    }
                });

                editFrame.findViewById(R.id.searchSocial).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String name = contact.getName();
                        String[] mach = name.split(" ");

                        Intent i = new Intent(Intent.ACTION_VIEW);
                        if (mach.length >= 2) {
                            String str = "";
                            for (int i2 = 0; i2 < mach.length; i2++) {
                                str += mach[i2];
                                if (i2 != mach.length - 1) str += "%20";
                            }
                            //  if (selectedContact.listOfContacts == null || selectedContact.listOfContacts.isEmpty())
                            i.setData(Uri.parse("https://www.linkedin.com/mwlite/search/results/all/?keywords=" + str));
                            //  else
                            //      i.setData(Uri.parse("https://www.linkedin.com/search/results/companies/v2/?keywords=" + str));

                        } else {
                            //  if (selectedContact.listOfContacts == null || selectedContact.listOfContacts.isEmpty())
                            i.setData(Uri.parse("https://www.linkedin.com/mwlite/search/results/all/?keywords=" + mach[0]));
                            //  else
                            //      i.setData(Uri.parse("https://www.linkedin.com/search/results/companies/v2/?keywords=" + mach[0]));

                        }

                        startActivity(i);
                    }
                });


                editFrame.findViewById(R.id.imageEditSocial).setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {


                        ImageView cImg = ((ImageView) editFrame.findViewById(R.id.imageEditSocial));
                        //    ImageView imageView = ((ImageView) profilePopUp.findViewById(R.id.imagePreviewIcon));
                        //  FrameLayout frameLayout = ((FrameLayout) mainView.findViewById(R.id.company_profile_block));
                        switch (motionEvent.getAction()) {
                            case MotionEvent.ACTION_DOWN: {

                                if (checkClick_facebookEdit) {
                                    checkClick_facebookEdit = false;
                                    int colorFrom = Color.parseColor("#e2e5e8");
                                    int colorTo = Color.parseColor("#F9A825");
                                    Drawable image = getResources().getDrawable(R.drawable.icn_social_linked2);
                                    AnimColorMessenger.ActionDown_(colorFrom, colorTo, image, cImg);
                                }

                                //     OnTouchMethod(textView);
                                break;
                            }
                            case MotionEvent.ACTION_UP: {
                                if (!checkClick_facebookEdit) {
                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#e2e5e8");
                                    Drawable image = getResources().getDrawable(R.drawable.icn_social_linked2);
                                    AnimColorMessenger.ActionUp_(colorFrom, colorTo, image, cImg);
                                }

                                break;
                            }
                            case MotionEvent.ACTION_CANCEL: {

                                int colorFrom = Color.parseColor("#F9A825");
                                int colorTo = Color.parseColor("#e2e5e8");
                                Drawable image = getResources().getDrawable(R.drawable.icn_social_linked2);
                                AnimColorMessenger.ActionCancel_(colorFrom, colorTo, image, cImg);

                                break;
                            }
                            case MotionEvent.ACTION_MOVE: {
                                if (!checkClick_facebookEdit) {

                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#e2e5e8");
                                    Drawable image = getResources().getDrawable(R.drawable.icn_social_linked2);
                                    checkClick_facebookEdit = AnimColorMessenger.ActionMove_(colorFrom, colorTo, image, cImg, motionEvent, mainView);

                                }

                                break;
                            }
                        }

                        return false;
                    }
                });


                //editFrame.setVisibility(View.VISIBLE);
                openedViews.add(editFrame);
                ((TextView) editFrame.findViewById(R.id.typeField)).setText("LinkedIn");


                editFrame.findViewById(R.id.updateContactField).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        Realm realm = Realm.getDefaultInstance(); //-
                        realm.beginTransaction();
                        SocialModel socialModel;
                        if (contact.getSocialModel() == null) {
                            socialModel = realm.createObject(SocialModel.class);
                        } else
                            socialModel = contact.getSocialModel();

                        if (((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().length() > 0) {
                            //    if (contact.getSocialModel() != null)

                            if (socialModel.getLinkedInLink() != null && !socialModel.getLinkedInLink().isEmpty()) {
                                contactsService.deleteNoteContact(contact.getIdContact(), socialModel.getLinkedInLink());
                            }

                            String f = ((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().toString();

                            if (!f.contains(".com/")) {
                                f = "https://www.linkedin.com/in/" + f;
                            }

                            if (ClipboardType.isLinkedIn(f)) {
                                socialModel.setLinkedInLink(f);
                            } else {
                                socialModel.setLinkedInLink(((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().toString());
                            }

                            //socialModel.setLinkedInLink(((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().toString());
                            //      ((ImageView) socialPopup.findViewById(R.id.link_icon)).setImageResource(R.drawable.icn_social_linkedin);
                            //      ((ImageView) getActivity().findViewById(R.id.link_icon)).setImageResource(R.drawable.icn_social_linkedin);
                            Drawable colorl = new ColorDrawable(Color.parseColor("#0077B7"));
                            Drawable imagel = getResources().getDrawable(R.drawable.icn_social_linked2);
                            LayerDrawable ldl = new LayerDrawable(new Drawable[]{colorl, imagel});

                        /*    ((ImageView) socialPopup.findViewById(R.id.link_icon)).setImageDrawable(ldl);

                            ((ImageView) getActivity().findViewById(R.id.link_icon)).setImageDrawable(ldl);

                            try{
                                ((ImageView) profilePopUp.findViewById(R.id.link_icon)).setImageDrawable(ldl);
                            }catch (Exception e){

                            }

                            try{
                                ((ImageView) companyProfilePopup.findViewById(R.id.link_icon)).setImageDrawable(ldl);
                            }catch (Exception e){

                            }*/

                            if (profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE) {
                                ((ImageView) profilePopUp.findViewById(R.id.link_icon)).setImageDrawable(ldl);
                            } else if (companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE) {
                                ((ImageView) companyProfilePopup.findViewById(R.id.link_icon)).setImageDrawable(ldl);
                            } else {
                                ((ImageView) getActivity().findViewById(R.id.link_icon)).setImageDrawable(ldl);
                            }


                            try {
                                String link = socialModel.getLinkedInLink();
                                String sub = link.substring(link.length() - 20, link.length());
                                ((TextView) socialPopup.findViewById(R.id.link_text)).setText(sub);
                            } catch (Exception e) {
                                e.printStackTrace();
                                ((TextView) socialPopup.findViewById(R.id.link_text)).setText(socialModel.getLinkedInLink());
                            }
                            //  ((TextView) socialPopup.findViewById(R.id.link_text)).setText(socialModel.getLinkedInLink());
                            contact.hasLinked = true;
                            contact.setSocialModel(socialModel);
                            realm.commitTransaction();
                            contactsService.addNoteToContact(contact.getIdContact(), contact.getSocialModel().getLinkedInLink(), contact.getName());
                            ContactCacheService.updateContact(contact, mainView.getContext());
                            //  EventBus.getDefault().post(new UpdateFile());
                            editFrame.setVisibility(View.GONE);
                        } else {
                            if (socialModel.getLinkedInLink() != null && !socialModel.getLinkedInLink().isEmpty()) {
                                contactsService.deleteNoteContact(contact.getIdContact(), socialModel.getLinkedInLink());
                            }
                            contact.getSocialModel().setLinkedInLink(null);
                            //     ((ImageView) socialPopup.findViewById(R.id.link_icon)).setImageResource(R.drawable.icn_social_linkedin_gray);
                            //     ((ImageView) getActivity().findViewById(R.id.link_icon)).setImageResource(R.drawable.icn_social_linkedin_gray);
                            Drawable color3 = new ColorDrawable(Color.parseColor("#e2e5e8"));
                            Drawable image3 = getResources().getDrawable(R.drawable.icn_social_linked2);
                            LayerDrawable ld3 = new LayerDrawable(new Drawable[]{color3, image3});
                            ((ImageView) socialPopup.findViewById(R.id.link_icon)).setImageDrawable(ld3);
                           /* ((ImageView) getActivity().findViewById(R.id.link_icon)).setImageDrawable(ld3);

                            try{
                                ((ImageView) profilePopUp.findViewById(R.id.link_icon)).setImageDrawable(ld3);
                            }catch (Exception e){

                            }

                            try{
                                ((ImageView) companyProfilePopup.findViewById(R.id.link_icon)).setImageDrawable(ld3);
                            }catch (Exception e){

                            }*/

                            if (profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE) {
                                ((ImageView) profilePopUp.findViewById(R.id.link_icon)).setImageDrawable(ld3);
                            } else if (companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE) {
                                ((ImageView) companyProfilePopup.findViewById(R.id.link_icon)).setImageDrawable(ld3);
                            } else {
                                ((ImageView) getActivity().findViewById(R.id.link_icon)).setImageDrawable(ld3);
                            }

                            ((TextView) socialPopup.findViewById(R.id.link_text)).setText("add new link");
                            contact.hasLinked = false;
                            contact.setSocialModel(socialModel);
                            realm.commitTransaction();
                            ContactCacheService.updateContact(contact, mainView.getContext());
                            //  EventBus.getDefault().post(new UpdateFile());
                            editFrame.setVisibility(View.GONE);
                        }

                        //initIconColor(contact, mainView);

                        realm.close();

                        initIconColor(contact, mainView);


                        //EventBus.getDefault().post(new UpdateFile());

                    }
                });
                editFrame.findViewById(R.id.cancelEditField).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editFrame.setVisibility(View.GONE);
                    }
                });
            }
        });

        socialPopup.findViewById(R.id.instagramLink).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupProfileEditPreviewSocial != null && popupProfileEditPreviewSocial.getVisibility() == View.VISIBLE) {
                    popupProfileEditPreviewSocial.setVisibility(View.GONE);
                    return;
                }
                editFrame.setVisibility(View.GONE);
                ((EditText) editFrame.findViewById(R.id.dataToEdit)).setText("");
                if (contact.getSocialModel() != null)
                    if (contact.getSocialModel().getInstagramLink() != null)
                        ((EditText) editFrame.findViewById(R.id.dataToEdit)).setText(contact.getSocialModel().getInstagramLink());


                if (contact.getSocialModel() != null && contact.getSocialModel().getInstagramLink() != null)
                    showEditPopupPreviewSocial(contact, SocialEnums.INSTAGRAM);
                else {
                    editFrame.setVisibility(View.VISIBLE);


                    //if(contact.listOfContacts == null || contact.listOfContacts.isEmpty()) {
                    ((EditText) editFrame.findViewById(R.id.dataToEdit)).setPadding(0, 0, 150, 0);
                    editFrame.findViewById(R.id.ok_social).setVisibility(View.GONE);
                    editFrame.findViewById(R.id.get_last_clips).setVisibility(View.VISIBLE);
                    editFrame.findViewById(R.id.imageEditSocial).setVisibility(View.GONE);
                    editFrame.findViewById(R.id.searchSocial).setVisibility(View.VISIBLE);
                    editFrame.findViewById(R.id.frameImageEditSocialInsta).setVisibility(View.VISIBLE);

                    Drawable color = new ColorDrawable(Color.parseColor("#e2e5e8"));
                    Drawable image = getResources().getDrawable(R.drawable.icn_social_ints2);
                    LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
                    ((ImageView) editFrame.findViewById(R.id.imageEditSocialInsta)).setImageDrawable(ld);
                    ((EditText) editFrame.findViewById(R.id.dataToEdit)).setHint("Add link, username or search");

                }

                editFrame.findViewById(R.id.get_last_clips).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String socialLinkClip = "";
                        for (Clipboard cl : FabNotificationService.clipboard) { //NullPointerException
                            if (cl.getListClipboards() != null && !cl.getListClipboards().isEmpty()) {
                                boolean checkFind = false;
                                for (Clipboard cl2 : cl.getListClipboards()) {
                                    if (cl2.getType().equals(ClipboardEnum.INSTAGRAM)) {
                                        socialLinkClip = cl2.getValueCopy();
                                        checkFind = true;
                                        break;
                                    }
                                }
                                if (checkFind) break;
                            } else {
                                if (cl.getType().equals(ClipboardEnum.INSTAGRAM)) {
                                    socialLinkClip = cl.getValueCopy();
                                    break;
                                }
                            }
                        }
                        ((EditText) editFrame.findViewById(R.id.dataToEdit)).setText(socialLinkClip);
                    }
                });


                editFrame.findViewById(R.id.imageEditSocialInsta).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                editFrame.findViewById(R.id.searchSocial).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });


                editFrame.findViewById(R.id.imageEditSocialInsta).setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {


                        ImageView cImg = ((ImageView) editFrame.findViewById(R.id.imageEditSocialInsta));
                        //    ImageView imageView = ((ImageView) profilePopUp.findViewById(R.id.imagePreviewIcon));
                        //  FrameLayout frameLayout = ((FrameLayout) mainView.findViewById(R.id.company_profile_block));
                        switch (motionEvent.getAction()) {
                            case MotionEvent.ACTION_DOWN: {

                                if (checkClick_facebookEdit) {
                                    checkClick_facebookEdit = false;
                                    int colorFrom = Color.parseColor("#e2e5e8");
                                    int colorTo = Color.parseColor("#F9A825");
                                    Drawable image = getResources().getDrawable(R.drawable.icn_social_ints2);
                                    AnimColorMessenger.ActionDown_(colorFrom, colorTo, image, cImg);
                                }

                                //     OnTouchMethod(textView);
                                break;
                            }
                            case MotionEvent.ACTION_UP: {
                                if (!checkClick_facebookEdit) {
                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#e2e5e8");
                                    Drawable image = getResources().getDrawable(R.drawable.icn_social_ints2);
                                    AnimColorMessenger.ActionUp_(colorFrom, colorTo, image, cImg);
                                }

                                break;
                            }
                            case MotionEvent.ACTION_CANCEL: {

                                int colorFrom = Color.parseColor("#F9A825");
                                int colorTo = Color.parseColor("#e2e5e8");
                                Drawable image = getResources().getDrawable(R.drawable.icn_social_ints2);
                                AnimColorMessenger.ActionCancel_(colorFrom, colorTo, image, cImg);

                                break;
                            }
                            case MotionEvent.ACTION_MOVE: {
                                if (!checkClick_facebookEdit) {

                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#e2e5e8");
                                    Drawable image = getResources().getDrawable(R.drawable.icn_social_ints2);
                                    checkClick_facebookEdit = AnimColorMessenger.ActionMove_(colorFrom, colorTo, image, cImg, motionEvent, mainView);

                                }

                                break;
                            }
                        }

                        return false;
                    }
                });


                //editFrame.setVisibility(View.VISIBLE);
                openedViews.add(editFrame);
                ((TextView) editFrame.findViewById(R.id.typeField)).setText("Instagram");
                //((EditText) editFrame.findViewById(R.id.dataToEdit)).setHint("Add link, username or search");

                editFrame.findViewById(R.id.updateContactField).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Realm realm = Realm.getDefaultInstance(); //-
                        realm.beginTransaction();
                        SocialModel socialModel;
                        if (contact.getSocialModel() == null) {
                            socialModel = realm.createObject(SocialModel.class);
                        } else
                            socialModel = contact.getSocialModel();

                        if (((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().length() > 0) {
                            //  if (contact.getSocialModel() != null)

                            if (socialModel.getInstagramLink() != null && !socialModel.getInstagramLink().isEmpty()) {
                                contactsService.deleteNoteContact(contact.getIdContact(), socialModel.getInstagramLink());
                            }



                            String username = ((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().toString();

                            if (username.contains("?utm")) {
                                int ind = username.indexOf('?');
                                if (ind != -1)
                                    username = username.substring(0, ind);

                            }

                            if (username.charAt(username.length() - 1) == '/') {
                                username = username.substring(0, username.length() - 1);
                            }

                            if (!username.toLowerCase().contains("instagram.com")) {
                                username = "https://instagram.com/" + username;
                            }

                            socialModel.setInstagramLink(username);
                            //    ((ImageView) socialPopup.findViewById(R.id.instagram_icon)).setImageResource(R.drawable.icn_social_instagram);
                            //    ((ImageView) getActivity().findViewById(R.id.inst_icon)).setImageResource(R.drawable.icn_social_instagram);
                            Drawable colori = new ColorDrawable(Color.parseColor("#8a3ab9"));
                            Drawable imagei = getResources().getDrawable(R.drawable.icn_social_ints2);
                            LayerDrawable ldi = new LayerDrawable(new Drawable[]{colori, imagei});
                            ((ImageView) socialPopup.findViewById(R.id.instagram_icon)).setImageDrawable(ldi);


                            if (profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE) {
                                ((ImageView) profilePopUp.findViewById(R.id.inst_icon)).setImageDrawable(ldi);
                            } else if (companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE) {
                                ((ImageView) companyProfilePopup.findViewById(R.id.inst_icon)).setImageDrawable(ldi);
                            } else {
                                ((ImageView) getActivity().findViewById(R.id.inst_icon)).setImageDrawable(ldi);
                            }

                            //   if (contact.getSocialModel() != null)


                            contact.hasInst = true;
                            contact.setSocialModel(socialModel);
                            realm.commitTransaction();

                            String inst = contact.getSocialModel().getInstagramLink();

                            if (inst.contains(".com/")) {
                                int ind = inst.indexOf(".com/");
                                String outLink = inst.substring(ind + 5, inst.length());


                                if (outLink.contains("?")) {
                                    int in = outLink.indexOf("?");
                                    outLink = outLink.substring(0, in);
                                }

                                ((TextView) socialPopup.findViewById(R.id.instagramNick)).setText(outLink);
                            } else
                                ((TextView) socialPopup.findViewById(R.id.instagramNick)).setText(contact.getSocialModel().getInstagramLink());

                            contactsService.addNoteToContact(contact.getIdContact(), contact.getSocialModel().getInstagramLink(), contact.getName());
                            ContactCacheService.updateContact(contact, mainView.getContext());
                            //  EventBus.getDefault().post(new UpdateFile());
                            editFrame.setVisibility(View.GONE);
                        } else {
                            if (socialModel.getInstagramLink() != null && !socialModel.getInstagramLink().isEmpty()) {
                                contactsService.deleteNoteContact(contact.getIdContact(), socialModel.getInstagramLink());
                            }
                            socialModel.setInstagramLink(null);
                            //         ((ImageView) socialPopup.findViewById(R.id.instagram_icon)).setImageResource(R.drawable.icn_social_instagram_gray);
                            //         ((ImageView) getActivity().findViewById(R.id.inst_icon)).setImageResource(R.drawable.icn_social_instagram_gray);
                            Drawable color4 = new ColorDrawable(Color.parseColor("#e2e5e8"));
                            Drawable image4 = getResources().getDrawable(R.drawable.icn_social_ints2);
                            LayerDrawable ld4 = new LayerDrawable(new Drawable[]{color4, image4});
                            ((ImageView) socialPopup.findViewById(R.id.instagram_icon)).setImageDrawable(ld4);


                            if (profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE) {
                                ((ImageView) profilePopUp.findViewById(R.id.inst_icon)).setImageDrawable(ld4);
                            } else if (companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE) {
                                ((ImageView) companyProfilePopup.findViewById(R.id.inst_icon)).setImageDrawable(ld4);
                            } else {
                                ((ImageView) getActivity().findViewById(R.id.inst_icon)).setImageDrawable(ld4);
                            }


                            ((TextView) socialPopup.findViewById(R.id.instagramNick)).setText("add new link");
                            contact.hasInst = false;
                            contact.setSocialModel(socialModel);
                            realm.commitTransaction();

                            ContactCacheService.updateContact(contact, mainView.getContext());
                            //    EventBus.getDefault().post(new UpdateFile());
                            editFrame.setVisibility(View.GONE);
                        }

                        //initIconColor(contact, mainView);

                        realm.close();

                        initIconColor(contact, mainView);


                        //EventBus.getDefault().post(new UpdateFile());

                    }
                });
            }
        });

        socialPopup.findViewById(R.id.viberLink).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (popupProfileEditPreviewSocial != null && popupProfileEditPreviewSocial.getVisibility() == View.VISIBLE) {
                    popupProfileEditPreviewSocial.setVisibility(View.GONE);
                    return;
                }
                editFrame.setVisibility(View.GONE);

                ((EditText) editFrame.findViewById(R.id.dataToEdit)).setText("");
                if (contact.getSocialModel() != null)
                    if (contact.getSocialModel().getViberLink() != null)
                        ((EditText) editFrame.findViewById(R.id.dataToEdit)).setText(contact.getSocialModel().getViberLink());


                if (contact.getSocialModel() != null && contact.getSocialModel().getViberLink() != null)
                    showEditPopupPreviewSocial(contact, SocialEnums.VIBER);
                else {
                    editFrame.setVisibility(View.VISIBLE);

                    ((EditText) editFrame.findViewById(R.id.dataToEdit)).setPadding(0, 0, 0, 0);


                    editFrame.findViewById(R.id.frameImageEditSocialInsta).setVisibility(View.GONE);
                    editFrame.findViewById(R.id.imageEditSocial).setVisibility(View.GONE);
                    editFrame.findViewById(R.id.get_last_clips).setVisibility(View.GONE);
                    editFrame.findViewById(R.id.searchSocial).setVisibility(View.GONE);
                    editFrame.findViewById(R.id.ok_social).setVisibility(View.VISIBLE);
                }

                //editFrame.setVisibility(View.VISIBLE);
                openedViews.add(editFrame);
                ((TextView) editFrame.findViewById(R.id.typeField)).setText("Viber");
                ((EditText) editFrame.findViewById(R.id.dataToEdit)).setHint("Enter mobile number");

                editFrame.findViewById(R.id.updateContactField).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {



                        Realm realm = Realm.getDefaultInstance(); //-
                        realm.beginTransaction();
                        SocialModel socialModel;
                        if (contact.getSocialModel() == null) {
                            socialModel = realm.createObject(SocialModel.class);
                        } else
                            socialModel = contact.getSocialModel();

                        if (((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().length() > 0) {
                            //       if (contact.getSocialModel() != null)

                            if (socialModel.getViberLink() != null && !socialModel.getViberLink().isEmpty()) {
                                contactsService.deleteNoteContact(contact.getIdContact(), socialModel.getViberLink());
                            }

                            socialModel.setViberLink(((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().toString());
                            ((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().clear();
                            //        ((ImageView) socialPopup.findViewById(R.id.viber_icon)).setImageResource(R.drawable.icn_social_viber);
                            //        ((ImageView) getActivity().findViewById(R.id.viber_icon)).setImageResource(R.drawable.icn_social_viber);
                            Drawable colorvi = new ColorDrawable(Color.parseColor("#6F3FAA"));
                            Drawable imagevi = getResources().getDrawable(R.drawable.icn_social_viber2);
                            LayerDrawable ldvi = new LayerDrawable(new Drawable[]{colorvi, imagevi});
                            ((CircleImageView) socialPopup.findViewById(R.id.viber_icon)).setImageDrawable(ldvi);


                            if (profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE) {
                                ((CircleImageView) profilePopUp.findViewById(R.id.viber_icon)).setImageDrawable(ldvi);
                            } else if (companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE) {
                                ((CircleImageView) companyProfilePopup.findViewById(R.id.viber_icon)).setImageDrawable(ldvi);
                            } else {
                                ((CircleImageView) getActivity().findViewById(R.id.viber_icon)).setImageDrawable(ldvi);
                            }


                            //   if (contact.getSocialModel() != null)
                            ((TextView) socialPopup.findViewById(R.id.viber_text)).setText(socialModel.getViberLink());
                            contact.hasViber = true;
                            contact.setSocialModel(socialModel);
                            realm.commitTransaction();
                            contactsService.addNoteToContact(contact.getIdContact(), contact.getSocialModel().getViberLink(), contact.getName());
                            ContactCacheService.updateContact(contact, mainView.getContext());
                            //    EventBus.getDefault().post(new UpdateFile());
                            editFrame.setVisibility(View.GONE);
                        } else {
                            if (socialModel.getViberLink() != null && !socialModel.getViberLink().isEmpty()) {
                                contactsService.deleteNoteContact(contact.getIdContact(), socialModel.getViberLink());
                            }
                            socialModel.setViberLink(null);
                            ((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().clear();
                            //      ((ImageView) socialPopup.findViewById(R.id.viber_icon)).setImageResource(R.drawable.icn_social_viber_gray);
                            //      ((ImageView) getActivity().findViewById(R.id.viber_icon)).setImageResource(R.drawable.icn_social_viber_gray);
                            Drawable color5 = new ColorDrawable(Color.parseColor("#e2e5e8"));
                            Drawable image5 = getResources().getDrawable(R.drawable.icn_social_viber2_gray);
                            LayerDrawable ld5 = new LayerDrawable(new Drawable[]{color5, image5});
                            ((CircleImageView) socialPopup.findViewById(R.id.viber_icon)).setImageDrawable(ld5);


                            if (profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE) {
                                ((CircleImageView) profilePopUp.findViewById(R.id.viber_icon)).setImageDrawable(ld5);
                            } else if (companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE) {
                                ((CircleImageView) companyProfilePopup.findViewById(R.id.viber_icon)).setImageDrawable(ld5);
                            } else {
                                ((CircleImageView) getActivity().findViewById(R.id.viber_icon)).setImageDrawable(ld5);
                            }

                            ((TextView) socialPopup.findViewById(R.id.viber_text)).setText("add new link");
                            contact.hasViber = false;
                            contact.setSocialModel(socialModel);
                            realm.commitTransaction();
                            ContactCacheService.updateContact(contact, mainView.getContext());
                            //   EventBus.getDefault().post(new UpdateFile());
                            editFrame.setVisibility(View.GONE);
                        }

                        realm.close();

                        ArrayList<String> listEdit = new ArrayList<>();
                        ArrayList<Boolean> listEditBool = new ArrayList<>();

                        listEdit.add(contact.getName());
                        if (contact.listOfContacts == null || contact.listOfContacts.isEmpty())
                            listEditBool.add(true);
                        else
                            listEditBool.add(false);

                        MainActivity.listToManyUpdateFile.add("EDIT");
                        MainActivity.listToManyUpdateFile.add(listEdit);
                        MainActivity.listToManyUpdateFile.add(listEditBool);

                        //EventBus.getDefault().post(new UpdateFile());
                    }
                });
            }
        });


        socialPopup.findViewById(R.id.whatsapp_link).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (popupProfileEditPreviewSocial != null && popupProfileEditPreviewSocial.getVisibility() == View.VISIBLE) {
                    popupProfileEditPreviewSocial.setVisibility(View.GONE);
                    return;
                }
                editFrame.setVisibility(View.GONE);

                ((EditText) editFrame.findViewById(R.id.dataToEdit)).setText("");
                if (contact.getSocialModel() != null)
                    if (contact.getSocialModel().getWhatsappLink() != null)
                        ((EditText) editFrame.findViewById(R.id.dataToEdit)).setText(contact.getSocialModel().getWhatsappLink());

                if (contact.getSocialModel() != null && contact.getSocialModel().getWhatsappLink() != null)
                    showEditPopupPreviewSocial(contact, SocialEnums.WHATSAPP);
                else {
                    editFrame.setVisibility(View.VISIBLE);

                    ((EditText) editFrame.findViewById(R.id.dataToEdit)).setPadding(0, 0, 0, 0);

                    editFrame.findViewById(R.id.imageEditSocial).setVisibility(View.GONE);
                    editFrame.findViewById(R.id.searchSocial).setVisibility(View.GONE);
                    editFrame.findViewById(R.id.get_last_clips).setVisibility(View.GONE);
                    editFrame.findViewById(R.id.frameImageEditSocialInsta).setVisibility(View.GONE);
                    editFrame.findViewById(R.id.ok_social).setVisibility(View.VISIBLE);
                }

                //editFrame.setVisibility(View.VISIBLE);
                openedViews.add(editFrame);
                ((TextView) editFrame.findViewById(R.id.typeField)).setText("Whatsapp");
                ((EditText) editFrame.findViewById(R.id.dataToEdit)).setHint("Enter mobile number");

                editFrame.findViewById(R.id.updateContactField).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        Realm realm = Realm.getDefaultInstance(); //-
                        realm.beginTransaction();
                        SocialModel socialModel;
                        if (contact.getSocialModel() == null) {
                            socialModel = realm.createObject(SocialModel.class);
                        } else
                            socialModel = contact.getSocialModel();


                        //  contact.setSocialModel(new SocialModel());
                        if (((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().length() > 0) {
                            // if (contact.getSocialModel() != null)

                            if (socialModel.getWhatsappLink() != null && !socialModel.getWhatsappLink().isEmpty()) {
                                contactsService.deleteNoteContact(contact.getIdContact(), socialModel.getWhatsappLink());
                            }

                            socialModel.setWhatsappLink(((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().toString());

                            //    contact.getSocialModel().setWhatsappLink(((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().toString());
                            ((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().clear();
                            //  ((ImageView) socialPopup.findViewById(R.id.whatsapp_icon)).setImageResource(R.drawable.icn_social_whatsapp);
                            //  ((ImageView) getActivity().findViewById(R.id.whatsapp_icon)).setImageResource(R.drawable.icn_social_whatsapp);
                            Drawable colorw = new ColorDrawable(Color.parseColor("#75B73B"));
                            Drawable imagew = getResources().getDrawable(R.drawable.icn_social_whatsapp3);
                            LayerDrawable ldw = new LayerDrawable(new Drawable[]{colorw, imagew});
                            ((CircleImageView) socialPopup.findViewById(R.id.whatsapp_icon)).setImageDrawable(ldw);


                            if (profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE) {
                                ((CircleImageView) profilePopUp.findViewById(R.id.whatsapp_icon)).setImageDrawable(ldw);
                            } else if (companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE) {
                                ((CircleImageView) companyProfilePopup.findViewById(R.id.whatsapp_icon)).setImageDrawable(ldw);
                            } else {
                                ((CircleImageView) getActivity().findViewById(R.id.whatsapp_icon)).setImageDrawable(ldw);
                            }

                            //   if(contact.getSocialModel() != null)
                            ((TextView) socialPopup.findViewById(R.id.whatsapp_text)).setText(socialModel.getWhatsappLink());


                            contact.hasWhatsapp = true;
                            contact.setSocialModel(socialModel);
                            realm.commitTransaction();
                            contactsService.addNoteToContact(contact.getIdContact(), contact.getSocialModel().getWhatsappLink(), contact.getName());
                            ContactCacheService.updateContact(contact, mainView.getContext());
                            // EventBus.getDefault().post(new UpdateFile());
                            editFrame.setVisibility(View.GONE);
                        } else {
                            if (socialModel.getWhatsappLink() != null && !socialModel.getWhatsappLink().isEmpty()) {
                                contactsService.deleteNoteContact(contact.getIdContact(), socialModel.getWhatsappLink());
                            }
                            socialModel.setWhatsappLink(null);
                            //      contact.getSocialModel().setWhatsappLink(null);
                            ((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().clear();
                            //    ((ImageView) socialPopup.findViewById(R.id.whatsapp_icon)).setImageResource(R.drawable.icn_social_whatsapp_gray);
                            //    ((ImageView) getActivity().findViewById(R.id.whatsapp_icon)).setImageResource(R.drawable.icn_social_whatsapp_gray);
                            Drawable color6 = new ColorDrawable(Color.parseColor("#e2e5e8"));
                            Drawable image6 = getResources().getDrawable(R.drawable.icn_social_whatsapp3_gray);
                            LayerDrawable ld6 = new LayerDrawable(new Drawable[]{color6, image6});
                            ((CircleImageView) socialPopup.findViewById(R.id.whatsapp_icon)).setImageDrawable(ld6);


                            if (profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE) {
                                ((CircleImageView) profilePopUp.findViewById(R.id.whatsapp_icon)).setImageDrawable(ld6);
                            } else if (companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE) {
                                ((CircleImageView) companyProfilePopup.findViewById(R.id.whatsapp_icon)).setImageDrawable(ld6);
                            } else {
                                ((CircleImageView) getActivity().findViewById(R.id.whatsapp_icon)).setImageDrawable(ld6);
                            }


                            ((TextView) socialPopup.findViewById(R.id.whatsapp_text)).setText("add new link");
                            contact.hasWhatsapp = false;
                            contact.setSocialModel(socialModel);
                            realm.commitTransaction();
                            ContactCacheService.updateContact(contact, mainView.getContext());
                            //  EventBus.getDefault().post(new UpdateFile());
                            editFrame.setVisibility(View.GONE);
                        }

                        realm.close();

                        /*ArrayList<String> listEdit = new ArrayList<>();
                        ArrayList<Boolean> listEditBool = new ArrayList<>();

                        listEdit.add(contact.getName());
                        if (contact.listOfContacts == null || contact.listOfContacts.isEmpty())
                            listEditBool.add(true);
                        else
                            listEditBool.add(false);

                        MainActivity.listToManyUpdateFile.add("EDIT");
                        MainActivity.listToManyUpdateFile.add(listEdit);
                        MainActivity.listToManyUpdateFile.add(listEditBool);*/

                       //EventBus.getDefault().post(new UpdateFile());
                        //    realm.commitTransaction();
                    }
                });
            }
        });

        socialPopup.findViewById(R.id.telegramLink).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupProfileEditPreviewSocial != null && popupProfileEditPreviewSocial.getVisibility() == View.VISIBLE) {
                    popupProfileEditPreviewSocial.setVisibility(View.GONE);
                    return;
                }
                editFrame.setVisibility(View.GONE);

                ((EditText) editFrame.findViewById(R.id.dataToEdit)).setText("");
                if (contact.getSocialModel() != null)
                    if (contact.getSocialModel().getTelegramLink() != null)
                        ((EditText) editFrame.findViewById(R.id.dataToEdit)).setText(contact.getSocialModel().getTelegramLink());

                if (contact.getSocialModel() != null && contact.getSocialModel().getTelegramLink() != null)
                    showEditPopupPreviewSocial(contact, SocialEnums.TELEGRAM);
                else {
                    editFrame.setVisibility(View.VISIBLE);

                    ((EditText) editFrame.findViewById(R.id.dataToEdit)).setPadding(0, 0, 0, 0);

                    editFrame.findViewById(R.id.imageEditSocial).setVisibility(View.GONE);
                    editFrame.findViewById(R.id.searchSocial).setVisibility(View.GONE);
                    editFrame.findViewById(R.id.get_last_clips).setVisibility(View.GONE);
                    editFrame.findViewById(R.id.frameImageEditSocialInsta).setVisibility(View.GONE);
                    editFrame.findViewById(R.id.ok_social).setVisibility(View.VISIBLE);
                }


                //editFrame.setVisibility(View.VISIBLE);
                openedViews.add(editFrame);
                ((TextView) editFrame.findViewById(R.id.typeField)).setText("Telegram");
                ((EditText) editFrame.findViewById(R.id.dataToEdit)).setHint("Enter username or mobile number");

                editFrame.findViewById(R.id.updateContactField).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        Realm realm = Realm.getDefaultInstance(); //-
                        realm.beginTransaction();
                        SocialModel socialModel;
                        if (contact.getSocialModel() == null) {
                            socialModel = realm.createObject(SocialModel.class);
                        } else
                            socialModel = contact.getSocialModel();

                        if (((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().length() > 0) {
                            //    if (contact.getSocialModel() != null)

                            if (socialModel.getTelegramLink() != null && !socialModel.getTelegramLink().isEmpty()) {
                                contactsService.deleteNoteContact(contact.getIdContact(), socialModel.getTelegramLink());
                            }


                            //==================================

                            String username = ((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().toString();
                            char firstSymbol = username.charAt(0);
                            String regex = "[0-9]+";
                            username = username.replaceAll("[-() ]", "");
                            if (((firstSymbol == '@' && !username.substring(1).matches(regex)) || (firstSymbol != '@' && !username.matches(regex))) && !username.contains("t.me/")) {
                                username = "t.me/" + username;
                            }

                            //=======================

                            socialModel.setTelegramLink(username);
                            ((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().clear();
                            //  ((ImageView) socialPopup.findViewById(R.id.telegram_icon)).setImageResource(R.drawable.icn_social_telegram);
                            //  ((ImageView) getActivity().findViewById(R.id.telegram_icon)).setImageResource(R.drawable.icn_social_telegram);
                            Drawable colort = new ColorDrawable(Color.parseColor("#7AA5DA"));
                            Drawable imaget = getResources().getDrawable(R.drawable.icn_social_telegram2);
                            LayerDrawable ldt = new LayerDrawable(new Drawable[]{colort, imaget});
                            ((CircleImageView) socialPopup.findViewById(R.id.telegram_icon)).setImageDrawable(ldt);



                            if (profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE) {
                                ((CircleImageView) profilePopUp.findViewById(R.id.telegram_icon)).setImageDrawable(ldt);
                            } else if (companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE) {
                                ((CircleImageView) companyProfilePopup.findViewById(R.id.telegram_icon)).setImageDrawable(ldt);
                            } else {
                                ((CircleImageView) getActivity().findViewById(R.id.telegram_icon)).setImageDrawable(ldt);
                            }

                            //   if (contact.getSocialModel() != null)


                            contact.hasTelegram = true;
                            contact.setSocialModel(socialModel);
                            realm.commitTransaction();

                            String tel = contact.getSocialModel().getTelegramLink();
                            if (tel.contains(".me/")) {
                                int indexx = tel.indexOf(".me/");
                                String outLink = tel.substring(indexx + 4, tel.length());
                                ((TextView) socialPopup.findViewById(R.id.telegram_text)).setText(outLink);
                            } else if (tel.contains("?p=")) {
                                int indexx = tel.indexOf("?p=");
                                String outLink = tel.substring(indexx + 3, tel.length());
                                ((TextView) socialPopup.findViewById(R.id.telegram_text)).setText(outLink);

                            } else
                                ((TextView) socialPopup.findViewById(R.id.telegram_text)).setText(contact.getSocialModel().getTelegramLink());

                            contactsService.addNoteToContact(contact.getIdContact(), contact.getSocialModel().getTelegramLink(), contact.getName());
                            ContactCacheService.updateContact(contact, mainView.getContext());
                            //  EventBus.getDefault().post(new UpdateFile());
                            editFrame.setVisibility(View.GONE);
                        } else {
                            if (socialModel.getTelegramLink() != null && !socialModel.getTelegramLink().isEmpty()) {
                                contactsService.deleteNoteContact(contact.getIdContact(), socialModel.getTelegramLink());
                            }
                            socialModel.setTelegramLink(null);
                            ((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().clear();
                            //    ((ImageView) socialPopup.findViewById(R.id.telegram_icon)).setImageResource(R.drawable.icn_social_telegram_gray);
                            //    ((ImageView) getActivity().findViewById(R.id.telegram_icon)).setImageResource(R.drawable.icn_social_telegram_gray);
                            Drawable color7 = new ColorDrawable(Color.parseColor("#e2e5e8"));
                            Drawable image7 = getResources().getDrawable(R.drawable.icn_social_telegram2);
                            LayerDrawable ld7 = new LayerDrawable(new Drawable[]{color7, image7});
                            ((CircleImageView) socialPopup.findViewById(R.id.telegram_icon)).setImageDrawable(ld7);


                            if (profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE) {
                                ((CircleImageView) profilePopUp.findViewById(R.id.telegram_icon)).setImageDrawable(ld7);
                            } else if (companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE) {
                                ((CircleImageView) companyProfilePopup.findViewById(R.id.telegram_icon)).setImageDrawable(ld7);
                            } else {
                                ((CircleImageView) getActivity().findViewById(R.id.telegram_icon)).setImageDrawable(ld7);
                            }

                            ((TextView) socialPopup.findViewById(R.id.telegram_text)).setText("add new link");
                            contact.hasTelegram = false;
                            contact.setSocialModel(socialModel);
                            realm.commitTransaction();
                            ContactCacheService.updateContact(contact, mainView.getContext());
                            //  EventBus.getDefault().post(new UpdateFile());
                            editFrame.setVisibility(View.GONE);
                        }

                        realm.close();

                       /* ArrayList<String> listEdit = new ArrayList<>();
                        ArrayList<Boolean> listEditBool = new ArrayList<>();

                        listEdit.add(contact.getName());
                        if (contact.listOfContacts == null || contact.listOfContacts.isEmpty())
                            listEditBool.add(true);
                        else
                            listEditBool.add(false);

                        MainActivity.listToManyUpdateFile.add("EDIT");
                        MainActivity.listToManyUpdateFile.add(listEdit);
                        MainActivity.listToManyUpdateFile.add(listEditBool);*/

                        //EventBus.getDefault().post(new UpdateFile());
                    }
                });
            }
        });


        socialPopup.findViewById(R.id.skypeLink).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupProfileEditPreviewSocial != null && popupProfileEditPreviewSocial.getVisibility() == View.VISIBLE) {
                    popupProfileEditPreviewSocial.setVisibility(View.GONE);
                    return;
                }
                editFrame.setVisibility(View.GONE);
                //editFrame.setVisibility(View.VISIBLE);
                ((EditText) editFrame.findViewById(R.id.dataToEdit)).setText("");
                if (contact.getSocialModel() != null)
                    if (socialModel.getSkypeLink() != null)
                        ((EditText) editFrame.findViewById(R.id.dataToEdit)).setText(contact.getSocialModel().getSkypeLink());

                if (contact.getSocialModel() != null && contact.getSocialModel().getSkypeLink() != null)
                    showEditPopupPreviewSocial(contact, SocialEnums.SKYPE);
                else {
                    editFrame.setVisibility(View.VISIBLE);

                    ((EditText) editFrame.findViewById(R.id.dataToEdit)).setPadding(0, 0, 0, 0);

                    editFrame.findViewById(R.id.imageEditSocial).setVisibility(View.GONE);
                    editFrame.findViewById(R.id.searchSocial).setVisibility(View.GONE);
                    editFrame.findViewById(R.id.get_last_clips).setVisibility(View.GONE);
                    editFrame.findViewById(R.id.ok_social).setVisibility(View.VISIBLE);

                    editFrame.findViewById(R.id.frameImageEditSocialInsta).setVisibility(View.GONE);
                }

                //editFrame.setVisibility(View.VISIBLE);
                openedViews.add(editFrame);
                ((TextView) editFrame.findViewById(R.id.typeField)).setText("Skype");
                ((EditText) editFrame.findViewById(R.id.dataToEdit)).setHint("Enter username");

                editFrame.findViewById(R.id.updateContactField).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Realm realm = Realm.getDefaultInstance(); //-
                        realm.beginTransaction();
                        SocialModel socialModel;
                        if (contact.getSocialModel() == null) {
                            socialModel = realm.createObject(SocialModel.class);
                        } else
                            socialModel = contact.getSocialModel();

                        if (((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().length() > 0) {
                            //     if (contact.getSocialModel() != null)
                            //     contact.getSocialModel().setSkypeLink(((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().toString());
                            //     else

                            if (socialModel.getSkypeLink() != null && !socialModel.getSkypeLink().isEmpty()) {
                                contactsService.deleteNoteContact(contact.getIdContact(), "Skype: " + socialModel.getSkypeLink());
                            }

                            socialModel.setSkypeLink(((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().toString());

                            ((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().clear();
                            //    ((ImageView) socialPopup.findViewById(R.id.skype_icon)).setImageResource(R.drawable.icn_social_skype);
                            //    ((ImageView) getActivity().findViewById(R.id.skype_icon)).setImageResource(R.drawable.icn_social_skype);
                            Drawable colors = new ColorDrawable(Color.parseColor("#1eb8ff"));
                            Drawable images = getResources().getDrawable(R.drawable.icn_social_skype2);
                            LayerDrawable lds = new LayerDrawable(new Drawable[]{colors, images});
                            ((CircleImageView) socialPopup.findViewById(R.id.skype_icon)).setImageDrawable(lds);


                            if (profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE) {
                                ((CircleImageView) profilePopUp.findViewById(R.id.skype_icon)).setImageDrawable(lds);
                            } else if (companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE) {
                                ((CircleImageView) companyProfilePopup.findViewById(R.id.skype_icon)).setImageDrawable(lds);
                            } else {
                                ((CircleImageView) getActivity().findViewById(R.id.skype_icon)).setImageDrawable(lds);
                            }

                            //    if (contact.getSocialModel() != null)
                            //    ((TextView) socialPopup.findViewById(R.id.skype_text)).setText(contact.getSocialModel().getSkypeLink());
                            //    else
                            ((TextView) socialPopup.findViewById(R.id.skype_text)).setText(socialModel.getSkypeLink());
                            contact.hasSkype = true;
                            contact.setSocialModel(socialModel);
                            realm.commitTransaction();
                            contactsService.addNoteToContact(contact.getIdContact(), "Skype: " + contact.getSocialModel().getSkypeLink(), contact.getName());
                            ContactCacheService.updateContact(contact, mainView.getContext());
                            //  EventBus.getDefault().post(new UpdateFile());
                            editFrame.setVisibility(View.GONE);
                        } else {
                            if (socialModel.getSkypeLink() != null && !socialModel.getSkypeLink().isEmpty()) {
                                contactsService.deleteNoteContact(contact.getIdContact(), "Skype: " + socialModel.getSkypeLink());
                            }
                            socialModel.setSkypeLink(null);
                            ((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().clear();
                            //    ((ImageView) socialPopup.findViewById(R.id.skype_icon)).setImageResource(R.drawable.icn_social_skype_gray);
                            //    ((ImageView) getActivity().findViewById(R.id.skype_icon)).setImageResource(R.drawable.icn_social_skype_gray);
                            Drawable color8 = new ColorDrawable(Color.parseColor("#e2e5e8"));
                            Drawable image8 = getResources().getDrawable(R.drawable.icn_social_skype2_gray);
                            LayerDrawable ld8 = new LayerDrawable(new Drawable[]{color8, image8});
                            ((CircleImageView) socialPopup.findViewById(R.id.skype_icon)).setImageDrawable(ld8);


                            if (profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE) {
                                ((CircleImageView) profilePopUp.findViewById(R.id.skype_icon)).setImageDrawable(ld8);
                            } else if (companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE) {
                                ((CircleImageView) companyProfilePopup.findViewById(R.id.skype_icon)).setImageDrawable(ld8);
                            } else {
                                ((CircleImageView) getActivity().findViewById(R.id.skype_icon)).setImageDrawable(ld8);
                            }

                            ((TextView) socialPopup.findViewById(R.id.skype_text)).setText("add new link");
                            contact.hasSkype = false;
                            contact.setSocialModel(socialModel);
                            realm.commitTransaction();
                            ContactCacheService.updateContact(contact, mainView.getContext());
                            //    EventBus.getDefault().post(new UpdateFile());
                            editFrame.setVisibility(View.GONE);
                        }

                        realm.close();

                       /* ArrayList<String> listEdit = new ArrayList<>();
                        ArrayList<Boolean> listEditBool = new ArrayList<>();

                        listEdit.add(contact.getName());
                        if (contact.listOfContacts == null || contact.listOfContacts.isEmpty())
                            listEditBool.add(true);
                        else
                            listEditBool.add(false);

                        MainActivity.listToManyUpdateFile.add("EDIT");
                        MainActivity.listToManyUpdateFile.add(listEdit);
                        MainActivity.listToManyUpdateFile.add(listEditBool);*/

                        //EventBus.getDefault().post(new UpdateFile());
                    }
                });
            }
        });

        editFrame.findViewById(R.id.cancelEditField).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editFrame.setVisibility(View.GONE);
            }
        });

        socialPopup.findViewById(R.id.closeArrowSocial).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initIconColor(company, mainView);


                if (popupProfileEditPreviewSocial != null && popupProfileEditPreviewSocial.getVisibility() == View.VISIBLE)
                    popupProfileEditPreviewSocial.setVisibility(View.GONE);
                socialPopup.setVisibility(View.GONE);
            }
        });
        socialPopup.setVisibility(View.VISIBLE);
        openedViews.add(socialPopup);

    }

    public boolean checkClick = true;
    public boolean checkClick_viber = true;
    public boolean checkClick_telegram = true;
    public boolean checkClick_skype = true;

    public boolean checkClick_facebook = true;
    public boolean checkClick_twitter = true;
    public boolean checkClick_linked = true;
    public boolean checkClick_inst = true;
    public boolean checkClick_youtube = true;
    public boolean checkClick_vk = true;
    public boolean checkClick_medium = true;


    View FaceBook = null;
    View TwiTter = null;
    View LinkedIn = null;
    View Instagram = null;
    View YouTube = null;
    View Vkontakte = null;
    View Medium = null;

    public void initIconColor(Contact contact, View view){
        System.out.println("initicon 1");
        FaceBook = null;
        TwiTter = null;
        LinkedIn = null;
        Instagram = null;
        YouTube = null;
        Vkontakte = null;
        Medium = null;

        ArrayList<SocEnum> listExist = new ArrayList<>();
        LinearLayout linearLayout = view.findViewById(R.id.lineaSocials);
        linearLayout.removeAllViewsInLayout();

        if (contact.getSocialModel() != null && contact.getSocialModel().getFacebookLink() != null && !contact.getSocialModel().getFacebookLink().isEmpty()) {
            listExist.add(SocEnum.FACEBOOK);
            FaceBook = getLayoutInflater().inflate(R.layout.social_facebook, null);

            Drawable color = new ColorDrawable(Color.parseColor("#475993"));
            Drawable image = getResources().getDrawable(R.drawable.icn_social_facebook2);
            LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});

            ((ImageView) FaceBook.findViewById(R.id.facebook_icon)).setImageDrawable(ld);
            linearLayout.addView(FaceBook);

        }

        if ((contact.getSocialModel() != null && contact.getSocialModel().getTwitterLink() != null && !contact.getSocialModel().getTwitterLink().isEmpty())) {
            listExist.add(SocEnum.TWITTER);
            TwiTter = getLayoutInflater().inflate(R.layout.social_twitter, null);

            Drawable color = new ColorDrawable(Color.parseColor("#2ca7e0"));
            Drawable image = getResources().getDrawable(R.drawable.ic_twitter_white);
            LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
            ((ImageView) TwiTter.findViewById(R.id.twitter_icon)).setImageDrawable(ld);

            linearLayout.addView(TwiTter);
        }

        if ((contact.getSocialModel() != null && contact.getSocialModel().getLinkedInLink() != null && !contact.getSocialModel().getLinkedInLink().isEmpty())) {
            listExist.add(SocEnum.LINKEDIN);
            LinkedIn = getLayoutInflater().inflate(R.layout.social_linked, null);

            Drawable color = new ColorDrawable(Color.parseColor("#0077B7"));
            Drawable image = getResources().getDrawable(R.drawable.icn_social_linked2);
            LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
            ((ImageView) LinkedIn.findViewById(R.id.link_icon)).setImageDrawable(ld);

            linearLayout.addView(LinkedIn);
        }

        if ((contact.getSocialModel() != null && contact.getSocialModel().getInstagramLink() != null && !contact.getSocialModel().getInstagramLink().isEmpty())) {
            listExist.add(SocEnum.INSTAGRAM);
            Instagram = getLayoutInflater().inflate(R.layout.social_insta, null);

            Drawable color = new ColorDrawable(Color.parseColor("#8a3ab9"));
            Drawable image = getResources().getDrawable(R.drawable.icn_social_ints2);
            LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
            ((ImageView) Instagram.findViewById(R.id.inst_icon)).setImageDrawable(ld);

            linearLayout.addView(Instagram);
        }

        if (listExist.size() < 4 && (contact.getSocialModel() != null && contact.getSocialModel().getYoutubeLink() != null && !contact.getSocialModel().getYoutubeLink().isEmpty())) {
            listExist.add(SocEnum.YOUTUBE);
            YouTube = getLayoutInflater().inflate(R.layout.social_youtube, null);

            Drawable colorv = new ColorDrawable(Color.parseColor("#ed2524"));
            Drawable imagev = getResources().getDrawable(R.drawable.ic_youtube_white);
            LayerDrawable ldv = new LayerDrawable(new Drawable[]{colorv, imagev});
            ((ImageView) YouTube.findViewById(R.id.youtube_icon)).setImageDrawable(ldv);

            linearLayout.addView(YouTube);
        }

        if (listExist.size() < 4 && (contact.getSocialModel() != null && contact.getSocialModel().getVkLink() != null && !contact.getSocialModel().getVkLink().isEmpty())) {
            listExist.add(SocEnum.VK);
            Vkontakte = getLayoutInflater().inflate(R.layout.social_vk, null);

            Drawable colorv = new ColorDrawable(Color.parseColor("#507299"));
            Drawable imagev = getResources().getDrawable(R.drawable.icn_social_vk2);
            LayerDrawable ldv = new LayerDrawable(new Drawable[]{colorv, imagev});
            ((ImageView) Vkontakte.findViewById(R.id.vk_icon)).setImageDrawable(ldv);

            linearLayout.addView(Vkontakte);
        }

        if (listExist.size() < 4 && (contact.getSocialModel() != null && contact.getSocialModel().getMediumLink() != null && !contact.getSocialModel().getMediumLink().isEmpty())) {
            listExist.add(SocEnum.MEDIUM);
            Medium = getLayoutInflater().inflate(R.layout.social_medium, null);

            Drawable colorv = new ColorDrawable(Color.parseColor("#000000"));
            Drawable imagev = getResources().getDrawable(R.drawable.medium_white);
            LayerDrawable ldv = new LayerDrawable(new Drawable[]{colorv, imagev});
            ((ImageView) Medium.findViewById(R.id.medium_icon)).setImageDrawable(ldv);

            linearLayout.addView(Medium);
        }


        if (linearLayout.getChildCount() < 4) {
            while (true) {
                if (!listExist.contains(SocEnum.FACEBOOK)) {
                    FaceBook = getLayoutInflater().inflate(R.layout.social_facebook, null);

                    Drawable color = new ColorDrawable(Color.parseColor("#e2e5e8"));
                    Drawable image = getActivity().getResources().getDrawable(R.drawable.icn_social_facebook2);
                    LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
                    ((ImageView) FaceBook.findViewById(R.id.facebook_icon)).setImageDrawable(ld);

                    linearLayout.addView(FaceBook);
                }
                if (linearLayout.getChildCount() == 4) break;

                if (!listExist.contains(SocEnum.TWITTER)) {
                    TwiTter = getLayoutInflater().inflate(R.layout.social_twitter, null);

                    Drawable color = new ColorDrawable(Color.parseColor("#e2e5e8"));
                    Drawable image = getResources().getDrawable(R.drawable.ic_twitter_white);
                    LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
                    ((ImageView) TwiTter.findViewById(R.id.twitter_icon)).setImageDrawable(ld);

                    linearLayout.addView(TwiTter);
                }
                if (linearLayout.getChildCount() == 4) break;

                if (!listExist.contains(SocEnum.LINKEDIN)) {
                    LinkedIn = getLayoutInflater().inflate(R.layout.social_linked, null);

                    Drawable color = new ColorDrawable(Color.parseColor("#e2e5e8"));
                    Drawable image = getResources().getDrawable(R.drawable.icn_social_linked2);
                    LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
                    ((ImageView) LinkedIn.findViewById(R.id.link_icon)).setImageDrawable(ld);

                    linearLayout.addView(LinkedIn);
                }
                if (linearLayout.getChildCount() == 4) break;

                if (!listExist.contains(SocEnum.INSTAGRAM)) {
                    Instagram = getLayoutInflater().inflate(R.layout.social_insta, null);

                    Drawable color = new ColorDrawable(Color.parseColor("#e2e5e8"));
                    Drawable image = getResources().getDrawable(R.drawable.icn_social_ints2);
                    LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
                    ((ImageView) Instagram.findViewById(R.id.inst_icon)).setImageDrawable(ld);

                    linearLayout.addView(Instagram);
                }
                if (linearLayout.getChildCount() == 4) break;

            }
        }


        if (FaceBook != null) {
            FaceBook.findViewById(R.id.facebook_icon).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {


                    ImageView cImg = ((ImageView) FaceBook.findViewById(R.id.facebook_icon));
                    //    ImageView imageView = ((ImageView) profilePopUp.findViewById(R.id.imagePreviewIcon));
                    //  FrameLayout frameLayout = ((FrameLayout) mainView.findViewById(R.id.company_profile_block));
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN: {

                            if (listExist.contains(SocEnum.FACEBOOK) && checkClick_facebook) {
                                checkClick_facebook = false;
                                int colorFrom = Color.parseColor("#475993");
                                int colorTo = Color.parseColor("#F9A825");
                                Drawable image = getResources().getDrawable(R.drawable.icn_social_facebook2);
                                AnimColorMessenger.ActionDown_(colorFrom, colorTo, image, cImg);
                            } else {
                                checkClick_facebook = false;
                                int colorFrom = Color.parseColor("#e2e5e8");
                                int colorTo = Color.parseColor("#F9A825");
                                Drawable image = getResources().getDrawable(R.drawable.icn_social_facebook2);
                                AnimColorMessenger.ActionDown_(colorFrom, colorTo, image, cImg);
                            }

                            //     OnTouchMethod(textView);
                            break;
                        }
                        case MotionEvent.ACTION_UP: {
                            if (!checkClick_facebook) {
                                if (listExist.contains(SocEnum.FACEBOOK)) {
                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#475993");
                                    Drawable image = getResources().getDrawable(R.drawable.icn_social_facebook2);
                                    AnimColorMessenger.ActionUp_(colorFrom, colorTo, image, cImg);
                                } else {
                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#e2e5e8");
                                    Drawable image = getResources().getDrawable(R.drawable.icn_social_facebook2);
                                    AnimColorMessenger.ActionUp_(colorFrom, colorTo, image, cImg);
                                }
                            }
                            break;
                        }
                        case MotionEvent.ACTION_CANCEL: {
                            if (listExist.contains(SocEnum.FACEBOOK)) {
                                int colorFrom = Color.parseColor("#F9A825");
                                int colorTo = Color.parseColor("#475993");
                                Drawable image = getResources().getDrawable(R.drawable.icn_social_facebook2);
                                AnimColorMessenger.ActionCancel_(colorFrom, colorTo, image, cImg);
                            } else {
                                int colorFrom = Color.parseColor("#F9A825");
                                int colorTo = Color.parseColor("#e2e5e8");
                                Drawable image = getResources().getDrawable(R.drawable.icn_social_facebook2);
                                AnimColorMessenger.ActionCancel_(colorFrom, colorTo, image, cImg);
                            }
                            break;
                        }
                        case MotionEvent.ACTION_MOVE: {
                            if (!checkClick_facebook) {
                                if (listExist.contains(SocEnum.FACEBOOK)) {
                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#475993");
                                    Drawable image = getResources().getDrawable(R.drawable.icn_social_facebook2);
                                    checkClick_facebook = AnimColorMessenger.ActionMove_(colorFrom, colorTo, image, cImg, motionEvent, FaceBook);
                                } else {
                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#e2e5e8");
                                    Drawable image = getResources().getDrawable(R.drawable.icn_social_facebook2);
                                    checkClick_facebook = AnimColorMessenger.ActionMove_(colorFrom, colorTo, image, cImg, motionEvent, FaceBook);
                                }
                            }

                            break;
                        }
                    }

                    return false;
                }
            });

            FaceBook.findViewById(R.id.facebook_icon).setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (contact.getSocialModel() == null || contact.getSocialModel().getFacebookLink() == null || contact.getSocialModel().getFacebookLink().isEmpty()) {
                        String name = contact.getName();
                        String[] mach = name.split(" ");

                        Intent i = new Intent(Intent.ACTION_VIEW);
                        if (mach.length >= 2) {
                            String str = "";
                            for (int i2 = 0; i2 < mach.length; i2++) {
                                str += mach[i2];
                                if (i2 != mach.length - 1) str += "%20";
                            }

                            i.setData(Uri.parse("https://www.facebook.com/search/people/?q=" + str));
                        } else
                            i.setData(Uri.parse("https://www.facebook.com/search/people/?q=" + mach[0]));
                        startActivity(i);
                        return true;
                    }
                    return false;
                }
            });


            FaceBook.findViewById(R.id.facebook_icon).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if (contact.getSocialModel() == null || contact.getSocialModel().getFacebookLink() == null || contact.getSocialModel().getFacebookLink().isEmpty()) {
                        //    closeOtherPopup();
                        closeChildPopups();
                        showSocialPopup(contact);
                        socialPopup.findViewById(R.id.facebook_social).callOnClick();
                        socialPopup.setVisibility(View.GONE);
                    } else {

                        Intent intent;
                        if (contact.getSocialModel().getFacebookLink().contains("?id=")) {
                            String idProfile = contact.getSocialModel().getFacebookLink().substring(contact.getSocialModel().getFacebookLink().indexOf('=') + 1, contact.getSocialModel().getFacebookLink().length());
                            if (idProfile.contains("&")) {
                                idProfile = idProfile.substring(0, idProfile.indexOf("&"));
                            }
                            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/" + idProfile));

                        } else if (contact.getSocialModel().getFacebookLink().contains("/people/")) {
                            String idProfile = contact.getSocialModel().getFacebookLink().substring(contact.getSocialModel().getFacebookLink().lastIndexOf('/') + 1, contact.getSocialModel().getFacebookLink().length());
                            if (idProfile.contains("&")) {
                                idProfile = idProfile.substring(0, idProfile.indexOf("&"));
                            }

                            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/" + idProfile));

                        } else
                            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://facewebmodal/f?href=" + contact.getSocialModel().getFacebookLink().replace("fb","facebook")));

                        //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://facewebmodal/f?href="+contact.getSocialModel().getFacebookLink()));
                        PackageManager packageManager = getActivity().getPackageManager();
                        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
                        boolean isIntentSafe = activities.size() > 0;
                        if (!isIntentSafe) {
                        /*Intent intent2 = new Intent(Intent.ACTION_VIEW);
                              intent2.setData(Uri.parse("market://details?id=com.facebook.katana"));
                              startActivity(intent2);*/

                            try {
                                String uri = contact.getSocialModel().getFacebookLink();
                                if (!contact.getSocialModel().getFacebookLink().contains("https://") && !contact.getSocialModel().getFacebookLink().contains("http://"))
                                    uri = "https://" + uri;

                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(uri));
                                startActivity(i);
                            } catch (Exception e) {

                            }

                        } else
                            startActivity(intent);
                    }


                }
            });
        }


        if (TwiTter != null) {
            TwiTter.findViewById(R.id.twitter_icon).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {


                    ImageView cImg = ((ImageView) TwiTter.findViewById(R.id.twitter_icon));
                    //    ImageView imageView = ((ImageView) profilePopUp.findViewById(R.id.imagePreviewIcon));
                    //  FrameLayout frameLayout = ((FrameLayout) mainView.findViewById(R.id.company_profile_block));
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN: {

                            if (listExist.contains(SocEnum.TWITTER) && checkClick_twitter) {
                                checkClick_twitter = false;
                                int colorFrom = Color.parseColor("#2ca7e0");
                                int colorTo = Color.parseColor("#F9A825");
                                Drawable image = getResources().getDrawable(R.drawable.ic_twitter_white);
                                AnimColorMessenger.ActionDown_(colorFrom, colorTo, image, cImg);
                            } else {
                                checkClick_twitter = false;
                                int colorFrom = Color.parseColor("#e2e5e8");
                                int colorTo = Color.parseColor("#F9A825");
                                Drawable image = getResources().getDrawable(R.drawable.ic_twitter_white);
                                AnimColorMessenger.ActionDown_(colorFrom, colorTo, image, cImg);
                            }

                            //     OnTouchMethod(textView);
                            break;
                        }
                        case MotionEvent.ACTION_UP: {
                            if (!checkClick_twitter) {
                                if (listExist.contains(SocEnum.TWITTER)) {
                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#2ca7e0");
                                    Drawable image = getResources().getDrawable(R.drawable.ic_twitter_white);
                                    AnimColorMessenger.ActionUp_(colorFrom, colorTo, image, cImg);
                                } else {
                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#e2e5e8");
                                    Drawable image = getResources().getDrawable(R.drawable.ic_twitter_white);
                                    AnimColorMessenger.ActionUp_(colorFrom, colorTo, image, cImg);
                                }
                            }
                            break;
                        }
                        case MotionEvent.ACTION_CANCEL: {
                            if (listExist.contains(SocEnum.TWITTER)) {
                                int colorFrom = Color.parseColor("#F9A825");
                                int colorTo = Color.parseColor("#2ca7e0");
                                Drawable image = getResources().getDrawable(R.drawable.ic_twitter_white);
                                AnimColorMessenger.ActionCancel_(colorFrom, colorTo, image, cImg);
                            } else {
                                int colorFrom = Color.parseColor("#F9A825");
                                int colorTo = Color.parseColor("#e2e5e8");
                                Drawable image = getResources().getDrawable(R.drawable.ic_twitter_white);
                                AnimColorMessenger.ActionCancel_(colorFrom, colorTo, image, cImg);
                            }
                            break;
                        }
                        case MotionEvent.ACTION_MOVE: {
                            if (!checkClick_twitter) {
                                if (listExist.contains(SocEnum.TWITTER)) {
                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#2ca7e0");
                                    Drawable image = getResources().getDrawable(R.drawable.ic_twitter_white);
                                    checkClick_twitter = AnimColorMessenger.ActionMove_(colorFrom, colorTo, image, cImg, motionEvent, TwiTter);
                                } else {
                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#e2e5e8");
                                    Drawable image = getResources().getDrawable(R.drawable.ic_twitter_white);
                                    checkClick_twitter = AnimColorMessenger.ActionMove_(colorFrom, colorTo, image, cImg, motionEvent, TwiTter);
                                }
                            }

                            break;
                        }
                    }

                    return false;
                }
            });

            TwiTter.findViewById(R.id.twitter_icon).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if (contact.getSocialModel() == null || contact.getSocialModel().getTwitterLink() == null || contact.getSocialModel().getTwitterLink().isEmpty()) {
                        //    closeOtherPopup();
                        closeChildPopups();
                        showSocialPopup(contact);
                        socialPopup.findViewById(R.id.twitter_link).callOnClick();
                        socialPopup.setVisibility(View.GONE);
                    } else {
                        String text = contact.getSocialModel().getTwitterLink();
                        if (text.contains("twitter.com/")) {
                            text = text.substring(text.indexOf(".com/") + 5);
                        }
                        if(text.length() > 0 && text.charAt(0) == '@') text = text.substring(1);
                        Intent intent = null;
                        try {
                            // get the Twitter app if possible

                            getActivity().getPackageManager().getPackageInfo("com.twitter.android", 0);
                            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + text));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        } catch (Exception e) {
                            // no Twitter app, revert to browser
                            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/" + text));
                        }
                        getActivity().startActivity(intent);


                    }


                }
            });

            TwiTter.findViewById(R.id.twitter_icon).setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (contact.getSocialModel() == null || contact.getSocialModel().getTwitterLink() == null || contact.getSocialModel().getTwitterLink().isEmpty()) {
                        String name = contact.getName();

                        if (name == null || name.trim().isEmpty()) return false;

                        String[] mach = name.split(" ");

                        Intent i = new Intent(Intent.ACTION_VIEW);
                        if (mach.length >= 2) {
                            String str = "";
                            for (int i2 = 0; i2 < mach.length; i2++) {
                                str += mach[i2];
                                if (i2 != mach.length - 1) str += "%20";
                            }
                            //i.setData(Uri.parse("https://www.linkedin.com/mwlite/search/results/all/?keywords=" + str));

                            //   if (contact.listOfContacts == null || contact.listOfContacts.isEmpty())

                            i.setData(Uri.parse("https://twitter.com/search?q=" + str + "&src=typed_query&f=user"));
                            //   else
                            //       i.setData(Uri.parse("https://www.linkedin.com/search/results/companies/v2/?keywords=" + str));

                        } else {
                            //   if (contact.listOfContacts == null || contact.listOfContacts.isEmpty())
                            i.setData(Uri.parse("https://twitter.com/search?q=" + mach[0] + "&src=typed_query&f=user"));
                            //   else
                            //       i.setData(Uri.parse("https://www.linkedin.com/search/results/companies/v2/?keywords=" + mach[0]));
                        }

                        startActivity(i);
                        return true;
                    }
                    return false;
                }
            });

        }

        if (LinkedIn != null) {
            LinkedIn.findViewById(R.id.link_icon).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {


                    ImageView cImg = ((ImageView) LinkedIn.findViewById(R.id.link_icon));
                    //    ImageView imageView = ((ImageView) profilePopUp.findViewById(R.id.imagePreviewIcon));
                    //  FrameLayout frameLayout = ((FrameLayout) mainView.findViewById(R.id.company_profile_block));
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN: {

                            if (listExist.contains(SocEnum.LINKEDIN) && checkClick_linked) {
                                checkClick_linked = false;
                                int colorFrom = Color.parseColor("#0077B7");
                                int colorTo = Color.parseColor("#F9A825");
                                Drawable image = getResources().getDrawable(R.drawable.icn_social_linked2);
                                AnimColorMessenger.ActionDown_(colorFrom, colorTo, image, cImg);
                            } else {
                                checkClick_linked = false;
                                int colorFrom = Color.parseColor("#e2e5e8");
                                int colorTo = Color.parseColor("#F9A825");
                                Drawable image = getResources().getDrawable(R.drawable.icn_social_linked2);
                                AnimColorMessenger.ActionDown_(colorFrom, colorTo, image, cImg);
                            }

                            //     OnTouchMethod(textView);
                            break;
                        }
                        case MotionEvent.ACTION_UP: {
                            if (!checkClick_linked) {
                                if (listExist.contains(SocEnum.LINKEDIN)) {
                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#0077B7");
                                    Drawable image = getResources().getDrawable(R.drawable.icn_social_linked2);
                                    AnimColorMessenger.ActionUp_(colorFrom, colorTo, image, cImg);
                                } else {
                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#e2e5e8");
                                    Drawable image = getResources().getDrawable(R.drawable.icn_social_linked2);
                                    AnimColorMessenger.ActionUp_(colorFrom, colorTo, image, cImg);
                                }
                            }
                            break;
                        }
                        case MotionEvent.ACTION_CANCEL: {
                            if (listExist.contains(SocEnum.LINKEDIN)) {
                                int colorFrom = Color.parseColor("#F9A825");
                                int colorTo = Color.parseColor("#0077B7");
                                Drawable image = getResources().getDrawable(R.drawable.icn_social_linked2);
                                AnimColorMessenger.ActionCancel_(colorFrom, colorTo, image, cImg);
                            } else {
                                int colorFrom = Color.parseColor("#F9A825");
                                int colorTo = Color.parseColor("#e2e5e8");
                                Drawable image = getResources().getDrawable(R.drawable.icn_social_linked2);
                                AnimColorMessenger.ActionCancel_(colorFrom, colorTo, image, cImg);
                            }
                            break;
                        }
                        case MotionEvent.ACTION_MOVE: {
                            if (!checkClick_linked) {
                                if (listExist.contains(SocEnum.LINKEDIN)) {
                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#0077B7");
                                    Drawable image = getResources().getDrawable(R.drawable.icn_social_linked2);
                                    checkClick_linked = AnimColorMessenger.ActionMove_(colorFrom, colorTo, image, cImg, motionEvent, LinkedIn);
                                } else {
                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#e2e5e8");
                                    Drawable image = getResources().getDrawable(R.drawable.icn_social_linked2);
                                    checkClick_linked = AnimColorMessenger.ActionMove_(colorFrom, colorTo, image, cImg, motionEvent, LinkedIn);
                                }
                            }

                            break;
                        }
                    }

                    return false;
                }
            });


            LinkedIn.findViewById(R.id.link_icon).setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (contact.getSocialModel() == null || contact.getSocialModel().getLinkedInLink() == null || contact.getSocialModel().getLinkedInLink().isEmpty()) {
                        String name = contact.getName();
                        String[] mach = name.split(" ");

                        Intent i = new Intent(Intent.ACTION_VIEW);
                        if (mach.length >= 2) {
                            String str = "";
                            for (int i2 = 0; i2 < mach.length; i2++) {
                                str += mach[i2];
                                if (i2 != mach.length - 1) str += "%20";
                            }
                            //   if (selectedContact.listOfContacts == null || selectedContact.listOfContacts.isEmpty())
                            i.setData(Uri.parse("https://www.linkedin.com/mwlite/search/results/all/?keywords=" + str));
                            //   else
                            //       i.setData(Uri.parse("https://www.linkedin.com/search/results/companies/v2/?keywords=" + str));

                        } else {
                            //    if (selectedContact.listOfContacts == null || selectedContact.listOfContacts.isEmpty())
                            i.setData(Uri.parse("https://www.linkedin.com/mwlite/search/results/all/?keywords=" + mach[0]));
                            //    else
                            //        i.setData(Uri.parse("https://www.linkedin.com/search/results/companies/v2/?keywords=" + mach[0]));

                        }

                        startActivity(i);
                        return true;
                    }

                    return false;
                }
            });


            LinkedIn.findViewById(R.id.link_icon).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if (contact.getSocialModel() == null || contact.getSocialModel().getLinkedInLink() == null || contact.getSocialModel().getLinkedInLink().isEmpty()) {
                        //    closeOtherPopup();
                        closeChildPopups();
                        showSocialPopup(contact);
                        socialPopup.findViewById(R.id.linkedLink).callOnClick();
                        socialPopup.setVisibility(View.GONE);
                    } else {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(contact.getSocialModel().getLinkedInLink()));
                        PackageManager packageManager = getActivity().getPackageManager();
                        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
                        boolean isIntentSafe = activities.size() > 0;
                        if (!isIntentSafe) {
                            Intent intent2 = new Intent(Intent.ACTION_VIEW);
                            intent2.setData(Uri.parse("https://www.linkedin.com/in/"));
                            startActivity(intent2);
                        } else {
                            try {
                                startActivity(intent);
                            } catch (Exception e) {
                                Intent intent2 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/"));
                                startActivity(intent2);
                            }


                        }
                    }


                }
            });
        }

        if (Instagram != null) {
            Instagram.findViewById(R.id.inst_icon).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {


                    ImageView cImg = ((ImageView) Instagram.findViewById(R.id.inst_icon));
                    //    ImageView imageView = ((ImageView) profilePopUp.findViewById(R.id.imagePreviewIcon));
                    //  FrameLayout frameLayout = ((FrameLayout) mainView.findViewById(R.id.company_profile_block));
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN: {

                            if (listExist.contains(SocEnum.INSTAGRAM) && checkClick_inst) {
                                checkClick_inst = false;
                                int colorFrom = Color.parseColor("#8a3ab9");
                                int colorTo = Color.parseColor("#F9A825");
                                Drawable image = getResources().getDrawable(R.drawable.icn_social_ints2);
                                AnimColorMessenger.ActionDown_(colorFrom, colorTo, image, cImg);
                            } else {
                                checkClick_inst = false;
                                int colorFrom = Color.parseColor("#e2e5e8");
                                int colorTo = Color.parseColor("#F9A825");
                                Drawable image = getResources().getDrawable(R.drawable.icn_social_ints2);
                                AnimColorMessenger.ActionDown_(colorFrom, colorTo, image, cImg);
                            }

                            //     OnTouchMethod(textView);
                            break;
                        }
                        case MotionEvent.ACTION_UP: {
                            if (!checkClick_inst) {
                                if (listExist.contains(SocEnum.INSTAGRAM)) {
                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#8a3ab9");
                                    Drawable image = getResources().getDrawable(R.drawable.icn_social_ints2);
                                    AnimColorMessenger.ActionUp_(colorFrom, colorTo, image, cImg);
                                } else {
                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#e2e5e8");
                                    Drawable image = getResources().getDrawable(R.drawable.icn_social_ints2);
                                    AnimColorMessenger.ActionUp_(colorFrom, colorTo, image, cImg);
                                }
                            }
                            break;
                        }
                        case MotionEvent.ACTION_CANCEL: {
                            if (listExist.contains(SocEnum.INSTAGRAM)) {
                                int colorFrom = Color.parseColor("#F9A825");
                                int colorTo = Color.parseColor("#8a3ab9");
                                Drawable image = getResources().getDrawable(R.drawable.icn_social_ints2);
                                AnimColorMessenger.ActionCancel_(colorFrom, colorTo, image, cImg);
                            } else {
                                int colorFrom = Color.parseColor("#F9A825");
                                int colorTo = Color.parseColor("#e2e5e8");
                                Drawable image = getResources().getDrawable(R.drawable.icn_social_ints2);
                                AnimColorMessenger.ActionCancel_(colorFrom, colorTo, image, cImg);
                            }
                            break;
                        }
                        case MotionEvent.ACTION_MOVE: {
                            if (!checkClick_inst) {
                                if (listExist.contains(SocEnum.INSTAGRAM)) {
                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#8a3ab9");
                                    Drawable image = getResources().getDrawable(R.drawable.icn_social_ints2);
                                    checkClick_inst = AnimColorMessenger.ActionMove_(colorFrom, colorTo, image, cImg, motionEvent, Instagram);
                                } else {
                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#e2e5e8");
                                    Drawable image = getResources().getDrawable(R.drawable.icn_social_ints2);
                                    checkClick_inst = AnimColorMessenger.ActionMove_(colorFrom, colorTo, image, cImg, motionEvent, Instagram);
                                }
                            }

                            break;
                        }
                    }

                    return false;
                }
            });

            Instagram.findViewById(R.id.inst_icon).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if (contact.getSocialModel() == null || contact.getSocialModel().getInstagramLink() == null || contact.getSocialModel().getInstagramLink().isEmpty()) {
                        //   closeOtherPopup();
                        closeChildPopups();
                        showSocialPopup(contact);
                        socialPopup.findViewById(R.id.instagramLink).callOnClick();
                        socialPopup.setVisibility(View.GONE);
                    } else {


                        //if(contact.getSocialModel().getInstagramLink() == null) return;
                        try {
                            String str = contact.getSocialModel().getInstagramLink();
                            if (!str.toLowerCase().contains("instagram")) {
                                str = "https://instagram.com/" + contact.getSocialModel().getInstagramLink();
                            }

                            if (!str.contains("http://") && !str.contains("https://")) {
                                str = "https://" + contact.getSocialModel().getInstagramLink();
                            }

                            Uri uri = Uri.parse(str);
                            Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);
                            likeIng.setPackage("com.instagram.android");
                            PackageManager packageManager = getActivity().getPackageManager();
                            List<ResolveInfo> activities = packageManager.queryIntentActivities(likeIng, 0);
                            boolean isIntentSafe = activities.size() > 0;
                            if (!isIntentSafe) {
                            /*Intent intent2 = new Intent(Intent.ACTION_VIEW);
                                  intent2.setData(Uri.parse("market://details?id=com.instagram.android"));
                                  startActivity(intent2);*/

                                try {
                                    String uris = contact.getSocialModel().getInstagramLink();
                                    if (!contact.getSocialModel().getInstagramLink().contains("https://") && !contact.getSocialModel().getInstagramLink().contains("http://"))
                                        uris = "https://" + uris;

                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                    i.setData(Uri.parse(uris));
                                    startActivity(i);
                                } catch (Exception e) {

                                }

                            } else
                                startActivity(likeIng);
                        } catch (Exception e) {

                        }
                    }


                }
            });
        }

        if (YouTube != null) {
            YouTube.findViewById(R.id.youtube_icon).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {


                    ImageView cImg = ((ImageView) YouTube.findViewById(R.id.youtube_icon));
                    //    ImageView imageView = ((ImageView) profilePopUp.findViewById(R.id.imagePreviewIcon));
                    //  FrameLayout frameLayout = ((FrameLayout) mainView.findViewById(R.id.company_profile_block));
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN: {

                            if (listExist.contains(SocEnum.YOUTUBE) && checkClick_youtube) {
                                checkClick_youtube = false;
                                int colorFrom = Color.parseColor("#ed2524");
                                int colorTo = Color.parseColor("#F9A825");
                                Drawable image = getResources().getDrawable(R.drawable.ic_youtube_white);
                                AnimColorMessenger.ActionDown_(colorFrom, colorTo, image, cImg);
                            } else {
                                checkClick_youtube = false;
                                int colorFrom = Color.parseColor("#e2e5e8");
                                int colorTo = Color.parseColor("#F9A825");
                                Drawable image = getResources().getDrawable(R.drawable.ic_youtube_white);
                                AnimColorMessenger.ActionDown_(colorFrom, colorTo, image, cImg);
                            }

                            //     OnTouchMethod(textView);
                            break;
                        }
                        case MotionEvent.ACTION_UP: {
                            if (!checkClick_youtube) {
                                if (listExist.contains(SocEnum.YOUTUBE)) {
                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#ed2524");
                                    Drawable image = getResources().getDrawable(R.drawable.ic_youtube_white);
                                    AnimColorMessenger.ActionUp_(colorFrom, colorTo, image, cImg);
                                } else {
                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#e2e5e8");
                                    Drawable image = getResources().getDrawable(R.drawable.ic_youtube_white);
                                    AnimColorMessenger.ActionUp_(colorFrom, colorTo, image, cImg);
                                }
                            }
                            break;
                        }
                        case MotionEvent.ACTION_CANCEL: {
                            if (listExist.contains(SocEnum.YOUTUBE)) {
                                int colorFrom = Color.parseColor("#F9A825");
                                int colorTo = Color.parseColor("#ed2524");
                                Drawable image = getResources().getDrawable(R.drawable.ic_youtube_white);
                                AnimColorMessenger.ActionCancel_(colorFrom, colorTo, image, cImg);
                            } else {
                                int colorFrom = Color.parseColor("#F9A825");
                                int colorTo = Color.parseColor("#e2e5e8");
                                Drawable image = getResources().getDrawable(R.drawable.ic_youtube_white);
                                AnimColorMessenger.ActionCancel_(colorFrom, colorTo, image, cImg);
                            }
                            break;
                        }
                        case MotionEvent.ACTION_MOVE: {
                            if (!checkClick_youtube) {
                                if (listExist.contains(SocEnum.YOUTUBE)) {
                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#ed2524");
                                    Drawable image = getResources().getDrawable(R.drawable.ic_youtube_white);
                                    checkClick_youtube = AnimColorMessenger.ActionMove_(colorFrom, colorTo, image, cImg, motionEvent, YouTube);
                                } else {
                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#e2e5e8");
                                    Drawable image = getResources().getDrawable(R.drawable.ic_youtube_white);
                                    checkClick_youtube = AnimColorMessenger.ActionMove_(colorFrom, colorTo, image, cImg, motionEvent, YouTube);
                                }
                            }

                            break;
                        }
                    }

                    return false;
                }
            });

            YouTube.findViewById(R.id.youtube_icon).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if (contact.getSocialModel() == null || contact.getSocialModel().getYoutubeLink() == null || contact.getSocialModel().getYoutubeLink().isEmpty()) {
                        //    closeOtherPopup();
                        closeChildPopups();
                        showSocialPopup(contact);
                        socialPopup.findViewById(R.id.youtube_link).callOnClick();
                        socialPopup.setVisibility(View.GONE);
                    } else {
                        String text = contact.getSocialModel().getYoutubeLink();
                        if (text.contains("youtu.be/") || text.contains("watch?v=")) {
                            if (text.contains("youtu.be/"))
                                text = text.substring(text.indexOf("youtu.be/") + 9);
                            else if (text.contains("watch?v="))
                                text = text.substring(text.indexOf("watch?v=") + 8);

                            Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + text));

                            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("http://www.youtube.com/watch?v=" + text));

                            try {
                                getContext().startActivity(appIntent);
                            } catch (ActivityNotFoundException ex) {
                                getContext().startActivity(webIntent);
                            }
                        } else if (text.contains("user/") || text.contains("channel/") || text.contains("/c/")) {
                            try {
                                getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(text)));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }


                    }


                }
            });
        }

        if (Vkontakte != null) {
            Vkontakte.findViewById(R.id.vk_icon).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {


                    ImageView cImg = ((ImageView) Vkontakte.findViewById(R.id.vk_icon));
                    //    ImageView imageView = ((ImageView) profilePopUp.findViewById(R.id.imagePreviewIcon));
                    //  FrameLayout frameLayout = ((FrameLayout) mainView.findViewById(R.id.company_profile_block));
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN: {

                            if (listExist.contains(SocEnum.VK) && checkClick_vk) {
                                checkClick_vk = false;
                                int colorFrom = Color.parseColor("#507299");
                                int colorTo = Color.parseColor("#F9A825");
                                Drawable image = getResources().getDrawable(R.drawable.icn_social_vk2);
                                AnimColorMessenger.ActionDown_(colorFrom, colorTo, image, cImg);
                            } else {
                                checkClick_vk = false;
                                int colorFrom = Color.parseColor("#e2e5e8");
                                int colorTo = Color.parseColor("#F9A825");
                                Drawable image = getResources().getDrawable(R.drawable.icn_social_vk2);
                                AnimColorMessenger.ActionDown_(colorFrom, colorTo, image, cImg);
                            }

                            //     OnTouchMethod(textView);
                            break;
                        }
                        case MotionEvent.ACTION_UP: {
                            if (!checkClick_vk) {
                                if (listExist.contains(SocEnum.VK)) {
                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#507299");
                                    Drawable image = getResources().getDrawable(R.drawable.icn_social_vk2);
                                    AnimColorMessenger.ActionUp_(colorFrom, colorTo, image, cImg);
                                } else {
                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#e2e5e8");
                                    Drawable image = getResources().getDrawable(R.drawable.icn_social_vk2);
                                    AnimColorMessenger.ActionUp_(colorFrom, colorTo, image, cImg);
                                }
                            }
                            break;
                        }
                        case MotionEvent.ACTION_CANCEL: {
                            if (listExist.contains(SocEnum.VK)) {
                                int colorFrom = Color.parseColor("#F9A825");
                                int colorTo = Color.parseColor("#507299");
                                Drawable image = getResources().getDrawable(R.drawable.icn_social_vk2);
                                AnimColorMessenger.ActionCancel_(colorFrom, colorTo, image, cImg);
                            } else {
                                int colorFrom = Color.parseColor("#F9A825");
                                int colorTo = Color.parseColor("#e2e5e8");
                                Drawable image = getResources().getDrawable(R.drawable.icn_social_vk2);
                                AnimColorMessenger.ActionCancel_(colorFrom, colorTo, image, cImg);
                            }
                            break;
                        }
                        case MotionEvent.ACTION_MOVE: {
                            if (!checkClick_vk) {
                                if (listExist.contains(SocEnum.VK)) {
                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#507299");
                                    Drawable image = getResources().getDrawable(R.drawable.icn_social_vk2);
                                    checkClick_vk = AnimColorMessenger.ActionMove_(colorFrom, colorTo, image, cImg, motionEvent, Vkontakte);
                                } else {
                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#e2e5e8");
                                    Drawable image = getResources().getDrawable(R.drawable.icn_social_vk2);
                                    checkClick_vk = AnimColorMessenger.ActionMove_(colorFrom, colorTo, image, cImg, motionEvent, Vkontakte);
                                }
                            }

                            break;
                        }
                    }

                    return false;
                }
            });

            Vkontakte.findViewById(R.id.vk_icon).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if (contact.getSocialModel() == null || contact.getSocialModel().getVkLink() == null || contact.getSocialModel().getVkLink().isEmpty()) {
                        //    closeOtherPopup();
                        closeChildPopups();
                        showSocialPopup(contact);
                        socialPopup.findViewById(R.id.vk_link).callOnClick();
                        socialPopup.setVisibility(View.GONE);
                    } else {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format(contact.getSocialModel().getVkLink())));
                        Intent intent2 = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("vkontakte://profile/")));
                        try {
                            startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            e.printStackTrace();
                            PackageManager packageManager = getActivity().getPackageManager();
                            List<ResolveInfo> activities = packageManager.queryIntentActivities(intent2, 0);
                            boolean isIntentSafe = activities.size() > 0;
                            if (!isIntentSafe) {
                                /*Intent intent3 = new Intent(Intent.ACTION_VIEW);
                                intent3.setData(Uri.parse("market://details?id=com.vkontakte.android"));
                                startActivity(intent3);*/
                                try {
                                    String uris = contact.getSocialModel().getVkLink();
                                    if (!contact.getSocialModel().getVkLink().contains("https://") && !contact.getSocialModel().getVkLink().contains("http://"))
                                        uris = "https://" + uris;

                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                    i.setData(Uri.parse(uris));
                                    startActivity(i);
                                } catch (Exception e2) {

                                }
                            } else
                                startActivity(intent2);
                        }
                    }


                }
            });
        }


        if (Medium != null) {
            Medium.findViewById(R.id.medium_icon).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {


                    ImageView cImg = ((ImageView) Medium.findViewById(R.id.medium_icon));
                    //    ImageView imageView = ((ImageView) profilePopUp.findViewById(R.id.imagePreviewIcon));
                    //  FrameLayout frameLayout = ((FrameLayout) mainView.findViewById(R.id.company_profile_block));
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN: {

                            if (listExist.contains(SocEnum.MEDIUM) && checkClick_medium) {
                                checkClick_medium = false;
                                int colorFrom = Color.parseColor("#000000");
                                int colorTo = Color.parseColor("#F9A825");
                                Drawable image = getResources().getDrawable(R.drawable.medium_white);
                                AnimColorMessenger.ActionDown_(colorFrom, colorTo, image, cImg);
                            } else {
                                checkClick_medium = false;
                                int colorFrom = Color.parseColor("#e2e5e8");
                                int colorTo = Color.parseColor("#F9A825");
                                Drawable image = getResources().getDrawable(R.drawable.medium_white);
                                AnimColorMessenger.ActionDown_(colorFrom, colorTo, image, cImg);
                            }

                            //     OnTouchMethod(textView);
                            break;
                        }
                        case MotionEvent.ACTION_UP: {
                            if (!checkClick_medium) {
                                if (listExist.contains(SocEnum.MEDIUM)) {
                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#000000");
                                    Drawable image = getResources().getDrawable(R.drawable.medium_white);
                                    AnimColorMessenger.ActionUp_(colorFrom, colorTo, image, cImg);
                                } else {
                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#e2e5e8");
                                    Drawable image = getResources().getDrawable(R.drawable.medium_white);
                                    AnimColorMessenger.ActionUp_(colorFrom, colorTo, image, cImg);
                                }
                            }
                            break;
                        }
                        case MotionEvent.ACTION_CANCEL: {
                            if (listExist.contains(SocEnum.MEDIUM)) {
                                int colorFrom = Color.parseColor("#F9A825");
                                int colorTo = Color.parseColor("#000000");
                                Drawable image = getResources().getDrawable(R.drawable.medium_white);
                                AnimColorMessenger.ActionCancel_(colorFrom, colorTo, image, cImg);
                            } else {
                                int colorFrom = Color.parseColor("#F9A825");
                                int colorTo = Color.parseColor("#e2e5e8");
                                Drawable image = getResources().getDrawable(R.drawable.medium_white);
                                AnimColorMessenger.ActionCancel_(colorFrom, colorTo, image, cImg);
                            }
                            break;
                        }
                        case MotionEvent.ACTION_MOVE: {
                            if (!checkClick_medium) {
                                if (listExist.contains(SocEnum.MEDIUM)) {
                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#000000");
                                    Drawable image = getResources().getDrawable(R.drawable.medium_white);
                                    checkClick_medium = AnimColorMessenger.ActionMove_(colorFrom, colorTo, image, cImg, motionEvent, Medium);
                                } else {
                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#e2e5e8");
                                    Drawable image = getResources().getDrawable(R.drawable.medium_white);
                                    checkClick_medium = AnimColorMessenger.ActionMove_(colorFrom, colorTo, image, cImg, motionEvent, Medium);
                                }
                            }

                            break;
                        }
                    }

                    return false;
                }
            });

            Medium.findViewById(R.id.medium_icon).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if (contact.getSocialModel() == null || contact.getSocialModel().getMediumLink() == null || contact.getSocialModel().getMediumLink().isEmpty()) {
                        //    closeOtherPopup();
                        closeChildPopups();
                        showSocialPopup(contact);
                        socialPopup.findViewById(R.id.medium_link).callOnClick();
                        socialPopup.setVisibility(View.GONE);
                    } else {
                        /*Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format(contact.getSocialModel().getVkLink())));
                        Intent intent2 = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("vkontakte://profile/")));
                        try {
                            startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            e.printStackTrace();
                            PackageManager packageManager = getActivity().getPackageManager();
                            List<ResolveInfo> activities = packageManager.queryIntentActivities(intent2, 0);
                            boolean isIntentSafe = activities.size() > 0;
                            if (!isIntentSafe) {
                                *//*Intent intent3 = new Intent(Intent.ACTION_VIEW);
                                intent3.setData(Uri.parse("market://details?id=com.vkontakte.android"));
                                startActivity(intent3);*//*
                                try {
                                    String uris = contact.getSocialModel().getVkLink();
                                    if (!contact.getSocialModel().getVkLink().contains("https://") && !contact.getSocialModel().getVkLink().contains("http://"))
                                        uris = "https://" + uris;

                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                    i.setData(Uri.parse(uris));
                                    startActivity(i);
                                } catch (Exception e2) {

                                }
                            } else
                                startActivity(intent2);
                        }*/

                        try {

                            String uri = contact.getSocialModel().getMediumLink();
                            if (!uri.contains("https://") && !uri.contains("http://"))
                                uri = "https://" + uri;

                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(uri));
                            startActivity(i);
                        } catch (Exception e) {

                        }
                    }


                }
            });
        }


        //linearLayout.addView(v);


        View vi = view;

        if (contact.hasWhatsapp && contact.getSocialModel() != null && contact.getSocialModel().getWhatsappLink() != null && !contact.getSocialModel().getWhatsappLink().isEmpty()) {
            Drawable color = new ColorDrawable(Color.parseColor("#75B73B"));
            Drawable image = getResources().getDrawable(R.drawable.icn_social_whatsapp3);
            LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
            ((CircleImageView) view.findViewById(R.id.whatsapp_icon)).setImageDrawable(ld);
        } else {
            Drawable color = new ColorDrawable(Color.parseColor("#e2e5e8"));
            Drawable image = getResources().getDrawable(R.drawable.icn_social_whatsapp3_gray);
            LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
            ((CircleImageView) view.findViewById(R.id.whatsapp_icon)).setImageDrawable(ld);
        }
        // 48BB5F

        view.findViewById(R.id.whatsapp_icon).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {


                CircleImageView cImg = ((CircleImageView) vi.findViewById(R.id.whatsapp_icon));
                //    ImageView imageView = ((ImageView) profilePopUp.findViewById(R.id.imagePreviewIcon));
                //  FrameLayout frameLayout = ((FrameLayout) mainView.findViewById(R.id.company_profile_block));
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        checkClick = false;
                        if (contact.hasWhatsapp) {
                            int colorFrom = Color.parseColor("#75B73B");
                            int colorTo = Color.parseColor("#F9A825");
                            Drawable image = getResources().getDrawable(R.drawable.icn_social_whatsapp3);
                            AnimColorMessenger.ActionDown(colorFrom, colorTo, image, cImg);
                        } else {
                            int colorFrom = Color.parseColor("#e2e5e8");
                            int colorTo = Color.parseColor("#F9A825");
                            Drawable image = getResources().getDrawable(R.drawable.icn_social_whatsapp3_gray);
                            AnimColorMessenger.ActionDown(colorFrom, colorTo, image, cImg);
                        }
                        //     OnTouchMethod(textView);
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        if (!checkClick) {
                            if (contact.hasWhatsapp) {
                                int colorFrom = Color.parseColor("#F9A825");
                                int colorTo = Color.parseColor("#75B73B");
                                Drawable image = getResources().getDrawable(R.drawable.icn_social_whatsapp3);
                                AnimColorMessenger.ActionUp(colorFrom, colorTo, image, cImg);
                            } else {
                                int colorFrom = Color.parseColor("#F9A825");
                                int colorTo = Color.parseColor("#e2e5e8");
                                Drawable image = getResources().getDrawable(R.drawable.icn_social_whatsapp3_gray);
                                AnimColorMessenger.ActionUp(colorFrom, colorTo, image, cImg);
                            }
                        }
                        break;
                    }
                    case MotionEvent.ACTION_CANCEL: {
                        if (contact.hasWhatsapp) {
                            int colorFrom = Color.parseColor("#F9A825");
                            int colorTo = Color.parseColor("#75B73B");
                            Drawable image = getResources().getDrawable(R.drawable.icn_social_whatsapp3);
                            AnimColorMessenger.ActionCancel(colorFrom, colorTo, image, cImg);
                        } else {
                            int colorFrom = Color.parseColor("#F9A825");
                            int colorTo = Color.parseColor("#e2e5e8");
                            Drawable image = getResources().getDrawable(R.drawable.icn_social_whatsapp3_gray);
                            AnimColorMessenger.ActionCancel(colorFrom, colorTo, image, cImg);
                        }
                        break;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        if (!checkClick) {
                            if (contact.hasWhatsapp) {
                                int colorFrom = Color.parseColor("#F9A825");
                                int colorTo = Color.parseColor("#75B73B");
                                Drawable image = getResources().getDrawable(R.drawable.icn_social_whatsapp3);
                                checkClick = AnimColorMessenger.ActionMove(colorFrom, colorTo, image, cImg, motionEvent, mainView);
                            } else {
                                int colorFrom = Color.parseColor("#F9A825");
                                int colorTo = Color.parseColor("#e2e5e8");
                                Drawable image = getResources().getDrawable(R.drawable.icn_social_whatsapp3_gray);
                                checkClick = AnimColorMessenger.ActionMove(colorFrom, colorTo, image, cImg, motionEvent, mainView);
                            }
                        }

                        break;
                    }
                }

                return false;
            }
        });


        view.findViewById(R.id.whatsapp_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!contact.hasWhatsapp || contact.getSocialModel() == null || contact.getSocialModel().getWhatsappLink() == null || contact.getSocialModel().getWhatsappLink().isEmpty()) {
                    //  closeOtherPopup();
                    closeChildPopups();
                    showSocialPopup(contact);
                    socialPopup.findViewById(R.id.whatsapp_link).callOnClick();
                    socialPopup.setVisibility(View.GONE);
                } else {
                    Intent telegramIntent = new Intent(Intent.ACTION_VIEW);

                    String whatsappNum = contact.getSocialModel().getWhatsappLink();
                    if (/*whatsappNum.substring(0, 2).equalsIgnoreCase("+8") ||*/ whatsappNum.charAt(0) == '8') {
                        whatsappNum = whatsappNum.replaceFirst("8", "7");
                    }

                    if (whatsappNum.charAt(0) != '+') whatsappNum = "+" + whatsappNum;

                    telegramIntent.setData(Uri.parse("whatsapp://send?phone=" + whatsappNum));
                    PackageManager packageManager = getActivity().getPackageManager();
                    List<ResolveInfo> activities = packageManager.queryIntentActivities(telegramIntent, 0);
                    boolean isIntentSafe = activities.size() > 0;
                    if (!isIntentSafe) {
                        Intent intent2 = new Intent(Intent.ACTION_VIEW);
                        intent2.setData(Uri.parse("market://details?id=com.whatsapp"));
                        startActivity(intent2);
                    } else
                        startActivity(telegramIntent);
                }
            }
        });


        if ((contact.hasTelegram && contact.getSocialModel() != null && contact.getSocialModel().getTelegramLink() != null)) {
            Drawable color = new ColorDrawable(Color.parseColor("#7AA5DA"));
            Drawable image = getResources().getDrawable(R.drawable.icn_social_telegram2);
            LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
            ((CircleImageView) view.findViewById(R.id.telegram_icon)).setImageDrawable(ld);
        } else {
            Drawable color = new ColorDrawable(Color.parseColor("#e2e5e8"));
            Drawable image = getResources().getDrawable(R.drawable.icn_social_telegram2);
            LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
            ((CircleImageView) view.findViewById(R.id.telegram_icon)).setImageDrawable(ld);
        }


        view.findViewById(R.id.telegram_icon).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {


                CircleImageView cImg = ((CircleImageView) vi.findViewById(R.id.telegram_icon));
                //    ImageView imageView = ((ImageView) profilePopUp.findViewById(R.id.imagePreviewIcon));
                //  FrameLayout frameLayout = ((FrameLayout) mainView.findViewById(R.id.company_profile_block));
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        checkClick_telegram = false;
                        if ((contact.hasTelegram && contact.getSocialModel() != null && contact.getSocialModel().getTelegramLink() != null)) {
                            int colorFrom = Color.parseColor("#7AA5DA");
                            int colorTo = Color.parseColor("#F9A825");
                            Drawable image = getResources().getDrawable(R.drawable.icn_social_telegram2);
                            AnimColorMessenger.ActionDown(colorFrom, colorTo, image, cImg);
                        } else {
                            int colorFrom = Color.parseColor("#e2e5e8");
                            int colorTo = Color.parseColor("#F9A825");
                            Drawable image = getResources().getDrawable(R.drawable.icn_social_telegram2);
                            AnimColorMessenger.ActionDown(colorFrom, colorTo, image, cImg);
                        }
                        //     OnTouchMethod(textView);
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        if (!checkClick_telegram) {
                            if ((contact.hasTelegram && contact.getSocialModel() != null && contact.getSocialModel().getTelegramLink() != null)) {
                                int colorFrom = Color.parseColor("#F9A825");
                                int colorTo = Color.parseColor("#7AA5DA");
                                Drawable image = getResources().getDrawable(R.drawable.icn_social_telegram2);
                                AnimColorMessenger.ActionUp(colorFrom, colorTo, image, cImg);
                            } else {
                                int colorFrom = Color.parseColor("#F9A825");
                                int colorTo = Color.parseColor("#e2e5e8");
                                Drawable image = getResources().getDrawable(R.drawable.icn_social_telegram2);
                                AnimColorMessenger.ActionUp(colorFrom, colorTo, image, cImg);
                            }
                        }
                        break;
                    }
                    case MotionEvent.ACTION_CANCEL: {
                        if ((contact.hasTelegram && contact.getSocialModel() != null && contact.getSocialModel().getTelegramLink() != null)) {
                            int colorFrom = Color.parseColor("#F9A825");
                            int colorTo = Color.parseColor("#7AA5DA");
                            Drawable image = getResources().getDrawable(R.drawable.icn_social_telegram2);
                            AnimColorMessenger.ActionCancel(colorFrom, colorTo, image, cImg);
                        } else {
                            int colorFrom = Color.parseColor("#F9A825");
                            int colorTo = Color.parseColor("#e2e5e8");
                            Drawable image = getResources().getDrawable(R.drawable.icn_social_telegram2);
                            AnimColorMessenger.ActionCancel(colorFrom, colorTo, image, cImg);
                        }
                        break;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        if (!checkClick_telegram) {
                            if ((contact.hasTelegram && contact.getSocialModel() != null && contact.getSocialModel().getTelegramLink() != null)) {
                                int colorFrom = Color.parseColor("#F9A825");
                                int colorTo = Color.parseColor("#7AA5DA");
                                Drawable image = getResources().getDrawable(R.drawable.icn_social_telegram2);
                                checkClick_telegram = AnimColorMessenger.ActionMove(colorFrom, colorTo, image, cImg, motionEvent, mainView);
                            } else {
                                int colorFrom = Color.parseColor("#F9A825");
                                int colorTo = Color.parseColor("#e2e5e8");
                                Drawable image = getResources().getDrawable(R.drawable.icn_social_telegram2);
                                checkClick_telegram = AnimColorMessenger.ActionMove(colorFrom, colorTo, image, cImg, motionEvent, mainView);
                            }
                        }

                        break;
                    }
                }

                return false;
            }
        });

        view.findViewById(R.id.telegram_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ((!contact.hasTelegram) || (contact.hasTelegram && contact.getSocialModel() == null) || (contact.hasTelegram && contact.getSocialModel() != null && contact.getSocialModel().getTelegramLink() == null)) {
                    //  closeOtherPopup();
                    closeChildPopups();
                    showSocialPopup(contact);
                    socialPopup.findViewById(R.id.telegramLink).callOnClick();
                    socialPopup.setVisibility(View.GONE);
                } else {

                    String username = contact.getSocialModel().getTelegramLink();
                    char firstSymbol = username.charAt(0);
                    String regex = "[0-9]+";
                    username = username.replaceAll("[-() ]", "");
                    if ((firstSymbol == '+' && username.substring(1).matches(regex)) || (firstSymbol != '+' && username.matches(regex))) {
                        //final String contactId = getContactIdFromPhoneNumber(String.valueOf(contact.getSocialModel().getTelegramLink()));
                        final String contactId = contact.getIdContact();
                        final String contactMimeTypeDataId = getContactMimeTypeDataId(getContext(), contactId, "vnd.android.cursor.item/vnd.org.telegram.messenger.android.profile");
                        if (contactMimeTypeDataId != null) {
                            Intent intent;
                            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("content://com.android.contacts/data/" + contactMimeTypeDataId));
                            intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET | Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
                            intent.setPackage("org.telegram.messenger");
                            startActivity(intent);
                        } else {
                            Intent telegramIntent = new Intent(Intent.ACTION_VIEW);
                            telegramIntent.setData(Uri.parse("tg://resolve?domain=" + username));
                            PackageManager packageManager = getActivity().getPackageManager();
                            List<ResolveInfo> activities = packageManager.queryIntentActivities(telegramIntent, 0);
                            boolean isIntentSafe = activities.size() > 0;
                            if (!isIntentSafe) {
                                Intent intent3 = new Intent(Intent.ACTION_VIEW);
                                intent3.setData(Uri.parse("market://details?id=org.telegram.messenger"));
                                startActivity(intent3);
                            } else
                                startActivity(telegramIntent);

                        }

                    } else if ((firstSymbol == '@' && !username.substring(1).matches(regex)) || (firstSymbol != '@' && !username.matches(regex))) {
                        Intent telegramIntent = new Intent(Intent.ACTION_VIEW);

                        if (firstSymbol == '@')
                            username = username.substring(1);
                        else if (username.contains("t.me/") && !username.contains("@"))
                            username = username.substring(5);
                        else if (username.contains("t.me/@"))
                            username = username.substring(6);

                        telegramIntent.setData(Uri.parse("tg://resolve?domain=" + username));
                        PackageManager packageManager = getActivity().getPackageManager();
                        List<ResolveInfo> activities = packageManager.queryIntentActivities(telegramIntent, 0);
                        boolean isIntentSafe = activities.size() > 0;
                        if (!isIntentSafe) {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse("market://details?id=org.telegram.messenger"));
                            startActivity(intent);
                        } else
                            startActivity(telegramIntent);
                    }



                }
            }
        });


        if (contact.hasViber && contact.getSocialModel() != null && contact.getSocialModel().getViberLink() != null && !contact.getSocialModel().getViberLink().isEmpty()) {
            Drawable color = new ColorDrawable(Color.parseColor("#6F3FAA"));
            Drawable image = getResources().getDrawable(R.drawable.icn_social_viber2);
            LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
            ((CircleImageView) view.findViewById(R.id.viber_icon)).setImageDrawable(ld);
        } else {
            Drawable color = new ColorDrawable(Color.parseColor("#e2e5e8"));
            Drawable image = getResources().getDrawable(R.drawable.icn_social_viber2_gray);
            LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
            ((CircleImageView) view.findViewById(R.id.viber_icon)).setImageDrawable(ld);
        }


        view.findViewById(R.id.viber_icon).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {


                CircleImageView cImg = ((CircleImageView) vi.findViewById(R.id.viber_icon));
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {

                        checkClick_viber = false;
                        if (contact.hasViber) {

                            int colorFrom = Color.parseColor("#6F3FAA");
                            int colorTo = Color.parseColor("#F9A825");

                            Drawable image = getResources().getDrawable(R.drawable.icn_social_viber2);

                            AnimColorMessenger.ActionDown(colorFrom, colorTo, image, cImg);
                        } else {
                            int colorFrom = Color.parseColor("#e2e5e8");
                            int colorTo = Color.parseColor("#F9A825");
                            Drawable image = getResources().getDrawable(R.drawable.icn_social_viber2_gray);
                            AnimColorMessenger.ActionDown(colorFrom, colorTo, image, cImg);
                        }
                        //     OnTouchMethod(textView);
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        if (!checkClick_viber) {
                            if (contact.hasViber) {
                                int colorFrom = Color.parseColor("#F9A825");
                                int colorTo = Color.parseColor("#6F3FAA");
                                Drawable image = getResources().getDrawable(R.drawable.icn_social_viber2);
                                AnimColorMessenger.ActionUp(colorFrom, colorTo, image, cImg);
                            } else {
                                int colorFrom = Color.parseColor("#F9A825");
                                int colorTo = Color.parseColor("#e2e5e8");
                                Drawable image = getResources().getDrawable(R.drawable.icn_social_viber2_gray);
                                AnimColorMessenger.ActionUp(colorFrom, colorTo, image, cImg);
                            }
                        }
                        break;
                    }
                    case MotionEvent.ACTION_CANCEL: {
                        if (contact.hasViber) {
                            int colorFrom = Color.parseColor("#F9A825");
                            int colorTo = Color.parseColor("#6F3FAA");
                            Drawable image = getResources().getDrawable(R.drawable.icn_social_viber2);
                            AnimColorMessenger.ActionCancel(colorFrom, colorTo, image, cImg);
                        } else {
                            int colorFrom = Color.parseColor("#F9A825");
                            int colorTo = Color.parseColor("#e2e5e8");
                            Drawable image = getResources().getDrawable(R.drawable.icn_social_viber2_gray);
                            AnimColorMessenger.ActionCancel(colorFrom, colorTo, image, cImg);
                        }
                        break;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        if (!checkClick_viber) {
                            if (contact.hasViber) {
                                int colorFrom = Color.parseColor("#F9A825");
                                int colorTo = Color.parseColor("#6F3FAA");
                                Drawable image = getResources().getDrawable(R.drawable.icn_social_viber2);
                                checkClick_viber = AnimColorMessenger.ActionMove(colorFrom, colorTo, image, cImg, motionEvent, mainView);
                            } else {
                                int colorFrom = Color.parseColor("#F9A825");
                                int colorTo = Color.parseColor("#e2e5e8");
                                Drawable image = getResources().getDrawable(R.drawable.icn_social_viber2_gray);
                                checkClick_viber = AnimColorMessenger.ActionMove(colorFrom, colorTo, image, cImg, motionEvent, mainView);
                            }
                        }

                        break;
                    }
                }

                return false;
            }
        });

        view.findViewById(R.id.viber_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!contact.hasViber || contact.getSocialModel() == null || contact.getSocialModel().getViberLink() == null || contact.getSocialModel().getViberLink().isEmpty()) {
                    //   closeOtherPopup();
                    closeChildPopups();
                    showSocialPopup(contact);
                    socialPopup.findViewById(R.id.viberLink).callOnClick();
                    socialPopup.setVisibility(View.GONE);
                } else {
                    Intent intent;
                    final String contactId = getContactIdFromPhoneNumber(String.valueOf(contact.getSocialModel().getViberLink()));
                    final String contactMimeTypeDataId = getContactMimeTypeDataId(getContext(), contactId, "vnd.android.cursor.item/vnd.com.viber.voip.viber_number_message");
                    if (contactMimeTypeDataId != null) {
                        intent = new Intent(Intent.ACTION_VIEW, Uri.parse("content://com.android.contacts/data/" + contactMimeTypeDataId));
                        intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET | Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
                        intent.setPackage("com.viber.voip");
                    } else {
                        intent = new Intent("android.intent.action.VIEW", Uri.parse("tel:" + Uri.encode(String.valueOf(contact.getSocialModel().getViberLink()))));
                        intent.setClassName("com.viber.voip", "com.viber.voip.WelcomeActivity");
                    }

                    PackageManager packageManager = getActivity().getPackageManager();
                    List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
                    boolean isIntentSafe = activities.size() > 0;
                    if (!isIntentSafe) {
                        Intent intent2 = new Intent(Intent.ACTION_VIEW);
                        intent2.setData(Uri.parse("market://details?id=com.viber.voip"));
                        startActivity(intent2);
                    } else
                        startActivity(intent);
                }
            }
        });


        if (contact.hasSkype && contact.getSocialModel() != null && contact.getSocialModel().getSkypeLink() != null && !contact.getSocialModel().getSkypeLink().isEmpty()) {
            Drawable color = new ColorDrawable(Color.parseColor("#1eb8ff"));
            Drawable image = getResources().getDrawable(R.drawable.icn_social_skype2);
            LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
            ((CircleImageView) view.findViewById(R.id.skype_icon)).setImageDrawable(ld);
        } else {
            Drawable color = new ColorDrawable(Color.parseColor("#e2e5e8"));
            Drawable image = getResources().getDrawable(R.drawable.icn_social_skype2_gray);
            LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
            ((CircleImageView) view.findViewById(R.id.skype_icon)).setImageDrawable(ld);
        }


        view.findViewById(R.id.skype_icon).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                CircleImageView cImg = ((CircleImageView) vi.findViewById(R.id.skype_icon));
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        checkClick_skype = false;
                        if (contact.hasSkype && contact.getSocialModel() != null && contact.getSocialModel().getSkypeLink() != null && !contact.getSocialModel().getSkypeLink().isEmpty()) {
                            int colorFrom = Color.parseColor("#1eb8ff");
                            int colorTo = Color.parseColor("#F9A825");
                            Drawable image = getResources().getDrawable(R.drawable.icn_social_skype2);
                            AnimColorMessenger.ActionDown(colorFrom, colorTo, image, cImg);
                        } else {
                            int colorFrom = Color.parseColor("#e2e5e8");
                            int colorTo = Color.parseColor("#F9A825");
                            Drawable image = getResources().getDrawable(R.drawable.icn_social_skype2_gray);
                            AnimColorMessenger.ActionDown(colorFrom, colorTo, image, cImg);
                        }
                        //     OnTouchMethod(textView);
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        if (!checkClick_skype) {
                            if (contact.hasSkype && contact.getSocialModel() != null && contact.getSocialModel().getSkypeLink() != null && !contact.getSocialModel().getSkypeLink().isEmpty()) {
                                int colorFrom = Color.parseColor("#F9A825");
                                int colorTo = Color.parseColor("#1eb8ff");
                                Drawable image = getResources().getDrawable(R.drawable.icn_social_skype2);
                                AnimColorMessenger.ActionUp(colorFrom, colorTo, image, cImg);
                            } else {
                                int colorFrom = Color.parseColor("#F9A825");
                                int colorTo = Color.parseColor("#e2e5e8");
                                Drawable image = getResources().getDrawable(R.drawable.icn_social_skype2_gray);
                                AnimColorMessenger.ActionUp(colorFrom, colorTo, image, cImg);
                            }
                        }
                        break;
                    }
                    case MotionEvent.ACTION_CANCEL: {
                        if (contact.hasSkype && contact.getSocialModel() != null && contact.getSocialModel().getSkypeLink() != null && !contact.getSocialModel().getSkypeLink().isEmpty()) {
                            int colorFrom = Color.parseColor("#F9A825");
                            int colorTo = Color.parseColor("#1eb8ff");
                            Drawable image = getResources().getDrawable(R.drawable.icn_social_skype2);
                            AnimColorMessenger.ActionCancel(colorFrom, colorTo, image, cImg);
                        } else {
                            int colorFrom = Color.parseColor("#F9A825");
                            int colorTo = Color.parseColor("#e2e5e8");
                            Drawable image = getResources().getDrawable(R.drawable.icn_social_skype2_gray);
                            AnimColorMessenger.ActionCancel(colorFrom, colorTo, image, cImg);
                        }
                        break;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        if (!checkClick_skype) {
                            if (contact.hasSkype && contact.getSocialModel() != null && contact.getSocialModel().getSkypeLink() != null && !contact.getSocialModel().getSkypeLink().isEmpty()) {
                                int colorFrom = Color.parseColor("#F9A825");
                                int colorTo = Color.parseColor("#1eb8ff");
                                Drawable image = getResources().getDrawable(R.drawable.icn_social_skype2);
                                checkClick_skype = AnimColorMessenger.ActionMove(colorFrom, colorTo, image, cImg, motionEvent, mainView);
                            } else {
                                int colorFrom = Color.parseColor("#F9A825");
                                int colorTo = Color.parseColor("#e2e5e8");
                                Drawable image = getResources().getDrawable(R.drawable.icn_social_skype2_gray);
                                checkClick_skype = AnimColorMessenger.ActionMove(colorFrom, colorTo, image, cImg, motionEvent, mainView);
                            }
                        }

                        break;
                    }
                }

                return false;
            }
        });


        view.findViewById(R.id.skype_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!contact.hasSkype || contact.getSocialModel() == null || contact.getSocialModel().getSkypeLink() == null || contact.getSocialModel().getSkypeLink().isEmpty()) {
                    //   closeOtherPopup();
                    closeChildPopups();
                    showSocialPopup(contact);
                    socialPopup.findViewById(R.id.skypeLink).callOnClick();
                    socialPopup.setVisibility(View.GONE);
                } else {
                    Uri skypeUri = Uri.parse("skype:" + contact.getSocialModel().getSkypeLink() + "?chat");
                    Intent myIntent = new Intent(Intent.ACTION_VIEW, skypeUri);
                    myIntent.setComponent(new ComponentName("com.skype.raider", "com.skype.raider.Main"));
                    myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    PackageManager packageManager = getActivity().getPackageManager();
                    List<ResolveInfo> activities = packageManager.queryIntentActivities(myIntent, 0);
                    boolean isIntentSafe = activities.size() > 0;
                    if (!isIntentSafe) {
                        Intent intent2 = new Intent(Intent.ACTION_VIEW);
                        intent2.setData(Uri.parse("market://details?id=com.skype.raider"));
                        startActivity(intent2);
                    } else
                        startActivity(myIntent);
                }
            }
        });


        System.out.println("initicon 1");
    }

    public void showEditPopupPreviewSocial(Contact contact, SocialEnums typeEnum) {
        popupProfileEditPreviewSocial = (FrameLayout) getActivity().findViewById(R.id.popupPreviewEditSocial);

        popupProfileEditPreviewSocial.findViewById(R.id.updateContact).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupEditSocial = (FrameLayout) getActivity().findViewById(R.id.popupEditSocial);
                popupEditSocial.setVisibility(View.VISIBLE);
                popupEditSocial.setClickable(true);
                popupEditSocial.findViewById(R.id.ok_social).setVisibility(View.VISIBLE);
                popupEditSocial.findViewById(R.id.get_last_clips).setVisibility(View.GONE);
                popupEditSocial.findViewById(R.id.imageEditSocial).setVisibility(View.GONE);
                popupEditSocial.findViewById(R.id.searchSocial).setVisibility(View.GONE);
                popupEditSocial.findViewById(R.id.frameImageEditSocialInsta).setVisibility(View.GONE);

                if (typeEnum.equals(SocialEnums.FACEBOOK)) {
                    ((TextView) popupEditSocial.findViewById(R.id.typeField)).setText("Facebook");
                    ((EditText) popupEditSocial.findViewById(R.id.dataToEdit)).setText(contact.getSocialModel().getFacebookLink());
                    ((ImageView) popupEditSocial.findViewById(R.id.iconDataEdit)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_social_facebook));
                } else if (typeEnum.equals(SocialEnums.VK)) {
                    ((TextView) popupEditSocial.findViewById(R.id.typeField)).setText("Vk");
                    ((EditText) popupEditSocial.findViewById(R.id.dataToEdit)).setText(contact.getSocialModel().getVkLink());
                    ((ImageView) popupEditSocial.findViewById(R.id.iconDataEdit)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_social_vk));
                } else if (typeEnum.equals(SocialEnums.INSTAGRAM)) {
                    ((TextView) popupEditSocial.findViewById(R.id.typeField)).setText("Instagram");
                    ((EditText) popupEditSocial.findViewById(R.id.dataToEdit)).setText(contact.getSocialModel().getInstagramLink());
                    ((ImageView) popupEditSocial.findViewById(R.id.iconDataEdit)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_social_instagram));
                } else if (typeEnum.equals(SocialEnums.LINKEDIN)) {
                    ((TextView) popupEditSocial.findViewById(R.id.typeField)).setText("LinkedIn");
                    ((EditText) popupEditSocial.findViewById(R.id.dataToEdit)).setText(contact.getSocialModel().getLinkedInLink());
                    ((ImageView) popupEditSocial.findViewById(R.id.iconDataEdit)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_social_linkedin));
                } else if (typeEnum.equals(SocialEnums.TELEGRAM)) {
                    ((TextView) popupEditSocial.findViewById(R.id.typeField)).setText("Telegram");
                    ((EditText) popupEditSocial.findViewById(R.id.dataToEdit)).setText(contact.getSocialModel().getTelegramLink());
                    ((ImageView) popupEditSocial.findViewById(R.id.iconDataEdit)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_social_telegram));
                } else if (typeEnum.equals(SocialEnums.SKYPE)) {
                    ((TextView) popupEditSocial.findViewById(R.id.typeField)).setText("Skype");
                    ((EditText) popupEditSocial.findViewById(R.id.dataToEdit)).setText(contact.getSocialModel().getSkypeLink());
                    ((ImageView) popupEditSocial.findViewById(R.id.iconDataEdit)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_social_skype));
                } else if (typeEnum.equals(SocialEnums.VIBER)) {
                    ((TextView) popupEditSocial.findViewById(R.id.typeField)).setText("Viber");
                    ((EditText) popupEditSocial.findViewById(R.id.dataToEdit)).setText(contact.getSocialModel().getViberLink());
                    ((ImageView) popupEditSocial.findViewById(R.id.iconDataEdit)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_social_viber));
                } else if (typeEnum.equals(SocialEnums.WHATSAPP)) {
                    ((TextView) popupEditSocial.findViewById(R.id.typeField)).setText("WhatsApp");
                    ((EditText) popupEditSocial.findViewById(R.id.dataToEdit)).setText(contact.getSocialModel().getWhatsappLink());
                    ((ImageView) popupEditSocial.findViewById(R.id.iconDataEdit)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_social_whatsapp));
                } else if (typeEnum.equals(SocialEnums.TWITTER)) {
                    ((TextView) popupEditSocial.findViewById(R.id.typeField)).setText("Twitter");
                    ((EditText) popupEditSocial.findViewById(R.id.dataToEdit)).setText(contact.getSocialModel().getTwitterLink());
                    ((ImageView) popupEditSocial.findViewById(R.id.iconDataEdit)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_twitter_48));
                } else if (typeEnum.equals(SocialEnums.YOUTUBE)) {
                    ((TextView) popupEditSocial.findViewById(R.id.typeField)).setText("Youtube");
                    ((EditText) popupEditSocial.findViewById(R.id.dataToEdit)).setText(contact.getSocialModel().getYoutubeLink());
                    ((ImageView) popupEditSocial.findViewById(R.id.iconDataEdit)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_youtube_48));
                } else if (typeEnum.equals(SocialEnums.MEDIUM)) {
                    ((TextView) popupEditSocial.findViewById(R.id.typeField)).setText("Medium");
                    ((EditText) popupEditSocial.findViewById(R.id.dataToEdit)).setText(contact.getSocialModel().getMediumLink());
                    ((ImageView) popupEditSocial.findViewById(R.id.iconDataEdit)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.medium_size_64));
                }

                ((TextView) popupEditSocial.findViewById(R.id.typeField)).requestLayout();

                openedViews.add(popupEditSocial);

                popupEditSocial.findViewById(R.id.cancelEditField).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        popupEditSocial.setVisibility(View.GONE);
                    }
                });
            }
        });

        popupProfileEditPreviewSocial.setVisibility(View.VISIBLE);
        popupProfileEditPreviewSocial.setClickable(true);

        //((ImageView)popupProfileEditPreviewSocial.findViewById(R.id.imageCall)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_popup_web_blue));
        openedViews.add(popupProfileEditPreviewSocial);
        ((TextView) popupProfileEditPreviewSocial.findViewById(R.id.valueUpd)).setMovementMethod(new ScrollingMovementMethod());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ((ImageView) popupProfileEditPreviewSocial.findViewById(R.id.imageCall)).setImageTintMode(null);
        }

        if (typeEnum.equals(SocialEnums.FACEBOOK)) {
            ((TextView) popupProfileEditPreviewSocial.findViewById(R.id.valueUpd)).setText(contact.getSocialModel().getFacebookLink());
            ((TextView) popupProfileEditPreviewSocial.findViewById(R.id.typeField)).setText("Facebook");
            ((ImageView) popupProfileEditPreviewSocial.findViewById(R.id.imageCall)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_social_facebook));
        } else if (typeEnum.equals(SocialEnums.INSTAGRAM)) {
            ((TextView) popupProfileEditPreviewSocial.findViewById(R.id.valueUpd)).setText(contact.getSocialModel().getInstagramLink());
            ((TextView) popupProfileEditPreviewSocial.findViewById(R.id.typeField)).setText("Instagram");
            ((ImageView) popupProfileEditPreviewSocial.findViewById(R.id.imageCall)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_social_instagram));
        } else if (typeEnum.equals(SocialEnums.VK)) {
            ((TextView) popupProfileEditPreviewSocial.findViewById(R.id.valueUpd)).setText(contact.getSocialModel().getVkLink());
            ((TextView) popupProfileEditPreviewSocial.findViewById(R.id.typeField)).setText("Vk");
            ((ImageView) popupProfileEditPreviewSocial.findViewById(R.id.imageCall)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_social_vk));
        } else if (typeEnum.equals(SocialEnums.LINKEDIN)) {
            ((TextView) popupProfileEditPreviewSocial.findViewById(R.id.valueUpd)).setText(contact.getSocialModel().getLinkedInLink());
            ((TextView) popupProfileEditPreviewSocial.findViewById(R.id.typeField)).setText("LinkedIn");
            ((ImageView) popupProfileEditPreviewSocial.findViewById(R.id.imageCall)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_social_linkedin));
        } else if (typeEnum.equals(SocialEnums.SKYPE)) {
            ((TextView) popupProfileEditPreviewSocial.findViewById(R.id.valueUpd)).setText(contact.getSocialModel().getSkypeLink());
            ((TextView) popupProfileEditPreviewSocial.findViewById(R.id.typeField)).setText("Skype");
            ((ImageView) popupProfileEditPreviewSocial.findViewById(R.id.imageCall)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_social_skype));
        } else if (typeEnum.equals(SocialEnums.TELEGRAM)) {
            ((TextView) popupProfileEditPreviewSocial.findViewById(R.id.valueUpd)).setText(contact.getSocialModel().getTelegramLink());
            ((TextView) popupProfileEditPreviewSocial.findViewById(R.id.typeField)).setText("Telegram");
            ((ImageView) popupProfileEditPreviewSocial.findViewById(R.id.imageCall)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_social_telegram));
        } else if (typeEnum.equals(SocialEnums.VIBER)) {
            ((TextView) popupProfileEditPreviewSocial.findViewById(R.id.valueUpd)).setText(contact.getSocialModel().getViberLink());
            ((TextView) popupProfileEditPreviewSocial.findViewById(R.id.typeField)).setText("Viber");
            ((ImageView) popupProfileEditPreviewSocial.findViewById(R.id.imageCall)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_social_viber));
        } else if (typeEnum.equals(SocialEnums.WHATSAPP)) {
            ((TextView) popupProfileEditPreviewSocial.findViewById(R.id.valueUpd)).setText(contact.getSocialModel().getWhatsappLink());
            ((TextView) popupProfileEditPreviewSocial.findViewById(R.id.typeField)).setText("WhatsApp");
            ((ImageView) popupProfileEditPreviewSocial.findViewById(R.id.imageCall)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_social_whatsapp));
        } else if (typeEnum.equals(SocialEnums.TWITTER)) {
            ((TextView) popupProfileEditPreviewSocial.findViewById(R.id.valueUpd)).setText(contact.getSocialModel().getTwitterLink());
            ((TextView) popupProfileEditPreviewSocial.findViewById(R.id.typeField)).setText("Twitter");
            ((ImageView) popupProfileEditPreviewSocial.findViewById(R.id.imageCall)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_twitter_64));
        } else if (typeEnum.equals(SocialEnums.YOUTUBE)) {
            ((TextView) popupProfileEditPreviewSocial.findViewById(R.id.valueUpd)).setText(contact.getSocialModel().getYoutubeLink());
            ((TextView) popupProfileEditPreviewSocial.findViewById(R.id.typeField)).setText("YouTube");
            ((ImageView) popupProfileEditPreviewSocial.findViewById(R.id.imageCall)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_youtube_48));
        } else if (typeEnum.equals(SocialEnums.MEDIUM)) {
            ((TextView) popupProfileEditPreviewSocial.findViewById(R.id.valueUpd)).setText(contact.getSocialModel().getMediumLink());
            ((TextView) popupProfileEditPreviewSocial.findViewById(R.id.typeField)).setText("Medium");
            ((ImageView) popupProfileEditPreviewSocial.findViewById(R.id.imageCall)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.medium_size_64));
        }

        ((TextView) popupProfileEditPreviewSocial.findViewById(R.id.valueUpd)).scrollTo(0, 0);


        socialPopup.findViewById(R.id.linearSocialWindow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupProfileEditPreviewSocial.setVisibility(View.GONE);
            }
        });

        popupProfileEditPreviewSocial.findViewById(R.id.editPopupCopy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                copyToClipboard(mainView.getContext(), String.valueOf(((TextView) popupProfileEditPreviewSocial.findViewById(R.id.valueUpd)).getText().toString().trim()));
                Toast.makeText(mainView.getContext(), "Copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });

        popupProfileEditPreviewSocial.findViewById(R.id.editPopupShare).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = ((TextView) popupProfileEditPreviewSocial.findViewById(R.id.valueUpd)).getText().toString().trim();
                text += "\n\n";
                text += "Data shared via http://Extime.pro\n";
                shareIntent(text);
                //shareIntent(String.valueOf(String.valueOf(((TextView) popupProfileEditPreviewSocial.findViewById(R.id.valueUpd)).getText().toString().trim())));
            }
        });

        popupProfileEditPreviewSocial.findViewById(R.id.editPopupMail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        popupProfileEditPreviewSocial.findViewById(R.id.remind_contact_number).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        popupProfileEditPreviewSocial.findViewById(R.id.call2me_contact_number).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        popupProfileEditPreviewSocial.findViewById(R.id.updateContact).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupEditSocial = (FrameLayout) getActivity().findViewById(R.id.popupEditSocial);
                popupEditSocial.setVisibility(View.VISIBLE);
                popupEditSocial.setClickable(true);
                popupEditSocial.findViewById(R.id.ok_social).setVisibility(View.VISIBLE);
                popupEditSocial.findViewById(R.id.get_last_clips).setVisibility(View.GONE);
                popupEditSocial.findViewById(R.id.imageEditSocial).setVisibility(View.GONE);
                popupEditSocial.findViewById(R.id.searchSocial).setVisibility(View.GONE);
                popupEditSocial.findViewById(R.id.frameImageEditSocialInsta).setVisibility(View.GONE);

                if (typeEnum.equals(SocialEnums.FACEBOOK)) {
                    ((TextView) popupEditSocial.findViewById(R.id.typeField)).setText("Facebook");
                    ((EditText) popupEditSocial.findViewById(R.id.dataToEdit)).setText(contact.getSocialModel().getFacebookLink());
                } else if (typeEnum.equals(SocialEnums.VK)) {
                    ((TextView) popupEditSocial.findViewById(R.id.typeField)).setText("Vk");
                    ((EditText) popupEditSocial.findViewById(R.id.dataToEdit)).setText(contact.getSocialModel().getVkLink());
                } else if (typeEnum.equals(SocialEnums.INSTAGRAM)) {
                    ((TextView) popupEditSocial.findViewById(R.id.typeField)).setText("Instagram");
                    ((EditText) popupEditSocial.findViewById(R.id.dataToEdit)).setText(contact.getSocialModel().getInstagramLink());
                } else if (typeEnum.equals(SocialEnums.LINKEDIN)) {
                    ((TextView) popupEditSocial.findViewById(R.id.typeField)).setText("LinkedIn");
                    ((EditText) popupEditSocial.findViewById(R.id.dataToEdit)).setText(contact.getSocialModel().getLinkedInLink());
                } else if (typeEnum.equals(SocialEnums.TELEGRAM)) {
                    ((TextView) popupEditSocial.findViewById(R.id.typeField)).setText("Telegram");
                    ((EditText) popupEditSocial.findViewById(R.id.dataToEdit)).setText(contact.getSocialModel().getTelegramLink());
                } else if (typeEnum.equals(SocialEnums.SKYPE)) {
                    ((TextView) popupEditSocial.findViewById(R.id.typeField)).setText("Skype");
                    ((EditText) popupEditSocial.findViewById(R.id.dataToEdit)).setText(contact.getSocialModel().getSkypeLink());
                } else if (typeEnum.equals(SocialEnums.VIBER)) {
                    ((TextView) popupEditSocial.findViewById(R.id.typeField)).setText("Viber");
                    ((EditText) popupEditSocial.findViewById(R.id.dataToEdit)).setText(contact.getSocialModel().getViberLink());
                } else if (typeEnum.equals(SocialEnums.WHATSAPP)) {
                    ((TextView) popupEditSocial.findViewById(R.id.typeField)).setText("WhatsApp");
                    ((EditText) popupEditSocial.findViewById(R.id.dataToEdit)).setText(contact.getSocialModel().getWhatsappLink());
                } else if (typeEnum.equals(SocialEnums.TWITTER)) {
                    ((TextView) popupEditSocial.findViewById(R.id.typeField)).setText("Twitter");
                    ((EditText) popupEditSocial.findViewById(R.id.dataToEdit)).setText(contact.getSocialModel().getTwitterLink());
                } else if (typeEnum.equals(SocialEnums.YOUTUBE)) {
                    ((TextView) popupEditSocial.findViewById(R.id.typeField)).setText("Youtube");
                    ((EditText) popupEditSocial.findViewById(R.id.dataToEdit)).setText(contact.getSocialModel().getYoutubeLink());
                } else if (typeEnum.equals(SocialEnums.MEDIUM)) {
                    ((TextView) popupEditSocial.findViewById(R.id.typeField)).setText("Medium");
                    ((EditText) popupEditSocial.findViewById(R.id.dataToEdit)).setText(contact.getSocialModel().getMediumLink());
                }

                openedViews.add(popupEditSocial);


                popupEditSocial.findViewById(R.id.updateContactField).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Realm realm = Realm.getDefaultInstance(); //-

                        if (typeEnum.equals(SocialEnums.FACEBOOK)) {
                            realm.beginTransaction();
                            SocialModel socialModel;
                            if (contact.getSocialModel() == null) {
                                socialModel = realm.createObject(SocialModel.class);
                            } else
                                socialModel = contact.getSocialModel();

                            if (((EditText) popupEditSocial.findViewById(R.id.dataToEdit)).getText().length() > 0) {

                                if (socialModel.getFacebookLink() != null && !socialModel.getFacebookLink().isEmpty()) {
                                    contactsService.deleteNoteContact(contact.getIdContact(), socialModel.getFacebookLink());
                                }


                                String f = ((EditText) popupEditSocial.findViewById(R.id.dataToEdit)).getText().toString();

                                if (!f.contains(".com/")) {
                                    f = "https://www.facebook.com/" + f.trim();
                                }

                                if (ClipboardType.isFacebook(f)) {
                                    socialModel.setFacebookLink(f);
                                } else {
                                    socialModel.setFacebookLink(((EditText) popupEditSocial.findViewById(R.id.dataToEdit)).getText().toString());
                                }

                                //socialModel.setFacebookLink(((EditText) popupEditSocial.findViewById(R.id.dataToEdit)).getText().toString());

                                Drawable colorf = new ColorDrawable(Color.parseColor("#475993"));
                                Drawable imagef = getResources().getDrawable(R.drawable.icn_social_facebook2);
                                LayerDrawable ldf = new LayerDrawable(new Drawable[]{colorf, imagef});
                                ((ImageView) socialPopup.findViewById(R.id.facebook_icon)).setImageDrawable(ldf);


                                if (profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE) {
                                    ((ImageView) profilePopUp.findViewById(R.id.facebook_icon)).setImageDrawable(ldf);
                                } else if (companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE) {
                                    ((ImageView) companyProfilePopup.findViewById(R.id.facebook_icon)).setImageDrawable(ldf);
                                } else {
                                    ((ImageView) getActivity().findViewById(R.id.facebook_icon)).setImageDrawable(ldf);
                                }


                                String link = socialModel.getFacebookLink();
                                boolean checkich = false;
                                try {
                                    String checkFacebook = link.substring(0, 37);
                                    if (checkFacebook.equals("https://www.facebook.com/profile.php?")) {
                                        link = link.substring(37, link.length());
                                        ((TextView) socialPopup.findViewById(R.id.facebook_nick)).setText(link);
                                        checkich = true;
                                    }
                                } catch (Exception e) {

                                    e.printStackTrace();
                                }
                                if (!checkich) {
                                    try {
                                        if (link.contains("https://facebook.com/")) {
                                            link = link.substring(21, link.length());
                                            if (link.contains("profile.php")) {
                                                link = link.substring(link.indexOf(".php") + 5, link.length());
                                            }
                                            ((TextView) socialPopup.findViewById(R.id.facebook_nick)).setText(link);
                                            checkich = true;
                                        } else if (link.contains("https://m.facebook.com/")) {
                                            link = link.substring(23, link.length());
                                            if (link.contains("profile.php")) {
                                                link = link.substring(link.indexOf(".php") + 5, link.length());
                                            }
                                            ((TextView) socialPopup.findViewById(R.id.facebook_nick)).setText(link);
                                            checkich = true;
                                        } else if (link.contains("php?")) {
                                            int index = link.indexOf("php?");
                                            link = link.substring(index + 4, link.length());
                                            ((TextView) socialPopup.findViewById(R.id.facebook_nick)).setText(link);
                                            checkich = true;
                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                //   System.out.println(checkFacebook+", "+link);
                                if (!checkich)
                                    ((TextView) socialPopup.findViewById(R.id.facebook_nick)).setText(socialModel.getFacebookLink());

                                //   ((TextView) socialPopup.findViewById(R.id.facebook_nick)).setText(socialModel.getFacebookLink());
                                contact.hasFacebook = true;
                                contact.setSocialModel(socialModel);
                                realm.commitTransaction();
                                contactsService.addNoteToContact(contact.getIdContact(), contact.getSocialModel().getFacebookLink(), contact.getName());
                                ContactCacheService.updateContact(contact, mainView.getContext());
                                //  EventBus.getDefault().post(new UpdateFile());
                                popupEditSocial.setVisibility(View.GONE);
                                ((TextView) popupProfileEditPreviewSocial.findViewById(R.id.valueUpd)).setText(contact.getSocialModel().getFacebookLink());

                            } else {
                                if (socialModel.getFacebookLink() != null && !socialModel.getFacebookLink().isEmpty()) {
                                    contactsService.deleteNoteContact(contact.getIdContact(), socialModel.getFacebookLink());
                                }
                                contact.getSocialModel().setFacebookLink(null);
                                //  ((ImageView) socialPopup.findViewById(R.id.facebook_icon)).setImageResource(R.drawable.icn_social_facebook_gray);
                                //  ((ImageView) getActivity().findViewById(R.id.facebook_icon)).setImageResource(R.drawable.icn_social_facebook_gray);
                                Drawable color = new ColorDrawable(Color.parseColor("#e2e5e8"));
                                Drawable image = getResources().getDrawable(R.drawable.icn_social_facebook2);
                                LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
                                ((ImageView) socialPopup.findViewById(R.id.facebook_icon)).setImageDrawable(ld);


                                if (profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE) {
                                    ((ImageView) profilePopUp.findViewById(R.id.facebook_icon)).setImageDrawable(ld);
                                } else if (companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE) {
                                    ((ImageView) companyProfilePopup.findViewById(R.id.facebook_icon)).setImageDrawable(ld);
                                } else {
                                    ((ImageView) getActivity().findViewById(R.id.facebook_icon)).setImageDrawable(ld);
                                }


                                ((TextView) socialPopup.findViewById(R.id.facebook_nick)).setText("add username or link");
                                contact.hasFacebook = false;
                                contact.setSocialModel(socialModel);
                                realm.commitTransaction();
                                ContactCacheService.updateContact(contact, mainView.getContext());
                                // EventBus.getDefault().post(new UpdateFile());
                                popupEditSocial.setVisibility(View.GONE);
                                popupProfileEditPreviewSocial.setVisibility(View.GONE);
                            }

                            EventBus.getDefault().post(new UpdateFile());

                        } else if (typeEnum.equals(SocialEnums.VK)) {


                            realm.beginTransaction();
                            SocialModel socialModel;
                            if (contact.getSocialModel() == null) {
                                socialModel = realm.createObject(SocialModel.class);
                            } else
                                socialModel = contact.getSocialModel();

                            if (((EditText) popupEditSocial.findViewById(R.id.dataToEdit)).getText().length() > 0) {
                                //     if (contact.getSocialModel() != null)

                                if (socialModel.getVkLink() != null && !socialModel.getVkLink().isEmpty()) {
                                    contactsService.deleteNoteContact(contact.getIdContact(), socialModel.getVkLink());
                                }

                                String f = ((EditText) popupEditSocial.findViewById(R.id.dataToEdit)).getText().toString();

                                if (!f.contains(".com/")) {
                                    f = "https://vk.com/" + f.trim();
                                }

                                if (ClipboardType.isVk(f)) {
                                    socialModel.setVkLink(f);
                                } else {
                                    socialModel.setVkLink(((EditText) popupEditSocial.findViewById(R.id.dataToEdit)).getText().toString());
                                }

                                //socialModel.setVkLink(((EditText) popupEditSocial.findViewById(R.id.dataToEdit)).getText().toString());
                                //     ((ImageView) socialPopup.findViewById(R.id.vk_icon)).setImageResource(R.drawable.icn_social_vk);
                                //     ((ImageView) getActivity().findViewById(R.id.vk_icon)).setImageResource(R.drawable.icn_social_vk);
                                Drawable colorv = new ColorDrawable(Color.parseColor("#507299"));
                                Drawable imagev = getResources().getDrawable(R.drawable.icn_social_vk2);
                                LayerDrawable ldv = new LayerDrawable(new Drawable[]{colorv, imagev});
                                ((ImageView) socialPopup.findViewById(R.id.vk_icon)).setImageDrawable(ldv);




                                if (profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE) {
                                    ((ImageView) profilePopUp.findViewById(R.id.vk_icon)).setImageDrawable(ldv);
                                } else if (companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE) {
                                    ((ImageView) companyProfilePopup.findViewById(R.id.vk_icon)).setImageDrawable(ldv);
                                } else {
                                    ((ImageView) getActivity().findViewById(R.id.vk_icon)).setImageDrawable(ldv);
                                }


                                try {
                                    String link = socialModel.getVkLink();
                                    String checkVK = link.substring(0, 8);
                                    if (link.contains("https://vk.com/")) {
                                        System.out.println("TRUE VK LINK");
                                        link = link.substring(15, link.length());
                                        System.out.println("TRUE VK LINK2 = " + link);
                                        ((TextView) socialPopup.findViewById(R.id.vkNick)).setText(link);
                                    } else if (link.contains("https://m.vk.com/")) {
                                        link = link.substring(17, link.length());
                                        ((TextView) socialPopup.findViewById(R.id.vkNick)).setText(link);

                                    } else if (link.contains("m.vk.com/")) {
                                        link = link.substring(9, link.length());
                                        ((TextView) socialPopup.findViewById(R.id.vkNick)).setText(link);
                                    } else if (link.contains("vk.com/")) {
                                        link = link.substring(7, link.length());
                                        ((TextView) socialPopup.findViewById(R.id.vkNick)).setText(link);
                                    } else if (link.contains("https://www.vk.com/")) {
                                        link = link.substring(19, link.length());
                                        ((TextView) socialPopup.findViewById(R.id.vkNick)).setText(link);

                                    } else
                                        ((TextView) socialPopup.findViewById(R.id.vkNick)).setText(socialModel.getVkLink());
                                } catch (Exception e) {
                                    ((TextView) socialPopup.findViewById(R.id.vkNick)).setText(socialModel.getVkLink());
                                }

                                //        if (contact.getSocialModel() != null)
                                //    ((TextView) socialPopup.findViewById(R.id.vkNick)).setText(socialModel.getVkLink());
                                contact.hasVk = true;
                                contact.setSocialModel(socialModel);
                                realm.commitTransaction();
                                contactsService.addNoteToContact(contact.getIdContact(), contact.getSocialModel().getVkLink(), contact.getName());
                                ContactCacheService.updateContact(contact, mainView.getContext());
                                // EventBus.getDefault().post(new UpdateFile());
                                popupEditSocial.setVisibility(View.GONE);
                                ((TextView) popupProfileEditPreviewSocial.findViewById(R.id.valueUpd)).setText(contact.getSocialModel().getVkLink());
                            } else {
                                if (socialModel.getVkLink() != null && !socialModel.getVkLink().isEmpty()) {
                                    contactsService.deleteNoteContact(contact.getIdContact(), socialModel.getVkLink());
                                }
                                contact.getSocialModel().setVkLink(null);
                                //     ((ImageView) socialPopup.findViewById(R.id.vk_icon)).setImageResource(R.drawable.icn_social_vk_gray);
                                //     ((ImageView) getActivity().findViewById(R.id.vk_icon)).setImageResource(R.drawable.icn_social_vk_gray);
                                Drawable color2 = new ColorDrawable(Color.parseColor("#e2e5e8"));
                                Drawable image2 = getResources().getDrawable(R.drawable.icn_social_vk2);
                                LayerDrawable ld2 = new LayerDrawable(new Drawable[]{color2, image2});
                                ((ImageView) socialPopup.findViewById(R.id.vk_icon)).setImageDrawable(ld2);


                                if (profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE) {
                                    ((ImageView) profilePopUp.findViewById(R.id.vk_icon)).setImageDrawable(ld2);
                                } else if (companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE) {
                                    ((ImageView) companyProfilePopup.findViewById(R.id.vk_icon)).setImageDrawable(ld2);
                                } else {
                                    ((ImageView) getActivity().findViewById(R.id.vk_icon)).setImageDrawable(ld2);
                                }


                                ((TextView) socialPopup.findViewById(R.id.vkNick)).setText("add username or");
                                contact.hasVk = false;
                                contact.setSocialModel(socialModel);
                                realm.commitTransaction();
                                ContactCacheService.updateContact(contact, mainView.getContext());
                                //  EventBus.getDefault().post(new UpdateFile());
                                popupEditSocial.setVisibility(View.GONE);
                                popupProfileEditPreviewSocial.setVisibility(View.GONE);
                            }

                            ArrayList<String> listEdit = new ArrayList<>();
                            ArrayList<Boolean> listEditBool = new ArrayList<>();

                            listEdit.add(contact.getName());
                            if (contact.listOfContacts == null || contact.listOfContacts.isEmpty())
                                listEditBool.add(true);
                            else
                                listEditBool.add(false);

                            MainActivity.listToManyUpdateFile.add("EDIT");
                            MainActivity.listToManyUpdateFile.add(listEdit);
                            MainActivity.listToManyUpdateFile.add(listEditBool);

                            EventBus.getDefault().post(new UpdateFile());
                        } else if (typeEnum.equals(SocialEnums.INSTAGRAM)) {


                            realm.beginTransaction();
                            SocialModel socialModel;
                            if (contact.getSocialModel() == null) {
                                socialModel = realm.createObject(SocialModel.class);
                            } else
                                socialModel = contact.getSocialModel();

                            if (((EditText) popupEditSocial.findViewById(R.id.dataToEdit)).getText().length() > 0) {
                                //  if (contact.getSocialModel() != null)

                                if (socialModel.getInstagramLink() != null && !socialModel.getInstagramLink().isEmpty()) {
                                    contactsService.deleteNoteContact(contact.getIdContact(), socialModel.getInstagramLink());
                                }



                                String username = ((EditText) popupEditSocial.findViewById(R.id.dataToEdit)).getText().toString();

                                if (username.contains("?utm")) {
                                    int ind = username.indexOf('?');
                                    if (ind != -1)
                                        username = username.substring(0, ind);

                                }

                                if (username.charAt(username.length() - 1) == '/') {
                                    username = username.substring(0, username.length() - 1);
                                }

                                if (!username.toLowerCase().contains("instagram.com")) {
                                    username = "https://instagram.com/" + username;
                                }

                                socialModel.setInstagramLink(username);
                                //    ((ImageView) socialPopup.findViewById(R.id.instagram_icon)).setImageResource(R.drawable.icn_social_instagram);
                                //    ((ImageView) getActivity().findViewById(R.id.inst_icon)).setImageResource(R.drawable.icn_social_instagram);
                                Drawable colori = new ColorDrawable(Color.parseColor("#8a3ab9"));
                                Drawable imagei = getResources().getDrawable(R.drawable.icn_social_ints2);
                                LayerDrawable ldi = new LayerDrawable(new Drawable[]{colori, imagei});
                                ((ImageView) socialPopup.findViewById(R.id.instagram_icon)).setImageDrawable(ldi);


                                if (profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE) {
                                    ((ImageView) profilePopUp.findViewById(R.id.inst_icon)).setImageDrawable(ldi);
                                } else if (companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE) {
                                    ((ImageView) companyProfilePopup.findViewById(R.id.inst_icon)).setImageDrawable(ldi);
                                } else {
                                    ((ImageView) getActivity().findViewById(R.id.inst_icon)).setImageDrawable(ldi);
                                }

                                //   if (contact.getSocialModel() != null)


                                contact.hasInst = true;
                                contact.setSocialModel(socialModel);
                                realm.commitTransaction();

                                String inst = contact.getSocialModel().getInstagramLink();

                                if (inst.contains(".com/")) {
                                    int ind = inst.indexOf(".com/");
                                    String outLink = inst.substring(ind + 5, inst.length());


                                    if (outLink.contains("?")) {
                                        int in = outLink.indexOf("?");
                                        outLink = outLink.substring(0, in);
                                    }

                                    ((TextView) socialPopup.findViewById(R.id.instagramNick)).setText(outLink);
                                } else
                                    ((TextView) socialPopup.findViewById(R.id.instagramNick)).setText(contact.getSocialModel().getInstagramLink());

                                contactsService.addNoteToContact(contact.getIdContact(), contact.getSocialModel().getInstagramLink(), contact.getName());
                                ContactCacheService.updateContact(contact, mainView.getContext());
                                //  EventBus.getDefault().post(new UpdateFile());
                                popupEditSocial.setVisibility(View.GONE);
                                ((TextView) popupProfileEditPreviewSocial.findViewById(R.id.valueUpd)).setText(contact.getSocialModel().getInstagramLink());
                            } else {
                                if (socialModel.getInstagramLink() != null && !socialModel.getInstagramLink().isEmpty()) {
                                    contactsService.deleteNoteContact(contact.getIdContact(), socialModel.getInstagramLink());
                                }
                                socialModel.setInstagramLink(null);
                                //         ((ImageView) socialPopup.findViewById(R.id.instagram_icon)).setImageResource(R.drawable.icn_social_instagram_gray);
                                //         ((ImageView) getActivity().findViewById(R.id.inst_icon)).setImageResource(R.drawable.icn_social_instagram_gray);
                                Drawable color4 = new ColorDrawable(Color.parseColor("#e2e5e8"));
                                Drawable image4 = getResources().getDrawable(R.drawable.icn_social_ints2);
                                LayerDrawable ld4 = new LayerDrawable(new Drawable[]{color4, image4});
                                ((ImageView) socialPopup.findViewById(R.id.instagram_icon)).setImageDrawable(ld4);


                                if (profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE) {
                                    ((ImageView) profilePopUp.findViewById(R.id.inst_icon)).setImageDrawable(ld4);
                                } else if (companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE) {
                                    ((ImageView) companyProfilePopup.findViewById(R.id.inst_icon)).setImageDrawable(ld4);
                                } else {
                                    ((ImageView) getActivity().findViewById(R.id.inst_icon)).setImageDrawable(ld4);
                                }


                                ((TextView) socialPopup.findViewById(R.id.instagramNick)).setText("add new link");
                                contact.hasInst = false;
                                contact.setSocialModel(socialModel);
                                realm.commitTransaction();

                                ContactCacheService.updateContact(contact, mainView.getContext());
                                //    EventBus.getDefault().post(new UpdateFile());
                                popupEditSocial.setVisibility(View.GONE);
                                popupProfileEditPreviewSocial.setVisibility(View.GONE);
                            }


                            EventBus.getDefault().post(new UpdateFile());


                        } else if (typeEnum.equals(SocialEnums.LINKEDIN)) {

                            realm.beginTransaction();
                            SocialModel socialModel;
                            if (contact.getSocialModel() == null) {
                                socialModel = realm.createObject(SocialModel.class);
                            } else
                                socialModel = contact.getSocialModel();

                            if (((EditText) popupEditSocial.findViewById(R.id.dataToEdit)).getText().length() > 0) {
                                //    if (contact.getSocialModel() != null)

                                if (socialModel.getLinkedInLink() != null && !socialModel.getLinkedInLink().isEmpty()) {
                                    contactsService.deleteNoteContact(contact.getIdContact(), socialModel.getLinkedInLink());
                                }

                                String f = ((EditText) popupEditSocial.findViewById(R.id.dataToEdit)).getText().toString();

                                if (!f.contains(".com/")) {
                                    f = "https://www.linkedin.com/in/" + f.trim();
                                }

                                if (ClipboardType.isLinkedIn(f)) {
                                    socialModel.setLinkedInLink(f);
                                } else {
                                    socialModel.setLinkedInLink(((EditText) popupEditSocial.findViewById(R.id.dataToEdit)).getText().toString());
                                }

                                //socialModel.setLinkedInLink(((EditText) popupEditSocial.findViewById(R.id.dataToEdit)).getText().toString());

                                //      ((ImageView) socialPopup.findViewById(R.id.link_icon)).setImageResource(R.drawable.icn_social_linkedin);
                                //      ((ImageView) getActivity().findViewById(R.id.link_icon)).setImageResource(R.drawable.icn_social_linkedin);
                                Drawable colorl = new ColorDrawable(Color.parseColor("#0077B7"));
                                Drawable imagel = getResources().getDrawable(R.drawable.icn_social_linked2);
                                LayerDrawable ldl = new LayerDrawable(new Drawable[]{colorl, imagel});



                                if (profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE) {
                                    ((ImageView) profilePopUp.findViewById(R.id.link_icon)).setImageDrawable(ldl);
                                } else if (companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE) {
                                    ((ImageView) companyProfilePopup.findViewById(R.id.link_icon)).setImageDrawable(ldl);
                                } else {
                                    ((ImageView) getActivity().findViewById(R.id.link_icon)).setImageDrawable(ldl);
                                }


                                try {
                                    String link = socialModel.getLinkedInLink();
                                    String sub = link.substring(link.length() - 20, link.length());
                                    ((TextView) socialPopup.findViewById(R.id.link_text)).setText(sub);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    ((TextView) socialPopup.findViewById(R.id.link_text)).setText(socialModel.getLinkedInLink());
                                }
                                //  ((TextView) socialPopup.findViewById(R.id.link_text)).setText(socialModel.getLinkedInLink());
                                contact.hasLinked = true;
                                contact.setSocialModel(socialModel);
                                realm.commitTransaction();
                                contactsService.addNoteToContact(contact.getIdContact(), contact.getSocialModel().getLinkedInLink(), contact.getName());
                                ContactCacheService.updateContact(contact, mainView.getContext());
                                //  EventBus.getDefault().post(new UpdateFile());
                                popupEditSocial.setVisibility(View.GONE);
                                ((TextView) popupProfileEditPreviewSocial.findViewById(R.id.valueUpd)).setText(contact.getSocialModel().getLinkedInLink());
                            } else {
                                if (socialModel.getLinkedInLink() != null && !socialModel.getLinkedInLink().isEmpty()) {
                                    contactsService.deleteNoteContact(contact.getIdContact(), socialModel.getLinkedInLink());
                                }
                                contact.getSocialModel().setLinkedInLink(null);
                                //     ((ImageView) socialPopup.findViewById(R.id.link_icon)).setImageResource(R.drawable.icn_social_linkedin_gray);
                                //     ((ImageView) getActivity().findViewById(R.id.link_icon)).setImageResource(R.drawable.icn_social_linkedin_gray);
                                Drawable color3 = new ColorDrawable(Color.parseColor("#e2e5e8"));
                                Drawable image3 = getResources().getDrawable(R.drawable.icn_social_linked2);
                                LayerDrawable ld3 = new LayerDrawable(new Drawable[]{color3, image3});
                                ((ImageView) socialPopup.findViewById(R.id.link_icon)).setImageDrawable(ld3);


                                if (profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE) {
                                    ((ImageView) profilePopUp.findViewById(R.id.link_icon)).setImageDrawable(ld3);
                                } else if (companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE) {
                                    ((ImageView) companyProfilePopup.findViewById(R.id.link_icon)).setImageDrawable(ld3);
                                } else {
                                    ((ImageView) getActivity().findViewById(R.id.link_icon)).setImageDrawable(ld3);
                                }

                                ((TextView) socialPopup.findViewById(R.id.link_text)).setText("add new link");
                                contact.hasLinked = false;
                                contact.setSocialModel(socialModel);
                                realm.commitTransaction();
                                ContactCacheService.updateContact(contact, mainView.getContext());
                                //  EventBus.getDefault().post(new UpdateFile());
                                popupEditSocial.setVisibility(View.GONE);
                                popupProfileEditPreviewSocial.setVisibility(View.GONE);
                            }


                            EventBus.getDefault().post(new UpdateFile());

                        } else if (typeEnum.equals(SocialEnums.VIBER)) {


                            realm.beginTransaction();
                            SocialModel socialModel;
                            if (contact.getSocialModel() == null) {
                                socialModel = realm.createObject(SocialModel.class);
                            } else
                                socialModel = contact.getSocialModel();

                            if (((EditText) popupEditSocial.findViewById(R.id.dataToEdit)).getText().length() > 0) {
                                //       if (contact.getSocialModel() != null)

                                if (socialModel.getViberLink() != null && !socialModel.getViberLink().isEmpty()) {
                                    contactsService.deleteNoteContact(contact.getIdContact(), socialModel.getViberLink());
                                }

                                socialModel.setViberLink(((EditText) popupEditSocial.findViewById(R.id.dataToEdit)).getText().toString());
                                ((EditText) popupEditSocial.findViewById(R.id.dataToEdit)).getText().clear();
                                //        ((ImageView) socialPopup.findViewById(R.id.viber_icon)).setImageResource(R.drawable.icn_social_viber);
                                //        ((ImageView) getActivity().findViewById(R.id.viber_icon)).setImageResource(R.drawable.icn_social_viber);
                                Drawable colorvi = new ColorDrawable(Color.parseColor("#6F3FAA"));
                                Drawable imagevi = getResources().getDrawable(R.drawable.icn_social_viber2);
                                LayerDrawable ldvi = new LayerDrawable(new Drawable[]{colorvi, imagevi});
                                ((CircleImageView) socialPopup.findViewById(R.id.viber_icon)).setImageDrawable(ldvi);


                                if (profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE) {
                                    ((CircleImageView) profilePopUp.findViewById(R.id.viber_icon)).setImageDrawable(ldvi);
                                } else if (companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE) {
                                    ((CircleImageView) companyProfilePopup.findViewById(R.id.viber_icon)).setImageDrawable(ldvi);
                                } else {
                                    ((CircleImageView) getActivity().findViewById(R.id.viber_icon)).setImageDrawable(ldvi);
                                }


                                //   if (contact.getSocialModel() != null)
                                ((TextView) socialPopup.findViewById(R.id.viber_text)).setText(socialModel.getViberLink());
                                contact.hasViber = true;
                                contact.setSocialModel(socialModel);
                                realm.commitTransaction();
                                contactsService.addNoteToContact(contact.getIdContact(), contact.getSocialModel().getViberLink(), contact.getName());
                                ContactCacheService.updateContact(contact, mainView.getContext());
                                //    EventBus.getDefault().post(new UpdateFile());
                                popupEditSocial.setVisibility(View.GONE);
                                ((TextView) popupProfileEditPreviewSocial.findViewById(R.id.valueUpd)).setText(contact.getSocialModel().getViberLink());
                            } else {
                                if (socialModel.getViberLink() != null && !socialModel.getViberLink().isEmpty()) {
                                    contactsService.deleteNoteContact(contact.getIdContact(), socialModel.getViberLink());
                                }
                                socialModel.setViberLink(null);
                                ((EditText) popupEditSocial.findViewById(R.id.dataToEdit)).getText().clear();
                                //      ((ImageView) socialPopup.findViewById(R.id.viber_icon)).setImageResource(R.drawable.icn_social_viber_gray);
                                //      ((ImageView) getActivity().findViewById(R.id.viber_icon)).setImageResource(R.drawable.icn_social_viber_gray);
                                Drawable color5 = new ColorDrawable(Color.parseColor("#e2e5e8"));
                                Drawable image5 = getResources().getDrawable(R.drawable.icn_social_viber2_gray);
                                LayerDrawable ld5 = new LayerDrawable(new Drawable[]{color5, image5});
                                ((CircleImageView) socialPopup.findViewById(R.id.viber_icon)).setImageDrawable(ld5);


                                if (profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE) {
                                    ((CircleImageView) profilePopUp.findViewById(R.id.viber_icon)).setImageDrawable(ld5);
                                } else if (companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE) {
                                    ((CircleImageView) companyProfilePopup.findViewById(R.id.viber_icon)).setImageDrawable(ld5);
                                } else {
                                    ((CircleImageView) getActivity().findViewById(R.id.viber_icon)).setImageDrawable(ld5);
                                }

                                ((TextView) socialPopup.findViewById(R.id.viber_text)).setText("add new link");
                                contact.hasViber = false;
                                contact.setSocialModel(socialModel);
                                realm.commitTransaction();
                                ContactCacheService.updateContact(contact, mainView.getContext());
                                //   EventBus.getDefault().post(new UpdateFile());
                                popupEditSocial.setVisibility(View.GONE);
                                popupProfileEditPreviewSocial.setVisibility(View.GONE);
                            }


                            EventBus.getDefault().post(new UpdateFile());


                        } else if (typeEnum.equals(SocialEnums.WHATSAPP)) {


                            realm.beginTransaction();
                            SocialModel socialModel;
                            if (contact.getSocialModel() == null) {
                                socialModel = realm.createObject(SocialModel.class);
                            } else
                                socialModel = contact.getSocialModel();


                            //  contact.setSocialModel(new SocialModel());
                            if (((EditText) popupEditSocial.findViewById(R.id.dataToEdit)).getText().length() > 0) {
                                // if (contact.getSocialModel() != null)

                                if (socialModel.getWhatsappLink() != null && !socialModel.getWhatsappLink().isEmpty()) {
                                    contactsService.deleteNoteContact(contact.getIdContact(), socialModel.getWhatsappLink());
                                }

                                socialModel.setWhatsappLink(((EditText) popupEditSocial.findViewById(R.id.dataToEdit)).getText().toString());

                                //    contact.getSocialModel().setWhatsappLink(((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().toString());
                                ((EditText) popupEditSocial.findViewById(R.id.dataToEdit)).getText().clear();
                                //  ((ImageView) socialPopup.findViewById(R.id.whatsapp_icon)).setImageResource(R.drawable.icn_social_whatsapp);
                                //  ((ImageView) getActivity().findViewById(R.id.whatsapp_icon)).setImageResource(R.drawable.icn_social_whatsapp);
                                Drawable colorw = new ColorDrawable(Color.parseColor("#75B73B"));
                                Drawable imagew = getResources().getDrawable(R.drawable.icn_social_whatsapp3);
                                LayerDrawable ldw = new LayerDrawable(new Drawable[]{colorw, imagew});
                                ((CircleImageView) socialPopup.findViewById(R.id.whatsapp_icon)).setImageDrawable(ldw);


                                if (profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE) {
                                    ((CircleImageView) profilePopUp.findViewById(R.id.whatsapp_icon)).setImageDrawable(ldw);
                                } else if (companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE) {
                                    ((CircleImageView) companyProfilePopup.findViewById(R.id.whatsapp_icon)).setImageDrawable(ldw);
                                } else {
                                    ((CircleImageView) getActivity().findViewById(R.id.whatsapp_icon)).setImageDrawable(ldw);
                                }

                                //   if(contact.getSocialModel() != null)
                                ((TextView) socialPopup.findViewById(R.id.whatsapp_text)).setText(socialModel.getWhatsappLink());


                                contact.hasWhatsapp = true;
                                contact.setSocialModel(socialModel);
                                realm.commitTransaction();
                                contactsService.addNoteToContact(contact.getIdContact(), contact.getSocialModel().getWhatsappLink(), contact.getName());
                                ContactCacheService.updateContact(contact, mainView.getContext());
                                // EventBus.getDefault().post(new UpdateFile());
                                popupEditSocial.setVisibility(View.GONE);
                                ((TextView) popupProfileEditPreviewSocial.findViewById(R.id.valueUpd)).setText(contact.getSocialModel().getWhatsappLink());
                            } else {
                                if (socialModel.getWhatsappLink() != null && !socialModel.getWhatsappLink().isEmpty()) {
                                    contactsService.deleteNoteContact(contact.getIdContact(), socialModel.getWhatsappLink());
                                }
                                socialModel.setWhatsappLink(null);
                                //      contact.getSocialModel().setWhatsappLink(null);
                                ((EditText) popupEditSocial.findViewById(R.id.dataToEdit)).getText().clear();
                                //    ((ImageView) socialPopup.findViewById(R.id.whatsapp_icon)).setImageResource(R.drawable.icn_social_whatsapp_gray);
                                //    ((ImageView) getActivity().findViewById(R.id.whatsapp_icon)).setImageResource(R.drawable.icn_social_whatsapp_gray);
                                Drawable color6 = new ColorDrawable(Color.parseColor("#e2e5e8"));
                                Drawable image6 = getResources().getDrawable(R.drawable.icn_social_whatsapp3_gray);
                                LayerDrawable ld6 = new LayerDrawable(new Drawable[]{color6, image6});
                                ((CircleImageView) socialPopup.findViewById(R.id.whatsapp_icon)).setImageDrawable(ld6);


                                if (profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE) {
                                    ((CircleImageView) profilePopUp.findViewById(R.id.whatsapp_icon)).setImageDrawable(ld6);
                                } else if (companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE) {
                                    ((CircleImageView) companyProfilePopup.findViewById(R.id.whatsapp_icon)).setImageDrawable(ld6);
                                } else {
                                    ((CircleImageView) getActivity().findViewById(R.id.whatsapp_icon)).setImageDrawable(ld6);
                                }


                                ((TextView) socialPopup.findViewById(R.id.whatsapp_text)).setText("add new link");
                                contact.hasWhatsapp = false;
                                contact.setSocialModel(socialModel);
                                realm.commitTransaction();
                                ContactCacheService.updateContact(contact, mainView.getContext());
                                //  EventBus.getDefault().post(new UpdateFile());
                                popupEditSocial.setVisibility(View.GONE);
                                popupProfileEditPreviewSocial.setVisibility(View.GONE);
                            }


                            EventBus.getDefault().post(new UpdateFile());


                        } else if (typeEnum.equals(SocialEnums.TELEGRAM)) {


                            realm.beginTransaction();
                            SocialModel socialModel;
                            if (contact.getSocialModel() == null) {
                                socialModel = realm.createObject(SocialModel.class);
                            } else
                                socialModel = contact.getSocialModel();

                            if (((EditText) popupEditSocial.findViewById(R.id.dataToEdit)).getText().length() > 0) {
                                //    if (contact.getSocialModel() != null)

                                if (socialModel.getTelegramLink() != null && !socialModel.getTelegramLink().isEmpty()) {
                                    contactsService.deleteNoteContact(contact.getIdContact(), socialModel.getTelegramLink());
                                }

                                String username = ((EditText) popupEditSocial.findViewById(R.id.dataToEdit)).getText().toString();
                                char firstSymbol = username.charAt(0);
                                String regex = "[0-9]+";
                                username = username.replaceAll("[-() ]", "");
                                if (((firstSymbol == '@' && !username.substring(1).matches(regex)) || (firstSymbol != '@' && !username.matches(regex))) && !username.contains("t.me/")) {
                                    username = "t.me/" + username;
                                }

                                socialModel.setTelegramLink(username);
                                ((EditText) popupEditSocial.findViewById(R.id.dataToEdit)).getText().clear();
                                //  ((ImageView) socialPopup.findViewById(R.id.telegram_icon)).setImageResource(R.drawable.icn_social_telegram);
                                //  ((ImageView) getActivity().findViewById(R.id.telegram_icon)).setImageResource(R.drawable.icn_social_telegram);
                                Drawable colort = new ColorDrawable(Color.parseColor("#7AA5DA"));
                                Drawable imaget = getResources().getDrawable(R.drawable.icn_social_telegram2);
                                LayerDrawable ldt = new LayerDrawable(new Drawable[]{colort, imaget});
                                ((CircleImageView) socialPopup.findViewById(R.id.telegram_icon)).setImageDrawable(ldt);



                                if (profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE) {
                                    ((CircleImageView) profilePopUp.findViewById(R.id.telegram_icon)).setImageDrawable(ldt);
                                } else if (companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE) {
                                    ((CircleImageView) companyProfilePopup.findViewById(R.id.telegram_icon)).setImageDrawable(ldt);
                                } else {
                                    ((CircleImageView) getActivity().findViewById(R.id.telegram_icon)).setImageDrawable(ldt);
                                }

                                //   if (contact.getSocialModel() != null)


                                contact.hasTelegram = true;
                                contact.setSocialModel(socialModel);
                                realm.commitTransaction();

                                String tel = contact.getSocialModel().getTelegramLink();
                                if (tel.contains(".me/")) {
                                    int indexx = tel.indexOf(".me/");
                                    String outLink = tel.substring(indexx + 4, tel.length());
                                    ((TextView) socialPopup.findViewById(R.id.telegram_text)).setText(outLink);
                                } else if (tel.contains("?p=")) {
                                    int indexx = tel.indexOf("?p=");
                                    String outLink = tel.substring(indexx + 3, tel.length());
                                    ((TextView) socialPopup.findViewById(R.id.telegram_text)).setText(outLink);

                                } else
                                    ((TextView) socialPopup.findViewById(R.id.telegram_text)).setText(contact.getSocialModel().getTelegramLink());

                                contactsService.addNoteToContact(contact.getIdContact(), contact.getSocialModel().getTelegramLink(), contact.getName());
                                ContactCacheService.updateContact(contact, mainView.getContext());
                                //  EventBus.getDefault().post(new UpdateFile());
                                popupEditSocial.setVisibility(View.GONE);
                                ((TextView) popupProfileEditPreviewSocial.findViewById(R.id.valueUpd)).setText(contact.getSocialModel().getTelegramLink());
                            } else {
                                if (socialModel.getTelegramLink() != null && !socialModel.getTelegramLink().isEmpty()) {
                                    contactsService.deleteNoteContact(contact.getIdContact(), socialModel.getTelegramLink());
                                }
                                socialModel.setTelegramLink(null);
                                ((EditText) popupEditSocial.findViewById(R.id.dataToEdit)).getText().clear();
                                //    ((ImageView) socialPopup.findViewById(R.id.telegram_icon)).setImageResource(R.drawable.icn_social_telegram_gray);
                                //    ((ImageView) getActivity().findViewById(R.id.telegram_icon)).setImageResource(R.drawable.icn_social_telegram_gray);
                                Drawable color7 = new ColorDrawable(Color.parseColor("#e2e5e8"));
                                Drawable image7 = getResources().getDrawable(R.drawable.icn_social_telegram2);
                                LayerDrawable ld7 = new LayerDrawable(new Drawable[]{color7, image7});
                                ((CircleImageView) socialPopup.findViewById(R.id.telegram_icon)).setImageDrawable(ld7);


                                if (profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE) {
                                    ((CircleImageView) profilePopUp.findViewById(R.id.telegram_icon)).setImageDrawable(ld7);
                                } else if (companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE) {
                                    ((CircleImageView) companyProfilePopup.findViewById(R.id.telegram_icon)).setImageDrawable(ld7);
                                } else {
                                    ((CircleImageView) getActivity().findViewById(R.id.telegram_icon)).setImageDrawable(ld7);
                                }

                                ((TextView) socialPopup.findViewById(R.id.telegram_text)).setText("add new link");
                                contact.hasTelegram = false;
                                contact.setSocialModel(socialModel);
                                realm.commitTransaction();
                                ContactCacheService.updateContact(contact, mainView.getContext());
                                //  EventBus.getDefault().post(new UpdateFile());
                                popupEditSocial.setVisibility(View.GONE);
                                popupProfileEditPreviewSocial.setVisibility(View.GONE);
                            }


                            EventBus.getDefault().post(new UpdateFile());


                        } else if (typeEnum.equals(SocialEnums.SKYPE)) {


                            realm.beginTransaction();
                            SocialModel socialModel;
                            if (contact.getSocialModel() == null) {
                                socialModel = realm.createObject(SocialModel.class);
                            } else
                                socialModel = contact.getSocialModel();

                            if (((EditText) popupEditSocial.findViewById(R.id.dataToEdit)).getText().length() > 0) {
                                //     if (contact.getSocialModel() != null)
                                //     contact.getSocialModel().setSkypeLink(((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().toString());
                                //     else

                                if (socialModel.getSkypeLink() != null && !socialModel.getSkypeLink().isEmpty()) {
                                    contactsService.deleteNoteContact(contact.getIdContact(), "Skype: " + socialModel.getSkypeLink());
                                }

                                socialModel.setSkypeLink(((EditText) popupEditSocial.findViewById(R.id.dataToEdit)).getText().toString());

                                ((EditText) popupEditSocial.findViewById(R.id.dataToEdit)).getText().clear();
                                //    ((ImageView) socialPopup.findViewById(R.id.skype_icon)).setImageResource(R.drawable.icn_social_skype);
                                //    ((ImageView) getActivity().findViewById(R.id.skype_icon)).setImageResource(R.drawable.icn_social_skype);
                                Drawable colors = new ColorDrawable(Color.parseColor("#1eb8ff"));
                                Drawable images = getResources().getDrawable(R.drawable.icn_social_skype2);
                                LayerDrawable lds = new LayerDrawable(new Drawable[]{colors, images});
                                ((CircleImageView) socialPopup.findViewById(R.id.skype_icon)).setImageDrawable(lds);


                                if (profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE) {
                                    ((CircleImageView) profilePopUp.findViewById(R.id.skype_icon)).setImageDrawable(lds);
                                } else if (companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE) {
                                    ((CircleImageView) companyProfilePopup.findViewById(R.id.skype_icon)).setImageDrawable(lds);
                                } else {
                                    ((CircleImageView) getActivity().findViewById(R.id.skype_icon)).setImageDrawable(lds);
                                }

                                //    if (contact.getSocialModel() != null)
                                //    ((TextView) socialPopup.findViewById(R.id.skype_text)).setText(contact.getSocialModel().getSkypeLink());
                                //    else
                                ((TextView) socialPopup.findViewById(R.id.skype_text)).setText(socialModel.getSkypeLink());
                                contact.hasSkype = true;
                                contact.setSocialModel(socialModel);
                                realm.commitTransaction();
                                contactsService.addNoteToContact(contact.getIdContact(), "Skype: " + contact.getSocialModel().getSkypeLink(), contact.getName());
                                ContactCacheService.updateContact(contact, mainView.getContext());
                                //  EventBus.getDefault().post(new UpdateFile());
                                popupEditSocial.setVisibility(View.GONE);
                                ((TextView) popupProfileEditPreviewSocial.findViewById(R.id.valueUpd)).setText(contact.getSocialModel().getSkypeLink());
                            } else {
                                if (socialModel.getSkypeLink() != null && !socialModel.getSkypeLink().isEmpty()) {
                                    contactsService.deleteNoteContact(contact.getIdContact(), "Skype: " + socialModel.getSkypeLink());
                                }
                                socialModel.setSkypeLink(null);
                                ((EditText) popupEditSocial.findViewById(R.id.dataToEdit)).getText().clear();
                                //    ((ImageView) socialPopup.findViewById(R.id.skype_icon)).setImageResource(R.drawable.icn_social_skype_gray);
                                //    ((ImageView) getActivity().findViewById(R.id.skype_icon)).setImageResource(R.drawable.icn_social_skype_gray);
                                Drawable color8 = new ColorDrawable(Color.parseColor("#e2e5e8"));
                                Drawable image8 = getResources().getDrawable(R.drawable.icn_social_skype2_gray);
                                LayerDrawable ld8 = new LayerDrawable(new Drawable[]{color8, image8});
                                ((CircleImageView) socialPopup.findViewById(R.id.skype_icon)).setImageDrawable(ld8);


                                if (profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE) {
                                    ((CircleImageView) profilePopUp.findViewById(R.id.skype_icon)).setImageDrawable(ld8);
                                } else if (companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE) {
                                    ((CircleImageView) companyProfilePopup.findViewById(R.id.skype_icon)).setImageDrawable(ld8);
                                } else {
                                    ((CircleImageView) getActivity().findViewById(R.id.skype_icon)).setImageDrawable(ld8);
                                }

                                ((TextView) socialPopup.findViewById(R.id.skype_text)).setText("add new link");
                                contact.hasSkype = false;
                                contact.setSocialModel(socialModel);
                                realm.commitTransaction();
                                ContactCacheService.updateContact(contact, mainView.getContext());
                                //    EventBus.getDefault().post(new UpdateFile());
                                popupEditSocial.setVisibility(View.GONE);
                                popupProfileEditPreviewSocial.setVisibility(View.GONE);
                            }


                            EventBus.getDefault().post(new UpdateFile());

                        } else if (typeEnum.equals(SocialEnums.TWITTER)) {


                            realm.beginTransaction();
                            SocialModel socialModel;
                            if (contact.getSocialModel() == null) {
                                socialModel = realm.createObject(SocialModel.class);
                            } else
                                socialModel = contact.getSocialModel();

                            if (((EditText) popupEditSocial.findViewById(R.id.dataToEdit)).getText().length() > 0) {
                                //     if (contact.getSocialModel() != null)

                                if (socialModel.getTwitterLink() != null && !socialModel.getTwitterLink().isEmpty()) {
                                    contactsService.deleteNoteContact(contact.getIdContact(), socialModel.getTwitterLink());
                                }

                                String f = ((EditText) popupEditSocial.findViewById(R.id.dataToEdit)).getText().toString();

                                if (!f.contains(".com/")) {
                                    f = "https://twitter.com/" + f.trim();
                                }

                                if (ClipboardType.isTwitter(f)) {
                                    socialModel.setTwitterLink(f);
                                } else {
                                    socialModel.setTwitterLink(((EditText) popupEditSocial.findViewById(R.id.dataToEdit)).getText().toString());
                                }


                                ((ImageView) socialPopup.findViewById(R.id.twitter_icon)).setImageDrawable(getResources().getDrawable(R.drawable.ic_twitter_64));

                                ((ImageView) getActivity().findViewById(R.id.vk_icon)).setImageDrawable(getResources().getDrawable(R.drawable.ic_twitter_64));

                                if (socialModel.getTwitterLink().contains(".com/")) {
                                    ((TextView) socialPopup.findViewById(R.id.twitterNick)).setText(socialModel.getTwitterLink().substring(socialModel.getTwitterLink().indexOf(".com/") + 5));
                                } else
                                    ((TextView) socialPopup.findViewById(R.id.twitterNick)).setText(socialModel.getTwitterLink());
                                //}

                                //        if (contact.getSocialModel() != null)
                                //    ((TextView) socialPopup.findViewById(R.id.vkNick)).setText(socialModel.getVkLink());
                                contact.hasTwitter = true;
                                contact.setSocialModel(socialModel);
                                realm.commitTransaction();
                                contactsService.addNoteToContact(contact.getIdContact(), contact.getSocialModel().getTwitterLink(), contact.getName());
                                ContactCacheService.updateContact(contact, mainView.getContext());
                                // EventBus.getDefault().post(new UpdateFile());
                                popupEditSocial.setVisibility(View.GONE);
                                ((TextView) popupProfileEditPreviewSocial.findViewById(R.id.valueUpd)).setText(contact.getSocialModel().getTwitterLink());
                            } else {
                                if (socialModel.getTwitterLink() != null && !socialModel.getTwitterLink().isEmpty()) {
                                    contactsService.deleteNoteContact(contact.getIdContact(), socialModel.getTwitterLink());
                                }
                                contact.getSocialModel().setTwitterLink(null);
                                //     ((ImageView) socialPopup.findViewById(R.id.vk_icon)).setImageResource(R.drawable.icn_social_vk_gray);
                                //     ((ImageView) getActivity().findViewById(R.id.vk_icon)).setImageResource(R.drawable.icn_social_vk_gray);
                                Drawable color2 = new ColorDrawable(Color.parseColor("#e2e5e8"));
                                Drawable image2 = getResources().getDrawable(R.drawable.ic_twitter_white);
                                LayerDrawable ld2 = new LayerDrawable(new Drawable[]{color2, image2});
                                ((ImageView) socialPopup.findViewById(R.id.twitter_icon)).setImageDrawable(ld2);

                                ((ImageView) getActivity().findViewById(R.id.vk_icon)).setImageDrawable(ld2);



                                ((TextView) socialPopup.findViewById(R.id.twitterNick)).setText("add username or link");
                                contact.hasTwitter = false;
                                contact.setSocialModel(socialModel);
                                realm.commitTransaction();
                                ContactCacheService.updateContact(contact, mainView.getContext());
                                //  EventBus.getDefault().post(new UpdateFile());
                                popupEditSocial.setVisibility(View.GONE);
                                popupProfileEditPreviewSocial.setVisibility(View.GONE);
                            }



                            EventBus.getDefault().post(new UpdateFile());
                        } else if (typeEnum.equals(SocialEnums.YOUTUBE)) {


                            realm.beginTransaction();
                            SocialModel socialModel;
                            if (contact.getSocialModel() == null) {
                                socialModel = realm.createObject(SocialModel.class);
                            } else
                                socialModel = contact.getSocialModel();

                            if (((EditText) popupEditSocial.findViewById(R.id.dataToEdit)).getText().length() > 0) {
                                //     if (contact.getSocialModel() != null)

                                if (socialModel.getYoutubeLink() != null && !socialModel.getYoutubeLink().isEmpty()) {
                                    contactsService.deleteNoteContact(contact.getIdContact(), socialModel.getYoutubeLink());
                                }

                                String f = ((EditText) popupEditSocial.findViewById(R.id.dataToEdit)).getText().toString();

                                if (!f.contains(".com/")) {
                                    f = "https://www.youtube.com/channel/" + f;
                                }

                                if (ClipboardType.isYoutube(f)) {
                                    socialModel.setYoutubeLink(f);
                                } else {
                                    socialModel.setYoutubeLink(((EditText) popupEditSocial.findViewById(R.id.dataToEdit)).getText().toString());
                                }


                                Drawable colorv = new ColorDrawable(Color.parseColor("#ed2524"));
                                Drawable imagev = getResources().getDrawable(R.drawable.ic_youtube_white);
                                LayerDrawable ldv = new LayerDrawable(new Drawable[]{colorv, imagev});
                                ((ImageView) socialPopup.findViewById(R.id.youtube_icon)).setImageDrawable(ldv);


                                if (socialModel.getYoutubeLink().contains("user/") || socialModel.getYoutubeLink().contains("channel/")) {
                                    if (socialModel.getYoutubeLink().contains("user/")) {
                                        String text = socialModel.getYoutubeLink().substring(socialModel.getYoutubeLink().indexOf("user/") + 5);
                                        ((TextView) socialPopup.findViewById(R.id.youtubeNick)).setText(text);
                                    } else if (socialModel.getYoutubeLink().contains("channel/")) {
                                        String text = socialModel.getYoutubeLink().substring(socialModel.getYoutubeLink().indexOf("channel/") + 8);
                                        ((TextView) socialPopup.findViewById(R.id.youtubeNick)).setText(text);
                                    }
                                } else
                                    ((TextView) socialPopup.findViewById(R.id.youtubeNick)).setText(socialModel.getYoutubeLink());
                                //}

                                //        if (contact.getSocialModel() != null)
                                //    ((TextView) socialPopup.findViewById(R.id.vkNick)).setText(socialModel.getVkLink());
                                contact.hasYoutube = true;
                                contact.setSocialModel(socialModel);
                                realm.commitTransaction();
                                contactsService.addNoteToContact(contact.getIdContact(), contact.getSocialModel().getYoutubeLink(), contact.getName());
                                ContactCacheService.updateContact(contact, mainView.getContext());
                                // EventBus.getDefault().post(new UpdateFile());
                                popupEditSocial.setVisibility(View.GONE);
                                ((TextView) popupProfileEditPreviewSocial.findViewById(R.id.valueUpd)).setText(contact.getSocialModel().getYoutubeLink());
                            } else {
                                if (socialModel.getYoutubeLink() != null && !socialModel.getYoutubeLink().isEmpty()) {
                                    contactsService.deleteNoteContact(contact.getIdContact(), socialModel.getYoutubeLink());
                                }
                                contact.getSocialModel().setYoutubeLink(null);
                                //     ((ImageView) socialPopup.findViewById(R.id.vk_icon)).setImageResource(R.drawable.icn_social_vk_gray);
                                //     ((ImageView) getActivity().findViewById(R.id.vk_icon)).setImageResource(R.drawable.icn_social_vk_gray);
                                Drawable color2 = new ColorDrawable(Color.parseColor("#e2e5e8"));
                                Drawable image2 = getResources().getDrawable(R.drawable.ic_youtube_white);
                                LayerDrawable ld2 = new LayerDrawable(new Drawable[]{color2, image2});
                                ((ImageView) socialPopup.findViewById(R.id.youtube_icon)).setImageDrawable(ld2);


                                ((TextView) socialPopup.findViewById(R.id.youtubeNick)).setText("add username or link");
                                contact.hasYoutube = false;
                                contact.setSocialModel(socialModel);
                                realm.commitTransaction();
                                ContactCacheService.updateContact(contact, mainView.getContext());
                                //  EventBus.getDefault().post(new UpdateFile());
                                popupEditSocial.setVisibility(View.GONE);
                                popupProfileEditPreviewSocial.setVisibility(View.GONE);
                            }



                            EventBus.getDefault().post(new UpdateFile());

                        } else if (typeEnum.equals(SocialEnums.MEDIUM)) {


                            realm.beginTransaction();
                            SocialModel socialModel;
                            if (contact.getSocialModel() == null) {
                                socialModel = realm.createObject(SocialModel.class);
                            } else
                                socialModel = contact.getSocialModel();

                            if (((EditText) popupEditSocial.findViewById(R.id.dataToEdit)).getText().length() > 0) {
                                //  if (contact.getSocialModel() != null)

                                if (socialModel.getMediumLink() != null && !socialModel.getMediumLink().isEmpty()) {
                                    contactsService.deleteNoteContact(contact.getIdContact(), socialModel.getMediumLink());
                                }



                                String username = ((EditText) popupEditSocial.findViewById(R.id.dataToEdit)).getText().toString();

                                if (username.contains("?utm")) {
                                    int ind = username.indexOf('?');
                                    if (ind != -1)
                                        username = username.substring(0, ind);

                                }

                                if (username.charAt(username.length() - 1) == '/') {
                                    username = username.substring(0, username.length() - 1);
                                }


                                if (!username.contains("medium.com")) {
                                    if (!username.contains("@"))
                                        username = "https://medium.com/@" + username;
                                    else
                                        username = "https://medium.com/" + username;
                                }

                                socialModel.setMediumLink(username);
                                //    ((ImageView) socialPopup.findViewById(R.id.instagram_icon)).setImageResource(R.drawable.icn_social_instagram);
                                //    ((ImageView) getActivity().findViewById(R.id.inst_icon)).setImageResource(R.drawable.icn_social_instagram);
                                Drawable colori = new ColorDrawable(Color.parseColor("#000000"));
                                Drawable imagei = getResources().getDrawable(R.drawable.medium_white);
                                LayerDrawable ldi = new LayerDrawable(new Drawable[]{colori, imagei});
                                ((ImageView) socialPopup.findViewById(R.id.medium_icon)).setImageDrawable(ldi);


                                contact.hasMedium = true;
                                contact.setSocialModel(socialModel);
                                realm.commitTransaction();

                                String inst = contact.getSocialModel().getMediumLink();

                                if (inst.contains(".com/")) {
                                    int ind = inst.indexOf(".com/");
                                    String outLink = inst.substring(ind + 5, inst.length());


                                    if (outLink.contains("?")) {
                                        int in = outLink.indexOf("?");
                                        outLink = outLink.substring(0, in);
                                    }

                                    ((TextView) socialPopup.findViewById(R.id.mediumNick)).setText(outLink);
                                } else
                                    ((TextView) socialPopup.findViewById(R.id.mediumNick)).setText(contact.getSocialModel().getMediumLink());

                                contactsService.addNoteToContact(contact.getIdContact(), contact.getSocialModel().getMediumLink(), contact.getName());
                                ContactCacheService.updateContact(contact, mainView.getContext());
                                //  EventBus.getDefault().post(new UpdateFile());
                                popupEditSocial.setVisibility(View.GONE);
                                ((TextView) popupProfileEditPreviewSocial.findViewById(R.id.valueUpd)).setText(contact.getSocialModel().getMediumLink());
                            } else {
                                if (socialModel.getMediumLink() != null && !socialModel.getMediumLink().isEmpty()) {
                                    contactsService.deleteNoteContact(contact.getIdContact(), socialModel.getMediumLink());
                                }
                                socialModel.setMediumLink(null);
                                //         ((ImageView) socialPopup.findViewById(R.id.instagram_icon)).setImageResource(R.drawable.icn_social_instagram_gray);
                                //         ((ImageView) getActivity().findViewById(R.id.inst_icon)).setImageResource(R.drawable.icn_social_instagram_gray);
                                Drawable color4 = new ColorDrawable(Color.parseColor("#e2e5e8"));
                                Drawable image4 = getResources().getDrawable(R.drawable.medium_white);
                                LayerDrawable ld4 = new LayerDrawable(new Drawable[]{color4, image4});
                                ((ImageView) socialPopup.findViewById(R.id.medium_icon)).setImageDrawable(ld4);



                                ((TextView) socialPopup.findViewById(R.id.mediumNick)).setText("add new link");
                                contact.hasMedium = false;
                                contact.setSocialModel(socialModel);
                                realm.commitTransaction();

                                ContactCacheService.updateContact(contact, mainView.getContext());
                                //    EventBus.getDefault().post(new UpdateFile());
                                popupEditSocial.setVisibility(View.GONE);
                                popupProfileEditPreviewSocial.setVisibility(View.GONE);
                            }


                            EventBus.getDefault().post(new UpdateFile());


                        }

                        realm.close();


                        initIconColor(contact, mainView);

                    }
                });

                popupEditSocial.findViewById(R.id.cancelEditField).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        popupEditSocial.setVisibility(View.GONE);
                    }
                });
            }
        });

        popupProfileEditPreviewSocial.findViewById(R.id.editPopupCall).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (typeEnum.equals(SocialEnums.FACEBOOK)) {
                    if (!contact.hasFacebook) {
                        closeChildPopups();
                        showSocialPopup(contact);
                        socialPopup.findViewById(R.id.facebook_social).callOnClick();
                        socialPopup.setVisibility(View.GONE);
                    } else {

                        Intent intent;
                        if (contact.getSocialModel().getFacebookLink().contains("?id=")) {
                            String idProfile = contact.getSocialModel().getFacebookLink().substring(contact.getSocialModel().getFacebookLink().indexOf('=') + 1, contact.getSocialModel().getFacebookLink().length());
                            if (idProfile.contains("&")) {
                                idProfile = idProfile.substring(0, idProfile.indexOf("&"));
                            }
                            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/" + idProfile));

                        } else if (contact.getSocialModel().getFacebookLink().contains("/people/")) {
                            String idProfile = contact.getSocialModel().getFacebookLink().substring(contact.getSocialModel().getFacebookLink().lastIndexOf('/') + 1, contact.getSocialModel().getFacebookLink().length());
                            if (idProfile.contains("&")) {
                                idProfile = idProfile.substring(0, idProfile.indexOf("&"));
                            }

                            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/" + idProfile));

                        } else
                            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://facewebmodal/f?href=" + contact.getSocialModel().getFacebookLink()));

                        //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://facewebmodal/f?href=" + contact.getSocialModel().getFacebookLink()));
                        PackageManager packageManager = getActivity().getPackageManager();
                        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
                        boolean isIntentSafe = activities.size() > 0;
                        if (!isIntentSafe) {
                            /*Intent intent2 = new Intent(Intent.ACTION_VIEW);
                            intent2.setData(Uri.parse("market://details?id=com.facebook.katana"));
                            startActivity(intent2);*/

                            try {
                                String uris = contact.getSocialModel().getFacebookLink();
                                if (!contact.getSocialModel().getFacebookLink().contains("https://") && !contact.getSocialModel().getFacebookLink().contains("http://"))
                                    uris = "https://" + uris;

                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(uris));
                                startActivity(i);
                            } catch (Exception e) {

                            }

                        } else
                            startActivity(intent);
                    }
                } else if (typeEnum.equals(SocialEnums.INSTAGRAM)) {
                    //============================================================
                    if (!contact.hasInst) {
                        //   closeOtherPopup();
                        closeChildPopups();
                        showSocialPopup(contact);
                        socialPopup.findViewById(R.id.instagramLink).callOnClick();
                        socialPopup.setVisibility(View.GONE);
                    } else {
                        System.out.println("Go Insta");

                        String str = contact.getSocialModel().getInstagramLink();
                        if (!str.toLowerCase().contains("instagram")) {
                            str = "https://instagram.com/" + contact.getSocialModel().getInstagramLink();
                        }

                        if (!str.contains("http://") && !str.contains("https://")) {
                            str = "https://" + contact.getSocialModel().getInstagramLink();
                        }

                        Uri uri = Uri.parse(str);
                        Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);
                        likeIng.setPackage("com.instagram.android");
                        PackageManager packageManager = getActivity().getPackageManager();
                        List<ResolveInfo> activities = packageManager.queryIntentActivities(likeIng, 0);
                        boolean isIntentSafe = activities.size() > 0;
                        if (!isIntentSafe) {
                            /*Intent intent2 = new Intent(Intent.ACTION_VIEW);
                            intent2.setData(Uri.parse("market://details?id=com.instagram.android"));
                            startActivity(intent2);*/
                            try {
                                String uris = contact.getSocialModel().getInstagramLink();
                                if (!contact.getSocialModel().getInstagramLink().contains("https://") && !contact.getSocialModel().getInstagramLink().contains("http://"))
                                    uris = "https://" + uris;

                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(uris));
                                startActivity(i);
                            } catch (Exception e) {

                            }
                        } else
                            startActivity(likeIng);
                    }
                    //=========
                } else if (typeEnum.equals(SocialEnums.VK)) {
                    //============================================================
                    if (!contact.hasVk) {
                        //    closeOtherPopup();
                        closeChildPopups();
                        showSocialPopup(contact);
                        socialPopup.findViewById(R.id.vk_link).callOnClick();
                        socialPopup.setVisibility(View.GONE);
                    } else {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format(contact.getSocialModel().getVkLink())));
                        Intent intent2 = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("vkontakte://profile/")));
                        try {
                            startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            e.printStackTrace();
                            PackageManager packageManager = getActivity().getPackageManager();
                            List<ResolveInfo> activities = packageManager.queryIntentActivities(intent2, 0);
                            boolean isIntentSafe = activities.size() > 0;
                            if (!isIntentSafe) {
                                /*Intent intent3 = new Intent(Intent.ACTION_VIEW);
                                intent3.setData(Uri.parse("market://details?id=com.vkontakte.android"));
                                startActivity(intent3);*/
                                try {
                                    String uris = contact.getSocialModel().getVkLink();
                                    if (!contact.getSocialModel().getVkLink().contains("https://") && !contact.getSocialModel().getVkLink().contains("http://"))
                                        uris = "https://" + uris;

                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                    i.setData(Uri.parse(uris));
                                    startActivity(i);
                                } catch (Exception e2) {

                                }
                            } else
                                startActivity(intent2);
                        }
                    }
                    //==============
                } else if (typeEnum.equals(SocialEnums.LINKEDIN)) {
                    //============================================================
                    if (!contact.hasLinked) {
                        //    closeOtherPopup();
                        closeChildPopups();
                        showSocialPopup(contact);
                        socialPopup.findViewById(R.id.linkedLink).callOnClick();
                        socialPopup.setVisibility(View.GONE);
                    } else {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(contact.getSocialModel().getLinkedInLink()));
                        PackageManager packageManager = getActivity().getPackageManager();
                        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
                        boolean isIntentSafe = activities.size() > 0;
                        if (!isIntentSafe) {
                            Intent intent2 = new Intent(Intent.ACTION_VIEW);
                            intent2.setData(Uri.parse("https://www.linkedin.com/in/"));
                            startActivity(intent2);
                        } else {
                            try {
                                startActivity(intent);
                            } catch (Exception e) {
                                Intent intent2 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/"));
                                startActivity(intent2);
                            }


                        }
                    }
                    //============
                } else if (typeEnum.equals(SocialEnums.VIBER)) {
                    //===================================================
                    if (!contact.hasViber) {
                        //   closeOtherPopup();
                        closeChildPopups();
                        showSocialPopup(contact);
                        socialPopup.findViewById(R.id.viberLink).callOnClick();
                        socialPopup.setVisibility(View.GONE);
                    } else {
                        Intent intent;
                        final String contactId = getContactIdFromPhoneNumber(String.valueOf(contact.getSocialModel().getViberLink()));
                        final String contactMimeTypeDataId = getContactMimeTypeDataId(getContext(), contactId, "vnd.android.cursor.item/vnd.com.viber.voip.viber_number_message");
                        if (contactMimeTypeDataId != null) {
                            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("content://com.android.contacts/data/" + contactMimeTypeDataId));
                            intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET | Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
                            intent.setPackage("com.viber.voip");
                        } else {
                            intent = new Intent("android.intent.action.VIEW", Uri.parse("tel:" + Uri.encode(String.valueOf(contact.getSocialModel().getViberLink()))));
                            intent.setClassName("com.viber.voip", "com.viber.voip.WelcomeActivity");
                        }

                        PackageManager packageManager = getActivity().getPackageManager();
                        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
                        boolean isIntentSafe = activities.size() > 0;
                        if (!isIntentSafe) {
                            Intent intent2 = new Intent(Intent.ACTION_VIEW);
                            intent2.setData(Uri.parse("market://details?id=com.viber.voip"));
                            startActivity(intent2);
                        } else
                            startActivity(intent);
                    }
                    //============
                } else if (typeEnum.equals(SocialEnums.WHATSAPP)) {
                    //=================================================
                    if (!contact.hasWhatsapp) {
                        closeChildPopups();
                        showSocialPopup(contact);
                        socialPopup.findViewById(R.id.whatsapp_link).callOnClick();
                        socialPopup.setVisibility(View.GONE);
                    } else {
                        Intent telegramIntent = new Intent(Intent.ACTION_VIEW);

                        String whatsappNum = contact.getSocialModel().getWhatsappLink();
                        if (/*whatsappNum.substring(0, 2).equalsIgnoreCase("+8") ||*/ whatsappNum.charAt(0) == '8') {
                            whatsappNum = whatsappNum.replaceFirst("8", "7");
                        }

                        if (whatsappNum.charAt(0) != '+') whatsappNum = "+" + whatsappNum;

                        telegramIntent.setData(Uri.parse("whatsapp://send?phone=" + whatsappNum));
                        PackageManager packageManager = getActivity().getPackageManager();
                        List<ResolveInfo> activities = packageManager.queryIntentActivities(telegramIntent, 0);
                        boolean isIntentSafe = activities.size() > 0;
                        if (!isIntentSafe) {
                            Intent intent2 = new Intent(Intent.ACTION_VIEW);
                            intent2.setData(Uri.parse("market://details?id=com.whatsapp"));
                            startActivity(intent2);
                        } else
                            startActivity(telegramIntent);
                    }
                    //========
                } else if (typeEnum.equals(SocialEnums.SKYPE)) {
                    //=====================================================================
                    if (!contact.hasSkype) {
                        //   closeOtherPopup();
                        closeChildPopups();
                        showSocialPopup(contact);
                        socialPopup.findViewById(R.id.skypeLink).callOnClick();
                        socialPopup.setVisibility(View.GONE);
                    } else {
                        Uri skypeUri = Uri.parse("skype:" + contact.getSocialModel().getSkypeLink() + "?chat");
                        Intent myIntent = new Intent(Intent.ACTION_VIEW, skypeUri);
                        myIntent.setComponent(new ComponentName("com.skype.raider", "com.skype.raider.Main"));
                        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        PackageManager packageManager = getActivity().getPackageManager();
                        List<ResolveInfo> activities = packageManager.queryIntentActivities(myIntent, 0);
                        boolean isIntentSafe = activities.size() > 0;
                        if (!isIntentSafe) {
                            Intent intent2 = new Intent(Intent.ACTION_VIEW);
                            intent2.setData(Uri.parse("market://details?id=com.skype.raider"));
                            startActivity(intent2);
                        } else
                            startActivity(myIntent);
                    }
                    //===========
                } else if (typeEnum.equals(SocialEnums.TELEGRAM)) {
                    //======================================================================
                    if ((!contact.hasTelegram) || (contact.hasTelegram && contact.getSocialModel() == null) || (contact.hasTelegram && contact.getSocialModel() != null && contact.getSocialModel().getTelegramLink() == null)) {
                        //  closeOtherPopup();
                        closeChildPopups();
                        showSocialPopup(contact);
                        socialPopup.findViewById(R.id.telegramLink).callOnClick();
                        socialPopup.setVisibility(View.GONE);
                    } else {

                        String username = contact.getSocialModel().getTelegramLink();
                        char firstSymbol = username.charAt(0);
                        String regex = "[0-9]+";
                        username = username.replaceAll("[-() ]", "");
                        if ((firstSymbol == '+' && username.substring(1).matches(regex)) || (firstSymbol != '+' && username.matches(regex))) {
                            //final String contactId = getContactIdFromPhoneNumber(String.valueOf(contact.getSocialModel().getTelegramLink()));
                            final String contactId = contact.getIdContact();
                            final String contactMimeTypeDataId = getContactMimeTypeDataId(getContext(), contactId, "vnd.android.cursor.item/vnd.org.telegram.messenger.android.profile");
                            if (contactMimeTypeDataId != null) {
                                Intent intent;
                                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("content://com.android.contacts/data/" + contactMimeTypeDataId));
                                intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET | Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
                                intent.setPackage("org.telegram.messenger");
                                startActivity(intent);
                            } else {
                                Intent telegramIntent = new Intent(Intent.ACTION_VIEW);
                                telegramIntent.setData(Uri.parse("tg://resolve?domain=" + username));
                                PackageManager packageManager = getActivity().getPackageManager();
                                List<ResolveInfo> activities = packageManager.queryIntentActivities(telegramIntent, 0);
                                boolean isIntentSafe = activities.size() > 0;
                                if (!isIntentSafe) {
                                    Intent intent3 = new Intent(Intent.ACTION_VIEW);
                                    intent3.setData(Uri.parse("market://details?id=org.telegram.messenger"));
                                    startActivity(intent3);
                                } else
                                    startActivity(telegramIntent);

                            }

                        } else if ((firstSymbol == '@' && !username.substring(1).matches(regex)) || (firstSymbol != '@' && !username.matches(regex))) {
                            Intent telegramIntent = new Intent(Intent.ACTION_VIEW);

                            if (firstSymbol == '@')
                                username = username.substring(1);
                            else if (username.contains("t.me/") && !username.contains("@"))
                                username = username.substring(5);
                            else if (username.contains("t.me/@"))
                                username = username.substring(6);

                            telegramIntent.setData(Uri.parse("tg://resolve?domain=" + username));
                            PackageManager packageManager = getActivity().getPackageManager();
                            List<ResolveInfo> activities = packageManager.queryIntentActivities(telegramIntent, 0);
                            boolean isIntentSafe = activities.size() > 0;
                            if (!isIntentSafe) {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse("market://details?id=org.telegram.messenger"));
                                startActivity(intent);
                            } else
                                startActivity(telegramIntent);
                        }


                    }
                    //==============
                } else if (typeEnum.equals(SocialEnums.TWITTER)) {
                    //============================================================
                    if (!contact.hasTwitter) {
                        //    closeOtherPopup();
                        closeChildPopups();
                        showSocialPopup(contact);
                        socialPopup.findViewById(R.id.twitter_link).callOnClick();
                        socialPopup.setVisibility(View.GONE);
                    } else {
                        String text = contact.getSocialModel().getTwitterLink();
                        if (text.contains("twitter.com/")) {
                            text = text.substring(text.indexOf(".com/") + 5);
                        }
                        if(text.length() > 0 && text.charAt(0) == '@') text = text.substring(1);
                        Intent intent = null;
                        try {
                            // get the Twitter app if possible

                            getActivity().getPackageManager().getPackageInfo("com.twitter.android", 0);
                            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + text));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        } catch (Exception e) {
                            // no Twitter app, revert to browser
                            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/" + text));
                        }
                        getActivity().startActivity(intent);


                    }
                    //==============
                } else if (typeEnum.equals(SocialEnums.YOUTUBE)) {
                    //============================================================
                    if (!contact.hasYoutube) {
                        //    closeOtherPopup();
                        closeChildPopups();
                        showSocialPopup(contact);
                        socialPopup.findViewById(R.id.youtube_link).callOnClick();
                        socialPopup.setVisibility(View.GONE);
                    } else {
                        String text = contact.getSocialModel().getYoutubeLink();
                        if (text.contains("youtu.be/") || text.contains("watch?v=")) {
                            if (text.contains("youtu.be/"))
                                text = text.substring(text.indexOf("youtu.be/") + 9);
                            else if (text.contains("watch?v="))
                                text = text.substring(text.indexOf("watch?v=") + 8);

                            Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + text));

                            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("http://www.youtube.com/watch?v=" + text));

                            try {
                                getContext().startActivity(appIntent);
                            } catch (ActivityNotFoundException ex) {
                                getContext().startActivity(webIntent);
                            }
                        } else if (text.contains("user/") || text.contains("channel/") || text.contains("/c/")) {
                            try {
                                getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(text)));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }


                    }
                    //==============
                } else if (typeEnum.equals(SocialEnums.MEDIUM)) {
                    //============================================================
                    if (!contact.hasMedium) {
                        //    closeOtherPopup();
                        closeChildPopups();
                        showSocialPopup(contact);
                        socialPopup.findViewById(R.id.medium_link).callOnClick();
                        socialPopup.setVisibility(View.GONE);
                    } else {

                        try {
                            String uri = contact.getSocialModel().getMediumLink();

                            if (!uri.contains("https://") && !uri.contains("http://"))
                                uri = "https://" + uri;

                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(uri));
                            getContext().startActivity(i);
                        } catch (Exception e) {

                        }
                    }
                    //==============
                }
            }
        });

        popupProfileEditPreviewSocial.findViewById(R.id.delete_contact_number).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        getActivity());
                alertDialogBuilder.setTitle("Do you want to delete " + ((TextView) popupProfileEditPreviewSocial.findViewById(R.id.typeField)).getText().toString().toLowerCase().trim() + "?");
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Yes", (dialog, id) -> {

                            Realm realm = Realm.getDefaultInstance(); //-

                            if (typeEnum.equals(SocialEnums.FACEBOOK)) {


                                if (socialModel.getFacebookLink() != null && !socialModel.getFacebookLink().isEmpty()) {
                                    contactsService.deleteNoteContact(contact.getIdContact(), socialModel.getFacebookLink());
                                }
                                realm.beginTransaction();
                                contact.getSocialModel().setFacebookLink(null);
                                realm.commitTransaction();
                                Drawable color = new ColorDrawable(Color.parseColor("#e2e5e8"));
                                Drawable image = getResources().getDrawable(R.drawable.icn_social_facebook2);
                                LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
                                ((ImageView) socialPopup.findViewById(R.id.facebook_icon)).setImageDrawable(ld);


                                if (profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE) {
                                    ((ImageView) profilePopUp.findViewById(R.id.facebook_icon)).setImageDrawable(ld);
                                } else if (companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE) {
                                    ((ImageView) companyProfilePopup.findViewById(R.id.facebook_icon)).setImageDrawable(ld);
                                } else {
                                    ((ImageView) getActivity().findViewById(R.id.facebook_icon)).setImageDrawable(ld);
                                }


                                ((TextView) socialPopup.findViewById(R.id.facebook_nick)).setText("add username or link");
                                realm.beginTransaction();
                                contact.hasFacebook = false;
                                contact.setSocialModel(socialModel);
                                realm.commitTransaction();

                                ContactCacheService.updateContact(contact, mainView.getContext());
                                // EventBus.getDefault().post(new UpdateFile());


                            } else if (typeEnum.equals(SocialEnums.VK)) {
                                if (socialModel.getVkLink() != null && !socialModel.getVkLink().isEmpty()) {
                                    contactsService.deleteNoteContact(contact.getIdContact(), socialModel.getVkLink());
                                }
                                realm.beginTransaction();
                                contact.getSocialModel().setVkLink(null);
                                realm.commitTransaction();
                                //     ((ImageView) socialPopup.findViewById(R.id.vk_icon)).setImageResource(R.drawable.icn_social_vk_gray);
                                //     ((ImageView) getActivity().findViewById(R.id.vk_icon)).setImageResource(R.drawable.icn_social_vk_gray);
                                Drawable color2 = new ColorDrawable(Color.parseColor("#e2e5e8"));
                                Drawable image2 = getResources().getDrawable(R.drawable.icn_social_vk2);
                                LayerDrawable ld2 = new LayerDrawable(new Drawable[]{color2, image2});
                                ((ImageView) socialPopup.findViewById(R.id.vk_icon)).setImageDrawable(ld2);


                                if (profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE) {
                                    ((ImageView) profilePopUp.findViewById(R.id.vk_icon)).setImageDrawable(ld2);
                                } else if (companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE) {
                                    ((ImageView) companyProfilePopup.findViewById(R.id.vk_icon)).setImageDrawable(ld2);
                                } else {
                                    ((ImageView) getActivity().findViewById(R.id.vk_icon)).setImageDrawable(ld2);
                                }


                                ((TextView) socialPopup.findViewById(R.id.vkNick)).setText("add username or");
                                realm.beginTransaction();
                                contact.hasVk = false;
                                contact.setSocialModel(socialModel);
                                realm.commitTransaction();
                                ContactCacheService.updateContact(contact, mainView.getContext());
                                //  EventBus.getDefault().post(new UpdateFile());
                                popupProfileEditPreviewSocial.setVisibility(View.GONE);

                            } else if (typeEnum.equals(SocialEnums.INSTAGRAM)) {
                                if (socialModel.getInstagramLink() != null && !socialModel.getInstagramLink().isEmpty()) {
                                    contactsService.deleteNoteContact(contact.getIdContact(), socialModel.getInstagramLink());
                                }
                                realm.beginTransaction();
                                socialModel.setInstagramLink(null);
                                realm.commitTransaction();
                                //         ((ImageView) socialPopup.findViewById(R.id.instagram_icon)).setImageResource(R.drawable.icn_social_instagram_gray);
                                //         ((ImageView) getActivity().findViewById(R.id.inst_icon)).setImageResource(R.drawable.icn_social_instagram_gray);
                                Drawable color4 = new ColorDrawable(Color.parseColor("#e2e5e8"));
                                Drawable image4 = getResources().getDrawable(R.drawable.icn_social_ints2);
                                LayerDrawable ld4 = new LayerDrawable(new Drawable[]{color4, image4});
                                ((ImageView) socialPopup.findViewById(R.id.instagram_icon)).setImageDrawable(ld4);
                          /*  ((ImageView) getActivity().findViewById(R.id.inst_icon)).setImageDrawable(ld4);

                            try{
                                ((ImageView) profilePopUp.findViewById(R.id.inst_icon)).setImageDrawable(ld4);
                            }catch (Exception e){

                            }

                            try{
                                ((ImageView) companyProfilePopup.findViewById(R.id.inst_icon)).setImageDrawable(ld4);
                            }catch (Exception e){

                            }*/

                                if (profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE) {
                                    ((ImageView) profilePopUp.findViewById(R.id.inst_icon)).setImageDrawable(ld4);
                                } else if (companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE) {
                                    ((ImageView) companyProfilePopup.findViewById(R.id.inst_icon)).setImageDrawable(ld4);
                                } else {
                                    ((ImageView) getActivity().findViewById(R.id.inst_icon)).setImageDrawable(ld4);
                                }


                                ((TextView) socialPopup.findViewById(R.id.instagramNick)).setText("add new link");
                                realm.beginTransaction();
                                contact.hasInst = false;
                                contact.setSocialModel(socialModel);
                                realm.commitTransaction();

                                ContactCacheService.updateContact(contact, mainView.getContext());
                                //    EventBus.getDefault().post(new UpdateFile());

                                popupProfileEditPreviewSocial.setVisibility(View.GONE);

                            } else if (typeEnum.equals(SocialEnums.LINKEDIN)) {
                                if (socialModel.getLinkedInLink() != null && !socialModel.getLinkedInLink().isEmpty()) {
                                    contactsService.deleteNoteContact(contact.getIdContact(), socialModel.getLinkedInLink());
                                }
                                realm.beginTransaction();
                                contact.getSocialModel().setLinkedInLink(null);
                                realm.commitTransaction();
                                //     ((ImageView) socialPopup.findViewById(R.id.link_icon)).setImageResource(R.drawable.icn_social_linkedin_gray);
                                //     ((ImageView) getActivity().findViewById(R.id.link_icon)).setImageResource(R.drawable.icn_social_linkedin_gray);
                                Drawable color3 = new ColorDrawable(Color.parseColor("#e2e5e8"));
                                Drawable image3 = getResources().getDrawable(R.drawable.icn_social_linked2);
                                LayerDrawable ld3 = new LayerDrawable(new Drawable[]{color3, image3});
                                ((ImageView) socialPopup.findViewById(R.id.link_icon)).setImageDrawable(ld3);
                           /* ((ImageView) getActivity().findViewById(R.id.link_icon)).setImageDrawable(ld3);

                            try{
                                ((ImageView) profilePopUp.findViewById(R.id.link_icon)).setImageDrawable(ld3);
                            }catch (Exception e){

                            }

                            try{
                                ((ImageView) companyProfilePopup.findViewById(R.id.link_icon)).setImageDrawable(ld3);
                            }catch (Exception e){

                            }*/

                                if (profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE) {
                                    ((ImageView) profilePopUp.findViewById(R.id.link_icon)).setImageDrawable(ld3);
                                } else if (companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE) {
                                    ((ImageView) companyProfilePopup.findViewById(R.id.link_icon)).setImageDrawable(ld3);
                                } else {
                                    ((ImageView) getActivity().findViewById(R.id.link_icon)).setImageDrawable(ld3);
                                }

                                ((TextView) socialPopup.findViewById(R.id.link_text)).setText("add new link");
                                realm.beginTransaction();
                                contact.hasLinked = false;
                                contact.setSocialModel(socialModel);
                                realm.commitTransaction();
                                ContactCacheService.updateContact(contact, mainView.getContext());
                                //  EventBus.getDefault().post(new UpdateFile());


                                popupProfileEditPreviewSocial.setVisibility(View.GONE);
                            } else if (typeEnum.equals(SocialEnums.VIBER)) {
                                if (socialModel.getViberLink() != null && !socialModel.getViberLink().isEmpty()) {
                                    contactsService.deleteNoteContact(contact.getIdContact(), socialModel.getViberLink());
                                }
                                realm.beginTransaction();
                                socialModel.setViberLink(null);
                                realm.commitTransaction();

                                if (popupEditSocial != null)
                                    ((EditText) popupEditSocial.findViewById(R.id.dataToEdit)).getText().clear();
                                //      ((ImageView) socialPopup.findViewById(R.id.viber_icon)).setImageResource(R.drawable.icn_social_viber_gray);
                                //      ((ImageView) getActivity().findViewById(R.id.viber_icon)).setImageResource(R.drawable.icn_social_viber_gray);
                                Drawable color5 = new ColorDrawable(Color.parseColor("#e2e5e8"));
                                Drawable image5 = getResources().getDrawable(R.drawable.icn_social_viber2_gray);
                                LayerDrawable ld5 = new LayerDrawable(new Drawable[]{color5, image5});
                                ((CircleImageView) socialPopup.findViewById(R.id.viber_icon)).setImageDrawable(ld5);
                       /*     ((CircleImageView) getActivity().findViewById(R.id.viber_icon)).setImageDrawable(ld5);

                            try{
                                ((CircleImageView) profilePopUp.findViewById(R.id.viber_icon)).setImageDrawable(ld5);
                            }catch (Exception e){

                            }

                            try{
                                ((CircleImageView) companyProfilePopup.findViewById(R.id.viber_icon)).setImageDrawable(ld5);
                            }catch (Exception e){

                            }*/

                                if (profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE) {
                                    ((CircleImageView) profilePopUp.findViewById(R.id.viber_icon)).setImageDrawable(ld5);
                                } else if (companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE) {
                                    ((CircleImageView) companyProfilePopup.findViewById(R.id.viber_icon)).setImageDrawable(ld5);
                                } else {
                                    ((CircleImageView) getActivity().findViewById(R.id.viber_icon)).setImageDrawable(ld5);
                                }

                                ((TextView) socialPopup.findViewById(R.id.viber_text)).setText("add new link");
                                realm.beginTransaction();
                                contact.hasViber = false;
                                contact.setSocialModel(socialModel);
                                realm.commitTransaction();
                                ContactCacheService.updateContact(contact, mainView.getContext());
                                //   EventBus.getDefault().post(new UpdateFile());

                                popupProfileEditPreviewSocial.setVisibility(View.GONE);
                            } else if (typeEnum.equals(SocialEnums.WHATSAPP)) {
                                if (socialModel.getWhatsappLink() != null && !socialModel.getWhatsappLink().isEmpty()) {
                                    contactsService.deleteNoteContact(contact.getIdContact(), socialModel.getWhatsappLink());
                                }
                                realm.beginTransaction();
                                socialModel.setWhatsappLink(null);
                                realm.commitTransaction();
                                //      contact.getSocialModel().setWhatsappLink(null);
                                if (popupEditSocial != null)
                                    ((EditText) popupEditSocial.findViewById(R.id.dataToEdit)).getText().clear();
                                //    ((ImageView) socialPopup.findViewById(R.id.whatsapp_icon)).setImageResource(R.drawable.icn_social_whatsapp_gray);
                                //    ((ImageView) getActivity().findViewById(R.id.whatsapp_icon)).setImageResource(R.drawable.icn_social_whatsapp_gray);
                                Drawable color6 = new ColorDrawable(Color.parseColor("#e2e5e8"));
                                Drawable image6 = getResources().getDrawable(R.drawable.icn_social_whatsapp3_gray);
                                LayerDrawable ld6 = new LayerDrawable(new Drawable[]{color6, image6});
                                ((CircleImageView) socialPopup.findViewById(R.id.whatsapp_icon)).setImageDrawable(ld6);
                         /*   ((CircleImageView) getActivity().findViewById(R.id.whatsapp_icon)).setImageDrawable(ld6);

                            try{
                                ((CircleImageView) profilePopUp.findViewById(R.id.whatsapp_icon)).setImageDrawable(ld6);
                            }catch (Exception e){

                            }

                            try{
                                ((CircleImageView) companyProfilePopup.findViewById(R.id.whatsapp_icon)).setImageDrawable(ld6);
                            }catch (Exception e){

                            }*/

                                if (profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE) {
                                    ((CircleImageView) profilePopUp.findViewById(R.id.whatsapp_icon)).setImageDrawable(ld6);
                                } else if (companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE) {
                                    ((CircleImageView) companyProfilePopup.findViewById(R.id.whatsapp_icon)).setImageDrawable(ld6);
                                } else {
                                    ((CircleImageView) getActivity().findViewById(R.id.whatsapp_icon)).setImageDrawable(ld6);
                                }


                                ((TextView) socialPopup.findViewById(R.id.whatsapp_text)).setText("add new link");
                                realm.beginTransaction();
                                contact.hasWhatsapp = false;
                                contact.setSocialModel(socialModel);
                                realm.commitTransaction();
                                ContactCacheService.updateContact(contact, mainView.getContext());
                                //  EventBus.getDefault().post(new UpdateFile());

                                popupProfileEditPreviewSocial.setVisibility(View.GONE);
                            } else if (typeEnum.equals(SocialEnums.TELEGRAM)) {
                                if (socialModel.getTelegramLink() != null && !socialModel.getTelegramLink().isEmpty()) {
                                    contactsService.deleteNoteContact(contact.getIdContact(), socialModel.getTelegramLink());
                                }
                                realm.beginTransaction();
                                socialModel.setTelegramLink(null);
                                realm.commitTransaction();
                                if (popupEditSocial != null)
                                    ((EditText) popupEditSocial.findViewById(R.id.dataToEdit)).getText().clear();
                                //    ((ImageView) socialPopup.findViewById(R.id.telegram_icon)).setImageResource(R.drawable.icn_social_telegram_gray);
                                //    ((ImageView) getActivity().findViewById(R.id.telegram_icon)).setImageResource(R.drawable.icn_social_telegram_gray);
                                Drawable color7 = new ColorDrawable(Color.parseColor("#e2e5e8"));
                                Drawable image7 = getResources().getDrawable(R.drawable.icn_social_telegram2);
                                LayerDrawable ld7 = new LayerDrawable(new Drawable[]{color7, image7});
                                ((CircleImageView) socialPopup.findViewById(R.id.telegram_icon)).setImageDrawable(ld7);
                        /*    ((CircleImageView) getActivity().findViewById(R.id.telegram_icon)).setImageDrawable(ld7);

                            try{
                                ((CircleImageView) profilePopUp.findViewById(R.id.telegram_icon)).setImageDrawable(ld7);
                            }catch (Exception e){

                            }

                            try{
                                ((CircleImageView) companyProfilePopup.findViewById(R.id.telegram_icon)).setImageDrawable(ld7);
                            }catch (Exception e){

                            }*/

                                if (profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE) {
                                    ((CircleImageView) profilePopUp.findViewById(R.id.telegram_icon)).setImageDrawable(ld7);
                                } else if (companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE) {
                                    ((CircleImageView) companyProfilePopup.findViewById(R.id.telegram_icon)).setImageDrawable(ld7);
                                } else {
                                    ((CircleImageView) getActivity().findViewById(R.id.telegram_icon)).setImageDrawable(ld7);
                                }

                                ((TextView) socialPopup.findViewById(R.id.telegram_text)).setText("add new link");
                                realm.beginTransaction();
                                contact.hasTelegram = false;
                                contact.setSocialModel(socialModel);
                                realm.commitTransaction();
                                ContactCacheService.updateContact(contact, mainView.getContext());
                                //  EventBus.getDefault().post(new UpdateFile());

                                popupProfileEditPreviewSocial.setVisibility(View.GONE);
                            } else if (typeEnum.equals(SocialEnums.SKYPE)) {
                                if (socialModel.getSkypeLink() != null && !socialModel.getSkypeLink().isEmpty()) {
                                    contactsService.deleteNoteContact(contact.getIdContact(), "Skype: " + socialModel.getSkypeLink());
                                }
                                realm.beginTransaction();
                                socialModel.setSkypeLink(null);
                                realm.commitTransaction();
                                if (popupEditSocial != null)
                                    ((EditText) popupEditSocial.findViewById(R.id.dataToEdit)).getText().clear();

                                //    ((ImageView) socialPopup.findViewById(R.id.skype_icon)).setImageResource(R.drawable.icn_social_skype_gray);
                                //    ((ImageView) getActivity().findViewById(R.id.skype_icon)).setImageResource(R.drawable.icn_social_skype_gray);
                                Drawable color8 = new ColorDrawable(Color.parseColor("#e2e5e8"));
                                Drawable image8 = getResources().getDrawable(R.drawable.icn_social_skype2_gray);
                                LayerDrawable ld8 = new LayerDrawable(new Drawable[]{color8, image8});
                                ((CircleImageView) socialPopup.findViewById(R.id.skype_icon)).setImageDrawable(ld8);
                          /*  ((CircleImageView) getActivity().findViewById(R.id.skype_icon)).setImageDrawable(ld8);

                            try{
                                ((CircleImageView) profilePopUp.findViewById(R.id.skype_icon)).setImageDrawable(ld8);
                            }catch (Exception e){

                            }

                            try{
                                ((CircleImageView) companyProfilePopup.findViewById(R.id.skype_icon)).setImageDrawable(ld8);
                            }catch (Exception e){

                            }*/

                                if (profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE) {
                                    ((CircleImageView) profilePopUp.findViewById(R.id.skype_icon)).setImageDrawable(ld8);
                                } else if (companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE) {
                                    ((CircleImageView) companyProfilePopup.findViewById(R.id.skype_icon)).setImageDrawable(ld8);
                                } else {
                                    ((CircleImageView) getActivity().findViewById(R.id.skype_icon)).setImageDrawable(ld8);
                                }

                                ((TextView) socialPopup.findViewById(R.id.skype_text)).setText("add new link");
                                realm.beginTransaction();
                                contact.hasSkype = false;
                                contact.setSocialModel(socialModel);
                                realm.commitTransaction();
                                ContactCacheService.updateContact(contact, mainView.getContext());
                                //    EventBus.getDefault().post(new UpdateFile());

                                popupProfileEditPreviewSocial.setVisibility(View.GONE);
                            } else if (typeEnum.equals(SocialEnums.TWITTER)) {
                                if (socialModel.getTwitterLink() != null && !socialModel.getTwitterLink().isEmpty()) {
                                    contactsService.deleteNoteContact(contact.getIdContact(), socialModel.getTwitterLink());
                                }
                                realm.beginTransaction();
                                contact.getSocialModel().setTwitterLink(null);
                                realm.commitTransaction();
                                //     ((ImageView) socialPopup.findViewById(R.id.vk_icon)).setImageResource(R.drawable.icn_social_vk_gray);
                                //     ((ImageView) getActivity().findViewById(R.id.vk_icon)).setImageResource(R.drawable.icn_social_vk_gray);
                                Drawable color2 = new ColorDrawable(Color.parseColor("#e2e5e8"));
                                Drawable image2 = getResources().getDrawable(R.drawable.ic_twitter_white);
                                LayerDrawable ld2 = new LayerDrawable(new Drawable[]{color2, image2});
                                ((ImageView) socialPopup.findViewById(R.id.twitter_icon)).setImageDrawable(ld2);

                                ((ImageView) getActivity().findViewById(R.id.vk_icon)).setImageDrawable(ld2);


                               /* if (profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE) {
                                    ((ImageView) profilePopUp.findViewById(R.id.vk_icon)).setImageDrawable(ld2);
                                } else if (companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE) {
                                    ((ImageView) companyProfilePopup.findViewById(R.id.vk_icon)).setImageDrawable(ld2);
                                } else {
                                    ((ImageView) getActivity().findViewById(R.id.vk_icon)).setImageDrawable(ld2);
                                }*/


                                ((TextView) socialPopup.findViewById(R.id.twitterNick)).setText("add username or link");
                                realm.beginTransaction();
                                contact.hasTwitter = false;
                                contact.setSocialModel(socialModel);
                                realm.commitTransaction();
                                ContactCacheService.updateContact(contact, mainView.getContext());
                                //  EventBus.getDefault().post(new UpdateFile());
                                popupProfileEditPreviewSocial.setVisibility(View.GONE);

                            } else if (typeEnum.equals(SocialEnums.YOUTUBE)) {
                                if (socialModel.getYoutubeLink() != null && !socialModel.getYoutubeLink().isEmpty()) {
                                    contactsService.deleteNoteContact(contact.getIdContact(), socialModel.getYoutubeLink());
                                }
                                realm.beginTransaction();
                                contact.getSocialModel().setYoutubeLink(null);
                                realm.commitTransaction();
                                //     ((ImageView) socialPopup.findViewById(R.id.vk_icon)).setImageResource(R.drawable.icn_social_vk_gray);
                                //     ((ImageView) getActivity().findViewById(R.id.vk_icon)).setImageResource(R.drawable.icn_social_vk_gray);
                                Drawable color2 = new ColorDrawable(Color.parseColor("#e2e5e8"));
                                Drawable image2 = getResources().getDrawable(R.drawable.ic_youtube_white);
                                LayerDrawable ld2 = new LayerDrawable(new Drawable[]{color2, image2});
                                ((ImageView) socialPopup.findViewById(R.id.youtube_icon)).setImageDrawable(ld2);

                                ((ImageView) getActivity().findViewById(R.id.vk_icon)).setImageDrawable(ld2);


                              /*  if (profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE) {
                                    ((ImageView) profilePopUp.findViewById(R.id.vk_icon)).setImageDrawable(ld2);
                                } else if (companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE) {
                                    ((ImageView) companyProfilePopup.findViewById(R.id.vk_icon)).setImageDrawable(ld2);
                                } else {
                                    ((ImageView) getActivity().findViewById(R.id.vk_icon)).setImageDrawable(ld2);
                                }*/


                                ((TextView) socialPopup.findViewById(R.id.youtubeNick)).setText("add username or link");
                                realm.beginTransaction();
                                contact.hasYoutube = false;
                                contact.setSocialModel(socialModel);
                                realm.commitTransaction();
                                ContactCacheService.updateContact(contact, mainView.getContext());
                                //  EventBus.getDefault().post(new UpdateFile());
                                popupProfileEditPreviewSocial.setVisibility(View.GONE);

                            } else if (typeEnum.equals(SocialEnums.MEDIUM)) {
                                if (socialModel.getMediumLink() != null && !socialModel.getMediumLink().isEmpty()) {
                                    contactsService.deleteNoteContact(contact.getIdContact(), socialModel.getMediumLink());
                                }
                                realm.beginTransaction();
                                contact.getSocialModel().setMediumLink(null);
                                realm.commitTransaction();
                                //     ((ImageView) socialPopup.findViewById(R.id.vk_icon)).setImageResource(R.drawable.icn_social_vk_gray);
                                //     ((ImageView) getActivity().findViewById(R.id.vk_icon)).setImageResource(R.drawable.icn_social_vk_gray);
                                Drawable color2 = new ColorDrawable(Color.parseColor("#e2e5e8"));
                                Drawable image2 = getResources().getDrawable(R.drawable.medium_white);
                                LayerDrawable ld2 = new LayerDrawable(new Drawable[]{color2, image2});
                                ((ImageView) socialPopup.findViewById(R.id.medium_icon)).setImageDrawable(ld2);

                                //((ImageView) getActivity().findViewById(R.id.vk_icon)).setImageDrawable(ld2);


                              /*  if (profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE) {
                                    ((ImageView) profilePopUp.findViewById(R.id.vk_icon)).setImageDrawable(ld2);
                                } else if (companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE) {
                                    ((ImageView) companyProfilePopup.findViewById(R.id.vk_icon)).setImageDrawable(ld2);
                                } else {
                                    ((ImageView) getActivity().findViewById(R.id.vk_icon)).setImageDrawable(ld2);
                                }*/


                                ((TextView) socialPopup.findViewById(R.id.mediumNick)).setText("add username or link");
                                realm.beginTransaction();
                                contact.hasMedium = false;
                                contact.setSocialModel(socialModel);
                                realm.commitTransaction();
                                ContactCacheService.updateContact(contact, mainView.getContext());
                                //  EventBus.getDefault().post(new UpdateFile());
                                popupProfileEditPreviewSocial.setVisibility(View.GONE);

                            }

                            realm.close();

                            initIconColor(contact, mainView);


                            EventBus.getDefault().post(new UpdateFile());
                            popupProfileEditPreviewSocial.setVisibility(View.GONE);
                        })
                        .setNegativeButton("No", (dialog, id) -> dialog.cancel());

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

    }

    private void closeChildPopups() {
        //contactProfileDataFragment.closeOtherPopup();
        companyProfileDataFragment.closeOtherPopup();
    }

    private String getContactIdFromPhoneNumber(String phone) {
        final Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phone));
        final ContentResolver contentResolver = getActivity().getContentResolver();
        final Cursor phoneQueryCursor = contentResolver.query(uri, new String[]{ContactsContract.PhoneLookup._ID}, null, null, null);
        if (phoneQueryCursor != null) {
            if (phoneQueryCursor.moveToFirst()) {
                String result = phoneQueryCursor.getString(phoneQueryCursor.getColumnIndex(ContactsContract.PhoneLookup._ID));
                phoneQueryCursor.close();
                return result;
            }
            phoneQueryCursor.close();
        }
        return null;
    }

    public String getContactMimeTypeDataId(@NonNull Context context, String contactId, @NonNull String mimeType) {
        if (TextUtils.isEmpty(mimeType))
            return null;
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = null;
        try {
            cursor = cr.query(ContactsContract.Data.CONTENT_URI, new String[]{ContactsContract.Data._ID}, ContactsContract.Data.MIMETYPE + "= ? AND "
                    + ContactsContract.Data.CONTACT_ID + "= ?", new String[]{mimeType, contactId}, null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        if (cursor == null)
            return null;
        if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        String result = cursor.getString(cursor.getColumnIndex(ContactsContract.Data._ID));
        cursor.close();
        return result;
    }

    private void copyToClipboard(Context context, String text) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
            clipboard.setPrimaryClip(clip);
        }
        Toast.makeText(mainView.getContext(), "Copied to clipboard", Toast.LENGTH_SHORT).show();
    }

    public void shareIntent(String text) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(Intent.createChooser(shareIntent, "Share"));
    }


    private void showPopupUserHashtags(Contact contact) {
        System.out.println("Show popup hashtag");
        popupUserHashtags = (FrameLayout) getActivity().findViewById(R.id.popupProfileHashtags123);
        popupUserHashtags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        int nowCount = 0;

        ArrayList<String> tags = new ArrayList<>();

        for (HashTag hashTag : contact.getListOfHashtags()) {

            tags.add(hashTag.getHashTagValue());
        }

        LinearLayout linearLayout = (LinearLayout) popupUserHashtags.findViewById(R.id.containerHashTags);
        linearLayout.removeAllViews();

        for (HashTag hashTag : contact.getListOfHashtags()) {
            TextView text = new TextView(getActivity());
            text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            DisplayMetrics metrics = mainView.getContext().getResources().getDisplayMetrics();
            text.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            text.setTextColor(getResources().getColor(R.color.colorPrimary));
            text.setText(hashTag.getHashTagValue() + " ");
            text.setOnClickListener(v -> adapterC.searchByHashTagValue(hashTag.getHashTagValue()));
            text.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    deleteHashTagsFromUser(text.getText().toString(), contact);
                    return true;
                }
            });
            linearLayout.addView(text);
        }


        // linearLayout.addView(textView);

        HorizontalScrollView scrollView = (HorizontalScrollView) popupUserHashtags.findViewById(R.id.scrollHorizontal);
        if (scrollView.getChildCount() > 0) scrollView.removeAllViews();
        scrollView.addView(linearLayout);
        scrollView.setSmoothScrollingEnabled(false);
        scrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
        scrollView.setSmoothScrollingEnabled(true);

        popupUserHashtags.findViewById(R.id.ContactHashLinear).setVisibility(View.VISIBLE);
        popupUserHashtags.findViewById(R.id.companyVileEditPreview).setVisibility(View.GONE);
        EditText editText = (EditText) popupUserHashtags.findViewById(R.id.hashtagsList);
        editText.setVisibility(View.GONE);

        //=========================

        popupUserHashtags.findViewById(R.id.inviteFriend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                String friends = "";

                for (HashTag hashtag : contact.getListOfHashtags()) {
                    friends = friends + " " + hashtag.getHashTagValue();
                }

                shareIntent.putExtra(Intent.EXTRA_TEXT, "Join call2me community - i add to your card following tags: " + friends);
                startActivity(Intent.createChooser(shareIntent, "Поделиться контактом"));
            }
        });

        customTagsAdapter = new CustomTagsAdapter(tags, contact, this);
        RecyclerView customTagsContainer = (RecyclerView) popupUserHashtags.findViewById(R.id.customTagsContainer);
        RecyclerView.LayoutManager customLR = new LinearLayoutManager(mainView.getContext());
        customTagsContainer.setLayoutManager(customLR);
        customTagsContainer.setItemAnimator(new DefaultItemAnimator());
        customTagsContainer.setAdapter(customTagsAdapter);
        ((ImageView) popupUserHashtags.findViewById(R.id.penEdit)).setImageDrawable(getResources().getDrawable(R.drawable.icn_popup_pencil));
        penStartEditMode(contact);
        String[] suggest = {"#timesoftware", "#another", "#goodboy", "#smile"};
        List<String> suggestTags = Arrays.asList(suggest);
        HashtagMasstaggingAdapter suggestTagsAdapter = new HashtagMasstaggingAdapter(suggestTags, this, true, contact);
        containerAssistant = (RecyclerView) popupUserHashtags.findViewById(R.id.assistantSuggestContainer);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mainView.getContext());
        containerAssistant.setLayoutManager(mLayoutManager);
        containerAssistant.setItemAnimator(new DefaultItemAnimator());
        //   containerAssistant.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        containerAssistant.setAdapter(suggestTagsAdapter);

        ArrayList<HashTagQuantity> listH = new ArrayList<>();
        listH.addAll(((Postman) activityApp).getHashTagsAdapter().getListOfHashtags());
        Collections.sort(listH, new Comparator<HashTagQuantity>() {
            @Override
            public int compare(HashTagQuantity first, HashTagQuantity second) {
                return second.getQuantity() - first.getQuantity();
            }
        });

        String[] most = {"#pr", "#investor", "#VC", "#Media", "#PR"};
        List<String> mostUsed = Arrays.asList(most);
        mostTagAdapter = new MostUsedTagsAdapter(listH, this, true, contact);
        containerMost = (RecyclerView) popupUserHashtags.findViewById(R.id.mostUsedTagsContainer);
        RecyclerView.LayoutManager mostLayoutManager = new LinearLayoutManager(mainView.getContext());
        containerMost.setLayoutManager(mostLayoutManager);
        //  containerMost.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
        containerMost.setItemAnimator(new DefaultItemAnimator());
        containerMost.setAdapter(mostTagAdapter);

//        popupUserHashtags.findViewById(R.id.backToPreview).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                popupUserHashtags.setVisibility(View.GONE);
//                getActivity().findViewById(R.id.suggestPopup).setVisibility(View.GONE);
//            }
//        });

        popupUserHashtags.findViewById(R.id.openSuggestPopup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupUserHashtags.findViewById(R.id.arrowSuggest).getScaleY() != -1f) {
                    popupUserHashtags.findViewById(R.id.arrowSuggest).setScaleY(-1f);
                    suggestTagsAdapter.showAllTags();
                    mostTagAdapter.showAllTags();
                    ViewGroup.LayoutParams mostParams = containerMost.getLayoutParams();
                    mostParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 140, getResources().getDisplayMetrics());
                    containerMost.setLayoutParams(mostParams);
                    ViewGroup.LayoutParams assistantParams = containerAssistant.getLayoutParams();
                    assistantParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 140, getResources().getDisplayMetrics());
                    containerAssistant.setLayoutParams(assistantParams);
                    //showSuggestPopup(popupUserHashtags, contact);
                } else {
                    ViewGroup.LayoutParams mostParams = containerMost.getLayoutParams();
                    mostParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
                    containerMost.setLayoutParams(mostParams);
                    ViewGroup.LayoutParams assistantParams = containerAssistant.getLayoutParams();
                    assistantParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
                    containerAssistant.setLayoutParams(assistantParams);
                    popupUserHashtags.findViewById(R.id.arrowSuggest).setScaleY(1f);
                    popupUserHashtags.setVisibility(View.GONE);
                    showPopupUserHashtags(contact);
                    // getActivity().findViewById(R.id.suggestPopup).setVisibility(View.GONE);
                }
            }
        });
        popupUserHashtags.setVisibility(View.VISIBLE);

        openedViews.add(popupUserHashtags);
    }

    private void addHashtagToProfile(String hashtag) {


        for (HashTag hashTag : company.getListOfHashtags()) {
            if (hashTag.getHashTagValue().trim().compareTo(hashtag.trim().toLowerCase()) == 0) {
                Toast.makeText(mainView.getContext(), "Hashtag already exist", Toast.LENGTH_SHORT).show();
                return;
            }

        }
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getActivity());
        alertDialogBuilder.setTitle("Do you want to add hashtag " + hashtag.trim().toLowerCase() + " to contact?");
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> {
                    if (customTagsAdapter != null)
                        customTagsAdapter.addHashTag(hashtag.toLowerCase().trim());


                    Realm realm = Realm.getDefaultInstance(); //-

                    LinearLayout containerHashTags = (LinearLayout) mainView.findViewById(R.id.containerHashTags);
                    TextView text = new TextView(getActivity());
                    text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                    DisplayMetrics metrics = mainView.getContext().getResources().getDisplayMetrics();
                    text.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    text.setTextColor(getResources().getColor(R.color.colorPrimary));
                    text.setText(hashtag.trim().toLowerCase() + " ");


                    containerHashTags.addView(text);
                    realm.beginTransaction();
                    company.getListOfHashtags().add(new HashTag(hashtag.trim().toLowerCase()));
                    realm.commitTransaction();
                    realm.close();

                    contactsService.addNoteToContact(company.getIdContact(), hashtag.toString().toLowerCase().trim(), company.getName());

                    MainActivity.typeUpdate = "EDIT";
                    MainActivity.nameToUpd.clear();
                    MainActivity.nameToUpd.add(company.getName());
                    if (company.listOfContacts == null || company.listOfContacts.isEmpty())
                        MainActivity.nameToUpdTypeContact.add(true);
                    else
                        MainActivity.nameToUpdTypeContact.add(false);


                    ContactCacheService.updateContact(company, mainView.getContext());
                    EventBus.getDefault().post(new UpdateFile());
                    HorizontalScrollView scrollView = (HorizontalScrollView) mainView.findViewById(R.id.scrollHorizontal);
                    if (scrollView.getChildCount() > 0) scrollView.removeAllViews();
                    scrollView.addView(containerHashTags);
                    scrollView.setSmoothScrollingEnabled(false);
                    scrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
                    scrollView.setSmoothScrollingEnabled(true);
                    //contactAdapter.getContactFragment().initRecyclerHashTags();

                    if (adapterC != null && adapterC.getContactFragment() != null)
                        adapterC.getContactFragment().initRecyclerHashTags();
                    updateHashTags();
                    //System.out.println("PARENT = "+getParentFragment().getClass().getName());
                    Toast.makeText(mainView.getContext(), "Hashtag successfully added", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", (dialog, id) -> dialog.cancel());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void updateHashTags() {
        LinearLayout containerHashTags = (LinearLayout) mainView.findViewById(R.id.containerHashTags);
        if (containerHashTags.getChildCount() > 0)
            containerHashTags.removeAllViews();
        if (company.getListOfHashtags() != null)
            for (HashTag hashTag : company.getListOfHashtags()) {

                TextView text = new TextView(getActivity());
                text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                text.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                text.setTextColor(getResources().getColor(R.color.colorPrimary));
                text.setText(hashTag.getHashTagValue() + " ");
                text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        System.out.println("TEXT еее= " + hashTag.getHashTagValue());
                        //   contactAdapter.searchByHashTagValue(hashTag.getHashTagValue());
                    }
                });
                //   text.setOnClickListener(v -> contactAdapter.searchByHashTagValue(hashTag.getHashTagValue()));
                text.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        deleteHashTagsFromUser(hashTag.getHashTagValue(), company);
                        return true;
                    }
                });


                containerHashTags.addView(text);
            }

        if (company != null && (company.getListOfHashtags() == null || company.getListOfHashtags().size() == 0)) {
            System.out.println("Add Hashtah TExt added");
            TextView text = new TextView(getActivity());
            text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            //DisplayMetrics metrics = mainView.getContext().getResources().getDisplayMetrics();
            text.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            text.setTextColor(getResources().getColor(R.color.gray));
            text.setText("hashtags");
            text.setOnClickListener(v -> {
                //containerHashTags.removeAllViews();
                showPopupUserHashtags(company);
            });
            containerHashTags.addView(text);
        }

        if (company.getListOfHashtags() == null || company.getListOfHashtags().size() == 0) {
           /* TextView text = new TextView(getActivity());
            text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            DisplayMetrics metrics = mainView.getContext().getResources().getDisplayMetrics();
            text.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            text.setTextColor(getResources().getColor(R.color.gray));
            text.setText("hashtags");
            System.out.println("HASSHTAG CLEAR PROFILE update");
            text.setOnClickListener(v -> {
                containerHashTags.removeAllViews();
                showPopupUserHashtags(selectedContact);
            });
            containerHashTags.addView(text);*/
        }

        System.out.println("UPDATE HASH LIST");
    }

    @Override
    public void addHashtagsToSelectedContacts(String hashtag) {
        if (!company.getListOfHashtags().contains(new HashTag(hashtag))) {
            company.getListOfHashtags().add(new HashTag(hashtag.trim()));
            ContactCacheService.updateContact(company, mainView.getContext());
            MainActivity.typeUpdate = "EDIT";
            MainActivity.nameToUpd.clear();
            MainActivity.nameToUpdTypeContact.clear();
            MainActivity.nameToUpd.add(company.getName());
            if (company.listOfContacts == null || company.listOfContacts.isEmpty())
                MainActivity.nameToUpdTypeContact.add(true);
            else
                MainActivity.nameToUpdTypeContact.add(false);
            EventBus.getDefault().post(new UpdateFile());
            Toast.makeText(mainView.getContext(), "Hashtag successfully added", Toast.LENGTH_SHORT).show();
        } else {
            Toast toast = Toast.makeText(mainView.getContext(), "Hashtag already  exist", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    public void addNewTagToContact(String tag, Contact contact) {
        for (HashTag hashTag : contact.getListOfHashtags()) {
            if (hashTag.getHashTagValue().compareTo(tag) == 0) {
                Toast toast = Toast.makeText(mainView.getContext(), "Hashtag already  exist", Toast.LENGTH_SHORT);
                toast.show();
                return;
            }
        }

        if (popupUserHashtags.findViewById(R.id.ContactHashLinear).getVisibility() == View.VISIBLE) {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    getActivity());
            alertDialogBuilder.setTitle("Do you want to add " + tag + " ?");
            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialog, id) -> {
                        customTagsAdapter.addHashTag(tag);

                        Realm realm = Realm.getDefaultInstance(); //-
                        realm.beginTransaction();
                        contact.getListOfHashtags().add(new HashTag(tag.trim()));
                        realm.commitTransaction();
                        realm.close();
                        ContactCacheService.updateContact(contact, mainView.getContext());
                        contactsService.addNoteToContact(contact.getIdContact(), tag, contact.getName());

                        if (popupUserHashtags.findViewById(R.id.ContactHashLinear).getVisibility() == View.VISIBLE) {

                            ArrayList<String> tags = new ArrayList<>();

                            for (HashTag hashTag : contact.getListOfHashtags()) {

                                tags.add(hashTag.getHashTagValue());
                            }


                            LinearLayout linearLayout = (LinearLayout) popupUserHashtags.findViewById(R.id.containerHashTags);
                            linearLayout.removeAllViews();

                            for (HashTag hashTag : contact.getListOfHashtags()) {
                                TextView text = new TextView(getActivity());
                                text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                                DisplayMetrics metrics = mainView.getContext().getResources().getDisplayMetrics();
                                text.setLayoutParams(new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                text.setTextColor(getResources().getColor(R.color.colorPrimary));
                                text.setText(hashTag.getHashTagValue() + " ");
                                text.setOnClickListener(v -> adapterC.searchByHashTagValue(hashTag.getHashTagValue()));
                                text.setOnLongClickListener(new View.OnLongClickListener() {
                                    @Override
                                    public boolean onLongClick(View v) {
                                        deleteHashTagsFromUser(text.getText().toString(), contact);
                                        return true;
                                    }
                                });
                                linearLayout.addView(text);
                            }

                            //linearLayout.addView(textView);

                            HorizontalScrollView scrollView = (HorizontalScrollView) popupUserHashtags.findViewById(R.id.scrollHorizontal);
                            if (scrollView.getChildCount() > 0) scrollView.removeAllViews();
                            scrollView.addView(linearLayout);
                            scrollView.setSmoothScrollingEnabled(false);
                            scrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
                            scrollView.setSmoothScrollingEnabled(true);

                       /* popupUserHashtags.findViewById(R.id.ContactHashLinear).setVisibility(View.VISIBLE);
                        popupUserHashtags.findViewById(R.id.companyVileEditPreview).setVisibility(View.GONE);
                        EditText editText = (EditText) popupUserHashtags.findViewById(R.id.hashtagsList);
                        editText.setVisibility(View.GONE);*/
                        } else {
                            ((EditText) popupUserHashtags.findViewById(R.id.hashtagsList)).append(tag.trim() + " ");
                        }

                       /* MainActivity.typeUpdate = "EDIT";
                        MainActivity.nameToUpd.add(contact.getName());
                        if (contact.listOfContacts == null || contact.listOfContacts.isEmpty())
                            MainActivity.nameToUpdTypeContact.add(true);
                        else
                            MainActivity.nameToUpdTypeContact.add(false);*/

                        EventBus.getDefault().post(new UpdateFile());
                        Toast.makeText(mainView.getContext(), "Hashtag successfully added", Toast.LENGTH_SHORT).show();
                        ContactsFragment.UPD_ALL = true;
                        addHashtagPreview(tag);
                    })
                    .setNegativeButton("No", (dialog, id) -> dialog.cancel());

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
        ((EditText) popupUserHashtags.findViewById(R.id.hashtagsList)).append(tag.trim() + " ");

    }

    private void addHashtagPreview(String hashtag) {
        LinearLayout containerHashTags = (LinearLayout) mainView.findViewById(R.id.containerHashTags);
        TextView text = new TextView(getActivity());
        text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        text.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        text.setTextColor(getResources().getColor(R.color.colorPrimary));
        text.setText(hashtag + " ");
        containerHashTags.addView(text);
        updateHashTags();
        System.out.println("add prew hash");
    }

    @Override
    public void deleteHashTagsFromUser(String hashtag, Contact contact) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getActivity());
        alertDialogBuilder.setTitle("Do you want to delete " + hashtag + " ?");
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> {

                    Realm realm = Realm.getDefaultInstance(); //-

                    RealmList<HashTag> listOfHashtags = company.getListOfHashtags();
                    for (int indexI = 0; indexI < listOfHashtags.size(); indexI++) {
                        if (listOfHashtags.get(indexI).getHashTagValue().equals(hashtag.trim())) {
                            realm.beginTransaction();
                            listOfHashtags.remove(indexI);
                            realm.commitTransaction();
                            contactsService.deleteNoteContact(company.getIdContact(), hashtag);
                        }

                    }
                    realm.beginTransaction();
                    company.setListOfHashtags(listOfHashtags);
                    company.getListOfHashtags().remove(new HashTag(hashtag));
                    realm.commitTransaction();
                    realm.close();
                    ContactCacheService.updateContact(company, mainView.getContext());


                    EventBus.getDefault().post(new UpdateFile());
                    //closeOtherPopup();

                    if (popupUserHashtags != null && popupUserHashtags.getVisibility() == View.VISIBLE) {
                        showPopupUserHashtags(contact);
                    }

                    // showPopupUserHashtags(selectedContact);
                    updateHashTags();
                    //ContactAdapter contactAdapter = ContactAdapter.contactAd;
                    adapterC.getContactFragment().initRecyclerHashTags();

                })
                .setNegativeButton("No", (dialog, id) -> {
                    dialog.cancel();
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void updatedUserHashtags(ArrayList<String> updatedHashtags) {

    }


    private void addHashtagsToProfile(ArrayList<String> hashtags) {

        String addHashtags = "";

        for (HashTag hashTag : company.getListOfHashtags()) {
            for (int indexI = 0; indexI < hashtags.size(); indexI++) {
                String hashtag = hashtags.get(indexI).toLowerCase();
                if (hashTag.getHashTagValue().compareTo(hashtag) == 0) {
                    hashtags.remove(hashtag);
                    Toast.makeText(mainView.getContext(), "Hashtag " + hashtag + " already exist", Toast.LENGTH_SHORT).show();
                }
            }
        }

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getActivity());
        alertDialogBuilder.setTitle("Do you want to add hashtags to contact?");
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> {


                    Realm realm = Realm.getDefaultInstance(); //-

                    if (customTagsAdapter != null)
                        customTagsAdapter.addHashTags(hashtags);

                    LinearLayout containerHashTags = (LinearLayout) mainView.findViewById(R.id.containerHashTags);
                    HorizontalScrollView scrollView = (HorizontalScrollView) mainView.findViewById(R.id.scrollHorizontal);
                    if (scrollView.getChildCount() > 0) scrollView.removeAllViews();
                    for (String hashtag : hashtags) {
                        TextView text = new TextView(getActivity());
                        text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                        DisplayMetrics metrics = mainView.getContext().getResources().getDisplayMetrics();
                        text.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        text.setTextColor(getResources().getColor(R.color.colorPrimary));
                        text.setText(hashtag.toLowerCase().trim() + " ");


                        containerHashTags.addView(text);
                        realm.beginTransaction();
                        company.getListOfHashtags().add(new HashTag(hashtag.trim().toLowerCase()));
                        realm.commitTransaction();

                        contactsService.addNoteToContact(company.getIdContact(), hashtag.trim().toLowerCase().toString(), company.getName());
                    }
                    realm.close();

                    scrollView.addView(containerHashTags);
                    scrollView.setSmoothScrollingEnabled(false);
                    scrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
                    scrollView.setSmoothScrollingEnabled(true);
                    ContactCacheService.updateContact(company, mainView.getContext());
                    EventBus.getDefault().post(new UpdateFile());

                    MainActivity.typeUpdate = "EDIT";
                    MainActivity.nameToUpd.clear();
                    MainActivity.nameToUpdTypeContact.clear();
                    MainActivity.nameToUpd.add(company.getName());
                    if (company.listOfContacts == null || company.listOfContacts.isEmpty())
                        MainActivity.nameToUpdTypeContact.add(true);
                    else
                        MainActivity.nameToUpdTypeContact.add(false);

                    EventBus.getDefault().post(new UpdateFile());

                    updateHashTags();

                    adapterC.getContactFragment().initRecyclerHashTags();

                    Toast.makeText(mainView.getContext(), "Hashtags successfully added", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", (dialog, id) -> dialog.cancel());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void penStartEditMode(Contact contact) {
        popupUserHashtags.findViewById(R.id.penEdit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ImageView) popupUserHashtags.findViewById(R.id.penEdit)).setImageDrawable(getResources().getDrawable(R.drawable.icn_saving_pig));
                customTagsAdapter.startEditMode();

                popupUserHashtags.findViewById(R.id.ContactHashLinear).setVisibility(View.GONE);
                popupUserHashtags.findViewById(R.id.companyVileEditPreview).setVisibility(View.VISIBLE);
                EditText editText = (EditText) popupUserHashtags.findViewById(R.id.hashtagsList);
                editText.setSelection(0);
                editText.setVisibility(View.VISIBLE);
                editText.setText("");
                for (String str : customTagsAdapter.listOfHashtags)
                    editText.append(str + " ");


                penEndEditMode(contact);
            }
        });
    }


    private void penEndEditMode(Contact contact) {
        popupUserHashtags.findViewById(R.id.penEdit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ImageView) popupUserHashtags.findViewById(R.id.penEdit)).setImageDrawable(getResources().getDrawable(R.drawable.icn_popup_pencil));
                customTagsAdapter.stopEditMode();

                EditText editText = (EditText) popupUserHashtags.findViewById(R.id.hashtagsList);
                String s = editText.getText().toString().trim();
                String[] magicSplit = s.split(" ");

                String findStr = s.toString();

                ArrayList<String> listHash = new ArrayList<>();
                if (magicSplit.length > 0) {
                    findStr = magicSplit[magicSplit.length - 1];

                    for (int i = 0; i < magicSplit.length; i++) {
                        if (magicSplit[i].length() > 1 && magicSplit[i].charAt(0) == '#')
                            listHash.add(magicSplit[i]);
                    }


                }
                System.out.println("HASHTAGS LENG = " + magicSplit.toString());


                updatedUserHashtags(listHash);

                popupUserHashtags.findViewById(R.id.ContactHashLinear).setVisibility(View.VISIBLE);
                popupUserHashtags.findViewById(R.id.companyVileEditPreview).setVisibility(View.GONE);
                EditText editText2 = (EditText) popupUserHashtags.findViewById(R.id.hashtagsList);
                editText2.setVisibility(View.GONE);

                ArrayList<String> tags = new ArrayList<>();

                for (HashTag hashTag : contact.getListOfHashtags()) {

                    tags.add(hashTag.getHashTagValue());
                }


                LinearLayout linearLayout = (LinearLayout) popupUserHashtags.findViewById(R.id.containerHashTags);
                linearLayout.removeAllViews();

                for (HashTag hashTag : contact.getListOfHashtags()) {
                    TextView text = new TextView(getActivity());
                    text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                    DisplayMetrics metrics = mainView.getContext().getResources().getDisplayMetrics();
                    text.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    text.setTextColor(getResources().getColor(R.color.colorPrimary));
                    text.setText(hashTag.getHashTagValue() + " ");
                    text.setOnClickListener(v1 -> adapterC.searchByHashTagValue(hashTag.getHashTagValue()));
                    text.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            deleteHashTagsFromUser(text.getText().toString(), contact);
                            return true;
                        }
                    });
                    linearLayout.addView(text);
                }

                /*if (contact != null && (contact.getListOfHashtags() == null || contact.getListOfHashtags().size() == 0)) {
                    System.out.println("Add Hashtah TExt added");
                    TextView text = new TextView(getActivity());
                    text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                    //DisplayMetrics metrics = mainView.getContext().getResources().getDisplayMetrics();
                    text.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    text.setTextColor(getResources().getColor(R.color.gray));
                    text.setText("hashtags");
                    text.setOnClickListener(v1 -> {
                        linearLayout.removeAllViews();
                        showPopupUserHashtags(selectedContact);
                    });
                    linearLayout.addView(text);
                }*/

                //linearLayout.addView(textView);

                HorizontalScrollView scrollView = (HorizontalScrollView) popupUserHashtags.findViewById(R.id.scrollHorizontal);
                if (scrollView.getChildCount() > 0) scrollView.removeAllViews();
                scrollView.addView(linearLayout);
                scrollView.setSmoothScrollingEnabled(false);
                scrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
                scrollView.setSmoothScrollingEnabled(true);


                penStartEditMode(contact);
            }
        });
    }

    private ProfileSectionAdapter adapter;

    private FragmentManager fragmentManagerSetup;

    public void setupSectionPager() {
        ViewPager viewPager = (ViewPager) mainView.findViewById(R.id.viewpager);

        companyProfileDataFragment = new CompanyProfileDataFragment();

        companyContactsFragment = new CompanyContactsFragment();

        Bundle args = new Bundle();
        args.putSerializable("selectedContact", company);

        companyProfileDataFragment.setArguments(args);
        companyContactsFragment.setArguments(args);

        if (fragmentManagerSetup != null) {
            adapter = new ProfileSectionAdapter(fragmentManagerSetup);
        } else {
            adapter = new ProfileSectionAdapter(getChildFragmentManager());
        }


            adapter.addFragment(companyProfileDataFragment, "Profile");




        contactTimelineDataFragment = new ContactTimelineDataFragment();
        contactTimelineDataFragment.setArguments(args);
        adapter.addFragment(contactTimelineDataFragment, "Timeline");
        adapter.addFragment(new ScheduleFragment(), "Schedule");
        //  adapter.addFragment(new PlacesFragment(), "Places");
        //adapter.addFragment(new FilesFragment(), "Files");
        adapter.addFragment(new FilesConatct(), "Files");
        adapter.addFragment(companyContactsFragment, "Contacts");

        viewPager.setAdapter(adapter);
        SmartTabLayout tabs = (SmartTabLayout) mainView.findViewById(R.id.result_tabs);
        tabs.setViewPager(viewPager);
        mainView.findViewById(R.id.closerTabs).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeOtherPopup();
                //contactProfileDataFragment.closeOtherPopup();
            }
        });


    }



    private void endEditMode() {

        Contact selectedContact = company;

        Realm realm = Realm.getDefaultInstance(); //-
        realm.beginTransaction();
        String oldName = selectedContact.getName();
        if (((EditText) mainView.findViewById(R.id.editNameProflie)).getText().length() > 0) {
            selectedContact.setName(((EditText) mainView.findViewById(R.id.editNameProflie)).getText().toString().trim());
            if (selectedContact.listOfContacts == null || selectedContact.listOfContacts.isEmpty())
                contactsService.updateName(selectedContact.getIdContact(), oldName, selectedContact.getName());
        }

        //System.out.println("OLD NAME = "+oldName);
        //System.out.println("NEW NAME = "+selectedContact.getName());
        //System.out.println("NEW NAME2 = "+((EditText) mainView.findViewById(R.id.editNameProflie)).getText().toString().trim());

        //String oldComp = null;
        //String oldPosition = selectedContact.getCompanyPossition();
        //String mainCompany = selectedContact.getCompany();


        //String newPosition = ((EditText) fastEditPopup.findViewById(R.id.company_edit)).getText().toString();
        //String oldPosition = selectedContact.getCompanyPossition();


        //String mainCompany = selectedContact.getCompany();

        String oldComp = selectedContact.getCompany();


        if (((EditText) mainView.findViewById(R.id.company_title_edit)).getText().length() > 0) {
            oldComp = selectedContact.getCompany();
            if (selectedContact.getCompany() == null) {

                //contactsService.addCompanyToContact(String.valueOf(selectedContact.getId()),((EditText) mainView.findViewById(R.id.company_title_edit)).getText().toString());
            }
            selectedContact.setCompany(((EditText) mainView.findViewById(R.id.company_title_edit)).getText().toString().trim());

        }

        realm.commitTransaction();

        mainView.findViewById(R.id.lineNameProfile).setVisibility(View.GONE);
        mainView.findViewById(R.id.lineCompanyProfile).setVisibility(View.GONE);
        ((View) mainView.findViewById(R.id.lineCompanyProfileWeb)).setVisibility(View.GONE);
        mainView.findViewById(R.id.positionVileEditPreview).setVisibility(View.GONE);
        mainView.findViewById(R.id.lineHashProfile).setVisibility(View.GONE);


        realm.beginTransaction();


        if (selectedContact.getListOfHashtags() != null)
            for (int i = 0; i < selectedContact.getListOfHashtags().size(); i++) {
                contactsService.deleteNoteContact(selectedContact.getIdContact(), selectedContact.getListOfHashtags().get(i).getHashTagValue().toLowerCase());
            }

        if (((EditText) mainView.findViewById(R.id.hashTagsEdit)).getText().toString().trim().length() > 0) {
            String[] hashtags = ((EditText) mainView.findViewById(R.id.hashTagsEdit)).getText().toString().split(" ");
            RealmList<HashTag> listOfHashTag = new RealmList<>();
            for (int indexI = 0; indexI < hashtags.length; indexI++) {
                if (hashtags[indexI].length() == 1) continue;
                HashTag hashTag = realm.createObject(HashTag.class);
                hashTag.setHashTagValue(hashtags[indexI].toLowerCase());
                hashTag.setDate(new Date());
                if (!listOfHashTag.contains(hashTag)) {
                    listOfHashTag.add(hashTag);
                    contactsService.addNoteToContact(selectedContact.getIdContact(), hashTag.getHashTagValue(), selectedContact.getName());
                }

            }
            selectedContact.setListOfHashtags(listOfHashTag);
        } else {
            selectedContact.setListOfHashtags(new RealmList<>());
        }
        boolean findCompany = false;

        //===========================================


        try {
            realm.commitTransaction();
        } catch (Exception e) {
            e.fillInStackTrace();
        }

        if (selectedContact.listOfContacts == null || selectedContact.listOfContacts.isEmpty()) {

            contactsService.deleteCompany_Possition(selectedContact.getIdContact());
            if (selectedContact.getCompany() != null && !selectedContact.getCompany().isEmpty())
                contactsService.insertCompany(selectedContact.getIdContact(), selectedContact.getCompany(), selectedContact.getName());

            if (selectedContact.getCompanyPossition() != null && !selectedContact.getCompanyPossition().isEmpty() && (selectedContact.getCompany() != null && !selectedContact.getCompany().isEmpty()))
                contactsService.addComp(selectedContact.getIdContact(), selectedContact.getCompanyPossition(), selectedContact.getCompany());

            if (selectedContact.getCompanyPossition() != null && !selectedContact.getCompanyPossition().isEmpty() && (selectedContact.getCompany() == null || selectedContact.getCompany().isEmpty()))
                contactsService.insertPosition(selectedContact.getIdContact(), selectedContact.getCompanyPossition(), selectedContact.getName());

        }

        if (selectedContact.listOfContacts != null && !selectedContact.listOfContacts.isEmpty()) {


        }
        ContactCacheService.updateContact(selectedContact, mainView.getContext());

        if (selectedContact.listOfContacts != null && !selectedContact.listOfContacts.isEmpty()) {

            mainView.findViewById(R.id.web_title_edit).setVisibility(View.GONE);
            mainView.findViewById(R.id.web_title).setVisibility(View.VISIBLE);


            if (!selectedContact.getName().equals(oldName)) {
                System.out.println("QUALS NAME FALSE");
                for (Contact contact : selectedContact.listOfContacts) {
                    realm.beginTransaction();
                    contact.setCompany(selectedContact.getName());
                    realm.commitTransaction();


                    contactsService.addCompanybyCompany(contact.getIdContact(), oldName, selectedContact.getName());
                    //contactsService.updateCompany(contact.getIdContact(), oldName, selectedContact.getName());
                }
            }

            realm.beginTransaction();
            selectedContact.webSite = ((EditText) mainView.findViewById(R.id.web_title_edit)).getText().toString().trim();
            realm.commitTransaction();
            if (selectedContact.webSite != null && !selectedContact.webSite.isEmpty()) {
                ((ImageView) mainView.findViewById(R.id.webImgCompany)).setImageResource(R.drawable.icn_popup_web_blue);
                ((TextView) mainView.findViewById(R.id.web_title)).setVisibility(View.VISIBLE);
                ((TextView) mainView.findViewById(R.id.web_title)).setText(selectedContact.webSite);
                ((TextView) mainView.findViewById(R.id.web_title)).setTextColor(getResources().getColor(R.color.primary_dark));

                mainView.findViewById(R.id.web_title).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {

                            //System.out.println("WEB ICIT GO name = " + contact.getName() + ", web = " + contact.webSite);
                            String uri = selectedContact.webSite;
                            if (!uri.contains("https://") && !uri.contains("http://"))
                                uri = "https://" + uri;

                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(uri));

                            if (getActivity() != null)
                                getActivity().startActivity(i);
                            else
                                activityApp.startActivity(i);

                        } catch (Exception e) {

                        }
                    }
                });

                mainView.findViewById(R.id.webImgCompany).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {

                            //System.out.println("WEB ICIT GO name = " + contact.getName() + ", web = " + contact.webSite);
                            String uri = selectedContact.webSite;
                            if (!uri.contains("https://") && !uri.contains("http://"))
                                uri = "https://" + uri;

                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(uri));

                            if (getActivity() != null)
                                getActivity().startActivity(i);
                            else
                                activityApp.startActivity(i);

                        } catch (Exception e) {

                        }
                    }
                });


            } else {
                ((TextView) mainView.findViewById(R.id.web_title)).setText("add web");
                ((TextView) mainView.findViewById(R.id.web_title)).setTextColor(getResources().getColor(R.color.gray));
                ((ImageView) mainView.findViewById(R.id.webImgCompany)).setImageResource(R.drawable.icn_popup_web);


                mainView.findViewById(R.id.web_title).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {

                            FrameLayout editFrameWeb = (FrameLayout) getActivity().findViewById(R.id.popupEditMain);
                            ((TextView) editFrameWeb.findViewById(R.id.typeField)).setText("Web");

                            ((ImageView) editFrameWeb.findViewById(R.id.imageEditSocial)).setVisibility(View.VISIBLE);
                            ((ImageView) editFrameWeb.findViewById(R.id.imageEditSocialInsta)).setVisibility(View.GONE);
                            ((ImageView) editFrameWeb.findViewById(R.id.imageEditSocial)).setImageDrawable(getResources().getDrawable(R.drawable.icn_popup_web));
                            ((TextView) editFrameWeb.findViewById(R.id.dataToEdit)).setText("");
                            editFrameWeb.findViewById(R.id.ok_social).setVisibility(View.GONE);

                            editFrameWeb.findViewById(R.id.searchSocial).setVisibility(View.VISIBLE);
                            ((EditText) editFrameWeb.findViewById(R.id.dataToEdit)).setHint("Add name or address");
                            ((EditText) editFrameWeb.findViewById(R.id.dataToEdit)).setPadding(0, 0, 150, 0);


                            editFrameWeb.findViewById(R.id.cancelEditField).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ((TextView) editFrameWeb.findViewById(R.id.dataToEdit)).setText("");
                                    editFrameWeb.setVisibility(View.GONE);
                                }
                            });

                            editFrameWeb.findViewById(R.id.updateContactField).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (((TextView) editFrameWeb.findViewById(R.id.dataToEdit)).getText().length() > 0) {


                                        Realm realm = Realm.getDefaultInstance();
                                        realm.beginTransaction();
                                        selectedContact.webSite = ((TextView) editFrameWeb.findViewById(R.id.dataToEdit)).getText().toString();
                                        realm.commitTransaction();
                                        realm.close();


                                        ((ImageView) mainView.findViewById(R.id.webImgCompany)).setImageResource(R.drawable.icn_popup_web_blue);
                                        ((TextView) mainView.findViewById(R.id.web_title)).setVisibility(View.VISIBLE);
                                        ((TextView) mainView.findViewById(R.id.web_title)).setText(selectedContact.webSite);
                                        ((TextView) mainView.findViewById(R.id.web_title)).setTextColor(getResources().getColor(R.color.primary_dark));

                                        mainView.findViewById(R.id.web_title).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                try {

                                                    //System.out.println("WEB ICIT GO name = " + contact.getName() + ", web = " + contact.webSite);
                                                    String uri = selectedContact.webSite;
                                                    if (!uri.contains("https://") && !uri.contains("http://"))
                                                        uri = "https://" + uri;

                                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                                    i.setData(Uri.parse(uri));

                                                    if (getActivity() != null)
                                                        getActivity().startActivity(i);
                                                    else
                                                        activityApp.startActivity(i);

                                                } catch (Exception e) {

                                                }
                                            }
                                        });

                                        mainView.findViewById(R.id.webImgCompany).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                try {

                                                    //System.out.println("WEB ICIT GO name = " + contact.getName() + ", web = " + contact.webSite);
                                                    String uri = selectedContact.webSite;
                                                    if (!uri.contains("https://") && !uri.contains("http://"))
                                                        uri = "https://" + uri;

                                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                                    i.setData(Uri.parse(uri));

                                                    if (getActivity() != null)
                                                        getActivity().startActivity(i);
                                                    else
                                                        activityApp.startActivity(i);

                                                } catch (Exception e) {

                                                }
                                            }
                                        });
                                    }
                                    editFrameWeb.setVisibility(View.GONE);
                                }
                            });


                            editFrameWeb.findViewById(R.id.get_last_clips).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    String socialLinkClip = "";
                                    for (Clipboard cl : FabNotificationService.clipboard) { //NullPointerException
                                        if (cl.getListClipboards() != null && !cl.getListClipboards().isEmpty()) {
                                            boolean checkFind = false;
                                            for (Clipboard cl2 : cl.getListClipboards()) {
                                                if (cl2.getType().equals(ClipboardEnum.WEB)) {
                                                    socialLinkClip = cl2.getValueCopy();
                                                    checkFind = true;
                                                    break;
                                                }
                                            }
                                            if (checkFind) break;
                                        } else {
                                            if (cl.getType().equals(ClipboardEnum.WEB)) {
                                                socialLinkClip = cl.getValueCopy();
                                                break;
                                            }
                                        }
                                    }
                                    ((EditText) editFrameWeb.findViewById(R.id.dataToEdit)).setText(socialLinkClip);
                                }
                            });


                            editFrameWeb.findViewById(R.id.imageEditSocial).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    try {
                                        if (((EditText) editFrameWeb.findViewById(R.id.dataToEdit)).length() > 0) {
                                            //System.out.println("WEB ICIT GO name = " + contact.getName() + ", web = " + contact.webSite);
                                            String uri = selectedContact.webSite;
                                            if (!uri.contains("https://") && !uri.contains("http://"))
                                                uri = "https://" + uri;

                                            Intent i = new Intent(Intent.ACTION_VIEW);
                                            i.setData(Uri.parse(uri));

                                            if (getActivity() != null)
                                                getActivity().startActivity(i);
                                            else
                                                activityApp.startActivity(i);
                                        }

                                    } catch (Exception e) {

                                    }
                                }
                            });

                            editFrameWeb.findViewById(R.id.imageEditSocial).setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    return false;
                                }
                            });

                            editFrameWeb.findViewById(R.id.searchSocial).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    try {

                                        //System.out.println("WEB ICIT GO name = " + contact.getName() + ", web = " + contact.webSite);
                                        if (((EditText) editFrameWeb.findViewById(R.id.dataToEdit)).length() > 0) {
                                            String uri = ((EditText) editFrameWeb.findViewById(R.id.dataToEdit)).getText().toString();

                                           /* if(ClipboardType.isWeb(uri)) {
                                                if (!uri.contains("https://") && !uri.contains("http://"))
                                                    uri = "https://" + uri;

                                                Intent i = new Intent(Intent.ACTION_VIEW);
                                                i.setData(Uri.parse(uri));

                                                if (getActivity() != null)
                                                    getActivity().startActivity(i);
                                            }else {*/
                                                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                                                intent.putExtra(SearchManager.QUERY, ((EditText) editFrameWeb.findViewById(R.id.dataToEdit)).getText().toString());
                                                if (getActivity() != null)
                                                    getActivity().startActivity(intent);
                                         //   }

                                           /* else
                                                MainActivity.activityProfile.startActivity(i);*/
                                        }

                                    } catch (Exception e) {

                                    }
                                }
                            });


                            editFrameWeb.setVisibility(View.VISIBLE);


                            openedViews.add(editFrameWeb);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }


        ((TextView) mainView.findViewById(R.id.name)).setText(company.getName());
        mainView.findViewById(R.id.name).setVisibility(View.VISIBLE);
        mainView.findViewById(R.id.editNameProflie).setVisibility(View.GONE);
        if (company.getCompany() != null) {
            ((TextView) mainView.findViewById(R.id.company_title)).setText(company.getCompany());

           /* ((TextView) mainView.findViewById(R.id.company_title)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    findByCompany(selectedContact.getCompany());
                }
            });*/

            if (company.getCompanyPossition() != null) {
                Double ems = company.getCompanyPossition().length() / 2.5;
                int ems_count = ems.intValue();
                if (ems_count < 6) {
                    ((TextView) mainView.findViewById(R.id.company_title)).setMaxEms(6 + (6 - ems_count));
                } else ((TextView) mainView.findViewById(R.id.company_title)).setMaxEms(6);
            } else {
                ((TextView) mainView.findViewById(R.id.company_title)).setMaxEms(12);
            }

            mainView.findViewById(R.id.company_title).setVisibility(View.VISIBLE);
        }
        mainView.findViewById(R.id.company_title_edit).setVisibility(View.GONE);
        if (company.getCompanyPossition() != null) {

            if (company.getCompany() != null) {
                Double ems = company.getCompany().length() / 2.5;
                int ems_count = ems.intValue();
                if (ems_count < 6) {
                    ((TextView) mainView.findViewById(R.id.company)).setMaxEms(6 + (6 - ems_count));
                } else ((TextView) mainView.findViewById(R.id.company)).setMaxEms(6);
            } else {
                ((TextView) mainView.findViewById(R.id.company)).setMaxEms(12);
            }

            ((TextView) mainView.findViewById(R.id.company)).setText(company.getCompanyPossition());
            mainView.findViewById(R.id.company).setVisibility(View.VISIBLE);
        }
        mainView.findViewById(R.id.company_edit).setVisibility(View.GONE);
        LinearLayout containerHashTags = (LinearLayout) mainView.findViewById(R.id.containerHashTags);
        if (containerHashTags.getChildCount() > 0)
            containerHashTags.removeAllViews();
        for (HashTag hashTag : company.getListOfHashtags()) {
            TextView text = new TextView(getActivity());
            text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            DisplayMetrics metrics = mainView.getContext().getResources().getDisplayMetrics();
            text.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            text.setTextColor(getResources().getColor(R.color.colorPrimary));
            text.setText(hashTag.getHashTagValue() + " ");
            text.setOnClickListener(v -> adapterC.searchByHashTagValue(hashTag.getHashTagValue()));
            containerHashTags.addView(text);
        }

        HorizontalScrollView scrollView = (HorizontalScrollView) mainView.findViewById(R.id.scrollHorizontal);
        if (scrollView.getChildCount() > 0) scrollView.removeAllViews();
        scrollView.addView(containerHashTags);
        scrollView.setSmoothScrollingEnabled(false);
        scrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
        scrollView.setSmoothScrollingEnabled(true);
        mainView.findViewById(R.id.hashtagsLL).setVisibility(View.VISIBLE);
        mainView.findViewById(R.id.hashTagsEdit).setVisibility(View.GONE);
        updateHashTags();
        System.out.println("MODEEEE ");

        realm.close();

    }

    public ArrayList<Contact> listCompanyForKeyDown;

    public ArrayList<String> listPosition;

    private void startEditMode() {

        Contact selectedContact = company;

        closeOtherPopup();

        mainView.findViewById(R.id.lineHashProfile).setVisibility(View.VISIBLE);
        mainView.findViewById(R.id.lineNameProfile).setVisibility(View.VISIBLE);
        //mainView.findViewById(R.id.starImgProfile).setVisibility(View.GONE);


        companyProfileDataFragment.closeOtherPopup();


        listPosition = new ArrayList<>();

        mainView.findViewById(R.id.name).setVisibility(View.GONE);

        EditText editNameProfile = (EditText) mainView.findViewById(R.id.editNameProflie);
        editNameProfile.setText(selectedContact.getName());
        editNameProfile.setVisibility(View.VISIBLE);
        editNameProfile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                editModeChecker = true;
            }
        });
        //===============================================

        //contactAdapter = ContactAdapter.contactAd;

        /*if(popupCompaniesEdit != null) popupCompaniesEdit.setVisibility(View.GONE);
        popupCompaniesEdit = null;
        if(popupPositionEdit != null) popupPositionEdit.setVisibility(View.GONE);
        popupPositionEdit = null;*/


        if (selectedContact.listOfContacts == null || selectedContact.listOfContacts.isEmpty()) {

        } else {
            //float px = 20 * getContext().getResources().getDisplayMetrics().density;

            float px_r = 23 * getContext().getResources().getDisplayMetrics().density;
            //float px_bot = 3 * getContext().getResources().getDisplayMetrics().density;

            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mainView.findViewById(R.id.lineNameProfile).getLayoutParams();
            params.setMarginEnd((int) px_r);

            //float px_1 = 2 * getContext().getResources().getDisplayMetrics().density;
            float px_r_2 = 22 * getContext().getResources().getDisplayMetrics().density;

            ViewGroup.MarginLayoutParams params2 = (ViewGroup.MarginLayoutParams) mainView.findViewById(R.id.editNameProflie).getLayoutParams();
            params2.setMarginEnd((int) px_r_2);

            //mainView.findViewById(R.id.editNameProflie).setLayoutParams(params2);

        }

        listCompanyForKeyDown = ContactCacheService.getCompanies();


        //  ((View) mainView.findViewById(R.id.lineNameProfile)).setBackgroundColor(getResources().getColor(R.color.md_deep_orange_300));
        mainView.findViewById(R.id.editNameProflie).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    ((View) mainView.findViewById(R.id.lineNameProfile)).setBackgroundColor(getResources().getColor(R.color.md_deep_orange_300));
                    if (popupHelpCompanyposition != null)
                        popupHelpCompanyposition.setVisibility(View.GONE);
                } else {
                    ((View) mainView.findViewById(R.id.lineNameProfile)).setBackgroundColor(getResources().getColor(R.color.sortPopupLine));
                }
            }
        });


        ((EditText) mainView.findViewById(R.id.company_edit)).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    System.out.println("POSITION FOCUS");
                    ((View) mainView.findViewById(R.id.positionVileEditPreview)).setBackgroundColor(getResources().getColor(R.color.md_deep_orange_300));
                    ((View) mainView.findViewById(R.id.lineCompanyProfile)).setBackgroundColor(getResources().getColor(R.color.sortPopupLine));

                   /* ArrayList<String> listOfPositions2 = new ArrayList<>(Arrays.asList("CEO","COO","Co-founder","CTO","Director","Engineer","Manager","Marketing"));
                    boolean find = false;
                    String text = ((EditText) mainView.findViewById(R.id.company_edit)).getText().toString().trim();
                    for(int i = 0;i<listOfPositions2.size();i++){
                        if(listOfPositions2.get(i).equals(text)){
                            find = true;
                            break;
                        }
                    }

                    if(popupPositionEdit != null) popupPositionEdit.setVisibility(View.GONE);

                    if(!find && ((EditText) mainView.findViewById(R.id.company_edit)).getText().toString().length() == 0) {
                        ArrayList<String> listOfPositions = new ArrayList<>(Arrays.asList("CEO", "COO", "Co-founder", "CTO", "Director", "Engineer", "Manager", "Marketing"));


                        System.out.println("CLICK Pos");
                        popupPositionEdit = (FrameLayout) getActivity().findViewById(R.id.companyAddPopupEdit);

                        popupPositionEdit.setVisibility(View.VISIBLE);

                        PositionEditAdapter selectCompanyAdapter = new PositionEditAdapter(listOfPositions, comp, getActivity());

                        RecyclerView containerCompanies = (RecyclerView) popupPositionEdit.findViewById(R.id.companiesContainer_edit);
                        RecyclerView.LayoutManager mostLayoutManager = new GridLayoutManager(mainView.getContext(), 2);
                        containerCompanies.setLayoutManager(mostLayoutManager);
                        //  containerMost.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
                        containerCompanies.setItemAnimator(new DefaultItemAnimator());
                        containerCompanies.setAdapter(selectCompanyAdapter);
                        openedViews.add(popupPositionEdit);
                    }else if(((EditText) mainView.findViewById(R.id.company_edit)).getText().toString().length() > 0 && !find) afterTextChangePosition(comp);*/


                    shopHelpPopupcompanyPosition();

                } else {
                    ((View) mainView.findViewById(R.id.positionVileEditPreview)).setBackgroundColor(getResources().getColor(R.color.sortPopupLine));
                    if (popupPositionEdit != null)
                        popupPositionEdit.setVisibility(View.GONE);

                    if (popupHelpCompanyposition != null)
                        popupHelpCompanyposition.setVisibility(View.GONE);
                }
            }
        });


        ((EditText) mainView.findViewById(R.id.company_edit)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                System.out.println("TEXT");
                if (((EditText) mainView.findViewById(R.id.company_edit)).isFocused()) {
                    if (popupHelpCompanyposition != null)
                        popupHelpCompanyposition.setVisibility(View.VISIBLE);
                    else
                        popupHelpCompanyposition = (FrameLayout) getActivity().findViewById(R.id.popupProfileCompanyPossitions);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                afterTextChangePosition(comp);
            }
        });


        ((EditText) mainView.findViewById(R.id.company_title_edit)).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    ((View) mainView.findViewById(R.id.lineCompanyProfile)).setBackgroundColor(getResources().getColor(R.color.md_deep_orange_300));
                    ((View) mainView.findViewById(R.id.positionVileEditPreview)).setBackgroundColor(getResources().getColor(R.color.sortPopupLine));

                    ArrayList<Contact> listOfCompanies2 = adapterC.getListOfCompanies();
                    boolean find = false;
                    String text = ((EditText) mainView.findViewById(R.id.company_title_edit)).getText().toString().trim();
                    for (int i = 0; i < listOfCompanies2.size(); i++) {
                        if (listOfCompanies2.get(i).getName().toString().trim().equals(text)) {
                            System.out.println("TRUE COMPANY");
                            find = true;
                            break;
                        }
                    }

                    System.out.println("CLICK company");
                    ArrayList<Contact> listComp = new ArrayList<>();
                    listComp.addAll(adapterC.getListOfCompanies());
                    if (listComp.size() > 0 && ((EditText) mainView.findViewById(R.id.company_title_edit)).getText().toString().length() == 0) {
                        //popupCompaniesEdit = (FrameLayout) getActivity().findViewById(R.id.companyAddPopupEdit);

                        //popupCompaniesEdit.setVisibility(View.VISIBLE);

                        CompanyEditAdapter selectCompanyAdapter = new CompanyEditAdapter(adapterC.getListOfCompanies(), comp, getActivity());

                        RecyclerView containerCompanies = (RecyclerView) getActivity().findViewById(R.id.companiesContainer_edit);
                        RecyclerView.LayoutManager mostLayoutManager = new GridLayoutManager(mainView.getContext(), 1);
                        containerCompanies.setLayoutManager(mostLayoutManager);
                        //  containerMost.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
                        containerCompanies.setItemAnimator(new DefaultItemAnimator());
                        containerCompanies.setAdapter(selectCompanyAdapter);
                        //openedViews.add(popupCompaniesEdit);


                        if (popupCompaniesEdit != null && find)
                            popupCompaniesEdit.setVisibility(View.GONE);
                    } else if (((EditText) mainView.findViewById(R.id.company_title_edit)).getText().toString().length() > 0 && !find)
                        afterTextChangeCompany(comp);


                    shopHelpPopupcompanyPosition();

                } else {
                    ((View) mainView.findViewById(R.id.lineCompanyProfile)).setBackgroundColor(getResources().getColor(R.color.sortPopupLine));
                    //if(popupCompaniesEdit != null)
                    //popupCompaniesEdit.setVisibility(View.GONE);

                    if (popupHelpCompanyposition != null)
                        popupHelpCompanyposition.setVisibility(View.GONE);
                }
            }
        });


        ((EditText) mainView.findViewById(R.id.company_title_edit)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (((EditText) mainView.findViewById(R.id.company_title_edit)).isFocused()) {
                    if (popupHelpCompanyposition != null)
                        popupHelpCompanyposition.setVisibility(View.VISIBLE);
                    else
                        popupHelpCompanyposition = (FrameLayout) getActivity().findViewById(R.id.popupProfileCompanyPossitions);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                afterTextChangeCompany(comp);

            }
        });


        ((EditText) mainView.findViewById(R.id.hashTagsEdit)).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    ((View) mainView.findViewById(R.id.lineHashProfile)).setBackgroundColor(getResources().getColor(R.color.md_deep_orange_300));
                    if (popupCompaniesEdit != null) popupCompaniesEdit.setVisibility(View.GONE);
                } else {
                    ((View) mainView.findViewById(R.id.lineHashProfile)).setBackgroundColor(getResources().getColor(R.color.sortPopupLine));
                }
            }
        });


        ((EditText) mainView.findViewById(R.id.company_edit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> listOfPositions = ContactCacheService.getPossitionContacts();
                boolean find = false;
                String text = ((EditText) mainView.findViewById(R.id.company_edit)).getText().toString().trim();
                for (int i = 0; i < listOfPositions.size(); i++) {
                    if (listOfPositions.get(i).equals(text)) {
                        find = true;
                        break;
                    }
                }
                if (popupPositionEdit != null && !find)
                    popupPositionEdit.setVisibility(View.VISIBLE);

                shopHelpPopupcompanyPosition();


            }
        });


        ((EditText) mainView.findViewById(R.id.company_title_edit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("CLICER");
                ArrayList<Contact> listOfCompanies = adapterC.getListOfCompanies();
                boolean find = false;
                String text = ((EditText) mainView.findViewById(R.id.company_title_edit)).getText().toString().trim();
                for (int i = 0; i < listOfCompanies.size(); i++) {
                    if (listOfCompanies.get(i).getName().toString().trim().equals(text)) {
                        System.out.println("TRUE COMPANY");
                        find = true;
                        break;
                    }
                }

                if (popupHelpCompanyposition != null && !find)
                    popupHelpCompanyposition.setVisibility(View.VISIBLE);

                shopHelpPopupcompanyPosition();


            }
        });

        mainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (popupHelpCompanyposition != null)
                    popupHelpCompanyposition.setVisibility(View.GONE);
                //if(popupPositionEdit != null) popupPositionEdit.setVisibility(View.GONE);
            }
        });

        //=================================================

        mainView.findViewById(R.id.company_title).setVisibility(View.GONE);

        EditText companyTitle = (EditText) mainView.findViewById(R.id.company_title_edit);
        companyTitle.setVisibility(View.VISIBLE);

        if (selectedContact.listOfContacts == null || selectedContact.listOfContacts.isEmpty()) {
            mainView.findViewById(R.id.lineCompanyProfile).setVisibility(View.VISIBLE);
            mainView.findViewById(R.id.positionVileEditPreview).setVisibility(View.VISIBLE);
            ((View) mainView.findViewById(R.id.lineCompanyProfileWeb)).setVisibility(View.GONE);


        } else {
            mainView.findViewById(R.id.lineCompanyProfile).setVisibility(View.GONE);
            mainView.findViewById(R.id.positionVileEditPreview).setVisibility(View.GONE);
            ((View) mainView.findViewById(R.id.lineCompanyProfileWeb)).setVisibility(View.VISIBLE);
            mainView.findViewById(R.id.web_title).setVisibility(View.GONE);
            mainView.findViewById(R.id.web_title_edit).setVisibility(View.VISIBLE);
            ((EditText) mainView.findViewById(R.id.web_title_edit)).setText(selectedContact.webSite);


            mainView.findViewById(R.id.web_title_edit).setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        ((View) mainView.findViewById(R.id.lineCompanyProfileWeb)).setBackgroundColor(getResources().getColor(R.color.md_deep_orange_300));
                        if (popupHelpCompanyposition != null)
                            popupHelpCompanyposition.setVisibility(View.GONE);
                    } else {
                        ((View) mainView.findViewById(R.id.lineCompanyProfileWeb)).setBackgroundColor(getResources().getColor(R.color.sortPopupLine));
                    }
                }
            });


        }
        if (selectedContact.getCompany() != null) {
            companyTitle.setText(selectedContact.getCompany());
        } else {
            companyTitle.setText("");
        }
        companyTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                editModeChecker = true;
            }
        });

        mainView.findViewById(R.id.company).setVisibility(View.GONE);

        EditText company = (EditText) mainView.findViewById(R.id.company_edit);
        company.setVisibility(View.VISIBLE);
        if (selectedContact.getCompanyPossition() != null) {
            company.setText(selectedContact.getCompanyPossition());
            // mainView.findViewById(R.id.lineCompanyProfile).setVisibility(View.VISIBLE);
        } else {
            company.setText("");
        }
        company.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                editModeChecker = true;
            }
        });
        mainView.findViewById(R.id.hashtagsLL).setVisibility(View.GONE);

        if (selectedContact.getListOfHashtags().size() >= 0) {
            String hashTagsStr = "";
            for (HashTag hashTag : selectedContact.getListOfHashtags()) {
                hashTagsStr += hashTag.getHashTagValue() + " ";
            }
            EditText hashtasgs = (EditText) mainView.findViewById(R.id.hashTagsEdit);
            hashtasgs.setVisibility(View.VISIBLE);
            hashtasgs.setText(hashTagsStr.trim());
            hashtasgs.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    editModeChecker = true;
                }
            });
        }
    }


    private void cancleEditMode() {

        Contact selectedContact = company;

        nowEdit = false;

        ((TextView) mainView.findViewById(R.id.name)).setText(selectedContact.getName());
        mainView.findViewById(R.id.name).setVisibility(View.VISIBLE);
        mainView.findViewById(R.id.editNameProflie).setVisibility(View.GONE);
        if (selectedContact.getCompany() != null) {
            ((TextView) mainView.findViewById(R.id.company_title)).setText(selectedContact.getCompany());
            mainView.findViewById(R.id.company_title).setVisibility(View.VISIBLE);
        }
        mainView.findViewById(R.id.company_title_edit).setVisibility(View.GONE);
        if (selectedContact.getCompanyPossition() != null) {
            ((TextView) mainView.findViewById(R.id.company)).setText(selectedContact.getCompanyPossition());
            mainView.findViewById(R.id.company).setVisibility(View.VISIBLE);
        }
        mainView.findViewById(R.id.company_edit).setVisibility(View.GONE);

        LinearLayout containerHashTags = (LinearLayout) mainView.findViewById(R.id.containerHashTags);
        if (containerHashTags.getChildCount() > 0)
            containerHashTags.removeAllViews();
        for (HashTag hashTag : selectedContact.getListOfHashtags()) {
            TextView text = new TextView(getActivity());
            text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            DisplayMetrics metrics = mainView.getContext().getResources().getDisplayMetrics();
            text.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            text.setTextColor(getResources().getColor(R.color.colorPrimary));
            text.setText(hashTag.getHashTagValue() + " ");

            containerHashTags.addView(text);
        }

        if (selectedContact != null && (selectedContact.getListOfHashtags() == null || selectedContact.getListOfHashtags().size() == 0)) {
            System.out.println("Add Hashtah TExt added");
            TextView text = new TextView(getActivity());
            text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            //DisplayMetrics metrics = mainView.getContext().getResources().getDisplayMetrics();
            text.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            text.setTextColor(getResources().getColor(R.color.gray));
            text.setText("hashtags");
            text.setOnClickListener(v1 -> {
                //containerHashTags.removeAllViews();
                showPopupUserHashtags(selectedContact);
            });
            containerHashTags.addView(text);
        }

        HorizontalScrollView scrollView = (HorizontalScrollView) mainView.findViewById(R.id.scrollHorizontal);
        if (scrollView.getChildCount() > 0) scrollView.removeAllViews();
        scrollView.addView(containerHashTags);
        scrollView.setSmoothScrollingEnabled(false);
        scrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
        scrollView.setSmoothScrollingEnabled(true);
        mainView.findViewById(R.id.hashtagsLL).setVisibility(View.VISIBLE);
        mainView.findViewById(R.id.hashTagsEdit).setVisibility(View.GONE);
        editModeChecker = false;

        mainView.findViewById(R.id.lineNameProfile).setVisibility(View.GONE);
        mainView.findViewById(R.id.lineCompanyProfile).setVisibility(View.GONE);
        ((View) mainView.findViewById(R.id.lineCompanyProfileWeb)).setVisibility(View.GONE);
        mainView.findViewById(R.id.positionVileEditPreview).setVisibility(View.GONE);
        mainView.findViewById(R.id.lineHashProfile).setVisibility(View.GONE);
        popupHelpCompanyposition.setVisibility(View.GONE);


            mainView.findViewById(R.id.web_title_edit).setVisibility(View.GONE);
            mainView.findViewById(R.id.web_title).setVisibility(View.VISIBLE);
            //mainView.findViewById(R.id.starImgProfile).setVisibility(View.GONE);



        initIconColor(selectedContact, mainView);
        /*if (selectedContact.getListOfContactInfo() != null)
            contactProfileDataFragment.contactNumberAdapter.setContactInfos(selectedContact.getListOfContactInfo());*/

        System.out.println("EDIT MODE END");
    }

    public String sortPopupComp = "sortByAsc";

    public boolean sortTimePopup = false;

    public boolean sortPopulPopup = false;

    //public CompanyEditAdapter selectCompanyAdapter;

    public PositionEditAdapter selectPositionAdapter;

    public void afterTextChangeCompany(CompanySelectInterface comp) {


        /*if(!((EditText) mainView.findViewById(R.id.company_title_edit)).isFocused() ) {
            popupCompaniesEdit.setVisibility(View.GONE);
            System.out.println("No FOCUS");
            return;
        }*/

        String ss = ((EditText) mainView.findViewById(R.id.company_title_edit)).getText().toString().toLowerCase();
        RealmList<Contact> listCompanies = new RealmList<>();
        ArrayList<Contact> listC = new ArrayList<>();
        listC.addAll(listCompanyForKeyDown);
        for (int i = 0; i < listC.size(); i++) {
            if (listC.get(i).getName().toLowerCase().contains(ss)) {
                listCompanies.add(listC.get(i));
            }
        }


        //if(listCompanies.size() == 0){
        //popupHelpCompanyposition.setVisibility(View.GONE);
        // }else {


        //popupHelpCompanyposition.setVisibility(View.VISIBLE);
        selectCompanyAdapter = new CompanyEditAdapter(listCompanies, comp, ss, getActivity());

        if (sortPopupComp.equals("sortByAsc")) {
            selectCompanyAdapter.sortCompanybyAsc();
            // selectPositionAdapter.sortPosByAsc();
            ((TextView) popupHelpCompanyposition.findViewById(R.id.sortTextPopup)).setTextColor(getActivity().getResources().getColor(R.color.primary));
            ((ImageView) popupHelpCompanyposition.findViewById(R.id.timeSortPopup)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
            ((ImageView) popupHelpCompanyposition.findViewById(R.id.populSort)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
            ((TextView) popupHelpCompanyposition.findViewById(R.id.sortTextPopup)).setText("A-Z");
            sortTimePopup = false;
        } else if (sortPopupComp.equals("sortByDesc")) {
            ((TextView) popupHelpCompanyposition.findViewById(R.id.sortTextPopup)).setTextColor(getActivity().getResources().getColor(R.color.primary));
            ((ImageView) popupHelpCompanyposition.findViewById(R.id.timeSortPopup)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
            ((ImageView) popupHelpCompanyposition.findViewById(R.id.populSort)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
            ((TextView) popupHelpCompanyposition.findViewById(R.id.sortTextPopup)).setText("Z-A");
            selectCompanyAdapter.sortCompanybyDesc();
            //selectPositionAdapter.sortPosByDesc();
        } else if (sortPopupComp.equals("sortByAscTime")) {
            ((TextView) popupHelpCompanyposition.findViewById(R.id.sortTextPopup)).setTextColor(getActivity().getResources().getColor(R.color.gray));
            ((ImageView) popupHelpCompanyposition.findViewById(R.id.timeSortPopup)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));
            ((ImageView) popupHelpCompanyposition.findViewById(R.id.populSort)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
            selectCompanyAdapter.sortByAscTime();
            sortPopupComp = "sortByAscTime";
            sortTimePopup = true;
        } else if (sortPopupComp.equals("sortByDescTime")) {
            ((TextView) popupHelpCompanyposition.findViewById(R.id.sortTextPopup)).setTextColor(getActivity().getResources().getColor(R.color.gray));
            ((ImageView) popupHelpCompanyposition.findViewById(R.id.timeSortPopup)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));
            ((ImageView) popupHelpCompanyposition.findViewById(R.id.populSort)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
            selectCompanyAdapter.sortByDescTime();
            sortPopupComp = "sortByDescTime";
            sortTimePopup = false;
        } else if (sortPopupComp.equals("sortByAscPopul")) {
            ((TextView) popupHelpCompanyposition.findViewById(R.id.sortTextPopup)).setTextColor(getActivity().getResources().getColor(R.color.gray));
            ((ImageView) popupHelpCompanyposition.findViewById(R.id.timeSortPopup)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
            ((ImageView) popupHelpCompanyposition.findViewById(R.id.populSort)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));
            selectCompanyAdapter.sortByAscPopul();
            selectPositionAdapter.sortByPopupAsc();
            sortPopupComp = "sortByAscPopul";
            sortPopulPopup = true;
        } else if (sortPopupComp.equals("sortByDescPopul")) {
            ((TextView) popupHelpCompanyposition.findViewById(R.id.sortTextPopup)).setTextColor(getActivity().getResources().getColor(R.color.gray));
            ((ImageView) popupHelpCompanyposition.findViewById(R.id.timeSortPopup)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
            ((ImageView) popupHelpCompanyposition.findViewById(R.id.populSort)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));
            selectCompanyAdapter.sortByDescPopul();
            selectPositionAdapter.sortByPopupDesc();
            sortPopupComp = "sortByDescPopul";
            sortPopulPopup = false;
        }

        RecyclerView containerCompanies = (RecyclerView) popupHelpCompanyposition.findViewById(R.id.recycleCompanyPopup);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mainView.getContext());
        containerCompanies.setLayoutManager(mLayoutManager);
        containerCompanies.setItemAnimator(new DefaultItemAnimator());
        containerCompanies.setAdapter(selectCompanyAdapter);

        //openedViews.add(popupCompaniesEdit);


        // }
    }

    CompanySelectInterface comp = this;


    public void shopHelpPopupcompanyPosition() {

        if (listPosition.isEmpty()) {
            listPosition = ContactCacheService.getPossitionContacts();
        }

        if (popupHelpCompanyposition.getVisibility() == View.GONE) {

            SharedPreferences mSettings;
            mSettings = getActivity().getSharedPreferences("SortEditPopup", Context.MODE_PRIVATE);
            sortPopupComp = mSettings.getString("Sort", "sortByDescPopul");
            //sortPopupComp = "sortByDescTime";

            RecyclerView recyclerCompany = (RecyclerView) getActivity().findViewById(R.id.recycleCompanyPopup);
            RecyclerView recyclerPosition = (RecyclerView) getActivity().findViewById(R.id.recyclePositionPopup);

            RecyclerView.LayoutManager mostLayoutManager = new LinearLayoutManager(mainView.getContext());
            recyclerCompany.setLayoutManager(mostLayoutManager);
            recyclerCompany.setItemAnimator(new DefaultItemAnimator());

            RecyclerView.LayoutManager mostLayoutManager2 = new LinearLayoutManager(mainView.getContext());
            recyclerPosition.setLayoutManager(mostLayoutManager2);
            recyclerPosition.setItemAnimator(new DefaultItemAnimator());

            ArrayList<Contact> listCompanies = adapterC.getListOfCompanies();
            ArrayList<String> listOfPositions = new ArrayList<>(listPosition);
            ArrayList<Contact> listCompaniesClone = new ArrayList<>(listCompanies);
            ArrayList<String> listOfPositionsClone = new ArrayList<>(listOfPositions);


            selectCompanyAdapter = new CompanyEditAdapter(listCompanies, comp, getActivity());

            selectPositionAdapter = new PositionEditAdapter(listOfPositions, comp, getActivity());

            if (sortPopupComp.equals("sortByAsc")) {
                selectCompanyAdapter.sortCompanybyAsc();
                selectPositionAdapter.sortPosByAsc();
                ((TextView) popupHelpCompanyposition.findViewById(R.id.sortTextPopup)).setTextColor(getActivity().getResources().getColor(R.color.primary));
                ((ImageView) popupHelpCompanyposition.findViewById(R.id.timeSortPopup)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
                ((ImageView) popupHelpCompanyposition.findViewById(R.id.populSort)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
                ((TextView) popupHelpCompanyposition.findViewById(R.id.sortTextPopup)).setText("A-Z");
                sortTimePopup = false;
            } else if (sortPopupComp.equals("sortByDesc")) {
                ((TextView) popupHelpCompanyposition.findViewById(R.id.sortTextPopup)).setTextColor(getActivity().getResources().getColor(R.color.primary));
                ((ImageView) popupHelpCompanyposition.findViewById(R.id.timeSortPopup)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
                ((ImageView) popupHelpCompanyposition.findViewById(R.id.populSort)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
                ((TextView) popupHelpCompanyposition.findViewById(R.id.sortTextPopup)).setText("Z-A");
                selectCompanyAdapter.sortCompanybyDesc();
                selectPositionAdapter.sortPosByDesc();
            } else if (sortPopupComp.equals("sortByAscTime")) {
                ((TextView) popupHelpCompanyposition.findViewById(R.id.sortTextPopup)).setTextColor(getActivity().getResources().getColor(R.color.gray));
                ((ImageView) popupHelpCompanyposition.findViewById(R.id.timeSortPopup)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));
                ((ImageView) popupHelpCompanyposition.findViewById(R.id.populSort)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
                selectCompanyAdapter.sortByAscTime();
                sortPopupComp = "sortByAscTime";
                sortTimePopup = true;
            } else if (sortPopupComp.equals("sortByDescTime")) {
                ((TextView) popupHelpCompanyposition.findViewById(R.id.sortTextPopup)).setTextColor(getActivity().getResources().getColor(R.color.gray));
                ((ImageView) popupHelpCompanyposition.findViewById(R.id.timeSortPopup)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));
                ((ImageView) popupHelpCompanyposition.findViewById(R.id.populSort)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
                selectCompanyAdapter.sortByDescTime();
                sortPopupComp = "sortByDescTime";
                sortTimePopup = false;
            } else if (sortPopupComp.equals("sortByAscPopul")) {
                ((TextView) popupHelpCompanyposition.findViewById(R.id.sortTextPopup)).setTextColor(getActivity().getResources().getColor(R.color.gray));
                ((ImageView) popupHelpCompanyposition.findViewById(R.id.timeSortPopup)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
                ((ImageView) popupHelpCompanyposition.findViewById(R.id.populSort)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));
                selectCompanyAdapter.sortByAscPopul();
                selectPositionAdapter.sortByPopupAsc();
                sortPopupComp = "sortByAscPopul";
                sortPopulPopup = true;
            } else if (sortPopupComp.equals("sortByDescPopul")) {
                ((TextView) popupHelpCompanyposition.findViewById(R.id.sortTextPopup)).setTextColor(getActivity().getResources().getColor(R.color.gray));
                ((ImageView) popupHelpCompanyposition.findViewById(R.id.timeSortPopup)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
                ((ImageView) popupHelpCompanyposition.findViewById(R.id.populSort)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));
                selectCompanyAdapter.sortByDescPopul();
                selectPositionAdapter.sortByPopupDesc();
                sortPopupComp = "sortByDescPopul";
                sortPopulPopup = false;
            }


            recyclerCompany.setAdapter(selectCompanyAdapter);


            recyclerPosition.setAdapter(selectPositionAdapter);

            popupHelpCompanyposition.setVisibility(View.VISIBLE);

            openedViews.add(popupHelpCompanyposition);


            popupHelpCompanyposition.findViewById(R.id.sortElementsPopup).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (((TextView) popupHelpCompanyposition.findViewById(R.id.sortTextPopup)).getText().toString().equals("A-Z")) {

                        ((TextView) popupHelpCompanyposition.findViewById(R.id.sortTextPopup)).setTextColor(getActivity().getResources().getColor(R.color.primary));
                        ((ImageView) popupHelpCompanyposition.findViewById(R.id.timeSortPopup)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
                        ((ImageView) popupHelpCompanyposition.findViewById(R.id.populSort)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
                        ((TextView) popupHelpCompanyposition.findViewById(R.id.sortTextPopup)).setText("Z-A");
                        selectCompanyAdapter.sortCompanybyDesc();
                        selectPositionAdapter.sortPosByDesc();
                        sortPopupComp = "sortByDesc";

                        SharedPreferences.Editor editor = mSettings.edit();
                        editor.putString("Sort", "sortByDesc");
                        editor.commit();

                        //sortTimePopup = false;

                    } else {
                        ((TextView) popupHelpCompanyposition.findViewById(R.id.sortTextPopup)).setTextColor(getActivity().getResources().getColor(R.color.primary));
                        ((ImageView) popupHelpCompanyposition.findViewById(R.id.timeSortPopup)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
                        ((ImageView) popupHelpCompanyposition.findViewById(R.id.populSort)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
                        ((TextView) popupHelpCompanyposition.findViewById(R.id.sortTextPopup)).setText("A-Z");
                        selectCompanyAdapter.sortCompanybyAsc();
                        selectPositionAdapter.sortPosByAsc();
                        sortPopupComp = "sortByAsc";

                        SharedPreferences.Editor editor = mSettings.edit();
                        editor.putString("Sort", "sortByAsc");
                        editor.commit();

                        //sortTimePopup = false;
                    }
                }
            });

            popupHelpCompanyposition.findViewById(R.id.sortByTimePopup).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!sortTimePopup) {
                        ((TextView) popupHelpCompanyposition.findViewById(R.id.sortTextPopup)).setTextColor(getActivity().getResources().getColor(R.color.gray));
                        ((ImageView) popupHelpCompanyposition.findViewById(R.id.timeSortPopup)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));
                        ((ImageView) popupHelpCompanyposition.findViewById(R.id.populSort)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
                        selectCompanyAdapter.sortByAscTime();
                        sortPopupComp = "sortByAscTime";
                        sortTimePopup = true;

                        SharedPreferences.Editor editor = mSettings.edit();
                        editor.putString("Sort", "sortByAscTime");
                        editor.commit();

                    } else {
                        ((TextView) popupHelpCompanyposition.findViewById(R.id.sortTextPopup)).setTextColor(getActivity().getResources().getColor(R.color.gray));
                        ((ImageView) popupHelpCompanyposition.findViewById(R.id.timeSortPopup)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));
                        ((ImageView) popupHelpCompanyposition.findViewById(R.id.populSort)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
                        selectCompanyAdapter.sortByDescTime();
                        sortPopupComp = "sortByDescTime";
                        sortTimePopup = false;

                        SharedPreferences.Editor editor = mSettings.edit();
                        editor.putString("Sort", "sortByDescTime");
                        editor.commit();

                    }
                }
            });

            popupHelpCompanyposition.findViewById(R.id.sortByPopupPopup).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!sortPopulPopup) {
                        ((TextView) popupHelpCompanyposition.findViewById(R.id.sortTextPopup)).setTextColor(getActivity().getResources().getColor(R.color.gray));
                        ((ImageView) popupHelpCompanyposition.findViewById(R.id.timeSortPopup)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
                        ((ImageView) popupHelpCompanyposition.findViewById(R.id.populSort)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));

                        ArrayList<Contact> listCompanies = new ArrayList<>(listCompaniesClone);

                        ArrayList<String> listOfPositions = new ArrayList<>(listOfPositionsClone);

                        selectCompanyAdapter.updateList(listCompanies);
                        selectPositionAdapter.updateList(listOfPositions);

                        selectCompanyAdapter.sortByAscPopul();
                        selectPositionAdapter.sortByPopupAsc();
                        sortPopupComp = "sortByAscPopul";
                        sortPopulPopup = true;

                        SharedPreferences.Editor editor = mSettings.edit();
                        editor.putString("Sort", "sortByAscPopul");
                        editor.commit();

                    } else {
                        ((TextView) popupHelpCompanyposition.findViewById(R.id.sortTextPopup)).setTextColor(getActivity().getResources().getColor(R.color.gray));
                        ((ImageView) popupHelpCompanyposition.findViewById(R.id.timeSortPopup)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
                        ((ImageView) popupHelpCompanyposition.findViewById(R.id.populSort)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));

                        ArrayList<Contact> listCompanies = new ArrayList<>(listCompaniesClone);

                        ArrayList<String> listOfPositions = new ArrayList<>(listOfPositionsClone);

                        selectCompanyAdapter.updateList(listCompanies);
                        selectPositionAdapter.updateList(listOfPositions);

                        selectCompanyAdapter.sortByDescPopul();
                        selectPositionAdapter.sortByPopupDesc();
                        sortPopupComp = "sortByDescPopul";
                        sortPopulPopup = false;

                        SharedPreferences.Editor editor = mSettings.edit();
                        editor.putString("Sort", "sortByDescPopul");
                        editor.commit();

                    }
                }
            });


        }
    }

    @Override
    public void addSelectedCompany(String company) {

    }

    @Override
    public void addSelectedCompanyEdit(String company) {
        ((EditText) mainView.findViewById(R.id.company_title_edit)).setText(company);
        ((EditText) mainView.findViewById(R.id.company_title_edit)).setSelection(((EditText) mainView.findViewById(R.id.company_title_edit)).getText().length());
    }

    @Override
    public void addSelectPosition(String position) {
        ((EditText) mainView.findViewById(R.id.company_edit)).setText(position);
        ((EditText) mainView.findViewById(R.id.company_edit)).setSelection(((EditText) mainView.findViewById(R.id.company_edit)).getText().length());
    }

    private Time getRandomDate() {
        final Random random = new Random();
        final int millisInDay = 24 * 60 * 60 * 1000;
        return new Time((long) random.nextInt(millisInDay));
    }


    public void afterTextChangePosition(CompanySelectInterface comp) {
        String ss = ((EditText) mainView.findViewById(R.id.company_edit)).getText().toString().toLowerCase();
        ArrayList<String> listOfPositions = new ArrayList<>(listPosition);
        ArrayList<String> listC = new ArrayList<>();
        //listC.addAll(listOfPositions);
        for (int i = 0; i < listOfPositions.size(); i++) {
            if (listOfPositions.get(i).toString().toLowerCase().contains(ss)) {
                listC.add(listOfPositions.get(i));
            }
        }
        //if(listC.size() == 0){
        //popupPositionEdit.setVisibility(View.GONE);
        //popupHelpCompanyposition.setVisibility(View.GONE);
        // }else {
        //popupHelpCompanyposition.setVisibility(View.VISIBLE);

        selectPositionAdapter = new PositionEditAdapter(listC, comp, ss, getActivity());

        if (sortPopupComp.equals("sortByAsc")) {
            //selectCompanyAdapter.sortCompanybyAsc();
            selectPositionAdapter.sortPosByAsc();
            ((TextView) popupHelpCompanyposition.findViewById(R.id.sortTextPopup)).setTextColor(getActivity().getResources().getColor(R.color.primary));
            ((ImageView) popupHelpCompanyposition.findViewById(R.id.timeSortPopup)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
            ((ImageView) popupHelpCompanyposition.findViewById(R.id.populSort)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
            ((TextView) popupHelpCompanyposition.findViewById(R.id.sortTextPopup)).setText("A-Z");
            sortTimePopup = false;
        } else if (sortPopupComp.equals("sortByDesc")) {
            ((TextView) popupHelpCompanyposition.findViewById(R.id.sortTextPopup)).setTextColor(getActivity().getResources().getColor(R.color.primary));
            ((ImageView) popupHelpCompanyposition.findViewById(R.id.timeSortPopup)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
            ((ImageView) popupHelpCompanyposition.findViewById(R.id.populSort)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
            ((TextView) popupHelpCompanyposition.findViewById(R.id.sortTextPopup)).setText("Z-A");
            //selectCompanyAdapter.sortCompanybyDesc();
            selectPositionAdapter.sortPosByDesc();
        } else if (sortPopupComp.equals("sortByAscPopul")) {
            ((TextView) popupHelpCompanyposition.findViewById(R.id.sortTextPopup)).setTextColor(getActivity().getResources().getColor(R.color.gray));
            ((ImageView) popupHelpCompanyposition.findViewById(R.id.timeSortPopup)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
            ((ImageView) popupHelpCompanyposition.findViewById(R.id.populSort)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));
            selectPositionAdapter.sortByPopupAsc();
            sortPopupComp = "sortByAscPopul";
            sortPopulPopup = true;
        } else if (sortPopupComp.equals("sortByDescPopul")) {
            ((TextView) popupHelpCompanyposition.findViewById(R.id.sortTextPopup)).setTextColor(getActivity().getResources().getColor(R.color.gray));
            ((ImageView) popupHelpCompanyposition.findViewById(R.id.timeSortPopup)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
            ((ImageView) popupHelpCompanyposition.findViewById(R.id.populSort)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));
            selectPositionAdapter.sortByPopupDesc();
            sortPopupComp = "sortByDescPopul";
            sortPopulPopup = false;
        }

        RecyclerView containerCompanies = (RecyclerView) popupHelpCompanyposition.findViewById(R.id.recyclePositionPopup);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mainView.getContext());
        containerCompanies.setLayoutManager(mLayoutManager);
        containerCompanies.setItemAnimator(new DefaultItemAnimator());
        containerCompanies.setAdapter(selectPositionAdapter);

        //openedViews.add(popupPositionEdit);

            /*if(!((EditText) mainView.findViewById(R.id.company_edit)).isFocused() ) {
                //popupPositionEdit.setVisibility(View.GONE);
                System.out.println("No FOCUS");
            }*/
        // }
    }
}
