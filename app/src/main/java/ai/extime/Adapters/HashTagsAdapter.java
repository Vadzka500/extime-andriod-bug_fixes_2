package ai.extime.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import ai.extime.Activity.MainActivity;
import ai.extime.Fragments.ContactsFragment;
import ai.extime.Models.ContactInfo;
import ai.extime.Models.HashTag;
import ai.extime.Models.HashTagQuantity;
import ai.extime.Utils.ClipboardType;

import com.extime.R;

/**
 * Created by patal on 05.10.2017.
 */

public class HashTagsAdapter extends RecyclerView.Adapter<HashTagsAdapter.HashTagsViewHolder> {

    private Context context;

    public ArrayList<HashTagQuantity> listOfHashtags;

    private Set<String> hashBind;
    private Set<String> hashPin;

    public ArrayList<HashTagQuantity> listOfHashtagsCheck;
    public ArrayList<String> listOfHashtagsCheck2;

    private ContactsFragment contactsFragment;

    public boolean allHashtagsCheck;

    public String hyperHashtag;

    String selectedHashtag;

    public boolean sortAsc = false;
    public boolean sorttime = false;

    public boolean sortAscByPopul = false;

    public boolean sortAscByTime = true;

    public int quantityCheck = 0;

    public static int countAll = 0;

    View mainView;

    public boolean fromhyper = false;

    public SharedPreferences mPref;

    public Activity activity;

    public ArrayList<HashTagQuantity> getListOfHashtags() {
        return listOfHashtags;
    }

    static class HashTagsViewHolder extends RecyclerView.ViewHolder {

        CheckBox hashTagSelect;
        TextView hashTagValue;
        TextView hashTagQuantity;
        LinearLayout hyperHashTag;
        View card;

        HashTagsViewHolder(View itemView) {
            super(itemView);
            card = itemView;
            hashTagSelect = (CheckBox) itemView.findViewById(R.id.hashtag_check);
            hashTagValue = (TextView) itemView.findViewById(R.id.hashtag_edit_value);
            hashTagQuantity = (TextView) itemView.findViewById(R.id.quantityHashTags);
            hyperHashTag = (LinearLayout) itemView.findViewById(R.id.hyperHashTag);
        }
    }

