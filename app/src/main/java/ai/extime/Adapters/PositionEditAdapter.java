package ai.extime.Adapters;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ai.extime.Interfaces.CompanySelectInterface;
import ai.extime.Services.ContactCacheService;

import com.extime.R;

public class PositionEditAdapter extends RecyclerView.Adapter<PositionEditAdapter.SelectPositionViewHolderEdit> {

    String search = "";
    private View mainView;
    private List <String> listOfPosition = new ArrayList<>();
    CompanySelectInterface companySelectInterface;
    boolean checkClick = true;
    Activity activity;

    public PositionEditAdapter(List<String> listOfPosition, CompanySelectInterface companySelectInterface, Activity activity){
        this.listOfPosition = listOfPosition;
        this.companySelectInterface = companySelectInterface;
        this.activity = activity;
    }

    public void updateList(ArrayList<String> list){
        this.listOfPosition = list;
    }

    public void sortPosByAsc(){
        try {
            Collections.sort(listOfPosition, (contactFirst, contactSecond) -> contactFirst.compareToIgnoreCase(contactSecond));
        } catch (Exception e) {
        }
        notifyDataSetChanged();
    }

    public void sortPosByDesc(){
        try {
            Collections.sort(listOfPosition, (contactFirst, contactSecond) -> contactSecond.compareToIgnoreCase(contactFirst));
        } catch (Exception e) {
        }
        notifyDataSetChanged();
    }

    public void sortByPopupAsc(){
        listOfPosition = ContactCacheService.getPopulPositionAsc(listOfPosition, new ArrayList<Integer>());
        notifyDataSetChanged();
    }

    public void sortByPopupDesc(){
        listOfPosition = ContactCacheService.getPopulPositionDesc(listOfPosition, new ArrayList<Integer>());
        notifyDataSetChanged();
    }


    public PositionEditAdapter(List<String> listOfPosition, CompanySelectInterface companySelectInterface, String s, Activity activity){
        this.listOfPosition = listOfPosition;
        this.companySelectInterface = companySelectInterface;
        this.search = s;
        this.activity = activity;
    }

