package ai.extime.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.extime.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import ai.extime.Fragments.ContactsFragment;
import ai.extime.Models.Clipboard;
import ai.extime.Models.HashSearchModel;
import ai.extime.Models.HashTag;
import ai.extime.Models.HashTagQuantity;

public class HashTagSearchAdapter extends RecyclerView.Adapter<HashTagSearchAdapter.HashTagsViewHolder> {

    private Context context;

    private Activity activity;

    private SharedPreferences mPref;

    private View mainView;

    private boolean sortAsc = false;

    private Set <String> hashBind;
    private Set <String> hashPin;

    private boolean sortAscByPopul = false;

    private boolean sortAscByTime = true;

    public ArrayList<HashTagQuantity> listOfHashtags;

    public ArrayList<HashTagQuantity> listOfHashtagsView;



    private String search;

    private HashTagsAdapter HASHTAG_ADAPTER;

    private ContactsFragment contactsFragment;

    public HashTagSearchAdapter(Context context, ArrayList<HashTagQuantity> listOfHashTags, Activity activity, HashTagsAdapter HASHTAG_ADAPTER, ContactsFragment contactsFragment, Set <String> hashBind, Set <String> hashPin){
        this.context = context;
        this.listOfHashtags = listOfHashTags;

        this.listOfHashtagsView = new ArrayList<>();
        this.listOfHashtagsView.addAll(listOfHashTags);

        this.contactsFragment = contactsFragment;

        this.activity = activity;
        mPref = context.getSharedPreferences("Sort", Context.MODE_PRIVATE);

        this.HASHTAG_ADAPTER = HASHTAG_ADAPTER;

        ((TextView)activity.findViewById(R.id.text_search_count)).setText("previous searches");

        if (hashBind == null) hashBind = new HashSet<String>();
        if (hashPin == null) hashPin = new HashSet<String>();

        this.hashBind = hashBind;
        this.hashPin = hashPin;

        //sortHashtag();

        //listOfHashtagsCheck = new ArrayList<>();
        //listOfHashtagsCheck2 = new ArrayList<>();
    }

    public void updateList(ArrayList<HashTagQuantity> listOfHashTags){
        this.listOfHashtags = listOfHashTags;
    }

    public void updateListNotify(ArrayList<HashTagQuantity> listOfHashTags){
        this.listOfHashtags = listOfHashTags;
        this.listOfHashtagsView = new ArrayList<>();
        this.listOfHashtagsView.addAll(listOfHashTags);
        this.search = "";

        ((TextView)activity.findViewById(R.id.text_search_count)).setText("previous searches");

        //sortHashtag();

        notifyDataSetChanged();
    }

