package ai.extime.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.extime.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import org.greenrobot.eventbus.EventBus;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

import ai.extime.Activity.MainActivity;
import ai.extime.Enums.TypeCardTimeline;
import ai.extime.Events.EventSetStarMessage;
import ai.extime.Events.NotifyKanbanTimeline;
import ai.extime.Events.TypeCard;
import ai.extime.Fragments.TimeLineFragment;
import ai.extime.Interfaces.IKanbanList;
import ai.extime.Interfaces.IMessage;
import ai.extime.Interfaces.IOpenSocials;
import ai.extime.Models.Contact;
import ai.extime.Models.ContactOfMessage;
import ai.extime.Models.EmailMessage;
import ai.extime.Models.MessageData;
import ai.extime.Utils.ClipboardType;
import ai.extime.Utils.ShareTemplatesMessageReply;
import de.hdodenhof.circleimageview.CircleImageView;

import static ai.extime.Utils.ShareTemplatesMessageReply.iMessage;

public class KanbanTimelineAdapter extends RecyclerView.Adapter<KanbanTimelineAdapter.KanBanViewHolder> {

    private ArrayList<EmailMessage> list;

    private Context context;

    public static TypeCardTimeline typeCard;

    private View mainView;

    //private Activity activity;

    private IMessage iMessage;

    private int type;

    public KanbanTimelineAdapter(ArrayList<EmailMessage> list, Context context, TypeCardTimeline typeCard, Activity activity, IMessage iMessage, int type) {
        this.list = list;
        this.context = context;
        this.iMessage = iMessage;
        this.type = type;
        KanbanTimelineAdapter.typeCard = typeCard;
        //this.activity = activity;
    }

    public void updateList(ArrayList<EmailMessage> list) {
        this.list = list;

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public KanBanViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        if (i == 2)
            mainView = layoutInflater.inflate(R.layout.kanban_card_timeline, viewGroup, false);
        else {
            mainView = layoutInflater.inflate(R.layout.kanban_card, viewGroup, false);

        }
        return new KanbanTimelineAdapter.KanBanViewHolder(mainView);
    }

