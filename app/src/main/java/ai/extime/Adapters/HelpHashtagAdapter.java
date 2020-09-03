package ai.extime.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ai.extime.Fragments.ContactsFragment;
import ai.extime.Models.HashTagQuantity;
import com.extime.R;

/**
 * Created by patal on 05.10.2017.
 */

public class HelpHashtagAdapter extends RecyclerView.Adapter<HelpHashtagAdapter.HashTagsViewHolder>  {

    private View mainView;

    private Context context;

    private List<HashTagQuantity> listOfHashtags = new ArrayList<>();

    private List<HashTagQuantity> savedlistOfHashtags = new ArrayList<>();

    private List<Integer> checkHash = new ArrayList<>();

    private ContactsFragment contactsFragment;

    private boolean sortAsc = true;

    private boolean sortAscByPopul = false;

    private boolean sortAscByTime = true;

    private String findStr;

    public boolean getsortAsc(){
        return  sortAsc;
    }

    public  boolean getsortpopul(){
        return  sortAscByPopul;
    }

    public boolean getsorttime(){
        return sortAscByTime;
    }

    public void setsortAsc(boolean c){
        sortAsc = c;
    }

    public void setSortByPpul(boolean c){
        sortAscByPopul = c;
    }

    public void setSortByTime(boolean c){
        sortAscByTime = c;
    }

    public void sortList(){
        if(sortAsc)  sortByDesc();
        else sortByAsc();
    }

    public void sortListByPopul(){

        if(sortAscByPopul)  sortByDescPopul();
        else sortByAscPopul();
    }

    public void sortListByTime(){
        if(sortAscByTime)  sortByDescTime();
        else sortByAscTime();
    }

    private void sortByDescTime(){


         /* listOfHashtags.clear();
          listOfHashtags.addAll(savedlistOfHashtags);
*/
        Collections.sort(listOfHashtags, (hashTagFirst, hashTagSecond) -> hashTagFirst.getHashTag().getHashTagValue().compareToIgnoreCase(hashTagSecond.getHashTag().getHashTagValue()));
        Collections.sort(listOfHashtags, (hashTagFirst, hashTagSecond) -> hashTagSecond.getHashTag().getDate().compareTo(hashTagFirst.getHashTag().getDate()));
        sortAscByTime = false;
        notifyDataSetChanged();
    }

    private void sortByAscTime(){

         /* listOfHashtags.clear();
          listOfHashtags.addAll(savedlistOfHashtags);*/

        Collections.sort(listOfHashtags, (hashTagFirst, hashTagSecond) -> hashTagFirst.getHashTag().getHashTagValue().compareToIgnoreCase(hashTagSecond.getHashTag().getHashTagValue()));
        Collections.sort(listOfHashtags, (hashTagFirst, hashTagSecond) -> hashTagFirst.getHashTag().getDate().compareTo(hashTagSecond.getHashTag().getDate()));
        sortAscByTime = true;
        notifyDataSetChanged();
    }

    private void sortByDescPopul(){
        Collections.sort(listOfHashtags, new Comparator<HashTagQuantity>() {
            @Override
            public int compare(HashTagQuantity first, HashTagQuantity second) {
                return second.getQuantity() - first.getQuantity();
            }
        });
        sortAscByPopul = false;
        notifyDataSetChanged();
    }

    private void sortByAscPopul(){
        Collections.sort(listOfHashtags, new Comparator<HashTagQuantity>() {
            @Override
            public int compare(HashTagQuantity first, HashTagQuantity second) {
                return first.getQuantity() - second.getQuantity();
            }
        });
        sortAscByPopul = true;
        notifyDataSetChanged();
    }

    private void sortByDesc(){
        Collections.sort(listOfHashtags, (hashTagFirst, hashTagSecond) -> hashTagSecond.getHashTag().getHashTagValue().compareToIgnoreCase(hashTagFirst.getHashTag().getHashTagValue()));
        sortAsc = false;
        notifyDataSetChanged();
    }

    private void sortByAsc(){
        Collections.sort(listOfHashtags, (hashTagFirst, hashTagSecond) -> hashTagFirst.getHashTag().getHashTagValue().compareToIgnoreCase(hashTagSecond.getHashTag().getHashTagValue()));
        sortAsc = true;

        notifyDataSetChanged();
    }

    static class HashTagsViewHolder extends RecyclerView.ViewHolder {

        TextView hashTagValue;
        CheckBox checkBox;
        View card;

        HashTagsViewHolder(View itemView) {
            super(itemView);
            card = itemView;
            checkBox = (CheckBox) itemView.findViewById(R.id.helpHashCheckBox);
            hashTagValue = (TextView) itemView.findViewById(R.id.hashtag_edit_value);
        }
    }

    public HelpHashtagAdapter(List<HashTagQuantity> listOfHashtags, ContactsFragment contactsFragment, View mainView){
        this.savedlistOfHashtags.addAll(listOfHashtags);
        this.contactsFragment = contactsFragment;
        this.listOfHashtags.clear();
        this.listOfHashtags.addAll(savedlistOfHashtags);
        this.mainView = mainView;
    }

    public void setListHashTag(List<HashTagQuantity> listOfHashtags){
        this.savedlistOfHashtags.clear();
        this.savedlistOfHashtags.addAll(listOfHashtags);

        this.listOfHashtags.clear();
        this.listOfHashtags.addAll(savedlistOfHashtags);
        notifyDataSetChanged();

    }

