package ai.extime.Adapters;

import android.Manifest;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberToCarrierMapper;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import org.greenrobot.eventbus.EventBus;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import ai.extime.Activity.MainActivity;
import ai.extime.Events.RemoveCompanyFromListAdapter;
import ai.extime.Events.UpdateFile;
import ai.extime.Fragments.CompanyProfileDataFragment;
import ai.extime.Fragments.ContactsFragment;
import ai.extime.Fragments.CreateFragment;
import ai.extime.Fragments.ProfileFragment;
import ai.extime.Interfaces.Postman;
import ai.extime.Models.Contact;
import ai.extime.Models.HashTag;
import ai.extime.Services.ContactCacheService;
import ai.extime.Services.ContactsService;
import ai.extime.Utils.ClipboardType;
import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import ai.extime.Models.ContactInfo;

import com.extime.R;

public class CompanyAdapter extends RecyclerView.Adapter<CompanyAdapter.CompanyViewHolder> {

    private View mainView;

    private RealmList<Contact> listOfContacts;

    private RealmList<Contact> listOfContactsOnly;

    private Fragment parentFragment;
    private static Integer PERMISSION_REQUEST_CONTACT_WRITE = 48;

    private boolean sortTimeAsc = false;

    private ContactsService contactsService;

    private Context context;

    public static boolean selectionModeCompany = false;
    public ArrayList<Contact> selectedContacts;
    public ArrayList<Contact> savedSelectedContacts;
    public int countSelectContact;
    public static boolean updateContactAdapter = false;
    public static boolean clickmerge = false;

    public static boolean mergeCompanyAdapter = false;

    public static boolean removeCompany = false;

    //private RealmConfiguration contextRealm;

    private Toolbar toolbarC;

    private Contact company;

    ArrayList<ContactInfo> listOfInfo;

    int countInfo = 0;

    int countMainData = 1;

    //private Realm realm;


    public void setListContacts(RealmList<Contact> listContacts) {
        this.listOfContacts = listContacts;
    }

    public void sortByAsc() {
        SharedPreferences.Editor editor = ((CompanyProfileDataFragment) parentFragment).mPref.edit();
        editor.putString("typeSortCompany", "sortByAsc");
        editor.apply();
        Realm realm = Realm.getDefaultInstance(); //+
        try {

            realm.beginTransaction();
            Collections.sort(listOfContactsOnly, (contactFirst, contactSecond) -> contactFirst.getName().compareToIgnoreCase(contactSecond.getName()));
            realm.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
        }


        realm.close();
        updateNEw();
        notifyDataSetChanged();
    }

    public void updateNEw() {
        countInfo = 0;
        countMainData = 1;
        this.listOfContacts = new RealmList<>();


        this.listOfContacts.add(0, null);

        if (company.webSite != null && !company.webSite.isEmpty()) {
            this.listOfContacts.add(0, null);
            countMainData++;
        }

        this.listOfInfo = new ArrayList<>();
        if (company.getListOfContactInfo() != null) {
            for (ContactInfo ci : company.getListOfContactInfo()) {
                if (ClipboardType.isWeb(ci.value)) {
                    this.listOfContacts.add(0, null);
                    this.listOfInfo.add(ci);
                    countInfo++;
                } else if (ClipboardType.isEmail(ci.value)) {
                    this.listOfContacts.add(0, null);
                    this.listOfInfo.add(ci);
                    countInfo++;
                }

            }
        }

        //this.listOfContacts.addAll(listOfContacts);

        listOfContacts.addAll(listOfContactsOnly);
    }

    public void sortByDesc() {
        SharedPreferences.Editor editor = ((CompanyProfileDataFragment) parentFragment).mPref.edit();
        editor.putString("typeSortCompany", "sortByDesc");
        editor.apply();
        Realm realm = Realm.getDefaultInstance(); //+
        realm.beginTransaction();
        Collections.sort(listOfContactsOnly, (contactFirst, contactSecond) -> contactSecond.getName().compareToIgnoreCase(contactFirst.getName()));
        realm.commitTransaction();
        realm.close();
        updateNEw();
        notifyDataSetChanged();
    }

    public void sortByTimeAsc() {
        sortTimeAsc = false;
        SharedPreferences.Editor editor = ((CompanyProfileDataFragment) parentFragment).mPref.edit();
        editor.putString("typeSortCompany", "sortByTimeAsc");
        editor.apply();
        Realm realm = Realm.getDefaultInstance(); //+
        realm.beginTransaction();
        Collections.sort(listOfContactsOnly, new Comparator<Contact>() {
            @Override
            public int compare(Contact o1, Contact o2) {

                return o1.getDateCreate().compareTo(o2.getDateCreate());
            }
        });
        realm.commitTransaction();
        realm.close();
        updateNEw();
        notifyDataSetChanged();
    }


    public void sortByTimeDesc() {
        sortTimeAsc = true;
        SharedPreferences.Editor editor = ((CompanyProfileDataFragment) parentFragment).mPref.edit();
        editor.putString("typeSortCompany", "sortByTimeDesc");
        editor.apply();
        Realm realm = Realm.getDefaultInstance(); //+
        realm.beginTransaction();
        Collections.sort(listOfContactsOnly, new Comparator<Contact>() {
            @Override
            public int compare(Contact o1, Contact o2) {
                return o2.getDateCreate().compareTo(o1.getDateCreate());
            }
        });
        realm.commitTransaction();
        realm.close();
        updateNEw();
        notifyDataSetChanged();
    }

    public CompanyAdapter(Context context, RealmList<Contact> listOfContacts, Fragment parentFragment, ContactsService contactsService, Toolbar toolbar, Contact company) {
        this.context = context;
        this.listOfContacts = new RealmList<>();

       /* this.listOfContacts.add(0,null);
        this.listOfContacts.add(0,null);
        this.listOfContacts.add(0,null);*/


        this.listOfContactsOnly = listOfContacts;

        //this.listOfContacts = listOfContacts;
        this.parentFragment = parentFragment;
        this.contactsService = contactsService;
        this.selectedContacts = new ArrayList<>();
        this.toolbarC = toolbar;

        this.company = company;

       /* if(company.getShortInto() != null && !company.getShortInto().isEmpty()){
            this.listOfContacts.add(0,null);
            countMainData++;
        }*/

        this.listOfContacts.add(0, null);

        if (company.webSite != null && !company.webSite.isEmpty()) {
            this.listOfContacts.add(0, null);
            countMainData++;
        }

        this.listOfInfo = new ArrayList<>();
        if (company.getListOfContactInfo() != null) {
            for (ContactInfo ci : company.getListOfContactInfo()) {
                if (ClipboardType.isWeb(ci.value)) {
                    this.listOfContacts.add(0, null);
                    this.listOfInfo.add(ci);
                    countInfo++;
                } else if (ClipboardType.isEmail(ci.value)) {
                    this.listOfContacts.add(0, null);
                    this.listOfInfo.add(ci);
                    countInfo++;
                }

            }
        }

        this.listOfContacts.addAll(listOfContacts);
    }


