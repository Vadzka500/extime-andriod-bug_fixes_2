package ai.extime;

import android.app.Application;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.provider.ContactsContract;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import ai.extime.Models.Contact;
import ai.extime.Models.ContactInfo;
import ai.extime.Models.HashTag;
import ai.extime.Models.SocialModel;
import ai.extime.Services.ContactCacheService;
import ai.extime.Services.ContactsService;
import ai.extime.Utils.ClipboardType;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;

public class SynkFromAddress {

    private ArrayList<Contact> listOfContacts;

    private ArrayList<Boolean> listofCheckContacts;

    private ArrayList<Contact> listOFCompanies;

    private ContactsService contactsService;

    private Application application;

    public SynkFromAddress(ContentResolver contentResolver, Application application) {
        this.listOfContacts = ContactCacheService.getContacts();
        Collections.sort(listOfContacts, (contactFirst, contactSecond) -> contactFirst.getName().compareToIgnoreCase(contactSecond.getName()));
        this.listofCheckContacts = new ArrayList<>();
        for(int i = 0;i<listOfContacts.size();i++)
            this.listofCheckContacts.add(false);
        this.listOFCompanies = ContactCacheService.getCompanies();
        this.contactsService = new ContactsService(contentResolver, false);
        this.application = application;


    }

    public void startSync() {

        Cursor cursorForPrevWithTypes = contactsService.getCursorByType(ContactsService.TYPE_CURSOR.CONTACT_PREV, null);


        Realm realm = Realm.getDefaultInstance(); //-

        int c = 1;

        if (cursorForPrevWithTypes == null){
            realm.close();
            return;
        }

        cursorForPrevWithTypes.moveToNext();

        while (cursorForPrevWithTypes.moveToNext()) {

            if (cursorForPrevWithTypes.getString(cursorForPrevWithTypes.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)) == null
                    || cursorForPrevWithTypes.getString(cursorForPrevWithTypes.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)).trim().equals(""))
                continue;

            c++;

