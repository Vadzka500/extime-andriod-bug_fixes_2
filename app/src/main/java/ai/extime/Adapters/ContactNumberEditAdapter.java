package ai.extime.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberToCarrierMapper;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.Locale;
import java.util.regex.Matcher;

import ai.extime.Activity.MainActivity;
import ai.extime.Models.SocialModel;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import ai.extime.Enums.TypeData;
import ai.extime.Fragments.ContactProfileDataFragment;
import ai.extime.Fragments.ProfileFragment;
import ai.extime.Models.ContactInfo;

import com.extime.R;

import ai.extime.Services.ContactsService;
import ai.extime.Utils.ClipboardType;

/**
 * Created by teaarte on 13.11.2017.
 */

public class ContactNumberEditAdapter extends RecyclerView.Adapter<ContactNumberEditAdapter.ContactViewHolder> {

    private RealmList<ContactInfo> contactInfos;
    private RealmList<ContactInfo> contactInfosNEW;

    private Context context;
    private boolean editModeEnabled;
    private String selectedId = null;
    //  private ProfileFragment profileFragment;
    private ContactInfo contactInfoForReplace;
    private ContactsService contactsService;
    private Fragment parentFragment;
    View v;

    static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView type;
        EditText value;
        ImageView contactTypeImage;
        CheckBox isConfirmed;
        TextView carrier;
        ImageView region;
        TextView regionNA;

        ImageView acceptData;

        TextView typeSocial;

