package ai.extime.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import ai.extime.Fragments.CreateFragment;
import ai.extime.Fragments.ProfileFragment;
import ai.extime.Models.Contact;
import ai.extime.Models.ContactInfo;
import ai.extime.Models.HashTag;
import ai.extime.Models.SocialModel;
import ai.extime.Utils.ClipboardType;
import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;

public class SearchContactByNameAdaper extends RecyclerView.Adapter<SearchContactByNameAdaper.SearchViewHolder> {

    public ArrayList<Contact> listContacts;

    private View mainView;

    private Context context;

    private String search;

    private CreateFragment createFragment;

    private FragmentManager fragmentManager;



    public SearchContactByNameAdaper(ArrayList<Contact> list, Context context, String search, CreateFragment fragment, FragmentManager fragmentManager) {
        this.listContacts = list;
        this.context = context;
        this.search = search;
        this.createFragment = fragment;
        this.fragmentManager = fragmentManager;


    }

    public void updateList(ArrayList<Contact> list, String search) {
        this.listContacts = list;
        notifyDataSetChanged();
        this.search = search;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mainView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_search_name_contact, viewGroup, false);
        return new SearchViewHolder(mainView);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int i) {
        Contact c = listContacts.get(i);



        if((c.getCompany() == null || c.getCompany().isEmpty()) && (c.getCompanyPossition() == null || c.getCompanyPossition().isEmpty())){
            holder.linearCompany.setVisibility(View.GONE);
        }else{
            holder.linearCompany.setVisibility(View.VISIBLE);
            if(c.getCompany() != null && !c.getCompany().isEmpty()){
                holder.company.setText(c.getCompany());
                holder.company.setVisibility(View.VISIBLE);
            }else{
                holder.company.setVisibility(View.GONE);
            }
            if(c.getCompanyPossition() != null && !c.getCompanyPossition().isEmpty()){
                holder.positionContact.setText(c.getCompanyPossition());
                holder.positionContact.setVisibility(View.VISIBLE);
            }else{
                holder.positionContact.setVisibility(View.GONE);
            }
        }


        int startI = c.getName().toLowerCase().indexOf(search.toLowerCase());
        final SpannableStringBuilder text = new SpannableStringBuilder(c.getName());
        final ForegroundColorSpan style = new ForegroundColorSpan(Color.parseColor("#1976D2"));
        StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
        text.setSpan(style, startI, startI + search.length(), Spannable.SPAN_COMPOSING);
        text.setSpan(bss, startI, startI + search.length(), Spannable.SPAN_COMPOSING);

        holder.nameContact.setText(text);

        try {
            holder.initials.setVisibility(View.GONE);
            holder.circle.setVisibility(View.GONE);
            holder.circle.setVisibility(View.VISIBLE);
            holder.circle.setImageURI(Uri.parse(c.getPhotoURL()));
            if (((BitmapDrawable) holder.circle.getDrawable()).getBitmap() == null) {
                holder.circle.setVisibility(View.VISIBLE);
                GradientDrawable circle = (GradientDrawable) ContextCompat.getDrawable(context, R.drawable.blue_circle).mutate();
                circle.setColor(c.color);
                holder.circle.setBackground(circle);
                holder.circle.setImageDrawable(null);
                String initials = getInitials(c);
                holder.initials.setVisibility(View.VISIBLE);
                holder.initials.setText(initials);
            }
        } catch (Exception e) {
            holder.circle.setVisibility(View.VISIBLE);
            GradientDrawable circle = (GradientDrawable) ContextCompat.getDrawable(context, R.drawable.blue_circle).mutate();
            circle.setColor(c.color);
            holder.circle.setBackground(circle);
            holder.circle.setImageDrawable(null);
            String initials = getInitials(c);
            holder.initials.setVisibility(View.VISIBLE);
            holder.initials.setText(initials);
        }


        holder.mainBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);
                alertDialogBuilder.setTitle("Do you want to update contact?");
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Update", (dialog, id) -> {


                            Contact newContact = new Contact();
                            newContact.setId(-1);
                            newContact.setIdContact("-1");
                            newContact.setName(" ");

                            RealmList<HashTag> listOfHashTag = new RealmList<HashTag>();
                            ArrayList<String> hashtags = new ArrayList<>(Arrays.asList(createFragment.hashTagList.getText().toString().split(" ")));

                            for (String hashTag : hashtags) {
                                if (hashTag != "" && hashTag.length() > 1)
                                    listOfHashTag.add(new HashTag(hashTag.toLowerCase().trim()));
                            }

                            newContact.setListOfHashtags(listOfHashTag);

                            if (createFragment.contactProfileDataFragment.contactNumberEditAdapter != null && createFragment.contactProfileDataFragment.contactNumberEditAdapter.getContactInfos() != null)
                                newContact.listOfContactInfo.addAll(createFragment.contactProfileDataFragment.contactNumberEditAdapter.getContactInfos());

                            for (ContactInfo i : newContact.getListOfContactInfo()) {
                                if (i.value.equals("+000000000000")) {
                                    newContact.getListOfContactInfo().remove(i);

                                    break;
                                }
                            }

                            newContact.listOfContacts = null;

                            if (createFragment.companyContact.getText().toString().trim().length() > 0) {
                                newContact.setCompany(createFragment.companyContact.getText().toString().trim());
                            } else newContact.setCompany(null);

                            if (createFragment.possitionCompanyContact.getText().toString().trim().length() > 0) {
                                newContact.setCompanyPossition(createFragment.possitionCompanyContact.getText().toString().trim());
                            } else newContact.setCompanyPossition(null);


                            newContact.setSocialModel(CreateFragment.createdContact.getSocialModel());

                            ArrayList<Contact> list = new ArrayList<>();
                            list.add(c);
                            list.add(newContact);

                            createFragment.popupName.setVisibility(View.GONE);

                            ContactAdapter.checkMergeContacts = true;

                            fragmentManager.popBackStack();

                            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.main_content, CreateFragment.newInstance(list)).addToBackStack("create").commit();


                        })
                        .setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return listContacts.size();
    }

    public void sortCompanybyAsc() {
        try {
            Collections.sort(listContacts, (contactFirst, contactSecond) -> contactFirst.getName().toString().trim().compareToIgnoreCase(contactSecond.getName().toString().trim()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        notifyDataSetChanged();
    }

    public void sortCompanybyDesc() {
        try {
            Collections.sort(listContacts, (contactFirst, contactSecond) -> contactSecond.getName().toString().trim().compareToIgnoreCase(contactFirst.getName().toString().trim()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        notifyDataSetChanged();
    }

    public void sortByAscTime() {
        Collections.sort(listContacts, (hashTagFirst, hashTagSecond) -> hashTagFirst.getDateCreate().compareTo(hashTagSecond.getDateCreate()));
        notifyDataSetChanged();
    }

    public void sortByDescTime() {
        Collections.sort(listContacts, (hashTagFirst, hashTagSecond) -> hashTagSecond.getDateCreate().compareTo(hashTagFirst.getDateCreate()));
        notifyDataSetChanged();
    }

    /*public void sortByDescPopul() {
        Collections.sort(listOfCompanies, new Comparator<Contact>() {
            @Override
            public int compare(Contact first, Contact second) {
                return second.listOfContacts.size() - first.listOfContacts.size();
            }
        });
        notifyDataSetChanged();
    }

    public void sortByAscPopul() {
        Collections.sort(listOfCompanies, new Comparator<Contact>() {
            @Override
            public int compare(Contact first, Contact second) {
                return first.listOfContacts.size() - second.listOfContacts.size();
            }
        });
        notifyDataSetChanged();
    }*/

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

    private String getInitialsByName(String name) {
        String initials = "";
        if (name != null && !name.isEmpty()) {
            String names[] = name.split("\\s+");
            for (String namePart : names) {
                if (namePart != null && namePart.length() > 0)
                    initials += namePart.charAt(0);
            }
        }
        return initials.toUpperCase();
    }

    public static class SearchViewHolder extends RecyclerView.ViewHolder {

        CircleImageView circle;
        TextView nameContact;
        TextView initials;
        LinearLayout mainBox;
        //FrameLayout frameCompany;
        ImageView imageCompany;
        //TextView nameCompany;
        //TextView initialsCompany;

        LinearLayout linearCompany;
        TextView company;
        TextView positionContact;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            circle = itemView.findViewById(R.id.contactCircleColor);
            initials = itemView.findViewById(R.id.contactInitials);
            nameContact = itemView.findViewById(R.id.nameContact);
            mainBox = itemView.findViewById(R.id.linearNameSearch);
            //frameCompany = itemView.findViewById(R.id.frameCompany);
            imageCompany = itemView.findViewById(R.id.companyPhoto);
            //nameCompany = itemView.findViewById(R.id.nameCompany);
            //initialsCompany = itemView.findViewById(R.id.companyInitials);

            linearCompany = itemView.findViewById(R.id.linearComp);
            company = itemView.findViewById(R.id.companySearch);
            positionContact = itemView.findViewById(R.id.positionSearch);
        }
    }

}
