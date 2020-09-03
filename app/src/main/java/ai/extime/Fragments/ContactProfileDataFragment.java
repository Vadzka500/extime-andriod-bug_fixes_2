package ai.extime.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;

import ai.extime.Activity.MainActivity;
import ai.extime.Adapters.ContactNumberAdapter;
import ai.extime.Adapters.ContactNumberEditAdapter;
import ai.extime.Interfaces.IMessage;
import ai.extime.Interfaces.PopupsInter;
import ai.extime.Models.EmailMessage;
import ai.extime.Models.SocialModel;
import ai.extime.Models.Template;
import ai.extime.Services.ContactCacheService;
import ai.extime.Utils.ShareTemplatesMessageReply;
import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import ai.extime.Events.UpdateFile;
import ai.extime.Models.Contact;
import ai.extime.Models.ContactInfo;

import com.extime.R;

import ai.extime.Services.ContactsService;
import ai.extime.Utils.ClipboardType;

/**
 * Created by patal on 21.09.2017.
 */

public class ContactProfileDataFragment extends Fragment implements PopupsInter, IMessage {

    View mainView;

    //View rootView;

    public ArrayList<Contact> selectedContact;

    RecyclerView listOfProfileDataRV;

    ContactNumberAdapter contactNumberAdapter;

    //private Activity activityApp;

    ArrayList<View> openedViews = new ArrayList<>();

    private FrameLayout popupProfileEditPreview;

    public ContactNumberEditAdapter contactNumberEditAdapter;

    public FrameLayout popupProfileEdit;

    private ContactsService contactsService;

    public static boolean checkRealPhone = true;

    public FrameLayout frameClick;

    public int getQuantityOpenedViews() {
        return openedViews.size();
    }


