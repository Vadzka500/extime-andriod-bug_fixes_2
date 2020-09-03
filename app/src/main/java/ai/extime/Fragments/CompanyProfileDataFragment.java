package ai.extime.Fragments;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
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
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
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
import android.util.Patterns;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionMenu;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberToCarrierMapper;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.w3c.dom.Text;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;

import ai.extime.Activity.MainActivity;
import ai.extime.Adapters.CustomTagsAdapter;
import ai.extime.Adapters.DialogAdapter;
import ai.extime.Adapters.HashtagMasstaggingAdapter;
import ai.extime.Adapters.MostUsedTagsAdapter;
import ai.extime.Adapters.SelectPositionAdapter;
import ai.extime.Enums.ClipboardEnum;
import ai.extime.Enums.SocEnum;
import ai.extime.Events.AddContact;
import ai.extime.Events.ClickBackOnProfile;
import ai.extime.Events.RemoveContact;
import ai.extime.Events.UpdateMessengersInCompany;
import ai.extime.Events.UpdateMessengersPreview;
import ai.extime.Interfaces.HashtagAddInterface;
import ai.extime.Interfaces.PopupsInter;
import ai.extime.Interfaces.Postman;
import ai.extime.Models.Clipboard;
import ai.extime.Models.DataUpdate;
import ai.extime.Models.HashTag;
import ai.extime.Models.HashTagQuantity;
import ai.extime.Services.ContactCacheService;
import ai.extime.Services.FabNotificationService;
import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import ai.extime.Adapters.CompanyAdapter;
import ai.extime.Adapters.ContactAdapter;
import ai.extime.Enums.SocialEnums;
import ai.extime.Events.AnimColorMessenger;
import ai.extime.Events.UpdateFile;
import ai.extime.Models.Contact;
import ai.extime.Models.ContactInfo;
import ai.extime.Models.SocialModel;

import com.extime.R;

import ai.extime.Services.ContactsService;
import ai.extime.Utils.ClipboardType;

/**
 * Created by patal on 21.09.2017.
 */

public class CompanyProfileDataFragment extends Fragment implements PopupsInter, HashtagAddInterface {

    View mainView;

    View rootView;

    RecyclerView recyclerView;

    public Activity activityApp;

    private ContactAdapter adapterC;

    private Toolbar toolbarC;

    public Contact selectedContact;

    String lastOpenedProfileId = "";

    public CompanyAdapter companyAdapter;

    FrameLayout profilePopUp;

    public ContactAdapter contactAdapter;

    FrameLayout socialPopup;

    Contact selectedContactPopup;

    ContactsService contactsService;

    private FrameLayout fastEditPopup;

    private FrameLayout remindPopup;

    private SocialModel socialModel;

    private FrameLayout popupProfileEditPreview;

    public SharedPreferences mPref;

    private boolean sortedDesc;

    private boolean sortTimeAsc = false;

    private ArrayList<View> openedViews = new ArrayList<>();

    private FrameLayout popupProfileEditPreviewSocial;
    private FrameLayout popupEditSocial;

    FrameLayout popupUserHashtags;
    FrameLayout companySelectPopup;
    FrameLayout mainCompanyPopup;

    public FrameLayout popupProfileEdit;

    public FrameLayout popupEditDataCompany;

    CustomTagsAdapter customTagsAdapter;
    RecyclerView containerAssistant;
    MostUsedTagsAdapter mostTagAdapter;
    RecyclerView containerMost;

    public void updateConatcts() {
        ((ProfileFragment) getParentFragment()).upd();
    }

    private void initViews() {
        recyclerView = (RecyclerView) mainView.findViewById(R.id.recycler_view);
//        if(selectedContact.listOfContacts.size() > 1) companyCount.setText(selectedContact.listOfContacts.size()+" contacts");
//        if(selectedContact.listOfContacts.size() == 1) companyCount.setText(selectedContact.listOfContacts.size()+" contact");

        popupProfileEditPreview = (FrameLayout) getParentFragment().getView().findViewById(R.id.popupPreviewEdit);
        popupProfileEdit = (FrameLayout) getParentFragment().getView().findViewById(R.id.popupEditProfile);
        popupEditDataCompany = (FrameLayout) getParentFragment().getView().findViewById(R.id.popupEditDataCompany);

        recyclerView.setLayoutManager(new LinearLayoutManager(mainView.getContext()));
        companyAdapter = new CompanyAdapter(mainView.getContext(), selectedContact.listOfContacts, this, contactsService, toolbarC, selectedContact);
        recyclerView.setAdapter(companyAdapter);

        if (selectedContact.listOfContacts.size() == 1) {
            ((TextView) mainView.findViewById(R.id.count_contact_company_preview)).setText("1 contact");
        } else {
            ((TextView) mainView.findViewById(R.id.count_contact_company_preview)).setText(String.valueOf(selectedContact.listOfContacts.size()) + " contacts");
        }


        mainView.findViewById(R.id.companyCloser).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ProfileFragment) getParentFragment()).closeOtherPopup();
                closeOtherPopup();
                companyAdapter.notifyDataSetChanged();
            }
        });

        mainView.findViewById(R.id.sortElementsCompany).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortList();
            }
        });

        mainView.findViewById(R.id.sortByPopular).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mainView.findViewById(R.id.sortByTimeCompany).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TextView) mainView.findViewById(R.id.sortText)).setTextColor(getResources().getColor(R.color.gray));
                ((ImageView) mainView.findViewById(R.id.timeSort)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));

                if (((ProfileFragment) getParentFragment()).companyContactsFragment.mainView != null)
                    ((ProfileFragment) getParentFragment()).companyContactsFragment.setTime();
                sortByTime();

                updateConatcts();
            }
        });


        FrameLayout frame = (FrameLayout) mainView.findViewById(R.id.companyCloser);

        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                frame.dispatchTouchEvent(motionEvent);

                ((ProfileFragment) getParentFragment()).closeOtherPopup();
                closeOtherPopup();
                //companyAdapter.notifyDataSetChanged();


                return false;
            }
        });

    }


    private void getDataFromBundle() {
        Bundle args = getArguments();
        selectedContact = (Contact) args.getSerializable("selectedContact");
    }

    public void sortByTime() {

        if (!sortTimeAsc) {
            sortByTimeDesc();

            sortTimeAsc = true;
        } else {
            sortByTimeAsc();
            sortTimeAsc = false;
        }
        //companyAdapter.notifyDataSetChanged();
    }

    public void sortByTimeAsc() {
        sortTimeAsc = false;
        companyAdapter.sortByTimeAsc();
    }

    public void sortByTimeDesc() {
        sortTimeAsc = true;
        companyAdapter.sortByTimeDesc();
    }

    public void sortList() {
        if (sortedDesc) {
            sortArrayByAsc();
            sortedDesc = false;

            updateConatcts();
            return;
        }
        sortArrayByDesc();
        sortedDesc = true;

        updateConatcts();
    }

    private void sortArrayByDesc() {
        ((TextView) mainView.findViewById(R.id.sortText)).setTextColor(getResources().getColor(R.color.primary));
        ((ImageView) mainView.findViewById(R.id.timeSort)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
        companyAdapter.sortByDesc();
        TextView sortText = (TextView) mainView.findViewById(R.id.sortText);
        sortText.setText("Z-A");

        if (((ProfileFragment) getParentFragment()).companyContactsFragment.mainView != null)
            ((ProfileFragment) getParentFragment()).companyContactsFragment.setTimeD();

    }

    private void sortArrayByAsc() {
        ((TextView) mainView.findViewById(R.id.sortText)).setTextColor(getResources().getColor(R.color.primary));
        ((ImageView) mainView.findViewById(R.id.timeSort)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
        companyAdapter.sortByAsc();
        TextView sortText = (TextView) mainView.findViewById(R.id.sortText);
        sortText.setText("A-Z");
        if (((ProfileFragment) getParentFragment()).companyContactsFragment.mainView != null)
            ((ProfileFragment) getParentFragment()).companyContactsFragment.setTAsc();


    }

    public void initAdapter() {
        mPref = getContext().getSharedPreferences("Sort", Context.MODE_PRIVATE);
        String sort = mPref.getString("typeSortCompany", "sortByTimeDesc");

        if (sort.equals("sortByAsc")) {
            companyAdapter.sortByAsc();
            sortedDesc = false;
            ((TextView) mainView.findViewById(R.id.sortText)).setTextColor(getResources().getColor(R.color.primary));
            ((TextView) mainView.findViewById(R.id.sortText)).setText("A-Z");
            ((ImageView) mainView.findViewById(R.id.timeSort)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
        } else if (sort.equals("sortByDesc")) {
            companyAdapter.sortByDesc();
            sortedDesc = true;
            ((TextView) mainView.findViewById(R.id.sortText)).setTextColor(getResources().getColor(R.color.primary));
            ((TextView) mainView.findViewById(R.id.sortText)).setText("Z-A");
            ((ImageView) mainView.findViewById(R.id.timeSort)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
        } else if (sort.equals("sortByTimeAsc")) {
            companyAdapter.sortByTimeAsc();
            sortTimeAsc = false;
            ((TextView) mainView.findViewById(R.id.sortText)).setTextColor(getResources().getColor(R.color.gray));
            ((ImageView) mainView.findViewById(R.id.timeSort)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));
        } else if (sort.equals("sortByTimeDesc")) {
            companyAdapter.sortByTimeDesc();
            sortTimeAsc = true;
            ((TextView) mainView.findViewById(R.id.sortText)).setTextColor(getResources().getColor(R.color.gray));
            ((ImageView) mainView.findViewById(R.id.timeSort)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle bundle) {
        mainView = inflater.inflate(R.layout.fragment_company_profile_data_list, viewGroup, false);
        rootView = mainView.getRootView();
        contactsService = new ContactsService(getActivity().getContentResolver(), false);

        adapterC = ((Postman) activityApp).getAdapter();
        toolbarC = ((Postman) activityApp).getToolbar();

        getDataFromBundle();
        initViews();
        initAdapter();
        return mainView;
    }

    public int[] number_of_clicks = {0};
    public boolean[] thread_started = {false};
    public int DELAY_BETWEEN_CLICKS_IN_MILLISECONDS = 250;

    public void showProfilePopUp(final Contact contact) {

        closeOtherPopup();
        profilePopUp = (FrameLayout) mainView.getRootView().findViewById(R.id.profile_popup);

        profilePopUp.findViewById(R.id.popupStar).setVisibility(View.GONE);

        ((ImageView) profilePopUp.findViewById(R.id.emailImg)).setImageResource(R.drawable.icn_bottombar_emails);
        //  ((ImageView)profilePopUp.findViewById(R.id.webImg)).setImageResource(R.drawable.icn_popup_web);
        profilePopUp.findViewById(R.id.emailImg).setOnClickListener(v -> {
        });
        //  profilePopUp.findViewById(R.id.webImg).setOnClickListener(v -> {});
        LinearLayout containerHashTags = (LinearLayout) profilePopUp.findViewById(R.id.containerHashTags);
        if (containerHashTags.getChildCount() > 0)
            containerHashTags.removeAllViews();
        for (HashTag hashTag : contact.getListOfHashtags()) {
            TextView text = new TextView(getActivity());
            text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            DisplayMetrics metrics = mainView.getContext().getResources().getDisplayMetrics();
            text.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            text.setTextColor(getResources().getColor(R.color.colorPrimary));
            text.setText(hashTag.getHashTagValue() + " ");
//            text.setOnClickListener(v -> contactAdapter.searchByHashTagValue(hashTag.getHashTagValue()));
            containerHashTags.addView(text);
        }

        if (contact.getListOfHashtags().size() == 0) {
            TextView text = new TextView(getActivity());
            text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            DisplayMetrics metrics = mainView.getContext().getResources().getDisplayMetrics();
            text.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            text.setTextColor(getResources().getColor(R.color.gray));
            text.setText("hashtags");
            text.setOnClickListener(v -> {
                containerHashTags.removeAllViews();
                showPopupUserHashtags(contact);
            });
            containerHashTags.addView(text);
        }

        if (contact.isFavorite || contact.isPause || contact.isFinished || contact.isImportant || contact.isCrown || contact.isVip || contact.isStartup || contact.isInvestor) {
            if (contact.isFavorite) {
                ((ImageView) profilePopUp.findViewById(R.id.starImg)).setImageDrawable(getResources().getDrawable(R.drawable.star));
            } else if (contact.isImportant) {
                ((ImageView) profilePopUp.findViewById(R.id.starImg)).setImageDrawable(getResources().getDrawable(R.drawable.checked_2));
            } else if (contact.isFinished) {
                ((ImageView) profilePopUp.findViewById(R.id.starImg)).setImageDrawable(getResources().getDrawable(R.drawable.finish_1));
            } else if (contact.isPause) {
                ((ImageView) profilePopUp.findViewById(R.id.starImg)).setImageDrawable(getResources().getDrawable(R.drawable.pause_1));
            } else if (contact.isCrown) {
                ((ImageView) profilePopUp.findViewById(R.id.starImg)).setImageDrawable(getResources().getDrawable(R.drawable.crown));
            } else if (contact.isVip) {
                ((ImageView) profilePopUp.findViewById(R.id.starImg)).setImageDrawable(getResources().getDrawable(R.drawable.vip_new));
            } else if (contact.isStartup) {
                ((ImageView) profilePopUp.findViewById(R.id.starImg)).setImageDrawable(getResources().getDrawable(R.drawable.startup));
            } else if (contact.isInvestor) {
                ((ImageView) profilePopUp.findViewById(R.id.starImg)).setImageDrawable(getResources().getDrawable(R.drawable.investor_));
            }
            profilePopUp.findViewById(R.id.starImg).setVisibility(View.VISIBLE);
        } else {
            profilePopUp.findViewById(R.id.starImg).setVisibility(View.GONE);
        }

        //     LinearLayout containerHashTags = (LinearLayout) parentFragment.getActivity().findViewById(R.id.containerHashTags);
       /* if(contact.getListOfHashtags().size()==0){
            TextView text = new TextView(getContext());
            text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            DisplayMetrics metrics = mainView.getContext().getResources().getDisplayMetrics();
            text.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            text.setTextColor(getResources().getColor(R.color.gray));
            text.setText("hashtags");
            text.setOnClickListener(v -> {
                   containerHashTags.removeAllViews();
                   showPopupUserHashtags(contact);
            });
            containerHashTags.addView(text);
        }*/


        popupUserHashtags = (FrameLayout) profilePopUp.findViewById(R.id.popupProfileHashtags);
        companySelectPopup = (FrameLayout) getActivity().findViewById(R.id.positionAddPopup);


        selectedContactPopup = contact;
        lastOpenedProfileId = String.valueOf(contact.getId());


            Calendar current = Calendar.getInstance();
            Calendar contactDate = Calendar.getInstance();
            current.setTime(new Date());
            contactDate.setTime(contact.getDateCreate());
            String timeStr = "";
            if (current.get(Calendar.YEAR) == contactDate.get(Calendar.YEAR) && current.get(Calendar.MONTH) == contactDate.get(Calendar.MONTH) && current.get(Calendar.DAY_OF_MONTH) == contactDate.get(Calendar.DAY_OF_MONTH)) {

                timeStr = Time.valueOf(selectedContact.time).getHours() + ":";
                if (Time.valueOf(selectedContact.time).getMinutes() < 10) timeStr += "0";
                timeStr += Time.valueOf(selectedContact.time).getMinutes();
            } else if (current.get(Calendar.YEAR) == contactDate.get(Calendar.YEAR) && (current.get(Calendar.MONTH) != contactDate.get(Calendar.MONTH) || current.get(Calendar.DAY_OF_MONTH) != contactDate.get(Calendar.DAY_OF_MONTH))) {


                timeStr = contactDate.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.ENGLISH) + " " + contactDate.get(Calendar.DAY_OF_MONTH);
            } else {

                timeStr = contactDate.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.ENGLISH) + " " + String.valueOf(contactDate.get(Calendar.YEAR))/*.substring(2)*/;

            }


            ((TextView)profilePopUp.findViewById(R.id.cardTime)).setText(/*getUpdTime(contact.getDateCreate(), Time.valueOf(contact.time))*/ timeStr);

        profilePopUp.findViewById(R.id.starImg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int[] location = new int[2];
                profilePopUp.findViewById(R.id.starImg).getLocationOnScreen(location);


                ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) profilePopUp.findViewById(R.id.popupStar).getLayoutParams();
                lp.leftMargin = (int) (location[0] - 15.5 * getContext().getResources().getDisplayMetrics().density);
                lp.topMargin = (int) (1 * getContext().getResources().getDisplayMetrics().density);


                profilePopUp.findViewById(R.id.popupStar).requestLayout();
                profilePopUp.findViewById(R.id.popupStar).setVisibility(View.VISIBLE);

                profilePopUp.findViewById(R.id.popupStar).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                if (!contact.isFavorite) {
                    ((ImageView) profilePopUp.findViewById(R.id.favoriteContactIcon)).setColorFilter(ContextCompat.getColor(getContext(), R.color.gray), android.graphics.PorterDuff.Mode.SRC_IN);
                } else {
                    ((ImageView) profilePopUp.findViewById(R.id.favoriteContactIcon)).setColorFilter(null);
                }

                if (!contact.isImportant) {
                    ((ImageView) profilePopUp.findViewById(R.id.importantContactIcon)).setColorFilter(ContextCompat.getColor(getContext(), R.color.gray), android.graphics.PorterDuff.Mode.SRC_IN);
                } else {
                    ((ImageView) profilePopUp.findViewById(R.id.importantContactIcon)).setColorFilter(null);
                }

                if (!contact.isFinished) {
                    ((ImageView) profilePopUp.findViewById(R.id.finishedContactIcon)).setColorFilter(ContextCompat.getColor(getContext(), R.color.gray), android.graphics.PorterDuff.Mode.SRC_IN);
                } else {
                    ((ImageView) profilePopUp.findViewById(R.id.finishedContactIcon)).setColorFilter(null);
                }

                if (!contact.isPause) {
                    ((ImageView) profilePopUp.findViewById(R.id.pauseContactIcon)).setColorFilter(ContextCompat.getColor(getContext(), R.color.gray), android.graphics.PorterDuff.Mode.SRC_IN);
                } else {
                    ((ImageView) profilePopUp.findViewById(R.id.pauseContactIcon)).setColorFilter(null);
                }

                if (!contact.isCrown) {
                    ((ImageView) profilePopUp.findViewById(R.id.crownContactIcon)).setColorFilter(ContextCompat.getColor(getContext(), R.color.gray), android.graphics.PorterDuff.Mode.SRC_IN);
                } else {
                    ((ImageView) profilePopUp.findViewById(R.id.crownContactIcon)).setColorFilter(null);
                }

                if (!contact.isVip) {
                    ((ImageView) profilePopUp.findViewById(R.id.vipContactIcon)).setColorFilter(ContextCompat.getColor(getContext(), R.color.gray), android.graphics.PorterDuff.Mode.SRC_IN);
                } else {
                    ((ImageView) profilePopUp.findViewById(R.id.vipContactIcon)).setColorFilter(null);
                }

                if (!contact.isStartup) {
                    ((ImageView) profilePopUp.findViewById(R.id.startupContactIcon)).setColorFilter(ContextCompat.getColor(getContext(), R.color.gray), android.graphics.PorterDuff.Mode.SRC_IN);
                } else {
                    ((ImageView) profilePopUp.findViewById(R.id.startupContactIcon)).setColorFilter(null);
                }

                if (!contact.isInvestor) {
                    ((ImageView) profilePopUp.findViewById(R.id.investorContactIcon)).setColorFilter(ContextCompat.getColor(getContext(), R.color.gray), android.graphics.PorterDuff.Mode.SRC_IN);
                } else {
                    ((ImageView) profilePopUp.findViewById(R.id.investorContactIcon)).setColorFilter(null);
                }


                profilePopUp.findViewById(R.id.starFrame).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Realm realm = Realm.getDefaultInstance(); //+
                        realm.beginTransaction();
                        if (contact.isFavorite) {
                            contact.isFavorite = false;
                            profilePopUp.findViewById(R.id.starImg).setVisibility(View.GONE);

                            ContactsService.updateFaroviteContact(false, contact.getIdContact(), getContext());

                               /* Drawable d = DrawableCompat.wrap(getResources().getDrawable( R.drawable.icn_important));
                                DrawableCompat.setTint(d, Color.parseColor("#9e9e9e"));
                                ((ImageView)profilePopUp.findViewById(R.id.importantContactIcon)).setImageDrawable(d);*/
                            ((ImageView) profilePopUp.findViewById(R.id.favoriteContactIcon)).setColorFilter(ContextCompat.getColor(getContext(), R.color.gray), PorterDuff.Mode.SRC_IN);
                            Toast.makeText(getContext(), "Deleted from important", Toast.LENGTH_SHORT).show();


                        } else {

                            ContactsService.updateFaroviteContact(true, contact.getIdContact(), getContext());

                            contact.isFavorite = true;
                            contact.isImportant = false;
                            contact.isPause = false;
                            contact.isFinished = false;

                            contact.isCrown = false;
                            contact.isVip = false;
                            contact.isStartup = false;
                            contact.isInvestor = false;

                            ((ImageView) profilePopUp.findViewById(R.id.favoriteContactIcon)).setColorFilter(null);

                            ((ImageView) profilePopUp.findViewById(R.id.starImg)).setImageDrawable(getResources().getDrawable(R.drawable.star));
                            profilePopUp.findViewById(R.id.starImg).setVisibility(View.VISIBLE);

                            ((ImageView) profilePopUp.findViewById(R.id.importantContactIcon)).setColorFilter(ContextCompat.getColor(getContext(), R.color.gray), PorterDuff.Mode.SRC_IN);
                            ((ImageView) profilePopUp.findViewById(R.id.pauseContactIcon)).setColorFilter(ContextCompat.getColor(getContext(), R.color.gray), PorterDuff.Mode.SRC_IN);
                            ((ImageView) profilePopUp.findViewById(R.id.finishedContactIcon)).setColorFilter(ContextCompat.getColor(getContext(), R.color.gray), PorterDuff.Mode.SRC_IN);

                            ((ImageView) profilePopUp.findViewById(R.id.crownContactIcon)).setColorFilter(ContextCompat.getColor(getContext(), R.color.gray), PorterDuff.Mode.SRC_IN);
                            ((ImageView) profilePopUp.findViewById(R.id.vipContactIcon)).setColorFilter(ContextCompat.getColor(getContext(), R.color.gray), PorterDuff.Mode.SRC_IN);
                            ((ImageView) profilePopUp.findViewById(R.id.startupContactIcon)).setColorFilter(ContextCompat.getColor(getContext(), R.color.gray), PorterDuff.Mode.SRC_IN);
                            ((ImageView) profilePopUp.findViewById(R.id.investorContactIcon)).setColorFilter(ContextCompat.getColor(getContext(), R.color.gray), PorterDuff.Mode.SRC_IN);

                            profilePopUp.findViewById(R.id.popupStar).setVisibility(View.GONE);


                            Toast.makeText(getContext(), "Set as important", Toast.LENGTH_SHORT).show();
                        }
                        realm.commitTransaction();
                        realm.close();
                        ContactsFragment.UPD_FAV = true;
                    }
                });


                profilePopUp.findViewById(R.id.impFrame).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Realm realm = Realm.getDefaultInstance(); //+
                        realm.beginTransaction();
                        if (contact.isImportant) {
                            contact.isImportant = false;
                            profilePopUp.findViewById(R.id.starImg).setVisibility(View.GONE);

                               /* Drawable d = DrawableCompat.wrap(getResources().getDrawable( R.drawable.icn_important));
                                DrawableCompat.setTint(d, Color.parseColor("#9e9e9e"));
                                ((ImageView)profilePopUp.findViewById(R.id.importantContactIcon)).setImageDrawable(d);*/
                            ((ImageView) profilePopUp.findViewById(R.id.importantContactIcon)).setColorFilter(ContextCompat.getColor(getContext(), R.color.gray), PorterDuff.Mode.SRC_IN);
                            Toast.makeText(getContext(), "Deleted from important", Toast.LENGTH_SHORT).show();


                        } else {
                            if (contact.isFavorite) {
                                ContactsService.updateFaroviteContact(false, contact.getIdContact(), getContext());
                            }
                            contact.isImportant = true;
                            contact.isFavorite = false;
                            contact.isPause = false;
                            contact.isFinished = false;

                            contact.isCrown = false;
                            contact.isVip = false;
                            contact.isStartup = false;
                            contact.isInvestor = false;

                            ((ImageView) profilePopUp.findViewById(R.id.importantContactIcon)).setColorFilter(null);

                            ((ImageView) profilePopUp.findViewById(R.id.starImg)).setImageDrawable(getResources().getDrawable(R.drawable.checked_2));
                            profilePopUp.findViewById(R.id.starImg).setVisibility(View.VISIBLE);

                            ((ImageView) profilePopUp.findViewById(R.id.favoriteContactIcon)).setColorFilter(ContextCompat.getColor(getContext(), R.color.gray), PorterDuff.Mode.SRC_IN);
                            ((ImageView) profilePopUp.findViewById(R.id.pauseContactIcon)).setColorFilter(ContextCompat.getColor(getContext(), R.color.gray), PorterDuff.Mode.SRC_IN);
                            ((ImageView) profilePopUp.findViewById(R.id.finishedContactIcon)).setColorFilter(ContextCompat.getColor(getContext(), R.color.gray), PorterDuff.Mode.SRC_IN);

                            ((ImageView) profilePopUp.findViewById(R.id.crownContactIcon)).setColorFilter(ContextCompat.getColor(getContext(), R.color.gray), PorterDuff.Mode.SRC_IN);
                            ((ImageView) profilePopUp.findViewById(R.id.vipContactIcon)).setColorFilter(ContextCompat.getColor(getContext(), R.color.gray), PorterDuff.Mode.SRC_IN);
                            ((ImageView) profilePopUp.findViewById(R.id.startupContactIcon)).setColorFilter(ContextCompat.getColor(getContext(), R.color.gray), PorterDuff.Mode.SRC_IN);
                            ((ImageView) profilePopUp.findViewById(R.id.investorContactIcon)).setColorFilter(ContextCompat.getColor(getContext(), R.color.gray), PorterDuff.Mode.SRC_IN);

                            profilePopUp.findViewById(R.id.popupStar).setVisibility(View.GONE);

                            Toast.makeText(getContext(), "Set as important", Toast.LENGTH_SHORT).show();
                        }
                        realm.commitTransaction();
                        realm.close();
                        ContactsFragment.UPD_IMP = true;
                    }
                });

                profilePopUp.findViewById(R.id.stopFrame).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Realm realm = Realm.getDefaultInstance(); //+
                        realm.beginTransaction();
                        if (contact.isFinished) {
                            contact.isFinished = false;
                            profilePopUp.findViewById(R.id.starImg).setVisibility(View.GONE);
                            ((ImageView) profilePopUp.findViewById(R.id.finishedContactIcon)).setColorFilter(ContextCompat.getColor(getContext(), R.color.gray), android.graphics.PorterDuff.Mode.SRC_IN);
                            Toast.makeText(getContext(), "Back to communication", Toast.LENGTH_SHORT).show();

                        } else {
                            if (contact.isFavorite) {
                                ContactsService.updateFaroviteContact(false, contact.getIdContact(), getContext());
                            }
                            contact.isFinished = true;
                            contact.isFavorite = false;
                            contact.isPause = false;
                            contact.isImportant = false;

                            contact.isCrown = false;
                            contact.isVip = false;
                            contact.isStartup = false;
                            contact.isInvestor = false;

                            ((ImageView) profilePopUp.findViewById(R.id.finishedContactIcon)).setColorFilter(null);

                            ((ImageView) profilePopUp.findViewById(R.id.starImg)).setImageDrawable(getResources().getDrawable(R.drawable.finish_1));
                            profilePopUp.findViewById(R.id.starImg).setVisibility(View.VISIBLE);

                            ((ImageView) profilePopUp.findViewById(R.id.favoriteContactIcon)).setColorFilter(ContextCompat.getColor(getContext(), R.color.gray), PorterDuff.Mode.SRC_IN);
                            ((ImageView) profilePopUp.findViewById(R.id.pauseContactIcon)).setColorFilter(ContextCompat.getColor(getContext(), R.color.gray), PorterDuff.Mode.SRC_IN);
                            ((ImageView) profilePopUp.findViewById(R.id.importantContactIcon)).setColorFilter(ContextCompat.getColor(getContext(), R.color.gray), PorterDuff.Mode.SRC_IN);

                            ((ImageView) profilePopUp.findViewById(R.id.crownContactIcon)).setColorFilter(ContextCompat.getColor(getContext(), R.color.gray), PorterDuff.Mode.SRC_IN);
                            ((ImageView) profilePopUp.findViewById(R.id.vipContactIcon)).setColorFilter(ContextCompat.getColor(getContext(), R.color.gray), PorterDuff.Mode.SRC_IN);
                            ((ImageView) profilePopUp.findViewById(R.id.startupContactIcon)).setColorFilter(ContextCompat.getColor(getContext(), R.color.gray), PorterDuff.Mode.SRC_IN);
                            ((ImageView) profilePopUp.findViewById(R.id.investorContactIcon)).setColorFilter(ContextCompat.getColor(getContext(), R.color.gray), PorterDuff.Mode.SRC_IN);

                            profilePopUp.findViewById(R.id.popupStar).setVisibility(View.GONE);


                            Toast.makeText(getContext(), "Finished communication", Toast.LENGTH_SHORT).show();
                        }
                        realm.commitTransaction();
                        realm.close();
                        ContactsFragment.UPD_FIN = true;
                    }
                });

                profilePopUp.findViewById(R.id.pauseFrame).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Realm realm = Realm.getDefaultInstance(); //+
                        realm.beginTransaction();
                        if (contact.isPause) {
                            contact.isPause = false;
                            profilePopUp.findViewById(R.id.starImg).setVisibility(View.GONE);
                            ((ImageView) profilePopUp.findViewById(R.id.pauseContactIcon)).setColorFilter(ContextCompat.getColor(getContext(), R.color.gray), android.graphics.PorterDuff.Mode.SRC_IN);

                            Toast.makeText(getContext(), "Back to communication", Toast.LENGTH_SHORT).show();

                        } else {
                            if (contact.isFavorite) {
                                ContactsService.updateFaroviteContact(false, contact.getIdContact(), getContext());
                            }
                            contact.isPause = true;
                            contact.isFavorite = false;
                            contact.isFinished = false;
                            contact.isImportant = false;

                            contact.isCrown = false;
                            contact.isVip = false;
                            contact.isStartup = false;
                            contact.isInvestor = false;

                            ((ImageView) profilePopUp.findViewById(R.id.pauseContactIcon)).setColorFilter(null);

                            ((ImageView) profilePopUp.findViewById(R.id.starImg)).setImageDrawable(getResources().getDrawable(R.drawable.pause_1));
                            profilePopUp.findViewById(R.id.starImg).setVisibility(View.VISIBLE);

                            ((ImageView) profilePopUp.findViewById(R.id.favoriteContactIcon)).setColorFilter(ContextCompat.getColor(getContext(), R.color.gray), PorterDuff.Mode.SRC_IN);
                            ((ImageView) profilePopUp.findViewById(R.id.finishedContactIcon)).setColorFilter(ContextCompat.getColor(getContext(), R.color.gray), PorterDuff.Mode.SRC_IN);
                            ((ImageView) profilePopUp.findViewById(R.id.importantContactIcon)).setColorFilter(ContextCompat.getColor(getContext(), R.color.gray), PorterDuff.Mode.SRC_IN);

                            ((ImageView) profilePopUp.findViewById(R.id.crownContactIcon)).setColorFilter(ContextCompat.getColor(getContext(), R.color.gray), PorterDuff.Mode.SRC_IN);
                            ((ImageView) profilePopUp.findViewById(R.id.vipContactIcon)).setColorFilter(ContextCompat.getColor(getContext(), R.color.gray), PorterDuff.Mode.SRC_IN);
                            ((ImageView) profilePopUp.findViewById(R.id.startupContactIcon)).setColorFilter(ContextCompat.getColor(getContext(), R.color.gray), PorterDuff.Mode.SRC_IN);
                            ((ImageView) profilePopUp.findViewById(R.id.investorContactIcon)).setColorFilter(ContextCompat.getColor(getContext(), R.color.gray), PorterDuff.Mode.SRC_IN);


                            profilePopUp.findViewById(R.id.popupStar).setVisibility(View.GONE);


                            Toast.makeText(getContext(), "Paused communication", Toast.LENGTH_SHORT).show();
                        }
                        realm.commitTransaction();
                        realm.close();
                        ContactsFragment.UPD_PAUSE = true;
                    }
                });

                profilePopUp.findViewById(R.id.crownFrame).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Realm realm = Realm.getDefaultInstance(); //+
                        realm.beginTransaction();
                        if (contact.isCrown) {
                            contact.isCrown = false;
                            profilePopUp.findViewById(R.id.starImg).setVisibility(View.GONE);
                            ((ImageView) profilePopUp.findViewById(R.id.crownContactIcon)).setColorFilter(ContextCompat.getColor(getContext(), R.color.gray), android.graphics.PorterDuff.Mode.SRC_IN);

                            Toast.makeText(getContext(), "Deleted from Crown", Toast.LENGTH_SHORT).show();

                        } else {
                            if (contact.isFavorite) {
                                ContactsService.updateFaroviteContact(false, contact.getIdContact(), getContext());
                            }
                            contact.isCrown = true;
                            contact.isFavorite = false;
                            contact.isFinished = false;
                            contact.isImportant = false;

                            contact.isPause = false;
                            contact.isVip = false;
                            contact.isStartup = false;
                            contact.isInvestor = false;

                            ((ImageView) profilePopUp.findViewById(R.id.crownContactIcon)).setColorFilter(null);

                            ((ImageView) profilePopUp.findViewById(R.id.starImg)).setImageDrawable(getResources().getDrawable(R.drawable.crown));
                            profilePopUp.findViewById(R.id.starImg).setVisibility(View.VISIBLE);

                            ((ImageView) profilePopUp.findViewById(R.id.favoriteContactIcon)).setColorFilter(ContextCompat.getColor(getContext(), R.color.gray), PorterDuff.Mode.SRC_IN);
                            ((ImageView) profilePopUp.findViewById(R.id.finishedContactIcon)).setColorFilter(ContextCompat.getColor(getContext(), R.color.gray), PorterDuff.Mode.SRC_IN);
                            ((ImageView) profilePopUp.findViewById(R.id.importantContactIcon)).setColorFilter(ContextCompat.getColor(getContext(), R.color.gray), PorterDuff.Mode.SRC_IN);

                            ((ImageView) profilePopUp.findViewById(R.id.pauseContactIcon)).setColorFilter(ContextCompat.getColor(getContext(), R.color.gray), PorterDuff.Mode.SRC_IN);
                            ((ImageView) profilePopUp.findViewById(R.id.vipContactIcon)).setColorFilter(ContextCompat.getColor(getContext(), R.color.gray), PorterDuff.Mode.SRC_IN);
                            ((ImageView) profilePopUp.findViewById(R.id.startupContactIcon)).setColorFilter(ContextCompat.getColor(getContext(), R.color.gray), PorterDuff.Mode.SRC_IN);
                            ((ImageView) profilePopUp.findViewById(R.id.investorContactIcon)).setColorFilter(ContextCompat.getColor(getContext(), R.color.gray), PorterDuff.Mode.SRC_IN);


                            profilePopUp.findViewById(R.id.popupStar).setVisibility(View.GONE);


                            Toast.makeText(getContext(), "Set as Crown", Toast.LENGTH_SHORT).show();
                        }
                        realm.commitTransaction();
                        realm.close();
                        ContactsFragment.UPD_CROWN = true;
                    }
                });

                profilePopUp.findViewById(R.id.vipFrame).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Realm realm = Realm.getDefaultInstance(); //+
                        realm.beginTransaction();
                        if (contact.isVip) {
                            contact.isVip = false;
                            profilePopUp.findViewById(R.id.starImg).setVisibility(View.GONE);
                            ((ImageView) profilePopUp.findViewById(R.id.vipContactIcon)).setColorFilter(ContextCompat.getColor(getContext(), R.color.gray), android.graphics.PorterDuff.Mode.SRC_IN);

                            Toast.makeText(getContext(), "Deleted from Vip", Toast.LENGTH_SHORT).show();

                        } else {
                            if (contact.isFavorite) {
                                ContactsService.updateFaroviteContact(false, contact.getIdContact(), getContext());
                            }
                            contact.isVip = true;
                            contact.isFavorite = false;
                            contact.isFinished = false;
                            contact.isImportant = false;

                            contact.isCrown = false;
                            contact.isPause = false;
                            contact.isStartup = false;
                            contact.isInvestor = false;

                            ((ImageView) profilePopUp.findViewById(R.id.vipContactIcon)).setColorFilter(null);

                            ((ImageView) profilePopUp.findViewById(R.id.starImg)).setImageDrawable(getResources().getDrawable(R.drawable.vip_new));
                            profilePopUp.findViewById(R.id.starImg).setVisibility(View.VISIBLE);

                            ((ImageView) profilePopUp.findViewById(R.id.favoriteContactIcon)).setColorFilter(ContextCompat.getColor(getContext(), R.color.gray), PorterDuff.Mode.SRC_IN);
                            ((ImageView) profilePopUp.findViewById(R.id.finishedContactIcon)).setColorFilter(ContextCompat.getColor(getContext(), R.color.gray), PorterDuff.Mode.SRC_IN);
                            ((ImageView) profilePopUp.findViewById(R.id.importantContactIcon)).setColorFilter(ContextCompat.getColor(getContext(), R.color.gray), PorterDuff.Mode.SRC_IN);

                            ((ImageView) profilePopUp.findViewById(R.id.pauseContactIcon)).setColorFilter(ContextCompat.getColor(getContext(), R.color.gray), PorterDuff.Mode.SRC_IN);
                            ((ImageView) profilePopUp.findViewById(R.id.crownContactIcon)).setColorFilter(ContextCompat.getColor(getContext(), R.color.gray), PorterDuff.Mode.SRC_IN);
                            ((ImageView) profilePopUp.findViewById(R.id.startupContactIcon)).setColorFilter(ContextCompat.getColor(getContext(), R.color.gray), PorterDuff.Mode.SRC_IN);
                            ((ImageView) profilePopUp.findViewById(R.id.investorContactIcon)).setColorFilter(ContextCompat.getColor(getContext(), R.color.gray), PorterDuff.Mode.SRC_IN);


                            profilePopUp.findViewById(R.id.popupStar).setVisibility(View.GONE);


                            Toast.makeText(getContext(), "Set as Vip", Toast.LENGTH_SHORT).show();
                        }
                        realm.commitTransaction();
                        realm.close();
                        ContactsFragment.UPD_VIP = true;
                    }
                });

                profilePopUp.findViewById(R.id.startupFrame).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Realm realm = Realm.getDefaultInstance(); //+
                        realm.beginTransaction();
                        if (contact.isStartup) {
                            contact.isStartup = false;
                            profilePopUp.findViewById(R.id.starImg).setVisibility(View.GONE);
                            ((ImageView) profilePopUp.findViewById(R.id.startupContactIcon)).setColorFilter(ContextCompat.getColor(getContext(), R.color.gray), android.graphics.PorterDuff.Mode.SRC_IN);

                            Toast.makeText(getContext(), "Deleted from Startup", Toast.LENGTH_SHORT).show();

                        } else {
                            if (contact.isFavorite) {
                                ContactsService.updateFaroviteContact(false, contact.getIdContact(), getContext());
                            }
                            contact.isStartup = true;
                            contact.isFavorite = false;
                            contact.isFinished = false;
                            contact.isImportant = false;

                            contact.isCrown = false;
                            contact.isPause = false;
                            contact.isVip = false;
                            contact.isInvestor = false;

                            ((ImageView) profilePopUp.findViewById(R.id.startupContactIcon)).setColorFilter(null);

                            ((ImageView) profilePopUp.findViewById(R.id.starImg)).setImageDrawable(getResources().getDrawable(R.drawable.startup));
                            profilePopUp.findViewById(R.id.starImg).setVisibility(View.VISIBLE);

                            ((ImageView) profilePopUp.findViewById(R.id.favoriteContactIcon)).setColorFilter(ContextCompat.getColor(getContext(), R.color.gray), PorterDuff.Mode.SRC_IN);
                            ((ImageView) profilePopUp.findViewById(R.id.finishedContactIcon)).setColorFilter(ContextCompat.getColor(getContext(), R.color.gray), PorterDuff.Mode.SRC_IN);
                            ((ImageView) profilePopUp.findViewById(R.id.importantContactIcon)).setColorFilter(ContextCompat.getColor(getContext(), R.color.gray), PorterDuff.Mode.SRC_IN);

                            ((ImageView) profilePopUp.findViewById(R.id.pauseContactIcon)).setColorFilter(ContextCompat.getColor(getContext(), R.color.gray), PorterDuff.Mode.SRC_IN);
                            ((ImageView) profilePopUp.findViewById(R.id.crownContactIcon)).setColorFilter(ContextCompat.getColor(getContext(), R.color.gray), PorterDuff.Mode.SRC_IN);
                            ((ImageView) profilePopUp.findViewById(R.id.vipContactIcon)).setColorFilter(ContextCompat.getColor(getContext(), R.color.gray), PorterDuff.Mode.SRC_IN);
                            ((ImageView) profilePopUp.findViewById(R.id.investorContactIcon)).setColorFilter(ContextCompat.getColor(getContext(), R.color.gray), PorterDuff.Mode.SRC_IN);


                            profilePopUp.findViewById(R.id.popupStar).setVisibility(View.GONE);


                            Toast.makeText(getContext(), "Set as Startup", Toast.LENGTH_SHORT).show();
                        }
                        realm.commitTransaction();
                        realm.close();
                        ContactsFragment.UPD_STARTUP = true;
                    }
                });

                profilePopUp.findViewById(R.id.investorFrame).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Realm realm = Realm.getDefaultInstance(); //+
                        realm.beginTransaction();
                        if (contact.isInvestor) {
                            contact.isInvestor = false;
                            profilePopUp.findViewById(R.id.starImg).setVisibility(View.GONE);
                            ((ImageView) profilePopUp.findViewById(R.id.investorContactIcon)).setColorFilter(ContextCompat.getColor(getContext(), R.color.gray), android.graphics.PorterDuff.Mode.SRC_IN);

                            Toast.makeText(getContext(), "Deleted from Investor", Toast.LENGTH_SHORT).show();

                        } else {
                            if (contact.isFavorite) {
                                ContactsService.updateFaroviteContact(false, contact.getIdContact(), getContext());
                            }
                            contact.isInvestor = true;
                            contact.isFavorite = false;
                            contact.isFinished = false;
                            contact.isImportant = false;

                            contact.isCrown = false;
                            contact.isPause = false;
                            contact.isVip = false;
                            contact.isStartup = false;

                            ((ImageView) profilePopUp.findViewById(R.id.investorContactIcon)).setColorFilter(null);

                            ((ImageView) profilePopUp.findViewById(R.id.starImg)).setImageDrawable(getResources().getDrawable(R.drawable.investor_));
                            profilePopUp.findViewById(R.id.starImg).setVisibility(View.VISIBLE);

                            ((ImageView) profilePopUp.findViewById(R.id.favoriteContactIcon)).setColorFilter(ContextCompat.getColor(getContext(), R.color.gray), PorterDuff.Mode.SRC_IN);
                            ((ImageView) profilePopUp.findViewById(R.id.finishedContactIcon)).setColorFilter(ContextCompat.getColor(getContext(), R.color.gray), PorterDuff.Mode.SRC_IN);
                            ((ImageView) profilePopUp.findViewById(R.id.importantContactIcon)).setColorFilter(ContextCompat.getColor(getContext(), R.color.gray), PorterDuff.Mode.SRC_IN);

                            ((ImageView) profilePopUp.findViewById(R.id.pauseContactIcon)).setColorFilter(ContextCompat.getColor(getContext(), R.color.gray), PorterDuff.Mode.SRC_IN);
                            ((ImageView) profilePopUp.findViewById(R.id.crownContactIcon)).setColorFilter(ContextCompat.getColor(getContext(), R.color.gray), PorterDuff.Mode.SRC_IN);
                            ((ImageView) profilePopUp.findViewById(R.id.vipContactIcon)).setColorFilter(ContextCompat.getColor(getContext(), R.color.gray), PorterDuff.Mode.SRC_IN);
                            ((ImageView) profilePopUp.findViewById(R.id.startupContactIcon)).setColorFilter(ContextCompat.getColor(getContext(), R.color.gray), PorterDuff.Mode.SRC_IN);


                            profilePopUp.findViewById(R.id.popupStar).setVisibility(View.GONE);


                            Toast.makeText(getContext(), "Set as Investor", Toast.LENGTH_SHORT).show();
                        }
                        realm.commitTransaction();
                        realm.close();
                        ContactsFragment.UPD_INVESTOR = true;
                    }
                });

            }
        });


        //======================================

        /*ArrayList<String> list = contactsService.getContactPhones(contact.getIdContact());
        boolean check_phone = false;
        String phoneF = null;
        if (list != null && contact.getListOfContactInfo() != null) {
            for (ContactInfo p2 : contact.getListOfContactInfo()) {
                if (p2.type.equalsIgnoreCase("phone") || (p2.typeData != null && p2.typeData.equalsIgnoreCase("phone"))) {

                    String str = p2.value;
                    if (phoneF == null || phoneF.equals("+000000000000"))
                        phoneF = str;

                    str = str.replaceAll("[\\s\\-\\+\\(\\)]", "");
                    for (String p : list) {

                        String str2 = p;
                        str2 = str2.replaceAll("[\\s\\-\\+\\(\\)]", "");
                        if (str.equalsIgnoreCase(str2)) {
                            check_phone = true;
                            break;
                        }
                    }
                }
                if (check_phone) break;
            }
        }

        if (check_phone) {

            RealmConfiguration contextRealm = new RealmConfiguration.
                    Builder().
                    deleteRealmIfMigrationNeeded().
                    build();
            Realm realm = Realm.getInstance(contextRealm);


            Cursor accountCursor = contactsService.getCursorByType(ContactsService.TYPE_CURSOR.ACCOUNT_TYPE, contact.getIdContact());
            if (accountCursor != null && accountCursor.getCount() > 0) {
                while (accountCursor.moveToNext()) {
                    int accountTypeI = accountCursor.getColumnIndex(ContactsContract.RawContacts.ACCOUNT_TYPE);
                    if (accountTypeI != -1) {
                        String accountType = accountCursor.getString(accountTypeI);
                        if (accountType != null && accountType.contains("telegram") && (contact.getSocialModel() == null || (contact.getSocialModel() != null && (contact.getSocialModel().getTelegramLink() == null || contact.getSocialModel().getTelegramLink().isEmpty())))) {
                            realm.beginTransaction();
                            contact.hasTelegram = true;
                            contact.addAccountType(accountType);
                            if (contact.getSocialModel() == null) {
                                SocialModel sc = realm.createObject(SocialModel.class);
                                sc.setTelegramLink(phoneF);
                                contact.setSocialModel(sc);
                            } else contact.getSocialModel().setTelegramLink(phoneF);

                            realm.commitTransaction();
                        }

                        if (accountType != null) {

                            *//*try {
                                if (accountType.contains("viber") && (contact.getSocialModel() == null || (contact.getSocialModel() != null && (contact.getSocialModel().getViberLink() == null || contact.getSocialModel().getViberLink().isEmpty()))) && !phoneF.equals("+000000000000")) {
                                    realm.beginTransaction();
                                    contact.hasViber = true;
                                    contact.addAccountType(accountType);
                                    if (contact.getSocialModel() == null) {
                                        SocialModel sc = realm.createObject(SocialModel.class);
                                        sc.setViberLink(phoneF);
                                        contact.setSocialModel(sc);
                                    } else contact.getSocialModel().setViberLink(phoneF);

                                    realm.commitTransaction();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }*//*

                            try {
                                if (accountType.contains("whatsapp") && (contact.getSocialModel() == null || (contact.getSocialModel() != null && (contact.getSocialModel().getWhatsappLink() == null || contact.getSocialModel().getWhatsappLink().isEmpty()))) && !phoneF.equals("+000000000000")) {
                                    realm.beginTransaction();
                                    contact.hasWhatsapp = true;
                                    contact.addAccountType(accountType);
                                    if (contact.getSocialModel() == null) {
                                        SocialModel sc = realm.createObject(SocialModel.class);
                                        sc.setWhatsappLink(phoneF);
                                        contact.setSocialModel(sc);
                                    } else contact.getSocialModel().setWhatsappLink(phoneF);

                                    realm.commitTransaction();
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }
                }
                accountCursor.close();
            }

            realm.close();
        }
*/

        //======================================


        initIconColor(contact, profilePopUp);
        fillProfileData(contact, profilePopUp);
        profilePopUp.setVisibility(View.VISIBLE);
        profilePopUp.setFocusable(true);
        profilePopUp.setClickable(true);
        profilePopUp.requestFocus();


        //================new

        final long idThread = contact.getId();
        new Thread(new Runnable() {
            @Override
            public void run() {

                Realm realm2 = Realm.getDefaultInstance();

                Contact selectedContact = ContactCacheService.getContactByIdSynk(idThread, realm2);
                ArrayList<String> conatctPhones = new ArrayList<>();

                if (selectedContact.listOfContactInfo != null && !selectedContact.listOfContactInfo.isEmpty()) {
                    for (ContactInfo info : selectedContact.listOfContactInfo) {
                        if ((info.type.equalsIgnoreCase("phone") || (info.typeData != null && info.typeData.equalsIgnoreCase("phone"))) && !info.value.equals("+000000000000")) {
                            conatctPhones.add(info.value);
                        }
                    }
                }

                SocialModel scContact = null;
                if (selectedContact.getSocialModel() != null)
                    scContact = selectedContact.getSocialModel();

                if (!contactsService.getContactById(selectedContact.getIdContact(), selectedContact.getId(), conatctPhones, scContact)) {


                    long newId = contactsService.saveContact(selectedContact);


                    Realm realm = Realm.getDefaultInstance(); //+

                    realm.beginTransaction();
                    selectedContact.setIdContact(String.valueOf(newId));
                    realm.commitTransaction();

                    realm.close();


                }

                realm2.close();

                EventBus.getDefault().post(new UpdateMessengersInCompany(contact));

            }
        }).start();

        //===============


        if (openedViews != null) openedViews.add(profilePopUp);


        profilePopUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                if (popupUserHashtags != null)
                    if (popupUserHashtags.getVisibility() == View.VISIBLE) {
                        ViewGroup.LayoutParams mostParams = containerMost.getLayoutParams();
                        mostParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
                        containerMost.setLayoutParams(mostParams);
                        ViewGroup.LayoutParams assistantParams = containerAssistant.getLayoutParams();
                        assistantParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
                        containerAssistant.setLayoutParams(assistantParams);
                        popupUserHashtags.findViewById(R.id.arrowSuggest).setScaleY(1f);
                        popupUserHashtags.setVisibility(View.GONE);
                    }
                /*if (companySelectPopup != null) {
                    if (companySelectPopup.getVisibility() == View.VISIBLE) {
                        companySelectPopup.setVisibility(View.GONE);
                    }
                }*/

                if (profilePopUp != null) {
                    profilePopUp.findViewById(R.id.popupStar).setVisibility(View.GONE);
                }

                if (contact.getListOfHashtags().size() == 0) {
                    showProfilePopUp(contact);
                }

                if (companySelectPopup.getVisibility() == View.VISIBLE) {
                    companySelectPopup.setVisibility(View.GONE);
                }

                final String idCo = contact.getIdContact();
                final long idC = contact.getId();

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

                                    //
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (profilePopUp.findViewById(R.id.starImg).getVisibility() == View.VISIBLE) {
                                                profilePopUp.findViewById(R.id.starImg).setVisibility(View.GONE);

                                                if (contact.isFavorite) {

                                                    ContactCacheService.updateFavoriteContact(false, idC);
                                                    ContactsService.updateFaroviteContact(false, idCo, getContext());
                                                    ContactsFragment.UPD_FAV = true;
                                                    Toast.makeText(getContext(), "Deleted from Favorites", Toast.LENGTH_SHORT).show();
                                                } else if (contact.isImportant) {
                                                    ContactCacheService.updateImportantContact(false, idC);
                                                    ContactsFragment.UPD_IMP = true;
                                                    Toast.makeText(getContext(), "Deleted from Important", Toast.LENGTH_SHORT).show();
                                                } else if (contact.isFinished) {
                                                    ContactCacheService.updateFinishedContact(false, idC);
                                                    ContactsFragment.UPD_FIN = true;
                                                    Toast.makeText(getContext(), "Back to communication", Toast.LENGTH_SHORT).show();
                                                } else if (contact.isPause) {
                                                    ContactCacheService.updatePauseContact(false, idC);
                                                    ContactsFragment.UPD_PAUSE = true;
                                                    Toast.makeText(getContext(), "Back to communication", Toast.LENGTH_SHORT).show();
                                                } else if (contact.isCrown) {
                                                    ContactCacheService.updateCrownContact(false, idC);
                                                    ContactsFragment.UPD_CROWN = true;
                                                    Toast.makeText(getContext(), "Deleted from Crown", Toast.LENGTH_SHORT).show();
                                                } else if (contact.isVip) {
                                                    ContactCacheService.updateVipContact(false, idC);
                                                    ContactsFragment.UPD_VIP = true;
                                                    Toast.makeText(getContext(), "Deleted from Vip", Toast.LENGTH_SHORT).show();
                                                } else if (contact.isStartup) {
                                                    ContactCacheService.updateStartupContact(false, idC);
                                                    ContactsFragment.UPD_STARTUP = true;
                                                    Toast.makeText(getContext(), "Deleted from Startup", Toast.LENGTH_SHORT).show();
                                                } else if (contact.isInvestor) {
                                                    ContactCacheService.updateInvestorContact(false, idC);
                                                    ContactsFragment.UPD_INVESTOR = true;
                                                    Toast.makeText(getContext(), "Deleted from Investor", Toast.LENGTH_SHORT).show();
                                                }

                                            } else {
                                                ((ImageView) profilePopUp.findViewById(R.id.starImg)).setImageDrawable(getResources().getDrawable(R.drawable.star));
                                                profilePopUp.findViewById(R.id.starImg).setVisibility(View.VISIBLE);
                                                ContactCacheService.updateFavoriteContact(true, idC);
                                                ContactsService.updateFaroviteContact(true, idCo, getContext());
                                                ContactsFragment.UPD_FAV = true;
                                                Toast.makeText(getContext(), "Successfully added to Favorites", Toast.LENGTH_SHORT).show();

                                                profilePopUp.findViewById(R.id.starImg).post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        profilePopUp.findViewById(R.id.starImg).callOnClick();
                                                    }
                                                });
                                            }
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
        });


        profilePopUp.findViewById(R.id.arrowShowHashtags).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (socialPopup != null) {
                    if (socialPopup.getVisibility() == View.VISIBLE)
                        return;
                    showPopupUserHashtags(contact);
                } else
                    showPopupUserHashtags(contact);
            }
        });

        profilePopUp.findViewById(R.id.company).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //   ((TextView) profilePopUp.findViewById(R.id.company)).setVisibility(View.VISIBLE);
                //   ((TextView) profilePopUp.findViewById(R.id.company)).setText("");
                //   ((TextView) profilePopUp.findViewById(R.id.company)).setHint("Position");


                if (((TextView) profilePopUp.findViewById(R.id.company)).getText().toString().length() == 0) {

                    showPositionAddPopup(contact, null, false);
                }

            }
        });

        /*profilePopUp.findViewById(R.id.user_edit_block).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });*/

        profilePopUp.findViewById(R.id.socialsArrowDown).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSocialPopup(contact);
            }
        });

    }

    private void showPopupUserHashtags(Contact contact) {
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
            text.setOnClickListener(v -> contactAdapter.searchByHashTagValue(hashTag.getHashTagValue()));
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

                shareIntent.putExtra(Intent.EXTRA_TEXT, "Join extime community - i add to your card following tags: " + friends);
                startActivity(Intent.createChooser(shareIntent, "Поделиться контактом"));
            }
        });

        customTagsAdapter = new CustomTagsAdapter(tags, contact, this);
        RecyclerView customTagsContainer = (RecyclerView) popupUserHashtags.findViewById(R.id.customTagsContainer);
        RecyclerView.LayoutManager customLR = new LinearLayoutManager(mainView.getContext());
        customTagsContainer.setLayoutManager(customLR);
        customTagsContainer.setItemAnimator(new DefaultItemAnimator());
        customTagsContainer.setAdapter(customTagsAdapter);
        penStartEditMode(contact);
        ((ImageView) popupUserHashtags.findViewById(R.id.penEdit)).setImageDrawable(getResources().getDrawable(R.drawable.icn_popup_pencil));
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
                    text.setOnClickListener(v1 -> contactAdapter.searchByHashTagValue(hashTag.getHashTagValue()));
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


                penStartEditMode(contact);
            }
        });
    }


    private void showPositionAddPopup(Contact contact, @Nullable String company, Boolean update) {
        if (socialPopup != null)
            if (socialPopup.getVisibility() == View.VISIBLE)
                socialPopup.setVisibility(View.GONE);
        if (popupUserHashtags != null)
            if (popupUserHashtags.getVisibility() == View.VISIBLE)
                popupUserHashtags.setVisibility(View.GONE);

        ((ImageView) companySelectPopup.findViewById(R.id.updateCompany)).setImageDrawable(getResources().getDrawable(R.drawable.icn_saving_pig));
        companySelectPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        ((EditText) companySelectPopup.findViewById(R.id.companyName)).setText("");

        if (company != null)
            ((EditText) companySelectPopup.findViewById(R.id.companyName)).setText(company);
        companySelectPopup.findViewById(R.id.companyName).setFocusable(true);
        companySelectPopup.findViewById(R.id.companyName).setFocusableInTouchMode(true);
        companySelectPopup.findViewById(R.id.companyName).setClickable(true);

        if (update) {
            companySelectPopup.findViewById(R.id.companyName).setFocusable(false);
            companySelectPopup.findViewById(R.id.companyName).setFocusableInTouchMode(false);
            companySelectPopup.findViewById(R.id.companyName).setClickable(false);
            ((EditText) companySelectPopup.findViewById(R.id.companyName)).setText(company);
            ((ImageView) companySelectPopup.findViewById(R.id.updateCompany)).setImageDrawable(getResources().getDrawable(R.drawable.icn_popup_pencil));
            companySelectPopup.findViewById(R.id.updateCompany).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPositionAddPopup(contact, company, false);
                }
            });
        } else {
            companySelectPopup.findViewById(R.id.updateCompany).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String companyName = ((EditText) companySelectPopup.findViewById(R.id.companyName)).getText().toString();


                    Realm realm = Realm.getDefaultInstance(); //-
                    realm.beginTransaction();
                    contact.setCompanyPossition(companyName.trim());
                    realm.commitTransaction();
                    ContactCacheService.updateContact(contact, mainView.getContext());


                    String mainCompany = contact.getCompany();
                    //String oldPosition = contact.getCompanyPossition();
                    //String ContactId = contactsService.getIdContactByName(contact.getName());
                    //   contactsService.addCompanyPossitionToContact(contact.getIdContact(), contact.getCompanyPossition());


                    /*if(mainCompany != null && mainCompany != "")
                        contactsService.addComp(contact.getIdContact(),contact.getCompanyPossition(), mainCompany );
                    else
                        contactsService.insertPosition(contact.getIdContact(), contact.getCompanyPossition());*/


                    if (contact.listOfContacts == null || contact.listOfContacts.isEmpty()) {

                        contactsService.deleteCompany_Possition(contact.getIdContact());
                        if (contact.getCompany() != null && !contact.getCompany().isEmpty())
                            contactsService.insertCompany(contact.getIdContact(), contact.getCompany(), contact.getName());

                        if (contact.getCompanyPossition() != null && !contact.getCompanyPossition().isEmpty() && (contact.getCompany() != null && !contact.getCompany().isEmpty()))
                            contactsService.addComp(contact.getIdContact(), contact.getCompanyPossition(), contact.getCompany());

                        if (contact.getCompanyPossition() != null && !contact.getCompanyPossition().isEmpty() && (contact.getCompany() == null || contact.getCompany().isEmpty()))
                            contactsService.insertPosition(contact.getIdContact(), contact.getCompanyPossition(), contact.getName());


                    }


                    MainActivity.listToManyUpdateFile.clear();
                    ArrayList<String> listEdit = new ArrayList<>();
                    ArrayList<Boolean> listEditBool = new ArrayList<>();
                    listEdit.add(contact.getName());
                    listEditBool.add(true);

                    MainActivity.listToManyUpdateFile.add("EDIT");
                    MainActivity.listToManyUpdateFile.add(listEdit);
                    MainActivity.listToManyUpdateFile.add(listEditBool);

                    //EventBus.getDefault().post(new UpdateFile());

                    //EventBus.getDefault().post(new UpdateFile());
                    if (companyName.length() == 0) {
                        showPositionAddPopup(contact, "", true);
                        ((TextView) profilePopUp.findViewById(R.id.company)).setText("");
                    } else {
                        showPositionAddPopup(contact, companyName, true);
                        ((TextView) profilePopUp.findViewById(R.id.company)).setText(companyName);
                    }
                    //   CompanyAdapter companyAdapter = new CompanyAdapter();
                    Contact comp = ContactCacheService.getCompany(selectedContact.getName());
                    realm.beginTransaction();
                    selectedContact.listOfContacts = comp.listOfContacts;
                    realm.commitTransaction();
                    realm.close();
                    companyAdapter.setListContacts(selectedContact.listOfContacts);
                    //  companyAdapter.notifyDataSetChanged();
                    //              recyclerView.setAdapter();

                }
            });
        }

        companySelectPopup.setVisibility(View.VISIBLE);
        ArrayList<String> listOfPositions = ContactCacheService.getPossitionContacts();

        SelectPositionAdapter selectPositionAdapter = new SelectPositionAdapter(listOfPositions, ((EditText) companySelectPopup.findViewById(R.id.companyName)));
        RecyclerView containerCompanies = (RecyclerView) companySelectPopup.findViewById(R.id.companiesContainer);
        RecyclerView.LayoutManager mostLayoutManager = new GridLayoutManager(mainView.getContext(), 2);
        containerCompanies.setLayoutManager(mostLayoutManager);
        //  containerMost.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
        containerCompanies.setItemAnimator(new DefaultItemAnimator());
        containerCompanies.setAdapter(selectPositionAdapter);

        openedViews.add(companySelectPopup);

    }


    public int getQuantityOpenedViews() {
        return openedViews.size();
    }

    private String getPhoneNumberInfo(String number) {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

        try {
            Phonenumber.PhoneNumber swissNumberProto = phoneUtil.parse(number, "");
            PhoneNumberToCarrierMapper carrierMapper = PhoneNumberToCarrierMapper.getInstance();

            String region = "";
            region = phoneUtil.getRegionCodeForCountryCode(swissNumberProto.getCountryCode());

            if (region.length() > 0) {
                region = " " + region;
            }

            String carrier = "";
            carrier = carrierMapper.getNameForNumber(swissNumberProto, Locale.ENGLISH);

            if (carrier.length() > 0) {
                carrier = " " + carrier;
            }
            String result = number + region + carrier;
            return result;
        } catch (NumberParseException e) {
            return number;
        }
    }

    private Time getRandomDate() {
        final Random random = new Random();
        final int millisInDay = 24 * 60 * 60 * 1000;
        return new Time((long) random.nextInt(millisInDay));
    }


    public void showFastEditPopup(Contact contact) {
        closeOtherPopup();
        fastEditPopup = (FrameLayout) getActivity().findViewById(R.id.fastEditPopup);
        ((EditText) fastEditPopup.findViewById(R.id.nameContact)).setText(contact.getName());

        fastEditPopup.findViewById(R.id.hashtagsList).setVisibility(View.GONE);

        ((EditText) fastEditPopup.findViewById(R.id.hashtagsList)).setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);

        if (contact.getListOfHashtags().size() >= 0) {
            String hashTagsStr = "";
            for (HashTag hashTag : contact.getListOfHashtags()) {
                hashTagsStr += hashTag.getHashTagValue() + " ";
            }
            fastEditPopup.findViewById(R.id.hashtagsList).setVisibility(View.VISIBLE);
            ((EditText) fastEditPopup.findViewById(R.id.hashtagsList)).setText(hashTagsStr.trim());
        }

        ((EditText) fastEditPopup.findViewById(R.id.hashtagsList)).addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.subSequence(start, start + count).toString().compareTo(" ") == 0) {
                    ((EditText) fastEditPopup.findViewById(R.id.hashtagsList)).setText(s.toString() + "#");
                    ((EditText) fastEditPopup.findViewById(R.id.hashtagsList)).setSelection(((EditText) fastEditPopup.findViewById(R.id.hashtagsList)).getText().length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        fastEditPopup.findViewById(R.id.companyText).setVisibility(View.GONE);
        fastEditPopup.findViewById(R.id.companyPossitionText).setVisibility(View.GONE);

        if (contact.getCompany() != null) {
            ((EditText) fastEditPopup.findViewById(R.id.companyText)).setText(contact.getCompany());
            fastEditPopup.findViewById(R.id.companyText).setVisibility(View.VISIBLE);
        }

        if (contact.getCompanyPossition() != null) {
            ((EditText) fastEditPopup.findViewById(R.id.companyPossitionText)).setText(contact.getCompanyPossition());
            fastEditPopup.findViewById(R.id.companyPossitionText).setVisibility(View.VISIBLE);
        } else {
            ((EditText) fastEditPopup.findViewById(R.id.companyPossitionText)).setText("");
            ((EditText) fastEditPopup.findViewById(R.id.companyPossitionText)).setHint("Position");
            fastEditPopup.findViewById(R.id.companyPossitionText).setVisibility(View.VISIBLE);
        }


        initIconColor(contact, fastEditPopup);


        try {
            fastEditPopup.findViewById(R.id.profilePopupInitials).setVisibility(View.GONE);
            ((ImageView) fastEditPopup.findViewById(R.id.profilePopupAvatar)).setImageURI(Uri.parse(contact.getPhotoURL()));
            if (((BitmapDrawable) ((ImageView) fastEditPopup.findViewById(R.id.profilePopupAvatar)).getDrawable()).getBitmap() == null) {
                GradientDrawable circle = (GradientDrawable) ContextCompat.getDrawable(mainView.getContext(), R.drawable.blue_circle).mutate();
                circle.setColor(contact.color);
                fastEditPopup.findViewById(R.id.profilePopupAvatar).setBackground(circle);
                ((ImageView) fastEditPopup.findViewById(R.id.profilePopupAvatar)).setImageDrawable(null);

                String initials = "";
                String names[] = contact.getName().split("\\s+");

                for (String namePart : names)
                    initials += namePart.charAt(0);

                fastEditPopup.findViewById(R.id.profilePopupInitials).setVisibility(View.VISIBLE);
                ((TextView) fastEditPopup.findViewById(R.id.profilePopupInitials)).setText(initials);
            }
        } catch (Exception e) {
            GradientDrawable circle = (GradientDrawable) ContextCompat.getDrawable(mainView.getContext(), R.drawable.blue_circle).mutate();
            circle.setColor(contact.color);
            fastEditPopup.findViewById(R.id.profilePopupAvatar).setBackground(circle);
            ((ImageView) fastEditPopup.findViewById(R.id.profilePopupAvatar)).setImageDrawable(null);

            String initials = "";
            String names[] = contact.getName().split("\\s+");

            for (String namePart : names)
                initials += namePart.charAt(0);

            fastEditPopup.findViewById(R.id.profilePopupInitials).setVisibility(View.VISIBLE);
            ((TextView) fastEditPopup.findViewById(R.id.profilePopupInitials)).setText(initials);
        }


        fastEditPopup.findViewById(R.id.updateContacts).setOnClickListener(v -> {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    getActivity());
            alertDialogBuilder.setTitle("Do you want to edit a contact?");
            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialog, id) -> {


                        Realm realm = Realm.getDefaultInstance(); //-

                        if (contact.getListOfHashtags() != null)
                            for (int i = 0; i < contact.getListOfHashtags().size(); i++) {
                                contactsService.deleteNoteContact(contact.getIdContact(), contact.getListOfHashtags().get(i).getHashTagValue());
                            }


                        realm.beginTransaction();
                        if (((EditText) fastEditPopup.findViewById(R.id.hashtagsList)).getText().length() > 1) {
                            RealmList<HashTag> listOfHashTag = new RealmList<HashTag>();
                            ArrayList<String> hashtags = new ArrayList<>(Arrays.asList(((EditText) fastEditPopup.findViewById(R.id.hashtagsList)).getText().toString().split(" ")));
                            for (String hashTag : hashtags) {
                                HashTag hashTag1 = realm.createObject(HashTag.class);
                                hashTag1.setHashTagValue(hashTag.toLowerCase().trim());

                                if (!listOfHashTag.contains(hashTag1)) {
                                    listOfHashTag.add(hashTag1);
                                    contactsService.addNoteToContact(contact.getIdContact(), hashTag1.getHashTagValue(), contact.getName());
                                }

                            }
                            contact.setListOfHashtags(listOfHashTag);
                        } else {
                            contact.setListOfHashtags(new RealmList<>());
                        }
                        realm.commitTransaction();

                        boolean checkRemoveCompany = false;
                        String newName = ((EditText) fastEditPopup.findViewById(R.id.nameContact)).getText().toString();
                        String newCompany = ((EditText) fastEditPopup.findViewById(R.id.companyText)).getText().toString();
                        String newPosition = ((EditText) fastEditPopup.findViewById(R.id.companyPossitionText)).getText().toString();


                        String oldName = contact.getName();
                        if (!contact.getName().equals(newName)) {
                            Contact contact1 = ContactCacheService.getContactById(contact.getId());
                            realm.beginTransaction();
                            contact1.setName(newName.trim());
                            contact.setName(newName.trim());
                            contactsService.updateName(contact.getIdContact(), oldName, contact.getName());
                            realm.commitTransaction();
                        }


                        //=====================UPDATE COMPANY

                        String mainCompany = contact.getCompany();

                        if (!contact.getCompany().equals(newCompany)) {
                            Contact contact1 = ContactCacheService.getContactById(contact.getId());
                            Contact company = ContactCacheService.getCompany(contact.getCompany());
                            String oldComppp = contact.getCompany();


                            if (company != null) {
                                //    contactsService.updateCompany(IdContact, contact.getCompany(), company.getName());
                                if (company.listOfContacts.size() == 1) {
                                    //removeContactById(oldCompany);
                                    //contactAdapter.removeContactById(oldCompany);
                                    //======ADD UPDATE FILE
                                    ArrayList<String> listToEdit = new ArrayList<>();
                                    ArrayList<Boolean> listToEditCheck = new ArrayList<>();
                                    listToEdit.add(company.getName());
                                    listToEditCheck.add(false);
                                    MainActivity.listToManyUpdateFile.add("Delete");
                                    MainActivity.listToManyUpdateFile.add(listToEdit);
                                    MainActivity.listToManyUpdateFile.add(listToEditCheck);
                                    adapterC.removeContactById(company);

                                    //===========
                                } else {
                                    ArrayList<String> listToEdit = new ArrayList<>();
                                    ArrayList<Boolean> listToEditCheck = new ArrayList<>();
                                    listToEdit.add(company.getName());
                                    listToEditCheck.add(false);
                                    MainActivity.listToManyUpdateFile.add("EDIT");
                                    MainActivity.listToManyUpdateFile.add(listToEdit);
                                    MainActivity.listToManyUpdateFile.add(listToEditCheck);
                                }
                                //String oldC = company.getName();
                                checkRemoveCompany = ContactCacheService.removeContactFromCompany(company, contact1);
                            }

                            if (newCompany.trim().length() != 0) {
                                realm.beginTransaction();
                                contact1.setCompany(newCompany.trim());
                                contact.setCompany(newCompany.trim());
                                realm.commitTransaction();
                                Contact newComp = ContactCacheService.getCompany(newCompany);
                                if (newComp == null) {
                                    int nameHash = newCompany.hashCode();

                                    Date date = new Date();
                                    Calendar cal = Calendar.getInstance();
                                    cal.setTime(date);
                                    Time time = getRandomDate();
                                    time.setHours(cal.get(Calendar.HOUR_OF_DAY));
                                    time.setMinutes(cal.get(Calendar.MINUTE));
                                    time.setSeconds(cal.get(Calendar.SECOND));


                                    Contact companyContact = new Contact(date);
                                    companyContact.setName(newCompany.trim());
                                    companyContact.time = time.toString();
                                    companyContact.color = Color.rgb(Math.abs(nameHash * 28439) % 255, Math.abs(nameHash * 211239) % 255, Math.abs(nameHash * 42368) % 255);
                                    companyContact.listOfContacts = new RealmList<Contact>();
                                    companyContact.listOfContacts.add(contact);
                                    ContactCacheService.updateCompany(companyContact);

                                    ArrayList<String> listToEdit = new ArrayList<>();
                                    ArrayList<Boolean> listToEditCheck = new ArrayList<>();
                                    listToEdit.add(companyContact.getName());
                                    listToEditCheck.add(false);
                                    MainActivity.listToManyUpdateFile.add("ADD");
                                    MainActivity.listToManyUpdateFile.add(listToEdit);
                                    MainActivity.listToManyUpdateFile.add(listToEditCheck);

                                } else {
                                    realm.beginTransaction();
                                    newComp.listOfContacts.add(contact);
                                    realm.commitTransaction();
                                    ContactCacheService.updateCompany(newComp);

                                    ArrayList<String> listToEdit = new ArrayList<>();
                                    ArrayList<Boolean> listToEditCheck = new ArrayList<>();
                                    listToEdit.add(newComp.getName());
                                    listToEditCheck.add(false);
                                    MainActivity.listToManyUpdateFile.add("EDIT");
                                    MainActivity.listToManyUpdateFile.add(listToEdit);
                                    MainActivity.listToManyUpdateFile.add(listToEditCheck);

                                }

                                  /* if(company != null)
                                       contactsService.addCompanybyCompany(contact.getIdContact(), oldComppp, newCompany);
                                   else if(company == null && contact1.getCompanyPossition() != null)
                                       contactsService.addCompanybyPosition(contact.getIdContact(), contact1.getCompanyPossition(), newCompany);
                                   else
                                       contactsService.addCompanyToContact(contact.getIdContact(), newCompany);*/
                                mainCompany = newCompany;

                            } else {
                                realm.beginTransaction();
                                contact1.setCompany(null);
                                contact.setCompany(null);
                                realm.commitTransaction();

                                   /*if(company != null)
                                       contactsService.addCompanybyCompany(contact.getIdContact(), oldComppp, null);
                                   else if(company == null && contact1.getCompanyPossition() != null)
                                       contactsService.addCompanybyPosition(contact.getIdContact(), contact1.getCompanyPossition(), null);
                                   else
                                       contactsService.addCompanyToContact(contact.getIdContact(), null);*/
                                mainCompany = null;
                            }


                        }

                        //==================POSITION
                        if ((contact.getCompanyPossition() == null && newPosition.length() != 0) || (contact.getCompanyPossition() != null && !contact.getCompanyPossition().equals(newPosition))) {
                            Contact contact1 = ContactCacheService.getContactById(contact.getId());
                            String oldPosition = contact1.getCompanyPossition();
                            realm.beginTransaction();
                            if (newPosition.length() == 0) {
                                contact1.setCompanyPossition(null);
                                contact.setCompanyPossition(null);

                               /* if(mainCompany != null && mainCompany != "")
                                    contactsService.addComp(contact.getIdContact(),null, mainCompany );
                                else if((mainCompany == null || mainCompany == "") && oldPosition != null)
                                    contactsService.addPostionByPosition(contact.getIdContact(), oldPosition, null);
                                else
                                    contactsService.insertPosition(contact.getIdContact(), null);*/

                            } else {
                                contact1.setCompanyPossition(newPosition.trim());
                                contact.setCompanyPossition(newPosition.trim());

                                /*if(mainCompany != null && mainCompany != "")
                                    contactsService.addComp(contact.getIdContact(),newPosition, mainCompany );
                                else if((mainCompany == null || mainCompany == "") && oldPosition != null)
                                    contactsService.addPostionByPosition(contact.getIdContact(), oldPosition, newPosition);
                                else
                                    contactsService.insertPosition(contact.getIdContact(), newPosition);*/
                            }
                            realm.commitTransaction();
                        }
                        //===========================

                        realm.close();


                        if (contact.listOfContacts == null || contact.listOfContacts.isEmpty()) {

                            contactsService.deleteCompany_Possition(contact.getIdContact());
                            if (contact.getCompany() != null && !contact.getCompany().isEmpty())
                                contactsService.insertCompany(contact.getIdContact(), contact.getCompany(), contact.getName());

                            if (contact.getCompanyPossition() != null && !contact.getCompanyPossition().isEmpty() && (contact.getCompany() != null && !contact.getCompany().isEmpty()))
                                contactsService.addComp(contact.getIdContact(), contact.getCompanyPossition(), contact.getCompany());

                            if (contact.getCompanyPossition() != null && !contact.getCompanyPossition().isEmpty() && (contact.getCompany() == null || contact.getCompany().isEmpty()))
                                contactsService.insertPosition(contact.getIdContact(), contact.getCompanyPossition(), contact.getName());


                        }


                        ContactsFragment.UPD_LIST = true;
                        //ContactAdapter.savedContacts = ContactCacheService.getAllContacts(null);
                        //EventBus.getDefault().post(new UpdateListSavedContacts());
                        ((Postman) activityApp).updateSavedList();


                        if (checkRemoveCompany) {
                            getActivity().onBackPressed();
                        }

                        if (!checkRemoveCompany) {

                            ((TextView) getActivity().findViewById(R.id.companyNumb)).setText(String.valueOf(selectedContact.listOfContacts.size()));

                            companyAdapter.notifyDataSetChanged();
                        }
                        //==============UPDATE FILE
                        MainActivity.listToManyUpdateFile.add("EDIT OLD");
                        MainActivity.oldNameToUpdate = oldName;
                        ArrayList<String> listToEdit2 = new ArrayList<>();
                        ArrayList<Boolean> listToEditCheck2 = new ArrayList<>();
                        listToEdit2.add(contact.getName());
                        listToEditCheck2.add(true);
                        MainActivity.listToManyUpdateFile.add(listToEdit2);
                        MainActivity.listToManyUpdateFile.add(listToEditCheck2);

                        //EventBus.getDefault().post(new UpdateFile());
                        //==================

                        companyAdapter.notifyDataSetChanged();

                        // contactsService.fastUpdateContact(contact);

                        closeOtherPopup();
                    })
                    .setNegativeButton("No", (dialog, id) -> dialog.cancel());

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        });
        fastEditPopup.findViewById(R.id.cancelFastEdit).setOnClickListener(v -> {
                    closeOtherPopup();
                    showProfilePopUp(contact);
                }
        );

        fastEditPopup.setVisibility(View.VISIBLE);
        if (openedViews != null) openedViews.add(fastEditPopup);

    }


    public void showRemindPopup(Contact contact) {
        closeOtherPopup();
        remindPopup = (FrameLayout) getActivity().findViewById(R.id.remindPopup);
        ((TextView) remindPopup.findViewById(R.id.nameContact)).setText(selectedContactPopup.getName());
        ((TextView) remindPopup.findViewById(R.id.company_title)).setText(selectedContactPopup.getCompany());

        initIconColor(selectedContact, mainView);

        LinearLayout containerHashTags = (LinearLayout) remindPopup.findViewById(R.id.containerHashTags);
        if (containerHashTags.getChildCount() > 0)
            containerHashTags.removeAllViews();
        for (HashTag hashTag : contact.getListOfHashtags()) {
            TextView text = new TextView(getActivity());

            text.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            text.setTextColor(getResources().getColor(R.color.primary_dark));
            text.setText(hashTag.getHashTagValue() + " ");
            containerHashTags.addView(text);
        }

        if (contact.getPhotoURL() == null) {
            GradientDrawable circle = (GradientDrawable) ContextCompat.getDrawable(mainView.getContext(), R.drawable.blue_circle).mutate();
            circle.setColor(contact.color);
            remindPopup.findViewById(R.id.profilePopupAvatar).setBackground(circle);
            ((ImageView) remindPopup.findViewById(R.id.profilePopupAvatar)).setImageDrawable(null);

            String initials = "";
            String names[] = contact.getName().split("\\s+");

            for (String namePart : names)
                initials += namePart.charAt(0);

            remindPopup.findViewById(R.id.profilePopupInitials).setVisibility(View.VISIBLE);
            ((TextView) remindPopup.findViewById(R.id.profilePopupInitials)).setText(initials);
        } else {
            remindPopup.findViewById(R.id.profilePopupInitials).setVisibility(View.GONE);
            ((ImageView) remindPopup.findViewById(R.id.profilePopupAvatar)).setImageURI(Uri.parse(contact.getPhotoURL()));
        }

        remindPopup.findViewById(R.id.remind_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remindPopup.setVisibility(View.GONE);
                showProfilePopUp(contact);
            }
        });
        initIconColor(contact, remindPopup);
        remindPopup.setVisibility(View.VISIBLE);
        if (openedViews != null) openedViews.add(remindPopup);
    }

    private void fillProfileData(final Contact contact, final View profilePopUp) {
        ((TextView) profilePopUp.findViewById(R.id.name)).setText(contact.getName());
        ((TextView) profilePopUp.findViewById(R.id.company_title)).setText(contact.getCompany());
        //   ((TextView) profilePopUp.findViewById(R.id.company_title)).setClickable(false);

        profilePopUp.findViewById(R.id.company).setVisibility(View.VISIBLE);
        //   profilePopUp.findViewById(R.id.company_title).setVisibility(View.GONE);

        if (contact.getCompanyPossition() != null && contact.getCompanyPossition() != "") {
            profilePopUp.findViewById(R.id.company).setVisibility(View.VISIBLE);
            ((TextView) profilePopUp.findViewById(R.id.company)).setText(contact.getCompanyPossition());
        } else {
            ((TextView) profilePopUp.findViewById(R.id.company)).setHint("Position");
            ((TextView) profilePopUp.findViewById(R.id.company)).setText("");
        }

        profilePopUp.findViewById(R.id.user_call_block).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + contact.listOfContactInfo.get(0).toString())));
            }
        });

        profilePopUp.findViewById(R.id.user_share_block).setOnClickListener(v -> {
            String exportData = "";
            //contact.fillData(getContext(), contactsService);
            if (contact.getName() != null) exportData += "Name: " + contact.getName() + "\n";
            if (contact.getCompany() != null)
                exportData += "Company: " + contact.getCompany() + "\n";
            if (contact.getCompanyPossition() != null)
                exportData += "Position: " + contact.getCompanyPossition() + "\n";
            if (contact.listOfContactInfo != null) {
                for (ContactInfo contactInfo : contact.listOfContactInfo) {
                    if (contactInfo.isPhone && !contactInfo.value.equals("+000000000000"))
                        exportData += "Phone: " + contactInfo.toString() + "\n";
                    if (contactInfo.isEmail) exportData += "Email: " + contactInfo + "\n";
                    if (contactInfo.isNote && ClipboardType.isFacebook(contactInfo.value)) {
                        exportData += "Facebook: " + contactInfo + "\n";
                        continue;
                    }
                    if (contactInfo.isNote && ClipboardType.isVk(contactInfo.value)) {
                        exportData += "Vk: " + contactInfo + "\n";
                        continue;
                    }
                    if (contactInfo.isNote && ClipboardType.isInsta(contactInfo.value)) {
                        exportData += "Instagram: " + contactInfo + "\n";
                        continue;
                    }
                    if (contactInfo.isNote && ClipboardType.isLinkedIn(contactInfo.value)) {
                        exportData += "Linkedin: " + contactInfo + "\n";
                        continue;
                    }
                    if (contactInfo.isNote && ClipboardType.isTwitter(contactInfo.value)) {
                        exportData += "Twitter: " + contactInfo + "\n";
                        continue;
                    }
                    if (contactInfo.isNote && ClipboardType.isYoutube(contactInfo.value)) {
                        exportData += "Youtube: " + contactInfo + "\n";
                        continue;
                    }
                    if (contactInfo.isNote && ClipboardType.isG_Sheet(contactInfo.value)) {
                        exportData += "Google_sheet: " + contactInfo + "\n";
                        continue;
                    }
                    if (contactInfo.isNote && ClipboardType.isG_Doc(contactInfo.value)) {
                        exportData += "Google_doc: " + contactInfo + "\n";
                        continue;
                    }

                    if (contactInfo.isNote && ClipboardType.is_Tumblr(contactInfo.value)) {
                        exportData += "Tumblr: " + contactInfo + "\n";
                        continue;
                    }
                    if (contactInfo.isNote && ClipboardType.is_Angel(contactInfo.value)) {
                        exportData += "Angel: " + contactInfo + "\n";
                        continue;
                    }
                    if (contactInfo.isNote && ClipboardType.isMedium(contactInfo.value)) {
                        exportData += "Medium: " + contactInfo + "\n";
                        continue;
                    }
                    if (contactInfo.isNote && ClipboardType.isGitHub(contactInfo.value)) {
                        exportData += "Github: " + contactInfo + "\n";
                        continue;
                    }
                    if (contactInfo.isNote && ClipboardType.isWeb(contactInfo.value))
                        exportData += "Web: " + contactInfo + "\n";
                }
            }
            if (contact.getSocialModel() != null) {
                if (contact.getSocialModel().getFacebookLink() != null && !contact.getSocialModel().getFacebookLink().isEmpty())
                    exportData += "Facebook: " + getPhoneNumberInfo(contact.getSocialModel().getFacebookLink()) + "\n";
                if (contact.getSocialModel().getVkLink() != null && !contact.getSocialModel().getVkLink().isEmpty())
                    exportData += "Vk: " + getPhoneNumberInfo(contact.getSocialModel().getVkLink()) + "\n";
                if (contact.getSocialModel().getLinkedInLink() != null && !contact.getSocialModel().getLinkedInLink().isEmpty())
                    exportData += "Linkedin: " + getPhoneNumberInfo(contact.getSocialModel().getLinkedInLink()) + "\n";
                if (contact.getSocialModel().getInstagramLink() != null && !contact.getSocialModel().getInstagramLink().isEmpty())
                    exportData += "Instagram: " + getPhoneNumberInfo(contact.getSocialModel().getInstagramLink()) + "\n";
                if (contact.getSocialModel().getTwitterLink() != null && !contact.getSocialModel().getTwitterLink().isEmpty())
                    exportData += "Twitter: " + getPhoneNumberInfo(contact.getSocialModel().getTwitterLink()) + "\n";
                if (contact.getSocialModel().getYoutubeLink() != null && !contact.getSocialModel().getYoutubeLink().isEmpty())
                    exportData += "Youtube: " + getPhoneNumberInfo(contact.getSocialModel().getYoutubeLink()) + "\n";
               /* if(contact.getSocialModel().getWhatsappLink() != null && !contact.getSocialModel().getWhatsappLink().isEmpty())
                    exportData += "Whatsapp: " + getPhoneNumberInfo(contact.getSocialModel().getWhatsappLink()) + "\n";
                if(contact.getSocialModel().getViberLink() != null && !contact.getSocialModel().getViberLink().isEmpty())
                    exportData += "Viber: " + getPhoneNumberInfo(contact.getSocialModel().getViberLink()) + "\n";
                if(contact.getSocialModel().getTelegramLink() != null && !contact.getSocialModel().getTelegramLink().isEmpty())
                    exportData += "Telegram: " + getPhoneNumberInfo(contact.getSocialModel().getTelegramLink()) + "\n";
                if(contact.getSocialModel().getSkypeLink() != null && !contact.getSocialModel().getSkypeLink().isEmpty())
                    exportData += "Skype: " + getPhoneNumberInfo(contact.getSocialModel().getSkypeLink()) + "\n";*/
            }

            if (contact.getListOfHashtags() != null && !contact.getListOfHashtags().isEmpty()) {
                exportData += "Tags:";
                for (HashTag hashTag : contact.getListOfHashtags()) {
                    if (hashTag != null && hashTag.getHashTagValue() != null) {
                        exportData += " " + hashTag.getHashTagValue();
                    }
                }
                exportData += "\n";
            }

            exportData += "\n";
            exportData += "Data shared via http://Extime.pro\n";
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, exportData);
            startActivity(Intent.createChooser(shareIntent, "Поделиться контактом"));
        });


        for (ContactInfo contactInfo : contact.listOfContactInfo) {
            if (contactInfo.type.toLowerCase().compareTo("email") == 0) {
                ((ImageView) profilePopUp.findViewById(R.id.emailImg)).setImageResource(R.drawable.icn_bottombar_emails_blue);
                profilePopUp.findViewById(R.id.emailImg).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                "mailto", contactInfo.value, null));
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
                        emailIntent.putExtra(Intent.EXTRA_TEXT, "");
                        startActivity(Intent.createChooser(emailIntent, "Send email"));
                    }
                });
            }
        }
        /*if(contact.webSite.length() > 0){
            ((ImageView)profilePopUp.findViewById(R.id.webImg)).setImageResource(R.drawable.icn_popup_web_blue);
            profilePopUp.findViewById(R.id.webImg).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse("http://"+contact.webSite));
                        startActivity(i);
                    }catch (Exception e){
                        Toast.makeText(mainView.getContext()," Incorrect web site",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }*/

        profilePopUp.findViewById(R.id.user_edit_block).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFastEditPopup(selectedContactPopup);
            }
        });

        profilePopUp.findViewById(R.id.user_remind_block).setOnClickListener(v -> {
            profilePopUp.setVisibility(View.GONE);
            showRemindPopup(selectedContactPopup);
        });

        profilePopUp.findViewById(R.id.user_profile_block).setOnClickListener(v -> {

            android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.main_content, ProfileFragment.newInstance(contact, false)).addToBackStack("contact").commit();

            profilePopUp.setVisibility(View.GONE);
        });


        /////===================================================================================
        profilePopUp.findViewById(R.id.user_profile_block).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                TextView textView = ((TextView) profilePopUp.findViewById(R.id.textPreviewProfile));
                //    ImageView imageView = ((ImageView) profilePopUp.findViewById(R.id.imagePreviewIcon));
                FrameLayout frameLayout = ((FrameLayout) profilePopUp.findViewById(R.id.user_profile_block));
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        OnTouchMethod(textView);
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        OnUpTouchMethod(textView);
                        break;
                    }
                    case MotionEvent.ACTION_CANCEL: {
                        OnCalcelTouchMethod(textView);
                        break;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        OnMoveTouchMethod(textView, motionEvent, frameLayout);
                        break;
                    }
                }

                return false;
            }
        });

        profilePopUp.findViewById(R.id.user_edit_block).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                TextView textView = ((TextView) profilePopUp.findViewById(R.id.textEditPreview));
                ImageView imageView = ((ImageView) profilePopUp.findViewById(R.id.editImagePreview));
                FrameLayout frameLayout = ((FrameLayout) profilePopUp.findViewById(R.id.user_edit_block));
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        OnTouchMethod(textView);
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        OnUpTouchMethod(textView);
                        break;
                    }
                    case MotionEvent.ACTION_CANCEL: {
                        OnCalcelTouchMethod(textView);
                        break;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        OnMoveTouchMethod(textView, motionEvent, frameLayout);
                        break;
                    }
                }

                return false;
            }
        });


        profilePopUp.findViewById(R.id.user_call_block).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                TextView textView = ((TextView) profilePopUp.findViewById(R.id.TExtCallPreview));
                ImageView imageView = ((ImageView) profilePopUp.findViewById(R.id.CallImagePreview));
                FrameLayout frameLayout = ((FrameLayout) profilePopUp.findViewById(R.id.user_call_block));
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        OnTouchMethod(textView);
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        OnUpTouchMethod(textView);
                        break;
                    }
                    case MotionEvent.ACTION_CANCEL: {
                        OnCalcelTouchMethod(textView);
                        break;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        OnMoveTouchMethod(textView, motionEvent, frameLayout);
                        break;
                    }
                }

                return false;
            }
        });


        profilePopUp.findViewById(R.id.user_remind_block).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                TextView textView = ((TextView) profilePopUp.findViewById(R.id.TextRemindPreview));
                ImageView imageView = ((ImageView) profilePopUp.findViewById(R.id.ImageRemindPreview));
                FrameLayout frameLayout = ((FrameLayout) profilePopUp.findViewById(R.id.user_remind_block));
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        OnTouchMethod(textView);
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        OnUpTouchMethod(textView);
                        break;
                    }
                    case MotionEvent.ACTION_CANCEL: {
                        OnCalcelTouchMethod(textView);
                        break;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        OnMoveTouchMethod(textView, motionEvent, frameLayout);
                        break;
                    }
                }

                return false;
            }
        });


        profilePopUp.findViewById(R.id.user_share_block).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                TextView textView = ((TextView) profilePopUp.findViewById(R.id.TextSharePreview));
                ImageView imageView = ((ImageView) profilePopUp.findViewById(R.id.ImageSharePreview));
                FrameLayout frameLayout = ((FrameLayout) profilePopUp.findViewById(R.id.user_share_block));
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        OnTouchMethod(textView);
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        OnUpTouchMethod(textView);
                        break;
                    }
                    case MotionEvent.ACTION_CANCEL: {
                        OnCalcelTouchMethod(textView);
                        break;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        OnMoveTouchMethod(textView, motionEvent, frameLayout);
                        break;
                    }
                }

                return false;
            }
        });
        //=======================


        // ((TextView) profilePopUp.findViewById(R.id.time)).setText(/*getUpdTime*/(contact.time));

       /* if (contact.getPhotoURL() == null) {
            GradientDrawable circle = (GradientDrawable) ContextCompat.getDrawable(mainView.getContext(), R.drawable.blue_circle).mutate();
            circle.setColor(contact.color);
            profilePopUp.findViewById(R.id.profilePopupAvatar).setBackground(circle);
            ((ImageView) profilePopUp.findViewById(R.id.profilePopupAvatar)).setImageDrawable(null);

            String initials = "";
            String names[] = contact.getName().split("\\s+");

            for (String namePart : names)
                initials += namePart.charAt(0);

            profilePopUp.findViewById(R.id.profilePopupInitials).setVisibility(View.VISIBLE);
            ((TextView) profilePopUp.findViewById(R.id.profilePopupInitials)).setText(initials);
        } else {
            profilePopUp.findViewById(R.id.profilePopupInitials).setVisibility(View.GONE);
            profilePopUp.findViewById(R.id.profilePopupAvatar).setBackgroundColor(Color.TRANSPARENT);
            ((ImageView) profilePopUp.findViewById(R.id.profilePopupAvatar)).setImageURI(Uri.parse(contact.getPhotoURL()));
        }*/


        try {
            profilePopUp.findViewById(R.id.profilePopupInitials).setVisibility(View.GONE);
            profilePopUp.findViewById(R.id.profilePopupAvatar).setBackgroundColor(Color.TRANSPARENT);
            ((ImageView) profilePopUp.findViewById(R.id.profilePopupAvatar)).setImageURI(Uri.parse(contact.getPhotoURL()));
            if (((BitmapDrawable) ((ImageView) profilePopUp.findViewById(R.id.profilePopupAvatar)).getDrawable()).getBitmap() == null) {
                GradientDrawable circle = (GradientDrawable) ContextCompat.getDrawable(mainView.getContext(), R.drawable.blue_circle).mutate();
                circle.setColor(contact.color);
                profilePopUp.findViewById(R.id.profilePopupAvatar).setBackground(circle);
                ((ImageView) profilePopUp.findViewById(R.id.profilePopupAvatar)).setImageDrawable(null);

                String initials = "";
                String names[] = contact.getName().split("\\s+");

                for (String namePart : names)
                    initials += namePart.charAt(0);

                profilePopUp.findViewById(R.id.profilePopupInitials).setVisibility(View.VISIBLE);
                ((TextView) profilePopUp.findViewById(R.id.profilePopupInitials)).setText(initials);
            }
        } catch (Exception e) {
            GradientDrawable circle = (GradientDrawable) ContextCompat.getDrawable(mainView.getContext(), R.drawable.blue_circle).mutate();
            circle.setColor(contact.color);
            profilePopUp.findViewById(R.id.profilePopupAvatar).setBackground(circle);
            ((ImageView) profilePopUp.findViewById(R.id.profilePopupAvatar)).setImageDrawable(null);

            String initials = "";
            String names[] = contact.getName().split("\\s+");

            for (String namePart : names)
                initials += namePart.charAt(0);

            profilePopUp.findViewById(R.id.profilePopupInitials).setVisibility(View.VISIBLE);
            ((TextView) profilePopUp.findViewById(R.id.profilePopupInitials)).setText(initials);
        }


        for (ContactInfo contactInfo : contact.listOfContactInfo) {
            if (contactInfo.type.toLowerCase().compareTo("email") == 0) {
                ((ImageView) profilePopUp.findViewById(R.id.emailImg)).setImageResource(R.drawable.icn_bottombar_emails_blue);
                profilePopUp.findViewById(R.id.emailImg).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                "mailto", contactInfo.value, null));
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
                        emailIntent.putExtra(Intent.EXTRA_TEXT, "");
                        startActivity(Intent.createChooser(emailIntent, "Send email"));
                    }
                });
            }
        }
        if (contact.webSite.length() > 0) {
            ((ImageView) profilePopUp.findViewById(R.id.webImg)).setImageResource(R.drawable.icn_popup_web_blue);
            profilePopUp.findViewById(R.id.webImg).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse("http://" + contact.webSite));
                        startActivity(i);
                    } catch (Exception e) {
                        Toast.makeText(mainView.getContext(), " Incorrect web site", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }


        //====================================================================================================


        long id = contact.getId();


        new Thread(new Runnable() {
            @Override
            public void run() {


                Contact contact = ContactCacheService.getContactByIdAndWithoutRealm(id);


                    /*RealmConfiguration contextRealm = new RealmConfiguration.
                            Builder().
                            deleteRealmIfMigrationNeeded().
                            build();
                    Realm realm = Realm.getInstance(contextRealm);*/

                    /*Cursor c = contactsService.getCursorByType(ContactsService.TYPE_CURSOR.CONTACT_NAME, contact.getIdContact());
                    if (c == null || c.getCount() == 0) {
                        String idN = contactsService.getIdContactByName(contact.getName());
                        if(idN == null){
                            return;
                        }else{
                            //realm.beginTransaction();
                            contact.setIdContact(idN);
                            //realm.commitTransaction();
                        }
                    }*/

                Cursor c = contactsService.getCursorByType(ContactsService.TYPE_CURSOR.CONTACT_NAME, contact.getIdContact());
                if (c == null || c.getCount() == 0) {
                        /*System.out.println("SEARCH NULL CONTACT");
                        String idN = contactsService.getIdContactByName(contact.getName());
                        if (idN == null) {
                            return;
                        } else {
                            //realm.beginTransaction();
                            contact.setIdContact(idN);
                            //realm.commitTransaction();
                        }*/
                    return;
                } else {
                    /*boolean checkName = false;
                    while (c.moveToNext()) {

                        if (contact.getName().equalsIgnoreCase(c.getString(0))) {
                            checkName = true;
                            break;
                        }
                    }
                    if (!checkName) {
                        return;
                    }*/
                }


                ArrayList<DataUpdate> listUpdatingData;
                listUpdatingData = new ArrayList<>();
                Cursor organizationCursor = contactsService.getCursorByType(ContactsService.TYPE_CURSOR.ORGANIZATION, contact.getIdContact());

                Realm realm2 = Realm.getDefaultInstance();

                ArrayList<Contact> listOFCompanies = ContactCacheService.getCompaniesRealm(realm2);

                boolean showUpdate = false;

                String companyOfContact = null;
                if (contact.getCompany() != null)
                    companyOfContact = contact.getCompany();

                String positionContact = null;

                if (contact.getCompanyPossition() != null)
                    positionContact = contact.getCompanyPossition();

                //realm.beginTransaction();
                contact.setCompanyPossition(null);
                //realm.commitTransaction();

                int nameHash = contact.getName().hashCode();

                boolean found = false;

                while (organizationCursor != null && organizationCursor.moveToNext()) {
                    String orgName = organizationCursor.getString(organizationCursor.getColumnIndex(ContactsContract.CommonDataKinds.Organization.DATA));
                    String companyPossition = organizationCursor.getString(organizationCursor.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TITLE));



                    //realm.beginTransaction();
                    contact.setCompanyPossition(companyPossition);

                    //realm.commitTransaction();

                    if (orgName != null && !orgName.isEmpty()) {



                        String oldCompany = null;
                        if (contact.getCompany() != null && !contact.getCompany().isEmpty() && !contact.getCompany().trim().equalsIgnoreCase(orgName.trim()))
                            oldCompany = contact.getCompany();

                        //realm.beginTransaction();
                        contact.setCompany(orgName.trim());
                        //realm.commitTransaction();

                            /*for (Contact searchCompanyContact : listOFCompanies) {
                                if (searchCompanyContact.isValid() && searchCompanyContact.getName().equalsIgnoreCase(orgName.trim())) {
                                    if (oldCompany != null) {
                                        for (Contact comp : listOFCompanies) {
                                            if (comp.isValid() && comp.getName().equalsIgnoreCase(oldCompany.trim())) {
                                                if (comp.listOfContacts.size() == 1) {
                                                    EventBus.getDefault().post(new RemoveContact(comp.getId()));
                                                }
                                                ContactCacheService.removeContactFromCompany(comp, contact);
                                                break;
                                            }
                                        }
                                    }

                                    if (!searchCompanyContact.listOfContacts.contains(contact)) {
                                        if (searchCompanyContact.getName().equals("1337")) {

                                        }

                                        realm.beginTransaction();
                                        searchCompanyContact.listOfContacts.add(contact);
                                        realm.commitTransaction();
                                    }
                                    found = true;
                                }
                            }*/

                            /*if (!found) {
                                Date date1 = contact.getDateCreate();
                                Calendar cal1 = Calendar.getInstance();
                                cal1.setTime(date1);
                                Time time1 = contactsService.getRandomDate();
                                time1.setHours(cal1.get(Calendar.HOUR_OF_DAY));
                                time1.setMinutes(cal1.get(Calendar.MINUTE));
                                time1.setSeconds(cal1.get(Calendar.SECOND));
                                Contact companyContact = new Contact(0, orgName.trim(), null, false, true, true, time1.toString(), null, date1);
                                companyContact.time = contact.time.toString();
                                companyContact.color = Color.rgb(Math.abs(nameHash * 28439) % 255, Math.abs(nameHash * 211239) % 255, Math.abs(nameHash * 42368) % 255);
                                if (oldCompany != null) {
                                    for (Contact comp : listOFCompanies) {
                                        if (comp.isValid() && comp.getName().equalsIgnoreCase(oldCompany.trim())) {

                                            if (comp.listOfContacts.size() == 1) {
                                            *//*ContactAdapter cont = ContactAdapter.contactAd;
                                            cont.removeContactById(comp);*//*
                         *//* getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    contactAdapter.removeContactById(comp);
                                                    contactAdapter.notifyDataSetChanged();
                                                }
                                            });*//*
                                                //ContactsFragment.UPD_ALL = true;
                                                EventBus.getDefault().post(new RemoveContact(comp.getId()));
                                            }
                                            ContactCacheService.removeContactFromCompany(comp, contact);
                                            break;
                                        }
                                    }
                                }
                                companyContact.listOfContacts.add(contact);
                                ContactCacheService.updateCompany(companyContact);
                                listOFCompanies.add(ContactCacheService.getCompany(companyContact.getName()));
                                //ContactAdapter cont = ContactAdapter.contactAd;
                                //cont.addContact(companyContact);
                                //if(typeEnum.equals(FillDataEnums.PROFILE) || typeEnum.equals(FillDataEnums.PREVIEW)) {
                                EventBus.getDefault().post(new AddContact(companyContact.getId()));
                                ContactsFragment.UPD_ALL = true;
                           *//* }else if(typeEnum.equals(FillDataEnums.NEW)){
                                companyNewReturn = companyContact;
                            }*//*
                            }*/
                    }
                }
                boolean checkNull = false;
                if (organizationCursor != null && organizationCursor.getCount() > 0) {
                    organizationCursor.moveToFirst();
                    do {
                        if (organizationCursor.getString(organizationCursor.getColumnIndex(ContactsContract.CommonDataKinds.Organization.DATA)) != null && !organizationCursor.getString(organizationCursor.getColumnIndex(ContactsContract.CommonDataKinds.Organization.DATA)).equals("")) {
                            checkNull = true;
                            break;
                        }
                    } while (organizationCursor.moveToNext());

                }

                if (!checkNull) {
                    if (contact.getCompany() != null) {

                        for (Contact comp : listOFCompanies) {
                            if (comp.isValid() && comp.getName().equalsIgnoreCase(contact.getCompany())) {

                                    /*if (comp.listOfContacts.size() == 1) {
                                        //ContactAdapter cont = ContactAdapter.contactAd;
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            contactAdapter.removeContactById(comp);
                                            contactAdapter.notifyDataSetChanged();
                                        }
                                    });
                                        EventBus.getDefault().post(new RemoveContact(comp.getId()));


                                        //cont.notifyDataSetChanged();
                                        //ContactsFragment.UPD_ALL = true;
                                    }*/

                                //ContactCacheService.removeContactFromCompany(comp, contact);
                                //realm.beginTransaction();
                                contact.setCompany(null);
                                //realm.commitTransaction();
                                break;
                            }
                        }
                    }
                }

                realm2.close();


                if ((companyOfContact == null && contact.getCompany() != null) || (companyOfContact != null && contact.getCompany() == null) || (companyOfContact != null && contact.getCompany() != null && !companyOfContact.equals(contact.getCompany()))) {
                    showUpdate = true;
                    if (contact.getCompany() != null) {
                        String nameC = contact.getCompany();
                        listUpdatingData.add(new DataUpdate(nameC, ClipboardEnum.COMPANY));
                            /*getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ((TextView) profilePopUp.findViewById(R.id.company_title)).setText(nameC);

                                }
                            });*/

                    } else {
                            /*getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                               *//* ((TextView) profilePopUp.findViewById(R.id.company_title)).setText("");

                                ((TextView) profilePopUp.findViewById(R.id.company_title)).setHint("Company");

                                profilePopUp.findViewById(R.id.company_title).setOnClickListener(v -> {

                                            if (((TextView) profilePopUp.findViewById(R.id.company_title)).getText() == "")
                                                showCompanyAddPopup(ContactCacheService.getContactById(id), null, false);
                                        }
                                );*//*
                                }
                            });*/
                    }
                }

                if ((positionContact == null && contact.getCompanyPossition() != null) || (positionContact != null && contact.getCompanyPossition() == null) || (positionContact != null && contact.getCompanyPossition() != null && !positionContact.equals(contact.getCompanyPossition()))) {
                    //contact.setCompanyPossition(positionContact);
                    showUpdate = true;
                    if (contact.getCompanyPossition() != null) {
                        String pos = contact.getCompanyPossition();
                        listUpdatingData.add(new DataUpdate(pos, ClipboardEnum.POSITION));
                            /*getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {


                                    profilePopUp.findViewById(R.id.company).setVisibility(View.VISIBLE);
                                    ((TextView) profilePopUp.findViewById(R.id.company)).setText(pos);


                                }
                            });*/
                    } else {

                            /*getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ((TextView) profilePopUp.findViewById(R.id.company)).setVisibility(View.VISIBLE);
                                    ((TextView) profilePopUp.findViewById(R.id.company)).setText("");
                                    ((TextView) profilePopUp.findViewById(R.id.company)).setHint("Position");
                                }
                            });*/


                    }
                }


                String contactName = ContactsService.getDisplayName(getContext(), contact.getIdContact());
                if (contactName == null || contactName.equals("")) {

                } else if (!contact.getName().equals(contactName)) {
                    //realm.beginTransaction();
                    contact.setName(contactName);
                    //realm.commitTransaction();
                    showUpdate = true;
                    listUpdatingData.add(new DataUpdate(contactName, ClipboardEnum.NAME));

                        /*getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ((TextView) profilePopUp.findViewById(R.id.name)).setText(contactName);
                            }
                        });*/
                }

                //phones


                ArrayList<String> listPhones = new ArrayList<>();
                for (int i = 0; i < contact.getListOfContactInfo().size(); i++) {
                    if (contact.getListOfContactInfo().get(i).type.equalsIgnoreCase("phone")) {
                        listPhones.add(contact.getListOfContactInfo().get(i).value);
                    }
                }


                ArrayList<String> phone = contactsService.getContactPhones(contact.getIdContact());

                //realm.beginTransaction();
                if (phone != null && phone.size() > 0) {
                    for (int i = 0; i < phone.size(); i++) {

                        String phonee = phone.get(i).trim();
                        phonee = phonee.replaceAll("[\\.\\s\\-\\+\\(\\)]", "");

                        boolean phoneFind = false;
                        for (int j = 0; j < listPhones.size(); j++) {
                            String phone1 = listPhones.get(j);
                            phone1 = phone1.replaceAll("[\\.\\s\\-\\+\\(\\)]", "");
                            if (phone1.equals(phonee)) {
                                phoneFind = true;
                                //listPhones.remove(j);
                                break;
                            }
                        }

                        if (!phoneFind) {
                            if (!phone.get(i).equals("+000000000000")) {
                                showUpdate = true;
                                contact.addPhone(phone.get(i));
                                listUpdatingData.add(new DataUpdate(phone.get(i), ClipboardEnum.PHONE));
                            }
                        }
                    }
                } else if (listPhones.isEmpty()) {
                    //contact.addPhone("+000000000000");
                    //contactsService.addPhoneToContact(contact.getIdContact(), "+000000000000", contact.getName());
                    //setName(contactName.trim());
                }

                if (listPhones != null && listPhones.size() > 0) {
                    for (int i = 0; i < contact.getListOfContactInfo().size(); i++) {
                        for (String str : listPhones) {
                            if (contact.getListOfContactInfo().get(i).value.equals(str)) {
                                //contact.listOfContactInfo.remove(contact.getListOfContactInfo().get(i));
                                //showUpdate = true;
                                //i--;
                                break;
                            }
                        }
                    }
                }


                //email
                //realm.beginTransaction();
                ArrayList<String> listEmail = new ArrayList<>();
                if (contact.getListOfContactInfo() != null) {
                    for (int i = 0; i < contact.getListOfContactInfo().size(); i++) {
                        if (contact.getListOfContactInfo().get(i).type.equalsIgnoreCase("email")) {
                            listEmail.add(contact.getListOfContactInfo().get(i).value);
                        }
                    }
                }

                ArrayList<String> addr = contactsService.getContactEmails(contact.getIdContact());
                for (String str : addr) {
                    boolean emailfind = false;
                    for (int i = 0; i < listEmail.size(); i++) {
                        if (str.equalsIgnoreCase(listEmail.get(i))) {
                            emailfind = true;
                            listEmail.remove(i);
                            break;
                        }
                    }
                    if (!emailfind) {
                        showUpdate = true;
                        contact.addEmail(str);
                        listUpdatingData.add(new DataUpdate(str, ClipboardEnum.EMAIL));
                    }
                }

                if (listEmail != null && listEmail.size() > 0) {
                    for (int i = 0; i < contact.getListOfContactInfo().size(); i++) {
                        for (String str : listEmail) {
                            if (contact.getListOfContactInfo().get(i).value.equals(str)) {
                                contact.listOfContactInfo.remove(contact.getListOfContactInfo().get(i));
                                showUpdate = true;
                                i--;
                                break;
                            }
                        }
                    }
                }

                //realm.commitTransaction();
                //


                //address


                //realm.beginTransaction();


                ArrayList<String> listAddress = new ArrayList<>();
                for (int i = 0; i < contact.getListOfContactInfo().size(); i++) {
                    if (contact.getListOfContactInfo().get(i).type.equalsIgnoreCase("address")) {
                        listAddress.add(contact.getListOfContactInfo().get(i).value);
                    }
                }

                Cursor adressCursor = contactsService.getCursorByType(ContactsService.TYPE_CURSOR.CONTACT_ADRESS, contact.getIdContact());
                while (adressCursor != null && adressCursor.moveToNext()) {
                    String adress = adressCursor.getString(adressCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
                    boolean findAddress = false;
                    for (String str : listAddress) {
                        if (str.equalsIgnoreCase(adress)) {
                            findAddress = true;
                            listAddress.remove(str);
                            break;
                        }
                    }
                    if (!findAddress) {
                        showUpdate = true;
                        contact.addAddress(adress);
                        listUpdatingData.add(new DataUpdate(adress, ClipboardEnum.ADDRESS));
                    }

                }

                if (listAddress != null && listAddress.size() > 0) {
                    for (int i = 0; i < contact.getListOfContactInfo().size(); i++) {
                        for (String str : listAddress) {
                            if (contact.getListOfContactInfo().get(i).value.equals(str)) {
                                contact.listOfContactInfo.remove(contact.getListOfContactInfo().get(i));
                                showUpdate = true;
                                i--;
                                break;
                            }
                        }
                    }
                }


                //hash
                ArrayList<String> listOfHash = new ArrayList<>();
                RealmList<HashTag> hashList = new RealmList<>();

                ArrayList<HashTag> listHashOfContacts = new ArrayList<>();
                if (contact.getListOfHashtags() != null)
                    listHashOfContacts.addAll(contact.getListOfHashtags());

                Cursor noteCursor = contactsService.getCursorByType(ContactsService.TYPE_CURSOR.NOTE, contact.getIdContact());

                //realm.beginTransaction();
                SocialModel socialModel = new SocialModel();
                //realm.commitTransaction();
                boolean hasFacebook = false;
                boolean hasInst = false;
                boolean hasLinked = false;
                boolean hasVk = false;
                boolean hasSkype = false;
                boolean hasTelegram = contact.hasTelegram;
                boolean hasViber = contact.hasViber;
                boolean hasWhatsapp = contact.hasWhatsapp;
                boolean hasYoutube = false;
                boolean hasTwitter = false;
                boolean hasMedium = false;
                //realm.beginTransaction();
                if (noteCursor != null && noteCursor.getCount() > 0) {
                    while (noteCursor.moveToNext()) {
                        String note = noteCursor.getString(noteCursor.getColumnIndex(ContactsContract.CommonDataKinds.Note.NOTE));
                        if (note != null && note.length() > 0) {
                            if (note.charAt(0) == '#') {
                                if (!listOfHash.contains(note.toLowerCase().trim())) {
                                    HashTag hashtag = new HashTag();
                                    hashtag.setDate(new Date());
                                    hashtag.setHashTagValue(note.toLowerCase().trim());
                                    hashList.add(hashtag);
                                    listOfHash.add(note.toLowerCase().trim());
                                }


                            } else if (/*(note.length() > 26 && note.toString().substring(0, 26).equalsIgnoreCase("https://www.instagram.com/")) || (note.length() > 18 && note.toString().substring(0, 18).equalsIgnoreCase("www.instagram.com/") ||
                                    (note.length() > 14 && note.toString().substring(0, 14).equalsIgnoreCase("instagram.com/")) || (note.length() > 22 && note.toString().substring(0, 22).equalsIgnoreCase("https://instagram.com/")))*/ ClipboardType.isInsta(note)) {
                                //SocialModel socialModel = contact.getSocialModel();


                                String username = note;
                                if (username.contains("?utm")) {
                                    int ind = username.indexOf('?');
                                    if (ind != -1)
                                        username = username.substring(0, ind);

                                    contactsService.updateNote(contact.getIdContact(), note, username);
                                }

                                if (socialModel != null) {
                                    if (socialModel.getInstagramLink() == null || socialModel.getInstagramLink().isEmpty()) {
                                        socialModel.setInstagramLink(username);
                                        hasInst = true;
                                    } else {
                                        if (contact.getListOfContactInfo() == null) {
                                            contact.addNote(note);
                                        } else {
                                            boolean checkS = false;
                                            for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                                if (contactInfo.value.equalsIgnoreCase(note)) {
                                                    checkS = true;
                                                    break;
                                                }
                                            }
                                            if (!checkS)
                                                contact.addNote(note);

                                        }
                                    }
                                }
                            } else if (ClipboardType.isVk(note)) {
                                //SocialModel socialModel = contact.getSocialModel();


                                if (socialModel != null) {
                                    if (socialModel.getVkLink() == null || socialModel.getVkLink().isEmpty()) {
                                        socialModel.setVkLink(note);
                                        hasVk = true;
                                    } else {
                                        if (contact.getListOfContactInfo() == null) {
                                            contact.addNote(note);
                                        } else {
                                            boolean checkS = false;
                                            for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                                if (contactInfo.value.equalsIgnoreCase(note)) {
                                                    checkS = true;
                                                    break;
                                                }
                                            }
                                            if (!checkS)
                                                contact.addNote(note);

                                        }
                                    }
                                }

                            } else if (ClipboardType.isFacebook(note)) {
                                //SocialModel socialModel = contact.getSocialModel();


                                if (socialModel != null) {
                                    if (socialModel.getFacebookLink() == null || socialModel.getFacebookLink().isEmpty()) {
                                        socialModel.setFacebookLink(note);
                                        hasFacebook = true;
                                    } else {
                                        if (contact.getListOfContactInfo() == null) {
                                            contact.addNote(note);
                                        } else {
                                            boolean checkS = false;
                                            for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                                if (contactInfo.value.equalsIgnoreCase(note)) {
                                                    checkS = true;
                                                    break;
                                                }
                                            }
                                            if (!checkS)
                                                contact.addNote(note);

                                        }
                                    }
                                }

                            } else if (ClipboardType.isLinkedIn(note)) {
                                //SocialModel socialModel = contact.getSocialModel();


                                if (socialModel != null) {
                                    if (socialModel.getLinkedInLink() == null || socialModel.getLinkedInLink().isEmpty()) {
                                        socialModel.setLinkedInLink(note);
                                        hasLinked = true;
                                    } else {
                                        if (contact.getListOfContactInfo() == null) {
                                            contact.addNote(note);
                                        } else {
                                            boolean checkS = false;
                                            for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                                if (contactInfo.value.equalsIgnoreCase(note)) {
                                                    checkS = true;
                                                    break;
                                                }
                                            }
                                            if (!checkS)
                                                contact.addNote(note);

                                        }
                                    }
                                }

                            } else if (note.contains("viber.com") || note.contains("https://www.viber.com")) {
                                //SocialModel socialModel = contact.getSocialModel();
                                hasViber = true;

                                socialModel.setViberLink(note);

                            } else if (note.toString().contains("whatsapp.com") || note.toString().contains("https://www.whatsapp.com")) {
                                //SocialModel socialModel = contact.getSocialModel();
                                hasWhatsapp = true;

                                socialModel.setWhatsappLink(note);

                            } else if (ClipboardType.isTelegram(note)) {
                                //SocialModel socialModel = contact.getSocialModel();
                                //hasTelegram = true;

                                //socialModel.setTelegramLink(note);

                                if (contact.getListOfContactInfo() == null) {
                                    contact.addNote(note);
                                } else {
                                    boolean checkS = false;
                                    for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                        if (contactInfo.value.equalsIgnoreCase(note)) {
                                            checkS = true;
                                            break;
                                        }
                                    }
                                    if (!checkS)
                                        contact.addNote(note);

                                }

                            } else if (note.toString().contains("skype.com") || note.toString().contains("https://www.skype.com") || note.contains("Skype: ")) {
                                //SocialModel socialModel = contact.getSocialModel();
                                if (note.contains("Skype: ")) {
                                    hasSkype = true;
                                    String username = note.substring(7, note.length());
                                    socialModel.setSkypeLink(username);
                                } else {
                                    hasSkype = true;
                                    String username = note.substring(note.indexOf(".com") + 5, note.length());
                                    if (username.charAt(username.length() - 1) == '/')
                                        username = username.substring(0, username.length() - 1);

                                    socialModel.setSkypeLink(username);
                                }

                            } else if (ClipboardType.isYoutube(note)) {

                                if (note.contains("user") || note.contains("channel")) {
                                    if (socialModel != null) {
                                        if (socialModel.getYoutubeLink() == null || socialModel.getYoutubeLink().isEmpty()) {
                                            socialModel.setYoutubeLink(note);
                                            hasYoutube = true;
                                        } else {
                                            if (contact.getListOfContactInfo() == null) {
                                                contact.addNote(note);
                                            } else {
                                                boolean checkS = false;
                                                for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                                    if (contactInfo.value.equalsIgnoreCase(note)) {
                                                        checkS = true;
                                                        break;
                                                    } else if (note.contains("?")) {

                                                        int ind = note.indexOf('?');
                                                        if (ind != -1) {

                                                            if (contactInfo.value.equalsIgnoreCase(note.substring(0, ind))) {

                                                                contactsService.updateNote(contact.getIdContact(), note, note.substring(0, ind));
                                                                checkS = true;
                                                                break;
                                                            }


                                                        }


                                                    }
                                                }
                                                if (!checkS)
                                                    contact.addNote(note);

                                            }
                                        }
                                    }
                                } else {
                                    if (contact.getListOfContactInfo() == null) {
                                        contact.addNote(note);
                                    } else {
                                        boolean checkS = false;
                                        for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                            if (contactInfo.value.equalsIgnoreCase(note)) {
                                                checkS = true;
                                                break;
                                            }
                                        }
                                        if (!checkS)
                                            contact.addNote(note);

                                    }
                                }
                            } else if (ClipboardType.isTwitter(note)) {


                                if (socialModel != null) {
                                    if (socialModel.getTwitterLink() == null || socialModel.getTwitterLink().isEmpty()) {
                                        socialModel.setTwitterLink(note);
                                        hasTwitter = true;
                                    } else {
                                        if (contact.getListOfContactInfo() == null) {
                                            contact.addNote(note);
                                        } else {
                                            boolean checkS = false;
                                            for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                                if (contactInfo.value.equalsIgnoreCase(note)) {
                                                    checkS = true;
                                                    break;
                                                }
                                            }
                                            if (!checkS)
                                                contact.addNote(note);

                                        }
                                    }
                                }
                            } else if (ClipboardType.isMedium(note)) {


                                if (note.contains("com/@")) {
                                    if (socialModel != null) {
                                        if (socialModel.getMediumLink() == null || socialModel.getMediumLink().isEmpty()) {
                                            socialModel.setMediumLink(note);
                                            hasMedium = true;
                                        } else {
                                            if (contact.getListOfContactInfo() == null) {
                                                contact.addNote(note);
                                            } else {
                                                boolean checkS = false;
                                                for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                                    if (contactInfo.value.equalsIgnoreCase(note)) {
                                                        checkS = true;
                                                        break;
                                                    }
                                                }
                                                if (!checkS)
                                                    contact.addNote(note);

                                            }
                                        }
                                    }
                                } else {
                                    if (contact.getListOfContactInfo() == null) {
                                        contact.addNote(note);
                                    } else {
                                        boolean checkS = false;
                                        for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                            if (contactInfo.value.equalsIgnoreCase(note)) {
                                                checkS = true;
                                                break;
                                            }
                                        }
                                        if (!checkS)
                                            contact.addNote(note);

                                    }
                                }

                            } else {
                                boolean checkS = false;
                                for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                    if (contactInfo.value.equalsIgnoreCase(note)) {
                                        checkS = true;
                                        break;
                                    }
                                }
                                if (!checkS) {
                                    contact.addNote(note);
                                    showUpdate = true;
                                    if (ClipboardType.isWeb(note)) {
                                        listUpdatingData.add(new DataUpdate(note, ClipboardEnum.WEB));
                                    } else {
                                        listUpdatingData.add(new DataUpdate(note, ClipboardEnum.NOTE));
                                    }
                                }
                            }
                        }
                    }
                }
                //realm.commitTransaction();


                Cursor notesC = contactsService.getCursorByType(ContactsService.TYPE_CURSOR.NOTE, contact.getIdContact());

                ArrayList<String> listInfoOfContact = new ArrayList<>();
                ArrayList<String> listNotes = new ArrayList<>();


                for (int i = 0; i < contact.getListOfContactInfo().size(); i++) {
                    listInfoOfContact.add(contact.getListOfContactInfo().get(i).value);
                }

                if (notesC != null && notesC.getCount() > 0) {
                    while (notesC.moveToNext()) {
                        String note = notesC.getString(notesC.getColumnIndex(ContactsContract.CommonDataKinds.Note.NOTE));
                        listNotes.add(note);
                        for (String str : listInfoOfContact) {
                            if (str.equals(note)) {
                                listInfoOfContact.remove(str);
                                break;
                            }
                        }
                    }
                }


                if (listInfoOfContact != null && listInfoOfContact.size() > 0) {
                    for (int i = 0; i < contact.getListOfContactInfo().size(); i++) {
                        for (String str : listInfoOfContact) {
                            if (contact.getListOfContactInfo().get(i).value.equals(str) && contact.getListOfContactInfo().get(i).type.equals("note")) {
                                contact.listOfContactInfo.remove(contact.getListOfContactInfo().get(i));
                                showUpdate = true;
                                i--;
                                break;
                            }
                        }
                    }
                }


                //realm.beginTransaction();
                if (contact.hasFacebook != hasFacebook) {
                    showUpdate = true;
                    contact.hasFacebook = hasFacebook;
                }
                if (contact.hasVk != hasVk) {
                    showUpdate = true;
                    contact.hasVk = hasVk;
                }
                if (contact.hasInst != hasInst) {
                    showUpdate = true;
                    contact.hasInst = hasInst;
                }
                if (contact.hasLinked != hasLinked) {
                    showUpdate = true;
                    contact.hasLinked = hasLinked;
                }
                if (contact.hasTwitter != hasTwitter) {
                    showUpdate = true;
                    contact.hasTwitter = hasTwitter;
                }
                if (contact.hasYoutube != hasYoutube) {
                    showUpdate = true;
                    contact.hasYoutube = hasYoutube;
                }
                if (contact.hasMedium != hasMedium) {
                    showUpdate = true;
                    contact.hasMedium = hasMedium;
                }

                if (contact.getSocialModel() != null && (contact.getSocialModel().getFacebookLink() == null || contact.getSocialModel().getFacebookLink().isEmpty()) && socialModel.getFacebookLink() != null) {
                    listUpdatingData.add(new DataUpdate(socialModel.getFacebookLink(), ClipboardEnum.FACEBOOK));
                }
                if (contact.getSocialModel() != null && (contact.getSocialModel().getInstagramLink() == null || contact.getSocialModel().getInstagramLink().isEmpty()) && socialModel.getInstagramLink() != null) {
                    listUpdatingData.add(new DataUpdate(socialModel.getInstagramLink(), ClipboardEnum.INSTAGRAM));
                }
                if (contact.getSocialModel() != null && (contact.getSocialModel().getLinkedInLink() == null || contact.getSocialModel().getLinkedInLink().isEmpty()) && socialModel.getLinkedInLink() != null) {
                    listUpdatingData.add(new DataUpdate(socialModel.getLinkedInLink(), ClipboardEnum.LINKEDIN));
                }
                if (contact.getSocialModel() != null && (contact.getSocialModel().getVkLink() == null || contact.getSocialModel().getVkLink().isEmpty()) && socialModel.getVkLink() != null) {
                    listUpdatingData.add(new DataUpdate(socialModel.getVkLink(), ClipboardEnum.VK));
                }
                if (contact.getSocialModel() != null && (contact.getSocialModel().getTwitterLink() == null || contact.getSocialModel().getTwitterLink().isEmpty()) && socialModel.getTwitterLink() != null) {
                    listUpdatingData.add(new DataUpdate(socialModel.getTwitterLink(), ClipboardEnum.TWITTER));
                }
                if (contact.getSocialModel() != null && (contact.getSocialModel().getYoutubeLink() == null || contact.getSocialModel().getYoutubeLink().isEmpty()) && socialModel.getYoutubeLink() != null) {
                    listUpdatingData.add(new DataUpdate(socialModel.getYoutubeLink(), ClipboardEnum.YOUTUBE));
                }
                if (contact.getSocialModel() != null && (contact.getSocialModel().getMediumLink() == null || contact.getSocialModel().getMediumLink().isEmpty()) && socialModel.getMediumLink() != null) {
                    listUpdatingData.add(new DataUpdate(socialModel.getMediumLink(), ClipboardEnum.MEDIUM));
                }

                if (contact.getSocialModel() != null && ((contact.getSocialModel().getSkypeLink() == null || contact.getSocialModel().getSkypeLink().isEmpty()) || ((contact.getSocialModel().getSkypeLink() != null && !contact.getSocialModel().getSkypeLink().isEmpty()) && (socialModel.getSkypeLink() != null && !socialModel.getSkypeLink().isEmpty()) && !contact.getSocialModel().getSkypeLink().equalsIgnoreCase(socialModel.getSkypeLink()))) && socialModel.getSkypeLink() != null) {
                    listUpdatingData.add(new DataUpdate(socialModel.getSkypeLink(), ClipboardEnum.SKYPE));
                    showUpdate = true;
                }

                if (hasTelegram && contact.getSocialModel() != null && contact.getSocialModel().getTelegramLink() != null && !contact.getSocialModel().getTelegramLink().isEmpty()) {
                    socialModel.setTelegramLink(contact.getSocialModel().getTelegramLink());
                } else {
                    if (!hasTelegram) {
                        hasTelegram = false;
                        socialModel.setTelegramLink(null);
                    }
                }

                if (hasViber && contact.getSocialModel() != null && contact.getSocialModel().getViberLink() != null && !contact.getSocialModel().getViberLink().isEmpty()) {
                    socialModel.setViberLink(contact.getSocialModel().getViberLink());
                } else {
                    if (!hasViber) {
                        hasViber = false;
                        socialModel.setViberLink(null);
                    }
                }

                if (hasWhatsapp && contact.getSocialModel() != null && contact.getSocialModel().getWhatsappLink() != null && !contact.getSocialModel().getWhatsappLink().isEmpty()) {
                    socialModel.setWhatsappLink(contact.getSocialModel().getWhatsappLink());
                } else {
                    if (!hasWhatsapp) {
                        hasWhatsapp = false;
                        socialModel.setWhatsappLink(null);
                    }
                }

                /*if (hasSkype && contact.getSocialModel() != null && contact.getSocialModel().getSkypeLink() != null && !contact.getSocialModel().getSkypeLink().isEmpty()) {
                    socialModel.setSkypeLink(contact.getSocialModel().getSkypeLink());
                } else {
                    if (!hasSkype) {
                        hasSkype = false;
                        socialModel.setSkypeLink(null);
                    }
                }*/

                contact.setSocialModel(socialModel);
                //realm.commitTransaction();


                //realm.beginTransaction();
                contact.setListOfHashtags(hashList);
                //realm.commitTransaction();

                if (contact.getListOfHashtags().size() != listHashOfContacts.size()) {
                    //
                    showUpdate = true;
                } else {
                    for (HashTag h1 : listHashOfContacts) {
                        boolean check = false;
                        for (HashTag h2 : contact.getListOfHashtags()) {
                            if (h1.getHashTagValue().equalsIgnoreCase(h2.getHashTagValue())) {
                                check = true;
                                break;
                            }
                        }
                        if (!check) {
                            showUpdate = true;
                            break;
                        }
                    }
                }

                if (listHashOfContacts == null && contact.getListOfHashtags() != null) {
                    String str = "";
                    for (int i = 0; i < contact.getListOfHashtags().size(); i++) {
                        str += contact.getListOfHashtags().get(i).getHashTagValue() + " ";
                    }
                    listUpdatingData.add(new DataUpdate(str, ClipboardEnum.HASHTAG));
                } else if (listHashOfContacts != null && contact.getListOfHashtags() != null) {
                    String hst = "";
                    for (int i = 0; i < contact.getListOfHashtags().size(); i++) {
                        boolean checkH = false;
                        for (HashTag h : listHashOfContacts) {
                            if (contact.getListOfHashtags().get(i).getHashTagValue().equalsIgnoreCase(h.getHashTagValue())) {
                                checkH = true;
                                break;
                            }
                        }
                        if (!checkH)
                            hst += contact.getListOfHashtags().get(i).getHashTagValue() + " ";
                    }
                    if (hst.length() > 0)
                        listUpdatingData.add(new DataUpdate(hst, ClipboardEnum.HASHTAG));
                }


                String photoURL = ContactsService.getPhotoURI(getContext(), contact.getIdContact());

                if ((photoURL == null && contact.getPhotoURL() != null) || (photoURL != null && contact.getPhotoURL() == null) || (photoURL != null && contact.getPhotoURL() != null && !photoURL.equals(contact.getPhotoURL()))) {
                    showUpdate = true;


                    //realm.beginTransaction();
                    contact.setPhotoURL(photoURL);
                    //realm.commitTransaction();

                    int colotC = contact.color;
                    String nameContact = contact.getName();
                    try {
                            /*getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {


                                    try {
                                        profilePopUp.findViewById(R.id.profilePopupInitials).setVisibility(View.GONE);
                                        profilePopUp.findViewById(R.id.profilePopupAvatar).setBackgroundColor(Color.TRANSPARENT);
                                        ((ImageView) profilePopUp.findViewById(R.id.profilePopupAvatar)).setImageURI(Uri.parse(photoURL));
                                        if (((BitmapDrawable) ((ImageView) profilePopUp.findViewById(R.id.profilePopupAvatar)).getDrawable()).getBitmap() == null) {
                                            GradientDrawable circle = (GradientDrawable) ContextCompat.getDrawable(mainView.getContext(), R.drawable.blue_circle).mutate();
                                            circle.setColor(colotC);
                                            profilePopUp.findViewById(R.id.profilePopupAvatar).setBackground(circle);
                                            ((ImageView) profilePopUp.findViewById(R.id.profilePopupAvatar)).setImageDrawable(null);

                                            String initials = "";
                                            String names[] = nameContact.split("\\s+");

                                            for (String namePart : names)
                                                initials += namePart.charAt(0);

                                            profilePopUp.findViewById(R.id.profilePopupInitials).setVisibility(View.VISIBLE);
                                            ((TextView) profilePopUp.findViewById(R.id.profilePopupInitials)).setText(initials);
                                        }
                                    } catch (Exception e) {
                                        GradientDrawable circle = (GradientDrawable) ContextCompat.getDrawable(mainView.getContext(), R.drawable.blue_circle).mutate();
                                        circle.setColor(colotC);
                                        profilePopUp.findViewById(R.id.profilePopupAvatar).setBackground(circle);
                                        ((ImageView) profilePopUp.findViewById(R.id.profilePopupAvatar)).setImageDrawable(null);

                                        String initials = "";
                                        String names[] = nameContact.split("\\s+");

                                        for (String namePart : names)
                                            initials += namePart.charAt(0);

                                        profilePopUp.findViewById(R.id.profilePopupInitials).setVisibility(View.VISIBLE);
                                        ((TextView) profilePopUp.findViewById(R.id.profilePopupInitials)).setText(initials);
                                    }


                                }
                            });*/
                    } catch (NullPointerException e) {

                    }


                }

                boolean finalShowUpdate = showUpdate;
                try {

                        /*getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Contact contact = ContactCacheService.getContactById(id);


                                if(contact.listOfContactInfo != null) {
                                    boolean checkMail = false;
                                    ((ImageView) profilePopUp.findViewById(R.id.emailImg)).setImageResource(R.drawable.icn_bottombar_emails);
                                    for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                        if (contactInfo.type.toLowerCase().compareTo("email") == 0) {
                                            checkMail = true;
                                            ((ImageView) profilePopUp.findViewById(R.id.emailImg)).setImageResource(R.drawable.icn_bottombar_emails_blue);
                                            profilePopUp.findViewById(R.id.emailImg).setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                                            "mailto", contactInfo.value, null));
                                                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
                                                    emailIntent.putExtra(Intent.EXTRA_TEXT, "");
                                                    startActivity(Intent.createChooser(emailIntent, "Send email"));
                                                }
                                            });
                                        }
                                        if(!checkMail){
                                            profilePopUp.findViewById(R.id.emailImg).setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {

                                                }
                                            });
                                        }
                                    }
                                }
                                //=============================================== SOCIALS


                                //===============================================

                                LinearLayout containerHashTags = (LinearLayout) profilePopUp.findViewById(R.id.containerHashTags);
                                if (containerHashTags.getChildCount() > 0)
                                    containerHashTags.removeAllViews();
                                for (HashTag hashTag : contact.getListOfHashtags()) {
                                    TextView text = new TextView(getActivity());
                                    text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                                    DisplayMetrics metrics = mainView.getContext().getResources().getDisplayMetrics();
                                    text.setLayoutParams(new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                    text.setTextColor(getResources().getColor(R.color.colorPrimary));
                                    text.setText(hashTag.getHashTagValue() + " ");
//            text.setOnClickListener(v -> contactAdapter.searchByHashTagValue(hashTag.getHashTagValue()));
                                    containerHashTags.addView(text);
                                }

                                if (contact.getListOfHashtags().size() == 0) {
                                    TextView text = new TextView(getActivity());
                                    text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                                    DisplayMetrics metrics = mainView.getContext().getResources().getDisplayMetrics();
                                    text.setLayoutParams(new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                    text.setTextColor(getResources().getColor(R.color.gray));
                                    text.setText("hashtags");
                                    text.setOnClickListener(v -> {
                                        containerHashTags.removeAllViews();
                                        showPopupUserHashtags(contact);
                                    });
                                    containerHashTags.addView(text);
                                }


                                profilePopUp.findViewById(R.id.arrowShowHashtags).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (socialPopup != null) {
                                            if (socialPopup.getVisibility() == View.VISIBLE)
                                                return;
                                            showPopupUserHashtags(contact);
                                        } else
                                            showPopupUserHashtags(contact);
                                    }
                                });

                                initIconColor(contact, profilePopUp);

                                if (finalShowUpdate) {

                                }
                            }
                        });*/
                } catch (NullPointerException e) {

                }

                try {
                    if (finalShowUpdate) {


/*

                            AlertDialog alertDialog = new AlertDialog.Builder(((ProfileFragment) getParentFragment()).getActivity()).create();

                            alertDialog.setTitle("Updating");

                            alertDialog.setMessage("Some of data was updated via address book. Do u want to make changes inside app also?");

                            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int id) {
                                    updateContactAfterCheckSynk(contact);
                                    //...

                                }
                            });

                            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Create New", new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int id) {

                                    if (profilePopUp != null)
                                        profilePopUp.setVisibility(View.GONE);

                                    alertDialog.hide();
                                    ContactsFragment.createContact = true;
                                    ArrayList<Contact> contactExtract = new ArrayList<>();
                                                        *//*contact.setId(-1);
                                                        contact.setIdContact(null);*//*

                                    if (contact.getListOfContactInfo() != null && !contact.getListOfContactInfo().isEmpty()) {
                                        for (ContactInfo i : contact.getListOfContactInfo()) {
                                            i.generateNewId();
                                        }
                                    }
                                    contactExtract.add(contact);
                                    //Contact contact1 = new Contact();
                                    //contact1.setName("");
                                    //contactExtract.add(contact1);
                                    //contactExtract.add(contact1);
                                    android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                    android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                    fragmentTransaction.replace(R.id.main_content, CreateFragment.newInstance(contactExtract)).addToBackStack("contacts").commit();
                                    //...

                                }
                            });

                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "No", new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int id) {
                                    alertDialog.hide();
                                    //...

                                }
                            });

                            alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                                @Override
                                public void onShow(DialogInterface dialog) {
                                    Button negButton = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_NEGATIVE);

                                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.WRAP_CONTENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT
                                    );
                                    params.setMargins(0, 0, 50, 0);
                                    negButton.setLayoutParams(params);
                                }
                            });

                           *//* if (stopThread) {

                                return;
                            }*//*

                            alertDialog.show();*/


                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (finalShowUpdate) {
                                        /*profilePopUp.findViewById(R.id.updateTextView).setVisibility(View.VISIBLE);
                                        EventBus.getDefault().post(new UpdateFile());*/

                                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();

                                    alertDialog.setTitle("Updating");

                                    alertDialog.setMessage("Some of data was updated via address book. Do u want to make changes inside app also?");

                                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int id) {
                                            updateContactAfterCheckSynk(contact);
                                            //...

                                        }
                                    });

                                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Create New", new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int id) {

                                            if (profilePopUp != null)
                                                profilePopUp.setVisibility(View.GONE);

                                            alertDialog.hide();
                                            ContactsFragment.createContact = true;
                                            ArrayList<Contact> contactExtract = new ArrayList<>();
                                                        /*contact.setId(-1);
                                                        contact.setIdContact(null);*/

                                            if (contact.getListOfContactInfo() != null && !contact.getListOfContactInfo().isEmpty()) {
                                                for (ContactInfo i : contact.getListOfContactInfo()) {
                                                    i.generateNewId();
                                                }
                                            }
                                            contactExtract.add(contact);
                                            //Contact contact1 = new Contact();
                                            //contact1.setName("");
                                            //contactExtract.add(contact1);
                                            //contactExtract.add(contact1);
                                            android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                            fragmentTransaction.replace(R.id.main_content, CreateFragment.newInstance(contactExtract)).addToBackStack("create").commit();
                                            //...

                                        }
                                    });

                                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "No", new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int id) {
                                            alertDialog.hide();
                                            //...

                                        }
                                    });

                                    alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                                        @Override
                                        public void onShow(DialogInterface dialog) {
                                            Button negButton = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_NEGATIVE);

                                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                                    LinearLayout.LayoutParams.WRAP_CONTENT
                                            );
                                            params.setMargins(0, 0, 50, 0);
                                            negButton.setLayoutParams(params);
                                        }
                                    });

                           /* if (stopThread) {

                                return;
                            }*/

                                    //alertDialog.show();
                                    openDialodUpdate(listUpdatingData, contact);

                                }
                            }
                        });

                           /* try {
                                Thread.sleep(2500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }*/

                           /* getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (finalShowUpdate) {
                                        profilePopUp.findViewById(R.id.updateTextView).setVisibility(View.GONE);
                                    }
                                }
                            });*/
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

            }
        }).start();


    }


    public void openDialodUpdate(ArrayList<DataUpdate> list, Contact contact) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View view = getLayoutInflater().inflate(R.layout.doalog_update, null);
        //list = new ArrayList<>();
        final float scale = getResources().getDisplayMetrics().density;
       /* listUpdatingData.add(new DataUpdate());
        listUpdatingData.add(new DataUpdate());
        listUpdatingData.add(new DataUpdate());
        listUpdatingData.add(new DataUpdate());*/
        builder.setView(view);
        AlertDialog alertDialog = builder.create();

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycleUpdate);
        TextView countValues = view.findViewById(R.id.textCount);
        countValues.setText(list.size() + " new fields added");
        ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
        countValues.setVisibility(View.VISIBLE);
        if (list.size() == 0) {
            params.height = 0;
            countValues.setVisibility(View.GONE);
        } else if (list.size() == 1) {
            params.height = (int) ((int) 55 * scale);
        } else if (list.size() == 2) {
            params.height = (int) ((int) 110 * scale);
        } else {
            params.height = (int) ((int) 165 * scale);
        }
        recyclerView.setLayoutParams(params);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        DialogAdapter dialogAdapter = new DialogAdapter(list, getContext());
        recyclerView.setAdapter(dialogAdapter);

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                updateContactAfterCheckSynk(contact);
                //...

            }
        });

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Create New", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {

                if (profilePopUp != null)
                    profilePopUp.setVisibility(View.GONE);

                alertDialog.hide();
                ContactsFragment.createContact = true;
                ArrayList<Contact> contactExtract = new ArrayList<>();
                contact.setId(-1);
                contact.setIdContact(null);
                if (contact.getListOfContactInfo() != null && !contact.getListOfContactInfo().isEmpty()) {
                    for (ContactInfo i : contact.getListOfContactInfo()) {
                        i.generateNewId();
                    }
                }
                contactExtract.add(contact);
                //Contact contact1 = new Contact();
                //contact1.setName("");
                //contactExtract.add(contact1);
                //contactExtract.add(contact1);
                android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_content, CreateFragment.newInstance(contactExtract)).addToBackStack("create").commit();
                //...

            }
        });

        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "No", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
               /* alertDialog.hide();
                //...*/

            }
        });

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button negButton = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_NEGATIVE);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(0, 0, 50, 0);
                negButton.setLayoutParams(params);
            }
        });

        alertDialog.show();

    }


    public void updateContactAfterCheckSynk(Contact c) {
        long id = c.getId();


        new Thread(new Runnable() {
            @Override
            public void run() {

                Realm realm = Realm.getDefaultInstance(); //-

                Contact contact = ContactCacheService.getContactByIdSynk(id, realm);



                Cursor c = contactsService.getCursorByType(ContactsService.TYPE_CURSOR.CONTACT_NAME, contact.getIdContact());
                if (c == null || c.getCount() == 0) {
                    /*String idN = contactsService.getIdContactByName(contact.getName());
                    if (idN == null) {
                        return;
                    } else {
                        realm.beginTransaction();
                        contact.setIdContact(idN);
                        realm.commitTransaction();
                    }*/
                    return;
                }


                Cursor organizationCursor = contactsService.getCursorByType(ContactsService.TYPE_CURSOR.ORGANIZATION, contact.getIdContact());

                Realm realm2 = Realm.getDefaultInstance();

                ArrayList<Contact> listOFCompanies = ContactCacheService.getCompaniesRealm(realm2);

                boolean showUpdate = false;

                String companyOfContact = null;
                if (contact.getCompany() != null)
                    companyOfContact = contact.getCompany();

                String positionContact = null;

                if (contact.getCompanyPossition() != null)
                    positionContact = contact.getCompanyPossition();

                realm.beginTransaction();
                contact.setCompanyPossition(null);
                realm.commitTransaction();

                int nameHash = contact.getName().hashCode();

                boolean found = false;

                while (organizationCursor != null && organizationCursor.moveToNext()) {
                    String orgName = organizationCursor.getString(organizationCursor.getColumnIndex(ContactsContract.CommonDataKinds.Organization.DATA));
                    String companyPossition = organizationCursor.getString(organizationCursor.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TITLE));



                    realm.beginTransaction();
                    contact.setCompanyPossition(companyPossition);

                    realm.commitTransaction();

                    if (orgName != null && !orgName.isEmpty()) {



                        String oldCompany = null;
                        if (contact.getCompany() != null && !contact.getCompany().isEmpty() && !contact.getCompany().trim().equalsIgnoreCase(orgName.trim()))
                            oldCompany = contact.getCompany();

                        realm.beginTransaction();
                        contact.setCompany(orgName.trim());
                        realm.commitTransaction();

                        for (Contact searchCompanyContact : listOFCompanies) {
                            if (searchCompanyContact.isValid() && searchCompanyContact.getName().equalsIgnoreCase(orgName.trim())) {
                                if (oldCompany != null) {
                                    for (Contact comp : listOFCompanies) {
                                        if (comp.isValid() && comp.getName().equalsIgnoreCase(oldCompany.trim())) {
                                            if (comp.listOfContacts.size() == 1) {
                                                EventBus.getDefault().post(new RemoveContact(comp.getId()));
                                            }
                                            ContactCacheService.removeContactFromCompany(comp, contact);
                                            break;
                                        }
                                    }
                                }

                                if (!searchCompanyContact.listOfContacts.contains(contact)) {
                                    if (searchCompanyContact.getName().equals("1337")) {

                                    }

                                    realm.beginTransaction();
                                    searchCompanyContact.listOfContacts.add(contact);
                                    realm.commitTransaction();
                                }
                                found = true;
                            }
                        }

                        if (!found) {
                            Date date1 = contact.getDateCreate();
                            Calendar cal1 = Calendar.getInstance();
                            cal1.setTime(date1);
                            Time time1 = contactsService.getRandomDate();
                            time1.setHours(cal1.get(Calendar.HOUR_OF_DAY));
                            time1.setMinutes(cal1.get(Calendar.MINUTE));
                            time1.setSeconds(cal1.get(Calendar.SECOND));
                            Contact companyContact = new Contact(0, orgName.trim(), null, false, true, true, time1.toString(), null, date1);
                            companyContact.time = contact.time.toString();
                            companyContact.color = Color.rgb(Math.abs(nameHash * 28439) % 255, Math.abs(nameHash * 211239) % 255, Math.abs(nameHash * 42368) % 255);
                            if (oldCompany != null) {
                                for (Contact comp : listOFCompanies) {
                                    if (comp.isValid() && comp.getName().equalsIgnoreCase(oldCompany.trim())) {

                                        if (comp.listOfContacts.size() == 1) {
                                            /*ContactAdapter cont = ContactAdapter.contactAd;
                                            cont.removeContactById(comp);*/
                                           /* getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    contactAdapter.removeContactById(comp);
                                                    contactAdapter.notifyDataSetChanged();
                                                }
                                            });*/
                                            //ContactsFragment.UPD_ALL = true;
                                            EventBus.getDefault().post(new RemoveContact(comp.getId()));
                                        }
                                        ContactCacheService.removeContactFromCompany(comp, contact);
                                        break;
                                    }
                                }
                            }
                            companyContact.listOfContacts.add(contact);
                            ContactCacheService.updateCompany(companyContact);
                            listOFCompanies.add(ContactCacheService.getCompany(companyContact.getName()));
                            //ContactAdapter cont = ContactAdapter.contactAd;
                            //cont.addContact(companyContact);
                            //if(typeEnum.equals(FillDataEnums.PROFILE) || typeEnum.equals(FillDataEnums.PREVIEW)) {
                            EventBus.getDefault().post(new AddContact(companyContact.getId()));
                            ContactsFragment.UPD_ALL = true;
                           /* }else if(typeEnum.equals(FillDataEnums.NEW)){
                                companyNewReturn = companyContact;
                            }*/
                        }
                    }
                }
                boolean checkNull = false;
                if (organizationCursor != null && organizationCursor.getCount() > 0) {
                    organizationCursor.moveToFirst();
                    do {
                        if (organizationCursor.getString(organizationCursor.getColumnIndex(ContactsContract.CommonDataKinds.Organization.DATA)) != null && !organizationCursor.getString(organizationCursor.getColumnIndex(ContactsContract.CommonDataKinds.Organization.DATA)).equals("")) {
                            checkNull = true;
                            break;
                        }
                    } while (organizationCursor.moveToNext());

                }

                if (!checkNull) {
                    if (contact.getCompany() != null) {

                        for (Contact comp : listOFCompanies) {
                            if (comp.isValid() && comp.getName().equalsIgnoreCase(contact.getCompany())) {

                                if (comp.listOfContacts.size() == 1) {
                                    //ContactAdapter cont = ContactAdapter.contactAd;
                                    /*getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            contactAdapter.removeContactById(comp);
                                            contactAdapter.notifyDataSetChanged();
                                        }
                                    });*/
                                    EventBus.getDefault().post(new RemoveContact(comp.getId()));


                                    //cont.notifyDataSetChanged();
                                    //ContactsFragment.UPD_ALL = true;
                                }

                                ContactCacheService.removeContactFromCompany(comp, contact);
                                realm.beginTransaction();
                                contact.setCompany(null);
                                realm.commitTransaction();
                                break;
                            }
                        }
                    }
                }

                realm2.close();


                if ((companyOfContact == null && contact.getCompany() != null) || (companyOfContact != null && contact.getCompany() == null) || (companyOfContact != null && contact.getCompany() != null && !companyOfContact.equals(contact.getCompany()))) {
                    showUpdate = true;
                    if (contact.getCompany() != null) {
                        String nameC = contact.getCompany();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ((TextView) profilePopUp.findViewById(R.id.company_title)).setText(nameC);

                            }
                        });

                    } else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                               /* ((TextView) profilePopUp.findViewById(R.id.company_title)).setText("");

                                ((TextView) profilePopUp.findViewById(R.id.company_title)).setHint("Company");

                                profilePopUp.findViewById(R.id.company_title).setOnClickListener(v -> {

                                            if (((TextView) profilePopUp.findViewById(R.id.company_title)).getText() == "")
                                                showCompanyAddPopup(ContactCacheService.getContactById(id), null, false);
                                        }
                                );*/
                            }
                        });
                    }
                }

                if ((positionContact == null && contact.getCompanyPossition() != null) || (positionContact != null && contact.getCompanyPossition() == null) || (positionContact != null && contact.getCompanyPossition() != null && !positionContact.equals(contact.getCompanyPossition()))) {
                    //contact.setCompanyPossition(positionContact);
                    showUpdate = true;
                    if (contact.getCompanyPossition() != null) {
                        String pos = contact.getCompanyPossition();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {


                                profilePopUp.findViewById(R.id.company).setVisibility(View.VISIBLE);
                                ((TextView) profilePopUp.findViewById(R.id.company)).setText(pos);


                            }
                        });
                    } else {

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ((TextView) profilePopUp.findViewById(R.id.company)).setVisibility(View.VISIBLE);
                                ((TextView) profilePopUp.findViewById(R.id.company)).setText("");
                                ((TextView) profilePopUp.findViewById(R.id.company)).setHint("Position");
                            }
                        });


                    }
                }


                String contactName = ContactsService.getDisplayName(getContext(), contact.getIdContact());
                if (contactName == null || contactName.equals("")) {

                } else if (!contact.getName().equals(contactName)) {
                    realm.beginTransaction();
                    contact.setName(contactName);
                    realm.commitTransaction();
                    showUpdate = true;

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((TextView) profilePopUp.findViewById(R.id.name)).setText(contactName);
                        }
                    });
                }


                //phones


                ArrayList<String> listPhones = new ArrayList<>();
                for (int i = 0; i < contact.getListOfContactInfo().size(); i++) {
                    if (contact.getListOfContactInfo().get(i).type.equalsIgnoreCase("phone")) {
                        listPhones.add(contact.getListOfContactInfo().get(i).value);
                    }
                }


                ArrayList<String> phone = contactsService.getContactPhones(contact.getIdContact());

                realm.beginTransaction();
                if (phone != null && phone.size() > 0) {
                    for (int i = 0; i < phone.size(); i++) {

                        String phonee = phone.get(i).trim();
                        phonee = phonee.replaceAll("[\\.\\s\\-\\+\\(\\)]", "");

                        boolean phoneFind = false;
                        for (int j = 0; j < listPhones.size(); j++) {
                            String phone1 = listPhones.get(j);
                            phone1 = phone1.replaceAll("[\\.\\s\\-\\+\\(\\)]", "");
                            if (phone1.equals(phonee)) {
                                phoneFind = true;
                                //listPhones.remove(j);
                                break;
                            }
                        }

                        if (!phoneFind) {
                            showUpdate = true;
                            contact.addPhone(phone.get(i));
                        }
                    }
                } else if (listPhones.isEmpty()) {
                    contact.addPhone("+000000000000");
                    contactsService.addPhoneToContact(contact.getIdContact(), "+000000000000", contact.getName());
                    //setName(contactName.trim());
                }

                if (listPhones != null && listPhones.size() > 0) {
                    for (int i = 0; i < contact.getListOfContactInfo().size(); i++) {
                        for (String str : listPhones) {
                            if (contact.getListOfContactInfo().get(i).value.equals(str)) {
                                //contact.listOfContactInfo.remove(contact.getListOfContactInfo().get(i));
                                //showUpdate = true;
                                //i--;
                                break;
                            }
                        }
                    }
                }


                realm.commitTransaction();


                //email
                realm.beginTransaction();
                ArrayList<String> listEmail = new ArrayList<>();
                if (contact.getListOfContactInfo() != null) {
                    for (int i = 0; i < contact.getListOfContactInfo().size(); i++) {
                        if (contact.getListOfContactInfo().get(i).type.equalsIgnoreCase("email")) {
                            listEmail.add(contact.getListOfContactInfo().get(i).value);
                        }
                    }
                }

                ArrayList<String> addr = contactsService.getContactEmails(contact.getIdContact());
                for (String str : addr) {
                    boolean emailfind = false;
                    for (int i = 0; i < listEmail.size(); i++) {
                        if (str.equalsIgnoreCase(listEmail.get(i))) {
                            emailfind = true;
                            listEmail.remove(i);
                            break;
                        }
                    }
                    if (!emailfind) {
                        showUpdate = true;
                        contact.addEmail(str);
                    }
                }

                if (listEmail != null && listEmail.size() > 0) {
                    for (int i = 0; i < contact.getListOfContactInfo().size(); i++) {
                        for (String str : listEmail) {
                            if (contact.getListOfContactInfo().get(i).value.equals(str)) {
                                contact.listOfContactInfo.remove(contact.getListOfContactInfo().get(i));
                                showUpdate = true;
                                i--;
                                break;
                            }
                        }
                    }
                }

                realm.commitTransaction();
                //


                //address


                realm.beginTransaction();


                ArrayList<String> listAddress = new ArrayList<>();
                for (int i = 0; i < contact.getListOfContactInfo().size(); i++) {
                    if (contact.getListOfContactInfo().get(i).type.equalsIgnoreCase("address")) {
                        listAddress.add(contact.getListOfContactInfo().get(i).value);
                    }
                }

                Cursor adressCursor = contactsService.getCursorByType(ContactsService.TYPE_CURSOR.CONTACT_ADRESS, contact.getIdContact());
                while (adressCursor != null && adressCursor.moveToNext()) {
                    String adress = adressCursor.getString(adressCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
                    boolean findAddress = false;
                    for (String str : listAddress) {
                        if (str.equalsIgnoreCase(adress)) {
                            findAddress = true;
                            listAddress.remove(str);
                            break;
                        }
                    }
                    if (!findAddress) {
                        showUpdate = true;
                        contact.addAddress(adress);
                    }

                }

                if (listAddress != null && listAddress.size() > 0) {
                    for (int i = 0; i < contact.getListOfContactInfo().size(); i++) {
                        for (String str : listAddress) {
                            if (contact.getListOfContactInfo().get(i).value.equals(str)) {
                                contact.listOfContactInfo.remove(contact.getListOfContactInfo().get(i));
                                showUpdate = true;
                                i--;
                                break;
                            }
                        }
                    }
                }

                realm.commitTransaction();


                //hash
                ArrayList<String> listOfHash = new ArrayList<>();
                RealmList<HashTag> hashList = new RealmList<>();

                ArrayList<HashTag> listHashOfContacts = new ArrayList<>();
                if (contact.getListOfHashtags() != null)
                    listHashOfContacts.addAll(contact.getListOfHashtags());

                Cursor noteCursor = contactsService.getCursorByType(ContactsService.TYPE_CURSOR.NOTE, contact.getIdContact());

                realm.beginTransaction();
                SocialModel socialModel = realm.createObject(SocialModel.class);
                realm.commitTransaction();
                boolean hasFacebook = false;
                boolean hasInst = false;
                boolean hasLinked = false;
                boolean hasVk = false;
                boolean hasSkype = false;
                boolean hasTelegram = contact.hasTelegram;
                boolean hasViber = contact.hasViber;
                boolean hasWhatsapp = contact.hasWhatsapp;
                boolean hasYoutube = false;
                boolean hasTwitter = false;
                boolean hasMedium = false;
                realm.beginTransaction();
                if (noteCursor != null && noteCursor.getCount() > 0) {
                    while (noteCursor.moveToNext()) {
                        String note = noteCursor.getString(noteCursor.getColumnIndex(ContactsContract.CommonDataKinds.Note.NOTE));
                        if (note != null && note.length() > 0) {
                            if (note.charAt(0) == '#') {
                                if (!listOfHash.contains(note.toLowerCase().trim())) {
                                    HashTag hashtag = realm.createObject(HashTag.class);
                                    hashtag.setDate(new Date());
                                    hashtag.setHashTagValue(note.toLowerCase().trim());
                                    hashList.add(hashtag);
                                    listOfHash.add(note.toLowerCase().trim());
                                }


                            } else if (/*(note.length() > 26 && note.toString().substring(0, 26).equalsIgnoreCase("https://www.instagram.com/")) || (note.length() > 18 && note.toString().substring(0, 18).equalsIgnoreCase("www.instagram.com/") ||
                                    (note.length() > 14 && note.toString().substring(0, 14).equalsIgnoreCase("instagram.com/")) || (note.length() > 22 && note.toString().substring(0, 22).equalsIgnoreCase("https://instagram.com/")))*/ ClipboardType.isInsta(note)) {
                                //SocialModel socialModel = contact.getSocialModel();


                                String username = note;
                                if (username.contains("?utm")) {
                                    int ind = username.indexOf('?');
                                    if (ind != -1)
                                        username = username.substring(0, ind);

                                    contactsService.updateNote(contact.getIdContact(), note, username);
                                }

                                if (socialModel != null) {
                                    if (socialModel.getInstagramLink() == null || socialModel.getInstagramLink().isEmpty()) {
                                        socialModel.setInstagramLink(username);
                                        hasInst = true;
                                    } else {
                                        if (contact.getListOfContactInfo() == null) {
                                            contact.addNote(note);
                                        } else {
                                            boolean checkS = false;
                                            for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                                if (contactInfo.value.equalsIgnoreCase(note)) {
                                                    checkS = true;
                                                    break;
                                                }
                                            }
                                            if (!checkS)
                                                contact.addNote(note);

                                        }
                                    }
                                }
                            } else if (ClipboardType.isVk(note)) {
                                //SocialModel socialModel = contact.getSocialModel();


                                if (socialModel != null) {
                                    if (socialModel.getVkLink() == null || socialModel.getVkLink().isEmpty()) {
                                        socialModel.setVkLink(note);
                                        hasVk = true;
                                    } else {
                                        if (contact.getListOfContactInfo() == null) {
                                            contact.addNote(note);
                                        } else {
                                            boolean checkS = false;
                                            for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                                if (contactInfo.value.equalsIgnoreCase(note)) {
                                                    checkS = true;
                                                    break;
                                                }
                                            }
                                            if (!checkS)
                                                contact.addNote(note);

                                        }
                                    }
                                }

                            } else if (ClipboardType.isFacebook(note)) {
                                //SocialModel socialModel = contact.getSocialModel();


                                if (socialModel != null) {
                                    if (socialModel.getFacebookLink() == null || socialModel.getFacebookLink().isEmpty()) {
                                        socialModel.setFacebookLink(note);
                                        hasFacebook = true;
                                    } else {
                                        if (contact.getListOfContactInfo() == null) {
                                            contact.addNote(note);
                                        } else {
                                            boolean checkS = false;
                                            for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                                if (contactInfo.value.equalsIgnoreCase(note)) {
                                                    checkS = true;
                                                    break;
                                                }
                                            }
                                            if (!checkS)
                                                contact.addNote(note);

                                        }
                                    }
                                }

                            } else if (ClipboardType.isLinkedIn(note)) {
                                //SocialModel socialModel = contact.getSocialModel();


                                if (socialModel != null) {
                                    if (socialModel.getLinkedInLink() == null || socialModel.getLinkedInLink().isEmpty()) {
                                        socialModel.setLinkedInLink(note);
                                        hasLinked = true;
                                    } else {
                                        if (contact.getListOfContactInfo() == null) {
                                            contact.addNote(note);
                                        } else {
                                            boolean checkS = false;
                                            for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                                if (contactInfo.value.equalsIgnoreCase(note)) {
                                                    checkS = true;
                                                    break;
                                                }
                                            }
                                            if (!checkS)
                                                contact.addNote(note);

                                        }
                                    }
                                }

                            } else if (note.contains("viber.com") || note.contains("https://www.viber.com")) {
                                //SocialModel socialModel = contact.getSocialModel();
                                hasViber = true;

                                socialModel.setViberLink(note);

                            } else if (note.toString().contains("whatsapp.com") || note.toString().contains("https://www.whatsapp.com")) {
                                //SocialModel socialModel = contact.getSocialModel();
                                hasWhatsapp = true;

                                socialModel.setWhatsappLink(note);

                            } else if ((note.toString().contains("telegram.org") || note.toString().contains("https://telegram.org")) || ClipboardType.isTelegram(note)) {
                                //SocialModel socialModel = contact.getSocialModel();
                                //hasTelegram = true;

                                //socialModel.setTelegramLink(note);

                                if (contact.getListOfContactInfo() == null) {
                                    contact.addNote(note);
                                } else {
                                    boolean checkS = false;
                                    for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                        if (contactInfo.value.equalsIgnoreCase(note)) {
                                            checkS = true;
                                            break;
                                        }
                                    }
                                    if (!checkS)
                                        contact.addNote(note);

                                }

                            } else if (note.toString().contains("skype.com") || note.toString().contains("https://www.skype.com") || note.contains("Skype: ")) {
                                //SocialModel socialModel = contact.getSocialModel();
                                if (note.contains("Skype: ")) {
                                    hasSkype = true;
                                    String username = note.substring(7, note.length());
                                    socialModel.setSkypeLink(username);
                                } else {
                                    hasSkype = true;
                                    String username = note.substring(note.indexOf(".com") + 5, note.length());
                                    if (username.charAt(username.length() - 1) == '/')
                                        username = username.substring(0, username.length() - 1);

                                    socialModel.setSkypeLink(username);
                                }

                            } else if (ClipboardType.isYoutube(note)) {

                                if (note.contains("user") || note.contains("channel")) {
                                    if (socialModel != null) {
                                        if (socialModel.getYoutubeLink() == null || socialModel.getYoutubeLink().isEmpty()) {
                                            socialModel.setYoutubeLink(note);
                                            hasYoutube = true;
                                        } else {
                                            if (contact.getListOfContactInfo() == null) {
                                                contact.addNote(note);
                                            } else {
                                                boolean checkS = false;
                                                for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                                    if (contactInfo.value.equalsIgnoreCase(note)) {
                                                        checkS = true;
                                                        break;
                                                    } else if (note.contains("?")) {

                                                        int ind = note.indexOf('?');
                                                        if (ind != -1) {

                                                            if (contactInfo.value.equalsIgnoreCase(note.substring(0, ind))) {

                                                                contactsService.updateNote(contact.getIdContact(), note, note.substring(0, ind));
                                                                checkS = true;
                                                                break;
                                                            }


                                                        }


                                                    }
                                                }
                                                if (!checkS)
                                                    contact.addNote(note);

                                            }
                                        }
                                    }
                                } else {
                                    if (contact.getListOfContactInfo() == null) {
                                        contact.addNote(note);
                                    } else {
                                        boolean checkS = false;
                                        for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                            if (contactInfo.value.equalsIgnoreCase(note)) {
                                                checkS = true;
                                                break;
                                            }
                                        }
                                        if (!checkS)
                                            contact.addNote(note);

                                    }
                                }
                            } else if (ClipboardType.isTwitter(note)) {


                                if (socialModel != null) {
                                    if (socialModel.getTwitterLink() == null || socialModel.getTwitterLink().isEmpty()) {
                                        socialModel.setTwitterLink(note);
                                        hasTwitter = true;
                                    } else {
                                        if (contact.getListOfContactInfo() == null) {
                                            contact.addNote(note);
                                        } else {
                                            boolean checkS = false;
                                            for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                                if (contactInfo.value.equalsIgnoreCase(note)) {
                                                    checkS = true;
                                                    break;
                                                }
                                            }
                                            if (!checkS)
                                                contact.addNote(note);

                                        }
                                    }
                                }
                            } else if (ClipboardType.isMedium(note)) {


                                if (note.contains("com/@")) {
                                    if (socialModel != null) {
                                        if (socialModel.getMediumLink() == null || socialModel.getMediumLink().isEmpty()) {
                                            socialModel.setMediumLink(note);
                                            hasMedium = true;
                                        } else {
                                            if (contact.getListOfContactInfo() == null) {
                                                contact.addNote(note);
                                            } else {
                                                boolean checkS = false;
                                                for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                                    if (contactInfo.value.equalsIgnoreCase(note)) {
                                                        checkS = true;
                                                        break;
                                                    }
                                                }
                                                if (!checkS)
                                                    contact.addNote(note);

                                            }
                                        }
                                    }
                                } else {
                                    if (contact.getListOfContactInfo() == null) {
                                        contact.addNote(note);
                                    } else {
                                        boolean checkS = false;
                                        for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                            if (contactInfo.value.equalsIgnoreCase(note)) {
                                                checkS = true;
                                                break;
                                            }
                                        }
                                        if (!checkS)
                                            contact.addNote(note);

                                    }
                                }

                            } else {
                                boolean checkS = false;
                                for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                    if (contactInfo.value.equalsIgnoreCase(note)) {
                                        checkS = true;
                                        break;
                                    }
                                }
                                if (!checkS) {
                                    contact.addNote(note);
                                    showUpdate = true;
                                }
                            }
                        }
                    }
                }
                realm.commitTransaction();


                //====================
                realm.beginTransaction();
                Cursor notesC = contactsService.getCursorByType(ContactsService.TYPE_CURSOR.NOTE, contact.getIdContact());

                ArrayList<String> listInfoOfContact = new ArrayList<>();
                ArrayList<String> listNotes = new ArrayList<>();


                for (int i = 0; i < contact.getListOfContactInfo().size(); i++) {
                    listInfoOfContact.add(contact.getListOfContactInfo().get(i).value);
                }

                if (notesC != null && notesC.getCount() > 0) {
                    while (notesC.moveToNext()) {
                        String note = notesC.getString(notesC.getColumnIndex(ContactsContract.CommonDataKinds.Note.NOTE));
                        listNotes.add(note);
                        for (String str : listInfoOfContact) {
                            if (str.equals(note)) {
                                listInfoOfContact.remove(str);
                                break;
                            }
                        }
                    }
                }


                if (listInfoOfContact != null && listInfoOfContact.size() > 0) {
                    for (int i = 0; i < contact.getListOfContactInfo().size(); i++) {
                        for (String str : listInfoOfContact) {
                            if (contact.getListOfContactInfo().get(i).value.equals(str) && contact.getListOfContactInfo().get(i).type.equals("note")) {
                                contact.listOfContactInfo.remove(contact.getListOfContactInfo().get(i));
                                showUpdate = true;
                                i--;
                                break;
                            }
                        }
                    }
                }

                    /*
                    if(socialModel.getFacebookLink() != null){

                        showUpdate = true;
                        if(!listNotes.contains(socialModel.getFacebookLink())){

                            boolean find = false;
                            for(int i = 0;i<contact.getListOfContactInfo().size();i++){
                                if(ClipboardType.isFacebook(contact.getListOfContactInfo().get(i).value)){

                                    socialModel.setFacebookLink(contact.getListOfContactInfo().get(i).value);
                                    contact.getListOfContactInfo().remove(contact.getListOfContactInfo().get(i));
                                    find = true;
                                    break;
                                }
                            }
                            if(!find){

                                socialModel.setFacebookLink(null);
                                hasFacebook = false;
                            }
                        }
                    }*/


                realm.commitTransaction();

                //=-=-----------------


                realm.beginTransaction();
                if (contact.hasFacebook != hasFacebook) {
                    showUpdate = true;
                    contact.hasFacebook = hasFacebook;
                }
                if (contact.hasVk != hasVk) {
                    showUpdate = true;
                    contact.hasVk = hasVk;
                }
                if (contact.hasInst != hasInst) {
                    showUpdate = true;
                    contact.hasInst = hasInst;
                }
                if (contact.hasLinked != hasLinked) {
                    showUpdate = true;
                    contact.hasLinked = hasLinked;
                }
                if (contact.hasTwitter != hasTwitter) {
                    showUpdate = true;
                    contact.hasTwitter = hasTwitter;
                }
                if (contact.hasYoutube != hasYoutube) {
                    showUpdate = true;
                    contact.hasYoutube = hasYoutube;
                }
                if (contact.hasMedium != hasMedium) {
                    showUpdate = true;
                    contact.hasMedium = hasMedium;
                }

                if (hasTelegram && contact.getSocialModel() != null && contact.getSocialModel().getTelegramLink() != null && !contact.getSocialModel().getTelegramLink().isEmpty()) {
                    socialModel.setTelegramLink(contact.getSocialModel().getTelegramLink());
                } else {
                    if (!hasTelegram) {
                        hasTelegram = false;
                        socialModel.setTelegramLink(null);
                    }
                }

                if (hasViber && contact.getSocialModel() != null && contact.getSocialModel().getViberLink() != null && !contact.getSocialModel().getViberLink().isEmpty()) {
                    socialModel.setViberLink(contact.getSocialModel().getViberLink());
                } else {
                    if (!hasViber) {
                        hasViber = false;
                        socialModel.setViberLink(null);
                    }
                }

                if (hasWhatsapp && contact.getSocialModel() != null && contact.getSocialModel().getWhatsappLink() != null && !contact.getSocialModel().getWhatsappLink().isEmpty()) {
                    socialModel.setWhatsappLink(contact.getSocialModel().getWhatsappLink());
                } else {
                    if (!hasWhatsapp) {
                        hasWhatsapp = false;
                        socialModel.setWhatsappLink(null);
                    }
                }

                if (contact.getSocialModel() != null && ((contact.getSocialModel().getSkypeLink() == null || contact.getSocialModel().getSkypeLink().isEmpty()) || ((contact.getSocialModel().getSkypeLink() != null && !contact.getSocialModel().getSkypeLink().isEmpty()) && (socialModel.getSkypeLink() != null && !socialModel.getSkypeLink().isEmpty()) && !contact.getSocialModel().getSkypeLink().equalsIgnoreCase(socialModel.getSkypeLink()))) && socialModel.getSkypeLink() != null) {
                    showUpdate = true;
                }

               /* if (hasSkype && contact.getSocialModel() != null && contact.getSocialModel().getSkypeLink() != null && !contact.getSocialModel().getSkypeLink().isEmpty()) {
                    socialModel.setSkypeLink(contact.getSocialModel().getSkypeLink());
                } else {
                    if (!hasSkype) {
                        hasSkype = false;
                        socialModel.setSkypeLink(null);
                    }
                }*/

                contact.setSocialModel(socialModel);
                realm.commitTransaction();


                realm.beginTransaction();
                contact.setListOfHashtags(hashList);
                realm.commitTransaction();

                if (contact.getListOfHashtags().size() != listHashOfContacts.size()) {
                    //
                    showUpdate = true;
                } else {
                    for (HashTag h1 : listHashOfContacts) {
                        boolean check = false;
                        for (HashTag h2 : contact.getListOfHashtags()) {
                            if (h1.getHashTagValue().equalsIgnoreCase(h2.getHashTagValue())) {
                                check = true;
                                break;
                            }
                        }
                        if (!check) {
                            showUpdate = true;
                            break;
                        }
                    }
                }


                String photoURL = ContactsService.getPhotoURI(getContext(), contact.getIdContact());

                if ((photoURL == null && contact.getPhotoURL() != null) || (photoURL != null && contact.getPhotoURL() == null) || (photoURL != null && contact.getPhotoURL() != null && !photoURL.equals(contact.getPhotoURL()))) {
                    showUpdate = true;


                    realm.beginTransaction();
                    contact.setPhotoURL(photoURL);
                    realm.commitTransaction();

                    int colotC = contact.color;
                    String nameContact = contact.getName();
                    try {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {


                                try {
                                    profilePopUp.findViewById(R.id.profilePopupInitials).setVisibility(View.GONE);
                                    profilePopUp.findViewById(R.id.profilePopupAvatar).setBackgroundColor(Color.TRANSPARENT);
                                    ((ImageView) profilePopUp.findViewById(R.id.profilePopupAvatar)).setImageURI(Uri.parse(photoURL));
                                    if (((BitmapDrawable) ((ImageView) profilePopUp.findViewById(R.id.profilePopupAvatar)).getDrawable()).getBitmap() == null) {
                                        GradientDrawable circle = (GradientDrawable) ContextCompat.getDrawable(mainView.getContext(), R.drawable.blue_circle).mutate();
                                        circle.setColor(colotC);
                                        profilePopUp.findViewById(R.id.profilePopupAvatar).setBackground(circle);
                                        ((ImageView) profilePopUp.findViewById(R.id.profilePopupAvatar)).setImageDrawable(null);

                                        String initials = "";
                                        String names[] = nameContact.split("\\s+");

                                        for (String namePart : names)
                                            initials += namePart.charAt(0);

                                        profilePopUp.findViewById(R.id.profilePopupInitials).setVisibility(View.VISIBLE);
                                        ((TextView) profilePopUp.findViewById(R.id.profilePopupInitials)).setText(initials);
                                    }
                                } catch (Exception e) {
                                    GradientDrawable circle = (GradientDrawable) ContextCompat.getDrawable(mainView.getContext(), R.drawable.blue_circle).mutate();
                                    circle.setColor(colotC);
                                    profilePopUp.findViewById(R.id.profilePopupAvatar).setBackground(circle);
                                    ((ImageView) profilePopUp.findViewById(R.id.profilePopupAvatar)).setImageDrawable(null);

                                    String initials = "";
                                    String names[] = nameContact.split("\\s+");

                                    for (String namePart : names)
                                        initials += namePart.charAt(0);

                                    profilePopUp.findViewById(R.id.profilePopupInitials).setVisibility(View.VISIBLE);
                                    ((TextView) profilePopUp.findViewById(R.id.profilePopupInitials)).setText(initials);
                                }


                            }
                        });
                    } catch (NullPointerException e) {

                    }


                }

                boolean finalShowUpdate = showUpdate;
                try {

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Contact contact = ContactCacheService.getContactById(id);


                            if (contact.listOfContactInfo != null) {
                                boolean checkMail = false;
                                ((ImageView) profilePopUp.findViewById(R.id.emailImg)).setImageResource(R.drawable.icn_bottombar_emails);
                                for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                    if (contactInfo.type.toLowerCase().compareTo("email") == 0) {
                                        checkMail = true;
                                        ((ImageView) profilePopUp.findViewById(R.id.emailImg)).setImageResource(R.drawable.icn_bottombar_emails_blue);
                                        profilePopUp.findViewById(R.id.emailImg).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                                        "mailto", contactInfo.value, null));
                                                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
                                                emailIntent.putExtra(Intent.EXTRA_TEXT, "");
                                                startActivity(Intent.createChooser(emailIntent, "Send email"));
                                            }
                                        });
                                    }
                                    if (!checkMail) {
                                        profilePopUp.findViewById(R.id.emailImg).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                            }
                                        });
                                    }
                                }
                            }
                            //=============================================== SOCIALS


                            //===============================================

                            LinearLayout containerHashTags = (LinearLayout) profilePopUp.findViewById(R.id.containerHashTags);
                            if (containerHashTags.getChildCount() > 0)
                                containerHashTags.removeAllViews();
                            for (HashTag hashTag : contact.getListOfHashtags()) {
                                TextView text = new TextView(getActivity());
                                text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                                DisplayMetrics metrics = mainView.getContext().getResources().getDisplayMetrics();
                                text.setLayoutParams(new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                text.setTextColor(getResources().getColor(R.color.colorPrimary));
                                text.setText(hashTag.getHashTagValue() + " ");
//            text.setOnClickListener(v -> contactAdapter.searchByHashTagValue(hashTag.getHashTagValue()));
                                containerHashTags.addView(text);
                            }

                            if (contact.getListOfHashtags().size() == 0) {
                                TextView text = new TextView(getActivity());
                                text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                                DisplayMetrics metrics = mainView.getContext().getResources().getDisplayMetrics();
                                text.setLayoutParams(new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                text.setTextColor(getResources().getColor(R.color.gray));
                                text.setText("hashtags");
                                text.setOnClickListener(v -> {
                                    containerHashTags.removeAllViews();
                                    showPopupUserHashtags(contact);
                                });
                                containerHashTags.addView(text);
                            }


                            profilePopUp.findViewById(R.id.arrowShowHashtags).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (socialPopup != null) {
                                        if (socialPopup.getVisibility() == View.VISIBLE)
                                            return;
                                        showPopupUserHashtags(contact);
                                    } else
                                        showPopupUserHashtags(contact);
                                }
                            });

                            initIconColor(contact, profilePopUp);

                            if (finalShowUpdate) {

                            }
                        }
                    });
                } catch (NullPointerException e) {

                }

                try {
                    if (finalShowUpdate) {


                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (finalShowUpdate) {
                                    profilePopUp.findViewById(R.id.updateTextView).setVisibility(View.VISIBLE);
                                    //EventBus.getDefault().post(new UpdateFile());
                                }
                            }
                        });

                        try {
                            Thread.sleep(2500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (finalShowUpdate) {
                                    profilePopUp.findViewById(R.id.updateTextView).setVisibility(View.GONE);
                                }
                            }
                        });
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

                realm.close();

            }
        }).start();
    }


    public boolean checkClick = true;

    public void OnTouchMethod(TextView textview) {

        checkClick = false;
        int colorFrom = getResources().getColor(R.color.colorPrimary);
        int colorTo = getResources().getColor(R.color.md_deep_orange_300);
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(700); // milliseconds
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                textview.setTextColor((int) animator.getAnimatedValue());
                //  imageview.setColorFilter((int) animator.getAnimatedValue());
                textview.setTypeface(null, Typeface.BOLD);
            }
        });
        colorAnimation.start();
    }

    public void OnUpTouchMethod(TextView textview) {
        if (!checkClick) {
            int colorFrom = getResources().getColor(R.color.md_deep_orange_300);
            int colorTo = getResources().getColor(R.color.colorPrimary);
            ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
            colorAnimation.setDuration(1000); // milliseconds
            colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    textview.setTextColor((int) animator.getAnimatedValue());
                    //  imageview.setColorFilter((int) animator.getAnimatedValue());
                    textview.setTypeface(null, Typeface.NORMAL);
                }

            });
            colorAnimation.start();
        }
    }

    public void OnCalcelTouchMethod(TextView textView) {
        int colorFrom = getResources().getColor(R.color.md_deep_orange_300);
        int colorTo = getResources().getColor(R.color.colorPrimary);
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(1000); // milliseconds
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                textView.setTextColor((int) animator.getAnimatedValue());
                //      imageView.setColorFilter((int) animator.getAnimatedValue());
                textView.setTypeface(null, Typeface.NORMAL);
            }

        });
        colorAnimation.start();
    }

    public void OnMoveTouchMethod(TextView textView, MotionEvent motionEvent, FrameLayout frameLayout) {
        int[] location = new int[2];
        frameLayout.getLocationInWindow(location);
        int leftX = 0;
        int rightX = leftX + frameLayout.getWidth();
        int topY = 0;
        int bottomY = topY + frameLayout.getHeight();
        float xCurrent = motionEvent.getX();
        float yCurrent = motionEvent.getY();

        if (xCurrent > rightX || xCurrent < leftX || yCurrent > bottomY || yCurrent < topY) {
            if (!checkClick) {

                int colorFrom = getResources().getColor(R.color.md_deep_orange_300);
                int colorTo = getResources().getColor(R.color.colorPrimary);
                int colorTo2 = getResources().getColor(R.color.colorPrimaryDark);
                ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
                colorAnimation.setDuration(1000); // milliseconds

                ValueAnimator colorAnimation2 = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo2);
                colorAnimation2.setDuration(1000); // milliseconds

                colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animator) {
                        //  textView.setTextColor((int) animator.getAnimatedValue());
                        // imageView.setColorFilter((int) animator.getAnimatedValue());
                        //   textView.setTypeface(null, Typeface.NORMAL);
                    }

                });
                colorAnimation.start();


                colorAnimation2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animator) {
                        textView.setTextColor((int) animator.getAnimatedValue());
                        //  imageView.setColorFilter((int) animator.getAnimatedValue());
                        textView.setTypeface(null, Typeface.NORMAL);
                    }

                });
                colorAnimation2.start();


                checkClick = true;
            }

        }
    }


    private String getUpdTime(Time time) {
        String timeStr = time.getHours() + ":";
        if (time.getMinutes() < 10) timeStr += "0";
        timeStr += time.getMinutes();
        return timeStr;
    }

    //public boolean checkClick = true;
    public boolean checkClick_viber = true;
    public boolean checkClick_telegram = true;
    public boolean checkClick_skype = true;

    public boolean checkClick_facebook = true;
    public boolean checkClick_vk = true;
    public boolean checkClick_linked = true;
    public boolean checkClick_inst = true;
    public boolean checkClick_youtube = true;
    public boolean checkClick_twitter = true;
    public boolean checkClick_medium = true;

    View FaceBook = null;
    View TwiTter = null;
    View LinkedIn = null;
    View Instagram = null;
    View YouTube = null;
    View Vkontakte = null;
    View Medium = null;

    private void initIconColor(Contact contact, View view) {


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

        if (contact.getSocialModel() != null && contact.getSocialModel().getTwitterLink() != null && !contact.getSocialModel().getTwitterLink().isEmpty()) {
            listExist.add(SocEnum.TWITTER);
            TwiTter = getLayoutInflater().inflate(R.layout.social_twitter, null);

            Drawable color = new ColorDrawable(Color.parseColor("#2ca7e0"));
            Drawable image = getResources().getDrawable(R.drawable.ic_twitter_white);
            LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
            ((ImageView) TwiTter.findViewById(R.id.twitter_icon)).setImageDrawable(ld);

            linearLayout.addView(TwiTter);
        }

        if (contact.getSocialModel() != null && contact.getSocialModel().getLinkedInLink() != null && !contact.getSocialModel().getLinkedInLink().isEmpty()) {
            listExist.add(SocEnum.LINKEDIN);
            LinkedIn = getLayoutInflater().inflate(R.layout.social_linked, null);

            Drawable color = new ColorDrawable(Color.parseColor("#0077B7"));
            Drawable image = getResources().getDrawable(R.drawable.icn_social_linked2);
            LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
            ((ImageView) LinkedIn.findViewById(R.id.link_icon)).setImageDrawable(ld);

            linearLayout.addView(LinkedIn);
        }

        if (contact.getSocialModel() != null && contact.getSocialModel().getInstagramLink() != null && !contact.getSocialModel().getInstagramLink().isEmpty()) {
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
                        //closeChildPopups();
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
                       /* Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format(contact.getSocialModel().getVkLink())));
                        Intent intent2 = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("vkontakte://profile/")));
                        try {
                            startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            e.printStackTrace();
                            PackageManager packageManager = getActivity().getPackageManager();
                            List<ResolveInfo> activities = packageManager.queryIntentActivities(intent2, 0);
                            boolean isIntentSafe = activities.size() > 0;
                            if (!isIntentSafe) {
                                Intent intent3 = new Intent(Intent.ACTION_VIEW);
                                intent3.setData(Uri.parse("market://details?id=com.vkontakte.android"));
                                startActivity(intent3);
                            } else
                                startActivity(intent2);
                        }*/
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
                            //    if (contact.listOfContacts == null || contact.listOfContacts.isEmpty())
                            i.setData(Uri.parse("https://www.linkedin.com/mwlite/search/results/all/?keywords=" + str));
                            //    else
                            //        i.setData(Uri.parse("https://www.linkedin.com/search/results/companies/v2/?keywords=" + str));
                        } else {
                            //    if (contact.listOfContacts == null || contact.listOfContacts.isEmpty())
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
                        //closeChildPopups();
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
                        showSocialPopup(contact);
                        socialPopup.findViewById(R.id.instagramLink).callOnClick();
                        socialPopup.setVisibility(View.GONE);
                    } else {

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
                    System.out.println("CLICK WATS ANIM");

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
                        //closeChildPopups();
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
                                Intent intent3 = new Intent(Intent.ACTION_VIEW);
                                intent3.setData(Uri.parse("market://details?id=com.vkontakte.android"));
                                startActivity(intent3);
                            } else
                                startActivity(intent2);
                        }*/
                    }


                }
            });
        }

        if (Vkontakte != null) {
            Vkontakte.findViewById(R.id.vk_icon).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    System.out.println("CLICK WATS ANIM");

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
                        //closeChildPopups();
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


      /*  ((ImageView) view.findViewById(R.id.facebook_icon))
                .setImageResource(contact.hasFacebook ? R.drawable.icn_social_facebook : R.drawable.icn_social_facebook_gray);*/

        /*if (contact.hasFacebook && contact.getSocialModel() != null && contact.getSocialModel().getFacebookLink() != null && !contact.getSocialModel().getFacebookLink().isEmpty()) {
            Drawable color = new ColorDrawable(Color.parseColor("#475993"));
            Drawable image = getResources().getDrawable(R.drawable.icn_social_facebook2);
            LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
            ((ImageView) view.findViewById(R.id.facebook_icon)).setImageDrawable(ld);
        } else {
            Drawable color = new ColorDrawable(Color.parseColor("#e2e5e8"));
            Drawable image = getResources().getDrawable(R.drawable.icn_social_facebook2);
            LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
            ((ImageView) view.findViewById(R.id.facebook_icon)).setImageDrawable(ld);
        }


        view.findViewById(R.id.facebook_icon).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                System.out.println("CLICK WATS ANIM");

                ImageView cImg = ((ImageView) view.findViewById(R.id.facebook_icon));
                //    ImageView imageView = ((ImageView) profilePopUp.findViewById(R.id.imagePreviewIcon));
                //  FrameLayout frameLayout = ((FrameLayout) mainView.findViewById(R.id.company_profile_block));
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {

                        if (contact.hasFacebook && checkClick_facebook) {
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
                            if (contact.hasFacebook) {
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
                        if (contact.hasFacebook) {
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
                            if (contact.hasFacebook) {
                                int colorFrom = Color.parseColor("#F9A825");
                                int colorTo = Color.parseColor("#475993");
                                Drawable image = getResources().getDrawable(R.drawable.icn_social_facebook2);
                                checkClick_facebook = AnimColorMessenger.ActionMove_(colorFrom, colorTo, image, cImg, motionEvent, mainView);
                            } else {
                                int colorFrom = Color.parseColor("#F9A825");
                                int colorTo = Color.parseColor("#e2e5e8");
                                Drawable image = getResources().getDrawable(R.drawable.icn_social_facebook2);
                                checkClick_facebook = AnimColorMessenger.ActionMove_(colorFrom, colorTo, image, cImg, motionEvent, mainView);
                            }
                        }

                        break;
                    }
                }

                return false;
            }
        });


        view.findViewById(R.id.facebook_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!contact.hasFacebook || contact.getSocialModel() == null || contact.getSocialModel().getFacebookLink() == null || contact.getSocialModel().getFacebookLink().isEmpty()) {
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

                    //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://facewebmodal/f?href="+contact.getSocialModel().getFacebookLink()));
                    PackageManager packageManager = getActivity().getPackageManager();
                    List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
                    boolean isIntentSafe = activities.size() > 0;
                    if (!isIntentSafe) {
                        *//*Intent intent2 = new Intent(Intent.ACTION_VIEW);
                        intent2.setData(Uri.parse("market://details?id=com.facebook.katana"));
                        startActivity(intent2);*//*
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
            }
        });*/

        /*((ImageView) view.findViewById(R.id.whatsapp_icon))
                .setImageResource(contact.hasWhatsapp ? R.drawable.icn_social_whatsapp : R.drawable.icn_social_whatsapp_gray);*/

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


        view.findViewById(R.id.whatsapp_icon).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                System.out.println("CLICK WATS ANIM");

                CircleImageView cImg = ((CircleImageView) view.findViewById(R.id.whatsapp_icon));
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
                if (!contact.hasWhatsapp) {
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
                        getActivity().startActivity(Intent.createChooser(telegramIntent, "Open with...").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        //startActivity(telegramIntent);

                }
            }
        });
        /*((ImageView) view.findViewById(R.id.telegram_icon))
                .setImageResource(contact.hasTelegram ? R.drawable.icn_social_telegram : R.drawable.icn_social_telegram_gray);*/

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
                System.out.println("CLICK WATS ANIM");

                CircleImageView cImg = ((CircleImageView) view.findViewById(R.id.telegram_icon));
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
                System.out.println("CLICK TG");
              /*  if((!contact.hasTelegram) || (contact.hasTelegram && contact.getSocialModel() == null) || (contact.hasTelegram && contact.getSocialModel() != null && contact.getSocialModel().getTelegramLink() == null)){
                    showSocialPopup(contact);
                    socialPopup.findViewById(R.id.telegramLink).callOnClick();
                    socialPopup.setVisibility(View.GONE);
                }else{
                    Intent intent;
                    final String contactId = getContactIdFromPhoneNumber(String.valueOf(contact.getSocialModel().getTelegramLink()));
                    final String contactMimeTypeDataId = getContactMimeTypeDataId(getContext(),contactId, "vnd.android.cursor.item/vnd.org.telegram.messenger.android.profile");
                    if (contactMimeTypeDataId != null) {
                        intent = new Intent(Intent.ACTION_VIEW, Uri.parse("content://com.android.contacts/data/" + contactMimeTypeDataId));
                        intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET | Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
                        intent.setPackage("org.telegram.messenger");
                    } else {
                        intent = new Intent("android.intent.action.VIEW", Uri.parse("tel:" + Uri.encode(String.valueOf(contact.listOfContactInfo.get(0).value))));
                        intent.setClassName("org.telegram.messenger", "org.telegram.messenger.WelcomeActivity");
                    }

                        startActivity(intent);


           *//*         Intent  telegramIntent = new Intent(Intent.ACTION_VIEW);
                    String username = contact.getSocialModel().getTelegramLink();
                    char firstSymbol = username.charAt(0);
                    if(firstSymbol == '@')
                        username = username.substring(1);
                    telegramIntent.setData(Uri.parse("tg://resolve?domain="+username));
                    PackageManager packageManager = getActivity().getPackageManager();
                    List<ResolveInfo> activities = packageManager.queryIntentActivities(telegramIntent, 0);
                    boolean isIntentSafe = activities.size() > 0;
                    if(!isIntentSafe){
                        Intent intent2 = new Intent(Intent.ACTION_VIEW);
                        intent2.setData(Uri.parse("market://details?id=org.telegram.messenger"));
                        startActivity(intent2);
                    }else
                        startActivity(telegramIntent);*//*

                }*/


                if ((!contact.hasTelegram) || (contact.hasTelegram && contact.getSocialModel() == null) || (contact.hasTelegram && contact.getSocialModel() != null && contact.getSocialModel().getTelegramLink() == null)) {
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


              /*  Intent intent;
                final String contactId = getContactIdFromPhoneNumber(String.valueOf(contact.getSocialModel().getTelegramLink()));
                final String contactMimeTypeDataId = getContactMimeTypeDataId(getContext(),contactId, "vnd.android.cursor.item/vnd.org.telegram.messenger.android.profile");
                if (contactMimeTypeDataId != null) {
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("content://com.android.contacts/data/" + contactMimeTypeDataId));
                    intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET | Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
                    intent.setPackage("org.telegram.messenger");
                } else {
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("market://details?id=org.telegram.messenger"));
                    //startActivity(intent2);
                }

                    startActivity(intent);*/

           /*         Intent  telegramIntent = new Intent(Intent.ACTION_VIEW);
                    String username = contact.getSocialModel().getTelegramLink();
                    char firstSymbol = username.charAt(0);
                    if(firstSymbol == '@')
                        username = username.substring(1);
                    telegramIntent.setData(Uri.parse("tg://resolve?domain="+username));
                    PackageManager packageManager = getActivity().getPackageManager();
                    List<ResolveInfo> activities = packageManager.queryIntentActivities(telegramIntent, 0);
                    boolean isIntentSafe = activities.size() > 0;
                    if(!isIntentSafe){
                        Intent intent2 = new Intent(Intent.ACTION_VIEW);
                        intent2.setData(Uri.parse("market://details?id=org.telegram.messenger"));
                        startActivity(intent2);
                    }else
                        startActivity(telegramIntent);*/
                }

            }
        });
       /* ((ImageView) view.findViewById(R.id.link_icon))
                .setImageResource(contact.hasLinked ? R.drawable.icn_social_linkedin : R.drawable.icn_social_linkedin_gray);*/

        /*if (contact.hasLinked && contact.getSocialModel() != null && contact.getSocialModel().getLinkedInLink() != null && !contact.getSocialModel().getLinkedInLink().isEmpty()) {
            Drawable color = new ColorDrawable(Color.parseColor("#0077B7"));
            Drawable image = getResources().getDrawable(R.drawable.icn_social_linked2);
            LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
            ((ImageView) view.findViewById(R.id.link_icon)).setImageDrawable(ld);
        } else {
            Drawable color = new ColorDrawable(Color.parseColor("#e2e5e8"));
            Drawable image = getResources().getDrawable(R.drawable.icn_social_linked2);
            LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
            ((ImageView) view.findViewById(R.id.link_icon)).setImageDrawable(ld);
        }


        view.findViewById(R.id.link_icon).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                System.out.println("CLICK WATS ANIM");

                ImageView cImg = ((ImageView) view.findViewById(R.id.link_icon));
                //    ImageView imageView = ((ImageView) profilePopUp.findViewById(R.id.imagePreviewIcon));
                //  FrameLayout frameLayout = ((FrameLayout) mainView.findViewById(R.id.company_profile_block));
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {

                        if (contact.hasLinked && checkClick_linked) {
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
                            if (contact.hasLinked) {
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
                        if (contact.hasLinked) {
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
                            if (contact.hasLinked) {
                                int colorFrom = Color.parseColor("#F9A825");
                                int colorTo = Color.parseColor("#0077B7");
                                Drawable image = getResources().getDrawable(R.drawable.icn_social_linked2);
                                checkClick_linked = AnimColorMessenger.ActionMove_(colorFrom, colorTo, image, cImg, motionEvent, mainView);
                            } else {
                                int colorFrom = Color.parseColor("#F9A825");
                                int colorTo = Color.parseColor("#e2e5e8");
                                Drawable image = getResources().getDrawable(R.drawable.icn_social_linked2);
                                checkClick_linked = AnimColorMessenger.ActionMove_(colorFrom, colorTo, image, cImg, motionEvent, mainView);
                            }
                        }

                        break;
                    }
                }

                return false;
            }
        });


        view.findViewById(R.id.link_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!contact.hasLinked) {
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
        });*/
        /*((ImageView) view.findViewById(R.id.viber_icon))
                .setImageResource(contact.hasViber ? R.drawable.icn_social_viber : R.drawable.icn_social_viber_gray);*/

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
                System.out.println("CLICK WATS ANIM");

                CircleImageView cImg = ((CircleImageView) view.findViewById(R.id.viber_icon));
                //    ImageView imageView = ((ImageView) profilePopUp.findViewById(R.id.imagePreviewIcon));
                //  FrameLayout frameLayout = ((FrameLayout) mainView.findViewById(R.id.company_profile_block));
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
                if (!contact.hasViber) {
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
//        System.out.println("Link = "+contact.getSocialModel().getVkLink()+", bool = "+contact.hasVk);
       /* ((ImageView) view.findViewById(R.id.vk_icon))
                .setImageResource(contact.hasVk ? R.drawable.icn_social_vk : R.drawable.icn_social_vk_gray);*/

        /*if (contact.hasTwitter && contact.getSocialModel() != null && contact.getSocialModel().getTwitterLink() != null && !contact.getSocialModel().getTwitterLink().isEmpty()) {
            Drawable color = new ColorDrawable(Color.parseColor("#2ca7e0"));
            Drawable image = getResources().getDrawable(R.drawable.ic_twitter_white);
            LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
            ((ImageView) view.findViewById(R.id.vk_icon)).setImageDrawable(ld);
        } else {
            Drawable color = new ColorDrawable(Color.parseColor("#e2e5e8"));
            Drawable image = getResources().getDrawable(R.drawable.ic_twitter_white);
            LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
            ((ImageView) view.findViewById(R.id.vk_icon)).setImageDrawable(ld);
        }

        view.findViewById(R.id.vk_icon).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                System.out.println("CLICK WATS ANIM");

                ImageView cImg = ((ImageView) view.findViewById(R.id.vk_icon));
                //    ImageView imageView = ((ImageView) profilePopUp.findViewById(R.id.imagePreviewIcon));
                //  FrameLayout frameLayout = ((FrameLayout) mainView.findViewById(R.id.company_profile_block));
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {

                        if (contact.hasTwitter && checkClick_vk) {
                            checkClick_vk = false;
                            int colorFrom = Color.parseColor("#2ca7e0");
                            int colorTo = Color.parseColor("#F9A825");
                            Drawable image = getResources().getDrawable(R.drawable.ic_twitter_white);
                            AnimColorMessenger.ActionDown_(colorFrom, colorTo, image, cImg);
                        } else {
                            checkClick_vk = false;
                            int colorFrom = Color.parseColor("#e2e5e8");
                            int colorTo = Color.parseColor("#F9A825");
                            Drawable image = getResources().getDrawable(R.drawable.ic_twitter_white);
                            AnimColorMessenger.ActionDown_(colorFrom, colorTo, image, cImg);
                        }

                        //     OnTouchMethod(textView);
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        if (!checkClick_vk) {
                            if (contact.hasTwitter) {
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
                        if (contact.hasTwitter) {
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
                        if (!checkClick_vk) {
                            if (contact.hasTwitter) {
                                int colorFrom = Color.parseColor("#F9A825");
                                int colorTo = Color.parseColor("#2ca7e0");
                                Drawable image = getResources().getDrawable(R.drawable.ic_twitter_white);
                                checkClick_vk = AnimColorMessenger.ActionMove_(colorFrom, colorTo, image, cImg, motionEvent, mainView);
                            } else {
                                int colorFrom = Color.parseColor("#F9A825");
                                int colorTo = Color.parseColor("#e2e5e8");
                                Drawable image = getResources().getDrawable(R.drawable.ic_twitter_white);
                                checkClick_vk = AnimColorMessenger.ActionMove_(colorFrom, colorTo, image, cImg, motionEvent, mainView);
                            }
                        }

                        break;
                    }
                }

                return false;
            }
        });


        view.findViewById(R.id.vk_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!contact.hasTwitter) {
                    showSocialPopup(contact);
                    socialPopup.findViewById(R.id.twitter_link).callOnClick();
                    socialPopup.setVisibility(View.GONE);
                } else {
                    String text = contact.getSocialModel().getTwitterLink();
                    if (text.contains("twitter.com/")) {
                        text = text.substring(text.indexOf(".com/") + 5);
                    }
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
        });*/
        /*((ImageView) view.findViewById(R.id.skype_icon))
                .setImageResource(contact.hasSkype ? R.drawable.icn_social_skype : R.drawable.icn_social_skype_gray);*/

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
                System.out.println("CLICK WATS ANIM");

                CircleImageView cImg = ((CircleImageView) view.findViewById(R.id.skype_icon));
                //    ImageView imageView = ((ImageView) profilePopUp.findViewById(R.id.imagePreviewIcon));
                //  FrameLayout frameLayout = ((FrameLayout) mainView.findViewById(R.id.company_profile_block));
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        checkClick_skype = false;
                        if (contact.hasSkype) {
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
                            if (contact.hasSkype) {
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
                        if (contact.hasSkype) {
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
                            if (contact.hasSkype) {
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
        /*((ImageView) view.findViewById(R.id.inst_icon))
                .setImageResource(contact.hasInst ? R.drawable.icn_social_instagram : R.drawable.icn_social_instagram_gray);*/

       /* if (contact.hasInst) {
            Drawable color = new ColorDrawable(Color.parseColor("#8a3ab9"));
            Drawable image = getResources().getDrawable(R.drawable.icn_social_ints2);
            LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
            ((ImageView) view.findViewById(R.id.inst_icon)).setImageDrawable(ld);
        } else {
            Drawable color = new ColorDrawable(Color.parseColor("#e2e5e8"));
            Drawable image = getResources().getDrawable(R.drawable.icn_social_ints2);
            LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
            ((ImageView) view.findViewById(R.id.inst_icon)).setImageDrawable(ld);
        }


        view.findViewById(R.id.inst_icon).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                System.out.println("CLICK WATS ANIM");

                ImageView cImg = ((ImageView) view.findViewById(R.id.inst_icon));
                //    ImageView imageView = ((ImageView) profilePopUp.findViewById(R.id.imagePreviewIcon));
                //  FrameLayout frameLayout = ((FrameLayout) mainView.findViewById(R.id.company_profile_block));
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {

                        if (contact.hasInst && checkClick_inst) {
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
                            if (contact.hasInst) {
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
                        if (contact.hasInst) {
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
                            if (contact.hasInst) {
                                int colorFrom = Color.parseColor("#F9A825");
                                int colorTo = Color.parseColor("#8a3ab9");
                                Drawable image = getResources().getDrawable(R.drawable.icn_social_ints2);
                                checkClick_inst = AnimColorMessenger.ActionMove_(colorFrom, colorTo, image, cImg, motionEvent, mainView);
                            } else {
                                int colorFrom = Color.parseColor("#F9A825");
                                int colorTo = Color.parseColor("#e2e5e8");
                                Drawable image = getResources().getDrawable(R.drawable.icn_social_ints2);
                                checkClick_inst = AnimColorMessenger.ActionMove_(colorFrom, colorTo, image, cImg, motionEvent, mainView);
                            }
                        }

                        break;
                    }
                }

                return false;
            }
        });

        view.findViewById(R.id.inst_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!contact.hasInst) {
                    showSocialPopup(contact);
                    socialPopup.findViewById(R.id.instagramLink).callOnClick();
                    socialPopup.setVisibility(View.GONE);
                } else {


                    String str = contact.getSocialModel().getInstagramLink();
                    if (!str.contains("instagram")) {
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
                       *//* Intent intent2 = new Intent(Intent.ACTION_VIEW);
                        intent2.setData(Uri.parse("market://details?id=com.instagram.android"));
                        startActivity(intent2);*//*
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
            }
        });*/
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

    public boolean checkClick_facebookEdit = true;

    public void showSocialPopup(Contact contact) {

        socialPopup = (FrameLayout) getActivity().findViewById(R.id.popupSocial);

        ((ScrollView) socialPopup.findViewById(R.id.scroll)).scrollTo(0, 0);

        socialPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        FrameLayout editFrame = (FrameLayout) getActivity().findViewById(R.id.popupEditMain);
        editFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

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
        ((TextView) socialPopup.findViewById(R.id.link_text)).setText("add  username or link");

        //  ((ImageView) socialPopup.findViewById(R.id.instagram_icon)).setImageResource(R.drawable.icn_social_instagram_gray);
        Drawable color4 = new ColorDrawable(Color.parseColor("#e2e5e8"));
        Drawable image4 = getResources().getDrawable(R.drawable.icn_social_ints2);
        LayerDrawable ld4 = new LayerDrawable(new Drawable[]{color4, image4});
        ((ImageView) socialPopup.findViewById(R.id.instagram_icon)).setImageDrawable(ld4);

        ((TextView) socialPopup.findViewById(R.id.instagramNick)).setText("add username or link");
        Drawable color41 = new ColorDrawable(Color.parseColor("#e2e5e8"));
        Drawable image41 = getResources().getDrawable(R.drawable.ic_twitter_white);
        LayerDrawable ld41 = new LayerDrawable(new Drawable[]{color41, image41});
        ((ImageView) socialPopup.findViewById(R.id.twitter_icon)).setImageDrawable(ld41);

        ((TextView) socialPopup.findViewById(R.id.twitterNick)).setText("add username or link");

        Drawable color411 = new ColorDrawable(Color.parseColor("#e2e5e8"));
        Drawable image411 = getResources().getDrawable(R.drawable.ic_youtube_white);
        LayerDrawable ld411 = new LayerDrawable(new Drawable[]{color411, image411});
        ((ImageView) socialPopup.findViewById(R.id.youtube_icon)).setImageDrawable(ld411);

        ((TextView) socialPopup.findViewById(R.id.youtubeNick)).setText("add username or link");

        Drawable color4112 = new ColorDrawable(Color.parseColor("#e2e5e8"));
        Drawable image4112 = getResources().getDrawable(R.drawable.medium_white);
        LayerDrawable ld4112 = new LayerDrawable(new Drawable[]{color4112, image4112});
        ((ImageView) socialPopup.findViewById(R.id.medium_icon)).setImageDrawable(ld4112);
        ((TextView) socialPopup.findViewById(R.id.mediumNick)).setText("add username or link");

        Drawable color5 = new ColorDrawable(Color.parseColor("#e2e5e8"));
        Drawable image5 = getResources().getDrawable(R.drawable.icn_social_viber2_gray);
        LayerDrawable ld5 = new LayerDrawable(new Drawable[]{color5, image5});
        ((CircleImageView) socialPopup.findViewById(R.id.viber_icon)).setImageDrawable(ld5);
        //   ((ImageView) socialPopup.findViewById(R.id.viber_icon)).setImageResource(R.drawable.icn_social_viber_gray);
        ((TextView) socialPopup.findViewById(R.id.viber_text)).setText("add new link");

        Drawable color6 = new ColorDrawable(Color.parseColor("#e2e5e8"));
        Drawable image6 = getResources().getDrawable(R.drawable.icn_social_whatsapp3_gray);
        LayerDrawable ld6 = new LayerDrawable(new Drawable[]{color6, image6});
        ((CircleImageView) socialPopup.findViewById(R.id.whatsapp_icon)).setImageDrawable(ld6);

        //  ((ImageView) socialPopup.findViewById(R.id.whatsapp_icon)).setImageResource(R.drawable.icn_social_whatsapp_gray);
        ((TextView) socialPopup.findViewById(R.id.whatsapp_text)).setText("add new link");

        //   ((ImageView) socialPopup.findViewById(R.id.telegram_icon)).setImageResource(R.drawable.icn_social_telegram_gray);
        Drawable color7 = new ColorDrawable(Color.parseColor("#e2e5e8"));
        Drawable image7 = getResources().getDrawable(R.drawable.icn_social_telegram2);
        LayerDrawable ld7 = new LayerDrawable(new Drawable[]{color7, image7});
        ((CircleImageView) socialPopup.findViewById(R.id.telegram_icon)).setImageDrawable(ld7);

        ((TextView) socialPopup.findViewById(R.id.telegram_text)).setText("add new link");

        //    ((ImageView) socialPopup.findViewById(R.id.skype_icon)).setImageResource(R.drawable.icn_social_skype_gray);
        Drawable color8 = new ColorDrawable(Color.parseColor("#e2e5e8"));
        Drawable image8 = getResources().getDrawable(R.drawable.icn_social_skype2_gray);
        LayerDrawable ld8 = new LayerDrawable(new Drawable[]{color8, image8});
        ((CircleImageView) socialPopup.findViewById(R.id.skype_icon)).setImageDrawable(ld8);

        ((TextView) socialPopup.findViewById(R.id.skype_text)).setText("add new link");


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
                        String checkFacebook = link.substring(0, 25);
                        if (checkFacebook.equals("https://www.facebook.com/")) {
                            link = link.substring(25, link.length());
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
                    if (checkVK.equals("https://")) {
                        System.out.println("TRUE VK LINK");
                        link = link.substring(8, link.length());
                        System.out.println("TRUE VK LINK2 = " + link);
                        ((TextView) socialPopup.findViewById(R.id.vkNick)).setText(link);
                    } else
                        ((TextView) socialPopup.findViewById(R.id.vkNick)).setText(contact.getSocialModel().getVkLink());
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
                    ((TextView) socialPopup.findViewById(R.id.instagramNick)).setText(outLink);
                } else
                    ((TextView) socialPopup.findViewById(R.id.instagramNick)).setText(contact.getSocialModel().getInstagramLink());

            }

            if (socialModel.getTwitterLink() != null) {
                //   ((ImageView) socialPopup.findViewById(R.id.link_icon)).setImageResource(R.drawable.icn_social_linkedin);


                Drawable colori = new ColorDrawable(Color.parseColor("#2ca7e0"));
                Drawable imagei = getResources().getDrawable(R.drawable.ic_twitter_white);
                LayerDrawable ldi = new LayerDrawable(new Drawable[]{colori, imagei});
                ((ImageView) socialPopup.findViewById(R.id.twitter_icon)).setImageDrawable(ldi);

                try {
                    /*String link = socialModel.getLinkedInLink();
                    String sub = link.substring(link.length() - 20, link.length());*/
                    if (socialModel.getTwitterLink().contains(".com/")) {
                        ((TextView) socialPopup.findViewById(R.id.twitterNick)).setText(socialModel.getTwitterLink().substring(socialModel.getTwitterLink().indexOf(".com/") + 5));
                    } else
                        ((TextView) socialPopup.findViewById(R.id.twitterNick)).setText(socialModel.getTwitterLink());
                } catch (Exception e) {
                    e.printStackTrace();
                    //((TextView) socialPopup.findViewById(R.id.link_text)).setText(contact.getSocialModel().getLinkedInLink());
                }
            }

            if (socialModel.getYoutubeLink() != null) {
                //   ((ImageView) socialPopup.findViewById(R.id.link_icon)).setImageResource(R.drawable.icn_social_linkedin);
                Drawable colori = new ColorDrawable(Color.parseColor("#ed2524"));
                Drawable imagei = getResources().getDrawable(R.drawable.ic_youtube_white);
                LayerDrawable ldi = new LayerDrawable(new Drawable[]{colori, imagei});
                ((ImageView) socialPopup.findViewById(R.id.youtube_icon)).setImageDrawable(ldi);

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

            if (socialModel.getMediumLink() != null) {
                //   ((ImageView) socialPopup.findViewById(R.id.link_icon)).setImageResource(R.drawable.icn_social_linkedin);
                Drawable colori = new ColorDrawable(Color.parseColor("#000000"));
                Drawable imagei = getResources().getDrawable(R.drawable.medium_white);
                LayerDrawable ldi2 = new LayerDrawable(new Drawable[]{colori, imagei});
                ((ImageView) socialPopup.findViewById(R.id.medium_icon)).setImageDrawable(ldi2);

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
                //     System.out.println("WHAAATS APP");
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

                    ((ImageView) editFrame.findViewById(R.id.iconDataEdit)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_twitter_48));

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
                        System.out.println("CLICK WATS ANIM");

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
                                contact.getSocialModel().setTwitterLink(f);
                            } else {
                                contact.getSocialModel().setTwitterLink(((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().toString());
                            }

                            //socialModel.setTwitterLink(((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().toString());
                            //     ((ImageView) socialPopup.findViewById(R.id.vk_icon)).setImageResource(R.drawable.icn_social_vk);
                            //     ((ImageView) getActivity().findViewById(R.id.vk_icon)).setImageResource(R.drawable.icn_social_vk);
                            Drawable colorv = new ColorDrawable(Color.parseColor("#2ca7e0"));
                            Drawable imagev = getResources().getDrawable(R.drawable.ic_twitter_white);
                            LayerDrawable ldv = new LayerDrawable(new Drawable[]{colorv, imagev});
                            ((ImageView) socialPopup.findViewById(R.id.twitter_icon)).setImageDrawable(ldv);

                            try {
                                ((ImageView) profilePopUp.findViewById(R.id.vk_icon)).setImageDrawable(ldv);
                            } catch (Exception e) {

                            }



                          /*  ((ImageView) getActivity().findViewById(R.id.vk_icon)).setImageDrawable(ldv);

                            try{
                                ((ImageView) profilePopUp.findViewById(R.id.vk_icon)).setImageDrawable(ldv);
                            }catch (Exception e){

                            }

                            try{
                                ((ImageView) companyProfilePopup.findViewById(R.id.vk_icon)).setImageDrawable(ldv);
                            }catch (Exception e){

                            }*/


                          /*  if (profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE) {
                                ((ImageView) profilePopUp.findViewById(R.id.vk_icon)).setImageDrawable(ldv);
                            } else if (companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE) {
                                ((ImageView) companyProfilePopup.findViewById(R.id.vk_icon)).setImageDrawable(ldv);
                            } else {
                                ((ImageView) getActivity().findViewById(R.id.vk_icon)).setImageDrawable(ldv);
                            }*/


                            /*try {
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
                            } catch (Exception e) {*/
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

                            try {
                                ((ImageView) profilePopUp.findViewById(R.id.vk_icon)).setImageDrawable(ld2);
                            } catch (Exception e) {

                            }

                         /*   ((ImageView) getActivity().findViewById(R.id.vk_icon)).setImageDrawable(ld2);

                            try{
                                ((ImageView) profilePopUp.findViewById(R.id.facebook_icon)).setImageDrawable(ld2);
                            }catch (Exception e){

                            }

                            try{
                                ((ImageView) companyProfilePopup.findViewById(R.id.facebook_icon)).setImageDrawable(ld2);
                            }catch (Exception e){

                            }*/

                           /* if (profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE) {
                                ((ImageView) profilePopUp.findViewById(R.id.vk_icon)).setImageDrawable(ld2);
                            } else if (companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE) {
                                ((ImageView) companyProfilePopup.findViewById(R.id.vk_icon)).setImageDrawable(ld2);
                            } else {
                                ((ImageView) getActivity().findViewById(R.id.vk_icon)).setImageDrawable(ld2);
                            }*/


                            ((TextView) socialPopup.findViewById(R.id.twitterNick)).setText("add username or");
                            contact.hasTwitter = false;
                            contact.setSocialModel(socialModel);
                            realm.commitTransaction();
                            ContactCacheService.updateContact(contact, mainView.getContext());
                            //  EventBus.getDefault().post(new UpdateFile());
                            editFrame.setVisibility(View.GONE);
                        }

                        realm.close();

                        if (profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE) {
                            initIconColor(contact, profilePopUp);
                        } else if (fastEditPopup != null && fastEditPopup.getVisibility() == View.VISIBLE) {
                            initIconColor(contact, fastEditPopup);
                        }

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
                    editFrame.findViewById(R.id.ok_social).setVisibility(View.GONE);
                    editFrame.findViewById(R.id.get_last_clips).setVisibility(View.VISIBLE);
                    editFrame.findViewById(R.id.imageEditSocial).setVisibility(View.VISIBLE);
                    editFrame.findViewById(R.id.searchSocial).setVisibility(View.VISIBLE);
                    editFrame.findViewById(R.id.frameImageEditSocialInsta).setVisibility(View.GONE);

                    ((ImageView) editFrame.findViewById(R.id.iconDataEdit)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_youtube_48));

                    Drawable color = new ColorDrawable(Color.parseColor("#e2e5e8"));
                    Drawable image = getResources().getDrawable(R.drawable.ic_youtube_white);
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
                        System.out.println("CLICK WATS ANIM");

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
                                contact.getSocialModel().setYoutubeLink(f);
                            } else {
                                contact.getSocialModel().setYoutubeLink(((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().toString());
                            }

                            //socialModel.setYoutubeLink(((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().toString());
                            //     ((ImageView) socialPopup.findViewById(R.id.vk_icon)).setImageResource(R.drawable.icn_social_vk);
                            //     ((ImageView) getActivity().findViewById(R.id.vk_icon)).setImageResource(R.drawable.icn_social_vk);
                            Drawable colorv = new ColorDrawable(Color.parseColor("#ed2524"));
                            Drawable imagev = getResources().getDrawable(R.drawable.ic_youtube_white);
                            LayerDrawable ldv = new LayerDrawable(new Drawable[]{colorv, imagev});
                            ((ImageView) socialPopup.findViewById(R.id.youtube_icon)).setImageDrawable(ldv);

                          /*  ((ImageView) getActivity().findViewById(R.id.vk_icon)).setImageDrawable(ldv);

                            try{
                                ((ImageView) profilePopUp.findViewById(R.id.vk_icon)).setImageDrawable(ldv);
                            }catch (Exception e){

                            }

                            try{
                                ((ImageView) companyProfilePopup.findViewById(R.id.vk_icon)).setImageDrawable(ldv);
                            }catch (Exception e){

                            }*/


                           /* if (profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE) {
                                ((ImageView) profilePopUp.findViewById(R.id.vk_icon)).setImageDrawable(ldv);
                            } else if (companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE) {
                                ((ImageView) companyProfilePopup.findViewById(R.id.vk_icon)).setImageDrawable(ldv);
                            } else {
                                ((ImageView) getActivity().findViewById(R.id.vk_icon)).setImageDrawable(ldv);
                            }*/


                            /*try {
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
                            } catch (Exception e) {*/
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
                         /*   ((ImageView) getActivity().findViewById(R.id.vk_icon)).setImageDrawable(ld2);

                            try{
                                ((ImageView) profilePopUp.findViewById(R.id.facebook_icon)).setImageDrawable(ld2);
                            }catch (Exception e){

                            }

                            try{
                                ((ImageView) companyProfilePopup.findViewById(R.id.facebook_icon)).setImageDrawable(ld2);
                            }catch (Exception e){

                            }*/

                          /*  if (profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE) {
                                ((ImageView) profilePopUp.findViewById(R.id.vk_icon)).setImageDrawable(ld2);
                            } else if (companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE) {
                                ((ImageView) companyProfilePopup.findViewById(R.id.vk_icon)).setImageDrawable(ld2);
                            } else {
                                ((ImageView) getActivity().findViewById(R.id.vk_icon)).setImageDrawable(ld2);
                            }*/


                            ((TextView) socialPopup.findViewById(R.id.youtubeNick)).setText("add username or");
                            contact.hasYoutube = false;
                            contact.setSocialModel(socialModel);
                            realm.commitTransaction();
                            ContactCacheService.updateContact(contact, mainView.getContext());
                            //  EventBus.getDefault().post(new UpdateFile());
                            editFrame.setVisibility(View.GONE);
                        }

                        realm.close();

                        if (profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE) {
                            initIconColor(contact, profilePopUp);
                        } else if (fastEditPopup != null && fastEditPopup.getVisibility() == View.VISIBLE) {
                            initIconColor(contact, fastEditPopup);
                        }

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
                editFrame.findViewById(R.id.cancelEditField).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editFrame.setVisibility(View.GONE);
                    }
                });
            }
        });


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

                    ((ImageView) editFrame.findViewById(R.id.iconDataEdit)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.medium_size_64));

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
                        System.out.println("CLICK WATS ANIM");

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
                ((TextView) editFrame.findViewById(R.id.typeField)).setText("Medium");


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
                                contact.getSocialModel().setMediumLink(f);
                            } else {
                                contact.getSocialModel().setMediumLink(((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().toString());
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


                           /* if (profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE) {
                                ((ImageView) profilePopUp.findViewById(R.id.vk_icon)).setImageDrawable(ldv);
                            } else if (companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE) {
                                ((ImageView) companyProfilePopup.findViewById(R.id.vk_icon)).setImageDrawable(ldv);
                            } else {
                                ((ImageView) getActivity().findViewById(R.id.vk_icon)).setImageDrawable(ldv);
                            }*/


                            /*try {
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
                            } catch (Exception e) {*/

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

                            //}

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

                          /*  if (profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE) {
                                ((ImageView) profilePopUp.findViewById(R.id.vk_icon)).setImageDrawable(ld2);
                            } else if (companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE) {
                                ((ImageView) companyProfilePopup.findViewById(R.id.vk_icon)).setImageDrawable(ld2);
                            } else {
                                ((ImageView) getActivity().findViewById(R.id.vk_icon)).setImageDrawable(ld2);
                            }*/


                            ((TextView) socialPopup.findViewById(R.id.mediumNick)).setText("add username or link");
                            contact.hasMedium = false;
                            contact.setSocialModel(socialModel);
                            realm.commitTransaction();
                            ContactCacheService.updateContact(contact, mainView.getContext());
                            //  EventBus.getDefault().post(new UpdateFile());
                            editFrame.setVisibility(View.GONE);
                        }

                        realm.close();

                        if (profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE) {
                            initIconColor(contact, profilePopUp);
                        } else if (fastEditPopup != null && fastEditPopup.getVisibility() == View.VISIBLE) {
                            initIconColor(contact, fastEditPopup);
                        }

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

                        EventBus.getDefault().post(new UpdateFile());

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

                if (contact.getSocialModel() != null && contact.getSocialModel().getFacebookLink() != null)
                    showEditPopupPreviewSocial(contact, SocialEnums.FACEBOOK);
                else {
                    editFrame.setVisibility(View.VISIBLE);
                    editFrame.findViewById(R.id.ok_social).setVisibility(View.GONE);
                    editFrame.findViewById(R.id.get_last_clips).setVisibility(View.VISIBLE);

                    if (contact.listOfContacts == null || contact.listOfContacts.isEmpty()) {
                        editFrame.findViewById(R.id.imageEditSocial).setVisibility(View.VISIBLE);
                        editFrame.findViewById(R.id.searchSocial).setVisibility(View.VISIBLE);
                        editFrame.findViewById(R.id.frameImageEditSocialInsta).setVisibility(View.GONE);



                        Drawable color = new ColorDrawable(Color.parseColor("#e2e5e8"));
                        Drawable image = getActivity().getResources().getDrawable(R.drawable.icn_social_facebook2);
                        LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
                        ((ImageView) editFrame.findViewById(R.id.imageEditSocial)).setImageDrawable(ld);


                        ((EditText) editFrame.findViewById(R.id.dataToEdit)).setHint("Add link, username or search");
                        ((EditText) editFrame.findViewById(R.id.dataToEdit)).setPadding(0, 0, 150, 0);
                    } else {
                        ((EditText) editFrame.findViewById(R.id.dataToEdit)).setPadding(0, 0, 0, 0);
                        editFrame.findViewById(R.id.imageEditSocial).setVisibility(View.GONE);
                        editFrame.findViewById(R.id.searchSocial).setVisibility(View.GONE);
                        editFrame.findViewById(R.id.frameImageEditSocialInsta).setVisibility(View.GONE);
                        ((EditText) editFrame.findViewById(R.id.dataToEdit)).setHint("Add link or username");
                    }
                    ((ImageView) editFrame.findViewById(R.id.iconDataEdit)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_social_facebook));
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
                        if (mach.length >= 2)
                            i.setData(Uri.parse("https://www.facebook.com/search/people/?q=" + mach[0] + "%20" + mach[1]));
                        else
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
                        if (mach.length >= 2)
                            i.setData(Uri.parse("https://www.facebook.com/search/people/?q=" + mach[0] + "%20" + mach[1]));
                        else
                            i.setData(Uri.parse("https://www.facebook.com/search/people/?q=" + mach[0]));
                        startActivity(i);

                    }
                });


                editFrame.findViewById(R.id.imageEditSocial).setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        System.out.println("CLICK WATS ANIM");

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
                //((EditText) editFrame.findViewById(R.id.dataToEdit)).setHint("Enter link");
                editFrame.findViewById(R.id.updateContactField).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Realm realm = Realm.getDefaultInstance(); //-
                        realm.beginTransaction();
                        //SocialModel socialModel;
                        if (contact.getSocialModel() == null) {
                            socialModel = realm.createObject(SocialModel.class);
                        } else
                            socialModel = contact.getSocialModel();

                        contact.setSocialModel(socialModel);


                        if (((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().length() > 0) {

                            if (socialModel.getFacebookLink() != null && !socialModel.getFacebookLink().isEmpty()) {
                                contactsService.deleteNoteContact(contact.getIdContact(), socialModel.getFacebookLink());
                            }


                            String f = ((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().toString();

                            if (!f.contains(".com/")) {
                                f = "https://www.facebook.com/" + f;
                            }

                            if (ClipboardType.isFacebook(f)) {
                                contact.getSocialModel().setFacebookLink(f);
                            } else {
                                contact.getSocialModel().setFacebookLink(((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().toString());
                            }


                            //contact.getSocialModel().setFacebookLink(((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().toString());
                            //     ((ImageView) socialPopup.findViewById(R.id.facebook_icon)).setImageResource(R.drawable.icn_social_facebook);
                            //     ((ImageView) getActivity().findViewById(R.id.facebook_icon)).setImageResource(R.drawable.icn_social_facebook);
                            Drawable colorf = new ColorDrawable(Color.parseColor("#475993"));
                            Drawable imagef = getResources().getDrawable(R.drawable.icn_social_facebook2);
                            LayerDrawable ldf = new LayerDrawable(new Drawable[]{colorf, imagef});
                            ((ImageView) socialPopup.findViewById(R.id.facebook_icon)).setImageDrawable(ldf);

                            //((ImageView) getActivity().findViewById(R.id.facebook_icon)).setImageDrawable(ldf);


                            try {
                                ((ImageView) profilePopUp.findViewById(R.id.facebook_icon)).setImageDrawable(ldf);
                            } catch (Exception e) {

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
                                    String checkFacebook = link.substring(0, 25);
                                    if (checkFacebook.equals("https://www.facebook.com/")) {
                                        link = link.substring(25, link.length());
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

                            contact.hasFacebook = true;
                            contact.setSocialModel(socialModel);
                            realm.commitTransaction();
                            contactsService.addNoteToContact(contact.getIdContact(), contact.getSocialModel().getFacebookLink(), contact.getName());
                            ContactCacheService.updateContact(contact, mainView.getContext());
                            //EventBus.getDefault().post(new UpdateFile());
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
                            ((ImageView) getActivity().findViewById(R.id.facebook_icon)).setImageDrawable(ld);

                            ((TextView) socialPopup.findViewById(R.id.facebook_nick)).setText("add new link");
                            contact.hasFacebook = false;
                            contact.setSocialModel(socialModel);
                            realm.commitTransaction();
                            ContactCacheService.updateContact(contact, mainView.getContext());
                            //EventBus.getDefault().post(new UpdateFile());
                            editFrame.setVisibility(View.GONE);
                        }

                        realm.close();

                        if (profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE) {
                            initIconColor(contact, profilePopUp);
                        } else if (fastEditPopup != null && fastEditPopup.getVisibility() == View.VISIBLE) {
                            initIconColor(contact, fastEditPopup);
                        }

                        EventBus.getDefault().post(new UpdateFile());
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
                    editFrame.findViewById(R.id.ok_social).setVisibility(View.GONE);
                    editFrame.findViewById(R.id.get_last_clips).setVisibility(View.VISIBLE);

                    if (contact.listOfContacts == null || contact.listOfContacts.isEmpty()) {
                        editFrame.findViewById(R.id.imageEditSocial).setVisibility(View.VISIBLE);
                        editFrame.findViewById(R.id.searchSocial).setVisibility(View.VISIBLE);
                        editFrame.findViewById(R.id.frameImageEditSocialInsta).setVisibility(View.GONE);

                        Drawable color = new ColorDrawable(Color.parseColor("#e2e5e8"));
                        Drawable image = getResources().getDrawable(R.drawable.icn_social_vk2);
                        LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
                        ((ImageView) editFrame.findViewById(R.id.imageEditSocial)).setImageDrawable(ld);
                        ((EditText) editFrame.findViewById(R.id.dataToEdit)).setHint("Add link, username or search");
                        ((EditText) editFrame.findViewById(R.id.dataToEdit)).setPadding(0, 0, 150, 0);
                    } else {
                        ((EditText) editFrame.findViewById(R.id.dataToEdit)).setPadding(0, 0, 0, 0);
                        editFrame.findViewById(R.id.imageEditSocial).setVisibility(View.GONE);
                        editFrame.findViewById(R.id.searchSocial).setVisibility(View.GONE);
                        editFrame.findViewById(R.id.frameImageEditSocialInsta).setVisibility(View.GONE);
                        ((EditText) editFrame.findViewById(R.id.dataToEdit)).setHint("Add link or username");
                    }
                    ((ImageView) editFrame.findViewById(R.id.iconDataEdit)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_social_vk));

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
                        System.out.println("CLICK WATS ANIM");

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

                openedViews.add(editFrame);
                ((TextView) editFrame.findViewById(R.id.typeField)).setText("vkontakte");
                //((EditText) editFrame.findViewById(R.id.dataToEdit)).setHint("Enter link");

                editFrame.findViewById(R.id.updateContactField).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        Realm realm = Realm.getDefaultInstance(); //-
                        realm.beginTransaction();
                        //SocialModel socialModel;
                        if (contact.getSocialModel() == null) {
                            socialModel = realm.createObject(SocialModel.class);
                        } else
                            socialModel = contact.getSocialModel();

                        contact.setSocialModel(socialModel);

                        if (((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().length() > 0) {

                            if (socialModel.getVkLink() != null && !socialModel.getVkLink().isEmpty()) {
                                contactsService.deleteNoteContact(contact.getIdContact(), socialModel.getVkLink());
                            }


                            String f = ((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().toString();

                            if (!f.contains(".com/")) {
                                f = "https://vk.com/" + f;
                            }

                            if (ClipboardType.isVk(f)) {
                                contact.getSocialModel().setVkLink(f);
                            } else {
                                contact.getSocialModel().setVkLink(((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().toString());
                            }

                            //contact.getSocialModel().setVkLink(((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().toString());
                            //      ((ImageView) socialPopup.findViewById(R.id.vk_icon)).setImageResource(R.drawable.icn_social_vk);
                            //      ((ImageView) getActivity().findViewById(R.id.vk_icon)).setImageResource(R.drawable.icn_social_vk);
                            Drawable colorv = new ColorDrawable(Color.parseColor("#507299"));
                            Drawable imagev = getResources().getDrawable(R.drawable.icn_social_vk2);
                            LayerDrawable ldv = new LayerDrawable(new Drawable[]{colorv, imagev});
                            ((ImageView) socialPopup.findViewById(R.id.vk_icon)).setImageDrawable(ldv);
                            //((ImageView) getActivity().findViewById(R.id.vk_icon)).setImageDrawable(ldv);

                            try {
                                ((ImageView) profilePopUp.findViewById(R.id.vk_icon)).setImageDrawable(ldv);
                            } catch (Exception e) {

                            }

                            try {
                                String link = socialModel.getVkLink();
                                String checkVK = link.substring(0, 8);
                                if (checkVK.equals("https://")) {
                                    System.out.println("TRUE VK LINK");
                                    link = link.substring(8, link.length());
                                    System.out.println("TRUE VK LINK2 = " + link);
                                    ((TextView) socialPopup.findViewById(R.id.vkNick)).setText(link);
                                } else
                                    ((TextView) socialPopup.findViewById(R.id.vkNick)).setText(socialModel.getVkLink());
                            } catch (Exception e) {
                                ((TextView) socialPopup.findViewById(R.id.vkNick)).setText(socialModel.getVkLink());
                            }

                            contact.hasVk = true;
                            contact.setSocialModel(socialModel);

                            realm.commitTransaction();
                            contactsService.addNoteToContact(contact.getIdContact(), contact.getSocialModel().getVkLink(), contact.getName());
                            ContactCacheService.updateContact(contact, mainView.getContext());
                            //EventBus.getDefault().post(new UpdateFile());
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
                            ((ImageView) getActivity().findViewById(R.id.vk_icon)).setImageDrawable(ld2);

                            ((TextView) socialPopup.findViewById(R.id.vkNick)).setText("add new link");
                            contact.hasVk = false;
                            contact.setSocialModel(socialModel);
                            realm.commitTransaction();
                            ContactCacheService.updateContact(contact, mainView.getContext());
                            //EventBus.getDefault().post(new UpdateFile());
                            editFrame.setVisibility(View.GONE);
                        }

                        realm.close();

                        if (profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE) {
                            initIconColor(contact, profilePopUp);
                        } else if (fastEditPopup != null && fastEditPopup.getVisibility() == View.VISIBLE) {
                            initIconColor(contact, fastEditPopup);
                        }

                        EventBus.getDefault().post(new UpdateFile());
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
                    editFrame.findViewById(R.id.ok_social).setVisibility(View.GONE);
                    editFrame.findViewById(R.id.get_last_clips).setVisibility(View.VISIBLE);

                    if (contact.listOfContacts == null || contact.listOfContacts.isEmpty()) {
                        ((EditText) editFrame.findViewById(R.id.dataToEdit)).setPadding(0, 0, 150, 0);
                        editFrame.findViewById(R.id.imageEditSocial).setVisibility(View.VISIBLE);
                        editFrame.findViewById(R.id.searchSocial).setVisibility(View.VISIBLE);
                        editFrame.findViewById(R.id.frameImageEditSocialInsta).setVisibility(View.GONE);

                        Drawable color = new ColorDrawable(Color.parseColor("#e2e5e8"));
                        Drawable image = getResources().getDrawable(R.drawable.icn_social_linked2);
                        LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
                        ((ImageView) editFrame.findViewById(R.id.imageEditSocial)).setImageDrawable(ld);
                        ((EditText) editFrame.findViewById(R.id.dataToEdit)).setHint("Add link, username or search");
                    } else {
                        ((EditText) editFrame.findViewById(R.id.dataToEdit)).setPadding(0, 0, 0, 0);
                        editFrame.findViewById(R.id.imageEditSocial).setVisibility(View.GONE);
                        editFrame.findViewById(R.id.searchSocial).setVisibility(View.GONE);
                        editFrame.findViewById(R.id.frameImageEditSocialInsta).setVisibility(View.GONE);
                        ((EditText) editFrame.findViewById(R.id.dataToEdit)).setHint("Add link or username");
                    }
                    ((ImageView) editFrame.findViewById(R.id.iconDataEdit)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_social_linkedin));
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
                            //i.setData(Uri.parse("https://www.linkedin.com/mwlite/search/results/all/?keywords=" + mach[0] + "%20" + mach[1]));

                            //    if (contact.listOfContacts == null || contact.listOfContacts.isEmpty())
                            i.setData(Uri.parse("https://www.linkedin.com/mwlite/search/results/all/?keywords=" + mach[0] + "%20" + mach[1]));
                            //    else
                            //        i.setData(Uri.parse("https://www.linkedin.com/search/results/companies/v2/?keywords=" + mach[0] + "%20" + mach[1]));


                        } else {
                            //i.setData(Uri.parse("https://www.linkedin.com/mwlite/search/results/all/?keywords=" + mach[0]));

                            //    if (contact.listOfContacts == null || contact.listOfContacts.isEmpty())
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
                            //i.setData(Uri.parse("https://www.linkedin.com/mwlite/search/results/all/?keywords=" + mach[0] + "%20" + mach[1]));

                            //    if (contact.listOfContacts == null || contact.listOfContacts.isEmpty())
                            i.setData(Uri.parse("https://www.linkedin.com/mwlite/search/results/all/?keywords=" + mach[0] + "%20" + mach[1]));
                            //    else
                            //        i.setData(Uri.parse("https://www.linkedin.com/search/results/companies/v2/?keywords=" + mach[0] + "%20" + mach[1]));

                        } else {
                            //i.setData(Uri.parse("https://www.linkedin.com/mwlite/search/results/all/?keywords=" + mach[0]));

                            //    if (contact.listOfContacts == null || contact.listOfContacts.isEmpty())
                            i.setData(Uri.parse("https://www.linkedin.com/mwlite/search/results/all/?keywords=" + mach[0]));
                            //    else
                            //        i.setData(Uri.parse("https://www.linkedin.com/search/results/companies/v2/?keywords=" + mach[0]));

                        }

                        startActivity(i);
                    }
                });


                editFrame.findViewById(R.id.imageEditSocial).setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        System.out.println("CLICK WATS ANIM");

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

                openedViews.add(editFrame);
                ((TextView) editFrame.findViewById(R.id.typeField)).setText("LinkedIn");
                //((EditText) editFrame.findViewById(R.id.dataToEdit)).setHint("Enter link");


                editFrame.findViewById(R.id.updateContactField).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        Realm realm = Realm.getDefaultInstance(); //-
                        realm.beginTransaction();
                        //SocialModel socialModel;
                        if (contact.getSocialModel() == null) {
                            socialModel = realm.createObject(SocialModel.class);
                        } else
                            socialModel = contact.getSocialModel();

                        contact.setSocialModel(socialModel);

                        if (((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().length() > 0) {

                            if (socialModel.getLinkedInLink() != null && !socialModel.getLinkedInLink().isEmpty()) {
                                contactsService.deleteNoteContact(contact.getIdContact(), socialModel.getLinkedInLink());
                            }

                            String f = ((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().toString();

                            if (!f.contains(".com/")) {
                                f = "https://www.linkedin.com/in/" + f;
                            }

                            if (ClipboardType.isLinkedIn(f)) {
                                contact.getSocialModel().setLinkedInLink(f);
                            } else {
                                contact.getSocialModel().setLinkedInLink(((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().toString());
                            }


                            //contact.getSocialModel().setLinkedInLink(((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().toString());
                            //      ((ImageView) socialPopup.findViewById(R.id.link_icon)).setImageResource(R.drawable.icn_social_linkedin);
                            //      ((ImageView) getActivity().findViewById(R.id.link_icon)).setImageResource(R.drawable.icn_social_linkedin);
                            Drawable colorl = new ColorDrawable(Color.parseColor("#0077B7"));
                            Drawable imagel = getResources().getDrawable(R.drawable.icn_social_linked2);
                            LayerDrawable ldl = new LayerDrawable(new Drawable[]{colorl, imagel});
                            ((ImageView) socialPopup.findViewById(R.id.link_icon)).setImageDrawable(ldl);
                            //((ImageView) getActivity().findViewById(R.id.link_icon)).setImageDrawable(ldl);


                            try {
                                ((ImageView) profilePopUp.findViewById(R.id.link_icon)).setImageDrawable(ldl);
                            } catch (Exception e) {

                            }

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
                                ((TextView) socialPopup.findViewById(R.id.link_text)).setText(socialModel.getLinkedInLink());
                            }

                            contact.hasLinked = true;
                            contact.setSocialModel(socialModel);
                            realm.commitTransaction();
                            contactsService.addNoteToContact(contact.getIdContact(), contact.getSocialModel().getLinkedInLink(), contact.getName());
                            ContactCacheService.updateContact(contact, mainView.getContext());
                            //EventBus.getDefault().post(new UpdateFile());
                            editFrame.setVisibility(View.GONE);
                        } else {
                            if (socialModel.getLinkedInLink() != null && !socialModel.getLinkedInLink().isEmpty()) {
                                contactsService.deleteNoteContact(contact.getIdContact(), socialModel.getLinkedInLink());
                            }
                            contact.getSocialModel().setLinkedInLink(null);
                            //    ((ImageView) socialPopup.findViewById(R.id.link_icon)).setImageResource(R.drawable.icn_social_linkedin_gray);
                            //    ((ImageView) getActivity().findViewById(R.id.link_icon)).setImageResource(R.drawable.icn_social_linkedin_gray);
                            Drawable color3 = new ColorDrawable(Color.parseColor("#e2e5e8"));
                            Drawable image3 = getResources().getDrawable(R.drawable.icn_social_linked2);
                            LayerDrawable ld3 = new LayerDrawable(new Drawable[]{color3, image3});
                            ((ImageView) socialPopup.findViewById(R.id.link_icon)).setImageDrawable(ld3);
                            ((ImageView) getActivity().findViewById(R.id.link_icon)).setImageDrawable(ld3);

                            ((TextView) socialPopup.findViewById(R.id.link_text)).setText("add new link");
                            contact.hasLinked = false;
                            contact.setSocialModel(socialModel);
                            realm.commitTransaction();
                            ContactCacheService.updateContact(contact, mainView.getContext());
                            //EventBus.getDefault().post(new UpdateFile());
                            editFrame.setVisibility(View.GONE);
                        }

                        realm.close();

                        if (profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE) {
                            initIconColor(contact, profilePopUp);
                        } else if (fastEditPopup != null && fastEditPopup.getVisibility() == View.VISIBLE) {
                            initIconColor(contact, fastEditPopup);
                        }

                        EventBus.getDefault().post(new UpdateFile());
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
                    editFrame.findViewById(R.id.ok_social).setVisibility(View.GONE);
                    editFrame.findViewById(R.id.get_last_clips).setVisibility(View.VISIBLE);

                    if (contact.listOfContacts == null || contact.listOfContacts.isEmpty()) {
                        ((EditText) editFrame.findViewById(R.id.dataToEdit)).setPadding(0, 0, 150, 0);
                        editFrame.findViewById(R.id.imageEditSocial).setVisibility(View.GONE);
                        editFrame.findViewById(R.id.searchSocial).setVisibility(View.VISIBLE);
                        editFrame.findViewById(R.id.frameImageEditSocialInsta).setVisibility(View.VISIBLE);

                        Drawable color = new ColorDrawable(Color.parseColor("#e2e5e8"));
                        Drawable image = getResources().getDrawable(R.drawable.icn_social_ints2);
                        LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
                        ((ImageView) editFrame.findViewById(R.id.imageEditSocialInsta)).setImageDrawable(ld);
                        ((EditText) editFrame.findViewById(R.id.dataToEdit)).setHint("Add link, username or search");
                    } else {
                        ((EditText) editFrame.findViewById(R.id.dataToEdit)).setPadding(0, 0, 0, 0);
                        editFrame.findViewById(R.id.imageEditSocial).setVisibility(View.GONE);
                        editFrame.findViewById(R.id.searchSocial).setVisibility(View.GONE);
                        editFrame.findViewById(R.id.frameImageEditSocialInsta).setVisibility(View.GONE);
                        ((EditText) editFrame.findViewById(R.id.dataToEdit)).setHint("Add link or username");
                    }
                    ((ImageView) editFrame.findViewById(R.id.iconDataEdit)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_social_instagram));
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
                        System.out.println("CLICK WATS ANIM");

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

                openedViews.add(editFrame);
                ((TextView) editFrame.findViewById(R.id.typeField)).setText("Instagram");
                //((EditText) editFrame.findViewById(R.id.dataToEdit)).setHint("Enter username");

                editFrame.findViewById(R.id.updateContactField).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        Realm realm = Realm.getDefaultInstance(); //-
                        realm.beginTransaction();
                        //SocialModel socialModel;
                        if (contact.getSocialModel() == null) {
                            socialModel = realm.createObject(SocialModel.class);
                        } else
                            socialModel = contact.getSocialModel();

                        contact.setSocialModel(socialModel);
                        if (((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().length() > 0) {

                            if (socialModel.getInstagramLink() != null && !socialModel.getInstagramLink().isEmpty()) {
                                contactsService.deleteNoteContact(contact.getIdContact(), socialModel.getInstagramLink());
                            }

                            String username = ((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().toString();

                            if (username.contains("?utm")) {
                                int ind = username.indexOf('?');
                                if (ind != -1)
                                    username = username.substring(0, ind);

                            }

                            if (!username.toLowerCase().contains("instagram.com")) {
                                username = "https://instagram.com/" + username;
                            }

                            socialModel.setInstagramLink(username);
                            //      ((ImageView) socialPopup.findViewById(R.id.instagram_icon)).setImageResource(R.drawable.icn_social_instagram);
                            Drawable colori = new ColorDrawable(Color.parseColor("#8a3ab9"));
                            Drawable imagei = getResources().getDrawable(R.drawable.icn_social_ints2);
                            LayerDrawable ldi = new LayerDrawable(new Drawable[]{colori, imagei});
                            ((ImageView) socialPopup.findViewById(R.id.instagram_icon)).setImageDrawable(ldi);
                            ((ImageView) getActivity().findViewById(R.id.inst_icon)).setImageDrawable(ldi);

                            try {
                                ((ImageView) profilePopUp.findViewById(R.id.inst_icon)).setImageDrawable(ldi);
                            } catch (Exception e) {

                            }


                            contact.hasInst = true;
                            contact.setSocialModel(socialModel);
                            realm.commitTransaction();

                            String inst = contact.getSocialModel().getInstagramLink();

                            if (inst.contains(".com/")) {
                                int ind = inst.indexOf(".com/");
                                String outLink = inst.substring(ind + 5, inst.length());
                                ((TextView) socialPopup.findViewById(R.id.instagramNick)).setText(outLink);
                            } else
                                ((TextView) socialPopup.findViewById(R.id.instagramNick)).setText(contact.getSocialModel().getInstagramLink());

                            contactsService.addNoteToContact(contact.getIdContact(), contact.getSocialModel().getInstagramLink(), contact.getName());
                            ContactCacheService.updateContact(contact, mainView.getContext());
                            //EventBus.getDefault().post(new UpdateFile());
                            editFrame.setVisibility(View.GONE);
                        } else {
                            if (socialModel.getInstagramLink() != null && !socialModel.getInstagramLink().isEmpty()) {
                                contactsService.deleteNoteContact(contact.getIdContact(), socialModel.getInstagramLink());
                            }
                            contact.getSocialModel().setInstagramLink(null);
                            //   ((ImageView) socialPopup.findViewById(R.id.instagram_icon)).setImageResource(R.drawable.icn_social_instagram_gray);
                            //   ((ImageView) getActivity().findViewById(R.id.instagram_icon)).setImageResource(R.drawable.icn_social_instagram_gray);
                            Drawable color4 = new ColorDrawable(Color.parseColor("#e2e5e8"));
                            Drawable image4 = getResources().getDrawable(R.drawable.icn_social_ints2);
                            LayerDrawable ld4 = new LayerDrawable(new Drawable[]{color4, image4});
                            ((ImageView) socialPopup.findViewById(R.id.instagram_icon)).setImageDrawable(ld4);
                            ((ImageView) getActivity().findViewById(R.id.inst_icon)).setImageDrawable(ld4);

                            ((TextView) socialPopup.findViewById(R.id.instagramNick)).setText("add new link");
                            contact.hasInst = false;
                            contact.setSocialModel(socialModel);
                            realm.commitTransaction();
                            ContactCacheService.updateContact(contact, mainView.getContext());
                            //EventBus.getDefault().post(new UpdateFile());
                            editFrame.setVisibility(View.GONE);
                        }

                        realm.close();

                        if (profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE) {
                            initIconColor(contact, profilePopUp);
                        } else if (fastEditPopup != null && fastEditPopup.getVisibility() == View.VISIBLE) {
                            initIconColor(contact, fastEditPopup);
                        }

                        EventBus.getDefault().post(new UpdateFile());

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

                    ((ImageView) editFrame.findViewById(R.id.iconDataEdit)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_social_viber));
                    editFrame.findViewById(R.id.frameImageEditSocialInsta).setVisibility(View.GONE);
                    editFrame.findViewById(R.id.imageEditSocial).setVisibility(View.GONE);
                    editFrame.findViewById(R.id.get_last_clips).setVisibility(View.VISIBLE);
                    editFrame.findViewById(R.id.searchSocial).setVisibility(View.GONE);
                    //editFrame.findViewById(R.id.ok_social).setVisibility(View.VISIBLE);
                }

                editFrame.findViewById(R.id.get_last_clips).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String socialLinkClip = "";
                        for (Clipboard cl : FabNotificationService.clipboard) { //NullPointerException
                            if (cl.getListClipboards() != null && !cl.getListClipboards().isEmpty()) {
                                boolean checkFind = false;
                                for (Clipboard cl2 : cl.getListClipboards()) {
                                    if (cl2.getType().equals(ClipboardEnum.VIBER) || cl2.getType().equals(ClipboardEnum.PHONE)) {
                                        socialLinkClip = cl2.getValueCopy();
                                        checkFind = true;
                                        break;
                                    }
                                }
                                if (checkFind) break;
                            } else {
                                if (cl.getType().equals(ClipboardEnum.VIBER)) {
                                    socialLinkClip = cl.getValueCopy();
                                    break;
                                }
                            }
                        }
                        ((EditText) editFrame.findViewById(R.id.dataToEdit)).setText(socialLinkClip);

                    }
                });


                openedViews.add(editFrame);
                ((TextView) editFrame.findViewById(R.id.typeField)).setText("Viber");
                ((EditText) editFrame.findViewById(R.id.dataToEdit)).setHint("Enter mobile number");

                editFrame.findViewById(R.id.updateContactField).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Realm realm = Realm.getDefaultInstance(); //-
                        realm.beginTransaction();
                        //SocialModel socialModel;
                        if (contact.getSocialModel() == null) {
                            socialModel = realm.createObject(SocialModel.class);
                        } else
                            socialModel = contact.getSocialModel();

                        contact.setSocialModel(socialModel);

                        if (((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().length() > 0) {

                            if (socialModel.getViberLink() != null && !socialModel.getViberLink().isEmpty()) {
                                contactsService.deleteNoteContact(contact.getIdContact(), socialModel.getViberLink());
                            }

                            contact.getSocialModel().setViberLink(((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().toString());
                            ((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().clear();

                            //    ((ImageView) socialPopup.findViewById(R.id.viber_icon)).setImageResource(R.drawable.icn_social_viber);

                            Drawable colorvi = new ColorDrawable(Color.parseColor("#6F3FAA"));
                            Drawable imagevi = getResources().getDrawable(R.drawable.icn_social_viber2);
                            LayerDrawable ldvi = new LayerDrawable(new Drawable[]{colorvi, imagevi});
                            ((CircleImageView) socialPopup.findViewById(R.id.viber_icon)).setImageDrawable(ldvi);
                            //((CircleImageView) getActivity().findViewById(R.id.viber_icon)).setImageDrawable(ldvi);

                            try {
                                ((CircleImageView) profilePopUp.findViewById(R.id.viber_icon)).setImageDrawable(ldvi);
                            } catch (Exception e) {

                            }

                            ((TextView) socialPopup.findViewById(R.id.viber_text)).setText(contact.getSocialModel().getViberLink());
                            contact.hasViber = true;
                            contact.setSocialModel(socialModel);
                            realm.commitTransaction();
                            contactsService.addNoteToContact(contact.getIdContact(), contact.getSocialModel().getViberLink(), contact.getName());
                            ContactCacheService.updateContact(contact, mainView.getContext());
                            //EventBus.getDefault().post(new UpdateFile());
                            editFrame.setVisibility(View.GONE);
                        } else {
                            if (socialModel.getViberLink() != null && !socialModel.getViberLink().isEmpty()) {
                                contactsService.deleteNoteContact(contact.getIdContact(), socialModel.getViberLink());
                            }
                            contact.getSocialModel().setViberLink(null);
                            ((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().clear();
                            //         ((ImageView) socialPopup.findViewById(R.id.viber_icon)).setImageResource(R.drawable.icn_social_viber_gray);
                            //         ((ImageView) getActivity().findViewById(R.id.viber_icon)).setImageResource(R.drawable.icn_social_viber_gray);
                            Drawable color5 = new ColorDrawable(Color.parseColor("#e2e5e8"));
                            Drawable image5 = getResources().getDrawable(R.drawable.icn_social_viber2_gray);
                            LayerDrawable ld5 = new LayerDrawable(new Drawable[]{color5, image5});
                            ((CircleImageView) socialPopup.findViewById(R.id.viber_icon)).setImageDrawable(ld5);
                            ((CircleImageView) getActivity().findViewById(R.id.viber_icon)).setImageDrawable(ld5);

                            ((TextView) socialPopup.findViewById(R.id.viber_text)).setText("add new link");
                            contact.hasViber = false;
                            contact.setSocialModel(socialModel);
                            realm.commitTransaction();
                            ContactCacheService.updateContact(contact, mainView.getContext());
                            //EventBus.getDefault().post(new UpdateFile());
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

                        EventBus.getDefault().post(new UpdateFile());
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
                    ((ImageView) editFrame.findViewById(R.id.iconDataEdit)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_social_whatsapp));
                    editFrame.findViewById(R.id.imageEditSocial).setVisibility(View.GONE);
                    editFrame.findViewById(R.id.searchSocial).setVisibility(View.GONE);
                    editFrame.findViewById(R.id.get_last_clips).setVisibility(View.VISIBLE);
                    editFrame.findViewById(R.id.frameImageEditSocialInsta).setVisibility(View.GONE);
                    //editFrame.findViewById(R.id.ok_social).setVisibility(View.VISIBLE);
                }

                openedViews.add(editFrame);
                ((TextView) editFrame.findViewById(R.id.typeField)).setText("Whatsapp");
                ((EditText) editFrame.findViewById(R.id.dataToEdit)).setHint("Enter mobile number");

                editFrame.findViewById(R.id.get_last_clips).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String socialLinkClip = "";
                        for (Clipboard cl : FabNotificationService.clipboard) { //NullPointerException
                            if (cl.getListClipboards() != null && !cl.getListClipboards().isEmpty()) {
                                boolean checkFind = false;
                                for (Clipboard cl2 : cl.getListClipboards()) {
                                    if (cl2.getType().equals(ClipboardEnum.WHATSAPP)) {
                                        socialLinkClip = cl2.getValueCopy();
                                        checkFind = true;
                                        break;
                                    }
                                }
                                if (checkFind) break;
                            } else {
                                if (cl.getType().equals(ClipboardEnum.WHATSAPP)) {
                                    socialLinkClip = cl.getValueCopy();
                                    break;
                                }
                            }
                        }
                        ((EditText) editFrame.findViewById(R.id.dataToEdit)).setText(socialLinkClip);
                    }
                });

                editFrame.findViewById(R.id.updateContactField).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        Realm realm = Realm.getDefaultInstance(); //-
                        realm.beginTransaction();
                        //SocialModel socialModel;
                        if (contact.getSocialModel() == null) {
                            socialModel = realm.createObject(SocialModel.class);
                        } else
                            socialModel = contact.getSocialModel();

                        contact.setSocialModel(socialModel);
                        if (((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().length() > 0) {

                            if (socialModel.getWhatsappLink() != null && !socialModel.getWhatsappLink().isEmpty()) {
                                contactsService.deleteNoteContact(contact.getIdContact(), socialModel.getWhatsappLink());
                            }

                            contact.getSocialModel().setWhatsappLink(((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().toString());
                            ((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().clear();
                            //    ((ImageView) socialPopup.findViewById(R.id.whatsapp_icon)).setImageResource(R.drawable.icn_social_whatsapp);
                            Drawable colorw = new ColorDrawable(Color.parseColor("#75B73B"));
                            Drawable imagew = getResources().getDrawable(R.drawable.icn_social_whatsapp3);
                            LayerDrawable ldw = new LayerDrawable(new Drawable[]{colorw, imagew});
                            ((CircleImageView) socialPopup.findViewById(R.id.whatsapp_icon)).setImageDrawable(ldw);
                            //((CircleImageView) getActivity().findViewById(R.id.whatsapp_icon)).setImageDrawable(ldw);

                            try {
                                ((CircleImageView) profilePopUp.findViewById(R.id.whatsapp_icon)).setImageDrawable(ldw);
                            } catch (Exception e) {

                            }

                            ((TextView) socialPopup.findViewById(R.id.whatsapp_text)).setText(contact.getSocialModel().getWhatsappLink());
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
                            contact.getSocialModel().setWhatsappLink(null);
                            ((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().clear();
                            //    ((ImageView) socialPopup.findViewById(R.id.whatsapp_icon)).setImageResource(R.drawable.icn_social_whatsapp_gray);
                            //    ((ImageView) getActivity().findViewById(R.id.whatsapp_icon)).setImageResource(R.drawable.icn_social_whatsapp_gray);
                            Drawable color6 = new ColorDrawable(Color.parseColor("#e2e5e8"));
                            Drawable image6 = getResources().getDrawable(R.drawable.icn_social_whatsapp3_gray);
                            LayerDrawable ld6 = new LayerDrawable(new Drawable[]{color6, image6});
                            ((CircleImageView) socialPopup.findViewById(R.id.whatsapp_icon)).setImageDrawable(ld6);
                            ((CircleImageView) getActivity().findViewById(R.id.whatsapp_icon)).setImageDrawable(ld6);

                            ((TextView) socialPopup.findViewById(R.id.whatsapp_text)).setText("add new link");
                            contact.hasWhatsapp = false;
                            contact.setSocialModel(socialModel);
                            realm.commitTransaction();
                            ContactCacheService.updateContact(contact, mainView.getContext());
                            //EventBus.getDefault().post(new UpdateFile());
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

                        EventBus.getDefault().post(new UpdateFile());
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
                    ((ImageView) editFrame.findViewById(R.id.iconDataEdit)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_social_telegram));
                    editFrame.findViewById(R.id.imageEditSocial).setVisibility(View.GONE);
                    editFrame.findViewById(R.id.searchSocial).setVisibility(View.GONE);
                    editFrame.findViewById(R.id.get_last_clips).setVisibility(View.VISIBLE);
                    editFrame.findViewById(R.id.frameImageEditSocialInsta).setVisibility(View.GONE);
                    //editFrame.findViewById(R.id.ok_social).setVisibility(View.VISIBLE);
                }

                editFrame.findViewById(R.id.get_last_clips).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String socialLinkClip = "";
                        for (Clipboard cl : FabNotificationService.clipboard) { //NullPointerException
                            if (cl.getListClipboards() != null && !cl.getListClipboards().isEmpty()) {
                                boolean checkFind = false;
                                for (Clipboard cl2 : cl.getListClipboards()) {
                                    if (cl2.getType().equals(ClipboardEnum.TELEGRAM)) {
                                        socialLinkClip = cl2.getValueCopy();
                                        checkFind = true;
                                        break;
                                    }
                                }
                                if (checkFind) break;
                            } else {
                                if (cl.getType().equals(ClipboardEnum.TELEGRAM)) {
                                    socialLinkClip = cl.getValueCopy();
                                    break;
                                }
                            }
                        }
                        ((EditText) editFrame.findViewById(R.id.dataToEdit)).setText(socialLinkClip);
                    }
                });

                openedViews.add(editFrame);
                ((TextView) editFrame.findViewById(R.id.typeField)).setText("Telegram");
                ((EditText) editFrame.findViewById(R.id.dataToEdit)).setHint("Enter username or mobile number");

                editFrame.findViewById(R.id.updateContactField).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        Realm realm = Realm.getDefaultInstance(); //-
                        realm.beginTransaction();
                        //SocialModel socialModel;
                        if (contact.getSocialModel() == null) {
                            socialModel = realm.createObject(SocialModel.class);
                        } else
                            socialModel = contact.getSocialModel();

                        contact.setSocialModel(socialModel);
                        if (((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().length() > 0) {

                            if (socialModel.getTelegramLink() != null && !socialModel.getTelegramLink().isEmpty()) {
                                contactsService.deleteNoteContact(contact.getIdContact(), socialModel.getTelegramLink());
                            }

                            String username = ((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().toString();
                            char firstSymbol = username.charAt(0);
                            String regex = "[0-9]+";
                            username = username.replaceAll("[-() ]", "");
                            if (((firstSymbol == '@' && !username.substring(1).matches(regex)) || (firstSymbol != '@' && !username.matches(regex))) && !username.contains("t.me/")) {
                                username = "t.me/" + username;
                            }

                            contact.getSocialModel().setTelegramLink(username);
                            ((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().clear();
                            //  ((ImageView) socialPopup.findViewById(R.id.telegram_icon)).setImageResource(R.drawable.icn_social_telegram);

                            Drawable colort = new ColorDrawable(Color.parseColor("#7AA5DA"));
                            Drawable imaget = getResources().getDrawable(R.drawable.icn_social_telegram2);
                            LayerDrawable ldt = new LayerDrawable(new Drawable[]{colort, imaget});
                            ((CircleImageView) socialPopup.findViewById(R.id.telegram_icon)).setImageDrawable(ldt);
                            //((CircleImageView) getActivity().findViewById(R.id.telegram_icon)).setImageDrawable(ldt);

                            try {
                                ((CircleImageView) profilePopUp.findViewById(R.id.telegram_icon)).setImageDrawable(ldt);
                            } catch (Exception e) {

                            }


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
                            //EventBus.getDefault().post(new UpdateFile());
                            editFrame.setVisibility(View.GONE);
                        } else {
                            if (socialModel.getTelegramLink() != null && !socialModel.getTelegramLink().isEmpty()) {
                                contactsService.deleteNoteContact(contact.getIdContact(), socialModel.getTelegramLink());
                            }
                            contact.getSocialModel().setTelegramLink(null);
                            ((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().clear();
                            //   ((ImageView) socialPopup.findViewById(R.id.telegram_icon)).setImageResource(R.drawable.icn_social_telegram_gray);
                            Drawable color7 = new ColorDrawable(Color.parseColor("#e2e5e8"));
                            Drawable image7 = getResources().getDrawable(R.drawable.icn_social_telegram2);
                            LayerDrawable ld7 = new LayerDrawable(new Drawable[]{color7, image7});
                            ((CircleImageView) socialPopup.findViewById(R.id.telegram_icon)).setImageDrawable(ld7);
                            ((CircleImageView) getActivity().findViewById(R.id.telegram_icon)).setImageDrawable(ld7);

                            ((TextView) socialPopup.findViewById(R.id.telegram_text)).setText("add new link");
                            contact.hasTelegram = false;
                            contact.setSocialModel(socialModel);
                            realm.commitTransaction();
                            ContactCacheService.updateContact(contact, mainView.getContext());
                            // EventBus.getDefault().post(new UpdateFile());
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

                        EventBus.getDefault().post(new UpdateFile());
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

                ((EditText) editFrame.findViewById(R.id.dataToEdit)).setText("");
                if (contact.getSocialModel() != null)
                    if (contact.getSocialModel().getSkypeLink() != null)
                        ((EditText) editFrame.findViewById(R.id.dataToEdit)).setText(contact.getSocialModel().getSkypeLink());

                if (contact.getSocialModel() != null && contact.getSocialModel().getSkypeLink() != null)
                    showEditPopupPreviewSocial(contact, SocialEnums.SKYPE);
                else {
                    editFrame.setVisibility(View.VISIBLE);

                    ((EditText) editFrame.findViewById(R.id.dataToEdit)).setPadding(0, 0, 0, 0);
                    ((ImageView) editFrame.findViewById(R.id.iconDataEdit)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_social_skype));
                    editFrame.findViewById(R.id.imageEditSocial).setVisibility(View.GONE);
                    editFrame.findViewById(R.id.searchSocial).setVisibility(View.GONE);
                    editFrame.findViewById(R.id.get_last_clips).setVisibility(View.VISIBLE);
                    editFrame.findViewById(R.id.frameImageEditSocialInsta).setVisibility(View.GONE);
                    //editFrame.findViewById(R.id.ok_social).setVisibility(View.VISIBLE);
                }

                editFrame.findViewById(R.id.get_last_clips).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String socialLinkClip = "";

                        /*for (Clipboard cl : FabNotificationService.clipboard) { //NullPointerException
                            if (cl.getListClipboards() != null && !cl.getListClipboards().isEmpty()) {
                                boolean checkFind = false;
                                for (Clipboard cl2 : cl.getListClipboards()) {
                                    if (cl2.getType().equals(ClipboardEnum.SKYPE)) {
                                        socialLinkClip = cl2.getValueCopy();
                                        checkFind = true;
                                        break;
                                    }
                                }
                                if (checkFind) break;
                            } else {
                                if (cl.getType().equals(ClipboardEnum.SKYPE)) {
                                    socialLinkClip = cl.getValueCopy();
                                    break;
                                }
                            }
                        }*/

                        ((EditText) editFrame.findViewById(R.id.dataToEdit)).setText(FabNotificationService.clipboard.get(0).getValueCopy());
                    }
                });

                openedViews.add(editFrame);

                ((TextView) editFrame.findViewById(R.id.typeField)).setText("Skype");
                ((EditText) editFrame.findViewById(R.id.dataToEdit)).setHint("Enter username");

                editFrame.findViewById(R.id.updateContactField).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        Realm realm = Realm.getDefaultInstance(); //-
                        realm.beginTransaction();
                        //SocialModel socialModel;
                        if (contact.getSocialModel() == null) {
                            socialModel = realm.createObject(SocialModel.class);
                        } else
                            socialModel = contact.getSocialModel();

                        contact.setSocialModel(socialModel);
                        if (((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().length() > 0) {

                            if (socialModel.getSkypeLink() != null && !socialModel.getSkypeLink().isEmpty()) {
                                contactsService.deleteNoteContact(contact.getIdContact(), "Skype: " + socialModel.getSkypeLink());
                            }

                            contact.getSocialModel().setSkypeLink(((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().toString());

                            ((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().clear();
                            //      ((ImageView) socialPopup.findViewById(R.id.skype_icon)).setImageResource(R.drawable.icn_social_skype);
                            Drawable colors = new ColorDrawable(Color.parseColor("#1eb8ff"));
                            Drawable images = getResources().getDrawable(R.drawable.icn_social_skype2);
                            LayerDrawable lds = new LayerDrawable(new Drawable[]{colors, images});
                            ((CircleImageView) socialPopup.findViewById(R.id.skype_icon)).setImageDrawable(lds);
                            //((CircleImageView) getActivity().findViewById(R.id.skype_icon)).setImageDrawable(lds);

                            try {
                                ((ImageView) profilePopUp.findViewById(R.id.skype_icon)).setImageDrawable(lds);
                            } catch (Exception e) {

                            }

                            ((TextView) socialPopup.findViewById(R.id.skype_text)).setText(contact.getSocialModel().getSkypeLink());
                            contact.hasSkype = true;
                            contact.setSocialModel(socialModel);
                            realm.commitTransaction();
                            contactsService.addNoteToContact(contact.getIdContact(), "Skype: " + contact.getSocialModel().getSkypeLink(), contact.getName());
                            ContactCacheService.updateContact(contact, mainView.getContext());
                            //EventBus.getDefault().post(new UpdateFile());
                            editFrame.setVisibility(View.GONE);
                        } else {
                            if (socialModel.getSkypeLink() != null && !socialModel.getSkypeLink().isEmpty()) {
                                contactsService.deleteNoteContact(contact.getIdContact(), "Skype: " + socialModel.getSkypeLink());
                            }
                            contact.getSocialModel().setSkypeLink(null);
                            ((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().clear();
                            //    ((ImageView) socialPopup.findViewById(R.id.skype_icon)).setImageResource(R.drawable.icn_social_skype_gray);
                            Drawable color8 = new ColorDrawable(Color.parseColor("#e2e5e8"));
                            Drawable image8 = getResources().getDrawable(R.drawable.icn_social_skype2_gray);
                            LayerDrawable ld8 = new LayerDrawable(new Drawable[]{color8, image8});
                            ((CircleImageView) socialPopup.findViewById(R.id.skype_icon)).setImageDrawable(ld8);
                            ((CircleImageView) getActivity().findViewById(R.id.skype_icon)).setImageDrawable(ld8);

                            ((TextView) socialPopup.findViewById(R.id.skype_text)).setText("add new link");
                            contact.hasSkype = false;
                            contact.setSocialModel(socialModel);
                            realm.commitTransaction();
                            ContactCacheService.updateContact(contact, mainView.getContext());
                            // EventBus.getDefault().post(new UpdateFile());
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

                        EventBus.getDefault().post(new UpdateFile());
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
                closeOtherPopup();
                //   initIconColor(selectedContact,mainView);
                socialPopup.setVisibility(View.GONE);
                showProfilePopUp(contact);
            }
        });
        socialPopup.setVisibility(View.VISIBLE);
        openedViews.add(socialPopup);

    }

    public void addView(View view) {
        if (openedViews == null)
            openedViews = new ArrayList<>();
        if (view != null)
            openedViews.add(view);
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
    public void onPause() {
        System.out.println("PAUSE COMPANYS");
        super.onPause();
    }

    @Override
    public void onStop() {
        System.out.println("STOP COMPANYS");
        closeOtherPopup();
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void addHashtagsToSelectedContacts(String hashtag) {
        if (!selectedContact.getListOfHashtags().contains(new HashTag(hashtag))) {
            selectedContact.getListOfHashtags().add(new HashTag(hashtag.trim()));
            ContactCacheService.updateContact(selectedContact, profilePopUp.getContext());
            MainActivity.typeUpdate = "EDIT";
            MainActivity.nameToUpd.clear();
            MainActivity.nameToUpd.add(selectedContact.getName());
            if (selectedContact.listOfContacts == null || selectedContact.listOfContacts.isEmpty())
                MainActivity.nameToUpdTypeContact.add(true);
            else
                MainActivity.nameToUpdTypeContact.add(false);
            EventBus.getDefault().post(new UpdateFile());
            Toast.makeText(profilePopUp.getContext(), "Hashtag successfully added", Toast.LENGTH_SHORT).show();
        } else {
            Toast toast = Toast.makeText(profilePopUp.getContext(), "Hashtag already  exist", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    public void addNewTagToContact(String hashtag, Contact contact) {
        for (HashTag hashTag : contact.getListOfHashtags()) {
            if (hashTag.getHashTagValue().compareTo(hashtag) == 0) {
                Toast toast = Toast.makeText(profilePopUp.getContext(), "Hashtag already  exist", Toast.LENGTH_SHORT);
                toast.show();
                return;
            }
        }

        if (popupUserHashtags.findViewById(R.id.ContactHashLinear).getVisibility() == View.VISIBLE) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    getActivity());
            alertDialogBuilder.setTitle("Do you want to add " + hashtag + " ?");
            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialog, id) -> {
                        customTagsAdapter.addHashTag(hashtag);


                        contactsService.addNoteToContact(contact.getIdContact(), hashtag.trim().toString(), contact.getName());
                        Realm realm = Realm.getDefaultInstance(); //-
                        realm.beginTransaction();
                        contact.getListOfHashtags().add(new HashTag(hashtag.trim()));
                        realm.commitTransaction();
                        realm.close();
                        ContactCacheService.updateContact(contact, profilePopUp.getContext());

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
                                text.setOnClickListener(v -> contactAdapter.searchByHashTagValue(hashTag.getHashTagValue()));
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
                            ((EditText) popupUserHashtags.findViewById(R.id.hashtagsList)).append(hashtag.trim() + " ");
                        }


                        EventBus.getDefault().post(new UpdateFile());
                        Toast.makeText(profilePopUp.getContext(), "Hashtag successfully added", Toast.LENGTH_SHORT).show();
                        ContactsFragment.UPD_ALL = true;
                        addHashtagPreview(hashtag);
                    })
                    .setNegativeButton("No", (dialog, id) -> dialog.cancel());

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

        } else
            ((EditText) popupUserHashtags.findViewById(R.id.hashtagsList)).append(hashtag.trim() + " ");
    }

    @Override
    public void deleteHashTagsFromUser(String hashtag, Contact contact) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getActivity());
        alertDialogBuilder.setTitle("Do you want to delete " + hashtag + " ?");
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> {
                    RealmList<HashTag> listOfHashtags = selectedContactPopup.getListOfHashtags();

                    Realm realm = Realm.getDefaultInstance(); //-
                    realm.beginTransaction();
                    for (int indexI = 0; indexI < listOfHashtags.size(); indexI++) {
                        if (listOfHashtags.get(indexI).getHashTagValue().equals(hashtag.trim()))
                            listOfHashtags.remove(indexI);
                    }
                    selectedContactPopup.setListOfHashtags(listOfHashtags);
                    selectedContactPopup.getListOfHashtags().remove(new HashTag(hashtag));
                    realm.commitTransaction();
                    realm.close();

                    ContactCacheService.updateContact(selectedContact, profilePopUp.getContext());
                    EventBus.getDefault().post(new UpdateFile());
                    //closeOtherPopup();
                    LinearLayout containerHashTags = (LinearLayout) profilePopUp.findViewById(R.id.containerHashTags);
                    if (containerHashTags.getChildCount() > 0)
                        containerHashTags.removeAllViews();
                    for (HashTag hashTag : selectedContactPopup.getListOfHashtags()) {
                        TextView text = new TextView(getActivity());
                        text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                        text.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        text.setTextColor(getResources().getColor(R.color.colorPrimary));
                        text.setText(hashTag.getHashTagValue() + " ");
                        containerHashTags.addView(text);
                    }
                    showPopupUserHashtags(selectedContactPopup);
                })
                .setNegativeButton("No", (dialog, id) -> {
                    dialog.cancel();
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    @Override
    public void updatedUserHashtags(ArrayList<String> updatedHashtags) {

        Realm realm = Realm.getDefaultInstance();

        //    RealmList<HashTag> listOfHashtag = new RealmList<>();
        RealmList<HashTag> listOfHashtag = new RealmList<>();
        ArrayList<String> listForAdapter = new ArrayList<>();
        for (int indexI = 0; indexI < updatedHashtags.size(); indexI++) {
            String hashtag = updatedHashtags.get(indexI);
            if (!listOfHashtag.contains(new HashTag(hashtag)) && hashtag.trim().toString().length() > 0) {
                realm.beginTransaction();
                HashTag ht = realm.createObject(HashTag.class);

                ht.setHashTagValue(hashtag);
                realm.commitTransaction();
                listOfHashtag.add(ht);
                listForAdapter.add(hashtag);
            }
        }

        realm.beginTransaction();
        selectedContactPopup.setListOfHashtags(listOfHashtag);
        realm.commitTransaction();
        ContactCacheService.updateContact(selectedContactPopup, profilePopUp.getContext());
        ContactsFragment.UPD_ALL = true;
        System.out.println("SIZE HASH NEW = " + listForAdapter.size());
        customTagsAdapter.setNewContacts(listForAdapter);
        updateAllProfileTags();
        EventBus.getDefault().post(new UpdateFile());
    }

    private void updateAllProfileTags() {
        LinearLayout containerHashTags = (LinearLayout) profilePopUp.findViewById(R.id.containerHashTags);
        if (containerHashTags.getChildCount() > 0)
            containerHashTags.removeAllViews();
        for (HashTag hashTag : selectedContactPopup.getListOfHashtags()) {
            TextView text = new TextView(getActivity());
            text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            DisplayMetrics metrics = profilePopUp.getContext().getResources().getDisplayMetrics();
            text.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            text.setTextColor(getResources().getColor(R.color.colorPrimary));
            text.setText(hashTag.getHashTagValue() + " ");

            text.setOnClickListener(v -> contactAdapter.searchByHashTagValue(hashTag.getHashTagValue()));
            text.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    deleteHashTagsFromUser(text.getText().toString(), selectedContactPopup);
                    return true;
                }
            });
            containerHashTags.addView(text);
        }
    }


    private void addHashtagPreview(String hashtag) {

        LinearLayout containerHashTags = (LinearLayout) profilePopUp.findViewById(R.id.containerHashTags);
        TextView text = new TextView(getActivity());
        text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        text.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        text.setTextColor(getResources().getColor(R.color.colorPrimary));
        text.setText(hashtag + " ");
        containerHashTags.addView(text);
    }

    @Override
    public void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ClickBackOnProfile event) {

        //  System.out.println("UPDATE FILEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEES");
        //  System.out.println("SIZE UUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUU = "+MainActivity.LIST_TO_SAVE_OF_FILE.size());

        if (CompanyAdapter.removeCompany) {

            android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.popBackStack();
            CompanyAdapter.removeCompany = false;

        } else {
            companyAdapter.stopSelectionMode();
        }
        //  updateFile();
    }

    ;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(UpdateMessengersInCompany event) {

        updateMessengers(event.getContact());
    }


    public void updateMessengers(Contact contact) {

        ArrayList<String> list = contactsService.getContactPhones(contact.getIdContact());
        boolean check_phone = false;
        String phoneF = null;
        if (list != null && contact.getListOfContactInfo() != null) {
            for (ContactInfo p2 : contact.getListOfContactInfo()) {
                if (p2.type.equalsIgnoreCase("phone") || (p2.typeData != null && p2.typeData.equalsIgnoreCase("phone"))) {

                    String str = p2.value;
                    if (phoneF == null || phoneF.equals("+000000000000"))
                        phoneF = str;

                    str = str.replaceAll("[\\.\\s\\-\\+\\(\\)]", "");
                    for (String p : list) {

                        String str2 = p;
                        str2 = str2.replaceAll("[\\.\\s\\-\\+\\(\\)]", "");
                        if (str.equalsIgnoreCase(str2)) {
                            check_phone = true;
                            break;
                        }
                    }
                }
                if (check_phone) break;
            }
        }

        if (check_phone) {


            Realm realm = Realm.getDefaultInstance();
            boolean check2 = false;

            Cursor accountCursor = contactsService.getCursorByType(ContactsService.TYPE_CURSOR.ACCOUNT_TYPE, contact.getIdContact());
            if (accountCursor != null && accountCursor.getCount() > 0) {
                while (accountCursor.moveToNext()) {
                    int accountTypeI = accountCursor.getColumnIndex(ContactsContract.RawContacts.ACCOUNT_TYPE);
                    if (accountTypeI != -1) {
                        String accountType = accountCursor.getString(accountTypeI);
                        System.out.println("ACC = " + accountType);
                        //System.out.println("ACC TYPE = "+accountType);
                           /* System.out.println("ACC TYPE = "+ accountCursor.getString(1));
                            System.out.println("ACC TYPE = "+ accountCursor.getString(2));*/
                        if (accountType != null && accountType.contains("telegram") && (contact.getSocialModel() == null || (contact.getSocialModel() != null && (contact.getSocialModel().getTelegramLink() == null || contact.getSocialModel().getTelegramLink().isEmpty())))) {
                            realm.beginTransaction();
                            contact.hasTelegram = true;
                            contact.addAccountType(accountType);
                            if (contact.getSocialModel() == null) {
                                SocialModel sc = realm.createObject(SocialModel.class);
                                sc.setTelegramLink(phoneF);
                                contact.setSocialModel(sc);
                            } else contact.getSocialModel().setTelegramLink(phoneF);

                            check2 = true;
                            realm.commitTransaction();
                        }

                        if (accountType != null) {

                                /*try {
                                    if (accountType.contains("viber") && (contact.getSocialModel() == null || (contact.getSocialModel() != null && (contact.getSocialModel().getViberLink() == null || contact.getSocialModel().getViberLink().isEmpty()))) && !phoneF.equals("+000000000000")) {
                                        realm.beginTransaction();
                                        contact.hasViber = true;
                                        contact.addAccountType(accountType);
                                        if (contact.getSocialModel() == null) {
                                            SocialModel sc = realm.createObject(SocialModel.class);
                                            sc.setViberLink(phoneF);
                                            contact.setSocialModel(sc);
                                        } else contact.getSocialModel().setViberLink(phoneF);

                                        realm.commitTransaction();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }*/

                            try {
                                if (accountType.contains("whatsapp") && (contact.getSocialModel() == null || (contact.getSocialModel() != null && (contact.getSocialModel().getWhatsappLink() == null || contact.getSocialModel().getWhatsappLink().isEmpty()))) && !phoneF.equals("+000000000000")) {
                                    realm.beginTransaction();
                                    contact.hasWhatsapp = true;
                                    contact.addAccountType(accountType);
                                    if (contact.getSocialModel() == null) {
                                        SocialModel sc = realm.createObject(SocialModel.class);
                                        sc.setWhatsappLink(phoneF);
                                        contact.setSocialModel(sc);
                                    } else contact.getSocialModel().setWhatsappLink(phoneF);

                                    check2 = true;
                                    realm.commitTransaction();
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }
                }
                accountCursor.close();
            }

            realm.close();


            if (check2) initIconColor(contact, profilePopUp);
        }

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
    }

    public void shareIntent(String text) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(Intent.createChooser(shareIntent, "Share"));
    }


    public void showEditPopupPreviewSocial(Contact contact, SocialEnums typeEnum) {
        popupProfileEditPreviewSocial = (FrameLayout) getActivity().findViewById(R.id.popupPreviewEditSocial);


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
            ((ImageView) popupProfileEditPreviewSocial.findViewById(R.id.iconDataC)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_social_facebook));
        } else if (typeEnum.equals(SocialEnums.INSTAGRAM)) {
            ((TextView) popupProfileEditPreviewSocial.findViewById(R.id.valueUpd)).setText(contact.getSocialModel().getInstagramLink());
            ((TextView) popupProfileEditPreviewSocial.findViewById(R.id.typeField)).setText("Instagram");
            ((ImageView) popupProfileEditPreviewSocial.findViewById(R.id.imageCall)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_social_instagram));
            ((ImageView) popupProfileEditPreviewSocial.findViewById(R.id.iconDataC)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_social_instagram));
        } else if (typeEnum.equals(SocialEnums.VK)) {
            ((TextView) popupProfileEditPreviewSocial.findViewById(R.id.valueUpd)).setText(contact.getSocialModel().getVkLink());
            ((TextView) popupProfileEditPreviewSocial.findViewById(R.id.typeField)).setText("Vk");
            ((ImageView) popupProfileEditPreviewSocial.findViewById(R.id.imageCall)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_social_vk));
            ((ImageView) popupProfileEditPreviewSocial.findViewById(R.id.iconDataC)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_social_vk));
        } else if (typeEnum.equals(SocialEnums.LINKEDIN)) {
            ((TextView) popupProfileEditPreviewSocial.findViewById(R.id.valueUpd)).setText(contact.getSocialModel().getLinkedInLink());
            ((TextView) popupProfileEditPreviewSocial.findViewById(R.id.typeField)).setText("LinkedIn");
            ((ImageView) popupProfileEditPreviewSocial.findViewById(R.id.imageCall)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_social_linkedin));
            ((ImageView) popupProfileEditPreviewSocial.findViewById(R.id.iconDataC)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_social_linkedin));
        } else if (typeEnum.equals(SocialEnums.SKYPE)) {
            ((TextView) popupProfileEditPreviewSocial.findViewById(R.id.valueUpd)).setText(contact.getSocialModel().getSkypeLink());
            ((TextView) popupProfileEditPreviewSocial.findViewById(R.id.typeField)).setText("Skype");
            ((ImageView) popupProfileEditPreviewSocial.findViewById(R.id.imageCall)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_social_skype));
            ((ImageView) popupProfileEditPreviewSocial.findViewById(R.id.iconDataC)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_social_skype));
        } else if (typeEnum.equals(SocialEnums.TELEGRAM)) {
            ((TextView) popupProfileEditPreviewSocial.findViewById(R.id.valueUpd)).setText(contact.getSocialModel().getTelegramLink());
            ((TextView) popupProfileEditPreviewSocial.findViewById(R.id.typeField)).setText("Telegram");
            ((ImageView) popupProfileEditPreviewSocial.findViewById(R.id.imageCall)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_social_telegram));
            ((ImageView) popupProfileEditPreviewSocial.findViewById(R.id.iconDataC)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_social_telegram));
        } else if (typeEnum.equals(SocialEnums.VIBER)) {
            ((TextView) popupProfileEditPreviewSocial.findViewById(R.id.valueUpd)).setText(contact.getSocialModel().getViberLink());
            ((TextView) popupProfileEditPreviewSocial.findViewById(R.id.typeField)).setText("Viber");
            ((ImageView) popupProfileEditPreviewSocial.findViewById(R.id.imageCall)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_social_viber));
            ((ImageView) popupProfileEditPreviewSocial.findViewById(R.id.iconDataC)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_social_viber));
        } else if (typeEnum.equals(SocialEnums.WHATSAPP)) {
            ((TextView) popupProfileEditPreviewSocial.findViewById(R.id.valueUpd)).setText(contact.getSocialModel().getWhatsappLink());
            ((TextView) popupProfileEditPreviewSocial.findViewById(R.id.typeField)).setText("WhatsApp");
            ((ImageView) popupProfileEditPreviewSocial.findViewById(R.id.imageCall)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_social_whatsapp));
            ((ImageView) popupProfileEditPreviewSocial.findViewById(R.id.iconDataC)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_social_whatsapp));
        } else if (typeEnum.equals(SocialEnums.TWITTER)) {
            ((TextView) popupProfileEditPreviewSocial.findViewById(R.id.valueUpd)).setText(contact.getSocialModel().getTwitterLink());
            ((TextView) popupProfileEditPreviewSocial.findViewById(R.id.typeField)).setText("Twitter");
            ((ImageView) popupProfileEditPreviewSocial.findViewById(R.id.imageCall)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_twitter_64));
            ((ImageView) popupProfileEditPreviewSocial.findViewById(R.id.iconDataC)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_twitter_64));
        } else if (typeEnum.equals(SocialEnums.YOUTUBE)) {
            ((TextView) popupProfileEditPreviewSocial.findViewById(R.id.valueUpd)).setText(contact.getSocialModel().getYoutubeLink());
            ((TextView) popupProfileEditPreviewSocial.findViewById(R.id.typeField)).setText("YouTube");
            ((ImageView) popupProfileEditPreviewSocial.findViewById(R.id.imageCall)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_youtube_48));
            ((ImageView) popupProfileEditPreviewSocial.findViewById(R.id.iconDataC)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_youtube_48));
        } else if (typeEnum.equals(SocialEnums.MEDIUM)) {
            ((TextView) popupProfileEditPreviewSocial.findViewById(R.id.valueUpd)).setText(contact.getSocialModel().getMediumLink());
            ((TextView) popupProfileEditPreviewSocial.findViewById(R.id.typeField)).setText("Medium");
            ((ImageView) popupProfileEditPreviewSocial.findViewById(R.id.imageCall)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.medium_size_64));
            ((ImageView) popupProfileEditPreviewSocial.findViewById(R.id.iconDataC)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.medium_size_64));
        }

        ((TextView) popupProfileEditPreviewSocial.findViewById(R.id.valueUpd)).scrollTo(0, 0);
        ((TextView) popupProfileEditPreviewSocial.findViewById(R.id.typeField)).requestLayout();

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

                openedViews.add(popupEditSocial);


                popupEditSocial.findViewById(R.id.updateContactField).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Realm realm = Realm.getDefaultInstance();

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
                                    f = "https://www.facebook.com/" + f;
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
                                    f = "https://vk.com/" + f;
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
                         /*   ((ImageView) getActivity().findViewById(R.id.inst_icon)).setImageDrawable(ldi);

                            try{
                                ((ImageView) profilePopUp.findViewById(R.id.inst_icon)).setImageDrawable(ldi);
                            }catch (Exception e){

                            }

                            try{
                                ((ImageView) companyProfilePopup.findViewById(R.id.inst_icon)).setImageDrawable(ldi);
                            }catch (Exception e){

                            }*/

                                if (profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE) {
                                    ((ImageView) profilePopUp.findViewById(R.id.inst_icon)).setImageDrawable(ldi);
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

                                if (!username.contains("medium.com")) {
                                    username = "https://medium.com/" + username;
                                }

                                socialModel.setMediumLink(username);
                                //    ((ImageView) socialPopup.findViewById(R.id.instagram_icon)).setImageResource(R.drawable.icn_social_instagram);
                                //    ((ImageView) getActivity().findViewById(R.id.inst_icon)).setImageResource(R.drawable.icn_social_instagram);

                                Drawable colori = new ColorDrawable(Color.parseColor("#000000"));
                                Drawable imagei = getResources().getDrawable(R.drawable.medium_white);
                                LayerDrawable ldi = new LayerDrawable(new Drawable[]{colori, imagei});
                                ((ImageView) socialPopup.findViewById(R.id.medium_icon)).setImageDrawable(ldi);

                         /*   ((ImageView) getActivity().findViewById(R.id.inst_icon)).setImageDrawable(ldi);

                            try{
                                ((ImageView) profilePopUp.findViewById(R.id.inst_icon)).setImageDrawable(ldi);
                            }catch (Exception e){

                            }

                            try{
                                ((ImageView) companyProfilePopup.findViewById(R.id.inst_icon)).setImageDrawable(ldi);
                            }catch (Exception e){

                            }*/

                                if (profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE) {
                                    ((ImageView) profilePopUp.findViewById(R.id.medium_icon)).setImageDrawable(ldi);
                                }

                                //   if (contact.getSocialModel() != null)


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
                                    ((ImageView) profilePopUp.findViewById(R.id.medium_icon)).setImageDrawable(ld4);
                                }


                                ((TextView) socialPopup.findViewById(R.id.mediumNick)).setText("add username or link");
                                contact.hasMedium = false;
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
                                    f = "https://www.linkedin.com/in/" + f;
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
                                }


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
                         /*   ((CircleImageView) getActivity().findViewById(R.id.viber_icon)).setImageDrawable(ldvi);

                            try{
                                ((CircleImageView) profilePopUp.findViewById(R.id.viber_icon)).setImageDrawable(ldvi);
                            }catch (Exception e){

                            }

                            try{
                                ((CircleImageView) companyProfilePopup.findViewById(R.id.viber_icon)).setImageDrawable(ldvi);
                            }catch (Exception e){

                            }*/

                                if (profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE) {
                                    ((CircleImageView) profilePopUp.findViewById(R.id.viber_icon)).setImageDrawable(ldvi);
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
                        /*    ((CircleImageView) getActivity().findViewById(R.id.whatsapp_icon)).setImageDrawable(ldw);

                            try{
                                ((CircleImageView) profilePopUp.findViewById(R.id.whatsapp_icon)).setImageDrawable(ldw);
                            }catch (Exception e){

                            }

                            try{
                                ((CircleImageView) companyProfilePopup.findViewById(R.id.whatsapp_icon)).setImageDrawable(ldw);
                            }catch (Exception e){

                            }*/

                                if (profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE) {
                                    ((CircleImageView) profilePopUp.findViewById(R.id.whatsapp_icon)).setImageDrawable(ldw);
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

                       /*     ((CircleImageView) getActivity().findViewById(R.id.telegram_icon)).setImageDrawable(ldt);

                            try{
                                ((CircleImageView) profilePopUp.findViewById(R.id.telegram_icon)).setImageDrawable(ldt);
                            }catch (Exception e){

                            }

                            try{
                                ((CircleImageView) companyProfilePopup.findViewById(R.id.telegram_icon)).setImageDrawable(ldt);
                            }catch (Exception e){
                            }*/

                                if (profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE) {
                                    ((CircleImageView) profilePopUp.findViewById(R.id.telegram_icon)).setImageDrawable(ldt);
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
                        /*    ((CircleImageView) getActivity().findViewById(R.id.skype_icon)).setImageDrawable(lds);

                            try{
                                ((CircleImageView) profilePopUp.findViewById(R.id.skype_icon)).setImageDrawable(lds);
                            }catch (Exception e){

                            }

                            try{
                                ((CircleImageView) companyProfilePopup.findViewById(R.id.skype_icon)).setImageDrawable(lds);
                            }catch (Exception e){

                            }*/

                                if (profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE) {
                                    ((CircleImageView) profilePopUp.findViewById(R.id.skype_icon)).setImageDrawable(lds);
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
                                    f = "https://twitter.com/" + f;
                                }

                                if (ClipboardType.isTwitter(f)) {
                                    socialModel.setTwitterLink(f);
                                } else {
                                    socialModel.setTwitterLink(((EditText) popupEditSocial.findViewById(R.id.dataToEdit)).getText().toString());
                                }

                                //socialModel.setTwitterLink(((EditText) popupEditSocial.findViewById(R.id.dataToEdit)).getText().toString());
                                //     ((ImageView) socialPopup.findViewById(R.id.vk_icon)).setImageResource(R.drawable.icn_social_vk);
                                //     ((ImageView) getActivity().findViewById(R.id.vk_icon)).setImageResource(R.drawable.icn_social_vk);
                                Drawable colorv = new ColorDrawable(Color.parseColor("#2ca7e0"));
                                Drawable imagev = getResources().getDrawable(R.drawable.ic_twitter_white);
                                LayerDrawable ldv = new LayerDrawable(new Drawable[]{colorv, imagev});
                                ((ImageView) socialPopup.findViewById(R.id.twitter_icon)).setImageDrawable(ldv);

                                try {
                                    ((ImageView) profilePopUp.findViewById(R.id.vk_icon)).setImageDrawable(ldv);
                                } catch (Exception e) {

                                }


                          /*  ((ImageView) getActivity().findViewById(R.id.vk_icon)).setImageDrawable(ldv);

                            try{
                                ((ImageView) profilePopUp.findViewById(R.id.vk_icon)).setImageDrawable(ldv);
                            }catch (Exception e){

                            }

                            try{
                                ((ImageView) companyProfilePopup.findViewById(R.id.vk_icon)).setImageDrawable(ldv);
                            }catch (Exception e){

                            }*/


                              /*  if (profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE) {
                                    ((ImageView) profilePopUp.findViewById(R.id.vk_icon)).setImageDrawable(ldv);
                                } else if (companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE) {
                                    ((ImageView) companyProfilePopup.findViewById(R.id.vk_icon)).setImageDrawable(ldv);
                                } else {
                                    ((ImageView) getActivity().findViewById(R.id.vk_icon)).setImageDrawable(ldv);
                                }*/


                               /* try {
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
                                } catch (Exception e) {*/
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

                                try {
                                    ((ImageView) profilePopUp.findViewById(R.id.facebook_icon)).setImageDrawable(ld2);
                                } catch (Exception e) {

                                }

                         /*   ((ImageView) getActivity().findViewById(R.id.vk_icon)).setImageDrawable(ld2);

                            try{
                                ((ImageView) profilePopUp.findViewById(R.id.facebook_icon)).setImageDrawable(ld2);
                            }catch (Exception e){

                            }

                            try{
                                ((ImageView) companyProfilePopup.findViewById(R.id.facebook_icon)).setImageDrawable(ld2);
                            }catch (Exception e){

                            }*/

                               /* if (profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE) {
                                    ((ImageView) profilePopUp.findViewById(R.id.vk_icon)).setImageDrawable(ld2);
                                } else if (companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE) {
                                    ((ImageView) companyProfilePopup.findViewById(R.id.vk_icon)).setImageDrawable(ld2);
                                } else {
                                    ((ImageView) getActivity().findViewById(R.id.vk_icon)).setImageDrawable(ld2);
                                }*/


                                ((TextView) socialPopup.findViewById(R.id.twitterNick)).setText("add username or link");
                                contact.hasTwitter = false;
                                contact.setSocialModel(socialModel);
                                realm.commitTransaction();
                                ContactCacheService.updateContact(contact, mainView.getContext());
                                //  EventBus.getDefault().post(new UpdateFile());
                                popupEditSocial.setVisibility(View.GONE);
                                popupProfileEditPreviewSocial.setVisibility(View.GONE);
                            }

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


                                //socialModel.setYoutubeLink(((EditText) popupEditSocial.findViewById(R.id.dataToEdit)).getText().toString());
                                //     ((ImageView) socialPopup.findViewById(R.id.vk_icon)).setImageResource(R.drawable.icn_social_vk);
                                //     ((ImageView) getActivity().findViewById(R.id.vk_icon)).setImageResource(R.drawable.icn_social_vk);
                                Drawable colorv = new ColorDrawable(Color.parseColor("#ed2524"));
                                Drawable imagev = getResources().getDrawable(R.drawable.ic_youtube_white);
                                LayerDrawable ldv = new LayerDrawable(new Drawable[]{colorv, imagev});
                                ((ImageView) socialPopup.findViewById(R.id.youtube_icon)).setImageDrawable(ldv);

                          /*  ((ImageView) getActivity().findViewById(R.id.vk_icon)).setImageDrawable(ldv);

                            try{
                                ((ImageView) profilePopUp.findViewById(R.id.vk_icon)).setImageDrawable(ldv);
                            }catch (Exception e){

                            }

                            try{
                                ((ImageView) companyProfilePopup.findViewById(R.id.vk_icon)).setImageDrawable(ldv);
                            }catch (Exception e){

                            }*/


                              /*  if (profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE) {
                                    ((ImageView) profilePopUp.findViewById(R.id.vk_icon)).setImageDrawable(ldv);
                                } else if (companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE) {
                                    ((ImageView) companyProfilePopup.findViewById(R.id.vk_icon)).setImageDrawable(ldv);
                                } else {
                                    ((ImageView) getActivity().findViewById(R.id.vk_icon)).setImageDrawable(ldv);
                                }*/


                                /*try {
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
                                } catch (Exception e) {*/
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
                         /*   ((ImageView) getActivity().findViewById(R.id.vk_icon)).setImageDrawable(ld2);

                            try{
                                ((ImageView) profilePopUp.findViewById(R.id.facebook_icon)).setImageDrawable(ld2);
                            }catch (Exception e){

                            }

                            try{
                                ((ImageView) companyProfilePopup.findViewById(R.id.facebook_icon)).setImageDrawable(ld2);
                            }catch (Exception e){

                            }*/
/*
                                if (profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE) {
                                    ((ImageView) profilePopUp.findViewById(R.id.vk_icon)).setImageDrawable(ld2);
                                } else if (companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE) {
                                    ((ImageView) companyProfilePopup.findViewById(R.id.vk_icon)).setImageDrawable(ld2);
                                } else {
                                    ((ImageView) getActivity().findViewById(R.id.vk_icon)).setImageDrawable(ld2);
                                }*/


                                ((TextView) socialPopup.findViewById(R.id.youtubeNick)).setText("add username or link");
                                contact.hasYoutube = false;
                                contact.setSocialModel(socialModel);
                                realm.commitTransaction();
                                ContactCacheService.updateContact(contact, mainView.getContext());
                                //  EventBus.getDefault().post(new UpdateFile());
                                popupEditSocial.setVisibility(View.GONE);
                                popupProfileEditPreviewSocial.setVisibility(View.GONE);
                            }

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

                            EventBus.getDefault().post(new UpdateFile());
                        }

                        if (profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE) {
                            initIconColor(contact, profilePopUp);
                        } else if (fastEditPopup != null && fastEditPopup.getVisibility() == View.VISIBLE) {
                            initIconColor(contact, fastEditPopup);
                        }

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

        popupProfileEditPreviewSocial.findViewById(R.id.editPopupCall).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                String link = null;

                if (typeEnum.equals(SocialEnums.FACEBOOK)) {
                    link = contact.getSocialModel().getFacebookLink();
                } else if (typeEnum.equals(SocialEnums.INSTAGRAM)){
                    link = contact.getSocialModel().getInstagramLink();
                }else if (typeEnum.equals(SocialEnums.VK)){
                    link = contact.getSocialModel().getVkLink();
                }else if (typeEnum.equals(SocialEnums.LINKEDIN)){
                    link = contact.getSocialModel().getLinkedInLink();
                } else if (typeEnum.equals(SocialEnums.TWITTER)){
                    link = contact.getSocialModel().getTwitterLink();
                } else if (typeEnum.equals(SocialEnums.YOUTUBE)){
                    link = contact.getSocialModel().getYoutubeLink();
                }else if (typeEnum.equals(SocialEnums.MEDIUM)){
                    link = contact.getSocialModel().getMediumLink();
                }

                if(link != null){
                    try {
                        String uris = link;
                        if (!link.contains("https://") && !link.contains("http://"))
                            uris = "https://" + uris;

                        System.out.println("Check soc");

                        Intent i = new Intent(Intent.ACTION_VIEW);

                        //i.addCategory(Intent.CATEGORY_APP_BROWSER);

                        i.setData(Uri.parse(uris));

                        System.out.println("uris = "+uris);

                        getActivity().startActivity(Intent.createChooser(i, "Open with..."));


                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
                return true;
            }
        });

        popupProfileEditPreviewSocial.findViewById(R.id.editPopupCall).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (typeEnum.equals(SocialEnums.FACEBOOK)) {
                    if (!contact.hasFacebook) {
                        //closeChildPopups();
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

                        //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://facewebmodal/f?href=" + contact.getSocialModel().getFacebookLink()));
                        PackageManager packageManager = getActivity().getPackageManager();
                        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
                        boolean isIntentSafe = activities.size() > 0;
                        if (!isIntentSafe) {
                           /* Intent intent2 = new Intent(Intent.ACTION_VIEW);
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
                        //closeChildPopups();
                        showSocialPopup(contact);
                        socialPopup.findViewById(R.id.instagramLink).callOnClick();
                        socialPopup.setVisibility(View.GONE);
                    } else {
                        System.out.println("Go Insta");

                        String str = contact.getSocialModel().getInstagramLink();
                        if (!str.toLowerCase().contains("instagram")) {
                            str = "https://instagram.com/" + contact.getSocialModel().getInstagramLink();
                        }

                        Uri uri = Uri.parse(str);
                        Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);
                        likeIng.setPackage("com.instagram.android");
                        PackageManager packageManager = getActivity().getPackageManager();
                        List<ResolveInfo> activities = packageManager.queryIntentActivities(likeIng, 0);
                        boolean isIntentSafe = activities.size() > 0;
                        if (!isIntentSafe) {
                           /* Intent intent2 = new Intent(Intent.ACTION_VIEW);
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
                        //closeChildPopups();
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
                        //closeChildPopups();
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
                        //closeChildPopups();
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
                            getActivity().startActivity(Intent.createChooser(telegramIntent, "Open with...").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                            //startActivity(telegramIntent);
                    }
                    //========
                } else if (typeEnum.equals(SocialEnums.SKYPE)) {
                    //=====================================================================
                    if (!contact.hasSkype) {
                        //   closeOtherPopup();
                        //closeChildPopups();
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
                        //closeChildPopups();
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
                        //closeChildPopups();
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
                       /* Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format(contact.getSocialModel().getVkLink())));
                        Intent intent2 = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("vkontakte://profile/")));
                        try {
                            startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            e.printStackTrace();
                            PackageManager packageManager = getActivity().getPackageManager();
                            List<ResolveInfo> activities = packageManager.queryIntentActivities(intent2, 0);
                            boolean isIntentSafe = activities.size() > 0;
                            if (!isIntentSafe) {
                                Intent intent3 = new Intent(Intent.ACTION_VIEW);
                                intent3.setData(Uri.parse("market://details?id=com.vkontakte.android"));
                                startActivity(intent3);
                            } else
                                startActivity(intent2);
                        }*/
                    }
                    //==============
                } else if (typeEnum.equals(SocialEnums.YOUTUBE)) {
                    //============================================================
                    if (!contact.hasYoutube) {
                        //    closeOtherPopup();
                        //closeChildPopups();
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
                                Intent intent3 = new Intent(Intent.ACTION_VIEW);
                                intent3.setData(Uri.parse("market://details?id=com.vkontakte.android"));
                                startActivity(intent3);
                            } else
                                startActivity(intent2);
                        }*/
                    }
                    //==============
                } else if (typeEnum.equals(SocialEnums.MEDIUM)) {
                    //============================================================
                    if (!contact.hasMedium) {
                        //    closeOtherPopup();
                        //closeChildPopups();
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

                if (profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE) {
                    initIconColor(contact, profilePopUp);
                } else if (fastEditPopup != null && fastEditPopup.getVisibility() == View.VISIBLE) {
                    initIconColor(contact, fastEditPopup);
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


                            Realm realm = Realm.getDefaultInstance();

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

                                if (profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE) {
                                    ((ImageView) profilePopUp.findViewById(R.id.vk_icon)).setImageDrawable(ld2);
                                }

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

                                if (profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE) {
                                    ((ImageView) profilePopUp.findViewById(R.id.vk_icon)).setImageDrawable(ld2);
                                }

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

                            EventBus.getDefault().post(new UpdateFile());
                            popupProfileEditPreviewSocial.setVisibility(View.GONE);
                        })
                        .setNegativeButton("No", (dialog, id) -> dialog.cancel());

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

    }


    @Override
    public void onResume() {
        System.out.println("RESUME COMPANY FRAGMENT");

        if (CompanyAdapter.selectionModeCompany) {
            System.out.println("CompanyAdapter.clickmerge = true");
            if (companyAdapter != null && companyAdapter.selectedContacts != null) {
                System.out.println("CompanyAdapter.clickmerge = true");
                companyAdapter.startSelectionMode();
                if (getActivity() == null) System.out.println("RESUME NULL");
                toolbarC.findViewById(R.id.toolbar_title).setVisibility(View.GONE);
                ((TextView) toolbarC.findViewById(R.id.cancel_toolbar)).setVisibility(View.VISIBLE);
                ((TextView) toolbarC.findViewById(R.id.cancel_toolbar)).setTextSize(18);
                ((TextView) toolbarC.findViewById(R.id.cancel_toolbar)).setText("" + companyAdapter.selectedContacts.size() + "");
            }
        }

        super.onResume();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            activityApp = (Activity) context;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (CompanyAdapter.selectionModeCompany && companyAdapter != null) {
            companyAdapter.stopSelectionMode();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser && isResumed()) {
            System.out.println("TIMELINE VISIBLE 1");


        } else {
            System.out.println("TIMELINE GONE 1");
            if (CompanyAdapter.selectionModeCompany && companyAdapter != null) {
                companyAdapter.stopSelectionMode();
            }
        }
    }

    public void showEditPopupPreview(String contactInfo, String type) {
        System.out.println("CLICK 3");
        closeOtherPopup();

        try {
            ((ProfileFragment) getParentFragment()).closeOtherPopup();
        } catch (Exception e) {
            ((CreateFragment) getParentFragment()).closeOtherPopup();
        }
        popupProfileEditPreview.setVisibility(View.VISIBLE);
        popupProfileEditPreview.setOnClickListener(v -> {

        });


        //((ImageView) popupProfileEditPreview.findViewById(R.id.imageCall)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_popup_call));


        //Matcher matcher = Patterns.WEB_URL.matcher(contactInfo.value.toString());

        if (type.equalsIgnoreCase("email")) {
            ((ImageView) popupProfileEditPreview.findViewById(R.id.imageCall)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.gmail));
            ((ImageView) popupProfileEditPreview.findViewById(R.id.icn_mail_clip)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_remind_message));
            ((ImageView) popupProfileEditPreview.findViewById(R.id.iconDataProfile)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_emails));

            popupProfileEditPreview.findViewById(R.id.search_note_popup).setVisibility(View.VISIBLE);
            ((ImageView) popupProfileEditPreview.findViewById(R.id.imageView3)).setVisibility(View.GONE);
        } else if (type.equalsIgnoreCase("web")) {
            ((ImageView) popupProfileEditPreview.findViewById(R.id.imageCall)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_popup_web_blue));
            ((ImageView) popupProfileEditPreview.findViewById(R.id.icn_mail_clip)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_extract_social));
            ((ImageView) popupProfileEditPreview.findViewById(R.id.iconDataProfile)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_popup_web_blue));
            popupProfileEditPreview.findViewById(R.id.search_note_popup).setVisibility(View.VISIBLE);
            ((ImageView) popupProfileEditPreview.findViewById(R.id.imageView3)).setVisibility(View.GONE);

        } else if (type.equalsIgnoreCase("intro")) {
            ((ImageView) popupProfileEditPreview.findViewById(R.id.imageCall)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_fab_google));
            ((ImageView) popupProfileEditPreview.findViewById(R.id.icn_mail_clip)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_remind_message));
            ((ImageView) popupProfileEditPreview.findViewById(R.id.iconDataProfile)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.intro_n));

            popupProfileEditPreview.findViewById(R.id.search_note_popup).setVisibility(View.VISIBLE);
            ((ImageView) popupProfileEditPreview.findViewById(R.id.imageView3)).setVisibility(View.GONE);
        }

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ((ImageView) popupProfileEditPreview.findViewById(R.id.imageCall)).setImageTintMode(null);
        }
        if (ClipboardType.isFacebook(contactInfo.value)) {
            ((ImageView) popupProfileEditPreview.findViewById(R.id.imageCall)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_social_facebook));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ((ImageView) popupProfileEditPreview.findViewById(R.id.imageCall)).setImageTintMode(null);
            }

        } else if (ClipboardType.isInsta(contactInfo.value)) {
            ((ImageView) popupProfileEditPreview.findViewById(R.id.imageCall)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_social_instagram));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ((ImageView) popupProfileEditPreview.findViewById(R.id.imageCall)).setImageTintMode(null);
            }

        } else if (ClipboardType.isG_Doc(contactInfo.value)) {
            ((ImageView) popupProfileEditPreview.findViewById(R.id.imageCall)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.google_docs));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ((ImageView) popupProfileEditPreview.findViewById(R.id.imageCall)).setImageTintMode(null);
            }

        } else if (ClipboardType.isG_Sheet(contactInfo.value)) {
            ((ImageView) popupProfileEditPreview.findViewById(R.id.imageCall)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.google_sheets));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ((ImageView) popupProfileEditPreview.findViewById(R.id.imageCall)).setImageTintMode(null);
            }

        } else if (ClipboardType.isTwitter(contactInfo.value)) {
            ((ImageView) popupProfileEditPreview.findViewById(R.id.imageCall)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_twitter_64));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ((ImageView) popupProfileEditPreview.findViewById(R.id.imageCall)).setImageTintMode(null);
            }

        } else if (ClipboardType.isYoutube(contactInfo.value)) {
            ((ImageView) popupProfileEditPreview.findViewById(R.id.imageCall)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_youtube_48));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ((ImageView) popupProfileEditPreview.findViewById(R.id.imageCall)).setImageTintMode(null);
            }

        } else if (ClipboardType.isVk(contactInfo.value)) {
            ((ImageView) popupProfileEditPreview.findViewById(R.id.imageCall)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_social_vk));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ((ImageView) popupProfileEditPreview.findViewById(R.id.imageCall)).setImageTintMode(null);
            }

        } else if (ClipboardType.isLinkedIn(contactInfo.value)) {
            ((ImageView) popupProfileEditPreview.findViewById(R.id.imageCall)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_social_linkedin));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ((ImageView) popupProfileEditPreview.findViewById(R.id.imageCall)).setImageTintMode(null);
            }

        } else if (ClipboardType.isTelegram(contactInfo.value)) {
            ((ImageView) popupProfileEditPreview.findViewById(R.id.imageCall)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_social_telegram));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ((ImageView) popupProfileEditPreview.findViewById(R.id.imageCall)).setImageTintMode(null);
            }

        } else if (ClipboardType.is_Tumblr(contactInfo.value)) {
            ((ImageView) popupProfileEditPreview.findViewById(R.id.imageCall)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.tumblr));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ((ImageView) popupProfileEditPreview.findViewById(R.id.imageCall)).setImageTintMode(null);
            }

        }  else if (ClipboardType.isMedium(contactInfo.value)) {
            ((ImageView) popupProfileEditPreview.findViewById(R.id.imageCall)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.medium_size_64));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ((ImageView) popupProfileEditPreview.findViewById(R.id.imageCall)).setImageTintMode(null);
            }

        } else if (ClipboardType.is_Angel(contactInfo.value)) {
            ((ImageView) popupProfileEditPreview.findViewById(R.id.imageCall)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.angel));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ((ImageView) popupProfileEditPreview.findViewById(R.id.imageCall)).setImageTintMode(null);
            }

        }else if (*//*matcher.matches() && !contactInfo.value.contains("@")*//*ClipboardType.isWeb(contactInfo.value) && contactInfo.isNote) {

            ((ImageView) popupProfileEditPreview.findViewById(R.id.imageCall)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_popup_web_blue));

        }*/

        //    ((TextView) popupProfileEditPreview.findViewById(R.id.valueUpd)).setEnabled(false);
        //    ((TextView) popupProfileEditPreview.findViewById(R.id.valueUpd)).setEnabled(true);

        ((TextView) popupProfileEditPreview.findViewById(R.id.typeField)).setText(type);
        ((TextView) popupProfileEditPreview.findViewById(R.id.typeField)).requestLayout();
        ((TextView) popupProfileEditPreview.findViewById(R.id.valueUpd)).setText(contactInfo);

        //((TextView) popupProfileEditPreview.findViewById(R.id.valueUpd)).setMovementMethod(new ScrollingMovementMethod());

        //  ((TextView) popupProfileEditPreview.findViewById(R.id.valueUpd)).setEnabled(false);
        //  ((TextView) popupProfileEditPreview.findViewById(R.id.valueUpd)).setEnabled(true);


        popupProfileEditPreview.findViewById(R.id.updateContact).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupProfileEditPreview.setVisibility(View.INVISIBLE);
                showEditPopup(contactInfo, type);
            }
        });

        popupProfileEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        if (openedViews != null) openedViews.add(popupProfileEditPreview);

        popupProfileEditPreview.findViewById(R.id.editPopupCopy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyToClipboard(mainView.getContext(), String.valueOf(contactInfo));
                Toast.makeText(mainView.getContext(), "Copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });

        popupProfileEditPreview.findViewById(R.id.editPopupShare).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = contactInfo;
                text += "\n\n";
                text += "Data shared via http://Extime.pro\n";
                shareIntent(text);
                //shareIntent(String.valueOf(contactInfo.value));
            }
        });

        switch (type.toLowerCase()) {

            case "email": {

                popupProfileEditPreview.findViewById(R.id.editPopupCall).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("message/rfc822");
                        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{contactInfo});
                        intent.putExtra(Intent.EXTRA_SUBJECT, "");
                        intent.setPackage("com.google.android.gm");
                        if (intent.resolveActivity(activityApp.getPackageManager()) != null)
                            activityApp.startActivity(intent);
                    }
                });

                popupProfileEditPreview.findViewById(R.id.editPopupMail).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendEmail(String.valueOf(contactInfo));
                    }
                });
                popupProfileEditPreview.findViewById(R.id.delete_contact_number).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                getActivity());
                        alertDialogBuilder.setTitle("Do you want to delete email?");
                        alertDialogBuilder
                                .setCancelable(false)
                                .setPositiveButton("Yes", (dialog, id) -> {

                                    //contactsService.deleteEmailContact(selectedContact.get(0).getIdContact(), contactInfo.value);
                                    //contactNumberAdapter.removeContactNumber(contactInfo);
                                    //frameClick.callOnClick();

                                    Realm realm = Realm.getDefaultInstance();
                                    realm.beginTransaction();
                                    //selectedContact.emailCompany = "";
                                    if (selectedContact.listOfContactInfo != null) {
                                        for (ContactInfo ci : selectedContact.listOfContactInfo) {
                                            if (ci.value.equalsIgnoreCase(contactInfo)) {
                                                selectedContact.listOfContactInfo.remove(ci);
                                                break;
                                            }
                                        }
                                    }
                                    realm.commitTransaction();
                                    realm.close();

                                    popupProfileEditPreview.setVisibility(View.GONE);

                                    companyAdapter.updateNEw();
                                    companyAdapter.notifyDataSetChanged();

                                    //((ProfileFragment) getParentFragment()).contactTimelineDataFragment.checkEmail();
                                })
                                .setNegativeButton("No", (dialog, id) -> dialog.cancel());

                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                });

                popupProfileEditPreview.findViewById(R.id.remind_contact_number).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });
                popupProfileEditPreview.findViewById(R.id.call2me_contact_number).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });
                break;
            }
            case "web": {
                System.out.println("CLICK NOTE TYPEDATA");
                popupProfileEditPreview.findViewById(R.id.editPopupCall).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            String uri = contactInfo;
                            if (!contactInfo.contains("https://") && !contactInfo.contains("http://"))
                                uri = "https://" + uri;

                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(uri));
                            startActivity(i);
                        } catch (Exception e) {

                        }
                    }
                });


                popupProfileEditPreview.findViewById(R.id.editPopupMail).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                popupProfileEditPreview.findViewById(R.id.delete_contact_number).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                getActivity());
                        alertDialogBuilder.setTitle("Do you want to delete web?");
                        alertDialogBuilder
                                .setCancelable(false)
                                .setPositiveButton("Yes", (dialog, id) -> {
                                    //contactNumberAdapter.removeContactNumber(contactInfo);
                                    //frameClick.callOnClick();
                                    Realm realm = Realm.getDefaultInstance();
                                    realm.beginTransaction();

                                    System.out.println("WEB 1 = "+ contactInfo);

                                    if (selectedContact.listOfContactInfo != null) {
                                        for (ContactInfo ci : selectedContact.listOfContactInfo) {
                                            System.out.println("WEB 2 = "+ci.value);
                                            if (ci.value.contains(contactInfo)) {
                                                System.out.println("SUCCESS REMOVE");
                                                selectedContact.listOfContactInfo.remove(ci);
                                                break;
                                            }
                                        }
                                    }

                                    //System.out.println("WEB 3 = "+selectedContact.webSite);

                                    if (selectedContact.webSite != null && selectedContact.webSite.contains(contactInfo))
                                        selectedContact.webSite = "";

                                    realm.commitTransaction();
                                    realm.close();

                                    ((ProfileFragment) getParentFragment()).updateWeb();

                                    companyAdapter.updateNEw();
                                    companyAdapter.notifyDataSetChanged();

                                    popupProfileEditPreview.setVisibility(View.GONE);

                                    //contactsService.deleteNoteContact(selectedContact.get(0).getIdContact(), contactInfo.value);
                                })
                                .setNegativeButton("No", (dialog, id) -> dialog.cancel());

                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                });
                popupProfileEditPreview.findViewById(R.id.remind_contact_number).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });
                popupProfileEditPreview.findViewById(R.id.call2me_contact_number).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });
                break;
            }

            case "intro": {
                System.out.println("CLICK NOTE TYPEDATA");
                popupProfileEditPreview.findViewById(R.id.editPopupCall).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            String uri = contactInfo;
                            if (!contactInfo.contains("https://") && !contactInfo.contains("http://"))
                                uri = "https://" + uri;

                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(uri));
                            startActivity(i);
                        } catch (Exception e) {

                        }
                    }
                });


                popupProfileEditPreview.findViewById(R.id.editPopupMail).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                popupProfileEditPreview.findViewById(R.id.delete_contact_number).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                getActivity());
                        alertDialogBuilder.setTitle("Do you want to delete intro?");
                        alertDialogBuilder
                                .setCancelable(false)
                                .setPositiveButton("Yes", (dialog, id) -> {
                                    //contactNumberAdapter.removeContactNumber(contactInfo);
                                    //frameClick.callOnClick();

                                    Realm realm = Realm.getDefaultInstance();
                                    realm.beginTransaction();
                                    selectedContact.shortInto = "";
                                    realm.commitTransaction();
                                    realm.close();

                                    companyAdapter.updateNEw();
                                    companyAdapter.notifyDataSetChanged();

                                    popupProfileEditPreview.setVisibility(View.GONE);

                                    //contactsService.deleteNoteContact(selectedContact.get(0).getIdContact(), contactInfo.value);
                                })
                                .setNegativeButton("No", (dialog, id) -> dialog.cancel());

                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                });
                popupProfileEditPreview.findViewById(R.id.remind_contact_number).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });
                popupProfileEditPreview.findViewById(R.id.call2me_contact_number).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });
                break;
            }
        }
    }


    public void showEditPopup(String contactInfo, String type) {
        closeOtherPopup();
        openedViews.add(popupProfileEdit);
        ((EditText) popupProfileEdit.findViewById(R.id.dataToEdit)).setText(contactInfo);
        ((TextView) popupProfileEdit.findViewById(R.id.typeField)).setText(type);

        //popupProfileEdit.findViewById(R.id.imageEditSocial).setVisibility(View.GONE);
        //popupProfileEdit.findViewById(R.id.frameImageEditSocialInsta).setVisibility(View.GONE);

        //popupProfileEdit.findViewById(R.id.get_last_clips).setVisibility(View.GONE);



        if (type.equalsIgnoreCase("email")) {
            ((ImageView) popupProfileEdit.findViewById(R.id.iconDataEdit)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_emails));
        } else if (type.equalsIgnoreCase("web")) {
            ((ImageView) popupProfileEdit.findViewById(R.id.iconDataEdit)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_popup_web_blue));
        } else if (type.equalsIgnoreCase("intro")) {
            ((ImageView) popupProfileEdit.findViewById(R.id.iconDataEdit)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.intro_n));
        }


                popupProfileEdit.findViewById(R.id.typeFieldEdit).setVisibility(View.GONE);

            popupProfileEdit.findViewById(R.id.ok_social).setVisibility(View.GONE);




        popupProfileEdit.setOnClickListener(v -> {
        });
        popupProfileEdit.findViewById(R.id.updateContactField).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //System.out.println(this.getClass() + " QWER 111 = " + contactInfo.typeData.toString());
                switch (type.toLowerCase()) {
                    case "web": {
                        if (((EditText) popupProfileEdit.findViewById(R.id.dataToEdit)).getText().toString().length() > 0) {
                            //contactsService.updatePhone(String.valueOf(selectedContact.get(0).getIdContact()), contactInfo.value, ((EditText) popupProfileEdit.findViewById(R.id.dataToEdit)).getText().toString());

                            Realm realm = Realm.getDefaultInstance(); //+
                            realm.beginTransaction();

                            /*try {
                                for (int i = 0; i < CreateFragment.createdContact.listOfContactInfo.size(); i++) {
                                    if (CreateFragment.createdContact.listOfContactInfo.get(i).value.trim().equals(contactInfo.value.trim())) {
                                        CreateFragment.createdContact.listOfContactInfo.get(i).value = ((EditText) popupProfileEdit.findViewById(R.id.dataToEdit)).getText().toString().trim();
                                    }
                                }
                            } catch (Exception e) {

                            }*/

                            //System.out.println("NULL SELECYED CONTACTS = " + selectedContact.size());

                            /*try {
                                String oldPhone = contactInfo.value;
                                SocialModel socialModel = selectedContact.get(0).getSocialModel();
                                if (socialModel.getViberLink() != null && !socialModel.getViberLink().isEmpty()) {
                                    if (socialModel.getViberLink().equalsIgnoreCase(oldPhone)) {
                                        contactsService.updateNote(selectedContact.get(0).getIdContact(), socialModel.getViberLink(), ((EditText) popupProfileEdit.findViewById(R.id.dataToEdit)).getText().toString().trim());
                                        socialModel.setViberLink(((EditText) popupProfileEdit.findViewById(R.id.dataToEdit)).getText().toString().trim());
                                    } else {
                                        String phoneViber = socialModel.getViberLink().replaceAll("[\\s\\-\\+\\(\\)]", "");
                                        String oldReplace = oldPhone.replaceAll("[\\s\\-\\+\\(\\)]", "");
                                        if (phoneViber.equalsIgnoreCase(oldReplace)) {
                                            contactsService.updateNote(selectedContact.get(0).getIdContact(), socialModel.getViberLink(), ((EditText) popupProfileEdit.findViewById(R.id.dataToEdit)).getText().toString().trim());
                                            socialModel.setViberLink(((EditText) popupProfileEdit.findViewById(R.id.dataToEdit)).getText().toString().trim());
                                        }
                                    }
                                }

                                if (socialModel.getTelegramLink() != null && !socialModel.getTelegramLink().isEmpty()) {
                                    if (socialModel.getTelegramLink().equalsIgnoreCase(oldPhone)) {
                                        contactsService.updateNote(selectedContact.get(0).getIdContact(), socialModel.getTelegramLink(), ((EditText) popupProfileEdit.findViewById(R.id.dataToEdit)).getText().toString().trim());
                                        socialModel.setTelegramLink(((EditText) popupProfileEdit.findViewById(R.id.dataToEdit)).getText().toString().trim());
                                    } else {
                                        String phoneViber = socialModel.getTelegramLink().replaceAll("[\\s\\-\\+\\(\\)]", "");
                                        String oldReplace = oldPhone.replaceAll("[\\s\\-\\+\\(\\)]", "");
                                        if (phoneViber.equalsIgnoreCase(oldReplace)) {
                                            contactsService.updateNote(selectedContact.get(0).getIdContact(), socialModel.getTelegramLink(), ((EditText) popupProfileEdit.findViewById(R.id.dataToEdit)).getText().toString().trim());
                                            socialModel.setTelegramLink(((EditText) popupProfileEdit.findViewById(R.id.dataToEdit)).getText().toString().trim());
                                        }
                                    }
                                }

                                if (socialModel.getWhatsappLink() != null && !socialModel.getWhatsappLink().isEmpty()) {
                                    if (socialModel.getWhatsappLink().equalsIgnoreCase(oldPhone)) {
                                        contactsService.updateNote(selectedContact.get(0).getIdContact(), socialModel.getWhatsappLink(), ((EditText) popupProfileEdit.findViewById(R.id.dataToEdit)).getText().toString().trim());
                                        socialModel.setWhatsappLink(((EditText) popupProfileEdit.findViewById(R.id.dataToEdit)).getText().toString().trim());
                                    } else {
                                        String phoneViber = socialModel.getWhatsappLink().replaceAll("[\\s\\-\\+\\(\\)]", "");
                                        String oldReplace = oldPhone.replaceAll("[\\s\\-\\+\\(\\)]", "");
                                        if (phoneViber.equalsIgnoreCase(oldReplace)) {
                                            contactsService.updateNote(selectedContact.get(0).getIdContact(), socialModel.getWhatsappLink(), ((EditText) popupProfileEdit.findViewById(R.id.dataToEdit)).getText().toString().trim());
                                            socialModel.setWhatsappLink(((EditText) popupProfileEdit.findViewById(R.id.dataToEdit)).getText().toString().trim());
                                        }
                                    }
                                }
                            } catch (Exception e) {

                            }*/


                            //contactInfo.value = ((EditText) popupProfileEdit.findViewById(R.id.dataToEdit)).getText().toString().trim();


                            if (selectedContact.listOfContactInfo != null) {
                                for (ContactInfo ci : selectedContact.listOfContactInfo) {
                                    if (ci.value.equalsIgnoreCase(contactInfo)) {
                                        ci.value = ((EditText) popupProfileEdit.findViewById(R.id.dataToEdit)).getText().toString().trim();
                                        break;
                                        //break;
                                    }
                                }
                            }
                            if (selectedContact.webSite != null && selectedContact.webSite.equalsIgnoreCase(contactInfo))
                                selectedContact.webSite = ((EditText) popupProfileEdit.findViewById(R.id.dataToEdit)).getText().toString().trim();

                            realm.commitTransaction();
                            realm.close();
                            companyAdapter.notifyDataSetChanged();


                        } else {

                            Realm realm = Realm.getDefaultInstance(); //+
                            realm.beginTransaction();

                            /*try {
                                String oldPhone = contactInfo.value;
                                SocialModel socialModel = selectedContact.get(0).getSocialModel();
                                if (socialModel.getViberLink() != null && !socialModel.getViberLink().isEmpty()) {
                                    if (socialModel.getViberLink().equalsIgnoreCase(oldPhone)) {
                                        contactsService.deleteNoteContact(selectedContact.get(0).getIdContact(), socialModel.getViberLink());
                                        socialModel.setViberLink(null);
                                        selectedContact.get(0).hasViber = false;
                                    } else {
                                        String phoneViber = socialModel.getViberLink().replaceAll("[\\s\\-\\+\\(\\)]", "");
                                        String oldReplace = oldPhone.replaceAll("[\\s\\-\\+\\(\\)]", "");
                                        if (phoneViber.equalsIgnoreCase(oldReplace)) {
                                            contactsService.deleteNoteContact(selectedContact.get(0).getIdContact(), socialModel.getViberLink());
                                            socialModel.setViberLink(null);
                                            selectedContact.get(0).hasViber = false;
                                        }
                                    }
                                }

                                if (socialModel.getTelegramLink() != null && !socialModel.getTelegramLink().isEmpty()) {
                                    if (socialModel.getTelegramLink().equalsIgnoreCase(oldPhone)) {
                                        contactsService.deleteNoteContact(selectedContact.get(0).getIdContact(), socialModel.getTelegramLink());
                                        socialModel.setTelegramLink(null);
                                        selectedContact.get(0).hasTelegram = false;
                                    } else {
                                        String phoneViber = socialModel.getTelegramLink().replaceAll("[\\s\\-\\+\\(\\)]", "");
                                        String oldReplace = oldPhone.replaceAll("[\\s\\-\\+\\(\\)]", "");
                                        if (phoneViber.equalsIgnoreCase(oldReplace)) {
                                            contactsService.deleteNoteContact(selectedContact.get(0).getIdContact(), socialModel.getTelegramLink());
                                            socialModel.setTelegramLink(null);
                                            selectedContact.get(0).hasTelegram = false;
                                        }
                                    }
                                }

                                if (socialModel.getWhatsappLink() != null && !socialModel.getWhatsappLink().isEmpty()) {
                                    if (socialModel.getWhatsappLink().equalsIgnoreCase(oldPhone)) {
                                        contactsService.deleteNoteContact(selectedContact.get(0).getIdContact(), socialModel.getWhatsappLink());
                                        socialModel.setWhatsappLink(null);
                                        selectedContact.get(0).hasWhatsapp = false;
                                    } else {
                                        String phoneViber = socialModel.getWhatsappLink().replaceAll("[\\s\\-\\+\\(\\)]", "");
                                        String oldReplace = oldPhone.replaceAll("[\\s\\-\\+\\(\\)]", "");
                                        if (phoneViber.equalsIgnoreCase(oldReplace)) {
                                            contactsService.deleteNoteContact(selectedContact.get(0).getIdContact(), socialModel.getWhatsappLink());
                                            socialModel.setWhatsappLink(null);
                                            selectedContact.get(0).hasWhatsapp = false;
                                        }
                                    }
                                }

                                ((ProfileFragment) getParentFragment()).initIconColor(selectedContact.get(0), ((ProfileFragment) getParentFragment()).mainView);
                            } catch (Exception e) {

                            }*/

                            //selectedContact.webSite = "";

                            if (selectedContact.listOfContactInfo != null) {
                                for (ContactInfo ci : selectedContact.listOfContactInfo) {
                                    if (ci.value.equalsIgnoreCase(contactInfo)) {
                                        selectedContact.listOfContactInfo.remove(ci);
                                        break;
                                        //break;
                                    }
                                }
                            }
                            if (selectedContact.webSite != null && selectedContact.webSite.equalsIgnoreCase(contactInfo))
                                selectedContact.webSite = "";

                            realm.commitTransaction();
                            realm.close();


                            //contactsService.deletePhoneContact(selectedContact.get(0).getIdContact(), contactInfo.value);
                            //contactNumberAdapter.removeContactNumber(contactInfo);

                        }

                        companyAdapter.updateNEw();
                        companyAdapter.notifyDataSetChanged();
                        ((ProfileFragment) getParentFragment()).updateWeb();

                        EventBus.getDefault().post(new UpdateFile());

                        break;
                    }
                    case "email": {
                        if (((EditText) popupProfileEdit.findViewById(R.id.dataToEdit)).getText().toString().length() > 0) {
                            //contactsService.updateEmail(String.valueOf(selectedContact.get(0).getIdContact()), contactInfo.value, ((EditText) popupProfileEdit.findViewById(R.id.dataToEdit)).getText().toString());

                            Realm realm = Realm.getDefaultInstance(); //+
                            realm.beginTransaction();
                            //contactInfo.value = ((EditText) popupProfileEdit.findViewById(R.id.dataToEdit)).getText().toString();

                            if (selectedContact.listOfContactInfo != null) {
                                for (ContactInfo ci : selectedContact.listOfContactInfo) {
                                    if (ci.value.equalsIgnoreCase(contactInfo)) {
                                        ci.value = ((EditText) popupProfileEdit.findViewById(R.id.dataToEdit)).getText().toString().trim();
                                        break;
                                        //break;
                                    }
                                }
                            }

                            //selectedContact.emailCompany = ((EditText) popupProfileEdit.findViewById(R.id.dataToEdit)).getText().toString();

                            realm.commitTransaction();
                            realm.close();
                            //contactNumberAdapter.replaceContactNumber(contactInfo);
                        } else {
                            //contactsService.deleteEmailContact(String.valueOf(selectedContact.get(0).getIdContact()), contactInfo.value);
                            //contactNumberAdapter.removeContactNumber(contactInfo);

                            Realm realm = Realm.getDefaultInstance(); //+
                            realm.beginTransaction();
                            //contactInfo.value = ((EditText) popupProfileEdit.findViewById(R.id.dataToEdit)).getText().toString();

                            if (selectedContact.listOfContactInfo != null) {
                                for (ContactInfo ci : selectedContact.listOfContactInfo) {
                                    if (ci.value.equalsIgnoreCase(contactInfo)) {
                                        selectedContact.listOfContactInfo.remove(ci);
                                        break;
                                        //break;
                                    }
                                }
                            }

                            realm.commitTransaction();
                            realm.close();

                        }

                        //((ProfileFragment) getParentFragment()).contactTimelineDataFragment.checkEmail();

                        companyAdapter.updateNEw();
                        companyAdapter.notifyDataSetChanged();

                        EventBus.getDefault().post(new UpdateFile());

                        break;
                    }
                    case "intro": {
                        if (((EditText) popupProfileEdit.findViewById(R.id.dataToEdit)).getText().toString().length() > 0) {
                            //contactsService.updateLocation(String.valueOf(selectedContact.get(0).getIdContact()), contactInfo.value, ((EditText) popupProfileEdit.findViewById(R.id.dataToEdit)).getText().toString());

                            Realm realm = Realm.getDefaultInstance();
                            realm.beginTransaction();
                            selectedContact.shortInto = ((EditText) popupProfileEdit.findViewById(R.id.dataToEdit)).getText().toString();
                            realm.commitTransaction();
                            realm.close();

                        } else {
                            Realm realm = Realm.getDefaultInstance();
                            realm.beginTransaction();
                            selectedContact.shortInto = "";
                            realm.commitTransaction();
                            realm.close();
                        }

                        companyAdapter.updateNEw();
                        companyAdapter.notifyDataSetChanged();
                        EventBus.getDefault().post(new UpdateFile());

                        break;
                    }
                }

                closeOtherPopup();

            }
        });

        popupProfileEdit.findViewById(R.id.cancelEditField).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupProfileEdit.setVisibility(View.GONE);
                popupProfileEditPreview.setVisibility(View.VISIBLE);
                openedViews.add(popupProfileEditPreview);
            }
        });

        popupProfileEdit.setVisibility(View.VISIBLE);


    }

    private void sendEmail(String email) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:" + email));
        startActivity(Intent.createChooser(emailIntent, "Send email"));
    }

    public void showPopupEditDataCompany(String type) {

        ((EditText) popupEditDataCompany.findViewById(R.id.dataToEdit)).setText("");


        if (type.equalsIgnoreCase("web")) {
            ((ImageView) popupEditDataCompany.findViewById(R.id.imageTypeData)).setImageDrawable(getContext().getResources().getDrawable(R.drawable.icn_popup_web_blue));
            ((TextView) popupEditDataCompany.findViewById(R.id.textNameData)).setText("Web");
            ((EditText) popupEditDataCompany.findViewById(R.id.dataToEdit)).setHint("Add web or search");
        } else if (type.equalsIgnoreCase("email")) {
            ((ImageView) popupEditDataCompany.findViewById(R.id.imageTypeData)).setImageDrawable(getContext().getResources().getDrawable(R.drawable.icn_bottombar_emails_blue));
            ((TextView) popupEditDataCompany.findViewById(R.id.textNameData)).setText("Email");
            ((EditText) popupEditDataCompany.findViewById(R.id.dataToEdit)).setHint("Add email or search");
        } else if (type.equalsIgnoreCase("intro")) {
            ((ImageView) popupEditDataCompany.findViewById(R.id.imageTypeData)).setImageDrawable(getContext().getResources().getDrawable(R.drawable.icn_companies));
            ((TextView) popupEditDataCompany.findViewById(R.id.textNameData)).setText("Intro");
            ((EditText) popupEditDataCompany.findViewById(R.id.dataToEdit)).setHint("Add intro or search");
            //((EditText)popupEditDataCompany.findViewById(R.id.dataToEdit)).setText(selectedContact.getName());
        }

       /* popupEditDataCompany.findViewById(R.id.imageEditSocial).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                String term = selectedContact.getName();
                intent.putExtra(SearchManager.QUERY, term);
                activityApp.startActivity(intent);
            }
        });*/

        popupEditDataCompany.findViewById(R.id.searchSocial).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                String term = selectedContact.getName();
                intent.putExtra(SearchManager.QUERY, term);
                activityApp.startActivity(intent);
            }
        });

        popupEditDataCompany.findViewById(R.id.get_last_clips).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String socialLinkClip = "";
                for (Clipboard cl : FabNotificationService.clipboard) { //NullPointerException
                    if (cl.getListClipboards() != null && !cl.getListClipboards().isEmpty()) {
                        boolean checkFind = false;
                        for (Clipboard cl2 : cl.getListClipboards()) {

                            if (type.equalsIgnoreCase("web")) {
                                if (cl2.getType().equals(ClipboardEnum.WEB)) {
                                    socialLinkClip = cl2.getValueCopy();
                                    checkFind = true;
                                    break;
                                }
                            } else if (type.equalsIgnoreCase("email")) {
                                if (cl2.getType().equals(ClipboardEnum.EMAIL)) {
                                    socialLinkClip = cl2.getValueCopy();
                                    checkFind = true;
                                    break;
                                }
                            } else if (type.equalsIgnoreCase("intro")) {
                                if (cl2.getType().equals(ClipboardEnum.NOTE)) {
                                    socialLinkClip = cl2.getValueCopy();
                                    checkFind = true;
                                    break;
                                }
                            }


                        }
                        if (checkFind) break;
                    } else {

                        if (type.equalsIgnoreCase("web")) {
                            if (cl.getType().equals(ClipboardEnum.WEB)) {
                                socialLinkClip = cl.getValueCopy();
                                break;
                            }
                        } else if (type.equalsIgnoreCase("email")) {
                            if (cl.getType().equals(ClipboardEnum.EMAIL)) {
                                socialLinkClip = cl.getValueCopy();
                                break;
                            }
                        } else if (type.equalsIgnoreCase("intro")) {
                            if (cl.getType().equals(ClipboardEnum.NOTE)) {
                                socialLinkClip = cl.getValueCopy();
                                break;
                            }
                        }

                    }
                }
                ((EditText) popupEditDataCompany.findViewById(R.id.dataToEdit)).setText(socialLinkClip);

            }
        });


        popupEditDataCompany.findViewById(R.id.cancelEditField).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupEditDataCompany.setVisibility(View.GONE);
            }
        });

        popupEditDataCompany.findViewById(R.id.updateContactField).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((EditText) popupEditDataCompany.findViewById(R.id.dataToEdit)).getText().length() > 0) {
                    if (type.equalsIgnoreCase("web")) {
                        Realm realm = Realm.getDefaultInstance();
                        realm.beginTransaction();


                        if (selectedContact.webSite == null || selectedContact.webSite.isEmpty())
                            selectedContact.webSite = ((EditText) popupEditDataCompany.findViewById(R.id.dataToEdit)).getText().toString().trim();
                        else {
                            if (selectedContact.listOfContactInfo == null || selectedContact.listOfContactInfo.isEmpty()) {
                                selectedContact.addNote(((EditText) popupEditDataCompany.findViewById(R.id.dataToEdit)).getText().toString().trim());
                            } else {
                                boolean checK_C = true;
                                for (ContactInfo ci : selectedContact.getListOfContactInfo()) {
                                    if (ci.value.equalsIgnoreCase(((EditText) popupEditDataCompany.findViewById(R.id.dataToEdit)).getText().toString().trim())) {
                                        //Toast.makeText(MainActivity.this, "Data already exists", Toast.LENGTH_SHORT).show();
                                        checK_C = false;
                                        break;
                                    }
                                }
                                if (checK_C) {
                                    selectedContact.addNote(((EditText) popupEditDataCompany.findViewById(R.id.dataToEdit)).getText().toString().trim());
                                }
                            }
                        }
                        realm.commitTransaction();
                        realm.close();

                        try {
                            ((ProfileFragment) getParentFragment()).updateWeb();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else if (type.equalsIgnoreCase("email")) {
                        Realm realm = Realm.getDefaultInstance();
                        realm.beginTransaction();

                        boolean check = true;
                        for (ContactInfo contactInfo : selectedContact.getListOfContactInfo()) {
                            if (contactInfo.value.trim().equals(((EditText) popupEditDataCompany.findViewById(R.id.dataToEdit)).getText().toString().trim()))
                                check = false;
                        }
                        if (check) {
                            selectedContact.addEmail(((EditText) popupEditDataCompany.findViewById(R.id.dataToEdit)).getText().toString().trim());
                            //contactsService.addMailToContact(contact.getIdContact(), extrator.getValueCopy().trim());
                        }

                        //selectedContact.emailCompany = ;
                        realm.commitTransaction();
                        realm.close();
                    } else if (type.equalsIgnoreCase("intro")) {
                        Realm realm = Realm.getDefaultInstance();
                        realm.beginTransaction();
                        selectedContact.shortInto = ((EditText) popupEditDataCompany.findViewById(R.id.dataToEdit)).getText().toString().trim();
                        realm.commitTransaction();
                        realm.close();
                    }
                }
                companyAdapter.updateNEw();
                companyAdapter.notifyDataSetChanged();
                popupEditDataCompany.setVisibility(View.GONE);

                EventBus.getDefault().post(new UpdateFile());
            }
        });


        openedViews.add(popupEditDataCompany);
        popupEditDataCompany.setVisibility(View.VISIBLE);
    }


}
