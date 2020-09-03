package ai.extime.Models;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


import ai.extime.Enums.ClipboardEnum;
import ai.extime.Events.AddContact;
import ai.extime.Fragments.ContactsFragment;
import ai.extime.Services.ContactCacheService;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import ai.extime.Adapters.ContactAdapter;
import ai.extime.Enums.FillDataEnums;
import ai.extime.Services.ContactsService;
import ai.extime.Utils.ClipboardType;

public class Contact extends RealmObject implements Serializable, Parcelable {
    public boolean isFullLoaded = false;

    @PrimaryKey
    private long id;
    private String name;
    private String company;
    private String companyPossition;
    private String carrier;
    private String region;
    private String adress;
    public String webSite = "";
    public boolean hashtag;

    public RealmList<ListAdress> listOfAdress;
    public boolean call2me;
    public boolean isNew;
    public boolean isCreate;
    public String time;
    private SocialModel socialModel;
    private String idContact;

    public String shortInto;
    //public String emailCompany;

    protected Contact(Parcel in) {
        if(in != null) {
            try {
                isFullLoaded = in.readByte() != 0;
                id = in.readLong();
                name = in.readString();
                company = in.readString();
                companyPossition = in.readString();
                carrier = in.readString();
                region = in.readString();
                adress = in.readString();
                webSite = in.readString();
                hashtag = in.readByte() != 0;
                call2me = in.readByte() != 0;
                isNew = in.readByte() != 0;
                isCreate = in.readByte() != 0;
                time = in.readString();
                idContact = in.readString();
                photoURL = in.readString();
                color = in.readInt();
                hasViber = in.readByte() != 0;
                hasTelegram = in.readByte() != 0;
                hasFacebook = in.readByte() != 0;
                hasWhatsapp = in.readByte() != 0;
                hasSkype = in.readByte() != 0;
                hasLinked = in.readByte() != 0;
                hasTwitter = in.readByte() != 0;
                hasYoutube = in.readByte() != 0;
                hasInst = in.readByte() != 0;
                hasVk = in.readByte() != 0;
                shortInto = in.readString();
            }
            catch (IllegalStateException exception) {
                exception.printStackTrace();
                this.dateCreate = new Date();
                this.listOfContactInfo = new RealmList<>();
                this.accountTypes = new RealmList<>();
                this.listOfContacts = new RealmList<>();
                this.listOfHashtags = new RealmList<>();
            }
        }
        else {
            this.dateCreate = new Date();
            this.listOfContactInfo = new RealmList<>();
            this.accountTypes = new RealmList<>();
            this.listOfContacts = new RealmList<>();
            this.listOfHashtags = new RealmList<>();
        }
    }

    public static final Creator<Contact> CREATOR = new Creator<Contact>() {
        @Override
        public Contact createFromParcel(Parcel in) {
            return new Contact(in);
        }

        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };

    public SocialModel getSocialModel() {
        return socialModel;
    }

    public void setSocialModel(SocialModel socialModel) {
        this.socialModel = socialModel;
    }


    public String photoURL;
    public int color;
    public RealmList<ContactInfo> listOfContactInfo;
    public RealmList<ListAdress> accountTypes;
    private RealmList<HashTag> listOfHashtags;
    public boolean hasViber;
    public boolean hasTelegram;
    public boolean hasFacebook;
    public boolean hasWhatsapp;
    public boolean hasSkype;
    public boolean hasLinked;
    public boolean hasTwitter;
    public boolean hasYoutube;
    public boolean hasMedium;
    public RealmList<Contact> listOfContacts;
    public boolean hasInst;
    public boolean hasVk;
    private Date dateCreate;

    public boolean isFavorite;

    public boolean isImportant;
    public boolean isFinished;
    public boolean isPause;

    public boolean isCrown;
    public boolean isVip;
    public boolean isStartup;
    public boolean isInvestor;

    public  Date dateAddMark;

    public void createDate() {
        dateAddMark = new Date();
    }



    public Contact() {
        this.dateCreate = new Date();
        this.listOfContactInfo = new RealmList<>();
        this.accountTypes = new RealmList<>();
        this.listOfContacts = new RealmList<>();
        this.listOfHashtags = new RealmList<>();

    }