            String contactId = cursorForPrevWithTypes.getString(cursorForPrevWithTypes.getColumnIndex(ContactsContract.Contacts._ID));
            String contactName = cursorForPrevWithTypes.getString(cursorForPrevWithTypes.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            String photoURL = cursorForPrevWithTypes.getString(cursorForPrevWithTypes.getColumnIndex(ContactsContract.Contacts.PHOTO_URI));

            Contact contact = null;
            boolean newContact = false;


            int it = 0;
            for (Contact contact1 : listOfContacts) {
                if ((contactId != null && !contactId.isEmpty() && contact1.getIdContact() != null && contact1.getIdContact().equals(contactId)) || (contact1.getName().equals(contactName.trim()) && !listofCheckContacts.get(it))) {
                    contact = contact1;
                    listofCheckContacts.set(it,true);
                    break;
                }
                it++;
            }

            if (contact == null) {
                contact = new Contact();
                newContact = true;
                contact.setName(contactName.trim());

                Date date = new Date();


                Date lastDate = new Date(date.getTime() - 40 * 24 * 3600 * 1000L);


                Calendar cal = Calendar.getInstance();
                cal.setTime(lastDate);
                Time time = contactsService.getRandomDate();
                time.setHours(cal.get(Calendar.HOUR_OF_DAY));
                time.setMinutes(cal.get(Calendar.MINUTE));
                time.setSeconds(cal.get(Calendar.SECOND));

                //cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 2);

                contact.time = time.toString();

                contact.setDateCreate(lastDate);


            }

            realm.beginTransaction();

            int nameHash = contact.getName().hashCode();
            contact.color = Color.rgb(Math.abs(nameHash * 28439) % 255, Math.abs(nameHash * 211239) % 255, Math.abs(nameHash * 42368) % 255);


            contact.setIdContact(contactId);


            //contact.setPhotoURL(photoURL);

            if (photoURL != null) {
                contact.setPhotoURL(photoURL);
            }

            contact.listOfContactInfo = new RealmList<>();
            contact.accountTypes = new RealmList<>();


            ArrayList<String> phone = contactsService.getContactPhones(contactId);


            if (phone != null && phone.size() > 0) {
                for (int i = 0; i < phone.size(); i++) {
                    //   phone.get(i).trim();
                    contact.addPhone(phone.get(i));
                    contactName = contactName.trim();
                    String phonee = phone.get(i).trim();
                    phonee = phonee.replaceAll(" ", "");
                    phonee = phonee.replaceAll("-", "");
                    if (contactName.contains(phonee) && contactName.length() > phonee.length()) {
                        contactName = contactName.replace(phonee, "");
                        contactName = contactName.trim();
                    }

                }
                //contact.setName(contactName.trim());
            } else {
                contact.addPhone("+000000000000");
                contactsService.addPhoneToContact(contactId, "+000000000000", contactName.trim());

                contact.setName(contactName.trim());

            }


            ArrayList<String> addr = contactsService.getContactEmails(contactId);
            for (int i = 0; i < addr.size(); i++) {
                contact.addEmail(addr.get(i));
            }



            //contact.setListOfHashtags(new RealmList<>());


            RealmList<HashTag> hashList = new RealmList<>();
            ArrayList<String> listOfHash = new ArrayList<>();

            SocialModel socialModel = realm.createObject(SocialModel.class);
            contact.hasFacebook = false;
            contact.hasInst = false;
            contact.hasLinked = false;
            contact.hasVk = false;
            contact.hasSkype = false;
            contact.hasTelegram = false;
            contact.hasViber = false;
            contact.hasWhatsapp = false;
            contact.hasYoutube = false;
            contact.hasTwitter = false;

            Cursor noteCursor = contactsService.getCursorByType(ContactsService.TYPE_CURSOR.NOTE, contactId);

            if (noteCursor != null && noteCursor.getCount() > 0) {
                while (noteCursor.moveToNext()) {
                    String note = noteCursor.getString(noteCursor.getColumnIndex(ContactsContract.CommonDataKinds.Note.NOTE));
                    if (note != null && note.length() > 0) {
                        if (note.charAt(0) == '#' && !listOfHash.contains(note.toLowerCase().trim())) {
                            HashTag hashtag = realm.createObject(HashTag.class);
                            hashtag.setDate(new Date());
                            hashtag.setHashTagValue(note.toLowerCase().trim());
                            //HashTag hashtag = new HashTag(note.toLowerCase().trim());

                            hashList.add(hashtag);
                            listOfHash.add(note.toLowerCase().trim());

                        } else if (ClipboardType.isInsta(note)) {
                            //SocialModel socialModel = contact.getSocialModel();
                            contact.hasInst = true;


                            String username = note;

                            /*if(!username.contains("instagram.com")){
                                username = "https://instagram.com/"+username;
                            }*/

                            //.substring(note.indexOf(".com")+5, note.length());
                                        /*if(username.charAt(username.length()-1) == '/')
                                            username = username.substring(0,username.length()-1);*/

                            if (username.contains("?utm")) {
                                int ind = username.indexOf('?');
                                if (ind != -1)
                                    username = username.substring(0, ind);

                                contactsService.updateNote(contactId, note, username);
                            }

                            if(socialModel != null) {
                                if(socialModel.getInstagramLink() == null || socialModel.getInstagramLink().isEmpty())
                                    socialModel.setInstagramLink(username);
                                else{
                                    if(contact.listOfContactInfo == null){
                                        contact.addNote(note);
                                    }else {
                                        boolean checkS = false;
                                        for (ContactInfo contactInfo : contact.listOfContactInfo){
                                            if(contactInfo.value.equalsIgnoreCase(note)){
                                                checkS = true;
                                                break;
                                            }
                                        }
                                        if(!checkS)
                                            contact.addNote(note);

                                    }
                                }
                            }
                            //contact.setSocialModel(socialModel);


                        } else if (ClipboardType.isVk(note)) {
                            //SocialModel socialModel = contact.getSocialModel();
                            contact.hasVk = true;

                            if(socialModel != null) {
                                if(socialModel.getVkLink() == null || socialModel.getVkLink().isEmpty())
                                    socialModel.setVkLink(note);
                                else{
                                    if(contact.listOfContactInfo == null){
                                        contact.addNote(note);
                                    }else {
                                        boolean checkS = false;
                                        for (ContactInfo contactInfo : contact.listOfContactInfo){
                                            if(contactInfo.value.equalsIgnoreCase(note)){
                                                checkS = true;
                                                break;
                                            }
                                        }
                                        if(!checkS)
                                            contact.addNote(note);

                                    }
                                }
                            }

                        } else if (ClipboardType.isFacebook(note)) {
                            //SocialModel socialModel = contact.getSocialModel();
                            contact.hasFacebook = true;

                            if(socialModel != null) {
                                if(socialModel.getFacebookLink() == null || socialModel.getFacebookLink().isEmpty())
                                    socialModel.setFacebookLink(note);
                                else{
                                    if(contact.listOfContactInfo == null){
                                        contact.addNote(note);
                                    }else {
                                        boolean checkS = false;
                                        for (ContactInfo contactInfo : contact.listOfContactInfo){
                                            if(contactInfo.value.equalsIgnoreCase(note)){
                                                checkS = true;
                                                break;
                                            }
                                        }
                                        if(!checkS)
                                            contact.addNote(note);

                                    }
                                }
                            }

                        } else if (ClipboardType.isLinkedIn(note)) {
                            //SocialModel socialModel = contact.getSocialModel();
                            contact.hasLinked = true;

                            if(socialModel != null) {
                                if(socialModel.getLinkedInLink() == null || socialModel.getLinkedInLink().isEmpty())
                                    socialModel.setLinkedInLink(note);
                                else{
                                    if(contact.listOfContactInfo == null){
                                        contact.addNote(note);
                                    }else {
                                        boolean checkS = false;
                                        for (ContactInfo contactInfo : contact.listOfContactInfo){
                                            if(contactInfo.value.equalsIgnoreCase(note)){
                                                checkS = true;
                                                break;
                                            }
                                        }
                                        if(!checkS)
                                            contact.addNote(note);

                                    }
                                }
                            }

                        } else if (note.contains("viber.com") || note.contains("https://www.viber.com")) {
                            //SocialModel socialModel = contact.getSocialModel();
                            contact.hasViber = true;

                            socialModel.setViberLink(note);

                        } else if (note.toString().contains("whatsapp.com") || note.toString().contains("https://www.whatsapp.com")) {
                            //SocialModel socialModel = contact.getSocialModel();
                            contact.hasWhatsapp = true;

                            socialModel.setWhatsappLink(note);

                        } else if (ClipboardType.isTelegram(note)) {
                            //SocialModel socialModel = contact.getSocialModel();
                            contact.hasTelegram = true;

                            socialModel.setTelegramLink(note);

                        } else if (note.toString().contains("skype.com") || note.toString().contains("https://www.skype.com")) {
                            //SocialModel socialModel = contact.getSocialModel();
                            contact.hasSkype = true;
                            String username = note.substring(note.indexOf(".com") + 5, note.length());
                            if (username.charAt(username.length() - 1) == '/')
                                username = username.substring(0, username.length() - 1);

                            socialModel.setSkypeLink(username);

                        } else if(ClipboardType.isYoutube(note)){
                            contact.hasYoutube = true;

                            if(socialModel != null) {
                                if(socialModel.getYoutubeLink() == null || socialModel.getYoutubeLink().isEmpty())
                                    socialModel.setYoutubeLink(note);
                                else{
                                    if(contact.listOfContactInfo == null){
                                        contact.addNote(note);
                                    }else {
                                        boolean checkS = false;
                                        for (ContactInfo contactInfo : contact.listOfContactInfo){
                                            if(contactInfo.value.equalsIgnoreCase(note)){
                                                checkS = true;
                                                break;
                                            }
                                        }
                                        if(!checkS)
                                            contact.addNote(note);

                                    }
                                }
                            }
                        }else if(ClipboardType.isTwitter(note)) {
                            contact.hasTwitter = true;

                            if(socialModel != null) {
                                if(socialModel.getTwitterLink() == null || socialModel.getTwitterLink().isEmpty())
                                    socialModel.setTwitterLink(note);
                                else{
                                    if(contact.listOfContactInfo == null){
                                        contact.addNote(note);
                                    }else {
                                        boolean checkS = false;
                                        for (ContactInfo contactInfo : contact.listOfContactInfo){
                                            if(contactInfo.value.equalsIgnoreCase(note)){
                                                checkS = true;
                                                break;
                                            }
                                        }
                                        if(!checkS)
                                            contact.addNote(note);

                                    }
                                }
                            }
                        }else{
                            contact.addNote(note);
                        }
                    }
                }
            }
            if (noteCursor != null)
                noteCursor.close();


            if(contact.getListOfHashtags() != null && !contact.getListOfHashtags().isEmpty()){
                for(HashTag hashTag : contact.getListOfHashtags()){
                    boolean checkHash = false;
                    for(HashTag hashNew : hashList){
                        if(hashTag.getHashTagValue().equalsIgnoreCase(hashNew.getHashTagValue())) checkHash = true;
                    }
                    if(!checkHash){
                        contactsService.addNoteToContact(contact.getIdContact(), hashTag.getHashTagValue(), contact.getName());
                    }
                }

                for(HashTag hashTagNew : hashList){
                    boolean checkHash = false;
                    for(HashTag hashTag : contact.getListOfHashtags()){
                        if(hashTag.getHashTagValue().equalsIgnoreCase(hashTagNew.getHashTagValue())) checkHash = true;
                    }
                    if(!checkHash){
                        contact.getListOfHashtags().add(hashTagNew);
                    }

                }

            }else
                contact.setListOfHashtags(hashList);

            contact.setSocialModel(socialModel);


            Cursor accountCursor = contactsService.getCursorByType(ContactsService.TYPE_CURSOR.ACCOUNT_TYPE, contactId);

            if (accountCursor != null && accountCursor.getCount() > 0) {

                while (accountCursor.moveToNext()) {
                    int accountTypeI = accountCursor.getColumnIndex(ContactsContract.RawContacts.ACCOUNT_TYPE);

                    if (accountTypeI != -1) {
                        String accountType = accountCursor.getString(accountTypeI);
                        if (accountType != null && accountType.contains("telegram") && phone.size() == 0) {

                        } else
                            contact.addAccountType(accountType);

                        if (accountType != null) {


                            if (accountType.contains("viber") && phone.size() > 0) {
                                contact.hasViber = true;
                                //SocialModel sc = contact.getSocialModel();

                                socialModel.setViberLink(phone.get(0));

                            }


                            if (accountType.contains("whatsapp") && phone.size() > 0) {
                                contact.hasWhatsapp = true;

                                socialModel.setWhatsappLink(phone.get(0));

                            }


                            if (accountType.contains("telegram") && phone.size() > 0) {


                                if (phone.size() != 0 && !phone.get(0).equals("+000000000000")) {
                                    contact.hasTelegram = true;

                                    socialModel.setTelegramLink(phone.get(0));
                                }
                            }
                        }
                    }
                }
                accountCursor.close();
            } else
                contact.addAccountType("phone");


            Cursor adressCursor = contactsService.getCursorByType(ContactsService.TYPE_CURSOR.CONTACT_ADRESS, contactId);
            while (adressCursor != null && adressCursor.moveToNext()) {
                String adress = adressCursor.getString(adressCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
                contact.addAddress(adress);
            }

            if (adressCursor != null)
                adressCursor.close();

            realm.commitTransaction();

            boolean found = false;

            //    RealmList<ContactInfo> ctinf = new RealmList<>();
            //boolean checkC = false;

            boolean checkCompany = true;


            Cursor organizationCursor = contactsService.getCursorByType(ContactsService.TYPE_CURSOR.ORGANIZATION, contactId);

            while (organizationCursor != null && organizationCursor.moveToNext()) {
                String orgName = organizationCursor.getString(organizationCursor.getColumnIndex(ContactsContract.CommonDataKinds.Organization.DATA));
                String companyPossition = organizationCursor.getString(organizationCursor.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TITLE));



                realm.beginTransaction();
                //if (companyPossition != null)
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
                        // if (searchCompanyContact.listOfContacts != null) {
                        if (searchCompanyContact.isValid() && searchCompanyContact.getName().equalsIgnoreCase(orgName.trim())) {

                            //checkC = true;

                            //try {

                            if (contact.getId() == 0 && newContact) {
                                ContactCacheService.justInsertContact(contact);
                                contact = ContactCacheService.getContactById(contact.getId());
                                listOfContacts.add(contact);
                                listofCheckContacts.add(true);

                            }
                                      /*  else
                                            ContactCacheService.updateContact(contact,null);*/

                            //   Contact contact1 = ContactCacheService.getContactByName(contact.getName());
                            if (newContact) {
                                if (!searchCompanyContact.listOfContacts.contains(contact)) {
                                    realm.beginTransaction();
                                    searchCompanyContact.listOfContacts.add(contact);
                                    realm.commitTransaction();
                                }
                            } else {
                                //if(!searchCompanyContact.listOfContacts.contains(contact))
                                if (oldCompany != null) {
                                    /*realm.beginTransaction();
                                    searchCompanyContact.listOfContacts.add(contact);
                                    realm.commitTransaction();*/

                                    for (Contact comp : listOFCompanies) {
                                        if (comp.isValid() && comp.getName().equalsIgnoreCase(oldCompany.trim())) {
                                            boolean checkRemove = ContactCacheService.removeContactFromCompany(comp, contact);
                                            if (checkRemove) {
                                                Intent responseIntent = new Intent();
                                                responseIntent.setAction("finishSyncUpdate");
                                                responseIntent.addCategory(Intent.CATEGORY_DEFAULT);
                                                application.sendBroadcast(responseIntent);
                                            }
                                            break;
                                        }
                                    }
                                }

                                if (!searchCompanyContact.listOfContacts.contains(contact)) {
                                    if(searchCompanyContact.getName().equals("1337")){

                                    }

                                    realm.beginTransaction();
                                    searchCompanyContact.listOfContacts.add(contact);
                                    realm.commitTransaction();
                                }

                            }
                                        /*if(!searchCompanyContact.listOfContacts.contains(contact))
                                            searchCompanyContact.listOfContacts.add(contact);*/

                            //ContactCacheService.updateCompany(searchCompanyContact);
                                    /*}catch (Exception e){

                                        e.printStackTrace();
                                    }*/

                            found = true;
                        }
                        //    }
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
                        // Contact companyContact = new Contact(new Date());
                        //  companyContact.setName(orgName);

                        companyContact.time = contact.time.toString();
                        //     companyContact.time = "";
                        //companyContact.setIdContact(contactId);
                        companyContact.color = Color.rgb(Math.abs(nameHash * 28439) % 255, Math.abs(nameHash * 211239) % 255, Math.abs(nameHash * 42368) % 255);
                        //  companyContact.listOfContacts = new RealmList<>();
                        //    companyContact.listOfContacts.add(contact);
                        //    listOfContacts.add(companyContact);
                        //      countConpany++;
                        //MainActivity.mainCountCompanuFromLoad++;
                        checkCompany = false;
                        //checkC = true;
                                    /*new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            *//*
                                            ContactCacheService.insertOneContact(contact);
                                            companyContact.listOfContacts.add(contact);
                                            listOfContacts.add(companyContact);
                                            ContactCacheService.updateCompany(companyContact);*//*


                                        }
                                    }).start();*/



                        if (newContact) {
                            ContactCacheService.justInsertContact(contact);
                            contact = ContactCacheService.getContactById(contact.getId());
                            listOfContacts.add(contact);
                            listofCheckContacts.add(true);
                        }else if (oldCompany != null) {
                                    /*realm.beginTransaction();
                                    searchCompanyContact.listOfContacts.add(contact);
                                    realm.commitTransaction();*/

                            for (Contact comp : listOFCompanies) {
                                if (comp.isValid() && comp.getName().equalsIgnoreCase(oldCompany.trim())) {
                                    boolean checkRemove = ContactCacheService.removeContactFromCompany(comp, contact);
                                    if (checkRemove) {
                                        Intent responseIntent = new Intent();
                                        responseIntent.setAction("finishSyncUpdate");
                                        responseIntent.addCategory(Intent.CATEGORY_DEFAULT);
                                        application.sendBroadcast(responseIntent);
                                    }
                                    break;
                                }
                            }
                        }





                        companyContact.listOfContacts.add(contact);


                        //listOfContacts.add(companyContact);

                        ContactCacheService.updateCompany(companyContact);

                        listOFCompanies.add(ContactCacheService.getCompany(companyContact.getName()));


                    }
                }
            }

            /*if (checkCompany)
                countContact++;*/



            boolean checkNull = false;
            if(organizationCursor != null && organizationCursor.getCount() > 0) {
                organizationCursor.moveToFirst();
                do{
                    if(organizationCursor.getString(organizationCursor.getColumnIndex(ContactsContract.CommonDataKinds.Organization.DATA)) != null && !organizationCursor.getString(organizationCursor.getColumnIndex(ContactsContract.CommonDataKinds.Organization.DATA)).equals("")){

                        checkNull = true;
                        break;
                    }
                }while (organizationCursor.moveToNext());

            }


            if (!checkNull) {


                if (contact.getCompany() == null && newContact) {

                    ContactCacheService.justInsertContact(contact);


                    contact = ContactCacheService.getContactById(contact.getId());
                    listOfContacts.add(contact);
                    listofCheckContacts.add(true);

                }

                if (contact.getCompany() != null) {

                    for (Contact comp : listOFCompanies) {
                        if (comp.isValid() && comp.getName().equalsIgnoreCase(contact.getCompany())) {
                            boolean checkR = ContactCacheService.removeContactFromCompany(comp, contact);
                            realm.beginTransaction();
                            contact.setCompany(null);
                            realm.commitTransaction();

                            if (checkR) {
                                Intent responseIntent = new Intent();
                                responseIntent.setAction("finishSyncUpdate");
                                responseIntent.addCategory(Intent.CATEGORY_DEFAULT);
                                application.sendBroadcast(responseIntent);
                            }

                            break;
                        }
                    }
                }
            }


            if (organizationCursor != null)
                organizationCursor.close();


            Intent responseIntent = new Intent();
            responseIntent.setAction("finishSyncUpdateCount");
            responseIntent.addCategory(Intent.CATEGORY_DEFAULT);
            responseIntent.putExtra("update", c);
            responseIntent.putExtra("updateAll", cursorForPrevWithTypes.getCount());

            application.sendBroadcast(responseIntent);


        }

        realm.close();

        Intent responseIntent = new Intent();
        responseIntent.setAction("finishSyncUpdateCount");
        responseIntent.addCategory(Intent.CATEGORY_DEFAULT);
        responseIntent.putExtra("update", -1);
        responseIntent.putExtra("updateAll", cursorForPrevWithTypes.getCount());

        application.sendBroadcast(responseIntent);


        cursorForPrevWithTypes.close();


    }

}
