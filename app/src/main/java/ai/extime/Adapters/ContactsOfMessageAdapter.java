package ai.extime.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.extime.R;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import ai.extime.Events.PopupCompanyInProfile;
import ai.extime.Events.PopupInProfile;
import ai.extime.Models.ContactOfMessage;
import de.hdodenhof.circleimageview.CircleImageView;

public class ContactsOfMessageAdapter extends RecyclerView.Adapter<ContactsOfMessageAdapter.ContactsOfMessageHolder> {

    private Context context;

    private ArrayList<ContactOfMessage> list;

    private View mainView;

    ArrayList<ContactOfMessage> listOfMess;

    private TextView textView;


    public ContactsOfMessageAdapter(Context context, ArrayList<ContactOfMessage> list, TextView textView){
        this.context = context;
        this.list = list;
        this.textView = textView;
        this.listOfMess = new ArrayList<>();
    }

    @NonNull
    @Override
    public ContactsOfMessageHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mainView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_contact_of_message, viewGroup, false);
        return new ContactsOfMessageAdapter.ContactsOfMessageHolder(mainView);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactsOfMessageHolder holder, int i) {
        ContactOfMessage contactOfMessage = list.get(i);

        holder.textSnderCard.setVisibility(View.GONE);

        holder.google_recipient.setVisibility(View.GONE);

        if(contactOfMessage.isGoogle()){
            holder.google_recipient.setVisibility(View.VISIBLE);
        }

        if(i == 0){
            holder.textSnderCard.setText("Sender");
            holder.textSnderCard.setVisibility(View.VISIBLE);
        }else if(i == 1){
            holder.textSnderCard.setText("Recipients ( "+String.valueOf(list.size() - 1)+" )");
            holder.textSnderCard.setVisibility(View.VISIBLE);
        }

        holder.extractValue.setText("");
        holder.extractValueEmail.setText("");

        if(list.get(i).getName() != null && !list.get(i).getName().equals("")){
            holder.extractValue.setText(list.get(i).getName()+" ");
        }else holder.extractValue.setVisibility(View.GONE);


        if(list.get(i).getEmail() != null){
            holder.extractValueEmail.setText("< "+list.get(i).getEmail()+" >");
        }

        int nameHash = 0;

        if(list.get(i).getName() != null)
            nameHash = contactOfMessage.getName().hashCode();
        else if(list.get(i).getEmail() != null)
            nameHash = contactOfMessage.getEmail().hashCode();

        int color = Color.rgb(Math.abs(nameHash * 28439) % 255, Math.abs(nameHash * 211239) % 255, Math.abs(nameHash * 42368) % 255);

        GradientDrawable circle = (GradientDrawable) ContextCompat.getDrawable(context, R.drawable.blue_circle).mutate();
        circle.setColor(color);
        holder.profileIconAvatarContact.setBackground(circle);

        if(list.get(i).getName() != null) {
            String initials = getInitials(contactOfMessage.getName());
            holder.profileIconInitialsContact.setText(initials);
        }else if(list.get(i).getEmail() != null){
            String initials = getInitials(contactOfMessage.getEmail());
            holder.profileIconInitialsContact.setText(initials);
        }

        holder.profileIconAvatar.setVisibility(View.GONE);
        holder.profileIconInitials.setVisibility(View.GONE);
        holder.imageTypeClipboardGroupList.setVisibility(View.GONE);
        holder.contactSearch.setVisibility(View.GONE);

        if(contactOfMessage.getSearchContact() != null){
            if (contactOfMessage.getSearchContact().getPhotoURL() != null) {
                ((CircleImageView) holder.profileIconAvatar).setImageURI(Uri.parse(contactOfMessage.getSearchContact().getPhotoURL()));
                holder.profileIconAvatar.setVisibility(View.VISIBLE);
                holder.profileIconInitials.setVisibility(View.GONE);
            }else{
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

            /*holder.profileIconAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (contactOfMessage.getSearchContact().listOfContacts == null || contactOfMessage.getSearchContact().listOfContacts.isEmpty()) {
                        EventBus.getDefault().post(new PopupInProfile(contactOfMessage.getSearchContact().getId()));
                    } else
                        EventBus.getDefault().post(new PopupCompanyInProfile(contactOfMessage.getSearchContact().getId()));

                }
            });*/

        }else{


            holder.imageTypeClipboardGroupList.setVisibility(View.VISIBLE);
            holder.contactSearch.setVisibility(View.VISIBLE);
        }


        if(listOfMess.contains(contactOfMessage)){
            holder.checkExtract.setChecked(true);
        }else
            holder.checkExtract.setChecked(false);


        holder.checkExtract.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    listOfMess.add(contactOfMessage);

                    if(listOfMess.size() == 1){
                        textView.setTextColor(context.getResources().getColor(R.color.primary));
                    }else{
                        textView.setTextColor(context.getResources().getColor(R.color.gray));
                    }
                }else{
                    listOfMess.remove(contactOfMessage);

                    if(listOfMess.size() == 1){
                        textView.setTextColor(context.getResources().getColor(R.color.primary));
                    }
                    
                    if(listOfMess.size() == 0){
                        textView.setTextColor(context.getResources().getColor(R.color.gray));
                    }
                }
            }
        });

    }

    public ArrayList<ContactOfMessage> getSelectList(){
        return listOfMess;
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

        }
    }
}
