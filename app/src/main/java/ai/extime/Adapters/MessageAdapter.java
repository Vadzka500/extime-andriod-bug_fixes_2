package ai.extime.Adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.BlurMaskFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.extime.R;
import com.google.api.services.gmail.model.Message;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import ai.extime.Enums.FileEnums;
import ai.extime.Fragments.ProfileFragment;
import ai.extime.Interfaces.IMessage;
import ai.extime.Models.Contact;
import ai.extime.Models.EmailMessage;
import ai.extime.Models.MessageData;
import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private View mainView;

    private Context context;

    private ArrayList<EmailMessage> listMessages;

    private boolean blur;

    IMessage iMessage;

    private Contact contact;

    public void setBlur(boolean blur) {
        this.blur = blur;
        notifyDataSetChanged();
    }
    public void setContact(Contact contact){
        this.contact = contact;
    }

    public void clearList() {
        if (listMessages != null) {
            listMessages = new ArrayList<>();
            notifyDataSetChanged();
        }
    }

    public float px;
    public int type;

    public MessageAdapter(Context context, ArrayList<EmailMessage> list, IMessage iMessage, int type) {
        this.context = context;
        this.listMessages = list;
        this.iMessage = iMessage;
        px = 15 * context.getResources().getDisplayMetrics().density;
        this.type = type;
    }

    public void updateKanban(ArrayList<EmailMessage> list){
        this.listMessages = list;
        notifyDataSetChanged();
    }

    public void updateList(ArrayList<EmailMessage> list) {
        int initialSize = listMessages.size();
        this.listMessages = list;


        notifyItemRangeInserted(initialSize, listMessages.size() - 1);
        //notifyDataSetChanged();
    }

    public void updateListNew(ArrayList<EmailMessage> list, int count) {
        int initialSize = listMessages.size();
        this.listMessages = list;


        notifyItemRangeInserted(0, count);
        //notifyDataSetChanged();
    }

    public void updateListMore(ArrayList<EmailMessage> list) {
        int initialSize = listMessages.size();
        this.listMessages = list;



            //notifyItemInserted(listMessages.size() - 1);
            notifyItemRangeInserted(initialSize, listMessages.size() - 1);

        //notifyItemRangeInserted(0, count);
        //notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mainView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_card, viewGroup, false);

        if(type == 2){

            int px = (int) (19 * context.getResources().getDisplayMetrics().density);

            int width_medium = Resources.getSystem().getDisplayMetrics().widthPixels - px;
            int height_medium = (int) (64 * context.getResources().getDisplayMetrics().density);

            mainView.setLayoutParams(new LinearLayout.LayoutParams(width_medium, height_medium));
        }

        return new MessageViewHolder(mainView);
    }

    public Time getRandomDate() {
        final Random random = new Random();
        final int millisInDay = 24 * 60 * 60 * 1000;
        return new Time((long) random.nextInt(millisInDay));
    }



    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int i) {
        final EmailMessage message = listMessages.get(i);

        holder.replyMess.setVisibility(View.GONE);


        if ((i != listMessages.size() - 1 && listMessages.size() > 1) || listMessages.get(i).getAccount() != null) {


            holder.title.setTextSize(TypedValue.COMPLEX_UNIT_PX, px);
            holder.value.setTextSize(TypedValue.COMPLEX_UNIT_PX, px);
            holder.value.setTextColor(Color.parseColor("#808080"));
            holder.value.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));


            try {

                if(message.getTitle().length() > 55) {
                    String tit = message.getTitle().substring(0, 55);
                    holder.title.setText(tit);
                }else
                    holder.title.setText(message.getTitle());


                if (message.getMessage().contains("<html>")  || message.getMessage().contains("<br>")) {


                    String t = Html.fromHtml(message.getMessage()).toString();
                    if(t.length() > 55){
                        holder.value.setText(t.substring(0,55));
                    }else
                        holder.value.setText(t);


                }else {

                    if(message.getMessage().length() > 55){
                        String t = message.getMessage().substring(0,55);
                        holder.value.setText(t);
                    }else
                        holder.value.setText(message.getMessage());
                }

                if (message.getTitle().length() > 4) {

                    if (message.getTitle().substring(0, 3).contains("Re:") || message.getTitle().substring(0, 3).contains("Re[")) {
                        holder.replyMess.setImageDrawable(context.getResources().getDrawable(R.drawable.reply_message));
                        holder.replyMess.setVisibility(View.VISIBLE);
                    } else if (message.getTitle().substring(0, 4).contains("Fwd:") || message.getTitle().substring(0, 4).contains("Fwd[")) {
                        holder.replyMess.setImageDrawable(context.getResources().getDrawable(R.drawable.foward_message));
                        holder.replyMess.setVisibility(View.VISIBLE);
                    }
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }


            if(message.getListOfTypeMessage().contains("STARRED")){
                holder.favoriteMessageCard.setVisibility(View.VISIBLE);
                holder.favoriteMessageCardEmpty.setVisibility(View.GONE);
            }else{
                holder.favoriteMessageCard.setVisibility(View.GONE);
                holder.favoriteMessageCardEmpty.setVisibility(View.VISIBLE);
            }

            if(message.getListOfTypeMessage().contains("DRAFT")){
                holder.draft_message_text.setVisibility(View.VISIBLE);
            }else{
                holder.draft_message_text.setVisibility(View.GONE);
            }


            holder.favoriteMessageCardEmpty.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iMessage.setStarMessage(message, true);
                    holder.favoriteMessageCardEmpty.setVisibility(View.GONE);
                    holder.favoriteMessageCard.setVisibility(View.VISIBLE);
                    Toast.makeText(mainView.getContext(), "Successfully added to Starred", Toast.LENGTH_SHORT).show();

                }
            });

            holder.favoriteMessageCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iMessage.setStarMessage(message, false);
                    holder.favoriteMessageCardEmpty.setVisibility(View.VISIBLE);
                    holder.favoriteMessageCard.setVisibility(View.GONE);
                    Toast.makeText(mainView.getContext(), "Deleted from Starred", Toast.LENGTH_SHORT).show();
                }
            });



            holder.title.setTypeface(null, Typeface.NORMAL);


            if (message.getListOfTypeMessage() != null) {
                if (message.getListOfTypeMessage().contains("UNREAD") && message.getListOfTypeMessage().contains("INBOX")) {
                    holder.title.setTypeface(holder.title.getTypeface(), Typeface.BOLD);
                    holder.value.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));

                }
            }

            holder.imageJoin.setVisibility(View.GONE);


            int nameHash = message.getAccount().getName().hashCode();
            int color = Color.rgb(Math.abs(nameHash * 28439) % 255, Math.abs(nameHash * 211239) % 255, Math.abs(nameHash * 42368) % 255);

            GradientDrawable circle = (GradientDrawable) ContextCompat.getDrawable(context, R.drawable.blue_circle).mutate();
            circle.setColor(color);
            holder.avatar.setBackground(circle);
            String initials = getInitials(message.getAccount().getName());
            holder.initials.setText(initials);

            holder.avatar.setVisibility(View.VISIBLE);
            holder.initials.setVisibility(View.VISIBLE);


            try {

                Calendar current = Calendar.getInstance();
                Calendar contactDate = Calendar.getInstance();
                current.setTime(new Date());
                contactDate.setTime(message.getDate());
                String timeStr = "";

                Calendar cal1 = Calendar.getInstance();
                cal1.setTime(message.getDate());
                Time time = getRandomDate();
                time.setHours(cal1.get(Calendar.HOUR_OF_DAY));
                time.setMinutes(cal1.get(Calendar.MINUTE));
                time.setSeconds(cal1.get(Calendar.SECOND));

                if (current.get(Calendar.YEAR) == contactDate.get(Calendar.YEAR) && current.get(Calendar.MONTH) == contactDate.get(Calendar.MONTH) && current.get(Calendar.DAY_OF_MONTH) == contactDate.get(Calendar.DAY_OF_MONTH)) {
                    timeStr = time.getHours() + ":";
                    if (time.getMinutes() < 10) timeStr += "0";
                    timeStr += time.getMinutes();
                } else if (current.get(Calendar.YEAR) == contactDate.get(Calendar.YEAR) && (current.get(Calendar.MONTH) != contactDate.get(Calendar.MONTH) || current.get(Calendar.DAY_OF_MONTH) != contactDate.get(Calendar.DAY_OF_MONTH))) {
                    timeStr = contactDate.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.ENGLISH) + " " + contactDate.get(Calendar.DAY_OF_MONTH);
                } else {
                    timeStr = contactDate.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.ENGLISH) + " " + String.valueOf(contactDate.get(Calendar.YEAR))/*.substring(2)*/;
                }


                holder.time.setText(timeStr);
            } catch (Exception e) {
                System.out.println("ERROR TO GET TIME Contact Adapter");
            }


            holder.file_1.setVisibility(View.GONE);
            holder.file_2.setVisibility(View.GONE);
            holder.file_count.setVisibility(View.GONE);

            int count_f = 0;
            if (message.getMessageData() != null && !message.getMessageData().isEmpty()) {

                for (MessageData e : message.getMessageData()) {
                    if (e.getClipboard() == null) {

                        if (holder.file_1.getVisibility() == View.GONE) {
                            holder.file_1.setImageDrawable(context.getResources().getDrawable(e.getFileEnums().getId()));
                            holder.file_1.setVisibility(View.VISIBLE);
                            continue;
                        }

                        if (holder.file_2.getVisibility() == View.GONE) {
                            holder.file_2.setImageDrawable(context.getResources().getDrawable(e.getFileEnums().getId()));
                            holder.file_2.setVisibility(View.VISIBLE);
                            continue;
                        }

                        count_f++;

                        holder.file_count.setVisibility(View.VISIBLE);
                        holder.count_f.setText(String.valueOf(count_f));

                    }

                }

            }


            holder.linear.requestLayout();
            holder.linear_2.requestLayout();

            holder.linear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickMessage(message);
                }
            });

            holder.linear_2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickMessage(message);
                }
            });

            holder.frameIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    iMessage.openFragmentMessage(message);

                }
            });

            holder.time.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iMessage.openContactList(message);
                }
            });

            holder.replyMess.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iMessage.openContactList(message);
                }
            });


            if (blur) {
                holder.value.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                float radius = holder.value.getTextSize() / 3.5f;
                BlurMaskFilter filter = new BlurMaskFilter(radius, BlurMaskFilter.Blur.NORMAL);
                holder.value.getPaint().setMaskFilter(filter);
            } else {
                holder.value.getPaint().setMaskFilter(null);
            }

        } else {

            float px = 10 * context.getResources().getDisplayMetrics().density;

            float px2 = 14 * context.getResources().getDisplayMetrics().density;

            holder.title.setTextSize(TypedValue.COMPLEX_UNIT_PX, px);
            holder.value.setTextSize(TypedValue.COMPLEX_UNIT_PX, px2);

            holder.value.setTextColor(context.getResources().getColor(R.color.black));

            holder.value.getPaint().setMaskFilter(null);

            holder.value.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));

            holder.initials.setVisibility(View.GONE);
            holder.avatar.setVisibility(View.GONE);
            holder.file_1.setVisibility(View.GONE);
            holder.file_2.setVisibility(View.GONE);
            holder.file_count.setVisibility(View.GONE);
            holder.draft_message_text.setVisibility(View.GONE);
            holder.imageJoin.setVisibility(View.VISIBLE);

            holder.favoriteMessageCard.setVisibility(View.GONE);
            holder.favoriteMessageCardEmpty.setVisibility(View.GONE);



            if (blur) {
                holder.value.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                float radius = holder.value.getTextSize() / 3.5f;
                BlurMaskFilter filter = new BlurMaskFilter(radius, BlurMaskFilter.Blur.NORMAL);
                holder.value.getPaint().setMaskFilter(filter);
            } else {
                holder.value.getPaint().setMaskFilter(null);
            }


            if (message.getMessage().equalsIgnoreCase("extime")) {
                holder.imageJoin.setImageResource(R.drawable.new_icon);
            } else {
                holder.imageJoin.setImageDrawable(context.getResources().getDrawable(R.drawable.icn_fab_google));
            }

            if(ProfileFragment.selectedContact != null && ProfileFragment.selectedContact.isValid() && (ProfileFragment.selectedContact.listOfContacts == null || ProfileFragment.selectedContact.listOfContacts.isEmpty()))
                holder.title.setText("joined - id"+ ProfileFragment.selectedContact.getId());
            else
                holder.title.setText("joined");

            holder.value.setText(message.getMessage());



            Calendar contactDate = Calendar.getInstance();
            contactDate.setTime(message.getDate());

            holder.time.setText(contactDate.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.ENGLISH) + " " + contactDate.get(Calendar.DAY_OF_MONTH));


            holder.linear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            holder.linear_2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            holder.frameIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return listMessages.size();
    }

    public boolean isNullList() {
        return listMessages == null;
    }

    public void clickMessage(EmailMessage message) {
        iMessage.clickMessage(message);
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

    static class MessageViewHolder extends RecyclerView.ViewHolder {

        CircleImageView avatar;
        TextView title, value, initials, time, count_f;
        LinearLayout linear, linear_2;
        ImageView file_1, file_2;
        FrameLayout file_count;
        ImageView imageJoin;
        FrameLayout frameIcon;

        ImageView replyMess;

        ImageView favoriteMessageCard, favoriteMessageCardEmpty;

        TextView draft_message_text;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.accountAvatar);
            title = itemView.findViewById(R.id.messageTitle);
            value = itemView.findViewById(R.id.messageText);
            initials = itemView.findViewById(R.id.accountInitials);
            time = itemView.findViewById(R.id.cardTime);

            linear = itemView.findViewById(R.id.linearMessage);

            linear_2 = itemView.findViewById(R.id.linear_2);
            file_1 = itemView.findViewById(R.id.file_first);
            file_2 = itemView.findViewById(R.id.file_second);
            file_count = itemView.findViewById(R.id.files_count);

            count_f = itemView.findViewById(R.id.f_count);

            imageJoin = itemView.findViewById(R.id.type_image);

            frameIcon = itemView.findViewById(R.id.frameIcon);

            replyMess = itemView.findViewById(R.id.replyMess);

            favoriteMessageCard = itemView.findViewById(R.id.favoriteMessageCard);
            favoriteMessageCardEmpty = itemView.findViewById(R.id.favoriteMessageCardEmpty);

            draft_message_text = itemView.findViewById(R.id.draft_message_text);
        }
    }
}
