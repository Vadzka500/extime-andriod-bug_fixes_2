package ai.extime.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
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
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.extime.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import ai.extime.Fragments.CreateFragment;
import ai.extime.Interfaces.IAddRecipient;
import ai.extime.Models.Contact;
import ai.extime.Models.ContactInfo;
import ai.extime.Models.HashTag;
import ai.extime.Utils.ClipboardType;
import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.RealmList;

public class SearchContactWithEmailAdapter extends RecyclerView.Adapter<SearchContactWithEmailAdapter.SearchViewHolder> {

    public ArrayList<Contact> listContacts;

    private View mainView;

    private Context context;

    private String search;

    private IAddRecipient iAddRecipient;


    public SearchContactWithEmailAdapter(ArrayList<Contact> list, Context context, String search, IAddRecipient iAddRecipient) {
        this.listContacts = list;
        this.context = context;
        this.search = search;
        this.iAddRecipient = iAddRecipient;

    }

    public void updateSearch(String text, ArrayList<Contact> list) {
        this.listContacts = list;
        search = text;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mainView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_contact_with_email, viewGroup, false);
        return new SearchContactWithEmailAdapter.SearchViewHolder(mainView);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int i) {
        Contact c = listContacts.get(i);


        holder.linearCompany.setVisibility(View.VISIBLE);
        if (c.getCompany() != null && !c.getCompany().isEmpty()) {
            holder.company.setText(c.getCompany());
            holder.company.setVisibility(View.VISIBLE);
        } else {
            holder.company.setVisibility(View.GONE);
        }

            /*if (c.getCompanyPossition() != null && !c.getCompanyPossition().isEmpty()) {
                holder.positionContact.setText(c.getCompanyPossition());
                holder.positionContact.setVisibility(View.VISIBLE);
            } else {
                holder.positionContact.setVisibility(View.GONE);
            }*/


        //holder.emailSearch.setText("");
        int countE = 0;

        holder.emailSearchLinear.removeAllViews();

        for (ContactInfo ci : c.getListOfContactInfo()) {
            if (ClipboardType.isEmail(ci.value) && ci.isPrimary) {
                countE++;
                //holder.emailSearch.append(ci.value);
                //holder.emailSearch.append("  ");


                TextView text = new TextView(context);
                text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);
                DisplayMetrics metrics = mainView.getContext().getResources().getDisplayMetrics();
                text.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                text.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                text.setText(ci.value + " ");

                text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        iAddRecipient.addRecipient(c.getName(), ci.value);
                    }
                });

                holder.emailSearchLinear.addView(text);

                int px_n = (int) (8 * context.getResources().getDisplayMetrics().density);
                int px_t = (int) (2 * context.getResources().getDisplayMetrics().density);
                ImageView imageView = new ImageView(context);

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        px_n, px_n);

                lp.topMargin = px_t;

                imageView.setLayoutParams(lp);
                imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.tick));
                imageView.setColorFilter(ContextCompat.getColor(context, R.color.primary), PorterDuff.Mode.SRC_IN);




                holder.emailSearchLinear.addView(imageView);


                TextView text2 = new TextView(context);
                text2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);

                text2.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                text2.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                text2.setText("  ");

                holder.emailSearchLinear.addView(text2);


            }
        }

        for (ContactInfo ci : c.getListOfContactInfo()) {
            if (ClipboardType.isEmail(ci.value) && !ci.isPrimary) {
                countE++;
                //holder.emailSearch.append(ci.value);
                //holder.emailSearch.append("  ");


                TextView text = new TextView(context);
                text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);
                DisplayMetrics metrics = mainView.getContext().getResources().getDisplayMetrics();
                text.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                text.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                text.setText(ci.value + "  ");

                text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        iAddRecipient.addRecipient(c.getName(), ci.value);
                    }
                });

                holder.emailSearchLinear.addView(text);


            }
        }

        if (countE < 2) {
            holder.count_email.setVisibility(View.GONE);
        } else {
            holder.count_email.setText(String.valueOf(countE));
            holder.count_email.setVisibility(View.VISIBLE);
        }


        if (c.getName().toLowerCase().contains(search.toLowerCase())) {
            int startI = c.getName().toLowerCase().indexOf(search.toLowerCase());
            final SpannableStringBuilder text = new SpannableStringBuilder(c.getName());
            final ForegroundColorSpan style = new ForegroundColorSpan(Color.parseColor("#1976D2"));
            StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
            text.setSpan(style, startI, startI + search.length(), Spannable.SPAN_COMPOSING);
            text.setSpan(bss, startI, startI + search.length(), Spannable.SPAN_COMPOSING);

            holder.nameContact.setText(text);

        } else {
            holder.nameContact.setText(c.getName());
        }

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
                for (ContactInfo ci : c.getListOfContactInfo()) {
                    if (ClipboardType.isEmail(ci.value) && ci.isPrimary) {
                        iAddRecipient.addRecipient(c.getName(), ci.value);
                        return;
                    }
                }

                for (ContactInfo ci : c.getListOfContactInfo()) {
                    if (ClipboardType.isEmail(ci.value)) {
                        iAddRecipient.addRecipient(c.getName(), ci.value);
                        return;
                    }
                }
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

        TextView emailSearch, count_email;

        LinearLayout emailSearchLinear;

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

            emailSearch = itemView.findViewById(R.id.emailSearch);

            count_email = itemView.findViewById(R.id.count_email);

            emailSearchLinear = itemView.findViewById(R.id.emailSearchLinear);
        }
    }

}
