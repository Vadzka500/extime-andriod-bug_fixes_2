package ai.extime.Adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberToCarrierMapper;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.Locale;

import io.realm.RealmList;
import ai.extime.Enums.TypeData;
import ai.extime.Models.ContactInfo;
import com.extime.R;

public class ContactNumberCreateAdapter extends RecyclerView.Adapter<ContactNumberCreateAdapter.ContactViewHolder> {

    private RealmList<ContactInfo> contactInfos;
    private Context context;
    private String selectedId = null;
    View v;

    static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView type;
        EditText value;
        TextView value1;
        ImageView contactTypeImage;
        CheckBox isConfirmed;
        TextView carrier;
        ImageView region;
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
        }
    }

    public void updateContactsList(RealmList<ContactInfo> contactInfos){
        this.contactInfos = contactInfos;
        notifyDataSetChanged();
    }

    public ContactNumberCreateAdapter(Context context, RealmList<ContactInfo> contactInfos) {
        this.context = context;
        this.contactInfos = contactInfos;
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_contact_number, viewGroup, false);
        ContactViewHolder contactViewHolder = new ContactViewHolder(v);
        contactViewHolder.setIsRecyclable(false);
        contactInfos = new RealmList<>();
        return contactViewHolder;
    }

    @Override
    public void onBindViewHolder(ContactViewHolder viewHolder, int i) {


        final ContactInfo contactInfo = contactInfos.get(i);

        if (contactInfo.isEmail) {
            viewHolder.contactTypeImage.setImageResource(R.drawable.icn_emails);
            contactInfo.typeData = TypeData.EMAIL.toString();
            viewHolder.regionNA.setVisibility(View.GONE);
        }

        if (contactInfo.isGeo) {
            viewHolder.contactTypeImage.setImageResource(R.drawable.icn_geo);
            contactInfo.typeData = TypeData.LOCATION.toString();
            viewHolder.regionNA.setVisibility(View.GONE);
        }

        if (contactInfo.isNote) {
            viewHolder.contactTypeImage.setImageResource(R.drawable.icn_notes);
            contactInfo.typeData = TypeData.NOTE.toString();
            viewHolder.regionNA.setVisibility(View.GONE);
        }

        if (contactInfo.isPhone) {

            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
            String phoneNumber = contactInfo.value;
            try {
                Phonenumber.PhoneNumber swissNumberProto = phoneUtil.parse(phoneNumber, "");
                PhoneNumberToCarrierMapper carrierMapper = PhoneNumberToCarrierMapper.getInstance();

                String region = "";
                region = phoneUtil.getRegionCodeForCountryCode(swissNumberProto.getCountryCode());

                viewHolder.regionNA.setVisibility(View.VISIBLE);
                if (region.length() > 0) {
                    viewHolder.regionNA.setVisibility(View.GONE);
                    int drawableResourceId = context.getResources().getIdentifier("country_" + region.toLowerCase(), "drawable", context.getPackageName());
                    viewHolder.region.setImageResource(drawableResourceId);
                    viewHolder.region.setVisibility(View.VISIBLE);
                }

                String carrier = "";
                carrier = carrierMapper.getNameForNumber(swissNumberProto, Locale.ENGLISH);

                if (carrier.length() > 0) {
                    viewHolder.carrier.setText("(" + carrier + ")");
                    viewHolder.carrier.setVisibility(View.VISIBLE);
                }


            } catch (NumberParseException e) {
                System.err.println("NumberParseException was thrown: " + e.toString());
            }
            viewHolder.contactTypeImage.setImageResource(R.drawable.icn_phone);
            contactInfo.typeData = TypeData.PHONE.toString();
        }

        viewHolder.isConfirmed.setVisibility(contactInfo.isConfirmed ? View.VISIBLE : View.GONE);
        //viewHolder.value.setText(listOfContactInfo.value);
        viewHolder.value1.setText(contactInfo.value);
        viewHolder.type.setText(contactInfo.type);
    }

    @Override
    public int getItemCount() {

        if(contactInfos == null)
            return 0;
        else
            return contactInfos.size();
    }

    public RealmList<ContactInfo> getContactInfos() {
        return contactInfos;
    }

    public void setContactInfos(RealmList<ContactInfo> contactInfos) {
        this.contactInfos = contactInfos;

        notifyDataSetChanged();
    }
}

