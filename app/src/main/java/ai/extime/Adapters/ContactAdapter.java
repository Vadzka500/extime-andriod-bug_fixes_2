package ai.extime.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberToCarrierMapper;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import org.greenrobot.eventbus.EventBus;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import ai.extime.Activity.MainActivity;
import ai.extime.Events.AddHistoryEntry;
import ai.extime.Events.OpenDriver;
import ai.extime.Events.UpdateFile;
import ai.extime.Fragments.ContactsFragment;
import ai.extime.Fragments.CreateFragment;
import ai.extime.Fragments.ProfileFragment;
import ai.extime.Interfaces.Postman;
import ai.extime.Models.Clipboard;
import ai.extime.Models.Contact;
import ai.extime.Models.HashTag;
import ai.extime.Models.HashTagQuantity;
import ai.extime.Models.ListAdress;
import ai.extime.Models.UpdateCountClipboard;
import ai.extime.Services.ContactCacheService;
import ai.extime.Services.ContactsService;
import ai.extime.Utils.Call2MeBackUpHelper;
import ai.extime.Utils.ClipboardType;
import de.hdodenhof.circleimageview.CircleImageView;
import ai.extime.Models.ContactInfo;

import com.extime.R;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by patal on 10.08.2017.
 */

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> implements FastScrollRecyclerView.SectionedAdapter {

    private ArrayList<Contact> listOfContacts;
    private Context context;
    private View mainView;
    public static boolean selectionModeEnabled;
    public ArrayList<Contact> selectedContacts = new ArrayList<>();
    private ArrayList<String> listOfTypesForSort;
    public ArrayList<Contact> savedContacts;
    private boolean sortAsc = true;
    private boolean sortTimeAsc = false;
    public ContactsFragment contactsFragment;
    private String searchString = "";
    private int countSelectedCompany = 0;
    private int countSelectedContacts = 0;
    private TYPE_MERGE typeMerge = TYPE_MERGE.NONE_CONTACTS;
    public static String selectedContactId;

    public Date currentDate;


    public ContactsService contactsService = null;


    public ArrayList<Contact> listOfSaveContact;

    private Toolbar toolbarC;

    public ArrayList<Contact> listOfSaveSort;

    public ArrayList<Contact> listHashSave;

    public static boolean checkMerge = false;

    public static boolean checkFoActionIconProfile = false;
    public static boolean checkFoActionIconProfileCompany = false;

    public static boolean checkFoActionIcon = false;
    public static boolean checkMergeContacts = false;

    public SharedPreferences mPref;

    private HashTagsAdapter hashTagAdapter;

    private enum TYPE_MERGE {NONE_COMPANY, NONE_CONTACTS, CONTACTS_MERGE, COMPANY_MERGE, CONTACTS_IN_COMPANY_MERGE}

    public int getCountCompanies() {
        int count = 0;
        for (Contact contact : savedContacts) {
            //===========================================================================
            // new       if(contact.listOfContacts != null && contact.getCompany() != null && contact.getCompany().trim().compareTo("") != 0){
            if (contact.listOfContacts != null && !contact.listOfContacts.isEmpty() && contact.getName().trim().compareTo("") != 0) {

                count++;
            }
        }


        return count;
    }

    public void saveHashList(){
        listHashSave = new ArrayList<>();
        listHashSave.addAll(listOfContacts);
    }

    public void setListSaveSort() {
        if (listOfSaveSort != null && listOfSaveSort.isEmpty()) {

            listOfSaveSort = new ArrayList<>();

            listOfSaveSort.addAll(listOfContacts);
        }


    }

    public ArrayList<Contact> getListSaveSort() {
        return listOfSaveSort;
    }

    public void clearListSort() {
        listOfSaveSort = new ArrayList<>();
    }

    public ArrayList<Contact> getListOfCompanies() {
        return ContactCacheService.getCompanies();
    }

    public int getContactCount() {
        int count = 0;

        for (Contact contact : savedContacts) {
            if ((contact.listOfContacts == null || contact.listOfContacts.isEmpty()) && contact.getName().trim().compareTo("") != 0) {
                count++;
            }
        }
        return count;
    }

    public ContactsFragment getContactFragment() {
        return contactsFragment;
    }


    @NonNull
    @Override
    public String getSectionName(int position) {
        Contact contact = listOfContacts.get(position);
        String name = contact.getName();
        return name.substring(0, 1);
    }

    public void sortByTime() {

        if (!sortTimeAsc) {
            sortByTimeDesc();

            sortTimeAsc = true;
        } else {
            sortByTimeAsc();
            sortTimeAsc = false;
        }
        notifyDataSetChanged();
    }

    public void selectedListContacts() {
        searchString = "";
        listOfContacts.clear();
        listOfContacts.addAll(selectedContacts);
        sortContacts();

        notifyDataSetChanged();

        if(contactsFragment.mode == 2){
            contactsFragment.updateKankabFull();
        }
    }

    public int getSizeSelectlist() {
        if (selectedContacts != null) {
            return selectedContacts.size();
        } else return 0;
    }

    public String getSelectContactID() {
        return selectedContactId;
    }

    public void addToSelectList(Contact contact) {
        selectedContacts = new ArrayList<>();
        selectedContacts.add(contact);
    }

    public void clearSelectList() {
        selectedContacts = new ArrayList<>();
    }






    public void defaultListContactsWithFilter() {

        searchString = "";
        //listOfSaveSort.clear();
        listOfContacts.clear();
        if (((CheckBox) contactsFragment.getActivity().findViewById(R.id.all_fav_check)).isChecked())
            listOfContacts.addAll(savedContacts);
        else if (!((CheckBox) contactsFragment.getActivity().findViewById(R.id.all_fav_check)).isChecked() && ((CheckBox) contactsFragment.getActivity().findViewById(R.id.companies_fav_check)).isChecked())
            listOfContacts.addAll(ContactCacheService.getCompanies());
        else if (!((CheckBox) contactsFragment.getActivity().findViewById(R.id.all_fav_check)).isChecked() && !((CheckBox) contactsFragment.getActivity().findViewById(R.id.companies_fav_check)).isChecked() && ((CheckBox) contactsFragment.getActivity().findViewById(R.id.people_fav_check)).isChecked()) {
            listOfContacts.addAll(ContactCacheService.getContacts());
        } else {
            listOfContacts.addAll(savedContacts);
        }


        clearListSort();
        setListSaveSort();


        //sortContacts();

        /*if (sortAsc)
            sortByAsc();
        else
            sortByDesc();*/

        if (!((EditText) contactsFragment.getActivity().findViewById(R.id.magic_edit_text)).getText().toString().isEmpty()) {
            searchByStr(((EditText) contactsFragment.getActivity().findViewById(R.id.magic_edit_text)).getText().toString());
        }


        notifyDataSetChanged();



    }


    public void defaultListContacts() {

        searchString = "";
        //listOfSaveSort.clear();
        listOfContacts.clear();
        if (((CheckBox) contactsFragment.getActivity().findViewById(R.id.all_fav_check)).isChecked())
            listOfContacts.addAll(savedContacts);
        else {

            if(((CheckBox) contactsFragment.getActivity().findViewById(R.id.companies_fav_check)).isChecked()) {
                listOfContacts.addAll(ContactCacheService.getCompanies());
            }
            if(((CheckBox) contactsFragment.getActivity().findViewById(R.id.people_fav_check)).isChecked()) {
                listOfContacts.addAll(ContactCacheService.getContacts());
            }else{
                if(((CheckBox) contactsFragment.getActivity().findViewById(R.id.favorites_fav_check)).isChecked()){
                    addFavorites();
                }

                if(((CheckBox) contactsFragment.getActivity().findViewById(R.id.important_fav_check)).isChecked()){
                    addImportant();
                }

                if(((CheckBox) contactsFragment.getActivity().findViewById(R.id.pause_fav_check)).isChecked()){
                    addPaused();
                }

                if(((CheckBox) contactsFragment.getActivity().findViewById(R.id.finished_fav_check)).isChecked()){
                    addFinished();
                }
            }

        }
            /* if (!((CheckBox) contactsFragment.getActivity().findViewById(R.id.all_fav_check)).isChecked() && ((CheckBox) contactsFragment.getActivity().findViewById(R.id.companies_fav_check)).isChecked()) {
            listOfContacts.addAll(ContactCacheService.getCompanies());



        }else if (!((CheckBox) contactsFragment.getActivity().findViewById(R.id.all_fav_check)).isChecked() && !((CheckBox) contactsFragment.getActivity().findViewById(R.id.companies_fav_check)).isChecked() && ((CheckBox) contactsFragment.getActivity().findViewById(R.id.people_fav_check)).isChecked()) {
            listOfContacts.addAll(ContactCacheService.getContacts());
        } else {
            listOfContacts.addAll(savedContacts);
        }*/



        clearListSort();
        setListSaveSort();


        sortContacts();

        /*if (sortAsc)
            sortByAsc();
        else
            sortByDesc();*/

        if (!((EditText) contactsFragment.getActivity().findViewById(R.id.magic_edit_text)).getText().toString().isEmpty()) {
            searchByStr(((EditText) contactsFragment.getActivity().findViewById(R.id.magic_edit_text)).getText().toString());
        }


        notifyDataSetChanged();

        if(contactsFragment.mode == 2){
            contactsFragment.updateKankabFull();
        }


    }

    public void setListOfContacts(ArrayList<Contact> list) {
        listOfContacts.clear();
        listOfContacts.addAll(list);
        notifyDataSetChanged();
    }

    public void setSaveSortToList() {
        searchString = "";
        listOfContacts.clear();
        listOfContacts.addAll(listOfSaveSort);
        //listOfSaveSort.clear();
        sortContacts();
        notifyDataSetChanged();

        if(contactsFragment.mode == 2){
            contactsFragment.updateKankabFull();
        }
    }

    public void setSaveSortToList2() {
        searchString = "";
        listOfContacts.clear();
        listOfContacts.addAll(listOfSaveSort);
        //listOfSaveSort.clear();



        sortContacts();
        notifyDataSetChanged();

        if(contactsFragment.mode == 2){
            contactsFragment.updateKankabFull();
        }
    }

    public void sortContacts() {

        SharedPreferences mPref = context.getSharedPreferences("Sort", Context.MODE_PRIVATE);

        String sort = mPref.getString("typeSort", "sortByAsc");



        if (sort.equals("sortByAsc")) {
            sortByAsc();
            //((TextView) getActivity().findViewById(R.id.sortText)).setTextColor(getResources().getColor(R.color.primary));
            //((ImageView) getActivity().findViewById(R.id.timeSort)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
        } else if (sort.equals("sortByDesc")) {
            sortByDesc();
            //((TextView) getActivity().findViewById(R.id.sortText)).setTextColor(getResources().getColor(R.color.primary));
            //((ImageView) getActivity().findViewById(R.id.timeSort)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
        } else if (sort.equals("sortByTimeAsc")) {
            sortByTimeAsc();
            //((TextView) getActivity().findViewById(R.id.sortText)).setTextColor(getResources().getColor(R.color.gray));
            //((ImageView) getActivity().findViewById(R.id.timeSort)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));
        } else if (sort.equals("sortByTimeDesc")) {
            sortByTimeDesc();
            //((TextView) getActivity().findViewById(R.id.sortText)).setTextColor(getResources().getColor(R.color.gray));
            //((ImageView) getActivity().findViewById(R.id.timeSort)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));
        }
    }


    public void addToList(Contact contact) {
        listOfContacts.add(contact);
    }

    public void addContactToSaved2(Contact contact) {
        if (listOfSaveSort != null && !listOfSaveSort.isEmpty())
            listOfSaveSort.add(contact);
    }

    public void addContactToSavedList(Contact contact) {
        savedContacts.add(contact);

        try {
            contactsFragment.listOfContacts.add(contact);

            //if(listOfSaveSort != null && !listOfSaveSort.isEmpty())
            //    listOfSaveSort.add(contact);

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (listOfSaveSort != null && !listOfSaveSort.isEmpty())
            listOfSaveSort.add(contact);


        sortContacts();
        notifyDataSetChanged();

    }

    public void addContact(Contact contact) {
        savedContacts.add(contact);
        listOfContacts.add(contact);
        try {
            contactsFragment.listOfContacts.add(contact);

            //if(listOfSaveSort != null && !listOfSaveSort.isEmpty())
            //    listOfSaveSort.add(contact);

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (listOfSaveSort != null && !listOfSaveSort.isEmpty())
            listOfSaveSort.add(contact);


        sortContacts();
        notifyDataSetChanged();

    }

    public boolean addContactWithoutNotify(Contact contact) {
        savedContacts.add(contact);
        boolean checkAdd = false;
        try {
        if (((CheckBox) contactsFragment.getActivity().findViewById(R.id.all_fav_check)).isChecked()) {
            listOfContacts.add(contact);
            checkAdd = true;
        } else if (!((CheckBox) contactsFragment.getActivity().findViewById(R.id.people_fav_check)).isChecked() && ((CheckBox) contactsFragment.getActivity().findViewById(R.id.companies_fav_check)).isChecked() && (contact.listOfContacts != null && !contact.listOfContacts.isEmpty())) {
            listOfContacts.add(contact);
            checkAdd = true;
        } else if (((CheckBox) contactsFragment.getActivity().findViewById(R.id.people_fav_check)).isChecked() && !((CheckBox) contactsFragment.getActivity().findViewById(R.id.companies_fav_check)).isChecked() && (contact.listOfContacts == null || contact.listOfContacts.isEmpty())) {
            listOfContacts.add(contact);
            checkAdd = true;
        }


            contactsFragment.listOfContacts.add(contact);

            //if(listOfSaveSort != null && !listOfSaveSort.isEmpty())
            //    listOfSaveSort.add(contact);

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (listOfSaveSort != null && !listOfSaveSort.isEmpty())
            listOfSaveSort.add(contact);


        sortContacts();
        return checkAdd;
    }

    public void addContact2(Contact contact) {
        savedContacts.add(contact);
        listOfContacts.add(contact);
        contactsFragment.listOfContacts.add(contact);
    }

   /* public void sortList() {
        if (sortAsc)
            sortByAsc();
        else
            sortByDesc();
    }*/

    public void addHashtagsToSelectedContacts(ArrayList<String> hashtasgs) {



        Realm realm = Realm.getDefaultInstance(); //+

        boolean checjMeergeHash = false;
        for (Contact contact : selectedContacts) {
            for (String hashtag : hashtasgs) {
                realm.beginTransaction();

                if (hashtasgs.size() == 1 && selectedContacts.size() == 1 && contact.getListOfHashtags() != null) {

                    if (contact.getListOfHashtags().contains(new HashTag(hashtag.toLowerCase().trim())))
                        Toast.makeText(mainView.getContext(), "Hashtag already exist", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(mainView.getContext(), "Hashtag successfully added", Toast.LENGTH_SHORT).show();

                    checjMeergeHash = true;
                }

                if (contact.getListOfHashtags() == null || !contact.getListOfHashtags().contains(new HashTag(hashtag.toLowerCase().trim()))) {
                    contact.getListOfHashtags().add(new HashTag(hashtag.toLowerCase().trim()));
                    //  String idConatct = contactsService.getIdContactByName(contact.getName());
                    if (contact.listOfContacts == null || contact.listOfContacts.isEmpty())
                        contactsService.addNoteToContact(contact.getIdContact(), hashtag.toLowerCase().trim(), contact.getName());
                }

                realm.commitTransaction();
            }
        }

        realm.close();

       /* MainActivity.listToManyUpdateFile.add(nameContact);
        MainActivity.listToManyUpdateFile.add(ckechContact);*/

        //EventBus.getDefault().post(new UpdateFile());

        if (!checjMeergeHash)
            Toast.makeText(mainView.getContext(), "Hashtag successfully added", Toast.LENGTH_SHORT).show();

    }

    public void searchByStrFromSelected(String str) {
        searchString = str;
        listOfContacts.clear();
        for (Contact contact : selectedContacts) {
            if (contact.getName().toLowerCase().contains(str.toLowerCase()) /*&& !listOfContacts.contains(contact)*/) {
                listOfContacts.add(contact);
            }
            if (contact.getCompany() != null && !listOfContacts.contains(contact)) {
                if (contact.getCompany().toLowerCase().contains(str.toLowerCase()))
                    listOfContacts.add(contact);
            }
            if (contact.getCompanyPossition() != null && !listOfContacts.contains(contact)) {
                if (contact.getCompanyPossition().toLowerCase().contains(str.toLowerCase()))
                    listOfContacts.add(contact);
            }
        }
        contactsFragment.updateCountSearchConatcts();
        notifyDataSetChanged();

        if(contactsFragment.mode == 2){
            contactsFragment.updateKankabFull();
        }

    }

    public void searchByEmailFromSelected(String str) {

        searchString = str;
        listOfContacts.clear();
        for (Contact contact : selectedContacts) {
            if (contact.listOfContactInfo != null && !contact.listOfContactInfo.isEmpty()) {
                for (ContactInfo ci : contact.listOfContactInfo) {
                    if (ci.value.equalsIgnoreCase(str)) {
                        listOfContacts.add(contact);
                        break;
                    }
                }

            }

        }
        notifyDataSetChanged();

    }

    public void searchByEmail(String str) {
        searchString = str;
        ArrayList<Contact> listForSearch = new ArrayList<>();
        listForSearch.addAll(listOfSaveSort);
        listOfContacts.clear();
        listOfContacts = ContactCacheService.getListByEmail(str);
        listForSearch.clear();
        notifyDataSetChanged();
    }


    public void searchByStrAllData(String str) {

        searchString = str;

        ArrayList<Contact> listForSearch = new ArrayList<>();

        if (((CheckBox) contactsFragment.getActivity().findViewById(R.id.all_fav_check)).isChecked())
            listForSearch.addAll(listOfSaveSort);
        else if (!((CheckBox) contactsFragment.getActivity().findViewById(R.id.all_fav_check)).isChecked() && ((CheckBox) contactsFragment.getActivity().findViewById(R.id.companies_fav_check)).isChecked())
            listForSearch.addAll(ContactCacheService.getCompanies());
        else {
            listForSearch.addAll(listOfSaveSort);
        }
        if (contactsFragment.getActivity().findViewById(R.id.frame_select_bar).getVisibility() == View.VISIBLE && (contactsFragment.listForSelect != null && !contactsFragment.listForSelect.isEmpty())) {
            listForSearch.clear();
            listForSearch.addAll(contactsFragment.listForSelect);
        }


        listOfContacts.clear();
        for (Contact contact : listForSearch) {
            if (contact != null && !contact.isValid()) {
                listForSearch.remove(contact);
                continue;
            }
            if (contact != null && contact.getName().toLowerCase().contains(str.toLowerCase()) /*&& !listOfContacts.contains(contact)*/) {
                listOfContacts.add(contact);
                continue;
            }
            if (contact != null && contact.getCompany() != null /*&& !listOfContacts.contains(contact)*/) {
                if (contact.getCompany().toLowerCase().contains(str.toLowerCase())) {
                    listOfContacts.add(contact);
                    continue;
                }
            }
            if (contact != null && contact.getCompanyPossition() != null /*&& !listOfContacts.contains(contact)*/) {
                if (contact.getCompanyPossition().toLowerCase().contains(str.toLowerCase())) {
                    listOfContacts.add(contact);
                    continue;
                }
            }

            if(contact != null && contact.getSocialModel() != null){
                if(contact.getSocialModel().getFacebookLink() != null && contact.getSocialModel().getFacebookLink().toLowerCase().contains(str.toLowerCase())){
                    listOfContacts.add(contact);
                    continue;
                }

                if(contact.getSocialModel().getInstagramLink() != null && contact.getSocialModel().getInstagramLink().toLowerCase().contains(str.toLowerCase())){
                    listOfContacts.add(contact);
                    continue;
                }

                if(contact.getSocialModel().getLinkedInLink() != null && contact.getSocialModel().getLinkedInLink().toLowerCase().contains(str.toLowerCase())){
                    listOfContacts.add(contact);
                    continue;
                }

                if(contact.getSocialModel().getTwitterLink() != null && contact.getSocialModel().getTwitterLink().toLowerCase().contains(str.toLowerCase())){
                    listOfContacts.add(contact);
                    continue;
                }

                if(contact.getSocialModel().getVkLink() != null && contact.getSocialModel().getVkLink().toLowerCase().contains(str.toLowerCase())){
                    listOfContacts.add(contact);
                    continue;
                }

                if(contact.getSocialModel().getMediumLink() != null && contact.getSocialModel().getMediumLink().toLowerCase().contains(str.toLowerCase())){
                    listOfContacts.add(contact);
                    continue;
                }

                if(contact.getSocialModel().getYoutubeLink() != null && contact.getSocialModel().getYoutubeLink().toLowerCase().contains(str.toLowerCase())){
                    listOfContacts.add(contact);
                    continue;
                }

            }

            if(contact.getListOfHashtags() != null && str.trim().charAt(0) == '#'){

                str = str.trim();

                str = str.replaceAll(" # ", " ");
                str = str.replaceAll("  ", " ");
                if(str.charAt(str.length() - 1) == '#')
                    str = str.substring(0, str.length() - 1);

                str = str.trim();

                String[] hashtags = str.split(" ");



                int count = 0;
                for(HashTag hashTag : contact.getListOfHashtags()){
                    for(String tag : hashtags){
                        if((hashTag != null && hashTag.getHashTagValue().toLowerCase().equalsIgnoreCase(tag.toLowerCase()))){

                            count++;
                            break;
                        }
                    }
                }
                if(count == hashtags.length){
                    listOfContacts.add(contact);
                }
            }

            if(contact.getListOfContactInfo() != null){
                for(ContactInfo ci : contact.getListOfContactInfo()){
                    if(ci.value.toLowerCase().contains(str.toLowerCase()) && !listOfContacts.contains(contact)){
                        listOfContacts.add(contact);
                        break;
                    }
                }
            }
        }
        contactsFragment.updateCountSearchConatcts();
        listForSearch.clear();
        sortContacts();
        notifyDataSetChanged();

        if(contactsFragment.mode == 2){
            contactsFragment.updateKankabFull();
        }



    }


    public void searchByStr(String str) {
        System.out.println("SEARCH = " + str);
        searchString = str;

        ArrayList<Contact> listForSearch = new ArrayList<>();

        if (((CheckBox) contactsFragment.getActivity().findViewById(R.id.all_fav_check)).isChecked())
            listForSearch.addAll(listOfSaveSort);
        else if (!((CheckBox) contactsFragment.getActivity().findViewById(R.id.all_fav_check)).isChecked() && ((CheckBox) contactsFragment.getActivity().findViewById(R.id.companies_fav_check)).isChecked())
            listForSearch.addAll(ContactCacheService.getCompanies());
        else {
            listForSearch.addAll(listOfSaveSort);
        }
        if (contactsFragment.getActivity().findViewById(R.id.frame_select_bar).getVisibility() == View.VISIBLE && (contactsFragment.listForSelect != null && !contactsFragment.listForSelect.isEmpty())) {

            listForSearch.clear();
            listForSearch.addAll(contactsFragment.listForSelect);



            if(!((CheckBox) contactsFragment.getActivity().findViewById(R.id.all_fav_check)).isChecked() && (((CheckBox) contactsFragment.getActivity().findViewById(R.id.favorites_fav_check)).isChecked()
            || ((CheckBox) contactsFragment.getActivity().findViewById(R.id.important_fav_check)).isChecked()
            || ((CheckBox) contactsFragment.getActivity().findViewById(R.id.pause_fav_check)).isChecked()
            || ((CheckBox) contactsFragment.getActivity().findViewById(R.id.finished_fav_check)).isChecked())){


                listForSearch.clear();
                listForSearch.addAll(listOfSaveSort);
            }

        }



        listOfContacts.clear();
        for (Contact contact : listForSearch) {
            if (contact != null && !contact.isValid()) {
                listForSearch.remove(contact);
                continue;
            }
            if (contact != null && contact.getName().toLowerCase().contains(str.toLowerCase()) /*&& !listOfContacts.contains(contact)*/) {
                listOfContacts.add(contact);
                continue;
            }
            if (contact != null && contact.getCompany() != null /*&& !listOfContacts.contains(contact)*/) {
                if (contact.getCompany().toLowerCase().contains(str.toLowerCase()))
                    listOfContacts.add(contact);
                continue;
            }
            if (contact != null && contact.getCompanyPossition() != null /*&& !listOfContacts.contains(contact)*/) {
                if (contact.getCompanyPossition().toLowerCase().contains(str.toLowerCase())) {
                    listOfContacts.add(contact);
                    continue;
                }
            }

             /*if(contact.getListOfContactInfo() != null){
                for(ContactInfo ci : contact.getListOfContactInfo()){
                    if(ClipboardType.isEmail(ci.value) && ci.value.toLowerCase().contains(str.toLowerCase())){
                        listOfContacts.add(contact);
                        break;
                    }
                }
            }*/

            /*if(contact.getListOfContactInfo() != null){
                for(ContactInfo ci : contact.getListOfContactInfo()){
                    if(ci.value.toLowerCase().contains(str.toLowerCase())){
                        listOfContacts.add(contact);
                        break;
                    }
                }
            }*/

        }
        contactsFragment.updateCountSearchConatcts();
        listForSearch.clear();
        sortContacts();
        notifyDataSetChanged();

        if(contactsFragment.mode == 2){
            contactsFragment.updateKankabFull();
        }



    }

    public void replaceCompany(String firstName, String lastName) {
        for (Contact contact : listOfContacts) {
            if (contact.getName().equals(firstName))
                contact.setName(lastName.trim());
        }
        sortByAsc();
        notifyDataSetChanged();
    }

    public void replaceContact(int possition, Contact contact) {
        listOfContacts.remove(possition);
        listOfContacts.add(contact);
        //sortByAsc();
        sortContacts();
        notifyDataSetChanged();
    }

    public int indexElement(Contact contact) {
        return listOfContacts.indexOf(contact);
    }

    public void addTypeToList(String type) {
        if (listOfTypesForSort == null) listOfTypesForSort = new ArrayList<>();
        listOfTypesForSort.add(type);
        sortByTypes();
    }

    public void addPhoneTypeToList() {
        listOfTypesForSort.addAll(Arrays.asList("phone", "com.android.contacts.sim", "vnd.sec.contact.sim"));
        sortByTypes();
    }

    public void removePhoneTypeFromList() {
        listOfTypesForSort.removeAll(Arrays.asList("phone", "com.android.contacts.sim", "vnd.sec.contact.sim"));
        sortByTypes();
    }

    public void removeTypeFromList(String type) {
        listOfTypesForSort.remove(type);
        sortByTypes();
    }

    private void sortByTypes() {
        listOfContacts.clear();
        notifyDataSetChanged();
        for (String type : listOfTypesForSort) {
            for (Contact contact : savedContacts) {
//                if(contact.listOfContacts!=null && !listOfContacts.contains(contact)) listOfContacts.add(contact);
                if (contact.accountTypes != null)
                    for (ListAdress str : contact.accountTypes)
                        if (str.getAddress().contains(type) /*&& !listOfContacts.contains(contact)*/ && (contact.listOfContacts == null || contact.listOfContacts.isEmpty()) /*&& !listOfContacts.contains(contact)*/) {
                            listOfContacts.add(contact);
                            break;
                        }

                if (type.equalsIgnoreCase("facebook")) {
                    if (contact.getSocialModel() != null) {
                        if (contact.getSocialModel().getFacebookLink() != null && !contact.getSocialModel().getFacebookLink().isEmpty() && !listOfContacts.contains(contact)) {
                            listOfContacts.add(contact);
                        }
                    }
                }

                if (type.equalsIgnoreCase("instagram")) {
                    if (contact.getSocialModel() != null) {
                        if (contact.getSocialModel().getInstagramLink() != null && !contact.getSocialModel().getInstagramLink().isEmpty() && !listOfContacts.contains(contact)) {
                            listOfContacts.add(contact);
                        }
                    }
                }

                if (type.equalsIgnoreCase("linkedin")) {
                    if (contact.getSocialModel() != null) {
                        if (contact.getSocialModel().getLinkedInLink() != null && !contact.getSocialModel().getLinkedInLink().isEmpty() && !listOfContacts.contains(contact)) {
                            listOfContacts.add(contact);
                        }
                    }
                }


            }
        }
        sortContacts();
    }

    private void sortByTypesAll() {
        listOfContacts.clear();
        notifyDataSetChanged();
        defaultListContacts();
        /*for (Contact contact : savedContacts) {

            //    mains:
            //    for (String type:listOfTypesForSort) {

            //  for(ListAdress str : contact.accountTypes) {
            if (*//*str.getAddress().contains(type)*//* *//*&& !listOfContacts.contains(contact)*//* *//*&&*//* (contact.listOfContacts == null || contact.listOfContacts.isEmpty())) {
                listOfContacts.add(contact);

                //          break mains;
                //        }
                //      }

            }
        }*/
        //listOfContacts.addAll(savedContacts);

        sortContacts();
    }

    static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        TextView companyTitle;
        TextView companyName;
        TextView time;
        TextView isNew;
        TextView initials;
        TextView hash;
        CircleImageView contactImage;
        ImageView imageSelect;
        ImageView companyImage;
        FrameLayout imageBlock;
        LinearLayout companyBlock;
        FrameLayout favotiveContact;
        LinearLayout linearNameIcons;

        LinearLayout socialContactAdapter;
        LinearLayout linear_card_phone_email;

        LinearLayout card_phone_contact;
        LinearLayout email_contact;

        TextView text_phone_card, text_email_card;


        LinearLayout timeBlock;

        LinearLayout linearDataCard;

        LinearLayout searchBlock;
        TextView searchText, searchTextCount;

        ContactViewHolder(View itemView) {
            super(itemView);
            userName = (TextView) itemView.findViewById(R.id.contactName);
            time = (TextView) itemView.findViewById(R.id.cardTime);
            companyName = (TextView) itemView.findViewById(R.id.cardCompanyName);
            companyTitle = (TextView) itemView.findViewById(R.id.companyText);
            isNew = (TextView) itemView.findViewById(R.id.is_new);
            initials = (TextView) itemView.findViewById(R.id.contactInitials);
            contactImage = (CircleImageView) itemView.findViewById(R.id.contactCircleColor);
            imageBlock = (FrameLayout) itemView.findViewById(R.id.contact_image_block);
            companyImage = (ImageView) itemView.findViewById(R.id.companyPhoto);
            companyBlock = (LinearLayout) itemView.findViewById(R.id.companyBlock);
            imageSelect = (ImageView) itemView.findViewById(R.id.imageSelect);
            favotiveContact = itemView.findViewById(R.id.frameSratCard);
            linearNameIcons = (LinearLayout) itemView.findViewById(R.id.linearNameIcons);

            socialContactAdapter = itemView.findViewById(R.id.socialContactAdapter);
            linear_card_phone_email = itemView.findViewById(R.id.linear_card_phone_email);

            card_phone_contact = itemView.findViewById(R.id.card_phone_contact);
            email_contact = itemView.findViewById(R.id.email_contact);

            text_phone_card = itemView.findViewById(R.id.text_phone_card);
            text_email_card = itemView.findViewById(R.id.text_email_card);

            timeBlock = itemView.findViewById(R.id.timeBlock);
            linearDataCard = itemView.findViewById(R.id.linearDataCard);
            //  hash = (TextView) itemView.findViewById(R.id.hashCard);

            searchBlock = itemView.findViewById(R.id.searchBlock);
            searchText = itemView.findViewById(R.id.searchText);
            searchTextCount = itemView.findViewById(R.id.searchTextCount);
        }
    }

    public void selectAllContacts() {
        listOfTypesForSort.clear();
        listOfTypesForSort.addAll(Arrays.asList("phone", "com.android.contacts.sim", "vnd.sec.contact.sim", "com.whatsapp", "org.telegram.messenger",
                "com.google", "com.samsung.android.scloud", "com.viber.voip", "com.skype.contacts.sync"));
        sortByTypesAll();
    }

    public void removeAllContacts() {
        listOfTypesForSort.clear();
        listOfTypesForSort.removeAll(Arrays.asList("phone", "com.android.contacts.sim", "vnd.sec.contact.sim", "com.whatsapp", "org.telegram.messenger",
                "com.google", "com.samsung.android.scloud", "com.viber.voip", "com.skype.contacts.sync"));
        sortByTypes();
    }

    private void addTypesToList() {
        listOfTypesForSort = new ArrayList<>();
        listOfTypesForSort.addAll(Arrays.asList("phone", "com.android.contacts.sim", "vnd.sec.contact.sim", "com.whatsapp", "org.telegram.messenger",
                "com.google", "com.samsung.android.scloud", "com.viber.voip", "com.skype.contacts.sync"));
    }

    public int findByPossition(String possition) {
        listOfContacts.clear();
        notifyDataSetChanged();
        for (Contact contact : savedContacts) {
            if (contact.getCompanyPossition() != null)
                if (contact.getCompanyPossition().toLowerCase().equals(possition.toLowerCase())) {
                    listOfContacts.add(contact);
                }
        }
        notifyDataSetChanged();
        return listOfContacts.size();
    }

    public ContactAdapter(Context context, ArrayList<Contact> listOfContacts, ContactsFragment contactsFragment, Toolbar toolbar, HashTagsAdapter hashTagsAdapter) {
        addTypesToList();
        savedContacts = new ArrayList<>();

        savedContacts.addAll((ArrayList<Contact>) listOfContacts.clone());

        this.contactsFragment = contactsFragment;
        this.context = context;
        this.toolbarC = toolbar;
        this.hashTagAdapter = hashTagsAdapter;
        currentDate = new Date();


        mPref = context.getSharedPreferences("Sort", Context.MODE_PRIVATE);

        if (mPref.getString("typeSort", "null").equals("null")) {
            SharedPreferences.Editor editor = mPref.edit();
            editor.putString("typeSort", "sortByTimeDesc");
            editor.apply();
        }

        this.listOfContacts = listOfContacts;
        listOfSaveSort = new ArrayList<>();
        listOfSaveSort.addAll(listOfContacts);

    }

    public void sortByTimeAsc() {
        sortTimeAsc = false;
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString("typeSort", "sortByTimeAsc");
        editor.apply();
        Collections.sort(listOfContacts, new Comparator<Contact>() {
            @Override
            public int compare(Contact o1, Contact o2) {
                if (o1.getDateCreate() != null && o2.getDateCreate() != null)
                    return o1.getDateCreate().compareTo(o2.getDateCreate());
                else
                    return 0;

            }
        });
    }

    public void sortByTimeAscSaved() {
        sortTimeAsc = false;
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString("typeSort", "sortByTimeAsc");
        editor.apply();
        Collections.sort(savedContacts, new Comparator<Contact>() {
            @Override
            public int compare(Contact o1, Contact o2) {

                return o1.getDateCreate().compareTo(o2.getDateCreate());

            }
        });
    }

    public void setSelectContactID(String name) {
        selectedContactId = name;
    }

    public void addContactNew(ArrayList<Long> list) {
        ArrayList<Contact> listConatcts = new ArrayList<>();
        for (long id : list) {
            Contact contact = ContactCacheService.getContactById(id);
            listConatcts.add(contact);
        }

        SharedPreferences mPref = context.getSharedPreferences("Sort", Context.MODE_PRIVATE);

        String sort = mPref.getString("typeSort", "sortByAsc");

        int countAdd = 0;
        for (Contact c : listConatcts) {
            if (addContactWithoutNotify(c)) {
                countAdd++;
            }
        }

        LinearLayoutManager myLayoutManager = (LinearLayoutManager) contactsFragment.containerContacts.getLayoutManager();
        int scrollPosition = myLayoutManager.findFirstVisibleItemPosition();


        if (sort.equalsIgnoreCase("sortByTimeDesc")) {
            notifyItemRangeInserted(0, countAdd);
            if (scrollPosition == 0)
                contactsFragment.containerContacts.scrollToPosition(0);

            //notifyItemRangeChanged(0,listConatcts.size());
        } else {
            notifyDataSetChanged();
        }

    }

    public void searchByCompany(String company) {
        listOfContacts.clear();
        for (Contact contactForSearch : savedContacts) {
            if (contactForSearch.getCompany() != null) {
                if (contactForSearch.getCompany().toLowerCase().compareTo(company.toLowerCase()) == 0) {
                    listOfContacts.add(contactForSearch);
                }
            }
        }

        contactsFragment.closeOtherPopup();
        //  contactsFragment.getActivity().findViewById(R.id.frame_select_bar).setVisibility(View.VISIBLE);

        contactsFragment.getActivity().findViewById(R.id.contactsText).setVisibility(View.GONE);
        contactsFragment.getActivity().findViewById(R.id.stop_selection_mode).setOnClickListener(v -> {

            stopSelectionMode();
            stopNEWSelection();
            //    contactsFragment.getActivity().findViewById(R.id.frame_select_bar).setVisibility(View.GONE);

            defaultListContacts();
        });
        ((TextView) contactsFragment.getActivity().findViewById(R.id.quantity_selected_contacts)).setText(listOfContacts.size() + " selected by company " + company);

        notifyDataSetChanged();
    }


    public void sortByTimeDesc() {
        sortTimeAsc = true;
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString("typeSort", "sortByTimeDesc");
        editor.apply();
        Collections.sort(listOfContacts, new Comparator<Contact>() {
            @Override
            public int compare(Contact o1, Contact o2) {
                // add to another sort
                if ( o1 != null && o2 != null && o1.isValid() && o2.isValid() && o1.getDateCreate() != null && o2.getDateCreate() != null && o1.getDateCreate() != null && o2.getDateCreate() != null)
                    return o2.getDateCreate().compareTo(o1.getDateCreate());
                else
                    return 0;

            }
        });
    }

    public void sortByTimeDescSaved() {
        sortTimeAsc = true;
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString("typeSort", "sortByTimeDesc");
        editor.apply();
        Collections.sort(savedContacts, new Comparator<Contact>() {
            @Override
            public int compare(Contact o1, Contact o2) {

                if ( o1 != null && o2 != null && o1.isValid() && o2.isValid() && o1.getDateCreate() != null && o2.getDateCreate() != null && o1.getDateCreate() != null && o2.getDateCreate() != null)
                    return o2.getDateCreate().compareTo(o1.getDateCreate());
                else
                    return 0;



            }
        });
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

    public static void startAct(Intent intent) {


    }

    public void searchByHashTagValue(String hashTagValue) {

        System.out.println("HASHTAG SEARCH + " + hashTagValue);
        contactsFragment.clearHashtagPopup();
        listOfContacts.clear();
        //  selectedContacts = new ArrayList<>();
        for (Contact contact : savedContacts) {
            for (HashTag hashTag : contact.getListOfHashtags()) {
                if (hashTag.getHashTagValue().compareTo(hashTagValue) == 0) {
                    listOfContacts.add(contact);
                    //  selectedContacts.add(contact);
                }
            }
        }

        contactsFragment.checkByHashTag(hashTagValue);
        contactsFragment.closeOtherPopup();
        contactsFragment.getActivity().findViewById(R.id.docButton).setVisibility(View.GONE);
        contactsFragment.getActivity().findViewById(R.id.frame_select_bar).setVisibility(View.VISIBLE);  //select bar

        contactsFragment.getActivity().findViewById(R.id.typeHash).setVisibility(View.VISIBLE);

        if (contactsFragment.hashTagPopup != null && ((RadioButton) contactsFragment.hashTagPopup.findViewById(R.id.radioOR)).isChecked()) {
            ((TextView) contactsFragment.getActivity().findViewById(R.id.typeHash)).setText("OR");
        } else if (contactsFragment.hashTagPopup != null && ((RadioButton) contactsFragment.hashTagPopup.findViewById(R.id.radioAND)).isChecked()) {
            ((TextView) contactsFragment.getActivity().findViewById(R.id.typeHash)).setText("AND");
        }

        contactsFragment.getActivity().findViewById(R.id.contactsText).setVisibility(View.GONE);
        contactsFragment.getActivity().findViewById(R.id.stop_selection_mode).setOnClickListener(v -> {
            ((CheckBox) contactsFragment.getActivity().findViewById(R.id.allHashtagCheck)).setChecked(true);


            ((CheckBox) contactsFragment.getActivity().findViewById(R.id.allHashtagCheckContactss)).setChecked(true);
            ((CheckBox) contactsFragment.getActivity().findViewById(R.id.NoTagsCheck)).setChecked(true);
            ((CheckBox) contactsFragment.getActivity().findViewById(R.id.allHashtagCheckContacts)).setChecked(true);


            contactsFragment.getActivity().findViewById(R.id.frame_select_bar).setVisibility(View.GONE);
            contactsFragment.initRecyclerHashTags();
            stopSelectionMode();
            stopNEWSelection();


            defaultListContacts();
            //setListOfContacts(contactsFragment.listForSelect);
            contactsFragment.listForSelect.clear();
        });
        ((TextView) contactsFragment.getActivity().findViewById(R.id.barHashtag_count)).setText("(1)");
        ((TextView) contactsFragment.getActivity().findViewById(R.id.quantity_selected_contacts)).setText(listOfContacts.size() + " selected by hashtag " + hashTagValue);
    }


    public void shareSelectedContacts() {
        String exportData = "";

        for (Contact contact : selectedContacts) {
            //contact.fillData(getContext(), contactsService);
            if (contact.getName() != null)
                exportData += "Name: " + contact.getName() + "\n";
            if (contact.getCompany() != null)
                exportData += "Company: " + contact.getCompany() + "\n";
            if (contact.getCompanyPossition() != null)
                exportData += "Position: " + contact.getCompanyPossition() + "\n";
            if (contact.listOfContactInfo != null) {
                //contactInfos.addAll(contact.listOfContactInfo);
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

            //contactInfos.clear();
        }
        exportData += "\n";
        exportData += "Data shared via http://Extime.pro\n";


        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, exportData);
        context.startActivity(Intent.createChooser(shareIntent, "Поделиться контактом"));
    }

    public void mergeSelectedContactsToList() {
        if (listOfContacts.size() != selectedContacts.size()) {
            listOfSaveContact = new ArrayList<>();
            listOfSaveContact.addAll(listOfContacts);
            listOfContacts.clear();
            listOfContacts.addAll(selectedContacts);
            notifyDataSetChanged();
        }
    }

    public void sortByDesc() {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString("typeSort", "sortByDesc");
        editor.apply();
        Collections.sort(listOfContacts, (contactFirst, contactSecond) -> contactSecond.getName().compareToIgnoreCase(contactFirst.getName()));
        sortAsc = false;
        notifyDataSetChanged();
    }

    public ArrayList<Contact> getSelectedContacts() {
        return selectedContacts;
    }

    public void clearListContacts() {
        listOfContacts.clear();
        notifyDataSetChanged();
    }

    public void removeNoTags() {
        //    listOfContacts.clear();

        for (Contact contact : contactsFragment.mergedContacts ? selectedContacts : savedContacts) {
            if (contact.getListOfHashtags() == null || contact.getListOfHashtags().isEmpty())
                // for (HashTag hashtagValue : contact.getListOfHashtags()) {
                //     if(hashtagValue.getHashTagValue().trim().compareTo(hashtag.trim()) == 0 && !listOfContacts.contains(contact)) {
                listOfContacts.remove(contact);
            //     }
            //  }
        }

        sortContacts();

        //  return listOfContacts.size();
    }


    public void removeByHashtags(String hashtag) {

        List<Contact> contactsList = new ArrayList<>();
        contactsList.addAll(contactsFragment.mergedContacts ? selectedContacts : listOfContacts);


        for (int indexI = 0; indexI < contactsList.size(); indexI++) {
            Contact contact = contactsList.get(indexI);
            boolean remove = false;
            boolean endRemove = false;
            for (HashTag hashtagValue : contact.getListOfHashtags()) {
                //  remove = false;
                if (hashtagValue.getHashTagValue().equals(hashtag)) {
                    remove = true;
                    break;
                }
                //  if (!remove) break;
            }
            if (contactsFragment.mergedContacts && remove) selectedContacts.remove(contact);
            if (!contactsFragment.mergedContacts && remove) listOfContacts.remove(contact);
        }

        notifyDataSetChanged();
    }

    public void removeByHashtagsNEW(ArrayList<HashTagQuantity> hashtag, String hash) {

        List<Contact> contactsList = new ArrayList<>();
        contactsList.addAll(contactsFragment.mergedContacts ? selectedContacts : listOfContacts);


        for (int indexI = 0; indexI < contactsList.size(); indexI++) {
            Contact contact = contactsList.get(indexI);
            boolean remove = false;
            if (contact.getListOfHashtags() == null || contact.getListOfHashtags().isEmpty()) {
                remove = true;
            } else
                for (HashTag hashtagValue : contact.getListOfHashtags()) {
                    for (HashTagQuantity hs : hashtag) {
                        if (hs.getHashTag().getHashTagValue().trim().equalsIgnoreCase(hashtagValue.getHashTagValue().trim())) {
                            remove = true;
                            break;
                        }
                    }
                    if (remove) break;
                }

            if (contactsFragment.mergedContacts && !remove) selectedContacts.remove(contact);
            if (!contactsFragment.mergedContacts && !remove) listOfContacts.remove(contact);
        }

        if (contactsFragment.getActivity().findViewById(R.id.frame_select_bar).getVisibility() == View.VISIBLE && ((TextView) contactsFragment.getActivity().findViewById(R.id.quantity_selected_contacts)).getText().toString().contains("hashtag")) {
            String s = "";
            for (HashTagQuantity hs : hashtag) {
                if (s.equals("")) s += hs.getHashTag().getHashTagValue().toString().trim();
                else {
                    s += ", " + hs.getHashTag().getHashTagValue().toString().trim();
                }
            }

            ((TextView) contactsFragment.getActivity().findViewById(R.id.quantity_selected_contacts)).setText(listOfContacts.size() + " selected by hashtag " + s);
        }

        if (hashtag.size() == 0)
            contactsFragment.getActivity().findViewById(R.id.frame_select_bar).setVisibility(View.GONE);

        notifyDataSetChanged();
    }

    public void addHyperHashtag(String hashtag) {
        listOfContacts.clear();

        for (Contact contact : contactsFragment.mergedContacts ? selectedContacts : savedContacts) {
            if (contact.getListOfHashtags() != null)
                for (HashTag hashtagValue : contact.getListOfHashtags()) {
                    if (hashtagValue.getHashTagValue().trim().equals(hashtag.trim()) && !listOfContacts.contains(contact)) {
                        listOfContacts.add(contact);
                    }
                }
        }
        sortContacts();

        contactsFragment.getActivity().findViewById(R.id.docButton).setVisibility(View.GONE);
        contactsFragment.getActivity().findViewById(R.id.frame_select_bar).setVisibility(View.VISIBLE);  //select bar


        contactsFragment.getActivity().findViewById(R.id.typeHash).setVisibility(View.VISIBLE);

        if (contactsFragment.hashTagPopup != null && ((RadioButton) contactsFragment.hashTagPopup.findViewById(R.id.radioOR)).isChecked()) {
            ((TextView) contactsFragment.getActivity().findViewById(R.id.typeHash)).setText("OR");
        } else if (contactsFragment.hashTagPopup != null && ((RadioButton) contactsFragment.hashTagPopup.findViewById(R.id.radioAND)).isChecked()) {
            ((TextView) contactsFragment.getActivity().findViewById(R.id.typeHash)).setText("AND");
        }


        contactsFragment.getActivity().findViewById(R.id.contactsText).setVisibility(View.GONE);

        contactsFragment.getActivity().findViewById(R.id.stop_selection_mode).setOnClickListener(v -> {

            if(hashTagAdapter == null){
                hashTagAdapter = contactsFragment.HASHTAG_ADAPTER;
            }


            if (contactsFragment.hashTagPopup != null && ((RadioButton) contactsFragment.hashTagPopup.findViewById(R.id.radioOR)).isChecked()) {
                ((CheckBox) contactsFragment.getActivity().findViewById(R.id.allHashtagCheck)).setChecked(true);


                ((CheckBox) contactsFragment.getActivity().findViewById(R.id.allHashtagCheckContactss)).setChecked(true);
                ((CheckBox) contactsFragment.getActivity().findViewById(R.id.NoTagsCheck)).setChecked(true);
                ((CheckBox) contactsFragment.getActivity().findViewById(R.id.allHashtagCheckContacts)).setChecked(true);

                contactsFragment.getActivity().findViewById(R.id.frame_select_bar).setVisibility(View.GONE);

                contactsFragment.initRecyclerHashTags();
                stopSelectionMode();
                stopNEWSelection();


                defaultListContacts();
                //setListOfContacts(contactsFragment.listForSelect);
                contactsFragment.listForSelect.clear();
            } else if (contactsFragment.hashTagPopup != null && ((RadioButton) contactsFragment.hashTagPopup.findViewById(R.id.radioAND)).isChecked()) {


                //((RadioButton)hashTagPopup.findViewById(R.id.radioAND)).setTextColor(getResources().getColor(R.color.primary));
                //Toast.makeText(getContext(), "Selected tags same time", Toast.LENGTH_SHORT).show();

                contactsFragment.getActivity().findViewById(R.id.frame_select_bar).setVisibility(View.GONE);

                ContactAdapter.selectionModeEnabled = false;
                contactsFragment.listForSelect = new ArrayList<>();
                selectedContacts = new ArrayList<>();
                listOfSaveContact = new ArrayList<>();

                if (!((CheckBox) contactsFragment.getActivity().findViewById(R.id.all_fav_check)).isChecked()) {
                    ((CheckBox) contactsFragment.getActivity().findViewById(R.id.all_fav_check)).setChecked(true);
                } else {
                    defaultListContacts();
                    try {
                        if(hashTagAdapter != null)
                        ((TextView) contactsFragment.getActivity().findViewById(R.id.barHashtag_count)).setText("" + String.valueOf(hashTagAdapter.listOfHashtags.size()) + "");
                        else
                            ((TextView) contactsFragment.getActivity().findViewById(R.id.barHashtag_count)).setText("" + String.valueOf(contactsFragment.HASHTAG_ADAPTER.listOfHashtags.size()) + "");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                //getActivity().findViewById(R.id.allContactsLL).callOnClick();

                ((CheckBox) contactsFragment.getActivity().findViewById(R.id.NoTagsCheck)).setChecked(false);
                ((CheckBox) contactsFragment.getActivity().findViewById(R.id.allHashtagCheckContacts)).setChecked(false);
                contactsFragment.checkChangeHash = true;
                ((CheckBox) contactsFragment.getActivity().findViewById(R.id.allHashtagCheck)).setChecked(false);
                contactsFragment.checkChangeHash = false;
                ((CheckBox) contactsFragment.getActivity().findViewById(R.id.allHashtagCheckContactss)).setChecked(false);




                hashTagAdapter.quantityCheck = 0;
                hashTagAdapter.setAllHashtagsCheck(false);
                hashTagAdapter.allHashtagsCheck = false;
                hashTagAdapter.hyperHashtag = "";
                hashTagAdapter.notifyDataSetChanged();

            }
        });
        ((TextView) contactsFragment.getActivity().findViewById(R.id.barHashtag_count)).setText("(1)");
        ((TextView) contactsFragment.getActivity().findViewById(R.id.quantity_selected_contacts)).setText(listOfContacts.size() + " selected by hashtag " + hashtag);




        notifyDataSetChanged();
    }

    public void setListOfListOFrSelect() {

        searchString = "";
        listOfContacts = new ArrayList<>();
        listOfContacts.addAll(contactsFragment.listForSelect);
        sortContacts();
        notifyDataSetChanged();

        if(contactsFragment.mode == 2){
            contactsFragment.updateKankabFull();
        }
    }

    public void getAllHashtag() {
        listOfContacts.clear();

        for (Contact contact : contactsFragment.mergedContacts ? selectedContacts : savedContacts) {
            if (contact.getListOfHashtags() != null && !contact.getListOfHashtags().isEmpty())
                // for (HashTag hashtagValue : contact.getListOfHashtags()) {
                //     if(hashtagValue.getHashTagValue().trim().compareTo(hashtag.trim()) == 0 && !listOfContacts.contains(contact)) {
                listOfContacts.add(contact);
            //     }
            //  }
        }
        sortContacts();


    }

    public int getAllWithoutHashtag() {
        listOfContacts.clear();

        for (Contact contact : contactsFragment.mergedContacts ? selectedContacts : savedContacts) {
            if (contact.getListOfHashtags() == null || contact.getListOfHashtags().isEmpty())
                // for (HashTag hashtagValue : contact.getListOfHashtags()) {
                //     if(hashtagValue.getHashTagValue().trim().compareTo(hashtag.trim()) == 0 && !listOfContacts.contains(contact)) {
                listOfContacts.add(contact);
            //     }
            //  }
        }

        sortContacts();

        return listOfContacts.size();
    }

    public void addAllWithoutHashtag() {
        //   listOfContacts.clear();

        for (Contact contact : contactsFragment.mergedContacts ? selectedContacts : savedContacts) {
            if (contact.getListOfHashtags() == null || contact.getListOfHashtags().isEmpty())
                // for (HashTag hashtagValue : contact.getListOfHashtags()) {
                //     if(hashtagValue.getHashTagValue().trim().compareTo(hashtag.trim()) == 0 && !listOfContacts.contains(contact)) {
                if (!listOfContacts.contains(contact))
                    listOfContacts.add(contact);
            //     }
            //  }
        }

        sortContacts();

        //return listOfContacts.size();
    }

    public void addContactsByHashtag(String hashtag) {
        for (Contact contact : contactsFragment.mergedContacts ? selectedContacts : savedContacts) {
            if (contact.getListOfHashtags() != null)
                for (HashTag hashtagValue : contact.getListOfHashtags()) {
                    if (hashtagValue.getHashTagValue().trim().compareTo(hashtag.trim()) == 0 && !listOfContacts.contains(contact)) {
                        listOfContacts.add(contact);
                    }
                }
        }


        sortContacts();
        notifyDataSetChanged();
    }

    public void addContactsByHashtagNEW(ArrayList<HashTagQuantity> list, String hashtag) {
        for (Contact contact : contactsFragment.mergedContacts ? selectedContacts : savedContacts) {
            if (contact.getListOfHashtags() != null)
                for (HashTag hashtagValue : contact.getListOfHashtags()) {
                    if (hashtagValue.getHashTagValue().trim().compareTo(hashtag.trim()) == 0 && !listOfContacts.contains(contact)) {
                        listOfContacts.add(contact);
                    }
                }
        }

        if (contactsFragment.getActivity().findViewById(R.id.frame_select_bar).getVisibility() == View.VISIBLE && ((TextView) contactsFragment.getActivity().findViewById(R.id.quantity_selected_contacts)).getText().toString().contains("hashtag")) {

            String s = "";
            for (HashTagQuantity hs : list) {
                if (s.equals("")) s += hs.getHashTag().getHashTagValue().toString().trim();
                else {
                    s += ", " + hs.getHashTag().getHashTagValue().toString().trim();
                }
            }

            ((TextView) contactsFragment.getActivity().findViewById(R.id.quantity_selected_contacts)).setText(listOfContacts.size() + " selected by hashtag " + s);
        }

        sortContacts();

        saveHashList();

        notifyDataSetChanged();
    }


    public void addContactsByHashtagNEWAND(ArrayList<HashTagQuantity> list, ArrayList<String> listHash) {
        listOfContacts.clear();
        for (Contact contact : contactsFragment.mergedContacts ? selectedContacts : savedContacts) {
            if (contact.getListOfHashtags() != null) {
                boolean checkLast = true;

                for (String h : listHash) {
                    boolean checkHashFind = false;
                    for (HashTag hashtagValue : contact.getListOfHashtags()) {
                        if (hashtagValue.getHashTagValue().trim().compareTo(h.trim()) == 0 && !listOfContacts.contains(contact)) {
                            checkHashFind = true;
                            break;
                        }
                    }

                    if (!checkHashFind) {
                        checkLast = false;
                        break;
                    }
                }

                if (checkLast) listOfContacts.add(contact);
            }
        }

        if (contactsFragment.getActivity().findViewById(R.id.frame_select_bar).getVisibility() == View.VISIBLE && ((TextView) contactsFragment.getActivity().findViewById(R.id.quantity_selected_contacts)).getText().toString().contains("hashtag")) {

            String s = "";
            for (HashTagQuantity hs : list) {
                if (s.equals("")) s += hs.getHashTag().getHashTagValue().toString().trim();
                else {
                    s += ", " + hs.getHashTag().getHashTagValue().toString().trim();
                }
            }

            ((TextView) contactsFragment.getActivity().findViewById(R.id.quantity_selected_contacts)).setText(listOfContacts.size() + " selected by hashtag " + s);
        }

        sortContacts();

        saveHashList();

        notifyDataSetChanged();
    }

    public void addContactsByHashtagNEWWithoutNotify(ArrayList<HashTagQuantity> list, String hashtag) {
        for (Contact contact : contactsFragment.mergedContacts ? selectedContacts : savedContacts) {
            if (contact.getListOfHashtags() != null)
                for (HashTag hashtagValue : contact.getListOfHashtags()) {
                    if (hashtagValue.getHashTagValue().trim().compareTo(hashtag.trim()) == 0 && !listOfContacts.contains(contact)) {
                        listOfContacts.add(contact);
                    }
                }
        }

        if (contactsFragment.getActivity().findViewById(R.id.frame_select_bar).getVisibility() == View.VISIBLE && ((TextView) contactsFragment.getActivity().findViewById(R.id.quantity_selected_contacts)).getText().toString().contains("hashtag")) {

            String s = "";
            for (HashTagQuantity hs : list) {
                if (s.equals("")) s += hs.getHashTag().getHashTagValue().toString().trim();
                else {
                    s += ", " + hs.getHashTag().getHashTagValue().toString().trim();
                }
            }

            ((TextView) contactsFragment.getActivity().findViewById(R.id.quantity_selected_contacts)).setText(listOfContacts.size() + " selected by hashtag " + s);
        }


    }

    private void setMargins(View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }

    public void removeContact(Contact contact) {

        savedContacts.remove(contact);
        try {
            listOfContacts.remove(contact);
        } catch (Exception e) {

        }

    }


    public void removeContactById(Contact contact) {

        try {
            for (int i = 0; i < savedContacts.size(); i++) {
                if (savedContacts.get(i) == null || !savedContacts.get(i).isValid() || (contact != null && savedContacts.get(i).getId() == contact.getId())) {
                    savedContacts.remove(i);
                    break;
                }
            }


            for (int i = 0; i < listOfContacts.size(); i++) {
                if (listOfContacts.get(i) == null || !listOfContacts.get(i).isValid() || (contact != null && listOfContacts.get(i).getId() == contact.getId())) {
                    listOfContacts.remove(i);
                    break;
                }
            }

            for (int i = 0; i < contactsFragment.listOfContacts.size(); i++) {
                if (contactsFragment.listOfContacts.get(i) == null || !contactsFragment.listOfContacts.get(i).isValid() || (contact != null && contactsFragment.listOfContacts.get(i).getId() == contact.getId())) {
                    contactsFragment.listOfContacts.remove(i);
                    break;
                }
            }

            if (listOfSaveSort != null)
                for (int i = 0; i < listOfSaveSort.size(); i++) {
                    if (listOfSaveSort.get(i) == null || !listOfSaveSort.get(i).isValid() || (contact != null && listOfSaveSort.get(i).getId() == contact.getId())) {
                        listOfSaveSort.remove(i);
                        break;
                    }
                }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void removeContactByIdLong(long contact) {

        try {
            for (int i = 0; i < savedContacts.size(); i++) {
                if (savedContacts.get(i).getId() == contact) {
                    savedContacts.remove(i);
                    break;
                }
            }


            for (int i = 0; i < listOfContacts.size(); i++) {
                if (listOfContacts.get(i).getId() == contact) {
                    listOfContacts.remove(i);
                    break;
                }
            }

            for (int i = 0; i < contactsFragment.listOfContacts.size(); i++) {
                if (contactsFragment.listOfContacts.get(i).getId() == contact) {
                    contactsFragment.listOfContacts.remove(i);
                    break;
                }
            }

            if (listOfSaveSort != null)
                for (int i = 0; i < listOfSaveSort.size(); i++) {
                    if (listOfSaveSort.get(i).getId() == contact) {
                        listOfSaveSort.remove(i);
                        break;
                    }
                }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void removeSelectedContacts() {

        for (Contact contact : selectedContacts) {

            for (int i = 0; i < savedContacts.size(); i++) {
                if (savedContacts.get(i).getId() == contact.getId()) {

                    savedContacts.remove(i);
                }

                if (savedContacts.get(i).getId() == contact.getId()) {

                    listOfContacts.remove(i);
                }
            }
            //  savedContacts.remove(contact);


            // listOfContacts.remove(contact);


            try {


            } catch (Exception e) {
                e.printStackTrace();
            }


        }
        notifyDataSetChanged();
        // ContactCacheService.updateCacheContacts(savedContacts,context);
        try {
            Call2MeBackUpHelper.requestBackup(mainView.getContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void removeSelectedContactsFromMERGE(ArrayList<Contact> selectedContacts) {

        for (Contact contact : selectedContacts) {

            for (int i = 0; i < savedContacts.size(); i++) {
                if (savedContacts.get(i).getId() == contact.getId()) {

                    savedContacts.remove(i);
                }


            }
            for (int i = 0; i < listOfContacts.size(); i++) {
                if (listOfContacts.get(i).getId() == contact.getId()) {

                    listOfContacts.remove(i);
                }
            }
            //  savedContacts.remove(contact);
            try {

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        notifyDataSetChanged();
        // ContactCacheService.updateCacheContacts(savedContacts,context);
        try {
            Call2MeBackUpHelper.requestBackup(mainView.getContext());
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    public ArrayList<Contact> getSavedList() {
        return savedContacts;
    }

    public ArrayList<Contact> getListOfContacts() {

        return listOfContacts;
    }

    public void changeFromRemoveCompany(Contact c) {

        Realm realm = Realm.getDefaultInstance(); //-
        realm.beginTransaction();

        try {
            for (int j = 0; j < savedContacts.size(); j++) {
                if (savedContacts.get(j).getCompany() != null && savedContacts.get(j).getCompany().equals(c.getName())) {
                    savedContacts.get(j).setCompany(null);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            for (int j = 0; j < listOfContacts.size(); j++) {
                if (listOfContacts.get(j).getCompany() != null && listOfContacts.get(j).getCompany().equals(c.getName())) {
                    listOfContacts.get(j).setCompany(null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        realm.commitTransaction();
        realm.close();
    }


    public void removeCompanyFromList(Contact contact) {

        savedContacts.remove(contact);
        listOfContacts.remove(contact);
        //  ContactsFragment.listOfContacts.remove(contact);


    }

    public void sortByAsc() {

        SharedPreferences.Editor editor = mPref.edit();
        editor.putString("typeSort", "sortByAsc");
        editor.apply();
        try {

            Collections.sort(listOfContacts, (contactFirst, contactSecond) -> contactFirst.getName().compareToIgnoreCase(contactSecond.getName()));
        } catch (Exception e) {

        }

        sortAsc = true;
        notifyDataSetChanged();
    }

    public void removeLastSelectedContact() {
        if (selectedContacts.size() != 0) {
            if (selectedContacts.get(selectedContacts.size() - 1).listOfContacts == null || selectedContacts.get(selectedContacts.size() - 1).listOfContacts.isEmpty())
                countSelectedContacts--;
            else
                countSelectedCompany--;
            selectedContacts.remove(selectedContacts.size() - 1);
            //   changeContactsCountBar();
            updateCountSelected();
            checkMergeCount();
            if (selectedContacts.size() == 0) stopNEWSelection();
            notifyDataSetChanged();

        } else {
            stopNEWSelection();
            notifyDataSetChanged();
        }
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        mainView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_contact, viewGroup, false);
        if (contactsService == null)
            contactsService = new ContactsService(contactsFragment.getActivity().getContentResolver(), false);
        //ContactAdapter.contactAd = this;
        return new ContactViewHolder(mainView);
    }

    @Override
    public void onBindViewHolder(final ContactViewHolder holder, int position) {

        final Contact contact = listOfContacts.get(position);

       /*try {
           holder.isNew.setVisibility(contact.isNew ? View.VISIBLE : View.GONE);
       }catch (Exception e){
           e.printStackTrace();
       }*/

        holder.socialContactAdapter.setVisibility(View.GONE);

        holder.socialContactAdapter.findViewById(R.id.facebook_icon_adapter).setVisibility(View.GONE);
        holder.socialContactAdapter.findViewById(R.id.twitter_icon_adapter).setVisibility(View.GONE);
        holder.socialContactAdapter.findViewById(R.id.linked_icon_adapter).setVisibility(View.GONE);
        holder.socialContactAdapter.findViewById(R.id.instagram_icon_adapter).setVisibility(View.GONE);
        holder.socialContactAdapter.findViewById(R.id.youtube_icon_adapter).setVisibility(View.GONE);
        holder.socialContactAdapter.findViewById(R.id.vk_icon_adapter).setVisibility(View.GONE);
        holder.socialContactAdapter.findViewById(R.id.medium_icon_adapter).setVisibility(View.GONE);

        holder.searchBlock.setVisibility(View.GONE);

        holder.linear_card_phone_email.setVisibility(View.GONE);


        int type = 1;
        try {
           /* Calendar cal = Calendar.getInstance();
            cal.setTime(contact.getDateCreate());
            String time =  cal.get(Calendar.HOUR_OF_DAY)+":"+cal.get(Calendar.MINUTE);*/
            Calendar current = Calendar.getInstance();
            Calendar contactDate = Calendar.getInstance();
            current.setTime(currentDate);
            contactDate.setTime(contact.getDateCreate());
            String timeStr = "";
            if (current.get(Calendar.YEAR) == contactDate.get(Calendar.YEAR) && current.get(Calendar.MONTH) == contactDate.get(Calendar.MONTH) && current.get(Calendar.DAY_OF_MONTH) == contactDate.get(Calendar.DAY_OF_MONTH)) {

                timeStr = Time.valueOf(contact.time).getHours() + ":";
                if (Time.valueOf(contact.time).getMinutes() < 10) timeStr += "0";
                timeStr += Time.valueOf(contact.time).getMinutes();
            } else if (current.get(Calendar.YEAR) == contactDate.get(Calendar.YEAR) && (current.get(Calendar.MONTH) != contactDate.get(Calendar.MONTH) || current.get(Calendar.DAY_OF_MONTH) != contactDate.get(Calendar.DAY_OF_MONTH))) {

                type = 2;
                timeStr = contactDate.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.ENGLISH) + " " + contactDate.get(Calendar.DAY_OF_MONTH);
            } else {
                type = 3;
                timeStr = contactDate.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.ENGLISH) + " " + String.valueOf(contactDate.get(Calendar.YEAR))/*.substring(2)*/;

            }


            holder.time.setText(/*getUpdTime(contact.getDateCreate(), Time.valueOf(contact.time))*/ timeStr);
        } catch (Exception e) {
            System.out.println("ERROR TO GET TIME Contact Adapter");
        }


        if (contact.isFavorite || contact.isImportant || contact.isFinished || contact.isPause || contact.isCrown || contact.isVip || contact.isStartup || contact.isInvestor) {
            holder.favotiveContact.setVisibility(View.VISIBLE);

            if (contact.isFavorite) {
                ((ImageView) holder.favotiveContact.findViewById(R.id.favoriteIcon2)).setImageDrawable(contactsFragment.getResources().getDrawable(R.drawable.star));
            } else if (contact.isImportant) {
                ((ImageView) holder.favotiveContact.findViewById(R.id.favoriteIcon2)).setImageDrawable(contactsFragment.getResources().getDrawable(R.drawable.checked_2));
            } else if (contact.isFinished) {
                ((ImageView) holder.favotiveContact.findViewById(R.id.favoriteIcon2)).setImageDrawable(contactsFragment.getResources().getDrawable(R.drawable.finish_1));
            } else if (contact.isPause) {
                ((ImageView) holder.favotiveContact.findViewById(R.id.favoriteIcon2)).setImageDrawable(contactsFragment.getResources().getDrawable(R.drawable.pause_1));
            }else if (contact.isCrown) {
                ((ImageView) holder.favotiveContact.findViewById(R.id.favoriteIcon2)).setImageDrawable(contactsFragment.getResources().getDrawable(R.drawable.crown));
            }else if (contact.isVip) {
                ((ImageView) holder.favotiveContact.findViewById(R.id.favoriteIcon2)).setImageDrawable(contactsFragment.getResources().getDrawable(R.drawable.vip_new));
            }else if (contact.isStartup) {
                ((ImageView) holder.favotiveContact.findViewById(R.id.favoriteIcon2)).setImageDrawable(contactsFragment.getResources().getDrawable(R.drawable.startup));
            }else if (contact.isInvestor) {
                ((ImageView) holder.favotiveContact.findViewById(R.id.favoriteIcon2)).setImageDrawable(contactsFragment.getResources().getDrawable(R.drawable.investor_));
            }


            /*holder.time.post(new Runnable() {
                @Override
                public void run() {*/
            if (type == 1) {
                //int px = holder.timeBlock.getWidth();
                int px_2 = (int) (17 * context.getResources().getDisplayMetrics().density);
                int px_padding = (int) (54 * context.getResources().getDisplayMetrics().density);

                holder.linearDataCard.setPadding(0, 0, px_padding + px_2, 0);
                holder.linearDataCard.requestLayout();
            } else if (type == 2) {
                //int px = holder.timeBlock.getWidth();
                int px_2 = (int) (17 * context.getResources().getDisplayMetrics().density);
                int px_padding = (int) (60 * context.getResources().getDisplayMetrics().density);

                holder.linearDataCard.setPadding(0, 0, px_2 + px_padding, 0);
                holder.linearDataCard.requestLayout();
            } else if (type == 3) {
                int px_2 = (int) (17 * context.getResources().getDisplayMetrics().density);
                int px_padding = (int) (68 * context.getResources().getDisplayMetrics().density);

                holder.linearDataCard.setPadding(0, 0, px_2 + px_padding, 0);
                holder.linearDataCard.requestLayout();
            }

               /* }
            });*/

        } else {
            holder.favotiveContact.setVisibility(View.GONE);   //new


            /*holder.time.post(new Runnable() {
                @Override
                public void run() {
                    int px = holder.timeBlock.getWidth();
                    int px_padding = (int) (5 * context.getResources().getDisplayMetrics().density);
                    //int px_2 = (int) (17 * context.getResources().getDisplayMetrics().density);

                    holder.linearDataCard.setPadding(0,0,px + px_padding,0);
                    holder.linearDataCard.requestLayout();


                }
            });*/

            if (type == 1) {
                //int px = holder.timeBlock.getWidth();
                //int px_2 = (int) (17 * context.getResources().getDisplayMetrics().density);
                int px_padding = (int) (54 * context.getResources().getDisplayMetrics().density);

                holder.linearDataCard.setPadding(0, 0, px_padding, 0);
                holder.linearDataCard.requestLayout();
            } else if (type == 2) {
                //int px = holder.timeBlock.getWidth();
                //int px_2 = (int) (17 * context.getResources().getDisplayMetrics().density);
                int px_padding = (int) (60 * context.getResources().getDisplayMetrics().density);

                holder.linearDataCard.setPadding(0, 0, px_padding, 0);
                holder.linearDataCard.requestLayout();
            } else if (type == 3) {
                int px_padding = (int) (68 * context.getResources().getDisplayMetrics().density);

                holder.linearDataCard.setPadding(0, 0, px_padding, 0);
                holder.linearDataCard.requestLayout();
            }

        }


        if (MainActivity.secretMode == 2 || MainActivity.secretMode == 3) {
            int countSocials = 0;

            //holder.favotiveContact.setVisibility(View.GONE);    //new

            if (contact.getSocialModel() != null) {
                if (contact.getSocialModel().getFacebookLink() != null && !contact.getSocialModel().getFacebookLink().isEmpty()) {
                    holder.socialContactAdapter.findViewById(R.id.facebook_icon_adapter).setVisibility(View.VISIBLE);
                    countSocials++;
                }
                if (contact.getSocialModel().getTwitterLink() != null && !contact.getSocialModel().getTwitterLink().isEmpty()) {
                    holder.socialContactAdapter.findViewById(R.id.twitter_icon_adapter).setVisibility(View.VISIBLE);
                    countSocials++;
                }
                if (contact.getSocialModel().getLinkedInLink() != null && !contact.getSocialModel().getLinkedInLink().isEmpty()) {
                    holder.socialContactAdapter.findViewById(R.id.linked_icon_adapter).setVisibility(View.VISIBLE);
                    countSocials++;
                }
                if (contact.getSocialModel().getInstagramLink() != null && !contact.getSocialModel().getInstagramLink().isEmpty()) {
                    holder.socialContactAdapter.findViewById(R.id.instagram_icon_adapter).setVisibility(View.VISIBLE);
                    countSocials++;
                }

                while (true) {
                    if (countSocials < 4) {
                        if (contact.getSocialModel().getYoutubeLink() != null && !contact.getSocialModel().getYoutubeLink().isEmpty()) {
                            holder.socialContactAdapter.findViewById(R.id.youtube_icon_adapter).setVisibility(View.VISIBLE);
                            countSocials++;
                        }
                    }

                    if (countSocials < 4) {
                        if (contact.getSocialModel().getVkLink() != null && !contact.getSocialModel().getVkLink().isEmpty()) {
                            holder.socialContactAdapter.findViewById(R.id.vk_icon_adapter).setVisibility(View.VISIBLE);
                            countSocials++;
                        }
                    }

                    if (countSocials < 4) {
                        if (contact.getSocialModel().getMediumLink() != null && !contact.getSocialModel().getMediumLink().isEmpty()) {
                            holder.socialContactAdapter.findViewById(R.id.medium_icon_adapter).setVisibility(View.VISIBLE);
                            countSocials++;
                        }
                    }

                    break;
                }


            }


            if (countSocials > 0)
                holder.socialContactAdapter.setVisibility(View.VISIBLE);
            else
                holder.socialContactAdapter.setVisibility(View.GONE);

            if (MainActivity.secretMode == 3) {

                int count_phone = 0;
                int count_email = 0;
                if (contact.getListOfContactInfo() != null) {
                    for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                        if (contactInfo.isEmail) count_email++;
                        else if (contactInfo.isPhone && !contactInfo.value.equals("+000000000000"))
                            count_phone++;
                    }

                    if (count_phone != 0) {
                        holder.card_phone_contact.setVisibility(View.VISIBLE);
                        holder.text_phone_card.setText(String.valueOf(count_phone));
                        holder.isNew.setVisibility(View.GONE);
                        holder.linear_card_phone_email.setVisibility(View.VISIBLE);
                    } else holder.card_phone_contact.setVisibility(View.GONE);

                    if (count_email != 0) {
                        holder.email_contact.setVisibility(View.VISIBLE);
                        holder.text_email_card.setText(String.valueOf(count_email));
                        holder.isNew.setVisibility(View.GONE);
                        holder.linear_card_phone_email.setVisibility(View.VISIBLE);
                    } else holder.email_contact.setVisibility(View.GONE);

                }
            }

        } else if (MainActivity.secretMode == 1) {


           /* if (contact.isFavorite || contact.isFinished || contact.isImportant || contact.isPause) {
                if (contact.isFavorite) {
                    ((ImageView)holder.favotiveContact.findViewById(R.id.favoriteIcon2)).setImageDrawable(contactsFragment.getResources().getDrawable(R.drawable.star));
                } else if (contact.isImportant) {
                    ((ImageView)holder.favotiveContact.findViewById(R.id.favoriteIcon2)).setImageDrawable(contactsFragment.getResources().getDrawable(R.drawable.checked_2));
                } else if (contact.isFinished) {
                    ((ImageView)holder.favotiveContact.findViewById(R.id.favoriteIcon2)).setImageDrawable(contactsFragment.getResources().getDrawable(R.drawable.finish_1));
                } else if (contact.isPause) {
                    ((ImageView)holder.favotiveContact.findViewById(R.id.favoriteIcon2)).setImageDrawable(contactsFragment.getResources().getDrawable(R.drawable.pause_1));
                }
                holder.favotiveContact.setVisibility(View.VISIBLE);
                //holder.socialContactAdapter.setVisibility(View.VISIBLE);
            } else {
                holder.favotiveContact.setVisibility(View.GONE);
                //holder.socialContactAdapter.setVisibility(View.GONE);
            }*/    //new

            //holder.socialContactAdapter.setVisibility(View.GONE);
        }

        if (contactsFragment.listNewContacts != null && contactsFragment.listNewContacts.contains(contact.getId())) {
            holder.isNew.setVisibility(View.VISIBLE);
        } else {
            holder.isNew.setVisibility(View.GONE);
        }

        setMargins(holder.linearNameIcons, 0, 52, 0, 0);

//        holder.userName.setGravity(View.TEXT_ALIGNMENT_CENTER);
//        holder.companyBlock.setVisibility(View.GONE);

        holder.companyName.setVisibility(View.GONE);
        holder.companyTitle.setVisibility(View.GONE);

        holder.itemView.findViewById(R.id.icon_search_contact_adapter).setVisibility(View.GONE);

        holder.companyTitle.setVisibility(View.VISIBLE);

        holder.userName.setText(contact.getName());

        holder.companyBlock.setVisibility(View.VISIBLE);

        if (searchString != "" && contact.getName().toLowerCase().contains(searchString.toLowerCase())) {

            try {
                int startI = contact.getName().toLowerCase().indexOf(searchString.toLowerCase());
                final SpannableStringBuilder text = new SpannableStringBuilder(contact.getName());
                final ForegroundColorSpan style = new ForegroundColorSpan(Color.parseColor("#1976D2"));
                StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
                text.setSpan(style, startI, startI + searchString.length(), Spannable.SPAN_COMPOSING);
                text.setSpan(bss, startI, startI + searchString.length(), Spannable.SPAN_COMPOSING);

                holder.userName.setText(text);
            } catch (Exception e) {
                holder.userName.setText(contact.getName());
            }
        } else if(searchString != "" && !contact.getName().toLowerCase().contains(searchString.toLowerCase())){
            if(contact.getCompany() != null && contact.getCompany().toLowerCase().contains(searchString.toLowerCase())) {

                holder.userName.setText(contact.getName());
            }else if(contact.getCompanyPossition() != null && contact.getCompanyPossition().toLowerCase().contains(searchString.toLowerCase())){

                holder.userName.setText(contact.getName());
            }else{

                int count_data = 0;

                if(contact.getListOfHashtags() != null && searchString.trim().charAt(0) == '#'){

                    searchString = searchString.trim();

                    searchString = searchString.replaceAll(" # ", " ");
                    searchString = searchString.replaceAll("  ", " ");
                    if(searchString.charAt(searchString.length() - 1) == '#')
                        searchString = searchString.substring(0, searchString.length() - 1);

                    searchString = searchString.trim();

                    String [] hash = searchString.split(" ");


                    boolean checkHash = false;
                    StringBuilder hashFind = new StringBuilder();

                    holder.searchText.setText("");

                    int countH = 0;
                    for(HashTag hashTag : contact.getListOfHashtags()){

                        for(String tag : hash) {
                            if(tag.length() == 1 && tag.equalsIgnoreCase("#")){
                                countH++;
                            }else if (hashTag != null && hashTag.getHashTagValue().toLowerCase().equalsIgnoreCase(tag.toLowerCase().trim())) {



                                hashFind.append(tag).append(" ");

                                countH++;
                                    checkHash = true;




                                break;

                            }
                        }
                    }
                    if(checkHash && countH == hash.length){

                        int startI = 0;
                        final SpannableStringBuilder text = new SpannableStringBuilder(hashFind);
                        final ForegroundColorSpan style = new ForegroundColorSpan(Color.parseColor("#1976D2"));
                        StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
                        text.setSpan(style, startI, startI + hashFind.length(), Spannable.SPAN_COMPOSING);
                        text.setSpan(bss, startI, startI + hashFind.length(), Spannable.SPAN_COMPOSING);

                        holder.searchText.setText(text);

                        count_data++;
                    }
                }

                if(contact.getListOfContactInfo() != null) {

                    holder.searchTextCount.setVisibility(View.GONE);




                    if(contact.getSocialModel() != null){
                        if(contact.getSocialModel().getFacebookLink() != null && contact.getSocialModel().getFacebookLink().toLowerCase().contains(searchString.toLowerCase())){

                            if(count_data == 0){

                                int startI = contact.getSocialModel().getFacebookLink().toLowerCase().indexOf(searchString.toLowerCase());
                                final SpannableStringBuilder text = new SpannableStringBuilder(contact.getSocialModel().getFacebookLink());
                                final ForegroundColorSpan style = new ForegroundColorSpan(Color.parseColor("#1976D2"));
                                StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
                                text.setSpan(style, startI, startI + searchString.length(), Spannable.SPAN_COMPOSING);
                                text.setSpan(bss, startI, startI + searchString.length(), Spannable.SPAN_COMPOSING);

                                holder.searchText.setText(text);

                                ((ImageView) holder.itemView.findViewById(R.id.icon_search_contact_adapter)).setImageDrawable(contactsFragment.getResources().getDrawable(R.drawable.icn_social_facebook));
                                holder.itemView.findViewById(R.id.icon_search_contact_adapter).setVisibility(View.VISIBLE);

                            }

                            count_data++;
                        }

                        if(contact.getSocialModel().getInstagramLink() != null && contact.getSocialModel().getInstagramLink().toLowerCase().contains(searchString.toLowerCase())){

                            if(count_data == 0){

                                int startI = contact.getSocialModel().getInstagramLink().toLowerCase().indexOf(searchString.toLowerCase());
                                final SpannableStringBuilder text = new SpannableStringBuilder(contact.getSocialModel().getInstagramLink());
                                final ForegroundColorSpan style = new ForegroundColorSpan(Color.parseColor("#1976D2"));
                                StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
                                text.setSpan(style, startI, startI + searchString.length(), Spannable.SPAN_COMPOSING);
                                text.setSpan(bss, startI, startI + searchString.length(), Spannable.SPAN_COMPOSING);

                                holder.searchText.setText(text);

                                ((ImageView) holder.itemView.findViewById(R.id.icon_search_contact_adapter)).setImageDrawable(contactsFragment.getResources().getDrawable(R.drawable.instagram_adapter));
                                holder.itemView.findViewById(R.id.icon_search_contact_adapter).setVisibility(View.VISIBLE);

                            }

                            count_data++;
                        }

                        if(contact.getSocialModel().getLinkedInLink() != null && contact.getSocialModel().getLinkedInLink().toLowerCase().contains(searchString.toLowerCase())){

                            if(count_data == 0){

                                int startI = contact.getSocialModel().getLinkedInLink().toLowerCase().indexOf(searchString.toLowerCase());
                                final SpannableStringBuilder text = new SpannableStringBuilder(contact.getSocialModel().getLinkedInLink());
                                final ForegroundColorSpan style = new ForegroundColorSpan(Color.parseColor("#1976D2"));
                                StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
                                text.setSpan(style, startI, startI + searchString.length(), Spannable.SPAN_COMPOSING);
                                text.setSpan(bss, startI, startI + searchString.length(), Spannable.SPAN_COMPOSING);

                                holder.searchText.setText(text);

                                ((ImageView) holder.itemView.findViewById(R.id.icon_search_contact_adapter)).setImageDrawable(contactsFragment.getResources().getDrawable(R.drawable.icn_social_linkedin));
                                holder.itemView.findViewById(R.id.icon_search_contact_adapter).setVisibility(View.VISIBLE);

                            }

                            count_data++;
                        }

                        if(contact.getSocialModel().getTwitterLink() != null && contact.getSocialModel().getTwitterLink().toLowerCase().contains(searchString.toLowerCase())){

                            if(count_data == 0){

                                int startI = contact.getSocialModel().getTwitterLink().toLowerCase().indexOf(searchString.toLowerCase());
                                final SpannableStringBuilder text = new SpannableStringBuilder(contact.getSocialModel().getTwitterLink());
                                final ForegroundColorSpan style = new ForegroundColorSpan(Color.parseColor("#1976D2"));
                                StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
                                text.setSpan(style, startI, startI + searchString.length(), Spannable.SPAN_COMPOSING);
                                text.setSpan(bss, startI, startI + searchString.length(), Spannable.SPAN_COMPOSING);

                                holder.searchText.setText(text);

                                ((ImageView) holder.itemView.findViewById(R.id.icon_search_contact_adapter)).setImageDrawable(contactsFragment.getResources().getDrawable(R.drawable.ic_twitter_64));
                                holder.itemView.findViewById(R.id.icon_search_contact_adapter).setVisibility(View.VISIBLE);

                            }

                            count_data++;
                        }

                        if(contact.getSocialModel().getVkLink() != null && contact.getSocialModel().getVkLink().toLowerCase().contains(searchString.toLowerCase())){

                            if(count_data == 0){

                                int startI = contact.getSocialModel().getVkLink().toLowerCase().indexOf(searchString.toLowerCase());
                                final SpannableStringBuilder text = new SpannableStringBuilder(contact.getSocialModel().getVkLink());
                                final ForegroundColorSpan style = new ForegroundColorSpan(Color.parseColor("#1976D2"));
                                StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
                                text.setSpan(style, startI, startI + searchString.length(), Spannable.SPAN_COMPOSING);
                                text.setSpan(bss, startI, startI + searchString.length(), Spannable.SPAN_COMPOSING);

                                holder.searchText.setText(text);

                                ((ImageView) holder.itemView.findViewById(R.id.icon_search_contact_adapter)).setImageDrawable(contactsFragment.getResources().getDrawable(R.drawable.icn_social_vk));
                                holder.itemView.findViewById(R.id.icon_search_contact_adapter).setVisibility(View.VISIBLE);

                            }

                            count_data++;
                        }

                        if(contact.getSocialModel().getMediumLink() != null && contact.getSocialModel().getMediumLink().toLowerCase().contains(searchString.toLowerCase())){

                            if(count_data == 0){

                                int startI = contact.getSocialModel().getMediumLink().toLowerCase().indexOf(searchString.toLowerCase());
                                final SpannableStringBuilder text = new SpannableStringBuilder(contact.getSocialModel().getMediumLink());
                                final ForegroundColorSpan style = new ForegroundColorSpan(Color.parseColor("#1976D2"));
                                StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
                                text.setSpan(style, startI, startI + searchString.length(), Spannable.SPAN_COMPOSING);
                                text.setSpan(bss, startI, startI + searchString.length(), Spannable.SPAN_COMPOSING);

                                holder.searchText.setText(text);

                                ((ImageView) holder.itemView.findViewById(R.id.icon_search_contact_adapter)).setImageDrawable(contactsFragment.getResources().getDrawable(R.drawable.medium_adapter));
                                holder.itemView.findViewById(R.id.icon_search_contact_adapter).setVisibility(View.VISIBLE);

                            }

                            count_data++;
                        }

                        if(contact.getSocialModel().getYoutubeLink() != null && contact.getSocialModel().getYoutubeLink().toLowerCase().contains(searchString.toLowerCase())){

                            if(count_data == 0){

                                int startI = contact.getSocialModel().getYoutubeLink().toLowerCase().indexOf(searchString.toLowerCase());
                                final SpannableStringBuilder text = new SpannableStringBuilder(contact.getSocialModel().getYoutubeLink());
                                final ForegroundColorSpan style = new ForegroundColorSpan(Color.parseColor("#1976D2"));
                                StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
                                text.setSpan(style, startI, startI + searchString.length(), Spannable.SPAN_COMPOSING);
                                text.setSpan(bss, startI, startI + searchString.length(), Spannable.SPAN_COMPOSING);

                                holder.searchText.setText(text);

                                ((ImageView) holder.itemView.findViewById(R.id.icon_search_contact_adapter)).setImageDrawable(contactsFragment.getResources().getDrawable(R.drawable.ic_youtube_48));
                                holder.itemView.findViewById(R.id.icon_search_contact_adapter).setVisibility(View.VISIBLE);

                            }

                            count_data++;
                        }

                    }


                    for(ContactInfo ci : contact.getListOfContactInfo()){
                        if(ci != null && ci.value != null){
                            if(ci.value.toLowerCase().contains(searchString.toLowerCase())){
                                if(count_data == 0){

                                    int startI = ci.value.toLowerCase().indexOf(searchString.toLowerCase());
                                    final SpannableStringBuilder text = new SpannableStringBuilder(ci.value);
                                    final ForegroundColorSpan style = new ForegroundColorSpan(Color.parseColor("#1976D2"));
                                    StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
                                    text.setSpan(style, startI, startI + searchString.length(), Spannable.SPAN_COMPOSING);
                                    text.setSpan(bss, startI, startI + searchString.length(), Spannable.SPAN_COMPOSING);

                                    holder.searchText.setText(text);

                                    if(ClipboardType.isPhone(ci.value)){
                                        ((ImageView) holder.itemView.findViewById(R.id.icon_search_contact_adapter)).setImageDrawable(contactsFragment.getResources().getDrawable(R.drawable.icn_phone));
                                        holder.itemView.findViewById(R.id.icon_search_contact_adapter).setVisibility(View.VISIBLE);
                                    }else if(ClipboardType.isFacebook(ci.value)){
                                        ((ImageView) holder.itemView.findViewById(R.id.icon_search_contact_adapter)).setImageDrawable(contactsFragment.getResources().getDrawable(R.drawable.icn_social_facebook));
                                        holder.itemView.findViewById(R.id.icon_search_contact_adapter).setVisibility(View.VISIBLE);
                                    }else if(ClipboardType.isInsta(ci.value)){
                                        ((ImageView) holder.itemView.findViewById(R.id.icon_search_contact_adapter)).setImageDrawable(contactsFragment.getResources().getDrawable(R.drawable.instagram_adapter));
                                        holder.itemView.findViewById(R.id.icon_search_contact_adapter).setVisibility(View.VISIBLE);
                                    }else if(ClipboardType.isLinkedIn(ci.value)){
                                        ((ImageView) holder.itemView.findViewById(R.id.icon_search_contact_adapter)).setImageDrawable(contactsFragment.getResources().getDrawable(R.drawable.icn_social_linkedin));
                                        holder.itemView.findViewById(R.id.icon_search_contact_adapter).setVisibility(View.VISIBLE);
                                    }else if(ClipboardType.isVk(ci.value)){
                                        ((ImageView) holder.itemView.findViewById(R.id.icon_search_contact_adapter)).setImageDrawable(contactsFragment.getResources().getDrawable(R.drawable.icn_social_vk));
                                        holder.itemView.findViewById(R.id.icon_search_contact_adapter).setVisibility(View.VISIBLE);
                                    }else if(ClipboardType.isTwitter(ci.value)){
                                        ((ImageView) holder.itemView.findViewById(R.id.icon_search_contact_adapter)).setImageDrawable(contactsFragment.getResources().getDrawable(R.drawable.ic_twitter_64));
                                        holder.itemView.findViewById(R.id.icon_search_contact_adapter).setVisibility(View.VISIBLE);
                                    }else if(ClipboardType.isMedium(ci.value)){
                                        ((ImageView) holder.itemView.findViewById(R.id.icon_search_contact_adapter)).setImageDrawable(contactsFragment.getResources().getDrawable(R.drawable.medium_adapter));
                                        holder.itemView.findViewById(R.id.icon_search_contact_adapter).setVisibility(View.VISIBLE);
                                    }else if(ClipboardType.isYoutube(ci.value)){
                                        ((ImageView) holder.itemView.findViewById(R.id.icon_search_contact_adapter)).setImageDrawable(contactsFragment.getResources().getDrawable(R.drawable.ic_youtube_48));
                                        holder.itemView.findViewById(R.id.icon_search_contact_adapter).setVisibility(View.VISIBLE);
                                    }else if(ClipboardType.isTelegram(ci.value)){
                                        ((ImageView) holder.itemView.findViewById(R.id.icon_search_contact_adapter)).setImageDrawable(contactsFragment.getResources().getDrawable(R.drawable.icn_social_telegram));
                                        holder.itemView.findViewById(R.id.icon_search_contact_adapter).setVisibility(View.VISIBLE);
                                    }else if(ClipboardType.isGitHub(ci.value)){
                                        ((ImageView) holder.itemView.findViewById(R.id.icon_search_contact_adapter)).setImageDrawable(contactsFragment.getResources().getDrawable(R.drawable.github_logo_32));
                                        holder.itemView.findViewById(R.id.icon_search_contact_adapter).setVisibility(View.VISIBLE);
                                    }else if(ClipboardType.is_Tumblr(ci.value)){
                                        ((ImageView) holder.itemView.findViewById(R.id.icon_search_contact_adapter)).setImageDrawable(contactsFragment.getResources().getDrawable(R.drawable.tumblr));
                                        holder.itemView.findViewById(R.id.icon_search_contact_adapter).setVisibility(View.VISIBLE);
                                    }else if(ClipboardType.is_Angel(ci.value)){
                                        ((ImageView) holder.itemView.findViewById(R.id.icon_search_contact_adapter)).setImageDrawable(contactsFragment.getResources().getDrawable(R.drawable.angel));
                                        holder.itemView.findViewById(R.id.icon_search_contact_adapter).setVisibility(View.VISIBLE);
                                    }else if(ClipboardType.isEmail(ci.value)){
                                        ((ImageView) holder.itemView.findViewById(R.id.icon_search_contact_adapter)).setImageDrawable(contactsFragment.getResources().getDrawable(R.drawable.icn_bottombar_emails_blue));
                                        holder.itemView.findViewById(R.id.icon_search_contact_adapter).setVisibility(View.VISIBLE);
                                    }else if(ClipboardType.isG_Sheet(ci.value)){
                                        ((ImageView) holder.itemView.findViewById(R.id.icon_search_contact_adapter)).setImageDrawable(contactsFragment.getResources().getDrawable(R.drawable.google_sheets));
                                        holder.itemView.findViewById(R.id.icon_search_contact_adapter).setVisibility(View.VISIBLE);
                                    }else if(ClipboardType.isG_Doc(ci.value)){
                                        ((ImageView) holder.itemView.findViewById(R.id.icon_search_contact_adapter)).setImageDrawable(contactsFragment.getResources().getDrawable(R.drawable.google_docs));
                                        holder.itemView.findViewById(R.id.icon_search_contact_adapter).setVisibility(View.VISIBLE);
                                    }else if(ClipboardType.isWeb(ci.value)){
                                        ((ImageView) holder.itemView.findViewById(R.id.icon_search_contact_adapter)).setImageDrawable(contactsFragment.getResources().getDrawable(R.drawable.icn_popup_web_blue));
                                        holder.itemView.findViewById(R.id.icon_search_contact_adapter).setVisibility(View.VISIBLE);
                                    }else if(ClipboardType.isWeb(ci.value)){
                                        ((ImageView) holder.itemView.findViewById(R.id.icon_search_contact_adapter)).setImageDrawable(contactsFragment.getResources().getDrawable(R.drawable.icn_notes));
                                        holder.itemView.findViewById(R.id.icon_search_contact_adapter).setVisibility(View.VISIBLE);
                                    }

                                }
                                count_data++;
                            }
                        }
                    }

                    if(count_data > 1){
                        holder.searchTextCount.setText(String.valueOf(count_data));
                        holder.searchTextCount.setVisibility(View.VISIBLE);
                    }

                    setMargins(holder.linearNameIcons, 0, 0, 0, 0);
                    holder.companyBlock.setVisibility(View.GONE);
                    holder.searchBlock.setVisibility(View.VISIBLE);
                }

            }

        }


        //      boolean checkLoadImage = false;


        try {
            holder.initials.setVisibility(View.GONE);
            holder.companyImage.setVisibility(View.GONE);
            holder.contactImage.setVisibility(View.VISIBLE);
            holder.contactImage.setImageURI(Uri.parse(contact.getPhotoURL()));
            if (((BitmapDrawable) holder.contactImage.getDrawable()).getBitmap() == null) {
                holder.contactImage.setVisibility(View.VISIBLE);
                GradientDrawable circle = (GradientDrawable) ContextCompat.getDrawable(context, R.drawable.blue_circle).mutate();
                circle.setColor(contact.color);
                holder.contactImage.setBackground(circle);
                holder.contactImage.setImageDrawable(null);
                String initials = getInitials(contact);
                holder.initials.setVisibility(View.VISIBLE);
                holder.initials.setText(initials);
                if (contact.listOfContacts != null && !contact.listOfContacts.isEmpty()) {
                    holder.contactImage.setVisibility(View.GONE);
                    holder.companyImage.setVisibility(View.VISIBLE);
                    holder.companyImage.setBackgroundColor(contact.color);
                }
            }
        } catch (Exception e) {
            holder.contactImage.setVisibility(View.VISIBLE);
            GradientDrawable circle = (GradientDrawable) ContextCompat.getDrawable(context, R.drawable.blue_circle).mutate();
            circle.setColor(contact.color);
            holder.contactImage.setBackground(circle);
            holder.contactImage.setImageDrawable(null);
            String initials = getInitials(contact);
            holder.initials.setVisibility(View.VISIBLE);
            holder.initials.setText(initials);
            if (contact.listOfContacts != null && !contact.listOfContacts.isEmpty()) {
                holder.contactImage.setVisibility(View.GONE);
                holder.companyImage.setVisibility(View.VISIBLE);
                holder.companyImage.setBackgroundColor(contact.color);
            }
        }


        holder.itemView.setBackgroundColor(Color.TRANSPARENT);

        ///////////
        /*if (contact.isFavorite) {
            holder.favotiveContact.setVisibility(View.VISIBLE);
        } else holder.favotiveContact.setVisibility(View.GONE);*/

        holder.imageSelect.setVisibility(View.GONE);
        try {
            if (selectionModeEnabled)
                if (contact.isValid()
                        && selectedContacts.contains(contact)) {

                    /*if (!contactsFragment.mergedContacts)
                        contactsFragment.closeOtherPopup();*/
                    holder.itemView.setBackgroundResource(R.drawable.selected_card_bg);
                    if (contact.listOfContacts == null || contact.listOfContacts.isEmpty()) {
                        ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) holder.imageSelect.getLayoutParams();
                        p.setMargins(0, 0, 39, 36);

                        holder.imageSelect.setLayoutParams(p);
                        holder.imageSelect.setVisibility(View.VISIBLE);

                    } else {
                        ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) holder.imageSelect.getLayoutParams();
                        p.setMargins(0, 0, 27, 27);
                        holder.imageSelect.setLayoutParams(p);
                        holder.imageSelect.setVisibility(View.VISIBLE);
                    }
                }

        }catch (Exception e){
            e.printStackTrace();
        }

        holder.companyTitle.setText("");

        if (contact.listOfContacts != null && !contact.listOfContacts.isEmpty()) {
            setMargins(holder.linearNameIcons, 0, 0, 0, 0);
            holder.companyTitle.setVisibility(View.VISIBLE);
            holder.companyTitle.setText(contact.listOfContacts.size() + " contacts");
        }



        if (contact.getCompany() != null) {

            if (contact.getCompany().compareTo("") != 0) {

                setMargins(holder.linearNameIcons, 0, 0, 0, 0);
                holder.companyTitle.setVisibility(View.VISIBLE);
                holder.companyTitle.setText(contact.getCompany());
            }
        }

        if (contact.getCompanyPossition() != null) {
            if (contact.getCompanyPossition().compareTo("") != 0) {
                setMargins(holder.linearNameIcons, 0, 0, 0, 0);
                holder.companyName.setVisibility(View.VISIBLE);
                Double ems = holder.companyTitle.getText().length() / 2.5;
                int ems_count = ems.intValue();
                /*if(ems_count < 8){
                    holder.companyName.setMaxEms(6 + (8-ems_count));
                }*/

                holder.companyName.setText(contact.getCompanyPossition());
            }
        }

        if (holder.companyTitle.length() > 0 && holder.companyName.getVisibility() == View.VISIBLE) {
            Double ems = holder.companyName.getText().length() / 2.5;
            int ems_count = ems.intValue();
            /*if(ems_count < 6){
                holder.companyTitle.setMaxEms(8 + (6-ems_count));
            }*/
        }


        holder.linearDataCard.requestLayout();
        holder.userName.requestLayout();


       /* if(contact.getListOfHashtags() != null && !contact.getListOfHashtags().isEmpty()){
            for(int i = 0;i<contact.getListOfHashtags().size();i++){
                holder.hash.setText( contact.getListOfHashtags().get(i).toString());
            }
        }*/

        if (selectionModeEnabled) {

            holder.imageBlock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (contactsFragment.getQuantityOpenedViews() == 0) {

                        MainActivity.CloseStatusPopup();
                        selectedContactId = String.valueOf(contact.getId());
                        if (contact.listOfContacts != null && !contact.listOfContacts.isEmpty()) {

                            contactsFragment.showCompanyPopup(contact);

                            if (ClibpboardAdapter.checkSelectClips) {

                                /*if (contactsFragment.getActivity() == null)
                                    ((TextView) MainActivity.activityProfile.findViewById(R.id.updateContactClipboard)).setTextColor(contactsFragment.getResources().getColor(R.color.colorPrimary));
                                else*/
                                ((TextView) contactsFragment.getActivity().findViewById(R.id.updateContactClipboard)).setTextColor(contactsFragment.getResources().getColor(R.color.colorPrimary));
                            }

                        } else {
                            //contact.fillData(context, contactsFragment.contactsService, FillDataEnums.PREVIEW);

                            contactsFragment.showProfilePopUp(contact);

                            if (ClibpboardAdapter.checkSelectClips) {

                                /*if (contactsFragment.getActivity() == null)
                                    ((TextView) MainActivity.activityProfile.findViewById(R.id.updateContactClipboard)).setTextColor(contactsFragment.getResources().getColor(R.color.colorPrimary));
                                else*/
                                ((TextView) contactsFragment.getActivity().findViewById(R.id.updateContactClipboard)).setTextColor(contactsFragment.getResources().getColor(R.color.colorPrimary));
                            }

                        }
                    } else {
                        holder.itemView.setBackgroundColor(Color.TRANSPARENT);


                        if (!ClibpboardAdapter.checkUpdateClips) {

                            /*if (contactsFragment.getActivity() == null)
                                ((TextView) MainActivity.activityProfile.findViewById(R.id.updateContactClipboard)).setTextColor(contactsFragment.getResources().getColor(R.color.gray));
                            else*/
                            ((TextView) contactsFragment.getActivity().findViewById(R.id.updateContactClipboard)).setTextColor(contactsFragment.getResources().getColor(R.color.gray));
                        }
                        selectedContactId = null;
                        contactsFragment.closeOtherPopup();
                        notifyDataSetChanged();
                    }
                }
            });
            holder.itemView.setOnClickListener(v -> {

                if (contactsFragment.getQuantityOpenedViews() == 0) {
                    addContactToSelectList(holder, contact);

                } else {


                    if (!ClibpboardAdapter.checkUpdateClips) {

                        /*if (contactsFragment.getActivity() == null)
                            ((TextView) MainActivity.activityProfile.findViewById(R.id.updateContactClipboard)).setTextColor(contactsFragment.getResources().getColor(R.color.gray));
                        else*/
                        ((TextView) contactsFragment.getActivity().findViewById(R.id.updateContactClipboard)).setTextColor(contactsFragment.getResources().getColor(R.color.gray));
                    }

                    selectedContactId = null;
                    contactsFragment.closeOtherPopup();
                    notifyDataSetChanged();
                }

            });
            holder.itemView.setOnLongClickListener(v -> {

                addContactToSelectList(holder, contact);
                return false;
            });
        } else {
            holder.imageBlock.setOnClickListener(v -> {

                if (contactsFragment.
                        getQuantityOpenedViews() == 0) {

                    //contact.fillData(context, contactsFragment.contactsService, FillDataEnums.PROFILE);


                    if(((EditText)contactsFragment.getActivity().findViewById(R.id.magic_edit_text)).getText().length() > 0){

                        EventBus.getDefault().post(new AddHistoryEntry(contact.getName()));
                    }

                    android.support.v4.app.FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();

                    android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    ProfileFragment profileFragment = ProfileFragment.newInstance(contact, false);
                    if(!profileFragment.isAdded()) {
                        fragmentTransaction.replace(R.id.main_content, profileFragment).addToBackStack("contacts").commit();
                    }
                    //contactAd = this;


                    if (contactsFragment.profilePopUp != null && contactsFragment.profilePopUp.getVisibility() == View.VISIBLE) {
                        contactsFragment.profilePopUp.setVisibility(View.INVISIBLE);
                    }
                } else {

                    selectedContactId = null;
                    contactsFragment.closeOtherPopup();
                    notifyDataSetChanged();
                }
            });


            holder.itemView.setOnClickListener(v -> {

                if (contactsFragment.getQuantityOpenedViews() == 0 && contactsFragment.getActivity().findViewById(R.id.popupInfoClipboard).getVisibility() == View.GONE && contactsFragment.getActivity().findViewById(R.id.popupEditClip).getVisibility() == View.GONE
                && contactsFragment.getActivity().findViewById(R.id.popupChangeType).getVisibility() == View.GONE) {

                    MainActivity.CloseStatusPopup();
                    if (contact.listOfContacts != null && !contact.listOfContacts.isEmpty()) {
                        selectedContactId = contact.getName();
                        contactsFragment.showCompanyPopup(contact);

                        if (ClibpboardAdapter.checkSelectClips) {

                            /*if (contactsFragment.getActivity() == null)
                                ((TextView) MainActivity.activityProfile.findViewById(R.id.updateContactClipboard)).setTextColor(contactsFragment.getResources().getColor(R.color.colorPrimary));
                            else*/
                            ((TextView) contactsFragment.getActivity().findViewById(R.id.updateContactClipboard)).setTextColor(contactsFragment.getResources().getColor(R.color.colorPrimary));
                        }

                    } else {
                        //contact.fillData(context, contactsFragment.contactsService, FillDataEnums.PREVIEW);
                        selectedContactId = String.valueOf(contact.getId());
                        contactsFragment.showProfilePopUp(contact);

                        if (ClibpboardAdapter.checkSelectClips) {

                            /*if (contactsFragment.getActivity() == null)
                                ((TextView) MainActivity.activityProfile.findViewById(R.id.updateContactClipboard)).setTextColor(contactsFragment.getResources().getColor(R.color.colorPrimary));
                            else*/
                            ((TextView) contactsFragment.getActivity().findViewById(R.id.updateContactClipboard)).setTextColor(contactsFragment.getResources().getColor(R.color.colorPrimary));
                        }

                    }

                    holder.itemView.setBackgroundColor(Color.parseColor("#E3E3E3"));
                } else {
                    contactsFragment.getActivity().findViewById(R.id.popupInfoClipboard).setVisibility(View.GONE);
                    //contactsFragment.getActivity().findViewById(R.id.popupInfoLinkClipboard).setVisibility(View.GONE);
                    contactsFragment.getActivity().findViewById(R.id.popupEditClip).setVisibility(View.GONE);
                    contactsFragment.getActivity().findViewById(R.id.popupChangeType).setVisibility(View.GONE);

                    EventBus.getDefault().post(new UpdateCountClipboard());

                    if (contactsFragment.popupUserHashtags != null) {
                        if (contactsFragment.popupUserHashtags.getVisibility() == View.VISIBLE) {
                            contactsFragment.popupUserHashtags.setVisibility(View.GONE);
                        } else {
                            contactsFragment.closeOtherPopup();
                            notifyDataSetChanged();
                        }
                    } else {

                        //findViewById(R.id.framePopupSocial).setVisibility(View.GONE);

                        if (!ClibpboardAdapter.checkUpdateClips) {

                            /*if (contactsFragment.getActivity() == null)
                                ((TextView) MainActivity.activityProfile.findViewById(R.id.updateContactClipboard)).setTextColor(contactsFragment.getResources().getColor(R.color.gray));
                            else*/
                            ((TextView) contactsFragment.getActivity().findViewById(R.id.updateContactClipboard)).setTextColor(contactsFragment.getResources().getColor(R.color.gray));
                        }

                        selectedContactId = null;
                    }
                    contactsFragment.closeOtherPopup();
                    notifyDataSetChanged();
                }
            });
        }



        holder.imageBlock.setOnLongClickListener(v -> {
           /* if (!selectionModeEnabled) {
                contactsFragment.startSelectionMode();
                selectedContacts.add(contact);
                countSelectedCompany = 0;
                countSelectedContacts = 0;
                holder.itemView.setBackgroundColor(Color.TRANSPARENT);
                if(contact.listOfContacts != null) ++countSelectedCompany;
                else ++countSelectedContacts;
                checkMergeCount();
                changeContactsCountBar();
            }
            return true;*/

            if (!selectionModeEnabled) {

                contactsFragment.startSelectionMode();
                selectedContacts.add(contact);
                countSelectedCompany = 0;
                countSelectedContacts = 0;
                holder.itemView.setBackgroundColor(Color.TRANSPARENT);
                if (contact.listOfContacts != null && !contact.listOfContacts.isEmpty())
                    ++countSelectedCompany;
                else {
                    ++countSelectedContacts;
                    //    holder.imageSelect.setVisibility(View.VISIBLE);
                }
                checkMergeCount();
                //changeContactsCountBar();

                // checkMerge = true;
                startNewSelection();


            }
            return true;
        });

        holder.itemView.setOnLongClickListener(v -> {

            if (!selectionModeEnabled) {

                contactsFragment.startSelectionMode();
                selectedContacts.add(contact);
                countSelectedCompany = 0;
                countSelectedContacts = 0;
                holder.itemView.setBackgroundColor(Color.TRANSPARENT);
                if (contact.listOfContacts != null && !contact.listOfContacts.isEmpty())
                    ++countSelectedCompany;
                else {
                    ++countSelectedContacts;
                    //    holder.imageSelect.setVisibility(View.VISIBLE);
                }

                checkMergeCount();
                //changeContactsCountBar();

                startNewSelection();


            }
            return true;
        });

    }


    public void startNewSelection() {
        checkMerge = true;

        ((Postman) contactsFragment.getActivity()).getMenu().getItem(0).setVisible(false);
        ((Postman) contactsFragment.getActivity()).getMenu().getItem(2).setVisible(true);
        ((Postman) contactsFragment.getActivity()).getMenu().getItem(3).setVisible(true);
        ((Postman) contactsFragment.getActivity()).getMenu().getItem(4).setVisible(true);


        toolbarC.findViewById(R.id.toolbar_title).setVisibility(View.GONE);
        ((TextView) toolbarC.findViewById(R.id.cancel_toolbar)).setTextSize(18);
        ((TextView) toolbarC.findViewById(R.id.cancel_toolbar)).setText("" + selectedContacts.size() + "");
        toolbarC.findViewById(R.id.cancel_toolbar).setVisibility(View.VISIBLE);
        toolbarC.setNavigationIcon(R.drawable.icn_arrow_back);



       /* View view = contactsFragment.getLayoutInflater().inflate(R.layout.social_insta, null);
        FrameLayout chbox = view.findViewById(R.id.socialInstas);

        toolbarC.addView(chbox);*/

        toolbarC.findViewById(R.id.toolbar_kanban_clicker).setVisibility(View.GONE);

        toolbarC.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopNEWSelection();
            }
        });
    }

    public void updateCountSelected() {
        ((TextView) toolbarC.findViewById(R.id.cancel_toolbar)).setText("" + selectedContacts.size() + "");
    }

    public void stopNEWSelection() {
        try {
            ((Postman) contactsFragment.getActivity()).getMenu().getItem(0).setVisible(true);
            ((Postman) contactsFragment.getActivity()).getMenu().getItem(2).setVisible(false);
            ((Postman) contactsFragment.getActivity()).getMenu().getItem(3).setVisible(false);
            ((Postman) contactsFragment.getActivity()).getMenu().getItem(4).setVisible(false);


        } catch (Exception e) {
            e.printStackTrace();
        }


        toolbarC.setNavigationIcon(R.drawable.icn_menu);


        toolbarC.findViewById(R.id.toolbar_kanban_clicker).setVisibility(View.VISIBLE);

        toolbarC.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EventBus.getDefault().post(new OpenDriver());
                EventBus.getDefault().post(new OpenDriver());
            }


        });

        toolbarC.findViewById(R.id.toolbar_title).setVisibility(View.VISIBLE);

        ((TextView) toolbarC.findViewById(R.id.cancel_toolbar)).setTextSize(14);
        ((TextView) toolbarC.findViewById(R.id.cancel_toolbar)).setVisibility(View.GONE);
        contactsFragment.stopSelectionMode();
        ((TextView) toolbarC.findViewById(R.id.cancel_toolbar)).setText("CANCEL");
    }

    private void addContactToSelectList(ContactViewHolder holder, Contact contact) {
        if (selectedContacts.contains(contact)) {
            if (contact.listOfContacts != null && !contact.listOfContacts.isEmpty())
                --countSelectedCompany;
            else {
                --countSelectedContacts;
                holder.imageSelect.setVisibility(View.GONE);
            }
            checkMergeCount();
            selectedContacts.remove(contact);
        } else {
            if (contact.listOfContacts != null && !contact.listOfContacts.isEmpty())
                ++countSelectedCompany;
            else {
                ++countSelectedContacts;

            }
            //  holder.itemView.setBackgroundResource(R.drawable.selected_card_bg);
            /*Animation animation = AnimationUtils.loadAnimation(context,R.anim.select_icon);
            holder.imageSelect.setAnimation(animation);
            holder.imageSelect.setVisibility(View.VISIBLE);*/
            checkMergeCount();
            selectedContacts.add(contact);
        }
        //changeContactsCountBar();
        notifyDataSetChanged();

        updateCountSelected();

        if (selectedContacts.size() == 0) {
            // contactsFragment.stopSelectionMode();

            stopNEWSelection();
        }
    }

    public void mergeContacts() {

        ContactsService contactsService = new ContactsService(mainView.getContext().getContentResolver(), false);
        switch (typeMerge) {
            case CONTACTS_MERGE: {

//                Contact mainContactForMerge = selectedContacts.get(0);
//                for (int indexI = 1; indexI < selectedContacts.size(); indexI++){
//                    Contact selectedContact = selectedContacts.get(indexI);
//                    if(selectedContact.hasFacebook)
//                        mainContactForMerge.hasFacebook = true;
//                    if(selectedContact.hasLinked)
//                        mainContactForMerge.hasLinked = true;
//                    if(selectedContact.hasInst)
//                        mainContactForMerge.hasInst = true;
//                    if(selectedContact.hashtag)
//                        mainContactForMerge.hashtag = true;
//                    if(selectedContact.hasSkype)
//                        mainContactForMerge.hasSkype = true;
//                    if(selectedContact.hasViber)
//                        mainContactForMerge.hasViber = true;
//                    if(selectedContact.hasWhatsapp)
//                        mainContactForMerge.hasWhatsapp = true;
//                    if(selectedContact.hasTelegram)
//                        mainContactForMerge.hasTelegram = true;
//                    for (ContactInfo contactNumber:selectedContact.listOfContactInfo) {
//                        if (contactNumber.isEmail){
//                            contactsService.addMailToContact(mainContactForMerge.getId(), contactNumber.value);
//                            mainContactForMerge.addEmail(contactNumber.value);
//                        }
//                        if (contactNumber.isGeo){
//                            if (contactNumber.getNewValue() != null) {
//                                contactsService.updateLocation(profileFragment.selectedContact.getId(),contactNumber.value, contactNumber.getNewValue());
//                                contactNumber.value = contactNumber.getNewValue();
                //mainContactForMerge.addEmail(contactNumber.value);
//                            }
//                        }
//                        if (contactNumber.isPhone) {
//                            contactsService.addPhoneToContact(mainContactForMerge.getId(), contactNumber.value);
//                            mainContactForMerge.addPhone(contactNumber.value);
//                        }
//                        if (contactNumber.isNote) {
//                            contactsService.addNoteToContact(mainContactForMerge.getId(), contactNumber.value);
//                            mainContactForMerge.addNote(contactNumber.value);
//                        }
                //   }
//                    contactsService.deleteContact(selectedContact);
//                    savedContacts.remove(selectedContact);
//                }
//                ContactCacheService.updateCacheContacts(savedContacts,mainView.getContext());
//                defaultListContacts();

                //contactAd = this;
                checkMerge = false;
                checkMergeContacts = true;

                contactsFragment.closeOtherPopup();


                android.support.v4.app.FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                CreateFragment createFragment = CreateFragment.newInstance(selectedContacts);
                fragmentTransaction.replace(R.id.main_content, createFragment).addToBackStack("contacts").commit();

                contactsFragment.hideSelectMenu();

                checkFoActionIcon = true;

                /*((TextView) MainActivity.mainToolBar.findViewById(R.id.cancel_toolbar)).setVisibility(View.VISIBLE);
                ((TextView) MainActivity.mainToolBar.findViewById(R.id.cancel_toolbar)).setText("CANCEL");
                MainActivity.mainToolBar.setNavigationIcon(null);*/
                // MainActivity.MAIN_MENU.getItem(2).setVisible(false);
                // MainActivity.MAIN_MENU.getItem(3).setVisible(false);


                // stopSelectionMode();

                //   contactsFragment.stopSelectionMode();

//                Toast toast = Toast.makeText(mainView.getContext(), "Merge succeed", Toast.LENGTH_SHORT);
//                toast.show();
                break;
//            }
            }
            case CONTACTS_IN_COMPANY_MERGE: {

                Contact companis = null;
                ArrayList<Contact> selectListForCheck = new ArrayList<>();
                selectListForCheck.addAll(selectedContacts);
                for (int i = 0; i < selectListForCheck.size(); i++) {

                    if (selectListForCheck.get(i).listOfContacts != null && !selectListForCheck.get(i).listOfContacts.isEmpty()) {
                        companis = selectListForCheck.get(i);
                        selectListForCheck.remove(i);

                        break;
                    }
                }

                if (companis != null) {
                    for (int i = 0; i < selectListForCheck.size(); i++) {

                       /* for (Contact contact : companis.listOfContacts) {
                            if (selectListForCheck.get(i).getId() == contact.getId()) {
                                selectListForCheck.remove(i);
                                if (i <= selectListForCheck.size() - 1) i--;
                                break;
                            }
                        }*/

                        if (companis.listOfContacts.contains(selectListForCheck.get(i))) {
                            selectListForCheck.remove(i);
                            if (i <= selectListForCheck.size() - 1) i--;
                        }


                    }

                    if (selectListForCheck.size() == 0) {
                        Toast.makeText(context, "The contact is already in this company", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        mainView.getContext());
                alertDialogBuilder.setTitle("Do you want add contacts to company?");
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Yes", (dialog, id) -> {
                            Contact company = null;
                            for (int indexI = 0; indexI < selectedContacts.size(); indexI++) {
                                Contact contact = selectedContacts.get(indexI);
                                if (contact != null)
                                    if (contact.listOfContacts != null && !contact.listOfContacts.isEmpty()) {
                                        company = contact;
                                        selectedContacts.remove(contact);

                                        indexI--;
                                        continue;
                                        //break;
                                    }
                            }


                            for (int indexI = 0; indexI < selectedContacts.size(); indexI++) {
                                if (!company.listOfContacts.contains(selectedContacts.get(indexI))) {
                                    Contact contact = selectedContacts.get(indexI);



                                    //contactsService.updateCompany(contact.getIdContact(), contact.getCompany(), company.getName());



                                    contactsService.deleteCompany_Possition(contact.getIdContact());
                                    //if (company.getName() != null && !company.getCompany().isEmpty())
                                    contactsService.insertCompany(contact.getIdContact(), company.getName(), contact.getName());

                                    if (contact.getCompanyPossition() != null && !contact.getCompanyPossition().isEmpty() && (contact.getCompany() != null && !contact.getCompany().isEmpty()))
                                        contactsService.addComp(contact.getIdContact(), contact.getCompanyPossition(), company.getName());

                                    if (contact.getCompanyPossition() != null && !contact.getCompanyPossition().isEmpty() && (contact.getCompany() == null || contact.getCompany().isEmpty()))
                                        contactsService.insertPosition(contact.getIdContact(), contact.getCompanyPossition(), contact.getName());



                                    String nameCompany = null;
                                    if (contact.getCompany() != null && !contact.getCompany().isEmpty())
                                        nameCompany = contact.getCompany();

                               /*     if(contact.getCompany() != null && !contact.getCompany().isEmpty()){

                                     nameCompany = contact.getCompany();

                                     Contact companuCheck = ContactCacheService.getCompany(nameCompany);

                                            ch = ContactCacheService.removeContactFromCompany(companuCheck, contact);

                                }*/


                                    Realm realm = Realm.getDefaultInstance(); //-

                                    realm.beginTransaction();
                                    contact.setCompany(company.getName());
                                    realm.commitTransaction();
                                    //ContactCacheService.updateContact(contact, null);
                                    realm.beginTransaction();
                                    company.listOfContacts.add(contact);
                                    realm.commitTransaction();
                                    //ContactCacheService.updateCompany(company);

                                    realm.close();

                                    //\\\\\\\ search company and remove

                                    for (int i = 0; i < savedContacts.size(); i++) {
                                        if (selectedContacts != null)
                                            /*if (savedContacts.get(i) == selectedContacts.get(indexI) || savedContacts.get(i).equals(selectedContacts.get(indexI))) {

                                                savedContacts.set(i, contact);
                                                continue;
                                            }*/
                                            if (savedContacts.get(i).getName().equals(nameCompany) || savedContacts.get(i).getName() == nameCompany) {
                                                if (contact.getCompany() != null && !contact.getCompany().isEmpty()) {

                                                    Contact companuCheck = ContactCacheService.getCompany(nameCompany);

                                                    if (companuCheck != null && companuCheck.listOfContacts.size() == 1) {
                                                        removeContactById(companuCheck);
                                                        //listOfContacts.remove(companuCheck);
                                                        //savedContacts.remove(companuCheck);
                                                        i--;

                                                    } else continue;
                                                    ContactCacheService.removeContactFromCompany(companuCheck, contact);

                                                }
                                            /*if (ch) {
                                                savedContacts.remove(i);
                                                i--;
                                                contactsFragment.updateCountAfterRemoveCompany();


                                            } else {

                                                Contact contact1 = ContactCacheService.getCompany(nameCompany);
                                                savedContacts.set(i, contact1);


                                            }*/
                                        /*else
                                        savedContacts.set(i,cmn);*/

                                            }
                                    }

                                    ///============================== end search company


                                    //  notifyDataSetChanged();
                                }

                            }

                            //     contactsFragment.checkAll();
                            //((CheckBox) contactsFragment.getActivity().findViewById(R.id.all_fav_check)).setChecked(true);

                            savedContacts = ContactCacheService.getAllContacts(null);

                            //  ContactCacheService.updateCacheContacts(savedContacts,mainView.getContext());
                            //((LinearLayout) contactsFragment.getActivity().findViewById(R.id.allContactsLL)).callOnClick();

                            notifyDataSetChanged();

                            //EventBus.getDefault().post(new UpdateFile());
                            //defaultListContacts();
                            //stopSelectionMode();
                            stopNEWSelection();
                            //contactsFragment.contactAdapter.stopNEWSelection();

                            //contactsFragment.inicCaontactBar
                            Toast toast = Toast.makeText(mainView.getContext(), "Merge success", Toast.LENGTH_SHORT);
                            toast.show();
                        })
                        .setNegativeButton("No", (dialog, id) -> dialog.cancel());

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

                break;
            }
            case COMPANY_MERGE: {
                /*RealmConfiguration contextRealm = new RealmConfiguration.
                        Builder().
                        deleteRealmIfMigrationNeeded().
                        build();
                Realm realm = Realm.getInstance(contextRealm);


                Contact company = selectedContacts.get(0);
                for (int indexI = 1; indexI < selectedContacts.size(); indexI++) {
                    Contact updatedCompany = selectedContacts.get(indexI);
                    for (Contact contact : savedContacts) {
                        if (contact.getCompany() != null) {
                            if (contact.getCompany().compareTo(updatedCompany.getName()) == 0) {
                                contactsService.updateCompany(contact.getIdContact(), updatedCompany.getName(), company.getName());
                                realm.beginTransaction();
                                contact.setCompany(company.getName());

                                company.listOfContacts.add(contact);
                                realm.commitTransaction();
                            }
                        }
                    }
                    savedContacts.remove(updatedCompany);
                    listOfContacts.remove(updatedCompany);
                }
                // ContactCacheService.updateCacheContacts(savedContacts,mainView.getContext());
                EventBus.getDefault().post(new UpdateFile());
                defaultListContacts();
                stopSelectionMode();
                contactsFragment.stopSelectionMode();
                android.support.v4.app.FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_content, ProfileFragment.newInstance(company, false)).addToBackStack("contacts").commit();
                Toast toast = Toast.makeText(mainView.getContext(), "Merge succeed", Toast.LENGTH_SHORT);
                toast.show();*/
                break;
            }
            case NONE_COMPANY: {
                Toast toast = Toast.makeText(mainView.getContext(), "Please leave one company", Toast.LENGTH_SHORT);
                toast.show();
                break;
            }
            case NONE_CONTACTS: {
                Toast toast = Toast.makeText(mainView.getContext(), "Please select more than one contact", Toast.LENGTH_SHORT);
                toast.show();
                break;
            }
        }
    }

    public void checkMergeCount() {
        ((TextView) contactsFragment.getActivity().findViewById(R.id.mergeTxt)).setText("Merge");
        ((TextView) contactsFragment.getActivity().findViewById(R.id.mergeTxt)).setTextColor(mainView.getContext().getResources().getColor(R.color.colorPrimary));

        ((ImageView) contactsFragment.getActivity().findViewById(R.id.mergeIcon)).setColorFilter(mainView.getContext().getResources().getColor(R.color.colorPrimary));
        ((ImageView) contactsFragment.getActivity().findViewById(R.id.mergeIcon)).setImageDrawable(contactsFragment.getActivity().getResources().getDrawable(R.drawable.icn_sort_merge));

        ImageView ivVectorImage = (ImageView) contactsFragment.getActivity().findViewById(R.id.mergeIcon);
        ivVectorImage.setColorFilter(mainView.getContext().getResources().getColor(R.color.colorPrimary));

        ((TextView) contactsFragment.getActivity().findViewById(R.id.editTextSelectMenu)).setText("Edit");
        ((TextView) contactsFragment.getActivity().findViewById(R.id.editTextSelectMenu)).setTextColor(mainView.getContext().getResources().getColor(R.color.colorPrimary));

        ((ImageView) contactsFragment.getActivity().findViewById(R.id.editIconSelect)).setColorFilter(mainView.getContext().getResources().getColor(R.color.colorPrimary));
        ((ImageView) contactsFragment.getActivity().findViewById(R.id.editIconSelect)).setImageDrawable(contactsFragment.getActivity().getResources().getDrawable(R.drawable.icn_popup_pencil));

        //ImageView ivVectorImage = (ImageView) contactsFragment.getActivity().findViewById(R.id.mergeIcon);
        //ivVectorImage.setColorFilter(mainView.getContext().getResources().getColor(R.color.colorPrimary));


        //editIconSelect


        if (countSelectedCompany > 2 && countSelectedContacts == 0) {
        }

        if (countSelectedContacts > 0 && countSelectedCompany == 1) {
            ((TextView) contactsFragment.getActivity().findViewById(R.id.mergeTxt)).setText("Join");
            typeMerge = TYPE_MERGE.CONTACTS_IN_COMPANY_MERGE;
        }
        if (countSelectedContacts > 2 && countSelectedCompany > 2) {
            typeMerge = TYPE_MERGE.NONE_COMPANY;
        }
        if (countSelectedContacts > 1 && countSelectedCompany == 0) {
            typeMerge = TYPE_MERGE.CONTACTS_MERGE;
            ((TextView) contactsFragment.getActivity().findViewById(R.id.mergeTxt)).setText("Merge");
        }
        if (countSelectedCompany > 1) {
            typeMerge = TYPE_MERGE.NONE_COMPANY;
            ((TextView) contactsFragment.getActivity().findViewById(R.id.mergeTxt)).setTextColor(mainView.getContext().getResources().getColor(R.color.gray));
            ((TextView) contactsFragment.getActivity().findViewById(R.id.mergeTxt)).setText("Join");
            ivVectorImage = (ImageView) contactsFragment.getActivity().findViewById(R.id.mergeIcon);
            ivVectorImage.setColorFilter(mainView.getContext().getResources().getColor(R.color.gray));

        }
        if (countSelectedContacts == 1 && countSelectedCompany == 0 || countSelectedContacts == 0 && countSelectedCompany == 1) {
            typeMerge = TYPE_MERGE.NONE_CONTACTS;
            ((TextView) contactsFragment.getActivity().findViewById(R.id.mergeTxt)).setTextColor(mainView.getContext().getResources().getColor(R.color.gray));
            ivVectorImage = (ImageView) contactsFragment.getActivity().findViewById(R.id.mergeIcon);
            ivVectorImage.setColorFilter(mainView.getContext().getResources().getColor(R.color.gray));
        }
        if (countSelectedCompany > 1 && countSelectedContacts == 0) {
            typeMerge = TYPE_MERGE.COMPANY_MERGE;
            ivVectorImage = (ImageView) contactsFragment.getActivity().findViewById(R.id.mergeIcon);
            ivVectorImage.setColorFilter(mainView.getContext().getResources().getColor(R.color.colorPrimary));

        }
        if (countSelectedContacts == 1 && countSelectedCompany == 0) {

            ((TextView) contactsFragment.getActivity().findViewById(R.id.editTextSelectMenu)).setTextColor(mainView.getContext().getResources().getColor(R.color.colorPrimary));
            ivVectorImage = (ImageView) contactsFragment.getActivity().findViewById(R.id.editIconSelect);
            ivVectorImage.setColorFilter(mainView.getContext().getResources().getColor(R.color.colorPrimary));
        } else {
            ((TextView) contactsFragment.getActivity().findViewById(R.id.editTextSelectMenu)).setTextColor(mainView.getContext().getResources().getColor(R.color.gray));
            ivVectorImage = (ImageView) contactsFragment.getActivity().findViewById(R.id.editIconSelect);
            ivVectorImage.setColorFilter(mainView.getContext().getResources().getColor(R.color.gray));
        }
    }

    private String getUpdTime(Date time, Time times) {
        /*String timeStr = time.getHours() + ":";
        if (time.getMinutes() < 10) timeStr += "0";
        timeStr += time.getMinutes();*/

        Calendar current = Calendar.getInstance();
        Calendar contactDate = Calendar.getInstance();
        current.setTime(currentDate);
        contactDate.setTime(time);
        String timeStr = "";
        if (current.get(Calendar.YEAR) == contactDate.get(Calendar.YEAR) && current.get(Calendar.MONTH) == contactDate.get(Calendar.MONTH) && current.get(Calendar.DAY_OF_MONTH) == contactDate.get(Calendar.DAY_OF_MONTH)) {

            timeStr = times.getHours() + ":";
            if (times.getMinutes() < 10) timeStr += "0";
            timeStr += times.getMinutes();
        } else if (current.get(Calendar.YEAR) == contactDate.get(Calendar.YEAR) && (current.get(Calendar.MONTH) != contactDate.get(Calendar.MONTH) || current.get(Calendar.DAY_OF_MONTH) != contactDate.get(Calendar.DAY_OF_MONTH))) {

            timeStr = contactDate.get(Calendar.DAY_OF_MONTH) + " " + contactDate.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.ENGLISH);
        } else {
            timeStr = contactDate.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.ENGLISH) + " " + String.valueOf(contactDate.get(Calendar.YEAR)).substring(2);

        }
        //timeStr = contactDate.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.ENGLISH) + " " + String.valueOf(contactDate.get(Calendar.YEAR)).substring(2);

        return timeStr;
    }

    private void changeContactsCountBar() {
        contactsFragment.getActivity().findViewById(R.id.contactsText).setVisibility(View.VISIBLE);
        ((TextView) contactsFragment.getActivity().findViewById(R.id.quantity_selected_contacts)).setText(String.valueOf(selectedContacts.size()));
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

    public void startSelectionMode() {

        contactsFragment.closeOtherPopup();
        selectionModeEnabled = true;
        selectedContacts = new ArrayList<>();
        notifyDataSetChanged();
    }

    public void stopSelectionMode() {

        selectedContacts = new ArrayList<>();

        if (listOfSaveContact != null && !listOfSaveContact.isEmpty()) {
            listOfContacts.clear();
            listOfContacts.addAll(listOfSaveContact);
            listOfSaveContact.clear();
        }
        selectionModeEnabled = false;


        contactsFragment.closeOtherPopup();
        notifyDataSetChanged();
    }

    public void stopSelectionModeNew() {
        selectedContacts = new ArrayList<>();
        selectionModeEnabled = false;
        //  defaultListContacts();
        //  defaultListContactsWithoutSort();

        contactsFragment.closeOtherPopup();
        notifyDataSetChanged();

    }

    public int contactsCount() {
        int count = 0;
        for (Contact contact : savedContacts) {
            if (contact.listOfContacts == null) count++;
        }
        return count;
    }

    public void showContacts() {

        listOfContacts.clear();
        for (Contact contact : savedContacts) {
            if (contact.listOfContacts == null || contact.listOfContacts.isEmpty()) //*&& !listOfContacts.contains(contact)*//*){
                listOfContacts.add(contact);
        }


        notifyDataSetChanged();
    }

    public void addContacts() {
        listOfContacts.addAll(ContactCacheService.getContacts());
        //sortByAsc();
        sortContacts();
        notifyDataSetChanged();

    }

    public void removeFinished(){

        if(!contactsFragment.HASHTAG_ADAPTER.allHashtagsCheck && contactsFragment.getActivity().findViewById(R.id.frame_select_bar).getVisibility() == View.VISIBLE){
            for(int i = 0;i<listHashSave.size(); i++){
                if(listHashSave.get(i).isFinished){
                    if(listOfContacts.contains(listHashSave.get(i))) {
                        listOfContacts.remove(listHashSave.get(i));
                        i--;
                    }
                }
            }
        }else{
            ArrayList<Contact> listFF = ContactCacheService.getFinished();
            for(int i = 0; i< listFF.size(); i++){
                if(listOfContacts.contains(listFF.get(i))){
                    listOfContacts.remove(listFF.get(i));
                    i--;
                }
            }

        }

        sortContacts();

        if (!((EditText) contactsFragment.getActivity().findViewById(R.id.magic_edit_text)).getText().toString().isEmpty()) {
            searchByStr(((EditText) contactsFragment.getActivity().findViewById(R.id.magic_edit_text)).getText().toString());
        }

        notifyDataSetChanged();
    }

    public void addFinished(){

        if(!contactsFragment.HASHTAG_ADAPTER.allHashtagsCheck && contactsFragment.getActivity().findViewById(R.id.frame_select_bar).getVisibility() == View.VISIBLE){
            for(int i = 0;i<listHashSave.size(); i++){
                if(listHashSave.get(i).isFinished){
                    if(!listOfContacts.contains(listHashSave.get(i)))
                        listOfContacts.add(listHashSave.get(i));
                    //i--;
                }
            }
        }else{
            for(Contact c : ContactCacheService.getFinished()){
                if(!listOfContacts.contains(c)) listOfContacts.add(c);
            }
        }

        sortContacts();

        if (!((EditText) contactsFragment.getActivity().findViewById(R.id.magic_edit_text)).getText().toString().isEmpty()) {
            searchByStr(((EditText) contactsFragment.getActivity().findViewById(R.id.magic_edit_text)).getText().toString());
        }

        notifyDataSetChanged();
    }

    public void addCrown(){

        if(!contactsFragment.HASHTAG_ADAPTER.allHashtagsCheck && contactsFragment.getActivity().findViewById(R.id.frame_select_bar).getVisibility() == View.VISIBLE){
            for(int i = 0;i<listHashSave.size(); i++){
                if(listHashSave.get(i).isCrown){
                    if(!listOfContacts.contains(listHashSave.get(i)))
                        listOfContacts.add(listHashSave.get(i));
                    //i--;
                }
            }
        }else{
            for(Contact c : ContactCacheService.getCrown()){
                if(!listOfContacts.contains(c)) listOfContacts.add(c);
            }
        }

        sortContacts();

        if (!((EditText) contactsFragment.getActivity().findViewById(R.id.magic_edit_text)).getText().toString().isEmpty()) {
            searchByStr(((EditText) contactsFragment.getActivity().findViewById(R.id.magic_edit_text)).getText().toString());
        }

        notifyDataSetChanged();
    }

    public void addVip(){

        if(!contactsFragment.HASHTAG_ADAPTER.allHashtagsCheck && contactsFragment.getActivity().findViewById(R.id.frame_select_bar).getVisibility() == View.VISIBLE){
            for(int i = 0;i<listHashSave.size(); i++){
                if(listHashSave.get(i).isVip){
                    if(!listOfContacts.contains(listHashSave.get(i)))
                        listOfContacts.add(listHashSave.get(i));
                    //i--;
                }
            }
        }else{
            for(Contact c : ContactCacheService.getVip()){
                if(!listOfContacts.contains(c)) listOfContacts.add(c);
            }
        }

        sortContacts();

        if (!((EditText) contactsFragment.getActivity().findViewById(R.id.magic_edit_text)).getText().toString().isEmpty()) {
            searchByStr(((EditText) contactsFragment.getActivity().findViewById(R.id.magic_edit_text)).getText().toString());
        }

        notifyDataSetChanged();
    }

    public void addStartup(){

        if(!contactsFragment.HASHTAG_ADAPTER.allHashtagsCheck && contactsFragment.getActivity().findViewById(R.id.frame_select_bar).getVisibility() == View.VISIBLE){
            for(int i = 0;i<listHashSave.size(); i++){
                if(listHashSave.get(i).isStartup){
                    if(!listOfContacts.contains(listHashSave.get(i)))
                        listOfContacts.add(listHashSave.get(i));
                    //i--;
                }
            }
        }else{
            for(Contact c : ContactCacheService.getStartup()){
                if(!listOfContacts.contains(c)) listOfContacts.add(c);
            }
        }

        sortContacts();

        if (!((EditText) contactsFragment.getActivity().findViewById(R.id.magic_edit_text)).getText().toString().isEmpty()) {
            searchByStr(((EditText) contactsFragment.getActivity().findViewById(R.id.magic_edit_text)).getText().toString());
        }

        notifyDataSetChanged();
    }

    public void addInvestor(){

        if(!contactsFragment.HASHTAG_ADAPTER.allHashtagsCheck && contactsFragment.getActivity().findViewById(R.id.frame_select_bar).getVisibility() == View.VISIBLE){
            for(int i = 0;i<listHashSave.size(); i++){
                if(listHashSave.get(i).isInvestor){
                    if(!listOfContacts.contains(listHashSave.get(i)))
                        listOfContacts.add(listHashSave.get(i));
                    //i--;
                }
            }
        }else{
            for(Contact c : ContactCacheService.getInvestor()){
                if(!listOfContacts.contains(c)) listOfContacts.add(c);
            }
        }

        sortContacts();

        if (!((EditText) contactsFragment.getActivity().findViewById(R.id.magic_edit_text)).getText().toString().isEmpty()) {
            searchByStr(((EditText) contactsFragment.getActivity().findViewById(R.id.magic_edit_text)).getText().toString());
        }

        notifyDataSetChanged();
    }

    public void removePaused(){

        if(!contactsFragment.HASHTAG_ADAPTER.allHashtagsCheck && contactsFragment.getActivity().findViewById(R.id.frame_select_bar).getVisibility() == View.VISIBLE){
            for(int i = 0;i<listHashSave.size(); i++){
                if(listHashSave.get(i).isPause){
                    if(listOfContacts.contains(listHashSave.get(i))) {
                        listOfContacts.remove(listHashSave.get(i));
                        i--;
                    }
                }
            }
        }else{
            ArrayList<Contact> listFF = ContactCacheService.getPaused();
            for(int i = 0; i< listFF.size(); i++){
                if(listOfContacts.contains(listFF.get(i))){
                    listOfContacts.remove(listFF.get(i));
                    i--;
                }
            }

        }

        sortContacts();

        if (!((EditText) contactsFragment.getActivity().findViewById(R.id.magic_edit_text)).getText().toString().isEmpty()) {
            searchByStr(((EditText) contactsFragment.getActivity().findViewById(R.id.magic_edit_text)).getText().toString());
        }

        notifyDataSetChanged();
    }

    public void removeCrown(){

        if(!contactsFragment.HASHTAG_ADAPTER.allHashtagsCheck && contactsFragment.getActivity().findViewById(R.id.frame_select_bar).getVisibility() == View.VISIBLE){
            for(int i = 0;i<listHashSave.size(); i++){
                if(listHashSave.get(i).isCrown){
                    if(listOfContacts.contains(listHashSave.get(i))) {
                        listOfContacts.remove(listHashSave.get(i));
                        i--;
                    }
                }
            }
        }else{
            ArrayList<Contact> listFF = ContactCacheService.getCrown();
            for(int i = 0; i< listFF.size(); i++){
                if(listOfContacts.contains(listFF.get(i))){
                    listOfContacts.remove(listFF.get(i));
                    i--;
                }
            }

        }

        sortContacts();

        if (!((EditText) contactsFragment.getActivity().findViewById(R.id.magic_edit_text)).getText().toString().isEmpty()) {
            searchByStr(((EditText) contactsFragment.getActivity().findViewById(R.id.magic_edit_text)).getText().toString());
        }

        notifyDataSetChanged();
    }

    public void removeVip(){

        if(!contactsFragment.HASHTAG_ADAPTER.allHashtagsCheck && contactsFragment.getActivity().findViewById(R.id.frame_select_bar).getVisibility() == View.VISIBLE){
            for(int i = 0;i<listHashSave.size(); i++){
                if(listHashSave.get(i).isVip){
                    if(listOfContacts.contains(listHashSave.get(i))) {
                        listOfContacts.remove(listHashSave.get(i));
                        i--;
                    }
                }
            }
        }else{
            ArrayList<Contact> listFF = ContactCacheService.getVip();
            for(int i = 0; i< listFF.size(); i++){
                if(listOfContacts.contains(listFF.get(i))){
                    listOfContacts.remove(listFF.get(i));
                    i--;
                }
            }

        }

        sortContacts();

        if (!((EditText) contactsFragment.getActivity().findViewById(R.id.magic_edit_text)).getText().toString().isEmpty()) {
            searchByStr(((EditText) contactsFragment.getActivity().findViewById(R.id.magic_edit_text)).getText().toString());
        }

        notifyDataSetChanged();
    }

    public void removeStartup(){

        if(!contactsFragment.HASHTAG_ADAPTER.allHashtagsCheck && contactsFragment.getActivity().findViewById(R.id.frame_select_bar).getVisibility() == View.VISIBLE){
            for(int i = 0;i<listHashSave.size(); i++){
                if(listHashSave.get(i).isStartup){
                    if(listOfContacts.contains(listHashSave.get(i))) {
                        listOfContacts.remove(listHashSave.get(i));
                        i--;
                    }
                }
            }
        }else{
            ArrayList<Contact> listFF = ContactCacheService.getStartup();
            for(int i = 0; i< listFF.size(); i++){
                if(listOfContacts.contains(listFF.get(i))){
                    listOfContacts.remove(listFF.get(i));
                    i--;
                }
            }

        }

        sortContacts();

        if (!((EditText) contactsFragment.getActivity().findViewById(R.id.magic_edit_text)).getText().toString().isEmpty()) {
            searchByStr(((EditText) contactsFragment.getActivity().findViewById(R.id.magic_edit_text)).getText().toString());
        }

        notifyDataSetChanged();
    }

    public void removeInvestor(){

        if(!contactsFragment.HASHTAG_ADAPTER.allHashtagsCheck && contactsFragment.getActivity().findViewById(R.id.frame_select_bar).getVisibility() == View.VISIBLE){
            for(int i = 0;i<listHashSave.size(); i++){
                if(listHashSave.get(i).isInvestor){
                    if(listOfContacts.contains(listHashSave.get(i))) {
                        listOfContacts.remove(listHashSave.get(i));
                        i--;
                    }
                }
            }
        }else{
            ArrayList<Contact> listFF = ContactCacheService.getInvestor();
            for(int i = 0; i< listFF.size(); i++){
                if(listOfContacts.contains(listFF.get(i))){
                    listOfContacts.remove(listFF.get(i));
                    i--;
                }
            }

        }

        sortContacts();

        if (!((EditText) contactsFragment.getActivity().findViewById(R.id.magic_edit_text)).getText().toString().isEmpty()) {
            searchByStr(((EditText) contactsFragment.getActivity().findViewById(R.id.magic_edit_text)).getText().toString());
        }

        notifyDataSetChanged();
    }

    public void addPaused(){



        if(!contactsFragment.HASHTAG_ADAPTER.allHashtagsCheck && contactsFragment.getActivity().findViewById(R.id.frame_select_bar).getVisibility() == View.VISIBLE){
            for(int i = 0;i<listHashSave.size(); i++){
                if(listHashSave.get(i).isPause){
                    if(!listOfContacts.contains(listHashSave.get(i)))
                        listOfContacts.add(listHashSave.get(i));
                    //i--;
                }
            }
        }else{
            for(Contact c : ContactCacheService.getPaused()){
                if(!listOfContacts.contains(c)) listOfContacts.add(c);
            }
        }

        sortContacts();

        if (!((EditText) contactsFragment.getActivity().findViewById(R.id.magic_edit_text)).getText().toString().isEmpty()) {
            searchByStr(((EditText) contactsFragment.getActivity().findViewById(R.id.magic_edit_text)).getText().toString());
        }

        notifyDataSetChanged();
    }

    public void removeImportant(){

        if(!contactsFragment.HASHTAG_ADAPTER.allHashtagsCheck && contactsFragment.getActivity().findViewById(R.id.frame_select_bar).getVisibility() == View.VISIBLE){
            for(int i = 0;i<listHashSave.size(); i++){
                if(listHashSave.get(i).isImportant){
                    if(listOfContacts.contains(listHashSave.get(i))) {
                        listOfContacts.remove(listHashSave.get(i));
                        i--;
                    }
                }
            }
        }else{
            ArrayList<Contact> listFF = ContactCacheService.getImportant();
            for(int i = 0; i< listFF.size(); i++){
                if(listOfContacts.contains(listFF.get(i))){
                    listOfContacts.remove(listFF.get(i));
                    i--;
                }
            }

        }

        sortContacts();

        if (!((EditText) contactsFragment.getActivity().findViewById(R.id.magic_edit_text)).getText().toString().isEmpty()) {
            searchByStr(((EditText) contactsFragment.getActivity().findViewById(R.id.magic_edit_text)).getText().toString());
        }

        notifyDataSetChanged();
    }

    public void addImportant(){



        if(!contactsFragment.HASHTAG_ADAPTER.allHashtagsCheck && contactsFragment.getActivity().findViewById(R.id.frame_select_bar).getVisibility() == View.VISIBLE){
            for(int i = 0;i<listHashSave.size(); i++){
                if(listHashSave.get(i).isImportant){
                    if(!listOfContacts.contains(listHashSave.get(i)))
                        listOfContacts.add(listHashSave.get(i));
                    //i--;
                }
            }
        }else{
            for(Contact c : ContactCacheService.getImportant()){
                if(!listOfContacts.contains(c)) listOfContacts.add(c);
            }
        }

        sortContacts();

        if (!((EditText) contactsFragment.getActivity().findViewById(R.id.magic_edit_text)).getText().toString().isEmpty()) {
            searchByStr(((EditText) contactsFragment.getActivity().findViewById(R.id.magic_edit_text)).getText().toString());
        }

        notifyDataSetChanged();
    }

    public void removeFavorites(){

        if(!contactsFragment.HASHTAG_ADAPTER.allHashtagsCheck && contactsFragment.getActivity().findViewById(R.id.frame_select_bar).getVisibility() == View.VISIBLE){
            for(int i = 0;i<listHashSave.size(); i++){
                if(listHashSave.get(i).isFavorite){
                    if(listOfContacts.contains(listHashSave.get(i))) {
                        listOfContacts.remove(listHashSave.get(i));
                        i--;
                    }
                }
            }
        }else{
            ArrayList<Contact> listFF = ContactCacheService.getFavorite();
            for(int i = 0; i< listFF.size(); i++){
                if(listOfContacts.contains(listFF.get(i))){
                    listOfContacts.remove(listFF.get(i));
                    i--;
                }
            }

        }

        sortContacts();

        if (!((EditText) contactsFragment.getActivity().findViewById(R.id.magic_edit_text)).getText().toString().isEmpty()) {
            searchByStr(((EditText) contactsFragment.getActivity().findViewById(R.id.magic_edit_text)).getText().toString());
        }

        notifyDataSetChanged();
    }

    public void addFavorites(){



        if(!contactsFragment.HASHTAG_ADAPTER.allHashtagsCheck && contactsFragment.getActivity().findViewById(R.id.frame_select_bar).getVisibility() == View.VISIBLE){
            for(int i = 0;i<listHashSave.size(); i++){
                if(listHashSave.get(i).isFavorite){
                    if(!listOfContacts.contains(listHashSave.get(i)))
                    listOfContacts.add(listHashSave.get(i));
                    //i--;
                }
            }
        }else{
            for(Contact c : ContactCacheService.getFavorite()){
                if(!listOfContacts.contains(c)) listOfContacts.add(c);
            }
        }

        sortContacts();

        if (!((EditText) contactsFragment.getActivity().findViewById(R.id.magic_edit_text)).getText().toString().isEmpty()) {
            searchByStr(((EditText) contactsFragment.getActivity().findViewById(R.id.magic_edit_text)).getText().toString());
        }

        notifyDataSetChanged();
    }

    public void showFavorite() {

        listOfContacts.clear();



        if(!contactsFragment.HASHTAG_ADAPTER.allHashtagsCheck && contactsFragment.getActivity().findViewById(R.id.frame_select_bar).getVisibility() == View.VISIBLE){
            for(int i = 0;i<listHashSave.size(); i++){
                if(listHashSave.get(i).isFavorite){
                    listOfContacts.add(listHashSave.get(i));
                    //i--;
                }
            }
        }else{

            listOfContacts = ContactCacheService.getFavorite();
        }


        ((TextView) contactsFragment.getActivity().findViewById(R.id.barFavorite_count)).setText("(" + getListOfContacts().size() + ")");
        ((ImageView) contactsFragment.getActivity().findViewById(R.id.selected_filter)).setImageDrawable(contactsFragment.getResources().getDrawable(R.drawable.icn_sort_star));

        /*for(int i = 0;i<listOfContacts.size(); i++){
            if(!listOfContacts.get(i).isFavorite){
                listOfContacts.remove(i);
                i--;

            }
        }*/

       /* for(Contact c : listOfContacts){
            if(!c.isFavorite){
                listOfContacts.remove(c);
            }
        }*/

        clearListSort();
        setListSaveSort();

        sortContacts();

        //setListSaveSort();

        if (!((EditText) contactsFragment.getActivity().findViewById(R.id.magic_edit_text)).getText().toString().isEmpty()) {
            searchByStr(((EditText) contactsFragment.getActivity().findViewById(R.id.magic_edit_text)).getText().toString());
        }

        notifyDataSetChanged();
    }

    public void showImportant() {
        listOfContacts.clear();


        if(!contactsFragment.HASHTAG_ADAPTER.allHashtagsCheck && contactsFragment.getActivity().findViewById(R.id.frame_select_bar).getVisibility() == View.VISIBLE){
            for(int i = 0;i<listHashSave.size(); i++){
                if(listHashSave.get(i).isImportant){
                    listOfContacts.add(listHashSave.get(i));
                    //i--;
                }
            }
        }else{
            listOfContacts = ContactCacheService.getImportant();
        }

        ((TextView) contactsFragment.getActivity().findViewById(R.id.barFavorite_count)).setText("(" + getListOfContacts().size() + ")");
        ((ImageView) contactsFragment.getActivity().findViewById(R.id.selected_filter)).setImageDrawable(contactsFragment.getResources().getDrawable(R.drawable.check_3));

        clearListSort();
        setListSaveSort();



        sortContacts();
        if (!((EditText) contactsFragment.getActivity().findViewById(R.id.magic_edit_text)).getText().toString().isEmpty()) {
            searchByStr(((EditText) contactsFragment.getActivity().findViewById(R.id.magic_edit_text)).getText().toString());
        }
        notifyDataSetChanged();
    }

    public void showFinished() {
        /*listOfContacts.clear();
        listOfContacts = ContactCacheService.getFinished();*/

        listOfContacts.clear();



        if(!contactsFragment.HASHTAG_ADAPTER.allHashtagsCheck && contactsFragment.getActivity().findViewById(R.id.frame_select_bar).getVisibility() == View.VISIBLE){
            for(int i = 0;i<listHashSave.size(); i++){
                if(listHashSave.get(i).isFinished){
                    listOfContacts.add(listHashSave.get(i));
                    //i--;
                }
            }
        }else{
            listOfContacts = ContactCacheService.getFinished();
        }

        ((TextView) contactsFragment.getActivity().findViewById(R.id.barFavorite_count)).setText("(" + getListOfContacts().size() + ")");
        ((ImageView) contactsFragment.getActivity().findViewById(R.id.selected_filter)).setImageDrawable(contactsFragment.getResources().getDrawable(R.drawable.finish_1));

        clearListSort();
        setListSaveSort();

        sortContacts();
        if (!((EditText) contactsFragment.getActivity().findViewById(R.id.magic_edit_text)).getText().toString().isEmpty()) {
            searchByStr(((EditText) contactsFragment.getActivity().findViewById(R.id.magic_edit_text)).getText().toString());
        }
        notifyDataSetChanged();
    }

    public void showPaused() {


        /*listOfContacts.clear();
        listOfContacts = ContactCacheService.getPaused();*/

        listOfContacts.clear();



        if(!contactsFragment.HASHTAG_ADAPTER.allHashtagsCheck && contactsFragment.getActivity().findViewById(R.id.frame_select_bar).getVisibility() == View.VISIBLE){
            for(int i = 0;i<listHashSave.size(); i++){
                if(listHashSave.get(i).isPause){
                    listOfContacts.add(listHashSave.get(i));
                    //i--;
                }
            }
        }else{
            listOfContacts = ContactCacheService.getPaused();
        }

        ((TextView) contactsFragment.getActivity().findViewById(R.id.barFavorite_count)).setText("(" + getListOfContacts().size() + ")");
        ((ImageView) contactsFragment.getActivity().findViewById(R.id.selected_filter)).setImageDrawable(contactsFragment.getResources().getDrawable(R.drawable.pause));

        clearListSort();
        setListSaveSort();


        sortContacts();
        if (!((EditText) contactsFragment.getActivity().findViewById(R.id.magic_edit_text)).getText().toString().isEmpty()) {
            searchByStr(((EditText) contactsFragment.getActivity().findViewById(R.id.magic_edit_text)).getText().toString());
        }
        notifyDataSetChanged();
    }

    public void showCrown() {

        listOfContacts.clear();

        if(!contactsFragment.HASHTAG_ADAPTER.allHashtagsCheck && contactsFragment.getActivity().findViewById(R.id.frame_select_bar).getVisibility() == View.VISIBLE){
            for(int i = 0;i<listHashSave.size(); i++){
                if(listHashSave.get(i).isCrown){
                    listOfContacts.add(listHashSave.get(i));
                    //i--;
                }
            }
        }else{
            listOfContacts = ContactCacheService.getCrown();
        }

        ((TextView) contactsFragment.getActivity().findViewById(R.id.barFavorite_count)).setText("(" + getListOfContacts().size() + ")");
        ((ImageView) contactsFragment.getActivity().findViewById(R.id.selected_filter)).setImageDrawable(contactsFragment.getResources().getDrawable(R.drawable.crown));

        clearListSort();
        setListSaveSort();

        sortContacts();
        if (!((EditText) contactsFragment.getActivity().findViewById(R.id.magic_edit_text)).getText().toString().isEmpty()) {
            searchByStr(((EditText) contactsFragment.getActivity().findViewById(R.id.magic_edit_text)).getText().toString());
        }
        notifyDataSetChanged();
    }

    public void showVip() {

        listOfContacts.clear();

        if(!contactsFragment.HASHTAG_ADAPTER.allHashtagsCheck && contactsFragment.getActivity().findViewById(R.id.frame_select_bar).getVisibility() == View.VISIBLE){
            for(int i = 0;i<listHashSave.size(); i++){
                if(listHashSave.get(i).isVip){
                    listOfContacts.add(listHashSave.get(i));
                    //i--;
                }
            }
        }else{
            listOfContacts = ContactCacheService.getVip();
        }

        ((TextView) contactsFragment.getActivity().findViewById(R.id.barFavorite_count)).setText("(" + getListOfContacts().size() + ")");
        ((ImageView) contactsFragment.getActivity().findViewById(R.id.selected_filter)).setImageDrawable(contactsFragment.getResources().getDrawable(R.drawable.vip_new));

        clearListSort();
        setListSaveSort();

        sortContacts();
        if (!((EditText) contactsFragment.getActivity().findViewById(R.id.magic_edit_text)).getText().toString().isEmpty()) {
            searchByStr(((EditText) contactsFragment.getActivity().findViewById(R.id.magic_edit_text)).getText().toString());
        }
        notifyDataSetChanged();
    }

    public void showStartup() {

        listOfContacts.clear();

        if(!contactsFragment.HASHTAG_ADAPTER.allHashtagsCheck && contactsFragment.getActivity().findViewById(R.id.frame_select_bar).getVisibility() == View.VISIBLE){
            for(int i = 0;i<listHashSave.size(); i++){
                if(listHashSave.get(i).isStartup){
                    listOfContacts.add(listHashSave.get(i));
                    //i--;
                }
            }
        }else{
            listOfContacts = ContactCacheService.getStartup();
        }

        ((TextView) contactsFragment.getActivity().findViewById(R.id.barFavorite_count)).setText("(" + getListOfContacts().size() + ")");
        ((ImageView) contactsFragment.getActivity().findViewById(R.id.selected_filter)).setImageDrawable(contactsFragment.getResources().getDrawable(R.drawable.startup));

        clearListSort();
        setListSaveSort();

        sortContacts();
        if (!((EditText) contactsFragment.getActivity().findViewById(R.id.magic_edit_text)).getText().toString().isEmpty()) {
            searchByStr(((EditText) contactsFragment.getActivity().findViewById(R.id.magic_edit_text)).getText().toString());
        }
        notifyDataSetChanged();
    }

    public void showInvestor() {

        listOfContacts.clear();

        if(!contactsFragment.HASHTAG_ADAPTER.allHashtagsCheck && contactsFragment.getActivity().findViewById(R.id.frame_select_bar).getVisibility() == View.VISIBLE){
            for(int i = 0;i<listHashSave.size(); i++){
                if(listHashSave.get(i).isInvestor){
                    listOfContacts.add(listHashSave.get(i));
                    //i--;
                }
            }
        }else{
            listOfContacts = ContactCacheService.getInvestor();
        }

        ((TextView) contactsFragment.getActivity().findViewById(R.id.barFavorite_count)).setText("(" + getListOfContacts().size() + ")");
        ((ImageView) contactsFragment.getActivity().findViewById(R.id.selected_filter)).setImageDrawable(contactsFragment.getResources().getDrawable(R.drawable.investor_));

        clearListSort();
        setListSaveSort();

        sortContacts();
        if (!((EditText) contactsFragment.getActivity().findViewById(R.id.magic_edit_text)).getText().toString().isEmpty()) {
            searchByStr(((EditText) contactsFragment.getActivity().findViewById(R.id.magic_edit_text)).getText().toString());
        }
        notifyDataSetChanged();
    }

    public void showCompanies() {
        listOfContacts.clear();  //new

        listOfContacts = ContactCacheService.getCompanies();

        sortContacts();

        if (!((EditText) contactsFragment.getActivity().findViewById(R.id.magic_edit_text)).getText().toString().isEmpty()) {
            searchByStr(((EditText) contactsFragment.getActivity().findViewById(R.id.magic_edit_text)).getText().toString());
        }

        notifyDataSetChanged();
    }

    public void setNULLSelectedID() {
        selectedContactId = null;
    }

    public void addCompanies() {
        listOfContacts.addAll(ContactCacheService.getCompanies());
        //sortByAsc();
        sortContacts();
        notifyDataSetChanged();
    }

    public void removeContacts() {
        for (Contact contact : savedContacts) {
            if (contact.listOfContacts.isEmpty()) {
                listOfContacts.remove(contact);
            }
        }
        //sortByAsc();
        sortContacts();
        notifyDataSetChanged();
    }

    public void removeCompanies() {
        for (Contact contact : savedContacts) {
            if (!contact.listOfContacts.isEmpty()) {
                listOfContacts.remove(contact);
            }
        }
        //sortByAsc();
        sortContacts();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return listOfContacts.size();
    }

}
