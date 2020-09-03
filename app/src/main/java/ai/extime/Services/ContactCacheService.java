package ai.extime.Services;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.util.Patterns;
import android.view.View;

import com.extime.R;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import ai.extime.Interfaces.Postman;
import ai.extime.Models.AdapterScrollModel;
import ai.extime.Utils.ClipboardType;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.exceptions.RealmPrimaryKeyConstraintException;
import ai.extime.Activity.MainActivity;
import ai.extime.Enums.ClipboardEnum;
import ai.extime.Models.Contact;
import ai.extime.Models.ContactInfo;


public class ContactCacheService {

    private final static String CONTACTS_PREFERENCES = "Call2MePref";
    private static final String FIRST_RUN = "firstRun";
    private final static String CONTACTS = "contacts";
    private final static String CLIPBOARD = "clipboard";

    public static Realm realmContactService;

    public static void initRealm(){
        realmContactService = Realm.getDefaultInstance();
    }


    public static void insertOneContactFromFile(Contact c) {


        Realm realm = Realm.getDefaultInstance(); //+
        try {
            boolean che = true;
            realm.beginTransaction();
            Contact contact = realm.where(Contact.class).equalTo("name", c.getName()).findFirst();
            long res = realm.where(Contact.class).count();
            //  long q;
            if (res == 0)
                che = false;

            //    q = (long) realm.where(Contact.class).max("id");
            if (contact == null) {

                Contact contact1 = c;
                long id;
                //  contact1 = c;
                if (che) {
                    id = (long) realm.where(Contact.class).max("id") + 1;
                    contact1.setId(id);
                } else {
                    id = 1;
                    contact1.setId(id);

                }

                realm.insertOrUpdate(contact1);
            } else {

                long id = contact.getId();
                contact = c;
                try {
                    contact.setId(id);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                realm.insertOrUpdate(contact);
            }
            realm.commitTransaction();
            realm.close();

        } catch (Exception e) {
            System.out.println("ERROR UPDATE " + e.fillInStackTrace());
            realm.commitTransaction();
            realm.close();
        }
    }


    public static void justInsertContact(Contact c) {

        Realm realm = Realm.getDefaultInstance(); //+
        realm.beginTransaction();
        long id = 1;
        try {
            id = (long) realm.where(Contact.class).max("id") + 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        c.setId(id);
        try {
            realm.insert(c);
        } catch (RealmPrimaryKeyConstraintException e) {
            realm.commitTransaction();
            realm.close();

            justInsertContact(c);
        }
        realm.commitTransaction();
        realm.close();

    }


    public static void justInsertContactNewSynk(Contact c, Realm realm) {

        //Realm realm = Realm.getDefaultInstance(); //+
        realm.beginTransaction();
        long id = 1;
        try {
            id = (long) realm.where(Contact.class).max("id") + 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        c.setId(id);
        try {
            realm.insert(c);
        } catch (RealmPrimaryKeyConstraintException e) {
            realm.commitTransaction();
            //realm.close();

            justInsertContactNewSynk(c, realm);
        }
        realm.commitTransaction();
        //realm.close();

    }

    public static boolean checkNewContact(String id, Realm realm) {


        //Realm realm = Realm.getDefaultInstance(); //+
        long c = realm.where(Contact.class).equalTo("idContact", id).count();
        //realm.close();
        if (c == 0) return false;
        else return true;
    }

    public static boolean checkNewContactByName(String name, Realm realm) {
        //Realm realm = Realm.getDefaultInstance(); //+
        long c = realm.where(Contact.class).equalTo("name", name).count();
        //realm.close();
        if (c == 0) return false;
        else return true;
    }


    public static void insertOneContact(Contact c) {

        Realm realm = Realm.getDefaultInstance(); //+
        try {
            boolean che = true;
            realm.beginTransaction();

            Contact contact = realm.where(Contact.class).equalTo("name", c.getName()).isEmpty("listOfContacts").findFirst();

            RealmResults<Contact> res = realm.where(Contact.class).findAll();
            //  long q;
            if (res.size() == 0 || res == null)
                che = false;

            //    q = (long) realm.where(Contact.class).max("id");
            if (contact == null) {

                Contact contact1 = c;
                //  contact1 = c;
                long id;
                if (che) {
                    id = (long) realm.where(Contact.class).max("id") + 1;
                    contact1.setId(id);
                    //  System.out.println("new id = "+id);
                } else {
                    id = 1;
                    contact1.setId(id);
                }


                realm.insertOrUpdate(contact1);
                Contact con = realm.where(Contact.class).equalTo("id", id).findFirst();
                MainActivity.LIST_TO_SAVE_OF_FILE.add(realm.copyFromRealm(con));

                //    MainActivity.counter++;

            } else {

                long id = contact.getId();
                contact = c;
                contact.setId(id);
                realm.insertOrUpdate(contact);


                Contact con = realm.where(Contact.class).equalTo("id", id).findFirst();
                try {
                    MainActivity.LIST_TO_SAVE_OF_FILE.set((int) (id - 1), realm.copyFromRealm(con));
                } catch (Exception e) {
                    e.printStackTrace();

                }


            }
            realm.commitTransaction();
            realm.close();

        } catch (Exception e) {
            System.out.println("ERROR UPDATE " + e.fillInStackTrace());
            e.printStackTrace();
            realm.commitTransaction();
            realm.close();
        }
    }

    public static ArrayList<Contact> getCompanies() {
        //Realm realm = Realm.getDefaultInstance();
        return new ArrayList<>(realmContactService.where(Contact.class).isNotEmpty("listOfContacts").findAll());
    }

    public static ArrayList<Contact> getCompaniesRealm(Realm realm) {
        //Realm realm = Realm.getDefaultInstance();
        return new ArrayList<>(realm.where(Contact.class).isNotEmpty("listOfContacts").findAll());
    }

    public static ArrayList<Contact> getFavorite() {
        //Realm realm = Realm.getDefaultInstance();
        return new ArrayList<>(realmContactService.where(Contact.class).equalTo("isFavorite", true).findAll());
    }

    public static ArrayList<Contact> getCrown() {
        //Realm realm = Realm.getDefaultInstance();
        return new ArrayList<>(realmContactService.where(Contact.class).equalTo("isCrown", true).findAll());
    }

    public static ArrayList<Contact> getVip() {
        //Realm realm = Realm.getDefaultInstance();
        return new ArrayList<>(realmContactService.where(Contact.class).equalTo("isVip", true).findAll());
    }

    public static ArrayList<Contact> getStartup() {
        //Realm realm = Realm.getDefaultInstance();
        return new ArrayList<>(realmContactService.where(Contact.class).equalTo("isStartup", true).findAll());
    }

    public static ArrayList<Contact> getInvestor() {
        //Realm realm = Realm.getDefaultInstance();
        return new ArrayList<>(realmContactService.where(Contact.class).equalTo("isInvestor", true).findAll());
    }

    public static ArrayList<Contact> getImportant() {
        //Realm realm = Realm.getDefaultInstance();
        return new ArrayList<>(realmContactService.where(Contact.class).equalTo("isImportant", true).findAll());
    }

    public static ArrayList<Contact> getFinished() {
        //Realm realm = Realm.getDefaultInstance();
        return new ArrayList<>(realmContactService.where(Contact.class).equalTo("isFinished", true).findAll());
    }

    public static ArrayList<Contact> getPaused() {
        //Realm realm = Realm.getDefaultInstance();
        return new ArrayList<>(realmContactService.where(Contact.class).equalTo("isPause", true).findAll());
    }

    public static ArrayList<Contact> getContacts() {

        //Realm realm = Realm.getDefaultInstance();
        ArrayList<Contact> contacts = new ArrayList<>(realmContactService.where(Contact.class).isEmpty("listOfContacts").findAll());

        return contacts;
    }


    public static void updateCacheContacts(ArrayList<Contact> contactsMain, Context context) {
        System.out.println("UPDATE CONTACT");
        Realm realm = Realm.getDefaultInstance(); //-
        try {

            for (int i = 0; i < contactsMain.size(); i++) {
                try {
                    realm.beginTransaction();
                    Contact contact = realm.where(Contact.class).equalTo("name", contactsMain.get(i).getName()).findFirst();
                    RealmResults<Contact> res = realm.where(Contact.class).findAll();
                    long q;
                    if (res.size() == 0 || res == null)
                        q = 1;
                    else
                        q = (long) realm.where(Contact.class).max("id");
                    if (contact == null) {
                        Contact contact1 = new Contact();
                        contact1 = contactsMain.get(i);
                        contact1.setId(q + 1);
                        realm.insertOrUpdate(contact1);
                    } else {
                        contact = contactsMain.get(i);
                        realm.insertOrUpdate(contact);
                    }
                    realm.commitTransaction();
                } catch (Exception e) {
                    System.out.println("ERROR UPDATE " + e.fillInStackTrace());
                    realm.commitTransaction();
                }
            }
            realm.close();
        } catch (Exception exc) {
            System.out.println("MAIN UPDATE ERROR " + exc.fillInStackTrace());
            realm.close();
        }
        System.out.println("END UPDATE");

    }


    public static long getCountContacts() {
        Realm realm = Realm.getDefaultInstance(); //+
        long count = realm.where(Contact.class).count();
        realm.close();
        return count;
    }

    public static int getOnlyContacts() {

        Realm realm = Realm.getDefaultInstance(); //+
        //realm.beginTransaction();

        int count = (int) realm.where(Contact.class).isEmpty("listOfContacts").count();

        //realm.commitTransaction();
        realm.close();
        return count;
    }

    public static long getCountFavorite() {
        Realm realm = Realm.getDefaultInstance(); //+
        //realm.beginTransaction();
        long count = realm.where(Contact.class).equalTo("isFavorite", true).count();
        //realm.commitTransaction();
        realm.close();
        return count;
    }

    public static long getCountImportant() {
        Realm realm = Realm.getDefaultInstance(); //+
        //realm.beginTransaction();
        long count = realm.where(Contact.class).equalTo("isImportant", true).count();
        //realm.commitTransaction();
        realm.close();
        return count;
    }

    public static long getCountFinished() {
        Realm realm = Realm.getDefaultInstance(); //+
        //realm.beginTransaction();
        long count = realm.where(Contact.class).equalTo("isFinished", true).count();
        //realm.commitTransaction();
        realm.close();
        return count;
    }

    public static long getCountPause() {
        Realm realm = Realm.getDefaultInstance(); //+
        long count = realm.where(Contact.class).equalTo("isPause", true).count();
        realm.close();
        return count;
    }

    public static long getCountCrown() {
        Realm realm = Realm.getDefaultInstance(); //+
        long count = realm.where(Contact.class).equalTo("isCrown", true).count();
        realm.close();
        return count;
    }

    public static long getCountVip() {
        Realm realm = Realm.getDefaultInstance(); //+
        long count = realm.where(Contact.class).equalTo("isVip", true).count();
        realm.close();
        return count;
    }

    public static long getCountSturtup() {
        Realm realm = Realm.getDefaultInstance(); //+
        long count = realm.where(Contact.class).equalTo("isStartup", true).count();
        realm.close();
        return count;
    }

    public static long getCountInvestor() {
        Realm realm = Realm.getDefaultInstance(); //+
        long count = realm.where(Contact.class).equalTo("isInvestor", true).count();
        realm.close();
        return count;
    }


    public static int getOnlyCompanySize() {

        Realm realm = Realm.getDefaultInstance(); //+
        //realm.beginTransaction();
        int count = realm.where(Contact.class).isNotEmpty("listOfContacts").findAll().size();
        //realm.commitTransaction();
        realm.close();

        return count;
    }

    public static void setPrimaryInfo(ContactInfo contactInfo, boolean primary){
        Realm realm = Realm.getDefaultInstance(); //+
        realm.beginTransaction();
        contactInfo.isPrimary = primary;
        realm.commitTransaction();
        realm.close();

    }

    public static ArrayList<Contact> getAllContacts(Context context) {


        //Realm realm = Realm.getDefaultInstance(); //+
        //realm.beginTransaction();
        if(realmContactService.isClosed()) realmContactService = Realm.getDefaultInstance();

        ArrayList<Contact> contacts = new ArrayList<Contact>(realmContactService.where(Contact.class).findAll());

        //realm.commitTransaction();

        if (contacts.size() == 0) {
            //realm.close();
            return null;
        } else {
            return contacts;
        }
    }

    public static ArrayList<Contact> getListWithEmail(){
        ArrayList<Contact> contacts = getAllContacts(null);
        ArrayList<Contact> listWithEmail = new ArrayList<>();
        if (contacts != null) {
            for(Contact ct : contacts){
                if(ct != null && ct.isValid() && ct.getListOfContactInfo() != null){
                    for(ContactInfo i : ct.getListOfContactInfo()){
                        if(ClipboardType.isEmail(i.value)){
                            listWithEmail.add(ct);
                            break;
                        }
                    }
                }
            }
        }
        return  listWithEmail;
    }

    public static ArrayList<Contact> getAllContactsRealm(Context context, Realm realm) {


        //Realm realm = Realm.getDefaultInstance(); //+
        //realm.beginTransaction();
        ArrayList<Contact> contacts = new ArrayList<Contact>(realm.where(Contact.class).findAll());

        //realm.commitTransaction();

        if (contacts.size() == 0) {
            //realm.close();
            return null;
        } else {
            return contacts;
        }
    }

    public static void updateContact(Contact contact, Context context) {

        Realm realm = Realm.getDefaultInstance(); //+
        realm.beginTransaction();
        realm.insertOrUpdate(contact);
        realm.commitTransaction();
        realm.close();
    }

    public static void updateCompany(Contact contact) {

        Realm realm = Realm.getDefaultInstance(); //+

        realm.beginTransaction();
        boolean che = true;
        Contact company = realm.where(Contact.class).equalTo("name", contact.getName()).isNotEmpty("listOfContacts").findFirst();
        RealmResults<Contact> res = realm.where(Contact.class).findAll();
        if (res.size() == 0 || res == null)
            che = false;

        if (company == null) {
            MainActivity.mainCountCompanuFromLoad++;

            Contact contact1 = contact;
            long id;
            if (che) {
                id = (long) realm.where(Contact.class).max("id") + 1;
                contact1.setId(id);
                contact.setId(id);

            } else {
                id = 1;
                contact1.setId(id);
                contact.setId(id);
            }



            realm.insertOrUpdate(contact1);
            //Contact con = realm.where(Contact.class).equalTo("id",id).findFirst();
            //if(MainActivity.upd == 1)
            //MainActivity.LIST_TO_SAVE_OF_FILE.add(realm.copyFromRealm(con));
        } else {


            long id = company.getId();
            company = contact;

            try {
                company.setId(id);
            } catch (Exception e) {

            }
            realm.insertOrUpdate(company);
            //Contact con = realm.where(Contact.class).equalTo("id",id).findFirst();
            //if(MainActivity.upd == 1)
            //MainActivity.LIST_TO_SAVE_OF_FILE.set((int) (id-1),realm.copyFromRealm(con));
        }

        realm.commitTransaction();
        realm.close();

    }

    public static void updateCompany2(Contact contact) {
        Realm realm = Realm.getDefaultInstance(); //+
        realm.beginTransaction();
        realm.insertOrUpdate(contact);
        realm.commitTransaction();
        realm.close();
    }

    public static Contact getCompany(String name) {
        //Realm realm = Realm.getDefaultInstance();

        //realm.beginTransaction();
        Contact contact = realmContactService.where(Contact.class).equalTo("name", name).isNotEmpty("listOfContacts").findFirst();

        //realm.commitTransaction();

        //  realm.close();
        return contact;

    }

    public static Contact getContactByIDContact(Realm realm, String id) {
        //Realm realm = Realm.getDefaultInstance();
        //realm.beginTransaction();
        Contact contact = realm.where(Contact.class).equalTo("idContact", id).isEmpty("listOfContacts").findFirst();
        //realm.close();
        //realm.commitTransaction();
        return contact;

    }

    public static Contact getContactByName(String name) {
        Realm realm = Realm.getDefaultInstance();  //-

        realm.beginTransaction();
        Contact contact = realm.where(Contact.class).equalTo("name", name).isEmpty("listOfContacts").findFirst();

        realm.commitTransaction();

        //  realm.close();
        return contact;

    }

    public static long getListConatctWithHashtagCount() {
        Realm realm = Realm.getDefaultInstance(); //+

        realm.beginTransaction();
        long result = realm.where(Contact.class).isNotEmpty("listOfHashtags").count();
        realm.commitTransaction();
        realm.close();
        return result;
    }

    public static boolean removeContactFromCompany(Contact company, Contact contact) {
        Realm realm = Realm.getDefaultInstance(); //+

        realm.beginTransaction();
        Contact comp;
        try {
            comp = realm.where(Contact.class).equalTo("name", company.getName()).isNotEmpty("listOfContacts").findFirst();
        }catch (NullPointerException e){
            comp = realm.where(Contact.class).equalTo("id", company.getId()).findFirst();
        }


        for (int i = 0; i < comp.listOfContacts.size(); i++) {
            if (comp.listOfContacts.get(i).getId() == contact.getId()) {
                if (comp.listOfContacts.size() == 1) {
                    comp.deleteFromRealm();
                    realm.commitTransaction();
                    realm.close();
                    return true;

                } else if (comp.listOfContacts.size() > 1) {

                    comp.listOfContacts.remove(i);

                }
            }
        }


        realm.commitTransaction();
        realm.close();
        return false;

    }


    public static Contact getContactById(long id) {
        //Realm realm = Realm.getDefaultInstance();
        return realmContactService.where(Contact.class).equalTo("id", id).findFirst();
    }

    public static Contact getContactByIdSynk(long id, Realm realm) {
        //Realm realm = Realm.getDefaultInstance();
        return realm.where(Contact.class).equalTo("id", id).findFirst();
    }

    public static Contact getContactByIdAndWithoutRealm(long id) {
        Realm realm = Realm.getDefaultInstance(); //+

        if (realm.isInTransaction()) realm.commitTransaction();
        realm.beginTransaction();
        Contact ct = realm.where(Contact.class).equalTo("id", id).findFirst();
        if (ct != null)
            ct = realm.copyFromRealm(ct);

        realm.commitTransaction();
        realm.close();
        return ct;

    }


    public static void removeContact(Contact contact) {
        Realm realm = Realm.getDefaultInstance(); //+
        Contact c = realm.where(Contact.class).equalTo("id", contact.getId()).findFirst();
        realm.beginTransaction();
        try {
            if (c != null) {
                c.deleteFromRealm();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        realm.commitTransaction();
        realm.close();

    }


    public static void removeContactById(Contact contact) {
        Realm realm = Realm.getDefaultInstance(); //+
        Contact c = realm.where(Contact.class).equalTo("id", contact.getId()).findFirst();
        realm.beginTransaction();
        try {

            c.deleteFromRealm();
        } catch (Exception e) {
            e.printStackTrace();
        }
        realm.commitTransaction();
        realm.close();

    }

    public static ArrayList<String> getPossitionContacts() {
        Realm realm = Realm.getDefaultInstance(); //+

        RealmResults<Contact> list = realm.where(Contact.class).distinct("companyPossition").isNotNull("companyPossition").isNotEmpty("companyPossition").findAll();
        ArrayList<String> l = new ArrayList<>();
        for (Contact contact : list) {
            if (!l.contains(contact.getCompanyPossition().toString().trim()))
                l.add(contact.getCompanyPossition().trim());
        }
        realm.close();

        return l;
    }

    public static boolean checkPhoneNumber(String phone) {

        Realm realm = Realm.getDefaultInstance(); //+

        RealmResults<Contact> list = realm.where(Contact.class).equalTo("listOfContactInfo.type", "phone").or().equalTo("listOfContactInfo.typeData", "phone").findAll();
        boolean r = false;
        if (list != null) {

            String str2 = phone;
            str2 = str2.replaceAll("[\\.\\s\\-\\+\\(\\)]", "");
            if (str2.length() == 0) return true;
            if ((str2.charAt(0) == '7' || str2.charAt(0) == '8') && str2.length() == 11) {
                str2 = str2.substring(1);
            }

            for (Contact c : list) {
                boolean checkF = false;
                for (ContactInfo info : c.getListOfContactInfo()) {
                    if ((info.type.equalsIgnoreCase("phone") || (info.typeData != null && info.typeData.equalsIgnoreCase("phone"))) /*&& !info.value.equals("+000000000000")*/) {
                        String str = info.value;


                        str = str.replaceAll("[\\.\\s\\-\\+\\(\\)]", "");

                        if ((str.charAt(0) == '7' || str.charAt(0) == '8') && str.length() == 11) {
                            str = str.substring(1);
                        }


                        if (str.equalsIgnoreCase(str2)) {
                            r = true;
                            checkF = true;
                            break;
                        }

                    }
                }
                if (checkF) break;
            }

        }

        realm.close();

        return r;
    }

    public static boolean searchByPhoneNew(String phone, Realm realm) {
        //Realm realm = Realm.getDefaultInstance(); //+
        long count = realm.where(Contact.class).equalTo("listOfContactInfo.value", phone, Case.INSENSITIVE).count();
        //realm.close();
        if (count > 0) return true;
        else return false;
    }


    public static boolean checkPhoneNumberById(String phone, String id) {

        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RealmResults<Contact> list = realm.where(Contact.class).equalTo("listOfContactInfo.type", "phone").or().equalTo("listOfContactInfo.typeData", "phone").findAll();
        boolean r = false;
        if (list != null) {
            for (Contact c : list) {
                boolean checkF = false;
                for (ContactInfo info : c.getListOfContactInfo()) {
                    if ((info.type.equalsIgnoreCase("phone") || (info.typeData != null && info.typeData.equalsIgnoreCase("phone"))) /*&& !info.value.equals("+000000000000")*/) {
                        String str = info.value;
                        String str2 = phone;

                        str = str.replaceAll("[\\.\\s\\-\\+\\(\\)]", "");
                        str2 = str2.replaceAll("[\\.\\s\\-\\+\\(\\)]", "");
                        if ((str.charAt(0) == '7' || str.charAt(0) == '8') && str.length() == 11) {
                            str = str.substring(1);
                        }
                        if ((str2.charAt(0) == '7' || str2.charAt(0) == '8') && str2.length() == 11) {
                            str2 = str2.substring(1);
                        }

                        if (str.equalsIgnoreCase(str2)) {
                            r = true;
                            checkF = true;
                            break;
                        }

                    }
                }
                if (checkF) break;
            }

        }
        realm.commitTransaction();
        realm.close();

        return r;
    }

    public static Contact find2(String link, ClipboardEnum clipboardEnum, Activity activity) {

        Realm realm = Realm.getDefaultInstance(); //+


        //realm.beginTransaction();
        Contact contact = null;

        if (clipboardEnum.equals(ClipboardEnum.FACEBOOK)) {
            String str = link;

            int ind1 = str.indexOf(".com/");
            if (ind1 != -1) str = str.substring(ind1 + 5);

            if (str.charAt(str.length() - 1) == '/') {
                str = str.substring(0, str.length() - 1);
            }

            RealmResults<Contact> ll = realm.where(Contact.class).contains("socialModel.facebookLink", str, Case.INSENSITIVE).findAll();

            if(ll != null && !ll.isEmpty()){
                for(Contact c : ll){
                    if(c != null && c.getSocialModel() != null && c.getSocialModel().getFacebookLink() != null){

                            String ss = c.getSocialModel().getFacebookLink();

                            int ind2 = ss.indexOf(".com/");
                            if (ind2 != -1) ss = ss.substring(ind2 + 5);

                            if (ss.charAt(ss.length() - 1) == '/') {
                                ss = ss.substring(0, ss.length() - 1);
                            }

                            if(ss.equalsIgnoreCase(str)){
                                contact = c;
                                break;
                            }
                    }
                }
            }

            if (contact == null) {

                RealmResults<Contact> list = realm.where(Contact.class).isEmpty("listOfContacts").contains("listOfContactInfo.value", str, Case.INSENSITIVE).equalTo("listOfContactInfo.type", "note").findAll();
                for (Contact c : list) {
                    boolean checkF = false;
                    if (c.listOfContactInfo != null) {
                        for (ContactInfo ci : c.listOfContactInfo) {
                            if (ci.value.toLowerCase().contains(str.toLowerCase()) && ClipboardType.isFacebook(ci.value)) {

                                String ss = ci.value;

                                int ind2 = ss.indexOf(".com/");
                                if (ind2 != -1) ss = ss.substring(ind2 + 5);

                                if (ss.charAt(ss.length() - 1) == '/') {
                                    ss = ss.substring(0, ss.length() - 1);
                                }

                                if(ss.equalsIgnoreCase(str)){
                                    contact = c;
                                    checkF = true;
                                    break;
                                }


                            }

                        }
                    }
                    if (checkF) {
                        break;
                    }
                }
            }
        }
        if (clipboardEnum.equals(ClipboardEnum.INSTAGRAM)) {


            String str = link;
            int ind1 = str.indexOf(".com/");
            if (ind1 != -1) str = str.substring(ind1 + 5);

            if (str.charAt(str.length() - 1) == '/') {
                str = str.substring(0, str.length() - 1);
            }

            RealmResults<Contact> ll = realm.where(Contact.class).contains("socialModel.instagramLink", str, Case.INSENSITIVE).findAll();



            if(ll != null && !ll.isEmpty()){
                for(Contact c : ll){
                    if(c != null && c.getSocialModel() != null && c.getSocialModel().getInstagramLink() != null){

                        String ss = c.getSocialModel().getInstagramLink();

                        int ind2 = ss.indexOf(".com/");
                        if (ind2 != -1) ss = ss.substring(ind2 + 5);

                        if (ss.charAt(ss.length() - 1) == '/') {
                            ss = ss.substring(0, ss.length() - 1);
                        }

                        if(ss.equalsIgnoreCase(str)){
                            contact = c;
                            break;
                        }
                    }
                }
            }




            if (contact == null) {

                RealmResults<Contact> list = realm.where(Contact.class).isEmpty("listOfContacts").contains("listOfContactInfo.value", str, Case.INSENSITIVE).equalTo("listOfContactInfo.type", "note").findAll();
                for (Contact c : list) {
                    boolean checkF = false;
                    if (c.listOfContactInfo != null) {
                        for (ContactInfo ci : c.listOfContactInfo) {
                            if (ci.value.toLowerCase().contains(str.toLowerCase()) && ClipboardType.isInsta(ci.value)) {

                                String ss = ci.value;

                                int ind2 = ss.indexOf(".com/");
                                if (ind2 != -1) ss = ss.substring(ind2 + 5);

                                if (ss.charAt(ss.length() - 1) == '/') {
                                    ss = ss.substring(0, ss.length() - 1);
                                }

                                if(ss.equalsIgnoreCase(str)){
                                    contact = c;
                                    checkF = true;
                                    break;
                                }

                            }

                        }
                    }
                    if (checkF) break;
                }
            }




        }
        if (clipboardEnum.equals(ClipboardEnum.VK)) {
            String str = link;
            int ind1 = str.indexOf(".com/");
            if (ind1 != -1) str = str.substring(ind1 + 5);

            if (str.charAt(str.length() - 1) == '/') {
                str = str.substring(0, str.length() - 1);
            }

            RealmResults<Contact> ll = realm.where(Contact.class).contains("socialModel.vkLink", str, Case.INSENSITIVE).findAll();


            if(ll != null && !ll.isEmpty()){
                for(Contact c : ll){
                    if(c != null && c.getSocialModel() != null && c.getSocialModel().getVkLink() != null){

                        String ss = c.getSocialModel().getVkLink();

                        int ind2 = ss.indexOf(".com/");
                        if (ind2 != -1) ss = ss.substring(ind2 + 5);

                        if (ss.charAt(ss.length() - 1) == '/') {
                            ss = ss.substring(0, ss.length() - 1);
                        }

                        if(ss.equalsIgnoreCase(str)){
                            contact = c;
                            break;
                        }
                    }
                }
            }

            if (contact == null) {

                RealmResults<Contact> list = realm.where(Contact.class).isEmpty("listOfContacts").contains("listOfContactInfo.value", str, Case.INSENSITIVE).equalTo("listOfContactInfo.type", "note").findAll();
                for (Contact c : list) {
                    boolean checkF = false;
                    if (c.listOfContactInfo != null) {
                        for (ContactInfo ci : c.listOfContactInfo) {
                            if (ci.value.toLowerCase().contains(str.toLowerCase()) && ClipboardType.isVk(ci.value)) {

                                String ss = ci.value;

                                int ind2 = ss.indexOf(".com/");
                                if (ind2 != -1) ss = ss.substring(ind2 + 5);

                                if (ss.charAt(ss.length() - 1) == '/') {
                                    ss = ss.substring(0, ss.length() - 1);
                                }

                                if(ss.equalsIgnoreCase(str)){
                                    contact = c;
                                    checkF = true;
                                    break;
                                }

                            }

                        }
                    }
                    if (checkF) break;
                }
            }

        }

        if (clipboardEnum.equals(ClipboardEnum.TWITTER)) {
            String str = link;
            int ind1 = str.indexOf(".com/");
            if (ind1 != -1) str = str.substring(ind1 + 5);

            if (str.charAt(str.length() - 1) == '/') {
                str = str.substring(0, str.length() - 1);
            }

            str = str.replace("@","");

            RealmResults<Contact> ll = realm.where(Contact.class).contains("socialModel.twitterLink", str, Case.INSENSITIVE).findAll();


            if(ll != null && !ll.isEmpty()){
                for(Contact c : ll){
                    if(c != null && c.getSocialModel() != null && c.getSocialModel().getTwitterLink() != null){

                        String ss = c.getSocialModel().getTwitterLink();

                        int ind2 = ss.indexOf(".com/");
                        if (ind2 != -1) ss = ss.substring(ind2 + 5);

                        if (ss.charAt(ss.length() - 1) == '/') {
                            ss = ss.substring(0, ss.length() - 1);
                        }

                        ss = ss.replace("@","");

                        if(ss.equalsIgnoreCase(str)){
                            contact = c;
                            break;
                        }
                    }
                }
            }

            if (contact == null) {

                RealmResults<Contact> list = realm.where(Contact.class).isEmpty("listOfContacts").contains("listOfContactInfo.value", str, Case.INSENSITIVE).equalTo("listOfContactInfo.type", "note").findAll();
                for (Contact c : list) {
                    boolean checkF = false;
                    if (c.listOfContactInfo != null) {
                        for (ContactInfo ci : c.listOfContactInfo) {
                            if (ci.value.toLowerCase().contains(str.toLowerCase()) && ClipboardType.isTwitter(ci.value)) {

                                String ss = ci.value;

                                int ind2 = ss.indexOf(".com/");
                                if (ind2 != -1) ss = ss.substring(ind2 + 5);

                                if (ss.charAt(ss.length() - 1) == '/') {
                                    ss = ss.substring(0, ss.length() - 1);
                                }

                                if(ss.equalsIgnoreCase(str)){
                                    contact = c;
                                    checkF = true;
                                    break;
                                }

                            }

                        }
                    }
                    if (checkF) break;
                }
            }


        }

        if (clipboardEnum.equals(ClipboardEnum.YOUTUBE)) {
            String str = link;
            int ind1 = str.indexOf(".com/");
            if (ind1 != -1) str = str.substring(ind1 + 5);

            if (str.charAt(str.length() - 1) == '/') {
                str = str.substring(0, str.length() - 1);
            }

            RealmResults<Contact> ll = realm.where(Contact.class).contains("socialModel.youtubeLink", str, Case.INSENSITIVE).findAll();


            if(ll != null && !ll.isEmpty()){
                for(Contact c : ll){
                    if(c != null && c.getSocialModel() != null && c.getSocialModel().getYoutubeLink() != null){

                        String ss = c.getSocialModel().getYoutubeLink();

                        int ind2 = ss.indexOf(".com/");
                        if (ind2 != -1) ss = ss.substring(ind2 + 5);

                        if (ss.charAt(ss.length() - 1) == '/') {
                            ss = ss.substring(0, ss.length() - 1);
                        }

                        if(ss.equalsIgnoreCase(str)){
                            contact = c;
                            break;
                        }
                    }
                }
            }


            if (contact == null) {

                RealmResults<Contact> list = realm.where(Contact.class).isEmpty("listOfContacts").contains("listOfContactInfo.value", str, Case.INSENSITIVE).equalTo("listOfContactInfo.type", "note").findAll();
                for (Contact c : list) {
                    boolean checkF = false;
                    if (c.listOfContactInfo != null) {
                        for (ContactInfo ci : c.listOfContactInfo) {
                            if (ci.value.toLowerCase().contains(str.toLowerCase()) && ClipboardType.isYoutube(ci.value)) {

                                String ss = ci.value;

                                int ind2 = ss.indexOf(".com/");
                                if (ind2 != -1) ss = ss.substring(ind2 + 5);

                                if (ss.charAt(ss.length() - 1) == '/') {
                                    ss = ss.substring(0, ss.length() - 1);
                                }

                                if(ss.equalsIgnoreCase(str)){
                                    contact = c;
                                    checkF = true;
                                    break;
                                }

                            }

                        }
                    }
                    if (checkF) break;
                }
            }


        }

        if (clipboardEnum.equals(ClipboardEnum.LINKEDIN)) {

            String str = link;
            int ind1 = str.indexOf("/in/");
            if (ind1 != -1) str = str.substring(ind1 + 4);


            if (str.length() > 0 && str.charAt(str.length() - 1) == '/') {
                str = str.substring(0, str.length() - 1);
            }

            RealmResults<Contact> ll = realm.where(Contact.class).contains("socialModel.linkedInLink", str, Case.INSENSITIVE).findAll();

            if(ll != null && !ll.isEmpty()){
                for(Contact c : ll){
                    if(c != null && c.getSocialModel() != null && c.getSocialModel().getLinkedInLink() != null){

                        String ss = c.getSocialModel().getLinkedInLink();


                        int ind2 = ss.indexOf("/in/");
                        if (ind2 != -1) ss = ss.substring(ind2 + 4);

                        if (ss.charAt(ss.length() - 1) == '/') {
                            ss = ss.substring(0, ss.length() - 1);
                        }

                        if(ss.equalsIgnoreCase(str)){
                            contact = c;
                            break;
                        }
                    }
                }
            }

            if (contact == null) {

                RealmResults<Contact> list = realm.where(Contact.class).isEmpty("listOfContacts").contains("listOfContactInfo.value", str, Case.INSENSITIVE).equalTo("listOfContactInfo.type", "note").findAll();
                for (Contact c : list) {
                    boolean checkF = false;
                    if (c.listOfContactInfo != null) {
                        for (ContactInfo ci : c.listOfContactInfo) {
                            if (ci.value.toLowerCase().contains(str.toLowerCase()) && ClipboardType.isLinkedIn(ci.value)) {

                                String ss = ci.value;

                                int ind2 = ss.indexOf("/in/");
                                if (ind2 != -1) ss = ss.substring(ind2 + 4);

                                if (ss.charAt(ss.length() - 1) == '/') {
                                    ss = ss.substring(0, ss.length() - 1);
                                }

                                if(ss.equalsIgnoreCase(str)){
                                    contact = c;
                                    checkF = true;
                                    break;
                                }
                            }

                        }
                    }
                    if (checkF) break;
                }
            }


        }


        if (clipboardEnum.equals(ClipboardEnum.TELEGRAM)) {

            String str = link;
            int ind1 = str.indexOf(".me/");
            if (ind1 != -1) str = str.substring(ind1 + 4);

            if (str.charAt(str.length() - 1) == '/') {
                str = str.substring(0, str.length() - 1);
            }

            RealmResults<Contact> ll = realm.where(Contact.class).contains("socialModel.telegramLink", str, Case.INSENSITIVE).findAll();

            if(ll != null && !ll.isEmpty()){
                for(Contact c : ll){
                    if(c != null && c.getSocialModel() != null && c.getSocialModel().getTelegramLink() != null){

                        String ss = c.getSocialModel().getTelegramLink();


                        int ind2 = ss.indexOf(".me/");
                        if (ind2 != -1) ss = ss.substring(ind2 + 4);

                        if (ss.charAt(ss.length() - 1) == '/') {
                            ss = ss.substring(0, ss.length() - 1);
                        }

                        if(ss.equalsIgnoreCase(str)){
                            contact = c;
                            break;
                        }
                    }
                }
            }

            if (contact == null) {

                RealmResults<Contact> list = realm.where(Contact.class).isEmpty("listOfContacts").contains("listOfContactInfo.value", str, Case.INSENSITIVE).equalTo("listOfContactInfo.type", "note").findAll();
                for (Contact c : list) {
                    boolean checkF = false;
                    if (c.listOfContactInfo != null) {
                        for (ContactInfo ci : c.listOfContactInfo) {
                            if (ci.value.toLowerCase().contains(str.toLowerCase()) && ClipboardType.isTelegram(ci.value)) {

                                String ss = ci.value;

                                int ind2 = ss.indexOf(".me/");
                                if (ind2 != -1) ss = ss.substring(ind2 + 4);

                                if (ss.charAt(ss.length() - 1) == '/') {
                                    ss = ss.substring(0, ss.length() - 1);
                                }

                                if(ss.equalsIgnoreCase(str)){
                                    contact = c;
                                    checkF = true;
                                    break;
                                }
                            }

                        }
                    }
                    if (checkF) break;
                }
            }


        }

        if (clipboardEnum.equals(ClipboardEnum.SKYPE)) {

            contact = realm.where(Contact.class).equalTo("socialModel.skypeLink", link, Case.INSENSITIVE).findFirst();

            if (contact == null) {
                RealmResults<Contact> list = realm.where(Contact.class).isEmpty("listOfContacts").equalTo("listOfContactInfo.value", link, Case.INSENSITIVE).equalTo("listOfContactInfo.type", "note").findAll();
                for (Contact c : list) {
                    boolean checkF = false;
                    if (c.listOfContactInfo != null) {
                        for (ContactInfo ci : c.listOfContactInfo) {
                            if (ci.value.equalsIgnoreCase(link) && ci.type.equalsIgnoreCase("note")) {
                                contact = c;
                                checkF = true;
                                break;
                            }
                        }
                    }
                    if (checkF) break;
                }
            }

        }


        if (clipboardEnum.equals(ClipboardEnum.EMAIL)) {
            RealmResults<Contact> list = realm.where(Contact.class).equalTo("listOfContactInfo.value", link, Case.INSENSITIVE).equalTo("listOfContactInfo.type", "email").findAll();
            for (Contact c : list) {
                boolean checkF = false;
                if (c.listOfContactInfo != null) {
                    for (ContactInfo ci : c.listOfContactInfo) {
                        if (ci.value.equalsIgnoreCase(link) && ci.type.equalsIgnoreCase("email")) {
                            contact = c;
                            checkF = true;
                            break;
                        }
                    }
                }
                if (checkF) break;
            }

        }

        if (clipboardEnum.equals(ClipboardEnum.GITHUB)) {
            RealmResults<Contact> list = realm.where(Contact.class).equalTo("listOfContactInfo.value", link, Case.INSENSITIVE).equalTo("listOfContactInfo.type", "note").findAll();
            for (Contact c : list) {
                boolean checkF = false;
                if (c.listOfContactInfo != null) {
                    for (ContactInfo ci : c.listOfContactInfo) {
                        if (ci.value.equalsIgnoreCase(link) && ci.type.equalsIgnoreCase("note")) {
                            contact = c;
                            checkF = true;
                            break;
                        }
                    }
                }
                if (checkF) break;
            }

        }

        if (clipboardEnum.equals(ClipboardEnum.ANGEL)) {
            RealmResults<Contact> list = realm.where(Contact.class).equalTo("listOfContactInfo.value", link, Case.INSENSITIVE).equalTo("listOfContactInfo.type", "note").findAll();
            for (Contact c : list) {
                boolean checkF = false;
                if (c.listOfContactInfo != null) {
                    for (ContactInfo ci : c.listOfContactInfo) {
                        if (ci.value.equalsIgnoreCase(link) && ci.type.equalsIgnoreCase("note")) {
                            contact = c;
                            checkF = true;
                            break;
                        }
                    }
                }
                if (checkF) break;
            }

        }

        if (clipboardEnum.equals(ClipboardEnum.TUMBLR)) {
            RealmResults<Contact> list = realm.where(Contact.class).equalTo("listOfContactInfo.value", link, Case.INSENSITIVE).equalTo("listOfContactInfo.type", "note").findAll();
            for (Contact c : list) {
                boolean checkF = false;
                if (c.listOfContactInfo != null) {
                    for (ContactInfo ci : c.listOfContactInfo) {
                        if (ci.value.equalsIgnoreCase(link) && ci.type.equalsIgnoreCase("note")) {
                            contact = c;
                            checkF = true;
                            break;
                        }
                    }
                }
                if (checkF) break;
            }

        }

        if (clipboardEnum.equals(ClipboardEnum.G_DOC)) {
            RealmResults<Contact> list = realm.where(Contact.class).equalTo("listOfContactInfo.value", link, Case.INSENSITIVE).equalTo("listOfContactInfo.type", "note").findAll();
            for (Contact c : list) {
                boolean checkF = false;
                if (c.listOfContactInfo != null) {
                    for (ContactInfo ci : c.listOfContactInfo) {
                        if (ci.value.equalsIgnoreCase(link) && ci.type.equalsIgnoreCase("note")) {
                            contact = c;
                            checkF = true;
                            break;
                        }
                    }
                }
                if (checkF) break;
            }

        }

        if (clipboardEnum.equals(ClipboardEnum.G_SHEET)) {
            RealmResults<Contact> list = realm.where(Contact.class).equalTo("listOfContactInfo.value", link, Case.INSENSITIVE).equalTo("listOfContactInfo.type", "note").findAll();
            for (Contact c : list) {
                boolean checkF = false;
                if (c.listOfContactInfo != null) {
                    for (ContactInfo ci : c.listOfContactInfo) {
                        if (ci.value.equalsIgnoreCase(link) && ci.type.equalsIgnoreCase("note")) {
                            contact = c;
                            checkF = true;
                            break;
                        }
                    }
                }
                if (checkF) break;
            }

        }

        if (clipboardEnum.equals(ClipboardEnum.MEDIUM)) {

            String str = link;
            int ind1 = str.indexOf(".com/");
            if (ind1 != -1) str = str.substring(ind1 + 5);

            if (str.charAt(str.length() - 1) == '/') {
                str = str.substring(0, str.length() - 1);
            }

            RealmResults<Contact> ll = realm.where(Contact.class).contains("socialModel.mediumLink", str, Case.INSENSITIVE).findAll();

            if(ll != null && !ll.isEmpty()){
                for(Contact c : ll){
                    if(c != null && c.getSocialModel() != null && c.getSocialModel().getMediumLink() != null){

                        String ss = c.getSocialModel().getMediumLink();

                        int ind2 = ss.indexOf(".com/");
                        if (ind2 != -1) ss = ss.substring(ind2 + 5);

                        if (ss.charAt(ss.length() - 1) == '/') {
                            ss = ss.substring(0, ss.length() - 1);
                        }

                        if(ss.equalsIgnoreCase(str)){
                            contact = c;
                            break;
                        }
                    }
                }
            }

            if (contact == null) {

                RealmResults<Contact> list = realm.where(Contact.class).isEmpty("listOfContacts").contains("listOfContactInfo.value", str, Case.INSENSITIVE).equalTo("listOfContactInfo.type", "note").findAll();
                for (Contact c : list) {
                    boolean checkF = false;
                    if (c.listOfContactInfo != null) {
                        for (ContactInfo ci : c.listOfContactInfo) {
                            if (ci.value.toLowerCase().contains(str.toLowerCase()) && ClipboardType.isMedium(ci.value)) {

                                String ss = ci.value;

                                int ind2 = ss.indexOf(".com/");
                                if (ind2 != -1) ss = ss.substring(ind2 + 5);

                                if (ss.charAt(ss.length() - 1) == '/') {
                                    ss = ss.substring(0, ss.length() - 1);
                                }

                                if(ss.equalsIgnoreCase(str)){
                                    contact = c;
                                    checkF = true;
                                    break;
                                }
                            }

                        }
                    }
                    if (checkF) break;
                }
            }
        }

        if (clipboardEnum.equals(ClipboardEnum.PHONE)) {

            contact = realm.where(Contact.class).contains("listOfContactInfo.value", link, Case.INSENSITIVE).findFirst();


            if (contact == null) {

                RealmResults<Contact> list = realm.where(Contact.class).findAll();



                String str2 = link;
                str2 = str2.replaceAll("[\\.\\s\\-\\+\\(\\)]", "");
                if ((str2.charAt(0) == '7' || str2.charAt(0) == '8') && str2.length() == 11) {
                    str2 = str2.substring(1);
                }

                for (Contact c : list) {
                    boolean checkF = false;
                    for (ContactInfo info : c.getListOfContactInfo()) {

                        if(activity != null){
                            if(activity.findViewById(R.id.clipboardContainer).getVisibility() == View.GONE){

                                return null;
                            }
                        }

                        if ((info.type.equalsIgnoreCase("phone") || (info.typeData != null && info.typeData.equalsIgnoreCase("phone"))) && !info.value.equals("+000000000000") && info.value.length() > 7) {
                            String str = info.value;
                            str = str.replaceAll("[\\.\\s\\-\\+\\(\\)]", "").intern();

                            if (str.length() < 8) continue;

                            if ((str.charAt(0) == '7' || str.charAt(0) == '8') && str.length() == 11) {
                                str = str.substring(1);
                            }
                            if (str.equalsIgnoreCase(str2)) {
                                contact = c;
                                checkF = true;
                                break;
                            }
                        }
                    }
                    if (checkF) break;
                }
            }



        }
        if (clipboardEnum.equals(ClipboardEnum.WEB)) {
            String linkSubs = link;

            if (link.toLowerCase().contains("http://")) {
                int ind = link.toLowerCase().indexOf("http://");
                linkSubs = link.substring(ind + 7);
            } else if (link.toLowerCase().contains("https://")) {
                int ind = link.toLowerCase().indexOf("https://");
                linkSubs = link.substring(ind + 8);
            }

            if (link.toLowerCase().contains("www.")) {
                int ind = link.toLowerCase().indexOf("www.");
                linkSubs = link.substring(ind + 4);
            }

            if (linkSubs.charAt(linkSubs.length() - 1) == '/') {
                linkSubs = linkSubs.substring(0, linkSubs.length() - 1);
            }

            contact = realm.where(Contact.class).isNotEmpty("listOfContacts").equalTo("webSite", linkSubs, Case.INSENSITIVE).findFirst();


            if (contact == null) {
                RealmResults<Contact> list = realm.where(Contact.class).isEmpty("listOfContacts").contains("listOfContactInfo.value", linkSubs, Case.INSENSITIVE).equalTo("listOfContactInfo.type", "note").findAll();
                for (Contact c : list) {
                    boolean checkF = false;
                    if (c.listOfContactInfo != null) {
                        for (ContactInfo ci : c.listOfContactInfo) {
                            if (ci.value.toLowerCase().contains(linkSubs.toLowerCase()) && ClipboardType.isWeb(ci.value)) {
                                String ss = ci.value;

                                int ind2 = ss.indexOf("http://");
                                if (ind2 != -1) ss = ss.substring(ind2 + 7);

                                int ind3 = ss.indexOf("https://");
                                if (ind3 != -1) ss = ss.substring(ind3 + 8);

                                int ind4 = ss.indexOf("www.");
                                if (ind4 != -1) ss = ss.substring(ind4 + 4);


                                if (ss.charAt(ss.length() - 1) == '/') {
                                    ss = ss.substring(0, ss.length() - 1);
                                }

                                if(ss.equalsIgnoreCase(linkSubs)){
                                    contact = c;
                                    checkF = true;
                                    break;
                                }
                            }

                        }
                    }
                    if (checkF) break;
                }
            }


            //  }
        }

        if (clipboardEnum.equals(ClipboardEnum.ADDRESS)) {
            RealmResults<Contact> list = realm.where(Contact.class).equalTo("listOfContactInfo.value", link, Case.INSENSITIVE).equalTo("listOfContactInfo.type", "address").findAll();
            for (Contact c : list) {
                boolean checkF = false;
                if (c.listOfContactInfo != null) {
                    for (ContactInfo ci : c.listOfContactInfo) {
                        if (ci.value.equalsIgnoreCase(link) && ci.type.equalsIgnoreCase("address")) {
                            contact = c;
                            checkF = true;
                            break;
                        }
                    }
                }
                if (checkF) break;
            }
        }

        if (clipboardEnum.equals(ClipboardEnum.COMPANY)) {

            contact = realm.where(Contact.class).isNotEmpty("listOfContacts").equalTo("name", link, Case.INSENSITIVE).findFirst();
            if(contact == null)
                contact = realm.where(Contact.class).isNotEmpty("listOfContacts").equalTo("name", link.trim(), Case.INSENSITIVE).findFirst();

        }

        if (clipboardEnum.equals(ClipboardEnum.NAME)) {
            contact = realm.where(Contact.class).equalTo("name", link, Case.INSENSITIVE).isEmpty("listOfContacts").findFirst();
        }

        if (contact != null)
            contact = realm.copyFromRealm(contact);

        //realm.commitTransaction();
        realm.close();

        return contact;

    }

    public static void updateFavoriteContact(boolean f, long idC) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        Contact c = realm.where(Contact.class).equalTo("id", idC).findFirst();
        c.isFavorite = f;

        realm.commitTransaction();
        realm.close();
    }

    public static void updateImportantContact(boolean f, long idC) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        Contact c = realm.where(Contact.class).equalTo("id", idC).findFirst();
        c.isImportant = f;
        realm.commitTransaction();
        realm.close();
    }

    public static void updateFinishedContact(boolean f, long idC) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        Contact c = realm.where(Contact.class).equalTo("id", idC).findFirst();
        c.isFinished = f;
        realm.commitTransaction();
        realm.close();
    }

    public static void updatePauseContact(boolean f, long idC) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        Contact c = realm.where(Contact.class).equalTo("id", idC).findFirst();
        c.isPause = f;
        realm.commitTransaction();
        realm.close();
    }

    public static void updateCrownContact(boolean f, long idC) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        Contact c = realm.where(Contact.class).equalTo("id", idC).findFirst();
        c.isCrown = f;
        realm.commitTransaction();
        realm.close();
    }

