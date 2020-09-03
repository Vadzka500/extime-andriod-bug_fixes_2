package ai.extime.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import ai.extime.Fragments.CompanyProfileDataFragment;
import ai.extime.Fragments.ContactsFragment;
import ai.extime.Fragments.ProfileFragment;
import ai.extime.Models.Contact;
import ai.extime.Models.ContactInfo;
import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmList;

public class Contacts_company_preview_adapter extends RecyclerView.Adapter<Contacts_company_preview_adapter.ContactsViewHolder> {

    private View mainView;

    private RealmList<Contact> listOfContacts;

    private Context context;

    private SharedPreferences mPref;

    private Fragment parentFragment;

    private Date currentDate;


    public Contacts_company_preview_adapter(RealmList<Contact> listOfContacts, Context context, Fragment fragment){
        this.listOfContacts = listOfContacts;
        this.context = context;
        mPref = context.getSharedPreferences("Sort", Context.MODE_PRIVATE);
        this.parentFragment = fragment;
        currentDate = new Date();
    }

    @NonNull
    @Override
    public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mainView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.contact_card_company_preview, viewGroup, false);



        return new ContactsViewHolder(mainView);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactsViewHolder holder, int i) {
        Contact contact = listOfContacts.get(i);



        holder.first_social.setVisibility(View.GONE);
        holder.second_social.setVisibility(View.GONE);

        if(contact.getCompanyPossition() != null && !contact.getCompanyPossition().isEmpty()){
            holder.positionSearch.setText(contact.getCompanyPossition());
            holder.linearComp.setVisibility(View.VISIBLE);
        }else{
            holder.linearComp.setVisibility(View.GONE);
        }


        try {
            holder.initials.setVisibility(View.GONE);
            holder.avatar.setVisibility(View.GONE);
            holder.avatar.setVisibility(View.VISIBLE);
            holder.avatar.setImageURI(Uri.parse(contact.getPhotoURL()));
            if (((BitmapDrawable) holder.avatar.getDrawable()).getBitmap() == null) {
                holder.avatar.setVisibility(View.VISIBLE);
                GradientDrawable circle = (GradientDrawable) ContextCompat.getDrawable(context, R.drawable.blue_circle).mutate();
                circle.setColor(contact.color);
                holder.avatar.setBackground(circle);
                holder.avatar.setImageDrawable(null);
                String initials = getInitials(contact);
                holder.initials.setVisibility(View.VISIBLE);
                holder.initials.setText(initials);
            }
        } catch (Exception e) {
            holder.avatar.setVisibility(View.VISIBLE);
            GradientDrawable circle = (GradientDrawable) ContextCompat.getDrawable(context, R.drawable.blue_circle).mutate();
            circle.setColor(contact.color);
            holder.avatar.setBackground(circle);
            holder.avatar.setImageDrawable(null);
            String initials = getInitials(contact);
            holder.initials.setVisibility(View.VISIBLE);
            holder.initials.setText(initials);
        }

        int count_social = 0;

        if(contact.getSocialModel() != null){
            while (true) {

                if (contact.getSocialModel().getFacebookLink() != null && !contact.getSocialModel().getFacebookLink().isEmpty()) {
                    holder.first_social.setImageDrawable(context.getResources().getDrawable(R.drawable.icn_social_facebook));
                    count_social++;
                }

                if (contact.getSocialModel().getTwitterLink() != null && !contact.getSocialModel().getTwitterLink().isEmpty()) {
                    if (count_social == 0) {
                        holder.first_social.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_twitter_32));
                    } else if (count_social == 1) {
                        holder.second_social.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_twitter_32));
                    }
                    count_social++;
                }

                if(count_social == 2) break;

                if (contact.getSocialModel().getLinkedInLink() != null && !contact.getSocialModel().getLinkedInLink().isEmpty()) {
                    if (count_social == 0) {
                        holder.first_social.setImageDrawable(context.getResources().getDrawable(R.drawable.icn_social_linkedin));
                    } else if (count_social == 1) {
                        holder.second_social.setImageDrawable(context.getResources().getDrawable(R.drawable.icn_social_linkedin));
                    }
                    count_social++;
                }

                if(count_social == 2) break;

                if (contact.getSocialModel().getInstagramLink() != null && !contact.getSocialModel().getInstagramLink().isEmpty()) {
                    if (count_social == 0) {
                        holder.first_social.setImageDrawable(context.getResources().getDrawable(R.drawable.instagram_adapter));
                    } else if (count_social == 1) {
                        holder.second_social.setImageDrawable(context.getResources().getDrawable(R.drawable.instagram_adapter));
                    }
                    count_social++;
                }

                if(count_social == 2) break;

                if (contact.getSocialModel().getYoutubeLink() != null && !contact.getSocialModel().getYoutubeLink().isEmpty()) {
                    if (count_social == 0) {
                        holder.first_social.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_youtube_48));
                    } else if (count_social == 1) {
                        holder.second_social.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_youtube_48));
                    }
                    count_social++;
                }

                if(count_social == 2) break;

                if (contact.getSocialModel().getVkLink() != null && !contact.getSocialModel().getVkLink().isEmpty()) {
                    if (count_social == 0) {
                        holder.first_social.setImageDrawable(context.getResources().getDrawable(R.drawable.icn_social_vk));
                    } else if (count_social == 1) {
                        holder.second_social.setImageDrawable(context.getResources().getDrawable(R.drawable.icn_social_vk));
                    }
                    count_social++;
                }

                if(count_social == 2) break;

                if (contact.getSocialModel().getMediumLink() != null && !contact.getSocialModel().getMediumLink().isEmpty()) {
                    if (count_social == 0) {
                        holder.first_social.setImageDrawable(context.getResources().getDrawable(R.drawable.medium_adapter));
                    } else if (count_social == 1) {
                        holder.second_social.setImageDrawable(context.getResources().getDrawable(R.drawable.medium_adapter));
                    }
                    count_social++;
                }

                break;
            }

            if(count_social == 1){
                holder.first_social.setVisibility(View.VISIBLE);
            }
            if(count_social == 2){
                holder.second_social.setVisibility(View.VISIBLE);
            }
        }

        //holder.linearName.requestLayout();




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
                //holder.isNew.setVisibility(View.GONE);
                holder.linear_card_phone_email.setVisibility(View.VISIBLE);
            } else holder.card_phone_contact.setVisibility(View.GONE);

            if (count_email != 0) {
                holder.email_contact.setVisibility(View.VISIBLE);
                holder.text_email_card.setText(String.valueOf(count_email));
               // holder.isNew.setVisibility(View.GONE);
                holder.linear_card_phone_email.setVisibility(View.VISIBLE);
            } else holder.email_contact.setVisibility(View.GONE);

        }

        holder.nameContact.setText(contact.getName());

        holder.nameContact.requestLayout();
        holder.linearName.requestLayout();
        holder.mainBox.requestLayout();

        holder.mainBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ((ContactsFragment) parentFragment).showProf(contact);
                }catch (Exception e){
                    try {
                        ((ProfileFragment) parentFragment).companyProfileDataFragment.showProfilePopUp(contact);
                    }catch (Exception e2){
                        e2.printStackTrace();
                    }
                }
            }
        });


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
        //sortTimeAsc = false;
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



    public void sortByTimeDesc() {
        //sortTimeAsc = true;
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

    @Override
    public int getItemCount() {
        return listOfContacts.size();
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

    public static class ContactsViewHolder extends RecyclerView.ViewHolder{

        CircleImageView avatar;
        TextView positionSearch, nameContact, initials;
        LinearLayout linearComp, mainBox, linearName;
        ImageView first_social, second_social;
        TextView time;

        LinearLayout linear_card_phone_email;

        LinearLayout card_phone_contact;
        LinearLayout email_contact;

        TextView text_phone_card, text_email_card;


        public ContactsViewHolder(@NonNull View itemView) {
            super(itemView);

            avatar = itemView.findViewById(R.id.contactCircleColor);
            positionSearch = itemView.findViewById(R.id.positionSearch);
            nameContact = itemView.findViewById(R.id.nameContact);
            linearComp = itemView.findViewById(R.id.linearComp);
            initials = itemView.findViewById(R.id.contactInitials);

            first_social = itemView.findViewById(R.id.first_icon_social_contacts_company_preview);
            second_social = itemView.findViewById(R.id.second_icon_social_contacts_company_preview);

            mainBox = itemView.findViewById(R.id.linearNameSearch);

            linearName = itemView.findViewById(R.id.linearName);

            time = (TextView) itemView.findViewById(R.id.cardTime);

            text_phone_card = itemView.findViewById(R.id.text_phone_card);
            text_email_card = itemView.findViewById(R.id.text_email_card);

            email_contact = itemView.findViewById(R.id.email_contact);

            linear_card_phone_email = itemView.findViewById(R.id.linear_card_phone_email);

            card_phone_contact = itemView.findViewById(R.id.card_phone_contact);




        }
    }
}
