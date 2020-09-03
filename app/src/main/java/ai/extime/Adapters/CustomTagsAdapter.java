package ai.extime.Adapters;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import ai.extime.Interfaces.HashtagAddInterface;
import ai.extime.Models.Contact;
import com.extime.R;

/**
 * Created by patal on 05.10.2017.
 */

public class CustomTagsAdapter extends RecyclerView.Adapter<CustomTagsAdapter.HashTagsViewHolder>  {

    private View mainView;

    public ArrayList<String> listOfHashtags;

    private Contact selectedContact;

    private boolean editMode = false;

    private boolean saveHashtags = false;

    private HashtagAddInterface hashtagAddInterface;

    static class HashTagsViewHolder extends RecyclerView.ViewHolder {

        EditText hashTagEditValue;
        TextView hashtagValue;
        View card;
        ImageView removeHashtag;
        LinearLayout inputValueLL;

        HashTagsViewHolder(View itemView) {
            super(itemView);
            card = itemView;
            hashTagEditValue = (EditText) itemView.findViewById(R.id.hashtag_edit_value);
            removeHashtag = (ImageView) itemView.findViewById(R.id.trashForRemove);
            hashtagValue = (TextView) itemView.findViewById(R.id.hashtag_value);
            inputValueLL = (LinearLayout) itemView.findViewById(R.id.inputValueLL);
        }
    }

    public CustomTagsAdapter(ArrayList<String> listOfHashtags, Contact contact, HashtagAddInterface hashtagAddInterface){
        this.listOfHashtags = listOfHashtags;
        this.selectedContact = contact;
        this.hashtagAddInterface  = hashtagAddInterface;
    }

    public void setNewContacts(ArrayList<String> listOfHashtags){
        this.listOfHashtags = listOfHashtags;
        notifyDataSetChanged();
    }

    public void stopEditMode(){
        editMode = false;
        notifyDataSetChanged();
    }

    public void startEditMode(){
        editMode = true;

        notifyDataSetChanged();
    }

    public void removeHashtag(String hashtag){
        listOfHashtags.remove(hashtag);
        notifyDataSetChanged();
    }

    public void addHashTags(ArrayList<String> listOfHashtags){
        for (String hashtag:listOfHashtags) {
            this.listOfHashtags.add(hashtag.toLowerCase().trim());
        }
        notifyDataSetChanged();
    }

    public void addHashTag(String hashtag){
        listOfHashtags.add(hashtag);
        notifyDataSetChanged();
    }

    @Override
    public CustomTagsAdapter.HashTagsViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        mainView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_custom_hashtag, viewGroup, false);
        return new CustomTagsAdapter.HashTagsViewHolder(mainView);
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(final CustomTagsAdapter.HashTagsViewHolder holder, int position) {
        String hashtag  = listOfHashtags.get(position);
        if(editMode){
            holder.inputValueLL.setVisibility(View.VISIBLE);
            holder.hashTagEditValue.setVisibility(View.VISIBLE);
            holder.hashtagValue.setVisibility(View.GONE);
            holder.removeHashtag.setVisibility(View.VISIBLE);
            holder.hashTagEditValue.setText(hashtag);
            holder.hashTagEditValue.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        listOfHashtags.set(position, s.toString());
                    }catch (Exception e){

                    }
                }
            });
            holder.removeHashtag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.hashTagEditValue.setText("");
                }
            });
        }else{
            holder.inputValueLL.setVisibility(View.GONE);
            holder.hashTagEditValue.setVisibility(View.GONE);
            holder.hashtagValue.setVisibility(View.VISIBLE);
            holder.removeHashtag.setVisibility(View.GONE);
            holder.hashtagValue.setText(hashtag);
        }

        holder.hashTagEditValue.setText(listOfHashtags.get(position));
        holder.hashTagEditValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    listOfHashtags.set(position, s.toString());
                }catch (Exception e){

                }
            }
        });
        //hashtagAddInterface.updatedUserHashtags(updatedHashtags);
    }

    @Override
    public int getItemCount() {
        return listOfHashtags.size();
    }

}
