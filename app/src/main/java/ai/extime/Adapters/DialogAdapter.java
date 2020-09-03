package ai.extime.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.extime.R;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberToCarrierMapper;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.ArrayList;
import java.util.Locale;

import ai.extime.Enums.ClipboardEnum;
import ai.extime.Enums.TypeData;
import ai.extime.Models.DataUpdate;
import ai.extime.Utils.ClipboardType;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class DialogAdapter extends RecyclerView.Adapter<DialogAdapter.DialogViewHolder> {

    private View mainView;

    private ArrayList<DataUpdate> list;

    private Context context;


    static class DialogViewHolder extends RecyclerView.ViewHolder {

        TextView type;
        EditText value;
        TextView value1;
        ImageView contactTypeImage;
        CheckBox isConfirmed;
        TextView carrier;
        ImageView region;
        ImageView createdCall2me;
        TextView regionNA;
        FrameLayout frame;

        public DialogViewHolder(@NonNull View itemView) {
            super(itemView);
            frame = (FrameLayout) itemView.findViewById(R.id.frameDataContact);
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

    public DialogAdapter(ArrayList<DataUpdate> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public DialogAdapter.DialogViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mainView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.dialog_number_card, viewGroup, false);
        return new DialogViewHolder(mainView);
    }

    @Override
    public void onBindViewHolder(@NonNull DialogAdapter.DialogViewHolder viewHolder, int i) {

        DataUpdate item = list.get(i);
        // v.setLayoutParams(params2);
        // v.setVisibility(View.VISIBLE);
        viewHolder.frame.setVisibility(View.VISIBLE);

        viewHolder.createdCall2me.setVisibility(View.GONE);
        viewHolder.region.setVisibility(View.GONE);



        Realm realm = Realm.getDefaultInstance(); //-
        realm.beginTransaction();
        viewHolder.type.setText(item.getType().toString().toLowerCase());
        if (item.getType().equals(ClipboardEnum.EMAIL)) {
            viewHolder.contactTypeImage.setImageResource(R.drawable.icn_emails);
            //contactInfo.typeData = TypeData.EMAIL.toString();
            viewHolder.regionNA.setVisibility(View.GONE);
        } else if (item.getType().equals(ClipboardEnum.ADDRESS)) {
            viewHolder.contactTypeImage.setImageResource(R.drawable.icn_geo);

            viewHolder.regionNA.setVisibility(View.GONE);
        } else if (item.getType().equals(ClipboardEnum.DATEOFBIRTH)) {
            viewHolder.contactTypeImage.setImageResource(R.drawable.gift_blue);
            viewHolder.type.setText("date of birth");
            //contactInfo.typeData = TypeData.DATEOFBIRTH.toString();
            viewHolder.regionNA.setVisibility(View.GONE);
        } else if (item.getType().equals(ClipboardEnum.FACEBOOK)) {
            viewHolder.contactTypeImage.setImageResource(R.drawable.icn_social_facebook);
            viewHolder.type.setText("facebook");
        } else if (item.getType().equals(ClipboardEnum.VK)) {
            viewHolder.contactTypeImage.setImageResource(R.drawable.icn_social_vk);
            viewHolder.type.setText("vk");
        } else if (item.getType().equals(ClipboardEnum.LINKEDIN)) {
            viewHolder.contactTypeImage.setImageResource(R.drawable.icn_social_linkedin);
            viewHolder.type.setText("linkedin");
        } else if (item.getType().equals(ClipboardEnum.INSTAGRAM)) {
            viewHolder.contactTypeImage.setImageResource(R.drawable.icn_social_instagram);
            viewHolder.type.setText("instagram");
        } else if (item.getType().equals(ClipboardEnum.TWITTER)) {
            viewHolder.contactTypeImage.setImageResource(R.drawable.ic_twitter_64);
            viewHolder.type.setText("twitter");
        } else if (item.getType().equals(ClipboardEnum.YOUTUBE)) {
            viewHolder.contactTypeImage.setImageResource(R.drawable.ic_youtube_48);
            viewHolder.type.setText("youtube");
        } else if (item.getType().equals(ClipboardEnum.GITHUB)) {
            viewHolder.contactTypeImage.setImageResource(R.drawable.github_logo_64);
            viewHolder.type.setText("github");
        } else if (item.getType().equals(ClipboardEnum.MEDIUM)) {
            viewHolder.contactTypeImage.setImageResource(R.drawable.medium_size_64);
            viewHolder.type.setText("medium");
        } else if (item.getType().equals(ClipboardEnum.VIBER)) {
            viewHolder.contactTypeImage.setImageResource(R.drawable.icn_social_viber);
            viewHolder.type.setText("viber");
        } else if (item.getType().equals(ClipboardEnum.WHATSAPP)) {
            viewHolder.contactTypeImage.setImageResource(R.drawable.icn_social_whatsapp);
            viewHolder.type.setText("whatsapp");
        } else if (item.getType().equals(ClipboardEnum.TELEGRAM)) {
            viewHolder.contactTypeImage.setImageResource(R.drawable.icn_social_telegram);
            viewHolder.type.setText("telegram");
        } else if (item.getType().equals(ClipboardEnum.SKYPE)) {
            viewHolder.contactTypeImage.setImageResource(R.drawable.icn_social_skype);
            viewHolder.type.setText("skype");
        } else if (item.getType().equals(ClipboardEnum.G_DOC)) {
            viewHolder.contactTypeImage.setImageResource(R.drawable.google_docs);
            viewHolder.type.setText("google doc");
        }else if (item.getType().equals(ClipboardEnum.G_SHEET)) {
            viewHolder.contactTypeImage.setImageResource(R.drawable.google_sheets);
            viewHolder.type.setText("google sheet");
        }else if (item.getType().equals(ClipboardEnum.ANGEL)) {
            viewHolder.contactTypeImage.setImageResource(R.drawable.angel);
            viewHolder.type.setText("angel");
        }else if (item.getType().equals(ClipboardEnum.TUMBLR)) {
            viewHolder.contactTypeImage.setImageResource(R.drawable.tumblr);
            viewHolder.type.setText("tumblr");
        } else if (item.getType().equals(ClipboardEnum.WEB)/*matcher.matches() && !contactInfo.value.contains("@")*/) {
            viewHolder.contactTypeImage.setImageResource(R.drawable.icn_popup_web_blue);
            viewHolder.type.setText("web");
        }else if (item.getType().equals(ClipboardEnum.HASHTAG)/*matcher.matches() && !contactInfo.value.contains("@")*/) {
            viewHolder.contactTypeImage.setImageResource(R.drawable.icn_sort_hashtah);
            viewHolder.type.setText("hashtag");
        }else if (item.getType().equals(ClipboardEnum.POSITION)/*matcher.matches() && !contactInfo.value.contains("@")*/) {
            viewHolder.contactTypeImage.setImageResource(R.drawable.position_blue);
            viewHolder.type.setText("position");
        }else if (item.getType().equals(ClipboardEnum.COMPANY)/*matcher.matches() && !contactInfo.value.contains("@")*/) {
            viewHolder.contactTypeImage.setImageResource(R.drawable.icn_companies);
            viewHolder.type.setText("company");
        }else if (item.getType().equals(ClipboardEnum.NAME)/*matcher.matches() && !contactInfo.value.contains("@")*/) {
            viewHolder.contactTypeImage.setImageResource(R.drawable.profile_blue);
            viewHolder.type.setText("name");
        }else if (item.getType().equals(ClipboardEnum.BIO_INTRO)) {
            viewHolder.contactTypeImage.setImageResource(R.drawable.intro_n);
            viewHolder.type.setText("bio/intro");
        } else {
            viewHolder.contactTypeImage.setImageResource(R.drawable.icn_notes);
        }

        viewHolder.regionNA.setVisibility(View.GONE);


        realm.commitTransaction();
        realm.close();



        if (item.getType().equals(ClipboardEnum.PHONE)) {

            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
            String phoneNumber = item.getValue();
            try {
                Phonenumber.PhoneNumber swissNumberProto = phoneUtil.parse(phoneNumber, "");
                PhoneNumberToCarrierMapper carrierMapper = PhoneNumberToCarrierMapper.getInstance();

                String region = "";
                region = phoneUtil.getRegionCodeForCountryCode(swissNumberProto.getCountryCode());

                viewHolder.regionNA.setVisibility(View.VISIBLE);




                if (region.length() > 0) {
                    viewHolder.regionNA.setVisibility(View.GONE);
                    try {


                        int drawableResourceId = context.getResources().getIdentifier("country_" + region.toLowerCase(), "drawable", context.getPackageName());
                        viewHolder.region.setImageResource(drawableResourceId);
                        viewHolder.region.setVisibility(View.VISIBLE);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }

                String carrier = "";
                carrier = carrierMapper.getNameForNumber(swissNumberProto, Locale.ENGLISH);

                if (carrier.length() > 0) {
                    viewHolder.carrier.setText("(" + carrier + ")");
                    viewHolder.carrier.setVisibility(View.VISIBLE);
                }


            } catch (NumberParseException e) {
                System.err.println("NumberParseException was thrown: " + e.toString());
                String str = phoneNumber.replaceAll("[\\s\\-\\+\\(\\)]", "");
                if ((str.length() == 11 && str.charAt(0) == '8') || (str.length() == 12 && str.charAt(0) == '+' && str.charAt(1) == '8')) {

                    //String region = "ru";
                    viewHolder.regionNA.setVisibility(View.GONE);
                    try {
                        int drawableResourceId = context.getResources().getIdentifier("country_ru", "drawable", context.getPackageName());
                        viewHolder.region.setImageResource(drawableResourceId);
                        viewHolder.region.setVisibility(View.VISIBLE);
                    } catch (NullPointerException e1) {
                        e1.printStackTrace();
                    }
                }
            }
            viewHolder.contactTypeImage.setImageResource(R.drawable.icn_phone);


            //realm.beginTransaction();

            //contactInfo.typeData = TypeData.PHONE.toString();
            //realm.commitTransaction();
        }

        //viewHolder.isConfirmed.setVisibility(contactInfo.isConfirmed ? View.VISIBLE : View.GONE);
        //viewHolder.value.setText(listOfContactInfo.value);


        //===========================================


        String text = item.getValue();
        if (item.getType().equals(ClipboardEnum.INSTAGRAM)) {
            //text = contact.getSocialModel().getInstagramLink();

            if (text.contains(".com/")) {
                int ind = text.indexOf(".com/");
                text = text.substring(ind + 5, text.length());

                if (text.contains("?")) {
                    int in = text.indexOf("?");
                    text = text.substring(0, in);
                }
            }
        }


        viewHolder.value1.setText(text);
      //  if (text != null && text.length() > 40)
      //      viewHolder.value1.setText(text.substring(0, 38) + "...");


    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