    @Override
    public SelectPositionViewHolderEdit onCreateViewHolder(ViewGroup parent, int viewType) {
        mainView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_company_edit, parent, false);
        return new PositionEditAdapter.SelectPositionViewHolderEdit(mainView);

    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(SelectPositionViewHolderEdit holder, int position) {
        holder.f.setVisibility(View.GONE);
        if(search == "") {
            holder.hashTagValue.setText(listOfPosition.get(position).toString().trim());
            holder.countCompany.setVisibility(View.GONE);
        }else{
            int startI = listOfPosition.get(position).toString().toLowerCase().indexOf(search.toLowerCase());
            final SpannableStringBuilder text = new SpannableStringBuilder(listOfPosition.get(position).toString().trim());
            final ForegroundColorSpan style = new ForegroundColorSpan(Color.parseColor("#1976D2"));
            StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
            if(startI != -1) {
                text.setSpan(style, startI, startI + search.length(), Spannable.SPAN_COMPOSING);
                text.setSpan(bss, startI, startI + search.length(), Spannable.SPAN_COMPOSING);
            }
            holder.hashTagValue.setText(text);
            holder.countCompany.setVisibility(View.GONE);
        }

        holder.hashTagValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                companySelectInterface.addSelectPosition(listOfPosition.get(position).toString().trim());
            }
        });

        holder.hashTagValue.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {


                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        OnTouchMethod(holder.hashTagValue);
                        break;
                    }
                    case MotionEvent.ACTION_UP:{
                        OnUpTouchMethod(holder.hashTagValue);
                        break;
                    }
                    case MotionEvent.ACTION_CANCEL:{
                        OnCalcelTouchMethod(holder.hashTagValue);
                        break;
                    }
                    case MotionEvent.ACTION_MOVE:{
                        OnMoveTouchMethod(holder.hashTagValue, motionEvent);
                        break;
                    }
                }
                return false;
            }
        });
    }

    public void OnTouchMethod(TextView textview){

        checkClick = false;

        int colorFrom;
        String s = activity.getResources().getResourceEntryName(textview.getId());




        colorFrom = activity.getResources().getColor(R.color.gray);



        int colorTo = activity.getResources().getColor(R.color.md_deep_orange_300);
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(50); // milliseconds
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                textview.setTextColor((int) animator.getAnimatedValue());
                //  imageview.setColorFilter((int) animator.getAnimatedValue());
                // textview.setTypeface(null, Typeface.BOLD);
            }
        });
        colorAnimation.start();
    }


    public void OnCalcelTouchMethod(TextView textView){
        int colorFrom = activity.getResources().getColor(R.color.md_deep_orange_300);
        String s = activity.getResources().getResourceEntryName(textView.getId());
        int colorTo;



        colorTo = activity.getResources().getColor(R.color.gray);




        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(300); // milliseconds
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                textView.setTextColor((int) animator.getAnimatedValue());
                //      imageView.setColorFilter((int) animator.getAnimatedValue());
                textView.setTypeface(null, Typeface.NORMAL);
            }

        });
        colorAnimation.start();
    }


    public void OnUpTouchMethod(TextView textview){
        if(!checkClick) {
            int colorFrom = activity.getResources().getColor(R.color.md_deep_orange_300);
            //   int colorTo = getResources().getColor(R.color.colorPrimaryDark);
            String s = activity.getResources().getResourceEntryName(textview.getId());
            int colorTo;



            colorTo = activity.getResources().getColor(R.color.gray);




            ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
            colorAnimation.setDuration(50); // milliseconds
            colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    textview.setTextColor((int) animator.getAnimatedValue());
                    //  imageview.setColorFilter((int) animator.getAnimatedValue());
                    //textview.setTypeface(null, Typeface.NORMAL);
                }

            });
            colorAnimation.start();
        }
    }



    public void OnMoveTouchMethod(TextView textView, MotionEvent motionEvent){
        int[] location = new int[2];
        textView.getLocationInWindow(location);
        int leftX = 0;
        int rightX = leftX + textView.getWidth();
        int topY = 0;
        int bottomY = topY + textView.getHeight();
        float xCurrent = motionEvent.getX();
        float yCurrent = motionEvent.getY();

        if (xCurrent > rightX || xCurrent < leftX || yCurrent > bottomY || yCurrent < topY) {
            if(!checkClick) {
                checkClick = true;


                String s = activity.getResources().getResourceEntryName(textView.getId());
                int colorTo2;



                colorTo2 = activity.getResources().getColor(R.color.gray);




                int colorFrom = activity.getResources().getColor(R.color.md_deep_orange_300);
                // int colorTo = textView.getTextColors().getDefaultColor();
                //colorTo2 = textView.getTextColors().getDefaultColor();
                //  ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
                //  colorAnimation.setDuration(1000); // milliseconds

                ValueAnimator colorAnimation2 = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo2);
                colorAnimation2.setDuration(50); // milliseconds

             /*   colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animator) {
                      //  textView.setTextColor((int) animator.getAnimatedValue());
                       // imageView.setColorFilter((int) animator.getAnimatedValue());
                     //   textView.setTypeface(null, Typeface.NORMAL);
                    }

                });
                colorAnimation.start();*/


                colorAnimation2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animator) {
                        textView.setTextColor((int) animator.getAnimatedValue());
                        //  imageView.setColorFilter((int) animator.getAnimatedValue());
                        //    textView.setTypeface(null, Typeface.NORMAL);
                    }

                });
                colorAnimation2.start();



            }

        }
    }

    @Override
    public int getItemCount() {
        return listOfPosition.size();
    }

    static class SelectPositionViewHolderEdit extends RecyclerView.ViewHolder {

        TextView hashTagValue;
        TextView countCompany;
        View card;
        LinearLayout linearLayout;
        FrameLayout f;

        SelectPositionViewHolderEdit (View itemView) {
            super(itemView);
            card = itemView;
            hashTagValue = (TextView) itemView.findViewById(R.id.companyValueEdit);
            countCompany = (TextView) itemView.findViewById(R.id.countCompanyContacts);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.companySelectEdit);
            f = itemView.findViewById(R.id.frameInageIconClipboard);
        }
    }
}
