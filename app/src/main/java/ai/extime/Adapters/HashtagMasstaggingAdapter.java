  package ai.extime.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ai.extime.Interfaces.HashtagAddInterface;
import ai.extime.Models.Contact;
import com.extime.R;

  /**
 * Created by patal on 05.10.2017.
 */

public class HashtagMasstaggingAdapter extends RecyclerView.Adapter<HashtagMasstaggingAdapter.HashTagsViewHolder>  {

    private View mainView;

    private Context context;

    private List<String> listOfHashtags = new ArrayList<>();

    private HashtagAddInterface hashtagAddimpl;

    private boolean fromPopup  = false;

    private Contact selectedContact;

    private List<String> savedTags;

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

    public HashtagMasstaggingAdapter(List<String> listOfHashtags, HashtagAddInterface hashtagAddimpl, @Nullable Boolean fromPopup, Contact contact){
        this.savedTags = listOfHashtags;
        this.listOfHashtags.clear();
        this.listOfHashtags.addAll(savedTags);
        this.hashtagAddimpl = hashtagAddimpl;
        this.fromPopup = fromPopup;
        this.selectedContact = contact;
    }

    @Override
    public HashtagMasstaggingAdapter.HashTagsViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        mainView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_suggest_hashtag, viewGroup, false);
        return new HashtagMasstaggingAdapter.HashTagsViewHolder(mainView);
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(final HashtagMasstaggingAdapter.HashTagsViewHolder holder, int position) {
        holder.hashTagValue.setText(listOfHashtags.get(position));
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
                    hashtagAddimpl.addNewTagToContact(listOfHashtags.get(position),selectedContact);
                }
            });
        }else{
            holder.card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            mainView.getContext());
                    alertDialogBuilder.setTitle("Do you want to add new hashtags to contacts?");
                    alertDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("Yes", (dialog, id) -> {
                                hashtagAddimpl.addHashtagsToSelectedContacts(listOfHashtags.get(position).trim());
                            })
                            .setNegativeButton("No", (dialog, id) -> dialog.cancel());

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return listOfHashtags.size();
    }

}
