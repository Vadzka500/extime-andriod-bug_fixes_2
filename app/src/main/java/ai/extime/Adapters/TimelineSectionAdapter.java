package ai.extime.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.extime.R;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.util.ArrayList;
import java.util.List;

import ai.extime.Enums.TypeCardTimeline;
import ai.extime.Fragments.TimeLineFragment;

public class TimelineSectionAdapter extends FragmentPagerAdapter implements SmartTabLayout.TabProvider {

    private final List<Fragment> listOfFragment = new ArrayList<>();

    private final List<String> listOfTitleFragment = new ArrayList<>();

    private final List<Long> listOfTitleCount = new ArrayList<>();

    private Context context;

    private TypeCardTimeline type;

    public void addFragment(Fragment fragment, String title, long count){
        listOfFragment.add(fragment);
        listOfTitleFragment.add(title);
        listOfTitleCount.add(count);
    }

    public TimelineSectionAdapter(FragmentManager fm, Context context, TypeCardTimeline type) {
        super(fm);
        this.context = context;
        this.type = type;

    }



    @Override
    public CharSequence getPageTitle(int position){
        return listOfTitleFragment.get(position);
    }

    @Override
    public Fragment getItem(int position) {
        return listOfFragment.get(position);
    }

    @Override
    public int getCount() {
        return listOfFragment.size();
    }

    @Override
    public View createTabView(ViewGroup container, int position, PagerAdapter adapter) {

        View v;

        LinearLayout.LayoutParams lp;
        int px_start = (int) (13 * context.getResources().getDisplayMetrics().density);
        int px_start_top = (int) (40 * context.getResources().getDisplayMetrics().density);
        int px_start_n = (int) (16 * context.getResources().getDisplayMetrics().density);
        if(type.equals(TypeCardTimeline.EXTRA)) {
            v = LayoutInflater.from(this.context).inflate(R.layout.kanban_row_head_timeline, null);


            lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, px_start_n);
            if(listOfTitleFragment.get(position).equals("Inbox")){
                lp.setMargins(px_start,0,30,0);
            }else{
                lp.setMargins(30,0,30,0);
            }

        }else {
            v = LayoutInflater.from(this.context).inflate(R.layout.kanban_mode_top, null);

            lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, px_start_top);
            if(listOfTitleFragment.get(position).equals("Inbox")){
                lp.setMargins(0,0,8,0);
            }else{
                lp.setMargins(0,0,8,0);
            }
        }





        if(listOfTitleFragment.get(position).equals("Inbox")){
            ((ImageView) v.findViewById(R.id.imageTab)).setImageDrawable(context.getResources().getDrawable(R.drawable.inbox_timeline_2));
            ((TextView) v.findViewById(R.id.text_title_tab)).setText("Inbox");
        }else if(listOfTitleFragment.get(position).equals("Unread")){

            ((ImageView) v.findViewById(R.id.imageTab)).setImageDrawable(context.getResources().getDrawable(R.drawable.icn_remind_message));
            ((TextView) v.findViewById(R.id.text_title_tab)).setText("Unread");
            ((TextView) v.findViewById(R.id.text_title_tab)).setTypeface(((TextView) v.findViewById(R.id.text_title_tab)).getTypeface(), Typeface.BOLD);
        }else if(listOfTitleFragment.get(position).equals("Favorites")){
            ((ImageView) v.findViewById(R.id.imageTab)).setImageDrawable(context.getResources().getDrawable(R.drawable.star));
            ((TextView) v.findViewById(R.id.text_title_tab)).setText("Starred");
        }else if(listOfTitleFragment.get(position).equals("Sent")){
            ((ImageView) v.findViewById(R.id.imageTab)).setImageDrawable(context.getResources().getDrawable(R.drawable.icn_ms_plane));
            ((TextView) v.findViewById(R.id.text_title_tab)).setText("Sent");
        }
        else if(listOfTitleFragment.get(position).equals("Important")){
            ((ImageView) v.findViewById(R.id.imageTab)).setImageDrawable(context.getResources().getDrawable(R.drawable.ic_important_mail));

            ((TextView) v.findViewById(R.id.text_title_tab)).setText("Important");
        }
        else if(listOfTitleFragment.get(position).equals("Drafts")){
            ((ImageView) v.findViewById(R.id.imageTab)).setImageDrawable(context.getResources().getDrawable(R.drawable.draft));
            ((ImageView) v.findViewById(R.id.imageTab)).setColorFilter(ContextCompat.getColor(context, R.color.primary));
            ((TextView) v.findViewById(R.id.text_title_tab)).setText("Drafts");
        }
        else if(listOfTitleFragment.get(position).equals("Spam")){
            ((ImageView) v.findViewById(R.id.imageTab)).setImageDrawable(context.getResources().getDrawable(R.drawable.spam));
            ((ImageView) v.findViewById(R.id.imageTab)).setColorFilter(ContextCompat.getColor(context, R.color.primary));
            ((TextView) v.findViewById(R.id.text_title_tab)).setText("Spam");
        }
        else if(listOfTitleFragment.get(position).equals("Trash")){
            ((ImageView) v.findViewById(R.id.imageTab)).setImageDrawable(context.getResources().getDrawable(R.drawable.icn_profile_edit_trash));
            ((ImageView) v.findViewById(R.id.imageTab)).setColorFilter(ContextCompat.getColor(context, R.color.primary));
            ((TextView) v.findViewById(R.id.text_title_tab)).setText("Trash");
        }

        ((TextView) v.findViewById(R.id.text_count_tab)).setText(String.valueOf(listOfTitleCount.get(position)));

        v.setLayoutParams(lp);

        v.requestLayout();

        return v;
    }
}