    static class CompanyViewHolder extends RecyclerView.ViewHolder {

        ImageView companyPhoto;
        CircleImageView contactPhoto;
        TextView initials;
        TextView conatactName;
        TextView companyText;
        TextView cardCompanyName;
        View card;
        //LinearLayout timeBlock;
        LinearLayout containerHashTagsCompany;
        ImageView imageSelect;
        LinearLayout mainLayout;


        FrameLayout frameDataContact;
        LinearLayout frameOld;

        ImageView type_image;
        TextView hashtag_text, value1, textAddInfo;

        LinearLayout linearInfo;
        FrameLayout frameCircleList;


        LinearLayout linearInfoIntro;

        TextView value1Intro;

        CompanyViewHolder(View itemView) {
            super(itemView);
            card = itemView;
            companyPhoto = (ImageView) itemView.findViewById(R.id.companyPhoto);
            contactPhoto = (CircleImageView) itemView.findViewById(R.id.contactCircleColor);
            initials = (TextView) itemView.findViewById(R.id.contactInitials);
            conatactName = (TextView) itemView.findViewById(R.id.contactName);
            companyText = (TextView) itemView.findViewById(R.id.companyText);
            cardCompanyName = (TextView) itemView.findViewById(R.id.cardCompanyName);
            //timeBlock = (LinearLayout) itemView.findViewById(R.id.timeBlock);
            containerHashTagsCompany = (LinearLayout) itemView.findViewById(R.id.containerHashTagsCompany);
            imageSelect = (ImageView) itemView.findViewById(R.id.imageSelect);
            mainLayout = itemView.findViewById(R.id.card_company_main_layout);


            frameDataContact = itemView.findViewById(R.id.frameDataContact);
            frameOld = itemView.findViewById(R.id.frameOld);

            type_image = itemView.findViewById(R.id.type_image);
            hashtag_text = itemView.findViewById(R.id.hashtag_text);
            value1 = itemView.findViewById(R.id.value1);

            textAddInfo = itemView.findViewById(R.id.textAddInfo);

            linearInfo = itemView.findViewById(R.id.linearInfo);
            frameCircleList = itemView.findViewById(R.id.frameCircleList);

            linearInfoIntro = itemView.findViewById(R.id.linearInfoIntro);

            value1Intro = itemView.findViewById(R.id.value1Intro);
        }

    }