    public Contact(Date dateCreate) {
        this.dateCreate = dateCreate;
        this.listOfContactInfo = new RealmList<>();
        this.accountTypes = new RealmList<>();
        this.listOfContacts = new RealmList<>();
        this.listOfHashtags = new RealmList<>();

    }

    public Contact(int id, String name, @Nullable String company, boolean hashtag, boolean call2me, boolean isNew, String time, Integer imageId, Date dateCreate) {
        this.id = id;
        this.name = name;
        this.company = company;
        this.hashtag = hashtag;
        this.call2me = call2me;
        this.isNew = isNew;
        this.time = time;
        this.color = Color.RED;
        this.listOfContactInfo = new RealmList<>();
        this.accountTypes = new RealmList<>();
        this.listOfContacts = new RealmList<>();
        this.listOfHashtags = new RealmList<>();
        this.hasViber = false;
        this.hasTelegram = false;
        this.hasFacebook = false;
        this.hasWhatsapp = false;
        this.hasTwitter = false;
        this.hasYoutube = false;
        this.hasSkype = false;
        this.hasLinked = false;
        this.hasInst = false;
        this.hasVk = false;
        this.isFullLoaded = false;
        this.dateCreate = dateCreate;
        this.isCreate = false;


        //    this.listOfAdress = new RealmList<>();
    }


    public void addAdress(String adress) {
        listOfAdress.add(new ListAdress(adress));
    }

    public RealmList<ListAdress> getListOfAdress() {
        return listOfAdress;
    }

    public boolean hasPhone() {
        for (ContactInfo contactInfo : this.listOfContactInfo)
            if (contactInfo.isPhone)
                return true;
        return false;
    }

    public RealmList<HashTag> getListOfHashtags() {
        return listOfHashtags;
    }

    public void setListOfHashtags(RealmList<HashTag> listOfHashtags) {
        this.listOfHashtags = listOfHashtags;
    }

    public void addPhone(String phoneNumber) {
        if (phoneNumber != null && !phoneNumber.equals("")) {
            System.out.println("ADD phone " + phoneNumber);
            listOfContactInfo.add(new ContactInfo("phone", phoneNumber, false, true, false, false, false));
        }

    }




    public Date getDateCreate() {
        return dateCreate;
    }

    public void setDateCreate(Date datecreate) {
        this.dateCreate = datecreate;
    }

    public void addEmail(String email) {
        if (email != null && !email.equals(""))
            listOfContactInfo.add(new ContactInfo("email", email, true, false, false, false, false));
    }

    public void addNote(String note) {
        if (note != null && !note.equals("")) {
            System.out.println("NOTE = "+note);
            if ((note.contains("youtu.be") || note.contains("youtube")) && note.contains("?uid")) {
                int ind = note.indexOf('?');
                if (ind != -1)
                    note = note.substring(0, ind);
            }

            listOfContactInfo.add(new ContactInfo("note", note, false, false, false, true, false));
        }
    }

    public void addBio(String note) {
        if (note != null && !note.equals("")) {
            listOfContactInfo.add(0, new ContactInfo("bio", note, false, false, false, false, false));
        }
    }

    public void addDateBirth(String note) {
        if (note != null && !note.equals("")) {
            listOfContactInfo.add(new ContactInfo("date of birth", note, false, false, false, false, false));
        }
    }


