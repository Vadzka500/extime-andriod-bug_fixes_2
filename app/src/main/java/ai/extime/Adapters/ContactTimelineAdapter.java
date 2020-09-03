package ai.extime.Adapters;


import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ai.extime.Models.ContactInfo;
import com.extime.R;

public class ContactTimelineAdapter extends RecyclerView.Adapter<ContactTimelineAdapter.ContactViewHolder> {

    private ArrayList<ContactInfo> contactInfos;
    private Context context;
    private boolean editModeEnabled;
    private String selectedId = null;
    private Fragment parentFragment;
    private ContactInfo contactInfoForReplace;
    View v;

    static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView type;
        EditText value;
        TextView value1;
        ImageView contactTypeImage;
        CheckBox isConfirmed;
        TextView carrier;
        ImageView region;
        ImageView createdCall2me;
        TextView regionNA;

        ContactViewHolder(View itemView) {
            super(itemView);
            type = (TextView) itemView.findViewById(R.id.hashtag_text);
            //value = (EditText) itemView.findViewById(R.id.value);
            value1 = (TextView) itemView.findViewById(R.id.value1);
            isConfirmed = (CheckBox) itemView.findViewById(R.id.checkConf);
            contactTypeImage = (ImageView) itemView.findViewById(R.id.type_image);
            carrier = (TextView) itemView.findViewById(R.id.carrier);
            region = (ImageView) itemView.findViewById(R.id.region);
            regionNA = (TextView) itemView.findViewById(R.id.regionNA);
            createdCall2me = (ImageView) itemView.findViewById(R.id.contactFromCall2me);
        }
    }


    public ContactTimelineAdapter(Context context, ArrayList<ContactInfo> contactInfos, Fragment parentFragment) {
        this.context = context;
        this.contactInfos = contactInfos;
        editModeEnabled = false;
        this.parentFragment = parentFragment;
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_contact_timeline, viewGroup, false);
        return new ContactViewHolder(v);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(ContactViewHolder viewHolder, int i) {
        final ContactInfo contactInfo = contactInfos.get(i);
        Date date = new Date(Long.valueOf(contactInfo.value));
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);
        int hours = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);
        viewHolder.value1.setText(dayOfMonth+"."+(month+1)+"."+year+" "+hours+":"+min);
        viewHolder.contactTypeImage.setImageResource(R.drawable.icn_login);
        if(contactInfo.type.equals("Created")) {
            viewHolder.createdCall2me.setImageResource(R.drawable.new_icon);
        }else {
            viewHolder.createdCall2me.setImageResource(R.drawable.icn_phone_gray);
        }
    }

    @Override
    public int getItemCount() {
        return contactInfos.size();
    }

}

