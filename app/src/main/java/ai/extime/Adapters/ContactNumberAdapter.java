package ai.extime.Adapters;


import android.app.Activity;
import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberToCarrierMapper;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import ai.extime.Enums.TypeData;
import ai.extime.Fragments.ContactProfileDataFragment;
import ai.extime.Fragments.ProfileFragment;
import ai.extime.Models.Contact;
import ai.extime.Models.ContactInfo;

import com.extime.R;

import ai.extime.Utils.ClipboardType;


public class ContactNumberAdapter extends RecyclerView.Adapter<ContactNumberAdapter.ContactViewHolder> {

    public RealmList<ContactInfo> contactInfos;
    public RealmList<ContactInfo> contactInfosNEW;


    private Context context;
    private boolean editModeEnabled;
    private String selectedId = null;
    private Fragment parentFragment;
    private ContactInfo contactInfoForReplace;
    View v;
    public RecyclerView.LayoutParams params;
    public RecyclerView.LayoutParams params2;

    private Activity activity;

    public boolean blur = false;

    static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView type;
        EditText value;
        TextView value1;
        ImageView contactTypeImage;
        CheckBox isConfirmed;
        TextView carrier;
        ImageView region;
        ImageView createdCall2me;
        TextView regionNA;
        FrameLayout frame;

        ImageView acceptData;

        TextView typeSocial;