    public void updateList(String str){
        this.search = str;
        listOfHashtagsView = new ArrayList<>();
        for(HashTagQuantity hashTag : listOfHashtags){
            if(hashTag.getHashTag().getHashTagValue().contains(search)){
                listOfHashtagsView.add(hashTag);
            }
        }

        ((TextView)activity.findViewById(R.id.text_search_count)).setText(listOfHashtagsView.size() + " matched entries");



        sortHashtag();

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HashTagsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mainView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_hashtag, viewGroup, false);
        mPref = context.getSharedPreferences("Sort", Context.MODE_PRIVATE);
        return new HashTagSearchAdapter.HashTagsViewHolder(mainView);
    }

    @Override
    public void onBindViewHolder(@NonNull HashTagsViewHolder holder, int position) {
        HashTagQuantity hashTag = listOfHashtagsView.get(position);


        holder.hashTagQuantity.setText("(" + hashTag.getQuantity() + ")");
        holder.hashTagValue.requestLayout();

        holder.itemView.findViewById(R.id.hash_pin).setVisibility(View.GONE);
        holder.itemView.findViewById(R.id.hash_bind).setVisibility(View.GONE);

        if(hashBind.contains(hashTag.getHashTag().getHashTagValue())){
            holder.itemView.findViewById(R.id.hash_bind).setVisibility(View.VISIBLE);
        }

        if(hashPin.contains(hashTag.getHashTag().getHashTagValue())){
            holder.itemView.findViewById(R.id.hash_pin).setVisibility(View.VISIBLE);
        }

        //holder.hashTagValue.setText(hashTag.getHashTag().getHashTagValue());

        if(search != ""){
            try {
                int startI = hashTag.getHashTag().getHashTagValue().toLowerCase().indexOf(search.toLowerCase());
                final SpannableStringBuilder text = new SpannableStringBuilder(hashTag.getHashTag().getHashTagValue());
                final ForegroundColorSpan style = new ForegroundColorSpan(Color.parseColor("#1976D2"));
                StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
                text.setSpan(style, startI, startI + search.length(), Spannable.SPAN_COMPOSING);
                text.setSpan(bss, startI, startI + search.length(), Spannable.SPAN_COMPOSING);
                holder.hashTagValue.setText(text);
            }catch (Exception e){
                //e.printStackTrace();
                holder.hashTagValue.setText(hashTag.getHashTag().getHashTagValue());
            }
        }else
            holder.hashTagValue.setText(hashTag.getHashTag().getHashTagValue());


        if (HASHTAG_ADAPTER.allHashtagsCheck) {
            holder.hashTagSelect.setChecked(true);
        } else {
            holder.hashTagSelect.setChecked(false);
        }

        if (HASHTAG_ADAPTER.listOfHashtagsCheck2.contains(hashTag.getHashTag().getHashTagValue())) {
            holder.hashTagSelect.setChecked(true);
        }


        holder.hashTagSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isChecked = holder.hashTagSelect.isChecked();

                if (isChecked) {

                    HASHTAG_ADAPTER.quantityCheck++;
                    if (HASHTAG_ADAPTER.quantityCheck == listOfHashtags.size()) {

                    }
                    //   if(position+1==listOfHashtags.size()) ((TextView)contactsFragment.getActivity().findViewById(R.id.barHashtag_count)).setText(" "+quantityCheck+"");
                    //else
                    if (HASHTAG_ADAPTER.listOfHashtagsCheck2 != null && !HASHTAG_ADAPTER.listOfHashtagsCheck2.contains(hashTag.getHashTag().getHashTagValue())) {
                        HASHTAG_ADAPTER.listOfHashtagsCheck.add(hashTag);
                        HASHTAG_ADAPTER.listOfHashtagsCheck2.add(hashTag.getHashTag().getHashTagValue());
                    }
                    if (HASHTAG_ADAPTER.quantityCheck < HASHTAG_ADAPTER.countAll)
                        ((TextView) contactsFragment.getActivity().findViewById(R.id.barHashtag_count)).setText("(" + HASHTAG_ADAPTER.quantityCheck + ")");
                    else
                        ((TextView) contactsFragment.getActivity().findViewById(R.id.barHashtag_count)).setText(" " + HASHTAG_ADAPTER.quantityCheck + "");
                    //contactsFragment.addToListByHashTag(hashTag.getHashTag().getHashTagValue(),quantityCheck == listOfHashtags.size());

                    if (((RadioButton) contactsFragment.getActivity().findViewById(R.id.radioOR)).isChecked())
                        contactsFragment.addToListByHashTagNEW(HASHTAG_ADAPTER.listOfHashtagsCheck, hashTag.getHashTag().getHashTagValue());
                    else
                        contactsFragment.addToListByHashTagNEWAND(HASHTAG_ADAPTER.listOfHashtagsCheck, HASHTAG_ADAPTER.listOfHashtagsCheck2);


                } else {

                    if (HASHTAG_ADAPTER.listOfHashtagsCheck2 != null && HASHTAG_ADAPTER.listOfHashtagsCheck2.contains(hashTag.getHashTag().getHashTagValue())) {
                        HASHTAG_ADAPTER.listOfHashtagsCheck.remove(hashTag);
                        HASHTAG_ADAPTER.listOfHashtagsCheck2.remove(hashTag.getHashTag().getHashTagValue());
                    }


                    HASHTAG_ADAPTER.quantityCheck--;

                    if (HASHTAG_ADAPTER.quantityCheck < HASHTAG_ADAPTER.countAll)
                        ((TextView) contactsFragment.getActivity().findViewById(R.id.barHashtag_count)).setText("(" + HASHTAG_ADAPTER.quantityCheck + ")");
                    else if (HASHTAG_ADAPTER.quantityCheck == HASHTAG_ADAPTER.countAll)
                        ((TextView) contactsFragment.getActivity().findViewById(R.id.barHashtag_count)).setText(" " + HASHTAG_ADAPTER.quantityCheck + "");


                    //contactsFragment.removeFromListByHashTag(hashTag.getHashTag().getHashTagValue(), true);
                    if (((RadioButton) contactsFragment.getActivity().findViewById(R.id.radioOR)).isChecked())
                        contactsFragment.removeFromListByHashTagNEW(hashTag.getHashTag().getHashTagValue(), HASHTAG_ADAPTER.listOfHashtagsCheck, true);
                    else
                        contactsFragment.addToListByHashTagNEWAND(HASHTAG_ADAPTER.listOfHashtagsCheck, HASHTAG_ADAPTER.listOfHashtagsCheck2);
                    //     ((CheckBox) MainActivity.getactivity().findViewById(R.id.NoTagsCheck)).setChecked(false);


                }
                if (HASHTAG_ADAPTER.quantityCheck == listOfHashtags.size() && !((RadioButton) contactsFragment.getActivity().findViewById(R.id.radioAND)).isChecked() ) {
                    contactsFragment.checkAllHashtags();
                }
                // contactsFragment.getActivity().findViewById(R.id.frame_select_bar).setVisibility(View.GONE);

                HASHTAG_ADAPTER.notifyDataSetChanged();

            }
        });



        holder.hyperHashTag.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                activity.findViewById(R.id.popup_menu_hashtag).setVisibility(View.VISIBLE);
                ((TextView)activity.findViewById(R.id.menu_hashtag_text)).setText(hashTag.getHashTag().getHashTagValue());
                ((TextView)activity.findViewById(R.id.menu_hashtag_text)).requestLayout();

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

                activity.findViewById(R.id.menu_hashtag_bind).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {



                        if(hashBind.contains(hashTag.getHashTag().getHashTagValue())){
                            hashBind.remove(hashTag.getHashTag().getHashTagValue());
                            //activity.findViewById(R.id.check_bind).setVisibility(View.GONE);
                        }else{
                            hashBind.add(hashTag.getHashTag().getHashTagValue());
                            //activity.findViewById(R.id.check_bind).setVisibility(View.VISIBLE);
                        }

                        SharedPreferences mPref = context.getSharedPreferences("HashtagModel", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = mPref.edit();
                        editor.putStringSet("Bind", hashBind);
                        editor.apply();

                        notifyDataSetChanged();

                        HASHTAG_ADAPTER.notifyDataSetChanged();

                    }
                });

                /*activity.findViewById(R.id.radio_Hash).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });*/

                activity.findViewById(R.id.menu_hashtag_pin).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(hashPin.contains(hashTag.getHashTag().getHashTagValue())){
                            hashPin.remove(hashTag.getHashTag().getHashTagValue());
                            //activity.findViewById(R.id.check_pin).setVisibility(View.GONE);

                                ((ImageView) activity.findViewById(R.id.pin_icon)).setColorFilter(ContextCompat.getColor(activity, R.color.primary), PorterDuff.Mode.SRC_IN);
                                ((TextView) activity.findViewById(R.id.pin_text)).setText("Pin to top");


                        }else{
                            hashPin.add(hashTag.getHashTag().getHashTagValue());
                            //activity.findViewById(R.id.check_pin).setVisibility(View.VISIBLE);

                            ((ImageView) activity.findViewById(R.id.pin_icon)).setColorFilter(ContextCompat.getColor(activity, R.color.md_red_900), PorterDuff.Mode.SRC_IN);
                            ((TextView) activity.findViewById(R.id.pin_text)).setText("Unpin from top");
                        }

                        SharedPreferences mPref = context.getSharedPreferences("HashtagModel", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = mPref.edit();
                        editor.putStringSet("Pin", hashPin);
                        editor.apply();

                        sortHashtag();

                        HASHTAG_ADAPTER.sortHashtag();

                    }
                });

                return true;
            }
        });




        holder.hyperHashTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                HASHTAG_ADAPTER.hyperHashtag = hashTag.getHashTag().getHashTagValue();


                SharedPreferences mPref = context.getSharedPreferences("HashtagSearch", Context.MODE_PRIVATE);

                String str = mPref.getString("HashtagS", "");

                Gson gson = new Gson();

                ArrayList<HashSearchModel> listHash = new ArrayList<>();
                if(!str.isEmpty()) {
                    listHash.addAll(gson.fromJson(str, new TypeToken<ArrayList<HashSearchModel>>() {
                    }.getType()));
                }

                if(listHash.isEmpty()){
                    listHash.add(new HashSearchModel(hashTag.getHashTag().getHashTagValue()));
                }else{
                    for(HashSearchModel st : listHash){
                        if(st.getHash().equalsIgnoreCase(hashTag.getHashTag().getHashTagValue())) {
                            listHash.remove(st);
                            break;
                        }
                    }
                    listHash.add(0,new HashSearchModel(hashTag.getHashTag().getHashTagValue()));
                }



                SharedPreferences.Editor editor = mPref.edit();
                editor.putString("HashtagS", gson.toJson(listHash));
                editor.commit();



                contactsFragment.addHyperHashtag(hashTag.getHashTag().getHashTagValue());

                if ((contactsFragment.listForSelect != null && contactsFragment.listForSelect.isEmpty()) || contactsFragment.listForSelect == null) {
                    contactsFragment.listForSelect = new ArrayList<>();
                    contactsFragment.listForSelect.addAll(contactsFragment.contactAdapter.getListOfContacts());
                }

                ((TextView) contactsFragment.getActivity().findViewById(R.id.barHashtag_count)).setText("(1)");

                HASHTAG_ADAPTER.quantityCheck = 1;
                HASHTAG_ADAPTER.allHashtagsCheck = false;
                //sortByAsc();

                if (HASHTAG_ADAPTER.listOfHashtagsCheck != null) {
                    HASHTAG_ADAPTER.listOfHashtagsCheck.clear();
                    HASHTAG_ADAPTER.listOfHashtagsCheck.add(hashTag);
                } else {
                    HASHTAG_ADAPTER.listOfHashtagsCheck = new ArrayList<>();
                    HASHTAG_ADAPTER.listOfHashtagsCheck.add(hashTag);
                }

                if (HASHTAG_ADAPTER.listOfHashtagsCheck2 != null) {
                    HASHTAG_ADAPTER.listOfHashtagsCheck2.clear();
                    HASHTAG_ADAPTER.listOfHashtagsCheck2.add(hashTag.getHashTag().getHashTagValue());
                } else {
                    HASHTAG_ADAPTER.listOfHashtagsCheck2 = new ArrayList<>();
                    HASHTAG_ADAPTER.listOfHashtagsCheck2.add(hashTag.getHashTag().getHashTagValue());
                }


                notifyDataSetChanged();
                HASHTAG_ADAPTER.notifyDataSetChanged();
            }
        });


    }

    @Override
    public int getItemCount() {
        return listOfHashtagsView.size();
    }

    public void sortHashtag() {
        SharedPreferences mPref = context.getSharedPreferences("Sort", Context.MODE_PRIVATE);

        String sort = mPref.getString("typeSortHashtag", "sortByDescPopul");
        //sort = "sortByDescPopul";

        if (sort.equals("sortByAsc")) {
            sortByAsc();
            ((ImageView) activity.findViewById(R.id.timeSortH)).setColorFilter(ContextCompat.getColor(context, R.color.gray));
            ((ImageView) activity.findViewById(R.id.populTagH)).setColorFilter(ContextCompat.getColor(context, R.color.gray));
            ((TextView) activity.findViewById(R.id.sortTextH)).setTextColor(context.getResources().getColor(R.color.primary));
            ((TextView) activity.findViewById(R.id.sortTextH)).setText("A-Z");
            //((TextView) getActivity().findViewById(R.id.sortText)).setTextColor(getResources().getColor(R.color.primary));
            //((ImageView) getActivity().findViewById(R.id.timeSort)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
        } else if (sort.equals("sortByDesc")) {
            sortByDesc();
            ((ImageView) activity.findViewById(R.id.timeSortH)).setColorFilter(ContextCompat.getColor(context, R.color.gray));
            ((ImageView) activity.findViewById(R.id.populTagH)).setColorFilter(ContextCompat.getColor(context, R.color.gray));
            ((TextView) activity.findViewById(R.id.sortTextH)).setTextColor(context.getResources().getColor(R.color.primary));
            ((TextView) activity.findViewById(R.id.sortTextH)).setText("Z-A");
            //((TextView) getActivity().findViewById(R.id.sortText)).setTextColor(getResources().getColor(R.color.primary));
            //((ImageView) getActivity().findViewById(R.id.timeSort)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
        } else if (sort.equals("sortByTimeAsc")) {
            sortByAscTime();
            ((ImageView) activity.findViewById(R.id.timeSortH)).setColorFilter(ContextCompat.getColor(context, R.color.primary));
            ((ImageView) activity.findViewById(R.id.populTagH)).setColorFilter(ContextCompat.getColor(context, R.color.gray));
            ((TextView) activity.findViewById(R.id.sortTextH)).setTextColor(context.getResources().getColor(R.color.gray));
            //((TextView) getActivity().findViewById(R.id.sortText)).setTextColor(getResources().getColor(R.color.gray));
            //((ImageView) getActivity().findViewById(R.id.timeSort)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));
        } else if (sort.equals("sortByTimeDesc")) {
            sortByDescTime();
            ((ImageView) activity.findViewById(R.id.timeSortH)).setColorFilter(ContextCompat.getColor(context, R.color.primary));
            ((ImageView) activity.findViewById(R.id.populTagH)).setColorFilter(ContextCompat.getColor(context, R.color.gray));
            ((TextView) activity.findViewById(R.id.sortTextH)).setTextColor(context.getResources().getColor(R.color.gray));
            //((TextView) getActivity().findViewById(R.id.sortText)).setTextColor(getResources().getColor(R.color.gray));
            //((ImageView) getActivity().findViewById(R.id.timeSort)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));
        } else if (sort.equals("sortByAscPopul")) {
            sortByAscPopul();
            ((ImageView) activity.findViewById(R.id.timeSortH)).setColorFilter(ContextCompat.getColor(context, R.color.gray));
            ((ImageView) activity.findViewById(R.id.populTagH)).setColorFilter(ContextCompat.getColor(context, R.color.primary));
            ((TextView) activity.findViewById(R.id.sortTextH)).setTextColor(context.getResources().getColor(R.color.gray));
        } else if (sort.equals("sortByDescPopul")) {
            sortByDescPopul();
            ((ImageView) activity.findViewById(R.id.timeSortH)).setColorFilter(ContextCompat.getColor(context, R.color.gray));
            ((ImageView) activity.findViewById(R.id.populTagH)).setColorFilter(ContextCompat.getColor(context, R.color.primary));
            ((TextView) activity.findViewById(R.id.sortTextH)).setTextColor(context.getResources().getColor(R.color.gray));
        }

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

        Collections.sort(listOfHashtagsView, (hashTagFirst, hashTagSecond) -> hashTagSecond.getHashTag().getDate().compareTo(hashTagFirst.getHashTag().getDate()));
        sortAscByTime = false;

        if(hashPin == null) hashPin = new HashSet<>();

        for(int i = 0; i < listOfHashtagsView.size();i++){
            if(hashPin.contains(listOfHashtagsView.get(i).getHashTag().getHashTagValue())){
                HashTagQuantity h = listOfHashtagsView.get(i);
                listOfHashtagsView.remove(i);
                listOfHashtagsView.add(0, h);

            }
        }

        notifyDataSetChanged();
    }

    private void sortByAscTime() {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString("typeSortHashtag", "sortByTimeAsc");
        editor.apply();
        Collections.sort(listOfHashtagsView, (hashTagFirst, hashTagSecond) -> hashTagFirst.getHashTag().getDate().compareTo(hashTagSecond.getHashTag().getDate()));
        sortAscByTime = true;

        if(hashPin == null) hashPin = new HashSet<>();

        for(int i = 0; i < listOfHashtagsView.size();i++){
            if(hashPin.contains(listOfHashtagsView.get(i).getHashTag().getHashTagValue())){
                HashTagQuantity h = listOfHashtagsView.get(i);
                listOfHashtagsView.remove(i);
                listOfHashtagsView.add(0, h);

            }
        }

        notifyDataSetChanged();
    }

    private void sortByDescPopul() {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString("typeSortHashtag", "sortByDescPopul");
        editor.apply();
        Collections.sort(listOfHashtagsView, new Comparator<HashTagQuantity>() {
            @Override
            public int compare(HashTagQuantity first, HashTagQuantity second) {
                return second.getQuantity() - first.getQuantity();
            }
        });
        sortAscByPopul = false;

        if(hashPin == null) hashPin = new HashSet<>();

        for(int i = 0; i < listOfHashtagsView.size();i++){
            if(hashPin.contains(listOfHashtagsView.get(i).getHashTag().getHashTagValue())){
                HashTagQuantity h = listOfHashtagsView.get(i);
                listOfHashtagsView.remove(i);
                listOfHashtagsView.add(0, h);

            }
        }

        notifyDataSetChanged();
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

    private void sortByAscPopul() {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString("typeSortHashtag", "sortByAscPopul");
        editor.apply();
        Collections.sort(listOfHashtagsView, new Comparator<HashTagQuantity>() {
            @Override
            public int compare(HashTagQuantity first, HashTagQuantity second) {
                return first.getQuantity() - second.getQuantity();
            }
        });
        sortAscByPopul = true;

        if(hashPin == null) hashPin = new HashSet<>();

        for(int i = 0; i < listOfHashtagsView.size();i++){
            if(hashPin.contains(listOfHashtagsView.get(i).getHashTag().getHashTagValue())){
                HashTagQuantity h = listOfHashtagsView.get(i);
                listOfHashtagsView.remove(i);
                listOfHashtagsView.add(0, h);

            }
        }

        notifyDataSetChanged();
    }

    private void sortByDesc() {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString("typeSortHashtag", "sortByDesc");
        editor.apply();
        Collections.sort(listOfHashtagsView, new Comparator<HashTagQuantity>() {
            @Override
            public int compare(HashTagQuantity o1, HashTagQuantity o2) {
                return o2.getHashTag().getHashTagValue().compareToIgnoreCase(o1.getHashTag().getHashTagValue());
            }
        });
        sortAsc = false;
        notifyDataSetChanged();
    }

    private void sortByAsc() {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString("typeSortHashtag", "sortByAsc");
        editor.apply();
        Collections.sort(listOfHashtagsView, new Comparator<HashTagQuantity>() {
            @Override
            public int compare(HashTagQuantity o1, HashTagQuantity o2) {
                return o1.getHashTag().getHashTagValue().compareToIgnoreCase(o2.getHashTag().getHashTagValue());
            }
        });
        sortAsc = true;
        notifyDataSetChanged();
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
}