    @Override
    public CompanyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        mainView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_contact_company, viewGroup, false);


        if (savedSelectedContacts != null && !savedSelectedContacts.isEmpty()) {
            selectedContacts.clear();
            selectedContacts.addAll(savedSelectedContacts);
            savedSelectedContacts = null;

            toolbarC.findViewById(R.id.toolbar_title).setVisibility(View.GONE);
            ((TextView) toolbarC.findViewById(R.id.cancel_toolbar)).setVisibility(View.VISIBLE);
            ((TextView) toolbarC.findViewById(R.id.cancel_toolbar)).setTextSize(18);
            ((TextView) toolbarC.findViewById(R.id.cancel_toolbar)).setText("" + selectedContacts.size() + "");

            startSelectionMode();
        }

        return new CompanyViewHolder(mainView);
    }

    private void setMargins(View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }

    public static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(CompanyViewHolder holder, int position) {

        holder.mainLayout.setOnClickListener(v -> {

        });

        if (position >= countInfo + countMainData) {

            holder.frameDataContact.setVisibility(View.GONE);
            holder.frameOld.setVisibility(View.VISIBLE);

            Contact contact = listOfContacts.get(position);
            String initials = getInitials(contact);
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
            holder.companyPhoto.setVisibility(View.GONE);
            //holder.timeBlock.setVisibility(View.GONE);

            holder.conatactName.setText(contact.getName());
            holder.companyText.setVisibility(View.GONE);

            holder.imageSelect.setVisibility(View.GONE);

            holder.cardCompanyName.setText(contact.getCompanyPossition());

            if (contact.getCompanyPossition() != null && !contact.getCompanyPossition().isEmpty()) {
                setMargins(holder.conatactName, 0, 0, 0, 0);
            } else {
                if (contact.getListOfHashtags() == null || contact.getListOfHashtags().isEmpty())
                    setMargins(holder.conatactName, 0, 52, 0, 0);
                else
                    setMargins(holder.conatactName, 0, 0, 0, 0);
            }


            try {

                holder.contactPhoto.setImageURI(Uri.parse(contact.getPhotoURL()));
                holder.initials.setVisibility(View.GONE);

                if (((BitmapDrawable) holder.contactPhoto.getDrawable()).getBitmap() == null) {
                    GradientDrawable circle = (GradientDrawable) ContextCompat.getDrawable(context, R.drawable.blue_circle).mutate();
                    circle.setColor(contact.color);
                    holder.contactPhoto.setVisibility(View.VISIBLE);
                    holder.contactPhoto.setBackground(circle);
                    holder.contactPhoto.setImageDrawable(null);
                    holder.initials.setVisibility(View.VISIBLE);
                    holder.initials.setText(initials);
                }
            } catch (Exception e) {
                GradientDrawable circle = (GradientDrawable) ContextCompat.getDrawable(context, R.drawable.blue_circle).mutate();
                circle.setColor(contact.color);
                holder.contactPhoto.setVisibility(View.VISIBLE);
                holder.contactPhoto.setBackground(circle);
                holder.contactPhoto.setImageDrawable(null);
                holder.initials.setVisibility(View.VISIBLE);
                holder.initials.setText(initials);
            }


            //if (contact.getCompanyPossition() == null ) holder.cardCompanyName.setVisibility(View.GONE);


            if (holder.containerHashTagsCompany.getChildCount() > 0)
                holder.containerHashTagsCompany.removeAllViews();
            for (HashTag hashTag : contact.getListOfHashtags()) {
                TextView text = new TextView(mainView.getContext());
                text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                DisplayMetrics metrics = mainView.getContext().getResources().getDisplayMetrics();
                text.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                text.setText(hashTag.getHashTagValue() + " ");
                text.setTextColor(Color.parseColor("#87aade"));
                holder.containerHashTagsCompany.addView(text);

            }


            if (!ContactAdapter.selectionModeEnabled) {
                if (!selectionModeCompany) {

                    holder.containerHashTagsCompany.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (holder.frameOld.getVisibility() == View.VISIBLE) {
                                if (!selectionModeCompany) {
                                    if (((CompanyProfileDataFragment) parentFragment).getQuantityOpenedViews() == 0) {
                                        //contact.fillData(context, contactsService, FillDataEnums.PREVIEW);
                                        ((CompanyProfileDataFragment) parentFragment).showProfilePopUp(contact);
                                        holder.itemView.setBackgroundColor(Color.parseColor("#E3E3E3"));
                                    } else {
                                        ((CompanyProfileDataFragment) parentFragment).closeOtherPopup();
                                        notifyDataSetChanged();
                                    }
                                } else {
                                    holder.card.callOnClick();
                                }
                            }
                        }
                    });

                    holder.containerHashTagsCompany.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            if (holder.frameOld.getVisibility() == View.VISIBLE) {
                                if (!selectionModeCompany) {

                                    startSelectionMode();
                                    selectedContacts.add(contact);

                                    toolbarC.findViewById(R.id.toolbar_title).setVisibility(View.GONE);


                                    ((TextView) toolbarC.findViewById(R.id.cancel_toolbar)).setVisibility(View.VISIBLE);
                                    ((TextView) toolbarC.findViewById(R.id.cancel_toolbar)).setTextSize(18);
                                    ((TextView) toolbarC.findViewById(R.id.cancel_toolbar)).setText("" + selectedContacts.size() + "");


                                    //    checkMergeCount();
                                    //    changeContactsCountBar();

                                    //    startNewSelection();
                                }
                            }
                            return false;
                        }
                    });

                    holder.card.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            if (holder.frameOld.getVisibility() == View.VISIBLE) {
                                if (!selectionModeCompany) {

                                    startSelectionMode();
                                    selectedContacts.add(contact);

                                    toolbarC.findViewById(R.id.toolbar_title).setVisibility(View.GONE);


                                    ((TextView) toolbarC.findViewById(R.id.cancel_toolbar)).setVisibility(View.VISIBLE);
                                    ((TextView) toolbarC.findViewById(R.id.cancel_toolbar)).setTextSize(18);
                                    ((TextView) toolbarC.findViewById(R.id.cancel_toolbar)).setText("" + selectedContacts.size() + "");


                                    //    checkMergeCount();
                                    //    changeContactsCountBar();

                                    //    startNewSelection();
                                }
                            }
                            return false;
                        }
                    });

                    holder.contactPhoto.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            if (holder.frameOld.getVisibility() == View.VISIBLE) {

                                startSelectionMode();
                                selectedContacts.add(contact);

                                toolbarC.findViewById(R.id.toolbar_title).setVisibility(View.GONE);


                                ((TextView) toolbarC.findViewById(R.id.cancel_toolbar)).setVisibility(View.VISIBLE);
                                ((TextView) toolbarC.findViewById(R.id.cancel_toolbar)).setTextSize(18);
                                ((TextView) toolbarC.findViewById(R.id.cancel_toolbar)).setText("" + selectedContacts.size() + "");
                            }
                            return false;
                        }
                    });


                    holder.card.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (holder.frameOld.getVisibility() == View.VISIBLE) {

                                if (((CompanyProfileDataFragment) parentFragment).getQuantityOpenedViews() == 0) {
                                    //contact.fillData(context, contactsService, FillDataEnums.PREVIEW);
                                    ((CompanyProfileDataFragment) parentFragment).showProfilePopUp(contact);
                                    holder.itemView.setBackgroundColor(Color.parseColor("#E3E3E3"));
                                } else {
                                    ((CompanyProfileDataFragment) parentFragment).closeOtherPopup();
                                    notifyDataSetChanged();
                                }
                            }
                        }
                    });

                    holder.contactPhoto.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (holder.frameOld.getVisibility() == View.VISIBLE) {
                                //contact.fillData(context, contactsService, FillDataEnums.PROFILE);
                                android.support.v4.app.FragmentManager fragmentManager = parentFragment.getActivity().getSupportFragmentManager();
                                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.main_content, ProfileFragment.newInstance(contact, false)).addToBackStack("company").commit();

                                ((CompanyProfileDataFragment) parentFragment).closeOtherPopup();
                            }
                        }
                    });


                } else {

                    holder.containerHashTagsCompany.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            if (holder.frameOld.getVisibility() == View.VISIBLE) {
                                if (!selectionModeCompany) {

                                    startSelectionMode();
                                    selectedContacts.add(contact);

                                    toolbarC.findViewById(R.id.toolbar_title).setVisibility(View.GONE);


                                    ((TextView) toolbarC.findViewById(R.id.cancel_toolbar)).setVisibility(View.VISIBLE);
                                    ((TextView) toolbarC.findViewById(R.id.cancel_toolbar)).setTextSize(18);
                                    ((TextView) toolbarC.findViewById(R.id.cancel_toolbar)).setText("" + selectedContacts.size() + "");


                                    //    checkMergeCount();
                                    //    changeContactsCountBar();

                                    //    startNewSelection();
                                }
                            }
                            return false;
                        }
                    });


                    holder.card.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (holder.frameOld.getVisibility() == View.VISIBLE) {
                                if (!selectedContacts.contains(contact)) {
                                    selectedContacts.add(contact);
                                    countSelectContact++;
                                    updateCountSelected();
                                } else {
                                    selectedContacts.remove(contact);
                                    countSelectContact--;
                                    updateCountSelected();
                                    if (selectedContacts.isEmpty())
                                        stopSelectionMode();
                                }
                                notifyDataSetChanged();
                            }
                        }
                    });


                    holder.contactPhoto.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (holder.frameOld.getVisibility() == View.VISIBLE) {
                                if (((CompanyProfileDataFragment) parentFragment).getQuantityOpenedViews() == 0) {
                                    //contact.fillData(context, contactsService, FillDataEnums.PREVIEW);
                                    ((CompanyProfileDataFragment) parentFragment).showProfilePopUp(contact);
                                    //    holder.itemView.setBackgroundColor(Color.parseColor("#E3E3E3"));
                                } else {
                                    ((CompanyProfileDataFragment) parentFragment).closeOtherPopup();
                                    notifyDataSetChanged();
                                }
                            }
                        }
                    });


                    if (selectedContacts.contains(contact)) {
                        holder.itemView.setBackgroundResource(R.drawable.selected_card_bg);
                        ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) holder.imageSelect.getLayoutParams();
                        p.setMargins(0, 0, 39, 36);

                        holder.imageSelect.setLayoutParams(p);
                        holder.imageSelect.setVisibility(View.VISIBLE);
                    }

                }
            } else {
                holder.card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (holder.frameOld.getVisibility() == View.VISIBLE) {

                            if (((CompanyProfileDataFragment) parentFragment).getQuantityOpenedViews() == 0) {
                                //contact.fillData(context, contactsService, FillDataEnums.PREVIEW);
                                ((CompanyProfileDataFragment) parentFragment).showProfilePopUp(contact);
                                holder.itemView.setBackgroundColor(Color.parseColor("#E3E3E3"));
                            } else {
                                ((CompanyProfileDataFragment) parentFragment).closeOtherPopup();
                                notifyDataSetChanged();
                            }
                        }
                    }
                });

                holder.contactPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (holder.frameOld.getVisibility() == View.VISIBLE) {
                            //contact.fillData(context, contactsService, FillDataEnums.PROFILE);
                            android.support.v4.app.FragmentManager fragmentManager = parentFragment.getActivity().getSupportFragmentManager();
                            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.main_content, ProfileFragment.newInstance(contact, false)).addToBackStack("company").commit();

                            ((CompanyProfileDataFragment) parentFragment).closeOtherPopup();
                        }
                    }
                });
            }

        } else {

            holder.itemView.setBackgroundColor(Color.TRANSPARENT);

            holder.frameDataContact.setVisibility(View.VISIBLE);
            holder.frameOld.setVisibility(View.GONE);
            holder.linearInfoIntro.setVisibility(View.GONE);

            if (position == 0) {
                holder.type_image.setImageDrawable(context.getResources().getDrawable(R.drawable.intro_n));
                if (company.shortInto != null && !company.shortInto.isEmpty()) {

                    holder.linearInfo.setVisibility(View.GONE);

                    /*holder.hashtag_text.setText("Short intro");
                    holder.value1.setText(company.getShortInto());

                    holder.linearInfo.setVisibility(View.VISIBLE);
                    holder.textAddInfo.setVisibility(View.GONE);
                    holder.frameCircleList.setVisibility(View.GONE);*/

                    holder.hashtag_text.setText("intro");
                    holder.value1.setText(company.shortInto);
                    //holder.hashtag_text.setVisibility(View.GONE);


                    holder.linearInfoIntro.setVisibility(View.VISIBLE);
                    holder.value1Intro.setText(company.shortInto);

                    holder.textAddInfo.setVisibility(View.GONE);
                    holder.frameCircleList.setVisibility(View.GONE);
                } else {
                    holder.linearInfo.setVisibility(View.GONE);
                    holder.textAddInfo.setVisibility(View.VISIBLE);
                    holder.textAddInfo.setText("Add short intro");
                    holder.frameCircleList.setVisibility(View.VISIBLE);
                    holder.linearInfoIntro.setVisibility(View.GONE);
                }
            } else if (position == 1 && company.webSite != null && !company.webSite.isEmpty()) {
                holder.type_image.setImageDrawable(context.getResources().getDrawable(R.drawable.icn_popup_web_blue));
                if (company.webSite != null && !company.webSite.isEmpty()) {
                    holder.hashtag_text.setText("web");

                    String web_c = company.webSite;
                    if (web_c.contains("www.")) {
                        web_c = web_c.replace("www.", "");
                    }
                    if (web_c.contains("https://")) {
                        web_c = web_c.replace("https://", "");
                    }
                    if (web_c.contains("http://")) {
                        web_c = web_c.replace("http://", "");
                    }

                    holder.value1.setText(web_c);

                    holder.linearInfo.setVisibility(View.VISIBLE);
                    holder.textAddInfo.setVisibility(View.GONE);
                    holder.frameCircleList.setVisibility(View.GONE);

                } else {
                    holder.linearInfo.setVisibility(View.GONE);
                    holder.textAddInfo.setVisibility(View.VISIBLE);
                    holder.textAddInfo.setText("Add web");
                    holder.frameCircleList.setVisibility(View.VISIBLE);
                }

            } else {
                try {
                    if (listOfInfo != null && listOfInfo.size() != position - countMainData && listOfInfo.get(position - countMainData) != null) {
                        if (ClipboardType.isWeb(listOfInfo.get(position - countMainData).value)) {
                            holder.hashtag_text.setText("web");

                            String web_c = listOfInfo.get(position - countMainData).value;
                            if (web_c.contains("www.")) {
                                web_c = web_c.replace("www.", "");
                            }
                            if (web_c.contains("https://")) {
                                web_c = web_c.replace("https://", "");
                            }
                            if (web_c.contains("http://")) {
                                web_c = web_c.replace("http://", "");
                            }

                            holder.value1.setText(web_c);
                            holder.type_image.setImageDrawable(context.getResources().getDrawable(R.drawable.icn_popup_web_blue));
                            holder.linearInfo.setVisibility(View.VISIBLE);
                            holder.textAddInfo.setVisibility(View.GONE);
                            holder.frameCircleList.setVisibility(View.GONE);
                        } else if (ClipboardType.isEmail(listOfInfo.get(position - countMainData).value)) {

                            holder.type_image.setImageDrawable(context.getResources().getDrawable(R.drawable.icn_bottombar_emails_blue));

                            holder.hashtag_text.setText("email");

                            holder.value1.setText(listOfInfo.get(position - countMainData).value);

                            holder.linearInfo.setVisibility(View.VISIBLE);
                            holder.textAddInfo.setVisibility(View.GONE);
                            holder.frameCircleList.setVisibility(View.GONE);
                        }
                    }
                }catch (ArrayIndexOutOfBoundsException e){
                    e.printStackTrace();
                }
            }
            /*if(position == 2){
                holder.type_image.setImageDrawable(context.getResources().getDrawable(R.drawable.icn_bottombar_emails_blue));

                if(company.emailCompany != null && !company.emailCompany.isEmpty()) {

                    holder.hashtag_text.setText("email");

                    holder.value1.setText(company.emailCompany);

                    holder.linearInfo.setVisibility(View.VISIBLE);
                    holder.textAddInfo.setVisibility(View.GONE);
                    holder.frameCircleList.setVisibility(View.GONE);

                }else{
                    holder.linearInfo.setVisibility(View.GONE);
                    holder.textAddInfo.setVisibility(View.VISIBLE);
                    holder.textAddInfo.setText("Add email");
                    holder.frameCircleList.setVisibility(View.VISIBLE);
                }

            }*/


            holder.frameDataContact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (((CompanyProfileDataFragment) parentFragment).getQuantityOpenedViews() > 0) {
                        ((CompanyProfileDataFragment) parentFragment).closeOtherPopup();
                    } else {
                        if (holder.textAddInfo.getVisibility() == View.GONE) {

                            if (((CompanyProfileDataFragment) parentFragment).popupProfileEdit != null && ((CompanyProfileDataFragment) parentFragment).popupProfileEdit.getVisibility() != View.VISIBLE)
                                ((CompanyProfileDataFragment) parentFragment).showEditPopupPreview(((TextView) v.findViewById(R.id.value1)).getText().toString(), ((TextView) v.findViewById(R.id.hashtag_text)).getText().toString());
                        } else {
                            if (position == 0) {
                                ((CompanyProfileDataFragment) parentFragment).showPopupEditDataCompany("intro");
                            } else if (position == 1) {
                                ((CompanyProfileDataFragment) parentFragment).showPopupEditDataCompany("web");
                            } else if (position == 2) {
                                ((CompanyProfileDataFragment) parentFragment).showPopupEditDataCompany("email");
                            }
                        }
                    }
                }
            });


        }

    }

    @Override
    public int getItemCount() {
        if (listOfContacts == null) {
            listOfContacts = new RealmList<>();
        }
        return listOfContacts.size();
    }

    private String getInitials(Contact contact) {
        String initials = "";
        if (contact.getName() != null && !contact.getName().isEmpty()) {
            String names[] = contact.getName().split("\\s+");
            for (String namePart : names)
                initials += namePart.charAt(0);
        }
        return initials.toUpperCase();
    }

    public void updateCountSelected() {
        ((TextView) toolbarC.findViewById(R.id.cancel_toolbar)).setText("" + selectedContacts.size() + "");

        if (selectedContacts.size() < 2) {


            ((TextView) parentFragment.getActivity().findViewById(R.id.editTextSelectMenu)).setTextColor(mainView.getContext().getResources().getColor(R.color.gray));
            ImageView ivVectorImage = (ImageView) parentFragment.getActivity().findViewById(R.id.editIconSelect);
            ivVectorImage.setColorFilter(mainView.getContext().getResources().getColor(R.color.gray));


        } else {
            ((TextView) parentFragment.getActivity().findViewById(R.id.editTextSelectMenu)).setTextColor(mainView.getContext().getResources().getColor(R.color.colorPrimary));
            ImageView ivVectorImage = (ImageView) parentFragment.getActivity().findViewById(R.id.editIconSelect);
            ivVectorImage.setColorFilter(mainView.getContext().getResources().getColor(R.color.colorPrimary));
        }

    }

    public void stopSelectionMode() {

        ((TextView) toolbarC.findViewById(R.id.cancel_toolbar)).setTextSize(14);
        ((TextView) toolbarC.findViewById(R.id.cancel_toolbar)).setVisibility(View.GONE);


        ((TextView) parentFragment.getActivity().findViewById(R.id.mergeTxt)).setText("Merge");
        ((TextView) parentFragment.getActivity().findViewById(R.id.mergeTxt)).setTextColor(parentFragment.getActivity().getResources().getColor(R.color.gray));
        ((ImageView) parentFragment.getActivity().findViewById(R.id.mergeIcon)).setColorFilter(mainView.getContext().getResources().getColor(R.color.gray));
        ((ImageView) parentFragment.getActivity().findViewById(R.id.mergeIcon)).setImageDrawable(parentFragment.getActivity().getResources().getDrawable(R.drawable.icn_sort_merge));

        parentFragment.getActivity().findViewById(R.id.frame_select_bar).setVisibility(View.GONE);
        parentFragment.getActivity().findViewById(R.id.select_menu).setVisibility(View.GONE);
        selectedContacts = new ArrayList<>();

        //((Postman)((CompanyProfileDataFragment) parentFragment).activityApp).getMenu().getItem(1).setVisible(true);


        toolbarC.findViewById(R.id.toolbar_title).setVisibility(View.VISIBLE);

        ((TextView) toolbarC.findViewById(R.id.cancel_toolbar)).setText("CANCEL");

        selectionModeCompany = false;

        notifyDataSetChanged();

    }

    private void askWritePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            if (ContextCompat.checkSelfPermission(parentFragment.getActivity(), Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(parentFragment.getActivity(), new String[]{Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,}, PERMISSION_REQUEST_CONTACT_WRITE);
    }

    @SuppressLint("RestrictedApi")
    public void startSelectionMode() {

        /*parentFragment.getActivity().findViewById(R.id.frame_select_bar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/
        /*parentFragment.getActivity().findViewById(R.id.docButton).setVisibility(View.VISIBLE);


        parentFragment.getActivity().findViewById(R.id.docButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                mergedContacts = true;
                contactAdapter.mergeSelectedContactsToList();
            }
        });*/
        // if(selectionModeCompany) {

        ((TextView) parentFragment.getActivity().findViewById(R.id.mergeTxt)).setText("Merge");
        ((TextView) parentFragment.getActivity().findViewById(R.id.mergeTxt)).setTextColor(parentFragment.getActivity().getResources().getColor(R.color.gray));
        ImageView ivVectorImage = (ImageView) parentFragment.getActivity().findViewById(R.id.mergeIcon);
        ivVectorImage.setColorFilter(parentFragment.getActivity().getResources().getColor(R.color.gray));


        if (selectedContacts.size() == 0) {


            ((TextView) parentFragment.getActivity().findViewById(R.id.editTextSelectMenu)).setTextColor(parentFragment.getActivity().getResources().getColor(R.color.gray));
            ivVectorImage = (ImageView) parentFragment.getActivity().findViewById(R.id.editIconSelect);
            ivVectorImage.setColorFilter(parentFragment.getActivity().getResources().getColor(R.color.colorPrimary));


        } else {
            ((TextView) parentFragment.getActivity().findViewById(R.id.editTextSelectMenu)).setTextColor(parentFragment.getActivity().getResources().getColor(R.color.colorPrimary));
            ivVectorImage = (ImageView) parentFragment.getActivity().findViewById(R.id.editIconSelect);
            ivVectorImage.setColorFilter(parentFragment.getActivity().getResources().getColor(R.color.gray));
        }

     /*   ((TextView) parentFragment.getActivity().findViewById(R.id.editTextSelectMenu)).setTextColor(mainView.getContext().getResources().getColor(R.color.gray));
        ivVectorImage = (ImageView)parentFragment.getActivity().findViewById(R.id.editIconSelect);
        ivVectorImage.setColorFilter(mainView.getContext().getResources().getColor(R.color.gray));*/


        ((TextView) parentFragment.getActivity().findViewById(R.id.mergeTxt)).setText("Merge");
        /*parentFragment.getActivity().findViewById(R.id.mergeData).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                clickmerge = true;
                parentFragment.getActivity().findViewById(R.id.select_menu).setVisibility(View.GONE);
                android.support.v4.app.FragmentManager fragmentManager =  ((FragmentActivity)context).getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_content, CreateFragment.newInstance(selectedContacts)).addToBackStack("contacts").commit();

                ((TextView) MainActivity.mainToolBar.findViewById(R.id.cancel_toolbar)).setVisibility(View.GONE);
            }
        });*/

        parentFragment.getActivity().findViewById(R.id.share_contacts_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareSelectedContacts();
            }
        });

        parentFragment.getActivity().findViewById(R.id.back_contacts).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopSelectionMode();
            }
        });

        parentFragment.getActivity().findViewById(R.id.editSelectMenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //contact.fillData(contactsService);
                //((CompanyProfileDataFragment) parentFragment).showFastEditPopup(selectedContacts.get(0));
                if (selectedContacts != null && selectedContacts.size() > 1) {

                    //contactAd = this;

                    ContactAdapter.checkMerge = false;
                    ContactAdapter.checkMergeContacts = true;

                    //contactsFragment.closeOtherPopup();
                    ((CompanyProfileDataFragment) parentFragment).closeOtherPopup();

                    mergeCompanyAdapter = true;

                    savedSelectedContacts = new ArrayList<>();
                    savedSelectedContacts.addAll(selectedContacts);

                    android.support.v4.app.FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                    android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.main_content, CreateFragment.newInstance(selectedContacts)).addToBackStack("create").commit();

                    //contactsFragment.hideSelectMenu();

                    ((CompanyProfileDataFragment) parentFragment).getActivity().findViewById(R.id.frame_select_bar).setVisibility(View.GONE);
                    ((CompanyProfileDataFragment) parentFragment).getActivity().findViewById(R.id.select_menu).setVisibility(View.GONE);

                    ContactAdapter.checkFoActionIcon = true;

                }
            }
        });


        parentFragment.getActivity().findViewById(R.id.mergeData).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(parentFragment.getActivity(), Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                    askWritePermission();
                } else {
                    String text = "Do you want to remove contact from the company?";
                    if (selectedContacts.size() > 1)
                        text = "Do you want to remove contacts from the company?";

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            parentFragment.getActivity());
                    alertDialogBuilder.setTitle(text);
                    alertDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("Remove", (dialog, id) -> {



                                Realm realm = Realm.getDefaultInstance(); //+


                                if (ProfileFragment.selectedContact.listOfContacts.size() > selectedContacts.size()) {
                                    for (Contact contact : selectedContacts) {
                                        if (listOfContacts.contains(contact)) {
                                            realm.beginTransaction();
                                            listOfContacts.remove(contact);
                                            realm.commitTransaction();
                                        }
                                    }


                                   /* ArrayList<String> listEdit = new ArrayList<>();
                                    ArrayList<Boolean> listEditBool = new ArrayList<>();*/
                                    for (Contact contact : selectedContacts) {
                                        contactsService.updateCompany(contact.getIdContact(), contact.getCompany(), null);
                                        //listEdit.add(contact.getName());
                                        //listEditBool.add(true);
                                        realm.beginTransaction();
                                        contact.setCompany(null);
                                        realm.commitTransaction();
                                        ContactCacheService.removeContactFromCompany(ProfileFragment.selectedContact, contact);
                                        //contactsService.deleteContact(contact.getIdContact());


                                    }
                                   /* MainActivity.listToManyUpdateFile.add("EDIT");
                                    MainActivity.listToManyUpdateFile.add(listEdit);
                                    MainActivity.listToManyUpdateFile.add(listEditBool);*/

                                    //EventBus.getDefault().post(new UpdateFile());

                                    //((TextView) parentFragment.getActivity().findViewById(R.id.companyNumb)).setText(String.valueOf(listOfContacts.size()));

                                    if (company.listOfContacts.size() == 1) {
                                        ((TextView) parentFragment.getActivity().findViewById(R.id.count_contact_company_preview)).setText("1 contact");
                                    } else {
                                        ((TextView) parentFragment.getActivity().findViewById(R.id.count_contact_company_preview)).setText(String.valueOf(company.listOfContacts.size()) + " contacts");
                                    }

                                    ((CompanyProfileDataFragment) parentFragment).updateConatcts();

                                    stopSelectionMode();

                                    notifyDataSetChanged();

                                    updateContactAdapter = true;

                                } else if (ProfileFragment.selectedContact.listOfContacts.size() == selectedContacts.size()) {


                                   /* ArrayList<String> listEditDel = new ArrayList<>();
                                    ArrayList<Boolean> listEditBoolDel = new ArrayList<>();
                                    listEditDel.add(ProfileFragment.selectedContact.getName());
                                    listEditBoolDel.add(false);
                                    MainActivity.listToManyUpdateFile.add("Delete");
                                    MainActivity.listToManyUpdateFile.add(listEditDel);
                                    MainActivity.listToManyUpdateFile.add(listEditBoolDel);*/


                                    EventBus.getDefault().post(new RemoveCompanyFromListAdapter(ProfileFragment.selectedContact.getId()));
                                    /*ArrayList<String> listEdit = new ArrayList<>();
                                    ArrayList<Boolean> listEditBool = new ArrayList<>();*/
                                    for (Contact contact : selectedContacts) {
                                        contactsService.updateCompany(contact.getIdContact(), contact.getCompany(), null);
                                        //listEdit.add(contact.getName());
                                        //listEditBool.add(true);
                                        realm.beginTransaction();
                                        contact.setCompany(null);
                                        realm.commitTransaction();
                                        ContactCacheService.removeContactFromCompany(ProfileFragment.selectedContact, contact);
                                        //contactsService.deleteContact(contact.getIdContact());

                                    }
                                   /* MainActivity.listToManyUpdateFile.add("EDIT");
                                    MainActivity.listToManyUpdateFile.add(listEdit);
                                    MainActivity.listToManyUpdateFile.add(listEditBool);*/

                                    //EventBus.getDefault().post(new UpdateFile());

                                    removeCompany = true;
                                    selectionModeCompany = false;
                                    selectedContacts = new ArrayList<>();
                                    ContactsFragment.goneSelectMeny = true;

                                    android.support.v4.app.FragmentManager fragmentManager = parentFragment.getActivity().getSupportFragmentManager();
                                    fragmentManager.popBackStack();


                                }
                                realm.close();

                                Toast toast = Toast.makeText(mainView.getContext(), "Delete success", Toast.LENGTH_SHORT);
                                toast.show();
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //EventBus.getDefault().post(new UpdateFile());
                                    }
                                }).start();

                            })
                            .setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            }
        });

        parentFragment.getActivity().findViewById(R.id.delete_selected_contacts).setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(parentFragment.getActivity(), Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                askWritePermission();
            } else {
                String text = "Do you want to delete contact?";
                if (selectedContacts.size() > 1)
                    text = "Do you want to delete contacts?";

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        parentFragment.getActivity());
                alertDialogBuilder.setTitle(text);
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Delete", (dialog, id) -> {



                            Realm realm = Realm.getDefaultInstance(); //+


                            if (ProfileFragment.selectedContact.listOfContacts.size() > selectedContacts.size()) {

                                for (Contact contact : selectedContacts) {
                                    if (listOfContacts.contains(contact)) {
                                        realm.beginTransaction();
                                        listOfContacts.remove(contact);
                                        realm.commitTransaction();
                                    }
                                }



                                for (Contact contact : selectedContacts) {
                                    contactsService.updateCompany(contact.getIdContact(), contact.getCompany(), null);


                                    realm.beginTransaction();
                                    contact.setCompany(null);
                                    realm.commitTransaction();

                                    ContactCacheService.removeContactFromCompany(ProfileFragment.selectedContact, contact);
                                    contactsService.deleteContact(contact.getIdContact());
                                    EventBus.getDefault().post(new RemoveCompanyFromListAdapter(contact.getId()));
                                    ContactCacheService.removeContact(contact);


                                }


                                //EventBus.getDefault().post(new UpdateFile());

                                //((TextView) parentFragment.getActivity().findViewById(R.id.companyNumb)).setText(String.valueOf(listOfContacts.size()));

                                if (company.listOfContacts.size() == 1) {
                                    ((TextView) parentFragment.getActivity().findViewById(R.id.count_contact_company_preview)).setText("1 contact");
                                } else {
                                    ((TextView) parentFragment.getActivity().findViewById(R.id.count_contact_company_preview)).setText(String.valueOf(company.listOfContacts.size()) + " contacts");
                                }

                                ((CompanyProfileDataFragment) parentFragment).updateConatcts();

                                stopSelectionMode();

                                notifyDataSetChanged();

                                updateContactAdapter = true;

                            } else if (ProfileFragment.selectedContact.listOfContacts.size() == selectedContacts.size()) {




                                EventBus.getDefault().post(new RemoveCompanyFromListAdapter(ProfileFragment.selectedContact.getId()));
                               /* ArrayList<String> listEdit = new ArrayList<>();
                                ArrayList<Boolean> listEditBool = new ArrayList<>();*/
                                for (Contact contact : selectedContacts) {
                                    contactsService.updateCompany(contact.getIdContact(), contact.getCompany(), null);
                                    //listEdit.add(contact.getName());
                                    //listEditBool.add(true);
                                    realm.beginTransaction();
                                    contact.setCompany(null);
                                    realm.commitTransaction();
                                    ContactCacheService.removeContactFromCompany(ProfileFragment.selectedContact, contact);
                                    contactsService.deleteContact(contact.getIdContact());
                                    EventBus.getDefault().post(new RemoveCompanyFromListAdapter(contact.getId()));
                                    ContactCacheService.removeContact(contact);

                                }
                               /* MainActivity.listToManyUpdateFile.add("EDIT");
                                MainActivity.listToManyUpdateFile.add(listEdit);
                                MainActivity.listToManyUpdateFile.add(listEditBool);*/

                                //EventBus.getDefault().post(new UpdateFile());

                                removeCompany = true;
                                selectionModeCompany = false;
                                selectedContacts = new ArrayList<>();
                                ContactsFragment.goneSelectMeny = true;

                               /* android.support.v4.app.FragmentManager fragmentManager = parentFragment.getActivity().getSupportFragmentManager();
                                fragmentManager.popBackStack();*/


                                android.support.v4.app.FragmentManager fm = parentFragment.getActivity().getSupportFragmentManager();
                                List<Fragment> listF = fm.getFragments();


                                int c = 1;
                                for (int i = listF.size() - 1; i >= 0; i--) {
                                    try {
                                        if (!((ProfileFragment) listF.get(i)).getMainContact().isValid()) {
                                            c++;
                                            continue;

                                        }

                                    } catch (Exception e) {

                                    }
                                    break;
                                }
                                for (int q = 0; q < c; q++) {
                                    parentFragment.getActivity().getSupportFragmentManager().popBackStack();
                                }


                            }


                            realm.close();
                            Toast toast = Toast.makeText(mainView.getContext(), "Delete success", Toast.LENGTH_SHORT);
                            toast.show();

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    //EventBus.getDefault().post(new UpdateFile());
                                }
                            }).start();

                        })
                        .setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });


        toolbarC.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectionModeCompany)
                    stopSelectionMode();
                else {


                    android.support.v4.app.FragmentManager fm = parentFragment.getActivity().getSupportFragmentManager();
                    List<Fragment> listF = fm.getFragments();


                    int c = 1;
                    for (int i = listF.size() - 2; i >= 0; i--) {
                        try {
                            if (!((ProfileFragment) listF.get(i)).getMainContact().isValid()) {
                                c++;
                                continue;

                            }

                        } catch (Exception e) {

                        }
                        break;
                    }
                    for (int q = 0; q < c; q++) {
                        parentFragment.getActivity().getSupportFragmentManager().popBackStack();
                    }

                }

            }
        });


        // }


        countSelectContact = 1;

        ((TextView) parentFragment.getActivity().findViewById(R.id.mergeTxt)).setText("Quit");
        ((TextView) parentFragment.getActivity().findViewById(R.id.mergeTxt)).setTextColor(parentFragment.getActivity().getResources().getColor(R.color.colorPrimary));
        ((ImageView) parentFragment.getActivity().findViewById(R.id.mergeIcon)).setColorFilter(parentFragment.getActivity().getResources().getColor(R.color.colorPrimary));
        ((ImageView) parentFragment.getActivity().findViewById(R.id.mergeIcon)).setImageDrawable(parentFragment.getActivity().getResources().getDrawable(R.drawable.quit_company));

        ((TextView) parentFragment.getActivity().findViewById(R.id.editTextSelectMenu)).setText("Merge");
        ((TextView) parentFragment.getActivity().findViewById(R.id.editTextSelectMenu)).setTextColor(parentFragment.getActivity().getResources().getColor(R.color.colorPrimary));
        ((ImageView) parentFragment.getActivity().findViewById(R.id.editIconSelect)).setColorFilter(parentFragment.getActivity().getResources().getColor(R.color.colorPrimary));
        ((ImageView) parentFragment.getActivity().findViewById(R.id.editIconSelect)).setImageDrawable(parentFragment.getActivity().getResources().getDrawable(R.drawable.icn_sort_merge));

        if (selectedContacts.size() == 0) {


            ((TextView) parentFragment.getActivity().findViewById(R.id.editTextSelectMenu)).setTextColor(parentFragment.getActivity().getResources().getColor(R.color.gray));
            ivVectorImage = (ImageView) parentFragment.getActivity().findViewById(R.id.editIconSelect);
            ivVectorImage.setColorFilter(parentFragment.getActivity().getResources().getColor(R.color.gray));


        } else {
            ((TextView) parentFragment.getActivity().findViewById(R.id.editTextSelectMenu)).setTextColor(parentFragment.getActivity().getResources().getColor(R.color.colorPrimary));
            ivVectorImage = (ImageView) parentFragment.getActivity().findViewById(R.id.editIconSelect);
            ivVectorImage.setColorFilter(parentFragment.getActivity().getResources().getColor(R.color.colorPrimary));
        }

        parentFragment.getActivity().findViewById(R.id.select_menu).setVisibility(View.VISIBLE);
        ((Postman) ((CompanyProfileDataFragment) parentFragment).activityApp).getMenu().getItem(1).setVisible(false);


        parentFragment.getActivity().findViewById(R.id.editSelectMenu).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                TextView textView = ((TextView) parentFragment.getActivity().findViewById(R.id.editTextSelectMenu));
                //    ImageView imageView = ((ImageView) profilePopUp.findViewById(R.id.imagePreviewIcon));
                FrameLayout frameLayout = ((FrameLayout) parentFragment.getActivity().findViewById(R.id.editSelectMenu));
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


        parentFragment.getActivity().findViewById(R.id.mergeData).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                TextView textView = ((TextView) parentFragment.getActivity().findViewById(R.id.mergeTxt));
                //    ImageView imageView = ((ImageView) profilePopUp.findViewById(R.id.imagePreviewIcon));
                FrameLayout frameLayout = ((FrameLayout) parentFragment.getActivity().findViewById(R.id.mergeData));
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

        /*parentFragment.getActivity().findViewById(R.id.stop_selection_mode).setOnClickListener(v -> {
            parentFragment.getActivity().findViewById(R.id.frame_select_bar).setVisibility(View.GONE);
            parentFragment.getActivity().findViewById(R.id.select_menu).setVisibility(View.GONE);
            //mergedContacts = false;


            contactAdapter.stopSelectionMode();


        });*/

        /*ContactAdapter contactAdapter = (ContactAdapter) containerContacts.getAdapter();
        if (contactAdapter == null)
            return;*/



        ((CompanyProfileDataFragment) parentFragment).closeOtherPopup();
        selectionModeCompany = true;
        //selectedContacts = new ArrayList<>();
        try {
            notifyDataSetChanged();
        } catch (IllegalStateException e) {

        }

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

    public boolean checkClick = true;

    public void OnTouchMethod(TextView textview) {

        checkClick = false;

        int colorFrom;
        String s = parentFragment.getActivity().getResources().getResourceEntryName(textview.getId());


        if (s.equals("textDeleteSelectMenu") || s.equals("textShareSelect") || s.equals("textBackSelectMenu"))
            colorFrom = parentFragment.getActivity().getResources().getColor(R.color.colorPrimary);
        else if (s.equals("mergeTxt")) {
            if (selectedContacts.size() >= 2)
                colorFrom = parentFragment.getActivity().getResources().getColor(R.color.colorPrimary);
            else
                colorFrom = parentFragment.getActivity().getResources().getColor(R.color.gray);

        } else if (s.equals("editTextSelectMenu")) {
            if (selectedContacts.size() == 1)
                colorFrom = parentFragment.getActivity().getResources().getColor(R.color.colorPrimary);
            else
                colorFrom = parentFragment.getActivity().getResources().getColor(R.color.gray);
        } else
            colorFrom = parentFragment.getActivity().getResources().getColor(R.color.colorPrimaryDark);

        if (textview.getText().toString().equals("Quit"))
            colorFrom = parentFragment.getActivity().getResources().getColor(R.color.colorPrimary);

        if (textview.getText().toString().equals("Merge")) {
            if (selectedContacts.size() > 1)
                colorFrom = parentFragment.getActivity().getResources().getColor(R.color.colorPrimary);
            else
                colorFrom = parentFragment.getActivity().getResources().getColor(R.color.gray);
        }

        int colorTo = parentFragment.getActivity().getResources().getColor(R.color.md_deep_orange_300);
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
            int colorFrom = parentFragment.getActivity().getResources().getColor(R.color.md_deep_orange_300);
            //   int colorTo = getResources().getColor(R.color.colorPrimaryDark);
            String s = parentFragment.getActivity().getResources().getResourceEntryName(textview.getId());
            int colorTo;


            if (s.equals("textDeleteSelectMenu") || s.equals("textShareSelect") || s.equals("textBackSelectMenu"))
                colorTo = parentFragment.getActivity().getResources().getColor(R.color.colorPrimary);
            else if (s.equals("mergeTxt")) {
                if (selectedContacts.size() >= 2)
                    colorTo = parentFragment.getActivity().getResources().getColor(R.color.colorPrimary);
                else
                    colorTo = parentFragment.getActivity().getResources().getColor(R.color.gray);

            } else if (s.equals("editTextSelectMenu")) {
                if (selectedContacts.size() == 1)
                    colorTo = parentFragment.getActivity().getResources().getColor(R.color.colorPrimary);
                else
                    colorTo = parentFragment.getActivity().getResources().getColor(R.color.gray);
            } else
                colorTo = parentFragment.getActivity().getResources().getColor(R.color.colorPrimaryDark);

            if (textview.getText().toString().equals("Quit"))
                colorTo = parentFragment.getActivity().getResources().getColor(R.color.colorPrimary);

            if (textview.getText().toString().equals("Merge")) {
                if (selectedContacts.size() > 1)
                    colorTo = parentFragment.getActivity().getResources().getColor(R.color.colorPrimary);
                else
                    colorTo = parentFragment.getActivity().getResources().getColor(R.color.gray);
            }


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
        int colorFrom = parentFragment.getActivity().getResources().getColor(R.color.md_deep_orange_300);
        String s = parentFragment.getActivity().getResources().getResourceEntryName(textView.getId());
        int colorTo;


        if (s.equals("textDeleteSelectMenu") || s.equals("textShareSelect") || s.equals("textBackSelectMenu"))
            colorTo = parentFragment.getActivity().getResources().getColor(R.color.colorPrimary);
        else if (s.equals("mergeTxt")) {
            if (selectedContacts.size() >= 2)
                colorTo = parentFragment.getActivity().getResources().getColor(R.color.colorPrimary);
            else
                colorTo = parentFragment.getActivity().getResources().getColor(R.color.gray);

        } else if (s.equals("editTextSelectMenu")) {
            if (selectedContacts.size() == 1)
                colorTo = parentFragment.getActivity().getResources().getColor(R.color.colorPrimary);
            else
                colorTo = parentFragment.getActivity().getResources().getColor(R.color.gray);
        } else
            colorTo = parentFragment.getActivity().getResources().getColor(R.color.colorPrimaryDark);

        if (textView.getText().toString().equals("Quit"))
            colorTo = parentFragment.getActivity().getResources().getColor(R.color.colorPrimary);


        if (textView.getText().toString().equals("Merge")) {
            if (selectedContacts.size() > 1)
                colorTo = parentFragment.getActivity().getResources().getColor(R.color.colorPrimary);
            else
                colorTo = parentFragment.getActivity().getResources().getColor(R.color.gray);
        }

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


                String s = parentFragment.getActivity().getResources().getResourceEntryName(textView.getId());
                int colorTo2;


                if (s.equals("textDeleteSelectMenu") || s.equals("textShareSelect") || s.equals("textBackSelectMenu"))
                    colorTo2 = parentFragment.getActivity().getResources().getColor(R.color.colorPrimary);
                else if (s.equals("mergeTxt")) {
                    if (selectedContacts.size() >= 2)
                        colorTo2 = parentFragment.getActivity().getResources().getColor(R.color.colorPrimary);
                    else
                        colorTo2 = parentFragment.getActivity().getResources().getColor(R.color.gray);

                } else if (s.equals("editTextSelectMenu")) {
                    if (selectedContacts.size() == 1)
                        colorTo2 = parentFragment.getActivity().getResources().getColor(R.color.colorPrimary);
                    else
                        colorTo2 = parentFragment.getActivity().getResources().getColor(R.color.gray);
                } else
                    colorTo2 = parentFragment.getActivity().getResources().getColor(R.color.colorPrimaryDark);

                if (textView.getText().toString().equals("Quit"))
                    colorTo2 = parentFragment.getActivity().getResources().getColor(R.color.colorPrimary);


                if (textView.getText().toString().equals("Merge")) {
                    if (selectedContacts.size() > 1)
                        colorTo2 = parentFragment.getActivity().getResources().getColor(R.color.colorPrimary);
                    else
                        colorTo2 = parentFragment.getActivity().getResources().getColor(R.color.gray);
                }

                int colorFrom = parentFragment.getActivity().getResources().getColor(R.color.md_deep_orange_300);
                // int colorTo = textView.getTextColors().getDefaultColor();
                //colorTo2 = textView.getTextColors().getDefaultColor();
                //  ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
                //  colorAnimation.setDuration(1000); // milliseconds

                ValueAnimator colorAnimation2 = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo2);
                colorAnimation2.setDuration(1000); // milliseconds

             /*   colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animator) {
                      //  textView.setTextColor((int) animator.getAnimatedValue());
                       // imageView.setColorFilter((int) animator.getAnimatedValue());
                     //   textView.setTypeface(null, Typeface.NORMAL);
                    }

                });
                colorAnimation.start();*/


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


}
