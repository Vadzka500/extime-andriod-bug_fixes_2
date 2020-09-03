package ai.extime.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.extime.R;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import ai.extime.Activity.MainActivity;
import ai.extime.Fragments.CompanyProfileDataFragment;
import ai.extime.Fragments.ProfileFragment;
import ai.extime.Models.Contact;
import ai.extime.Models.ContactInfo;
import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmList;

public class CompanyContactAdapterNewTab extends RecyclerView.Adapter<CompanyContactAdapterNewTab.CompanyContactAdapterNewTabViewHolder> {

    private RealmList<Contact> listOfContacts;

    private View mainView;

    private Date currentDate;

    private Context context;

    private Fragment parentFragment;

    public static boolean selectionModeCompany = false;

    public SharedPreferences mPref;

    private Contact selectedContact;

    public CompanyContactAdapterNewTab(RealmList<Contact> list, Context context, Fragment fragment, Contact selectedContact) {
        this.listOfContacts = list;
        this.context = context;
        this.parentFragment = fragment;
        this.currentDate = new Date();
        this.selectedContact = selectedContact;
        mPref = context.getSharedPreferences("Sort", Context.MODE_PRIVATE);
    }
    private boolean sortTimeAsc = false;

    public void sortByDesc() {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString("typeSortCompany", "sortByDesc");
        editor.apply();
        Realm realm = Realm.getDefaultInstance(); //+
        realm.beginTransaction();
        Collections.sort(listOfContacts, (contactFirst, contactSecond) -> contactSecond.getName().compareToIgnoreCase(contactFirst.getName()));
        realm.commitTransaction();
        realm.close();

        notifyDataSetChanged();
    }

    public void sortByTimeAsc() {
        sortTimeAsc = false;
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString("typeSortCompany", "sortByTimeAsc");
        editor.apply();
        Realm realm = Realm.getDefaultInstance(); //+
        realm.beginTransaction();
        Collections.sort(listOfContacts, new Comparator<Contact>() {
            @Override
            public int compare(Contact o1, Contact o2) {

                return o1.getDateCreate().compareTo(o2.getDateCreate());
            }
        });
        realm.commitTransaction();
        realm.close();

        notifyDataSetChanged();
    }

    public void sortByAsc() {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString("typeSortCompany", "sortByAsc");
        editor.apply();
        Realm realm = Realm.getDefaultInstance(); //+
        try {

            realm.beginTransaction();
            Collections.sort(listOfContacts, (contactFirst, contactSecond) -> contactFirst.getName().compareToIgnoreCase(contactSecond.getName()));
            realm.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
        }



        realm.close();

        notifyDataSetChanged();
    }



