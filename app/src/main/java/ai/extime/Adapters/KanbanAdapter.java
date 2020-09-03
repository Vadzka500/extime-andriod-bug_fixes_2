package ai.extime.Adapters;


import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.extime.R;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberToCarrierMapper;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ai.extime.Activity.MainActivity;
import ai.extime.Enums.SocEnum;
import ai.extime.Events.AddHistoryEntry;
import ai.extime.Events.AnimColorMessenger;
import ai.extime.Events.TypeCard;
import ai.extime.Fragments.ContactsFragment;
import ai.extime.Fragments.EmptyFragment;
import ai.extime.Fragments.FilesConatct;
import ai.extime.Fragments.ProfileFragment;
import ai.extime.Interfaces.CardViewPagerCLicker;
import ai.extime.Interfaces.IKanbanList;
import ai.extime.Interfaces.IOpenSocials;
import ai.extime.Models.Contact;
import ai.extime.Models.ContactInfo;
import ai.extime.Models.HashTag;
import ai.extime.Utils.ClipboardType;
import de.hdodenhof.circleimageview.CircleImageView;

public class KanbanAdapter extends RecyclerView.Adapter<KanbanAdapter.KanBanViewHolder> implements CardViewPagerCLicker {

    private ArrayList<Contact> listOfContacts;

    private View mainView;

    Context context;

    IKanbanList iKanbanList;

    IOpenSocials iOpenSocials;

    FragmentManager fragmentManager;

    protected static TypeCard typeCard;


    private boolean sortAsc = true;
    private boolean sortTimeAsc = false;

    //private ArrayList<Integer> listCount;

    public KanbanAdapter(ArrayList<Contact> list, Context context, IKanbanList iKanbanList, FragmentManager f, TypeCard typeCard, IOpenSocials iOpenSocials) {
        this.listOfContacts = list;
        this.context = context;
        //this.listCount = listCount;
        this.iKanbanList = iKanbanList;
        this.fragmentManager = f;
        this.typeCard = typeCard;
        this.iOpenSocials = iOpenSocials;
        sortContacts();
    }

    public void updateList(ArrayList<Contact> list) {
        this.listOfContacts = list;
        sortContacts();
        notifyDataSetChanged();
    }

    public void updateType(TypeCard typeCard) {
        this.typeCard = typeCard;
        notifyDataSetChanged();
    }