    public static void updateVipContact(boolean f, long idC) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        Contact c = realm.where(Contact.class).equalTo("id", idC).findFirst();
        c.isVip = f;
        realm.commitTransaction();
        realm.close();
    }

    public static void updateStartupContact(boolean f, long idC) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        Contact c = realm.where(Contact.class).equalTo("id", idC).findFirst();
        c.isStartup = f;
        realm.commitTransaction();
        realm.close();
    }

    public static void updateInvestorContact(boolean f, long idC) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        Contact c = realm.where(Contact.class).equalTo("id", idC).findFirst();
        c.isInvestor = f;
        realm.commitTransaction();
        realm.close();
    }

    public static ArrayList<Contact> getListByEmail(String str) {
        //Realm realm = Realm.getDefaultInstance();
        ArrayList<Contact> listReturn = new ArrayList<>();
        realmContactService.beginTransaction();

        RealmResults<Contact> list = realmContactService.where(Contact.class).equalTo("listOfContactInfo.value", str, Case.INSENSITIVE).equalTo("listOfContactInfo.type", "email").findAll();
        for (Contact c : list) {
            if (c.listOfContactInfo != null) {
                for (ContactInfo ci : c.listOfContactInfo) {
                    if (ci.value.equalsIgnoreCase(str) && ci.type.equalsIgnoreCase("email")) {
                        listReturn.add(c);
                        break;
                    }
                }
            }
        }

        realmContactService.commitTransaction();
        return listReturn;
    }


    public static boolean checkExistConatctFromClipboard(long id, String link, ClipboardEnum clipboardEnum, Activity activity) {
        Realm realm = Realm.getDefaultInstance(); //+
        //realm.beginTransaction();
        Contact contact1 = null;

        try {
            if (clipboardEnum.equals(ClipboardEnum.FACEBOOK)) {


                String str = link;
                int ind1 = str.indexOf(".com/");
                if (ind1 != -1) str = str.substring(ind1 + 5);

                if (str.charAt(str.length() - 1) == '/') {
                    str = str.substring(0, str.length() - 1);
                }

                contact1 = realm.where(Contact.class).equalTo("id", id).contains("socialModel.facebookLink", str, Case.INSENSITIVE).findFirst();

                if(contact1 != null && contact1.getSocialModel() != null && contact1.getSocialModel().getFacebookLink() != null){

                    String ss = contact1.getSocialModel().getFacebookLink();

                    int ind2 = ss.indexOf(".com/");
                    if (ind2 != -1) ss = ss.substring(ind2 + 5);

                    if (ss.charAt(ss.length() - 1) == '/') {
                        ss = ss.substring(0, ss.length() - 1);
                    }

                    if(!ss.equalsIgnoreCase(str)){
                        contact1 = null;
                    }
                }

                if (contact1 == null) {


                    contact1 = realm.where(Contact.class).equalTo("id", id).isEmpty("listOfContacts").contains("listOfContactInfo.value", str, Case.INSENSITIVE).equalTo("listOfContactInfo.type", "note").findFirst();

                    boolean find = false;

                    if(contact1 != null && contact1.getListOfContactInfo() != null){

                        for (ContactInfo ci : contact1.listOfContactInfo) {
                            if (ci.value.toLowerCase().contains(str.toLowerCase()) && ClipboardType.isFacebook(ci.value)) {

                                String ss = ci.value;

                                int ind2 = ss.indexOf(".com/");
                                if (ind2 != -1) ss = ss.substring(ind2 + 5);

                                if (ss.charAt(ss.length() - 1) == '/') {
                                    ss = ss.substring(0, ss.length() - 1);
                                }

                                if(ss.equalsIgnoreCase(str)){
                                   find = true;
                                    break;
                                }

                            }
                        }
                    }

                    if(!find) contact1 = null;
                }



            } else if (clipboardEnum.equals(ClipboardEnum.INSTAGRAM)) {


                String str = link;
                int ind1 = str.indexOf(".com/");
                if (ind1 != -1) str = str.substring(ind1 + 5);

                if (str.charAt(str.length() - 1) == '/') {
                    str = str.substring(0, str.length() - 1);
                }

                contact1 = realm.where(Contact.class).equalTo("id", id).contains("socialModel.instagramLink", str, Case.INSENSITIVE).findFirst();



                        if(contact1 != null && contact1.getSocialModel() != null && contact1.getSocialModel().getInstagramLink() != null){

                            String ss = contact1.getSocialModel().getInstagramLink();

                            int ind2 = ss.indexOf(".com/");
                            if (ind2 != -1) ss = ss.substring(ind2 + 5);

                            if (ss.charAt(ss.length() - 1) == '/') {
                                ss = ss.substring(0, ss.length() - 1);
                            }

                            if(!ss.equalsIgnoreCase(str)){
                               contact1 = null;
                            }
                        }


                if (contact1 == null) {


                    contact1 = realm.where(Contact.class).equalTo("id", id).isEmpty("listOfContacts").contains("listOfContactInfo.value", str, Case.INSENSITIVE).equalTo("listOfContactInfo.type", "note").findFirst();

                    boolean find = false;

                    if(contact1 != null && contact1.getListOfContactInfo() != null){

                        for (ContactInfo ci : contact1.listOfContactInfo) {
                            if (ci.value.toLowerCase().contains(str.toLowerCase()) && ClipboardType.isInsta(ci.value)) {

                                String ss = ci.value;

                                int ind2 = ss.indexOf(".com/");
                                if (ind2 != -1) ss = ss.substring(ind2 + 5);

                                if (ss.charAt(ss.length() - 1) == '/') {
                                    ss = ss.substring(0, ss.length() - 1);
                                }

                                if(ss.equalsIgnoreCase(str)){
                                    find = true;
                                    break;
                                }

                            }
                        }
                    }

                    if(!find) contact1 = null;


                }

            } else if (clipboardEnum.equals(ClipboardEnum.VK)) {

                String str = link;
                int ind1 = str.indexOf(".com/");
                if (ind1 != -1) str = str.substring(ind1 + 5);

                if (str.charAt(str.length() - 1) == '/') {
                    str = str.substring(0, str.length() - 1);
                }

                contact1 = realm.where(Contact.class).equalTo("id", id).contains("socialModel.vkLink", str, Case.INSENSITIVE).findFirst();

                if(contact1 != null && contact1.getSocialModel() != null && contact1.getSocialModel().getVkLink() != null){

                    String ss = contact1.getSocialModel().getVkLink();

                    int ind2 = ss.indexOf(".com/");
                    if (ind2 != -1) ss = ss.substring(ind2 + 5);

                    if (ss.charAt(ss.length() - 1) == '/') {
                        ss = ss.substring(0, ss.length() - 1);
                    }

                    if(!ss.equalsIgnoreCase(str)){
                        contact1 = null;
                    }
                }

                if (contact1 == null) {


                    contact1 = realm.where(Contact.class).equalTo("id", id).isEmpty("listOfContacts").contains("listOfContactInfo.value", str, Case.INSENSITIVE).equalTo("listOfContactInfo.type", "note").findFirst();

                    boolean find = false;

                    if(contact1 != null && contact1.getListOfContactInfo() != null){

                        for (ContactInfo ci : contact1.listOfContactInfo) {
                            if (ci.value.toLowerCase().contains(str.toLowerCase()) && ClipboardType.isVk(ci.value)) {

                                String ss = ci.value;

                                int ind2 = ss.indexOf(".com/");
                                if (ind2 != -1) ss = ss.substring(ind2 + 5);

                                if (ss.charAt(ss.length() - 1) == '/') {
                                    ss = ss.substring(0, ss.length() - 1);
                                }

                                if(ss.equalsIgnoreCase(str)){
                                    find = true;
                                    break;
                                }

                            }
                        }
                    }

                    if(!find) contact1 = null;

                }

            } else if (clipboardEnum.equals(ClipboardEnum.TWITTER)) {

                String str = link;
                int ind1 = str.indexOf(".com/");
                if (ind1 != -1) str = str.substring(ind1 + 5);

                if (str.charAt(str.length() - 1) == '/') {
                    str = str.substring(0, str.length() - 1);
                }

                contact1 = realm.where(Contact.class).equalTo("id", id).contains("socialModel.twitterLink", str, Case.INSENSITIVE).findFirst();

                if(contact1 != null && contact1.getSocialModel() != null && contact1.getSocialModel().getTwitterLink() != null){

                    String ss = contact1.getSocialModel().getTwitterLink();

                    int ind2 = ss.indexOf(".com/");
                    if (ind2 != -1) ss = ss.substring(ind2 + 5);

                    if (ss.charAt(ss.length() - 1) == '/') {
                        ss = ss.substring(0, ss.length() - 1);
                    }

                    if(!ss.equalsIgnoreCase(str)){
                        contact1 = null;
                    }
                }

                if (contact1 == null) {


                    contact1 = realm.where(Contact.class).equalTo("id", id).isEmpty("listOfContacts").contains("listOfContactInfo.value", str, Case.INSENSITIVE).equalTo("listOfContactInfo.type", "note").findFirst();

                    boolean find = false;

                    if(contact1 != null && contact1.getListOfContactInfo() != null){

                        for (ContactInfo ci : contact1.listOfContactInfo) {
                            if (ci.value.toLowerCase().contains(str.toLowerCase()) && ClipboardType.isTwitter(ci.value)) {

                                String ss = ci.value;

                                int ind2 = ss.indexOf(".com/");
                                if (ind2 != -1) ss = ss.substring(ind2 + 5);

                                if (ss.charAt(ss.length() - 1) == '/') {
                                    ss = ss.substring(0, ss.length() - 1);
                                }

                                if(ss.equalsIgnoreCase(str)){
                                    find = true;
                                    break;
                                }

                            }
                        }
                    }

                    if(!find) contact1 = null;

                }

            } else if (clipboardEnum.equals(ClipboardEnum.YOUTUBE)) {

                String str = link;
                int ind1 = str.indexOf(".com/");
                if (ind1 != -1) str = str.substring(ind1 + 5);

                if (str.charAt(str.length() - 1) == '/') {
                    str = str.substring(0, str.length() - 1);
                }

                contact1 = realm.where(Contact.class).equalTo("id", id).contains("socialModel.youtubeLink", str, Case.INSENSITIVE).findFirst();

                if(contact1 != null && contact1.getSocialModel() != null && contact1.getSocialModel().getYoutubeLink() != null){

                    String ss = contact1.getSocialModel().getYoutubeLink();

                    int ind2 = ss.indexOf(".com/");
                    if (ind2 != -1) ss = ss.substring(ind2 + 5);

                    if (ss.charAt(ss.length() - 1) == '/') {
                        ss = ss.substring(0, ss.length() - 1);
                    }

                    if(!ss.equalsIgnoreCase(str)){
                        contact1 = null;
                    }
                }

                if (contact1 == null) {


                    contact1 = realm.where(Contact.class).equalTo("id", id).isEmpty("listOfContacts").contains("listOfContactInfo.value", str, Case.INSENSITIVE).equalTo("listOfContactInfo.type", "note").findFirst();


                    boolean find = false;

                    if(contact1 != null && contact1.getListOfContactInfo() != null){

                        for (ContactInfo ci : contact1.listOfContactInfo) {
                            if (ci.value.toLowerCase().contains(str.toLowerCase()) && ClipboardType.isYoutube(ci.value)) {

                                String ss = ci.value;

                                int ind2 = ss.indexOf(".com/");
                                if (ind2 != -1) ss = ss.substring(ind2 + 5);

                                if (ss.charAt(ss.length() - 1) == '/') {
                                    ss = ss.substring(0, ss.length() - 1);
                                }

                                if(ss.equalsIgnoreCase(str)){
                                    find = true;
                                    break;
                                }

                            }
                        }
                    }

                    if(!find) contact1 = null;


                }

            } else if (clipboardEnum.equals(ClipboardEnum.LINKEDIN)) {
                String str = link;
                int ind1 = str.indexOf("/in/");
                if (ind1 != -1) str = str.substring(ind1 + 4);

                if (str.charAt(str.length() - 1) == '/') {
                    str = str.substring(0, str.length() - 1);
                }

                contact1 = realm.where(Contact.class).equalTo("id", id).contains("socialModel.linkedInLink", str, Case.INSENSITIVE).findFirst();

                if(contact1 != null && contact1.getSocialModel() != null && contact1.getSocialModel().getLinkedInLink() != null){

                    String ss = contact1.getSocialModel().getLinkedInLink();

                    int ind2 = ss.indexOf("/in/");
                    if (ind2 != -1) ss = ss.substring(ind1 + 4);

                    if (ss.charAt(ss.length() - 1) == '/') {
                        ss = ss.substring(0, ss.length() - 1);
                    }

                    if(!ss.equalsIgnoreCase(str)){
                        contact1 = null;
                    }
                }

                if (contact1 == null) {


                    contact1 = realm.where(Contact.class).equalTo("id", id).isEmpty("listOfContacts").contains("listOfContactInfo.value", str, Case.INSENSITIVE).equalTo("listOfContactInfo.type", "note").findFirst();


                    boolean find = false;

                    if(contact1 != null && contact1.getListOfContactInfo() != null){

                        for (ContactInfo ci : contact1.listOfContactInfo) {
                            if (ci.value.toLowerCase().contains(str.toLowerCase()) && ClipboardType.isLinkedIn(ci.value)) {

                                String ss = ci.value;

                                int ind2 = ss.indexOf("/in/");
                                if (ind2 != -1) ss = ss.substring(ind1 + 4);

                                if (ss.charAt(ss.length() - 1) == '/') {
                                    ss = ss.substring(0, ss.length() - 1);
                                }

                                if(ss.equalsIgnoreCase(str)){
                                    find = true;
                                    break;
                                }

                            }
                        }
                    }

                    if(!find) contact1 = null;


                }

            } else if (clipboardEnum.equals(ClipboardEnum.TELEGRAM)) {
                String str = link;
                int ind1 = str.indexOf(".me/");
                if (ind1 != -1) str = str.substring(ind1 + 4);

                if (str.charAt(str.length() - 1) == '/') {
                    str = str.substring(0, str.length() - 1);
                }

                contact1 = realm.where(Contact.class).equalTo("id", id).contains("socialModel.telegramLink", str, Case.INSENSITIVE).findFirst();

                if(contact1 != null && contact1.getSocialModel() != null && contact1.getSocialModel().getTelegramLink() != null){

                    String ss = contact1.getSocialModel().getTelegramLink();

                    int ind2 = ss.indexOf(".me/");
                    if (ind2 != -1) ss = ss.substring(ind1 + 4);

                    if (ss.charAt(ss.length() - 1) == '/') {
                        ss = ss.substring(0, ss.length() - 1);
                    }

                    if(!ss.equalsIgnoreCase(str)){
                        contact1 = null;
                    }
                }

                if (contact1 == null) {


                    contact1 = realm.where(Contact.class).equalTo("id", id).isEmpty("listOfContacts").contains("listOfContactInfo.value", str, Case.INSENSITIVE).equalTo("listOfContactInfo.type", "note").findFirst();


                    boolean find = false;

                    if(contact1 != null && contact1.getListOfContactInfo() != null){

                        for (ContactInfo ci : contact1.listOfContactInfo) {
                            if (ci.value.toLowerCase().contains(str.toLowerCase()) && ClipboardType.isTelegram(ci.value)) {

                                String ss = ci.value;

                                int ind2 = ss.indexOf(".me/");
                                if (ind2 != -1) ss = ss.substring(ind1 + 4);

                                if (ss.charAt(ss.length() - 1) == '/') {
                                    ss = ss.substring(0, ss.length() - 1);
                                }

                                if(ss.equalsIgnoreCase(str)){
                                    find = true;
                                    break;
                                }

                            }
                        }
                    }

                    if(!find) contact1 = null;


                }

            }  else if (clipboardEnum.equals(ClipboardEnum.SKYPE)) {
                contact1 = realm.where(Contact.class).equalTo("id", id).equalTo("socialModel.skypeLink", link, Case.INSENSITIVE).findFirst();

                if (contact1 == null) {
                    RealmResults<Contact> list = realm.where(Contact.class).isEmpty("listOfContacts").equalTo("listOfContactInfo.value", link, Case.INSENSITIVE).equalTo("listOfContactInfo.type", "note").findAll();
                    for (Contact c : list) {
                        if (c.listOfContactInfo != null) {
                            for (ContactInfo ci : c.listOfContactInfo) {
                                if (ci.value.equalsIgnoreCase(link) && ci.type.equalsIgnoreCase("note")) {
                                    if (c.getId() == id)
                                        contact1 = c;
                                    break;
                                }
                            }
                        }
                    }
                }

            } else if (clipboardEnum.equals(ClipboardEnum.EMAIL)) {
                contact1 = realm.where(Contact.class).equalTo("id", id).equalTo("listOfContactInfo.value", link, Case.INSENSITIVE).equalTo("listOfContactInfo.type", "email").findFirst();

                boolean ch = false;
                if (contact1 != null && contact1.listOfContactInfo != null) {
                    for (ContactInfo ci : contact1.listOfContactInfo) {
                        if (ci.value.equalsIgnoreCase(link) && ci.type.equalsIgnoreCase("email")) {
                            ch = true;
                            break;
                        }
                    }
                }
                if (!ch)
                    contact1 = null;

            } else if (clipboardEnum.equals(ClipboardEnum.GITHUB)) {
                contact1 = realm.where(Contact.class).equalTo("id", id).equalTo("listOfContactInfo.value", link, Case.INSENSITIVE).equalTo("listOfContactInfo.type", "note").findFirst();

                boolean ch = false;
                if (contact1 != null && contact1.listOfContactInfo != null) {
                    for (ContactInfo ci : contact1.listOfContactInfo) {
                        if (ci.value.equalsIgnoreCase(link) && ci.type.equalsIgnoreCase("note")) {
                            ch = true;
                            break;
                        }
                    }
                }
                if (!ch)
                    contact1 = null;

            }else if (clipboardEnum.equals(ClipboardEnum.ANGEL)) {
                contact1 = realm.where(Contact.class).equalTo("id", id).equalTo("listOfContactInfo.value", link, Case.INSENSITIVE).equalTo("listOfContactInfo.type", "note").findFirst();

                boolean ch = false;
                if (contact1 != null && contact1.listOfContactInfo != null) {
                    for (ContactInfo ci : contact1.listOfContactInfo) {
                        if (ci.value.equalsIgnoreCase(link) && ci.type.equalsIgnoreCase("note")) {
                            ch = true;
                            break;
                        }
                    }
                }
                if (!ch)
                    contact1 = null;

            }else if (clipboardEnum.equals(ClipboardEnum.TUMBLR)) {
                contact1 = realm.where(Contact.class).equalTo("id", id).equalTo("listOfContactInfo.value", link, Case.INSENSITIVE).equalTo("listOfContactInfo.type", "note").findFirst();

                boolean ch = false;
                if (contact1 != null && contact1.listOfContactInfo != null) {
                    for (ContactInfo ci : contact1.listOfContactInfo) {
                        if (ci.value.equalsIgnoreCase(link) && ci.type.equalsIgnoreCase("note")) {
                            ch = true;
                            break;
                        }
                    }
                }
                if (!ch)
                    contact1 = null;

            }else if (clipboardEnum.equals(ClipboardEnum.G_DOC)) {
                contact1 = realm.where(Contact.class).equalTo("id", id).equalTo("listOfContactInfo.value", link, Case.INSENSITIVE).equalTo("listOfContactInfo.type", "note").findFirst();

                boolean ch = false;
                if (contact1 != null && contact1.listOfContactInfo != null) {
                    for (ContactInfo ci : contact1.listOfContactInfo) {
                        if (ci.value.equalsIgnoreCase(link) && ci.type.equalsIgnoreCase("note")) {
                            ch = true;
                            break;
                        }
                    }
                }
                if (!ch)
                    contact1 = null;

            }else if (clipboardEnum.equals(ClipboardEnum.G_SHEET)) {
                contact1 = realm.where(Contact.class).equalTo("id", id).equalTo("listOfContactInfo.value", link, Case.INSENSITIVE).equalTo("listOfContactInfo.type", "note").findFirst();

                boolean ch = false;
                if (contact1 != null && contact1.listOfContactInfo != null) {
                    for (ContactInfo ci : contact1.listOfContactInfo) {
                        if (ci.value.equalsIgnoreCase(link) && ci.type.equalsIgnoreCase("note")) {
                            ch = true;
                            break;
                        }
                    }
                }
                if (!ch)
                    contact1 = null;

            } else if (clipboardEnum.equals(ClipboardEnum.MEDIUM)) {

                String str = link;
                int ind1 = str.indexOf(".com/");
                if (ind1 != -1) str = str.substring(ind1 + 5);

                if (str.charAt(str.length() - 1) == '/') {
                    str = str.substring(0, str.length() - 1);
                }

                contact1 = realm.where(Contact.class).equalTo("id", id).contains("socialModel.mediumLink", str, Case.INSENSITIVE).findFirst();

                if(contact1 != null && contact1.getSocialModel() != null && contact1.getSocialModel().getMediumLink() != null){

                    String ss = contact1.getSocialModel().getMediumLink();

                    int ind2 = ss.indexOf(".com/");
                    if (ind2 != -1) ss = ss.substring(ind2 + 5);

                    if (ss.charAt(ss.length() - 1) == '/') {
                        ss = ss.substring(0, ss.length() - 1);
                    }

                    if(!ss.equalsIgnoreCase(str)){
                        contact1 = null;
                    }
                }

                if (contact1 == null) {


                    contact1 = realm.where(Contact.class).equalTo("id", id).isEmpty("listOfContacts").contains("listOfContactInfo.value", str, Case.INSENSITIVE).equalTo("listOfContactInfo.type", "note").findFirst();


                    boolean find = false;

                    if(contact1 != null && contact1.getListOfContactInfo() != null){

                        for (ContactInfo ci : contact1.listOfContactInfo) {
                            if (ci.value.toLowerCase().contains(str.toLowerCase()) && ClipboardType.isMedium(ci.value)) {

                                String ss = ci.value;

                                int ind2 = ss.indexOf(".com/");
                                if (ind2 != -1) ss = ss.substring(ind2 + 5);

                                if (ss.charAt(ss.length() - 1) == '/') {
                                    ss = ss.substring(0, ss.length() - 1);
                                }

                                if(ss.equalsIgnoreCase(str)){
                                    find = true;
                                    break;
                                }

                            }
                        }
                    }

                    if(!find) contact1 = null;


                }



            } else if (clipboardEnum.equals(ClipboardEnum.PHONE)) {


                contact1 = realm.where(Contact.class).equalTo("id", id).findFirst();
                boolean ch = false;
                if (contact1 != null && contact1.listOfContactInfo != null) {
                    for (ContactInfo info : contact1.getListOfContactInfo()) {

                        if(activity != null){
                            if(activity.findViewById(R.id.clipboardContainer).getVisibility() == View.GONE){
                                return false;
                            }
                        }

                        if (info.type.equalsIgnoreCase("phone") || (info.typeData != null && info.typeData.equalsIgnoreCase("phone"))) {

                            String str = info.value;
                            String str2 = link;
                            str = str.replaceAll("[\\.\\s\\-\\+\\(\\)]", "");
                            str2 = str2.replaceAll("[\\.\\s\\-\\+\\(\\)]", "");

                            if ((str.charAt(0) == '7' || str.charAt(0) == '8') && str.length() == 11) {
                                str = str.substring(1);
                            }
                            if ((str2.charAt(0) == '7' || str2.charAt(0) == '8') && str2.length() == 11) {
                                str2 = str2.substring(1);
                            }

                            if (str.equalsIgnoreCase(str2)) {
                                ch = true;
                                break;
                            }
                        }
                    }
                }

                if (!ch)
                    contact1 = null;



            } else if (clipboardEnum.equals(ClipboardEnum.WEB)) {


                String linkSubs = link;
                if (link.toLowerCase().contains("http://")) {
                    int ind = link.toLowerCase().indexOf("http://");
                    linkSubs = link.substring(ind + 7);
                } else if (link.toLowerCase().contains("https://")) {
                    int ind = link.toLowerCase().indexOf("https://");
                    linkSubs = link.substring(ind + 8);
                }

                if (link.toLowerCase().contains("www.")) {
                    int ind = link.toLowerCase().indexOf("www.");
                    linkSubs = link.substring(ind + 4);
                }

                if (linkSubs.charAt(linkSubs.length() - 1) == '/') {
                    linkSubs = linkSubs.substring(0, linkSubs.length() - 1);
                }



                Contact cn = realm.where(Contact.class).equalTo("id", id).findFirst();


                if (cn != null) {
                    if (cn.listOfContacts != null && !cn.listOfContacts.isEmpty()) {
                        if (cn.webSite.toLowerCase().equalsIgnoreCase(linkSubs.toLowerCase())) {
                            contact1 = cn;

                        }
                    }

                    if (cn.listOfContactInfo != null && contact1 == null) {
                        for (ContactInfo ci : cn.listOfContactInfo) {
                            if (ci.value.toLowerCase().contains(linkSubs.toLowerCase()) && ClipboardType.isWeb(ci.value)) {
                                String ss = ci.value;

                                int ind2 = ss.indexOf("http://");
                                if (ind2 != -1) ss = ss.substring(ind2 + 7);

                                int ind3 = ss.indexOf("https://");
                                if (ind3 != -1) ss = ss.substring(ind3 + 8);

                                int ind4 = ss.indexOf("www.");
                                if (ind4 != -1) ss = ss.substring(ind4 + 4);


                                if (ss.charAt(ss.length() - 1) == '/') {
                                    ss = ss.substring(0, ss.length() - 1);
                                }

                                if(ss.equalsIgnoreCase(linkSubs)){
                                    contact1 = cn;
                                    //checkF = true;
                                    break;
                                }
                            }
                        }
                    }
                }


                //  }


            } else if (clipboardEnum.equals(ClipboardEnum.ADDRESS)) {
                contact1 = realm.where(Contact.class).equalTo("id", id).equalTo("listOfContactInfo.value", link, Case.INSENSITIVE).equalTo("listOfContactInfo.type", "address").findFirst();
                boolean ch = false;
                if (contact1 != null && contact1.listOfContactInfo != null) {
                    for (ContactInfo ci : contact1.listOfContactInfo) {
                        if (ci.value.equalsIgnoreCase(link) && ci.type.equalsIgnoreCase("address")) {
                            ch = true;
                            break;
                        }
                    }
                }
                if (!ch)
                    contact1 = null;

            } else if (clipboardEnum.equals(ClipboardEnum.NAME)) {
                contact1 = realm.where(Contact.class).equalTo("id", id).equalTo("name", link, Case.INSENSITIVE).isEmpty("listOfContacts").findFirst();
            } else if (clipboardEnum.equals(ClipboardEnum.COMPANY)) {

                contact1 = realm.where(Contact.class).equalTo("id", id).isNotEmpty("listOfContacts").findFirst();

            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return false;
        }

        //realm.commitTransaction();
        realm.close();
        if (contact1 != null)
            return true;
        else
            return false;
    }

    public static boolean checkExistName(String name) {
        Realm realm = Realm.getDefaultInstance(); //+
        realm.beginTransaction();
        long count = realm.where(Contact.class).equalTo("name", name, Case.INSENSITIVE).isEmpty("listOfContacts").count();
        realm.commitTransaction();
        realm.close();
        if (count == 0) return false;
        else return true;
    }

    public static boolean checkExistCompany(String name) {
        Realm realm = Realm.getDefaultInstance(); //+
        realm.beginTransaction();
        long count = realm.where(Contact.class).equalTo("name", name, Case.INSENSITIVE).isNotEmpty("listOfContacts").count();
        realm.commitTransaction();
        realm.close();
        if (count == 0) return false;
        else return true;
    }

    public static boolean checkExistPosition(String poisition) {
        ArrayList<String> list = getPossitionContacts();
        for (String s : list) {
            if (poisition.equalsIgnoreCase(s)) return true;
        }
        return false;
    }

    public static List<String> getPopulPositionAsc(List<String> listPos, ArrayList<Integer> listCount) {
        Realm realm = Realm.getDefaultInstance(); //+
        //listPos.addAll(getPossitionContacts());


        realm.beginTransaction();


        for (String str : listPos) {
            int count = (int) realm.where(Contact.class).equalTo("companyPossition", str).count();
            listCount.add(count);
        }
        realm.commitTransaction();
        realm.close();

        for (int i = listCount.size() - 1; i > 0; i--) {
            for (int j = 0; j < i; j++) {

                if (listCount.get(j) > listCount.get(j + 1)) {
                    Integer tmp = listCount.get(j);
                    listCount.set(j, listCount.get(j + 1));
                    listCount.set(j + 1, tmp);

                    String str = listPos.get(j);
                    listPos.set(j, listPos.get(j + 1));
                    listPos.set(j + 1, str);

                }
            }
        }
        return listPos;
    }

    public static List<String> getPopulPositionDesc(List<String> listPos, ArrayList<Integer> listCount) {
        Realm realm = Realm.getDefaultInstance(); //+
        realm.beginTransaction();

        //listPos.addAll(getPossitionContacts());
        for (String str : listPos) {
            int count = (int) realm.where(Contact.class).equalTo("companyPossition", str).count();
            listCount.add(count);
        }
        realm.commitTransaction();
        realm.close();

        for (int i = listCount.size() - 1; i > 0; i--) {
            for (int j = 0; j < i; j++) {

                if (listCount.get(j) < listCount.get(j + 1)) {
                    Integer tmp = listCount.get(j);
                    listCount.set(j, listCount.get(j + 1));
                    listCount.set(j + 1, tmp);

                    String str = listPos.get(j);
                    listPos.set(j, listPos.get(j + 1));
                    listPos.set(j + 1, str);

                }
            }
        }

        return listPos;
    }

    public static void removeCompany(String name) {
        Realm realm = Realm.getDefaultInstance(); //+
        realm.beginTransaction();
        Contact company = realm.where(Contact.class).equalTo("name", name).isNotEmpty("listOfContacts").findFirst();

        company.listOfContacts = null;
        company.deleteFromRealm();
        realm.commitTransaction();
        realm.close();

    }

    public static String getAllToJson() {
        Realm realm = Realm.getDefaultInstance(); //+

        Gson gson = new Gson();

        List<Contact> list = getAllContacts(null);

        realm.beginTransaction();

        list = realm.copyFromRealm(list);

        realm.commitTransaction();

        String json = gson.toJson(list);

        realm.close();

        return json;
    }


}