    public void sortHashtag() {
        SharedPreferences mPref = context.getSharedPreferences("Sort", Context.MODE_PRIVATE);

        String sort = mPref.getString("typeSortHashtag", "sortByDescPopul");
        //sort = "sortByDescPopul";

        if (sort.equals("sortByAsc")) {
            sortByAsc();
            ((ImageView) activity.findViewById(R.id.timeSortH)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
            ((ImageView) activity.findViewById(R.id.populTagH)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
            ((TextView) activity.findViewById(R.id.sortTextH)).setTextColor(context.getResources().getColor(R.color.primary));
            ((TextView) activity.findViewById(R.id.sortTextH)).setText("A-Z");
            //((TextView) getActivity().findViewById(R.id.sortText)).setTextColor(getResources().getColor(R.color.primary));
            //((ImageView) getActivity().findViewById(R.id.timeSort)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
        } else if (sort.equals("sortByDesc")) {
            sortByDesc();
            ((ImageView) activity.findViewById(R.id.timeSortH)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
            ((ImageView) activity.findViewById(R.id.populTagH)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
            ((TextView) activity.findViewById(R.id.sortTextH)).setTextColor(context.getResources().getColor(R.color.primary));
            ((TextView) activity.findViewById(R.id.sortTextH)).setText("Z-A");
            //((TextView) getActivity().findViewById(R.id.sortText)).setTextColor(getResources().getColor(R.color.primary));
            //((ImageView) getActivity().findViewById(R.id.timeSort)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
        } else if (sort.equals("sortByTimeAsc")) {
            sortByAscTime();
            ((ImageView) activity.findViewById(R.id.timeSortH)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));
            ((ImageView) activity.findViewById(R.id.populTagH)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
            ((TextView) activity.findViewById(R.id.sortTextH)).setTextColor(context.getResources().getColor(R.color.gray));
            //((TextView) getActivity().findViewById(R.id.sortText)).setTextColor(getResources().getColor(R.color.gray));
            //((ImageView) getActivity().findViewById(R.id.timeSort)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));
        } else if (sort.equals("sortByTimeDesc")) {
            sortByDescTime();
            ((ImageView) activity.findViewById(R.id.timeSortH)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));
            ((ImageView) activity.findViewById(R.id.populTagH)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
            ((TextView) activity.findViewById(R.id.sortTextH)).setTextColor(context.getResources().getColor(R.color.gray));
            //((TextView) getActivity().findViewById(R.id.sortText)).setTextColor(getResources().getColor(R.color.gray));
            //((ImageView) getActivity().findViewById(R.id.timeSort)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));
        } else if (sort.equals("sortByAscPopul")) {
            sortByAscPopul();
            ((ImageView) activity.findViewById(R.id.timeSortH)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
            ((ImageView) activity.findViewById(R.id.populTagH)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));
            ((TextView) activity.findViewById(R.id.sortTextH)).setTextColor(context.getResources().getColor(R.color.gray));
        } else if (sort.equals("sortByDescPopul")) {
            sortByDescPopul();
            ((ImageView) activity.findViewById(R.id.timeSortH)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
            ((ImageView) activity.findViewById(R.id.populTagH)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));
            ((TextView) activity.findViewById(R.id.sortTextH)).setTextColor(context.getResources().getColor(R.color.gray));
        }

    }

    public void checkByHashTag(String hashtag) {
        hyperHashtag = hashtag;
        allHashtagsCheck = false;
        contactsFragment.addToListByHashTag(hashtag, false);
        notifyDataSetChanged();
    }

    public void setAllHashtagsCheck(boolean allHashtagsCheck) {

        this.allHashtagsCheck = allHashtagsCheck;
        ContactsFragment.mergedContacts = false;

        listOfHashtagsCheck = new ArrayList<>();
        listOfHashtagsCheck2 = new ArrayList<>();

        if (allHashtagsCheck) {
            listOfHashtagsCheck.addAll(listOfHashtags);
            for (HashTagQuantity h : listOfHashtags) {
                listOfHashtagsCheck2.add(h.getHashTag().getHashTagValue());
            }
        }

        // if(!allHashtagsCheck)  quantityCheck = listOfHashtags.size();
        try {
            notifyDataSetChanged();
        } catch (Exception e) {
        }
    }

    public HashTagSearchAdapter hashTagSearchAdapter;

    public HashTagsAdapter(Context context, ArrayList<HashTagQuantity> listOfHashTags, ContactsFragment contactsFragment, View mainView, Activity activity, Set<String> hashBind, Set<String> hashPin, HashTagSearchAdapter hashTagSearchAdapter) {
        this.context = context;
        this.listOfHashtags = listOfHashTags;
        this.contactsFragment = contactsFragment;
        this.mainView = mainView;
        this.activity = activity;
        mPref = context.getSharedPreferences("Sort", Context.MODE_PRIVATE);
        countAll = listOfHashTags.size();
        quantityCheck = countAll;
        listOfHashtagsCheck = new ArrayList<>();
        listOfHashtagsCheck2 = new ArrayList<>();

        this.hashTagSearchAdapter = hashTagSearchAdapter;

        if (hashBind == null) hashBind = new HashSet<String>();
        if (hashPin == null) hashPin = new HashSet<String>();

        this.hashBind = hashBind;
        this.hashPin = hashPin;


        sortHashtag();
        //    quantityCheck = listOfHashTags.size();
    }

    public void updateHash(ArrayList<HashTagQuantity> listOfHashTags) {
        this.listOfHashtags = listOfHashTags;
        //countAll = listOfHashTags.size();
        //quantityCheck = countAll;
        sortHashtag();
        notifyDataSetChanged();
    }


    public void sortList() {
        if (sortAsc) sortByDesc();
        else sortByAsc();
    }

    public void sortListByPopul() {
        if (sortAscByPopul) sortByDescPopul();
        else sortByAscPopul();
    }

    public void sortListByTime() {
        if (sortAscByTime) sortByDescTime();
        else sortByAscTime();
    }

    private void sortByDescTime() {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString("typeSortHashtag", "sortByTimeDesc");
        editor.apply();

        Collections.sort(listOfHashtags, (hashTagFirst, hashTagSecond) -> hashTagSecond.getHashTag().getDate().compareTo(hashTagFirst.getHashTag().getDate()));
        sortAscByTime = false;

        for (int i = 0; i < listOfHashtags.size(); i++) {
            if (hashPin.contains(listOfHashtags.get(i).getHashTag().getHashTagValue())) {
                HashTagQuantity h = listOfHashtags.get(i);
                listOfHashtags.remove(i);
                listOfHashtags.add(0, h);

            }
        }

        notifyDataSetChanged();
    }

    private void sortByAscTime() {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString("typeSortHashtag", "sortByTimeAsc");
        editor.apply();
        Collections.sort(listOfHashtags, (hashTagFirst, hashTagSecond) -> hashTagFirst.getHashTag().getDate().compareTo(hashTagSecond.getHashTag().getDate()));
        sortAscByTime = true;

        for (int i = 0; i < listOfHashtags.size(); i++) {
            if (hashPin.contains(listOfHashtags.get(i).getHashTag().getHashTagValue())) {
                HashTagQuantity h = listOfHashtags.get(i);
                listOfHashtags.remove(i);
                listOfHashtags.add(0, h);

            }
        }

        notifyDataSetChanged();
    }

    private void sortByDescPopul() {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString("typeSortHashtag", "sortByDescPopul");
        editor.apply();
        Collections.sort(listOfHashtags, new Comparator<HashTagQuantity>() {
            @Override
            public int compare(HashTagQuantity first, HashTagQuantity second) {
                return second.getQuantity() - first.getQuantity();
            }
        });
        sortAscByPopul = false;

        for (int i = 0; i < listOfHashtags.size(); i++) {
            if (hashPin.contains(listOfHashtags.get(i).getHashTag().getHashTagValue())) {
                HashTagQuantity h = listOfHashtags.get(i);
                listOfHashtags.remove(i);
                listOfHashtags.add(0, h);

            }
        }


        notifyDataSetChanged();
    }

    private void sortByAscPopul() {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString("typeSortHashtag", "sortByAscPopul");
        editor.apply();
        Collections.sort(listOfHashtags, new Comparator<HashTagQuantity>() {
            @Override
            public int compare(HashTagQuantity first, HashTagQuantity second) {
                return first.getQuantity() - second.getQuantity();
            }
        });
        sortAscByPopul = true;

        for (int i = 0; i < listOfHashtags.size(); i++) {
            if (hashPin.contains(listOfHashtags.get(i).getHashTag().getHashTagValue())) {
                HashTagQuantity h = listOfHashtags.get(i);
                listOfHashtags.remove(i);
                listOfHashtags.add(0, h);

            }
        }

        /*for(int i = listOfHashtags.size() - 1; i >=0; i--){
            if(hashPin.contains(listOfHashtags.get(i).getHashTag().getHashTagValue())){
                HashTagQuantity h = listOfHashtags.get(i);
                listOfHashtags.remove(i);
                listOfHashtags.add(0, h);
            }
        }*/

        notifyDataSetChanged();
    }

    private void sortByDesc() {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString("typeSortHashtag", "sortByDesc");
        editor.apply();
        Collections.sort(listOfHashtags, new Comparator<HashTagQuantity>() {
            @Override
            public int compare(HashTagQuantity o1, HashTagQuantity o2) {
                return o2.getHashTag().getHashTagValue().compareToIgnoreCase(o1.getHashTag().getHashTagValue());
            }
        });
        sortAsc = false;

        for (int i = 0; i < listOfHashtags.size(); i++) {
            if (hashPin.contains(listOfHashtags.get(i).getHashTag().getHashTagValue())) {
                HashTagQuantity h = listOfHashtags.get(i);
                listOfHashtags.remove(i);
                listOfHashtags.add(0, h);

            }
        }

       /* for(int i = listOfHashtags.size() - 1; i >=0; i--){
            if(hashPin.contains(listOfHashtags.get(i).getHashTag().getHashTagValue())){
                HashTagQuantity h = listOfHashtags.get(i);
                listOfHashtags.remove(i);
                listOfHashtags.add(0, h);

            }
        }*/

        notifyDataSetChanged();
    }

    private void sortByAsc() {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString("typeSortHashtag", "sortByAsc");
        editor.apply();
        Collections.sort(listOfHashtags, new Comparator<HashTagQuantity>() {
            @Override
            public int compare(HashTagQuantity o1, HashTagQuantity o2) {
                return o1.getHashTag().getHashTagValue().compareToIgnoreCase(o2.getHashTag().getHashTagValue());
            }
        });
        sortAsc = true;

        for (int i = 0; i < listOfHashtags.size(); i++) {
            if (hashPin.contains(listOfHashtags.get(i).getHashTag().getHashTagValue())) {
                HashTagQuantity h = listOfHashtags.get(i);
                listOfHashtags.remove(i);
                listOfHashtags.add(0, h);

            }
        }

       /* for(int i = listOfHashtags.size() - 1; i >=0; i--){
            if(hashPin.contains(listOfHashtags.get(i).getHashTag().getHashTagValue())){
                HashTagQuantity h = listOfHashtags.get(i);
                listOfHashtags.remove(i);
                listOfHashtags.add(0, h);
            }
        }*/

        notifyDataSetChanged();
    }

    @Override
    public HashTagsAdapter.HashTagsViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        mainView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_hashtag, viewGroup, false);
        mPref = context.getSharedPreferences("Sort", Context.MODE_PRIVATE);
        return new HashTagsAdapter.HashTagsViewHolder(mainView);
    }

    @Override
    public void onBindViewHolder(final HashTagsAdapter.HashTagsViewHolder holder, int position) {


        HashTagQuantity hashTag = listOfHashtags.get(position);
        holder.hashTagQuantity.setText("(" + hashTag.getQuantity() + ")");
        holder.hashTagValue.setText(hashTag.getHashTag().getHashTagValue());
        holder.hashTagValue.requestLayout();

        holder.itemView.findViewById(R.id.hash_pin).setVisibility(View.GONE);
        holder.itemView.findViewById(R.id.hash_bind).setVisibility(View.GONE);

        if (hashBind.contains(hashTag.getHashTag().getHashTagValue())) {
            holder.itemView.findViewById(R.id.hash_bind).setVisibility(View.VISIBLE);
        }

        if (hashPin.contains(hashTag.getHashTag().getHashTagValue())) {
            holder.itemView.findViewById(R.id.hash_pin).setVisibility(View.VISIBLE);
        }

        //holder.itemView.findViewById(R.id.ll_hash).requestLayout();

        holder.hashTagSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isChecked = holder.hashTagSelect.isChecked();
                if (isChecked) {

                    quantityCheck++;
                    if (quantityCheck == listOfHashtags.size()) {
                        //((TextView)contactsFragment.getActivity().findViewById(R.id.barHashtag_count)).setText(quantityCheck);
                        //((TextView)contactsFragment.getActivity().findViewById(R.id.barHashtag_count)).setText(" "+quantityCheck+"");
                    }
                    //   if(position+1==listOfHashtags.size()) ((TextView)contactsFragment.getActivity().findViewById(R.id.barHashtag_count)).setText(" "+quantityCheck+"");
                    //else
                    if (listOfHashtagsCheck2 != null && !listOfHashtagsCheck2.contains(hashTag.getHashTag().getHashTagValue())) {
                        listOfHashtagsCheck.add(hashTag);
                        listOfHashtagsCheck2.add(hashTag.getHashTag().getHashTagValue());
                    }
                    if (quantityCheck < countAll)
                        ((TextView) contactsFragment.getActivity().findViewById(R.id.barHashtag_count)).setText("(" + quantityCheck + ")");
                    else
                        ((TextView) contactsFragment.getActivity().findViewById(R.id.barHashtag_count)).setText(" " + quantityCheck + "");
                    //contactsFragment.addToListByHashTag(hashTag.getHashTag().getHashTagValue(),quantityCheck == listOfHashtags.size());

                    if (((RadioButton) contactsFragment.getActivity().findViewById(R.id.radioOR)).isChecked())
                        contactsFragment.addToListByHashTagNEW(listOfHashtagsCheck, hashTag.getHashTag().getHashTagValue());
                    else
                        contactsFragment.addToListByHashTagNEWAND(listOfHashtagsCheck, listOfHashtagsCheck2);


                } else {

                    if (listOfHashtagsCheck2 != null && listOfHashtagsCheck2.contains(hashTag.getHashTag().getHashTagValue())) {
                        listOfHashtagsCheck.remove(hashTag);
                        listOfHashtagsCheck2.remove(hashTag.getHashTag().getHashTagValue());
                    }


                    quantityCheck--;

                    if (quantityCheck < countAll)
                        ((TextView) contactsFragment.getActivity().findViewById(R.id.barHashtag_count)).setText("(" + quantityCheck + ")");
                    else if (quantityCheck == countAll)
                        ((TextView) contactsFragment.getActivity().findViewById(R.id.barHashtag_count)).setText(" " + quantityCheck + "");


                    //contactsFragment.removeFromListByHashTag(hashTag.getHashTag().getHashTagValue(), true);
                    if (((RadioButton) contactsFragment.getActivity().findViewById(R.id.radioOR)).isChecked())
                        contactsFragment.removeFromListByHashTagNEW(hashTag.getHashTag().getHashTagValue(), listOfHashtagsCheck, true);
                    else
                        contactsFragment.addToListByHashTagNEWAND(listOfHashtagsCheck, listOfHashtagsCheck2);
                    //     ((CheckBox) MainActivity.getactivity().findViewById(R.id.NoTagsCheck)).setChecked(false);


                }
                if (quantityCheck == listOfHashtags.size() && !((RadioButton) contactsFragment.getActivity().findViewById(R.id.radioAND)).isChecked()) {
                    contactsFragment.checkAllHashtags();
                }

                if (contactsFragment.mode == 2 && ((MainActivity) contactsFragment.getActivity()).selectedFragment.getClass().equals(ContactsFragment.class))
                    contactsFragment.setKanbanMode();

                // contactsFragment.getActivity().findViewById(R.id.frame_select_bar).setVisibility(View.GONE);
            }
        });
        /*holder.hashTagSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){

                    quantityCheck++;
                    if(position+1==listOfHashtags.size()) ((TextView)contactsFragment.getActivity().findViewById(R.id.barHashtag_count)).setText(" "+quantityCheck+"");
                    else ((TextView)contactsFragment.getActivity().findViewById(R.id.barHashtag_count)).setText(" "+quantityCheck+"");
                    contactsFragment.addToListByHashTag(hashTag.getHashTag().getHashTagValue(),quantityCheck == listOfHashtags.size());
                }
                else {
                    quantityCheck--;
                    ((TextView)contactsFragment.getActivity().findViewById(R.id.barHashtag_count)).setText(" "+quantityCheck+"");

                    contactsFragment.removeFromListByHashTag(hashTag.getHashTag().getHashTagValue(), true);
               //     ((CheckBox) MainActivity.getactivity().findViewById(R.id.NoTagsCheck)).setChecked(false);


                }
            }
        });*/