    public void addAddress(String address) {
        if (address != null && !address.equals(""))
            listOfContactInfo.add(new ContactInfo("address", address, false, false, true, false, false));
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public String getCompanyPossition() {
        return companyPossition;
    }

    public void setCompanyPossition(String companyPossition) {
        this.companyPossition = companyPossition;
    }

    public RealmList<ContactInfo> getListOfContactInfo() {

        return listOfContactInfo;
    }

    public void setListOfContactInfo(RealmList<ContactInfo> listOfContactInfo) {
        this.listOfContactInfo = listOfContactInfo;
    }

    public void setListOfAdress(RealmList<ListAdress> listOfAdress) {
        this.listOfAdress = listOfAdress;
    }

    public Contact fillData(Context context, ContactsService contactsService, FillDataEnums typeEnum, ContactAdapter adapterC) {

        Contact companyNewReturn = null;
        if (listOfContacts == null || listOfContacts.isEmpty()) {


            Realm realm = Realm.getDefaultInstance(); //-


            String contactId = idContact;

            realm.beginTransaction();
            String contactName = ContactsService.getDisplayName(context, idContact);
            if (contactName == null || contactName.equals(""))
                contactName = name;
            else
                name = contactName;

            String photoURL = ContactsService.getPhotoURI(context, contactId);
            if (photoURL == null || photoURL.equals(""))
                photoURL = getPhotoURL();
            else
                setPhotoURL(photoURL);

            realm.commitTransaction();

            realm.beginTransaction();

            int nameHash = name.hashCode();
            color = Color.rgb(Math.abs(nameHash * 28439) % 255, Math.abs(nameHash * 211239) % 255, Math.abs(nameHash * 42368) % 255);

            listOfContactInfo = new RealmList<>();
            accountTypes = new RealmList<>();


            ArrayList<String> phone = contactsService.getContactPhones(contactId);


            ArrayList<String> phonesContact = new ArrayList<>();

            if (phone != null && phone.size() > 0) {
                for (int i = 0; i < phone.size(); i++) {
                    if (!phonesContact.contains(phone.get(i).replaceAll("[\\.\\s\\-\\+\\(\\)]", ""))) {
                        phonesContact.add(phone.get(i).replaceAll("[\\.\\s\\-\\+\\(\\)]", ""));
                        addPhone(phone.get(i));
                        contactName = contactName.trim();
                        String phonee = phone.get(i).trim();
                        phonee = phonee.replaceAll(" ", "");
                        phonee = phonee.replaceAll("-", "");

                        if (contactName.contains(phonee) && contactName.length() > phonee.length()) {
                            contactName = contactName.replace(phonee, "");
                            contactName = contactName.trim();
                        }
                    }
                }
            } else {
                addPhone("+000000000000");
                contactsService.addPhoneToContact(contactId, "+000000000000", contactName.trim());
                setName(contactName.trim());

            }


            ArrayList<String> addr = contactsService.getContactEmails(contactId);
            for (int i = 0; i < addr.size(); i++) {
                addEmail(addr.get(i));
            }

            RealmList<HashTag> hashList = new RealmList<>();
            ArrayList<String> listOfHash = new ArrayList<>();

            SocialModel socialModel = realm.createObject(SocialModel.class);
            hasFacebook = false;
            hasInst = false;
            hasLinked = false;
            hasVk = false;
            hasSkype = false;
            hasTelegram = false;
            hasViber = false;
            hasWhatsapp = false;
            hasYoutube = false;
            hasTwitter = false;
            hasMedium = false;

            Cursor noteCursor = contactsService.getCursorByType(ContactsService.TYPE_CURSOR.NOTE, contactId);

            if (noteCursor != null && noteCursor.getCount() > 0) {
                while (noteCursor.moveToNext()) {
                    String note = noteCursor.getString(noteCursor.getColumnIndex(ContactsContract.CommonDataKinds.Note.NOTE));
                    if (note != null && note.length() > 0) {
                        if (note.charAt(0) == '#' && !listOfHash.contains(note.toLowerCase().trim())) {
                            HashTag hashtag = realm.createObject(HashTag.class);
                            hashtag.setDate(new Date());
                            hashtag.setHashTagValue(note.toLowerCase().trim());
                            hashList.add(hashtag);
                            listOfHash.add(note.toLowerCase().trim());

                        } else if (ClipboardType.isInsta(note)) {
                            //SocialModel socialModel = contact.getSocialModel();
                            hasInst = true;


                            String username = note;
                            if (username.contains("?utm")) {
                                int ind = username.indexOf('?');
                                if (ind != -1)
                                    username = username.substring(0, ind);

                                contactsService.updateNote(contactId, note, username);
                            }

                            if (socialModel != null) {
                                if (socialModel.getInstagramLink() == null || socialModel.getInstagramLink().isEmpty())
                                    socialModel.setInstagramLink(username);
                                else {
                                    if (listOfContactInfo == null) {
                                        addNote(note);
                                    } else {
                                        boolean checkS = false;
                                        for (ContactInfo contactInfo : listOfContactInfo) {
                                            if (contactInfo.value.equalsIgnoreCase(note)) {
                                                checkS = true;
                                                break;
                                            }
                                        }
                                        if (!checkS)
                                            addNote(note);

                                    }
                                }
                            }
                        } else if (ClipboardType.isVk(note)) {
                            //SocialModel socialModel = contact.getSocialModel();
                            hasVk = true;

                            if (socialModel != null) {
                                if (socialModel.getVkLink() == null || socialModel.getVkLink().isEmpty())
                                    socialModel.setVkLink(note);
                                else {
                                    if (listOfContactInfo == null) {
                                        addNote(note);
                                    } else {
                                        boolean checkS = false;
                                        for (ContactInfo contactInfo : listOfContactInfo) {
                                            if (contactInfo.value.equalsIgnoreCase(note)) {
                                                checkS = true;
                                                break;
                                            }
                                        }
                                        if (!checkS)
                                            addNote(note);

                                    }
                                }
                            }

                        } else if (/*(note.length() > 21 && note.toString().substring(0, 21).equalsIgnoreCase("https://facebook.com/")) || (note.length() > 23 && note.toString().substring(0, 23).equalsIgnoreCase("https://m.facebook.com/")) ||
                                (note.length() > 25 && note.toString().substring(0, 25).equalsIgnoreCase("https://www.facebook.com/")) || (note.length() > 13 && note.toString().substring(0, 13).equalsIgnoreCase("facebook.com/")) ||
                                (note.length() > 15 && note.toString().substring(0, 15).equalsIgnoreCase("m.facebook.com/")) || (note.length() > 7 && note.toString().substring(0, 7).equalsIgnoreCase("fb.com/")) ||
                                (note.length() > 11 && note.toString().substring(0, 11).equalsIgnoreCase("www.fb.com/"))*/ ClipboardType.isFacebook(note)) {
                            //SocialModel socialModel = contact.getSocialModel();
                            hasFacebook = true;

                            if (socialModel != null) {
                                if (socialModel.getFacebookLink() == null || socialModel.getFacebookLink().isEmpty())
                                    socialModel.setFacebookLink(note);
                                else {
                                    if (listOfContactInfo == null) {
                                        addNote(note);
                                    } else {
                                        boolean checkS = false;
                                        for (ContactInfo contactInfo : listOfContactInfo) {
                                            if (contactInfo.value.equalsIgnoreCase(note)) {
                                                checkS = true;
                                                break;
                                            }
                                        }
                                        if (!checkS)
                                            addNote(note);

                                    }
                                }
                            }

                        } else if (ClipboardType.isLinkedIn(note)) {
                            //SocialModel socialModel = contact.getSocialModel();
                            hasLinked = true;

                            if (socialModel != null) {
                                if (socialModel.getLinkedInLink() == null || socialModel.getLinkedInLink().isEmpty())
                                    socialModel.setLinkedInLink(note);
                                else {
                                    if (listOfContactInfo == null) {
                                        addNote(note);
                                    } else {
                                        boolean checkS = false;
                                        for (ContactInfo contactInfo : listOfContactInfo) {
                                            if (contactInfo.value.equalsIgnoreCase(note)) {
                                                checkS = true;
                                                break;
                                            }
                                        }
                                        if (!checkS)
                                            addNote(note);

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
                            hasTelegram = true;

                            socialModel.setTelegramLink(note);

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
                           /* hasYoutube = true;


                            if (socialModel != null) {
                                if (socialModel.getYoutubeLink() == null || socialModel.getYoutubeLink().isEmpty())
                                    socialModel.setYoutubeLink(note);
                                else {
                                    if (listOfContactInfo == null) {
                                        addNote(note);
                                    } else {
                                        boolean checkS = false;
                                        for (ContactInfo contactInfo : listOfContactInfo) {
                                            if (contactInfo.value.equalsIgnoreCase(note)) {
                                                checkS = true;
                                                break;
                                            }
                                        }
                                        if (!checkS)
                                            addNote(note);

                                    }
                                }
                            }*/



                            if (note.contains("user") || note.contains("channel")) {
                                if (socialModel != null) {
                                    if (socialModel.getYoutubeLink() == null || socialModel.getYoutubeLink().isEmpty()) {
                                        socialModel.setYoutubeLink(note);
                                        hasYoutube = true;
                                    } else {
                                        if (getListOfContactInfo() == null) {
                                            addNote(note);
                                        } else {
                                            boolean checkS = false;
                                            for (ContactInfo contactInfo : getListOfContactInfo()) {
                                                if (contactInfo.value.equalsIgnoreCase(note)) {
                                                    checkS = true;
                                                    break;
                                                }
                                            }
                                            if (!checkS)
                                                addNote(note);

                                        }
                                    }
                                }
                            } else {
                                if (getListOfContactInfo() == null) {
                                    addNote(note);
                                } else {
                                    boolean checkS = false;
                                    for (ContactInfo contactInfo : getListOfContactInfo()) {
                                        if (contactInfo.value.equalsIgnoreCase(note)) {
                                            checkS = true;
                                            break;
                                        }
                                    }
                                    if (!checkS)
                                        addNote(note);

                                }
                            }


                        } else if (ClipboardType.isTwitter(note)) {
                            hasTwitter = true;

                            if (socialModel != null) {
                                if (socialModel.getTwitterLink() == null || socialModel.getTwitterLink().isEmpty())
                                    socialModel.setTwitterLink(note);
                                else {
                                    if (listOfContactInfo == null) {
                                        addNote(note);
                                    } else {
                                        boolean checkS = false;
                                        for (ContactInfo contactInfo : listOfContactInfo) {
                                            if (contactInfo.value.equalsIgnoreCase(note)) {
                                                checkS = true;
                                                break;
                                            }
                                        }
                                        if (!checkS)
                                            addNote(note);

                                    }
                                }
                            }
                        }else if(ClipboardType.isMedium(note)){

                            if (note.contains("com/@")) {
                                if (socialModel != null) {
                                    if (socialModel.getMediumLink() == null || socialModel.getMediumLink().isEmpty()) {
                                        socialModel.setMediumLink(note);
                                        hasMedium = true;
                                    } else {
                                        if (getListOfContactInfo() == null) {
                                            addNote(note);
                                        } else {
                                            boolean checkS = false;
                                            for (ContactInfo contactInfo : getListOfContactInfo()) {
                                                if (contactInfo.value.equalsIgnoreCase(note)) {
                                                    checkS = true;
                                                    break;
                                                }
                                            }
                                            if (!checkS)
                                                addNote(note);

                                        }
                                    }
                                }
                            } else {
                                if (getListOfContactInfo() == null) {
                                    addNote(note);
                                } else {
                                    boolean checkS = false;
                                    for (ContactInfo contactInfo : getListOfContactInfo()) {
                                        if (contactInfo.value.equalsIgnoreCase(note)) {
                                            checkS = true;
                                            break;
                                        }
                                    }
                                    if (!checkS)
                                        addNote(note);

                                }
                            }

                        } else {
                            //addNote(note);

                            boolean checkS = false;
                            for (ContactInfo contactInfo : getListOfContactInfo()) {
                                if (contactInfo.value.equalsIgnoreCase(note)) {
                                    contactsService.deleteNoteContact(idContact, note); //new
                                    checkS = true;
                                    break;
                                }
                            }
                            if (!checkS) {
                                addNote(note);

                                /*if (ClipboardType.isWeb(note)) {
                                    listUpdatingData.add(new DataUpdate(note, ClipboardEnum.WEB));
                                } else {
                                    listUpdatingData.add(new DataUpdate(note, ClipboardEnum.NOTE));
                                }*/
                            }

                        }
                    }
                }
            }
            if (noteCursor != null)
                noteCursor.close();

            setListOfHashtags(hashList);

            setSocialModel(socialModel);


            Cursor accountCursor = contactsService.getCursorByType(ContactsService.TYPE_CURSOR.ACCOUNT_TYPE, contactId);

            if (accountCursor != null && accountCursor.getCount() > 0) {

                while (accountCursor.moveToNext()) {
                    int accountTypeI = accountCursor.getColumnIndex(ContactsContract.RawContacts.ACCOUNT_TYPE);

                    if (accountTypeI != -1) {
                        String accountType = accountCursor.getString(accountTypeI);
                        if (accountType != null && accountType.contains("telegram") && phone.size() == 0) {

                        } else
                            addAccountType(accountType);

                        if (accountType != null) {


                            if (accountType.contains("viber") && phone.size() > 0) {
                                hasViber = true;
                                socialModel.setViberLink(phone.get(0));

                            }

                            if (accountType.contains("whatsapp") && phone.size() > 0) {
                                hasWhatsapp = true;
                                socialModel.setWhatsappLink(phone.get(0));

                            }

                            if (accountType.contains("telegram") && phone.size() > 0) {

                                if (phone.size() != 0 && !phone.get(0).equals("+000000000000")) {
                                    hasTelegram = true;

                                    socialModel.setTelegramLink(phone.get(0));
                                }
                            }
                        }
                    }
                }
                accountCursor.close();
            } else
                addAccountType("phone");


            Cursor adressCursor = contactsService.getCursorByType(ContactsService.TYPE_CURSOR.CONTACT_ADRESS, contactId);
            while (adressCursor != null && adressCursor.moveToNext()) {
                String adress = adressCursor.getString(adressCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
                addAddress(adress);
            }

            if (adressCursor != null)
                adressCursor.close();

            realm.commitTransaction();

            boolean found = false;

            Cursor organizationCursor = contactsService.getCursorByType(ContactsService.TYPE_CURSOR.ORGANIZATION, contactId);

            //Realm realm2 = Realm.getDefaultInstance();

            ArrayList<Contact> listOFCompanies = ContactCacheService.getCompaniesRealm(realm);

            realm.beginTransaction();
            setCompanyPossition(null);
            realm.commitTransaction();

            while (organizationCursor != null && organizationCursor.moveToNext()) {
                String orgName = organizationCursor.getString(organizationCursor.getColumnIndex(ContactsContract.CommonDataKinds.Organization.DATA));
                String companyPossition = organizationCursor.getString(organizationCursor.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TITLE));



                realm.beginTransaction();
                setCompanyPossition(companyPossition);

                realm.commitTransaction();

                if (orgName != null && !orgName.isEmpty()) {



                    String oldCompany = null;
                    if (getCompany() != null && !getCompany().isEmpty() && !getCompany().trim().equalsIgnoreCase(orgName.trim()))
                        oldCompany = getCompany();

                    realm.beginTransaction();
                    setCompany(orgName.trim());
                    realm.commitTransaction();

                    for (Contact searchCompanyContact : listOFCompanies) {
                        if ( searchCompanyContact != null && searchCompanyContact.isValid() && searchCompanyContact.getName().equalsIgnoreCase(orgName.trim())) {
                            if (oldCompany != null) {
                                for (Contact comp : listOFCompanies) {
                                    if (comp.isValid() && comp.getName().equalsIgnoreCase(oldCompany.trim())) {
                                        ContactCacheService.removeContactFromCompany(comp, this);
                                        break;
                                    }
                                }
                            }

                            if (!searchCompanyContact.listOfContacts.contains(this)) {
                                if (searchCompanyContact.getName().equals("1337")) {

                                }

                                realm.beginTransaction();
                                searchCompanyContact.listOfContacts.add(this);
                                realm.commitTransaction();
                            }
                            found = true;
                        }
                    }

                    if (!found) {
                        Date date1 = getDateCreate();
                        Calendar cal1 = Calendar.getInstance();
                        cal1.setTime(date1);
                        Time time1 = contactsService.getRandomDate();
                        time1.setHours(cal1.get(Calendar.HOUR_OF_DAY));
                        time1.setMinutes(cal1.get(Calendar.MINUTE));
                        time1.setSeconds(cal1.get(Calendar.SECOND));
                        Contact companyContact = new Contact(0, orgName.trim(), null, false, true, true, time1.toString(), null, date1);
                        companyContact.time = time.toString();
                        companyContact.color = Color.rgb(Math.abs(nameHash * 28439) % 255, Math.abs(nameHash * 211239) % 255, Math.abs(nameHash * 42368) % 255);
                        if (oldCompany != null) {
                            for (Contact comp : listOFCompanies) {
                                if ( comp != null && comp.isValid() && comp.getName().equalsIgnoreCase(oldCompany.trim())) {

                                    if (comp.listOfContacts.size() == 1) {
                                        //ContactAdapter cont = ContactAdapter.contactAd;
                                        adapterC.removeContactById(comp);
                                        ContactsFragment.UPD_ALL = true;
                                    }
                                    ContactCacheService.removeContactFromCompany(comp, this);
                                    break;
                                }
                            }
                        }
                        companyContact.listOfContacts.add(this);
                        ContactCacheService.updateCompany(companyContact);
                        listOFCompanies.add(ContactCacheService.getCompany(companyContact.getName()));
                        //ContactAdapter cont = ContactAdapter.contactAd;
                        //cont.addContact(companyContact);
                        if (typeEnum.equals(FillDataEnums.PROFILE) || typeEnum.equals(FillDataEnums.PREVIEW)) {
                            EventBus.getDefault().post(new AddContact(companyContact.getId()));
                            ContactsFragment.UPD_ALL = true;
                        } else if (typeEnum.equals(FillDataEnums.NEW)) {
                            companyNewReturn = companyContact;
                        }
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
                if (getCompany() != null) {

                    for (Contact comp : listOFCompanies) {
                        if (comp.isValid() && comp.getName().equalsIgnoreCase(getCompany())) {

                            if (comp.listOfContacts.size() == 1) {
                                //ContactAdapter cont = ContactAdapter.contactAd;
                                adapterC.removeContactById(comp);
                                ContactsFragment.UPD_ALL = true;
                            }

                            ContactCacheService.removeContactFromCompany(comp, this);
                            realm.beginTransaction();
                            setCompany(null);
                            realm.commitTransaction();
                            break;
                        }
                    }
                }
            }

            realm.close();

            if (organizationCursor != null)
                organizationCursor.close();
        }
        return companyNewReturn;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        if (company != null) company.trim();
        this.company = company;

    }

    public void addAccountType(String type) {

        if (type == null)
            return;
        accountTypes.add(new ListAdress(type));
        if (type.contains("viber"))
            hasViber = true;
        if (type.contains("whatsapp"))
            hasWhatsapp = true;
        if (type.contains("skype"))
            hasSkype = true;
        if (type.contains("telegram"))
            hasTelegram = true;
        if (type.contains("facebook"))
            hasFacebook = true;
        if (type.contains("valueCopy"))
            hasLinked = true;
        if (type.contains("vkontakte"))
            hasVk = true;
        if (type.contains("instagram"))
            hasInst = true;
        if (type.contains("linkedIn"))
            hasLinked = true;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        if(parcel != null) {
            try {
                parcel.writeByte((byte) (isFullLoaded ? 1 : 0));
                parcel.writeLong(id);
                parcel.writeString(name);
                parcel.writeString(company);
                parcel.writeString(companyPossition);
                parcel.writeString(carrier);
                parcel.writeString(region);
                parcel.writeString(adress);
                parcel.writeString(webSite);
                parcel.writeByte((byte) (hashtag ? 1 : 0));
                parcel.writeByte((byte) (call2me ? 1 : 0));
                parcel.writeByte((byte) (isNew ? 1 : 0));
                parcel.writeByte((byte) (isCreate ? 1 : 0));
                parcel.writeString(time);
                parcel.writeString(idContact);
                parcel.writeString(photoURL);
                parcel.writeInt(color);
                parcel.writeByte((byte) (hasViber ? 1 : 0));
                parcel.writeByte((byte) (hasTelegram ? 1 : 0));
                parcel.writeByte((byte) (hasFacebook ? 1 : 0));
                parcel.writeByte((byte) (hasWhatsapp ? 1 : 0));
                parcel.writeByte((byte) (hasSkype ? 1 : 0));
                parcel.writeByte((byte) (hasLinked ? 1 : 0));
                parcel.writeByte((byte) (hasTwitter ? 1 : 0));
                parcel.writeByte((byte) (hasYoutube ? 1 : 0));
                parcel.writeByte((byte) (hasInst ? 1 : 0));
                parcel.writeByte((byte) (hasVk ? 1 : 0));

                parcel.writeString(shortInto);
            }
            catch (Exception exception) {
                exception.printStackTrace();

            }
        }
    }

    public String getIdContact() {
        return idContact;
    }

    public void setIdContact(String idContact) {
        this.idContact = idContact;
    }

    public boolean isCall2me() {
        return call2me;
    }
}
