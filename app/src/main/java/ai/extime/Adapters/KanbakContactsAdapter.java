package ai.extime.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
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
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.extime.R;

import org.greenrobot.eventbus.EventBus;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ai.extime.Activity.MainActivity;
import ai.extime.Events.AddHistoryEntry;
import ai.extime.Events.CloseOtherPopups;
import ai.extime.Events.PopupContact;
import ai.extime.Fragments.ContactsFragment;
import ai.extime.Fragments.ProfileFragment;
import ai.extime.Models.Contact;
import ai.extime.Models.ContactInfo;
import ai.extime.Models.HashTag;
import ai.extime.Models.UpdateCountClipboard;
import ai.extime.Utils.ClipboardType;
import de.hdodenhof.circleimageview.CircleImageView;

public class KanbakContactsAdapter extends RecyclerView.Adapter<KanbakContactsAdapter.ContactViewHolder> {

    private View mainView;

    private ArrayList<Contact> list;

    private Context context;

    private Activity activity;




    public void updateList(ArrayList<Contact> list){
        this.list = list;
        notifyDataSetChanged();
    }

    public KanbakContactsAdapter(ArrayList<Contact> list, Context context, Activity activity){

        this.list = list;
        this.context = context;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mainView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_contact, viewGroup, false);

        return new KanbakContactsAdapter.ContactViewHolder(mainView);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        final Contact contact = list.get(position);

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