        if (allHashtagsCheck) {
            holder.hashTagSelect.setChecked(true);
        } else {
            holder.hashTagSelect.setChecked(false);
        }
        //   holder.hashTagSelect.setChecked(false);

        /*if(hyperHashtag!=null) {
            if (hyperHashtag.trim().compareTo(hashTag.getHashTag().getHashTagValue().trim()) == 0) {
                holder.hashTagSelect.setChecked(true);
                //hyperHashtag = null;
            }
        }*/

        if (listOfHashtagsCheck2.contains(hashTag.getHashTag().getHashTagValue())) {
            holder.hashTagSelect.setChecked(true);
        }

        final boolean[] check_click = {false};

        holder.hyperHashTag.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                activity.findViewById(R.id.popup_menu_hashtag).setVisibility(View.VISIBLE);
                ((TextView) activity.findViewById(R.id.menu_hashtag_text)).setText(hashTag.getHashTag().getHashTagValue());
                ((TextView) activity.findViewById(R.id.menu_hashtag_text)).requestLayout();


                if(hashPin.contains(hashTag.getHashTag().getHashTagValue())){
                    ((ImageView) activity.findViewById(R.id.pin_icon)).setColorFilter(ContextCompat.getColor(activity, R.color.md_red_900), PorterDuff.Mode.SRC_IN);
                    ((TextView) activity.findViewById(R.id.pin_text)).setText("Unpin from top");
                }else{
                    ((ImageView) activity.findViewById(R.id.pin_icon)).setColorFilter(ContextCompat.getColor(activity, R.color.primary), PorterDuff.Mode.SRC_IN);
                    ((TextView) activity.findViewById(R.id.pin_text)).setText("Pin to top");
                }

