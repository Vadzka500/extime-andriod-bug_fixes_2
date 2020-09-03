package ai.extime.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

import ai.extime.Fragments.TimeLineFragment;
import de.hdodenhof.circleimageview.CircleImageView;
import ai.extime.Models.Contact;
import ai.extime.Models.TimeLine;
import com.extime.R;

/**
 * Created by patal on 10.08.2017.
 */

public class TimeLineAdapter extends RecyclerView.Adapter<TimeLineAdapter.TimeLineViewHolder>  {

    private ArrayList<TimeLine> listOfTimelines;

    private TimeLine previosTimeline;

    private View mainView;

    private TimeLineFragment timeLineFragment;

    private Context context;

    static class TimeLineViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        TextView initials;
        CircleImageView contactImage;
        ImageView email;
        ImageView geo;
        ImageView remind;
        ImageView facebook;
        ImageView arrow;
        ImageView pdf;
        TextView count;
        TextView date;
        FrameLayout timelineDateBar;
        TextView dayTimeline;
        TextView dateTimeline;
        TextView bottomValue;
        TextView typeValue;
        View card;


        TimeLineViewHolder(View itemView) {
            super(itemView);
            card = itemView;
            userName = (TextView) itemView.findViewById(R.id.contactName);
            initials = (TextView) itemView.findViewById(R.id.contactInitials);
            count = (TextView) itemView.findViewById(R.id.countTimeline);
            date = (TextView) itemView.findViewById(R.id.cardTime);
            contactImage = (CircleImageView)itemView.findViewById(R.id.contactCircleColor);
            email = (ImageView) itemView.findViewById(R.id.emailTimeline);
            geo = (ImageView) itemView.findViewById(R.id.geoTimeline);
            arrow = (ImageView) itemView.findViewById(R.id.arrowTimeline);
            remind = (ImageView) itemView.findViewById(R.id.remindTimeline);
            facebook = (ImageView) itemView.findViewById(R.id.facebookTimeline);
            pdf = (ImageView) itemView.findViewById(R.id.pdfTimeline);
            timelineDateBar = (FrameLayout) itemView.findViewById(R.id.timelineDateBar);
            dayTimeline = (TextView) itemView.findViewById(R.id.dayTimeline);
            dateTimeline = (TextView) itemView.findViewById(R.id.dateTimeline);
            bottomValue = (TextView) itemView.findViewById(R.id.bottomValueTimeline);
            typeValue = (TextView) itemView.findViewById(R.id.typeTimeline);
        }
    }

    private String getInitials(Contact contact){
        String initials = "";
        if (contact.getName() != null && !contact.getName().isEmpty()) {
            String names[] = contact.getName().split("\\s+");
            for (String namePart : names)
                initials += namePart.charAt(0);
        }
        return initials.toUpperCase();
    }

    public TimeLineAdapter(Context context, ArrayList<TimeLine> listOfTimelines, TimeLineFragment timeLineFragment) {
        this.context = context;
        this.listOfTimelines = listOfTimelines;
        this.timeLineFragment = timeLineFragment;
    }

    @Override
    public TimeLineViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        mainView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_timeline, viewGroup, false);

        return new TimeLineViewHolder(mainView);
    }

    @Override
    public void onBindViewHolder(final TimeLineViewHolder holder, int position) {

        final TimeLine timeLine = listOfTimelines.get(position);

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeLineFragment.closeOtherPopup();
            }
        });

        holder.userName.setText(timeLine.getContact().getName());

        if(!timeLine.isHasEmail()) holder.email.setVisibility(View.GONE);

        if(!timeLine.isHasFacebook()) holder.facebook.setVisibility(View.GONE);

        if(!timeLine.isHasGeo()) holder.geo.setVisibility(View.GONE);

        if(!timeLine.isHasPDF()) holder.pdf.setVisibility(View.GONE);

        if(!timeLine.isHasRemind()) holder.remind.setVisibility(View.GONE);

        if(timeLine.getCount() > 0) holder.count.setText(timeLine.getCount());

        holder.date.setText("21:00");

        if(previosTimeline == null) {
            holder.dayTimeline.setText("Today");
            holder.dateTimeline.setText(timeLine.getDate().toString());
            previosTimeline = timeLine;
        }else{
            holder.timelineDateBar.setVisibility(View.GONE);
        }

        if(timeLine.getBottomValue() != null){
            holder.bottomValue.setText(timeLine.getBottomValue());
        }

        holder.arrow.setVisibility(View.GONE);

        if(timeLine.getHasArrow()) {
            holder.arrow.setVisibility(View.VISIBLE);
        }else if(timeLine.isHasPause()){
            holder.arrow.setImageResource(R.drawable.icn_timeline_pause);
            holder.arrow.setVisibility(View.VISIBLE);
        }

        if (timeLine.getContact().getPhotoURL() == null) {
            int nameHash = timeLine.getContact().getName().hashCode();
            timeLine.getContact().color = Color.rgb(Math.abs(nameHash * 28439) % 255, Math.abs(nameHash * 211239) % 255, Math.abs(nameHash * 42368) % 255);
            GradientDrawable circle = (GradientDrawable) ContextCompat.getDrawable(context, R.drawable.blue_circle).mutate();
            circle.setColor(timeLine.getContact().color);
            holder.contactImage.setBackground(circle);
            holder.contactImage.setImageDrawable(null);
            String initials = getInitials(timeLine.getContact());
            holder.initials.setVisibility(View.VISIBLE);
            holder.initials.setText(initials);
        } else {
            holder.initials.setVisibility(View.GONE);
            try {
                holder.contactImage.setImageURI(Uri.parse(timeLine.getContact().getPhotoURL()));
            }catch (Exception e){
                GradientDrawable circle = (GradientDrawable) ContextCompat.getDrawable(context, R.drawable.blue_circle).mutate();
                circle.setColor(timeLine.getContact().color);
                holder.contactImage.setBackground(circle);
                holder.contactImage.setImageDrawable(null);
                String initials = getInitials(timeLine.getContact());
                holder.initials.setVisibility(View.VISIBLE);
                holder.initials.setText(initials);
            }
        }

    }

    @Override
    public int getItemCount() {
        return listOfTimelines.size();
    }

}