    public void sortByTimeAsc() {
        sortTimeAsc = false;

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

    public void sortByTimeDesc() {
        sortTimeAsc = true;

        Collections.sort(listOfContacts, new Comparator<Contact>() {
            @Override
            public int compare(Contact o1, Contact o2) {

                if (o1.getDateCreate() != null && o2.getDateCreate() != null)
                    return o2.getDateCreate().compareTo(o1.getDateCreate());
                else
                    return 0;

            }
        });
    }

    public void sortByAsc() {

        try {

            Collections.sort(listOfContacts, (contactFirst, contactSecond) -> contactFirst.getName().compareToIgnoreCase(contactSecond.getName()));
        } catch (Exception e) {

        }

        sortAsc = true;
        notifyDataSetChanged();
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

    public void sortByDesc() {

        Collections.sort(listOfContacts, (contactFirst, contactSecond) -> contactSecond.getName().compareToIgnoreCase(contactFirst.getName()));
        sortAsc = false;
        notifyDataSetChanged();
    }

    LayoutInflater layoutInflater;

    @NonNull
    @Override
    public KanBanViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        layoutInflater = LayoutInflater.from(viewGroup.getContext());

        if(i == 1 || i == 2){
            mainView = layoutInflater.inflate(R.layout.kanban_card, viewGroup, false);
        }else{
            mainView = layoutInflater.inflate(R.layout.kanban_medium_layout, viewGroup, false);

            int width_medium = Resources.getSystem().getDisplayMetrics().widthPixels;
            int height_medium = (int) (173 * context.getResources().getDisplayMetrics().density);

            mainView.setLayoutParams(new LinearLayout.LayoutParams(width_medium, height_medium));
        }


        return new KanBanViewHolder(mainView);
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull KanBanViewHolder holder, int i) {



       /* if(i >= 0 && i <=3){
            holder.head.setVisibility(View.VISIBLE);
            holder.body.setVisibility(View.GONE);

            if(i == 0){
                holder.countType.setText(String.valueOf(listCount.get(0)));
                holder.imageType.setImageDrawable(context.getResources().getDrawable(R.drawable.star));
                holder.textType.setText("Favorites");
            }
            if(i == 1){
                holder.countType.setText(String.valueOf(listCount.get(1)));
                holder.imageType.setImageDrawable(context.getResources().getDrawable(R.drawable.icn_important));
                holder.textType.setText("Important");
            }
            if(i == 2){
                holder.countType.setText(String.valueOf(listCount.get(2)));
                holder.imageType.setImageDrawable(context.getResources().getDrawable(R.drawable.pause));
                holder.textType.setText("Pause");
            }
            if(i == 3){
                holder.countType.setText(String.valueOf(listCount.get(3)));
                holder.imageType.setImageDrawable(context.getResources().getDrawable(R.drawable.icn_finished));
                holder.textType.setText("Finished");
            }

        }else{*/
        //holder.head.setVisibility(View.GONE);
        //holder.body.setVisibility(View.VISIBLE);



        if (listOfContacts.get(i) == null) {
            //holder.body.setVisibility(View.INVISIBLE);
        } else {
            if (typeCard.equals(TypeCard.FULL) || typeCard.equals(TypeCard.SHORT)) {

                Contact contact = listOfContacts.get(i);

                if (iKanbanList.checkSelectId(contact.getId())) {
                    holder.body.setBackground(context.getResources().getDrawable(R.drawable.popup_background_select));
                    holder.clicker_2.setVisibility(View.GONE);
                } else {
                    holder.body.setBackground(context.getResources().getDrawable(R.drawable.popup_background_card));
                    holder.clicker_2.setVisibility(View.VISIBLE);
                }


                holder.socialContactAdapter.setVisibility(View.GONE);

                holder.socialContactAdapter.findViewById(R.id.facebook_icon_adapter).setVisibility(View.GONE);
                holder.socialContactAdapter.findViewById(R.id.twitter_icon_adapter).setVisibility(View.GONE);
                holder.socialContactAdapter.findViewById(R.id.linked_icon_adapter).setVisibility(View.GONE);
                holder.socialContactAdapter.findViewById(R.id.instagram_icon_adapter).setVisibility(View.GONE);
                holder.socialContactAdapter.findViewById(R.id.youtube_icon_adapter).setVisibility(View.GONE);
                holder.socialContactAdapter.findViewById(R.id.vk_icon_adapter).setVisibility(View.GONE);
                holder.socialContactAdapter.findViewById(R.id.medium_icon_adapter).setVisibility(View.GONE);

                try {
                    holder.initials.setVisibility(View.GONE);
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
                }

                holder.itemView.setBackgroundColor(Color.TRANSPARENT);

                //holder.number_card.setText(String.valueOf(i - 3));

                if (contact.isFavorite) {
                    holder.type_image_contact.setImageDrawable(context.getResources().getDrawable(R.drawable.star));
                } else if (contact.isImportant) {
                    holder.type_image_contact.setImageDrawable(context.getResources().getDrawable(R.drawable.icn_important));
                } else if (contact.isPause) {
                    holder.type_image_contact.setImageDrawable(context.getResources().getDrawable(R.drawable.pause));
                } else if (contact.isFinished) {
                    holder.type_image_contact.setImageDrawable(context.getResources().getDrawable(R.drawable.icn_finished));
                }else if (contact.isCrown) {
                    holder.type_image_contact.setImageDrawable(context.getResources().getDrawable(R.drawable.crown));
                }else if (contact.isVip) {
                    holder.type_image_contact.setImageDrawable(context.getResources().getDrawable(R.drawable.vip_new));
                }else if (contact.isStartup) {
                    holder.type_image_contact.setImageDrawable(context.getResources().getDrawable(R.drawable.startup));
                }else if (contact.isInvestor) {
                    holder.type_image_contact.setImageDrawable(context.getResources().getDrawable(R.drawable.investor_));
                }

                holder.number_card.setText(String.valueOf(i + 1));


                int type = 1;

                try {
                    Calendar current = Calendar.getInstance();
                    Calendar contactDate = Calendar.getInstance();
                    current.setTime(new Date());
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

                }


                int countSocials = 0;

                //holder.favotiveContact.setVisibility(View.GONE);    //new

                if (contact.getSocialModel() != null) {
                    if (contact.getSocialModel().getFacebookLink() != null && !contact.getSocialModel().getFacebookLink().isEmpty()) {
                        holder.socialContactAdapter.findViewById(R.id.facebook_icon_adapter).setVisibility(View.VISIBLE);
                        holder.socialContactAdapter.findViewById(R.id.facebook_icon_adapter).setOnClickListener(v -> iOpenSocials.openFacebook(contact));
                        countSocials++;
                    }
                    if (contact.getSocialModel().getTwitterLink() != null && !contact.getSocialModel().getTwitterLink().isEmpty()) {
                        holder.socialContactAdapter.findViewById(R.id.twitter_icon_adapter).setVisibility(View.VISIBLE);
                        holder.socialContactAdapter.findViewById(R.id.twitter_icon_adapter).setOnClickListener(v -> iOpenSocials.openTwitter(contact));
                        countSocials++;
                    }
                    if (contact.getSocialModel().getLinkedInLink() != null && !contact.getSocialModel().getLinkedInLink().isEmpty()) {
                        holder.socialContactAdapter.findViewById(R.id.linked_icon_adapter).setVisibility(View.VISIBLE);
                        holder.socialContactAdapter.findViewById(R.id.linked_icon_adapter).setOnClickListener(v -> iOpenSocials.openLinkedIn(contact));
                        countSocials++;
                    }
                    if (contact.getSocialModel().getInstagramLink() != null && !contact.getSocialModel().getInstagramLink().isEmpty()) {
                        holder.socialContactAdapter.findViewById(R.id.instagram_icon_adapter).setVisibility(View.VISIBLE);
                        holder.socialContactAdapter.findViewById(R.id.instagram_icon_adapter).setOnClickListener(v -> iOpenSocials.openInstagram(contact));
                        countSocials++;
                    }

                    while (true) {
                        if (countSocials < 4) {
                            if (contact.getSocialModel().getYoutubeLink() != null && !contact.getSocialModel().getYoutubeLink().isEmpty()) {
                                holder.socialContactAdapter.findViewById(R.id.youtube_icon_adapter).setVisibility(View.VISIBLE);
                                holder.socialContactAdapter.findViewById(R.id.youtube_icon_adapter).setOnClickListener(v -> iOpenSocials.openYoutube(contact));
                                countSocials++;
                            }
                        }

                        if (countSocials < 4) {
                            if (contact.getSocialModel().getVkLink() != null && !contact.getSocialModel().getVkLink().isEmpty()) {
                                holder.socialContactAdapter.findViewById(R.id.vk_icon_adapter).setVisibility(View.VISIBLE);
                                holder.socialContactAdapter.findViewById(R.id.vk_icon_adapter).setOnClickListener(v -> iOpenSocials.openVk(contact));
                                countSocials++;
                            }
                        }

                        if (countSocials < 4) {
                            if (contact.getSocialModel().getMediumLink() != null && !contact.getSocialModel().getMediumLink().isEmpty()) {
                                holder.socialContactAdapter.findViewById(R.id.medium_icon_adapter).setVisibility(View.VISIBLE);
                                holder.socialContactAdapter.findViewById(R.id.medium_icon_adapter).setOnClickListener(v -> iOpenSocials.openMedium(contact));
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
                        holder.linear_card_phone_email.setVisibility(View.VISIBLE);

                        holder.card_phone_contact.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                boolean findPrimary = false;
                                for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                    if (contactInfo.isPhone && !contactInfo.value.equals("+000000000000") && contactInfo.isPrimary) {
                                        iOpenSocials.intentPhone(contactInfo.value);
                                        findPrimary = true;
                                        break;
                                    }
                                }

                                if (!findPrimary) {
                                    for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                        if (contactInfo.isPhone && !contactInfo.value.equals("+000000000000")) {
                                            iOpenSocials.intentPhone(contactInfo.value);
                                            break;
                                        }
                                    }
                                }
                            }
                        });

                    } else holder.card_phone_contact.setVisibility(View.GONE);

                    if (count_email != 0) {
                        holder.email_contact.setVisibility(View.VISIBLE);
                        holder.text_email_card.setText(String.valueOf(count_email));
                        holder.linear_card_phone_email.setVisibility(View.VISIBLE);

                        holder.email_contact.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                boolean findPrimary = false;
                                for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                    if (contactInfo.isEmail && contactInfo.isPrimary) {
                                        iOpenSocials.intentEmail(contactInfo.value);
                                        findPrimary = true;
                                        break;
                                    }
                                }

                                if (!findPrimary) {
                                    for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                        if (contactInfo.isEmail) {
                                            iOpenSocials.intentEmail(contactInfo.value);
                                            break;
                                        }
                                    }
                                }

                            }
                        });

                    } else holder.email_contact.setVisibility(View.GONE);

                }

                holder.textCompany.setVisibility(View.INVISIBLE);
                holder.textPosition.setVisibility(View.INVISIBLE);


                if (contact.getCompany() != null) {

                    if (contact.getCompany().compareTo("") != 0) {

                        //setMargins(holder.linearNameIcons, 0, 0, 0, 0);
                        holder.textCompany.setVisibility(View.VISIBLE);
                        holder.textCompany.setText(contact.getCompany());
                    }
                }

                if (contact.getCompanyPossition() != null) {
                    if (contact.getCompanyPossition().compareTo("") != 0) {
                        //setMargins(holder.linearNameIcons, 0, 0, 0, 0);
                        holder.textPosition.setVisibility(View.VISIBLE);
                        Double ems = holder.textCompany.getText().length() / 2.5;
                        int ems_count = ems.intValue();
                /*if(ems_count < 8){
                    holder.companyName.setMaxEms(6 + (8-ems_count));
                }*/

                        holder.textPosition.setText(contact.getCompanyPossition());
                    }
                }

                if (holder.textCompany.length() > 0 && holder.textPosition.getVisibility() == View.VISIBLE) {
                    Double ems = holder.textPosition.getText().length() / 2.5;
                    int ems_count = ems.intValue();
                }


                holder.userName.setText(contact.getName());
                holder.userName.requestLayout();


                HorizontalScrollView scrollView = holder.scrollHorizontal;


                LinearLayout containerHashTags = holder.containerHashTags;

                if (containerHashTags.getChildCount() > 0)
                    containerHashTags.removeAllViews();
                for (HashTag hashTag : contact.getListOfHashtags()) {
                    TextView text = new TextView(context);
                    text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
                    DisplayMetrics metrics = mainView.getContext().getResources().getDisplayMetrics();
                    text.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    text.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                    text.setText(hashTag.getHashTagValue() + " ");

                    text.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {



                           /* contactAdapter.searchByHashTagValue(hashTag.getHashTagValue());
                            HASHTAG_ADAPTER.setMainHashTag(hashTag.getHashTagValue());

                            if ((listForSelect != null && listForSelect.isEmpty()) || listForSelect == null) {
                                listForSelect = new ArrayList<>();
                                listForSelect.addAll(contactAdapter.getListOfContacts());
                            }*/

                        }
                    });

                    text.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {

                            if (iKanbanList.checkSelectId(contact.getId())) {
                                scrollView.getParent().requestDisallowInterceptTouchEvent(true);
                            }
                            return false;
                        }
                    });

                    /*text.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            deleteHashTagsFromUser(hashTag.getHashTagValue(), contact);
                            return false;
                        }
                    });*/


                    //     text.setOnClickListener(v -> contactAdapter.searchByHashTagValue(hashTag.getHashTagValue()));
                    containerHashTags.addView(text);
                }


                //containerHashTags.setScrollX(0);


                //if (scrollView.getChildCount() > 0) scrollView.removeAllViews();
                //scrollView.addView(containerHashTags);
                scrollView.setSmoothScrollingEnabled(false);
                scrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
                scrollView.setSmoothScrollingEnabled(true);

                //scrollView.setScrollX(0);
                scrollView.scrollTo(0, 0);

                scrollView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                scrollView.setOnTouchListener((v, event) -> {

                    if (iKanbanList.checkSelectId(contact.getId())) {
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                    }
                    return false;
                });


                holder.clickerPreview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (iKanbanList.priviewIsVisible()) {
                            iKanbanList.closeOtherPopups();
                        } else iKanbanList.openPreview(contact, i);
                    }
                });

                holder.clicker_1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        if (iKanbanList.priviewIsVisible()) {
                            iKanbanList.closeOtherPopups();
                        } else {
                            if (iKanbanList.checkSelectId(contact.getId())) {
                                //iKanbanList.setSelectId(-1);
                            } else if (iKanbanList.isSelectEmpty()) {
                                iKanbanList.setSelectId(contact.getId());
                            } else {
                                iKanbanList.setSelectId(-1);
                            }
                        }
                    }
                });

            /*holder.closerTabs.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (iKanbanList.checkSelectId(contact.getId())) {
                        iKanbanList.setSelectId(-1);
                    } else {
                        iKanbanList.setSelectId(contact.getId());
                    }
                }
            });*/


                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        if (iKanbanList.priviewIsVisible()) {
                            iKanbanList.closeOtherPopups();
                        } else {
                            if (!iKanbanList.isSelectEmpty() && !iKanbanList.checkSelectId(contact.getId())) {
                                iKanbanList.setSelectId(-1);
                            }

                            iKanbanList.closeOtherPopups();
                        }
                    }
                });


                if (typeCard.equals(TypeCard.FULL)) {


                    ProfileSectionAdapter adapter;

                    adapter = new ProfileSectionAdapter(fragmentManager);

                    //ViewPager viewPager = holder.viewpager;

               /* Bundle args = new Bundle();
                args.putSerializable("selectedContact", contact.getId());


                args.putSerializable("clicker", KanbanAdapter.this);

                EmptyFragment emptyFragment_1 = new EmptyFragment();
                EmptyFragment emptyFragment_2 = new EmptyFragment();
                EmptyFragment emptyFragment_3 = new EmptyFragment();
                EmptyFragment emptyFragment_4 = new EmptyFragment();
                emptyFragment_1.setArguments(args);
                emptyFragment_2.setArguments(args);
                emptyFragment_3.setArguments(args);
                emptyFragment_4.setArguments(args);*/

                    adapter.addFragment(new FilesConatct(), "Bio");

                    adapter.addFragment(new FilesConatct(), "Timeline");

                    adapter.addFragment(new FilesConatct(), "Schedule");

                    adapter.addFragment(new FilesConatct(), "               Follow-up");

                    //adapter.addFragment(new FilesConatct(), "Files 2");

                    //adapter.addFragment(new ScheduleFragment(), "Schedule 2");

                    holder.viewpager.setAdapter(adapter);


                    holder.result_tabs.setViewPager(holder.viewpager);

                    LinearLayout ll = (LinearLayout) holder.result_tabs.getChildAt(0);

                    for (int j = 0; j < ll.getChildCount(); j++) {
                        TextView tvTabTitle = (TextView) ll.getChildAt(j);
                        tvTabTitle.setTypeface(null, Typeface.NORMAL);
                    }


                    holder.viewpager.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            //if (iKanbanList.checkSelectId(contact.getId())) {
                            v.getParent().requestDisallowInterceptTouchEvent(true);
                            //}
                            return false;
                        }
                    });

                    /*holder.result_tabs.setOnTabClickListener(new SmartTabLayout.OnTabClickListener() {
                        @Override
                        public void onTabClicked(int position) {


                            if(iKanbanList.checkSelectId(contact.getId())){
                                iKanbanList.setSelectId(-1);
                            }else{
                                iKanbanList.setSelectId(contact.getId());
                            }

                            //holder.viewpager.getAdapter().set

                        }
                    });*/





                    holder.clicker_2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            if (iKanbanList.priviewIsVisible()) {
                                iKanbanList.closeOtherPopups();
                            } else {

                                if (iKanbanList.checkSelectId(contact.getId())) {
                                    //iKanbanList.setSelectId(-1);
                                } else if (iKanbanList.isSelectEmpty()) {
                                    iKanbanList.setSelectId(contact.getId());
                                } else {
                                    iKanbanList.setSelectId(-1);
                                }
                            }

                        }
                    });

                    holder.closerTabs.setVisibility(View.VISIBLE);

                    holder.line_card_contact_1.setVisibility(View.VISIBLE);


                } else if (typeCard.equals(TypeCard.SHORT)) {

                    holder.closerTabs.setVisibility(View.GONE);
                    holder.line_card_contact_1.setVisibility(View.GONE);
                } else {

                }



               /* kankabPreview.findViewById(R.id.closerTabs).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //closeOtherPopup();
                        //contactProfileDataFragment.closeOtherPopup();
                    }
                });*/


                holder.prifile_contact_card.setOnClickListener(v -> iKanbanList.openProfile(contact));

                holder.share_contact_card.setOnClickListener(v -> iKanbanList.shareContact(contact));


            }else{
                // MEDIUM MODE

                Contact contact = listOfContacts.get(i);

                if (contact.isFavorite) {
                    ((ImageView) holder.itemView.findViewById(R.id.starImg)).setImageDrawable(context.getResources().getDrawable(R.drawable.star));
                } else if (contact.isImportant) {
                    ((ImageView) holder.itemView.findViewById(R.id.starImg)).setImageDrawable(context.getResources().getDrawable(R.drawable.checked_2));
                } else if (contact.isFinished) {
                    ((ImageView) holder.itemView.findViewById(R.id.starImg)).setImageDrawable(context.getResources().getDrawable(R.drawable.finish_1));
                } else if (contact.isPause) {
                    ((ImageView) holder.itemView.findViewById(R.id.starImg)).setImageDrawable(context.getResources().getDrawable(R.drawable.pause_1));
                }else if (contact.isCrown) {
                    ((ImageView) holder.itemView.findViewById(R.id.starImg)).setImageDrawable(context.getResources().getDrawable(R.drawable.crown));
                }else if (contact.isVip) {
                    ((ImageView) holder.itemView.findViewById(R.id.starImg)).setImageDrawable(context.getResources().getDrawable(R.drawable.vip_new));
                }else if (contact.isStartup) {
                    ((ImageView) holder.itemView.findViewById(R.id.starImg)).setImageDrawable(context.getResources().getDrawable(R.drawable.startup));
                }else if (contact.isInvestor) {
                    ((ImageView) holder.itemView.findViewById(R.id.starImg)).setImageDrawable(context.getResources().getDrawable(R.drawable.investor_));
                }

                //HASHTAG


                LinearLayout containerHashTags = (LinearLayout) holder.itemView.findViewById(R.id.containerHashTags);
                if (containerHashTags.getChildCount() > 0)
                    containerHashTags.removeAllViews();
                for (HashTag hashTag : contact.getListOfHashtags()) {
                    TextView text = new TextView(context);
                    text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                    DisplayMetrics metrics = mainView.getContext().getResources().getDisplayMetrics();
                    text.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    text.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                    text.setText(hashTag.getHashTagValue() + " ");
                    text.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {



                           /* contactAdapter.searchByHashTagValue(hashTag.getHashTagValue());
                            HASHTAG_ADAPTER.setMainHashTag(hashTag.getHashTagValue());

                            if ((listForSelect != null && listForSelect.isEmpty()) || listForSelect == null) {
                                listForSelect = new ArrayList<>();
                                listForSelect.addAll(contactAdapter.getListOfContacts());
                            }*/

                        }
                    });

                    text.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            //deleteHashTagsFromUser(hashTag.getHashTagValue(), contact);
                            return false;
                        }
                    });

                    text.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {

                           // if (iKanbanList.checkSelectId(contact.getId())) {
                                holder.itemView.findViewById(R.id.scrollHorizontal).getParent().requestDisallowInterceptTouchEvent(true);
                           // }
                            return false;
                        }
                    });


                    //     text.setOnClickListener(v -> contactAdapter.searchByHashTagValue(hashTag.getHashTagValue()));
                    containerHashTags.addView(text);
                }

                holder.itemView.findViewById(R.id.scrollHorizontal).setOnTouchListener((v, event) -> {

                   // if (iKanbanList.checkSelectId(contact.getId())) {
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                  //  }
                    return false;
                });


                //FILL DATA

                ((TextView) holder.itemView.findViewById(R.id.name)).setText(contact.getName());



                //COMPANY POSITION

                holder.itemView.findViewById(R.id.company).setVisibility(View.GONE);
                holder.itemView.findViewById(R.id.company_title).setVisibility(View.VISIBLE);

                if (contact.getCompany() != null && !contact.getCompany().isEmpty()) {

                    holder.itemView.findViewById(R.id.company_title).setOnClickListener(v -> {

                                for (Contact searchCompany : listOfContacts) {
                                    if (searchCompany.getName().toLowerCase().compareTo(contact.getCompany().toLowerCase()) == 0) {


                                        //showCompanyPopup(searchCompany);
                                    }
                                }
                            }
                    );

                    ((TextView) holder.itemView.findViewById(R.id.company_title)).setText(contact.getCompany());
                    ((TextView) holder.itemView.findViewById(R.id.company_title)).setHint("");

                    holder.itemView.findViewById(R.id.company_title).setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            //contactAdapter.searchByCompany(contact.getCompany());
                            return true;
                        }
                    });

                } else {
                    ((TextView) holder.itemView.findViewById(R.id.company_title)).setText("");

                    ((TextView) holder.itemView.findViewById(R.id.company_title)).setHint("Company");

                    holder.itemView.findViewById(R.id.company_title).setOnClickListener(v -> {

                               /* if (((TextView) profilePopUp.findViewById(R.id.company_title)).getText() == "")
                                    showCompanyAddPopup(contact, null, false);*/
                            }
                    );
                }


                if (contact.getCompanyPossition() != null) {
                    holder.itemView.findViewById(R.id.company).setVisibility(View.VISIBLE);
                    ((TextView) holder.itemView.findViewById(R.id.company)).setText(contact.getCompanyPossition());


                    holder.itemView.findViewById(R.id.company).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                        }
                    });
                } else {
                    ((TextView) holder.itemView.findViewById(R.id.company_title)).setHint("Company");
                    ((TextView) holder.itemView.findViewById(R.id.company)).setVisibility(View.VISIBLE);
                    ((TextView) holder.itemView.findViewById(R.id.company)).setText("");
                    ((TextView) holder.itemView.findViewById(R.id.company)).setHint("  Position");
                    holder.itemView.findViewById(R.id.company).setOnClickListener(v -> {
                                //showPositionAddPopup(contact, null, false);
                            }
                    );
                }

                //============

                try {
                    holder.itemView.findViewById(R.id.profilePopupInitials).setVisibility(View.GONE);
                    holder.itemView.findViewById(R.id.profilePopupAvatar).setBackgroundColor(Color.TRANSPARENT);
                    ((ImageView) holder.itemView.findViewById(R.id.profilePopupAvatar)).setImageURI(Uri.parse(contact.getPhotoURL()));
                    if (((BitmapDrawable) ((ImageView) holder.itemView.findViewById(R.id.profilePopupAvatar)).getDrawable()).getBitmap() == null) {
                        GradientDrawable circle = (GradientDrawable) ContextCompat.getDrawable(mainView.getContext(), R.drawable.blue_circle).mutate();
                        circle.setColor(contact.color);
                        holder.itemView.findViewById(R.id.profilePopupAvatar).setBackground(circle);
                        ((ImageView) holder.itemView.findViewById(R.id.profilePopupAvatar)).setImageDrawable(null);

                        String initials = "";
                        String names[] = contact.getName().split("\\s+");

                        for (String namePart : names)
                            initials += namePart.charAt(0);

                        holder.itemView.findViewById(R.id.profilePopupInitials).setVisibility(View.VISIBLE);
                        ((TextView) holder.itemView.findViewById(R.id.profilePopupInitials)).setText(initials);
                    }
                } catch (Exception e) {
                    GradientDrawable circle = (GradientDrawable) ContextCompat.getDrawable(mainView.getContext(), R.drawable.blue_circle).mutate();
                    circle.setColor(contact.color);
                    holder.itemView.findViewById(R.id.profilePopupAvatar).setBackground(circle);
                    ((ImageView) holder.itemView.findViewById(R.id.profilePopupAvatar)).setImageDrawable(null);

                    String initials = "";
                    String names[] = contact.getName().split("\\s+");

                    for (String namePart : names)
                        initials += namePart.charAt(0);

                    holder.itemView.findViewById(R.id.profilePopupInitials).setVisibility(View.VISIBLE);
                    ((TextView) holder.itemView.findViewById(R.id.profilePopupInitials)).setText(initials);
                }

                ///====================


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
                        holder.linear_card_phone_email.setVisibility(View.VISIBLE);

                        holder.card_phone_contact.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                boolean findPrimary = false;
                                for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                    if (contactInfo.isPhone && !contactInfo.value.equals("+000000000000") && contactInfo.isPrimary) {
                                        iOpenSocials.intentPhone(contactInfo.value);
                                        findPrimary = true;
                                        break;
                                    }
                                }

                                if (!findPrimary) {
                                    for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                        if (contactInfo.isPhone && !contactInfo.value.equals("+000000000000")) {
                                            iOpenSocials.intentPhone(contactInfo.value);
                                            break;
                                        }
                                    }
                                }
                            }
                        });

                    } else holder.card_phone_contact.setVisibility(View.GONE);

                    if (count_email != 0) {
                        holder.email_contact.setVisibility(View.VISIBLE);
                        holder.text_email_card.setText(String.valueOf(count_email));
                        holder.linear_card_phone_email.setVisibility(View.VISIBLE);

                        holder.email_contact.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                boolean findPrimary = false;
                                for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                    if (contactInfo.isEmail && contactInfo.isPrimary) {
                                        iOpenSocials.intentEmail(contactInfo.value);
                                        findPrimary = true;
                                        break;
                                    }
                                }

                                if (!findPrimary) {
                                    for (ContactInfo contactInfo : contact.getListOfContactInfo()) {
                                        if (contactInfo.isEmail) {
                                            iOpenSocials.intentEmail(contactInfo.value);
                                            break;
                                        }
                                    }
                                }

                            }
                        });

                    } else holder.email_contact.setVisibility(View.GONE);

                }


                //==============

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        iKanbanList.closeOtherPopups();
                    }
                });

                try {
                    Calendar current = Calendar.getInstance();
                    Calendar contactDate = Calendar.getInstance();
                    current.setTime(new Date());
                    contactDate.setTime(contact.getDateCreate());
                    String timeStr = "";
                    if (current.get(Calendar.YEAR) == contactDate.get(Calendar.YEAR) && current.get(Calendar.MONTH) == contactDate.get(Calendar.MONTH) && current.get(Calendar.DAY_OF_MONTH) == contactDate.get(Calendar.DAY_OF_MONTH)) {

                        timeStr = Time.valueOf(contact.time).getHours() + ":";
                        if (Time.valueOf(contact.time).getMinutes() < 10) timeStr += "0";
                        timeStr += Time.valueOf(contact.time).getMinutes();
                    } else if (current.get(Calendar.YEAR) == contactDate.get(Calendar.YEAR) && (current.get(Calendar.MONTH) != contactDate.get(Calendar.MONTH) || current.get(Calendar.DAY_OF_MONTH) != contactDate.get(Calendar.DAY_OF_MONTH))) {


                        timeStr = contactDate.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.ENGLISH) + " " + contactDate.get(Calendar.DAY_OF_MONTH);
                    } else {

                        timeStr = contactDate.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.ENGLISH) + " " + String.valueOf(contactDate.get(Calendar.YEAR))/*.substring(2)*/;

                    }


                    holder.time.setText(timeStr);
                } catch (Exception e) {
                    e.printStackTrace();

                }

                holder.number_card.setText(String.valueOf(i + 1));

                //====== TOUCH


                holder.itemView.findViewById(R.id.user_call_block).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (contact.listOfContactInfo != null) {
                            for (ContactInfo cf : contact.listOfContactInfo) {
                                if (ClipboardType.isPhone(cf.value) && !cf.value.equals("+000000000000")) {
                                    new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + cf.value));
                                    break;
                                }
                            }
                        }

                    }
                });

                //profilePopUp.findViewById(R.id.user_call_block).setOnClickListener(v -> startActivity());

                holder.itemView.findViewById(R.id.user_share_block).setOnClickListener(v -> {

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

                        if (contact.getSocialModel().getMediumLink() != null && !contact.getSocialModel().getMediumLink().isEmpty())
                            exportData += "Medium: " + getPhoneNumberInfo(contact.getSocialModel().getMediumLink()) + "\n";

                /*if(contact.getSocialModel().getWhatsappLink() != null && !contact.getSocialModel().getWhatsappLink().isEmpty())
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
                    context.startActivity(Intent.createChooser(shareIntent, "Поделиться контактом"));
                });

                holder.itemView.findViewById(R.id.user_edit_block).setOnClickListener(v -> {

                    //showFastEditPopup(selectedContactPopup);
                });


                holder.itemView.findViewById(R.id.user_remind_block).setOnClickListener(v -> {

                    //  closeOtherPopup();
                    //profilePopUp.setVisibility(View.GONE);
                    //showRemindPopup(selectedContactPopup);
                });


                holder.itemView.findViewById(R.id.user_profile_block).setOnClickListener(v -> {

                    //ContactAdapter.contactAd = contactAdapter;

                    if (ContactAdapter.checkMerge) {

                        ContactAdapter.checkFoActionIconProfile = true;
                        ContactAdapter.checkMerge = false;
                    }

                    /*if(((EditText)getActivity().findViewById(R.id.magic_edit_text)).getText().length() > 0){

                        EventBus.getDefault().post(new AddHistoryEntry(((EditText)getActivity().findViewById(R.id.magic_edit_text)).getText().toString()));
                    }*/



                    android.support.v4.app.FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                    android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    fragmentTransaction.replace(R.id.main_content, ProfileFragment.newInstance(contact, false)).addToBackStack("contact").commit();

                    //closeOtherPopup();
                    //profilePopUp.setVisibility(View.INVISIBLE);


                });


                holder.itemView.findViewById(R.id.user_profile_block).setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {

                        TextView textView = ((TextView) holder.itemView.findViewById(R.id.textPreviewProfile));
                        //    ImageView imageView = ((ImageView) profilePopUp.findViewById(R.id.imagePreviewIcon));
                        FrameLayout frameLayout = ((FrameLayout) holder.itemView.findViewById(R.id.user_profile_block));
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

                holder.itemView.findViewById(R.id.user_edit_block).setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {

                        TextView textView = ((TextView) holder.itemView.findViewById(R.id.textEditPreview));
                        ImageView imageView = ((ImageView) holder.itemView.findViewById(R.id.editImagePreview));
                        FrameLayout frameLayout = ((FrameLayout) holder.itemView.findViewById(R.id.user_edit_block));
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


                holder.itemView.findViewById(R.id.user_call_block).setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {

                        TextView textView = ((TextView) holder.itemView.findViewById(R.id.TExtCallPreview));
                        ImageView imageView = ((ImageView) holder.itemView.findViewById(R.id.CallImagePreview));
                        FrameLayout frameLayout = ((FrameLayout) holder.itemView.findViewById(R.id.user_call_block));
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


                holder.itemView.findViewById(R.id.user_remind_block).setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {

                        TextView textView = ((TextView) holder.itemView.findViewById(R.id.TextRemindPreview));
                        ImageView imageView = ((ImageView) holder.itemView.findViewById(R.id.ImageRemindPreview));
                        FrameLayout frameLayout = ((FrameLayout) holder.itemView.findViewById(R.id.user_remind_block));
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


                holder.itemView.findViewById(R.id.user_share_block).setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {

                        TextView textView = ((TextView) holder.itemView.findViewById(R.id.TextSharePreview));
                        ImageView imageView = ((ImageView) holder.itemView.findViewById(R.id.ImageSharePreview));
                        FrameLayout frameLayout = ((FrameLayout) holder.itemView.findViewById(R.id.user_share_block));
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









                initIconColor(contact, holder.itemView);





            }
        }
        // }
    }


    //===================================================touch methods
    public void OnTouchMethod(TextView textview) {

        checkClick = false;

        int colorFrom;
        String s = context.getResources().getResourceEntryName(textview.getId());


        /*if (s.equals("textDeleteSelectMenu") || s.equals("textShareSelect") || s.equals("textBackSelectMenu"))
            colorFrom = context.getResources().getColor(R.color.colorPrimary);
        else if (s.equals("mergeTxt")) {

            if (contactAdapter.getSelectedContacts().size() >= 2)
                colorFrom = context.getResources().getColor(R.color.colorPrimary);
            else
                colorFrom = context.getResources().getColor(R.color.gray);

        } else if (s.equals("editTextSelectMenu")) {
            if (contactAdapter.getSelectedContacts().size() == 1)
                colorFrom = getResources().getColor(R.color.colorPrimary);
            else
                colorFrom = getResources().getColor(R.color.gray);
        } else*/
            colorFrom = context.getResources().getColor(R.color.colorPrimaryDark);

        int colorTo = context.getResources().getColor(R.color.md_deep_orange_300);
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
            int colorFrom = context.getResources().getColor(R.color.md_deep_orange_300);
            //   int colorTo = getResources().getColor(R.color.colorPrimaryDark);
            String s = context.getResources().getResourceEntryName(textview.getId());
            int colorTo;


            /*if (s.equals("textDeleteSelectMenu") || s.equals("textShareSelect") || s.equals("textBackSelectMenu"))
                colorTo = getResources().getColor(R.color.colorPrimary);
            else if (s.equals("mergeTxt")) {
                if (contactAdapter.getSelectedContacts().size() >= 2)
                    colorTo = getResources().getColor(R.color.colorPrimary);
                else
                    colorTo = getResources().getColor(R.color.gray);

            } else if (s.equals("editTextSelectMenu")) {
                if (contactAdapter.getSelectedContacts().size() == 1)
                    colorTo = getResources().getColor(R.color.colorPrimary);
                else
                    colorTo = getResources().getColor(R.color.gray);
            } else*/
                colorTo = context.getResources().getColor(R.color.colorPrimaryDark);


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
        int colorFrom = context.getResources().getColor(R.color.md_deep_orange_300);
        String s = context.getResources().getResourceEntryName(textView.getId());
        int colorTo;


        /*if (s.equals("textDeleteSelectMenu") || s.equals("textShareSelect") || s.equals("textBackSelectMenu"))
            colorTo = getResources().getColor(R.color.colorPrimary);
        else if (s.equals("mergeTxt")) {
            if (contactAdapter.getSelectedContacts().size() >= 2)
                colorTo = getResources().getColor(R.color.colorPrimary);
            else
                colorTo = getResources().getColor(R.color.gray);

        } else if (s.equals("editTextSelectMenu")) {
            if (contactAdapter.getSelectedContacts().size() == 1)
                colorTo = getResources().getColor(R.color.colorPrimary);
            else
                colorTo = getResources().getColor(R.color.gray);
        } else*/
            colorTo = context.getResources().getColor(R.color.colorPrimaryDark);


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


                String s = context.getResources().getResourceEntryName(textView.getId());
                int colorTo2;


                /*if (s.equals("textDeleteSelectMenu") || s.equals("textShareSelect") || s.equals("textBackSelectMenu"))
                    colorTo2 = context.getResources().getColor(R.color.colorPrimary);
                else if (s.equals("mergeTxt")) {
                    if (contactAdapter.getSelectedContacts().size() >= 2)
                        colorTo2 = getResources().getColor(R.color.colorPrimary);
                    else
                        colorTo2 = getResources().getColor(R.color.gray);

                } else if (s.equals("editTextSelectMenu")) {
                    if (contactAdapter.getSelectedContacts().size() == 1)
                        colorTo2 = getResources().getColor(R.color.colorPrimary);
                    else
                        colorTo2 = getResources().getColor(R.color.gray);
                } else*/
                    colorTo2 = context.getResources().getColor(R.color.colorPrimaryDark);


                int colorFrom = context.getResources().getColor(R.color.md_deep_orange_300);
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

    public boolean checkClick = true;

    //==============================================================


    private void setMargins(View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
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

    @Override
    public int getItemCount() {
        return listOfContacts.size();
    }

    @Override
    public int getItemViewType(int position) {
        return typeCard.getId();
    }

    @Override
    public void click(long id) {
        if (iKanbanList.checkSelectId(id)) {
            iKanbanList.setSelectId(-1);
        } else {
            iKanbanList.setSelectId(id);

        }


    }





    public void initIconColor(Contact contact, View view) {

       /* view.findViewById(R.id.social_facebook_preview).setVisibility(View.GONE);
        view.findViewById(R.id.social_twitter_preview).setVisibility(View.GONE);
        view.findViewById(R.id.social_linked_preview).setVisibility(View.GONE);
        view.findViewById(R.id.social_insta_preview).setVisibility(View.GONE);
        view.findViewById(R.id.social_youtube_preview).setVisibility(View.GONE);
        view.findViewById(R.id.social_vk_preview).setVisibility(View.GONE);
        view.findViewById(R.id.social_medium_preview).setVisibility(View.GONE);*/


        final boolean[] checkClick_viber = {true};
        final boolean[] checkClick_telegram = {true};
        final boolean[] checkClick_skype = {true};

        final boolean[] checkClick_facebook = {true};
        final boolean[] checkClick_vk = {true};
        final boolean[] checkClick_linked = {true};
        final boolean[] checkClick_inst = {true};
        final boolean[] checkClick_youtube = {true};
        final boolean[] checkClick_twitter = {true};
        final boolean[] checkClick_medium = {true};

        View FaceBook = null;
        View TwiTter = null;
        View LinkedIn = null;
        View Instagram = null;
        View YouTube = null;
        View Vkontakte = null;
        View Medium = null;

        final boolean[] checkClick = {true};



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
            FaceBook = layoutInflater.inflate(R.layout.social_facebook, null);



            Drawable color = new ColorDrawable(Color.parseColor("#475993"));
            Drawable image = context.getResources().getDrawable(R.drawable.icn_social_facebook2);
            LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
            ((ImageView) FaceBook.findViewById(R.id.facebook_icon)).setImageDrawable(ld);

            //view.findViewById(R.id.social_facebook_preview).setVisibility(View.VISIBLE);

            linearLayout.addView(FaceBook);

        }

        if ((contact.getSocialModel() != null && contact.getSocialModel().getTwitterLink() != null && !contact.getSocialModel().getTwitterLink().isEmpty())) {
            listExist.add(SocEnum.TWITTER);
            TwiTter = layoutInflater.inflate(R.layout.social_twitter, null);

            Drawable color = new ColorDrawable(Color.parseColor("#2ca7e0"));
            Drawable image = context.getResources().getDrawable(R.drawable.ic_twitter_white);
            LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
            ((ImageView) TwiTter.findViewById(R.id.twitter_icon)).setImageDrawable(ld);

            //view.findViewById(R.id.social_twitter_preview).setVisibility(View.VISIBLE);

            linearLayout.addView(TwiTter);
        }

        if ((contact.getSocialModel() != null && contact.getSocialModel().getLinkedInLink() != null && !contact.getSocialModel().getLinkedInLink().isEmpty())) {
            listExist.add(SocEnum.LINKEDIN);
            LinkedIn = layoutInflater.inflate(R.layout.social_linked, null);

            Drawable color = new ColorDrawable(Color.parseColor("#0077B7"));
            Drawable image = context.getResources().getDrawable(R.drawable.icn_social_linked2);
            LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
            ((ImageView) LinkedIn.findViewById(R.id.link_icon)).setImageDrawable(ld);

            //LinkedIn.setVisibility(View.VISIBLE);

            linearLayout.addView(LinkedIn);

        }

        if ((contact.getSocialModel() != null && contact.getSocialModel().getInstagramLink() != null && !contact.getSocialModel().getInstagramLink().isEmpty())) {
            listExist.add(SocEnum.INSTAGRAM);
            Instagram = layoutInflater.inflate(R.layout.social_insta, null);

            Drawable color = new ColorDrawable(Color.parseColor("#8a3ab9"));
            Drawable image = context.getResources().getDrawable(R.drawable.icn_social_ints2);
            LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
            ((ImageView) Instagram.findViewById(R.id.inst_icon)).setImageDrawable(ld);

            //Instagram.setVisibility(View.VISIBLE);
            linearLayout.addView(Instagram);

        }


        if (listExist.size() < 4 && (contact.getSocialModel() != null && contact.getSocialModel().getYoutubeLink() != null && !contact.getSocialModel().getYoutubeLink().isEmpty())) {
            listExist.add(SocEnum.YOUTUBE);
            YouTube = layoutInflater.inflate(R.layout.social_youtube, null);

            Drawable colorv = new ColorDrawable(Color.parseColor("#ed2524"));
            Drawable imagev = context.getResources().getDrawable(R.drawable.ic_youtube_white);
            LayerDrawable ldv = new LayerDrawable(new Drawable[]{colorv, imagev});
            ((ImageView) YouTube.findViewById(R.id.youtube_icon)).setImageDrawable(ldv);

            linearLayout.addView(YouTube);
        }

        if (listExist.size() < 4 && (contact.getSocialModel() != null && contact.getSocialModel().getVkLink() != null && !contact.getSocialModel().getVkLink().isEmpty())) {
            listExist.add(SocEnum.VK);
            Vkontakte = layoutInflater.inflate(R.layout.social_vk, null);

            Drawable colorv = new ColorDrawable(Color.parseColor("#507299"));
            Drawable imagev = context.getResources().getDrawable(R.drawable.icn_social_vk2);
            LayerDrawable ldv = new LayerDrawable(new Drawable[]{colorv, imagev});
            ((ImageView) Vkontakte.findViewById(R.id.vk_icon)).setImageDrawable(ldv);

            linearLayout.addView(Vkontakte);
        }

        if (listExist.size() < 4 && (contact.getSocialModel() != null && contact.getSocialModel().getMediumLink() != null && !contact.getSocialModel().getMediumLink().isEmpty())) {
            listExist.add(SocEnum.MEDIUM);
            Medium = layoutInflater.inflate(R.layout.social_medium, null);

            Drawable colorv = new ColorDrawable(Color.parseColor("#000000"));
            Drawable imagev = context.getResources().getDrawable(R.drawable.medium_white);
            LayerDrawable ldv = new LayerDrawable(new Drawable[]{colorv, imagev});
            ((ImageView) Medium.findViewById(R.id.medium_icon)).setImageDrawable(ldv);

            linearLayout.addView(Medium);
        }




        if (linearLayout.getChildCount() < 4) {
            while (true) {
                if (!listExist.contains(SocEnum.FACEBOOK)) {
                    FaceBook = layoutInflater.inflate(R.layout.social_facebook, null);

                    Drawable color = new ColorDrawable(Color.parseColor("#e2e5e8"));
                    Drawable image = context.getResources().getDrawable(R.drawable.icn_social_facebook2);
                    LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
                    ((ImageView) FaceBook.findViewById(R.id.facebook_icon)).setImageDrawable(ld);

                    linearLayout.addView(FaceBook);
                }
                if (linearLayout.getChildCount() == 4) break;

                if (!listExist.contains(SocEnum.TWITTER)) {
                    TwiTter = layoutInflater.inflate(R.layout.social_twitter, null);

                    Drawable color = new ColorDrawable(Color.parseColor("#e2e5e8"));
                    Drawable image = context.getResources().getDrawable(R.drawable.ic_twitter_white);
                    LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
                    ((ImageView) TwiTter.findViewById(R.id.twitter_icon)).setImageDrawable(ld);

                    linearLayout.addView(TwiTter);
                }
                if (linearLayout.getChildCount() == 4) break;

                if (!listExist.contains(SocEnum.LINKEDIN)) {
                    LinkedIn = layoutInflater.inflate(R.layout.social_linked, null);

                    Drawable color = new ColorDrawable(Color.parseColor("#e2e5e8"));
                    Drawable image = context.getResources().getDrawable(R.drawable.icn_social_linked2);
                    LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
                    ((ImageView) LinkedIn.findViewById(R.id.link_icon)).setImageDrawable(ld);

                    linearLayout.addView(LinkedIn);

                }
                if (linearLayout.getChildCount() == 4) break;

                if (!listExist.contains(SocEnum.INSTAGRAM)) {
                    Instagram = layoutInflater.inflate(R.layout.social_insta, null);

                    Drawable color = new ColorDrawable(Color.parseColor("#e2e5e8"));
                    Drawable image = context.getResources().getDrawable(R.drawable.icn_social_ints2);
                    LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
                    ((ImageView) Instagram.findViewById(R.id.inst_icon)).setImageDrawable(ld);

                    linearLayout.addView(Instagram);
                }
                if (linearLayout.getChildCount() == 4) break;

            }
        }





        if (FaceBook != null) {
            View finalFaceBook = FaceBook;
            FaceBook.findViewById(R.id.facebook_icon).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {


                    ImageView cImg = ((ImageView) finalFaceBook.findViewById(R.id.facebook_icon));
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN: {

                            if (listExist.contains(SocEnum.FACEBOOK) && checkClick_facebook[0]) {
                                checkClick_facebook[0] = false;
                                int colorFrom = Color.parseColor("#475993");
                                int colorTo = Color.parseColor("#F9A825");
                                Drawable image = context.getResources().getDrawable(R.drawable.icn_social_facebook2);
                                AnimColorMessenger.ActionDown_(colorFrom, colorTo, image, cImg);
                            } else {
                                checkClick_facebook[0] = false;
                                int colorFrom = Color.parseColor("#e2e5e8");
                                int colorTo = Color.parseColor("#F9A825");
                                Drawable image = context.getResources().getDrawable(R.drawable.icn_social_facebook2);
                                AnimColorMessenger.ActionDown_(colorFrom, colorTo, image, cImg);
                            }


                            break;
                        }
                        case MotionEvent.ACTION_UP: {
                            if (!checkClick_facebook[0]) {
                                if (listExist.contains(SocEnum.FACEBOOK)) {
                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#475993");
                                    Drawable image = context.getResources().getDrawable(R.drawable.icn_social_facebook2);
                                    AnimColorMessenger.ActionUp_(colorFrom, colorTo, image, cImg);
                                } else {
                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#e2e5e8");
                                    Drawable image = context.getResources().getDrawable(R.drawable.icn_social_facebook2);
                                    AnimColorMessenger.ActionUp_(colorFrom, colorTo, image, cImg);
                                }
                            }
                            break;
                        }
                        case MotionEvent.ACTION_CANCEL: {
                            if (listExist.contains(SocEnum.FACEBOOK)) {
                                int colorFrom = Color.parseColor("#F9A825");
                                int colorTo = Color.parseColor("#475993");
                                Drawable image = context.getResources().getDrawable(R.drawable.icn_social_facebook2);
                                AnimColorMessenger.ActionCancel_(colorFrom, colorTo, image, cImg);
                            } else {
                                int colorFrom = Color.parseColor("#F9A825");
                                int colorTo = Color.parseColor("#e2e5e8");
                                Drawable image = context.getResources().getDrawable(R.drawable.icn_social_facebook2);
                                AnimColorMessenger.ActionCancel_(colorFrom, colorTo, image, cImg);
                            }
                            break;
                        }
                        case MotionEvent.ACTION_MOVE: {
                            if (!checkClick_facebook[0]) {
                                if (listExist.contains(SocEnum.FACEBOOK)) {
                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#475993");
                                    Drawable image = context.getResources().getDrawable(R.drawable.icn_social_facebook2);
                                    checkClick_facebook[0] = AnimColorMessenger.ActionMove_(colorFrom, colorTo, image, cImg, motionEvent, finalFaceBook);
                                } else {
                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#e2e5e8");
                                    Drawable image = context.getResources().getDrawable(R.drawable.icn_social_facebook2);
                                    checkClick_facebook[0] = AnimColorMessenger.ActionMove_(colorFrom, colorTo, image, cImg, motionEvent, finalFaceBook);
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

                        context.startActivity(i);
                        return true;
                    }
                    return false;
                }
            });

            FaceBook.findViewById(R.id.facebook_icon).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (contact.getSocialModel() == null || contact.getSocialModel().getFacebookLink() == null || contact.getSocialModel().getFacebookLink().isEmpty()) {

                        /*showSocialPopup(contact);
                        socialPopup.findViewById(R.id.facebook_social).callOnClick();
                        socialPopup.setVisibility(View.GONE);*/

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


                        PackageManager packageManager = context.getPackageManager();
                        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
                        boolean isIntentSafe = activities.size() > 0;
                        if (!isIntentSafe) {
                            try {
                                String uris = contact.getSocialModel().getFacebookLink();
                                if (!contact.getSocialModel().getFacebookLink().contains("https://") && !contact.getSocialModel().getFacebookLink().contains("http://"))
                                    uris = "https://" + uris;

                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(uris));
                                context.startActivity(i);
                            } catch (Exception e) {

                            }
                        } else
                            context.startActivity(intent);
                    }

                }
            });
        }


        if (TwiTter != null) {
            View finalTwiTter = TwiTter;
            TwiTter.findViewById(R.id.twitter_icon).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {


                    ImageView cImg = ((ImageView) finalTwiTter.findViewById(R.id.twitter_icon));
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN: {

                            if (listExist.contains(SocEnum.TWITTER) && checkClick_twitter[0]) {
                                checkClick_twitter[0] = false;
                                int colorFrom = Color.parseColor("#2ca7e0");
                                int colorTo = Color.parseColor("#F9A825");
                                Drawable image = context.getResources().getDrawable(R.drawable.ic_twitter_white);
                                AnimColorMessenger.ActionDown_(colorFrom, colorTo, image, cImg);
                            } else {
                                checkClick_twitter[0] = false;
                                int colorFrom = Color.parseColor("#e2e5e8");
                                int colorTo = Color.parseColor("#F9A825");
                                Drawable image = context.getResources().getDrawable(R.drawable.ic_twitter_white);
                                AnimColorMessenger.ActionDown_(colorFrom, colorTo, image, cImg);
                            }

                            break;
                        }
                        case MotionEvent.ACTION_UP: {
                            if (!checkClick_twitter[0]) {
                                if (listExist.contains(SocEnum.TWITTER)) {
                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#2ca7e0");
                                    Drawable image = context.getResources().getDrawable(R.drawable.ic_twitter_white);
                                    AnimColorMessenger.ActionUp_(colorFrom, colorTo, image, cImg);
                                } else {
                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#e2e5e8");
                                    Drawable image = context.getResources().getDrawable(R.drawable.ic_twitter_white);
                                    AnimColorMessenger.ActionUp_(colorFrom, colorTo, image, cImg);
                                }
                            }
                            break;
                        }
                        case MotionEvent.ACTION_CANCEL: {
                            if (listExist.contains(SocEnum.TWITTER)) {
                                int colorFrom = Color.parseColor("#F9A825");
                                int colorTo = Color.parseColor("#2ca7e0");
                                Drawable image = context.getResources().getDrawable(R.drawable.ic_twitter_white);
                                AnimColorMessenger.ActionCancel_(colorFrom, colorTo, image, cImg);
                            } else {
                                int colorFrom = Color.parseColor("#F9A825");
                                int colorTo = Color.parseColor("#e2e5e8");
                                Drawable image = context.getResources().getDrawable(R.drawable.ic_twitter_white);
                                AnimColorMessenger.ActionCancel_(colorFrom, colorTo, image, cImg);
                            }
                            break;
                        }
                        case MotionEvent.ACTION_MOVE: {
                            if (!checkClick_twitter[0]) {
                                if (listExist.contains(SocEnum.TWITTER)) {
                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#2ca7e0");
                                    Drawable image = context.getResources().getDrawable(R.drawable.ic_twitter_white);
                                    checkClick_twitter[0] = AnimColorMessenger.ActionMove_(colorFrom, colorTo, image, cImg, motionEvent, finalTwiTter);
                                } else {
                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#e2e5e8");
                                    Drawable image = context.getResources().getDrawable(R.drawable.ic_twitter_white);
                                    checkClick_twitter[0] = AnimColorMessenger.ActionMove_(colorFrom, colorTo, image, cImg, motionEvent, finalTwiTter);
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

                        /*showSocialPopup(contact);
                        socialPopup.findViewById(R.id.twitter_link).callOnClick();
                        socialPopup.setVisibility(View.GONE);*/

                    } else {
                        String text = contact.getSocialModel().getTwitterLink();
                        if (text.contains("twitter.com/")) {
                            text = text.substring(text.indexOf(".com/") + 5);
                        }
                        if(text.length() > 0 && text.charAt(0) == '@') text = text.substring(1);
                        Intent intent = null;
                        try {


                            context.getPackageManager().getPackageInfo("com.twitter.android", 0);
                            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + text));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        } catch (Exception e) {

                            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/" + text));
                        }
                        context.startActivity(intent);

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

                        context.startActivity(i);
                        return true;
                    }
                    return false;
                }
            });

        }

        if (LinkedIn != null) {
            View finalLinkedIn = LinkedIn;
            LinkedIn.findViewById(R.id.link_icon).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {


                    ImageView cImg = ((ImageView) finalLinkedIn.findViewById(R.id.link_icon));
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN: {

                            if (listExist.contains(SocEnum.LINKEDIN) && checkClick_linked[0]) {
                                checkClick_linked[0] = false;
                                int colorFrom = Color.parseColor("#0077B7");
                                int colorTo = Color.parseColor("#F9A825");
                                Drawable image = context.getResources().getDrawable(R.drawable.icn_social_linked2);
                                AnimColorMessenger.ActionDown_(colorFrom, colorTo, image, cImg);
                            } else {
                                checkClick_linked[0] = false;
                                int colorFrom = Color.parseColor("#e2e5e8");
                                int colorTo = Color.parseColor("#F9A825");
                                Drawable image = context.getResources().getDrawable(R.drawable.icn_social_linked2);
                                AnimColorMessenger.ActionDown_(colorFrom, colorTo, image, cImg);
                            }

                            break;
                        }
                        case MotionEvent.ACTION_UP: {
                            if (!checkClick_linked[0]) {
                                if (listExist.contains(SocEnum.LINKEDIN)) {
                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#0077B7");
                                    Drawable image = context.getResources().getDrawable(R.drawable.icn_social_linked2);
                                    AnimColorMessenger.ActionUp_(colorFrom, colorTo, image, cImg);
                                } else {
                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#e2e5e8");
                                    Drawable image = context.getResources().getDrawable(R.drawable.icn_social_linked2);
                                    AnimColorMessenger.ActionUp_(colorFrom, colorTo, image, cImg);
                                }
                            }
                            break;
                        }
                        case MotionEvent.ACTION_CANCEL: {
                            if (listExist.contains(SocEnum.LINKEDIN)) {
                                int colorFrom = Color.parseColor("#F9A825");
                                int colorTo = Color.parseColor("#0077B7");
                                Drawable image = context.getResources().getDrawable(R.drawable.icn_social_linked2);
                                AnimColorMessenger.ActionCancel_(colorFrom, colorTo, image, cImg);
                            } else {
                                int colorFrom = Color.parseColor("#F9A825");
                                int colorTo = Color.parseColor("#e2e5e8");
                                Drawable image = context.getResources().getDrawable(R.drawable.icn_social_linked2);
                                AnimColorMessenger.ActionCancel_(colorFrom, colorTo, image, cImg);
                            }
                            break;
                        }
                        case MotionEvent.ACTION_MOVE: {
                            if (!checkClick_linked[0]) {
                                if (listExist.contains(SocEnum.LINKEDIN)) {
                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#0077B7");
                                    Drawable image = context.getResources().getDrawable(R.drawable.icn_social_linked2);
                                    checkClick_linked[0] = AnimColorMessenger.ActionMove_(colorFrom, colorTo, image, cImg, motionEvent, finalLinkedIn);
                                } else {
                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#e2e5e8");
                                    Drawable image = context.getResources().getDrawable(R.drawable.icn_social_linked2);
                                    checkClick_linked[0] = AnimColorMessenger.ActionMove_(colorFrom, colorTo, image, cImg, motionEvent, finalLinkedIn);
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

                            i.setData(Uri.parse("https://www.linkedin.com/mwlite/search/results/all/?keywords=" + str));

                        } else {
                            i.setData(Uri.parse("https://www.linkedin.com/mwlite/search/results/all/?keywords=" + mach[0]));

                        }

                        context.startActivity(i);
                        return true;
                    }
                    return false;
                }
            });


            LinkedIn.findViewById(R.id.link_icon).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if (contact.getSocialModel() == null || contact.getSocialModel().getLinkedInLink() == null || contact.getSocialModel().getLinkedInLink().isEmpty()) {

                        /*showSocialPopup(contact);
                        socialPopup.findViewById(R.id.linkedLink).callOnClick();
                        socialPopup.setVisibility(View.GONE);*/

                    } else {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(contact.getSocialModel().getLinkedInLink()));
                        PackageManager packageManager = context.getPackageManager();
                        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
                        boolean isIntentSafe = activities.size() > 0;
                        if (!isIntentSafe) {
                            Intent intent2 = new Intent(Intent.ACTION_VIEW);
                            intent2.setData(Uri.parse("https://www.linkedin.com/in/"));
                            context.startActivity(intent2);
                        } else {
                            try {
                                context.startActivity(intent);
                            } catch (Exception e) {
                                Intent intent2 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/"));
                                context.startActivity(intent2);
                            }


                        }
                    }


                }
            });
        }

        if (Instagram != null) {
            View finalInstagram = Instagram;
            Instagram.findViewById(R.id.inst_icon).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {


                    ImageView cImg = ((ImageView) finalInstagram.findViewById(R.id.inst_icon));
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN: {

                            if (listExist.contains(SocEnum.INSTAGRAM) && checkClick_inst[0]) {
                                checkClick_inst[0] = false;
                                int colorFrom = Color.parseColor("#8a3ab9");
                                int colorTo = Color.parseColor("#F9A825");
                                Drawable image = context.getResources().getDrawable(R.drawable.icn_social_ints2);
                                AnimColorMessenger.ActionDown_(colorFrom, colorTo, image, cImg);
                            } else {
                                checkClick_inst[0] = false;
                                int colorFrom = Color.parseColor("#e2e5e8");
                                int colorTo = Color.parseColor("#F9A825");
                                Drawable image = context.getResources().getDrawable(R.drawable.icn_social_ints2);
                                AnimColorMessenger.ActionDown_(colorFrom, colorTo, image, cImg);
                            }

                            break;
                        }
                        case MotionEvent.ACTION_UP: {
                            if (!checkClick_inst[0]) {
                                if (listExist.contains(SocEnum.INSTAGRAM)) {
                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#8a3ab9");
                                    Drawable image = context.getResources().getDrawable(R.drawable.icn_social_ints2);
                                    AnimColorMessenger.ActionUp_(colorFrom, colorTo, image, cImg);
                                } else {
                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#e2e5e8");
                                    Drawable image = context.getResources().getDrawable(R.drawable.icn_social_ints2);
                                    AnimColorMessenger.ActionUp_(colorFrom, colorTo, image, cImg);
                                }
                            }
                            break;
                        }
                        case MotionEvent.ACTION_CANCEL: {
                            if (listExist.contains(SocEnum.INSTAGRAM)) {
                                int colorFrom = Color.parseColor("#F9A825");
                                int colorTo = Color.parseColor("#8a3ab9");
                                Drawable image = context.getResources().getDrawable(R.drawable.icn_social_ints2);
                                AnimColorMessenger.ActionCancel_(colorFrom, colorTo, image, cImg);
                            } else {
                                int colorFrom = Color.parseColor("#F9A825");
                                int colorTo = Color.parseColor("#e2e5e8");
                                Drawable image = context.getResources().getDrawable(R.drawable.icn_social_ints2);
                                AnimColorMessenger.ActionCancel_(colorFrom, colorTo, image, cImg);
                            }
                            break;
                        }
                        case MotionEvent.ACTION_MOVE: {
                            if (!checkClick_inst[0]) {
                                if (listExist.contains(SocEnum.INSTAGRAM)) {
                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#8a3ab9");
                                    Drawable image = context.getResources().getDrawable(R.drawable.icn_social_ints2);
                                    checkClick_inst[0] = AnimColorMessenger.ActionMove_(colorFrom, colorTo, image, cImg, motionEvent, finalInstagram);
                                } else {
                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#e2e5e8");
                                    Drawable image = context.getResources().getDrawable(R.drawable.icn_social_ints2);
                                    checkClick_inst[0] = AnimColorMessenger.ActionMove_(colorFrom, colorTo, image, cImg, motionEvent, finalInstagram);
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

                        /*showSocialPopup(contact);
                        socialPopup.findViewById(R.id.instagramLink).callOnClick();
                        socialPopup.setVisibility(View.GONE);*/

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
                            PackageManager packageManager = context.getPackageManager();
                            List<ResolveInfo> activities = packageManager.queryIntentActivities(likeIng, 0);
                            boolean isIntentSafe = activities.size() > 0;
                            if (!isIntentSafe) {

                                try {
                                    String uris = contact.getSocialModel().getInstagramLink();
                                    if (!contact.getSocialModel().getInstagramLink().contains("https://") && !contact.getSocialModel().getInstagramLink().contains("http://"))
                                        uris = "https://" + uris;

                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                    i.setData(Uri.parse(uris));
                                    context.startActivity(i);
                                } catch (Exception e) {

                                }
                            } else
                                context.startActivity(likeIng);
                        } catch (Exception e) {

                        }
                    }

                }
            });
        }

        if (YouTube != null) {
            View finalYouTube = YouTube;
            YouTube.findViewById(R.id.youtube_icon).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {


                    ImageView cImg = ((ImageView) finalYouTube.findViewById(R.id.youtube_icon));
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN: {

                            if (listExist.contains(SocEnum.YOUTUBE) && checkClick_youtube[0]) {
                                checkClick_youtube[0] = false;
                                int colorFrom = Color.parseColor("#ed2524");
                                int colorTo = Color.parseColor("#F9A825");
                                Drawable image = context.getResources().getDrawable(R.drawable.ic_youtube_white);
                                AnimColorMessenger.ActionDown_(colorFrom, colorTo, image, cImg);
                            } else {
                                checkClick_youtube[0] = false;
                                int colorFrom = Color.parseColor("#e2e5e8");
                                int colorTo = Color.parseColor("#F9A825");
                                Drawable image = context.getResources().getDrawable(R.drawable.ic_youtube_white);
                                AnimColorMessenger.ActionDown_(colorFrom, colorTo, image, cImg);
                            }


                            break;
                        }
                        case MotionEvent.ACTION_UP: {
                            if (!checkClick_youtube[0]) {
                                if (listExist.contains(SocEnum.YOUTUBE)) {
                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#ed2524");
                                    Drawable image = context.getResources().getDrawable(R.drawable.ic_youtube_white);
                                    AnimColorMessenger.ActionUp_(colorFrom, colorTo, image, cImg);
                                } else {
                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#e2e5e8");
                                    Drawable image = context.getResources().getDrawable(R.drawable.ic_youtube_white);
                                    AnimColorMessenger.ActionUp_(colorFrom, colorTo, image, cImg);
                                }
                            }
                            break;
                        }
                        case MotionEvent.ACTION_CANCEL: {
                            if (listExist.contains(SocEnum.YOUTUBE)) {
                                int colorFrom = Color.parseColor("#F9A825");
                                int colorTo = Color.parseColor("#ed2524");
                                Drawable image = context.getResources().getDrawable(R.drawable.ic_youtube_white);
                                AnimColorMessenger.ActionCancel_(colorFrom, colorTo, image, cImg);
                            } else {
                                int colorFrom = Color.parseColor("#F9A825");
                                int colorTo = Color.parseColor("#e2e5e8");
                                Drawable image = context.getResources().getDrawable(R.drawable.ic_youtube_white);
                                AnimColorMessenger.ActionCancel_(colorFrom, colorTo, image, cImg);
                            }
                            break;
                        }
                        case MotionEvent.ACTION_MOVE: {
                            if (!checkClick_youtube[0]) {
                                if (listExist.contains(SocEnum.YOUTUBE)) {
                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#ed2524");
                                    Drawable image = context.getResources().getDrawable(R.drawable.ic_youtube_white);
                                    checkClick_youtube[0] = AnimColorMessenger.ActionMove_(colorFrom, colorTo, image, cImg, motionEvent, finalYouTube);
                                } else {
                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#e2e5e8");
                                    Drawable image = context.getResources().getDrawable(R.drawable.ic_youtube_white);
                                    checkClick_youtube[0] = AnimColorMessenger.ActionMove_(colorFrom, colorTo, image, cImg, motionEvent, finalYouTube);
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

                        /*showSocialPopup(contact);
                        socialPopup.findViewById(R.id.youtube_link).callOnClick();
                        socialPopup.setVisibility(View.GONE);*/

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
                                context.startActivity(appIntent);
                            } catch (ActivityNotFoundException ex) {
                                context.startActivity(webIntent);
                            }
                        } else if (text.contains("user/") || text.contains("channel/") || text.contains("/c/")) {
                            try {
                                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(text)));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }


                }
            });
        }

        if (Vkontakte != null) {
            View finalVkontakte = Vkontakte;
            Vkontakte.findViewById(R.id.vk_icon).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {


                    ImageView cImg = ((ImageView) finalVkontakte.findViewById(R.id.vk_icon));
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN: {

                            if (listExist.contains(SocEnum.VK) && checkClick_vk[0]) {
                                checkClick_vk[0] = false;
                                int colorFrom = Color.parseColor("#507299");
                                int colorTo = Color.parseColor("#F9A825");
                                Drawable image = context.getResources().getDrawable(R.drawable.icn_social_vk2);
                                AnimColorMessenger.ActionDown_(colorFrom, colorTo, image, cImg);
                            } else {
                                checkClick_vk[0] = false;
                                int colorFrom = Color.parseColor("#e2e5e8");
                                int colorTo = Color.parseColor("#F9A825");
                                Drawable image = context.getResources().getDrawable(R.drawable.icn_social_vk2);
                                AnimColorMessenger.ActionDown_(colorFrom, colorTo, image, cImg);
                            }

                            break;
                        }
                        case MotionEvent.ACTION_UP: {
                            if (!checkClick_vk[0]) {
                                if (listExist.contains(SocEnum.VK)) {
                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#507299");
                                    Drawable image = context.getResources().getDrawable(R.drawable.icn_social_vk2);
                                    AnimColorMessenger.ActionUp_(colorFrom, colorTo, image, cImg);
                                } else {
                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#e2e5e8");
                                    Drawable image = context.getResources().getDrawable(R.drawable.icn_social_vk2);
                                    AnimColorMessenger.ActionUp_(colorFrom, colorTo, image, cImg);
                                }
                            }
                            break;
                        }
                        case MotionEvent.ACTION_CANCEL: {
                            if (listExist.contains(SocEnum.VK)) {
                                int colorFrom = Color.parseColor("#F9A825");
                                int colorTo = Color.parseColor("#507299");
                                Drawable image = context.getResources().getDrawable(R.drawable.icn_social_vk2);
                                AnimColorMessenger.ActionCancel_(colorFrom, colorTo, image, cImg);
                            } else {
                                int colorFrom = Color.parseColor("#F9A825");
                                int colorTo = Color.parseColor("#e2e5e8");
                                Drawable image = context.getResources().getDrawable(R.drawable.icn_social_vk2);
                                AnimColorMessenger.ActionCancel_(colorFrom, colorTo, image, cImg);
                            }
                            break;
                        }
                        case MotionEvent.ACTION_MOVE: {
                            if (!checkClick_vk[0]) {
                                if (listExist.contains(SocEnum.VK)) {
                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#507299");
                                    Drawable image = context.getResources().getDrawable(R.drawable.icn_social_vk2);
                                    checkClick_vk[0] = AnimColorMessenger.ActionMove_(colorFrom, colorTo, image, cImg, motionEvent, finalVkontakte);
                                } else {
                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#e2e5e8");
                                    Drawable image = context.getResources().getDrawable(R.drawable.icn_social_vk2);
                                    checkClick_vk[0] = AnimColorMessenger.ActionMove_(colorFrom, colorTo, image, cImg, motionEvent, finalVkontakte);
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

                        /*showSocialPopup(contact);
                        socialPopup.findViewById(R.id.vk_link).callOnClick();
                        socialPopup.setVisibility(View.GONE);*/

                    } else {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format(contact.getSocialModel().getVkLink())));
                        Intent intent2 = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("vkontakte://profile/")));
                        try {
                            context.startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            e.printStackTrace();
                            PackageManager packageManager = context.getPackageManager();
                            List<ResolveInfo> activities = packageManager.queryIntentActivities(intent2, 0);
                            boolean isIntentSafe = activities.size() > 0;
                            if (!isIntentSafe) {

                                try {
                                    String uris = contact.getSocialModel().getVkLink();
                                    if (!contact.getSocialModel().getVkLink().contains("https://") && !contact.getSocialModel().getVkLink().contains("http://"))
                                        uris = "https://" + uris;

                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                    i.setData(Uri.parse(uris));
                                    context.startActivity(i);
                                } catch (Exception e2) {

                                }
                            } else
                                context.startActivity(intent2);
                        }
                    }


                }
            });
        }


        if (Medium != null) {
            View finalMedium = Medium;
            Medium.findViewById(R.id.medium_icon).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {


                    ImageView cImg = ((ImageView) finalMedium.findViewById(R.id.medium_icon));
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN: {

                            if (listExist.contains(SocEnum.MEDIUM) && checkClick_medium[0]) {
                                checkClick_medium[0] = false;
                                int colorFrom = Color.parseColor("#000000");
                                int colorTo = Color.parseColor("#F9A825");
                                Drawable image = context.getResources().getDrawable(R.drawable.medium_white);
                                AnimColorMessenger.ActionDown_(colorFrom, colorTo, image, cImg);
                            } else {
                                checkClick_medium[0] = false;
                                int colorFrom = Color.parseColor("#e2e5e8");
                                int colorTo = Color.parseColor("#F9A825");
                                Drawable image = context.getResources().getDrawable(R.drawable.medium_white);
                                AnimColorMessenger.ActionDown_(colorFrom, colorTo, image, cImg);
                            }

                            break;
                        }
                        case MotionEvent.ACTION_UP: {
                            if (!checkClick_medium[0]) {
                                if (listExist.contains(SocEnum.MEDIUM)) {
                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#000000");
                                    Drawable image = context.getResources().getDrawable(R.drawable.medium_white);
                                    AnimColorMessenger.ActionUp_(colorFrom, colorTo, image, cImg);
                                } else {
                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#e2e5e8");
                                    Drawable image = context.getResources().getDrawable(R.drawable.medium_white);
                                    AnimColorMessenger.ActionUp_(colorFrom, colorTo, image, cImg);
                                }
                            }
                            break;
                        }
                        case MotionEvent.ACTION_CANCEL: {
                            if (listExist.contains(SocEnum.MEDIUM)) {
                                int colorFrom = Color.parseColor("#F9A825");
                                int colorTo = Color.parseColor("#000000");
                                Drawable image = context.getResources().getDrawable(R.drawable.medium_white);
                                AnimColorMessenger.ActionCancel_(colorFrom, colorTo, image, cImg);
                            } else {
                                int colorFrom = Color.parseColor("#F9A825");
                                int colorTo = Color.parseColor("#e2e5e8");
                                Drawable image = context.getResources().getDrawable(R.drawable.medium_white);
                                AnimColorMessenger.ActionCancel_(colorFrom, colorTo, image, cImg);
                            }
                            break;
                        }
                        case MotionEvent.ACTION_MOVE: {
                            if (!checkClick_medium[0]) {
                                if (listExist.contains(SocEnum.MEDIUM)) {
                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#000000");
                                    Drawable image = context.getResources().getDrawable(R.drawable.medium_white);
                                    checkClick_medium[0] = AnimColorMessenger.ActionMove_(colorFrom, colorTo, image, cImg, motionEvent, finalMedium);
                                } else {
                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#e2e5e8");
                                    Drawable image = context.getResources().getDrawable(R.drawable.medium_white);
                                    checkClick_medium[0] = AnimColorMessenger.ActionMove_(colorFrom, colorTo, image, cImg, motionEvent, finalMedium);
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

                       /* showSocialPopup(contact);
                        socialPopup.findViewById(R.id.medium_link).callOnClick();
                        socialPopup.setVisibility(View.GONE);*/

                    } else {

                        try {

                            String uri = contact.getSocialModel().getMediumLink();
                            if (!uri.contains("https://") && !uri.contains("http://"))
                                uri = "https://" + uri;

                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(uri));
                            context.startActivity(i);
                        } catch (Exception e) {

                        }
                    }


                }
            });
        }


        if (contact.listOfContacts == null || contact.listOfContacts.isEmpty()) {




            if (contact.hasWhatsapp && contact.getSocialModel() != null && contact.getSocialModel().getWhatsappLink() != null && !contact.getSocialModel().getWhatsappLink().isEmpty()) {
                Drawable color = new ColorDrawable(Color.parseColor("#75B73B"));
                Drawable image = context.getResources().getDrawable(R.drawable.whatsapp_new_3);
                LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
                ((CircleImageView) view.findViewById(R.id.whatsapp_icon)).setImageDrawable(ld);
            } else {
                Drawable color = new ColorDrawable(Color.parseColor("#e2e5e8"));
                Drawable image = context.getResources().getDrawable(R.drawable.whatsapp_new_3_gray);
                LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
                ((CircleImageView) view.findViewById(R.id.whatsapp_icon)).setImageDrawable(ld);
            }





            view.findViewById(R.id.whatsapp_icon).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {


                    CircleImageView cImg = ((CircleImageView) view.findViewById(R.id.whatsapp_icon));
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN: {
                            checkClick[0] = false;
                            if (contact.hasWhatsapp) {
                                int colorFrom = Color.parseColor("#75B73B");
                                int colorTo = Color.parseColor("#F9A825");
                                Drawable image = context.getResources().getDrawable(R.drawable.whatsapp_new_3);
                                AnimColorMessenger.ActionDown(colorFrom, colorTo, image, cImg);
                            } else {
                                int colorFrom = Color.parseColor("#e2e5e8");
                                int colorTo = Color.parseColor("#F9A825");
                                Drawable image = context.getResources().getDrawable(R.drawable.whatsapp_new_3_gray);
                                AnimColorMessenger.ActionDown(colorFrom, colorTo, image, cImg);
                            }
                            break;
                        }
                        case MotionEvent.ACTION_UP: {
                            if (!checkClick[0]) {
                                if (contact.hasWhatsapp) {
                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#75B73B");
                                    Drawable image = context.getResources().getDrawable(R.drawable.whatsapp_new_3);
                                    AnimColorMessenger.ActionUp(colorFrom, colorTo, image, cImg);
                                } else {
                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#e2e5e8");
                                    Drawable image = context.getResources().getDrawable(R.drawable.whatsapp_new_3_gray);
                                    AnimColorMessenger.ActionUp(colorFrom, colorTo, image, cImg);
                                }
                            }
                            break;
                        }
                        case MotionEvent.ACTION_CANCEL: {
                            if (contact.hasWhatsapp) {
                                int colorFrom = Color.parseColor("#F9A825");
                                int colorTo = Color.parseColor("#75B73B");
                                Drawable image = context.getResources().getDrawable(R.drawable.whatsapp_new_3);
                                AnimColorMessenger.ActionCancel(colorFrom, colorTo, image, cImg);
                            } else {
                                int colorFrom = Color.parseColor("#F9A825");
                                int colorTo = Color.parseColor("#e2e5e8");
                                Drawable image = context.getResources().getDrawable(R.drawable.whatsapp_new_3_gray);
                                AnimColorMessenger.ActionCancel(colorFrom, colorTo, image, cImg);
                            }
                            break;
                        }
                        case MotionEvent.ACTION_MOVE: {
                            if (!checkClick[0]) {
                                if (contact.hasWhatsapp) {
                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#75B73B");
                                    Drawable image = context.getResources().getDrawable(R.drawable.whatsapp_new_3);
                                    checkClick[0] = AnimColorMessenger.ActionMove(colorFrom, colorTo, image, cImg, motionEvent, mainView);
                                } else {
                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#e2e5e8");
                                    Drawable image = context.getResources().getDrawable(R.drawable.whatsapp_new_3_gray);
                                    checkClick[0] = AnimColorMessenger.ActionMove(colorFrom, colorTo, image, cImg, motionEvent, mainView);
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

                        /*showSocialPopup(contact);
                        socialPopup.findViewById(R.id.whatsapp_link).callOnClick();
                        socialPopup.setVisibility(View.GONE);*/

                    } else {
                        Intent telegramIntent = new Intent(Intent.ACTION_VIEW);



                        String whatsappNum = contact.getSocialModel().getWhatsappLink();
                        if (/*whatsappNum.substring(0, 2).equalsIgnoreCase("+8") ||*/ whatsappNum.charAt(0) == '8') {
                            whatsappNum = whatsappNum.replaceFirst("8", "7");
                        }

                        if (whatsappNum.charAt(0) != '+') whatsappNum = "+" + whatsappNum;

                        telegramIntent.setData(Uri.parse("whatsapp://send?phone=" + whatsappNum));
                        PackageManager packageManager = context.getPackageManager();
                        List<ResolveInfo> activities = packageManager.queryIntentActivities(telegramIntent, 0);
                        boolean isIntentSafe = activities.size() > 0;
                        if (!isIntentSafe) {
                            Intent intent2 = new Intent(Intent.ACTION_VIEW);
                            intent2.setData(Uri.parse("market://details?id=com.whatsapp"));
                            context.startActivity(intent2);
                        } else
                            context.startActivity(Intent.createChooser(telegramIntent, "Open with...").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                            //context.startActivity(telegramIntent);

                    }
                }
            });


            if (contact.hasTelegram && contact.getSocialModel() != null && contact.getSocialModel().getTelegramLink() != null && !contact.getSocialModel().getTelegramLink().isEmpty()) {
                Drawable color = new ColorDrawable(Color.parseColor("#7AA5DA"));
                Drawable image = context.getResources().getDrawable(R.drawable.telegram_new_1);
                LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
                ((CircleImageView) view.findViewById(R.id.telegram_icon)).setImageDrawable(ld);
            } else {
                Drawable color = new ColorDrawable(Color.parseColor("#e2e5e8"));
                Drawable image = context.getResources().getDrawable(R.drawable.telegram_new_1);
                LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
                ((CircleImageView) view.findViewById(R.id.telegram_icon)).setImageDrawable(ld);
            }



            view.findViewById(R.id.telegram_icon).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {


                    CircleImageView cImg = ((CircleImageView) view.findViewById(R.id.telegram_icon));
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN: {
                            checkClick_telegram[0] = false;
                            if ((contact.hasTelegram && contact.getSocialModel() != null && contact.getSocialModel().getTelegramLink() != null)) {
                                int colorFrom = Color.parseColor("#7AA5DA");
                                int colorTo = Color.parseColor("#F9A825");
                                Drawable image = context.getResources().getDrawable(R.drawable.telegram_new_1);
                                AnimColorMessenger.ActionDown(colorFrom, colorTo, image, cImg);
                            } else {
                                int colorFrom = Color.parseColor("#e2e5e8");
                                int colorTo = Color.parseColor("#F9A825");
                                Drawable image = context.getResources().getDrawable(R.drawable.telegram_new_1);
                                AnimColorMessenger.ActionDown(colorFrom, colorTo, image, cImg);
                            }
                            break;
                        }
                        case MotionEvent.ACTION_UP: {
                            if (!checkClick_telegram[0]) {
                                if ((contact.hasTelegram && contact.getSocialModel() != null && contact.getSocialModel().getTelegramLink() != null)) {
                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#7AA5DA");
                                    Drawable image = context.getResources().getDrawable(R.drawable.telegram_new_1);
                                    AnimColorMessenger.ActionUp(colorFrom, colorTo, image, cImg);
                                } else {
                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#e2e5e8");
                                    Drawable image = context.getResources().getDrawable(R.drawable.telegram_new_1);
                                    AnimColorMessenger.ActionUp(colorFrom, colorTo, image, cImg);
                                }
                            }
                            break;
                        }
                        case MotionEvent.ACTION_CANCEL: {
                            if ((contact.hasTelegram && contact.getSocialModel() != null && contact.getSocialModel().getTelegramLink() != null)) {
                                int colorFrom = Color.parseColor("#F9A825");
                                int colorTo = Color.parseColor("#7AA5DA");
                                Drawable image = context.getResources().getDrawable(R.drawable.telegram_new_1);
                                AnimColorMessenger.ActionCancel(colorFrom, colorTo, image, cImg);
                            } else {
                                int colorFrom = Color.parseColor("#F9A825");
                                int colorTo = Color.parseColor("#e2e5e8");
                                Drawable image = context.getResources().getDrawable(R.drawable.telegram_new_1);
                                AnimColorMessenger.ActionCancel(colorFrom, colorTo, image, cImg);
                            }
                            break;
                        }
                        case MotionEvent.ACTION_MOVE: {
                            if (!checkClick_telegram[0]) {
                                if ((contact.hasTelegram && contact.getSocialModel() != null && contact.getSocialModel().getTelegramLink() != null)) {
                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#7AA5DA");
                                    Drawable image = context.getResources().getDrawable(R.drawable.telegram_new_1);
                                    checkClick_telegram[0] = AnimColorMessenger.ActionMove(colorFrom, colorTo, image, cImg, motionEvent, mainView);
                                } else {
                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#e2e5e8");
                                    Drawable image = context.getResources().getDrawable(R.drawable.telegram_new_1);
                                    checkClick_telegram[0] = AnimColorMessenger.ActionMove(colorFrom, colorTo, image, cImg, motionEvent, mainView);
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



                    if ((!contact.hasTelegram) || (contact.hasTelegram && contact.getSocialModel() == null) || (contact.hasTelegram && contact.getSocialModel() != null && contact.getSocialModel().getTelegramLink() == null)) {

                       /* showSocialPopup(contact);
                        socialPopup.findViewById(R.id.telegramLink).callOnClick();
                        socialPopup.setVisibility(View.GONE);*/

                    } else {

                        String username = contact.getSocialModel().getTelegramLink();
                        char firstSymbol = username.charAt(0);
                        String regex = "[0-9]+";
                        username = username.replaceAll("[-() ]", "");
                        if ((firstSymbol == '+' && username.substring(1).matches(regex)) || (firstSymbol != '+' && username.matches(regex))) {
                            final String contactId = contact.getIdContact();
                            final String contactMimeTypeDataId = getContactMimeTypeDataId(context, contactId, "vnd.android.cursor.item/vnd.org.telegram.messenger.android.profile");
                            if (contactMimeTypeDataId != null) {
                                Intent intent;
                                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("content://com.android.contacts/data/" + contactMimeTypeDataId));
                                intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET | Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
                                intent.setPackage("org.telegram.messenger");
                                context.startActivity(intent);
                            } else {
                                Intent telegramIntent = new Intent(Intent.ACTION_VIEW);
                                telegramIntent.setData(Uri.parse("tg://resolve?domain=" + username));
                                PackageManager packageManager = context.getPackageManager();
                                List<ResolveInfo> activities = packageManager.queryIntentActivities(telegramIntent, 0);
                                boolean isIntentSafe = activities.size() > 0;
                                if (!isIntentSafe) {
                                    Intent intent3 = new Intent(Intent.ACTION_VIEW);
                                    intent3.setData(Uri.parse("market://details?id=org.telegram.messenger"));
                                    context.startActivity(intent3);
                                } else
                                    context.startActivity(telegramIntent);

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
                            PackageManager packageManager = context.getPackageManager();
                            List<ResolveInfo> activities = packageManager.queryIntentActivities(telegramIntent, 0);
                            boolean isIntentSafe = activities.size() > 0;
                            if (!isIntentSafe) {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse("market://details?id=org.telegram.messenger"));
                                context.startActivity(intent);
                            } else
                                context.startActivity(telegramIntent);
                        }

                    }
                }
            });



            if (contact.hasViber && contact.getSocialModel() != null && contact.getSocialModel().getViberLink() != null && !contact.getSocialModel().getViberLink().isEmpty()) {
                Drawable color = new ColorDrawable(Color.parseColor("#6F3FAA"));
                Drawable image = context.getResources().getDrawable(R.drawable.icn_social_viber2);
                LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
                ((CircleImageView) view.findViewById(R.id.viber_icon)).setImageDrawable(ld);
            } else {
                Drawable color = new ColorDrawable(Color.parseColor("#e2e5e8"));
                Drawable image = context.getResources().getDrawable(R.drawable.icn_social_viber2_gray);
                LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
                ((CircleImageView) view.findViewById(R.id.viber_icon)).setImageDrawable(ld);
            }



            view.findViewById(R.id.viber_icon).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {


                    CircleImageView cImg = ((CircleImageView) view.findViewById(R.id.viber_icon));
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN: {

                            checkClick_viber[0] = false;
                            if (contact.hasViber) {

                                int colorFrom = Color.parseColor("#6F3FAA");
                                int colorTo = Color.parseColor("#F9A825");

                                Drawable image = context.getResources().getDrawable(R.drawable.icn_social_viber2);

                                AnimColorMessenger.ActionDown(colorFrom, colorTo, image, cImg);
                            } else {
                                int colorFrom = Color.parseColor("#e2e5e8");
                                int colorTo = Color.parseColor("#F9A825");
                                Drawable image = context.getResources().getDrawable(R.drawable.icn_social_viber2_gray);
                                AnimColorMessenger.ActionDown(colorFrom, colorTo, image, cImg);
                            }
                            break;
                        }
                        case MotionEvent.ACTION_UP: {
                            if (!checkClick_viber[0]) {
                                if (contact.hasViber) {
                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#6F3FAA");
                                    Drawable image = context.getResources().getDrawable(R.drawable.icn_social_viber2);
                                    AnimColorMessenger.ActionUp(colorFrom, colorTo, image, cImg);
                                } else {
                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#e2e5e8");
                                    Drawable image = context.getResources().getDrawable(R.drawable.icn_social_viber2_gray);
                                    AnimColorMessenger.ActionUp(colorFrom, colorTo, image, cImg);
                                }
                            }
                            break;
                        }
                        case MotionEvent.ACTION_CANCEL: {
                            if (contact.hasViber) {
                                int colorFrom = Color.parseColor("#F9A825");
                                int colorTo = Color.parseColor("#6F3FAA");
                                Drawable image = context.getResources().getDrawable(R.drawable.icn_social_viber2);
                                AnimColorMessenger.ActionCancel(colorFrom, colorTo, image, cImg);
                            } else {
                                int colorFrom = Color.parseColor("#F9A825");
                                int colorTo = Color.parseColor("#e2e5e8");
                                Drawable image = context.getResources().getDrawable(R.drawable.icn_social_viber2_gray);
                                AnimColorMessenger.ActionCancel(colorFrom, colorTo, image, cImg);
                            }
                            break;
                        }
                        case MotionEvent.ACTION_MOVE: {
                            if (!checkClick_viber[0]) {
                                if (contact.hasViber) {
                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#6F3FAA");
                                    Drawable image = context.getResources().getDrawable(R.drawable.icn_social_viber2);
                                    checkClick_viber[0] = AnimColorMessenger.ActionMove(colorFrom, colorTo, image, cImg, motionEvent, mainView);
                                } else {
                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#e2e5e8");
                                    Drawable image = context.getResources().getDrawable(R.drawable.icn_social_viber2_gray);
                                    checkClick_viber[0] = AnimColorMessenger.ActionMove(colorFrom, colorTo, image, cImg, motionEvent, mainView);
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
                    if (!contact.hasViber || contact.getSocialModel() == null || contact.getSocialModel().getViberLink() == null || contact.getSocialModel().getViberLink().isEmpty()) {

                        /*showSocialPopup(contact);
                        socialPopup.findViewById(R.id.viberLink).callOnClick();
                        socialPopup.setVisibility(View.GONE);*/

                    } else {
                        Intent intent;
                        final String contactId = getContactIdFromPhoneNumber(String.valueOf(contact.getSocialModel().getViberLink()));
                        final String contactMimeTypeDataId = getContactMimeTypeDataId(context, contactId, "vnd.android.cursor.item/vnd.com.viber.voip.viber_number_message");
                        if (contactMimeTypeDataId != null) {
                            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("content://com.android.contacts/data/" + contactMimeTypeDataId));
                            intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET | Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
                            intent.setPackage("com.viber.voip");
                        } else {
                            intent = new Intent("android.intent.action.VIEW", Uri.parse("tel:" + Uri.encode(String.valueOf(contact.getSocialModel().getViberLink()))));
                            intent.setClassName("com.viber.voip", "com.viber.voip.WelcomeActivity");
                        }

                        PackageManager packageManager = context.getPackageManager();
                        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
                        boolean isIntentSafe = activities.size() > 0;
                        if (!isIntentSafe) {
                            Intent intent2 = new Intent(Intent.ACTION_VIEW);
                            intent2.setData(Uri.parse("market://details?id=com.viber.voip"));
                            context.startActivity(intent2);
                        } else
                            context.startActivity(intent);

                    }
                }
            });


            if (contact.hasSkype && contact.getSocialModel() != null && contact.getSocialModel().getSkypeLink() != null && !contact.getSocialModel().getSkypeLink().isEmpty()) {
                Drawable color = new ColorDrawable(Color.parseColor("#1eb8ff"));
                Drawable image = context.getResources().getDrawable(R.drawable.icn_social_skype2);
                LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
                ((CircleImageView) view.findViewById(R.id.skype_icon)).setImageDrawable(ld);
            } else {
                Drawable color = new ColorDrawable(Color.parseColor("#e2e5e8"));
                Drawable image = context.getResources().getDrawable(R.drawable.icn_social_skype2_gray);
                LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
                ((CircleImageView) view.findViewById(R.id.skype_icon)).setImageDrawable(ld);
            }




            view.findViewById(R.id.skype_icon).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {


                    CircleImageView cImg = ((CircleImageView) view.findViewById(R.id.skype_icon));

                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN: {
                            checkClick_skype[0] = false;
                            if (contact.hasSkype) {
                                int colorFrom = Color.parseColor("#1eb8ff");
                                int colorTo = Color.parseColor("#F9A825");
                                Drawable image = context.getResources().getDrawable(R.drawable.icn_social_skype2);
                                AnimColorMessenger.ActionDown(colorFrom, colorTo, image, cImg);
                            } else {
                                int colorFrom = Color.parseColor("#e2e5e8");
                                int colorTo = Color.parseColor("#F9A825");
                                Drawable image = context.getResources().getDrawable(R.drawable.icn_social_skype2_gray);
                                AnimColorMessenger.ActionDown(colorFrom, colorTo, image, cImg);
                            }
                            break;
                        }
                        case MotionEvent.ACTION_UP: {
                            if (!checkClick_skype[0]) {
                                if (contact.hasSkype) {
                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#1eb8ff");
                                    Drawable image = context.getResources().getDrawable(R.drawable.icn_social_skype2);
                                    AnimColorMessenger.ActionUp(colorFrom, colorTo, image, cImg);
                                } else {
                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#e2e5e8");
                                    Drawable image = context.getResources().getDrawable(R.drawable.icn_social_skype2_gray);
                                    AnimColorMessenger.ActionUp(colorFrom, colorTo, image, cImg);
                                }
                            }
                            break;
                        }
                        case MotionEvent.ACTION_CANCEL: {
                            if (contact.hasSkype) {
                                int colorFrom = Color.parseColor("#F9A825");
                                int colorTo = Color.parseColor("#1eb8ff");
                                Drawable image = context.getResources().getDrawable(R.drawable.icn_social_skype2);
                                AnimColorMessenger.ActionCancel(colorFrom, colorTo, image, cImg);
                            } else {
                                int colorFrom = Color.parseColor("#F9A825");
                                int colorTo = Color.parseColor("#e2e5e8");
                                Drawable image = context.getResources().getDrawable(R.drawable.icn_social_skype2_gray);
                                AnimColorMessenger.ActionCancel(colorFrom, colorTo, image, cImg);
                            }
                            break;
                        }
                        case MotionEvent.ACTION_MOVE: {
                            if (!checkClick_skype[0]) {
                                if (contact.hasSkype) {
                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#1eb8ff");
                                    Drawable image = context.getResources().getDrawable(R.drawable.icn_social_skype2);
                                    checkClick_skype[0] = AnimColorMessenger.ActionMove(colorFrom, colorTo, image, cImg, motionEvent, mainView);
                                } else {
                                    int colorFrom = Color.parseColor("#F9A825");
                                    int colorTo = Color.parseColor("#e2e5e8");
                                    Drawable image = context.getResources().getDrawable(R.drawable.icn_social_skype2_gray);
                                    checkClick_skype[0] = AnimColorMessenger.ActionMove(colorFrom, colorTo, image, cImg, motionEvent, mainView);
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

                       /* showSocialPopup(contact);
                        socialPopup.findViewById(R.id.skypeLink).callOnClick();
                        socialPopup.setVisibility(View.GONE);*/

                    } else {
                        Uri skypeUri = Uri.parse("skype:" + contact.getSocialModel().getSkypeLink() + "?chat");
                        Intent myIntent = new Intent(Intent.ACTION_VIEW, skypeUri);
                        myIntent.setComponent(new ComponentName("com.skype.raider", "com.skype.raider.Main"));
                        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        PackageManager packageManager = context.getPackageManager();
                        List<ResolveInfo> activities = packageManager.queryIntentActivities(myIntent, 0);
                        boolean isIntentSafe = activities.size() > 0;
                        if (!isIntentSafe) {
                            Intent intent2 = new Intent(Intent.ACTION_VIEW);
                            intent2.setData(Uri.parse("market://details?id=com.skype.raider"));
                            context.startActivity(intent2);
                        } else
                            context.startActivity(myIntent);



                    }
                }
            });


        }


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


    private String getContactIdFromPhoneNumber(String phone) {
        final Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phone));
        final ContentResolver contentResolver = context.getContentResolver();
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

    public static class KanBanViewHolder extends RecyclerView.ViewHolder {


        LinearLayout body;

        ImageView contactImage;
        TextView initials;

        ImageView type_image_contact;

        TextView time;

        TextView number_card;

        LinearLayout socialContactAdapter;

        TextView userName;

        LinearLayout card_phone_contact;
        LinearLayout email_contact;

        TextView text_phone_card, text_email_card;

        LinearLayout linear_card_phone_email;

        TextView textCompany, textPosition;

        LinearLayout containerHashTags;

        HorizontalScrollView scrollHorizontal;

        LinearLayout clickerPreview;

        SmartTabLayout result_tabs;

        ViewPager viewpager;

        FrameLayout prifile_contact_card, share_contact_card;

        FrameLayout clicker_1;

        RelativeLayout closerTabs;

        View line_card_contact_1;

        FrameLayout clicker_2;

        //==================== MEDIUM




        public KanBanViewHolder(@NonNull View itemView) {
            super(itemView);

            if(typeCard.equals(TypeCard.FULL) || typeCard.equals(TypeCard.SHORT)) {
                body = itemView.findViewById(R.id.body_card);

                contactImage = itemView.findViewById(R.id.contactCircleColor);
                initials = itemView.findViewById(R.id.contactInitials);

                type_image_contact = itemView.findViewById(R.id.type_image_contact);

                time = itemView.findViewById(R.id.cardTime);

                number_card = itemView.findViewById(R.id.number_card);

                socialContactAdapter = itemView.findViewById(R.id.socialContactAdapter);

                userName = itemView.findViewById(R.id.contactName);

                card_phone_contact = itemView.findViewById(R.id.card_phone_contact);
                email_contact = itemView.findViewById(R.id.email_contact);

                text_phone_card = itemView.findViewById(R.id.text_phone_card);
                text_email_card = itemView.findViewById(R.id.text_email_card);

                linear_card_phone_email = itemView.findViewById(R.id.linear_card_phone_email);

                textCompany = itemView.findViewById(R.id.textCompany);
                textPosition = itemView.findViewById(R.id.textPosition);

                containerHashTags = itemView.findViewById(R.id.containerHashTags);

                scrollHorizontal = itemView.findViewById(R.id.scrollHorizontal);

                clickerPreview = itemView.findViewById(R.id.clickerPreview);

                result_tabs = itemView.findViewById(R.id.result_tabs);

                viewpager = itemView.findViewById(R.id.viewpager);

                prifile_contact_card = itemView.findViewById(R.id.prifile_contact_card);

                share_contact_card = itemView.findViewById(R.id.share_contact_card);

                closerTabs = itemView.findViewById(R.id.closerTabs);

                line_card_contact_1 = itemView.findViewById(R.id.line_card_contact_1);

                clicker_1 = itemView.findViewById(R.id.clicker_1);

                clicker_2 = itemView.findViewById(R.id.clicker_2);

            }else{

                card_phone_contact = itemView.findViewById(R.id.card_phone_contact);
                email_contact = itemView.findViewById(R.id.email_contact);

                text_phone_card = itemView.findViewById(R.id.text_phone_card);
                text_email_card = itemView.findViewById(R.id.text_email_card);

                linear_card_phone_email = itemView.findViewById(R.id.linear_card_phone_email);

                number_card = itemView.findViewById(R.id.number_card);

                time = itemView.findViewById(R.id.cardTime);

            }


        }
    }
}