               /* if(hashBind.contains(hashTag.getHashTag().getHashTagValue())){
                    activity.findViewById(R.id.check_bind).setVisibility(View.VISIBLE);
                }else{
                    activity.findViewById(R.id.check_bind).setVisibility(View.GONE);
                }

                if(hashPin.contains(hashTag.getHashTag().getHashTagValue())){
                    activity.findViewById(R.id.check_pin).setVisibility(View.VISIBLE);
                }else{
                    activity.findViewById(R.id.check_pin).setVisibility(View.GONE);
                }*/

                activity.findViewById(R.id.hashtag_menu_share_whatsapp).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        shareContactToPackage("com.whatsapp", hashTag.getHashTag().getHashTagValue());
                    }
                });

                activity.findViewById(R.id.hashtag_menu_share_telegram).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        shareContactToPackage("org.telegram.messenger", hashTag.getHashTag().getHashTagValue());
                    }
                });

                activity.findViewById(R.id.hashtag_menu_share_slack).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        shareContactToPackage("com.Slack", hashTag.getHashTag().getHashTagValue());
                    }
                });

                activity.findViewById(R.id.menu_hashtag_share).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        shareHashtag(hashTag.getHashTag().getHashTagValue());
                    }
                });

                activity.findViewById(R.id.select_one_hash).setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        check_click[0] = true;
                        holder.hyperHashTag.callOnClick();
                        check_click[0] = false;
                        return true;
                    }
                });

                activity.findViewById(R.id.select_one_hash).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activity.findViewById(R.id.popup_menu_hashtag).setVisibility(View.GONE);
                    }
                });

                activity.findViewById(R.id.menu_hashtag_bind).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        if (hashBind.contains(hashTag.getHashTag().getHashTagValue())) {
                            hashBind.remove(hashTag.getHashTag().getHashTagValue());
                            //activity.findViewById(R.id.check_bind).setVisibility(View.GONE);
                        } else {
                            hashBind.add(hashTag.getHashTag().getHashTagValue());
                            //activity.findViewById(R.id.check_bind).setVisibility(View.VISIBLE);
                        }

                        SharedPreferences mPref = context.getSharedPreferences("HashtagModel", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = mPref.edit();
                        editor.putStringSet("Bind", hashBind);
                        editor.apply();

                        notifyDataSetChanged();


                        if(hashTagSearchAdapter !=  null) hashTagSearchAdapter.notifyDataSetChanged();

                    }
                });


               /* activity.findViewById(R.id.radio_Hash).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });*/

                activity.findViewById(R.id.menu_hashtag_pin).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (hashPin.contains(hashTag.getHashTag().getHashTagValue())) {
                            hashPin.remove(hashTag.getHashTag().getHashTagValue());

                            ((ImageView) activity.findViewById(R.id.pin_icon)).setColorFilter(ContextCompat.getColor(activity, R.color.primary), PorterDuff.Mode.SRC_IN);
                            ((TextView) activity.findViewById(R.id.pin_text)).setText("Pin to top");


                            //activity.findViewById(R.id.check_pin).setVisibility(View.GONE);
                        } else {
                            hashPin.add(hashTag.getHashTag().getHashTagValue());

                            ((ImageView) activity.findViewById(R.id.pin_icon)).setColorFilter(ContextCompat.getColor(activity, R.color.md_red_900), PorterDuff.Mode.SRC_IN);
                            ((TextView) activity.findViewById(R.id.pin_text)).setText("Unpin from top");
                            //activity.findViewById(R.id.check_pin).setVisibility(View.VISIBLE);
                        }

                        SharedPreferences mPref = context.getSharedPreferences("HashtagModel", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = mPref.edit();
                        editor.putStringSet("Pin", hashPin);
                        editor.apply();

                        sortHashtag();

                        if(hashTagSearchAdapter !=  null) hashTagSearchAdapter.sortHashtag();

                    }
                });


                return true;
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activity.findViewById(R.id.popup_menu_hashtag).getVisibility() == View.VISIBLE) {
                    activity.findViewById(R.id.popup_menu_hashtag).setVisibility(View.GONE);
                }
            }
        });

        holder.hyperHashTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (activity.findViewById(R.id.popup_menu_hashtag).getVisibility() == View.VISIBLE && !check_click[0]) {
                    activity.findViewById(R.id.popup_menu_hashtag).setVisibility(View.GONE);
                    return;
                }

                hyperHashtag = hashTag.getHashTag().getHashTagValue();


                contactsFragment.addHyperHashtag(hashTag.getHashTag().getHashTagValue());

                if ((contactsFragment.listForSelect != null && contactsFragment.listForSelect.isEmpty()) || contactsFragment.listForSelect == null) {
                    contactsFragment.listForSelect = new ArrayList<>();
                    contactsFragment.listForSelect.addAll(contactsFragment.contactAdapter.getListOfContacts());
                }

                ((TextView) contactsFragment.getActivity().findViewById(R.id.barHashtag_count)).setText("(1)");

                quantityCheck = 1;
                allHashtagsCheck = false;
                //sortByAsc();

                if (listOfHashtagsCheck != null) {
                    listOfHashtagsCheck.clear();
                    listOfHashtagsCheck.add(hashTag);
                } else {
                    listOfHashtagsCheck = new ArrayList<>();
                    listOfHashtagsCheck.add(hashTag);
                }

                if (listOfHashtagsCheck2 != null) {
                    listOfHashtagsCheck2.clear();
                    listOfHashtagsCheck2.add(hashTag.getHashTag().getHashTagValue());
                } else {
                    listOfHashtagsCheck2 = new ArrayList<>();
                    listOfHashtagsCheck2.add(hashTag.getHashTag().getHashTagValue());
                }


                notifyDataSetChanged();
            }
        });

    }

    public void shareHashtag(String hash) {
        String exportData = hash;


        exportData += "\n\n";
        exportData += "Data shared via http://Extime.pro\n";

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, exportData);
        activity.startActivity(Intent.createChooser(shareIntent, "Поделиться хештегом"));
    }

    public void shareContactToPackage(String appName, String hash) {
        String exportData = hash;

        exportData += "\n\n";
        exportData += "Data shared via http://Extime.pro\n";

        final boolean isAppInstalled = isAppAvailable(activity.getApplicationContext(), appName);

        if (isAppInstalled) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.setPackage(appName);
            shareIntent.putExtra(Intent.EXTRA_TEXT, exportData);
            activity.startActivity(Intent.createChooser(shareIntent, "Поделиться контактом"));
        } else {
            Toast.makeText(activity, "Application not installed", Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean isAppAvailable(Context context, String appName) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(appName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public void setMainHashTag(String hash) {

        HashTagQuantity hashTag = null;
        for (HashTagQuantity hs : listOfHashtags) {
            if (hs.getHashTag().getHashTagValue().equals(hash)) {
                hyperHashtag = hs.getHashTag().getHashTagValue();
                hashTag = hs;
                break;
            }
        }

        //hyperHashtag = hashTag.getHashTag().getHashTagValue();

        if ((contactsFragment.listForSelect != null && contactsFragment.listForSelect.isEmpty()) || contactsFragment.listForSelect == null) {
            contactsFragment.listForSelect = new ArrayList<>();
            contactsFragment.listForSelect.addAll(contactsFragment.contactAdapter.getListOfContacts());
        }

        //contactsFragment.addHyperHashtag(hashTag.getHashTag().getHashTagValue());

        ((TextView) contactsFragment.getActivity().findViewById(R.id.barHashtag_count)).setText("(1)");
        quantityCheck = 1;
        allHashtagsCheck = false;
        //sortByAsc();

        if (listOfHashtagsCheck != null) {
            listOfHashtagsCheck.clear();
            listOfHashtagsCheck.add(hashTag);
        } else {
            listOfHashtagsCheck = new ArrayList<>();
            listOfHashtagsCheck.add(hashTag);
        }

        if (listOfHashtagsCheck2 != null) {
            listOfHashtagsCheck2.clear();
            listOfHashtagsCheck2.add(hashTag.getHashTag().getHashTagValue());
        } else {
            listOfHashtagsCheck2 = new ArrayList<>();
            listOfHashtagsCheck2.add(hashTag.getHashTag().getHashTagValue());
        }


        notifyDataSetChanged();

    }

    @Override
    public int getItemCount() {
        return listOfHashtags.size();
    }

}
