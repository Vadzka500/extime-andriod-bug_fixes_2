package ai.extime.Fragments;


import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.FragmentManager;
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
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
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
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
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

import com.github.clans.fab.FloatingActionMenu;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
import ai.extime.Adapters.ClibpboardAdapter;
import ai.extime.Adapters.CompanyEditAdapter;
import ai.extime.Adapters.ContactNumberCreateAdapter;
import ai.extime.Adapters.CustomTagsAdapter;
import ai.extime.Adapters.ExtratorAdapter;
import ai.extime.Adapters.HashtagMasstaggingAdapter;
import ai.extime.Adapters.MostUsedTagsAdapter;
import ai.extime.Adapters.PositionEditAdapter;
import ai.extime.Adapters.SearchContactByNameAdaper;
import ai.extime.Enums.ClipboardEnum;
import ai.extime.Enums.SocEnum;
import ai.extime.Events.AddHistoryEntry;
import ai.extime.Events.ClickNameCreate;
import ai.extime.Events.NotifyClipboardAdapter;
import ai.extime.Events.SaveCreate;
import ai.extime.Events.UpdateFile;
import ai.extime.Interfaces.CompanySelectInterface;
import ai.extime.Interfaces.HashtagAddInterface;
import ai.extime.Interfaces.PopupsInter;
import ai.extime.Interfaces.Postman;
import ai.extime.Models.Clipboard;
import ai.extime.Models.HashTag;
import ai.extime.Models.HashTagQuantity;
import ai.extime.Services.ContactCacheService;
import ai.extime.Services.FabNotificationService;
import ai.extime.Utils.SocialEq;
import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import ai.extime.Adapters.CompanyAdapter;
import ai.extime.Adapters.ContactAdapter;
import ai.extime.Adapters.ProfileSectionAdapter;
import ai.extime.Enums.SocialEnums;
import ai.extime.Events.AnimColorMessenger;
import ai.extime.Events.UpdateContactCreate;
import ai.extime.Models.Contact;
import ai.extime.Models.ContactInfo;
import ai.extime.Models.SocialModel;

import com.extime.R;

import ai.extime.Services.ContactsService;
import ai.extime.Utils.ClipboardType;

/**
 * Created by patal on 14.08.2017.
 */

public class CreateFragment extends Fragment implements PopupsInter, CompanySelectInterface, HashtagAddInterface {

    private View mainView;

    private static final String CONTACT_EXTRA = "contact";

    public static Contact createdContact;

    private ArrayList<View> openedViews = new ArrayList<>();

    private RecyclerView recyclerView;

    private RecyclerView nameRecycler;

    private SearchContactByNameAdaper nameAdaper;

    RecyclerView containerMost;

    private MostUsedTagsAdapter mostTagAdapter;

    private CircleImageView contactImage;

    public EditText hashTagList;

    public FrameLayout popupName;

    public EditText companyContact;

    public EditText possitionCompanyContact;

    RecyclerView containerAssistant;

    private ContactsService contactService;

    private EditText userName;

    private TextView initialsContact;

    private boolean mergeContacts = false;

    private ContactNumberCreateAdapter contactNumberAdapter;

    private SocialModel socialModel;

    private ArrayList<Contact> listMergedContacts;

    private Contact contactForSend;

    FrameLayout socialPopup;

    public static boolean checkMerge = true;

    private FloatingActionMenu floatingActionMenu;

    public FrameLayout popupHelpCompanyposition;

    CustomTagsAdapter customTagsAdapter;

    ExtratorAdapter extratorAdapter;

    private FrameLayout popupProfileEditPreviewSocial;
    private FrameLayout popupEditSocial;

    FrameLayout popupUserHashtags;

    private FrameLayout frameSelectBar;
    public boolean hideSelect = false;


    public ContactProfileDataFragment contactProfileDataFragment;
    public CompanyProfileDataFragment companyProfileDataFragment;
    public ContactTimelineDataFragment contactTimelineDataFragment;
    public ProfileSectionAdapter adapter;

    public static String nameContactExtractFromSocial;
    public static SocialEnums socialEnums;

    public FrameLayout popupCompaniesEdit = null;
    public FrameLayout popupPositionEdit = null;

    private Activity activityApp;
    public ContactAdapter adapterC;
    private Toolbar toolbarC;