        ContactViewHolder(View itemView) {
            super(itemView);

            acceptData = itemView.findViewById(R.id.acceptData);


            frame = (FrameLayout) itemView.findViewById(R.id.frameDataContact);
            type = (TextView) itemView.findViewById(R.id.hashtag_text);
            //value = (EditText) itemView.findViewById(R.id.value);
            value1 = (TextView) itemView.findViewById(R.id.value1);
            isConfirmed = (CheckBox) itemView.findViewById(R.id.checkConf);
            contactTypeImage = (ImageView) itemView.findViewById(R.id.type_image);
            carrier = (TextView) itemView.findViewById(R.id.carrier);
            region = (ImageView) itemView.findViewById(R.id.region);
            regionNA = (TextView) itemView.findViewById(R.id.regionNA);
            createdCall2me = (ImageView) itemView.findViewById(R.id.contactFromCall2me);

            typeSocial = itemView.findViewById(R.id.typeSocial);
        }
    }

    public void updateContactsList(RealmList<ContactInfo> contactInfos) {
        this.contactInfos = new RealmList<>();
        this.contactInfosNEW = new RealmList<>();
        for (int j = 0; j < contactInfos.size(); j++) {

            if (!contactInfos.get(j).toString().equals("+000000000000")) {

                this.contactInfosNEW.add(contactInfos.get(j));
            } else {

                ProfileFragment.noPhone = true;
                //    this.contactInfosNEW.add(contactInfos.get(j));
            }
        }

        this.contactInfos = contactInfos;



        notifyDataSetChanged();
    }

    public void replaceContactNumber(ContactInfo contactInfo) {


        Realm realm = Realm.getDefaultInstance(); //+
        realm.beginTransaction();
        contactInfos.remove(contactInfoForReplace);
        realm.commitTransaction();
        realm.close();
        notifyDataSetChanged();
    }

    public void replaceValuePhoneContactNumber(ContactInfo contactInfo, String value) {


        Realm realm = Realm.getDefaultInstance(); //+
        for (ContactInfo contactInfo1 : contactInfos) {
            if (contactInfo.value.equalsIgnoreCase(contactInfo1.value)) {
                realm.beginTransaction();
                contactInfo1.value = value;
                realm.commitTransaction();
                break;
            }
        }
        for (ContactInfo contactInfo1 : contactInfosNEW) {
            if (contactInfo.value.equalsIgnoreCase(contactInfo1.value)) {
                realm.beginTransaction();
                contactInfosNEW.remove(contactInfo1);
                realm.commitTransaction();
                break;
            }
        }

        realm.close();

        notifyDataSetChanged();
    }


    public void removeContactNumber(ContactInfo contactInfoForReplace) {

        Realm realm = Realm.getDefaultInstance(); //+
        realm.beginTransaction();

        if (contactInfos.size() == 1)
            contactInfos.remove(0);
        else
            for (int i = 0; i < contactInfos.size(); i++) {
                if (contactInfos.get(i).value.equals(contactInfoForReplace.value))
                    contactInfos.remove(i);
            }

        for (int i = 0; i < contactInfosNEW.size(); i++) {
            if (contactInfosNEW.get(i).value.equals(contactInfoForReplace.value))
                contactInfosNEW.remove(i);
        }


        realm.commitTransaction();
        realm.close();
        notifyDataSetChanged();
    }

    public void sortByPrimary(){
        Realm realm = Realm.getDefaultInstance(); //+
        realm.beginTransaction();
        try {
            Collections.sort(contactInfosNEW, (contactInfo, t1) -> Boolean.compare(t1.isPrimary, contactInfo.isPrimary));
            Collections.sort(contactInfos, (contactInfo, t1) -> Boolean.compare(t1.isPrimary, contactInfo.isPrimary));
        }catch (Exception e){
            e.printStackTrace();
        }



        realm.commitTransaction();
        realm.close();
    }


    public ContactNumberAdapter(Context context, RealmList<ContactInfo> contactInfos, Fragment parentFragment, Activity activity) {

        Realm realm = Realm.getDefaultInstance(); //+
        realm.beginTransaction();
        try {
            Collections.sort(contactInfos, (contactInfo, t1) -> Boolean.compare(t1.isPrimary, contactInfo.isPrimary));
        }catch (Exception e){
            e.printStackTrace();
        }



        realm.commitTransaction();
        realm.close();

        this.activity = activity;
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
        this.parentFragment = parentFragment;

        params = new RecyclerView.LayoutParams(0, 0);
        params2 = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 150);
    }

    public ContactNumberAdapter(Context context, ArrayList<Contact> contacts, Fragment parentFragment, Activity activity) {

        Realm realm = Realm.getDefaultInstance(); //+
        realm.beginTransaction();
        try {
            Collections.sort(contactInfos, (contactInfo, t1) -> Boolean.compare(t1.isPrimary, contactInfo.isPrimary));
        }catch (Exception e){
            e.printStackTrace();
        }



        realm.commitTransaction();
        realm.close();

        this.activity = activity;
        this.context = context;
        // this.contacts = contacts;
        contactInfos = new RealmList<>();
        this.contactInfosNEW = new RealmList<>();
        for (int i = 0; i < contacts.size(); i++) {
            if (contacts.get(i).listOfContactInfo != null)
                for (int j = 0; j < contacts.get(i).listOfContactInfo.size(); j++) {

                    if (!contacts.get(i).listOfContactInfo.get(j).toString().equals("+000000000000")) {

                        this.contactInfosNEW.add(contacts.get(i).listOfContactInfo.get(j));
                        contactInfos.add(contacts.get(i).listOfContactInfo.get(j));

                    } else {

                        ProfileFragment.noPhone = true;
                        contactInfos.add(contacts.get(i).listOfContactInfo.get(j));
                        //  contactInfos.add(contacts.get(i).listOfContactInfo.get(j));
                    }
                }
        }


        editModeEnabled = false;
        this.parentFragment = parentFragment;
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_contact_number, viewGroup, false);


        return new ContactViewHolder(v);
    }

    public void setBlur(boolean blur) {
        this.blur = blur;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(ContactViewHolder viewHolder, int i) {



        ContactInfo contactInfo = contactInfosNEW.get(i);

        if (contactInfo.isPrimary)
            viewHolder.acceptData.setVisibility(View.VISIBLE);
        else
            viewHolder.acceptData.setVisibility(View.GONE);

        viewHolder.carrier.setVisibility(View.GONE);
        viewHolder.region.setVisibility(View.GONE);
        viewHolder.regionNA.setVisibility(View.GONE);
        viewHolder.typeSocial.setVisibility(View.GONE);


        if (contactInfo.type.equals("phone") && contactInfo.value.equals("+000000000000")) {

            //v.setVisibility(View.GONE);
            // viewHolder.frame.setVisibility(View.GONE);
            // v.setVisibility(View.GONE);

            // v.setLayoutParams(params);

        } else {

            // v.setLayoutParams(params2);
            // v.setVisibility(View.VISIBLE);
            viewHolder.frame.setVisibility(View.VISIBLE);

            viewHolder.createdCall2me.setVisibility(View.GONE);
            viewHolder.region.setVisibility(View.GONE);


            Realm realm = Realm.getDefaultInstance(); //+
            realm.beginTransaction();

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


            //Matcher matcher = Patterns.WEB_URL.matcher(contactInfo.value.toString());

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
                } else if (ClipboardType.isGitHub(contactInfo.value)) {
                    viewHolder.contactTypeImage.setImageResource(R.drawable.github_logo_64);
                    viewHolder.type.setText("github");
                } else if (ClipboardType.isMedium(contactInfo.value)) {
                    viewHolder.contactTypeImage.setImageResource(R.drawable.medium_size_64);
                    viewHolder.type.setText("medium");
                }/*else if (ClipboardType.isZoom(contactInfo.value)) {
                    viewHolder.contactTypeImage.setImageResource(R.drawable.zoom_logo);
                    viewHolder.type.setText("zoom");
                }*/ else if (ClipboardType.is_Tumblr(contactInfo.value)) {
                    viewHolder.contactTypeImage.setImageResource(R.drawable.tumblr);
                    viewHolder.type.setText("tumblr");
                } else if (ClipboardType.is_Angel(contactInfo.value)) {
                    viewHolder.contactTypeImage.setImageResource(R.drawable.angel);
                    viewHolder.type.setText("angel");
                } else if (ClipboardType.isG_Sheet(contactInfo.value)) {
                    viewHolder.contactTypeImage.setImageResource(R.drawable.google_sheets);
                    viewHolder.type.setText("google sheet");
                } else if (ClipboardType.isG_Doc(contactInfo.value)) {
                    viewHolder.contactTypeImage.setImageResource(R.drawable.google_docs);
                    viewHolder.type.setText("google doc");
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

                if(contactInfo.titleValue != null && !contactInfo.titleValue.isEmpty())
                    viewHolder.type.setText(contactInfo.titleValue);
                
                viewHolder.regionNA.setVisibility(View.GONE);
            }



           /* ArrayList<Clipboard> listOfSites = new ArrayList<>();

            if (matcher.matches()) {
                listOfSites.add(new Clipboard(R.drawable.icn_popup_web, text, R.drawable.icn_popup_web, dateFormant.format(new Date()), ClipboardEnum.WEB));
            }
            return listOfSites;*/


            realm.commitTransaction();




            if (contactInfo.isPhone) {

                String phoneNumber = contactInfo.value;
                realm.beginTransaction();
                contactInfo.typeData = TypeData.PHONE.toString();
                realm.commitTransaction();



                viewHolder.contactTypeImage.setImageResource(R.drawable.icn_phone);


                new Thread(new Runnable() {
                    @Override
                    public void run() {


                        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();


                        try {
                            Phonenumber.PhoneNumber swissNumberProto = phoneUtil.parse(phoneNumber, "");
                            PhoneNumberToCarrierMapper carrierMapper = PhoneNumberToCarrierMapper.getInstance();

                            String region;
                            region = phoneUtil.getRegionCodeForCountryCode(swissNumberProto.getCountryCode());

                            String carrier;
                            carrier = carrierMapper.getNameForNumber(swissNumberProto, Locale.ENGLISH);


                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {


                                    viewHolder.regionNA.setVisibility(View.VISIBLE);

                                    if (region.length() > 0) {
                                        viewHolder.regionNA.setVisibility(View.GONE);
                                        try {
                                            int drawableResourceId = context.getResources().getIdentifier("country_" + region.toLowerCase(), "drawable", context.getPackageName());
                                            viewHolder.region.setImageResource(drawableResourceId);
                                            viewHolder.region.setVisibility(View.VISIBLE);
                                        } catch (NullPointerException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    if (carrier.length() > 0) {
                                        viewHolder.carrier.setText("(" + carrier + ")");
                                        viewHolder.carrier.setVisibility(View.VISIBLE);
                                    }

                                }
                            });


                        } catch (NumberParseException e) {
                            //e.printStackTrace();

                            System.err.println("NumberParseException was thrown: " + e.toString());
                            String str = phoneNumber.replaceAll("[\\.\\s\\-\\+\\(\\)]", "");
                            if ((str.length() == 11 && str.charAt(0) == '8') || (str.length() == 12 && str.charAt(0) == '+' && str.charAt(1) == '8')) {

                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {


                                        viewHolder.regionNA.setVisibility(View.GONE);
                                        try {
                                            int drawableResourceId = context.getResources().getIdentifier("country_ru", "drawable", context.getPackageName());
                                            viewHolder.region.setImageResource(drawableResourceId);
                                            viewHolder.region.setVisibility(View.VISIBLE);
                                        } catch (NullPointerException e1) {
                                            e1.printStackTrace();
                                        }


                                    }
                                });

                            }
                        }


                    }
                }).start();

            }



            realm.close();

            viewHolder.isConfirmed.setVisibility(contactInfo.isConfirmed ? View.VISIBLE : View.GONE);
            //viewHolder.value.setText(listOfContactInfo.value);


            //===========================================


            String text = contactInfo.value;
            if (ClipboardType.isInsta(contactInfo.value)) {
                //text = contact.getSocialModel().getInstagramLink();

                if (text.contains(".com/")) {
                    int ind = text.indexOf(".com/");
                    text = text.substring(ind + 5, text.length());

                    if (text.contains("?")) {
                        int in = text.indexOf("?");
                        text = text.substring(0, in);
                    }
                    if (text.contains("p/") && text.indexOf("p/") == 0) {
                        //int indP = text.indexOf("p/");
                        text = text.substring(2);
                    }
                }

            }


            viewHolder.value1.setText(text);


            if (text.length() > 40)
                viewHolder.value1.setText(text.substring(0, 38) + "...");


            if (blur) {
                viewHolder.value1.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                float radius = viewHolder.value1.getTextSize() / 3.3f;
                BlurMaskFilter filter = new BlurMaskFilter(radius, BlurMaskFilter.Blur.NORMAL);
                viewHolder.value1.getPaint().setMaskFilter(filter);
            } else {
                viewHolder.value1.getPaint().setMaskFilter(null);
            }

            //viewHolder.value1.setMovementMethod(null);


            if (contactInfo.type.compareTo("Joined") == 0) {
                viewHolder.createdCall2me.setVisibility(View.VISIBLE);
                Date date = new Date(Long.valueOf(contactInfo.value));
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
                int month = cal.get(Calendar.MONTH);
                int year = cal.get(Calendar.YEAR);
                viewHolder.value1.setText(dayOfMonth + "." + (month + 1) + "." + year);
                viewHolder.contactTypeImage.setImageResource(R.drawable.icn_login);
            } else {
                viewHolder.frame.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        String notes = ((TextView) v.findViewById(R.id.value1)).getText().toString();

                        /*ContactInfo contactInfo1 = null;
                        for (ContactInfo contactInfo2 : contactInfosNEW) {
                            if (contactInfo2.value.trim().equals(contactInfo.value)) {
                                contactInfo1 = contactInfo2;
                            }
                        }*/

                        /*if(notes.length() > 5 && notes.substring(notes.length()-3,notes.length() ).equals("...")){

                            notes = notes.substring(0, notes.length()-3);
                        }*/

                        if (((ContactProfileDataFragment) parentFragment).getQuantityOpenedViews() > 0) {
                            ((ContactProfileDataFragment) parentFragment).closeOtherPopup();
                        } else {
                            if (((ContactProfileDataFragment) parentFragment).popupProfileEdit != null && ((ContactProfileDataFragment) parentFragment).popupProfileEdit.getVisibility() != View.VISIBLE)
                                ((ContactProfileDataFragment) parentFragment).showEditPopupPreview(contactInfo, ((TextView) v.findViewById(R.id.hashtag_text)).getText().toString());
                        }
                    }
                });


            }
        }
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
        // this.contactInfos = contactInfos;
        this.contactInfosNEW = new RealmList<>();
        for (int j = 0; j < contactInfos.size(); j++) {

            if (!contactInfos.get(j).toString().equals("+000000000000")) {

                this.contactInfosNEW.add(contactInfos.get(j));
            } else {

                ProfileFragment.noPhone = true;
                //  this.contactInfosNEW.add(contactInfos.get(j));
            }
        }

        this.contactInfos = contactInfos;

        notifyDataSetChanged();
    }
}