    @SuppressLint("ClickableViewAccessibility")
    private void initRecycler() {
        listOfProfileDataRV = (RecyclerView) mainView.findViewById(R.id.recycler_view);

        listOfProfileDataRV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        //   Date date = selectedContact.getDateCreate();

        if (selectedContact == null || selectedContact.isEmpty() || selectedContact.size() == 1) {

            if (selectedContact == null || selectedContact.isEmpty()) {

                Contact createdContact = new Contact(new Date());
                createdContact.setCompany(null);
                createdContact.setCompanyPossition(null);
            /*Time time = getRandomDate();
            //  createdContact.time = time;
            createdContact.time = "";*/
                selectedContact = new ArrayList<Contact>();
                selectedContact.add(createdContact);
            }

            if (selectedContact.get(0).listOfContactInfo == null)
                selectedContact.get(0).listOfContactInfo = new RealmList<>();

            if (ContactsFragment.createContact || ProfileFragment.nowEdit || (selectedContact.get(0) != null && selectedContact.get(0).getId() == 0))
                contactNumberEditAdapter = new ContactNumberEditAdapter(mainView.getContext(), selectedContact.get(0).listOfContactInfo, this);
            else
                contactNumberAdapter = new ContactNumberAdapter(mainView.getContext(), selectedContact.get(0).listOfContactInfo, this, getActivity());


        } else if (selectedContact != null && !selectedContact.isEmpty() && selectedContact.size() > 1) {

            contactNumberAdapter = new ContactNumberAdapter(mainView.getContext(), selectedContact, this, getActivity());

        }
        //Toast.makeText(getActivity(), String.valueOf(ContactsFragment.createContact), Toast.LENGTH_SHORT).show();


        LinearLayoutManager llm = new LinearLayoutManager(mainView.getContext());

        llm.setOrientation(LinearLayoutManager.VERTICAL);
        listOfProfileDataRV.setLayoutManager(llm);
        //((LinearLayoutManager)listOfProfileDataRV.getLayoutManager()).setStackFromEnd(true);

        if (ContactsFragment.createContact || ProfileFragment.nowEdit || (selectedContact.get(0) != null && selectedContact.get(0).getId() == 0))
            listOfProfileDataRV.setAdapter(contactNumberEditAdapter);
        else
            listOfProfileDataRV.setAdapter(contactNumberAdapter);

        mainView.findViewById(R.id.closerContactProfile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    ((ProfileFragment) getParentFragment()).closeOtherPopup();
                } catch (Exception e) {
                    ((CreateFragment) getParentFragment()).closeOtherPopup();
                }
                closeOtherPopup();
            }
        });

        frameClick = (FrameLayout) mainView.findViewById(R.id.closerContactProfile);

        listOfProfileDataRV.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                frameClick.dispatchTouchEvent(motionEvent);

                try {
                    ((ProfileFragment) getParentFragment()).closeOtherPopup();
                } catch (Exception e) {
                    ((CreateFragment) getParentFragment()).closeOtherPopup();
                }
                closeOtherPopup();


                return false;
            }
        });
    }

    public void setDefaultAdapter(Activity activityApp) {
        //  Date date = selectedContact.getDateCreate();

        contactNumberAdapter = new ContactNumberAdapter(activityApp, selectedContact.get(0).listOfContactInfo, this, activityApp);
        listOfProfileDataRV.setAdapter(contactNumberAdapter);

    }


    public void setDefaultAdapterDolo() {
        contactNumberAdapter = new ContactNumberAdapter(mainView.getContext(), selectedContact, this, getActivity());
        listOfProfileDataRV.setAdapter(contactNumberAdapter);
    }

    public void setBlur(boolean blur) {
        contactNumberAdapter.setBlur(blur);
    }

    public void setEditAdapter() {

        if (listOfProfileDataRV == null)
            listOfProfileDataRV = (RecyclerView) mainView.findViewById(R.id.recycler_view);
        LinearLayoutManager llm = new LinearLayoutManager(mainView.getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        listOfProfileDataRV.setLayoutManager(llm);


//        if (!selectedContact.listOfContactInfo.contains(new ContactInfo(new ContactInfo("Joined", "" + date.getTime(), true, false, false, false, false)))) {
//            selectedContact.listOfContactInfo.add(new ContactInfo(new ContactInfo("Joined", "" + date.getTime(), true, false, false, false, false)));
//        }
//        selectedContact.listOfContactInfo.remove(new ContactInfo(new ContactInfo("Joined", ""+selectedContact.getDateCreate().getTime(), true, false, false, false, false)));

        RealmList<ContactInfo> listOfContactInfo = new RealmList<>();
        for (ContactInfo contactInfo : selectedContact.get(0).listOfContactInfo) {
            if (contactInfo.value.equals("+000000000000")) {
                ContactProfileDataFragment.checkRealPhone = false;
                // listOfContactInfo.add(contactInfo);
            }
            //else
            // listOfContactInfo.add(contactInfo);
        }
        contactNumberEditAdapter = new ContactNumberEditAdapter(mainView.getContext(), selectedContact.get(0).listOfContactInfo, this);
        listOfProfileDataRV.setAdapter(contactNumberEditAdapter);
    }

    public void setEditAdapter2(ArrayList<Contact> sel, Activity activityApp) {
        listOfProfileDataRV = (RecyclerView) activityApp.findViewById(R.id.recycler_view);
        LinearLayoutManager llm = new LinearLayoutManager(activityApp);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        listOfProfileDataRV.setLayoutManager(llm);


//        if (!selectedContact.listOfContactInfo.contains(new ContactInfo(new ContactInfo("Joined", "" + date.getTime(), true, false, false, false, false)))) {
//            selectedContact.listOfContactInfo.add(new ContactInfo(new ContactInfo("Joined", "" + date.getTime(), true, false, false, false, false)));
//        }
//        selectedContact.listOfContactInfo.remove(new ContactInfo(new ContactInfo("Joined", ""+selectedContact.getDateCreate().getTime(), true, false, false, false, false)));

        selectedContact = sel;
        RealmList<ContactInfo> listOfContactInfo = new RealmList<>();
        for (ContactInfo contactInfo : selectedContact.get(0).listOfContactInfo) {
            if (contactInfo.value.equals("+000000000000")) {
                ContactProfileDataFragment.checkRealPhone = false;
                // listOfContactInfo.add(contactInfo);
            }
            //else
            // listOfContactInfo.add(contactInfo);
        }
        contactNumberEditAdapter = new ContactNumberEditAdapter(activityApp, selectedContact.get(0).listOfContactInfo, this);
        listOfProfileDataRV.setAdapter(contactNumberEditAdapter);
    }


    private void getDataFromBundle() {
        Bundle args = getArguments();
        try {
            selectedContact = (ArrayList<Contact>) args.getSerializable("selectedContact");

        } catch (Exception e) {

            selectedContact.add((Contact) args.getSerializable("selectedContact"));
        }


    }

   /* private Time getRandomDate(){
        final Random random = new Random();
        final int millisInDay = 24*60*60*1000;
        return new Time((long)random.nextInt(millisInDay));
    }*/

    private void initViews() {

        if (selectedContact != null && !selectedContact.isEmpty() && selectedContact.size() == 1) {

            popupProfileEditPreview = (FrameLayout) getParentFragment().getView().findViewById(R.id.popupPreviewEdit);
            popupProfileEdit = (FrameLayout) getParentFragment().getView().findViewById(R.id.popupEditProfile);
        } else {
            popupProfileEditPreview = (FrameLayout) getParentFragment().getView().findViewById(R.id.popupPreviewEditCreate);
            popupProfileEdit = (FrameLayout) getParentFragment().getView().findViewById(R.id.popupEditProfileCreate);
        }

        contactsService = new ContactsService(getActivity().getContentResolver(), false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle bundle) {
        mainView = inflater.inflate(R.layout.fragment_contact_profile_data_list, viewGroup, false);
        //rootView = mainView.getRootView();
        selectedContact = new ArrayList<>();

        getDataFromBundle();
        initViews();
        initRecycler();


        //mainView.getForeground().setAlpha(220);
        //setEditAdapter();

        return mainView;
    }

    public void showEditPopup(ContactInfo contactInfo, String type) {
        closeOtherPopup();
        openedViews.add(popupProfileEdit);
        ((EditText) popupProfileEdit.findViewById(R.id.dataToEdit)).setText(contactInfo.value);





     /*   if (!contactInfo.type.equalsIgnoreCase("date of birth")) {

            popupProfileEdit.findViewById(R.id.typeField).setVisibility(View.VISIBLE);
            popupProfileEdit.findViewById(R.id.typeFieldEdit).setVisibility(View.GONE);

            //popupProfileEdit.findViewById(R.id.t_social).setVisibility(View.GONE);


            ((TextView) popupProfileEdit.findViewById(R.id.typeField)).setText(type);
        } else {*/

            popupProfileEdit.findViewById(R.id.typeField).setVisibility(View.GONE);
            popupProfileEdit.findViewById(R.id.typeFieldEdit).setVisibility(View.VISIBLE);

            if (contactInfo.titleValue == null || contactInfo.titleValue.isEmpty()) {

                if(contactInfo.type.equalsIgnoreCase("date of birth")) {
                    ((EditText) popupProfileEdit.findViewById(R.id.typeFieldEdit)).setText("Birthday");
                }else{

                    StringBuilder builder = new StringBuilder(type);
                    //выставляем первый символ заглавным, если это буква
                    if (Character.isAlphabetic(type.codePointAt(0)))
                        builder.setCharAt(0, Character.toUpperCase(type.charAt(0)));

                    ((EditText) popupProfileEdit.findViewById(R.id.typeFieldEdit)).setText(builder);
                }

            } else
                ((EditText) popupProfileEdit.findViewById(R.id.typeFieldEdit)).setText(contactInfo.titleValue);

       // }

        ((TextView) popupProfileEdit.findViewById(R.id.typeField)).requestLayout();


        if (ClipboardType.isFacebook(contactInfo.value)) {

            ((ImageView) popupProfileEdit.findViewById(R.id.iconDataEdit)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_social_facebook));

        } else if (ClipboardType.isInsta(contactInfo.value)) {

            ((ImageView) popupProfileEdit.findViewById(R.id.iconDataEdit)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_social_instagram));

        } else if (ClipboardType.isG_Sheet(contactInfo.value)) {

            ((ImageView) popupProfileEdit.findViewById(R.id.iconDataEdit)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.google_sheets));

        } else if (ClipboardType.isG_Doc(contactInfo.value)) {

            ((ImageView) popupProfileEdit.findViewById(R.id.iconDataEdit)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.google_docs));

        } else if (ClipboardType.isTwitter(contactInfo.value)) {

            ((ImageView) popupProfileEdit.findViewById(R.id.iconDataEdit)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_twitter_64));

        } else if (ClipboardType.isYoutube(contactInfo.value)) {

            ((ImageView) popupProfileEdit.findViewById(R.id.iconDataEdit)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_youtube_48));

        } else if (ClipboardType.isVk(contactInfo.value)) {

            ((ImageView) popupProfileEdit.findViewById(R.id.iconDataEdit)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_social_vk));

        } else if (ClipboardType.isLinkedIn(contactInfo.value)) {

            ((ImageView) popupProfileEdit.findViewById(R.id.iconDataEdit)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_social_linkedin));

        } else if (ClipboardType.isTelegram(contactInfo.value)) {

            ((ImageView) popupProfileEdit.findViewById(R.id.iconDataEdit)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_social_telegram));

        } else if (ClipboardType.is_Tumblr(contactInfo.value)) {

            ((ImageView) popupProfileEdit.findViewById(R.id.iconDataEdit)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.tumblr));

        } else if (ClipboardType.isMedium(contactInfo.value)) {

            ((ImageView) popupProfileEdit.findViewById(R.id.iconDataEdit)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.medium_size_64));

        } else if (ClipboardType.is_Angel(contactInfo.value)) {

            ((ImageView) popupProfileEdit.findViewById(R.id.iconDataEdit)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.angel));

        } else if (ClipboardType.isEmail(contactInfo.value)) {

            ((ImageView) popupProfileEdit.findViewById(R.id.iconDataEdit)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_emails));

        } else if (type.equalsIgnoreCase("date of birth")) {

            ((ImageView) popupProfileEdit.findViewById(R.id.iconDataEdit)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.gift_blue));

        }else if (type.equalsIgnoreCase("bio")) {

            ((ImageView) popupProfileEdit.findViewById(R.id.iconDataEdit)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.intro_n));

        } else if (ClipboardType.isPhone(contactInfo.value)) {

            ((ImageView) popupProfileEdit.findViewById(R.id.iconDataEdit)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_phone));

        } else if (ClipboardType.isWeb(contactInfo.value)) {

            ((ImageView) popupProfileEdit.findViewById(R.id.iconDataEdit)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_popup_web_blue));

        } else {
            if (type.equalsIgnoreCase("address")) {

                ((ImageView) popupProfileEdit.findViewById(R.id.iconDataEdit)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_fab_maps));
            } else {

                ((ImageView) popupProfileEdit.findViewById(R.id.iconDataEdit)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_notes));
            }
        }

        //popupProfileEdit.findViewById(R.id.imageEditSocial).setVisibility(View.GONE);
        //popupProfileEdit.findViewById(R.id.frameImageEditSocialInsta).setVisibility(View.GONE);

        //popupProfileEdit.findViewById(R.id.get_last_clips).setVisibility(View.GONE);

        //  if (contactInfo.isPhone || contactInfo.isEmail) {
        ((ImageView) popupProfileEdit.findViewById(R.id.ok_social)).setVisibility(View.VISIBLE);
      /*  }else{
            ((ImageView) popupProfileEdit.findViewById(R.id.ok_social)).setVisibility(View.GONE);
        }*/

        if (!contactInfo.isPrimary) {
            ((ImageView) popupProfileEdit.findViewById(R.id.ok_social)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
        } else {
            ((ImageView) popupProfileEdit.findViewById(R.id.ok_social)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.orange));
        }

        popupProfileEdit.findViewById(R.id.ok_social).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (contactInfo.isPhone || contactInfo.isEmail) {
                    if (!contactInfo.isPrimary) {
                        if (contactInfo.isPhone) {
                            contactsService.setPhoneIsPrimary(selectedContact.get(0).getIdContact(), contactInfo.value, true);
                            for (ContactInfo ci : selectedContact.get(0).getListOfContactInfo()) {
                                if (ci.isPhone && ci.isPrimary) {
                                    contactsService.setPhoneIsPrimary(selectedContact.get(0).getIdContact(), ci.value, false);
                                    ContactCacheService.setPrimaryInfo(ci, false);
                                }
                            }
                        } else {
                            contactsService.setMailIsPrimary(selectedContact.get(0).getIdContact(), contactInfo.value, true);

                            for (ContactInfo ci : selectedContact.get(0).getListOfContactInfo()) {
                                if (ci.isEmail && ci.isPrimary) {
                                    contactsService.setMailIsPrimary(selectedContact.get(0).getIdContact(), ci.value, false);
                                    ContactCacheService.setPrimaryInfo(ci, false);
                                }
                            }
                        }

                        //((ImageView) popupProfileEdit.findViewById(R.id.ok_social)).setImageTintMode(null);
                        ((ImageView) popupProfileEdit.findViewById(R.id.ok_social)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.orange));

                        ContactCacheService.setPrimaryInfo(contactInfo, true);
                        Toast.makeText(mainView.getContext(), "Set as default", Toast.LENGTH_SHORT).show();
                    } else {
                        if (contactInfo.isPhone)
                            contactsService.setPhoneIsPrimary(selectedContact.get(0).getIdContact(), contactInfo.value, false);
                        else
                            contactsService.setMailIsPrimary(selectedContact.get(0).getIdContact(), contactInfo.value, false);

                        ((ImageView) popupProfileEdit.findViewById(R.id.ok_social)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
                        ContactCacheService.setPrimaryInfo(contactInfo, false);
                        Toast.makeText(mainView.getContext(), "Deleted from default", Toast.LENGTH_SHORT).show();
                    }
                    contactNumberAdapter.sortByPrimary();
                    contactNumberAdapter.notifyDataSetChanged();
                }
            }
        });


        popupProfileEdit.setOnClickListener(v -> {
        });
        popupProfileEdit.findViewById(R.id.updateContactField).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (contactInfo.typeData.toString()) {
                    case "PHONE": {
                        if (((EditText) popupProfileEdit.findViewById(R.id.dataToEdit)).getText().toString().length() > 0) {
                            contactsService.updatePhone(String.valueOf(selectedContact.get(0).getIdContact()), contactInfo.value, ((EditText) popupProfileEdit.findViewById(R.id.dataToEdit)).getText().toString());

                            Realm realm = Realm.getDefaultInstance(); //+
                            realm.beginTransaction();

                            try {
                                for (int i = 0; i < CreateFragment.createdContact.listOfContactInfo.size(); i++) {
                                    if (CreateFragment.createdContact.listOfContactInfo.get(i).value.trim().equals(contactInfo.value.trim())) {
                                        CreateFragment.createdContact.listOfContactInfo.get(i).value = ((EditText) popupProfileEdit.findViewById(R.id.dataToEdit)).getText().toString().trim();
                                    }
                                }
                            } catch (Exception e) {

                            }



                            try {
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

                            }


                            contactInfo.value = ((EditText) popupProfileEdit.findViewById(R.id.dataToEdit)).getText().toString().trim();
                            realm.commitTransaction();
                            realm.close();
                            contactNumberAdapter.replaceContactNumber(contactInfo);


                        } else {

                            Realm realm = Realm.getDefaultInstance(); //+
                            realm.beginTransaction();

                            try {
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

                            }
                            realm.commitTransaction();
                            realm.close();


                            contactsService.deletePhoneContact(selectedContact.get(0).getIdContact(), contactInfo.value);
                            contactNumberAdapter.removeContactNumber(contactInfo);
                        }


                        //EventBus.getDefault().post(new UpdateFile());

                        break;
                    }
                    case "EMAIL": {
                        if (((EditText) popupProfileEdit.findViewById(R.id.dataToEdit)).getText().toString().length() > 0) {
                            contactsService.updateEmail(String.valueOf(selectedContact.get(0).getIdContact()), contactInfo.value, ((EditText) popupProfileEdit.findViewById(R.id.dataToEdit)).getText().toString());

                            Realm realm = Realm.getDefaultInstance(); //+
                            realm.beginTransaction();
                            contactInfo.value = ((EditText) popupProfileEdit.findViewById(R.id.dataToEdit)).getText().toString();
                            realm.commitTransaction();
                            realm.close();
                            contactNumberAdapter.replaceContactNumber(contactInfo);
                        } else {
                            contactsService.deleteEmailContact(String.valueOf(selectedContact.get(0).getIdContact()), contactInfo.value);
                            contactNumberAdapter.removeContactNumber(contactInfo);
                        }

                        ((ProfileFragment) getParentFragment()).contactTimelineDataFragment.checkEmail();

                        //EventBus.getDefault().post(new UpdateFile());

                        break;
                    }
                    case "LOCATION": {
                        if (((EditText) popupProfileEdit.findViewById(R.id.dataToEdit)).getText().toString().length() > 0) {
                            contactsService.updateLocation(String.valueOf(selectedContact.get(0).getIdContact()), contactInfo.value, ((EditText) popupProfileEdit.findViewById(R.id.dataToEdit)).getText().toString());

                            Realm realm = Realm.getDefaultInstance(); //+
                            realm.beginTransaction();
                            contactInfo.value = ((EditText) popupProfileEdit.findViewById(R.id.dataToEdit)).getText().toString();
                            realm.commitTransaction();
                            realm.close();
                            contactNumberAdapter.replaceContactNumber(contactInfo);
                        } else {
                            contactsService.deleteEmailContact(String.valueOf(selectedContact.get(0).getIdContact()), contactInfo.value);
                            contactNumberAdapter.removeContactNumber(contactInfo);
                        }

                        //EventBus.getDefault().post(new UpdateFile());

                        break;
                    }
                    case "NOTE": {
                        if (((EditText) popupProfileEdit.findViewById(R.id.dataToEdit)).getText().toString().length() > 0) {

                            Realm realm = Realm.getDefaultInstance(); //+

                            contactsService.updateNote(String.valueOf(selectedContact.get(0).getIdContact()), contactInfo.value.trim(), ((EditText) popupProfileEdit.findViewById(R.id.dataToEdit)).getText().toString().trim());
                            realm.beginTransaction();
                            contactInfo.value = ((EditText) popupProfileEdit.findViewById(R.id.dataToEdit)).getText().toString();

                            if(contactInfo.type.equalsIgnoreCase("date of birth")){
                                contactInfo.titleValue = ((EditText) popupProfileEdit.findViewById(R.id.typeFieldEdit)).getText().toString().trim();
                            }

                            realm.commitTransaction();
                            realm.close();
                            //contactNumberAdapter.replaceContactNumber(contactInfo);
                            contactNumberAdapter.notifyDataSetChanged();
                        } else {
                            contactsService.deleteNoteContact(String.valueOf(selectedContact.get(0).getIdContact()), contactInfo.value.trim());
                            contactNumberAdapter.removeContactNumber(contactInfo);
                        }

                        //EventBus.getDefault().post(new UpdateFile());

                        break;
                    }
                }

                if(contactInfo.type.equalsIgnoreCase("date of birth")){

                    if (((EditText) popupProfileEdit.findViewById(R.id.dataToEdit)).getText().toString().length() > 0) {

                        Realm realm = Realm.getDefaultInstance(); //+

                        contactsService.updateNote(String.valueOf(selectedContact.get(0).getIdContact()), contactInfo.value.trim(), ((EditText) popupProfileEdit.findViewById(R.id.dataToEdit)).getText().toString().trim());
                        realm.beginTransaction();
                        contactInfo.value = ((EditText) popupProfileEdit.findViewById(R.id.dataToEdit)).getText().toString();

                        if(contactInfo.type.equalsIgnoreCase("date of birth")){

                            contactInfo.titleValue = ((EditText) popupProfileEdit.findViewById(R.id.typeFieldEdit)).getText().toString().trim();
                        }

                        realm.commitTransaction();
                        realm.close();
                        //contactNumberAdapter.replaceContactNumber(contactInfo);
                        contactNumberAdapter.notifyDataSetChanged();
                    } else {
                        contactsService.deleteNoteContact(String.valueOf(selectedContact.get(0).getIdContact()), contactInfo.value.trim());
                        contactNumberAdapter.removeContactNumber(contactInfo);
                    }

                    //EventBus.getDefault().post(new UpdateFile());
                }

                if(contactInfo.type.equalsIgnoreCase("bio")){

                    if (((EditText) popupProfileEdit.findViewById(R.id.dataToEdit)).getText().toString().length() > 0) {

                        Realm realm = Realm.getDefaultInstance(); //+

                        //contactsService.updateNote(String.valueOf(selectedContact.get(0).getIdContact()), contactInfo.value.trim(), ((EditText) popupProfileEdit.findViewById(R.id.dataToEdit)).getText().toString().trim());
                        realm.beginTransaction();
                        contactInfo.value = ((EditText) popupProfileEdit.findViewById(R.id.dataToEdit)).getText().toString();

                        if(contactInfo.type.equalsIgnoreCase("date of birth")){

                            contactInfo.titleValue = ((EditText) popupProfileEdit.findViewById(R.id.typeFieldEdit)).getText().toString().trim();
                        }

                        realm.commitTransaction();
                        realm.close();
                        //contactNumberAdapter.replaceContactNumber(contactInfo);
                        contactNumberAdapter.notifyDataSetChanged();
                    } else {
                        contactsService.deleteNoteContact(String.valueOf(selectedContact.get(0).getIdContact()), contactInfo.value.trim());
                        contactNumberAdapter.removeContactNumber(contactInfo);
                    }

                    //EventBus.getDefault().post(new UpdateFile());
                }

                Realm realm = Realm.getDefaultInstance(); //+
                realm.beginTransaction();
                //if(contactInfo.type.equalsIgnoreCase("date of birth")){

                    contactInfo.titleValue = ((EditText) popupProfileEdit.findViewById(R.id.typeFieldEdit)).getText().toString().trim();
                //}

                realm.commitTransaction();
                realm.close();

                //popupProfileEdit.setVisibility(View.GONE);
                frameClick.callOnClick();
                //popupProfileEditPreview.setVisibility(View.VISIBLE);
                //openedViews.add(popupProfileEditPreview);

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


    public void showEditPopupPreview(ContactInfo contactInfo, String type) {

        closeOtherPopup();
        try {
            ((ProfileFragment) getParentFragment()).closeOtherPopup();
        } catch (Exception e) {
            ((CreateFragment) getParentFragment()).closeOtherPopup();
        }
        popupProfileEditPreview.setVisibility(View.VISIBLE);
        popupProfileEditPreview.setOnClickListener(v -> {
        });


        ((ImageView) popupProfileEditPreview.findViewById(R.id.imageCall)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_fab_google));

        ((TextView) popupProfileEditPreview.findViewById(R.id.valueUpd)).setTextColor(getActivity().getResources().getColor(R.color.primary_dark));

        popupProfileEditPreview.findViewById(R.id.imageView3).setVisibility(View.GONE);
        popupProfileEditPreview.findViewById(R.id.search_note_popup).setVisibility(View.VISIBLE);

        ((TextView) popupProfileEditPreview.findViewById(R.id.typeField)).setTextColor(getActivity().getResources().getColor(R.color.gray_textView));

        Matcher matcher = Patterns.WEB_URL.matcher(contactInfo.value.toString());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ((ImageView) popupProfileEditPreview.findViewById(R.id.imageCall)).setImageTintMode(null);
        }
        if (ClipboardType.isFacebook(contactInfo.value)) {
            ((ImageView) popupProfileEditPreview.findViewById(R.id.imageCall)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_social_facebook));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ((ImageView) popupProfileEditPreview.findViewById(R.id.imageCall)).setImageTintMode(null);
            }

            ((ImageView) popupProfileEditPreview.findViewById(R.id.iconDataProfile)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_social_facebook));

        } else if (ClipboardType.isInsta(contactInfo.value)) {
            ((ImageView) popupProfileEditPreview.findViewById(R.id.imageCall)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_social_instagram));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ((ImageView) popupProfileEditPreview.findViewById(R.id.imageCall)).setImageTintMode(null);
            }
            ((ImageView) popupProfileEditPreview.findViewById(R.id.iconDataProfile)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_social_instagram));

        } else if (ClipboardType.isG_Sheet(contactInfo.value)) {
            ((ImageView) popupProfileEditPreview.findViewById(R.id.imageCall)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.google_sheets));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ((ImageView) popupProfileEditPreview.findViewById(R.id.imageCall)).setImageTintMode(null);
            }

            ((ImageView) popupProfileEditPreview.findViewById(R.id.iconDataProfile)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.google_sheets));

        } else if (ClipboardType.isG_Doc(contactInfo.value)) {
            ((ImageView) popupProfileEditPreview.findViewById(R.id.imageCall)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.google_docs));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ((ImageView) popupProfileEditPreview.findViewById(R.id.imageCall)).setImageTintMode(null);
            }

            ((ImageView) popupProfileEditPreview.findViewById(R.id.iconDataProfile)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.google_docs));

        } else if (ClipboardType.isTwitter(contactInfo.value)) {
            ((ImageView) popupProfileEditPreview.findViewById(R.id.imageCall)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_twitter_64));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ((ImageView) popupProfileEditPreview.findViewById(R.id.imageCall)).setImageTintMode(null);
            }

            ((ImageView) popupProfileEditPreview.findViewById(R.id.iconDataProfile)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_twitter_64));

        } else if (ClipboardType.isYoutube(contactInfo.value)) {
            ((ImageView) popupProfileEditPreview.findViewById(R.id.imageCall)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_youtube_48));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ((ImageView) popupProfileEditPreview.findViewById(R.id.imageCall)).setImageTintMode(null);
            }

            ((ImageView) popupProfileEditPreview.findViewById(R.id.iconDataProfile)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_youtube_48));

        } else if (ClipboardType.isVk(contactInfo.value)) {
            ((ImageView) popupProfileEditPreview.findViewById(R.id.imageCall)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_social_vk));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ((ImageView) popupProfileEditPreview.findViewById(R.id.imageCall)).setImageTintMode(null);
            }

            ((ImageView) popupProfileEditPreview.findViewById(R.id.iconDataProfile)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_social_vk));

        } else if (ClipboardType.isLinkedIn(contactInfo.value)) {
            ((ImageView) popupProfileEditPreview.findViewById(R.id.imageCall)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_social_linkedin));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ((ImageView) popupProfileEditPreview.findViewById(R.id.imageCall)).setImageTintMode(null);
            }

            ((ImageView) popupProfileEditPreview.findViewById(R.id.iconDataProfile)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_social_linkedin));

        } else if (ClipboardType.isTelegram(contactInfo.value)) {
            ((ImageView) popupProfileEditPreview.findViewById(R.id.imageCall)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_social_telegram));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ((ImageView) popupProfileEditPreview.findViewById(R.id.imageCall)).setImageTintMode(null);
            }

            ((ImageView) popupProfileEditPreview.findViewById(R.id.iconDataProfile)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_social_telegram));

        } else if (ClipboardType.is_Tumblr(contactInfo.value)) {
            ((ImageView) popupProfileEditPreview.findViewById(R.id.imageCall)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.tumblr));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ((ImageView) popupProfileEditPreview.findViewById(R.id.imageCall)).setImageTintMode(null);
            }

            ((ImageView) popupProfileEditPreview.findViewById(R.id.iconDataProfile)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.tumblr));

        } else if (ClipboardType.isMedium(contactInfo.value)) {
            ((ImageView) popupProfileEditPreview.findViewById(R.id.imageCall)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.medium_size_64));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ((ImageView) popupProfileEditPreview.findViewById(R.id.imageCall)).setImageTintMode(null);
            }

            ((ImageView) popupProfileEditPreview.findViewById(R.id.iconDataProfile)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.medium_size_64));

        } else if (ClipboardType.is_Angel(contactInfo.value)) {
            ((ImageView) popupProfileEditPreview.findViewById(R.id.imageCall)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.angel));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ((ImageView) popupProfileEditPreview.findViewById(R.id.imageCall)).setImageTintMode(null);
            }

            ((ImageView) popupProfileEditPreview.findViewById(R.id.iconDataProfile)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.angel));

        } else if (ClipboardType.isEmail(contactInfo.value)) {
            ((ImageView) popupProfileEditPreview.findViewById(R.id.imageCall)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.gmail));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ((ImageView) popupProfileEditPreview.findViewById(R.id.imageCall)).setImageTintMode(null);
            }

            ((ImageView) popupProfileEditPreview.findViewById(R.id.iconDataProfile)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_emails));

        } else if (type.equalsIgnoreCase("date of birth")) {

            ((ImageView) popupProfileEditPreview.findViewById(R.id.iconDataProfile)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.gift_blue));
            ((ImageView) popupProfileEditPreview.findViewById(R.id.imageCall)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.google_calendar));

        }else if (type.equalsIgnoreCase("bio")) {

            ((ImageView) popupProfileEditPreview.findViewById(R.id.iconDataProfile)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.intro_n));
            ((ImageView) popupProfileEditPreview.findViewById(R.id.imageCall)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_fab_google));

        } else if (ClipboardType.isPhone(contactInfo.value) && contactInfo.isPhone) {
            ((ImageView) popupProfileEditPreview.findViewById(R.id.imageCall)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_popup_call));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ((ImageView) popupProfileEditPreview.findViewById(R.id.imageCall)).setImageTintMode(null);
            }

            ((ImageView) popupProfileEditPreview.findViewById(R.id.iconDataProfile)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_phone));

        } else if (ClipboardType.isWeb(contactInfo.value)) {
            ((ImageView) popupProfileEditPreview.findViewById(R.id.imageCall)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_popup_web_blue));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ((ImageView) popupProfileEditPreview.findViewById(R.id.imageCall)).setImageTintMode(null);
            }

            ((ImageView) popupProfileEditPreview.findViewById(R.id.iconDataProfile)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_popup_web_blue));

        } else {
            if (type.equalsIgnoreCase("address")) {
                ((ImageView) popupProfileEditPreview.findViewById(R.id.imageCall)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_fab_maps));
                ((ImageView) popupProfileEditPreview.findViewById(R.id.iconDataProfile)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_geo));
            } else {
                ((ImageView) popupProfileEditPreview.findViewById(R.id.imageCall)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_fab_google));
                ((ImageView) popupProfileEditPreview.findViewById(R.id.iconDataProfile)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_notes));
                ((TextView) popupProfileEditPreview.findViewById(R.id.typeField)).setTextColor(getActivity().getResources().getColor(R.color.black));
                ((TextView) popupProfileEditPreview.findViewById(R.id.valueUpd)).setTextColor(getActivity().getResources().getColor(R.color.black));

            }
        }


        //    ((TextView) popupProfileEditPreview.findViewById(R.id.valueUpd)).setEnabled(false);
        //    ((TextView) popupProfileEditPreview.findViewById(R.id.valueUpd)).setEnabled(true);


        if(contactInfo.titleValue == null || contactInfo.titleValue.isEmpty()){
            if (contactInfo.type.equalsIgnoreCase("date of birth")){
                ((TextView) popupProfileEditPreview.findViewById(R.id.typeField)).setText("Birthday");
            }else {

                StringBuilder builder = new StringBuilder(type);
                //выставляем первый символ заглавным, если это буква
                if (Character.isAlphabetic(type.codePointAt(0)))
                    builder.setCharAt(0, Character.toUpperCase(type.charAt(0)));

                ((TextView) popupProfileEditPreview.findViewById(R.id.typeField)).setText(builder);
            }
        }else{
            ((TextView) popupProfileEditPreview.findViewById(R.id.typeField)).setText(contactInfo.titleValue);
        }

       /* if (!contactInfo.type.equalsIgnoreCase("date of birth")) {
            ((TextView) popupProfileEditPreview.findViewById(R.id.typeField)).setText(type);

        } else {
            if (contactInfo.titleValue == null || contactInfo.titleValue.isEmpty())
                ((TextView) popupProfileEditPreview.findViewById(R.id.typeField)).setText("Birthday");
            else
                ((TextView) popupProfileEditPreview.findViewById(R.id.typeField)).setText(contactInfo.titleValue);
        }*/

        ((TextView) popupProfileEditPreview.findViewById(R.id.typeField)).requestLayout();
        ((TextView) popupProfileEditPreview.findViewById(R.id.valueUpd)).setText(contactInfo.value);

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
                copyToClipboard(mainView.getContext(), String.valueOf(contactInfo.value));
                Toast.makeText(mainView.getContext(), "Copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });

        popupProfileEditPreview.findViewById(R.id.editPopupShare).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = contactInfo.value;
                text += "\n\n";
                text += "Data shared via http://Extime.pro\n";
                shareIntent(text);
                //shareIntent(String.valueOf(contactInfo.value));
            }
        });

        switch (contactInfo.typeData.toString()) {
            case "PHONE": {
                popupProfileEditPreview.findViewById(R.id.editPopupCall).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callContact(String.valueOf(contactInfo.value));
                    }
                });

                popupProfileEditPreview.findViewById(R.id.editPopupCall).setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        startActivity(Intent.createChooser(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", contactInfo.value, null)), "Open with..."));
                        return true;
                    }
                });

                popupProfileEditPreview.findViewById(R.id.editPopupMail).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        sendSms(String.valueOf(contactInfo.value));

                    }
                });
                popupProfileEditPreview.findViewById(R.id.delete_contact_number).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                getActivity());
                        alertDialogBuilder.setTitle("Do you want to delete phone number?");
                        alertDialogBuilder
                                .setCancelable(false)
                                .setPositiveButton("Yes", (dialog, id) -> {
                                    String id_S = selectedContact.get(0).getIdContact();
                                    String val = contactInfo.value;


                                    contactsService.deletePhoneContact(selectedContact.get(0).getIdContact(), contactInfo.value);


                                    if (selectedContact.get(0).getSocialModel() != null) {


                                        Realm realm = Realm.getDefaultInstance();

                                        /*if (selectedContact.get(0).getSocialModel().getViberLink() != null && selectedContact.get(0).getSocialModel().getViberLink().equalsIgnoreCase(contactInfo.value)) {
                                            realm.beginTransaction();
                                            selectedContact.get(0).getSocialModel().setViberLink(null);
                                            selectedContact.get(0).hasViber = false;
                                            realm.commitTransaction();

                                            Drawable color5 = new ColorDrawable(Color.parseColor("#e2e5e8"));
                                            Drawable image5 = getResources().getDrawable(R.drawable.icn_social_viber2_gray);
                                            LayerDrawable ld5 = new LayerDrawable(new Drawable[]{color5, image5});
                                            ((CircleImageView) getActivity().findViewById(R.id.viber_icon)).setImageDrawable(ld5);
                                        }
                                        if (selectedContact.get(0).getSocialModel().getWhatsappLink() != null && selectedContact.get(0).getSocialModel().getWhatsappLink().equalsIgnoreCase(contactInfo.value)) {
                                            realm.beginTransaction();
                                            selectedContact.get(0).getSocialModel().setWhatsappLink(null);
                                            selectedContact.get(0).hasWhatsapp = false;
                                            realm.commitTransaction();

                                            Drawable color6 = new ColorDrawable(Color.parseColor("#e2e5e8"));
                                            Drawable image6 = getResources().getDrawable(R.drawable.icn_social_whatsapp3_gray);
                                            LayerDrawable ld6 = new LayerDrawable(new Drawable[]{color6, image6});
                                            ((CircleImageView) getActivity().findViewById(R.id.whatsapp_icon)).setImageDrawable(ld6);
                                        }
                                        if (selectedContact.get(0).getSocialModel().getTelegramLink() != null && selectedContact.get(0).getSocialModel().getTelegramLink().equalsIgnoreCase(contactInfo.value)) {
                                            realm.beginTransaction();
                                            selectedContact.get(0).getSocialModel().setTelegramLink(null);
                                            selectedContact.get(0).hasTelegram = false;
                                            realm.commitTransaction();

                                            Drawable color7 = new ColorDrawable(Color.parseColor("#e2e5e8"));
                                            Drawable image7 = getResources().getDrawable(R.drawable.icn_social_telegram2);
                                            LayerDrawable ld7 = new LayerDrawable(new Drawable[]{color7, image7});
                                            ((CircleImageView) getActivity().findViewById(R.id.telegram_icon)).setImageDrawable(ld7);
                                        }*/


                                        realm.beginTransaction();

                                        try {
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

                                        }

                                        realm.commitTransaction();
                                        realm.close(); //+
                                    }

                                    boolean havePhone = false;
                                    for (ContactInfo contactInfo1 : selectedContact.get(0).listOfContactInfo) {
                                        if (!contactInfo1.value.equalsIgnoreCase(contactInfo.value)
                                                && ((contactInfo1.typeData != null && contactInfo1.typeData.equalsIgnoreCase("PHONE")) || contactInfo1.type.equalsIgnoreCase("phone")))
                                            havePhone = true;
                                    }

                                    if (!havePhone) {
                                        contactsService.addPhoneToContact(selectedContact.get(0).getIdContact(), "+000000000000", selectedContact.get(0).getName());

                                        // realm.beginTransaction();
                                        contactNumberAdapter.replaceValuePhoneContactNumber(contactInfo, "+000000000000");
                                        //contactInfo.value = "+000000000000";
                                        //  realm.commitTransaction();
                                        ProfileFragment.noPhone = true;
                                    } else
                                        contactNumberAdapter.removeContactNumber(contactInfo);

                                    //EventBus.getDefault().post(new UpdateFile());

                                    frameClick.callOnClick();
                                    popupProfileEditPreview.setVisibility(View.GONE);
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
            case "EMAIL": {
                popupProfileEditPreview.findViewById(R.id.editPopupMail).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //sendEmail(String.valueOf(contactInfo.value));

                        SharedPreferences mSettings;
                        mSettings = getActivity().getSharedPreferences("accountUser", Context.MODE_PRIVATE);
                        Set<String> set = mSettings.getStringSet("accounts", null);

                        String finalReplyFrom = null;

                        if (set != null && !set.isEmpty()) {

                            for (String s : set) {
                                finalReplyFrom = s;
                                break;
                            }

                        }

                        if (finalReplyFrom == null) {

                         /*   try {
                                Toast.makeText(getContext(), "Choose account", Toast.LENGTH_SHORT).show();
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }

                            return;*/

                            ArrayList<String> sTo = new ArrayList<>();
                            ArrayList<String> sName = new ArrayList<>();

                            sTo.add(contactInfo.value);
                            sName.add(selectedContact.get(0).getName());

                            ShareTemplatesMessageReply.showChooseAccount(getActivity(), false, sTo, sName, finalReplyFrom, null, ContactProfileDataFragment.this, false);

                        }else {
                            ArrayList<String> sTo = new ArrayList<>();
                            ArrayList<String> sName = new ArrayList<>();

                            sTo.add(contactInfo.value);
                            sName.add(selectedContact.get(0).getName());


                            ShareTemplatesMessageReply.showTemplatesPopup(getActivity(), false, sTo, sName, finalReplyFrom, null, ContactProfileDataFragment.this, false);
                        }

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
                                    contactsService.deleteEmailContact(selectedContact.get(0).getIdContact(), contactInfo.value);
                                    contactNumberAdapter.removeContactNumber(contactInfo);
                                    frameClick.callOnClick();
                                    popupProfileEditPreview.setVisibility(View.GONE);

                                    ((ProfileFragment) getParentFragment()).contactTimelineDataFragment.checkEmail();
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

                popupProfileEditPreview.findViewById(R.id.editPopupCall).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                        emailIntent.setData(Uri.parse("mailto:" + contactInfo.value));
                        startActivity(emailIntent);
                    }
                });

                popupProfileEditPreview.findViewById(R.id.editPopupCall).setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                        emailIntent.setData(Uri.parse("mailto:" + contactInfo.value));
                        startActivity(Intent.createChooser(emailIntent, "Send email"));
                        return true;
                    }
                });


                break;
            }
            case "NOTE": {


                popupProfileEditPreview.findViewById(R.id.editPopupCall).setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if(ClipboardType.isWeb(contactInfo.value)){
                            try {
                                String uris = contactInfo.value;
                                if (!contactInfo.value.contains("https://") && !contactInfo.value.contains("http://"))
                                    uris = "https://" + uris;



                                Intent i = new Intent(Intent.ACTION_VIEW);

                                //i.addCategory(Intent.CATEGORY_APP_BROWSER);

                                i.setData(Uri.parse(uris));



                                getActivity().startActivity(Intent.createChooser(i, "Open with..."));

                            } catch (Exception e2) {
                                e2.printStackTrace();
                            }
                        }else{
                            Intent map = new Intent(Intent.ACTION_WEB_SEARCH);
                            map.putExtra(SearchManager.QUERY, contactInfo.value);
                            startActivity(Intent.createChooser(map, "Open with..."));
                        }
                        return true;
                    }
                });

                popupProfileEditPreview.findViewById(R.id.editPopupCall).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Matcher matcher = Patterns.WEB_URL.matcher(contactInfo.value.toString());

                        if (ClipboardType.isFacebook(contactInfo.value)) {

                            Intent intent;
                            if (contactInfo.value.contains("?id=")) {
                                String idProfile = contactInfo.value.substring(contactInfo.value.indexOf('=') + 1, contactInfo.value.length());
                                if (idProfile.contains("&")) {
                                    idProfile = idProfile.substring(0, idProfile.indexOf("&"));
                                }
                                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/" + idProfile));

                            } else if (contactInfo.value.contains("/people/")) {
                                String idProfile = contactInfo.value.substring(contactInfo.value.lastIndexOf('/') + 1, contactInfo.value.length());
                                if (idProfile.contains("&")) {
                                    idProfile = idProfile.substring(0, idProfile.indexOf("&"));
                                }

                                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/" + idProfile));

                            } else
                                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://facewebmodal/f?href=" + contactInfo.value.replace("fb","facebook")));

                            //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://facewebmodal/f?href=" + contact.getSocialModel().getFacebookLink()));
                            PackageManager packageManager = getActivity().getPackageManager();
                            List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
                            boolean isIntentSafe = activities.size() > 0;
                            if (!isIntentSafe) {
                                /*Intent intent2 = new Intent(Intent.ACTION_VIEW);
                                intent2.setData(Uri.parse("market://details?id=com.facebook.katana"));
                                startActivity(intent2);*/

                                try {
                                    String uris = contactInfo.value;
                                    if (!contactInfo.value.contains("https://") && !contactInfo.value.contains("http://"))
                                        uris = "https://" + uris;

                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                    i.setData(Uri.parse(uris));
                                    startActivity(i);
                                } catch (Exception e) {

                                }

                            } else
                                startActivity(intent);


                        } else if (ClipboardType.isInsta(contactInfo.value)) {



                            String str = contactInfo.value;
                            if (!str.toLowerCase().contains("instagram")) {
                                str = "https://instagram.com/" + contactInfo.value;
                            }

                            if (!str.contains("http://") && !str.contains("https://")) {
                                str = "https://" + contactInfo.value;
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
                                    String uris = contactInfo.value;
                                    if (!contactInfo.value.contains("https://") && !contactInfo.value.contains("http://"))
                                        uris = "https://" + uris;

                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                    i.setData(Uri.parse(uris));
                                    startActivity(i);
                                } catch (Exception e) {

                                }
                            } else
                                startActivity(likeIng);


                        } else if (ClipboardType.isTwitter(contactInfo.value)) {

                            String text = contactInfo.value;
                            if (text.contains("twitter.com/")) {
                                text = text.substring(text.indexOf(".com/") + 5);
                            }
                            if (text.length() > 0 && text.charAt(0) == '@')
                                text = text.substring(1);
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

                           /*

                            String str = contactInfo.value;
                            if(!str.contains("instagram")){
                                str = "https://instagram.com/" + contactInfo.value;
                            }

                            Uri uri = Uri.parse(str);
                            Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);
                            likeIng.setPackage("com.instagram.android");
                            PackageManager packageManager = getActivity().getPackageManager();
                            List<ResolveInfo> activities = packageManager.queryIntentActivities(likeIng, 0);
                            boolean isIntentSafe = activities.size() > 0;
                            if(!isIntentSafe){
                                Intent intent2 = new Intent(Intent.ACTION_VIEW);
                                intent2.setData(Uri.parse("market://details?id=com.instagram.android"));
                                startActivity(intent2);
                            }else
                                startActivity(likeIng);*/


                        } else if (ClipboardType.isYoutube(contactInfo.value)) {

                            String text = contactInfo.value;
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

                            /*

                            String str = contactInfo.value;
                            if(!str.contains("instagram")){
                                str = "https://instagram.com/" + contactInfo.value;
                            }

                            Uri uri = Uri.parse(str);
                            Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);
                            likeIng.setPackage("com.instagram.android");
                            PackageManager packageManager = getActivity().getPackageManager();
                            List<ResolveInfo> activities = packageManager.queryIntentActivities(likeIng, 0);
                            boolean isIntentSafe = activities.size() > 0;
                            if(!isIntentSafe){
                                Intent intent2 = new Intent(Intent.ACTION_VIEW);
                                intent2.setData(Uri.parse("market://details?id=com.instagram.android"));
                                startActivity(intent2);
                            }else
                                startActivity(likeIng);*/


                        } else if (ClipboardType.isVk(contactInfo.value)) {

                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format(contactInfo.value)));
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
                                        String uris = contactInfo.value;
                                        if (!contactInfo.value.contains("https://") && !contactInfo.value.contains("http://"))
                                            uris = "https://" + uris;

                                        Intent i = new Intent(Intent.ACTION_VIEW);
                                        i.setData(Uri.parse(uris));
                                        startActivity(i);
                                    } catch (Exception e2) {

                                    }
                                } else
                                    startActivity(intent2);
                            }


                        } else if (ClipboardType.isG_Doc(contactInfo.value) || ClipboardType.isG_Sheet(contactInfo.value)) {


                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(contactInfo.value)));


                        } else if (ClipboardType.isLinkedIn(contactInfo.value)) {

                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(contactInfo.value));
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

                        } else if (ClipboardType.isTelegram(contactInfo.value)) {


                            Intent telegramIntent = new Intent(Intent.ACTION_VIEW);

                            String username = contactInfo.value;
                            if (contactInfo.value.contains("@")) {
                                username = contactInfo.value.substring(contactInfo.value.indexOf("@") + 1);
                            } else {
                                username = contactInfo.value.substring(contactInfo.value.lastIndexOf("/") + 1);
                            }

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


                        } else if (/*matcher.matches() && !contactInfo.value.contains("@")*/ClipboardType.isWeb(contactInfo.value)) {
                            try {
                                String uri = contactInfo.value;
                                if (!contactInfo.value.contains("https://") && !contactInfo.value.contains("http://"))
                                    uri = "https://" + uri;

                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(uri));
                                startActivity(i);
                            } catch (Exception e) {

                            }
                        } else {

                            if (type.equalsIgnoreCase("date of birth")) {

                               /* Intent calIntent = new Intent(Intent.ACTION_VIEW).setData(new Timestamp(System.currentTimeMillis()));
                                calIntent.setData(CalendarContract.Events.CONTENT_URI);
                                startActivity(calIntent);*/

                            } else if (type.equalsIgnoreCase("address")) {



                                try {
                                    Intent map = new Intent(Intent.ACTION_VIEW,
                                            Uri.parse(String.format("geo:0,0?q=%s",
                                                    URLEncoder.encode(contactInfo.value, "UTF-8"))));

                                    startActivity(map);

                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }

                            } else {
                                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                                String term = contactInfo.value;
                                intent.putExtra(SearchManager.QUERY, term);
                                startActivity(intent);
                            }
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
                        alertDialogBuilder.setTitle("Do you want to delete note?");
                        alertDialogBuilder
                                .setCancelable(false)
                                .setPositiveButton("Yes", (dialog, id) -> {
                                    contactNumberAdapter.removeContactNumber(contactInfo);
                                    frameClick.callOnClick();
                                    popupProfileEditPreview.setVisibility(View.GONE);

                                    contactsService.deleteNoteContact(selectedContact.get(0).getIdContact(), contactInfo.value);
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
            default: {


                popupProfileEditPreview.findViewById(R.id.editPopupCall).setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if(ClipboardType.isWeb(contactInfo.value)){
                            try {
                                String uris = contactInfo.value;
                                if (!contactInfo.value.contains("https://") && !contactInfo.value.contains("http://"))
                                    uris = "https://" + uris;



                                Intent i = new Intent(Intent.ACTION_VIEW);

                                //i.addCategory(Intent.CATEGORY_APP_BROWSER);

                                i.setData(Uri.parse(uris));



                                getActivity().startActivity(Intent.createChooser(i, "Open with..."));


                            } catch (Exception e2) {
                                e2.printStackTrace();
                            }finally {
                                return true;
                            }
                        } else if (type.equalsIgnoreCase("bio")) {



                            Intent map = new Intent(Intent.ACTION_WEB_SEARCH);

                            map.putExtra(SearchManager.QUERY, contactInfo.value);


                            startActivity(Intent.createChooser(map, "Open with..."));

                        }
                        return true;
                    }
                });

                popupProfileEditPreview.findViewById(R.id.editPopupCall).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Matcher matcher = Patterns.WEB_URL.matcher(contactInfo.value.toString());

                        if (ClipboardType.isFacebook(contactInfo.value)) {

                            Intent intent;
                            if (contactInfo.value.contains("?id=")) {
                                String idProfile = contactInfo.value.substring(contactInfo.value.indexOf('=') + 1, contactInfo.value.length());
                                if (idProfile.contains("&")) {
                                    idProfile = idProfile.substring(0, idProfile.indexOf("&"));
                                }
                                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/" + idProfile));

                            } else if (contactInfo.value.contains("/people/")) {
                                String idProfile = contactInfo.value.substring(contactInfo.value.lastIndexOf('/') + 1, contactInfo.value.length());
                                if (idProfile.contains("&")) {
                                    idProfile = idProfile.substring(0, idProfile.indexOf("&"));
                                }

                                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/" + idProfile));

                            } else
                                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://facewebmodal/f?href=" + contactInfo.value.replace("fb","facebook")));

                            //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://facewebmodal/f?href=" + contact.getSocialModel().getFacebookLink()));
                            PackageManager packageManager = getActivity().getPackageManager();
                            List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
                            boolean isIntentSafe = activities.size() > 0;
                            if (!isIntentSafe) {
                                /*Intent intent2 = new Intent(Intent.ACTION_VIEW);
                                intent2.setData(Uri.parse("market://details?id=com.facebook.katana"));
                                startActivity(intent2);*/

                                try {
                                    String uris = contactInfo.value;
                                    if (!contactInfo.value.contains("https://") && !contactInfo.value.contains("http://"))
                                        uris = "https://" + uris;

                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                    i.setData(Uri.parse(uris));
                                    startActivity(i);
                                } catch (Exception e) {

                                }

                            } else
                                startActivity(intent);


                        } else if (ClipboardType.isInsta(contactInfo.value)) {



                            String str = contactInfo.value;
                            if (!str.toLowerCase().contains("instagram")) {
                                str = "https://instagram.com/" + contactInfo.value;
                            }

                            if (!str.contains("http://") && !str.contains("https://")) {
                                str = "https://" + contactInfo.value;
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
                                    String uris = contactInfo.value;
                                    if (!contactInfo.value.contains("https://") && !contactInfo.value.contains("http://"))
                                        uris = "https://" + uris;

                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                    i.setData(Uri.parse(uris));
                                    startActivity(i);
                                } catch (Exception e) {

                                }
                            } else
                                startActivity(likeIng);


                        } else if (ClipboardType.isTwitter(contactInfo.value)) {

                            String text = contactInfo.value;
                            if (text.contains("twitter.com/")) {
                                text = text.substring(text.indexOf(".com/") + 5);
                            }
                            if (text.length() > 0 && text.charAt(0) == '@')
                                text = text.substring(1);
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

                           /*

                            String str = contactInfo.value;
                            if(!str.contains("instagram")){
                                str = "https://instagram.com/" + contactInfo.value;
                            }

                            Uri uri = Uri.parse(str);
                            Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);
                            likeIng.setPackage("com.instagram.android");
                            PackageManager packageManager = getActivity().getPackageManager();
                            List<ResolveInfo> activities = packageManager.queryIntentActivities(likeIng, 0);
                            boolean isIntentSafe = activities.size() > 0;
                            if(!isIntentSafe){
                                Intent intent2 = new Intent(Intent.ACTION_VIEW);
                                intent2.setData(Uri.parse("market://details?id=com.instagram.android"));
                                startActivity(intent2);
                            }else
                                startActivity(likeIng);*/


                        } else if (ClipboardType.isYoutube(contactInfo.value)) {

                            String text = contactInfo.value;
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

                            /*

                            String str = contactInfo.value;
                            if(!str.contains("instagram")){
                                str = "https://instagram.com/" + contactInfo.value;
                            }

                            Uri uri = Uri.parse(str);
                            Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);
                            likeIng.setPackage("com.instagram.android");
                            PackageManager packageManager = getActivity().getPackageManager();
                            List<ResolveInfo> activities = packageManager.queryIntentActivities(likeIng, 0);
                            boolean isIntentSafe = activities.size() > 0;
                            if(!isIntentSafe){
                                Intent intent2 = new Intent(Intent.ACTION_VIEW);
                                intent2.setData(Uri.parse("market://details?id=com.instagram.android"));
                                startActivity(intent2);
                            }else
                                startActivity(likeIng);*/


                        } else if (ClipboardType.isVk(contactInfo.value)) {

                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format(contactInfo.value)));
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
                                        String uris = contactInfo.value;
                                        if (!contactInfo.value.contains("https://") && !contactInfo.value.contains("http://"))
                                            uris = "https://" + uris;

                                        Intent i = new Intent(Intent.ACTION_VIEW);
                                        i.setData(Uri.parse(uris));
                                        startActivity(i);
                                    } catch (Exception e2) {

                                    }
                                } else
                                    startActivity(intent2);
                            }


                        } else if (ClipboardType.isG_Doc(contactInfo.value) || ClipboardType.isG_Sheet(contactInfo.value)) {


                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(contactInfo.value)));


                        } else if (ClipboardType.isLinkedIn(contactInfo.value)) {

                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(contactInfo.value));
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

                        } else if (/*matcher.matches() && !contactInfo.value.contains("@")*/ClipboardType.isWeb(contactInfo.value)) {
                            try {
                                String uri = contactInfo.value;
                                if (!contactInfo.value.contains("https://") && !contactInfo.value.contains("http://"))
                                    uri = "https://" + uri;

                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(uri));
                                startActivity(i);
                            } catch (Exception e) {

                            }
                        } else if (ClipboardType.isEmail(contactInfo.value)) {
                            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                            emailIntent.setData(Uri.parse("mailto:" + contactInfo.value));
                            startActivity(Intent.createChooser(emailIntent, "Send email"));
                        } else {
                            if (type.equalsIgnoreCase("date of birth")) {

                               /* Intent calIntent = new Intent(Intent.ACTION_VIEW).setData(new Timestamp(System.currentTimeMillis()));
                                calIntent.setData(CalendarContract.Events.CONTENT_URI);
                                startActivity(calIntent);*/

                            } else if (type.equalsIgnoreCase("address")) {



                                try {
                                    Intent map = new Intent(Intent.ACTION_VIEW,
                                            Uri.parse(String.format("geo:0,0?q=%s",
                                                    URLEncoder.encode(contactInfo.value, "UTF-8"))));

                                    startActivity(map);

                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }

                            } else if (type.equalsIgnoreCase("bio")) {



                                Intent map = new Intent(Intent.ACTION_WEB_SEARCH);

                                map.putExtra(SearchManager.QUERY, contactInfo.value);


                                startActivity(map);

                            }  else {
                                try {
                                    Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                                    String term = contactInfo.value;
                                    intent.putExtra(SearchManager.QUERY, term);
                                    startActivity(intent);
                                } catch (Exception e) {

                                }
                            }
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
                        alertDialogBuilder.setTitle("Do you want to delete note?");
                        alertDialogBuilder
                                .setCancelable(false)
                                .setPositiveButton("Yes", (dialog, id) -> {
                                    contactNumberAdapter.removeContactNumber(contactInfo);


                                    frameClick.callOnClick();
                                    popupProfileEditPreview.setVisibility(View.GONE);

                                    if (contactInfo.type.equals("address"))
                                        contactsService.deleteAddressContact(selectedContact.get(0).getIdContact(), contactInfo.value);
                                    else
                                        contactsService.deleteNoteContact(selectedContact.get(0).getIdContact(), contactInfo.value);
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

    private void sendEmail(String email) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:" + email));
        startActivity(Intent.createChooser(emailIntent, "Send email"));
    }

    private void sendSms(String number) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + number)));
    }

    private void callContact(String number) {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", number, null)));
    }

    public void shareIntent(String text) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(Intent.createChooser(shareIntent, "Share"));
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

    /*@Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof Activity){
            activityApp = (Activity) context;

        }
    }*/
}