    @Override
    public void onBindViewHolder(@NonNull KanBanViewHolder holder, int i_2) {

        EmailMessage message = list.get(i_2);

        holder.textMessage.setTextIsSelectable(true);

        if (message.getListOfTypeMessage().contains("STARRED")) {
            holder.favoriteMessageCard.setVisibility(View.VISIBLE);
            holder.favoriteMessageCardEmpty.setVisibility(View.GONE);
        } else {
            holder.favoriteMessageCard.setVisibility(View.GONE);
            holder.favoriteMessageCardEmpty.setVisibility(View.VISIBLE);
        }

        holder.favoriteMessageCardEmpty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //iMessage.setStarMessage(message, true);
                holder.favoriteMessageCardEmpty.setVisibility(View.GONE);
                holder.favoriteMessageCard.setVisibility(View.VISIBLE);
                Toast.makeText(mainView.getContext(), "Successfully added to Starred", Toast.LENGTH_SHORT).show();
                EventBus.getDefault().post(new EventSetStarMessage(true, message));

            }
        });

        holder.favoriteMessageCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iMessage.setStarMessage(message, false);
                holder.favoriteMessageCardEmpty.setVisibility(View.VISIBLE);
                holder.favoriteMessageCard.setVisibility(View.GONE);
                Toast.makeText(mainView.getContext(), "Deleted from Starred", Toast.LENGTH_SHORT).show();
                EventBus.getDefault().post(new EventSetStarMessage(false, message));
            }
        });

        if (message.getTitle().length() > 20) {
            holder.subject_email.setText(message.getTitle().substring(0, 20));
        } else {
            holder.subject_email.setText(message.getTitle());
        }

        if (message.getMessage().contains("<html>") || message.getMessage().contains("<br>")) {

            String t = Html.fromHtml(message.getMessage()).toString();
            holder.textMessage.setText(t);

        } else {
            if (message.getMessage().length() > 55) {
                String t = message.getMessage().substring(0, 55);
                holder.textMessage.setText(t);
            } else
                holder.textMessage.setText(message.getMessage());
        }

        holder.textMessage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {


                if (TimeLineFragment.selectedId != null && TimeLineFragment.selectedId.equals(message.getId()) && TimeLineFragment.type_k == type) {
                    holder.scrollText.getParent().requestDisallowInterceptTouchEvent(true);
                }

                return false;
            }
        });

        holder.profileIconAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iMessage.openContactList(message);
            }
        });


        holder.scrollName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)context).findViewById(R.id.popup_menu_timeline).setVisibility(View.GONE);
                ((MainActivity)context).findViewById(R.id.popup_menu_settings).setVisibility(View.GONE);
                ((MainActivity)context).findViewById(R.id.inboxContacts).setVisibility(View.GONE);

            }
        });

        holder.scrollName_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)context).findViewById(R.id.popup_menu_timeline).setVisibility(View.GONE);
                ((MainActivity)context).findViewById(R.id.popup_menu_settings).setVisibility(View.GONE);
                ((MainActivity)context).findViewById(R.id.inboxContacts).setVisibility(View.GONE);

            }
        });

        holder.textMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)context).findViewById(R.id.popup_menu_timeline).setVisibility(View.GONE);
                ((MainActivity)context).findViewById(R.id.popup_menu_settings).setVisibility(View.GONE);
                ((MainActivity)context).findViewById(R.id.inboxContacts).setVisibility(View.GONE);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)context).findViewById(R.id.popup_menu_timeline).setVisibility(View.GONE);
                ((MainActivity)context).findViewById(R.id.popup_menu_settings).setVisibility(View.GONE);
                ((MainActivity)context).findViewById(R.id.inboxContacts).setVisibility(View.GONE);
                if (iMessage.isOpenPreview()) iMessage.clickMessage(message);
            }
        });


        holder.scrollName.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (TimeLineFragment.selectedId != null && TimeLineFragment.selectedId.equals(message.getId()) && TimeLineFragment.type_k == type)
                    holder.scrollName.getParent().requestDisallowInterceptTouchEvent(true);

                return false;
            }
        });

        holder.scrollName_2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {


                if (TimeLineFragment.selectedId != null && TimeLineFragment.selectedId.equals(message.getId()) && TimeLineFragment.type_k == type)
                    holder.scrollName_2.getParent().requestDisallowInterceptTouchEvent(true);

                return false;
            }
        });

        holder.number_card.setText(String.valueOf(i_2 + 1));

        int nameHash = message.getAccount().getName().hashCode();
        int color = Color.rgb(Math.abs(nameHash * 28439) % 255, Math.abs(nameHash * 211239) % 255, Math.abs(nameHash * 42368) % 255);

        GradientDrawable circle = (GradientDrawable) ContextCompat.getDrawable(context, R.drawable.blue_circle).mutate();
        circle.setColor(color);
        holder.profileIconAvatar.setBackground(circle);
        String initials = getInitials(message.getAccount().getName());
        holder.profileIconInitials.setText(initials);

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
                timeStr = contactDate.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.ENGLISH) + " " + contactDate.get(Calendar.YEAR)/*.substring(2)*/;
            }

            holder.cardTime.setText(timeStr);
        } catch (Exception e) {
            System.out.println("ERROR TO GET TIME Contact Adapter");
        }


        holder.file_first.setVisibility(View.GONE);

        holder.files_count.setVisibility(View.GONE);

        int count_f = 0;
        if (message.getMessageData() != null && !message.getMessageData().isEmpty()) {

            for (MessageData e : message.getMessageData()) {
                if (e.getClipboard() == null) {

                    if (holder.file_first.getVisibility() == View.GONE) {
                        holder.file_first.setImageDrawable(context.getResources().getDrawable(e.getFileEnums().getId()));
                        holder.file_first.setVisibility(View.VISIBLE);
                        continue;
                    }

                    count_f++;

                    holder.files_count.setVisibility(View.VISIBLE);
                    holder.f_count.setText(String.valueOf(count_f));

                }

            }

        }

        if (count_f == 0) {
            holder.frame_open_popup_message.setVisibility(View.GONE);
        } else {
            holder.frame_open_popup_message.setVisibility(View.VISIBLE);
        }

        String sendTo = null;
        String sendToName = null;
        String replyFrom = null;


        if (TimeLineFragment.email_contact.contains(message.getAccount().getEmail())/*message.getAccount().getEmail().equals(email_contact)*/) {


            holder.contactName.setText(message.getAccount().getName() + " < " + message.getAccount().getEmail() + " >");


            SharedPreferences mSettings;
            mSettings = context.getSharedPreferences("accountUser", Context.MODE_PRIVATE);
            Set<String> set = mSettings.getStringSet("accounts", null);


            if (set != null && set.contains(message.getAccount().getEmail()) || set != null && set.contains(message.getAccount().getName())) {
                if (set.contains(message.getAccount().getEmail()))
                    replyFrom = message.getAccount().getEmail();
                else replyFrom = message.getAccount().getName();
            } else {
                sendTo = message.getAccount().getEmail();
                sendToName = message.getAccount().getName();
            }

        } else {
            holder.contactName.setText("< " + message.getAccount().getName() + " >");
        }


        if (TimeLineFragment.selectedId != null && TimeLineFragment.selectedId.equals(message.getId()) && TimeLineFragment.type_k == type) {
            holder.body.setBackground(context.getResources().getDrawable(R.drawable.popup_background_select));
            holder.clicker_1.setVisibility(View.GONE);
        } else {
            holder.body.setBackground(context.getResources().getDrawable(R.drawable.popup_background_card));
            holder.clicker_1.setVisibility(View.VISIBLE);
        }

        holder.clicker_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TimeLineFragment.selectedId == null || !TimeLineFragment.selectedId.equals(message.getId())) {
                    TimeLineFragment.selectedId = message.getId();
                    TimeLineFragment.type_k = type;
                    EventBus.getDefault().post(new NotifyKanbanTimeline());
                }

                holder.clicker_1.setVisibility(View.GONE);

                ((MainActivity)context).findViewById(R.id.clicker_message_hide).setVisibility(View.VISIBLE);
                ((MainActivity)context).findViewById(R.id.clicker_message_hide).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        iMessage.clickMessage(message);
                        ((MainActivity)context).findViewById(R.id.clicker_message_hide).setVisibility(View.GONE);

                    }
                });

                if (iMessage.isOpenPreview()) {
                    iMessage.clickMessage(message);
                    iMessage.clickMessage(message);
                } else {
                    iMessage.clickMessage(message);
                    if (!iMessage.isOpenPreview()) iMessage.clickMessage(message);
                }


            }
        });

        //================================


        String data = null;

        ArrayList<ContactOfMessage> listOfContactsMessage = new ArrayList<>();

        ContactOfMessage sender = new ContactOfMessage();

        for (int i2 = 0; i2 < message.getListHeaders().size(); i2++) {
            System.out.println(i2 + ". " + message.getListHeaders().get(i2).getValue() + ", type = " + message.getListHeaders().get(i2).getName());

            if (message.getListHeaders().get(i2).getName().equalsIgnoreCase("From")) {
                String send = message.getListHeaders().get(i2).getValue();
                String email = ClipboardType.getEmail(send);

                if (email != null) {


                    send = send.replace(email, "");
                    send = send.replace("<", "");
                    send = send.replace(">", "");
                    send = send.replaceAll("\"", "");

                    send = send.trim();

                    if (send.length() > 0) sender.setName(send);
                }
            }

            if (message.getListHeaders().get(i2).getName().equalsIgnoreCase("To")) {
                data = message.getListHeaders().get(i2).getValue();
                break;
            }
        }

        if (data != null) {
            String[] mas = data.split(",");
            if (mas.length > 0) {
                for (int i = 0; i < mas.length; i++) {
                    String email = ClipboardType.getEmail(mas[i]);
                    ContactOfMessage contact = new ContactOfMessage();
                    if (email != null)
                        contact.setEmail(email.trim());

                    mas[i] = mas[i].replace(email, "");
                    mas[i] = mas[i].replace("<", "");
                    mas[i] = mas[i].replace(">", "");
                    mas[i] = mas[i].replaceAll("\"", "");

                    mas[i] = mas[i].trim();
                    if (mas[i].length() > 0) contact.setName(mas[i]);


                    SharedPreferences mSettings;
                    mSettings = context.getSharedPreferences("accountUser", Context.MODE_PRIVATE);
                    Set<String> set = mSettings.getStringSet("accounts", null);

                    if (replyFrom != null && sendTo == null) {
                        sendTo = email.trim();
                        sendToName = mas[i];
                    }

                    if (set != null && !set.isEmpty()) {

                        if (set.contains(email)) {
                            contact.setGoogle(true);
                            replyFrom = contact.getEmail();
                        }

                    }
                    listOfContactsMessage.add(contact);
                }
            }
        }


        SharedPreferences mSettings;
        mSettings = context.getSharedPreferences("accountUser", Context.MODE_PRIVATE);
        Set<String> set = mSettings.getStringSet("accounts", null);

        String finalSendTo = sendTo;
        String finalSendToName = sendToName;
        String finalReplyFrom = replyFrom;
        holder.replyMeny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList<String> sTo = new ArrayList<>();
                ArrayList<String> sName = new ArrayList<>();

                sTo.add(finalSendTo);
                sName.add(finalSendToName);

                ShareTemplatesMessageReply.showTemplatesPopup( ((MainActivity)context), false, sTo, sName, finalReplyFrom, message, null, true);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return typeCard.getId();
    }

    private String getInitials(String name) {
        String initials = "";
        if (name != null && !name.isEmpty()) {
            String[] names = name.split("\\s+");
            for (String namePart : names) {
                if (namePart != null && namePart.length() > 0)
                    initials += namePart.charAt(0);
            }
        }
        return initials.toUpperCase();
    }

    public Time getRandomDate() {
        final Random random = new Random();
        final int millisInDay = 24 * 60 * 60 * 1000;
        return new Time((long) random.nextInt(millisInDay));
    }


    public static class KanBanViewHolder extends RecyclerView.ViewHolder {

        LinearLayout body;

        TextView number_card;
        CircleImageView profileIconAvatar;
        TextView contactName;
        ImageView menu_fragment_message;
        TextView subject_email;

        ImageView favoriteMessageCard, favoriteMessageCardEmpty;
        TextView cardTime;

        TextView textMessage;

        ImageView replyMeny;

        TextView profileIconInitials;

        ScrollView scrollText;
        HorizontalScrollView scrollName, scrollName_2;


        FrameLayout files_count;
        TextView f_count;
        ImageView file_first;

        LinearLayout frame_open_popup_message;

        FrameLayout clicker_1;


        public KanBanViewHolder(@NonNull View itemView) {
            super(itemView);

            body = itemView.findViewById(R.id.body_card);

            number_card = itemView.findViewById(R.id.number_card);

            profileIconAvatar = itemView.findViewById(R.id.profileIconAvatar);
            contactName = itemView.findViewById(R.id.contactName);
            menu_fragment_message = itemView.findViewById(R.id.menu_fragment_message);
            subject_email = itemView.findViewById(R.id.subject_email);
            favoriteMessageCard = itemView.findViewById(R.id.favoriteMessageCard);
            favoriteMessageCardEmpty = itemView.findViewById(R.id.favoriteMessageCardEmpty);
            cardTime = itemView.findViewById(R.id.cardTime);

            textMessage = itemView.findViewById(R.id.textMessage);

            replyMeny = itemView.findViewById(R.id.replyMeny);

            profileIconInitials = itemView.findViewById(R.id.profileIconInitials);

            scrollText = itemView.findViewById(R.id.scrollText);

            scrollName = itemView.findViewById(R.id.scrollName);


            files_count = itemView.findViewById(R.id.files_count);
            f_count = itemView.findViewById(R.id.f_count);
            file_first = itemView.findViewById(R.id.file_first);

            frame_open_popup_message = itemView.findViewById(R.id.frame_open_popup_message);

            scrollName_2 = itemView.findViewById(R.id.scrollName_2);

            clicker_1 = itemView.findViewById(R.id.clicker_1);

        }
    }
}