    public static CreateFragment newInstance() {
        Bundle args = new Bundle();
        CreateFragment fragment = new CreateFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static CreateFragment newInstance(ArrayList<Contact> contactsForMerge) {
        Bundle args = new Bundle();
        args.putSerializable("contactsForMerge", contactsForMerge);
        CreateFragment fragment = new CreateFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().getSerializable("contactsForMerge") != null) {

            listMergedContacts = (ArrayList<Contact>) getArguments().getSerializable("contactsForMerge");
            mergeContacts = true;
            if ((listMergedContacts.size() == 1 || listMergedContacts.get(1).getName().equals(""))) {
                mergeContacts = false;
            }
        }


        //  System.out.println("listMergedContacts = "+listMergedContacts.size());
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        menu.findItem(R.id.action_favorite).setVisible(false);
        menu.findItem(R.id.edit_contact).setVisible(false);
        menu.findItem(R.id.save_contact).setVisible(true);
        menu.findItem(R.id.action_spis).setVisible(false);
        menu.findItem(R.id.action_star).setVisible(false);
        menu.findItem(R.id.action_export).setVisible(false);

        getActivity().findViewById(R.id.select_menu).setVisibility(View.GONE);

        //   if(ContactAdapter.checkMergeContacts) {
        Toolbar mainToolBar = (Toolbar) getActivity().findViewById(R.id.main_toolbar);
        mainToolBar.setNavigationIcon(null);
        //    }else{
        //       Toolbar mainToolBar = (Toolbar) getActivity().findViewById(R.id.main_toolbar);
        //        mainToolBar.setNavigationIcon(R.drawable.icn_arrow_back);
        //    }

        ((TextView) mainToolBar.findViewById(R.id.toolbar_title)).setText("Create");

        mainToolBar.findViewById(R.id.toolbar_kanban_clicker).setVisibility(View.GONE);

        menu.findItem(R.id.edit_contact).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return false;
            }
        });
        menu.findItem(R.id.save_contact).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (createdContact.getName() != null && !createdContact.getName().equals("")) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            getActivity());
                    if (mergeContacts && listMergedContacts.size() > 1)
                        alertDialogBuilder.setTitle("Do you want to merge contacts?");
                    else
                        alertDialogBuilder.setTitle("Do you want to create new contact?");
                    alertDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("Yes", (dialog, id) -> {

                                Realm realm = Realm.getDefaultInstance(); //+

                                popupHelpCompanyposition.setVisibility(View.GONE);


                                boolean checkBackStack = false;
                                if (mergeContacts) {
                                    System.out.println("MERGE CREATEEEEE");
                                    /*ArrayList<String> del = new ArrayList<>();
                                    ArrayList<Boolean> delCheck = new ArrayList<>();*/

                                    adapterC.clearSelectList(); //new

                                    for (Contact contacts : listMergedContacts) {
                                        // MainActivity.typeUpdate = "Delete";
                                        // MainActivity.nameToUpd.add(contacts.getName());

                                        if (contacts.getCompany() != null) {
                                            Contact companyOldContact = ContactCacheService.getCompany(contacts.getCompany());
                                            //String nameCompany = contacts.getCompany();
                                            if (companyOldContact != null && companyOldContact.isValid() && (companyOldContact.listOfContacts == null || companyOldContact.listOfContacts.size() == 1)) {
                                                adapterC.removeContactById(companyOldContact);
                                            }
                                            /*boolean checlDelete = */
                                            //try {
                                            try {
                                                checkBackStack = ContactCacheService.removeContactFromCompany(companyOldContact, contacts);

                                                System.out.println("CGECK BACK = "+checkBackStack);
                                            }catch (Exception e){
                                                e.printStackTrace();
                                            }


                                            //if(checlDelete){
                                              /* del.add(nameCompany);
                                               delCheck.add(false);*/
                                            //}
                                        }

                                        contactService.deleteContact(contacts.getIdContact());
                                        /*del.add(contacts.getName());
                                        delCheck.add(true);*/
                                    }
                                    /*MainActivity.listToManyUpdateFile.add("Delete");
                                    MainActivity.listToManyUpdateFile.add(del);
                                    MainActivity.listToManyUpdateFile.add(delCheck);*/
                                    //   EventBus.getDefault().post(new UpdateFile());
                                    //EventBus.getDefault().post(new UpdateFileSync("Delete",del));

                                }


                                System.out.println("MERGEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");

                                Date date = new Date();
                                Calendar cal = Calendar.getInstance();
                                cal.setTime(date);
                                Time time = getRandomDate();
                                time.setHours(cal.get(Calendar.HOUR_OF_DAY));
                                time.setMinutes(cal.get(Calendar.MINUTE));
                                time.setSeconds(cal.get(Calendar.SECOND));

                                createdContact.setDateCreate(date);
                                createdContact.time = time.toString();

                                RealmList<HashTag> listOfHashTag = new RealmList<HashTag>();
                                ArrayList<String> hashtags = new ArrayList<>(Arrays.asList(hashTagList.getText().toString().split(" ")));
                                System.out.println("HASHTAG STRING = " + hashtags.size());

                                createdContact.setName(userName.getText().toString().trim());

                                for (String hashTag : hashtags) {
                                    if (hashTag != "" && hashTag.length() > 1)
                                        listOfHashTag.add(new HashTag(hashTag.toLowerCase().trim()));
                                }

                                createdContact.setListOfHashtags(listOfHashTag);


                                //      ArrayList<Contact> contacts = ContactCacheService.getAllContacts(getActivity().getApplicationContext());

                                //  contacts.add(createdContact);


                                //   System.out.println("Count contactInfos = "+contactProfileDataFragment.contactNumberAdapter.contactInfos.size());
                                createdContact.listOfContactInfo = new RealmList<>();

                                if (contactProfileDataFragment.contactNumberEditAdapter != null && contactProfileDataFragment.contactNumberEditAdapter.getContactInfos() != null && !ContactAdapter.checkMergeContacts)
                                    createdContact.listOfContactInfo.addAll(contactProfileDataFragment.contactNumberEditAdapter.savechanges(activityApp));
                                else if (ContactAdapter.checkMergeContacts && contactProfileDataFragment.contactNumberEditAdapter.getContactInfos() != null)
                                    createdContact.listOfContactInfo.addAll(contactProfileDataFragment.contactNumberEditAdapter.getContactInfos());

                                createdContact.listOfContacts = null;

                                if (mergeContacts) {


                                    // System.out.println(listMergedContacts);

                                    //ArrayList<Contact> listForRemove = new ArrayList<>();


                                    //     System.out.println("COOOUNT MERGT LIST = "+ct.getListOfContacts().size());
                                    // ct.removeSelectedContactsFromMERGE(listMergedContacts);


                                    for (Contact contacts : listMergedContacts) {
                                        adapterC.removeContactById(contacts);
                                        ContactCacheService.removeContactById(contacts);


                                    }


                                }
                                /*else{

                                }*/


                                createdContact.call2me = true;
                                createdContact.isNew = true;
                                createdContact.isCreate = true;
                                createdContact.listOfContacts = null;


                                if (createdContact.getCompany() == null || createdContact.getCompany().isEmpty() || createdContact.getCompany().toString() == "")
                                    createdContact.setCompany(null);

                                if (createdContact.getCompanyPossition() == null || createdContact.getCompanyPossition().isEmpty() || createdContact.getCompanyPossition().toString() == "")
                                    createdContact.setCompanyPossition(null);


                                long idContAdress = contactService.saveContact(createdContact);
                                if (idContAdress != -1)
                                    createdContact.setIdContact(String.valueOf(idContAdress));


                                Contact contact = null;
                                ContactCacheService.justInsertContact(createdContact);

                              /*  if(idContAdress != -1)
                                    contact = ContactCacheService.getContactByIDContact(String.valueOf(idContAdress));
                                else*/
                                contact = ContactCacheService.getContactById(createdContact.getId());

                                System.out.println("CONTACT = " + contact.getId());
                                //    ct.addContact(contact);
                                if (hideSelect)
                                    adapterC.addContactToSavedList(contact);
                                else
                                    adapterC.addContact(contact);


                                ArrayList<Contact> companies = ContactCacheService.getCompanies();
                                boolean findCompany = false;
                                String companyName = createdContact.getCompany();
                                if (createdContact.getCompany() != null && createdContact.getCompany().length() > 0) {
                                    for (Contact contacts : companies) {
                                        if (contacts.getName().toLowerCase().equals(createdContact.getCompany().toLowerCase())) {
                                            System.out.println("FIND COMPANY");
                                            realm.beginTransaction();
                                            contacts.listOfContacts.add(contact);
                                            realm.commitTransaction();
                                            findCompany = true;

                                            /*MainActivity.listToManyUpdateFile.add("EDIT");
                                            ArrayList<String> l2 = new ArrayList<>();
                                            ArrayList<Boolean> l2Check = new ArrayList<>();
                                            l2.add(contacts.getName());
                                            l2Check.add(false);
                                            MainActivity.listToManyUpdateFile.add(l2);
                                            MainActivity.listToManyUpdateFile.add(l2Check);*/


                                        }
                                    }
                                    if (!findCompany) {
                                        System.out.println("DON't find COMPANY");

                                        Date date1 = new Date();
                                        Calendar cal1 = Calendar.getInstance();
                                        cal1.setTime(date1);
                                        Time time1 = getRandomDate();
                                        time1.setHours(cal1.get(Calendar.HOUR_OF_DAY));
                                        time1.setMinutes(cal1.get(Calendar.MINUTE));
                                        time1.setSeconds(cal1.get(Calendar.SECOND));

                                        Contact company = new Contact(date1);
                                        company.setName(companyName.trim());
                                        company.time = time1.toString();
                                        company.color = Color.rgb(Math.abs(companyName.hashCode() * 28439) % 255, Math.abs(companyName.hashCode() * 211239) % 255, Math.abs(companyName.hashCode() * 42368) % 255);
                                        RealmList<Contact> listForSaveContact = new RealmList<Contact>();
                                        listForSaveContact.add(createdContact);
                                        company.listOfContacts = listForSaveContact;
                                        ContactCacheService.updateCompany(company);

                                        Contact companyy = ContactCacheService.getCompany(company.getName());
                                        adapterC.addContact(companyy);

                                        /*ArrayList<String> list = new ArrayList<>();
                                        ArrayList<Boolean> listCheck = new ArrayList<>();
                                        list.add(companyy.getName());
                                        listCheck.add(false);
                                     //   EventBus.getDefault().post(new UpdateFileSync("ADD",list));
                                        MainActivity.listToManyUpdateFile.add("ADD");
                                        MainActivity.listToManyUpdateFile.add(list);
                                        MainActivity.listToManyUpdateFile.add(listCheck);*/

                                        // contacts.add(company);
                                    }
                                }
                                createNew = false;

                                companies.clear();
                                realm.close();


                                //    ContactCacheService.updateCacheContacts(contacts, mainView.getContext());
                                //   MainActivity.typeUpdate = "ADD";
                                //   MainActivity.nameToUpd.add(createdContact.getName());
                                System.out.println("CHECK ADD RUN");
                                //    EventBus.getDefault().post(new UpdateFile());
                                /*ArrayList<String> l3 = new ArrayList<>();
                                ArrayList<Boolean> l3Check = new ArrayList<>();
                                l3.add(contact.getName());
                                l3Check.add(true);
                                MainActivity.listToManyUpdateFile.add("ADD");
                                MainActivity.listToManyUpdateFile.add(l3);
                                MainActivity.listToManyUpdateFile.add(l3Check);*/
                                // EventBus.getDefault().post(new UpdateFileSync("ADD",l3));
                                //EventBus.getDefault().post(new UpdateFile());
                                EventBus.getDefault().post(new NotifyClipboardAdapter());

                                ContactsFragment.UPD_ALL = true;
                                /*FragmentManager fm = getActivity().getFragmentManager();
                                fm.popBackStack();*/
                                mergeContacts = false;
                                checkMerge = true;
                                //ContactsFragment.mergedContacts = false;

                                //  if(ContactsFragment.createContact){
                                System.out.println("QQQ WWW");
                                ContactAdapter.checkFoActionIconProfile = true;
                                //ContactAdapter.checkFoActionIcon = true;
                                ContactAdapter.checkMerge = false;
                                //  }


                                ContactsFragment.createContact = false;
                                floatingActionMenu.setVisibility(View.VISIBLE);

                                //CompanyAdapter.mergeCompanyAdapter = false;

                                //Runtime.getRuntime().gc();

                                android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

                                /*System.out.println("SIZE BACK STACK"+fragmentManager.getBackStackEntryCount());
                                if(fragmentManager.getBackStackEntryCount() == 2){
                                    getFragmentManager().popBackStack();
                                    getFragmentManager().popBackStack();
                                }else*/
                                //fragmentManager.popBackStack();


                               /* System.out.println("QWEQWEQWEQWEQWE create");
                                android.support.v4.app.FragmentManager fm = getActivity().getSupportFragmentManager();
                                List<Fragment> listF = fm.getFragments();
                                System.out.println("COUNT FRAG = " + listF.size());

                                int c = 1;
                                    for (int i = listF.size() - 1; i >= 0; i--) {
                                    try {
                                        if (!((ProfileFragment) listF.get(i)).getMainContact().isValid()) {
                                            c++;
                                            continue;

                                        }
                                        System.out.println("Name = " + ((ProfileFragment) listF.get(i)).getMainContact().getName());
                                    } catch (Exception e) {
                                        System.out.println("CATCH");
                                    }
                                    break;
                                }
                                System.out.println("SIZE TE POP BACK STACK = "+c);
                                for (int q = 0; q < c; q++) {
                                    try {
                                        getActivity().getSupportFragmentManager().popBackStack();
                                    } catch (IllegalStateException e) {
                                        e.printStackTrace();
                                    }

                                }*/

                                if (checkBackStack && CompanyAdapter.mergeCompanyAdapter) {
                                    //android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

                                    System.out.println("GO TO ALL");

                                    fragmentManager.popBackStack("contacts", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                } else {
                                    fragmentManager.popBackStack();

                                    if (ProfileFragment.selectedContact != null && !ProfileFragment.selectedContact.isValid())
                                        fragmentManager.popBackStack();
                                }

                                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.main_content, ProfileFragment.newInstance(contact, false)).addToBackStack("createProfile").commit();



                            /*    android.support.v4.app.FragmentManager fragmentManager =  getActivity().getSupportFragmentManager();
                                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.main_content, ProfileFragment.newInstance(createdContact)).addToBackStack("company").commit();*/


                            })
                            .setNegativeButton("No", (dialog, id) -> dialog.cancel());

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                } else {
                    Toast.makeText(mainView.getContext(), "Field name can not be empty", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SaveCreate event) {

        Realm realm = Realm.getDefaultInstance(); //+

        popupHelpCompanyposition.setVisibility(View.GONE);


        boolean checkBackStack = false;
        if (mergeContacts) {
            System.out.println("MERGE CREATEEEEE");

            for (Contact contacts : listMergedContacts) {

                if (contacts.getCompany() != null) {
                    Contact companyOldContact = ContactCacheService.getCompany(contacts.getCompany());
                    //String nameCompany = contacts.getCompany();
                    if (companyOldContact != null && companyOldContact.isValid() && (companyOldContact.listOfContacts == null || companyOldContact.listOfContacts.size() == 1)) {
                        adapterC.removeContactById(companyOldContact);
                    }
                    /*boolean checlDelete = */
                    checkBackStack = ContactCacheService.removeContactFromCompany(companyOldContact, contacts);
                    //if(checlDelete){
                                              /* del.add(nameCompany);
                                               delCheck.add(false);*/
                    //}
                }

                contactService.deleteContact(contacts.getIdContact());

            }

        }


        System.out.println("MERGEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");

        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        Time time = getRandomDate();
        time.setHours(cal.get(Calendar.HOUR_OF_DAY));
        time.setMinutes(cal.get(Calendar.MINUTE));
        time.setSeconds(cal.get(Calendar.SECOND));

        createdContact.setDateCreate(date);
        createdContact.time = time.toString();

        RealmList<HashTag> listOfHashTag = new RealmList<HashTag>();
        ArrayList<String> hashtags = new ArrayList<>(Arrays.asList(hashTagList.getText().toString().split(" ")));
        System.out.println("HASHTAG STRING = " + hashtags.size());

        createdContact.setName(userName.getText().toString().trim());

        for (String hashTag : hashtags) {
            if (hashTag != "" && hashTag.length() > 1)
                listOfHashTag.add(new HashTag(hashTag.toLowerCase().trim()));
        }

        createdContact.setListOfHashtags(listOfHashTag);

        createdContact.listOfContactInfo = new RealmList<>();

        if (contactProfileDataFragment.contactNumberEditAdapter != null && contactProfileDataFragment.contactNumberEditAdapter.getContactInfos() != null && !ContactAdapter.checkMergeContacts)
            createdContact.listOfContactInfo.addAll(contactProfileDataFragment.contactNumberEditAdapter.savechanges(activityApp));
        else if (ContactAdapter.checkMergeContacts && contactProfileDataFragment.contactNumberEditAdapter.getContactInfos() != null)
            createdContact.listOfContactInfo.addAll(contactProfileDataFragment.contactNumberEditAdapter.getContactInfos());

        createdContact.listOfContacts = null;

        if (mergeContacts) {


            for (Contact contacts : listMergedContacts) {
                adapterC.removeContactById(contacts);
                ContactCacheService.removeContactById(contacts);


            }


        }

        createdContact.call2me = true;
        createdContact.isNew = true;
        createdContact.isCreate = true;
        createdContact.listOfContacts = null;


        if (createdContact.getCompany() == null || createdContact.getCompany().isEmpty() || createdContact.getCompany().toString() == "")
            createdContact.setCompany(null);

        if (createdContact.getCompanyPossition() == null || createdContact.getCompanyPossition().isEmpty() || createdContact.getCompanyPossition().toString() == "")
            createdContact.setCompanyPossition(null);


        long idContAdress = contactService.saveContact(createdContact);
        if (idContAdress != -1)
            createdContact.setIdContact(String.valueOf(idContAdress));


        Contact contact = null;
        ContactCacheService.justInsertContact(createdContact);

                              /*  if(idContAdress != -1)
                                    contact = ContactCacheService.getContactByIDContact(String.valueOf(idContAdress));
                                else*/
        contact = ContactCacheService.getContactById(createdContact.getId());

        System.out.println("CONTACT = " + contact.getId());
        //    ct.addContact(contact);
        if (hideSelect)
            adapterC.addContactToSavedList(contact);
        else
            adapterC.addContact(contact);


        ArrayList<Contact> companies = ContactCacheService.getCompanies();
        boolean findCompany = false;
        String companyName = createdContact.getCompany();
        if (createdContact.getCompany() != null && createdContact.getCompany().length() > 0) {
            for (Contact contacts : companies) {
                if (contacts.getName().toLowerCase().equals(createdContact.getCompany().toLowerCase())) {
                    System.out.println("FIND COMPANY");
                    realm.beginTransaction();
                    contacts.listOfContacts.add(contact);
                    realm.commitTransaction();
                    findCompany = true;


                }
            }
            if (!findCompany) {
                System.out.println("DON't find COMPANY");

                Date date1 = new Date();
                Calendar cal1 = Calendar.getInstance();
                cal1.setTime(date1);
                Time time1 = getRandomDate();
                time1.setHours(cal1.get(Calendar.HOUR_OF_DAY));
                time1.setMinutes(cal1.get(Calendar.MINUTE));
                time1.setSeconds(cal1.get(Calendar.SECOND));

                Contact company = new Contact(date1);
                company.setName(companyName.trim());
                company.time = time1.toString();
                company.color = Color.rgb(Math.abs(companyName.hashCode() * 28439) % 255, Math.abs(companyName.hashCode() * 211239) % 255, Math.abs(companyName.hashCode() * 42368) % 255);
                RealmList<Contact> listForSaveContact = new RealmList<Contact>();
                listForSaveContact.add(createdContact);
                company.listOfContacts = listForSaveContact;
                ContactCacheService.updateCompany(company);

                Contact companyy = ContactCacheService.getCompany(company.getName());
                adapterC.addContact(companyy);

            }
        }
        createNew = false;

        companies.clear();
        realm.close();


        System.out.println("CHECK ADD RUN");

        //EventBus.getDefault().post(new UpdateFile());
        EventBus.getDefault().post(new NotifyClipboardAdapter());

        ContactsFragment.UPD_ALL = true;
                                /*FragmentManager fm = getActivity().getFragmentManager();
                                fm.popBackStack();*/
        mergeContacts = false;
        checkMerge = true;
        //ContactsFragment.mergedContacts = false;

        //  if(ContactsFragment.createContact){
        System.out.println("QQQ WWW");
        ContactAdapter.checkFoActionIconProfile = true;
        ContactAdapter.checkMerge = false;
        ContactsFragment.createContact = false;
        floatingActionMenu.setVisibility(View.VISIBLE);

        ((TextView) toolbarC.findViewById(R.id.cancel_toolbar)).setVisibility(View.GONE);


        /*android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        if (checkBackStack && CompanyAdapter.mergeCompanyAdapter) {
            fragmentManager.popBackStack("contacts", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } else {
            fragmentManager.popBackStack();
            if (ProfileFragment.selectedContact != null && !ProfileFragment.selectedContact.isValid())
                fragmentManager.popBackStack();
        }
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.main_content, ProfileFragment.newInstance(contact, false)).addToBackStack("createProfile").commit();*/

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(UpdateContactCreate event) {
        Contact contact = event.getContact();


        ArrayList<String> listSetedHashtags = new ArrayList<>();
        ArrayList<String> listSavedNames = new ArrayList<>();
        ArrayList<String> listSavedCompany = new ArrayList<>();
        ArrayList<String> listSavedPossition = new ArrayList<>();
        ArrayList<ContactInfo> listOfContactInfo = new ArrayList<>();

            /*if (!listSavedNames.contains(contact.getName())) {
                if (userName.getText().length() == 0) {
                    userName.setText(contact.getName());
                    listSavedNames.add(contact.getName());
                } else {
                    userName.setText(userName.getText() + " " + contact.getName());
                    listSavedNames.add(contact.getName());
                }
            }*/
        if (contact.getName() != null && !contact.getName().isEmpty()) {
            userName.setText(contact.getName());
        }

            /*if (!listSavedCompany.contains(contact.getCompany())) {
                if (companyContact.getText().length() == 0) {
                    companyContact.setText(contact.getCompany());
                    listSavedCompany.add(contact.getCompany());
                } else {
                    if (contact.getCompany() != null) {
                        companyContact.setText(companyContact.getText() + " " + contact.getCompany());
                        listSavedCompany.add(contact.getCompany());
                    }
                }
            }*/
        if (contact.getCompany() != null && !contact.getCompany().isEmpty()) {
            companyContact.setText(contact.getCompany());
        }

            /*if (!listSavedPossition.contains(contact.getCompanyPossition())) {
                if (possitionCompanyContact.getText().length() == 0) {
                    possitionCompanyContact.setText(contact.getCompanyPossition());
                    listSavedPossition.add(contact.getCompanyPossition());
                } else {
                    if (contact.getCompanyPossition() != null) {
                        possitionCompanyContact.setText(possitionCompanyContact.getText() + " " + contact.getCompanyPossition());
                        listSavedPossition.add(contact.getCompanyPossition());
                    }
                }
            }*/
        if (contact.getCompanyPossition() != null && !contact.getCompanyPossition().isEmpty()) {
            possitionCompanyContact.setText(contact.getCompanyPossition());
        }


          /*  if (contact.getListOfHashtags() != null)
                for (HashTag hashTag : contact.getListOfHashtags()) {
                    if (hashTagList.getText().length() == 0) {
                        hashTagList.setText(hashTag.getHashTagValue());
                        listSetedHashtags.add(hashTag.getHashTagValue());
                    } else {
                        if (!listSetedHashtags.contains(hashTag.getHashTagValue())) {
                            hashTagList.setText(hashTagList.getText() + " " + hashTag);
                            listSetedHashtags.add(hashTag.getHashTagValue());
                        }
                    }
                }*/

          /*if(contact.getListOfHashtags() != null && !contact.getListOfHashtags().isEmpty()){
              String[] hashList = hashTagList.getText().toString().trim().split(" ");
              for(HashTag hashTag : contact.getListOfHashtags()){
                  if(hashTagList.getText().length() == 0){

                  }else{

                  }
              }
          }*/

        if (contact.listOfContactInfo != null) {
            for (ContactInfo contactInfo : contact.listOfContactInfo) {
                //if(contactInfo != null && contact.listOfContactInfo !=  null)
                if (!listOfContactInfo.contains(contactInfo)) {
                    System.out.println("ADD");
                    if (createdContact.listOfContactInfo == null)
                        createdContact.listOfContactInfo = new RealmList<>();
                    if (createdContact.getSocialModel() != null) {
                        if (createdContact.getSocialModel().getFacebookLink() != null && !createdContact.getSocialModel().getFacebookLink().isEmpty()) {
                            //if (createdContact.getSocialModel().getFacebookLink().equalsIgnoreCase(contactInfo.value))
                                if (SocialEq.checkStrSocials(createdContact.getSocialModel().getFacebookLink(), contactInfo.value))
                                continue;
                        }
                        if (createdContact.getSocialModel().getVkLink() != null && !createdContact.getSocialModel().getVkLink().isEmpty()) {
                            if (SocialEq.checkStrSocials(createdContact.getSocialModel().getVkLink(), contactInfo.value))
                                continue;
                        }
                        if (createdContact.getSocialModel().getInstagramLink() != null && !createdContact.getSocialModel().getInstagramLink().isEmpty()) {
                            if (SocialEq.checkStrSocials(createdContact.getSocialModel().getInstagramLink(), contactInfo.value))
                                continue;
                        }
                        if (createdContact.getSocialModel().getLinkedInLink() != null && !createdContact.getSocialModel().getLinkedInLink().isEmpty()) {
                            if (SocialEq.checkStrSocials(createdContact.getSocialModel().getLinkedInLink(), contactInfo.value))
                                continue;
                        }
                        if (createdContact.getSocialModel().getTwitterLink() != null && !createdContact.getSocialModel().getTwitterLink().isEmpty()) {
                            if (SocialEq.checkStrSocials(createdContact.getSocialModel().getTwitterLink(), contactInfo.value))
                                continue;
                        }
                        if (createdContact.getSocialModel().getYoutubeLink() != null && !createdContact.getSocialModel().getYoutubeLink().isEmpty()) {
                            if (SocialEq.checkStrSocials(createdContact.getSocialModel().getYoutubeLink(), contactInfo.value))
                                continue;
                        }

                        /*if(createdContact.getSocialModel().getTelegramLink() != null && !createdContact.getSocialModel().getTelegramLink().isEmpty()){
                            if(createdContact.getSocialModel().getTelegramLink().equalsIgnoreCase(contactInfo.value)) continue;
                        }
                        if(createdContact.getSocialModel().getWhatsappLink() != null && !createdContact.getSocialModel().getWhatsappLink().isEmpty()){
                            if(createdContact.getSocialModel().getWhatsappLink().equalsIgnoreCase(contactInfo.value)) continue;
                        }
                        if(createdContact.getSocialModel().getSkypeLink() != null && !createdContact.getSocialModel().getSkypeLink().isEmpty()){
                            if(createdContact.getSocialModel().getSkypeLink().equalsIgnoreCase(contactInfo.value)) continue;
                        }
                        if(createdContact.getSocialModel().getViberLink() != null && !createdContact.getSocialModel().getViberLink().isEmpty()){
                            if(createdContact.getSocialModel().getViberLink().equalsIgnoreCase(contactInfo.value)) continue;
                        }*/
                    }
                    createdContact.listOfContactInfo.add(contactInfo);
                    listOfContactInfo.add(contactInfo);
                    contactProfileDataFragment.contactNumberEditAdapter.addInfo(contactInfo);
                }
            }
        }

        createdContact.setListOfHashtags(contact.getListOfHashtags());

        if (createdContact.getSocialModel() == null)
            createdContact.setSocialModel(new SocialModel());

        if (contact.getSocialModel() != null) {
            if (contact.hasFacebook || (contact.getSocialModel().getFacebookLink() != null && !contact.getSocialModel().getFacebookLink().isEmpty())) {


                if (createdContact.getSocialModel() != null) {
                    if (createdContact.getSocialModel().getFacebookLink() == null || createdContact.getSocialModel().getFacebookLink().isEmpty()) {
                        createdContact.getSocialModel().setFacebookLink(contact.getSocialModel().getFacebookLink());
                        createdContact.hasFacebook = true;
                    } else if (!SocialEq.checkStrSocials(createdContact.getSocialModel().getFacebookLink(), contact.getSocialModel().getFacebookLink())) {
                        if (createdContact.listOfContactInfo == null) {
                            createdContact.addNote(contact.getSocialModel().getFacebookLink());
                            contactProfileDataFragment.contactNumberEditAdapter.addInfo(new ContactInfo("note", contact.getSocialModel().getFacebookLink(), false, false, false, true, false));
                        } else {
                            boolean checkS = false;
                            for (ContactInfo contactInfo : createdContact.listOfContactInfo) {
                                if (SocialEq.checkStrSocials(contactInfo.value, contact.getSocialModel().getFacebookLink()) /*contactInfo.value.equalsIgnoreCase(contact.getSocialModel().getFacebookLink())*/) {
                                    checkS = true;
                                    break;
                                }
                            }
                            if (!checkS) {
                                createdContact.addNote(contact.getSocialModel().getFacebookLink());
                                contactProfileDataFragment.contactNumberEditAdapter.addInfo(new ContactInfo("note", contact.getSocialModel().getFacebookLink(), false, false, false, true, false));
                            }

                        }
                    }
                }

                //createdContact.getSocialModel().setFacebookLink(contact.getSocialModel().getFacebookLink());

            }


            if (contact.hasLinked || (contact.getSocialModel().getLinkedInLink() != null && !contact.getSocialModel().getLinkedInLink().isEmpty())) {


                if (createdContact.getSocialModel() != null) {
                    if (createdContact.getSocialModel().getLinkedInLink() == null || createdContact.getSocialModel().getLinkedInLink().isEmpty()) {
                        createdContact.getSocialModel().setLinkedInLink(contact.getSocialModel().getLinkedInLink());
                        createdContact.hasLinked = true;
                    } else if (!SocialEq.checkStrSocials(createdContact.getSocialModel().getLinkedInLink(), contact.getSocialModel().getLinkedInLink())) {
                        if (createdContact.listOfContactInfo == null) {
                            createdContact.addNote(contact.getSocialModel().getLinkedInLink());
                            contactProfileDataFragment.contactNumberEditAdapter.addInfo(new ContactInfo("note", contact.getSocialModel().getLinkedInLink(), false, false, false, true, false));
                        } else {
                            boolean checkS = false;
                            for (ContactInfo contactInfo : createdContact.listOfContactInfo) {
                                if (SocialEq.checkStrSocials(contactInfo.value, contact.getSocialModel().getLinkedInLink())) {
                                    checkS = true;
                                    break;
                                }
                            }
                            if (!checkS) {
                                createdContact.addNote(contact.getSocialModel().getLinkedInLink());
                                contactProfileDataFragment.contactNumberEditAdapter.addInfo(new ContactInfo("note", contact.getSocialModel().getLinkedInLink(), false, false, false, true, false));
                            }

                        }
                    }
                }
            }

            if (contact.hasVk || (contact.getSocialModel().getVkLink() != null && !contact.getSocialModel().getVkLink().isEmpty())) {
                createdContact.hasVk = true;
                //createdContact.getSocialModel().setVkLink(contact.getSocialModel().getVkLink());

                if (createdContact.getSocialModel() != null) {
                    if (createdContact.getSocialModel().getVkLink() == null || createdContact.getSocialModel().getVkLink().isEmpty())
                        createdContact.getSocialModel().setVkLink(contact.getSocialModel().getVkLink());
                    else if (!SocialEq.checkStrSocials(createdContact.getSocialModel().getVkLink(), contact.getSocialModel().getVkLink())) {
                        if (createdContact.listOfContactInfo == null) {
                            createdContact.addNote(contact.getSocialModel().getVkLink());
                            contactProfileDataFragment.contactNumberEditAdapter.addInfo(new ContactInfo("note", contact.getSocialModel().getVkLink(), false, false, false, true, false));
                        } else {
                            boolean checkS = false;
                            for (ContactInfo contactInfo : createdContact.listOfContactInfo) {
                                if (SocialEq.checkStrSocials(contactInfo.value, contact.getSocialModel().getVkLink())) {
                                    checkS = true;
                                    break;
                                }
                            }
                            if (!checkS) {
                                createdContact.addNote(contact.getSocialModel().getVkLink());
                                contactProfileDataFragment.contactNumberEditAdapter.addInfo(new ContactInfo("note", contact.getSocialModel().getVkLink(), false, false, false, true, false));
                            }

                        }
                    }
                }

            }

            if (contact.hasTwitter || (contact.getSocialModel().getTwitterLink() != null && !contact.getSocialModel().getTwitterLink().isEmpty())) {
                createdContact.hasTwitter = true;
                //createdContact.getSocialModel().setVkLink(contact.getSocialModel().getVkLink());

                if (createdContact.getSocialModel() != null) {
                    System.out.println("GET = " + contact.getSocialModel().getTwitterLink() + ", new = " + createdContact.getSocialModel().getTwitterLink());
                    if (createdContact.getSocialModel().getTwitterLink() == null || createdContact.getSocialModel().getTwitterLink().isEmpty()) {
                        createdContact.getSocialModel().setTwitterLink(contact.getSocialModel().getTwitterLink());
                        createdContact.hasTwitter = true;
                    } else if (!SocialEq.checkStrSocials(createdContact.getSocialModel().getTwitterLink(), contact.getSocialModel().getTwitterLink())) {
                        if (createdContact.listOfContactInfo == null) {
                            createdContact.addNote(contact.getSocialModel().getTwitterLink());
                            contactProfileDataFragment.contactNumberEditAdapter.addInfo(new ContactInfo("note", contact.getSocialModel().getTwitterLink(), false, false, false, true, false));
                        } else {
                            boolean checkS = false;
                            for (ContactInfo contactInfo : createdContact.listOfContactInfo) {
                                if (SocialEq.checkStrSocials(contactInfo.value, contact.getSocialModel().getTwitterLink())) {
                                    checkS = true;
                                    break;
                                }
                            }
                            if (!checkS) {
                                createdContact.addNote(contact.getSocialModel().getTwitterLink());
                                contactProfileDataFragment.contactNumberEditAdapter.addInfo(new ContactInfo("note", contact.getSocialModel().getTwitterLink(), false, false, false, true, false));
                            }

                        }
                    }
                }

            }

            if (contact.hasYoutube || (contact.getSocialModel().getYoutubeLink() != null && !contact.getSocialModel().getYoutubeLink().isEmpty())) {
                createdContact.hasYoutube = true;
                //createdContact.getSocialModel().setVkLink(contact.getSocialModel().getVkLink());

                if (createdContact.getSocialModel() != null) {
                    if (createdContact.getSocialModel().getYoutubeLink() == null || createdContact.getSocialModel().getYoutubeLink().isEmpty())
                        createdContact.getSocialModel().setYoutubeLink(contact.getSocialModel().getYoutubeLink());
                    else if (!SocialEq.checkStrSocials(createdContact.getSocialModel().getYoutubeLink(), contact.getSocialModel().getYoutubeLink())) {
                        if (createdContact.listOfContactInfo == null) {
                            createdContact.addNote(contact.getSocialModel().getYoutubeLink());
                            contactProfileDataFragment.contactNumberEditAdapter.addInfo(new ContactInfo("note", contact.getSocialModel().getYoutubeLink(), false, false, false, true, false));
                        } else {
                            boolean checkS = false;
                            for (ContactInfo contactInfo : createdContact.listOfContactInfo) {
                                if (SocialEq.checkStrSocials(contactInfo.value, contact.getSocialModel().getYoutubeLink())) {
                                    checkS = true;
                                    break;
                                }
                            }
                            if (!checkS) {
                                createdContact.addNote(contact.getSocialModel().getYoutubeLink());
                                contactProfileDataFragment.contactNumberEditAdapter.addInfo(new ContactInfo("note", contact.getSocialModel().getYoutubeLink(), false, false, false, true, false));
                            }

                        }
                    }
                }

            }

            if (contact.hasInst || (contact.getSocialModel().getInstagramLink() != null && !contact.getSocialModel().getInstagramLink().isEmpty())) {
                createdContact.hasInst = true;
                //createdContact.getSocialModel().setInstagramLink(contact.getSocialModel().getInstagramLink());

                if (createdContact.getSocialModel() != null) {
                    if (createdContact.getSocialModel().getInstagramLink() == null || createdContact.getSocialModel().getInstagramLink().isEmpty())
                        createdContact.getSocialModel().setInstagramLink(contact.getSocialModel().getInstagramLink());
                    else if (!SocialEq.checkStrSocials(createdContact.getSocialModel().getInstagramLink(), contact.getSocialModel().getInstagramLink())) {
                        if (createdContact.listOfContactInfo == null) {
                            createdContact.addNote(contact.getSocialModel().getInstagramLink());
                            contactProfileDataFragment.contactNumberEditAdapter.addInfo(new ContactInfo("note", contact.getSocialModel().getInstagramLink(), false, false, false, true, false));
                        } else {
                            boolean checkS = false;
                            for (ContactInfo contactInfo : createdContact.listOfContactInfo) {
                                if (SocialEq.checkStrSocials(contactInfo.value, contact.getSocialModel().getInstagramLink())) {
                                    checkS = true;
                                    break;
                                }
                            }
                            if (!checkS) {
                                createdContact.addNote(contact.getSocialModel().getInstagramLink());
                                contactProfileDataFragment.contactNumberEditAdapter.addInfo(new ContactInfo("note", contact.getSocialModel().getInstagramLink(), false, false, false, true, false));
                            }

                        }
                    }
                }
            }
            //========
            if (contact.hasViber || (contact.getSocialModel().getViberLink() != null && !contact.getSocialModel().getViberLink().isEmpty())) {
                createdContact.hasViber = true;
                createdContact.getSocialModel().setViberLink(contact.getSocialModel().getViberLink());
            }

            if (contact.hasWhatsapp || (contact.getSocialModel().getWhatsappLink() != null && !contact.getSocialModel().getWhatsappLink().isEmpty())) {
                createdContact.hasWhatsapp = true;
                createdContact.getSocialModel().setWhatsappLink(contact.getSocialModel().getWhatsappLink());
            }

            if (contact.hasTelegram || (contact.getSocialModel().getTelegramLink() != null && !contact.getSocialModel().getTelegramLink().isEmpty())) {
                createdContact.hasTelegram = true;
                createdContact.getSocialModel().setTelegramLink(contact.getSocialModel().getTelegramLink());
            }

            if (contact.hasSkype || (contact.getSocialModel().getSkypeLink() != null && !contact.getSocialModel().getSkypeLink().isEmpty())) {
                createdContact.hasSkype = true;
                createdContact.getSocialModel().setSkypeLink(contact.getSocialModel().getSkypeLink());
            }
        }


        userName.setSelection(userName.getText().length());

        initIconColor(createdContact, getView());

        Toast.makeText(getContext(), "Update success", Toast.LENGTH_SHORT).show();
    }

    ;


    public void setAdapter() {


        //contactNumberAdapter = new ContactNumberCreateAdapter(mainView.getContext(),createdContact.listOfContactInfo);
        //recyclerView.setAdapter(contactNumberAdapter);
    }

    private void bindViews() {
        if (!EventBus.getDefault().isRegistered(this)) EventBus.getDefault().register(this);
        //  recyclerView = (RecyclerView) mainView.findViewById(R.id.recycler_view);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(mainView.getContext());
        //   recyclerView.setLayoutManager(mLinearLayoutManager);
        hashTagList = (EditText) mainView.findViewById(R.id.hashTagsEdit);
        contactImage = (CircleImageView) mainView.findViewById(R.id.profilePopupAvatar);
        initialsContact = (TextView) mainView.findViewById(R.id.profilePopupInitials);
        contactService = new ContactsService(getActivity().getContentResolver(), false);
        companyContact = (EditText) mainView.findViewById(R.id.company_title_edit);
        possitionCompanyContact = (EditText) mainView.findViewById(R.id.company_edit);
        userName = ((EditText) mainView.findViewById(R.id.editNameProflie));
        popupName = ((FrameLayout) mainView.findViewById(R.id.popupName));
        frameSelectBar = (FrameLayout) getActivity().findViewById(R.id.frame_select_bar);
        popupHelpCompanyposition = (FrameLayout) getActivity().findViewById(R.id.popupProfileCompanyPossitions);
        nameRecycler = mainView.findViewById(R.id.searchNameCreate);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mainView.getContext());
        nameRecycler.setLayoutManager(mLayoutManager);
        socialPopup = (FrameLayout) getActivity().findViewById(R.id.popupSocial);
    }


    private Time getRandomDate() {
        final Random random = new Random();
        final int millisInDay = 24 * 60 * 60 * 1000;
        return new Time((long) random.nextInt(millisInDay));
    }


    private void showCreatePopup() {
        FrameLayout createPopup = (FrameLayout) getActivity().findViewById(R.id.popup_create);

        createPopup.findViewById(R.id.createContact).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        if (createPopup.getVisibility() == View.VISIBLE) {
            createPopup.setVisibility(View.GONE);
        } else {
            createPopup.setVisibility(View.VISIBLE);
            openedViews.add(createPopup);
        }
        createPopup.findViewById(R.id.createCompany).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        createPopup.findViewById(R.id.createCommunity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        getActivity().findViewById(R.id.create_item).setOnClickListener(v -> {
            showCreatePopup();
        });

    }

    public ArrayList<Contact> listCompanyForKeyDown;

    public String sortPopupComp = "sortByAsc";

    public String sortPopupContact = "sortByAsc";

    CompanySelectInterface comp = this;

    public CompanyEditAdapter selectCompanyAdapter;

    public PositionEditAdapter selectPositionAdapter;

    public boolean sortTimePopup = false;

    public boolean sortTimeContactPopup = false;

    public boolean sortPopulPopup = false;


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

            ArrayList<Contact> listCompanies = ContactCacheService.getCompanies();
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

    public ArrayList<String> listPosition;


    private void initViews() {
        System.out.println("Create c 2.1");
        createdContact = new Contact(new Date());
        createdContact.setCompany(null);
        createdContact.setCompanyPossition(null);
        Time time = getRandomDate();
        System.out.println("Create c 2.2");
        //  createdContact.time = time;
        createdContact.time = time.toString();

        if (frameSelectBar.getVisibility() == View.VISIBLE) {
            frameSelectBar.setVisibility(View.GONE);
            hideSelect = true;
        }
        System.out.println("Create c 2.3");

        mainView.findViewById(R.id.socialsArrowDown).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSocialPopup(createdContact);
            }
        });

        listPosition = new ArrayList<>();
        //=======================================================


        /*if(popupCompaniesEdit != null) popupCompaniesEdit.setVisibility(View.GONE);
        popupCompaniesEdit = null;
        if(popupPositionEdit != null) popupPositionEdit.setVisibility(View.GONE);
        popupPositionEdit = null;*/

        listCompanyForKeyDown = adapterC.getListOfCompanies();

        System.out.println("Create c 2.4");

        //((ImageView) mainView.findViewById(R.id.mailImage)).setImageResource(R.drawable.icn_bottombar_emails);

        ((View) mainView.findViewById(R.id.lineNameCreate)).setBackgroundColor(getResources().getColor(R.color.md_deep_orange_300));
        mainView.findViewById(R.id.editNameProflie).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    EventBus.getDefault().post(new ClickNameCreate());
                    ((View) mainView.findViewById(R.id.lineNameCreate)).setBackgroundColor(getResources().getColor(R.color.md_deep_orange_300));
                    if (popupHelpCompanyposition != null)
                        popupHelpCompanyposition.setVisibility(View.GONE);
                } else {
                    ((View) mainView.findViewById(R.id.lineNameCreate)).setBackgroundColor(getResources().getColor(R.color.sortPopupLine));
                }
            }
        });

        mainView.findViewById(R.id.editNameProflie).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new ClickNameCreate());
            }
        });


        ((EditText) mainView.findViewById(R.id.company_edit)).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    System.out.println("POSITION FOCUS");
                    ((View) mainView.findViewById(R.id.positionVileEditPreview)).setBackgroundColor(getResources().getColor(R.color.md_deep_orange_300));
                    ((View) mainView.findViewById(R.id.lineCompanycreate)).setBackgroundColor(getResources().getColor(R.color.sortPopupLine));

                    /*ArrayList<String> listOfPositions2 = new ArrayList<>(Arrays.asList("CEO","COO","Co-founder","CTO","Director","Engineer","Manager","Marketing"));
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

                        selectPositionAdapter = new PositionEditAdapter(listOfPositions, comp, getActivity());

                        RecyclerView containerCompanies = (RecyclerView) popupPositionEdit.findViewById(R.id.companiesContainer_edit);
                        RecyclerView.LayoutManager mostLayoutManager = new GridLayoutManager(mainView.getContext(), 2);
                        containerCompanies.setLayoutManager(mostLayoutManager);
                        //  containerMost.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
                        containerCompanies.setItemAnimator(new DefaultItemAnimator());
                        containerCompanies.setAdapter(selectPositionAdapter);
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
                    ((View) mainView.findViewById(R.id.lineCompanycreate)).setBackgroundColor(getResources().getColor(R.color.md_deep_orange_300));
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
                    ((View) mainView.findViewById(R.id.lineCompanycreate)).setBackgroundColor(getResources().getColor(R.color.sortPopupLine));
                    /*if(popupCompaniesEdit != null)
                        popupCompaniesEdit.setVisibility(View.GONE);*/

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
                    ((View) mainView.findViewById(R.id.lineHashCreate)).setBackgroundColor(getResources().getColor(R.color.md_deep_orange_300));
                    if (popupCompaniesEdit != null) popupCompaniesEdit.setVisibility(View.GONE);
                } else {
                    ((View) mainView.findViewById(R.id.lineHashCreate)).setBackgroundColor(getResources().getColor(R.color.sortPopupLine));
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

                if (popupCompaniesEdit != null && !find)
                    popupCompaniesEdit.setVisibility(View.VISIBLE);

                shopHelpPopupcompanyPosition();
            }
        });

        mainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (popupHelpCompanyposition != null)
                    popupHelpCompanyposition.setVisibility(View.GONE);
            }
        });


        //=======================================================


        companyContact.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                createdContact.setCompany(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        possitionCompanyContact.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                createdContact.setCompanyPossition(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        hashTagList.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.subSequence(start, start + count).toString().compareTo(" ") == 0) {
                    hashTagList.setText(s.toString() + "#");
                    hashTagList.setSelection(hashTagList.getText().length());
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mainView.findViewById(R.id.arrowShowHashtagsCreate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("ARROW CLICK");

                if (socialPopup != null) {
                    if (socialPopup.getVisibility() == View.VISIBLE)
                        return;
                    showPopupUserHashtags(createdContact);
                } else {
                    showPopupUserHashtags(createdContact);
                }
            }
        });


        activityApp.findViewById(R.id.plane_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*if(((EditText)getActivity().findViewById(R.id.magic_edit_text)).getText().length() > 0){
                    System.out.println("ADD HISTORY 0");
                    EventBus.getDefault().post(new AddHistoryEntry(((EditText)getActivity().findViewById(R.id.magic_edit_text)).getText().toString()));
                }*/

                String[] magicSplit = ((EditText) activityApp.findViewById(R.id.magic_edit_text)).getText().toString().trim().split(" ");

                String[] hashList = ((EditText) activityApp.findViewById(R.id.hashTagsEdit)).getText().toString().trim().split(" ");


                ArrayList<String> listOfHashtags = new ArrayList<>();
                ArrayList<String> listOfHashtags2 = new ArrayList<>();

                for (int i = 0; i < hashList.length; i++) {
                    if (hashList[i].length() > 1 && String.valueOf(hashList[i].charAt(0)).compareTo("#") == 0)
                        listOfHashtags2.add(hashList[i].toLowerCase());
                }

                for (int indexI = 0; indexI < magicSplit.length; indexI++) {
                    if (magicSplit[indexI].trim().length() > 1 && String.valueOf(magicSplit[indexI].trim().charAt(0)).compareTo("#") == 0) {
                        if (!listOfHashtags.contains(magicSplit[indexI].trim().toLowerCase()))
                            listOfHashtags.add(magicSplit[indexI].trim().toLowerCase());
                    }
                }

                if (listOfHashtags.size() > 0) {
                    if (listOfHashtags.size() == 1) {
                        if (listOfHashtags2.contains(listOfHashtags.get(0))) {
                            Toast.makeText(mainView.getContext(), "Hashtag already exist", Toast.LENGTH_SHORT).show();
                            return;
                        } else
                            addHashtagToProfile(((EditText) getActivity().findViewById(R.id.magic_edit_text)).getText().toString());
                    } else {
                        addHashtagsToProfile(listOfHashtags);
                    }
                    ContactsFragment.UPD_ALL = true;
                    ((EditText) getActivity().findViewById(R.id.magic_edit_text)).getText().clear();
                } else {
                    boolean checkExist = false;
                    if (createdContact.listOfContactInfo != null && !createdContact.listOfContactInfo.isEmpty()) {

                        String phone_clear = ((EditText) getActivity().findViewById(R.id.magic_edit_text)).getText().toString();

                        if (ClipboardType.isPhone(((EditText) getActivity().findViewById(R.id.magic_edit_text)).getText().toString())) {

                            phone_clear = ((EditText) getActivity().findViewById(R.id.magic_edit_text)).getText().toString().replaceAll("[\\.\\s\\-\\+\\(\\)]", "");

                        }

                        for (ContactInfo contactInfo : createdContact.listOfContactInfo) {

                            if (ClipboardType.isPhone(contactInfo.value)) {
                                String phone_c = contactInfo.value.replaceAll("[\\.\\s\\-\\+\\(\\)]", "");
                                if (phone_c.equalsIgnoreCase(phone_clear))
                                    checkExist = true;
                            } else if ( (SocialEq.isSocial(((EditText) getActivity().findViewById(R.id.magic_edit_text)).getText().toString()) && SocialEq.checkStrSocials(((EditText) getActivity().findViewById(R.id.magic_edit_text)).getText().toString(), contactInfo.value))
                                    ||  contactInfo.value.equalsIgnoreCase(((EditText) getActivity().findViewById(R.id.magic_edit_text)).getText().toString()))
                                checkExist = true;

                        }
                    }

                    if (!checkExist) {
                        if (ClipboardType.isEmail(((EditText) getActivity().findViewById(R.id.magic_edit_text)).getText().toString().trim())) {
                            createdContact.addEmail(((EditText) getActivity().findViewById(R.id.magic_edit_text)).getText().toString().trim());

                        } else if (ClipboardType.isPhone(((EditText) getActivity().findViewById(R.id.magic_edit_text)).getText().toString().trim())) {

                            if (createdContact.listOfContactInfo == null || createdContact.listOfContactInfo.size() == 0) {
                                createdContact.addPhone(((EditText) getActivity().findViewById(R.id.magic_edit_text)).getText().toString().trim());
                            } else {
                                boolean checkNull = false;
                                for (int i = 0; i < createdContact.listOfContactInfo.size(); i++) {
                                    if (createdContact.listOfContactInfo.get(i).toString().equals("+000000000000")) {
                                        System.out.println("11");


                                        Realm realm = Realm.getDefaultInstance(); //+
                                        realm.beginTransaction();
                                        createdContact.listOfContactInfo.get(i).value = ((EditText) getActivity().findViewById(R.id.magic_edit_text)).getText().toString().trim();
                                        realm.commitTransaction();
                                        realm.close();
                                        checkNull = true;
                                        break;
                                    }
                                }
                                if (!checkNull)
                                    createdContact.addPhone(((EditText) getActivity().findViewById(R.id.magic_edit_text)).getText().toString().trim());

                            }


                            System.out.println("ITS PHONEEEEEEEEEEEEEEEEEE");

                        } else {
                            addNoteToContact(((EditText) getActivity().findViewById(R.id.magic_edit_text)).getText().toString().trim());
                            //  createdContact.addNote(((EditText)getActivity().findViewById(R.id.magic_edit_text)).getText().toString());
                        }
                    } else
                        Toast.makeText(getActivity(), "Data already exists", Toast.LENGTH_SHORT).show();


                }

                ((EditText) getActivity().findViewById(R.id.magic_edit_text)).getText().clear();

                //  contactProfileDataFragment.contactNumberAdapter.setContactInfos(null);
                System.out.println("CREATE LIST INFOS = " + createdContact.listOfContactInfo.size());


               /* RealmList<ContactInfo> listOfContactInfo = new RealmList<>();
                for(ContactInfo contactInfo : createdContact.listOfContactInfo){
                    if(!contactInfo.value.equals("+000000000000"))
                        listOfContactInfo.add(contactInfo);
                }*/
                if (createdContact.listOfContactInfo == null)
                    createdContact.listOfContactInfo = new RealmList<>();
                ArrayList<Contact> list = new ArrayList<>();
                list.add(createdContact);
                contactProfileDataFragment.setEditAdapter2(list, activityApp);
                contactProfileDataFragment.contactNumberEditAdapter.setContactInfos(createdContact.listOfContactInfo);
                // contactProfileDataFragment.contactNumberAdapter.notifyDataSetChanged();
                //  contactNumberAdapter.setContactInfos(createdContact.listOfContactInfo);


            }
        });

        userName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (userName.getText().length() > 0) {
                    createdContact.setName(userName.getText().toString().trim());
                    if (createdContact.getPhotoURL() == null) {
                        int nameHash = createdContact.getName().hashCode();
                        createdContact.color = Color.rgb(Math.abs(nameHash * 28439) % 255, Math.abs(nameHash * 211239) % 255, Math.abs(nameHash * 42368) % 255);
                        GradientDrawable circle = (GradientDrawable) ContextCompat.getDrawable(mainView.getContext(), R.drawable.blue_circle).mutate();
                        circle.setColor(createdContact.color);
                        contactImage.setBackground(null);
                        contactImage.setBackground(circle);
                        contactImage.setImageDrawable(null);
                        String initials = getInitials(createdContact);
                        initialsContact.setVisibility(View.VISIBLE);
                        initialsContact.setText(initials);
                    }

                    ArrayList<Contact> list = ((Postman) activityApp).getListOfContacts();
                    ArrayList<Contact> listSearch = new ArrayList<>();
                    String text = String.valueOf(userName.getText());


                    for (Contact c : list) {
                        if (c != null && c.isValid() && c.getName() != null && c.getName().contains(text)) {
                            listSearch.add(c);
                        }
                    }


                    if (listSearch.size() != 0) {
                        if (nameAdaper == null) {
                            nameAdaper = new SearchContactByNameAdaper(listSearch, getContext(), text, CreateFragment.this, getActivity().getSupportFragmentManager());


                            /*SharedPreferences mSettings;
                            mSettings = getActivity().getSharedPreferences("SortEditPopup", Context.MODE_PRIVATE);
                            sortPopupContact = mSettings.getString("Sort", "sortByDescPopul");*/

                            //sortPopupComp = "sortByDescTime";

                            /*RecyclerView recyclerCompany = (RecyclerView) getActivity().findViewById(R.id.recycleCompanyPopup);
                            RecyclerView recyclerPosition = (RecyclerView) getActivity().findViewById(R.id.recyclePositionPopup);

                            RecyclerView.LayoutManager mostLayoutManager = new LinearLayoutManager(mainView.getContext());
                            recyclerCompany.setLayoutManager(mostLayoutManager);
                            recyclerCompany.setItemAnimator(new DefaultItemAnimator());

                            RecyclerView.LayoutManager mostLayoutManager2 = new LinearLayoutManager(mainView.getContext());
                            recyclerPosition.setLayoutManager(mostLayoutManager2);
                            recyclerPosition.setItemAnimator(new DefaultItemAnimator());

                            ArrayList<Contact> listCompanies = ContactAdapter.contactAd.getListOfCompanies();
                            ArrayList<String> listOfPositions = ContactCacheService.getPossitionContacts();
                            ArrayList<Contact> listCompaniesClone = new ArrayList<>(listCompanies);
                            ArrayList<String> listOfPositionsClone = new ArrayList<>(listOfPositions);


                            selectCompanyAdapter = new CompanyEditAdapter(listCompanies, comp, getActivity());

                            selectPositionAdapter = new PositionEditAdapter(listOfPositions, comp, getActivity());*/

                            if (sortPopupContact.equals("sortByAsc")) {
                                nameAdaper.sortCompanybyAsc();
                                //selectPositionAdapter.sortPosByAsc();
                                ((TextView) popupName.findViewById(R.id.sortTextPopup)).setTextColor(getActivity().getResources().getColor(R.color.primary));
                                ((ImageView) popupName.findViewById(R.id.timeSortPopup)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
                                ((ImageView) popupName.findViewById(R.id.populSort)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
                                ((TextView) popupName.findViewById(R.id.sortTextPopup)).setText("A-Z");
                                sortTimeContactPopup = false;
                            } else if (sortPopupContact.equals("sortByDesc")) {
                                ((TextView) popupName.findViewById(R.id.sortTextPopup)).setTextColor(getActivity().getResources().getColor(R.color.primary));
                                ((ImageView) popupName.findViewById(R.id.timeSortPopup)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
                                ((ImageView) popupName.findViewById(R.id.populSort)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
                                ((TextView) popupName.findViewById(R.id.sortTextPopup)).setText("Z-A");
                                nameAdaper.sortCompanybyDesc();
                                //selectPositionAdapter.sortPosByDesc();
                            } else if (sortPopupContact.equals("sortByAscTime")) {
                                ((TextView) popupName.findViewById(R.id.sortTextPopup)).setTextColor(getActivity().getResources().getColor(R.color.gray));
                                ((ImageView) popupName.findViewById(R.id.timeSortPopup)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));
                                ((ImageView) popupName.findViewById(R.id.populSort)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
                                nameAdaper.sortByAscTime();
                                sortPopupContact = "sortByAscTime";
                                sortTimeContactPopup = true;
                            } else if (sortPopupContact.equals("sortByDescTime")) {
                                ((TextView) popupName.findViewById(R.id.sortTextPopup)).setTextColor(getActivity().getResources().getColor(R.color.gray));
                                ((ImageView) popupName.findViewById(R.id.timeSortPopup)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));
                                ((ImageView) popupName.findViewById(R.id.populSort)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
                                nameAdaper.sortByDescTime();
                                sortPopupContact = "sortByDescTime";
                                sortTimeContactPopup = false;
                            }/* else if (sortPopupContact.equals("sortByAscPopul")) {
                                ((TextView) popupHelpCompanyposition.findViewById(R.id.sortTextPopup)).setTextColor(getActivity().getResources().getColor(R.color.gray));
                                ((ImageView) popupHelpCompanyposition.findViewById(R.id.timeSortPopup)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
                                ((ImageView) popupHelpCompanyposition.findViewById(R.id.populSort)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));
                                selectCompanyAdapter.sortByAscPopul();
                                selectPositionAdapter.sortByPopupAsc();
                                sortPopupComp = "sortByAscPopul";
                                sortPopulPopup = true;
                            } else if (sortPopupContact.equals("sortByDescPopul")) {
                                ((TextView) popupHelpCompanyposition.findViewById(R.id.sortTextPopup)).setTextColor(getActivity().getResources().getColor(R.color.gray));
                                ((ImageView) popupHelpCompanyposition.findViewById(R.id.timeSortPopup)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
                                ((ImageView) popupHelpCompanyposition.findViewById(R.id.populSort)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));
                                selectCompanyAdapter.sortByDescPopul();
                                selectPositionAdapter.sortByPopupDesc();
                                sortPopupComp = "sortByDescPopul";
                                sortPopulPopup = false;
                            }*/


                            //recyclerCompany.setAdapter(selectCompanyAdapter);


                            //recyclerPosition.setAdapter(selectPositionAdapter);

                            //popupHelpCompanyposition.setVisibility(View.VISIBLE);

                            //openedViews.add(popupHelpCompanyposition);


                            popupName.findViewById(R.id.sortElementsPopup).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (((TextView) popupName.findViewById(R.id.sortTextPopup)).getText().toString().equals("A-Z")) {

                                        ((TextView) popupName.findViewById(R.id.sortTextPopup)).setTextColor(getActivity().getResources().getColor(R.color.primary));
                                        ((ImageView) popupName.findViewById(R.id.timeSortPopup)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
                                        ((ImageView) popupName.findViewById(R.id.populSort)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
                                        ((TextView) popupName.findViewById(R.id.sortTextPopup)).setText("Z-A");
                                        nameAdaper.sortCompanybyDesc();
                                        //selectPositionAdapter.sortPosByDesc();
                                        sortPopupContact = "sortByDesc";

                                      /*  SharedPreferences.Editor editor = mSettings.edit();
                                        editor.putString("Sort", "sortByDesc");
                                        editor.commit();*/


                                    } else {
                                        ((TextView) popupName.findViewById(R.id.sortTextPopup)).setTextColor(getActivity().getResources().getColor(R.color.primary));
                                        ((ImageView) popupName.findViewById(R.id.timeSortPopup)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
                                        ((ImageView) popupName.findViewById(R.id.populSort)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
                                        ((TextView) popupName.findViewById(R.id.sortTextPopup)).setText("A-Z");
                                        nameAdaper.sortCompanybyAsc();
                                        //selectPositionAdapter.sortPosByAsc();
                                        sortPopupContact = "sortByAsc";

                                      /*  SharedPreferences.Editor editor = mSettings.edit();
                                        editor.putString("Sort", "sortByAsc");
                                        editor.commit();*/


                                    }
                                }
                            });

                            popupName.findViewById(R.id.sortByTimePopup).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (!sortTimeContactPopup) {
                                        ((TextView) popupName.findViewById(R.id.sortTextPopup)).setTextColor(getActivity().getResources().getColor(R.color.gray));
                                        ((ImageView) popupName.findViewById(R.id.timeSortPopup)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));
                                        ((ImageView) popupName.findViewById(R.id.populSort)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
                                        nameAdaper.sortByAscTime();
                                        sortPopupContact = "sortByAscTime";
                                        sortTimeContactPopup = true;

                                        /*SharedPreferences.Editor editor = mSettings.edit();
                                        editor.putString("Sort", "sortByAscTime");
                                        editor.commit();*/

                                    } else {
                                        ((TextView) popupName.findViewById(R.id.sortTextPopup)).setTextColor(getActivity().getResources().getColor(R.color.gray));
                                        ((ImageView) popupName.findViewById(R.id.timeSortPopup)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));
                                        ((ImageView) popupName.findViewById(R.id.populSort)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
                                        nameAdaper.sortByDescTime();
                                        sortPopupContact = "sortByDescTime";
                                        sortTimeContactPopup = false;

                                       /* SharedPreferences.Editor editor = mSettings.edit();
                                        editor.putString("Sort", "sortByDescTime");
                                        editor.commit();*/

                                    }
                                }
                            });

                            popupName.findViewById(R.id.sortByPopupPopup).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    /*if (!sortPopulPopup) {
                                        ((TextView) popupHelpCompanyposition.findViewById(R.id.sortTextPopup)).setTextColor(getActivity().getResources().getColor(R.color.gray));
                                        ((ImageView) popupHelpCompanyposition.findViewById(R.id.timeSortPopup)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
                                        ((ImageView) popupHelpCompanyposition.findViewById(R.id.populSort)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));

                                        ArrayList<Contact> listCompanies = new ArrayList<>(listCompaniesClone);

                                        ArrayList<String> listOfPositions = new ArrayList<>(listOfPositionsClone);

                                        selectCompanyAdapter.updateList(listCompanies);
                                        selectPositionAdapter.updateList(listOfPositions);

                                        selectCompanyAdapter.sortByAscPopul();
                                        selectPositionAdapter.sortByPopupAsc();
                                        sortPopupContact = "sortByAscPopul";
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
                                        sortPopupContact = "sortByDescPopul";
                                        sortPopulPopup = false;

                                        SharedPreferences.Editor editor = mSettings.edit();
                                        editor.putString("Sort", "sortByDescPopul");
                                        editor.commit();

                                    }*/
                                }
                            });


                        } else {
                            nameAdaper.updateList(listSearch, text);

                            if (sortPopupContact.equals("sortByAsc")) {
                                nameAdaper.sortCompanybyAsc();
                                //selectPositionAdapter.sortPosByAsc();
                                ((TextView) popupName.findViewById(R.id.sortTextPopup)).setTextColor(getActivity().getResources().getColor(R.color.primary));
                                ((ImageView) popupName.findViewById(R.id.timeSortPopup)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
                                ((ImageView) popupName.findViewById(R.id.populSort)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
                                ((TextView) popupName.findViewById(R.id.sortTextPopup)).setText("A-Z");
                                sortTimeContactPopup = false;
                            } else if (sortPopupContact.equals("sortByDesc")) {
                                ((TextView) popupName.findViewById(R.id.sortTextPopup)).setTextColor(getActivity().getResources().getColor(R.color.primary));
                                ((ImageView) popupName.findViewById(R.id.timeSortPopup)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
                                ((ImageView) popupName.findViewById(R.id.populSort)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
                                ((TextView) popupName.findViewById(R.id.sortTextPopup)).setText("Z-A");
                                nameAdaper.sortCompanybyDesc();
                                //selectPositionAdapter.sortPosByDesc();
                            } else if (sortPopupContact.equals("sortByAscTime")) {
                                ((TextView) popupName.findViewById(R.id.sortTextPopup)).setTextColor(getActivity().getResources().getColor(R.color.gray));
                                ((ImageView) popupName.findViewById(R.id.timeSortPopup)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));
                                ((ImageView) popupName.findViewById(R.id.populSort)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
                                nameAdaper.sortByAscTime();
                                sortPopupContact = "sortByAscTime";
                                sortTimeContactPopup = true;
                            } else if (sortPopupContact.equals("sortByDescTime")) {
                                ((TextView) popupName.findViewById(R.id.sortTextPopup)).setTextColor(getActivity().getResources().getColor(R.color.gray));
                                ((ImageView) popupName.findViewById(R.id.timeSortPopup)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));
                                ((ImageView) popupName.findViewById(R.id.populSort)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
                                nameAdaper.sortByDescTime();
                                sortPopupContact = "sortByDescTime";
                                sortTimeContactPopup = false;
                            }

                        }


                        nameRecycler.setAdapter(nameAdaper);

                        popupName.setVisibility(View.VISIBLE);

                        if (openedViews != null && !openedViews.contains(popupName))
                            openedViews.add(popupName);
                    } else {
                        popupName.setVisibility(View.GONE);
                    }

                } else {
                    createdContact.setName("");
                    popupName.setVisibility(View.GONE);
                }
            }
        });


        getActivity().findViewById(R.id.textExtractSocial).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                TextView textView = ((TextView) getActivity().findViewById(R.id.textExtractSocial));
                //    ImageView imageView = ((ImageView) profilePopUp.findViewById(R.id.imagePreviewIcon));
                FrameLayout frameLayout = ((FrameLayout) getActivity().findViewById(R.id.frameTextExtract));
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        OnTouchMethodExtract(textView);
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        OnUpTouchMethodExtract(textView);
                        break;
                    }
                    case MotionEvent.ACTION_CANCEL: {
                        OnCalcelTouchMethodExtract(textView);
                        break;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        OnMoveTouchMethodExtract(textView, motionEvent);
                        break;
                    }
                }
                return false;
            }
        });


       /* getActivity().findViewById(R.id.textExtractSocial).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                System.out.println("CLick ext");


            }
        });*/

        getActivity().findViewById(R.id.textExtractSocial2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().findViewById(R.id.textExtractSocial).callOnClick();
            }
        });

        getActivity().findViewById(R.id.nameContactExtractFind).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!ContactsFragment.createContact) {
                    if (ContactsFragment.contactFindExtractSocial != null) {

                        if (!ContactAdapter.checkMerge) {
                            System.out.println("QQQ WWW");
                            ContactAdapter.checkFoActionIconProfile = true;
                            ContactAdapter.checkMerge = false;
                        }

                        android.support.v4.app.FragmentManager fragmentManager = ((FragmentActivity) getActivity()).getSupportFragmentManager();
                        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.main_content, ProfileFragment.newInstance(ContactCacheService.getContactById(ContactsFragment.contactFindExtractSocial.getId()), false)).addToBackStack("contacts").commit();

                        getActivity().findViewById(R.id.framePopupSocial).setVisibility(View.GONE);
                        ContactsFragment.contactFindExtractSocial = null;
                        // MainActivity.MAIN_MENU.getItem(2).setVisible(false);
                        // MainActivity.MAIN_MENU.getItem(3).setVisible(false);

                    }
                }
            }
        });

        getActivity().findViewById(R.id.textExtractSocial).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //  if(ContactsFragment.createContact) {
                System.out.println("Clicl extract");

                int index = nameContactExtractFromSocial.length();
                if (socialEnums.equals(SocialEnums.INSTAGRAM)) {
                    index = nameContactExtractFromSocial.indexOf('@');
                    index -= 2;
                } else if (socialEnums.equals(SocialEnums.VK)) {
                    index = nameContactExtractFromSocial.length();
                } else if (socialEnums.equals(SocialEnums.FACEBOOK)) {
                    index = nameContactExtractFromSocial.indexOf('(');
                    index--;
                }
                if (index < 0)
                    index = nameContactExtractFromSocial.length();

                ((EditText) mainView.findViewById(R.id.editNameProflie)).setText(nameContactExtractFromSocial.substring(0, index).trim());

                getActivity().findViewById(R.id.plane_icon).callOnClick();

            }
        });


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

        String[] hashList = ((EditText) getActivity().findViewById(R.id.hashTagsEdit)).getText().toString().split(" ");


        //ArrayList<String> listOfHashtags2 = new ArrayList<>();

        for (int i = 0; i < hashList.length; i++) {
            if (hashList[i].length() > 1 && String.valueOf(hashList[i].charAt(0)).compareTo("#") == 0)
                tags.add(hashList[i]);
        }

        /*for (HashTag hashTag : contact.getListOfHashtags()) {

            tags.add(hashTag.getHashTagValue());
        }*/

        LinearLayout linearLayout = (LinearLayout) popupUserHashtags.findViewById(R.id.containerHashTags);
        linearLayout.removeAllViews();

        for (String t : tags) {
            TextView text = new TextView(getActivity());
            text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            DisplayMetrics metrics = mainView.getContext().getResources().getDisplayMetrics();
            text.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            text.setTextColor(getResources().getColor(R.color.colorPrimary));
            text.setText(t + " ");
            text.setOnClickListener(v -> adapterC.searchByHashTagValue(t));
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
                    //findStr = magicSplit[magicSplit.length - 1];

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

                /*ArrayList<String> tags = new ArrayList<>();



                for (HashTag hashTag : contact.getListOfHashtags()) {

                    tags.add(hashTag.getHashTagValue());
                }*/


                LinearLayout linearLayout = (LinearLayout) popupUserHashtags.findViewById(R.id.containerHashTags);
                linearLayout.removeAllViews();

                for (String hashTag : listHash) {
                    TextView text = new TextView(getActivity());
                    text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                    DisplayMetrics metrics = mainView.getContext().getResources().getDisplayMetrics();
                    text.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    text.setTextColor(getResources().getColor(R.color.colorPrimary));
                    text.setText(hashTag + " ");
                    //text.setOnClickListener(v1 -> contactAdapter.searchByHashTagValue(hashTag));
                    text.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            deleteHashTagsFromUser(text.getText().toString(), contact);
                            return true;
                        }
                    });
                    linearLayout.addView(text);
                }


                EditText linearLayout2 = (EditText) mainView.findViewById(R.id.hashTagsEdit);
                linearLayout2.setText("");
                for (String t : listHash) {
                    linearLayout2.append(t + " ");
                }

                /*for (HashTag hashTag : contact.getListOfHashtags()) {
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
                }*/



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
                        showPopupUserHashtags(contact);
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


    @Override
    public void addHashtagsToSelectedContacts(String hashtag) {

    }

    @Override
    public void addNewTagToContact(String hashtag, Contact contact) {


        EditText linearLayout2 = (EditText) mainView.findViewById(R.id.hashTagsEdit);

        String s = linearLayout2.getText().toString().trim();
        String[] magicSplit = s.split(" ");

        //String findStr = s.toString();

        ArrayList<String> listHash = new ArrayList<>();
        if (magicSplit.length > 0) {
            //findStr = magicSplit[magicSplit.length - 1];

            for (int i = 0; i < magicSplit.length; i++) {
                if (magicSplit[i].length() > 1 && magicSplit[i].charAt(0) == '#')
                    listHash.add(magicSplit[i].trim());
            }
        }

        for (String h : listHash) {
            if (h.compareTo(hashtag) == 0) {
                Toast toast = Toast.makeText(mainView.getContext(), "Hashtag already  exist", Toast.LENGTH_SHORT);
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
                        listHash.add(hashtag.trim());


                        if (popupUserHashtags.findViewById(R.id.ContactHashLinear).getVisibility() == View.VISIBLE) {

                            ArrayList<String> tags = new ArrayList<>();


                            LinearLayout linearLayout = (LinearLayout) popupUserHashtags.findViewById(R.id.containerHashTags);
                            linearLayout.removeAllViews();

                            for (String h : listHash) {
                                TextView text = new TextView(getActivity());
                                text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                                DisplayMetrics metrics = mainView.getContext().getResources().getDisplayMetrics();
                                text.setLayoutParams(new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                text.setTextColor(getResources().getColor(R.color.colorPrimary));
                                text.setText(h + " ");
                                text.setOnClickListener(v -> adapterC.searchByHashTagValue(h));
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

                       /* MainActivity.typeUpdate = "EDIT";
                        MainActivity.nameToUpd.add(contact.getName());
                        if (contact.listOfContacts == null || contact.listOfContacts.isEmpty())
                            MainActivity.nameToUpdTypeContact.add(true);
                        else
                            MainActivity.nameToUpdTypeContact.add(false);*/

                        /*EventBus.getDefault().post(new UpdateFile());*/
                        Toast.makeText(mainView.getContext(), "Hashtag successfully added", Toast.LENGTH_SHORT).show();
                        //ContactsFragment.UPD_ALL = true;
                        //addHashtagPreview(tag);
                    })
                    .setNegativeButton("No", (dialog, id) -> dialog.cancel());

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

        ((EditText) popupUserHashtags.findViewById(R.id.hashtagsList)).append(hashtag.trim() + " ");


        linearLayout2.setText("");
        for (String t : listHash) {
            linearLayout2.append(t.trim() + " ");
        }
        linearLayout2.append(hashtag.trim() + " ");

    }

    public void deleteHashTagsFromUser(String hashtag, Contact contact) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getActivity());
        alertDialogBuilder.setTitle("Do you want to delete " + hashtag + " ?");
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> {

                    EditText linearLayout2 = (EditText) mainView.findViewById(R.id.hashTagsEdit);

                    String s = linearLayout2.getText().toString().trim();
                    String[] magicSplit = s.split(" ");

                    //String findStr = s.toString();

                    ArrayList<String> listHash = new ArrayList<>();
                    if (magicSplit.length > 0) {
                        //findStr = magicSplit[magicSplit.length - 1];

                        for (int i = 0; i < magicSplit.length; i++) {
                            if (magicSplit[i].length() > 1 && magicSplit[i].charAt(0) == '#')
                                listHash.add(magicSplit[i].trim());
                        }
                    }

                    for (String ss : listHash) {
                        if (ss.equalsIgnoreCase(hashtag.trim())) {
                            listHash.remove(ss);
                            break;
                        }
                    }

                    linearLayout2.setText("");
                    for (String t : listHash) {
                        linearLayout2.append(t + " ");
                    }


                    if (popupUserHashtags != null && popupUserHashtags.getVisibility() == View.VISIBLE) {
                        showPopupUserHashtags(contact);
                    }

                    // showPopupUserHashtags(selectedContact);
                    //updateHashTags();

                    //contactAdapter.getContactFragment().initRecyclerHashTags();

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
        /*if(listC.size() == 0){
            popupPositionEdit.setVisibility(View.GONE);
        }else {
            popupPositionEdit.setVisibility(View.VISIBLE);*/

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
        RecyclerView.LayoutManager mostLayoutManager = new GridLayoutManager(mainView.getContext(), 1);
        containerCompanies.setLayoutManager(mostLayoutManager);
        containerCompanies.setItemAnimator(new DefaultItemAnimator());
        containerCompanies.setAdapter(selectPositionAdapter);

        //openedViews.add(popupPositionEdit);

           /* if(!((EditText) mainView.findViewById(R.id.company_edit)).isFocused() ) {
                popupPositionEdit.setVisibility(View.GONE);
                System.out.println("No FOCUS");
            }*/
        // }
    }

    public void afterTextChangeCompany(CompanySelectInterface comp) {


       /* if(!((EditText) mainView.findViewById(R.id.company_title_edit)).isFocused() ) {
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
        RecyclerView.LayoutManager mostLayoutManager = new GridLayoutManager(mainView.getContext(), 1);
        containerCompanies.setLayoutManager(mostLayoutManager);
        containerCompanies.setItemAnimator(new DefaultItemAnimator());
        containerCompanies.setAdapter(selectCompanyAdapter);


    }


    public void OnTouchMethodExtract(TextView textview) {
        System.out.println("DOWN");
        checkClick = false;
        System.out.println("COLOR = " + textview.getTextColors().getDefaultColor());
        int colorFrom;
        String s = getActivity().getResources().getResourceEntryName(textview.getId());

        colorFrom = getActivity().getResources().getColor(R.color.primary);

        int colorTo = getActivity().getResources().getColor(R.color.md_deep_orange_300);
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(50); // milliseconds
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                textview.setTextColor((int) animator.getAnimatedValue());
                //  imageview.setColorFilter((int) animator.getAnimatedValue());
                // textview.setTypeface(null, Typeface.BOLD);
            }
        });
        colorAnimation.start();
    }


    public void OnCalcelTouchMethodExtract(TextView textView) {
        int colorFrom = getActivity().getResources().getColor(R.color.md_deep_orange_300);
        String s = getActivity().getResources().getResourceEntryName(textView.getId());
        int colorTo;
        colorTo = getActivity().getResources().getColor(R.color.primary);

        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(300); // milliseconds
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


    public void OnUpTouchMethodExtract(TextView textview) {
        if (!checkClick) {
            int colorFrom = getActivity().getResources().getColor(R.color.md_deep_orange_300);
            //   int colorTo = getResources().getColor(R.color.colorPrimaryDark);
            String s = getActivity().getResources().getResourceEntryName(textview.getId());
            int colorTo;
            colorTo = getActivity().getResources().getColor(R.color.primary);
            ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
            colorAnimation.setDuration(50); // milliseconds
            colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    textview.setTextColor((int) animator.getAnimatedValue());
                    //  imageview.setColorFilter((int) animator.getAnimatedValue());
                    //textview.setTypeface(null, Typeface.NORMAL);
                }

            });
            colorAnimation.start();
        }
    }


    public void OnMoveTouchMethodExtract(TextView textView, MotionEvent motionEvent) {
        int[] location = new int[2];
        textView.getLocationInWindow(location);
        int leftX = 0;
        int rightX = leftX + textView.getWidth();
        int topY = 0;
        int bottomY = topY + textView.getHeight();
        float xCurrent = motionEvent.getX();
        float yCurrent = motionEvent.getY();
        System.out.println(leftX + ", " + rightX + ", " + topY + ", " + bottomY + ",== " + location[0] + ", = " + location[1] + ", ==== " + xCurrent + ", " + yCurrent);
        if (xCurrent > rightX || xCurrent < leftX || yCurrent > bottomY || yCurrent < topY) {
            if (!checkClick) {
                checkClick = true;
                System.out.println("UPKI222222");

                String s = getActivity().getResources().getResourceEntryName(textView.getId());
                int colorTo2;


                colorTo2 = getActivity().getResources().getColor(R.color.primary);


                int colorFrom = getActivity().getResources().getColor(R.color.md_deep_orange_300);
                // int colorTo = textView.getTextColors().getDefaultColor();
                //colorTo2 = textView.getTextColors().getDefaultColor();
                //  ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
                //  colorAnimation.setDuration(1000); // milliseconds

                ValueAnimator colorAnimation2 = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo2);
                colorAnimation2.setDuration(50); // milliseconds


                colorAnimation2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animator) {
                        textView.setTextColor((int) animator.getAnimatedValue());
                        //  imageView.setColorFilter((int) animator.getAnimatedValue());
                        //    textView.setTypeface(null, Typeface.NORMAL);
                    }

                });
                colorAnimation2.start();


            }

        }
    }


    private void addHashtagsToProfile(ArrayList<String> hashtags) {

        String addHashtags = "";

        String[] hashList = ((EditText) getActivity().findViewById(R.id.hashTagsEdit)).getText().toString().split(" ");


        ArrayList<String> listOfHashtags2 = new ArrayList<>();

        for (int i = 0; i < hashList.length; i++) {
            if (hashList[i].length() > 1 && String.valueOf(hashList[i].charAt(0)).compareTo("#") == 0)
                listOfHashtags2.add(hashList[i]);
        }


        for (String hashTag : listOfHashtags2) {
            for (int indexI = 0; indexI < hashtags.size(); indexI++) {
                String hashtag = hashtags.get(indexI);
                if (hashTag.equals(hashtag)) {
                    hashtags.remove(hashtag);
                    //   Toast.makeText(mainView.getContext(), "Hashtag "+hashtag+" already exist", Toast.LENGTH_SHORT).show();
                }
            }
        }


        //   containerHashTags.addView(text);
        EditText editText = (EditText) mainView.findViewById(R.id.hashTagsEdit);
        for (String hashtag : hashtags) {


            if (editText.length() == 0) {
                editText.append(hashtag + " ");
            } else {
                editText.append(" " + hashtag + " ");
            }


        }


        Toast.makeText(mainView.getContext(), "Hashtags successfully added", Toast.LENGTH_SHORT).show();

    }


    private void addHashtagToProfile(String hashtag) {


        EditText editText = (EditText) mainView.findViewById(R.id.hashTagsEdit);
        if (editText.length() == 0) {
            editText.append(hashtag + " ");
        } else {
            editText.append(" " + hashtag + " ");
        }


        //    realm.beginTransaction();
        //    createdContact.getListOfHashtags().add(new HashTag(hashtag.trim()));
        //    realm.commitTransaction();
        //  ContactCacheService.updateContact(createdContact,mainView.getContext());
        //  EventBus.getDefault().post(new UpdateFile());
                   /* HorizontalScrollView scrollView = (HorizontalScrollView) mainView.findViewById(R.id.scrollHorizontal);
                    if(scrollView.getChildCount()>0) scrollView.removeAllViews();
                    scrollView.addView(containerHashTags);
                    scrollView.setSmoothScrollingEnabled(false);
                    scrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
                    scrollView.setSmoothScrollingEnabled(true);*/
        //contactAdapter.getContactFragment().initRecyclerHashTags();
        //  ContactAdapter contactAdapter = ContactAdapter.contactAd;
        //   contactAdapter.getContactFragment().initRecyclerHashTags();
        // updateHashTags();
        //System.out.println("PARENT = "+getParentFragment().getClass().getName());
        Toast.makeText(mainView.getContext(), "Hashtag successfully added", Toast.LENGTH_SHORT).show();
               /* })
                .setNegativeButton("No", (dialog, id) -> dialog.cancel());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();*/
    }


    /*private void updateHashTags(){
        LinearLayout containerHashTags = (LinearLayout) mainView.findViewById(R.id.containerHashTags);
        if( containerHashTags.getChildCount() > 0)
            containerHashTags.removeAllViews();
        if(createdContact.getListOfHashtags() != null)
            for (HashTag hashTag:createdContact.getListOfHashtags()) {

                TextView text = new TextView(getActivity());
                text.setTextSize(TypedValue.COMPLEX_UNIT_DIP,14);
                text.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                text.setTextColor(getResources().getColor(R.color.colorPrimary));
                text.setText(hashTag.getHashTagValue()+" ");
                text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        System.out.println("TEXT еее= "+hashTag.getHashTagValue());
                        //   contactAdapter.searchByHashTagValue(hashTag.getHashTagValue());
                    }
                });
                //   text.setOnClickListener(v -> contactAdapter.searchByHashTagValue(hashTag.getHashTagValue()));
                text.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        deleteHashTagsFromUser(hashTag.getHashTagValue(),createdContact);
                        return true;
                    }
                });


                containerHashTags.addView(text);
            }


        System.out.println("UPDATE HASH LIST");
    }*/


   /* public void deleteHashTagsFromUser(String hashtag,Contact contact){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getActivity());
        alertDialogBuilder.setTitle("Do you want to delete "+hashtag+" ?");
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> {
                    RealmConfiguration contextRealm = new RealmConfiguration.
                            Builder().
                            deleteRealmIfMigrationNeeded().
                            build();
                    Realm realm = Realm.getInstance(contextRealm);

                    RealmList<HashTag> listOfHashtags = createdContact.getListOfHashtags();
                    for (int indexI = 0; indexI< listOfHashtags.size(); indexI++) {
                        if(listOfHashtags.get(indexI).getHashTagValue().compareTo(hashtag) == 0){
                            realm.beginTransaction();
                            listOfHashtags.remove(indexI);
                            realm.commitTransaction();
                        }

                    }
                    realm.beginTransaction();
                    createdContact.setListOfHashtags(listOfHashtags);
                    createdContact.getListOfHashtags().remove(new HashTag(hashtag));
                    realm.commitTransaction();
                  //  ContactCacheService.updateContact(createdContact,mainView.getContext());
                  //  EventBus.getDefault().post(new UpdateFile());
                    closeOtherPopup();

                    // showPopupUserHashtags(selectedContact);
                    //updateHashTags();

                //    contactAdapter.getContactFragment().initRecyclerHashTags();

                })
                .setNegativeButton("No", (dialog, id) -> {
                    dialog.cancel();
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }*/


    public void addNoteToContact(String note) {


        Realm realm = Realm.getDefaultInstance(); //-

        //   contactNumberAdapter.updateContactsList(selectedContact.listOfContactInfo);
        //   contactNumberAdapter = new ContactNumberAdapter(mainView.getContext(), selectedContact.listOfContactInfo,this);
        /*if(createdContact.listOfContacts == null || createdContact.listOfContacts.isEmpty())
            contactProfileDataFragment.contactNumberAdapter.updateContactsList(createdContact.listOfContactInfo);*/

        String[] splitNote = ((EditText) getActivity().findViewById(R.id.magic_edit_text)).getText().toString().split("/");
        String noteLink = ((EditText) getActivity().findViewById(R.id.magic_edit_text)).getText().toString();


        String textMagic = ((EditText) getActivity().findViewById(R.id.magic_edit_text)).getText().toString();


        String mainWay = "";
        if (textMagic.contains(".com"))
            mainWay = textMagic.substring(0, textMagic.indexOf(".com") + 4);
        else if (textMagic.contains(".org"))
            mainWay = textMagic.substring(0, textMagic.indexOf(".org") + 4);



                   /* else if((mainWay.toLowerCase().equals("facebook.com") || mainWay.toLowerCase().equals("https://facebook.com")) || (mainWay.toLowerCase().equals("vk.com")||mainWay.toLowerCase().equals("https://vk.com")) || (mainWay.toLowerCase().equals("linkedin.com")|| mainWay.toLowerCase().equals("https://www.linkedin.com")) || (mainWay.toLowerCase().equals("instagram.com") || mainWay.toLowerCase().equals("https://www.instagram.com")) || (mainWay.toLowerCase().equals("viber.com") || mainWay.toLowerCase().equals("https://www.viber.com")) || (mainWay.toLowerCase().equals("whatsapp.com") || mainWay.toLowerCase().equals("https://www.whatsapp.com"))  || (mainWay.toLowerCase().equals("telegram.org") || mainWay.toLowerCase().equals("https://telegram.org")) || (mainWay.toLowerCase().equals("skype.com") || mainWay.toLowerCase().equals("https://www.skype.com"))){
                        if(mainWay.length() <textMagic.length()) {


                        }*/


        if (createdContact.getSocialModel() == null && socialModel == null) {
            realm.beginTransaction();
            socialModel = new SocialModel();

            createdContact.setSocialModel(socialModel);
            realm.commitTransaction();

        }
        boolean socialCheck = true;

        if (ClipboardType.isFacebook(textMagic)) {
            if (createdContact.getSocialModel() != null && createdContact.getSocialModel().getFacebookLink() != null &&  SocialEq.checkStrSocials(createdContact.getSocialModel().getFacebookLink(), noteLink) /*createdContact.getSocialModel().getFacebookLink().equals(noteLink)*/) {
                Toast.makeText(getActivity(), "Data already exists", Toast.LENGTH_SHORT).show();
                return;
            }

            if (createdContact.getSocialModel() != null && createdContact.getSocialModel().getFacebookLink() != null && !createdContact.getSocialModel().getFacebookLink().isEmpty()) {
                socialCheck = true;
            } else {

                if (noteLink.contains("?") && !noteLink.contains("watch?")  && !noteLink.contains("?id=") && !noteLink.contains("?v=") &&  !noteLink.contains("?story")) {
                    int ind = noteLink.indexOf("?");
                    if (ind != -1)
                        noteLink = noteLink.substring(0, ind);
                }

                realm.beginTransaction();
                createdContact.getSocialModel().setFacebookLink(noteLink);


                Drawable color = new ColorDrawable(Color.parseColor("#475993"));
                Drawable image = getResources().getDrawable(R.drawable.icn_social_facebook2);
                LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
                ((ImageView) mainView.findViewById(R.id.facebook_icon)).setImageDrawable(ld);


                // ((ImageView) mainView.findViewById(R.id.facebook_icon)).setImageResource(R.drawable.icn_social_facebook);
                if (socialPopup != null)
                    ((ImageView) socialPopup.findViewById(R.id.facebook_icon)).setImageResource(R.drawable.icn_social_facebook);
                createdContact.hasFacebook = true;
                realm.commitTransaction();
                initIconColor(createdContact, mainView);
                //  ContactCacheService.updateContact(createdContact, mainView.getContext());
                //  EventBus.getDefault().post(new UpdateFile());
                socialCheck = false;
            }
        } else if (ClipboardType.isVk(textMagic)) {
            if (createdContact.getSocialModel() != null && createdContact.getSocialModel().getVkLink() != null &&  SocialEq.checkStrSocials(createdContact.getSocialModel().getVkLink(), noteLink)) {
                Toast.makeText(getActivity(), "Data already exists", Toast.LENGTH_SHORT).show();
                return;
            }

            if (createdContact.getSocialModel() != null && createdContact.getSocialModel().getVkLink() != null && !createdContact.getSocialModel().getVkLink().isEmpty()) {
                socialCheck = true;

            } else {

                if (noteLink.contains("?") && !noteLink.contains("watch?")  && !noteLink.contains("?id")) {
                    int ind = noteLink.indexOf("?");
                    if (ind != -1)
                        noteLink = noteLink.substring(0, ind);
                }

                realm.beginTransaction();
                createdContact.getSocialModel().setVkLink(noteLink);


                //((ImageView) mainView.findViewById(R.id.vk_icon)).setImageResource(R.drawable.icn_social_vk);
                Drawable color = new ColorDrawable(Color.parseColor("#507299"));
                Drawable image = getResources().getDrawable(R.drawable.icn_social_vk2);
                LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
                ((ImageView) mainView.findViewById(R.id.vk_icon)).setImageDrawable(ld);

                if (socialPopup != null)
                    ((ImageView) socialPopup.findViewById(R.id.vk_icon)).setImageResource(R.drawable.icn_social_vk);
                createdContact.hasVk = true;
                realm.commitTransaction();
                initIconColor(createdContact, mainView);
                ///   ContactCacheService.updateContact(createdContact, mainView.getContext());
                //   EventBus.getDefault().post(new UpdateFile());
                socialCheck = false;
            }
        } else if (ClipboardType.isLinkedIn(textMagic)) {
            if (createdContact.getSocialModel() != null && createdContact.getSocialModel().getLinkedInLink() != null && SocialEq.checkStrSocials(createdContact.getSocialModel().getLinkedInLink(), noteLink)) {
                Toast.makeText(getActivity(), "Data already exists", Toast.LENGTH_SHORT).show();
                return;
            }

            if (createdContact.getSocialModel() != null && createdContact.getSocialModel().getLinkedInLink() != null && !createdContact.getSocialModel().getLinkedInLink().isEmpty()) {
                socialCheck = true;
            } else {

                if (noteLink.contains("?") && !noteLink.contains("watch?")) {
                    int ind = noteLink.indexOf("?");
                    if (ind != -1)
                        noteLink = noteLink.substring(0, ind);
                }

                realm.beginTransaction();
                createdContact.getSocialModel().setLinkedInLink(noteLink);
                realm.commitTransaction();
                // ((ImageView) mainView.findViewById(R.id.link_icon)).setImageResource(R.drawable.icn_social_linkedin);

                Drawable color = new ColorDrawable(Color.parseColor("#0077B7"));
                Drawable image = getResources().getDrawable(R.drawable.icn_social_linked2);
                LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
                ((ImageView) mainView.findViewById(R.id.link_icon)).setImageDrawable(ld);

                if (socialPopup != null)
                    ((ImageView) socialPopup.findViewById(R.id.link_icon)).setImageResource(R.drawable.icn_social_linkedin);
                createdContact.hasLinked = true;
                initIconColor(createdContact, mainView);
                //   ContactCacheService.updateContact(createdContact, mainView.getContext());
                //   EventBus.getDefault().post(new UpdateFile());
                socialCheck = false;
            }
        } else if (ClipboardType.isInsta(textMagic)) {
            if (createdContact.getSocialModel() != null && createdContact.getSocialModel().getInstagramLink() != null && SocialEq.checkStrSocials(createdContact.getSocialModel().getInstagramLink(), noteLink)) {
                Toast.makeText(getActivity(), "Data already exists", Toast.LENGTH_SHORT).show();
                return;
            }

            if (createdContact.getSocialModel() != null && createdContact.getSocialModel().getInstagramLink() != null && !createdContact.getSocialModel().getInstagramLink().isEmpty()) {
                socialCheck = true;
            } else {

                realm.beginTransaction();

                String username = noteLink;


                if (username.contains("?utm")) {
                    int ind = username.indexOf('?');
                    if (ind != -1)
                        username = username.substring(0, ind);

                }

                if (username.charAt(username.length() - 1) == '/') {
                    username = username.substring(0, username.length() - 1);
                    //contactsService.updateNote(contact.getIdContact(), note, username);
                }

                if (!username.toLowerCase().contains("instagram.com")) {
                    username = "https://instagram.com/" + username;
                }

                if (username.contains("?") && !username.contains("watch?")) {
                    int ind = username.indexOf("?");
                    if (ind != -1)
                        username = username.substring(0, ind);
                }

                createdContact.getSocialModel().setInstagramLink(username);
                // ((ImageView) mainView.findViewById(R.id.inst_icon)).setImageResource(R.drawable.icn_social_instagram);

                Drawable color = new ColorDrawable(Color.parseColor("#8a3ab9"));
                Drawable image = getResources().getDrawable(R.drawable.icn_social_ints2);
                LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
                ((ImageView) mainView.findViewById(R.id.inst_icon)).setImageDrawable(ld);

                if (socialPopup != null)
                    ((ImageView) socialPopup.findViewById(R.id.instagram_icon)).setImageResource(R.drawable.icn_social_instagram);
                createdContact.hasInst = true;
                realm.commitTransaction();
                initIconColor(createdContact, mainView);
                //   ContactCacheService.updateContact(createdContact, mainView.getContext());
                //   EventBus.getDefault().post(new UpdateFile());
                socialCheck = false;
            }
        }

        if (mainWay.length() > 1 && mainWay.length() < textMagic.length()) {
            // realm.beginTransaction();
            switch (mainWay.toLowerCase()) {

                case "viber.com":
                case "https://www.viber.com": {

                    if (createdContact.getSocialModel() != null && createdContact.getSocialModel().getViberLink() != null && createdContact.getSocialModel().getViberLink().equals(noteLink)) {
                        Toast.makeText(getActivity(), "Data already exists", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (noteLink.contains("?") && !noteLink.contains("watch?")) {
                        int ind = noteLink.indexOf("?");
                        if (ind != -1)
                            noteLink = noteLink.substring(0, ind);
                    }

                    realm.beginTransaction();
                    createdContact.getSocialModel().setViberLink(noteLink);
                    // ((ImageView) mainView.findViewById(R.id.viber_icon)).setImageResource(R.drawable.icn_social_viber);

                    Drawable color = new ColorDrawable(Color.parseColor("#6F3FAA"));
                    Drawable image = getResources().getDrawable(R.drawable.icn_social_viber2);
                    LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
                    ((CircleImageView) mainView.findViewById(R.id.viber_icon)).setImageDrawable(ld);

                    if (socialPopup != null)
                        ((ImageView) socialPopup.findViewById(R.id.viber_icon)).setImageResource(R.drawable.icn_social_viber);
                    createdContact.hasViber = true;
                    realm.commitTransaction();
                    //    ContactCacheService.updateContact(createdContact, mainView.getContext());
                    //    EventBus.getDefault().post(new UpdateFile());
                    socialCheck = false;
                    break;
                }
                case "whatsapp.com":
                case "https://www.whatsapp.com": {

                    if (createdContact.getSocialModel() != null && createdContact.getSocialModel().getWhatsappLink() != null && createdContact.getSocialModel().getWhatsappLink().equals(noteLink)) {
                        Toast.makeText(getActivity(), "Data already exists", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (noteLink.contains("?") && !noteLink.contains("watch?")) {
                        int ind = noteLink.indexOf("?");
                        if (ind != -1)
                            noteLink = noteLink.substring(0, ind);
                    }

                    realm.beginTransaction();
                    createdContact.getSocialModel().setWhatsappLink(noteLink);
                    //   ((ImageView) mainView.findViewById(R.id.whatsapp_icon)).setImageResource(R.drawable.icn_social_whatsapp);

                    Drawable color = new ColorDrawable(Color.parseColor("#75B73B"));
                    Drawable image = getResources().getDrawable(R.drawable.icn_social_whatsapp3);
                    LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
                    ((CircleImageView) mainView.findViewById(R.id.whatsapp_icon)).setImageDrawable(ld);

                    if (socialPopup != null)
                        ((ImageView) socialPopup.findViewById(R.id.whatsapp_icon)).setImageResource(R.drawable.icn_social_whatsapp);
                    createdContact.hasWhatsapp = true;
                    realm.commitTransaction();
                    //     ContactCacheService.updateContact(createdContact, mainView.getContext());
                    //    EventBus.getDefault().post(new UpdateFile());
                    socialCheck = false;
                    break;
                }
                case "telegram.org":
                case "https://telegram.org": {

                    if (createdContact.getSocialModel() != null && createdContact.getSocialModel().getTelegramLink() != null && createdContact.getSocialModel().getTelegramLink().equals(noteLink)) {
                        Toast.makeText(getActivity(), "Data already exists", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (noteLink.contains("?") && !noteLink.contains("watch?")) {
                        int ind = noteLink.indexOf("?");
                        if (ind != -1)
                            noteLink = noteLink.substring(0, ind);
                    }

                    realm.beginTransaction();
                    createdContact.getSocialModel().setTelegramLink(noteLink);
                    //   ((ImageView) mainView.findViewById(R.id.telegram_icon)).setImageResource(R.drawable.icn_social_telegram);

                    Drawable color = new ColorDrawable(Color.parseColor("#7AA5DA"));
                    Drawable image = getResources().getDrawable(R.drawable.icn_social_telegram2);
                    LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
                    ((CircleImageView) mainView.findViewById(R.id.telegram_icon)).setImageDrawable(ld);

                    if (socialPopup != null)
                        ((ImageView) socialPopup.findViewById(R.id.telegram_icon)).setImageResource(R.drawable.icn_social_telegram);
                    createdContact.hasTelegram = true;
                    realm.commitTransaction();
                    //     ContactCacheService.updateContact(createdContact, mainView.getContext());
                    //     EventBus.getDefault().post(new UpdateFile());
                    socialCheck = false;
                    break;
                }
                case "skype.com":
                case "https://www.skype.com": {

                    if (createdContact.getSocialModel() != null && createdContact.getSocialModel().getSkypeLink() != null && createdContact.getSocialModel().getSkypeLink().equals(noteLink)) {
                        Toast.makeText(getActivity(), "Data already exists", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    realm.beginTransaction();

                    String username = noteLink.substring(noteLink.indexOf(".com") + 5, noteLink.length());
                    if (username.charAt(username.length() - 1) == '/')
                        username = username.substring(0, username.length() - 1);
                    System.out.println("USERNAME Stype = " + username);

                    createdContact.getSocialModel().setSkypeLink(username);
                    //  ((ImageView) mainView.findViewById(R.id.skype_icon)).setImageResource(R.drawable.icn_social_skype);

                    Drawable color = new ColorDrawable(Color.parseColor("#1eb8ff"));
                    Drawable image = getResources().getDrawable(R.drawable.icn_social_skype2);
                    LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
                    ((CircleImageView) mainView.findViewById(R.id.skype_icon)).setImageDrawable(ld);

                    if (socialPopup != null)
                        ((ImageView) socialPopup.findViewById(R.id.skype_icon)).setImageResource(R.drawable.icn_social_skype);
                    createdContact.hasSkype = true;
                    realm.commitTransaction();
                    //   ContactCacheService.updateContact(createdContact, mainView.getContext());
                    //   EventBus.getDefault().post(new UpdateFile());
                    socialCheck = false;
                    break;
                }
            }
            //  realm.commitTransaction();
        }

        if (ClipboardType.isTwitter(noteLink)) {
            if (createdContact.getSocialModel() != null && createdContact.getSocialModel().getTwitterLink() != null && SocialEq.checkStrSocials(createdContact.getSocialModel().getTwitterLink(), noteLink)) {
                Toast.makeText(getActivity(), "Data already exists", Toast.LENGTH_SHORT).show();
                return;
            }

            if (createdContact.getSocialModel() != null && createdContact.getSocialModel().getTwitterLink() != null && !createdContact.getSocialModel().getTwitterLink().isEmpty()) {
                socialCheck = true;
            } else socialCheck = false;

            if (noteLink.contains("?") && !noteLink.contains("watch?")) {
                int ind = noteLink.indexOf("?");
                if (ind != -1)
                    noteLink = noteLink.substring(0, ind);
            }

            if (!socialCheck) {
                realm.beginTransaction();
                createdContact.getSocialModel().setTwitterLink(noteLink);


                //((ImageView) mainView.findViewById(R.id.vk_icon)).setImageResource(R.drawable.icn_social_vk);
                Drawable color = new ColorDrawable(Color.parseColor("#2ca7e0"));
                Drawable image = getResources().getDrawable(R.drawable.ic_twitter_white);
                LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
                //((ImageView) mainView.findViewById(R.id.vk_icon)).setImageDrawable(ld);

                if (socialPopup != null)
                    ((ImageView) socialPopup.findViewById(R.id.twitter_icon)).setImageDrawable(ld);
                createdContact.hasTwitter = true;
                realm.commitTransaction();
                initIconColor(createdContact, mainView);
                ///   ContactCacheService.updateContact(createdContact, mainView.getContext());
                //   EventBus.getDefault().post(new UpdateFile());
                socialCheck = false;
            }
        } else if (ClipboardType.isYoutube(noteLink) && (noteLink.contains("user") || noteLink.contains("channel"))) {
            if (createdContact.getSocialModel() != null && createdContact.getSocialModel().getYoutubeLink() != null && SocialEq.checkStrSocials(createdContact.getSocialModel().getYoutubeLink(), noteLink)) {
                Toast.makeText(getActivity(), "Data already exists", Toast.LENGTH_SHORT).show();
                return;
            }

            if (createdContact.getSocialModel() != null && createdContact.getSocialModel().getYoutubeLink() != null && !createdContact.getSocialModel().getYoutubeLink().isEmpty()) {
                socialCheck = true;
            } else socialCheck = false;

            if (noteLink.contains("?") && !noteLink.contains("watch?")) {
                int ind = noteLink.indexOf("?");
                if (ind != -1)
                    noteLink = noteLink.substring(0, ind);
            }

            if (!socialCheck) {
                realm.beginTransaction();
                createdContact.getSocialModel().setYoutubeLink(noteLink);


                //((ImageView) mainView.findViewById(R.id.vk_icon)).setImageResource(R.drawable.icn_social_vk);
                Drawable color = new ColorDrawable(Color.parseColor("#ed2524"));
                Drawable image = getResources().getDrawable(R.drawable.ic_youtube_white);
                LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
                //((ImageView) mainView.findViewById(R.id.vk_icon)).setImageDrawable(ld);

                if (socialPopup != null)
                    ((ImageView) socialPopup.findViewById(R.id.youtube_icon)).setImageDrawable(ld);
                createdContact.hasYoutube = true;
                realm.commitTransaction();
                initIconColor(createdContact, mainView);
                ///   ContactCacheService.updateContact(createdContact, mainView.getContext());
                //   EventBus.getDefault().post(new UpdateFile());
                socialCheck = false;
            }
        } else if (ClipboardType.isMedium(noteLink) && (noteLink.contains("com/@"))) {
            if (createdContact.getSocialModel() != null && createdContact.getSocialModel().getMediumLink() != null && SocialEq.checkStrSocials(createdContact.getSocialModel().getMediumLink(), noteLink)) {
                Toast.makeText(getActivity(), "Data already exists", Toast.LENGTH_SHORT).show();
                return;
            }

            if (createdContact.getSocialModel() != null && createdContact.getSocialModel().getMediumLink() != null && !createdContact.getSocialModel().getMediumLink().isEmpty()) {
                socialCheck = true;
            } else socialCheck = false;

            if (noteLink.contains("?") && !noteLink.contains("watch?")) {
                int ind = noteLink.indexOf("?");
                if (ind != -1)
                    noteLink = noteLink.substring(0, ind);
            }

            if (!socialCheck) {
                realm.beginTransaction();
                createdContact.getSocialModel().setMediumLink(noteLink);


                //((ImageView) mainView.findViewById(R.id.vk_icon)).setImageResource(R.drawable.icn_social_vk);
                Drawable color = new ColorDrawable(Color.parseColor("#000000"));
                Drawable image = getResources().getDrawable(R.drawable.medium_white);
                LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
                //((ImageView) mainView.findViewById(R.id.vk_icon)).setImageDrawable(ld);

                if (socialPopup != null)
                    ((ImageView) socialPopup.findViewById(R.id.medium_icon)).setImageDrawable(ld);
                createdContact.hasMedium = true;
                realm.commitTransaction();
                initIconColor(createdContact, mainView);
                ///   ContactCacheService.updateContact(createdContact, mainView.getContext());
                //   EventBus.getDefault().post(new UpdateFile());
                socialCheck = false;
            }
        }

        if (socialCheck) {
            if (createdContact.listOfContacts == null || createdContact.listOfContacts.isEmpty()) {
                // createdContact.addNoteToContact(String.valueOf(createdContact.getId()),note);
                String noteLinkfinal = note;
                if (ClipboardType.isWeb(noteLinkfinal.trim())) {
                    if (noteLinkfinal.contains("?") && !noteLinkfinal.contains("watch?")) {
                        int ind = noteLinkfinal.indexOf("?");
                        if (ind != -1)
                            noteLinkfinal = noteLinkfinal.substring(0, ind);
                    }
                }


                realm.beginTransaction();
                createdContact.addNote(noteLinkfinal);
                realm.commitTransaction();
                //   ContactCacheService.updateContact(selectedContact, mainView.getContext());
            }
        }
        if (socialPopup != null && socialPopup.getVisibility() == View.VISIBLE) {
            showSocialPopup(createdContact);
        }
        //   ((EditText)getActivity().findViewById(R.id.magic_edit_text)).getText().clear();

        realm.close();

    }

    private void getMergedData() {
        ArrayList<String> listSetedHashtags = new ArrayList<>();
        ArrayList<String> listSavedNames = new ArrayList<>();
        ArrayList<String> listSavedCompany = new ArrayList<>();
        ArrayList<String> listSavedPossition = new ArrayList<>();
        ArrayList<ContactInfo> listOfContactInfo = new ArrayList<>();
        for (Contact contact : listMergedContacts) {

            if (contact.getPhotoURL() != null) {
                createdContact.setPhotoURL(contact.getPhotoURL());
            }

            if (!listSavedNames.contains(contact.getName())) {
                if (userName.getText().length() == 0) {
                    userName.setText(contact.getName());
                    listSavedNames.add(contact.getName());
                } else {
                    userName.setText((userName.getText() + " " + contact.getName()).trim());
                    listSavedNames.add(contact.getName());
                }
            }

            if (!listSavedCompany.contains(contact.getCompany())) {
                if (companyContact.getText().length() == 0) {
                    companyContact.setText(contact.getCompany());
                    listSavedCompany.add(contact.getCompany());
                } else {
                    if (contact.getCompany() != null) {
                        companyContact.setText(companyContact.getText() + " " + contact.getCompany());
                        listSavedCompany.add(contact.getCompany());
                    }
                }
            }

            if (!listSavedPossition.contains(contact.getCompanyPossition())) {
                if (possitionCompanyContact.getText().length() == 0) {
                    possitionCompanyContact.setText(contact.getCompanyPossition());
                    listSavedPossition.add(contact.getCompanyPossition());
                } else {
                    if (contact.getCompanyPossition() != null) {
                        possitionCompanyContact.setText(possitionCompanyContact.getText() + " " + contact.getCompanyPossition());
                        listSavedPossition.add(contact.getCompanyPossition());
                    }
                }
            }
            if (contact.getListOfHashtags() != null)
                for (HashTag hashTag : contact.getListOfHashtags()) {
                    if (hashTagList.getText().length() == 0) {
                        hashTagList.setText(hashTag.getHashTagValue());
                        listSetedHashtags.add(hashTag.getHashTagValue());
                    } else {
                        if (!listSetedHashtags.contains(hashTag.getHashTagValue())) {
                            hashTagList.setText(hashTagList.getText() + " " + hashTag);
                            listSetedHashtags.add(hashTag.getHashTagValue());
                        }
                    }
                }

            if (contact.listOfContactInfo != null)
                for (ContactInfo contactInfo : contact.listOfContactInfo) {
                    //if(contactInfo != null && contact.listOfContactInfo !=  null)
                    if (!listOfContactInfo.contains(contactInfo)) {
                        System.out.println("ADD");
                        if (createdContact.listOfContactInfo == null)
                            createdContact.listOfContactInfo = new RealmList<>();

                        createdContact.listOfContactInfo.add(contactInfo);
                        listOfContactInfo.add(contactInfo);
                    }
                }

            if (createdContact.getSocialModel() == null)
                createdContact.setSocialModel(new SocialModel());

            if (contact.getSocialModel() != null) {
                if (contact.hasFacebook || (contact.getSocialModel().getFacebookLink() != null && !contact.getSocialModel().getFacebookLink().isEmpty())) {
                /*    if(createdContact.hasFacebook && createdContact.getSocialModel().getFacebookLink() != null && !createdContact.getSocialModel().getFacebookLink().isEmpty()){

                        if(createdContact.listOfContactInfo == null)
                            createdContact.listOfContactInfo = new RealmList<>();

                        ContactInfo contactInfos = new ContactInfo("note", contact.getSocialModel().getFacebookLink(), false, false, false, true, false);
                        *//*createdContact.listOfContactInfo.add(contactInfo);*//*

                        for (ContactInfo contactInfo :contact.listOfContactInfo) {
                            //if(contactInfo != null && contact.listOfContactInfo !=  null)
                            if(!listOfContactInfo.contains(contactInfos)){
                                System.out.println("ADD");
                                if(createdContact.listOfContactInfo == null)
                                    createdContact.listOfContactInfo = new RealmList<>();

                                createdContact.listOfContactInfo.add(contactInfos);
                                listOfContactInfo.add(contactInfos);
                                contact.listOfContactInfo.add(contactInfos);
                            }
                        }


                    }else {*/
                    createdContact.hasFacebook = true;

                    if (createdContact.getSocialModel() != null) {
                        if (createdContact.getSocialModel().getFacebookLink() == null || createdContact.getSocialModel().getFacebookLink().isEmpty()) {
                            createdContact.getSocialModel().setFacebookLink(contact.getSocialModel().getFacebookLink());
                        } else if(!SocialEq.checkStrSocials(contact.getSocialModel().getFacebookLink(), createdContact.getSocialModel().getFacebookLink())){
                            if (createdContact.listOfContactInfo == null) {
                                createdContact.addNote(contact.getSocialModel().getFacebookLink());
                            } else {
                                boolean checkS = false;
                                for (ContactInfo contactInfo : createdContact.listOfContactInfo) {
                                    if (SocialEq.checkStrSocials(contactInfo.value, contact.getSocialModel().getFacebookLink())) {
                                        checkS = true;
                                        break;
                                    }
                                }
                                if (!checkS)
                                    createdContact.addNote(contact.getSocialModel().getFacebookLink());

                            }
                        }
                    }

                    //createdContact.getSocialModel().setFacebookLink(contact.getSocialModel().getFacebookLink());

                }


                if (contact.hasLinked || (contact.getSocialModel().getLinkedInLink() != null && !contact.getSocialModel().getLinkedInLink().isEmpty())) {
                    createdContact.hasLinked = true;
                    //createdContact.getSocialModel().setLinkedInLink(contact.getSocialModel().getLinkedInLink());

                    if (createdContact.getSocialModel() != null) {
                        if (createdContact.getSocialModel().getLinkedInLink() == null || createdContact.getSocialModel().getLinkedInLink().isEmpty())
                            createdContact.getSocialModel().setLinkedInLink(contact.getSocialModel().getLinkedInLink());
                        else if(!SocialEq.checkStrSocials(contact.getSocialModel().getLinkedInLink(), createdContact.getSocialModel().getLinkedInLink())){
                            if (createdContact.listOfContactInfo == null) {
                                createdContact.addNote(contact.getSocialModel().getLinkedInLink());
                            } else {
                                boolean checkS = false;
                                for (ContactInfo contactInfo : createdContact.listOfContactInfo) {
                                    if (SocialEq.checkStrSocials(contactInfo.value, contact.getSocialModel().getLinkedInLink())) {
                                        checkS = true;
                                        break;
                                    }
                                }
                                if (!checkS)
                                    createdContact.addNote(contact.getSocialModel().getLinkedInLink());

                            }
                        }
                    }
                }

                if (contact.hasVk || (contact.getSocialModel().getVkLink() != null && !contact.getSocialModel().getVkLink().isEmpty())) {
                    createdContact.hasVk = true;
                    //createdContact.getSocialModel().setVkLink(contact.getSocialModel().getVkLink());

                    if (createdContact.getSocialModel() != null) {
                        if (createdContact.getSocialModel().getVkLink() == null || createdContact.getSocialModel().getVkLink().isEmpty())
                            createdContact.getSocialModel().setVkLink(contact.getSocialModel().getVkLink());
                        else if(!SocialEq.checkStrSocials(contact.getSocialModel().getVkLink(), createdContact.getSocialModel().getVkLink())) {
                            if (createdContact.listOfContactInfo == null) {
                                createdContact.addNote(contact.getSocialModel().getVkLink());
                            } else {
                                boolean checkS = false;
                                for (ContactInfo contactInfo : createdContact.listOfContactInfo) {
                                    if (SocialEq.checkStrSocials(contactInfo.value, contact.getSocialModel().getVkLink())) {
                                        checkS = true;
                                        break;
                                    }
                                }
                                if (!checkS)
                                    createdContact.addNote(contact.getSocialModel().getVkLink());

                            }
                        }
                    }

                }

                if (contact.hasTwitter || (contact.getSocialModel().getTwitterLink() != null && !contact.getSocialModel().getTwitterLink().isEmpty())) {
                    createdContact.hasTwitter = true;
                    //createdContact.getSocialModel().setVkLink(contact.getSocialModel().getVkLink());

                    if (createdContact.getSocialModel() != null) {
                        if (createdContact.getSocialModel().getTwitterLink() == null || createdContact.getSocialModel().getTwitterLink().isEmpty())
                            createdContact.getSocialModel().setTwitterLink(contact.getSocialModel().getTwitterLink());
                        else if(!SocialEq.checkStrSocials(contact.getSocialModel().getTwitterLink(), createdContact.getSocialModel().getTwitterLink())){
                            if (createdContact.listOfContactInfo == null) {
                                createdContact.addNote(contact.getSocialModel().getTwitterLink());
                            } else {
                                boolean checkS = false;
                                for (ContactInfo contactInfo : createdContact.listOfContactInfo) {
                                    if (SocialEq.checkStrSocials(contactInfo.value, contact.getSocialModel().getTwitterLink())) {
                                        checkS = true;
                                        break;
                                    }
                                }
                                if (!checkS)
                                    createdContact.addNote(contact.getSocialModel().getTwitterLink());

                            }
                        }
                    }

                }

                if (contact.hasYoutube || (contact.getSocialModel().getYoutubeLink() != null && !contact.getSocialModel().getYoutubeLink().isEmpty())) {
                    createdContact.hasYoutube = true;
                    //createdContact.getSocialModel().setVkLink(contact.getSocialModel().getVkLink());

                    if (createdContact.getSocialModel() != null) {
                        if (createdContact.getSocialModel().getYoutubeLink() == null || createdContact.getSocialModel().getYoutubeLink().isEmpty())
                            createdContact.getSocialModel().setYoutubeLink(contact.getSocialModel().getYoutubeLink());
                        else if(!SocialEq.checkStrSocials(contact.getSocialModel().getYoutubeLink(), createdContact.getSocialModel().getYoutubeLink())){
                            if (createdContact.listOfContactInfo == null) {
                                createdContact.addNote(contact.getSocialModel().getYoutubeLink());
                            } else {
                                boolean checkS = false;
                                for (ContactInfo contactInfo : createdContact.listOfContactInfo) {
                                    if (SocialEq.checkStrSocials(contactInfo.value, contact.getSocialModel().getYoutubeLink())) {
                                        checkS = true;
                                        break;
                                    }
                                }
                                if (!checkS)
                                    createdContact.addNote(contact.getSocialModel().getYoutubeLink());

                            }
                        }
                    }

                }

                if (contact.hasMedium || (contact.getSocialModel().getMediumLink() != null && !contact.getSocialModel().getMediumLink().isEmpty())) {
                    createdContact.hasMedium = true;
                    //createdContact.getSocialModel().setVkLink(contact.getSocialModel().getVkLink());

                    if (createdContact.getSocialModel() != null) {
                        if (createdContact.getSocialModel().getMediumLink() == null || createdContact.getSocialModel().getMediumLink().isEmpty())
                            createdContact.getSocialModel().setMediumLink(contact.getSocialModel().getMediumLink());
                        else if(!SocialEq.checkStrSocials(contact.getSocialModel().getMediumLink(), createdContact.getSocialModel().getMediumLink())){
                            if (createdContact.listOfContactInfo == null) {
                                createdContact.addNote(contact.getSocialModel().getMediumLink());
                            } else {
                                boolean checkS = false;
                                for (ContactInfo contactInfo : createdContact.listOfContactInfo) {
                                    if (SocialEq.checkStrSocials(contactInfo.value, contact.getSocialModel().getMediumLink())) {
                                        checkS = true;
                                        break;
                                    }
                                }
                                if (!checkS)
                                    createdContact.addNote(contact.getSocialModel().getMediumLink());

                            }
                        }
                    }

                }

                if (contact.hasInst || (contact.getSocialModel().getInstagramLink() != null && !contact.getSocialModel().getInstagramLink().isEmpty())) {
                    createdContact.hasInst = true;
                    //createdContact.getSocialModel().setInstagramLink(contact.getSocialModel().getInstagramLink());

                    if (createdContact.getSocialModel() != null) {
                        if (createdContact.getSocialModel().getInstagramLink() == null || createdContact.getSocialModel().getInstagramLink().isEmpty())
                            createdContact.getSocialModel().setInstagramLink(contact.getSocialModel().getInstagramLink());
                        else if(!SocialEq.checkStrSocials(contact.getSocialModel().getInstagramLink(), createdContact.getSocialModel().getInstagramLink())){
                            if (createdContact.listOfContactInfo == null) {
                                createdContact.addNote(contact.getSocialModel().getInstagramLink());
                            } else {
                                boolean checkS = false;
                                for (ContactInfo contactInfo : createdContact.listOfContactInfo) {
                                    if (SocialEq.checkStrSocials(contactInfo.value, contact.getSocialModel().getInstagramLink())) {
                                        checkS = true;
                                        break;
                                    }
                                }
                                if (!checkS)
                                    createdContact.addNote(contact.getSocialModel().getInstagramLink());

                            }
                        }
                    }
                }
                //========
                if (contact.hasViber || (contact.getSocialModel().getViberLink() != null && !contact.getSocialModel().getViberLink().isEmpty())) {
                    createdContact.hasViber = true;
                    createdContact.getSocialModel().setViberLink(contact.getSocialModel().getViberLink());
                }

                if (contact.hasWhatsapp || (contact.getSocialModel().getWhatsappLink() != null && !contact.getSocialModel().getWhatsappLink().isEmpty())) {
                    createdContact.hasWhatsapp = true;
                    createdContact.getSocialModel().setWhatsappLink(contact.getSocialModel().getWhatsappLink());
                }

                if (contact.hasTelegram || (contact.getSocialModel().getTelegramLink() != null && !contact.getSocialModel().getTelegramLink().isEmpty())) {
                    createdContact.hasTelegram = true;
                    createdContact.getSocialModel().setTelegramLink(contact.getSocialModel().getTelegramLink());
                }

                if (contact.hasSkype || (contact.getSocialModel().getSkypeLink() != null && !contact.getSocialModel().getSkypeLink().isEmpty())) {
                    createdContact.hasSkype = true;
                    createdContact.getSocialModel().setSkypeLink(contact.getSocialModel().getSkypeLink());
                }
            }

        }

        userName.setSelection(userName.getText().length());


        if (createdContact.getPhotoURL() != null) {
            try {
                initialsContact.setVisibility(View.GONE);
                //holder.companyImage.setVisibility(View.GONE);
                //holder.contactImage.setVisibility(View.VISIBLE);
                contactImage.setImageURI(Uri.parse(createdContact.getPhotoURL()));
                if (((BitmapDrawable) contactImage.getDrawable()).getBitmap() == null) {
                    int nameHash = createdContact.getName().hashCode();
                    createdContact.color = Color.rgb(Math.abs(nameHash * 28439) % 255, Math.abs(nameHash * 211239) % 255, Math.abs(nameHash * 42368) % 255);
                    GradientDrawable circle = (GradientDrawable) ContextCompat.getDrawable(mainView.getContext(), R.drawable.blue_circle).mutate();
                    circle.setColor(createdContact.color);
                    contactImage.setBackground(null);
                    contactImage.setBackground(circle);
                    contactImage.setImageDrawable(null);
                    String initials = getInitials(createdContact);
                    initialsContact.setVisibility(View.VISIBLE);
                    initialsContact.setText(initials);
                }
            } catch (Exception e) {
                int nameHash = createdContact.getName().hashCode();
                createdContact.color = Color.rgb(Math.abs(nameHash * 28439) % 255, Math.abs(nameHash * 211239) % 255, Math.abs(nameHash * 42368) % 255);
                GradientDrawable circle = (GradientDrawable) ContextCompat.getDrawable(mainView.getContext(), R.drawable.blue_circle).mutate();
                circle.setColor(createdContact.color);
                contactImage.setBackground(null);
                contactImage.setBackground(circle);
                contactImage.setImageDrawable(null);
                String initials = getInitials(createdContact);
                initialsContact.setVisibility(View.VISIBLE);
                initialsContact.setText(initials);
            }

        } else {
            int nameHash;
            if (createdContact.getName() != null) {
                nameHash = createdContact.getName().hashCode();

                createdContact.color = Color.rgb(Math.abs(nameHash * 28439) % 255, Math.abs(nameHash * 211239) % 255, Math.abs(nameHash * 42368) % 255);
                GradientDrawable circle = (GradientDrawable) ContextCompat.getDrawable(mainView.getContext(), R.drawable.blue_circle).mutate();
                circle.setColor(createdContact.color);
                contactImage.setBackground(null);
                contactImage.setBackground(circle);
                contactImage.setImageDrawable(null);
                String initials = getInitials(createdContact);
                initialsContact.setVisibility(View.VISIBLE);
                initialsContact.setText(initials);
            } else {
                contactImage.setBackground(null);
                initialsContact.setVisibility(View.GONE);
            }
        }


        //  initIconColor(createdContact, mainView);

        popupName.setVisibility(View.GONE);

    }

    public boolean checkClick = true;
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
                    System.out.println("CLICK WATS ANIM NEW");

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

                        if (name == null || name.trim().isEmpty()) return false;

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

                }
            });
        }


        if (Medium != null) {
            Medium.findViewById(R.id.medium_icon).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    System.out.println("CLICK WATS ANIM");

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


        if (TwiTter != null) {
            TwiTter.findViewById(R.id.twitter_icon).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    System.out.println("CLICK WATS ANIM");

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
                        showSocialPopup(contact);
                        socialPopup.findViewById(R.id.twitter_link).callOnClick();
                        socialPopup.setVisibility(View.GONE);
                    } else {
                        String text = contact.getSocialModel().getTwitterLink();
                        if (text.contains("twitter.com/")) {
                            text = text.substring(text.indexOf(".com/") + 5);
                        }
                        if (text.length() > 0 && text.charAt(0) == '@') text = text.substring(1);
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
                    System.out.println("CLICK WATS ANIM");

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
                            i.setData(Uri.parse("https://www.linkedin.com/mwlite/search/results/all/?keywords=" + str));
                            //   else
                            //       i.setData(Uri.parse("https://www.linkedin.com/search/results/companies/v2/?keywords=" + str));

                        } else {
                            //   if (contact.listOfContacts == null || contact.listOfContacts.isEmpty())
                            i.setData(Uri.parse("https://www.linkedin.com/mwlite/search/results/all/?keywords=" + mach[0]));
                            //   else
                            //       i.setData(Uri.parse("https://www.linkedin.com/search/results/companies/v2/?keywords=" + mach[0]));
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

        if (Instagram != null) {
            Instagram.findViewById(R.id.inst_icon).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    System.out.println("CLICK WATS ANIM");

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


                    if (contact.getSocialModel() == null || contact.getSocialModel().getInstagramLink() == null || contact.getSocialModel().getInstagramLink().isEmpty()) {
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
        });*/


        /*view.findViewById(R.id.facebook_icon).setOnClickListener(new View.OnClickListener() {
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
                       *//* Intent intent2 = new Intent(Intent.ACTION_VIEW);
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
                if (!contact.hasWhatsapp || contact.getSocialModel() == null || contact.getSocialModel().getWhatsappLink() == null || contact.getSocialModel().getWhatsappLink().isEmpty()) {
                    showSocialPopup(contact);
                    socialPopup.findViewById(R.id.whatsapp_link).callOnClick();
                    socialPopup.setVisibility(View.GONE);
                } else {
                    Intent telegramIntent = new Intent(Intent.ACTION_VIEW);

                    telegramIntent.setData(Uri.parse("whatsapp://send?phone=" + contact.getSocialModel().getWhatsappLink()));
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

       /* ((ImageView) view.findViewById(R.id.telegram_icon))
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
        /*((ImageView) view.findViewById(R.id.link_icon))
                .setImageResource(contact.hasLinked ? R.drawable.icn_social_linkedin : R.drawable.icn_social_linkedin_gray);*/

        /*if (contact.hasLinked) {
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
        });*/


        /*view.findViewById(R.id.link_icon).setOnClickListener(new View.OnClickListener() {
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

        if (contact.hasViber) {
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
        /*((ImageView) view.findViewById(R.id.vk_icon))
                .setImageResource(contact.hasVk ? R.drawable.icn_social_vk : R.drawable.icn_social_vk_gray);*/

        /*if (contact.hasTwitter) {
            Drawable color = new ColorDrawable(Color.parseColor("#2ca7e0"));
            Drawable image = getResources().getDrawable(R.drawable.ic_twitter_white);
            LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
            ((ImageView) view.findViewById(R.id.vk_icon)).setImageDrawable(ld);
        } else {
            Drawable color = new ColorDrawable(Color.parseColor("#e2e5e8"));
            Drawable image = getResources().getDrawable(R.drawable.ic_twitter_white);
            LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
            ((ImageView) view.findViewById(R.id.vk_icon)).setImageDrawable(ld);
        }*/

       /* view.findViewById(R.id.vk_icon).setOnTouchListener(new View.OnTouchListener() {
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
        });*/


        /*view.findViewById(R.id.vk_icon).setOnClickListener(new View.OnClickListener() {
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

        /*if (contact.hasInst) {
            Drawable color = new ColorDrawable(Color.parseColor("#8a3ab9"));
            Drawable image = getResources().getDrawable(R.drawable.icn_social_ints2);
            LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
            ((ImageView) view.findViewById(R.id.inst_icon)).setImageDrawable(ld);
        } else {
            Drawable color = new ColorDrawable(Color.parseColor("#e2e5e8"));
            Drawable image = getResources().getDrawable(R.drawable.icn_social_ints2);
            LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
            ((ImageView) view.findViewById(R.id.inst_icon)).setImageDrawable(ld);
        }*/


        /*view.findViewById(R.id.inst_icon).setOnTouchListener(new View.OnTouchListener() {
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
        });*/

        /*view.findViewById(R.id.inst_icon).setOnClickListener(new View.OnClickListener() {
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
                        *//*Intent intent2 = new Intent(Intent.ACTION_VIEW);
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

    private String getInitials(Contact contact) {
        String initials = "";
        if (contact.getName() != null && !contact.getName().isEmpty()) {
            String names[] = contact.getName().split("\\s+");
            for (String namePart : names) {
                if (namePart.length() > 0)
                    initials += namePart.charAt(0);
            }
        }
        return initials.toUpperCase();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mergeContacts)
            ((MainActivity) mainView.getContext()).setCreateToContent();
        else {
            if (listMergedContacts.size() > 1)
                ((MainActivity) mainView.getContext()).setMergeToContent();
            else
                ((MainActivity) mainView.getContext()).setCreateToContent();
        }
        ContactsFragment.createContact = true;
    }

    public boolean checkClick_facebookEdit = true;

    public void showSocialPopup(Contact contact) {


        socialPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        ((ScrollView) socialPopup.findViewById(R.id.scroll)).scrollTo(0, 0);

        FrameLayout editFrame = (FrameLayout) getActivity().findViewById(R.id.popupEditMain);
        editFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        /*((ImageView) socialPopup.findViewById(R.id.facebook_icon)).setImageResource(R.drawable.icn_social_facebook_gray);
        ((TextView) socialPopup.findViewById(R.id.facebook_nick)).setText("add new link");
        ((ImageView) socialPopup.findViewById(R.id.vk_icon)).setImageResource(R.drawable.icn_social_vk_gray);
        ((TextView) socialPopup.findViewById(R.id.vkNick)).setText("add new link");
        ((ImageView) socialPopup.findViewById(R.id.link_icon)).setImageResource(R.drawable.icn_social_linkedin_gray);
        ((TextView) socialPopup.findViewById(R.id.link_text)).setText("add new link");
        ((ImageView) socialPopup.findViewById(R.id.instagram_icon)).setImageResource(R.drawable.icn_social_instagram_gray);
        ((TextView) socialPopup.findViewById(R.id.instagramNick)).setText("add new link");
        ((ImageView) socialPopup.findViewById(R.id.viber_icon)).setImageResource(R.drawable.icn_social_viber_gray);
        ((TextView) socialPopup.findViewById(R.id.viber_text)).setText("add new link");
        ((ImageView) socialPopup.findViewById(R.id.whatsapp_icon)).setImageResource(R.drawable.icn_social_whatsapp_gray);
        ((TextView) socialPopup.findViewById(R.id.whatsapp_text)).setText("add new link");
        ((ImageView) socialPopup.findViewById(R.id.telegram_icon)).setImageResource(R.drawable.icn_social_telegram_gray);
        ((TextView) socialPopup.findViewById(R.id.telegram_text)).setText("add new link");
        ((ImageView) socialPopup.findViewById(R.id.skype_icon)).setImageResource(R.drawable.icn_social_skype_gray);
        ((TextView) socialPopup.findViewById(R.id.skype_text)).setText("add new link");*/


        ////////====================================1
        System.out.println("CREATEEEEEEE !!! images");
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


        Drawable color32 = new ColorDrawable(Color.parseColor("#e2e5e8"));
        Drawable image32 = getResources().getDrawable(R.drawable.medium_white);
        LayerDrawable ld32 = new LayerDrawable(new Drawable[]{color32, image32});
        ((ImageView) socialPopup.findViewById(R.id.medium_icon)).setImageDrawable(ld32);

        //  ((ImageView) socialPopup.findViewById(R.id.link_icon)).setImageResource(R.drawable.icn_social_linkedin_gray);
        ((TextView) socialPopup.findViewById(R.id.link_text)).setText("add username or link");

        //  ((ImageView) socialPopup.findViewById(R.id.instagram_icon)).setImageResource(R.drawable.icn_social_instagram_gray);
        Drawable color4 = new ColorDrawable(Color.parseColor("#e2e5e8"));
        Drawable image4 = getResources().getDrawable(R.drawable.icn_social_ints2);
        LayerDrawable ld4 = new LayerDrawable(new Drawable[]{color4, image4});
        ((ImageView) socialPopup.findViewById(R.id.instagram_icon)).setImageDrawable(ld4);

        ((TextView) socialPopup.findViewById(R.id.instagramNick)).setText("add username or link");

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

        Drawable color81 = new ColorDrawable(Color.parseColor("#e2e5e8"));
        Drawable image81 = getResources().getDrawable(R.drawable.ic_youtube_white);
        LayerDrawable ld81 = new LayerDrawable(new Drawable[]{color81, image81});
        ((ImageView) socialPopup.findViewById(R.id.youtube_icon)).setImageDrawable(ld81);
        ((TextView) socialPopup.findViewById(R.id.youtubeNick)).setText("add username or link");

        Drawable color811 = new ColorDrawable(Color.parseColor("#e2e5e8"));
        Drawable image811 = getResources().getDrawable(R.drawable.ic_twitter_white);
        LayerDrawable ld811 = new LayerDrawable(new Drawable[]{color811, image811});
        ((ImageView) socialPopup.findViewById(R.id.twitter_icon)).setImageDrawable(ld811);
        ((TextView) socialPopup.findViewById(R.id.twitterNick)).setText("add username or link");

        //====================================11

        /*if (contact.getSocialModel() != null) {
            socialModel = contact.getSocialModel();*/

        if ((contact.hasTelegram && contact.getSocialModel() != null && contact.getSocialModel().getTelegramLink() != null)) {
            // ((ImageView) socialPopup.findViewById(R.id.telegram_icon)).setImageResource(R.drawable.icn_social_telegram);
            Drawable colort = new ColorDrawable(Color.parseColor("#7AA5DA"));
            Drawable imaget = getResources().getDrawable(R.drawable.icn_social_telegram2);
            LayerDrawable ldt = new LayerDrawable(new Drawable[]{colort, imaget});
            ((CircleImageView) socialPopup.findViewById(R.id.telegram_icon)).setImageDrawable(ldt);

        }

        if (contact.getSocialModel() != null) {
            socialModel = contact.getSocialModel();

            ///=\\\\\\\\\\\\\\\\\\\\\\2

            /*if (socialModel.getFacebookLink() != null) {
                ((ImageView) socialPopup.findViewById(R.id.facebook_icon)).setImageResource(R.drawable.icn_social_facebook);
                ((TextView) socialPopup.findViewById(R.id.facebook_nick)).setText(contact.getSocialModel().getFacebookLink());
            }
            if (socialModel.getVkLink() != null) {
                ((ImageView) socialPopup.findViewById(R.id.vk_icon)).setImageResource(R.drawable.icn_social_vk);
                ((TextView) socialPopup.findViewById(R.id.vkNick)).setText(contact.getSocialModel().getVkLink());
            }
            if (socialModel.getLinkedInLink() != null) {
                ((ImageView) socialPopup.findViewById(R.id.link_icon)).setImageResource(R.drawable.icn_social_linkedin);
                ((TextView) socialPopup.findViewById(R.id.link_text)).setText(contact.getSocialModel().getLinkedInLink());
            }
            if (socialModel.getInstagramLink() != null) {
                ((ImageView) socialPopup.findViewById(R.id.instagram_icon)).setImageResource(R.drawable.icn_social_instagram);
                ((TextView) socialPopup.findViewById(R.id.instagramNick)).setText(contact.getSocialModel().getInstagramLink());
            }
            if (socialModel.getViberLink() != null) {
                ((ImageView) socialPopup.findViewById(R.id.viber_icon)).setImageResource(R.drawable.icn_social_viber);
                ((TextView) socialPopup.findViewById(R.id.viber_text)).setText(contact.getSocialModel().getViberLink());
            }
            if (socialModel.getWhatsappLink() != null) {
                ((ImageView) socialPopup.findViewById(R.id.whatsapp_icon)).setImageResource(R.drawable.icn_social_whatsapp);
                ((TextView) socialPopup.findViewById(R.id.whatsapp_text)).setText(contact.getSocialModel().getWhatsappLink());
            }
            if (socialModel.getTelegramLink() != null) {
                ((ImageView) socialPopup.findViewById(R.id.telegram_icon)).setImageResource(R.drawable.icn_social_telegram);
                ((TextView) socialPopup.findViewById(R.id.telegram_text)).setText(contact.getSocialModel().getTelegramLink());
            }
            if (socialModel.getSkypeLink() != null) {
                ((ImageView) socialPopup.findViewById(R.id.skype_icon)).setImageResource(R.drawable.icn_social_skype);
                ((TextView) socialPopup.findViewById(R.id.skype_text)).setText(contact.getSocialModel().getSkypeLink());
            }*/


            if (socialModel.getFacebookLink() != null) {
                //  ((ImageView) socialPopup.findViewById(R.id.facebook_icon)).setImageResource(R.drawable.icn_social_facebook);
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
                    } else if (link.contains("php?")) {
                        int index = link.indexOf("php?");
                        link = link.substring(index + 4, link.length());
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
                    e.printStackTrace();
                    ((TextView) socialPopup.findViewById(R.id.vkNick)).setText(contact.getSocialModel().getVkLink());
                }
            }
            if (socialModel.getLinkedInLink() != null) {
                //  ((ImageView) socialPopup.findViewById(R.id.link_icon)).setImageResource(R.drawable.icn_social_linkedin);
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
                // ((ImageView) socialPopup.findViewById(R.id.instagram_icon)).setImageResource(R.drawable.icn_social_instagram);
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
                System.out.println("MEDIUM not null");
                //   ((ImageView) socialPopup.findViewById(R.id.link_icon)).setImageResource(R.drawable.icn_social_linkedin);
                Drawable colori = new ColorDrawable(Color.parseColor("#000000"));
                Drawable imagei = getResources().getDrawable(R.drawable.medium_white);
                LayerDrawable ldi = new LayerDrawable(new Drawable[]{colori, imagei});
                ((ImageView) socialPopup.findViewById(R.id.medium_icon)).setImageDrawable(ldi);

                String inst = socialModel.getMediumLink();


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
                //  ((ImageView) socialPopup.findViewById(R.id.viber_icon)).setImageResource(R.drawable.icn_social_viber);
                Drawable colorvi = new ColorDrawable(Color.parseColor("#6F3FAA"));
                Drawable imagevi = getResources().getDrawable(R.drawable.icn_social_viber2);
                LayerDrawable ldvi = new LayerDrawable(new Drawable[]{colorvi, imagevi});
                ((CircleImageView) socialPopup.findViewById(R.id.viber_icon)).setImageDrawable(ldvi);

                ((TextView) socialPopup.findViewById(R.id.viber_text)).setText(contact.getSocialModel().getViberLink());
            }
            if (socialModel.getWhatsappLink() != null) {
                // ((ImageView) socialPopup.findViewById(R.id.whatsapp_icon)).setImageResource(R.drawable.icn_social_whatsapp);
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
                //    ((ImageView) socialPopup.findViewById(R.id.skype_icon)).setImageResource(R.drawable.icn_social_skype);
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


                        Realm realm = Realm.getDefaultInstance(); //+
                        realm.beginTransaction();

                        if (socialModel == null)
                            socialModel = new SocialModel();
                        /*}else
                            socialModel = contact.getSocialModel();*/

                        if (((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().length() > 0) {
                            //   if (contact.getSocialModel() != null)

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
                            //   ((ImageView) socialPopup.findViewById(R.id.facebook_icon)).setImageResource(R.drawable.icn_social_facebook);
                            //   ((ImageView) getActivity().findViewById(R.id.facebook_icon)).setImageResource(R.drawable.icn_social_facebook);
                            Drawable colorf = new ColorDrawable(Color.parseColor("#2ca7e0"));
                            Drawable imagef = getResources().getDrawable(R.drawable.ic_twitter_white);
                            LayerDrawable ldf = new LayerDrawable(new Drawable[]{colorf, imagef});
                            ((ImageView) socialPopup.findViewById(R.id.twitter_icon)).setImageDrawable(ldf);
                            ((ImageView) getActivity().findViewById(R.id.vk_icon)).setImageDrawable(ldf);

                            /*try{
                                ((ImageView) profilePopUp.findViewById(R.id.facebook_icon)).setImageDrawable(ldf);
                            }catch (Exception e){

                            }
                            try{
                                ((ImageView) companyProfilePopup.findViewById(R.id.facebook_icon)).setImageDrawable(ldf);
                            }catch (Exception e){

                            }*/
                            //    if (contact.getSocialModel() != null)

                            /*String link = socialModel.getFacebookLink();
                            boolean checkich = false;
                            try {
                                String checkFacebook = link.substring(0, 37);
                                if (checkFacebook.equals("https://www.facebook.com/profile.php?")) {
                                    link = link.substring(37, link.length());
                                    ((TextView) socialPopup.findViewById(R.id.facebook_nick)).setText(link);
                                    checkich = true;
                                }
                            }catch (Exception e){

                                e.printStackTrace();
                            }
                            if(!checkich) {
                                try {
                                    String checkFacebook = link.substring(0, 25);
                                    if (checkFacebook.equals("https://www.facebook.com/")) {
                                        link = link.substring(25, link.length());
                                        ((TextView) socialPopup.findViewById(R.id.facebook_nick)).setText(link);
                                        checkich = true;
                                    }else if(link.contains("php?")){
                                        int index = link.indexOf("php?");
                                        link = link.substring(index+4,link.length());
                                        ((TextView) socialPopup.findViewById(R.id.facebook_nick)).setText(link);
                                        checkich = true;
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            //   System.out.println(checkFacebook+", "+link);
                            if(!checkich)*/
                            if (socialModel.getTwitterLink().contains(".com/")) {
                                ((TextView) socialPopup.findViewById(R.id.twitterNick)).setText(socialModel.getTwitterLink().substring(socialModel.getTwitterLink().indexOf(".com/") + 5));
                            } else
                                ((TextView) socialPopup.findViewById(R.id.twitterNick)).setText(socialModel.getTwitterLink());


                            //((TextView) socialPopup.findViewById(R.id.facebook_nick)).setText(socialModel.getFacebookLink());
                            contact.hasTwitter = true;
                            contact.setSocialModel(socialModel);
                            realm.commitTransaction();
                            //    ContactCacheService.updateContact(contact, mainView.getContext());
                            //    EventBus.getDefault().post(new UpdateFile());
                            editFrame.setVisibility(View.GONE);
                            //    ((ImageView) profilePopUp.findViewById(R.id.facebook_icon)).setImageResource(R.drawable.icn_social_facebook);
                        } else {
                            socialModel.setTwitterLink(null);
                            //    ((ImageView) socialPopup.findViewById(R.id.facebook_icon)).setImageResource(R.drawable.icn_social_facebook_gray);
                            //        ((ImageView) getActivity().findViewById(R.id.facebook_icon)).setImageResource(R.drawable.icn_social_facebook_gray);
                            Drawable color = new ColorDrawable(Color.parseColor("#e2e5e8"));
                            Drawable image = getResources().getDrawable(R.drawable.ic_twitter_white);
                            LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
                            ((ImageView) socialPopup.findViewById(R.id.twitter_icon)).setImageDrawable(ld);
                            ((ImageView) getActivity().findViewById(R.id.vk_icon)).setImageDrawable(ld);

                            ((TextView) socialPopup.findViewById(R.id.twitterNick)).setText("add username or link");
                            contact.hasTwitter = false;
                            contact.setSocialModel(socialModel);
                            realm.commitTransaction();
                            //  ContactCacheService.updateContact(contact, mainView.getContext());
                            //  EventBus.getDefault().post(new UpdateFile());
                            editFrame.setVisibility(View.GONE);
                        }
                        realm.close();
                        initIconColor(createdContact, mainView);
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
                    ((ImageView) editFrame.findViewById(R.id.iconDataEdit)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_youtube_48));

                    //if(contact.listOfContacts == null || contact.listOfContacts.isEmpty()) {
                    editFrame.findViewById(R.id.ok_social).setVisibility(View.GONE);
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

                        Realm realm = Realm.getDefaultInstance(); //+
                        realm.beginTransaction();

                        if (socialModel == null)
                            socialModel = new SocialModel();

                        if (((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().length() > 0) {
                            //   if (contact.getSocialModel() != null)

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
                            //   ((ImageView) socialPopup.findViewById(R.id.facebook_icon)).setImageResource(R.drawable.icn_social_facebook);
                            //   ((ImageView) getActivity().findViewById(R.id.facebook_icon)).setImageResource(R.drawable.icn_social_facebook);
                            Drawable colorf = new ColorDrawable(Color.parseColor("#ed2524"));
                            Drawable imagef = getResources().getDrawable(R.drawable.ic_youtube_white);
                            LayerDrawable ldf = new LayerDrawable(new Drawable[]{colorf, imagef});
                            ((ImageView) socialPopup.findViewById(R.id.youtube_icon)).setImageDrawable(ldf);


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


                            //((TextView) socialPopup.findViewById(R.id.facebook_nick)).setText(socialModel.getFacebookLink());
                            contact.hasYoutube = true;
                            contact.setSocialModel(socialModel);
                            realm.commitTransaction();

                            editFrame.setVisibility(View.GONE);
                            //    ((ImageView) profilePopUp.findViewById(R.id.facebook_icon)).setImageResource(R.drawable.icn_social_facebook);
                        } else {
                            socialModel.setYoutubeLink(null);

                            Drawable color = new ColorDrawable(Color.parseColor("#e2e5e8"));
                            Drawable image = getResources().getDrawable(R.drawable.ic_youtube_white);
                            LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
                            ((ImageView) socialPopup.findViewById(R.id.youtube_icon)).setImageDrawable(ld);
                            //((ImageView) getActivity().findViewById(R.id.facebook_icon)).setImageDrawable(ld);

                            ((TextView) socialPopup.findViewById(R.id.youtubeNick)).setText("add username or link");
                            contact.hasYoutube = false;
                            contact.setSocialModel(socialModel);
                            realm.commitTransaction();
                            editFrame.setVisibility(View.GONE);
                        }
                        realm.close();
                        initIconColor(createdContact, mainView);

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

                    ((ImageView) editFrame.findViewById(R.id.iconDataEdit)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.medium_size_64));
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

                        Realm realm = Realm.getDefaultInstance(); //+
                        realm.beginTransaction();

                        if (socialModel == null)
                            socialModel = new SocialModel();
                        /*}else
                            socialModel = contact.getSocialModel();*/

                        if (((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().length() > 0) {
                            //   if (contact.getSocialModel() != null)


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


                            Drawable colorf = new ColorDrawable(Color.parseColor("#000000"));
                            Drawable imagef = getResources().getDrawable(R.drawable.medium_white);
                            LayerDrawable ldf = new LayerDrawable(new Drawable[]{colorf, imagef});
                            ((ImageView) socialPopup.findViewById(R.id.medium_icon)).setImageDrawable(ldf);
                            //((ImageView) getActivity().findViewById(R.id.facebook_icon)).setImageDrawable(ldf);

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


                            //((TextView) socialPopup.findViewById(R.id.facebook_nick)).setText(socialModel.getFacebookLink());
                            contact.hasMedium = true;
                            contact.setSocialModel(socialModel);
                            realm.commitTransaction();

                            editFrame.setVisibility(View.GONE);
                            //    ((ImageView) profilePopUp.findViewById(R.id.facebook_icon)).setImageResource(R.drawable.icn_social_facebook);
                        } else {
                            socialModel.setMediumLink(null);

                            Drawable color = new ColorDrawable(Color.parseColor("#e2e5e8"));
                            Drawable image = getResources().getDrawable(R.drawable.medium_white);
                            LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
                            ((ImageView) socialPopup.findViewById(R.id.medium_icon)).setImageDrawable(ld);
                            //((ImageView) getActivity().findViewById(R.id.facebook_icon)).setImageDrawable(ld);

                            ((TextView) socialPopup.findViewById(R.id.mediumNick)).setText("add username or link");
                            contact.hasMedium = false;
                            contact.setSocialModel(socialModel);
                            realm.commitTransaction();
                            editFrame.setVisibility(View.GONE);
                        }
                        realm.close();
                        initIconColor(createdContact, mainView);

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

                        ((ImageView) editFrame.findViewById(R.id.iconDataEdit)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_social_facebook));

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
                        if (name == null || name.trim().isEmpty()) return;
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

                        if (name == null || name.trim().isEmpty()) return;
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

                //editFrame.setVisibility(View.VISIBLE);
                openedViews.add(editFrame);
                ((TextView) editFrame.findViewById(R.id.typeField)).setText("facebook");
                ((EditText) editFrame.findViewById(R.id.dataToEdit)).setHint("Enter link");
                //  ((EditText) editFrame.findViewById(R.id.dataToEdit)).setText("");

                editFrame.findViewById(R.id.updateContactField).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Realm realm = Realm.getDefaultInstance(); //+
                        realm.beginTransaction();

                        if (socialModel == null)
                            socialModel = new SocialModel();
                        /*}else
                            socialModel = contact.getSocialModel();*/

                        if (((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().length() > 0) {
                            //   if (contact.getSocialModel() != null)

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
                            //   ((ImageView) socialPopup.findViewById(R.id.facebook_icon)).setImageResource(R.drawable.icn_social_facebook);
                            //   ((ImageView) getActivity().findViewById(R.id.facebook_icon)).setImageResource(R.drawable.icn_social_facebook);
                            Drawable colorf = new ColorDrawable(Color.parseColor("#475993"));
                            Drawable imagef = getResources().getDrawable(R.drawable.icn_social_facebook2);
                            LayerDrawable ldf = new LayerDrawable(new Drawable[]{colorf, imagef});
                            ((ImageView) socialPopup.findViewById(R.id.facebook_icon)).setImageDrawable(ldf);
                            ((ImageView) getActivity().findViewById(R.id.facebook_icon)).setImageDrawable(ldf);

                            /*try{
                                ((ImageView) profilePopUp.findViewById(R.id.facebook_icon)).setImageDrawable(ldf);
                            }catch (Exception e){

                            }
                            try{
                                ((ImageView) companyProfilePopup.findViewById(R.id.facebook_icon)).setImageDrawable(ldf);
                            }catch (Exception e){

                            }*/
                            //    if (contact.getSocialModel() != null)

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


                            //((TextView) socialPopup.findViewById(R.id.facebook_nick)).setText(socialModel.getFacebookLink());
                            contact.hasFacebook = true;
                            contact.setSocialModel(socialModel);
                            realm.commitTransaction();
                            //    ContactCacheService.updateContact(contact, mainView.getContext());
                            //    EventBus.getDefault().post(new UpdateFile());
                            editFrame.setVisibility(View.GONE);
                            //    ((ImageView) profilePopUp.findViewById(R.id.facebook_icon)).setImageResource(R.drawable.icn_social_facebook);
                        } else {
                            socialModel.setFacebookLink(null);
                            //    ((ImageView) socialPopup.findViewById(R.id.facebook_icon)).setImageResource(R.drawable.icn_social_facebook_gray);
                            //        ((ImageView) getActivity().findViewById(R.id.facebook_icon)).setImageResource(R.drawable.icn_social_facebook_gray);
                            Drawable color = new ColorDrawable(Color.parseColor("#e2e5e8"));
                            Drawable image = getResources().getDrawable(R.drawable.icn_social_facebook2);
                            LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
                            ((ImageView) socialPopup.findViewById(R.id.facebook_icon)).setImageDrawable(ld);
                            ((ImageView) getActivity().findViewById(R.id.facebook_icon)).setImageDrawable(ld);

                            ((TextView) socialPopup.findViewById(R.id.facebook_nick)).setText("add username or link");
                            contact.hasFacebook = false;
                            contact.setSocialModel(socialModel);
                            realm.commitTransaction();
                            //  ContactCacheService.updateContact(contact, mainView.getContext());
                            //  EventBus.getDefault().post(new UpdateFile());
                            editFrame.setVisibility(View.GONE);
                        }
                        realm.close();
                        initIconColor(createdContact, mainView);
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
                //editFrame.setVisibility(View.VISIBLE);

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

                        ((ImageView) editFrame.findViewById(R.id.iconDataEdit)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_social_vk));

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
                ((EditText) editFrame.findViewById(R.id.dataToEdit)).setHint("Enter link");
                //   ((EditText) editFrame.findViewById(R.id.dataToEdit)).setText("");

                editFrame.findViewById(R.id.updateContactField).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Realm realm = Realm.getDefaultInstance(); //+
                        realm.beginTransaction();
                        //SocialModel socialModel;
                        if (socialModel == null)
                            socialModel = new SocialModel();
                       /* }else
                            socialModel = contact.getSocialModel();*/

                        if (((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().length() > 0) {
                            //        if (contact.getSocialModel() != null)

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
                            //  ((ImageView) socialPopup.findViewById(R.id.vk_icon)).setImageResource(R.drawable.icn_social_vk);
                            //  ((ImageView) getActivity().findViewById(R.id.vk_icon)).setImageResource(R.drawable.icn_social_vk);
                            Drawable colorv = new ColorDrawable(Color.parseColor("#507299"));
                            Drawable imagev = getResources().getDrawable(R.drawable.icn_social_vk2);
                            LayerDrawable ldv = new LayerDrawable(new Drawable[]{colorv, imagev});
                            ((ImageView) socialPopup.findViewById(R.id.vk_icon)).setImageDrawable(ldv);
                            ((ImageView) getActivity().findViewById(R.id.vk_icon)).setImageDrawable(ldv);


                            /*try{
                                ((ImageView) profilePopUp.findViewById(R.id.vk_icon)).setImageDrawable(ldv);
                            }catch (Exception e){

                            }

                            try{
                                ((ImageView) companyProfilePopup.findViewById(R.id.vk_icon)).setImageDrawable(ldv);
                            }catch (Exception e){

                            }*/

                            //   if (contact.getSocialModel() != null)
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
                            //       System.out.println("SOCIAL VK LINK = "+contact.getSocialModel().getVkLink()+", bool = "+contact.hasVk);
                            //     ContactCacheService.updateContact(contact, mainView.getContext());
                            //     EventBus.getDefault().post(new UpdateFile());
                            editFrame.setVisibility(View.GONE);
                            // ((ImageView) profilePopUp.findViewById(R.id.vk_icon)).setImageResource(R.drawable.icn_social_vk);
                        } else {
                            socialModel.setVkLink(null);
                            //  ((ImageView) socialPopup.findViewById(R.id.vk_icon)).setImageResource(R.drawable.icn_social_vk_gray);
                            //  ((ImageView) getActivity().findViewById(R.id.vk_icon)).setImageResource(R.drawable.icn_social_vk_gray);
                            Drawable color2 = new ColorDrawable(Color.parseColor("#e2e5e8"));
                            Drawable image2 = getResources().getDrawable(R.drawable.icn_social_vk2);
                            LayerDrawable ld2 = new LayerDrawable(new Drawable[]{color2, image2});
                            ((ImageView) socialPopup.findViewById(R.id.vk_icon)).setImageDrawable(ld2);
                            ((ImageView) getActivity().findViewById(R.id.vk_icon)).setImageDrawable(ld2);

                            ((TextView) socialPopup.findViewById(R.id.vkNick)).setText("add username or link");
                            contact.hasVk = false;
                            contact.setSocialModel(socialModel);
                            realm.commitTransaction();
                            //   ContactCacheService.updateContact(contact, mainView.getContext());
                            //    EventBus.getDefault().post(new UpdateFile());
                            editFrame.setVisibility(View.GONE);
                        }
                        realm.close();
                        initIconColor(createdContact, mainView);

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

                //editFrame.setVisibility(View.VISIBLE);

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

                        ((ImageView) editFrame.findViewById(R.id.iconDataEdit)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_social_linkedin));

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
                            i.setData(Uri.parse("https://www.linkedin.com/mwlite/search/results/all/?keywords=" + str));
                            //     else
                            //         i.setData(Uri.parse("https://www.linkedin.com/search/results/companies/v2/?keywords=" + str));
                        } else {
                            //     if (contact.listOfContacts == null || contact.listOfContacts.isEmpty())
                            i.setData(Uri.parse("https://www.linkedin.com/mwlite/search/results/all/?keywords=" + mach[0]));
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
                ((EditText) editFrame.findViewById(R.id.dataToEdit)).setHint("Enter link");
                //    ((EditText) editFrame.findViewById(R.id.dataToEdit)).setText("");

                editFrame.findViewById(R.id.updateContactField).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Realm realm = Realm.getDefaultInstance(); //+
                        realm.beginTransaction();

                        if (contact.getSocialModel() == null)
                            socialModel = new SocialModel();


                        if (((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().length() > 0) {
                            //       if (contact.getSocialModel() != null)

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
                            //        ((ImageView) socialPopup.findViewById(R.id.link_icon)).setImageResource(R.drawable.icn_social_linkedin);
                            Drawable colorl = new ColorDrawable(Color.parseColor("#0077B7"));
                            Drawable imagel = getResources().getDrawable(R.drawable.icn_social_linked2);
                            LayerDrawable ldl = new LayerDrawable(new Drawable[]{colorl, imagel});
                            ((ImageView) socialPopup.findViewById(R.id.link_icon)).setImageDrawable(ldl);
                            ((ImageView) getActivity().findViewById(R.id.link_icon)).setImageDrawable(ldl);
                            //   ((ImageView) getActivity().findViewById(R.id.link_icon)).setImageDrawable(ldl);

                           /* try {
                                ((ImageView) profilePopUp.findViewById(R.id.link_icon)).setImageDrawable(ldl);
                            }catch (Exception e){

                            }
                            try{
                                ((ImageView) companyProfilePopup.findViewById(R.id.link_icon)).setImageDrawable(ldl);
                            }catch (Exception e){

                            }*/
                            //   if (contact.getSocialModel() != null)
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
                            //      ContactCacheService.updateContact(contact, mainView.getContext());
                            //      EventBus.getDefault().post(new UpdateFile());
                            editFrame.setVisibility(View.GONE);
                        } else {
                            socialModel.setLinkedInLink(null);
                            //    ((ImageView) socialPopup.findViewById(R.id.link_icon)).setImageResource(R.drawable.icn_social_linkedin_gray);
                            //    ((ImageView) getActivity().findViewById(R.id.link_icon)).setImageResource(R.drawable.icn_social_linkedin_gray);
                            Drawable color3 = new ColorDrawable(Color.parseColor("#e2e5e8"));
                            Drawable image3 = getResources().getDrawable(R.drawable.icn_social_linked2);
                            LayerDrawable ld3 = new LayerDrawable(new Drawable[]{color3, image3});
                            ((ImageView) socialPopup.findViewById(R.id.link_icon)).setImageDrawable(ld3);
                            ((ImageView) getActivity().findViewById(R.id.link_icon)).setImageDrawable(ld3);

                            ((TextView) socialPopup.findViewById(R.id.link_text)).setText("add username or link");
                            contact.hasLinked = false;
                            contact.setSocialModel(socialModel);
                            realm.commitTransaction();
                            //      ContactCacheService.updateContact(contact, mainView.getContext());
                            //      EventBus.getDefault().post(new UpdateFile());
                            editFrame.setVisibility(View.GONE);
                        }
                        realm.close();
                        initIconColor(createdContact, mainView);
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

                        ((ImageView) editFrame.findViewById(R.id.iconDataEdit)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_social_instagram));

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
                ((EditText) editFrame.findViewById(R.id.dataToEdit)).setHint("Enter username");
                //      ((EditText) editFrame.findViewById(R.id.dataToEdit)).setText("");

                editFrame.findViewById(R.id.updateContactField).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Realm realm = Realm.getDefaultInstance(); //+
                        realm.beginTransaction();

                        if (contact.getSocialModel() == null)
                            socialModel = new SocialModel();

                        if (((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().length() > 0) {
                            //      if (contact.getSocialModel() != null)

                            String username = ((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().toString();

                            if (username.contains("?utm")) {
                                int ind = username.indexOf('?');
                                if (ind != -1)
                                    username = username.substring(0, ind);

                            }

                            if (username.charAt(username.length() - 1) == '/') {
                                username = username.substring(0, username.length() - 1);
                                //contactsService.updateNote(contact.getIdContact(), note, username);
                            }

                            if (!username.toLowerCase().contains("instagram.com")) {
                                username = "https://instagram.com/" + username;
                            }

                            socialModel.setInstagramLink(username);
                            //    ((ImageView) socialPopup.findViewById(R.id.instagram_icon)).setImageResource(R.drawable.icn_social_instagram);
                            Drawable colori = new ColorDrawable(Color.parseColor("#8a3ab9"));
                            Drawable imagei = getResources().getDrawable(R.drawable.icn_social_ints2);
                            LayerDrawable ldi = new LayerDrawable(new Drawable[]{colori, imagei});
                            ((ImageView) socialPopup.findViewById(R.id.instagram_icon)).setImageDrawable(ldi);
                            ((ImageView) getActivity().findViewById(R.id.instagram_icon)).setImageDrawable(ldi);
                            //      ((ImageView) getActivity().findViewById(R.id.inst_icon)).setImageDrawable(ldi);

                            try {
                                ((ImageView) mainView.findViewById(R.id.inst_icon)).setImageDrawable(ldi);
                            } catch (Exception e) {

                            }
                            try {
                                //      ((ImageView) companyProfilePopup.findViewById(R.id.inst_icon)).setImageDrawable(ldi);
                            } catch (Exception e) {

                            }
                            //    if (contact.getSocialModel() != null)


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

                            ////     ContactCacheService.updateContact(contact, mainView.getContext());
                            //     EventBus.getDefault().post(new UpdateFile());
                            editFrame.setVisibility(View.GONE);
                        } else {
                            socialModel.setInstagramLink(null);
                            // ((ImageView) socialPopup.findViewById(R.id.instagram_icon)).setImageResource(R.drawable.icn_social_instagram_gray);
                            // ((ImageView) getActivity().findViewById(R.id.instagram_icon)).setImageResource(R.drawable.icn_social_instagram_gray);
                            Drawable color4 = new ColorDrawable(Color.parseColor("#e2e5e8"));
                            Drawable image4 = getResources().getDrawable(R.drawable.icn_social_ints2);
                            LayerDrawable ld4 = new LayerDrawable(new Drawable[]{color4, image4});
                            ((ImageView) socialPopup.findViewById(R.id.instagram_icon)).setImageDrawable(ld4);
                            ((ImageView) getActivity().findViewById(R.id.inst_icon)).setImageDrawable(ld4);

                            ((TextView) socialPopup.findViewById(R.id.instagramNick)).setText("add username or link");
                            contact.hasInst = false;
                            contact.setSocialModel(socialModel);
                            realm.commitTransaction();
                            //    ContactCacheService.updateContact(contact, mainView.getContext());
                            //    EventBus.getDefault().post(new UpdateFile());
                            editFrame.setVisibility(View.GONE);
                        }
                        realm.close();
                        initIconColor(createdContact, mainView);
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
                //editFrame.setVisibility(View.VISIBLE);

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
                    editFrame.findViewById(R.id.ok_social).setVisibility(View.GONE);
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
                //      ((EditText) editFrame.findViewById(R.id.dataToEdit)).setText("");

                editFrame.findViewById(R.id.updateContactField).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Realm realm = Realm.getDefaultInstance(); //+
                        realm.beginTransaction();

                        if (contact.getSocialModel() == null)
                            socialModel = new SocialModel();


                        if (((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().length() > 0) {
                            //   if (contact.getSocialModel() != null)
                            socialModel.setViberLink(((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().toString());
                            ((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().clear();
                            //     ((ImageView) socialPopup.findViewById(R.id.viber_icon)).setImageResource(R.drawable.icn_social_viber);
                            Drawable colorvi = new ColorDrawable(Color.parseColor("#6F3FAA"));
                            Drawable imagevi = getResources().getDrawable(R.drawable.icn_social_viber2);
                            LayerDrawable ldvi = new LayerDrawable(new Drawable[]{colorvi, imagevi});
                            ((CircleImageView) socialPopup.findViewById(R.id.viber_icon)).setImageDrawable(ldvi);
                            ((CircleImageView) getActivity().findViewById(R.id.viber_icon)).setImageDrawable(ldvi);
                            //     ((CircleImageView) getActivity().findViewById(R.id.viber_icon)).setImageDrawable(ldvi);

                          /*  try {
                                ((CircleImageView) profilePopUp.findViewById(R.id.viber_icon)).setImageDrawable(ldvi);
                            }catch (Exception e){

                            }
                            try{
                                ((CircleImageView) companyProfilePopup.findViewById(R.id.viber_icon)).setImageDrawable(ldvi);
                            }catch (Exception e){

                            }*/
                            //      if (contact.getSocialModel() != null)
                            ((TextView) socialPopup.findViewById(R.id.viber_text)).setText(socialModel.getViberLink());
                            contact.hasViber = true;
                            contact.setSocialModel(socialModel);
                            realm.commitTransaction();
                            //      ContactCacheService.updateContact(contact, mainView.getContext());
                            //      EventBus.getDefault().post(new UpdateFile());
                            editFrame.setVisibility(View.GONE);
                        } else {
                            socialModel.setViberLink(null);
                            ((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().clear();
                            //    ((ImageView) socialPopup.findViewById(R.id.viber_icon)).setImageResource(R.drawable.icn_social_viber_gray);
                            //    ((ImageView) getActivity().findViewById(R.id.viber_icon)).setImageResource(R.drawable.icn_social_viber_gray);
                            Drawable color5 = new ColorDrawable(Color.parseColor("#e2e5e8"));
                            Drawable image5 = getResources().getDrawable(R.drawable.icn_social_viber2_gray);
                            LayerDrawable ld5 = new LayerDrawable(new Drawable[]{color5, image5});
                            ((CircleImageView) socialPopup.findViewById(R.id.viber_icon)).setImageDrawable(ld5);
                            ((CircleImageView) getActivity().findViewById(R.id.viber_icon)).setImageDrawable(ld5);

                            ((TextView) socialPopup.findViewById(R.id.viber_text)).setText("add new link");
                            contact.hasViber = false;
                            contact.setSocialModel(socialModel);
                            realm.commitTransaction();
                            //     ContactCacheService.updateContact(contact, mainView.getContext());
                            //      EventBus.getDefault().post(new UpdateFile());
                            editFrame.setVisibility(View.GONE);
                        }
                        realm.close();
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
                    editFrame.findViewById(R.id.frameImageEditSocialInsta).setVisibility(View.GONE);
                    editFrame.findViewById(R.id.imageEditSocial).setVisibility(View.GONE);
                    editFrame.findViewById(R.id.get_last_clips).setVisibility(View.VISIBLE);
                    editFrame.findViewById(R.id.searchSocial).setVisibility(View.GONE);
                    editFrame.findViewById(R.id.ok_social).setVisibility(View.GONE);
                }

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

                openedViews.add(editFrame);
                ((TextView) editFrame.findViewById(R.id.typeField)).setText("Whatsapp");
                ((EditText) editFrame.findViewById(R.id.dataToEdit)).setHint("Enter mobile number");
                //      ((EditText) editFrame.findViewById(R.id.dataToEdit)).setText("");

                editFrame.findViewById(R.id.updateContactField).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        Realm realm = Realm.getDefaultInstance(); //+
                        realm.beginTransaction();

                        if (contact.getSocialModel() == null)
                            socialModel = new SocialModel();


                        if (((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().length() > 0) {
                            //       if (contact.getSocialModel() != null)
                            socialModel.setWhatsappLink(((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().toString());
                            ((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().clear();
                            //    ((ImageView) socialPopup.findViewById(R.id.whatsapp_icon)).setImageResource(R.drawable.icn_social_whatsapp);
                            Drawable colorw = new ColorDrawable(Color.parseColor("#75B73B"));
                            Drawable imagew = getResources().getDrawable(R.drawable.icn_social_whatsapp3);
                            LayerDrawable ldw = new LayerDrawable(new Drawable[]{colorw, imagew});
                            ((CircleImageView) socialPopup.findViewById(R.id.whatsapp_icon)).setImageDrawable(ldw);
                            ((CircleImageView) getActivity().findViewById(R.id.whatsapp_icon)).setImageDrawable(ldw);
                            //    ((CircleImageView) getActivity().findViewById(R.id.whatsapp_icon)).setImageDrawable(ldw);

                           /* try {
                                ((CircleImageView) profilePopUp.findViewById(R.id.whatsapp_icon)).setImageDrawable(ldw);
                            }catch (Exception e){

                            }
                            try{
                                ((CircleImageView) companyProfilePopup.findViewById(R.id.whatsapp_icon)).setImageDrawable(ldw);
                            }catch (Exception e){

                            }*/
                            //   if (contact.getSocialModel() != null)
                            ((TextView) socialPopup.findViewById(R.id.whatsapp_text)).setText(socialModel.getWhatsappLink());
                            contact.hasWhatsapp = true;
                            contact.setSocialModel(socialModel);
                            realm.commitTransaction();
                            //       ContactCacheService.updateContact(contact, mainView.getContext());
                            //       EventBus.getDefault().post(new UpdateFile());
                            editFrame.setVisibility(View.GONE);
                        } else {
                            socialModel.setWhatsappLink(null);
                            ((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().clear();
                            //  ((ImageView) socialPopup.findViewById(R.id.whatsapp_icon)).setImageResource(R.drawable.icn_social_whatsapp_gray);
                            //  ((ImageView) getActivity().findViewById(R.id.whatsapp_icon)).setImageResource(R.drawable.icn_social_whatsapp_gray);
                            Drawable color6 = new ColorDrawable(Color.parseColor("#e2e5e8"));
                            Drawable image6 = getResources().getDrawable(R.drawable.icn_social_whatsapp3_gray);
                            LayerDrawable ld6 = new LayerDrawable(new Drawable[]{color6, image6});
                            ((CircleImageView) socialPopup.findViewById(R.id.whatsapp_icon)).setImageDrawable(ld6);
                            ((CircleImageView) getActivity().findViewById(R.id.whatsapp_icon)).setImageDrawable(ld6);

                            ((TextView) socialPopup.findViewById(R.id.whatsapp_text)).setText("add new link");
                            contact.hasWhatsapp = false;
                            contact.setSocialModel(socialModel);
                            realm.commitTransaction();
                            //    ContactCacheService.updateContact(contact, mainView.getContext());
                            //    EventBus.getDefault().post(new UpdateFile());
                            editFrame.setVisibility(View.GONE);
                        }
                        realm.close();
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
                //  ((EditText) editFrame.findViewById(R.id.dataToEdit)).setText(contact.getSocialModel().getTelegramLink());

                if (contact.getSocialModel() != null && contact.getSocialModel().getTelegramLink() != null)
                    showEditPopupPreviewSocial(contact, SocialEnums.TELEGRAM);
                else {
                    editFrame.setVisibility(View.VISIBLE);


                    ((EditText) editFrame.findViewById(R.id.dataToEdit)).setPadding(0, 0, 0, 0);
                    ((ImageView) editFrame.findViewById(R.id.iconDataEdit)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icn_social_telegram));

                    editFrame.findViewById(R.id.frameImageEditSocialInsta).setVisibility(View.GONE);
                    editFrame.findViewById(R.id.imageEditSocial).setVisibility(View.GONE);
                    editFrame.findViewById(R.id.get_last_clips).setVisibility(View.VISIBLE);
                    editFrame.findViewById(R.id.searchSocial).setVisibility(View.GONE);
                    editFrame.findViewById(R.id.ok_social).setVisibility(View.GONE);
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
                //       ((EditText) editFrame.findViewById(R.id.dataToEdit)).setText("");

                editFrame.findViewById(R.id.updateContactField).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        Realm realm = Realm.getDefaultInstance(); //+
                        realm.beginTransaction();

                        if (contact.getSocialModel() == null)
                            socialModel = new SocialModel();


                        if (((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().length() > 0) {
                            //    if (contact.getSocialModel() != null)

                            String username = ((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().toString();
                            char firstSymbol = username.charAt(0);
                            String regex = "[0-9]+";
                            username = username.replaceAll("[-() ]", "");
                            if (((firstSymbol == '@' && !username.substring(1).matches(regex)) || (firstSymbol != '@' && !username.matches(regex))) && !username.contains("t.me/")) {
                                username = "t.me/" + username;
                            }

                            socialModel.setTelegramLink(username);
                            ((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().clear();
                            //    ((ImageView) socialPopup.findViewById(R.id.telegram_icon)).setImageResource(R.drawable.icn_social_telegram);
                            Drawable colort = new ColorDrawable(Color.parseColor("#7AA5DA"));
                            Drawable imaget = getResources().getDrawable(R.drawable.icn_social_telegram2);
                            LayerDrawable ldt = new LayerDrawable(new Drawable[]{colort, imaget});
                            ((CircleImageView) socialPopup.findViewById(R.id.telegram_icon)).setImageDrawable(ldt);
                            ((CircleImageView) getActivity().findViewById(R.id.telegram_icon)).setImageDrawable(ldt);
                            //      ((CircleImageView) getActivity().findViewById(R.id.telegram_icon)).setImageDrawable(ldt);


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

                            //     ContactCacheService.updateContact(contact, mainView.getContext());
                            //     EventBus.getDefault().post(new UpdateFile());
                            editFrame.setVisibility(View.GONE);
                        } else {
                            socialModel.setTelegramLink(null);
                            ((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().clear();
                            //   ((ImageView) socialPopup.findViewById(R.id.telegram_icon)).setImageResource(R.drawable.icn_social_telegram_gray);
                            //   ((ImageView) getActivity().findViewById(R.id.telegram_icon)).setImageResource(R.drawable.icn_social_telegram_gray);
                            Drawable color7 = new ColorDrawable(Color.parseColor("#e2e5e8"));
                            Drawable image7 = getResources().getDrawable(R.drawable.icn_social_telegram2);
                            LayerDrawable ld7 = new LayerDrawable(new Drawable[]{color7, image7});
                            ((CircleImageView) socialPopup.findViewById(R.id.telegram_icon)).setImageDrawable(ld7);
                            ((CircleImageView) getActivity().findViewById(R.id.telegram_icon)).setImageDrawable(ld7);

                            ((TextView) socialPopup.findViewById(R.id.telegram_text)).setText("add new link");
                            contact.hasTelegram = false;
                            contact.setSocialModel(socialModel);
                            realm.commitTransaction();
                            //    ContactCacheService.updateContact(contact, mainView.getContext());
                            //    EventBus.getDefault().post(new UpdateFile());
                            editFrame.setVisibility(View.GONE);
                        }
                        realm.close();
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

                    editFrame.findViewById(R.id.frameImageEditSocialInsta).setVisibility(View.GONE);
                    editFrame.findViewById(R.id.imageEditSocial).setVisibility(View.GONE);
                    editFrame.findViewById(R.id.get_last_clips).setVisibility(View.VISIBLE);
                    editFrame.findViewById(R.id.searchSocial).setVisibility(View.GONE);
                    editFrame.findViewById(R.id.ok_social).setVisibility(View.GONE);
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
                //      ((EditText) editFrame.findViewById(R.id.dataToEdit)).setText("");

                editFrame.findViewById(R.id.updateContactField).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Realm realm = Realm.getDefaultInstance(); //+
                        realm.beginTransaction();

                        if (contact.getSocialModel() == null)
                            socialModel = new SocialModel();


                        if (((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().length() > 0) {
                            //    if (contact.getSocialModel() != null)
                            socialModel.setSkypeLink(((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().toString());
                            ((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().clear();
                            //     ((ImageView) socialPopup.findViewById(R.id.skype_icon)).setImageResource(R.drawable.icn_social_skype);
                            Drawable colors = new ColorDrawable(Color.parseColor("#1eb8ff"));
                            Drawable images = getResources().getDrawable(R.drawable.icn_social_skype2);
                            LayerDrawable lds = new LayerDrawable(new Drawable[]{colors, images});
                            ((CircleImageView) socialPopup.findViewById(R.id.skype_icon)).setImageDrawable(lds);
                            ((CircleImageView) getActivity().findViewById(R.id.skype_icon)).setImageDrawable(lds);
                            //    ((CircleImageView) getActivity().findViewById(R.id.skype_icon)).setImageDrawable(lds);


                            ((TextView) socialPopup.findViewById(R.id.skype_text)).setText(socialModel.getSkypeLink());
                            contact.hasSkype = true;
                            contact.setSocialModel(socialModel);
                            realm.commitTransaction();
                            //     ContactCacheService.updateContact(contact, mainView.getContext());
                            //     EventBus.getDefault().post(new UpdateFile());
                            editFrame.setVisibility(View.GONE);
                        } else {
                            socialModel.setSkypeLink(null);
                            ((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().clear();
                            //   ((ImageView) socialPopup.findViewById(R.id.skype_icon)).setImageResource(R.drawable.icn_social_skype_gray);
                            //   ((ImageView) getActivity().findViewById(R.id.skype_icon)).setImageResource(R.drawable.icn_social_skype_gray);
                            Drawable color8 = new ColorDrawable(Color.parseColor("#e2e5e8"));
                            Drawable image8 = getResources().getDrawable(R.drawable.icn_social_skype2_gray);
                            LayerDrawable ld8 = new LayerDrawable(new Drawable[]{color8, image8});
                            ((CircleImageView) socialPopup.findViewById(R.id.skype_icon)).setImageDrawable(ld8);
                            ((CircleImageView) getActivity().findViewById(R.id.skype_icon)).setImageDrawable(ld8);

                            ((TextView) socialPopup.findViewById(R.id.skype_text)).setText("add new link");
                            contact.hasSkype = false;
                            contact.setSocialModel(socialModel);
                            realm.commitTransaction();
                            //    ContactCacheService.updateContact(contact, mainView.getContext());
                            //    EventBus.getDefault().post(new UpdateFile());
                            editFrame.setVisibility(View.GONE);
                        }
                        realm.close();
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
            }
        });

        socialPopup.setVisibility(View.VISIBLE);
        openedViews.add(socialPopup);

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


    @Override
    public void closeOtherPopup() {
        if (openedViews != null) {
            for (View view : openedViews) {
                view.setVisibility(View.GONE);
            }
            openedViews.clear();
        }

        if (getActivity() != null) {
            if (getActivity().findViewById(R.id.profile_popup).getVisibility() == View.VISIBLE)
                getActivity().findViewById(R.id.profile_popup).setVisibility(View.GONE);

            if (getActivity().findViewById(R.id.company_popup).getVisibility() == View.VISIBLE)
                getActivity().findViewById(R.id.company_popup).setVisibility(View.GONE);

        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle bundle) {
        mainView = inflater.inflate(R.layout.fragment_user_create, viewGroup, false);
        mainView.setFocusable(true);
        mainView.setClickable(true);
        mainView.requestFocus();

        getActivity().findViewById(R.id.extratorContainer).setVisibility(View.GONE);
        getActivity().findViewById(R.id.framePopupSocial).setVisibility(View.GONE);
        getActivity().findViewById(R.id.countSearchContacts).setVisibility(View.GONE);

        System.out.println("Create c 1");
        adapterC = ((Postman) activityApp).getAdapter();
        toolbarC = ((Postman) activityApp).getToolbar();


        bindViews();
        System.out.println("Create c 2");
        initViews();
        System.out.println("Create c 3");
        if (listMergedContacts != null) {
            System.out.println("MERGET DATA");
            getMergedData();
        }

        System.out.println("Create c 3.3");
        initIconColor(createdContact, mainView);
        System.out.println("Create c 3.4");
        setAdapter();
        System.out.println("Create c 3.5");
        System.out.println("Create c 4");
        setupSectionPager();
        System.out.println("Create c 5");


        if (ClibpboardAdapter.checkSelectClips) {
            System.out.println("CHECK CLIPS 1");
            if (getActivity() == null)
                ((TextView) activityApp.findViewById(R.id.updateContactClipboard)).setTextColor(getResources().getColor(R.color.colorPrimary));
            else
                ((TextView) getActivity().findViewById(R.id.updateContactClipboard)).setTextColor(getResources().getColor(R.color.colorPrimary));
        }


        floatingActionMenu = (FloatingActionMenu) getActivity().findViewById(R.id.fabMenuContainer);

        getActivity().findViewById(R.id.barFlipper).setVisibility(View.GONE);
        setHasOptionsMenu(true);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);


        //  if(ContactAdapter.checkMergeContacts){

        //MainActivity.mainToolBar.setNavigationIcon(null);


        ((TextView) toolbarC.findViewById(R.id.cancel_toolbar)).setVisibility(View.VISIBLE);
        ((TextView) toolbarC.findViewById(R.id.cancel_toolbar)).setText("CANCEL");
        ((TextView) toolbarC.findViewById(R.id.cancel_toolbar)).setTextSize(14);

/*

        LinearLayout clipboardPopup = (LinearLayout) getActivity().findViewById(R.id.clipboardContainer);

        clipboardPopup.findViewById(R.id.updateContactClipboard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Click update create");
            }
        });
*/

        toolbarC.findViewById(R.id.cancel_toolbar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        getActivity());
                alertDialogBuilder.setTitle("Do you want to quit creating a contact?");
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Yes", (dialog, id) -> {
                            if (getActivity().findViewById(R.id.framePopupSocial).getVisibility() == View.VISIBLE) {
                                ((EditText) getActivity().findViewById(R.id.magic_edit_text)).setText("");
                            }
                            getActivity().findViewById(R.id.framePopupSocial).setVisibility(View.GONE);
                            popupHelpCompanyposition.setVisibility(View.GONE);

                            ContactAdapter.checkMergeContacts = false;
                            ContactsFragment.createContact = false;
                            floatingActionMenu.setVisibility(View.VISIBLE);
                            ((TextView) toolbarC.findViewById(R.id.cancel_toolbar)).setVisibility(View.GONE);
                            getActivity().onBackPressed();


                        })
                        .setNegativeButton("No", (dialog, id) -> dialog.cancel());

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
        //MainActivity.mainToolBar.setNavigationIcon(null);

        System.out.println("Create c 6");
        //  }


        return mainView;
    }

    public boolean createNew = false;

    public void setupSectionPager() {
        ViewPager viewPager = (ViewPager) mainView.findViewById(R.id.viewpagerCreate);

        //companyProfileDataFragment = new CompanyProfileDataFragment();
        contactProfileDataFragment = new ContactProfileDataFragment();
        Bundle args = new Bundle();
        if (listMergedContacts == null || listMergedContacts.isEmpty())
            createNew = true;

        args.putSerializable("selectedContact", createdContact);
        contactProfileDataFragment.setArguments(args);
        //companyProfileDataFragment.setArguments(args);
        adapter = new ProfileSectionAdapter(getChildFragmentManager());
        //    if(selectedContact.listOfContacts==null || selectedContact.listOfContacts.isEmpty())
        adapter.addFragment(contactProfileDataFragment, "Profile");
        //     else
        //
        //        adapter.addFragment(companyProfileDataFragment  , "Profile");

        contactTimelineDataFragment = new ContactTimelineDataFragment();
        contactTimelineDataFragment.setArguments(args);
        adapter.addFragment(contactTimelineDataFragment, "Timeline");
        adapter.addFragment(new ScheduleFragment(), "Schedule");
        //  adapter.addFragment(new PlacesFragment(), "Places");
        adapter.addFragment(new FilesFragment(), "Files");
        viewPager.setAdapter(adapter);
        SmartTabLayout tabs = (SmartTabLayout) mainView.findViewById(R.id.result_tabsCreate);
        tabs.setViewPager(viewPager);
       /* mainView.findViewById(R.id.closerTabsCreate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeOtherPopup();
                contactProfileDataFragment.closeOtherPopup();
            }
        });*/


    }


    @Override
    public void onStop() {
        try {
            if (socialPopup != null && socialPopup.getVisibility() == View.VISIBLE)
                socialPopup.setVisibility(View.GONE);

            if (createNew)
                ContactCacheService.removeContactById(createdContact);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ContactsFragment.createContact = false;
        super.onStop();
    }

    @Override

    public void addSelectedCompany(String company) {
        //  ((EditText)companySelectPopup.findViewById(R.id.companyName)).setText(company);

//        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
//                getActivity());
//        alertDialogBuilder.setTitle("Do you want to add "+tag+" ?");
//        alertDialogBuilder
//                .setCancelable(false)
//                .setPositiveButton("Yes", (dialog, id) -> {
//                    customTagsAdapter.addHashTag(tag);
//                    contact.getListOfHashtags().add(new HashTag(tag.trim()));
//                    ContactCacheService.updateContact(contact,mainView.getContext());
//                    EventBus.getDefault().post(new UpdateFile());
//                    Toast.makeText(mainView.getContext(), "Hashtag successfully added", Toast.LENGTH_SHORT).show();
//                    initRecyclerHashTags();
//                    addHashtagPreview(tag);
//                })
//                .setNegativeButton("No", (dialog, id) -> dialog.cancel());
//
//        AlertDialog alertDialog = alertDialogBuilder.create();
//        alertDialog.show();
    }

    @Override
    public void addSelectedCompanyEdit(String company) {
        ((EditText) mainView.findViewById(R.id.company_title_edit)).setText(company);
        ((EditText) mainView.findViewById(R.id.company_title_edit)).setSelection(((EditText) mainView.findViewById(R.id.company_title_edit)).getText().length());
        //popupHelpCompanyposition.setVisibility(View.GONE);
        //fastEditPopup.setVisibility(View.GONE);

//        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
//                getActivity());
//        alertDialogBuilder.setTitle("Do you want to add "+tag+" ?");
//        alertDialogBuilder
//                .setCancelable(false)
//                .setPositiveButton("Yes", (dialog, id) -> {
//                    customTagsAdapter.addHashTag(tag);
//                    contact.getListOfHashtags().add(new HashTag(tag.trim()));
//                    ContactCacheService.updateContact(contact,mainView.getContext());
//                    EventBus.getDefault().post(new UpdateFile());
//                    Toast.makeText(mainView.getContext(), "Hashtag successfully added", Toast.LENGTH_SHORT).show();
//                    initRecyclerHashTags();
//                    addHashtagPreview(tag);
//                })
//                .setNegativeButton("No", (dialog, id) -> dialog.cancel());
//
//        AlertDialog alertDialog = alertDialogBuilder.create();
//        alertDialog.show();
    }

    public void addSelectPosition(String position) {
        ((EditText) mainView.findViewById(R.id.company_edit)).setText(position);
        ((EditText) mainView.findViewById(R.id.company_edit)).setSelection(((EditText) mainView.findViewById(R.id.company_edit)).getText().length());
        //popupPositionEdit.setVisibility(View.GONE);
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void showEditPopupPreviewSocial(Contact contact, SocialEnums typeEnum) {
        popupProfileEditPreviewSocial = (FrameLayout) getActivity().findViewById(R.id.popupPreviewEditSocial);

        //popupProfileEditPreviewSocial.findViewById(R.id.line_preview_social).setVisibility(View.GONE);

        popupProfileEditPreviewSocial.findViewById(R.id.imageView3).setVisibility(View.GONE);

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

                        Realm realm = Realm.getDefaultInstance(); //-

                        if (typeEnum.equals(SocialEnums.FACEBOOK)) {
                            realm.beginTransaction();
                            //SocialModel socialModel;
                            if (socialModel == null)

                                socialModel = new SocialModel();
                            /*}else
                                socialModel = contact.getSocialModel();*/

                            if (((EditText) popupEditSocial.findViewById(R.id.dataToEdit)).getText().length() > 0) {

                                if (socialModel.getFacebookLink() != null && !socialModel.getFacebookLink().isEmpty()) {
                                    contactService.deleteNoteContact(contact.getIdContact(), socialModel.getFacebookLink());
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



                                /*if(profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE){
                                    ((ImageView) profilePopUp.findViewById(R.id.facebook_icon)).setImageDrawable(ldf);
                                }else if(companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE){
                                    ((ImageView) companyProfilePopup.findViewById(R.id.facebook_icon)).setImageDrawable(ldf);
                                }else{*/
                                ((ImageView) getActivity().findViewById(R.id.facebook_icon)).setImageDrawable(ldf);
                                //}


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
                                contactService.addNoteToContact(contact.getIdContact(), contact.getSocialModel().getFacebookLink(), contact.getName());
                                ContactCacheService.updateContact(contact, mainView.getContext());
                                //  EventBus.getDefault().post(new UpdateFile());
                                popupEditSocial.setVisibility(View.GONE);
                                ((TextView) popupProfileEditPreviewSocial.findViewById(R.id.valueUpd)).setText(contact.getSocialModel().getFacebookLink());

                            } else {
                                if (socialModel.getFacebookLink() != null && !socialModel.getFacebookLink().isEmpty()) {
                                    contactService.deleteNoteContact(contact.getIdContact(), socialModel.getFacebookLink());
                                }
                                contact.getSocialModel().setFacebookLink(null);
                                //  ((ImageView) socialPopup.findViewById(R.id.facebook_icon)).setImageResource(R.drawable.icn_social_facebook_gray);
                                //  ((ImageView) getActivity().findViewById(R.id.facebook_icon)).setImageResource(R.drawable.icn_social_facebook_gray);
                                Drawable color = new ColorDrawable(Color.parseColor("#e2e5e8"));
                                Drawable image = getResources().getDrawable(R.drawable.icn_social_facebook2);
                                LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
                                ((ImageView) socialPopup.findViewById(R.id.facebook_icon)).setImageDrawable(ld);


                                /*if(profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE){
                                    ((ImageView) profilePopUp.findViewById(R.id.facebook_icon)).setImageDrawable(ld);
                                }else if(companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE){
                                    ((ImageView) companyProfilePopup.findViewById(R.id.facebook_icon)).setImageDrawable(ld);
                                }else{*/
                                ((ImageView) getActivity().findViewById(R.id.facebook_icon)).setImageDrawable(ld);
                                //}


                                ((TextView) socialPopup.findViewById(R.id.facebook_nick)).setText("add username or link");
                                contact.hasFacebook = false;
                                contact.setSocialModel(socialModel);
                                realm.commitTransaction();
                                ContactCacheService.updateContact(contact, mainView.getContext());
                                // EventBus.getDefault().post(new UpdateFile());
                                popupEditSocial.setVisibility(View.GONE);
                                popupProfileEditPreviewSocial.setVisibility(View.GONE);
                            }

                           // EventBus.getDefault().post(new UpdateFile());

                        } else if (typeEnum.equals(SocialEnums.VK)) {


                            realm.beginTransaction();

                            if (contact.getSocialModel() == null)
                                socialModel = new SocialModel();


                            if (((EditText) popupEditSocial.findViewById(R.id.dataToEdit)).getText().length() > 0) {
                                //     if (contact.getSocialModel() != null)

                                if (socialModel.getVkLink() != null && !socialModel.getVkLink().isEmpty()) {
                                    contactService.deleteNoteContact(contact.getIdContact(), socialModel.getVkLink());
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


                               /* if(profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE){
                                    ((ImageView) profilePopUp.findViewById(R.id.vk_icon)).setImageDrawable(ldv);
                                }else if(companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE){
                                    ((ImageView) companyProfilePopup.findViewById(R.id.vk_icon)).setImageDrawable(ldv);
                                }else{*/
                                ((ImageView) getActivity().findViewById(R.id.vk_icon)).setImageDrawable(ldv);
                                //}


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
                                contactService.addNoteToContact(contact.getIdContact(), contact.getSocialModel().getVkLink(), contact.getName());
                                ContactCacheService.updateContact(contact, mainView.getContext());
                                // EventBus.getDefault().post(new UpdateFile());
                                popupEditSocial.setVisibility(View.GONE);
                                ((TextView) popupProfileEditPreviewSocial.findViewById(R.id.valueUpd)).setText(contact.getSocialModel().getVkLink());
                            } else {
                                if (socialModel.getVkLink() != null && !socialModel.getVkLink().isEmpty()) {
                                    contactService.deleteNoteContact(contact.getIdContact(), socialModel.getVkLink());
                                }
                                contact.getSocialModel().setVkLink(null);
                                //     ((ImageView) socialPopup.findViewById(R.id.vk_icon)).setImageResource(R.drawable.icn_social_vk_gray);
                                //     ((ImageView) getActivity().findViewById(R.id.vk_icon)).setImageResource(R.drawable.icn_social_vk_gray);
                                Drawable color2 = new ColorDrawable(Color.parseColor("#e2e5e8"));
                                Drawable image2 = getResources().getDrawable(R.drawable.icn_social_vk2);
                                LayerDrawable ld2 = new LayerDrawable(new Drawable[]{color2, image2});
                                ((ImageView) socialPopup.findViewById(R.id.vk_icon)).setImageDrawable(ld2);

                                ((ImageView) getActivity().findViewById(R.id.vk_icon)).setImageDrawable(ld2);


                                ((TextView) socialPopup.findViewById(R.id.vkNick)).setText("add username or");
                                contact.hasVk = false;
                                contact.setSocialModel(socialModel);
                                realm.commitTransaction();
                                ContactCacheService.updateContact(contact, mainView.getContext());
                                //  EventBus.getDefault().post(new UpdateFile());
                                popupEditSocial.setVisibility(View.GONE);
                                popupProfileEditPreviewSocial.setVisibility(View.GONE);
                            }

                          /*  ArrayList<String> listEdit = new ArrayList<>();
                            ArrayList<Boolean> listEditBool = new ArrayList<>();

                            listEdit.add(contact.getName());
                            if (contact.listOfContacts == null || contact.listOfContacts.isEmpty())
                                listEditBool.add(true);
                            else
                                listEditBool.add(false);

                            MainActivity.listToManyUpdateFile.add("EDIT");
                            MainActivity.listToManyUpdateFile.add(listEdit);
                            MainActivity.listToManyUpdateFile.add(listEditBool);*/

                           // EventBus.getDefault().post(new UpdateFile());
                        } else if (typeEnum.equals(SocialEnums.INSTAGRAM)) {


                            realm.beginTransaction();

                            if (contact.getSocialModel() == null)
                                socialModel = new SocialModel();


                            if (((EditText) popupEditSocial.findViewById(R.id.dataToEdit)).getText().length() > 0) {
                                //  if (contact.getSocialModel() != null)

                                if (socialModel.getInstagramLink() != null && !socialModel.getInstagramLink().isEmpty()) {
                                    contactService.deleteNoteContact(contact.getIdContact(), socialModel.getInstagramLink());
                                }



                                String username = ((EditText) popupEditSocial.findViewById(R.id.dataToEdit)).getText().toString();

                                if (username.contains("?utm")) {
                                    int ind = username.indexOf('?');
                                    if (ind != -1)
                                        username = username.substring(0, ind);

                                }

                                if (username.charAt(username.length() - 1) == '/') {
                                    username = username.substring(0, username.length() - 1);
                                    //contactsService.updateNote(contact.getIdContact(), note, username);
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

                                ((ImageView) getActivity().findViewById(R.id.inst_icon)).setImageDrawable(ldi);


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

                                contactService.addNoteToContact(contact.getIdContact(), contact.getSocialModel().getInstagramLink(), contact.getName());
                                ContactCacheService.updateContact(contact, mainView.getContext());
                                //  EventBus.getDefault().post(new UpdateFile());
                                popupEditSocial.setVisibility(View.GONE);
                                ((TextView) popupProfileEditPreviewSocial.findViewById(R.id.valueUpd)).setText(contact.getSocialModel().getInstagramLink());
                            } else {
                                if (socialModel.getInstagramLink() != null && !socialModel.getInstagramLink().isEmpty()) {
                                    contactService.deleteNoteContact(contact.getIdContact(), socialModel.getInstagramLink());
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

                                /*if(profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE){
                                    ((ImageView) profilePopUp.findViewById(R.id.inst_icon)).setImageDrawable(ld4);
                                }else if(companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE){
                                    ((ImageView) companyProfilePopup.findViewById(R.id.inst_icon)).setImageDrawable(ld4);
                                }else{*/
                                ((ImageView) getActivity().findViewById(R.id.inst_icon)).setImageDrawable(ld4);


                                ((TextView) socialPopup.findViewById(R.id.instagramNick)).setText("add username or link");
                                contact.hasInst = false;
                                contact.setSocialModel(socialModel);
                                realm.commitTransaction();

                                ContactCacheService.updateContact(contact, mainView.getContext());
                                //    EventBus.getDefault().post(new UpdateFile());
                                popupEditSocial.setVisibility(View.GONE);
                                popupProfileEditPreviewSocial.setVisibility(View.GONE);
                            }


                            //EventBus.getDefault().post(new UpdateFile());


                        } else if (typeEnum.equals(SocialEnums.MEDIUM)) {


                            realm.beginTransaction();

                            if (contact.getSocialModel() == null)
                                socialModel = new SocialModel();


                            if (((EditText) popupEditSocial.findViewById(R.id.dataToEdit)).getText().length() > 0) {
                                //  if (contact.getSocialModel() != null)

                                if (socialModel.getMediumLink() != null && !socialModel.getMediumLink().isEmpty()) {
                                    contactService.deleteNoteContact(contact.getIdContact(), socialModel.getMediumLink());
                                }



                                String username = ((EditText) popupEditSocial.findViewById(R.id.dataToEdit)).getText().toString();

                                if (username.contains("?utm")) {
                                    int ind = username.indexOf('?');
                                    if (ind != -1)
                                        username = username.substring(0, ind);

                                }

                                if (username.charAt(username.length() - 1) == '/') {
                                    username = username.substring(0, username.length() - 1);
                                    //contactsService.updateNote(contact.getIdContact(), note, username);
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

                                ((ImageView) getActivity().findViewById(R.id.medium_icon)).setImageDrawable(ldi);


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

                                contactService.addNoteToContact(contact.getIdContact(), contact.getSocialModel().getMediumLink(), contact.getName());
                                ContactCacheService.updateContact(contact, mainView.getContext());
                                //  EventBus.getDefault().post(new UpdateFile());
                                popupEditSocial.setVisibility(View.GONE);
                                ((TextView) popupProfileEditPreviewSocial.findViewById(R.id.valueUpd)).setText(contact.getSocialModel().getMediumLink());
                            } else {
                                if (socialModel.getMediumLink() != null && !socialModel.getMediumLink().isEmpty()) {
                                    contactService.deleteNoteContact(contact.getIdContact(), socialModel.getMediumLink());
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

                                /*if(profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE){
                                    ((ImageView) profilePopUp.findViewById(R.id.inst_icon)).setImageDrawable(ld4);
                                }else if(companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE){
                                    ((ImageView) companyProfilePopup.findViewById(R.id.inst_icon)).setImageDrawable(ld4);
                                }else{*/
                                ((ImageView) getActivity().findViewById(R.id.medium_icon)).setImageDrawable(ld4);


                                ((TextView) socialPopup.findViewById(R.id.mediumNick)).setText("add username or link");
                                contact.hasMedium = false;
                                contact.setSocialModel(socialModel);
                                realm.commitTransaction();

                                ContactCacheService.updateContact(contact, mainView.getContext());
                                //    EventBus.getDefault().post(new UpdateFile());
                                popupEditSocial.setVisibility(View.GONE);
                                popupProfileEditPreviewSocial.setVisibility(View.GONE);
                            }


                           // EventBus.getDefault().post(new UpdateFile());


                        } else if (typeEnum.equals(SocialEnums.LINKEDIN)) {

                            realm.beginTransaction();

                            if (contact.getSocialModel() == null)
                                socialModel = new SocialModel();


                            if (((EditText) popupEditSocial.findViewById(R.id.dataToEdit)).getText().length() > 0) {
                                //    if (contact.getSocialModel() != null)

                                if (socialModel.getLinkedInLink() != null && !socialModel.getLinkedInLink().isEmpty()) {
                                    contactService.deleteNoteContact(contact.getIdContact(), socialModel.getLinkedInLink());
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

                               /* if(profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE){
                                    ((ImageView) profilePopUp.findViewById(R.id.link_icon)).setImageDrawable(ldl);
                                }else if(companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE){
                                    ((ImageView) companyProfilePopup.findViewById(R.id.link_icon)).setImageDrawable(ldl);
                                }else{*/
                                ((ImageView) getActivity().findViewById(R.id.link_icon)).setImageDrawable(ldl);


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
                                contactService.addNoteToContact(contact.getIdContact(), contact.getSocialModel().getLinkedInLink(), contact.getName());
                                ContactCacheService.updateContact(contact, mainView.getContext());
                                //  EventBus.getDefault().post(new UpdateFile());
                                popupEditSocial.setVisibility(View.GONE);
                                ((TextView) popupProfileEditPreviewSocial.findViewById(R.id.valueUpd)).setText(contact.getSocialModel().getLinkedInLink());
                            } else {
                                if (socialModel.getLinkedInLink() != null && !socialModel.getLinkedInLink().isEmpty()) {
                                    contactService.deleteNoteContact(contact.getIdContact(), socialModel.getLinkedInLink());
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

                                /*if(profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE){
                                    ((ImageView) profilePopUp.findViewById(R.id.link_icon)).setImageDrawable(ld3);
                                }else if(companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE){
                                    ((ImageView) companyProfilePopup.findViewById(R.id.link_icon)).setImageDrawable(ld3);
                                }else{*/
                                ((ImageView) getActivity().findViewById(R.id.link_icon)).setImageDrawable(ld3);
                                //}

                                ((TextView) socialPopup.findViewById(R.id.link_text)).setText("add username or link");
                                contact.hasLinked = false;
                                contact.setSocialModel(socialModel);
                                realm.commitTransaction();
                                ContactCacheService.updateContact(contact, mainView.getContext());
                                //  EventBus.getDefault().post(new UpdateFile());
                                popupEditSocial.setVisibility(View.GONE);
                                popupProfileEditPreviewSocial.setVisibility(View.GONE);
                            }


                            //EventBus.getDefault().post(new UpdateFile());

                        } else if (typeEnum.equals(SocialEnums.VIBER)) {


                            realm.beginTransaction();

                            if (contact.getSocialModel() == null)
                                socialModel = new SocialModel();


                            if (((EditText) popupEditSocial.findViewById(R.id.dataToEdit)).getText().length() > 0) {
                                //       if (contact.getSocialModel() != null)

                                if (socialModel.getViberLink() != null && !socialModel.getViberLink().isEmpty()) {
                                    contactService.deleteNoteContact(contact.getIdContact(), socialModel.getViberLink());
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

                               /* if(profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE){
                                    ((CircleImageView) profilePopUp.findViewById(R.id.viber_icon)).setImageDrawable(ldvi);
                                }else if(companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE){
                                    ((CircleImageView) companyProfilePopup.findViewById(R.id.viber_icon)).setImageDrawable(ldvi);
                                }else{*/
                                ((CircleImageView) getActivity().findViewById(R.id.viber_icon)).setImageDrawable(ldvi);
                                //}


                                //   if (contact.getSocialModel() != null)
                                ((TextView) socialPopup.findViewById(R.id.viber_text)).setText(socialModel.getViberLink());
                                contact.hasViber = true;
                                contact.setSocialModel(socialModel);
                                realm.commitTransaction();
                                contactService.addNoteToContact(contact.getIdContact(), contact.getSocialModel().getViberLink(), contact.getName());
                                ContactCacheService.updateContact(contact, mainView.getContext());
                                //    EventBus.getDefault().post(new UpdateFile());
                                popupEditSocial.setVisibility(View.GONE);
                                ((TextView) popupProfileEditPreviewSocial.findViewById(R.id.valueUpd)).setText(contact.getSocialModel().getViberLink());
                            } else {
                                if (socialModel.getViberLink() != null && !socialModel.getViberLink().isEmpty()) {
                                    contactService.deleteNoteContact(contact.getIdContact(), socialModel.getViberLink());
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

                               /* if(profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE){
                                    ((CircleImageView) profilePopUp.findViewById(R.id.viber_icon)).setImageDrawable(ld5);
                                }else if(companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE){
                                    ((CircleImageView) companyProfilePopup.findViewById(R.id.viber_icon)).setImageDrawable(ld5);
                                }else{*/
                                ((CircleImageView) getActivity().findViewById(R.id.viber_icon)).setImageDrawable(ld5);
                                //}

                                ((TextView) socialPopup.findViewById(R.id.viber_text)).setText("add new link");
                                contact.hasViber = false;
                                contact.setSocialModel(socialModel);
                                realm.commitTransaction();
                                ContactCacheService.updateContact(contact, mainView.getContext());
                                //   EventBus.getDefault().post(new UpdateFile());
                                popupEditSocial.setVisibility(View.GONE);
                                popupProfileEditPreviewSocial.setVisibility(View.GONE);
                            }


                            //EventBus.getDefault().post(new UpdateFile());


                        } else if (typeEnum.equals(SocialEnums.WHATSAPP)) {


                            realm.beginTransaction();

                            if (contact.getSocialModel() == null)
                                socialModel = new SocialModel();


                            //  contact.setSocialModel(new SocialModel());
                            if (((EditText) popupEditSocial.findViewById(R.id.dataToEdit)).getText().length() > 0) {
                                // if (contact.getSocialModel() != null)

                                if (socialModel.getWhatsappLink() != null && !socialModel.getWhatsappLink().isEmpty()) {
                                    contactService.deleteNoteContact(contact.getIdContact(), socialModel.getWhatsappLink());
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

                                /*if(profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE){
                                    ((CircleImageView) profilePopUp.findViewById(R.id.whatsapp_icon)).setImageDrawable(ldw);
                                }else if(companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE){
                                    ((CircleImageView) companyProfilePopup.findViewById(R.id.whatsapp_icon)).setImageDrawable(ldw);
                                }else{*/
                                ((CircleImageView) getActivity().findViewById(R.id.whatsapp_icon)).setImageDrawable(ldw);
                                //}

                                //   if(contact.getSocialModel() != null)
                                ((TextView) socialPopup.findViewById(R.id.whatsapp_text)).setText(socialModel.getWhatsappLink());


                                contact.hasWhatsapp = true;
                                contact.setSocialModel(socialModel);
                                realm.commitTransaction();
                                contactService.addNoteToContact(contact.getIdContact(), contact.getSocialModel().getWhatsappLink(), contact.getName());
                                ContactCacheService.updateContact(contact, mainView.getContext());
                                // EventBus.getDefault().post(new UpdateFile());
                                popupEditSocial.setVisibility(View.GONE);
                                ((TextView) popupProfileEditPreviewSocial.findViewById(R.id.valueUpd)).setText(contact.getSocialModel().getWhatsappLink());
                            } else {
                                if (socialModel.getWhatsappLink() != null && !socialModel.getWhatsappLink().isEmpty()) {
                                    contactService.deleteNoteContact(contact.getIdContact(), socialModel.getWhatsappLink());
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

                                /*if(profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE){
                                    ((CircleImageView) profilePopUp.findViewById(R.id.whatsapp_icon)).setImageDrawable(ld6);
                                }else if(companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE){
                                    ((CircleImageView) companyProfilePopup.findViewById(R.id.whatsapp_icon)).setImageDrawable(ld6);
                                }else{*/
                                ((CircleImageView) getActivity().findViewById(R.id.whatsapp_icon)).setImageDrawable(ld6);
                                //}


                                ((TextView) socialPopup.findViewById(R.id.whatsapp_text)).setText("add new link");
                                contact.hasWhatsapp = false;
                                contact.setSocialModel(socialModel);
                                realm.commitTransaction();
                                ContactCacheService.updateContact(contact, mainView.getContext());
                                //  EventBus.getDefault().post(new UpdateFile());
                                popupEditSocial.setVisibility(View.GONE);
                                popupProfileEditPreviewSocial.setVisibility(View.GONE);
                            }


                            //EventBus.getDefault().post(new UpdateFile());


                        } else if (typeEnum.equals(SocialEnums.TELEGRAM)) {


                            realm.beginTransaction();

                            if (contact.getSocialModel() == null)
                                socialModel = new SocialModel();


                            if (((EditText) popupEditSocial.findViewById(R.id.dataToEdit)).getText().length() > 0) {
                                //    if (contact.getSocialModel() != null)

                                if (socialModel.getTelegramLink() != null && !socialModel.getTelegramLink().isEmpty()) {
                                    contactService.deleteNoteContact(contact.getIdContact(), socialModel.getTelegramLink());
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

                                /*if(profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE){
                                    ((CircleImageView) profilePopUp.findViewById(R.id.telegram_icon)).setImageDrawable(ldt);
                                }else if(companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE){
                                    ((CircleImageView) companyProfilePopup.findViewById(R.id.telegram_icon)).setImageDrawable(ldt);
                                }else{*/
                                ((CircleImageView) getActivity().findViewById(R.id.telegram_icon)).setImageDrawable(ldt);
                                //}

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

                                contactService.addNoteToContact(contact.getIdContact(), contact.getSocialModel().getTelegramLink(), contact.getName());
                                ContactCacheService.updateContact(contact, mainView.getContext());
                                //  EventBus.getDefault().post(new UpdateFile());
                                popupEditSocial.setVisibility(View.GONE);
                                ((TextView) popupProfileEditPreviewSocial.findViewById(R.id.valueUpd)).setText(contact.getSocialModel().getTelegramLink());
                            } else {
                                if (socialModel.getTelegramLink() != null && !socialModel.getTelegramLink().isEmpty()) {
                                    contactService.deleteNoteContact(contact.getIdContact(), socialModel.getTelegramLink());
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

                                /*if(profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE){
                                    ((CircleImageView) profilePopUp.findViewById(R.id.telegram_icon)).setImageDrawable(ld7);
                                }else if(companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE){
                                    ((CircleImageView) companyProfilePopup.findViewById(R.id.telegram_icon)).setImageDrawable(ld7);
                                }else{*/
                                ((CircleImageView) getActivity().findViewById(R.id.telegram_icon)).setImageDrawable(ld7);
                                //}

                                ((TextView) socialPopup.findViewById(R.id.telegram_text)).setText("add new link");
                                contact.hasTelegram = false;
                                contact.setSocialModel(socialModel);
                                realm.commitTransaction();
                                ContactCacheService.updateContact(contact, mainView.getContext());
                                //  EventBus.getDefault().post(new UpdateFile());
                                popupEditSocial.setVisibility(View.GONE);
                                popupProfileEditPreviewSocial.setVisibility(View.GONE);
                            }


                            //EventBus.getDefault().post(new UpdateFile());


                        } else if (typeEnum.equals(SocialEnums.SKYPE)) {


                            realm.beginTransaction();

                            if (contact.getSocialModel() == null)
                                socialModel = new SocialModel();


                            if (((EditText) popupEditSocial.findViewById(R.id.dataToEdit)).getText().length() > 0) {
                                //     if (contact.getSocialModel() != null)
                                //     contact.getSocialModel().setSkypeLink(((EditText) editFrame.findViewById(R.id.dataToEdit)).getText().toString());
                                //     else

                                if (socialModel.getSkypeLink() != null && !socialModel.getSkypeLink().isEmpty()) {
                                    contactService.deleteNoteContact(contact.getIdContact(), socialModel.getSkypeLink());
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

                                /*if(profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE){
                                    ((CircleImageView) profilePopUp.findViewById(R.id.skype_icon)).setImageDrawable(lds);
                                }else if(companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE){
                                    ((CircleImageView) companyProfilePopup.findViewById(R.id.skype_icon)).setImageDrawable(lds);
                                }else{*/
                                ((CircleImageView) getActivity().findViewById(R.id.skype_icon)).setImageDrawable(lds);
                                //}

                                //    if (contact.getSocialModel() != null)
                                //    ((TextView) socialPopup.findViewById(R.id.skype_text)).setText(contact.getSocialModel().getSkypeLink());
                                //    else
                                ((TextView) socialPopup.findViewById(R.id.skype_text)).setText(socialModel.getSkypeLink());
                                contact.hasSkype = true;
                                contact.setSocialModel(socialModel);
                                realm.commitTransaction();
                                contactService.addNoteToContact(contact.getIdContact(), contact.getSocialModel().getSkypeLink(), contact.getName());
                                ContactCacheService.updateContact(contact, mainView.getContext());
                                //  EventBus.getDefault().post(new UpdateFile());
                                popupEditSocial.setVisibility(View.GONE);
                                ((TextView) popupProfileEditPreviewSocial.findViewById(R.id.valueUpd)).setText(contact.getSocialModel().getSkypeLink());
                            } else {
                                if (socialModel.getSkypeLink() != null && !socialModel.getSkypeLink().isEmpty()) {
                                    contactService.deleteNoteContact(contact.getIdContact(), socialModel.getSkypeLink());
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

                                /*if(profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE){
                                    ((CircleImageView) profilePopUp.findViewById(R.id.skype_icon)).setImageDrawable(ld8);
                                }else if(companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE){
                                    ((CircleImageView) companyProfilePopup.findViewById(R.id.skype_icon)).setImageDrawable(ld8);
                                }else{*/
                                ((CircleImageView) getActivity().findViewById(R.id.skype_icon)).setImageDrawable(ld8);
                                //}

                                ((TextView) socialPopup.findViewById(R.id.skype_text)).setText("add new link");
                                contact.hasSkype = false;
                                contact.setSocialModel(socialModel);
                                realm.commitTransaction();
                                ContactCacheService.updateContact(contact, mainView.getContext());
                                //    EventBus.getDefault().post(new UpdateFile());
                                popupEditSocial.setVisibility(View.GONE);
                                popupProfileEditPreviewSocial.setVisibility(View.GONE);
                            }


                           // EventBus.getDefault().post(new UpdateFile());

                        } else if (typeEnum.equals(SocialEnums.TWITTER)) {


                            realm.beginTransaction();

                            if (contact.getSocialModel() == null)
                                socialModel = new SocialModel();


                            if (((EditText) popupEditSocial.findViewById(R.id.dataToEdit)).getText().length() > 0) {
                                //     if (contact.getSocialModel() != null)

                                if (socialModel.getTwitterLink() != null && !socialModel.getTwitterLink().isEmpty()) {
                                    contactService.deleteNoteContact(contact.getIdContact(), socialModel.getTwitterLink());
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
                                ((ImageView) getActivity().findViewById(R.id.vk_icon)).setImageDrawable(ldv);

                          /*  ((ImageView) getActivity().findViewById(R.id.vk_icon)).setImageDrawable(ldv);

                            try{
                                ((ImageView) profilePopUp.findViewById(R.id.vk_icon)).setImageDrawable(ldv);
                            }catch (Exception e){

                            }

                            try{
                                ((ImageView) companyProfilePopup.findViewById(R.id.vk_icon)).setImageDrawable(ldv);
                            }catch (Exception e){

                            }*/


                               /* if(profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE){
                                    ((ImageView) profilePopUp.findViewById(R.id.vk_icon)).setImageDrawable(ldv);
                                }else if(companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE){
                                    ((ImageView) companyProfilePopup.findViewById(R.id.vk_icon)).setImageDrawable(ldv);
                                }else{*/
                                //((ImageView) getActivity().findViewById(R.id.twitter_icon)).setImageDrawable(ldv);
                                //}


                                /*try {
                                    String link = socialModel.getVkLink();
                                    String checkVK = link.substring(0, 8);
                                    if (link.contains("https://vk.com/")) {
                                        System.out.println("TRUE VK LINK");
                                        link = link.substring(15, link.length());
                                        System.out.println("TRUE VK LINK2 = " + link);
                                        ((TextView) socialPopup.findViewById(R.id.vkNick)).setText(link);
                                    }else if(link.contains("https://m.vk.com/")){
                                        link = link.substring(17, link.length());
                                        ((TextView) socialPopup.findViewById(R.id.vkNick)).setText(link);

                                    }else if(link.contains("m.vk.com/")) {
                                        link = link.substring(9, link.length());
                                        ((TextView) socialPopup.findViewById(R.id.vkNick)).setText(link);
                                    }else if(link.contains("vk.com/")) {
                                        link = link.substring(7, link.length());
                                        ((TextView) socialPopup.findViewById(R.id.vkNick)).setText(link);
                                    }else if(link.contains("https://www.vk.com/")) {
                                        link = link.substring(19, link.length());
                                        ((TextView) socialPopup.findViewById(R.id.vkNick)).setText(link);

                                    }else
                                        ((TextView) socialPopup.findViewById(R.id.vkNick)).setText(socialModel.getVkLink());
                                }catch (Exception e) {
                                    ((TextView) socialPopup.findViewById(R.id.vkNick)).setText(socialModel.getVkLink());
                                }*/

                                //        if (contact.getSocialModel() != null)
                                //    ((TextView) socialPopup.findViewById(R.id.vkNick)).setText(socialModel.getVkLink());
                                if (socialModel.getTwitterLink().contains(".com/")) {
                                    ((TextView) socialPopup.findViewById(R.id.twitterNick)).setText(socialModel.getTwitterLink().substring(socialModel.getTwitterLink().indexOf(".com/") + 5));
                                } else
                                    ((TextView) socialPopup.findViewById(R.id.twitterNick)).setText(socialModel.getTwitterLink());

                                contact.hasTwitter = true;
                                contact.setSocialModel(socialModel);
                                realm.commitTransaction();
                                contactService.addNoteToContact(contact.getIdContact(), contact.getSocialModel().getTwitterLink(), contact.getName());
                                ContactCacheService.updateContact(contact, mainView.getContext());
                                // EventBus.getDefault().post(new UpdateFile());
                                popupEditSocial.setVisibility(View.GONE);
                                ((TextView) popupProfileEditPreviewSocial.findViewById(R.id.valueUpd)).setText(contact.getSocialModel().getTwitterLink());
                            } else {
                                if (socialModel.getTwitterLink() != null && !socialModel.getTwitterLink().isEmpty()) {
                                    contactService.deleteNoteContact(contact.getIdContact(), socialModel.getTwitterLink());
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

                           // EventBus.getDefault().post(new UpdateFile());

                        } else if (typeEnum.equals(SocialEnums.YOUTUBE)) {


                            realm.beginTransaction();

                            if (contact.getSocialModel() == null)
                                socialModel = new SocialModel();


                            if (((EditText) popupEditSocial.findViewById(R.id.dataToEdit)).getText().length() > 0) {
                                //     if (contact.getSocialModel() != null)

                                if (socialModel.getYoutubeLink() != null && !socialModel.getYoutubeLink().isEmpty()) {
                                    contactService.deleteNoteContact(contact.getIdContact(), socialModel.getYoutubeLink());
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


                               /* if(profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE){
                                    ((ImageView) profilePopUp.findViewById(R.id.vk_icon)).setImageDrawable(ldv);
                                }else if(companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE){
                                    ((ImageView) companyProfilePopup.findViewById(R.id.vk_icon)).setImageDrawable(ldv);
                                }else{*/
                                //((ImageView) getActivity().findViewById(R.id.vk_icon)).setImageDrawable(ldv);
                                //}


                              /*  try {
                                    String link = socialModel.getVkLink();
                                    String checkVK = link.substring(0, 8);
                                    if (link.contains("https://vk.com/")) {
                                        System.out.println("TRUE VK LINK");
                                        link = link.substring(15, link.length());
                                        System.out.println("TRUE VK LINK2 = " + link);
                                        ((TextView) socialPopup.findViewById(R.id.vkNick)).setText(link);
                                    }else if(link.contains("https://m.vk.com/")){
                                        link = link.substring(17, link.length());
                                        ((TextView) socialPopup.findViewById(R.id.vkNick)).setText(link);

                                    }else if(link.contains("m.vk.com/")) {
                                        link = link.substring(9, link.length());
                                        ((TextView) socialPopup.findViewById(R.id.vkNick)).setText(link);
                                    }else if(link.contains("vk.com/")) {
                                        link = link.substring(7, link.length());
                                        ((TextView) socialPopup.findViewById(R.id.vkNick)).setText(link);
                                    }else if(link.contains("https://www.vk.com/")) {
                                        link = link.substring(19, link.length());
                                        ((TextView) socialPopup.findViewById(R.id.vkNick)).setText(link);

                                    }else
                                        ((TextView) socialPopup.findViewById(R.id.vkNick)).setText(socialModel.getVkLink());
                                }catch (Exception e) {*/
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
                                contactService.addNoteToContact(contact.getIdContact(), contact.getSocialModel().getYoutubeLink(), contact.getName());
                                ContactCacheService.updateContact(contact, mainView.getContext());
                                // EventBus.getDefault().post(new UpdateFile());
                                popupEditSocial.setVisibility(View.GONE);
                                ((TextView) popupProfileEditPreviewSocial.findViewById(R.id.valueUpd)).setText(contact.getSocialModel().getYoutubeLink());
                            } else {
                                if (socialModel.getYoutubeLink() != null && !socialModel.getYoutubeLink().isEmpty()) {
                                    contactService.deleteNoteContact(contact.getIdContact(), socialModel.getYoutubeLink());
                                }
                                contact.getSocialModel().setYoutubeLink(null);
                                //     ((ImageView) socialPopup.findViewById(R.id.vk_icon)).setImageResource(R.drawable.icn_social_vk_gray);
                                //     ((ImageView) getActivity().findViewById(R.id.vk_icon)).setImageResource(R.drawable.icn_social_vk_gray);
                                Drawable color2 = new ColorDrawable(Color.parseColor("#e2e5e8"));
                                Drawable image2 = getResources().getDrawable(R.drawable.ic_youtube_white);
                                LayerDrawable ld2 = new LayerDrawable(new Drawable[]{color2, image2});
                                ((ImageView) socialPopup.findViewById(R.id.youtube_icon)).setImageDrawable(ld2);

                                //((ImageView) getActivity().findViewById(R.id.vk_icon)).setImageDrawable(ld2);


                                ((TextView) socialPopup.findViewById(R.id.youtubeNick)).setText("add username or link");
                                contact.hasYoutube = false;
                                contact.setSocialModel(socialModel);
                                realm.commitTransaction();
                                ContactCacheService.updateContact(contact, mainView.getContext());
                                //  EventBus.getDefault().post(new UpdateFile());
                                popupEditSocial.setVisibility(View.GONE);
                                popupProfileEditPreviewSocial.setVisibility(View.GONE);
                            }
                            //EventBus.getDefault().post(new UpdateFile());

                        }

                        realm.close();

                        initIconColor(createdContact, mainView);
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
                        if (text.length() > 0 && text.charAt(0) == '@') text = text.substring(1);
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
                initIconColor(createdContact, mainView);
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
                                    contactService.deleteNoteContact(contact.getIdContact(), socialModel.getFacebookLink());
                                }
                                realm.beginTransaction();
                                contact.getSocialModel().setFacebookLink(null);
                                realm.commitTransaction();
                                Drawable color = new ColorDrawable(Color.parseColor("#e2e5e8"));
                                Drawable image = getResources().getDrawable(R.drawable.icn_social_facebook2);
                                LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
                                ((ImageView) socialPopup.findViewById(R.id.facebook_icon)).setImageDrawable(ld);



                                /*if(profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE){
                                    ((ImageView) profilePopUp.findViewById(R.id.facebook_icon)).setImageDrawable(ld);
                                }else if(companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE){
                                    ((ImageView) companyProfilePopup.findViewById(R.id.facebook_icon)).setImageDrawable(ld);
                                }else{*/
                                ((ImageView) getActivity().findViewById(R.id.facebook_icon)).setImageDrawable(ld);
                                //}


                                ((TextView) socialPopup.findViewById(R.id.facebook_nick)).setText("add username or link");
                                realm.beginTransaction();
                                contact.hasFacebook = false;
                                contact.setSocialModel(socialModel);
                                realm.commitTransaction();

                                ContactCacheService.updateContact(contact, mainView.getContext());
                                // EventBus.getDefault().post(new UpdateFile());


                            } else if (typeEnum.equals(SocialEnums.VK)) {
                                if (socialModel.getVkLink() != null && !socialModel.getVkLink().isEmpty()) {
                                    contactService.deleteNoteContact(contact.getIdContact(), socialModel.getVkLink());
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


                                /*if(profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE){
                                    ((ImageView) profilePopUp.findViewById(R.id.vk_icon)).setImageDrawable(ld2);
                                }else if(companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE){
                                    ((ImageView) companyProfilePopup.findViewById(R.id.vk_icon)).setImageDrawable(ld2);
                                }else{*/
                                ((ImageView) getActivity().findViewById(R.id.vk_icon)).setImageDrawable(ld2);
                                //}


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
                                    contactService.deleteNoteContact(contact.getIdContact(), socialModel.getInstagramLink());
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

                                /*if(profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE){
                                    ((ImageView) profilePopUp.findViewById(R.id.inst_icon)).setImageDrawable(ld4);
                                }else if(companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE){
                                    ((ImageView) companyProfilePopup.findViewById(R.id.inst_icon)).setImageDrawable(ld4);
                                }else{*/
                                ((ImageView) getActivity().findViewById(R.id.inst_icon)).setImageDrawable(ld4);
                                //}


                                ((TextView) socialPopup.findViewById(R.id.instagramNick)).setText("add username or link");
                                realm.beginTransaction();
                                contact.hasInst = false;
                                contact.setSocialModel(socialModel);
                                realm.commitTransaction();

                                ContactCacheService.updateContact(contact, mainView.getContext());
                                //    EventBus.getDefault().post(new UpdateFile());

                                popupProfileEditPreviewSocial.setVisibility(View.GONE);

                            } else if (typeEnum.equals(SocialEnums.MEDIUM)) {
                                if (socialModel.getMediumLink() != null && !socialModel.getMediumLink().isEmpty()) {
                                    contactService.deleteNoteContact(contact.getIdContact(), socialModel.getMediumLink());
                                }
                                realm.beginTransaction();
                                socialModel.setMediumLink(null);
                                realm.commitTransaction();
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

                                /*if(profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE){
                                    ((ImageView) profilePopUp.findViewById(R.id.inst_icon)).setImageDrawable(ld4);
                                }else if(companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE){
                                    ((ImageView) companyProfilePopup.findViewById(R.id.inst_icon)).setImageDrawable(ld4);
                                }else{*/
                                ((ImageView) getActivity().findViewById(R.id.medium_icon)).setImageDrawable(ld4);
                                //}


                                ((TextView) socialPopup.findViewById(R.id.mediumNick)).setText("add username or link");
                                realm.beginTransaction();
                                contact.hasMedium = false;
                                contact.setSocialModel(socialModel);
                                realm.commitTransaction();

                                ContactCacheService.updateContact(contact, mainView.getContext());
                                //    EventBus.getDefault().post(new UpdateFile());

                                popupProfileEditPreviewSocial.setVisibility(View.GONE);

                            } else if (typeEnum.equals(SocialEnums.LINKEDIN)) {
                                if (socialModel.getLinkedInLink() != null && !socialModel.getLinkedInLink().isEmpty()) {
                                    contactService.deleteNoteContact(contact.getIdContact(), socialModel.getLinkedInLink());
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

                               /* if(profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE){
                                    ((ImageView) profilePopUp.findViewById(R.id.link_icon)).setImageDrawable(ld3);
                                }else if(companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE){
                                    ((ImageView) companyProfilePopup.findViewById(R.id.link_icon)).setImageDrawable(ld3);
                                }else{*/
                                ((ImageView) getActivity().findViewById(R.id.link_icon)).setImageDrawable(ld3);
                                //}

                                ((TextView) socialPopup.findViewById(R.id.link_text)).setText("add username or link");
                                realm.beginTransaction();
                                contact.hasLinked = false;
                                contact.setSocialModel(socialModel);
                                realm.commitTransaction();
                                ContactCacheService.updateContact(contact, mainView.getContext());
                                //  EventBus.getDefault().post(new UpdateFile());


                                popupProfileEditPreviewSocial.setVisibility(View.GONE);
                            } else if (typeEnum.equals(SocialEnums.VIBER)) {
                                if (socialModel.getViberLink() != null && !socialModel.getViberLink().isEmpty()) {
                                    contactService.deleteNoteContact(contact.getIdContact(), socialModel.getViberLink());
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

                                /*if(profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE){
                                    ((CircleImageView) profilePopUp.findViewById(R.id.viber_icon)).setImageDrawable(ld5);
                                }else if(companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE){
                                    ((CircleImageView) companyProfilePopup.findViewById(R.id.viber_icon)).setImageDrawable(ld5);
                                }else{*/
                                ((CircleImageView) getActivity().findViewById(R.id.viber_icon)).setImageDrawable(ld5);
                                //}

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
                                    contactService.deleteNoteContact(contact.getIdContact(), socialModel.getWhatsappLink());
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

                                /*if(profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE){
                                    ((CircleImageView) profilePopUp.findViewById(R.id.whatsapp_icon)).setImageDrawable(ld6);
                                }else if(companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE){
                                    ((CircleImageView) companyProfilePopup.findViewById(R.id.whatsapp_icon)).setImageDrawable(ld6);
                                }else{*/
                                ((CircleImageView) getActivity().findViewById(R.id.whatsapp_icon)).setImageDrawable(ld6);
                                //}


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
                                    contactService.deleteNoteContact(contact.getIdContact(), socialModel.getTelegramLink());
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

                                /*if(profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE){
                                    ((CircleImageView) profilePopUp.findViewById(R.id.telegram_icon)).setImageDrawable(ld7);
                                }else if(companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE){
                                    ((CircleImageView) companyProfilePopup.findViewById(R.id.telegram_icon)).setImageDrawable(ld7);
                                }else{*/
                                ((CircleImageView) getActivity().findViewById(R.id.telegram_icon)).setImageDrawable(ld7);
                                //}

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
                                    contactService.deleteNoteContact(contact.getIdContact(), socialModel.getSkypeLink());
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

                                /*if(profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE){
                                    ((CircleImageView) profilePopUp.findViewById(R.id.skype_icon)).setImageDrawable(ld8);
                                }else if(companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE){
                                    ((CircleImageView) companyProfilePopup.findViewById(R.id.skype_icon)).setImageDrawable(ld8);
                                }else{*/
                                ((CircleImageView) getActivity().findViewById(R.id.skype_icon)).setImageDrawable(ld8);
                                //}

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
                                    contactService.deleteNoteContact(contact.getIdContact(), socialModel.getTwitterLink());
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


                                /*if(profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE){
                                    ((ImageView) profilePopUp.findViewById(R.id.vk_icon)).setImageDrawable(ld2);
                                }else if(companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE){
                                    ((ImageView) companyProfilePopup.findViewById(R.id.vk_icon)).setImageDrawable(ld2);
                                }else{*/
                                //((ImageView) getActivity().findViewById(R.id.vk_icon)).setImageDrawable(ld2);
                                //}


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
                                    contactService.deleteNoteContact(contact.getIdContact(), socialModel.getYoutubeLink());
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


                                /*if(profilePopUp != null && profilePopUp.getVisibility() == View.VISIBLE){
                                    ((ImageView) profilePopUp.findViewById(R.id.vk_icon)).setImageDrawable(ld2);
                                }else if(companyProfilePopup != null && companyProfilePopup.getVisibility() == View.VISIBLE){
                                    ((ImageView) companyProfilePopup.findViewById(R.id.vk_icon)).setImageDrawable(ld2);
                                }else{*/
                                //((ImageView) getActivity().findViewById(R.id.vk_icon)).setImageDrawable(ld2);
                                //}


                                ((TextView) socialPopup.findViewById(R.id.youtubeNick)).setText("add username or link");
                                realm.beginTransaction();
                                contact.hasYoutube = false;
                                contact.setSocialModel(socialModel);
                                realm.commitTransaction();
                                ContactCacheService.updateContact(contact, mainView.getContext());
                                //  EventBus.getDefault().post(new UpdateFile());
                                popupProfileEditPreviewSocial.setVisibility(View.GONE);

                            }

                            realm.close();

                            //EventBus.getDefault().post(new UpdateFile());
                            popupProfileEditPreviewSocial.setVisibility(View.GONE);
                        })
                        .setNegativeButton("No", (dialog, id) -> dialog.cancel());

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

    }


    @Override
    public void onDestroy() {
        if (!ClibpboardAdapter.checkUpdateClips) {
            System.out.println("CHECK CLIPS 2");
            if (getActivity() == null)
                ((TextView) activityApp.findViewById(R.id.updateContactClipboard)).setTextColor(getResources().getColor(R.color.gray));
            else
                ((TextView) getActivity().findViewById(R.id.updateContactClipboard)).setTextColor(getResources().getColor(R.color.gray));
        }

        if (hideSelect)
            frameSelectBar.setVisibility(View.VISIBLE);

        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try{
            getActivity().findViewById(R.id.popupEditMain).setVisibility(View.GONE);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            activityApp = (Activity) context;
        }
    }
}
