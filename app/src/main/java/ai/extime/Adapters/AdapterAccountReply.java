package ai.extime.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.extime.R;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import ai.extime.Events.PopupContact;
import ai.extime.Fragments.ReplyFragment;
import ai.extime.Models.ContactOfMessage;
import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterAccountReply extends RecyclerView.Adapter<AdapterAccountReply.ContactsOfMessageHolder> {


    private Context context;

    private ArrayList<ContactOfMessage> list;

    private View mainView;

    private ImageView share;

    private String mainEmail;

    private ArrayList<ContactOfMessage> listRe;

    public AdapterAccountReply(Context context, ArrayList<ContactOfMessage> list, ImageView imageView) {
        this.context = context;
        this.list = list;
        this.share = imageView;
        this.listRe = new ArrayList<>();

        for(ContactOfMessage cm : list){
            if(cm.getType().equals("Recipient") && cm.isSendCheck()){
                listRe.add(cm);
            }
        }



    }

    @NonNull
    @Override
    public AdapterAccountReply.ContactsOfMessageHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mainView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_contact_reply, viewGroup, false);
        return new AdapterAccountReply.ContactsOfMessageHolder(mainView);
    }

    public boolean checkOpen = false;

    private RadioButton lastButton = null;

    @SuppressLint({"RestrictedApi", "RecyclerView"})
    @Override
    public void onBindViewHolder(@NonNull AdapterAccountReply.ContactsOfMessageHolder holder, int i) {
        ContactOfMessage contactOfMessage = list.get(i);

        holder.textSnderCard.setVisibility(View.GONE);

        holder.google_recipient.setVisibility(View.GONE);


        holder.checkExtract.setVisibility(View.GONE);
        holder.profileIconArrowReply.setVisibility(View.GONE);
        holder.radioReply.setVisibility(View.GONE);



        if (contactOfMessage.isGoogle()) {
            holder.google_recipient.setVisibility(View.VISIBLE);
        }

        holder.profileIconAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ReplyFragment.openPreview = true;
                EventBus.getDefault().post(new PopupContact(contactOfMessage.getSearchContact().getId()));
            }
        });

        if (i == 0) {
            mainEmail = contactOfMessage.getEmail();

            holder.textSnderCard.setText("Sender");
            holder.textSnderCard.setVisibility(View.VISIBLE);

            holder.checkExtract.setVisibility(View.GONE);
            holder.profileIconArrowReply.setVisibility(View.VISIBLE);

            if (!checkOpen) {
                holder.profileIconArrowReply.setImageDrawable(context.getResources().getDrawable(R.drawable.arr_down));
            } else {
                holder.profileIconArrowReply.setImageDrawable(context.getResources().getDrawable(R.drawable.arr_up));
            }

            holder.profileIconArrowReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if (!checkOpen) {
                        checkOpen = true;

                        holder.profileIconArrowReply.setImageDrawable(context.getResources().getDrawable(R.drawable.arr_up));

                        list.addAll(i + 1, contactOfMessage.getListOfAccount());
                        notifyItemRangeInserted(i + 1, contactOfMessage.getListOfAccount().size());
                    } else {
                        checkOpen = false;

                        holder.profileIconArrowReply.setImageDrawable(context.getResources().getDrawable(R.drawable.arr_down));

                        for (int ii = 0; ii < contactOfMessage.getListOfAccount().size(); ii++)
                            list.remove(i + 1);

                        notifyItemRangeRemoved(i + 1, contactOfMessage.getListOfAccount().size());


                    }
                }
            });

        } else if (contactOfMessage.getType().equals("Recipient") && list.get(i - 1).getType().equals("Sender")) {
            holder.textSnderCard.setText("Recipients ( " + (list.size() - i) + " )");
            holder.textSnderCard.setVisibility(View.VISIBLE);
        }

        if (i > 0 && contactOfMessage.getType().equals("Sender")) {


            holder.radioReply.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        list.get(0).setEmail(contactOfMessage.getEmail());
                        list.get(0).setName(contactOfMessage.getName());
                        list.get(0).setGoogle(contactOfMessage.isGoogle());
                        list.get(0).setSearchContact(contactOfMessage.getSearchContact());

                        if (lastButton != null)
                            lastButton.setChecked(false);

                        lastButton = holder.radioReply;
                        try {
                            notifyItemChanged(0);
                        } catch (IllegalStateException e) {

                        }

                        holder.radioReply.setButtonTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.primary)));
                    }else{
                        holder.radioReply.setButtonTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.gray)));
                    }
                }
            });

            if (contactOfMessage.getEmail().equals(mainEmail)) {
                holder.radioReply.setChecked(true);
                lastButton = holder.radioReply;
            } else
                holder.radioReply.setChecked(false);

            holder.checkExtract.setVisibility(View.GONE);
            holder.profileIconArrowReply.setVisibility(View.GONE);
            holder.radioReply.setVisibility(View.VISIBLE);



        } else {
            holder.radioReply.setVisibility(View.GONE);
        }

        if (contactOfMessage.getType().equals("Recipient")) {
            holder.checkExtract.setVisibility(View.VISIBLE);

            if (listRe.contains(contactOfMessage)) {
                holder.checkExtract.setChecked(true);
            }
            else{
                holder.checkExtract.setChecked(false);
            }

            holder.profileIconArrowReply.setVisibility(View.GONE);
            holder.radioReply.setVisibility(View.GONE);
        }

        holder.checkExtract.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    listRe.add(contactOfMessage);
                }else{
                    listRe.remove(contactOfMessage);
                }
            }
        });


        holder.extractValue.setText("");
        holder.extractValueEmail.setText("");

        if (list.get(i).getName() != null && !list.get(i).getName().equals("")) {
            holder.extractValue.setText(list.get(i).getName() + " ");
        } else holder.extractValue.setVisibility(View.GONE);


        if (list.get(i).getEmail() != null) {
            holder.extractValueEmail.setText("< " + list.get(i).getEmail() + " >");
        }

        int nameHash = 0;

        if (list.get(i).getName() != null)
            nameHash = contactOfMessage.getName().hashCode();
        else if (list.get(i).getEmail() != null)
            nameHash = contactOfMessage.getEmail().hashCode();

        int color = Color.rgb(Math.abs(nameHash * 28439) % 255, Math.abs(nameHash * 211239) % 255, Math.abs(nameHash * 42368) % 255);

        GradientDrawable circle = (GradientDrawable) ContextCompat.getDrawable(context, R.drawable.blue_circle).mutate();
        circle.setColor(color);
        holder.profileIconAvatarContact.setBackground(circle);

        if (list.get(i).getName() != null) {
            String initials = getInitials(contactOfMessage.getName());
            holder.profileIconInitialsContact.setText(initials);
        } else if (list.get(i).getEmail() != null) {
            String initials = getInitials(contactOfMessage.getEmail());
            holder.profileIconInitialsContact.setText(initials);
        }

        holder.profileIconAvatar.setVisibility(View.GONE);
        holder.profileIconInitials.setVisibility(View.GONE);
        holder.imageTypeClipboardGroupList.setVisibility(View.GONE);
        holder.contactSearch.setVisibility(View.GONE);

        if (contactOfMessage.getSearchContact() != null) {
            if (contactOfMessage.getSearchContact().getPhotoURL() != null) {
                ((CircleImageView) holder.profileIconAvatar).setImageURI(Uri.parse(contactOfMessage.getSearchContact().getPhotoURL()));
                holder.profileIconAvatar.setVisibility(View.VISIBLE);
                holder.profileIconInitials.setVisibility(View.GONE);
            } else {
                int nameH = contactOfMessage.getSearchContact().getName().hashCode();
                int color2 = Color.rgb(Math.abs(nameHash * 28439) % 255, Math.abs(nameHash * 211239) % 255, Math.abs(nameHash * 42368) % 255);

                GradientDrawable circle2 = (GradientDrawable) ContextCompat.getDrawable(context, R.drawable.blue_circle).mutate();
                circle2.setColor(color2);
                holder.profileIconAvatar.setBackground(circle2);

                String initials = getInitials(contactOfMessage.getSearchContact().getName());
                holder.profileIconInitials.setText(initials);
                holder.profileIconAvatar.setVisibility(View.VISIBLE);
                holder.profileIconInitials.setVisibility(View.VISIBLE);
            }

        } else {

            if (contactOfMessage.getType().equals("Recipient")) {
                holder.imageTypeClipboardGroupList.setVisibility(View.VISIBLE);
                holder.contactSearch.setVisibility(View.VISIBLE);
            } else {
                holder.imageTypeClipboardGroupList.setVisibility(View.GONE);
                holder.contactSearch.setVisibility(View.GONE);
            }
        }

    }

    private String getInitials(String name) {
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

    public String getEmail() {
        return list.get(0).getEmail();
    }

    public ArrayList<ContactOfMessage> getListRe(){
        return  listRe;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ContactsOfMessageHolder extends RecyclerView.ViewHolder {

        CircleImageView profileIconAvatarContact, profileIconAvatar;
        TextView profileIconInitialsContact, profileIconInitials;

        TextView extractValue, extractValueEmail;

        ImageView imageTypeClipboardGroupList;
        TextView contactSearch;

        TextView textSnderCard;

        CheckBox checkExtract;

        ImageView google_recipient;

        RadioButton radioReply;

        CircleImageView profileIconArrowReply;


        ContactsOfMessageHolder(View itemView) {
            super(itemView);
            profileIconAvatarContact = itemView.findViewById(R.id.profileIconAvatarContact);
            profileIconAvatar = itemView.findViewById(R.id.profileIconAvatar);

            profileIconInitialsContact = itemView.findViewById(R.id.profileIconInitialsContact);
            profileIconInitials = itemView.findViewById(R.id.profileIconInitials);

            extractValue = itemView.findViewById(R.id.extractValue);

            imageTypeClipboardGroupList = itemView.findViewById(R.id.imageTypeClipboardGroupList);
            contactSearch = itemView.findViewById(R.id.contactSearch);

            textSnderCard = itemView.findViewById(R.id.textSnderCard);

            checkExtract = itemView.findViewById(R.id.checkExtract);

            extractValueEmail = itemView.findViewById(R.id.extractValueEmail);

            google_recipient = itemView.findViewById(R.id.google_recipient);

            radioReply = itemView.findViewById(R.id.radioReply);

            profileIconArrowReply = itemView.findViewById(R.id.profileIconArrowReply);

        }
    }
}