    public void sortByTimeDesc() {
        sortTimeAsc = true;
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString("typeSortCompany", "sortByTimeDesc");
        editor.apply();
        Realm realm = Realm.getDefaultInstance(); //+
        realm.beginTransaction();
        Collections.sort(listOfContacts, new Comparator<Contact>() {
            @Override
            public int compare(Contact o1, Contact o2) {
                return o2.getDateCreate().compareTo(o1.getDateCreate());
            }
        });
        realm.commitTransaction();
        realm.close();

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CompanyContactAdapterNewTabViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mainView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_contact, viewGroup, false);
        return new CompanyContactAdapterNewTabViewHolder(mainView);
    }

    @Override
    public void onBindViewHolder(@NonNull CompanyContactAdapterNewTabViewHolder holder, int i) {

        final Contact contact = listOfContacts.get(i);


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
                ((ImageView) holder.favotiveContact.findViewById(R.id.favoriteIcon2)).setImageDrawable(context.getResources().getDrawable(R.drawable.star));
            } else if (contact.isImportant) {
                ((ImageView) holder.favotiveContact.findViewById(R.id.favoriteIcon2)).setImageDrawable(context.getResources().getDrawable(R.drawable.checked_2));
            } else if (contact.isFinished) {
                ((ImageView) holder.favotiveContact.findViewById(R.id.favoriteIcon2)).setImageDrawable(context.getResources().getDrawable(R.drawable.finish_1));
            } else if (contact.isPause) {
                ((ImageView) holder.favotiveContact.findViewById(R.id.favoriteIcon2)).setImageDrawable(context.getResources().getDrawable(R.drawable.pause_1));
            } else if (contact.isCrown) {
                ((ImageView) holder.favotiveContact.findViewById(R.id.favoriteIcon2)).setImageDrawable(context.getResources().getDrawable(R.drawable.crown));
            } else if (contact.isVip) {
                ((ImageView) holder.favotiveContact.findViewById(R.id.favoriteIcon2)).setImageDrawable(context.getResources().getDrawable(R.drawable.vip_new));
            } else if (contact.isStartup) {
                ((ImageView) holder.favotiveContact.findViewById(R.id.favoriteIcon2)).setImageDrawable(context.getResources().getDrawable(R.drawable.startup));
            } else if (contact.isInvestor) {
                ((ImageView) holder.favotiveContact.findViewById(R.id.favoriteIcon2)).setImageDrawable(context.getResources().getDrawable(R.drawable.investor_));
            }


            if (type == 1) {

                int px_2 = (int) (17 * context.getResources().getDisplayMetrics().density);
                int px_padding = (int) (54 * context.getResources().getDisplayMetrics().density);

                holder.linearDataCard.setPadding(0, 0, px_padding + px_2, 0);
                holder.linearDataCard.requestLayout();
            } else if (type == 2) {

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


        } else {
            holder.favotiveContact.setVisibility(View.GONE);   //new


            if (type == 1) {

                int px_padding = (int) (54 * context.getResources().getDisplayMetrics().density);

                holder.linearDataCard.setPadding(0, 0, px_padding, 0);
                holder.linearDataCard.requestLayout();
            } else if (type == 2) {

                int px_padding = (int) (60 * context.getResources().getDisplayMetrics().density);

                holder.linearDataCard.setPadding(0, 0, px_padding, 0);
                holder.linearDataCard.requestLayout();
            } else if (type == 3) {
                int px_padding = (int) (68 * context.getResources().getDisplayMetrics().density);

                holder.linearDataCard.setPadding(0, 0, px_padding, 0);
                holder.linearDataCard.requestLayout();
            }

        }


        //if (MainActivity.secretMode == 2 || MainActivity.secretMode == 3) {
            int countSocials = 0;


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

            //if (MainActivity.secretMode == 3) {

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
          //  }

        /*} else if (MainActivity.secretMode == 1) {


        }*/

        /*if (contactsFragment.listNewContacts != null && contactsFragment.listNewContacts.contains(contact.getId())) {
            holder.isNew.setVisibility(View.VISIBLE);
        } else {*/
        holder.isNew.setVisibility(View.GONE);
        //  }

        setMargins(holder.linearNameIcons, 0, 52, 0, 0);


        holder.companyName.setVisibility(View.GONE);
        holder.companyTitle.setVisibility(View.GONE);


        holder.companyTitle.setVisibility(View.VISIBLE);


       /* if (searchString != "") {

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
        } else {*/

        holder.userName.setText(contact.getName());

        //    }

        holder.userName.requestLayout();


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


        holder.imageSelect.setVisibility(View.GONE);
        holder.companyTitle.setText("");

        if (contact.listOfContacts != null && !contact.listOfContacts.isEmpty()) {
            setMargins(holder.linearNameIcons, 0, 0, 0, 0);
            holder.companyTitle.setVisibility(View.VISIBLE);
            holder.companyTitle.setText(contact.listOfContacts.size() + " contacts");
        }


        holder.companyTitle.setVisibility(View.GONE);

        if (contact.getCompanyPossition() != null) {
            if (contact.getCompanyPossition().compareTo("") != 0) {
                setMargins(holder.linearNameIcons, 0, 0, 0, 0);
                holder.companyName.setVisibility(View.VISIBLE);
                Double ems = holder.companyTitle.getText().length() / 2.5;
                int ems_count = ems.intValue();

                holder.companyName.setText(contact.getCompanyPossition());
            }
        }

        if (holder.companyTitle.length() > 0 && holder.companyName.getVisibility() == View.VISIBLE) {
            Double ems = holder.companyName.getText().length() / 2.5;
            int ems_count = ems.intValue();

        }


        holder.linearDataCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((ProfileFragment) parentFragment).getQuantityOpenedViews() == 0) {
                    //contact.fillData(context, contactsService, FillDataEnums.PREVIEW);
                    ((ProfileFragment) parentFragment).showProfilePopUp(contact.getId());
                    holder.itemView.setBackgroundColor(Color.parseColor("#E3E3E3"));
                } else {
                    ((ProfileFragment) parentFragment).closeOtherPopup();
                    notifyDataSetChanged();
                }
            }
        });


        holder.contactImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //contact.fillData(context, contactsService, FillDataEnums.PROFILE);

                ((ProfileFragment) parentFragment).closeOtherPopup();

                android.support.v4.app.FragmentManager fragmentManager = parentFragment.getActivity().getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_content, ProfileFragment.newInstance(contact, false)).addToBackStack("contact").commit();


            }
        });






        /*if (!ContactAdapter.selectionModeEnabled) {
            if (!selectionModeCompany) {

                holder.containerHashTagsCompany.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

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
                });

                holder.containerHashTagsCompany.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {

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

                        return false;
                    }
                });

                holder.card.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {

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

                        return false;
                    }
                });

                holder.contactPhoto.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {


                            startSelectionMode();
                            selectedContacts.add(contact);

                            toolbarC.findViewById(R.id.toolbar_title).setVisibility(View.GONE);


                            ((TextView) toolbarC.findViewById(R.id.cancel_toolbar)).setVisibility(View.VISIBLE);
                            ((TextView) toolbarC.findViewById(R.id.cancel_toolbar)).setTextSize(18);
                            ((TextView) toolbarC.findViewById(R.id.cancel_toolbar)).setText("" + selectedContacts.size() + "");

                        return false;
                    }
                });


                holder.card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                            if (((CompanyProfileDataFragment) parentFragment).getQuantityOpenedViews() == 0) {
                                //contact.fillData(context, contactsService, FillDataEnums.PREVIEW);
                                ((CompanyProfileDataFragment) parentFragment).showProfilePopUp(contact);
                                holder.itemView.setBackgroundColor(Color.parseColor("#E3E3E3"));
                            } else {
                                ((CompanyProfileDataFragment) parentFragment).closeOtherPopup();
                                notifyDataSetChanged();
                            }

                    }
                });

                holder.contactPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                            //contact.fillData(context, contactsService, FillDataEnums.PROFILE);
                            android.support.v4.app.FragmentManager fragmentManager = parentFragment.getActivity().getSupportFragmentManager();
                            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.main_content, ProfileFragment.newInstance(contact, false)).addToBackStack("company").commit();

                            ((CompanyProfileDataFragment) parentFragment).closeOtherPopup();

                    }
                });


            } else {

                holder.containerHashTagsCompany.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {

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

                        return false;
                    }
                });


                holder.card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

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
                });


                holder.contactPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                            if (((CompanyProfileDataFragment) parentFragment).getQuantityOpenedViews() == 0) {
                                //contact.fillData(context, contactsService, FillDataEnums.PREVIEW);
                                ((CompanyProfileDataFragment) parentFragment).showProfilePopUp(contact);
                                //    holder.itemView.setBackgroundColor(Color.parseColor("#E3E3E3"));
                            } else {
                                ((CompanyProfileDataFragment) parentFragment).closeOtherPopup();
                                notifyDataSetChanged();
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


                        if (((CompanyProfileDataFragment) parentFragment).getQuantityOpenedViews() == 0) {
                            //contact.fillData(context, contactsService, FillDataEnums.PREVIEW);
                            ((CompanyProfileDataFragment) parentFragment).showProfilePopUp(contact);
                            holder.itemView.setBackgroundColor(Color.parseColor("#E3E3E3"));
                        } else {
                            ((CompanyProfileDataFragment) parentFragment).closeOtherPopup();
                            notifyDataSetChanged();
                        }

                }
            });

            holder.contactPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(holder.frameOld.getVisibility() == View.VISIBLE) {
                        //contact.fillData(context, contactsService, FillDataEnums.PROFILE);
                        android.support.v4.app.FragmentManager fragmentManager = parentFragment.getActivity().getSupportFragmentManager();
                        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.main_content, ProfileFragment.newInstance(contact, false)).addToBackStack("company").commit();

                        ((CompanyProfileDataFragment) parentFragment).closeOtherPopup();
                    }
                }
            });
        }
*/


    }

    private void setMargins(View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
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

    public static class CompanyContactAdapterNewTabViewHolder extends RecyclerView.ViewHolder {

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


        LinearLayout containerHashTagsCompany;

        LinearLayout searchBlock;
        TextView searchText, searchTextCount;

        public CompanyContactAdapterNewTabViewHolder(@NonNull View itemView) {
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

            containerHashTagsCompany = itemView.findViewById(R.id.containerHashTagsCompany);

            searchBlock = itemView.findViewById(R.id.searchBlock);
            searchText = itemView.findViewById(R.id.searchText);
            searchTextCount = itemView.findViewById(R.id.searchTextCount);
        }
    }
}