    public void findByStr(String findStr){
        this.findStr = findStr;
        listOfHashtags.clear();
        for (HashTagQuantity hashTagQuantity:savedlistOfHashtags) {
            if(hashTagQuantity.getHashTag().getHashTagValue().contains(findStr) && hashTagQuantity.getHashTag().getHashTagValue().trim().split(" ").length  == 1)
                listOfHashtags.add(hashTagQuantity);
            if(hashTagQuantity.getHashTag().getHashTagValue().compareTo(findStr.trim())==0){

                //contactsFragment.closeHelpHashtagsPopup();
            }
        }
        if(listOfHashtags.size() == 1 && listOfHashtags.get(0).getHashTag().getHashTagValue().equals(findStr)){
            //contactsFragment.closeHelpHashtagsPopup();
        }
        if(listOfHashtags.size()==0) {
            contactsFragment.closeHelpHashtagsPopup();

        }else
            contactsFragment.foundHelpsHashtags = true;
        notifyDataSetChanged();
    }


    @Override
    public HelpHashtagAdapter.HashTagsViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        mainView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_help_hashtag, viewGroup, false);
        return new HelpHashtagAdapter.HashTagsViewHolder(mainView);
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(final HelpHashtagAdapter.HashTagsViewHolder holder, int position) {

        HashTagQuantity hashTagQuantity  = listOfHashtags.get(position);

        if(findStr != ""){
            try {
                int startI = hashTagQuantity.getHashTag().getHashTagValue().toLowerCase().indexOf(findStr.toLowerCase());
                final SpannableStringBuilder text = new SpannableStringBuilder(hashTagQuantity.getHashTag().getHashTagValue());
                final ForegroundColorSpan style = new ForegroundColorSpan(Color.parseColor("#1976D2"));
                StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
                text.setSpan(style, startI, startI + findStr.length(), Spannable.SPAN_COMPOSING);
                text.setSpan(bss, startI, startI + findStr.length(), Spannable.SPAN_COMPOSING);
                holder.hashTagValue.setText(text);
            }catch (Exception e){
                //e.printStackTrace();
                holder.hashTagValue.setText(hashTagQuantity.getHashTag().getHashTagValue());
            }
        }else
            holder.hashTagValue.setText(hashTagQuantity.getHashTag().getHashTagValue());

        Integer integer = new Integer(position);
        if(checkHash.contains(integer)){
            holder.checkBox.setChecked(true);
            holder.hashTagValue.setTextColor(contactsFragment.getActivity().getResources().getColor(R.color.primary));
        }else{
            holder.checkBox.setChecked(false);
            holder.hashTagValue.setTextColor(contactsFragment.getActivity().getResources().getColor(R.color.gray));
        }


        if(holder.checkBox.isChecked())
            holder.hashTagValue.setTextColor(contactsFragment.getActivity().getResources().getColor(R.color.primary));
        else
            holder.hashTagValue.setTextColor(contactsFragment.getActivity().getResources().getColor(R.color.gray));


        holder.hashTagValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                contactsFragment.setHashTagToMagicString(listOfHashtags.get(position).getHashTag().getHashTagValue());
                contactsFragment.closeHelpHashtagsPopup();
            }
        });

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.checkBox.isChecked()){
                    holder.hashTagValue.setTextColor(contactsFragment.getActivity().getResources().getColor(R.color.primary));
                    checkHash.add(position);
                    contactsFragment.hideHelpHash = false;
                    contactsFragment.setHashTagToMagicString(listOfHashtags.get(position).getHashTag().getHashTagValue());

                    String magicText = ((EditText) contactsFragment.getActivity().findViewById(R.id.magic_edit_text)).getText().toString();

                    if (magicText.trim().equals(""))
                        ((EditText) contactsFragment.getActivity().findViewById(R.id.magic_edit_text)).append("#");
                    else {
                        if(magicText.charAt(magicText.length()-1) != '#') {
                            if (magicText.charAt(magicText.length() - 1) == ' ')
                                ((EditText) contactsFragment.getActivity().findViewById(R.id.magic_edit_text)).append("#");
                            else
                                ((EditText) contactsFragment.getActivity().findViewById(R.id.magic_edit_text)).append(" #");
                        }
                    }
                    contactsFragment.hideHelpHash = true;
                }else{
                    holder.hashTagValue.setTextColor(contactsFragment.getActivity().getResources().getColor(R.color.gray));
                    checkHash.remove(checkHash.indexOf(position));

                    contactsFragment.hideHelpHash = false;
                    String magicText = ((EditText) contactsFragment.getActivity().findViewById(R.id.magic_edit_text)).getText().toString();
                    if(magicText.contains(listOfHashtags.get(position).getHashTag().getHashTagValue()));
                    magicText = magicText.replace(listOfHashtags.get(position).getHashTag().getHashTagValue()+" ", "");

                    ((EditText) contactsFragment.getActivity().findViewById(R.id.magic_edit_text)).setText(magicText);

                    ((EditText) contactsFragment.getActivity().findViewById(R.id.magic_edit_text)).setSelection(((EditText) contactsFragment.getActivity().findViewById(R.id.magic_edit_text)).getText().length());

                    contactsFragment.hideHelpHash = true;

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listOfHashtags.size();
    }

}