            holder.time.setText(timeStr);
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (contact.isFavorite || contact.isImportant || contact.isFinished || contact.isPause || contact.isCrown || contact.isVip || contact.isStartup || contact.isInvestor) {
            holder.favotiveContact.setVisibility(View.VISIBLE);

            if (contact.isFavorite) {
                ((ImageView) holder.favotiveContact.findViewById(R.id.favoriteIcon2)).setImageDrawable(mainView.getContext().getResources().getDrawable(R.drawable.star));
            } else if (contact.isImportant) {
                ((ImageView) holder.favotiveContact.findViewById(R.id.favoriteIcon2)).setImageDrawable(mainView.getContext().getResources().getDrawable(R.drawable.checked_2));
            } else if (contact.isFinished) {
                ((ImageView) holder.favotiveContact.findViewById(R.id.favoriteIcon2)).setImageDrawable(mainView.getContext().getResources().getDrawable(R.drawable.finish_1));
            } else if (contact.isPause) {
                ((ImageView) holder.favotiveContact.findViewById(R.id.favoriteIcon2)).setImageDrawable(mainView.getContext().getResources().getDrawable(R.drawable.pause_1));
            }else if (contact.isCrown) {
                ((ImageView) holder.favotiveContact.findViewById(R.id.favoriteIcon2)).setImageDrawable(mainView.getContext().getResources().getDrawable(R.drawable.crown));
            }else if (contact.isVip) {
                ((ImageView) holder.favotiveContact.findViewById(R.id.favoriteIcon2)).setImageDrawable(mainView.getContext().getResources().getDrawable(R.drawable.vip_new));
            }else if (contact.isStartup) {
                ((ImageView) holder.favotiveContact.findViewById(R.id.favoriteIcon2)).setImageDrawable(mainView.getContext().getResources().getDrawable(R.drawable.startup));
            }else if (contact.isInvestor) {
                ((ImageView) holder.favotiveContact.findViewById(R.id.favoriteIcon2)).setImageDrawable(mainView.getContext().getResources().getDrawable(R.drawable.investor_));
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


        if (MainActivity.secretMode == 2 || MainActivity.secretMode == 3) {
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

        }

       /* if (contactsFragment.listNewContacts != null && contactsFragment.listNewContacts.contains(contact.getId())) {
            holder.isNew.setVisibility(View.VISIBLE);
        } else {
            holder.isNew.setVisibility(View.GONE);
        }*/

        setMargins(holder.linearNameIcons, 0, 52, 0, 0);

//        holder.userName.setGravity(View.TEXT_ALIGNMENT_CENTER);
//        holder.companyBlock.setVisibility(View.GONE);

        holder.companyName.setVisibility(View.GONE);
        holder.companyTitle.setVisibility(View.GONE);

        holder.itemView.findViewById(R.id.icon_search_contact_adapter).setVisibility(View.GONE);

        holder.companyTitle.setVisibility(View.VISIBLE);

        holder.userName.setText(contact.getName());

        holder.companyBlock.setVisibility(View.VISIBLE);

        /*if (searchString != "" && contact.getName().toLowerCase().contains(searchString.toLowerCase())) {

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

        }*/




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





            holder.imageBlock.setOnClickListener(v -> {
                if (ContactsFragment.
                        getQuantityOpenedViews() == 0) {

                    //contact.fillData(context, contactsFragment.contactsService, FillDataEnums.PROFILE);


                    if(((EditText)activity.findViewById(R.id.magic_edit_text)).getText().length() > 0){

                        EventBus.getDefault().post(new AddHistoryEntry(contact.getName()));
                    }

                    android.support.v4.app.FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();

                    android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.main_content, ProfileFragment.newInstance(contact, false)).addToBackStack("contacts").commit();

                    //contactAd = this;


                    if (activity.findViewById(R.id.profile_popup).getVisibility() == View.VISIBLE) {
                        activity.findViewById(R.id.profile_popup).setVisibility(View.INVISIBLE);
                    }
                } else {

                    ContactAdapter.selectedContactId = null;
                    EventBus.getDefault().post(new CloseOtherPopups());
                    //contactsFragment.closeOtherPopup();
                    notifyDataSetChanged();
                }
            });


            holder.itemView.setOnClickListener(v -> {

                if (ContactsFragment.getQuantityOpenedViews() == 0 && activity.findViewById(R.id.popupInfoClipboard).getVisibility() == View.GONE && activity.findViewById(R.id.popupEditClip).getVisibility() == View.GONE
                        && activity.findViewById(R.id.popupChangeType).getVisibility() == View.GONE) {

                    MainActivity.CloseStatusPopup();
                    if (contact.listOfContacts != null && !contact.listOfContacts.isEmpty()) {
                        ContactAdapter.selectedContactId = contact.getName();
                        EventBus.getDefault().post(new PopupContact(contact.getId()));
                        //contactsFragment.showCompanyPopup(contact);
                        //EventBus.getDefault().post(new Show);

                        if (ClibpboardAdapter.checkSelectClips) {

                            /*if (contactsFragment.getActivity() == null)
                                ((TextView) MainActivity.activityProfile.findViewById(R.id.updateContactClipboard)).setTextColor(contactsFragment.getResources().getColor(R.color.colorPrimary));
                            else*/
                            ((TextView) activity.findViewById(R.id.updateContactClipboard)).setTextColor(context.getResources().getColor(R.color.colorPrimary));
                        }

                    } else {
                        //contact.fillData(context, contactsFragment.contactsService, FillDataEnums.PREVIEW);
                        ContactAdapter.selectedContactId = String.valueOf(contact.getId());
                        //contactsFragment.showProfilePopUp(contact);
                        EventBus.getDefault().post(new PopupContact(contact.getId()));

                        if (ClibpboardAdapter.checkSelectClips) {

                            /*if (contactsFragment.getActivity() == null)
                                ((TextView) MainActivity.activityProfile.findViewById(R.id.updateContactClipboard)).setTextColor(contactsFragment.getResources().getColor(R.color.colorPrimary));
                            else*/
                            ((TextView) activity.findViewById(R.id.updateContactClipboard)).setTextColor(context.getResources().getColor(R.color.colorPrimary));
                        }

                    }

                    holder.itemView.setBackgroundColor(Color.parseColor("#E3E3E3"));
                } else {
                    activity.findViewById(R.id.popupInfoClipboard).setVisibility(View.GONE);
                    //contactsFragment.getActivity().findViewById(R.id.popupInfoLinkClipboard).setVisibility(View.GONE);
                    activity.findViewById(R.id.popupEditClip).setVisibility(View.GONE);
                    activity.findViewById(R.id.popupChangeType).setVisibility(View.GONE);

                    EventBus.getDefault().post(new UpdateCountClipboard());

                    /*if (contactsFragment.popupUserHashtags != null) {
                        if (contactsFragment.popupUserHashtags.getVisibility() == View.VISIBLE) {
                            contactsFragment.popupUserHashtags.setVisibility(View.GONE);
                        } else {
                            contactsFragment.closeOtherPopup();
                            notifyDataSetChanged();
                        }
                    } else {

                        //findViewById(R.id.framePopupSocial).setVisibility(View.GONE);

                        if (!ClibpboardAdapter.checkUpdateClips) {

                            *//*if (contactsFragment.getActivity() == null)
                                ((TextView) MainActivity.activityProfile.findViewById(R.id.updateContactClipboard)).setTextColor(contactsFragment.getResources().getColor(R.color.gray));
                            else*//*
                            ((TextView) contactsFragment.getActivity().findViewById(R.id.updateContactClipboard)).setTextColor(contactsFragment.getResources().getColor(R.color.gray));
                        }

                        ContactAdapter.selectedContactId = null;
                    }*/
                    EventBus.getDefault().post(new CloseOtherPopups());
                    notifyDataSetChanged();
                }
            });


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

            /*if (!selectionModeEnabled) {

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


            }*/
            return true;
        });

        /*holder.itemView.setOnLongClickListener(v -> {

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
        });*/
    }

    @Override
    public int getItemCount() {
        return list.size();
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
}
