package ai.extime.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import ai.extime.Interfaces.HashtagAddInterface;
import ai.extime.Models.Contact;
import ai.extime.Models.HashTagQuantity;
import com.extime.R;

/**
 * Created by patal on 05.10.2017.
 */

public class MostUsedTagsAdapter extends RecyclerView.Adapter<MostUsedTagsAdapter.HashTagsViewHolder>  {

    private View mainView;

    private Context context;

    private ArrayList<HashTagQuantity> listOfHashtags = new ArrayList<>();

    private HashtagAddInterface hashtagAddimpl;

    private boolean fromPopup  = false;

    private Contact selectedContact;

    private ArrayList<HashTagQuantity> savedTags;

    public void showAllTags(){
        listOfHashtags.clear();
        listOfHashtags.addAll(savedTags);
        notifyDataSetChanged();
    }

    public void showFirstTags(){
        listOfHashtags.clear();
        listOfHashtags.addAll(savedTags.subList(0,2));
        notifyDataSetChanged();
    }

    static class HashTagsViewHolder extends RecyclerView.ViewHolder {

        TextView hashTagValue;
        View card;

        HashTagsViewHolder(View itemView) {
            super(itemView);
            card = itemView;
            hashTagValue = (TextView) itemView.findViewById(R.id.hashtag_edit_value);
        }
    }


    public void setNewHashtags(ArrayList<HashTagQuantity> listOfHashtags){
        this.savedTags = listOfHashtags;
        this.listOfHashtags.clear();
        this.listOfHashtags.addAll(savedTags);
        notifyDataSetChanged();
    }

    public MostUsedTagsAdapter(ArrayList<HashTagQuantity> listOfHashtags, HashtagAddInterface hashtagAddimpl, @Nullable Boolean fromPopup, Contact contact){
        this.savedTags = listOfHashtags;
        this.listOfHashtags.clear();
        this.listOfHashtags.addAll(savedTags);
        this.hashtagAddimpl = hashtagAddimpl;
        this.fromPopup = fromPopup;
        this.selectedContact = contact;
    }

    @Override
    public MostUsedTagsAdapter.HashTagsViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        mainView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_suggest_hashtag, viewGroup, false);
        return new MostUsedTagsAdapter.HashTagsViewHolder(mainView);
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(final MostUsedTagsAdapter.HashTagsViewHolder holder, int position) {
        holder.hashTagValue.setText(listOfHashtags.get(position).getHashTag().getHashTagValue());
        holder.card.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {// РЅР°Р¶Р°С‚РёРµ
                        holder.hashTagValue.setTextColor(mainView.getResources().getColor(R.color.timelineGreen));
                        holder.hashTagValue.setTypeface(null, Typeface.BOLD);
                    }
                    break;
                    case MotionEvent.ACTION_MOVE: // РґРІРёР¶РµРЅРёРµ

                        break;
                    case MotionEvent.ACTION_UP: {
                        holder.hashTagValue.setTextColor(mainView.getResources().getColor(R.color.pink));
                        holder.hashTagValue.setTypeface(null, Typeface.NORMAL);
                        break;// РѕС‚РїСѓСЃРєР°РЅРёРµ
                    }
                    case MotionEvent.ACTION_CANCEL:
                        holder.hashTagValue.setTextColor(mainView.getResources().getColor(R.color.pink));
                        holder.hashTagValue.setTypeface(null, Typeface.NORMAL);
                        break;
                }

                return false;
            }
        });

        if(fromPopup){
            holder.card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hashtagAddimpl.addNewTagToContact(listOfHashtags.get(position).getHashTag().getHashTagValue(),selectedContact);
                }
            });
        }else{
            holder.card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    hashtagAddimpl.addHashtagsToSelectedContacts(listOfHashtags.get(position).getHashTag().getHashTagValue().trim());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return listOfHashtags.size();
    }

}