        ContactViewHolder(View itemView) {
            super(itemView);
            type = (TextView) itemView.findViewById(R.id.hashtag_text);
            value = (EditText) itemView.findViewById(R.id.value1);
            isConfirmed = (CheckBox) itemView.findViewById(R.id.checkConf);
            contactTypeImage = (ImageView) itemView.findViewById(R.id.type_image);
            carrier = (TextView) itemView.findViewById(R.id.carrier);
            region = (ImageView) itemView.findViewById(R.id.region);
            regionNA = (TextView) itemView.findViewById(R.id.regionNA);

            acceptData = itemView.findViewById(R.id.acceptData);

            typeSocial = itemView.findViewById(R.id.typeSocial);
        }
    }

    public void replaceContactNumber(ContactInfo contactInfo) {
        contactInfos.remove(contactInfoForReplace);
        notifyDataSetChanged();
    }

    public ContactNumberEditAdapter(Context context, RealmList<ContactInfo> contactInfos, Fragment fragment) {

        this.context = context;

        this.contactInfos = new RealmList<>();
        this.contactInfosNEW = new RealmList<>();
        if (contactInfos != null) {
            for (int j = 0; j < contactInfos.size(); j++) {

                if (!contactInfos.get(j).toString().equals("+000000000000")) {

                    this.contactInfosNEW.add(contactInfos.get(j));
                } else {

                    ProfileFragment.noPhone = true;
                    //  this.contactInfosNEW.add(contactInfos.get(j));
                }
            }

            this.contactInfos = contactInfos;
        }
        editModeEnabled = false;
        this.parentFragment = fragment;
    }

    public void updateList(RealmList<ContactInfo> contactInfos) {
        this.contactInfos = new RealmList<>();
        this.contactInfosNEW = new RealmList<>();
        if (contactInfos != null) {
            for (int j = 0; j < contactInfos.size(); j++) {

                if (!contactInfos.get(j).toString().equals("+000000000000")) {

                    this.contactInfosNEW.add(contactInfos.get(j));
                } else {

                    ProfileFragment.noPhone = true;
                    //  this.contactInfosNEW.add(contactInfos.get(j));
                }
            }
            this.contactInfos = contactInfos;
        }
        notifyDataSetChanged();
    }

    public void addInfo(ContactInfo contactInfo) {
        boolean check = false;
        if (contactInfosNEW != null && !contactInfosNEW.isEmpty()) {
            for (int i = 0; i < contactInfosNEW.size(); i++) {
                if (contactInfosNEW.get(i).value.equalsIgnoreCase(contactInfo.value)) {
                    check = true;
                }
            }
            if (!check) {
                this.contactInfos.add(contactInfo);
                this.contactInfosNEW.add(contactInfo);
            }
        } else {
            this.contactInfos = new RealmList<>();
            this.contactInfosNEW = new RealmList<>();
            this.contactInfos.add(contactInfo);
            this.contactInfosNEW.add(contactInfo);
        }
        notifyDataSetChanged();
    }

    @Override
    public ContactNumberEditAdapter.ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_contact_number_edit, viewGroup, false);
        ContactNumberEditAdapter.ContactViewHolder contactViewHolder = new ContactNumberEditAdapter.ContactViewHolder(v);
        contactViewHolder.setIsRecyclable(false);

        return contactViewHolder;
    }

    @Override
    public void onBindViewHolder(ContactNumberEditAdapter.ContactViewHolder viewHolder, int i) {
        final ContactInfo contactInfo = contactInfosNEW.get(i);

        if (contactInfo.isPrimary)
            viewHolder.acceptData.setVisibility(View.VISIBLE);
        else
            viewHolder.acceptData.setVisibility(View.GONE);

        viewHolder.typeSocial.setVisibility(View.GONE);

        Realm realms = Realm.getDefaultInstance(); //-
        realms.beginTransaction();

        if (contactInfo.isEmail) {
            viewHolder.contactTypeImage.setImageResource(R.drawable.icn_emails);
            contactInfo.typeData = TypeData.EMAIL.toString();
            viewHolder.regionNA.setVisibility(View.GONE);
        }

        if (contactInfo.isGeo) {
            viewHolder.contactTypeImage.setImageResource(R.drawable.icn_geo);
            contactInfo.typeData = TypeData.LOCATION.toString();
            viewHolder.regionNA.setVisibility(View.GONE);
        }

        if (contactInfo.type.equalsIgnoreCase("date of birth")) {
            viewHolder.contactTypeImage.setImageResource(R.drawable.gift_blue);
            contactInfo.typeData = TypeData.DATEOFBIRTH.toString();
            viewHolder.regionNA.setVisibility(View.GONE);
        }

        if (contactInfo.type.equalsIgnoreCase("bio")) {
            viewHolder.contactTypeImage.setImageResource(R.drawable.intro_n);
            contactInfo.typeData = TypeData.BIO.toString();
            viewHolder.regionNA.setVisibility(View.GONE);
        }

        viewHolder.type.setText(contactInfo.type);

        Matcher matcher = Patterns.WEB_URL.matcher(contactInfo.value.toString());


        if (contactInfo.isNote) {
            viewHolder.contactTypeImage.setImageResource(R.drawable.icn_notes);
            contactInfo.typeData = TypeData.NOTE.toString();

            if (ClipboardType.isFacebook(contactInfo.value)) {
                viewHolder.contactTypeImage.setImageResource(R.drawable.icn_social_facebook);
                viewHolder.type.setText("facebook");
            } else if (ClipboardType.isVk(contactInfo.value)) {
                viewHolder.contactTypeImage.setImageResource(R.drawable.icn_social_vk);
                viewHolder.type.setText("vk");
            } else if (ClipboardType.isLinkedIn(contactInfo.value)) {

                if(contactInfo.value.contains("/posts/")){
                    viewHolder.typeSocial.setText("post - ");
                    viewHolder.typeSocial.setVisibility(View.VISIBLE);
                }

                viewHolder.contactTypeImage.setImageResource(R.drawable.icn_social_linkedin);
                viewHolder.type.setText("linkedin");
            } else if (ClipboardType.isInsta(contactInfo.value)) {

                if(contactInfo.value.contains("/p/")){
                    viewHolder.typeSocial.setText("post - ");
                    viewHolder.typeSocial.setVisibility(View.VISIBLE);
                }else if(contactInfo.value.contains("/tv/")){
                    viewHolder.typeSocial.setText("IGTV - ");
                    viewHolder.typeSocial.setVisibility(View.VISIBLE);
                }else{
                    String str = contactInfo.value.substring(contactInfo.value.indexOf(".com/") + 5);

                    if (str.charAt(str.length() - 1) == '/') {
                        str = str.substring(0, str.length() - 1);
                    }

                    if(!str.contains("/")) {
                        viewHolder.typeSocial.setText("profile - ");
                        viewHolder.typeSocial.setVisibility(View.VISIBLE);
                    }
                }

                viewHolder.contactTypeImage.setImageResource(R.drawable.icn_social_instagram);
                viewHolder.type.setText("instagram");
            } else if (ClipboardType.isTwitter(contactInfo.value)) {
                viewHolder.contactTypeImage.setImageResource(R.drawable.ic_twitter_64);
                viewHolder.type.setText("twitter");
            } else if (ClipboardType.isYoutube(contactInfo.value)) {
                viewHolder.contactTypeImage.setImageResource(R.drawable.ic_youtube_48);
                viewHolder.type.setText("youtube");
            }else if (ClipboardType.isGitHub(contactInfo.value)) {
                viewHolder.contactTypeImage.setImageResource(R.drawable.github_logo_64);
                viewHolder.type.setText("github");
            }else if (ClipboardType.isG_Sheet(contactInfo.value)) {
                viewHolder.contactTypeImage.setImageResource(R.drawable.google_sheets);
                viewHolder.type.setText("google sheet");
            }else if (ClipboardType.isG_Doc(contactInfo.value)) {
                viewHolder.contactTypeImage.setImageResource(R.drawable.google_docs);
                viewHolder.type.setText("google doc");
            }else if (ClipboardType.is_Tumblr(contactInfo.value)) {
                viewHolder.contactTypeImage.setImageResource(R.drawable.tumblr);
                viewHolder.type.setText("tumblr");
            }else if (ClipboardType.is_Angel(contactInfo.value)) {
                viewHolder.contactTypeImage.setImageResource(R.drawable.angel);
                viewHolder.type.setText("angel");
            }else if (ClipboardType.isMedium(contactInfo.value)) {
                viewHolder.contactTypeImage.setImageResource(R.drawable.medium_size_64);
                viewHolder.type.setText("medium");
            } else if (contactInfo.value.contains("viber.com")) {
                viewHolder.contactTypeImage.setImageResource(R.drawable.icn_social_viber);
                viewHolder.type.setText("viber");
            } else if (contactInfo.value.contains("whatsapp.com")) {
                viewHolder.contactTypeImage.setImageResource(R.drawable.icn_social_whatsapp);
                viewHolder.type.setText("whatsapp");
            } else if (ClipboardType.isTelegram(contactInfo.value)) {
                viewHolder.contactTypeImage.setImageResource(R.drawable.icn_social_telegram);
                viewHolder.type.setText("telegram");
            } else if (contactInfo.value.contains("skype.com")) {
                viewHolder.contactTypeImage.setImageResource(R.drawable.icn_social_skype);
                viewHolder.type.setText("skype");
            } else if (ClipboardType.isWeb(contactInfo.value)/*matcher.matches() && !contactInfo.value.contains("@")*/) {
                viewHolder.contactTypeImage.setImageResource(R.drawable.icn_popup_web_blue);
                viewHolder.type.setText("web");
            }

            viewHolder.regionNA.setVisibility(View.GONE);
        }
        realms.commitTransaction();
        realms.close();
        if (contactInfo.isPhone) {

            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
            String phoneNumber = contactInfo.value;

            try {
                Phonenumber.PhoneNumber swissNumberProto = phoneUtil.parse(phoneNumber, "");
                PhoneNumberToCarrierMapper carrierMapper = PhoneNumberToCarrierMapper.getInstance();

                String region = "";
                region = phoneUtil.getRegionCodeForCountryCode(swissNumberProto.getCountryCode());

                viewHolder.regionNA.setVisibility(View.VISIBLE);
                if (region.length() > 0) {
                    viewHolder.regionNA.setVisibility(View.GONE);
                    int drawableResourceId = context.getResources().getIdentifier("country_" + region.toLowerCase(), "drawable", context.getPackageName());
                    viewHolder.region.setImageResource(drawableResourceId);
                    viewHolder.region.setVisibility(View.VISIBLE);
                }

                String carrier = "";
                carrier = carrierMapper.getNameForNumber(swissNumberProto, Locale.ENGLISH);

                if (carrier.length() > 0) {
                    viewHolder.carrier.setText("(" + carrier + ")");
                    viewHolder.carrier.setVisibility(View.VISIBLE);
                }

            } catch (NumberParseException e) {
                System.err.println("NumberParseException was thrown: " + e.toString());
                String str = phoneNumber.replaceAll("[\\s\\-\\+\\(\\)]", "");
                if ((str.length() == 11 && str.charAt(0) == '8') || (str.length() == 12 && str.charAt(0) == '+' && str.charAt(1) == '8')) {

                    //String region = "ru";
                    viewHolder.regionNA.setVisibility(View.GONE);
                    try {
                        int drawableResourceId = context.getResources().getIdentifier("country_ru", "drawable", context.getPackageName());
                        viewHolder.region.setImageResource(drawableResourceId);
                        viewHolder.region.setVisibility(View.VISIBLE);
                    } catch (NullPointerException e1) {
                        e1.printStackTrace();
                    }
                }
            }
            viewHolder.contactTypeImage.setImageResource(R.drawable.icn_phone);



            Realm realm = Realm.getDefaultInstance(); //-
            realm.beginTransaction();
            contactInfo.typeData = TypeData.PHONE.toString();
            realm.commitTransaction();
            realm.close();
        }


        viewHolder.isConfirmed.setVisibility(contactInfo.isConfirmed ? View.VISIBLE : View.GONE);
        viewHolder.value.getText().clear();
        viewHolder.value.append(contactInfo.value);
        //viewHolder.type.setText(contactInfo.type);

        //viewHolder.value.setScroller(new Scroller(context));
        //viewHolder.value.setMovementMethod(new ScrollingMovementMethod());

        viewHolder.value.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        if (viewHolder.value.length() > 30)
            viewHolder.value.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (v.getId() == R.id.value1) {
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        switch (event.getAction() & MotionEvent.ACTION_MASK) {
                            case MotionEvent.ACTION_UP:
                                v.getParent().requestDisallowInterceptTouchEvent(false);
                                break;
                        }
                    }
                    return false;
                }
            });


        viewHolder.value.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                ProfileFragment.editModeChecker = true;



                Realm realm = Realm.getDefaultInstance(); //-
                realm.beginTransaction();
                contactInfo.setNewValue(viewHolder.value.getText().toString());
                realm.commitTransaction();
                realm.close();
            }
        });
    }

    public RealmList<ContactInfo> savechanges(Activity activityApp) {

        boolean check_phone = false;
        if (context != null)
            contactsService = new ContactsService(context.getContentResolver(), false);
        else
            contactsService = new ContactsService(activityApp.getContentResolver(), false);

        for (int i = 0; i < contactInfosNEW.size(); i++) {
            ContactInfo contactInfo = contactInfosNEW.get(i);

            if (contactInfo.isEmail) {
                if (contactInfo.getNewValue() != null) {
                    contactsService.updateEmail(String.valueOf(((ContactProfileDataFragment) parentFragment).selectedContact.get(0).getIdContact()), contactInfo.value, contactInfo.getNewValue());
                    if (contactInfo.getNewValue().equals("")) {
                        contactInfosNEW.remove(contactInfo);
                        continue;
                    } else
                        contactInfo.value = contactInfo.getNewValue();
                }
            }

            if (contactInfo.isGeo) {
                if (contactInfo.getNewValue() != null) {
                    contactsService.updateLocation(String.valueOf(((ContactProfileDataFragment) parentFragment).selectedContact.get(0).getIdContact()), contactInfo.value, contactInfo.getNewValue());
                    if (contactInfo.getNewValue().equals("")) {
                        contactInfosNEW.remove(contactInfo);
                        i--;
                        continue;
                    } else
                        contactInfo.value = contactInfo.getNewValue();
                }
            }

            if (contactInfo.isPhone) {

                if (!contactInfo.value.equals("+000000000000"))
                    ContactProfileDataFragment.checkRealPhone = true;

                if (contactInfo.getNewValue() != null) {
                    contactsService.updatePhone(String.valueOf(((ContactProfileDataFragment) parentFragment).selectedContact.get(0).getIdContact()), contactInfo.value, contactInfo.getNewValue());


                    if (contactInfo.getNewValue().equals("")) {
                        contactInfosNEW.remove(contactInfo);


                        try {
                            String oldPhone = contactInfo.value;
                            SocialModel socialModel = ((ContactProfileDataFragment) parentFragment).selectedContact.get(0).getSocialModel();
                            if (socialModel.getViberLink() != null && !socialModel.getViberLink().isEmpty()) {
                                if (socialModel.getViberLink().equalsIgnoreCase(oldPhone)) {
                                    contactsService.deleteNoteContact(((ContactProfileDataFragment) parentFragment).selectedContact.get(0).getIdContact(), socialModel.getViberLink());
                                    socialModel.setViberLink(null);
                                    ((ContactProfileDataFragment) parentFragment).selectedContact.get(0).hasViber = false;
                                } else {
                                    String phoneViber = socialModel.getViberLink().replaceAll("[\\s\\-\\+\\(\\)]", "");
                                    String oldReplace = oldPhone.replaceAll("[\\s\\-\\+\\(\\)]", "");
                                    if (phoneViber.equalsIgnoreCase(oldReplace)) {
                                        contactsService.deleteNoteContact(((ContactProfileDataFragment) parentFragment).selectedContact.get(0).getIdContact(), socialModel.getViberLink());
                                        socialModel.setViberLink(null);
                                        ((ContactProfileDataFragment) parentFragment).selectedContact.get(0).hasViber = false;
                                    }
                                }
                            }

                            if (socialModel.getTelegramLink() != null && !socialModel.getTelegramLink().isEmpty()) {
                                if (socialModel.getTelegramLink().equalsIgnoreCase(oldPhone)) {
                                    contactsService.deleteNoteContact(((ContactProfileDataFragment) parentFragment).selectedContact.get(0).getIdContact(), socialModel.getTelegramLink());
                                    socialModel.setTelegramLink(null);
                                    ((ContactProfileDataFragment) parentFragment).selectedContact.get(0).hasTelegram = false;
                                } else {
                                    String phoneViber = socialModel.getTelegramLink().replaceAll("[\\s\\-\\+\\(\\)]", "");
                                    String oldReplace = oldPhone.replaceAll("[\\s\\-\\+\\(\\)]", "");
                                    if (phoneViber.equalsIgnoreCase(oldReplace)) {
                                        contactsService.deleteNoteContact(((ContactProfileDataFragment) parentFragment).selectedContact.get(0).getIdContact(), socialModel.getTelegramLink());
                                        socialModel.setTelegramLink(null);
                                        ((ContactProfileDataFragment) parentFragment).selectedContact.get(0).hasTelegram = false;
                                    }
                                }
                            }

                            if (socialModel.getWhatsappLink() != null && !socialModel.getWhatsappLink().isEmpty()) {
                                if (socialModel.getWhatsappLink().equalsIgnoreCase(oldPhone)) {
                                    contactsService.deleteNoteContact(((ContactProfileDataFragment) parentFragment).selectedContact.get(0).getIdContact(), socialModel.getWhatsappLink());
                                    socialModel.setWhatsappLink(null);
                                    ((ContactProfileDataFragment) parentFragment).selectedContact.get(0).hasWhatsapp = false;
                                } else {
                                    String phoneViber = socialModel.getWhatsappLink().replaceAll("[\\s\\-\\+\\(\\)]", "");
                                    String oldReplace = oldPhone.replaceAll("[\\s\\-\\+\\(\\)]", "");
                                    if (phoneViber.equalsIgnoreCase(oldReplace)) {
                                        contactsService.deleteNoteContact(((ContactProfileDataFragment) parentFragment).selectedContact.get(0).getIdContact(), socialModel.getWhatsappLink());
                                        socialModel.setWhatsappLink(null);
                                        ((ContactProfileDataFragment) parentFragment).selectedContact.get(0).hasWhatsapp = false;
                                    }
                                }
                            }
                        } catch (Exception e) {

                        }



                        continue;
                    } else {


                        try {
                            String oldPhone = contactInfo.value;
                            SocialModel socialModel = ((ContactProfileDataFragment) parentFragment).selectedContact.get(0).getSocialModel();
                            if (socialModel.getViberLink() != null && !socialModel.getViberLink().isEmpty()) {
                                if (socialModel.getViberLink().equalsIgnoreCase(oldPhone)) {
                                    contactsService.updateNote(((ContactProfileDataFragment) parentFragment).selectedContact.get(0).getIdContact(), socialModel.getViberLink(), contactInfo.getNewValue());
                                    socialModel.setViberLink(contactInfo.getNewValue());
                                } else {
                                    String phoneViber = socialModel.getViberLink().replaceAll("[\\s\\-\\+\\(\\)]", "");
                                    String oldReplace = oldPhone.replaceAll("[\\s\\-\\+\\(\\)]", "");
                                    if (phoneViber.equalsIgnoreCase(oldReplace)) {
                                        contactsService.updateNote(((ContactProfileDataFragment) parentFragment).selectedContact.get(0).getIdContact(), socialModel.getViberLink(), contactInfo.getNewValue());
                                        socialModel.setViberLink(contactInfo.getNewValue());
                                    }
                                }
                            }

                            if (socialModel.getTelegramLink() != null && !socialModel.getTelegramLink().isEmpty()) {
                                if (socialModel.getTelegramLink().equalsIgnoreCase(oldPhone)) {
                                    contactsService.updateNote(((ContactProfileDataFragment) parentFragment).selectedContact.get(0).getIdContact(), socialModel.getTelegramLink(), contactInfo.getNewValue());
                                    socialModel.setTelegramLink(contactInfo.getNewValue());
                                } else {
                                    String phoneViber = socialModel.getTelegramLink().replaceAll("[\\s\\-\\+\\(\\)]", "");
                                    String oldReplace = oldPhone.replaceAll("[\\s\\-\\+\\(\\)]", "");
                                    if (phoneViber.equalsIgnoreCase(oldReplace)) {
                                        contactsService.updateNote(((ContactProfileDataFragment) parentFragment).selectedContact.get(0).getIdContact(), socialModel.getTelegramLink(), contactInfo.getNewValue());
                                        socialModel.setTelegramLink(contactInfo.getNewValue());
                                    }
                                }
                            }

                            if (socialModel.getWhatsappLink() != null && !socialModel.getWhatsappLink().isEmpty()) {
                                if (socialModel.getWhatsappLink().equalsIgnoreCase(oldPhone)) {
                                    contactsService.updateNote(((ContactProfileDataFragment) parentFragment).selectedContact.get(0).getIdContact(), socialModel.getWhatsappLink(), contactInfo.getNewValue());
                                    socialModel.setWhatsappLink(contactInfo.getNewValue());
                                } else {
                                    String phoneViber = socialModel.getWhatsappLink().replaceAll("[\\s\\-\\+\\(\\)]", "");
                                    String oldReplace = oldPhone.replaceAll("[\\s\\-\\+\\(\\)]", "");
                                    if (phoneViber.equalsIgnoreCase(oldReplace)) {
                                        contactsService.updateNote(((ContactProfileDataFragment) parentFragment).selectedContact.get(0).getIdContact(), socialModel.getWhatsappLink(), contactInfo.getNewValue());
                                        socialModel.setWhatsappLink(contactInfo.getNewValue());
                                    }
                                }
                            }
                        } catch (Exception e) {

                        }




                        contactInfo.value = contactInfo.getNewValue();
                    }

                    ContactProfileDataFragment.checkRealPhone = true;
                    //check_phone = true;

                }
            }

            if (contactInfo.isNote) {
                if (contactInfo.getNewValue() != null) {
                    //contactsService.updatePhone(String.valueOf(((ContactProfileDataFragment) parentFragment).selectedContact.get(0).getIdContact()), contactInfo.value, contactInfo.getNewValue());
                    contactsService.updateNote(String.valueOf(((ContactProfileDataFragment) parentFragment).selectedContact.get(0).getIdContact()), contactInfo.value, contactInfo.getNewValue());

                    if (contactInfo.getNewValue().equals("")) {
                        contactInfosNEW.remove(contactInfo);
                        continue;
                    } else
                        contactInfo.value = contactInfo.getNewValue();
                }
            }

            System.out.println("INFO = " + contactInfo.value);
        }

        if (!ContactProfileDataFragment.checkRealPhone) {

           /* ContactInfo contactInfo = new ContactInfo();
            contactInfo.type = "phone";
            contactInfo.value = "+000000000000";*/
            contactInfosNEW.add(new ContactInfo("phone", "+000000000000", false, true, false, false, false));
            ContactProfileDataFragment.checkRealPhone = true;
        } else {
            for (ContactInfo c : contactInfosNEW) {
                if (c.value.equals("+000000000000")) {
                    contactInfosNEW.remove(c);
                    break;
                }
            }
        }

        return contactInfosNEW;
    }

    @Override
    public int getItemCount() {
        return contactInfosNEW.size();
    }

    public void setEditModeEnabled(boolean state) {
        editModeEnabled = state;
        selectedId = null;
        notifyDataSetChanged();
    }

    public RealmList<ContactInfo> getContactInfos() {
        return contactInfos;
    }

    public void setContactInfos(RealmList<ContactInfo> contactInfos) {
        this.contactInfos = contactInfos;

        notifyDataSetChanged();
    }

}

